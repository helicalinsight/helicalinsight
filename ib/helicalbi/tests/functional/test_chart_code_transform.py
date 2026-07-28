"""Tests for ChartCodeTransform unsafe toFixed rewriting."""

from helicalbi.core.vizflow.util.ChartCodeTransform import transform_chart_code


def test_rewrites_unsafe_tofixed_formatters():
    src = """
function DrawColumn() {
  const { Column } = components;
  const config = {
    label: { formatter: (v) => v.toFixed(2) },
    yAxis: { label: { formatter: (text) => text.toFixed(2) } },
    meta: { travel_cost: { formatter: (v) => v.toFixed(2) } }
  };
  return <div><Column {...config} /></div>;
}
"""
    out = transform_chart_code(src)
    assert "v.toFixed" not in out
    assert "text.toFixed" not in out
    assert "Number.isFinite" in out
    assert "typeof" in out


def test_preserves_already_safe_number_tofixed():
    src = """
function DrawColumn() {
  const { Column } = components;
  const config = {
    meta: { travel_cost: { formatter: (v) => Number(v).toFixed(2) } }
  };
  return <div><Column {...config} /></div>;
}
"""
    out = transform_chart_code(src)
    assert "Number(v).toFixed(2)" in out
