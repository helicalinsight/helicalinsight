import { Layout, Tooltip, Modal, Typography } from "antd";
import React, { useEffect, useRef, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { appActions, fileBrowserActions } from "../../../redux/actions";
import { HIFileBrowser } from "../../hi-fileBrowser/hi-fileBrowser";
import {
  agentFileDataAfterSave,
  saveAgentMetadataFileDetails,
  setAgentMode,
} from "../../../redux/actions/agent.actions";
import { CustomIcon } from "../../common/custom-icons/CustomIcon";
import HiMetadataArea from "../../hi-sidebar/hr-hreportSidebar/hi-metadata-area";
import agentRequests from "../../../base/requests/agent.requests";
import "../../../components/hi-instant-bi/components/info-container/info-container.scss";
import "./agent-sidebar.scss";

const { Sider } = Layout;
const { Text } = Typography;

export function AgentSidebar({ urlObj = {}, ...props }) {
  const { toggleSidebar } = useSelector((state) => state.app);
  const agentState = useSelector((store) => store.agent);
  const { metadataTablesData, metadataDetails } = agentState;
  const { fileName, path } = metadataDetails;
  const dispatch = useDispatch();
  const { getAgentMetadataTablesData } = agentRequests(dispatch);
  const isGlobalFbEnabled = useSelector(
    (state) => state.fileBrowser.globalFbEnabled,
  );
  const showFileBrowser = useSelector(
    (state) => state.fileBrowser.showFileBrowser,
  );
  const [metadataChangeModalVisible, setMetadataChangeModalVisible] =
    useState(false);
  const [metadataToBeChanged, setMetadataToBeChanged] = useState(null);
  const [modalSource, setModalSource] = useState(null);
  const [metadataLoading, setMetadataLoading] = useState(false);
  const skipMetadataFetch = useRef(false);
  const skipMetadataChangeConfirm = useRef(false);
  const metadataApiRef = useRef(null);
  const metadataAbortedRef = useRef(false);

  const abortMetadataFetch = () => {
    metadataAbortedRef.current = true;
    metadataApiRef.current?.abort({
      setLoading: (loading) => {
        if (!loading) {
          setMetadataLoading(false);
        }
      },
    });
    setMetadataLoading(false);
    metadataApiRef.current = null;
  };
  const openComponentFileBrowser = () => {
    dispatch(fileBrowserActions.setGlobalFbVisibility(false));
    dispatch(fileBrowserActions.setShowFileBrowser(true));
  };

  const applyMetadataSelection = (record) => {
    skipMetadataChangeConfirm.current = false;
    const fileDtls = record.path.split("/");
    const selectedFileName = fileDtls.pop();
    const selectedPath = fileDtls.join("/");
    if (props.onResetAgentEditor) {
      props.onResetAgentEditor();
    }
    dispatch(setAgentMode("create"));
    dispatch(agentFileDataAfterSave({}));
    dispatch(
      saveAgentMetadataFileDetails({
        path: selectedPath,
        fileName: selectedFileName,
      }),
    );
    dispatch(fileBrowserActions.setShowFileBrowser(false));
    document.title = "HI: Agent";
  };

  const onFbDoubleClick = (record) => {
    if (props.filebrowserFor === "edit") {
      dispatch(
        appActions.setEditModeInfo({
          dir: record.path,
          file: record.name,
          extension: record.extension,
          title: record.title,
        }),
      );
      return;
    }
    promptMetadataChange(record, "doubleClick");
  };

  useEffect(() => {
    if (!path || !fileName) {
      return;
    }
    if (skipMetadataFetch.current) {
      skipMetadataFetch.current = false;
      return;
    }
    if (metadataApiRef.current?.abort) {
      metadataAbortedRef.current = true;
      metadataApiRef.current.abort({
        setLoading: () => setMetadataLoading(false),
      });
    }
    metadataAbortedRef.current = false;
    setMetadataLoading(true);
    const metadataInstance = getAgentMetadataTablesData({
      path,
      fileName,
      callback: () => {
        if (metadataAbortedRef.current) {
          return;
        }
        setMetadataLoading(false);
        metadataApiRef.current = null;
      },
      errback: () => {
        if (metadataAbortedRef.current) {
          metadataAbortedRef.current = false;
          return;
        }
        setMetadataLoading(false);
        metadataApiRef.current = null;
      },
    });
    metadataApiRef.current = metadataInstance;
    return () => {
      metadataAbortedRef.current = true;
    };
  }, [path, fileName]);

  useEffect(() => {
    if (!showFileBrowser) {
      skipMetadataChangeConfirm.current = false;
    }
  }, [showFileBrowser]);

  useEffect(() => {
    if (Object.keys(urlObj).length && urlObj.dir && urlObj.file) {
      const fileArr = urlObj.file.split(".");
      const extension = fileArr[fileArr.length - 1];
      if (extension === "metadata") {
        dispatch(
          saveAgentMetadataFileDetails({
            path: urlObj.dir,
            fileName: urlObj.file,
          }),
        );
      }
    }
  }, [urlObj]);

  const checkIsMetadataPresent = () => Boolean(path && fileName);

  const promptMetadataChange = (record, source) => {
    if (skipMetadataChangeConfirm.current) {
      applyMetadataSelection(record);
      return;
    }
    if (checkIsMetadataPresent()) {
      setMetadataChangeModalVisible(true);
      setMetadataToBeChanged(record);
      setModalSource(source);
    } else {
      applyMetadataSelection(record);
    }
  };

  const handleMetadataModalOkClick = () => {
    if (modalSource === "openFileBrowser") {
      skipMetadataChangeConfirm.current = true;
      dispatch(setAgentMode("edit"));
      openComponentFileBrowser();
      props.setFilebrowserFor("edit");
    } else if (modalSource === "connectToMetadata") {
      skipMetadataChangeConfirm.current = true;
      props.setFilebrowserFor("");
      openComponentFileBrowser();
    } else if (metadataToBeChanged) {
      applyMetadataSelection(metadataToBeChanged);
    }
    setMetadataToBeChanged(null);
    setModalSource(null);
    setMetadataChangeModalVisible(false);
  };

  const changeMetadata = (record) => {
    promptMetadataChange(record, "callback");
  };

  let fbProperties = {
    extensionOptions: ["metadata"],
    contextMenuOptions: {
      append: true,
      options: [
        {
          icon: <CustomIcon name="Cube" />,
          id: "use",
          name: "Use This Metadata",
          types: ["file"],
          extensions: ["metadata"],
          disabled: false,
          callback: (record) => {
            changeMetadata(record);
          },
        },
      ],
    },
    onDoubleClick: { onFbDoubleClick },
  };
  if (Object.keys(props.fbProperties).length) {
    fbProperties = { ...props.fbProperties };
  }
  const openFileBrowser = () => {
    if (checkIsMetadataPresent()) {
      setMetadataChangeModalVisible(true);
      setMetadataToBeChanged(null);
      setModalSource("openFileBrowser");
    } else {
      dispatch(setAgentMode("edit"));
      openComponentFileBrowser();
      props.setFilebrowserFor("edit");
    }
  };

  const onConnectToMetadata = () => {
    if (checkIsMetadataPresent()) {
      setMetadataChangeModalVisible(true);
      setMetadataToBeChanged(null);
      setModalSource("connectToMetadata");
    } else {
      props.setFilebrowserFor("");
      openComponentFileBrowser();
    }
  };
  return (
    <Sider
      collapsed={toggleSidebar}
      collapsedWidth={0}
      width={"100%"}
      className={`hi-cube-sider ${props.customClass || ""}`}
    >
      <div className="agent-sidebar-shell">
        <div className="hi-instant-bi-info-container">
          <div type="secondary" style={{ whiteSpace: "nowrap", marginRight: 4 }}>
            Metadata:{" "}
          </div>
          <Tooltip
            title={`${metadataDetails?.path || ""}/${
              metadataDetails?.fileName || ""
            }`}
          >
            <div
              strong
              style={{
                overflow: "hidden",
                textOverflow: "ellipsis",
                whiteSpace: "nowrap",
                flex: 1,
                minWidth: 0,
              }}
            >
              {agentState.metadataTablesData.metadataName}
            </div>
          </Tooltip>
        </div>

        <div className="agent-sidebar-content">
          <HiMetadataArea
            metadata={metadataTablesData}
            size={{ height: "100%" }}
            openFileBrowser={openFileBrowser}
            module="agent"
            hideSideBar={false}
            onConnectToMetadata={onConnectToMetadata}
            metadataLoading={metadataLoading}
            abortFetchData={abortMetadataFetch}
          />
        </div>
      </div>
      {!isGlobalFbEnabled && showFileBrowser && (
        <HIFileBrowser
          {...fbProperties}
          onDoubleClick={onFbDoubleClick}
          showEditOnTop={props.filebrowserFor === "edit"}
        />
      )}
      <Modal
        title={"Open another metadata file?"}
        open={metadataChangeModalVisible}
        onOk={handleMetadataModalOkClick}
        onCancel={() => {
          skipMetadataChangeConfirm.current = false;
          setMetadataToBeChanged(null);
          setModalSource(null);
          setMetadataChangeModalVisible(false);
        }}
      >
        <Text>
          Are you sure you want to open another file? All your changes will be
          lost.
        </Text>
      </Modal>
    </Sider>
  );
}
