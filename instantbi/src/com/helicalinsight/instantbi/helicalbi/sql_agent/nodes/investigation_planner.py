"""Build a strategy-aware investigation plan from question + semantic context."""
from __future__ import annotations

import logging
from typing import Any, Dict, List, Mapping, Optional

from helicalbi.sql_agent.config import (
    CONTEXT_PLAN_PROMPT,
    SQL_SHAPE_DERIVED_TABLES,
    THINK_EXTRA_QUESTIONS,
    THINK_FLAT_SQL_PLAN_RULES,
)
from helicalbi.sql_agent.llm import invoke_agent_model
from helicalbi.sql_agent.models import DashboardChartSpec, InvestigationPlan
from helicalbi.sql_agent.modes import truncate_text
from helicalbi.sql_agent.personas import DEFAULT_PERSONA, PERSONA_TIERS, persona_prompt_block
from helicalbi.sql_agent.strategy_tree import (
    attach_strategy,
    charts_from_strategy,
    get_strategy,
    list_strategies,
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


def _cap_charts(plan: InvestigationPlan, max_charts: int) -> InvestigationPlan:
    limit = max(1, int(max_charts or 1))
    if len(plan.charts) <= limit:
        return plan
    return plan.model_copy(update={"charts": plan.charts[:limit]})


def build_investigation_plan(
    question: str,
    *,
    persona: Mapping[str, Any],
    semantic_overview: str = "",
    grounding_pack: str = "",
    validation_feedback: str = "",
    max_charts: int = 5,
    overview_chars: int = 3500,
    state: Optional[Dict[str, Any]] = None,
    strategy: Optional[Mapping[str, Any]] = None,
    selected_domains: Optional[List[str]] = None,
    selected_topics: Optional[List[str]] = None,
    flat_sql: bool = False,
) -> InvestigationPlan:
    """LLM plan guided by strategy + topic grounding pack."""
    chosen = _resolve_strategy(question, persona, strategy)
    if not chosen.get("template_id") and chosen.get("id"):
        chosen = {**get_strategy(str(chosen.get("id"))), **chosen}
    work_persona = attach_strategy(persona, chosen)
    domains = [str(d).strip() for d in (selected_domains or []) if str(d).strip()]
    topics = [str(t).strip() for t in (selected_topics or []) if str(t).strip()]
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
    chart_limit = max(1, int(max_charts or 1))
    sql_shape_rules = SQL_SHAPE_DERIVED_TABLES
    if flat_sql:
        chart_limit = chart_limit + max(0, int(THINK_EXTRA_QUESTIONS))
        sql_shape_rules = THINK_FLAT_SQL_PLAN_RULES
    try:
        parsed = invoke_agent_model(
            CONTEXT_PLAN_PROMPT,
            {
                "persona_block": persona_prompt_block(work_persona),
                "strategy_block": strategy_prompt_block(chosen),
                "strategy_catalog": strategy_catalog_prompt_block(exclude_id=str(chosen.get("id") or "")),
                "original_question": question,
                "semantic_overview": overview,
                "grounding_pack": pack or "(no topic grounding pack; use semantic overview)",
                "validation_feedback": (
                    f"Validation feedback from prior draft:\n{feedback}" if feedback else ""
                ),
                "max_charts": max_charts,
                "chart_limit": chart_limit,
                "sql_shape_rules": sql_shape_rules,
            },
            InvestigationPlan,
            state=state,
        )
    except Exception:
        logger.exception("Investigation planner LLM failed; using fallback plan")
        return fallback

    charts: List[DashboardChartSpec] = list(parsed.charts or [])
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
    if not str(data.get("domain") or "").strip() and domains:
        data["domain"] = domains[0]
    if not data.get("topics") and topics:
        data["topics"] = topics
    plan = InvestigationPlan.model_validate(data)
    return _cap_charts(plan, chart_limit)
