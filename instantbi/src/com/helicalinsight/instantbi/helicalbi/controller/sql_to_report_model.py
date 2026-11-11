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


def _as_viz_dict(viz_model: Any) -> dict[str, Any] | None:
    if viz_model is None:
        return None
    if hasattr(viz_model, "model_dump"):
        dumped = viz_model.model_dump()
    elif hasattr(viz_model, "dict"):
        dumped = viz_model.dict()
    elif isinstance(viz_model, dict):
        dumped = viz_model
    else:
        return None
    return dumped if isinstance(dumped, dict) else None


def _force_table_chart(viz_model: dict[str, Any] | None) -> dict[str, Any]:
    """Table is the getReport fallback when an older response only has a VF."""
    model = dict(viz_model or {})
    chart = dict(model.get("chart") or {})
    chart["viz"] = ""
    chart["mark"] = "Table"
    chart["dualaxis"] = False
    chart["measures"] = []
    model["chart"] = chart
    data = model.get("data")
    if not isinstance(data, dict):
        model["data"] = {"rows": [], "columns": []}
    return model


def _viz_model_from_sql(
    form_data: dict[str, Any] | None,
    *,
    chart_type: str = "",
) -> dict[str, Any] | None:
    """Derive VizModel shelves and chart type from SQL dimensions and measures."""
    data_types = _data_types_from_form_data(form_data)
    if not data_types:
        return _force_table_chart(None) if chart_type == "table" else None
    try:
        viz_model, resolved_chart, _context = build_viz_model(
            data_types=data_types,
            viz_hint=chart_type,
            viz_update=bool(chart_type),
        )
        logger.info(
            "SQL-to-report-model derived viz_model chart=%s fields=%s",
            resolved_chart,
            len(data_types),
        )
        dumped = _as_viz_dict(viz_model)
        if chart_type == "table":
            return _force_table_chart(dumped)
        return dumped
    except Exception:
        logger.exception("SQL-to-report-model could not derive viz_model from SQL")
        return _force_table_chart(None) if chart_type == "table" else None


def _to_report_model(
    form_data: dict[str, Any] | None,
    sql: str,
    *,
    chart_type: str = "",
) -> dict[str, Any]:
    payload = dict(form_data or {})
    if sql and "sql" not in payload:
        payload["sql"] = sql
    return ChatResponse.from_model_state(
        {
            "viz_form_data": payload,
            "viz_model": _viz_model_from_sql(form_data, chart_type=chart_type),
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
        default_chart = str(
            user_input.get("defaultChart") or user_input.get("default_chart") or ""
        ).strip().lower()
        if default_chart in {"grid", "data table"}:
            default_chart = "table"

        to_send: Dict[str, Any] = {}
        try:
            logger.info(
                "SQL-to-report-model request user=%s sql_len=%s location=%s file=%s chart=%s",
                username,
                len(sql),
                location,
                metadata_file_name,
                default_chart or "auto",
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
            to_send["report_model"] = _to_report_model(
                form_data, sql, chart_type=default_chart
            )
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
