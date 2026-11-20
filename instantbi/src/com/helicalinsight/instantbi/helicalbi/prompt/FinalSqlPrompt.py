import re

from helicalbi.common import app_config

_FINAL_SQL_REASON_LINE = (
    "If you cannot generate the sql due to lack of more information "
    "highlight the same in your reason. \n"
)


def _final_sql_important_block() -> str:
    rules = (
        "Use only the listed tables and columns. "
        "Never copy a slot (<table>, <alias>, <column>, <date_column>). "
        "Never write a table named t or a column named date_col unless listed. "
        "FROM <table> <alias>. Qualify columns with the alias. "
        "Never use SELECT * or COUNT(*)."
    )
    if app_config.hide_prompt_reason:
        return "#IMPORTANT\n" + rules + "\n"
    return "#IMPORTANT\n" + _FINAL_SQL_REASON_LINE + rules + "\n"


_DATE_FUNCTIONS = """
Filter values that are database functions
Use the listed {dialect} function, for example EXTRACT(YEAR FROM <alias>.<date_column>).
<date_column> is a slot. Substitute the provided date column.
"""

_DATE_RANGES = """
Date ranges
Write <alias>.<date_column> BETWEEN '2024-01-01' AND '2024-12-31'.
Do not write that range as IN (date1, date2).
"""

_RELATIVE_PERIODS = """
Relative periods
When the question means a period relative to today (this year, current year, last year, prior year, YTD),
filter the date column with BETWEEN, or with CURRENT_DATE and CURRENT_DATE minus 1 year.
Do not write EXTRACT(YEAR FROM CURRENT_DATE) - 1 inside an IN list.
"""

_ABSOLUTE_YEAR = """
Absolute calendar year
When the question names a literal year, filter with the year formula or dialect date function
from Descriptions, Metrics, or Functions. Copy that expression exactly and compare it to the year value.
Do not rewrite that filter as a date-column BETWEEN range.
"""

final_sql_prompt = """
You are an expert {dialect} SQL engineer. Write one query from the provided columns.
A slot is not a name. Never write <table>, <alias>, <column>, or <date_column> into the SQL.
Never write a table named t or a column named date_col unless listed.
Use only the listed tables and columns.
Never use SELECT * or COUNT(*).

SELECT
Alias every selected expression with the dimension, measure, or hierarchy display name.
A snake_case, camelCase, or ALLCAPS alias becomes Title Case.
For a count write COUNT(<alias>.<column>) or COUNT(DISTINCT <alias>.<column>).
When a metric or a measure has a formula, copy that formula into the SQL exactly as written. Do not rewrite, simplify, or re-aggregate it. Do not select the measure name as a column.
Do not apply formatString. Formatting is done in visualization.
additive: SUM across dimensions at that grain is valid.
non-additive: do not SUM the measure. Compute it from its formula components inside each group.
semi-additive: do not SUM across time. Sum only across non-time dimensions.

FROM
Every base table uses its original table name and an alias: FROM <table> <alias>.
Qualify every column with that alias. Never write FROM <alias> alone.

JOIN
Use only the joins provided below. Do not invent a join.
If every column in the SELECT clause belongs to the same table, do not use a JOIN.
Stay at the measure grain. Do not join a finer-grain table that duplicates those rows.

WHERE
When the user excludes a value, write NOT IN (...), including one value.
Use <> or != for that exclusion only when {dialect} has no IN / NOT IN.
Avoid regex (~, ~*, !~, !~*, RLIKE, SIMILAR TO). Use a function from the provided database functions list.

HAVING
Use HAVING only for a simple literal.
Do NOT write nested scalar subqueries in WHERE or HAVING.
JOINs preferred over nested subqueries.
For an above/below average by group question, join a grouped query to a query that computes the average.

ORDER BY
Only a dimension sort of Ascending (ASC) or Descending (DESC) is used.
Ignore Natural. Do not ORDER BY a measure.
If no sort is configured, omit ORDER BY unless the question asks for a ranking.

LIMIT
Add limit {default_sql_limit}.

Columns
{query_plan_json}

Descriptions
{required_column_description}

Functions
{required_functions}

Sorts
{column_sort_orders}

Joins
{required_joins}

Domain
{domain_context}

Metrics
{required_metrics}

Previous SQL
Use this only when it is related.
Ignore previous SQL that uses tables or columns outside the provided list.
{prev_sql}

Chat
{last_chats}

Question
{user_question}

{_final_sql_important_block}
"""


def format_metrics_for_prompt(metrics: list) -> str:
    """One SQL expression per measure. The table name is a header, not a field."""
    grouped: dict[str, list[str]] = {}
    order: list[str] = []
    for metric in metrics or []:
        if not isinstance(metric, dict):
            continue
        name = (
            metric.get("metric")
            or metric.get("measure_name")
            or metric.get("column_alias")
        )
        name = str(name or "").strip()
        if not name:
            continue
        tables = [str(table) for table in (metric.get("tables") or []) if table]
        table = tables[0] if tables else str(metric.get("table_alias") or "").strip()
        formula = str(metric.get("formula") or "").strip()
        if not formula:
            column = str(metric.get("column_name") or "").strip()
            target = f"{table}.{column}" if table and column else column
            agg = _sql_aggregator(metric.get("aggregator"))
            if agg and target:
                formula = f"{agg}({target})"
            else:
                formula = target
        line = f"{name} = {formula}" if formula else name
        if table not in grouped:
            order.append(table)
            grouped[table] = []
        grouped[table].append(line)
    blocks = []
    for table in order:
        lines = grouped[table]
        if table:
            blocks.append(table + "\n" + "\n".join(lines))
        else:
            blocks.append("\n".join(lines))
    return "\n".join(blocks)


def _sql_aggregator(value) -> str:
    return {
        "sum": "SUM",
        "count": "COUNT",
        "avg": "AVG",
        "average": "AVG",
        "min": "MIN",
        "max": "MAX",
    }.get(str(value or "").strip().lower(), "")


def render_final_sql_prompt(question: str = "") -> str:
    """Core SQL prompt, plus only the rule blocks this question needs."""
    extra = _sections_for(question)
    body = final_sql_prompt.replace(
        "{_final_sql_important_block}",
        _final_sql_important_block(),
    )
    if not extra:
        return body
    return body.replace("\nQuestion\n", extra + "\nQuestion\n", 1)


_RELATIVE_PERIOD_MARKERS = (
    "this year",
    "current year",
    "last year",
    "prior year",
    "previous year",
    "ytd",
    "pytd",
    "this month",
    "last month",
    "this quarter",
    "last quarter",
    "this week",
    "last week",
    "today",
    "yesterday",
)


def _has_absolute_year(text: str) -> bool:
    return bool(re.search(r"\b(?:year\s+)?(?:19|20)\d{2}\b", text))


def _has_relative_period(text: str) -> bool:
    return any(marker in text for marker in _RELATIVE_PERIOD_MARKERS)


def _sections_for(question: str) -> str:
    text = f" {str(question or '').lower()} "
    parts = []
    date_words = ("year", "quarter", "month", "date", "today", "week")
    if any(word in text for word in date_words) or _has_absolute_year(text):
        parts.append(_DATE_FUNCTIONS)
    if _has_absolute_year(text):
        parts.append(_ABSOLUTE_YEAR)
    if _has_relative_period(text):
        parts.append(_RELATIVE_PERIODS)
    if any(word in text for word in ("between", "range", " from ", " to ")):
        parts.append(_DATE_RANGES)
    return "".join(parts)
