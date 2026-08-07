"""Deterministic VizModel construction (no LLM).

Builds shelves, chart type, and properties from SQL result metadata,
cube format strings, and optional user chart hints already on ModelState.
"""
from __future__ import annotations

import logging
import re
from typing import Any, Optional

from helicalbi.common.CubeInfoModel import (
    build_viz_column_context,
    extract_result_field_names,
)
from helicalbi.model.output.viz.ChartSettings import ChartSettings, DimensionSetting
from helicalbi.model.output.viz.VizModel import (
    VizChart,
    VizData,
    VizFilter,
    VizModel,
    VizProperties,
)
from helicalbi.viz._chart_selection import (
    _MEASURE_TOKENS,
    infer_chart_shape,
    possible_chart_options,
    resolve_similar_charts,
)
from helicalbi.viz._charts import get_chart_definition, resolve_chart_name

logger = logging.getLogger(__name__)

# Prefer common charts when several options match the data shape.
_CHART_PREFERENCE = (
    "column",
    "bar",
    "line",
    "area",
    "grouped_column",
    "stacked_column",
    "pie",
    "donut",
    "kpi",
    "table",
    "other",
)

_META_ONLY_KEYS = frozenset({"rows", "row_count", "rowcount", "count", "total_rows"})
_TYPE_KEYS = ("type", "data_type", "dataType", "dtype", "columnType")


def _unique(names: list[str]) -> list[str]:
    seen: set[str] = set()
    out: list[str] = []
    for name in names:
        text = str(name or "").strip()
        if not text:
            continue
        key = text.lower()
        if key in seen:
            continue
        seen.add(key)
        out.append(text)
    return out


def _type_token(raw: Any) -> str:
    if raw is None:
        return ""
    if isinstance(raw, dict):
        for key in _TYPE_KEYS:
            if key in raw and raw[key] is not None:
                return _type_token(raw[key])
        for key, value in raw.items():
            if isinstance(value, str) and value.strip():
                return value.strip().lower()
            key_l = str(key).lower()
            if any(
                part in key_l
                for part in (
                    "integer",
                    "long",
                    "short",
                    "float",
                    "double",
                    "decimal",
                    "bigdecimal",
                    "number",
                )
            ):
                return "numeric"
            if "string" in key_l or "char" in key_l:
                return "text"
        return ""
    return str(raw).strip().lower()


def _is_measure_token(token: str) -> bool:
    if not token:
        return False
    return token in _MEASURE_TOKENS or any(t in token for t in _MEASURE_TOKENS)


def _name_from_desc(desc: Any, *, fallback: str = "") -> str:
    """Prefer nested column descriptor names over index keys (e.g. ``\"1\"``)."""
    if isinstance(desc, dict):
        for key in ("name", "alias", "alias_name", "column", "column_name", "label"):
            text = str(desc.get(key) or "").strip()
            if text:
                return text
    return str(fallback or "").strip()


def _iter_named_roles(data_types: Any):
    """Yield (name, role) from executeQuery-style metadata.

    Supports shapes such as::

        [{'1': {'name': 'Travel Type', 'type': 'text'}}, {'rows': 5}]
        [{'name': 'Travel Type', 'type': 'text'}, {'rows': 5}]
    """
    if data_types is None:
        return

    def _yield_desc(desc: Any, *, fallback: str = ""):
        name = _name_from_desc(desc, fallback=fallback)
        if not name:
            return
        token = _type_token(desc)
        yield name, ("measure" if _is_measure_token(token) else "dimension")

    if isinstance(data_types, list):
        for item in data_types:
            if not isinstance(item, dict):
                continue
            keys = {str(k).lower() for k in item.keys()}
            if keys and keys <= _META_ONLY_KEYS:
                continue
            # Direct column descriptor: {"name": "...", "type": "text"}
            if any(k in item for k in ("name", "alias", "column_name", "type", "data_type")):
                yield from _yield_desc(item)
                continue
            # Indexed map: {"1": {"name": "...", "type": "text"}, ...}
            for key, value in item.items():
                if str(key).lower() in _META_ONLY_KEYS:
                    continue
                yield from _yield_desc(value, fallback=str(key))
        return
    if isinstance(data_types, dict):
        keys = {str(k).lower() for k in data_types.keys()}
        if keys and keys <= _META_ONLY_KEYS:
            return
        if any(k in data_types for k in ("name", "alias", "column_name", "type", "data_type")):
            yield from _yield_desc(data_types)
            return
        for key, value in data_types.items():
            if str(key).lower() in _META_ONLY_KEYS:
                continue
            yield from _yield_desc(value, fallback=str(key))


def _pick_chart_type(
    data_types: Any,
    *,
    viz_hint: str = "",
    user_query: str = "",
) -> str:
    """Choose a catalog visualization_type without calling the LLM."""
    hint = resolve_chart_name(viz_hint) if viz_hint else None
    if hint:
        return hint

    query = (user_query or "").lower()
    for alias, name in (
        ("pie", "pie"),
        ("donut", "donut"),
        ("line", "line"),
        ("area", "area"),
        ("scatter", "scatter"),
        ("heatmap", "heatmap"),
        ("kpi", "kpi"),
        ("table", "table"),
        ("bar", "bar"),
        ("column", "column"),
    ):
        if re.search(rf"\b{alias}\b", query):
            resolved = resolve_chart_name(name)
            if resolved:
                return resolved

    dims, measures, ordered = infer_chart_shape(data_types)
    options = possible_chart_options(dims, measures, ordered)
    if not options:
        return "table"

    by_name = {opt.visualization_type: opt for opt in options}
    for preferred in _CHART_PREFERENCE:
        if preferred in by_name:
            return preferred
    return options[0].visualization_type


def _chart_viz_and_mark(chart_type: str) -> VizChart:
    """Map catalog type → VizChart(viz=component, mark=type/family mark)."""
    chart_def = get_chart_definition(chart_type)
    component = ""
    family = ""
    if chart_def and chart_def.conversion:
        component = str(chart_def.conversion.component or "").strip()
        family = str(chart_def.conversion.family or "").strip()
    viz = component or chart_type.replace("_", " ").title()
    mark = chart_type if chart_type else family or "bar"
    if family == "pie" and not component:
        viz = "arc"
    return VizChart(viz=viz, mark=mark)


def _wire_column_path(column: Any) -> str:
    """Normalize formData column field (string or ``{name, id}``) to a path string."""
    if isinstance(column, dict):
        return str(column.get("name") or "").strip()
    return str(column or "").strip()


def _display_name(column_wire: dict) -> str:
    """Prefer SELECT alias (matches result headers); else leaf of column path."""
    alias = str(column_wire.get("alias") or "").strip()
    if alias:
        return alias
    path = _wire_column_path(column_wire.get("column"))
    if not path:
        return ""
    return path.rsplit(".", 1)[-1].strip()


def _align_to_result_fields(names: list[str], result_fields: list[str]) -> list[str]:
    """Map sql_to_formdata aliases onto executeQuery header names when possible."""
    if not names:
        return []
    if not result_fields:
        return _unique(names)
    index = {f.lower(): f for f in result_fields if f}
    aligned: list[str] = []
    for name in names:
        key = str(name or "").strip()
        if not key:
            continue
        canonical = index.get(key.lower())
        if not canonical:
            leaf = key.rsplit(".", 1)[-1].strip().lower()
            canonical = index.get(leaf)
        aligned.append(canonical or key)
    return _unique(aligned)


def _shelves_from_form_data(
    form_data: dict[str, Any],
    *,
    result_fields: Optional[list[str]] = None,
) -> tuple[list[str], list[str], list[str], list[VizFilter]]:
    """Map sql_to_formdata wire columns/filters → VizModel shelves."""
    rows: list[str] = []
    columns: list[str] = []
    hidden: list[str] = []

    for col in form_data.get("columns") or []:
        if not isinstance(col, dict):
            continue
        name = _display_name(col)
        if not name:
            continue
        if col.get("hidden"):
            hidden.append(name)
            continue
        is_measure = bool(col.get("aggregate")) or (
            str(col.get("fieldType") or "").lower() == "measure"
        )
        if is_measure:
            columns.append(name)
        else:
            rows.append(name)

    filters: list[VizFilter] = []
    for bucket in ("filters", "having"):
        for item in form_data.get(bucket) or []:
            if not isinstance(item, dict):
                continue
            name = _filter_name_from_form_item(item)
            if not name:
                continue
            values = item.get("values")
            if isinstance(values, list):
                if len(values) == 1:
                    value: Any = values[0]
                else:
                    value = values
            else:
                value = values if values is not None else ""
            condition = str(
                item.get("condition")
                or item.get("customCondition")
                or ""
            ).strip()
            filters.append(VizFilter(name=name, value=value, condition=condition))

    rows = _align_to_result_fields(rows, result_fields or [])
    columns = _align_to_result_fields(columns, result_fields or [])
    hidden = _align_to_result_fields(hidden, result_fields or [])
    return _unique(rows), _unique(columns), _unique(hidden), filters


def _try_sql_to_form_data(
    sql: str,
    *,
    session_cookie: str = "",
    md_location: str = "",
    md_file_name: str = "",
    dialect: str | None = None,
) -> Optional[dict[str, Any]]:
    """Run sql_to_formdata when metadata refs are available; else None."""
    text = (sql or "").strip()
    location = (md_location or "").strip()
    file_name = (md_file_name or "").strip()
    if not text or not location or not file_name:
        logger.info(
            "viz_model_fill: sql_to_formdata skipped "
            "(sql=%s location=%s file=%s)",
            bool(text),
            bool(location),
            bool(file_name),
        )
        return None
    try:
        from helicalbi.sql_to_formdata import sql_to_form_data

        form_data = sql_to_form_data(
            text,
            location=location,
            metadata_dir=location,
            metadata_file_name=file_name,
            session_cookie=session_cookie or "",
            dialect=dialect,
        )
        logger.info(
            "viz_model_fill: sql_to_formdata ok columns=%s filters=%s",
            len(form_data.get("columns") or []),
            len(form_data.get("filters") or []),
        )
        return form_data
    except Exception:
        logger.exception("viz_model_fill: sql_to_formdata failed; using metadata fallback")
        return None


def _shelves_from_metadata(
    data_types: Any,
    *,
    sample_row: Optional[dict] = None,
) -> tuple[list[str], list[str]]:
    """Infer rows (dimensions) and columns (measures) from result metadata."""
    rows: list[str] = []
    columns: list[str] = []
    for name, role in _iter_named_roles(data_types):
        if role == "measure":
            columns.append(name)
        else:
            rows.append(name)
    rows, columns = _unique(rows), _unique(columns)
    if rows or columns:
        return rows, columns

    if isinstance(sample_row, dict) and sample_row:
        dims: list[str] = []
        measures: list[str] = []
        for key, value in sample_row.items():
            name = str(key or "").strip()
            if not name:
                continue
            if isinstance(value, bool):
                dims.append(name)
            elif isinstance(value, (int, float)):
                measures.append(name)
            else:
                dims.append(name)
        if measures or dims:
            return _unique(dims), _unique(measures)

    field_names = extract_result_field_names(data_types)
    if not field_names:
        return [], []
    if len(field_names) == 1:
        return [], _unique(field_names)
    return _unique(field_names[:-1]), _unique(field_names[-1:])


_CONDITION_ALIASES = {
    "EQ": "EQ",
    "NEQ": "NEQ",
    "NEQ_": "NEQ",
    "GT": "GT",
    "GTE": "GTE",
    "LT": "LT",
    "LTE": "LTE",
    "LIKE": "LIKE",
    "ILIKE": "ILIKE",
    "IN": "IN",
    "BETWEEN": "BETWEEN",
    "IS": "IS",
}


def _sql_literal_value(node: Any) -> Any:
    """Normalize a sqlglot literal / expression into a JSON-friendly value."""
    try:
        from sqlglot import exp

        if node is None:
            return ""
        if isinstance(node, exp.Null):
            return None
        if isinstance(node, exp.Boolean):
            return bool(node.this)
        if isinstance(node, exp.Literal):
            text = node.this
            if node.is_string:
                return text
            try:
                if "." in str(text):
                    return float(text)
                return int(text)
            except (TypeError, ValueError):
                return text
        if isinstance(node, (list, tuple)):
            return [_sql_literal_value(item) for item in node]
        if hasattr(node, "sql"):
            return str(node.sql()).strip("'\"")
    except Exception:
        pass
    return str(node or "").strip("'\"")


def _select_alias_index(tree: Any, dialect: str | None) -> dict[str, str]:
    """Map physical / expression SQL → SELECT alias for filter naming."""
    from sqlglot import exp

    index: dict[str, str] = {}
    if tree is None:
        return index
    for projection in tree.expressions or []:
        alias = ""
        expr = projection
        if isinstance(projection, exp.Alias):
            alias = str(projection.alias or "").strip()
            expr = projection.this
        elif isinstance(projection, exp.Column) and projection.alias:
            alias = str(projection.alias).strip()
        if not alias:
            continue
        # Expression key (ANSI form)
        try:
            expr_sql = expr.sql(dialect=dialect) if dialect else expr.sql()
        except Exception:
            expr_sql = str(expr)
        if expr_sql:
            index[expr_sql.lower()] = alias
            index[re.sub(r"\s+", " ", expr_sql).strip().lower()] = alias
        # Column leaf keys
        if isinstance(expr, exp.Column):
            col = str(expr.name or "").strip()
            table = str(expr.table or "").strip()
            if col:
                index[col.lower()] = alias
            if table and col:
                index[f"{table}.{col}".lower()] = alias
        # Nested column inside EXTRACT / TO_CHAR / etc.
        col_node = expr.find(exp.Column) if hasattr(expr, "find") else None
        if isinstance(col_node, exp.Column):
            col = str(col_node.name or "").strip()
            table = str(col_node.table or "").strip()
            if col and col.lower() not in index:
                index[col.lower()] = alias
            if table and col:
                index.setdefault(f"{table}.{col}".lower(), alias)
    return index


def _filter_lhs_name(
    left: Any,
    *,
    dialect: str | None,
    alias_index: dict[str, str],
) -> str:
    """Name for a WHERE/HAVING left-hand side.

    - ANSI / function expressions → full SQL text
    - Plain columns → SELECT alias when available, else column name
    """
    from sqlglot import exp

    if left is None:
        return ""
    if isinstance(left, exp.Paren):
        return _filter_lhs_name(left.this, dialect=dialect, alias_index=alias_index)

    try:
        left_sql = left.sql(dialect=dialect) if dialect else left.sql()
    except Exception:
        left_sql = str(left)
    left_sql = re.sub(r"\s+", " ", str(left_sql or "")).strip()

    # Plain column → prefer SELECT alias
    if isinstance(left, (exp.Column, exp.Identifier)):
        col = str(getattr(left, "name", None) or left_sql).strip()
        table = str(getattr(left, "table", None) or "").strip()
        for key in (
            f"{table}.{col}".lower() if table and col else "",
            col.lower(),
            left_sql.lower(),
        ):
            if key and key in alias_index:
                return alias_index[key]
        return col or left_sql

    # Function / ANSI expression → keep full SQL; only swap when SELECT
    # aliases the *same* expression (e.g. EXTRACT(...) AS "MONTH").
    for key in (left_sql.lower(), re.sub(r"\s+", " ", left_sql).strip().lower()):
        if key in alias_index:
            # Prefer alias only when it is clearly the projection alias for
            # this exact expression; still allow callers that want alias.
            # User rule: "if ansi then full otherwise alias" → keep full.
            return left_sql
    return left_sql


def _predicate_to_viz_filter(
    node: Any,
    *,
    dialect: str | None,
    alias_index: dict[str, str],
) -> Optional[VizFilter]:
    """Convert a single sqlglot predicate into a VizFilter."""
    from sqlglot import exp

    if node is None:
        return None
    if isinstance(node, exp.Paren):
        return _predicate_to_viz_filter(
            node.this, dialect=dialect, alias_index=alias_index
        )
    if isinstance(node, exp.Not):
        inner = _predicate_to_viz_filter(
            node.this, dialect=dialect, alias_index=alias_index
        )
        if inner is None:
            return None
        cond = inner.condition or "EQ"
        if not cond.upper().startswith("NOT_"):
            inner.condition = f"NOT_{cond}"
        return inner

    if isinstance(node, exp.Between):
        name = _filter_lhs_name(node.this, dialect=dialect, alias_index=alias_index)
        low = _sql_literal_value(node.args.get("low"))
        high = _sql_literal_value(node.args.get("high"))
        return VizFilter(name=name, value=[low, high], condition="BETWEEN")

    if isinstance(node, exp.In):
        name = _filter_lhs_name(node.this, dialect=dialect, alias_index=alias_index)
        values = [_sql_literal_value(v) for v in (node.expressions or [])]
        negated = bool(node.args.get("not"))
        return VizFilter(
            name=name,
            value=values if len(values) != 1 else values[0],
            condition="NOT_IN" if negated else "IN",
        )

    if isinstance(node, exp.Is):
        name = _filter_lhs_name(node.this, dialect=dialect, alias_index=alias_index)
        nullish = isinstance(node.expression, exp.Null)
        negated = bool(node.args.get("not"))
        if nullish:
            return VizFilter(
                name=name,
                value=None,
                condition="IS_NOT_NULL" if negated else "IS_NULL",
            )
        return VizFilter(
            name=name,
            value=_sql_literal_value(node.expression),
            condition="IS",
        )

    if isinstance(node, (exp.Like, exp.ILike)):
        name = _filter_lhs_name(node.this, dialect=dialect, alias_index=alias_index)
        return VizFilter(
            name=name,
            value=_sql_literal_value(node.expression),
            condition="ILIKE" if isinstance(node, exp.ILike) else "LIKE",
        )

    if isinstance(node, exp.Binary) and not isinstance(node, (exp.And, exp.Or)):
        left, right = node.left, node.right
        # Literal on the left → flip so name comes from the expression side.
        if isinstance(left, exp.Literal) and not isinstance(right, exp.Literal):
            left, right = right, left
        name = _filter_lhs_name(left, dialect=dialect, alias_index=alias_index)
        if not name:
            return None
        op = type(node).__name__.upper()
        condition = _CONDITION_ALIASES.get(op, op)
        return VizFilter(
            name=name,
            value=_sql_literal_value(right),
            condition=condition,
        )
    return None


def _flatten_sql_predicates(node: Any) -> list[Any]:
    """Flatten AND/OR trees into leaf predicates (WHERE / HAVING)."""
    from sqlglot import exp

    if node is None:
        return []
    if isinstance(node, exp.Paren):
        return _flatten_sql_predicates(node.this)
    if isinstance(node, (exp.And, exp.Or)):
        return _flatten_sql_predicates(node.left) + _flatten_sql_predicates(node.right)
    return [node]


def _extract_filters_from_sql(
    sql: str,
    *,
    dialect: str | None = None,
) -> list[VizFilter]:
    """Extract VizFilters from WHERE and HAVING.

    Naming rules:
    - ANSI / function LHS (EXTRACT, TO_CHAR, SUM, …) → full expression SQL
    - Plain column LHS → SELECT alias when present, else column name
    """
    text = (sql or "").strip()
    if not text:
        return []
    try:
        import sqlglot
        from sqlglot import exp

        read_dialect = dialect or None
        # Strip markdown fences if present.
        if text.startswith("```"):
            text = re.sub(r"^```(?:sql)?\s*", "", text, flags=re.I)
            text = re.sub(r"\s*```$", "", text)

        try:
            tree = sqlglot.parse_one(text, read=read_dialect)
        except Exception:
            tree = sqlglot.parse_one(text)
        if tree is None:
            return []

        alias_index = _select_alias_index(tree, read_dialect)
        filters: list[VizFilter] = []
        seen: set[tuple[str, str, str]] = set()

        for clause in (tree.find(exp.Where), tree.find(exp.Having)):
            if clause is None:
                continue
            root = clause.this if hasattr(clause, "this") else clause
            for predicate in _flatten_sql_predicates(root):
                item = _predicate_to_viz_filter(
                    predicate, dialect=read_dialect, alias_index=alias_index
                )
                if item is None or not item.name:
                    continue
                key = (
                    item.name.lower(),
                    str(item.condition or "").upper(),
                    str(item.value),
                )
                if key in seen:
                    continue
                seen.add(key)
                filters.append(item)
        return filters
    except Exception:
        logger.debug("viz_model_fill: SQL filter extract skipped", exc_info=True)
        return []


def _filter_name_from_form_item(item: dict[str, Any]) -> str:
    """Pick VizFilter.name from a sql_to_formdata filter/having wire item.

    Prefer full ANSI expression when the wire column/customCondition looks
    like SQL; otherwise use alias/label.
    """
    alias = str(item.get("alias") or item.get("label") or "").strip()
    column = _wire_column_path(item.get("column"))
    custom = str(item.get("customCondition") or item.get("custom_sql") or "").strip()

    # Explicit expression-looking column (custom SELECT / formula filters).
    if column and _looks_like_ansi_expression(column):
        return column
    if custom and _looks_like_ansi_expression(custom) and item.get("condition") == "CUSTOM":
        # customCondition is often just an operator ("<>", "IN ("); skip those.
        if any(ch.isalpha() for ch in custom):
            return custom
    # SQL extractor is authoritative when available; form-data fallback uses alias.
    if alias:
        return alias
    if column:
        if "." in column and not _looks_like_ansi_expression(column):
            return column.rsplit(".", 1)[-1]
        return column
    return ""


def _looks_like_ansi_expression(text: str) -> bool:
    value = (text or "").strip()
    if not value:
        return False
    upper = value.upper()
    if any(
        token in upper
        for token in (
            "EXTRACT(",
            "TO_CHAR(",
            "TO_DATE(",
            "CAST(",
            "COALESCE(",
            "NULLIF(",
            "CASE ",
            "SUM(",
            "AVG(",
            "COUNT(",
            "MIN(",
            "MAX(",
            "DATE_TRUNC(",
            "DATE_PART(",
        )
    ):
        return True
    if "(" in value and ")" in value and not value.startswith("("):
        return True
    return False


def _resolve_shelves(
    *,
    data_types: Any,
    sql: str = "",
    sample_row: Optional[dict] = None,
    session_cookie: str = "",
    md_location: str = "",
    md_file_name: str = "",
    dialect: str | None = None,
) -> tuple[list[str], list[str], list[str], list[VizFilter], Optional[dict[str, Any]]]:
    """Prefer sql_to_formdata for shelves; filters come from WHERE/HAVING SQL."""
    result_fields = extract_result_field_names(data_types)
    form_data = _try_sql_to_form_data(
        sql,
        session_cookie=session_cookie,
        md_location=md_location,
        md_file_name=md_file_name,
        dialect=dialect,
    )
    # Always prefer SQL-derived filters so ANSI expressions (EXTRACT/…) keep
    # their full LHS text and plain columns resolve to SELECT aliases.
    sql_filters = _extract_filters_from_sql(sql, dialect=dialect)

    if form_data:
        rows, columns, hidden, form_filters = _shelves_from_form_data(
            form_data, result_fields=result_fields
        )
        filters = sql_filters or form_filters
        if rows or columns:
            used = {n.lower() for n in rows + columns + hidden}
            for field in result_fields:
                if field.lower() not in used:
                    hidden.append(field)
            return rows, columns, _unique(hidden), filters, form_data

    rows, columns = _shelves_from_metadata(data_types, sample_row=sample_row)
    used = {n.lower() for n in rows + columns}
    hidden = [n for n in result_fields if n.lower() not in used]
    return rows, columns, hidden, sql_filters, form_data


def _default_title(rows: list[str], columns: list[str], vf_title: str = "") -> str:
    if (vf_title or "").strip():
        return vf_title.strip()
    meas = columns[0] if columns else ""
    dim = rows[0] if rows else ""
    if meas and dim:
        return f"{meas} by {dim}"
    return meas or dim or "Visualization"


def build_viz_model(
    *,
    data_types: Any,
    sql: str = "",
    sample_row: Optional[dict] = None,
    viz_hint: str = "",
    user_query: str = "",
    vf_title: str = "",
    format_strings: Optional[dict[str, str]] = None,
    cube_metadata: Optional[list] = None,
    ai_instructions: Optional[dict] = None,
    sort_orders: Optional[list] = None,
    domain_context: str = "",
    session_cookie: str = "",
    md_location: str = "",
    md_file_name: str = "",
    dialect: str | None = None,
) -> tuple[VizModel, str, dict[str, Any]]:
    """Build a VizModel and related viz context.

    Rows / columns / filters prefer ``sql_to_formdata`` (same path as instant-to-hr).
    Returns ``(viz_model, chart_type, viz_column_context)``.
    """
    chart_type = _pick_chart_type(
        data_types, viz_hint=viz_hint, user_query=user_query
    )
    rows, columns, hidden, filters, form_data = _resolve_shelves(
        data_types=data_types,
        sql=sql,
        sample_row=sample_row,
        session_cookie=session_cookie,
        md_location=md_location,
        md_file_name=md_file_name,
        dialect=dialect,
    )

    chart_def = get_chart_definition(chart_type)
    if chart_def and chart_def.option:
        opt = chart_def.option
        if opt.dims_max and len(rows) > opt.dims_max:
            # Overflow dimensions become hidden rather than dropped silently.
            overflow = rows[opt.dims_max :]
            rows = rows[: opt.dims_max]
            hidden = _unique(list(hidden) + overflow)
        if opt.measures_max and len(columns) > opt.measures_max:
            overflow = columns[opt.measures_max :]
            columns = columns[: opt.measures_max]
            hidden = _unique(list(hidden) + overflow)

    viz_context = build_viz_column_context(
        data_types,
        cube_metadata=cube_metadata or [],
        format_strings=format_strings or {},
        ai_instructions=ai_instructions or {},
        sort_orders=sort_orders or [],
        domain_context=domain_context or "",
    )
    if form_data is not None:
        viz_context = dict(viz_context)
        viz_context["form_data"] = form_data

    formatting = dict(viz_context.get("format_strings") or {})
    if formatting and (rows or columns):
        bound = {n.lower() for n in rows + columns}
        formatting = {
            key: value
            for key, value in formatting.items()
            if str(key).lower() in bound
        }

    title = _default_title(rows, columns, vf_title=vf_title)
    labels_x = rows[0] if rows else None
    labels_y = columns[0] if columns else (rows[1] if len(rows) > 1 else None)

    model = VizModel(
        data=VizData(
            rows=rows,
            columns=columns,
            filters=filters,
            hidden=hidden,
        ),
        chart=_chart_viz_and_mark(chart_type),
        properties=VizProperties(
            labelsX=labels_x,
            labelsY=labels_y,
            title=title,
            color="",
            formatting=formatting,
        ),
    )
    logger.info(
        "viz_model_fill built chart=%s viz=%s mark=%s rows=%s columns=%s "
        "filters=%s source=%s",
        chart_type,
        model.chart.viz,
        model.chart.mark,
        rows,
        columns,
        len(model.data.filters),
        "sql_to_formdata" if form_data is not None else "metadata",
    )
    return model, chart_type, viz_context


def viz_model_to_chart_settings(model: VizModel) -> ChartSettings:
    """Bridge VizModel shelves/properties → ChartSettings for VF injection."""
    props = model.properties
    color = props.color or None
    if (not color) and props.colorGradient:
        color = list(props.colorGradient)
    return ChartSettings(
        dimensions=DimensionSetting(names=list(model.data.rows or [])),
        measures=list(model.data.columns or []),
        labelsX=props.labelsX,
        labelsY=props.labelsY,
        title=props.title,
        color=color,
        measure_formats=dict(props.formatting or {}),
    )


def merge_properties_polish(model: VizModel, polish) -> VizModel:
    """Merge LLM polish fields into an existing VizModel (shelves/chart stay frozen)."""
    from helicalbi.model.output.viz.VizModel import VizProperties

    current = model.properties.model_dump()
    incoming = (
        polish.model_dump(exclude_none=True)
        if hasattr(polish, "model_dump")
        else {k: v for k, v in dict(polish or {}).items() if v is not None}
    )
    # Do not let polish wipe deterministic Excel-style formatting.
    incoming.pop("formatting", None)

    for key in ("title", "labelsX", "labelsY"):
        text = str(incoming.get(key) or "").strip()
        if text:
            current[key] = text
        incoming.pop(key, None)

    if "color" in incoming:
        current["color"] = incoming.pop("color") or ""
    if "colorGradient" in incoming:
        gradient = incoming.pop("colorGradient") or []
        current["colorGradient"] = list(gradient) or None
    if "theme" in incoming:
        current["theme"] = incoming.pop("theme") or None
    if "background" in incoming:
        current["background"] = incoming.pop("background") or None

    formatter = incoming.pop("formatter", None) or {}
    if isinstance(formatter, dict) and formatter:
        merged = dict(current.get("formatter") or {})
        for key, value in formatter.items():
            name = str(key or "").strip()
            body = str(value or "").strip()
            if name and body:
                merged[name] = body
        current["formatter"] = merged

    # Preserve unknown polish keys on properties (extra="allow").
    for key, value in incoming.items():
        current[key] = value

    model.properties = VizProperties.model_validate(current)
    return model


def resolve_similar_for_model(chart_type: str, data_types: Any) -> list:
    return resolve_similar_charts(chart_type, data_types=data_types)
