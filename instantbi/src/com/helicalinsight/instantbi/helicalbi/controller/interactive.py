"""Flask /interactive routes and SSE-aware fast-mode turn.

JSON non-stream fast path uses :func:`helicalbi.interactive.fast_flow.run_fast_mode`.
SSE / ``InteractiveTurn`` reuses the same session, state, cube-merge, and payload
helpers from ``helicalbi.interactive.fast`` so the two paths stay DRY.
"""
import logging
import time
import traceback
from typing import Any, Iterator

from flask import request

from GraphBuilderManger import (
    cube_info_sql_generator_graph,
    cube_info_sql_graph,
    sql_generator_graph,
    sql_graph,
)
from helicalbi.audit.llm_usage_audit import audit_llm_usage_async
from helicalbi.common.ChatManager import add_message, get_last_n
from helicalbi.common.RequestCancellation import request_cancellation
from helicalbi.common.app_config import is_debug
from helicalbi.common.auth import bind_request_identity
from helicalbi.controller.activity import ActivityReporter
from helicalbi.controller.app_context import app
from helicalbi.controller.graph_walk import GraphWalk, is_compiled_graph, iter_sql_generation
from helicalbi.controller.helpers import (
    RequestAborted,
    ensure_not_aborted,
    extract_token_usage_dict,
    graph_invoke_config,
    json_response,
    log_endpoint_input,
    resolve_audit_status_from_response,
    resolve_request_id,
    resolve_rm_cols_in_filter,
)
from helicalbi.controller.sse import SseEventWriter, respond, wants_stream
from helicalbi.core.flows.CubeInfoFlow import CubeInfoFlow
from helicalbi.core.flows.CubeInfoSqlGenerator import CubeInfoSqlGenerator
from helicalbi.core.flows.SqlExecutor import SqlExecutor
from helicalbi.core.flows.SqlGenerator import SqlGenerator
from helicalbi.interactive.auto_mode import bind_routed_interactive_mode
from helicalbi.interactive.constants import (
    AUDIT_ENDPOINT,
    AUTO_LABEL_FAST,
    AUTO_LABEL_THINK,
    MSG_AUTO_CHOSEN,
    MSG_AUTO_STARTED,
    MSG_REQUEST_CANCELLED,
    REQUEST_STATUS_ABORTED,
    REQUEST_STATUS_ERROR,
    REQUEST_STATUS_SUCCESS,
    STAGE_AUTO,
    STATUS_DONE,
    STATUS_STARTED,
)
from helicalbi.interactive.fast.cube_merge import CubeInfoResultMerger
from helicalbi.interactive.fast.payload import FastResultPayloadBuilder
from helicalbi.interactive.fast.session import FastModelSessionLoader
from helicalbi.interactive.fast.state_builder import FastTurnStateBuilder
from helicalbi.interactive.modes import (
    apply_requested_mode,
    is_auto_mode,
    is_think_mode,
    resolve_interactive_mode_from_input,
)
from helicalbi.model.output.ChatResponse import ChatResponse
from helicalbi.sql.sql_retry import execute_sql_with_retries

logger = logging.getLogger(__name__)


def _sql_to_data_model(*args, **kwargs):
    """Compat export for tests; implementation lives in fast_flow."""
    from helicalbi.interactive.fast_flow import sql_to_data_model

    return sql_to_data_model(*args, **kwargs)


class InteractiveTurn:
    """One fast interactive-chat request. ``run()`` returns JSON; ``stream()`` yields SSE."""

    def __init__(self, payload: dict[str, Any]) -> None:
        self.payload = payload or {}
        self.user_input = self.payload["input"]
        self.user_query = self.user_input["inputString"]
        (
            self.session_cookie,
            self.username,
            self.user_id,
            _org_id,
        ) = bind_request_identity(self.payload, self.user_input)
        self.model_file_name = self.user_input["model"]["file"]
        self.location = self.user_input["model"]["dir"]
        self.thread_id = self.user_input["chatid"]
        self.chat_seq_id = self.user_input["chat_seq_id"]
        self.request_id = resolve_request_id(self.payload, self.user_input)
        self.rm_cols_in_filter = resolve_rm_cols_in_filter(self.payload, self.user_input)
        self.actual_md: dict[str, Any] = {}
        self.reporter = ActivityReporter(self.user_query)
        self.walk = GraphWalk(self.reporter, self.request_id)
        self.result: Any = {}
        self.to_send: dict[str, Any] = {}
        self.request_status = REQUEST_STATUS_SUCCESS
        self.error_message: str | None = None
        self.request_started = time.perf_counter()
        self.md_location = ""
        self.med_file_name = ""
        self.metadata_fun_ref: dict[str, Any] = {}
        self._sessions = FastModelSessionLoader()
        self._states = FastTurnStateBuilder()
        self._merger = CubeInfoResultMerger()
        self._payloads = FastResultPayloadBuilder()
        self._model_session = None

    def run(self) -> dict[str, Any]:
        self._execute()
        return self._client_payload()

    def _client_payload(self) -> dict[str, Any]:
        self.to_send = apply_requested_mode(self.to_send, self.user_input)
        return self.to_send

    def stream(self) -> Iterator[str]:
        self.reporter.enable_streaming()
        writer = self.reporter.writer
        try:
            yield writer.begin()
            try:
                for _ in self._iter_pipeline():
                    yield from self.reporter.drain()
                yield writer.complete(self._client_payload())
            except RequestAborted:
                self._mark_aborted()
                yield from self.reporter.drain()
                yield writer.error(self._client_payload())
            except Exception as error:
                self._mark_error(error)
                yield writer.error(self._client_payload())
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
        if self.request_id:
            request_cancellation.register(self.request_id)
            logger.debug(
                "Registered cancellation for interactive requestId=%s",
                self.request_id,
            )
        self.reporter.understood_intent()
        yield
        self._prepare_state()
        ensure_not_aborted(self.request_id)
        yield from self._run_intent()
        ensure_not_aborted(self.request_id)
        self.reporter.generating_sql()
        yield
        yield from self._run_sql_generator()
        self.reporter.found_sql(self.result.get("sql") if isinstance(self.result, dict) else "")
        yield
        ensure_not_aborted(self.request_id)
        self.reporter.executing_sql()
        yield
        self._run_sql_executor()
        self.reporter.received_data()
        yield
        ensure_not_aborted(self.request_id)
        self.reporter.generating_visualization()
        yield
        yield from self._run_visualization()
        self.reporter.visualization_generated()
        yield
        self._build_success_payload()

    def _prepare_state(self) -> None:
        """Load model session and build the initial LangGraph state bag."""
        session = self._sessions.load(
            session_cookie=self.session_cookie,
            model_file_name=self.model_file_name,
            location=self.location,
        )
        self._model_session = session
        self.med_file_name = session.med_file_name
        self.md_location = session.md_location
        self.actual_md = session.actual_md
        self.use_cube_info_flow = session.use_cube_info_flow
        self.cube_info_prepared = session.cube_info_prepared
        self.metadata_fun_ref = session.metadata_fun_ref

        last_chats = get_last_n(self.thread_id)
        if not last_chats:
            last_chats = self.user_input.get("last_chats", [])
        add_message(self.thread_id, self.user_query)
        logger.debug(
            "Interactive chat context thread=%s chat_seq_id=%s prior_messages=%s",
            self.thread_id,
            self.chat_seq_id,
            len(last_chats),
        )
        self.state = self._states.build(
            session=session,
            user_query=self.user_query,
            username=self.username,
            session_cookie=self.session_cookie,
            thread_id=self.thread_id,
            last_chats=last_chats,
            rm_cols_in_filter=self.rm_cols_in_filter,
        )
        self.config = graph_invoke_config(self.thread_id, self.chat_seq_id)
        logger.info(
            "Interactive request received user=%s thread=%s chat_seq_id=%s requestId=%s",
            self.username,
            self.thread_id,
            self.chat_seq_id,
            self.request_id,
        )

    def _run_intent(self) -> Iterator[None]:
        """Run CubeInfoFlow / main graph for domain, topics, and intent rephrase."""
        if self.use_cube_info_flow:
            logger.debug("Invoking cube_info flow for thread=%s", self.thread_id)
            self.result = CubeInfoFlow().process_flow(self.state)
            self.reporter.graph_node("CubeInfoFlow")
            yield
            prepared = self.cube_info_prepared
            if prepared:
                self.result = self._merger.merge(self.result, prepared)
                yield from self.walk.apply(app().main_graph, self.result, self.config)
                self.result = self.walk.state
            return
        logger.debug("Invoking main graph for thread=%s", self.thread_id)
        yield from self.walk.apply(app().main_graph, self.state, self.config)
        self.result = self.walk.state

    def _run_sql_generator(self) -> Iterator[None]:
        inner = cube_info_sql_graph if self.use_cube_info_flow else sql_graph
        wrapper = (
            cube_info_sql_generator_graph
            if self.use_cube_info_flow
            else sql_generator_graph
        )
        if self.reporter.enabled and is_compiled_graph(inner) and is_compiled_graph(wrapper):
            if self.use_cube_info_flow:
                logger.debug("Streaming cube_info SQL generator for thread=%s", self.thread_id)
                runner = CubeInfoSqlGenerator(inner)
            else:
                logger.debug("Streaming SQL generator graph for thread=%s", self.thread_id)
                runner = SqlGenerator(inner)
            yield from iter_sql_generation(runner, self.result, self.config, self.walk)
            self.result = self.walk.state
            return
        if self.use_cube_info_flow:
            logger.debug("Invoking cube_info SQL generator for thread=%s", self.thread_id)
        else:
            logger.debug("Invoking SQL generator graph for thread=%s", self.thread_id)
        yield from self.walk.apply(wrapper, self.result, self.config)
        self.result = self.walk.state

    def _run_sql_executor(self) -> None:
        logger.debug("Executing SQL for thread=%s", self.thread_id)
        executor = SqlExecutor()

        def _regenerate(work: dict[str, Any], prompt: str) -> dict[str, Any]:
            original = self.user_query
            work["query"] = prompt
            work["skip"] = False
            self.result = work
            for _ in self._run_sql_generator():
                pass
            self.result["query"] = original
            return self.result

        self.result = execute_sql_with_retries(
            self.result,
            execute=executor.process_flow,
            regenerate=_regenerate,
            original_question=self.user_query,
        )
        executor.write_insight(self.result)

    def _run_visualization(self) -> Iterator[None]:
        logger.debug("Invoking visualization graph for thread=%s", self.thread_id)
        yield from self.walk.apply(app().viz_graph, self.result, self.config)
        self.result = self.walk.state
        logger.debug("LLM processing messages: %s", self.result.get("messages", []))
        self.result["messages"] = []
        self.result["last_chats"] = []

    def _build_success_payload(self) -> None:
        """Shape the wire response and persist chat-graph memory (shared with run_fast_mode)."""
        session = self._model_session
        if session is None:
            raise RuntimeError("InteractiveTurn._prepare_state must run before payload build")
        self.to_send, self.result = self._payloads.build(
            self.result if isinstance(self.result, dict) else {},
            session=session,
            data=self.payload,
            user_query=self.user_query,
            username=self.username,
            session_cookie=self.session_cookie,
            thread_id=self.thread_id,
            chat_seq_id=self.chat_seq_id,
            request_started=self.request_started,
            rm_cols_in_filter=self.rm_cols_in_filter,
        )

    def _mark_aborted(self) -> None:
        logger.info("Interactive request aborted for requestId=%s", self.request_id)
        self.request_status = REQUEST_STATUS_ABORTED
        self.error_message = MSG_REQUEST_CANCELLED
        self.to_send["messages"] = []
        self.to_send["error"] = self.error_message
        self.to_send["aborted"] = True
        if isinstance(self.result, dict):
            chat_response = ChatResponse.from_model_state(self.result)
            self.to_send["chat_response"] = chat_response.to_interactive_client_dict()
        else:
            self.to_send["chat_response"] = {}

    def _mark_error(self, error: Exception) -> None:
        logger.exception("Error while processing interactive request")
        self.request_status = REQUEST_STATUS_ERROR
        self.error_message = str(error)
        self.to_send["messages"] = []
        self.result = self.result if isinstance(self.result, dict) else {}
        self.to_send["error"] = self.error_message
        if is_debug():
            self.to_send["stack"] = traceback.format_exc()
        if isinstance(self.result, dict):
            chat_response = ChatResponse.from_model_state(self.result)
            self.to_send["chat_response"] = chat_response.to_interactive_client_dict()
        else:
            self.to_send["chat_response"] = {}

    def _finish_request(self) -> None:
        if self.request_id:
            request_cancellation.clear(self.request_id)
            logger.debug("Cleared cancellation for interactive requestId=%s", self.request_id)
        self.request_status, self.error_message = resolve_audit_status_from_response(
            self.to_send,
            self.request_status,
            self.error_message,
        )
        audit_llm_usage_async(
            endpoint=AUDIT_ENDPOINT,
            user_id=self.user_id,
            session_cookie=self.session_cookie,
            user_query=self.user_query,
            token_usage=extract_token_usage_dict(self.to_send),
            request_status=self.request_status,
            error_message=self.error_message,
            chat_id=str(self.thread_id) if self.thread_id else None,
            chat_seq_id=str(self.chat_seq_id) if self.chat_seq_id is not None else None,
        )


class RoutedInteractiveTurn:
    """Classify Auto, then run Fast or Think. Auto progress is streamed first."""

    def __init__(self, payload: dict[str, Any]) -> None:
        self.payload = payload or {}

    def run(self) -> dict[str, Any]:
        _requested, routed = bind_routed_interactive_mode(self.payload)
        return self._inner(routed).run()

    def stream(self) -> Iterator[str]:
        writer = SseEventWriter()
        yield writer.begin()
        requested = resolve_interactive_mode_from_input(
            (self.payload.get("input") or {}), self.payload
        )
        if is_auto_mode(requested):
            yield writer.progress(STAGE_AUTO, STATUS_STARTED, MSG_AUTO_STARTED)
            _requested, routed = bind_routed_interactive_mode(self.payload)
            chosen = AUTO_LABEL_THINK if is_think_mode(routed) else AUTO_LABEL_FAST
            yield writer.progress(
                STAGE_AUTO,
                STATUS_DONE,
                MSG_AUTO_CHOSEN.format(chosen=chosen),
            )
        else:
            _requested, routed = bind_routed_interactive_mode(self.payload)
        skipping_begin = True
        for event in self._inner(routed).stream():
            if skipping_begin and str(event).startswith("event: begin"):
                skipping_begin = False
                continue
            skipping_begin = False
            yield event

    def _inner(self, routed_mode: str):
        if is_think_mode(routed_mode):
            from helicalbi.interactive.think_flow import ThinkTurn

            return ThinkTurn(self.payload)
        return InteractiveTurn(self.payload)


def register(flask_app) -> None:
    @flask_app.route("/interactive", methods=["POST"])
    def interactive():
        from helicalbi.interactive.fast_flow import run_fast_mode

        data = request.get_json()
        log_endpoint_input("/interactive", data)

        # Java proxy sends ?stream=true; honor query or body (same as other SSE routes).
        if data and wants_stream():
            return respond(RoutedInteractiveTurn(data))

        user_input = data["input"]
        user_query = user_input["inputString"]
        session_cookie, username, user_id, _org_id = bind_request_identity(data, user_input)
        model_file_name = user_input["model"]["file"]
        location = user_input["model"]["dir"]
        thread_id = user_input["chatid"]
        chat_seq_id = user_input["chat_seq_id"]
        request_id = resolve_request_id(data, user_input)
        _requested, interactive_mode = bind_routed_interactive_mode(data)
        user_input = data["input"]
        rm_cols_in_filter = resolve_rm_cols_in_filter(data, user_input)
        result: dict[str, Any] = {}
        to_send: dict[str, Any] = {}
        request_status = "SUCCESS"
        error_message: str | None = None
        request_started = time.perf_counter()
        if request_id:
            request_cancellation.register(request_id)
            logger.debug("Registered cancellation for interactive requestId=%s", request_id)

        try:
            if is_think_mode(interactive_mode):
                from helicalbi.interactive.think_flow import ThinkTurn

                turn = ThinkTurn(data)
                to_send = turn.run()
                result = to_send
                logger.info(
                    "Interactive think completed user=%s thread=%s questions=%s",
                    username,
                    thread_id,
                    len(to_send.get("asked_questions") or []),
                )
            else:
                to_send, result = run_fast_mode(
                    data=data,
                    user_query=user_query,
                    session_cookie=session_cookie,
                    username=username,
                    model_file_name=model_file_name,
                    location=location,
                    thread_id=thread_id,
                    chat_seq_id=chat_seq_id,
                    request_id=request_id,
                    request_started=request_started,
                    last_chats_fallback=user_input.get("last_chats", []),
                    rm_cols_in_filter=rm_cols_in_filter,
                )

        except RequestAborted:
            logger.info("Interactive request aborted for requestId=%s", request_id)
            request_status = "ABORTED"
            error_message = "Request has been cancelled."
            to_send["messages"] = []
            to_send["error"] = error_message
            to_send["aborted"] = True
            if is_think_mode(interactive_mode) and isinstance(result, dict) and (
                result.get("asked_questions") or result.get("chat_responses")
            ):
                to_send.setdefault("mode", "think")
                to_send.setdefault("phase", result.get("phase") or "plan")
                to_send.setdefault("asked_questions", result.get("asked_questions") or [])
                to_send.setdefault("question_history", result.get("question_history") or [])
                to_send.setdefault("final_answer", result.get("final_answer") or "")
                to_send.setdefault("cited_question_indexes", result.get("cited_question_indexes") or [])
                to_send.setdefault("chat_responses", result.get("chat_responses") or [])
                to_send.setdefault("chat_response", result.get("chat_response") or {})
            elif isinstance(result, dict):
                chat_response = ChatResponse.from_model_state(result)
                to_send["chat_response"] = chat_response.to_interactive_client_dict()
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
            if is_think_mode(interactive_mode) and (
                result.get("asked_questions") or result.get("chat_responses")
            ):
                to_send.setdefault("mode", "think")
                to_send.setdefault("phase", result.get("phase") or "plan")
                to_send.setdefault("asked_questions", result.get("asked_questions") or [])
                to_send.setdefault("question_history", result.get("question_history") or [])
                to_send.setdefault("final_answer", result.get("final_answer") or "")
                to_send.setdefault("cited_question_indexes", result.get("cited_question_indexes") or [])
                to_send.setdefault("chat_responses", result.get("chat_responses") or [])
                to_send.setdefault("chat_response", result.get("chat_response") or {})
            elif isinstance(result, dict) and result:
                try:
                    chat_response = ChatResponse.from_model_state(result)
                    to_send["chat_response"] = chat_response.to_interactive_client_dict()
                except Exception:
                    to_send["chat_response"] = result.get("chat_response") or {}
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
            audit_llm_usage_async(
                endpoint="/interactive",
                user_id=user_id,
                session_cookie=session_cookie,
                user_query=user_query,
                token_usage=extract_token_usage_dict(to_send),
                request_status=request_status,
                error_message=error_message,
                chat_id=str(thread_id) if thread_id else None,
                chat_seq_id=str(chat_seq_id) if chat_seq_id is not None else None,
            )

        return json_response(apply_requested_mode(to_send, user_input))
