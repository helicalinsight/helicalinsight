import json
import logging
import uuid

from helicalbi.api.HttpCallService import fetch_service_api

logger = logging.getLogger(__name__)


def get_json_data_metadata(session_cookie: str, metadata, location) -> dict:
    logger.info("Metadata fetch requested for metadata=%s", metadata)

    form_data = {
        "location": location,
        "uniqueId": True,
        "metadataFileName": metadata,
        "provideJoins": True
    }
    payload_json = {
        "type": "adhoc",
        "serviceType": "metadata",
        "service": "get",
        "formData": json.dumps(form_data),
        "requestId": uuid.uuid4().hex
    }
    api_response = fetch_service_api(session_cookie=session_cookie, service_json=payload_json)
    return api_response["response"]

def get_db_function_of_metadata(session_cookie: str, metadata, location) -> dict:
    logger.info("Metadata getFunction requested for metadata=%s", metadata)

    form_data = {
        "location": location,
        "metadataFileName": metadata,
        "classifier": "db.generic"
    }
    payload_json = {
        "type": "adhoc",
        "serviceType": "metadata",
        "service": "getFunctions",
        "formData": json.dumps(form_data),
        "requestId": uuid.uuid4().hex
    }
    api_response = fetch_service_api(session_cookie=session_cookie, service_json=payload_json)
    return api_response["response"]



def build_column(column_name, alias, col_id=1000,dbname=""):
    return {
        "column": {
            "name": f"{dbname}.{column_name}" if dbname else column_name,
            "id": str(col_id)
        },
        "alias": alias,
        "floatingType": "discrete"
    }

