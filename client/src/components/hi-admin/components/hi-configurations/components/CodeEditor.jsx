import { useEffect, useRef, useState } from "react";
import { Button } from "antd";
import { SaveOutlined } from "@ant-design/icons";
import Editor from "@monaco-editor/react";

const DEFAULT_OPTIONS = {
  minimap: { enabled: false },
  scrollBeyondLastLine: false,
  automaticLayout: true,
  fontSize: 13,
  wordWrap: "on",
  wrappingStrategy: "advanced",
  scrollbar: {
    alwaysConsumeMouseWheel: false,
  },
  tabSize: 2,
  formatOnPaste: true,
  formatOnType: true,
};

/**
 * Monaco-based code editor used for XML / JSON / text configuration files.
 */
const CodeEditor = ({
  value = "",
  language = "plaintext",
  saving = false,
  onSave,
  height = "100%",
}) => {
  const [localValue, setLocalValue] = useState(value ?? "");
  const lastEmittedRef = useRef(value ?? "");

  useEffect(() => {
    if (value === lastEmittedRef.current) {
      return;
    }
    lastEmittedRef.current = value ?? "";
    setLocalValue(value ?? "");
  }, [value]);

  const handleChange = (nextValue) => {
    const normalized = nextValue ?? "";
    lastEmittedRef.current = normalized;
    setLocalValue(normalized);
  };

  return (
    <div className="hi-config-code-editor">
      <div className="hi-config-code-editor-body" style={{ height }}>
        <Editor
          key={language}
          height="100%"
          width="100%"
          language={language}
          value={localValue}
          onChange={handleChange}
          theme="vs-light"
          options={DEFAULT_OPTIONS}
        />
      </div>
      <div className="hi-config-editor-actions">
        <Button
          type="primary"
          icon={<SaveOutlined />}
          loading={saving}
          onClick={() => onSave?.(localValue)}
        >
          Save
        </Button>
      </div>
    </div>
  );
};

export default CodeEditor;
