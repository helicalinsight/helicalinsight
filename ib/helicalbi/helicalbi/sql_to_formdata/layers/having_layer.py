"""HAVING → formData.having[] (wire-having.json)."""

from __future__ import annotations

from ..models import ParsedQuery
from .filters_layer import _apply_condition_transform, _fq, _type_for


def build_having(parsed: ParsedQuery, metadata: dict | None = None) -> list[dict]:
    meta = metadata or {}
    out: list[dict] = []
    idx = 0

    # Explicit HAVING clause
    for item in parsed.having_filters:
        out.append(_to_having(item, parsed, meta, idx))
        idx += 1

    # Aggregated WHERE predicates (defensive)
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
    column_path = _fq(parsed, col_short) if col_short else ""

    wire = {
        "column": column_path,
        "label": label,
        "alias": label,
        "operator": item.operator or "AND",
        "dataType": backend,
        "id": idx,
        "mode": "auto",
    }
    if item.aggregate:
        wire["function"] = item.aggregate
    if item.database_function:
        wire["databaseFunction"] = item.database_function

    return _apply_condition_transform(wire, item, data_type)
