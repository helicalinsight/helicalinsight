import { ArrowLeftOutlined } from "@ant-design/icons";
import { Image, Skeleton, Tooltip } from "antd";
import React, { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { v4 as uuidv4 } from "uuid";
import { useWindowSize } from "../../../customHooks/useWindowSize.js";
import ErrorFallback from "../../../errorBoundry/ErrorFallback.jsx";
import { loadReportData, setAbortRequest, setHReportEditLoading, setHReportLoading, setHreportStylesId, setReportSemantic, updateReportModal } from "../../../redux/actions/hreport.actions.js";
import { exploredSemanticPayload } from "../../bridges/hreport/utils";
import {
  getRelativeCacheTime,
  isOpenSource,
  modifyFilters,
} from "../../../utils/utilities.js";
import LoadingBar from "../../common/components/hi-loading-bar.jsx";
import TutorialInfo from "../../common/hi-tutorial/index.jsx";
import HIIcon from "../../common/icons/hi-icons.jsx";
import { makeid } from "../../hi-dashboard-designer/utils/common-functions.js";
import notify from "../../hi-notifications/notify.js";
import { errorHandlingLiquidJs } from "../hi-editing-area/components/editor/css-editor.jsx";
import "../hi-reports-print.scss";
import { generateReport } from "../utils/base.js";
import AntChart from "./ant-charts/ant-chart.jsx";
import { getAntChartTheme } from "./ant-charts/ant-utils.js";
import { defaultColorPaletteSchemes } from "./ant-charts/constants.js";
import CustomChart from "./custom-charts/custom-chart.jsx";
import GridChart from "./grid-chart/grid-chart.jsx";
import "./hi-viz-area.scss";
import MapChart from "./map-chart/map-chart.jsx";
import PivtChart from "./pivot-view/pivot-chart.jsx";
import HIReportModal from "./report-modal/hi-report-modal.jsx";
import S2chart from "./s2-charts/s2chart.jsx";
import TableContainer from "./table/table.jsx";
import Toolbar from "./toolbar.jsx";
import ExploredChartChrome from "./explored-chart-chrome.jsx";
import KPICard from "./card/kpi-card.jsx";
import Watermark from "./watermark/watermark.jsx";


const VizArea = (props) => {
  const dispatch = useDispatch();
  const chartAreaRef = useRef();
  const [containerHeight, setContainerHeight] = useState(10);
  const [containerWidth, setContainerWidth] = useState(10);
  const [insightHeight, setInsightHeight] = useState(0);
  const [explaining, setExplaining] = useState(false);
  const [liveInsight, setLiveInsight] = useState("");
  const explainRequestRef = useRef(null);
  const [offsetWidth, offsetHeight] = useWindowSize();
  // const [isAborted, setIsAborted] = useState(false);
  let { reportId, fieldsAreaShelf, parameters, activeDrillthroughId, isPrintMode, resizing, handleExport = () => { }, handlePrintExport = () => { }, downloadingInfoModal = null } = props;
  reportId = activeDrillthroughId || reportId
  const editModeInfo = useSelector((state) => state.app.editModeInfo);
  const { user = {} } = useSelector((state) => state.app.applicationSettingsData.userData);
  const report = useSelector((state) => {
    let activeReport = state.hreport.present.reports.find(
      (report) => report.id === reportId
    );
    return activeReport;
  });
  const { chartColorPalette = {} } = report || {}
  const { chartTheme: { colorPalette = {} } = {}, canvas: canvasProperties = {}, tooltip: { showTooltip = true, tooltipTemplate = "", enableTemplate = false } = {} } = report?.reportData?.properties || {};
  let themeColors = getAntChartTheme(colorPalette, { ...chartColorPalette, ...defaultColorPaletteSchemes });
  const hreportEditLoading = useSelector((state) => state.hreport.present.hreportEditLoading)
  const userData = useSelector(
    (state) => state.app.applicationSettingsData.userData
  );
  const metaInfo = useSelector((state) => (state.app.applicationSettingsData.meta || {}));
  const openSource = isOpenSource(metaInfo);

  let { serverTime, clientTime } = userData || {}
  let {
    reportData,
    interactiveMode,
    drillDown,
    drillThrough,
    currentDrillDown,
    drillDownList,
    toolbarConfig,
    scripts,
    options,
    mode,
    fullscreen,
    combine,
    analytics,
    selectedType: reportSelectedType,
    hreportLoading,
    stylesId,
    styles,
    savedStyles,
    isAborted,
    referenceLineList,
    customChart,
    measures,
    reportModal,
    tableFilters,
    instantSemantic,
    activeTool,
  } = report || {};
  const exploredInsight = String(instantSemantic?.insight || "").trim();
  const showExploredChrome = !isPrintMode && Boolean(instantSemantic?.reportModel || exploredInsight);
  const explainChatId = String(instantSemantic?.chatId || "").trim();
  const explainSequenceId = String(instantSemantic?.chatSequenceId || "").trim();
  // Explain is skipped unless both ids were stored with the explored chart.
  const canExplain = Boolean(explainChatId && explainSequenceId);
  useEffect(() => {
    setLiveInsight(exploredInsight);
  }, [exploredInsight]);
  useEffect(() => () => explainRequestRef.current?.abort?.(), []);
  const handleInsightHeight = useCallback((height) => {
    setInsightHeight((prev) => (prev === height ? prev : height));
  }, []);
  useEffect(() => {
    if (drillDownList && drillDownList.length) {
      let modifiedState = modifyFilters(report, parameters);
      generateReport({ ...modifiedState, user }, dispatch, props.getApi);
      // generateReport({ ...modifiedState }, dispatch,props.getApi);
    }
  }, [currentDrillDown]);
  useEffect(() => {
    if (editModeInfo) {
      let { dir, file, extension } = editModeInfo;
      if (extension === "hr") {
        props.editReport({ path: dir, name: file });
      }
    }
  }, [editModeInfo])
  useEffect(() => {
    function outputsize() {
      if (chartAreaRef?.current?.parentNode) {
        let parentNode = chartAreaRef.current.parentNode;
        if (parentNode.offsetHeight - 10 !== containerHeight) {
          setContainerHeight(parentNode.offsetHeight - 10);
        }
        if (parentNode.offsetWidth - 5 !== containerWidth) {
          setContainerWidth(parentNode.offsetWidth - 5);
        }
      }
    }
    if (chartAreaRef?.current?.parentNode && process.env.NODE_ENV !== "test") {
      new ResizeObserver(outputsize).observe(chartAreaRef.current.parentNode);
    }
  }, []);

  useEffect(() => {
    window.addEventListener("beforeunload", handleUnload);
    return () => {
      window.removeEventListener("beforeunload", handleUnload);
    };
  }, []);

  useEffect(() => {
    if (!stylesId && reportId) {
      dispatch(setHreportStylesId({ id: `hi-report-${reportId?.split("-")[0]}` }))
    }
  }, [reportId])

  const handleUnload = (e) => {
    if (mode !== "edit" && mode !== "create") return null
    const message = "o/";
    (e || window.event).returnValue = message; //Gecko + IE
    return message;
  };
  let {
    data,
    metadata,
    dataId,
    offset,
    limitBy,
    pageSize,
    message,
    invalid,
    lastModified,
    loading,
    filters,
    fields,
    marksList,
    properties,
    selectedType,
  } = reportData || {};
  useEffect(() => {
    props.getLastModfified({ lastModified });
  }, [lastModified]);
  let availableHeight = containerHeight;
  let chartAreaWidth = containerWidth;

  if (["create", "edit"].includes(mode) && fieldsAreaShelf) {
    availableHeight = availableHeight - 60;
  }
  if (reportModal) {
    availableHeight = offsetHeight - 160;
    chartAreaWidth = offsetWidth - 160;
  }
  // The insight is a row under the chart. The drawn chart uses the height that remains.
  const chartAreaHeight = insightHeight > 0
    ? Math.max(120, availableHeight - insightHeight)
    : availableHeight;

  let hreportCustomStyles = {
    height: availableHeight,
    overflow: "hidden"
  }


  function changeAborted() {
    dispatch(setAbortRequest(true))
  }

  function handleAbort(requestIdToCancel, requestId) {
    props.abortFetchData()
    dispatch(setHReportEditLoading(false))
    dispatch(setHReportLoading(false))
    // setIsAborted(true);
    changeAborted()
  }
  let vizProps = {
    selectedType,
    data,
    metadata,
    dataId,
    fields,
    filters,
    properties,
    marksList,
    interactiveMode,
    drillDown,
    drillThrough,
    toolbarConfig,
    reportId,
    chartAreaHeight,
    chartAreaWidth,
    options,
    addFilter: props.addFilter,
    addForwardFilterIPC: props.addForwardFilterIPC,
    deleteBackwardFilterIPC: props.deleteBackwardFilterIPC,
    refreshFiltersIPC: props.refreshFiltersIPC,
    getApi: props.getApi,
    combine,
    scripts,
    report,
    mode,
    analytics: analytics?.[0],
    isPrintMode,
    referenceLineList,
    customChart,
    measures,
    chartColorPalette,
    themeColors,
    canvasProperties,
    showTooltip,
    tooltipTemplate,
    enableTemplate,
    tableFilters,
  };
  const fetchMoreData = ({ tableLimitBy, offset, pageSize, fetchAllRecords }) => {
    let modifiedState = modifyFilters(report, parameters);
    // modifiedState = modifyTableProperties(modifiedState, pageSize);
    if (fetchAllRecords) {
      const { reportData = {} } = modifiedState || {};
      dispatch(loadReportData({ ...modifiedState, limitBy: tableLimitBy, pageSize, offset, reportData: { ...reportData, limitBy: tableLimitBy, pageSize, offset, dataId: uuidv4() } }));
      return;
    }
    generateReport(
      { ...modifiedState, tableLimitBy, offset, pageSize, id: reportId, user, isPrintMode },
      dispatch, props.getApi
    );
  };
  let tableProps = { offset, limitBy, pageSize, fetchMoreData };

  let timeFrom = getRelativeCacheTime(lastModified, serverTime, clientTime);

  const handleBack = () => {
    generateReport({ ...report, user }, dispatch, props.getApi);
  }

  const getStylesId = () => {
    if (stylesId) return stylesId
    return `hi-report-${reportId?.split("-")[0]}`
  }

  const Notify = notify(dispatch);
  const handleExplain = () => {
    if (!canExplain || explaining) return;
    setExplaining(true);
    // Loaded on click so the chart canvas does not import the chat request module at startup.
    import("../../hi-instant-bi/utils/instant-bi-requests").then(({ loadChartDataInsight }) => {
      explainRequestRef.current?.abort?.();
      explainRequestRef.current = loadChartDataInsight({
        hreport: report,
        dispatch,
        Notify,
        reportId,
        chatId: explainChatId,
        chatSequenceId: explainSequenceId,
        userInput: String(instantSemantic?.userInput || "").trim(),
        agent: instantSemantic?.agent || {},
        existingChatResponse: {},
        // The result is written onto this report. The chat message keeps its own copy.
        skipDispatch: true,
        onComplete: (result) => {
          setExplaining(false);
          explainRequestRef.current = null;
          const nextInsight = String(result?.insight || result?.response?.insight || "").trim();
          if (!nextInsight) return;
          setLiveInsight(nextInsight);
          dispatch(setReportSemantic({
            reportId,
            semanticDetails: exploredSemanticPayload({
              ...(instantSemantic || {}),
              insight: nextInsight,
            }),
          }));
        },
      });
    }).catch(() => setExplaining(false));
  };
  const cssData = useMemo(
    () =>
      errorHandlingLiquidJs({
        value: savedStyles,
        Notify
      }),
    [savedStyles]
  );

  const handleApplyCss = (cssData) => {
    let styleElement = document.createElement("style");
    let dynamicId = `hreport-dynamic-css-${makeid({ hreport: true })}`
    styleElement.setAttribute("id", dynamicId);
    styleElement.innerHTML = cssData;
    let parent = document.getElementById("hi-editing-area-container");
    const styleChildElement = document.getElementById(dynamicId);

    // fix for 6779
    // getElementsByTagName includes nested style tags, and removeChild throws unless the node is a direct child.
    if (mode !== 'dashboard' && parent) {
      Array.from(parent.children).forEach((child) => {
        if (child.tagName === "STYLE" && String(child.id || "").startsWith("hreport-dynamic-css-")) {
          child.remove();
        }
      });
    }
    if (!parent) return;
    if (cssData) {
      if (styleChildElement && parent.contains(styleChildElement)) {
        parent.removeChild(styleChildElement);
      }
      parent.appendChild(styleElement);
    } else {
      document.getElementById(dynamicId)?.remove();
    }
  }

  const handleReportModalClose = () => {
    dispatch(updateReportModal({ open: false, reportId }));
  }

  useEffect(() => {
    handleApplyCss(cssData)
  }, [savedStyles])

  let content = null;
  if (isAborted) {
    content = (
      <div className="muze-message-container">
        <Tooltip Tooltip title="Go back to previous view" >
          <span className="abort-return-button" onClick={handleBack}>
            <ArrowLeftOutlined />
          </span>
        </Tooltip >
        <div className="viz-invalid-state"> No Records/No Data </div>
      </div >
    );
  } else if (!selectedType) {
    content = (
      <TutorialInfo elementKey="hr-editing-area">
        <Image
          preview={false}
          className="hi-overview-image"
          width={"100%"}
          style={{ height: `${chartAreaHeight}px`, border: "1px solid #ddd", position: "absolute" }}
          src="images/hi-reports/Drag&drop_reports.png"
        />
      </TutorialInfo>
    );
  } else {
    content = (
      <div
        className="hi-report-container"
        id={getStylesId()}
        style={{ background: fullscreen ? "#fff" : "" }}
      >
        {!resizing && <VizContainer {...vizProps} {...tableProps} />}
        {mode !== "dashboard" && (
          <div className="hr-cache-time">
            <span
              className="hr-cache-time-text"
              data-testid="hr-cache-time-text"
            >
              {" Last Cached: "}
              {timeFrom}
            </span>
            <span
              className="hr-cache-time-icon"
              data-testid="hr-cache-time-icon"
            >
              <HIIcon name="hi-hour-glass" />
            </span>
          </div>
        )}
      </div>
    );
  }

  if (mode !== "edit" && mode !== "create" && loading) {
    content = <>
      <LoadingBar handleClick={props.abortFetchData} />
      <Skeleton />
    </>
  }

  if (hreportLoading || hreportEditLoading) {
    content = <>
      <LoadingBar handleClick={handleAbort} />
      <Skeleton />
    </>
  }



  if (selectedType !== "Table" && data && data.length === 0) {
    invalid = true
    message = message || "No Records/ no data"
  }
  if (selectedType === "Table" && !data?.length && message?.length) {
    invalid = true
    message = message
  }
  if (invalid) {
    content = (
      <div className="muze-message-container">
        <div style={{ position: "relative" }} >
          <Toolbar
            addForwardFilterIPC={props.addForwardFilterIPC}
            deleteBackwardFilterIPC={props.deleteBackwardFilterIPC}
            refreshFiltersIPC={props.refreshFiltersIPC}
            reportId={reportId}
          />
        </div>
        <div className="viz-invalid-state"> {message} </div>
      </div>
    );
  }
  if (invalid && reportSelectedType === "Table") {
    content = (
      <div className="muze-message-container">
        <Tooltip Tooltip title="Go back to previous view" >
          <span className="abort-return-button" onClick={handleBack}> <ArrowLeftOutlined /> </span>
        </Tooltip>
        {/* <div style={{position:"relative"}} >
          <Toolbar
            addForwardFilterIPC={props.addForwardFilterIPC}
            deleteBackwardFilterIPC={props.deleteBackwardFilterIPC}
            refreshFiltersIPC={props.refreshFiltersIPC}
            reportId={reportId}
          />
        </div> */}
        <div className="viz-invalid-state"> {message} </div>
      </div>
    );
  }

  if (selectedType === "CrossTab") {
    content = <div className="viz-invalid-state">Crosstab has been removed, please switch to Grid table.</div>
  }

  return (
    <ErrorFallback
      {...props}
      errMessage="Invalid data. Please use proper configuration"
      errTemplate={
        <div className="muze-message-container" style={{ height: `${chartAreaHeight}px` }}>
          <div className="viz-invalid-state"> Invalid data. Please use proper configuration </div>
        </div>
      }
    >
      <div
        style={!isPrintMode ? hreportCustomStyles : { overflow: "visible", height: "100%" }}
        ref={chartAreaRef}
        className={`hi-editing-area-container${showExploredChrome ? " hi-editing-area-container--explored" : ""}`}
        id="hi-editing-area-container"
      >
        {showExploredChrome ? (
          <ExploredChartChrome
            insight={liveInsight || exploredInsight}
            explaining={explaining}
            canExplain={canExplain}
            onExplain={handleExplain}
            onInsightHeight={handleInsightHeight}
            activeTool={activeTool}
          >
            {content}
          </ExploredChartChrome>
        ) : content}
        {openSource ?
          <Watermark
            text={`Powered by ${metaInfo.productName}©${metaInfo.version}`}
            link={metaInfo.link || "https://www.helicalinsight.com/"}
            placement="bottom-right"
            tooltip="Please upgrade your license to remove this watermark."
          />
          : null}
      </div>

      <HIReportModal
        open={reportModal}
        onClose={handleReportModalClose}
        reportId={reportId}
        {...{
          handleExport,
          handlePrintExport,
          dispatch,
          report,
          downloadingInfoModal
        }}>
        {content}
      </HIReportModal>
    </ErrorFallback>
  );
};
const areEqual = (prevProps, nextProps) => {
  if (
    prevProps.dataId !== nextProps.dataId ||
    prevProps.chartAreaHeight !== nextProps.chartAreaHeight ||
    prevProps.chartAreaWidth !== nextProps.chartAreaWidth ||
    prevProps.isPrintMode !== nextProps.isPrintMode ||
    prevProps.properties.table !== nextProps.properties.table
  ) {
    return false;
  } else {
    return true;
  }
};
const VizContainer = React.memo(props => {
  const dispatch = useDispatch()
  let { selectedType, customChart } = props
  const vizProps = { ...props }
  if (customChart?.selected && customChart?.applied) {
    return (
      <>
        <CustomChart {...vizProps} />
        <div className="hr-loaded-container" />
      </>
    )
  }

  return (
    <>
      {selectedType === "Table" && <TableContainer {...vizProps} />}
      {selectedType === "SyncChart" && <PivtChart {...vizProps} />}
      {selectedType === "GridChart" && <GridChart {...vizProps} />}
      {selectedType === "Antcharts" && <AntChart {...vizProps} />}
      {selectedType === "MapChart" && <MapChart {...vizProps} />}
      {selectedType === "S2Chart" && <S2chart {...vizProps} />}
      {selectedType === "Card" && <KPICard {...vizProps} />}
      <div className="hr-loaded-container"></div>
    </>
  )

}, areEqual)

export default VizArea;
