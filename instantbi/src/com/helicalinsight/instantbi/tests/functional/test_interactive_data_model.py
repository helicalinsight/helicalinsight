"""Functional tests for interactive data_model (base64 query, empty columns)."""
import base64

import pytest

from helicalbi.controller.interactive import _sql_to_data_model
from helicalbi.model.output.ChatResponse import ChatResponse
from helicalbi.sql_to_formdata import FunctionCatalog
from helicalbi.viz.viz_model_fill import _try_sql_to_form_data


pytestmark = pytest.mark.functional


def _count_catalog() -> FunctionCatalog:
    return FunctionCatalog.from_api_payload(
        {
            "response": {
                "reference": "postgresql",
                "functions": {
                    "db.generic.aggregate.count": "count",
                    "db.generic.groupBy.group": "group",
                },
                "databaseFunctions": {
                    "postgresql specific": [
                        {
                            "key": "sql.date.extract",
                            "value": "extract",
                            "signature": "extract(${unit} from ${date})",
                            "returns": "numeric",
                            "parameters": [
                                {"name": "unit", "defaultValue": "'century'"},
                                {"name": "date", "column": True},
                            ],
                        }
                    ]
                },
            }
        }
    )


class TestSqlToDataModel:
    def test_encodes_sql_as_base64_query_with_empty_columns(self):
        sql = 'select "travel_details"."travel_cost" from "travel_details"'
        result = _sql_to_data_model(
            sql,
            location="test",
            metadata_dir="test",
            metadata_file_name="pg_sample_travel_data.metadata",
        )

        assert result["location"] == "test"
        assert result["metadataFileName"] == "pg_sample_travel_data.metadata"
        assert result["columns"] == []
        assert result["query"] == base64.b64encode(sql.encode("utf-8")).decode("utf-8")
        assert base64.b64decode(result["query"]).decode("utf-8") == sql

    def test_strips_markdown_before_encoding(self):
        sql = "SELECT 1"
        result = _sql_to_data_model(
            "```sql\nSELECT 1\n```",
            location="test",
            metadata_dir="test",
            metadata_file_name="meta.metadata",
        )
        assert base64.b64decode(result["query"]).decode("utf-8") == sql

    def test_returns_none_without_sql_or_metadata_refs(self):
        kwargs = {
            "location": "test",
            "metadata_dir": "test",
            "metadata_file_name": "meta.metadata",
        }
        assert _sql_to_data_model("", **kwargs) is None
        assert _sql_to_data_model("SELECT 1", location="", metadata_dir="", metadata_file_name="meta.metadata") is None
        assert _sql_to_data_model("SELECT 1", location="test", metadata_dir="test", metadata_file_name="") is None


class TestSqlErrorDataModelColumns:
    """SQL execution errors skip viz, but data_model.columns still come from SQL."""

    def test_try_sql_to_form_data_fills_columns_without_query_results(self):
        sql = """
        SELECT
          COUNT("meeting_details"."meet_cancellation_status") AS "Cancelled Meetings"
        FROM "meeting_details"
        WHERE
          "meeting_details"."meet_cancellation_status" = 'Yes'
          AND EXTRACT(YEAR FROM "meeting_details"."travel_date") = 2026
        LIMIT 100
        """
        form_data = _try_sql_to_form_data(
            sql,
            md_location="Postgres_travel_data",
            md_file_name="Metadata_1.metadata",
            dialect="postgresql",
            catalog=_count_catalog(),
            metadata={
                "name": "sampletraveldata.public",
                "tables": {
                    "meeting_details": {
                        "columns": {
                            "meet_cancellation_status": {
                                "id": "1",
                                "alias": "meet_cancellation_status",
                            },
                        }
                    }
                },
            },
        )

        assert form_data is not None
        aliases = [col.get("alias") for col in form_data["columns"]]
        assert "Cancelled Meetings" in aliases

    def test_error_response_keeps_columns_and_empty_viz(self):
        response = ChatResponse.from_model_state(
            {
                "sql": "SELECT COUNT(1) AS cancelled FROM meeting_details",
                "sql_error": (
                    "Error: PSQLException: ERROR: column meeting_details.travel_date "
                    "does not exist"
                ),
                "viz_form_data": {
                    "location": "Postgres_travel_data",
                    "metadataFileName": "Metadata_1.metadata",
                    "columns": [{"alias": "Cancelled Meetings", "aggregate": True}],
                    "sql": "SELECT COUNT(1) AS cancelled FROM meeting_details",
                    "query": "c2VsZWN0IDE=",
                },
            }
        )
        payload = response.to_interactive_client_dict()
        assert payload["error"]
        assert payload["viz"]["chart_name"] == ""
        assert payload["report_model"]["viz_model"] is None
        assert payload["report_model"]["data_model"]["columns"] == [
            {"alias": "Cancelled Meetings", "aggregate": True}
        ]
        assert "query" not in payload["report_model"]["data_model"]
