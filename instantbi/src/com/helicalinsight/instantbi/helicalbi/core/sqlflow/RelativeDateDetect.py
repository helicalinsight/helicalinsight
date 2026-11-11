"""After final SQL, ask the model which predicates are relative dates."""

from __future__ import annotations

import logging
from typing import Optional

from helicalbi.model.output.RelativeDate import RelativeDates
from helicalbi.prompt.FormatInstruction import format_instruction_string
from helicalbi.prompt.RelativeDatePrompt import render_relative_date_prompt

logger = logging.getLogger(__name__)

_RELATIVE_MARKERS = (
    "CURRENT_DATE",
    "CURRENT_TIMESTAMP",
    "NOW()",
    "GETDATE(",
    "SYSDATE",
)


def sql_has_relative_date(sql: str) -> bool:
    """True when the SQL mentions a clock function the relative-date prompt should read."""
    text = str(sql or "").upper()
    return any(marker in text for marker in _RELATIVE_MARKERS)


def detect_relative_dates(
    sql: str,
    catalog: dict | None = None,
    *,
    state: Optional[dict] = None,
) -> RelativeDates:
    """Return relative-date tokens for ``sql``. SQL with no clock function is empty."""
    if not sql_has_relative_date(sql):
        return RelativeDates()
    try:
        from helicalbi.sql_agent.llm import invoke_agent_model

        parsed = invoke_agent_model(
            render_relative_date_prompt(catalog) + format_instruction_string,
            {"sql": sql},
            RelativeDates,
            state=state,
        )
    except Exception:
        logger.exception("Relative date detection failed")
        return RelativeDates()
    if isinstance(parsed, RelativeDates):
        return parsed
    return RelativeDates()
