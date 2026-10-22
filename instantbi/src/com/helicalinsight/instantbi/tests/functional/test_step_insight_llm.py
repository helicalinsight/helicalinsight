"""Tests for think-step answer: SqlSuccess insight (no data) + findings analysis."""

from unittest.mock import MagicMock, patch

import pytest

from helicalbi.sql_agent.tools.analysis import (
    AnalysisTools,
    StepInsightOutput,
    apply_sql_success_insight,
    sql_success_insight,
)

pytestmark = pytest.mark.functional


def test_analyze_summary_insight_uses_sql_success_without_data():
    tools = AnalysisTools()
    invoke = MagicMock(
        return_value=StepInsightOutput(
            answer="Total travel cost is 18,533,970 for the period.",
            analysis=(
                "This headline spend figure shows the full travel outlay in scope. "
                "Leaders can use it as the baseline before drilling into type or channel."
            ),
        )
    )
    success_text = "This looks at overall travel spend for the selected period."
    state = {
        "current_sub_question": "What is the total travel cost?",
        "generated_sql": 'SELECT SUM("Travel Cost") AS "Travel Cost" FROM travel',
        "query_result": "[{'Travel Cost': 18533970}]",
        "selected_domains": ["Travel"],
        "selected_topics": ["Cost"],
        "username": "alex",
        "chat_seq_id": "1",
        "collected_data": [],
        "token_usage": {},
        "session_context": {
            "_last_sql_state": {
                "data": [{"Travel Cost": 18533970}],
                "output": success_text,
                "required_tables": ["travel_details"],
                "domain": ["Travel"],
                "topics": ["Cost"],
            }
        },
    }

    result = tools.analyze(
        "",
        state,
        question="What is the total travel cost?",
        title="Total Travel Cost",
        _invoke=invoke,
        _invoke_llm=lambda *args, **kwargs: (_ for _ in ()).throw(
            AssertionError("should reuse SqlExecutor output")
        ),
    )

    assert result["ok"] is True
    assert "answer" not in result
    assert "baseline" in result["analysis"]
    invoke.assert_called_once()
    inputs = invoke.call_args.args[1]
    assert "18533970" in inputs["sample_data"] or "Travel Cost" in inputs["sample_data"]
    step = result["state_patch"]["collected_data"][-1]
    assert "answer" not in step
    assert step["chat_response"]["summary"]["insight"] == success_text
    assert "18,533,970" not in step["chat_response"]["summary"]["insight"]
    assert "baseline" in step["analysis"]


def test_sql_success_insight_reuses_executor_output():
    last_sql = {"output": "This view shows travel spend by destination."}
    text = sql_success_insight(
        question="Spend by destination?",
        sql="SELECT destination, SUM(cost) FROM travel GROUP BY destination",
        state={},
        last_sql=last_sql,
        _invoke_llm=lambda *args, **kwargs: (_ for _ in ()).throw(
            AssertionError("should reuse output")
        ),
    )
    assert text == "This view shows travel spend by destination."


def test_apply_sql_success_insight_keeps_analysis_and_sets_insight():
    step = apply_sql_success_insight(
        {
            "chat_response": {
                "summary": {"insight": "This breaks travel cost down by month."}
            },
            "analysis": "March was the peak month at 2.1M.",
        },
        {"selected_domains": ["Travel"], "selected_topics": ["Cost"]},
        question="Cost by month?",
        sql="SELECT month, SUM(cost) FROM travel GROUP BY month",
        last_sql={"data": [{"month": "March", "cost": 2100000}]},
        _invoke_llm=lambda *args, **kwargs: (_ for _ in ()).throw(
            AssertionError("should reuse existing insight")
        ),
    )
    assert "answer" not in step
    assert step["chat_response"]["summary"]["insight"] == "This breaks travel cost down by month."
    assert step["analysis"] == "March was the peak month at 2.1M."


def test_sql_success_prompt_omits_result_rows():
    from helicalbi.core.flows.SqlExecutor import generate_sql_success_insight
    from helicalbi.prompt.SqlSuccessPrompty import success_prompt_formatted

    captured = {}

    def _capture(formatted, state=None):
        captured["formatted"] = formatted
        msg = MagicMock()
        msg.content = "This looks at travel spend by month."
        return msg, None

    text = generate_sql_success_insight(
        "Cost by month?",
        "SELECT month, SUM(cost) FROM travel GROUP BY month",
        {"required_tables": ["travel_details"], "domain": ["Travel"], "topics": ["Cost"]},
        _invoke_llm=_capture,
    )
    assert text == "This looks at travel spend by month."
    formatted = captured["formatted"]
    assert "2100000" not in formatted
    assert "sample_data" not in formatted
    assert "Cost by month?" in formatted
    assert "SELECT month, SUM(cost) FROM travel GROUP BY month" in formatted
    assert set(success_prompt_formatted.input_variables) == {
        "user_query",
        "sql_query",
        "metadata",
    }


def test_analyze_falls_back_when_llm_fails():
    tools = AnalysisTools()

    def _boom(*_args, **_kwargs):
        raise RuntimeError("llm down")

    state = {
        "current_sub_question": "What is the total travel cost?",
        "generated_sql": "SELECT 1",
        "query_result": "[{'Travel Cost': 18533970}]",
        "collected_data": [],
        "chat_seq_id": "1",
        "session_context": {},
        "token_usage": {},
    }
    with patch(
        "helicalbi.sql_agent.tools.analysis.sql_success_insight",
        return_value="",
    ):
        result = tools.analyze(
            "[{'Travel Cost': 18533970}]",
            state,
            question="What is the total travel cost?",
            title="Total Travel Cost",
            _invoke=_boom,
        )
    assert result["ok"] is True
    step = result["state_patch"]["collected_data"][-1]
    assert "answer" not in step
    assert "18,533,970" in step["chat_response"]["summary"]["insight"]
    assert not step["chat_response"]["summary"]["insight"].lstrip().startswith("[")
