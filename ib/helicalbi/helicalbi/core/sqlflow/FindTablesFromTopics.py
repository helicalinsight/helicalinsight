import logging

from helicalbi.common.JsonToPara import get_all_cube_tables
from helicalbi.service.agentservice.AgentLayerHelper import AgentLayerHelper
from helicalbi.service.agentservice.InformationProvider import InformationProvider
from helicalbi.model.SQLAgent import SQLAgent
logger = logging.getLogger(__name__)
class FindTablesFromTopics:
    def process_flow(self, state: SQLAgent):
        logger.info("FindTablesFromTopics flow started")
        topics = state["topics"]
        helper = AgentLayerHelper(state["session_cookie"], state["agent_file_name"], state["agent_location"])
        agent_data = helper.get_agent_semantic_layer() or {}
        info_provider = InformationProvider(agent_data=agent_data)
        input_tables = info_provider.get_input_tables(topics)
        if not input_tables:
            input_tables = get_all_cube_tables(state.get("cube_metadata"))
        state["required_tables"] = input_tables
        return state
