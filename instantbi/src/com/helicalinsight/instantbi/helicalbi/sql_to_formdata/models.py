"""Intermediate parse models shared across parts."""

from __future__ import annotations

from dataclasses import dataclass, field
from typing import Any


@dataclass
class ColumnRef:
    table: str | None = None
    name: str = ""
    catalog: str | None = None  # e.g. sampletraveldata.public

    @property
    def short(self) -> str:
        if self.table:
            return f"{self.table}.{self.name}"
        return self.name

    @property
    def fully_qualified(self) -> str:
        parts = [p for p in (self.catalog, self.table, self.name) if p]
        return ".".join(parts)


@dataclass
class SelectItem:
    alias: str
    column: ColumnRef | None = None
    aggregate: str | None = None  # primary db.generic.aggregate.*
    aggregates: list[str] = field(default_factory=list)  # e.g. [sum, distinct]
    database_function: dict | None = None
    # SQL of the database-function expression only (no AS alias / outer aggregate)
    database_function_sql: str = ""
    functions_definition: str = ""
    custom_expression: str | None = None
    is_custom: bool = False
    used_columns: list[ColumnRef] = field(default_factory=list)
    raw_sql: str = ""
    hidden: bool = False
    include_in_resultset: bool = False


@dataclass
class FilterItem:
    column: ColumnRef | None
    ui_condition: str
    values: list[Any] = field(default_factory=list)
    operator: str = "AND"  # AND | OR joining to previous
    aggregate: str | None = None  # if set → having
    database_function: dict | None = None
    # SQL of the filter's database-function expression (left side), no alias
    database_function_sql: str = ""
    alias: str | None = None
    is_all: bool = False  # '_all_' = '_all_'
    custom_sql: str | None = None
    used_columns: list[ColumnRef] = field(default_factory=list)
    raw_sql: str = ""


@dataclass
class OrderItem:
    alias_or_column: str
    direction: str = "asc"  # asc | desc
    item: SelectItem | None = None  # parsed ORDER BY expression


@dataclass
class ParsedQuery:
    database_name: str = ""
    table_alias: str = ""
    table_name: str = ""
    # SQL alias / name → physical table name (e.g. td → travel_details)
    table_aliases: dict[str, str] = field(default_factory=dict)
    selects: list[SelectItem] = field(default_factory=list)
    group_by: list[ColumnRef] = field(default_factory=list)
    group_by_items: list[SelectItem] = field(default_factory=list)
    where_filters: list[FilterItem] = field(default_factory=list)
    having_filters: list[FilterItem] = field(default_factory=list)
    order_by: list[OrderItem] = field(default_factory=list)
    limit: int | None = None
    offset: int | None = None
    dialect: str = "postgres"
    sql: str = ""  # SQL text that was parsed
    # getFunctions catalog used while parsing (aggregates / databaseFunctions)
    function_catalog: Any = None
