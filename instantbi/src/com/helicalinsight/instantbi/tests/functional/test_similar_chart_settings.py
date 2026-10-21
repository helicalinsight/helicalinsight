"""Tests that viz responses no longer carry similar-chart suggestions."""
from __future__ import annotations

from helicalbi.model.output.ChatResponse import ChatResponse
from helicalbi.viz._charts import hydrate_saved_viz


def test_chat_response_omits_similar_chart():
    response = ChatResponse.from_model_state(
        {
            "viz_hint": "bar",
            "vf_title": "Sales by Category",
            "viz_reason": "ranked comparison",
            "similar_chart": ["donut", "point"],
            "vf_string": "function DrawBar() { return null; }",
        }
    )
    payload = response.to_dict()
    assert payload["viz"]["chart_name"] == "bar"
    assert "similar_chart" not in payload["viz"]
    assert "settings" not in payload["viz"]
    assert payload["viz"]["vf_template"] == ""


def test_other_chart_includes_vf_template_when_vf_created():
    response = ChatResponse.from_model_state(
        {
            "viz_hint": "other",
            "vf_title": "Custom viz",
            "viz_reason": "custom request",
            "vf_string": "function DrawOther() { return null; }",
        }
    )
    payload = response.to_dict()
    assert payload["viz"]["chart_name"] == "other"
    assert payload["viz"]["vf_template"]
    assert "similar_chart" not in payload["viz"]


def test_spaced_chart_name_is_preserved():
    response = ChatResponse.from_model_state(
        {
            "viz_hint": "heat map",
            "vf_title": "Cost matrix",
            "viz_reason": "two dimensions",
            "vf_string": "function DrawHeatmap() { return null; }",
        }
    )
    payload = response.to_dict()
    assert payload["viz"]["chart_name"] == "heat map"
    assert "similar_chart" not in payload["viz"]


def test_hydrate_saved_viz_drops_similar_chart():
    payload = hydrate_saved_viz(
        {
            "chart_name": "relation",
            "vf_title": "Sales",
            "vf_template": "",
            "similar_chart": ["bar", "sankey"],
            "settings": {"backgroundColor": "#ffffff"},
        }
    )
    assert payload["chart_name"] == "relation"
    assert "similar_chart" not in payload
    assert "settings" not in payload
