import { Row, Col, Form, Tooltip, Input, Space, Button } from "antd";
import { fileBrowserActions } from "../../../redux/actions";
import { HIFileBrowser } from "../..";
import { useSelector, useDispatch } from "react-redux";
import {
  FolderOpenOutlined,
  EditOutlined,
  QuestionCircleOutlined,
} from "@ant-design/icons";
import {
  setButtonType,
  setFileBrowserFolder,
} from "../../../redux/actions/datasource.actions";
import { useEffect, useRef } from "react";
import { UnControlled as CodeMirror } from "react-codemirror2";
import { getSampleCode } from "../utils/getSampleCode";
import { v4 as uuidv4 } from "uuid";
require("codemirror/mode/groovy/groovy");

const DataSourceAdvancedFiles = (props) => {
  const {
    editable,
    activeKey,
    testConnClick,
    saveConnClick,
    editorInput = null,
    setEditorInput = false,
    checkIfGroovyPlain = () => { },
    checkIfGroovyManaged = () => { },
  } = props;
  const dispatch = useDispatch();
  const clickedActiveDatabaseData = useSelector(
    (store) => store.datasource.clickedActiveDatabaseData
  );
  const selectedDriverInfo = useSelector(
    (store) => store.datasource.selectedDriverInfo
  );
  const fileBrowserFolder = useSelector(
    (store) => store.datasource.fileBrowserFolder
  );
  // const clickedRecordData = useSelector(
  //   (store) => store.datasource.clickedRecordData
  // );
  const editData = useSelector(
    (store) => store.datasource.editData
  );
  const editorRef = useRef(null);

  useEffect(() => {
    // bug 5722 fix - start
    if (editable && editData?.data?.dir) {
      dispatch(
        setFileBrowserFolder({
          path: editData.data.dir,
        })
      );
    } else {
      dispatch(setFileBrowserFolder({}));
    }
    // bug 5722 fix - end
    typeof editorRef?.current?.editor?.setValue === "function" &&
      editorRef.current.editor.setValue(((activeKey === '1' && !editable) ? createdefaultCode() : editorInput) || "");
  }, [editable, activeKey]);


  useEffect(() => {
    if (editorInput === null) {
      let result = getSampleCode(
        clickedActiveDatabaseData,
        checkIfGroovyManaged
      );
      typeof editorRef?.current?.editor?.setValue === "function" &&
        editorRef.current.editor.setValue(result || "");
    }
  }, [editorInput]);

  const createdefaultCode = () => {
    let result = getSampleCode(
      clickedActiveDatabaseData,
      checkIfGroovyManaged
    );
    setEditorInput(result);
    typeof editorRef?.current?.editor?.setValue === "function" &&
      editorRef.current.editor.setValue(result || "");
    return result;
  }

  const getDefaultInput = () => {
    if (editorInput === null) { //  || (activeKey === '1' && !editable)
      return createdefaultCode();
    }
    return editorInput;
  };

  const renderEditorForGroovy = () => {
    return (
      <Form.Item label={["Groovy"]}>
        <CodeMirror
          ref={editorRef}
          options={{
            lineWrapping: true,
            lint: true,
            mode: "groovy",
            lineNumbers: true,
            theme: "dracula",
            autoCloseTags: true,
            autoCloseBrackets: true,
          }}
          defaultValue={getDefaultInput()}
          onChange={(editor, data, value) => {
            setEditorInput(value);
          }}
        />
      </Form.Item>
    );
  };

  const isGroovy =
    checkIfGroovyManaged(clickedActiveDatabaseData) ||
    checkIfGroovyPlain(clickedActiveDatabaseData);

  return (
    <>
      <Row gutter={16}>
        {(checkIfGroovyManaged(clickedActiveDatabaseData) || !editable) &&
          !checkIfGroovyPlain(clickedActiveDatabaseData) &&
          clickedActiveDatabaseData?.name &&
          clickedActiveDatabaseData?.name !== "Plain Jdbc DataSource" &&
          clickedActiveDatabaseData?.name !== "Managed DataSource" && (
            <Col span={12}>
              <Form.Item
                {...(checkIfGroovyManaged(clickedActiveDatabaseData)
                  ? { name: "datasourceName" }
                  : {})}
                  htmlFor={null}
                label={[
                  "Datasource Name",
                  <Tooltip title="Please enter a name of your choice to save the datasource">
                    &nbsp;
                    <QuestionCircleOutlined
                      style={{ marginLeft: "5px", fontSize: "10px" }}
                    />
                  </Tooltip>,
                ]}
                rules={[
                  { required: true, message: "Please enter datasource Name" },
                ]}
                data-testid="hi-advanced-tab-datasource-name"
              >
                <Input id= {uuidv4()} placeholder="Please enter DataSource Name" />
              </Form.Item>
            </Col>
          )}
        {clickedActiveDatabaseData.name !== "Managed DataSource" && (
          <>
            <Col
              span={checkIfGroovyManaged(clickedActiveDatabaseData) ? 12 : 13}
            >
              <Row>
                <Col span={editable ? 24 : 15}>
                  <Form.Item label="Location">
                    <Input disabled value={fileBrowserFolder.path} />
                  </Form.Item>
                </Col>
                {!editable && (
                  <Col span={4}>
                    <Form.Item label=" ">
                      <Button
                        type="primary"
                        onClick={() =>
                          dispatch(fileBrowserActions.setShowFileBrowser(true))
                        }
                        icon={<FolderOpenOutlined />}
                      >
                        Browse
                      </Button>
                    </Form.Item>
                  </Col>
                )}
              </Row>
            </Col>
            <Col span={24}>{!!isGroovy && renderEditorForGroovy()}</Col>
          </>
        )}
      </Row>
      {clickedActiveDatabaseData.classifier === "efwd" && (
        <Row gutter={16}>
          <Col span={24}>
            <Space>
              <Form.Item>
                <Button
                  loading={testConnClick}
                  type="primary"
                  htmlType="submit"
                  onClick={() =>
                    dispatch(
                      setButtonType({ type: "test", datasourceType: "efwd" })
                    )
                  }
                >
                  Test Connection
                </Button>
              </Form.Item>
              <Form.Item>
                <Button
                  loading={saveConnClick}
                  type="primary"
                  htmlType="submit"
                  onClick={() =>
                    dispatch(
                      setButtonType({ type: "save", datasourceType: "efwd" })
                    )
                  }
                >
                  {editable ? "Update Datasource" : "Save Datasource"}
                </Button>
              </Form.Item>
            </Space>
          </Col>
        </Row>
      )}
      <Row>
        <HIFileBrowser
          contextMenuOptions={{
            append: false,
            options: [
              {
                name: "Use This Folder",
                types: ["folder"],
                icon: <EditOutlined />,
                callback: (rec) => {
                  dispatch(setFileBrowserFolder({ path: rec.path }));
                },
              },
            ],
          }}
        />
      </Row>
    </>
  );
};

export default DataSourceAdvancedFiles;
