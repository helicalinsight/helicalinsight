"""Walk compiled LangGraph graphs and report each finished node."""

from __future__ import annotations

from typing import Any, Iterator, Optional

from helicalbi.controller.activity import ActivityReporter
from helicalbi.controller.helpers import ensure_not_aborted


def is_compiled_graph(graph: Any) -> bool:
    """True for a real LangGraph compiled graph (not a test mock)."""
    if graph is None or not callable(getattr(graph, "stream", None)):
        return False
    module = getattr(type(graph), "__module__", "") or ""
    return "langgraph" in module


def _stream_chunks(graph: Any, state: Any, config: Any) -> Any:
    stream = getattr(graph, "stream", None)
    if not callable(stream):
        return None
    if config is None:
        try:
            chunks = stream(state, stream_mode="updates")
        except TypeError:
            return None
    else:
        try:
            chunks = stream(state, config, stream_mode="updates")
        except TypeError:
            try:
                chunks = stream(state, stream_mode="updates", config=config)
            except TypeError:
                try:
                    chunks = stream(state, stream_mode="updates")
                except TypeError:
                    return None
    if chunks is None:
        return None
    if type(chunks).__name__ in {"MagicMock", "Mock", "AsyncMock"}:
        return None
    return chunks


class GraphWalk:
    """Run a graph. Streaming yields after each node so SSE can flush."""

    def __init__(
        self,
        reporter: ActivityReporter,
        request_id: Optional[str] = None,
    ) -> None:
        self.reporter = reporter
        self.request_id = request_id
        self.state: Any = {}

    def apply(self, graph: Any, state: Any, config: Any = None) -> Iterator[None]:
        if self.reporter.enabled:
            chunks = _stream_chunks(graph, state, config)
            if chunks is not None:
                merged = dict(state) if isinstance(state, dict) else {}
                emitted = False
                for chunk in chunks:
                    ensure_not_aborted(self.request_id)
                    emitted = True
                    merged = self._consume_chunk(merged, chunk)
                    self.state = merged
                    yield
                if emitted:
                    return
        ensure_not_aborted(self.request_id)
        self.state = graph.invoke(state, config)
        yield

    def _consume_chunk(self, merged: Any, chunk: Any) -> Any:
        if isinstance(chunk, tuple) and len(chunk) == 2:
            chunk = chunk[1]
        if not isinstance(chunk, dict):
            return merged
        for node_name, update in chunk.items():
            self.reporter.graph_node(str(node_name))
            if isinstance(merged, dict) and isinstance(update, dict):
                merged.update(update)
            elif update is not None:
                merged = update
        return merged


def iter_sql_generation(
    generator: Any,
    state: Any,
    config: Any,
    walk: GraphWalk,
) -> Iterator[None]:
    """Init → inner SQL graph nodes → fold result back into outer state."""
    state = generator.init_sql_state(state)
    walk.state = state
    if state.get("skip"):
        yield
        return
    sub_state = generator._build_sql_state(state)
    for _ in walk.apply(generator._sql_graph, sub_state, config):
        yield
    walk.state = generator._apply_sql_result(state, walk.state)
