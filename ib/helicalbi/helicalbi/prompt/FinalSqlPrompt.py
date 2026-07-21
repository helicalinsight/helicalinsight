from langchain_core.prompts import PromptTemplate

from helicalbi.common.app_config import default_sql_limit, hide_prompt_reason

_FINAL_SQL_REASON_LINE = (
    "If you cannot generate the sql due to lack of more information "
    "highlight the same in your reason. \n"
)


def _final_sql_important_block() -> str:
    if hide_prompt_reason:
        return """#IMPORTANT
Dont generate inappropriate sql other than provided table and column 
Never invent tables from previous SQL — only use tables/columns listed above.
"""
    return f"""#IMPORTANT
{_FINAL_SQL_REASON_LINE}Dont generate inappropriate sql other than provided table and column 
Never invent tables from previous SQL — only use tables/columns listed above.
"""


final_sql_prompt="""
You are an expert {dialect} SQL engineer.

Your job is to generate a valid SQL query using the provided columns.
Use proper {dialect} syntax.
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
 {required_column_description}

-----------------------------------------------------
Default column/measure functions (use when aggregating or grouping)
 {required_functions}

-----------------------------------------------------
Column sort orders for ORDER BY
Map sortOrder / sort values to SQL directions: 0 or Ascending = ASC, 1 or
Descending = DESC (or use an explicit asc/desc value when provided). Prefer
this ordering when the user question does not request a different sort.
 {column_sort_orders}

-----------------------------------------------------
Use the below joins (do not invent new join other than this) ignore if empty
 {required_joins}


-----------------------------------------------------
Domain context
 {domain_context}


-----------------------------------------------------
Business metrics for required columns
 {required_metrics}




Generate the SQL query now based on the above details.  
Always use alias for selected columns.
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

final_sql_prompt = final_sql_prompt.replace(
    "{_final_sql_important_block}",
    _final_sql_important_block(),
)

final_sql_prompt_formatted = PromptTemplate.from_template(
    final_sql_prompt,
    partial_variables={"default_sql_limit": default_sql_limit},
)
