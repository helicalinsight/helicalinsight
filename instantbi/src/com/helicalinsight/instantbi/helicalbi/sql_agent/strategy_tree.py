"""Load consulting strategies from JSON and pick one via a decision tree."""
from __future__ import annotations

import json
import logging
from functools import lru_cache
from pathlib import Path
from typing import Any, Dict, Mapping, Optional

logger = logging.getLogger(__name__)

CATALOG_PATH = Path(__file__).resolve().parent / "strategies" / "dashboard_strategies.json"

STRATEGY_INVERTED_PYRAMID = "inverted_pyramid"
STRATEGY_PROGRESSIVE_DISCLOSURE = "progressive_disclosure"
STRATEGY_MECE = "mece_metric_structuring"
STRATEGY_SEMANTIC_COLOR = "semantic_color_coding"
STRATEGY_INTENT_LAYOUT = "intent_driven_layout"
STRATEGY_SO_WHAT = "so_what_contextual_anchoring"

_INTENT_OVERVIEW = "overview"


def _read_catalog(path: Optional[Path] = None) -> dict[str, Any]:
    catalog_path = Path(path) if path is not None else CATALOG_PATH
    with catalog_path.open(encoding="utf-8") as handle:
        data = json.load(handle)
    if not isinstance(data, dict) or not isinstance(data.get("strategies"), dict):
        raise ValueError(f"Invalid strategy catalog: {catalog_path}")
    return data


@lru_cache(maxsize=4)
def load_strategy_catalog(path: str = "") -> dict[str, Any]:
    """Cached catalog. Pass a path string to load an alternate file (tests)."""
    return _read_catalog(Path(path) if path else CATALOG_PATH)


def clear_strategy_catalog_cache() -> None:
    load_strategy_catalog.cache_clear()


def list_strategies(catalog: Optional[Mapping[str, Any]] = None) -> Dict[str, dict[str, Any]]:
    data = catalog or load_strategy_catalog()
    strategies = data.get("strategies") or {}
    return {str(key): dict(value) for key, value in strategies.items() if isinstance(value, dict)}


def get_strategy(strategy_id: Optional[str], catalog: Optional[Mapping[str, Any]] = None) -> dict[str, Any]:
    data = catalog or load_strategy_catalog()
    strategies = list_strategies(data)
    key = str(strategy_id or "").strip()
    if key in strategies:
        return dict(strategies[key])
    default_id = str(data.get("default_strategy") or STRATEGY_MECE)
    if default_id in strategies:
        return dict(strategies[default_id])
    if strategies:
        return dict(next(iter(strategies.values())))
    raise ValueError("Strategy catalog has no strategies")


def classify_intent(question: str, catalog: Optional[Mapping[str, Any]] = None) -> str:
    """First matching intent from the catalog keyword list; otherwise overview."""
    text = f" {str(question or '').strip().lower()} "
    intents = (catalog or load_strategy_catalog()).get("intents") or []
    for intent in intents:
        if not isinstance(intent, Mapping):
            continue
        intent_id = str(intent.get("id") or "").strip()
        keywords = intent.get("keywords") or []
        if not intent_id:
            continue
        for keyword in keywords:
            needle = str(keyword or "").strip().lower()
            if needle and needle in text:
                return intent_id
    return _INTENT_OVERVIEW


def _context_value(context: Mapping[str, Any], field: str) -> str:
    return str(context.get(field) or "").strip().lower()


def _case_matches(case: Mapping[str, Any], value: str) -> bool:
    if case.get("default"):
        return False
    if "equals" in case:
        return value == str(case.get("equals") or "").strip().lower()
    allowed = case.get("in") or []
    if isinstance(allowed, (list, tuple)):
        return value in {str(item).strip().lower() for item in allowed}
    return False


def _walk_tree(
    node: Mapping[str, Any],
    context: Mapping[str, Any],
    catalog: Mapping[str, Any],
) -> str:
    if not isinstance(node, Mapping):
        return str(catalog.get("default_strategy") or STRATEGY_MECE)
    if node.get("strategy"):
        return str(node["strategy"])
    field = str(node.get("match") or "").strip()
    value = _context_value(context, field) if field else ""
    cases = node.get("cases") or []
    default_case: Optional[Mapping[str, Any]] = None
    for case in cases:
        if not isinstance(case, Mapping):
            continue
        if case.get("default"):
            default_case = case
            continue
        if _case_matches(case, value):
            if case.get("strategy"):
                return str(case["strategy"])
            if isinstance(case.get("then"), Mapping):
                return _walk_tree(case["then"], context, catalog)
    if default_case:
        if default_case.get("strategy"):
            return str(default_case["strategy"])
        if isinstance(default_case.get("then"), Mapping):
            return _walk_tree(default_case["then"], context, catalog)
    return str(catalog.get("default_strategy") or STRATEGY_MECE)


def select_strategy(
    question: str,
    *,
    persona: Optional[Mapping[str, Any]] = None,
    hint: Optional[str] = None,
    catalog: Optional[Mapping[str, Any]] = None,
) -> dict[str, Any]:
    """Pick exactly one consulting strategy using hint, then the JSON decision tree."""
    data = catalog or load_strategy_catalog()
    strategies = list_strategies(data)
    raw_hint = hint
    if not raw_hint and persona:
        raw_hint = persona.get("strategy_hint") or persona.get("strategy_id")
    hinted = str(raw_hint or "").strip().replace("-", "_").replace(" ", "_")
    if hinted and hinted in strategies:
        chosen = dict(strategies[hinted])
        chosen["selection"] = {"source": "hint", "intent": classify_intent(question, data)}
        return chosen

    persona_name = str((persona or {}).get("name") or "").strip()
    intent = classify_intent(question, data)
    tree = data.get("decision_tree") if isinstance(data.get("decision_tree"), Mapping) else {}
    strategy_id = _walk_tree(
        tree,
        {"persona": persona_name, "intent": intent, "strategy_hint": hinted},
        data,
    )
    chosen = get_strategy(strategy_id, data)
    chosen["selection"] = {
        "source": "decision_tree",
        "intent": intent,
        "persona": persona_name,
        "strategy_id": chosen.get("id"),
    }
    logger.info(
        "Selected dashboard strategy=%s intent=%s persona=%s",
        chosen.get("id"),
        intent,
        persona_name,
    )
    return chosen


def attach_strategy(persona: Mapping[str, Any], strategy: Mapping[str, Any]) -> dict[str, Any]:
    """Copy persona and stash the selected strategy id (full catalog used at runtime)."""
    attached = dict(persona or {})
    strategy_id = str(strategy.get("id") or "").strip()
    attached["strategy_id"] = strategy_id
    attached["strategies"] = [strategy_id] if strategy_id else []
    # Keep a compact strategy map for internal prompt helpers; public_persona strips it.
    attached["strategy"] = {"id": strategy_id}
    attached.pop("strategy_guide", None)
    attached.pop("template_id", None)
    return attached


def strategy_prompt_block(strategy: Mapping[str, Any]) -> str:
    charts = strategy.get("charts") or []
    chart_lines = []
    for index, chart in enumerate(charts, start=1):
        if not isinstance(chart, Mapping):
            continue
        chart_lines.append(
            f"{index}. [{chart.get('level') or 'middle'}] {chart.get('title') or ''} "
            f"({chart.get('viz_hint') or 'bar'}) — {chart.get('purpose') or 'reference slot'}"
        )
    return "\n".join(
        [
            f"Selected strategy: {strategy.get('id') or ''}",
            f"Title: {strategy.get('title') or ''}",
            f"Layout template name: {strategy.get('template_id') or ''}",
            f"Purpose: {strategy.get('consulting_purpose') or ''}",
            "Chart slots (reference only — write original questions from the semantic "
            "model; do not copy titles or canned question text):",
            *(chart_lines or ["(none)"]),
        ]
    )


def strategy_catalog_prompt_block(
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
        purpose = str(spec.get("consulting_purpose") or spec.get("title") or "").strip()
        lines.append(f"- {strategy_id}: {purpose}")
    return "\n".join(lines) or "(none)"


def public_strategy(strategy: Mapping[str, Any] | str | None) -> str:
    """API/storage view: strategy id only."""
    if isinstance(strategy, str):
        return strategy.strip()
    if isinstance(strategy, Mapping):
        return str(strategy.get("id") or "").strip()
    return ""


def public_persona(persona: Mapping[str, Any] | None) -> dict[str, Any]:
    """Persona without nested/repeated strategy objects — id lives on plan/response."""
    attached = dict(persona or {})
    for key in ("strategy", "strategies", "strategy_guide", "template_id", "strategy_id"):
        attached.pop(key, None)
    return attached


def resolve_plan_runtime(plan: Mapping[str, Any] | None) -> dict[str, Any]:
    """Hydrate layout/color from the catalog using the stored template name."""
    plan = plan if isinstance(plan, Mapping) else {}
    strategy_id = str(plan.get("strategy_id") or "").strip()
    template_id = str(plan.get("template_id") or "").strip()
    if not strategy_id and not template_id:
        return {
            "template_id": "",
            "layout_guidance": str(plan.get("layout_guidance") or ""),
            "color_guidance": str(plan.get("color_guidance") or ""),
            "design_application": str(plan.get("design_application") or ""),
        }
    catalog = get_strategy(strategy_id) if strategy_id else {}
    return {
        "template_id": template_id or str(catalog.get("template_id") or ""),
        "layout_guidance": str(plan.get("layout_guidance") or catalog.get("layout_guidance") or ""),
        "color_guidance": str(plan.get("color_guidance") or catalog.get("color_guidance") or ""),
        "design_application": str(plan.get("design_application") or catalog.get("design_application") or ""),
    }


def charts_from_strategy(
    strategy: Mapping[str, Any],
    question: str,
    *,
    max_charts: int = 5,
) -> list[dict[str, Any]]:
    q = (question or "").strip() or "the requested metric"
    limit = max(1, int(max_charts or 3))
    charts: list[dict[str, Any]] = []
    for spec in (strategy.get("charts") or [])[:limit]:
        if not isinstance(spec, Mapping):
            continue
        title = str(spec.get("title") or "Chart")
        charts.append(
            {
                "level": str(spec.get("level") or "middle"),
                "title": title,
                "question": f"{q} — {title}" if title else q,
                "viz_hint": str(spec.get("viz_hint") or "bar"),
                "purpose": str(spec.get("purpose") or ""),
                "context_anchor": str(spec.get("context_anchor") or ""),
                "include_in_dashboard": True,
            }
        )
    return charts
