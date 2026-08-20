"""WHERE → formData.filters[] (getFilters / wire-filters)."""

from __future__ import annotations

from typing import Any

from ..functions_catalog import to_wire_database_function
from ..mappings.conditions import CONDITION_WIRE_MAP
from ..mappings.types import infer_data_type
from ..metadata import resolve_wire_column
from ..models import FilterItem, ParsedQuery


DATE_TYPES = {"date", "dateTime", "time"}


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
    type_info = _type_for(col_short, col_name, meta, has_aggregate=bool(item.aggregate))
    data_type = type_info["dataType"]
    backend = type_info["backendDataType"]

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
        "dataType": backend,
        "id": idx,
        "mode": "auto",
        "condition": item.ui_condition or "CUSTOM",
    }

    if item.database_function:
        wire_dbf = to_wire_database_function(item.database_function)
        if wire_dbf:
            wire["databaseFunction"] = wire_dbf

    if item.ui_condition == "CUSTOM":
        return _as_custom_filter(wire, item, list(item.values or []))

    return _apply_condition_transform(wire, item, data_type)


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


def _apply_condition_transform(wire: dict, item: FilterItem, data_type: str) -> dict:
    ui = item.ui_condition
    values = list(item.values)
    is_date = data_type in DATE_TYPES

    if ui == "EQUALS":
        wire["be_condition"] = "EQUALS"
        return _set_be_value(wire, values, values)

    if ui == "NOT_EQUALS":
        wire["be_condition"] = "CUSTOM"
        wire["customCondition"] = "<>"
        wire["isCustomValue"] = True
        return _set_be_value(wire, values, values)

    if ui == "IS_ONE_OF":
        wire["be_condition"] = "CUSTOM"
        wire["customCondition"] = " IN ("
        wire["isCustomValue"] = True
        wire["encloseInQuotes"] = False
        return _set_be_value(wire, [_format_in_list(values, data_type)], values)

    if ui == "IS_NOT_ONE_OF":
        wire["be_condition"] = "CUSTOM"
        wire["customCondition"] = " NOT IN ("
        wire["isCustomValue"] = True
        wire["encloseInQuotes"] = False
        return _set_be_value(wire, [_format_in_list(values, data_type)], values)

    if ui in ("CONTAINS", "DOES_NOT_CONTAINS", "STARTS_WITH", "ENDS_WITH",
              "DOES_NOT_STARTS_WITH", "DOES_NOT_ENDS_WITH"):
        mapping = CONDITION_WIRE_MAP[ui]
        wire["be_condition"] = "CUSTOM"
        wire["customCondition"] = mapping["customCondition"]
        v = str(values[0]) if values else ""
        pattern = mapping.get("values_pattern", "'%value%'").replace("value", v)
        wire["encloseInQuotes"] = False
        return _set_be_value(wire, [pattern], values)

    if ui in ("IS_LESS_THAN", "IS_GREATER_THAN", "IS_LESS_THAN_OR_EQUAL_TO", "IS_GREATER_THAN_OR_EQUAL_TO"):
        mapping = CONDITION_WIRE_MAP[ui]
        wire["be_condition"] = "CUSTOM"
        wire["customCondition"] = mapping["customCondition"]
        wire["isCustomValue"] = True
        return _set_be_value(wire, [str(v) for v in values], values)

    if ui in ("IS_BETWEEN", "IS_NOT_BETWEEN"):
        wire["be_condition"] = "CUSTOM"
        wire["customCondition"] = "NOT BETWEEN" if ui == "IS_NOT_BETWEEN" else "BETWEEN"
        low = values[0] if values else ""
        high = values[1] if len(values) > 1 else low
        if is_date:
            be_value = [f"'{low}' AND '{high}'"]
        else:
            be_value = [f"{low} AND {high}"]
        wire["isCustomValue"] = True
        return _set_be_value(wire, be_value, values)

    if ui in ("IN_RANGE", "NOT_IN_RANGE"):
        wire["be_condition"] = ui
        be_value = [float(v) if _is_number(v) else v for v in values]
        wire["encloseInQuotes"] = False
        wire["isCustomValue"] = True
        return _set_be_value(wire, be_value, values)

    if ui == "IS_NULL":
        wire["be_condition"] = "CUSTOM"
        wire["customCondition"] = "IS NULL"
        wire["encloseInQuotes"] = False
        return wire

    if ui == "IS_NOT_NULL":
        wire["be_condition"] = "CUSTOM"
        wire["customCondition"] = "IS NOT NULL"
        wire["encloseInQuotes"] = False
        return wire

    return _as_custom_filter(wire, item, values)


def _as_custom_filter(wire: dict, item, values: list[Any]) -> dict:
    """Unmatched UI condition or complex SQL → condition CUSTOM, values = full payload."""
    wire["condition"] = "CUSTOM"
    wire["be_condition"] = "CUSTOM"
    wire["customCondition"] = item.custom_sql or item.raw_sql or "CUSTOM"
    wire["isCustomValue"] = True
    wire["mode"] = "custom"
    payload = _custom_payload(item, values)
    return _set_be_value(wire, payload, payload)


def _custom_payload(item, values: list[Any]) -> list[Any]:
    """Keep original SQL pieces (no LIKE-strip / IN-paren split)."""
    if values:
        return list(values)
    raw = item.custom_sql or item.raw_sql
    return [raw] if raw else []


def _set_be_value(wire: dict, be_value: list[Any], sql_values: list[Any] | None) -> dict:
    """be_value = Adhoc wire string; values = literals from SQL (no % / IN paren).

    For CUSTOM / complex filters, both keys hold the same original payload.
    """
    wire["be_value"] = be_value
    wire["values"] = list(sql_values or [])
    return wire


def _format_in_list(values: list[Any], data_type: str) -> str:
    if data_type in ("numeric",):
        inner = ",".join(str(v) for v in values)
    else:
        inner = ",".join(f"'{v}'" for v in values)
    return f"{inner})"


def _is_number(v: Any) -> bool:
    try:
        float(v)
        return True
    except (TypeError, ValueError):
        return False


def _fq(parsed: ParsedQuery, short: str) -> str:
    return short


def _type_for(col_short: str, col_name: str, meta: dict, has_aggregate: bool) -> dict:
    by_column = meta.get("by_column") or {}
    hit = by_column.get(col_short) or by_column.get(col_name) or {}
    if isinstance(hit, dict) and hit.get("type"):
        return hit["type"]
    return infer_data_type(col_name, has_aggregate=has_aggregate)
