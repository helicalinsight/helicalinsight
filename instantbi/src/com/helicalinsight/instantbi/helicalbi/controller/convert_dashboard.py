import logging
import traceback
from typing import Any, Dict

from flask import request

from GraphBuilderManger import dashboard_layout_graph
from helicalbi.controller.helpers import json_response, log_endpoint_input
from helicalbi.common.app_config import is_debug
from helicalbi.common.auth import bind_request_identity
from helicalbi.core.dashboardflow.collect_items import collect_items

logger = logging.getLogger(__name__)


def register(flask_app) -> None:
    @flask_app.route("/convert-dashboard", methods=["POST"])
    def convert_dashboard():
        logger.info("Convert-dashboard endpoint invoked")
        data = request.get_json()
        log_endpoint_input("/convert-dashboard", data)

        user_input = data.get("input", data) if data else {}
        session_cookie, username, user_id, _org_id = bind_request_identity(data, user_input)
        thread_id = str(
            user_input.get("chatid") or user_input.get("thread_id") or user_input.get("chat_id") or ""
        )

        to_send: Dict[str, Any] = {}
        try:
            items = collect_items(user_input)
            if not items:
                raise RuntimeError("No visualizations were provided for convert-dashboard.")
            logger.info(
                "Convert-dashboard request user=%s thread=%s items=%s",
                username,
                thread_id,
                len(items),
            )
            result = dashboard_layout_graph.invoke(
                {
                    "items": items,
                    "user_input": user_input,
                    "username": username,
                    "user_id": user_id,
                    "session_cookie": session_cookie,
                    "thread_id": thread_id,
                    "chatid": thread_id,
                    "domain": user_input.get("domain"),
                    "topics": user_input.get("topics"),
                    "user_query": str(user_input.get("inputString") or user_input.get("query") or ""),
                }
            )
            if result.get("error"):
                to_send["error"] = result["error"]
            to_send.update(
                {
                    "chatid": thread_id,
                    "items": result.get("items") or items,
                    "theme": result.get("theme") or {},
                    "templateId": result.get("templateId") or "",
                    "layout": result.get("layout") or [],
                    "token_usage": result.get("token_usage") or {},
                }
            )
            logger.info(
                "Convert-dashboard completed user=%s items=%s",
                username,
                len(to_send.get("items") or []),
            )
        except Exception as e:
            logger.exception("Error while converting chat viz array to dashboard user=%s", username)
            to_send["error"] = str(e)
            if is_debug():
                to_send["stack"] = traceback.format_exc()

        return json_response(to_send)
