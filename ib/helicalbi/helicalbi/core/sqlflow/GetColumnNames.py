import json
import logging
from collections import defaultdict
from copy import deepcopy
from typing import Any, Dict, List, Optional

from langchain_core.messages import AIMessage
from langchain_core.output_parsers import PydanticOutputParser
from langchain_core.prompts import PromptTemplate

from helicalbi.common.LlmInvokeHelper import invoke_structured
from helicalbi.common.configuration import llm
from helicalbi.common.JsonToPara import (
    iter_cube_entries,
    split_table_column_ref,
    unquote_identifier,
)
from helicalbi.core.sqlflow.util.FallbackSqlHelpers import tables_from_query_plan
from helicalbi.model.SQLModel import SQLModel
from helicalbi.model.output.ColumnResponse import get_column_response_model
from helicalbi.prompt.DetectColumnPrompt import detect_column_prompt_string
from helicalbi.prompt.FormatInstruction import format_instruction_string
from helicalbi.sql.GetContextForSQL import get_table_col_description

logger = logging.getLogger(__name__)


def _register_name(mapping: Dict[str, List[str]], name: Any, table_name: str) -> None:
    key = str(name or "").strip()
    if not key:
        return
    if table_name not in mapping[key]:
        mapping[key].append(table_name)


def _build_column_to_tables(cube_metadata) -> Dict[str, List[str]]:
    """Map physical column names and semantic aliases to owning tables."""
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
                _register_name(mapping, col_name, table_name)
                _register_name(mapping, item.get("alias_name"), table_name)
                _register_name(mapping, item.get("measure_name"), table_name)
    return mapping


def _cube_by_table(cube_metadata) -> Dict[str, dict]:
    return {
        str(cube.get("database_table")): cube
        for cube in iter_cube_entries(cube_metadata or [])
        if cube.get("database_table")
    }


def _physical_column_name(cube: dict, name: str) -> Optional[str]:
    """Resolve a physical column_name or semantic alias to the physical name."""
    target = unquote_identifier(name)
    if not target or not isinstance(cube, dict):
        return None
    for bucket in ("columns", "measures"):
        for item in cube.get(bucket) or []:
            if not isinstance(item, dict):
                continue
            col_name = item.get("column_name")
            if not col_name:
                continue
            if str(col_name) == target:
                return str(col_name)
            alias = item.get("alias_name") or item.get("measure_name")
            if alias and str(alias) == target:
                return str(col_name)
    return None


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
    """Normalize columnName to unquoted ``table.physical_column`` refs."""
    column_refs = query_plan.get("columnName") or []
    if not isinstance(column_refs, list):
        return query_plan

    column_to_tables = _build_column_to_tables(cube_metadata)
    cubes = _cube_by_table(cube_metadata)
    qualified: List[str] = []
    seen = set()
    for raw_ref in column_refs:
        table_name, col_name = split_table_column_ref(raw_ref)
        if not col_name:
            continue

        if not table_name:
            table_name = _select_table_for_column(
                col_name, column_to_tables, required_tables
            ) or ""

        physical = col_name
        cube = cubes.get(table_name) if table_name else None
        if cube:
            resolved = _physical_column_name(cube, col_name)
            if resolved:
                physical = resolved
        elif not table_name:
            # Bare alias/column: resolve against any matching table.
            for candidate_table in column_to_tables.get(col_name) or []:
                resolved = _physical_column_name(cubes.get(candidate_table) or {}, col_name)
                if resolved:
                    table_name = candidate_table
                    physical = resolved
                    break

        if not table_name:
            table_name = _select_table_for_column(
                physical, column_to_tables, required_tables
            ) or ""

        normalized = f"{table_name}.{physical}" if table_name else physical
        if normalized in seen:
            continue
        seen.add(normalized)
        qualified.append(normalized)

    query_plan["columnName"] = qualified
    return query_plan


class GetColumnNames:

    def process_flow(self, state: SQLModel):
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
            model_data={
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
