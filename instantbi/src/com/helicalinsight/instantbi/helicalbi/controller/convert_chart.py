import logging
import traceback
from typing import Any, Dict, Optional

from flask import request

from helicalbi.controller.helpers import json_response, log_endpoint_input
from helicalbi.common.ChatGraphMemory import chat_graph_memory
from helicalbi.common.app_config import is_debug
from helicalbi.common.auth import bind_request_identity
from helicalbi.viz.chart_conversion import ChartConversionError, convert_chart

logger = logging.getLogger(__name__)


def _memory_node(thread_id: str, chat_seq_id: Any) -> dict:
    if not thread_id or chat_seq_id is None:
        return {}
    if not chat_graph_memory.has_node(thread_id, chat_seq_id):
        return {}
    node = chat_graph_memory.get_node(thread_id, chat_seq_id) or {}
    return node if isinstance(node, dict) else {}


def _resolve_data_types(thread_id: str, chat_seq_id: Any) -> Optional[list]:
    """Optional metadata from chat memory for field inference / similar_chart."""
    node = _memory_node(thread_id, chat_seq_id)
    metadata = node.get("metadata")
    if isinstance(metadata, list):
        return metadata
    chat_response = node.get("chat_response") or {}
    nested = chat_response.get("metadata")
    return nested if isinstance(nested, list) else None


def _resolve_vf_title(user_input: dict, thread_id: str, chat_seq_id: Any) -> str:
    title = user_input.get("vf_title") or ""
    if title:
        return str(title)
    node = _memory_node(thread_id, chat_seq_id)
    if node.get("vf_title"):
        return str(node.get("vf_title") or "")
    chat_response = node.get("chat_response") or {}
    viz = chat_response.get("viz") or {}
    return str(viz.get("vf_title") or "")


def _resolve_format_strings(
    user_input: dict, thread_id: str, chat_seq_id: Any
) -> dict[str, str]:
    """Excel-style formats from request or chat memory (interactive / load-chat)."""
    raw = user_input.get("format_strings")
    if isinstance(raw, dict) and raw:
        return {str(k): str(v) for k, v in raw.items() if str(k).strip() and str(v).strip()}

    node = _memory_node(thread_id, chat_seq_id)
    memory_formats = node.get("format_strings")
    if isinstance(memory_formats, dict) and memory_formats:
        return {
            str(k): str(v)
            for k, v in memory_formats.items()
            if str(k).strip() and str(v).strip()
        }

    chat_response = node.get("chat_response") or {}
    nested = chat_response.get("format_strings")
    if isinstance(nested, dict) and nested:
        return {
            str(k): str(v)
            for k, v in nested.items()
            if str(k).strip() and str(v).strip()
        }
    return {}


def register(flask_app) -> None:
    @flask_app.route("/convert-chart", methods=["POST"])
    def convert_chart_endpoint():
        """Convert a base64 ``vf_template`` to ``selected_chart`` without LLM."""
        logger.info("Convert-chart endpoint invoked")
        data = request.get_json()
        log_endpoint_input("/convert-chart", data)

        user_input = data.get("input", data) if data else {}
        _session_cookie, username, _user_id, _org_id = bind_request_identity(
            data, user_input
        )

        thread_id = str(user_input.get("chat_id") or "")
        chat_seq_id = user_input.get("chat_sequence_id")
        vf_template = user_input.get("vf_template") or ""
        selected_chart = user_input.get("selected_chart") or ""

        logger.info(
            "Convert-chart request user=%s thread=%s chat_sequence_id=%s selected_chart=%s",
            username,
            thread_id,
            chat_seq_id,
            selected_chart,
        )

        to_send: Dict[str, Any] = {}
        try:
            if not selected_chart:
                raise ChartConversionError("selected_chart is required.")
            result = convert_chart(
                vf_template,
                selected_chart,
                data_types=_resolve_data_types(thread_id, chat_seq_id),
                vf_title=_resolve_vf_title(user_input, thread_id, chat_seq_id),
                format_strings=_resolve_format_strings(
                    user_input, thread_id, chat_seq_id
                ),
            )
            to_send = {"viz": result}
            logger.info(
                "Convert-chart completed user=%s thread=%s chart_name=%s",
                username,
                thread_id,
                result.get("chart_name"),
            )
        except ChartConversionError as e:
            logger.exception(
                "Error while converting chart user=%s thread=%s",
                username,
                thread_id,
            )
            to_send["error"] = str(e)
            if e.viz:
                to_send["viz"] = e.viz
            if is_debug():
                to_send["stack"] = traceback.format_exc()
        except Exception as e:
            logger.exception(
                "Error while converting chart user=%s thread=%s",
                username,
                thread_id,
            )
            to_send["error"] = str(e)
            if is_debug():
                to_send["stack"] = traceback.format_exc()

        return json_response(to_send)
