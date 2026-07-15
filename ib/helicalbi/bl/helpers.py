import json
import logging
import traceback
from typing import Any, List, Optional, Tuple

from flask import request

from helicalbi.common.ErrorMessages import extract_message_from_stack_trace
from helicalbi.common import app_config

from helicalbi.common.RequestCancellation import request_cancellation
from helicalbi.model.TokenUsage import TokenUsage
from helicalbi.model.TimeConsumed import TimeConsumed
from helicalbi.model.output.ChatResponse import (
    ChatResponse,
    SqlSection,
    SummarySection,
    VizSection,
)

logger = logging.getLogger(__name__)

_BORDER = "--------------"


class RequestAborted(Exception):
    """Raised when a client aborts an in-flight request."""


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


def build_chat_response_from_item(
    item: dict,
    *,
    data: list,
    metadata: list,
    formatted_sql: str,
    error: str = "",
) -> dict:
    """Assemble a chat response payload from a pre-generated item plus query results."""
    logger.debug(
        "Building chat response from item rows=%s metadata_cols=%s has_sql=%s",
        len(data),
        len(metadata),
        bool(formatted_sql),
    )
    sql_section = SqlSection(**(item.get("sql") or {}))
    if formatted_sql:
        sql_section.raw_sql = formatted_sql

    token_raw = item.get("token_usage") or {}
    token_usage = TokenUsage(
        **{key: value for key, value in token_raw.items() if value is not None}
    )
    time_raw = item.get("time_consumed") or {}
    time_consumed = TimeConsumed(
        **{key: value for key, value in time_raw.items() if value is not None}
    )

    return ChatResponse(
        viz=VizSection(**(item.get("viz") or {})),
        sql=sql_section,
        summary=SummarySection(**(item.get("summary") or {})),
        data=data,
        metadata=metadata,
        token_usage=token_usage,
        time_consumed=time_consumed,
        error=error,
    ).to_dict()


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
    """Reset ephemeral AgentState fields at the start of each user turn."""
    return {
        "skip": False,
        "sql": "",
        "sqlAgent": {},
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


def agent_error_payload(messages: list[str]) -> dict[str, Any]:
    """Agent-shaped error payload for semantic generation endpoints."""
    return {
        "metadata": {"domain": [""], "description": ""},
        "semantic_layer": [],
        "error": messages,
    }
