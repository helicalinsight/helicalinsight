"""Agent flow for cube_info semantic layer files."""

import logging

from helicalbi.common.CubeInfoAgent import extract_domain_topics
from helicalbi.model.AgentState import AgentState
from helicalbi.service.agentservice.AgentLayerHelper import AgentLayerHelper

logger = logging.getLogger(__name__)


class CubeInfoFlow:
    """Prepare agent state for SQL generation from cube_info agent files.

    Skips the LLM-based domain/topic discovery used by the standard main graph
    and instead reads domain and topics directly from the agent file.
    """

    def process_flow(self, state: AgentState) -> AgentState:
        logger.info("CubeInfoFlow started")
        helper = AgentLayerHelper(
            state["session_cookie"],
            state["agent_file_name"],
            state["agent_location"],
        )
        agent_data = helper.get_agent_semantic_layer() or {}
        domains, topics = extract_domain_topics(agent_data)

        state["domain"] = domains or state.get("domain") or []
        state["topics"] = topics or state.get("topics") or []
        state["action"] = "none"
        state["sql_query"] = state.get("query", "") or ""
        state["viz_query"] = state.get("query", "") or ""
        return state
