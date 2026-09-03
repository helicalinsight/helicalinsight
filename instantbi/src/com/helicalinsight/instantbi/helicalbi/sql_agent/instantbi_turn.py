"""Run one InstantBI question through existing metadata / SQL / viz APIs."""
from __future__ import annotations

import base64
import logging
from typing import Any, Optional

from langchain_core.messages import HumanMessage

from helicalbi.common.ChatGraphMemory import chat_graph_memory
from helicalbi.common.ChatManager import add_message, get_last_n
from helicalbi.common.CubeInfoModel import is_cube_info_model, prepare_cube_info_model_data
from helicalbi.common.JsonToPara import has_table_column_info, prevalidate_cube_metadata
from helicalbi.model.output.ChatResponse import ChatResponse
from helicalbi.sql.SqlSanitizer import as_sql_markdown, format_sql, strip_sql_markdown
from helicalbi.sql_agent.config import DEFAULT_RESULT_ROW_CAP, DEFAULT_SCHEMA_TOP_K
from helicalbi.sql_agent.database.schema_indexer import SchemaIndexer
from helicalbi.sql_agent.database.semantic_indexer import SemanticLayerIndexer

logger = logging.getLogger(__name__)


def sql_to_data_model(
    sql: str,
    *,
    location: str,
    metadata_file_name: str,
) -> dict[str, Any] | None:
    """Build fetchData formData with base64 SQL when Adhoc formData is missing."""
    cleaned_sql = strip_sql_markdown(sql or "").strip()
    if not cleaned_sql or not metadata_file_name or not location:
        return None
    return {
        "location": location,
        "metadataFileName": metadata_file_name,
        "query": base64.b64encode(cleaned_sql.encode("utf-8")).decode("utf-8"),
        "columns": [],
    }


def _apply_cube_info_prepared(result: dict[str, Any], prepared: dict[str, Any]) -> dict[str, Any]:
    if not prepared:
        return result
    result["domain"] = prepared.get("domain") or result.get("domain") or []
    result["topics"] = prepared.get("topics") or result.get("topics") or []
    result["domain_context"] = prepared.get("domain_context") or result.get("domain_context") or ""
    result["topic_mappings"] = prepared.get("topic_mappings") or result.get("topic_mappings") or []
    result["synonyms"] = prepared.get("synonyms") or result.get("synonyms") or []
    result["business_metrics"] = prepared.get("business_metrics") or result.get("business_metrics") or []
    result["format_strings"] = prepared.get("format_strings") or result.get("format_strings") or {}
    result["column_format_strings"] = (
        prepared.get("column_format_strings") or result.get("column_format_strings") or ""
    )
    result["ai_instructions"] = prepared.get("ai_instructions") or result.get("ai_instructions") or {}
    result["column_ai_instructions"] = (
        prepared.get("column_ai_instructions") or result.get("column_ai_instructions") or ""
    )
    result["sort_orders"] = prepared.get("sort_orders") or result.get("sort_orders") or []
    result["column_sort_orders"] = prepared.get("column_sort_orders") or result.get("column_sort_orders") or ""
    result["got_domain"] = True
    return result


def load_model_session(
    *,
    session_cookie: str,
    username: str,
    model_file_name: str,
    model_location: str,
    thread_id: str,
    user_query: str,
    last_chats: Optional[list] = None,
) -> dict[str, Any]:
    """Load model, metadata, dialect, and a schema indexer via existing HI APIs."""
    from helicalbi.controller.app_context import app
    from helicalbi.controller.helpers import turn_state_defaults

    helper = app().ModelLayerHelper(session_cookie, model_file_name, model_location)
    model_data = helper.get_model_semantic_layer() or {}
    med_file_name = helper.get_metadata_layerfile()
    md_location = helper.get_metadata_layerlocation()
    actual_md = helper.get_metadata() or {}
    use_cube_info_flow = is_cube_info_model(model_data)
    cube_info_prepared: dict[str, Any] = {}
    if use_cube_info_flow:
        cube_info_prepared = prepare_cube_info_model_data(model_data, actual_md)
        cube_metadata = cube_info_prepared.get("cube_metadata") or []
        logger.info(
            "Agent dashboard using cube_info model file=%s tables=%s",
            model_file_name,
            len(cube_metadata),
        )
    else:
        original_cube_metadata = model_data.get("cube_metadata")
        cube_metadata = prevalidate_cube_metadata(original_cube_metadata, actual_md)
        if not has_table_column_info(original_cube_metadata) and cube_metadata:
            logger.info(
                "Agent dashboard using metadata API fallback file=%s tables=%s",
                med_file_name,
                len(cube_metadata),
            )

    joins = actual_md.get("joins") or []
    metadata_fun_ref = app().get_db_function_of_metadata(session_cookie, med_file_name, md_location)
    prior = last_chats if last_chats is not None else get_last_n(thread_id)

    indexer = SchemaIndexer()
    indexer.index_from_cube_metadata(cube_metadata, joins)
    schema_overview = indexer.retrieve_schema(user_query, top_k=DEFAULT_SCHEMA_TOP_K)
    extra_names = [table.name for table in indexer.catalog.tables()]
    if extra_names and schema_overview:
        listed = {name.lower() for name in extra_names if f"TABLE {name}" in schema_overview}
        remaining = [name for name in extra_names if name.lower() not in listed]
        if remaining:
            schema_overview += "\n\nOther tables: " + ", ".join(remaining[:80])
    elif extra_names and not schema_overview:
        schema_overview = indexer.catalog.to_prompt(extra_names[:12])

    semantic_indexer = SemanticLayerIndexer()
    semantic_indexer.index_model(model_data, cube_info_prepared)
    semantic_overview = semantic_indexer.overview()
    if not semantic_overview:
        semantic_overview = schema_overview

    base_state: dict[str, Any] = {
        **turn_state_defaults(),
        "query": user_query,
        "table_columns": [],
        "messages": [],
        "session_cookie": session_cookie,
        "last_chats": prior or [],
        "user_name": username,
        "thread_id": thread_id,
        "reduced_para": "",
        "cube_metadata": cube_metadata,
        "business_metrics": cube_info_prepared.get("business_metrics", []) if use_cube_info_flow else [],
        "topic_mappings": cube_info_prepared.get("topic_mappings", []) if use_cube_info_flow else [],
        "synonyms": cube_info_prepared.get("synonyms", []) if use_cube_info_flow else [],
        "domain_context": cube_info_prepared.get("domain_context", "") if use_cube_info_flow else "",
        "format_strings": cube_info_prepared.get("format_strings", {}) if use_cube_info_flow else {},
        "column_format_strings": cube_info_prepared.get("column_format_strings", "") if use_cube_info_flow else "",
        "ai_instructions": cube_info_prepared.get("ai_instructions", {}) if use_cube_info_flow else {},
        "column_ai_instructions": cube_info_prepared.get("column_ai_instructions", "") if use_cube_info_flow else "",
        "sort_orders": cube_info_prepared.get("sort_orders", []) if use_cube_info_flow else [],
        "column_sort_orders": cube_info_prepared.get("column_sort_orders", "") if use_cube_info_flow else "",
        "use_cube_info_sql_flow": use_cube_info_flow,
        "relationship_of_table": joins,
        "dbname": actual_md.get("databaseName") or "",
        "md_location": md_location,
        "md_file_name": med_file_name,
        "model_file_name": model_file_name,
        "model_location": model_location,
        "dialect": (metadata_fun_ref or {}).get("reference") or "",
    }
    return {
        "base_state": base_state,
        "use_cube_info_flow": use_cube_info_flow,
        "cube_info_prepared": cube_info_prepared,
        "md_location": md_location,
        "md_file_name": med_file_name,
        "metadata_fun_ref": metadata_fun_ref or {},
        "cube_metadata": cube_metadata,
        "metadata": actual_md,
        "indexer": indexer,
        "semantic_indexer": semantic_indexer,
        "schema_overview": schema_overview,
        "semantic_overview": semantic_overview,
        "username": username,
        "session_cookie": session_cookie,
    }


def _new_question_state(query: str, session: dict[str, Any], thread_id: str) -> dict[str, Any]:
    from helicalbi.controller.helpers import turn_state_defaults

    last_chats = get_last_n(thread_id)
    return {
        **session["base_state"],
        **turn_state_defaults(),
        "query": query,
        "sql_query": query,
        "viz_query": query,
        "messages": [HumanMessage(content=query, username=session.get("username") or "")],
        "last_chats": last_chats or session["base_state"].get("last_chats") or [],
        "thread_id": thread_id,
    }


def generate_sql_for_question(
    query: str,
    session: dict[str, Any],
    *,
    thread_id: str,
    chat_seq_id: Any,
    request_id: Optional[str] = None,
    agent_context: Optional[dict[str, Any]] = None,
) -> dict[str, Any]:
    """Run InstantBI domain + SQL graphs only (no executeQuery, no viz).

    When ``agent_context`` is provided (dashboard agent), uses the branched
    agent SQL graph to skip redundant domain / rephrase LLM steps when possible.
    """
    from GraphBuilderManger import cube_info_sql_generator_graph, sql_generator_graph
    from helicalbi.controller.app_context import app
    from helicalbi.controller.helpers import ensure_not_aborted, graph_invoke_config
    from helicalbi.core.flows.CubeInfoFlow import CubeInfoFlow

    ensure_not_aborted(request_id)
    state = _new_question_state(query, session, thread_id)
    config = graph_invoke_config(thread_id, chat_seq_id)
    use_cube_info_flow = bool(session.get("use_cube_info_flow"))
    cube_info_prepared = session.get("cube_info_prepared") or {}

    if agent_context is not None:
        from helicalbi.sql_agent.instantbi_agent_graph import run_agent_sql_turn

        # Agent path: do not pollute ChatManager history with every facet.
        return run_agent_sql_turn(
            state,
            session,
            thread_id=thread_id,
            chat_seq_id=chat_seq_id,
            request_id=request_id,
            agent_mode=str(agent_context.get("agent_mode") or ""),
            selected_domains=agent_context.get("selected_domains"),
            selected_topics=agent_context.get("selected_topics"),
        )

    add_message(thread_id, query)
    ensure_not_aborted(request_id)
    if use_cube_info_flow:
        result = CubeInfoFlow().process_flow(state)
        result = _apply_cube_info_prepared(result, cube_info_prepared)
        result = app().main_graph.invoke(result, config)
        ensure_not_aborted(request_id)
        result = cube_info_sql_generator_graph.invoke(result, config)
    else:
        result = app().main_graph.invoke(state, config)
        ensure_not_aborted(request_id)
        result = sql_generator_graph.invoke(result, config)
    return result


def execute_sql_state(state: dict[str, Any], request_id: Optional[str] = None) -> dict[str, Any]:
    """Run InstantBI executeQuery for an already-generated SQL state."""
    from helicalbi.controller.helpers import ensure_not_aborted
    from helicalbi.core.flows.SqlExecutor import SqlExecutor

    ensure_not_aborted(request_id)
    result = SqlExecutor().process_flow(state)
    if isinstance(result.get("sql_result"), dict) and "data" in result["sql_result"]:
        result["data"] = result["sql_result"]["data"]
    data = result.get("data")
    if isinstance(data, list) and len(data) > DEFAULT_RESULT_ROW_CAP:
        result["data"] = data[:DEFAULT_RESULT_ROW_CAP]
    return result


def build_viz_for_state(
    result: dict[str, Any],
    session: dict[str, Any],
    *,
    thread_id: str,
    chat_seq_id: Any,
    request_id: Optional[str] = None,
    agent_mode: Optional[str] = None,
) -> dict[str, Any]:
    """Run InstantBI viz graph and persist chat memory for one executed SQL state.

    When ``agent_mode`` is set, uses the agent viz path (fast skips polish LLM).
    """
    from helicalbi.controller.app_context import app
    from helicalbi.controller.helpers import (
        build_chat_memory_payload,
        ensure_not_aborted,
        graph_invoke_config,
    )

    ensure_not_aborted(request_id)
    config = graph_invoke_config(thread_id, chat_seq_id)
    if agent_mode is not None:
        from helicalbi.sql_agent.instantbi_agent_graph import run_agent_viz

        result = run_agent_viz(
            result,
            agent_mode=agent_mode,
            thread_id=thread_id,
            chat_seq_id=chat_seq_id,
            request_id=request_id,
        )
    else:
        result = app().viz_graph.invoke(result, config)
    result["messages"] = []
    result["last_chats"] = []

    if isinstance(result.get("sql_result"), dict) and "data" in result["sql_result"]:
        result["data"] = result["sql_result"]["data"]

    sql = result.get("sql", "")
    formatted_sql = format_sql(sql, dialect=result.get("dialect"), pretty=True)
    raw_sql = sql
    if sql:
        result["sql"] = as_sql_markdown(formatted_sql)

    # Viz skip (SQL error) still needs columns from the generated SQL.
    if not isinstance(result.get("viz_form_data"), dict):
        from helicalbi.viz.viz_model_fill import _try_sql_to_form_data

        form_data = _try_sql_to_form_data(
            formatted_sql or raw_sql,
            session_cookie=session.get("session_cookie") or "",
            md_location=session.get("md_location") or "",
            md_file_name=session.get("md_file_name") or "",
            dialect=result.get("dialect")
            or (session.get("metadata_fun_ref") or {}).get("reference"),
            metadata=session.get("metadata"),
            catalog=session.get("metadata_fun_ref"),
        )
        if form_data is None:
            form_data = sql_to_data_model(
                formatted_sql or raw_sql,
                location=session.get("md_location") or "",
                metadata_file_name=session.get("md_file_name") or "",
            )
        if form_data is not None:
            result["viz_form_data"] = form_data

    chat_response = ChatResponse.from_model_state(result).to_dict()
    chat_graph_memory.add_node(
        thread_id,
        chat_seq_id,
        build_chat_memory_payload(
            chat_response=chat_response,
            sql=raw_sql,
            dialect=(session.get("metadata_fun_ref") or {}).get("reference", ""),
            user_query=result.get("query") or "",
            user_name=session.get("username") or "",
            domain=result.get("domain") or [],
            topics=result.get("topics") or [],
            state=result if isinstance(result, dict) else None,
        ),
    )
    data = result.get("data")
    if isinstance(data, list) and len(data) > DEFAULT_RESULT_ROW_CAP:
        result["data"] = data[:DEFAULT_RESULT_ROW_CAP]
    result["_chat_response"] = chat_response
    return result


def run_interactive_turn(
    query: str,
    session: dict[str, Any],
    *,
    thread_id: str,
    chat_seq_id: Any,
    request_id: Optional[str] = None,
) -> dict[str, Any]:
    """Full InstantBI turn: generate SQL, executeQuery, then viz."""
    result = generate_sql_for_question(
        query,
        session,
        thread_id=thread_id,
        chat_seq_id=chat_seq_id,
        request_id=request_id,
    )
    result = execute_sql_state(result, request_id=request_id)
    return build_viz_for_state(
        result,
        session,
        thread_id=thread_id,
        chat_seq_id=chat_seq_id,
        request_id=request_id,
    )
