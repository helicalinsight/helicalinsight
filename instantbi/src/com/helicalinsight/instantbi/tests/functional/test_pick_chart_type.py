"""Deterministic chart picking must not collapse multi-field results to table."""
from __future__ import annotations

import pytest

from helicalbi.viz.viz_model_fill import _pick_chart_type, similar_charts_for_data

pytestmark = pytest.mark.functional


def _md(*fields):
    return [{"name": name, "type": dtype} for name, dtype in fields]


def test_infer_geographic_type_roles():
    from helicalbi.viz.viz_model_fill import infer_geographic_type

    assert infer_geographic_type("latitude") == "lat"
    assert infer_geographic_type("longitude") == "long"
    assert infer_geographic_type("Client City") == "city"
    assert infer_geographic_type("state_province") == "state"
    assert infer_geographic_type("country_name") == "country"


def test_apply_geographic_types_to_form_data():
    from helicalbi.viz.viz_model_fill import apply_geographic_types_to_form_data

    form_data = {
        "columns": [
            {"alias": "city", "column": {"name": "t.city", "id": "1"}},
            {"alias": "sales", "column": {"name": "t.sales", "id": "2"}, "aggregate": True},
        ]
    }
    stamped = apply_geographic_types_to_form_data(form_data, {"city": "city"})
    assert stamped["columns"][0]["geographicType"] == "city"
    assert "geographicType" not in stamped["columns"][1]


def test_column_chart_request_picks_column():
    assert (
        _pick_chart_type(
            _md(("category", "text"), ("amount", "numeric")),
            user_query="show this as a column chart",
        )
        == "column"
    )


def test_one_dimension_two_measures_picks_bar_not_table():
    picked = _pick_chart_type(
        _md(("region", "text"), ("sales", "numeric"), ("cost", "numeric"))
    )
    assert picked == "bar"
    assert picked != "table"


def test_two_dimensions_one_measure_picks_grid_table_not_heatmap():
    picked = _pick_chart_type(
        _md(("region", "text"), ("product", "text"), ("sales", "numeric"))
    )
    assert picked == "grid_table"
    assert picked != "heatmap"
    assert picked != "table"


def test_two_dimensions_no_measures_picks_table():
    picked = _pick_chart_type(
        _md(("region", "text"), ("product", "text"), ("channel", "text"))
    )
    assert picked == "table"
    assert picked != "grid_table"


def test_one_dimension_no_measures_picks_wordcloud_not_grid_table():
    """DISTINCT / single categorical column is not a crosstab."""
    picked = _pick_chart_type(_md(("booking_platform", "text")))
    assert picked == "wordcloud"
    assert picked != "grid_table"


def test_city_and_measure_does_not_auto_pick_map():
    picked = _pick_chart_type(
        _md(("city", "text"), ("sales", "numeric"))
    )
    assert picked == "bar"
    assert picked != "heatmap"


def test_latitude_longitude_does_not_auto_pick_map():
    picked = _pick_chart_type(
        _md(("latitude", "numeric"), ("longitude", "numeric"))
    )
    assert picked != "point"
    assert picked != "heatmap"


def test_lat_lon_and_measure_does_not_auto_pick_map():
    picked = _pick_chart_type(
        _md(("latitude", "numeric"), ("longitude", "numeric"), ("sales", "numeric"))
    )
    assert picked != "heatmap"
    assert picked != "point"


def test_explicit_heatmap_request_still_honored():
    assert (
        _pick_chart_type(
            _md(("region", "text"), ("product", "text"), ("sales", "numeric")),
            user_query="show this as a heatmap",
        )
        == "heatmap"
    )


def test_two_dimensions_two_measures_picks_grid_table():
    """Multi-dim multi-measure prefers Grid Table over Relation."""
    picked = _pick_chart_type(
        _md(
            ("region", "text"),
            ("product", "text"),
            ("sales", "numeric"),
            ("cost", "numeric"),
        )
    )
    assert picked == "grid_table"


def test_three_dimensions_two_measures_picks_grid_table():
    picked = _pick_chart_type(
        _md(
            ("region", "text"),
            ("product", "text"),
            ("channel", "text"),
            ("sales", "numeric"),
            ("cost", "numeric"),
        )
    )
    assert picked == "grid_table"


def test_three_dimensions_one_measure_picks_grid_table_not_relation():
    picked = _pick_chart_type(
        _md(
            ("booking_platform", "text"),
            ("travel_type", "text"),
            ("travel_medium", "text"),
            ("travel_cost", "numeric"),
        )
    )
    assert picked == "grid_table"
    assert picked != "relation"


def test_ordered_dimension_one_measure_picks_line():
    assert _pick_chart_type(_md(("travel_date", "date"), ("cost", "numeric"))) == "line"


def test_ordered_dimension_two_measures_picks_line_family_not_table():
    picked = _pick_chart_type(
        _md(("travel_date", "date"), ("sales", "numeric"), ("cost", "numeric"))
    )
    assert picked in {"line", "area"}
    assert picked != "table"


_QUARTER_FORM_DATA = {
    "columns": [
        {
            "column": {
                "name": "sampletraveldata.public.travel_details.travel_date",
                "id": "1065",
            },
            "alias": "Quarter",
            "order": "asc",
            "databaseFunction": 'QUARTER("travel_details"."travel_date")',
            "usedColumns": [
                "sampletraveldata.public.travel_details.travel_date",
            ],
        },
        {
            "column": {
                "name": "sampletraveldata.public.travel_details.travel_id",
                "id": "1064",
            },
            "alias": "Number of Travels",
            "aggregate": True,
            "aggregateList": ["db.generic.aggregate.count"],
        },
        {
            "column": {
                "name": "sampletraveldata.public.travel_details.travel_cost",
                "id": "1072",
            },
            "alias": "Travel Cost",
            "aggregate": True,
            "aggregateList": ["db.generic.aggregate.sum"],
        },
    ]
}


def test_quarter_extract_all_numeric_with_shelf_counts_picks_line():
    """EXTRACT(QUARTER) is JDBC-numeric; form_data shelf counts skip type-based ordered."""
    picked = _pick_chart_type(
        _md(
            ("Quarter", "numeric"),
            ("Number of Travels", "numeric"),
            ("Travel Cost", "numeric"),
        ),
        dimension_count=1,
        measure_count=2,
        form_data=_QUARTER_FORM_DATA,
        field_names=["Quarter", "Number of Travels", "Travel Cost"],
    )
    assert picked == "line"


def test_quarter_alias_without_date_type_picks_line():
    picked = _pick_chart_type(
        _md(("Quarter", "numeric"), ("amount", "numeric")),
        dimension_count=1,
        measure_count=1,
        field_names=["Quarter", "amount"],
    )
    assert picked == "line"


def test_year_extract_function_picks_line_even_with_opaque_alias():
    picked = _pick_chart_type(
        _md(("Bucket", "numeric"), ("amount", "numeric")),
        dimension_count=1,
        measure_count=1,
        form_data={
            "columns": [
                {
                    "column": {"name": "facts.bucket_key", "id": "1"},
                    "alias": "Bucket",
                    "databaseFunction": 'YEAR("travel_details"."travel_date")',
                },
                {
                    "column": {"name": "t.amount", "id": "2"},
                    "alias": "amount",
                    "aggregate": True,
                    "aggregateList": ["db.generic.aggregate.sum"],
                },
            ]
        },
    )
    assert picked == "line"


def test_quarter_extract_similar_charts_prefer_trend_over_bar():
    names = similar_charts_for_data(
        _md(
            ("Quarter", "numeric"),
            ("Number of Travels", "numeric"),
            ("Travel Cost", "numeric"),
        ),
        current="line",
        dimension_count=1,
        measure_count=2,
        form_data=_QUARTER_FORM_DATA,
    )
    assert "area" in names
    assert "bar" in names
    assert names.index("area") < names.index("bar")


def test_explicit_table_request_still_honored():
    assert (
        _pick_chart_type(
            _md(("region", "text"), ("product", "text"), ("sales", "numeric")),
            user_query="show this as a table",
        )
        == "table"
    )


def test_grid_chart_request_picks_grid_table():
    assert (
        _pick_chart_type(
            _md(
                ("booking_platform", "text"),
                ("travel_type", "text"),
                ("travel_medium", "text"),
                ("travel_cost", "numeric"),
            ),
            user_query=(
                "show me travel cost, booking platform by travel type "
                "and travel medium in grid chart"
            ),
        )
        == "grid_table"
    )
