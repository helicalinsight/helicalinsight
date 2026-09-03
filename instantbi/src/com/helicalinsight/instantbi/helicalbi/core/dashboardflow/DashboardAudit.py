"""Fire-and-forget LLM usage audit for convert-dashboard."""
from __future__ import annotations

import logging

from helicalbi.audit.llm_usage_audit import audit_llm_usage_async
from helicalbi.model.DashboardLayoutState import DashboardLayoutState

logger = logging.getLogger(__name__)


class DashboardAudit:
    def process_flow(self, state: DashboardLayoutState) -> DashboardLayoutState:
        # Lazy import: controller.helpers pulls controller.__init__ → convert_dashboard
        # → GraphBuilderManger, which is still loading when this graph is built.
        from helicalbi.controller.helpers import (
            extract_token_usage_dict,
            resolve_audit_status_from_response,
        )

        payload = {
            "error": state.get("error"),
            "token_usage": state.get("token_usage") or {},
        }
        request_status, error_message = resolve_audit_status_from_response(
            payload,
            "ERROR" if state.get("error") else "SUCCESS",
            str(state.get("error") or "") or None,
        )
        try:
            audit_llm_usage_async(
                endpoint="/convert-dashboard",
                user_id=state.get("user_id"),
                session_cookie=str(state.get("session_cookie") or ""),
                user_query=str(state.get("user_query") or "convert-dashboard"),
                token_usage=extract_token_usage_dict(payload),
                request_status=request_status,
                error_message=error_message,
                chat_id=str(state.get("chatid") or state.get("thread_id") or "") or None,
            )
        except Exception:
            logger.exception("convert-dashboard audit layer failed")
        return state
