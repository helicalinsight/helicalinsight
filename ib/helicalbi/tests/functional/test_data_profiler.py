"""Functional tests for ``helicalbi.api.DataProfiler``."""
import json
from unittest.mock import patch

import pytest

from helicalbi.api.DataProfiler import (
    SQL_TEMPLATES,
    get_profile_values,
    get_query,
    prepare_formdata,
)
from helicalbi.api.SqlExecutorModel import SqlExecutorModel


pytestmark = pytest.mark.functional


class TestGetQuery:
    @pytest.mark.parametrize(
        ("datatype", "expected_substring"),
        [
            ("numeric", "AVG"),
            ("date", "MIN"),
            ("string", "COUNT(DISTINCT"),
        ],
    )
    def test_supported_datatypes(self, datatype, expected_substring):
        sql = get_query("orders", "amount", datatype)
        assert expected_substring in sql
        assert '"orders"' in sql
        assert '"amount"' in sql

    def test_dateTime_treated_as_date(self):
        sql_date = get_query("t", "c", "date")
        sql_dt = get_query("t", "c", "dateTime")
        assert sql_date == sql_dt

    def test_text_treated_as_string(self):
        sql = get_query("t", "c", "text")
        assert sql == SQL_TEMPLATES["string"].format(table="t", col="c")

    def test_boolean_treated_as_string(self):
        sql = get_query("t", "c", "boolean")
        assert sql == SQL_TEMPLATES["string"].format(table="t", col="c")

    def test_unsupported_datatype_raises(self):
        with pytest.raises(ValueError, match="Unsupported datatype"):
            get_query("t", "c", "json")


class TestPrepareFormdata:
    def test_creates_well_formed_payload(self):
        model = SqlExecutorModel(
            md_location="/loc",
            md_file_name="meta.json",
            table="users",
            column="id",
            data_type="numeric",
        )
        payload = prepare_formdata("SELECT 1", model)

        assert payload["service"] == "executeQuery"
        assert payload["serviceType"] == "report"
        assert payload["type"] == "adhoc"
        assert "requestId" in payload

        form_data = json.loads(payload["formData"])
        assert form_data["query"] == "SELECT 1"
        assert form_data["location"] == "/loc"
        assert form_data["metadataFileName"] == "meta.json"
        assert form_data["refresh"] is True
        assert form_data["provideJoins"] is True


class TestGetProfileValues:
    def test_numeric_profile_returns_first_row(self, session_cookie):
        model = SqlExecutorModel(
            md_location="/loc",
            md_file_name="meta.json",
            table="t",
            column="amount",
            data_type="numeric",
        )
        fake_response = {
            "response": {"data": [{"null_count": 0, "distinct_count": 5}]}
        }
        with patch(
            "helicalbi.api.DataProfiler.fetch_service_api",
            return_value=fake_response,
        ) as mock_fetch:
            result = get_profile_values(model, session_cookie)
        assert result == {"null_count": 0, "distinct_count": 5}
        assert mock_fetch.call_count == 1

    def test_text_profile_makes_two_calls_and_attaches_stats(self, session_cookie):
        model = SqlExecutorModel(
            md_location="/loc",
            md_file_name="meta.json",
            table="t",
            column="city",
            data_type="text",
        )
        responses = [
            {"response": {"data": [{"distinct_count": 3}]}},
            {"response": {"data": [{"val": "Pune", "value_count": 2}]}},
        ]
        with patch(
            "helicalbi.api.DataProfiler.fetch_service_api",
            side_effect=responses,
        ) as mock_fetch:
            result = get_profile_values(model, session_cookie)

        assert mock_fetch.call_count == 2
        assert result["distinct_count"] == 3
        assert result["column_stats"] == [{"val": "Pune", "value_count": 2}]
