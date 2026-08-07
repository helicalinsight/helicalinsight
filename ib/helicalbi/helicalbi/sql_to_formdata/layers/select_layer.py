"""SELECT → formData.columns[] (prepareColumns / wire-column)."""

from __future__ import annotations

from ..functions_catalog import to_wire_database_function
from ..metadata import resolve_wire_column
from ..models import ParsedQuery, SelectItem


def build_columns(parsed: ParsedQuery, metadata: dict | None = None) -> list[dict]:
    meta = metadata or {}
    return [_to_wire_column(item, meta) for item in parsed.selects]


def _to_wire_column(item: SelectItem, meta: dict) -> dict:
    aggregates = item.aggregates or ([item.aggregate] if item.aggregate else [])
    wire_dbf = to_wire_database_function(item.database_function)

    if item.column and (wire_dbf or not item.is_custom):
        column_ref = resolve_wire_column(
            item.column.table,
            item.column.name,
            meta,
            fallback_name=item.column.short,
        )
    elif item.is_custom and item.custom_expression:
        column_ref = item.custom_expression
    elif item.column:
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
    elif item.is_custom:
        wire["custom"] = True

    if aggregates:
        wire["aggregate"] = True
        wire["aggregateList"] = aggregates

    return wire
