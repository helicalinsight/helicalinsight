const OVERVIEW_CHART_COLORS = {
  primary: "#1ec469",
  secondary: "#4c84ee",
  warning: "#faad14",
  danger: "#f5222d",
  muted: "#e8edf3",
  free: "#ccc",
};

const getOverviewChartTheme = () => ({
  colors10: [
    OVERVIEW_CHART_COLORS.primary,
    OVERVIEW_CHART_COLORS.secondary,
    OVERVIEW_CHART_COLORS.warning,
    OVERVIEW_CHART_COLORS.danger,
    "#9254de",
    "#13c2c2",
  ],
});

const getRingProgressConfig = ({ percent, title, value, height = 140 }) => ({
  height,
  autoFit: true,
  percent: Math.min(Math.max(percent || 0, 0), 1),
  color: [OVERVIEW_CHART_COLORS.primary, OVERVIEW_CHART_COLORS.muted],
  innerRadius: 0.75,
  radius: 1,
  statistic: {
    title: {
      style: { fontSize: "12px", color: "#555" },
      content: title,
    },
    content: {
      style: { fontSize: "18px", fontWeight: 600, color: "#333" },
      content: value,
    },
  },
});

const getPieConfig = ({ data, height = 160 }) => ({
  data,
  height,
  autoFit: true,
  angleField: "value",
  colorField: "type",
  radius: 0.95,
  innerRadius: 0.55,
  label: false,
  legend: {
    position: "bottom",
    itemHeight: 14,
    itemName: { style: { fontSize: 11 } },
  },
  color: [OVERVIEW_CHART_COLORS.primary, OVERVIEW_CHART_COLORS.free],
  interactions: [{ type: "element-active" }],
  tooltip: {
    formatter: (datum) => ({
      name: datum.type,
      value: datum.displayValue ?? datum.value,
    }),
  },
});

const getColumnConfig = ({ data, height = 200, color = OVERVIEW_CHART_COLORS.secondary }) => ({
  data,
  height,
  autoFit: true,
  xField: "category",
  yField: "value",
  color,
  columnStyle: { radius: [4, 4, 0, 0] },
  label: {
    position: "top",
    style: { fontSize: 11 },
    formatter: (datum) => datum.displayValue ?? datum.value,
  },
  xAxis: {
    label: { style: { fontSize: 11 } },
  },
  yAxis: {
    label: { style: { fontSize: 11 } },
    grid: { line: { style: { stroke: "#f0f0f0" } } },
  },
  meta: {
    value: { alias: "Count" },
  },
  tooltip: {
    formatter: (datum) => ({
      name: datum.category,
      value: datum.displayValue ?? datum.value,
    }),
  },
});

const getUsagePercent = (used, total) => {
  const usedNum = parseFloat(used);
  const totalNum = parseFloat(total);
  if (!totalNum || isNaN(usedNum) || isNaN(totalNum)) return 0;
  return usedNum / totalNum;
};

export {
  OVERVIEW_CHART_COLORS,
  getOverviewChartTheme,
  getRingProgressConfig,
  getPieConfig,
  getColumnConfig,
  getUsagePercent,
};
