"""Shared literals for interactive chat (fast, think routing, auto).

Mode-specific think literals stay in ``interactive.think.constants``.
Fast-mode literals live in ``interactive.fast.constants``.
"""
from __future__ import annotations

from typing import Final

# ---------------------------------------------------------------------------
# Request / audit
# ---------------------------------------------------------------------------
AUDIT_ENDPOINT: Final[str] = "/interactive"
REQUEST_STATUS_SUCCESS: Final[str] = "SUCCESS"
REQUEST_STATUS_ABORTED: Final[str] = "ABORTED"
REQUEST_STATUS_ERROR: Final[str] = "ERROR"
MSG_REQUEST_CANCELLED: Final[str] = "Request has been cancelled."

# ---------------------------------------------------------------------------
# Auto-mode SSE progress
# ---------------------------------------------------------------------------
STAGE_AUTO: Final[str] = "auto"
STATUS_STARTED: Final[str] = "started"
STATUS_DONE: Final[str] = "done"
MSG_AUTO_STARTED: Final[str] = "Checking how complex this question is…"
MSG_AUTO_CHOSEN: Final[str] = "This looks like a {chosen} question. Using {chosen} mode."
AUTO_LABEL_THINK: Final[str] = "Think"
AUTO_LABEL_FAST: Final[str] = "Fast"

# Re-export auto heuristics from think.constants for a single interactive import path.
from helicalbi.interactive.think.constants import (  # noqa: E402
    AUTO_THINK_AGGREGATE_PATTERN,
    AUTO_THINK_MARKERS,
    AUTO_THINK_MIN_QUESTION_MARKS,
    AUTO_THINK_MIN_TEXT_LENGTH,
)

__all__ = [
    "AUDIT_ENDPOINT",
    "AUTO_LABEL_FAST",
    "AUTO_LABEL_THINK",
    "AUTO_THINK_AGGREGATE_PATTERN",
    "AUTO_THINK_MARKERS",
    "AUTO_THINK_MIN_QUESTION_MARKS",
    "AUTO_THINK_MIN_TEXT_LENGTH",
    "MSG_AUTO_CHOSEN",
    "MSG_AUTO_STARTED",
    "MSG_REQUEST_CANCELLED",
    "REQUEST_STATUS_ABORTED",
    "REQUEST_STATUS_ERROR",
    "REQUEST_STATUS_SUCCESS",
    "STAGE_AUTO",
    "STATUS_DONE",
    "STATUS_STARTED",
]
