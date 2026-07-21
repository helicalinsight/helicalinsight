"""Load chart definitions from ``viz/charts/*.json``.

Each file name is the visualization_type. Add a chart by dropping a new JSON
file into that folder — no Python module required.
"""
from __future__ import annotations

import json
import logging
from dataclasses import dataclass
from pathlib import Path
from typing import Optional

from helicalbi.viz._chart_selection import ChartOption, _build_chart_selection_table
from helicalbi.viz._template_instructions import BASE_RULES, OTHER_BASE_RULES

logger = logging.getLogger(__name__)

CHARTS_DIR = Path(__file__).parent / "charts"

_BASE_RULES = {
    "default": BASE_RULES,
    "other": OTHER_BASE_RULES,
}


@dataclass(frozen=True)
class ChartDefinition:
    name: str
    option: ChartOption
    template: str


_CACHE: Optional[dict[str, ChartDefinition]] = None


def _build_template(payload: dict) -> str:
    base = _BASE_RULES.get(payload.get("base", "default"), BASE_RULES)
    instructions = "\n" + str(payload.get("instructions", "")).strip() + "\n"
    code = str(payload.get("code", ""))
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
    global _CACHE
    if _CACHE is None:
        _CACHE = load_charts()
        _log_decision_table(_CACHE)
    return _CACHE


def get_chart_config() -> dict[str, str]:
    return {name: chart.template for name, chart in get_charts().items()}


def get_chart_options() -> tuple[ChartOption, ...]:
    return tuple(chart.option for chart in get_charts().values())
