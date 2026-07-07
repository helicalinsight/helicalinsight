from langchain_core.prompts import PromptTemplate
from helicalbi.common.app_config import default_sql_limit

final_sql_prompt="""
You are an expert {dialect} SQL engineer.

Your job is to generate a valid SQL query using the provided columns.
Use proper {dialect} syntax.
Donot add any new columns
Return ONLY the SQL query.




-----------------------------------------------------
Provided columns
 {query_plan_json}

-----------------------------------------------------
Use the below joins (do not invent new join other than this) ignore if empty
 {required_joins}


-----------------------------------------------------
Here is few business metrics for hint
 {required_metrics}




Generate the SQL query now based on the above details.  
Always use alias for selected columns.
Make sure syntactically proper query is generated. 
Add limit always (limit {default_sql_limit} unless asked for more)


Previous sql generated: (Use this only when context is related)
{prev_sql}


-----------------------------------------------------
below is the chat history:
{last_chats}


-----------------------------------------------------
User Question:
{user_question}

 """

final_sql_prompt_formatted = PromptTemplate.from_template(
    final_sql_prompt,
    partial_variables={"default_sql_limit": default_sql_limit},
)