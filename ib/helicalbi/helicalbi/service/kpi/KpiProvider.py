from langchain_core.output_parsers import PydanticOutputParser
from langchain_core.prompts import PromptTemplate

from helicalbi.common.LlmInvokeHelper import invoke_structured
from helicalbi.common.configuration import llm
from helicalbi.model.TokenUsage import TokenUsage
from helicalbi.model.output.KpiData import get_kpi_schema_model
from helicalbi.prompt.FormatInstruction import format_instruction_string
from helicalbi.prompt.KpiPrompt import prompt_string
from helicalbi.service.agentservice.InformationProvider import InformationProvider


class KpiProvider:
    def __init__(self, agent_data, input_domain, user_query="Suggest few Measurable and timebound KPIS"):
        self.agent_data = agent_data
        self.input_domain = input_domain
        self.topics = []
        self.user_query = user_query

    def top_kpis(self):
        info_provider = InformationProvider(agent_data=self.agent_data)
        domain_topic_string = info_provider.format_domain_info(self.input_domain)
        topics = info_provider.get_topics(self.input_domain)
        semantic_string = info_provider.format_semantic_layer(topics)
        input_tables = info_provider.get_input_tables(topics)
        mapping_string = info_provider.get_attribute_string(topics)
        business_logic = info_provider.get_matching_descriptions(input_tables)
        business_logic_string = "\n".join(business_logic)
        business_logic = "Business Logic:" + business_logic_string

        parser = PydanticOutputParser(pydantic_object=get_kpi_schema_model())
        prompt = PromptTemplate(
            template=prompt_string + format_instruction_string,
            input_variables=["business_logic", "semantic_string", "domain_topic_string", "mapping_string",
                             "user_query"],
            partial_variables={"format_instructions": parser.get_format_instructions()},
        )

        invoke_inputs = {
            "business_logic": business_logic,
            "semantic_string": semantic_string,
            "domain_topic_string": domain_topic_string,
            "mapping_string": mapping_string,
            "user_query": self.user_query,
        }
        response, usage = invoke_structured(prompt, llm, parser, invoke_inputs)
        return response.answer, usage.model_dump(exclude_none=True)
