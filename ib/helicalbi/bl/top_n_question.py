import logging

from flask import request

from bl.app_context import app
from bl.helpers import error_messages_from_exception, json_response
from helicalbi.common.auth import resolve_session_auth

logger = logging.getLogger(__name__)


def register(flask_app) -> None:
    @flask_app.route("/topNQuestion", methods=["POST"])
    def top_n_question():
        data = request.get_json()
        domain = data["domain"]
        top_n = data["topN"]
        session_cookie, username = resolve_session_auth(data)
        agent_file_name = data["agent"]["file"]
        location = data["agent"]["dir"]
        logger.info(
            "Top N questions requested user=%s domain=%s topN=%s agent=%s",
            username,
            domain,
            top_n,
            agent_file_name,
        )
        try:
            helper = app().AgentLayerHelper(session_cookie, agent_file_name, location)
            agent_data = helper.get_agent_semantic_layer()
            kpi_provider = app().KpiProvider(agent_data, domain)
            found = kpi_provider.top_kpis()
            logger.info("Top N questions resolved count=%s domain=%s", len(found), domain)
            return "\n".join(found)
        except Exception as exc:
            messages = error_messages_from_exception(exc)
            logger.exception(
                "Top N questions failed user=%s domain=%s agent=%s messages=%s",
                username,
                domain,
                agent_file_name,
                messages,
            )
            return json_response({"error": messages})
