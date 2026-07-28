/**
 * Frontend-only InstantBI VF chart-type conversion.
 * Swaps Ant Design Plots components / field orientation without calling /interactive.
 */

const normalizeType = (name = "") =>
  String(name)
    .trim()
    .toLowerCase()
    .replace(/\s+/g, "_")
    .replace(/-/g, "_");

/** InstantBI visualization_type -> Ant Design Plots / UI component name */
export const IB_CHART_COMPONENT = {
  bar: "Bar",
  column: "Column",
  line: "Line",
  area: "Area",
  pie: "Pie",
  donut: "Pie",
  doughnut: "Pie",
  rose_chart: "Rose",
  scatter: "Scatter",
  point: "Scatter",
  bubble_chart: "Scatter",
  heatmap: "Heatmap",
  funnel_chart: "Funnel",
  waterfall: "Waterfall",
  radar: "Radar",
  gauge: "Gauge",
  progress: "RingProgress",
  treemap: "Treemap",
  wordcloud: "WordCloud",
  dual_line: "DualAxes",
  column_line: "DualAxes",
  tiny_line: "Tiny.Line",
  tiny_area: "Tiny.Area",
  tiny_column: "Tiny.Column",
  kpi: "Statistic",
  table: "Table",
  grid_table: "GridTable",
  pivot_table: "GridTable",
};

const PASCAL = (type) => {
  const key = normalizeType(type);
  return key
    .split("_")
    .filter(Boolean)
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
    .join("");
};

const swapXyFields = (code) => {
  let next = code;
  const xMatch = next.match(/xField\s*:\s*["'`]([^"'`]+)["'`]/);
  const yMatch = next.match(/yField\s*:\s*["'`]([^"'`]+)["'`]/);
  if (!xMatch || !yMatch) {
    // Also support const refs: xField: dimensionField / measureField
    const xRef = next.match(/xField\s*:\s*([A-Za-z_][\w]*)/);
    const yRef = next.match(/yField\s*:\s*([A-Za-z_][\w]*)/);
    if (xRef && yRef && xRef[1] !== "data" && yRef[1] !== "data") {
      next = next.replace(/xField\s*:\s*[A-Za-z_][\w]*/, `xField: ${yRef[1]}`);
      next = next.replace(/yField\s*:\s*[A-Za-z_][\w]*/, `yField: ${xRef[1]}`);
    }
    return next;
  }
  const xVal = xMatch[1];
  const yVal = yMatch[1];
  next = next.replace(/xField\s*:\s*["'`][^"'`]+["'`]/, `xField: ${JSON.stringify(yVal)}`);
  next = next.replace(/yField\s*:\s*["'`][^"'`]+["'`]/, `yField: ${JSON.stringify(xVal)}`);
  return next;
};

const ensureInnerRadius = (code, radius = 0.6) => {
  if (/innerRadius\s*:/.test(code)) {
    return code.replace(/innerRadius\s*:\s*[^,\n}]+/, `innerRadius: ${radius}`);
  }
  if (/radius\s*:/.test(code)) {
    return code.replace(/(radius\s*:\s*[^,\n}]+)/, `$1,\n    innerRadius: ${radius}`);
  }
  return code.replace(/(const config = \{)/, `$1\n    innerRadius: ${radius},`);
};

const removeInnerRadius = (code) =>
  code
    .replace(/,?\s*innerRadius\s*:\s*[^,\n}]+/g, "")
    .replace(/\{\s*,/g, "{");

/**
 * Convert a Draw* VF function string from one InstantBI chart type to another.
 * @returns {{ code: string, chartName: string, converted: boolean }}
 */
export const convertIbVfChartType = (vfCode, fromType, toType) => {
  const from = normalizeType(fromType);
  const to = normalizeType(toType);
  if (!vfCode || !to || from === to) {
    return { code: vfCode || "", chartName: to || from, converted: false };
  }

  const fromComp = IB_CHART_COMPONENT[from];
  const toComp = IB_CHART_COMPONENT[to];
  let code = String(vfCode);

  if (fromComp && toComp && fromComp !== toComp) {
    const fromRe = new RegExp(`\\b${fromComp}\\b`, "g");
    code = code.replace(fromRe, toComp);
  }

  if (
    (from === "bar" && to === "column") ||
    (from === "column" && to === "bar")
  ) {
    code = swapXyFields(code);
  }

  if (
    (from === "pie" || !/innerRadius/.test(vfCode)) &&
    (to === "donut" || to === "doughnut")
  ) {
    code = ensureInnerRadius(code, 0.6);
  }
  if ((from === "donut" || from === "doughnut") && to === "pie") {
    code = removeInnerRadius(code);
  }
  if (
    (from === "donut" || from === "doughnut") &&
    (to === "donut" || to === "doughnut")
  ) {
    code = ensureInnerRadius(code, 0.6);
  }

  const fromDraw = `Draw${PASCAL(from)}`;
  const toDraw = `Draw${PASCAL(to)}`;
  code = code.replace(new RegExp(`\\bfunction\\s+${fromDraw}\\b`, "g"), `function ${toDraw}`);
  code = code.replace(new RegExp(`\\bfunction\\s+Draw[A-Za-z0-9_]+\\b`), `function ${toDraw}`);

  return {
    code,
    chartName: to,
    converted: code !== vfCode,
  };
};

export default convertIbVfChartType;
