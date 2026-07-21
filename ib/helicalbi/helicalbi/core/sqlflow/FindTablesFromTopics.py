import logging
from typing import Dict, List

from langchain_core.output_parsers import PydanticOutputParser
from langchain_core.prompts import PromptTemplate

from helicalbi.common.JsonToPara import get_all_cube_tables
from helicalbi.common.LlmInvokeHelper import invoke_structured
from helicalbi.common.configuration import llm
from helicalbi.core.sqlflow.util.FallbackSqlHelpers import needs_llm_table_narrowing
from helicalbi.model.SQLModel import SQLModel
from helicalbi.model.output.TableResponse import get_table_response_model
from helicalbi.prompt.FindTablesPrompt import find_tables_prompt_string
from helicalbi.prompt.FormatInstruction import format_instruction_string
from helicalbi.service.modelservice.ModelLayerHelper import ModelLayerHelper
from helicalbi.service.modelservice.InformationProvider import InformationProvider
from helicalbi.sql.GetContextForSQL import get_table_col_description

logger = logging.getLogger(__name__)


class FindTablesFromTopics:
    def _select_tables_via_llm(
        self,
        state: SQLModel,
        cube_metadata,
        model_data: Dict,
        known_tables: List[str],
    ) -> List[str]:
        user_query = state.get("query", "")
        last_chats = state.get("last_chats", [])
        table_column_description = get_table_col_description(
            cube_metadata,
            table_names=None,
            user_query=user_query,
            model_data=model_data,
        )

        parser = PydanticOutputParser(pydantic_object=get_table_response_model())
        prompt = PromptTemplate(
            template=find_tables_prompt_string + format_instruction_string,
            input_variables=["user_query", "last_chats", "table_column_description"],
            partial_variables={"format_instructions": parser.get_format_instructions()},
        )
        response, _ = invoke_structured(
            prompt,
            llm,
            parser,
            {
                "user_query": user_query,
                "last_chats": last_chats,
                "table_column_description": table_column_description,
            },
            state=state,
        )

        known_lookup = {str(table) for table in known_tables if table}
        selected = [
            str(table)
            for table in (response.required_tables or [])
            if str(table) in known_lookup
        ]
        selected = list(dict.fromkeys(selected))
        logger.debug(
            "LLM table selection: raw=%s validated=%s",
            response.required_tables,
            selected,
        )
        return selected

    def process_flow(self, state: SQLModel):
        logger.info("FindTablesFromTopics flow started")
        topics = state["topics"]
        helper = ModelLayerHelper(state["session_cookie"], state["model_file_name"], state["model_location"])
        model_data = helper.get_model_semantic_layer() or {}
        cube_metadata = state.get("cube_metadata") or model_data.get("cube_metadata") or []
        # Prefer prepared cube_info context already on state (topic_mappings, etc.).
        model_data = {
            **model_data,
            "cube_metadata": cube_metadata,
            "topic_mappings": state.get("topic_mappings") or model_data.get("topic_mappings") or [],
            "business_metrics": state.get("business_metrics") or model_data.get("business_metrics") or [],
            "synonyms": state.get("synonyms") or model_data.get("synonyms") or [],
        }
        info_provider = InformationProvider(model_data=model_data)
        input_tables = info_provider.get_input_tables(topics)
        known_tables = get_all_cube_tables(cube_metadata)

        if needs_llm_table_narrowing(input_tables, known_tables, topics):
            logger.info(
                "Topic matching did not narrow tables (topics=%s tables=%s); using LLM selection",
                topics,
                len(known_tables),
            )
            llm_tables = self._select_tables_via_llm(
                state,
                cube_metadata,
                model_data,
                known_tables,
            )
            if llm_tables:
                input_tables = llm_tables
            elif not input_tables:
                input_tables = known_tables

        if not input_tables:
            input_tables = known_tables

        state["required_tables"] = input_tables
        logger.debug("Required tables selected: %s", input_tables)
        return state
