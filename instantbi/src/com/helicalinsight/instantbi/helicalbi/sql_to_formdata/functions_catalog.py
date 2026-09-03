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
import re
from dataclasses import dataclass, field
from typing import Any

from sqlglot import exp, parse_one

from helicalbi.common.DialectMapper import resolve_sqlglot_dialect


class UnmappedDatabaseFunction(Exception):
    """A SQL function in the expression is not in the getFunctions catalog."""


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
        """sqlglot dialect from getFunctions ``reference`` via DialectMapper."""
        return resolve_sqlglot_dialect(self.reference) or "postgres"

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
            # Fall through to fuzzy signature match (all categories).
            return self._build_from_signature_match(expr, **kwargs)

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
        if entry:
            param_defs = [p for p in (entry.get("parameters") or []) if isinstance(p, dict)]
            args = _func_args_for_catalog(expr, param_defs)
            return self._fill_catalog_entry(entry, args, **kwargs)

        # Name miss → fuzzy-match SQL against catalog signatures (all categories).
        return self._build_from_signature_match(expr, **kwargs)

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

    def match_signature(
        self,
        sql: str,
    ) -> tuple[dict[str, Any], dict[str, str]] | None:
        """
        Fuzzy-match a SQL expression against getFunctions signatures (all categories).

        Prefers more-specific signatures (more literal tokens), e.g.
        ``extract(month from ${datetime})`` over ``extract(${unit} from ${date})``.
        Description is only a weak tie-breaker; signature drives the match.
        No LLM involved.
        """
        normalized = _normalize_signature_text(sql)
        if not normalized:
            return None

        best: tuple[dict[str, Any], dict[str, str], float] | None = None
        for entry in self.database_fn_by_key.values():
            signature = str(entry.get("signature") or "").strip()
            if not signature:
                continue
            compiled = _compile_signature_pattern(signature)
            if compiled is None:
                continue
            pattern, specificity = compiled
            match = pattern.fullmatch(normalized)
            if not match:
                continue
            captures = {k: (v or "").strip() for k, v in match.groupdict().items()}
            score = float(specificity)
            # Weak description boost when distinctive SQL tokens appear in description.
            desc = str(entry.get("description") or "").lower()
            if desc:
                skip = {"from", "as", "and", "or", "the", "for", "with", "into"}
                for token in re.findall(r"[a-z_]{3,}", normalized):
                    if token in skip:
                        continue
                    if token in desc:
                        score += 0.1
                        break
            if best is None or score > best[2]:
                best = (entry, captures, score)

        if best is None:
            return None
        return best[0], best[1]

    def _build_from_signature_match(
        self,
        expr: exp.Expression,
        *,
        dialect: str,
        database_name: str,
        table_alias: str,
        table_aliases: dict[str, str] | None,
    ) -> dict[str, Any] | None:
        sql_text = expr.sql(dialect=dialect)
        hit = self.match_signature(sql_text)
        if hit is None:
            return None
        entry, captures = hit
        kwargs = {
            "dialect": dialect,
            "database_name": database_name,
            "table_alias": table_alias,
            "table_aliases": table_aliases,
        }
        param_defs = [p for p in (entry.get("parameters") or []) if isinstance(p, dict)]
        args: list[exp.Expression] = []
        for param in param_defs:
            name = str(param.get("name") or "")
            text = captures.get(name, "").strip()
            if not text:
                return None
            args.append(_parse_capture_arg(text, dialect=dialect))
        return self._fill_catalog_entry(entry, args, **kwargs)

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

        # Extra SQL args must not be silently dropped (e.g. 3-arg CONCAT vs
        # catalog concat(${string1}, ${string2})). Fail → custom fallback.
        if len(args) > len(param_defs):
            label = str(entry.get("value") or entry.get("key") or "function")
            raise UnmappedDatabaseFunction(
                f"{label}: got {len(args)} args, catalog defines {len(param_defs)}"
            )

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


def to_wire_database_function_expression(
    catalog_dbf: dict[str, Any] | None,
    *,
    metadata: dict[str, Any] | None = None,
    dialect: str | None = None,
) -> str | None:
    """
    Convert catalog-shaped (or wire-onion) databaseFunction → Helical SQL string.

    Examples::
        MONTH("travel_details"."travel_date")
        LENGTH(CONCAT("travel_details"."destination", ' x'))
        to_char(DATETRUNC('MONTH', "travel_details"."travel_date"), YYYY Mon)

    Rules:
    - No space between function name and ``(``.
    - Column refs → dialect-quoted ``"table"."column"``.
    - If signature already wraps a param in quotes (e.g. ``'${formatMask}'``),
      pass the bare value (``YYYY Mon``) without adding quotes.
    - Other literals stay as stored (e.g. ``'MONTH'``).
    """
    if not catalog_dbf or not isinstance(catalog_dbf, dict):
        return None

    name = _dbf_display_name(catalog_dbf)
    if not name:
        return None

    resolved_dialect = dialect or str((metadata or {}).get("dialect") or "postgres")
    signature = str(catalog_dbf.get("signature") or "")
    params = catalog_dbf.get("parameters")
    args: list[str] = []

    if isinstance(params, list):
        for param in params:
            if not isinstance(param, dict):
                continue
            rendered = _render_dbf_param_expression(
                param,
                metadata=metadata,
                dialect=resolved_dialect,
                signature=signature,
            )
            if rendered is None:
                continue
            args.append(rendered)
    elif isinstance(params, dict):
        for pname, value in params.items():
            rendered = _render_dbf_value_expression(
                value,
                is_column=False,
                metadata=metadata,
                dialect=resolved_dialect,
                signature_already_quotes=_signature_quotes_param(signature, str(pname)),
            )
            if rendered is None:
                continue
            args.append(rendered)
    elif params is None and isinstance(catalog_dbf.get("value"), str):
        raw = str(catalog_dbf.get("value") or "").strip()
        if "(" in raw:
            return raw
        return f"{name}()"

    if not args:
        return f"{name}()"
    return f"{name}({', '.join(args)})"


def _dbf_display_name(dbf: dict[str, Any]) -> str:
    value = str(dbf.get("value") or "").strip()
    if value and "(" not in value:
        return value
    key = str(dbf.get("key") or dbf.get("functionName") or "").strip()
    if key:
        return key.rsplit(".", 1)[-1].upper()
    return value


def _signature_quotes_param(signature: str, param_name: str) -> bool:
    """True when signature already wraps ``${param}`` in quotes."""
    if not signature or not param_name:
        return False
    needle = "${" + param_name + "}"
    patterns = (
        rf"'\s*{re.escape(needle)}\s*'",
        rf'"\s*{re.escape(needle)}\s*"',
    )
    return any(re.search(p, signature, flags=re.IGNORECASE) for p in patterns)


def _render_dbf_param_expression(
    param: dict[str, Any],
    *,
    metadata: dict[str, Any] | None = None,
    dialect: str = "postgres",
    signature: str = "",
) -> str | None:
    name = str(param.get("name") or "")
    return _render_dbf_value_expression(
        param.get("value"),
        is_column=bool(param.get("column")),
        metadata=metadata,
        dialect=dialect,
        signature_already_quotes=_signature_quotes_param(signature, name),
    )


def _render_dbf_value_expression(
    value: Any,
    *,
    is_column: bool,
    metadata: dict[str, Any] | None = None,
    dialect: str = "postgres",
    signature_already_quotes: bool = False,
) -> str | None:
    if value is None:
        return None
    if isinstance(value, dict) and (
        value.get("key")
        or value.get("value")
        or value.get("functionName")
        or isinstance(value.get("parameters"), (list, dict))
    ):
        return to_wire_database_function_expression(
            value, metadata=metadata, dialect=dialect
        )

    text = str(value).strip()
    if not text:
        return None

    if is_column or (not _is_quoted_literal(text) and _looks_like_column_ref(text)):
        return _column_table_column_for_expression(text, metadata, dialect)

    if signature_already_quotes:
        if _is_quoted_literal(text):
            return text[1:-1]
        return text

    if _is_quoted_literal(text):
        return text

    if re.match(r"^[A-Za-z_][\w]*\s*\(", text):
        return re.sub(r"^([A-Za-z_][\w]*)\s*\(", r"\1(", text)

    if _looks_like_number(text) or text.upper() in {"NULL", "TRUE", "FALSE"}:
        return text

    return _quote_string_literal(text)


def _is_quoted_literal(text: str) -> bool:
    return (text.startswith("'") and text.endswith("'")) or (
        text.startswith('"') and text.endswith('"')
    )


def _column_table_column_for_expression(
    text: str,
    metadata: dict[str, Any] | None,
    dialect: str,
) -> str:
    """Resolve FQ / short path → dialect-quoted table.column (not alias)."""
    cleaned = text.strip().strip('"').strip("'")
    by_column = (metadata or {}).get("by_column") or {}
    table = ""
    column = ""
    for key in (cleaned, cleaned.split(".")[-1] if "." in cleaned else ""):
        if not key:
            continue
        hit = by_column.get(key)
        if isinstance(hit, dict):
            table = str(hit.get("table") or "").strip()
            column = str(hit.get("column") or hit.get("alias") or "").strip()
            if column:
                break
    if not column:
        parts = [p for p in cleaned.replace('"', "").split(".") if p]
        if len(parts) >= 2:
            table, column = parts[-2], parts[-1]
        elif parts:
            column = parts[-1]
    if table and column:
        return f"{_quote_ident(table, dialect)}.{_quote_ident(column, dialect)}"
    if column:
        return _quote_ident(column, dialect)
    return cleaned


def _quote_ident(name: str, dialect: str) -> str:
    """Dialect-aware identifier quoting for table/column segments."""
    ident = str(name or "").strip().strip('"').strip("`").strip("[]")
    if not ident:
        return ident
    d = (dialect or "postgres").lower()
    if d in {"mysql", "mariadb", "hive", "spark", "bigquery"}:
        return "`" + ident.replace("`", "``") + "`"
    if d in {"tsql", "mssql", "sqlserver"}:
        return "[" + ident.replace("]", "]]") + "]"
    return '"' + ident.replace('"', '""') + '"'


def _looks_like_column_ref(text: str) -> bool:
    """Guess FQ column paths (contain ``.``) when ``column`` flag is absent."""
    if not text or any(ch in text for ch in "()'"):
        return False
    return "." in text


def _looks_like_number(text: str) -> bool:
    return bool(re.fullmatch(r"[-+]?\d+(\.\d+)?", text))


def _quote_string_literal(text: str) -> str:
    return "'" + text.replace("'", "''") + "'"


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


def _normalize_signature_text(text: str) -> str:
    """Lowercase + collapse whitespace for signature comparison."""
    return re.sub(r"\s+", " ", str(text or "").strip().lower())


def _compile_signature_pattern(signature: str) -> tuple[re.Pattern[str], int] | None:
    """
    Turn ``extract(month from ${datetime})`` into a regex with named groups.

    Specificity = count of literal word tokens (prefer MONTH over generic EXTRACT).
    """
    sig = _normalize_signature_text(signature)
    if not sig:
        return None

    parts: list[str] = []
    specificity = 0
    pos = 0
    for match in re.finditer(r"\$\{([^}]+)\}", sig):
        literal = sig[pos : match.start()]
        if literal:
            specificity += len(re.findall(r"[a-z0-9_]+", literal))
            parts.append(_escape_signature_literal(literal))
        name = re.sub(r"[^a-zA-Z0-9_]", "_", match.group(1))
        if not name:
            return None
        # Non-greedy capture; commas/parens balanced enough for flat signatures.
        parts.append(f"(?P<{name}>.+?)")
        pos = match.end()
    trailing = sig[pos:]
    if trailing:
        specificity += len(re.findall(r"[a-z0-9_]+", trailing))
        parts.append(_escape_signature_literal(trailing))
    if not parts:
        return None
    try:
        return re.compile("^" + "".join(parts) + "$", re.IGNORECASE), specificity
    except re.error:
        return None


def _escape_signature_literal(literal: str) -> str:
    """Escape literal signature text but keep whitespace flexible."""
    escaped = re.escape(literal)
    return escaped.replace(r"\ ", r"\s+")


def _parse_capture_arg(text: str, *, dialect: str) -> exp.Expression:
    """Parse a signature-capture fragment back into a sqlglot expression."""
    try:
        parsed = parse_one(text, read=dialect)
        if isinstance(parsed, exp.Expression):
            return parsed
    except Exception:
        pass
    # Bare identifier / dotted column
    cleaned = text.strip().strip("'\"")
    if re.fullmatch(r"[A-Za-z_][\w.]*(?:\"[^\"]+\")?", cleaned):
        parts = [p for p in cleaned.replace('"', "").split(".") if p]
        if len(parts) >= 2:
            return exp.column(parts[-1], table=".".join(parts[:-1]))
        if parts:
            return exp.column(parts[0])
    return exp.Literal.string(cleaned)


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
