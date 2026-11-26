import React, { useEffect, useRef, useState } from "react";
import { CloseOutlined } from "@ant-design/icons";
import { Drawer, Switch } from "antd";
import { useDispatch, useSelector } from "react-redux";
import {
  JsonEditorPanel,
  copyToClipboard,
} from "../../../../common/json-editor";
import notify from "../../../../hi-notifications/notify";
import {
  changeEditingPane,
  loadReportData,
  setHReportLoading,
} from "../../../../../redux/actions/hreport.actions";
import { generateReport } from "../../../utils/base";
import {
  buildMetadataMismatchReportData,
  getReportJsonFetchDecision,
  mergeReportJsonIntoReport,
  normalizeReportForFetch,
  parseReportJsonText,
  serializeReportJson,
} from "../../../utils/hr-json-utils";

const EMPTY_JSON = "{\n}\n";

const notifyFrontend = (dispatch, level, message) =>
  notify(dispatch)[level]({ type: "Frontend", message });

const HrJsonEditor = ({ reportId, getApi }) => {
  const dispatch = useDispatch();
  const activeReport = useSelector((state) =>
    state.hreport.present.reports.find((report) => report.id === reportId),
  );
  const { user = {} } = useSelector(
    (state) => state.app.applicationSettingsData.userData,
  );
  const [jsonText, setJsonText] = useState(EMPTY_JSON);
  const [hasUnsavedJsonChanges, setHasUnsavedJsonChanges] = useState(false);
  const [reportModelText, setReportModelText] = useState(EMPTY_JSON);
  const [hasUnsavedModelChanges, setHasUnsavedModelChanges] = useState(false);
  // The header toggle swaps the editor between the report spec and the Instant BI report model.
  const [showReportModel, setShowReportModel] = useState(false);
  // An explored report opens on the model. A manual toggle is kept after that.
  const viewChosenRef = useRef(false);
  const skipJsonSyncRef = useRef(false);
  const lastSerializedJsonRef = useRef("");
  const skipModelSyncRef = useRef(false);
  const lastSerializedModelRef = useRef("");
  // Blocks the editor from clearing while the bridge rebuilds the report and drops semantic state.
  const applyingModelRef = useRef(false);
  const hasMetadata = Boolean(activeReport?.metadata);

  useEffect(() => {
    if (!hasMetadata) {
      notifyFrontend(dispatch, "warning", "Metadata not found");
    }
  }, [dispatch, hasMetadata]);

  useEffect(() => {
    if (hasUnsavedJsonChanges || skipJsonSyncRef.current) {
      return;
    }
    const nextJsonText = activeReport
      ? serializeReportJson(activeReport)
      : EMPTY_JSON;
    if (nextJsonText === lastSerializedJsonRef.current) {
      return;
    }
    skipJsonSyncRef.current = true;
    lastSerializedJsonRef.current = nextJsonText;
    setJsonText(nextJsonText);
    setHasUnsavedJsonChanges(false);
    requestAnimationFrame(() => {
      skipJsonSyncRef.current = false;
    });
  }, [activeReport, hasUnsavedJsonChanges]);

  const storedReportModel = activeReport?.instantSemantic?.reportModel;
  const hasReportModel = Boolean(
    storedReportModel
    && typeof storedReportModel === "object"
    && !Array.isArray(storedReportModel)
    && Object.keys(storedReportModel).length,
  );

  useEffect(() => {
    if (applyingModelRef.current || hasUnsavedModelChanges || skipModelSyncRef.current) {
      return;
    }
    const nextModelText = hasReportModel
      ? JSON.stringify(storedReportModel, null, 2)
      : EMPTY_JSON;
    if (nextModelText === lastSerializedModelRef.current) {
      return;
    }
    skipModelSyncRef.current = true;
    lastSerializedModelRef.current = nextModelText;
    setReportModelText(nextModelText);
    setHasUnsavedModelChanges(false);
    requestAnimationFrame(() => {
      skipModelSyncRef.current = false;
    });
  }, [storedReportModel, hasReportModel, hasUnsavedModelChanges]);

  // A new report starts on the spec. The manual toggle is cleared with it.
  useEffect(() => {
    viewChosenRef.current = false;
    setShowReportModel(false);
  }, [reportId]);

  // An explored report has a model, so the drawer opens on that model until the user switches it.
  useEffect(() => {
    if (viewChosenRef.current || !hasReportModel) return;
    setShowReportModel(true);
  }, [reportId, hasReportModel]);

  const handleCloseDrawer = () => {
    dispatch(changeEditingPane({ id: "1" }));
  };

  const handleApplyJson = () => {
    try {
      const parsedReportState = parseReportJsonText(jsonText);
      const mergedReport = mergeReportJsonIntoReport(
        activeReport,
        parsedReportState,
      );
      const serializedJson = serializeReportJson(mergedReport);
      lastSerializedJsonRef.current = serializedJson;
      setJsonText(serializedJson);
      setHasUnsavedJsonChanges(false);
      const fetchDecision = getReportJsonFetchDecision(mergedReport);

      if (fetchDecision.reason === "metadata-mismatch") {
        dispatch(
          loadReportData({
            ...mergedReport,
            reportData: buildMetadataMismatchReportData(fetchDecision.message),
            loadState: true,
            undoRedoAction: true,
          }),
        );
        notifyFrontend(dispatch, "warning", fetchDecision.message);
        return;
      }

      dispatch(
        loadReportData({
          ...mergedReport,
          loadState: true,
          undoRedoAction: true,
        }),
      );

      if (!fetchDecision.shouldFetch) {
        notifyFrontend(dispatch, "success", fetchDecision.message);
        return;
      }

      const reportForFetch = normalizeReportForFetch(mergedReport, user);
      window.setTimeout(() => {
        try {
          const result = generateReport(reportForFetch, dispatch, getApi);
          if (result?.then) {
            result.catch(() => {
              dispatch(
                setHReportLoading({ id: reportForFetch.id, loading: false }),
              );
            });
          }
        } catch (error) {
          dispatch(
            setHReportLoading({ id: reportForFetch.id, loading: false }),
          );
          notifyFrontend(
            dispatch,
            "error",
            `Failed to fetch report data: ${error.message}`,
          );
        }
      }, 0);

      notifyFrontend(
        dispatch,
        "success",
        "JSON applied successfully. Fetching report data.",
      );
    } catch (error) {
      notifyFrontend(dispatch, "error", `Invalid JSON: ${error.message}`);
    }
  };

  const handleJsonChange = (nextContent) => {
    setJsonText(nextContent ?? "");
    setHasUnsavedJsonChanges(true);
  };

  const handleReportModelChange = (nextContent) => {
    setReportModelText(nextContent ?? "");
    setHasUnsavedModelChanges(true);
  };

  const reportMetadataFromReport = (report) => {
    if (!report?.metadata?.tables) return null;
    return {
      metadata: report.metadata,
      funcs: {
        functions: report.functions,
        databaseFunctions: report.databaseFunctions || {},
      },
      dateFunctions: report.dateFunctions,
      isCube: report.isCube,
      cube: report.cube,
    };
  };

  // Rebuild the open report from the edited model. The spec Apply path stays separate.
  const handleApplyReportModel = async () => {
    let parsedModel;
    try {
      parsedModel = JSON.parse(reportModelText);
    } catch (error) {
      notifyFrontend(dispatch, "error", `Invalid JSON: ${error.message}`);
      return;
    }
    if (
      !parsedModel
      || typeof parsedModel !== "object"
      || Array.isArray(parsedModel)
      || !Object.keys(parsedModel).length
    ) {
      notifyFrontend(dispatch, "error", "Report model must be a JSON object.");
      return;
    }

    const serializedModel = JSON.stringify(parsedModel, null, 2);
    applyingModelRef.current = true;
    lastSerializedModelRef.current = serializedModel;
    setReportModelText(serializedModel);
    setHasUnsavedModelChanges(false);

    try {
      // Deferred so this drawer does not import the bridge while the bridge is still loading the editor.
      const { applyReportModelToActiveHreport } = await import(
        "../../../../hi-instant-bi/utils/instant-bi-requests"
      );
      const result = await applyReportModelToActiveHreport({
        dispatch,
        reportId,
        reportModel: parsedModel,
        reportMetadata: reportMetadataFromReport(activeReport),
        semanticDetails: { reportModel: parsedModel },
      });
      if (result?.error) {
        setHasUnsavedModelChanges(true);
        notifyFrontend(dispatch, "error", "Failed to apply report model.");
        return;
      }
      notifyFrontend(dispatch, "success", "Report model applied. Updating the report.");
    } catch (error) {
      setHasUnsavedModelChanges(true);
      notifyFrontend(dispatch, "error", `Failed to apply report model: ${error.message}`);
    } finally {
      applyingModelRef.current = false;
    }
  };

  const displayedJson = showReportModel ? reportModelText : jsonText;

  return (
    <Drawer
      title={(
        <div className="hi-hr-json-editor-title">
          <span>Report Spec</span>
          <Switch
            checked={showReportModel}
            onChange={(checked) => {
              viewChosenRef.current = true;
              setShowReportModel(checked);
            }}
            disabled={!hasReportModel && !showReportModel}
            data-testid="hi-hr-json-report-model-toggle"
          />
          <span>Report Model</span>
        </div>
      )}
      placement="right"
      width="45%"
      visible
      onClose={handleCloseDrawer}
      className="hi-hr-json-editor-drawer"
      closeIcon={<CloseOutlined data-testid="hi-hr-json-editor-close-icon" />}
      destroyOnClose
    >
      <div
        className="hi-hr-json-editor-drawer-body"
        data-testid="hi-hr-json-editor-drawer-body"
      >
        <JsonEditorPanel
          value={displayedJson}
          onChange={showReportModel ? handleReportModelChange : handleJsonChange}
          onSave={showReportModel ? handleApplyReportModel : handleApplyJson}
          onCopy={async () => {
            try {
              await copyToClipboard(displayedJson);
              notifyFrontend(dispatch, "success", "Copied JSON to clipboard");
            } catch {
              notifyFrontend(dispatch, "error", "Failed to copy JSON");
            }
          }}
          hasUnsavedChanges={showReportModel ? hasUnsavedModelChanges : hasUnsavedJsonChanges}
          saveTitle={showReportModel ? "Apply Report Model" : "Apply JSON"}
          showToolbar
        />
      </div>
    </Drawer>
  );
};

export default HrJsonEditor;
