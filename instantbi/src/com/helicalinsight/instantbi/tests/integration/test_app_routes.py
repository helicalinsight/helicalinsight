"""Integration tests for the Flask endpoints defined in ``app.py``.

Each test exercises a real ``Flask`` route via the test client. External
collaborators (HTTP calls, LangGraph invocations, helpers) are patched so
that the routing, request parsing, and response shaping behaviour is
verified end-to-end without hitting LLMs or back-end services.
"""
import base64
import json
import logging
from contextlib import ExitStack
from pathlib import Path
from unittest.mock import MagicMock, patch

import pytest


pytestmark = pytest.mark.integration


def _patch_interactive_pipeline(app_module, helper_mock, *, metadata=None, db_ref=None):
    """Common patches for /interactive so tests never hit stub.invalid."""
    sql_generator_mock = MagicMock()
    sql_generator_mock.invoke.side_effect = lambda state, config=None: state
    helper_mock.get_metadata.return_value = (
        metadata
        if metadata is not None
        else {"joins": [], "databaseName": "testdb"}
    )
    return (
        patch.object(app_module, "ModelLayerHelper", return_value=helper_mock),
        patch.object(
            app_module,
            "get_db_function_of_metadata",
            return_value=db_ref or {"reference": "postgres"},
        ),
        patch("helicalbi.controller.interactive.sql_generator_graph", sql_generator_mock),
        patch(
            "helicalbi.controller.interactive.SqlExecutor.process_flow",
            side_effect=lambda state: state,
        ),
        patch("helicalbi.controller.interactive.audit_llm_usage_async"),
    )


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
    def test_returns_domain_name_from_model(self, app_module, flask_client, session_auth):
        helper_mock = MagicMock()
        helper_mock.get_model_semantic_layer.return_value = {
            "domain": [{"domain_name": "Sales Operation", "topics": []}]
        }
        helper_mock.get_model_description.return_value = ""

        with patch.object(app_module, "ModelLayerHelper", return_value=helper_mock):
            resp = flask_client.post(
                "/suggestDomain",
                json={
                    **session_auth,
                    "model": {"file": "a.json", "dir": "/models"},
                },
            )

        assert resp.status_code == 200
        assert resp.data.decode() == "Sales Operation"

    def test_falls_back_to_model_description(self, app_module, flask_client, session_auth):
        helper_mock = MagicMock()
        helper_mock.get_model_semantic_layer.return_value = {"domain": []}
        helper_mock.get_model_description.return_value = "Travel spend analytics"

        with patch.object(app_module, "ModelLayerHelper", return_value=helper_mock):
            resp = flask_client.post(
                "/suggestDomain",
                json={
                    **session_auth,
                    "model": {"file": "a.json", "dir": "/models"},
                },
            )

        assert resp.status_code == 200
        assert resp.data.decode() == "Travel spend analytics"


# ---------------------------------------------------------------------------
# /topNQuestion
# ---------------------------------------------------------------------------
class TestTopNQuestion:
    def test_returns_kpis_joined_with_newlines(
        self, app_module, flask_client, session_auth
    ):
        helper_mock = MagicMock()
        helper_mock.get_model_semantic_layer.return_value = {"domain": []}
        helper_mock.get_metadata_layerfile.return_value = "metadata.json"
        helper_mock.get_metadata_layerlocation.return_value = "/meta"
        helper_mock.get_metadata.return_value = {
            "joins": [],
            "databaseName": "testdb",
            "tables": {},
        }

        kpi_mock = MagicMock()
        kpi_mock.user_query = "Suggest KPIs"
        kpi_mock.top_kpis.return_value = (["KPI A", "KPI B", "KPI C"], {"total_tokens": 0})

        with patch.object(app_module, "ModelLayerHelper", return_value=helper_mock), patch.object(
            app_module, "KpiProvider", return_value=kpi_mock
        ):
            resp = flask_client.post(
                "/topNQuestion",
                json={
                    **session_auth,
                    "domain": "Sales Operation",
                    "topN": 3,
                    "model": {"file": "a.json", "dir": "/models"},
                },
            )

        assert resp.status_code == 200
        assert resp.data.decode() == "KPI A\nKPI B\nKPI C"

    def test_returns_error_message_array_on_failure(
        self, app_module, flask_client, session_auth
    ):
        with patch.object(
            app_module,
            "ModelLayerHelper",
            side_effect=RuntimeError("Failed to fetch model."),
        ):
            resp = flask_client.post(
                "/topNQuestion",
                json={
                    **session_auth,
                    "domain": "Sales Operation",
                    "topN": 3,
                    "model": {"file": "a.json", "dir": "/models"},
                },
            )

        assert resp.status_code == 200
        body = json.loads(resp.data)
        assert body["error"] == ["Failed to fetch model."]


# ---------------------------------------------------------------------------
# /interactive
# ---------------------------------------------------------------------------
class TestInteractive:
    def _build_payload(self, session_auth, query="Show me sales"):
        return {
            "input": {
                **session_auth,
                "inputString": query,
                "model": {"file": "model.json", "dir": "/models"},
                "chatid": "chat-1",
                "chat_seq_id": "1",
            }
        }

    @staticmethod
    def _metadata_payload(**extra):
        payload = {"joins": [], "databaseName": "testdb"}
        payload.update(extra)
        return payload

    def test_happy_path_returns_json_with_sql_and_citations(
        self, app_module, flask_client, session_auth, patch_graphs
    ):
        main_mock, viz_mock = patch_graphs

        viz_mock.invoke.return_value = {
            "sql": "select a from t",
            "flow": ["[ABC123XYZ]", "no-match"],
            "messages": [],
            "sql_result": {"data": [{"a": 1}, {"a": 2}], "metadata": [{}]},
            "dialect": "postgres",
        }

        helper_mock = MagicMock()
        helper_mock.get_model_semantic_layer.return_value = {
            "cube_metadata": [{"database_table": "t"}]
        }
        helper_mock.get_metadata_layerfile.return_value = "metadata.json"
        helper_mock.get_metadata_layerlocation.return_value = "/meta"

        with ExitStack() as stack:
            for ctx in _patch_interactive_pipeline(app_module, helper_mock):
                stack.enter_context(ctx)
            resp = flask_client.post(
                "/interactive", json=self._build_payload(session_auth)
            )

        assert resp.status_code == 200
        body = json.loads(resp.data)

        chat = body["chat_response"]
        # SQL formatted with the markdown code fence.
        assert chat["sql"]["raw_sql"].startswith("```sql")
        # Interactive wire response includes viz; omits data and metadata.
        assert "viz" in chat
        assert "chart_name" in chat["viz"]
        assert "vf_title" in chat["viz"]
        assert "vf_template" in chat["viz"]
        assert "data" not in chat
        assert "metadata" not in chat
        # Graphs were invoked.
        assert main_mock.invoke.called
        assert viz_mock.invoke.called
        data_model = chat["report_model"]["data_model"]
        assert data_model["location"] == "/meta"
        assert data_model["metadataFileName"] == "metadata.json"
        assert data_model["columns"]
        assert data_model["columns"][0]["alias"] == "a"
        assert "query" not in data_model
        assert "data_model" not in chat
        assert "viz_model" not in chat.get("viz", {})

    def test_sql_error_populates_data_model_columns_without_viz(
        self, app_module, flask_client, session_auth, patch_graphs
    ):
        _, viz_mock = patch_graphs
        sql = (
            'SELECT COUNT("meeting_details"."meet_cancellation_status") '
            'AS "Cancelled Meetings" FROM "meeting_details" '
            "WHERE EXTRACT(YEAR FROM \"meeting_details\".\"travel_date\") = 2026"
        )
        viz_mock.invoke.return_value = {
            "sql": sql,
            "sql_error": (
                "Error: PSQLException: ERROR: column meeting_details.travel_date "
                "does not exist"
            ),
            "skip": True,
            "output": "Sorry, that figure isn't available.",
            "dialect": "postgresql",
            "messages": [],
        }

        helper_mock = MagicMock()
        helper_mock.get_model_semantic_layer.return_value = {
            "cube_metadata": [{"database_table": "meeting_details"}]
        }
        helper_mock.get_metadata_layerfile.return_value = "Metadata_1.metadata"
        helper_mock.get_metadata_layerlocation.return_value = "Postgres_travel_data"

        with ExitStack() as stack:
            for ctx in _patch_interactive_pipeline(
                app_module,
                helper_mock,
                metadata={
                    "joins": [],
                    "databaseName": "testdb",
                    "name": "sampletraveldata.public",
                    "tables": {
                        "meeting_details": {
                            "columns": {
                                "meet_cancellation_status": {
                                    "id": "1",
                                    "alias": "meet_cancellation_status",
                                }
                            }
                        }
                    },
                },
                db_ref={
                    "reference": "postgresql",
                    "functions": {"db.generic.aggregate.count": "count"},
                },
            ):
                stack.enter_context(ctx)
            resp = flask_client.post(
                "/interactive", json=self._build_payload(session_auth)
            )

        assert resp.status_code == 200
        body = json.loads(resp.data)
        chat = body["chat_response"]
        assert "travel_date" in chat["error"]
        assert chat["viz"]["chart_name"] == ""
        assert chat["report_model"]["viz_model"] is None
        columns = chat["report_model"]["data_model"]["columns"]
        assert any(col.get("alias") == "Cancelled Meetings" for col in columns)
        assert "query" not in chat["report_model"]["data_model"]

    def test_exception_path_returns_error_payload(
        self, app_module, flask_client, session_auth, patch_graphs
    ):
        main_mock, _ = patch_graphs
        # The /interactive endpoint wraps graph invocation in try/except,
        # so a failure in main_graph.invoke must be turned into the JSON
        # error payload instead of a 500 response.
        main_mock.invoke.side_effect = RuntimeError("boom")

        helper_mock = MagicMock()
        helper_mock.get_model_semantic_layer.return_value = {
            "cube_metadata": [{"database_table": "t"}]
        }
        helper_mock.get_metadata_layerfile.return_value = "metadata.json"
        helper_mock.get_metadata_layerlocation.return_value = "/meta"

        with ExitStack() as stack:
            for ctx in _patch_interactive_pipeline(app_module, helper_mock):
                stack.enter_context(ctx)
            stack.enter_context(patch("helicalbi.controller.interactive.is_debug", return_value=True))
            resp = flask_client.post(
                "/interactive", json=self._build_payload(session_auth)
            )

        assert resp.status_code == 200
        body = json.loads(resp.data)
        assert body["error"] == "boom"
        assert "RuntimeError" in body["stack"]
        assert body["messages"] == []

    def test_service_api_failure_returns_error_key(
        self, app_module, flask_client, session_auth, patch_graphs
    ):
        from helicalbi.api.HttpCallService import ServiceApiError

        with ExitStack() as stack:
            for ctx in _patch_interactive_pipeline(app_module, MagicMock()):
                stack.enter_context(ctx)
            stack.enter_context(
                patch.object(
                    app_module,
                    "ModelLayerHelper",
                    side_effect=ServiceApiError("The file missing.model doesn't exists."),
                )
            )
            resp = flask_client.post(
                "/interactive", json=self._build_payload(session_auth)
            )

        assert resp.status_code == 200
        body = json.loads(resp.data)
        assert body["error"] == "The file missing.model doesn't exists."

    def test_empty_model_structure_uses_metadata_fallback_and_logs_each_layer(
        self, app_module, flask_client, session_auth, patch_graphs, caplog
    ):
        main_mock, viz_mock = patch_graphs
        metadata_fixture = (
            Path(__file__).resolve().parents[1] / "fixtures" / "sample_metadata_response.json"
        )
        metadata_blob = json.loads(metadata_fixture.read_text(encoding="utf-8"))
        metadata_payload = metadata_blob.get("response", metadata_blob)
        metadata_payload.setdefault("joins", [])
        metadata_payload.setdefault("databaseName", "sampletraveldata.public")

        helper_mock = MagicMock()
        # Simulate an empty semantic structure coming from the model file.
        helper_mock.get_model_semantic_layer.return_value = {}
        helper_mock.get_metadata_layerfile.return_value = "metadata.json"
        helper_mock.get_metadata_layerlocation.return_value = "/meta"

        empty_state = {
            "query": "Top 5 travel cost by travel type and travel medium",
            "sql": "",
            "sqlModel": {},
            "sql_result": {},
            "messages": [],
            "flow": [],
            "dialect": "postgres",
        }
        main_mock.invoke.return_value = empty_state

        with caplog.at_level(logging.DEBUG, logger="helicalbi.controller.interactive"), ExitStack() as stack:
            for ctx in _patch_interactive_pipeline(
                app_module, helper_mock, metadata=metadata_payload
            ):
                stack.enter_context(ctx)
            viz_mock.invoke.return_value = empty_state
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
        assert "data" not in body["chat_response"]
        assert "metadata" not in body["chat_response"]
        assert "viz" in body["chat_response"]

        log_text = caplog.text
        assert "Invoking main graph" in log_text
        assert "Invoking SQL generator graph" in log_text
        assert "Executing SQL" in log_text
        assert "Invoking visualization graph" in log_text


# ---------------------------------------------------------------------------
# /clear-api-cache
# ---------------------------------------------------------------------------
class TestClearApiCache:
    def test_clears_cached_api_responses(self, app_module, flask_client, session_cookie, monkeypatch):
        from helicalbi.api.ApiCallCache import set as cache_set
        from helicalbi.common import app_config
        from helicalbi.common.auth import set_api_cache_identity

        monkeypatch.setattr(app_config, "api_cache_enabled", True)
        set_api_cache_identity("alice", "acme", user_id=1, org_id=5)
        cache_set('{"metadataFileName":"meta.json"}', "alice", "acme", {"status": 1}, org_id=5)

        resp = flask_client.post("/clear-api-cache", json={})
        assert resp.status_code == 200
        body = json.loads(resp.data)
        assert body["status"] == 1
        assert body["cleared"] == 1
        assert body["message"] == "API cache cleared successfully"

        from helicalbi.api.ApiCallCache import get as cache_get

        assert cache_get('{"metadataFileName":"meta.json"}', "alice", "acme", org_id=5) is None

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
        helper_mock.get_model_semantic_layer.return_value = {
            "cube_metadata": [{"database_table": "t"}]
        }
        helper_mock.get_metadata_layerfile.return_value = "metadata.json"
        helper_mock.get_metadata_layerlocation.return_value = "/meta"

        payload = {
            "requestId": request_id,
            "input": {
                "inputString": "Show me sales",
                "sessionCookie": session_auth["sessionCookie"],
                "username": session_auth["username"],
                "model": {"file": "model.json", "dir": "/models"},
                "chatid": "chat-1",
                "chat_seq_id": "1",
            },
        }

        with ExitStack() as stack:
            for ctx in _patch_interactive_pipeline(
                app_module,
                helper_mock,
                metadata={"joins": [], "databaseName": "db"},
            ):
                stack.enter_context(ctx)
            resp = flask_client.post("/interactive", json=payload)

        assert resp.status_code == 200
        body = json.loads(resp.data)
        assert body["aborted"] is True
        assert body["error"] == "Request has been cancelled."
        # Partial graph state may be returned; abort flag is the contract.
        assert isinstance(body.get("chat_response"), dict)


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
        assert mock_execute.call_args.kwargs["sql"] == 'SELECT "region", "sales" FROM "t"'
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

        with patch.object(app_module, "ModelLayerHelper") as helper_cls, patch.object(
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
                        "model": {"file": "PgSampleModel_13.model", "dir": "test"},
                        "sessionCookie": session_auth["sessionCookie"], "username": session_auth["username"],
                    }
                },
            )

        assert resp.status_code == 200
        mock_execute.assert_called_once()
        assert mock_execute.call_args.kwargs["sql"] == 'SELECT "region", "sales" FROM "t"'
        assert mock_execute.call_args.kwargs["md_location"] == "/meta"
        assert mock_execute.call_args.kwargs["md_file_name"] == "metadata.json"

    def test_edit_mode_resolves_sql_from_chat_response_item(
        self, app_module, flask_client, session_auth
    ):
        llm_response, usage = self._llm_insight_response()

        with patch.object(app_module, "ModelLayerHelper") as helper_cls, patch.object(
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
                        "model": {"file": "PgSampleModel_13.model", "dir": "test"},
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
        assert mock_execute.call_args.kwargs["sql"] == 'SELECT "region", "sales" FROM "t"'

    def test_passes_only_ten_rows_to_llm(self, app_module, flask_client, session_auth, monkeypatch):
        from helicalbi.common import app_config

        monkeypatch.setattr(app_config, "default_sql_limit", 10)
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
# /instant-to-hr
# ---------------------------------------------------------------------------
class TestInstantToHr:
    def test_returns_form_data(self, flask_client, session_auth):
        form_data = {
            "location": "/meta",
            "metadataFileName": "metadata.json",
            "columns": [
                {
                    "column": {"name": "t.region", "id": "1"},
                    "alias": "region",
                    "floatingType": "discrete",
                }
            ],
            "functions": [],
        }
        with patch(
            "helicalbi.controller.instant_to_hr.sql_to_form_data", return_value=form_data
        ) as mock_convert:
            resp = flask_client.post(
                "/instant-to-hr",
                json={
                    "input": {
                        "sql": "SELECT region FROM t",
                        "metadata_dir": "/meta",
                        "metadata_file_name": "metadata.json",
                        "sessionCookie": session_auth["sessionCookie"],
                        "username": session_auth["username"],
                    }
                },
            )

        assert resp.status_code == 200
        body = json.loads(resp.data)
        assert "sql_parts" in body
        assert "viz_parts" in body
        assert body["sql_parts"]["columns"] == [
            {
                "table": "t",
                "column": "region",
                "databaseFunction": "",
                "shelf": "row",
                "alias": "region",
            }
        ]
        assert "limitBy" not in body
        mock_convert.assert_called_once()
        assert mock_convert.call_args.args[0] == 'SELECT "region" FROM "t"'
        assert mock_convert.call_args.kwargs["metadata_dir"] == "/meta"
        assert mock_convert.call_args.kwargs["metadata_file_name"] == "metadata.json"
        assert mock_convert.call_args.kwargs["location"] == "/meta"
        assert mock_convert.call_args.kwargs["session_cookie"] == session_auth["sessionCookie"]

    def test_returns_sql_parts_location_from_model(self, app_module, flask_client, session_auth):
        helper_mock = MagicMock()
        helper_mock.get_metadata_layerfile.return_value = "pg.metadata"
        helper_mock.get_metadata_layerlocation.return_value = "/meta"
        helper_mock.get_metadata.return_value = {
            "classifier": "db.generic",
            "tables": {
                "t": {
                    "alias": "t",
                    "columns": {
                        "region": {"id": "1", "alias": "region"},
                    },
                }
            },
        }
        form_data = {
            "location": "/meta",
            "metadataFileName": "pg.metadata",
            "columns": [
                {
                    "column": {"name": "t.region", "id": "1"},
                    "alias": "region",
                    "floatingType": "discrete",
                }
            ],
        }
        with patch.object(app_module, "ModelLayerHelper", return_value=helper_mock), patch(
            "helicalbi.controller.instant_to_hr.sql_to_form_data", return_value=form_data
        ) as mock_convert:
            resp = flask_client.post(
                "/instant-to-hr",
                json={
                    "input": {
                        "sql": "SELECT region FROM t",
                        "model": {"file": "travel.agent", "dir": "/models"},
                        "sessionCookie": session_auth["sessionCookie"],
                        "username": session_auth["username"],
                    }
                },
            )

        assert resp.status_code == 200
        body = json.loads(resp.data)
        assert "metadata" not in body
        assert body["sql_parts"]["location"] == "/meta"
        assert body["sql_parts"]["metadataFileName"] == "pg.metadata"
        assert mock_convert.call_args.kwargs["metadata"]["tables"]["t"]["alias"] == "t"

    def test_accepts_md_location_aliases(self, flask_client, session_auth):
        with patch(
            "helicalbi.controller.instant_to_hr.sql_to_form_data",
            return_value={"columns": []},
        ) as mock_convert:
            resp = flask_client.post(
                "/instant-to-hr",
                json={
                    "input": {
                        "sql": "SELECT 1",
                        "md_location": "/meta",
                        "md_file_name": "metadata.json",
                        "sessionCookie": session_auth["sessionCookie"],
                        "username": session_auth["username"],
                    }
                },
            )

        assert resp.status_code == 200
        mock_convert.assert_called_once()
        assert mock_convert.call_args.kwargs["location"] == "/meta"
        assert mock_convert.call_args.kwargs["metadata_dir"] == "/meta"
        assert mock_convert.call_args.kwargs["metadata_file_name"] == "metadata.json"

    def test_missing_sql_returns_error(self, flask_client, session_auth):
        resp = flask_client.post(
            "/instant-to-hr",
            json={
                "input": {
                    "metadata_dir": "/meta",
                    "metadata_file_name": "metadata.json",
                    "sessionCookie": session_auth["sessionCookie"],
                    "username": session_auth["username"],
                }
            },
        )
        assert resp.status_code == 200
        body = json.loads(resp.data)
        assert "No SQL found" in body["error"]

    def test_resolves_sql_from_chat_response_item(self, flask_client, session_auth):
        with patch(
            "helicalbi.controller.instant_to_hr.sql_to_form_data",
            return_value={"columns": []},
        ) as mock_convert:
            resp = flask_client.post(
                "/instant-to-hr",
                json={
                    "input": {
                        "chat_response_item": {
                            "sql": {
                                "raw_sql": "SELECT region FROM sales",
                                "dialect": "postgres",
                            }
                        },
                        "metadata_dir": "/meta",
                        "metadata_file_name": "metadata.json",
                        "sessionCookie": session_auth["sessionCookie"],
                        "username": session_auth["username"],
                    }
                },
            )

        assert resp.status_code == 200
        mock_convert.assert_called_once()
        assert mock_convert.call_args.args[0] == 'SELECT "region" FROM "sales"'

    def test_exception_returns_error_payload(self, flask_client, session_auth):
        with patch(
            "helicalbi.controller.instant_to_hr.sql_to_form_data",
            side_effect=RuntimeError("convert failed"),
        ):
            resp = flask_client.post(
                "/instant-to-hr",
                json={
                    "input": {
                        "sql": "SELECT 1",
                        "metadata_dir": "/meta",
                        "metadata_file_name": "metadata.json",
                        "sessionCookie": session_auth["sessionCookie"],
                        "username": session_auth["username"],
                    }
                },
            )

        assert resp.status_code == 200
        body = json.loads(resp.data)
        assert body["error"] == "convert failed"


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

    def test_returns_model_error_payload_on_failure(
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


# ---------------------------------------------------------------------------
# /utility/*
# ---------------------------------------------------------------------------
class TestUtilityConfig:
    def test_list_llm_providers(self, flask_client):
        resp = flask_client.get("/utility/llm")
        assert resp.status_code == 200
        body = json.loads(resp.data)
        assert body["status"] == 1
        assert "providers" in body
        assert body["default_provider"] == "openai"

    def test_list_models_for_package(self, flask_client):
        resp = flask_client.get(
            "/settings/models",
            query_string={"package": "langchain-openai"},
        )
        assert resp.status_code == 200
        body = json.loads(resp.data)
        assert body["status"] == 1
        assert body["package"] == "langchain-openai"
        assert "gpt-4.1-mini" in body["models"]

    def test_list_models_requires_package_or_provider(self, flask_client):
        resp = flask_client.get("/settings/models")
        assert resp.status_code == 200
        body = json.loads(resp.data)
        assert body["status"] == 0
        assert body["error"]

    def test_get_app_config(self, flask_client):
        resp = flask_client.get("/utility/app-config")
        assert resp.status_code == 200
        body = json.loads(resp.data)
        assert body["status"] == 1
        assert "logging" in body["config"]
        assert "kpi" in body["config"]


# ---------------------------------------------------------------------------
# /convert-dashboard
# ---------------------------------------------------------------------------
class TestConvertDashboard:
    def test_returns_layout_parts_not_efwdd(self, flask_client, session_auth):
        graph_result = {
            "items": [
                {
                    "component_id": "ab12CD34",
                    "report_model": {
                        "viz_model": {"chart": {"viz": "Bar", "mark": "Chart"}},
                        "data_model": None,
                    },
                    "dashboard_model": {
                        "kind": "viz",
                        "title": "Cost",
                        "layout": {"x": 0, "y": 2, "w": 6, "h": 4},
                    },
                }
            ],
            "theme": {"color": "#1677ff", "background": "#ffffff"},
            "layout": [{"itemId": "seq-3", "x": 0, "y": 2, "w": 6, "h": 4}],
        }
        graph_mock = MagicMock()
        graph_mock.invoke.return_value = graph_result
        with patch(
            "helicalbi.controller.convert_dashboard.dashboard_layout_graph", graph_mock
        ):
            resp = flask_client.post(
                "/convert-dashboard",
                json={
                    "input": {
                        "chatid": "c1",
                        "sessionCookie": session_auth["sessionCookie"],
                        "username": session_auth["username"],
                        "items": [
                            {
                                "id": "seq-3",
                                "sql": "SELECT region FROM t",
                                "viz": {"chart_name": "bar", "vf_template": "function(){}"},
                            }
                        ],
                    }
                },
            )

        assert resp.status_code == 200
        body = json.loads(resp.data)
        assert "items" in body
        assert "metadata" not in body
        assert "gridItemsData" not in body
        assert "sql_parts" not in (body["items"][0] or {})
        assert body["items"][0]["dashboard_model"]["layout"]["w"] == 6
        assert "viz" not in body["items"][0]
        invoked = graph_mock.invoke.call_args[0][0]["items"][0]
        assert "sql_parts" not in invoked
        assert "vf_template" not in (invoked.get("viz") or {})
        graph_mock.invoke.assert_called_once()

    def test_missing_items_returns_error(self, flask_client, session_auth):
        resp = flask_client.post(
            "/convert-dashboard",
            json={
                "input": {
                    "chatid": "missing",
                    "sessionCookie": session_auth["sessionCookie"],
                    "username": session_auth["username"],
                    "items": [],
                }
            },
        )
        assert resp.status_code == 200
        body = json.loads(resp.data)
        assert "No visualizations" in body["error"]


# ---------------------------------------------------------------------------
# /agent-dashboard
# ---------------------------------------------------------------------------
class TestAgentDashboard:
    def _payload(self, session_auth, query="Why is travel cost high?"):
        return {
            "input": {
                **session_auth,
                "inputString": query,
                "model": {"file": "model.json", "dir": "/models"},
                "dashboardid": "dash-agent-1",
                "dashboard_sequence_id": "1",
            }
        }

    def test_returns_plan_without_executing(self, flask_client, session_auth):
        plan_result = {
            "phase": "plan",
            "original_question": "Why is travel cost high?",
            "dashboardid": "dash-agent-1",
            "dashboard_sequence_id": "1",
            "persona": {"name": "tactical_manager", "tier": "tactical"},
            "plan": {
                "charts": [
                    {
                        "title": "Total travel cost",
                        "question": "What is total travel cost?",
                        "viz_hint": "kpi",
                        "purpose": "Headline KPI",
                    }
                ]
            },
            "message": "Plan ready.",
            "asked_questions": ["What is total travel cost?"],
            "attempt_count": 0,
            "investigation_steps": [
                {"step": 1, "question": "What is total travel cost?", "kind": "planned_chart", "analysis": "Headline KPI"}
            ],
            "sub_questions": [],
            "dashboard": {},
            "token_usage": {},
            "mode": {"name": "balanced"},
        }
        payload = self._payload(session_auth)
        payload["userRole"] = [{"roleName": "CFO"}]
        payload["userProfile"] = [{"name": "dept", "value": "finance"}]
        captured = {}

        def _create_plan(*_args, **kwargs):
            captured.update(kwargs)
            return plan_result

        with patch(
            "helicalbi.sql_agent.investigation.create_and_store_plan",
            side_effect=_create_plan,
        ) as create_plan, patch(
            "helicalbi.sql_agent.dashboard_graph.run_dashboard_agent"
        ) as run_agent, patch("helicalbi.controller.agent_dashboard.audit_llm_usage_async"):
            resp = flask_client.post("/agent-dashboard", json=payload)

        assert resp.status_code == 200
        body = json.loads(resp.data)
        assert body["phase"] == "plan"
        assert body["dashboardid"] == "dash-agent-1"
        assert body["plan"]["charts"][0]["viz_hint"] == "kpi"
        assert body["dashboard"] == {}
        assert captured["user_role"][0]["roleName"] == "CFO"
        assert captured["user_profile"][0]["value"] == "finance"
        create_plan.assert_called_once()
        run_agent.assert_not_called()

    def test_execute_plan_runs_stored_investigation(self, flask_client, session_auth):
        stored = {
            "kind": "dashboard_investigation_plan",
            "original_question": "Why is travel cost high?",
            "agent_mode": "balanced",
            "persona": {"name": "tactical_manager"},
            "user_role": [{"roleName": "CFO"}],
            "user_profile": [],
            "plan": {"charts": [{"question": "What is total travel cost?", "viz_hint": "kpi"}]},
        }
        agent_result = {
            "original_question": "Why is travel cost high?",
            "final_answer": "West region drives travel cost.",
            "collected_data": [
                {
                    "sub_question": "What is total travel cost?",
                    "analysis": "Total is 10",
                    "chat_seq_id": "1-1",
                    "chat_response": {
                        "report_model": {
                            "data_model": {"columns": [{"alias": "cost"}]},
                            "viz_model": {"chart": {"viz": "KPI"}},
                        }
                    },
                    "report_model": {
                        "data_model": {"columns": [{"alias": "cost"}]},
                        "viz_model": {"chart": {"viz": "KPI"}},
                    },
                }
            ],
            "dashboard": {
                "dashboardid": "dash-agent-1",
                "items": [{"component_id": "ab12CD34"}],
                "layout": [{"itemId": "ab12CD34", "x": 0, "y": 0, "w": 6, "h": 4}],
                "theme": {"color": "#1677ff"},
                "templateId": "analytical-grid",
            },
            "token_usage": {"total_tokens": 9},
            "asked_questions": ["What is total travel cost?"],
            "attempt_count": 4,
            "investigation_plan": stored["plan"],
            "persona": stored["persona"],
            "mode": {"name": "balanced"},
        }
        payload = self._payload(session_auth, query="execute plan")
        captured = {}

        def _run_agent(question, **kwargs):
            captured["question"] = question
            captured.update(kwargs)
            return agent_result

        with patch(
            "helicalbi.sql_agent.investigation.stored_plan_or_raise",
            return_value=stored,
        ), patch(
            "helicalbi.sql_agent.dashboard_graph.run_dashboard_agent",
            side_effect=_run_agent,
        ), patch("helicalbi.sql_agent.plan_memory.save_plan"), patch(
            "helicalbi.controller.agent_dashboard.audit_llm_usage_async"
        ):
            resp = flask_client.post("/agent-dashboard", json=payload)

        assert resp.status_code == 200
        body = json.loads(resp.data)
        assert body["phase"] == "execute"
        assert body["original_question"] == "Why is travel cost high?"
        assert captured["question"] == "Why is travel cost high?"
        assert captured["investigation_plan"]["charts"][0]["viz_hint"] == "kpi"
        assert body["final_answer"] == "West region drives travel cost."
        assert body["asked_questions"] == ["What is total travel cost?"]
        assert body["attempt_count"] == 4
        assert body["investigation_steps"][0]["question"] == "What is total travel cost?"
        assert body["sub_questions"][0]["report_model"]["viz_model"]["chart"]["viz"] == "KPI"
        assert body["dashboard"]["templateId"] == "analytical-grid"

    def test_execute_plan_without_stored_plan_returns_error(self, flask_client, session_auth):
        from helicalbi.common.ChatGraphMemory import chat_graph_memory

        chat_graph_memory.clear()
        payload = self._payload(session_auth, query="execute plan")
        with patch("helicalbi.controller.agent_dashboard.audit_llm_usage_async"):
            resp = flask_client.post("/agent-dashboard", json=payload)
        assert resp.status_code == 200
        body = json.loads(resp.data)
        assert "No stored plan" in body["error"]

    def test_missing_question_returns_error(self, flask_client, session_auth):
        with patch("helicalbi.controller.agent_dashboard.audit_llm_usage_async"):
            resp = flask_client.post(
                "/agent-dashboard",
                json={
                    "input": {
                        **session_auth,
                        "inputString": "",
                        "model": {"file": "model.json", "dir": "/models"},
                        "dashboardid": "dash-agent-1",
                    }
                },
            )
        assert resp.status_code == 200
        body = json.loads(resp.data)
        assert "inputString" in body["error"]

    def test_missing_dashboardid_returns_error(self, flask_client, session_auth):
        with patch("helicalbi.controller.agent_dashboard.audit_llm_usage_async"):
            resp = flask_client.post(
                "/agent-dashboard",
                json={
                    "input": {
                        **session_auth,
                        "inputString": "Why is travel cost high?",
                        "model": {"file": "model.json", "dir": "/models"},
                    }
                },
            )
        assert resp.status_code == 200
        body = json.loads(resp.data)
        assert "dashboardid" in body["error"]

    def test_reads_sequence_id_and_ignores_payload_max_sub_questions(
        self, flask_client, session_auth, monkeypatch
    ):
        from helicalbi.common import app_config

        monkeypatch.setattr(app_config, "dashboard_max_sub_questions", 7)
        monkeypatch.setattr(app_config, "dashboard_default_mode", "balanced")
        captured = {}

        def _create_plan(*_args, **kwargs):
            captured.update(kwargs)
            return {
                "phase": "plan",
                "original_question": "Why is travel cost high?",
                "dashboardid": kwargs.get("thread_id"),
                "dashboard_sequence_id": str(kwargs.get("chat_seq_id") or "1"),
                "plan": {"charts": []},
                "persona": {"name": "tactical_manager"},
                "mode": {"name": "fast", "max_charts": 2, "max_tool_loops": 10, "use_llm_synthesizer": False},
                "token_usage": {},
            }

        payload = self._payload(session_auth)
        payload["input"]["max_sub_questions"] = 99
        payload["input"]["mode"] = "fast"
        with patch(
            "helicalbi.sql_agent.investigation.create_and_store_plan",
            side_effect=_create_plan,
        ), patch("helicalbi.controller.agent_dashboard.audit_llm_usage_async"):
            resp = flask_client.post("/agent-dashboard", json=payload)

        assert resp.status_code == 200
        body = json.loads(resp.data)
        assert captured["chat_seq_id"] == "1"
        assert captured["max_sub_questions"] == 7
        assert captured["agent_mode"] == "fast"
        assert captured["thread_id"] == "dash-agent-1"
        assert body["mode"]["name"] == "fast"
        assert body["phase"] == "plan"



