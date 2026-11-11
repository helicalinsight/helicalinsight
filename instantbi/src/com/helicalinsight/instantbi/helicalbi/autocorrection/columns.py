"""Find SQL columns that are not on the table they were written against."""
from __future__ import annotations

from difflib import SequenceMatcher
from typing import Dict, List, Optional

import sqlglot
from sqlglot import exp

from helicalbi.common.JsonToPara import iter_cube_entries
from helicalbi.sql.SqlSanitizer import strip_sql_markdown
from helicalbi.sql_agent.nodes.validator import _norm, _parse_sql, _tables_and_aliases


def _column_catalog(cube_metadata) -> tuple[Dict[str, str], Dict[str, Dict[str, str]]]:
    """Physical column names keyed by lower-case table and column."""
    tables: Dict[str, str] = {}
    columns: Dict[str, Dict[str, str]] = {}
    for cube in iter_cube_entries(cube_metadata or []):
        table_name = str(cube.get("database_table") or "").strip()
        if not table_name:
            continue
        table_key = _norm(table_name)
        tables.setdefault(table_key, table_name)
        bucket = columns.setdefault(table_key, {})
        for group in ("columns", "measures"):
            for item in cube.get(group) or []:
                if not isinstance(item, dict):
                    continue
                column_name = str(item.get("column_name") or "").strip()
                if column_name:
                    bucket.setdefault(_norm(column_name), column_name)
    return tables, columns


def _name_score(left: str, right: str) -> float:
    a = _norm(left)
    b = _norm(right)
    if not a or not b:
        return 0.0
    score = SequenceMatcher(None, a, b).ratio()
    a_parts = {part for part in a.split("_") if len(part) >= 2}
    b_parts = {part for part in b.split("_") if len(part) >= 2}
    if a_parts and b_parts and a_parts & b_parts:
        score = max(score, 0.6)
    return score


def sql_column_mismatches(
    sql: str,
    cube_metadata,
    dialect: Optional[str] = None,
    known_columns: Optional[set] = None,
) -> List[dict]:
    """Qualified columns whose name is not on the cube table they use.

    Each item names the reference, the tables that actually store that column,
    and similar column names.
    """
    cleaned = strip_sql_markdown(sql or "").strip()
    tables, columns = _column_catalog(cube_metadata)
    if not cleaned or not tables:
        return []
    try:
        tree = _parse_sql(cleaned, dialect)
    except sqlglot.errors.ParseError:
        return []

    _physical, aliases = _tables_and_aliases(tree)
    allowed = {
        (_norm(table), _norm(column))
        for table, column in (known_columns or set())
        if table and column
    }
    mismatches: List[dict] = []
    seen = set()
    for column in tree.find_all(exp.Column):
        column_name = str(column.name or "").strip()
        qualifier = str(column.table or "").strip()
        if not column_name or not qualifier or column_name == "*":
            continue
        table_name = aliases.get(_norm(qualifier), qualifier)
        table_key = _norm(table_name)
        if table_key not in tables:
            continue
        column_key = _norm(column_name)
        if column_key in columns.get(table_key, {}) or (table_key, column_key) in allowed:
            continue
        reference = f"{tables[table_key]}.{column_name}"
        if reference in seen:
            continue
        seen.add(reference)
        proper = [
            f"{tables[owner]}.{columns[owner][column_key]}"
            for owner in columns
            if column_key in columns[owner]
        ]
        similar = _similar_names(column_name, table_key, tables, columns, proper)
        mismatches.append(
            {
                "reference": reference,
                "table": tables[table_key],
                "column": column_name,
                "proper": proper,
                "similar": similar,
            }
        )
    return mismatches


def _similar_names(
    column_name: str,
    table_key: str,
    tables: Dict[str, str],
    columns: Dict[str, Dict[str, str]],
    proper: List[str],
) -> List[str]:
    proper_keys = {_norm(name) for name in proper}
    same_table: List[tuple] = []
    elsewhere: List[tuple] = []
    for owner, names in columns.items():
        for key, display in names.items():
            qualified = f"{tables[owner]}.{display}"
            if _norm(qualified) in proper_keys:
                continue
            score = _name_score(column_name, display)
            if score < 0.55:
                continue
            target = same_table if owner == table_key else elsewhere
            label = display if owner == table_key else qualified
            target.append((score, label))
    same_table.sort(key=lambda item: item[0], reverse=True)
    elsewhere.sort(key=lambda item: item[0], reverse=True)
    ordered = [label for _score, label in same_table[:3]]
    ordered.extend(label for _score, label in elsewhere[:2])
    return ordered


def columns_for_join_recheck(
    sql: str,
    cube_metadata,
    dialect: Optional[str] = None,
) -> List[str]:
    """Qualified cube columns for a fresh join-API call.

    A column written on the wrong table is replaced by the table that stores it.
    """
    cleaned = strip_sql_markdown(sql or "").strip()
    tables, columns = _column_catalog(cube_metadata)
    if not cleaned or not tables:
        return []
    try:
        tree = _parse_sql(cleaned, dialect)
    except sqlglot.errors.ParseError:
        return []
    mismatches = {
        item["reference"]: item
        for item in sql_column_mismatches(sql, cube_metadata, dialect)
    }
    _physical, aliases = _tables_and_aliases(tree)
    refs: List[str] = []
    seen = set()
    for column in tree.find_all(exp.Column):
        column_name = str(column.name or "").strip()
        qualifier = str(column.table or "").strip()
        if not column_name or not qualifier or column_name == "*":
            continue
        table_name = aliases.get(_norm(qualifier), qualifier)
        table_key = _norm(table_name)
        if table_key not in tables:
            continue
        reference = f"{tables[table_key]}.{column_name}"
        item = mismatches.get(reference)
        if item and item.get("proper"):
            chosen = item["proper"][0]
        elif _norm(column_name) in columns.get(table_key, {}):
            chosen = f"{tables[table_key]}.{columns[table_key][_norm(column_name)]}"
        else:
            continue
        if chosen in seen:
            continue
        seen.add(chosen)
        refs.append(chosen)
    return refs


def join_api_columns(api_join: str, dialect: Optional[str] = None) -> set:
    """(table, column) pairs used in the join returned by the join API.

    Those columns exist in the database even when the cube does not list them.
    """
    text = strip_sql_markdown(api_join or "").strip().rstrip(";")
    if not text:
        return set()
    wrapped = f"SELECT 1 FROM __join_anchor__ {text}"
    try:
        tree = _parse_sql(wrapped, dialect)
    except sqlglot.errors.ParseError:
        try:
            tree = _parse_sql(text, dialect)
        except sqlglot.errors.ParseError:
            return set()
    found = set()
    for join in tree.find_all(exp.Join):
        for column in join.find_all(exp.Column):
            table_name = str(column.table or "").strip()
            column_name = str(column.name or "").strip()
            if table_name and column_name and _norm(table_name) != "__join_anchor__":
                found.add((table_name, column_name))
    return found


def join_needs_rewrite(sql: str, api_join: str, dialect: Optional[str] = None) -> bool:
    """True when the SQL join does not use the columns returned by the join API."""
    api_columns = {_norm(column) for _table, column in join_api_columns(api_join, dialect)}
    if not api_columns:
        return False
    try:
        tree = _parse_sql(strip_sql_markdown(sql or "").strip(), dialect)
    except sqlglot.errors.ParseError:
        return True
    sql_columns = set()
    for join in tree.find_all(exp.Join):
        for column in join.find_all(exp.Column):
            if column.name:
                sql_columns.add(_norm(column.name))
    return not api_columns <= sql_columns


def _join_alias_nodes(api_join: str, dialect: Optional[str]) -> Dict[str, exp.Expression]:
    """Table name -> alias node taken from the join API text."""
    text = strip_sql_markdown(api_join or "").strip().rstrip(";")
    if not text:
        return {}
    wrapped = f"SELECT 1 FROM __join_anchor__ {text}"
    try:
        tree = _parse_sql(wrapped, dialect)
    except sqlglot.errors.ParseError:
        try:
            tree = _parse_sql(text, dialect)
        except sqlglot.errors.ParseError:
            return {}
    nodes: Dict[str, exp.Expression] = {}
    for table in tree.find_all(exp.Table):
        if not table.name or _norm(table.name) == "__join_anchor__":
            continue
        alias = table.args.get("alias")
        if alias is not None:
            nodes.setdefault(_norm(table.name), alias)
    return nodes


def restore_join_aliases(sql: str, api_join: str, dialect: Optional[str] = None) -> str:
    """Put a join-API alias back on a table that the correction left unnamed.

    Only fills a missing alias when that alias is already used in the SQL and
    the physical table name is not itself used as a qualifier.
    """
    cleaned = strip_sql_markdown(sql or "").strip()
    alias_nodes = _join_alias_nodes(api_join, dialect)
    if not cleaned or not alias_nodes:
        return sql
    try:
        tree = _parse_sql(cleaned, dialect)
    except sqlglot.errors.ParseError:
        return sql
    qualifiers = {
        _norm(column.table)
        for column in tree.find_all(exp.Column)
        if column.table
    }
    _physical, declared = _tables_and_aliases(tree)
    missing = qualifiers - set(declared)
    if not missing:
        return sql
    changed = False
    for table in tree.find_all(exp.Table):
        if table.alias or not table.name:
            continue
        alias = alias_nodes.get(_norm(table.name))
        if alias is None:
            continue
        alias_name = alias.name if hasattr(alias, "name") else ""
        if _norm(alias_name) not in missing:
            continue
        if _norm(table.name) in qualifiers:
            continue
        table.set("alias", alias.copy())
        changed = True
    if not changed:
        return sql
    from helicalbi.common.DialectMapper import resolve_sqlglot_dialect

    return tree.sql(dialect=resolve_sqlglot_dialect(dialect))


def _inside_aggregate(column: exp.Column) -> bool:
    parent = column.parent
    while parent is not None:
        if isinstance(parent, exp.AggFunc):
            return True
        parent = parent.parent
    return False


def _grouped_names(tree: exp.Expression) -> set:
    names = set()
    for group in tree.find_all(exp.Group):
        for column in group.find_all(exp.Column):
            if column.name:
                names.add(_norm(column.name))
    return names


def _same_table_replacement(
    column_name: str,
    similar: List[str],
    *,
    in_aggregate: bool,
    grouped: set,
) -> Optional[str]:
    """Pick a real column on the same table. An aggregate prefers an id column."""
    same_table = [name for name in similar if "." not in str(name)]
    if not same_table:
        return None
    if not in_aggregate:
        return same_table[0]
    identifiers = [
        name for name in same_table if _norm(name) == "id" or _norm(name).endswith("_id")
    ]
    for pool in (identifiers, same_table):
        unused = [name for name in pool if _norm(name) not in grouped]
        if unused:
            return unused[0]
    return identifiers[0] if identifiers else same_table[0]


def replace_missing_columns(
    sql: str,
    mismatches: List[dict],
    dialect: Optional[str] = None,
) -> tuple:
    """Replace a column that exists on no table with a similar column of that table.

    Returns the SQL and a list of ``table.old -> table.new`` replacements.
    A column that belongs on a different table is left for the join rewrite.
    """
    pending = [item for item in mismatches if not item.get("proper")]
    cleaned = strip_sql_markdown(sql or "").strip()
    if not pending or not cleaned:
        return sql, []
    try:
        tree = _parse_sql(cleaned, dialect)
    except sqlglot.errors.ParseError:
        return sql, []
    _physical, aliases = _tables_and_aliases(tree)
    grouped = _grouped_names(tree)
    aggregate_refs = set()
    for column in tree.find_all(exp.Column):
        if not _inside_aggregate(column):
            continue
        qualifier = str(column.table or "").strip()
        column_name = str(column.name or "").strip()
        if qualifier and column_name:
            table_name = aliases.get(_norm(qualifier), qualifier)
            aggregate_refs.add((_norm(table_name), _norm(column_name)))
    replacements = {}
    notes = []
    for item in pending:
        table_key = _norm(item.get("table"))
        column_key = _norm(item.get("column"))
        chosen = _same_table_replacement(
            item.get("column") or "",
            item.get("similar") or [],
            in_aggregate=(table_key, column_key) in aggregate_refs,
            grouped=grouped,
        )
        if not chosen or _norm(chosen) == column_key:
            continue
        replacements[(table_key, column_key)] = chosen
        notes.append(f"{item['table']}.{item['column']} -> {item['table']}.{chosen}")
    if not replacements:
        return sql, []
    changed = False
    for column in tree.find_all(exp.Column):
        qualifier = str(column.table or "").strip()
        column_name = str(column.name or "").strip()
        if not qualifier or not column_name:
            continue
        table_name = aliases.get(_norm(qualifier), qualifier)
        chosen = replacements.get((_norm(table_name), _norm(column_name)))
        if not chosen:
            continue
        quoted = bool(getattr(column.this, "quoted", False))
        column.set("this", exp.to_identifier(chosen, quoted=quoted))
        changed = True
    if not changed:
        return sql, []
    from helicalbi.common.DialectMapper import resolve_sqlglot_dialect

    return tree.sql(dialect=resolve_sqlglot_dialect(dialect)), notes


def format_column_corrections(mismatches: List[dict]) -> str:
    """Text the SQL model can use to replace mismatched columns."""
    if not mismatches:
        return ""
    lines = ["These columns are not on the table used in the SQL."]
    for item in mismatches:
        proper = item.get("proper") or []
        similar = item.get("similar") or []
        if proper:
            use = " or ".join(proper)
            line = f"- {item['reference']} is not a column of {item['table']}. Use {use}."
        else:
            line = (
                f"- {item['reference']} is not a column of {item['table']}. "
                "Replace it with a similar column of the same table."
            )
        if similar:
            line += " Similar columns: " + ", ".join(similar) + "."
        lines.append(line)
    if any(item.get("proper") for item in mismatches):
        lines.append(
            "Take each mismatched column from the table that stores it."
        )
    if any(not item.get("proper") for item in mismatches):
        lines.append(
            "When no table stores the column, keep it on the same table and use a similar column. "
            "Do not keep the unknown column."
        )
    return "\n".join(lines)
