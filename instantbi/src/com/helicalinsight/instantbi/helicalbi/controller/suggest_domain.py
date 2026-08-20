import logging

from flask import request

from helicalbi.controller.app_context import app
from helicalbi.controller.helpers import json_response, log_endpoint_input
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
        try:
            helper = app().ModelLayerHelper(session_cookie, model_file_name, location)
            model_data = helper.get_model_semantic_layer() or {}
            domain_name = ""
            domains = model_data.get("domain") or []
            if domains and isinstance(domains[0], dict):
                domain_name = str(domains[0].get("domain_name") or "").strip()
                if not domain_name:
                    domain_name = str(domains[0].get("description") or "").strip()
            if not domain_name:
                domain_name = helper.get_model_description()
            logger.info("Domain suggestion resolved domain=%s", domain_name)
            return domain_name
        except Exception as exc:
            logger.exception(
                "Domain suggestion failed user=%s model=%s location=%s",
                username,
                model_file_name,
                location,
            )
            return json_response({"error": str(exc)})
