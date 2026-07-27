"""Aggregate + orderBy → functions slice (extractColumnFunctions)."""

from __future__ import annotations

from ..models import ParsedQuery
from .groupby_layer import build_groupby


def build_functions(
    parsed: ParsedQuery,
    columns: list[dict] | None = None,
) -> dict:
    columns = columns or []
    functions: dict = {}

    group_by = build_groupby(parsed, columns)
    if group_by:
        functions["groupBy"] = group_by

    aggregates = []
    for col in columns:
        if not col.get("aggregate"):
            continue
        entry = {
            "column": col["column"],
            "function": (col.get("aggregateList") or ["db.generic.aggregate.sum"])[0],
            "alias": col.get("alias"),
            "custom": bool(col.get("custom")),
        }
        if col.get("applyBeforeAggregate"):
            entry["applyBeforeAggregate"] = True
        aggregates.append(entry)
    if aggregates:
        functions["aggregate"] = aggregates

    order_by = []
    for o in parsed.order_by:
        order_by.append(
            {
                "alias": o.alias_or_column,
                "order": o.direction,
                "custom": True,
            }
        )
    if order_by:
        functions["orderBy"] = order_by

    return functions
