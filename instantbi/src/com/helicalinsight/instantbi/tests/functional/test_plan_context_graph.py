"""Context-aware plan graph grounding for aigen-like travel acquisition models."""

from __future__ import annotations

import pytest

from helicalbi.sql_agent.database.semantic_indexer import SemanticLayerIndexer
from helicalbi.sql_agent.models import DashboardChartSpec, InvestigationPlan
from helicalbi.sql_agent.nodes.plan_graph import (
    enrich_topic_context_node,
    run_plan_context_graph,
    select_domain_topics_node,
    validate_questions_node,
)
from helicalbi.sql_agent.personas import resolve_persona
from helicalbi.sql_agent.strategy_tree import select_strategy


pytestmark = pytest.mark.functional


def _aigen_like_model() -> dict:
    """Subset of the aigen Client Acquisition Cost semantic model."""
    return {
        "modelName": "aigen",
        "description": "Sales travel spend treated as client acquisition cost.",
        "domain": [
            {
                "domain_name": "Client Acquisition Cost",
                "description": (
                    "Sales travel spend treated as the cost to acquire or pursue clients."
                ),
                "topics": [
                    {
                        "topic": "Client Cost",
                        "description": "Which client was pursued and travel cost spent.",
                        "components": [
                            {"id": "1045", "name": "Client Name"},
                            {"id": "1072", "name": "Travel Cost"},
                            {"id": "eb7e19af", "name": "Failed Acquisition Cost"},
                            {"id": "ec49acb4", "name": "Successful Visit Cost"},
                            {"id": "9c40fdaf", "name": "Cost per Client"},
                        ],
                    },
                    {
                        "topic": "Wasted Acquisition Spend",
                        "description": "Travel cost tied to unsuccessful meetings.",
                        "components": [
                            {"id": "1048", "name": "Meeting Cancellation Status"},
                            {"id": "1049", "name": "Cancellation Reason"},
                            {"id": "1072", "name": "Travel Cost"},
                            {"id": "eb7e19af", "name": "Failed Acquisition Cost"},
                            {"id": "e92d602f", "name": "Meeting Cancellation Rate"},
                        ],
                    },
                    {
                        "topic": "Acquisition Metrics",
                        "description": "Calculated CAC metrics.",
                        "components": [
                            {"id": "eb7e19af", "name": "Failed Acquisition Cost"},
                            {"id": "ec49acb4", "name": "Successful Visit Cost"},
                            {"id": "9c40fdaf", "name": "Cost per Client"},
                            {"id": "e6f403cd", "name": "Successful Meeting Rate"},
                        ],
                    },
                ],
            },
            {
                "domain_name": "Sales Meetings",
                "description": "Client meeting pipeline and success versus failure.",
                "topics": [
                    {
                        "topic": "Failed Visits",
                        "description": "Cancelled or unsuccessful meetings with reasons.",
                        "components": [
                            {"id": "1045", "name": "Client Name"},
                            {"id": "1048", "name": "Meeting Cancellation Status"},
                            {"id": "1049", "name": "Cancellation Reason"},
                            {"id": "eb7e19af", "name": "Failed Acquisition Cost"},
                        ],
                    }
                ],
            },
        ],
        "cube": [
            {
                "dimensions": [
                    {
                        "dimensionName": "Client Name",
                        "columnName": "meeting_details.client_name",
                        "aiContext": {
                            "instructions": "Prospect visited for sales acquisition.",
                            "synonyms": "client, customer, prospect",
                        },
                    },
                    {
                        "dimensionName": "Meeting Cancellation Status",
                        "columnName": "meeting_details.meet_cancellation_status",
                        "aiContext": {
                            "instructions": (
                                "Exact literals only: 'No' means successful meeting. "
                                "'Yes' means not successful / cancelled."
                            ),
                            "synonyms": "cancellation status, cancelled",
                            "examples": "Yes = not successful; No = successful",
                        },
                    },
                    {
                        "dimensionName": "Cancellation Reason",
                        "columnName": "meeting_details.cancellation_reason",
                        "aiContext": {
                            "instructions": "Free-text reason when status is Yes.",
                            "synonyms": "cancel reason, failure reason",
                        },
                    },
                ],
                "measures": [
                    {
                        "measureName": "Travel Cost",
                        "columnName": "travel_details.travel_cost",
                        "aiContext": {
                            "instructions": (
                                "Total travel spend for client visits. Treat as client "
                                "acquisition cost when analyzed with Client Name."
                            ),
                            "synonyms": "trip cost, acquisition cost, CAC travel",
                        },
                    },
                    {
                        "measureName": "Failed Acquisition Cost",
                        "formula": "sum(travel_cost) filter status=Yes",
                        "aiContext": {
                            "instructions": (
                                "Travel cost on unsuccessful meetings "
                                "(meet_cancellation_status='Yes')."
                            ),
                            "synonyms": "wasted CAC, cancelled meeting cost",
                        },
                    },
                    {
                        "measureName": "Successful Visit Cost",
                        "formula": "sum(travel_cost) filter status=No",
                        "aiContext": {
                            "instructions": "Travel cost on successful meetings (status='No').",
                            "synonyms": "productive CAC",
                        },
                    },
                    {
                        "measureName": "Cost per Client",
                        "formula": "sum(travel_cost)/count(distinct client)",
                        "aiContext": {
                            "instructions": "Average travel acquisition spend per client.",
                            "synonyms": "CAC per client",
                        },
                    },
                    {
                        "measureName": "Meeting Cancellation Rate",
                        "aiContext": {
                            "instructions": "Share of meetings that were not successful.",
                            "synonyms": "failure rate",
                        },
                    },
                    {
                        "measureName": "Successful Meeting Rate",
                        "aiContext": {
                            "instructions": "Share of meetings that succeeded.",
                            "synonyms": "success rate, win rate",
                        },
                    },
                ],
            }
        ],
    }


def test_topic_pack_includes_components_and_ai_context():
    indexer = SemanticLayerIndexer()
    indexer.index_model(_aigen_like_model())
    catalog = indexer.topic_catalog()
    topics = {entry["topic"] for entry in catalog}
    assert "Wasted Acquisition Spend" in topics
    assert "Client Cost" in topics

    pack = indexer.topic_pack(["Wasted Acquisition Spend"], max_chars=4000)
    assert "Wasted Acquisition Spend" in pack
    assert "Failed Acquisition Cost" in pack
    assert "Meeting Cancellation Status" in pack
    assert "Yes" in pack or "successful" in pack.lower()

    allowed = indexer.allowed_component_names(["Wasted Acquisition Spend"])
    assert "Failed Acquisition Cost" in allowed
    assert "Travel Cost" in allowed


def test_select_domain_topics_prefers_wasted_spend_for_cac_question():
    indexer = SemanticLayerIndexer()
    model = _aigen_like_model()
    indexer.index_model(model)
    state = {
        "question": "Analyze client acquisition cost and wasted spend on failed visits",
        "topic_catalog": indexer.topic_catalog(),
        "session": {"semantic_indexer": indexer},
        "max_domains": 2,
        "max_topics": 3,
    }
    selected = select_domain_topics_node(state)
    topics = {t.lower() for t in selected["selected_topics"]}
    assert any("wasted" in t or "client cost" in t or "failed" in t for t in topics)
    assert any(
        "acquisition" in d.lower() or "sales" in d.lower()
        for d in selected["selected_domains"]
    )


def test_enrich_topic_context_appends_relation_exploration():
    from helicalbi.sql_agent.database.schema_indexer import SchemaIndexer

    semantic = SemanticLayerIndexer()
    semantic.index_model(_aigen_like_model())
    schema = SchemaIndexer()
    schema.index_from_cube_metadata(
        [
            {
                "database_table": "travel_details",
                "columns": [
                    {"column_name": "travel_cost", "data_type": "numeric"},
                    {"column_name": "travel_type", "data_type": "text"},
                    {"column_name": "travelled_by", "data_type": "integer"},
                ],
            },
            {
                "database_table": "employee_details",
                "columns": [
                    {"column_name": "employee_id", "data_type": "integer"},
                    {"column_name": "department", "data_type": "text"},
                ],
            },
        ],
        [
            {
                "left": {"table": "employee_details", "column": "employee_id"},
                "right": {"table": "travel_details", "column": "travelled_by"},
            }
        ],
    )
    enriched = enrich_topic_context_node(
        {
            "question": "Total travel cost per year",
            "selected_topics": ["Client Cost"],
            "topic_catalog": semantic.topic_catalog(),
            "session": {"semantic_indexer": semantic, "indexer": schema},
            "overview_chars": 4000,
        }
    )
    pack = enriched["grounding_pack"]
    assert "Travel Cost" in pack
    assert "Schema relation exploration" in pack
    assert "travel_type" in pack
    assert "department" in enriched["allowed_names"]


def test_run_plan_context_graph_grounds_asked_questions(monkeypatch):
    indexer = SemanticLayerIndexer()
    model = _aigen_like_model()
    indexer.index_model(model)
    session = {
        "semantic_indexer": indexer,
        "semantic_overview": indexer.overview(),
        "cube_info_prepared": {},
    }
    persona = resolve_persona(["Marketing Manager"])
    strategy = select_strategy(
        "Analyze client acquisition cost and wasted spend",
        persona=persona,
    )

    parsed = InvestigationPlan(
        persona=persona["name"],
        tier="tactical",
        strategies=[strategy["id"]],
        strategy_id=strategy["id"],
        template_id=str(strategy.get("template_id") or ""),
        domain="Client Acquisition Cost",
        topics=["Wasted Acquisition Spend", "Client Cost"],
        original_question="Analyze client acquisition cost and wasted spend",
        rationale="Grounded in CAC topics.",
        charts=[
            DashboardChartSpec(
                title="Travel Cost",
                question="What is total Travel Cost?",
                viz_hint="kpi",
                topic="Client Cost",
                components=["Travel Cost"],
                measure_hints=["Travel Cost"],
            ),
            DashboardChartSpec(
                title="Failed vs Successful",
                question="What is Failed Acquisition Cost vs Successful Visit Cost?",
                viz_hint="bar",
                topic="Wasted Acquisition Spend",
                components=["Failed Acquisition Cost", "Successful Visit Cost"],
                measure_hints=["Failed Acquisition Cost", "Successful Visit Cost"],
            ),
            DashboardChartSpec(
                title="Cost per Client",
                question="What is Cost per Client by Client Name?",
                viz_hint="bar",
                topic="Client Cost",
                components=["Cost per Client", "Client Name"],
                measure_hints=["Cost per Client"],
            ),
            DashboardChartSpec(
                title="AOV",
                question="What is average order value and COGS?",
                viz_hint="kpi",
                topic="Client Cost",
                components=["Average Order Value"],
                measure_hints=["Average Order Value"],
            ),
        ],
    )
    monkeypatch.setattr(
        "helicalbi.sql_agent.nodes.investigation_planner.invoke_agent_model",
        lambda *args, **kwargs: parsed,
    )

    result = run_plan_context_graph(
        "Analyze client acquisition cost and wasted spend",
        persona=persona,
        strategy=strategy,
        session=session,
        max_charts=5,
        overview_chars=4000,
        max_domains=2,
        max_topics=3,
    )
    asked = [
        str(chart.get("question") or "")
        for chart in (result["plan"].get("charts") or [])
    ]
    assert asked
    blob = " ".join(asked).lower()
    assert "travel cost" in blob or "failed acquisition" in blob or "cost per client" in blob
    assert "average order value" not in blob
    assert "cogs" not in blob

    allowed = {name.lower() for name in result["allowed_names"]}
    for chart in result["plan"]["charts"]:
        for hint in chart.get("measure_hints") or []:
            assert hint.lower() in allowed
        for component in chart.get("components") or []:
            assert component.lower() in allowed


def test_validate_rejects_ungrounded_then_fallback_uses_allowed_names():
    indexer = SemanticLayerIndexer()
    indexer.index_model(_aigen_like_model())
    enrich = enrich_topic_context_node(
        {
            "selected_topics": ["Wasted Acquisition Spend"],
            "session": {"semantic_indexer": indexer},
            "topic_catalog": indexer.topic_catalog(),
            "overview_chars": 4000,
        }
    )
    persona = resolve_persona(["Marketing Manager"])
    state = {
        "question": "Why is wasted acquisition spend high?",
        "persona": persona,
        "strategy": select_strategy(
            "Why is wasted acquisition spend high?",
            persona=persona,
        ),
        "selected_domains": ["Client Acquisition Cost"],
        "selected_topics": ["Wasted Acquisition Spend"],
        "allowed_names": enrich["allowed_names"],
        "grounding_pack": enrich["grounding_pack"],
        "max_charts": 3,
        "repair_count": 1,
        "plan": {
            "charts": [
                {
                    "title": "Bad",
                    "question": "What is retail AOV and COGS?",
                    "viz_hint": "kpi",
                    "components": ["AOV"],
                    "measure_hints": ["AOV"],
                }
            ]
        },
    }
    validated = validate_questions_node(state)
    assert validated["validation_ok"] is True
    charts = validated["plan"]["charts"]
    assert charts
    allowed = {name.lower() for name in enrich["allowed_names"]}
    for chart in charts:
        question = str(chart.get("question") or "").lower()
        assert "aov" not in question
        assert any(name in question for name in allowed) or chart.get("measure_hints")
        for hint in chart.get("measure_hints") or []:
            assert hint.lower() in allowed
