"""Consolidated prompts and prompt directives involved in Think mode."""
from helicalbi.sql_agent.prompt.insight_prompts import (
    STEP_INSIGHT_PROMPT,
    THINK_SUCCESS_EXECUTION_PROMPT,
    THINK_SUCCESS_PROMPT_FORMATTED,
)
from helicalbi.sql_agent.prompt.plan_prompts import (
    THINK_FLAT_SQL_PLAN_RULES,
    THINK_QUESTION_SYSTEM,
    _THINK_QUESTION_SYSTEM,
)
from helicalbi.sql_agent.prompt.routing_prompts import AUTO_MODE_PROMPT
from helicalbi.sql_agent.prompt.sql_prompts import (
    THINK_FLAT_SQL_GENERATION,
    THINK_FLAT_SQL_RETRY_DIRECTIVE,
    THINK_SUBQUERY_REJECTION,
)
from helicalbi.sql_agent.prompt.synthesis_prompts import THINK_FINAL_ANSWER_PROMPT

__all__ = [
    "AUTO_MODE_PROMPT",
    "THINK_QUESTION_SYSTEM",
    "_THINK_QUESTION_SYSTEM",
    "THINK_FLAT_SQL_PLAN_RULES",
    "THINK_FLAT_SQL_GENERATION",
    "THINK_FLAT_SQL_RETRY_DIRECTIVE",
    "THINK_SUBQUERY_REJECTION",
    "THINK_SUCCESS_EXECUTION_PROMPT",
    "THINK_SUCCESS_PROMPT_FORMATTED",
    "STEP_INSIGHT_PROMPT",
    "THINK_FINAL_ANSWER_PROMPT",
]
