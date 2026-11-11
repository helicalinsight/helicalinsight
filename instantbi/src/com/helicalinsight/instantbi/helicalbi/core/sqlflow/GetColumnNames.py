import json
import logging
import re
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
from helicalbi.core.sqlflow.util.SchemaChunks import (
    column_entries,
    cubes_for_columns,
    cubes_for_tables,
    merge_column_plans,
    split_chunks,
)
from helicalbi.prompt.DetectColumnPrompt import detect_column_prompt_string
from helicalbi.prompt.FormatInstruction import (
    brief_format_instructions,
    format_instruction_string,
)
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
                _register_name(mapping, item.get("dimension_name"), table_name)
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
            alias = (
                item.get("alias_name")
                or item.get("measure_name")
                or item.get("dimension_name")
            )
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


def _qualify_column_refs(
    column_refs: List[Any],
    cube_metadata,
    required_tables: List[str],
    column_to_tables: Optional[Dict[str, List[str]]] = None,
    cubes: Optional[Dict[str, dict]] = None,
) -> List[str]:
    """Normalize refs to unquoted ``table.physical_column`` strings."""
    if not isinstance(column_refs, list):
        return []

    column_to_tables = column_to_tables or _build_column_to_tables(cube_metadata)
    cubes = cubes if cubes is not None else _cube_by_table(cube_metadata)
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
            else:
                # LLM often prefixes a real column with the wrong table
                # (Year lives on travel_details, not meeting_details).
                owner = _select_table_for_column(
                    col_name, column_to_tables, required_tables
                )
                if owner:
                    table_name = owner
                    physical = (
                        _physical_column_name(cubes.get(owner) or {}, col_name)
                        or col_name
                    )
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
    return qualified


def _physical_owners(cube_metadata) -> Dict[str, List[str]]:
    """Map physical column_name values to the tables that store them."""
    mapping: Dict[str, List[str]] = defaultdict(list)
    for cube in iter_cube_entries(cube_metadata or []):
        table_name = cube.get("database_table")
        if not table_name:
            continue
        for bucket in ("columns", "measures"):
            for item in cube.get(bucket) or []:
                if isinstance(item, dict):
                    _register_name(mapping, item.get("column_name"), str(table_name))
    return mapping


def _refs_named_in_picked(
    query_plan: dict,
    cube_metadata,
    required_tables: List[str],
) -> List[str]:
    """Qualified columns written inside pickedDimensions / pickedMetrics text."""
    texts: List[str] = []
    for key in ("pickedDimensions", "pickedMetrics"):
        values = query_plan.get(key) or []
        if isinstance(values, list):
            texts.extend(str(item) for item in values if str(item).strip())
    blob = "\n".join(texts)
    if not blob.strip():
        return []
    owners = _physical_owners(cube_metadata)
    cubes = _cube_by_table(cube_metadata)
    refs: List[str] = []
    for column_name in sorted(owners, key=len, reverse=True):
        if not re.search(
            rf"(?<![A-Za-z0-9_]){re.escape(column_name)}(?![A-Za-z0-9_])",
            blob,
        ):
            continue
        table_name = _select_table_for_column(
            column_name, owners, required_tables
        ) or ""
        if not table_name:
            continue
        physical = (
            _physical_column_name(cubes.get(table_name) or {}, column_name)
            or column_name
        )
        refs.append(f"{table_name}.{physical}")
    return refs


def _qualify_query_plan_columns(
    query_plan: dict,
    cube_metadata,
    required_tables: List[str],
) -> dict:
    """Normalize columnName / selectColumnName to unquoted ``table.physical_column`` refs."""
    column_refs = query_plan.get("columnName") or []
    if not isinstance(column_refs, list):
        return query_plan

    column_to_tables = _build_column_to_tables(cube_metadata)
    cubes = _cube_by_table(cube_metadata)
    query_plan["columnName"] = _qualify_column_refs(
        column_refs, cube_metadata, required_tables, column_to_tables, cubes
    )
    select_refs = query_plan.get("selectColumnName")
    if isinstance(select_refs, list):
        query_plan["selectColumnName"] = _qualify_column_refs(
            select_refs, cube_metadata, required_tables, column_to_tables, cubes
        )
    # The model often leaves columnName empty and names the group-by only in SELECT.
    if not query_plan["columnName"] and query_plan.get("selectColumnName"):
        query_plan["columnName"] = list(query_plan["selectColumnName"])
    seen = set(query_plan["columnName"])
    for ref in _refs_named_in_picked(query_plan, cube_metadata, required_tables):
        if ref not in seen:
            query_plan["columnName"].append(ref)
            seen.add(ref)
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
        model_data = {
            "topic_mappings": state.get("topic_mappings"),
            "cube_metadata": cube_metadata,
            "domain_context": state.get("domain_context"),
            "domain": state.get("domain"),
            "topics": state.get("topics"),
        }
        scoped_tables = []
        if isinstance(table_names, list):
            scoped_tables = [str(table) for table in table_names if table]
        scoped_cubes = (
            cubes_for_tables(cube_metadata, scoped_tables) or cube_metadata
            if scoped_tables
            else cube_metadata
        )
        groups = split_chunks(column_entries(scoped_cubes)) or [None]

        parser = PydanticOutputParser(pydantic_object=get_column_response_model())
        prompt = PromptTemplate(
            template=detect_column_prompt_string + format_instruction_string,
            input_variables=["user_query", "required_synonyms", "last_chats",
                             "table_column_description", "dialect"],
            partial_variables={"format_instructions": brief_format_instructions(parser.pydantic_object)},
        )
        plans = []
        for index, group in enumerate(groups, start=1):
            if group is None:
                chunk_cubes = scoped_cubes
                chunk_tables = scoped_tables
            else:
                if len(groups) > 1:
                    logger.info(
                        "Column prompt chunk %s/%s (%s columns)",
                        index,
                        len(groups),
                        len(group),
                    )
                chunk_cubes = cubes_for_columns(cube_metadata, group)
                chunk_tables = list(dict.fromkeys(table for table, _bucket, _item in group))
            table_column_description = get_table_col_description(
                chunk_cubes,
                chunk_tables,
                user_query=user_query,
                model_data=model_data,
                domain_context=state.get("domain_context"),
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
                    "dialect": state.get("dialect") or "",
                },
                state=state,
            )
            plans.append(response.model_dump())

        raw_query_plan = plans[0] if len(plans) == 1 else merge_column_plans(plans)
        query_plan = _qualify_query_plan_columns(
            deepcopy(raw_query_plan),
            cube_metadata,
            table_names,
        )
        # Narrow to tables the query plan actually uses. Allow cube tables the
        # column qualifier remapped onto (FindTables may have missed a join).
        known_tables = list(_cube_by_table(cube_metadata).keys()) or list(table_names or [])
        plan_tables = tables_from_query_plan(query_plan, known_tables)
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
