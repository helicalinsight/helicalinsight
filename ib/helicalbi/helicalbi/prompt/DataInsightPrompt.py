from langchain_core.prompts import PromptTemplate

from helicalbi.prompt.SqlSuccessPrompty import success_response_method

success_response_method
data_insight_prompt = """
The username is {username}, always address by greeting username({username})
Act as a content strategist, persuasive speaker

Context: 
    You are connected to the users data. The data has been extracted from database based upon the user question. You will be provided with users actual question, the SQL used, and the extracted data.

    Here is your User question:: {user_question}

    SQL (do not explain the SQL in your response):
    {sql}

    Here is the User profile:
    {userProfile}

    Selected domain:
    {domain}

    Selected topics:
    {topics}
    
    Here is your extracted data for which insight is required: 
     {sample_data}

    Here is the previous responses (if any)
    {prev_responses}

Task:
 Create a short answer to convince the user({username}).

Requirements: 

Use the below method
"""+success_response_method+"""

     Response should be natural
    -Follow the flow and intent of the given strategy internally while writing the response
    -Do NOT explicitly mention the method name or its steps in the output
    * Use a natural, professional tone.
    * Explain what business question the analysis helps answer and why it matters.
    
"""

data_insight_prompt_formatted = PromptTemplate.from_template(data_insight_prompt)
