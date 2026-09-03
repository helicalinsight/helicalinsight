"""Split Adhoc wire formData + chat viz into convert-hreport parts.

Python must not emit Helical Report save JSON. The frontend builds that from
these compact SQL / viz parts, then opens Helical metadata from
``sql_parts.location`` + ``sql_parts.metadataFileName``.
"""

from __future__ import annotations

import re
from typing import Any, Optional


def form_data_to_sql_parts(form_data: dict[str, Any] | None) -> dict[str, Any]:
    """Extract columns / filters / orderBy from sql_to_formdata wire output."""
    payload = form_data if isinstance(form_data, dict) else {}
    wire_columns = list(payload.get("columns") or [])

    columns: list[dict[str, Any]] = []
    for col in wire_columns:
        if col.get("hidden"):
            continue
        table, column = _split_column_ref(col.get("column"), col.get("usedColumns"))
        if not column:
            continue
        entry = {
            "table": table,
            "column": column,
            "databaseFunction": _column_database_function(col),
            "shelf": "column" if col.get("aggregate") else "row",
            "alias": str(col.get("alias") or column),
        }
        if col.get("order"):
            entry["order"] = str(col.get("order") or "asc").lower()
        columns.append(entry)

    filters: list[dict[str, Any]] = []
    for item in payload.get("filters") or []:
        if not isinstance(item, dict):
            continue
        table, column = _split_column_ref(item.get("column"), item.get("usedColumns"))
        if not column:
            continue
        filters.append(
            {
                "table": table,
                "column": column,
                "databaseFunction": _database_function_key(item.get("databaseFunction")),
                "condition": str(item.get("condition") or ""),
                "value": item.get("values"),
            }
        )

    order_by: list[dict[str, Any]] = []
    for col in wire_columns:
        if not isinstance(col, dict) or not col.get("order"):
            continue
        table, column = _split_column_ref(col.get("column"), col.get("usedColumns"))
        alias = str(col.get("alias") or column or "")
        if not column and not alias:
            continue
        order_by.append(
            {
                "table": table,
                "column": column or alias,
                "databaseFunction": _column_database_function(col),
                "direction": str(col.get("order") or "asc").lower(),
                "alias": alias or column,
            }
        )

    return {
        "location": str(payload.get("location") or ""),
        "metadataFileName": str(payload.get("metadataFileName") or ""),
        "columns": columns,
        "filters": filters,
        "orderBy": order_by,
    }


def viz_model_to_viz_parts(
    viz: dict[str, Any] | None = None,
    *,
    viz_model: dict[str, Any] | None = None,
) -> dict[str, Any]:
    """Pass through InstantBI viz pieces the frontend maps onto HelicalReports."""
    viz = viz if isinstance(viz, dict) else {}
    model = viz_model if isinstance(viz_model, dict) else viz.get("viz_model")
    if not isinstance(model, dict):
        model = {}
    chart = model.get("chart") if isinstance(model.get("chart"), dict) else {}
    props = model.get("properties") if isinstance(model.get("properties"), dict) else {}
    color = props.get("color")
    color_field = ""
    color_value = ""
    if isinstance(color, str) and color.strip():
        if color.strip().startswith("#") or color.strip().lower().startswith("rgb"):
            color_value = color.strip()
        else:
            color_field = color.strip()

    title = props.get("title") or viz.get("vf_title") or ""
    return {
        "chart_name": str(viz.get("chart_name") or ""),
        "mark": str(chart.get("mark") or ""),
        "viz": str(chart.get("viz") or ""),
        "color": color_value,
        "background": str(props.get("background") or ""),
        "title": str(title or ""),
        "colorField": color_field,
    }


def _has_tables(metadata: dict[str, Any] | None) -> bool:
    tables = metadata.get("tables") if isinstance(metadata, dict) else None
    return isinstance(tables, dict) and bool(tables)


def unwrap_tables_metadata(metadata: dict[str, Any] | None) -> dict[str, Any] | None:
    """Return the Helical metadata object that contains ``tables``, if any."""
    if not isinstance(metadata, dict):
        return None
    if _has_tables(metadata):
        return metadata
    nested = metadata.get("data")
    if isinstance(nested, dict) and _has_tables(nested):
        return nested
    if isinstance(nested, dict):
        inner_meta = nested.get("metadata")
        found = unwrap_tables_metadata(inner_meta)
        if found:
            form_data = dict(found.get("formData") or {})
            source = inner_meta if isinstance(inner_meta, dict) else {}
            location = (
                source.get("location")
                or found.get("location")
                or form_data.get("location")
                or ""
            )
            metadata_file_name = (
                source.get("metadataFileName")
                or found.get("metadataFileName")
                or form_data.get("metadataFileName")
                or ""
            )
            if location:
                form_data["location"] = location
            if metadata_file_name:
                form_data["metadataFileName"] = metadata_file_name
            out = dict(found)
            if form_data:
                out["formData"] = form_data
            if location:
                out["location"] = location
            if metadata_file_name:
                out["metadataFileName"] = metadata_file_name
            return out
    response = metadata.get("response")
    if isinstance(response, dict):
        return unwrap_tables_metadata(response)
    return None


def hr_metadata_for_convert(
    metadata: dict[str, Any] | None,
    *,
    location: str = "",
    metadata_file_name: str = "",
) -> dict[str, Any] | None:
    """Shape metadata for HelicalReports ``add_row`` / ``add_column``."""
    payload = unwrap_tables_metadata(metadata)
    if not payload:
        return None
    out = dict(payload)
    out.pop("by_column", None)
    out.pop("by_alias", None)
    form_data = dict(out.get("formData") or {})
    if location:
        form_data["location"] = location
        out["location"] = location
        out["metadataDir"] = out.get("metadataDir") or location
    if metadata_file_name:
        form_data["metadataFileName"] = metadata_file_name
        out["metadataFileName"] = metadata_file_name
    if form_data:
        out["formData"] = form_data
    if not out.get("classifier"):
        out["classifier"] = "db.generic"
    return out


def build_convert_hreport_parts(
    form_data: dict[str, Any] | None,
    *,
    viz: dict[str, Any] | None = None,
    viz_model: dict[str, Any] | None = None,
    metadata: dict[str, Any] | None = None,
    location: str = "",
    metadata_file_name: str = "",
) -> dict[str, Any]:
    """UI contract for /instant-to-hr (convert-hreport).

    Do not embed Helical ``tables`` metadata. The UI opens it from
    ``sql_parts.location`` and ``sql_parts.metadataFileName``.
    ``metadata`` is accepted for call-site compatibility and ignored.
    """
    sql_parts = form_data_to_sql_parts(form_data)
    if location:
        sql_parts["location"] = location
    if metadata_file_name:
        sql_parts["metadataFileName"] = metadata_file_name
    return {
        "sql_parts": sql_parts,
        "viz_parts": viz_model_to_viz_parts(viz, viz_model=viz_model),
    }


def _split_column_ref(column: Any, used_columns: Any = None) -> tuple[str, str]:
    if isinstance(used_columns, list) and used_columns:
        first = used_columns[0]
        return _split_column_ref(first)
    if isinstance(column, dict):
        name = str(column.get("name") or "")
    else:
        name = str(column or "")
    name = name.strip().strip('"')
    if not name:
        return "", ""
    parts = [p for p in name.replace('"', "").split(".") if p]
    if not parts:
        return "", ""
    if len(parts) == 1:
        return "", parts[0]
    return parts[-2], parts[-1]


def _column_database_function(col: dict[str, Any]) -> str:
    aggregates = col.get("aggregateList")
    if isinstance(aggregates, list) and aggregates:
        first = aggregates[0]
        if isinstance(first, str) and first.strip():
            return first.strip()
    return _database_function_key(col.get("databaseFunction"))


def _database_function_key(value: Any) -> str:
    if isinstance(value, str):
        return value.strip()
    if isinstance(value, dict):
        return str(value.get("functionName") or value.get("key") or "").strip()
    return ""


def resolve_viz_from_sources(
    user_input: dict[str, Any],
    memory_node: Optional[dict[str, Any]] = None,
) -> dict[str, Any]:
    """Prefer request viz, then saved chat item, then chat-memory chat_response."""
    for source in (
        user_input.get("viz"),
        (user_input.get("chat_response_item") or {}).get("viz")
        if isinstance(user_input.get("chat_response_item"), dict)
        else None,
        ((memory_node or {}).get("chat_response") or {}).get("viz")
        if isinstance(memory_node, dict)
        else None,
    ):
        if isinstance(source, dict) and source:
            return source
    return {}


def _alias_key(value: Any) -> str:
    return re.sub(r"[^a-z0-9]", "", str(value or "").strip().lower())


def _find_column_by_alias(metadata: dict[str, Any] | None, name: Any) -> dict[str, str] | None:
    wanted = _alias_key(name)
    tables = (metadata or {}).get("tables") if isinstance(metadata, dict) else None
    if not wanted or not isinstance(tables, dict):
        return None
    for table_key, table in tables.items():
        columns = table.get("columns") if isinstance(table, dict) else None
        if not isinstance(columns, dict):
            continue
        for column_key, column in columns.items():
            alias = column.get("alias") if isinstance(column, dict) else ""
            col_name = column.get("name") if isinstance(column, dict) else ""
            if wanted in {_alias_key(column_key), _alias_key(alias), _alias_key(col_name)}:
                return {"table": str(table_key), "column": str(column_key)}
    return None


def _column_entry(metadata: dict[str, Any] | None, resolved: dict[str, str], *, shelf: str, name: Any = "") -> dict[str, Any]:
    table = resolved["table"]
    column = resolved["column"]
    meta_column = ((metadata or {}).get("tables") or {}).get(table, {}).get("columns", {}).get(column) or {}
    return {
        "table": table,
        "column": column,
        "shelf": shelf,
        "databaseFunction": str(meta_column.get("defaultFunction") or ""),
        "alias": str(name or meta_column.get("alias") or column),
    }


def sql_parts_from_viz_model(viz: dict[str, Any] | None, metadata: dict[str, Any] | None) -> dict[str, Any]:
    viz = viz if isinstance(viz, dict) else {}
    model = viz.get("viz_model") if isinstance(viz.get("viz_model"), dict) else {}
    data = model.get("data") if isinstance(model.get("data"), dict) else {}
    columns: list[dict[str, Any]] = []
    for name in data.get("rows") or []:
        resolved = _find_column_by_alias(metadata, name)
        if resolved:
            columns.append(_column_entry(metadata, resolved, shelf="row", name=name))
    for name in data.get("columns") or []:
        resolved = _find_column_by_alias(metadata, name)
        if resolved:
            columns.append(_column_entry(metadata, resolved, shelf="column", name=name))
    filters: list[dict[str, Any]] = []
    for item in data.get("filters") or []:
        if not isinstance(item, dict):
            continue
        resolved = _find_column_by_alias(metadata, item.get("name") or item.get("column"))
        if not resolved:
            continue
        filters.append(
            {
                **resolved,
                "condition": item.get("condition"),
                "value": item["value"] if "value" in item else item.get("values"),
            }
        )
    return {"columns": columns, "filters": filters}


_QUOTED_COL = re.compile(r'"([^"]+)"\s*\.\s*"([^"]+)"')
_AGG_BEFORE = {
    "count": "db.generic.aggregate.count",
    "sum": "db.generic.aggregate.sum",
    "avg": "db.generic.aggregate.avg",
    "min": "db.generic.aggregate.min",
    "max": "db.generic.aggregate.max",
}


def sql_parts_from_sql(sql: str, metadata: dict[str, Any] | None) -> dict[str, Any]:
    text = re.sub(r"```sql\s*", "", str(sql or ""), flags=re.I)
    text = text.replace("```", "")
    columns: list[dict[str, Any]] = []
    seen: set[str] = set()
    for match in _QUOTED_COL.finditer(text):
        key = f"{match.group(1)}.{match.group(2)}".lower()
        if key in seen:
            continue
        seen.add(key)
        table_name, column_name = match.group(1), match.group(2)
        tables = (metadata or {}).get("tables") if isinstance(metadata, dict) else {}
        table = table_name if table_name in (tables or {}) else next(
            (name for name in (tables or {}) if name.lower() == table_name.lower()),
            table_name,
        )
        table_meta = (tables or {}).get(table) or {}
        cols = table_meta.get("columns") if isinstance(table_meta, dict) else {}
        column = column_name if column_name in (cols or {}) else next(
            (name for name in (cols or {}) if name.lower() == column_name.lower()),
            column_name,
        )
        meta_column = (cols or {}).get(column)
        if not isinstance(meta_column, dict):
            continue
        before = text[max(0, match.start() - 48) : match.start()]
        agg_key = ""
        agg_match = re.search(r"\b(count|sum|avg|min|max)\s*\(\s*$", before, flags=re.I)
        if agg_match:
            agg_key = _AGG_BEFORE[agg_match.group(1).lower()]
        columns.append(
            {
                "table": table,
                "column": column,
                "shelf": "column" if agg_key else "row",
                "databaseFunction": agg_key or str(meta_column.get("defaultFunction") or ""),
                "alias": str(meta_column.get("alias") or column),
            }
        )
    return {"columns": columns, "filters": []}


def sql_parts_from_item(item: dict[str, Any] | None, metadata: dict[str, Any] | None) -> dict[str, Any]:
    item = item if isinstance(item, dict) else {}
    parts = item.get("sql_parts")
    if isinstance(parts, dict) and parts.get("columns"):
        return parts
    data_model = item.get("data_model") if isinstance(item.get("data_model"), dict) else {}
    if data_model.get("columns"):
        from_data = form_data_to_sql_parts(data_model)
        if from_data.get("columns"):
            return from_data
    viz = item.get("viz") if isinstance(item.get("viz"), dict) else {}
    if item.get("viz_model") and not viz.get("viz_model"):
        viz = {**viz, "viz_model": item.get("viz_model")}
    from_viz = sql_parts_from_viz_model(viz, metadata)
    if from_viz.get("columns"):
        return from_viz
    return sql_parts_from_sql(str(item.get("sql") or ""), metadata)
