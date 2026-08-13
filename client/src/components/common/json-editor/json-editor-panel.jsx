import React, { useRef } from "react";
import {
  CopyOutlined,
  SaveOutlined,
  SearchOutlined,
} from "@ant-design/icons";
import { MonacoJsonEditor } from "./json-editor";
import { ToolbarIconButton } from "../toolbar-icon-button";
import "./json-editor.scss";

// Shared Monaco JSON panel (Find / Copy / Save) 
export function JsonEditorPanel({
  value = "",
  onChange,
  onSave,
  onCopy,
  hasUnsavedChanges = false,
  saveTitle = "Save JSON",
  className = "json-editor-panel",
  toolbarClassName = "json-editor-panel-toolbar",
  editorClassName = "monaco-json-editor",
  iconClassName = "json-editor-panel-icon",
}) {
  const editorRef = useRef(null);
  const toolbarActions = [
    {
      title: "Find & Replace",
      onClick: () => {
        editorRef.current?.focus();
        editorRef.current
          ?.getAction("editor.action.startFindReplaceAction")
          ?.run();
      },
      icon: SearchOutlined,
    },
    { title: "Copy JSON", onClick: onCopy, icon: CopyOutlined },
    {
      title: saveTitle,
      onClick: onSave,
      disabled: !hasUnsavedChanges,
      showIndicator: hasUnsavedChanges,
      icon: SaveOutlined,
    },
  ];

  return (
    <div className={className}>
      <div className={toolbarClassName}>
        {toolbarActions.map(({ title, icon: Icon, ...actionProps }) => (
          <ToolbarIconButton key={title} title={title} {...actionProps}>
            <Icon className={iconClassName} />
          </ToolbarIconButton>
        ))}
      </div>
      <MonacoJsonEditor
        value={value}
        onChange={onChange}
        isActive
        editorRef={editorRef}
        className={editorClassName}
      />
    </div>
  );
}

export default JsonEditorPanel;
