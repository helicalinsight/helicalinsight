"""SQL generation, retry, and validation prompts/rules for agents."""

THINK_FLAT_SQL_GENERATION = (
    "Think mode: write one flat SELECT only. "
    "Do not use subqueries, CTEs (WITH), derived tables, or any nested SELECT. "
    "JOIN only base tables from the provided joins. "
    "Table Affinity: When the metric and dimension/date are on the same table (e.g., travel_details.travel_cost and travel_details.travel_date), write a single-table query with NO joins. "
    "Anti-Fan-Out: Never join two distinct transaction tables across an entity key (e.g., joining travel_details to meeting_details on employee_id) when computing sums or aggregates, as this creates a Cartesian product that severely inflates metrics. "
    "Single-Period Filters: When querying for a specific period (such as 'current year', 'most recent year', or 'prior year'), add an explicit WHERE filter for that exact period (e.g., EXTRACT(YEAR FROM ...) = ... or YEAR(...) = ...) rather than executing an unbounded multi-year query. "
    "If this question cannot be answered without a subquery, answer only the "
    "part that is a single grouped or filtered SELECT. "
    "Measure traits: stay at the measure grain from the semantic context and do not join a finer-grain table that duplicates those rows. "
    "If additivity is non-additive, do not SUM that measure; aggregate its formula inputs and compute the ratio in the SELECT. "
    "If semi-additive, do not sum across time. If additive, SUM at the measure grain."
)

THINK_FLAT_SQL_RETRY_DIRECTIVE = (
    "Think mode forbids subqueries. Rewrite as one flat SELECT that "
    "joins base tables only. Do not use CTEs, derived tables, or any "
    "nested SELECT. Prefer querying a single table if the metric and date are on the same table. "
    "Never join multiple transaction tables (e.g., travel_details and meeting_details) that duplicate aggregate rows. "
    "If the comparison needs another aggregate, drop that part — it belongs in a separate question."
)

THINK_SUBQUERY_REJECTION = (
    "Subqueries are not allowed in think mode. "
    "Write one flat SELECT that joins base tables only. "
    "If a comparison needs another aggregate, put that in a separate question."
)
