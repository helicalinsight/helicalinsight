import logging

from flask import request

from bl.app_context import app
from bl.helpers import log_endpoint_input
from helicalbi.common.auth import bind_request_identity

logger = logging.getLogger(__name__)


def register(flask_app) -> None:
    @flask_app.route("/suggestDomain", methods=["POST"])
    def suggest_domain():
        data = request.get_json()
        log_endpoint_input("/suggestDomain", data)
        session_cookie, username, _user_id, _org_id = bind_request_identity(data)
        model_file_name = data["model"]["file"]
        location = data["model"]["dir"]
        logger.info(
            "Domain suggestion requested user=%s model=%s location=%s",
            username,
            model_file_name,
            location,
        )
        helper = app().ModelLayerHelper(session_cookie, model_file_name, location)
        model_data = helper.get_model_semantic_layer()
        domain_name = model_data["domain"][0]["domain_name"]
        logger.info("Domain suggestion resolved domain=%s", domain_name)
        return domain_name
