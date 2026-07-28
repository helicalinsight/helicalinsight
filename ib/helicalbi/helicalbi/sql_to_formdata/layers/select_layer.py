"""SELECT → formData.columns[] (prepareColumns / wire-column.json)."""

from __future__ import annotations

from ..mappings.types import infer_data_type
from ..models import ParsedQuery, SelectItem


def build_columns(parsed: ParsedQuery, metadata: dict | None = None) -> list[dict]:
    meta = metadata or {}
    columns = []
    for item in parsed.selects:
        columns.append(_to_wire_column(item, parsed, meta))
    return columns


def _to_wire_column(item: SelectItem, parsed: ParsedQuery, meta: dict) -> dict:
    col_meta = _lookup_meta(item, meta)
    aggregates = item.aggregates or ([item.aggregate] if item.aggregate else [])
    has_agg = bool(aggregates)
    field_type = col_meta.get("type") or infer_data_type(
        item.column.name if item.column else item.alias,
        has_aggregate=has_agg,
    )
    data_type = field_type.get("dataType", "text")
    if item.database_function and item.database_function.get("returns"):
        # Prefer catalog return type when a DB function is applied
        returns = str(item.database_function["returns"]).lower()
        if returns in ("numeric", "number", "integer", "int", "float", "double"):
            data_type = "numeric"
            field_type = {
                "backendDataType": field_type.get("backendDataType") or "java.lang.Integer",
                "dataType": "numeric",
            }
        elif returns in ("text", "string", "varchar"):
            data_type = "text"
            field_type = {
                "backendDataType": field_type.get("backendDataType") or "java.lang.String",
                "dataType": "text",
            }
    is_measure = has_agg or data_type == "numeric"

    if item.is_custom and item.custom_expression:
        # Custom column (unknown DB function / complex expr) — raw SQL as column
        column_path = item.custom_expression
    elif item.column:
        column_path = _fq_column(parsed, item.column.short)
    else:
        column_path = item.raw_sql or item.alias

    wire = {
        "column": column_path,
        "alias": item.alias,
        "floatingType": "" if is_measure else "discrete",
        "fieldType": "measure" if is_measure else "dimension",
        "fieldDataType": field_type,
        "addedAs": "column",
    }

    if item.is_custom:
        wire["custom"] = True

    if aggregates:
        wire["aggregate"] = True
        wire["aggregateList"] = aggregates

    if item.database_function:
        wire["databaseFunction"] = item.database_function

    if item.functions_definition:
        wire["functionsDefinition"] = item.functions_definition

    if col_meta.get("hidden"):
        wire["hidden"] = True
    if col_meta.get("hiddenIncludeInResultSet"):
        wire["hidden"] = True
        wire["includeInResultset"] = True

    return wire


def _fq_column(parsed: ParsedQuery, short: str) -> str:
    """database.table.column — matches formdata-builder fq()."""
    db = parsed.database_name or ""
    if not db:
        return short
    if short.startswith(db + "."):
        return short
    return f"{db}.{short}"


def _lookup_meta(item: SelectItem, meta: dict) -> dict:
    by_alias = meta.get("by_alias") or {}
    by_column = meta.get("by_column") or {}
    if item.alias in by_alias:
        return by_alias[item.alias]
    if item.column and item.column.short in by_column:
        return by_column[item.column.short]
    if item.column and item.column.name in by_column:
        return by_column[item.column.name]
    return {}
