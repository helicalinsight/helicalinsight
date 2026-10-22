"""SSE/JSON turn adapter for interactive think mode."""
from __future__ import annotations

import logging
from typing import Any, Iterator, Optional

from helicalbi.common.RequestCancellation import request_cancellation
from helicalbi.controller.activity import ActivityReporter
from helicalbi.controller.helpers import RequestAborted, resolve_request_id
from helicalbi.interactive.modes import apply_requested_mode
from helicalbi.interactive.think.constants import (
    AUDIT_ENDPOINT,
    MSG_REQUEST_CANCELLED,
    ProgressCallback,
    REQUEST_STATUS_ABORTED,
    REQUEST_STATUS_ERROR,
    REQUEST_STATUS_SUCCESS,
    STREAM_ABORTED,
    STREAM_DONE,
    STREAM_ERROR,
)
from helicalbi.interactive.think.orchestrator import run_think_mode
from helicalbi.interactive.think.payload_builder import ThinkErrorPayloadBuilder

logger = logging.getLogger(__name__)


class ThinkTurn:
    """SSE/JSON turn for interactive think (plan + text-only execute)."""

    def __init__(self, payload: dict[str, Any]) -> None:
        self.payload = payload or {}
        self.user_input = self.payload["input"]
        self.user_query = self.user_input["inputString"]
        from helicalbi.common.auth import bind_request_identity, resolve_role_profile
        from helicalbi.common.ChatManager import add_message, get_last_n
        from helicalbi.common.LlmInvokeHelper import set_total_time_consumed
        import time

        self._time = time
        self._add_message = add_message
        self._get_last_n = get_last_n
        self._set_total_time_consumed = set_total_time_consumed
        (
            self.session_cookie,
            self.username,
            self.user_id,
            _org_id,
        ) = bind_request_identity(self.payload, self.user_input)
        self.role_profile = resolve_role_profile(self.payload, self.user_input)
        self.model_file_name = self.user_input["model"]["file"]
        self.location = self.user_input["model"]["dir"]
        self.thread_id = self.user_input["chatid"]
        self.chat_seq_id = self.user_input["chat_seq_id"]
        self.request_id = resolve_request_id(self.payload, self.user_input)
        self.reporter = ActivityReporter(self.user_query)
        self.to_send: dict[str, Any] = {}
        self.request_status = REQUEST_STATUS_SUCCESS
        self.error_message: str | None = None
        self.request_started = time.perf_counter()
        self._errors = ThinkErrorPayloadBuilder()

    def run(self) -> dict[str, Any]:
        self._execute()
        return self._client_payload()

    def _client_payload(self) -> dict[str, Any]:
        self.to_send = apply_requested_mode(self.to_send, self.user_input)
        return self.to_send

    def stream(self) -> Iterator[str]:
        import queue
        import threading

        self.reporter.enable_streaming()
        writer = self.reporter.writer
        event_queue: queue.Queue = queue.Queue()

        def on_progress(stage: str, status: str, message: str) -> None:
            event_queue.put(writer.progress(stage, status, message))

        def worker() -> None:
            try:
                self._run_think(stream=True, on_progress=on_progress)
                event_queue.put((STREAM_DONE, None))
            except RequestAborted as error:
                event_queue.put((STREAM_ABORTED, error))
            except Exception as error:
                event_queue.put((STREAM_ERROR, error))

        try:
            yield writer.begin()
            self.reporter.understood_intent()
            yield from self.reporter.drain()
            thread = threading.Thread(target=worker, daemon=True)
            thread.start()
            while True:
                item = event_queue.get()
                if isinstance(item, tuple):
                    kind, payload = item
                    if kind == STREAM_DONE:
                        break
                    if kind == STREAM_ABORTED:
                        self.request_status = REQUEST_STATUS_ABORTED
                        self.error_message = MSG_REQUEST_CANCELLED
                        self.to_send = self._errors.build(
                            error=self.error_message,
                            aborted=True,
                            asked_questions=self.to_send.get("asked_questions") or [],
                            question_history=self.to_send.get("question_history") or [],
                            final_answer=self.to_send.get("final_answer") or "",
                        )
                        yield writer.error(self._client_payload())
                        return
                    if kind == STREAM_ERROR:
                        logger.exception("Think stream failed")
                        self.request_status = REQUEST_STATUS_ERROR
                        self.error_message = str(payload)
                        self.to_send = self._errors.build(error=self.error_message)
                        yield writer.error(self._client_payload())
                        return
                else:
                    yield item
            yield from self.reporter.drain()
            yield writer.complete(self._client_payload())
        finally:
            self._finish()

    def _execute(self) -> None:
        try:
            self._run_think(stream=False)
        except RequestAborted:
            self.request_status = REQUEST_STATUS_ABORTED
            self.error_message = MSG_REQUEST_CANCELLED
            self.to_send = self._errors.build(
                error=self.error_message,
                aborted=True,
            )
        except Exception as error:
            logger.exception("Think turn failed")
            self.request_status = REQUEST_STATUS_ERROR
            self.error_message = str(error)
            self.to_send = self._errors.build(error=self.error_message)
        finally:
            self._finish()

    def _run_think(
        self,
        *,
        stream: bool,
        on_progress: Optional[ProgressCallback] = None,
    ) -> None:
        if self.request_id:
            request_cancellation.register(self.request_id)
        last_chats = self._get_last_n(self.thread_id)
        if not last_chats:
            last_chats = self.user_input.get("last_chats", [])
        self._add_message(self.thread_id, self.user_query)

        progress_cb = on_progress
        if progress_cb is None and self.reporter.enabled:

            def progress_cb(stage: str, status: str, message: str) -> None:
                self.reporter.custom(stage, status, message)

        self.to_send = run_think_mode(
            self.user_query,
            session_cookie=self.session_cookie,
            username=self.username,
            model_file_name=self.model_file_name,
            model_location=self.location,
            thread_id=str(self.thread_id),
            chat_seq_id=self.chat_seq_id,
            last_chats=last_chats,
            request_id=self.request_id,
            agent_mode=self.user_input.get("agent_mode")
            or self.user_input.get("dashboard_mode"),
            user_role=self.role_profile.get("userRole")
            or self.user_input.get("userRole")
            or [],
            user_profile=self.role_profile.get("userProfile")
            or self.user_input.get("userProfile")
            or [],
            persona_hint=self.user_input.get("persona")
            or self.user_input.get("user_persona"),
            strategy_hint=self.user_input.get("strategy")
            or self.user_input.get("strategy_id"),
            show_llm_activity_details=self.user_input.get("show_llm_activity_details")
            if "show_llm_activity_details" in self.user_input
            else self.payload.get("show_llm_activity_details"),
            on_progress=progress_cb,
        )
        self._set_total_time_consumed(
            self.to_send, self._time.perf_counter() - self.request_started
        )

    def _finish(self) -> None:
        from helicalbi.audit.llm_usage_audit import audit_llm_usage_async
        from helicalbi.controller.helpers import (
            extract_token_usage_dict,
            resolve_audit_status_from_response,
        )

        if self.request_id:
            request_cancellation.clear(self.request_id)
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
