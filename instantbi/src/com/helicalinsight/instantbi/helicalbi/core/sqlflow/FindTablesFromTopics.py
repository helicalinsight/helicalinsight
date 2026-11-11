import logging
import re
from typing import Dict, List

from langchain_core.output_parsers import PydanticOutputParser
from langchain_core.prompts import PromptTemplate

from helicalbi.common.JsonToPara import get_all_cube_tables, iter_cube_entries
from helicalbi.common.LlmInvokeHelper import invoke_structured
from helicalbi.common.configuration import llm
from helicalbi.core.sqlflow.util.FallbackSqlHelpers import needs_llm_table_narrowing
from helicalbi.model.SQLModel import SQLModel
from helicalbi.model.output.TableResponse import get_table_response_model
from helicalbi.prompt.FindTablesPrompt import (
    find_tables_format_instructions,
    find_tables_prompt_string,
)
from helicalbi.prompt.FormatInstruction import format_instruction_string
from helicalbi.service.modelservice.ModelLayerHelper import ModelLayerHelper
from helicalbi.service.modelservice.InformationProvider import InformationProvider
from helicalbi.core.sqlflow.util.SchemaChunks import (
    cubes_for_tables,
    merge_name_lists,
    split_chunks,
)
from helicalbi.sql.GetContextForSQL import get_table_selection_description

logger = logging.getLogger(__name__)

# FindTables only needs recent turns; older history bloats local-model prompts.
_MAX_FIND_TABLES_CHATS = 3


def _phrase_in_question(phrase: str, question: str) -> bool:
    return re.search(
        rf"(?<![a-z0-9]){re.escape(phrase)}(?![a-z0-9])",
        question,
    ) is not None


def _phrase_parts(value) -> List[str]:
    if value is None:
        return []
    if isinstance(value, list):
        parts: List[str] = []
        for item in value:
            parts.extend(_phrase_parts(item))
        return parts
    text = str(value).strip()
    if not text:
        return []
    return [part.strip() for part in text.split(",") if part.strip()]


def _table_from_column_ref(column_ref: str) -> str:
    ref = str(column_ref or "").strip().strip('"').strip("`")
    if "." not in ref:
        return ""
    return ref.rsplit(".", 1)[0].strip().strip('"').strip("`")


def tables_named_in_question(
    question: str,
    topic_mappings,
    cube_metadata,
) -> List[str]:
    """Tables whose component name or synonym appears in the question."""
    question_text = str(question or "").lower()
    if not question_text.strip():
        return []

    labeled: List[tuple] = []

    def _add(phrase, table_name: str) -> None:
        text = str(phrase or "").strip().lower()
        table = str(table_name or "").strip()
        if len(text) < 3 or not table:
            return
        labeled.append((text, table))

    for entry in topic_mappings or []:
        if not isinstance(entry, dict):
            continue
        for component in entry.get("components") or []:
            if not isinstance(component, dict):
                continue
            table_name = _table_from_column_ref(component.get("columnName"))
            _add(component.get("name"), table_name)
            ai_context = component.get("aiContext")
            if isinstance(ai_context, dict):
                for synonym in _phrase_parts(ai_context.get("synonyms")):
                    _add(synonym, table_name)

    for cube in iter_cube_entries(cube_metadata or []):
        table_name = str(cube.get("database_table") or "").strip()
        for bucket in ("columns", "measures"):
            for item in cube.get(bucket) or []:
                if not isinstance(item, dict):
                    continue
                _add(item.get("alias_name"), table_name)
                _add(item.get("dimension_name"), table_name)
                _add(item.get("measure_name"), table_name)
                for synonym in _phrase_parts(item.get("synonyms")):
                    _add(synonym, table_name)
                ai_context = item.get("ai_context") or item.get("aiContext")
                if isinstance(ai_context, dict):
                    for synonym in _phrase_parts(ai_context.get("synonyms")):
                        _add(synonym, table_name)

    tables: List[str] = []
    seen = set()
    for phrase, table_name in sorted(labeled, key=lambda item: len(item[0]), reverse=True):
        if table_name in seen:
            continue
        if _phrase_in_question(phrase, question_text):
            seen.add(table_name)
            tables.append(table_name)
    return tables


def _trim_last_chats(last_chats) -> list:
    if not isinstance(last_chats, list) or not last_chats:
        return []
    return last_chats[-_MAX_FIND_TABLES_CHATS:]


class FindTablesFromTopics:
    def _select_table_chunk(
        self,
        state: SQLModel,
        cube_metadata,
        model_data: Dict,
        known_tables: List[str],
    ) -> List[str]:
        user_query = state.get("query", "")
        last_chats = _trim_last_chats(state.get("last_chats", []))
        table_column_description = get_table_selection_description(
            cube_metadata,
            model_data=model_data,
            topics=state.get("topics"),
        )

        parser = PydanticOutputParser(pydantic_object=get_table_response_model())
        prompt = PromptTemplate(
            template=find_tables_prompt_string + format_instruction_string,
            input_variables=["user_query", "last_chats", "table_column_description"],
            partial_variables={"format_instructions": find_tables_format_instructions()},
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
        return merge_name_lists(selected)

    def _select_tables_via_llm(
        self,
        state: SQLModel,
        cube_metadata,
        model_data: Dict,
        known_tables: List[str],
    ) -> List[str]:
        groups = split_chunks([str(table) for table in known_tables if table])
        selected: List[str] = []
        for index, group in enumerate(groups, start=1):
            if len(groups) > 1:
                logger.info(
                    "Table prompt chunk %s/%s (%s tables)",
                    index,
                    len(groups),
                    len(group),
                )
            chunk_cubes = cubes_for_tables(cube_metadata, group) or cube_metadata
            selected.extend(
                self._select_table_chunk(state, chunk_cubes, model_data, group)
            )
        selected = merge_name_lists(selected)
        logger.debug("LLM table selection: validated=%s", selected)
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
            "domain_context": state.get("domain_context") or model_data.get("domain_context") or "",
            "domain": state.get("domain") or model_data.get("domain") or [],
            "topics": state.get("topics") or model_data.get("topics") or topics,
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

        known_lookup = {str(table) for table in known_tables if table}
        mentioned = [
            table
            for table in tables_named_in_question(
                state.get("query"),
                model_data.get("topic_mappings"),
                cube_metadata,
            )
            if table in known_lookup
        ]
        added = [table for table in mentioned if table not in input_tables]
        if added:
            logger.info(
                "Question names a component on %s; adding those tables",
                added,
            )
            input_tables = merge_name_lists([*input_tables, *mentioned])

        state["required_tables"] = input_tables
        logger.debug("Required tables selected: %s", input_tables)
        return state
