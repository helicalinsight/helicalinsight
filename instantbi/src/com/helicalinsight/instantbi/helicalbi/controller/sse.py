"""Server-sent event helpers for InstantBI streaming endpoints."""

from __future__ import annotations

import json
from typing import Any, Iterable, Iterator

from flask import Response, request, stream_with_context

from helicalbi.controller.helpers import json_response


class SseEventWriter:
    """Format named SSE events consumed by HIStreamClient."""

    def begin(self) -> str:
        return self._format("begin", {"status": "STARTED"})

    def progress(self, stage: str, status: str, message: str) -> str:
        return self._format(
            "progress",
            {"stage": stage, "status": status, "message": message},
        )

    def complete(self, payload: dict[str, Any]) -> str:
        return self._format("complete", payload)

    def error(self, payload: dict[str, Any]) -> str:
        return self._format("error", payload)

    def _format(self, event_name: str, data: dict[str, Any]) -> str:
        return f"event: {event_name}\ndata: {json.dumps(data)}\n\n"


def wants_stream() -> bool:
    """True when the client asked for SSE via query or JSON body."""
    query_value = str(request.args.get("stream") or "").strip().lower()
    if query_value in ("1", "true", "yes"):
        return True
    payload = request.get_json(silent=True) or {}
    body_value = payload.get("stream")
    if isinstance(body_value, str):
        return body_value.strip().lower() in ("1", "true", "yes")
    return bool(body_value)


def sse_response(events: Iterable[str]) -> Response:
    # Do not set Connection: keep-alive. PEP 3333 forbids hop-by-hop headers
    # in WSGI responses, and Waitress raises AssertionError if they appear.
    # Keep the iterator a generator so Werkzeug/Waitress cannot assign
    # Content-Length and hold every activity event until the turn finishes.
    response = Response(
        stream_with_context(_iter_events(events)),
        mimetype="text/event-stream",
        headers={
            "Cache-Control": "no-cache",
            "X-Accel-Buffering": "no",
        },
    )
    response.implicit_sequence_conversion = False
    response.automatically_set_content_length = False
    return response


def respond(turn: Any) -> Any:
    """Return SSE when requested, otherwise the buffered JSON body."""
    if wants_stream():
        return sse_response(turn.stream())
    return json_response(turn.run())


def _iter_events(events: Iterable[str]) -> Iterator[str]:
    for event in events:
        if event:
            yield event
