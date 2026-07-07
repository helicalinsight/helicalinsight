"""Functional tests for cube_metadata prevalidation fallback."""
import pytest

from helicalbi.common.JsonToPara import (
    cube_metadata_from_metadata_api,
    has_table_column_info,
    prevalidate_cube_metadata,
)


pytestmark = pytest.mark.functional

_METADATA_API_RESPONSE = {
    "tables": {
        "orders": {
            "columns": {
                "order_id": {"type": {"java.lang.Integer": True}},
                "amount": {"type": {"java.math.BigDecimal": True}},
            }
        },
        "customers": {
            "columns": {
                "customer_id": {"type": {"java.lang.Integer": True}},
            }
        },
    }
}


class TestHasTableColumnInfo:
    def test_true_when_columns_present(self):
        cube_metadata = [
            {
                "database_table": "orders",
                "columns": [{"column_name": "order_id"}],
            }
        ]
        assert has_table_column_info(cube_metadata) is True

    def test_false_when_tables_have_no_columns(self):
        cube_metadata = [{"database_table": "orders", "columns": []}]
        assert has_table_column_info(cube_metadata) is False

    def test_false_when_empty(self):
        assert has_table_column_info([]) is False


class TestCubeMetadataFromMetadataApi:
    def test_builds_tables_and_columns(self):
        result = cube_metadata_from_metadata_api(_METADATA_API_RESPONSE)
        assert len(result) == 2
        orders = next(item for item in result if item["database_table"] == "orders")
        assert orders["columns"] == [
            {"column_name": "order_id"},
            {"column_name": "amount"},
        ]


class TestPrevalidateCubeMetadata:
    def test_keeps_agent_cube_when_schema_present(self):
        cube_metadata = [
            {
                "database_table": "orders",
                "columns": [{"column_name": "order_id"}],
            }
        ]
        result = prevalidate_cube_metadata(cube_metadata, _METADATA_API_RESPONSE)
        assert result is cube_metadata

    def test_falls_back_to_metadata_api_when_agent_cube_empty(self):
        result = prevalidate_cube_metadata([], _METADATA_API_RESPONSE)
        assert len(result) == 2
        assert result[0]["database_table"] in {"orders", "customers"}

    def test_falls_back_when_agent_cube_has_tables_without_columns(self):
        cube_metadata = [{"database_table": "orders", "columns": []}]
        result = prevalidate_cube_metadata(cube_metadata, _METADATA_API_RESPONSE)
        assert has_table_column_info(result) is True
        orders = next(item for item in result if item["database_table"] == "orders")
        assert orders["columns"][0]["column_name"] == "order_id"
