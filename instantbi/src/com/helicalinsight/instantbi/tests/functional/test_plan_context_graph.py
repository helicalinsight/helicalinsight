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


def test_select_analytical_operations_node_populates_operations():
    from helicalbi.sql_agent.nodes.plan_graph import select_analytical_operations_node
    from helicalbi.sql_agent.strategy_tree import select_analytical_operations

    ops = select_analytical_operations("Why did travel spend drop last quarter compared to first quarter?")
    assert ops
    op_names = [op["operation"] for op in ops]
    assert any(name in op_names for name in ("period_comparison", "trend", "contribution", "mix_analysis"))

    state = {
        "question": "Why did travel spend drop last quarter compared to first quarter?",
        "activity_trace": [],
    }
    result = select_analytical_operations_node(state)
    assert result["analytical_operations"]
    assert result["selected_operations"]
    trace = result["activity_trace"]
    assert len(trace) == 1
    assert trace[0]["node"] == "select_analytical_operations"
    assert trace[0]["stage"] == "plan_graph"
    assert "operations" in trace[0]["result"]


def test_plan_json_contains_query_analytical_strategies():
    from helicalbi.sql_agent.models import DashboardChartSpec, InvestigationPlan
    from helicalbi.sql_agent.investigation import plan_to_public_dict

    chart1 = DashboardChartSpec(
        title="Revenue Trend",
        question="What is the monthly revenue trend?",
        viz_hint="line",
    )
    chart2 = DashboardChartSpec(
        title="Revenue by Region",
        question="What is revenue breakdown by region?",
        viz_hint="bar",
    )
    plan = InvestigationPlan(
        persona="executive",
        tier="strategic",
        strategy_id="inverted_pyramid",
        original_question="How is revenue trending and performing across regions?",
        charts=[chart1, chart2],
    )
    plan_dict = plan_to_public_dict(plan)
    assert "query_analytical_strategies" in plan_dict
    assert len(plan_dict["query_analytical_strategies"]) == 2
    assert plan_dict["query_analytical_strategies"][0]["analytical_strategy"] == "trend"
    assert plan_dict["query_analytical_strategies"][1]["analytical_strategy"] == "segment"
    assert plan_dict["charts"][0]["analytical_strategy"] == "trend"
    assert plan_dict["charts"][1]["analytical_strategy"] == "segment"
    assert plan_dict["charts"][0]["operation"] == "trend"
    assert plan_dict["charts"][1]["operation"] == "segment"


def test_measure_grain_direction_and_process_stages_indexing():
    indexer = SemanticLayerIndexer()
    model = {
        "modelName": "sales_model",
        "domain": [
            {
                "domain_name": "Sales Meetings",
                "topics": [
                    {
                        "topic": "Meeting Overview",
                        "description": "Sales meeting volume and outcomes",
                        "components": ["Meeting Count", "Meeting Cancellation Rate"],
                    }
                ],
            }
        ],
        "measures": [
            {
                "measureName": "Meeting Count",
                "semanticType": "count",
                "formula": "COUNT(meeting_details.meeting_id)",
                "grain": "meeting",
                "additivity": "additive",
                "direction": "neutral",
                "processStages": ["S1", "S5", "S6"],
            },
            {
                "measureName": "Meeting Cancellation Rate",
                "semanticType": "ratio",
                "formula": "SUM(cancelled) / COUNT(meeting_id)",
                "grain": "meeting",
                "additivity": "non-additive",
                "direction": "negative",
                "processStages": ["S5", "S6"],
            },
        ],
    }
    indexer.index_model(model)

    assert indexer.measure_direction("Meeting Count") == "neutral"
    assert indexer.measure_grain("Meeting Count") == "meeting"
    assert indexer.measure_direction("Meeting Cancellation Rate") == "negative"
    assert indexer.measure_grain("Meeting Cancellation Rate") == "meeting"

    meta = indexer.measure_metadata("Meeting Count")
    assert meta["grain"] == "meeting"
    assert meta["direction"] == "neutral"
    assert meta["additivity"] == "additive"
    assert meta["semantic_type"] == "count"
    assert meta["process_stages"] == ["S1", "S5", "S6"]
    assert meta["formula"] == "COUNT(meeting_details.meeting_id)"

    pack = indexer.topic_pack(["Meeting Overview"])
    assert "field Meeting Count:" in pack
    assert "formula: COUNT(meeting_details.meeting_id)" in pack
    assert "grain: meeting" in pack
    assert "business direction: neutral" in pack
    assert "additivity: additive" in pack
    assert "semantic type: count" in pack
    assert "process stages: S1, S5, S6" in pack
    assert "business direction: negative" in pack


def test_cube_info_measure_grain_direction():
    from helicalbi.common.CubeInfoModel import cube_info_to_cube_metadata

    cube = {
        "name": "sales_cube",
        "measures": [
            {
                "measureName": "Meeting Count",
                "columnName": "meeting_details.meeting_id",
                "semanticType": "count",
                "formula": "COUNT(meeting_details.meeting_id)",
                "grain": "meeting",
                "additivity": "additive",
                "direction": "neutral",
                "processStages": ["S1", "S5", "S6"],
            }
        ],
    }
    resolved = cube_info_to_cube_metadata([cube], {})
    assert len(resolved) == 1
    table_measures = resolved[0]["measures"]
    assert len(table_measures) == 1
    m = table_measures[0]
    assert m["measure_name"] == "Meeting Count"
    assert m["grain"] == "meeting"
    assert m["direction"] == "neutral"
    assert m["additivity"] == "additive"
    assert m["process_stages"] == ["S1", "S5", "S6"]


def test_diagnostic_capabilities_indexing_and_retrieval():
    from helicalbi.sql_agent.database.semantic_indexer import SemanticLayerIndexer

    diag_model = {
        "modelName": "sales_model",
        "diagnosticCapabilities": [
            {
                "capability": "growth_diagnosis",
                "applicableTo": ["Travel Cost"],
                "uses": ["trend", "period_comparison", "segment"],
            }
        ],
        "measures": [
            {"measureName": "Travel Cost", "grain": "travel", "direction": "negative"}
        ],
    }
    indexer = SemanticLayerIndexer()
    indexer.index_model(diag_model, {})

    caps = indexer.diagnostic_capabilities()
    assert len(caps) == 1
    assert caps[0]["capability"] == "growth_diagnosis"
    assert caps[0]["uses"] == ["trend", "period_comparison", "segment"]

    matches = indexer.matching_diagnostic_capabilities("How much total travel cost has changed over the year")
    assert len(matches) == 1
    assert matches[0]["capability"] == "growth_diagnosis"


def test_custom_strategy_type_is_loaded_and_prompted():
    from helicalbi.sql_agent.database.semantic_indexer import SemanticLayerIndexer
    from helicalbi.sql_agent.strategy_tree import (
        analytical_operations_prompt_block,
        merge_custom_operations,
        operations_for_diagnostic_uses,
        resolve_diagnostic_uses,
    )

    custom = {
        "operation": "fare gap",
        "type": "custom",
        "category": "custom",
        "description": "Compare fare to a target.",
        "requiredInputs": ["measure", "benchmark"],
        "optionalInputs": ["timeDimension"],
        "preconditions": ["A target exists."],
        "limitations": ["Do not invent a benchmark."],
        "execution": {"method": "sql", "logic": "Subtract target from actual."},
        "custom": True,
    }
    model = {
        "customAnalyticalOperations": [
            custom,
            {"operation": "ignored catalog copy", "category": "comparison"},
        ],
        "diagnosticCapabilities": [
            {
                "capability": "fare review",
                "applicableTo": ["Fare"],
                "uses": [{"analyticalOperation": "fare gap", "measure": "Fare"}],
            }
        ],
    }
    indexer = SemanticLayerIndexer()
    indexer.index_model(model, {})
    loaded = indexer.custom_analytical_operations()
    assert [op["operation"] for op in loaded] == ["fare gap"]
    assert loaded[0]["type"] == "custom"
    assert "fare gap (custom)" in indexer.overview()

    merged = merge_custom_operations(
        [{"operation": "trend", "description": "Change over time."}],
        loaded,
    )
    assert [op["operation"] for op in merged] == ["trend", "fare gap"]
    use_plan = resolve_diagnostic_uses(
        model["diagnosticCapabilities"],
        operations=merged,
        measures=["Fare"],
        dimensions=[],
    )
    chosen = operations_for_diagnostic_uses(use_plan, merged)
    assert [op["operation"] for op in chosen] == ["fare gap"]
    block = analytical_operations_prompt_block(chosen)
    assert "fare gap (custom): Compare fare to a target." in block
    assert "required inputs: measure, benchmark" in block
    assert "preconditions: A target exists." in block
    assert "limitations: Do not invent a benchmark." in block
    assert "execution: Subtract target from actual." in block


def test_analytical_operations_diagnostic_boosting():
    from helicalbi.sql_agent.strategy_tree import select_analytical_operations

    diag_caps = [
        {
            "capability": "growth_diagnosis",
            "applicableTo": ["Travel Cost"],
            "uses": ["trend", "period_comparison", "segment"],
        }
    ]
    ops = select_analytical_operations(
        "How much total travel cost has changed over the year",
        max_operations=3,
        diagnostic_capabilities=diag_caps,
    )
    op_names = {op["operation"] for op in ops}
    assert "trend" in op_names
    assert "period_comparison" in op_names
    assert "segment" in op_names


def test_diagnostic_capabilities_object_shaped_uses_and_applicable_to():
    from helicalbi.sql_agent.database.semantic_indexer import SemanticLayerIndexer
    from helicalbi.sql_agent.strategy_tree import (
        iter_applicable_measures,
        iter_diagnostic_uses,
        list_analytical_operations_for_api,
        select_analytical_operations,
    )

    ops = list_analytical_operations_for_api()
    assert any(op["operation"] == "trend" for op in ops)
    trend = next(op for op in ops if op["operation"] == "trend")
    assert "measure" in trend["requiredInputs"]
    assert "timeDimension" in trend["requiredInputs"]

    cap = {
        "capability": "Successful Meeting diagnosis",
        "applicableTo": [{"measure": "Successful Meeting Rate"}],
        "uses": [
            {
                "analyticalOperation": "trend",
                "measure": "Successful Meeting Rate",
                "timeDimension": "Meeting Date",
            }
        ],
    }
    assert iter_applicable_measures(cap["applicableTo"]) == ["Successful Meeting Rate"]
    assert iter_diagnostic_uses(cap["uses"]) == [
        {
            "name": "trend",
            "hinted_kind": "strategy",
            "measures": ["Successful Meeting Rate"],
            "dimensions": ["Meeting Date"],
        }
    ]

    indexer = SemanticLayerIndexer()
    indexer.index_model({"diagnosticCapabilities": [cap]}, {})
    matches = indexer.matching_diagnostic_capabilities(
        "Why did Successful Meeting Rate change?"
    )
    assert len(matches) == 1
    selected = select_analytical_operations(
        "Why did Successful Meeting Rate change?",
        max_operations=3,
        diagnostic_capabilities=[cap],
    )
    assert "trend" in {op["operation"] for op in selected}


def test_hierarchy_level_deduplication_across_tables():
    from helicalbi.common.CubeInfoModel import (
        _expand_hierarchy_items,
        build_cube_component_index,
        resolve_topic_component,
    )

    dimensions = [
        {
            "dimensionName": "Meeting Date",
            "tableId": "1043",
            "columnName": "meeting_details.meeting_date",
            "hierarchies": [
                {
                    "hierarchyName": "Meeting Date Hierarchy",
                    "tableId": "1043",
                    "levels": [
                        {
                            "levelName": "YEAR",
                            "tableId": "1043",
                            "columnName": "meeting_details.meeting_date",
                            "columnId": "meet-col-1",
                        }
                    ],
                }
            ],
        },
        {
            "dimensionName": "Travel Date",
            "tableId": "1065",
            "columnName": "travel_details.travel_date",
            "hierarchies": [
                {
                    "hierarchyName": "Travel Date Hierarchy",
                    "tableId": "1065",
                    "levels": [
                        {
                            "levelName": "YEAR",
                            "tableId": "1065",
                            "columnName": "travel_details.travel_date",
                            "columnId": "trav-col-1",
                        }
                    ],
                }
            ],
        },
    ]

    expanded = _expand_hierarchy_items(dimensions, "dimensionName")
    # Both YEAR levels must be preserved because they belong to distinct tables
    year_levels = [item for item in expanded if item.get("levelName") == "YEAR"]
    assert len(year_levels) == 2
    table_ids = {item["tableId"] for item in year_levels}
    assert "1043" in table_ids
    assert "1065" in table_ids

    # When querying with a preferred table ID (e.g. 1065 for travel_details),
    # resolve_topic_component should resolve to travel_details.travel_date
    by_id, by_name = build_cube_component_index([{"dimensions": expanded}])
    resolved = resolve_topic_component(
        {"name": "YEAR"},
        by_id,
        by_name,
        preferred_table_id="1065",
    )
    assert resolved["tableId"] == "1065"
    assert resolved["columnName"] == "travel_details.travel_date"


def _ratio_diagnosis_model():
    return {
        "modelName": "meetings",
        "diagnosticCapabilities": [
            {
                "capability": "ratio_diagnosis",
                "applicableTo": [
                    "Meeting Cancellation Rate",
                    "Successful Meeting Rate",
                    "Cost per Client",
                ],
                "uses": ["measureDecomposition", "trend", "segment", "Client Name"],
            }
        ],
        "measures": [
            {
                "measureName": "Meeting Cancellation Rate",
                "formula": "Cancelled Meetings / Meeting Count",
                "grain": "meeting",
                "additivity": "non-additive",
                "direction": "negative",
            },
            {
                "measureName": "Cancelled Meetings",
                "grain": "meeting",
                "additivity": "additive",
                "direction": "neutral",
            },
            {
                "measureName": "Meeting Count",
                "grain": "meeting",
                "additivity": "additive",
                "direction": "neutral",
            },
            {"measureName": "Successful Meeting Rate"},
            {"measureName": "Cost per Client"},
        ],
        "cube_info": [
            {
                "name": "meetings",
                "dimensions": [{"dimensionName": "Client Name"}],
                "measures": [],
            }
        ],
    }


def test_think_mode_uses_applicable_measures_and_classified_uses():
    from helicalbi.sql_agent.database.semantic_indexer import SemanticLayerIndexer
    from helicalbi.sql_agent.nodes.plan_graph import select_analytical_operations_node
    from helicalbi.sql_agent.strategy_tree import diagnostic_capabilities_prompt_block

    indexer = SemanticLayerIndexer()
    indexer.index_model(_ratio_diagnosis_model(), {})
    state = {
        "question": "Why is the meeting cancellation rate high?",
        "flat_sql": True,
        "diagnostic_capabilities": indexer.diagnostic_capabilities(),
        "session": {"semantic_indexer": indexer},
        "allowed_names": ["Meeting Cancellation Rate"],
        "grounding_pack": "TOPIC Meetings\n  components: Meeting Cancellation Rate",
        "activity_trace": [],
    }
    result = select_analytical_operations_node(state)

    assert result["selected_operations"] == ["decompose", "trend", "segment"]
    kinds = {(item["kind"], item["name"]) for item in result["diagnostic_use_plan"]}
    assert ("strategy", "decompose") in kinds
    assert ("strategy", "trend") in kinds
    assert ("strategy", "segment") in kinds
    assert ("dimension", "Client Name") in kinds
    assert ("measure", "Cancelled Meetings") in kinds
    assert ("measure", "Meeting Count") in kinds
    assert result["matched_diagnostic_capabilities"][0]["matchedMeasures"] == [
        "Meeting Cancellation Rate"
    ]
    assert "Client Name" in result["allowed_names"]
    assert "Cancelled Meetings" in result["allowed_names"]
    assert "formula: Cancelled Meetings / Meeting Count" in result["grounding_pack"]
    assert "grain: meeting" in result["grounding_pack"]
    assert "additivity: non-additive" in result["grounding_pack"]
    assert "business direction: negative" in result["grounding_pack"]
    assert "do not SUM this measure" in result["grounding_pack"]

    block = diagnostic_capabilities_prompt_block(
        result["matched_diagnostic_capabilities"],
        use_plan=result["diagnostic_use_plan"],
    )
    assert "Meeting Cancellation Rate" in block
    assert "measureDecomposition" in block
    assert "strategy decompose" in block
    assert "dimension Client Name" in block
    assert "Cancelled Meetings" in block
    assert "grain 'meeting'" in block
    assert "do not SUM this measure" in block
    assert "an increase is unfavorable" in block
    assert "SUM is valid across dimensions at this grain" in block
    assert "Allowed strategies: decompose, trend, segment" in block
    assert "Allowed measures: Meeting Cancellation Rate, Cancelled Meetings, Meeting Count" in block
    assert "Allowed dimensions: Client Name" in block
    assert "Do not use any other" in block
    assert "3 to 5 flat questions" not in block


def test_think_mode_ignores_diagnostic_capability_for_unrelated_question():
    from helicalbi.sql_agent.database.semantic_indexer import SemanticLayerIndexer
    from helicalbi.sql_agent.nodes.plan_graph import select_analytical_operations_node

    indexer = SemanticLayerIndexer()
    indexer.index_model(_ratio_diagnosis_model(), {})
    result = select_analytical_operations_node(
        {
            "question": "How many meetings happened?",
            "flat_sql": True,
            "diagnostic_capabilities": indexer.diagnostic_capabilities(),
            "session": {"semantic_indexer": indexer},
            "activity_trace": [],
        }
    )
    assert result["matched_diagnostic_capabilities"] == []
    assert result["diagnostic_use_plan"] == []
    assert "decompose" not in result["selected_operations"]


def test_think_mode_repairs_plan_missing_diagnostic_uses():
    from helicalbi.sql_agent.nodes.plan_graph import validate_questions_node

    validated = validate_questions_node(
        {
            "question": "Why is the meeting cancellation rate high?",
            "flat_sql": True,
            "repair_count": 0,
            "allowed_names": [
                "Meeting Cancellation Rate",
                "Client Name",
                "Cancelled Meetings",
                "Meeting Count",
            ],
            "matched_diagnostic_capabilities": [
                {
                    "capability": "ratio_diagnosis",
                    "matchedMeasures": ["Meeting Cancellation Rate"],
                }
            ],
            "diagnostic_use_plan": [
                {"kind": "strategy", "name": "decompose", "source": "measureDecomposition"},
                {"kind": "strategy", "name": "trend", "source": "trend"},
                {"kind": "strategy", "name": "segment", "source": "segment"},
                {"kind": "dimension", "name": "Client Name", "source": "Client Name"},
            ],
            "plan": {
                "charts": [
                    {
                        "title": "Cancellation by client",
                        "question": "What is Meeting Cancellation Rate by Client Name?",
                        "viz_hint": "bar",
                        "operation": "segment",
                        "measure_hints": ["Meeting Cancellation Rate"],
                        "components": ["Meeting Cancellation Rate", "Client Name"],
                    }
                ]
            },
            "activity_trace": [],
        }
    )
    assert validated["validation_ok"] is False
    feedback = validated["validation_feedback"]
    assert "Missing diagnostic strategy 'decompose'" in feedback
    assert "Missing diagnostic strategy 'trend'" in feedback


def test_think_mode_keeps_only_configured_strategy_fields():
    from helicalbi.sql_agent.database.semantic_indexer import SemanticLayerIndexer
    from helicalbi.sql_agent.nodes.plan_graph import select_analytical_operations_node
    from helicalbi.sql_agent.strategy_tree import (
        charts_within_diagnostic_scope,
        diagnostic_capabilities_prompt_block,
        diagnostic_field_scope,
        diagnostic_scope_lock,
    )

    indexer = SemanticLayerIndexer()
    indexer.index_model(
        {
            "modelName": "travel",
            "diagnosticCapabilities": [
                {
                    "capability": "travel_cost_split",
                    "applicableTo": ["Travel Cost", "Meeting Count"],
                    "uses": [
                        {
                            "analyticalOperation": "segment",
                            "measure": "Travel Cost",
                            "dimension": "Client Destination",
                            "additionalDimensions": ["Booking Platform"],
                        }
                    ],
                }
            ],
            "measures": [
                {"measureName": "Travel Cost"},
                {"measureName": "Meeting Count"},
            ],
            "cube_info": [
                {
                    "name": "travel",
                    "dimensions": [
                        {"dimensionName": "Client Destination"},
                        {"dimensionName": "Booking Platform"},
                        {"dimensionName": "Employee Name"},
                    ],
                    "measures": [],
                }
            ],
        },
        {},
    )
    result = select_analytical_operations_node(
        {
            "question": "Why is Travel Cost high?",
            "flat_sql": True,
            "diagnostic_capabilities": indexer.diagnostic_capabilities(),
            "session": {"semantic_indexer": indexer},
            "allowed_names": ["Travel Cost"],
            "grounding_pack": "TOPIC Travel",
            "activity_trace": [],
        }
    )
    assert result["selected_operations"] == ["segment"]
    kinds = {(item["kind"], item["name"]) for item in result["diagnostic_use_plan"]}
    assert ("strategy", "segment") in kinds
    assert ("measure", "Travel Cost") in kinds
    assert ("dimension", "Client Destination") in kinds
    assert ("dimension", "Booking Platform") in kinds
    assert ("measure", "Meeting Count") not in kinds

    scope = diagnostic_field_scope(
        result["diagnostic_use_plan"],
        result["matched_diagnostic_capabilities"],
    )
    assert scope["strategies"] == ["segment"]
    assert scope["measures"] == ["Travel Cost"]
    assert scope["dimensions"] == ["Client Destination", "Booking Platform"]

    block = diagnostic_capabilities_prompt_block(
        result["matched_diagnostic_capabilities"],
        use_plan=result["diagnostic_use_plan"],
    )
    assert "Configured fields for this strategy: measures Travel Cost; dimensions Client Destination, Booking Platform" in block
    assert "Allowed strategies: segment" in block
    lock = diagnostic_scope_lock(scope)
    assert "same analytical strategy: segment" in lock
    assert "Dashboard layout slots are optional" in lock
    assert "Travel Cost" in lock
    assert "Employee Name" not in lock
    assert "Meeting Count" not in lock

    kept = charts_within_diagnostic_scope(
        [
            {
                "question": "What is Travel Cost by Client Destination?",
                "operation": "segment",
                "measure_hints": ["Travel Cost"],
                "components": ["Travel Cost", "Client Destination"],
            },
            {
                "question": "What is Travel Cost by Employee Name?",
                "operation": "segment",
                "measure_hints": ["Travel Cost"],
                "components": ["Travel Cost", "Employee Name"],
            },
            {
                "question": "Which employees rank highest on Meeting Count?",
                "operation": "ranking",
                "measure_hints": ["Meeting Count"],
                "components": ["Employee Name", "Meeting Count"],
            },
        ],
        scope,
    )
    assert len(kept) == 1
    assert kept[0]["operation"] == "segment"
    assert kept[0]["measure_hints"] == ["Travel Cost"]
    assert kept[0]["components"] == ["Travel Cost", "Client Destination"]

    from helicalbi.sql_agent.nodes.plan_graph import validate_questions_node

    validated = validate_questions_node(
        {
            "question": "Why is Travel Cost high?",
            "flat_sql": True,
            "repair_count": 0,
            "allowed_names": ["Travel Cost", "Client Destination", "Booking Platform"],
            "session": {"semantic_indexer": indexer},
            "matched_diagnostic_capabilities": result["matched_diagnostic_capabilities"],
            "diagnostic_use_plan": result["diagnostic_use_plan"],
            "plan": {
                "charts": [
                    {
                        "title": "Cost by employee",
                        "question": "What is Travel Cost by Employee Name?",
                        "viz_hint": "bar",
                        "operation": "ranking",
                        "measure_hints": ["Meeting Count"],
                        "components": ["Travel Cost", "Employee Name"],
                    }
                ]
            },
            "activity_trace": [],
        }
    )
    feedback = validated["validation_feedback"]
    assert "strategy 'ranking'" in feedback
    assert "measure 'Meeting Count'" in feedback
    assert "Employee Name" in feedback


def test_think_mode_locks_misspelled_question_to_configured_trend():
    from helicalbi.sql_agent.database.semantic_indexer import SemanticLayerIndexer
    from helicalbi.sql_agent.nodes.plan_graph import select_analytical_operations_node
    from helicalbi.sql_agent.strategy_tree import (
        charts_within_diagnostic_scope,
        diagnostic_field_scope,
    )

    indexer = SemanticLayerIndexer()
    indexer.index_model(
        {
            "modelName": "meetings",
            "diagnosticCapabilities": [
                {
                    "capability": "Sucessful Meeting diagonsis",
                    "applicableTo": [{"measure": "Successful Meeting Rate"}],
                    "uses": [
                        {
                            "analyticalOperation": "trend",
                            "measure": "Successful Meeting Rate",
                            "timeDimension": "YEAR",
                        }
                    ],
                }
            ],
            "measures": [
                {"measureName": "Successful Meeting Rate"},
                {"measureName": "Meeting Cancellation Rate"},
                {"measureName": "Successful Visit Cost"},
            ],
            "cube_info": [
                {
                    "name": "meetings",
                    "dimensions": [
                        {"dimensionName": "YEAR"},
                        {"dimensionName": "Meeting Purpose"},
                        {"dimensionName": "Client Name"},
                    ],
                    "measures": [],
                }
            ],
        },
        {},
    )
    result = select_analytical_operations_node(
        {
            "question": "how sucessful meeting are impacting business",
            "flat_sql": True,
            "diagnostic_capabilities": indexer.diagnostic_capabilities(),
            "session": {"semantic_indexer": indexer},
            "allowed_names": [],
            "grounding_pack": "TOPIC Meetings",
            "activity_trace": [],
        }
    )
    assert result["selected_operations"] == ["trend"]
    activity = result["activity_trace"][-1]["reason"]
    assert "Picked capability 'Sucessful Meeting diagonsis'" in activity
    assert "Measure: Successful Meeting Rate" in activity
    assert "Analytical strategy: trend" in activity
    assert "Dimension: YEAR" in activity
    assert result["matched_diagnostic_capabilities"][0]["matchedMeasures"] == [
        "Successful Meeting Rate"
    ]
    scope = diagnostic_field_scope(
        result["diagnostic_use_plan"],
        result["matched_diagnostic_capabilities"],
    )
    assert scope["strategies"] == ["trend"]
    assert scope["measures"] == ["Successful Meeting Rate"]
    assert scope["dimensions"] == ["YEAR"]

    kept = charts_within_diagnostic_scope(
        [
            {
                "question": "How does Successful Meeting Rate change by YEAR?",
                "operation": "trend",
                "measure_hints": ["Successful Meeting Rate"],
                "components": ["Successful Meeting Rate", "YEAR"],
            },
            {
                "question": "What is Meeting Cancellation Rate by Meeting Purpose?",
                "operation": "segment",
                "measure_hints": ["Meeting Cancellation Rate"],
                "components": ["Meeting Cancellation Rate", "Meeting Purpose"],
            },
            {
                "question": "Which clients drive Successful Visit Cost?",
                "operation": "contribution",
                "measure_hints": ["Successful Visit Cost"],
                "components": ["Successful Visit Cost", "Client Name"],
            },
        ],
        scope,
    )
    assert len(kept) == 1
    assert kept[0]["operation"] == "trend"
    assert kept[0]["measure_hints"] == ["Successful Meeting Rate"]



