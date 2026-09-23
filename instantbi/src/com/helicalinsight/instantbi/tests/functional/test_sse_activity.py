"""Unit tests for SSE formatting and activity reporting."""

import json

from flask import Flask

from helicalbi.controller.activity import ActivityReporter
from helicalbi.controller.sse import SseEventWriter, sse_response


def _parse_event(raw: str) -> dict:
    event = None
    data_lines = []
    for line in raw.strip().splitlines():
        if line.startswith("event:"):
            event = line.split(":", 1)[1].strip()
        elif line.startswith("data:"):
            data_lines.append(line.split(":", 1)[1].strip())
    return {"event": event, "data": json.loads("\n".join(data_lines))}


class TestSseEventWriter:
    def test_begin_event(self):
        parsed = _parse_event(SseEventWriter().begin())
        assert parsed["event"] == "begin"
        assert parsed["data"]["status"] == "STARTED"

    def test_progress_event(self):
        parsed = _parse_event(
            SseEventWriter().progress("sql", "started", "Generating SQL...")
        )
        assert parsed["event"] == "progress"
        assert parsed["data"]["stage"] == "sql"
        assert parsed["data"]["message"] == "Generating SQL..."

    def test_complete_and_error_events(self):
        writer = SseEventWriter()
        complete = _parse_event(writer.complete({"chat_response": {}}))
        error = _parse_event(writer.error({"aborted": True, "error": "cancelled"}))
        assert complete["event"] == "complete"
        assert complete["data"]["chat_response"] == {}
        assert error["event"] == "error"
        assert error["data"]["aborted"] is True


class TestSseResponse:
    def test_omits_hop_by_hop_connection_header(self):
        app = Flask(__name__)
        with app.test_request_context():
            response = sse_response(["event: begin\ndata: {}\n\n"])
        header_names = {name.lower() for name in response.headers.keys()}
        assert "connection" not in header_names
        assert response.mimetype == "text/event-stream"
        assert response.headers["Cache-Control"] == "no-cache"

    def test_keeps_generator_so_activity_is_not_buffered(self):
        app = Flask(__name__)

        def delayed_events():
            yield "event: begin\ndata: {\"status\":\"STARTED\"}\n\n"
            yield "event: progress\ndata: {\"message\":\"Generating SQL...\"}\n\n"

        with app.test_request_context():
            response = sse_response(delayed_events())
        assert response.is_streamed
        assert not response.is_sequence
        assert response.automatically_set_content_length is False
        assert "Content-Length" not in response.headers


class TestActivityReporter:
    def test_silent_until_streaming_enabled(self):
        reporter = ActivityReporter("sales by region")
        reporter.understood_intent()
        reporter.generating_sql()
        assert reporter.drain() == []

    def test_named_methods_emit_expected_messages(self):
        reporter = ActivityReporter("sales by region")
        reporter.enable_streaming()
        reporter.understood_intent()
        reporter.generating_sql()
        reporter.found_sql("SELECT region, sales FROM t")
        reporter.executing_sql()
        reporter.received_data()
        reporter.generating_visualization()
        reporter.visualization_generated()
        events = [_parse_event(raw) for raw in reporter.drain()]
        messages = [item["data"]["message"] for item in events]
        stages = [item["data"]["stage"] for item in events]
        assert messages[0].startswith("Hi, you want to understand")
        assert "sales by region" in messages[0]
        assert messages[1] == "I can query the database for this."
        assert messages[2] == "Found this query is suitable: SELECT region, sales FROM t"
        assert messages[3] == "Executing your query…"
        assert messages[4] == "Got your data."
        assert messages[5] == "Finding which visualization suits this best…"
        assert messages[6] == "Found it. Chart is ready."
        assert stages[-1] == "viz"
        assert reporter.drain() == []

    def test_found_sql_without_query_stays_descriptive(self):
        reporter = ActivityReporter()
        reporter.enable_streaming()
        reporter.found_sql("")
        parsed = _parse_event(reporter.drain()[0])
        assert parsed["data"]["message"] == "Found this query is suitable."

    def test_dashboard_and_insight_methods(self):
        reporter = ActivityReporter()
        reporter.enable_streaming()
        reporter.collecting_visualizations()
        reporter.planning_dashboard()
        reporter.selecting_filters()
        reporter.generating_layout()
        reporter.generating_insight()
        reporter.insight_generated()
        reporter.dashboard_generated()
        parsed = [_parse_event(raw) for raw in reporter.drain()]
        messages = [item["data"]["message"] for item in parsed]
        stages = [item["data"]["stage"] for item in parsed]
        assert "Collecting your visualizations…" in messages
        assert "Planning the dashboard…" in messages
        assert "Selecting filters…" in messages
        assert "Generating the layout…" in messages
        assert "Writing an insight from your data…" in messages
        assert "Insight is ready." in messages
        assert "Dashboard is ready." in messages
        assert "insight" in stages
        assert "assemble" in stages

    def test_graph_node_uses_descriptive_copy_and_skips_unknown(self):
        reporter = ActivityReporter()
        reporter.enable_streaming()
        reporter.graph_node("FindTablesFromTopics")
        reporter.graph_node("FinalSqlGen")
        reporter.graph_node("NotARealNode")
        messages = [_parse_event(raw)["data"]["message"] for raw in reporter.drain()]
        assert messages == [
            "Looking up the tables that match this question…",
            "Writing the SQL…",
        ]


class TestGraphWalk:
    def test_stream_emits_activity_after_each_node(self):
        from helicalbi.controller.graph_walk import GraphWalk

        class FakeGraph:
            def invoke(self, state, config=None):
                return {**state, "sql": "SELECT 1"}

            def stream(self, state, config=None, stream_mode="updates"):
                yield {"FindTablesFromTopics": {"required_tables": "t"}}
                yield {"FinalSqlGen": {"final_sql": "SELECT 1"}}

        reporter = ActivityReporter("sales")
        reporter.enable_streaming()
        walk = GraphWalk(reporter, None)
        yields = list(walk.apply(FakeGraph(), {"query": "sales"}))
        assert len(yields) == 2
        assert walk.state["final_sql"] == "SELECT 1"
        messages = [_parse_event(raw)["data"]["message"] for raw in reporter.drain()]
        assert messages[0] == "Looking up the tables that match this question…"
        assert messages[1] == "Writing the SQL…"

    def test_falls_back_to_invoke_when_stream_is_a_mock(self):
        from unittest.mock import MagicMock

        from helicalbi.controller.graph_walk import GraphWalk, is_compiled_graph

        graph = MagicMock()
        graph.invoke.return_value = {"sql": "SELECT 1"}
        reporter = ActivityReporter()
        reporter.enable_streaming()
        walk = GraphWalk(reporter, None)
        list(walk.apply(graph, {"query": "q"}))
        assert walk.state == {"sql": "SELECT 1"}
        graph.invoke.assert_called_once()
        assert is_compiled_graph(graph) is False


class TestActivityCopy:
    def test_quote_question_shortens_long_text(self):
        from helicalbi.controller.activity import quote_question

        quoted = quote_question("a " * 60)
        assert quoted.endswith("…")
        assert len(quoted) <= 80

    def test_sql_snippet_strips_markdown_and_truncates(self):
        from helicalbi.controller.activity import sql_snippet

        snippet = sql_snippet("```sql\nSELECT 1\n```")
        assert snippet == "SELECT 1"
        long_sql = "SELECT " + ("x, " * 80) + "y FROM t"
        truncated = sql_snippet(long_sql)
        assert truncated.endswith("…")
        assert len(truncated) <= 140
