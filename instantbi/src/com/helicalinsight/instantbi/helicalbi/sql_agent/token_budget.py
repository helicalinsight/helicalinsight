"""Compact tool payloads and planner history to cut LLM tokens."""
from __future__ import annotations

import json
from typing import Any, Dict, List, Optional, Sequence

from langchain_core.messages import AIMessage, BaseMessage, HumanMessage, SystemMessage, ToolMessage

from helicalbi.sql_agent.modes import truncate_text

# Top-level tool fields that are safe to shorten for the LLM (state_patch stays full until applied).
_COMPACT_KEYS = {
    "model_context": 600,
    "schema": 500,
    "sql": 400,
    "row_preview": 300,
    "report_model": 200,
    "analysis": 300,
    "error": 400,
}


def compact_tool_payload(payload: dict[str, Any]) -> dict[str, Any]:
    """Shorten bulky top-level tool fields; keep state_patch for apply_patches."""
    if not isinstance(payload, dict):
        return {}
    out: dict[str, Any] = {}
    for key, value in payload.items():
        if key == "state_patch":
            out[key] = value
            continue
        if key in _COMPACT_KEYS:
            if isinstance(value, str):
                out[key] = truncate_text(value, _COMPACT_KEYS[key])
            elif isinstance(value, list):
                out[key] = value[:5]
            elif isinstance(value, dict):
                out[key] = {"_keys": list(value.keys())[:12], "_truncated": True}
            else:
                out[key] = value
            continue
        out[key] = value
    return out


def llm_tool_payload(payload: dict[str, Any]) -> dict[str, Any]:
    """Payload stored in ToolMessage after state_patch has been applied (no bulky patch)."""
    compact = compact_tool_payload(payload)
    compact.pop("state_patch", None)
    compact["state_applied"] = True
    return compact


def _payload(content: Any) -> dict[str, Any]:
    if isinstance(content, dict):
        return content
    text = content if isinstance(content, str) else str(content or "")
    try:
        parsed = json.loads(text)
    except (TypeError, ValueError, json.JSONDecodeError):
        return {}
    return parsed if isinstance(parsed, dict) else {}


def chart_count(state: Dict[str, Any]) -> int:
    return sum(
        1
        for step in (state.get("collected_data") or [])
        if step.get("include_in_dashboard")
    )


def charts_complete(state: Dict[str, Any]) -> bool:
    max_charts = int(state.get("max_sub_questions") or 0)
    if max_charts <= 0:
        return False
    return chart_count(state) >= max_charts


def trim_planner_history(
    messages: Sequence[BaseMessage],
    *,
    keep_tool_rounds: int = 4,
) -> List[BaseMessage]:
    """Keep system + human seed and the last N AI/tool rounds for the next LLM call."""
    messages = list(messages or [])
    if keep_tool_rounds < 0 or len(messages) <= 2:
        return messages

    prefix: List[BaseMessage] = []
    rest: List[BaseMessage] = []
    for message in messages:
        if not rest and isinstance(message, (SystemMessage, HumanMessage)):
            prefix.append(message)
            continue
        rest.append(message)

    if not rest:
        return prefix

    # Split rest into rounds starting at each AIMessage with tool_calls (or any AIMessage).
    rounds: List[List[BaseMessage]] = []
    current: List[BaseMessage] = []
    for message in rest:
        if isinstance(message, AIMessage) and current:
            rounds.append(current)
            current = [message]
        else:
            current.append(message)
    if current:
        rounds.append(current)

    if len(rounds) <= keep_tool_rounds:
        return prefix + rest

    kept = rounds[-keep_tool_rounds:]
    dropped = len(rounds) - keep_tool_rounds
    marker = SystemMessage(
        content=f"[Earlier {dropped} tool round(s) omitted to save tokens. Use findings already in state.]"
    )
    trimmed: List[BaseMessage] = list(prefix)
    trimmed.append(marker)
    for round_messages in kept:
        trimmed.extend(round_messages)
    return trimmed
