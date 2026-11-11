import json
import logging
import uuid
from typing import Any, Optional

from helicalbi.api.HttpCallService import fetch_service_api

logger = logging.getLogger(__name__)


def execute_query(
    session_cookie: str,
    md_location: str,
    md_file_name: str,
    sql: str,
    request_id: Optional[str] = None,
) -> dict:
    """Execute SQL via the report ``executeQuery`` service."""
    form_data = {
        "location": md_location,
        "uniqueId": True,
        "metadataFileName": md_file_name,
        "provideJoins": True,
        "replaceView": True,
        "runtimeView": True,
        "refresh": True,
        "classifier": "db.workflow",
        "query": sql,
    }
    payload_json = {
        "type": "adhoc",
        "serviceType": "report",
        "service": "executeQuery",
        "formData": json.dumps(form_data),
        "requestId": request_id or str(uuid.uuid4()),
    }
    logger.info("Executing query via executeQuery service")
    return fetch_service_api(session_cookie=session_cookie, service_json=payload_json)


def _form_data_text(form_data: Any) -> str:
    if isinstance(form_data, str):
        return form_data
    return json.dumps(form_data)


def generate_query(
    session_cookie: str,
    form_data: Any,
    request_id: Optional[str] = None,
) -> dict:
    """Build the current SQL for a report definition via ``generateQuery``.

    The returned query includes filters applied on the chart now, which can
    differ from the original user question.
    """
    payload_json = {
        "type": "adhoc",
        "serviceType": "report",
        "service": "generateQuery",
        "formData": _form_data_text(form_data),
        "requestId": request_id or str(uuid.uuid4()),
    }
    logger.info("Generating current report query via generateQuery service")
    return fetch_service_api(session_cookie=session_cookie, service_json=payload_json)


def fetch_data(
    session_cookie: str,
    form_data: Any,
    request_id: Optional[str] = None,
) -> dict:
    """Run a report definition via the report ``fetchData`` service."""
    payload_json = {
        "type": "adhoc",
        "serviceType": "report",
        "service": "fetchData",
        "formData": _form_data_text(form_data),
        "requestId": request_id or str(uuid.uuid4()),
    }
    logger.info("Executing report via fetchData service")
    return fetch_service_api(session_cookie=session_cookie, service_json=payload_json)
