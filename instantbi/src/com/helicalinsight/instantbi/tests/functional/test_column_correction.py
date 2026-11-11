"""Column names in generated SQL are checked against the cube."""

import logging

import pytest

from helicalbi.autocorrection import (
    apply_sql_correction,
    columns_for_join_recheck,
    format_column_corrections,
    join_api_columns,
    join_needs_rewrite,
    replace_missing_columns,
    restore_join_aliases,
    sql_column_mismatches,
)
from helicalbi.common import app_config

pytestmark = pytest.mark.functional

_CUBES = [
    {
        "database_table": "meeting_details",
        "columns": [
            {"column_name": "client_name"},
            {"column_name": "meeting_id"},
        ],
    },
    {
        "database_table": "travel_details",
        "columns": [
            {"column_name": "travel_id"},
            {"column_name": "travel_cost"},
            {"column_name": "travel_date"},
        ],
    },
]


def test_wrong_table_reports_the_table_that_stores_the_column():
    sql = (
        "SELECT travel_details.client_name, COUNT(travel_details.travel_id) "
        "FROM travel_details "
        "WHERE EXTRACT(YEAR FROM travel_details.travel_date) = 2026 "
        "GROUP BY travel_details.client_name"
    )
    mismatches = sql_column_mismatches(sql, _CUBES, "PostgreSQL")
    assert len(mismatches) == 1
    assert mismatches[0]["reference"] == "travel_details.client_name"
    assert mismatches[0]["proper"] == ["meeting_details.client_name"]
    note = format_column_corrections(mismatches)
    assert "Use meeting_details.client_name" in note
    assert "travel_details.travel_id" not in note.split("Use", 1)[0]


def test_alias_is_resolved_before_the_column_is_checked():
    sql = (
        'SELECT td.meeting_id FROM travel_details td'
    )
    mismatches = sql_column_mismatches(sql, _CUBES, "PostgreSQL")
    assert mismatches[0]["proper"] == ["meeting_details.meeting_id"]
    assert "travel_id" in mismatches[0]["similar"]


def test_join_recheck_uses_the_table_that_stores_the_column():
    sql = (
        "SELECT travel_details.client_name, COUNT(travel_details.travel_id) "
        "FROM travel_details GROUP BY travel_details.client_name"
    )
    assert columns_for_join_recheck(sql, _CUBES, "PostgreSQL") == [
        "meeting_details.client_name",
        "travel_details.travel_id",
    ]


def test_invented_join_key_needs_the_api_join():
    sql = (
        "SELECT meeting_details.client_name FROM meeting_details "
        "JOIN travel_details ON travel_details.travel_id = meeting_details.meeting_id"
    )
    api_join = (
        "JOIN travel_details ON travel_details.travelled_by = meeting_details.meeting_by"
    )
    assert join_needs_rewrite(sql, api_join, "PostgreSQL") is True
    matching = (
        "SELECT meeting_details.client_name FROM meeting_details "
        "JOIN travel_details ON travel_details.travelled_by = meeting_details.meeting_by"
    )
    assert join_needs_rewrite(matching, api_join, "PostgreSQL") is False


def test_correction_keeps_the_join_alias_on_the_intermediate_table():
    sql = (
        'SELECT "td"."booking_platform" AS "Booking Platform", '
        '"md"."meet_cancellation_status" AS "Cancellation Status", '
        'COUNT(DISTINCT "td"."booking_id") AS "Total Tours" '
        'FROM "travel_details" AS "td" '
        'INNER JOIN "employee_details" '
        'ON ("td"."travelled_by" = "ed"."employee_id") '
        'INNER JOIN "meeting_details" AS "md" '
        'ON ("ed"."employee_id" = "md"."meeting_by") '
        'GROUP BY "td"."booking_platform", "md"."meet_cancellation_status"'
    )
    api_join = (
        'INNER JOIN "employee_details" AS "ed" '
        'ON "td"."travelled_by" = "ed"."employee_id" '
        'INNER JOIN "meeting_details" AS "md" '
        'ON "ed"."employee_id" = "md"."meeting_by"'
    )
    fixed = restore_join_aliases(sql, api_join, "PostgreSQL")
    assert 'employee_details" AS "ed"' in fixed
    assert '"td"."travelled_by" = "ed"."employee_id"' in fixed


def test_alias_is_not_added_when_the_table_name_is_the_qualifier():
    sql = (
        "SELECT td.booking_platform FROM travel_details AS td "
        "JOIN employee_details ON td.travelled_by = employee_details.employee_id"
    )
    api_join = "JOIN employee_details AS ed ON td.travelled_by = ed.employee_id"
    assert " AS ed" not in restore_join_aliases(sql, api_join, "PostgreSQL")


def test_matching_columns_are_left_alone():
    sql = (
        "SELECT meeting_details.client_name, SUM(travel_details.travel_cost) "
        "FROM meeting_details "
        "JOIN travel_details ON meeting_details.meeting_id = travel_details.travel_id "
        "GROUP BY meeting_details.client_name"
    )
    assert sql_column_mismatches(sql, _CUBES, "PostgreSQL") == []


_API_JOIN = (
    'INNER JOIN "sampletraveldata"."public"."employee_details" '
    'ON ("employee_details"."employee_id" = "travel_details"."travelled_by") '
    'INNER JOIN "sampletraveldata"."public"."meeting_details" '
    'ON ("employee_details"."employee_id" = "meeting_details"."meeting_by")'
)

_TOUR_CUBES = [
    {
        "database_table": "travel_details",
        "columns": [
            {"column_name": "booking_platform"},
            {"column_name": "travel_id"},
            {"column_name": "travel_date"},
            {"column_name": "travel_medium"},
        ],
    },
    {
        "database_table": "meeting_details",
        "columns": [
            {"column_name": "meet_cancellation_status"},
            {"column_name": "meeting_id"},
            {"column_name": "meeting_impact"},
        ],
    },
]


def test_missing_booking_id_is_replaced_and_join_keys_are_kept():
    sql = (
        'SELECT travel_details.booking_platform AS "Booking Platform", '
        'meeting_details.meet_cancellation_status AS "Cancellation Status", '
        'COUNT(DISTINCT travel_details.booking_id) AS "Total Tours" '
        "FROM travel_details "
        "INNER JOIN employee_details "
        "ON (travel_details.travelled_by = employee_details.employee_id) "
        "INNER JOIN meeting_details "
        "ON (employee_details.employee_id = meeting_details.meeting_by) "
        "GROUP BY travel_details.booking_platform, meeting_details.meet_cancellation_status"
    )
    known = join_api_columns(_API_JOIN, "PostgreSQL")
    assert ("travel_details", "travelled_by") in known
    assert ("meeting_details", "meeting_by") in known
    mismatches = sql_column_mismatches(sql, _TOUR_CUBES, "PostgreSQL", known)
    assert [item["reference"] for item in mismatches] == ["travel_details.booking_id"]
    fixed, notes = replace_missing_columns(sql, mismatches, "PostgreSQL")
    assert notes == ["travel_details.booking_id -> travel_details.travel_id"]
    assert "booking_id" not in fixed
    assert "travel_id" in fixed
    assert "booking_platform" in fixed
    assert "travelled_by" in fixed
    assert sql_column_mismatches(fixed, _TOUR_CUBES, "PostgreSQL", known) == []


def test_autocorrect_stays_off_and_leaves_sql_unchanged(caplog):
    assert app_config.sql_autocorrect is False
    sql = "SELECT travel_details.client_name FROM travel_details"
    with caplog.at_level(logging.INFO, logger="helicalbi.autocorrection.apply"):
        corrected, response = apply_sql_correction(
            sql,
            object(),
            state={"cube_metadata": _CUBES},
            question="client name",
            dialect="PostgreSQL",
            parser=object(),
        )
    assert corrected == sql
    assert response is not None
    assert "SQL autocorrect off" in caplog.text
