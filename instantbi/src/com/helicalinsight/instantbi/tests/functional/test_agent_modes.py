"""Agent mode token profiles."""

from helicalbi.sql_agent.modes import (
    DASHBOARD_NODES_PER_TOOL_LOOP,
    MODE_BALANCED,
    MODE_FAST,
    MODE_RESEARCH,
    normalize_mode,
    recursion_limit_for_loops,
    resolve_mode_profile,
    truncate_text,
)
from helicalbi.sql_agent.nodes.synthesizer import synthesizer_node
from helicalbi.sql_agent.state import initial_agent_state


def test_normalize_mode_aliases():
    assert normalize_mode("FAST") == MODE_FAST
    assert normalize_mode("quick") == MODE_FAST
    assert normalize_mode("deep") == MODE_RESEARCH
    assert normalize_mode("nope") == MODE_BALANCED


def test_recursion_limit_covers_full_tool_loop_graph():
    balanced = resolve_mode_profile(MODE_BALANCED)
    fast = resolve_mode_profile(MODE_FAST)
    research = resolve_mode_profile(MODE_RESEARCH)
    for profile in (fast, balanced, research):
        needed = recursion_limit_for_loops(profile.max_tool_loops)
        assert profile.recursion_limit >= needed
        assert needed > profile.max_tool_loops * DASHBOARD_NODES_PER_TOOL_LOOP


def test_fast_mode_is_cheaper_than_research():
    fast = resolve_mode_profile(MODE_FAST, config_max_charts=5)
    research = resolve_mode_profile(MODE_RESEARCH, config_max_charts=8)
    assert fast.max_charts < research.max_charts
    assert fast.max_tool_loops < research.max_tool_loops
    assert fast.overview_chars < research.overview_chars
    assert fast.use_llm_synthesizer is False
    assert research.use_llm_synthesizer is True


def test_config_ceiling_caps_mode_charts():
    profile = resolve_mode_profile(MODE_RESEARCH, config_max_charts=3)
    assert profile.max_charts == 3
    assert profile.name == MODE_RESEARCH


def test_truncate_text():
    assert truncate_text("hello", 100) == "hello"
    assert truncate_text("abcdefghij", 8).endswith("[truncated]")


def test_fast_synthesizer_skips_llm(monkeypatch):
    called = {"llm": 0}

    def boom(*args, **kwargs):
        called["llm"] += 1
        raise AssertionError("LLM synthesizer should not run in fast mode")

    monkeypatch.setattr("helicalbi.sql_agent.nodes.synthesizer.invoke_agent_model", boom)
    state = initial_agent_state(
        "Why is cost high?",
        agent_mode=MODE_FAST,
        use_llm_synthesizer=False,
        max_tool_loops=10,
    )
    state["collected_data"] = [
        {
            "sub_question": "Total cost KPI",
            "analysis": "Cost is 10",
            "include_in_dashboard": True,
        }
    ]
    state["tool_loop_count"] = 4
    result = synthesizer_node(state)
    assert called["llm"] == 0
    assert "Total cost KPI" in result["final_answer"]
    assert result["attempt_count"] == 4
    assert result["asked_questions"] == ["Total cost KPI"]
