import {
  EditOutlined,
  ExportOutlined,
  FieldTimeOutlined,
  FileExcelOutlined,
  FilePdfOutlined,
  FunnelPlotOutlined,
  PushpinFilled,
  PushpinOutlined,
  ReloadOutlined,
  SyncOutlined,
  FilePptOutlined,
  FileImageOutlined
} from "@ant-design/icons";
import { Modal, Progress, Typography } from "antd";
import produce from "immer";
import { cloneDeep, isEqual } from "lodash-es";
import React, { useEffect, useRef, useState } from "react";
import { Responsive, WidthProvider } from "react-grid-layout";
import { useDispatch, useSelector } from "react-redux";
import { v4 as uuidv4 } from "uuid";
import { HINavbar, HITabs } from "../components";
import HIShortcuts from "../components/common/hi-shortcuts/HIShortcuts";
import HIIcon from "../components/common/icons/hi-icons";
import SaveItems from "../components/hi-fileBrowser/SaveItems";
import { ShareFinalModal } from "../components/hi-fileBrowser/components";
import { HIFileBrowser } from "../components/hi-fileBrowser/hi-fileBrowser";
import notify from "../components/hi-notifications/notify";
import EditingArea from "../components/hi-reports/hi-editing-area/hi-editing-area";
import FieldsArea from "../components/hi-reports/hi-fields-area/hi-fields-area";
import "../components/hi-reports/hi-reports.scss";
import VizArea from "../components/hi-reports/hi-viz-area/hi-viz-area";
import { checkIfDrillThrough, clearGridChart, addFilter as dilldownFilter } from "../components/hi-reports/hi-viz-area/utils/utillities";
import getHrTaskBarItems, {
  handleFullScreenClick,
  toggleToolsAreaShelf,
} from "../components/hi-reports/hr-taskbar-items";
import {
  fetchReport,
  generateReport,
  getGeoJsonData,
  loadChildReport,
  openCube,
  openMetadata,
  replaceStylesAndStylesIdInActiveReport,
  saveReportFile,
} from "../components/hi-reports/utils/base";
import { getMilliseconds, handleHrShortcuts, handleVizShortcuts, resetShortcuts } from "../components/hi-reports/utils/utilities";
import MetadataArea from "../components/hi-sidebar/hr-hreportSidebar/hi-metadata-area";
import useWindowResize from "../customHooks/useWindowResize";
import { useWindowSize } from "../customHooks/useWindowSize";
import HILayout from "../layouts/hi-layout";
import { appActions, fileBrowserActions, setKeysPressed, setShotCutCurrentLocation } from "../redux/actions";
import {
  addDilldownFilter,
  addNewReport,
  changeReport,
  clearUndoRedoHistory,
  createFilter,
  deleteDilldownFilter,
  loadIntialReport,
  loadReportData,
  removeAllReports,
  removeReport,
  resetActiveReport,
  setHReportEditLoading,
  setHrSidebar,
  updateFullScreenState,
  updateReportLayout,
  updateGridItemsLayout
} from "../redux/actions/hreport.actions";
import { checkRelativeDateFilter } from "../utils/filter-utils";
import {
  checkForAnchorRelativeParameters,
  checkWhetherStateIsLoaded,
  exportPrintedReport,
  exportReport,
  getFieldDisplayName,
  modifyFilters,
} from "../utils/utilities";
import { validateReportName } from "./utils/helperMethods";
import useHreportGridLayout from "../hooks/useHreportGridLayout";
import { getHreportInitialGridLayout } from "../redux/reducers/hreport.reducer";
import { getRelativeDateValues } from "../components/hi-reports/hi-editing-area/utils/filter-utils";
import useExportOptions from "../hooks/useExportOptions";

const { Text } = Typography;

let env = process.env.NODE_ENV;
const createReportId = () => {
  if (env === "test") {
    return "test_123";
  } else {
    return uuidv4();
  }
};

const ResponsiveGridLayout = WidthProvider(Responsive);
const isTestMode = process.env.NODE_ENV === "test";

const HelicalReports = (props) => {
  const dispatch = useDispatch();
  let { parameters } = props;
  const paramsRef = useRef({});
  const fileInfoRef = useRef(props.file);
  const shareRef = useRef(null);
  const apiRef = useRef(null);
  const generateRef = useRef(null);
  const cacheRefreshRef = useRef(null);
  const saveRef = useRef(null);
  const saveAsRef = useRef(null);
  const shareTaskBarItemRef = useRef(null);
  const previewRef = useRef(null);
  const addTabRef = useRef(null);
  const vizRef = useRef(null);
  const dropDownRef = useRef(null);
  const saveDropDownRef = useRef(null);
  const exportDropDownRef = useRef(null);
  const undoRef = useRef(null)
  const redoRef = useRef(null)

  const [, offsetHeight] = useWindowSize();
  const isWindowResizing = useWindowResize();
  const { getMenuOptions, getDropdownOptions } = useExportOptions();
  let [reportId, setReportId] = useState("");
  let [invalidFile, setInValidFile] = useState(null);
  let [downloadingInfo, setDownloadingInfo] = useState(null);
  const [filebrowserOptions, setShowFb] = useState(false);
  const [metadataFromSettings, setMetadataFromSettings] = useState(false);
  const [refs, setRefs] = useState({
    generate: generateRef,
    cacheRefresh: cacheRefreshRef,
    save: saveRef,
    saveAs: saveAsRef,
    share: shareTaskBarItemRef,
    preview: previewRef,
    dropDown: dropDownRef,
    saveDropDown: saveDropDownRef,
    exportDropDown: exportDropDownRef,
    undo: undoRef,
    redo: redoRef,
  });
  const [resizing, setResizing] = useState(false)
  let editModeInfo = useSelector((state) => state.app.editModeInfo);
  const isShareModalVisible = useSelector(
    (state) => state.fileBrowser.isShareModalVisible
  );
  const reports = useSelector((state) =>
    state.hreport.present.reports.filter((report) => !report.drillThroughId)
  );
  const activeReportId = useSelector((state) => state.hreport.present.activeReportId);
  const isSavingInProgress = useSelector((state) => state.hreport.present.isSavingInProgress);
  const sidebarLoading = useSelector((state) => state.hreport.present.sidebarLoading);
  const keysPressed = useSelector((state) => state.app.keysPressed);
  const currentSCLocation = useSelector((state) => state.app.currentSCLocation);

  // hreport past and future state 	
  const hreportPast = useSelector((state) => state.hreport.past);
  const hreportFuture = useSelector((state) => state.hreport.future);
  const { user = {} } = useSelector((state) => state.app.applicationSettingsData.userData);

  const [savedLayout, setSavedLayout] = useState(null);
  const [resizeByArea, setResizeByArea] = useState({
    "sidebar-area": false,
    "editing-area": false,
    "chart-area": false,
  });
  const hiAxiosRef = useRef(null);
  const [metadataChangeModalVisible, setMetadataChangeModalVisible] = useState(false);
  const [metadataToBeChange, setMetdataToBeChange] = useState(null)
  const isPrintMode = props?.parameters?.navigatorUserAgent === "print" && props?.parameters?.mode === "dashboard"
  const editModeInfoRef = useRef(null)

  //change the parameters for anchor realtive to [7142]
  parameters = checkForAnchorRelativeParameters(props?.mode === 'filter' ? props.allFilters : parameters);

  useEffect(() => {
    return () => {
      dispatch(clearUndoRedoHistory())
      if (!["dashboard", "filter"].includes(props?.mode)) {
        dispatch(removeAllReports())
      }
    }
  }, [])
  useEffect(() => {
    if (!isShareModalVisible) {
      shareRef.current = false;
    }
  }, [isShareModalVisible]);
  useEffect(() => {
    let reportId = createReportId();
    setReportId(reportId);
    let { file, dashboardFilter } = props;
    if (env === "development") {
      //  mode = "open"
      // file = {
      // 	path: "1_naresh/child.hr",
      // 	name: "child.hr"
      // }
      // editReport(file)
      // to edit in dev mode
      // mode = "filter"
      // file = { path: "naresh/notify.hr", name: "notify.hr" }
      // dashboardFilter = {
      //     uid: '9d7448e7-7c34-49b8-aa70-b6b900a75e6f'
      // }
      // changeCube({ path: 'cube/hrCube.cube' })
    }
    if (props.urlObj) {
      let { dir, file } = props.urlObj
      if (typeof dir === 'string' && dir.length && typeof file === 'string' && file.includes('.')) {
        editModeInfo = { dir: dir + "/" + file, file, extension: file?.split(".")[1], reportProps: {} }
      }
    }
    if (editModeInfo) {
      let { dir, file, extension, reportProps } = editModeInfo;
      reportProps = reportProps || {}
      if (extension === "metadata") {
        dispatch(appActions.setEditModeInfo(null));
        changeMetadata({ path: dir });
      }
      if (extension === "cube") {
        dispatch(appActions.setEditModeInfo(null));
        changeCube({ path: dir });
      }
      if (extension === "hr") {
        dispatch(appActions.setEditModeInfo(null));
        dispatch(removeAllReports());
        editReport({ path: dir, name: file, ...reportProps, reportId });
      }
      editModeInfoRef.current = editModeInfo;
    }
    if (["open", "dashboard", "filter", "instant-bi"].includes(props.mode)) {
      parameters = props.mode === "filter" ? props.allFilters : parameters;
      if (props.mode === 'filter') {
        parameters = checkForAnchorRelativeParameters(parameters);
      }
      let loadedState = checkWhetherStateIsLoaded(file, reports);
      if (props.mode === "filter" && loadedState) {
        loadedState.loadState = true;
        loadedState.id = reportId;
        loadedState.mode = "filter";
        loadedState.dashboardFilter = dashboardFilter;
        loadedState = modifyFilters(loadedState, parameters);
        // generateReport({...loadedState,user}, dispatch);
        generateReport(loadedState, dispatch);
      } else {
        setInValidFile(null)
        // dispatch(setHReportEditLoading( true))
        fetchReport(
          { file: { ...file, ...props }, mode: props.mode, parameters },
          dispatch,
          getApi
        ).then((res) => {
          res.loadState = true;
          res.id = reportId;
          if (res.drillThrough && res.activeDrillthroughId) {
            res.activeDrillthroughId = reportId;
            res.drillThroughList = (res.drillThroughList || []).map((item) => {
              if (item.active) {
                item = { ...item, drillThroughId: reportId };
              }
              return item;
            });
          }
          if (props.mode === "filter") {
            res.dashboardFilter = dashboardFilter;
          }
          if (typeof props.renderTaskbar === "function") {
            renderTaskbar(reportId);
            toggleToolsAreaShelf(dispatch);
          }
          if (res?.isNetworkCallFail) {
            setInValidFile(res)
            return;
          }
          if (typeof props.setFileInfo === "function") {
            props.setFileInfo({ fileTitle: res.reportInfo.reportName });
          }
          if (typeof props.getReportInfo === "function") {
            props.getReportInfo({
              filtersList: res.filters,
              reportId: reportId
            });
          }
          res = modifyFilters(res, parameters);
          setParametersReview({ ...res });
          if (res?.selectedType === "MapChart") {
            dispatch(loadReportData({ ...res, undoRedoAction: true, reportData: { loading: true } }))
            fetchMapData(res, dispatch, getApi);
          } else {
            generateReport({ ...res, user, isPrintMode }, dispatch, getApi, handleAddDrilldownFilterForOpenMode);
          }
          if (['dashboard'].includes(props.mode) && props.refreshDashboard && typeof props.refreshDashboard === 'function') { // added for 7720
            props.refreshDashboard()
          }
          // generateReport({ ...res, isPrintMode }, dispatch, getApi);
        })
          .catch(err => {
            setInValidFile(err)
          });
      }
    } else {
      dispatch(appActions.showNavbar(true));
      if (!reports.length || !["dashboard", "filter"].includes(props.mode)) {
        if (props.testMode !== "viz") {
          dispatch(loadIntialReport({ reportId }));
        }
      }
    }
    document.addEventListener("fullscreenchange", (event) => {
      if (document.fullscreenElement) {
        dispatch(updateFullScreenState(true));
        // handleToggle('fullscreen', true);
      } else {
        dispatch(updateFullScreenState(false));
        // handleToggle('fullscreen', false);
      }
    });
    return () => {
      dispatch(removeReport({ id: reportId }))
    }
  }, []);

  useEffect(() => {
    if (editModeInfo && !isEqual(editModeInfo, editModeInfoRef.current)) {
      let { dir, extension, route } = editModeInfo;
      if (extension === "metadata" && route !== 'metadata') {
        dispatch(appActions.setEditModeInfo(null));
        changeMetadata({ path: dir });
      }
    }
  }, [editModeInfo])

  const handleAddDrilldownFilterForOpenMode = (report) => {
    if (report?.drillDown) {
      if (parameters && Object.keys(parameters).length) {
        let filteredFields = report?.fields?.filter((field) => {
          let name = parameters[getFieldDisplayName(field)];
          if (!name) return false;
          return field
        })?.map((item) => {
          return getFieldDisplayName(item)
        })
        if (filteredFields?.length) {
          filteredFields?.forEach((field) => {
            let newFilter = dilldownFilter([
              { field, value: parameters[field], condition: "IS_ONE_OF", drillownFilter: "report", drillDownFilterValues: report?.reportData?.data || [] }
            ], report?.fields)[0]
            newFilter.reportId = report?.id
            dispatch(createFilter(newFilter));
          })
        }
      }
    }
  }


  const fetchMapData = (data, dispatch, getApi) => {
    const { fields = [] } = data || {}
    const geoGraphicRolesMap = {
      country: "world_countries.json",
      world: "world_countries.json",
      state: "world_states.json",
      city: "world_cities.json",
    }
    const geoGraphicRoles = fields?.filter((field) => field?.geographicType)?.map((item) => {
      let role = item?.geographicType
      if (geoGraphicRolesMap[role]) return { file: geoGraphicRolesMap[role], type: role };
    }).filter(Boolean)
    let allFilesPromise = geoGraphicRoles?.map(({ file, type }) => {
      let hreportState = {}
      dispatch((_, getState) => {
        hreportState = getState()?.hreport?.present;
      })
      if (hreportState?.geoJsonData[type]?.length) return null;
      return getGeoJsonData({
        value: file, type
      }, dispatch)
    }).filter(Boolean)
    Promise.all(allFilesPromise).then(() => {
      generateReport({ ...data, user }, dispatch, getApi);
    }).catch((err) => {
      console.log(err)
    })
  }

  const setMapGeoJsonData = () => {
    const geoGraphicRoles = [
      { file: "world_countries.json", type: "country" },
      { file: "world_countries.json", type: "world" },
      { file: "world_states.json", type: "state" },
      { file: "world_cities.json", type: "city" },
    ]
    let allFilesPromise = geoGraphicRoles?.map(({ file, type }) => {
      let hreportState = {}
      dispatch((_, getState) => {
        hreportState = getState()?.hreport?.present;
      })
      if (hreportState?.geoJsonData[type]?.length) return null;
      return getGeoJsonData({
        value: file, type
      }, dispatch)
    }).filter(Boolean)
    Promise.all(allFilesPromise).then(() => {
      console.log("Fetch MapGeoJsonData success")
    }).catch((err) => {
      console.log(err)
    })
  }

  useEffect(() => {
    !['open', 'dashboard', 'filter'].includes(props.mode) && setMapGeoJsonData()
  }, [])



  useEffect(() => {
    let { file } = props;
    // if (props.mode === 'filter') { // added 7231, 7230 && 7255
    //   parameters = props.allFilters;
    // }
    if (
      !isEqual(file, fileInfoRef.current) &&
      ["dashboard", "open", "filter"].includes(props.mode)
    ) {
      dispatch(removeAllReports());
      reloadCurrentReport();
      fileInfoRef.current = file;
    }
    if (
      !isEqual(parameters, paramsRef.current) &&
      ["dashboard", "open", "filter"].includes(props.mode)
    ) {
      if ((props.mode !== "filter") && parameters.print !== paramsRef.current.print && ["pdf", "ppt", "pptx", "png"].includes(parameters?.print)) {
        let format = parameters?.print === 'ppt' ? 'pptx' : parameters?.print;
        handlePrintExport(format, reportId);
      } else if ((props.mode !== "filter") && parameters.print !== paramsRef.current.print && parameters?.print === "csv") {
        handleURLExport(reportId, parameters.print)
      } else if ((props.mode !== "filter") && parameters.print !== paramsRef.current.print && ["xls", "xlsx"].includes(parameters?.print)) {
        // handleURLExport(reportId, parameters.print)
        handleURLExport(reportId, "xls")
      } else {
        let activeReport = reports.find((report) => report.id === reportId);
        if (activeReport) {
          let modifiedState = modifyFilters(activeReport, parameters);
          if (props.mode === 'filter') {
            const { filters: mFltrs = [] } = modifiedState;
            const { filters: aFltrs = [] } = activeReport;
            if (!isEqual(mFltrs, aFltrs)) {
              generateReport({ ...modifiedState, loadState: true, isPrintMode, user }, dispatch, getApi);
            }
          } else {
            generateReport({ ...modifiedState, loadState: true, isPrintMode, user }, dispatch, getApi);
          }
        }

        // if (activeReport) { // remove filter !== check for bug id : 7231, 7230 && 7255
        //   let modifiedState = modifyFilters(activeReport, parameters);
        //   generateReport({ ...modifiedState, loadState: true, isPrintMode, user }, dispatch, getApi);
        // }
        // if (activeReport && props.mode !== 'filter') { // added props.mode !== "filter" condition for 6050 fix 
        //   let modifiedState = modifyFilters(activeReport, parameters);
        //   generateReport({ ...modifiedState, loadState: true, isPrintMode, user }, dispatch, getApi);
        //   // generateReport({ ...modifiedState, loadState: true, isPrintMode }, dispatch, getApi);
        // }
      }
      paramsRef.current = parameters;
    }
  });
  useEffect(() => {
    if (props.cacheRefresh && props.cacheRefreshCount) {
      let activeReport = reports.find((report) => report.id === reportId);
      if (activeReport) {
        let modifiedState = modifyFilters(activeReport, parameters);
        generateReport({ ...modifiedState, refresh: true, user }, dispatch, getApi);
        // generateReport({ ...modifiedState, refresh: true }, dispatch, getApi);
      }
    }
  }, [props.cacheRefreshCount]);

  const { reportName } = useSelector((state) => {
    return (
      state.hreport.present.reports.find((report) => report.active)?.reportInfo || {}
    );
  });
  useEffect(() => {
    if (
      activeReportId &&
      (["create", "edit"].includes(props.mode) || !props.mode)
    ) {
      setReportId(activeReportId);
    }
  }, [activeReportId]);
  useEffect(() => {
    if (reportName && !["open", "dashboard", "filter"].includes(props.mode)) {
      document.title = `${reportName} | HI:Helical-Report`;
    }
  }, [reportName]);
  const layout = useSelector((state) => {
    return state.hreport.present.layout;
  });
  const { metadata, preview, fullscreen, isCube, metadataLoading } = useSelector((state) => {
    return (
      state.hreport.present.reports.find((report) => report.id === activeReportId) || {}
    );
  });
  const activeReport = reports.find((report) => report.id === reportId) || {};
  let { reportInfo, reportData, id, activeDrillthroughId, properties, styles, stylesId } =
    activeReport;
  const isDrillThroughReport = checkIfDrillThrough(dispatch, id, activeDrillthroughId)
  const { cache } = properties || {};
  let isOpenMode = ["open", "instant-bi"].includes(props.mode);
  let isInstantBIMode = ["instant-bi"].includes(props.mode);
  let isDashboardMode = props.mode === "dashboard";
  let isFilterMode = ["filter"].includes(props.mode);
  const { metadataShelf, toolsAreaShelf, fieldsAreaShelf, gridItemsLayout: gridItems } = layout;
  const { handleToggle = () => { } } = useHreportGridLayout({ preview, fullscreen, metadataShelf, toolsAreaShelf })

  useEffect(() => {
    savedLayout &&
      savedLayout.prev &&
      savedLayout.current.forEach((ele) => {
        const prevRow = savedLayout.prev.find((j) => j.i === ele.i);
        if (JSON.stringify(ele) !== JSON.stringify(prevRow)) {
          const obj = { ...resizeByArea };
          if (!obj[ele["i"]]) {
            obj[ele["i"]] = true;
            setResizeByArea(obj);
          }
        }
      });
  }, [savedLayout]);

  useEffect(() => {
    !isTestMode && !activeReport?.selectedType === "MapChart" && setResizing(isWindowResizing)
  }, [isWindowResizing])

  useEffect(() => {
    let timer;

    if (props?.parameters?.navigatorUserAgent !== "print" && ["open", "dashboard"].includes(props?.mode)) {
      const milliSeconds = getMilliseconds(cache?.interval);
      if (milliSeconds > 0 && cache?.isCacheEnabled) {
        timer = setInterval(() => {
          generateReport({ ...activeReport, refresh: true, user }, dispatch, getApi);
          // generateReport({ ...activeReport, refresh: true }, dispatch, getApi);
        }, milliSeconds);
      }
    }
    return () => clearInterval(timer);
  }, [cache, props?.parameters?.navigatorUserAgent]);


  useEffect(() => {
    if (!["open", "dashboard", "filter"].includes(props.mode)) {
      if (keysPressed.length === 1 && keysPressed[0] === "Alt") {
        dispatch(setShotCutCurrentLocation("HR"))
      }
      // SubModules Navigations:
      if (currentSCLocation === "HR" && keysPressed.length === 2 && keysPressed[0] === "Alt") {
        handleHrShortcuts(dispatch, keysPressed);
      }
      // Visualization
      if (
        currentSCLocation === "HR VZ" &&
        keysPressed.length === 3 &&
        keysPressed[0] === "Alt" &&
        keysPressed[1].toLowerCase() === "v"
      ) {
        handleVizShortcuts(keysPressed[2], vizRef, dispatch);
      }
      //Tabs 
      if (currentSCLocation === "HR" && keysPressed.length === 2 && keysPressed[0] === "Alt" && keysPressed[1].toLowerCase() === "a") {
        addTabRef.current.edit("", "add")
        resetShortcuts(dispatch)
      }
      if (currentSCLocation === "HR" && keysPressed.length === 2 && keysPressed[0] === "Alt" && keysPressed[1].toLowerCase() === "d") {
        addTabRef.current.edit(addTabRef.current.tabData.activeKey, "remove")
        resetShortcuts(dispatch)
      }

      if (currentSCLocation === "HR" && keysPressed.length === 2 && keysPressed[0] === "Alt" && keysPressed[1].toLowerCase() === "c") {
        openFileBrowser("metadata")
        resetShortcuts(dispatch)
      }

      // Layout
      if (currentSCLocation === "HR" && keysPressed.length === 2 && keysPressed[0] === "Alt" && keysPressed[1].toLowerCase() === "l") {
        dispatch(setShotCutCurrentLocation("HR LY"))
        dropDownRef.current.click()
      }
      if (currentSCLocation === "HR LY" && keysPressed.length === 3 && keysPressed[0] === "Alt" && keysPressed[1].toLowerCase() === "l" && keysPressed[2] === "1") {
        dispatch(updateReportLayout({ pane: "metadataShelf" }));
        dispatch(setKeysPressed("reset"))
        dropDownRef.current.click()
      }
      if (currentSCLocation === "HR LY" && keysPressed.length === 3 && keysPressed[0] === "Alt" && keysPressed[1].toLowerCase() === "l" && keysPressed[2] === "2") {
        dispatch(updateReportLayout({ pane: "toolsAreaShelf" }));
        dispatch(setKeysPressed("reset"))
        dropDownRef.current.click()
      }
      if (currentSCLocation === "HR LY" && keysPressed.length === 3 && keysPressed[0] === "Alt" && keysPressed[1].toLowerCase() === "l" && keysPressed[2] === "3") {
        dispatch(updateReportLayout({ pane: "fieldsAreaShelf" }));
        dispatch(setKeysPressed("reset"))
        dropDownRef.current.click()
      }

      // Save 
      if (currentSCLocation === "HR" && keysPressed.length === 2 && keysPressed[0] === "Alt" && keysPressed[1] === "s") {
        dispatch(setShotCutCurrentLocation("HR SV"))
        saveDropDownRef.current.click()
      }
      if (currentSCLocation === "HR SV" && keysPressed.length === 3 && keysPressed[0] === "Alt" && keysPressed[1] === "s" && keysPressed[2] === "1") {
        saveDropDownRef.current.click()
        saveRef.current()
        dispatch(setKeysPressed("reset"))
      }
      if (currentSCLocation === "HR SV" && keysPressed.length === 3 && keysPressed[0] === "Alt" && keysPressed[1] === "s" && keysPressed[2] === "2") {
        saveDropDownRef.current.click()
        saveAsRef.current()
        dispatch(setKeysPressed("reset"))
      }

      // Export 
      if (currentSCLocation === "HR" && keysPressed.length === 2 && keysPressed[0] === "Alt" && keysPressed[1].toLowerCase() === "e") {
        dispatch(setShotCutCurrentLocation("HR EX"))
        exportDropDownRef.current.click()
      }
      if (currentSCLocation === "HR EX" && keysPressed.length === 3 && keysPressed[0] === "Alt" && keysPressed[1].toLowerCase() === "e" && keysPressed[2] === "1") {
        handleExport(reportId, "csv")
        dispatch(setKeysPressed("reset"))
        exportDropDownRef.current.click()
      }
      if (currentSCLocation === "HR EX" && keysPressed.length === 3 && keysPressed[0] === "Alt" && keysPressed[1].toLowerCase() === "e" && keysPressed[2] === "2") {
        handleExport(reportId, "xls")
        dispatch(setKeysPressed("reset"))
        exportDropDownRef.current.click()
      }

      // File Browser
      if (currentSCLocation === "HR" && keysPressed.length === 2 && keysPressed[0] === "Alt" && keysPressed[1].toLowerCase() === "b") {
        openFileBrowser("hr")
        resetShortcuts(dispatch)
      }

      //Preview
      if (currentSCLocation === "HR" && keysPressed.length === 2 && keysPressed[0] === "Alt" && keysPressed[1] === "8") {
        refs.preview.current()
        dispatch(setKeysPressed("reset"))
      }

      //Fullscreen 
      if (currentSCLocation === "HR" && keysPressed.length === 2 && keysPressed[0] === "Alt" && keysPressed[1] === "9") {
        handleFullScreenClick()
        resetShortcuts(dispatch)
      }

      // Generate 
      if (currentSCLocation === "HR" && keysPressed.length === 2 && keysPressed[0] === "Alt" && keysPressed[1] === "g") {
        generateRef.current.click()
        resetShortcuts(dispatch)
      }
      // Cache Refresh 
      if (currentSCLocation === "HR" && keysPressed.length === 2 && keysPressed[0] === "Alt" && keysPressed[1] === "r") {
        cacheRefreshRef.current.click()
        resetShortcuts(dispatch)
      }
      // Share 
      if (currentSCLocation === "HR" && keysPressed.length === 2 && keysPressed[0] === "Alt" && keysPressed[1] === "h") {
        shareTaskBarItemRef.current.click()
        resetShortcuts(dispatch)
      }
      // Undo
      if (currentSCLocation === "HR" && keysPressed.length === 2 && keysPressed[0] === "Alt" && keysPressed[1].toLowerCase() === "z") {
        undoRef.current()
        resetShortcuts(dispatch)
      }
      // Redo
      if (currentSCLocation === "HR" && keysPressed.length === 2 && keysPressed[0] === "Alt" && keysPressed[1].toLowerCase() === "y") {
        redoRef.current()
        resetShortcuts(dispatch)
      }
    }
  }, [keysPressed])

  useEffect(() => {
    let newLayout = getHreportInitialGridLayout()
    if (isWindowResizing && savedLayout && !isEqual(savedLayout, newLayout)) {
      dispatch(updateGridItemsLayout({ layout: newLayout }));
    }
  }, [offsetHeight, isWindowResizing])

  const Notify = notify(dispatch);
  const saveAs = useRef(null);
  const onSave = () => {
    if (!reportData?.dataId) {
      return Notify.warning({ message: "Please generate any report." });
    }
    if (!activeReport.reportInfo.uuid) {
      setShowFb({
        footerForm: {
          type: "Save",
        },
      });
      dispatch(fileBrowserActions.setShowFileBrowser(true));
    } else {
      saveReportFile({ ...activeReport }, dispatch);
    }
  };
  const onSaveAs = () => {
    if (!reportData?.dataId) {
      return Notify.warning({ message: "Please generate any report." });
    }
    saveAs.current = true;
    replaceStylesAndStylesIdInActiveReport({ styles, stylesId, dispatch })
    setShowFb({
      footerForm: {
        type: "Save",
      },
    });
    dispatch(fileBrowserActions.setShowFileBrowser(true));
  };
  const saveCallback = (dir, fileName) => {
    setShowFb(false);
    if (!dir || !fileName) {
      return Notify.warning({ message: "Please select a directory to save" });
    }
    if (dir?.type !== 'folder') { // 6708
      return Notify.error({ message: "Please select a directory to save" });
    }
    let { path } = dir;
    let reportInfo = { location: path, reportName: fileName };
    saveReportFile(
      { ...activeReport, reportInfo, saveAs: saveAs.current },
      dispatch
    );
    saveAs.current = false;
  };

  const handleDeletedMetadataEditModeReport = (res) => {
    dispatch(loadReportData({ ...res, reportData: { data: [], message: res?.message || "", selectedType: res?.selectedType || "" }, loadState: true }))
    return;
  }

  const editReport = async (file) => {
    dispatch(setHReportEditLoading(true))
    let tempReports = reports;
    dispatch((_, getState) => {
      tempReports = getState().hreport.present.reports.filter((report) => !report.drillThroughId);
    })
    if (tempReports.length && tempReports.find((report) => report.active)?.metadata) {
      if (tempReports.length > 3) {
        dispatch(setHReportEditLoading(false))
        return Notify.warning({
          message: "You have reached the maximum number of tabs.",
          type: "Frontend",
        });
      }
      addReport();
    }
    // if (reports.length > 4) {
    //   dispatch(setHReportEditLoading(false))
    //   return Notify.warning({
    //     message: "You have reached the maximum number of tabs.",
    //     type: "Frontend",
    //   });
    // }
    setInValidFile(null)
    fetchReport({ file, mode: "edit" }, dispatch, getApi).then((res) => {
      if (!res?.metadata) {
        handleDeletedMetadataEditModeReport(res);
        return;
      }
      res.loadState = true;
      if (file.reportId) {
        res.id = file.reportId
      }
      if (res.drillThrough && res.activeDrillthroughId) {
        res.activeDrillthroughId = res.id;
        res.drillThroughList = (res.drillThroughList || []).map((item) => {
          if (item.active) {
            item.drillThroughId = res.id;
          }
          return item;
        });
      }
      res.filters = checkRelativeDateFilter(res.filters, {}, "edit")
      //added this if selected type check for map Chart if we don't have the geojson data then it can call it.
      if (res?.selectedType === "MapChart") {
        dispatch(loadReportData({ ...res, undoRedoAction: true, reportData: { loading: true } }))
        fetchMapData(res, dispatch, getApi);
      } else {
        generateReport({ ...res, user }, dispatch, getApi);
      }
      // generateReport(res, dispatch);
    }).then(err => {
      dispatch(setHReportEditLoading(false))
      setInValidFile(err)
    });
  };
  const loadDrillThroughReport = (file) => {
    setShowFb(false);
    loadChildReport(file, dispatch, getApi);
  };
  const openFbForDrillThrough = () => {
    setShowFb({
      onDoubleClick: loadDrillThroughReport,
      contextMenuOptions: {
        // append: true,
        options: [
          {
            name: "Drill Through",
            types: ["file"],
            icon: <EditOutlined />,
            extensions: ["hr"],
            callback: loadDrillThroughReport,
          },
        ],
      },
      extensionOptions: ["hr"],
    });
    dispatch(fileBrowserActions.setShowFileBrowser(true));
  };
  const openForEdit = () => {
    setShowFb({
      onDoubleClick: editReport,
      contextMenuOptions: {
        append: true,
        options: [
          {
            merge: true,
            id: "edt",
            name: "Edit",
            types: ["file"],
            extensions: ["hr"],
            disabled: false,
            callback: editReport,
          },
          {
            merge: true,
            id: "opn",
            types: ["file"],
            extensions: ["hr"],
            hide: true,
          },
          {
            merge: true,
            id: "onw",
            types: ["file"],
            extensions: ["hr"],
            hide: true,
          },
        ],
      },
      extensionOptions: ["hr"],
    });
    dispatch(fileBrowserActions.setShowFileBrowser(true));
  };
  const checkIsMetadataPresent = () => metadata !== null && metadata !== undefined
  const setMetadata = (rec) => {
    let { path } = { ...rec };
    path = path.split("/");
    let metadataFileName = path.pop();
    let location = path.join("/");
    let formData = {
      location,
      metadataFileName,
    };
    dispatch(setHrSidebar('metadata'));
    openMetadata(formData, dispatch, getApi);
    setMetadataFromSettings(false)
  }
  const handleMetadataModalOkClick = () => {
    const { id = "" } = activeReport || {}
    !metadataFromSettings && dispatch(resetActiveReport({ reportId: id, undoRedoAction: false, metadata: activeReport.metadata, databaseFunctions: activeReport.databaseFunctions, dateFunctions: activeReport.dateFunctions }))
    setMetadata({ ...metadataToBeChange })
    setMetdataToBeChange(null)
    setMetadataChangeModalVisible(false)
    setMetadataFromSettings(false)
  }
  const changeMetadata = (rec) => {
    if (checkIsMetadataPresent()) {
      setMetadataChangeModalVisible(true)
      setMetdataToBeChange(rec)
    } else {
      setMetadata(rec)
    }
  };
  const changeCube = (rec) => {
    let { path } = { ...rec };
    path = path.split("/");
    let file = path.pop();
    let dir = path.join("/");
    let formData = {
      dir,
      file,
    };
    dispatch(setHrSidebar('cube'));
    openCube(formData, dispatch);
  };

  const handleExport = (id, format) => {
    exportReport({ id, format, callback: setDownloadingInfo }, dispatch);
  }

  const handleURLExport = (id, format) => {
    if (id) {
      hiAxiosRef.current = exportReport({ id, format, callback: setDownloadingInfo }, dispatch);
    }
  };

  const handlePrintExport = (format, reportId) => {
    let changedFilterValues = {};
    let { file } = props
    if (reportId) {
      dispatch((dispatch, getState) => {
        let activeReport = getState().hreport.present.reports.find(
          (item) => item.id === reportId
        );
        if (activeReport.activeDrillthroughId) {
          activeReport = getState().hreport.present.reports.find(item => item.id === activeReport.activeDrillthroughId);
          let { reportInfo } = activeReport
          let { location, uuid, reportName } = reportInfo
          file = { path: location + '/' + uuid, name: uuid, title: reportName }
        }
        let { filters } = activeReport;
        filters.map((filter) => {
          changedFilterValues[getFieldDisplayName(filter)] = filter.values;
        });
      });
    }
    hiAxiosRef.current = exportPrintedReport(
      {
        file,
        parameters: { ...props.parameters, ...changedFilterValues },
        format,
        callback: setDownloadingInfo,
      },
      dispatch
    );
  };
  const reloadCurrentReport = () => {
    let { file, mode } = props;
    let reportId = uuidv4();
    setReportId(reportId);
    parameters = props.mode === "filter" ? props.allFilters : parameters;
    if (props.mode === "filter") {  // [7142]
      parameters = checkForAnchorRelativeParameters(parameters);
    }
    setInValidFile(null)
    fetchReport({ file, mode, parameters }, dispatch, getApi).then((res) => {
      if (res?.isNetworkCallFail) {
        setInValidFile(res)
        return;
      }
      res.loadState = true;
      res.id = reportId;
      res = modifyFilters(res, parameters);
      setParametersReview({ ...res });
      if (typeof props.setFileInfo === "function") {
        props.setFileInfo({ fileTitle: res.reportInfo.reportName });
      }
      if (typeof props.getReportInfo === "function") {
        props.getReportInfo({
          filtersList: res.filters,
          reportId: reportId,
        });
      }
      generateReport({ ...res, user }, dispatch, getApi);
      // generateReport(res, dispatch);
    }).catch(err => {
      setInValidFile(err)
    });
    renderTaskbar(reportId);
  };
  const renderTaskbar = (reportId) => {
    props.renderTaskbar([
      {
        title: "Filters",
        icon: <FunnelPlotOutlined />,
        callback: () => toggleToolsAreaShelf(dispatch),
      },
      {
        title: "cacheTime",
        key: "cacheTime",
        icon: <FieldTimeOutlined />,
        callback: () => { },
      },
      {
        title: "Export",
        icon: <ExportOutlined />,
        // callback: e=> toggleToolsAreaShelf(dispatch),
        dropdown: true,
        menu: getMenuOptions("hreport", (format) => ["pdf", "png", "pptx"].includes(format) ? handlePrintExport(format, reportId) : handleExport(reportId, format))
      },
      {
        title: "Refresh",
        icon: <SyncOutlined />,
        // callback: e=> toggleToolsAreaShelf(dispatch),
        dropdown: true,
        menu: [
          {
            title: "Cache",
            key: "cache",
            icon: <SyncOutlined />,
            callback: () => handleRefreshData(reportId),
          },
          {
            title: "Current Report",
            key: "currentReport",
            icon: <ReloadOutlined />,
            callback: () => reloadCurrentReport(),
          },
        ],
      },
    ]);
  };
  const addFilter = (data) => {
    if (typeof props.addFilter === "function") {
      props.addFilter(data);
    } else {
      dispatch(createFilter(data));
    }
  };

  const addForwardFilterIPC = (data) => {
    if (typeof props.addForwardFilterIPC === "function") {
      props.addForwardFilterIPC(data);
    } else {
      dispatch(addDilldownFilter(data));
    }
  };

  const deleteBackwardFilterIPC = (data) => {
    if (typeof props.deleteBackwardFilterIPC === "function") {
      props.deleteBackwardFilterIPC(data);
    } else {
      dispatch(deleteDilldownFilter(data));
    }
  };

  const refreshFiltersIPC = (data) => {
    if (typeof props.refreshFiltersIPC === "function") {
      props.refreshFiltersIPC(data);
    } else {
      dispatch(deleteDilldownFilter(data));
    }
  };

  const handleRefreshData = (id) => {
    dispatch((dispatch, getState) => {
      let activeReport = getState().hreport.present.reports.find(
        (item) => item.id === id
      );
      activeReport &&
        generateReport({ ...activeReport, refresh: true, user }, dispatch, getApi);
      // generateReport({ ...activeReport, refresh: true }, dispatch, getApi);
    });
  };
  const getLastModfified = (data) => {
    if (props.getLastModfified) {
      props.getLastModfified(data);
    }
  };
  const openFileBrowser = (ext, fromSettings = false) => {
    if (fromSettings) setMetadataFromSettings(true)
    if (ext === "hr") {
      openForEdit();
      return null;
    }
    setShowFb({
      onDoubleClick: (rec) => { rec.extension === 'cube' ? changeCube(rec) : changeMetadata(rec) },
      contextMenuOptions: {
        append: true,
        options: [
          {
            name: "Use This Metadata",
            types: ["file"],
            icon: <EditOutlined />,
            extensions: ["metadata"],
            callback: changeMetadata,
            merge: true,
            id: "chr",
          },
          {
            name: "Use This Cube",
            types: ["file"],
            icon: <EditOutlined />,
            extensions: ["cube"],
            callback: changeCube,
            // merge: true,
            // id: "chr",
          },
        ],
      },
      extensionOptions: ["metadata", "cube"],
    });
    dispatch(fileBrowserActions.setShowFileBrowser(true));
  };
  const addReport = () => {
    dispatch(addNewReport());
  };
  const changeTab = ({ activeKey }) => {
    dispatch(changeReport({ id: activeKey }));
    clearGridChart(activeKey, reports, dispatch)
  };
  const deleteReport = (id) => {
    dispatch(removeReport({ id }));
  };

  const checkForDuplicateFilters = (filters = []) => {
    if (!filters?.length) return false
    const filterNames = filters.map((fltr) => getFieldDisplayName(fltr));
    const tempSet = new Set();
    for (const name of filterNames) {
      if (tempSet.has(name)) {
        return true;
      }
      tempSet.add(name);
    }
    return false;
  }

  const handleRelativeDateParameters = (filters, parameters) => {
    let tempParameters = cloneDeep(parameters)
    for (let param in tempParameters) {
      let filter = filters.find(fltr => getFieldDisplayName(fltr) === param);
      if (filter?.anchor?.relativePart) {
        const { anchor = {}, values } = filter || {}
        tempParameters[param] = getRelativeDateValues({ anchorDateData: anchor }, values, filter)
      }
    }
    return tempParameters
  }

  const setParametersReview = (activeReport, changedData) => {
    let parameters = {};
    activeReport.filters.map((fltr) => {
      let fltrName = getFieldDisplayName(fltr);
      parameters[fltrName] = fltr.values;
    });
    if (changedData) {
      parameters = { ...parameters, ...changedData };
      activeReport = modifyFilters(activeReport, parameters)
    }
    parameters = handleRelativeDateParameters(activeReport.filters, parameters)
    if (typeof props.setParametersReview === "function") {
      generateReport({
        ...activeReport,
        getFormData: true,
        printFormat: "email",
        user,
      }).then((adhocFormData) => {
        adhocFormData = {
          ...adhocFormData,
          isAdhoc: true,
          requestType: "adhoc",
          serviceType: "report",
          service: "fetchData",
        };
        props.setParametersReview({ adhocFormData, parameters, duplicateFiltersPresent: checkForDuplicateFilters(activeReport?.filters) });
      });
    }
  };
  const updateParameters = (label, data) => {
    if (typeof props.onChange === "function") {
      props.onChange(data);
    }
    let activeReport = reports.find((report) => report.id === reportId);
    let parameters = {};
    activeReport.filters.map((fltr) => {
      parameters[getFieldDisplayName(fltr)] = fltr.values;
    });
    setParametersReview(activeReport, { [label]: data });
  };
  const getApi = apiIntance => {
    apiRef.current = apiIntance
  }
  const abortFetchData = () => {
    apiRef.current?.abort()
  }
  const handleShare = () => {
    if (!reportInfo?.location || !reportInfo?.uuid) {
      Notify.warning({ message: "Please save the report!" });
      return;
    }
    shareRef.current = true;
    dispatch(fileBrowserActions.setShareModalVisibility());
  };

  const handleUpdateGridLayout = (key, data, savedLayout) => {
    let newLayout = ((state) => {
      return produce(state, (draft) => {
        const toggleInteraction = (item) => {
          if (!savedLayout?.current) return item
          if (key && item.i === key) {
            item.isResizable = !item.isResizable;
            item.isDraggable = !item.isDraggable;
          }
          let currentItem = savedLayout.current.find(
            (updatedItem) => updatedItem.i === item.i
          );
          return {
            ...item,
            x: currentItem.x,
            y: currentItem.y,
            h: currentItem.h,
            w: currentItem.w,
          };
        };
        draft.lg = draft.lg.map(toggleInteraction);
        draft.md = draft.md.map(toggleInteraction);
        draft.sm = draft.sm.map(toggleInteraction);
        draft.xs = draft.xs.map(toggleInteraction);
        draft.xxs = draft.xxs.map(toggleInteraction);
      });
    })(data);
    dispatch(updateGridItemsLayout({ layout: newLayout }));
  }

  const handlePin = (key) => {
    handleUpdateGridLayout(key, gridItems, savedLayout);
  };
  let filebrowserConfig = filebrowserOptions;
  if (filebrowserConfig && filebrowserConfig.footerForm) {
    const inputValue =
      reportInfo?.uuid && reportInfo?.location ? reportInfo?.reportName : "Report_1";
    filebrowserConfig.footerForm.form = (
      <SaveItems
        formHeading="Report file name"
        onFormSumbit={saveCallback}
        saveButtonText="Save"
        cancelButtonText="Cancel"
        inputValue={inputValue}
        validateName={validateReportName}
      />
    );
  }
  let reportsList = reports.map((report) => {
    return { key: report?.id, title: report?.reportInfo?.reportName };
  });
  if (invalidFile) {
    {
      return (
        <div> {invalidFile.message} </div>
      )
    }
  }
  if (!isOpenMode && !isDashboardMode && !isFilterMode) {
    id = activeReportId;
    reportId = activeReportId;
  }

  if (!id) return null;
  const parentContainerId = `hi-reports-${reportId?.slice(0, 6) ?? uuidv4().slice(0, 6)}`;

  const gridProps = {
    resizeHandles: ["se"],
    cols: { lg: 100, md: 100, sm: 100, xs: 100, xxs: 100 },
    breakpoints: { lg: 1200, md: 996, sm: 768, xs: 480, xxs: 0 },
    isDroppable: true,
    compactType: null,
    preventCollision: true,
    rowHeight: 1,
    colWidth: 1,
    margin: [5, 10],
    containerPadding: [1, 1],
    measureBeforeMount: true,
    useCSSTransforms: false
  };
  let metadataGridItem = gridItems?.lg?.find((item) => item.i === "sidebar-area");
  let editingGridItem = gridItems?.lg?.find((item) => item.i === "editing-area");
  let chartGridItem = gridItems?.lg?.find((item) => item.i === "chart-area");

  let metadataChangeModal = (
    <Modal
      title={'Open another metadata file?'}
      visible={metadataChangeModalVisible}
      onOk={handleMetadataModalOkClick}
      onCancel={() => {
        setMetdataToBeChange(null)
        setMetadataChangeModalVisible(false)
      }}
    >
      <Text>
        Are you sure you want to open another file? All your changes will be lost.
      </Text>
    </Modal>
  )

  const downloadingInfoModal = (
    <Modal
      visible={!!downloadingInfo}
      className="hi-hr-download-modal"
      title="Downloading"
      okText={() => null}
      cancelText="Cancel"
      maskClosable={false}
      // onOk={() => {
      //   remove(warningVisible.deleteKey); // test
      //   setWarningVisible({ visible: false, deleteKey: "" });
      // }}
      onCancel={() => {
        hiAxiosRef.current.abort();
        setDownloadingInfo(null);
      }}
    >
      <div>
        <Progress percent={downloadingInfo?.progress} />
        <div>Please wait while downloading</div>
      </div>
    </Modal>
  )

  const vizAreaCommonProps = {
    getApi,
    abortFetchData,
    reportId,
    activeDrillthroughId,
    addFilter,
    editReport,
    addForwardFilterIPC,
    deleteBackwardFilterIPC,
    refreshFiltersIPC,
    parameters,
    getLastModfified,
    isPrintMode,
    resizing,
    handleExport,
    handlePrintExport,
    downloadingInfoModal
  }


  let navbar = (
    <HINavbar
      // tabsData={reportsList}
      // add={addReport}
      // activeId={activeReportId}
      changeTab={changeTab}
      // delete={deleteReport}
      taskbar={getHrTaskBarItems({
        activeReport,
        dispatch,
        layout,
        handleShare,
        onSave,
        onSaveAs,
        preview,
        getApi,
        refs,
        hreportPast,
        hreportFuture,
        activeReportId,
        user,
        isSavingInProgress,
        handleToggle,
        preview,
        fullscreen,
        getDropdownOptions
      })}
    >
      <HITabs
        type="editable-card"
        add={addReport}
        addTabRef={addTabRef}
        remove={deleteReport}
        tabData={{ panes: reportsList, activeKey: activeReportId }}
        setTabData={changeTab}
      />
    </HINavbar>
  );
  let content = (
    <HILayout
      header={navbar}
      defaultSidebar={false}
      content={
        <>
          <ResponsiveGridLayout
            onLayoutChange={(layout) => {
              if (!savedLayout) {
                setSavedLayout({ prev: undefined, current: layout });
              } else {
                setSavedLayout({ prev: savedLayout.current, current: layout });
              }
            }}
            onResizeStop={(layout) => {
              handleUpdateGridLayout(null, gridItems, { current: layout });
              if (savedLayout && savedLayout.current) {
                setSavedLayout({ prev: savedLayout.current, current: layout });
              }
              setResizing(false)
            }}
            onResizeStart={() => {
              setResizing(true)
            }}
            layouts={gridItems}
            className="layout hr-grid-layout"
            {...gridProps}
          >
            <div
              className={`grid-item hr-sidebar ${resizeByArea["sidebar-area"] ? "" : "grid-height-99"
                } ${isDrillThroughReport ? "hr-metadata-container-disabled" : ""}`}
              key={"sidebar-area"}
            >
              {!(preview || !metadataShelf) && (
                <>
                  <MetadataArea
                    metadata={metadata}
                    abortFetchData={abortFetchData}
                    size={{ height: metadataGridItem?.h }}
                    openFileBrowser={openFileBrowser}
                    filebrowserConfig={filebrowserConfig}
                    onGlobalSearch={() => setShowFb(false)}
                    isCube={isCube}
                    // metadataLoading={metadataLoading}
                    metadataLoading={sidebarLoading}
                    report={activeReport}
                    hideSideBar={false}
                    parentContainerId={parentContainerId}
                  />
                  {filebrowserConfig && <HIFileBrowser {...filebrowserConfig} />}
                  <div
                    className="hr-resize-pin"
                    onClick={() => handlePin("sidebar-area")}
                  >
                    {metadataGridItem?.isResizable ? (
                      <PushpinOutlined />
                    ) : (
                      <PushpinFilled />
                    )}
                  </div>
                </>
              )}
            </div>
            <div
              className={`grid-item ${resizeByArea["editing-area"] ? "" : "grid-height-99"
                }`}
              key={"editing-area"}
            >
              {!(preview || !toolsAreaShelf) && (
                <>
                  <EditingArea
                    getApi={getApi}
                    reportId={reportId}
                    abortFetchData={abortFetchData}
                    // reportId={reportId}
                    openFbForDrillThrough={openFbForDrillThrough}
                    openFileBrowser={openFileBrowser}
                    vizRef={vizRef}
                  />
                  <div
                    className="hr-resize-pin"
                    onClick={() => handlePin("editing-area")}
                  >
                    {editingGridItem?.isResizable ? (
                      <PushpinOutlined />
                    ) : (
                      <PushpinFilled />
                    )}
                  </div>
                </>
              )}
            </div>
            <div
              className={`grid-item  ${resizeByArea["chart-area"] ? "" : "grid-height-99"}`}
              key={"chart-area"}
            >
              {!(preview || !fieldsAreaShelf) && <FieldsArea {...{ savedLayout }} />}
              <VizArea {...vizAreaCommonProps} size={{ height: chartGridItem?.h }} fieldsAreaShelf={fieldsAreaShelf} />
              <div className="hr-resize-pin-editing" onClick={() => handlePin("chart-area")}>
                {chartGridItem?.isResizable ? (
                  <PushpinOutlined />
                ) : (
                  <PushpinFilled />
                )}
              </div>
              {/* {toggleIcons} */}
            </div>
          </ResponsiveGridLayout>
          {shareRef.current && (
            <ShareFinalModal
              shareOptions={{
                type: "file",
                dir: reportInfo?.location,
                file: reportInfo?.uuid,
              }}
            />
          )}
          {metadataChangeModal}
        </>
      }
    />
  );
  if (fullscreen || isDashboardMode) {
    content = <VizArea {...vizAreaCommonProps} />
  }
  if (isOpenMode) {
    let className = toolsAreaShelf
      ? "hr-report-area-open"
      : "hr-report-area-open hr-report-area-open-fullscreen";
    if (isInstantBIMode) {
      className = "hr-report-area-open hr-report-area-open-fullscreen";
    }

    content = (
      <>
        <div className={className}>
          <VizArea {...vizAreaCommonProps} size={{ height: chartGridItem?.h }} />
        </div>
        {toolsAreaShelf && props.mode !== "instant-bi" && (
          <div className="hr-editing-area-open">
            <EditingArea
              getApi={getApi}
              reportId={reportId}
              abortFetchData={abortFetchData}
              changeDashboardFilter={updateParameters}
              openFileBrowser={openFileBrowser}
              vizRef={vizRef}
            />
          </div>
        )}
        {downloadingInfoModal}
      </>
    );
  }
  if (isFilterMode) {
    content = (
      <EditingArea
        getApi={getApi}
        vizRef={vizRef}
        reportId={reportId}
        abortFetchData={abortFetchData}
        changeDashboardFilter={updateParameters}
        dashboardFilter={{
          ...props.dashboardFilter,
          selectedValues: props.parameters,
        }}
        allFilters={props.allFilters}
        openFileBrowser={openFileBrowser}
        isFilterComponent={props?.isFilterComponent ?? false}
      />
    );
  }

  return <>
    {!["open", "dashboard", "filter", "instant-bi"].includes(props.mode) && <HIShortcuts moduleData={{ name: "HelicalReport", commonRefs: { saveRef, undoRef, redoRef } }} />}
    <div className="hr-main-container" id="hr-main-container">{content}</div>
  </>
};

export { HelicalReports };

