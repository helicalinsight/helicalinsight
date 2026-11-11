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
Do not replace a date or time function with CAST(<alias>.<date_column> AS date).
"""

_DATE_RANGES = """
Date ranges
Write <alias>.<date_column> BETWEEN '2024-01-01' AND '2024-12-31'.
Do not write that range as IN (date1, date2).
"""

_RELATIVE_PERIODS = """
Relative periods
Filter the date column with BETWEEN, or with CURRENT_DATE and CURRENT_DATE minus 1 year.
Do not write EXTRACT(YEAR FROM CURRENT_DATE) - 1 inside an IN list.
"""

_EXCLUSIONS = """
Exclusion filters
When the user excludes a value, write NOT IN (...), including one value.
Use <> or != for that exclusion only when {dialect} has no IN / NOT IN.
"""

_AVERAGES = """
JOINs preferred over nested subqueries
Do NOT write nested scalar subqueries in WHERE or HAVING.
For an above/below average by group question, join a grouped query to a query that computes the average.
"""

final_sql_prompt = """
You are an expert {dialect} SQL engineer. Write one query from the provided columns.
A slot is not a name. Never write <table>, <alias>, <column>, or <date_column> into the SQL.
Never write a table named t or a column named date_col unless that exact name is listed.
Every base table uses its original table name and an alias: FROM <table> <alias>.
Qualify every column with that alias. Never write FROM <alias> alone.
Never use SELECT * or COUNT(*). For counts write COUNT(<alias>.<column>) or COUNT(DISTINCT <alias>.<column>).
Avoid regex (~, ~*, !~, !~*, RLIKE, SIMILAR TO). Use a function from the provided database functions list.
If every column in the SELECT clause belongs to the same table, do not use a JOIN.
If a measure has a formula, expand that formula. Do not select the measure name as a column.
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


def _sections_for(question: str) -> str:
    text = f" {str(question or '').lower()} "
    parts = []
    if any(word in text for word in ("year", "quarter", "month", "date", "today", "week")):
        parts.append(_DATE_FUNCTIONS)
        parts.append(_RELATIVE_PERIODS)
    if any(word in text for word in ("between", "range", " from ", " to ")):
        parts.append(_DATE_RANGES)
    if any(word in text for word in ("not in", "except", "excluding", "other than", "exclude")):
        parts.append(_EXCLUSIONS)
    if any(word in text for word in ("average", "above", "below")):
        parts.append(_AVERAGES)
    return "".join(parts)
