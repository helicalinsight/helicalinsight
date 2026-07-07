TEMPLATE_INSTRUCTIONS = """
Use this template exactly and only replace placeholder values.
Rules:
1) Do not add imports, wrappers, markdown, or explanation.
2) Keep `data` as-is; never inline sample data.
3) Return valid JavaScript/JSX with proper braces and closing tags.
4) Keep function name and component usage consistent with template.
"""


chart_config = {
    "bar": f"""
{TEMPLATE_INSTRUCTIONS}
function DrawBar() {{
  const {{ Column }} = components;
  const config = {{
    data,
    xField: "x_field",
    yField: "y_field",
    title: {{
      visible: true,
      text: "title_text",
      align: "center"
    }},
    xAxis: {{
      title: {{
        text: "Label for X axis"
      }}
    }},
    yAxis: {{
      title: {{
        text: "Label for Y axis"
      }}
    }},
    seriesField: "series_field",
    legend: {{
      position: "top-left"
    }}
  }};
  return <div><Column {{...config}} /></div>;
}}
""",
    "pie": f"""
{TEMPLATE_INSTRUCTIONS}
function DrawPie() {{
  const {{ Pie }} = components;
  const config = {{
    appendPadding: 10,
    data,
    angleField: "measure_column",
    colorField: "dimension_column",
    radius: 1,
    label: {{
      type: "outer",
      content: "{{name}}: {{percentage}}"
    }},
    interactions: [{{ type: "element-active" }}]
  }};
  return <div><Pie {{...config}} /></div>;
}}
""",
    "column": f"""
{TEMPLATE_INSTRUCTIONS}
function DrawColumn() {{
  const {{ Column }} = components;
  const config = {{
    data,
    xField: "x_field",
    yField: "y_field",
    label: {{
      position: "middle",
      style: {{
        fill: "#FFFFFF",
        opacity: 0.6
      }}
    }},
    xAxis: {{
      title: {{
        text: "Label for X axis"
      }}
    }},
    yAxis: {{
      title: {{
        text: "Label for Y axis"
      }}
    }},
    meta: {{
      x_field: {{ alias: "x_field" }},
      y_field: {{ alias: "y_field" }}
    }}
  }};
  return <div><Column {{...config}} /></div>;
}}
""",
    "line": f"""
{TEMPLATE_INSTRUCTIONS}
function DrawLine() {{
  const {{ Line }} = components;
  const config = {{
    data,
    xField: "non_aggregate_column",
    yField: "aggregate_column",
    xAxis: {{
      title: {{
        text: "non_aggregate_column"
      }}
    }},
    yAxis: {{
      title: {{
        text: "aggregate_column"
      }}
    }},
    point: {{
      size: 5,
      shape: "diamond"
    }}
  }};
  return <div><Line {{...config}} /></div>;
}}
""",
    "table": f"""
{TEMPLATE_INSTRUCTIONS}
function DrawTable() {{
  const {{ Table }} = components;
  const columns =
    data.length > 0
      ? Object.keys(data[0]).map((key) => ({{
          title: key.replace(/_/g, " "),
          dataIndex: key,
          key
        }}))
      : [];
  return (
    <div>
      <Table
        columns={{columns}}
        dataSource={{data}}
        rowKey={{(record, index) => index}}
        pagination={{true}}
      />
    </div>
  );
}}
""",
    "area": f"""
{TEMPLATE_INSTRUCTIONS}
function DrawArea() {{
  const {{ Area }} = components;
  const config = {{
    data,
    xField: "x_field",
    yField: "y_field",
    areaStyle: {{
      fill: "l(270) 0:#ffffff 0.5:#69b2f8 1:#0050b3"
    }}
  }};
  return <div><Area {{...config}} /></div>;
}}
""",
    "pivot table": f"""
{TEMPLATE_INSTRUCTIONS}
function DemoPivot() {{
  const {{ PivotViewComponent }} = components;
  const {{ getPropertiesConfig, getTooltip }} = helperFunctions;
  const dataSourceSettings = {{
    columns: [{{ name: "column_fields" }}],
    values: [{{ name: "values_fields" }}],
    dataSource: data,
    rows: [],
    formatSettings: [],
    expandAll: true,
    filters: [],
    ...getPropertiesConfig("crosstab", report)
  }};
  return (
    <div>
      <PivotViewComponent
        id="PivotView"
        {{...getTooltip("crosstab", report, dataSourceSettings)}}
        dataSourceSettings={{dataSourceSettings}}
      />
    </div>
  );
}}
""",
    "kpi": f"""
{TEMPLATE_INSTRUCTIONS}
function DrawKPI() {{
  const {{ Card, Statistic }} = components;
  return (
    <Card loading={{false}} style={{{{ width: 200, border: 0 }}}}>
      <Statistic
        title="Put the title here"
        value={{data[0]?.value_column}}
        valueStyle={{{{ color: "#999" }}}}
      />
    </Card>
  );
}}
""",
}
