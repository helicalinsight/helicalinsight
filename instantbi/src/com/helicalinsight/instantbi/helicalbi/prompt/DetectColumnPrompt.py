from langchain_core.prompts import PromptTemplate

detect_column_prompt_string = """
Pick the columns for this question. Use only names from the schema.
Dialect: {dialect}
A year, quarter, or month filter uses this dialect's date function beside the date column.
Do not write that filter as CAST of a date range.
Include WHERE, GROUP BY, and ORDER BY columns, not only SELECT columns.
columnName: table.column. Put a formula beside a computed column.
pickedDimensions and pickedMetrics: cube names from the schema, not SQL.
selectColumnName: SELECT columns only.
If the user asks for a row count such as top 10, set limit. Otherwise leave limit empty.
Never use * or COUNT(*). For a count, pick a real column (prefer the primary key).

Schema:
{table_column_description}

Chat:
{last_chats}

Question:
{user_query}

Synonyms:
{required_synonyms}
"""

column_formatted = PromptTemplate.from_template(detect_column_prompt_string)
