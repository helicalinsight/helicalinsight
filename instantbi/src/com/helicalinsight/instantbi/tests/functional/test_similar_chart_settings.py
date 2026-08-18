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
    assert "donut" in similar
    assert "pie" in similar
    assert "point" in similar
    assert "bar" not in similar
    assert "other" not in similar
    assert "grid_table" not in similar


def test_donut_similar_includes_other_shape_matches():
    _reload_charts()
    metadata = [
        {"name": "region", "type": "text"},
        {"name": "sales", "type": "numeric"},
    ]
    similar = resolve_similar_charts("donut", data_types=metadata)
    assert "bar" in similar
    assert "column" in similar
    assert "pie" in similar
    assert "point" in similar
    assert "donut" not in similar
    assert "grid_table" not in similar


def test_chat_response_includes_similar_chart():
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
    assert payload["viz"]["similar_chart"] == [
        {"vf.bar": "bar"},
        {"vf.donut": "donut"},
        {"vf.point": "point"},
    ]
    assert "settings" not in payload["viz"]
    assert payload["viz"]["vf_template"]


def test_spaced_chart_name_becomes_vf_underscore_key():
    """chart_name may contain whitespace; wire key uses underscores."""
    response = ChatResponse.from_model_state(
        {
            "viz_hint": "heat map",
            "vf_title": "Cost matrix",
            "viz_reason": "two dimensions",
            "similar_chart": ["relation", "heat map"],
            "vf_string": "function DrawHeatmap() { return null; }",
        }
    )
    payload = response.to_dict()
    assert payload["viz"]["chart_name"] == "heat map"
    assert payload["viz"]["similar_chart"][0] == {"vf.heat_map": "heat map"}
    assert {"vf.relation": "relation"} in payload["viz"]["similar_chart"]


def test_hydrate_saved_viz_fills_missing_similar_on_open():
    _reload_charts()
    payload = hydrate_saved_viz(
        {
            "chart_name": "relation",
            "vf_title": "Sales",
            "vf_template": "",
            "settings": {"backgroundColor": "#ffffff"},
        },
        data_types=[
            {"name": "category", "type": "text"},
            {"name": "region", "type": "text"},
            {"name": "amount", "type": "numeric"},
        ],
    )
    assert payload["chart_name"] == "relation"
    assert "settings" not in payload
    assert payload["similar_chart"][0] == {"vf.relation": "relation"}
