"""SELECT → formData.columns[] (prepareColumns / wire-column)."""

from __future__ import annotations

import re
from dataclasses import replace

from ..functions_catalog import to_wire_database_function
from ..metadata import resolve_wire_column
from ..models import ParsedQuery, SelectItem


def build_columns(parsed: ParsedQuery, metadata: dict | None = None) -> list[dict]:
    meta = metadata or {}
    columns = [_to_wire_column(item, meta) for item in parsed.selects]
    _attach_hidden_clause_columns(parsed, columns, meta)
    return columns


def _to_wire_column(item: SelectItem, meta: dict) -> dict:
    aggregates = item.aggregates or ([item.aggregate] if item.aggregate else [])
    wire_dbf = to_wire_database_function(item.database_function)

    # Adhoc custom column (selectRaw / CUSTOM_FORMULA): column is the raw SQL
    # string, not {name, id}. Java SimpleSelectFragment requires custom=true.
    if item.is_custom and not wire_dbf:
        wire = {
            "column": item.custom_expression or item.raw_sql or item.alias,
            "alias": item.alias,
            "floatingType": "discrete",
            "custom": True,
        }
        used = _used_column_names(item, meta)
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

    if item.column:
        column_ref = resolve_wire_column(
            item.column.table,
            item.column.name,
            meta,
            fallback_name=item.column.short,
        )
    else:
        column_ref = item.raw_sql or item.alias

    wire: dict = {
        "column": column_ref,
        "alias": item.alias,
        "floatingType": "discrete",
    }

    if wire_dbf:
        wire["databaseFunction"] = wire_dbf

    if aggregates:
        wire["aggregate"] = True
        wire["aggregateList"] = aggregates

    if item.hidden:
        wire["hidden"] = True
        if item.include_in_resultset:
            wire["includeInResultset"] = True

    return wire


def _used_column_names(item: SelectItem, meta: dict) -> list[str]:
    cols = list(item.used_columns or [])
    if not cols and item.column:
        cols = [item.column]
    names: list[str] = []
    seen: set[str] = set()
    for col in cols:
        ref = resolve_wire_column(
            col.table,
            col.name,
            meta,
            fallback_name=col.short,
        )
        name = ref["name"] if isinstance(ref, dict) else str(ref or "")
        if name and name not in seen:
            seen.add(name)
            names.append(name)
    return names


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


def _should_materialize(item: SelectItem) -> bool:
    if item.column is not None or item.database_function:
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
    item_dbf = to_wire_database_function(item.database_function)
    hint = (alias_hint or item.alias or "").strip().lower()

    for col in columns:
        col_dbf = col.get("databaseFunction")
        col_alias = str(col.get("alias") or "")

        if item_dbf and col_dbf:
            if _dbf_same(item_dbf, col_dbf, item):
                return col
            continue

        if item_dbf:
            continue

        if hint and col_alias.lower() == hint:
            return col

        if item.column and _wire_column_refers_to(col, item):
            return col

        if item.raw_sql and _norm_sql(item.raw_sql) == _norm_sql(str(col.get("alias") or "")):
            return col

    return None


def _dbf_same(left: dict, right: dict, item: SelectItem) -> bool:
    if (left.get("functionName") or "") != (right.get("functionName") or ""):
        return False
    item_col = (item.column.name if item.column else "").lower()
    right_params = right.get("parameters") or {}
    for value in right_params.values():
        if not isinstance(value, str):
            continue
        leaf = value.split(".")[-1].lower()
        if item_col and leaf == item_col:
            return True
        left_params = left.get("parameters") or {}
        for lv in left_params.values():
            if isinstance(lv, str) and lv.split(".")[-1].lower() == leaf:
                return True
    return not item_col and left.get("parameters") == right.get("parameters")


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
    dbf = to_wire_database_function(item.database_function)
    col_alias = _meta_column_alias(item, meta)
    if dbf and col_alias:
        part = str(dbf.get("functionName") or "").split(".")[-1]
        if part:
            return _date_part_display_alias(col_alias, part)
    if item.alias and item.alias.lower() != (item.column.name.lower() if item.column else ""):
        return item.alias
    return col_alias or item.alias or "expr"


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
