"""Load chart definitions from ``viz/charts/*.json``.

Only ``other.json`` remains as a VF template (DrawOther). Standard chart types
are declared in ``_chart_catalog.py`` for selection and aliases.
"""
from __future__ import annotations

import json
import logging
import re
from dataclasses import dataclass, field
from pathlib import Path
from typing import Any, Optional

from helicalbi.viz._chart_selection import ChartOption
from helicalbi.viz._chart_settings import (
    settings_template_from_payload,
    settings_template_to_prompt,
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
    settings: Optional[dict[str, Any]] = None


_CACHE: Optional[dict[str, ChartDefinition]] = None
_ALIAS_INDEX: Optional[dict[str, str]] = None


def _join_lines(value: Any) -> str:
    """Join chart JSON ``instructions`` / ``code`` (list[str] or legacy str) with newlines."""
    if isinstance(value, list):
        return "\n".join(str(line) for line in value)
    if value is None:
        return ""
    return str(value)


def _build_other_code_template(payload: dict) -> str:
    """LLM prompt for the catch-all ``other`` chart: full JS, not ChartSettings."""
    base = OTHER_BASE_RULES
    instructions = _join_lines(payload.get("instructions", "")).strip()
    code = _join_lines(payload.get("code", "")).strip()
    parts = [
        "",
        base.strip(),
        "",
        "Return ONLY a complete DrawOther() JavaScript/JSX function.",
        "Do NOT return ChartSettings JSON — settings injection does not work for other charts.",
        "Adapt the starter template to the user question and result-column metadata.",
        "Replace any `${setting}` / setting.* bindings with real field names from metadata.",
        "Keep function name DrawOther. Keep `data` as the data source.",
        "",
        "### STARTER TEMPLATE (adapt this)",
        code or "// function DrawOther() { ... }",
    ]
    if instructions:
        parts.extend(["", "### CHART INSTRUCTIONS", instructions])
    parts.append("")
    return "\n".join(parts)


def _build_template(payload: dict, settings_template: dict[str, Any]) -> str:
    """LLM fill prompt for a chart.

    Default charts: base rules + settings-fill notes + settings template (no JS code).
    ``other`` (base=other): instructions + starter JS — LLM returns DrawOther code.
    """
    if str(payload.get("base") or "").strip().lower() == "other":
        return _build_other_code_template(payload)

    base = _BASE_RULES.get(payload.get("base", "default"), BASE_RULES)
    instructions = _join_lines(payload.get("instructions", "")).strip()
    settings_json = settings_template_to_prompt(settings_template)
    parts = [
        "",
        base.strip(),
        "",
        "Return ChartSettings JSON only — do NOT return JavaScript / JSX.",
        "Fill the settings template below with real result-column names and labels.",
        "The filled settings object will be injected into the chart code at ${setting}.",
        "",
        "### CHART SETTINGS TEMPLATE (fill these fields)",
        settings_json,
    ]
    if instructions:
        parts.extend(["", "### SETTINGS FILL NOTES", instructions])
    parts.append("")
    return "\n".join(parts)


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
        settings = settings_template_from_payload(payload)
        charts[name] = ChartDefinition(
            name=name,
            option=_option_from_payload(name, payload),
            template=_build_template(payload, settings),
            code=_join_lines(payload.get("code", "")).strip(),
            conversion=_conversion_from_payload(name, payload),
            settings=settings,
        )
    return charts


def get_charts() -> dict[str, ChartDefinition]:
    global _CACHE, _ALIAS_INDEX
    if _CACHE is None:
        _CACHE = load_charts()
        _ALIAS_INDEX = None
    return _CACHE


def get_chart_config() -> dict[str, str]:
    """Return visualization_type -> LLM settings prompt (no JS skeleton)."""
    return {name: chart.template for name, chart in get_charts().items()}


def get_chart_code(chart_name: str) -> str:
    """Return the raw JS/JSX skeleton for ``chart_name`` (no LLM instructions)."""
    chart = get_chart_definition(chart_name)
    return chart.code if chart else ""


def get_chart_settings_schema(chart_name: str) -> Optional[dict[str, Any]]:
    """Return the settings *template* the LLM must fill for ``chart_name``."""
    chart = get_chart_definition(chart_name)
    return dict(chart.settings) if chart and chart.settings else None


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
    """Return static catalog options (plus ``other`` when present on disk)."""
    from helicalbi.viz._chart_catalog import _CHART_OPTION_ROWS

    options: list[ChartOption] = [
        ChartOption(
            visualization_type=row[0],
            dims_min=row[1],
            dims_max=row[2],
            measures_min=row[3],
            measures_max=row[4],
            instruction=row[5],
            requires_ordered=row[6],
            aliases=tuple(row[7]),
        )
        for row in _CHART_OPTION_ROWS
    ]
    other = get_charts().get("other")
    if other is not None:
        options.append(other.option)
    return tuple(options)


def _normalize_chart_name(chart_name: str) -> str:
    return (chart_name or "").strip().lower().replace(" ", "_").replace("-", "_")


def _alias_index() -> dict[str, str]:
    """Map normalized chart name / alias -> canonical visualization_type."""
    global _ALIAS_INDEX
    if _ALIAS_INDEX is not None:
        return _ALIAS_INDEX
    from helicalbi.viz._chart_catalog import CHART_ALIASES

    index: dict[str, str] = dict(CHART_ALIASES)
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


def is_other_chart(chart_name: str) -> bool:
    """True when the type is the catch-all ``other`` chart (or unknown → other)."""
    resolved = resolve_chart_name(chart_name)
    if not resolved:
        return True
    return resolved == "other"


_FUNCTIONAL_FORMAT_RE = re.compile(
    r"(?i)\b("
    r"custom\s+format(?:ter|ting)?|"
    r"format(?:ter|ting)?\s+function|"
    r"functional\s+format(?:ting)?|"
    r"write\s+(?:a\s+)?format(?:ter|ting)?|"
    r"axis\s+format(?:ter|ting)?|"
    r"label\s+format(?:ter|ting)?|"
    r"tooltip\s+format(?:ter|ting)?|"
    r"toFixed|"
    r"Intl\.NumberFormat|"
    r"abbreviate|abbreviation|"
    r"format\s+as\s+(?:k|m|million|billion|percent|percentage|currency)|"
    r"show\s+as\s+(?:k|m|million|billion)|"
    r"conditional\s+(?:format|color|label)|"
    r"custom\s+(?:label|tooltip|axis)\s+format"
    r")\b"
)


def requests_functional_formatting(user_query: str) -> bool:
    """True when the user asks for custom/JS functional formatting beyond Excel formats."""
    text = str(user_query or "").strip()
    if not text:
        return False
    return bool(_FUNCTIONAL_FORMAT_RE.search(text))


def needs_other_fallback(chart_name: str, user_query: str = "") -> bool:
    """Route to Fallback (full DrawOther JS) for ``other`` charts or custom formatters."""
    return is_other_chart(chart_name) or requests_functional_formatting(user_query)


def hydrate_saved_viz(viz: Any) -> dict[str, Any]:
    """Normalize a saved InstantBI viz payload.

    Drops legacy fields (``settings``, ``plot_config``) so older files still load.
    Keeps ``similar_chart`` when present so convert suggestions survive save/load.
    """
    payload = dict(viz or {}) if isinstance(viz, dict) else {}

    payload.pop("settings", None)
    payload.pop("plot_config", None)

    return payload
