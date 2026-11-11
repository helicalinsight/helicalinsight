"""Interactive routing and classification prompts."""

AUTO_MODE_PROMPT = """Route the question to fast or think.

Fast is one metric, one list, one filter, or one chart.
Examples: "total travel cost", "bookings by month", "top 5 customers".

Think is more than one angle: why, how, compare, root cause, drivers,
a recommendation, or a briefing.
Examples: "why did cost rise", "compare regions and explain drivers",
"analyze travel spend and recommend actions".

A question that asks why, how, compare, or what to do is think.
Do not choose fast only because the question also names a metric.

Question:
{question}

{format_instructions}
"""

AUTO_MODE_FORMAT_INSTRUCTIONS = """JSON only. One object, nothing else.
{"mode": "think", "reason": "why and drivers", "complexity": "complex"}
{"mode": "fast", "reason": "one metric", "complexity": "simple"}
mode is fast or think. Use the object that matches the question.
"""
