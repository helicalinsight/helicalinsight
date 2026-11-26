"""Build the client-facing fast-mode response and persist chat memory."""
from __future__ import annotations

import logging
import time
from typing import Any, Optional

from helicalbi.common.ChatGraphMemory import chat_graph_memory
from helicalbi.common.ChatManager import get_last_insight
from helicalbi.common.LlmInvokeHelper import set_total_time_consumed
from helicalbi.controller.data_insight import _generate_data_insight_from_rows
from helicalbi.controller.helpers import build_chat_memory_payload
from helicalbi.interactive.fast.form_data import VizFormDataResolver
from helicalbi.interactive.fast.session import FastModelSession
from helicalbi.interactive.modes import INTERACTIVE_MODE_FAST
from helicalbi.model.output.ChatResponse import ChatResponse
from helicalbi.sql.SqlSanitizer import as_sql_markdown, format_sql

logger = logging.getLogger(__name__)

# SqlExecutor leaves this placeholder on a successful query. It is not a failure.
_PLACEHOLDER_FLOW_ERROR = "Not Generated"


def _flow_failed(result: dict[str, Any]) -> bool:
    """True when SQL or the turn recorded a real error."""
    for key in ("sql_error", "error"):
        value = result.get(key)
        if value is None:
            continue
        text = str(value).strip()
        if text and text != _PLACEHOLDER_FLOW_ERROR:
            return True
    return False


def _club_fast_data_insight(
    result: dict[str, Any],
    *,
    data: dict[str, Any],
    user_query: str,
    username: str,
    thread_id: Any,
    raw_sql: str,
) -> dict[str, Any] | None:
    """Same explanation Explain chart would return, sent with the fast answer."""
    if _flow_failed(result):
        return None
    rows = result.get("data") or []
    if not isinstance(rows, list):
        rows = []
    profile_rows = []
    if isinstance(data, dict):
        profile_rows = data.get("userProfile") or (data.get("input") or {}).get("userProfile") or []
    try:
        insight, token_usage = _generate_data_insight_from_rows(
            username=username,
            user_question=user_query,
            sql=raw_sql or "",
            sample_data=rows,
            thread_id=str(thread_id or ""),
            profile={"userProfile": profile_rows},
            memory={
                "domain": result.get("domain") or [],
                "topics": result.get("topics") or [],
            },
            last_chats=get_last_insight(thread_id) if thread_id else [],
        )
    except Exception:
        logger.exception("Fast data insight was not added to the chat response")
        return None
    text = str(insight or "").strip()
    if not text:
        return None
    return {"insight": text, "token_usage": token_usage or {}}


class FastResultPayloadBuilder:
    """Shape result → interactive wire dict and store chat-graph memory."""

    def __init__(self, *, form_data: Optional[VizFormDataResolver] = None) -> None:
        self._form_data = form_data or VizFormDataResolver()

    def build(
        self,
        result: dict[str, Any],
        *,
        session: FastModelSession,
        data: dict[str, Any],
        user_query: str,
        username: str,
        session_cookie: str,
        thread_id: Any,
        chat_seq_id: Any,
        request_started: float,
        rm_cols_in_filter: bool = True,
        parameter_functions: dict | None = None,
    ) -> tuple[dict[str, Any], dict[str, Any]]:
        if (
            "sql_result" in result
            and isinstance(result["sql_result"], dict)
            and "data" in result["sql_result"]
        ):
            result["data"] = result["sql_result"]["data"]

        sql = result.get("sql", "")
        raw_sql = sql
        formatted_sql = format_sql(sql, dialect=result.get("dialect"), pretty=True)

        result["user_input"] = data
        if sql:
            result["sql"] = as_sql_markdown(formatted_sql)

        result = self._form_data.ensure(
            result,
            session=session,
            session_cookie=session_cookie,
            formatted_sql=formatted_sql,
            raw_sql=raw_sql,
            rm_cols_in_filter=rm_cols_in_filter,
            parameter_functions=parameter_functions or result.get("parameter_functions"),
            relative_dates=result.get("relative_dates") or None,
            thread_id=thread_id,
            chat_seq_id=chat_seq_id,
        )

        set_total_time_consumed(result, time.perf_counter() - request_started)
        chat_response = ChatResponse.from_model_state(result)
        chat_response_dict = chat_response.to_dict()
        # Explain chart reads this same block. Fast sends it with the answer.
        data_insight = _club_fast_data_insight(
            result,
            data=data,
            user_query=user_query,
            username=username,
            thread_id=thread_id,
            raw_sql=raw_sql,
        )
        if data_insight:
            chat_response_dict["data_insight"] = data_insight
        wire = chat_response.to_interactive_client_dict()
        if data_insight:
            wire["data_insight"] = data_insight
        to_send: dict[str, Any] = {
            "mode": INTERACTIVE_MODE_FAST,
            "chat_response": wire,
        }
        if chat_response_dict.get("error"):
            to_send["error"] = chat_response_dict["error"]

        chat_graph_memory.add_node(
            thread_id,
            chat_seq_id,
            build_chat_memory_payload(
                chat_response=chat_response_dict,
                sql=raw_sql,
                dialect=session.metadata_fun_ref.get("reference", ""),
                user_query=user_query,
                user_name=username,
                domain=result.get("domain") or [],
                topics=result.get("topics") or [],
                state=result if isinstance(result, dict) else None,
            ),
        )
        logger.info(
            "Interactive request completed user=%s thread=%s chat_seq_id=%s has_sql=%s",
            username,
            thread_id,
            chat_seq_id,
            bool(raw_sql),
        )
        return to_send, result
