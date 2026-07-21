"""Tests for query-plan normalization in ``GetColumnNames``."""

import pytest

from helicalbi.core.sqlflow.GetColumnNames import _qualify_query_plan_columns
from helicalbi.core.sqlflow.util.FallbackSqlHelpers import tables_from_query_plan


pytestmark = pytest.mark.functional


def test_qualifies_bare_column_using_required_table():
    cube_metadata = [
        {
            "database_table": "travel_details",
            "columns": [{"column_name": "travel_cost"}],
            "measures": [],
        }
    ]
    query_plan = {
        "columnName": ["travel_cost"],
        "reason": "Travel revenue",
    }

    result = _qualify_query_plan_columns(query_plan, cube_metadata, ["travel_details"])

    assert result["columnName"] == ["travel_details.travel_cost"]


def test_keeps_existing_qualified_column_unchanged():
    cube_metadata = [
        {
            "database_table": "travel_details",
            "columns": [{"column_name": "travel_cost"}],
        }
    ]
    query_plan = {
        "columnName": ["travel_details.travel_cost"],
        "reason": "Travel revenue",
    }

    result = _qualify_query_plan_columns(query_plan, cube_metadata, ["travel_details"])

    assert result["columnName"] == ["travel_details.travel_cost"]


def test_prefers_required_table_when_column_exists_multiple_tables():
    cube_metadata = [
        {"database_table": "travel_details", "columns": [{"column_name": "id"}]},
        {"database_table": "booking_details", "columns": [{"column_name": "id"}]},
    ]
    query_plan = {"columnName": ["id"], "reason": "Need booking id"}

    result = _qualify_query_plan_columns(query_plan, cube_metadata, ["booking_details"])

    assert result["columnName"] == ["booking_details.id"]


def test_resolves_quoted_alias_to_physical_column():
    cube_metadata = [
        {
            "database_table": "travel_details",
            "columns": [],
            "measures": [
                {
                    "column_name": "travel_cost",
                    "alias_name": "Total cost",
                    "measure_name": "Total cost",
                }
            ],
        }
    ]
    query_plan = {
        "columnName": ['travel_details."Total cost"'],
        "reason": "alias leaked from schema SQL hint",
    }

    result = _qualify_query_plan_columns(query_plan, cube_metadata, ["travel_details"])

    assert result["columnName"] == ["travel_details.travel_cost"]


def test_tables_from_query_plan_extracts_unique_tables():
    query_plan = {
        "columnName": [
            "travel_details.travel_type",
            "travel_details.travel_cost",
            "employee_details.employee_name",
        ]
    }
    assert tables_from_query_plan(query_plan) == [
        "travel_details",
        "employee_details",
    ]


def test_tables_from_query_plan_filters_unknown_tables():
    query_plan = {
        "columnName": [
            "travel_details.travel_cost",
            "booking.booking_id",
        ]
    }
    assert tables_from_query_plan(query_plan, ["travel_details"]) == ["travel_details"]
