import React from "react";
import { JsonEditorPanel } from "../../../../common/json-editor";

export function AgentJsonPanel(props) {
  return (
    <JsonEditorPanel
      className="json-editor-panel agent-json-panel"
      toolbarClassName="json-editor-panel-toolbar agent-json-toolbar"
      editorClassName="monaco-json-editor ai-agent-json-editor"
      iconClassName="cube-add-metric-icon"
      {...props}
    />
  );
}

export default AgentJsonPanel;
