"""SQL rewrite-retry helper used by think and fast/normal execute."""
import pytest

from helicalbi.sql.sql_retry import (
    SQL_RETRY_MAX,
    execute_sql_with_retries,
    rewrite_failed_sql_prompt,
    sql_execution_failed,
)


pytestmark = pytest.mark.functional


def test_rewrite_prompt_keeps_original_question_and_error():
    prompt = rewrite_failed_sql_prompt(
        "Total travel cost",
        "SELECT missing_col FROM t",
        "Unknown column missing_col",
    )
    assert prompt.startswith("Total travel cost")
    assert "SELECT missing_col FROM t" in prompt
    assert "Unknown column missing_col" in prompt
    assert "Previous SQL that failed" in prompt
    assert "derived tables" in prompt


def test_rewrite_prompt_forbids_subquery_in_think_mode():
    prompt = rewrite_failed_sql_prompt(
        "Travel cost above average",
        "SELECT t.cost FROM t WHERE t.cost > (SELECT AVG(t.cost) FROM t)",
        "Subqueries are not allowed in think mode.",
        flat_sql=True,
    )
    assert "forbids subqueries" in prompt
    assert "Do not use CTEs, derived tables" in prompt


def test_sql_execution_failed_ignores_placeholder():
    assert sql_execution_failed({"sql_error": "Not Generated"}) is False
    assert sql_execution_failed({"sql_error": ""}) is False
    assert sql_execution_failed({"sql_error": "syntax error"}) is True


def test_retries_until_success_and_restores_query():
    calls = {"execute": 0, "regen": 0}

    def execute(state):
        calls["execute"] += 1
        if calls["execute"] < 3:
            state["sql_error"] = f"boom {calls['execute']}"
            state["sql"] = f"SELECT fail_{calls['execute']}"
            state["skip"] = True
            return state
        state["sql_error"] = "Not Generated"
        state["sql"] = "SELECT 1"
        state["skip"] = False
        return state

    def regenerate(state, prompt):
        calls["regen"] += 1
        assert "Total travel cost" in prompt
        assert "Previous SQL that failed" in prompt
        assert state["query"] == "Total travel cost"
        state["sql"] = f"SELECT retry_{calls['regen']}"
        return state

    result = execute_sql_with_retries(
        {"query": "Total travel cost", "sql": "SELECT fail_0"},
        execute=execute,
        regenerate=regenerate,
        original_question="Total travel cost",
    )
    assert calls["execute"] == 3
    assert calls["regen"] == 2
    assert result["query"] == "Total travel cost"
    assert result["sql"] == "SELECT 1"
    assert result.get("_defer_sql_insight") is None
    assert sql_execution_failed(result) is False


def test_success_on_first_execute_does_not_regenerate():
    def execute(state):
        state["sql_error"] = "Not Generated"
        state["sql_result"] = {"data": [{"a": 1}]}
        return state

    result = execute_sql_with_retries(
        {"query": "Show sales", "sql": "SELECT a FROM t"},
        execute=execute,
        regenerate=lambda state, prompt: (_ for _ in ()).throw(AssertionError(prompt)),
        original_question="Show sales",
    )
    assert result["query"] == "Show sales"
    assert result["sql_result"]["data"] == [{"a": 1}]


def test_gives_up_after_max_attempts_without_mutating_question():
    def execute(state):
        state["sql_error"] = "still broken"
        state["sql"] = "SELECT bad"
        return state

    regen = {"n": 0}

    def regenerate(state, prompt):
        regen["n"] += 1
        return state

    result = execute_sql_with_retries(
        {"query": "Show sales"},
        execute=execute,
        regenerate=regenerate,
        original_question="Show sales",
    )
    assert SQL_RETRY_MAX == 3
    assert regen["n"] == SQL_RETRY_MAX - 1
    assert result["query"] == "Show sales"
    assert result["sql_error"] == "still broken"
    assert result.get("_defer_sql_insight") is None
