import React from "react";
import { CopyOutlined, SaveOutlined } from "@ant-design/icons";
import { MonacoJsonEditor } from "../../../../common/json-editor";
import { ToolbarIconButton } from "../../../../common/toolbar-icon-button";

export function AgentJsonPanel({
  value = "",
  onChange,
  onSave,
  onCopy,
  hasUnsavedChanges = false,
}) {
  return (
    <div className="agent-json-panel">
      <div className="cube-business-view-toolbar agent-json-toolbar">
        <ToolbarIconButton title="Copy JSON" onClick={onCopy}>
          <CopyOutlined className="cube-add-metric-icon" />
        </ToolbarIconButton>
        <ToolbarIconButton
          title="Save JSON"
          onClick={onSave}
          disabled={!hasUnsavedChanges}
          showIndicator={hasUnsavedChanges}
        >
          <SaveOutlined className="cube-add-metric-icon" />
        </ToolbarIconButton>
      </div>
      <MonacoJsonEditor
        value={value}
        onChange={onChange}
        isActive
        className="monaco-json-editor ai-agent-json-editor"
      />
    </div>
  );
}

export default AgentJsonPanel;
