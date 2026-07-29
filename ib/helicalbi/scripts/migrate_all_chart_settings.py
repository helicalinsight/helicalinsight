"""Migrate all chart JSON skeletons to ``const setting = ${setting}`` + setting.* refs."""
from __future__ import annotations

import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT))

from helicalbi.viz._chart_settings import synthesize_settings_template  # noqa: E402

CHARTS = ROOT / "helicalbi" / "viz" / "charts"

FORMAT_HELPER = [
    "  const measureFormats = setting.measure_formats || {};",
    "  const measureField = (setting.measures && setting.measures[0]) || '';",
    "  const formatMeasure = (v, field = measureField) => {",
    "    const fmt = measureFormats[field];",
    "    const raw = (v != null && typeof v === 'object') ? (v[field] ?? v.value ?? v) : v;",
    "    if (!fmt || raw == null || raw === '') return raw;",
    "    const num = typeof raw === 'number' ? raw : Number(raw);",
    "    if (typeof num !== 'number' || !Number.isFinite(num)) return raw;",
    "    if (fmt === '0.00' || fmt === '0.0#' || /^0\\.0+$/.test(fmt)) {",
    "      return num.toFixed((fmt.split('.')[1] || '').length);",
    "    }",
    "    if (fmt.includes('#') || fmt.includes(',') || fmt.includes('$')) {",
    "      const decimals = (fmt.split('.')[1] || '').replace(/[^0]/g, '').length;",
    "      let out = num.toLocaleString(undefined, {",
    "        minimumFractionDigits: decimals,",
    "        maximumFractionDigits: decimals || 0",
    "      });",
    "      if (fmt.trim().startsWith('$')) out = '$' + out;",
    "      return out;",
    "    }",
    "    return raw;",
    "  };",
]


def _fn_name(stem: str, component: str) -> str:
    # Prefer Draw + PascalCase from stem
    parts = [p.capitalize() for p in stem.split("_") if p]
    return "Draw" + "".join(parts) if parts else f"Draw{component}"


def _cartesian(stem: str, component: str, *, extra_config: list[str] | None = None) -> list[str]:
    fn = _fn_name(stem, component)
    lines = [
        f"function {fn}() {{",
        f"  const {{ {component} }} = components;",
        "  const setting = ${setting};",
        *FORMAT_HELPER,
        "  const config = {",
        "    data,",
        "    xField: setting.dimensions.name,",
        "    yField: setting.measures[0],",
        "    ...(setting.color ? { color: setting.color } : {}),",
        "    ...(setting.series ? { seriesField: setting.series } : {}),",
        "    xAxis: {",
        "      title: {",
        "        text: setting.labelsX",
        "      }",
        "    },",
        "    yAxis: {",
        "      title: {",
        "        text: setting.labelsY",
        "      },",
        "      label: {",
        "        formatter: (v) => formatMeasure(v, measureField)",
        "      }",
        "    },",
    ]
    if extra_config:
        lines.extend(extra_config)
    lines.extend(
        [
            "    meta: {",
            "      [measureField]: { alias: setting.labelsY || measureField, formatter: (v) => formatMeasure(v, measureField) }",
            "    }",
            "  };",
            f"  return <div><{component} {{...config}} /></div>;",
            "}",
        ]
    )
    return lines


def _bar(stem: str) -> list[str]:
    return [
        "function DrawBar() {",
        "  const { Bar } = components;",
        "  const setting = ${setting};",
        *FORMAT_HELPER,
        "  const config = {",
        "    data,",
        "    xField: setting.measures[0],",
        "    yField: setting.dimensions.name,",
        "    ...(setting.color ? { color: setting.color } : {}),",
        "    title: {",
        "      visible: !!setting.title,",
        "      text: setting.title,",
        "      align: 'center'",
        "    },",
        "    xAxis: {",
        "      title: { text: setting.labelsX },",
        "      label: { formatter: (v) => formatMeasure(v, measureField) }",
        "    },",
        "    yAxis: {",
        "      title: { text: setting.labelsY }",
        "    },",
        "    ...(setting.series ? { seriesField: setting.series, legend: { position: 'top-left' } } : {}),",
        "    meta: {",
        "      [measureField]: { alias: setting.labelsX || measureField, formatter: (v) => formatMeasure(v, measureField) }",
        "    }",
        "  };",
        "  return <div><Bar {...config} /></div>;",
        "}",
    ]


def _pie(stem: str, *, donut: bool = False) -> list[str]:
    fn = "DrawDonut" if donut else "DrawPie"
    lines = [
        f"function {fn}() {{",
        "  const { Pie } = components;",
        "  const setting = ${setting};",
        *FORMAT_HELPER,
        "  const config = {",
        "    appendPadding: 10,",
        "    data,",
        "    angleField: setting.measures[0],",
        "    colorField: setting.dimensions.name,",
        "    radius: 0.8,",
    ]
    if donut:
        lines.extend(
            [
                "    innerRadius: 0.6,",
                "    statistic: {",
                "      title: {",
                "        style: { color: '#000', fontSize: 14 },",
                "        formatter: () => setting.title || ''",
                "      },",
                "      content: {",
                "        style: { fontSize: 14 },",
                "        content: String(",
                "          data.reduce((sum, row) => sum + (row[measureField] || 0), 0)",
                "        )",
                "      }",
                "    },",
            ]
        )
    lines.extend(
        [
            "    ...(setting.color ? { color: setting.color } : {}),",
            "    label: {",
            "      type: 'outer',",
            "      content: '{name}: {percentage}'",
            "    },",
            "    interactions: [{ type: 'element-active' }],",
            "    meta: {",
            "      [measureField]: { alias: setting.title || measureField, formatter: (v) => formatMeasure(v, measureField) }",
            "    }",
            "  };",
            "  return <div><Pie {...config} /></div>;",
            "}",
        ]
    )
    return lines


def _dual_axes(stem: str, geometry: list[dict]) -> list[str]:
    fn = _fn_name(stem, "DualAxes")
    geo_lines = ["    geometryOptions: ["]
    for g in geometry:
        parts = [f"geometry: '{g['geometry']}'"]
        if g.get("series"):
            parts.append("...(setting.series ? { seriesField: setting.series } : {})")
        if g.get("isStack"):
            parts.append("isStack: true")
        if g.get("isGroup"):
            parts.append("isGroup: true")
        geo_lines.append(f"      {{ {', '.join(parts)} }},")
    geo_lines.append("    ],")
    return [
        f"function {fn}() {{",
        "  const { DualAxes } = components;",
        "  const setting = ${setting};",
        *FORMAT_HELPER,
        "  const config = {",
        "    data: [data, data],",
        "    xField: setting.dimensions.name,",
        "    yField: [setting.measures[0], setting.measures[1]],",
        "    ...(setting.color ? { color: setting.color } : {}),",
        *geo_lines,
        "    meta: {",
        "      [setting.measures[0]]: { alias: setting.labelsY || setting.measures[0], formatter: (v) => formatMeasure(v, setting.measures[0]) },",
        "      [setting.measures[1]]: { alias: setting.labelsZ || setting.measures[1], formatter: (v) => formatMeasure(v, setting.measures[1]) }",
        "    }",
        "  };",
        "  return <div><DualAxes {...config} /></div>;",
        "}",
    ]


def _tiny(stem: str, component: str) -> list[str]:
    fn = _fn_name(stem, component)
    return [
        f"function {fn}() {{",
        f"  const {{ {component} }} = components;",
        "  const setting = ${setting};",
        *FORMAT_HELPER,
        "  const config = {",
        "    data,",
        "    xField: setting.dimensions.name,",
        "    yField: setting.measures[0],",
        "    ...(setting.color ? { color: setting.color } : {}),",
        "    meta: {",
        "      [measureField]: { alias: setting.title || measureField, formatter: (v) => formatMeasure(v, measureField) }",
        "    }",
        "  };",
        f"  return <div><{component} {{...config}} /></div>;",
        "}",
    ]


def _heatmap() -> list[str]:
    return [
        "function DrawHeatmap() {",
        "  const { Heatmap } = components;",
        "  const setting = ${setting};",
        *FORMAT_HELPER,
        "  const xDim = (setting.dimensions.names && setting.dimensions.names[0]) || setting.dimensions.name;",
        "  const yDim = (setting.dimensions.names && setting.dimensions.names[1]) || setting.dimensions.name;",
        "  const config = {",
        "    data,",
        "    xField: xDim,",
        "    yField: yDim,",
        "    colorField: setting.measures[0],",
        "    color: setting.color || ['#B8E1FF', '#9AC5FF', '#7DAAFF', '#5B8FF9', '#3D76DD', '#085EC0', '#0047A5', '#00318A', '#001D70'],",
        "    meta: {",
        "      [xDim]: { type: 'cat' },",
        "      [yDim]: { type: 'cat' },",
        "      [measureField]: { alias: setting.labelsY || measureField, formatter: (v) => formatMeasure(v, measureField) }",
        "    },",
        "    legend: { position: 'bottom' },",
        "    interactions: [{ type: 'element-active' }]",
        "  };",
        "  return <div><Heatmap {...config} /></div>;",
        "}",
    ]


def _calendar() -> list[str]:
    # calendar often uses heatmap-like encoding
    return [
        "function DrawCalendar() {",
        "  const { Heatmap } = components;",
        "  const setting = ${setting};",
        *FORMAT_HELPER,
        "  const xDim = (setting.dimensions.names && setting.dimensions.names[0]) || setting.dimensions.name;",
        "  const yDim = (setting.dimensions.names && setting.dimensions.names[1]) || setting.dimensions.name;",
        "  const config = {",
        "    data,",
        "    xField: xDim,",
        "    yField: yDim,",
        "    colorField: setting.measures[0],",
        "    color: setting.color || ['#BAE7FF', '#1890FF', '#0050B3'],",
        "    meta: {",
        "      [xDim]: { type: 'cat' },",
        "      [yDim]: { type: 'cat' }",
        "    },",
        "    legend: { position: 'bottom' }",
        "  };",
        "  return <div><Heatmap {...config} /></div>;",
        "}",
    ]


def _bubble() -> list[str]:
    return [
        "function DrawBubbleChart() {",
        "  const { Scatter } = components;",
        "  const setting = ${setting};",
        *FORMAT_HELPER,
        "  const config = {",
        "    data,",
        "    xField: setting.measures[0],",
        "    yField: setting.measures[1],",
        "    sizeField: setting.measures[2] || setting.measures[1],",
        "    size: [4, 30],",
        "    shape: 'circle',",
        "    ...(setting.dimensions.name ? { colorField: setting.dimensions.name } : {}),",
        "    ...(setting.color ? { color: setting.color } : {}),",
        "    xAxis: { title: { text: setting.labelsX } },",
        "    yAxis: { title: { text: setting.labelsY } },",
        "    interactions: [{ type: 'element-active' }],",
        "    meta: {",
        "      [setting.measures[0]]: { alias: setting.labelsX || setting.measures[0], formatter: (v) => formatMeasure(v, setting.measures[0]) },",
        "      [setting.measures[1]]: { alias: setting.labelsY || setting.measures[1], formatter: (v) => formatMeasure(v, setting.measures[1]) }",
        "    }",
        "  };",
        "  return <div><Scatter {...config} /></div>;",
        "}",
    ]


def _kpi() -> list[str]:
    return [
        "function DrawKPI() {",
        "  const { Card, Statistic } = components;",
        "  const setting = ${setting};",
        "  const measureFormats = setting.measure_formats || {};",
        "  const measureField = setting.measures[0];",
        "  const formatKpi = (v) => {",
        "    const fmt = measureFormats[measureField];",
        "    if (!fmt || v == null || v === '') return v;",
        "    const num = typeof v === 'number' ? v : Number(v);",
        "    if (typeof num !== 'number' || !Number.isFinite(num)) return v;",
        "    if (fmt === '0.00' || fmt === '0.0#' || /^0\\.0+$/.test(fmt)) {",
        "      return num.toFixed((fmt.split('.')[1] || '').length);",
        "    }",
        "    return num;",
        "  };",
        "  return (",
        "    <Card loading={false} style={{ width: 200, border: 0 }}>",
        "      <Statistic",
        "        title={setting.title || measureField}",
        "        value={formatKpi(data[0]?.[measureField])}",
        "        valueStyle={{ color: setting.color || '#999' }}",
        "      />",
        "    </Card>",
        "  );",
        "}",
    ]


def _gauge() -> list[str]:
    return [
        "function DrawGauge() {",
        "  const { Gauge } = components;",
        "  const setting = ${setting};",
        *FORMAT_HELPER,
        "  const targetField = setting.measures[1];",
        "  const total = data.reduce((sum, row) => sum + (row[measureField] || 0), 0);",
        "  const target = targetField ? (data[0]?.[targetField] ?? total) : total;",
        "  const percent = target > 0 ? total / target : 0;",
        "  const config = {",
        "    percent,",
        "    range: {",
        "      color: setting.color || ['#5B8FF9', '#E8EDF3']",
        "    },",
        "    indicator: {",
        "      pointer: { style: { stroke: '#D0D0D0' } },",
        "      pin: { style: { stroke: '#D0D0D0' } }",
        "    },",
        "    meta: {",
        "      [measureField]: { alias: setting.title || measureField, formatter: (v) => formatMeasure(v, measureField) }",
        "    }",
        "  };",
        "  return <div><Gauge {...config} /></div>;",
        "}",
    ]


def _progress() -> list[str]:
    return [
        "function DrawProgress() {",
        "  const { RingProgress } = components;",
        "  const setting = ${setting};",
        *FORMAT_HELPER,
        "  const targetField = setting.measures[1];",
        "  const total = data.reduce((sum, row) => sum + (row[measureField] || 0), 0);",
        "  const target = targetField ? (data[0]?.[targetField] ?? total) : total;",
        "  const percent = target > 0 ? Math.min(total / target, 1) : 0;",
        "  const config = {",
        "    percent,",
        "    color: setting.color || ['#5B8FF9', '#E8EDF3'],",
        "    statistic: {",
        "      content: {",
        "        style: { fontSize: 14 },",
        "        content: `${((typeof percent === 'number' && Number.isFinite(percent) ? percent : Number(percent)) * 100).toFixed(2)}%`",
        "      }",
        "    }",
        "  };",
        "  return <div><RingProgress {...config} /></div>;",
        "}",
    ]


def _grid_table(stem: str, *, multi_dim: bool = False) -> list[str]:
    fn = "DrawPivotTable" if stem == "pivot_table" else "DrawGridTable"
    dim_expr = (
        "(setting.dimensions.names && setting.dimensions.names.length "
        "? setting.dimensions.names "
        ": (setting.dimensions.name ? [setting.dimensions.name] : []))"
    )
    return [
        f"function {fn}() {{",
        "  const { GridTable } = components;",
        "  const { buildGridTableReport } = helperFunctions;",
        "  const setting = ${setting};",
        f"  const dimensions = {dim_expr};",
        "  const measures = setting.measures || [];",
        "  const minReport = buildGridTableReport({",
        "    dimensions,",
        "    measures,",
        "    formatStrings: setting.measure_formats || {},",
        "    showTotals: true",
        "  });",
        "  return (",
        "    <div>",
        "      <GridTable",
        "        {...report}",
        "        data={data}",
        "        fields={minReport.fields}",
        "        metadata={minReport.metadata}",
        "        marksList={minReport.marksList}",
        "        report={minReport}",
        "      />",
        "    </div>",
        "  );",
        "}",
    ]


def _hierarchy(stem: str, component: str) -> list[str]:
    fn = _fn_name(stem, component)
    return [
        f"function {fn}() {{",
        f"  const {{ {component} }} = components;",
        "  const setting = ${setting};",
        *FORMAT_HELPER,
        "  const dimensionField = setting.dimensions.name || (setting.dimensions.names && setting.dimensions.names[0]);",
        "  const chartData = {",
        "    name: 'root',",
        "    children: data.map((item) => ({",
        "      name: item[dimensionField],",
        "      value: item[measureField],",
        "      ...item",
        "    }))",
        "  };",
        "  const config = {",
        "    data: chartData,",
        "    colorField: 'name',",
        "    ...(setting.color ? { color: setting.color } : {}),",
        "    label: {",
        "      fields: [dimensionField]",
        "    },",
        "    meta: {",
        "      [measureField]: { alias: setting.title || measureField, formatter: (v) => formatMeasure(v, measureField) }",
        "    }",
        "  };",
        f"  return <div><{component} {{...config}} /></div>;",
        "}",
    ]


def _table(stem: str, component: str = "Table") -> list[str]:
    fn = _fn_name(stem, component)
    return [
        f"function {fn}() {{",
        f"  const {{ {component} }} = components;",
        "  const setting = ${setting};",
        "  const columnFormats = setting.measure_formats || {};",
        "  const formatCell = (key, value) => {",
        "    const fmt = columnFormats[key];",
        "    if (!fmt || value == null || value === '') return value;",
        "    const num = Number(value);",
        "    if (isNaN(num)) return value;",
        "    if (fmt === '0.00' || fmt === '0.0#' || /^0\\.0+$/.test(fmt)) {",
        "      return num.toFixed((fmt.split('.')[1] || '').length);",
        "    }",
        "    if (fmt.includes('#') || fmt.includes(',')) {",
        "      const decimals = (fmt.split('.')[1] || '').replace(/[^0]/g, '').length;",
        "      return num.toLocaleString(undefined, {",
        "        minimumFractionDigits: decimals,",
        "        maximumFractionDigits: decimals || 0",
        "      });",
        "    }",
        "    return value;",
        "  };",
        "  const columns =",
        "    data.length > 0",
        "      ? Object.keys(data[0]).map((key) => ({",
        "          title: key.replace(/_/g, ' '),",
        "          dataIndex: key,",
        "          key,",
        "          render: (value) => formatCell(key, value)",
        "        }))",
        "      : [];",
        "  return (",
        "    <div>",
        f"      <{component}",
        "        columns={columns}",
        "        dataSource={data}",
        "        rowKey={(record, index) => index}",
        "        pagination={true}",
        "      />",
        "    </div>",
        "  );",
        "}",
    ]


def _histogram() -> list[str]:
    return [
        "function DrawHistogram() {",
        "  const { Histogram } = components;",
        "  const setting = ${setting};",
        *FORMAT_HELPER,
        "  const config = {",
        "    data,",
        "    binField: setting.measures[0],",
        "    ...(setting.color ? { color: setting.color } : {}),",
        "    meta: {",
        "      [measureField]: { alias: setting.labelsY || measureField, formatter: (v) => formatMeasure(v, measureField) }",
        "    }",
        "  };",
        "  return <div><Histogram {...config} /></div>;",
        "}",
    ]


def _wordcloud() -> list[str]:
    return [
        "function DrawWordcloud() {",
        "  const { WordCloud } = components;",
        "  const setting = ${setting};",
        "  const config = {",
        "    data,",
        "    wordField: setting.dimensions.name,",
        "    weightField: setting.measures[0],",
        "    ...(setting.color ? { color: setting.color } : {}),",
        "  };",
        "  return <div><WordCloud {...config} /></div>;",
        "}",
    ]


def _funnel() -> list[str]:
    return [
        "function DrawFunnelChart() {",
        "  const { Funnel } = components;",
        "  const setting = ${setting};",
        *FORMAT_HELPER,
        "  const config = {",
        "    data,",
        "    xField: setting.dimensions.name,",
        "    yField: setting.measures[0],",
        "    ...(setting.color ? { color: setting.color } : {}),",
        "    meta: {",
        "      [measureField]: { alias: setting.labelsY || measureField, formatter: (v) => formatMeasure(v, measureField) }",
        "    }",
        "  };",
        "  return <div><Funnel {...config} /></div>;",
        "}",
    ]


def _rose() -> list[str]:
    return [
        "function DrawRoseChart() {",
        "  const { Rose } = components;",
        "  const setting = ${setting};",
        *FORMAT_HELPER,
        "  const config = {",
        "    data,",
        "    xField: setting.dimensions.name,",
        "    yField: setting.measures[0],",
        "    seriesField: setting.dimensions.name,",
        "    ...(setting.color ? { color: setting.color } : {}),",
        "    meta: {",
        "      [measureField]: { alias: setting.labelsY || measureField, formatter: (v) => formatMeasure(v, measureField) }",
        "    }",
        "  };",
        "  return <div><Rose {...config} /></div>;",
        "}",
    ]


def _other() -> list[str]:
    return [
        "function DrawOther() {",
        "  const { Column } = components;",
        "  const setting = ${setting};",
        *FORMAT_HELPER,
        "  const ChartComponent = Column;",
        "  const config = {",
        "    data,",
        "    xField: setting.dimensions.name,",
        "    yField: setting.measures[0],",
        "    ...(setting.color ? { color: setting.color } : {}),",
        "    xAxis: { title: { text: setting.labelsX } },",
        "    yAxis: {",
        "      title: { text: setting.labelsY },",
        "      label: { formatter: (v) => formatMeasure(v, measureField) }",
        "    },",
        "    meta: {",
        "      [measureField]: { alias: setting.labelsY || measureField, formatter: (v) => formatMeasure(v, measureField) }",
        "    }",
        "  };",
        "  return <div><ChartComponent {...config} /></div>;",
        "}",
    ]


def _scatter() -> list[str]:
    return _cartesian(
        "scatter",
        "Scatter",
        extra_config=[
            "    shape: 'circle',",
            "    interactions: [{ type: 'element-active' }],",
        ],
    )


def _radar() -> list[str]:
    return [
        "function DrawRadar() {",
        "  const { Radar } = components;",
        "  const setting = ${setting};",
        *FORMAT_HELPER,
        "  const config = {",
        "    data,",
        "    xField: setting.dimensions.name,",
        "    yField: setting.measures[0],",
        "    ...(setting.series ? { seriesField: setting.series } : {}),",
        "    ...(setting.color ? { color: setting.color } : {}),",
        "    meta: {",
        "      [measureField]: { alias: setting.labelsY || measureField, formatter: (v) => formatMeasure(v, measureField) }",
        "    }",
        "  };",
        "  return <div><Radar {...config} /></div>;",
        "}",
    ]


def build_code(stem: str, payload: dict) -> list[str]:
    conversion = payload.get("conversion") or {}
    family = str(conversion.get("family") or "").lower()
    component = str(conversion.get("component") or "").strip()

    if stem == "bar":
        return _bar(stem)
    if stem == "pie":
        return _pie(stem, donut=False)
    if stem == "donut":
        return _pie(stem, donut=True)
    if stem == "heatmap":
        return _heatmap()
    if stem == "calendar":
        return _calendar()
    if stem == "bubble_chart":
        return _bubble()
    if stem == "kpi":
        return _kpi()
    if stem == "gauge":
        return _gauge()
    if stem == "progress":
        return _progress()
    if stem == "histogram":
        return _histogram()
    if stem == "wordcloud":
        return _wordcloud()
    if stem == "funnel_chart":
        return _funnel()
    if stem == "rose_chart":
        return _rose()
    if stem == "scatter":
        return _scatter()
    if stem == "radar":
        return _radar()
    if stem == "other":
        return _other()
    if stem in {"table"}:
        return _table(stem, component or "Table")
    if stem in {"grid_table", "pivot_table"}:
        return _grid_table(stem, multi_dim=stem == "pivot_table")
    if family == "tiny":
        return _tiny(stem, component or "TinyLine")
    if family == "hierarchy":
        return _hierarchy(stem, component or "Treemap")
    if family == "dual_axes":
        if stem == "dual_line":
            geo = [{"geometry": "line", "series": True}, {"geometry": "line", "series": True}]
        elif stem == "column_line":
            geo = [{"geometry": "column"}, {"geometry": "line", "series": True}]
        elif stem == "grouped_column_line":
            geo = [{"geometry": "column", "isGroup": True, "series": True}, {"geometry": "line", "series": True}]
        elif stem == "stacked_column_line":
            geo = [{"geometry": "column", "isStack": True, "series": True}, {"geometry": "line", "series": True}]
        elif stem == "stacked_and_grouped_column_line":
            geo = [
                {"geometry": "column", "isStack": True, "isGroup": True, "series": True},
                {"geometry": "line", "series": True},
            ]
        else:
            geo = [{"geometry": "column"}, {"geometry": "line"}]
        return _dual_axes(stem, geo)
    if family == "bar":
        return _bar(stem)
    if family == "pie":
        return _pie(stem, donut=stem == "donut")
    if stem == "area":
        return _cartesian(
            stem,
            "Area",
            extra_config=[
                "    areaStyle: {",
                "      fill: 'l(270) 0:#ffffff 0.5:#69b2f8 1:#0050b3'",
                "    },",
            ],
        )
    if stem == "line":
        return _cartesian(
            stem,
            "Line",
            extra_config=[
                "    point: {",
                "      size: 5,",
                "      shape: 'diamond'",
                "    },",
            ],
        )
    if stem == "column":
        return _cartesian(
            stem,
            "Column",
            extra_config=[
                "    label: {",
                "      position: 'middle',",
                "      style: { fill: '#FFFFFF', opacity: 0.6 },",
                "      formatter: (v) => formatMeasure(v, measureField)",
                "    },",
            ],
        )
    if stem == "waterfall":
        return _cartesian(stem, "Waterfall")
    if stem == "point":
        return _cartesian(
            stem,
            "Scatter",
            extra_config=[
                "    shape: 'circle',",
            ],
        )
    # default cartesian-like
    return _cartesian(stem, component or "Column")


def main() -> None:
    for path in sorted(CHARTS.glob("*.json")):
        payload = json.loads(path.read_text(encoding="utf-8"))
        settings = synthesize_settings_template(payload)
        # Preserve richer authored settings for already-migrated charts when present
        # and shaped like the new template (has labelsX or dimensions.name prompt).
        existing = payload.get("settings")
        if isinstance(existing, dict) and (
            "labelsX" in existing
            or (isinstance(existing.get("dimensions"), dict) and "name" in existing["dimensions"])
            or (isinstance(existing.get("dimensions"), dict) and "names" in existing["dimensions"])
        ):
            # Merge synthesized keys that are missing.
            for key, value in settings.items():
                existing.setdefault(key, value)
            settings = existing

        code = build_code(path.stem, payload)
        new_payload = {}
        for key, value in payload.items():
            if key in {"settings", "code"}:
                continue
            new_payload[key] = value
            if key == "conversion":
                new_payload["settings"] = settings
        if "settings" not in new_payload:
            new_payload["settings"] = settings
        new_payload["code"] = code
        # keep instructions if present
        if "instructions" in payload and "instructions" not in new_payload:
            # instructions may appear before code in original; ensure order
            pass
        # Rebuild with stable key order
        ordered = {}
        for key in (
            "dims_min",
            "dims_max",
            "measures_min",
            "measures_max",
            "instruction",
            "aliases",
            "requires_ordered",
            "base",
            "conversion",
            "settings",
            "instructions",
            "code",
        ):
            if key in payload or key in new_payload:
                ordered[key] = new_payload.get(key, payload.get(key))
        for key, value in payload.items():
            if key not in ordered and key not in {"settings", "code"}:
                ordered[key] = value
        ordered["settings"] = settings
        ordered["code"] = code

        path.write_text(json.dumps(ordered, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
        print(f"migrated {path.name}")


if __name__ == "__main__":
    main()
