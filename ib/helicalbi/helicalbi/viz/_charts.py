"""Load chart definitions from ``viz/charts/*.json``.

Each file name is the visualization_type. Add a chart by dropping a new JSON
file into that folder — no Python module required.

Optional JSON keys:
- ``instructions`` / ``code``: list of lines (joined with ``\\n`` for the LLM)
- ``conversion``: non-LLM interconversion contract (family, field roles, omit keys)

Similar charts are deduced at runtime from ``possible_chart_options`` in
``_chart_selection.py`` (same filter used for the LLM viz prompt) — not from
chart JSON.
"""
from __future__ import annotations

import json
import logging
from dataclasses import dataclass, field
from pathlib import Path
from typing import Any, Optional

from helicalbi.viz._chart_selection import (
    ChartOption,
    _build_chart_selection_table,
    format_similar_chart_wire,
    resolve_similar_charts,
)
from helicalbi.viz._template_instructions import BASE_RULES, OTHER_BASE_RULES

logger = logging.getLogger(__name__)

CHARTS_DIR = Path(__file__).parent / "charts"

_BASE_RULES = {
    "default": BASE_RULES,
    "other": OTHER_BASE_RULES,
}

# Valid conversion.family values (Ant Design Charts v1 data-shape families).
CONVERSION_FAMILIES = frozenset(
    {
        "cartesian",
        "bar",
        "pie",
        "dual_axes",
        "tiny",
        "hierarchy",
        "percent",
        "heatmap",
        "bubble",
        "kpi",
        "table",
        "other",
    }
)


@dataclass(frozen=True)
class ConversionField:
    """Maps a skeleton placeholder to a semantic bag role."""

    role: str  # dimension | measure | series | title
    index: int = 0
    optional: bool = False


@dataclass(frozen=True)
class ChartConversion:
    """Non-LLM conversion contract declared in chart JSON."""

    family: str
    component: str = ""
    fields: dict[str, ConversionField] = field(default_factory=dict)
    omit_when_missing: tuple[str, ...] = ()


@dataclass(frozen=True)
class ChartDefinition:
    name: str
    option: ChartOption
    template: str
    code: str = ""
    conversion: Optional[ChartConversion] = None


_CACHE: Optional[dict[str, ChartDefinition]] = None
_ALIAS_INDEX: Optional[dict[str, str]] = None


def _join_lines(value: Any) -> str:
    """Join chart JSON ``instructions`` / ``code`` (list[str] or legacy str) with newlines."""
    if isinstance(value, list):
        return "\n".join(str(line) for line in value)
    if value is None:
        return ""
    return str(value)


def _build_template(payload: dict) -> str:
    base = _BASE_RULES.get(payload.get("base", "default"), BASE_RULES)
    instructions = "\n" + _join_lines(payload.get("instructions", "")).strip() + "\n"
    code = _join_lines(payload.get("code", ""))
    if code and not code.startswith("\n"):
        code = "\n" + code
    if code and not code.endswith("\n"):
        code = code + "\n"
    return "\n" + base + instructions + code


def _aliases_from_payload(payload: dict) -> tuple[str, ...]:
    raw = payload.get("aliases") or []
    if not isinstance(raw, list):
        return ()
    aliases: list[str] = []
    seen: set[str] = set()
    for item in raw:
        text = str(item).strip()
        if not text:
            continue
        key = text.casefold()
        if key in seen:
            continue
        seen.add(key)
        aliases.append(text)
    return tuple(aliases)


def _option_from_payload(name: str, payload: dict) -> ChartOption:
    return ChartOption(
        visualization_type=name,
        dims_min=int(payload["dims_min"]),
        dims_max=payload.get("dims_max"),
        measures_min=int(payload["measures_min"]),
        measures_max=payload.get("measures_max"),
        instruction=str(payload["instruction"]),
        requires_ordered=bool(payload.get("requires_ordered", False)),
        aliases=_aliases_from_payload(payload),
    )


def _conversion_from_payload(name: str, payload: dict) -> Optional[ChartConversion]:
    """Parse optional ``conversion`` block used by non-LLM chart switching."""
    raw = payload.get("conversion")
    if not isinstance(raw, dict) or not raw:
        return None

    family = str(raw.get("family") or "").strip().lower()
    if family not in CONVERSION_FAMILIES:
        logger.warning(
            "Chart %s has unknown conversion.family=%r; ignoring conversion block",
            name,
            family,
        )
        return None

    fields_raw = raw.get("fields") or {}
    fields: dict[str, ConversionField] = {}
    if isinstance(fields_raw, dict):
        for placeholder, spec in fields_raw.items():
            key = str(placeholder).strip()
            if not key or not isinstance(spec, dict):
                continue
            role = str(spec.get("role") or "").strip().lower()
            if role not in {"dimension", "measure", "series", "title"}:
                logger.warning(
                    "Chart %s conversion field %r has invalid role=%r",
                    name,
                    key,
                    role,
                )
                continue
            fields[key] = ConversionField(
                role=role,
                index=int(spec.get("index") or 0),
                optional=bool(spec.get("optional", False)),
            )

    omit_raw = raw.get("omit_when_missing") or []
    omit: list[str] = []
    if isinstance(omit_raw, list):
        omit = [str(item).strip() for item in omit_raw if str(item).strip()]

    return ChartConversion(
        family=family,
        component=str(raw.get("component") or "").strip(),
        fields=fields,
        omit_when_missing=tuple(omit),
    )


def load_charts() -> dict[str, ChartDefinition]:
    """Scan ``charts/*.json`` and return name -> definition."""
    charts: dict[str, ChartDefinition] = {}
    if not CHARTS_DIR.is_dir():
        return charts
    for path in sorted(CHARTS_DIR.glob("*.json")):
        name = path.stem
        payload = json.loads(path.read_text(encoding="utf-8"))
        charts[name] = ChartDefinition(
            name=name,
            option=_option_from_payload(name, payload),
            template=_build_template(payload),
            code=_join_lines(payload.get("code", "")).strip(),
            conversion=_conversion_from_payload(name, payload),
        )
    return charts


def _log_decision_table(charts: dict[str, ChartDefinition]) -> None:
    options = tuple(chart.option for chart in charts.values())
    table = _build_chart_selection_table(options)
    logger.info(
        "Loaded chart decision table (%s charts):\n%s",
        len(options),
        table,
    )


def get_charts() -> dict[str, ChartDefinition]:
    global _CACHE, _ALIAS_INDEX
    if _CACHE is None:
        _CACHE = load_charts()
        _ALIAS_INDEX = None
        _log_decision_table(_CACHE)
    return _CACHE


def get_chart_config() -> dict[str, str]:
    """Return visualization_type -> template string (LLM fill prompts)."""
    return {name: chart.template for name, chart in get_charts().items()}


def get_chart_code(chart_name: str) -> str:
    """Return the raw JS/JSX skeleton for ``chart_name`` (no LLM instructions)."""
    chart = get_chart_definition(chart_name)
    return chart.code if chart else ""


def get_chart_definition(chart_name: str) -> Optional[ChartDefinition]:
    """Return the catalog definition for a chart name or alias."""
    resolved = resolve_chart_name(chart_name)
    if not resolved:
        return None
    return get_charts().get(resolved)


def get_chart_conversion(chart_name: str) -> Optional[ChartConversion]:
    """Return the non-LLM conversion contract for a chart, if declared."""
    chart = get_chart_definition(chart_name)
    return chart.conversion if chart else None


def get_chart_options() -> tuple[ChartOption, ...]:
    return tuple(chart.option for chart in get_charts().values())


def _normalize_chart_name(chart_name: str) -> str:
    return (chart_name or "").strip().lower().replace(" ", "_").replace("-", "_")


def _alias_index() -> dict[str, str]:
    """Map normalized chart name / alias -> canonical visualization_type."""
    global _ALIAS_INDEX
    if _ALIAS_INDEX is not None:
        return _ALIAS_INDEX
    index: dict[str, str] = {}
    for name, chart in get_charts().items():
        index[_normalize_chart_name(name)] = name
        for alias in chart.option.aliases:
            key = _normalize_chart_name(alias)
            index.setdefault(key, name)
    _ALIAS_INDEX = index
    return index


def resolve_chart_name(chart_name: str) -> Optional[str]:
    """Resolve a chart name or alias to a catalog visualization_type."""
    key = _normalize_chart_name(chart_name)
    if not key:
        return None
    return _alias_index().get(key)


def hydrate_saved_viz(viz: Any, *, data_types: Any = None) -> dict[str, Any]:
    """Fill missing similar_chart on a saved InstantBI viz payload.

    Used by ``/load-chat`` so open mode restores preferred chart type
    even for older files that only stored ``chart_name`` / ``vf_template``.
    """
    payload = dict(viz or {}) if isinstance(viz, dict) else {}
    chart_name = str(payload.get("chart_name") or "").strip()
    similar = payload.get("similar_chart")

    needs_similar = not isinstance(similar, list) or len(similar) == 0

    if chart_name and needs_similar:
        try:
            similar = resolve_similar_charts(
                chart_name,
                data_types=data_types,
            )
        except Exception:
            logger.exception(
                "Failed to hydrate saved viz preferences for chart_name=%s",
                chart_name,
            )
            similar = []

    payload["similar_chart"] = format_similar_chart_wire(similar)

    # Drop legacy InstantBI chart settings if present on older saves.
    payload.pop("settings", None)
    payload.pop("plot_config", None)

    return payload
