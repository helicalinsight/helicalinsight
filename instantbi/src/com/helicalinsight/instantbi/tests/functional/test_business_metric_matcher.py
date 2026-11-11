"""Functional tests for business metric schema matching."""

import json

import pytest

from helicalbi.core.sqlflow.util.BusinessMetricMatcher import (
    filter_required_business_metrics,
    metric_matches_required_schema,
)
from helicalbi.prompt.FinalSqlPrompt import format_metrics_for_prompt

pytestmark = pytest.mark.functional


class TestBusinessMetricMatcher:
    def test_matches_explicit_tables(self):
        metric = {
            "metric": "canceled_meetings",
            "description": "Total canceled meetings",
            "tables": ["meeting_details"],
        }
        result = filter_required_business_metrics([metric], ["meeting_details"])
        assert result == [metric]

    def test_matches_formula_table_column_reference(self):
        metric = {
            "metric": "travel_cost_total",
            "tables": ["other_table"],
            "formula": "SUM(travel_details.travel_cost)",
        }
        query_plan = {
            "columnName": ["travel_details.travel_cost", "travel_details.destination_id"],
            "reason": "Travel cost by destination",
        }
        result = filter_required_business_metrics([metric], ["travel_details"], query_plan)
        assert result == [metric]

    def test_matches_filter_column_reference(self):
        metric = {
            "metric": "active_only",
            "tables": ["unused_table"],
            "filter": "employee_details.employee_name = 'Ada'",
        }
        result = filter_required_business_metrics(
            [metric],
            ["employee_details"],
            {"columnName": ["employee_details.employee_name"]},
        )
        assert result == [metric]

    def test_matches_nested_metric_key(self):
        metric = {
            "metric": "geo_metric",
            "tables": [],
            "geo_cordinates": {"location_id": "join key"},
        }
        assert metric_matches_required_schema(
            metric,
            {"geo_cordinates"},
            set(),
            {"geo_cordinates.location_id"},
        ) is True

    def test_does_not_match_unrelated_metric(self):
        metric = {
            "metric": "headcount",
            "tables": ["employee_details"],
            "formula": "COUNT(employee_details.employee_id)",
        }
        result = filter_required_business_metrics(
            [metric],
            ["meeting_details"],
            {"columnName": ["meeting_details.meeting_id"]},
        )
        assert result == []

    def test_accepts_query_plan_json_string(self):
        metric = {
            "metric": "canceled_meetings",
            "filter": "meeting_details.meet_cancellation_status = 'Yes'",
        }
        query_plan = json.dumps(
            {
                "columnName": ["meeting_details.meet_cancellation_status"],
                "reason": "Canceled meetings",
            }
        )
        result = filter_required_business_metrics([metric], ["meeting_details"], query_plan)
        assert result == [metric]

    def test_excludes_same_table_unrelated_metrics_when_column_is_specific(self):
        related = {
            "metric": "booking_platform",
            "tables": ["travel_details"],
            "column_name": "booking_platform",
            "formula": "concat('Traveled With', booking_platform)",
        }
        unrelated = {
            "metric": "travel_cost",
            "tables": ["travel_details"],
            "column_name": "travel_cost",
            "formula": "sum(travel_cost)",
        }
        result = filter_required_business_metrics(
            [related, unrelated],
            ["travel_details"],
            {"columnName": ["travel_details.booking_platform"]},
        )
        assert result == [related]

    def test_same_table_formula_on_another_column_is_excluded(self):
        metric = {
            "metric": "High Impact Meeting Count",
            "tables": ["meeting_details"],
            "formula": "SUM(CASE WHEN meeting_details.meeting_impact='High' THEN 1 ELSE 0 END)",
        }
        result = filter_required_business_metrics(
            [metric],
            ["meeting_details"],
            {"columnName": ["meeting_details.meet_cancellation_status"]},
        )
        assert result == []


def test_metrics_prompt_names_each_table_once():
    text = format_metrics_for_prompt(
        [
            {
                "metric": "Cost Incurred",
                "tables": ["travel_details"],
                "table_alias": "travel_details",
                "column_name": "travel_cost",
                "formula": "SUM(travel_details.travel_cost)",
                "description": "AI instructions (SQL/viz): long text",
                "sort_order": 13,
            },
            {
                "metric": "Total Travel",
                "tables": ["travel_details"],
                "column_name": "travel_id",
                "aggregator": "Count",
            },
        ]
    )
    assert text == (
        "travel_details\n"
        "Cost Incurred = SUM(travel_details.travel_cost)\n"
        "Total Travel = COUNT(travel_details.travel_id)"
    )
    assert "table_alias" not in text
    assert "sort_order" not in text
    assert "AI instructions" not in text
