import { Tooltip, Modal, Typography } from "antd";
import { TableOutlined } from "@ant-design/icons";
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
import TutorialInfo from "../../common/hi-tutorial";
import HiMetadataArea from "../../hi-sidebar/hr-hreportSidebar/hi-metadata-area";
import agentRequests from "../../../base/requests/agent.requests";
import "../../hi-instant-bi/components/info-container/info-container.scss";
import "./agent-sidebar.scss";

const { Text } = Typography;

export function AgentMetadataShelf({ urlObj = {}, className = "", ...props }) {
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
  const [pendingEditRecord, setPendingEditRecord] = useState(null);
  const [metadataLoading, setMetadataLoading] = useState(false);
  const skipMetadataFetch = useRef(false);
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
  const checkIsMetadataPresent = () => Boolean(path && fileName);

  const applyMetadataSelection = (record) => {
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
    document.title = "HI: Semantic Model";
  };

  const changeMetadata = (record) => {
    if (checkIsMetadataPresent()) {
      setMetadataToBeChanged(record);
      setMetadataChangeModalVisible(true);
    } else {
      applyMetadataSelection(record);
    }
  };

  const applyEditSelection = (record) => {
    dispatch(
      appActions.setEditModeInfo({
        dir: record.path,
        file: record.name,
        extension: record.extension,
        title: record.title,
      }),
    );
  };

  const handleMetadataModalOkClick = () => {
    if (pendingEditRecord) {
      applyEditSelection(pendingEditRecord);
      setPendingEditRecord(null);
    } else if (metadataToBeChanged) {
      applyMetadataSelection(metadataToBeChanged);
      setMetadataToBeChanged(null);
    }
    setMetadataChangeModalVisible(false);
  };

  const onFbDoubleClick = (record) => {
    if (props.filebrowserFor === "edit") {
      if (checkIsMetadataPresent()) {
        setPendingEditRecord(record);
        setMetadataChangeModalVisible(true);
      } else {
        applyEditSelection(record);
      }
      return;
    }
    changeMetadata(record);
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

  let fbProperties = {
    extensionOptions: ["metadata"],
    contextMenuOptions: {
      append: true,
      options: [
        {
          icon: <TableOutlined/>,
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
  if (Object.keys(props.fbProperties || {}).length) {
    fbProperties = { ...props.fbProperties };
  }

  const openFileBrowser = () => {
    dispatch(setAgentMode("edit"));
    openComponentFileBrowser();
    props.setFilebrowserFor("edit");
  };

  const onConnectToMetadata = () => {
    props.setFilebrowserFor("");
    openComponentFileBrowser();
  };

  return (
    <div
      id="agent-metadata-shelf"
      className={`agent-metadata-shelf ${className}`.trim()}
    >
      <div className="agent-sidebar-shell">
        <TutorialInfo elementKey="hi-agent-metadata-shelf">
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
        </TutorialInfo>

        <div className="agent-sidebar-content">
          <HiMetadataArea
            metadata={metadataTablesData}
            size={{ height: props.metadataGridHeight || "100%" }}
            openFileBrowser={openFileBrowser}
            module="agent"
            hideSideBar={false}
            onConnectToMetadata={onConnectToMetadata}
            metadataLoading={metadataLoading}
            abortFetchData={abortMetadataFetch}
            parentContainerId="agent-metadata-shelf"
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
        title={`Open another ${pendingEditRecord ? "model" : "metadata"} file?`}        open={metadataChangeModalVisible}
        onOk={handleMetadataModalOkClick}
        onCancel={() => {
          setMetadataToBeChanged(null);
          setPendingEditRecord(null);
          setMetadataChangeModalVisible(false);
        }}
      >
        <Text>
          {`Are you sure you want to open another ${pendingEditRecord ? "model" : "metadata"} file? All your changes will be lost.`}
        </Text>
      </Modal>
    </div>
  );
}

export default AgentMetadataShelf;
