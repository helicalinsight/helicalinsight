from langchain_core.prompts import PromptTemplate

success_response_method = """ AIDA
    Attention – Grab the user’s focus
    Interest – Build curiosity or relevance
    Desire – Create a strong need or want
    Action – Encourage the user to take the next step
"""
success_execution_prompt="""
Context:
User question: {user_query}

SQL: {sql_query}

Metadata: {metadata}

Task:
Generate a short metadata insight explaining the purpose and business value of the analysis.

Requirements:

* Keep it to 2–4 sentences.
* Use a natural, professional tone.
* Explain what business question the analysis helps answer and why it matters.
* Suggest a relevant next step when appropriate.
* Do not mention SQL, metadata, charts, or technical details.
* Do not infer or state any findings from the data.
* Focus on analytical intent and business impact, not results.

"""

success_prompt_formatted = PromptTemplate.from_template(success_execution_prompt)