import * as Icons from "@ant-design/icons";
import * as MapCharts from "@ant-design/maps";
import * as Plots from "@ant-design/plots";
import muze from "@chartshq/muze";
import Muze, { Canvas, Layer } from "@chartshq/react-muze/components";
import * as MuzeConfig from "@chartshq/react-muze/configurations";
import * as AntdComponents from "antd";
import React, { useContext, useLayoutEffect } from "react";
import { LiveContext, LivePreview, LiveProvider } from "react-live";
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
import "../../hi-reports/hi-viz-area/table/table.scss";
import { applyIbCompactPlotTheme } from "../utils/ib-plot-theme";

const MuzeCharts = {
  Muze,
  Canvas,
  Layer,
  ...MuzeConfig,
  muze,
};
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

export const IB_CHART_RENDER_ERROR = "Something went wrong. Please try again.";

const PreviewErrorObserver = ({ onPreviewError }) => {
  const { error } = useContext(LiveContext);
  useLayoutEffect(() => {
    onPreviewError?.(Boolean(error));
  }, [error, onPreviewError]);
  return null;
};

const IBChartRenderFallback = ({ onPreviewError }) => {
  useLayoutEffect(() => {
    onPreviewError?.(true);
    return () => onPreviewError?.(false);
  }, [onPreviewError]);
  return null;
};

const IBCustomChart = (props) => {
  const dispatch = useDispatch();
  const {
    customChart = {},
    data,
    autoFit = true,
    compact = false,
    isKpiChart = false,
    onPreviewError,
  } = props;
  const useCompactTheme = Boolean(compact);
  const plotComponents = autoFit
    ? withAutoFitPlots(Plots, useCompactTheme)
    : Plots;
  const mapComponents = autoFit
    ? withAutoFitPlots(MapCharts, useCompactTheme)
    : MapCharts;
  const scope = {
    components: {
      ...mapComponents,
      ...plotComponents,
      ...MuzeCharts,
      ...AntdComponents,
      ...Icons,
      GridTable,
      MuzeTooltip,
    },
    data,
    report: { ...props, dispatch, autoFit },
    helperFunctions: {
      getTooltip,
      getPropertiesConfig,
      enableInteractivity,
      getGridChartLabels,
      getTableColumns,
      getGridChartConfig,
      changePageSize,
      applyIbCompactPlotTheme,
    },
  };
  const chartCode = customChart.code || "";
  if (!chartCode.trim()) {
    return <IBChartRenderFallback onPreviewError={onPreviewError} />;
  }

  const previewClassName = [
    "ib-live-preview",
    isKpiChart ? "ib-live-preview--kpi" : "",
    useCompactTheme && !isKpiChart ? "ib-live-preview--chart" : "",
  ]
    .filter(Boolean)
    .join(" ");

  return (
    <LiveProvider code={chartCode} scope={scope}>
      {onPreviewError && <PreviewErrorObserver onPreviewError={onPreviewError} />}
      <LivePreview
        className={previewClassName}
        style={
          isKpiChart
            ? { width: "100%", height: useCompactTheme ? "auto" : "100%" }
            : getPreviewStyles({ ...props, autoFit })
        }
      />
    </LiveProvider>
  );
};

const areEqual = (prevProps, nextProps) => {
  const { dataId, chartAreaHeight, chartAreaWidth, customChart = {}, compact } = prevProps;
  const isSameCode =
    (customChart.code || "") === (nextProps.customChart?.code || "");
  return (
    dataId === nextProps.dataId &&
    chartAreaHeight === nextProps.chartAreaHeight &&
    chartAreaWidth === nextProps.chartAreaWidth &&
    compact === nextProps.compact &&
    isSameCode
  );
};

export default React.memo(IBCustomChart, areEqual);
