"""Tests for think-step answer via data-insight style LLM."""

from unittest.mock import MagicMock

import pytest

from helicalbi.sql_agent.tools.analysis import AnalysisTools, StepInsightOutput

pytestmark = pytest.mark.functional


def test_analyze_uses_llm_insight_like_data_insight():
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
            }
        },
    }

    result = tools.analyze(
        "",
        state,
        question="What is the total travel cost?",
        title="Total Travel Cost",
        _invoke=invoke,
    )

    assert result["ok"] is True
    assert result["answer"] == "Total travel cost is 18,533,970 for the period."
    assert "baseline" in result["analysis"]
    assert "[" not in result["answer"]
    invoke.assert_called_once()
    kwargs = invoke.call_args
    # prompt template is first positional; inputs dict second
    inputs = kwargs.args[1]
    assert "18533970" in inputs["sample_data"] or "Travel Cost" in inputs["sample_data"]
    step = result["state_patch"]["collected_data"][-1]
    assert step["answer"] == result["answer"]
    assert step["chat_response"]["summary"]["insight"] == result["answer"]


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
    result = tools.analyze(
        "[{'Travel Cost': 18533970}]",
        state,
        question="What is the total travel cost?",
        title="Total Travel Cost",
        _invoke=_boom,
    )
    assert result["ok"] is True
    assert "18,533,970" in result["answer"]
    assert not result["answer"].lstrip().startswith("[")
