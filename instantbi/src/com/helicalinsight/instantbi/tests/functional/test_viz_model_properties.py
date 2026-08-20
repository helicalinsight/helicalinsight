"""Functional tests for VizModel property key shape."""
from helicalbi.model.output.viz.VizModel import VizModel, VizProperties
from helicalbi.viz.viz_model_fill import (
    MARK_VIZ_CATALOG,
    _chart_viz_and_mark,
    merge_properties_polish,
    viz_model_to_chart_settings,
)


def test_viz_properties_use_labelx_labely_and_drop_removed_keys():
    props = VizProperties.model_validate(
        {
            "labelsX": "Travel Type",
            "labelsY": "Travel Cost",
            "title": "Travel Cost by Type",
            "colorGradient": ["#111", "#222"],
            "theme": "sales-bold",
            "formatter": {"Travel Cost": "return value;"},
            "color": "#2F6FED",
        }
    )
    dumped = props.model_dump()
    assert dumped["labelX"] == "Travel Type"
    assert dumped["labelY"] == "Travel Cost"
    assert "labelsX" not in dumped
    assert "labelsY" not in dumped
    assert "colorGradient" not in dumped
    assert "theme" not in dumped
    assert "formatter" not in dumped
    assert dumped["color"] == "#2F6FED"


def test_chart_viz_and_mark_uses_hi_mark_parent_and_child_viz():
    assert _chart_viz_and_mark("bar").model_dump() == {"viz": "Bar", "mark": "Chart"}
    assert _chart_viz_and_mark("column").model_dump() == {"viz": "Bar", "mark": "Chart"}
    assert _chart_viz_and_mark("pie").model_dump() == {"viz": "Arc", "mark": "Chart"}
    assert _chart_viz_and_mark("arc").model_dump() == {"viz": "Arc", "mark": "Chart"}
    assert _chart_viz_and_mark("gauge").model_dump() == {"viz": "Arc", "mark": "Chart"}
    assert _chart_viz_and_mark("wordcloud").model_dump() == {
        "viz": "Text",
        "mark": "Chart",
    }
    assert _chart_viz_and_mark("text").model_dump() == {"viz": "Text", "mark": "Chart"}
    assert _chart_viz_and_mark("donut").model_dump() == {
        "viz": "Doughnut",
        "mark": "Chart",
    }
    assert _chart_viz_and_mark("doughnut").model_dump() == {
        "viz": "Doughnut",
        "mark": "Chart",
    }
    assert _chart_viz_and_mark("heatmap").model_dump() == {
        "viz": "Heatmap",
        "mark": "Maps",
    }
    assert _chart_viz_and_mark("kpi").model_dump() == {"viz": "Bar", "mark": "Card"}
    assert _chart_viz_and_mark("table").model_dump() == {"viz": "", "mark": "Table"}
    assert _chart_viz_and_mark("unknown_chart").model_dump() == {
        "viz": "",
        "mark": "VF",
    }

    by_name = {entry["name"]: entry["values"] for entry in MARK_VIZ_CATALOG}
    assert by_name["Chart"] == [
        "Arc",
        "Area",
        "Bar",
        "Calendar",
        "Doughnut",
        "Line",
        "Point",
        "Progress",
        "Radar",
        "Relation",
        "Text",
        "Waterfall",
    ]
    assert by_name["Table"] == []
    assert by_name["Grid Table"] == []
    assert by_name["VF"] == []


def test_merge_properties_polish_drops_removed_keys_and_renames_labels():
    model = VizModel.model_validate(
        {
            "data": {"rows": ["city"], "columns": ["sales"], "filters": []},
            "chart": {"viz": "Bar", "mark": "Chart"},
            "properties": {
                "labelX": "city",
                "labelY": "sales",
                "title": "Sales by City",
            },
        }
    )
    polish = {
        "labelX": "City",
        "labelY": "Sales",
        "color": "#123456",
        "theme": "retail-warm",
        "colorGradient": ["#aaa", "#bbb"],
        "formatter": {"sales": "return value;"},
        "background": "#ffffff",
    }
    merged = merge_properties_polish(model, polish)
    props = merged.properties.model_dump()
    assert props["labelX"] == "City"
    assert props["labelY"] == "Sales"
    assert props["color"] == "#123456"
    assert props["background"] == "#ffffff"
    assert "theme" not in props
    assert "colorGradient" not in props
    assert "formatter" not in props

    settings = viz_model_to_chart_settings(merged)
    assert settings.labelsX == "City"
    assert settings.labelsY == "Sales"
