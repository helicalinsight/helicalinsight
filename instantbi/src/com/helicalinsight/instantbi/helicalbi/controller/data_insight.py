import json
import logging
import traceback
from typing import Any, Dict, Iterator, Optional, Tuple

from flask import request

from helicalbi.controller.activity import ActivityReporter
from helicalbi.controller.app_context import app
from helicalbi.controller.helpers import (
    RequestAborted,
    as_list,
    domain_topics_from_chat_response,
    ensure_not_aborted,
    extract_token_usage_dict,
    log_endpoint_input,
    resolve_audit_status_from_response,
    resolve_request_id,
    resolve_sql_from_request,
)
from helicalbi.audit.llm_usage_audit import audit_llm_usage_async
from helicalbi.common.ChatGraphMemory import chat_graph_memory
from helicalbi.common.ChatManager import add_insight
from helicalbi.common.RequestCancellation import request_cancellation
from helicalbi.common.app_config import is_debug
from helicalbi.common import app_config
from helicalbi.common.auth import bind_request_identity, resolve_role_profile
from helicalbi.common.configuration import llm
from helicalbi.controller.sse import respond
from helicalbi.prompt.DataInsightPrompt import data_insight_prompt_formatted
from helicalbi.prompt.ErrorPrompt import error_prompt_formatted

logger = logging.getLogger(__name__)


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


def _generate_data_insight_from_rows(
    *,
    username: str,
    user_question: str,
    sql: str,
    sample_data: list[Any],
    thread_id: str,
    profile: Optional[Dict[str, Any]] = None,
    memory: Optional[Dict[str, Any]] = None,
    last_chats: list[Any],
) -> Tuple[str, Dict[str, Any]]:
    row_count = len(sample_data)
    sql_limit = app_config.default_sql_limit
    if row_count > sql_limit:
        logger.debug(
            "Truncating data-insight sample rows from %s to %s",
            row_count,
            sql_limit,
        )
        sample_data = sample_data[:sql_limit]

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
            last_chats=last_chats,
        ),
    )
    insight = insight_msg.content
    if thread_id:
        add_insight(thread_id, insight)
        logger.debug("Stored data insight for thread=%s", thread_id)
    logger.info("Data insight generated for user=%s thread=%s", username, thread_id)
    return insight, usage.model_dump(exclude_none=True)


class DataInsightTurn:
    """One data-insight request. ``run()`` returns JSON; ``stream()`` yields SSE."""

    def __init__(self, payload: dict[str, Any]) -> None:
        self.payload = payload or {}
        self.user_input = self.payload.get("input", self.payload)
        (
            self.session_cookie,
            self.username,
            self.user_id,
            _org_id,
        ) = bind_request_identity(self.payload, self.user_input)
        self.profile = resolve_role_profile(self.payload, self.user_input)
        self.thread_id = self.user_input.get("thread_id", self.user_input.get("chatid", ""))
        self.chat_seq_id = self.user_input.get("chat_seq_id")
        self.user_question = (
            self.user_input.get("user_question")
            or self.user_input.get("userQuestion")
            or self.user_input.get("inputString")
            or ""
        )
        self.md_location = self.user_input.get("md_location", self.user_input.get("mdLocation", ""))
        self.md_file_name = self.user_input.get("md_file_name", self.user_input.get("mdFileName", ""))
        self.model = self.user_input.get("model")
        self.sql = resolve_sql_from_request(
            self.user_input, self.thread_id, self.chat_seq_id, context="data-insight"
        )
        self.last_chats = self.user_input.get("last_chats", [])
        self.memory = _resolve_memory_for_data_insight(
            self.user_input, self.thread_id, self.chat_seq_id
        )
        self.request_id = resolve_request_id(self.payload, self.user_input)
        self.reporter = ActivityReporter(self.user_question)
        self.to_send: Dict[str, Any] = {}
        self.request_status = "SUCCESS"
        self.error_message: Optional[str] = None

    def run(self) -> dict[str, Any]:
        self._execute()
        return self.to_send

    def stream(self) -> Iterator[str]:
        self.reporter.enable_streaming()
        writer = self.reporter.writer
        try:
            yield writer.begin()
            try:
                for _ in self._iter_pipeline():
                    yield from self.reporter.drain()
                yield writer.complete(self.to_send)
            except RequestAborted:
                self._mark_aborted()
                yield writer.error(self.to_send)
            except Exception as error:
                self._mark_error(error)
                yield writer.error(self.to_send)
        finally:
            self._finish_request()

    def _execute(self) -> None:
        try:
            for _ in self._iter_pipeline():
                pass
        except RequestAborted:
            self._mark_aborted()
        except Exception as error:
            self._mark_error(error)
        finally:
            self._finish_request()

    def _iter_pipeline(self) -> Iterator[None]:
        logger.info("Data-insight endpoint invoked")
        if self.request_id:
            request_cancellation.register(self.request_id)
        ensure_not_aborted(self.request_id)
        self._resolve_metadata()
        if not self.sql:
            raise RuntimeError("No SQL found for data insight request.")
        self.reporter.executing_sql()
        yield
        api_response = self._execute_query()
        ensure_not_aborted(self.request_id)
        self.reporter.received_data()
        yield
        self.reporter.generating_insight()
        yield
        self._build_insight(api_response)
        self.reporter.insight_generated()
        yield

    def _resolve_metadata(self) -> None:
        if self.model and not (self.md_location and self.md_file_name):
            helper = app().ModelLayerHelper(
                self.session_cookie, self.model["file"], self.model["dir"]
            )
            self.md_file_name = helper.get_metadata_layerfile()
            self.md_location = helper.get_metadata_layerlocation()

    def _execute_query(self) -> dict[str, Any]:
        return app().execute_query(
            session_cookie=self.session_cookie,
            md_location=self.md_location,
            md_file_name=self.md_file_name,
            sql=self.sql,
            request_id=self.request_id or str(self.thread_id),
        )

    def _build_insight(self, api_response: dict[str, Any]) -> None:
        if api_response.get("status") != 1:
            sql_error = api_response.get("response", "SQL execution failed.")
            insight_msg, usage = app().invoke_llm(
                llm,
                error_prompt_formatted.format(
                    response_string=sql_error,
                    user_query=self.user_question,
                    username=self.username,
                ),
            )
            self.to_send["insight"] = insight_msg.content
            self.to_send["sql_error"] = sql_error
            self.to_send["error"] = sql_error
            self.to_send["token_usage"] = usage.model_dump(exclude_none=True)
            return

        response_payload = api_response.get("response") or {}
        data_rows: list[Any] = []
        if isinstance(response_payload, dict):
            data_rows = response_payload.get("data") or []
        insight, token_usage = _generate_data_insight_from_rows(
            username=self.username,
            user_question=self.user_question,
            sql=self.sql,
            sample_data=data_rows,
            thread_id=self.thread_id,
            profile=self.profile,
            memory=self.memory,
            last_chats=self.last_chats,
        )
        self.to_send["insight"] = insight
        self.to_send["token_usage"] = token_usage

    def _mark_aborted(self) -> None:
        logger.info("Data insight request aborted for requestId=%s", self.request_id)
        self.request_status = "ABORTED"
        self.error_message = "Request has been cancelled."
        self.to_send["error"] = self.error_message
        self.to_send["aborted"] = True
        self.to_send["insight"] = ""

    def _mark_error(self, error: Exception) -> None:
        logger.exception(
            "Error while generating data insight thread=%s chat_seq_id=%s",
            self.thread_id,
            self.chat_seq_id,
        )
        self.request_status = "ERROR"
        self.error_message = str(error)
        self.to_send["insight"] = ""
        self.to_send["error"] = self.error_message
        if is_debug():
            self.to_send["stack"] = traceback.format_exc()

    def _finish_request(self) -> None:
        if self.request_id:
            request_cancellation.clear(self.request_id)
        self.request_status, self.error_message = resolve_audit_status_from_response(
            self.to_send,
            self.request_status,
            self.error_message,
        )
        audit_llm_usage_async(
            endpoint="/data-insight",
            user_id=self.user_id,
            session_cookie=self.session_cookie,
            user_query=self.user_question,
            token_usage=extract_token_usage_dict(self.to_send),
            request_status=self.request_status,
            error_message=self.error_message,
            chat_id=str(self.thread_id) if self.thread_id else None,
            chat_seq_id=str(self.chat_seq_id) if self.chat_seq_id is not None else None,
        )


def register(flask_app) -> None:
    @flask_app.route("/data-insight", methods=["POST"])
    def data_insight():
        """Execute SQL, sample result rows, and generate a Markdown insight."""
        data = request.get_json()
        log_endpoint_input("/data-insight", data)
        return respond(DataInsightTurn(data))
