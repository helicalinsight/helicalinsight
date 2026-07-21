"""Detection and conversion helpers for cube_info model semantic layers."""

from __future__ import annotations

import re
from typing import Any, Dict, List, Optional, Set, Tuple

from helicalbi.common.JsonToPara import (
    enrich_cube_metadata_with_aliases,
    resolve_column_alias,
    resolve_table_alias,
)


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


def _hierarchy_level_to_item(level: dict, name_key: str, parent: Optional[dict] = None) -> dict:
    """Convert a hierarchy level into a dimension or measure dict."""
    item = {k: v for k, v in level.items() if k != "hierarchies"}
    if parent:
        item = _inherit_parent_display_fields(item, parent)
    if not item.get(name_key):
        level_name = item.get("levelName") or item.get("dimensionName") or item.get("measureName")
        if level_name:
            item[name_key] = level_name
    return apply_ai_context(item)


def _expand_hierarchy_items(items: list, name_key: str) -> List[dict]:
    """Flatten hierarchy levels into the dimension/measure list.

    Levels are added first so they take precedence when names collide with the
    parent container. Existing non-hierarchy items are preserved unchanged
    (aside from aiContext normalization).
    """
    expanded: List[dict] = []
    seen_names: Set[str] = set()

    def _append(item: dict) -> None:
        name = item.get(name_key)
        if name:
            key = str(name)
            if key in seen_names:
                return
            seen_names.add(key)
        expanded.append(item)

    for item in items or []:
        if not isinstance(item, dict):
            continue

        parent_fields = {k: v for k, v in item.items() if k != "hierarchies"}
        hierarchies = item.get("hierarchies") or []
        for hierarchy in hierarchies:
            if not isinstance(hierarchy, dict):
                continue
            for level in hierarchy.get("levels") or []:
                if not isinstance(level, dict):
                    continue
                _append(_hierarchy_level_to_item(level, name_key, parent_fields))

        parent = apply_ai_context(parent_fields)
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


def build_column_to_table_map(metadata_response: dict) -> Dict[str, str]:
    """Map physical column names to table names from the metadata API response."""
    column_to_table: Dict[str, str] = {}
    tables = (metadata_response or {}).get("tables") or {}

    for table_name, table in tables.items():
        if not table_name or not isinstance(table, dict):
            continue
        for col_name in (table.get("columns") or {}):
            if col_name:
                column_to_table[str(col_name)] = table_name

    return column_to_table


def build_metadata_id_maps(
    metadata_response: dict,
) -> Tuple[Dict[str, str], Dict[str, Tuple[str, str]]]:
    """Map table/column IDs from the metadata API to physical names."""
    table_id_to_name: Dict[str, str] = {}
    column_id_to_ref: Dict[str, Tuple[str, str]] = {}
    tables = (metadata_response or {}).get("tables") or {}

    for table_name, table in tables.items():
        if not table_name or not isinstance(table, dict):
            continue
        table_id = table.get("id")
        if table_id:
            table_id_to_name[str(table_id)] = table_name
        for col_name, col_meta in (table.get("columns") or {}).items():
            if not col_name:
                continue
            col_id = col_meta.get("id") if isinstance(col_meta, dict) else None
            if col_id:
                column_id_to_ref[str(col_id)] = (table_name, str(col_name))

    return table_id_to_name, column_id_to_ref


def _parse_column_reference(column_name: str) -> Tuple[Optional[str], str]:
    """Split ``table.column`` references; bare column names return ``(None, column)``."""
    column_name = str(column_name).strip()
    if "." in column_name:
        table_name, col_name = column_name.rsplit(".", 1)
        table_name = table_name.strip()
        col_name = col_name.strip()
        if table_name and col_name:
            return table_name, col_name
    return None, column_name


def _default_function_from_cube_item(item: dict, metadata_response: Optional[dict] = None) -> str:
    for key in ("defaultFunction", "default_function"):
        value = item.get(key)
        if value:
            return str(value)

    table_name = item.get("_resolved_table")
    column_name = item.get("_resolved_column")
    if table_name and column_name and metadata_response:
        table = ((metadata_response or {}).get("tables") or {}).get(table_name) or {}
        col_meta = ((table.get("columns") or {}).get(column_name) or {}) if isinstance(table, dict) else {}
        if isinstance(col_meta, dict):
            for key in ("defaultFunction", "default_function"):
                value = col_meta.get(key)
                if value:
                    return str(value)
    return ""


def _resolve_table_name(
    item: dict,
    column_to_table: Dict[str, str],
    table_id_to_name: Optional[Dict[str, str]] = None,
    column_id_to_ref: Optional[Dict[str, Tuple[str, str]]] = None,
) -> Optional[str]:
    column_name = item.get("columnName")
    if column_name:
        table_name, bare_column = _parse_column_reference(str(column_name))
        if table_name:
            return table_name
        resolved = column_to_table.get(bare_column)
        if resolved:
            return resolved

    table_id = item.get("tableId")
    if table_id and table_id_to_name:
        resolved = table_id_to_name.get(str(table_id))
        if resolved:
            return resolved

    column_id = item.get("columnId")
    if column_id and column_id_to_ref:
        ref = column_id_to_ref.get(str(column_id))
        if ref:
            return ref[0]

    return None


def _resolve_column_name(
    item: dict,
    column_id_to_ref: Optional[Dict[str, Tuple[str, str]]] = None,
) -> Optional[str]:
    column_name = item.get("columnName")
    if column_name:
        _, bare_column = _parse_column_reference(str(column_name))
        return bare_column

    column_id = item.get("columnId")
    if column_id and column_id_to_ref:
        ref = column_id_to_ref.get(str(column_id))
        if ref:
            return ref[1]

    return None


_FORMULA_TABLE_REF = re.compile(r"([A-Za-z_][A-Za-z0-9_]*)\s*\.\s*[A-Za-z_][A-Za-z0-9_]*")


def _known_table_names(metadata_response: Optional[dict]) -> Set[str]:
    """Collect physical table names available in the metadata API response."""
    return {
        str(name)
        for name in ((metadata_response or {}).get("tables") or {})
        if name
    }


def _formula_from_cube_item(item: dict) -> str:
    metric_obj = item.get("metric") if isinstance(item.get("metric"), dict) else {}
    return str(item.get("formula") or metric_obj.get("formula") or "")


def _table_from_formula(formula: str, known_tables: Set[str]) -> Optional[str]:
    """Infer a table name from qualified ``table.column`` refs inside a formula."""
    matches = _FORMULA_TABLE_REF.findall(formula or "")
    for token in matches:
        if token in known_tables:
            return token
    return matches[0] if matches else None


def cube_info_to_cube_metadata(
    cube_info: list,
    metadata_response: dict,
    domain_topics: Optional[List[str]] = None,
) -> List[dict]:
    """Convert cube_info entries into legacy cube_metadata for the SQL pipeline."""
    cube_info = normalize_cube_info(cube_info)
    column_to_table = build_column_to_table_map(metadata_response)
    table_id_to_name, column_id_to_ref = build_metadata_id_maps(metadata_response)
    known_tables = _known_table_names(metadata_response)
    tables: Dict[str, dict] = {}
    domain_topics = domain_topics or []

    for cube in cube_info or []:
        if not isinstance(cube, dict):
            continue

        cube_name = cube.get("cubeName") or ""
        dimension_names = [
            str(dim.get("dimensionName"))
            for dim in (cube.get("dimensions") or [])
            if isinstance(dim, dict) and dim.get("dimensionName")
        ]
        measure_names = [
            str(measure.get("measureName"))
            for measure in (cube.get("measures") or [])
            if isinstance(measure, dict) and measure.get("measureName")
        ]
        component_names = list(dict.fromkeys(dimension_names + measure_names))

        for dim in cube.get("dimensions") or []:
            if not isinstance(dim, dict):
                continue
            table_name = _resolve_table_name(
                dim, column_to_table, table_id_to_name, column_id_to_ref
            )
            column_name = _resolve_column_name(dim, column_id_to_ref)
            if not table_name or not column_name:
                continue

            entry = tables.setdefault(
                table_name,
                {
                    "database_table": table_name,
                    "table_alias": resolve_table_alias(table_name, metadata_response),
                    "dimension_name": component_names or domain_topics,
                    "description": cube_name,
                    "columns": [],
                    "measures": [],
                },
            )
            dim["_resolved_table"] = table_name
            dim["_resolved_column"] = column_name
            preferred_alias = dim.get("dimensionName")
            column_entry = {
                "column_name": column_name,
                # Prefer dimension name; fall back to metadata alias when absent.
                "alias_name": str(preferred_alias)
                if preferred_alias
                else resolve_column_alias(table_name, column_name, metadata_response),
                "description": dim.get("description") or dim.get("dimensionName") or "",
                "semantic_type": dim.get("semanticType"),
                "synonyms": dim.get("synonyms") or [],
                "default_function": _default_function_from_cube_item(dim, metadata_response) or None,
                "format_string": _format_string_from_cube_item(dim) or None,
                "sort_order": _sort_order_raw_from_cube_item(dim),
                "sort_direction": sort_direction_from_value(
                    _sort_order_raw_from_cube_item(dim)
                ),
                "metric": dim.get("metric") or {},
            }
            if dim.get("ai_instructions"):
                column_entry["ai_instructions"] = dim.get("ai_instructions")
            if dim.get("ai_examples"):
                column_entry["ai_examples"] = dim.get("ai_examples")
            entry["columns"].append(column_entry)

        for measure in cube.get("measures") or []:
            if not isinstance(measure, dict):
                continue
            table_name = _resolve_table_name(
                measure, column_to_table, table_id_to_name, column_id_to_ref
            )
            column_name = _resolve_column_name(measure, column_id_to_ref)

            # Formula-only measures leave columnName/columnId blank. Expose the
            # measure name itself as a selectable computed column so the SQL
            # pipeline can pick it up (heading = measureName, carrying formula).
            computed = False
            if not column_name:
                formula = _formula_from_cube_item(measure)
                measure_label = measure.get("measureName")
                if not formula or not measure_label:
                    continue
                if not table_name:
                    table_name = _table_from_formula(formula, known_tables)
                if not table_name:
                    continue
                column_name = str(measure_label)
                computed = True

            if not table_name or not column_name:
                continue

            entry = tables.setdefault(
                table_name,
                {
                    "database_table": table_name,
                    "table_alias": resolve_table_alias(table_name, metadata_response),
                    "dimension_name": component_names or domain_topics,
                    "description": cube_name,
                    "columns": [],
                    "measures": [],
                },
            )
            measure["_resolved_table"] = table_name
            if not computed:
                measure["_resolved_column"] = column_name
            preferred_alias = measure.get("measureName")
            measure_entry = {
                "column_name": column_name,
                # Prefer measure name; fall back to metadata alias when absent.
                "alias_name": str(preferred_alias)
                if preferred_alias
                else resolve_column_alias(table_name, column_name, metadata_response),
                "aggregator": _aggregator_from_cube_item(measure),
                "description": measure.get("description") or measure.get("measureName") or "",
                "semantic_type": measure.get("semanticType"),
                "format_string": _format_string_from_cube_item(measure) or None,
                "sort_order": _sort_order_raw_from_cube_item(measure),
                "sort_direction": sort_direction_from_value(
                    _sort_order_raw_from_cube_item(measure)
                ),
                "default_function": _default_function_from_cube_item(measure, metadata_response) or None,
                "metric": measure.get("metric") or {},
                "synonyms": measure.get("synonyms") or [],
            }
            if measure.get("ai_instructions"):
                measure_entry["ai_instructions"] = measure.get("ai_instructions")
            if measure.get("ai_examples"):
                measure_entry["ai_examples"] = measure.get("ai_examples")
            if computed:
                # Not a physical column; carries a formula the LLM must expand.
                measure_entry["is_computed"] = True
                measure_entry["formula"] = _formula_from_cube_item(measure)
            entry["measures"].append(measure_entry)

    return list(tables.values())


def _aggregator_from_cube_item(item: dict) -> str:
    metric_obj = item.get("metric") if isinstance(item.get("metric"), dict) else {}
    return str(item.get("aggregator") or metric_obj.get("aggregator") or "")


def _format_string_from_cube_item(item: dict) -> str:
    metric_obj = item.get("metric") if isinstance(item.get("metric"), dict) else {}
    for source in (item, metric_obj):
        for key in ("formatString", "format_string", "format"):
            value = source.get(key)
            if value:
                return str(value)
    return ""


def _sort_order_raw_from_cube_item(item: dict) -> Any:
    """Return the raw sortOrder / sort_order / sort value when present.

    Cube payloads may use numeric ``sortOrder`` (0=ASC, 1=DESC or priority)
    or an explicit ``sort`` direction string such as ``Ascending`` / ``Descending``.
    """
    for key in ("sortOrder", "sort_order", "sort"):
        if key in item and item.get(key) not in (None, ""):
            return item.get(key)
    metric_obj = item.get("metric") if isinstance(item.get("metric"), dict) else {}
    for key in ("sortOrder", "sort_order", "sort"):
        if key in metric_obj and metric_obj.get(key) not in (None, ""):
            return metric_obj.get(key)
    return None


def sort_direction_from_value(value: Any) -> Optional[str]:
    """Map cube sortOrder values to SQL directions.

    ``0`` / ``"asc"`` → ASC, ``1`` / ``"desc"`` → DESC. Other integers default
    to ASC so sequential sort priorities (0, 1, 2, ...) still produce valid
    ORDER BY clauses. Explicit direction strings are preserved.
    """
    if value is None or value == "":
        return None
    if isinstance(value, str):
        lowered = value.strip().lower()
        if not lowered:
            return None
        if lowered in ("0", "asc", "ascending"):
            return "ASC"
        if lowered in ("1", "desc", "descending"):
            return "DESC"
        try:
            value = int(lowered)
        except ValueError:
            return lowered.upper()
    try:
        num = int(value)
    except (TypeError, ValueError):
        return None
    if num == 1:
        return "DESC"
    return "ASC"


def _sort_priority_from_value(value: Any) -> int:
    """Numeric priority for ORDER BY sequencing (lower first)."""
    if value is None or value == "":
        return 10**9
    if isinstance(value, str):
        lowered = value.strip().lower()
        if lowered in ("asc", "ascending"):
            return 0
        if lowered in ("desc", "descending"):
            return 1
        try:
            return int(lowered)
        except ValueError:
            return 10**9
    try:
        return int(value)
    except (TypeError, ValueError):
        return 10**9


def _cube_item_display_names(item: dict, primary_name_key: str) -> List[str]:
    """Names to key format/sort maps: dimension/measure/level/column/metric."""
    names: List[str] = []
    for key in (
        primary_name_key,
        "levelName",
        "dimensionName",
        "measureName",
        "columnName",
        "metricId",
    ):
        value = item.get(key)
        if value not in (None, ""):
            names.append(str(value))
    metric_obj = item.get("metric") if isinstance(item.get("metric"), dict) else {}
    for key in ("metric", "name", "id"):
        value = metric_obj.get(key)
        if value not in (None, ""):
            names.append(str(value))
    # Prefer bare column leaf when columnName is table.column.
    column_name = item.get("columnName")
    if column_name and "." in str(column_name):
        _, leaf = _parse_column_reference(str(column_name))
        if leaf:
            names.append(leaf)
    return list(dict.fromkeys(names))


def format_strings_from_cube_info(cube_info: list) -> Dict[str, str]:
    """Map dimension/measure/column/metric names to formatString values."""
    cube_info = normalize_cube_info(cube_info)
    mapping: Dict[str, str] = {}
    for cube in cube_info or []:
        if not isinstance(cube, dict):
            continue
        for dim in cube.get("dimensions") or []:
            if not isinstance(dim, dict):
                continue
            fmt = _format_string_from_cube_item(dim)
            if not fmt:
                continue
            for name in _cube_item_display_names(dim, "dimensionName"):
                mapping.setdefault(name, fmt)
        for measure in cube.get("measures") or []:
            if not isinstance(measure, dict):
                continue
            fmt = _format_string_from_cube_item(measure)
            if not fmt:
                continue
            for name in _cube_item_display_names(measure, "measureName"):
                mapping.setdefault(name, fmt)
    return mapping


def sort_orders_from_cube_info(cube_info: list) -> List[dict]:
    """Collect sortOrder entries for final SQL ORDER BY hints.

    Each entry: ``{name, direction, priority, raw}`` sorted by priority.
    Direction mapping: ``0``→ASC, ``1``→DESC (or explicit asc/desc strings).
    """
    cube_info = normalize_cube_info(cube_info)
    entries: List[dict] = []
    seen: Set[str] = set()

    def _append(item: dict, primary_name_key: str) -> None:
        raw = _sort_order_raw_from_cube_item(item)
        if raw is None:
            return
        direction = sort_direction_from_value(raw)
        if not direction:
            return
        names = _cube_item_display_names(item, primary_name_key)
        primary = next(
            (n for n in names if n == str(item.get(primary_name_key) or "")),
            names[0] if names else "",
        )
        if not primary or primary in seen:
            return
        seen.add(primary)
        entries.append(
            {
                "name": primary,
                "direction": direction,
                "priority": _sort_priority_from_value(raw),
                "raw": raw,
            }
        )

    for cube in cube_info or []:
        if not isinstance(cube, dict):
            continue
        for dim in cube.get("dimensions") or []:
            if isinstance(dim, dict):
                _append(dim, "dimensionName")
        for measure in cube.get("measures") or []:
            if isinstance(measure, dict):
                _append(measure, "measureName")

    entries.sort(key=lambda e: (e["priority"], e["name"]))
    return entries


def format_sort_orders_for_prompt(sort_orders: List[dict]) -> str:
    """Render sort-order hints for the final SQL prompt."""
    if not sort_orders:
        return ""
    lines = [
        "Use these column sort directions in ORDER BY "
        "(0/Ascending=ASC, 1/Descending=DESC; follow the listed priority order):"
    ]
    for entry in sort_orders:
        lines.append(
            f"- {entry['name']}: {entry['direction']} "
            f"(sort={entry.get('raw')})"
        )
    return "\n".join(lines)


def format_format_strings_for_prompt(format_strings: Dict[str, str]) -> str:
    """Render format-string hints for visualization prompts."""
    if not format_strings:
        return ""
    lines = [
        "Apply these Excel-style format strings on matching chart axes / labels / tooltips "
        "(match by column, metric, dimension, or measure name):"
    ]
    for name, fmt in format_strings.items():
        lines.append(f"- {name}: {fmt}")
    return "\n".join(lines)


def _metric_name_from_cube_item(item: dict, primary_name_key: str) -> str:
    metric_obj = item.get("metric")
    if isinstance(metric_obj, dict):
        for key in ("metric", "name", "id"):
            value = metric_obj.get(key)
            if value:
                return str(value)
    primary = item.get(primary_name_key)
    if primary:
        return str(primary)
    column_name = item.get("columnName")
    if column_name:
        return str(column_name)
    return ""


def _description_from_cube_item(item: dict, primary_name_key: str) -> str:
    metric_obj = item.get("metric") if isinstance(item.get("metric"), dict) else {}
    description = (
        item.get("description")
        or metric_obj.get("description")
        or item.get(primary_name_key)
        or ""
    )
    aggregator = _aggregator_from_cube_item(item)
    if aggregator and description:
        return f"{description} (aggregation: {aggregator})"
    if aggregator:
        return f"{_metric_name_from_cube_item(item, primary_name_key)} (aggregation: {aggregator})"
    return str(description)


def _cube_item_has_formula_or_filter(item: dict) -> bool:
    metric_obj = item.get("metric") if isinstance(item.get("metric"), dict) else {}
    for field in ("formula", "filter"):
        if item.get(field) or metric_obj.get(field):
            return True
    return False


def _business_metric_from_cube_item(
    item: dict,
    table_name: str,
    primary_name_key: str,
    label_key: str,
    metadata_response: Optional[dict] = None,
    column_id_to_ref: Optional[Dict[str, Tuple[str, str]]] = None,
) -> Optional[dict]:
    metric_name = _metric_name_from_cube_item(item, primary_name_key)
    if not table_name or not metric_name:
        return None

    entry: dict[str, Any] = {
        "metric": metric_name,
        "description": _description_from_cube_item(item, primary_name_key),
        "tables": [table_name],
    }
    if metadata_response:
        entry["table_alias"] = resolve_table_alias(table_name, metadata_response)
    column_name = _resolve_column_name(item, column_id_to_ref)
    if column_name:
        entry["column_name"] = column_name
        preferred_alias = item.get(primary_name_key)
        if preferred_alias:
            entry["column_alias"] = str(preferred_alias)
        elif metadata_response:
            entry["column_alias"] = resolve_column_alias(
                table_name,
                column_name,
                metadata_response,
            )
    if item.get(primary_name_key):
        entry[label_key] = item[primary_name_key]
    aggregator = _aggregator_from_cube_item(item)
    if aggregator:
        entry["aggregator"] = aggregator
    format_string = _format_string_from_cube_item(item)
    if format_string:
        entry["format_string"] = format_string
    sort_order = _sort_order_raw_from_cube_item(item)
    if sort_order is not None:
        entry["sort_order"] = sort_order
        entry["sort_direction"] = sort_direction_from_value(sort_order)
    default_function = _default_function_from_cube_item(item, metadata_response)
    if default_function:
        entry["default_function"] = default_function

    metric_obj = item.get("metric") if isinstance(item.get("metric"), dict) else {}
    for field in ("formula", "filter"):
        value = item.get(field) or metric_obj.get(field)
        if value:
            entry[field] = value
    return entry


def _append_business_metric(
    metrics: List[dict],
    seen: set[tuple[str, str]],
    entry: Optional[dict],
) -> None:
    if not entry:
        return
    dedupe_key = (entry["metric"], entry["tables"][0])
    if dedupe_key in seen:
        return
    seen.add(dedupe_key)
    metrics.append(entry)


def business_metrics_from_cube_info(cube_info: list, metadata_response: dict) -> List[dict]:
    """Build legacy business_metrics entries from cube_info measures and dimension formulas."""
    cube_info = normalize_cube_info(cube_info)
    column_to_table = build_column_to_table_map(metadata_response)
    table_id_to_name, column_id_to_ref = build_metadata_id_maps(metadata_response)
    known_tables = _known_table_names(metadata_response)
    metrics: List[dict] = []
    seen: set[tuple[str, str]] = set()

    for cube in cube_info or []:
        if not isinstance(cube, dict):
            continue

        for measure in cube.get("measures") or []:
            if not isinstance(measure, dict):
                continue
            table_name = _resolve_table_name(
                measure, column_to_table, table_id_to_name, column_id_to_ref
            )
            if not table_name:
                # Formula-only measure: infer the table from the formula so the
                # metric (and its formula) still reaches the SQL prompt.
                table_name = _table_from_formula(
                    _formula_from_cube_item(measure), known_tables
                )
            entry = _business_metric_from_cube_item(
                measure,
                table_name or "",
                "measureName",
                "measure_name",
                metadata_response,
                column_id_to_ref,
            )
            _append_business_metric(metrics, seen, entry)

        for dim in cube.get("dimensions") or []:
            if not isinstance(dim, dict) or not _cube_item_has_formula_or_filter(dim):
                continue
            table_name = _resolve_table_name(
                dim, column_to_table, table_id_to_name, column_id_to_ref
            )
            entry = _business_metric_from_cube_item(
                dim,
                table_name or "",
                "dimensionName",
                "dimension_name",
                metadata_response,
                column_id_to_ref,
            )
            _append_business_metric(metrics, seen, entry)

    return metrics


def merge_business_metrics(*sources: list) -> List[dict]:
    """Merge business metric lists while dropping exact duplicates."""
    merged: List[dict] = []
    seen: set[tuple[Any, tuple]] = set()

    for source in sources:
        for item in source or []:
            if not isinstance(item, dict):
                continue
            key = (item.get("metric"), tuple(item.get("tables") or []))
            if key in seen:
                continue
            seen.add(key)
            merged.append(item)

    return merged


def _component_alias(name: str) -> str:
    return " alias:-" + str(name)


def _cube_component_names(cube_info: list) -> List[str]:
    """Collect dimension and measure names used as topic-mapping components."""
    cube_info = normalize_cube_info(cube_info)
    components: List[str] = []
    for cube in cube_info or []:
        if not isinstance(cube, dict):
            continue
        for dim in cube.get("dimensions") or []:
            if isinstance(dim, dict) and dim.get("dimensionName"):
                components.append(_component_alias(dim["dimensionName"]))
        for measure in cube.get("measures") or []:
            if isinstance(measure, dict) and measure.get("measureName"):
                components.append(_component_alias(measure["measureName"]))
    return list(dict.fromkeys(components))


def topic_mappings_from_domain(model_data: dict) -> List[dict]:
    """Build topic_mappings from domain topic component lists when present."""
    mappings: List[dict] = []
    for entry in model_data.get("domain") or []:
        if not isinstance(entry, dict):
            continue
        for topic in entry.get("topics") or []:
            if not isinstance(topic, dict):
                continue
            topic_name = _topic_name(topic)
            if not topic_name:
                continue
            components: List[str] = []
            for component in topic.get("components") or []:
                if not isinstance(component, dict):
                    continue
                name = component.get("name")
                if name:
                    components.append(_component_alias(name))
            if components:
                mappings.append(
                    {
                        "topic_name": topic_name,
                        "component": list(dict.fromkeys(components)),
                    }
                )
    return mappings


def topic_mappings_from_cube_info(cube_info: list, topics: List[str]) -> List[dict]:
    """Build topic_mappings so topics resolve to cube dimensions and measures."""
    components = _cube_component_names(cube_info)
    if not topics or not components:
        return []
    return [
        {"topic_name": topic, "component": components}
        for topic in topics
        if topic
    ]


def synonyms_from_cube_info(cube_info: list, metadata_response: dict) -> List[dict]:
    """Build legacy synonym entries from cube_info dimension/measure synonyms."""
    cube_info = normalize_cube_info(cube_info)
    column_to_table = build_column_to_table_map(metadata_response)
    table_id_to_name, column_id_to_ref = build_metadata_id_maps(metadata_response)
    synonyms: List[dict] = []
    seen: set[tuple[str, str]] = set()

    def _append_synonyms(item: dict, name_key: str) -> None:
        item_synonyms = item.get("synonyms") or []
        if not item_synonyms:
            return
        table_name = _resolve_table_name(
            item, column_to_table, table_id_to_name, column_id_to_ref
        )
        if not table_name:
            return
        label = str(item.get(name_key) or "")
        dedupe_key = (table_name, label)
        if dedupe_key in seen:
            return
        seen.add(dedupe_key)
        synonyms.append(
            {
                "database_table": table_name,
                "dimension_name": label,
                "synonyms": [str(entry) for entry in item_synonyms if entry],
            }
        )

    for cube in cube_info or []:
        if not isinstance(cube, dict):
            continue
        for dim in cube.get("dimensions") or []:
            if isinstance(dim, dict):
                _append_synonyms(dim, "dimensionName")
        for measure in cube.get("measures") or []:
            if isinstance(measure, dict):
                _append_synonyms(measure, "measureName")
    return synonyms


def domain_context_from_model(model_data: dict) -> str:
    """Format domain descriptions for SQL and visualization prompts."""
    lines: List[str] = []
    for entry in model_data.get("domain") or []:
        if not isinstance(entry, dict):
            continue
        domain_name = entry.get("domain_name")
        description = entry.get("description")
        topics = entry.get("topics") or []
        if not domain_name and not description and not topics:
            continue
        parts = [f"Domain: {domain_name}"] if domain_name else []
        if description:
            parts.append(f"description: {description}")
        topic_labels: List[str] = []
        for topic in topics:
            if isinstance(topic, dict):
                name = _topic_name(topic)
                if not name:
                    continue
                topic_desc = str(topic.get("description") or "").strip()
                component_names = [
                    str(component.get("name"))
                    for component in (topic.get("components") or [])
                    if isinstance(component, dict) and component.get("name")
                ]
                label = name
                extras: List[str] = []
                if topic_desc:
                    extras.append(topic_desc)
                if component_names:
                    extras.append(f"components: {', '.join(component_names)}")
                if extras:
                    label = f"{name} ({'; '.join(extras)})"
                topic_labels.append(label)
            else:
                name = _topic_name(topic)
                if name:
                    topic_labels.append(name)
        if topic_labels:
            parts.append(f"topics: {', '.join(topic_labels)}")
        lines.append("; ".join(parts))
    return "\n".join(lines)


def prepare_cube_info_model_data(model_data: dict, metadata_response: dict) -> dict[str, Any]:
    """Return cube_metadata, domain/topics, and business_metrics for cube_info models."""
    _, topics = extract_domain_topics(model_data)
    # Flatten hierarchy levels into dimensions/measures before any conversion.
    cube_info = normalize_cube_info(model_data.get("cube_info") or model_data.get("cube") or [])
    cube_metadata = enrich_cube_metadata_with_aliases(
        cube_info_to_cube_metadata(
            cube_info,
            metadata_response,
            domain_topics=topics,
        ),
        metadata_response,
    )
    domains, topics = extract_domain_topics(model_data)
    derived_metrics = business_metrics_from_cube_info(cube_info, metadata_response)
    business_metrics = merge_business_metrics(model_data.get("business_metrics"), derived_metrics)
    topic_mappings = (
        model_data.get("topic_mappings")
        or topic_mappings_from_domain(model_data)
        or topic_mappings_from_cube_info(cube_info, topics)
    )
    derived_synonyms = synonyms_from_cube_info(cube_info, metadata_response)
    synonyms = list(model_data.get("synonyms") or [])
    existing_keys = {
        (entry.get("database_table"), entry.get("dimension_name"))
        for entry in synonyms
        if isinstance(entry, dict)
    }
    for entry in derived_synonyms:
        key = (entry.get("database_table"), entry.get("dimension_name"))
        if key not in existing_keys:
            synonyms.append(entry)
            existing_keys.add(key)
    format_strings = format_strings_from_cube_info(cube_info)
    sort_orders = sort_orders_from_cube_info(cube_info)
    return {
        "cube_metadata": cube_metadata,
        "domain": domains,
        "topics": topics,
        "business_metrics": business_metrics,
        "topic_mappings": topic_mappings,
        "synonyms": synonyms,
        "domain_context": domain_context_from_model(model_data),
        "format_strings": format_strings,
        "sort_orders": sort_orders,
        "column_sort_orders": format_sort_orders_for_prompt(sort_orders),
        "column_format_strings": format_format_strings_for_prompt(format_strings),
    }
