"""Interactive chat mode: fast | think | auto (LLM picks fast or think)."""
from __future__ import annotations

from typing import Any, Mapping, Optional

INTERACTIVE_MODE_FAST = "fast"
INTERACTIVE_MODE_THINK = "think"
INTERACTIVE_MODE_AUTO = "auto"
DEFAULT_INTERACTIVE_MODE = INTERACTIVE_MODE_FAST
VALID_INTERACTIVE_MODES = (
    INTERACTIVE_MODE_FAST,
    INTERACTIVE_MODE_THINK,
    INTERACTIVE_MODE_AUTO,
)
ROUTED_INTERACTIVE_MODES = (INTERACTIVE_MODE_FAST, INTERACTIVE_MODE_THINK)

_ALIASES = {
    "quick": INTERACTIVE_MODE_FAST,
    "normal": INTERACTIVE_MODE_FAST,
    "default": INTERACTIVE_MODE_FAST,
    "deep": INTERACTIVE_MODE_THINK,
    "agent": INTERACTIVE_MODE_THINK,
    "agentic": INTERACTIVE_MODE_THINK,
    "plan": INTERACTIVE_MODE_THINK,
    "research": INTERACTIVE_MODE_THINK,
    "automatic": INTERACTIVE_MODE_AUTO,
    "smart": INTERACTIVE_MODE_AUTO,
}


def normalize_interactive_mode(
    mode: Optional[str],
    default: str = DEFAULT_INTERACTIVE_MODE,
) -> str:
    fallback = default if default in VALID_INTERACTIVE_MODES else DEFAULT_INTERACTIVE_MODE
    raw = str(mode or fallback or DEFAULT_INTERACTIVE_MODE).strip().lower()
    raw = _ALIASES.get(raw, raw)
    if raw not in VALID_INTERACTIVE_MODES:
        return fallback
    return raw


def is_think_mode(mode: Optional[str]) -> bool:
    return normalize_interactive_mode(mode) == INTERACTIVE_MODE_THINK


def is_auto_mode(mode: Optional[str]) -> bool:
    return normalize_interactive_mode(mode) == INTERACTIVE_MODE_AUTO


def resolve_interactive_mode_from_input(
    user_input: Mapping[str, Any] | None,
    data: Mapping[str, Any] | None = None,
    default: Optional[str] = None,
) -> str:
    """Read mode from request input (input.mode or top-level mode)."""
    payload = user_input or {}
    root = data or {}
    raw = payload.get("mode") or payload.get("interactive_mode") or root.get("mode")
    return normalize_interactive_mode(raw, default=default or DEFAULT_INTERACTIVE_MODE)


def apply_requested_mode(
    payload: Mapping[str, Any] | None,
    user_input: Mapping[str, Any] | None,
) -> dict[str, Any]:
    """Keep requested auto mode on the response while routed mode stays fast/think."""
    body = dict(payload or {})
    requested = str((user_input or {}).get("requested_mode") or "").strip().lower()
    if is_auto_mode(requested):
        body["requested_mode"] = INTERACTIVE_MODE_AUTO
    return body
