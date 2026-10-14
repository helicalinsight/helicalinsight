"""SQL generation, retry, and validation prompts/rules for agents."""

THINK_FLAT_SQL_GENERATION = (
    "Think mode: write one flat SELECT only. "
    "Do not use subqueries, CTEs (WITH), derived tables, or any nested SELECT. "
    "JOIN only base tables from the provided joins. "
    "If this question cannot be answered without a subquery, answer only the "
    "part that is a single grouped or filtered SELECT."
)

THINK_FLAT_SQL_RETRY_DIRECTIVE = (
    "Think mode forbids subqueries. Rewrite as one flat SELECT that "
    "joins base tables only. Do not use CTEs, derived tables, or any "
    "nested SELECT. If the comparison needs another aggregate, drop "
    "that part — it belongs in a separate question."
)

THINK_SUBQUERY_REJECTION = (
    "Subqueries are not allowed in think mode. "
    "Write one flat SELECT that joins base tables only. "
    "If a comparison needs another aggregate, put that in a separate question."
)
