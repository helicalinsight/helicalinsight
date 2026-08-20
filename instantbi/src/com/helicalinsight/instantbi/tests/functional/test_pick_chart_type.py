"""Deterministic chart picking must not collapse multi-field results to table."""
from __future__ import annotations

import pytest

from helicalbi.viz.viz_model_fill import _pick_chart_type

pytestmark = pytest.mark.functional


def _md(*fields):
    return [{"name": name, "type": dtype} for name, dtype in fields]


def test_one_dimension_one_measure_picks_bar():
    assert _pick_chart_type(_md(("category", "text"), ("amount", "numeric"))) == "bar"


def test_column_chart_request_picks_column():
    assert (
        _pick_chart_type(
            _md(("category", "text"), ("amount", "numeric")),
            user_query="show this as a column chart",
        )
        == "column"
    )


def test_one_dimension_two_measures_picks_radar_not_table():
    picked = _pick_chart_type(
        _md(("region", "text"), ("sales", "numeric"), ("cost", "numeric"))
    )
    assert picked == "radar"
    assert picked != "table"


def test_two_dimensions_one_measure_picks_heatmap_not_table():
    picked = _pick_chart_type(
        _md(("region", "text"), ("product", "text"), ("sales", "numeric"))
    )
    assert picked == "heatmap"
    assert picked != "table"


def test_two_dimensions_two_measures_picks_relation_not_table():
    picked = _pick_chart_type(
        _md(
            ("region", "text"),
            ("product", "text"),
            ("sales", "numeric"),
            ("cost", "numeric"),
        )
    )
    assert picked == "relation"
    assert picked != "table"


def test_three_dimensions_two_measures_picks_relation_not_table():
    picked = _pick_chart_type(
        _md(
            ("region", "text"),
            ("product", "text"),
            ("channel", "text"),
            ("sales", "numeric"),
            ("cost", "numeric"),
        )
    )
    assert picked == "relation"
    assert picked != "table"


def test_ordered_dimension_one_measure_picks_line():
    assert _pick_chart_type(_md(("travel_date", "date"), ("cost", "numeric"))) == "line"


def test_ordered_dimension_two_measures_picks_line_family_not_table():
    picked = _pick_chart_type(
        _md(("travel_date", "date"), ("sales", "numeric"), ("cost", "numeric"))
    )
    assert picked in {"line", "area"}
    assert picked != "table"


def test_explicit_table_request_still_honored():
    assert (
        _pick_chart_type(
            _md(("region", "text"), ("product", "text"), ("sales", "numeric")),
            user_query="show this as a table",
        )
        == "table"
    )
