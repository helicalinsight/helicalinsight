"""Token-budget helpers: compact tools, trim history, chart cap."""

import json

from langchain_core.messages import AIMessage, HumanMessage, SystemMessage, ToolMessage

from helicalbi.sql_agent.modes import MODE_FAST, resolve_mode_profile
from helicalbi.sql_agent.nodes.apply_patches import apply_tool_patches
from helicalbi.sql_agent.token_budget import (
    charts_complete,
    compact_tool_payload,
    llm_tool_payload,
    trim_planner_history,
)
from helicalbi.sql_agent.tools.tools import planner_tools


def test_compact_tool_payload_keeps_state_patch():
    payload = compact_tool_payload(
        {
            "ok": True,
            "model_context": "x" * 2000,
            "schema": "y" * 2000,
            "state_patch": {"generated_sql": "SELECT 1", "session_context": {"a": 1}},
        }
    )
    assert payload["state_patch"]["generated_sql"] == "SELECT 1"
    assert len(payload["model_context"]) < 2000
    assert "[truncated]" in payload["model_context"]


def test_llm_tool_payload_drops_state_patch():
    payload = llm_tool_payload(
        {"ok": True, "sql": "SELECT 1", "state_patch": {"generated_sql": "SELECT 1"}}
    )
    assert "state_patch" not in payload
    assert payload["state_applied"] is True


def test_trim_planner_history_keeps_last_rounds():
    messages = [
        SystemMessage(content="sys"),
        HumanMessage(content="q"),
    ]
    for i in range(6):
        messages.append(
            AIMessage(content="", tool_calls=[{"name": "generate_sql", "args": {}, "id": f"c{i}"}])
        )
        messages.append(ToolMessage(content="{}", tool_call_id=f"c{i}"))
    trimmed = trim_planner_history(messages, keep_tool_rounds=2)
    assert isinstance(trimmed[0], SystemMessage)
    assert isinstance(trimmed[1], HumanMessage)
    assert any("omitted" in str(m.content) for m in trimmed if isinstance(m, SystemMessage))
    tool_msgs = [m for m in trimmed if isinstance(m, ToolMessage)]
    assert len(tool_msgs) == 2


def test_charts_complete():
    assert charts_complete({"max_sub_questions": 2, "collected_data": []}) is False
    assert (
        charts_complete(
            {
                "max_sub_questions": 1,
                "collected_data": [{"include_in_dashboard": True}],
            }
        )
        is True
    )


def test_apply_tool_patches_compacts_and_sets_complete_on_chart_cap():
    result = apply_tool_patches(
        {
            "max_sub_questions": 1,
            "collected_data": [],
            "messages": [
                AIMessage(
                    content="",
                    tool_calls=[{"name": "build_report", "args": {}, "id": "1"}],
                    id="ai-1",
                ),
                ToolMessage(
                    content=json.dumps(
                        {
                            "ok": True,
                            "model_context": "huge " * 200,
                            "state_patch": {
                                "collected_data": [
                                    {
                                        "sub_question": "KPI",
                                        "include_in_dashboard": True,
                                    }
                                ]
                            },
                        }
                    ),
                    tool_call_id="1",
                    id="tm-1",
                ),
            ],
        }
    )
    assert result["is_complete"] is True
    assert len(result["collected_data"]) == 1
    assert "messages" in result
    compact = json.loads(result["messages"][0].content)
    assert "state_patch" not in compact
    assert compact["state_applied"] is True


def test_fast_planner_tools_exclude_validate_and_analyze():
    names = {getattr(t, "name", "") for t in planner_tools(MODE_FAST)}
    assert "generate_sql" in names
    assert "validate_sql" not in names
    assert "analyze_result" not in names
    assert "retrieve_schema" not in names
    assert resolve_mode_profile(MODE_FAST).history_tool_rounds == 2
