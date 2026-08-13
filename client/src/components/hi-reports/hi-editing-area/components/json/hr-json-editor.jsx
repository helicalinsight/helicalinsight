import React, { useEffect, useRef, useState } from "react";
import { CloseOutlined } from "@ant-design/icons";
import { Drawer } from "antd";
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
  const skipJsonSyncRef = useRef(false);
  const lastSerializedJsonRef = useRef("");
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

  const handleCloseDrawer = () => {
    dispatch(changeEditingPane({ id: "1" }));
  };

  const handleCopyJson = async () => {
    try {
      await copyToClipboard(jsonText);
      notifyFrontend(dispatch, "success", "Copied JSON to clipboard");
    } catch {
      notifyFrontend(dispatch, "error", "Failed to copy JSON");
    }
  };

  const handleApplyJson = () => {
    if (!activeReport?.metadata) {
      notifyFrontend(dispatch, "warning", "Metadata not found");
      return;
    }
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

  return (
    <Drawer
      title="JSON Editor"
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
          value={jsonText}
          onChange={handleJsonChange}
          onSave={handleApplyJson}
          onCopy={handleCopyJson}
          hasUnsavedChanges={hasUnsavedJsonChanges}
          saveTitle="Apply JSON"
        />
      </div>
    </Drawer>
  );
};

export default HrJsonEditor;
