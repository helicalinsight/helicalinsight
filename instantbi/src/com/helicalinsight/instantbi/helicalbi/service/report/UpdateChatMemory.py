"""Hydrate in-process chat memory from a saved Instant report payload."""

from __future__ import annotations

import logging
from typing import Any

from helicalbi.common.ChatGraphMemory import chat_graph_memory
from helicalbi.common.ChatManager import (
    add_insight,
    add_message,
    add_sql,
    add_viz_response,
    reset_thread,
)
from helicalbi.sql.SqlSanitizer import strip_sql_markdown

logger = logging.getLogger(__name__)

_CHAT_RESPONSE_KEYS = (
    "viz",
    "sql",
    "summary",
    "data",
    "metadata",
    "report_model",
    "token_usage",
    "error",
    "hreportId",
)


def unwrap_report_data(payload: Any) -> dict:
    """Normalize report envelopes down to ``{reportName, metadata, state}``."""
    if not isinstance(payload, dict):
        return {}
    if isinstance(payload.get("state"), dict):
        return payload
    response = payload.get("response")
    if isinstance(response, dict):
        data = response.get("data")
        if isinstance(data, dict):
            return data
        if isinstance(response.get("state"), dict):
            return response
    data = payload.get("data")
    if isinstance(data, dict):
        return data
    return {}


def _as_list(value: Any) -> list:
    if isinstance(value, list):
        return value
    return []


def _seq_key(value: Any) -> int:
    try:
        return int(value)
    except (TypeError, ValueError):
        return 0


def _format_strings_from_entry(entry: dict) -> dict[str, str]:
    report_model = entry.get("report_model") if isinstance(entry.get("report_model"), dict) else {}
    viz_model = report_model.get("viz_model") if isinstance(report_model.get("viz_model"), dict) else {}
    properties = viz_model.get("properties") if isinstance(viz_model.get("properties"), dict) else {}
    formatting = properties.get("formatting")
    if not isinstance(formatting, dict):
        return {}
    return {
        str(name).strip(): str(fmt).strip()
        for name, fmt in formatting.items()
        if str(name or "").strip() and str(fmt or "").strip()
    }


def _chat_response_from_saved(entry: dict) -> dict:
    payload = {key: entry[key] for key in _CHAT_RESPONSE_KEYS if key in entry}
    formatting = _format_strings_from_entry(entry)
    if formatting and not payload.get("format_strings"):
        payload["format_strings"] = formatting
    return payload


def _build_memory_payload(
    *,
    chat_response: dict,
    sql: str,
    dialect: str,
    user_query: str,
    user_name: str,
    domain: Any,
    topics: Any,
    format_strings: dict[str, str],
) -> dict[str, Any]:
    viz = chat_response.get("viz") if isinstance(chat_response.get("viz"), dict) else {}
    metadata = chat_response.get("metadata") or []
    if not isinstance(metadata, list):
        metadata = []
    return {
        "chat_response": chat_response,
        "sql": sql,
        "dialect": dialect,
        "user_query": user_query,
        "user_name": user_name,
        "domain": domain or [],
        "topics": topics or [],
        "metadata": metadata,
        "vf_title": str(viz.get("vf_title") or ""),
        "format_strings": format_strings or {},
    }


def hydrate_chat_memory_from_report(
    report_payload: Any,
    *,
    username: str = "",
    thread_id: str = "",
) -> dict[str, Any]:
    """Replace ChatGraphMemory + ChatManager history from a saved Instant report."""
    data = unwrap_report_data(report_payload)
    state = data.get("state") if isinstance(data.get("state"), dict) else {}
    chat_id = str(
        thread_id
        or state.get("activeChatId")
        or state.get("activeChatID")
        or ""
    ).strip()
    if not chat_id:
        logger.warning("updateMemory skipped: no activeChatId")
        return {"chatid": "", "turns": 0}

    inputs = _as_list(state.get("inputs"))
    query_by_seq = {
        _seq_key(item.get("chat_sequence_id")): str(item.get("input") or "")
        for item in inputs
        if isinstance(item, dict)
    }
    responses = [
        item
        for item in _as_list(state.get("chat_responses"))
        if isinstance(item, dict)
    ]
    responses.sort(key=lambda item: _seq_key(item.get("chat_sequence_id")))

    reset_thread(chat_id)
    chat_graph_memory.clear(chat_id)

    for entry in responses:
        seq_id = entry.get("chat_sequence_id")
        if seq_id is None:
            continue
        chat_response = _chat_response_from_saved(entry)
        sql_section = chat_response.get("sql") if isinstance(chat_response.get("sql"), dict) else {}
        dialect = str(sql_section.get("dialect") or "")
        raw_sql = strip_sql_markdown(str(sql_section.get("raw_sql") or ""))
        user_query = query_by_seq.get(_seq_key(seq_id), "")
        domain = sql_section.get("required_domain") or []
        topics = sql_section.get("required_topic") or []
        viz_model = ((chat_response.get("report_model") or {}).get("viz_model"))
        insight = ((chat_response.get("summary") or {}).get("insight") or "")

        if user_query:
            add_message(chat_id, user_query)
        if raw_sql:
            add_sql(
                chat_id,
                {
                    "sql": raw_sql,
                    "reason": sql_section.get("reason") or "",
                },
            )
        if viz_model:
            add_viz_response(chat_id, viz_model)
        elif chat_response.get("viz"):
            add_viz_response(chat_id, chat_response.get("viz"))
        if insight:
            add_insight(chat_id, insight)

        chat_graph_memory.add_node(
            chat_id,
            seq_id,
            _build_memory_payload(
                chat_response=chat_response,
                sql=raw_sql,
                dialect=dialect,
                user_query=user_query,
                user_name=username,
                domain=domain,
                topics=topics,
                format_strings=_format_strings_from_entry(entry),
            ),
        )

    logger.info(
        "Hydrated InstantBI chat memory chatid=%s turns=%s",
        chat_id,
        len(responses),
    )
    return {"chatid": chat_id, "turns": len(responses)}
