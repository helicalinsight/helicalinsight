import logging
import traceback
from typing import Any

from flask import request
from langchain_core.messages import HumanMessage

from GraphBuilderManger import sql_generator_graph
from bl.app_context import app
from bl.helpers import (
    RequestAborted,
    ensure_not_aborted,
    graph_invoke_config,
    json_response,
    resolve_request_id,
    turn_state_defaults,
)
from helicalbi.common.ChatGraphMemory import chat_graph_memory
from helicalbi.common.ChatManager import add_message, get_last_n
from helicalbi.common.CubeInfoAgent import is_cube_info_agent, prepare_cube_info_agent_data
from helicalbi.common.JsonToPara import has_table_column_info, prevalidate_cube_metadata
from helicalbi.core.flows.CubeInfoFlow import CubeInfoFlow
from helicalbi.common.RequestCancellation import request_cancellation
from helicalbi.common.app_config import is_debug
from helicalbi.common.auth import resolve_session_auth
from helicalbi.core.flows.SqlExecutor import SqlExecutor
from helicalbi.model.output.ChatResponse import ChatResponse
from helicalbi.sql.SqlSanitizer import format_sql

logger = logging.getLogger(__name__)


def register(flask_app) -> None:
    @flask_app.route("/interactive", methods=["POST"])
    def interactive():
        data = request.get_json()
        user_input = data["input"]
        user_query = user_input["inputString"]
        session_cookie, username = resolve_session_auth(data, user_input)
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
                    result["domain"] = cube_info_prepared.get("domain") or result.get("domain") or []
                    result["topics"] = cube_info_prepared.get("topics") or result.get("topics") or []
            else:
                logger.debug("Invoking main graph for thread=%s", thread_id)
                result = app().main_graph.invoke(state, config)

            ensure_not_aborted(request_id)
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

            chat_response_dict = ChatResponse.from_agent_state(result).to_dict()
            to_send["chat_response"] = chat_response_dict

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
            to_send["messages"] = []
            to_send["error"] = "Request has been cancelled."
            to_send["aborted"] = True
            to_send["chat_response"] = {}
        except Exception as e:
            logger.exception("Error while processing interactive request")
            to_send["messages"] = []
            result = result if isinstance(result, dict) else {}
            to_send["error"] = str(e)
            if is_debug():
                to_send["stack"] = traceback.format_exc()
            to_send["chat_response"] = {}
        finally:
            if request_id:
                request_cancellation.clear(request_id)
                logger.debug("Cleared cancellation for interactive requestId=%s", request_id)

        return json_response(to_send)
