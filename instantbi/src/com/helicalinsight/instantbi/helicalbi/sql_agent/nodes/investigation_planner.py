"""LLM + fallback builders for strategy-aware InvestigationPlan charts.

Used by the plan-context graph's draft node. Prompt framing (think flat-SQL
rules, persona/strategy blocks) is assembled here; strategy selection itself
lives in ``helicalbi.sql_agent.strategy``.
"""
from __future__ import annotations

import logging
import time
from typing import Any, Dict, List, Mapping, Optional

from langchain_core.messages import AIMessage, HumanMessage
from langchain_core.prompts import PromptTemplate

from helicalbi.sql_agent.config import (
    CONTEXT_PLAN_PROMPT,
    SQL_SHAPE_DERIVED_TABLES,
    THINK_FLAT_SQL_PLAN_RULES,
)
from helicalbi.sql_agent.constants import DEFAULT_OVERVIEW_CHARS
from helicalbi.sql_agent.prompt import _THINK_QUESTION_SYSTEM
from helicalbi.sql_agent.llm import invoke_agent_model
from helicalbi.sql_agent.models import DashboardChartSpec, InvestigationPlan
from helicalbi.sql_agent.modes import truncate_text
from helicalbi.sql_agent.personas import DEFAULT_PERSONA, PERSONA_TIERS, persona_prompt_block
from helicalbi.sql_agent.strategy_tree import (
    analytical_operations_prompt_block,
    attach_strategy,
    charts_from_strategy,
    charts_within_diagnostic_scope,
    diagnostic_activity_reason,
    diagnostic_capabilities_prompt_block,
    diagnostic_field_scope,
    diagnostic_scope_lock,
    get_strategy,
    list_strategies,
    load_analytical_operations,
    select_analytical_operations,
    select_strategy,
    strategy_catalog_prompt_block,
    strategy_prompt_block,
)

logger = logging.getLogger(__name__)


def _resolve_strategy(
    question: str,
    persona: Mapping[str, Any],
    strategy: Optional[Mapping[str, Any]] = None,
) -> dict[str, Any]:
    if isinstance(strategy, Mapping) and strategy.get("id"):
        return dict(strategy)
    attached = persona.get("strategy") if isinstance(persona.get("strategy"), Mapping) else None
    if attached and attached.get("id"):
        return dict(attached)
    return select_strategy(question, persona=persona)


def _applied_strategy(
    parsed: InvestigationPlan,
    suggested: Mapping[str, Any],
) -> dict[str, Any]:
    """Honor an LLM substitution when it is a real catalog strategy; keep hints."""
    catalog = list_strategies()
    forced = str((suggested.get("selection") or {}).get("source") or "") == "hint"
    if forced:
        return dict(suggested)
    candidate = str(parsed.strategy_id or "").strip()
    if not candidate and parsed.strategies:
        candidate = str(parsed.strategies[0] or "").strip()
    suggested_id = str(suggested.get("id") or "")
    if not candidate or candidate not in catalog:
        return dict(suggested)
    applied = get_strategy(candidate)
    selection = dict(suggested.get("selection") or {})
    if candidate != suggested_id:
        selection["source"] = "llm_adapt"
        selection["suggested_strategy_id"] = suggested_id
    selection["strategy_id"] = candidate
    applied["selection"] = selection
    return applied


def fallback_investigation_plan(
    question: str,
    *,
    persona: Mapping[str, Any],
    max_charts: int = 5,
    strategy: Optional[Mapping[str, Any]] = None,
    selected_domains: Optional[List[str]] = None,
    selected_topics: Optional[List[str]] = None,
) -> InvestigationPlan:
    """Deterministic plan from the selected strategy's JSON chart skeleton."""
    chosen = _resolve_strategy(question, persona, strategy)
    if not chosen.get("template_id") and chosen.get("id"):
        chosen = {**get_strategy(str(chosen.get("id"))), **chosen}
    name = str(persona.get("name") or DEFAULT_PERSONA)
    strategy_id = str(chosen.get("id") or "")
    charts = [
        DashboardChartSpec.model_validate(item)
        for item in charts_from_strategy(chosen, question, max_charts=max_charts)
    ]
    domains = [str(d).strip() for d in (selected_domains or []) if str(d).strip()]
    topics = [str(t).strip() for t in (selected_topics or []) if str(t).strip()]
    return InvestigationPlan(
        persona=name,
        tier=str(persona.get("tier") or PERSONA_TIERS.get(name) or "tactical"),
        strategies=[strategy_id] if strategy_id else [],
        strategy_id=strategy_id,
        template_id=str(chosen.get("template_id") or ""),
        domain=domains[0] if domains else "",
        topics=topics,
        original_question=question,
        rationale=(
            f"Fallback {name} plan using decision-tree strategy "
            f"{chosen.get('title') or strategy_id}."
        ),
        charts=charts,
    )



def _charts_for_configured_scope(scope: Mapping[str, Any]) -> list[DashboardChartSpec]:
    """One flat question per configured strategy, using only the configured fields."""
    measures = [str(name).strip() for name in (scope.get("measures") or []) if str(name).strip()]
    dimensions = [str(name).strip() for name in (scope.get("dimensions") or []) if str(name).strip()]
    measure_text = ", ".join(measures) if measures else "the configured measure"
    dimension_text = ", ".join(dimensions)
    charts: list[DashboardChartSpec] = []
    for name in scope.get("strategies") or []:
        strategy_name = str(name).strip()
        if not strategy_name:
            continue
        if dimension_text:
            question = f"What is {measure_text} by {dimension_text} using {strategy_name}?"
        else:
            question = f"What is {measure_text} using {strategy_name}?"
        charts.append(
            DashboardChartSpec(
                level="middle",
                title=strategy_name.replace("_", " ").title(),
                question=question,
                viz_hint="bar" if dimensions else "line",
                purpose=f"Configured {strategy_name} question.",
                operation=strategy_name,
                analytical_strategy=strategy_name,
                components=[*measures, *dimensions],
                measure_hints=list(measures),
            )
        )
    return charts


def _constrain_think_plan(
    plan: InvestigationPlan,
    scope: Mapping[str, Any],
) -> InvestigationPlan:
    """Drop think-mode charts that leave the configured strategy, measure, and dimension set."""
    kept = charts_within_diagnostic_scope(
        [chart.model_dump() for chart in (plan.charts or [])],
        scope,
    )
    charts = [DashboardChartSpec.model_validate(chart) for chart in kept] or _charts_for_configured_scope(scope)
    if not charts:
        return plan
    data = plan.model_dump()
    data["charts"] = [chart.model_dump() for chart in charts]
    data["analytical_operations"] = list(scope.get("strategies") or [])
    activity = str(scope.get("activity_reason") or "").strip()
    if activity:
        data["rationale"] = activity
    data["query_analytical_strategies"] = [
        {"question": chart.question, "analytical_strategy": chart.operation}
        for chart in charts
        if str(chart.question or "").strip()
    ]
    return InvestigationPlan.model_validate(data)


def _frame_think_questions(
    *,
    inputs: dict[str, Any],
    state: Optional[Dict[str, Any]],
) -> InvestigationPlan:
    """Think-only planner. Context is fixed; the agent only frames questions."""
    from langchain.agents import create_agent

    from helicalbi.common.LlmInvokeHelper import (
        get_token_usage_factory,
        merge_time_consumed,
        merge_token_usage,
    )
    from helicalbi.common.configuration import llm as default_llm
    from helicalbi.model.TimeConsumed import TimeConsumed

    user_prompt = PromptTemplate(
        template=CONTEXT_PLAN_PROMPT,
        input_variables=list(inputs.keys()),
        partial_variables={
            "format_instructions": (
                "Return the investigation plan fields. Domain and topics are already chosen."
            ),
        },
    )
    agent = create_agent(
        default_llm,
        tools=None,
        system_prompt=_THINK_QUESTION_SYSTEM,
        response_format=InvestigationPlan,
        name="think_question_framer",
    )
    started = time.perf_counter()
    result = agent.invoke({"messages": [HumanMessage(content=user_prompt.format(**inputs))]})
    elapsed = time.perf_counter() - started
    structured = result.get("structured_response") if isinstance(result, dict) else None
    if isinstance(structured, InvestigationPlan):
        parsed = structured
    elif isinstance(structured, dict):
        parsed = InvestigationPlan.model_validate(structured)
    else:
        raise ValueError("Think question agent returned no investigation plan")
    if state is not None:
        for message in reversed(list(result.get("messages") or [])):
            if isinstance(message, AIMessage):
                usage = get_token_usage_factory(None).from_ai_message(message)
                merge_token_usage(state, usage)
                merge_time_consumed(state, TimeConsumed(llm_seconds=round(elapsed, 3)))
                break
    return parsed


def _cap_charts(plan: InvestigationPlan, max_charts: int) -> InvestigationPlan:
    limit = max(1, int(max_charts or 1))
    if len(plan.charts) <= limit:
        return plan
    capped_charts = plan.charts[:limit]
    queries = [
        {
            "question": str(chart.question or "").strip(),
            "analytical_strategy": str(chart.analytical_strategy or chart.operation or "").strip(),
        }
        for chart in capped_charts
        if str(chart.question or "").strip()
    ]
    return plan.model_copy(update={"charts": capped_charts, "query_analytical_strategies": queries})


def build_investigation_plan(
    question: str,
    *,
    persona: Mapping[str, Any],
    semantic_overview: str = "",
    grounding_pack: str = "",
    validation_feedback: str = "",
    max_charts: int = 5,
    overview_chars: int = DEFAULT_OVERVIEW_CHARS,
    state: Optional[Dict[str, Any]] = None,
    strategy: Optional[Mapping[str, Any]] = None,
    selected_domains: Optional[List[str]] = None,
    selected_topics: Optional[List[str]] = None,
    flat_sql: bool = False,
    analytical_operations: Optional[List[Dict[str, Any]]] = None,
    diagnostic_capabilities: Optional[List[Dict[str, Any]]] = None,
    diagnostic_use_plan: Optional[List[Dict[str, Any]]] = None,
) -> InvestigationPlan:
    """LLM plan guided by strategy + topic grounding pack."""
    diag_caps = list(diagnostic_capabilities or [])
    chosen = _resolve_strategy(question, persona, strategy)
    if not chosen.get("template_id") and chosen.get("id"):
        chosen = {**get_strategy(str(chosen.get("id"))), **chosen}
    if not diag_caps and isinstance(chosen, Mapping) and chosen.get("diagnostic_capability"):
        diag_caps = [dict(chosen["diagnostic_capability"])]
    work_persona = attach_strategy(persona, chosen)
    domains = [str(d).strip() for d in (selected_domains or []) if str(d).strip()]
    topics = [str(t).strip() for t in (selected_topics or []) if str(t).strip()]
    scope = (
        diagnostic_field_scope(list(diagnostic_use_plan or []), diag_caps)
        if flat_sql
        else {"strategies": [], "measures": [], "dimensions": []}
    )
    ops = (
        list(analytical_operations)
        if analytical_operations is not None
        else select_analytical_operations(question, diagnostic_capabilities=diag_caps)
    )
    if scope["strategies"]:
        by_name = {
            str(op.get("operation") or "").strip().lower(): op
            for op in load_analytical_operations()
            if str(op.get("operation") or "").strip()
        }
        for op in ops:
            name = str(op.get("operation") or "").strip()
            if name:
                by_name[name.lower()] = op
        configured_ops = [
            by_name[name.lower()]
            for name in scope["strategies"]
            if name.lower() in by_name
        ]
        if configured_ops:
            ops = configured_ops
    fallback = fallback_investigation_plan(
        question,
        persona=work_persona,
        max_charts=max_charts,
        strategy=chosen,
        selected_domains=domains,
        selected_topics=topics,
    )
    overview = truncate_text(semantic_overview or "", overview_chars)
    pack = truncate_text(grounding_pack or overview, overview_chars)
    feedback = str(validation_feedback or "").strip()
    chart_limit = max(1, int(max_charts or 1)) if max_charts is not None else None
    sql_shape_rules = SQL_SHAPE_DERIVED_TABLES
    if flat_sql:
        chart_limit = None
        sql_shape_rules = THINK_FLAT_SQL_PLAN_RULES
    strategy_block = strategy_prompt_block(chosen)
    if scope["strategies"]:
        strategy_block = (
            strategy_block
            + "\nDashboard layout slots above are optional. "
            "Do not write a question for every band. "
            "Every question must use the configured analytical strategy and only the configured dimensions."
        )
    catalog = strategy_catalog_prompt_block(exclude_id=str(chosen.get("id") or ""))
    if scope["strategies"]:
        catalog = (
            "None. Do not substitute another strategy. Configured strategies: "
            + ", ".join(scope["strategies"])
            + "."
        )
    else:
        catalog = (
            "Use only if the suggested strategy is a poor fit for the semantic model.\n"
            + catalog
        )
    operations_block = analytical_operations_prompt_block(ops)
    if scope["strategies"] and operations_block:
        operations_block = (
            "Configured analytical strategies (these are the only operations allowed):\n"
            + operations_block
        )
    prompt_inputs = {
        "persona_block": persona_prompt_block(work_persona),
        "strategy_block": strategy_block,
        "strategy_catalog": catalog,
        "analytical_operations_block": operations_block,
        "diagnostic_capabilities_block": diagnostic_capabilities_prompt_block(
            diag_caps,
            use_plan=list(diagnostic_use_plan or []) if flat_sql else None,
        ),
        "scope_lock": diagnostic_scope_lock(scope) if flat_sql else "",
        "original_question": question,
        "semantic_overview": overview,
        "grounding_pack": pack or "(no topic grounding pack; use semantic overview)",
        "validation_feedback": (
            f"Validation feedback from prior draft:\n{feedback}" if feedback else ""
        ),
        "max_charts": max_charts,
        "chart_limit": "",
        "sql_shape_rules": sql_shape_rules,
    }
    if domains or topics:
        prompt_inputs["validation_feedback"] = (
            "Finalized context (do not change): "
            f"domain={domains[:1]}; topics={topics}.\n"
            + str(prompt_inputs["validation_feedback"] or "")
        ).strip()
    try:
        if flat_sql:
            parsed = _frame_think_questions(inputs=prompt_inputs, state=state)
        else:
            parsed = invoke_agent_model(
                CONTEXT_PLAN_PROMPT,
                prompt_inputs,
                InvestigationPlan,
                state=state,
            )
    except Exception:
        logger.exception("Investigation planner LLM failed; using fallback plan")
        return fallback

    charts: List[DashboardChartSpec] = list(parsed.charts or [])
    if not charts and scope["strategies"]:
        charts = _charts_for_configured_scope(scope)
    if not charts:
        return fallback
    applied = _applied_strategy(parsed, chosen)
    data = parsed.model_dump()
    strategy_id = str(applied.get("id") or chosen.get("id") or parsed.strategy_id or "")
    data["persona"] = work_persona.get("name") or parsed.persona or DEFAULT_PERSONA
    data["tier"] = work_persona.get("tier") or parsed.tier or fallback.tier
    data["strategies"] = [strategy_id] if strategy_id else list(parsed.strategies or fallback.strategies)
    data["strategy_id"] = strategy_id
    data["template_id"] = str(
        parsed.template_id or applied.get("template_id") or chosen.get("template_id") or ""
    )
    data["original_question"] = question
    data["charts"] = charts
    if domains and (flat_sql or not str(data.get("domain") or "").strip()):
        data["domain"] = domains[0]
    if topics and (flat_sql or not data.get("topics")):
        data["topics"] = topics
    if ops and not data.get("analytical_operations"):
        data["analytical_operations"] = [
            str(op.get("operation") or "").strip()
            for op in ops
            if str(op.get("operation") or "").strip()
        ]
    plan = InvestigationPlan.model_validate(data)
    if flat_sql and scope["strategies"]:
        scope = {
            **scope,
            "activity_reason": diagnostic_activity_reason(diag_caps, scope),
        }
        plan = _constrain_think_plan(plan, scope)
    if diag_caps and flat_sql and len(plan.charts) > 5:
        plan = _cap_charts(plan, 5)
    elif chart_limit is not None and not flat_sql:
        return _cap_charts(plan, chart_limit)
    return plan

