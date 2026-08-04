import json
import logging
from typing import Any

from helicalbi.common.JsonToPara import (
    generate_bare_minimum_context,
    generate_compact_table_catalog,
    generate_semantic_hint,
    is_bare_minimum_config,
    iter_cube_entries,
    split_table_column_ref,
    unquote_identifier,
)
from helicalbi.common.CubeInfoModel import (
    format_topic_mappings_compact,
    format_topic_mappings_for_prompt,
)

logger = logging.getLogger(__name__)


def get_tables_and_columns_by_topics(topics: list, topic_table: dict):
    required_tables = []
    for topic in topics:
        required_tables.append(topic_table.get(topic, ""))
    return required_tables


def get_table_col_description(
    cube_metadata,
    table_names=None,
    user_query=None,
    model_data=None,
    domain_context=None,
):
    cube_metadata = cube_metadata or []
    table_names = table_names or []
    all_cubes = list(iter_cube_entries(cube_metadata))
    bare_minimum = is_bare_minimum_config(model_data) if model_data else False

    if table_names:
        reduced_cubes = [
            cube for cube in all_cubes
            if cube.get("database_table") in table_names
        ]
    else:
        reduced_cubes = all_cubes

    if not reduced_cubes:
        reduced_cubes = all_cubes

    if bare_minimum or not table_names:
        schema = generate_bare_minimum_context(user_query, reduced_cubes)
    else:
        schema = generate_semantic_hint(reduced_cubes)

    model_data = model_data or {}
    domain_block = (
        domain_context
        or model_data.get("domain_context")
        or ""
    )
    topic_mappings = model_data.get("topic_mappings") or []
    topic_block = format_topic_mappings_for_prompt(topic_mappings)
    parts: list[str] = []
    if domain_block and topic_block and topic_block in domain_block:
        parts.append(str(domain_block).strip())
    else:
        if domain_block:
            parts.append(str(domain_block).strip())
        if topic_block:
            parts.append(topic_block)
    if schema:
        parts.append(schema)
    return "\n\n".join(part for part in parts if part)


def get_table_selection_description(
    cube_metadata,
    model_data=None,
    topics=None,
) -> str:
    """Compact schema for FindTables — table/column names + slim topic hints only.

    Omits per-column descriptions, formulas, aiContext, and full domain prose so
    local models (e.g. Ollama) stay within context / memory limits.
    """
    catalog = generate_compact_table_catalog(cube_metadata)
    model_data = model_data or {}
    topic_mappings = model_data.get("topic_mappings") or []
    if topics:
        wanted = {str(t).strip().lower() for t in topics if t}
        if wanted:
            topic_mappings = [
                entry
                for entry in topic_mappings
                if str(entry.get("topic_name") or "").strip().lower() in wanted
            ] or topic_mappings
    topic_block = format_topic_mappings_compact(topic_mappings)
    parts = [part for part in (topic_block, catalog) if part]
    return "\n\n".join(parts)


def _normalize_query_plan(query_plan: Any) -> dict:
    if isinstance(query_plan, dict):
        return query_plan
    if isinstance(query_plan, str) and query_plan.strip():
        try:
            parsed = json.loads(query_plan)
        except json.JSONDecodeError:
            logger.error(
                "Invalid query_plan JSON in GetContextForSQL; using empty plan",
                exc_info=True,
            )
            return {}
        return parsed if isinstance(parsed, dict) else {}
    return {}


def _lookup_column_meta(cube: dict, col_name: str) -> dict:
    """Return column or measure metadata for a bare column name or alias."""
    target = unquote_identifier(col_name)
    if not target:
        return {}
    # Prefer exact alias / semantic-name matches (hierarchy levels share column_name).
    for column in cube.get("columns") or []:
        if not isinstance(column, dict):
            continue
        if column.get("alias_name") == target or column.get("dimension_name") == target:
            return column
        if column.get("level_name") == target:
            return column
    for measure in cube.get("measures") or []:
        if not isinstance(measure, dict):
            continue
        if (
            measure.get("alias_name") == target
            or measure.get("measure_name") == target
        ):
            return measure
    for column in cube.get("columns") or []:
        if not isinstance(column, dict):
            continue
        if column.get("column_name") == target:
            return column
    for measure in cube.get("measures") or []:
        if not isinstance(measure, dict):
            continue
        if measure.get("column_name") == target:
            return measure
    return {}


def _lookup_all_column_meta(cube: dict, col_name: str) -> list[dict]:
    """Return all column/measure metadata entries matching a physical name or alias."""
    target = unquote_identifier(col_name)
    if not target:
        return []
    matches: list[dict] = []
    seen: set[tuple] = set()

    def _append(item: dict) -> None:
        key = (
            item.get("alias_name") or item.get("measure_name") or item.get("column_name"),
            item.get("hierarchy_name"),
            item.get("level_name"),
            item.get("is_computed"),
            item.get("formula"),
        )
        if key in seen:
            return
        seen.add(key)
        matches.append(item)

    for column in cube.get("columns") or []:
        if not isinstance(column, dict):
            continue
        if (
            column.get("column_name") == target
            or column.get("alias_name") == target
            or column.get("dimension_name") == target
            or column.get("level_name") == target
        ):
            _append(column)
    for measure in cube.get("measures") or []:
        if not isinstance(measure, dict):
            continue
        if (
            measure.get("column_name") == target
            or measure.get("alias_name") == target
            or measure.get("measure_name") == target
        ):
            _append(measure)
    return matches


def _item_kind(meta: dict) -> str:
    if meta.get("is_computed") or (
        meta.get("measure_name") and not meta.get("column_name")
    ):
        return "computed_measure"
    if meta.get("hierarchy_name") or meta.get("level_name"):
        return "hierarchy"
    if meta.get("measure_name") or meta.get("aggregator"):
        return "measure"
    return "dimension"


def _strip_format_string(meta: dict) -> dict:
    """Return a copy without formatString — formatting belongs to the viz flow."""
    cleaned = {
        key: value
        for key, value in (meta or {}).items()
        if key not in ("format_string", "formatString", "format")
    }
    return cleaned


def _sql_sort_label(meta: dict) -> str:
    """Return ASC/DESC for SQL hints; omit none/empty and all measures."""
    # Measures (physical column or formula) are never ORDER BY candidates.
    kind = _item_kind(meta)
    if kind in ("measure", "computed_measure") or meta.get("measure_name"):
        return ""
    direction = meta.get("sort_direction")
    if direction in ("ASC", "DESC"):
        return str(direction)
    raw = meta.get("sort_order")
    if raw in (None, ""):
        return ""
    from helicalbi.common.CubeInfoModel import (
        _NO_SORT_STRINGS,
        sort_direction_from_value,
    )

    if isinstance(raw, str) and raw.strip().lower() in _NO_SORT_STRINGS:
        return ""

    resolved = sort_direction_from_value(raw)
    return resolved if resolved in ("ASC", "DESC") else ""


def _format_column_detail(meta: dict) -> str:
    """Format picked column/measure metadata for final SQL (no formatString)."""
    parts: list[str] = []
    kind = _item_kind(meta)
    parts.append(f"kind: {kind}")
    dimension_name = meta.get("dimension_name")
    if dimension_name:
        parts.append(f"dimension: {dimension_name}")
    measure_name = meta.get("measure_name")
    if measure_name:
        parts.append(f"measure: {measure_name}")
    hierarchy_name = meta.get("hierarchy_name")
    if hierarchy_name:
        parts.append(f"hierarchy: {hierarchy_name}")
    level_name = meta.get("level_name")
    if level_name:
        parts.append(f"level: {level_name}")
    description = meta.get("description")
    if description:
        parts.append(str(description))
    ai_instructions = meta.get("ai_instructions")
    if ai_instructions and (
        not description or str(ai_instructions) not in str(description)
    ):
        parts.append(f"AI instructions (SQL/viz): {ai_instructions}")
    ai_examples = meta.get("ai_examples")
    if ai_examples and (not description or str(ai_examples) not in str(description)):
        parts.append(f"examples: {ai_examples}")
    ai_context = meta.get("ai_context")
    if isinstance(ai_context, dict) and ai_context:
        ai_parts = []
        for key in ("instructions", "synonyms", "examples"):
            value = ai_context.get(key)
            if value in (None, ""):
                continue
            if isinstance(value, list):
                text = ", ".join(str(item) for item in value if item)
            else:
                text = str(value).strip()
            if text:
                ai_parts.append(f"{key}: {text}")
        # Preserve any extra aiContext keys.
        for key, value in ai_context.items():
            if key in ("instructions", "synonyms", "examples") or value in (None, ""):
                continue
            ai_parts.append(f"{key}: {value}")
        if ai_parts:
            parts.append(f"aiContext: {{{'; '.join(ai_parts)}}}")
    semantic_type = meta.get("semantic_type")
    if semantic_type:
        parts.append(f"type: {semantic_type}")
    aggregator = meta.get("aggregator")
    if aggregator:
        parts.append(f"aggregator: {aggregator}")
    default_function = meta.get("default_function")
    if default_function:
        parts.append(f"function: {default_function}")
    # formatString is intentionally omitted — applied only in visualization flow.
    sort_label = _sql_sort_label(meta)
    if sort_label:
        raw = meta.get("sort_order")
        if raw not in (None, "") and str(raw).upper() != sort_label:
            parts.append(f"sort: {sort_label} (sort={raw})")
        else:
            parts.append(f"sort: {sort_label}")
    metric_obj = meta.get("metric") if isinstance(meta.get("metric"), dict) else {}
    formula = meta.get("formula") or metric_obj.get("formula")
    if formula:
        if meta.get("is_computed") or kind == "computed_measure":
            parts.append(
                "COMPUTED measure - this is NOT a physical column; implement the "
                "following formula as a SQL expression and alias it with the "
                f"measure name: {formula}"
            )
        else:
            parts.append(f"formula: {formula}")
    synonyms = meta.get("synonyms") or []
    if synonyms:
        parts.append(
            "synonyms: " + ", ".join(str(item) for item in synonyms if item)
        )
    return "; ".join(parts)


def collect_picked_column_items(cube_metadata, query_plan) -> dict:
    """Collect full cube metadata items for picked columns, grouped by table.

    Includes dimensions, measures, hierarchy levels, and blank-column computed
    measures. ``formatString`` is stripped — it belongs to the viz flow only.
    """
    plan = _normalize_query_plan(query_plan)
    column_refs = plan.get("columnName") or []
    picked_dimensions = plan.get("pickedDimensions") or plan.get("picked_dimensions") or []
    picked_metrics = plan.get("pickedMetrics") or plan.get("picked_metrics") or []

    by_table: dict[str, dict] = {}

    def _bucket_for(meta: dict) -> str:
        kind = _item_kind(meta)
        if kind == "hierarchy":
            return "hierarchies"
        if kind == "computed_measure":
            return "computed_measures"
        if kind == "measure":
            return "measures"
        return "dimensions"

    def _add(table_name: str, meta: dict) -> None:
        if not table_name or not meta:
            return
        cleaned = _strip_format_string(dict(meta))
        cleaned["kind"] = _item_kind(cleaned)
        cleaned["table"] = table_name
        entry = by_table.setdefault(
            table_name,
            {
                "database_table": table_name,
                "dimensions": [],
                "hierarchies": [],
                "measures": [],
                "computed_measures": [],
            },
        )
        bucket = _bucket_for(cleaned)
        dedupe_key = (
            cleaned.get("alias_name")
            or cleaned.get("measure_name")
            or cleaned.get("dimension_name")
            or cleaned.get("column_name"),
            cleaned.get("hierarchy_name"),
            cleaned.get("level_name"),
            cleaned.get("formula"),
        )
        existing_keys = {
            (
                item.get("alias_name")
                or item.get("measure_name")
                or item.get("dimension_name")
                or item.get("column_name"),
                item.get("hierarchy_name"),
                item.get("level_name"),
                item.get("formula"),
            )
            for item in entry[bucket]
        }
        if dedupe_key in existing_keys:
            return
        entry[bucket].append(cleaned)

    cubes = list(iter_cube_entries(cube_metadata or []))
    cube_by_table = {
        cube.get("database_table"): cube
        for cube in cubes
        if cube.get("database_table")
    }

    for ref in column_refs:
        table_name, col_name = split_table_column_ref(ref)
        if not col_name:
            continue
        if table_name and table_name in cube_by_table:
            for meta in _lookup_all_column_meta(cube_by_table[table_name], col_name):
                _add(table_name, meta)
            continue
        for cube in cubes:
            matches = _lookup_all_column_meta(cube, col_name)
            if not matches:
                continue
            resolved_table = cube.get("database_table") or table_name or ""
            for meta in matches:
                _add(resolved_table, meta)
            break

    semantic_targets = [
        unquote_identifier(str(name))
        for name in list(picked_dimensions) + list(picked_metrics)
        if name
    ]
    for target in semantic_targets:
        for cube in cubes:
            table_name = cube.get("database_table") or ""
            for meta in _lookup_all_column_meta(cube, target):
                _add(table_name, meta)

    # Drop empty buckets for cleaner prompt payloads.
    result: dict[str, dict] = {}
    for table_name, entry in by_table.items():
        trimmed = {
            "database_table": table_name,
        }
        for key in ("dimensions", "hierarchies", "measures", "computed_measures"):
            if entry.get(key):
                trimmed[key] = entry[key]
        result[table_name] = trimmed
    return result


def format_picked_columns_for_sql(picked_by_table: dict) -> str:
    """Render full picked cube items (no formatString) for the final SQL prompt."""
    if not picked_by_table:
        return ""
    lines = [
        "Picked cube items arranged by table "
        "(full dimension / hierarchy / measure / blank-column measure objects; "
        "display formatting is applied later in visualization only):"
    ]
    for table_name, entry in picked_by_table.items():
        lines.append(f"Table: {table_name}")
        for bucket in ("dimensions", "hierarchies", "measures", "computed_measures"):
            items = entry.get(bucket) or []
            if not items:
                continue
            lines.append(f"  {bucket}:")
            for item in items:
                detail = _format_column_detail(item)
                alias = (
                    item.get("alias_name")
                    or item.get("measure_name")
                    or item.get("dimension_name")
                    or item.get("column_name")
                    or ""
                )
                col_name = item.get("column_name") or alias
                prefix = f"{table_name}.{col_name}"
                if alias and alias != col_name:
                    prefix = f"{prefix} (alias: {alias})"
                lines.append(f"  - {prefix}: {detail}" if detail else f"  - {prefix}")
                # Also emit the whole item so hierarchy/aiContext fields are intact.
                payload = {
                    key: value
                    for key, value in item.items()
                    if key not in ("format_string", "formatString", "format")
                    and value not in (None, "", [], {})
                }
                lines.append(f"    item: {json.dumps(payload, default=str)}")
    return "\n".join(lines)


def get_required_column_description(cube_metadata, query_plan) -> str:
    """Build column descriptions for columns selected in the query plan.

    Uses the full picked dimension/measure/hierarchy/computed-measure items
    (arranged by table). formatString is not included.
    """
    picked = collect_picked_column_items(cube_metadata, query_plan)
    if picked:
        return format_picked_columns_for_sql(picked)

    # Fallback for legacy metadata without rich cube items.
    plan = _normalize_query_plan(query_plan)
    column_refs = plan.get("columnName") or []
    if not column_refs:
        return ""

    cube_by_table = {
        cube.get("database_table"): cube
        for cube in iter_cube_entries(cube_metadata or [])
        if cube.get("database_table")
    }

    lines: list[str] = []
    seen: set[str] = set()
    for ref in column_refs:
        ref = str(ref).strip()
        if not ref or ref in seen:
            continue
        seen.add(ref)

        table_name, col_name = split_table_column_ref(ref)
        if not col_name:
            continue

        col_desc = ""
        table_desc = ""
        col_alias = ""
        table_alias = ""
        col_meta: dict = {}
        cube = cube_by_table.get(table_name) if table_name else None
        if cube:
            table_desc = cube.get("description", "") or ""
            table_alias = cube.get("table_alias") or ""
            col_meta = _lookup_column_meta(cube, col_name)
            col_desc = _format_column_detail(col_meta)
            col_alias = col_meta.get("alias_name") or ""
        elif table_name:
            for cube_entry in iter_cube_entries(cube_metadata or []):
                col_meta = _lookup_column_meta(cube_entry, col_name)
                if col_meta:
                    col_desc = _format_column_detail(col_meta)
                    col_alias = col_meta.get("alias_name") or ""
                    table_desc = cube_entry.get("description", "") or ""
                    table_alias = cube_entry.get("table_alias") or ""
                    break
        else:
            for cube_entry in iter_cube_entries(cube_metadata or []):
                col_meta = _lookup_column_meta(cube_entry, col_name)
                if col_meta:
                    col_desc = _format_column_detail(col_meta)
                    col_alias = col_meta.get("alias_name") or ""
                    table_desc = cube_entry.get("description", "") or ""
                    table_alias = cube_entry.get("table_alias") or ""
                    table_name = cube_entry.get("database_table", "") or ""
                    break

        if table_name:
            line_ref = f"{table_name}.{col_name}"
            if col_alias and col_alias != col_name:
                line_ref = f"{line_ref} (alias: {col_alias})"
            if table_alias and table_alias != table_name:
                line_ref = f"{line_ref} [table alias: {table_alias}]"
            if col_desc:
                lines.append(f"- {line_ref}: {col_desc}")
            elif table_desc:
                lines.append(f"- {line_ref} (table: {table_desc})")
            else:
                lines.append(f"- {line_ref}")
        elif col_desc:
            lines.append(f"- {col_name}: {col_desc}")
        else:
            lines.append(f"- {col_name}")

    return "\n".join(lines)


def get_required_functions(cube_metadata, query_plan) -> str:
    """Build default-function hints for columns selected in the query plan."""
    plan = _normalize_query_plan(query_plan)
    column_refs = plan.get("columnName") or []
    if not column_refs:
        return ""

    cube_by_table = {
        cube.get("database_table"): cube
        for cube in iter_cube_entries(cube_metadata or [])
        if cube.get("database_table")
    }

    lines: list[str] = []
    seen: set[str] = set()
    for ref in column_refs:
        ref = str(ref).strip()
        if not ref or ref in seen:
            continue
        seen.add(ref)

        table_name, col_name = split_table_column_ref(ref)
        if not col_name:
            continue

        cube = cube_by_table.get(table_name) if table_name else None
        if not cube:
            for cube_entry in iter_cube_entries(cube_metadata or []):
                if _lookup_column_meta(cube_entry, col_name):
                    cube = cube_entry
                    if not table_name:
                        table_name = cube_entry.get("database_table", "")
                    break

        if not cube:
            continue

        meta = _lookup_column_meta(cube, col_name)
        default_function = meta.get("default_function")
        if default_function:
            line_ref = f"{table_name}.{col_name}" if table_name else col_name
            lines.append(f"- {line_ref}: {default_function}")

    return "\n".join(lines)
