const IB_CHART_FONT_SIZE = 10;

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

const withIbFontStyle = (style = {}) => ({
  ...style,
  fontSize: IB_CHART_FONT_SIZE,
});

const mergeAxisTheme = (axis = {}) => ({
  ...axis,
  label: axis?.label === null
    ? null
    : {
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

const mergeLegendTheme = (legend) => {
  if (legend === false || legend === null) return legend;
  const legendConfig = legend === true ? {} : legend || {};
  return {
    ...legendConfig,
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

export const applyIbCompactPlotTheme = (config = {}) => {
  const next = { ...config };

  next.theme = {
    ...(next.theme || {}),
    styleSheet: {
      ...((next.theme || {}).styleSheet || {}),
      ...IB_COMPACT_STYLE_SHEET,
    },
  };

  if (next.xAxis !== false && next.xAxis !== null) {
    next.xAxis = mergeAxisTheme(next.xAxis || {});
  }
  if (next.yAxis !== false && next.yAxis !== null) {
    next.yAxis = mergeAxisTheme(next.yAxis || {});
  }
  if (next.legend !== false && next.legend !== null) {
    next.legend = mergeLegendTheme(next.legend ?? {});
  }

  if (next.slider) {
    const slider = next.slider === true ? {} : next.slider;
    next.slider = {
      ...slider,
      textStyle: withIbFontStyle(slider?.textStyle),
    };
  }

  if (next.label && next.label !== true) {
    next.label = {
      ...next.label,
      style: withIbFontStyle(next.label?.style),
    };
  } else if (next.label === true) {
    next.label = { style: withIbFontStyle() };
  }

  return next;
};

export const IB_CHART_FONT_SIZE_PX = IB_CHART_FONT_SIZE;
