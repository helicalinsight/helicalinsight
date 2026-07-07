import React, {
  useCallback,
  useEffect,
  useImperativeHandle,
  useMemo,
  useReducer,
  useRef,
  useState,
} from "react";
import { useSelector } from "react-redux";
import { Cube } from "../../../hi-cube/cube";
import { CubeEditorProvider } from "../../../hi-cube/cubeEditorContext";
import {
  setCubeFieldsData,
  setCubeTableMode,
} from "../../utils/agent-editor-actions";
import {
  agentEditorReducer,
  createAgentEditorState,
  mergeAgentEditorState,
} from "../../utils/agent-editor";
import { EditorLoadingView } from "../../../common/editor-loading-view";
import {
  JsonEditorShell,
  useJsonClipboard,
} from "../../../common/json-editor";
import notify from "../../../hi-notifications/notify";
import {
  ensureShape,
  parsePastedAgentPayload,
} from "./semantic-metadata-utils";
import {
  convertAgentDataToCubeFieldsData,
  convertCubeFieldsDataToAgentData,
  getAgentStateFromCubeFields,
  resolveAgentDataFromInput,
  serializeAgentData,
  serializeAgentDataForDisplay,
  stripInternalTableRefsFromAgentState,
} from "../../utils/agent-cube-bridge";
import "./semantic-metadata-editor.scss";

const EMPTY_AGENT_DATA = ensureShape({});

const SemanticMetadataEditor = React.forwardRef(
  (
    {
      agentData = null,
      onContentChange,
      isLoading = false,
      handleAbort,
      dispatch: dispatchProp,
      isRawJsonView = false,
      onTableModeChange,
    },
    ref,
  ) => {
    const agentSlice = useSelector((store) => store.agent);
    const [editorState, editorDispatch] = useReducer(
      agentEditorReducer,
      agentSlice,
      (slice) => createAgentEditorState(slice),
    );
    const cubeState = useMemo(
      () => mergeAgentEditorState(editorState, agentSlice),
      [editorState, agentSlice],
    );
    const cubeFieldsData = cubeState.cubeFieldsData;
    const [jsonText, setJsonText] = useState(() =>
      serializeAgentDataForDisplay(EMPTY_AGENT_DATA),
    );
    const agentDataRef = useRef(EMPTY_AGENT_DATA);
    const editorStateRef = useRef(editorState);
    editorStateRef.current = editorState;
    const lastLoadedAgentPropRef = useRef(null);
    const skipCubeSyncRef = useRef(false);
    const skipJsonSyncRef = useRef(false);
    const lastFieldsSerializedRef = useRef(
      serializeAgentData(EMPTY_AGENT_DATA),
    );
    const onContentChangeRef = useRef(onContentChange);
    const onTableModeChangeRef = useRef(onTableModeChange);

    const Notify = notify(dispatchProp);

    onContentChangeRef.current = onContentChange;
    onTableModeChangeRef.current = onTableModeChange;

    useEffect(() => {
      onTableModeChangeRef.current?.(cubeState.isCubeTableModeNormal);
    }, [cubeState.isCubeTableModeNormal]);

    const syncCubeFromAgent = useCallback((nextAgentData) => {
      skipCubeSyncRef.current = true;
      editorDispatch(
        setCubeFieldsData({
          mode: "edit",
          value: convertAgentDataToCubeFieldsData(nextAgentData),
        }),
      );
      requestAnimationFrame(() => {
        skipCubeSyncRef.current = false;
      });
    }, []);

    const publishAgentData = useCallback(
      (nextAgentData, { syncCube = true, syncJson = true } = {}) => {
        const shaped = ensureShape(nextAgentData);
        const serialized = serializeAgentData(shaped);

        agentDataRef.current = shaped;

        if (syncJson) {
          skipJsonSyncRef.current = true;
          setJsonText(serializeAgentDataForDisplay(shaped));
          lastFieldsSerializedRef.current = serialized;
          requestAnimationFrame(() => {
            skipJsonSyncRef.current = false;
          });
        }

        if (syncCube) {
          syncCubeFromAgent(shaped);
        }

        onContentChangeRef.current?.(serialized);
        return shaped;
      },
      [syncCubeFromAgent],
    );

    const applyAgentData = useCallback(
      (nextAgentData, options) => publishAgentData(nextAgentData, options),
      [publishAgentData],
    );

    const applyParsedPayload = useCallback(
      (rawText, { syncJson = true } = {}) => {
        const { agentState } = parsePastedAgentPayload(rawText);
        const nextAgentData = resolveAgentDataFromInput(
          agentDataRef.current,
          agentState,
        );
        lastLoadedAgentPropRef.current = serializeAgentData(nextAgentData);
        return applyAgentData(nextAgentData, { syncJson });
      },
      [applyAgentData],
    );

    const getClipboardPayload = useCallback(() => {
      const agentState = stripInternalTableRefsFromAgentState(
        getAgentStateFromCubeFields(cubeFieldsData, agentDataRef.current),
      );
      return serializeAgentData(agentState);
    }, [cubeFieldsData]);

    const {
      pasteOpen,
      pasteText,
      setPasteText,
      handleCopy,
      handleOpenPaste,
      handlePasteLoad,
      closePasteModal,
      resetPasteModal,
    } = useJsonClipboard({
      getPayload: getClipboardPayload,
      applyPayload: applyParsedPayload,
      onCopySuccess: () => {
        Notify.success({
          type: "Frontend",
          message: "Copied JSON to clipboard",
        });
      },
      onPasteError: (err) => {
        Notify.error({
          type: "Frontend",
          message: `Invalid JSON: ${err.message}`,
        });
      },
    });

    const resetEditor = useCallback(() => {
      lastLoadedAgentPropRef.current = null;
      resetPasteModal();
      skipCubeSyncRef.current = true;
      skipJsonSyncRef.current = true;
      editorDispatch(setCubeFieldsData({ mode: "reset" }));
      publishAgentData(EMPTY_AGENT_DATA, { syncCube: false, syncJson: true });
      requestAnimationFrame(() => {
        skipCubeSyncRef.current = false;
        skipJsonSyncRef.current = false;
      });
    }, [publishAgentData, resetPasteModal]);

    useEffect(() => {
      if (isLoading) {
        return;
      }

      if (agentData == null) {
        if (lastLoadedAgentPropRef.current != null) {
          resetEditor();
        }
        return;
      }

      const serializedProp = serializeAgentData(agentData);
      if (lastLoadedAgentPropRef.current === serializedProp) {
        return;
      }

      lastLoadedAgentPropRef.current = serializedProp;
      applyAgentData(agentData);
    }, [agentData, isLoading, applyAgentData, resetEditor]);

    const syncJsonFromCubeFields = useCallback(() => {
      if (skipCubeSyncRef.current || skipJsonSyncRef.current) {
        return;
      }

      const nextAgentData = convertCubeFieldsDataToAgentData(
        cubeFieldsData,
        agentDataRef.current,
      );
      const serialized = serializeAgentData(nextAgentData);
      if (serialized === lastFieldsSerializedRef.current) {
        return;
      }

      publishAgentData(nextAgentData, { syncCube: false, syncJson: true });
    }, [cubeFieldsData, publishAgentData]);

    useEffect(() => {
      syncJsonFromCubeFields();
    }, [syncJsonFromCubeFields]);

    const handleJsonChange = useCallback(
      (content) => {
        if (!content?.trim()) {
          applyAgentData(EMPTY_AGENT_DATA, { syncCube: true, syncJson: true });
          return;
        }

        try {
          const nextAgentData = resolveAgentDataFromInput(
            agentDataRef.current,
            JSON.parse(content),
          );
          lastLoadedAgentPropRef.current = serializeAgentData(nextAgentData);
          applyAgentData(nextAgentData, { syncCube: true, syncJson: true });
        } catch {
          setJsonText(content);
        }
      },
      [applyAgentData],
    );

    useImperativeHandle(ref, () => ({
      handleCopy,
      handleOpenPaste,
      toggleTableMode: () => editorDispatch(setCubeTableMode()),
      resetEditor,
      getSaveState: () => ({
        cubeFieldsData: editorStateRef.current.cubeFieldsData,
        agentState: agentDataRef.current,
      }),
    }));

    if (isLoading) {
      return (
        <EditorLoadingView
          handleAbort={handleAbort}
          className="semantic-metadata-editor"
        />
      );
    }

    return (
      <CubeEditorProvider
        cubeState={cubeState}
        dispatch={editorDispatch}
        variant="agent"
      >
        <JsonEditorShell
          className={`semantic-metadata-editor${
            isRawJsonView ? " semantic-metadata-editor--raw-json" : ""
          }`}
          isRawJsonView={isRawJsonView}
          jsonText={jsonText}
          onJsonChange={handleJsonChange}
          isJsonActive={false}
          pasteOpen={pasteOpen}
          pasteText={pasteText}
          onPasteTextChange={setPasteText}
          onPasteClose={closePasteModal}
          onPasteLoad={handlePasteLoad}
        >
          <Cube />
        </JsonEditorShell>
      </CubeEditorProvider>
    );
  },
);

export default SemanticMetadataEditor;