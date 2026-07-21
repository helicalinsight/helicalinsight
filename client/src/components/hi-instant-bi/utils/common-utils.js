import { ConsoleSqlOutlined, DatabaseOutlined, EyeOutlined, TableOutlined } from "@ant-design/icons";
import { useEffect, useRef, useState } from "react";
import { v4 as uuidv4 } from "uuid";
import IBCustomChart, { IB_CHART_RENDER_ERROR } from "../components/ib-custom-chart";
import CommonMarkdownTable from "./common-markdown-table";

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

const COMPACT_CHART_ASPECT_RATIO = 400 / 1200;
const CHART_PREVIEW_MODAL_HEIGHT_OFFSET = 140;
const MIN_CHART_SIZE = 1;

export const getChartPreviewModalHeight = () =>
  typeof window !== "undefined"
    ? Math.max(window.innerHeight - CHART_PREVIEW_MODAL_HEIGHT_OFFSET, 400)
    : 600;

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
        if (prev.width === nextWidth && prev.height === nextHeight) return prev;
        return { width: nextWidth, height: nextHeight };
      });
    };
    update();
    const observer = new ResizeObserver(update);
    observer.observe(el);
    return () => observer.disconnect();
  }, [containerRef, width, height, observe]);
  return {
    width: width ?? size.width,
    height: height ?? size.height,
  };
};

export const isIbKpiChart = (chartName = "", vf = "") => {
  const name = String(chartName).toLowerCase();
  if (name === "kpi" || name === "other") return true;
  return /function\s+Draw(?:KPI|Other)\b/.test(String(vf));
};

export const isIbTableChart = (chartName = "", vf = "") => {
  if (String(chartName).toLowerCase() === "table") return true;
  const vfText = String(vf);
  return (
    /function\s+DrawTable\b/.test(vfText) ||
    /<Table[\s/>]/.test(vfText) ||
    /\bGridTable\b/.test(vfText)
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

const IBPreviewErrorMessage = ({ className = "", backendError }) => {
  const [showDetails, setShowDetails] = useState(false);
  const errorMessage = parseBackendErrorMessage(backendError);
  const hasDetails = Boolean(errorMessage);

  return (
    <div className={`ib-response-error ${className}`.trim()}>
      <span>{IB_CHART_RENDER_ERROR}</span>
      {hasDetails && (
        <>
          {" "}
          <button
            type="button"
            className="ib-response-error__details-link"
            onClick={() => setShowDetails((prev) => !prev)}
          >
            {showDetails ? "Hide Details" : "View Details"}
          </button>
        </>
      )}
      {showDetails && hasDetails && (
        <div className="ib-response-error__details">{errorMessage}</div>
      )}
    </div>
  );
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
  const observeSize = width == null || (!compact && height == null);
  const { width: measuredWidth, height: measuredHeight } = useChartContainerSize(
    containerRef,
    { width, height, observe: observeSize && !isTableChart },
  );
  const [hasError, setHasError] = useState(
    () => !isTableChart && !String(vf || "").trim(),
  );

  useEffect(() => {
    if (isTableChart) {
      setHasError(false);
      onPreviewError?.(false);
      return;
    }
    const hasVfError = !String(vf || "").trim();
    setHasError(hasVfError);
    onPreviewError?.(hasVfError);
  }, [vf, id, isTableChart, onPreviewError]);

  const handlePreviewError = (error) => {
    setHasError(Boolean(error));
    onPreviewError?.(error);
  };

  if (isTableChart) {
    return (
      <div
        ref={containerRef}
        className={`chart-wrapper chart-wrapper--table${compact ? " chart-wrapper--compact" : ""} ${className}`.trim()}
        style={{
          ...(width == null ? { width: "100%" } : { width }),
        }}
      >
        <div className="json-data-viewer">
          <CommonMarkdownTable data={data || []} />
        </div>
      </div>
    );
  }

  if (hasError) {
    return (
      <IBPreviewErrorMessage className={className} backendError={backendError} />
    );
  }

  const effectiveWidth = Math.max(measuredWidth, MIN_CHART_SIZE);
  const compactHeight =
    compact && !isKpiChart
      ? Math.max(effectiveWidth * COMPACT_CHART_ASPECT_RATIO, MIN_CHART_SIZE)
      : undefined;
  const effectiveHeight = compact
    ? compactHeight
    : Math.max(measuredHeight || getChartPreviewModalHeight(), MIN_CHART_SIZE);
  const canRenderChart = effectiveWidth > MIN_CHART_SIZE;

  const chart = canRenderChart ? (
    <IBCustomChart
      data={data}
      showToolbar={false}
      customChart={{ code: vf }}
      dataId={id}
      autoFit={true}
      compact={compact}
      isKpiChart={isKpiChart}
      onPreviewError={handlePreviewError}
      chartAreaWidth={effectiveWidth}
      chartAreaHeight={effectiveHeight}
    />
  ) : null;

  return (
    <div
      ref={containerRef}
      className={`chart-wrapper${compact ? " chart-wrapper--compact" : ""}${
        isKpiChart ? " chart-wrapper--kpi" : ""
      } ${className}`.trim()}
      style={{
        ...(width == null ? { width: "100%" } : { width }),
        ...(compactHeight != null ? { height: compactHeight } : undefined),
      }}
    >
      <div className="chart-wrapper__content" style={{ width: "100%", height: "100%" }}>
        {chart}
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