"""Reject SQL that copies prompt sample names instead of the provided tables."""

import pytest

from helicalbi.core.sqlflow.FinalSqlGen import _columns_from_query_plan
from helicalbi.sql_agent.nodes.validator import sql_disallowed_names

pytestmark = pytest.mark.functional

_COPIED_SAMPLE = """
SELECT
  "t"."destination",
  SUM("t"."travel_cost") AS "Total Travel Cost"
FROM "t"
WHERE
  EXTRACT(YEAR FROM "t"."date_col") IN (
    EXTRACT(YEAR FROM CURRENT_DATE),
    EXTRACT(YEAR FROM CURRENT_DATE) - 1
  )
GROUP BY
  "t"."destination"
LIMIT 100
"""


def test_copied_sample_sql_is_rejected_for_real_table():
    problem = sql_disallowed_names(
        _COPIED_SAMPLE,
        {"travel_details"},
        {"destination", "travel_cost", "travel_date"},
        "PostgreSQL",
    )
    assert problem is not None
    assert "t" in problem
    assert "travel_details" in problem


def test_real_table_and_date_column_are_accepted():
    sql = (
        'SELECT "travel_details"."destination", '
        'SUM("travel_details"."travel_cost") AS "Total Travel Cost" '
        'FROM "travel_details" '
        'WHERE "travel_details"."travel_date" >= CURRENT_DATE - INTERVAL \'1 year\' '
        'GROUP BY "travel_details"."destination"'
    )
    assert (
        sql_disallowed_names(
            sql,
            {"travel_details"},
            {"destination", "travel_cost", "travel_date"},
            "PostgreSQL",
        )
        is None
    )


def test_alias_t_on_a_real_table_is_not_a_sample_table():
    sql = (
        'SELECT t."destination" FROM "travel_details" t '
        'WHERE t."travel_date" >= CURRENT_DATE'
    )
    assert (
        sql_disallowed_names(
            sql,
            {"travel_details"},
            {"destination", "travel_date"},
            "PostgreSQL",
        )
        is None
    )


def test_example_slot_left_in_sql_is_rejected():
    problem = sql_disallowed_names(
        "SELECT <date_column> FROM travel_details",
        {"travel_details"},
        {"travel_date"},
        "PostgreSQL",
    )
    assert problem is not None
    assert "slot" in problem


def test_date_col_is_allowed_when_the_plan_names_it():
    sql = 'SELECT "t"."date_col" FROM "t"'
    assert (
        sql_disallowed_names(sql, {"t"}, {"date_col"}, "PostgreSQL") is None
    )


def test_columns_from_query_plan_drop_the_table_qualifier():
    columns = _columns_from_query_plan(
        {
            "columnName": [
                "travel_details.destination",
                "travel_details.travel_date",
            ]
        }
    )
    assert columns == {"destination", "travel_date"}
