"""SELECT → formData.columns[] (prepareColumns / wire-column)."""

from __future__ import annotations

import re
from dataclasses import replace
from typing import Any

from ..functions_catalog import to_wire_database_function_expression
from ..metadata import (
    resolve_host_column_from_used,
    resolve_wire_column,
    to_raw_database_function,
    used_column_fq_names,
)
from ..models import ParsedQuery, SelectItem


def build_columns(parsed: ParsedQuery, metadata: dict | None = None) -> list[dict]:
    meta = metadata or {}
    columns = [_to_wire_column(item, meta) for item in parsed.selects]
    _attach_hidden_clause_columns(parsed, columns, meta)
    _attach_column_order(parsed, columns)
    return columns


def _to_wire_column(item: SelectItem, meta: dict) -> dict:
    aggregates = item.aggregates or ([item.aggregate] if item.aggregate else [])
    used = used_column_fq_names(item.used_columns, meta, fallback=item.column)

    # Catalog/signature match -> databaseFunction string, keep column ref.
    wire_dbf = to_wire_database_function_expression(
        item.database_function,
        metadata=meta,
        dialect=meta.get("dialect"),
    )
    if wire_dbf and item.column is not None:
        column_ref = resolve_wire_column(
            item.column.table,
            item.column.name,
            meta,
            fallback_name=item.column.short,
        )
        wire: dict[str, Any] = {
            "column": column_ref,
            "alias": item.alias,
            "databaseFunction": wire_dbf,
        }
        if used:
            wire["usedColumns"] = used
        if aggregates:
            wire["aggregate"] = True
            wire["aggregateList"] = aggregates
        if item.hidden:
            wire["hidden"] = True
            if item.include_in_resultset:
                wire["includeInResultset"] = True
        return wire

    # Match miss / explicit custom:
    #   usedColumns present → RAW(complete expression) on one host column
    #   usedColumns empty  → original expression SQL + custom:true
    # Do NOT treat plain-column raw_sql as custom.
    dbf_sql = (item.database_function_sql or "").strip()
    custom_expr = (item.custom_expression or "").strip()
    if item.is_custom or dbf_sql:
        expr = dbf_sql or custom_expr or (item.raw_sql or "").strip() or item.alias
        if used:
            host = resolve_host_column_from_used(used, meta, column=item.column)
            if host:
                wire = {
                    "column": host,
                    "alias": item.alias,
                    "databaseFunction": to_raw_database_function(expr),
                }
            else:
                wire = {
                    "column": expr,
                    "alias": item.alias,
                    "custom": True,
                }
        else:
            wire = {
                "column": expr,
                "alias": item.alias,
                "custom": True,
            }
        wire["usedColumns"] = used
        if aggregates:
            wire["aggregate"] = True
            wire["aggregateList"] = aggregates
        if item.hidden:
            wire["hidden"] = True
            if item.include_in_resultset:
                wire["includeInResultset"] = True
        return wire

    if item.column:
        column_ref = resolve_wire_column(
            item.column.table,
            item.column.name,
            meta,
            fallback_name=item.column.short,
        )
    else:
        column_ref = item.raw_sql or item.alias

    wire = {
        "column": column_ref,
        "alias": item.alias,
    }

    if aggregates:
        wire["aggregate"] = True
        wire["aggregateList"] = aggregates

    if item.hidden:
        wire["hidden"] = True
        if item.include_in_resultset:
            wire["includeInResultset"] = True

    return wire


def _attach_hidden_clause_columns(
    parsed: ParsedQuery,
    columns: list[dict],
    meta: dict,
) -> None:
    """
    ORDER BY / GROUP BY expressions that are not projected must still exist as
    formData columns so the engine can ORDER/GROUP by alias.

    Those extras are marked hidden + includeInResultset (present in SELECT SQL,
    omitted from the visible result / viz shelves).
    """
    used_aliases = {str(c.get("alias") or "") for c in columns}

    for order in parsed.order_by:
        if order.item is None or not _should_materialize(order.item):
            continue
        match = _find_matching_column(order.item, columns, order.alias_or_column)
        if match is not None:
            order.alias_or_column = str(match.get("alias") or order.alias_or_column)
            continue
        alias = _unique_alias(_hidden_alias(order.item, meta), used_aliases)
        hidden = replace(order.item, alias=alias, hidden=True, include_in_resultset=True)
        columns.append(_to_wire_column(hidden, meta))
        used_aliases.add(alias)
        order.alias_or_column = alias

    for item in parsed.group_by_items:
        if not _should_materialize(item):
            continue
        if _find_matching_column(item, columns, item.alias) is not None:
            continue
        alias = _unique_alias(_hidden_alias(item, meta), used_aliases)
        hidden = replace(item, alias=alias, hidden=True, include_in_resultset=True)
        columns.append(_to_wire_column(hidden, meta))
        used_aliases.add(alias)


def _attach_column_order(parsed: ParsedQuery, columns: list[dict]) -> None:
    """Set ``order`` on wire columns that appear in ORDER BY (omit when absent)."""
    for order in parsed.order_by:
        direction = str(order.direction or "asc").lower()
        if direction not in ("asc", "desc"):
            direction = "asc"
        match = None
        if order.item is not None:
            match = _find_matching_column(order.item, columns, order.alias_or_column)
        if match is None:
            hint = str(order.alias_or_column or "").strip().lower()
            if hint:
                for col in columns:
                    if str(col.get("alias") or "").lower() == hint:
                        match = col
                        break
        if match is not None:
            _set_order_after_alias(match, direction)


def _set_order_after_alias(col: dict, direction: str) -> None:
    rebuilt: dict = {}
    placed = False
    for key, value in col.items():
        if key == "order":
            continue
        rebuilt[key] = value
        if key == "alias":
            rebuilt["order"] = direction
            placed = True
    if not placed:
        rebuilt["order"] = direction
    col.clear()
    col.update(rebuilt)


def _should_materialize(item: SelectItem) -> bool:
    if item.column is not None or item.database_function or item.database_function_sql:
        return True
    raw = (item.raw_sql or item.custom_expression or "").strip()
    if not raw:
        return False
    return not raw.isdigit()


def _find_matching_column(
    item: SelectItem,
    columns: list[dict],
    alias_hint: str = "",
) -> dict | None:
    item_expr = _norm_sql(item.database_function_sql or item.custom_expression or "")
    hint = (alias_hint or item.alias or "").strip().lower()
    item_dbf_name = ""
    if isinstance(item.database_function, dict):
        item_dbf_name = str(item.database_function.get("value") or "").strip().upper()

    for col in columns:
        col_expr = _wire_expr_sql(col)
        col_alias = str(col.get("alias") or "")

        if hint and col_alias.lower() == hint:
            return col

        # Matched wire DBF (MONTH (travel_date)) â parse item still holding EXTRACT SQL
        if item_dbf_name and col.get("databaseFunction") and item.column:
            wire_name = str(col.get("databaseFunction") or "").split("(")[0].strip().upper()
            if wire_name == item_dbf_name and _wire_column_refers_to(col, item):
                return col

        if item_expr:
            if col_expr and _dbf_same(item_expr, col_expr, item):
                return col
            continue

        if item.column and not col.get("custom") and _wire_column_refers_to(col, item):
            return col

        if item.raw_sql and _norm_sql(item.raw_sql) == _norm_sql(str(col.get("alias") or "")):
            return col

    return None


def _wire_expr_sql(col: dict) -> str:
    dbf = col.get("databaseFunction")
    if dbf:
        return _norm_sql(_unwrap_raw(str(dbf)))
    if col.get("custom"):
        return _norm_sql(str(col.get("column") or ""))
    return ""


def _unwrap_raw(sql: str) -> str:
    text = (sql or "").strip()
    if len(text) >= 5 and text[:4].upper() == "RAW(" and text.endswith(")"):
        return text[4:-1].strip()
    return text


def _dbf_same(left: str, right: object, item: SelectItem) -> bool:
    right_sql = _norm_sql(str(right or ""))
    if not left or not right_sql:
        return False
    if left == right_sql:
        return True
    item_col = (item.column.name if item.column else "").lower()
    if item_col and item_col in right_sql and item_col in left:
        # Same function family on the same leaf column (quote / qualifier diffs)
        left_fn = left.split("(")[0].strip()
        right_fn = right_sql.split("(")[0].strip()
        return bool(left_fn) and left_fn == right_fn
    return False


def _wire_column_refers_to(col: dict, item: SelectItem) -> bool:
    if not item.column:
        return False
    name = _wire_column_name(col)
    if not name:
        return False
    parts = name.split(".")
    if parts[-1].lower() != item.column.name.lower():
        return False
    if item.column.table and len(parts) >= 2:
        return item.column.table.lower() in {p.lower() for p in parts}
    return True


def _wire_column_name(col: dict) -> str:
    ref = col.get("column")
    if isinstance(ref, dict):
        return str(ref.get("name") or "")
    return str(ref or "")


def _hidden_alias(item: SelectItem, meta: dict) -> str:
    dbf_sql = (item.database_function_sql or "").strip()
    col_alias = _meta_column_alias(item, meta)
    if dbf_sql and col_alias:
        part = _function_display_part(dbf_sql, item)
        if part:
            return _date_part_display_alias(col_alias, part)
    if item.alias and item.alias.lower() != (item.column.name.lower() if item.column else ""):
        return item.alias
    return col_alias or item.alias or "expr"


def _function_display_part(dbf_sql: str, item: SelectItem) -> str:
    key = ""
    if isinstance(item.database_function, dict):
        key = str(
            item.database_function.get("key")
            or item.database_function.get("functionName")
            or ""
        )
    if key:
        return key.split(".")[-1]
    head = dbf_sql.split("(")[0].strip()
    return head.split()[-1] if head else ""


def _meta_column_alias(item: SelectItem, meta: dict) -> str:
    if not item.column:
        return item.alias or ""
    by_column = meta.get("by_column") or {}
    hit = by_column.get(item.column.short) or by_column.get(item.column.name)
    if isinstance(hit, dict) and hit.get("alias"):
        return str(hit["alias"])
    return item.column.name or item.alias or ""


def _date_part_display_alias(col_alias: str, part: str) -> str:
    titled = str(col_alias).replace("_", " ").strip().title()
    part_titled = part.replace("_", " ").strip().title()
    for suffix in (" Date", " Datetime", " Date Time", " Time", " Timestamp"):
        if titled.endswith(suffix):
            return f"{titled[: -len(suffix)]} {part_titled}".strip()
    if titled.lower().endswith(part_titled.lower()):
        return titled
    return f"{titled} {part_titled}".strip()


def _unique_alias(alias: str, used: set[str]) -> str:
    base = alias or "expr"
    if base not in used:
        return base
    i = 2
    while f"{base} {i}" in used:
        i += 1
    return f"{base} {i}"


def _norm_sql(sql: str) -> str:
    s = sql.lower().replace('"', "").replace("'", "")
    return re.sub(r"\s+", " ", s).strip()
