"""Shelf layout and chart suggestion from actual result shape."""
from __future__ import annotations

import pytest

from helicalbi.viz._shelf_layout import arrange_shelves
from helicalbi.viz.viz_model_fill import (
    _pick_chart_type,
    build_viz_model,
    is_viz_update_intent,
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


def test_two_measures_build_grid_chart_dual_axis():
    model, chart, _ = build_viz_model(
        data_types=_md(("region", "text"), ("sales", "numeric"), ("cost", "numeric")),
        user_query="sales and cost by region",
    )
    assert chart == "bar"
    assert model.chart.dualaxis is True
    assert model.chart.mark == "Grid Chart"
    assert model.chart.viz == "Bar"
    series = {item["field"]: item for item in model.chart.model_dump()["measures"]}
    assert series["sales"]["viz"] == "Bar"
    assert series["cost"]["viz"] == "Line"
    assert not series["sales"]["color"]
    assert not series["cost"]["color"]


def test_line_with_two_measures_uses_bar_on_the_second_axis():
    model, chart, _ = build_viz_model(
        data_types=_md(("region", "text"), ("sales", "numeric"), ("cost", "numeric")),
        user_query="show a line chart of sales and cost by region",
    )
    assert chart == "line"
    assert model.chart.dualaxis is True
    assert model.chart.mark == "Grid Chart"
    series = {item["field"]: item for item in model.chart.model_dump()["measures"]}
    assert series["sales"]["viz"] == "Line"
    assert series["cost"]["viz"] == "Bar"


def test_two_measures_use_cube_column_color_and_leave_others_empty():
    model, _, _ = build_viz_model(
        data_types=_md(("region", "text"), ("sales", "numeric"), ("cost", "numeric")),
        user_query="sales and cost by region",
        cube_metadata=[
            {
                "database_table": "travel",
                "measures": [
                    {
                        "alias_name": "sales",
                        "measure_name": "sales",
                        "color": "#112233",
                    },
                    {
                        "alias_name": "cost",
                        "measure_name": "cost",
                        "properties": {"color": "#AABBCC"},
                    },
                ],
            }
        ],
    )
    series = {item["field"]: item for item in model.chart.model_dump()["measures"]}
    assert series["sales"]["color"] == "#112233"
    assert series["cost"]["color"] == "#AABBCC"


def test_pie_with_two_measures_stays_single_axis():
    model, chart, _ = build_viz_model(
        data_types=_md(("region", "text"), ("sales", "numeric"), ("cost", "numeric")),
        user_query="show a pie chart of sales and cost",
    )
    assert chart == "pie"
    assert model.chart.dualaxis is False
    assert model.chart.measures == []


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
    assert model.chart.model_dump() == {
        "viz": "Heatmap",
        "mark": "Maps",
        "dualaxis": False,
        "measures": [],
    }
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
    assert model.chart.dualaxis is False
    assert model.chart.mark != "Maps"
    assert "geographic_roles" not in ctx

    model, chart, ctx = build_viz_model(
        data_types=_md(("city", "text"), ("sales", "numeric")),
        user_query="sales by city on a map",
    )
    assert chart == "heatmap"
    assert model.chart.model_dump() == {
        "viz": "Heatmap",
        "mark": "Maps",
        "dualaxis": False,
        "measures": [],
    }
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
