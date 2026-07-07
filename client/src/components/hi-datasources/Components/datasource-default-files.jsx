import { useEffect, useRef, useState } from "react";
import { Row, Col, Form, Tooltip, Input, Button, Space, Tabs } from "antd";
import { useDispatch, useSelector } from "react-redux";
import { LoadingOutlined, QuestionCircleOutlined } from "@ant-design/icons";
import { setButtonType } from "../../../redux/actions/datasource.actions";
import { decompile } from "../utils/decompile";
import { checkIfGroovyManaged } from "../utils/checkIfGroovyManaged";
import notify from "../../hi-notifications/notify";
import requests from "../../../base/requests";
import TooltipDrawer from "../utils/tooltipDrawer";
import { UnControlled as CodeMirror } from "react-codemirror2";
import { getDefaultCode } from "../utils/getSampleCode";
import { v4 as uuidv4 } from "uuid";
import { getSelectedDriverURL } from "../utils/datasource.utils";

require("codemirror/mode/groovy/groovy");


const { TabPane } = Tabs;
const acceptFileTypes = {
  flatfile: {
    "Flatfile" : ".zip,.csv,.json,.parquet,.xlsx",
    "Flatfile csv": ".zip,.csv",
    "Flatfile excel": ".zip,.xlsx",
    "Flatfile json": ".zip,.json",
    "Flatfile parquet": ".zip,.parquet",
  }
}

const DataSourceDefaultFiles = (props) => {
  const {
    editable,
    driverCategory,
    urlFields,
    setUrlFields,
    testConnClick,
    saveConnClick,
    checkIfGroovyPlain,
    advancedSettingsClick,
    CodeEditorTab,
    WrappedConfigEditor,
    setUploadFileName,
  } = props;
  const dispatch = useDispatch();

  const editData = useSelector((store) => store.datasource.editData);
  const clickedActiveDatabaseData = useSelector(
    (store) => store.datasource.clickedActiveDatabaseData
  );
  const selectedDriverInfo = useSelector(
    (store) => store.datasource.selectedDriverInfo
  );

  const dataSourceEditName =
    editData?.type === "sql.jdbc.groovy" ? editData.name : editData["@name"];
    const FileEditName = editable ? editData?.jdbcUrl?.split(':').pop() : "";
    const Notify = notify(dispatch);
    const UPLOAD_FILE_DATA = "file_data";

    const [isUploading, setIsUploading] = useState(false);
    const uploadFileDataRef = useRef(null);

    const isFlatFile = () => {
      return selectedDriverInfo?.databaseDialect === "flatfile"
    };

    const showFileDataOption = () => {
      return ![
        "Flatfile aws",
        "Flatfile azure blobstorage",
        "Flatfile cloudfare r2",
        "Flatfile GCS",
        "Flatfile Google spreadsheet",
      ].includes(selectedDriverInfo?.name);
    }

    const [currentTab, setCurrentTab] = useState(checkIfGroovyManaged(clickedActiveDatabaseData) ? "2" : "1");

    const isHiApi = () => {
      return selectedDriverInfo?.databaseDialect === "hihttp"
    }

    const handleUploadClick = (type) => {
      switch (type) {
        case UPLOAD_FILE_DATA:
          uploadFileDataRef.current.click();
          break;
        default:
          return;
      }
    };

    const handleUploadChange = (event, type) => {
      const fileUploaded = event.target.files[0];
      const formData = new FormData();

      if(type === UPLOAD_FILE_DATA){
        setUrlFields({
          uploadDataFile: fileUploaded?.name,
        });
      }

      Notify.warning({
        type: "Network Call",
        message: `Uploading File ${fileUploaded?.name}`,
      });

      formData.append("destination", "");
      formData.append("type", "flatfile");
      formData.append("dataFile", true);
      formData.append("driverType", "flatfile");
      formData.append("file", fileUploaded);

      setIsUploading((prev) => ({ ...prev, [type]: true }));

      requests.datasource(dispatch).postUploadFile(
        formData,
        {},
        (res) => {
          setUploadFileName(res.fileName);
          setIsUploading((prev) => ({ ...prev, [type]: false }));

          Notify.success({
            type: "Network Call",
            message: `Uploaded File : ${fileUploaded.name}`,
          });
        },
        (e) => {
          setIsUploading((prev) => ({ ...prev, [type]: false }));
          // Notify.error({
          //   type: "Network Call",
          //   ...e,
          // });
        }
      );
    };  

  useEffect(() => {
    const selectedDriverURL = getSelectedDriverURL(selectedDriverInfo);
    if (selectedDriverURL) {
      if (editable && selectedDriverURL && editData.jdbcUrl) {
        const data = decompile(selectedDriverURL, editData.jdbcUrl);
        setUrlFields(data);
      } else if (
        editable &&
        editData?.type === "sql.jdbc.groovy" &&
        editData?.data?.url
      ) {
        const data = decompile(selectedDriverURL, editData.data.url);
        setUrlFields(data);
      } else if (editable && editData?.data?.jdbcUrl) {
        const data = decompile(selectedDriverURL, editData.data.jdbcUrl);
        setUrlFields(data);
      }
    }
  }, [selectedDriverInfo, editData]);

  if (driverCategory === "advanced" || driverCategory === "Flat Files")
    return null;

  const getIntitialValue = ({ editable, editData, key = "username" }) => {
    if (!editable) return "";
    let result = checkIfGroovyPlain(editData)
      ? editData?.data
        ? key === "username"
          ? editData.data.userName || editData.data.username
          : editData.data.password
        : ""
      : editData[key === "username" ? "userName" : key];
    return result;
    return checkIfGroovyPlain(editData)
      ? key === "username" && editData.data[key]
        ? editData.data[key]
        : editData.data["userName"]
      : editData[key === "username" ? "userName" : key];
  };

  let shouldRenderFields = editable ? Object.values(editData).length : true; // this is a flag for :: if it is for edit then we should render if edit data is fetched - in this case we are rendering input fields

  return (
    <>
      <Row gutter={16}>
        {selectedDriverInfo.parameters &&
          selectedDriverInfo.parameters.hostName && (
            <Col span={12}>
              <Form.Item
                data-testid="datasource-host-name"
                name="hostName"
                htmlFor={null}
                label={[
                  "Host",
                  <Tooltip title="Please enter the server detail/IP/url where your data resides. Eg: 127.0.0.1 or https://dummyjson.com/products">
                    <QuestionCircleOutlined
                      style={{ marginLeft: "5px", fontSize: "10px" }}
                    />
                  </Tooltip>,
                ]}
                rules={[
                  {
                    required: true,
                    message: "Please enter Host Name",
                  },
                ]}
              >
                <Input id= {uuidv4()} placeholder="Please enter Host Name" />
              </Form.Item>
            </Col>
          )}

        {selectedDriverInfo.parameters && selectedDriverInfo.parameters.port && (
          <Col span={12}>
            <Form.Item
              data-testid="datasource-port"
              name="port"
              htmlFor={null}
              label={[
                "Port",
                <Tooltip title="Please enter the port number to connect your database server">
                  <QuestionCircleOutlined
                    style={{ marginLeft: "5px", fontSize: "10px" }}
                  />
                </Tooltip>,
              ]}
              rules={[
                {
                  required: true,
                  message: "Please enter Port Number",
                },
              ]}
            >
              <Input id= {uuidv4()} placeholder="Please enter Port" />
            </Form.Item>
          </Col>
        )}
      </Row>

      <Row gutter={16}>
        {!checkIfGroovyManaged(clickedActiveDatabaseData) && getSelectedDriverURL(selectedDriverInfo)  === undefined && (
          <Col span={12}>
            <Form.Item
              name="inputUrl"
              label="URL"
              initialValue={editable ? editData.jdbcUrl : ""}
            >
              <Input placeholder="Please enter Url" />
            </Form.Item>
          </Col>
        )}

        {selectedDriverInfo.parameters !== undefined &&
          selectedDriverInfo.parameters.database !== undefined && (
            <Col span={12}>
              <Form.Item
                data-testid="datasource-database-name"
                name="databaseName"
                htmlFor={null}
                onKeyDown={(e) => (e.keyCode === 13 ? e.preventDefault() : "")}
                label={[
                  "Database Name",
                  <Tooltip title="Please enter the name of your database For Ex: path/service id/service name.">
                    <QuestionCircleOutlined
                      style={{ marginLeft: "5px", fontSize: "10px" }}
                    />
                  </Tooltip>,
                ]}
                rules={[
                  {
                    required: true,
                    message: "Please enter database name",
                  },
                ]}
              >
                <Input id= {uuidv4()} placeholder="Please enter Database name" />
              </Form.Item>
            </Col>
          )}

        {!checkIfGroovyManaged(clickedActiveDatabaseData) && shouldRenderFields && (
          <Col span={12}>
            <Form.Item
              data-testid="datasource-datasource-name"
              name="datasourceName"
              htmlFor={null}
              onKeyDown={(e) => (e.keyCode === 13 ? e.preventDefault() : "")}
              label={[
                "Datasource Name",
                <Tooltip title="Please enter a name of your choice to save the datasource.">
                  <QuestionCircleOutlined
                    style={{ marginLeft: "5px", fontSize: "10px" }}
                  />
                </Tooltip>,
              ]}
              rules={[
                { required: true, message: "Please enter datasource Name" },
              ]}
              initialValue={editable ? dataSourceEditName : ""}
            >
              <Input id= {uuidv4()} placeholder="Please enter datasource Name" />
            </Form.Item>
          </Col>
        )}
      </Row>

      {!checkIfGroovyManaged(clickedActiveDatabaseData) &&
        shouldRenderFields && (
          <Row
            gutter={16}
            style={
              !advancedSettingsClick && isFlatFile()
                ? {
                    position: "fixed",
                    visibility: "hidden",
                  }
                : {}
            }
          >
            <Col span={12}>
              <Form.Item
                name="username"
                htmlFor={null}
                onKeyDown={(e) => (e.keyCode === 13 ? e.preventDefault() : "")}
                label={[
                  "User Name",
                  <Tooltip title="Please enter username credential if required to connect to your database server">
                    <QuestionCircleOutlined
                      style={{ marginLeft: "5px", fontSize: "10px" }}
                    />
                  </Tooltip>,
                ]}
                initialValue={getIntitialValue({
                  editable,
                  editData,
                  key: "username",
                })}
              >
                <Input id= {uuidv4()} placeholder="Please enter User Name" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="password"
                htmlFor={null}
                onKeyDown={(e) => (e.keyCode === 13 ? e.preventDefault() : "")}
                label={[
                  "Password",
                  <Tooltip title="Please enter password credential if required to connect to your database server">
                    <QuestionCircleOutlined
                      style={{ marginLeft: "5px", fontSize: "10px" }}
                    />
                  </Tooltip>,
                ]}
                // initialValue={editable ? editData.password : ""}
                initialValue={getIntitialValue({
                  editable,
                  editData,
                  key: "password",
                })}
              >
                <Input id= {uuidv4()} type="password" placeholder="Please enter Password" />
              </Form.Item>
            </Col>
          </Row>
        )}
      {isFlatFile() && showFileDataOption() && (
        <Col span={12} style={{ display: "flex", alignItems: "flex-end" }}>
          <input
            type="file"
            ref={uploadFileDataRef}
            accept={
              acceptFileTypes[selectedDriverInfo.databaseDialect][
                selectedDriverInfo.name
              ]
            }
            onChange={(e) => handleUploadChange(e, UPLOAD_FILE_DATA)}
            style={{ display: "none" }}
          />
          <Form.Item
            label={[
              "File Data",
              <Tooltip title="Please upload your data file here">
                <QuestionCircleOutlined
                  style={{ marginLeft: "5px", fontSize: "10px" }}
                />
              </Tooltip>,
            ]}
            style={{ flex: "1" }}
          >
            <Input
              value={uploadFileDataRef?.current?.files[0]?.name || FileEditName}
              disabled={true}
            />
          </Form.Item>
          <Form.Item>
            <Button
              type="primary"
              onClick={() => handleUploadClick(UPLOAD_FILE_DATA)}
              icon={isUploading[UPLOAD_FILE_DATA] ? <LoadingOutlined /> : ""}
              disabled={isUploading[UPLOAD_FILE_DATA]}
            >
              Upload
            </Button>
          </Form.Item>
        </Col>
      )}

      {isHiApi() && (
        <Col span={24}>
          <Form.Item>
            <CodeEditorTab
            activeTab={currentTab}
            onTabChange={(key) => setCurrentTab(key)}
              // editorRefValue={configEditorRef?.current?.editor?.getValue()}
              // editorRefValueGroovy={groovyEditorRef?.current?.editor?.getValue()}
            />
          </Form.Item>
        </Col>
      )}

      {isFlatFile() && (
        <Col span={24}>
          <Form.Item label={["Configuration Editor", <TooltipDrawer />]}>
            <WrappedConfigEditor
              // editorRefValue={configEditorRef?.current?.editor?.getValue()}
            />
          </Form.Item>
        </Col>
      )}

      {clickedActiveDatabaseData.classifier !== "efwd" && (
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
                      setButtonType({ type: "test", datasourceType: "default" })
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
                      setButtonType({ type: "save", datasourceType: "default" })
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

export default DataSourceDefaultFiles;
