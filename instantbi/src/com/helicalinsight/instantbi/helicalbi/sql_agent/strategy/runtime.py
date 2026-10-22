"""Public API projections and plan-runtime hydration from the strategy catalog."""
from __future__ import annotations

from typing import Any, Mapping

from helicalbi.sql_agent.constants import (
    CHART_QUESTION_TEMPLATE,
    DEFAULT_CHART_LEVEL,
    DEFAULT_CHART_TITLE,
    DEFAULT_MAX_CHARTS,
    DEFAULT_QUESTION_FALLBACK,
    DEFAULT_VIZ_HINT,
    FALLBACK_MAX_CHARTS,
    OPERATION_ANALYTICAL_STRATEGY_KEY,
    OPERATION_KEY,
    PUBLIC_PERSONA_STRIPPED_KEYS,
)
from helicalbi.sql_agent.strategy.catalog import get_strategy


class PublicStrategyProjector:
    """API/storage view: strategy id only."""

    def project(self, strategy: Mapping[str, Any] | str | None) -> str:
        if isinstance(strategy, str):
            return strategy.strip()
        if isinstance(strategy, Mapping):
            return str(strategy.get("id") or "").strip()
        return ""


class PublicPersonaProjector:
    """Persona without nested/repeated strategy objects — id lives on plan/response."""

    def project(self, persona: Mapping[str, Any] | None) -> dict[str, Any]:
        attached = dict(persona or {})
        for key in PUBLIC_PERSONA_STRIPPED_KEYS:
            attached.pop(key, None)
        return attached


class PlanRuntimeResolver:
    """Hydrate layout/color from the catalog using the stored template name."""

    def resolve(self, plan: Mapping[str, Any] | None) -> dict[str, Any]:
        plan = plan if isinstance(plan, Mapping) else {}
        strategy_id = str(plan.get("strategy_id") or "").strip()
        template_id = str(plan.get("template_id") or "").strip()
        if not strategy_id and not template_id:
            return {
                "template_id": "",
                "layout_guidance": str(plan.get("layout_guidance") or plan.get("layout") or ""),
                "color_guidance": str(plan.get("color_guidance") or ""),
                "design_application": str(plan.get("design_application") or ""),
            }
        catalog = get_strategy(strategy_id) if strategy_id else {}
        layout = str(
            plan.get("layout_guidance")
            or plan.get("layout")
            or catalog.get("layout")
            or catalog.get("layout_guidance")
            or ""
        )
        return {
            "template_id": template_id or str(catalog.get("template_id") or ""),
            "layout_guidance": layout,
            "color_guidance": str(plan.get("color_guidance") or catalog.get("color_guidance") or ""),
            "design_application": str(
                plan.get("design_application") or catalog.get("design_application") or ""
            ),
        }


class StrategyChartBuilder:
    """Materialize chart question slots from a strategy template."""

    def build(
        self,
        strategy: Mapping[str, Any],
        question: str,
        *,
        max_charts: int = DEFAULT_MAX_CHARTS,
    ) -> list[dict[str, Any]]:
        q = (question or "").strip() or DEFAULT_QUESTION_FALLBACK
        limit = max(1, int(max_charts or FALLBACK_MAX_CHARTS))
        charts: list[dict[str, Any]] = []
        for spec in (strategy.get("charts") or [])[:limit]:
            if not isinstance(spec, Mapping):
                continue
            title = str(spec.get("title") or DEFAULT_CHART_TITLE)
            op = str(
                spec.get(OPERATION_KEY)
                or spec.get(OPERATION_ANALYTICAL_STRATEGY_KEY)
                or ""
            )
            charts.append(
                {
                    "level": str(spec.get("level") or DEFAULT_CHART_LEVEL),
                    "title": title,
                    "question": (
                        CHART_QUESTION_TEMPLATE.format(question=q, title=title)
                        if title
                        else q
                    ),
                    "viz_hint": str(spec.get("viz_hint") or DEFAULT_VIZ_HINT),
                    OPERATION_KEY: op,
                    OPERATION_ANALYTICAL_STRATEGY_KEY: op,
                    "purpose": str(spec.get("purpose") or ""),
                    "context_anchor": str(spec.get("context_anchor") or ""),
                    "include_in_dashboard": True,
                }
            )
        return charts


_PUBLIC_STRATEGY = PublicStrategyProjector()
_PUBLIC_PERSONA = PublicPersonaProjector()
_RUNTIME = PlanRuntimeResolver()
_CHARTS = StrategyChartBuilder()


def public_strategy(strategy: Mapping[str, Any] | str | None) -> str:
    """API/storage view: strategy id only."""
    return _PUBLIC_STRATEGY.project(strategy)


def public_persona(persona: Mapping[str, Any] | None) -> dict[str, Any]:
    """Persona without nested/repeated strategy objects — id lives on plan/response."""
    return _PUBLIC_PERSONA.project(persona)


def resolve_plan_runtime(plan: Mapping[str, Any] | None) -> dict[str, Any]:
    """Hydrate layout/color from the catalog using the stored template name."""
    return _RUNTIME.resolve(plan)


def charts_from_strategy(
    strategy: Mapping[str, Any],
    question: str,
    *,
    max_charts: int = DEFAULT_MAX_CHARTS,
) -> list[dict[str, Any]]:
    return _CHARTS.build(strategy, question, max_charts=max_charts)
