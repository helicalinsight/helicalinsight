import { Row, Col } from "antd";
import Editor from "./Editor";
import { InfoCircleFilled } from "@ant-design/icons";

const DataSourceEditor = (props) => {
  const { dataSourceData, html, setHtml } = props;
  if (dataSourceData.length === 0)
    return (
      <p className="create-message">
        {" "}
        <InfoCircleFilled /> &nbsp; Please Add datasource to create report
      </p>
    );
  return (
    <Row>
      <Col span={24} className="edit-language-title">
        Configure
      </Col>
      <Col span={24}>
        <Editor language="xml" displayName="HTML" value={html} setEditorState={setHtml} />
      </Col>
    </Row>
  );
};

export default DataSourceEditor;
