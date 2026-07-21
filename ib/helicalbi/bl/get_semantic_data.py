import logging

from flask import request

from bl.app_context import app
from bl.helpers import model_error_payload, error_messages_from_exception, json_response, log_endpoint_input
from helicalbi.common.auth import bind_request_identity

logger = logging.getLogger(__name__)


def register(flask_app) -> None:
    @flask_app.route("/getSemanticData", methods=["POST"])
    def get_semantic_data():
        data = request.get_json()
        log_endpoint_input("/getSemanticData", data)
        user_input = data["input"]
        location = user_input["location"]
        file_name = user_input["fileName"]
        session_cookie, username, _user_id, _org_id = bind_request_identity(data, user_input)
        tables_requested = user_input.get("tables", [])
        logger.info(
            "Semantic data requested user=%s file=%s location=%s tables=%s",
            username,
            file_name,
            location,
            len(tables_requested),
        )
        try:
            result = app().transform_json(
                session_cookie, file_name, location, tables_requested
            )
            logger.info("Semantic data resolved file=%s", file_name)
            return json_response(result)
        except Exception as exc:
            messages = error_messages_from_exception(exc)
            logger.exception(
                "Semantic data generation failed user=%s file=%s location=%s messages=%s",
                username,
                file_name,
                location,
                messages,
            )
            return json_response(model_error_payload(messages))
