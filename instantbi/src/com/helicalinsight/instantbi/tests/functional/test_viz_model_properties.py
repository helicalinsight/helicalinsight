"""Functional tests for VizModel property key shape."""
from helicalbi.model.output.viz.VizModel import VizModel, VizProperties
from helicalbi.viz.viz_model_fill import (
    MARK_VIZ_CATALOG,
    _build_properties_formatting,
    _chart_viz_and_mark,
    build_viz_model,
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


def test_viz_data_drops_filters():
    model = VizModel.model_validate(
        {
            "data": {
                "rows": ["Travel Type"],
                "columns": ["Travel Count"],
                "filters": [
                    {
                        "name": "Travel Type",
                        "value": "International",
                        "condition": "EQ",
                    },
                    {
                        "name": 'COUNT("travel_details"."travel_id")',
                        "value": [20, 50],
                        "condition": "BETWEEN",
                    },
                ],
            },
            "chart": {"viz": "Bar", "mark": "Chart"},
            "properties": {"labelX": "Travel Type"},
        }
    )
    dumped = model.model_dump()
    assert dumped["data"]["rows"] == ["Travel Type"]
    assert dumped["data"]["columns"] == ["Travel Count"]
    assert "filters" not in dumped["data"]
    assert not hasattr(model.data, "filters")


def _chart(viz, mark):
    return {"viz": viz, "mark": mark, "dualaxis": False, "measures": []}


def test_chart_viz_and_mark_uses_hi_mark_parent_and_child_viz():
    assert _chart_viz_and_mark("bar").model_dump() == _chart("Bar", "Chart")
    assert _chart_viz_and_mark("column").model_dump() == _chart("Bar", "Chart")
    assert _chart_viz_and_mark("pie").model_dump() == _chart("Arc", "Chart")
    assert _chart_viz_and_mark("arc").model_dump() == _chart("Arc", "Chart")
    assert _chart_viz_and_mark("gauge").model_dump() == _chart("Arc", "Chart")
    assert _chart_viz_and_mark("wordcloud").model_dump() == _chart("Word cloud", "Chart")
    assert _chart_viz_and_mark("text").model_dump() == _chart("Word cloud", "Chart")
    assert _chart_viz_and_mark("donut").model_dump() == _chart("Doughnut", "Chart")
    assert _chart_viz_and_mark("doughnut").model_dump() == _chart("Doughnut", "Chart")
    assert _chart_viz_and_mark("heatmap").model_dump() == _chart("Heatmap", "Maps")
    assert _chart_viz_and_mark("point", geo=True).model_dump() == _chart("Point", "Maps")
    assert _chart_viz_and_mark("line", geo=True).model_dump() == _chart("Line", "Maps")
    assert _chart_viz_and_mark("point").model_dump() == _chart("Point", "Chart")
    assert _chart_viz_and_mark("line").model_dump() == _chart("Line", "Chart")
    assert _chart_viz_and_mark("kpi").model_dump() == _chart("KPI", "Card")
    assert _chart_viz_and_mark("table").model_dump() == _chart("Table", "Table")
    assert _chart_viz_and_mark("grid_table").model_dump() == _chart("Grid table", "Grid Table")
    assert _chart_viz_and_mark("unknown_chart").model_dump() == _chart("", "VF")

    by_name = {entry["name"]: entry["values"] for entry in MARK_VIZ_CATALOG}
    assert by_name["Chart"] == [
        "Bar",
        "Line",
        "Word cloud",
        "Arc",
        "Area",
        "Doughnut",
        "Point",
        "Waterfall",
        "Radar",
        "Progress",
        "Relation",
        "Calendar",
    ]
    assert by_name["Maps"] == ["Line", "Point", "Heatmap"]
    assert by_name["Table"] == ["Table"]
    assert by_name["Grid Table"] == ["Grid table"]
    assert by_name["Grid Chart"] == ["Bar", "Line", "Area", "Point", "Tick"]
    assert by_name["Card"] == ["KPI", "Trend"]
    assert by_name["VF"] == []


def test_merge_properties_polish_drops_removed_keys_and_renames_labels():
    model = VizModel.model_validate(
        {
            "data": {"rows": ["city"], "columns": ["sales"]},
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


def test_build_properties_formatting_maps_data_model_columns():
    formatting = _build_properties_formatting(
        ["Travel Type", "Travel Cost"],
        format_strings={"travel_cost": "$#,##0.00"},
        cube_metadata=[
            {
                "database_table": "travel_details",
                "columns": [
                    {
                        "alias_name": "Travel Type",
                        "dimension_name": "travel_type",
                        "format_string": "General",
                    }
                ],
                "measures": [
                    {
                        "alias_name": "Travel Cost",
                        "measure_name": "travel_cost",
                        "format_string": "$#,##0.00",
                    }
                ],
            }
        ],
    )
    assert formatting == {
        "Travel Type": "General",
        "Travel Cost": "$#,##0.00",
    }


def test_build_viz_model_populates_properties_formatting(monkeypatch):
    form_data = {
        "columns": [
            {
                "alias": "Travel Type",
                "fieldType": "dimension",
                "column": {"name": "travel_details.travel_type"},
            },
            {
                "alias": "Travel Cost",
                "fieldType": "measure",
                "aggregate": "sum",
                "column": {"name": "travel_details.travel_cost"},
            },
        ]
    }
    monkeypatch.setattr(
        "helicalbi.viz.viz_model_fill._try_sql_to_form_data",
        lambda *args, **kwargs: form_data,
    )

    metadata = [
        {"name": "Travel Type", "type": "text"},
        {"name": "Travel Cost", "type": "numeric"},
    ]
    model, _, _ = build_viz_model(
        data_types=metadata,
        sql="SELECT travel_type, SUM(travel_cost) FROM travel_details GROUP BY 1",
        md_location="/meta",
        md_file_name="model.json",
        format_strings={"travel_cost": "$#,##0.00"},
    )

    assert model.properties.formatting == {
        "Travel Type": "",
        "Travel Cost": "$#,##0.00",
    }
    assert "filters" not in model.data.model_dump()

    settings = viz_model_to_chart_settings(model, data_types=metadata)
    assert settings.measure_formats == {"Travel Cost": "$#,##0.00"}


def test_dual_axis_measure_keeps_feasible_grid_chart_marks():
    model = VizModel.model_validate(
        {
            "data": {"rows": ["sales", "cost"], "columns": ["region"]},
            "chart": {
                "viz": "Bar",
                "mark": "Grid Chart",
                "dualaxis": True,
                "measures": [
                    {"field": "sales", "viz": "bar", "color": "#5B8FF9"},
                    {"field": "cost", "viz": "arc", "color": " #E8684A "},
                    {"field": "qty", "viz": "tick", "color": "#5AD8A6"},
                ],
            },
        }
    )
    dumped = model.chart.model_dump()
    assert dumped["dualaxis"] is True
    assert dumped["mark"] == "Grid Chart"
    assert [item["viz"] for item in dumped["measures"]] == ["Bar", "Line", "Tick"]
    assert dumped["measures"][1]["color"] == "#E8684A"


def test_merge_properties_polish_fills_recommended_dual_axis_colors():
    model = VizModel.model_validate(
        {
            "data": {"rows": ["sales", "cost"], "columns": ["region"]},
            "chart": {
                "viz": "Bar",
                "mark": "Grid Chart",
                "dualaxis": True,
                "measures": [
                    {"field": "sales", "viz": "Bar", "color": None},
                    {"field": "cost", "viz": "Line", "color": None},
                ],
            },
            "properties": {"title": "Sales and Cost"},
        }
    )
    merged = merge_properties_polish(
        model,
        {
            "measure_colors": {"Travel Cost": "#ff0000", "cost": "#00aa00"},
            "color": "#123456",
        },
    )
    series = {item["field"]: item for item in merged.chart.model_dump()["measures"]}
    assert series["sales"]["color"] is None
    assert series["cost"]["color"] == "#00aa00"
    assert merged.properties.color == "#123456"
    assert "measure_colors" not in merged.properties.model_dump()
