"""Shelf layout and chart suggestion from actual result shape."""
from __future__ import annotations

import pytest

from helicalbi.viz._shelf_layout import arrange_shelves
from helicalbi.viz.viz_model_fill import (
    _pick_chart_type,
    build_viz_model,
    is_viz_update_intent,
    similar_charts_for_data,
)

pytestmark = pytest.mark.functional


def _md(*fields):
    return [{"name": name, "type": dtype} for name, dtype in fields]


def test_viz_update_intent_detects_updt_viz_and_viz_update():
    assert is_viz_update_intent(action="updt_viz") is True
    assert is_viz_update_intent(action="updt_both") is True
    assert is_viz_update_intent(intent="VIZ_UPDATE") is True
    assert is_viz_update_intent(intent="VISUALIZATION_UPDATE") is True
    assert is_viz_update_intent(action="none") is False
    assert is_viz_update_intent(action="updt_sql") is False


def test_one_dimension_two_measures_picks_bar():
    picked = _pick_chart_type(
        _md(("region", "text"), ("sales", "numeric"), ("cost", "numeric"))
    )
    assert picked == "bar"


def test_two_dimensions_two_measures_picks_relation():
    picked = _pick_chart_type(
        _md(
            ("region", "text"),
            ("product", "text"),
            ("sales", "numeric"),
            ("cost", "numeric"),
        )
    )
    assert picked == "relation"


def test_single_measure_picks_kpi_not_pie():
    assert _pick_chart_type(_md(("amount", "numeric"))) == "kpi"


def test_similar_charts_follow_result_shape():
    names = similar_charts_for_data(
        _md(("category", "text"), ("amount", "numeric")),
        current="bar",
    )
    assert "bar" not in names
    assert "table" not in names
    assert "line" in names
    assert "pie" in names or "donut" in names


def test_auto_bar_keeps_dimension_on_rows():
    model, chart, _ = build_viz_model(
        data_types=_md(("region", "text"), ("sales", "numeric")),
        user_query="sales by region",
    )
    assert chart == "bar"
    assert model.data.rows == ["region"]
    assert model.data.columns == ["sales"]


def test_named_waterfall_without_viz_update_still_uses_valid_shelves():
    """Waterfall cannot keep InstantBI default; validity fix is not a convert."""
    model, chart, _ = build_viz_model(
        data_types=_md(("region", "text"), ("sales", "numeric")),
        user_query="show this as a waterfall chart",
        viz_update=False,
    )
    assert chart == "waterfall"
    assert model.data.rows == ["sales"]
    assert model.data.columns == ["region"]


def test_convert_to_waterfall_swaps_to_dim_in_columns():
    model, chart, _ = build_viz_model(
        data_types=_md(("region", "text"), ("sales", "numeric")),
        user_query="convert this to a waterfall chart",
        viz_update=True,
    )
    assert chart == "waterfall"
    assert model.data.columns == ["region"]
    assert model.data.rows == ["sales"]


def test_convert_to_arc_swaps_dimension_onto_columns():
    model, chart, _ = build_viz_model(
        data_types=_md(("region", "text"), ("sales", "numeric")),
        user_query="show as a pie chart",
        viz_update=True,
    )
    assert chart == "pie"
    assert model.data.columns == ["region"]
    assert model.data.rows == ["sales"]


def test_convert_to_bar_swaps_only_when_viz_update():
    md = _md(("region", "text"), ("sales", "numeric"))
    auto, _, _ = build_viz_model(
        data_types=md, user_query="sales by region", viz_update=False
    )
    assert auto.data.rows == ["region"]
    assert auto.data.columns == ["sales"]

    converted, chart, _ = build_viz_model(
        data_types=md,
        user_query="convert to bar",
        viz_hint="bar",
        viz_update=True,
    )
    assert chart == "bar"
    assert converted.data.rows == ["sales"]
    assert converted.data.columns == ["region"]


def test_leftover_hint_ignored_unless_viz_update():
    md = _md(("region", "text"), ("sales", "numeric"))
    assert _pick_chart_type(md, viz_hint="pie", viz_update=False) == "bar"
    assert (
        _pick_chart_type(
            md, viz_hint="bar", user_query="convert to pie", viz_update=True
        )
        == "pie"
    )


def test_arrange_shelves_swaps_when_forced():
    rows, columns, swapped = arrange_shelves(
        "waterfall",
        ["region"],
        ["sales"],
        dimensions=["region"],
        measures=["sales"],
        force_preferred=True,
    )
    assert swapped
    assert columns == ["region"]
    assert rows == ["sales"]
