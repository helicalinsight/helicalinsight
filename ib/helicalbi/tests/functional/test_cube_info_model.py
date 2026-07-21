"""Functional tests for cube_info model detection and conversion."""

import pytest

from helicalbi.common.CubeInfoModel import (
    business_metrics_from_cube_info,
    cube_info_to_cube_metadata,
    extract_domain_topics,
    format_format_strings_for_prompt,
    format_sort_orders_for_prompt,
    format_strings_from_cube_info,
    is_cube_info_model,
    merge_business_metrics,
    prepare_cube_info_model_data,
    sort_direction_from_value,
    sort_orders_from_cube_info,
)

pytestmark = pytest.mark.functional

_METADATA_RESPONSE = {
    "tables": {
        "travel_details": {
            "id": "112",
            "alias": "travel",
            "columns": {
                "booking_platform": {"id": "1074", "alias": "platform"},
                "destination_id": {"id": "1070", "alias": "destination_count"},
            },
        }
    }
}

_CUBE_INFO_AGENT = {
    "domain": [
        {
            "domain_name": "Sales Operation",
            "description": "Travel domain metadata",
            "topics": ["Travel", "Meetings"],
        }
    ],
    "cube_info": [
        {
            "cubeName": "Travel Cube",
            "dimensions": [
                {
                    "dimensionName": "Booking Platform",
                    "semanticType": "string",
                    "synonyms": ["platform"],
                    "tableId": "112",
                    "columnName": "booking_platform",
                    "columnId": "1074",
                    "description": "Booking platform name",
                }
            ],
            "measures": [
                {
                    "measureName": "Destination Count",
                    "aggregator": "count",
                    "columnId": "1070",
                    "tableId": "112",
                    "formatString": "0.00",
                    "semanticType": "numeric",
                    "columnName": "destination_id",
                    "description": "Number of destinations",
                    "metric": {},
                }
            ],
        }
    ],
}


class TestIsCubeInfoModel:
    def test_true_for_cube_info_structure(self):
        assert is_cube_info_model(_CUBE_INFO_AGENT) is True

    def test_false_for_legacy_cube_metadata(self):
        assert is_cube_info_model({"cube_metadata": [{"database_table": "t"}]}) is False

    def test_false_for_empty_payload(self):
        assert is_cube_info_model({}) is False


class TestCubeInfoConversion:
    def test_extracts_domain_and_topics(self):
        domains, topics = extract_domain_topics(_CUBE_INFO_AGENT)
        assert domains == ["Sales Operation"]
        assert topics == ["Travel", "Meetings"]

    def test_converts_cube_info_to_cube_metadata(self):
        result = cube_info_to_cube_metadata(
            _CUBE_INFO_AGENT["cube_info"],
            _METADATA_RESPONSE,
            domain_topics=["Travel", "Meetings"],
        )
        assert len(result) == 1
        table = result[0]
        assert table["database_table"] == "travel_details"
        assert table["table_alias"] == "travel"
        assert table["columns"][0]["column_name"] == "booking_platform"
        assert table["columns"][0]["alias_name"] == "Booking Platform"
        assert table["measures"][0]["column_name"] == "destination_id"
        assert table["measures"][0]["alias_name"] == "Destination Count"

    def test_prepare_cube_info_model_data(self):
        prepared = prepare_cube_info_model_data(_CUBE_INFO_AGENT, _METADATA_RESPONSE)
        assert prepared["domain"] == ["Sales Operation"]
        assert prepared["topics"] == ["Travel", "Meetings"]
        assert prepared["cube_metadata"][0]["database_table"] == "travel_details"
        assert prepared["business_metrics"] == [
            {
                "metric": "Destination Count",
                "description": "Number of destinations (aggregation: count)",
                "tables": ["travel_details"],
                "table_alias": "travel",
                "column_name": "destination_id",
                "column_alias": "Destination Count",
                "measure_name": "Destination Count",
                "aggregator": "count",
                "format_string": "0.00",
            }
        ]
        assert "Domain: Sales Operation" in prepared["domain_context"]
        assert prepared["topic_mappings"]
        assert prepared["topic_mappings"][0]["topic_name"] == "Travel"

    def test_business_metrics_from_cube_info_uses_metric_object(self):
        cube_info = [
            {
                "measures": [
                    {
                        "measureName": "Canceled Meetings",
                        "tableId": "112",
                        "columnName": "destination_id",
                        "metric": {
                            "metric": "canceled_meetings",
                            "description": "Total canceled meetings",
                            "aggregator": "count",
                        },
                    }
                ]
            }
        ]
        result = business_metrics_from_cube_info(cube_info, _METADATA_RESPONSE)
        assert result[0]["metric"] == "canceled_meetings"
        assert result[0]["description"] == "Total canceled meetings (aggregation: count)"
        assert result[0]["tables"] == ["travel_details"]

    def test_business_metrics_from_dimension_formula(self):
        metadata_response = {
            "tables": {
                "meeting_details": {
                    "id": "347",
                    "alias": "meetings",
                    "columns": {
                        "client_name": {"id": "2849", "alias": "client"},
                        "meeting_by": {"id": "2848", "alias": "meeting_owner"},
                    },
                }
            }
        }
        cube_info = [
            {
                "dimensions": [
                    {
                        "dimensionName": "client",
                        "tableId": "347",
                        "columnName": "client_name",
                        "columnId": "2849",
                        "description": "This description says the client name",
                        "metric": {"formula": "concat('mr',client_name)"},
                    }
                ],
                "measures": [
                    {
                        "measureName": "meeting_by",
                        "aggregator": "Sum",
                        "columnId": "2848",
                        "tableId": "347",
                        "formatString": "0.00",
                        "columnName": "meeting_by",
                        "description": "This description tells about count of the meeting",
                        "metric": {"formula": "count (meeting_by)"},
                    }
                ],
            }
        ]
        result = business_metrics_from_cube_info(cube_info, metadata_response)
        assert len(result) == 2

        client_metric = next(item for item in result if item["metric"] == "client")
        assert client_metric == {
            "metric": "client",
            "description": "This description says the client name",
            "tables": ["meeting_details"],
            "table_alias": "meetings",
            "column_name": "client_name",
            "column_alias": "client",
            "dimension_name": "client",
            "formula": "concat('mr',client_name)",
        }

        meeting_metric = next(item for item in result if item["metric"] == "meeting_by")
        assert meeting_metric["formula"] == "count (meeting_by)"
        assert meeting_metric["aggregator"] == "Sum"
        assert meeting_metric["format_string"] == "0.00"
        assert meeting_metric["column_alias"] == "meeting_by"

    def test_business_metrics_use_aggregator_and_format_from_metric_object(self):
        cube_info = [
            {
                "dimensions": [
                    {
                        "dimensionName": "client",
                        "tableId": "347",
                        "columnName": "client_name",
                        "description": "Client display name",
                        "metric": {
                            "formula": "concat('mr',client_name)",
                            "aggregator": "none",
                            "formatString": "#,##0",
                        },
                    }
                ]
            }
        ]
        metadata_response = {
            "tables": {
                "meeting_details": {
                    "id": "347",
                    "columns": {"client_name": {"id": "2849"}},
                }
            }
        }
        result = business_metrics_from_cube_info(cube_info, metadata_response)
        assert result[0]["aggregator"] == "none"
        assert result[0]["format_string"] == "#,##0"
        assert result[0]["formula"] == "concat('mr',client_name)"

    def test_business_metrics_skips_dimension_without_formula(self):
        cube_info = [
            {
                "dimensions": [
                    {
                        "dimensionName": "client",
                        "tableId": "112",
                        "columnName": "booking_platform",
                        "description": "Booking platform",
                    }
                ]
            }
        ]
        result = business_metrics_from_cube_info(cube_info, _METADATA_RESPONSE)
        assert result == []

    def test_resolves_table_and_column_from_ids_without_column_name(self):
        cube_info = [
            {
                "measures": [
                    {
                        "measureName": "Orphan Metric",
                        "tableId": "112",
                        "columnId": "1070",
                    }
                ]
            }
        ]
        metrics = business_metrics_from_cube_info(cube_info, _METADATA_RESPONSE)
        assert metrics[0]["tables"] == ["travel_details"]
        assert metrics[0]["column_name"] == "destination_id"
        assert metrics[0]["column_alias"] == "Orphan Metric"

        result = cube_info_to_cube_metadata(cube_info, _METADATA_RESPONSE)
        assert result[0]["database_table"] == "travel_details"
        assert result[0]["measures"][0]["column_name"] == "destination_id"
        assert result[0]["measures"][0]["alias_name"] == "Orphan Metric"

    def test_resolves_table_and_alias_from_column_name_only(self):
        cube_info = [
            {
                "dimensions": [
                    {
                        "dimensionName": "Booking Platform",
                        "columnName": "booking_platform",
                    }
                ]
            }
        ]
        result = cube_info_to_cube_metadata(cube_info, _METADATA_RESPONSE)
        table = result[0]
        assert table["database_table"] == "travel_details"
        assert table["table_alias"] == "travel"
        assert table["columns"][0]["column_name"] == "booking_platform"
        assert table["columns"][0]["alias_name"] == "Booking Platform"

    def test_resolves_qualified_table_column_name(self):
        metadata_response = {
            "tables": {
                "meeting_details": {
                    "alias": "meetings",
                    "columns": {
                        "client_name": {"alias": "client"},
                    },
                }
            }
        }
        cube_info = [
            {
                "dimensions": [
                    {
                        "dimensionName": "client",
                        "columnName": "meeting_details.client_name",
                        "description": "Client name",
                    }
                ],
                "measures": [
                    {
                        "measureName": "client metric",
                        "columnName": "meeting_details.client_name",
                        "metric": {"formula": "count(client_name)"},
                    }
                ],
            }
        ]
        result = cube_info_to_cube_metadata(cube_info, metadata_response)
        table = result[0]
        assert table["database_table"] == "meeting_details"
        assert table["columns"][0]["column_name"] == "client_name"
        assert table["columns"][0]["alias_name"] == "client"

        metrics = business_metrics_from_cube_info(cube_info, metadata_response)
        assert metrics[0]["column_name"] == "client_name"
        assert metrics[0]["tables"] == ["meeting_details"]
        assert metrics[0]["column_alias"] == "client metric"

    def test_alias_falls_back_to_metadata_when_name_missing(self):
        cube_info = [
            {
                "dimensions": [
                    {
                        "columnName": "booking_platform",
                        "tableId": "112",
                        "columnId": "1074",
                    }
                ],
                "measures": [
                    {
                        "columnName": "destination_id",
                        "tableId": "112",
                        "columnId": "1070",
                        "metric": {"formula": "count(destination_id)"},
                    }
                ],
            }
        ]
        result = cube_info_to_cube_metadata(cube_info, _METADATA_RESPONSE)
        table = result[0]
        assert table["columns"][0]["alias_name"] == "platform"
        assert table["measures"][0]["alias_name"] == "destination_count"

        metrics = business_metrics_from_cube_info(cube_info, _METADATA_RESPONSE)
        assert metrics[0]["column_alias"] == "destination_count"

    def test_merge_business_metrics_prefers_first_source(self):
        first = [{"metric": "a", "tables": ["t1"], "description": "first"}]
        second = [{"metric": "a", "tables": ["t1"], "description": "second"}]
        assert merge_business_metrics(first, second) == first


class TestHierarchyAndAiContext:
    _METADATA = {
        "tables": {
            "employee_details": {
                "id": "348",
                "alias": "employees",
                "columns": {
                    "address": {"id": "2857", "alias": "addr"},
                    "employee_name": {"id": "2855", "alias": "emp_name"},
                },
            },
            "geo_cordinates": {
                "id": "350",
                "alias": "geo",
                "columns": {
                    "location": {"id": "2871", "alias": "loc"},
                },
            },
        }
    }

    _AGENT = {
        "domain": [
            {
                "domain_name": "business domain",
                "description": "",
                "topics": [
                    {
                        "topic": "topic",
                        "description": "",
                        "components": [
                            {"id": "2857", "name": "address"},
                            {"id": "2855", "name": "employee_name"},
                        ],
                    },
                    {
                        "topic": "topic 2",
                        "description": "",
                        "components": [
                            {"id": "2857", "name": "address"},
                            {"id": "2855", "name": "employee_name"},
                            {
                                "id": "eb541c54-f2fc-44be-b92f-5a08895a1c6d",
                                "name": "metric_1",
                            },
                        ],
                    },
                ],
            }
        ],
        "cube_info": [
            {
                "cubeName": "",
                "dimensions": [
                    {
                        "dimensionName": "address",
                        "semanticType": "",
                        "tableId": "348",
                        "columnName": "employee_details.address",
                        "columnId": "9a776c12-50bd-40d0-9a10-27e4ee6223ed",
                        "defaultFunction": "db.generic.groupBy.group",
                        "description": "",
                        "metric": {"formula": ""},
                        "sortOrder": 0,
                        "hierarchies": [
                            {
                                "hierarchyName": "address",
                                "primaryColumnId": "9a776c12-50bd-40d0-9a10-27e4ee6223ed",
                                "tableId": "348",
                                "columnName": "employee_details.address",
                                "levels": [
                                    {
                                        "levelName": "address",
                                        "semanticType": "Text",
                                        "tableId": "348",
                                        "columnName": "employee_details.address",
                                        "columnId": "2857",
                                        "defaultFunction": "db.generic.groupBy.group",
                                        "description": "this is des",
                                        "metric": {"formula": "this is formula"},
                                        "aiContext": {
                                            "instructions": "inst",
                                            "synonyms": "synonyms",
                                            "examples": "examples",
                                        },
                                    },
                                    {
                                        "levelName": "employee_name",
                                        "semanticType": "Text",
                                        "tableId": "348",
                                        "columnName": "employee_details.employee_name",
                                        "columnId": "2855",
                                        "defaultFunction": "db.generic.groupBy.group",
                                        "description": "d2",
                                        "metric": {"formula": "f2"},
                                        "aiContext": {
                                            "instructions": "i2",
                                            "synonyms": "s2",
                                            "examples": "e2",
                                        },
                                    },
                                ],
                            }
                        ],
                        "aiContext": {
                            "instructions": "",
                            "synonyms": "",
                            "examples": "",
                        },
                    },
                    {
                        "dimensionName": "location",
                        "semanticType": "Text",
                        "tableId": "350",
                        "columnName": "geo_cordinates.location",
                        "columnId": "2871",
                        "defaultFunction": "db.generic.groupBy.group",
                        "description": "d4",
                        "metric": {"formula": "f4"},
                        "sortOrder": 1,
                        "aiContext": {
                            "instructions": "i4",
                            "synonyms": "s4",
                            "examples": "e4",
                        },
                    },
                ],
                "measures": [
                    {
                        "measureName": "metric_1",
                        "aggregator": "Sum",
                        "metricId": "eb541c54-f2fc-44be-b92f-5a08895a1c6d",
                        "tableId": "",
                        "defaultFunction": "",
                        "formatString": "0.00",
                        "semanticType": "Number",
                        "columnName": "",
                        "description": "d3",
                        "metric": {"formula": "f3"},
                        "sortOrder": 2,
                        "aiContext": {
                            "instructions": "j3",
                            "synonyms": "s3",
                            "examples": "e3",
                        },
                    }
                ],
            }
        ],
    }

    def test_expands_hierarchy_levels_into_dimensions(self):
        from helicalbi.common.CubeInfoModel import expand_hierarchies_in_cube_info

        expanded = expand_hierarchies_in_cube_info(self._AGENT["cube_info"])
        dim_names = [
            dim["dimensionName"] for dim in expanded[0]["dimensions"]
        ]
        assert dim_names == ["address", "employee_name", "location"]
        address = expanded[0]["dimensions"][0]
        assert address["columnId"] == "2857"
        assert address["ai_instructions"] == "inst"
        assert address["synonyms"] == ["synonyms"]
        assert "AI instructions (SQL/viz): inst" in address["description"]

    def test_expands_hierarchy_levels_into_measures(self):
        from helicalbi.common.CubeInfoModel import expand_hierarchies_in_cube_info

        cube_info = [
            {
                "measures": [
                    {
                        "measureName": "parent_metric",
                        "hierarchies": [
                            {
                                "levels": [
                                    {
                                        "levelName": "child_metric",
                                        "columnName": "employee_details.address",
                                        "tableId": "348",
                                        "columnId": "2857",
                                        "aggregator": "Sum",
                                        "aiContext": {
                                            "instructions": "use sum",
                                            "synonyms": "total,amount",
                                            "examples": "sum of address",
                                        },
                                    }
                                ]
                            }
                        ],
                    }
                ]
            }
        ]
        expanded = expand_hierarchies_in_cube_info(cube_info)
        measure_names = [
            measure["measureName"] for measure in expanded[0]["measures"]
        ]
        assert measure_names == ["child_metric", "parent_metric"]
        child = expanded[0]["measures"][0]
        assert child["synonyms"] == ["total", "amount"]
        assert child["ai_instructions"] == "use sum"

    def test_prepare_uses_hierarchy_and_domain_components(self):
        prepared = prepare_cube_info_model_data(self._AGENT, self._METADATA)
        assert prepared["topics"] == ["topic", "topic 2"]
        assert prepared["domain"] == ["business domain"]

        aliases = {
            column["alias_name"]
            for table in prepared["cube_metadata"]
            for column in table.get("columns") or []
        }
        assert "address" in aliases
        assert "employee_name" in aliases
        assert "location" in aliases

        address_col = next(
            column
            for table in prepared["cube_metadata"]
            for column in table.get("columns") or []
            if column.get("alias_name") == "address"
        )
        assert address_col["ai_instructions"] == "inst"
        assert "synonyms" in (address_col.get("synonyms") or [])
        assert address_col.get("sort_order") == 0
        assert address_col.get("sort_direction") == "ASC"

        topic_map = {
            entry["topic_name"]: entry["component"]
            for entry in prepared["topic_mappings"]
        }
        assert topic_map["topic"] == [
            " alias:-address",
            " alias:-employee_name",
        ]
        assert " alias:-metric_1" in topic_map["topic 2"]

        synonym_dims = {
            entry["dimension_name"]: entry["synonyms"]
            for entry in prepared["synonyms"]
        }
        assert synonym_dims["address"] == ["synonyms"]
        assert synonym_dims["location"] == ["s4"]

        assert "topic" in prepared["domain_context"]
        assert "components: address, employee_name" in prepared["domain_context"]

        assert prepared["format_strings"]["metric_1"] == "0.00"
        assert "0.00" in prepared["column_format_strings"]

        sort_by_name = {entry["name"]: entry for entry in prepared["sort_orders"]}
        assert sort_by_name["address"]["direction"] == "ASC"
        assert sort_by_name["location"]["direction"] == "DESC"
        assert sort_by_name["metric_1"]["direction"] == "ASC"
        assert "address: ASC" in prepared["column_sort_orders"]
        assert "location: DESC" in prepared["column_sort_orders"]

    def test_extract_domain_topics_supports_object_topics(self):
        domains, topics = extract_domain_topics(self._AGENT)
        assert domains == ["business domain"]
        assert topics == ["topic", "topic 2"]


class TestFormatStringAndSortOrder:
    def test_sort_direction_mapping(self):
        assert sort_direction_from_value(0) == "ASC"
        assert sort_direction_from_value(1) == "DESC"
        assert sort_direction_from_value(2) == "ASC"
        assert sort_direction_from_value("asc") == "ASC"
        assert sort_direction_from_value("Ascending") == "ASC"
        assert sort_direction_from_value("DESC") == "DESC"
        assert sort_direction_from_value("Descending") == "DESC"

    def test_format_strings_map_by_names(self):
        cube_info = [
            {
                "dimensions": [
                    {
                        "dimensionName": "client",
                        "columnName": "meeting_details.client_name",
                        "formatString": "#,##0",
                    }
                ],
                "measures": [
                    {
                        "measureName": "Travel Cost",
                        "formatString": "0.00",
                        "metric": {"formula": "SUM(x)"},
                    }
                ],
            }
        ]
        mapping = format_strings_from_cube_info(cube_info)
        assert mapping["client"] == "#,##0"
        assert mapping["client_name"] == "#,##0"
        assert mapping["Travel Cost"] == "0.00"

    def test_sort_orders_priority_and_direction(self):
        cube_info = [
            {
                "dimensions": [
                    {"dimensionName": "address", "sortOrder": 0},
                    {"dimensionName": "location", "sortOrder": 1},
                ],
                "measures": [
                    {"measureName": "metric_1", "sortOrder": 2},
                ],
            }
        ]
        orders = sort_orders_from_cube_info(cube_info)
        assert [o["name"] for o in orders] == ["address", "location", "metric_1"]
        assert [o["direction"] for o in orders] == ["ASC", "DESC", "ASC"]

    def test_sort_key_ascending_descending_strings(self):
        """Cube payloads may use ``sort: Ascending|Descending`` instead of sortOrder."""
        cube_info = [
            {
                "dimensions": [
                    {
                        "dimensionName": "booking_platform",
                        "columnName": "travel_details.booking_platform",
                        "sort": "Ascending",
                    }
                ],
                "measures": [
                    {
                        "measureName": "travel_cost",
                        "formatString": "0.00",
                        "sort": "Descending",
                    }
                ],
            }
        ]
        orders = sort_orders_from_cube_info(cube_info)
        by_name = {entry["name"]: entry for entry in orders}
        assert by_name["booking_platform"]["direction"] == "ASC"
        assert by_name["travel_cost"]["direction"] == "DESC"

        mapping = format_strings_from_cube_info(cube_info)
        assert mapping["travel_cost"] == "0.00"

        prompt_text = format_sort_orders_for_prompt(orders)
        assert "booking_platform: ASC" in prompt_text
        assert "travel_cost: DESC" in prompt_text
        assert "0.00" in format_format_strings_for_prompt(mapping)
