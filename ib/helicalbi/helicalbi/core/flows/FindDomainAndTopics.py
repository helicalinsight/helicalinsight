import logging

from langchain_core.messages import AIMessage
from langchain_core.output_parsers import PydanticOutputParser
from langchain_core.prompts import PromptTemplate

from helicalbi.common.LlmInvokeHelper import invoke_structured, merge_token_usage
from helicalbi.common.configuration import llm
from helicalbi.common.JsonToPara import is_bare_minimum_config
from helicalbi.model.AgentState import AgentState
from helicalbi.model.output.DomainTopicReason import DomainTopicReason
from helicalbi.prompt.DomainTopicPrompt import domain_topic_template, personification, domain_topic_prompt_string
from helicalbi.prompt.FormatInstruction import format_instruction_string
from helicalbi.service.agentservice.AgentLayerHelper import AgentLayerHelper
from helicalbi.service.agentservice.InformationProvider import InformationProvider

logger = logging.getLogger(__name__)


class FindDomainAndTopics:
    def process_flow(self, state: AgentState):
        logger.info("FindDomainAndTopics flow started")
        last_chats = state["last_chats"]

        user_query = state.get("query", "")
        helper = AgentLayerHelper(state["session_cookie"], state["agent_file_name"], state["agent_location"])
        agent_data = helper.get_agent_semantic_layer() or {}
        cube_metadata = state.get("cube_metadata") or agent_data.get("cube_metadata") or []
        agent_data = {**agent_data, "cube_metadata": cube_metadata}

        info_provider = InformationProvider(agent_data=agent_data)
        primary_domain = info_provider.get_primary_domain()
        domain_topic_string = info_provider.format_domain_info(primary_domain)
        topics = info_provider.get_topics(primary_domain)
        semantic_string = info_provider.format_semantic_layer(topics)
        input_tables = info_provider.get_input_tables(topics)
        mapping_string = info_provider.get_attribute_string(topics)
        mps = "\n".join(f"{k}->{v}" for d in mapping_string for k, v in d.items())

        if is_bare_minimum_config(agent_data) and not mps.strip():
            mps = info_provider.get_bare_minimum_context(user_query)

        business_logic = info_provider.get_matching_descriptions(input_tables)
        business_logic_string = "\n".join(business_logic)
        business_logic = "Business Logic:" + business_logic_string
        parser = PydanticOutputParser(pydantic_object=DomainTopicReason)
        prompt = PromptTemplate(
            template=domain_topic_prompt_string + format_instruction_string,
            input_variables=["domain_topic_string", "semantic_string", "business_logic", "mps",
                             "user_query,personification,last_chats","messages"],
            partial_variables={"format_instructions": parser.get_format_instructions()},
        )
        response, usage = invoke_structured(
            prompt,
            llm,
            parser,
            {
                "domain_topic_string": domain_topic_string,
                "semantic_string": semantic_string,
                "business_logic": business_logic,
                "mps": mps,
                "user_query": user_query,
                "personification": personification,
                "last_chats": state["last_chats"],
            },
        )
        merge_token_usage(state, usage)
        #state["messages"]=[AIMessage(content=str(response))]
        #state["classifyintent"]=state["messages"]

        state["domain"] = response.domain
        state["topics"] = response.topics
        return state
