"""Functional tests for cube_info agent detection and conversion."""

import pytest

from helicalbi.common.CubeInfoAgent import (
    business_metrics_from_cube_info,
    cube_info_to_cube_metadata,
    extract_domain_topics,
    is_cube_info_agent,
    merge_business_metrics,
    prepare_cube_info_agent_data,
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


class TestIsCubeInfoAgent:
    def test_true_for_cube_info_structure(self):
        assert is_cube_info_agent(_CUBE_INFO_AGENT) is True

    def test_false_for_legacy_cube_metadata(self):
        assert is_cube_info_agent({"cube_metadata": [{"database_table": "t"}]}) is False

    def test_false_for_empty_payload(self):
        assert is_cube_info_agent({}) is False


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

    def test_prepare_cube_info_agent_data(self):
        prepared = prepare_cube_info_agent_data(_CUBE_INFO_AGENT, _METADATA_RESPONSE)
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
