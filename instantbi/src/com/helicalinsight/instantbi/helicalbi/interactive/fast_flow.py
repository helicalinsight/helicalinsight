"""Fast mode for /interactive — single-question InstantBI turn (existing behavior)."""
from __future__ import annotations

import base64
import logging
import time
from typing import Any

from langchain_core.messages import HumanMessage

from helicalbi.common.ChatGraphMemory import chat_graph_memory
from helicalbi.common.ChatManager import add_message, get_last_n
from helicalbi.common.CubeInfoModel import is_cube_info_model, prepare_cube_info_model_data
from helicalbi.common.JsonToPara import has_table_column_info, prevalidate_cube_metadata
from helicalbi.common.LlmInvokeHelper import set_total_time_consumed
from helicalbi.controller.app_context import app
from helicalbi.controller.helpers import (
    build_chat_memory_payload,
    ensure_not_aborted,
    graph_invoke_config,
    turn_state_defaults,
)
from helicalbi.core.flows.CubeInfoFlow import CubeInfoFlow
from helicalbi.core.flows.SqlExecutor import SqlExecutor
from helicalbi.interactive.modes import INTERACTIVE_MODE_FAST
from helicalbi.model.output.ChatResponse import ChatResponse
from helicalbi.sql.SqlSanitizer import as_sql_markdown, format_sql, strip_sql_markdown
from helicalbi.sql.sql_retry import execute_sql_with_retries

logger = logging.getLogger(__name__)


def sql_to_data_model(
    sql: str,
    *,
    location: str,
    metadata_dir: str,
    metadata_file_name: str,
) -> dict[str, Any] | None:
    """Build fetchData formData with base64 SQL in ``query`` and empty columns.

    Used only when sql_to_formdata Adhoc formData is unavailable.
    """
    cleaned_sql = strip_sql_markdown(sql or "").strip()
    if not cleaned_sql or not metadata_file_name or not (location or metadata_dir):
        return None
    return {
        "query": base64.b64encode(cleaned_sql.encode("utf-8")).decode("utf-8"),
        "columns": [],
    }


def run_fast_mode(
    *,
    data: dict[str, Any],
    user_query: str,
    session_cookie: str,
    username: str,
    model_file_name: str,
    location: str,
    thread_id: Any,
    chat_seq_id: Any,
    request_id: str | None,
    request_started: float,
    last_chats_fallback: list | None = None,
    rm_cols_in_filter: bool = True,
) -> tuple[dict[str, Any], dict[str, Any]]:
    """Run the classic single-turn interactive pipeline.

    Returns ``(to_send, result_state)``.
    """
    helper = app().ModelLayerHelper(session_cookie, model_file_name, location)
    model_data = helper.get_model_semantic_layer()
    med_file_name = helper.get_metadata_layerfile()
    md_location = helper.get_metadata_layerlocation()
    actual_md = helper.get_metadata()
    use_cube_info_flow = is_cube_info_model(model_data)
    cube_info_prepared: dict[str, Any] = {}
    if use_cube_info_flow:
        cube_info_prepared = prepare_cube_info_model_data(model_data, actual_md)
        cube_metadata = cube_info_prepared["cube_metadata"]
        logger.info(
            "Interactive chat using cube_info model flow file=%s location=%s tables=%s",
            model_file_name,
            location,
            len(cube_metadata),
        )
    else:
        original_cube_metadata = model_data.get("cube_metadata")
        cube_metadata = prevalidate_cube_metadata(original_cube_metadata, actual_md)
        if not has_table_column_info(original_cube_metadata) and cube_metadata:
            logger.info(
                "Interactive chat using metadata API fallback for cube schema "
                "file=%s location=%s tables=%s",
                med_file_name,
                md_location,
                len(cube_metadata),
            )
    joins = actual_md.get("joins") or []
    metadata_fun_ref = app().get_db_function_of_metadata(session_cookie, med_file_name, md_location)

    last_chats = get_last_n(thread_id)
    if not last_chats:
        last_chats = last_chats_fallback or []
    add_message(thread_id, user_query)
    logger.debug(
        "Interactive chat context thread=%s chat_seq_id=%s prior_messages=%s",
        thread_id,
        chat_seq_id,
        len(last_chats),
    )
    state: dict[str, Any] = {
        **turn_state_defaults(),
        "query": user_query,
        "table_columns": [],
        "messages": [HumanMessage(content=user_query, username=username)],
        "session_cookie": session_cookie,
        "last_chats": last_chats,
        "user_name": username,
        "thread_id": thread_id,
        "reduced_para": "",
        "cube_metadata": cube_metadata,
        "business_metrics": cube_info_prepared.get("business_metrics", []) if use_cube_info_flow else [],
        "topic_mappings": cube_info_prepared.get("topic_mappings", []) if use_cube_info_flow else [],
        "synonyms": cube_info_prepared.get("synonyms", []) if use_cube_info_flow else [],
        "domain_context": cube_info_prepared.get("domain_context", "") if use_cube_info_flow else "",
        "format_strings": cube_info_prepared.get("format_strings", {}) if use_cube_info_flow else {},
        "column_format_strings": (
            cube_info_prepared.get("column_format_strings", "") if use_cube_info_flow else ""
        ),
        "ai_instructions": cube_info_prepared.get("ai_instructions", {}) if use_cube_info_flow else {},
        "column_ai_instructions": (
            cube_info_prepared.get("column_ai_instructions", "") if use_cube_info_flow else ""
        ),
        "sort_orders": cube_info_prepared.get("sort_orders", []) if use_cube_info_flow else [],
        "column_sort_orders": (
            cube_info_prepared.get("column_sort_orders", "") if use_cube_info_flow else ""
        ),
        "use_cube_info_sql_flow": use_cube_info_flow,
        "relationship_of_table": joins,
        "dbname": actual_md["databaseName"],
        "md_location": md_location,
        "md_file_name": med_file_name,
        "model_file_name": model_file_name,
        "model_location": location,
        "dialect": metadata_fun_ref["reference"],
        "rm_cols_in_filter": rm_cols_in_filter,
    }
    config = graph_invoke_config(thread_id, chat_seq_id)
    logger.info(
        "Interactive request received user=%s thread=%s chat_seq_id=%s mode=%s requestId=%s",
        username,
        thread_id,
        chat_seq_id,
        INTERACTIVE_MODE_FAST,
        request_id,
    )
    ensure_not_aborted(request_id)
    if use_cube_info_flow:
        logger.debug("Invoking cube_info flow for thread=%s", thread_id)
        result = CubeInfoFlow().process_flow(state)
        if cube_info_prepared:
            logger.info("cube info prepared")
            result["domain"] = cube_info_prepared.get("domain") or result.get("domain") or []
            result["topics"] = cube_info_prepared.get("topics") or result.get("topics") or []
            result["domain_context"] = (
                cube_info_prepared.get("domain_context") or result.get("domain_context") or ""
            )
            result["topic_mappings"] = (
                cube_info_prepared.get("topic_mappings") or result.get("topic_mappings") or []
            )
            result["synonyms"] = cube_info_prepared.get("synonyms") or result.get("synonyms") or []
            result["business_metrics"] = (
                cube_info_prepared.get("business_metrics") or result.get("business_metrics") or []
            )
            result["format_strings"] = (
                cube_info_prepared.get("format_strings") or result.get("format_strings") or {}
            )
            result["column_format_strings"] = (
                cube_info_prepared.get("column_format_strings")
                or result.get("column_format_strings")
                or ""
            )
            result["ai_instructions"] = (
                cube_info_prepared.get("ai_instructions") or result.get("ai_instructions") or {}
            )
            result["column_ai_instructions"] = (
                cube_info_prepared.get("column_ai_instructions")
                or result.get("column_ai_instructions")
                or ""
            )
            result["sort_orders"] = (
                cube_info_prepared.get("sort_orders") or result.get("sort_orders") or []
            )
            result["column_sort_orders"] = (
                cube_info_prepared.get("column_sort_orders") or result.get("column_sort_orders") or ""
            )
            result["got_domain"] = True
            # Still run intent rephrase; domain/topic LLM discovery is skipped via got_domain.
            result = app().main_graph.invoke(result, config)
    else:
        logger.debug("Invoking main graph for thread=%s", thread_id)
        result = app().main_graph.invoke(state, config)

    ensure_not_aborted(request_id)
    from GraphBuilderManger import cube_info_sql_generator_graph, sql_generator_graph

    if use_cube_info_flow:
        logger.debug("Invoking cube_info SQL generator for thread=%s", thread_id)
        result = cube_info_sql_generator_graph.invoke(result, config)
    else:
        logger.debug("Invoking SQL generator graph for thread=%s", thread_id)
        result = sql_generator_graph.invoke(result, config)

    ensure_not_aborted(request_id)
    logger.debug("Executing SQL for thread=%s", thread_id)

    def _regenerate_sql(work: dict[str, Any], prompt: str) -> dict[str, Any]:
        from GraphBuilderManger import cube_info_sql_generator_graph, sql_generator_graph

        original = str(work.get("query") or user_query)
        work["query"] = prompt
        work["skip"] = False
        graph = cube_info_sql_generator_graph if use_cube_info_flow else sql_generator_graph
        work = graph.invoke(work, config)
        work["query"] = original
        return work

    executor = SqlExecutor()
    result = execute_sql_with_retries(
        result,
        execute=executor.process_flow,
        regenerate=_regenerate_sql,
        original_question=user_query,
    )
    executor.write_insight(result)
    ensure_not_aborted(request_id)
    logger.debug("Invoking visualization graph for thread=%s", thread_id)
    result = app().viz_graph.invoke(result, config)
    logger.debug("LLM processing messages: %s", result.get("messages", []))
    result["messages"] = []
    result["last_chats"] = []

    if (
        "sql_result" in result
        and isinstance(result["sql_result"], dict)
        and "data" in result["sql_result"]
    ):
        result["data"] = result["sql_result"]["data"]

    sql = result.get("sql", "")
    raw_sql = sql
    formatted_sql = format_sql(sql, dialect=result.get("dialect"), pretty=True)

    result["user_input"] = data
    if sql:
        result["sql"] = as_sql_markdown(formatted_sql)

    # Prefer sql_to_formdata Adhoc formData already on viz_form_data.
    # Viz skip (SQL error) still needs columns from the generated SQL;
    # empty viz is expected because executeQuery did not return rows.
    # Do not overwrite Adhoc formData with fetchData {query, columns: []}.
    if not isinstance(result.get("viz_form_data"), dict):
        try:
            from helicalbi.viz.viz_model_fill import _try_sql_to_form_data

            form_data = _try_sql_to_form_data(
                formatted_sql or raw_sql,
                session_cookie=session_cookie,
                md_location=md_location,
                md_file_name=med_file_name,
                dialect=result.get("dialect") or metadata_fun_ref.get("reference"),
                metadata=actual_md,
                catalog=metadata_fun_ref,
                rm_cols_in_filter=rm_cols_in_filter,
            )
            if form_data is None:
                form_data = sql_to_data_model(
                    formatted_sql or raw_sql,
                    location=md_location,
                    metadata_dir=md_location,
                    metadata_file_name=med_file_name,
                )
            if form_data is not None:
                result["viz_form_data"] = form_data
        except Exception:
            logger.exception(
                "Interactive data_model failed; data_model omitted thread=%s chat_seq_id=%s",
                thread_id,
                chat_seq_id,
            )

    set_total_time_consumed(result, time.perf_counter() - request_started)
    chat_response = ChatResponse.from_model_state(result)
    chat_response_dict = chat_response.to_dict()
    to_send: dict[str, Any] = {
        "mode": INTERACTIVE_MODE_FAST,
        "chat_response": chat_response.to_interactive_client_dict(),
    }
    if chat_response_dict.get("error"):
        to_send["error"] = chat_response_dict["error"]

    chat_graph_memory.add_node(
        thread_id,
        chat_seq_id,
        build_chat_memory_payload(
            chat_response=chat_response_dict,
            sql=raw_sql,
            dialect=metadata_fun_ref.get("reference", ""),
            user_query=user_query,
            user_name=username,
            domain=result.get("domain") or [],
            topics=result.get("topics") or [],
            state=result if isinstance(result, dict) else None,
        ),
    )
    logger.info(
        "Interactive request completed user=%s thread=%s chat_seq_id=%s has_sql=%s",
        username,
        thread_id,
        chat_seq_id,
        bool(raw_sql),
    )
    return to_send, result
