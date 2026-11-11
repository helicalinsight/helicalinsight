from langchain_core.prompts import PromptTemplate

data_insight_prompt = """
Write 2–4 sentences for a business user. Follow the current query and the result sample when they differ from the question.
Do not invent numbers. Do not mention SQL, tables, columns, metadata, or file names.
No headings or labels.

User: {username}
Question: {user_question}
Query: {sql}
Profile: {userProfile}
Domain: {domain}
Topics: {topics}
Sample: {sample_data}
Chat history: {last_chats}
"""

data_insight_prompt_formatted = PromptTemplate.from_template(data_insight_prompt)

data_insight_summary_prompt = """
Answer the original question in 2–5 sentences using the insights below.
Lead with the answer. After a claim, add [1] or [2] for the supporting question that backs it.
Do not invent numbers or indexes. Do not mention SQL, tables, columns, or metadata.

Question:
{user_question}

Original insight:
{original_insight}

Supporting insights:
{numbered_insights}

User: {username}
"""

data_insight_summary_prompt_formatted = PromptTemplate.from_template(data_insight_summary_prompt)
