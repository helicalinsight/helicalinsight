import logging

from langchain_core.output_parsers import PydanticOutputParser
from langchain_core.prompts import PromptTemplate

from helicalbi.common.CubeInfoModel import topic_mappings_from_domain
from helicalbi.common.LlmInvokeHelper import invoke_structured
from helicalbi.common.configuration import llm
from helicalbi.common.JsonToPara import is_bare_minimum_config
from helicalbi.model.ModelState import ModelState
from helicalbi.model.output.DomainTopicReason import get_domain_topic_reason_model
from helicalbi.prompt.DomainTopicPrompt import personification, domain_topic_prompt_string
from helicalbi.prompt.FormatInstruction import format_instruction_string
from helicalbi.service.modelservice.ModelLayerHelper import ModelLayerHelper
from helicalbi.service.modelservice.InformationProvider import InformationProvider

logger = logging.getLogger(__name__)


class FindDomainAndTopics:
    def process_flow(self, state: ModelState):
        logger.info("FindDomainAndTopics flow started")
        if state.get("got_domain", False):
            return state

        user_query = state.get("query", "")
        helper = ModelLayerHelper(state["session_cookie"], state["model_file_name"], state["model_location"])
        model_data = helper.get_model_semantic_layer() or {}
        cube_metadata = state.get("cube_metadata") or model_data.get("cube_metadata") or []

        # Prefer prepared mappings; otherwise identify from domain topic objects
        # (``{topic, description, components}``) before semantic lookup.
        topic_mappings = (
            state.get("topic_mappings")
            or model_data.get("topic_mappings")
            or topic_mappings_from_domain(model_data)
        )
        if topic_mappings:
            state["topic_mappings"] = topic_mappings

        model_data = {
            **model_data,
            "cube_metadata": cube_metadata,
            "topic_mappings": topic_mappings or [],
        }

        info_provider = InformationProvider(model_data=model_data)
        primary_domain = info_provider.get_primary_domain()
        domain_topic_string = info_provider.format_domain_info(primary_domain)
        topics = info_provider.get_topics(primary_domain)
        semantic_string = info_provider.format_semantic_layer(topics)
        input_tables = info_provider.get_input_tables(topics)
        mapping_string = info_provider.get_attribute_string(topics)
        mps = "\n".join(f"{k}->{v}" for d in mapping_string for k, v in d.items())

        if is_bare_minimum_config(model_data) and not mps.strip():
            mps = info_provider.get_bare_minimum_context(user_query)

        business_logic = info_provider.get_matching_descriptions(input_tables)
        business_logic_string = "\n".join(business_logic)
        business_logic = "Business Logic:" + business_logic_string
        parser = PydanticOutputParser(pydantic_object=get_domain_topic_reason_model())
        prompt = PromptTemplate(
            template=domain_topic_prompt_string + format_instruction_string,
            input_variables=["domain_topic_string", "semantic_string", "business_logic", "mps",
                             "user_query,personification,last_chats","messages"],
            partial_variables={"format_instructions": parser.get_format_instructions()},
        )
        response, _ = invoke_structured(
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
            state=state,
        )

        state["domain"] = response.domain
        state["topics"] = response.topics

        # Keep topic mappings aligned with the LLM-selected topics.
        selected_topics = set(response.topics or [])
        if selected_topics and state.get("topic_mappings"):
            state["topic_mappings"] = [
                entry
                for entry in state["topic_mappings"]
                if isinstance(entry, dict) and entry.get("topic_name") in selected_topics
            ]
        elif topic_mappings:
            state["topic_mappings"] = topic_mappings

        return state
