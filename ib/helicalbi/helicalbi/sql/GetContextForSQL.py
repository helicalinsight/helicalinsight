import json
from typing import Any

from helicalbi.common.JsonToPara import (
    generate_bare_minimum_context,
    generate_semantic_hint,
    is_bare_minimum_config,
    iter_cube_entries,
)


def get_tables_and_columns_by_topics(topics: list, topic_table: dict):
    required_tables = []
    for topic in topics:
        required_tables.append(topic_table.get(topic, ""))
    return required_tables


def get_table_col_description(
    cube_metadata,
    table_names=None,
    user_query=None,
    agent_data=None,
):
    cube_metadata = cube_metadata or []
    table_names = table_names or []
    all_cubes = list(iter_cube_entries(cube_metadata))
    bare_minimum = is_bare_minimum_config(agent_data) if agent_data else False

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
        return generate_bare_minimum_context(user_query, reduced_cubes)

    return generate_semantic_hint(reduced_cubes)


def _normalize_query_plan(query_plan: Any) -> dict:
    if isinstance(query_plan, dict):
        return query_plan
    if isinstance(query_plan, str) and query_plan.strip():
        try:
            parsed = json.loads(query_plan)
        except json.JSONDecodeError:
            return {}
        return parsed if isinstance(parsed, dict) else {}
    return {}


def _lookup_column_meta(cube: dict, col_name: str) -> dict:
    """Return column or measure metadata for a bare column name."""
    for column in cube.get("columns") or []:
        if isinstance(column, dict) and column.get("column_name") == col_name:
            return column
    for measure in cube.get("measures") or []:
        if isinstance(measure, dict) and measure.get("column_name") == col_name:
            return measure
    return {}


def _format_column_detail(meta: dict) -> str:
    parts: list[str] = []
    description = meta.get("description")
    if description:
        parts.append(str(description))
    semantic_type = meta.get("semantic_type")
    if semantic_type:
        parts.append(f"type: {semantic_type}")
    aggregator = meta.get("aggregator")
    if aggregator:
        parts.append(f"aggregator: {aggregator}")
    default_function = meta.get("default_function")
    if default_function:
        parts.append(f"function: {default_function}")
    format_string = meta.get("format_string")
    if format_string:
        parts.append(f"format: {format_string}")
    metric_obj = meta.get("metric") if isinstance(meta.get("metric"), dict) else {}
    formula = meta.get("formula") or metric_obj.get("formula")
    if formula:
        if meta.get("is_computed"):
            parts.append(
                "COMPUTED measure - this is NOT a physical column; implement the "
                "following formula as a SQL expression and alias it with the "
                f"measure name: {formula}"
            )
        else:
            parts.append(f"formula: {formula}")
    return "; ".join(parts)


def get_required_column_description(cube_metadata, query_plan) -> str:
    """Build column descriptions for columns selected in the query plan."""
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

        if "." in ref:
            table_name, col_name = ref.rsplit(".", 1)
        else:
            table_name, col_name = "", ref

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

        if "." in ref:
            table_name, col_name = ref.rsplit(".", 1)
        else:
            table_name, col_name = "", ref

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
