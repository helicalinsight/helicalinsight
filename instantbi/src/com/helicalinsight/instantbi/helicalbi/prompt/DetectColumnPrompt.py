from langchain_core.prompts import PromptTemplate

detect_column_prompt_string = """
Pick the fields for this question from the catalog.
Dialect: {dialect}

columnName: physical columns used anywhere in the query, written table.column.
selectColumnName: physical columns that appear in the SELECT list.
pickedDimensions: dimension names from the catalog.
pickedDimensions are dimension names from the Dimensions section or alias labels, never quoted SQL.
pickedMetrics: measure names from the catalog.
pickedMetrics are measure names from the Measures section or alias labels, never quoted SQL.
limit: the requested row count, such as top 10. Leave empty when no count is requested.
Never use * or COUNT(*). A count uses the id column named on that measure.
Do not add a column that is not in the list.

A field with a Columns heading contributes each of those physical columns to columnName.
A measure is selected under its Name. Its formula is applied later.
A relative year, quarter, or month filter (this year, last year, YTD) uses the date column named on that field.
A literal calendar year uses the catalog Year dimension when present, including its Formula as written.

Catalog fields use these headings:
Name: business name. Return it in pickedDimensions or pickedMetrics.
Column: one physical table.column. Return it in columnName.
Columns: physical columns a computed measure uses. Return each in columnName.
Type: data type.
Formula: stored expression. Keep columnName as the physical Column or Columns.
If a column is derived from a formula or database function, put that formula along with the column name.
Sort: stored sort. Natural means leave ORDER BY empty unless the question asks for a ranking.
Use: when to pick this field.
Also: other words for the same field.
Examples: sample questions for this field.

Catalog:
{table_column_description}

Chat:
{last_chats}

Question:
{user_query}
"""

column_formatted = PromptTemplate.from_template(detect_column_prompt_string)
