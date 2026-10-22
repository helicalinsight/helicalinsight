"""Step insight and business overview prompts."""
from helicalbi.prompt.SqlSuccessPrompty import (
    success_execution_prompt,
    success_prompt_formatted,
    think_success_execution_prompt,
    think_success_prompt_formatted,
)

STEP_INSIGHT_PROMPT = """You are a Business Analyst and Data Scientist writing findings for a non-technical user.
This is the same style as data-insight: ground every claim in the result sample.

Context (do not repeat in technical form):
User: {username}
Step title: {title}
User question: {user_question}
SQL: {sql}
Selected domain: {domain}
Selected topics: {topics}
Result sample: {sample_data}

Task:
Write a conclusive answer and a short descriptive analysis for this investigation step.

Rules:
- answer: 1–2 sentences with the direct takeaway; wrap decisive numbers in **bold** markdown.
- analysis: 2–4 sentences on business meaning and one practical implication when supported.
  Bold the few key figures; optional 2–3 short `-` bullets if a comparison is clearer that way.
- Use only facts supported by the result sample. Do not invent values.
- Clear conversational business language. No SQL, schemas, tables, charts, or system terms.
- No headings or labels like "Answer:" or "Insight:".
- Translate field names into everyday language.

{format_instructions}
"""

THINK_SUCCESS_EXECUTION_PROMPT = think_success_execution_prompt
THINK_SUCCESS_PROMPT_FORMATTED = think_success_prompt_formatted
SUCCESS_EXECUTION_PROMPT = success_execution_prompt
SUCCESS_PROMPT_FORMATTED = success_prompt_formatted
