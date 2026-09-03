"""Persist investigation plans against dashboardid + dashboard_sequence_id."""
from __future__ import annotations

from typing import Any, Optional

from helicalbi.common.ChatGraphMemory import chat_graph_memory

PLAN_KIND = "dashboard_investigation_plan"


def save_plan(dashboard_id: Any, sequence_id: Any, payload: dict[str, Any]) -> None:
    record = dict(payload or {})
    record["kind"] = PLAN_KIND
    chat_graph_memory.add_node(dashboard_id, sequence_id, record)


def load_plan(dashboard_id: Any, sequence_id: Any) -> Optional[dict[str, Any]]:
    node = chat_graph_memory.get_node(dashboard_id, sequence_id)
    if not isinstance(node, dict) or node.get("kind") != PLAN_KIND:
        return None
    return node


def has_plan(dashboard_id: Any, sequence_id: Any) -> bool:
    return load_plan(dashboard_id, sequence_id) is not None
