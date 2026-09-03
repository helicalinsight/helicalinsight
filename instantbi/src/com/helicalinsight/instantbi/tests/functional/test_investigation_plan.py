"""Persona mapping, plan memory, and investigation planner fallbacks."""

import pytest

from helicalbi.common.ChatGraphMemory import chat_graph_memory
from helicalbi.sql_agent.investigation import (
    PLAN_READY_MESSAGE,
    create_and_store_plan,
    is_execute_plan_request,
    stored_plan_or_raise,
)
from helicalbi.sql_agent.models import DashboardChartSpec, InvestigationPlan
from helicalbi.sql_agent.nodes.investigation_planner import (
    build_investigation_plan,
    fallback_investigation_plan,
)
from helicalbi.sql_agent.personas import (
    PERSONA_ANALYST,
    PERSONA_EXECUTIVE,
    PERSONA_OPERATIONAL,
    PERSONA_TACTICAL,
    STRATEGY_INVERTED_PYRAMID,
    STRATEGY_MECE,
    STRATEGY_PROGRESSIVE_DISCLOSURE,
    STRATEGY_SEMANTIC_COLOR,
    resolve_persona,
)
from helicalbi.sql_agent.plan_memory import has_plan, load_plan, save_plan


pytestmark = pytest.mark.functional


@pytest.fixture(autouse=True)
def _clear_plan_memory():
    chat_graph_memory.clear()
    yield
    chat_graph_memory.clear()


def test_is_execute_plan_request():
    assert is_execute_plan_request("execute plan") is True
    assert is_execute_plan_request("  Execute   Plan  ") is True
    assert is_execute_plan_request("run_plan") is True
    assert is_execute_plan_request("compare sales", {"action": "execute"}) is True
    assert is_execute_plan_request("compare sales") is False
    assert is_execute_plan_request("execute a campaign") is False


def test_resolve_persona_from_role_and_profile():
    executive = resolve_persona([{"id": 1, "roleName": "CFO"}])
    assert executive["name"] == PERSONA_EXECUTIVE

    operational = resolve_persona(["Supply Chain Lead"])
    assert operational["name"] == PERSONA_OPERATIONAL

    tactical = resolve_persona([], [{"name": "title", "value": "Product Owner"}])
    assert tactical["name"] == PERSONA_TACTICAL

    analyst = resolve_persona(["ROLE_USER"], [{"name": "persona", "value": "Data Analyst"}])
    assert analyst["name"] == PERSONA_ANALYST

    hinted = resolve_persona(["ROLE_USER"], hint="executive")
    assert hinted["name"] == PERSONA_EXECUTIVE


def test_decision_tree_picks_one_strategy():
    from helicalbi.sql_agent.strategy_tree import (
        classify_intent,
        select_strategy,
    )

    ceo = resolve_persona(["CEO"])
    revenue = select_strategy("Are we meeting our monthly revenue targets?", persona=ceo)
    assert classify_intent("Are we meeting our monthly revenue targets?") == "target"
    assert revenue["id"] == STRATEGY_INVERTED_PYRAMID
    assert revenue["selection"]["source"] == "decision_tree"

    analyst = resolve_persona(["Data Analyst"])
    attrition = select_strategy("Why is engineering team attrition spiking?", persona=analyst)
    assert attrition["id"] == STRATEGY_PROGRESSIVE_DISCLOSURE

    stock = select_strategy(
        "Which fulfillment centers are running out of stock?",
        persona=resolve_persona(["Product Owner"]),
    )
    assert stock["id"] == STRATEGY_SEMANTIC_COLOR

    tactical = resolve_persona(["Product Owner"])
    margins = select_strategy("What factors are squeezing our profit margins?", persona=tactical)
    assert margins["id"] == STRATEGY_MECE

    forced = select_strategy("any question", persona=ceo, hint="mece_metric_structuring")
    assert forced["id"] == STRATEGY_MECE
    assert forced["selection"]["source"] == "hint"


def test_fallback_plan_uses_inverted_pyramid_for_executives():
    persona = resolve_persona(["CEO"])
    plan = fallback_investigation_plan(
        "Are we meeting our monthly revenue targets?",
        persona=persona,
        max_charts=5,
    )
    assert plan.persona == PERSONA_EXECUTIVE
    assert [chart.viz_hint for chart in plan.charts] == ["kpi", "line", "table"]
    assert plan.charts[0].context_anchor
    assert "headline KPI for:" not in plan.charts[0].question.lower()
    assert "{question}" not in plan.charts[0].question


def test_build_investigation_plan_uses_llm_then_caps_charts(monkeypatch):
    parsed = InvestigationPlan(
        persona=PERSONA_TACTICAL,
        tier="tactical",
        strategies=[STRATEGY_MECE],
        original_question="What factors are squeezing our profit margins?",
        charts=[
            DashboardChartSpec(title=f"C{i}", question=f"q{i}", viz_hint="kpi")
            for i in range(8)
        ],
    )
    monkeypatch.setattr(
        "helicalbi.sql_agent.nodes.investigation_planner.invoke_agent_model",
        lambda *args, **kwargs: parsed,
    )
    persona = resolve_persona(["Marketing Manager"])
    plan = build_investigation_plan(
        "What factors are squeezing our profit margins?",
        persona=persona,
        semantic_overview="Domain: SaaS Finance",
        max_charts=3,
    )
    assert len(plan.charts) == 3
    assert plan.persona == PERSONA_TACTICAL
    assert plan.strategy_id == STRATEGY_MECE


def test_build_investigation_plan_adapts_to_similar_strategy(monkeypatch):
    parsed = InvestigationPlan(
        persona=PERSONA_TACTICAL,
        tier="tactical",
        strategies=[STRATEGY_PROGRESSIVE_DISCLOSURE],
        strategy_id=STRATEGY_PROGRESSIVE_DISCLOSURE,
        original_question="Why did travel spend change?",
        rationale="MECE driver split is not in the travel model; use drill-down instead.",
        charts=[
            DashboardChartSpec(
                title="Total travel spend",
                question="What is total travel cost this period?",
                viz_hint="kpi",
            ),
            DashboardChartSpec(
                title="Spend by route",
                question="What is travel cost by origin and destination?",
                viz_hint="bar",
            ),
        ],
        layout_guidance="KPI then route breakdown",
    )
    monkeypatch.setattr(
        "helicalbi.sql_agent.nodes.investigation_planner.invoke_agent_model",
        lambda *args, **kwargs: parsed,
    )
    persona = resolve_persona(["Marketing Manager"])
    plan = build_investigation_plan(
        "Why did travel spend change?",
        persona=persona,
        semantic_overview="Domain: Travel Spend. Topics: Travel Routes, Trip Economics.",
        max_charts=5,
    )
    assert plan.strategy_id == STRATEGY_PROGRESSIVE_DISCLOSURE
    assert plan.charts[1].question == "What is travel cost by origin and destination?"
    assert plan.template_id == "drilldown-hierarchical"


def test_build_investigation_plan_keeps_hinted_strategy(monkeypatch):
    from helicalbi.sql_agent.strategy_tree import select_strategy

    parsed = InvestigationPlan(
        persona=PERSONA_EXECUTIVE,
        tier="strategic",
        strategies=[STRATEGY_PROGRESSIVE_DISCLOSURE],
        strategy_id=STRATEGY_PROGRESSIVE_DISCLOSURE,
        charts=[DashboardChartSpec(title="KPI", question="What is total travel cost?", viz_hint="kpi")],
    )
    monkeypatch.setattr(
        "helicalbi.sql_agent.nodes.investigation_planner.invoke_agent_model",
        lambda *args, **kwargs: parsed,
    )
    persona = resolve_persona(["CEO"])
    hinted = select_strategy("Why did spend change?", persona=persona, hint=STRATEGY_MECE)
    plan = build_investigation_plan(
        "Why did spend change?",
        persona=persona,
        strategy=hinted,
        max_charts=3,
    )
    assert plan.strategy_id == STRATEGY_MECE
    assert plan.charts[0].question == "What is total travel cost?"


def test_plan_prompt_allows_similar_line_adaptation():
    from helicalbi.sql_agent.config import CONTEXT_PLAN_PROMPT

    assert "{strategy_catalog}" in CONTEXT_PLAN_PROMPT
    assert "closest equivalent" in CONTEXT_PLAN_PROMPT
    assert "poor fit" in CONTEXT_PLAN_PROMPT
    assert "reference only" in CONTEXT_PLAN_PROMPT
    assert "Never copy" in CONTEXT_PLAN_PROMPT


def test_strategy_prompt_omits_question_templates():
    from helicalbi.sql_agent.strategy_tree import get_strategy, strategy_prompt_block

    block = strategy_prompt_block(get_strategy(STRATEGY_MECE))
    assert "Layout template name: analytical-grid" in block
    assert "question_template" not in block
    assert "What is the primary outcome KPI for:" not in block
    assert "{question}" not in block


def test_build_investigation_plan_falls_back_when_llm_fails(monkeypatch):
    def boom(*args, **kwargs):
        raise RuntimeError("llm down")

    monkeypatch.setattr(
        "helicalbi.sql_agent.nodes.investigation_planner.invoke_agent_model",
        boom,
    )
    persona = resolve_persona(["CMO"])
    plan = build_investigation_plan("Are we meeting targets?", persona=persona, max_charts=2)
    assert len(plan.charts) == 2
    assert plan.charts[0].viz_hint == "kpi"


def test_plan_memory_round_trip():
    save_plan("dash-1", "1", {"original_question": "q", "plan": {"charts": []}})
    assert has_plan("dash-1", "1") is True
    loaded = load_plan("dash-1", "1")
    assert loaded["kind"] == "dashboard_investigation_plan"
    assert loaded["original_question"] == "q"
    assert load_plan("dash-1", "2") is None


def test_create_and_store_plan(monkeypatch):
    monkeypatch.setattr(
        "helicalbi.sql_agent.investigation.load_model_session",
        lambda **kwargs: {"semantic_overview": "Domain: Sales / Revenue"},
    )
    monkeypatch.setattr(
        "helicalbi.sql_agent.investigation.build_investigation_plan",
        lambda question, **kwargs: fallback_investigation_plan(
            question,
            persona=kwargs["persona"],
            max_charts=kwargs["max_charts"],
        ),
    )
    result = create_and_store_plan(
        "Are we meeting our monthly revenue targets?",
        session_cookie="cookie",
        username="cfo",
        model_file_name="model.json",
        model_location="/models",
        thread_id="dash-plan",
        chat_seq_id="1",
        user_role=[{"roleName": "CFO"}],
        user_profile=[{"name": "dept", "value": "finance"}],
    )
    assert result["phase"] == "plan"
    assert result["persona"]["name"] == PERSONA_EXECUTIVE
    assert result["strategy"] == STRATEGY_INVERTED_PYRAMID
    assert "strategies" not in result["persona"]
    assert "strategy" not in result["persona"]
    assert "strategy_guide" not in result["persona"]
    assert "template_id" not in result["persona"]
    assert "question_template" not in str(result)
    assert result["plan"]["strategy_id"] == STRATEGY_INVERTED_PYRAMID
    assert result["plan"]["template_id"] == "executive-kpi-first"
    assert "strategies" not in result["plan"]
    assert "layout_guidance" not in result["plan"]
    stored = stored_plan_or_raise("dash-plan", "1")
    assert stored["plan"]["template_id"] == "executive-kpi-first"
    assert stored["plan"]["strategy_id"] == STRATEGY_INVERTED_PYRAMID
    assert "question_template" not in str(stored)
    assert stored["original_question"] == "Are we meeting our monthly revenue targets?"
    assert result["message"] == PLAN_READY_MESSAGE
    with pytest.raises(ValueError, match="No stored plan"):
        stored_plan_or_raise("dash-plan", "99")
