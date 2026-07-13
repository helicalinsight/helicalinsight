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
        ],
        "measures": [
            {
                "column_name": "travel_cost",
                "alias_name": "cost of travel",
                "measure_name": "cost of travel",
            }
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
        assert result["picked_metrics"] == ["cost of travel"]

    def test_uses_explicit_planner_picks(self):
        query_plan = {
            "columnName": ["travel_details.booking_platform"],
            "pickedDimensions": ["booking platform"],
            "pickedMetrics": ["cost of travel"],
            "reason": "explicit picks",
        }
        result = build_required_cube_info(_CUBE_METADATA, query_plan)
        assert result["picked_dimensions"] == ["booking platform"]
        assert result["picked_metrics"] == ["cost of travel"]

    def test_includes_matched_business_metrics(self):
        query_plan = {"columnName": ["travel_details.booking_platform"], "reason": "x"}
        metrics = [
            {
                "metric": "cost of travel",
                "measure_name": "cost of travel",
                "tables": ["travel_details"],
            }
        ]
        result = build_required_cube_info(_CUBE_METADATA, query_plan, metrics)
        assert result["picked_metrics"] == ["cost of travel"]

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


class TestGetRequiredMetricsCubeInfo:
    def test_sets_required_cube_info_on_state(self):
        state = {
            "required_tables": ["travel_details"],
            "business_metrics": [
                {
                    "metric": "cost of travel",
                    "measure_name": "cost of travel",
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
                    "pickedMetrics": ["cost of travel"],
                    "reason": "planner",
                }
            ),
        }
        result = GetRequiredMetrics().process_flow(state)
        assert result["required_cube_info"] == {
            "picked_dimensions": ["booking platform"],
            "picked_metrics": ["cost of travel"],
        }
