from __future__ import annotations

from typing import Any, Dict, List, Optional, Set, Tuple

import sqlglot
from sqlglot import exp

from helicalbi.common.DialectMapper import resolve_sqlglot_dialect
from helicalbi.common.JsonToPara import metadata_has_column, metadata_has_table
from helicalbi.sql.SqlSanitizer import quote_spaced_identifiers, strip_sql_markdown
from helicalbi.sql_agent.database.catalog import SchemaCatalog


def _expression_types(*names: str):
    types = []
    for name in names:
        cls = getattr(exp, name, None)
        if isinstance(cls, type):
            types.append(cls)
    return tuple(types)


_WRITE_TYPES = _expression_types(
    "Insert",
    "Update",
    "Delete",
    "Drop",
    "Create",
    "Alter",
    "Command",
    "Merge",
    "TruncateTable",
    "Truncate",
)


def _norm(name: Optional[str]) -> str:
    return str(name or "").strip().strip('"').strip("`").strip("[]").lower()


def _parse_sql(sql: str, dialect: Optional[str]) -> exp.Expression:
    read_dialect = resolve_sqlglot_dialect(dialect)
    candidates = [sql, quote_spaced_identifiers(sql)]
    last_error = None
    for candidate in candidates:
        try:
            return sqlglot.parse_one(candidate, read=read_dialect)
        except sqlglot.errors.ParseError as exc:
            last_error = exc
    raise sqlglot.errors.ParseError(str(last_error or "Unable to parse SQL"))


def _is_read_only(tree: exp.Expression) -> bool:
    if isinstance(tree, _WRITE_TYPES):
        return False
    for node in tree.walk():
        if isinstance(node, _WRITE_TYPES):
            return False
    return isinstance(tree, (exp.Select, exp.Union, exp.With, exp.Subquery)) or bool(
        tree.find(exp.Select)
    )


def _cte_names(tree: exp.Expression) -> Set[str]:
    names = set()
    for cte in tree.find_all(exp.CTE):
        alias = cte.alias
        if alias:
            names.add(_norm(alias))
    return names


def _tables_and_aliases(tree: exp.Expression) -> Tuple[List[str], Dict[str, str]]:
    cte_names = _cte_names(tree)
    physical: List[str] = []
    aliases: Dict[str, str] = {}
    for table in tree.find_all(exp.Table):
        name = table.name
        if not name:
            continue
        key = _norm(name)
        if key in cte_names:
            continue
        physical.append(name)
        alias = table.alias
        if alias:
            aliases[_norm(alias)] = name
        aliases[key] = name
    return physical, aliases


def _star_used(tree: exp.Expression) -> bool:
    for star in tree.find_all(exp.Star):
        parent = star.parent
        if isinstance(parent, exp.Count):
            return True
        return True
    return False


def _known_table(
    catalog: SchemaCatalog,
    table_name: str,
    metadata: Optional[dict] = None,
) -> bool:
    return catalog.has_table(table_name) or metadata_has_table(metadata, table_name)


def _known_column(
    catalog: SchemaCatalog,
    table_name: str,
    column_name: str,
    metadata: Optional[dict] = None,
) -> bool:
    if catalog.has_column(table_name, column_name):
        return True
    return metadata_has_column(metadata, table_name, column_name)


def validate_sql_against_catalog(
    sql: str,
    catalog: SchemaCatalog,
    dialect: Optional[str] = None,
    metadata: Optional[dict] = None,
) -> Optional[str]:
    """Return an error string if SQL is invalid, otherwise None.

    Checks the semantic cube catalog first. Columns/tables missing there are
    accepted when present in the physical metadata API payload.
    """
    cleaned = strip_sql_markdown(sql or "").strip()
    if not cleaned:
        return "SQL is empty"
    if ";" in cleaned.rstrip(";"):
        return "Multiple SQL statements are not allowed"

    try:
        tree = _parse_sql(cleaned, dialect)
    except sqlglot.errors.ParseError as exc:
        return f"SQL parse error: {exc}"

    if not _is_read_only(tree):
        return "Only read-only SELECT statements are allowed"
    if _star_used(tree):
        return "SELECT * and COUNT(*) are not allowed; list explicit columns"

    physical_tables, aliases = _tables_and_aliases(tree)
    if not physical_tables:
        return "SQL does not reference any catalog table"

    unknown_tables = [
        name for name in physical_tables if not _known_table(catalog, name, metadata)
    ]
    if unknown_tables:
        return f"Unknown table(s): {', '.join(sorted(set(unknown_tables)))}"

    cte_names = _cte_names(tree)
    unknown_columns: List[str] = []
    for column in tree.find_all(exp.Column):
        col_name = column.name
        if not col_name or col_name == "*":
            continue
        table_ref = column.table
        if table_ref and _norm(table_ref) in cte_names:
            continue
        resolved_table = aliases.get(_norm(table_ref)) if table_ref else None
        if resolved_table:
            if not _known_column(catalog, resolved_table, col_name, metadata):
                unknown_columns.append(f"{resolved_table}.{col_name}")
            continue
        if table_ref and not _known_table(catalog, table_ref, metadata):
            unknown_columns.append(f"{table_ref}.{col_name}")
            continue
        if table_ref:
            if not _known_column(catalog, table_ref, col_name, metadata):
                unknown_columns.append(f"{table_ref}.{col_name}")
            continue
        owners = [
            name
            for name in physical_tables
            if _known_column(catalog, name, col_name, metadata)
        ]
        if not owners:
            unknown_columns.append(col_name)

    if unknown_columns:
        unique = sorted(set(unknown_columns))
        return f"Unknown column(s): {', '.join(unique)}"
    return None

