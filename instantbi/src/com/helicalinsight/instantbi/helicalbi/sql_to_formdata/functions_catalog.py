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


class UnmappedDatabaseFunction(Exception):
    """A SQL function in the expression is not in the getFunctions catalog."""


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

# sqlglot rewritten names → catalog SQL ``value`` tokens (postgresql.xml)
SQLGLOT_TO_CATALOG_SQL = {
    "TIME_TO_STR": "TO_CHAR",
    "TO_CHAR": "TO_CHAR",
    "TIMESTAMP_TRUNC": "DATETRUNC",
    "DATE_TRUNC": "DATETRUNC",
    "DATETRUNC": "DATETRUNC",
    "DATETIME_TRUNC": "DATETTIMETRUNC",
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
        key = fn_name.upper()
        hit = self.database_fn_by_sql.get(key)
        if hit:
            return hit
        alias = SQLGLOT_TO_CATALOG_SQL.get(key)
        if alias:
            return self.database_fn_by_sql.get(alias)
        return None

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
        table_aliases: dict[str, str] | None = None,
    ) -> dict[str, Any] | None:
        """
        Build nested databaseFunction matching nested_agg formData shape.

        Simple:  CONCAT(col, 'x') → parameters[].value = fq column / literal
        Nested:  LENGTH(CAST(CONCAT(...) AS VARCHAR)) → param value is nested DBF object

        Returns None when any SQL function in the expression is not in the catalog —
        caller should treat the whole expression as a custom column.
        """
        try:
            return self._build_database_function_impl(
                expr,
                dialect=dialect,
                database_name=database_name,
                table_alias=table_alias,
                table_aliases=table_aliases,
            )
        except UnmappedDatabaseFunction:
            return None

    def _build_database_function_impl(
        self,
        expr: exp.Expression,
        *,
        dialect: str,
        database_name: str = "",
        table_alias: str = "",
        table_aliases: dict[str, str] | None = None,
    ) -> dict[str, Any] | None:
        expr = _unwrap_paren(expr)
        kwargs = {
            "dialect": dialect,
            "database_name": database_name,
            "table_alias": table_alias,
            "table_aliases": table_aliases,
        }
        if isinstance(expr, exp.Extract):
            built = self._build_extract_function(expr, **kwargs)
            if built is not None:
                return built

        # CAST is often a type wrapper, not a catalog function. Peel it unless CAST
        # itself is in getFunctions, so CAST(extract(month from col)) still maps.
        if isinstance(expr, exp.Cast):
            fn_name = _sql_fn_name(expr)
            entry = self.lookup_database_fn(fn_name) or self.lookup_database_fn("CAST")
            if entry:
                param_defs = [p for p in (entry.get("parameters") or []) if isinstance(p, dict)]
                args = _func_args_for_catalog(expr, param_defs)
                return self._fill_catalog_entry(entry, args, **kwargs)
            inner = expr.this
            if inner is None:
                return None
            return self._build_database_function_impl(inner, **kwargs)

        if not isinstance(expr, exp.Func) or isinstance(expr, exp.AggFunc):
            return None

        fn_name = _sql_fn_name(expr)
        entry = self.lookup_database_fn(fn_name)
        if not entry:
            return None

        param_defs = [p for p in (entry.get("parameters") or []) if isinstance(p, dict)]
        args = _func_args_for_catalog(expr, param_defs)
        return self._fill_catalog_entry(entry, args, **kwargs)

    def _build_extract_function(
        self,
        expr: exp.Extract,
        *,
        dialect: str,
        database_name: str,
        table_alias: str,
        table_aliases: dict[str, str] | None,
    ) -> dict[str, Any] | None:
        """Map EXTRACT(MONTH FROM col) → sql.dateTime.month (unit-specific) when possible."""
        unit = _extract_unit_name(expr)
        datetime_expr = expr.args.get("expression")
        kwargs = {
            "dialect": dialect,
            "database_name": database_name,
            "table_alias": table_alias,
            "table_aliases": table_aliases,
        }
        if datetime_expr is None:
            return None

        entry = None
        for candidate in _extract_unit_candidates(unit):
            entry = self.lookup_database_fn(candidate)
            if entry is None:
                entry = self._lookup_by_extract_signature(candidate)
            if entry is not None:
                break

        if entry is not None:
            # Unit-specific fns (MONTH) take only the datetime column.
            return self._fill_catalog_entry(entry, [datetime_expr], **kwargs)

        generic = self.lookup_database_fn("EXTRACT")
        if generic is None:
            return None
        args: list[exp.Expression] = []
        if expr.this is not None:
            args.append(expr.this)
        args.append(datetime_expr)
        return self._fill_catalog_entry(generic, args, **kwargs)

    def _lookup_by_extract_signature(self, unit: str) -> dict[str, Any] | None:
        if not unit:
            return None
        needle = f"extract({unit.lower()}from"
        for entry in self.database_fn_by_key.values():
            sig = str(entry.get("signature") or "").lower().replace(" ", "")
            if needle in sig:
                return entry
        return None

    def _fill_catalog_entry(
        self,
        entry: dict[str, Any],
        args: list[exp.Expression],
        *,
        dialect: str,
        database_name: str,
        table_alias: str,
        table_aliases: dict[str, str] | None,
    ) -> dict[str, Any]:
        out = copy.deepcopy(entry)
        signature = str(entry.get("signature") or "")
        param_defs = [p for p in (entry.get("parameters") or []) if isinstance(p, dict)]

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
                table_aliases=table_aliases,
                param_name=str(param_def.get("name") or ""),
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
        table_aliases: dict[str, str] | None = None,
        param_name: str = "",
    ) -> tuple[Any, bool]:
        """Return (value, is_column_ref). Nested funcs become nested databaseFunction dicts."""
        arg = _unwrap_paren(arg)

        if isinstance(arg, exp.Column):
            return _fq_column_ref(arg, database_name, table_alias, table_aliases), True

        if isinstance(arg, exp.Literal):
            if arg.is_string:
                text = str(arg.this)
                if param_name in ("formatMask", "format"):
                    text = _restore_postgres_format(text)
                # formatMask in expected wire is unquoted; unit keeps quotes
                if param_name in ("formatMask", "format"):
                    return text, False
                return f"'{text}'", False
            return str(arg.this), False

        if isinstance(arg, exp.Var):
            # DATE_TRUNC unit often becomes Var(MONTH)
            token = str(arg.this or arg)
            if param_name == "unit":
                return f"'{token.upper()}'", False
            return token, False

        if isinstance(arg, exp.Null):
            return "NULL", False

        if isinstance(arg, exp.Boolean):
            return str(arg.this).lower(), False

        map_kwargs = {
            "dialect": dialect,
            "database_name": database_name,
            "table_alias": table_alias,
            "table_aliases": table_aliases,
        }

        # CAST wrappers first — sqlglot Cast is a Func subclass.
        if isinstance(arg, exp.Cast):
            return self._map_param_value(
                arg.this,
                dialect=dialect,
                database_name=database_name,
                table_alias=table_alias,
                table_aliases=table_aliases,
                param_name=param_name,
            )

        # Nested database function (e.g. DATE_TRUNC inside TO_CHAR)
        if isinstance(arg, exp.Func) and not isinstance(arg, exp.AggFunc):
            nested = self._build_database_function_impl(arg, **map_kwargs)
            if nested is not None:
                return nested, False
            raise UnmappedDatabaseFunction(_sql_fn_name(arg) or arg.sql(dialect=dialect))

        self._assert_funcs_mapped(arg, **map_kwargs)
        return arg.sql(dialect=dialect), False

    def _assert_funcs_mapped(
        self,
        expr: exp.Expression,
        *,
        dialect: str,
        database_name: str,
        table_alias: str,
        table_aliases: dict[str, str] | None,
    ) -> None:
        """Abort mapping if any nested (non-CAST) function is missing from getFunctions."""
        for node in expr.walk():
            if isinstance(node, (exp.Cast, exp.AggFunc)):
                continue
            if not isinstance(node, exp.Func):
                continue
            nested = self._build_database_function_impl(
                node,
                dialect=dialect,
                database_name=database_name,
                table_alias=table_alias,
                table_aliases=table_aliases,
            )
            if nested is None:
                raise UnmappedDatabaseFunction(
                    _sql_fn_name(node) or node.sql(dialect=dialect)
                )

    def functions_definition(self, dbf: dict[str, Any] | None) -> str:
        """Human/UI functionsDefinition string, e.g. CONCAT(destination,' travelled')."""
        if not dbf:
            return ""
        return _functions_definition(dbf)


def to_wire_database_function(catalog_dbf: dict[str, Any] | None) -> dict[str, Any] | None:
    """
    Convert catalog-shaped databaseFunction → Helical wire onion.

    Catalog::
        { key, returns, parameters: [{name, value, column}, ...] }

    Wire::
        { functionName, dataType, parameters: { name: value|nestedWire } }
    """
    if not catalog_dbf or not isinstance(catalog_dbf, dict):
        return None
    # Already wire-shaped
    if catalog_dbf.get("functionName") and "key" not in catalog_dbf:
        return catalog_dbf

    params_out: dict[str, Any] = {}
    for param in catalog_dbf.get("parameters") or []:
        if not isinstance(param, dict):
            continue
        name = param.get("name")
        if not name:
            continue
        value = param.get("value")
        if isinstance(value, dict) and (value.get("key") or value.get("functionName")):
            params_out[str(name)] = to_wire_database_function(value)
        else:
            params_out[str(name)] = value

    return {
        "functionName": catalog_dbf.get("key") or catalog_dbf.get("functionName"),
        "dataType": catalog_dbf.get("returns") or catalog_dbf.get("dataType") or "text",
        "parameters": params_out,
    }


def _sql_fn_name(node: exp.Func) -> str:
    """Resolve SQL function name, including Anonymous (unknown) calls."""
    if isinstance(node, exp.Anonymous):
        return str(node.this or getattr(node, "name", "") or "")
    name = node.sql_name()
    if str(name).upper() == "ANONYMOUS":
        return str(getattr(node, "this", None) or getattr(node, "name", "") or "")
    return str(name or "")


def _extract_unit_name(node: exp.Extract) -> str:
    unit = node.this
    if unit is None:
        return ""
    if isinstance(unit, exp.Literal):
        return str(unit.this or "").strip("'\"").upper()
    if isinstance(unit, exp.Var):
        return str(unit.this or "").upper()
    return str(getattr(unit, "name", None) or getattr(unit, "this", None) or unit).upper()


def _extract_unit_candidates(unit: str) -> list[str]:
    """EXTRACT(MONTH FROM …) / EXTRACT(MILLISECONDS FROM …) → catalog SQL names."""
    if not unit:
        return []
    aliases = {
        "MILLISECONDS": "MILLISECOND",
        "SECONDS": "SECOND",
        "MINUTES": "MINUTE",
        "HOURS": "HOUR",
        "DAYS": "DAY",
        "MONTHS": "MONTH",
        "YEARS": "YEAR",
        "DOW": "DAYOFWEEK",
        "DOY": "DAYOFYEAR",
    }
    out = [unit]
    mapped = aliases.get(unit)
    if mapped and mapped not in out:
        out.append(mapped)
    return out


def _func_args(node: exp.Func) -> list[exp.Expression]:
    """Positional SQL arguments for a sqlglot Func node."""
    if isinstance(node, exp.Extract):
        args: list[exp.Expression] = []
        if node.this is not None:
            args.append(node.this)
        datetime_expr = node.args.get("expression")
        if datetime_expr is not None:
            args.append(datetime_expr)
        return args
    # Anonymous: this is the function name string; args are in expressions only
    if isinstance(node, exp.Anonymous):
        return [a for a in (node.expressions or []) if isinstance(a, exp.Expression)]

    exprs = list(node.expressions or [])
    if node.this is not None and not exprs:
        return [node.this]
    if node.this is not None and exprs:
        return [node.this, *exprs]
    return exprs


def _func_args_for_catalog(
    node: exp.Func,
    param_defs: list[dict[str, Any]],
) -> list[exp.Expression]:
    """
    Map sqlglot AST args onto catalog parameter order.

    Handles rewritten forms where args live in named fields:
      TimeToStr(this, format=...)     → to_char(value, formatMask)
      TimestampTrunc(this, unit=...)  → DATE_TRUNC(unit, date)
    """
    param_names = [str(p.get("name") or "") for p in param_defs]

    # TO_CHAR / TimeToStr: this=value, format=formatMask
    if isinstance(node, exp.TimeToStr) or (
        set(param_names) >= {"value", "formatMask"} and node.args.get("format") is not None
    ):
        value = node.this
        fmt = node.args.get("format")
        ordered: list[exp.Expression] = []
        for name in param_names:
            if name in ("value", "datetime", "date", "column") and value is not None:
                ordered.append(value)
            elif name in ("formatMask", "format") and fmt is not None:
                ordered.append(fmt)
            elif name and node.args.get(name) is not None:
                ordered.append(node.args[name])
        if ordered:
            return ordered

    # DATE_TRUNC / TimestampTrunc: catalog wants unit then date
    if isinstance(node, (exp.TimestampTrunc, exp.DateTrunc)) or (
        "unit" in param_names and node.args.get("unit") is not None
    ):
        date_expr = node.this
        unit_expr = node.args.get("unit")
        ordered = []
        for name in param_names:
            if name == "unit" and unit_expr is not None:
                ordered.append(unit_expr)
            elif name in ("date", "datetime", "value", "column") and date_expr is not None:
                ordered.append(date_expr)
            elif name and node.args.get(name) is not None:
                ordered.append(node.args[name])
        if ordered:
            return ordered

    return _func_args(node)


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


def _fq_column_ref(
    col: exp.Column,
    database_name: str,
    table_alias: str,
    table_aliases: dict[str, str] | None = None,
) -> str:
    """Wire column path: catalog.schema.table.column when database_name is set."""
    table = col.table or table_alias or ""
    if table_aliases and table:
        table = table_aliases.get(table, table)
    name = col.name
    parts = [p for p in (database_name, table, name) if p]
    return ".".join(parts) if parts else name


def _restore_postgres_format(fmt: str) -> str:
    """Reverse common sqlglot strftime rewrites back to to_char masks."""
    text = fmt
    replacements = (
        ("%Y", "YYYY"),
        ("%y", "YY"),
        ("%m", "MM"),
        ("%d", "DD"),
        ("%H", "HH24"),
        ("%I", "HH"),
        ("%M", "MI"),
        ("%S", "SS"),
        ("%b", "Mon"),
        ("%B", "Month"),
        ("%a", "Dy"),
        ("%A", "Day"),
    )
    for src, dst in replacements:
        text = text.replace(src, dst)
    return text


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
