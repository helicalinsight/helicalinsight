"""Functional tests for FindJoinFromApi join-column resolution."""

import json
from unittest.mock import patch

import pytest

from helicalbi.core.sqlflow.FindJoinFromApi import (
    FindJoinFromApi,
    _plan_columns_by_table,
    _resolve_join_column,
)

pytestmark = pytest.mark.functional


class TestPlanColumnsByTable:
    def test_parses_qualified_refs_from_dict(self):
        plan = {"columnName": ["travel_details.travel_id", "employee.emp_id"]}
        assert _plan_columns_by_table(plan) == {
            "travel_details": ["travel_id"],
            "employee": ["emp_id"],
        }

    def test_parses_json_string(self):
        plan = json.dumps({"columnName": ["t.a", "t.b"]})
        assert _plan_columns_by_table(plan) == {"t": ["a", "b"]}

    def test_ignores_bare_columns(self):
        assert _plan_columns_by_table({"columnName": ["bare", "t.a"]}) == {"t": ["a"]}

    def test_handles_invalid_input(self):
        assert _plan_columns_by_table("") == {}
        assert _plan_columns_by_table("not-json") == {}
        assert _plan_columns_by_table(None) == {}


class TestResolveJoinColumn:
    def _cube(self):
        return {
            "database_table": "travel_details",
            "columns": [
                {"column_name": "mode_of_payment"},
                {"column_name": "travel_id"},
            ],
        }

    def test_prefers_unique_identifier(self):
        cube = {**self._cube(), "unique_identifier_of_table": "travel_id"}
        assert _resolve_join_column(cube, ["mode_of_payment"]) == "travel_id"

    def test_uses_plan_column_when_no_uid(self):
        assert _resolve_join_column(self._cube(), ["travel_id"]) == "travel_id"

    def test_falls_back_to_first_column_without_plan(self):
        assert _resolve_join_column(self._cube(), None) == "mode_of_payment"

    def test_falls_back_when_plan_column_not_in_cube(self):
        # Plan column unknown to the cube -> use first cube column.
        assert _resolve_join_column(self._cube(), ["unknown_col"]) == "mode_of_payment"

    def test_plan_column_from_measures(self):
        cube = {
            "database_table": "travel_details",
            "columns": [{"column_name": "mode_of_payment"}],
            "measures": [{"column_name": "travel_cost"}],
        }
        assert _resolve_join_column(cube, ["travel_cost"]) == "travel_cost"


class TestFetchSqlUsesPlanColumns:
    def test_join_payload_uses_plan_column(self):
        cube_metadata = [
            {
                "database_table": "travel_details",
                "columns": [
                    {"column_name": "mode_of_payment"},
                    {"column_name": "travel_id"},
                ],
            },
            {
                "database_table": "employee",
                "columns": [
                    {"column_name": "emp_name"},
                    {"column_name": "emp_id"},
                ],
            },
        ]
        query_plan = json.dumps(
            {"columnName": ["travel_details.travel_id", "employee.emp_id"]}
        )

        with patch(
            "helicalbi.core.sqlflow.FindJoinFromApi.fetch_service_api",
            return_value={"response": {"query": "SELECT 1"}},
        ) as mock_api:
            FindJoinFromApi().fetch_sql(
                "cookie",
                "md.json",
                "/",
                tables=["travel_details", "employee"],
                cube_metadata=cube_metadata,
                dbname="demo",
                query_plan=query_plan,
            )

        service_json = mock_api.call_args.kwargs["service_json"]
        form_data = json.loads(service_json["formData"])
        column_names = {col["column"]["name"] for col in form_data["columns"]}
        assert column_names == {
            "demo.travel_details.travel_id",
            "demo.employee.emp_id",
        }
