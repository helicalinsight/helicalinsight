"""Asynchronous LLM usage audit client for the Java server."""

from __future__ import annotations

import json
import logging
import math
from concurrent.futures import ThreadPoolExecutor
from typing import Any, Mapping, Optional

import requests
import urllib3

from helicalbi.common import app_config
from helicalbi.common.auth import get_api_cache_user_id
from helicalbi.common.configuration import baseUrl as configured_base_url

logger = logging.getLogger(__name__)

_executor = ThreadPoolExecutor(max_workers=4, thread_name_prefix="llm-usage-audit")

_COST_FIELDS = ("input_cost", "output_cost", "total_cost")
_UNSAVED_LOG_PREFIX = "LLM_USAGE_AUDIT_UNSAVED"


def _sanitize_audit_token_usage(usage: dict[str, Any]) -> dict[str, Any]:
    """Drop non-finite cost values so Java BigDecimal conversion cannot fail."""
    cleaned = dict(usage)
    for field in _COST_FIELDS:
        value = cleaned.get(field)
        if value is None:
            cleaned.pop(field, None)
            continue
        try:
            number = float(value)
        except (TypeError, ValueError):
            cleaned.pop(field, None)
            continue
        if math.isfinite(number):
            cleaned[field] = number
        else:
            cleaned.pop(field, None)
    return cleaned


def _normalize_token_usage(token_usage: Any) -> dict[str, Any]:
    if token_usage is None:
        return {}
    if hasattr(token_usage, "model_dump"):
        return token_usage.model_dump(exclude_none=True)
    if isinstance(token_usage, Mapping):
        return dict(token_usage)
    return {}


def _build_audit_payload(
    *,
    endpoint: str,
    user_id: int,
    user_query: str,
    token_usage: dict[str, Any],
    request_status: str,
    error_message: Optional[str],
    chat_id: Optional[str],
    chat_seq_id: Optional[str],
) -> dict[str, Any]:
    return {
        "userId": user_id,
        "endpoint": endpoint,
        "userQuery": user_query or "",
        "tokenUsage": token_usage,
        "requestStatus": request_status,
        "errorMessage": error_message,
        "chatId": chat_id,
        "chatSeqId": chat_seq_id,
    }


def _log_unsaved_audit(
    payload: dict[str, Any],
    *,
    reason: str,
    status_code: Optional[int] = None,
    response_body: Optional[str] = None,
) -> None:
    """Log audit rows that were not persisted so they can be replayed from logs."""
    logger.error(
        "%s reason=%s status_code=%s response=%s payload=%s",
        _UNSAVED_LOG_PREFIX,
        reason,
        status_code,
        response_body,
        json.dumps(payload, default=str, separators=(",", ":")),
    )


def _audit_persisted(response: requests.Response) -> tuple[bool, str]:
    if response.status_code != 200:
        return False, f"http_status_{response.status_code}"

    try:
        body = response.json()
    except ValueError:
        logger.error(
            "LLM usage audit response was not valid JSON status=%s",
            response.status_code,
            exc_info=True,
        )
        return False, "invalid_json_response"

    if not isinstance(body, dict):
        return False, "unexpected_response_shape"

    if body.get("status") == 1:
        return True, ""

    return False, str(body.get("message") or "audit_rejected")


def _post_audit(
    *,
    endpoint: str,
    user_id: int,
    session_cookie: str,
    base_url: str,
    user_query: str,
    token_usage: dict[str, Any],
    request_status: str,
    error_message: Optional[str],
    chat_id: Optional[str],
    chat_seq_id: Optional[str],
) -> None:
    audit_url = f"{base_url.rstrip('/')}/ai/llm-usage-audit"
    payload = _build_audit_payload(
        endpoint=endpoint,
        user_id=user_id,
        user_query=user_query,
        token_usage=token_usage,
        request_status=request_status,
        error_message=error_message,
        chat_id=chat_id,
        chat_seq_id=chat_seq_id,
    )
    session = requests.Session()
    session.cookies.set("JSESSIONID", session_cookie)
    urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)
    try:
        response = session.post(audit_url, json=payload, verify=False, timeout=10)
        persisted, reason = _audit_persisted(response)
        if not persisted:
            _log_unsaved_audit(
                payload,
                reason=reason,
                status_code=response.status_code,
                response_body=response.text,
            )
            return
        logger.debug(
            "LLM usage audit persisted endpoint=%s user_id=%s tokens=%s",
            endpoint,
            user_id,
            token_usage.get("total_tokens"),
        )
    except Exception as exc:
        _log_unsaved_audit(payload, reason=f"request_failed:{exc.__class__.__name__}")
        logger.exception("LLM usage audit request failed endpoint=%s user_id=%s", endpoint, user_id)


def audit_llm_usage_async(
    *,
    endpoint: str,
    user_id: Optional[int],
    session_cookie: str,
    base_url: str,
    user_query: str,
    token_usage: Any,
    request_status: str,
    error_message: Optional[str] = None,
    chat_id: Optional[str] = None,
    chat_seq_id: Optional[str] = None,
) -> None:
    """Fire-and-forget POST to Java ``/ai/llm-usage-audit``. No-op when disabled or tokens==0."""
    if not app_config.enable_llm_usage_audit:
        return

    resolved_session_cookie = (session_cookie or "").strip()
    resolved_base_url = (base_url or configured_base_url or "").strip()
    resolved_user_id = user_id if user_id is not None else get_api_cache_user_id()
    if not resolved_session_cookie or not resolved_base_url or not resolved_user_id:
        logger.debug(
            "Skipping LLM usage audit due to missing session context endpoint=%s "
            "has_session=%s has_base_url=%s has_user_id=%s",
            endpoint,
            bool(resolved_session_cookie),
            bool(resolved_base_url),
            bool(resolved_user_id),
        )
        return

    usage = _sanitize_audit_token_usage(_normalize_token_usage(token_usage))
    total_tokens = int(usage.get("total_tokens") or 0)
    if total_tokens <= 0:
        return

    _executor.submit(
        _post_audit,
        endpoint=endpoint,
        user_id=resolved_user_id,
        session_cookie=resolved_session_cookie,
        base_url=resolved_base_url,
        user_query=user_query or "",
        token_usage=usage,
        request_status=request_status,
        error_message=error_message,
        chat_id=chat_id,
        chat_seq_id=chat_seq_id,
    )
