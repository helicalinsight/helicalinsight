"""Exclusion filters prefer NOT IN when the dialect supports it."""

import pytest

from helicalbi.sql.exclusion_predicate import (
    dialect_supports_not_in,
    prefer_not_in,
    question_requests_exclusion,
)
from helicalbi.sql_agent.prompt.sql_prompts import THINK_FLAT_SQL_GENERATION

pytestmark = pytest.mark.functional

_YEAR_SQL = """
SELECT
EXTRACT(YEAR FROM "travel_details"."travel_date") AS "Year",
SUM("travel_details"."travel_cost") AS "Travel Cost"
FROM "travel_details"
WHERE
EXTRACT(YEAR FROM "travel_details"."travel_date") <> 2015
GROUP BY
EXTRACT(YEAR FROM "travel_details"."travel_date")
LIMIT 100
"""

_QUESTION = "Show me cost for table not in the year 2015"


def test_postgres_supports_not_in_and_prql_does_not():
    assert dialect_supports_not_in("postgresql")
    assert dialect_supports_not_in("postgres")
    assert dialect_supports_not_in("mysql")
    assert dialect_supports_not_in("sqlserver")
    assert dialect_supports_not_in("")
    assert not dialect_supports_not_in("default")
    assert not dialect_supports_not_in("prql")
    assert not dialect_supports_not_in("dynamo")
    assert not dialect_supports_not_in("himongo")


def test_exclusion_phrase_detection():
    assert question_requests_exclusion(_QUESTION)
    assert question_requests_exclusion("travel cost excluding 2015")
    assert question_requests_exclusion("all years except 2015")
    assert not question_requests_exclusion("cost not equal to 2015")
    assert not question_requests_exclusion("show travel cost")


def test_not_in_year_rewrites_inequality_for_postgres():
    sql = prefer_not_in(_YEAR_SQL, question=_QUESTION, dialect="postgresql")
    compact = " ".join(sql.split()).upper()
    assert "NOT IN" in compact
    assert "<>" not in compact
    assert "2015" in compact


def test_not_equal_question_keeps_inequality():
    sql = prefer_not_in(
        _YEAR_SQL,
        question="Show me cost where the year is not equal to 2015",
        dialect="postgresql",
    )
    assert "<>" in sql
    assert "NOT IN" not in sql.upper()


def test_unsupported_dialect_keeps_inequality():
    sql = prefer_not_in(_YEAR_SQL, question=_QUESTION, dialect="default")
    assert "<>" in sql
    assert "NOT IN" not in sql.upper()


def test_column_to_column_inequality_is_left_alone():
    sql = """
    SELECT "t"."a" AS "A"
    FROM "t"
    WHERE "t"."a" <> "t"."b" AND "t"."year" <> 2015
    """
    rewritten = prefer_not_in(sql, question=_QUESTION, dialect="postgresql")
    compact = " ".join(rewritten.split()).upper()
    assert "NOT IN" in compact
    assert '<> "T"."B"' in compact or "<> T.B" in compact.replace('"', "")


def test_bang_equal_literal_becomes_not_in():
    sql = 'SELECT "t"."cost" AS "Cost" FROM "t" WHERE "t"."year" != 2015'
    rewritten = prefer_not_in(sql, question=_QUESTION, dialect="postgresql")
    assert "NOT IN (2015)" in rewritten
    assert "!=" not in rewritten
    assert '"t"."year"' in rewritten


def test_not_expr_in_collapses_to_not_in_keyword():
    sql = """
    SELECT EXTRACT(YEAR FROM "travel_details"."travel_date") AS "Year"
    FROM "travel_details"
    WHERE NOT EXTRACT(YEAR FROM "travel_details"."travel_date") IN (2015)
    """
    rewritten = prefer_not_in(sql, question=_QUESTION, dialect="postgresql")
    assert (
        'EXTRACT(YEAR FROM "travel_details"."travel_date") NOT IN (2015)'
        in rewritten
    )
    assert "NOT EXTRACT" not in rewritten


def test_prompts_prefer_not_in_for_exclusions():
    from helicalbi.prompt.FinalSqlPrompt import render_final_sql_prompt

    prompt = render_final_sql_prompt("excluding 2015")
    assert "write NOT IN" in prompt
    assert "no IN / NOT IN" in prompt
    assert "write NOT IN" not in render_final_sql_prompt("total travel cost")
    assert "NOT IN (...)" in THINK_FLAT_SQL_GENERATION
    assert "does not support NOT IN" in THINK_FLAT_SQL_GENERATION
