import logging
import traceback
from typing import Any

from flask import request

from bl.app_context import app
from bl.helpers import (
    RequestAborted,
    build_chat_response_from_item,
    domain_topics_from_chat_response,
    ensure_not_aborted,
    json_response,
    log_endpoint_input,
    resolve_request_id,
)
from helicalbi.common.ChatGraphMemory import chat_graph_memory
from helicalbi.common.RequestCancellation import request_cancellation
from helicalbi.common.app_config import is_debug
from helicalbi.common.auth import bind_request_identity
from helicalbi.sql.SqlSanitizer import extract_sql, format_sql

logger = logging.getLogger(__name__)


def register(flask_app) -> None:
    @flask_app.route("/load-chat", methods=["POST"])
    def load_chat():
        data = request.get_json()
        log_endpoint_input("/load-chat", data)
        user_input = data["input"]
        user_query = user_input["inputString"]
        session_cookie, username, _user_id, _org_id = bind_request_identity(data, user_input)
        agent_file_name = user_input["agent"]["file"]
        location = user_input["agent"]["dir"]
        helper = app().AgentLayerHelper(session_cookie, agent_file_name, location)
        med_file_name = helper.get_metadata_layerfile()
        md_location = helper.get_metadata_layerlocation()
        metadata_fun_ref = app().get_db_function_of_metadata(session_cookie, med_file_name, md_location)

        thread_id = user_input["chatid"]
        chat_seq_id = user_input["chat_seq_id"]
        chat_response_item = user_input.get("chat_response_item") or {}

        loaded_item = chat_response_item

        request_id = resolve_request_id(data, user_input)
        if request_id:
            request_cancellation.register(request_id)
            logger.debug("Registered cancellation for load-chat requestId=%s", request_id)

        to_send: dict[str, Any] = {}
        try:
            logger.info(
                "Load-chat request received user=%s thread=%s chat_seq_id=%s requestId=%s",
                username,
                thread_id,
                chat_seq_id,
                request_id,
            )
            ensure_not_aborted(request_id)

            sql_section = loaded_item.get("sql") or {}
            dialect = sql_section.get("dialect") or metadata_fun_ref.get("reference", "")
            raw_sql = extract_sql(sql_section.get("raw_sql", ""), dialect)
            logger.info("Load-chat resolved SQL thread=%s chat_seq_id=%s has_sql=%s", thread_id, chat_seq_id, bool(raw_sql))
            replaced_sql = raw_sql.replace("```sql", "").replace("```", "").strip()
            formatted_sql = format_sql(raw_sql, dialect=dialect, pretty=True) if raw_sql else ""
            display_sql = f"```sql\n{formatted_sql}" if formatted_sql else ""

            data_rows: list[Any] = []
            metadata_rows: list[Any] = []
            if raw_sql:
                logger.debug("Load-chat executing SQL for thread=%s", thread_id)
                api_response = app().execute_query(
                    session_cookie=session_cookie,
                    md_location=md_location,
                    md_file_name=med_file_name,
                    sql=replaced_sql,
                    request_id=request_id or str(thread_id),
                )
                ensure_not_aborted(request_id)
                if api_response.get("status") != 1:
                    sql_error = str(api_response.get("response", "SQL execution failed."))
                    logger.warning(
                        "Load-chat SQL execution failed thread=%s chat_seq_id=%s error=%s",
                        thread_id,
                        chat_seq_id,
                        sql_error,
                    )
                    chat_response_dict = build_chat_response_from_item(
                        loaded_item,
                        data=[],
                        metadata=[],
                        formatted_sql=display_sql,
                        error=sql_error,
                    )
                    to_send["chat_response"] = chat_response_dict
                    to_send["error"] = sql_error
                else:
                    response_payload = api_response.get("response") or {}
                    if isinstance(response_payload, dict):
                        data_rows = response_payload.get("data") or []
                        metadata_rows = response_payload.get("metadata") or []
                    logger.info(
                        "Load-chat SQL executed thread=%s rows=%s metadata_cols=%s",
                        thread_id,
                        len(data_rows),
                        len(metadata_rows),
                    )

                    chat_response_dict = build_chat_response_from_item(
                        loaded_item,
                        data=data_rows,
                        metadata=metadata_rows,
                        formatted_sql=display_sql,
                    )
                    to_send["chat_response"] = chat_response_dict
            else:
                chat_response_dict = build_chat_response_from_item(
                    loaded_item,
                    data=data_rows,
                    metadata=metadata_rows,
                    formatted_sql=display_sql,
                )
                to_send["chat_response"] = chat_response_dict

            domain, topics = domain_topics_from_chat_response(loaded_item)
            chat_graph_memory.add_node(
                thread_id,
                chat_seq_id,
                {
                    "chat_response": chat_response_dict,
                    "sql": raw_sql,
                    "dialect": dialect,
                    "user_query": user_query,
                    "user_name": username,
                    "domain": domain,
                    "topics": topics,
                },
            )
            logger.info(
                "Load-chat request completed user=%s thread=%s chat_seq_id=%s",
                username,
                thread_id,
                chat_seq_id,
            )

        except RequestAborted:
            logger.info("Load-chat request aborted for requestId=%s", request_id)
            to_send["messages"] = []
            to_send["error"] = "Request has been cancelled."
            to_send["aborted"] = True
            to_send["chat_response"] = {}
        except Exception as e:
            logger.exception("Error while processing load-chat request")
            to_send["messages"] = []
            to_send["error"] = str(e)
            if is_debug():
                to_send["stack"] = traceback.format_exc()
            to_send["chat_response"] = {}
        finally:
            if request_id:
                request_cancellation.clear(request_id)
                logger.debug("Cleared cancellation for load-chat requestId=%s", request_id)

        return json_response(to_send)
