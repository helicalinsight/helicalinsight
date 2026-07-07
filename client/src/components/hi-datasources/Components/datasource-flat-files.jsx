import { useState, useEffect, useRef } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Row, Col, Form, Tooltip, Input, Button, Space, Progress } from "antd";
import { PlusOutlined, QuestionCircleOutlined } from "@ant-design/icons";
import {
  setFlatFilesUploadName,
  setButtonType,
} from "../../../redux/actions/datasource.actions";
import requests from "../../../base/requests";
import axios from "axios";
import notify from "../../hi-notifications/notify";
import { v4 as uuidv4 } from "uuid";

const DataSourceFlatFiles = (props) => {
  const { editable, driverCategory, testConnClick, saveConnClick } = props;
  const dispatch = useDispatch();
  const editData = useSelector((store) => store.datasource.editData);
  const clickedActiveDatabaseData = useSelector(
    (store) => store.datasource.clickedActiveDatabaseData
  );
  const flatFileUploadName = useSelector(
    (store) => store.datasource.flatFileUploadName
  );
  const [progress, setProgress] = useState(0);
  const [isUploading, setIsUploading] = useState(false);

  const hiddenFileInput = useRef(null);
  const apiRef = useRef(null);
  
  function handleAbort(prop = {}) {
    apiRef.current?.abort(prop);
  }

  const Notify = notify(dispatch);

  useEffect(() => {
    if (editable) {
      dispatch(setFlatFilesUploadName(editData.jdbcUrl));
    } else {
      dispatch(setFlatFilesUploadName(""));
    }
    return () => {
      dispatch(setFlatFilesUploadName(""));
    };
  }, [editable,editData]);

  const dataSourceEditName = editData["@name"];

  const handleClick = () => {
    hiddenFileInput.current.click();
  };

  const handleChange = (event) => {
    const fileUploaded = event.target.files[0];
    const formData = new FormData();

    Notify.warning({
      type: "Network Call",
      message: `Uploading File ${fileUploaded.name}`,
    });

    formData.append("destination", "");
    formData.append("type", "csv");
    formData.append("file", fileUploaded);

    apiRef.current = requests.datasource(dispatch).postUploadFile(
      formData,
      {
        onUploadProgress: (progressEvent) => {
          const percentCompleted = Math.round(
            (progressEvent.loaded * 100) / progressEvent.total
          );
          setIsUploading(true);
          setProgress(percentCompleted);
        },
      },
      (res) => {
        setIsUploading(false);
        dispatch(setFlatFilesUploadName(res.fileName));

        // Notify.success({
        //   type: "Network Call",
        //   message: `Upload of ${fileUploaded.name} is successful`,
        // });
        Notify.success({
          type: "Network Call",
          message: `Uploaded File : ${fileUploaded.name}`,
        });
      },
      (e) => {
        setIsUploading(false);
        // Notify.error({
        //   type: "Network Call",
        //   ...e,
        // });
      }
    );
    event.target.value = "";
  };

  const handleClearUpload = () => {
    dispatch(setFlatFilesUploadName(""));
  };

  if (driverCategory !== "Flat Files") return null;
  return (
    <>
      <Row data-testid = "hi-datasource-flat-files" gutter={16}>
        <Col span={12}>
          <Row>
            <Col span={15}>
              <Form.Item
                label={[
                  "URL",
                  <Tooltip title="Please provide the URL connection details to the database">
                    <QuestionCircleOutlined
                      style={{ marginLeft: "5px", fontSize: "10px" }}
                    />
                  </Tooltip>,
                ]}
              >
                <Input value={flatFileUploadName} disabled />
                {flatFileUploadName !== "" && (
                  <p className="clear-button" onClick={handleClearUpload}>
                    Clear
                  </p>
                )}
              </Form.Item>

              <input
                type="file"
                ref={hiddenFileInput}
                onChange={handleChange}
                style={{ display: "none" }}
              />
            </Col>
            <Col span={4}>
              <Form.Item label=" ">
                <Button
                  type="primary"
                  onClick={handleClick}
                  icon={<PlusOutlined />}
                >
                  Upload File
                </Button>
              </Form.Item>
            </Col>
            <Col span={24}>
              {isUploading && (
                <Row justify="space-between" middle="middle">
                  <Col span={18}>
                    <Progress percent={progress} />
                  </Col>
                  <Col span={4}>
                    <span
                      className="clear-button"
                      onClick={() => handleAbort()}
                    >                 
                      Cancel
                    </span>
                  </Col>
                </Row>
              )}
            </Col>
          </Row>
        </Col>

        <Col span={12}>
          <Form.Item
            name="dataName"
            htmlFor={null}
            label={[
              "Datasource Name",
              <Tooltip title="Please enter a name of your choice to save the datasource">
                <QuestionCircleOutlined
                  style={{ marginLeft: "5px", fontSize: "10px" }}
                />
              </Tooltip>,
            ]}
            rules={[
              { required: true, message: "Please enter datasource name" },
            ]}
            initialValue={editable ? dataSourceEditName : ""}
          >
            <Input id= {uuidv4()} placeholder="Please enter datasource name" />
          </Form.Item>
        </Col>
      </Row>

      {clickedActiveDatabaseData.classifier !== "efwd" && (
        <Row>
          <Col span={24}>
            <Space>
              <Form.Item>
                <Button
                  loading={testConnClick}
                  type="primary"
                  htmlType="submit"
                  onClick={() =>
                    dispatch(
                      setButtonType({
                        type: "test",
                        datasourceType: "flatfiles",
                      })
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
                      setButtonType({
                        type: "save",
                        datasourceType: "flatfiles",
                      })
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
    </>
  );
};

export default DataSourceFlatFiles;
