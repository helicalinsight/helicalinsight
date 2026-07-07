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


def format_sql(sql, dialect=None, pretty=False):
    """Safely normalize and optionally pretty-print SQL for API responses."""
    if not sql:
        return sql

    formatted = extract_sql(sql, dialect)
    if not pretty:
        return formatted

    final_dialect = resolve_sqlglot_dialect(dialect)
    try:
        return sqlglot.transpile(
            formatted,
            pretty=True,
            read=final_dialect,
            write=final_dialect,
        )[0]
    except Exception:
        logger.error(
            "SQL pretty-print failed; returning normalized SQL",
            exc_info=True,
        )
        return formatted
