"""Question → SQL checks for the Corporate Travel Management model JSON.

``HELICALBI_LLM_MODE=live`` sends each question to the LLM configured in
``llm_config.yaml`` (DeepSeek when ``DEEPSEEK_API_KEY`` is set). Stub mode
uses the sample SQL so the same cases run without a provider.

Each case keeps one sample statement. Wording and aliases may differ.
sqlglot checks that the named columns are in the SELECT list and that each
filter column and value is in the WHERE clause.
"""
from __future__ import annotations

import json
import re

import pytest
import sqlglot
from sqlglot import exp

from helicalbi.common.DialectMapper import resolve_sqlglot_dialect
from helicalbi.common.app_config import default_sql_limit
from helicalbi.common.cube_info.prepare import prepare_cube_info_model_data
from helicalbi.prompt.FinalSqlPrompt import render_final_sql_prompt
from helicalbi.sql.GetContextForSQL import get_table_col_description

pytestmark = pytest.mark.llm

CORPORATE_TRAVEL_MODEL = {
    "cube": [
        {
            "dimensions": [
                {
                    "dimensionName": "Client Name",
                    "semanticType": "Person Name",
                    "columnName": "meeting_details.client_name",
                    "columnId": "14006",
                    "sort": "Natural",
                    "aiContext": {
                        "instructions": "Represents the name of the client associated with a meeting.",
                        "synonyms": "customer, client",
                        "examples": "Meetings by client",
                    },
                },
                {
                    "dimensionName": "Cancellation Status",
                    "semanticType": "Boolean",
                    "columnName": "meeting_details.meet_cancellation_status",
                    "columnId": "14009",
                    "formula": "meeting_details.meet_cancellation_status='Yes'",
                    "sort": "Natural",
                    "aiContext": {
                        "instructions": "Yes means the meeting was cancelled.",
                        "synonyms": "cancelled, meeting cancelled",
                        "examples": "Cancelled meetings",
                    },
                },
                {
                    "dimensionName": "Journey Date",
                    "semanticType": "Datetime",
                    "columnName": "travel_details.travel_date",
                    "columnId": "14016",
                    "sort": "Natural",
                    "aiContext": {
                        "instructions": "Primary date dimension. Apply a year filter to Journey Date.",
                        "synonyms": "travel date, journey date",
                        "examples": "Travel this year",
                    },
                },
                {
                    "dimensionName": "Year",
                    "semanticType": "Number",
                    "columnName": "travel_details.travel_date",
                    "columnId": "14016",
                    "formula": "EXTRACT(YEAR FROM travel_details.travel_date)",
                    "sort": "Natural",
                    "aiContext": {
                        "instructions": "Calendar year from Journey Date. Filter the year on Journey Date.",
                        "synonyms": "travel year, calendar year",
                        "examples": "Travel cost by year",
                    },
                },
                {
                    "dimensionName": "City of Travel Start",
                    "semanticType": "City",
                    "columnName": "travel_details.source",
                    "columnId": "14020",
                    "sort": "Natural",
                    "aiContext": {
                        "instructions": "City where travel started.",
                        "synonyms": "origin, departure city, source",
                        "examples": "Travel from Hyderabad",
                    },
                },
                {
                    "dimensionName": "City meeting held",
                    "semanticType": "City",
                    "columnName": "travel_details.destination",
                    "columnId": "14022",
                    "sort": "Natural",
                    "aiContext": {
                        "instructions": "City where travel ended.",
                        "synonyms": "destination, arrival city",
                        "examples": "Travel to Delhi",
                    },
                },
                {
                    "dimensionName": "Booking Platform",
                    "semanticType": "Text",
                    "columnName": "travel_details.booking_platform",
                    "columnId": "23788",
                    "sort": "Natural",
                    "aiContext": {
                        "instructions": "Platform used to book the travel.",
                        "synonyms": "booking platform",
                        "examples": "Bookings by platform",
                    },
                },
            ],
            "measures": [
                {
                    "measureName": "Cost Incurred",
                    "aggregator": "Sum",
                    "columnName": "travel_details.travel_cost",
                    "columnId": "14023",
                    "semanticType": "Currency",
                    "formula": "Sum(travel_details.travel_cost)",
                    "aiContext": {
                        "instructions": "Monetary cost of travel. Aggregate with Sum.",
                        "synonyms": "travel cost, trip cost",
                        "examples": "Total travel cost",
                    },
                },
                {
                    "measureName": "Total Travel",
                    "aggregator": "Count",
                    "columnName": "travel_details.travel_id",
                    "columnId": "14356",
                    "semanticType": "Number",
                    "formula": "",
                    "aiContext": {
                        "instructions": "Number of travel occurrences. Aggregate with Count.",
                        "synonyms": "total tours, travel count",
                        "examples": "Number of travels",
                    },
                },
                {
                    "measureName": "Meeting Id",
                    "aggregator": "Count",
                    "columnName": "meeting_details.meeting_id",
                    "columnId": "23750",
                    "semanticType": "Number",
                    "formula": "",
                    "aiContext": {
                        "instructions": "Count of meetings.",
                        "synonyms": "meeting count",
                        "examples": "",
                    },
                },
            ],
        }
    ],
    "domain": [
        {
            "domain_name": "Corporate Travel Management",
            "description": "Corporate travel, cost, and meetings.",
            "topics": [
                {
                    "topic": "Travel",
                    "description": "Travel cost, volume, route, and date.",
                    "components": [
                        {"id": "14016", "name": "Journey Date"},
                        {"id": "14016", "name": "Year"},
                        {"id": "14020", "name": "City of Travel Start"},
                        {"id": "14022", "name": "City meeting held"},
                        {"id": "23788", "name": "Booking Platform"},
                        {"id": "14023", "name": "Cost Incurred"},
                        {"id": "14356", "name": "Total Travel"},
                    ],
                },
                {
                    "topic": "Meetings",
                    "description": "Client meetings and cancellations.",
                    "components": [
                        {"id": "14006", "name": "Client Name"},
                        {"id": "14009", "name": "Cancellation Status"},
                        {"id": "23750", "name": "Meeting Id"},
                    ],
                },
            ],
        }
    ],
}

# One sample statement per question. SELECT aliases, join style, and function
# spelling may differ. ``select`` columns must appear in the SELECT list.
# Each ``where`` item must appear as that column filtered to that value.
QUESTIONS = [
    {
        "id": "travel_cost_2026",
        "question": "What is the total travel cost for the year 2026",
        "select": ["travel_cost"],
        "where": [{"column": "travel_date", "value": "2026"}],
        "example_sql": (
            "SELECT SUM(t.travel_cost) AS \"Cost Incurred\" "
            "FROM travel_details t "
            "WHERE EXTRACT(YEAR FROM t.travel_date) = 2026"
        ),
    },
    {
        "id": "travel_count_2026",
        "question": "What are the total number of travels for the year 2026",
        "select": ["travel_id"],
        "where": [{"column": "travel_date", "value": "2026"}],
        "example_sql": (
            "SELECT COUNT(t.travel_id) AS \"Total Travel\" "
            "FROM travel_details t "
            "WHERE EXTRACT(YEAR FROM t.travel_date) = 2026"
        ),
    },
    {
        "id": "canceled_meetings_2026",
        "question": "What are the total number of canceled meetings for the year 2026",
        "select": ["meeting_id"],
        "where": [
            {"column": "meet_cancellation_status", "value": "Yes"},
            {"column": "travel_date", "value": "2026"},
        ],
        "example_sql": (
            "SELECT COUNT(m.meeting_id) AS \"Meeting Id\" "
            "FROM meeting_details m "
            "JOIN travel_details t ON t.travel_id = m.meeting_id "
            "WHERE m.meet_cancellation_status = 'Yes' "
            "AND EXTRACT(YEAR FROM t.travel_date) = 2026"
        ),
    },
    {
        "id": "client_travels_and_cost_2026",
        "question": "Show me the client name wise number of travels and cost for the year 2026",
        "select": ["client_name", "travel_id", "travel_cost"],
        "where": [{"column": "travel_date", "value": "2026"}],
        "example_sql": (
            "SELECT m.client_name AS \"Client Name\", "
            "COUNT(t.travel_id) AS \"Total Travel\", "
            "SUM(t.travel_cost) AS \"Cost Incurred\" "
            "FROM meeting_details m "
            "JOIN travel_details t ON t.travel_id = m.meeting_id "
            "WHERE EXTRACT(YEAR FROM t.travel_date) = 2026 "
            "GROUP BY m.client_name"
        ),
    },
    {
        "id": "quarter_travels_and_cost_2026",
        "question": "Show me quarter wise number of travels and cost for the year 2026",
        "select": ["travel_date", "travel_id", "travel_cost"],
        "where": [{"column": "travel_date", "value": "2026"}],
        "example_sql": (
            "SELECT EXTRACT(QUARTER FROM t.travel_date) AS quarter, "
            "COUNT(t.travel_id) AS \"Total Travel\", "
            "SUM(t.travel_cost) AS \"Cost Incurred\" "
            "FROM travel_details t "
            "WHERE EXTRACT(YEAR FROM t.travel_date) = 2026 "
            "GROUP BY EXTRACT(QUARTER FROM t.travel_date)"
        ),
    },
    {
        "id": "top_5_routes",
        "question": "List the top 5 source-destination combinations with the highest total tours",
        "select": ["source", "destination", "travel_id"],
        "where": [],
        "limit": "5",
        "example_sql": (
            "SELECT t.source AS \"City of Travel Start\", "
            "t.destination AS \"City meeting held\", "
            "COUNT(t.travel_id) AS \"Total Travel\" "
            "FROM travel_details t "
            "GROUP BY t.source, t.destination "
            "ORDER BY COUNT(t.travel_id) DESC "
            "LIMIT 5"
        ),
    },
    {
        "id": "tours_by_cancellation_and_platform",
        "question": "Show total tours by meet cancellation status for each booking platform",
        "select": ["meet_cancellation_status", "booking_platform", "travel_id"],
        "where": [],
        "example_sql": (
            "SELECT m.meet_cancellation_status AS \"Cancellation Status\", "
            "t.booking_platform AS \"Booking Platform\", "
            "COUNT(t.travel_id) AS \"Total Travel\" "
            "FROM meeting_details m "
            "JOIN travel_details t ON t.travel_id = m.meeting_id "
            "GROUP BY m.meet_cancellation_status, t.booking_platform"
        ),
    },
]


def _catalog(prepared: dict) -> str:
    return get_table_col_description(
        prepared["cube_metadata"],
        ["travel_details", "meeting_details"],
        model_data=prepared,
    )


def _prompt(question: str, catalog: str) -> str:
    metrics = (
        "Cost Incurred = Sum(travel_details.travel_cost)\n"
        "Total Travel = COUNT(travel_details.travel_id)\n"
        "Meeting Id = COUNT(meeting_details.meeting_id)\n"
        "Year = EXTRACT(YEAR FROM travel_details.travel_date)\n"
        "Cancellation Status = meeting_details.meet_cancellation_status='Yes'"
    )
    body = render_final_sql_prompt(question).format(
        dialect="postgresql",
        query_plan_json=catalog,
        required_joins="",
        required_metrics=metrics,
        required_column_description=catalog,
        required_functions="",
        column_sort_orders=(
            "No Ascending/Descending dimension sorts are configured. "
            "Add ORDER BY only when the question asks for a ranking."
        ),
        domain_context="Domain: Corporate Travel Management. Topics: Travel, Meetings.",
        prev_sql="",
        last_chats=[],
        user_question=question,
        default_sql_limit=default_sql_limit,
    )
    return body + "\nReturn one JSON object: {\"sql\": \"...\", \"reason\": \"...\"}\n"


def _extract_sql(text: str) -> str:
    raw = str(text or "").strip()
    fenced = re.search(r"```(?:sql|json)?\s*(.*?)```", raw, re.DOTALL | re.IGNORECASE)
    if fenced:
        raw = fenced.group(1).strip()
    try:
        payload = json.loads(raw)
    except json.JSONDecodeError:
        payload = None
    if isinstance(payload, dict) and payload.get("sql"):
        return str(payload["sql"]).strip()
    return raw.strip()


def _live_sql(prompt: str) -> str:
    from helicalbi.core.manager.LLMManager import LLMManager

    llm = LLMManager().get_llm()
    if llm is None:
        pytest.skip("Configured LLM is unavailable. Set DEEPSEEK_API_KEY for the deepseek provider.")
    response = llm.invoke(prompt)
    content = getattr(response, "content", response)
    return _extract_sql(content if isinstance(content, str) else str(content))


@pytest.fixture(scope="module")
def travel_prepared() -> dict:
    return prepare_cube_info_model_data(CORPORATE_TRAVEL_MODEL, {})


def _column_names(node) -> set[str]:
    return {column.name.lower() for column in node.find_all(exp.Column) if column.name}


def _literal_texts(node) -> list[str]:
    return [str(literal.this) for literal in node.find_all(exp.Literal)]


def _assert_sql_columns(sql: str, case: dict) -> None:
    """Confirm SELECT columns and WHERE column/value pairs with sqlglot."""
    try:
        tree = sqlglot.parse_one(sql, read=resolve_sqlglot_dialect("postgresql"))
    except sqlglot.errors.ParseError as exc:
        pytest.fail(f"{case['question']}\nsqlglot parse error: {exc}\nactual:\n{sql}")

    select = tree.find(exp.Select)
    assert select is not None, f"{case['question']}\nno SELECT\nactual:\n{sql}"
    select_columns = set()
    for expression in select.expressions:
        select_columns |= _column_names(expression)

    where = tree.find(exp.Where)
    where_columns = _column_names(where) if where is not None else set()
    where_values = _literal_texts(where) if where is not None else []

    missing = []
    for column in case["select"]:
        if column.lower() not in select_columns:
            missing.append(f"SELECT {column}")
    for predicate in case["where"]:
        column = predicate["column"]
        value = str(predicate["value"]).lower()
        if column.lower() not in where_columns:
            missing.append(f"WHERE {column}")
        elif not any(value in literal.lower() for literal in where_values):
            missing.append(f"WHERE {column} = {predicate['value']}")

    if case.get("limit"):
        limit = tree.find(exp.Limit)
        limit_text = "" if limit is None else " ".join(_literal_texts(limit))
        if case["limit"] not in limit_text.split():
            missing.append(f"LIMIT {case['limit']}")

    if any(count.find(exp.Star) is not None for count in tree.find_all(exp.Count)):
        missing.append("COUNT(*)")

    assert not missing, (
        f"{case['question']}\n"
        f"missing: {missing}\n"
        f"select columns: {sorted(select_columns)}\n"
        f"where columns: {sorted(where_columns)}\n"
        f"where values: {where_values}\n"
        f"one acceptable style:\n{case['example_sql']}\n"
        f"actual:\n{sql}"
    )


@pytest.mark.parametrize("case", QUESTIONS, ids=[case["id"] for case in QUESTIONS])
def test_question_against_expected_sql(case, travel_prepared, llm_mode):
    if llm_mode == "live":
        sql = _live_sql(_prompt(case["question"], _catalog(travel_prepared)))
    else:
        sql = case["example_sql"]
    _assert_sql_columns(sql, case)
