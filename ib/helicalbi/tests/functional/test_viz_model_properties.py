"""Functional tests for VizModel property key shape."""
from helicalbi.model.output.viz.VizModel import VizModel, VizProperties
from helicalbi.viz.viz_model_fill import merge_properties_polish, viz_model_to_chart_settings


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


def test_merge_properties_polish_drops_removed_keys_and_renames_labels():
    model = VizModel.model_validate(
        {
            "data": {"rows": ["city"], "columns": ["sales"], "filters": [], "hidden": []},
            "chart": {"viz": "Bar", "mark": "bar"},
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
