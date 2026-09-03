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


def test_only_other_chart_template_is_loaded():
    charts = _reload_charts()
    assert set(charts.keys()) == {"other"}
    other = get_chart_definition("other")
    assert other is not None
    assert "DrawOther" in other.template or "STARTER TEMPLATE" in other.template
    assert get_chart_definition("bar") is None


def test_is_other_chart_detection():
    _reload_charts()
    assert is_other_chart("other") is True
    assert is_other_chart("custom") is True
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


def test_get_chart_config_includes_other_only():
    _reload_charts()
    cfg = get_chart_config()
    assert set(cfg.keys()) == {"other"}
    assert "bar" not in cfg
