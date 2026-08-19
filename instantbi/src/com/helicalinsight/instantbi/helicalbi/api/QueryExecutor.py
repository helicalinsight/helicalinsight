import json
import logging
import uuid
from typing import Optional

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
