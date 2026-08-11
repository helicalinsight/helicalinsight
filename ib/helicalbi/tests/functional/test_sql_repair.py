"""Tests for the SQL self-correction loop in ``SqlExecutor.process_flow``.

When a generated query is rejected by the database, the executor feeds the
error back to the model to regenerate a corrected query, up to
``app_config.sql_repair_max_attempts`` times, before surfacing an error to the
user. ``0`` preserves the original single-shot behaviour.
"""
from types import SimpleNamespace
from unittest.mock import patch

import pytest

from helicalbi.core.flows.SqlExecutor import SqlExecutor

pytestmark = pytest.mark.functional


def _base_state():
    return {
        "query": "show me total sales by region",
        "session_cookie": "cookie",
        "md_location": "/loc",
        "md_file_name": "model.metadata",
        "sql": "SELECT bad_col FROM sales",
        "thread_id": "t-1",
        "user_name": "Darshan",
        "intent": "NEW",
        "required_tables": ["sales"],
        "domain": ["sales"],
        "topics": ["revenue"],
        "dialect": "postgres",
        "dbname": "warehouse",
        "required_details": {"required_tables": ["sales"]},
    }


def _fake_insight(*_args, **_kwargs):
    return SimpleNamespace(content="a friendly message"), None


def _set_max_attempts(monkeypatch, value):
    # Setting a real attribute shadows the module-level __getattr__ resolver.
    monkeypatch.setattr(
        "helicalbi.common.app_config.sql_repair_max_attempts", value, raising=False
    )


class TestSqlSelfCorrection:
    def test_repairs_and_succeeds(self, monkeypatch):
        _set_max_attempts(monkeypatch, 1)
        responses = [
            {"status": 0, "response": "ERROR: column bad_col does not exist"},
            {"status": 1, "response": {"data": [{"total": 42}], "metadata": [{"rows": 1}]}},
        ]
        with patch("helicalbi.core.flows.SqlExecutor.execute_query", side_effect=responses) as exec_mock, \
             patch("helicalbi.core.flows.SqlExecutor.invoke_llm", side_effect=_fake_insight), \
             patch.object(SqlExecutor, "_repair_sql", return_value="SELECT SUM(amount) AS total FROM sales") as repair_mock:
            state = SqlExecutor().process_flow(_base_state())

        assert exec_mock.call_count == 2
        assert repair_mock.call_count == 1
        assert state["sql"] == "SELECT SUM(amount) AS total FROM sales"
        assert state["data"] == [{"total": 42}]
        assert state["sql_error"] == "Not Generated"
        assert not state.get("skip")

    def test_gives_up_after_max_attempts(self, monkeypatch):
        _set_max_attempts(monkeypatch, 1)
        with patch("helicalbi.core.flows.SqlExecutor.execute_query",
                   return_value={"status": 0, "response": "ERROR: boom"}) as exec_mock, \
             patch("helicalbi.core.flows.SqlExecutor.invoke_llm", side_effect=_fake_insight) as llm_mock, \
             patch.object(SqlExecutor, "_repair_sql", return_value="SELECT still_wrong FROM sales") as repair_mock:
            state = SqlExecutor().process_flow(_base_state())

        # one initial execution + exactly one repair execution
        assert exec_mock.call_count == 2
        assert repair_mock.call_count == 1
        assert state["skip"] is True
        assert state["sql_error"] == "ERROR: boom"
        assert state["output"] == "a friendly message"
        # only the final user-facing error message is generated via invoke_llm here
        assert llm_mock.call_count == 1

    def test_disabled_when_zero(self, monkeypatch):
        _set_max_attempts(monkeypatch, 0)
        with patch("helicalbi.core.flows.SqlExecutor.execute_query",
                   return_value={"status": 0, "response": "ERROR: boom"}) as exec_mock, \
             patch("helicalbi.core.flows.SqlExecutor.invoke_llm", side_effect=_fake_insight), \
             patch.object(SqlExecutor, "_repair_sql", return_value="SELECT anything") as repair_mock:
            state = SqlExecutor().process_flow(_base_state())

        assert exec_mock.call_count == 1
        assert repair_mock.call_count == 0
        assert state["skip"] is True

    def test_success_first_try_does_not_repair(self, monkeypatch):
        _set_max_attempts(monkeypatch, 2)
        with patch("helicalbi.core.flows.SqlExecutor.execute_query",
                   return_value={"status": 1, "response": {"data": [], "metadata": []}}) as exec_mock, \
             patch("helicalbi.core.flows.SqlExecutor.invoke_llm", side_effect=_fake_insight), \
             patch.object(SqlExecutor, "_repair_sql", return_value="SELECT 1") as repair_mock:
            state = SqlExecutor().process_flow(_base_state())

        assert exec_mock.call_count == 1
        assert repair_mock.call_count == 0
        assert state["sql_error"] == "Not Generated"
        assert not state.get("skip")
