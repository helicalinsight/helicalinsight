"""Tests for think-mode llm activity details helpers."""

from helicalbi.sql_agent.activity_details import (
    build_llm_activity_details,
    strategy_selection_reason,
)

import pytest

pytestmark = pytest.mark.functional


def test_strategy_selection_reason_decision_tree():
    reason = strategy_selection_reason(
        {
            "id": "mece_drivers",
            "selection": {
                "source": "decision_tree",
                "intent": "overview",
                "persona": "analyst",
            },
        }
    )
    assert "mece_drivers" in reason
    assert "overview" in reason
    assert "analyst" in reason


def test_build_llm_activity_details_shape():
    details = build_llm_activity_details(
        persona={"name": "analyst"},
        strategy={
            "id": "mece_drivers",
            "selection": {"source": "hint", "intent": "trend"},
        },
        selected_domains=["Travel"],
        selected_topics=["Travel Cost"],
        selected_tables=["travel_details"],
        plan={
            "rationale": "Understand spend drivers.",
            "charts": [
                {
                    "question": "What is total cost?",
                    "purpose": "Headline KPI",
                    "topic": "Travel Cost",
                    "components": ["Travel Cost"],
                }
            ],
        },
        plan_graph=[{"node": "select_domain_topics", "reason": "picked travel"}],
        execute_graph=[{"node": "execute_plan", "reason": "ran charts"}],
        question_history=[
            {"index": 1, "analysis": "Cost is 120k", "answer": "120k"}
        ],
        cited_question_indexes=[1],
        final_answer="Cost is 120k (Question 1).",
    )
    assert details["domains"] == ["Travel"]
    assert details["tables"] == ["travel_details"]
    assert details["questions"][0]["why"] == "Headline KPI"
    assert details["questions"][0]["finding"] == "Cost is 120k"
    assert any(step.get("stage") == "tables" for step in details["path"])
    assert any(step.get("stage") == "final_answer" for step in details["path"])
