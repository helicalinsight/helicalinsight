"""A named component adds its table even when the model returns one table."""

import pytest

from helicalbi.core.sqlflow.FindTablesFromTopics import tables_named_in_question

pytestmark = pytest.mark.functional

QUESTION = "Show me the client name wise number of travels and cost for the year 2026"


def test_client_name_selects_meeting_details_and_year_selects_travel():
    topic_mappings = [
        {
            "topic_name": "Meetings",
            "components": [
                {
                    "name": "Client Name",
                    "columnName": "meeting_details.client_name",
                    "aiContext": {"synonyms": "customer, client, customer name"},
                }
            ],
        },
        {
            "topic_name": "Travel",
            "components": [
                {
                    "name": "Year",
                    "columnName": "travel_details.travel_date",
                },
                {
                    "name": "Cost Incurred",
                    "columnName": "travel_details.travel_cost",
                },
            ],
        },
    ]
    cubes = [
        {
            "database_table": "meeting_details",
            "columns": [
                {"column_name": "client_name", "alias_name": "Client Name"},
            ],
        },
        {
            "database_table": "travel_details",
            "columns": [
                {"column_name": "travel_date", "dimension_name": "Year"},
                {"column_name": "travel_cost", "alias_name": "Cost Incurred"},
            ],
        },
    ]

    tables = tables_named_in_question(QUESTION, topic_mappings, cubes)

    assert "meeting_details" in tables
    assert "travel_details" in tables


def test_unmentioned_table_stays_out():
    cubes = [
        {
            "database_table": "meeting_details",
            "columns": [
                {"column_name": "cancellation_reason", "alias_name": "cancellation reason"},
            ],
        }
    ]

    assert tables_named_in_question(QUESTION, [], cubes) == []
