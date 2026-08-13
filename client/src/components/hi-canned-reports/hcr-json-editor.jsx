import { CloseOutlined } from "@ant-design/icons";
import { Drawer } from "antd";
import { useEffect, useRef, useState } from "react";
import { useDispatch } from "react-redux";
import { copyToClipboard, JsonEditorPanel } from "../common/json-editor";
import notify from "../hi-notifications/notify";
import { parseReportJsonText } from "../hi-reports/utils/hr-json-utils";
import { getReportForViewer, mergeJSONIntoHCRReport } from "./hcr-json-utils";
import { hcrActions } from "../../redux/actions";

const EMPTY_JSON = "{\n}\n";

const notifyFrontend = (dispatch, level, message) =>
  notify(dispatch)[level]({ type: "Frontend", message });


const HCRJsonEditor = (props = {}) => {
  const {
    visible,
    onCloseDrawer = () => { },
    activeReport = {}
  } = props || {}

  const dispatch = useDispatch();
  const [jsonText, setJsonText] = useState(EMPTY_JSON);
  const [hasUnsavedJsonChanges, setHasUnsavedJsonChanges] = useState(false);
  const skipJsonSyncRef = useRef(false);
  const lastSerializedJsonRef = useRef("");

  const handleJsonChange = (text) => {
    setJsonText(text ?? "");
    setHasUnsavedJsonChanges(true);
  };

  const handleApplyJson = () => {
    try {
      const parsedReportState = parseReportJsonText(jsonText);
      const mergedReport = mergeJSONIntoHCRReport(
        activeReport,
        parsedReportState,
      );
      const serializedJson = getReportForViewer(mergedReport);
      lastSerializedJsonRef.current = serializedJson;
      setJsonText(serializedJson);
      setHasUnsavedJsonChanges(false);
      dispatch(hcrActions.hcrUpdateReportThroughEditor(mergedReport));

      notifyFrontend(
        dispatch,
        "success",
        "Canned Report Spec updated successfully",
      );
    } catch (error) {
      notifyFrontend(dispatch, "error", `Invalid SPEC: ${error.message}`);
    }
  }

  const handleCopyJson = async () => {
    try {
      await copyToClipboard(jsonText);
      notifyFrontend(dispatch, "success", "Copied JSON to clipboard");
    } catch {
      notifyFrontend(dispatch, "error", "Failed to copy JSON");
    }
  }

  useEffect(() => {
    if (hasUnsavedJsonChanges || skipJsonSyncRef.current) {
      return;
    }
    const nextJsonText = activeReport
      ? getReportForViewer(activeReport)
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


  return (
    <Drawer
      title="Canned Report Spec"
      placement="right"
      width="45%"
      visible={visible}
      onClose={onCloseDrawer}
      className="hcr-json-editor-drawer"
      closeIcon={<CloseOutlined data-testid="hcr-json-editor-close-icon" />}
      destroyOnClose
    >
      <div
        className="hcr-json-editor-drawer-body"
        data-testid="hcr-json-editor-drawer-body"
      >
        <JsonEditorPanel
          value={jsonText}
          onChange={handleJsonChange}
          onSave={handleApplyJson}
          onCopy={handleCopyJson}
          hasUnsavedChanges={hasUnsavedJsonChanges}
          saveTitle="Apply Changes, Note: Please do not change name of any key, as it may break the report."
        />
      </div>
    </Drawer>
  )
}

export default HCRJsonEditor