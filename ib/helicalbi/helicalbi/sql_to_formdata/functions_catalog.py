"""
Function catalog built from getFunctions API response.

Replaces hardcoded aggregates.py / database_functions.py mappings.
Response shape (see functionMapping.js):
  response.functions          → aggregate / groupBy / orderBy keys
  response.databaseFunctions  → non-aggregate SQL fns by category
  response.reference          → dialect hint (e.g. "postgresql")
"""

from __future__ import annotations

import copy
from dataclasses import dataclass, field
from typing import Any

from sqlglot import exp


# API reference string → sqlglot read dialect
REFERENCE_TO_SQLGLOT = {
    "postgresql": "postgres",
    "postgres": "postgres",
    "mysql": "mysql",
    "mariadb": "mysql",
    "oracle": "oracle",
    "sqlserver": "tsql",
    "mssql": "tsql",
    "tsql": "tsql",
    "hive": "hive",
    "spark": "spark",
    "redshift": "redshift",
    "snowflake": "snowflake",
    "bigquery": "bigquery",
    "presto": "presto",
    "trino": "trino",
    "sqlite": "sqlite",
    "duckdb": "duckdb",
}


@dataclass
class FunctionCatalog:
    """Lookups derived from a stored getFunctions response."""

    raw: dict[str, Any] = field(default_factory=dict)
    reference: str = ""
    # SQL name (SUM) → db.generic.aggregate.sum
    aggregate_by_sql: dict[str, str] = field(default_factory=dict)
    # db.generic.aggregate.sum → sum
    aggregate_by_key: dict[str, str] = field(default_factory=dict)
    group_by_key: str = "db.generic.groupBy.group"
    order_by_key: str = "db.generic.orderBy.order"
    # SQL name (ABS) → full function def from API
    database_fn_by_sql: dict[str, dict[str, Any]] = field(default_factory=dict)
    # key (sql.numeric.abs) → def
    database_fn_by_key: dict[str, dict[str, Any]] = field(default_factory=dict)

    @classmethod
    def from_api_payload(cls, payload: dict[str, Any]) -> FunctionCatalog:
        response = payload.get("response", payload) if isinstance(payload, dict) else {}
        if not isinstance(response, dict):
            response = {}

        catalog = cls(raw=payload, reference=str(response.get("reference") or ""))

        for key, sql_name in (response.get("functions") or {}).items():
            sql_upper = str(sql_name).upper().replace(" ", "_")
            if ".aggregate." in key:
                catalog.aggregate_by_key[key] = str(sql_name)
                catalog.aggregate_by_sql[sql_upper] = key
                # also map plain token e.g. AVG from "avg"
                catalog.aggregate_by_sql[str(sql_name).upper()] = key
            elif key.endswith("groupBy.group") or ".groupBy." in key:
                catalog.group_by_key = key
            elif key.endswith("orderBy.order") or ".orderBy." in key:
                catalog.order_by_key = key

        # COUNT(DISTINCT …) → distinct key when present
        if "DISTINCT" in catalog.aggregate_by_sql and "COUNT_DISTINCT" not in catalog.aggregate_by_sql:
            catalog.aggregate_by_sql["COUNT_DISTINCT"] = catalog.aggregate_by_sql["DISTINCT"]

        for _category, entries in (response.get("databaseFunctions") or {}).items():
            if not isinstance(entries, list):
                continue
            for entry in entries:
                if not isinstance(entry, dict):
                    continue
                key = entry.get("key")
                value = entry.get("value")
                if key:
                    catalog.database_fn_by_key[str(key)] = entry
                if value:
                    catalog.database_fn_by_sql[str(value).upper()] = entry

        return catalog

    @property
    def dialect(self) -> str:
        """sqlglot dialect derived from response.reference."""
        ref = (self.reference or "").lower().strip()
        return REFERENCE_TO_SQLGLOT.get(ref, ref or "postgres")

    def is_aggregate(self, sql_fn_name: str) -> bool:
        return bool(self.aggregate_key(sql_fn_name))

    def aggregate_key(self, sql_fn_name: str) -> str | None:
        if not sql_fn_name:
            return None
        return self.aggregate_by_sql.get(sql_fn_name.upper())

    def lookup_database_fn(self, fn_name: str) -> dict[str, Any] | None:
        if not fn_name:
            return None
        return self.database_fn_by_sql.get(fn_name.upper())

    def map_database_function(self, fn_name: str, column_ref: str) -> dict | None:
        """
        Legacy helper: single-column databaseFunction (wire-compatible flat params).

        Prefer `build_database_function` for nested / multi-arg expressions.
        """
        built = self.build_database_function_simple(fn_name, column_ref)
        if not built:
            return None
        # Flatten to older wire shape when only simple column params
        params_flat: dict[str, str] = {}
        for p in built.get("parameters") or []:
            if isinstance(p, dict) and isinstance(p.get("value"), str):
                params_flat[p["name"]] = p["value"]
        return {
            "functionName": built.get("key"),
            "dataType": built.get("returns") or "text",
            "parameters": params_flat,
        }

    def build_database_function_simple(self, fn_name: str, column_ref: str) -> dict | None:
        entry = self.lookup_database_fn(fn_name)
        if not entry:
            return None
        out = copy.deepcopy(entry)
        params = []
        filled = False
        for p in out.get("parameters") or []:
            if not isinstance(p, dict):
                continue
            param = copy.deepcopy(p)
            if not filled and param.get("column"):
                param["value"] = column_ref
                filled = True
            params.append(param)
        if not filled and params:
            params[0]["value"] = column_ref
            params[0]["column"] = True
        out["parameters"] = params
        return out

    def build_database_function(
        self,
        expr: exp.Expression,
        *,
        dialect: str,
        database_name: str = "",
        table_alias: str = "",
    ) -> dict[str, Any] | None:
        """
        Build nested databaseFunction matching nested_agg formData shape.

        Simple:  CONCAT(col, 'x') → parameters[].value = fq column / literal
        Nested:  LENGTH(CAST(CONCAT(...) AS VARCHAR)) → param value is nested DBF object

        Returns None when the (outer) SQL function is not in the catalog — caller
        should treat the expression as a custom column.
        """
        expr = _unwrap_paren(expr)
        if not isinstance(expr, exp.Func) or isinstance(expr, exp.AggFunc):
            return None

        fn_name = _sql_fn_name(expr)
        entry = self.lookup_database_fn(fn_name)
        if not entry:
            return None

        out = copy.deepcopy(entry)
        signature = str(entry.get("signature") or "")
        param_defs = [p for p in (entry.get("parameters") or []) if isinstance(p, dict)]
        args = _func_args(expr)

        # Peel signature wrappers e.g. length(cast(${string} as VARCHAR))
        peeled_args: list[exp.Expression] = []
        for i, arg in enumerate(args):
            name = param_defs[i]["name"] if i < len(param_defs) else None
            peeled_args.append(_peel_signature_wrappers(arg, name, signature))

        params: list[dict[str, Any]] = []
        for i, param_def in enumerate(param_defs):
            param = copy.deepcopy(param_def)
            if i >= len(peeled_args):
                params.append(param)
                continue
            arg = peeled_args[i]
            value, is_column = self._map_param_value(
                arg,
                dialect=dialect,
                database_name=database_name,
                table_alias=table_alias,
            )
            param["value"] = value
            param["column"] = bool(is_column) if not isinstance(value, dict) else False
            params.append(param)

        out["parameters"] = params
        return out

    def _map_param_value(
        self,
        arg: exp.Expression,
        *,
        dialect: str,
        database_name: str,
        table_alias: str,
    ) -> tuple[Any, bool]:
        """Return (value, is_column_ref). Nested funcs become nested databaseFunction dicts."""
        arg = _unwrap_paren(arg)

        if isinstance(arg, exp.Column):
            return _fq_column_ref(arg, database_name, table_alias), True

        if isinstance(arg, exp.Literal):
            if arg.is_string:
                return f"'{arg.this}'", False
            return str(arg.this), False

        if isinstance(arg, exp.Null):
            return "NULL", False

        if isinstance(arg, exp.Boolean):
            return str(arg.this).lower(), False

        # Nested database function (e.g. CONCAT inside LENGTH)
        if isinstance(arg, exp.Func) and not isinstance(arg, exp.AggFunc):
            nested = self.build_database_function(
                arg,
                dialect=dialect,
                database_name=database_name,
                table_alias=table_alias,
            )
            if nested is not None:
                return nested, False
            # Unknown nested fn → raw SQL snippet
            return arg.sql(dialect=dialect), False

        # Cast not peeled (no signature hint) — try inner
        if isinstance(arg, exp.Cast):
            return self._map_param_value(
                arg.this,
                dialect=dialect,
                database_name=database_name,
                table_alias=table_alias,
            )

        return arg.sql(dialect=dialect), False

    def functions_definition(self, dbf: dict[str, Any] | None) -> str:
        """Human/UI functionsDefinition string, e.g. CONCAT(destination,' travelled')."""
        if not dbf:
            return ""
        return _functions_definition(dbf)


def _sql_fn_name(node: exp.Func) -> str:
    """Resolve SQL function name, including Anonymous (unknown) calls."""
    if isinstance(node, exp.Anonymous):
        return str(node.this or getattr(node, "name", "") or "")
    name = node.sql_name()
    if str(name).upper() == "ANONYMOUS":
        return str(getattr(node, "this", None) or getattr(node, "name", "") or "")
    return str(name or "")


def _func_args(node: exp.Func) -> list[exp.Expression]:
    """Positional SQL arguments for a sqlglot Func node."""
    # Anonymous: this is the function name string; args are in expressions only
    if isinstance(node, exp.Anonymous):
        return [a for a in (node.expressions or []) if isinstance(a, exp.Expression)]

    exprs = list(node.expressions or [])
    if node.this is not None and not exprs:
        return [node.this]
    if node.this is not None and exprs:
        return [node.this, *exprs]
    return exprs


def _unwrap_paren(node: exp.Expression) -> exp.Expression:
    while isinstance(node, exp.Paren):
        node = node.this
    return node


def _peel_signature_wrappers(
    arg: exp.Expression,
    param_name: str | None,
    signature: str,
) -> exp.Expression:
    """
    If catalog signature wraps a param in cast(...), peel Cast from the AST arg.
    Example signature: length(cast(${string} as VARCHAR))
    """
    arg = _unwrap_paren(arg)
    if not param_name or not signature:
        return arg
    needle = f"cast(${{{param_name}}}"
    if needle.lower() in signature.lower() and isinstance(arg, exp.Cast):
        return _unwrap_paren(arg.this)
    return arg


def _fq_column_ref(col: exp.Column, database_name: str, table_alias: str) -> str:
    table = col.table or table_alias or ""
    name = col.name
    parts = [p for p in (database_name, table, name) if p]
    return ".".join(parts) if parts else name


def _functions_definition(dbf: dict[str, Any]) -> str:
    name = str(dbf.get("value") or dbf.get("key") or "FN").upper()
    if name and not name.isupper():
        name = str(dbf.get("value") or "FN")
    args: list[str] = []
    for p in dbf.get("parameters") or []:
        if not isinstance(p, dict):
            continue
        val = p.get("value")
        if isinstance(val, dict) and (val.get("key") or val.get("value")):
            args.append(_functions_definition(val))
        elif isinstance(val, str):
            # Prefer short column name in definition (last segment) for column refs
            if p.get("column") and "." in val and not (val.startswith("'") or val.startswith('"')):
                args.append(val.split(".")[-1])
            else:
                args.append(val)
        elif val is not None:
            args.append(str(val))
    return f"{dbf.get('value') or name}({','.join(args)})"


def collect_applied_dbfs(dbf: dict[str, Any] | None, seen: dict[str, dict] | None = None) -> list[dict]:
    """Collect unique catalog templates (no param values) used by a nested databaseFunction."""
    seen = seen if seen is not None else {}
    if not dbf or not isinstance(dbf, dict):
        return list(seen.values())
    key = dbf.get("key")
    if key and key not in seen:
        template = {
            "key": dbf.get("key"),
            "description": dbf.get("description"),
            "value": dbf.get("value"),
            "signature": dbf.get("signature"),
            "returns": dbf.get("returns"),
            "parameters": [],
        }
        for p in dbf.get("parameters") or []:
            if not isinstance(p, dict):
                continue
            tp = {"name": p.get("name"), "column": True if p.get("column") else p.get("column", True)}
            if "defaultValue" in p:
                tp["defaultValue"] = p["defaultValue"]
            # Nested param templates omit value; mark column true like appliedDbfs samples
            if isinstance(p.get("value"), dict):
                tp["column"] = True
            template["parameters"].append(tp)
        seen[str(key)] = template
    for p in dbf.get("parameters") or []:
        if isinstance(p, dict) and isinstance(p.get("value"), dict):
            collect_applied_dbfs(p["value"], seen)
    return list(seen.values())
