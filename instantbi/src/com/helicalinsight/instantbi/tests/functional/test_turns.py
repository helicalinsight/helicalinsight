"""Unit tests for InstantBI turn classes (buffered vs stream)."""

import json
from unittest.mock import MagicMock, patch

from helicalbi.controller.convert_dashboard import ConvertDashboardTurn
from helicalbi.controller.data_insight import DataInsightTurn
from helicalbi.controller.interactive import InteractiveTurn
from helicalbi.controller.sse import SseEventWriter


def _parse_events(chunks):
    text = "".join(chunks)
    events = []
    for block in text.split("\n\n"):
        if not block.strip():
            continue
        event_name = None
        data_lines = []
        for line in block.splitlines():
            if line.startswith("event:"):
                event_name = line.split(":", 1)[1].strip()
            elif line.startswith("data:"):
                data_lines.append(line.split(":", 1)[1].strip())
        events.append({"event": event_name, "data": json.loads("\n".join(data_lines))})
    return events


class TestInteractiveTurn:
    def test_stream_complete_matches_run_payload(self):
        payload = {
            "input": {
                "inputString": "Show sales",
                "sessionCookie": "cookie",
                "username": "alice",
                "model": {"file": "model.json", "dir": "/models"},
                "chatid": "turn-chat",
                "chat_seq_id": "1",
            }
        }
        helper = MagicMock()
        helper.get_model_semantic_layer.return_value = {
            "cube_metadata": [{"database_table": "t"}]
        }
        helper.get_metadata_layerfile.return_value = "metadata.json"
        helper.get_metadata_layerlocation.return_value = "/meta"
        helper.get_metadata.return_value = {"joins": [], "databaseName": "testdb"}

        app_mock = MagicMock()
        app_mock.ModelLayerHelper.return_value = helper
        app_mock.get_db_function_of_metadata.return_value = {"reference": "postgres"}
        app_mock.main_graph.invoke.side_effect = lambda state, config=None: state
        app_mock.viz_graph.invoke.side_effect = lambda state, config=None: {
            **state,
            "sql": "select a from t",
            "sql_result": {"data": [{"a": 1}], "metadata": [{}]},
            "dialect": "postgres",
            "messages": [],
            "flow": [],
        }

        with patch(
            "helicalbi.controller.interactive.app", return_value=app_mock
        ), patch(
            "helicalbi.controller.interactive.sql_generator_graph"
        ) as sql_graph, patch(
            "helicalbi.controller.interactive.SqlExecutor.process_flow",
            side_effect=lambda state: state,
        ), patch(
            "helicalbi.controller.interactive.SqlExecutor.write_insight",
            side_effect=lambda state: state,
        ), patch(
            "helicalbi.controller.interactive.audit_llm_usage_async"
        ), patch(
            "helicalbi.controller.interactive.bind_request_identity",
            return_value=("cookie", "alice", 1, 1),
        ), patch(
            "helicalbi.controller.interactive.resolve_request_id",
            return_value=None,
        ):
            sql_graph.invoke.side_effect = lambda state, config=None: state
            buffered = InteractiveTurn(payload).run()
            events = _parse_events(list(InteractiveTurn(payload).stream()))

        assert events[0]["event"] == "begin"
        messages = [item["data"]["message"] for item in events if item["event"] == "progress"]
        assert messages[0].startswith("Hi, you want to understand")
        assert "Show sales" in messages[0]
        assert messages[-1] == "Found it. Chart is ready."
        assert any("query the database" in item for item in messages)
        assert any("Executing your query" in item for item in messages)
        assert any("Got your data." in item for item in messages)
        complete = [item for item in events if item["event"] == "complete"][0]
        assert complete["data"]["chat_response"]["sql"] == buffered["chat_response"]["sql"]

    def test_stream_emits_activity_for_each_graph_node(self):
        payload = {
            "input": {
                "inputString": "Show sales",
                "sessionCookie": "cookie",
                "username": "alice",
                "model": {"file": "model.json", "dir": "/models"},
                "chatid": "turn-chat-nodes",
                "chat_seq_id": "1",
            }
        }
        helper = MagicMock()
        helper.get_model_semantic_layer.return_value = {
            "cube_metadata": [{"database_table": "t"}]
        }
        helper.get_metadata_layerfile.return_value = "metadata.json"
        helper.get_metadata_layerlocation.return_value = "/meta"
        helper.get_metadata.return_value = {"joins": [], "databaseName": "testdb"}

        def intent_stream(state, config=None, stream_mode="updates"):
            next_state = dict(state)
            yield {"UpdateIntentRephrase": next_state}
            yield {"FindDomainAndTopics": next_state}

        def viz_stream(state, config=None, stream_mode="updates"):
            next_state = {
                **state,
                "sql": "select a from t",
                "sql_result": {"data": [{"a": 1}], "metadata": [{}]},
                "dialect": "postgres",
                "messages": [],
                "flow": [],
            }
            yield {"VizModelFiller": next_state}
            yield {"VizPropertiesPolish": next_state}

        app_mock = MagicMock()
        app_mock.ModelLayerHelper.return_value = helper
        app_mock.get_db_function_of_metadata.return_value = {"reference": "postgres"}
        app_mock.main_graph.invoke.side_effect = lambda state, config=None: state
        app_mock.main_graph.stream.side_effect = intent_stream
        app_mock.viz_graph.invoke.side_effect = lambda state, config=None: {
            **state,
            "sql": "select a from t",
            "sql_result": {"data": [{"a": 1}], "metadata": [{}]},
            "dialect": "postgres",
            "messages": [],
            "flow": [],
        }
        app_mock.viz_graph.stream.side_effect = viz_stream

        with patch(
            "helicalbi.controller.interactive.app", return_value=app_mock
        ), patch(
            "helicalbi.controller.interactive.sql_generator_graph"
        ) as sql_graph, patch(
            "helicalbi.controller.interactive.SqlExecutor.process_flow",
            side_effect=lambda state: state,
        ), patch(
            "helicalbi.controller.interactive.SqlExecutor.write_insight",
            side_effect=lambda state: state,
        ), patch(
            "helicalbi.controller.interactive.audit_llm_usage_async"
        ), patch(
            "helicalbi.controller.interactive.bind_request_identity",
            return_value=("cookie", "alice", 1, 1),
        ), patch(
            "helicalbi.controller.interactive.resolve_request_id",
            return_value=None,
        ):
            sql_graph.invoke.side_effect = lambda state, config=None: state
            events = _parse_events(list(InteractiveTurn(payload).stream()))

        messages = [item["data"]["message"] for item in events if item["event"] == "progress"]
        assert "Restating your question so I can work with it…" in messages
        assert "I know which area of the data this is about." in messages
        assert "Matching your data to a chart type…" in messages
        assert "Tuning the chart so it reads clearly…" in messages
        assert messages[-1] == "Found it. Chart is ready."

    def test_sql_executor_retries_keep_original_question(self):
        payload = {
            "input": {
                "inputString": "Show sales",
                "sessionCookie": "cookie",
                "username": "alice",
                "model": {"file": "model.json", "dir": "/models"},
                "chatid": "turn-chat-retry",
                "chat_seq_id": "1",
            }
        }
        helper = MagicMock()
        helper.get_model_semantic_layer.return_value = {
            "cube_metadata": [{"database_table": "t"}]
        }
        helper.get_metadata_layerfile.return_value = "metadata.json"
        helper.get_metadata_layerlocation.return_value = "/meta"
        helper.get_metadata.return_value = {"joins": [], "databaseName": "testdb"}

        app_mock = MagicMock()
        app_mock.ModelLayerHelper.return_value = helper
        app_mock.get_db_function_of_metadata.return_value = {"reference": "postgres"}
        app_mock.main_graph.invoke.side_effect = lambda state, config=None: state
        app_mock.viz_graph.invoke.side_effect = lambda state, config=None: {
            **state,
            "sql": state.get("sql") or "select a from t",
            "sql_result": {"data": [{"a": 1}], "metadata": [{}]},
            "dialect": "postgres",
            "messages": [],
            "flow": [],
        }

        exec_calls = {"n": 0}
        seen_queries = []

        def fake_process(state):
            exec_calls["n"] += 1
            seen_queries.append(state.get("query"))
            if exec_calls["n"] < 2:
                state["sql_error"] = "Unknown column x"
                state["skip"] = True
                return state
            state["sql_error"] = "Not Generated"
            state["sql_result"] = {"data": [{"a": 1}], "metadata": [{}]}
            state["skip"] = False
            return state

        with patch(
            "helicalbi.controller.interactive.app", return_value=app_mock
        ), patch(
            "helicalbi.controller.interactive.sql_generator_graph"
        ) as sql_graph, patch(
            "helicalbi.controller.interactive.SqlExecutor.process_flow",
            side_effect=fake_process,
        ), patch(
            "helicalbi.controller.interactive.SqlExecutor.write_insight",
            side_effect=lambda state: state,
        ), patch(
            "helicalbi.controller.interactive.audit_llm_usage_async"
        ), patch(
            "helicalbi.controller.interactive.bind_request_identity",
            return_value=("cookie", "alice", 1, 1),
        ), patch(
            "helicalbi.controller.interactive.resolve_request_id",
            return_value=None,
        ):
            sql_graph.invoke.side_effect = lambda state, config=None: {
                **state,
                "sql": "select a from t",
            }
            result = InteractiveTurn(payload).run()

        assert exec_calls["n"] == 2
        assert sql_graph.invoke.call_count == 2
        assert seen_queries == ["Show sales", "Show sales"]
        rewrite_prompt = sql_graph.invoke.call_args_list[1].args[0]["query"]
        assert "Show sales" in rewrite_prompt
        assert "Previous SQL that failed" in rewrite_prompt
        assert "Unknown column x" in rewrite_prompt
        raw_sql = result["chat_response"]["sql"]["raw_sql"]
        assert "SELECT" in raw_sql
        assert '"a"' in raw_sql
        assert '"t"' in raw_sql


class TestDataInsightTurn:
    def test_stream_complete_matches_run_payload(self):
        payload = {
            "input": {
                "sql": "SELECT 1",
                "user_question": "q",
                "sessionCookie": "cookie",
                "username": "alice",
                "md_location": "/meta",
                "md_file_name": "metadata.json",
                "thread_id": "t1",
            }
        }
        llm_response = MagicMock()
        llm_response.content = "## Insight"
        usage = MagicMock()
        usage.model_dump.return_value = {"total_tokens": 3}

        app_mock = MagicMock()
        app_mock.execute_query.return_value = {
            "status": 1,
            "response": {"data": [{"n": 1}], "metadata": []},
        }
        app_mock.invoke_llm.return_value = (llm_response, usage)

        with patch(
            "helicalbi.controller.data_insight.app", return_value=app_mock
        ), patch(
            "helicalbi.controller.data_insight.audit_llm_usage_async"
        ), patch(
            "helicalbi.controller.data_insight.bind_request_identity",
            return_value=("cookie", "alice", 1, 1),
        ), patch(
            "helicalbi.controller.data_insight.resolve_role_profile",
            return_value={},
        ), patch(
            "helicalbi.controller.data_insight.resolve_sql_from_request",
            return_value="SELECT 1",
        ), patch(
            "helicalbi.controller.data_insight.resolve_request_id",
            return_value=None,
        ):
            buffered = DataInsightTurn(payload).run()
            events = _parse_events(list(DataInsightTurn(payload).stream()))

        messages = [item["data"]["message"] for item in events if item["event"] == "progress"]
        assert messages == [
            "Executing your query…",
            "Got your data.",
            "Writing an insight from your data…",
            "Insight is ready.",
        ]
        complete = [item for item in events if item["event"] == "complete"][0]
        assert complete["data"] == buffered
        assert buffered["insight"] == "## Insight"


class TestConvertDashboardTurn:
    def test_stream_maps_graph_nodes_to_activity(self):
        payload = {
            "input": {
                "chatid": "c1",
                "sessionCookie": "cookie",
                "username": "alice",
                "items": [{"id": "seq-1", "sql": "SELECT 1"}],
            }
        }
        graph = MagicMock()
        graph.invoke.return_value = {
            "items": [{"id": "seq-1"}],
            "theme": {"color": "#000"},
            "templateId": "t",
            "layout": [],
        }

        def stream_updates(state, stream_mode="updates"):
            yield {"CollectContext": {"items": [{"id": "seq-1"}]}}
            yield {"PlanSummary": {"theme": {"color": "#000"}}}
            yield {"SelectFilters": {}}
            yield {"MakeLayout": {"layout": [], "templateId": "t"}}

        graph.stream.side_effect = stream_updates

        with patch(
            "helicalbi.controller.convert_dashboard.dashboard_layout_graph", graph
        ), patch(
            "helicalbi.controller.convert_dashboard.bind_request_identity",
            return_value=("cookie", "alice", 1, 1),
        ), patch(
            "helicalbi.controller.convert_dashboard.collect_items",
            return_value=[{"id": "seq-1", "sql": "SELECT 1"}],
        ), patch(
            "helicalbi.controller.convert_dashboard.resolve_request_id",
            return_value=None,
        ):
            buffered = ConvertDashboardTurn(payload).run()
            events = _parse_events(list(ConvertDashboardTurn(payload).stream()))

        messages = [item["data"]["message"] for item in events if item["event"] == "progress"]
        assert messages == [
            "Collecting your visualizations…",
            "Planning the dashboard…",
            "Selecting filters…",
            "Generating the layout…",
            "Dashboard is ready.",
        ]
        complete = [item for item in events if item["event"] == "complete"][0]
        assert complete["data"]["items"] == buffered["items"]
        assert SseEventWriter().begin().startswith("event: begin")
