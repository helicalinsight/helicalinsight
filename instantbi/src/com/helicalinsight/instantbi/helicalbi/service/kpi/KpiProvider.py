import logging

from langchain_core.output_parsers import PydanticOutputParser
from langchain_core.prompts import PromptTemplate

from helicalbi.common.LlmInvokeHelper import invoke_structured
from helicalbi.common import app_config
from helicalbi.common.configuration import llm
from helicalbi.model.TokenUsage import TokenUsage
from helicalbi.model.output.KpiData import get_kpi_schema_model
from helicalbi.prompt.FormatInstruction import format_instruction_string
from helicalbi.prompt.KpiPrompt import kpi_format_instructions, prompt_string
from helicalbi.service.modelservice.InformationProvider import InformationProvider

logger = logging.getLogger(__name__)


def _suggestion_count(value) -> int:
    try:
        count = int(value)
    except (TypeError, ValueError):
        return 5
    return count if count > 0 else 5


class KpiProvider:
    def __init__(self, model_data, input_domain, user_query=None, top_n=None):
        self.model_data = model_data
        self.input_domain = input_domain
        self.top_n = _suggestion_count(top_n)
        self.topics = []
        model_suggestion = (model_data or {}).get("suggestion_query")
        if model_suggestion:
            self.user_query = model_suggestion
        elif user_query is not None:
            self.user_query = user_query
        else:
            self.user_query = app_config.kpi_suggestion_query

    def top_kpis(self):
        info_provider = InformationProvider(model_data=self.model_data)
        input_domain = str(self.input_domain or "").strip()
        if not input_domain:
            input_domain = info_provider.get_primary_domain()
        domain_topic_string = info_provider.format_domain_info(input_domain)
        topics = info_provider.get_topics(input_domain)
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
            partial_variables={"format_instructions": kpi_format_instructions(self.top_n)},
        )

        invoke_inputs = {
            "business_logic": business_logic,
            "semantic_string": semantic_string,
            "domain_topic_string": domain_topic_string,
            "mapping_string": mapping_string,
            "user_query": self.user_query,
        }
        response, usage = invoke_structured(prompt, llm, parser, invoke_inputs)
        if not response.answer:
            logger.info("Top N answer was empty; asking once more for %s questions", self.top_n)
            invoke_inputs["user_query"] = (
                f"{self.user_query}\n"
                f"The previous answer list was empty. Return {self.top_n} questions from the schema above."
            )
            response, retry_usage = invoke_structured(prompt, llm, parser, invoke_inputs)
            usage = usage + retry_usage
        return response.answer[: self.top_n], usage.model_dump(exclude_none=True)
