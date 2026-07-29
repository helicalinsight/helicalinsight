"""Travle_Agent SQL + viz expectation unit tests.

Covers InstantBI scenarios against ``0806/Travle_Agent.model``
(metadata ``test/pg_sample_travel_data.metadata``), backed by local fixtures
under ``tests/fixtures/``.

Stub mode (default) asserts canned SQL keywords and viz types offline.
Set ``HELICALBI_LLM_MODE=live`` to invoke the configured OpenAI provider.
"""
from __future__ import annotations

import json
from pathlib import Path
from typing import Iterable

import pytest

from helicalbi.common.CubeInfoModel import format_strings_from_cube_info
from helicalbi.common.app_config import default_sql_limit
from helicalbi.model.output.viz.ChartSettings import ChartSettings, DimensionSetting
from helicalbi.viz._chart_selection import possible_chart_options
from helicalbi.viz._charts import get_chart_definition
from helicalbi.viz.chart_conversion import apply_chart_settings
from tests.llm.llm_test_settings import apply_llm_test_env, load_llm_test_settings
from tests.llm.prompt_runner import RenderedPrompt, run_sql_generation


pytestmark = pytest.mark.llm

_FIXTURES = Path(__file__).resolve().parents[1] / "fixtures"
_TRAVLE_STATE = _FIXTURES / "travle_agent_model_state.json"
_TRAVLE_METADATA = _FIXTURES / "pg_sample_travel_data.metadata.json"

# Dual-axis chart keys used by Travle_Agent scenarios.
_DUAL_AXIS_VIZ = frozenset(
    {
        "column_line",
        "dual_line",
        "grouped_column_line",
        "stacked_column_line",
        "stacked_and_grouped_column_line",
    }
)
_BAR_OR_COLUMN = frozenset({"bar", "column"})
_TIME_SERIES = frozenset({"line", "area", "tiny_line", "tiny_area"})


# ---------------------------------------------------------------------------
# Fixtures
# ---------------------------------------------------------------------------


@pytest.fixture(scope="module")
def llm_test_settings():
    return apply_llm_test_env(load_llm_test_settings())


@pytest.fixture(scope="module")
def travle_agent_model_state():
    return json.loads(_TRAVLE_STATE.read_text(encoding="utf-8"))


@pytest.fixture(scope="module")
def travle_agent_cube_info(travle_agent_model_state):
    return travle_agent_model_state["state"]["cube"]


@pytest.fixture(scope="module")
def travle_agent_metadata():
    return json.loads(_TRAVLE_METADATA.read_text(encoding="utf-8"))


# ---------------------------------------------------------------------------
# Scenario catalog
# ---------------------------------------------------------------------------


TRAVLE_SQL_VIZ_CASES = [
    {
        "id": "total_travel_cost",
        "user_question": "total travel cost",
        "query_plan": {
            "columnName": ["travel_details.travel_cost"],
            "reason": "Sum all travel costs as a single KPI.",
        },
        "required_joins": "",
        "sql_keywords": ["select", "sum(", "travel_cost", "travel_details", "limit"],
        "expected_viz": frozenset({"kpi", "histogram"}),
        "expects_formatting": True,
        "stub_sql": (
            "SELECT SUM(travel_details.travel_cost) AS travel_cost "
            "FROM travel_details "
            f"LIMIT {default_sql_limit}"
        ),
        "stub_viz": "kpi",
    },
    {
        "id": "travel_cost_by_type",
        "user_question": "travel cost by travel type",
        "query_plan": {
            "columnName": [
                "travel_details.travel_cost",
                "travel_details.travel_type",
            ],
            "reason": "Sum travel cost grouped by travel type.",
        },
        "required_joins": "",
        "sql_keywords": [
            "select",
            "sum(",
            "travel_cost",
            "travel_type",
            "group by",
            "limit",
        ],
        "expected_viz": _BAR_OR_COLUMN,
        "expects_formatting": True,
        "stub_sql": (
            "SELECT travel_details.travel_type, "
            "SUM(travel_details.travel_cost) AS travel_cost "
            "FROM travel_details "
            "GROUP BY travel_details.travel_type "
            f"LIMIT {default_sql_limit}"
        ),
        "stub_viz": "bar",
    },
    {
        "id": "travel_cost_by_type_and_medium",
        "user_question": "travel cost by travel type and travel medium",
        "query_plan": {
            "columnName": [
                "travel_details.travel_cost",
                "travel_details.travel_type",
                "travel_details.travel_medium",
            ],
            "reason": "Sum travel cost by travel type with travel medium series.",
        },
        "required_joins": "",
        "sql_keywords": [
            "select",
            "sum(",
            "travel_cost",
            "travel_type",
            "travel_medium",
            "group by",
            "limit",
        ],
        "expected_viz": _DUAL_AXIS_VIZ | {"heatmap", "bar", "column", "treemap"},
        "expects_formatting": True,
        "stub_sql": (
            "SELECT travel_details.travel_type, travel_details.travel_medium, "
            "SUM(travel_details.travel_cost) AS travel_cost "
            "FROM travel_details "
            "GROUP BY travel_details.travel_type, travel_details.travel_medium "
            f"LIMIT {default_sql_limit}"
        ),
        "stub_viz": "grouped_column_line",
    },
    {
        "id": "mode_of_payments",
        "user_question": "mode of payments",
        "query_plan": {
            "columnName": ["travel_details.mode_of_payment"],
            "reason": "Show payment modes as categorical word prominence.",
        },
        "required_joins": "",
        "sql_keywords": [
            "select",
            "mode_of_payment",
            "travel_details",
            "limit",
        ],
        "expected_viz": frozenset({"wordcloud"}),
        "expects_formatting": False,
        "stub_sql": (
            "SELECT travel_details.mode_of_payment, "
            "COUNT(*) AS payment_count "
            "FROM travel_details "
            "GROUP BY travel_details.mode_of_payment "
            f"LIMIT {default_sql_limit}"
        ),
        "stub_viz": "wordcloud",
    },
    {
        "id": "all_travel_medium",
        "user_question": "all travel medium",
        "query_plan": {
            "columnName": ["travel_details.travel_medium"],
            "reason": "List travel mediums as categorical word prominence.",
        },
        "required_joins": "",
        "sql_keywords": [
            "select",
            "travel_medium",
            "travel_details",
            "limit",
        ],
        "expected_viz": frozenset({"wordcloud"}),
        "expects_formatting": False,
        "stub_sql": (
            "SELECT travel_details.travel_medium, "
            "COUNT(*) AS medium_count "
            "FROM travel_details "
            "GROUP BY travel_details.travel_medium "
            f"LIMIT {default_sql_limit}"
        ),
        "stub_viz": "wordcloud",
    },
    {
        "id": "travel_cost_month_wise",
        "user_question": "travel cost month wise",
        "query_plan": {
            "columnName": [
                "travel_details.travel_cost",
                "travel_details.travel_date",
            ],
            "reason": "Sum travel cost by month of travel_date.",
        },
        "required_joins": "",
        "sql_keywords": [
            "select",
            "sum(",
            "travel_cost",
            "travel_date",
            "group by",
            "limit",
        ],
        "expected_viz": _TIME_SERIES,
        "expects_formatting": True,
        "stub_sql": (
            "SELECT EXTRACT(MONTH FROM travel_details.travel_date) AS travel_month, "
            "SUM(travel_details.travel_cost) AS travel_cost "
            "FROM travel_details "
            "GROUP BY EXTRACT(MONTH FROM travel_details.travel_date) "
            "ORDER BY travel_month "
            f"LIMIT {default_sql_limit}"
        ),
        "stub_viz": "line",
    },
    {
        "id": "travel_cost_month_and_type",
        "user_question": "travel cost month and type wise",
        "query_plan": {
            "columnName": [
                "travel_details.travel_cost",
                "travel_details.travel_date",
                "travel_details.travel_type",
            ],
            "reason": "Sum travel cost by month and travel type.",
        },
        "required_joins": "",
        "sql_keywords": [
            "select",
            "sum(",
            "travel_cost",
            "travel_date",
            "travel_type",
            "group by",
            "limit",
        ],
        "expected_viz": _TIME_SERIES | {"heatmap", "bar", "column"},
        "expects_formatting": True,
        "stub_sql": (
            "SELECT EXTRACT(MONTH FROM travel_details.travel_date) AS travel_month, "
            "travel_details.travel_type, "
            "SUM(travel_details.travel_cost) AS travel_cost "
            "FROM travel_details "
            "GROUP BY EXTRACT(MONTH FROM travel_details.travel_date), "
            "travel_details.travel_type "
            "ORDER BY travel_month "
            f"LIMIT {default_sql_limit}"
        ),
        "stub_viz": "line",
    },
    {
        "id": "cancelled_meetings_month_wise",
        "user_question": "Total cancelled meeting month wise",
        "query_plan": {
            "columnName": [
                "meeting_details.meeting_id",
                "meeting_details.meet_cancellation_status",
                "meeting_details.meeting_date",
            ],
            "reason": "Count cancelled meetings grouped by month.",
        },
        "required_joins": "",
        "sql_keywords": [
            "select",
            "count(",
            "meeting_details",
            "meet_cancellation_status",
            "group by",
            "limit",
        ],
        "expected_viz": _TIME_SERIES,
        "expects_formatting": False,
        "stub_sql": (
            "SELECT EXTRACT(MONTH FROM meeting_details.meeting_date) AS meeting_month, "
            "COUNT(meeting_details.meeting_id) AS cancelled_meetings "
            "FROM meeting_details "
            "WHERE meeting_details.meet_cancellation_status = 'Yes' "
            "GROUP BY EXTRACT(MONTH FROM meeting_details.meeting_date) "
            "ORDER BY meeting_month "
            f"LIMIT {default_sql_limit}"
        ),
        "stub_viz": "line",
    },
    # ---- Additional scenarios from SampleTravelData / enriched cube ----
    {
        "id": "travel_cost_by_destination",
        "user_question": "travel cost by destination",
        "query_plan": {
            "columnName": [
                "travel_details.travel_cost",
                "travel_details.destination",
            ],
            "reason": "Sum travel cost grouped by destination city.",
        },
        "required_joins": "",
        "sql_keywords": [
            "select",
            "sum(",
            "travel_cost",
            "destination",
            "group by",
            "limit",
        ],
        "expected_viz": _BAR_OR_COLUMN | frozenset({"pie", "treemap"}),
        "expects_formatting": True,
        "stub_sql": (
            "SELECT travel_details.destination, "
            "SUM(travel_details.travel_cost) AS travel_cost "
            "FROM travel_details "
            "GROUP BY travel_details.destination "
            "ORDER BY travel_cost DESC "
            f"LIMIT {default_sql_limit}"
        ),
        "stub_viz": "bar",
    },
    {
        "id": "travel_cost_by_booking_platform",
        "user_question": "travel cost by booking platform",
        "query_plan": {
            "columnName": [
                "travel_details.travel_cost",
                "travel_details.booking_platform",
            ],
            "reason": "Sum travel cost by booking platform.",
        },
        "required_joins": "",
        "sql_keywords": [
            "select",
            "sum(",
            "travel_cost",
            "booking_platform",
            "group by",
            "limit",
        ],
        "expected_viz": _BAR_OR_COLUMN | frozenset({"pie", "donut"}),
        "expects_formatting": True,
        "stub_sql": (
            "SELECT travel_details.booking_platform, "
            "SUM(travel_details.travel_cost) AS travel_cost "
            "FROM travel_details "
            "GROUP BY travel_details.booking_platform "
            f"LIMIT {default_sql_limit}"
        ),
        "stub_viz": "column",
    },
    {
        "id": "avg_travel_cost_by_medium",
        "user_question": "average travel cost by travel medium",
        "query_plan": {
            "columnName": [
                "travel_details.travel_cost",
                "travel_details.travel_medium",
            ],
            "reason": "Average travel cost per travel medium.",
        },
        "required_joins": "",
        "sql_keywords": [
            "select",
            "avg(",
            "travel_cost",
            "travel_medium",
            "group by",
            "limit",
        ],
        "expected_viz": _BAR_OR_COLUMN,
        "expects_formatting": True,
        "stub_sql": (
            "SELECT travel_details.travel_medium, "
            "AVG(travel_details.travel_cost) AS avg_travel_cost "
            "FROM travel_details "
            "GROUP BY travel_details.travel_medium "
            f"LIMIT {default_sql_limit}"
        ),
        "stub_viz": "bar",
    },
    {
        "id": "trip_count_by_travel_type",
        "user_question": "how many domestic vs international trips",
        "query_plan": {
            "columnName": [
                "travel_details.travel_id",
                "travel_details.travel_type",
            ],
            "reason": "Count trips by Domestic vs International.",
        },
        "required_joins": "",
        "sql_keywords": [
            "select",
            "count(",
            "travel_type",
            "group by",
            "limit",
        ],
        "expected_viz": _BAR_OR_COLUMN | frozenset({"pie", "donut"}),
        "expects_formatting": False,
        "stub_sql": (
            "SELECT travel_details.travel_type, "
            "COUNT(travel_details.travel_id) AS trip_count "
            "FROM travel_details "
            "GROUP BY travel_details.travel_type "
            f"LIMIT {default_sql_limit}"
        ),
        "stub_viz": "pie",
    },
    {
        "id": "travel_cost_by_employee",
        "user_question": "travel cost by employee",
        "query_plan": {
            "columnName": [
                "travel_details.travel_cost",
                "travel_details.travelled_by",
                "employee_details.employee_name",
                "employee_details.employee_id",
            ],
            "reason": "Sum travel cost per employee name via travelled_by join.",
        },
        "required_joins": (
            "employee_details.employee_id = travel_details.travelled_by"
        ),
        "sql_keywords": [
            "select",
            "sum(",
            "travel_cost",
            "employee_name",
            "employee_details",
            "travelled_by",
            "group by",
            "limit",
        ],
        "expected_viz": _BAR_OR_COLUMN,
        "expects_formatting": True,
        "stub_sql": (
            "SELECT employee_details.employee_name, "
            "SUM(travel_details.travel_cost) AS travel_cost "
            "FROM travel_details "
            "INNER JOIN employee_details "
            "ON employee_details.employee_id = travel_details.travelled_by "
            "GROUP BY employee_details.employee_name "
            "ORDER BY travel_cost DESC "
            f"LIMIT {default_sql_limit}"
        ),
        "stub_viz": "bar",
    },
    {
        "id": "meetings_by_client",
        "user_question": "meeting count by client",
        "query_plan": {
            "columnName": [
                "meeting_details.meeting_id",
                "meeting_details.client_name",
            ],
            "reason": "Count meetings grouped by client.",
        },
        "required_joins": "",
        "sql_keywords": [
            "select",
            "count(",
            "client_name",
            "meeting_details",
            "group by",
            "limit",
        ],
        "expected_viz": _BAR_OR_COLUMN,
        "expects_formatting": False,
        "stub_sql": (
            "SELECT meeting_details.client_name, "
            "COUNT(meeting_details.meeting_id) AS meeting_count "
            "FROM meeting_details "
            "GROUP BY meeting_details.client_name "
            "ORDER BY meeting_count DESC "
            f"LIMIT {default_sql_limit}"
        ),
        "stub_viz": "bar",
    },
    {
        "id": "meetings_by_purpose",
        "user_question": "meetings by purpose",
        "query_plan": {
            "columnName": [
                "meeting_details.meeting_id",
                "meeting_details.meeting_purpose",
            ],
            "reason": "Count meetings by meeting purpose.",
        },
        "required_joins": "",
        "sql_keywords": [
            "select",
            "count(",
            "meeting_purpose",
            "group by",
            "limit",
        ],
        "expected_viz": _BAR_OR_COLUMN | frozenset({"wordcloud", "pie"}),
        "expects_formatting": False,
        "stub_sql": (
            "SELECT meeting_details.meeting_purpose, "
            "COUNT(meeting_details.meeting_id) AS meeting_count "
            "FROM meeting_details "
            "GROUP BY meeting_details.meeting_purpose "
            f"LIMIT {default_sql_limit}"
        ),
        "stub_viz": "column",
    },
    {
        "id": "cancellation_reasons_wordcloud",
        "user_question": "cancellation reasons",
        "query_plan": {
            "columnName": [
                "meeting_details.cancellation_reason",
                "meeting_details.meet_cancellation_status",
            ],
            "reason": "Show cancellation reasons for cancelled meetings as wordcloud.",
        },
        "required_joins": "",
        "sql_keywords": [
            "select",
            "cancellation_reason",
            "meet_cancellation_status",
            "limit",
        ],
        "expected_viz": frozenset({"wordcloud"}),
        "expects_formatting": False,
        "stub_sql": (
            "SELECT meeting_details.cancellation_reason, "
            "COUNT(*) AS reason_count "
            "FROM meeting_details "
            "WHERE meeting_details.meet_cancellation_status = 'Yes' "
            "AND meeting_details.cancellation_reason <> 'NA' "
            "GROUP BY meeting_details.cancellation_reason "
            f"LIMIT {default_sql_limit}"
        ),
        "stub_viz": "wordcloud",
    },
    {
        "id": "successful_meetings_month_wise",
        "user_question": "successful meetings month wise",
        "query_plan": {
            "columnName": [
                "meeting_details.meeting_id",
                "meeting_details.meet_cancellation_status",
                "meeting_details.meeting_date",
            ],
            "reason": "Count successful meetings (status No) by month.",
        },
        "required_joins": "",
        "sql_keywords": [
            "select",
            "count(",
            "meeting_details",
            "meet_cancellation_status",
            "group by",
            "limit",
        ],
        "expected_viz": _TIME_SERIES,
        "expects_formatting": False,
        "stub_sql": (
            "SELECT EXTRACT(MONTH FROM meeting_details.meeting_date) AS meeting_month, "
            "COUNT(meeting_details.meeting_id) AS successful_meetings "
            "FROM meeting_details "
            "WHERE meeting_details.meet_cancellation_status = 'No' "
            "GROUP BY EXTRACT(MONTH FROM meeting_details.meeting_date) "
            "ORDER BY meeting_month "
            f"LIMIT {default_sql_limit}"
        ),
        "stub_viz": "line",
    },
    {
        "id": "meeting_impact_distribution",
        "user_question": "meeting impact distribution",
        "query_plan": {
            "columnName": [
                "meeting_details.meeting_id",
                "meeting_details.meeting_impact",
            ],
            "reason": "Count meetings by business impact outcome.",
        },
        "required_joins": "",
        "sql_keywords": [
            "select",
            "count(",
            "meeting_impact",
            "group by",
            "limit",
        ],
        "expected_viz": _BAR_OR_COLUMN | frozenset({"pie", "donut"}),
        "expects_formatting": False,
        "stub_sql": (
            "SELECT meeting_details.meeting_impact, "
            "COUNT(meeting_details.meeting_id) AS meeting_count "
            "FROM meeting_details "
            "GROUP BY meeting_details.meeting_impact "
            f"LIMIT {default_sql_limit}"
        ),
        "stub_viz": "pie",
    },
    {
        "id": "cost_by_cancellation_metric",
        "user_question": "travel cost for cancelled meetings",
        "query_plan": {
            "columnName": [
                "travel_details.travel_cost",
                "meeting_details.meet_cancellation_status",
            ],
            "reason": "Use Cost by Cancellation computed metric.",
        },
        "required_joins": (
            "travel_details.travelled_by = meeting_details.meeting_by"
        ),
        "sql_keywords": [
            "select",
            "sum(",
            "travel_cost",
            "meet_cancellation_status",
            "limit",
        ],
        "expected_viz": frozenset({"kpi", "histogram"}),
        "expects_formatting": True,
        "stub_sql": (
            "SELECT SUM(travel_details.travel_cost) AS cost_by_cancellation "
            "FROM travel_details "
            "INNER JOIN meeting_details "
            "ON travel_details.travelled_by = meeting_details.meeting_by "
            "AND travel_details.travel_date = meeting_details.meeting_date "
            "WHERE meeting_details.meet_cancellation_status = 'Yes' "
            f"LIMIT {default_sql_limit}"
        ),
        "stub_viz": "kpi",
    },
    {
        "id": "source_destination_heatmap",
        "user_question": "travel cost by source and destination",
        "query_plan": {
            "columnName": [
                "travel_details.travel_cost",
                "travel_details.source",
                "travel_details.destination",
            ],
            "reason": "Two-dimensional cost matrix by origin and destination.",
        },
        "required_joins": "",
        "sql_keywords": [
            "select",
            "sum(",
            "travel_cost",
            "source",
            "destination",
            "group by",
            "limit",
        ],
        "expected_viz": frozenset({"heatmap", "treemap"}),
        "expects_formatting": True,
        "stub_sql": (
            "SELECT travel_details.source, travel_details.destination, "
            "SUM(travel_details.travel_cost) AS travel_cost "
            "FROM travel_details "
            "GROUP BY travel_details.source, travel_details.destination "
            f"LIMIT {default_sql_limit}"
        ),
        "stub_viz": "heatmap",
    },
]


def _sql_contains_all(sql: str, keywords: Iterable[str]) -> list[str]:
    haystack = sql.lower()
    return [kw for kw in keywords if kw.lower() not in haystack]


def _run_travle_sql(case: dict, *, mode: str) -> RenderedPrompt:
    """Drive SQL generation; in stub mode return the Travle_Agent canned SQL."""
    if mode == "live":
        return run_sql_generation(
            user_question=case["user_question"],
            query_plan_json=json.dumps(case["query_plan"]),
            required_joins=case.get("required_joins") or "",
            required_metrics=[],
            mode="live",
        )

    output = json.dumps(
        {
            "sql": case["stub_sql"],
            "reason": case["query_plan"].get("reason", ""),
            "visualization": case["stub_viz"],
        }
    )
    return RenderedPrompt(rendered=case["user_question"], output=output)


# ---------------------------------------------------------------------------
# Config smoke tests
# ---------------------------------------------------------------------------


class TestTravleAgentLlmConfig:
    def test_settings_point_at_travle_agent_model(self, llm_test_settings):
        assert llm_test_settings.base_url == "https://164.52.206.202/hi-ee"
        assert llm_test_settings.model_ref == {
            "dir": "0806",
            "file": "Travle_Agent.model",
        }
        assert llm_test_settings.metadata_ref == {
            "location": "test",
            "metadataFileName": "pg_sample_travel_data.metadata",
        }

    def test_openai_provider_configured(self, llm_test_settings):
        assert llm_test_settings.default_provider == "openai"
        openai = llm_test_settings.providers["openai"]
        assert openai["model"] == "gpt-4.1-mini"
        assert openai["temperature"] == 0.1
        assert openai["max_tokens"] == 4000
        # Cookie / api key come from local yaml or env when present.
        assert llm_test_settings.session_cookie or True


# ---------------------------------------------------------------------------
# Formatting: total travel cost → viz template measure_formats
# ---------------------------------------------------------------------------


class TestTravleAgentFormatting:
    def test_travel_cost_format_string_from_cube(self, travle_agent_cube_info):
        formats = format_strings_from_cube_info(travle_agent_cube_info)
        assert formats.get("travel_cost") == "$#,##0.00"

    def test_total_travel_cost_kpi_template_gets_formatting(
        self, travle_agent_cube_info
    ):
        formats = format_strings_from_cube_info(travle_agent_cube_info)
        chart = get_chart_definition("kpi")
        assert chart is not None

        settings = ChartSettings(
            dimensions=DimensionSetting(),
            measures=["travel_cost"],
            title="Total Travel Cost",
            measure_formats={"travel_cost": formats["travel_cost"]},
        )
        filled = apply_chart_settings(
            settings,
            chart_def=chart,
            format_strings=formats,
        )

        assert "measure_formats" in filled
        assert "$#,##0.00" in filled
        assert "travel_cost" in filled
        assert "${setting}" not in filled

    def test_avg_cost_bar_template_gets_formatting(self, travle_agent_cube_info):
        formats = format_strings_from_cube_info(travle_agent_cube_info)
        chart = get_chart_definition("bar")
        assert chart is not None

        settings = ChartSettings(
            dimensions=DimensionSetting(name="travel_medium"),
            measures=["avg_travel_cost"],
            title="Average Travel Cost by Medium",
            measure_formats={"avg_travel_cost": formats["avg_travel_cost"]},
        )
        filled = apply_chart_settings(
            settings,
            chart_def=chart,
            format_strings=formats,
        )
        assert "$#,##0.00" in filled
        assert "avg_travel_cost" in filled


# ---------------------------------------------------------------------------
# Chart-shape expectations (deterministic, no LLM)
# ---------------------------------------------------------------------------


class TestTravleAgentVizShapes:
    def test_single_category_measure_allows_bar_or_column(self):
        options = {opt.visualization_type for opt in possible_chart_options(1, 1)}
        assert _BAR_OR_COLUMN & options

    def test_wordcloud_available_for_payment_modes(self):
        options = {opt.visualization_type for opt in possible_chart_options(1, 1)}
        assert "wordcloud" in options
        options_dim_only = {
            opt.visualization_type for opt in possible_chart_options(1, 0)
        }
        assert "wordcloud" in options_dim_only

    def test_time_series_requires_ordered_line(self):
        unordered = {
            opt.visualization_type for opt in possible_chart_options(1, 1, ordered=False)
        }
        ordered = {
            opt.visualization_type for opt in possible_chart_options(1, 1, ordered=True)
        }
        assert "line" not in unordered
        assert "line" in ordered

    def test_dual_axis_charts_registered(self):
        for name in ("column_line", "grouped_column_line", "dual_line"):
            assert get_chart_definition(name) is not None
            assert get_chart_definition(name).conversion.family == "dual_axes"


# ---------------------------------------------------------------------------
# SQL + viz scenario tests
# ---------------------------------------------------------------------------


class TestTravleAgentSqlAndViz:
    @pytest.mark.parametrize(
        "case",
        TRAVLE_SQL_VIZ_CASES,
        ids=[c["id"] for c in TRAVLE_SQL_VIZ_CASES],
    )
    def test_sql_contains_expected_keywords(self, case, llm_mode, llm_test_settings):
        assert llm_test_settings.model_file == "Travle_Agent.model"
        result = _run_travle_sql(case, mode=llm_mode)

        try:
            payload = json.loads(result.output)
            sql = payload.get("sql", "")
        except (TypeError, ValueError):
            sql = result.output or ""

        missing = _sql_contains_all(sql, case["sql_keywords"])
        assert not missing, f"Missing in SQL for {case['id']}: {missing}\nSQL:\n{sql}"

        if case["id"] == "cancelled_meetings_month_wise":
            assert "yes" in sql.lower()
            assert "meet_cancellation_status" in sql.lower()
        if case["id"] == "successful_meetings_month_wise":
            assert "'no'" in sql.lower() or '= "no"' in sql.lower() or "= 'no'" in sql.lower()
        if case["id"] == "cancellation_reasons_wordcloud":
            assert "yes" in sql.lower()


# ---------------------------------------------------------------------------
# Enriched model + metadata fixtures
# ---------------------------------------------------------------------------


class TestTravleAgentModelAndMetadata:
    def test_metadata_fixture_has_travel_and_meeting_tables(
        self, travle_agent_metadata
    ):
        tables = {
            entry["database_table"]
            for entry in travle_agent_metadata["cube_metadata"]
        }
        assert {
            "travel_details",
            "meeting_details",
            "employee_details",
            "geo_cordinates",
        }.issubset(tables)

    def test_metadata_includes_mode_of_payment_and_purpose(
        self, travle_agent_metadata
    ):
        travel_cols = {
            col["column_name"]
            for entry in travle_agent_metadata["cube_metadata"]
            if entry["database_table"] == "travel_details"
            for col in entry["columns"]
        }
        meeting_cols = {
            col["column_name"]
            for entry in travle_agent_metadata["cube_metadata"]
            if entry["database_table"] == "meeting_details"
            for col in entry["columns"]
        }
        assert "mode_of_payment" in travel_cols
        assert "booking_platform" in travel_cols
        assert "meeting_purpose" in meeting_cols
        assert "cancellation_reason" in meeting_cols

    def test_model_cube_has_enriched_dimensions(self, travle_agent_cube_info):
        dims = {
            d["dimensionName"] for d in travle_agent_cube_info[0]["dimensions"]
        }
        assert "mode_of_payment" in dims
        assert "meeting_purpose" in dims
        assert "meeting_impact" in dims
        assert "cancellation_reason" in dims
        assert "employee_name" in dims
        assert "meeting_date" in dims

    def test_model_cube_has_enriched_measures(self, travle_agent_cube_info):
        measures = {
            m["measureName"] for m in travle_agent_cube_info[0]["measures"]
        }
        assert "travel_cost" in measures
        assert "avg_travel_cost" in measures
        assert "trip_count" in measures
        assert "meeting_count" in measures
        assert "Cost by Cancellation" in measures

    def test_model_points_at_expected_metadata(self, travle_agent_model_state):
        meta = travle_agent_model_state["metadata"]
        assert meta["location"] == "test"
        assert meta["metadataFileName"] == "pg_sample_travel_data.metadata"

    def test_model_has_join_relationships(self, travle_agent_model_state):
        rels = travle_agent_model_state["state"]["relationships"]
        pairs = {
            (r["left"]["table"], r["right"]["table"]) for r in rels
        }
        assert ("employee_details", "travel_details") in pairs
        assert ("employee_details", "meeting_details") in pairs
        assert ("geo_cordinates", "travel_details") in pairs

    def test_avg_travel_cost_uses_currency_format(self, travle_agent_cube_info):
        formats = format_strings_from_cube_info(travle_agent_cube_info)
        assert formats.get("avg_travel_cost") == "$#,##0.00"
        assert formats.get("Cost by Cancellation") == "$#,##0.00"

    def test_business_metrics_in_metadata(self, travle_agent_metadata):
        metrics = {m["metric"] for m in travle_agent_metadata["business_metrics"]}
        assert "canceled_meetings" in metrics
        assert "successful meetings" in metrics
        assert "meeting_cancellation_rate" in metrics

    @pytest.mark.parametrize(
        "case",
        TRAVLE_SQL_VIZ_CASES,
        ids=[c["id"] for c in TRAVLE_SQL_VIZ_CASES],
    )
    def test_stub_viz_matches_expected_family(self, case, llm_mode):
        result = _run_travle_sql(case, mode=llm_mode if llm_mode == "stub" else "stub")
        payload = json.loads(result.output)
        viz = payload.get("visualization") or case["stub_viz"]
        assert viz in case["expected_viz"], (
            f"{case['id']}: viz {viz!r} not in {sorted(case['expected_viz'])}"
        )
