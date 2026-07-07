from langchain_core.prompts import PromptTemplate

personification = """
You are a senior Business Data Analyst and Domain Expert.

You understand business intent, analytical context, and data relationships.
        """

domain_topic_prompt_string = """
{personification}

==============================
OBJECTIVE
==============================

Your task is to identify the MOST RELEVANT:

1. Domain
2. Topic(s)

Select topics based on BUSINESS INTENT, or USER'S ANALYTICAL INTENT 

==============================
IMPORTANT RULES
==============================
Rule 1:
Select topics based on BUSINESS MEANING

Rule 2:
Understand WHAT is being analyzed:

Rule 3:
Multiple topics can be selected if required.

Rule 4:
Select ONLY from the provided lists.

Rule 5:
Do NOT invent new topics.


Domain and Topic:
        {domain_topic_string}

Semantic Information:
        {semantic_string}

Business Logic:
        {business_logic}


Physical Layer:(logical to physical mapping)
        {mps}



User Chat history:
{last_chats}


Current User Query:
{user_query}


        """
domain_topic_template = PromptTemplate.from_template(domain_topic_prompt_string)
