"""Unit tests for descriptive SQL findings (no raw JSON in analysis)."""

from helicalbi.sql_agent.tools.findings import describe_query_findings, prefers_detail_table

import pytest

pytestmark = pytest.mark.functional


def test_describe_single_kpi_not_raw_json():
    described = describe_query_findings(
        "[{'Travel Cost': 18533970}]",
        question="What is the total travel cost for the selected period?",
        title="Total Travel Cost",
    )
    assert "[" not in described["answer"]
    assert "{" not in described["analysis"]
    assert "18,533,970" in described["answer"]
    assert "Travel Cost" in described["analysis"] or "travel cost" in described["analysis"].lower()


def test_describe_breakdown():
    raw = (
        "[{'Travel Type': 'Domestic', 'Travel Cost': 2389070}, "
        "{'Travel Type': 'International', 'Travel Cost': 16144900}]"
    )
    described = describe_query_findings(
        raw,
        question="How is total travel cost distributed across travel types?",
        title="Travel Cost by Travel Type",
    )
    assert "Domestic" in described["answer"]
    assert "International" in described["answer"]
    assert not described["analysis"].lstrip().startswith("[")


def test_describe_timeseries_mentions_detail_table():
    raw = (
        "[{'Travel Date': '2026-01-01', 'Travel Cost': 8000}, "
        "{'Travel Date': '2026-01-02', 'Travel Cost': 9000}, "
        "{'Travel Date': '2026-01-03', 'Travel Cost': 7000}]"
    )
    described = describe_query_findings(
        raw,
        question="How has total travel cost moved over travel dates?",
        title="Travel Cost Trend Over Time",
    )
    assert "time series" in described["analysis"].lower() or "points" in described["answer"].lower()
    assert not described["analysis"].lstrip().startswith("[")


def test_prefers_detail_table_for_trend():
    assert prefers_detail_table(viz_hint="line", question="cost trend over dates")
    assert prefers_detail_table(question="How has total travel cost moved over travel dates?")
    assert not prefers_detail_table(viz_hint="kpi", question="What is total travel cost?")
