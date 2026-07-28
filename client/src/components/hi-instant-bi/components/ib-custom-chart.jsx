import * as Icons from "@ant-design/icons";
import * as MapCharts from "@ant-design/maps";
import * as Plots from "@ant-design/plots";
import muze from "@chartshq/muze";
import Muze, { Canvas, Layer } from "@chartshq/react-muze/components";
import * as MuzeConfig from "@chartshq/react-muze/configurations";
import * as AntdComponents from "antd";
import React, { useLayoutEffect, useMemo } from "react";
import { generateElement } from "react-live";
import { useDispatch } from "react-redux";
import { getPreviewStyles } from "../../hi-reports/hi-viz-area/utils/utillities";
import {
  enableInteractivity,
  getGridChartConfig,
  getGridChartLabels,
  getPropertiesConfig,
  getTableColumns,
  getTooltip,
  changePageSize,
} from "../../hi-reports/hi-viz-area/custom-charts/utilities";
import GridTable from "../../hi-reports/hi-viz-area/s2-charts/s2chart";
import { buildGridTableReport } from "../../hi-reports/hi-viz-area/s2-charts/build-grid-table-report";
import "../../hi-reports/hi-viz-area/table/table.scss";
import { applyIbCompactPlotTheme } from "../utils/ib-plot-theme";

const MuzeCharts = { Muze, Canvas, Layer, ...MuzeConfig, muze };
const MuzeTooltip = MuzeConfig.Tooltip;

const REACT_FORWARD_REF = Symbol.for("react.forward_ref");
const REACT_MEMO = Symbol.for("react.memo");

const isReactComponent = (value) => {
  if (typeof value === "function") return true;
  if (!value || typeof value !== "object") return false;
  return value.$$typeof === REACT_FORWARD_REF || value.$$typeof === REACT_MEMO;
};

const wrapPlotForAutoFit = (Component, useCompactTheme = false) => {
  const ResponsivePlot = (plotProps) => {
    const { width, height, ...rest } = plotProps;
    const config = useCompactTheme ? applyIbCompactPlotTheme(rest) : rest;
    return <Component {...config} autoFit />;
  };
  ResponsivePlot.displayName = `AutoFit(${Component.displayName || Component.name || "Plot"})`;
  return ResponsivePlot;
};

const withAutoFitPlots = (plots, useCompactTheme = false) =>
  Object.fromEntries(
    Object.entries(plots).map(([name, Comp]) => [
      name,
      isReactComponent(Comp) ? wrapPlotForAutoFit(Comp, useCompactTheme) : Comp,
    ]),
  );

const autoFitLibs = {
  compact: {
    plots: withAutoFitPlots(Plots, true),
    maps: withAutoFitPlots(MapCharts, true),
  },
  default: {
    plots: withAutoFitPlots(Plots, false),
    maps: withAutoFitPlots(MapCharts, false),
  },
};

const HELPER_FUNCTIONS = {
  getTooltip,
  getPropertiesConfig,
  enableInteractivity,
  getGridChartLabels,
  getTableColumns,
  getGridChartConfig,
  changePageSize,
  applyIbCompactPlotTheme,
  buildGridTableReport,
};

const chartCache = new Map();

export const IB_CHART_RENDER_ERROR = "Something went wrong. Please try again.";
export const IB_VF_TEMPLATE_ERROR = "Something went wrong in vf_template";

const IBCustomChart = (props) => {
  const dispatch = useDispatch();
  const {
    customChart = {},
    data,
    dataId,
    autoFit = true,
    compact = false,
    isKpiChart = false,
    onPreviewError,
  } = props;
  const code = customChart.code || "";
  const useCompactTheme = Boolean(compact);
  const cacheKey = `${dataId}::${code}::${compact ? "c" : "d"}`;

  const Element = useMemo(() => {
    if (!code.trim()) return null;
    if (chartCache.has(cacheKey)) return chartCache.get(cacheKey);

    const libs = autoFit
      ? autoFitLibs[compact ? "compact" : "default"]
      : { plots: Plots, maps: MapCharts };

    try {
      const el = generateElement(
        {
          code,
          scope: {
            components: {
              ...libs.maps,
              ...libs.plots,
              ...MuzeCharts,
              ...AntdComponents,
              ...Icons,
              GridTable,
              MuzeTooltip,
            },
            data,
            report: { ...props, dispatch, autoFit },
            helperFunctions: HELPER_FUNCTIONS,
          },
        },
        () => {
        },
      );
      chartCache.set(cacheKey, el);
      return el;
    } catch {
      return null;
    }
  }, [cacheKey]);

  useLayoutEffect(() => {
    if (!code.trim()) {
      onPreviewError?.(true);
      return;
    }
    if (!Element) return;
    onPreviewError?.(false);
  }, [code, Element, onPreviewError]);

  const previewClassName = [
    "ib-live-preview",
    isKpiChart && "ib-live-preview--kpi",
    useCompactTheme && !isKpiChart && "ib-live-preview--chart",
  ]
    .filter(Boolean)
    .join(" ");

  const previewStyle = isKpiChart
    ? { width: "100%", height: useCompactTheme ? "auto" : "100%" }
    : getPreviewStyles({ ...props, autoFit });

  if (!code.trim()) return null;

  if (!Element) {
    return (
      <div className="ib-response-error" data-testid="ib-vf-template-error">
        {IB_VF_TEMPLATE_ERROR}
      </div>
    );
  }

  return (
    <div className={previewClassName} style={previewStyle}>
      <Element />
    </div>
  );
};

export default React.memo(
  IBCustomChart,
  (prev, next) =>
    prev.dataId === next.dataId &&
    prev.data === next.data &&
    prev.compact === next.compact &&
    Boolean(prev.isKpiChart) === Boolean(next.isKpiChart) &&
    (prev.customChart?.code || "") === (next.customChart?.code || ""),
);
