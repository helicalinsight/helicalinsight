"""Integration tests for the Flask endpoints defined in ``app.py``.

Each test exercises a real ``Flask`` route via the test client. External
collaborators (HTTP calls, LangGraph invocations, helpers) are patched so
that the routing, request parsing, and response shaping behaviour is
verified end-to-end without hitting LLMs or back-end services.
"""
import json
import logging
from pathlib import Path
from unittest.mock import MagicMock, patch

import pytest


pytestmark = pytest.mark.integration


# ---------------------------------------------------------------------------
# Health / root
# ---------------------------------------------------------------------------
class TestRoot:
    def test_root_returns_banner(self, flask_client):
        resp = flask_client.get("/")
        assert resp.status_code == 200
        assert b"AI and HI bridge connector" in resp.data


# ---------------------------------------------------------------------------
# /suggestDomain
# ---------------------------------------------------------------------------
class TestSuggestDomain:
    def test_returns_static_sales_operation(self, app_module, flask_client, session_auth):
        helper_mock = MagicMock()
        helper_mock.get_agent_semantic_layer.return_value = {"domain": []}

        with patch.object(app_module, "AgentLayerHelper", return_value=helper_mock):
            resp = flask_client.post(
                "/suggestDomain",
                json={
                    **session_auth,
                    "agent": {"file": "a.json", "dir": "/agents"},
                },
            )

        assert resp.status_code == 200
        assert resp.data.decode() == "Sales Operation"


# ---------------------------------------------------------------------------
# /topNQuestion
# ---------------------------------------------------------------------------
class TestTopNQuestion:
    def test_returns_kpis_joined_with_newlines(
        self, app_module, flask_client, session_auth
    ):
        helper_mock = MagicMock()
        helper_mock.get_agent_semantic_layer.return_value = {"domain": []}

        kpi_mock = MagicMock()
        kpi_mock.user_query = "Suggest KPIs"
        kpi_mock.top_kpis.return_value = (["KPI A", "KPI B", "KPI C"], {"total_tokens": 0})

        with patch.object(app_module, "AgentLayerHelper", return_value=helper_mock), patch.object(
            app_module, "KpiProvider", return_value=kpi_mock
        ):
            resp = flask_client.post(
                "/topNQuestion",
                json={
                    **session_auth,
                    "domain": "Sales Operation",
                    "topN": 3,
                    "agent": {"file": "a.json", "dir": "/agents"},
                },
            )

        assert resp.status_code == 200
        assert resp.data.decode() == "KPI A\nKPI B\nKPI C"

    def test_returns_error_message_array_on_failure(
        self, app_module, flask_client, session_auth
    ):
        with patch.object(
            app_module,
            "AgentLayerHelper",
            side_effect=RuntimeError("Failed to fetch agent."),
        ):
            resp = flask_client.post(
                "/topNQuestion",
                json={
                    **session_auth,
                    "domain": "Sales Operation",
                    "topN": 3,
                    "agent": {"file": "a.json", "dir": "/agents"},
                },
            )

        assert resp.status_code == 200
        body = json.loads(resp.data)
        assert body["error"] == ["Failed to fetch agent."]


# ---------------------------------------------------------------------------
# /interactive
# ---------------------------------------------------------------------------
class TestInteractive:
    def _build_payload(self, session_auth, query="Show me sales"):
        return {
            "input": {
                **session_auth,
                "inputString": query,
                "agent": {"file": "agent.json", "dir": "/agents"},
            }
        }

    def test_happy_path_returns_json_with_sql_and_citations(
        self, app_module, flask_client, session_auth, patch_graphs
    ):
        main_mock, viz_mock = patch_graphs

        viz_mock.invoke.return_value = {
            "sql": "select a from t",
            "flow": ["[ABC123XYZ]", "no-match"],
            "messages": [],
            "sql_result": {"data": [{"a": 1}, {"a": 2}], "metadata": [{}]},
        }

        helper_mock = MagicMock()
        helper_mock.get_agent_semantic_layer.return_value = {
            "cube_metadata": [{"database_table": "t"}]
        }
        helper_mock.get_metadata_layerfile.return_value = "metadata.json"
        helper_mock.get_metadata_layerlocation.return_value = "/meta"

        with patch.object(app_module, "AgentLayerHelper", return_value=helper_mock), patch.object(
            app_module, "get_json_data_metadata", return_value={"joins": []}
        ):
            resp = flask_client.post(
                "/interactive", json=self._build_payload(session_auth)
            )

        assert resp.status_code == 200
        body = json.loads(resp.data)

        # SQL formatted with the markdown code fence.
        assert body["sql"].startswith("```sql")
        # Data carried from sql_result -> top-level data.
        assert body["data"] == [{"a": 1}, {"a": 2}]
        # Citation regex extracts the inner of bracketed identifiers.
        assert "BC123XY" in body["citation"][0]
        assert body["cube_metadata"] == []
        # Graphs were invoked.
        assert main_mock.invoke.called
        assert viz_mock.invoke.called

    def test_exception_path_returns_error_payload(
        self, app_module, flask_client, session_auth, patch_graphs
    ):
        main_mock, _ = patch_graphs
        # The /interactive endpoint wraps graph invocation in try/except,
        # so a failure in main_graph.invoke must be turned into the JSON
        # error payload instead of a 500 response.
        main_mock.invoke.side_effect = RuntimeError("boom")

        helper_mock = MagicMock()
        helper_mock.get_agent_semantic_layer.return_value = {
            "cube_metadata": [{"database_table": "t"}]
        }
        helper_mock.get_metadata_layerfile.return_value = "metadata.json"
        helper_mock.get_metadata_layerlocation.return_value = "/meta"

        with patch.object(
            app_module, "AgentLayerHelper", return_value=helper_mock
        ), patch.object(
            app_module, "get_json_data_metadata", return_value={"joins": []}
        ):
            resp = flask_client.post(
                "/interactive", json=self._build_payload(session_auth)
            )

        assert resp.status_code == 200
        body = json.loads(resp.data)
        assert body["error"] == "boom"
        assert "RuntimeError" in body["stack"]
        assert body["messages"] == []

    def test_empty_agent_structure_uses_metadata_fallback_and_logs_each_layer(
        self, app_module, flask_client, session_auth, patch_graphs, caplog
    ):
        _, viz_mock = patch_graphs
        metadata_fixture = (
            Path(__file__).resolve().parents[1] / "fixtures" / "sample_metadata_response.json"
        )
        metadata_blob = json.loads(metadata_fixture.read_text(encoding="utf-8"))
        metadata_payload = metadata_blob.get("response", metadata_blob)
        metadata_payload.setdefault("joins", [])
        metadata_payload.setdefault("databaseName", "sampletraveldata.public")

        helper_mock = MagicMock()
        # Simulate an empty semantic structure coming from the agent file.
        helper_mock.get_agent_semantic_layer.return_value = {}
        helper_mock.get_metadata_layerfile.return_value = "metadata.json"
        helper_mock.get_metadata_layerlocation.return_value = "/meta"

        sql_generator_mock = MagicMock()
        sql_generator_mock.invoke.return_value = {
            "query": "Top 5 travel cost by travel type and travel medium",
            "sql": "",
            "sqlAgent": {},
            "sql_result": {},
            "messages": [],
            "flow": [],
            "dialect": "postgres",
        }

        with caplog.at_level(logging.DEBUG, logger="bl.interactive"), patch.object(
            app_module, "AgentLayerHelper", return_value=helper_mock
        ), patch.object(
            app_module, "get_json_data_metadata", return_value=metadata_payload
        ), patch.object(
            app_module, "get_db_function_of_metadata", return_value={"reference": "postgres"}
        ), patch(
            "bl.interactive.sql_generator_graph", sql_generator_mock
        ), patch(
            "bl.interactive.SqlExecutor.process_flow",
            return_value={
                "query": "Top 5 travel cost by travel type and travel medium",
                "sql": "",
                "sqlAgent": {},
                "sql_result": {},
                "messages": [],
                "flow": [],
                "dialect": "postgres",
            },
        ):
            viz_mock.invoke.return_value = {
                "query": "Top 5 travel cost by travel type and travel medium",
                "sql": "",
                "sqlAgent": {},
                "sql_result": {},
                "messages": [],
                "flow": [],
                "dialect": "postgres",
            }
            payload = self._build_payload(
                session_auth, query="Top 5 travel cost by travel type and travel medium"
            )
            payload["input"]["chatid"] = "chat-empty-1"
            payload["input"]["chat_seq_id"] = "1"
            resp = flask_client.post(
                "/interactive",
                json=payload,
            )

        assert resp.status_code == 200
        body = json.loads(resp.data)
        assert "error" not in body
        assert "chat_response" in body
        assert body["chat_response"]["sql"]["raw_sql"] == ""
        assert body["chat_response"]["sql"]["required_domain"] == []
        assert body["chat_response"]["sql"]["required_topic"] == []
        assert body["chat_response"]["data"] == []
        assert body["chat_response"]["metadata"] == []

        log_text = caplog.text
        assert "Invoking main graph" in log_text
        assert "Invoking SQL generator graph" in log_text
        assert "Executing SQL" in log_text
        assert "Invoking visualization graph" in log_text


# ---------------------------------------------------------------------------
# /clear-api-cache
# ---------------------------------------------------------------------------
class TestClearApiCache:
    def test_clears_cached_api_responses(self, app_module, flask_client, session_cookie):
        from helicalbi.api.ApiCallCache import set as cache_set
        from helicalbi.common.auth import set_api_cache_identity

        set_api_cache_identity("alice", "acme")
        cache_set('{"metadataFileName":"meta.json"}', "alice", "acme", {"status": 1})

        resp = flask_client.post("/clear-api-cache", json={})
        assert resp.status_code == 200
        body = json.loads(resp.data)
        assert body["status"] == 1
        assert body["cleared"] == 1
        assert body["message"] == "API cache cleared successfully"

        from helicalbi.api.ApiCallCache import get as cache_get

        assert cache_get('{"metadataFileName":"meta.json"}', "alice", "acme") is None

    def test_clear_on_empty_cache_returns_zero(self, flask_client):
        from helicalbi.api.ApiCallCache import clear as clear_api_cache

        clear_api_cache()
        resp = flask_client.post("/clear-api-cache", json={})
        assert resp.status_code == 200
        body = json.loads(resp.data)
        assert body["status"] == 1
        assert body["cleared"] == 0


# ---------------------------------------------------------------------------
# /abort
# ---------------------------------------------------------------------------
class TestAbort:
    def test_abort_requires_request_id(self, flask_client):
        resp = flask_client.post("/abort", json={})
        assert resp.status_code == 400
        body = json.loads(resp.data)
        assert body["status"] == 0
        assert "requestId" in body["error"]

    def test_abort_marks_request_cancelled(self, app_module, flask_client):
        request_id = "test-abort-123"
        app_module.request_cancellation.register(request_id)

        resp = flask_client.post("/abort", json={"requestId": request_id})
        assert resp.status_code == 200
        body = json.loads(resp.data)
        assert body["status"] == 1
        assert body["active"] is True
        assert app_module.request_cancellation.is_cancelled(request_id)

    def test_interactive_honours_abort_between_graph_steps(
        self, app_module, flask_client, session_auth, patch_graphs
    ):
        main_mock, _ = patch_graphs
        request_id = "interactive-abort-456"

        def invoke_and_abort(state, config):
            app_module.request_cancellation.cancel(request_id)
            return main_mock.invoke.return_value

        main_mock.invoke.side_effect = invoke_and_abort

        helper_mock = MagicMock()
        helper_mock.get_agent_semantic_layer.return_value = {
            "cube_metadata": [{"database_table": "t"}]
        }
        helper_mock.get_metadata_layerfile.return_value = "metadata.json"
        helper_mock.get_metadata_layerlocation.return_value = "/meta"

        payload = {
            "requestId": request_id,
            "input": {
                "inputString": "Show me sales",
                "sessionCookie": session_auth["sessionCookie"], "username": session_auth["username"],
                "agent": {"file": "agent.json", "dir": "/agents"},
                "chatid": "chat-1",
                "chat_seq_id": "1",
            },
        }

        with patch.object(app_module, "AgentLayerHelper", return_value=helper_mock), patch.object(
            app_module, "get_json_data_metadata", return_value={"joins": [], "databaseName": "db"}
        ), patch.object(
            app_module,
            "get_db_function_of_metadata",
            return_value={"reference": "postgres"},
        ):
            resp = flask_client.post("/interactive", json=payload)

        assert resp.status_code == 200
        body = json.loads(resp.data)
        assert body["aborted"] is True
        assert body["error"] == "Request has been cancelled."
        assert body["chat_response"] == {}


# ---------------------------------------------------------------------------
# /data-insight
# ---------------------------------------------------------------------------
class TestDataInsight:
    @staticmethod
    def _successful_query(data=None):
        return {
            "status": 1,
            "response": {
                "data": data
                if data is not None
                else [{"region": "APAC", "sales": 100}],
                "metadata": [],
            },
        }

    @staticmethod
    def _llm_insight_response(content="## Summary\n\nSales grew 10%."):
        response = MagicMock()
        response.content = content
        usage = MagicMock()
        usage.model_dump.return_value = {
            "input_tokens": 10,
            "output_tokens": 20,
            "total_tokens": 30,
        }
        return response, usage

    def test_returns_markdown_insight(self, app_module, flask_client, session_auth):
        llm_response, usage = self._llm_insight_response()

        with patch.object(
            app_module, "execute_query", return_value=self._successful_query()
        ) as mock_execute, patch.object(
            app_module, "invoke_llm", return_value=(llm_response, usage)
        ):
            resp = flask_client.post(
                "/data-insight",
                json={
                    "input": {
                        "sql": "SELECT region, sales FROM t",
                        "user_question": "What were sales by region?",
                        "username": "tester",
                        "thread_id": "chat-1",
                        "sessionCookie": session_auth["sessionCookie"], "username": session_auth["username"],
                        "md_location": "/meta",
                        "md_file_name": "metadata.json",
                    }
                },
            )

        assert resp.status_code == 200
        body = json.loads(resp.data)
        assert body["insight"] == "## Summary\n\nSales grew 10%."
        assert body["token_usage"]["total_tokens"] == 30
        mock_execute.assert_called_once()
        assert mock_execute.call_args.kwargs["sql"] == "SELECT region, sales FROM t"
        assert mock_execute.call_args.kwargs["md_location"] == "/meta"

    def test_exception_returns_error_payload(self, app_module, flask_client, session_auth):
        with patch.object(
            app_module,
            "execute_query",
            side_effect=RuntimeError("insight failed"),
        ):
            resp = flask_client.post(
                "/data-insight",
                json={
                    "input": {
                        "user_question": "test",
                        "sql": "SELECT 1",
                        "sessionCookie": session_auth["sessionCookie"], "username": session_auth["username"],
                        "md_location": "/meta",
                        "md_file_name": "metadata.json",
                    }
                },
            )

        assert resp.status_code == 200
        body = json.loads(resp.data)
        assert body["error"] == "insight failed"
        assert body["insight"] == ""

    def test_sql_execution_failure_returns_sql_error(
        self, app_module, flask_client, session_auth
    ):
        llm_response, usage = self._llm_insight_response("Query could not be run.")
        usage.model_dump.return_value = {}

        with patch.object(
            app_module,
            "execute_query",
            return_value={"status": 0, "response": "syntax error"},
        ), patch.object(
            app_module, "invoke_llm", return_value=(llm_response, usage)
        ):
            resp = flask_client.post(
                "/data-insight",
                json={
                    "input": {
                        "sql": "SELECT bad",
                        "user_question": "test",
                        "sessionCookie": session_auth["sessionCookie"], "username": session_auth["username"],
                        "md_location": "/meta",
                        "md_file_name": "metadata.json",
                    }
                },
            )

        body = json.loads(resp.data)
        assert body["sql_error"] == "syntax error"
        assert body["insight"] == "Query could not be run."

    def test_create_mode_resolves_sql_from_chat_memory(
        self, app_module, flask_client, session_auth
    ):
        app_module.chat_graph_memory.add_node(
            "chat-create-1",
            "2",
            {
                "sql": "SELECT region, sales FROM t",
                "dialect": "postgres",
                "user_query": "What were sales by region?",
                "user_name": session_auth["username"],
            },
        )
        llm_response, usage = self._llm_insight_response()

        with patch.object(app_module, "AgentLayerHelper") as helper_cls, patch.object(
            app_module, "execute_query", return_value=self._successful_query()
        ) as mock_execute, patch.object(
            app_module, "invoke_llm", return_value=(llm_response, usage)
        ):
            helper_cls.return_value.get_metadata_layerfile.return_value = "metadata.json"
            helper_cls.return_value.get_metadata_layerlocation.return_value = "/meta"
            resp = flask_client.post(
                "/data-insight",
                json={
                    "input": {
                        "inputString": "What were sales by region?",
                        "chatid": "chat-create-1",
                        "chat_seq_id": "2",
                        "agent": {"file": "PgSampleAgent_13.agent", "dir": "test"},
                        "sessionCookie": session_auth["sessionCookie"], "username": session_auth["username"],
                    }
                },
            )

        assert resp.status_code == 200
        mock_execute.assert_called_once()
        assert mock_execute.call_args.kwargs["sql"] == "SELECT region, sales FROM t"
        assert mock_execute.call_args.kwargs["md_location"] == "/meta"
        assert mock_execute.call_args.kwargs["md_file_name"] == "metadata.json"

    def test_edit_mode_resolves_sql_from_chat_response_item(
        self, app_module, flask_client, session_auth
    ):
        llm_response, usage = self._llm_insight_response()

        with patch.object(app_module, "AgentLayerHelper") as helper_cls, patch.object(
            app_module, "execute_query", return_value=self._successful_query()
        ) as mock_execute, patch.object(
            app_module, "invoke_llm", return_value=(llm_response, usage)
        ):
            helper_cls.return_value.get_metadata_layerfile.return_value = "metadata.json"
            helper_cls.return_value.get_metadata_layerlocation.return_value = "/meta"
            resp = flask_client.post(
                "/data-insight",
                json={
                    "input": {
                        "inputString": "What were sales by region?",
                        "chatid": "chat-edit-1",
                        "chat_seq_id": "3",
                        "agent": {"file": "PgSampleAgent_13.agent", "dir": "test"},
                        "sessionCookie": session_auth["sessionCookie"], "username": session_auth["username"],
                        "chat_response_item": {
                            "sql": {
                                "raw_sql": "```sql\nSELECT region, sales FROM t\n```",
                                "dialect": "postgres",
                            }
                        },
                    }
                },
            )

        assert resp.status_code == 200
        assert mock_execute.call_args.kwargs["sql"] == "SELECT region, sales FROM t"

    def test_passes_only_ten_rows_to_llm(self, app_module, flask_client, session_auth):
        rows = [{"id": i, "value": i * 10} for i in range(15)]
        llm_response, usage = self._llm_insight_response()

        with patch.object(
            app_module,
            "execute_query",
            return_value=self._successful_query(rows),
        ), patch.object(
            app_module, "invoke_llm", return_value=(llm_response, usage)
        ) as mock_invoke:
            resp = flask_client.post(
                "/data-insight",
                json={
                    "input": {
                        "sql": "SELECT id, value FROM t",
                        "user_question": "Show values",
                        "sessionCookie": session_auth["sessionCookie"], "username": session_auth["username"],
                        "md_location": "/meta",
                        "md_file_name": "metadata.json",
                    }
                },
            )

        assert resp.status_code == 200
        prompt = mock_invoke.call_args[0][1]
        assert '"id": 9' in prompt
        assert '"id": 10' not in prompt

    def test_passes_user_profile_to_llm_prompt(self, app_module, flask_client, session_auth):
        llm_response, usage = self._llm_insight_response()

        with patch.object(
            app_module,
            "execute_query",
            return_value=self._successful_query(),
        ), patch.object(
            app_module, "invoke_llm", return_value=(llm_response, usage)
        ) as mock_invoke:
            resp = flask_client.post(
                "/data-insight",
                json={
                    "userProfile": [
                        {"id": 2, "name": "dept", "value": "sales"},
                    ],
                    "input": {
                        "sql": "SELECT region, sales FROM t",
                        "user_question": "What were sales by region?",
                        "sessionCookie": session_auth["sessionCookie"],
                        "username": session_auth["username"],
                        "md_location": "/meta",
                        "md_file_name": "metadata.json",
                    },
                },
            )

        assert resp.status_code == 200
        prompt = mock_invoke.call_args[0][1]
        assert '"name": "dept"' in prompt
        assert '"value": "sales"' in prompt


# ---------------------------------------------------------------------------
# /getSemanticData
# ---------------------------------------------------------------------------
class TestGetSemanticData:
    def test_delegates_to_transform_json(self, app_module, flask_client, session_auth):
        payload = {
            "input": {
                "location": "/dir",
                "fileName": "meta.json",
                "sessionCookie": session_auth["sessionCookie"], "username": session_auth["username"],
                "tables": ["t1", "t2"],
            }
        }
        with patch.object(
            app_module,
            "transform_json",
            return_value={"metadata": {"domain": [""]}, "semantic_layer": []},
        ) as mock_transform:
            resp = flask_client.post("/getSemanticData", json=payload)

        assert resp.status_code == 200
        body = json.loads(resp.data)
        assert body == {"metadata": {"domain": [""]}, "semantic_layer": []}

        mock_transform.assert_called_once_with(
            session_auth["sessionCookie"], "meta.json", "/dir", ["t1", "t2"]
        )

    def test_returns_agent_error_payload_on_failure(
        self, app_module, flask_client, session_auth
    ):
        payload = {
            "input": {
                "location": "/dir",
                "fileName": "meta.json",
                "sessionCookie": session_auth["sessionCookie"],
                "username": session_auth["username"],
            }
        }
        with patch.object(
            app_module,
            "transform_json",
            side_effect=RuntimeError("metadata unavailable"),
        ):
            resp = flask_client.post("/getSemanticData", json=payload)

        assert resp.status_code == 200
        body = json.loads(resp.data)
        assert body["semantic_layer"] == []
        assert body["error"] == ["metadata unavailable"]

    def test_tables_param_defaults_to_empty_list(
        self, app_module, flask_client, session_auth
    ):
        payload = {
            "input": {
                "location": "/dir",
                "fileName": "meta.json",
                "sessionCookie": session_auth["sessionCookie"], "username": session_auth["username"],
            }
        }
        with patch.object(
            app_module, "transform_json", return_value={"ok": True}
        ) as mock_transform:
            flask_client.post("/getSemanticData", json=payload)
        mock_transform.assert_called_once_with(session_auth["sessionCookie"], "meta.json", "/dir", [])
