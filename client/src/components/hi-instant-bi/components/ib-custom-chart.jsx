import * as Icons from "@ant-design/icons";
import * as MapCharts from "@ant-design/maps";
import * as Plots from "@ant-design/plots";
import muze from "@chartshq/muze";
import Muze, { Canvas, Layer } from "@chartshq/react-muze/components";
import * as MuzeConfig from "@chartshq/react-muze/configurations";
import * as AntdComponents from "antd";
import React, { createContext, useContext, useLayoutEffect, useMemo, useState } from "react";
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
import {
  applyIbCompactPlotTheme,
  isIbCircularChart,
} from "../utils/ib-plot-theme";

const MuzeCharts = { Muze, Canvas, Layer, ...MuzeConfig, muze };
const MuzeTooltip = MuzeConfig.Tooltip;

const REACT_FORWARD_REF = Symbol.for("react.forward_ref");
const REACT_MEMO = Symbol.for("react.memo");

const IbPlotViewportContext = createContext({
  width: undefined,
  height: undefined,
  compact: false,
  chartName: "",
  circular: false,
});

const isReactComponent = (value) => {
  if (typeof value === "function") return true;
  if (!value || typeof value !== "object") return false;
  return value.$$typeof === REACT_FORWARD_REF || value.$$typeof === REACT_MEMO;
};

const wrapPlotForAutoFit = (Component, plotName = "") => {
  const ResponsivePlot = (plotProps) => {
    const viewport = useContext(IbPlotViewportContext);
    const { width: propWidth, height: propHeight, ...rest } = plotProps;
    const width = propWidth ?? viewport.width;
    const height = propHeight ?? viewport.height;
    const themed = applyIbCompactPlotTheme(rest, {
      plotName,
      chartName: viewport.chartName,
      compact: viewport.compact,
      circular: viewport.circular || undefined,
    });
    if (width > 1 && height > 1) {
      return <Component {...themed} width={width} height={height} autoFit={false} />;
    }
    return <Component {...themed} autoFit />;
  };
  ResponsivePlot.displayName = `AutoFit(${plotName || Component.displayName || Component.name || "Plot"})`;
  return ResponsivePlot;
};

const withAutoFitPlots = (plots) =>
  Object.fromEntries(
    Object.entries(plots).map(([name, Comp]) => {
      if (isReactComponent(Comp)) {
        return [name, wrapPlotForAutoFit(Comp, name)];
      }
      if (Comp && typeof Comp === "object" && !Comp.$$typeof) {
        return [name, withAutoFitPlots(Comp)];
      }
      return [name, Comp];
    }),
  );

const autoFitLibs = {
  compact: {
    plots: withAutoFitPlots(Plots),
    maps: withAutoFitPlots(MapCharts),
  },
  default: {
    plots: withAutoFitPlots(Plots),
    maps: withAutoFitPlots(MapCharts),
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
export const IB_VF_TEMPLATE_ERROR = IB_CHART_RENDER_ERROR;

export const IbResponseError = ({ details = "", className = "" }) => {
  const [showDetails, setShowDetails] = useState(false);
  return (
    <div className={`ib-response-error ${className}`.trim()} data-testid="ib-vf-template-error">
      <span>{IB_CHART_RENDER_ERROR}</span>
      {details ? (
        <>
          {" "}
          <button
            type="button"
            className="ib-response-error__details-link"
            onClick={() => setShowDetails((v) => !v)}
          >
            {showDetails ? "Hide Details" : "View Details"}
          </button>
        </>
      ) : null}
      {showDetails && details ? (
        <div className="ib-response-error__details">{details}</div>
      ) : null}
    </div>
  );
};

class IbChartErrorBoundary extends React.Component {
  state = { error: null };

  static getDerivedStateFromError(error) {
    return { error };
  }

  componentDidCatch(error) {
    console.error(error);
    this.props.onError?.(true);
  }

  componentDidUpdate(prevProps) {
    if (prevProps.resetKey !== this.props.resetKey && this.state.error) {
      this.setState({ error: null });
      this.props.onError?.(false);
    }
  }

  render() {
    if (this.state.error) {
      return <IbResponseError details={this.state.error?.message || String(this.state.error)} />;
    }
    return this.props.children;
  }
}

const IBCustomChart = (props) => {
  const dispatch = useDispatch();
  const {
    customChart = {},
    data,
    dataId,
    autoFit = true,
    compact = false,
    isKpiChart = false,
    chartName = "",
    chartAreaWidth,
    chartAreaHeight,
    onPreviewError,
  } = props;
  const code = customChart.code || "";
  const useCompactTheme = Boolean(compact);
  const circular = isIbCircularChart(chartName, code);
  const cacheKey = `${dataId}::${code}::${compact ? "c" : "d"}`;

  const viewport = useMemo(
    () => ({
      width: chartAreaWidth > 1 ? chartAreaWidth : undefined,
      height: chartAreaHeight > 1 ? chartAreaHeight : undefined,
      compact: useCompactTheme,
      chartName,
      circular,
    }),
    [chartAreaWidth, chartAreaHeight, useCompactTheme, chartName, circular],
  );

  const { Element, errorDetails } = useMemo(() => {
    if (!code.trim()) return { Element: null, errorDetails: "" };
    if (chartCache.has(cacheKey)) {
      return { Element: chartCache.get(cacheKey), errorDetails: "" };
    }

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
        () => {},
      );
      chartCache.set(cacheKey, el);
      return { Element: el, errorDetails: "" };
    } catch (error) {
      console.error(error);
      return { Element: null, errorDetails: error?.message || String(error) };
    }
  }, [cacheKey]);

  useLayoutEffect(() => {
    if (!code.trim()) {
      onPreviewError?.(true);
      return;
    }
    onPreviewError?.(!Element);
  }, [code, Element, onPreviewError]);

  const previewClassName = [
    "ib-live-preview",
    isKpiChart && "ib-live-preview--kpi",
    useCompactTheme && !isKpiChart && "ib-live-preview--chart",
    circular && !isKpiChart && "ib-live-preview--circular",
  ]
    .filter(Boolean)
    .join(" ");

  const previewStyle = isKpiChart
    ? { width: "100%", height: useCompactTheme ? "auto" : "100%" }
    : getPreviewStyles({ ...props, autoFit });

  if (!code.trim()) return null;

  if (!Element) {
    return <IbResponseError details={errorDetails} />;
  }

  return (
    <IbChartErrorBoundary resetKey={cacheKey} onError={onPreviewError}>
      <IbPlotViewportContext.Provider value={viewport}>
        <div className={previewClassName} style={previewStyle}>
          <Element />
        </div>
      </IbPlotViewportContext.Provider>
    </IbChartErrorBoundary>
  );
};

export default React.memo(
  IBCustomChart,
  (prev, next) =>
    prev.dataId === next.dataId &&
    prev.data === next.data &&
    prev.compact === next.compact &&
    Boolean(prev.isKpiChart) === Boolean(next.isKpiChart) &&
    (prev.chartName || "") === (next.chartName || "") &&
    prev.chartAreaWidth === next.chartAreaWidth &&
    prev.chartAreaHeight === next.chartAreaHeight &&
    (prev.customChart?.code || "") === (next.customChart?.code || ""),
);
