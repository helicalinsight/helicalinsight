import { useSelector, useDispatch } from "react-redux";
import { Row, Col, Form, Tooltip, Input, Button, Space } from "antd";
import { QuestionCircleOutlined } from "@ant-design/icons";
import { setButtonType } from "../../../redux/actions/datasource.actions";
import { v4 as uuidv4 } from "uuid";

const DataSourceJNDIFiles = (props) => {
  const { editable } = props;
  const dispatch = useDispatch();
  const editData = useSelector((store) => store.datasource.editData);
  const dataSourceEditName = editData["@name"];

  return (
    <>
      <Row data-testid = "hi-datasource-jndi-files" gutter={16}>
        <Col span={12}>
          <Form.Item
            name="name"
            initialValue={editable ? dataSourceEditName : ""}
            htmlFor={null}
            label={[
              "Datasource Name",
              <Tooltip title="Please enter a name of your choice to save the datasource">
                <QuestionCircleOutlined style={{ marginLeft: "5px", fontSize: "10px" }} />
              </Tooltip>,
            ]}
            rules={[{ required: true, message: "Please enter dataSource Name" }]}
          >
            <Input id= {uuidv4()} placeholder="Please enter DataSource Name" />
          </Form.Item>
        </Col>
        <Col span={12}>
          <Form.Item
            name="lookUpName"
            label="JNDI Name"
            initialValue={editable ? editData.lookUpName : ""}
            rules={[{ required: true, message: "Please enter JNDI Name" }]}
          >
            <Input />
          </Form.Item>
        </Col>
      </Row>
      <Row gutter={16}>
        <Col span={24}>
          <Space>
            <Form.Item>
              <Button
                type="primary"
                htmlType="submit"
                onClick={() => dispatch(setButtonType({ type: "test", datasourceType: "jndi" }))}
              >
                Test Connection
              </Button>
            </Form.Item>
            <Form.Item>
              <Button
                type="primary"
                htmlType="submit"
                onClick={() => dispatch(setButtonType({ type: "save", datasourceType: "jndi" }))}
              >
                {editable ? "Update Datasource" : "Save Datasource"}
              </Button>
            </Form.Item>
          </Space>
        </Col>
      </Row>
    </>
  );
};

export default DataSourceJNDIFiles;
