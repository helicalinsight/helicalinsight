from __future__ import annotations

import json
import logging
from typing import Any, Dict, List

from langchain_core.messages import AIMessage, ToolMessage

from helicalbi.sql_agent.state import AgentState
from helicalbi.sql_agent.token_budget import charts_complete, llm_tool_payload

logger = logging.getLogger(__name__)


def _payload(content: Any) -> dict[str, Any]:
    if isinstance(content, dict):
        return content
    text = content if isinstance(content, str) else str(content or "")
    try:
        parsed = json.loads(text)
    except (TypeError, ValueError, json.JSONDecodeError):
        return {}
    return parsed if isinstance(parsed, dict) else {}


def apply_tool_patches(state: AgentState) -> Dict[str, Any]:
    """Fold state_patch from latest tool messages, then shrink those messages for later planner turns."""
    messages = list(state.get("messages") or [])
    merged: Dict[str, Any] = {}
    rewritten: List[Any] = []

    # Walk back through the latest AI → tool batch.
    batch_tool_indexes: List[int] = []
    for index in range(len(messages) - 1, -1, -1):
        message = messages[index]
        if isinstance(message, ToolMessage):
            batch_tool_indexes.append(index)
            continue
        if isinstance(message, AIMessage):
            break
        break

    for index in reversed(batch_tool_indexes):
        message = messages[index]
        payload = _payload(message.content)
        patch = payload.get("state_patch")
        if isinstance(patch, dict):
            merged = {**merged, **patch}
        compact = llm_tool_payload(payload)
        kwargs = {
            "content": json.dumps(compact, default=str),
            "tool_call_id": message.tool_call_id,
        }
        message_id = getattr(message, "id", None)
        if message_id is not None:
            kwargs["id"] = message_id
        name = getattr(message, "name", None)
        if name:
            kwargs["name"] = name
        rewritten.append(ToolMessage(**kwargs))

    if merged:
        logger.debug("Applied tool state patch keys=%s", list(merged.keys()))

    work = {**state, **merged}
    if charts_complete(work):
        merged["is_complete"] = True
        logger.info("Chart cap reached; marking is_complete without another planner turn")

    if rewritten:
        # Same message ids → add_messages replaces content (drops bulky state_patch from history).
        merged["messages"] = rewritten
    return merged
