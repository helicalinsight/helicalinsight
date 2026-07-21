import { HINavbar } from "../components";
import SemanticMetadataEditor from "../components/hi-agent/components/semantic-metadata-editor";
import HILayout from "../layouts/hi-layout";
import AgentLayout from "../layouts/agent-layout";
import {
  SaveOutlined,
  SaveFilled,
  SettingOutlined,
  LayoutOutlined,
  CheckOutlined,
} from "@ant-design/icons";
import {
  agentSaveAPI,
  agentEditServiceAPI,
} from "../components/hi-instant-bi/utils/instant-bi-requests";
import notify from "../components/hi-notifications/notify";
import { appActions, fileBrowserActions } from "../redux/actions";
import { AGENT_INTERACT_ACTION } from "../components/hi-fileBrowser/constants";
import {
  agentFileDataAfterSave,
  agentLocalResetter,
  saveAgentMetadataFileDetails,
  setAgentMode,
} from "../redux/actions/agent.actions";
import agentRequests from "../base/requests/agent.requests";
import {
  getAgentStateForSave,
  validateAgentSaveInput,
} from "../components/hi-agent/utils/agent-helperMethods";
import { stripInternalTableRefsFromAgentState } from "../components/hi-agent/utils/agent-cube-bridge";
import SaveItems from "../components/hi-fileBrowser/SaveItems";
import React, { useEffect, useState, useRef, useCallback } from "react";
import { useDispatch, useSelector } from "react-redux";
import Watermark from "../components/hi-reports/hi-viz-area/watermark/watermark";
import { isOpenSource } from "../utils/utilities";

const AGENT_FILE_EXTENSION = ".model";
const DEFAULT_AGENT_NAME = "Model_1";

const toAgentFileUuid = (fileName = "") => {
  const trimmed = String(fileName).trim();
  if (!trimmed) return "";
  return trimmed.toLowerCase().endsWith(AGENT_FILE_EXTENSION)
    ? trimmed
    : `${trimmed}${AGENT_FILE_EXTENSION}`;
};

export function HIAGENT({ urlObj = {} }) {
  const agentState = useSelector((store) => store.agent);
  const { agentMode, metadataDetails, agentDataAfterSave } = agentState;
  const editModeInfo = useSelector((store) => store.app.editModeInfo);
  const metaInfo = useSelector(
    (store) => store.app.applicationSettingsData?.meta || {},
  );
  const openSource = isOpenSource(metaInfo);
  const dispatch = useDispatch();
  const [filebrowserFor, setFilebrowserFor] = useState("");
  const [agentData, setAgentData] = useState(null);
  const [editorContent, setEditorContent] = useState("");
  const [isJsonValid, setIsJsonValid] = useState(true);
  const [jsonError, setJsonError] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const [saveDetails, setSaveDetails] = useState({});
  const [doubleClickedFile, setDoubleClickedFile] = useState(null);
  const [agentName, setAgentName] = useState(DEFAULT_AGENT_NAME);
  const [isRawJsonView, setIsRawJsonView] = useState(false);
  const [isTableModeNormal, setIsTableModeNormal] = useState(true);
  const [shelfLayout, setShelfLayout] = useState({
    metadataShelf: true,
    fieldsShelf: true,
    toolsShelf: true,
  });
  const toggleShelf = (pane) =>
    setShelfLayout((prev) => ({ ...prev, [pane]: !prev[pane] }));

  const apiRef = useRef(null);
  const abortedRef = useRef(false);
  const semanticEditorRef = useRef(null);
  const { getSemanticTypes } = agentRequests(dispatch);
  const { dir: urlDir, file: urlFile } = urlObj;
  const clearEditorLoading = () => {
    setIsLoading(false);
    setIsSaving(false);
  };
  const handleAbort = () => {
    abortedRef.current = true;
    if (apiRef?.current?.abort) {
      apiRef.current.abort({
        setLoading: (loadingState) => {
          if (!loadingState) {
            clearEditorLoading();
          }
        },
      });
    } else {
      clearEditorLoading();
    }
    apiRef.current = null;
  };
  useEffect(() => {
    return () => {
      abortedRef.current = true;
      apiRef.current = null;
      dispatch(agentLocalResetter());
    };
  }, []);
  useEffect(() => {
    getSemanticTypes();
  }, []);
  useEffect(() => {
    if (editorContent && editorContent.trim() !== "") {
      try {
        JSON.parse(editorContent);
        setIsJsonValid(true);
        setJsonError(null);
      } catch (e) {
        setIsJsonValid(false);
        const errorMessage = e.message;
        const match = errorMessage.match(/at position (\d+)/);

        if (match && match[1]) {
          const position = parseInt(match[1]);
          const lines = editorContent.substring(0, position).split("\n");
          const lineNumber = lines.length;
          const columnNumber = lines[lines.length - 1].length;

          setJsonError({
            message: errorMessage,
            lineNumber,
            columnNumber,
            formattedMessage: `JSON Error at line ${lineNumber}, column ${columnNumber}: ${errorMessage}`,
          });
        } else {
          setJsonError({
            message: errorMessage,
            lineNumber: null,
            columnNumber: null,
            formattedMessage: `JSON Error: ${errorMessage}`,
          });
        }
      }
    } else {
      setIsJsonValid(true);
      setJsonError(null);
    }
  }, [editorContent]);

  let fbProperties = {};
  const shareRef = React.useRef(null);
  const recordPathFromDirFile = (dir, file) => (dir ? `${dir}/${file}` : file);
  const handleAgentEdit = ({ dir, file, title }) => {
    if (apiRef.current?.abort) {
      apiRef.current.abort();
    }
    abortedRef.current = false;
    setFilebrowserFor("edit");
    dispatch(setAgentMode("edit"));
    const Notify = notify(dispatch);
    const editInstance = agentEditServiceAPI({
      dir,
      file,
      dispatch,
      successCB: (response) => {
        if (abortedRef.current) {
          abortedRef.current = false;
          return;
        }
        apiRef.current = null;
        Notify.success({
          type: "Frontend",
          message: "Semantic model loaded successfully",
        });
        if (response?.data?.state) {
          setAgentData(response.data.state);
        }
        const responseAgentName = response?.data?.agentName || response?.data?.modelName;
        if (responseAgentName) {
          setAgentName(responseAgentName);
        }
        if (response?.data?.metadata) {
          const { location, metadataFileName } = response.data.metadata;
          dispatch(
            saveAgentMetadataFileDetails({
              path: location,
              fileName: metadataFileName,
            }),
          );
        }
        setDoubleClickedFile({ path: dir, fileName: file, title });
        dispatch(agentFileDataAfterSave({ dir, file }));
        document.title = `${title || file.split(".")[0]} | HI: Semantic Model`;
      },
      errorCB: () => {
        if (abortedRef.current) {
          abortedRef.current = false;
          return;
        }
        apiRef.current = null;
      },
    });
    apiRef.current = editInstance;
  };

  useEffect(() => {
    if (
      editModeInfo &&
      editModeInfo.extension === "model" &&
      editModeInfo.action !== AGENT_INTERACT_ACTION
    ) {
      const fileDtls = editModeInfo.dir.split("/");
      const file = fileDtls.pop();
      const dir = fileDtls.join("/");
      dir && file && handleAgentEdit({ dir, file, title: editModeInfo.title });
      dispatch(appActions.setEditModeInfo(null));
    }
  }, [editModeInfo]);

  useEffect(() => {
    if (Object.keys(urlObj).length && urlDir && urlFile) {
      const extension = urlFile.split(".").pop();
      if (extension === "model") {
        handleAgentEdit({ dir: urlDir, file: urlFile });
      }
    }
  }, [urlObj]);

  const getSaveContent = () => {
    if (isRawJsonView) {
      const { agentState } = semanticEditorRef.current?.getSaveState?.() || {};
      return stripInternalTableRefsFromAgentState(agentState);
    }
    const { cubeFieldsData, agentState } =
      semanticEditorRef.current?.getSaveState?.() || {};
    return stripInternalTableRefsFromAgentState(
      getAgentStateForSave({
        cubeFieldsData,
        existingAgentData: agentState,
      }),
    );
  };
  const onFormSumbit = (onSaveData, name, uuid) => {
    const dir = metadataDetails?.path;
    const file = metadataDetails?.fileName;
    const Notify = notify(dispatch);
    const finalName = name || agentName || DEFAULT_AGENT_NAME;
    let content;

    try {
      content = getSaveContent();
    } catch (e) {
      Notify.error({
        type: "Frontend",
        message: `Invalid JSON: ${e.message}`,
      });
      return;
    }
    setAgentName(finalName);
    abortedRef.current = false;
    setIsSaving(true);
    const apiParams = {
      dir,
      file,
      agentDir: onSaveData.path,
      modelName: finalName,
      content,
      dispatch,
      successCB: (response) => {
        if (abortedRef.current) {
          abortedRef.current = false;
          return;
        }
        setIsSaving(false);
        apiRef.current = null;
        const savedDir = response?.location || onSaveData.path;
        const savedUuid = response?.uuid || toAgentFileUuid(finalName);
        dispatch(fileBrowserActions.setShowFileBrowser(false));
        setFilebrowserFor("");
        setAgentName(finalName);
        document.title = `${finalName} | HI: Semantic Model`;
        setSaveDetails({
          location: savedDir,
          uuid: savedUuid,
          displayName: finalName,
        });
        setDoubleClickedFile({ path: savedDir, fileName: savedUuid });
        dispatch(setAgentMode("edit"));
        setAgentData(content);
        dispatch(
          agentFileDataAfterSave({
            dir: savedDir,
            file: savedUuid,
          }),
        );
        if (response?.data) {
          dispatch(
            fileBrowserActions.saveFileinFb(
              Array.isArray(response.data) ? response.data : [response.data],
            ),
          );
        }
      },
      errorCB: () => {
        if (abortedRef.current) {
          abortedRef.current = false;
          return;
        }
        setIsSaving(false);
        apiRef.current = null;
      },
    };
    if (uuid && agentMode === "edit" && filebrowserFor !== "saveAs") {
      apiParams.uuid = toAgentFileUuid(uuid);
    }
    apiRef.current = agentSaveAPI(apiParams);
  };
  useEffect(() => {
    if (agentData) {
      dispatch({ type: "SET_HAS_UNSAVED_DATA", payload: false });
    }
  }, [agentData]);

  const validateBeforeSave = () => {
    const { cubeFieldsData, agentState } =
      semanticEditorRef.current?.getSaveState?.() || {};
    return validateAgentSaveInput({
      cubeFieldsData,
      agentState,
      editorContent,
      isRawJsonView,
      dispatch,
    });
  };

  const onSaveAs = () => {
    if (isRawJsonView && !isJsonValid) {
      const Notify = notify(dispatch);
      if (jsonError) {
        Notify.error({
          type: "Frontend",
          message: `Cannot save: ${jsonError.formattedMessage}`,
        });
      } else {
        Notify.error({
          type: "Frontend",
          message: "Cannot save: JSON is invalid. Please fix the errors first.",
        });
      }
      return;
    }

    if (!validateBeforeSave()) {
      return;
    }

    setFilebrowserFor("saveAs");
    dispatch(fileBrowserActions.setShowFileBrowser(true));
    dispatch(fileBrowserActions.setGlobalFbVisibility(false));
  };

  if (filebrowserFor === "save" || filebrowserFor === "saveAs") {
    fbProperties.footerForm = {
      type: "Save",
      form: (
        <SaveItems
          key={filebrowserFor}
          formHeading="Semantic model file name"
          onFormSumbit={onFormSumbit}
          saveButtonText={filebrowserFor === "saveAs" ? "Save As" : "Save"}
          cancelButtonText="Cancel"
          inputValue={agentName || DEFAULT_AGENT_NAME}
          onNameChange={setAgentName}
        />
      ),
    };
  } else if (filebrowserFor === "edit") {
    fbProperties.extensionOptions = ["model"];
    fbProperties.showEditOnTop = true;
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
          extensions: ["model"],
          callback: (record) => {
            dispatch(
              appActions.setEditModeInfo({
                dir: record.path,
                file: record.name,
                extension: record.extension,
                title: record.title,
              }),
            );
          },
        },
      ],
    };
  }

  const resetAgentEditor = () => {
    semanticEditorRef.current?.resetEditor?.();
    setAgentData(null);
    setEditorContent("");
    setAgentName(DEFAULT_AGENT_NAME);
    setDoubleClickedFile(null);
    setSaveDetails({});
  };

  const onSave = () => {
    if (isRawJsonView && !isJsonValid) {
      const Notify = notify(dispatch);
      if (jsonError) {
        Notify.error({
          type: "Frontend",
          message: `Cannot save: ${jsonError.formattedMessage}`,
        });
      } else {
        Notify.error({
          type: "Frontend",
          message: "Cannot save: JSON is invalid. Please fix the errors first.",
        });
      }
      return;
    }

    if (!validateBeforeSave()) {
      return;
    }

    const savedLocation =
      saveDetails?.location ||
      agentDataAfterSave?.dir ||
      doubleClickedFile?.path;
    const savedFileName =
      agentDataAfterSave?.file ||
      saveDetails?.uuid ||
      doubleClickedFile?.fileName;
    if (agentMode === "edit" && savedLocation && savedFileName) {
      onFormSumbit({ path: savedLocation }, agentName, savedFileName);
      return;
    }
    setFilebrowserFor("save");
    dispatch(fileBrowserActions.setShowFileBrowser(true));
    dispatch(fileBrowserActions.setGlobalFbVisibility(false));
  };
  const saveDisabled = isRawJsonView && !isJsonValid;

  const taskBarItems = [
    {
      tooltip: "Save",
      icon: <SaveOutlined />,
      dropdown: [
        {
          tooltip: "Save",
          name: "Save",
          icon: <SaveOutlined />,
          callBack: onSave,
          disabled: saveDisabled,
        },
        {
          tooltip: "Save As",
          name: "Save As",
          icon: <SaveFilled />,
          callBack: onSaveAs,
          disabled: saveDisabled,
        },
      ],
    },
    {
      tooltip: isTableModeNormal ? "Switch To Advance" : "Switch To Normal",
      icon: <SettingOutlined />,
      callBack: () => semanticEditorRef.current?.toggleTableMode?.(),
    },
    {
      tooltip: "Layout",
      icon: <LayoutOutlined />,
      dropdown: [
        {
          tooltip: "Metadata Shelf",
          name: "Metadata Shelf",
          icon: shelfLayout.metadataShelf ? <CheckOutlined /> : null,
          callBack: () => toggleShelf("metadataShelf"),
        },
        {
          tooltip: "Fields Shelf",
          name: "Fields Shelf",
          icon: shelfLayout.fieldsShelf ? <CheckOutlined /> : null,
          callBack: () => toggleShelf("fieldsShelf"),
        },
        {
          tooltip: "Tools Shelf",
          name: "Tools Shelf",
          icon: shelfLayout.toolsShelf ? <CheckOutlined /> : null,
          callBack: () => toggleShelf("toolsShelf"),
        },
      ],
    },
  ];

  return (
    <HILayout
      customClass="hi-agent-page hi-p0"
      header={<HINavbar taskbar={taskBarItems} hideToggleSidebar />}
      content={
        <AgentLayout>
          <div className="height100percent" style={{ position: "relative" }}>
            <SemanticMetadataEditor
              ref={semanticEditorRef}
              agentData={agentData}
              agentName={agentName}
              onAgentNameChange={setAgentName}
              onContentChange={setEditorContent}
              isLoading={isLoading}
              handleAbort={handleAbort}
              dispatch={dispatch}
              isRawJsonView={isRawJsonView}
              onTableModeChange={setIsTableModeNormal}
              shelfLayout={shelfLayout}
              metadataShelfProps={{
                onResetAgentEditor: resetAgentEditor,
                urlObj,
                shareRef,
                fbProperties,
                setFilebrowserFor,
                filebrowserFor,
              }}
            />
            {openSource ? (
              <Watermark
                text={`Powered by ${metaInfo.productName}©${metaInfo.version}`}
                link={metaInfo.link || "https://www.helicalinsight.com/"}
                placement="bottom-right"
                tooltip="Please upgrade your license to remove this watermark."
                right={10}
              />
            ) : null}
          </div>
        </AgentLayout>
      }
      defaultSidebar={false}
    />
  );
}
