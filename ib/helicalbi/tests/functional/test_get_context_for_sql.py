"""Functional tests for ``helicalbi.sql.GetContextForSQL``."""
import pytest

from helicalbi.sql.GetContextForSQL import (
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

    def test_unknown_table_yields_empty_hint(self, sample_cube_metadata):
        result = get_table_col_description(sample_cube_metadata, ["nope"])
        assert result == ""
