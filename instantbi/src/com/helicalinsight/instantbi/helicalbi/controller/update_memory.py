import logging
import traceback
from typing import Any

from flask import request

from helicalbi.common.app_config import is_debug
from helicalbi.common.auth import bind_request_identity
from helicalbi.controller.helpers import json_response, log_endpoint_input
from helicalbi.service.report.UpdateChatMemory import (
    hydrate_chat_memory_from_report,
    unwrap_report_data,
)

logger = logging.getLogger(__name__)


def register(flask_app) -> None:
    @flask_app.route("/updateMemory", methods=["POST"])
    def update_memory():
        data = request.get_json() or {}
        logger.error("updateMemory data=%s", data)
        log_endpoint_input("/updateMemory", data)
        user_input = data.get("input", data) if isinstance(data, dict) else {}
        _session_cookie, username, _user_id, _org_id = bind_request_identity(data, user_input)
        thread_id = str(user_input.get("chatid") or user_input.get("thread_id") or "").strip()
        report_payload = user_input.get("report")
        if not isinstance(report_payload, dict):
            report_payload = data.get("report") if isinstance(data.get("report"), dict) else None
        if not isinstance(report_payload, dict):
            unwrapped = unwrap_report_data(user_input) or unwrap_report_data(data)
            if isinstance(unwrapped.get("state"), dict):
                report_payload = unwrapped

        to_send: dict[str, Any] = {}
        try:
            if not isinstance(report_payload, dict):
                raise ValueError("report is required for updateMemory")
            logger.info("updateMemory user=%s", username)
            memory = hydrate_chat_memory_from_report(
                report_payload,
                username=username,
                thread_id=thread_id,
            )
            to_send["status"] = 1
            to_send["memory"] = memory
            logger.error(
                "updateMemory completed user=%s chatid=%s turns=%s",
                username,
                memory.get("chatid"),
                memory.get("turns"),
            )
        except Exception as exc:
            logger.exception("updateMemory failed user=%s", username)
            to_send["status"] = 0
            to_send["error"] = str(exc)
            if is_debug():
                to_send["stack"] = traceback.format_exc()
        return json_response(to_send)
