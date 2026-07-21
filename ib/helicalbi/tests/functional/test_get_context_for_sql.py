"""Functional tests for ``helicalbi.sql.GetContextForSQL``."""
import json

import pytest

from helicalbi.sql.GetContextForSQL import (
    get_required_column_description,
    get_table_col_description,
    get_tables_and_columns_by_topics,
)


pytestmark = pytest.mark.functional


class TestGetTablesAndColumnsByTopics:
    def test_returns_table_for_each_known_topic(self):
        topic_table = {"Meetings": "meeting_details", "Travel": "travel_details"}
        assert get_tables_and_columns_by_topics(
            ["Meetings", "Travel"], topic_table
        ) == ["meeting_details", "travel_details"]

    def test_returns_empty_string_for_missing_topic(self):
        assert get_tables_and_columns_by_topics(["X"], {"Y": "z"}) == [""]

    def test_empty_topics_returns_empty_list(self):
        assert get_tables_and_columns_by_topics([], {}) == []


class TestGetTableColDescription:
    def test_filters_cubes_by_table_names(self, sample_cube_metadata):
        result = get_table_col_description(sample_cube_metadata, ["meeting_details"])
        assert "meeting_details" in result
        assert "employee_details" not in result

    def test_includes_all_when_multiple_tables_match(self, sample_cube_metadata):
        result = get_table_col_description(
            sample_cube_metadata, ["employee_details", "meeting_details"]
        )
        assert "employee_details" in result
        assert "meeting_details" in result

    def test_unknown_table_falls_back_to_all_cubes(self, sample_cube_metadata):
        result = get_table_col_description(sample_cube_metadata, ["nope"])
        assert "employee_details" in result
        assert "meeting_details" in result

    def test_includes_measure_column_name_in_hint(self):
        cube_metadata = [
            {
                "database_table": "travel_details",
                "table_alias": "travel",
                "description": "Travel data",
                "columns": [
                    {
                        "column_name": "booking_platform",
                        "alias_name": "platform",
                        "description": "Platform",
                    }
                ],
                "measures": [
                    {
                        "column_name": "destination_id",
                        "alias_name": "destination_count",
                        "description": "Countable destination id",
                    }
                ],
            }
        ]
        result = get_table_col_description(cube_metadata, ["travel_details"])
        assert "destination_id" in result
        assert "alias: platform" in result
        assert "alias: destination_count" in result


class TestGetRequiredColumnDescription:
    def test_returns_descriptions_for_query_plan_columns(self, sample_cube_metadata):
        employee_cube = next(
            item for item in sample_cube_metadata if item["database_table"] == "employee_details"
        )
        employee_cube["table_alias"] = "employees"
        employee_cube["columns"][1]["alias_name"] = "full_name"
        query_plan = {
            "columnName": [
                "employee_details.employee_name",
                "meeting_details.meeting_id",
            ],
            "reason": "Employee meetings",
        }
        result = get_required_column_description(sample_cube_metadata, query_plan)
        assert "employee_details.employee_name (alias: full_name) [table alias: employees]: Full name" in result
        assert "meeting_details.meeting_id: PK" in result

    def test_accepts_query_plan_json_string(self, sample_cube_metadata):
        query_plan = json.dumps(
            {"columnName": ["meeting_details.meeting_id"], "reason": "Meetings"}
        )
        result = get_required_column_description(sample_cube_metadata, query_plan)
        assert "meeting_details.meeting_id: PK" in result

    def test_returns_empty_when_no_columns_selected(self, sample_cube_metadata):
        assert get_required_column_description(sample_cube_metadata, {}) == ""
