"""Fast-mode pipeline orchestrator (JSON / non-SSE path).

Stages: load session → intent graph → SQL generate → execute+retry → viz → payload.
SSE streaming uses :class:`helicalbi.controller.interactive.InteractiveTurn`, which
reuses the same session / state / merge / form-data helpers.
"""
from __future__ import annotations

import logging
from typing import Any, Optional

from helicalbi.common.ChatManager import add_message, get_last_n
from helicalbi.controller.app_context import app
from helicalbi.controller.helpers import ensure_not_aborted, graph_invoke_config
from helicalbi.core.flows.CubeInfoFlow import CubeInfoFlow
from helicalbi.core.flows.SqlExecutor import SqlExecutor
from helicalbi.interactive.fast.cube_merge import CubeInfoResultMerger
from helicalbi.interactive.fast.payload import FastResultPayloadBuilder
from helicalbi.interactive.fast.session import FastModelSessionLoader
from helicalbi.interactive.fast.state_builder import FastTurnStateBuilder
from helicalbi.interactive.modes import INTERACTIVE_MODE_FAST
from helicalbi.sql.sql_retry import execute_sql_with_retries

logger = logging.getLogger(__name__)


class FastModeOrchestrator:
    """Run the classic single-turn interactive pipeline end-to-end."""

    def __init__(
        self,
        *,
        sessions: Optional[FastModelSessionLoader] = None,
        states: Optional[FastTurnStateBuilder] = None,
        merger: Optional[CubeInfoResultMerger] = None,
        payloads: Optional[FastResultPayloadBuilder] = None,
    ) -> None:
        self._sessions = sessions or FastModelSessionLoader()
        self._states = states or FastTurnStateBuilder()
        self._merger = merger or CubeInfoResultMerger()
        self._payloads = payloads or FastResultPayloadBuilder()

    def run(
        self,
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
        """Return ``(to_send, result_state)``."""
        session = self._sessions.load(
            session_cookie=session_cookie,
            model_file_name=model_file_name,
            location=location,
        )

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

        state = self._states.build(
            session=session,
            user_query=user_query,
            username=username,
            session_cookie=session_cookie,
            thread_id=thread_id,
            last_chats=last_chats,
            rm_cols_in_filter=rm_cols_in_filter,
        )
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

        result = self._run_intent(state, session, config)
        ensure_not_aborted(request_id)
        result = self._run_sql_generator(result, session.use_cube_info_flow, config)
        ensure_not_aborted(request_id)
        result = self._run_sql_executor(
            result,
            user_query=user_query,
            use_cube_info_flow=session.use_cube_info_flow,
            config=config,
        )
        ensure_not_aborted(request_id)
        result = self._run_visualization(result, config)

        return self._payloads.build(
            result,
            session=session,
            data=data,
            user_query=user_query,
            username=username,
            session_cookie=session_cookie,
            thread_id=thread_id,
            chat_seq_id=chat_seq_id,
            request_started=request_started,
            rm_cols_in_filter=rm_cols_in_filter,
        )

    def _run_intent(
        self,
        state: dict[str, Any],
        session,
        config: dict[str, Any],
    ) -> dict[str, Any]:
        if session.use_cube_info_flow:
            logger.debug("Invoking cube_info flow for thread=%s", state.get("thread_id"))
            result = CubeInfoFlow().process_flow(state)
            result = self._merger.merge(result, session.cube_info_prepared)
            # Still run intent rephrase; domain/topic LLM discovery is skipped via got_domain.
            return app().main_graph.invoke(result, config)
        logger.debug("Invoking main graph for thread=%s", state.get("thread_id"))
        return app().main_graph.invoke(state, config)

    @staticmethod
    def _run_sql_generator(
        result: dict[str, Any],
        use_cube_info_flow: bool,
        config: dict[str, Any],
    ) -> dict[str, Any]:
        from GraphBuilderManger import cube_info_sql_generator_graph, sql_generator_graph

        if use_cube_info_flow:
            logger.debug(
                "Invoking cube_info SQL generator for thread=%s", result.get("thread_id")
            )
            return cube_info_sql_generator_graph.invoke(result, config)
        logger.debug("Invoking SQL generator graph for thread=%s", result.get("thread_id"))
        return sql_generator_graph.invoke(result, config)

    def _run_sql_executor(
        self,
        result: dict[str, Any],
        *,
        user_query: str,
        use_cube_info_flow: bool,
        config: dict[str, Any],
    ) -> dict[str, Any]:
        logger.debug("Executing SQL for thread=%s", result.get("thread_id"))

        def _regenerate_sql(work: dict[str, Any], prompt: str) -> dict[str, Any]:
            original = str(work.get("query") or user_query)
            work["query"] = prompt
            work["skip"] = False
            work = self._run_sql_generator(work, use_cube_info_flow, config)
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
        return result

    @staticmethod
    def _run_visualization(result: dict[str, Any], config: dict[str, Any]) -> dict[str, Any]:
        logger.debug("Invoking visualization graph for thread=%s", result.get("thread_id"))
        result = app().viz_graph.invoke(result, config)
        logger.debug("LLM processing messages: %s", result.get("messages", []))
        result["messages"] = []
        result["last_chats"] = []
        return result


_ORCHESTRATOR = FastModeOrchestrator()


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
    return _ORCHESTRATOR.run(
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
        last_chats_fallback=last_chats_fallback,
        rm_cols_in_filter=rm_cols_in_filter,
    )
