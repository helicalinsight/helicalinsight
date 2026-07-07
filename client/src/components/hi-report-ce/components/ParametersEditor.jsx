import { useState } from "react";
import { Row, Col } from "antd";
import Editor from "./Editor";
import classNames from "classnames";
import { InfoCircleFilled } from "@ant-design/icons";

const ParametersEditor = (props) => {
  const { parametersData, sql, setSql, js, setJs } = props;
  const [activeEditor, setActiveEditor] = useState("js");

  if (parametersData.length === 0)
    return (
      <p className="create-message">
        <InfoCircleFilled /> &nbsp; Please Add parameters to create report
      </p>
    );
  return (
    <Row>
      <Col
        className={classNames({
          "edit-language-title": true,
          "active-tab": activeEditor === "js",
        })}
        span={12}
        onClick={() => setActiveEditor("js")}
      >
        <span>Configuration</span>
      </Col>
      <Col
        span={12}
        className={classNames({
          "edit-language-title": true,
          "active-tab": activeEditor === "sql",
        })}
        onClick={() => setActiveEditor("sql")}
      >
        SQL
      </Col>
      <Col span={24}>
        {activeEditor === "js" ? (
          <Editor language="javascript" displayName="JS" value={js} setEditorState={setJs} />
        ) : (
          <Editor language="sql" displayName="SQL" value={sql} setEditorState={setSql} />
        )}
      </Col>
    </Row>
  );
};

export default ParametersEditor;
