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
import { CubeEditorProvider } from "../../../hi-cube/cubeEditorContext";
import { AgentWorkspace } from "./agent-shelves";
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
  normalizeAgentData,
  normalizeAgentFieldFormats,
  resolveAgentDataFromInput,
  serializeAgentData,
  serializeAgentDataForDisplay,
} from "../../utils/agent-cube-bridge";
import { AgentNameProvider } from "../../../common/agent-name-context";
import "./semantic-metadata-editor.scss";

const EMPTY_AGENT_DATA = ensureShape({});
const EMPTY_SEMANTIC_TYPES = [];

const SemanticMetadataEditor = React.forwardRef(
  (
    {
      agentData = null,
      agentName = "Model_1",
      onAgentNameChange,
      onContentChange,
      isLoading = false,
      handleAbort,
      dispatch: dispatchProp,
      isRawJsonView = false,
      onTableModeChange,
      shelfLayout = {
        metadataShelf: true,
        fieldsShelf: true,
        toolsShelf: true,
      },
      metadataShelfProps = {},
    },
    ref,
  ) => {
    const agentSlice = useSelector((store) => store.agent);
    const semanticTypeOptions =
      agentSlice?.semanticTypes || EMPTY_SEMANTIC_TYPES;
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
    const [hasUnsavedJsonChanges, setHasUnsavedJsonChanges] = useState(false);
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
        const shaped = normalizeAgentFieldFormats(
          normalizeAgentData(nextAgentData),
          semanticTypeOptions,
          agentDataRef.current,
        );
        const serialized = serializeAgentData(shaped);
        agentDataRef.current = shaped;

        if (syncJson) {
          skipJsonSyncRef.current = true;
          setJsonText(serializeAgentDataForDisplay(shaped));
          setHasUnsavedJsonChanges(false);
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
      [semanticTypeOptions, syncCubeFromAgent],
    );

    const applyJsonText = useCallback(
      (rawText, options) => {
        if (!rawText?.trim()) {
          return publishAgentData(EMPTY_AGENT_DATA, options);
        }
        const { agentState } = parsePastedAgentPayload(rawText);
        const nextAgentData = resolveAgentDataFromInput(
          agentDataRef.current,
          normalizeAgentData(agentState),
        );
        lastLoadedAgentPropRef.current = serializeAgentData(nextAgentData);
        return publishAgentData(nextAgentData, options);
      },
      [publishAgentData],
    );

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
      getPayload: () => jsonText,
      applyPayload: applyJsonText,
      onCopySuccess: () => {
        notify(dispatchProp).success({
          type: "Frontend",
          message: "Copied JSON to clipboard",
        });
      },
      onPasteError: (err) => {
        notify(dispatchProp).error({
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
      publishAgentData(agentData);
    }, [agentData, isLoading, publishAgentData, resetEditor]);

    useEffect(() => {
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

    const handleJsonDraftChange = useCallback((content) => {
      setJsonText(content ?? "");
      setHasUnsavedJsonChanges(true);
    }, []);

    const handleJsonSave = useCallback(() => {
      try {
        applyJsonText(jsonText, { syncJson: true });
        notify(dispatchProp).success({
          type: "Frontend",
          message: "JSON saved successfully",
        });
      } catch (err) {
        notify(dispatchProp).error({
          type: "Frontend",
          message: `Invalid JSON: ${err.message}`,
        });
      }
    }, [applyJsonText, dispatchProp, jsonText]);

    const handleJsonChange = useCallback(
      (content) => {
        try {
          applyJsonText(content, { syncCube: true, syncJson: true });
        } catch {
          handleJsonDraftChange(content);
        }
      },
      [applyJsonText, handleJsonDraftChange],
    );

    useImperativeHandle(ref, () => ({
      handleCopy,
      handleOpenPaste,
      handleJsonSave,
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
      <AgentNameProvider
        agentName={agentName}
        onAgentNameChange={onAgentNameChange}
      >
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
            <AgentWorkspace
              shelfLayout={shelfLayout}
              metadataShelfProps={metadataShelfProps}
              jsonText={jsonText}
              onJsonChange={handleJsonDraftChange}
              onSaveJson={handleJsonSave}
              onCopyJson={handleCopy}
              hasUnsavedJsonChanges={hasUnsavedJsonChanges}
            />
          </JsonEditorShell>
        </CubeEditorProvider>
      </AgentNameProvider>
    );
  },
);

export default SemanticMetadataEditor;
