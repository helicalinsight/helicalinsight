"""HAVING → formData.having[] (wire-having)."""

from __future__ import annotations

from ..functions_catalog import to_wire_database_function
from ..metadata import resolve_wire_column
from ..models import ParsedQuery
from .filters_layer import _apply_condition_transform, _type_for


def build_having(parsed: ParsedQuery, metadata: dict | None = None) -> list[dict]:
    meta = metadata or {}
    out: list[dict] = []
    idx = 0

    for item in parsed.having_filters:
        out.append(_to_having(item, parsed, meta, idx))
        idx += 1

    for item in parsed.where_filters:
        if item.aggregate:
            out.append(_to_having(item, parsed, meta, idx))
            idx += 1

    return out


def _to_having(item, parsed: ParsedQuery, meta: dict, idx: int) -> dict:
    col_short = item.column.short if item.column else ""
    col_name = item.column.name if item.column else (item.alias or "expr")
    type_info = _type_for(col_short, col_name, meta, has_aggregate=True)
    backend = type_info["backendDataType"]
    data_type = type_info["dataType"]

    label = item.alias or (f"sum_{col_name}" if item.aggregate else col_name)
    if item.column:
        column_ref = resolve_wire_column(
            item.column.table,
            item.column.name,
            meta,
            fallback_name=col_short,
        )
    else:
        column_ref = col_short or ""

    wire = {
        "column": column_ref,
        "label": label,
        "alias": label,
        "operator": item.operator or "AND",
        "dataType": backend,
        "id": idx,
        "mode": "auto",
        "condition": item.ui_condition or "CUSTOM",
    }
    if item.aggregate:
        wire["function"] = item.aggregate
    if item.database_function:
        wire_dbf = to_wire_database_function(item.database_function)
        if wire_dbf:
            wire["databaseFunction"] = wire_dbf

    return _apply_condition_transform(wire, item, data_type)
