import json
import logging
import traceback
from typing import Any, Dict, Optional, Tuple

from flask import request

from bl.app_context import app
from bl.helpers import (
    RequestAborted,
    as_list,
    domain_topics_from_chat_response,
    ensure_not_aborted,
    extract_token_usage_dict,
    json_response,
    log_endpoint_input,
    resolve_request_id,
)
from helicalbi.audit.llm_usage_audit import audit_llm_usage_async
from helicalbi.common.ChatGraphMemory import chat_graph_memory
from helicalbi.common.ChatManager import add_insight, get_last_insight
from helicalbi.common.RequestCancellation import request_cancellation
from helicalbi.common.app_config import is_debug, default_sql_limit
from helicalbi.common.auth import bind_request_identity, resolve_role_profile
from helicalbi.common.configuration import llm
from helicalbi.prompt.DataInsightPrompt import data_insight_prompt_formatted
from helicalbi.prompt.ErrorPrompt import error_prompt_formatted
from helicalbi.sql.SqlSanitizer import extract_sql

logger = logging.getLogger(__name__)

_DATA_INSIGHT_SAMPLE_ROWS = default_sql_limit


def _resolve_memory_for_data_insight(
    user_input: dict,
    thread_id: str,
    chat_seq_id: Any,
) -> Dict[str, Any]:
    """Resolve domain/topic context for ``/data-insight`` from memory or chat item."""
    memory: Dict[str, Any] = {
        "domain": [],
        "topics": [],
    }

    if thread_id and chat_seq_id is not None and chat_graph_memory.has_node(thread_id, chat_seq_id):
        memory_node = chat_graph_memory.get_node(thread_id, chat_seq_id) or {}
        memory["domain"] = as_list(memory_node.get("domain"))
        memory["topics"] = as_list(memory_node.get("topics"))
        if memory["domain"] or memory["topics"]:
            logger.info(
                "Resolved domain/topic for data-insight from memory chatid=%s chat_seq_id=%s",
                thread_id,
                chat_seq_id,
            )
            return memory
        logger.debug(
            "Memory node found but domain/topic empty for chatid=%s chat_seq_id=%s",
            thread_id,
            chat_seq_id,
        )

    chat_response_item = user_input.get("chat_response_item") or {}
    if chat_response_item:
        domain, topics = domain_topics_from_chat_response(chat_response_item)
        memory["domain"] = domain
        memory["topics"] = topics
        if domain or topics:
            logger.info("Resolved domain/topic for data-insight from chat_response_item")
            return memory

    logger.debug(
        "No domain/topic resolved for data-insight chatid=%s chat_seq_id=%s",
        thread_id,
        chat_seq_id,
    )
    return memory


def _resolve_sql_for_data_insight(
    user_input: dict,
    thread_id: str,
    chat_seq_id: Any,
) -> str:
    """Resolve SQL for ``/data-insight`` from memory, input, or saved chat item."""

    def _clean_sql(raw_sql: str, dialect: str = "") -> str:
        if not raw_sql:
            return ""
        if "```" in raw_sql:
            raw_sql = extract_sql(raw_sql, dialect)
        return raw_sql.replace("```sql", "").replace("```", "").strip()

    if thread_id and chat_seq_id is not None:
        if chat_graph_memory.has_node(thread_id, chat_seq_id):
            memory_node = chat_graph_memory.get_node(thread_id, chat_seq_id)
            if memory_node:
                sql = _clean_sql(
                    memory_node.get("sql", ""),
                    memory_node.get("dialect", ""),
                )
                if sql:
                    logger.info(
                        "Resolved SQL for data-insight from memory chatid=%s chat_seq_id=%s",
                        thread_id,
                        chat_seq_id,
                    )
                    return sql
                logger.debug(
                    "Memory node found but SQL empty for chatid=%s chat_seq_id=%s",
                    thread_id,
                    chat_seq_id,
                )
        else:
            logger.debug(
                "No memory node for data-insight chatid=%s chat_seq_id=%s",
                thread_id,
                chat_seq_id,
            )

    sql = user_input.get("sql", "")
    if sql:
        resolved = _clean_sql(sql)
        logger.info("Resolved SQL for data-insight from request input")
        return resolved

    chat_response_item = user_input.get("chat_response_item") or {}
    if chat_response_item:
        sql_section = chat_response_item.get("sql") or {}
        dialect = sql_section.get("dialect", "")
        resolved = _clean_sql(sql_section.get("raw_sql", ""), dialect)
        if resolved:
            logger.info("Resolved SQL for data-insight from chat_response_item")
            return resolved

    logger.warning(
        "No SQL resolved for data-insight chatid=%s chat_seq_id=%s",
        thread_id,
        chat_seq_id,
    )
    return ""


def _generate_data_insight_from_rows(
    *,
    username: str,
    user_question: str,
    sql: str,
    sample_data: list[Any],
    thread_id: str,
    profile: Optional[Dict[str, Any]] = None,
    memory: Optional[Dict[str, Any]] = None,
) -> Tuple[str, Dict[str, Any]]:
    prev_responses = get_last_insight(thread_id) if thread_id else []
    row_count = len(sample_data)
    if row_count > _DATA_INSIGHT_SAMPLE_ROWS:
        logger.debug(
            "Truncating data-insight sample rows from %s to %s",
            row_count,
            _DATA_INSIGHT_SAMPLE_ROWS,
        )
        sample_data = sample_data[:_DATA_INSIGHT_SAMPLE_ROWS]

    logger.info(
        "Generating data insight for user=%s thread=%s rows=%s",
        username,
        thread_id,
        len(sample_data),
    )
    user_profile = (profile or {}).get("userProfile") or []
    selected_domain = (memory or {}).get("domain") or []
    selected_topics = (memory or {}).get("topics") or []
    insight_msg, usage = app().invoke_llm(
        llm,
        data_insight_prompt_formatted.format(
            username=username,
            user_question=user_question,
            sql=sql,
            userProfile=json.dumps(user_profile, default=str),
            domain=json.dumps(selected_domain, default=str),
            topics=json.dumps(selected_topics, default=str),
            sample_data=json.dumps(sample_data, default=str),
            prev_responses=prev_responses,
        ),
    )
    insight = insight_msg.content
    if thread_id:
        add_insight(thread_id, insight)
        logger.debug("Stored data insight for thread=%s", thread_id)
    logger.info("Data insight generated for user=%s thread=%s", username, thread_id)
    return insight, usage.model_dump(exclude_none=True)


def register(flask_app) -> None:
    @flask_app.route("/data-insight", methods=["POST"])
    def data_insight():
        """Execute SQL, sample result rows, and generate a Markdown insight."""
        logger.info("Data-insight endpoint invoked")
        data = request.get_json()
        log_endpoint_input("/data-insight", data)
        logger.debug("Data-insight parsed request JSON keys=%s", list((data or {}).keys()))

        user_input = data.get("input", data)
        logger.debug("Data-insight resolved user_input keys=%s", list((user_input or {}).keys()))

        session_cookie, username = bind_request_identity(data, user_input)
        profile = resolve_role_profile(data, user_input)
        logger.debug("Data-insight resolved session for user=%s", username)
        logger.debug(
            "Data-insight profile roles=%s profiles=%s",
            len(profile.get("userRole") or []),
            len(profile.get("userProfile") or []),
        )
        logger.debug("Data-insight username=%s", username)

        thread_id = user_input.get("thread_id", user_input.get("chatid", ""))
        logger.debug("Data-insight thread_id=%s", thread_id)

        chat_seq_id = user_input.get("chat_seq_id")
        logger.debug("Data-insight chat_seq_id=%s", chat_seq_id)

        user_question = (
            user_input.get("user_question")
            or user_input.get("userQuestion")
            or user_input.get("inputString")
            or ""
        )
        logger.debug("Data-insight user_question length=%s", len(user_question))

        md_location = user_input.get("md_location", user_input.get("mdLocation", ""))
        logger.debug("Data-insight md_location=%s", md_location)

        md_file_name = user_input.get("md_file_name", user_input.get("mdFileName", ""))
        logger.debug("Data-insight md_file_name=%s", md_file_name)

        agent = user_input.get("agent")
        logger.debug("Data-insight agent provided=%s", bool(agent))
        if agent and not (md_location and md_file_name):
            logger.debug("Resolving metadata from agent for data-insight user=%s", username)
            helper = app().AgentLayerHelper(session_cookie, agent["file"], agent["dir"])
            logger.debug("Data-insight AgentLayerHelper created agent=%s", agent.get("file"))
            md_file_name = helper.get_metadata_layerfile()
            logger.debug("Data-insight resolved md_file_name from agent=%s", md_file_name)
            md_location = helper.get_metadata_layerlocation()
            logger.debug("Data-insight resolved md_location from agent=%s", md_location)

        sql = _resolve_sql_for_data_insight(user_input, thread_id, chat_seq_id)
        memory = _resolve_memory_for_data_insight(user_input, thread_id, chat_seq_id)
        logger.info("Data-insight resolved SQL present=%s length=%s", bool(sql), len(sql))
        logger.debug(
            "Data-insight memory domain=%s topics=%s",
            memory.get("domain"),
            memory.get("topics"),
        )

        request_id = resolve_request_id(data, user_input)
        logger.debug("Data-insight request_id=%s", request_id)
        if request_id:
            request_cancellation.register(request_id)
            logger.debug("Registered cancellation for data-insight requestId=%s", request_id)

        to_send: Dict[str, Any] = {}
        request_status = "SUCCESS"
        error_message: Optional[str] = None
        try:
            logger.info(
                "Data insight request for user=%s thread=%s chat_seq_id=%s",
                username,
                thread_id,
                chat_seq_id,
            )
            ensure_not_aborted(request_id)
            logger.debug("Data-insight abort check passed requestId=%s", request_id)

            if not sql:
                logger.error("Data-insight no SQL found thread=%s chat_seq_id=%s", thread_id, chat_seq_id)
                raise RuntimeError("No SQL found for data insight request.")

            logger.debug(
                "Data-insight executing SQL thread=%s md_file=%s md_location=%s",
                thread_id,
                md_file_name,
                md_location,
            )
            api_response = app().execute_query(
                session_cookie=session_cookie,
                md_location=md_location,
                md_file_name=md_file_name,
                sql=sql,
                request_id=request_id or str(thread_id),
            )
            logger.debug(
                "Data-insight execute_query completed status=%s",
                api_response.get("status"),
            )
            ensure_not_aborted(request_id)
            logger.debug("Data-insight post-query abort check passed requestId=%s", request_id)

            if api_response.get("status") != 1:
                sql_error = api_response.get("response", "SQL execution failed.")
                logger.warning(
                    "Data-insight SQL execution failed thread=%s chat_seq_id=%s error=%s",
                    thread_id,
                    chat_seq_id,
                    sql_error,
                )
                logger.debug("Data-insight invoking LLM for SQL error insight user=%s", username)
                insight_msg, usage = app().invoke_llm(
                    llm,
                    error_prompt_formatted.format(
                        response_string=sql_error,
                        user_query=user_question,
                        username=username,
                    ),
                )
                logger.debug("Data-insight LLM error insight received length=%s", len(insight_msg.content or ""))
                to_send["insight"] = insight_msg.content
                logger.debug("Data-insight set insight from SQL error response")
                to_send["sql_error"] = sql_error
                to_send["error"] = sql_error
                logger.debug("Data-insight set sql_error in response")
                to_send["token_usage"] = usage.model_dump(exclude_none=True)
                logger.info(
                    "Data-insight returned SQL error insight for thread=%s tokens=%s",
                    thread_id,
                    to_send["token_usage"].get("total_tokens"),
                )
            else:
                response_payload = api_response.get("response") or {}
                logger.debug("Data-insight response payload type=%s", type(response_payload).__name__)
                data_rows: list[Any] = []
                if isinstance(response_payload, dict):
                    data_rows = response_payload.get("data") or []
                    logger.debug("Data-insight extracted data_rows count=%s", len(data_rows))
                logger.info(
                    "Data-insight SQL executed thread=%s rows=%s",
                    thread_id,
                    len(data_rows),
                )

                logger.debug("Data-insight invoking insight generation for user=%s", username)
                insight, token_usage = _generate_data_insight_from_rows(
                    username=username,
                    user_question=user_question,
                    sql=sql,
                    sample_data=data_rows,
                    thread_id=thread_id,
                    profile=profile,
                    memory=memory,
                )
                logger.debug("Data-insight insight generated length=%s", len(insight or ""))
                to_send["insight"] = insight
                logger.debug("Data-insight set insight in response")
                to_send["token_usage"] = token_usage
                logger.info(
                    "Data-insight request completed thread=%s chat_seq_id=%s tokens=%s",
                    thread_id,
                    chat_seq_id,
                    token_usage.get("total_tokens"),
                )

        except RequestAborted:
            logger.info("Data insight request aborted for requestId=%s", request_id)
            request_status = "ABORTED"
            error_message = "Request has been cancelled."
            to_send["error"] = error_message
            logger.debug("Data-insight set aborted error in response")
            to_send["aborted"] = True
            logger.debug("Data-insight set aborted=True in response")
            to_send["insight"] = ""
            logger.debug("Data-insight cleared insight after abort")
        except Exception as e:
            logger.exception("Error while generating data insight thread=%s chat_seq_id=%s", thread_id, chat_seq_id)
            request_status = "ERROR"
            error_message = str(e)
            to_send["insight"] = ""
            logger.debug("Data-insight cleared insight after exception")
            to_send["error"] = error_message
            logger.debug("Data-insight set error=%s in response", error_message)
            if is_debug():
                to_send["stack"] = traceback.format_exc()
                logger.debug("Data-insight attached stack trace to response")
        finally:
            if request_id:
                request_cancellation.clear(request_id)
                logger.debug("Cleared cancellation for data-insight requestId=%s", request_id)
            if to_send.get("error") and request_status == "SUCCESS":
                request_status = "ERROR"
                error_message = str(to_send.get("error"))
            base_url = user_input.get("baseUrl") or ""
            audit_llm_usage_async(
                endpoint="/data-insight",
                username=username,
                session_cookie=session_cookie,
                base_url=base_url,
                user_query=user_question,
                token_usage=extract_token_usage_dict(to_send),
                request_status=request_status,
                error_message=error_message,
                chat_id=str(thread_id) if thread_id else None,
                chat_seq_id=str(chat_seq_id) if chat_seq_id is not None else None,
            )

        logger.info(
            "Data-insight returning response thread=%s has_insight=%s has_error=%s aborted=%s",
            thread_id,
            bool(to_send.get("insight")),
            bool(to_send.get("error")),
            to_send.get("aborted", False),
        )
        return json_response(to_send)
