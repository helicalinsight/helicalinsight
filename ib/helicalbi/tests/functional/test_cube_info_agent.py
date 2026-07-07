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
            "columns": {
                "booking_platform": {"id": "1074"},
                "destination_id": {"id": "1070"},
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
        assert table["columns"][0]["column_name"] == "booking_platform"
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
                "column_name": "destination_id",
                "measure_name": "Destination Count",
                "aggregator": "count",
            }
        ]

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

    def test_merge_business_metrics_prefers_first_source(self):
        first = [{"metric": "a", "tables": ["t1"], "description": "first"}]
        second = [{"metric": "a", "tables": ["t1"], "description": "second"}]
        assert merge_business_metrics(first, second) == first
