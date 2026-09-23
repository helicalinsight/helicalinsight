import logging
import traceback
from typing import Any, Dict, Iterator

from flask import request

from GraphBuilderManger import dashboard_layout_graph
from helicalbi.controller.activity import ActivityReporter
from helicalbi.controller.graph_walk import GraphWalk
from helicalbi.controller.helpers import (
    RequestAborted,
    ensure_not_aborted,
    log_endpoint_input,
    resolve_request_id,
)
from helicalbi.common.app_config import is_debug
from helicalbi.common.auth import bind_request_identity
from helicalbi.common.RequestCancellation import request_cancellation
from helicalbi.controller.sse import respond
from helicalbi.core.dashboardflow.collect_items import collect_items

logger = logging.getLogger(__name__)


class ConvertDashboardTurn:
    """One convert-dashboard request. ``run()`` returns JSON; ``stream()`` yields SSE."""

    def __init__(self, payload: dict[str, Any]) -> None:
        self.payload = payload or {}
        self.user_input = self.payload.get("input", self.payload) if self.payload else {}
        (
            self.session_cookie,
            self.username,
            self.user_id,
            _org_id,
        ) = bind_request_identity(self.payload, self.user_input)
        self.thread_id = str(
            self.user_input.get("chatid")
            or self.user_input.get("thread_id")
            or self.user_input.get("chat_id")
            or ""
        )
        self.request_id = resolve_request_id(self.payload, self.user_input)
        self.reporter = ActivityReporter()
        self.walk = GraphWalk(self.reporter, self.request_id)
        self.to_send: Dict[str, Any] = {}
        self.items: list[Any] = []

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
            self._clear_cancellation()

    def _execute(self) -> None:
        try:
            for _ in self._iter_pipeline():
                pass
        except RequestAborted:
            self._mark_aborted()
        except Exception as error:
            self._mark_error(error)
        finally:
            self._clear_cancellation()

    def _iter_pipeline(self) -> Iterator[None]:
        if self.request_id:
            request_cancellation.register(self.request_id)
        ensure_not_aborted(self.request_id)
        self.items = collect_items(self.user_input)
        if not self.items:
            raise RuntimeError("No visualizations were provided for convert-dashboard.")
        logger.info(
            "Convert-dashboard request user=%s thread=%s items=%s",
            self.username,
            self.thread_id,
            len(self.items),
        )
        initial_state = {
            "items": self.items,
            "user_input": self.user_input,
            "username": self.username,
            "user_id": self.user_id,
            "session_cookie": self.session_cookie,
            "thread_id": self.thread_id,
            "chatid": self.thread_id,
            "domain": self.user_input.get("domain"),
            "topics": self.user_input.get("topics"),
            "user_query": str(
                self.user_input.get("inputString") or self.user_input.get("query") or ""
            ),
        }
        yield from self.walk.apply(dashboard_layout_graph, initial_state)
        result = self.walk.state
        self.reporter.dashboard_generated()
        yield
        self._store_result(result)

    def _store_result(self, result: dict[str, Any]) -> None:
        if result.get("error"):
            self.to_send["error"] = result["error"]
        self.to_send.update(
            {
                "chatid": self.thread_id,
                "title": result.get("title")
                or str(self.user_input.get("inputString") or self.user_input.get("query") or ""),
                "header": result.get("header") or {},
                "parameters": result.get("parameters") or {},
                "variables": result.get("variables") or {},
                "items": result.get("items") or self.items,
                "theme": result.get("theme") or {},
                "templateId": result.get("templateId") or "",
                "layout": result.get("layout") or [],
                "token_usage": result.get("token_usage") or {},
            }
        )
        logger.info(
            "Convert-dashboard completed user=%s items=%s",
            self.username,
            len(self.to_send.get("items") or []),
        )

    def _mark_aborted(self) -> None:
        logger.info("Convert-dashboard request aborted for requestId=%s", self.request_id)
        self.to_send["error"] = "Request has been cancelled."
        self.to_send["aborted"] = True

    def _mark_error(self, error: Exception) -> None:
        logger.exception(
            "Error while converting chat viz array to dashboard user=%s", self.username
        )
        self.to_send["error"] = str(error)
        if is_debug():
            self.to_send["stack"] = traceback.format_exc()

    def _clear_cancellation(self) -> None:
        if self.request_id:
            request_cancellation.clear(self.request_id)


def register(flask_app) -> None:
    @flask_app.route("/convert-dashboard", methods=["POST"])
    def convert_dashboard():
        logger.info("Convert-dashboard endpoint invoked")
        data = request.get_json()
        log_endpoint_input("/convert-dashboard", data)
        return respond(ConvertDashboardTurn(data or {}))
