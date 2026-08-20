import logging
import re

import sqlglot

from helicalbi.common.DialectMapper import resolve_sqlglot_dialect

logger = logging.getLogger(__name__)

_SQL_KEYWORDS = {
    "SELECT", "FROM", "WHERE", "GROUP", "BY", "ORDER", "HAVING", "LIMIT",
    "JOIN", "LEFT", "RIGHT", "INNER", "OUTER", "ON", "AS", "AND", "OR",
    "NOT", "IN", "IS", "NULL", "LIKE", "BETWEEN", "DISTINCT", "COUNT",
    "SUM", "AVG", "MIN", "MAX", "CASE", "WHEN", "THEN", "ELSE", "END",
    "ASC", "DESC", "UNION", "ALL", "INSERT", "UPDATE", "DELETE", "INTO",
    "VALUES", "SET", "TRUE", "FALSE", "OVER", "PARTITION", "ROW_NUMBER",
}

_SPACED_IDENTIFIER = re.compile(
    r'(?<!["\'])\b([A-Za-z_][A-Za-z0-9_]*(?: [A-Za-z0-9_]+)+)\b(?!["\'])'
)


def quote_spaced_identifiers(sql: str, known_names=None) -> str:
    """Quote bare identifiers that contain spaces so sqlglot can parse them."""
    if not sql:
        return sql

    if known_names:
        updated = sql
        for name in sorted({n for n in known_names if n and " " in str(n)}, key=len, reverse=True):
            pattern = rf'(?<!["\w])({re.escape(str(name))})(?!["\w])'
            updated = re.sub(pattern, r'"\1"', updated)
        sql = updated

    def _replace(match):
        token = match.group(1)
        first_word = token.split()[0].upper()
        if first_word in _SQL_KEYWORDS:
            return token
        return f'"{token}"'

    return _SPACED_IDENTIFIER.sub(_replace, sql)


def extract_sql(sql, dialect):
    if not sql:
        return sql

    final_dialect = resolve_sqlglot_dialect(dialect)
    candidates = [sql, quote_spaced_identifiers(sql)]

    for candidate in candidates:
        try:
            return sqlglot.transpile(
                candidate,
                identify=True,
                read=final_dialect,
                write=final_dialect,
            )[0]
        except sqlglot.errors.ParseError:
            logger.error(
                "SQL parse candidate failed for dialect=%s",
                final_dialect,
                exc_info=True,
            )
            continue
        except Exception:
            logger.error("SQL transpile failed for dialect=%s", dialect, exc_info=True)
            break

    return sql


def strip_sql_markdown(raw_sql: str) -> str:
    """Remove one or more markdown `` ```sql `` fences from SQL text."""
    if not raw_sql:
        return ""
    text = str(raw_sql).strip()
    # Peel repeated leading/trailing fences (handles ```sql\n```sql\n...).
    while True:
        stripped = text
        if stripped.lower().startswith("```sql"):
            stripped = stripped[6:].lstrip("\n\r")
        elif stripped.startswith("```"):
            stripped = stripped[3:].lstrip("\n\r")
        if stripped.endswith("```"):
            stripped = stripped[:-3].rstrip()
        if stripped == text:
            break
        text = stripped.strip()
    return (
        text.replace("```sql", "")
        .replace("```SQL", "")
        .replace("```", "")
        .strip()
    )


def as_sql_markdown(sql: str) -> str:
    """Wrap SQL in a single `` ```sql `` fence; do not double-wrap."""
    cleaned = strip_sql_markdown(sql)
    if not cleaned:
        return ""
    return f"```sql\n{cleaned}"


def format_sql(sql, dialect=None, pretty=False):
    """Safely normalize and optionally pretty-print SQL for API responses."""
    if not sql:
        return sql

    formatted = extract_sql(strip_sql_markdown(sql), dialect)
    formatted = strip_sql_markdown(formatted or "")
    if not pretty:
        return formatted

    final_dialect = resolve_sqlglot_dialect(dialect)
    try:
        pretty_sql = sqlglot.transpile(
            formatted,
            pretty=True,
            read=final_dialect,
            write=final_dialect,
        )[0]
        return strip_sql_markdown(pretty_sql or "")
    except Exception:
        logger.error(
            "SQL pretty-print failed; returning normalized SQL",
            exc_info=True,
        )
        return formatted
