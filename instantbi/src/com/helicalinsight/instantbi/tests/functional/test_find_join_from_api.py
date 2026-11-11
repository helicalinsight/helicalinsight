"""Functional tests for FindJoinFromApi join-column resolution."""

import json
from unittest.mock import patch

import pytest

from helicalbi.core.sqlflow.FindJoinFromApi import (
    FindJoinFromApi,
    _join_tables_from_select,
    _plan_columns_by_table,
    _resolve_join_column,
    _select_clause_refs,
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

    def test_keeps_select_columns_and_adds_filter_only_tables(self):
        plan = {
            "columnName": [
                "travel_details.travel_cost",
                "employee.emp_id",
            ],
            "selectColumnName": ["travel_details.travel_cost"],
        }
        assert _plan_columns_by_table(plan) == {
            "travel_details": ["travel_cost"],
            "employee": ["emp_id"],
        }
        assert _select_clause_refs(plan) == ["travel_details.travel_cost"]

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


class TestJoinApiUsesSelectClause:
    _CUBES = [
        {
            "database_table": "travel_details",
            "unique_identifier_of_table": "travel_id",
            "columns": [
                {"column_name": "mode_of_payment"},
                {"column_name": "travel_id"},
                {"column_name": "travel_cost"},
            ],
        },
        {
            "database_table": "employee",
            "unique_identifier_of_table": "emp_id",
            "columns": [
                {"column_name": "emp_name"},
                {"column_name": "emp_id"},
            ],
        },
    ]

    def test_payload_sends_all_select_columns_not_unique_ids(self):
        query_plan = {
            "columnName": [
                "travel_details.travel_cost",
                "travel_details.travel_id",
                "employee.emp_name",
            ],
            "selectColumnName": [
                "travel_details.travel_cost",
                "employee.emp_name",
            ],
        }

        with patch(
            "helicalbi.core.sqlflow.FindJoinFromApi.fetch_service_api",
            return_value={"response": {"query": "SELECT 1"}},
        ) as mock_api:
            FindJoinFromApi().fetch_sql(
                "cookie",
                "md.json",
                "/",
                tables=["travel_details", "employee"],
                cube_metadata=self._CUBES,
                dbname="demo",
                query_plan=query_plan,
            )

        service_json = mock_api.call_args.kwargs["service_json"]
        form_data = json.loads(service_json["formData"])
        column_names = {col["column"]["name"] for col in form_data["columns"]}
        assert column_names == {
            "demo.travel_details.travel_cost",
            "demo.employee.emp_name",
        }

    def test_sends_filter_only_table_so_the_join_is_built(self):
        query_plan = {
            "columnName": [
                "travel_details.travel_cost",
                "employee.emp_id",
            ],
            "selectColumnName": ["travel_details.travel_cost"],
        }

        with patch(
            "helicalbi.core.sqlflow.FindJoinFromApi.fetch_service_api",
            return_value={"response": {"query": "SELECT 1"}},
        ) as mock_api:
            FindJoinFromApi().fetch_sql(
                "cookie",
                "md.json",
                "/",
                tables=["travel_details", "employee"],
                cube_metadata=self._CUBES,
                dbname="demo",
                query_plan=query_plan,
            )

        service_json = mock_api.call_args.kwargs["service_json"]
        form_data = json.loads(service_json["formData"])
        column_names = {col["column"]["name"] for col in form_data["columns"]}
        assert column_names == {
            "demo.travel_details.travel_cost",
            "demo.employee.emp_id",
        }

    def test_process_flow_fetches_join_when_filter_uses_another_table(self):
        state = {
            "session_cookie": "cookie",
            "cube_metadata": self._CUBES,
            "metadata_file_name": "md.json",
            "location": "/",
            "required_tables": ["travel_details", "employee"],
            "dbname": "demo",
            "dialect": "PostgreSQL",
            "query_plan": json.dumps(
                {
                    "columnName": [
                        "travel_details.travel_cost",
                        "employee.emp_id",
                    ],
                    "selectColumnName": ["travel_details.travel_cost"],
                }
            ),
        }

        with patch(
            "helicalbi.core.sqlflow.FindJoinFromApi.fetch_service_api",
            return_value={
                "response": {
                    "query": "SELECT t.travel_cost FROM travel_details t JOIN employee e ON e.emp_id = t.travelled_by"
                }
            },
        ) as mock_api:
            result = FindJoinFromApi().process_flow(state)

        mock_api.assert_called_once()
        assert "JOIN employee" in result["required_joins"]

    def test_fetches_join_when_select_column_belongs_to_another_cube_table(self):
        cubes = [
            {
                "database_table": "meeting_details",
                "columns": [{"column_name": "client_name"}],
            },
            {
                "database_table": "travel_details",
                "columns": [
                    {"column_name": "travel_id"},
                    {"column_name": "travel_cost"},
                ],
            },
        ]
        state = {
            "session_cookie": "cookie",
            "cube_metadata": cubes,
            "metadata_file_name": "md.json",
            "location": "/",
            "required_tables": ["travel_details"],
            "dbname": "demo",
            "dialect": "PostgreSQL",
            "query_plan": json.dumps(
                {
                    "columnName": [
                        "meeting_details.client_name",
                        "travel_details.travel_id",
                        "travel_details.travel_cost",
                    ],
                    "selectColumnName": [
                        "meeting_details.client_name",
                        "travel_details.travel_id",
                        "travel_details.travel_cost",
                    ],
                }
            ),
        }

        with patch(
            "helicalbi.core.sqlflow.FindJoinFromApi.fetch_service_api",
            return_value={
                "response": {
                    "query": (
                        "SELECT m.client_name FROM meeting_details m "
                        "JOIN travel_details t ON t.travelled_by = m.meeting_by"
                    )
                }
            },
        ) as mock_api:
            result = FindJoinFromApi().process_flow(state)

        mock_api.assert_called_once()
        assert "JOIN travel_details" in result["required_joins"]

    def test_refresh_joins_calls_the_api_for_corrected_columns(self):
        state = {
            "session_cookie": "cookie",
            "cube_metadata": [
                {
                    "database_table": "meeting_details",
                    "columns": [{"column_name": "client_name"}, {"column_name": "meeting_by"}],
                },
                {
                    "database_table": "travel_details",
                    "columns": [{"column_name": "travel_id"}, {"column_name": "travelled_by"}],
                },
            ],
            "metadata_file_name": "md.json",
            "location": "/",
            "dbname": "demo",
            "dialect": "PostgreSQL",
            "required_joins": "",
        }
        with patch(
            "helicalbi.core.sqlflow.FindJoinFromApi.fetch_service_api",
            return_value={
                "response": {
                    "query": (
                        "SELECT m.client_name FROM meeting_details m "
                        "JOIN travel_details t ON t.travelled_by = m.meeting_by"
                    )
                }
            },
        ) as mock_api:
            join_text = FindJoinFromApi().refresh_joins(
                state,
                ["meeting_details.client_name", "travel_details.travel_id"],
            )

        mock_api.assert_called_once()
        assert "JOIN travel_details" in join_text
        assert state["required_joins"] == join_text

    def test_join_tables_include_filter_only_tables(self):
        plan = {
            "columnName": [
                "travel_details.travel_cost",
                "employee.emp_id",
            ],
            "selectColumnName": ["travel_details.travel_cost"],
        }
        assert _join_tables_from_select(plan, ["travel_details", "employee"]) == [
            "travel_details",
            "employee",
        ]

    def test_raises_when_generate_query_returns_status_zero(self):
        from helicalbi.api.HttpCallService import ServiceApiError

        cube_metadata = [
            {
                "database_table": "travel_details",
                "columns": [{"column_name": "travel_id"}],
            },
            {
                "database_table": "employee",
                "columns": [{"column_name": "emp_id"}],
            },
        ]
        query_plan = {
            "selectColumnName": [
                "travel_details.travel_id",
                "employee.emp_id",
            ]
        }
        with patch(
            "helicalbi.core.sqlflow.FindJoinFromApi.fetch_service_api",
            return_value={"status": 0, "response": {"message": "Error: join failed"}},
        ):
            with pytest.raises(ServiceApiError, match="join failed"):
                FindJoinFromApi().fetch_sql(
                    "cookie",
                    "md.json",
                    "/",
                    tables=["travel_details", "employee"],
                    cube_metadata=cube_metadata,
                    dbname="demo",
                    query_plan=query_plan,
                )
