import { useRef, useState, useEffect } from "react";
import { Checkbox, Button, Form, Radio, Progress, Row, Col, Tooltip, Space, Input } from "antd";
import { UploadOutlined, InfoCircleOutlined, FolderOpenOutlined, EditOutlined } from "@ant-design/icons";
import requests from "../../../../../base/requests";
import { useDispatch } from "react-redux";
import notify from "../../../../hi-notifications/notify";
import axios from "axios";
import { fileBrowserActions } from "../../../../../redux/actions";
import { HIFileBrowser } from "../../../../hi-fileBrowser/hi-fileBrowser";
import { setFileBrowserFolder } from "../../../../../redux/actions/datasource.actions";
let source;

const ImportFiles = () => {
  const dispatch = useDispatch();
  const Notify = notify(dispatch);
  const hiddenFileInput = useRef();
  const [browseButton,setBrowseButton] = useState(false);
  const [fileUploaded, setFileUploaded] = useState("");
  const [checkedList, setCheckedList] = useState([]);
  const [isUploading, setIsUploading] = useState(false);
  const [loading, setLoading] = useState(false);
  const [uploadedPercentage, setUploadedPercentage] = useState(0);
  const [value, setValue] = useState("");
  const [folderPath, setFolderPath] = useState("");
  const [key, setKey] = useState(null);

  useEffect(() => {
    setLoading(false);
  }, [fileUploaded]);

  const handleChange = (e) => {
    setValue(e.target.value);
  };

  const onUploadFile = (e) => {
    setFileUploaded(e.target.files[0]);
    handleImport({ upload: false, e });
  };

  const handleImport = ({ upload, e }) => {
    source = axios.CancelToken.source();
    if (upload && !fileUploaded) {
      return Notify.error({
        type: "Frontend",
        message: `Please choose .zip file`,
      });
    }

    if (upload) {
      setIsUploading(true);
    }

    const formData = new FormData();

    const data = {
      onConflict: upload ? value : "skip",
      upload,
      options: {
        share: checkedList.includes("share"),
        dataSource: checkedList.includes("dataSource"),
        schedules: checkedList.includes("schedules"),
      },
      destination: folderPath,
    };
    if(upload) {
      data.key = key;
    }
    formData.append("formData", JSON.stringify(data));
    if (e) {
      formData.append("file", e.target.files[0]);
    }

    if (!upload) {
      setLoading(true);
    }

    requests.admin(dispatch).postUploadFile(
      formData,
      {
        cancelToken: source.token,
        onUploadProgress: (progressEvent) => {
          const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);
          setUploadedPercentage(percentCompleted);
        },
      },
      (res) => {
        if (!upload) {
          setLoading(false);
          Object.entries(res.options).forEach(([key, value]) => {
            value && setCheckedList((prevList) => [...prevList, key]);
          });
          setKey(res.key);
        }
        if (upload) {
          Notify.success({
            type: "Network Call",
            message: `Imported ${fileUploaded.name} Successfully with ${res.insertCount} Inserts, ${res.skipCount} Skips and ${res.updateCount} Updates`,
          });
          setFileUploaded("");
          setCheckedList([]);
          setValue("");
          setIsUploading(false);
          setFolderPath("");
        }
      },
      (e) => {
        if (upload && e.message === "cancel") {
           Notify.success({
            type: "Network Call",
            message: "Request cancelled successfully",
          });
        }
        setLoading(false);
        setIsUploading(false);
      }
    );

    if (e) {
      e.target.value = "";
    }
  };

  const handleCheckedList = (list) => {
    setCheckedList(list);
  };

  return (
    <div className="import-container">
      <Form
      data-testid = "hi-admin-importfiles-form"
        labelCol={{
          span: 12,
        }}
        wrapperCol={{
          span: 14,
        }}
        layout="horizontal"
      >
        <Form.Item
          label={[
            <Tooltip title="Please export the files/folders using Filebrowser before Importing">
              <InfoCircleOutlined style={{ marginRight: "5px", fontSize: "14px" }} />
            </Tooltip>,
            "Choose .zip file",
          ]}
        >
          <input
            style={{ display: "none" }}
            type="file"
            onChange={onUploadFile}
            accept=".zip"
            ref={hiddenFileInput}
          // className="import-file"
          />
          <Space>
            <Button
            data-testid = "hi-admin-importfiles-btn"
              type="primary"
              loading={loading}
              onClick={() => {
                hiddenFileInput.current.click();
              }}
            >
              Select File
            </Button>
            {fileUploaded && <span>{fileUploaded.name}</span>}
          </Space>
        </Form.Item>
        <Form.Item label={["Options"]}>
          <Checkbox.Group data-testid = "hi-admin-importfiles-checkbox" value={checkedList} onChange={handleCheckedList}>
            <div>
              <Tooltip  title="Security information of users, roles, permissions and organizations">
                <Checkbox  value="share">Share Details</Checkbox>
              </Tooltip>
            </div>
            <div>
              <Tooltip title="Datasources related metadata">
                <Checkbox value="dataSource">Datasources</Checkbox>
              </Tooltip>
            </div>
            <div>
              <Tooltip title="Schedules of the reports">
                <Checkbox value="schedules">Schedules</Checkbox>
              </Tooltip>
            </div>
          </Checkbox.Group>
        </Form.Item>
        <Form.Item label={["On Conflict"]}>
          <Radio.Group data-testid = "hi-admin-importfiles-radiobtn" onChange={handleChange} value={value}>
            <Tooltip title="New files will not be created">
              <Radio value={"skip"}>Skip</Radio>
            </Tooltip>
            <Tooltip title="Updates the conflicted files">
              <Radio value={"update"}>Update</Radio>
            </Tooltip>
          </Radio.Group>
        </Form.Item>
        <Form.Item label={[
            <Tooltip title="Please choose the location where you want to import the zip file. If you don't choose any location, it will use the existing one from zip file.">
              <InfoCircleOutlined style={{ marginRight: "5px", fontSize: "14px" }} />
            </Tooltip>,
            "Location",
          ]}>
        <Row>
                <Col>
                  <Form.Item data-testid = "hi-admin-importfiles-folderPath">
                    <Input style={{width:"100%"}} disabled  value={folderPath} />
                  </Form.Item>
                </Col>
                  <Col>
                    <Form.Item>
                      <Button
                      style={{ marginLeft:"none"}}
                        type="primary"
                        onClick={() =>{ 
                          dispatch(fileBrowserActions.setShowFileBrowser(true))
                          setBrowseButton(true)
                        }
                        }
                        icon={<FolderOpenOutlined />}
                      >
                        Browse
                      </Button>
                    </Form.Item>
                  </Col>
              </Row>
        </Form.Item>
        <Form.Item label={["Upload File"]}>
          <Button
            type="primary"
            icon={<UploadOutlined />}
            onClick={() => handleImport({ upload: true, e: null })}
            loading={isUploading ? true : false}
          >
            Import
          </Button>
          {isUploading && (
            <Row>
              <Col span={8}>
                <Progress percent={uploadedPercentage} />
              </Col>
              <Col span={1}></Col>
              <Col span={4}>
                <span
                  className="clear-button"
                  onClick={() => source.cancel("cancel")}
                >
                  Cancel
                </span>
              </Col>
            </Row>
          )}
        </Form.Item>
      </Form>
        { browseButton ? 
          <HIFileBrowser
          contextMenuOptions={{
            append: false,
            options: browseButton ? [
              {
                name: "Use This Folder",
                types: ["folder"],
                icon: <EditOutlined />,
                callback: (rec) => {
                  dispatch(setFileBrowserFolder({ path: rec.path }));
                  setFolderPath(rec.path);
                  setBrowseButton(false);
                },
              },
            ] : [],
          }}
          onFbClose={() => { setBrowseButton(false)}}
        />   : null  }
    </div>
  );
};

export default ImportFiles;
