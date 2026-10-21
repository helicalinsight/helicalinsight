"""Centralized constants for interactive think mode.

Keep literals, stage names, match weights, and wire keys here so behavior
can change without hunting through orchestration code.
"""
from __future__ import annotations

from typing import Callable, Final

# ---------------------------------------------------------------------------
# Pipeline / agent flags
# ---------------------------------------------------------------------------
FLAT_SQL: Final[bool] = True
INCLUDE_IN_DASHBOARD_DEFAULT: Final[bool] = False
BUILD_DASHBOARD: Final[bool] = True
PLAN_STATUS_EXECUTED: Final[str] = "executed"
DEFAULT_AGENT_MODE: Final[str] = "balanced"
RESPONSE_PHASE_EXECUTE: Final[str] = "execute"
RESPONSE_PHASE_PLAN: Final[str] = "plan"

# ---------------------------------------------------------------------------
# Progress stages (SSE / ActivityReporter)
# ---------------------------------------------------------------------------
STAGE_PLAN: Final[str] = "think_plan"
STAGE_INTRO: Final[str] = "think_intro"
STAGE_QUESTION: Final[str] = "think_question"
STAGE_EXECUTE: Final[str] = "think_execute"
STAGE_ANSWER: Final[str] = "think_answer"

STATUS_STARTED: Final[str] = "started"
STATUS_DONE: Final[str] = "done"

# ---------------------------------------------------------------------------
# User-facing progress / result messages
# ---------------------------------------------------------------------------
MSG_PLAN_STARTED: Final[str] = "Planning investigation questions…"
MSG_INTRO_STARTED: Final[str] = "Writing a short overview…"
MSG_INTRO_DONE: Final[str] = "Overview is ready."
MSG_EXECUTE_STARTED: Final[str] = "Starting supporting questions…"
MSG_ANSWER_DONE: Final[str] = "Drafted conclusive final answer."
MSG_INVESTIGATION_COMPLETE: Final[str] = (
    "Investigation complete. Findings are shown as text explanations."
)
MSG_REQUEST_CANCELLED: Final[str] = "Request has been cancelled."

# ---------------------------------------------------------------------------
# Validation errors
# ---------------------------------------------------------------------------
ERR_INPUT_STRING_REQUIRED: Final[str] = "input.inputString is required for think mode"
ERR_MODEL_REQUIRED: Final[str] = "input.model.file and input.model.dir are required"
ERR_CHAT_ID_REQUIRED: Final[str] = "input.chatid is required for think mode"

# ---------------------------------------------------------------------------
# Synthesis / citation policy
# ---------------------------------------------------------------------------
FALLBACK_CITATION_LIMIT: Final[int] = 3
FALLBACK_FINDINGS_LIMIT: Final[int] = 3
CITATION_MARKER_PATTERN: Final[str] = r"\[(\d+)\]"
NONE_PLACEHOLDER: Final[str] = "(none)"
DEFAULT_FILTER_LABEL: Final[str] = "Filter"

# ---------------------------------------------------------------------------
# Activity-details truthy override values
# ---------------------------------------------------------------------------
TRUTHY_STRINGS: Final[frozenset[str]] = frozenset({"1", "true", "yes", "on"})

# ---------------------------------------------------------------------------
# Dashboard item classification
# ---------------------------------------------------------------------------
FILTER_KIND_ALIASES: Final[frozenset[str]] = frozenset({"instant.filter", "filter"})
REPORT_KIND_ALIASES: Final[frozenset[str]] = frozenset(
    {"instant", "viz", "chart", "report"}
)
NON_REPORT_KINDS: Final[frozenset[str]] = frozenset(
    {"summary", "text", "svg", "image", "separator", "apply_reset", "applyreset"}
)
ITEM_ID_KEYS: Final[tuple[str, ...]] = (
    "component_id",
    "itemId",
    "id",
    "chat_sequence_id",
)
DASHBOARD_CONTAINER_KEYS: Final[tuple[str, ...]] = (
    "items",
    "dashboard",
    "dashboard_model",
)
FILTER_LABEL_KEYS: Final[tuple[str, ...]] = ("label", "alias")
COLUMN_LABEL_KEYS: Final[tuple[str, ...]] = ("alias", "label")
FILTER_TITLE_SYNC_KEYS: Final[tuple[str, ...]] = ("alias", "label", "title")

# ---------------------------------------------------------------------------
# Filter ↔ report match scoring (Strategy weights)
# ---------------------------------------------------------------------------
SCORE_COLUMN_MATCH: Final[int] = 10
SCORE_TABLE_BONUS: Final[int] = 5
SCORE_LABEL_MATCH: Final[int] = 8
SCORE_TITLE_MATCH: Final[int] = 6

# ---------------------------------------------------------------------------
# Chat / history shaping keys stripped for interactive wire
# ---------------------------------------------------------------------------
INTERACTIVE_STRIP_KEYS: Final[tuple[str, ...]] = ("data", "metadata")

# ---------------------------------------------------------------------------
# Request status labels
# ---------------------------------------------------------------------------
REQUEST_STATUS_SUCCESS: Final[str] = "SUCCESS"
REQUEST_STATUS_ABORTED: Final[str] = "ABORTED"
REQUEST_STATUS_ERROR: Final[str] = "ERROR"

# ---------------------------------------------------------------------------
# Stream worker sentinel kinds
# ---------------------------------------------------------------------------
STREAM_DONE: Final[str] = "__done__"
STREAM_ABORTED: Final[str] = "__aborted__"
STREAM_ERROR: Final[str] = "__error__"

# ---------------------------------------------------------------------------
# Audit endpoint
# ---------------------------------------------------------------------------
AUDIT_ENDPOINT: Final[str] = "/interactive"

# ---------------------------------------------------------------------------
# Auto → Think heuristic routing
# ---------------------------------------------------------------------------
AUTO_THINK_MIN_QUESTION_MARKS: Final[int] = 2
AUTO_THINK_MIN_TEXT_LENGTH: Final[int] = 180
AUTO_THINK_MARKERS: Final[tuple[str, ...]] = (
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
AUTO_THINK_AGGREGATE_PATTERN: Final[str] = (
    r"\b(each|every|all)\b.+\b(by|across|versus|vs)\b"
)

ProgressCallback = Callable[[str, str, str], None]
