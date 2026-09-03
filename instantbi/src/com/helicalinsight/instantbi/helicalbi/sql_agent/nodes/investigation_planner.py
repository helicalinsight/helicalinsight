"""Build a strategy-aware investigation plan from question + semantic context."""
from __future__ import annotations

import logging
from typing import Any, Dict, List, Mapping, Optional

from helicalbi.sql_agent.config import CONTEXT_PLAN_PROMPT
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
    return InvestigationPlan(
        persona=name,
        tier=str(persona.get("tier") or PERSONA_TIERS.get(name) or "tactical"),
        strategies=[strategy_id] if strategy_id else [],
        strategy_id=strategy_id,
        template_id=str(chosen.get("template_id") or ""),
        domain="",
        topics=[],
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
    max_charts: int = 5,
    overview_chars: int = 3500,
    state: Optional[Dict[str, Any]] = None,
    strategy: Optional[Mapping[str, Any]] = None,
) -> InvestigationPlan:
    """LLM plan guided by one JSON strategy, with similar-line adaptation allowed."""
    chosen = _resolve_strategy(question, persona, strategy)
    if not chosen.get("template_id") and chosen.get("id"):
        chosen = {**get_strategy(str(chosen.get("id"))), **chosen}
    work_persona = attach_strategy(persona, chosen)
    fallback = fallback_investigation_plan(
        question,
        persona=work_persona,
        max_charts=max_charts,
        strategy=chosen,
    )
    try:
        parsed = invoke_agent_model(
            CONTEXT_PLAN_PROMPT,
            {
                "persona_block": persona_prompt_block(work_persona),
                "strategy_block": strategy_prompt_block(chosen),
                "strategy_catalog": strategy_catalog_prompt_block(exclude_id=str(chosen.get("id") or "")),
                "original_question": question,
                "semantic_overview": truncate_text(semantic_overview or "", overview_chars),
                "max_charts": max_charts,
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
    plan = InvestigationPlan.model_validate(data)
    return _cap_charts(plan, max_charts)
