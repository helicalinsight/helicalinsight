import json
import logging
import traceback
from typing import Any, List, Optional, Tuple

from flask import request

from helicalbi.common.ErrorMessages import extract_message_from_stack_trace
from helicalbi.common import app_config

from helicalbi.common.ChatGraphMemory import chat_graph_memory
from helicalbi.common.RequestCancellation import request_cancellation
from helicalbi.sql.SqlSanitizer import extract_sql, strip_sql_markdown

logger = logging.getLogger(__name__)

_BORDER = "--------------"


class RequestAborted(Exception):
    """Raised when a client aborts an in-flight request."""


def clean_sql(raw_sql: str, dialect: str = "") -> str:
    """Strip markdown fences and normalize SQL text."""
    if not raw_sql:
        return ""
    raw_sql = strip_sql_markdown(raw_sql)
    if not raw_sql:
        return ""
    return strip_sql_markdown(extract_sql(raw_sql, dialect) or "")


def resolve_sql_from_request(
    user_input: dict,
    thread_id: str,
    chat_seq_id: Any,
    *,
    context: str = "request",
) -> str:
    """Resolve SQL from memory, request input, or saved chat item (data-insight parity)."""
    if thread_id and chat_seq_id is not None:
        if chat_graph_memory.has_node(thread_id, chat_seq_id):
            memory_node = chat_graph_memory.get_node(thread_id, chat_seq_id)
            if memory_node:
                sql = clean_sql(
                    memory_node.get("sql", ""),
                    memory_node.get("dialect", ""),
                )
                if sql:
                    logger.info(
                        "Resolved SQL for %s from memory chatid=%s chat_seq_id=%s",
                        context,
                        thread_id,
                        chat_seq_id,
                    )
                    return sql
                logger.debug(
                    "Memory node found but SQL empty for %s chatid=%s chat_seq_id=%s",
                    context,
                    thread_id,
                    chat_seq_id,
                )
        else:
            logger.debug(
                "No memory node for %s chatid=%s chat_seq_id=%s",
                context,
                thread_id,
                chat_seq_id,
            )

    sql = user_input.get("sql", "")
    if sql:
        resolved = clean_sql(sql)
        logger.info("Resolved SQL for %s from request input", context)
        return resolved

    chat_response_item = user_input.get("chat_response_item") or {}
    if chat_response_item:
        sql_section = chat_response_item.get("sql") or {}
        dialect = sql_section.get("dialect", "")
        resolved = clean_sql(sql_section.get("raw_sql", ""), dialect)
        if resolved:
            logger.info("Resolved SQL for %s from chat_response_item", context)
            return resolved

    logger.warning(
        "No SQL resolved for %s chatid=%s chat_seq_id=%s",
        context,
        thread_id,
        chat_seq_id,
    )
    return ""


def resolve_request_id(data: Optional[dict] = None, user_input: Optional[dict] = None) -> Optional[str]:
    """Resolve the cancellation key from query params or JSON payload."""
    request_id = request.args.get("requestId")
    if request_id:
        logger.debug("Resolved requestId=%s from query params", request_id)
        return str(request_id)

    for source_name, source in (("data", data or {}), ("user_input", user_input or {})):
        value = source.get("requestId")
        if value:
            logger.debug("Resolved requestId=%s from %s", value, source_name)
            return str(value)
    logger.debug("No requestId found in request")
    return None


def ensure_not_aborted(request_id: Optional[str]) -> None:
    if request_id and request_cancellation.is_cancelled(request_id):
        logger.info("Request aborted check failed for requestId=%s", request_id)
        raise RequestAborted("Request has been cancelled.")


def graph_invoke_config(thread_id: Any, chat_seq_id: Any = None) -> dict[str, dict[str, str]]:
    """Build LangGraph runnable config.

    Uses a per-turn thread key so checkpoint state (if enabled) cannot bleed
    across questions in the same chat session.
    """
    turn_id = f"{thread_id}:{chat_seq_id}" if chat_seq_id is not None else str(thread_id)
    logger.debug("Built graph invoke config thread_id=%s chat_seq_id=%s turn_id=%s", thread_id, chat_seq_id, turn_id)
    return {"configurable": {"thread_id": turn_id}}


def _normalize_format_strings(raw: Any) -> dict[str, str]:
    """Normalize format-string maps for chat memory / chart conversion."""
    if not isinstance(raw, dict):
        return {}
    out: dict[str, str] = {}
    for key, value in raw.items():
        name = str(key or "").strip()
        fmt = str(value or "").strip()
        if name and fmt:
            out[name] = fmt
    return out


def _format_strings_from_vf_template(vf_template: Any) -> dict[str, str]:
    """Best-effort parse of measure/column formats from a base64 vf_template."""
    if not isinstance(vf_template, str) or not vf_template.strip():
        return {}
    try:
        from helicalbi.viz.chart_conversion import (
            decode_vf_template,
            _extract_format_maps,
        )

        return _extract_format_maps(decode_vf_template(vf_template))
    except Exception:  # noqa: BLE001 - memory enrichment must not fail the request
        logger.debug("Unable to extract format strings from vf_template", exc_info=True)
        return {}


def build_chat_memory_payload(
    *,
    chat_response: dict,
    sql: str,
    dialect: str,
    user_query: str,
    user_name: str,
    domain: Any = None,
    topics: Any = None,
    state: Optional[dict] = None,
) -> dict[str, Any]:
    """Build the ChatGraphMemory node payload for interactive chat.

    Persists query metadata, title, and applied Excel format strings for
    follow-up turns.
    """
    viz = (chat_response or {}).get("viz") or {}
    format_strings = _normalize_format_strings(
        (state or {}).get("format_strings")
        if isinstance(state, dict)
        else None
    )
    if not format_strings:
        format_strings = _normalize_format_strings(
            (chat_response or {}).get("format_strings")
        )
    if not format_strings:
        format_strings = _format_strings_from_vf_template(viz.get("vf_template"))

    metadata = (chat_response or {}).get("metadata") or []
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
        "format_strings": format_strings,
    }


def as_list(value: Any) -> List[Any]:
    if value is None or value == "":
        return []
    if isinstance(value, list):
        return value
    return [value]


def domain_topics_from_chat_response(chat_response: dict) -> Tuple[List[Any], List[Any]]:
    sql_section = (chat_response or {}).get("sql") or {}
    return (
        as_list(sql_section.get("required_domain")),
        as_list(sql_section.get("required_topic")),
    )


def turn_state_defaults() -> dict[str, Any]:
    """Reset ephemeral ModelState fields at the start of each user turn."""
    return {
        "skip": False,
        "sql": "",
        "sqlModel": {},
        "sql_result": {},
        "sql_error": "",
        "output": "",
        "output2": "",
        "visualization": "",
        "vf_string": "",
        "vf_title": "",
        "viz_hint": "",
        "viz_reason": "",
        "insight": "",
        "flow": [],
        "token_usage": {},
        "time_consumed": {},
    }


def json_response(payload: dict) -> str:
    return json.dumps(payload)


def resolve_audit_status_from_response(
    to_send: dict[str, Any],
    request_status: str,
    error_message: Optional[str] = None,
) -> Tuple[str, Optional[str]]:
    """Mark audit status as FAILURE when the response carries a non-empty error."""
    if request_status != "SUCCESS":
        return request_status, error_message
    error = to_send.get("error")
    if not error:
        return request_status, error_message
    error_text = str(error).strip()
    if not error_text:
        return request_status, error_message
    return "FAILURE", error_text


def extract_token_usage_dict(response_payload: dict) -> dict:
    """Read token_usage from top-level or chat_response.token_usage."""
    if not response_payload:
        return {}
    top_level = response_payload.get("token_usage")
    if isinstance(top_level, dict) and top_level:
        return top_level
    chat_response = response_payload.get("chat_response") or {}
    nested = chat_response.get("token_usage")
    if isinstance(nested, dict):
        return nested
    return {}


def log_endpoint_input(endpoint: str, data: Any) -> None:
    """Log the JSON payload received by an HTTP endpoint when enabled in config."""
    if not app_config.show_endpoint_log:
        return
    payload_text = (
        json.dumps(data, indent=2, default=str)
        if isinstance(data, (dict, list))
        else str(data)
    )
    logger.info(
        "%s\nEndpoint %s Input:\n%s\n%s",
        _BORDER,
        endpoint,
        payload_text,
        _BORDER,
    )


def error_message_chain(exc: BaseException) -> list[str]:
    """Collect exception messages from the cause chain (Java getMessageChain parity)."""
    messages: list[str] = []
    current: Optional[BaseException] = exc
    while current is not None:
        message = str(current).strip()
        if not message:
            message = type(current).__name__
        if message not in messages:
            messages.append(message)
        current = current.__cause__ or current.__context__
    return messages or [type(exc).__name__]


def error_messages_from_exception(exc: BaseException) -> list[str]:
    """Prefer the exception chain; fall back to parsing traceback text when needed."""
    messages = error_message_chain(exc)
    if len(messages) == 1 and "Traceback (most recent call last)" in messages[0]:
        return [extract_message_from_stack_trace(messages[0])]
    if not any(msg for msg in messages if msg):
        return [extract_message_from_stack_trace(traceback.format_exc())]
    return messages


def model_error_payload(messages: list[str]) -> dict[str, Any]:
    """Model-shaped error payload for semantic generation endpoints."""
    return {
        "metadata": {"domain": [""], "description": ""},
        "semantic_layer": [],
        "error": messages,
    }
