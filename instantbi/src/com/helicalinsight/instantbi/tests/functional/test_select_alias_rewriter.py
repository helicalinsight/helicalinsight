"""Tests for SELECT-clause business-friendly alias rewriting."""

import pytest

from helicalbi.sql.SelectAliasRewriter import (
    is_technical_alias,
    resolve_select_alias,
    rewrite_select_aliases,
    to_business_friendly_alias,
)


pytestmark = pytest.mark.functional


def test_detects_technical_alias_patterns():
    assert is_technical_alias("travel_cost")
    assert is_technical_alias("meetCancellationStatus")
    assert is_technical_alias("TRAVELCOST")
    assert is_technical_alias("a,b")
    assert not is_technical_alias("Travel Cost")
    assert not is_technical_alias("Destination Count")
    assert not is_technical_alias("platform")


def test_title_cases_underscore_names():
    assert to_business_friendly_alias("travel_cost") == "Travel Cost"
    assert to_business_friendly_alias("meet_cancellation_status") == "Meet Cancellation Status"
    assert to_business_friendly_alias("meetCancellationStatus") == "Meet Cancellation Status"


def test_resolve_prefers_cube_business_alias():
    alias_map = {"travel_cost": "Travel Cost", "Travel Cost": "Travel Cost"}
    assert (
        resolve_select_alias("travel_cost", physical="travel_cost", alias_map=alias_map)
        == "Travel Cost"
    )


def test_resolve_humanizes_when_no_cube_alias():
    assert resolve_select_alias("booking_platform") == "Booking Platform"


def test_resolve_keeps_friendly_alias():
    assert resolve_select_alias("Travel Cost") is None
    assert resolve_select_alias("platform") is None


def test_rewrite_select_aliases_uses_cube_alias():
    cube_metadata = [
        {
            "database_table": "travel_details",
            "columns": [
                {
                    "column_name": "booking_platform",
                    "alias_name": "Booking Platform",
                }
            ],
            "measures": [
                {
                    "column_name": "travel_cost",
                    "alias_name": "Travel Cost",
                    "measure_name": "Travel Cost",
                }
            ],
        }
    ]
    sql = (
        'SELECT travel_details.booking_platform AS booking_platform, '
        'SUM(travel_details.travel_cost) AS travel_cost '
        'FROM travel_details GROUP BY travel_details.booking_platform '
        'ORDER BY travel_cost DESC LIMIT 10'
    )

    result = rewrite_select_aliases(sql, cube_metadata, dialect="postgres")

    assert 'AS "Booking Platform"' in result or 'AS "BOOKING PLATFORM"' in result.upper()
    # sqlglot may emit mixed quoting; normalize for assertion
    assert "Booking Platform" in result
    assert "Travel Cost" in result
    assert "AS booking_platform" not in result
    assert "AS travel_cost" not in result


def test_rewrite_humanizes_bare_physical_select_without_as():
    sql = "SELECT travel_details.travel_cost FROM travel_details LIMIT 5"
    result = rewrite_select_aliases(sql, dialect="postgres")
    assert "Travel Cost" in result


def test_rewrite_returns_original_when_aliases_already_friendly():
    sql = (
        'SELECT travel_details.travel_cost AS "Travel Cost" '
        "FROM travel_details LIMIT 5"
    )
    result = rewrite_select_aliases(sql, dialect="postgres")
    assert result == sql
    assert "Travel Cost" in result
