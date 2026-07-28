"""Tests for similar_chart resolution in ChatResponse."""
from __future__ import annotations

import helicalbi.viz._charts as charts_mod
from helicalbi.model.output.ChatResponse import ChatResponse
from helicalbi.viz._chart_selection import resolve_similar_charts
from helicalbi.viz._charts import hydrate_saved_viz


def _reload_charts():
    charts_mod._CACHE = None
    return charts_mod.get_charts()


def test_bar_similar_comes_from_possible_chart_options():
    _reload_charts()
    metadata = [
        {"1": {"name": "category", "type": "text"}},
        {"2": {"name": "amount", "type": "numeric"}},
        {"rows": 10},
    ]
    similar = resolve_similar_charts("bar", data_types=metadata)
    assert "column" in similar
    assert "pie" in similar
    assert "bar" not in similar
    assert "other" not in similar
    assert "grid_table" not in similar


def test_pie_similar_includes_other_shape_matches():
    _reload_charts()
    metadata = [
        {"name": "region", "type": "text"},
        {"name": "sales", "type": "numeric"},
    ]
    similar = resolve_similar_charts("pie", data_types=metadata)
    assert "bar" in similar
    assert "column" in similar
    assert "donut" in similar or "doughnut" in similar
    assert "pie" not in similar
    assert "grid_table" not in similar


def test_chat_response_includes_similar_chart():
    response = ChatResponse.from_model_state(
        {
            "viz_hint": "bar",
            "vf_title": "Sales by Category",
            "viz_reason": "ranked comparison",
            "similar_chart": ["column", "pie"],
            "vf_string": "function DrawBar() { return null; }",
        }
    )
    payload = response.to_dict()
    assert payload["viz"]["chart_name"] == "bar"
    assert payload["viz"]["similar_chart"] == [
        {"vf.column": "column"},
        {"vf.pie": "pie"},
    ]
    assert "settings" not in payload["viz"]
    assert payload["viz"]["vf_template"]


def test_hydrate_saved_viz_fills_missing_similar_on_open():
    _reload_charts()
    payload = hydrate_saved_viz(
        {
            "chart_name": "bar",
            "vf_title": "Sales",
            "vf_template": "",
            "settings": {"backgroundColor": "#ffffff"},
        },
        data_types=[
            {"name": "category", "type": "text"},
            {"name": "amount", "type": "numeric"},
        ],
    )
    assert payload["chart_name"] == "bar"
    assert "settings" not in payload
    names = [
        next(iter(item.keys()))[3:]
        for item in payload["similar_chart"]
        if item
    ]
    assert "column" in names
