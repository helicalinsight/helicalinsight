"""Prefer NOT IN for membership exclusions when the dialect supports it.

Questions such as "not in the year 2015" are membership exclusions. Generated
SQL should use ``NOT IN (...)`` even for a single value. ``<>`` / ``!=`` stay
only for a true not-equal comparison, or when the dialect has no IN predicate.
"""

from __future__ import annotations

import logging
import re
from typing import Optional

import sqlglot
from sqlglot import exp

from helicalbi.common.DialectMapper import resolve_sqlglot_dialect

logger = logging.getLogger(__name__)

# sqlglot dialects (and a few source names) that are not SQL engines with IN.
# Standard SQL dialects — postgres, mysql, oracle, tsql, sqlite, and the rest
# of DialectMapper — support NOT IN and should use it.
_DIALECTS_WITHOUT_NOT_IN = frozenset(
    {
        "prql",
        "dune",
        "default",
        "dynamo",
        "himongo",
        "mongocdata",
        "mongo",
    }
)

_EXCLUSION_PHRASE = re.compile(
    r"\bnot\s+in\b|\bexcluding\b|\bother\s+than\b|\bexcept\b",
    re.IGNORECASE,
)


def dialect_supports_not_in(dialect: Optional[str]) -> bool:
    """True when this dialect should be asked to emit NOT IN.

    An empty dialect is treated as standard SQL and does support NOT IN.
    Non-SQL engines mapped to PRQL or Dune do not.
    """
    if dialect is None or str(dialect).strip() == "":
        return True
    key = str(dialect).lower().strip()
    resolved = str(resolve_sqlglot_dialect(key) or key).lower()
    return key not in _DIALECTS_WITHOUT_NOT_IN and resolved not in _DIALECTS_WITHOUT_NOT_IN


def question_requests_exclusion(question: Optional[str]) -> bool:
    """True when the question excludes values rather than asking not-equal."""
    if not question:
        return False
    return _EXCLUSION_PHRASE.search(question) is not None


def prefer_not_in(
    sql: str,
    *,
    question: Optional[str],
    dialect: Optional[str],
) -> str:
    """Rewrite WHERE/HAVING ``<>`` / ``!=`` against a literal to ``NOT IN``.

    Leaves the SQL unchanged when the question is not an exclusion, when the
    dialect has no NOT IN, when the comparison is column-to-column, or when
    parsing fails.
    """
    if not sql or not str(sql).strip():
        return sql
    if dialect_supports_not_in(dialect):
        sql = _collapse_not_expr_in(sql, dialect)

    if not question_requests_exclusion(question):
        return sql
    if not dialect_supports_not_in(dialect):
        return sql

    read_dialect = resolve_sqlglot_dialect(dialect) or None
    try:
        tree = sqlglot.parse_one(sql, read=read_dialect)
    except Exception:
        logger.error(
            "NOT IN rewrite skipped; SQL parse failed for dialect=%s",
            dialect,
            exc_info=True,
        )
        return sql

    literals: list[str] = []
    for neq in tree.find_all(exp.NEQ):
        if not _in_where_or_having(neq):
            continue
        literal_text = _literal_text_on_one_side(neq, read_dialect)
        if literal_text:
            literals.append(literal_text)

    if not literals:
        return sql

    rewritten = sql
    for literal_text in literals:
        updated = _replace_inequality_with_not_in(rewritten, literal_text)
        if updated is None:
            logger.debug(
                "NOT IN rewrite could not locate %s in generated SQL",
                literal_text,
            )
            continue
        rewritten = updated
    return rewritten


def _in_where_or_having(node: exp.Expression) -> bool:
    parent = node.parent
    while parent is not None:
        if isinstance(parent, (exp.Where, exp.Having)):
            return True
        if isinstance(parent, exp.Join):
            return False
        parent = parent.parent
    return False


def _unwrap(node: exp.Expression) -> exp.Expression:
    current = node
    while isinstance(current, exp.Paren):
        current = current.this
    return current


def _is_excluded_literal(node: exp.Expression) -> bool:
    value = _unwrap(node)
    if isinstance(value, exp.Neg):
        value = _unwrap(value.this)
    return isinstance(value, exp.Literal)


def _literal_text_on_one_side(neq: exp.NEQ, dialect: Optional[str]) -> Optional[str]:
    """Return the excluded literal as SQL text when the other side is not a literal."""
    left = neq.this
    right = neq.expression
    if left is None or right is None:
        return None
    right_literal = _is_excluded_literal(right)
    left_literal = _is_excluded_literal(left)
    if right_literal and not left_literal:
        value = right
    elif left_literal and not right_literal:
        value = left
    else:
        return None
    try:
        return _unwrap(value).sql(dialect=dialect)
    except Exception:
        logger.debug("Could not render excluded literal", exc_info=True)
        return None


def _collapse_not_expr_in(sql: str, dialect: Optional[str]) -> str:
    """Turn ``NOT expr IN (...)`` into ``expr NOT IN (...)``.

    Both are the same predicate. The keyword form is what exclusion questions
    should show.
    """
    read_dialect = resolve_sqlglot_dialect(dialect) or None
    try:
        tree = sqlglot.parse_one(sql, read=read_dialect)
    except Exception:
        logger.error(
            "NOT IN collapse skipped; SQL parse failed for dialect=%s",
            dialect,
            exc_info=True,
        )
        return sql

    rewritten = sql
    for node in tree.find_all(exp.Not):
        if not _in_where_or_having(node):
            continue
        inner = node.this
        if isinstance(inner, exp.Paren):
            inner = inner.this
        if not isinstance(inner, exp.In) or inner.this is None:
            continue
        try:
            subject = inner.this.sql(dialect=read_dialect)
        except Exception:
            logger.debug("Could not render NOT IN subject", exc_info=True)
            continue
        match = re.compile(
            r"NOT\s+" + re.escape(subject) + r"\s+IN\b",
            re.IGNORECASE,
        ).search(rewritten)
        if not match:
            continue
        rewritten = (
            rewritten[: match.start()]
            + f"{subject} NOT IN"
            + rewritten[match.end() :]
        )
    return rewritten


def _replace_inequality_with_not_in(sql: str, literal_sql: str) -> Optional[str]:
    """Replace ``expr <> literal`` / ``expr != literal`` with ``expr NOT IN (literal)``.

    The expression to the left of the operator is left as written.
    """
    match = re.compile(rf"(<>|!=)\s*{re.escape(literal_sql)}").search(sql)
    if not match:
        return None
    start, end = match.span()
    return sql[:start] + f"NOT IN ({literal_sql})" + sql[end:]
