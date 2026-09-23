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


def test_two_dimensions_two_measures_picks_grid_table():
    picked = _pick_chart_type(
        _md(
            ("region", "text"),
            ("product", "text"),
            ("sales", "numeric"),
            ("cost", "numeric"),
        )
    )
    assert picked == "grid_table"


def test_single_measure_picks_kpi_not_pie():
    assert _pick_chart_type(_md(("amount", "numeric"))) == "kpi"


def test_similar_charts_follow_result_shape():
    names = similar_charts_for_data(
        _md(("category", "text"), ("amount", "numeric")),
        current="bar",
    )
    assert "bar" not in names
    assert "table" not in names
    assert "grid_table" not in names
    assert "line" in names
    assert "pie" in names or "donut" in names
    assert "heatmap" not in names


def test_similar_charts_exclude_heatmap_without_geo():
    names = similar_charts_for_data(
        _md(("region", "text"), ("product", "text"), ("sales", "numeric")),
        current="grid_table",
    )
    assert "heatmap" not in names


def test_similar_charts_include_grid_table_for_multi_dim_aggregate():
    names = similar_charts_for_data(
        _md(("region", "text"), ("product", "text"), ("sales", "numeric")),
        current="relation",
    )
    assert "grid_table" in names


def test_similar_charts_omit_grid_table_for_single_dimension():
    names = similar_charts_for_data(
        _md(("booking_platform", "text")),
        current="wordcloud",
    )
    assert "grid_table" not in names
    assert "table" not in names

def test_lat_lon_viz_model_stays_chart_until_map_requested():
    model, chart, ctx = build_viz_model(
        data_types=_md(("latitude", "numeric"), ("longitude", "numeric")),
        user_query="plot locations",
    )
    assert model.chart.mark != "Maps"
    assert "geographicRoles" not in model.properties.model_dump()
    assert "geographic_roles" not in ctx

    model, chart, ctx = build_viz_model(
        data_types=_md(("latitude", "numeric"), ("longitude", "numeric")),
        user_query="plot locations on a map",
    )
    assert chart == "heatmap"
    assert model.chart.model_dump() == {"viz": "Heatmap", "mark": "Maps"}
    assert model.properties.model_dump().get("geographicRoles") == {
        "latitude": "lat",
        "longitude": "long",
    }
    assert ctx.get("geographic_roles") == {
        "latitude": "lat",
        "longitude": "long",
    }


def test_city_sales_viz_model_uses_map_only_when_asked():
    model, chart, ctx = build_viz_model(
        data_types=_md(("city", "text"), ("sales", "numeric")),
        user_query="sales by city",
    )
    assert chart == "bar"
    assert model.chart.mark != "Maps"
    assert "geographic_roles" not in ctx

    model, chart, ctx = build_viz_model(
        data_types=_md(("city", "text"), ("sales", "numeric")),
        user_query="sales by city on a map",
    )
    assert chart == "heatmap"
    assert model.chart.model_dump() == {"viz": "Heatmap", "mark": "Maps"}
    assert model.properties.model_dump().get("geographicRoles", {}).get("city") == "city"
    assert ctx.get("geographic_roles", {}).get("city") == "city"


def test_auto_bar_swaps_dimension_onto_columns():
    model, chart, _ = build_viz_model(
        data_types=_md(("region", "text"), ("sales", "numeric")),
        user_query="sales by region",
    )
    assert chart == "bar"
    assert model.data.rows == ["sales"]
    assert model.data.columns == ["region"]


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
    # Preferred shelves always applied at the end of viz_model build.
    assert auto.data.rows == ["sales"]
    assert auto.data.columns == ["region"]

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


def test_filtered_select_field_is_dropped_from_viz_and_chart_is_reconsidered(monkeypatch):
    form_data = {
        "columns": [
            {
                "alias": "Booking Platform",
                "column": {"name": "travel_details.booking_platform", "id": "1"},
            },
            {
                "alias": "Travel Cost",
                "aggregate": True,
                "aggregateList": ["db.generic.aggregate.sum"],
                "column": {"name": "travel_details.travel_cost", "id": "2"},
            },
        ],
        "filters": [
            {
                "alias": "Year",
                "column": {"name": "travel_details.travel_date", "id": "3"},
                "values": [2026],
            }
        ],
    }
    monkeypatch.setattr(
        "helicalbi.viz.viz_model_fill._try_sql_to_form_data",
        lambda *args, **kwargs: form_data,
    )
    model, chart, _ = build_viz_model(
        data_types=_md(
            ("Booking Platform", "text"),
            ("Year", "numeric"),
            ("Travel Cost", "numeric"),
        ),
        sql="SELECT booking_platform, year, SUM(travel_cost) FROM t GROUP BY 1, 2",
        md_location="/meta",
        md_file_name="meta.json",
    )
    assert "Year" not in model.data.rows
    assert "Year" not in model.data.columns
    assert model.data.rows == ["Travel Cost"]
    assert model.data.columns == ["Booking Platform"]
    assert chart == "bar"
