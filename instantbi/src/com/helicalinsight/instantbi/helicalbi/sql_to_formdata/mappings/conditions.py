"""
UI condition ↔ wire formData mapping (filter-conditions-map.json / getFilters.js).

SQL operators are first mapped to UI conditions, then to wire shapes.
"""

from __future__ import annotations

CONDITION_WIRE_MAP = {
    "EQUALS": {"condition": "EQUALS"},
    "NOT_EQUALS": {"condition": "CUSTOM", "customCondition": "<>"},
    "IS_ONE_OF": {"condition": "CUSTOM", "customCondition": " IN ("},
    "IS_NOT_ONE_OF": {"condition": "CUSTOM", "customCondition": " NOT IN ("},
    "CONTAINS": {"condition": "CUSTOM", "customCondition": "like"},
    "DOES_NOT_CONTAINS": {"condition": "CUSTOM", "customCondition": "not like"},
    "STARTS_WITH": {"condition": "CUSTOM", "customCondition": "like"},
    "DOES_NOT_STARTS_WITH": {"condition": "CUSTOM", "customCondition": "not like"},
    "ENDS_WITH": {"condition": "CUSTOM", "customCondition": "like"},
    "DOES_NOT_ENDS_WITH": {"condition": "CUSTOM", "customCondition": "not like"},
    "IS_LESS_THAN": {"condition": "CUSTOM", "customCondition": "<"},
    "IS_GREATER_THAN": {"condition": "CUSTOM", "customCondition": ">"},
    "IS_LESS_THAN_OR_EQUAL_TO": {"condition": "CUSTOM", "customCondition": "<="},
    "IS_GREATER_THAN_OR_EQUAL_TO": {"condition": "CUSTOM", "customCondition": ">="},
    "IS_BETWEEN": {"condition": "CUSTOM", "customCondition": "BETWEEN"},
    "IS_NOT_BETWEEN": {"condition": "CUSTOM", "customCondition": "NOT BETWEEN"},
    "IN_RANGE": {"condition": "IN_RANGE", "values_type": "array"},
    "NOT_IN_RANGE": {"condition": "NOT_IN_RANGE", "values_type": "array"},
    "IS_NULL": {"condition": "CUSTOM", "customCondition": "IS NULL", "values": "deleted"},
    "IS_NOT_NULL": {"condition": "CUSTOM", "customCondition": "IS NOT NULL", "values": "deleted"},
    "CUSTOM": {"condition": "CUSTOM", "customCondition": "user-defined", "mode": "custom"},
    "ALL": {"condition": "EQUALS", "valuesMode": "all"},
}

# sqlglot / SQL operator → UI condition name
_SQL_OP_MAP = {
    "EQ": "EQUALS",
    "=": "EQUALS",
    "NEQ": "NOT_EQUALS",
    "NE": "NOT_EQUALS",
    "<>": "NOT_EQUALS",
    "!=": "NOT_EQUALS",
    "GT": "IS_GREATER_THAN",
    ">": "IS_GREATER_THAN",
    "GTE": "IS_GREATER_THAN_OR_EQUAL_TO",
    ">=": "IS_GREATER_THAN_OR_EQUAL_TO",
    "LT": "IS_LESS_THAN",
    "<": "IS_LESS_THAN",
    "LTE": "IS_LESS_THAN_OR_EQUAL_TO",
    "<=": "IS_LESS_THAN_OR_EQUAL_TO",
    "IN": "IS_ONE_OF",
    "NOT_IN": "IS_NOT_ONE_OF",
    "NOTIN": "IS_NOT_ONE_OF",
    "BETWEEN": "IS_BETWEEN",
    "NOT_BETWEEN": "IS_NOT_BETWEEN",
    "LIKE": "CONTAINS",
    "ILIKE": "CONTAINS",
    "IS": "IS_NULL",
    "IS_NOT": "IS_NOT_NULL",
}


def sql_op_to_ui_condition(op: str) -> str:
    if not op:
        return "CUSTOM"
    key = op.upper().strip()
    return _SQL_OP_MAP.get(key, "CUSTOM")
