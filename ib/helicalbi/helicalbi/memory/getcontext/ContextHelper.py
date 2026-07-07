import json
from typing import Any, Optional

from helicalbi.memory.getcontext.ContextClass import ChatItem, GraphState


def format_chat_item_field(item: Optional[ChatItem], field: str) -> str:
    """Return a prompt-safe string for an optional ChatItem field."""
    if item is None:
        return ""
    value: Any = getattr(item, field, None)
    if value is None:
        return ""
    if isinstance(value, dict):
        return json.dumps(value)
    return str(value)


def resolve_context_node(state: GraphState):
    if state.query_type == "NEW_QUERY":
        return {"resolved_context": None}

    history = state.chat_history or []
    last_item = history[-1] if history else None

    return {"resolved_context": last_item}
