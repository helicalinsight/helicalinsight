import { Skeleton } from "antd";
import { isEqual } from "lodash-es";
import React, { useEffect, useRef, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { v4 as uuidv4 } from "uuid";
import { HIFileBrowser, HIInstantBIModule, HINavbar, HITabs } from "../components";
import { ShareFinalModal } from "../components/hi-fileBrowser/components";
import SaveItems from "../components/hi-fileBrowser/SaveItems";
import getInstantBIOpenTaskbarItems from "../components/hi-instant-bi/instant-bi-open-taskbar-items";
import getInstantBITaskBarItems from "../components/hi-instant-bi/instant-bi-taskbar-items";
import { CustomIcon } from "../components/common/custom-icons/CustomIcon";
import {
  fetchInstantBIReportAPI,
  connectAgentToReport,
  saveInstantBIReportAPI,
} from "../components/hi-instant-bi/utils/instant-bi-requests";
import notify from "../components/hi-notifications/notify";
import HILayout from "../layouts/hi-layout";
import { appActions, fileBrowserActions } from "../redux/actions";
import { AGENT_INTERACT_ACTION } from "../components/hi-fileBrowser/constants";
import { addNewIBReport, changeIBReport, loadInitialIBReport, removeIBReport, updateInstantBIMode } from "../redux/actions/instant-bi.actions";
import { routesUrl } from "../app/constants";


let env = process.env.NODE_ENV;
const createReportId = () => {
  if (env === "test") {
    return "test_123";
  } else {
    return uuidv4();
  }
};

const HIInstantBI = (props) => {
  let { mode, file, urlObj = {} } = props;
  let [reportId, setReportId] = useState("");
  const {
    activeReportId = '',
    reports = [],
    layout: { previewShelf, metadataShelf, chatShelf },
    isSaving,
    loading,
    searchValue
  } = useSelector((state) => state.instantBI)
  const activeReport = reports?.find((report) => report.id === reportId) || {};

  const editModeInfo = useSelector((state) => state.app.editModeInfo);

  const shareRef = useRef(null);
  let isEditMode = editModeInfo;
  let isOpenMode = mode === "open";
  let isCreateMode = !mode;
  const isDashboardMode = urlObj?.mode === "dashboard";
  const fileInfoRef = useRef(null);

  const { reportInfo = {}, metadata = {} } = activeReport;
  const { reportName = '' } = reportInfo || {}
  let isMetadataPresent = metadata && Object.keys(metadata).length > 0 && !metadata.loading;

  const dispatch = useDispatch();
  const [fileBrowserOptions, setFileBrowserOptions] = useState({});
  const isShareModalVisible = useSelector(
    (state) => state.fileBrowser.isShareModalVisible
  );
  const urlObjRef = useRef({});
  const addTabRef = useRef(null);
  const Notify = notify(dispatch);

  let reportsList = reports?.map((report) => {
    return { key: report?.id, title: report?.reportInfo?.reportName };
  });

  useEffect(() => {
    if (!isShareModalVisible) {
      shareRef.current = false;
    }
  }, [isShareModalVisible]);

  useEffect(() => {
    if (isCreateMode) {
      dispatch(
        updateInstantBIMode({
          mode: "create",
        })
      );
      let reportId = createReportId();
      setReportId(reportId);
      dispatch(loadInitialIBReport({ reportId }));
    }

    return () => {
      dispatch(removeIBReport({ id: reportId }))
      dispatch(fileBrowserActions.setShowFileBrowser(false));
    };
  }, []);

  useEffect(() => {
    if (isEditMode && editModeInfo?.extension === "instant") {
      let reportId = createReportId();
      setReportId(reportId);
      fetchInstantBIReportAPI({
        dispatch,
        // file: { mode: "", parameters: {} },
        file: {
          path: editModeInfo?.dir,
          name: editModeInfo?.file,
        },
        mode: "edit",
        // setFileInfo: props.setFileInfo,
        reportId
      });
      dispatch(appActions.setEditModeInfo(null));
    }
  }, [editModeInfo]);

  const isInstantOpenFile =
    isOpenMode && props.file?.name?.includes?.("instant");

  useEffect(() => {
    if (!isInstantOpenFile || !reportName || typeof props.renderTaskbar !== "function") return;
    props.setFileInfo?.({ fileTitle: reportName });
    props.renderTaskbar(
      getInstantBIOpenTaskbarItems({
        dispatch,
        file,
        reportId,
        setFileInfo: props.setFileInfo,
      })
    );
  }, [reportName, isInstantOpenFile, file, reportId]);

  useEffect(() => {
    if (!isEqual(file, fileInfoRef.current) && isOpenMode) {
      const newReportId = createReportId();
      setReportId(newReportId);
      fetchInstantBIReportAPI({
        dispatch,
        // file: { mode: "", parameters: {} },
        file: { path: file?.path, name: file?.name },
        mode: "open",
        setFileInfo: props.setFileInfo,
        reportId: newReportId,
      });
      fileInfoRef.current = file;
    }
  });

  useEffect(() => {
    window.addEventListener("beforeunload", handleUnload);
    return () => {
      window.removeEventListener("beforeunload", handleUnload);
    };
  }, []);

  useEffect(() => {
    if (
      activeReportId &&
      (["create", "edit"].includes(props.mode) || !props.mode)
    ) {
      setReportId(activeReportId);
    }
  }, [activeReportId]);

  useEffect(() => {
    if (!isEqual(urlObj, urlObjRef.current) && Object.keys(urlObj).length) {
      urlObjRef.current = urlObj;
      const fileArray = urlObj?.file.split(".");
      const extension = fileArray[fileArray.length - 1];
      if (extension === "metadata") {
        storeMetadata({
          title: urlObj?.file.split(".")[0],
          path: urlObj?.dir + "/" + urlObj?.file,
        });
      } else if (extension === "instant") {
        switch (urlObj?.mode) {
          case "open":
            fetchInstantBIReportAPI({
              dispatch,
              // file: { mode: "", parameters: {} },
              file: {
                path: urlObj?.dir + "/" + urlObj?.file,
                name: urlObj?.file,
              },
              mode: "open",
              // setFileInfo: props.setFileInfo,
            });

            break;
          case "dashboard":
            fetchInstantBIReportAPI({
              dispatch,
              // file: { mode: "", parameters: {} },
              file: {
                path: urlObj?.dir + "/" + urlObj?.file,
                name: urlObj?.file,
              },
              mode: "open",
              // setFileInfo: props.setFileInfo,
            });
            break;

          default:
            fetchInstantBIReportAPI({
              dispatch,
              // file: { mode: "", parameters: {} },
              file: {
                path: urlObj?.dir + "/" + urlObj?.file,
                name: urlObj?.file,
              },
              mode: "edit",
              // setFileInfo: props.setFileInfo,
            });
            break;
        }
      }
    }
  });

  // useEffect(() => {
  //   let reportId = createReportId();
  //   setReportId(reportId);
  //   if (!reports.length) {
  //     dispatch(loadInitialIBReport({ reportId }));
  //   }
  //   return () => {
  //     dispatch(removeIBReport({ id: reportId }))
  //   }
  // }, []);

  useEffect(() => {
    if (reportName && !["open"].includes(props.mode)) {
      document.title = `${reportName} | HI:Instant`;
    }
  }, [reportName]);

  const handleUnload = (e) => {
    const message = "o/";
    (e || window.event).returnValue = message; //Gecko + IE
    return message;
  };

  const storeMetadata = (rec) => {
    connectAgentToReport({
      path: rec.path,
      title: rec.title,
      reportId: activeReportId,
      dispatch,
    });
  };

  useEffect(() => {
    if (
      isEditMode &&
      editModeInfo?.extension === "model" &&
      editModeInfo?.action === AGENT_INTERACT_ACTION
    ) {
      dispatch((thunkDispatch, getState) => {
        const { activeReportId: reportId, reports } = getState().instantBI;
        if (!reportId || !reports.some((report) => report.id === reportId)) {
          return;
        }

        connectAgentToReport({
          path: editModeInfo.dir,
          title: editModeInfo.title,
          reportId,
          dispatch: thunkDispatch,
        });
        thunkDispatch(appActions.setEditModeInfo(null));
      });
    }
  }, [editModeInfo, activeReportId, dispatch]);

  const handleShare = () => {
    if (!reportInfo?.location || !reportInfo?.uuid) {
      Notify.warning({ message: "Please save the report!" });
      return;
    }
    shareRef.current = true;
    dispatch(fileBrowserActions.setShareModalVisibility());
  };

  const openFileBrowser = () => {
    setFileBrowserOptions({
      onDoubleClick: storeMetadata,
      contextMenuOptions: {
        append: true,
        options: [
          { id: "opn", merge: true, hide: true },
          { id: "onw", merge: true, hide: true },
          {
            name: "Use This Semantic Model",
            types: ["file"],
            icon: <CustomIcon name="Cube" />,
            extensions: ["model"],
            callback: storeMetadata,
            id: "use",
          },
        ],
      },
      extensionOptions: ["model"],
    });
    dispatch(fileBrowserActions.setShowFileBrowser(true));
  };
  const openInstantFileBrowser = () => {
    setFileBrowserOptions({
      extensionOptions: ["instant"],
      showEditOnTop: true,
      onDoubleClick: (record) => {
        dispatch(fileBrowserActions.setShowFileBrowser(false));
        dispatch(
          appActions.setEditModeInfo({
            dir: record.path,
            file: record.name,
            extension: record.extension,
          })
        );
        dispatch(appActions.updateRoute(routesUrl.instantBIUrl));
      },
      contextMenuOptions: {
        append: true,
        options: [
          { id: "opn", merge: true, hide: true },
          { id: "onw", merge: true, hide: true },
        ],
      },
    });
    dispatch(fileBrowserActions.setShowFileBrowser(true));
  };

  const onSaveAsFormSubmit = (dir, reportName) => {
    saveInstantBIReportAPI({
      activeReport,
      dispatch,
      saveFileInfo: { location: dir.path, reportName },
      searchValue,
    });
  };

  const onSave = () => {
    if (reportInfo?.location && reportInfo?.uuid) {
      saveInstantBIReportAPI({
        activeReport,
        dispatch,
        saveFileInfo: {
          location: reportInfo.location,
          reportName: reportInfo.reportName,
          uuid: reportInfo.uuid,
        },
        searchValue,
      });
    } else {
      setFileBrowserOptions({
        footerForm: {
          type: "Save",
          form: (
            <SaveItems
              formHeading="Report file name"
              onFormSumbit={onSaveAsFormSubmit}
              saveButtonText="Save"
              cancelButtonText="Cancel"
              inputValue={"Instant_1"}
            />
          ),
        },
      });
      dispatch(fileBrowserActions.setShowFileBrowser(true));
    }
  };

  const onSaveAs = () => {
    const reportName = (reportInfo?.uuid && reportInfo?.location) ? reportInfo.reportName : "Instant_1";
    setFileBrowserOptions({
      footerForm: {
        type: "Save",
        form: (
          <SaveItems
            formHeading="Report file name"
            onFormSumbit={onSaveAsFormSubmit}
            saveButtonText="Save"
            cancelButtonText="Cancel"
            inputValue={reportName}
          />
        ),
      },
    });
    dispatch(fileBrowserActions.setShowFileBrowser(true));
  };

  const addReport = () => {
    dispatch(addNewIBReport())
  };

  const deleteReport = (id) => {
    dispatch(removeIBReport({ id }))
  }

  const changeTab = ({ activeKey }) => {
    dispatch(changeIBReport({ id: activeKey }));
  };

  const propsList = {
    reportId,
    activeReport,
    reports,
    isMetadataPresent,
    mode,
    connectMetadata: openFileBrowser
  }

  if (!reportId) return null;

  const instantBIModule = (
    <div className={isOpenMode ? "hi-instant-bi-open-wrapper" : "height100percent"}>
      <Skeleton loading={loading}>
        <HIInstantBIModule {...propsList} />
        {<HIFileBrowser {...fileBrowserOptions} />}
        {shareRef.current && (
          <ShareFinalModal
            shareOptions={{
              type: "file",
              dir: reportInfo?.location,
              file: reportInfo?.uuid,
            }}
          />
        )}
      </Skeleton>
    </div>
  );

  const navbar = (
    <HINavbar
      taskbar={getInstantBITaskBarItems({
        dispatch,
        onSave,
        onSaveAs,
        handleShare,
        isSaving,
        previewShelf,
        metadataShelf,
        chatShelf,
        openFileBrowser,
        openInstantFileBrowser,
        isMetadataPresent
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

  let content = <HILayout header={navbar} content={instantBIModule} />;

  if (isOpenMode || isDashboardMode) {
    content = instantBIModule;
  }

  return content;
};
export { HIInstantBI };

