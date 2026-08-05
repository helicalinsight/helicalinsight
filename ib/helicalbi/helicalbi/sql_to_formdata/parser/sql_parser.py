"""sqlglot-based SQL breakdown into ParsedQuery."""

from __future__ import annotations

from typing import Any

import sqlglot
from sqlglot import exp

from ..functions_catalog import FunctionCatalog, REFERENCE_TO_SQLGLOT
from ..mappings.conditions import sql_op_to_ui_condition
from ..models import ColumnRef, FilterItem, OrderItem, ParsedQuery, SelectItem


def _sqlglot_dialect(dialect: str | None, catalog: FunctionCatalog | None = None) -> str:
    """Map API reference (postgresql) → sqlglot dialect (postgres)."""
    if dialect:
        key = str(dialect).lower().strip()
        return REFERENCE_TO_SQLGLOT.get(key, key)
    if catalog is not None:
        return catalog.dialect
    return "postgres"


def parse_sql(
    sql: str,
    dialect: str = "postgres",
    catalog: FunctionCatalog | None = None,
    database_name: str = "",
) -> ParsedQuery:
    if catalog is None:
        raise ValueError(
            "FunctionCatalog is required. Call getFunctions (/service) before parsing SQL."
        )

    dialect = _sqlglot_dialect(dialect or catalog.dialect, catalog)
    tree = sqlglot.parse_one(sql, read=dialect)
    if not isinstance(tree, exp.Select):
        raise ValueError("Only SELECT statements are supported")

    parsed = ParsedQuery(
        dialect=dialect,
        function_catalog=catalog,
        sql=tree.sql(dialect=dialect),
        database_name=database_name or "",
    )
    _extract_from(tree, parsed)
    # Prefer metadata catalog.schema when provided (wire FQ names).
    if database_name:
        parsed.database_name = database_name
    parsed.selects = [_parse_select_expr(e, parsed) for e in tree.expressions]

    group = tree.args.get("group")
    if group:
        parsed.group_by = [_column_ref(e, parsed) for e in group.expressions]
    else:
        parsed.group_by = []

    where = tree.args.get("where")
    if where:
        parsed.where_filters = _flatten_predicates(where.this, parsed, for_having=False)

    having = tree.args.get("having")
    if having:
        parsed.having_filters = _flatten_predicates(having.this, parsed, for_having=True)

    order = tree.args.get("order")
    if order:
        for ordered in order.expressions:
            parsed.order_by.append(
                OrderItem(
                    alias_or_column=_expr_alias_or_sql(ordered.this, parsed),
                    direction="desc" if ordered.args.get("desc") else "asc",
                )
            )

    limit = tree.args.get("limit")
    if limit and limit.expression:
        parsed.limit = int(limit.expression.this)

    offset = tree.args.get("offset")
    if offset and offset.expression:
        parsed.offset = int(offset.expression.this)

    return parsed


def _catalog(parsed: ParsedQuery) -> FunctionCatalog:
    return parsed.function_catalog


def _extract_from(tree: exp.Select, parsed: ParsedQuery) -> None:
    from_ = tree.args.get("from_")
    if not from_:
        return
    table = from_.this
    if isinstance(table, exp.Table):
        parts = [p.name for p in table.parts if isinstance(p, exp.Identifier)]
        if not parts and table.name:
            parts = [table.name]
        # "db"."schema"."table" or "table"
        if len(parts) >= 3:
            parsed.database_name = f"{parts[0]}.{parts[1]}"
            parsed.table_name = parts[-1]
        elif len(parts) == 2:
            parsed.database_name = parts[0]
            parsed.table_name = parts[1]
        elif len(parts) == 1:
            parsed.table_name = parts[0]
        parsed.table_alias = table.alias_or_name or parsed.table_name

    # Register FROM + JOIN tables so column refs can resolve aliases → real names.
    for t in tree.find_all(exp.Table):
        _register_table(parsed, t)


def _register_table(parsed: ParsedQuery, table: exp.Table) -> None:
    name = table.name or ""
    if not name:
        return
    alias = table.alias_or_name or name
    parsed.table_aliases[alias] = name
    parsed.table_aliases[name] = name


def _resolve_table_name(table: str | None, parsed: ParsedQuery) -> str | None:
    """Map SQL alias (td) to physical table name (travel_details)."""
    if not table:
        return parsed.table_name or parsed.table_alias or None
    return parsed.table_aliases.get(table, table)


def _column_ref(node: exp.Expression, parsed: ParsedQuery) -> ColumnRef:
    col = node
    if isinstance(node, exp.Alias):
        col = node.this
    if isinstance(col, exp.Column):
        raw_table = col.table or parsed.table_alias or parsed.table_name
        table = _resolve_table_name(raw_table or None, parsed)
        return ColumnRef(table=table or None, name=col.name, catalog=parsed.database_name or None)
    # fallback: treat whole expression as name
    table = _resolve_table_name(parsed.table_alias or parsed.table_name or None, parsed)
    return ColumnRef(table=table or None, name=col.sql(dialect=parsed.dialect), catalog=parsed.database_name or None)


def _parse_select_expr(node: exp.Expression, parsed: ParsedQuery) -> SelectItem:
    alias = ""
    expr = node
    if isinstance(node, exp.Alias):
        alias = node.alias
        expr = node.this
    elif isinstance(node, exp.Column):
        alias = node.name

    # Aggregate: SUM(col), SUM(DISTINCT col), COUNT(...), etc.
    cat = _catalog(parsed)
    if isinstance(expr, exp.AggFunc) or (
        isinstance(expr, exp.Func) and cat.is_aggregate(expr.sql_name())
    ):
        fn_name = expr.sql_name().upper()
        agg = cat.aggregate_key(fn_name)
        aggregates: list[str] = [agg] if agg else []
        inner = expr.this if hasattr(expr, "this") else None
        has_distinct = bool(expr.args.get("distinct"))

        # SUM(DISTINCT (...)) — sqlglot wraps as Distinct node
        if isinstance(inner, exp.Distinct):
            has_distinct = True
            if inner.expressions:
                inner = inner.expressions[0]
            elif inner.this is not None:
                inner = inner.this

        if has_distinct:
            # COUNT(DISTINCT x) prefers COUNT_DISTINCT when mapped; else append distinct
            if isinstance(expr, exp.Count):
                count_distinct = cat.aggregate_key("COUNT_DISTINCT")
                if count_distinct and count_distinct != agg:
                    aggregates = [count_distinct]
                    agg = count_distinct
                else:
                    distinct_key = cat.aggregate_key("DISTINCT")
                    if distinct_key and distinct_key not in aggregates:
                        aggregates.append(distinct_key)
            else:
                distinct_key = cat.aggregate_key("DISTINCT")
                if distinct_key and distinct_key not in aggregates:
                    aggregates.append(distinct_key)

        col_ref = None
        db_fn = None
        fn_def = ""
        custom = False
        raw = expr.sql(dialect=parsed.dialect)

        if isinstance(inner, exp.Column):
            col_ref = _column_ref(inner, parsed)
        elif isinstance(inner, exp.Func) and not isinstance(inner, exp.AggFunc):
            # e.g. SUM(ABS(col)) / SUM(CONCAT(...))
            db_fn = _build_db_fn(inner, parsed)
            inner_col = _find_column(inner)
            if inner_col:
                col_ref = _column_ref(inner_col, parsed)
            if db_fn:
                fn_def = cat.functions_definition(db_fn)
            elif inner_col:
                # known column but unknown nested fn → still custom measure expression
                custom = True
            else:
                custom = True
        elif inner is not None:
            custom = True

        if not alias:
            base = col_ref.name if col_ref else "expr"
            alias = f"{fn_name.lower()}_{base}" if agg else base

        return SelectItem(
            alias=alias,
            column=col_ref,
            aggregate=agg,
            aggregates=aggregates,
            database_function=db_fn,
            functions_definition=fn_def,
            is_custom=custom,
            custom_expression=raw if custom else None,
            raw_sql=raw,
        )

    # Non-aggregate database function: CONCAT(...), LENGTH(CAST(CONCAT(...) AS VARCHAR)), YEAR(col), ...
    if isinstance(expr, exp.Func):
        raw = expr.sql(dialect=parsed.dialect)
        db_fn = _build_db_fn(expr, parsed)
        inner_col = _find_column(expr)
        col_ref = _column_ref(inner_col, parsed) if inner_col else None
        if not alias:
            alias = col_ref.name if col_ref else expr.sql_name().lower()

        if db_fn:
            return SelectItem(
                alias=alias,
                column=col_ref,
                database_function=db_fn,
                functions_definition=cat.functions_definition(db_fn),
                raw_sql=raw,
            )

        # Not in functionMapping / getFunctions catalog → custom column (destin-style)
        return SelectItem(
            alias=alias or "custom",
            column=col_ref,
            is_custom=True,
            custom_expression=raw,
            raw_sql=raw,
        )

    if isinstance(expr, exp.Column):
        col_ref = _column_ref(expr, parsed)
        return SelectItem(alias=alias or col_ref.name, column=col_ref, raw_sql=expr.sql(dialect=parsed.dialect))

    # Custom / complex expression
    raw = expr.sql(dialect=parsed.dialect)
    return SelectItem(
        alias=alias or "custom",
        is_custom=True,
        custom_expression=raw,
        raw_sql=raw,
    )


def _build_db_fn(expr: exp.Expression, parsed: ParsedQuery) -> dict | None:
    cat = _catalog(parsed)
    return cat.build_database_function(
        expr,
        dialect=parsed.dialect,
        database_name=parsed.database_name or "",
        table_alias=parsed.table_alias or parsed.table_name or "",
        table_aliases=parsed.table_aliases,
    )


def _find_column(node: exp.Expression) -> exp.Column | None:
    if isinstance(node, exp.Column):
        return node
    for child in node.walk():
        if isinstance(child, exp.Column):
            return child
    return None


def _expr_alias_or_sql(node: exp.Expression, parsed: ParsedQuery) -> str:
    if isinstance(node, exp.Column):
        return node.name
    if isinstance(node, exp.Alias):
        return node.alias
    return node.sql(dialect=parsed.dialect)


def _literal_value(node: exp.Expression) -> Any:
    if node is None:
        return None
    if isinstance(node, exp.Null):
        return None
    if isinstance(node, exp.Boolean):
        return node.this
    if isinstance(node, exp.Literal):
        if node.is_string:
            return node.this
        text = node.this
        try:
            if "." in str(text):
                return float(text)
            return int(text)
        except (TypeError, ValueError):
            return text
    if isinstance(node, (exp.Paren,)):
        return _literal_value(node.this)
    return node.sql()


def _is_all_placeholder(left: exp.Expression, right: exp.Expression) -> bool:
    lv = _literal_value(left)
    rv = _literal_value(right)
    return lv == "_all_" and rv == "_all_"


def _flatten_predicates(
    node: exp.Expression,
    parsed: ParsedQuery,
    *,
    for_having: bool,
    join_op: str = "AND",
) -> list[FilterItem]:
    if isinstance(node, exp.Paren):
        return _flatten_predicates(node.this, parsed, for_having=for_having, join_op=join_op)

    if isinstance(node, exp.And):
        left = _flatten_predicates(node.left, parsed, for_having=for_having, join_op="AND")
        right = _flatten_predicates(node.right, parsed, for_having=for_having, join_op="AND")
        return left + right

    if isinstance(node, exp.Or):
        left = _flatten_predicates(node.left, parsed, for_having=for_having, join_op="OR")
        right = _flatten_predicates(node.right, parsed, for_having=for_having, join_op="OR")
        if right:
            right[0].operator = "OR"
        return left + right

    return [_parse_predicate(node, parsed, for_having=for_having, join_op=join_op)]


def _parse_predicate(
    node: exp.Expression,
    parsed: ParsedQuery,
    *,
    for_having: bool,
    join_op: str,
) -> FilterItem:
    raw = node.sql(dialect=parsed.dialect)

    # sqlglot often represents NOT IN / IS NOT NULL as Not(In(...)) / Not(Is(...))
    if isinstance(node, exp.Not):
        inner = node.this
        if isinstance(inner, exp.Paren):
            inner = inner.this
        if isinstance(inner, exp.In):
            return _parse_in(inner, parsed, for_having=for_having, join_op=join_op, negated=True, raw=raw)
        if isinstance(inner, exp.Is):
            return _parse_is(inner, parsed, for_having=for_having, join_op=join_op, negated=True, raw=raw)
        if isinstance(inner, exp.Between):
            return _parse_between(inner, parsed, for_having=for_having, join_op=join_op, negated=True, raw=raw)
        if isinstance(inner, exp.Like):
            item = _parse_like(inner, parsed, for_having=for_having, join_op=join_op, raw=raw)
            # flip CONTAINS ↔ DOES_NOT_CONTAINS etc.
            flip = {
                "CONTAINS": "DOES_NOT_CONTAINS",
                "STARTS_WITH": "DOES_NOT_STARTS_WITH",
                "ENDS_WITH": "DOES_NOT_ENDS_WITH",
            }
            item.ui_condition = flip.get(item.ui_condition, "CUSTOM")
            return item

    # '_all_' = '_all_'
    if isinstance(node, exp.EQ) and _is_all_placeholder(node.left, node.right):
        return FilterItem(
            column=None,
            ui_condition="ALL",
            values=[],
            operator=join_op,
            is_all=True,
            raw_sql=raw,
        )

    if isinstance(node, exp.Between):
        return _parse_between(node, parsed, for_having=for_having, join_op=join_op, negated=False, raw=raw)

    if isinstance(node, exp.In):
        return _parse_in(node, parsed, for_having=for_having, join_op=join_op, negated=bool(node.args.get("not")), raw=raw)

    if isinstance(node, exp.Is):
        return _parse_is(node, parsed, for_having=for_having, join_op=join_op, negated=bool(node.args.get("not")), raw=raw)

    if isinstance(node, exp.Like):
        return _parse_like(node, parsed, for_having=for_having, join_op=join_op, raw=raw)

    # Binary comparisons
    if isinstance(node, exp.Binary):
        left, right = node.left, node.right
        if isinstance(right, (exp.Column, exp.Func, exp.AggFunc)) and isinstance(left, exp.Literal):
            left, right = right, left

        aggregate, col_ref, db_fn, alias = _side_column_meta(left, parsed)
        op_name = type(node).__name__.upper()
        ui = sql_op_to_ui_condition(op_name)
        values = [_literal_value(right)]
        return FilterItem(
            column=col_ref,
            ui_condition=ui,
            values=values,
            operator=join_op,
            aggregate=aggregate if (for_having or aggregate) else None,
            database_function=db_fn,
            alias=alias,
            raw_sql=raw,
        )

    return FilterItem(
        column=None,
        ui_condition="CUSTOM",
        values=[],
        operator=join_op,
        custom_sql=raw,
        raw_sql=raw,
    )


def _parse_between(
    node: exp.Between,
    parsed: ParsedQuery,
    *,
    for_having: bool,
    join_op: str,
    negated: bool,
    raw: str,
) -> FilterItem:
    aggregate, col_ref, db_fn, alias = _side_column_meta(node.this, parsed)
    low = _literal_value(node.args.get("low"))
    high = _literal_value(node.args.get("high"))
    ui = "IS_NOT_BETWEEN" if negated or node.args.get("not") else "IS_BETWEEN"
    return FilterItem(
        column=col_ref,
        ui_condition=ui,
        values=[low, high],
        operator=join_op,
        aggregate=aggregate if for_having or aggregate else None,
        database_function=db_fn,
        alias=alias,
        raw_sql=raw,
    )


def _parse_in(
    node: exp.In,
    parsed: ParsedQuery,
    *,
    for_having: bool,
    join_op: str,
    negated: bool,
    raw: str,
) -> FilterItem:
    aggregate, col_ref, db_fn, alias = _side_column_meta(node.this, parsed)
    values = [_literal_value(v) for v in node.expressions]
    ui = "IS_NOT_ONE_OF" if negated or node.args.get("not") else "IS_ONE_OF"
    return FilterItem(
        column=col_ref,
        ui_condition=ui,
        values=values,
        operator=join_op,
        aggregate=aggregate if (for_having or aggregate) else None,
        database_function=db_fn,
        alias=alias,
        raw_sql=raw,
    )


def _parse_is(
    node: exp.Is,
    parsed: ParsedQuery,
    *,
    for_having: bool,
    join_op: str,
    negated: bool,
    raw: str,
) -> FilterItem:
    aggregate, col_ref, db_fn, alias = _side_column_meta(node.this, parsed)
    nullish = isinstance(node.expression, exp.Null)
    is_negated = negated or bool(node.args.get("not"))
    if nullish:
        ui = "IS_NOT_NULL" if is_negated else "IS_NULL"
    else:
        ui = "CUSTOM"
    return FilterItem(
        column=col_ref,
        ui_condition=ui,
        values=[],
        operator=join_op,
        aggregate=aggregate if (for_having or aggregate) else None,
        database_function=db_fn,
        alias=alias,
        raw_sql=raw,
    )


def _parse_like(
    node: exp.Like,
    parsed: ParsedQuery,
    *,
    for_having: bool,
    join_op: str,
    raw: str,
) -> FilterItem:
    aggregate, col_ref, db_fn, alias = _side_column_meta(node.this, parsed)
    pattern = str(_literal_value(node.expression) or "")
    ui = _like_to_condition(pattern)
    clean = pattern.strip("%")
    return FilterItem(
        column=col_ref,
        ui_condition=ui,
        values=[clean if ui in ("CONTAINS", "STARTS_WITH", "ENDS_WITH") else pattern],
        operator=join_op,
        aggregate=aggregate if (for_having or aggregate) else None,
        database_function=db_fn,
        alias=alias,
        raw_sql=raw,
    )


def _like_to_condition(pattern: str) -> str:
    if pattern.startswith("%") and pattern.endswith("%"):
        return "CONTAINS"
    if pattern.endswith("%") and not pattern.startswith("%"):
        return "STARTS_WITH"
    if pattern.startswith("%") and not pattern.endswith("%"):
        return "ENDS_WITH"
    return "CONTAINS"


def _side_column_meta(
    side: exp.Expression,
    parsed: ParsedQuery,
) -> tuple[str | None, ColumnRef | None, dict | None, str | None]:
    """Return (aggregate_key, column_ref, database_function, alias_hint)."""
    aggregate = None
    db_fn = None
    alias = None
    col_ref = None
    cat = _catalog(parsed)

    expr = side
    if isinstance(expr, exp.Paren):
        expr = expr.this

    if isinstance(expr, exp.AggFunc) or (
        isinstance(expr, exp.Func) and cat.is_aggregate(expr.sql_name())
    ):
        aggregate = cat.aggregate_key(expr.sql_name())
        inner = expr.this
        if isinstance(inner, exp.Distinct):
            distinct_key = cat.aggregate_key("DISTINCT")
            if isinstance(expr, exp.Count):
                aggregate = cat.aggregate_key("COUNT_DISTINCT") or distinct_key or aggregate
            # peel distinct for column extraction
            if inner.expressions:
                inner = inner.expressions[0]
            elif inner.this is not None:
                inner = inner.this
        elif isinstance(expr, exp.Count) and expr.args.get("distinct"):
            aggregate = cat.aggregate_key("COUNT_DISTINCT") or cat.aggregate_key("DISTINCT")

        if isinstance(inner, exp.Func) and not isinstance(inner, exp.AggFunc):
            inner_col = _find_column(inner)
            if inner_col:
                col_ref = _column_ref(inner_col, parsed)
            db_fn = _build_db_fn(inner, parsed)
        elif isinstance(inner, exp.Column):
            col_ref = _column_ref(inner, parsed)
        if col_ref and aggregate:
            fn = (expr.sql_name() or "agg").lower()
            alias = f"{fn}_{col_ref.name}"
        return aggregate, col_ref, db_fn, alias

    if isinstance(expr, exp.Func):
        inner_col = _find_column(expr)
        if inner_col:
            col_ref = _column_ref(inner_col, parsed)
        db_fn = _build_db_fn(expr, parsed)
        return None, col_ref, db_fn, alias

    if isinstance(expr, exp.Column):
        col_ref = _column_ref(expr, parsed)
        return None, col_ref, None, col_ref.name

    return None, None, None, None
