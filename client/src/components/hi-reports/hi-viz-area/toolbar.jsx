import { useSelector, useDispatch } from "react-redux";
import {
  CaretLeftOutlined,
  CaretRightOutlined,
  SyncOutlined,
  ArrowLeftOutlined,
  ArrowRightOutlined,
  RiseOutlined,
  FullscreenOutlined
} from "@ant-design/icons";

// import { addFilterToContainer, removeFilterFromContainer } from '../utils/report-actions';
import { Space, Tooltip } from "antd";
import {
  deleteDilldownFilter,
  changeInteractivity,
  updateReportModal,
  removeDrillthroughChildData,
} from "../../../redux/actions/hreport.actions";
import { fetchReport, generateReport, setActiveReport } from "../utils/base";
const Toolbar = (props) => {
  const dispatch = useDispatch();
  let { reportId } = props;
  const reports = useSelector((state) => state.hreport.present.reports);
  const { user = {} } = useSelector((state) => state.app.applicationSettingsData.userData);
  const activeReport = useSelector((state) => {
    let activeReport = state.hreport.present.reports.find(
      (report) => report.id === reportId
    );
    return activeReport;
  });
  const {
    active,
    drillDownList,
    currentDrillDown,
    toolbarConfig,
    selectedType,
    interactiveMode,
    drillDown,
    drillThrough,
    drillThroughList,
    marksList,
    mode,
    reportModal,
    drillThroughId,
    activeDrillthroughId,
    isDrillthroughActive
  } = activeReport;
  let hidePrev = drillThroughList.length && drillThroughList[0].active;
  let hideNext = (drillThroughList.length && drillThroughList[drillThroughList.length - 1].active) || !isDrillthroughActive;
  let hideReset = drillThroughList.length && drillThroughList[0].active;
  const drillReport = async (index) => {
    if (hidePrev && index === -1) {
      return null;
    }
    let tempDrillThroughList = setActiveReport({
      list: drillThroughList,
      index,
      updatedData: activeReport,
    });
    let currentReport = tempDrillThroughList.find((item) => item.active);
    let { reportInfo, drillThroughId } = currentReport;
    let saveData = reports.find(report => report.id === drillThroughId)
    reportInfo = reportInfo || activeReport.reportInfo;
    if (!saveData) {
      let file = {
        path: reportInfo.location + "/" + reportInfo.uuid,
        name: reportInfo.uuid,
      };
      let res = await fetchReport({ file, mode: "open" }, dispatch)
      // let res = await fetchReport({ file, mode: "edit" }, dispatch)
      saveData = { ...res, drillThrough: true };
    }
    if (!active) {
      dispatch(removeDrillthroughChildData({ id: reportId }))
    }

    if (saveData.drillThrough && saveData.drillThroughList[1] && ![0, -1].includes(index)) { // 7778 fix
      let childDrillThroughItem = saveData.drillThroughList[1]
      if (!tempDrillThroughList.map(item => item?.drillThroughId).includes(childDrillThroughItem.drillThroughId)) {
        tempDrillThroughList = [...tempDrillThroughList, childDrillThroughItem]
      } else {
        tempDrillThroughList = [...saveData.drillThroughList]
      }
    }

    let rId = tempDrillThroughList[0].drillThroughId
    saveData = {
      ...saveData,
      drillThrough: true,
      drillThroughList: tempDrillThroughList,
      loadState: true,
      fromDrillThrough: true,
      id: rId,
      drillThroughId
    };
    generateReport({ ...saveData, user }, dispatch, props.getApi);
  };

  const getDrilldownFilter = (currentDrillDown, drillDownList, activeReport) => {
    let drillDownFilter = drillDownList.find((item) => item.drillDownId === currentDrillDown);
    const { columnID = '' } = drillDownFilter || {}
    const mainReportFilter = activeReport?.filters?.find((fltr) => fltr.columnID === columnID);
    if (mainReportFilter) {
      return mainReportFilter;
    }
    return drillDownFilter;
  }

  const handleDrillDown = (e) => {
    if (e === 0) {
      let drillDownIds = drillDownList.map((clmn) => clmn.drillDownId);
      const filter = getDrilldownFilter(currentDrillDown, drillDownList, activeReport);
      props.refreshFiltersIPC({ drillDownIds, currentDrillDown: "", reportId, filter });
    }
    if (e === 1) {
      let currentIndex = drillDownList.findIndex(
        (item) => item.drillDownId === currentDrillDown
      );
      let newFltr = drillDownList[currentIndex + 1];
      props.addForwardFilterIPC({ newFltr, reportId });
    }
    if (e === -1) {
      let currentIndex = drillDownList.findIndex(
        (item) => item.drillDownId === currentDrillDown
      );
      let prevId = drillDownList[currentIndex - 1]
        ? drillDownList[currentIndex - 1].drillDownId
        : "";
      dispatch(
        deleteDilldownFilter({
          drillDownIds: [currentDrillDown],
          currentDrillDown: prevId,
          reportId,
        })
      );
      props.deleteBackwardFilterIPC({
        drillDownIds: [currentDrillDown],
        currentDrillDown: prevId,
        reportId
      });
    }
  };
  let findIndex = drillDownList.findIndex(
    (item) => item.drillDownId === currentDrillDown
  );
  let nextDrillDown =
    !drillDownList.length || findIndex === drillDownList.length - 1;
  let prevDrillDown = !drillDownList.length || findIndex === -1;
  let resetDrilldown = !currentDrillDown;
  // let marginRight = "7px";
  // if (mode === "open") {
  //     marginRight = "50px";
  // }
  // if (_uid) {
  //     marginRight = "25px";
  // }
  // if(prevDrillDown && nextDrillDown && resetDrilldown && !hidePrev && !hideNext && !hideReset){
  //     return null
  // }
  let { selectable } = toolbarConfig;
  const handleChange = () => {
    dispatch(
      changeInteractivity({
        interactiveMode,
        drillDown,
        drillThrough,
        toolbarConfig: { selectable: !selectable },
        reportId,
      })
    );
  };

  const checkIfModeIsDashboard = (id, reports) => {
    let report = reports.find((report) => report?.activeDrillthroughId === id);
    if (!report) return false;
    if (['dashboard'].includes(report?.mode)) return true;
    return false;
  }

  let hideSelection = marksList[0].subVizType === "arc" || marksList[0].subVizType === "doughnut"
  let showDrillthroughMoreIcons = drillThroughId && drillThroughList?.length && checkIfModeIsDashboard(drillThroughId, reports);
  const handleMaximizeReport = () => {
    dispatch(updateReportModal({ open: true, reportId }));
  }

  if (reportModal) return null;
  return (
    <>
      <Space align="baseline" className="toolbar-container">
        {(selectedType === "GridChart" && !hideSelection) && (
          <Tooltip placement = "left" title={"Select  multiple values"}>
            <span
              style={{ opacity: selectable ? "1" : "0.5" }}
              onClick={() =>
                handleChange(["toolbarConfig", "selectable"], !selectable)
              }
            >
              <RiseOutlined />
            </span>
          </Tooltip>
        )}
        {drillDown && drillDownList.length ? (
          <>
            <Tooltip placement='topLeft' title={"Go back one drill down step"}> {/*6229*/}
              <span
                style={{ opacity: !prevDrillDown ? "1" : "0.5" }}
                onClick={() => handleDrillDown(-1)}
              >
                <ArrowLeftOutlined />
              </span>
            </Tooltip>
            <Tooltip placement='topLeft' title={"Go forward one drill down step "}>  {/*6229*/}
              <span
                style={{ opacity: !nextDrillDown ? "1" : "0.5" }}
                onClick={() => handleDrillDown(1)}
              >
                <ArrowRightOutlined />
              </span>
            </Tooltip>
            <Tooltip placement='topLeft' title={"Reset drill down report to initial state"}>  {/*6229*/}
              <span
                style={{ opacity: !resetDrilldown ? "1" : "0.5" }}
                onClick={() => handleDrillDown(0)}
              >
                <SyncOutlined />
              </span>
            </Tooltip>
          </>
        ) : null}
        {drillThroughList.length > 1 ? (
          <>
            <Tooltip title={"Go back one drill through step"}>
              <span
                data-testid="drill-through-prev-btn"
                style={{ opacity: hidePrev ? "0.5" : "1", cursor: "pointer" }}
                onClick={() => drillReport(-1)}
              >
                <CaretLeftOutlined />
              </span>
            </Tooltip>
            <Tooltip title={"Go forward one drill through step"}>
              <span
                data-testid="drill-through-next-btn"
                style={{ opacity: hideNext ? "0.5" : "1", cursor: "pointer" }}
                onClick={() => hideNext ? null : drillReport(1)}
              >
                <CaretRightOutlined />
              </span>
            </Tooltip>
            <Tooltip title={"Reset drill through report to initial state"}>
              <span
                style={{ opacity: hideReset ? "0.5" : "1", cursor: "pointer" }}
                onClick={() => hideReset ? null : drillReport(0)}
              >
                <SyncOutlined />
              </span>
            </Tooltip>
            {showDrillthroughMoreIcons && !["edit"].includes(mode) ?
              <Tooltip title={"Maximize report"}>
                <span
                  style={{ cursor: "pointer" }}
                  onClick={handleMaximizeReport}
                >
                  <FullscreenOutlined />
                </span>
              </Tooltip>
              : null}
          </>
        ) : null}
      </Space>
    </>
  );
};

export default Toolbar;
