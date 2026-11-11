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


def kpi_format_instructions(count: int = 5) -> str:
    """Filled example. An empty answer list is what smaller models copy."""
    count = count if isinstance(count, int) and count > 0 else 5
    return (
        'JSON only. {"answer":["Question about one measure","Question about that measure by one dimension"],"reason":"from the schema"} '
        f"Put {count} questions in answer, taken from the schema above. "
        "Do not return an empty list. Do not copy the two sample questions."
    )
