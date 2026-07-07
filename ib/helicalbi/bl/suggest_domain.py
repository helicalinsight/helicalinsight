import logging

from flask import request

from bl.app_context import app
from helicalbi.common.auth import resolve_session_auth

logger = logging.getLogger(__name__)


def register(flask_app) -> None:
    @flask_app.route("/suggestDomain", methods=["POST"])
    def suggest_domain():
        data = request.get_json()
        session_cookie, username = resolve_session_auth(data)
        agent_file_name = data["agent"]["file"]
        location = data["agent"]["dir"]
        logger.info(
            "Domain suggestion requested user=%s agent=%s location=%s",
            username,
            agent_file_name,
            location,
        )
        helper = app().AgentLayerHelper(session_cookie, agent_file_name, location)
        agent_data = helper.get_agent_semantic_layer()
        domain_name = agent_data["domain"][0]["domain_name"]
        logger.info("Domain suggestion resolved domain=%s", domain_name)
        return domain_name
