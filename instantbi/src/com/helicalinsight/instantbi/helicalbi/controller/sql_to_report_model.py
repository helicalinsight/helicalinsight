import logging
import traceback
from typing import Any, Dict

from flask import request

from helicalbi.common.app_config import is_debug
from helicalbi.common.auth import bind_request_identity
from helicalbi.controller.helpers import clean_sql, json_response, log_endpoint_input
from helicalbi.model.output.ChatResponse import ChatResponse
from helicalbi.sql_to_formdata import sql_to_form_data
from helicalbi.viz.viz_model_fill import _display_name, build_viz_model

logger = logging.getLogger(__name__)


def _data_types_from_form_data(form_data: dict[str, Any] | None) -> list[dict[str, str]]:
    """Build chart-shape descriptors from sql_to_formdata dimension/measure columns."""
    types: list[dict[str, str]] = []
    for col in (form_data or {}).get("columns") or []:
        if not isinstance(col, dict) or col.get("hidden"):
            continue
        name = _display_name(col)
        if not name:
            continue
        is_measure = bool(col.get("aggregate")) or (
            str(col.get("fieldType") or "").lower() == "measure"
        )
        types.append({"name": name, "type": "numeric" if is_measure else "text"})
    return types


def _viz_model_from_sql(form_data: dict[str, Any] | None) -> dict[str, Any] | None:
    """Derive VizModel shelves and chart type from SQL dimensions and measures."""
    data_types = _data_types_from_form_data(form_data)
    if not data_types:
        return None
    try:
        viz_model, chart_type, _context = build_viz_model(data_types=data_types)
        logger.info(
            "SQL-to-report-model derived viz_model chart=%s fields=%s",
            chart_type,
            len(data_types),
        )
        if hasattr(viz_model, "model_dump"):
            return viz_model.model_dump()
        if hasattr(viz_model, "dict"):
            return viz_model.dict()
        return viz_model
    except Exception:
        logger.exception("SQL-to-report-model could not derive viz_model from SQL")
        return None


def _to_report_model(form_data: dict[str, Any] | None, sql: str) -> dict[str, Any]:
    payload = dict(form_data or {})
    if sql and "sql" not in payload:
        payload["sql"] = sql
    return ChatResponse.from_model_state(
        {
            "viz_form_data": payload,
            "viz_model": _viz_model_from_sql(form_data),
            "sql": sql,
        }
    ).to_dict().get("report_model") or {"data_model": None, "viz_model": None}


def register(flask_app) -> None:
    @flask_app.route("/sql-to-report-model", methods=["POST"])
    def sql_to_report_model():
        """Convert SQL into InstantBI report_model (data_model + derived viz_model)."""
        logger.info("SQL-to-report-model endpoint invoked")
        data = request.get_json()
        log_endpoint_input("/sql-to-report-model", data)

        user_input = data.get("input", data) if data else {}
        session_cookie, username, _user_id, _org_id = bind_request_identity(data, user_input)

        sql = clean_sql(user_input.get("sql") or "")
        location = str(user_input.get("location") or "").strip()
        metadata_file_name = str(
            user_input.get("metadataFileName") or user_input.get("metadata_file_name") or ""
        ).strip()

        to_send: Dict[str, Any] = {}
        try:
            logger.info(
                "SQL-to-report-model request user=%s sql_len=%s location=%s file=%s",
                username,
                len(sql),
                location,
                metadata_file_name,
            )
            if not sql:
                raise RuntimeError("sql is required for sql-to-report-model request.")
            if not location or not metadata_file_name:
                raise RuntimeError(
                    "location and metadataFileName are required for sql-to-report-model."
                )

            form_data = sql_to_form_data(
                sql,
                location=location,
                metadata_dir=location,
                metadata_file_name=metadata_file_name,
                session_cookie=session_cookie,
            )
            to_send["report_model"] = _to_report_model(form_data, sql)
            logger.info(
                "SQL-to-report-model completed user=%s columns=%s location=%s file=%s",
                username,
                len(((to_send.get("report_model") or {}).get("data_model") or {}).get("columns") or []),
                location,
                metadata_file_name,
            )
        except Exception as e:
            logger.exception("Error while converting SQL to report_model user=%s", username)
            to_send["error"] = str(e)
            if is_debug():
                to_send["stack"] = traceback.format_exc()

        return json_response(to_send)
