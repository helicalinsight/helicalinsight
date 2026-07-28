"""Functional tests for cube_info model detection and conversion."""

import pytest

from helicalbi.common.CubeInfoModel import (
    ai_instructions_from_cube_info,
    business_metrics_from_cube_info,
    cube_info_to_cube_metadata,
    extract_domain_topics,
    format_ai_instructions_for_prompt,
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
        assert address["hierarchyName"] == "address"
        assert address["levelName"] == "address"
        assert address["metric"]["formula"] == "this is formula"
        assert address["aiContext"] == {
            "instructions": "inst",
            "synonyms": "synonyms",
            "examples": "examples",
        }
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
        assert address_col.get("hierarchy_name") == "address"
        assert address_col.get("level_name") == "address"
        assert address_col.get("dimension_name") == "address"
        assert address_col.get("formula") == "this is formula"
        assert address_col.get("ai_context") == {
            "instructions": "inst",
            "synonyms": "synonyms",
            "examples": "examples",
        }

        topic_map = {
            entry["topic_name"]: entry["component"]
            for entry in prepared["topic_mappings"]
        }
        assert "address" in topic_map["topic"]
        assert "employee_name" in topic_map["topic"]
        assert " alias:-address" in topic_map["topic"]
        assert " alias:-employee_name" in topic_map["topic"]
        assert "metric_1" in topic_map["topic 2"] or " alias:-metric_1" in topic_map["topic 2"]

        rich_by_topic = {
            entry["topic_name"]: entry.get("components") or []
            for entry in prepared["topic_mappings"]
        }
        address_component = next(
            item for item in rich_by_topic["topic"] if item.get("name") == "address"
        )
        assert address_component["id"] == "2857"
        assert address_component["kind"] == "hierarchy"
        assert address_component.get("hierarchyName") == "address"
        metric_component = next(
            item for item in rich_by_topic["topic 2"] if item.get("name") == "metric_1"
        )
        assert metric_component["id"] == "eb541c54-f2fc-44be-b92f-5a08895a1c6d"
        assert metric_component["kind"] == "computed_measure"
        assert metric_component.get("formula") == "f3"

        synonym_dims = {
            entry["dimension_name"]: entry["synonyms"]
            for entry in prepared["synonyms"]
        }
        assert synonym_dims["address"] == ["synonyms"]
        assert synonym_dims["location"] == ["s4"]

        assert "topic" in prepared["domain_context"]
        assert "components: address" in prepared["domain_context"]
        assert "employee_name" in prepared["domain_context"]
        assert "id=2857" in prepared["domain_context"] or "id: 2857" in prepared["domain_context"]

        assert prepared["format_strings"]["metric_1"] == "0.00"
        assert "0.00" in prepared["column_format_strings"]

        assert prepared["ai_instructions"]["address"] == "inst"
        assert prepared["ai_instructions"]["location"] == "i4"
        assert prepared["ai_instructions"]["metric_1"] == "j3"
        assert "inst" in prepared["column_ai_instructions"]
        assert "j3" in prepared["column_ai_instructions"]

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
        assert sort_direction_from_value("none") is None
        assert sort_direction_from_value("None") is None
        assert sort_direction_from_value("") is None
        assert sort_direction_from_value(None) is None

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

    def test_none_sort_excluded_from_sort_list(self):
        from helicalbi.common.CubeInfoModel import (
            filter_domain_context_for_sql,
            filter_sort_orders_for_picked,
            format_sort_orders_for_prompt,
            sort_orders_from_cube_info,
        )

        cube_info = [
            {
                "dimensions": [
                    {
                        "dimensionName": "booking_platform",
                        "columnName": "travel_details.booking_platform",
                        "sort": "Ascending",
                    },
                    {
                        "dimensionName": "travel_type",
                        "columnName": "travel_details.travel_type",
                        "sort": "none",
                    },
                    {
                        "dimensionName": "source",
                        "columnName": "travel_details.source",
                        "sort": "",
                    },
                ],
                "measures": [
                    {
                        "measureName": "travel_cost",
                        "sort": "Descending",
                        "formatString": "$#,##0.00",
                    }
                ],
            }
        ]
        orders = sort_orders_from_cube_info(cube_info)
        names = [entry["name"] for entry in orders]
        assert "booking_platform" in names
        assert "travel_cost" in names
        assert "travel_type" not in names
        assert "source" not in names

        filtered = filter_sort_orders_for_picked(
            orders, ["booking_platform", "travel_type", "travel_cost"]
        )
        filtered_names = [entry["name"] for entry in filtered]
        assert filtered_names == ["booking_platform", "travel_cost"]
        prompt = format_sort_orders_for_prompt(filtered)
        assert "booking_platform: ASC" in prompt
        assert "travel_cost: DESC" in prompt
        assert "travel_type" not in prompt

        domain_ctx = filter_domain_context_for_sql(
            domain=["Sales Order"],
            topics=["Sales"],
            topic_mappings=[
                {
                    "topic_name": "Client Meeting",
                    "domain_name": "Sales Order",
                    "components": [{"id": "1045", "name": "client_name", "kind": "dimension"}],
                },
                {
                    "topic_name": "Sales",
                    "domain_name": "Sales Order",
                    "components": [
                        {"id": "1072", "name": "travel_cost", "kind": "measure"}
                    ],
                },
            ],
            domain_context="Domain: Sales Order; topics: Client Meeting, Sales",
        )
        assert "Sales Order" in domain_ctx
        assert "Topic: Sales" in domain_ctx
        assert "travel_cost" in domain_ctx
        assert "Client Meeting" not in domain_ctx or "Topic: Client Meeting" not in domain_ctx
        assert "Topic: Client Meeting" not in domain_ctx

    def test_ai_instructions_map_and_prompt(self):
        cube_info = [
            {
                "dimensions": [
                    {
                        "dimensionName": "booking_platform",
                        "columnName": "travel_details.booking_platform",
                        "aiContext": {
                            "instructions": "group platforms",
                            "synonyms": "",
                            "examples": "",
                        },
                    }
                ],
                "measures": [
                    {
                        "measureName": "travel_cost",
                        "aiContext": {
                            "instructions": "show as currency",
                            "synonyms": "",
                            "examples": "",
                        },
                    }
                ],
            }
        ]
        mapping = ai_instructions_from_cube_info(cube_info)
        assert mapping["booking_platform"] == "group platforms"
        assert mapping["travel_details.booking_platform"] == "group platforms"
        assert mapping["travel_cost"] == "show as currency"
        prompt_text = format_ai_instructions_for_prompt(mapping)
        assert "group platforms" in prompt_text
        assert "show as currency" in prompt_text
        assert "booking_platform:" in prompt_text
        assert "travel_cost:" in prompt_text


class TestSelectionFieldPropagation:
    """Fields required during table/column selection must reach cube_metadata."""

    def test_dimension_hierarchy_and_blank_measure_fields(self):
        from helicalbi.common.JsonToPara import generate_semantic_hint

        metadata_response = {
            "tables": {
                "travel_details": {
                    "id": "112",
                    "alias": "travel",
                    "columns": {
                        "booking_platform": {"id": "1074"},
                        "travel_date": {"id": "1065"},
                        "travel_cost": {"id": "1072"},
                    },
                },
                "meeting_details": {
                    "id": "109",
                    "alias": "meetings",
                    "columns": {
                        "meet_cancellation_status": {"id": "1048"},
                    },
                },
            }
        }
        cube_info = [
            {
                "cubeName": "Sales_Travel_And_Meetings",
                "dimensions": [
                    {
                        "dimensionName": "Booking Platform",
                        "semanticType": "Text",
                        "tableId": "112",
                        "columnName": "travel_details.booking_platform",
                        "columnId": "1074",
                        "aiContext": {
                            "instructions": "Platform used to book travel.",
                            "synonyms": "booking site, portal",
                            "examples": "Expedia, MakeMyTrip",
                        },
                    },
                    {
                        "dimensionName": "travel_date",
                        "semanticType": "Date",
                        "tableId": "112",
                        "columnName": "travel_details.travel_date",
                        "columnId": "dd6b8758-8940-474f-a901-5e9bb93be372",
                        "aiContext": {
                            "instructions": "Date of travel.",
                            "synonyms": "travel date",
                            "examples": "2024-01-15",
                        },
                        "hierarchies": [
                            {
                                "hierarchyName": "travel_date",
                                "primaryColumnId": "dd6b8758-8940-474f-a901-5e9bb93be372",
                                "tableId": "112",
                                "columnName": "travel_details.travel_date",
                                "levels": [
                                    {
                                        "levelName": "YEAR",
                                        "semanticType": "Text",
                                        "tableId": "112",
                                        "columnName": "travel_details.travel_date",
                                        "columnId": "1065",
                                        "metric": {
                                            "formula": "EXTRACT(YEAR from travel_details.travel_date)"
                                        },
                                        "aiContext": {
                                            "instructions": "Year of travel.",
                                            "synonyms": "year, travel year",
                                            "examples": "2023, 2024",
                                        },
                                    }
                                ],
                            }
                        ],
                    },
                ],
                "measures": [
                    {
                        "measureName": "travel_cost",
                        "aggregator": "Sum",
                        "columnId": "1072",
                        "tableId": "112",
                        "semanticType": "Currency",
                        "columnName": "travel_details.travel_cost",
                        "aiContext": {
                            "instructions": "Total travel expense.",
                            "synonyms": "trip cost",
                            "examples": "150.00",
                        },
                    },
                    {
                        "measureName": "Cost by Cancellation",
                        "aggregator": "None",
                        "metricId": "00b133c4-1d5a-4d6f-b8eb-b5639ede7f9d",
                        "tableId": "",
                        "semanticType": "Number",
                        "columnName": "",
                        "metric": {
                            "formula": (
                                "sum(travel_details.travel_cost) "
                                "filter meeting_details.meet_cancellation_status=Yes"
                            )
                        },
                        "aiContext": {
                            "instructions": "Cancelled meetings use Yes or No.",
                            "synonyms": "cancelled, loss",
                            "examples": "",
                        },
                    },
                ],
            }
        ]

        result = cube_info_to_cube_metadata(cube_info, metadata_response)
        columns = {
            col["alias_name"]: col
            for table in result
            for col in table.get("columns") or []
        }
        measures = {
            m["alias_name"]: m
            for table in result
            for m in table.get("measures") or []
        }

        booking = columns["Booking Platform"]
        assert booking["dimension_name"] == "Booking Platform"
        assert booking["semantic_type"] == "Text"
        assert booking["column_name"] == "booking_platform"
        assert booking["ai_context"] == {
            "instructions": "Platform used to book travel.",
            "synonyms": "booking site, portal",
            "examples": "Expedia, MakeMyTrip",
        }

        year = columns["YEAR"]
        assert year["hierarchy_name"] == "travel_date"
        assert year["level_name"] == "YEAR"
        assert year["column_name"] == "travel_date"
        assert year["formula"] == "EXTRACT(YEAR from travel_details.travel_date)"
        assert year["ai_context"]["instructions"] == "Year of travel."

        travel_cost = measures["travel_cost"]
        assert travel_cost["measure_name"] == "travel_cost"
        assert travel_cost["column_name"] == "travel_cost"
        assert travel_cost["ai_context"]["synonyms"] == "trip cost"

        cancelled = measures["Cost by Cancellation"]
        assert cancelled["is_computed"] is True
        assert cancelled["measure_name"] == "Cost by Cancellation"
        assert cancelled["formula"].startswith("sum(travel_details.travel_cost)")
        assert cancelled["semantic_type"] == "Number"
        assert cancelled["ai_context"] == {
            "instructions": "Cancelled meetings use Yes or No.",
            "synonyms": "cancelled, loss",
            "examples": "",
        }

        hint = generate_semantic_hint(result)
        assert "dimension: Booking Platform" in hint
        assert "aiContext:" in hint
        assert "Platform used to book travel." in hint
        assert "hierarchy: travel_date" in hint
        assert "level: YEAR" in hint
        assert "EXTRACT(YEAR from travel_details.travel_date)" in hint
        assert "measure: Cost by Cancellation" in hint
        assert "Cancelled meetings use Yes or No." in hint
        assert "type: Number" in hint


class TestVizColumnContext:
    def test_builds_context_for_execute_query_columns(self):
        from helicalbi.common.CubeInfoModel import (
            build_viz_column_context,
            extract_result_field_names,
        )

        metadata = [
            {"1": {"name": "YEAR", "type": "text"}},
            {"2": {"name": "travel_cost", "type": "numeric"}},
            {"rows": 2},
        ]
        assert extract_result_field_names(metadata) == ["YEAR", "travel_cost"]

        cube_metadata = [
            {
                "database_table": "travel_details",
                "columns": [
                    {
                        "column_name": "travel_date",
                        "alias_name": "YEAR",
                        "dimension_name": "YEAR",
                        "hierarchy_name": "travel_date",
                        "level_name": "YEAR",
                        "semantic_type": "Text",
                        "sort_direction": "ASC",
                        "sort_order": "Ascending",
                        "ai_context": {
                            "instructions": "Year of travel.",
                            "synonyms": "year",
                            "examples": "2024",
                        },
                    }
                ],
                "measures": [
                    {
                        "column_name": "travel_cost",
                        "alias_name": "travel_cost",
                        "measure_name": "travel_cost",
                        "semantic_type": "Currency",
                        "format_string": "$#,##0.00",
                        "ai_context": {
                            "instructions": "Show as currency.",
                            "synonyms": "trip cost",
                            "examples": "150.00",
                        },
                    }
                ],
            }
        ]
        context = build_viz_column_context(
            metadata,
            cube_metadata=cube_metadata,
            format_strings={"travel_cost": "$#,##0.00", "unused": "0"},
            ai_instructions={"YEAR": "Year of travel."},
            sort_orders=[
                {"name": "YEAR", "direction": "ASC", "raw": "Ascending", "priority": 0},
                {"name": "unused", "direction": "DESC", "raw": "Descending", "priority": 1},
            ],
            domain_context="Domain: Sales Order; topics: Sales",
        )
        assert context["field_names"] == ["YEAR", "travel_cost"]
        assert "unused" not in context["format_strings"]
        assert context["format_strings"]["travel_cost"] == "$#,##0.00"
        assert context["ai_instructions"]["YEAR"] == "Year of travel."
        assert [entry["name"] for entry in context["sort_orders"]] == ["YEAR"]
        by_name = {entry["name"]: entry for entry in context["columns"]}
        assert by_name["YEAR"]["semantic_type"] == "Text"
        assert by_name["YEAR"]["sort_direction"] == "ASC"
        assert by_name["travel_cost"]["format_string"] == "$#,##0.00"
        assert "Year of travel." in context["column_context"]
        assert "formatString: $#,##0.00" in context["column_context"]
        assert "semanticType: Text" in context["column_context"]
        assert context["domain_context"].startswith("Domain: Sales Order")


class TestDomainTopicIdMapping:
    def test_topic_components_resolve_by_id_across_kinds(self):
        from helicalbi.common.CubeInfoModel import (
            format_topic_mappings_for_prompt,
            topic_mappings_from_domain,
        )
        from helicalbi.sql.GetContextForSQL import get_table_col_description

        model_data = {
            "domain": [
                {
                    "domain_name": "Sales Order",
                    "description": "Sales meetings and travel.",
                    "topics": [
                        {
                            "topic": "Client Meeting",
                            "description": "Client meetings and cancellation.",
                            "components": [
                                {"id": "1045", "name": "client_name"},
                                {"id": "1044", "name": "meeting_by"},
                            ],
                        },
                        {
                            "topic": "Sales",
                            "description": "Travel cost and cancellation cost.",
                            "components": [
                                {"id": "1072", "name": "travel_cost"},
                                {
                                    "id": "00b133c4-1d5a-4d6f-b8eb-b5639ede7f9d",
                                    "name": "Cost by Cancellation",
                                },
                                {"id": "1065", "name": "YEAR"},
                            ],
                        },
                    ],
                }
            ],
            "cube": [
                {
                    "dimensions": [
                        {
                            "dimensionName": "client_name",
                            "semanticType": "Text",
                            "columnName": "meeting_details.client_name",
                            "columnId": "1045",
                            "tableId": "109",
                        },
                        {
                            "dimensionName": "travel_date",
                            "semanticType": "Date",
                            "columnName": "travel_details.travel_date",
                            "columnId": "dd6b8758-8940-474f-a901-5e9bb93be372",
                            "tableId": "112",
                            "hierarchies": [
                                {
                                    "hierarchyName": "travel_date",
                                    "columnName": "travel_details.travel_date",
                                    "levels": [
                                        {
                                            "levelName": "YEAR",
                                            "columnName": "travel_details.travel_date",
                                            "columnId": "1065",
                                            "tableId": "112",
                                            "metric": {
                                                "formula": (
                                                    "EXTRACT(YEAR from "
                                                    "travel_details.travel_date)"
                                                )
                                            },
                                        }
                                    ],
                                }
                            ],
                        },
                    ],
                    "measures": [
                        {
                            "measureName": "meeting_by",
                            "columnId": "1044",
                            "tableId": "109",
                            "columnName": "meeting_details.meeting_by",
                        },
                        {
                            "measureName": "travel_cost",
                            "columnId": "1072",
                            "tableId": "112",
                            "columnName": "travel_details.travel_cost",
                        },
                        {
                            "measureName": "Cost by Cancellation",
                            "metricId": "00b133c4-1d5a-4d6f-b8eb-b5639ede7f9d",
                            "columnName": "",
                            "semanticType": "Number",
                            "metric": {
                                "formula": (
                                    "sum(travel_details.travel_cost) "
                                    "filter meeting_details.meet_cancellation_status=Yes"
                                )
                            },
                        },
                    ],
                }
            ],
        }

        mappings = topic_mappings_from_domain(model_data)
        by_topic = {entry["topic_name"]: entry for entry in mappings}

        client = by_topic["Client Meeting"]
        assert client["domain_name"] == "Sales Order"
        kinds = {item["name"]: item["kind"] for item in client["components"]}
        assert kinds["client_name"] == "dimension"
        assert kinds["meeting_by"] == "measure"
        assert any(item["id"] == "1045" for item in client["components"])

        sales = by_topic["Sales"]
        sales_kinds = {item["name"]: item for item in sales["components"]}
        assert sales_kinds["travel_cost"]["kind"] == "measure"
        assert sales_kinds["Cost by Cancellation"]["kind"] == "computed_measure"
        assert sales_kinds["YEAR"]["kind"] == "hierarchy"
        assert "EXTRACT(YEAR" in sales_kinds["YEAR"]["formula"]

        prompt = format_topic_mappings_for_prompt(mappings)
        assert "Topic: Client Meeting" in prompt
        assert "id: 1045" in prompt
        assert "kind: computed_measure" in prompt
        assert "kind: hierarchy" in prompt

        description = get_table_col_description(
            [
                {
                    "database_table": "travel_details",
                    "columns": [{"column_name": "travel_cost"}],
                }
            ],
            table_names=["travel_details"],
            model_data={
                "topic_mappings": mappings,
                "domain_context": "Domain: Sales Order",
            },
        )
        assert "Domain: Sales Order" in description
        assert "Topic: Sales" in description
        assert "Cost by Cancellation" in description
        assert "travel_cost" in description
