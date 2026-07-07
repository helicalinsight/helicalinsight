import React, { useEffect, useRef, useState } from "react";
import { Button } from "antd";
import Editor from "@monaco-editor/react";
import { EditorLoadingView } from "../editor-loading-view";
import "./json-editor.scss";

export function PasteJsonModal({
  open,
  value,
  onChange,
  onClose,
  onLoad,
  placeholder = "Paste a metadata JSON here...",
}) {
  if (!open) {
    return null;
  }

  return (
    <div
      className="me-modal open"
      onClick={(e) => e.target === e.currentTarget && onClose()}
    >
      <div className="me-modal-card">
        <div className="me-modal-head">
          <h3>Paste JSON</h3>
          <button type="button" className="me-modal-close" onClick={onClose}>
            &times;
          </button>
        </div>
        <textarea
          id="me-paste-area"
          value={value}
          placeholder={placeholder}
          onChange={(e) => onChange(e.target.value)}
        />
        <div className="me-modal-foot">
          <Button onClick={onClose}>Cancel</Button>
          <Button type="primary" onClick={onLoad}>
            Load
          </Button>
        </div>
      </div>
    </div>
  );
}

export function MonacoJsonEditor({
  value = "",
  onChange,
  isLoading = false,
  handleAbort,
  isActive = true,
  className = "monaco-json-editor",
}) {
  const [localValue, setLocalValue] = useState(value);
  const isFocusedRef = useRef(false);

  useEffect(() => {
    if (!isFocusedRef.current) {
      setLocalValue(value ?? "");
    }
  }, [value]);

  const handleEditorChange = (nextValue) => {
    const normalized = nextValue ?? "";
    setLocalValue(normalized);
    onChange?.(normalized);
  };

  if (isLoading) {
    return (
      <EditorLoadingView handleAbort={handleAbort} className={className} />
    );
  }

  return (
    <div className={className} style={{ height: "100%", width: "100%" }}>
      <Editor
        height="100%"
        width="100%"
        language="json"
        value={localValue}
        onChange={handleEditorChange}
        onMount={(editor) => {
          editor.onDidFocusEditorText(() => {
            isFocusedRef.current = true;
          });
          editor.onDidBlurEditorText(() => {
            isFocusedRef.current = false;
          });
        }}
        theme="vs-light"
        options={{
          minimap: { enabled: true },
          scrollBeyondLastLine: false,
          automaticLayout: true,
          fontSize: 14,
          wordWrap: "on",
          tabSize: 2,
          formatOnPaste: true,
          formatOnType: true,
          readOnly: !isActive,
        }}
      />
    </div>
  );
}

export function JsonEditorShell({
  className = "",
  isRawJsonView = false,
  jsonText = "",
  onJsonChange,
  isJsonActive = true,
  pasteOpen = false,
  pasteText = "",
  onPasteTextChange,
  onPasteClose,
  onPasteLoad,
  pastePlaceholder = "Paste a metadata JSON here...",
  children,
}) {
  return (
    <div
      className={className}
      style={{ height: "100%", width: "100%", position: "relative" }}
    >
      <div
        style={{
          height: "100%",
          width: "100%",
          display: isRawJsonView ? "none" : "block",
        }}
      >
        {children}
      </div>
      {isRawJsonView && (
        <div style={{ height: "100%", width: "100%" }}>
          <MonacoJsonEditor
            value={jsonText}
            onChange={onJsonChange}
            isActive={isJsonActive}
          />
        </div>
      )}
      <PasteJsonModal
        open={pasteOpen}
        value={pasteText}
        placeholder={pastePlaceholder}
        onChange={onPasteTextChange}
        onClose={onPasteClose}
        onLoad={onPasteLoad}
      />
    </div>
  );
}
