"""WHERE → formData.filters[] (getFilters / wire-filters)."""

from __future__ import annotations

from typing import Any

from ..functions_catalog import to_wire_database_function_expression
from ..mappings.conditions import CONDITION_WIRE_MAP
from ..mappings.types import infer_data_type
from ..metadata import (
    resolve_host_column_from_used,
    resolve_wire_column,
    to_raw_database_function,
    used_column_fq_names,
)
from ..models import FilterItem, ParsedQuery


def build_filters(parsed: ParsedQuery, metadata: dict | None = None) -> list[dict]:
    meta = metadata or {}
    out: list[dict] = []
    idx = 0
    for item in parsed.where_filters:
        if item.aggregate:
            continue
        if item.is_all:
            continue
        wire = _to_wire_filter(item, parsed, meta, idx)
        out.append(wire)
        idx += 1
    return out


def _to_wire_filter(item: FilterItem, parsed: ParsedQuery, meta: dict, idx: int) -> dict:
    col_short = item.column.short if item.column else ""
    col_name = item.column.name if item.column else (item.alias or "custom")

    label = _label_for(item, parsed, meta, col_name)
    if item.column:
        column_ref = resolve_wire_column(
            item.column.table,
            item.column.name,
            meta,
            fallback_name=col_short or item.custom_sql or "",
        )
    else:
        column_ref = item.custom_sql or col_name

    wire: dict[str, Any] = {
        "column": column_ref,
        "label": label,
        "alias": label,
        "operator": item.operator or "AND",
        "id": idx,
        "mode": "auto",
        "condition": item.ui_condition or "CUSTOM",
    }
    _apply_database_function_or_custom(wire, item, meta)

    if item.ui_condition == "CUSTOM":
        return _as_custom_filter(wire, item, list(item.values or []))

    return _apply_condition_transform(wire, item)


def _apply_database_function_or_custom(wire: dict[str, Any], item, meta: dict) -> dict[str, Any]:
    """
    Catalog/signature match → ``databaseFunction`` string; keep column ref.
    Match miss / render fail:
      usedColumns present → RAW(complete expression) on one host column
      usedColumns empty  → ``custom: true`` with the original expression as ``column``

    Match::
        "databaseFunction": "MONTH(\"travel_details\".\"travel_date\")"

    Fallback (usedColumns present)::
        "column": {"name": "….travel_date", "id": "2859"},
        "databaseFunction": "RAW(TOTALLY_UNKNOWN_FN(travel_details.travel_date))",
        "usedColumns": ["….travel_date"]
    """
    used = used_column_fq_names(item.used_columns, meta, fallback=item.column)

    if item.database_function:
        wire_dbf = to_wire_database_function_expression(
            item.database_function,
            metadata=meta,
            dialect=meta.get("dialect"),
        )
        if wire_dbf:
            wire["databaseFunction"] = wire_dbf
            if used:
                wire["usedColumns"] = used
            return wire

    # Fallback: original SQL expression (parser-kept), not the alias rendering.
    expr = (item.database_function_sql or item.custom_sql or "").strip()
    if not expr:
        return wire
    if used:
        host = resolve_host_column_from_used(used, meta, column=item.column)
        if host:
            wire["column"] = host
        wire["databaseFunction"] = to_raw_database_function(expr)
        wire.pop("custom", None)
        wire["usedColumns"] = used
        return wire
    wire["column"] = expr
    wire["custom"] = True
    wire.pop("databaseFunction", None)
    wire["usedColumns"] = used
    return wire


# Back-compat alias for having_parts / callers
_apply_expression_as_custom = _apply_database_function_or_custom


def _label_for(item: FilterItem, parsed: ParsedQuery, meta: dict, fallback: str) -> str:
    if item.column:
        for sel in parsed.selects:
            if not sel.alias or not sel.column:
                continue
            if sel.column.name.lower() != item.column.name.lower():
                continue
            if (
                sel.column.table
                and item.column.table
                and sel.column.table.lower() != item.column.table.lower()
            ):
                continue
            return sel.alias
        by_column = meta.get("by_column") or {}
        hit = by_column.get(item.column.short) or by_column.get(item.column.name)
        if isinstance(hit, dict) and hit.get("alias"):
            return str(hit["alias"])
    if item.alias:
        return item.alias
    return fallback


def _apply_condition_transform(wire: dict, item: FilterItem) -> dict:
    ui = item.ui_condition
    values = list(item.values)

    if ui == "EQUALS":
        wire["values"] = values
        return wire

    if ui == "NOT_EQUALS":
        wire["customCondition"] = "<>"
        wire["isCustomValue"] = True
        wire["values"] = values
        return wire

    if ui == "IS_ONE_OF":
        wire["customCondition"] = " IN ("
        wire["isCustomValue"] = True
        wire["encloseInQuotes"] = False
        wire["values"] = values
        return wire

    if ui == "IS_NOT_ONE_OF":
        wire["customCondition"] = " NOT IN ("
        wire["isCustomValue"] = True
        wire["encloseInQuotes"] = False
        wire["values"] = values
        return wire

    if ui in ("CONTAINS", "DOES_NOT_CONTAINS", "STARTS_WITH", "ENDS_WITH",
              "DOES_NOT_STARTS_WITH", "DOES_NOT_ENDS_WITH"):
        mapping = CONDITION_WIRE_MAP[ui]
        wire["customCondition"] = mapping["customCondition"]
        wire["encloseInQuotes"] = False
        wire["values"] = values
        return wire

    if ui in ("IS_LESS_THAN", "IS_GREATER_THAN", "IS_LESS_THAN_OR_EQUAL_TO", "IS_GREATER_THAN_OR_EQUAL_TO"):
        mapping = CONDITION_WIRE_MAP[ui]
        wire["customCondition"] = mapping["customCondition"]
        wire["isCustomValue"] = True
        wire["values"] = values
        return wire

    if ui in ("IS_BETWEEN", "IS_NOT_BETWEEN"):
        wire["customCondition"] = "NOT BETWEEN" if ui == "IS_NOT_BETWEEN" else "BETWEEN"
        wire["isCustomValue"] = True
        wire["values"] = values
        return wire

    if ui in ("IN_RANGE", "NOT_IN_RANGE"):
        wire["encloseInQuotes"] = False
        wire["isCustomValue"] = True
        wire["values"] = values
        return wire

    if ui == "IS_NULL":
        wire["customCondition"] = "IS NULL"
        wire["encloseInQuotes"] = False
        return wire

    if ui == "IS_NOT_NULL":
        wire["customCondition"] = "IS NOT NULL"
        wire["encloseInQuotes"] = False
        return wire

    return _as_custom_filter(wire, item, values)


def _as_custom_filter(wire: dict, item, values: list[Any]) -> dict:
    """Unmatched UI condition or complex SQL → condition CUSTOM, values = full payload."""
    wire["condition"] = "CUSTOM"
    wire["customCondition"] = item.custom_sql or item.raw_sql or "CUSTOM"
    wire["isCustomValue"] = True
    wire["mode"] = "custom"
    wire["values"] = _custom_payload(item, values)
    return wire


def _custom_payload(item, values: list[Any]) -> list[Any]:
    """Keep original SQL pieces (no LIKE-strip / IN-paren split)."""
    if values:
        return list(values)
    raw = item.custom_sql or item.raw_sql
    return [raw] if raw else []


def _fq(parsed: ParsedQuery, short: str) -> str:
    return short


def _type_for(col_short: str, col_name: str, meta: dict, has_aggregate: bool) -> dict:
    by_column = meta.get("by_column") or {}
    hit = by_column.get(col_short) or by_column.get(col_name) or {}
    if isinstance(hit, dict) and hit.get("type"):
        return hit["type"]
    return infer_data_type(col_name, has_aggregate=has_aggregate)
