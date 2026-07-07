from langchain_core.prompts import PromptTemplate

prompt_string = """
Act as a Data Expert    
    
Context: 
    You will be provided with semantic layer, which consists of domain and topics. 
    

    {domain_topic_string}

    {semantic_string}
    
    {business_logic}


    Dimension and its Table:
    {mapping_string}
Task:
 {user_query}
 
"""

kpi_prompt_template = PromptTemplate.from_template(prompt_string)
