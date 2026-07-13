import json
import logging
from collections import defaultdict
from copy import deepcopy
from typing import Dict, List, Optional

from langchain_core.messages import AIMessage
from langchain_core.output_parsers import PydanticOutputParser
from langchain_core.prompts import PromptTemplate

from helicalbi.common.LlmInvokeHelper import invoke_structured
from helicalbi.common.configuration import llm
from helicalbi.common.JsonToPara import iter_cube_entries
from helicalbi.core.sqlflow.util.FallbackSqlHelpers import tables_from_query_plan
from helicalbi.model.SQLAgent import SQLAgent
from helicalbi.model.output.ColumnResponse import get_column_response_model
from helicalbi.prompt.DetectColumnPrompt import detect_column_prompt_string
from helicalbi.prompt.FormatInstruction import format_instruction_string
from helicalbi.sql.GetContextForSQL import get_table_col_description

logger = logging.getLogger(__name__)


def _build_column_to_tables(cube_metadata) -> Dict[str, List[str]]:
    mapping: Dict[str, List[str]] = defaultdict(list)
    for cube in iter_cube_entries(cube_metadata or []):
        table_name = cube.get("database_table")
        if not table_name:
            continue
        for bucket in ("columns", "measures"):
            for item in cube.get(bucket) or []:
                if not isinstance(item, dict):
                    continue
                col_name = item.get("column_name")
                if not col_name:
                    continue
                if table_name not in mapping[str(col_name)]:
                    mapping[str(col_name)].append(table_name)
    return mapping


def _select_table_for_column(
    column_name: str,
    column_to_tables: Dict[str, List[str]],
    required_tables: List[str],
) -> Optional[str]:
    candidates = column_to_tables.get(column_name) or []
    if not candidates:
        return None

    required_lookup = [str(table) for table in (required_tables or []) if table]
    for table_name in required_lookup:
        if table_name in candidates:
            return table_name

    # Fall back to first discovered table for deterministic behavior.
    return candidates[0]


def _qualify_query_plan_columns(
    query_plan: dict,
    cube_metadata,
    required_tables: List[str],
) -> dict:
    column_refs = query_plan.get("columnName") or []
    if not isinstance(column_refs, list):
        return query_plan

    column_to_tables = _build_column_to_tables(cube_metadata)
    qualified: List[str] = []
    seen = set()
    for raw_ref in column_refs:
        ref = str(raw_ref).strip()
        if not ref:
            continue
        if "." in ref:
            normalized = ref
        else:
            table_name = _select_table_for_column(ref, column_to_tables, required_tables)
            normalized = f"{table_name}.{ref}" if table_name else ref
        if normalized in seen:
            continue
        seen.add(normalized)
        qualified.append(normalized)

    query_plan["columnName"] = qualified
    return query_plan


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

        parser = PydanticOutputParser(pydantic_object=get_column_response_model())
        prompt = PromptTemplate(
            template=detect_column_prompt_string + format_instruction_string,
            input_variables=["user_query", "required_synonyms", "last_chats",
                             "table_column_description"],
            partial_variables={"format_instructions": parser.get_format_instructions()},
        )
        response, _ = invoke_structured(
            prompt,
            llm,
            parser,
            {
                "user_query": user_query,
                "required_synonyms": required_synonyms,
                "last_chats": last_chats,
                "table_column_description": table_column_description,
            },
            state=state,
        )

        raw_query_plan = response.model_dump()
        query_plan = _qualify_query_plan_columns(
            deepcopy(raw_query_plan),
            cube_metadata,
            table_names,
        )
        # Narrow required tables to those actually referenced by the query plan.
        # This avoids join-API calls across the full metadata-fallback catalog.
        plan_tables = tables_from_query_plan(query_plan, table_names)
        if plan_tables:
            logger.debug(
                "Refining required_tables from query plan: before=%s after=%s",
                table_names,
                plan_tables,
            )
            state["required_tables"] = plan_tables
        logger.debug(
            "Query plan normalization: raw=%s normalized=%s",
            raw_query_plan,
            query_plan,
        )
        state["query_plan"] = json.dumps(query_plan)
        #state["messages"] = [AIMessage(content=str(response))]
        return state
