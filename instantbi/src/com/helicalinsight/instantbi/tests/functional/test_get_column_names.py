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


def test_remaps_travel_date_prefixed_with_wrong_table():
    """Year is travel_details.travel_date; LLM often hangs it on meeting_details."""
    cube_metadata = [
        {
            "database_table": "meeting_details",
            "columns": [
                {
                    "column_name": "meet_cancellation_status",
                    "alias_name": "Cancellation Status",
                    "dimension_name": "Cancellation Status",
                }
            ],
        },
        {
            "database_table": "travel_details",
            "columns": [
                {
                    "column_name": "travel_date",
                    "alias_name": "Year",
                    "dimension_name": "Year",
                    "formula": "EXTRACT(YEAR FROM travel_details.travel_date)",
                }
            ],
        },
    ]
    query_plan = {
        "columnName": [
            "meeting_details.meet_cancellation_status",
            "meeting_details.travel_date",
        ],
        "pickedDimensions": ["Cancellation Status", "Year"],
        "reason": "canceled meetings in 2026",
    }

    result = _qualify_query_plan_columns(
        query_plan, cube_metadata, ["meeting_details"]
    )

    assert result["columnName"] == [
        "meeting_details.meet_cancellation_status",
        "travel_details.travel_date",
    ]
    assert tables_from_query_plan(
        result, ["meeting_details", "travel_details"]
    ) == ["meeting_details", "travel_details"]


def test_qualifies_select_clause_columns():
    cube_metadata = [
        {
            "database_table": "travel_details",
            "columns": [{"column_name": "travel_cost"}],
        },
        {
            "database_table": "employee",
            "columns": [{"column_name": "emp_id"}],
        },
    ]
    query_plan = {
        "columnName": ["travel_cost", "emp_id"],
        "selectColumnName": ["travel_cost"],
        "reason": "Cost only in SELECT",
    }

    result = _qualify_query_plan_columns(
        query_plan, cube_metadata, ["travel_details", "employee"]
    )

    assert result["columnName"] == [
        "travel_details.travel_cost",
        "employee.emp_id",
    ]
    assert result["selectColumnName"] == ["travel_details.travel_cost"]


def test_empty_column_list_keeps_client_name_table_and_year_column():
    """Client Name lives on meeting_details; the model often returns only travel_details."""
    cube_metadata = [
        {
            "database_table": "meeting_details",
            "columns": [{"column_name": "client_name", "dimension_name": "Client Name"}],
        },
        {
            "database_table": "travel_details",
            "columns": [
                {"column_name": "travel_date", "dimension_name": "Year"},
                {"column_name": "travel_id"},
            ],
            "measures": [{"column_name": "travel_cost", "measure_name": "Cost Incurred"}],
        },
    ]
    query_plan = {
        "columnName": [],
        "selectColumnName": ["client_name", "travel_id", "travel_cost"],
        "pickedDimensions": ["client_name", "EXTRACT(YEAR FROM travel_date)"],
        "pickedMetrics": ["travel_id", "travel_cost"],
    }

    result = _qualify_query_plan_columns(
        query_plan, cube_metadata, ["travel_details"]
    )

    assert result["selectColumnName"] == [
        "meeting_details.client_name",
        "travel_details.travel_id",
        "travel_details.travel_cost",
    ]
    assert result["columnName"][:3] == result["selectColumnName"]
    assert "travel_details.travel_date" in result["columnName"]
    assert tables_from_query_plan(
        result, ["meeting_details", "travel_details"]
    ) == ["meeting_details", "travel_details"]
