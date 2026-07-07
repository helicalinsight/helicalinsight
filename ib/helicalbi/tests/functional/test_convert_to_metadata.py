"""Functional tests for ``helicalbi.core.sqlflow.util.ConvertToMetadata``."""
import pytest

from helicalbi.core.sqlflow.util.ConvertToMetadata import (
    TABLE_PATH,
    build_column,
    convert,
)


pytestmark = pytest.mark.functional


class TestBuildColumn:
    def test_includes_qualified_name_and_alias(self):
        result = build_column("travel_id", "TID", col_id=42)
        assert result == {
            "column": {"name": f"{TABLE_PATH}.travel_id", "id": "42"},
            "alias": "TID",
            "floatingType": "discrete",
        }

    def test_default_id_is_1000(self):
        result = build_column("col", "alias")
        assert result["column"]["id"] == "1000"


class TestConvert:
    def test_converts_column_array(self):
        payload = {
            "columns": [
                {"columnName": "travel_id", "alias": "TID"},
                {"columnName": "destination", "alias": "Dest"},
            ]
        }
        result = convert(payload)
        assert len(result) == 2
        assert result[0]["alias"] == "TID"
        assert result[1]["column"]["name"].endswith("destination")

    def test_empty_columns_returns_empty_list(self):
        assert convert({"columns": []}) == []

    def test_missing_columns_key_returns_empty_list(self):
        assert convert({}) == []
