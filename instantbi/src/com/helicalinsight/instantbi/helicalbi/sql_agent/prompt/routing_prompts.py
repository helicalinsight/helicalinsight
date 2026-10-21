"""Interactive routing and classification prompts."""

AUTO_MODE_PROMPT = """You route user questions to Fast or Think.

Fast: one metric, list, filter, or a single chart.
Examples: "total travel cost", "bookings by month", "top 5 customers".

Think: investigation across several angles — why/how, comparison, root cause,
drivers, recommendations, or an executive briefing.
Examples: "why did cost rise", "compare regions and explain drivers",
"analyze travel spend and recommend actions".

Question:
{question}

{format_instructions}
"""
