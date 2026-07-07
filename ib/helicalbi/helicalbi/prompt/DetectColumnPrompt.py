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


{relationship_of_table}


Below is chat history:
{last_chats}

Current User Query:
{user_query}


Below is some synonyms for the table you can correlate
{required_synonyms}


Instructions
1 in the column names determine the column that will be part of the sql
2 Use exact column names from the schema; quote names that contain spaces (e.g. "Bill Qty")
"""

column_formatted = PromptTemplate.from_template(detect_column_prompt_string)