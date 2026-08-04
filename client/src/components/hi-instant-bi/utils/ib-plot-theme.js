const IB_CHART_FONT_SIZE = 10;
const IB_MAX_LABEL_POINTS = 10;
export const IB_CIRCULAR_CHART_TYPES = new Set([
  "pie",
  "donut",
  "doughnut",
  "arc",
  "rose_chart",
  "rose",
  "radar",
  "gauge",
  "progress",
  "sunburst",
  "circle_packing",
  "wordcloud",
  "funnel_chart",
  "funnel",
  "treemap",
]);
const IB_CIRCULAR_PLOT_NAMES = new Set([
  "Rose",
  "Pie",
  "Radar",
  "RadialBar",
  "Gauge",
  "Liquid",
  "RingProgress",
  "Sunburst",
  "Funnel",
  "Treemap",
  "WordCloud",
  "CirclePacking",
]);

const IB_AXIS_OFF_BY_DEFAULT = new Set(["Rose", "Pie", "Funnel", "Treemap", "WordCloud", "Sunburst", "CirclePacking"]);

const IB_POLAR_PLOTS = new Set([
  "Rose",
  "Pie",
  "Radar",
  "RadialBar",
  "Gauge",
  "Liquid",
  "RingProgress",
]);

const IB_COMPACT_STYLE_SHEET = {
  axisTitleTextFontSize: IB_CHART_FONT_SIZE,
  axisLabelFontSize: IB_CHART_FONT_SIZE,
  legendTitleTextFontSize: IB_CHART_FONT_SIZE,
  legendItemNameFontSize: IB_CHART_FONT_SIZE,
  legendPageNavigatorTextFontSize: IB_CHART_FONT_SIZE,
  legendPageNavigatorMarkerSize: 6,
  sliderLabelTextFontSize: IB_CHART_FONT_SIZE,
  labelFontSize: IB_CHART_FONT_SIZE,
  tooltipTextFontSize: IB_CHART_FONT_SIZE,
};

const IB_LABEL_LAYOUT = [
  { type: "interval-adjust-position" },
  { type: "interval-hide-overlap" },
  { type: "point-adjust-position" },
  { type: "path-adjust-position" },
  { type: "hide-overlap" },
  { type: "adjust-color" },
  { type: "limit-in-plot", cfg: { action: "hide" } },
];

const IB_POLAR_LABEL_LAYOUT = [
  { type: "hide-overlap" },
  { type: "adjust-color" },
  { type: "limit-in-plot", cfg: { action: "hide" } },
];

export const normalizeIbChartType = (chartName = "") =>
  String(chartName)
    .trim()
    .toLowerCase()
    .replace(/^vf\./, "")
    .replace(/\s+/g, "_")
    .replace(/-/g, "_");

export const isIbCircularChartType = (chartName = "") =>
  IB_CIRCULAR_CHART_TYPES.has(normalizeIbChartType(chartName));

export const isIbCircularPlotName = (plotName = "") =>
  IB_CIRCULAR_PLOT_NAMES.has(String(plotName));

export const isIbCircularChart = (chartName = "", vf = "") => {
  if (isIbCircularChartType(chartName)) return true;
  return /<(?:Radar|Pie|Rose|Gauge|Funnel|WordCloud|Treemap|Sunburst|CirclePacking|RingProgress|Liquid|RadialBar)\b/.test(
    String(vf),
  );
};

const withIbFontStyle = (style = {}) => {
  const next = { ...style, fontSize: IB_CHART_FONT_SIZE };
  delete next.rotate;
  return next;
};

const mergeAxisTheme = (axis = {}, { polar = false } = {}) => ({
  ...axis,
  label: axis?.label === null
    ? null
    : {
        autoRotate: !polar,
        autoHide: true,
        ...axis?.label,
        style: withIbFontStyle(axis?.label?.style),
      },
  title: axis?.title === null
    ? null
    : {
        ...axis?.title,
        style: withIbFontStyle(axis?.title?.style),
      },
});

const mergeLegendTheme = (legend, { circular = false, compact = false } = {}) => {
  if (legend === false || legend === null) return legend;
  const legendConfig = legend === true ? {} : legend || {};
  const position =
    legendConfig.position ||
    (circular ? "bottom" : undefined);
  return {
    ...legendConfig,
    ...(position ? { position } : {}),
    ...(circular && compact && legendConfig.maxRow == null ? { maxRow: 2 } : {}),
    itemName: legendConfig?.itemName === null
      ? null
      : {
          ...legendConfig?.itemName,
          style: withIbFontStyle(legendConfig?.itemName?.style),
        },
    pageNavigator: legendConfig?.pageNavigator === null
      ? null
      : {
          ...legendConfig?.pageNavigator,
          text: legendConfig?.pageNavigator?.text === null
            ? null
            : {
                ...legendConfig?.pageNavigator?.text,
                style: withIbFontStyle(legendConfig?.pageNavigator?.text?.style),
              },
        },
  };
};

const mergeDataLabel = (label, data, { polar = false } = {}) => {
  if (!label) return label;
  if (Array.isArray(data) && data.length > IB_MAX_LABEL_POINTS) {
    return false;
  }
  const base = label === true ? {} : { ...label };
  const { rotate, ...rest } = base;
  return {
    ...rest,
    layout: polar ? IB_POLAR_LABEL_LAYOUT : IB_LABEL_LAYOUT,
    style: withIbFontStyle(rest.style),
  };
};

const applyLabelOnConfig = (config = {}, { polar = false } = {}) => {
  const next = { ...config };
  if (next.label) {
    next.label = mergeDataLabel(next.label, next.data, { polar });
  }
  if (Array.isArray(next.geometryOptions)) {
    next.geometryOptions = next.geometryOptions.map((geo) =>
      geo?.label
        ? { ...geo, label: mergeDataLabel(geo.label, next.data, { polar }) }
        : geo,
    );
  }
  return next;
};

const applyCircularLayout = (config = {}, { compact = false, plotName = "" } = {}) => {
  const next = { ...config };
  if (next.appendPadding == null && next.padding == null) {
    next.appendPadding = compact ? [8, 8, 8, 8] : [12, 12, 12, 12];
  }
  if (next.radius == null && IB_POLAR_PLOTS.has(plotName)) {
    next.radius = compact ? 0.85 : 0.9;
  }
  return next;
};

/**
 * Shared Instant BI plot theme — works for every Ant Design Plots chart type.
 * @param {object} config Ant Design Plots config from VF
 * @param {{ plotName?: string, chartName?: string, compact?: boolean, circular?: boolean }} [options]
 */
export const applyIbCompactPlotTheme = (config = {}, options = {}) => {
  const plotName = options.plotName || "";
  const circular =
    options.circular != null
      ? Boolean(options.circular)
      : isIbCircularPlotName(plotName) || isIbCircularChartType(options.chartName);
  const polar = IB_POLAR_PLOTS.has(plotName) || circular;
  const preserveAxesOff = IB_AXIS_OFF_BY_DEFAULT.has(plotName) || (
    circular && !["Radar", "RadialBar"].includes(plotName)
  );
  const compact = Boolean(options.compact);
  let next = { ...config };

  next.theme = {
    ...(next.theme || {}),
    styleSheet: {
      ...((next.theme || {}).styleSheet || {}),
      ...IB_COMPACT_STYLE_SHEET,
    },
  };

  if (preserveAxesOff) {
    if (next.xAxis && typeof next.xAxis === "object") {
      next.xAxis = mergeAxisTheme(next.xAxis, { polar: true });
    }
    if (next.yAxis && typeof next.yAxis === "object") {
      next.yAxis = mergeAxisTheme(next.yAxis, { polar: true });
    }
  } else {
    if (next.xAxis !== false && next.xAxis !== null) {
      next.xAxis = mergeAxisTheme(next.xAxis || {}, { polar });
    }
    if (next.yAxis !== false && next.yAxis !== null) {
      next.yAxis = mergeAxisTheme(next.yAxis || {}, { polar });
    }
  }
  if (next.legend !== false && next.legend !== null) {
    next.legend = mergeLegendTheme(next.legend ?? {}, { circular, compact });
  }

  if (next.slider) {
    const slider = next.slider === true ? {} : next.slider;
    next.slider = {
      ...slider,
      textStyle: withIbFontStyle(slider?.textStyle),
    };
  }

  if (circular) {
    next = applyCircularLayout(next, { compact, plotName });
  }
  if (plotName === "Rose" && next.label == null) {
    next.label = false;
  } else if (
    polar &&
    next.label == null &&
    Array.isArray(next.data) &&
    next.data.length > IB_MAX_LABEL_POINTS
  ) {
    next.label = false;
  }
  next = applyLabelOnConfig(next, { polar });
  return next;
};

export const IB_CHART_FONT_SIZE_PX = IB_CHART_FONT_SIZE;
