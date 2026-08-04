import React, { useCallback, useRef } from "react";
import {
  CopyOutlined,
  SaveOutlined,
  SearchOutlined,
} from "@ant-design/icons";
import { MonacoJsonEditor } from "../../../../common/json-editor";
import { ToolbarIconButton } from "../../../../common/toolbar-icon-button";

export function AgentJsonPanel({
  value = "",
  onChange,
  onSave,
  onCopy,
  hasUnsavedChanges = false,
}) {
  const editorRef = useRef(null);
  const handleFindReplace = useCallback(() => {
    const editor = editorRef.current;
    if (!editor) return;
    editor.focus();
    editor.getAction("editor.action.startFindReplaceAction")?.run();
  }, []);

  return (
    <div className="agent-json-panel">
      <div className="cube-business-view-toolbar agent-json-toolbar">
        <ToolbarIconButton
          title="Find & Replace"
          onClick={handleFindReplace}
        >
          <SearchOutlined className="cube-add-metric-icon" />
        </ToolbarIconButton>
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
        editorRef={editorRef}
        className="monaco-json-editor ai-agent-json-editor"
      />
    </div>
  );
}

export default AgentJsonPanel;
