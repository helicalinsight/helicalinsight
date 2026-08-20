"""Tests for chart settings injection (${setting}) and conversion fill."""

import base64
import json

import pytest

from helicalbi.viz._charts import get_chart_definition
from helicalbi.viz.chart_conversion import (
    ChartConversionError,
    ExtractedFields,
    apply_chart_settings,
    convert_chart,
    decode_vf_template,
    extract_fields,
    fields_to_settings,
)


def _filled_setting(code: str) -> dict:
    """Parse the injected ``const setting = {...};`` object from filled code."""
    marker = "const setting ="
    start = code.index(marker) + len(marker)
    open_idx = code.index("{", start)
    depth = 0
    for i in range(open_idx, len(code)):
        ch = code[i]
        if ch == "{":
            depth += 1
        elif ch == "}":
            depth -= 1
            if depth == 0:
                return json.loads(code[open_idx : i + 1])
    raise AssertionError("setting object not found")


def test_apply_settings_injects_measure_with_spaces():
    chart = get_chart_definition("bar")
    assert chart is not None

    fields = ExtractedFields(
        dimensions=["Travel Type"],
        measures=["Total Travel Cost"],
        series=None,
        title="Total Travel Cost",
        source_chart="bar",
        source_family="bar",
        measure_formats={"Total Travel Cost": "0.00"},
    )
    filled = apply_chart_settings(fields_to_settings(fields), chart_def=chart)
    setting = _filled_setting(filled)

    assert setting["measures"] == ["Total Travel Cost"]
    # Temporary: formats/color are blanked on inject until format injection is stable.
    assert "measure_formats" not in setting or setting.get("measure_formats") in ({}, None)
    assert "xField: setting.measures[0]" in filled
    assert "yField: setting.dimensions.names[0]" in filled
    assert "${setting}" not in filled


def test_column_is_bar_with_swapped_measure_and_dimension():
    """Column uses the Bar component with axes swapped vs horizontal bar."""
    chart = get_chart_definition("column")
    assert chart is not None

    fields = ExtractedFields(
        dimensions=["Travel Type"],
        measures=["Travel Cost"],
        series=None,
        title="Travel Cost by Type",
        source_chart="column",
        source_family="cartesian",
    )
    filled = apply_chart_settings(fields_to_settings(fields), chart_def=chart)

    assert "const { Column } = components" in filled
    assert "xField: setting.dimensions.names[0]" in filled
    assert "yField: setting.measures[0]" in filled
    assert "xField: setting.measures[0]" not in filled
    assert chart.conversion is not None
    assert chart.conversion.component == "Column"



def test_apply_settings_injects_valid_identifier_measure():
    chart = get_chart_definition("bar")
    assert chart is not None

    fields = ExtractedFields(
        dimensions=["travel_type"],
        measures=["travel_cost"],
        series=None,
        title="travel_cost",
        source_chart="bar",
        source_family="bar",
        measure_formats={"travel_cost": "0.00"},
    )
    filled = apply_chart_settings(fields_to_settings(fields), chart_def=chart)
    setting = _filled_setting(filled)

    assert setting["measures"] == ["travel_cost"]
    assert "measure_formats" not in setting or setting.get("measure_formats") in ({}, None)
    assert "xField: setting.measures[0]" in filled
    assert "yField: setting.dimensions.names[0]" in filled


def test_convert_chart_returns_template_when_family_requirements_fail():
    """Requirement failures still include the requested chart template for edit/retry."""
    source_js = """
function DrawKPI() {
  const { Card, Statistic } = components;
  const setting = {
    "dimensions": {},
    "measures": ["Total Cost"],
    "title": "Total Cost"
  };
  return (
    <Card loading={false} style={{ width: 200, border: 0 }}>
      <Statistic
        title={setting.title}
        value={data[0]?.[setting.measures[0]]}
        valueStyle={{ color: '#999' }}
      />
    </Card>
  );
}
"""
    encoded = base64.b64encode(source_js.encode("utf-8")).decode("utf-8")

    with pytest.raises(ChartConversionError) as caught:
        convert_chart(encoded, "pie", vf_title="Total Cost")

    err = caught.value
    assert "needs at least 1 dimension" in str(err)
    assert err.viz is not None
    assert err.viz["chart_name"] == "pie"
    assert err.viz["vf_title"] == "Total Cost"
    assert err.viz["vf_template"]
    decoded = decode_vf_template(err.viz["vf_template"])
    assert "Pie" in decoded or "angleField" in decoded
    assert {"vf_template", "chart_name", "vf_title", "vf_reason"}.issubset(err.viz.keys())


def test_apply_settings_blanks_measure_formats_and_color():
    """Temporary: inject blanks formats/color until format injection is stable."""
    chart = get_chart_definition("pie")
    assert chart is not None

    fields = ExtractedFields(
        dimensions=["Travel Type"],
        measures=["Travel Cost"],
        series=None,
        title="Travel Cost by Travel Type",
        source_chart="column",
        source_family="cartesian",
        measure_formats={"Travel Cost": "#,##0.00"},
        color="['#5B8FF9', '#61DDAA']",
    )
    filled = apply_chart_settings(fields_to_settings(fields), chart_def=chart)
    setting = _filled_setting(filled)

    assert setting["dimensions"]["names"] == ["Travel Type"]
    assert setting["measures"] == ["Travel Cost"]
    assert "measure_formats" not in setting or setting.get("measure_formats") in ({}, None)
    assert "color" not in setting
    assert "angleField: setting.measures[0]" in filled
    assert "colorField: setting.dimensions.names[0]" in filled


TABLE_SOURCE_JS = """
function DrawTable() {
  const { Table } = components;
  const setting = {
    "dimensions": { "name": "Travel Type" },
    "measures": ["Travel Cost"],
    "measure_formats": { "Travel Cost": "#,##0.00" }
  };
  const columnFormats = setting.measure_formats || {};
  const formatCell = (key, value) => {
    const fmt = columnFormats[key];
    if (!fmt || value == null || value === '') return value;
    const num = Number(value);
    if (isNaN(num)) return value;
    return num.toFixed(2);
  };
  const columns =
    data.length > 0
      ? Object.keys(data[0]).map((key) => ({
          title: key.replace(/_/g, ' '),
          dataIndex: key,
          key,
          render: (value) => formatCell(key, value)
        }))
      : [];
  return (
    <div>
      <Table
        columns={columns}
        dataSource={data}
        rowKey={(record, index) => index}
        pagination={true}
      />
    </div>
  );
}
"""


def test_convert_table_to_relation_uses_settings_object():
    encoded = base64.b64encode(TABLE_SOURCE_JS.encode("utf-8")).decode("utf-8")
    result = convert_chart(
        encoded,
        "relation",
        vf_title="Travel Cost by Travel Type",
    )

    assert result["chart_name"] == "relation"
    decoded = decode_vf_template(result["vf_template"])
    assert "Travel Type" in decoded
    assert "Travel Cost" in decoded
    assert "Treemap" in decoded
    assert "conversionHints" in decoded


def test_extract_fields_reads_injected_settings_object():
    source_js = """
function DrawTable() {
  const { Table } = components;
  const setting = {
    "dimensions": { "name": "Travel Type" },
    "measures": ["Travel Cost"],
    "measure_formats": { "Travel Cost": "#,##0.00" }
  };
  return <div><Table columns={[]} dataSource={data} /></div>;
}
"""
    fields = extract_fields(source_js)

    assert fields.dimensions == ["Travel Type"]
    assert fields.measures == ["Travel Cost"]
    assert fields.measure_formats.get("Travel Cost") == "#,##0.00"


def test_apply_settings_writes_conversion_hints_for_table():
    chart = get_chart_definition("table")
    assert chart is not None
    fields = ExtractedFields(
        dimensions=["Travel Type"],
        measures=["Travel Cost"],
        series=None,
        title="Travel Cost by Travel Type",
        source_chart="column",
        source_family="cartesian",
        measure_formats={"Travel Cost": "#,##0.00"},
    )
    filled = apply_chart_settings(fields_to_settings(fields), chart_def=chart)
    setting = _filled_setting(filled)
    assert "conversionHints: xField: 'Travel Type', yField: 'Travel Cost'" in filled
    assert "measure_formats" not in setting or setting.get("measure_formats") in ({}, None)


def test_convert_chart_keeps_bindings_and_blanks_formats_color():
    source_js = """
function DrawColumn() {
  const { Column } = components;
  const setting = {
    "dimensions": { "name": "Travel Type" },
    "measures": ["Travel Cost"],
    "labelsX": "Travel Type",
    "labelsY": "Travel Cost",
    "color": ["#5B8FF9", "#61DDAA"],
    "measure_formats": { "Travel Cost": "#,##0.00" }
  };
  const config = {
    data,
    xField: setting.dimensions.name,
    yField: setting.measures[0],
    color: setting.color
  };
  return <div><Column {...config} /></div>;
}
"""
    encoded = base64.b64encode(source_js.encode("utf-8")).decode("utf-8")
    result = convert_chart(encoded, "bar", vf_title="Travel Cost by Travel Type")

    assert set(result.keys()) == {
        "vf_template",
        "chart_name",
        "vf_title",
        "vf_reason",
        "similar_chart",
    }
    assert result["chart_name"] == "bar"
    similar_keys = {
        next(iter(entry))
        for entry in result["similar_chart"]
        if isinstance(entry, dict) and entry
    }
    assert "vf.bar" in similar_keys
    assert "vf.column" in similar_keys
    assert "vf.donut" in similar_keys
    assert "vf.pie" in similar_keys
    assert "vf.point" in similar_keys
    decoded = decode_vf_template(result["vf_template"])
    setting = _filled_setting(decoded)
    assert setting["measures"] == ["Travel Cost"]
    assert setting["dimensions"]["names"] == ["Travel Type"]
    assert "measure_formats" not in setting or setting.get("measure_formats") in ({}, None)
    assert "color" not in setting
    assert "conversionHints" in decoded


def _encode_settings_chart(chart_name: str, settings: dict) -> str:
    """Build a minimal ${setting}-style source chart and base64-encode it."""
    chart = get_chart_definition(chart_name)
    assert chart is not None
    from helicalbi.model.output.viz.ChartSettings import ChartSettings

    filled = apply_chart_settings(
        ChartSettings.model_validate(settings),
        chart_def=chart,
    )
    return base64.b64encode(filled.encode("utf-8")).decode("utf-8")


def test_convert_backfills_second_dimension_from_metadata():
    """1-dim source + 2-dim result metadata can convert to heatmap."""
    encoded = _encode_settings_chart(
        "bar",
        {
            "dimensions": {"name": "travel_type"},
            "measures": ["travel_cost"],
        },
    )
    data_types = [
        {"name": "travel_type", "type": "text"},
        {"name": "booking_platform", "type": "text"},
        {"name": "travel_cost", "type": "numeric"},
    ]
    result = convert_chart(
        encoded,
        "heatmap",
        data_types=data_types,
    )
    setting = _filled_setting(decode_vf_template(result["vf_template"]))
    assert setting["dimensions"]["names"][:2] == ["travel_type", "booking_platform"]
    assert setting["measures"][0] == "travel_cost"


def test_convert_backfills_second_measure_from_metadata():
    """1-measure source + 2-measure result metadata can convert to radar."""
    encoded = _encode_settings_chart(
        "bar",
        {
            "dimensions": {"name": "travel_type"},
            "measures": ["travel_cost"],
        },
    )
    data_types = [
        {"name": "travel_type", "type": "text"},
        {"name": "travel_cost", "type": "numeric"},
        {"name": "employee_count", "type": "numeric"},
    ]
    result = convert_chart(
        encoded,
        "radar",
        data_types=data_types,
    )
    setting = _filled_setting(decode_vf_template(result["vf_template"]))
    assert setting["dimensions"]["names"][0] == "travel_type"
    assert setting["measures"][:2] == ["travel_cost", "employee_count"]


def test_extract_fields_appends_unused_metadata_columns():
    """Bound settings stay first; unused result dims/measures are appended."""
    encoded = _encode_settings_chart(
        "bar",
        {
            "dimensions": {"name": "travel_type"},
            "measures": ["travel_cost"],
        },
    )
    source_js = decode_vf_template(encoded)
    fields = extract_fields(
        source_js,
        data_types=[
            {"name": "travel_type", "type": "text"},
            {"name": "booking_platform", "type": "text"},
            {"name": "travel_cost", "type": "numeric"},
            {"name": "employee_count", "type": "numeric"},
        ],
    )
    assert fields.dimensions == ["travel_type", "booking_platform"]
    assert fields.measures == ["travel_cost", "employee_count"]


def test_table_to_wordcloud_dim_only_synthesizes_weight():
    """Dim-only table → wordcloud must bind wordField and synthesize weights."""
    encoded = _encode_settings_chart(
        "table",
        {
            "dimensions": {"name": "travel_medium"},
            "measures": [],
            "title": "All travel medium",
        },
    )
    result = convert_chart(encoded, "wordcloud", vf_title="All travel medium")
    decoded = decode_vf_template(result["vf_template"])
    setting = _filled_setting(decoded)
    assert setting["dimensions"]["names"] == ["travel_medium"]
    assert setting.get("measures") in ([], None) or setting["measures"] == []
    assert "wordField" in decoded
    assert "__weight" in decoded
    assert "counts[key]" in decoded


def test_table_empty_bindings_enriched_from_data_types_for_wordcloud():
    """Empty table settings still convert when result metadata lists the dim."""
    encoded = _encode_settings_chart(
        "table",
        {
            "dimensions": {"names": []},
            "measures": [],
        },
    )
    data_types = [
        {"1": {"name": "travel_medium", "type": "text"}},
    ]
    result = convert_chart(
        encoded,
        "wordcloud",
        data_types=data_types,
        vf_title="All travel medium",
    )
    setting = _filled_setting(decode_vf_template(result["vf_template"]))
    assert setting["dimensions"]["names"] == ["travel_medium"]


def test_table_to_kpi_binds_numeric_column_from_data_types():
    """Empty table measures still convert to KPI using a numeric metadata column."""
    from helicalbi.model.output.viz.ChartSettings import ChartSettings

    chart = get_chart_definition("table")
    assert chart is not None
    filled = apply_chart_settings(
        ChartSettings.model_validate(
            {
                "dimensions": {"names": []},
                "measures": [],
            }
        ),
        chart_def=chart,
    )
    encoded = base64.b64encode(filled.encode("utf-8")).decode("utf-8")
    data_types = [
        {"name": "Travel Type", "type": "text"},
        {"name": "Travel Cost", "type": "numeric"},
    ]
    result = convert_chart(encoded, "kpi", data_types=data_types)
    setting = _filled_setting(decode_vf_template(result["vf_template"]))
    assert setting["measures"] == ["Travel Cost"]


def test_table_to_kpi_reclassifies_numeric_dimension():
    """Numeric column listed only under dimensions is moved to KPI measure."""
    from helicalbi.model.output.viz.ChartSettings import ChartSettings

    chart = get_chart_definition("table")
    assert chart is not None
    filled = apply_chart_settings(
        ChartSettings.model_validate(
            {
                "dimensions": {"names": ["Travel Type", "Travel Cost"]},
                "measures": [],
            }
        ),
        chart_def=chart,
    )
    encoded = base64.b64encode(filled.encode("utf-8")).decode("utf-8")
    data_types = [
        {"name": "Travel Type", "type": "text"},
        {"name": "Travel Cost", "type": "numeric"},
    ]
    result = convert_chart(encoded, "kpi", data_types=data_types)
    setting = _filled_setting(decode_vf_template(result["vf_template"]))
    assert setting["measures"] == ["Travel Cost"]
    assert "Travel Cost" not in setting["dimensions"]["names"]


def test_table_to_kpi_uses_measure_formats_when_no_metadata():
    """Formatted value columns can seed a numeric measure without data_types."""
    from helicalbi.viz.chart_conversion import ensure_kpi_measure_from_numeric

    fields = ExtractedFields(
        dimensions=["Travel Type"],
        measures=[],
        measure_formats={"Travel Cost": "#,##0.00"},
        source_family="table",
    )
    ensured = ensure_kpi_measure_from_numeric(fields, data_types=None)
    assert ensured.measures == ["Travel Cost"]
