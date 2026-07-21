import React, { useMemo } from "react";
import { CopyOutlined, SnippetsOutlined } from "@ant-design/icons";
import { useCubeEditorBindings } from "../../../../hi-cube/cubeEditorContext";
import { MonacoJsonEditor } from "../../../../common/json-editor";
import { ToolbarIconButton } from "../../../../common/toolbar-icon-button";
import {
  getAgentStateFromCubeFields,
  serializeAgentDataForDisplay,
} from "../../../utils/agent-cube-bridge";

export function AgentJsonPanel({ onCopy, onPaste }) {
  const { cubeState } = useCubeEditorBindings();
  const { cubeFieldsData } = cubeState;

  const jsonText = useMemo(() => {
    try {
      return serializeAgentDataForDisplay(
        getAgentStateFromCubeFields(cubeFieldsData),
      );
    } catch {
      return "{}";
    }
  }, [cubeFieldsData]);

  return (
    <div className="agent-json-panel">
      <div className="cube-business-view-toolbar agent-json-toolbar">
        <ToolbarIconButton title="Copy JSON" onClick={onCopy}>
          <CopyOutlined className="cube-add-metric-icon" />
        </ToolbarIconButton>
        <ToolbarIconButton title="Paste JSON" onClick={onPaste}>
          <SnippetsOutlined className="cube-add-metric-icon" />
        </ToolbarIconButton>
      </div>
      <MonacoJsonEditor
        value={jsonText}
        isActive={false}
        className="monaco-json-editor ai-agent-json-editor"
      />
    </div>
  );
}

export default AgentJsonPanel;
