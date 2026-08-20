"""Functional tests for interactive data_model (base64 query, empty columns)."""
import base64

import pytest

from helicalbi.controller.interactive import _sql_to_data_model


pytestmark = pytest.mark.functional


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
