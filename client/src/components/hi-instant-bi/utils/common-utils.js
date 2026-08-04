import { ConsoleSqlOutlined, DatabaseOutlined, EyeOutlined, TableOutlined } from "@ant-design/icons";
import { useEffect, useRef, useState } from "react";
import { v4 as uuidv4 } from "uuid";
import IBCustomChart, { IbResponseError } from "../components/ib-custom-chart";
import CommonMarkdownTable from "./common-markdown-table";
import { isIbCircularChart } from "./ib-plot-theme";

export { isIbCircularChart, isIbCircularChartType } from "./ib-plot-theme";

export const cx = (...args) => {
    return args
        .flatMap(arg => {
            if (typeof arg === 'string' || typeof arg === 'number') {
                return arg;
            }
            if (Array.isArray(arg)) {
                return cx(...arg);
            }
            if (typeof arg === 'object' && arg !== null) {
                return Object.keys(arg)
                    .filter(key => arg[key])
                    .join(' ');
            }
            return '';
        })
        .filter(Boolean)
        .join(' ');
}

export const createInsantBIGridItems = ({ metadataShelf, previewShelf, chatShelf, offsetHeight }) => {
    let calculatedH = 52;
    try {
        calculatedH = offsetHeight / 12 || 52;
    } catch (e) {
        calculatedH = 52;
    }

    let metadataShelfWidth = !metadataShelf ? 0 : 16.5;
    let previewShelfWidth = !previewShelf ? 0 : (100 - (metadataShelfWidth + 30))
    let chatShelfWidth = !chatShelf ? 0 : 100 - previewShelfWidth - metadataShelfWidth

    let layoutItems = [
        {
            i: "metadata-area",
            y: 0,
            h: calculatedH,
            isDraggable: false,
            isResizable: false,
        },
        {
            i: "preview-area",
            y: 0,
            h: calculatedH,
            isDraggable: false,
            isResizable: false,
        },
        {
            i: "chat-area",
            y: 0,
            h: calculatedH,
            isDraggable: false,
            isResizable: false,
        },
    ];

    let lg = [
        { ...layoutItems[0], x: 0, w: metadataShelfWidth },
        {
            ...layoutItems[1],
            x: metadataShelfWidth,
            w: previewShelfWidth,
        },
        { ...layoutItems[2], x: (previewShelfWidth + metadataShelfWidth), w: chatShelfWidth },
    ];

    let obj = {
        lg,
        md: lg,
        sm: [
            { ...layoutItems[0], x: 0, w: 50 },
            { ...layoutItems[1], x: 50, w: 50 },
            { ...layoutItems[2], x: 0, w: 100 },
        ],
        xs: [
            { ...layoutItems[0], x: 0, w: 50 },
            { ...layoutItems[1], x: 50, w: 50 },
            { ...layoutItems[2], x: 0, w: 100 },
        ],
        xxs: [
            { ...layoutItems[0], x: 0, w: 100 },
            { ...layoutItems[1], x: 0, w: 100 },
            { ...layoutItems[2], x: 0, w: 100 },
        ],
    };
    return obj;
};

export const getTimeStamp = (timestamp = null) => {
    let date;
    if (timestamp) {
        date = new Date(timestamp);
    } else {
        date = new Date();
    }
    let hours = date.getHours();
    let minutes = date.getMinutes();
    let ampm = hours >= 12 ? "PM" : "AM";
    hours = hours % 12;
    hours = hours ? hours : 12;
    minutes = minutes < 10 ? "0" + minutes : minutes;
    let strTime = hours + ":" + minutes + " " + ampm;
    return strTime;
}

export const prepareIBChatNewMessage = (message = '', isUser = false) => {
    const id = uuidv4();
    return {
        id,
        text: message,
        time: getTimeStamp(),
        isUser: isUser
    }
}

export const transformRecommendations = (arr, chunkSizes = [2, 3, 4, 2]) => {
    let result = [];
    let index = 0;

    for (let size of chunkSizes) {
        result.push(arr.slice(index, index + size));
        index += size;
    }

    return result;
}

 export const tabItems = [
     {
       key: "preview",
       title: "Preview",
       icon: <EyeOutlined />,
     },
     {
       key: "data",
       title: "Data",
       icon: <TableOutlined />,
     },
     {
       key: "metadata",
       title: "Semantic",
       icon: <DatabaseOutlined />,
     },
     {
       key: "sql",
       title: "SQL",
       icon: <ConsoleSqlOutlined />,
     },
   ];

export const getInstantBIAgentSubject = (activeReport = {}) => {
    const { metadata = {}, subject = {} } = activeReport;
    const { formData: metadataFormData = {} } = metadata || {};
    const subjectModel = subject?.model || subject?.agent || {};
    if (subjectModel.file && subjectModel.dir) {
        return { file: subjectModel.file, dir: subjectModel.dir };
    }
    if (metadataFormData.metadataFileName && metadataFormData.location) {
        return {
            file: metadataFormData.metadataFileName,
            dir: metadataFormData.location,
        };
    }
    return null;
};

export const cleanSQL = (sqlText = "") => {
  return sqlText
    .replace(/```sql\s*/gi, "")
    .replace(/```/g, "")
    .trim();
};

/** Shared compact viewport for every chart type (same size in chat preview). */
const COMPACT_CHART_ASPECT_RATIO = 420 / 1000;
const COMPACT_CHART_MIN_HEIGHT = 220;
const CHART_PREVIEW_MODAL_HEIGHT_OFFSET = 140;
const MIN_CHART_SIZE = 1;

export const getChartPreviewModalHeight = () =>
  typeof window !== "undefined"
    ? Math.max(window.innerHeight - CHART_PREVIEW_MODAL_HEIGHT_OFFSET, 400)
    : 600;

export const getIbChartViewportSize = ({
  width,
  height,
  compact = false,
  isKpiChart = false,
} = {}) => {
  const chartWidth = Math.max(Number(width) || 0, MIN_CHART_SIZE);
  if (isKpiChart) {
    return { width: chartWidth, height: compact ? undefined : Math.max(Number(height) || 0, MIN_CHART_SIZE) };
  }
  if (compact) {
    const compactHeight = Math.max(
      chartWidth * COMPACT_CHART_ASPECT_RATIO,
      COMPACT_CHART_MIN_HEIGHT,
      MIN_CHART_SIZE,
    );
    return { width: chartWidth, height: compactHeight };
  }
  return {
    width: chartWidth,
    height: Math.max(Number(height) || getChartPreviewModalHeight(), MIN_CHART_SIZE),
  };
};

const useChartContainerSize = (containerRef, { width, height, observe }) => {
  const [size, setSize] = useState({ width: 0, height: 0 });
  useEffect(() => {
    if (!observe) return undefined;
    const el = containerRef.current;
    if (!el || typeof ResizeObserver === "undefined") return undefined;
    const update = () => {
      const nextWidth = width ?? el.clientWidth;
      const nextHeight = height ?? el.clientHeight;
      setSize((prev) => {
        const w = nextWidth <= 0 && prev.width > 0 ? prev.width : nextWidth;
        const h = nextHeight <= 0 && prev.height > 0 ? prev.height : nextHeight;
        return prev.width === w && prev.height === h ? prev : { width: w, height: h };
      });
    };
    update();
    const observer = new ResizeObserver(update);
    observer.observe(el);
    return () => observer.disconnect();
  }, [containerRef, width, height, observe]);
  return { width: width ?? size.width, height: height ?? size.height };
};

export const isIbKpiChart = (chartName = "", vf = "") => {
  const name = String(chartName).toLowerCase();
  if (name === "kpi" || name === "other") return true;
  return /function\s+Draw(?:KPI|Other)\b/.test(String(vf));
};

export const isIbTableChart = (chartName = "", vf = "") => {
  const name = String(chartName).toLowerCase();
  // Grid / pivot VF uses Ant Design Table in DrawGridTable — not markdown flat table.
  if (name === "grid_table" || name === "pivot_table") return false;
  if (name === "table") return true;
  const vfText = String(vf);
  if (/\bGridTable\b/.test(vfText) || /\bDrawGridTable\b/.test(vfText)) return false;
  return (
    /function\s+DrawTable\b/.test(vfText) ||
    /<Table[\s/>]/.test(vfText)
  );
};

export const parseBackendErrorMessage = (error) => {
  if (!error) return "";
  if (typeof error === "object" && error.message) return String(error.message);
  const str = String(error).trim();
  const normalized = str
    .replace(/\\\\'/g, "__SINGLE_QUOTE__")
    .replace(/\\"/g, "__DOUBLE_QUOTE__");
  const messageMatch = normalized.match(
    /['"]message['"]\s*:\s*['"]([\s\S]*?)['"]\s*(?:,\s*['"]className['"]\s*:|\})/i,
  );
  const extracted = messageMatch ? messageMatch[1] : normalized;
  return extracted
    .replace(/__SINGLE_QUOTE__/g, "'")
    .replace(/__DOUBLE_QUOTE__/g, '"')
    .replace(/\\\\/g, "\\")
    .trim();
};

export const ChartView = ({
  data,
  vf,
  id,
  width,
  height,
  className = "",
  compact = false,
  chartName = "",
  onPreviewError,
  backendError,
}) => {
  const containerRef = useRef(null);
  const isTableChart = isIbTableChart(chartName, vf);
  const isKpiChart = isIbKpiChart(chartName, vf);
  const isCircular = !isTableChart && !isKpiChart && isIbCircularChart(chartName, vf);
  const [lockedWidth, setLockedWidth] = useState(null);
  const observe = !lockedWidth && (width == null || (!compact && height == null));
  const { width: measuredWidth, height: measuredHeight } = useChartContainerSize(
    containerRef,
    { width, height, observe: observe && !isTableChart },
  );
  const hasVfError = !isTableChart && !String(vf || "").trim();

  useEffect(() => {
    setLockedWidth(null);
  }, [id, vf]);
  useEffect(() => {
    if (compact && !lockedWidth && !isTableChart && measuredWidth > MIN_CHART_SIZE) {
      setLockedWidth(measuredWidth);
    }
  }, [compact, lockedWidth, measuredWidth, isTableChart]);

  useEffect(() => {
    if (isTableChart) {
      onPreviewError?.(false);
      return;
    }
    if (hasVfError) onPreviewError?.(true);
  }, [vf, id, isTableChart, hasVfError, onPreviewError]);

  if (isTableChart) {
    return (
      <div
        ref={containerRef}
        className={`chart-wrapper chart-wrapper--table${compact ? " chart-wrapper--compact" : ""} ${className}`.trim()}
        style={width == null ? { width: "100%" } : { width }}
      >
        <div className="json-data-viewer">
          <CommonMarkdownTable data={data || []} />
        </div>
      </div>
    );
  }

  if (hasVfError) {
    return (
      <IbResponseError
        className={className}
        details={parseBackendErrorMessage(backendError)}
      />
    );
  }

  const { width: chartWidth, height: chartHeight } = getIbChartViewportSize({
    width: lockedWidth || measuredWidth,
    height: measuredHeight,
    compact,
    isKpiChart,
  });

  return (
    <div
      ref={containerRef}
      className={`chart-wrapper${compact ? " chart-wrapper--compact" : ""}${
        isKpiChart ? " chart-wrapper--kpi" : ""
      }${isCircular ? " chart-wrapper--circular" : ""} ${className}`.trim()}
      style={
        lockedWidth
          ? { width: lockedWidth, maxWidth: lockedWidth, height: isKpiChart ? undefined : chartHeight }
          : {
              ...(width == null ? { width: "100%" } : { width }),
              ...(chartHeight != null && !isKpiChart ? { height: chartHeight } : null),
            }
      }
    >
      <div className="chart-wrapper__content" style={{ width: "100%", height: "100%" }}>
        {chartWidth > MIN_CHART_SIZE ? (
          <IBCustomChart
            data={data}
            showToolbar={false}
            customChart={{ code: vf }}
            dataId={id}
            autoFit
            compact={compact}
            isKpiChart={isKpiChart}
            chartName={chartName}
            onPreviewError={onPreviewError}
            chartAreaWidth={chartWidth}
            chartAreaHeight={chartHeight}
          />
        ) : null}
      </div>
    </div>
  );
};

// {activeTab === "preview" && (
//   <ChartView data={data} vf={vf} id={id} width={300} />
// )}

{/* <Modal
  title="Chart Preview"
  open={isMaximized}
  onCancel={() => setIsMaximized(false)}
  width={"95%"}
  style={{ top: 20 }}
  footer={null}
>
  <ChartView data={data} vf={vf} id={id} width={1200} />
</Modal> */}