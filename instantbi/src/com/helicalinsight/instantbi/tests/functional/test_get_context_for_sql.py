"""Functional tests for ``helicalbi.sql.GetContextForSQL``."""
import json

import pytest

from helicalbi.sql.GetContextForSQL import (
    get_required_column_description,
    get_table_col_description,
    get_table_selection_description,
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


class TestGetTableSelectionDescription:
    def test_compact_catalog_omits_column_descriptions_and_aicontext(
        self, sample_cube_metadata
    ):
        cube_metadata = [
            {
                "database_table": "travel_details",
                "description": "Travel data",
                "columns": [
                    {
                        "column_name": "travel_date",
                        "alias_name": "YEAR",
                        "description": "Long column description that must not appear",
                        "ai_context": {
                            "instructions": "never ship this in FindTables",
                            "examples": ["2024"],
                        },
                        "formula": "EXTRACT(YEAR from travel_date)",
                    }
                ],
                "measures": [
                    {
                        "column_name": "travel_cost",
                        "alias_name": "cost",
                        "description": "Measure description",
                        "formula": "sum(travel_cost)",
                    }
                ],
            },
            sample_cube_metadata[0],
        ]
        result = get_table_selection_description(
            cube_metadata,
            model_data={
                "topic_mappings": [
                    {
                        "topic_name": "Sales",
                        "components": [
                            {
                                "name": "travel_cost",
                                "columnName": "travel_details.travel_cost",
                                "formula": "sum(travel_cost)",
                                "aiContext": {"instructions": "omit me"},
                            }
                        ],
                    },
                    {
                        "topic_name": "Meetings",
                        "components": [{"name": "client_name"}],
                    },
                ]
            },
            topics=["Sales"],
        )
        assert "travel_details" in result
        assert "cols:" in result
        assert "travel_date (YEAR)" in result
        assert "travel_cost (cost)" in result
        assert "travel_cost@travel_details" in result
        assert "Sales:" in result
        # Filtered to requested topics when present.
        assert "Meetings:" not in result
        assert "Long column description" not in result
        assert "never ship this" not in result
        assert "omit me" not in result
        assert "EXTRACT(YEAR" not in result
        assert "sum(travel_cost)" not in result
        # Smaller than full semantic hint path.
        full = get_table_col_description(cube_metadata, table_names=None)
        assert len(result) < len(full)

    def test_falls_back_to_all_topics_when_filter_empty(self, sample_cube_metadata):
        result = get_table_selection_description(
            sample_cube_metadata,
            model_data={
                "topic_mappings": [
                    {"topic_name": "Travel", "component": ["Travel"]},
                ]
            },
            topics=["UnknownTopic"],
        )
        assert "Travel:" in result
        assert "employee_details" in result


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
        assert "employee_details.employee_name" in result
        assert "alias: full_name" in result
        assert "Full name" in result
        assert "meeting_details.meeting_id" in result
        assert "PK" in result
        assert "format:" not in result
        assert '"format_string"' not in result
        assert '"formatString"' not in result

    def test_accepts_query_plan_json_string(self, sample_cube_metadata):
        query_plan = json.dumps(
            {"columnName": ["meeting_details.meeting_id"], "reason": "Meetings"}
        )
        result = get_required_column_description(sample_cube_metadata, query_plan)
        assert "meeting_details.meeting_id" in result
        assert "PK" in result

    def test_returns_empty_when_no_columns_selected(self, sample_cube_metadata):
        assert get_required_column_description(sample_cube_metadata, {}) == ""

    def test_includes_hierarchy_ai_context_and_omits_format_string(self):
        from helicalbi.sql.GetContextForSQL import collect_picked_column_items

        cube_metadata = [
            {
                "database_table": "travel_details",
                "columns": [
                    {
                        "column_name": "travel_date",
                        "alias_name": "YEAR",
                        "dimension_name": "YEAR",
                        "hierarchy_name": "travel_date",
                        "level_name": "YEAR",
                        "semantic_type": "Text",
                        "formula": "EXTRACT(YEAR from travel_details.travel_date)",
                        "format_string": "0",
                        "sort_order": "Ascending",
                        "sort_direction": "ASC",
                        "ai_context": {
                            "instructions": "Year of travel.",
                            "synonyms": "year",
                            "examples": "2024",
                        },
                    }
                ],
                "measures": [
                    {
                        "column_name": "Cost by Cancellation",
                        "alias_name": "Cost by Cancellation",
                        "measure_name": "Cost by Cancellation",
                        "is_computed": True,
                        "semantic_type": "Number",
                        "format_string": "$#,##0.00",
                        "formula": "sum(travel_details.travel_cost) filter status=Yes",
                        "ai_context": {
                            "instructions": "Yes means cancelled.",
                            "synonyms": "cancelled",
                            "examples": "",
                        },
                    }
                ],
            }
        ]
        query_plan = {
            "columnName": [
                "travel_details.travel_date",
                "travel_details.Cost by Cancellation",
            ],
            "pickedDimensions": ["YEAR"],
            "pickedMetrics": ["Cost by Cancellation"],
        }
        picked = collect_picked_column_items(cube_metadata, query_plan)
        travel = picked["travel_details"]
        assert travel["hierarchies"][0]["ai_context"]["instructions"] == "Year of travel."
        assert "format_string" not in travel["hierarchies"][0]
        assert travel["computed_measures"][0]["formula"].startswith("sum(")
        assert "format_string" not in travel["computed_measures"][0]

        text = get_required_column_description(cube_metadata, query_plan)
        assert "hierarchy: travel_date" in text
        assert "aiContext:" in text
        assert "Year of travel." in text
        assert "COMPUTED measure" in text
        assert "format:" not in text
        assert "$#,##0.00" not in text
        assert '"format_string"' not in text
        assert "sort: ASC" in text
