import { MonacoJsonEditor } from "../../common/json-editor";

export default function AGENTJSONEditor(props) {
  return (
    <MonacoJsonEditor
      {...props}
      className="monaco-json-editor ai-agent-json-editor"
    />
  );
}
