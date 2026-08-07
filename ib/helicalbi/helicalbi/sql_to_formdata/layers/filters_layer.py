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

    label = _label_for(item, meta, col_name)
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
    }

    if item.database_function:
        wire_dbf = to_wire_database_function(item.database_function)
        if wire_dbf:
            wire["databaseFunction"] = wire_dbf

    if item.ui_condition == "CUSTOM" and item.custom_sql:
        wire["condition"] = "CUSTOM"
        wire["customCondition"] = item.custom_sql
        wire["mode"] = "custom"
        wire["isCustomValue"] = True
        wire["values"] = item.values
        return wire

    return _apply_condition_transform(wire, item, data_type)


def _label_for(item: FilterItem, meta: dict, fallback: str) -> str:
    by_column = meta.get("by_column") or {}
    if item.column:
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
        wire["condition"] = "EQUALS"
        wire["values"] = values
        return wire

    if ui == "NOT_EQUALS":
        wire["condition"] = "CUSTOM"
        wire["customCondition"] = "<>"
        wire["values"] = values
        wire["isCustomValue"] = True
        return wire

    if ui == "IS_ONE_OF":
        wire["condition"] = "CUSTOM"
        wire["customCondition"] = " IN ("
        wire["values"] = [_format_in_list(values, data_type)]
        wire["isCustomValue"] = True
        wire["encloseInQuotes"] = False
        return wire

    if ui == "IS_NOT_ONE_OF":
        wire["condition"] = "CUSTOM"
        wire["customCondition"] = " NOT IN ("
        wire["values"] = [_format_in_list(values, data_type)]
        wire["isCustomValue"] = True
        wire["encloseInQuotes"] = False
        return wire

    if ui in ("CONTAINS", "DOES_NOT_CONTAINS", "STARTS_WITH", "ENDS_WITH",
              "DOES_NOT_STARTS_WITH", "DOES_NOT_ENDS_WITH"):
        mapping = CONDITION_WIRE_MAP[ui]
        wire["condition"] = "CUSTOM"
        wire["customCondition"] = mapping["customCondition"]
        v = str(values[0]) if values else ""
        pattern = mapping.get("values_pattern", "'%value%'").replace("value", v)
        wire["values"] = [pattern]
        wire["encloseInQuotes"] = False
        return wire

    if ui in ("IS_LESS_THAN", "IS_GREATER_THAN", "IS_LESS_THAN_OR_EQUAL_TO", "IS_GREATER_THAN_OR_EQUAL_TO"):
        mapping = CONDITION_WIRE_MAP[ui]
        wire["condition"] = "CUSTOM"
        wire["customCondition"] = mapping["customCondition"]
        wire["values"] = [str(v) for v in values]
        wire["isCustomValue"] = True
        return wire

    if ui in ("IS_BETWEEN", "IS_NOT_BETWEEN"):
        wire["condition"] = "CUSTOM"
        wire["customCondition"] = "NOT BETWEEN" if ui == "IS_NOT_BETWEEN" else "BETWEEN"
        low = values[0] if values else ""
        high = values[1] if len(values) > 1 else low
        if is_date:
            wire["values"] = [f"'{low}' AND '{high}'"]
        else:
            wire["values"] = [f"{low} AND {high}"]
        wire["isCustomValue"] = True
        return wire

    if ui in ("IN_RANGE", "NOT_IN_RANGE"):
        wire["condition"] = ui
        wire["values"] = [float(v) if _is_number(v) else v for v in values]
        wire["encloseInQuotes"] = False
        wire["isCustomValue"] = True
        return wire

    if ui == "IS_NULL":
        wire["condition"] = "CUSTOM"
        wire["customCondition"] = "IS NULL"
        return wire

    if ui == "IS_NOT_NULL":
        wire["condition"] = "CUSTOM"
        wire["customCondition"] = "IS NOT NULL"
        return wire

    wire["condition"] = "CUSTOM"
    wire["customCondition"] = item.raw_sql or ui
    wire["values"] = values
    wire["isCustomValue"] = True
    wire["mode"] = "custom"
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
