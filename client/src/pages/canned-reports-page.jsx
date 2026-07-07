import {
  CloseOutlined,
  ExportOutlined,
  FieldTimeOutlined,
  FilterOutlined,
  FolderViewOutlined,
  FunnelPlotOutlined,
  RedoOutlined,
  ReloadOutlined,
  SaveFilled,
  SaveOutlined,
  StopOutlined,
  SyncOutlined,
  UndoOutlined
} from "@ant-design/icons";
import { Pagination, Row, Tooltip } from "antd";
import { isEmpty, isEqual } from "lodash";
import { useEffect, useRef, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { v4 as uuidv4 } from "uuid";
import requests from "../base/requests";
import { postDownloadRequest } from "../base/service";
import {
  CannedReports,
  HIFileBrowser,
  HINavbar,
  HITabs,
  PreviewArea,
} from "../components";
import LoadingBar from "../components/common/components/hi-loading-bar";
import { CustomIcon } from "../components/common/custom-icons/CustomIcon";
import {
  CustomCannedSkeleton,
  getCanvasDimensions,
} from "../components/common/custom-icons/CustomSkeletons/cannedReports/customCannedSkeleton";
import "../components/common/custom-icons/CustomSkeletons/cannedReports/customCannedSkeleton.scss";
import TutorialInfo from "../components/common/hi-tutorial";
import HIIcon from "../components/common/icons/hi-icons";
import {
  hcrDSParameter,
  hcrDSQuery,
  hcrParaDateAndTimeRange,
  hcrParaDateRange,
  shortcutKeys,
} from "../components/hi-canned-reports/hcr-constants";
import {
  getErrorMessage,
  handleStreamResponse,
  NoDataTemplate,
} from "../components/hi-canned-reports/hcrCanvas/hcrCanvasPaneHelperMethods";
import { HCRFiltersDrawer } from "../components/hi-canned-reports/hcrFilters/hcrFiltersDrawer";
import {
  createHTMLTable,
  getDefaulPropertiesFromExportProperties,
  getHcrParameterFilters,
  getPreviewFormData,
  getSubDataSetsFromReportState,
  getTableStylesFromReportState,
  handleEmptyPaneCheck,
  handleSaveHcr,
  handleUrlParamsFilters,
  parseHCRNodesData,
  reportViewHcrGenerateReport,
  updateDateRangeFilterValues,
  validateHcrName,
  validateNodes,
} from "../components/hi-canned-reports/hcrHelperMethods";
import { ShareFinalModal } from "../components/hi-fileBrowser/components";
import SaveItems from "../components/hi-fileBrowser/SaveItems";
import notify from "../components/hi-notifications/notify";
import { useDebouncedCallback } from "../hooks";
import useExportOptions from "../hooks/useExportOptions";
import HILayout from "../layouts/hi-layout";
import { appActions, fileBrowserActions, hcrActions } from "../redux/actions";
import {
  clearHcrUndoRedoHistory,
  hcrRedo,
  hcrUndo
} from "../redux/actions/hcr.actions";
export let flowchartInstance, nodesPositions, queryTempuuidsMap;

const CannedReportsPage = (props) => {
  const hcrTabData = useSelector(
    (state) => state.cannedReports.present.hcrTabData,
  );

  const activeTab = useSelector(
    (state) =>
      state.cannedReports.present.hcrTabData?.panes?.find(
        (pane) => pane.key === hcrTabData.activeKey
      ) || {},
  );

  const {
    key: reportKey,
    saveDetails,
    groupCount,
    groupsOrder,
    selectedQueryId,
    title: reportName = "",
    canvasProperties = {},
    selectedDS = {},
    dsPaneTypes = [],
    hcrDiagramNodesData = [],
    hcrFiltersDrawerStatus,
    isPreviewing,
    isUpdatingCanvasPageStyles,
    hcrExportProperties = [],
    mode: hcrMode = "",
    defaultPropertiesAdded: isHCRDefaultPropertiesAdded,
    previewTag = "",
    hcrFBFor,
    pageDetails = {},
    hcrPreviewData = {},
    subDataSets = [],
    tableStyles = []
  } = activeTab || {};

  const { hcrExportPropertiesData = {} } = useSelector((state) => state.cannedReports.present) || {};

  const hCROldConfigurations = useSelector(
    (state) => state.cannedReports.present.hCROldConfigurations,
  );
  const saveDetailsisHcrLoaded = useSelector(
    (state) => state.cannedReports.present.isHcrLoaded,
    isEqual,
  );
  const isGlobalFbEnabled = useSelector(
    (state) => state.fileBrowser.globalFbEnabled,
  );
  const showFileBrowser = useSelector(
    (state) => state.fileBrowser.showFileBrowser,
  );
  const isShareModalVisible = useSelector(
    (state) => state.fileBrowser.isShareModalVisible,
  );
  const hcrPast = useSelector((state) => state.cannedReports.past);
  const hcrFuture = useSelector((state) => state.cannedReports.future);
  const editModeInfo = useSelector((state) => state.app.editModeInfo);
  const isStreamToggle = useSelector(
    (state) => state.app.applicationSettingsData?.streamResponse,
  );

  const { getMenuOptions, getDropdownOptions } = useExportOptions();
  const [showImage, setShowImage] = useState(true);

  const { exportType } = hCROldConfigurations || {};
  const dispatch = useDispatch();

  const {
    storeHCROldConfigurations,
    storeHCRPreviewDetails,
    onAddingHcrTabData,
    setHcrExportProperties,
    hcrAddDefaultExportProperties,
  } = hcrActions;
  const Notify = notify(dispatch);

  const [saveType, setSaveType] = useState("save");
  const tabNumbers = hcrTabData.panes.map((ele) => ele.key);
  const editCBRef = useRef(null);
  flowchartInstance = useRef(null);
  nodesPositions = useRef({});
  queryTempuuidsMap = useRef(null);
  const queries =
    dsPaneTypes?.find((ele) => ele.dataSourcePane === hcrDSQuery) || {};
  const parameters =
    dsPaneTypes?.find((pane) => pane.dataSourcePane === hcrDSParameter) || {};
  const presentActiveTabUuid = hcrTabData.panes?.find(
    (pane) => pane.key === hcrTabData.activeKey,
  )?.uuid;
  const shareRef = useRef(null);
  const { previewParameters } = canvasProperties || {};
  const [currentShortCut, setCurrentShortCut] = useState("");
  const [reportExport, setReportExport] = useState("");
  const {
    previewRequest,
    hcrConfigurationsRequest,
    saveHcrRequest,
    editHcrRequest,
    getExportProperties,
  } = requests.cannedReport(dispatch);
  const [appliedFilters, setAppliedFilters] = useState([]);
  const { urlObj } = props;

  const canUndo = hcrPast.length > 0;
  const canRedo = hcrFuture.length > 0;

  const {
    isPreviewLoading = false,
    isAborted = false,
    isStreamComplete = false,
    previewTotalPageCount = 1,
    streamFirstPageData = null,
    previewError = null,
    updatedPages = []
  } = hcrPreviewData || {}

  const lastSelectedNodeRef = useRef(null);
  const previewClosedRef = useRef(false);
  const apiRef = useRef(null);
  const nextPageRef = useRef(null);
  const prevPageRef = useRef(null);

  const getApiInstance = (getRef) => {
    apiRef.current = getRef;
  };

  const handleAbort = () => {
    if (isAborted || isStreamComplete || !apiRef.current) {
      return;
    }
    apiRef.current?.abort({
      onSuccess: (data) => {
        handlePreviewError(data?.response?.message || "")
      }
    });
    handlePreviewUpdate({ isStreamComplete: true, isAborted: true, isPreviewLoading: false })
  };

  const handlePreviewUpdate = (values, type = "update") => {
    dispatch(hcrActions.hcrUpdatePreview({ type, values, reportKey }));
  }
  const handleChangePageValue = (value) => {
    handlePreviewUpdate({ pageVal: value })
  }
  const handlePreviewError = (error) => {
    handlePreviewUpdate({ previewError: error })
  }
  const handlePreviewLoading = (value) => {
    handlePreviewUpdate({ isPreviewLoading: value })
  }

  const handleStreamResSuccess = (data) => {
    let dataObj = Object.values(data)?.[0] || {}
    const updatedValues = {
      streamFirstPageData: dataObj,
      isPreviewLoading: false,
    }
    if (dataObj?.reportPageInfo) {
      const { totalPageCount, currentPageNo = 0 } = dataObj?.reportPageInfo || {}
      if (totalPageCount) {
        updatedValues.previewTotalPageCount = totalPageCount
        dispatch(hcrActions.handlePageDetails({
          reportKey,
          pageDetails: { totalPageCount: totalPageCount * 10, currentPageNo: parseInt(currentPageNo || "0") + 1 }
        }));
      }
    }
    handlePreviewUpdate(updatedValues)
    dispatch(hcrActions.handlePreviewTag({ previewTag: dataObj?.response, reportKey }))
  }

  const handleStreamResError = (err) => {
    const errorData = getErrorMessage(err);
    handlePreviewUpdate({ previewError: errorData, isPreviewLoading: false })
  }

  const handleStreamResComplete = () => {
    handlePreviewUpdate({ isStreamComplete: true, isPreviewLoading: false })
  }

  const handlePageUpdates = (data) => {
    if (data?.length) {
      handlePreviewUpdate({
        pages: data,
      }, "add_updatedPages")
    }
  }

  const onStreamSuccess = (res) => {
    const { type, data } = handleStreamResponse(res);
    switch (type) {
      case "complete":
        handleStreamResComplete();
        break;
      case "error":
        handleStreamResError(data);
        break;
      case "stream":
        handleStreamResSuccess(data);
        break
      case "page_count":
        handlePreviewUpdate({
          previewTotalPageCount: data.totalPageCount,
        })
        break
      case "update":
        handlePageUpdates(data);
        break;
      default:
        break;
    }
  }

  const resetStream = (resetInfo = false) => {
    handlePreviewUpdate({
      pageVal: 1,
      isStreamComplete: false,
    })

    if (resetInfo) {
      resetStreamInfo()
    }
  }

  const resetStreamInfo = () => {
    handlePreviewUpdate({
      previewTotalPageCount: 1,
      streamFirstPageData: null,
    })
    dispatch(
      hcrActions.handlePageDetails({
        reportKey,
        pageDetails: {
          totalPageCount: 1,
          currentPageNo: 1,
        },
        updatedPages: []
      }),
    );
    dispatch(hcrActions.handlePreviewTag({ previewTag: "", reportKey }))
  }

  useEffect(() => {
    if (props?.parameters?.print && props.mode && isPreviewing) {
      setReportExport(props?.parameters?.print);
    }
  }, [props?.parameters?.print, isPreviewing]);

  function fetchPreviewDetails({
    updatedPageNo,
    isInitialPreview,
    isFilterOpn,
    isCache,
    openMode,
  }) {
    handlePreviewUpdate({ isAborted: false, previewError: null })
    const query =
      queries.menu.find((query) => query.id === selectedQueryId) || {};
    const { isValid, bandLimits } = validateNodes({
      flowchartInstance,
      hcrDiagramNodesData,
      Notify,
      dispatch,
      canvasProperties,
    });
    if (isValid) {

      let reqFilters = [];
      if (isInitialPreview) {
        reqFilters = getHcrParameterFilters({
          parameters,
          hcrDiagramNodesData,
        });

        setAppliedFilters(reqFilters);
      }
      if (isFilterOpn) {
        dispatch(hcrActions.updateHcrFiltersDrawerStatus());
      } else {
        previewClosedRef.current = false;
        handlePreviewUpdate({
          isPreviewLoading: true,
          pageVal: 1,
        })

        let formData = getPreviewFormData({
          hcrDiagramNodesData,
          flowchartInstance,
          query,
          canvasProperties,
          filters: isInitialPreview ? reqFilters : appliedFilters,
          reportName,
          bandLimits,
          updatedPageNo,
          saveDetails,
          isCache,
          openMode,
          allQueries: queries.menu,
          hcrExportProperties,
          tempUUIDsMap: queryTempuuidsMap.current,
          subDataSets,
          tableStyles
        })
        const isStream = isStreamToggle && (!streamFirstPageData || isCache)
        if (isStreamToggle && !isStream && !isCache) {
          formData = {
            designCacheKey: streamFirstPageData?.designCacheKey || "",
            page: updatedPageNo - 1
          }
        }

        handlePreviewUpdate({ previewError: null })

        const previewInstance = previewRequest(formData,
          (res) => {
            if (previewClosedRef.current) return;

            if (isStream) {
              onStreamSuccess(res)
            } else {
              dispatch(hcrActions.handlePreviewTag({ previewTag: res.response, reportKey }));
              dispatch(
                hcrActions.handlePageDetails({
                  pageDetails: {
                    totalPageCount: res?.reportPageInfo?.totalPageCount * 10 || 10,
                    currentPageNo: parseInt(res?.reportPageInfo?.currentPageNo) + 1,
                  },
                  reportKey
                }),
              );
              handlePreviewUpdate({ isPreviewLoading: false })
            }
            dispatch(appActions.changeLastModified());
          },
          (e) => {
            handlePreviewUpdate({ isPreviewLoading: false, previewError: "" })
          },
          isStream,
        );
        getApiInstance(previewInstance);
      }
    } else {
      dispatch(hcrActions.handleTogglePreview(false));
      handlePreviewUpdate({ isPreviewLoading: false })
    }
  }
  const handleBack = () => {
    handlePreviewUpdate({ previewError: null, isAborted: false })
    if (pageDetails?.currentPageNo > 1) {
      const prevPage = pageDetails.currentPageNo - 1;
      dispatch(
        hcrActions.handlePageDetails({
          reportKey,
          pageDetails: {
            ...pageDetails,
            currentPageNo: prevPage,
          }
        }),
      );
      handlePreviewUpdate({ isPreviewLoading: true })
      fetchPreviewDetails({
        updatedPageNo: prevPage,
      });
      return;
    }
    if (props.mode?.includes("open")) {
      handlePreviewUpdate({ isPreviewLoading: true })
      fetchPreviewDetails({
        updatedPageNo: pageDetails?.currentPageNo || 1,
        openMode: true,
      });
      return;
    }
    dispatch(hcrActions.handleTogglePreview(false));
  };
  useEffect(() => {
    if (currentShortCut) {
      const pgCount = pageDetails.totalPageCount / 10;
      switch (currentShortCut) {
        case "save":
          saveHcr();
          break;
        case "saveAs":
          saveAsHcr();
          break;
        case "preview":
          if (isPreviewing) {
            fetchPreviewDetails({});
          } else {
            const unHidedFilterList = parameters?.menu?.filter(
              (ele) => !ele?.canvasValues?.isChecked,
            );
            if (unHidedFilterList?.length && hcrDiagramNodesData?.length) {
              fetchPreviewDetails({
                isFilterOpn: true,
                isInitialPreview: true,
              });
            } else {
              dispatch(hcrActions.handleTogglePreview(true));
              fetchPreviewDetails({ isInitialPreview: true });
            }
          }
          break;
        case "validateNodes":
          if (!isPreviewing) {
            validateNodes({
              flowchartInstance,
              hcrDiagramNodesData,
              Notify,
              dispatch,
              canvasProperties,
            });
          }
          break;
        case "toggleParameterFilter":
          if (isPreviewing) {
            dispatch(hcrActions.updateHcrFiltersDrawerStatus());
          }
          break;
        case "closePreview":
          if (isPreviewing) {
            dispatch(hcrActions.handleTogglePreview(false));
            resetStream();
            resetStreamInfo();
          }
          break;
        case "nxtPg":
          if (isPreviewing) {
            if (pageDetails.currentPageNo < pgCount) {
              handlePreviewUpdate({ isPreviewLoading: true })
              fetchPreviewDetails({
                updatedPageNo: pageDetails.currentPageNo + 1,
              });
              dispatch(
                hcrActions.handlePageDetails({
                  reportKey,
                  pageDetails: {
                    ...pageDetails,
                    currentPageNo:
                      pageDetails.currentPageNo < pgCount
                        ? pageDetails.currentPageNo + 1
                        : pageDetails.currentPageNo,
                  }
                }),
              );
            }
          }
          break;
        case "lstPg":
          if (isPreviewing) {
            if (pageDetails.currentPageNo !== pgCount) {
              handlePreviewUpdate({ isPreviewLoading: true })
              fetchPreviewDetails({ updatedPageNo: pgCount });
              dispatch(
                hcrActions.handlePageDetails({
                  reportKey,
                  pageDetails: {
                    ...pageDetails,
                    currentPageNo:
                      pageDetails.currentPageNo !== pgCount
                        ? pgCount
                        : pageDetails.currentPageNo,
                  }
                }),
              );
            }
          }
          break;
        case "prevPg":
          if (isPreviewing) {
            if (pageDetails.currentPageNo > 1) {
              handlePreviewUpdate({ isPreviewLoading: true })
              fetchPreviewDetails({
                updatedPageNo: pageDetails.currentPageNo - 1,
              });
              dispatch(
                hcrActions.handlePageDetails({
                  reportKey,
                  pageDetails: {
                    ...pageDetails,
                    currentPageNo:
                      pageDetails.currentPageNo > 1
                        ? pageDetails.currentPageNo - 1
                        : pageDetails.currentPageNo,
                  }
                }),
              );
            }
          }
          break;
        case "initialPg":
          if (isPreviewing) {
            if (pageDetails.currentPageNo !== 1) {
              handlePreviewUpdate({ isPreviewLoading: true })
              fetchPreviewDetails({ updatedPageNo: 1 });
              dispatch(
                hcrActions.handlePageDetails({
                  reportKey,
                  pageDetails: {
                    ...pageDetails,
                    currentPageNo:
                      pageDetails.currentPageNo !== 1
                        ? 1
                        : pageDetails.currentPageNo,
                  }
                }),
              );
            }
          }
          break;
        default:
          break;
      }
      setCurrentShortCut("");
    }
  }, [currentShortCut]);

  function handleShrtCutKeys(e) {
    if (e.ctrlKey && e.keyCode === 83 && !e.shiftKey) {
      // save ctrl+s
      e.preventDefault();
      setCurrentShortCut("save");
    } else if (e.ctrlKey && e.shiftKey && e.keyCode === 83) {
      //save As ctrl+shift+s
      e.preventDefault();
      setCurrentShortCut("saveAs");
    } else if (e.ctrlKey && e.keyCode === 80 && !e.shiftKey) {
      // preview ctrl+p
      e.preventDefault();
      setCurrentShortCut("preview");
    } else if (e.ctrlKey && e.shiftKey && e.keyCode === 81) {
      // validate nodes ctrl+shift+q
      e.preventDefault();
      setCurrentShortCut("validateNodes");
    } else if (e.ctrlKey && e.keyCode === 77) {
      // parameter filter drawer toggle (ctrl+m)
      e.preventDefault();
      setCurrentShortCut("toggleParameterFilter");
    } else if (e.keyCode === 27) {
      // closing preview (esc)
      e.preventDefault();
      setCurrentShortCut("closePreview");
    } else if (e.ctrlKey && e.keyCode === 39 && !e.shiftKey) {
      // Arrow right nxtPg
      e.preventDefault();
      setCurrentShortCut("nxtPg");
    } else if (e.ctrlKey && e.shiftKey && e.keyCode === 39) {
      //  last pg
      e.preventDefault();
      setCurrentShortCut("lstPg");
    } else if (e.ctrlKey && e.keyCode === 37 && !e.shiftKey) {
      // Arrow left prevPg
      e.preventDefault();
      setCurrentShortCut("prevPg");
    } else if (e.ctrlKey && e.shiftKey && e.keyCode === 37) {
      // initial pg
      e.preventDefault();
      setCurrentShortCut("initialPg");
    } else if (e.ctrlKey && e.keyCode === 90) {
      e.preventDefault();
      dispatch(hcrUndo());
    } else if (e.ctrlKey && e.keyCode === 89) {
      e.preventDefault();
      dispatch(hcrRedo());
    }
  }

  useEffect(() => {
    !props.mode?.includes("open") &&
      !Object.keys(hCROldConfigurations).length &&
      hcrConfigurationsRequest(
        { contentId: "hcrConfigurations" },
        "content/static/getContents",
        (res) => {
          res.HCR = JSON.parse(res.HCR);
          dispatch(storeHCROldConfigurations(res));
        },
        (e) => {
          console.log(e);
        },
      );
    document.addEventListener("keydown", handleShrtCutKeys);
    if (editModeInfo) {
      if (editModeInfo.extension === "hcr") {
        handleHcrEdit({
          path: editModeInfo.dir,
          name: editModeInfo.file,
        });
        dispatch(appActions.setEditModeInfo(null));
      }
    } else if (
      urlObj &&
      urlObj?.dir &&
      urlObj?.file &&
      typeof urlObj.dir === "string" &&
      typeof urlObj.file === "string"
    ) {
      if (urlObj.file?.includes(".hcr") && urlObj.dir.length) {
        handleHcrEdit({ path: urlObj.dir, name: urlObj.file });
      }
    } else {
      dispatch(
        onAddingHcrTabData({
          pane: { title: "Untitled 1", key: "1", uuid: uuidv4() },
          activeKey: "1",
          undoRedoAction: false,
        }),
      );
    }
    return () => {
      document.removeEventListener("keydown", handleShrtCutKeys);
      dispatch(hcrActions.resetHcrState());
      dispatch(clearHcrUndoRedoHistory());
      resetStream();
    };
  }, []);

  useEffect(() => {
    if (hcrTabData.panes.length) {
      setShowImage(false);
    } else {
      setShowImage(true);
    }
  }, [hcrTabData.panes]);

  function add() {
    const { panes } = hcrTabData;
    if (panes.length < 4) {
      const newTabNum = ["1", "2", "3", "4"].find(
        (num) => !tabNumbers.includes(num),
      );
      dispatch(
        onAddingHcrTabData(
          {
            pane: {
              title: "Untitled " + newTabNum,
              key: newTabNum,
              uuid: uuidv4(),
            },
            activeKey: newTabNum,
          },
          true,
        ),
      );
    } else {
      notify(dispatch).warning({
        message: "Maximum Tabs Created",
        type: "Front End",
      });
    }
  }

  const remove = (targetKey) => {
    dispatch(hcrActions.RemoveHcrTab(targetKey));
  };

  function saveHcr() {
    const { isValid, bandLimits } = validateNodes({
      flowchartInstance,
      hcrDiagramNodesData,
      Notify,
      dispatch,
      canvasProperties,
    });
    const { menu = [] } = parameters || {};
    const rangeParameters = menu.filter((f) =>
      [hcrParaDateRange, hcrParaDateAndTimeRange].includes(
        f?.canvasValues?.filterType,
      ),
    );
    if (rangeParameters.length) {
      let areRangeFiltersValid = rangeParameters.every(
        (f) => f?.canvasValues?.start && f?.canvasValues?.end,
      );
      if (!areRangeFiltersValid) {
        notify(dispatch).warning({
          message:
            "Range filters are not completed, please select start and end range parameters in all range filters.",
          type: "Front End",
        });
        return;
      }
    }
    if (isValid) {
      dispatch(hcrActions.setHcrFilebrowserFor("save"));
      setSaveType("save");
      if (saveDetails?.uuid) {
        const query =
          queries.menu.find((query) => query.id === selectedQueryId) || {};
        const previewFormData = getPreviewFormData({
          hcrDiagramNodesData,
          flowchartInstance,
          query,
          canvasProperties,
          filters: appliedFilters,
          reportName,
          bandLimits,
          isHCR: true,
          saveDetails,
          allQueries: queries.menu,
          hcrExportProperties,
          tempUUIDsMap: queryTempuuidsMap.current,
          subDataSets,
          tableStyles
        });
        handleSaveHcr({
          selectedQueryId,
          type: "save",
          location: saveDetails.location,
          fileName: saveDetails.hcrName,
          dispatch,
          saveHcrRequest,
          presentActiveTabUuid,
          flowchartInstance,
          dsPaneTypes,
          hcrDiagramNodesData,
          uuid: saveDetails?.uuid,
          canvasProperties,
          groupCount,
          groupsOrder,
          previewFormData,
          hcrExportProperties,
          handleUpdateQueryuuids,
          subDataSets,
          tableStyles,
          tempUUIDsMap: queryTempuuidsMap.current,
        });
        return;
      }
      dispatch(fileBrowserActions.setSearchResults(null));
      resetStream();
      dispatch(fileBrowserActions.setShowFileBrowser(true));
    }
  }

  function saveAsHcr() {
    dispatch(hcrActions.setHcrFilebrowserFor("save"));
    setSaveType("saveAs");
    dispatch(fileBrowserActions.setSearchResults(null));
    resetStream();
    dispatch(fileBrowserActions.setShowFileBrowser(true));
  }

  let taskbar = [
    {
      tooltip: "Save",
      icon: <SaveOutlined />,
      dropdown: [
        {
          tooltip: "Save",
          name: "Save",
          icon: <SaveOutlined />,
          callBack: () => {
            saveHcr();
          },
        },
        {
          tooltip: "Save As",
          name: "Save As",
          icon: <SaveFilled />,
          callBack: () => {
            saveAsHcr();
          },
        },
      ],
      itemClz: "hcr-mr-18",
    },
    {
      tooltip: "Undo",
      icon: (
        <UndoOutlined
          style={{
            color: canUndo ? "#000" : "#ccc",
            cursor: canUndo ? "pointer" : "not-allowed",
          }}
        />
      ),
      callBack: () => canUndo && dispatch(hcrUndo()),
      itemClz: "hcr-mr-18",
      disabled: !canUndo,
    },
    {
      tooltip: "Redo",
      icon: (
        <RedoOutlined
          style={{
            color: canRedo ? "#000" : "#ccc",
            cursor: canRedo ? "pointer" : "not-allowed",
          }}
        />
      ),
      callBack: () => canRedo && dispatch(hcrRedo()),
      itemClz: "hcr-mr-18",
      disabled: !canRedo,
    },
  ];

  useEffect(() => {
    return () => dispatch(clearHcrUndoRedoHistory());
  }, []);

  const previewShowItem = {
    tooltip: "Preview",
    icon: <FolderViewOutlined />,
    callBack: () => {
      if (isUpdatingCanvasPageStyles) {
        Notify.warning({
          type: "Frontend",
          message:
            "Page Style not saved. Please save for the changes to reflect.",
        });
        return;
      }
      lastSelectedNodeRef.current = null;
      handlePreviewUpdate({ isAborted: false })

      const reqObj = { isInitialPreview: true };
      const unHidedFilterList = parameters?.menu?.filter(
        (ele) => !ele?.canvasValues?.isChecked,
      );
      if (unHidedFilterList?.length && hcrDiagramNodesData?.length) {
        reqObj.isFilterOpn = true;
      }
      if (!isPreviewing && !reqObj.isFilterOpn) {
        dispatch(hcrActions.handleTogglePreview(true));
        handlePreviewUpdate({ isPreviewLoading: true })
      }
      fetchPreviewDetails(reqObj);
    },
    tutorialKey: "hcr-preview",
    itemClz: "hcr-mr-18",
  };

  const previewCloseItem = {
    tooltip: "close preview",
    icon: <CloseOutlined />,
    callBack: () => {
      previewClosedRef.current = true;
      resetStream();
      resetStreamInfo();
      if (isStreamToggle && !isAborted && apiRef.current && !isStreamComplete) {
        handleAbort();
      }
      if (isPreviewing) {
        dispatch(hcrActions.handleTogglePreview(false));
      }
      if (hcrFiltersDrawerStatus) {
        dispatch(hcrActions.updateHcrFiltersDrawerStatus());
      }
    },
    itemClz: "hcr-mr-18",
  };
  const debouncedFetchPreview = useDebouncedCallback((pageNumber) => {
    handlePreviewUpdate({ isPreviewLoading: true })
    fetchPreviewDetails({ updatedPageNo: pageNumber });
  }, 300);
  function onPageChange(val) {
    dispatch(
      hcrActions.handlePageDetails({
        reportKey,
        pageDetails: { ...pageDetails, currentPageNo: val }
      }),
    );
    debouncedFetchPreview(val);
    if (updatedPages.length) {
      handlePreviewUpdate({}, "reset_updatedPages")
    }
  }

  const onRefreshUpdatePage = (pageNumber) => {
    onPageChange(pageNumber);
  }

  const pagination = (openMode = false) => {
    const showStopIcon = isStreamToggle && !isStreamComplete;
    const isUpdateAvailable = updatedPages?.includes(pageDetails?.currentPageNo);
    return (
      <Row
        align={openMode ? "" : "top"}
        justify={"center"}
        style={{
          margin: openMode ? "10px 0" : 0,
          ...(openMode ? { alignItems: "baseline" } : {})
        }}
      >
        <Pagination
          simple
          size={"small"}
          current={pageDetails?.currentPageNo || 1}
          onChange={onPageChange}
          defaultCurrent={1}
          total={isStreamToggle ? previewTotalPageCount : pageDetails?.totalPageCount}
          {...(isStreamToggle ? { pageSize: 1 } : {})}
          showSizeChanger={false}
          showTitle={false}
          showLessItems={true}
          itemRender={(current, type, originalElement) => {
            if (type === "prev") {
              return (
                <Tooltip title="Go to Previous Page"><span ref={prevPageRef}>{originalElement}</span></Tooltip>
              );
            }
            if (type === "next") {
              return (
                <Tooltip title="Go to Next Page"><span ref={nextPageRef}>{originalElement}</span></Tooltip>
              );
            }
            return originalElement;
          }}
        />
        {
          showStopIcon ?
            <Tooltip title="Abort streaming">
              <StopOutlined
                style={{
                  fontSize: openMode ? 11 : 18,
                  cursor: isStreamComplete ? "not-allowed" : "pointer",
                  opacity: isStreamComplete ? 0.5 : 1
                }}
                onClick={handleAbort}
                disabled={isStreamComplete}
              />
            </Tooltip>
            : null
        }
        {
          isStreamToggle ? <Tooltip title={isUpdateAvailable ? "Update is available for this page, refresh to get latest data." : "Refresh page"}>
            <div className="hcr-refresh-icon-container">
              {isUpdateAvailable ? <div className='hcr-refresh-icon-update-dot' /> : null}
              <SyncOutlined
                style={{
                  fontSize: openMode ? 11 : 18,
                  marginLeft: showStopIcon ? 10 : 0,
                  cursor: "pointer",
                }}
                onClick={() => onRefreshUpdatePage(pageDetails?.currentPageNo)}
              />
            </div>
          </Tooltip> : null
        }
      </Row>
    )
  }

  const reportPagination = {
    tooltip: null,
    icon: pagination(),
    callBack: () => { },
    itemClz: "hcr-mr-18 hcr-pagination",
  };

  useEffect(() => {
    if (!isShareModalVisible) {
      shareRef.current = false;
    }
  }, [isShareModalVisible]);

  useEffect(() => {
    if (previewTotalPageCount > 1) {
      dispatch(hcrActions.handlePageDetails({
        reportKey,
        pageDetails: { ...pageDetails, totalPageCount: previewTotalPageCount * 10 }
      }));
    }
  }, [previewTotalPageCount])

  const handleShare = () => {
    if (!saveDetails?.location || !saveDetails?.uuid) {
      Notify.warning({
        message: "Please save the canned report to a valid location!!!",
      });
      return;
    }
    shareRef.current = true;
    dispatch(fileBrowserActions.setShareModalVisibility());
  };

  const taskbarShare = {
    tooltip: "Share",
    icon: <HIIcon name="hi-share" />,
    callBack: handleShare,
    itemClz: "hcr-mr-18",
  };

  const hcrShrtCuts = {
    tooltip: createHTMLTable(shortcutKeys),
    icon: <HIIcon name="hi-hcr-shrt-cut-arw" />,
    callBack: () => { },
    itemClz: "hcr-mr-18",
  };

  // taskbar = showImage ? taskbar : [hcrShrtCuts, ...taskbar, taskbarShare, previewShowItem];
  taskbar = [hcrShrtCuts, ...taskbar, taskbarShare, previewShowItem];

  function onExport(format) {
    format = format.toLowerCase();
    const query =
      queries.menu.find((query) => query.id === selectedQueryId) || {};
    const { bandLimits } = validateNodes({
      flowchartInstance,
      hcrDiagramNodesData,
      Notify,
      dispatch,
      canvasProperties,
    });
    const previewFormData = getPreviewFormData({
      hcrDiagramNodesData,
      flowchartInstance,
      query,
      canvasProperties,
      filters: appliedFilters,
      reportName,
      bandLimits,
      updatedPageNo: pageDetails?.currentPageNo,
      isHCR: true,
      isExport: true,
      format,
      saveDetails,
      allQueries: queries.menu,
      hcrExportProperties,
      tempUUIDsMap: queryTempuuidsMap.current,
      subDataSets,
      tableStyles
    });
    postDownloadRequest({
      dispatch,
      formData: previewFormData,
      reportName,
      isReportNameInResponse: true,
    });
  }

  useEffect(() => {
    if (reportExport) {
      onExport(reportExport);
      setReportExport("");
    }
  }, [reportExport]);

  const taskbarExport = {
    tooltip: "Export",
    icon: <ExportOutlined />,
    dropdown: getDropdownOptions("hcr", (format) => setReportExport(format)),
    itemClz: "hcr-mr-18",
  };

  const taskbarFilter = {
    tooltip: "Filter",
    icon: <FilterOutlined />,
    callBack: () => {
      dispatch(hcrActions.updateHcrFiltersDrawerStatus());
    },
    itemClz: "hcr-mr-18",
  };

  if (isPreviewing) {
    taskbar = [
      reportPagination,
      previewCloseItem,
      taskbarExport,
    ];
    if (previewParameters.showParameters) {
      taskbar = [...taskbar, taskbarFilter];
    }
    taskbar = [...taskbar, hcrShrtCuts];
  }

  const handleTabActiveKey = ({ activeKey }) => {
    dispatch(hcrActions.setHcrTabActiveKey(activeKey));
  };

  const handleUpdateQueryuuids = (connectionDetails) => {
    if (!connectionDetails) return;
    // let queryPane = dsPanes.find((pane) => pane.dataSourcePane === hcrDSQuery);
    // const tempUUIDsMap = queryPane?.menu?.reduce((acc, { id, temp_uuid }) => {
    //     acc[id] = temp_uuid;
    //     return acc;
    // }, {})
    queryTempuuidsMap.current = { temp_uuid: connectionDetails.temp_uuid };
  };

  const resetQueryuuids = () => {
    queryTempuuidsMap.current = null;
  };

  const onFormSumbit = (arg, name) => {
    // range filter condition check
    const { menu = [] } = parameters || {};
    const rangeParameters = menu.filter((f) =>
      [hcrParaDateRange, hcrParaDateAndTimeRange].includes(
        f?.canvasValues?.filterType,
      ),
    );
    if (rangeParameters.length) {
      let areRangeFiltersValid = rangeParameters.every(
        (f) => f?.canvasValues?.start && f?.canvasValues?.end,
      );
      if (!areRangeFiltersValid) {
        notify(dispatch).warning({
          message:
            "Range filters are not completed, please select start and end range parameters in all range filters.",
          type: "Front End",
        });
        return;
      }
    }

    const query =
      queries.menu.find((query) => query.id === selectedQueryId) || {};
    const { bandLimits } = validateNodes({
      flowchartInstance,
      hcrDiagramNodesData,
      Notify,
      dispatch,
      canvasProperties,
    });
    const previewFormData = getPreviewFormData({
      hcrDiagramNodesData,
      flowchartInstance,
      query,
      canvasProperties,
      filters: appliedFilters,
      reportName,
      bandLimits,
      isHCR: true,
      saveDetails,
      allQueries: queries.menu,
      hcrExportProperties,
      tempUUIDsMap: queryTempuuidsMap.current,
      subDataSets,
      tableStyles
    });
    handleSaveHcr({
      selectedQueryId,
      type: saveType,
      location: arg.path,
      fileName: name,
      dispatch,
      saveHcrRequest,
      presentActiveTabUuid,
      flowchartInstance,
      dsPaneTypes,
      hcrDiagramNodesData,
      canvasProperties,
      groupCount,
      groupsOrder,
      previewFormData,
      hcrExportProperties,
      handleUpdateQueryuuids,
      subDataSets,
      tableStyles,
      tempUUIDsMap: queryTempuuidsMap.current,
    });
    dispatch(fileBrowserActions.setShowFileBrowser(false));
    dispatch(fileBrowserActions.setSearchResults(null));
    dispatch(hcrActions.setHcrFilebrowserFor(false));
  };

  function handleHcrEdit(record, isReportMode) {
    const dirArr = record.path.split("/")?.filter((ele) => ele !== record.name);
    const formData = { dir: dirArr.join("/"), file: record.name };
    if (saveDetailsisHcrLoaded) {
      dispatch(hcrActions.handleHcrLoaded(false));
    }
    if (isReportMode) {
      editHcrRequest(
        formData,
        (res) => {
          const dsPanes = res.state.dsPanes.map((pane) => {
            const reqPane = { ...pane };
            reqPane.menu = reqPane.menu.map((ele) => {
              const item = { ...ele };
              if (reqPane.key === "parameter") {
                item.executeQueryData = item.executeQueryData || {
                  data: [],
                  field: [],
                };
                item.parameterList = item.parameterList || [];
              }
              item.isSaved = true;
              item.isNameEditable = false;
              return item;
            });
            return reqPane;
          });
          let nodes = parseHCRNodesData(res.diagramData.nodes);
          let subDataSets = [], tableStyles = [];
          if (res.state.subDataSets) {
            subDataSets = res.state.subDataSets
          } else {
            subDataSets = getSubDataSetsFromReportState(nodes, dsPanes)
          }

          if (res.state.tableStyles) {
            tableStyles = res.state.tableStyles
          } else {
            const { alteredNodes, tableStyles: tStyles = [] } = getTableStylesFromReportState(nodes)
            tableStyles = tStyles;
            nodes = alteredNodes;
          }

          const stateArr = [
            { key: "dsPaneTypes", value: dsPanes },
            // {
            //   key: "hcrDiagramNodesData",
            //   value: [...res.diagramData.nodes],
            // },
            {
              key: "hcrDiagramNodesData",
              value: nodes,
            },
            {
              key: "canvasProperties",
              value: res.state.canvasProperties,
            },
            { key: "groupCount", value: res.state.groupCount || 0 },
            {
              key: "groupsOrder",
              value: res.state.groupsOrder || [],
            },
            { key: "title", value: res.file.name },
            {
              key: "selectedQueryId",
              value: res.state.selectedQueryId,
            },
            {
              key: "saveDetails",
              value: {
                location: res.file.dir,
                uuid: res.file.uuid,
                hcrName: res.file.name,
              },
            },
            {
              key: "selectedDS",
              value: { dataSourcePane: "Query", id: 1 },
            },
            { key: "uuid", value: res.file.uuid },
            { key: "hcrExportProperties", value: res.state.exportProperties },
            { key: "subDataSets", value: subDataSets },
            { key: "tableStyles", value: tableStyles },
          ];
          let filters = getHcrParameterFilters({
            parameters:
              dsPanes?.find((ele) => ele.dataSourcePane === hcrDSParameter) ||
              {},
            hcrDiagramNodesData,
          });
          if (Object.entries(props?.parameters || {}).length) {
            filters = handleUrlParamsFilters({
              urlParams: props.parameters,
              reqFilters: filters,
            });
          }
          filters = updateDateRangeFilterValues(filters);
          setAppliedFilters(filters);
          reportViewHcrGenerateReport({
            queries:
              dsPanes?.find((ele) => ele.dataSourcePane === hcrDSQuery) || {},
            selectedQueryId: res.state.selectedQueryId,
            flowchartInstance: {},
            // hcrDiagramNodesData: [...res.diagramData.nodes],
            hcrDiagramNodesData: nodes,
            Notify,
            dispatch,
            filters,
            canvasProperties: res.state.canvasProperties,
            setIsPreviewLoading: handlePreviewLoading,
            reportName: res.file.name,
            isPreviewing,
            saveDetails: {
              location: res.file.dir,
              uuid: res.file.uuid,
              hcrName: res.file.name,
            },
            openMode: true,
            hcrExportProperties: res.state.exportProperties,
            getApiInstance: getApiInstance,
            isStreamToggle,
            setPreviewError: handlePreviewError,
            onStreamSuccess,
            reportKey,
            subDataSets,
            tableStyles
          });
          dispatch(hcrActions.storeHcrState(stateArr));
        },
        (err) => {
          dispatch(hcrActions.handleHcrLoaded(true));
          handlePreviewUpdate({
            isPreviewLoading: false,
            previewError: getErrorMessage(err),
          });
        },
      );
    } else {
      const { panes } = hcrTabData;
      const emptyPane = handleEmptyPaneCheck(panes);
      if (emptyPane || panes.length < 4) {
        const newTabNum =
          ["1", "2", "3", "4"].find((num) => !tabNumbers.includes(num)) ||
          emptyPane.key;
        editHcrRequest(
          formData,
          (res) => {
            const dsPanes = res.state.dsPanes.map((pane) => {
              const reqPane = { ...pane };
              reqPane.menu = reqPane.menu.map((ele) => {
                const item = { ...ele };
                if (reqPane.key === "parameter") {
                  item.executeQueryData = item.executeQueryData || {
                    data: [],
                    field: [],
                  };
                  item.parameterList = item.parameterList || [];
                }
                item.isSaved = true;
                item.isNameEditable = false;
                return item;
              });
              return reqPane;
            });
            let nodes = parseHCRNodesData(res.diagramData.nodes);
            let subDataSets = [], tableStyles = [];
            if (res.state.subDataSets) {
              subDataSets = res.state.subDataSets
            } else {
              subDataSets = getSubDataSetsFromReportState(nodes, dsPanes)
            }

            if (res.state.tableStyles) {
              tableStyles = res.state.tableStyles
            } else {
              const { alteredNodes, tableStyles: tStyles = [] } = getTableStylesFromReportState(nodes)
              tableStyles = tStyles;
              nodes = alteredNodes;
            }


            const stateArr = [
              { key: "dsPaneTypes", value: dsPanes },
              {
                key: "hcrDiagramNodesData",
                value: nodes,
              },
              {
                key: "canvasProperties",
                value: res.state.canvasProperties,
              },
              {
                key: "groupCount",
                value: res.state.groupCount || 0,
              },
              {
                key: "groupsOrder",
                value: res.state.groupsOrder || [],
              },
              { key: "title", value: res.file.name },
              {
                key: "selectedQueryId",
                value: res.state.selectedQueryId,
              },
              {
                key: "saveDetails",
                value: {
                  location: res.file.dir,
                  uuid: res.file.uuid,
                  hcrName: res.file.name,
                },
              },
              {
                key: "selectedDS",
                value: { dataSourcePane: "Query", id: 1 },
              },
              {
                key: "sidebarPaneActiveKey",
                value: "",
              },
              { key: "uuid", value: res.file.uuid },
              { key: "hcrExportProperties", value: res.state.exportProperties },
              { key: "subDataSets", value: subDataSets },
              { key: "tableStyles", value: tableStyles },
            ];
            if (res.state.selectedQueryId) {
              const advancedComponents = nodes?.filter(({ category }) => ["advancedTable", "crosstab", "chart"].includes(category)) || [];
              if (advancedComponents.length) {
                const isMainQueryInAdvancedComponent = advancedComponents.some(({ selectedQueryID }) => selectedQueryID === res.state.selectedQueryId);
                if (isMainQueryInAdvancedComponent) {
                  notify(dispatch).warning({
                    message: "Main Report Dataset is currently used in one or more component dataset mappings. Please assign a separate dataset/subdataset for component-level data binding.",
                    type: "Front End",
                  });
                }
              }
            }
            dispatch(
              onAddingHcrTabData({
                pane: {
                  title: res.file.name,
                  key: newTabNum,
                  uuid: res.file.uuid,
                },
                activeKey: newTabNum,
                hcrEditedReportArr: stateArr,
                emptyPane,
              }),
            );
          },
          (err) => {
            dispatch(hcrActions.handleHcrLoaded(true));
          },
        );
      } else {
        notify(dispatch).warning({
          message: "tab list is reached to 4, close any tab to edit new report",
          type: "Front End",
        });
      }
    }
  }

  function handleUseImage(record) {
    const nodeId = lastSelectedNodeRef.current.id;
    const node = hcrDiagramNodesData.find((n) => n.id === nodeId);
    const tableNodes = hcrDiagramNodesData.filter((n) => n.category === "advancedTable");
    if (node) {
      dispatch(
        hcrActions.editNode({
          nodeId,
          nodeKey: "imagePath",
          nodeVal: record.path,
        }),
      );
      dispatch(
        hcrActions.editNode({
          nodeId,
          nodeKey: "imageResourceId",
          nodeVal: record.resourceId,
        }),
      );
    }
    if (!node && tableNodes?.length) {
      for (let tNode of tableNodes) {
        const { nodes = {}, id } = tNode || {}
        let childNodes = Object.values(nodes)?.flat() || [];
        let targetNode = childNodes.find((n) => n.id === nodeId);
        if (targetNode) {
          dispatch(hcrActions.hcrUpdateCanvasTabComponent({
            actionType: "updateImageNode",
            id,
            nodeId,
            properties: {
              imagePath: record.path,
              imageResourceId: record.resourceId
            }
          }))
          break;
        }
      }
    }
  }

  let fbProperties = {};
  if (hcrFBFor === "save") {
    fbProperties.isHideFilters = true;
    fbProperties.footerForm = {
      type: "Save",
      form: (
        <SaveItems
          formHeading="CannedReport file name"
          onFormSumbit={onFormSumbit}
          saveButtonText="Save"
          cancelButtonText="Cancel"
          inputValue={
            Object.keys(saveDetails || {}).length
              ? reportName
              : "CannedReport_1"
          }
          validateName={validateHcrName}
        />
      ),
    };
  } else if (hcrFBFor === "images") {
    fbProperties.onDoubleClick = handleUseImage;
    fbProperties.extensionOptions = ["image"];
    fbProperties.refreshWhenUpload = true;
    fbProperties.contextMenuOptions = {
      append: true,
      options: [
        {
          name: "Use This Image",
          key: "useThisImage",
          icon: <CustomIcon name={"HcrImage"} />,
          id: "useThisImage",
          merge: false,
          disabled: false,
          types: ["file"],
          extensions: ["image"],
          callback: handleUseImage,
        },
      ],
    };
  } else if (hcrFBFor === "edit") {
    fbProperties.onDoubleClick = handleHcrEdit;
    fbProperties.extensionOptions = ["hcr"];
    fbProperties.contextMenuOptions = {
      append: true,
      options: [
        {
          name: "Edit",
          key: "edit",
          id: "edt",
          merge: true,
          disabled: false,
          types: ["file"],
          extensions: ["hcr"],
          callback: handleHcrEdit,
        },
        {
          merge: true,
          id: "opn",
          types: ["file"],
          extensions: ["hcr"],
          hide: true,
        },
        {
          merge: true,
          id: "onw",
          types: ["file"],
          extensions: ["hcr"],
          hide: true,
        },
      ],
    };
  }

  const openFB = ({ fbFor = "edit", onEdit }) => {
    dispatch(hcrActions.setHcrFilebrowserFor(fbFor));
    dispatch(fileBrowserActions.setSearchResults(null));
    resetStream();
    dispatch(fileBrowserActions.setShowFileBrowser(true));
  };

  useEffect(() => {
    if (
      props.mode?.includes("open") &&
      props.file &&
      props.file?.name.includes("hcr")
    ) {
      handlePreviewUpdate({
        pageVal: 1,
        isStreamComplete: false,
        isAborted: false,
        previewError: null,
        isPreviewLoading: true,
      })
      hcrConfigurationsRequest(
        { contentId: "hcrConfigurations" },
        "content/static/getContents",
        (res) => {
          res.HCR = JSON.parse(res.HCR);
          dispatch(storeHCROldConfigurations(res));
        },
        (e) => {
          console.log(e);
        },
      );
      handleHcrEdit(props.file, props.mode);
    }
  }, [props.file]);

  useEffect(() => {
    getExportProperties(
      { contentId: "Static/hcrproperties" },
      (res) => {
        dispatch(setHcrExportProperties(res));
      },
      (e) => {
        console.log(e);
      },
    );
  }, []);

  useEffect(() => {
    if (!isEmpty(hcrExportPropertiesData) && !isHCRDefaultPropertiesAdded) {
      const defaultProperties = getDefaulPropertiesFromExportProperties(
        hcrExportPropertiesData,
      );
      if (defaultProperties?.length) {
        dispatch(hcrAddDefaultExportProperties(defaultProperties));
      }
    }
  }, [hcrExportPropertiesData, hcrTabData?.panes?.length]);

  useEffect(() => {
    if (
      reportName &&
      props.mode?.includes("open") &&
      props.file &&
      props.file?.name.includes("hcr")
    ) {
      if (saveDetails?.hcrName) {
        props.setFileInfo({ fileTitle: saveDetails.hcrName });
      }
    }
  }, [reportName]);

  useEffect(() => {
    if (
      reportName &&
      exportType &&
      props.mode?.includes("open") &&
      props.file &&
      props.file?.name.includes("hcr")
    ) {
      const taskbarItems = [
        {
          title: "Filters",
          icon: <FunnelPlotOutlined />,
          callback: () => {
            dispatch(hcrActions.updateHcrFiltersDrawerStatus());
          },
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
          menu: getMenuOptions("hcr", (format) => setReportExport(format)),
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
                handlePreviewUpdate({ isAborted: false })

                fetchPreviewDetails({
                  isCache: true,
                  openMode: true,
                });
              },
            },
            {
              title: "Current Report",
              key: "currentReport",
              icon: <ReloadOutlined />,
              callback: () => {
                handlePreviewUpdate({ isAborted: false })
                hcrConfigurationsRequest(
                  { contentId: "hcrConfigurations" },
                  "content/static/getContents",
                  (res) => {
                    res.HCR = JSON.parse(res.HCR);
                    dispatch(storeHCROldConfigurations(res));
                  },
                  (e) => {
                    console.log(e);
                  },
                );
                handleHcrEdit(props.file, props.mode);
              },
            },
          ],
        },
      ].filter(Boolean);
      props.renderTaskbar(taskbarItems);
    }
  }, [reportName, exportType, isStreamComplete, isAborted]);

  const { width, height } = getCanvasDimensions(canvasProperties);
  const showPreviewError = previewError || isAborted;
  const isAwaitingFirstStreamPage =
    isStreamToggle && !previewTag && !previewError && !isAborted;
  const showPreviewSkeleton = isPreviewLoading || isAwaitingFirstStreamPage;
  const divStyles = {
    width: `${width}px`,
    height: `${height}px`,
  }
  const previewAreaTag = showPreviewSkeleton ? (
    <div
      role="presentation"
      className="canvas-wrapper"
      style={divStyles}
    >
      <LoadingBar handleClick={handleAbort} />
      <CustomCannedSkeleton canvasHeight={height} />
    </div>
  ) : showPreviewError ? (
    <div
      role="presentation"
      className="canvas-wrapper no-data-wrapper"
      style={divStyles}
    >
      <NoDataTemplate
        message={{
          title: "",
          description: previewError,
        }}
        handleBack={handleBack}
      />
    </div>
  ) : (
    <div role="presentation">
      <PreviewArea
        previewTag={previewTag}
        isPreviewLoading={isPreviewLoading}
      />
    </div>
  );

  return (
    <>
      <HCRFiltersDrawer
        urlParameters={props.parameters}
        reportMode={props.mode}
        hcrFiltersDrawerStatus={hcrFiltersDrawerStatus}
        flowchartInstance={flowchartInstance}
        setIsPreviewLoading={handlePreviewLoading}
        setAppliedFilters={setAppliedFilters}
        setParametersReview={props?.setParametersReview}
        queryTempuuidsMap={queryTempuuidsMap}
        setPageVal={handleChangePageValue}
        streamEnabled={isStreamToggle}
        setPreviewError={handlePreviewError}
        onStreamSuccess={onStreamSuccess}
        resetStream={resetStream}
        reportKey={reportKey}
        subDataSets={subDataSets}
        tableStyles={tableStyles}
        resetQueryuuids={resetQueryuuids}
      />
      {["open"].includes(props.mode) ? (
        <div style={{ height: "100%" }}>
          <div style={{ height: "91%", overflowY: "auto" }}>
            {previewAreaTag}
          </div>
          {!showPreviewSkeleton && !previewError ? pagination(true) : null}
        </div>
      ) : (
        <>
          <HILayout
            header={
              <HINavbar taskbar={taskbar} hideToggleSidebar={isPreviewing}>
                <TutorialInfo elementKey="hcr-tabs">
                  <HITabs
                    type="editable-card"
                    add={add}
                    remove={remove}
                    tabData={hcrTabData}
                    setTabData={handleTabActiveKey}
                    isHcr={true}
                  />
                </TutorialInfo>
              </HINavbar>
            }
            content={
              <div
                className="canned-report-content"
                data-testid="canned-report-content-container"
              >
                {isPreviewing ? (
                  previewAreaTag
                ) : (
                  <CannedReports
                    flowchartInstance={flowchartInstance}
                    nodesPositions={nodesPositions}
                    queryTempuuidsMap={queryTempuuidsMap}
                    openFB={openFB}
                    tabNum={hcrTabData.activeKey}
                    lastSelectedNodeRef={lastSelectedNodeRef}
                    getApiInstance={getApiInstance}
                    handleAbort={handleAbort}
                    resetQueryuuids={resetQueryuuids}
                  />
                )}
              </div>
            }
            defaultSidebar={false}
          />
          {!isGlobalFbEnabled &&
            showFileBrowser &&
            Object.keys(fbProperties).length > 0 && (
              <HIFileBrowser {...fbProperties} />
            )}
          {shareRef?.current && (
            <ShareFinalModal
              shareOptions={{
                type: "file",
                dir: saveDetails?.location,
                file: saveDetails?.uuid,
              }}
            />
          )}
        </>
      )}
    </>
  );
};
export { CannedReportsPage };

