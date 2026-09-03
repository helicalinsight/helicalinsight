"""Agent InstantBI SQL/viz branching tests."""

from helicalbi.sql_agent.instantbi_agent_graph import (
    has_agent_topics,
    prepare_node,
    route_after_prepare,
    route_after_seed,
    seed_domain_node,
)
from helicalbi.sql_agent.modes import MODE_BALANCED, MODE_FAST, MODE_RESEARCH, resolve_mode_profile


def test_has_agent_topics():
    assert has_agent_topics(["Sales"]) is True
    assert has_agent_topics([]) is False
    assert has_agent_topics(None, model_state={"got_domain": True, "topics": ["T1"]}) is True
    assert has_agent_topics(None, model_state={"got_domain": False, "topics": ["T1"]}) is False


def test_route_after_prepare_and_seed():
    assert route_after_prepare({"has_seeded_topics": True}) == "seed_domain"
    assert route_after_prepare({"has_seeded_topics": False}) == "main_graph"
    assert route_after_seed({"skip_intent_rephrase": True}) == "sql_generator"
    assert route_after_seed({"skip_intent_rephrase": False}) == "rephrase"


def test_prepare_skips_rephrase_for_fast_when_seeded():
    out = prepare_node(
        {
            "agent_mode": MODE_FAST,
            "selected_topics": ["Travel"],
            "model_state": {"query": "cost by region"},
            "session": {},
        }
    )
    assert out["has_seeded_topics"] is True
    assert out["skip_intent_rephrase"] is True
    assert route_after_prepare(out) == "seed_domain"
    assert route_after_seed(out) == "sql_generator"


def test_prepare_keeps_rephrase_for_research_when_seeded():
    out = prepare_node(
        {
            "agent_mode": MODE_RESEARCH,
            "selected_topics": ["Travel"],
            "model_state": {"query": "cost by region"},
            "session": {},
        }
    )
    assert out["has_seeded_topics"] is True
    assert out["skip_intent_rephrase"] is False
    assert route_after_seed(out) == "rephrase"


def test_prepare_uses_main_graph_without_topics():
    out = prepare_node(
        {
            "agent_mode": MODE_BALANCED,
            "selected_topics": [],
            "model_state": {"query": "cost by region"},
            "session": {},
        }
    )
    assert out["has_seeded_topics"] is False
    assert out["branch"] == "main_graph"
    assert route_after_prepare(out) == "main_graph"


def test_seed_domain_sets_got_domain():
    out = seed_domain_node(
        {
            "selected_topics": ["Travel Cost"],
            "selected_domains": ["Travel"],
            "model_state": {
                "query": "cost by region",
                "topic_mappings": [
                    {"topic_name": "Travel Cost", "columns": ["cost"]},
                    {"topic_name": "Other", "columns": ["x"]},
                ],
            },
            "session": {"use_cube_info_flow": False, "cube_info_prepared": {}},
        }
    )
    state = out["model_state"]
    assert state["got_domain"] is True
    assert state["topics"] == ["Travel Cost"]
    assert state["domain"] == ["Travel"]
    assert state["sql_query"] == "cost by region"
    assert state["action"] == "none"
    assert len(state["topic_mappings"]) == 1
    assert state["topic_mappings"][0]["topic_name"] == "Travel Cost"


def test_mode_flags():
    fast = resolve_mode_profile(MODE_FAST)
    balanced = resolve_mode_profile(MODE_BALANCED)
    research = resolve_mode_profile(MODE_RESEARCH)
    assert fast.skip_intent_rephrase is True
    assert fast.skip_viz_polish is True
    assert balanced.skip_intent_rephrase is True
    assert balanced.skip_viz_polish is False
    assert research.skip_intent_rephrase is False
    assert research.skip_viz_polish is False


def test_balanced_seeded_path_skips_rephrase_like_fast():
    out = prepare_node(
        {
            "agent_mode": MODE_BALANCED,
            "selected_topics": ["KPI"],
            "model_state": {"query": "total sales"},
            "session": {},
        }
    )
    assert route_after_prepare(out) == "seed_domain"
    assert route_after_seed(out) == "sql_generator"
