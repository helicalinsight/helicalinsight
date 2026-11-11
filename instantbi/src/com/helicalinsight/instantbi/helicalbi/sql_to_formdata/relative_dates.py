"""Stamp pydantic relative-date tokens onto parsed WHERE filters.

The tokens come from the post-SQL prompt. This module does not read
CURRENT_DATE or DATE_TRUNC out of the SQL.
"""

from __future__ import annotations

import re
from dataclasses import replace
from typing import Any

from .models import FilterItem

_LOWER = ("IS_GREATER_THAN", "IS_GREATER_THAN_OR_EQUAL_TO")
_UPPER = ("IS_LESS_THAN", "IS_LESS_THAN_OR_EQUAL_TO")
# Same sets the hreport date filter uses for a relative list.
_RANGE = ("IS_BETWEEN", "IS_NOT_BETWEEN", "IN_RANGE", "NOT_IN_RANGE")
_SINGLE = (
    "EQUALS",
    "NOT_EQUALS",
    "IS_LESS_THAN",
    "IS_GREATER_THAN",
    "IS_LESS_THAN_OR_EQUAL_TO",
    "IS_GREATER_THAN_OR_EQUAL_TO",
)
_LIST = ("IS_ONE_OF", "IS_NOT_ONE_OF")
_SUPPORTED = _RANGE + _SINGLE + _LIST
_CONDITION_ALIASES = {
    "BETWEEN": "IS_BETWEEN",
    "NOT_BETWEEN": "IS_NOT_BETWEEN",
    "IN_RANGE": "IN_RANGE",
    "NOT_IN_RANGE": "NOT_IN_RANGE",
    "IN": "IS_ONE_OF",
    "NOT_IN": "IS_NOT_ONE_OF",
    "=": "EQUALS",
    "EQUAL": "EQUALS",
    "<>": "NOT_EQUALS",
    "!=": "NOT_EQUALS",
    "<": "IS_LESS_THAN",
    ">": "IS_GREATER_THAN",
    "<=": "IS_LESS_THAN_OR_EQUAL_TO",
    ">=": "IS_GREATER_THAN_OR_EQUAL_TO",
}
# hreport filter-utils relativeDateRegex. Day is stored as today, not day.
_TOKEN = re.compile(
    r"^(anchor_)?(today|tomorrow|yesterday|month|year|quarter|hour|minute|second|to_date)([-+]\d+)?$",
    re.IGNORECASE,
)
_FIXED = {"tomorrow", "yesterday", "to_date"}


def apply_relative_dates(items: list[FilterItem], detected: Any) -> list[FilterItem]:
    """Apply detected tokens. ``detected`` is a RelativeDates model or its dict."""
    specs = _specs(detected)
    if not specs or not items:
        return items
    result = list(items)
    drop: set[int] = set()
    for spec in specs:
        indexes = [
            index
            for index, item in enumerate(result)
            if index not in drop and _column_matches(item, spec["column"])
        ]
        if not indexes:
            continue
        if spec["condition"] in _RANGE:
            _apply_range(result, indexes, spec["condition"], spec["values"], drop)
            continue
        target = _prefer(result, indexes, spec["condition"])
        result[target] = replace(
            result[target],
            ui_condition=spec["condition"] or result[target].ui_condition,
            values=spec["values"],
            is_relative=True,
        )
    if not drop:
        return result
    return [item for index, item in enumerate(result) if index not in drop]


def _apply_range(
    items: list[FilterItem],
    indexes: list[int],
    condition: str,
    values: list[str],
    drop: set[int],
) -> None:
    lows = [index for index in indexes if items[index].ui_condition in _LOWER]
    highs = [index for index in indexes if items[index].ui_condition in _UPPER]
    if len(lows) == 1 and len(highs) == 1:
        keep = min(lows[0], highs[0])
        items[keep] = replace(
            items[keep],
            ui_condition=condition,
            values=values,
            is_relative=True,
        )
        drop.add(max(lows[0], highs[0]))
        return
    target = _prefer(items, indexes, condition)
    items[target] = replace(
        items[target],
        ui_condition=condition,
        values=values,
        is_relative=True,
    )


def _prefer(items: list[FilterItem], indexes: list[int], condition: str) -> int:
    for index in indexes:
        if items[index].ui_condition == condition:
            return index
    return indexes[0]


def _specs(detected: Any) -> list[dict[str, Any]]:
    raw = detected
    if hasattr(detected, "model_dump"):
        raw = detected.model_dump()
    if not isinstance(raw, dict):
        return []
    filters = raw.get("filters")
    if not isinstance(filters, list):
        return []
    specs: list[dict[str, Any]] = []
    for item in filters:
        if hasattr(item, "model_dump"):
            item = item.model_dump()
        if not isinstance(item, dict):
            continue
        column = str(item.get("column") or "").strip()
        condition = _condition(item.get("condition"))
        values = _tokens(item.get("values"))
        if not column or not values or condition not in _SUPPORTED:
            continue
        if condition in _SINGLE:
            values = values[:1]
        specs.append(
            {
                "column": column,
                "condition": condition,
                "values": values,
            }
        )
    return specs


def _tokens(values: Any) -> list[str]:
    if not isinstance(values, list):
        return []
    tokens: list[str] = []
    for value in values:
        token = _one_token(value)
        if token:
            tokens.append(token)
    return tokens


def _one_token(value: Any) -> str | None:
    """Keep a token only when hreport's relativeDateRegex accepts it."""
    text = str(value or "").strip().replace(" ", "")
    folded = text.lower()
    if folded == "day":
        text = "today"
    elif folded.startswith("anchor_day"):
        text = "anchor_today" + text[len("anchor_day"):]
    match = _TOKEN.match(text)
    if not match:
        return None
    prefix = (match.group(1) or "").lower()
    name = match.group(2).lower()
    modifier = match.group(3) or ""
    if name in _FIXED:
        modifier = ""
    return f"{prefix}{name}{modifier}"


def _condition(value: Any) -> str:
    key = str(value or "").strip().upper().replace(" ", "_")
    return _CONDITION_ALIASES.get(key, key)


def _column_matches(item: FilterItem, column: str) -> bool:
    if item.aggregate or not item.column:
        return False
    wanted = column.strip().strip('"').lower()
    name = str(item.column.name or "").lower()
    short = str(item.column.short or "").lower()
    tail = wanted.split(".")[-1]
    return wanted in {short, name} or tail == name
