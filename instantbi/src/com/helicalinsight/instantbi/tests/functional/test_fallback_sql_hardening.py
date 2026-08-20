"""Functional tests for metadata-fallback SQL path helpers."""

from types import SimpleNamespace

import pytest

from helicalbi.core.sqlflow.util.FallbackSqlHelpers import (
    filter_previous_sql_for_context,
    is_join_api_error,
    needs_llm_table_narrowing,
    tables_from_query_plan,
)

pytestmark = pytest.mark.functional


class TestNeedsLlmTableNarrowing:
    def test_true_when_topics_empty_and_all_tables_selected(self):
        assert needs_llm_table_narrowing(["a", "b", "c"], ["a", "b", "c"], [])

    def test_true_when_input_equals_full_catalog(self):
        assert needs_llm_table_narrowing(["a", "b"], ["a", "b"], ["Travel"])

    def test_false_when_already_narrowed(self):
        assert not needs_llm_table_narrowing(
            ["travel_details"],
            ["travel_details", "meeting_details", "dimdate"],
            ["Travel"],
        )

    def test_false_for_single_known_table(self):
        assert not needs_llm_table_narrowing(["travel_details"], ["travel_details"], [])


class TestJoinApiErrorHandling:
    def test_detects_ecma_error_payload(self):
        assert is_join_api_error(
            {
                "message": 'Error: EcmaError: TypeError: Cannot read property "orderBy" from undefined',
                "className": "EcmaError",
            }
        )

    def test_accepts_valid_query_payload(self):
        assert not is_join_api_error({"query": "SELECT 1 FROM travel_details"})


class TestTablesFromQueryPlan:
    def test_extracts_unique_tables(self):
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

    def test_filters_unknown_tables(self):
        query_plan = {
            "columnName": [
                "travel_details.travel_cost",
                "booking.booking_id",
            ]
        }
        assert tables_from_query_plan(query_plan, ["travel_details"]) == ["travel_details"]


class TestPreviousSqlFilter:
    def test_drops_sql_with_unknown_tables(self):
        prev = [
            {
                "previous_sql": SimpleNamespace(
                    sql=(
                        "SELECT travel_details.travel_type, SUM(travel_details.travel_cost) "
                        "FROM travel_details JOIN booking ON travel_details.booking_id = booking.booking_id "
                        "LIMIT 100"
                    ),
                    reason="",
                )
            }
        ]
        filtered = filter_previous_sql_for_context(prev, {"travel_details"})
        assert filtered == []

    def test_keeps_sql_within_allowed_tables(self):
        prev = [
            {
                "previous_sql": {
                    "sql": (
                        "SELECT travel_details.travel_type, SUM(travel_details.travel_cost) "
                        "FROM travel_details GROUP BY travel_details.travel_type LIMIT 100"
                    ),
                    "reason": "",
                }
            }
        ]
        filtered = filter_previous_sql_for_context(prev, {"travel_details"})
        assert filtered == prev
