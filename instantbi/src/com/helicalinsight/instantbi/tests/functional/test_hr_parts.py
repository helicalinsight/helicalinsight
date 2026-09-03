"""Functional tests for convert-hreport SQL / viz parts."""
from __future__ import annotations

import pytest

from helicalbi.sql_to_formdata import (
    FunctionCatalog,
    build_convert_hreport_parts,
    form_data_to_sql_parts,
    sql_to_form_data,
    viz_model_to_viz_parts,
)
from helicalbi.sql_to_formdata.hr_parts import hr_metadata_for_convert, resolve_viz_from_sources
from helicalbi.sql_to_formdata.metadata import build_column_index


pytestmark = pytest.mark.functional


def _catalog() -> FunctionCatalog:
    return FunctionCatalog.from_api_payload(
        {
            "response": {
                "reference": "postgresql",
                "functions": {
                    "db.generic.aggregate.sum": "sum",
                    "db.generic.groupBy.group": "group",
                    "db.generic.orderBy.order": "order",
                },
                "databaseFunctions": {},
            }
        }
    )


def _metadata() -> dict:
    return build_column_index(
        {
            "name": "sampletraveldata.public",
            "tables": {
                "travel_details": {
                    "columns": {
                        "booking_platform": {
                            "id": "2868",
                            "alias": "booking_platform",
                        },
                        "travel_cost": {
                            "id": "2866",
                            "alias": "travel_cost",
                            "type": {"java.lang.Integer": "numeric"},
                        },
                    }
                },
            },
        }
    )


def _form_data(sql: str) -> dict:
    return sql_to_form_data(
        sql,
        location="0007",
        metadata_file_name="pg.metadata",
        catalog=_catalog(),
        metadata=_metadata(),
        dialect="postgres",
    )


class TestFormDataToSqlParts:
    def test_splits_dimension_measure_filter_and_order(self):
        sql = """
        SELECT travel_details.booking_platform AS "Platform",
               sum(travel_details.travel_cost) AS "Cost"
        FROM travel_details
        WHERE travel_details.booking_platform = 'Agent'
        ORDER BY sum(travel_details.travel_cost) DESC
        LIMIT 10
        """
        parts = form_data_to_sql_parts(_form_data(sql))
        assert parts["location"] == "0007"
        assert parts["metadataFileName"] == "pg.metadata"
        shelves = {c["alias"]: c["shelf"] for c in parts["columns"]}
        assert shelves["Platform"] == "row"
        assert shelves["Cost"] == "column"
        cost = next(c for c in parts["columns"] if c["alias"] == "Cost")
        assert cost["table"] == "travel_details"
        assert cost["column"] == "travel_cost"
        assert "aggregate.sum" in cost["databaseFunction"]
        assert parts["filters"][0]["column"] == "booking_platform"
        assert parts["filters"][0]["value"] == ["Agent"]
        assert parts["orderBy"][0]["direction"] == "desc"

    def test_omits_hidden_order_by_columns_from_visible_columns(self):
        sql = """
        SELECT travel_details.booking_platform AS "Platform"
        FROM travel_details
        ORDER BY travel_details.travel_cost ASC
        """
        parts = form_data_to_sql_parts(_form_data(sql))
        aliases = [c["alias"] for c in parts["columns"]]
        assert "Platform" in aliases
        assert all(c.get("shelf") == "row" for c in parts["columns"])
        assert parts["orderBy"]
        assert parts["orderBy"][0]["direction"] == "asc"


class TestVizParts:
    def test_passes_chart_color_background_title(self):
        viz = {
            "chart_name": "bar",
            "vf_title": "Fallback title",
            "viz_model": {
                "chart": {"mark": "Chart", "viz": "Bar"},
                "properties": {
                    "title": "Travel cost",
                    "color": "#5470c6",
                    "background": "#ffffff",
                },
            },
        }
        parts = viz_model_to_viz_parts(viz)
        assert parts["chart_name"] == "bar"
        assert parts["mark"] == "Chart"
        assert parts["viz"] == "Bar"
        assert parts["color"] == "#5470c6"
        assert parts["background"] == "#ffffff"
        assert parts["title"] == "Travel cost"
        assert parts["colorField"] == ""

    def test_non_hex_color_becomes_color_field(self):
        parts = viz_model_to_viz_parts(
            {
                "viz_model": {
                    "chart": {"mark": "Chart", "viz": "Bar"},
                    "properties": {"color": "booking_platform"},
                }
            }
        )
        assert parts["color"] == ""
        assert parts["colorField"] == "booking_platform"


class TestBuildConvertHreportParts:
    def test_contract_has_sql_and_viz_parts_not_wire_formdata(self):
        form_data = {
            "location": "/meta",
            "metadataFileName": "m.metadata",
            "columns": [
                {
                    "column": {"name": "t.region", "id": "1"},
                    "alias": "region",
                    "floatingType": "discrete",
                }
            ],
            "sql": "SELECT region FROM t",
            "limitBy": 10,
        }
        payload = build_convert_hreport_parts(
            form_data,
            viz={"chart_name": "table", "viz_model": {"chart": {"mark": "Table", "viz": ""}}},
        )
        assert set(payload.keys()) == {"sql_parts", "viz_parts"}
        assert "limitBy" not in payload
        assert payload["sql_parts"]["columns"][0]["column"] == "region"
        assert payload["viz_parts"]["mark"] == "Table"

    def test_omits_tables_metadata_from_payload(self):
        form_data = {
            "location": "/meta",
            "metadataFileName": "m.metadata",
            "columns": [
                {
                    "column": {"name": "t.region", "id": "1"},
                    "alias": "region",
                    "floatingType": "discrete",
                }
            ],
        }
        metadata = {
            "name": "sample.public",
            "tables": {
                "t": {
                    "alias": "t",
                    "columns": {
                        "region": {
                            "id": "1",
                            "alias": "region",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {"java.lang.String": "text"},
                        }
                    },
                }
            },
        }
        payload = build_convert_hreport_parts(form_data, metadata=metadata)
        assert "metadata" not in payload
        assert payload["sql_parts"]["location"] == "/meta"
        assert payload["sql_parts"]["metadataFileName"] == "m.metadata"
        shaped = hr_metadata_for_convert(
            metadata,
            location="/meta",
            metadata_file_name="m.metadata",
        )
        assert shaped["tables"]["t"]["columns"]["region"]["alias"] == "region"
        assert shaped["formData"] == {
            "location": "/meta",
            "metadataFileName": "m.metadata",
        }
        assert shaped["classifier"] == "db.generic"
        assert "by_column" not in shaped

    def test_unwraps_nested_agent_metadata_data(self):
        shaped = hr_metadata_for_convert(
            {"data": {"tables": {"t": {"columns": {}}}}},
            location="/meta",
            metadata_file_name="m.metadata",
        )
        assert "t" in shaped["tables"]

    def test_unwraps_instantbi_model_nested_helical_tables(self):
        shaped = hr_metadata_for_convert(
            {
                "data": {
                    "metadata": {
                        "location": "test",
                        "metadataFileName": "pg_sample_travel_data.metadata",
                        "data": {"tables": {"travel_details": {"columns": {}}}},
                    }
                }
            }
        )
        assert "travel_details" in shaped["tables"]


class TestResolveVizFromSources:
    def test_prefers_request_then_chat_item_then_memory(self):
        assert resolve_viz_from_sources({"viz": {"chart_name": "pie"}})["chart_name"] == "pie"
        assert (
            resolve_viz_from_sources(
                {"chat_response_item": {"viz": {"chart_name": "bar"}}},
            )["chart_name"]
            == "bar"
        )
        assert (
            resolve_viz_from_sources(
                {},
                {"chat_response": {"viz": {"chart_name": "line"}}},
            )["chart_name"]
            == "line"
        )


class TestSqlPartsFromItem:
    def _metadata(self):
        return {
            "tables": {
                "travel_details": {
                    "columns": {
                        "travel_type": {
                            "alias": "travel_type",
                            "defaultFunction": "db.generic.groupBy.group",
                        },
                        "travel_cost": {
                            "alias": "travel_cost",
                            "defaultFunction": "db.generic.aggregate.sum",
                        },
                    }
                }
            }
        }

    def test_maps_display_names_onto_snake_case_columns(self):
        from helicalbi.sql_to_formdata.hr_parts import sql_parts_from_item

        parts = sql_parts_from_item(
            {
                "sql_parts": {},
                "viz": {
                    "viz_model": {
                        "data": {
                            "rows": ["Travel Type"],
                            "columns": ["Travel Cost"],
                        }
                    }
                },
            },
            self._metadata(),
        )
        shelves = {(c["column"], c["shelf"]) for c in parts["columns"]}
        assert shelves == {("travel_type", "row"), ("travel_cost", "column")}

    def test_parses_quoted_table_column_sql(self):
        from helicalbi.sql_to_formdata.hr_parts import sql_parts_from_sql

        parts = sql_parts_from_sql(
            'SELECT "travel_details"."travel_type" AS "Travel Type", '
            'SUM("travel_details"."travel_cost") AS "Travel Cost" '
            'FROM "travel_details"',
            self._metadata(),
        )
        by_column = {c["column"]: c for c in parts["columns"]}
        assert by_column["travel_type"]["shelf"] == "row"
        assert by_column["travel_cost"]["shelf"] == "column"
        assert "aggregate.sum" in by_column["travel_cost"]["databaseFunction"]
