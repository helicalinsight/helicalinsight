import { useState } from "react";
import { Row, Col } from "antd";
import Editor from "./Editor";
import classNames from "classnames";
import { InfoCircleFilled } from "@ant-design/icons";

const ReportEditor = (props) => {
  const { reportData, sql, setSql, js, setJs, visualization, setVisualization } = props;
  const [activeEditor, setActiveEditor] = useState("js");

  if (reportData.length === 0)
    return (
      <p className="create-message">
        <InfoCircleFilled /> &nbsp; Please Add reports to create report
      </p>
    );
  return (
    <Row>
      <Col
        span={8}
        className={classNames({
          "edit-language-title": true,
          "active-tab": activeEditor === "js",
        })}
        onClick={() => setActiveEditor("js")}
      >
        <span>Configuration</span>
      </Col>
      <Col
        span={8}
        className={classNames({
          "edit-language-title": true,
          "active-tab": activeEditor === "sql",
        })}
        onClick={() => setActiveEditor("sql")}
      >
        SQL
      </Col>
      <Col
        span={8}
        className={classNames({
          "edit-language-title": true,
          "active-tab": activeEditor === "visualization",
        })}
        onClick={() => setActiveEditor("visualization")}
      >
        Visualization
      </Col>
      <Col span={24}>
        {activeEditor === "js" ? (
          <Editor language="javascript" displayName="JS" value={js} setEditorState={setJs} />
        ) : activeEditor === "sql" ? (
          <Editor language="sql" displayName="SQL" value={sql} setEditorState={setSql} />
        ) : (
          <Editor
            language="xml"
            displayName="HTML"
            value={visualization}
            setEditorState={setVisualization}
          />
        )}
      </Col>
    </Row>
  );
};

export default ReportEditor;
