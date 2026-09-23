from helicalbi.common import app_config

_FINAL_SQL_REASON_LINE = (
    "If you cannot generate the sql due to lack of more information "
    "highlight the same in your reason. \n"
)


def _final_sql_important_block() -> str:
    if app_config.hide_prompt_reason:
        return """#IMPORTANT
Dont generate inappropriate sql other than provided table and column 
Never invent tables from previous SQL — only use tables/columns listed above.
Never use SELECT * or COUNT(*). Always use explicit column names (COUNT(column) / COUNT(DISTINCT column), never *).
"""
    return f"""#IMPORTANT
{_FINAL_SQL_REASON_LINE}Dont generate inappropriate sql other than provided table and column 
Never invent tables from previous SQL — only use tables/columns listed above.
Never use SELECT * or COUNT(*). Always use explicit column names (COUNT(column) / COUNT(DISTINCT column), never *).
"""


final_sql_prompt="""
You are an expert {dialect} SQL engineer.

Your job is to generate a valid SQL query using the provided columns.
Use proper {dialect} syntax.
Avoid regex operators or regex patterns such as ~, ~*, !~, !~*, RLIKE, SIMILAR TO, or character-class patterns like '^[aeiou]'.
If a text/pattern function is needed, use only a matching function from the provided database functions list — never an ad-hoc regex expression in WHERE/HAVING.
Donot add any new table and columns. Use the provided one only. 
Return ONLY the SQL query.

#COMPUTED MEASURES
Some provided columns are computed measures defined by a formula (marked as
"COMPUTED measure" or shown with a "formula:" / "computed formula:" in the
descriptions or business metrics below).
For these, DO NOT select them as a physical column (e.g. never write
"table"."Measure Name"). Instead, translate the formula into a valid {dialect}
SQL expression and alias it with the measure name.
Examples: SUM(t.col) -> SUM("t"."col"); COUNT(distinct t.col) -> COUNT(DISTINCT "t"."col");
COUNT(when t.col=Val) -> COUNT(CASE WHEN "t"."col" = 'Val' THEN 1 END).
Use the physical table/column references named inside the formula, not the measure name.




-----------------------------------------------------
Provided columns
 {query_plan_json}

-----------------------------------------------------
Required column descriptions
These are the full picked cube items arranged by table (dimensions, hierarchy
levels, measures, and blank-column computed measures), including hierarchy and
aiContext details. Do not expect or apply formatString here — formatting is
handled later in visualization.
 {required_column_description}

-----------------------------------------------------
Default column/measure functions (use when aggregating or grouping)
 {required_functions}

-----------------------------------------------------
Column sort orders for ORDER BY
Only dimension Ascending=ASC and Descending=DESC are listed.
Ignore none/empty/Natural sorts. Do not ORDER BY measures (column or formula).
If the section below says no sorts are configured, do not invent ORDER BY for
all selected columns — omit ORDER BY unless the user explicitly asks to sort
or rank. Prefer listed ordering when the user question does not request a
different sort.
 {column_sort_orders}

-----------------------------------------------------
Use the below joins (do not invent new join other than this) ignore if empty
 {required_joins}
If every column in the SELECT clause belongs to the same table, do not use a JOIN
even if joins are provided above. Query that single table only.

-----------------------------------------------------
JOINs preferred over nested subqueries
Prefer JOIN / INNER JOIN / CROSS JOIN of pre-aggregated derived tables.
Do NOT write nested scalar subqueries in WHERE or HAVING for
above-average, below-average, top-N-vs-average, or similar comparisons
(e.g. avoid HAVING SUM(x) > (SELECT AVG(...) FROM (SELECT SUM(x) ...))).

For "above/below average by group" style questions:
1. Build one grouped derived table with the measures (reuse the provided
   catalog joins inside it when multiple tables are required).
2. Build a second derived table that computes AVG(...) of those grouped
   measures (or other benchmark aggregates).
3. JOIN them and filter with ON / WHERE using the joined average columns.

Example shape (adapt names/joins to the provided schema):
SELECT g."Dim", g."Measure", g."Other Measure"
FROM (
  SELECT "t"."dim" AS "Dim", SUM("t"."m") AS "Measure", COUNT("t"."id") AS "Other Measure"
  FROM "t"
  /* catalog joins here when needed */
  GROUP BY "t"."dim"
) g
INNER JOIN (
  SELECT AVG(s."Measure") AS "Avg Measure", AVG(s."Other Measure") AS "Avg Other Measure"
  FROM (
    SELECT "t"."dim" AS "Dim", SUM("t"."m") AS "Measure", COUNT("t"."id") AS "Other Measure"
    FROM "t"
    /* same catalog joins as above when needed */
    GROUP BY "t"."dim"
  ) s
) a ON (
  g."Measure" > a."Avg Measure"
  AND g."Other Measure" > a."Avg Other Measure"
)
LIMIT {default_sql_limit}

Use HAVING only for simple literals or expressions that do not nest SELECT.
Do not invent physical join keys beyond the provided joins list; derived-table
JOIN/ON conditions may compare aggregated measure columns as shown above.

-----------------------------------------------------
Filtered domain / topics for this query
Use only this selected domain/topic context (with mapped component id+name).
 {domain_context}


-----------------------------------------------------
Business metrics for required columns
 {required_metrics}




Generate the SQL request now based on the above details.  
Never use SELECT * or COUNT(*). Always list explicit column names from the provided columns.
Avoid regex operators/patterns (~, ~*, '^[aeiou]'). If a text/pattern function is needed, use only a matching function from the provided database functions list.
For counts / "how many" questions, always write COUNT("table"."column") or COUNT(DISTINCT "table"."column") using a real schema column (prefer unique identifier / primary key). Never COUNT(*).
Always use an alias for every selected column/expression in the SELECT clause.
Prefer provided dimension/measure/hierarchy display names (alias labels) when available.
If a SELECT alias is technical — underscore_separated / snake_case, camelCase, ALLCAPS,
comma-separated tokens, or similar — replace it with a short business-friendly Title Case
name (e.g. travel_cost → "Travel Cost", meetCancellationStatus → "Meet Cancellation Status").
Do not leave raw underscore_separated or other machine-style names as the visible SELECT alias.
Make sure syntactically proper query is generated. 
Add limit always (limit {default_sql_limit})


Previous sql generated: (Use this only when context is related.
Ignore any previous SQL that uses tables/columns outside the provided columns list.)

 
{prev_sql}


-----------------------------------------------------
below is the chat history:
{last_chats}


-----------------------------------------------------
User Question:
{user_question}


{_final_sql_important_block}

 """


def render_final_sql_prompt() -> str:
    """Return the final-SQL prompt with live application-config substitutions.

    ``hide_prompt_reason`` and ``default_sql_limit`` are read from
    ``app_config`` at call time so Admin settings apply without a restart.
    """
    return final_sql_prompt.replace(
        "{_final_sql_important_block}",
        _final_sql_important_block(),
    )
