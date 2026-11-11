"""Detect cube_info models and flatten hierarchies and aiContext."""

from __future__ import annotations

import re
from typing import Any, List, Optional, Set, Tuple


def is_cube_info_model(model_data: dict) -> bool:
    """Return True when the model file uses the cube_info semantic structure."""
    if not isinstance(model_data, dict):
        return False

    cube_info = model_data.get("cube_info",model_data.get("cube"))
    if not isinstance(cube_info, list) or not cube_info:
        return False

    for cube in cube_info:
        if not isinstance(cube, dict):
            continue
        for key in ("dimensions", "measures"):
            items = cube.get(key)
            if not isinstance(items, list) or not items:
                continue
            for item in items:
                if not isinstance(item, dict):
                    continue
                if item.get("columnName") or item.get("dimensionName") or item.get("measureName"):
                    return True
                # Hierarchy levels alone are enough to treat this as cube_info.
                for hierarchy in item.get("hierarchies") or []:
                    if not isinstance(hierarchy, dict):
                        continue
                    for level in hierarchy.get("levels") or []:
                        if not isinstance(level, dict):
                            continue
                        if (
                            level.get("columnName")
                            or level.get("levelName")
                            or level.get("dimensionName")
                            or level.get("measureName")
                        ):
                            return True
    return False


def _topic_name(topic: Any) -> str:
    """Return a topic label from a string or ``{topic, ...}`` object."""
    if isinstance(topic, dict):
        name = topic.get("topic") or topic.get("topic_name") or ""
        return str(name) if name else ""
    if topic:
        return str(topic)
    return ""


def extract_domain_topics(model_data: dict) -> Tuple[List[str], List[str]]:
    """Collect domain names and topics from the cube_info model domain block."""
    domains: List[str] = []
    topics: List[str] = []
    for entry in model_data.get("domain") or []:
        if not isinstance(entry, dict):
            continue
        domain_name = entry.get("domain_name")
        if domain_name:
            domains.append(str(domain_name))
        for topic in entry.get("topics") or []:
            name = _topic_name(topic)
            if name:
                topics.append(name)
    return domains, list(dict.fromkeys(topics))


def _synonyms_from_value(value: Any) -> List[str]:
    """Normalize synonym payloads (list or comma/newline-separated string)."""
    if value is None:
        return []
    if isinstance(value, list):
        return [str(item).strip() for item in value if item and str(item).strip()]
    text = str(value).strip()
    if not text:
        return []
    if "," in text or "\n" in text:
        parts = re.split(r"[,;\n]+", text)
        return [part.strip() for part in parts if part.strip()]
    return [text]


def _ai_context_from_item(item: dict) -> dict:
    ai = item.get("aiContext") if isinstance(item.get("aiContext"), dict) else None
    if ai is None:
        ai = item.get("ai_context") if isinstance(item.get("ai_context"), dict) else {}
    return ai or {}


def apply_ai_context(item: dict) -> dict:
    """Fold ``aiContext`` into fields used by SQL and visualization prompts.

    ``instructions`` / ``examples`` enrich description (and dedicated fields).
    ``synonyms`` become a list on the item for synonym resolution.
    """
    if not isinstance(item, dict):
        return item
    if item.get("_ai_context_applied"):
        return item

    normalized = dict(item)
    ai = _ai_context_from_item(normalized)

    synonyms = _synonyms_from_value(normalized.get("synonyms"))
    if not synonyms:
        synonyms = _synonyms_from_value(ai.get("synonyms"))
    if synonyms:
        normalized["synonyms"] = synonyms

    instructions = str(ai.get("instructions") or "").strip()
    examples = str(ai.get("examples") or "").strip()
    if instructions:
        normalized["ai_instructions"] = instructions
    if examples:
        normalized["ai_examples"] = examples

    description = str(normalized.get("description") or "").strip()
    extras: List[str] = []
    if instructions:
        extras.append(f"AI instructions (SQL/viz): {instructions}")
    if examples:
        extras.append(f"examples: {examples}")
    if extras:
        normalized["description"] = (
            f"{description}; {'; '.join(extras)}" if description else "; ".join(extras)
        )

    normalized["_ai_context_applied"] = True
    return normalized


def _inherit_parent_display_fields(level: dict, parent: dict) -> dict:
    """Copy parent formatString / sortOrder onto a level when the level omits them."""
    item = dict(level)
    for key in ("formatString", "format_string", "format", "sortOrder", "sort_order","sort"):
        if item.get(key) in (None, "") and parent.get(key) not in (None, ""):
            item[key] = parent[key]
    return item


def _lift_formula_to_top_level(item: dict) -> dict:
    """Promote ``metric.formula`` to a top-level ``formula`` key when needed.

    Incoming cube payloads may nest formula under ``metric``; normalized items
    expose ``formula`` directly. Other metric fields (aggregator, format, …)
    stay on the metric object for backward-compatible readers.
    """
    if not isinstance(item, dict):
        return item
    metric_obj = item.get("metric") if isinstance(item.get("metric"), dict) else {}
    formula = item.get("formula") or metric_obj.get("formula")
    if formula in (None, ""):
        return item

    lifted = dict(item)
    lifted["formula"] = str(formula)
    if metric_obj and "formula" in metric_obj:
        cleaned_metric = {k: v for k, v in metric_obj.items() if k != "formula"}
        if cleaned_metric:
            lifted["metric"] = cleaned_metric
        else:
            lifted.pop("metric", None)
    return lifted


def _hierarchy_level_to_item(
    level: dict,
    name_key: str,
    parent: Optional[dict] = None,
    hierarchy: Optional[dict] = None,
) -> dict:
    """Convert a hierarchy level into a dimension or measure dict.

    Carries ``hierarchyName``, ``levelName``, ``columnName``, formula,
    and the whole ``aiContext`` object through for table/column selection.
    """
    item = {k: v for k, v in level.items() if k != "hierarchies"}
    if parent:
        item = _inherit_parent_display_fields(item, parent)
    if hierarchy:
        hierarchy_name = hierarchy.get("hierarchyName") or hierarchy.get("hierarchy_name")
        if hierarchy_name and not item.get("hierarchyName"):
            item["hierarchyName"] = hierarchy_name
        if not item.get("columnName") and hierarchy.get("columnName"):
            item["columnName"] = hierarchy.get("columnName")
        if not item.get("tableId") and hierarchy.get("tableId"):
            item["tableId"] = hierarchy.get("tableId")
    if not item.get(name_key):
        level_name = item.get("levelName") or item.get("dimensionName") or item.get("measureName")
        if level_name:
            item[name_key] = level_name
    return apply_ai_context(_lift_formula_to_top_level(item))


def _expand_hierarchy_items(items: list, name_key: str) -> List[dict]:
    """Flatten hierarchy levels into the dimension/measure list.

    Levels are added first. A parent container is omitted when one of its levels
    already uses the same display name (its column id is often a different
    hierarchy id). Existing non-hierarchy items are preserved unchanged
    (aside from aiContext normalization). Each level keeps hierarchyName,
    levelName, columnName, formula, and aiContext for selection prompts.
    """
    expanded: List[dict] = []
    seen_items: Set[tuple] = set()

    def _append(item: dict) -> None:
        name = item.get(name_key)
        col = item.get("columnName") or item.get("column_name") or ""
        tid = item.get("tableId") or item.get("table_id") or ""
        cid = item.get("columnId") or item.get("column_id") or ""
        key = (str(name), str(tid), str(col), str(cid))
        if key in seen_items:
            return
        seen_items.add(key)
        expanded.append(item)

    for item in items or []:
        if not isinstance(item, dict):
            continue

        parent_fields = {k: v for k, v in item.items() if k != "hierarchies"}
        hierarchies = item.get("hierarchies") or []
        level_names: Set[str] = set()
        for hierarchy in hierarchies:
            if not isinstance(hierarchy, dict):
                continue
            for level in hierarchy.get("levels") or []:
                if not isinstance(level, dict):
                    continue
                level_item = _hierarchy_level_to_item(
                    level, name_key, parent_fields, hierarchy=hierarchy
                )
                _append(level_item)
                level_name = level_item.get(name_key)
                if level_name not in (None, ""):
                    level_names.add(str(level_name))

        parent = apply_ai_context(_lift_formula_to_top_level(parent_fields))
        # The container often reuses a level's display name with a different
        # column id (a hierarchy primary id). The level already represents it.
        parent_name = parent.get(name_key)
        if parent_name not in (None, "") and str(parent_name) in level_names:
            continue
        _append(parent)

    return expanded


def expand_hierarchies_in_cube_info(cube_info: list) -> list:
    """Return cube_info with hierarchy levels promoted into dimensions/measures.

    Call this before conversion so the rest of the pipeline can keep treating
    dimensions and measures as flat lists.
    """
    expanded_cubes: List[dict] = []
    for cube in cube_info or []:
        if not isinstance(cube, dict):
            continue
        entry = dict(cube)
        entry["dimensions"] = _expand_hierarchy_items(
            cube.get("dimensions") or [],
            "dimensionName",
        )
        entry["measures"] = _expand_hierarchy_items(
            cube.get("measures") or [],
            "measureName",
        )
        expanded_cubes.append(entry)
    return expanded_cubes


def normalize_cube_info(cube_info: list) -> list:
    """Expand hierarchies and apply aiContext before cube_info processing."""
    return expand_hierarchies_in_cube_info(cube_info or [])
