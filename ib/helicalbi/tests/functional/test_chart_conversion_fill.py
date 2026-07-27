"""Tests for chart skeleton field placeholder filling."""

import base64

import pytest

from helicalbi.viz._charts import get_chart_definition
from helicalbi.viz.chart_conversion import (
    ChartConversionError,
    ExtractedFields,
    convert_chart,
    decode_vf_template,
    extract_fields,
    fill_skeleton,
)


def test_fill_skeleton_quotes_object_keys_with_spaces():
    chart = get_chart_definition("histogram")
    assert chart is not None

    fields = ExtractedFields(
        dimensions=[],
        measures=["Total Travel Cost"],
        series=None,
        title="Total Travel Cost",
        source_chart="column",
        source_family="bar",
    )
    filled = fill_skeleton(chart.code, fields, conversion=chart.conversion)

    assert "Total Travel Cost: '0.00'" not in filled
    assert "'Total Travel Cost': '0.00'" in filled
    assert "binField: 'Total Travel Cost'" in filled
    assert (
        "'Total Travel Cost': { alias: 'Total Travel Cost', "
        "formatter: (v) => formatMeasure(v, 'Total Travel Cost') }"
    ) in filled


def test_fill_skeleton_keeps_valid_identifier_keys_unquoted():
    chart = get_chart_definition("histogram")
    assert chart is not None

    fields = ExtractedFields(
        dimensions=[],
        measures=["travel_cost"],
        series=None,
        title="travel_cost",
        source_chart="column",
        source_family="bar",
    )
    filled = fill_skeleton(chart.code, fields, conversion=chart.conversion)

    assert "travel_cost: '0.00'" in filled
    assert "binField: 'travel_cost'" in filled


def test_convert_chart_returns_template_when_family_requirements_fail():
    """Requirement failures still include the requested chart template for edit/retry."""
    source_js = """
function DrawKPI() {
  const { Card, Statistic } = components;
  <Card loading={false} style={{ width: 200, border: 0 }}>
      <Statistic
        title='Total Cost'
        value={formatKpi(data[0]?.['Total Cost'])}
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
    # Response shape stays the same (no new top-level keys beyond viz fields).
    assert set(err.viz.keys()) == {"vf_template", "chart_name", "vf_title", "vf_reason"}


def test_fill_skeleton_preserves_measure_formats_and_color():
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
    filled = fill_skeleton(chart.code, fields, conversion=chart.conversion)

    assert "'Travel Cost': '#,##0.00'" in filled or "Travel Cost: '#,##0.00'" in filled
    assert "color: ['#5B8FF9', '#61DDAA']" in filled
    assert "angleField: 'Travel Cost'" in filled
    assert "colorField: 'Travel Type'" in filled


TABLE_SOURCE_JS = """
function DrawTable() {
  const { Table } = components;
  // conversionHints: xField: 'Travel Type', yField: 'Travel Cost'
  const columnFormats = {
    'Travel Cost': '#,##0.00'
  };
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

TABLE_METADATA = [
    {"name": "Travel Type", "type": "text"},
    {"name": "Travel Cost", "type": "numeric"},
    {"rows": 10},
]


def test_convert_table_to_circle_packing_uses_conversion_hints():
    encoded = base64.b64encode(TABLE_SOURCE_JS.encode("utf-8")).decode("utf-8")
    result = convert_chart(
        encoded,
        "circle_packing",
        vf_title="Travel Cost by Travel Type",
    )

    assert result["chart_name"] == "circle packing"
    decoded = decode_vf_template(result["vf_template"])
    assert "Travel Type" in decoded
    assert "Travel Cost" in decoded
    assert "CirclePacking" in decoded
    assert "conversionHints" in decoded
    assert "#,##0.00" in decoded


def test_extract_fields_table_reads_conversion_hints_and_memory_formats():
    source_js = """
function DrawTable() {
  const { Table } = components;
  // conversionHints: xField: 'Travel Type', yField: 'Travel Cost'
  const columnFormats = {
    measure_column: '0.00'
  };
  return <div><Table columns={[]} dataSource={data} /></div>;
}
"""
    fields = extract_fields(
        source_js,
        format_strings={"Travel Cost": "#,##0.00"},
    )

    assert fields.source_family == "table"
    assert fields.dimensions == ["Travel Type"]
    assert fields.measures == ["Travel Cost"]
    assert fields.measure_formats.get("Travel Cost") == "#,##0.00"


def test_fill_skeleton_writes_conversion_hints_for_table():
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
    filled = fill_skeleton(chart.code, fields, conversion=chart.conversion)
    assert "conversionHints: xField: 'Travel Type', yField: 'Travel Cost'" in filled
    assert "'Travel Cost': '#,##0.00'" in filled or "Travel Cost: '#,##0.00'" in filled


def test_convert_chart_preserves_formats_and_color_in_vf_template():
    source_js = """
function DrawColumn() {
  const { Column } = components;
  const measureFormats = {
    'Travel Cost': '#,##0.00'
  };
  const formatMeasure = (v, field = 'Travel Cost') => {
    const fmt = measureFormats[field];
    return v;
  };
  const config = {
    data,
    xField: 'Travel Type',
    yField: 'Travel Cost',
    color: ['#5B8FF9', '#61DDAA'],
    meta: {
      'Travel Cost': { alias: 'Travel Cost', formatter: (v) => formatMeasure(v, 'Travel Cost') }
    }
  };
  return <div><Column {...config} /></div>;
}
"""
    encoded = base64.b64encode(source_js.encode("utf-8")).decode("utf-8")
    result = convert_chart(encoded, "bar", vf_title="Travel Cost by Travel Type")

    assert set(result.keys()) == {"vf_template", "chart_name", "vf_title", "vf_reason"}
    assert result["chart_name"] == "bar"
    decoded = decode_vf_template(result["vf_template"])
    assert "Travel Cost" in decoded
    assert "Travel Type" in decoded
    assert "#,##0.00" in decoded
    assert "['#5B8FF9', '#61DDAA']" in decoded
    assert "conversionHints" in decoded
