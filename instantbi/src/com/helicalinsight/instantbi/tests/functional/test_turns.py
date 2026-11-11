"""Unit tests for InstantBI turn classes (buffered vs stream)."""

import json
from unittest.mock import MagicMock, patch

from helicalbi.controller.data_insight import DataInsightServiceTurn, DataInsightTurn
from helicalbi.controller.interactive import InteractiveTurn


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
            "helicalbi.interactive.fast.session.app", return_value=app_mock
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
            "helicalbi.interactive.fast.session.app", return_value=app_mock
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
            "helicalbi.interactive.fast.session.app", return_value=app_mock
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
        retry_state = sql_graph.invoke.call_args_list[1].args[0]
        assert retry_state["query"] == "Show sales"
        rewrite_prompt = retry_state["sql_retry_context"]
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

    def test_summarizes_step_insights_without_sql(self):
        payload = {
            "input": {
                "user_question": "Which employee should I pick?",
                "sessionCookie": "cookie",
                "username": "alice",
                "thread_id": "t1",
                "chat_seq_id": "1",
                "insights": [
                    {
                        "role": "original",
                        "index": 0,
                        "question": "Which employee should I pick?",
                        "insight": "Low-cost attendees are Employee 2 and 26.",
                    },
                    {
                        "role": "supporting",
                        "index": 1,
                        "question": "What is total travel cost by employee?",
                        "insight": "Employee 2 spent 9850.",
                    },
                    {
                        "role": "supporting",
                        "index": 2,
                        "question": "Meeting cancellations by employee",
                        "insight": "Cancellations cluster on a few people.",
                    },
                ],
            }
        }
        llm_response = MagicMock()
        llm_response.content = "Pick Employee 2 [1]. Cancellations are rare [2]."
        usage = MagicMock()
        usage.model_dump.return_value = {"total_tokens": 8}

        app_mock = MagicMock()
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
            side_effect=AssertionError("SQL should not be resolved for insight summaries"),
        ), patch(
            "helicalbi.controller.data_insight.resolve_request_id",
            return_value=None,
        ), patch(
            "helicalbi.controller.data_insight.add_insight"
        ):
            buffered = DataInsightTurn(payload).run()
            events = _parse_events(list(DataInsightTurn(payload).stream()))

        app_mock.execute_query.assert_not_called()
        prompt = app_mock.invoke_llm.call_args.args[1]
        assert "Which employee should I pick?" in prompt
        assert "Low-cost attendees are Employee 2 and 26." in prompt
        assert "[1] What is total travel cost by employee?" in prompt
        assert "[2] Meeting cancellations by employee" in prompt
        messages = [item["data"]["message"] for item in events if item["event"] == "progress"]
        assert messages == [
            "Writing an insight from your data…",
            "Insight is ready.",
        ]
        assert buffered["insight"] == "Pick Employee 2 [1]. Cancellations are rare [2]."

    def test_think_mode_loops_memory_questions_then_summarizes(self):
        from helicalbi.common.ChatGraphMemory import chat_graph_memory

        chat_graph_memory.clear("t-think")
        chat_graph_memory.add_node(
            "t-think",
            "1",
            {"sql": "", "user_query": "Which employee should I pick?"},
        )
        chat_graph_memory.add_node(
            "t-think",
            "1-1",
            {
                "sql": "SELECT cost FROM travel",
                "dialect": "postgres",
                "user_query": "What is total travel cost by employee?",
                "domain": ["Travel"],
                "topics": ["Cost"],
            },
        )
        chat_graph_memory.add_node(
            "t-think",
            "1-2",
            {
                "sql": "SELECT cancelled FROM meetings",
                "dialect": "postgres",
                "user_query": "Meeting cancellations by employee",
                "domain": ["Travel"],
                "topics": ["Meetings"],
            },
        )
        payload = {
            "input": {
                "inputString": "Which employee should I pick?",
                "sessionCookie": "cookie",
                "username": "alice",
                "thread_id": "t-think",
                "chatid": "t-think",
                "chat_seq_id": "1",
                "md_location": "/meta",
                "md_file_name": "metadata.json",
            }
        }

        def invoke_side_effect(_llm, prompt):
            text = str(prompt)
            response = MagicMock()
            usage = MagicMock()
            usage.model_dump.return_value = {"total_tokens": 4}
            if "Supporting insights:" in text:
                response.content = "Pick Employee 2 [1]. Cancellations are rare [2]."
            elif "What is total travel cost by employee?" in text:
                response.content = "Employee 2 spent 9850."
            else:
                response.content = "Cancellations cluster on a few people."
            return response, usage

        app_mock = MagicMock()
        app_mock.execute_query.side_effect = [
            {"status": 1, "response": {"data": [{"cost": 9850}], "metadata": []}},
            {"status": 1, "response": {"data": [{"cancelled": 3}], "metadata": []}},
        ]
        app_mock.invoke_llm.side_effect = invoke_side_effect

        try:
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
                "helicalbi.controller.data_insight.resolve_request_id",
                return_value=None,
            ), patch(
                "helicalbi.controller.data_insight.add_insight"
            ):
                buffered = DataInsightTurn(payload).run()
        finally:
            chat_graph_memory.clear("t-think")

        sqls = [call.kwargs["sql"] for call in app_mock.execute_query.call_args_list]
        assert len(sqls) == 2
        assert "travel" in sqls[0].lower()
        assert "meetings" in sqls[1].lower()
        assert app_mock.invoke_llm.call_count == 3
        summary_prompt = app_mock.invoke_llm.call_args.args[1]
        assert "Which employee should I pick?" in summary_prompt
        assert "[1] What is total travel cost by employee?" in summary_prompt
        assert "Employee 2 spent 9850." in summary_prompt
        assert "[2] Meeting cancellations by employee" in summary_prompt
        assert buffered["insight"] == "Pick Employee 2 [1]. Cancellations are rare [2]."


class TestDataInsightServiceTurn:
    def test_runs_form_data_through_fetch_data_without_sql_lookup(self):
        form_data = {
            "location": "TestFold",
            "metadataFileName": "Metadata_1.metadata",
            "columns": [{"column": "travel.travel_cost", "alias": "Travel Cost"}],
        }
        payload = {
            "input": {
                "user_question": "What were sales by region?",
                "sessionCookie": "cookie",
                "username": "alice",
                "chatid": "t-service",
                "chat_seq_id": "1",
                "last_chats": [{"question": "earlier travel question"}],
                "formData": json.dumps(form_data),
            }
        }
        llm_response = MagicMock()
        llm_response.content = "Travel cost is concentrated in a few categories."
        usage = MagicMock()
        usage.model_dump.return_value = {"total_tokens": 5}

        current_sql = (
            "SELECT SUM(travel_cost) AS travel_cost FROM travel "
            "WHERE travel_year = 2018"
        )
        call_order = []

        def generate_query(**kwargs):
            call_order.append("generateQuery")
            return {"status": 1, "response": {"query": current_sql}}

        def fetch_data(**kwargs):
            call_order.append("fetchData")
            return {
                "status": 1,
                "response": {"data": [{"Travel Cost": 120}], "metadata": []},
            }

        app_mock = MagicMock()
        app_mock.generate_query.side_effect = generate_query
        app_mock.fetch_data.side_effect = fetch_data
        app_mock.invoke_llm.return_value = (llm_response, usage)

        with patch(
            "helicalbi.controller.data_insight.app", return_value=app_mock
        ), patch(
            "helicalbi.controller.data_insight.audit_llm_usage_async"
        ) as audit, patch(
            "helicalbi.controller.data_insight.bind_request_identity",
            return_value=("cookie", "alice", 1, 1),
        ), patch(
            "helicalbi.controller.data_insight.resolve_role_profile",
            return_value={},
        ), patch(
            "helicalbi.controller.data_insight.resolve_sql_from_request",
            side_effect=AssertionError("SQL should not be resolved for formData insight"),
        ), patch(
            "helicalbi.controller.data_insight.resolve_request_id",
            return_value=None,
        ):
            buffered = DataInsightServiceTurn(payload).run()

        app_mock.execute_query.assert_not_called()
        app_mock.generate_query.assert_called_once()
        app_mock.fetch_data.assert_called_once()
        assert call_order == ["generateQuery", "fetchData"]
        assert app_mock.generate_query.call_args.kwargs["form_data"] == form_data
        assert app_mock.fetch_data.call_args.kwargs["form_data"] == form_data
        prompt = app_mock.invoke_llm.call_args.args[1]
        assert "What were sales by region?" in prompt
        assert current_sql in prompt
        assert "travel_year = 2018" in prompt
        assert "Travel Cost" in prompt
        assert "metadataFileName" not in prompt
        assert "earlier travel question" not in prompt
        assert "Chat history: []" in prompt
        assert buffered["insight"] == "Travel cost is concentrated in a few categories."
        assert audit.call_args.kwargs["endpoint"] == "/data-insight-service"
        assert audit.call_args.kwargs["chat_id"] is None
        assert audit.call_args.kwargs["chat_seq_id"] is None

    def test_saved_report_form_data_explains_think_steps_from_memory(self):
        from helicalbi.common.ChatGraphMemory import chat_graph_memory

        chat_graph_memory.clear("t-think-form")
        chat_graph_memory.add_node(
            "t-think-form",
            "1",
            {"sql": "", "user_query": "Which employee should I pick?"},
        )
        chat_graph_memory.add_node(
            "t-think-form",
            "1-1",
            {
                "sql": "SELECT cost FROM travel",
                "dialect": "postgres",
                "user_query": "What is total travel cost by employee?",
                "domain": ["Travel"],
                "topics": ["Cost"],
            },
        )
        payload = {
            "input": {
                "inputString": "Which employee should I pick?",
                "sessionCookie": "cookie",
                "username": "alice",
                "chatid": "t-think-form",
                "chat_seq_id": "1",
                "md_location": "/meta",
                "md_file_name": "metadata.json",
                "formData": json.dumps({
                    "input": "Which employee should I pick?",
                    "location": "0806",
                    "fileName": "Instant_1.instant",
                    "think": True,
                }),
            }
        }

        def invoke_side_effect(_llm, prompt):
            text = str(prompt)
            response = MagicMock()
            usage = MagicMock()
            usage.model_dump.return_value = {"total_tokens": 4}
            if "Supporting insights:" in text:
                response.content = "Employee 2 spent the most."
            else:
                response.content = "Employee 2 spent 9850."
            return response, usage

        app_mock = MagicMock()
        app_mock.execute_query.return_value = {
            "status": 1,
            "response": {"data": [{"cost": 9850}], "metadata": []},
        }
        app_mock.invoke_llm.side_effect = invoke_side_effect

        try:
            with patch(
                "helicalbi.controller.data_insight.app", return_value=app_mock
            ), patch(
                "helicalbi.controller.data_insight.audit_llm_usage_async"
            ) as audit, patch(
                "helicalbi.controller.data_insight.bind_request_identity",
                return_value=("cookie", "alice", 1, 1),
            ), patch(
                "helicalbi.controller.data_insight.resolve_role_profile",
                return_value={},
            ), patch(
                "helicalbi.controller.data_insight.resolve_request_id",
                return_value=None,
            ), patch(
                "helicalbi.controller.data_insight.add_insight"
            ):
                buffered = DataInsightServiceTurn(payload).run()
        finally:
            chat_graph_memory.clear("t-think-form")

        app_mock.fetch_data.assert_not_called()
        app_mock.generate_query.assert_not_called()
        app_mock.execute_query.assert_called_once()
        assert "travel" in app_mock.execute_query.call_args.kwargs["sql"].lower()
        assert buffered["insight"] == "Employee 2 spent the most."
        assert audit.call_args.kwargs["endpoint"] == "/data-insight"
        assert audit.call_args.kwargs["chat_id"] == "t-think-form"
        assert audit.call_args.kwargs["chat_seq_id"] == "1"

    def test_missing_form_data_returns_error(self):
        payload = {
            "input": {
                "user_question": "What were sales by region?",
                "sessionCookie": "cookie",
                "username": "alice",
                "thread_id": "t-service",
            }
        }
        app_mock = MagicMock()
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
            side_effect=AssertionError("SQL should not be resolved for formData insight"),
        ), patch(
            "helicalbi.controller.data_insight.resolve_request_id",
            return_value=None,
        ):
            buffered = DataInsightServiceTurn(payload).run()

        app_mock.generate_query.assert_not_called()
        app_mock.fetch_data.assert_not_called()
        app_mock.execute_query.assert_not_called()
        assert buffered["insight"] == ""
        assert "formData is required" in buffered["error"]

    def test_continues_when_generate_query_fails(self):
        form_data = {
            "location": "TestFold",
            "metadataFileName": "Metadata_1.metadata",
            "columns": [{"column": "travel.travel_cost", "alias": "Travel Cost"}],
        }
        payload = {
            "input": {
                "user_question": "What was total travel spend for 2015?",
                "sessionCookie": "cookie",
                "username": "alice",
                "formData": form_data,
            }
        }
        llm_response = MagicMock()
        llm_response.content = "Travel spend is about 13.67 million."
        usage = MagicMock()
        usage.model_dump.return_value = {"total_tokens": 5}

        app_mock = MagicMock()
        app_mock.generate_query.side_effect = RuntimeError("generateQuery unavailable")
        app_mock.fetch_data.return_value = {
            "status": 1,
            "response": {"data": [{"Travel Cost": 13670000}], "metadata": []},
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
            side_effect=AssertionError("SQL should not be resolved for formData insight"),
        ), patch(
            "helicalbi.controller.data_insight.resolve_request_id",
            return_value=None,
        ):
            buffered = DataInsightServiceTurn(payload).run()

        app_mock.fetch_data.assert_called_once()
        prompt = app_mock.invoke_llm.call_args.args[1]
        assert "metadataFileName" in prompt
        assert buffered["insight"] == "Travel spend is about 13.67 million."
