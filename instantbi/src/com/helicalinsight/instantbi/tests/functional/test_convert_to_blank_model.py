"""Functional tests for ``helicalbi.common.ConvertToBlankModel``."""
from unittest.mock import patch

import pytest

from helicalbi.common.ConvertToBlankModel import map_java_type, transform_json


pytestmark = pytest.mark.functional


class TestMapJavaType:
    @pytest.mark.parametrize(
        ("java_type", "expected"),
        [
            ({"java.lang.String": True}, "text"),
            ({"java.math.BigDecimal": True}, "numeric"),
            ({"java.lang.Integer": True}, "numeric"),
            ({"java.lang.Boolean": True}, "boolean"),
            ({"java.sql.Date": True}, "date"),
            ({"java.sql.Time": True}, "time"),
            ({"java.sql.Timestamp": True}, "dateTime"),
            ({"java.lang.Object": True}, "other"),
        ],
    )
    def test_known_mappings(self, java_type, expected):
        assert map_java_type(java_type) == expected

    def test_unknown_type_defaults_to_text(self):
        assert map_java_type({"java.lang.UnknownThing": True}) == "text"

    def test_empty_dict_returns_empty_string(self):
        assert map_java_type({}) == ""

    def test_none_returns_empty_string(self):
        assert map_java_type(None) == ""


class TestTransformJson:
    def test_collects_semantic_tables_with_profile_values(self, session_cookie):
        md_all = {
            "tables": {
                "users": {
                    "columns": {
                        "id": {"type": {"java.lang.Integer": True}},
                        "name": {"type": {"java.lang.String": True}},
                        "blob": {"type": {"java.sql.Blob": True}},
                    }
                }
            }
        }

        def fake_profile(sql_model, session_cookie):
            return {"distinct": 1, "null": 0}

        with patch(
            "helicalbi.common.ConvertToBlankModel.get_json_data_metadata",
            return_value=md_all,
        ), patch(
            "helicalbi.common.ConvertToBlankModel.get_profile_values",
            side_effect=fake_profile,
        ) as mock_profile:
            result = transform_json(session_cookie, "meta.json", "/dir", [])

        assert result["metadata"] == {"domain": [""], "description": ""}
        assert len(result["semantic_layer"]) == 1
        table = result["semantic_layer"][0]
        assert table["database_table"] == "users"

        columns = {c["column_name"]: c for c in table["columns"]}
        assert "id" in columns
        assert "blob" in columns
        # 'other' types should NOT have column_metrics populated
        assert "column_metrics" not in columns["blob"]
        # numeric/text types should have column_metrics from profiler
        assert columns["id"]["column_metrics"]["data_type"] == "numeric"
        assert columns["name"]["column_metrics"]["data_type"] == "text"
        # profile was called once per non-"other" type
        assert mock_profile.call_count == 2

    def test_empty_tables_returns_no_semantic_tables(self, session_cookie):
        with patch(
            "helicalbi.common.ConvertToBlankModel.get_json_data_metadata",
            return_value={"tables": {}},
        ):
            result = transform_json(session_cookie, "m.json", "/d", [])
        assert result["semantic_layer"] == []
