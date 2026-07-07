import { ArrowLeftOutlined } from "@ant-design/icons";
import { Image, Skeleton, Tooltip } from "antd";
import React, { useEffect, useMemo, useRef, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { v4 as uuidv4 } from "uuid";
import { useWindowSize } from "../../../customHooks/useWindowSize.js";
import ErrorFallback from "../../../errorBoundry/ErrorFallback.jsx";
import { loadReportData, setAbortRequest, setHReportEditLoading, setHReportLoading, setHreportStylesId, updateReportModal } from "../../../redux/actions/hreport.actions.js";
import {
  getRelativeCacheTime,
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
import KPICard from "./card/kpi-card.jsx";
import Watermark from "./watermark/watermark.jsx";


const VizArea = (props) => {
  const dispatch = useDispatch();
  const chartAreaRef = useRef();
  const [containerHeight, setContainerHeight] = useState(10);
  const [containerWidth, setContainerWidth] = useState(10);
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
  const metaInfo = useSelector((state) => (state.app.applicationSettingsData.meta || null));

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
    tableFilters
  } = report || {};
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
  let chartAreaHeight = containerHeight;
  let chartAreaWidth = containerWidth;

  if (["create", "edit"].includes(mode) && fieldsAreaShelf) {
    chartAreaHeight = chartAreaHeight - 60;
  }
  if (reportModal) {
    chartAreaHeight = offsetHeight - 160;
    chartAreaWidth = offsetWidth - 160;
  }

  let hreportCustomStyles = {
    height: chartAreaHeight,
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
    if (mode !== 'dashboard') {
      let parentContainer = document.getElementById("hi-editing-area-container")
      const styleElements = parentContainer.getElementsByTagName('style');
      if (styleElements) {
        for (const styleElement of styleElements) {
          parentContainer.removeChild(styleElement);
        }
      }
    }
    if (cssData) {
      if (parent.contains(styleChildElement)) {
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
        className="hi-editing-area-container"
        id="hi-editing-area-container"
      >
        {content}
        {metaInfo ?
          <Watermark
            text={metaInfo.productName || "Helical Insight"}
            link={metaInfo.link}
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
