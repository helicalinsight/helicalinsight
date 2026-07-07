import logging

from langchain_core.messages import AIMessage
from langchain_core.output_parsers import PydanticOutputParser
from langchain_core.prompts import PromptTemplate

from helicalbi.common.LlmInvokeHelper import invoke_structured, merge_token_usage
from helicalbi.common.configuration import llm
from helicalbi.model.SQLAgent import SQLAgent
from helicalbi.model.output.ColumnResponse import ColumnResponse
from helicalbi.prompt.DetectColumnPrompt import detect_column_prompt_string
from helicalbi.prompt.FormatInstruction import format_instruction_string
from helicalbi.sql.GetContextForSQL import get_table_col_description

logger = logging.getLogger(__name__)


class GetColumnNames:

    def process_flow(self, state: SQLAgent):
        logger.info("GetColumnNames flow started")
        user_query = state["query"]
        table_names = state["required_tables"]
        # required_examples = state["required_examples"]
        required_synonyms = state["required_synonyms"]
        last_chats = state["last_chats"]
        cube_metadata = state["cube_metadata"]
        relationship_of_table = state["relationship_of_table"]

        table_column_description = get_table_col_description(
            cube_metadata,
            table_names,
            user_query=user_query,
            agent_data={
                "topic_mappings": state.get("topic_mappings"),
                "cube_metadata": cube_metadata,
            },
        )

        parser = PydanticOutputParser(pydantic_object=ColumnResponse)
        prompt = PromptTemplate(
            template=detect_column_prompt_string + format_instruction_string,
            input_variables=["user_query", "required_synonyms", "last_chats", "relationship_of_table",
                             "table_column_description"],
            partial_variables={"format_instructions": parser.get_format_instructions()},
        )
        response, usage = invoke_structured(
            prompt,
            llm,
            parser,
            {
                "user_query": user_query,
                "required_synonyms": required_synonyms,
                "last_chats": last_chats,
                "relationship_of_table": relationship_of_table,
                "table_column_description": table_column_description,
            },
        )
        merge_token_usage(state, usage)

        state["query_plan"] = response.model_dump_json()
        #state["messages"] = [AIMessage(content=str(response))]
        return state
