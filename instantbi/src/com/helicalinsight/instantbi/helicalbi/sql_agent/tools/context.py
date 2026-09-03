"""Shared state helpers for dashboard agent tools."""
from __future__ import annotations

import json
import logging
from typing import Any

from helicalbi.common.LlmInvokeHelper import read_token_usage
from helicalbi.model.TokenUsage import TokenUsage
from helicalbi.sql_agent.config import DEFAULT_RESULT_ROW_CAP
from helicalbi.sql_agent.database.schema_indexer import get_indexer
from helicalbi.sql_agent.token_budget import compact_tool_payload

logger = logging.getLogger(__name__)


class AgentToolContext:
    """Read/write view over LangGraph agent state for InstantBI tools."""

    def __init__(self, state: dict[str, Any]):
        self.state = state

    @staticmethod
    def dump(payload: dict[str, Any]) -> str:
        """JSON for ToolMessage: compact top-level fields, full state_patch for apply_patches."""
        return json.dumps(compact_tool_payload(payload), default=str)

    @property
    def session(self) -> dict[str, Any]:
        session = self.state.get("session_context")
        if not isinstance(session, dict):
            session = {}
            self.state["session_context"] = session
        return session

    @property
    def catalog(self):
        return get_indexer(self.state.get("catalog_id") or "default").catalog

    @property
    def metadata(self) -> dict:
        """Physical metadata API payload for validator fallback (not merged into cube)."""
        session = self.session
        raw = session.get("metadata") or session.get("actual_metadata")
        return raw if isinstance(raw, dict) else {}

    @property
    def catalog_id(self) -> str:
        return str(self.state.get("catalog_id") or "default")

    @property
    def dialect(self) -> str | None:
        return self.state.get("dialect")

    @property
    def request_id(self) -> Any:
        return self.state.get("request_id")

    @property
    def thread_id(self) -> str:
        return str(self.state.get("thread_id") or "")

    def next_seq(self) -> str:
        step = len(self.state.get("collected_data") or []) + 1
        return f"{self.state.get('chat_seq_id') or '1'}-{step}"

    def unique_seq(self) -> str:
        """Prefer the seq from generate_sql, but never reuse one already collected."""
        seq = str(self.state.get("current_chat_seq_id") or "").strip()
        used = {
            str(step.get("chat_seq_id") or "").strip()
            for step in (self.state.get("collected_data") or [])
        }
        if not seq or seq in used:
            return self.next_seq()
        return seq

    def chart_count(self) -> int:
        return sum(
            1
            for step in (self.state.get("collected_data") or [])
            if step.get("include_in_dashboard")
        )

    def preview_data(self, result: dict[str, Any]) -> Any:
        data = result.get("data")
        if isinstance(data, list):
            return data[:DEFAULT_RESULT_ROW_CAP]
        sql_result = result.get("sql_result")
        if isinstance(sql_result, dict) and isinstance(sql_result.get("data"), list):
            return sql_result["data"][:DEFAULT_RESULT_ROW_CAP]
        return data

    def merge_usage(self, source: dict[str, Any]) -> dict[str, Any]:
        accumulated = read_token_usage(self.state)
        raw = source.get("token_usage") or {}
        if isinstance(raw, dict) and raw:
            try:
                accumulated = accumulated + TokenUsage.model_validate(raw)
            except Exception:
                logger.debug("Unable to merge tool token usage", exc_info=True)
        if hasattr(accumulated, "model_dump"):
            return accumulated.model_dump(exclude_none=True)
        return {}

    @staticmethod
    def sql_error(result: dict[str, Any]) -> str:
        error = str(result.get("sql_error") or result.get("error") or "").strip()
        if not error or error == "Not Generated":
            return ""
        return error
