"""The relative-date prompt runs after final SQL and does not parse the SQL itself."""

from helicalbi.core.sqlflow.RelativeDateDetect import sql_has_relative_date
from helicalbi.prompt.RelativeDatePrompt import catalog_token_names, render_relative_date_prompt

EXTRACT_SQL = """
SELECT "travel_details"."travel_medium" AS "Travel Medium",
       EXTRACT(YEAR FROM "travel_details"."travel_date") AS "Year",
       SUM("travel_details"."travel_cost") AS "Travel Cost"
FROM "travel_details"
WHERE EXTRACT(YEAR FROM "travel_details"."travel_date") IN (
    EXTRACT(YEAR FROM CURRENT_DATE),
    EXTRACT(YEAR FROM CURRENT_DATE) + 1
)
GROUP BY "travel_details"."travel_medium", EXTRACT(YEAR FROM "travel_details"."travel_date")
LIMIT 100
"""


def test_prompt_lists_catalog_tokens_and_the_extract_example():
    prompt = render_relative_date_prompt({
        "functions": [
            {"name": "year"},
            {"name": "today"},
            {"name": "to_date"},
        ]
    })
    assert "year, today, to_date" in prompt
    assert 'values: ["year", "year+1"]' in prompt
    assert "tomorrow" in prompt
    assert "anchor_" in prompt
    assert "IS_NOT_BETWEEN" in prompt
    assert "hour" in prompt
    assert "{sql}" in prompt
    assert "2015" in prompt


def test_missing_catalog_uses_shipped_token_names():
    assert "year" in catalog_token_names(None)
    assert "to_date" in catalog_token_names({"functions": []})


def test_extract_in_current_date_is_sent_to_the_prompt():
    assert sql_has_relative_date(EXTRACT_SQL) is True


def test_named_year_sql_is_not_sent_to_the_prompt():
    sql = 'SELECT 1 FROM travel_details WHERE EXTRACT(YEAR FROM travel_date) = 2015'
    assert sql_has_relative_date(sql) is False
