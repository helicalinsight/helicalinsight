from langchain_core.prompts import PromptTemplate

detect_column_prompt_string = """
You are a senior data analyst and query planner.
Your task is to analyze a user question and provide the column names which will be used in the sql.

Important Business Information:

Identify the important business entities mentioned in the user query.
Consider all the possible column, of the sql query parts 

Use below Input Schema to determine the required column
Consider the full sql query, not only select clause. 
Input Schema:
{table_column_description}




Below is chat history:
{last_chats}

Current User Query:
{user_query}


Below is some synonyms for the table you can correlate (ignore if empty)
{required_synonyms}


Instructions
1 in the column names determine the column that will be part of the sql
2 Use exact column names from the schema
3. Pick qualified column name  (eg: table_name.column_name) 
4. Pick all possible column that can be in any part say where clause , having clause 
4. Dont add column beyond the list. 
5. If it is derived from some formula/db function  then put the formula along with  the column name 
6. When the schema lists cube dimensions and measures, also populate pickedDimensions with the dimension names used in the query
7. When the schema lists cube measures, also populate pickedMetrics with the measure names used in the query
8. If the user asks for a specific number of rows (e.g. top 10, first 5, limit 50, show 20), set limit to that number. Otherwise leave limit null/empty.
"""

column_formatted = PromptTemplate.from_template(detect_column_prompt_string)