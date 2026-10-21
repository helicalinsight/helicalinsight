"""Build LLM prompt blocks for strategies, analytical ops, and diagnostics."""
from __future__ import annotations

from typing import Any, Mapping, Optional, Sequence

from helicalbi.sql_agent.constants import (
    ANALYTICAL_PROMPT_CUSTOM_DETAIL,
    ANALYTICAL_PROMPT_CUSTOM_LINE,
    ANALYTICAL_PROMPT_HEADER,
    ANALYTICAL_PROMPT_LINE,
    CUSTOM_OPERATION_TYPE,
    CAP_KEY_APPLICABLE_TO,
    CAP_KEY_APPLICABLE_TO_SNAKE,
    CAP_KEY_CAPABILITY,
    CAP_KEY_MATCHED_MEASURES,
    CAP_KEY_MEASURE_TRAITS,
    CAP_KEY_USES,
    DEFAULT_CHART_LEVEL,
    DEFAULT_CHART_PURPOSE,
    DEFAULT_VIZ_HINT,
    DIAGNOSTIC_PROMPT_APPLICABLE,
    DIAGNOSTIC_PROMPT_CAPABILITY,
    DIAGNOSTIC_PROMPT_HEADER,
    DIAGNOSTIC_PROMPT_RULE,
    DIAGNOSTIC_PROMPT_USES,
    KIND_DIMENSION,
    KIND_MEASURE,
    KIND_STRATEGY,
    OPERATION_DESCRIPTION_KEY,
    OPERATION_KEY,
    OPERATION_TYPE_KEY,
    PROMPT_NONE,
    STRATEGY_CATALOG_LINE,
    STRATEGY_PROMPT_CHART_INTRO,
    STRATEGY_PROMPT_CHART_LINE,
    STRATEGY_PROMPT_PURPOSE,
    STRATEGY_PROMPT_SELECTED,
    STRATEGY_PROMPT_TEMPLATE,
    STRATEGY_PROMPT_TITLE,
    THINK_PROMPT_ALLOWED_DIMENSIONS,
    THINK_PROMPT_ALLOWED_MEASURES,
    THINK_PROMPT_ALLOWED_STRATEGIES,
    THINK_PROMPT_CAPABILITY,
    THINK_PROMPT_COVER_ALL,
    THINK_PROMPT_DIMENSION,
    THINK_PROMPT_EXCLUSIVE,
    THINK_PROMPT_FOOTER,
    THINK_PROMPT_FOOTER_LOCKED,
    THINK_PROMPT_FORMULA_ORIGIN,
    THINK_PROMPT_HEADER,
    THINK_PROMPT_MEASURE,
    THINK_PROMPT_OTHER,
    THINK_PROMPT_STRATEGY,
    THINK_PROMPT_STRATEGY_FIELDS,
    THINK_PROMPT_STRATEGY_ORIGIN,
    THINK_PROMPT_TRAIT,
    THINK_PROMPT_UNSPECIFIED,
    TRAIT_KEY_ADDITIVITY,
    TRAIT_KEY_DIRECTION,
    TRAIT_KEY_GRAIN,
    TRAIT_KEY_NAME,
    USE_PLAN_KEY_DIMENSIONS,
    USE_PLAN_KEY_KIND,
    USE_PLAN_KEY_MEASURES,
    USE_PLAN_KEY_NAME,
    USE_PLAN_KEY_SOURCE,
    USE_SOURCE_FORMULA,
)
from helicalbi.sql_agent.strategy.catalog import list_strategies
from helicalbi.sql_agent.strategy.diagnostics import (
    diagnostic_field_scope,
    iter_applicable_measures,
    iter_diagnostic_uses,
    measure_trait_guidance,
)
from helicalbi.sql_agent.strategy.text_utils import compact


class StrategyPromptBuilder:
    """Format a selected strategy (and optional catalog peers) for LLM injection."""

    def selected_block(self, strategy: Mapping[str, Any]) -> str:
        charts = strategy.get("charts") or []
        chart_lines = []
        for index, chart in enumerate(charts, start=1):
            if not isinstance(chart, Mapping):
                continue
            chart_lines.append(
                STRATEGY_PROMPT_CHART_LINE.format(
                    index=index,
                    level=chart.get("level") or DEFAULT_CHART_LEVEL,
                    viz=chart.get("viz_hint") or DEFAULT_VIZ_HINT,
                    purpose=chart.get("purpose") or DEFAULT_CHART_PURPOSE,
                )
            )
        return "\n".join(
            [
                STRATEGY_PROMPT_SELECTED.format(strategy_id=strategy.get("id") or ""),
                STRATEGY_PROMPT_TITLE.format(title=strategy.get("title") or ""),
                STRATEGY_PROMPT_TEMPLATE.format(
                    template_id=strategy.get("template_id") or ""
                ),
                STRATEGY_PROMPT_PURPOSE.format(
                    purpose=strategy.get("layout") or strategy.get("consulting_purpose") or ""
                ),
                STRATEGY_PROMPT_CHART_INTRO,
                *(chart_lines or [PROMPT_NONE]),
            ]
        )

    def catalog_block(
        self,
        *,
        exclude_id: Optional[str] = None,
        catalog: Optional[Mapping[str, Any]] = None,
    ) -> str:
        """Short list of other catalog strategies for similar-line substitutions."""
        skip = str(exclude_id or "").strip()
        lines: list[str] = []
        for strategy_id, spec in list_strategies(catalog).items():
            if strategy_id == skip:
                continue
            purpose = str(
                spec.get("layout") or spec.get("consulting_purpose") or spec.get("title") or ""
            ).strip()
            lines.append(
                STRATEGY_CATALOG_LINE.format(strategy_id=strategy_id, purpose=purpose)
            )
        return "\n".join(lines) or PROMPT_NONE


class AnalyticalPromptBuilder:
    """Format selected analytical operations for LLM prompt injection."""

    def build(self, operations: list[dict[str, Any]]) -> str:
        if not operations:
            return ""
        lines = [ANALYTICAL_PROMPT_HEADER]
        for op in operations:
            name = str(op.get(OPERATION_KEY) or "").strip()
            description = str(op.get(OPERATION_DESCRIPTION_KEY) or "").strip()
            if str(op.get(OPERATION_TYPE_KEY) or "").strip().lower() == CUSTOM_OPERATION_TYPE:
                lines.append(
                    ANALYTICAL_PROMPT_CUSTOM_LINE.format(name=name, description=description)
                )
                lines.extend(self._custom_details(op))
            else:
                lines.append(
                    ANALYTICAL_PROMPT_LINE.format(name=name, description=description)
                )
        return "\n".join(lines)

    @staticmethod
    def _custom_details(op: Mapping[str, Any]) -> list[str]:
        details: list[str] = []

        def add(label: str, value: Any) -> None:
            if isinstance(value, list):
                text = ", ".join(str(item).strip() for item in value if str(item).strip())
            else:
                text = str(value or "").strip()
            if text:
                details.append(
                    ANALYTICAL_PROMPT_CUSTOM_DETAIL.format(label=label, value=text)
                )

        add("required inputs", op.get("requiredInputs"))
        add("optional inputs", op.get("optionalInputs"))
        add("preconditions", op.get("preconditions"))
        add("limitations", op.get("limitations"))
        add("output fields", op.get("outputFields"))
        execution = op.get("execution") if isinstance(op.get("execution"), Mapping) else {}
        add("execution", execution.get("logic"))
        return details


class DiagnosticPromptBuilder:
    """Format configured diagnostic capabilities for LLM prompt injection."""

    def build(
        self,
        capabilities: Optional[Sequence[Mapping[str, Any]]],
        *,
        use_plan: Optional[Sequence[Mapping[str, Any]]] = None,
    ) -> str:
        if use_plan:
            return self._think_block(capabilities, use_plan)
        if not capabilities:
            return ""
        lines = [DIAGNOSTIC_PROMPT_HEADER]
        for cap in capabilities:
            if not isinstance(cap, Mapping):
                continue
            name = str(cap.get(CAP_KEY_CAPABILITY) or "").strip()
            applicable = iter_applicable_measures(
                cap.get(CAP_KEY_APPLICABLE_TO) or cap.get(CAP_KEY_APPLICABLE_TO_SNAKE)
            )
            uses = [ref["name"] for ref in iter_diagnostic_uses(cap.get(CAP_KEY_USES))]
            line = DIAGNOSTIC_PROMPT_CAPABILITY.format(name=name)
            if applicable:
                line += DIAGNOSTIC_PROMPT_APPLICABLE.format(
                    measures=", ".join(applicable)
                )
            if uses:
                line += DIAGNOSTIC_PROMPT_USES.format(uses=", ".join(uses))
            lines.append(line)
            lines.append(DIAGNOSTIC_PROMPT_RULE)
        return "\n".join(lines)

    def _think_block(
        self,
        capabilities: Optional[Sequence[Mapping[str, Any]]],
        use_plan: Sequence[Mapping[str, Any]],
    ) -> str:
        """Strict think-mode instructions for matched diagnostic capabilities."""
        lines = list(THINK_PROMPT_HEADER)
        for cap in capabilities or []:
            if not isinstance(cap, Mapping):
                continue
            name = str(cap.get(CAP_KEY_CAPABILITY) or "").strip()
            matched = [
                str(item).strip()
                for item in (cap.get(CAP_KEY_MATCHED_MEASURES) or [])
                if str(item).strip()
            ]
            applicable = iter_applicable_measures(
                cap.get(CAP_KEY_APPLICABLE_TO) or cap.get(CAP_KEY_APPLICABLE_TO_SNAKE)
            )
            targets = matched or applicable
            lines.append(
                THINK_PROMPT_CAPABILITY.format(
                    name=name,
                    targets=", ".join(targets) or THINK_PROMPT_UNSPECIFIED,
                )
            )
            for trait in cap.get(CAP_KEY_MEASURE_TRAITS) or []:
                if not isinstance(trait, Mapping):
                    continue
                trait_name = str(trait.get(TRAIT_KEY_NAME) or "").strip()
                guidance = measure_trait_guidance(
                    str(trait.get(TRAIT_KEY_GRAIN) or ""),
                    str(trait.get(TRAIT_KEY_ADDITIVITY) or ""),
                    str(trait.get(TRAIT_KEY_DIRECTION) or ""),
                )
                if trait_name and guidance:
                    lines.append(
                        THINK_PROMPT_TRAIT.format(name=trait_name, guidance=guidance)
                    )
        lines.append(THINK_PROMPT_COVER_ALL)
        for item in use_plan:
            kind = str(item.get(USE_PLAN_KEY_KIND) or "other")
            name = str(item.get(USE_PLAN_KEY_NAME) or "").strip()
            source = str(item.get(USE_PLAN_KEY_SOURCE) or "").strip()
            if not name:
                continue
            if kind == KIND_STRATEGY:
                origin = (
                    THINK_PROMPT_STRATEGY_ORIGIN.format(source=source)
                    if source and compact(source) != compact(name)
                    else ""
                )
                lines.append(THINK_PROMPT_STRATEGY.format(name=name, origin=origin))
                field_bits: list[str] = []
                bound_measures = [
                    str(value).strip()
                    for value in (item.get(USE_PLAN_KEY_MEASURES) or [])
                    if str(value).strip()
                ]
                bound_dimensions = [
                    str(value).strip()
                    for value in (item.get(USE_PLAN_KEY_DIMENSIONS) or [])
                    if str(value).strip()
                ]
                if bound_measures:
                    field_bits.append("measures " + ", ".join(bound_measures))
                if bound_dimensions:
                    field_bits.append("dimensions " + ", ".join(bound_dimensions))
                if field_bits:
                    lines.append(
                        THINK_PROMPT_STRATEGY_FIELDS.format(fields="; ".join(field_bits))
                    )
            elif kind == KIND_MEASURE:
                origin = THINK_PROMPT_FORMULA_ORIGIN if source == USE_SOURCE_FORMULA else ""
                guidance = measure_trait_guidance(
                    str(item.get(TRAIT_KEY_GRAIN) or ""),
                    str(item.get(TRAIT_KEY_ADDITIVITY) or ""),
                    str(item.get(TRAIT_KEY_DIRECTION) or ""),
                )
                trait_note = f" {guidance}." if guidance else ""
                lines.append(
                    THINK_PROMPT_MEASURE.format(
                        name=name, origin=origin, trait_note=trait_note
                    )
                )
            elif kind == KIND_DIMENSION:
                lines.append(THINK_PROMPT_DIMENSION.format(name=name))
            else:
                lines.append(THINK_PROMPT_OTHER.format(name=name))
        scope = diagnostic_field_scope(use_plan, capabilities)
        if scope["strategies"]:
            lines.append(
                THINK_PROMPT_ALLOWED_STRATEGIES.format(names=", ".join(scope["strategies"]))
            )
            if scope["measures"]:
                lines.append(
                    THINK_PROMPT_ALLOWED_MEASURES.format(names=", ".join(scope["measures"]))
                )
            if scope["dimensions"]:
                lines.append(
                    THINK_PROMPT_ALLOWED_DIMENSIONS.format(
                        names=", ".join(scope["dimensions"])
                    )
                )
            lines.append(THINK_PROMPT_EXCLUSIVE)
            lines.append(
                THINK_PROMPT_FOOTER_LOCKED
                if scope["measures"] or scope["dimensions"]
                else THINK_PROMPT_FOOTER
            )
        else:
            lines.append(THINK_PROMPT_FOOTER)
        return "\n".join(lines)


_STRATEGY_PROMPTS = StrategyPromptBuilder()
_ANALYTICAL_PROMPTS = AnalyticalPromptBuilder()
_DIAGNOSTIC_PROMPTS = DiagnosticPromptBuilder()


def strategy_prompt_block(strategy: Mapping[str, Any]) -> str:
    return _STRATEGY_PROMPTS.selected_block(strategy)


def strategy_catalog_prompt_block(
    *,
    exclude_id: Optional[str] = None,
    catalog: Optional[Mapping[str, Any]] = None,
) -> str:
    """Short list of other catalog strategies for similar-line substitutions."""
    return _STRATEGY_PROMPTS.catalog_block(exclude_id=exclude_id, catalog=catalog)


def analytical_operations_prompt_block(operations: list[dict[str, Any]]) -> str:
    """Format selected analytical operations for LLM prompt injection."""
    return _ANALYTICAL_PROMPTS.build(operations)


def diagnostic_capabilities_prompt_block(
    capabilities: Optional[Sequence[Mapping[str, Any]]],
    *,
    use_plan: Optional[Sequence[Mapping[str, Any]]] = None,
) -> str:
    """Format configured diagnostic capabilities for LLM prompt injection."""
    return _DIAGNOSTIC_PROMPTS.build(capabilities, use_plan=use_plan)
