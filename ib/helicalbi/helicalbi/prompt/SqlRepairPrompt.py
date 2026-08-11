from langchain_core.prompts import PromptTemplate

# Prompt used by the SQL self-correction loop. When a generated query fails to
# execute against the database, the failing SQL and the exact database error are
# fed back to the model together with the same allowed schema context that was
# used to generate it, and a corrected read-only SELECT is requested.
sql_repair_prompt_string = """
You are an expert {dialect} SQL engineer fixing a query that FAILED to execute.

The user asked:
{user_query}

The previous SQL that was generated is:
{failed_sql}

Executing it on the {dialect} database "{dbname}" returned this error:
{db_error}

You may ONLY use the tables, columns, joins and metrics listed here. Do not
invent any new table or column:
{allowed_context}

Task:
Return a corrected {dialect} SQL query that fixes the cause of the error and
answers the user's question.

Requirements:
- Return ONLY the corrected SQL query. No explanation, no markdown fences, no commentary.
- The statement MUST be a single read-only SELECT. Never emit INSERT, UPDATE,
  DELETE, DROP, ALTER, TRUNCATE, MERGE, or multiple statements.
- Use valid {dialect} syntax and only the tables/columns/joins/metrics provided above.
- Address the specific problem indicated by the error, for example an unknown
  column, an invalid join, a GROUP BY / aggregation mistake, or a dialect
  specific function.
- If the error cannot be fixed with the provided context, return the closest
  valid SELECT you can using only the allowed context.
"""

sql_repair_prompt = PromptTemplate.from_template(sql_repair_prompt_string)
