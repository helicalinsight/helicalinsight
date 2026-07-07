import logging

import sqlglot
from sqlglot import exp

from helicalbi.common.DialectMapper import resolve_sqlglot_dialect

logger = logging.getLogger(__name__)

_EMPTY_SQL_PARTS = {
    "select": False,
    "where": False,
    "group_by": False,
    "having": False,
    "order_by": False,
    "limit": False,
    "join": False,
    "function": False,
}


def analyze_sql(sql, dialect=None):
    if not sql:
        return dict(_EMPTY_SQL_PARTS)

    try:
        parsed = sqlglot.parse_one(sql, read=resolve_sqlglot_dialect(dialect))
    except Exception:
        logger.error("SQL parse failed for dialect=%s", dialect, exc_info=True)
        return dict(_EMPTY_SQL_PARTS)

    result = {
        "select": parsed.find(exp.Select) is not None,
        "where": parsed.find(exp.Where) is not None,
        "group_by": parsed.find(exp.Group) is not None,
        "having": parsed.find(exp.Having) is not None,
        "order_by": parsed.find(exp.Order) is not None,
        "limit": parsed.find(exp.Limit) is not None,
        "join": parsed.find(exp.Join) is not None,
        "function": any(isinstance(node, exp.Func) for node in parsed.walk())
    }

    return result


if __name__ == "__main__":
    sql = """
    SELECT c.client_id, c.name, SUM(t.amount) AS total_spent
    FROM clients c
    JOIN transactions t ON c.client_id = t.client_id
    WHERE t.type = 'domestic'
    GROUP BY c.client_id, c.name
    ORDER BY total_spent DESC
    LIMIT 1;
    """
    logger.info("SQL analysis: %s", analyze_sql(sql))