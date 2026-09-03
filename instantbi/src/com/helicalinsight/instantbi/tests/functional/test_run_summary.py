"""Programmatic asked-questions / attempt-count summary."""

from helicalbi.sql_agent.nodes.run_summary import (
    append_question,
    asked_questions_from_state,
    attempt_count_from_state,
    build_run_summary,
)


def test_build_run_summary_from_tracked_questions():
    summary = build_run_summary(
        {
            "asked_questions": ["Total sales", "Total sales", "Sales by region"],
            "tool_loop_count": 7,
            "collected_data": [
                {
                    "sub_question": "Total sales",
                    "include_in_dashboard": True,
                    "analysis": "Sales are 10",
                },
                {
                    "sub_question": "Sales by region",
                    "include_in_dashboard": False,
                    "analysis": "West leads",
                },
            ],
        }
    )
    assert summary["asked_questions"] == ["Total sales", "Sales by region"]
    assert summary["attempt_count"] == 7
    assert summary["question_count"] == 2
    assert summary["investigation_steps"] == [
        {
            "step": 1,
            "question": "Total sales",
            "kind": "chart",
            "analysis": "Sales are 10",
        },
        {
            "step": 2,
            "question": "Sales by region",
            "kind": "lookup",
            "analysis": "West leads",
        },
    ]


def test_asked_questions_fallback_to_collected_data():
    questions = asked_questions_from_state(
        {
            "asked_questions": [],
            "collected_data": [
                {"sub_question": "KPI total"},
                {"sub_question": "Breakdown by region"},
            ],
        }
    )
    assert questions == ["KPI total", "Breakdown by region"]


def test_append_question_and_attempt_count():
    assert append_question([], "  Hello ") == ["Hello"]
    assert append_question(["Hello"], "") == ["Hello"]
    assert attempt_count_from_state({"tool_loop_count": 3}) == 3
    assert attempt_count_from_state({}) == 0
