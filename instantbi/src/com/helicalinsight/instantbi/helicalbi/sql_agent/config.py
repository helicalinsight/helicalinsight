"""System prompts and tunable parameters for the InstantBI dashboard agent.

Numeric knobs are defined in ``helicalbi.sql_agent.constants`` and re-exported
here so historical ``from helicalbi.sql_agent.config import …`` imports keep
working. Prompt strings are re-exported from ``helicalbi.sql_agent.prompt``.
"""

from helicalbi.sql_agent.constants import (
    DEFAULT_DASHBOARD_SUB_QUESTIONS,
    DEFAULT_EMBEDDING_DIM,
    DEFAULT_MAX_TOOL_LOOPS,
    DEFAULT_RESULT_ROW_CAP,
    DEFAULT_SCHEMA_TOP_K,
    RECURSION_LIMIT,
    THINK_EXTRA_QUESTIONS,
)
from helicalbi.sql_agent.prompt import (
    CONTEXT_PLAN_PROMPT,
    DEEP_DIVE_QUESTION_FRAMING_PROMPT,
    INVESTIGATION_EVALUATION_PROMPT,
    PLAN_EXECUTION_RULES,
    SEMANTIC_GROUNDING_PROMPT,
    SQL_SHAPE_DERIVED_TABLES,
    SYNTHESIZER_PROMPT,
    THINK_FLAT_SQL_GENERATION,
    THINK_FLAT_SQL_PLAN_RULES,
    TOOL_AGENT_PROMPT,
    TOOL_ORCHESTRATION_PROMPT,
)

__all__ = [
    "DEFAULT_SCHEMA_TOP_K",
    "DEFAULT_DASHBOARD_SUB_QUESTIONS",
    "THINK_EXTRA_QUESTIONS",
    "DEFAULT_MAX_TOOL_LOOPS",
    "DEFAULT_RESULT_ROW_CAP",
    "DEFAULT_EMBEDDING_DIM",
    "RECURSION_LIMIT",
    "CONTEXT_PLAN_PROMPT",
    "DEEP_DIVE_QUESTION_FRAMING_PROMPT",
    "INVESTIGATION_EVALUATION_PROMPT",
    "PLAN_EXECUTION_RULES",
    "SEMANTIC_GROUNDING_PROMPT",
    "SQL_SHAPE_DERIVED_TABLES",
    "SYNTHESIZER_PROMPT",
    "THINK_FLAT_SQL_GENERATION",
    "THINK_FLAT_SQL_PLAN_RULES",
    "TOOL_AGENT_PROMPT",
    "TOOL_ORCHESTRATION_PROMPT",
]
