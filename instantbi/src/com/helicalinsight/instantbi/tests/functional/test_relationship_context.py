"""Join text and neighbor-table expansion for column and table prompts."""
import pytest

from helicalbi.core.sqlflow.util.RelationshipContext import (
    column_schema_tables,
    format_joins,
    joins_section,
)

pytestmark = pytest.mark.functional

_JOINS = [
    {
        "left": {"table": "meeting_details", "column": "meeting_by"},
        "right": {"table": "employee_details", "column": "employee_id"},
    },
    {
        "left": {"table": "employee_details", "column": "employee_id"},
        "right": {"table": "travel_details", "column": "travelled_by"},
    },
    {
        "left": {"table": "travel_details", "column": "source_id"},
        "right": {"table": "geo_cordinates", "column": "location_id"},
    },
]


def test_two_hops_from_meetings_reaches_travel_not_geo():
    tables = column_schema_tables(["meeting_details"], _JOINS, hops=2)
    assert tables == ["meeting_details", "employee_details", "travel_details"]


def test_one_hop_stops_at_the_employee_bridge():
    tables = column_schema_tables(["meeting_details"], _JOINS, hops=1)
    assert tables == ["meeting_details", "employee_details"]


def test_format_joins_is_one_equation_per_link():
    text = format_joins(_JOINS, ["meeting_details", "employee_details", "travel_details"])
    assert text == (
        "meeting_details.meeting_by = employee_details.employee_id\n"
        "employee_details.employee_id = travel_details.travelled_by"
    )


def test_format_joins_without_relationships_is_empty():
    assert format_joins([]) == ""
    assert joins_section([]) == ""
    assert column_schema_tables(["meeting_details"], []) == ["meeting_details"]
