import logging
import time
import traceback
from typing import Any

from flask import request
from langchain_core.messages import HumanMessage

from GraphBuilderManger import cube_info_sql_generator_graph, sql_generator_graph
from bl.app_context import app
from bl.helpers import (
    RequestAborted,
    ensure_not_aborted,
    extract_token_usage_dict,
    graph_invoke_config,
    json_response,
    log_endpoint_input,
    resolve_audit_status_from_response,
    resolve_request_id,
    turn_state_defaults,
)
from helicalbi.audit.llm_usage_audit import audit_llm_usage_async
from helicalbi.common.ChatGraphMemory import chat_graph_memory
from helicalbi.common.ChatManager import add_message, get_last_n
from helicalbi.common.CubeInfoAgent import is_cube_info_agent, prepare_cube_info_agent_data
from helicalbi.common.JsonToPara import has_table_column_info, prevalidate_cube_metadata
from helicalbi.core.flows.CubeInfoFlow import CubeInfoFlow
from helicalbi.common.RequestCancellation import request_cancellation
from helicalbi.common.app_config import is_debug
from helicalbi.common.auth import bind_request_identity
from helicalbi.core.flows.SqlExecutor import SqlExecutor
from helicalbi.common.LlmInvokeHelper import set_total_time_consumed
from helicalbi.model.output.ChatResponse import ChatResponse
from helicalbi.sql.SqlSanitizer import format_sql

logger = logging.getLogger(__name__)


def register(flask_app) -> None:
    @flask_app.route("/interactive", methods=["POST"])
    def interactive():
        data = request.get_json()
        log_endpoint_input("/interactive", data)
        user_input = data["input"]
        user_query = user_input["inputString"]
        session_cookie, username, user_id, _org_id = bind_request_identity(data, user_input)
        agent_file_name = user_input["agent"]["file"]
        location = user_input["agent"]["dir"]
        helper = app().AgentLayerHelper(session_cookie, agent_file_name, location)
        agent_data = helper.get_agent_semantic_layer()
        med_file_name = helper.get_metadata_layerfile()
        md_location = helper.get_metadata_layerlocation()
        actual_md = app().get_json_data_metadata(session_cookie, med_file_name, md_location)
        use_cube_info_flow = is_cube_info_agent(agent_data)
        cube_info_prepared = {}
        if use_cube_info_flow:
            cube_info_prepared = prepare_cube_info_agent_data(agent_data, actual_md)
            cube_metadata = cube_info_prepared["cube_metadata"]
            logger.info(
                "Interactive chat using cube_info agent flow file=%s location=%s tables=%s",
                agent_file_name,
                location,
                len(cube_metadata),
            )
        else:
            original_cube_metadata = agent_data.get("cube_metadata")
            cube_metadata = prevalidate_cube_metadata(original_cube_metadata, actual_md)
            if not has_table_column_info(original_cube_metadata) and cube_metadata:
                logger.info(
                    "Interactive chat using metadata API fallback for cube schema file=%s location=%s tables=%s",
                    med_file_name,
                    md_location,
                    len(cube_metadata),
                )
        joins = actual_md["joins"]
        metadata_fun_ref = app().get_db_function_of_metadata(session_cookie, med_file_name, md_location)

        thread_id = user_input["chatid"]
        chat_seq_id = user_input["chat_seq_id"]

        last_chats = get_last_n(thread_id)
        if not last_chats:
            last_chats = user_input.get("last_chats", [])
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
            "use_cube_info_sql_flow": use_cube_info_flow,
            "relationship_of_table": joins,
            "dbname": actual_md["databaseName"],
            "md_location": md_location,
            "md_file_name": med_file_name,
            "agent_file_name": agent_file_name,
            "agent_location": location,
            "dialect": metadata_fun_ref["reference"],
        }
        config = graph_invoke_config(thread_id, chat_seq_id)
        request_id = resolve_request_id(data, user_input)
        if request_id:
            request_cancellation.register(request_id)
            logger.debug("Registered cancellation for interactive requestId=%s", request_id)

        result = {}
        to_send: dict[str, Any] = {}
        request_status = "SUCCESS"
        error_message: str | None = None
        request_started = time.perf_counter()
        try:
            logger.info(
                "Interactive request received user=%s thread=%s chat_seq_id=%s requestId=%s",
                username,
                thread_id,
                chat_seq_id,
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
                        cube_info_prepared.get("domain_context")
                        or result.get("domain_context")
                        or ""
                    )
                    result["topic_mappings"] = (
                        cube_info_prepared.get("topic_mappings")
                        or result.get("topic_mappings")
                        or []
                    )
                    result["synonyms"] = (
                        cube_info_prepared.get("synonyms")
                        or result.get("synonyms")
                        or []
                    )
                    result["business_metrics"] = (
                        cube_info_prepared.get("business_metrics")
                        or result.get("business_metrics")
                        or []
                    )
                    result["got_domain"] = True
                    # Still run intent rephrase; domain/topic LLM discovery is skipped via got_domain.
                    result = app().main_graph.invoke(result, config)
            else:
                logger.debug("Invoking main graph for thread=%s", thread_id)
                result = app().main_graph.invoke(state, config)

            ensure_not_aborted(request_id)
            if use_cube_info_flow:
                logger.debug("Invoking cube_info SQL generator for thread=%s", thread_id)
                result = cube_info_sql_generator_graph.invoke(result, config)
            else:
                logger.debug("Invoking SQL generator graph for thread=%s", thread_id)
                result = sql_generator_graph.invoke(result, config)

            ensure_not_aborted(request_id)
            logger.debug("Executing SQL for thread=%s", thread_id)
            result = SqlExecutor().process_flow(result)
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
                result["sql"] = f"```sql\n{formatted_sql}"

            set_total_time_consumed(result, time.perf_counter() - request_started)
            chat_response_dict = ChatResponse.from_agent_state(result).to_dict()
            to_send["chat_response"] = chat_response_dict
            if chat_response_dict.get("error"):
                to_send["error"] = chat_response_dict["error"]

            chat_graph_memory.add_node(
                thread_id,
                chat_seq_id,
                {
                    "chat_response": chat_response_dict,
                    "sql": raw_sql,
                    "dialect": metadata_fun_ref.get("reference", ""),
                    "user_query": user_query,
                    "user_name": username,
                    "domain": result.get("domain") or [],
                    "topics": result.get("topics") or [],
                },
            )
            logger.info(
                "Interactive request completed user=%s thread=%s chat_seq_id=%s has_sql=%s",
                username,
                thread_id,
                chat_seq_id,
                bool(raw_sql),
            )

        except RequestAborted:
            logger.info("Interactive request aborted for requestId=%s", request_id)
            request_status = "ABORTED"
            error_message = "Request has been cancelled."
            to_send["messages"] = []
            to_send["error"] = error_message
            to_send["aborted"] = True
            if isinstance(result, dict):
                to_send["chat_response"] = ChatResponse.from_agent_state(result).to_dict()
            else:
                to_send["chat_response"] = {}
        except Exception as e:
            logger.exception("Error while processing interactive request")
            request_status = "ERROR"
            error_message = str(e)
            to_send["messages"] = []
            result = result if isinstance(result, dict) else {}
            to_send["error"] = error_message
            if is_debug():
                to_send["stack"] = traceback.format_exc()
            if isinstance(result, dict):
                to_send["chat_response"] = ChatResponse.from_agent_state(result).to_dict()
            else:
                to_send["chat_response"] = {}
        finally:
            if request_id:
                request_cancellation.clear(request_id)
                logger.debug("Cleared cancellation for interactive requestId=%s", request_id)
            request_status, error_message = resolve_audit_status_from_response(
                to_send,
                request_status,
                error_message,
            )
            base_url = user_input.get("baseUrl") or ""
            audit_llm_usage_async(
                endpoint="/interactive",
                user_id=user_id,
                session_cookie=session_cookie,
                base_url=base_url,
                user_query=user_query,
                token_usage=extract_token_usage_dict(to_send),
                request_status=request_status,
                error_message=error_message,
                chat_id=str(thread_id) if thread_id else None,
                chat_seq_id=str(chat_seq_id) if chat_seq_id is not None else None,
            )

        return json_response(to_send)
