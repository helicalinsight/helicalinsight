import logging
import time
import traceback
from typing import Any, Iterator

from flask import request
from langchain_core.messages import HumanMessage

from GraphBuilderManger import (
    cube_info_sql_generator_graph,
    cube_info_sql_graph,
    sql_generator_graph,
    sql_graph,
)
from helicalbi.audit.llm_usage_audit import audit_llm_usage_async
from helicalbi.common.ChatGraphMemory import chat_graph_memory
from helicalbi.common.ChatManager import add_message, get_last_n
from helicalbi.common.CubeInfoModel import is_cube_info_model, prepare_cube_info_model_data
from helicalbi.common.JsonToPara import has_table_column_info, prevalidate_cube_metadata
from helicalbi.common.LlmInvokeHelper import set_total_time_consumed
from helicalbi.common.RequestCancellation import request_cancellation
from helicalbi.common.app_config import is_debug
from helicalbi.common.auth import bind_request_identity, resolve_role_profile
from helicalbi.controller.activity import ActivityReporter
from helicalbi.controller.app_context import app
from helicalbi.controller.graph_walk import GraphWalk, is_compiled_graph, iter_sql_generation
from helicalbi.controller.helpers import (
    RequestAborted,
    build_chat_memory_payload,
    ensure_not_aborted,
    extract_token_usage_dict,
    graph_invoke_config,
    json_response,
    log_endpoint_input,
    resolve_audit_status_from_response,
    resolve_request_id,
    resolve_rm_cols_in_filter,
    turn_state_defaults,
)
from helicalbi.controller.sse import SseEventWriter, respond, wants_stream
from helicalbi.core.flows.CubeInfoFlow import CubeInfoFlow
from helicalbi.core.flows.CubeInfoSqlGenerator import CubeInfoSqlGenerator
from helicalbi.core.flows.SqlExecutor import SqlExecutor
from helicalbi.core.flows.SqlGenerator import SqlGenerator
from helicalbi.interactive.auto_mode import bind_routed_interactive_mode
from helicalbi.interactive.modes import (
    INTERACTIVE_MODE_FAST,
    apply_requested_mode,
    is_auto_mode,
    is_think_mode,
    resolve_interactive_mode_from_input,
)
from helicalbi.model.output.ChatResponse import ChatResponse
from helicalbi.sql.SqlSanitizer import as_sql_markdown, format_sql
from helicalbi.sql.sql_retry import execute_sql_with_retries

logger = logging.getLogger(__name__)


def _sql_to_data_model(*args, **kwargs):
    """Compat export for tests; implementation lives in fast_flow."""
    from helicalbi.interactive.fast_flow import sql_to_data_model

    return sql_to_data_model(*args, **kwargs)


class InteractiveTurn:
    """One interactive-chat request. ``run()`` returns JSON; ``stream()`` yields SSE."""

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
        self.request_status = "SUCCESS"
        self.error_message: str | None = None
        self.request_started = time.perf_counter()
        self.md_location = ""
        self.med_file_name = ""
        self.metadata_fun_ref: dict[str, Any] = {}

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
        helper = app().ModelLayerHelper(
            self.session_cookie, self.model_file_name, self.location
        )
        model_data = helper.get_model_semantic_layer()
        self.med_file_name = helper.get_metadata_layerfile()
        self.md_location = helper.get_metadata_layerlocation()
        actual_md = helper.get_metadata()
        self.actual_md = actual_md
        self.use_cube_info_flow = is_cube_info_model(model_data)
        cube_info_prepared: dict[str, Any] = {}
        if self.use_cube_info_flow:
            cube_info_prepared = prepare_cube_info_model_data(model_data, actual_md)
            cube_metadata = cube_info_prepared["cube_metadata"]
            logger.info(
                "Interactive chat using cube_info model flow file=%s location=%s tables=%s",
                self.model_file_name,
                self.location,
                len(cube_metadata),
            )
        else:
            original_cube_metadata = model_data.get("cube_metadata")
            cube_metadata = prevalidate_cube_metadata(original_cube_metadata, actual_md)
            if not has_table_column_info(original_cube_metadata) and cube_metadata:
                logger.info(
                    "Interactive chat using metadata API fallback for cube schema file=%s location=%s tables=%s",
                    self.med_file_name,
                    self.md_location,
                    len(cube_metadata),
                )
        joins = actual_md.get("joins") or []
        self.metadata_fun_ref = app().get_db_function_of_metadata(
            self.session_cookie, self.med_file_name, self.md_location
        )

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
        cube = cube_info_prepared if self.use_cube_info_flow else {}
        self.state: dict[str, Any] = {
            **turn_state_defaults(),
            "query": self.user_query,
            "table_columns": [],
            "messages": [HumanMessage(content=self.user_query, username=self.username)],
            "session_cookie": self.session_cookie,
            "last_chats": last_chats,
            "user_name": self.username,
            "thread_id": self.thread_id,
            "reduced_para": "",
            "cube_metadata": cube_metadata,
            "business_metrics": cube.get("business_metrics", []),
            "topic_mappings": cube.get("topic_mappings", []),
            "synonyms": cube.get("synonyms", []),
            "domain_context": cube.get("domain_context", ""),
            "format_strings": cube.get("format_strings", {}),
            "column_format_strings": cube.get("column_format_strings", ""),
            "ai_instructions": cube.get("ai_instructions", {}),
            "column_ai_instructions": cube.get("column_ai_instructions", ""),
            "sort_orders": cube.get("sort_orders", []),
            "column_sort_orders": cube.get("column_sort_orders", ""),
            "use_cube_info_sql_flow": self.use_cube_info_flow,
            "relationship_of_table": joins,
            "dbname": actual_md["databaseName"],
            "md_location": self.md_location,
            "md_file_name": self.med_file_name,
            "model_file_name": self.model_file_name,
            "model_location": self.location,
            "dialect": self.metadata_fun_ref["reference"],
            "rm_cols_in_filter": self.rm_cols_in_filter,
        }
        self.cube_info_prepared = cube_info_prepared
        self.config = graph_invoke_config(self.thread_id, self.chat_seq_id)
        logger.info(
            "Interactive request received user=%s thread=%s chat_seq_id=%s requestId=%s",
            self.username,
            self.thread_id,
            self.chat_seq_id,
            self.request_id,
        )

    def _run_intent(self) -> Iterator[None]:
        if self.use_cube_info_flow:
            logger.debug("Invoking cube_info flow for thread=%s", self.thread_id)
            self.result = CubeInfoFlow().process_flow(self.state)
            self.reporter.graph_node("CubeInfoFlow")
            yield
            prepared = self.cube_info_prepared
            if prepared:
                logger.info("cube info prepared")
                self.result["domain"] = prepared.get("domain") or self.result.get("domain") or []
                self.result["topics"] = prepared.get("topics") or self.result.get("topics") or []
                self.result["domain_context"] = (
                    prepared.get("domain_context") or self.result.get("domain_context") or ""
                )
                self.result["topic_mappings"] = (
                    prepared.get("topic_mappings") or self.result.get("topic_mappings") or []
                )
                self.result["synonyms"] = prepared.get("synonyms") or self.result.get("synonyms") or []
                self.result["business_metrics"] = (
                    prepared.get("business_metrics") or self.result.get("business_metrics") or []
                )
                self.result["format_strings"] = (
                    prepared.get("format_strings") or self.result.get("format_strings") or {}
                )
                self.result["column_format_strings"] = (
                    prepared.get("column_format_strings")
                    or self.result.get("column_format_strings")
                    or ""
                )
                self.result["ai_instructions"] = (
                    prepared.get("ai_instructions") or self.result.get("ai_instructions") or {}
                )
                self.result["column_ai_instructions"] = (
                    prepared.get("column_ai_instructions")
                    or self.result.get("column_ai_instructions")
                    or ""
                )
                self.result["sort_orders"] = (
                    prepared.get("sort_orders") or self.result.get("sort_orders") or []
                )
                self.result["column_sort_orders"] = (
                    prepared.get("column_sort_orders") or self.result.get("column_sort_orders") or ""
                )
                self.result["got_domain"] = True
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
        if (
            "sql_result" in self.result
            and isinstance(self.result["sql_result"], dict)
            and "data" in self.result["sql_result"]
        ):
            self.result["data"] = self.result["sql_result"]["data"]

        sql = self.result.get("sql", "")
        raw_sql = sql
        formatted_sql = format_sql(sql, dialect=self.result.get("dialect"), pretty=True)

        self.result["user_input"] = self.payload
        if sql:
            self.result["sql"] = as_sql_markdown(formatted_sql)

        if not isinstance(self.result.get("viz_form_data"), dict):
            try:
                from helicalbi.viz.viz_model_fill import _try_sql_to_form_data

                form_data = _try_sql_to_form_data(
                    formatted_sql or raw_sql,
                    session_cookie=self.session_cookie,
                    md_location=self.md_location,
                    md_file_name=self.med_file_name,
                    dialect=self.result.get("dialect") or self.metadata_fun_ref.get("reference"),
                    metadata=self.actual_md,
                    catalog=self.metadata_fun_ref,
                    rm_cols_in_filter=self.rm_cols_in_filter,
                )
                if form_data is None:
                    form_data = _sql_to_data_model(
                        formatted_sql or raw_sql,
                        location=self.md_location,
                        metadata_dir=self.md_location,
                        metadata_file_name=self.med_file_name,
                    )
                if form_data is not None:
                    self.result["viz_form_data"] = form_data
            except Exception:
                logger.exception(
                    "Interactive data_model failed; data_model omitted "
                    "thread=%s chat_seq_id=%s",
                    self.thread_id,
                    self.chat_seq_id,
                )

        set_total_time_consumed(self.result, time.perf_counter() - self.request_started)
        chat_response = ChatResponse.from_model_state(self.result)
        chat_response_dict = chat_response.to_dict()
        self.to_send["mode"] = INTERACTIVE_MODE_FAST
        self.to_send["chat_response"] = chat_response.to_interactive_client_dict()
        if chat_response_dict.get("error"):
            self.to_send["error"] = chat_response_dict["error"]

        chat_graph_memory.add_node(
            self.thread_id,
            self.chat_seq_id,
            build_chat_memory_payload(
                chat_response=chat_response_dict,
                sql=raw_sql,
                dialect=self.metadata_fun_ref.get("reference", ""),
                user_query=self.user_query,
                user_name=self.username,
                domain=self.result.get("domain") or [],
                topics=self.result.get("topics") or [],
                state=self.result if isinstance(self.result, dict) else None,
            ),
        )
        logger.info(
            "Interactive request completed user=%s thread=%s chat_seq_id=%s has_sql=%s",
            self.username,
            self.thread_id,
            self.chat_seq_id,
            bool(raw_sql),
        )

    def _mark_aborted(self) -> None:
        logger.info("Interactive request aborted for requestId=%s", self.request_id)
        self.request_status = "ABORTED"
        self.error_message = "Request has been cancelled."
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
        self.request_status = "ERROR"
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
            endpoint="/interactive",
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
            yield writer.progress(
                "auto",
                "started",
                "Checking how complex this question is…",
            )
            _requested, routed = bind_routed_interactive_mode(self.payload)
            chosen = "Think" if is_think_mode(routed) else "Fast"
            yield writer.progress(
                "auto",
                "done",
                f"This looks like a {chosen} question. Using {chosen} mode.",
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
