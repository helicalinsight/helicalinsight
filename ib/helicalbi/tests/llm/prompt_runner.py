"""Runners that drive HelicalBI's prompts for deepeval test cases.

Each public function takes a user query plus the relevant slices of the
TravelDetails agent schema and returns the *string* that the LLM produced
(or, in ``stub`` mode, the canned response we'd expect a competent model
to produce).  Returning plain strings keeps the runners trivially
composable with :class:`deepeval.test_case.LLMTestCase`.

The runners deliberately stay close to the production wiring:

* They reuse :class:`InformationProvider` to format the context strings
  exactly the way the real flows do.
* They render the same :class:`PromptTemplate` instances from
  ``helicalbi.prompt`` so prompt-template changes break the tests too.

That means a single source of truth (``TravelDetailsCube.py``) drives both
production code and these LLM evaluations.
"""
from __future__ import annotations

import json
import os
from dataclasses import dataclass
from typing import Any, Iterable

from helicalbi.prompt.DetectColumnPrompt import detect_column_prompt_string
from helicalbi.prompt.DomainTopicPrompt import (
    domain_topic_prompt_string,
    personification,
)
from helicalbi.common.app_config import default_sql_limit
from helicalbi.prompt.FinalSqlPrompt import final_sql_prompt
from helicalbi.prompt.VizPrompt import viz_prompt
from helicalbi.service.agentservice.InformationProvider import InformationProvider
from helicalbi.sql.GetContextForSQL import get_table_col_description


@dataclass
class RenderedPrompt:
    """The literal prompt sent to the LLM plus the model's textual reply."""

    rendered: str
    output: str


def _live_llm():
    """Return the live LLM, side-stepping the stub installed in tests/conftest.py.

    ``tests/conftest.py`` registers a ``helicalbi.common.configuration``
    stub in ``sys.modules`` so the rest of the suite never hits a real
    model.  For the LLM evaluation suite we want the real one when the
    user opts in via ``HELICALBI_LLM_MODE=live``, so we build it directly
    from :class:`LLMManager`.
    """
    from helicalbi.core.manager.LLMManager import LLMManager

    return LLMManager().get_llm()


def _invoke(llm, rendered_prompt: str) -> str:
    response = llm.invoke(rendered_prompt)
    content = getattr(response, "content", response)
    return content if isinstance(content, str) else str(content)


# ---------------------------------------------------------------------------
# Domain & Topic identification
# ---------------------------------------------------------------------------


_DOMAIN_TOPIC_STUBS: dict[str, dict[str, Any]] = {
    "How many meetings were canceled last month?": {
        "domain": ["Sales Operation"],
        "topics": ["Meetings"],
        "reason": (
            "The user is asking about canceled meetings which maps to the "
            "Meetings topic under the Sales Operation domain."
        ),
    },
    "Show me total travel cost by destination city": {
        "domain": ["Sales Operation"],
        "topics": ["Travel"],
        "reason": (
            "Travel cost and destination map to the Travel topic which "
            "involves the Travel and Location components."
        ),
    },
    "Which employee had the highest number of successful meetings?": {
        "domain": ["Sales Operation"],
        "topics": ["Meetings"],
        "reason": (
            "Successful meetings per employee analyses the Meetings topic, "
            "which carries the Employee and Client components."
        ),
    },
    "List travel and meeting details per employee": {
        "domain": ["Sales Operation"],
        "topics": ["Travel", "Meetings"],
        "reason": (
            "The query combines travel records and meeting information, "
            "covering both Travel and Meetings topics."
        ),
    },
}


def render_domain_topic_prompt(agent_data: dict, user_query: str) -> str:
    info_provider = InformationProvider(agent_data=agent_data)
    domain_topic_string = info_provider.format_domain_info("Sales Operation")
    topics = info_provider.get_topics("Sales Operation")
    semantic_string = info_provider.format_semantic_layer(topics)
    input_tables = info_provider.get_input_tables(topics)
    mapping_string = info_provider.get_attribute_string(topics)
    mps = "\n".join(f"{k}->{v}" for d in mapping_string for k, v in d.items())
    business_logic = "Business Logic:" + "\n".join(
        info_provider.get_matching_descriptions(input_tables)
    )

    return domain_topic_prompt_string.format(
        personification=personification,
        domain_topic_string=domain_topic_string,
        semantic_string=semantic_string,
        business_logic=business_logic,
        mps=mps,
        user_query=user_query,
        last_chats=[],
    )


def run_domain_topic(agent_data: dict, user_query: str, *, mode: str) -> RenderedPrompt:
    rendered = render_domain_topic_prompt(agent_data, user_query)
    if mode == "live":
        output = _invoke(_live_llm(), rendered)
    else:
        stub = _DOMAIN_TOPIC_STUBS.get(
            user_query,
            {"domain": ["Sales Operation"], "topics": [], "reason": "unknown"},
        )
        output = json.dumps(stub)
    return RenderedPrompt(rendered=rendered, output=output)


# ---------------------------------------------------------------------------
# Column detection
# ---------------------------------------------------------------------------


_COLUMN_STUBS: dict[str, dict[str, Any]] = {
    "Show me total canceled meetings per client": {
        "columnName": [
            "meeting_details.client_name",
            "meeting_details.meeting_id",
            "meeting_details.meet_cancellation_status",
        ],
        "reason": (
            "Client groupings come from meeting_details.client_name, the "
            "count needs meeting_id, and the cancellation filter uses "
            "meet_cancellation_status."
        ),
    },
    "Average travel cost per employee": {
        "columnName": [
            "travel_details.travel_cost",
            "travel_details.travelled_by",
            "employee_details.employee_id",
            "employee_details.employee_name",
        ],
        "reason": (
            "Aggregating travel_cost from travel_details and joining to "
            "employee_details via travelled_by/employee_id."
        ),
    },
    "List meetings with their destination city": {
        "columnName": [
            "meeting_details.meeting_id",
            "meeting_details.meeting_date",
            "travel_details.destination_id",
            "geo_cordinates.location",
        ],
        "reason": (
            "Destination resolves through travel_details.destination_id to "
            "geo_cordinates.location."
        ),
    },
}


def render_column_prompt(
    agent_data: dict,
    cube_metadata: list,
    synonyms_list: list,
    relationships_list: list,
    table_names: Iterable[str],
    user_query: str,
) -> str:
    table_column_description = get_table_col_description(cube_metadata, list(table_names))
    return detect_column_prompt_string.format(
        table_column_description=table_column_description,
        relationship_of_table=relationships_list,
        last_chats=[],
        user_query=user_query,
        required_synonyms=synonyms_list,
    )


def run_column_detection(
    agent_data: dict,
    cube_metadata: list,
    synonyms_list: list,
    relationships_list: list,
    table_names: Iterable[str],
    user_query: str,
    *,
    mode: str,
) -> RenderedPrompt:
    rendered = render_column_prompt(
        agent_data,
        cube_metadata,
        synonyms_list,
        relationships_list,
        table_names,
        user_query,
    )
    if mode == "live":
        output = _invoke(_live_llm(), rendered)
    else:
        stub = _COLUMN_STUBS.get(
            user_query, {"columnName": [], "reason": "unknown"}
        )
        output = json.dumps(stub)
    return RenderedPrompt(rendered=rendered, output=output)


# ---------------------------------------------------------------------------
# Final SQL generation
# ---------------------------------------------------------------------------


_SQL_STUBS: dict[str, dict[str, Any]] = {
    "Count canceled meetings": {
        "sql": (
            "SELECT COUNT(meeting_details.meeting_id) AS canceled_meetings "
            "FROM meeting_details "
            "WHERE meeting_details.meet_cancellation_status = 'Yes' "
            f"LIMIT {default_sql_limit}"
        ),
        "reason": (
            "Counts rows in meeting_details where cancellation status is "
            "Yes; matches the canceled_meetings business metric."
        ),
    },
    "Total travel cost per destination city": {
        "sql": (
            "SELECT g.location AS destination, "
            "SUM(t.travel_cost) AS total_cost "
            "FROM travel_details t "
            "INNER JOIN geo_cordinates g "
            "ON g.location_id = t.destination_id "
            "GROUP BY g.location "
            f"LIMIT {default_sql_limit}"
        ),
        "reason": (
            "Aggregates travel_cost grouped by destination city using the "
            "travel_details -> geo_cordinates join on destination_id."
        ),
    },
}


def render_sql_prompt(
    user_question: str,
    query_plan_json: str,
    required_joins: str,
    required_metrics: list,
    dialect: str = "PostgreSQL",
) -> str:
    return final_sql_prompt.format(
        dialect=dialect,
        query_plan_json=query_plan_json,
        required_joins=required_joins,
        required_metrics=required_metrics,
        prev_sql="",
        last_chats=[],
        user_question=user_question,
        default_sql_limit=default_sql_limit,
    )


def run_sql_generation(
    user_question: str,
    query_plan_json: str,
    required_joins: str,
    required_metrics: list,
    *,
    dialect: str = "PostgreSQL",
    mode: str,
) -> RenderedPrompt:
    rendered = render_sql_prompt(
        user_question, query_plan_json, required_joins, required_metrics, dialect
    )
    if mode == "live":
        output = _invoke(_live_llm(), rendered)
    else:
        stub = _SQL_STUBS.get(
            user_question, {"sql": "SELECT 1", "reason": "unknown"}
        )
        output = json.dumps(stub)
    return RenderedPrompt(rendered=rendered, output=output)


# ---------------------------------------------------------------------------
# Visualization selection
# ---------------------------------------------------------------------------


_VIZ_STUBS: dict[str, dict[str, Any]] = {
    "Show meeting count by client as a chart": {
        "visualization": "bar",
        "title": "Meetings by Client",
        "reason": (
            "A bar chart compares discrete client categories on a numeric "
            "meeting count, which is the textbook use case for bar charts."
        ),
    },
    "Trend of travel cost over time": {
        "visualization": "line",
        "title": "Travel Cost Trend",
        "reason": (
            "Travel cost over travel_date is a time-series; a line chart "
            "renders temporal trends most clearly."
        ),
    },
    "Total canceled meetings as a single number": {
        "visualization": "kpi",
        "title": "Canceled Meetings",
        "reason": (
            "A single aggregated number is best surfaced as a KPI card."
        ),
    },
}


def render_viz_prompt(
    domain_str: str,
    topics_str: str,
    user_question: str,
    data_types: str,
) -> str:
    return viz_prompt.format(
        domain=domain_str,
        topics=topics_str,
        user_question=user_question,
        data_types=data_types,
        chat_history=[],
        previous_viz="",
    )


def run_viz_selection(
    domain_str: str,
    topics_str: str,
    user_question: str,
    data_types: str,
    *,
    mode: str,
) -> RenderedPrompt:
    rendered = render_viz_prompt(domain_str, topics_str, user_question, data_types)
    if mode == "live":
        output = _invoke(_live_llm(), rendered)
    else:
        stub = _VIZ_STUBS.get(
            user_question,
            {"visualization": "table", "title": "Result", "reason": "fallback"},
        )
        output = json.dumps(stub)
    return RenderedPrompt(rendered=rendered, output=output)
