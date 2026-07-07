import { HINavbar } from "../components";
import SemanticMetadataEditor from "../components/hi-agent/components/semantic-metadata-editor";
import HILayout from "../layouts/hi-layout";
import {
  SaveOutlined,
  SaveFilled,
  SettingOutlined,
  CopyOutlined,
  SnippetsOutlined,
  FileTextOutlined,
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
import {
  getAgentStateForSave,
  validateAgentSaveInput,
} from "../components/hi-agent/utils/agent-helperMethods";
import { stripInternalTableRefsFromAgentState } from "../components/hi-agent/utils/agent-cube-bridge";
import SaveItems from "../components/hi-fileBrowser/SaveItems";
import React, { useEffect, useState, useRef, useCallback } from "react";
import { useDispatch, useSelector } from "react-redux";
import { AgentSidebar } from "../components/hi-agent/components/agentSidebar";

const AGENT_FILE_EXTENSION = ".agent";

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
  const dispatch = useDispatch();
  const [filebrowserFor, setFilebrowserFor] = useState("");
  const [agentData, setAgentData] = useState(null);
  const [editorContent, setEditorContent] = useState("");
  const [isJsonValid, setIsJsonValid] = useState(true);
  const [jsonError, setJsonError] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const [saveDetails, setSaveDetails] = useState({});
  const [doubleClickedFile, setDoubleClickedFile] = useState(null);
  const [agentName, setAgentName] = useState("Agent_1");
  const [isRawJsonView, setIsRawJsonView] = useState(false);
  const [isTableModeNormal, setIsTableModeNormal] = useState(true);

  const apiRef = useRef(null);
  const abortedRef = useRef(false);
  const semanticEditorRef = useRef(null);
  const { dir: urlDir, file: urlFile } = urlObj;
  const clearEditorLoading = () => {
    setIsLoading(false);
    setIsEditing(false);
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
    setIsEditing(true);
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
        setIsEditing(false);
        apiRef.current = null;
        Notify.success({
          type: "Frontend",
          message: "Agent loaded successfully",
        });
        if (response?.data?.state) {
          setAgentData(response.data.state);
        }
        if (response?.data?.agentName) {
          setAgentName(response.data.agentName);
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
        document.title = `${title || file.split(".")[0]} | HI:Agent`;
      },
      errorCB: () => {
        if (abortedRef.current) {
          abortedRef.current = false;
          return;
        }
        setIsEditing(false);
        apiRef.current = null;
      },
    });
    apiRef.current = editInstance;
  };

  useEffect(() => {
    if (
      editModeInfo &&
      editModeInfo.extension === "agent" &&
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
      if (extension === "agent") {
        handleAgentEdit({ dir: urlDir, file: urlFile });
      }
    }
  }, [urlObj]);

  useEffect(() => {
    if (urlFile) {
      const base = urlFile.split(".")[0];
      if (base) {
        setAgentName(base);
      }
    }
  }, [urlFile]);
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
    let content;

    try {
      content = getSaveContent();
    } catch (e) {
      const Notify = notify(dispatch);
      Notify.error({
        type: "Frontend",
        message: `Invalid JSON: ${e.message}`,
      });
      return;
    }
    const Notify = notify(dispatch);
    const finalName = name || agentName || "Agent_1";
    abortedRef.current = false;
    setIsSaving(true);
    const apiParams = {
      dir,
      file,
      agentDir: onSaveData.path,
      agentName: finalName,
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
        document.title = `${finalName} | HI:Agent`;
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
          formHeading="Agent file name"
          onFormSumbit={onFormSumbit}
          saveButtonText={filebrowserFor === "saveAs" ? "Save As" : "Save"}
          cancelButtonText="Cancel"
          inputValue={agentName || "Agent_1"}
        />
      ),
    };
  } else if (filebrowserFor === "edit") {
    fbProperties.extensionOptions = ["agent"];
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
          extensions: ["agent"],
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
    setAgentName("Agent_1");
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
      tooltip: "Copy JSON",
      icon: <CopyOutlined />,
      callBack: () => {
        semanticEditorRef.current?.handleCopy?.();
      },
    },
    {
      tooltip: "Paste JSON",
      icon: <SnippetsOutlined />,
      callBack: () => {
        semanticEditorRef.current?.handleOpenPaste?.();
      },
    },
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
      tooltip: isRawJsonView ? "Switch To Editor" : "Raw JSON",
      icon: <FileTextOutlined />,
      callBack: () => setIsRawJsonView((prev) => !prev),
    },
    {
      tooltip: isTableModeNormal ? "Switch To Advance" : "Switch To Normal",
      icon: <SettingOutlined />,
      callBack: () => semanticEditorRef.current?.toggleTableMode?.(),
    },
  ];

  return (
    <HILayout
      customClass="hi-agent-page"
      header={<HINavbar taskbar={taskBarItems} />}
      content={
        <SemanticMetadataEditor
          ref={semanticEditorRef}
          agentData={agentData}
          onContentChange={setEditorContent}
          isLoading={isLoading || isEditing}
          handleAbort={handleAbort}
          dispatch={dispatch}
          isRawJsonView={isRawJsonView}
          onTableModeChange={setIsTableModeNormal}
        />
      }
      defaultSidebar={false}
      customSidebar={
        <AgentSidebar
          onResetAgentEditor={resetAgentEditor}
          urlObj={urlObj}
          shareRef={shareRef}
          fbProperties={fbProperties}
          setFilebrowserFor={setFilebrowserFor}
          filebrowserFor={filebrowserFor}
          onAgentDataLoaded={(stateData, agentNameFromResponse) => {
            setAgentData(stateData);
            if (agentNameFromResponse) {
              setAgentName(agentNameFromResponse);
            }
          }}
        />
      }
    />
  );
}
