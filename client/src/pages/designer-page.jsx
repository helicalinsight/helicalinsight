import React, { useState, useEffect, useRef, useCallback } from "react";
import { useDrop } from "react-dnd";
import { HINavbar, HIDashboardDesigner, SidebarFooter } from "../components";
import {
  SyncOutlined,
  ArrowRightOutlined,
  ReloadOutlined,
  CloseOutlined,
  ExportOutlined,
  FilterOutlined,
  FilePdfOutlined,
  FieldTimeOutlined,
  BarChartOutlined,
  FunnelPlotOutlined,
  FilePptOutlined,
  FileImageOutlined,
} from "@ant-design/icons";
import {
  Button,
  Progress,
  Modal,
  Skeleton,
  Card,
  Row,
  Col,
  Tooltip,
} from "antd";
import { useSelector, useDispatch } from "react-redux";
import { fileBrowserActions, appActions } from "../redux/actions";
import { HIFileBrowser } from "../components";
import SaveItems from "../components/hi-fileBrowser/SaveItems";
import notify from "../components/hi-notifications/notify";
import {
  saveTheDashboard,
  editTheDashboard,
  openInDashboardCallback,
} from "../components/hi-dashboard-designer/utils/dashboard-requests";
import {
  setDesignerMode,
  setPreviewMode,
  updateParameterDrawerStatus,
  designerUndo,
  designerRedo,
  toggleGridItemDrawer,
  toggleDashboardDrawer,
  updateGroupId,
  clearDesignerUndoRedoHistory,
  openCompactFbBrower,
  replaceReportId,
} from "../redux/actions/dashboard-designer.actions";
import { updateReportsRefresh } from "../redux/actions/dashboard.actions";
import Draggable from "react-draggable";
import HILayout from "../layouts/hi-layout";
import { TaskbarIcon } from "../components/common/custom-icons/CustomIcon";
import {
  getRelativeCacheTime,
  exportPrintedReportEfwdd,
  getDashboardVariableConfig,
} from "../utils/utilities";
import HIIcon from "../components/common/icons/hi-icons";
import { useDebounce } from "../hooks";
import { isEmpty, isEqual } from "lodash-es";
import "../components/hi-dashboard-designer/hi-designer-print.scss";
import HIShortcuts from "../components/common/hi-shortcuts/HIShortcuts";
import { handleDashboardDesignerKeyPress } from "../components/hi-dashboard-designer/utils/common-functions";
import ShortCutText from "../components/common/hi-shortcuts/hi-shortcuts";
import { designerTaskbarItems } from "../components/hi-dashboard-designer/utils/designer-taskbar-items";
import TutorialInfo from "../components/common/hi-tutorial";
import { validateDesignerName } from "./utils/helperMethods";
import { routesUrl } from "../app/constants";
import useExportOptions from "../hooks/useExportOptions";

const DashboardDesigner = (props) => {
  const gridItemsData = useSelector((state) =>
    state.designer.present.gridItemsData.map((item) => {
      if (!item.isSaved) {
        return { ...item, isSaved: true };
      }
      return item;
    })
  );
  const { getMenuOptions } = useExportOptions();
  const [refresh, setRefreshFn] = useState(new Date());
  const setRefresh = (date) => {
    dispatch(appActions.setShotCutCurrentLocation(""));
    setRefreshFn(date)
  }
  const layout = useSelector((state) => state.designer.present.layout);
  const dispatch = useDispatch();
  const dashboardUUID = useSelector(
    (state) => state.designer.present.dashboardUUID
  );
  const variables = useSelector((state) => state.designer.present.variables);
  const dashboardConfig = useSelector(
    (state) => state.designer.present.dashboardConfig
  );
  const gridIndex = useSelector((state) => state.designer.present.gridIndex);
  const isLoading = useSelector((state) => state.designer.present.isLoading);
  const isSaving = useSelector((state) => state.designer.present.isSaving);
  const gridSettingsData = useSelector(
    (state) => state.designer.present.gridSettings
  );
  const { lastModified } = useSelector((state) => state.app);
  const dashboardCSS = useSelector((state) => state.designer.present.css);
  const script = useSelector((state) => state.designer.present.script);
  const printOptions = useSelector(
    (state) => state.designer.present.printOptions
  );
  const savedReportName = useSelector(
    (state) => state.designer.present.savedReportName
  );
  const componentSettings = useSelector(
    (state) => state.designer.present.componentSettings
  );
  const parameterDrawerStatus = useSelector(
    (state) => state.designer.present.parameterDrawerStatus
  );
  const dashboardVariables = useSelector(
    (state) => state.dashboard.present.dashboardVariables
  );
  const components = useSelector((state) => state.dashboard.present.components);
  const designerSettings = useSelector(
    (state) => state.designer.present.designerSettings
  );
  const parameterSettings = designerSettings?.find(
    (item) => item.key === "parameters"
  )?.values;
  const gridWidthOption = useSelector(
    (state) => state.designer.present.gridWidthOption
  );
  const toggleIframes = useSelector(
    (state) => state.designer.present.toggleIframes
  );

  const openCompactFbBrowerState = useSelector(
    (state) => state.designer.present.openCompactFbBrower
  );

  const replaceReportIdValue = useSelector(
    (state) => state.designer.present.replaceReportId
  );

  const Notify = notify(dispatch);
  const [dir, setDir] = useState("");
  const [fileName, setFileName] = useState("");
  const [filebrowserOptions, setShowFb] = useState(false);
  const previewMode = useSelector(
    (state) => state.designer.present.previewMode
  );
  const editModeInfo = useSelector((state) => state.app.editModeInfo);
  // const [toggleToolsAreaShelf, setToggleToolsAreaShelf] = useState(true);
  const toggleToolsAreaShelf = useSelector(
    (state) => state.designer.present.toggleToolsAreaShelf
  );
  const [showFbCompact, setShowFbCompact] = useState(false);
  const [showFbFullView, setShowFbFullView] = useState(false);
  const isGlobalFbEnabled = useSelector(
    (state) => state.fileBrowser.globalFbEnabled
  );
  const paramsRef = useRef({});
  const [dragObj, setDragObj] = useState({ y: 470 });
  const [filterDragObj, setFilterDragObj] = useState({ x: 0, y: (window.screen.availHeight - 45) / 2 });

  // const [dragObj, setDragObj] = useState({ y: 46 });
  const DRAG_DELAY = 500;
  const debouncedDragObj = useDebounce(dragObj, DRAG_DELAY);
  let [downloadingInfo, setDownloadingInfo] = useState(null);
  const [editModeModal, setEditModeModal] = useState(false);
  const editRef = useRef(null);
  const fileInfoRef = useRef(null);
  let localEditModeInfo = localStorage.getItem("editModeInfo");
  localEditModeInfo = JSON.parse(localEditModeInfo);
  const hiAxiosRef = useRef(null);
  const { urlObj = {} } = props;
  const urlObjRef = useRef({});
  const designerPast = useSelector((state) => state.designer.past);
  const designerFuture = useSelector((state) => state.designer.future);
  const saveRef = useRef(null);
  const saveAsRef = useRef(null);
  const undoRef = useRef(null);
  const redoRef = useRef(null);
  const saveDropdownRef = useRef(null);
  const tutorialData = useSelector((state) => state.app.tutorialData || {});
  const { key } = tutorialData;
  const tutorialKey = key;
  const { updateRoute } = appActions;

  const refreshDashboard = useSelector((state) => state.dashboard.present.refreshDashboard); // addded for 7720


  const handleUndo = () => {
    dispatch(designerUndo());
    dispatch(toggleGridItemDrawer(false));
    dispatch(toggleDashboardDrawer(false));
  };

  const handleRedo = () => {
    dispatch(designerRedo());
    dispatch(toggleGridItemDrawer(false));
    dispatch(toggleDashboardDrawer(false));
  };

  const [refs, setRefs] = useState({
    save: saveRef,
    saveAs: saveAsRef,
    undo: undoRef,
    redo: redoRef,
    saveDropdown: saveDropdownRef,
  });

  // const [{ opacity }, drag] = useDrag(
  //   () => {
  //       return {
  //           type: "canvas_field",
  //           item: { type: "canvas_field", field },
  //           collect: (monitor) => ({
  //               opacity: monitor.isDragging() ? 0.5 : 1
  //           })
  //       }
  //   },
  //   []
  // )
  useEffect(() => {
    // used debouncing for efficiency
    if (debouncedDragObj != null) {
      dispatch(fileBrowserActions.setModeCoordniates(debouncedDragObj));
    }
  }, [debouncedDragObj]);
  const applicationSettingsData = useSelector(
    (state) => state.app.applicationSettingsData
  );
  let { userData } = applicationSettingsData || {};
  let { serverTime, clientTime } = userData;

  // let latestCachedTimeRaw = Math.max(
  //   ...gridItemsData.map((e) => e.lastModified)
  // );
  let latestCachedTimeRaw = lastModified ? lastModified : new Date().getTime();
  const latestCachedTime = getRelativeCacheTime(
    latestCachedTimeRaw,
    serverTime,
    clientTime
  );
  let { mode, file, parameters } = props;

  let isOpenMode = mode === "open";
  let isDashboardMode = urlObj?.mode === "dashboard" || mode === "dashboard";

  let isEditMode = editModeInfo;
  let isCreateMode = !mode;
  const fbCompactOptions = {
    containerId: "#dd-fb",
    closeFbOnCallback: false,
  };

  const fileBrowserEditMode = {
    onDoubleClick: (rec) => {
      if (["efwdd"].includes(rec?.extension)) {
        dispatch(
          appActions.setViewModeInfo({
            file: {
              path: rec.path,
              name: rec.name,
              title: rec.title,
            },
            resourceId: rec.resourceId,
            mode: "open",
            filters: [],
            extension: rec.extension,
          })
        );
        dispatch(updateRoute(routesUrl.reportViewUrl));
        return;
      }
      openInDashboardCallback({
        path: rec.path,
        name: rec.name,
        title: rec.title,
        resourceId: rec.resourceId,
        dispatch,
        gridIndex: gridIndex,
        gridItemsData,
        replaceReportIdValue,
      });
    },
    contextMenuOptions: {
      append: true,
      options: [
        {
          name: "Open in Dashboard",
          types: ["file"],
          icon: <ArrowRightOutlined />,
          extensions: ["report", "hr"],
          callback: (rec) => {
            openInDashboardCallback({
              path: rec.path,
              name: rec.name,
              title: rec.title,
              resourceId: rec.resourceId,
              dispatch,
              gridIndex: gridIndex,
              gridItemsData,
              replaceReportIdValue
            });
            dispatch(openCompactFbBrower(false));
            dispatch(replaceReportId(null));
          },
        },
        {
          name: "Edit",
          key: "edit",
          id: "edt",
          merge: true,
          disabled: false,
          types: ["file"],
          // icon: <EditOutlined />,
          extensions: ["efwdd"],
          executeCallbackOnly: true,
          callback: (record) => {
            setEditModeModal(record);
          },
        },
      ],
    },
    extensionOptions: showFbCompact ? ["hr", "efwdd"] : ["hr"],
  };

  const keysPressed = useSelector((state) => state.app.keysPressed);
  const altTriggered = useSelector((state) => state.app.altTriggered);
  const currentSCLocation = useSelector((state) => state.app.currentSCLocation);

  useEffect(() => {
    if (!openCompactFbBrowerState) return;
    setShowFb({
      ...fileBrowserEditMode,
      ...fbCompactOptions,
    });
    setShowFbCompact((prev) => !prev);
    setShowFbFullView(false);
  }, [openCompactFbBrowerState])

  useEffect(() => {
    if (tutorialKey && tutorialKey === "hi-designer-reports") {
      setShowFb({
        ...fileBrowserEditMode,
        ...fbCompactOptions,
      });
      setShowFbCompact(true);
      setShowFbFullView(false);
    } else if (tutorialKey && tutorialKey !== "hi-designer-reports") {
      setShowFbCompact(false);
    }
  }, [tutorialKey]);

  useEffect(() => {
    dispatch(appActions.setShotCutCurrentLocation("DD"));
    handleDashboardDesignerKeyPress({
      dispatch,
      keysPressed,
      onSave,
      onSaveAs,
      altTriggered,
      refs,
      currentSCLocation,
      setRefresh,
    });
  }, [keysPressed]);

  useEffect(() => {
    if (isCreateMode) {
      dispatch(setDesignerMode("create"));
    }
  }, []);

  useEffect(() => {
    return () => {
      dispatch(fileBrowserActions.setShowFileBrowser(false));
      dispatch(fileBrowserActions.setSearchResults(null));
    };
  }, []);

  useEffect(() => {
    window.addEventListener("beforeunload", handleUnload);
    return () => {
      window.removeEventListener("beforeunload", handleUnload);
      dispatch(clearDesignerUndoRedoHistory())
    };
  }, []);

  const handleUnload = (e) => {
    if (mode === "open") return null;
    const message = "o/";
    (e || window.event).returnValue = message; //Gecko + IE
    return message;
  };

  const handleEditMode = () => {
    const newUrl = window.baseURL + `#/dashboard-designer`;
    window.open(newUrl);
    setEditModeModal(false);
    editRef.current = new Date().getTime().toString();
    localStorage.setItem(
      "editModeInfo",
      JSON.stringify({
        dir: editModeModal.path,
        file: editModeModal.name,
        extension: editModeModal.extension,
        editFlag: editRef.current,
      })
    );
  };

  const getDirectory = ({ path, name }) => {
    return path?.replace(name, "").replace(/[\\|\/]+$/, "");
  };

  useEffect(() => {
    if (typeof props.setParametersReview === "function") {
      let tempParameters = getDashboardVariableConfig(dashboardVariables, dispatch); // added for 7720 fix 
      props.setParametersReview({
        parameters: tempParameters,
      });
    }
  }, [dashboardVariables, refreshDashboard]);

  useEffect(() => {
    if (toggleToolsAreaShelf && showFbCompact) {
      setShowFbFullView(false);
      dispatch(fileBrowserActions.setShowFileBrowser(true));
    }
    if (!toggleToolsAreaShelf) {
      setShowFbFullView(false);
      dispatch(fileBrowserActions.setShowFileBrowser(false));
    }
  }, [toggleToolsAreaShelf]);

  useEffect(() => {
    if (showFbCompact) {
      dispatch(fileBrowserActions.setShowFileBrowser(true));
    }
    if (!showFbCompact && !showFbFullView) {
      dispatch(fileBrowserActions.setSearchResults(null));
      dispatch(fileBrowserActions.setShowFileBrowser(false));
    }
  }, [showFbCompact]);

  useEffect(() => {
    if (
      localEditModeInfo &&
      localEditModeInfo?.extension === "efwdd" &&
      localEditModeInfo.editFlag !== editRef.current
    ) {
      const directory = getDirectory({
        path: localEditModeInfo?.dir,
        name: localEditModeInfo?.file,
      });
      setDir(directory);
      setFileName(localEditModeInfo?.file);
      editCallback(directory, localEditModeInfo?.file, "edit");
      // dispatch(appActions.setEditModeInfo(null));
      localStorage.removeItem("editModeInfo");
    }
    if (isEditMode && editModeInfo?.extension === "efwdd") {
      const directory = getDirectory({
        path: editModeInfo?.dir,
        name: editModeInfo?.file,
      });
      setDir(directory);
      setFileName(editModeInfo?.file);
      editCallback(directory, editModeInfo?.file, "edit");
      dispatch(appActions.setEditModeInfo(null));
    }
  }, [editModeInfo, localEditModeInfo]);

  useEffect(() => {
    if (
      (file && !isEqual(file, fileInfoRef.current)) ||
      (parameters && !isEqual(parameters, paramsRef.current) && isOpenMode)
    ) {
      const directory = getDirectory({
        path: file?.path,
        name: file?.name,
      });

      renderTaskbar();

      if (parameters && parameters.print) {
        let format = parameters?.print === 'ppt' ? 'pptx' : parameters?.print;
        handlePrintExport(format);
      } else {
        editTheDashboard({
          setFileInfo: props.setFileInfo,
          setParametersReview: props.setParametersReview,
          dir: directory,
          fileName: file?.name,
          dispatch,
          Notify,
          parameters,
          designerMode: mode,
        });
      }

      fileInfoRef.current = file;
      paramsRef.current = parameters;
    }
  }, [file, parameters]);

  useEffect(() => {
    if (!isEqual(urlObj, urlObjRef.current) && Object.keys(urlObj).length) {
      urlObjRef.current = urlObj;
      switch (urlObj?.mode) {
        case "dashboard":
          editTheDashboard({
            setFileInfo: props.setFileInfo,
            setParametersReview: props.setParametersReview,
            dir: urlObj?.dir,
            fileName: urlObj?.file,
            dispatch,
            Notify,
            parameters,
            designerMode: "open",
          });
          break;
        case "open":
          editTheDashboard({
            setFileInfo: props.setFileInfo,
            setParametersReview: props.setParametersReview,
            dir: urlObj?.dir,
            fileName: urlObj?.file,
            dispatch,
            Notify,
            parameters,
            designerMode: "open",
          });
          break;

        default:
          editTheDashboard({
            setFileInfo: props.setFileInfo,
            setParametersReview: props.setParametersReview,
            dir: urlObj?.dir,
            fileName: urlObj?.file,
            dispatch,
            Notify,
            parameters,
            designerMode: "edit",
          });
          if (!isEmpty(urlObj) && urlObj?.file && urlObj?.dir) {
            const { dir, file } = urlObj || {};
            setDir(dir);
            setFileName(file);
          }
          break;
      }
    }
  });

  const handlePrintExport = (format) => {
    hiAxiosRef.current = exportPrintedReportEfwdd(
      {
        file,
        format,
        urlParameters: { ...parameters },
        callback: setDownloadingInfo,
      },
      dispatch
    );
  };

  const renderTaskbar = () => {
    if (typeof props.renderTaskbar === "function") {
      props.renderTaskbar([
        {
          title: "Filters",
          icon: <FunnelPlotOutlined />,
          callback: () => {
            dispatch(updateParameterDrawerStatus(true));
          },
        },
        {
          title: "cacheTime",
          key: "cacheTime",
          icon: <FieldTimeOutlined />,
        },
        {
          title: "Export",
          icon: <ExportOutlined />,
          // callback: e=> toggleToolsAreaShelf(dispatch),
          dropdown: true,
          menu: getMenuOptions("designer", (format) => handlePrintExport(format))
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
              callback: () => {
                dispatch(updateReportsRefresh());
              },
            },
            {
              title: "Current Report",
              key: "currentReport",
              icon: <ReloadOutlined />,
              callback: () => {
                const directory = getDirectory({
                  path: file?.path,
                  name: file?.name,
                });
                editTheDashboard({
                  dir: directory,
                  fileName: file?.name,
                  dispatch,
                  Notify,
                  parameters,
                  designerMode: "open",
                });
              },
            },
          ],
        },
      ]);
    }
  };

  const onSaveAs = () => {
    const defaultReportName = savedReportName || "Dashboard_1";
    setShowFb({
      footerForm: {
        type: "Save",
        form: (
          <SaveItems
            formHeading="Report file name"
            onFormSumbit={saveCallback}
            saveButtonText="Save"
            cancelButtonText="Cancel"
            inputValue={defaultReportName}
            validateName={validateDesignerName}
          />
        ),
      },
    });
    setShowFbCompact(false);
    setShowFbFullView(true);
    dispatch(fileBrowserActions.setSearchResults(null));
    dispatch(fileBrowserActions.setShowFileBrowser(true));
  };

  const onSave = () => {
    const updateFileName = fileName.replace(/\.efwdd$/, '');
    if (!dashboardUUID) {
      onSaveAs();
    } else {
      if (dir && updateFileName) {
        saveTheDashboard({
          parameterDrawerStatus,
          designerSettings,
          dashboardVariables,
          components,
          dir,
          fileName: savedReportName,
          gridItemsData,
          layout,
          gridSettingsData,
          variables,
          dashboardConfig,
          css: dashboardCSS,
          script,
          printOptions,
          componentSettings,
          gridWidthOption,
          toggleIframes,
          dispatch,
          Notify,
          uuid: dashboardUUID,
        });
      } else {
        Notify.error({
          message: "Dir and/or filename cannot be empty",
          type: "Frontend",
        });
      }
    }
  };

  const editCallback = (directory, fileName) => {
    editTheDashboard({
      dir: directory,
      fileName,
      dispatch,
      Notify,
      designerMode: "edit",
    });
  };

  const saveCallback = (dir, fileName) => {
    setDir(dir.path);
    setFileName(fileName);
    saveTheDashboard({
      designerSettings,
      parameterDrawerStatus,
      dashboardVariables,
      components,
      dir: dir.path,
      fileName,
      gridItemsData,
      layout,
      gridSettingsData,
      variables,
      dashboardConfig,
      css: dashboardCSS,
      script,
      printOptions,
      componentSettings,
      gridWidthOption,
      toggleIframes,
      dispatch,
      Notify,
    });
  };
  const onDragFb = (_, d) => {
    setDragObj({ x: d.x, y: d.y + 470 })
  };

  const onDragFilter = (c, d) => {
    setFilterDragObj({ x: window.screen.availWidth - c.x, y: c.y })
  };

  const gridStyle = {
    textAlign: "center",
    width: "100%",
  };

  const [, drop] = useDrop(() => ({
    accept: "DragableBodyRow",
    drop: ({ record }) => {
      const rec = record;
      openInDashboardCallback({
        path: rec.path,
        name: rec.name,
        title: rec.title,
        resourceId: rec.resourceId,
        dispatch,
        gridIndex: gridIndex,
        gridItemsData,
      });
    },
  }));

  const designerModule = (
    <div className="hi-designer-module height100percent" ref={drop}>
      <Skeleton active loading={isLoading} className="height100percent">
        {previewMode && (
          <Button
            type="text"
            className="hi-designer-preview-close"
            onClick={() => {
              dispatch(setPreviewMode(false));
            }}
            icon={<CloseOutlined />}
          ></Button>
        )}
        {/* {filebrowserOptions && <HIFileBrowser {...filebrowserOptions} />} */}
        {!(isOpenMode || isDashboardMode || urlObj?.mode === "open") &&
          parameterSettings?.enable && !parameterSettings?.floatingFilter && (
            <Draggable
              onDrag={onDragFilter}
              defaultClassName={
                parameterDrawerStatus
                  ? "hi-hide"
                  : "hi-designer-parameter-drawer-button"
              }
            >
              <Button
                className="no-tansform"
                style={{
                  position: 'fixed',
                  top: filterDragObj?.y < 0 ? 0 : (filterDragObj?.y > (window.screen.availHeight - 150) ? window.screen.availHeight - 125 : filterDragObj?.y),
                  right: filterDragObj?.x < 0 ? 0 : (filterDragObj?.x > (window.screen.availWidth - 100) ? window.screen.availWidth - 100 : filterDragObj?.x),
                }}
                data-testid="hi-designer-parameters-button"
                icon={<FilterOutlined />}
                onClick={() => {
                  dispatch(updateParameterDrawerStatus(true));
                }}
              >
                <ShortCutText text="F" scLocation="DD">
                  Filters
                </ShortCutText>
              </Button>
            </Draggable>
          )}
        {!(isOpenMode || isDashboardMode || urlObj?.mode === "open") &&
          toggleToolsAreaShelf && (
            <Draggable
              defaultClassName="fb-compact"
              onDrag={onDragFb}
              handle=".handle"
            >
              <div
                data-testid="hi-designer-tools-shelf"
                className="no-tansform"
                style={{
                  display: "flex",
                  zIndex: 1000, // changed z-index for #7007
                  position: 'fixed',
                  top: dragObj.y < 0 ? 0 : (dragObj.y > (window.screen.availHeight - 200) ? window.screen.availHeight - 200 : dragObj.y),
                  left: dragObj.x < 0 ? 0 : (dragObj.x > (window.screen.availWidth - 115) ? window.screen.availWidth - 115 : dragObj.x),
                  transform: 'none',
                  height: 126,
                  width: 95
                }}
              >
                <div className="handle handle-bar">
                  <Card>
                    <Card.Grid hoverable={false} style={gridStyle}>
                      <Tooltip placement="right" title="Move around this panel">
                        <span>
                          <HIIcon name="hi-touch-drag" />
                        </span>
                      </Tooltip>
                    </Card.Grid>
                    <Card.Grid
                      hoverable={false}
                      style={{ ...gridStyle, cursor: "pointer" }}
                      onClick={() => {
                        setShowFb({
                          ...fileBrowserEditMode,
                          ...fbCompactOptions,
                        });
                        dispatch(openCompactFbBrower(false));
                        dispatch(replaceReportId(null))
                        setShowFbCompact((prev) => !prev);
                        setShowFbFullView(false);
                      }}
                    >
                      <Tooltip placement="right" title="View and select reports">
                        <div
                          style={{
                            display: "flex",
                            flexDirection: "column",
                            alignItems: "center",
                          }}
                        >
                          <BarChartOutlined
                            className={`${showFbCompact ? "highlight" : ""}`}
                            style={{ fontSize: "16px" }}
                          />
                          <div
                            style={{ marginTop: 0 }}
                            className={`${showFbCompact ? "highlight" : ""}`}
                          >
                            Reports
                          </div>
                        </div>
                      </Tooltip>
                    </Card.Grid>
                  </Card>
                </div>
                <TutorialInfo
                  elementKey="hi-designer-reports"
                  showFbCompact={showFbCompact}
                >
                  <div
                    className={`hi-dd-fb-compact-container ${showFbCompact ? "b1pxddd" : ""
                      }`}
                  >
                    {showFbCompact && (
                      <div id="dd-fb" className="fb-compact-wrapper">
                        {filebrowserOptions && (
                          <HIFileBrowser
                            {...filebrowserOptions}
                            mode="compact"
                            size="mini"
                          />
                        )}
                      </div>
                    )}
                    <Row
                      className={`sidebar-footer ${showFbCompact ? "show-compact " : "hide-compact"
                        }`}
                    >
                      <Col span={24}>
                        <SidebarFooter
                          fileBrowserOnClick={() => {
                            setShowFb(fileBrowserEditMode);
                            setShowFbCompact(false);
                            setShowFbFullView(true);
                            dispatch(
                              fileBrowserActions.setShowFileBrowser(true)
                            );
                          }}
                          onGlobalSearch={() => {
                            setShowFb(fileBrowserEditMode);
                            setShowFbCompact(false);
                            setShowFbFullView(true);
                            dispatch(
                              fileBrowserActions.setShowFileBrowser(true)
                            );
                          }}
                          onFbClose={() => {
                            setShowFb({
                              ...fileBrowserEditMode,
                              ...fbCompactOptions,
                            });
                            setShowFbCompact(true);
                            setShowFbFullView(false);
                          }}
                          color="white"
                        />
                      </Col>
                    </Row>
                  </div>
                </TutorialInfo>
              </div>
            </Draggable>
          )}
        {!isGlobalFbEnabled && showFbFullView && (
          <HIFileBrowser {...filebrowserOptions} onFbClose={() => {
            setShowFb({
              ...fileBrowserEditMode,
              ...fbCompactOptions,
            });
            setShowFbCompact(true);
            setShowFbFullView(false);
          }} />
        )}
        {<HIDashboardDesigner refresh={refresh} key={refresh} setRefresh={setRefresh} parameters={parameters} />}
        {isOpenMode && (
          <div className="dd-cache-time">
            <span className="dd-cache-time-text">
              {" Last Cached: "}
              {latestCachedTime}
            </span>
            <span className="dd-cache-time-icon">
              <HIIcon name="hi-hour-glass" />
            </span>
          </div>
          // <Tooltip title={`Last Cached: ${latestCachedTime}`}>
          //   <HourglassOutlined className="hi-designer-cached-time" />
          // </Tooltip>
        )}
      </Skeleton>
      <Modal
        visible={!!downloadingInfo}
        className="hi-dd-download-modal"
        title="Downloading"
        okText={() => null}
        cancelText="Cancel"
        maskClosable={false}
        // onOk={() => {
        //   remove(warningVisible.deleteKey);
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
      <Modal
        visible={!!editModeModal}
        okText={"Leave"}
        onOk={() => {
          handleEditMode();
        }}
        onCancel={() => {
          setEditModeModal(false);
        }}
      >
        Are you sure you want to leave?
      </Modal>
      <HIShortcuts
        moduleData={{
          name: "DashboardDesigner",
          commonRefs: { saveRef, undoRef, redoRef },
        }}
      />
    </div>
  );

  const taskbarItems = designerTaskbarItems({
    designerPast,
    designerFuture,
    isSaving,
    onSave,
    onSaveAs,
    dispatch,
    refs,
    handleUndo,
    handleRedo,
    setRefresh,
  });

  let content = (
    <HILayout
      header={<HINavbar taskbar={taskbarItems} />}
      content={designerModule}
    />
  );

  if (isOpenMode || isDashboardMode) {
    content = designerModule;
  }

  return content;
};
export { DashboardDesigner };
