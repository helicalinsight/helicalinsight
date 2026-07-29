"""Tests for other-chart LLM JS fill (settings injection does not apply)."""
from __future__ import annotations

import helicalbi.viz._charts as charts_mod
from helicalbi.model.output.viz.VizResponse import OtherChartFillerResponse
from helicalbi.viz._charts import (
    get_chart_config,
    get_chart_definition,
    is_other_chart,
)


def _reload_charts():
    charts_mod._CACHE = None
    charts_mod._ALIAS_INDEX = None
    return charts_mod.get_charts()


def test_other_chart_template_asks_for_js_not_settings():
    _reload_charts()
    other = get_chart_definition("other")
    assert other is not None
    template = other.template
    assert "DrawOther" in template
    assert (
        "ChartSettings injection does not work" in template
        or "Do NOT return ChartSettings" in template
    )
    assert "STARTER TEMPLATE" in template
    assert "function DrawOther" in template
    assert "Return ChartSettings JSON only" not in template


def test_bar_chart_template_still_uses_settings():
    _reload_charts()
    bar = get_chart_definition("bar")
    assert bar is not None
    assert "Return ChartSettings JSON only" in bar.template
    assert "CHART SETTINGS TEMPLATE" in bar.template


def test_is_other_chart_detection():
    _reload_charts()
    assert is_other_chart("other") is True
    assert is_other_chart("custom") is True  # alias
    assert is_other_chart("bar") is False
    assert is_other_chart("not_a_real_chart_xyz") is True


def test_other_chart_filler_response_model():
    m = OtherChartFillerResponse(
        code=(
            "function DrawOther() { const { Column } = components; "
            "return <div><Column {...{data}} /></div>; }"
        )
    )
    assert "DrawOther" in m.code


def test_get_chart_config_other_includes_starter():
    _reload_charts()
    cfg = get_chart_config()
    assert "other" in cfg
    assert "DrawOther" in cfg["other"]
