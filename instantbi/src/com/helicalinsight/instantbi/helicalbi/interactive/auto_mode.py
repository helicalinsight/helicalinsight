"""Classify a question as fast or think when interactive mode is auto."""
from __future__ import annotations

import logging
import re
from typing import Any, Mapping, Optional

from pydantic import BaseModel, Field

from helicalbi.interactive.modes import (
    INTERACTIVE_MODE_AUTO,
    INTERACTIVE_MODE_FAST,
    INTERACTIVE_MODE_THINK,
    is_auto_mode,
    resolve_interactive_mode_from_input,
)

logger = logging.getLogger(__name__)

_THINK_MARKERS = (
    "why ",
    "why?",
    "how come",
    "explain",
    "analy",
    "compare",
    "versus",
    " vs ",
    "breakdown",
    "break down",
    "root cause",
    "driver",
    "across ",
    "investigate",
    "recommend",
    "strategy",
    "dashboard",
    "what caused",
    "and why",
    "deep dive",
    "overview of",
    "briefing",
)

AUTO_MODE_PROMPT = """You route Instant BI questions to Fast or Think.

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


class InteractiveModeChoice(BaseModel):
    mode: str = Field(description="fast or think")
    reason: str = Field(default="", description="Short reason for the choice.")
    complexity: str = Field(
        default="simple",
        description="simple, moderate, or complex.",
    )


def heuristic_interactive_mode(question: str) -> str:
    """Cheap fallback when the classifier LLM is unavailable."""
    text = f" {str(question or '').strip().lower()} "
    if not text.strip():
        return INTERACTIVE_MODE_FAST
    if text.count("?") >= 2 or len(text) > 180:
        return INTERACTIVE_MODE_THINK
    if any(marker in text for marker in _THINK_MARKERS):
        return INTERACTIVE_MODE_THINK
    if re.search(r"\b(each|every|all)\b.+\b(by|across|versus|vs)\b", text):
        return INTERACTIVE_MODE_THINK
    return INTERACTIVE_MODE_FAST


def classify_interactive_mode(
    question: str,
    *,
    invoke=None,
) -> str:
    """Ask the LLM whether this question needs Fast or Think. Falls back to heuristics."""
    fallback = heuristic_interactive_mode(question)
    try:
        from helicalbi.sql_agent.llm import invoke_agent_model

        runner = invoke or invoke_agent_model
        parsed = runner(
            AUTO_MODE_PROMPT,
            {"question": str(question or "").strip()},
            InteractiveModeChoice,
        )
        chosen = str(getattr(parsed, "mode", "") or "").strip().lower()
        if chosen in {INTERACTIVE_MODE_FAST, INTERACTIVE_MODE_THINK}:
            logger.info(
                "Auto mode classified question as %s complexity=%s reason=%s",
                chosen,
                getattr(parsed, "complexity", ""),
                getattr(parsed, "reason", ""),
            )
            return chosen
    except Exception:
        logger.exception("Auto mode classifier failed; using heuristic=%s", fallback)
    return fallback


def resolve_routed_interactive_mode(
    user_input: Mapping[str, Any] | None,
    data: Mapping[str, Any] | None = None,
    *,
    default: Optional[str] = None,
    invoke=None,
) -> tuple[str, str]:
    """Return (requested_mode, routed_mode). Auto is classified to fast or think."""
    requested = resolve_interactive_mode_from_input(user_input, data, default=default)
    if not is_auto_mode(requested):
        return requested, requested
    question = str((user_input or {}).get("inputString") or "")
    return requested, classify_interactive_mode(question, invoke=invoke)


def bind_routed_interactive_mode(
    data: Mapping[str, Any] | None,
    *,
    default: Optional[str] = None,
    invoke=None,
) -> tuple[str, str]:
    """Rewrite input.mode to the routed fast/think choice when the user asked for auto."""
    payload = dict(data or {})
    user_input = dict(payload.get("input") or {})
    requested, routed = resolve_routed_interactive_mode(
        user_input, payload, default=default, invoke=invoke
    )
    if is_auto_mode(requested):
        user_input["mode"] = routed
        user_input["requested_mode"] = INTERACTIVE_MODE_AUTO
        payload["input"] = user_input
        if isinstance(data, dict):
            data["input"] = user_input
    return requested, routed
