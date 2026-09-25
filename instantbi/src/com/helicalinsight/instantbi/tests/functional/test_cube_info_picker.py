"""Functional tests for cube dimension/measure picker utilities."""

import json

import pytest

from helicalbi.core.sqlflow.GetRequiredMetrics import GetRequiredMetrics
from helicalbi.core.sqlflow.util.CubeInfoPicker import build_required_cube_info

pytestmark = pytest.mark.functional


_CUBE_METADATA = [
    {
        "database_table": "travel_details",
        "columns": [
            {
                "column_name": "booking_platform",
                "alias_name": "booking platform",
            },
            {
                "column_name": "travel_type",
                "alias_name": "travel type",
            },
            {
                "column_name": "destination",
                "alias_name": "Destination",
            },
        ],
        "measures": [
            {
                "column_name": "travel_cost",
                "alias_name": "Total cost",
                "measure_name": "Total cost",
            },
            {
                "column_name": "travel_cost",
                "alias_name": "Travel Cost",
                "measure_name": "Travel Cost",
            },
        ],
    }
]


class TestCubeInfoPicker:
    def test_derives_dimensions_and_metrics_from_query_plan_columns(self):
        query_plan = {
            "columnName": [
                "travel_details.booking_platform",
                "travel_details.travel_cost",
            ],
            "reason": "group and aggregate",
        }
        result = build_required_cube_info(_CUBE_METADATA, query_plan)
        assert result["picked_dimensions"] == ["booking platform"]
        assert result["picked_metrics"] == ["Total cost"]

    def test_uses_explicit_planner_picks(self):
        query_plan = {
            "columnName": ["travel_details.booking_platform"],
            "pickedDimensions": ["booking platform"],
            "pickedMetrics": ["Total cost"],
            "reason": "explicit picks",
        }
        result = build_required_cube_info(_CUBE_METADATA, query_plan)
        assert result["picked_dimensions"] == ["booking platform"]
        # Metric is not in the used columns — do not keep planner-only measures.
        assert result["picked_metrics"] == []

    def test_strips_quotes_and_rejects_unknown_picks(self):
        query_plan = {
            "columnName": ['travel_details."Total cost"'],
            "pickedDimensions": ['"Total cost"'],
            "pickedMetrics": ['"Total cost"'],
            "reason": "quoted alias leaked into plan",
        }
        result = build_required_cube_info(_CUBE_METADATA, query_plan)
        # "Total cost" is a measure, not a dimension — do not put it in dims
        assert result["picked_dimensions"] == []
        assert result["picked_metrics"] == ["Total cost"]

    def test_includes_matched_business_metrics(self):
        query_plan = {
            "columnName": [
                "travel_details.booking_platform",
                "travel_details.travel_cost",
            ],
            "reason": "x",
        }
        metrics = [
            {
                "metric": "Total cost",
                "measure_name": "Total cost",
                "tables": ["travel_details"],
            }
        ]
        result = build_required_cube_info(_CUBE_METADATA, query_plan, metrics)
        assert result["picked_metrics"] == ["Total cost"]

    def test_drops_planner_metrics_not_in_used_columns(self):
        query_plan = {
            "columnName": ["travel_details.booking_platform"],
            "reason": "dimension only",
        }
        metrics = [
            {
                "metric": "Total cost",
                "measure_name": "Total cost",
                "tables": ["travel_details"],
            }
        ]
        result = build_required_cube_info(_CUBE_METADATA, query_plan, metrics)
        assert result["picked_dimensions"] == ["booking platform"]
        assert result["picked_metrics"] == []

    def test_accepts_query_plan_json_string(self):
        query_plan = json.dumps(
            {
                "columnName": ["travel_details.travel_type"],
                "reason": "filter",
            }
        )
        result = build_required_cube_info(_CUBE_METADATA, query_plan)
        assert result["picked_dimensions"] == ["travel type"]
        assert result["picked_metrics"] == []

    def test_drops_unpicked_semantic_fields_from_query_plan(self):
        query_plan = {
            "columnName": [
                "travel_details.destination",
                "travel_details.booking_platform",
                "travel_details.travel_type",
                "travel_details.travel_cost",
            ],
            "pickedDimensions": ["Destination"],
            "pickedMetrics": ["Travel Cost"],
            "reason": "sql uses destination and travel cost only",
        }
        result = build_required_cube_info(_CUBE_METADATA, query_plan)
        assert result["picked_dimensions"] == ["Destination"]
        assert result["picked_metrics"] == ["Travel Cost"]
        travel = result["picked_by_table"]["travel_details"]
        dim_names = [
            item.get("alias_name") for item in travel.get("dimensions") or []
        ]
        measure_names = [
            item.get("alias_name") or item.get("measure_name")
            for bucket in ("measures", "computed_measures")
            for item in travel.get(bucket) or []
        ]
        assert dim_names == ["Destination"]
        assert measure_names == ["Travel Cost"]

    def test_accepts_picked_measures_alias(self):
        query_plan = {
            "columnName": ["travel_details.destination", "travel_details.travel_cost"],
            "pickedDimensions": ["Destination"],
            "pickedMeasures": ["Travel Cost"],
            "reason": "pickedMeasures used instead of pickedMetrics",
        }
        result = build_required_cube_info(_CUBE_METADATA, query_plan)
        assert result["picked_dimensions"] == ["Destination"]
        assert result["picked_metrics"] == ["Travel Cost"]

    def test_filter_columns_are_listed_with_select_dimensions(self):
        query_plan = {
            "columnName": [
                "travel_details.destination",
                "travel_details.booking_platform",
            ],
            "selectColumnName": ["travel_details.destination"],
            "reason": "booking platform is filter-only",
        }
        result = build_required_cube_info(_CUBE_METADATA, query_plan)
        assert result["picked_dimensions"] == ["Destination", "booking platform"]
        assert result["picked_metrics"] == []

    def test_drops_planner_dimensions_not_in_select(self):
        query_plan = {
            "columnName": ["travel_details.travel_cost"],
            "selectColumnName": ["travel_details.travel_cost"],
            "pickedDimensions": ["YEAR", "MONTH", "WEEK"],
            "pickedMetrics": ["Travel Cost"],
            "reason": "kpi total travel cost",
        }
        cube_metadata = [
            {
                "database_table": "travel_details",
                "columns": [
                    {
                        "column_name": "travel_date",
                        "alias_name": "YEAR",
                        "level_name": "YEAR",
                    },
                    {
                        "column_name": "travel_date",
                        "alias_name": "MONTH",
                        "level_name": "MONTH",
                    },
                    {
                        "column_name": "travel_date",
                        "alias_name": "WEEK",
                        "level_name": "WEEK",
                    },
                ],
                "measures": [
                    {
                        "column_name": "travel_cost",
                        "alias_name": "Travel Cost",
                        "measure_name": "Travel Cost",
                    }
                ],
            }
        ]
        result = build_required_cube_info(cube_metadata, query_plan)
        assert result["picked_dimensions"] == []
        assert result["picked_metrics"] == ["Travel Cost"]
        travel = result["picked_by_table"]["travel_details"]
        assert not travel.get("dimensions")
        assert not travel.get("hierarchies")
        assert [
            item.get("alias_name") or item.get("measure_name")
            for item in travel.get("measures") or []
        ] == ["Travel Cost"]

    def test_drops_planner_metrics_not_in_select_and_keeps_used_dimensions(self):
        query_plan = {
            "columnName": ["travel_details.destination"],
            "selectColumnName": ["travel_details.destination"],
            "pickedDimensions": ["Destination"],
            "pickedMetrics": ["Travel Cost"],
            "reason": "dimension-only listing",
        }
        result = build_required_cube_info(_CUBE_METADATA, query_plan)
        assert result["picked_dimensions"] == ["Destination"]
        assert result["picked_metrics"] == []

    def test_adds_used_select_columns_omitted_by_planner(self):
        query_plan = {
            "columnName": [
                "travel_details.destination",
                "travel_details.travel_cost",
            ],
            "selectColumnName": [
                "travel_details.destination",
                "travel_details.travel_cost",
            ],
            "pickedDimensions": ["Destination"],
            "reason": "planner forgot the measure used in SELECT",
        }
        result = build_required_cube_info(_CUBE_METADATA, query_plan)
        assert result["picked_dimensions"] == ["Destination"]
        assert result["picked_metrics"] == ["Total cost"]


class TestGetRequiredMetricsCubeInfo:
    def test_sets_required_cube_info_on_state(self):
        state = {
            "required_tables": ["travel_details"],
            "business_metrics": [
                {
                    "metric": "Total cost",
                    "measure_name": "Total cost",
                    "tables": ["travel_details"],
                    "column_name": "travel_cost",
                }
            ],
            "cube_metadata": _CUBE_METADATA,
            "query_plan": json.dumps(
                {
                    "columnName": [
                        "travel_details.booking_platform",
                        "travel_details.travel_cost",
                    ],
                    "pickedDimensions": ["booking platform"],
                    "pickedMetrics": ["Total cost"],
                    "reason": "planner",
                }
            ),
        }
        result = GetRequiredMetrics().process_flow(state)
        assert result["required_cube_info"]["picked_dimensions"] == ["booking platform"]
        assert result["required_cube_info"]["picked_metrics"] == ["Total cost"]
        picked_by_table = result["required_cube_info"]["picked_by_table"]
        assert "travel_details" in picked_by_table
        assert any(
            item.get("alias_name") == "booking platform"
            for item in picked_by_table["travel_details"].get("dimensions") or []
        )
        assert "sql_domain_context" in result
        assert "format_string" not in json.dumps(
            picked_by_table.get("travel_details") or {}
        ) or all(
            "format_string" not in item
            for bucket in ("dimensions", "hierarchies", "measures", "computed_measures")
            for item in (picked_by_table["travel_details"].get(bucket) or [])
        )

    def test_semantic_section_omits_unpicked_topic_components(self):
        state = {
            "required_tables": ["travel_details"],
            "business_metrics": [],
            "cube_metadata": _CUBE_METADATA,
            "domain": ["Sales Travel"],
            "topics": ["Travel"],
            "topic_mappings": [
                {
                    "topic_name": "Travel",
                    "components": [
                        {"name": "Destination", "kind": "dimension"},
                        {"name": "booking platform", "kind": "dimension"},
                        {"name": "Travel Cost", "kind": "measure"},
                        {"name": "travel type", "kind": "dimension"},
                    ],
                    "component": [
                        "Destination",
                        "booking platform",
                        "Travel Cost",
                        "travel type",
                    ],
                }
            ],
            "query_plan": json.dumps(
                {
                    "columnName": [
                        "travel_details.destination",
                        "travel_details.booking_platform",
                        "travel_details.travel_cost",
                    ],
                    "pickedDimensions": ["Destination"],
                    "pickedMetrics": ["Travel Cost"],
                    "reason": "planner",
                }
            ),
        }
        result = GetRequiredMetrics().process_flow(state)
        assert result["required_cube_info"]["picked_dimensions"] == ["Destination"]
        assert result["required_cube_info"]["picked_metrics"] == ["Travel Cost"]
        assert result["topics"] == ["Travel"]
        assert result["domain"] == ["Sales Travel"]
        assert [c["name"] for c in result["topic_mappings"][0]["components"]] == [
            "Destination",
            "Travel Cost",
        ]
        domain_ctx = result["sql_domain_context"]
        assert "Destination" in domain_ctx
        assert "Travel Cost" in domain_ctx
        assert "booking platform" not in domain_ctx
        assert "travel type" not in domain_ctx

    def test_semantic_section_omits_topics_without_used_dims_or_metrics(self):
        state = {
            "required_tables": ["travel_details"],
            "business_metrics": [],
            "cube_metadata": _CUBE_METADATA,
            "domain": ["Sales Travel"],
            "topics": ["Travel", "Meeting"],
            "topic_mappings": [
                {
                    "topic_name": "Travel",
                    "components": [
                        {"name": "Destination", "kind": "dimension"},
                        {"name": "Travel Cost", "kind": "measure"},
                    ],
                    "component": ["Destination", "Travel Cost"],
                },
                {
                    "topic_name": "Meeting",
                    "components": [
                        {"name": "Meeting Type", "kind": "dimension"},
                        {"name": "Meeting Count", "kind": "measure"},
                    ],
                    "component": ["Meeting Type", "Meeting Count"],
                },
            ],
            "query_plan": json.dumps(
                {
                    "columnName": [
                        "travel_details.destination",
                        "travel_details.travel_cost",
                    ],
                    "pickedDimensions": ["Destination"],
                    "pickedMetrics": ["Travel Cost"],
                    "reason": "planner",
                }
            ),
        }
        result = GetRequiredMetrics().process_flow(state)
        domain_ctx = result["sql_domain_context"]
        assert result["required_cube_info"]["picked_dimensions"] == ["Destination"]
        assert result["required_cube_info"]["picked_metrics"] == ["Travel Cost"]
        assert result["topics"] == ["Travel"]
        assert result["domain"] == ["Sales Travel"]
        assert [entry["topic_name"] for entry in result["topic_mappings"]] == ["Travel"]
        assert "Topic: Travel" in domain_ctx
        assert "Destination" in domain_ctx
        assert "Travel Cost" in domain_ctx
        assert "Topic: Meeting" not in domain_ctx
        assert "Meeting Type" not in domain_ctx
        assert "Meeting Count" not in domain_ctx
        assert "topics: Travel, Meeting" not in domain_ctx

    def test_semantic_section_omits_domains_without_used_dims_or_metrics(self):
        state = {
            "required_tables": ["travel_details"],
            "business_metrics": [],
            "cube_metadata": _CUBE_METADATA,
            "domain": ["Sales Travel", "Client Meetings"],
            "topics": ["Travel", "Meeting"],
            "topic_mappings": [
                {
                    "topic_name": "Travel",
                    "domain_name": "Sales Travel",
                    "components": [
                        {"name": "Destination", "kind": "dimension"},
                        {"name": "Travel Cost", "kind": "measure"},
                    ],
                    "component": ["Destination", "Travel Cost"],
                },
                {
                    "topic_name": "Meeting",
                    "domain_name": "Client Meetings",
                    "components": [
                        {"name": "Meeting Type", "kind": "dimension"},
                        {"name": "Meeting Count", "kind": "measure"},
                    ],
                    "component": ["Meeting Type", "Meeting Count"],
                },
            ],
            "query_plan": json.dumps(
                {
                    "columnName": [
                        "travel_details.destination",
                        "travel_details.travel_cost",
                    ],
                    "pickedDimensions": ["Destination"],
                    "pickedMetrics": ["Travel Cost"],
                    "reason": "planner",
                }
            ),
        }
        result = GetRequiredMetrics().process_flow(state)
        domain_ctx = result["sql_domain_context"]
        assert result["topics"] == ["Travel"]
        assert result["domain"] == ["Sales Travel"]
        assert [entry["topic_name"] for entry in result["topic_mappings"]] == ["Travel"]
        assert "Domain: Sales Travel" in domain_ctx
        assert "Domain: Sales Travel, Client Meetings" not in domain_ctx
        assert "Client Meetings" not in domain_ctx
        assert "Topic: Travel" in domain_ctx
        assert "Topic: Meeting" not in domain_ctx
