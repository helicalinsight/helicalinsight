import requests from "../../../base/requests";
import notify from "../../hi-notifications/notify";
import DataSourceFlatFiles from "./datasource-flat-files";
import DataSourceAdvancedFiles from "./datasource-advanced-files";
import DataSourceDefaultFiles from "./datasource-default-files";
import DataSourceJNDIFiles from "./datasource-jndi-files";
import { useState, useEffect, useRef } from "react";
import { useDispatch, useSelector } from "react-redux";
import { v4 as uuidv4 } from "uuid";
import { Button, Input, Form, Row, Col, Select, Tooltip, Skeleton, Tabs, Modal } from "antd";
import { EditOutlined, QuestionCircleOutlined } from "@ant-design/icons";
import {
  setConnectedDataSourceData,
  setDataSourceTestConnection,
  setDataSourceConnection,
  setSelectedDriverInfo,
  setViewData,
} from "../../../redux/actions/datasource.actions";
import { getCreateEditFD } from "../utils/getCreateEditFD";
import { checkIfGroovyManaged } from "../utils/checkIfGroovyManaged";
import { checkIfGroovyPlain } from "../utils/checkIfGroovyPlain.js";
import { getJdbcUrlSubString, getSelectedDriverURL } from "../utils/datasource.utils";
import { UnControlled as CodeMirror } from "react-codemirror2";
import { getDefaultCode } from "../utils/getSampleCode.js";
import TooltipDrawer from "../utils/tooltipDrawer.jsx";
import { cloneDeep, isArray } from "lodash";

const { Option } = Select;
const UPLOAD_FILE_CONFIG = 'file_config';
const UPLOAD_FILE_DATA = 'file_data';
const { TabPane } = Tabs;
const extractConfigFilePath = (url) => {
  if (!url) return url;

  return new URL(url).searchParams.get("configPath");
}

const DataSourceCreateAndEdit = (props) => {
  const { editable, activeKey } = props;

  const [url, setUrl] = useState("");
  const [driver, setDriver] = useState("");
  const [driverCategory, setDriverCategory] = useState("");
  const [advancedSettingsClick, setAdvancedSettingsClick] = useState(false);
  // const [otherOptions, setOtherOptions] = useState("");
  const [urlFields, setUrlFields] = useState({});
  const [testConnClick, setTestConnClick] = useState(false);
  const [saveConnClick, setSaveConnClick] = useState(false);
  const [selectedURLIndex, setSelectedURLIndex] = useState(0);

  const [form] = Form.useForm();
  const dataSources = useSelector((store) => store.datasource.dataSources);
  const editData = useSelector((store) => store.datasource.editData);
  const viewData = useSelector((store) => store.datasource.viewData);
  const buttonTypes = useSelector((store) => store.datasource.buttonTypes);
  const selectedDriverInfo = useSelector(
    (store) => store.datasource.selectedDriverInfo
  );
  const clickedDataSource = useSelector(
    (store) => store.datasource.clickedDataSource
  );
  const { url: clickedDSUrls = [] } = clickedDataSource || {}

  const [dataSourceProvider, setDataSourceProvider] = useState("");
  const clickedActiveDatabaseData = useSelector(
    (store) => store.datasource.clickedActiveDatabaseData
  );
  const driversList = useSelector(
    (store) => store.datasource.dataSourceDriversList
  );
  const flatFileUploadName = useSelector(
    (store) => store.datasource.flatFileUploadName
  );
  const fileBrowserFolder = useSelector(
    (store) => store.datasource.fileBrowserFolder
  );
  const isSericeCall = useSelector((store) => store.datasource.isSericeCall);
  const [editorInput, setEditorInput] = useState(
    editData?.data?.condition ? editData.data.condition : null
  );

  const [configEditorInput, setConfigEditorInput] = useState();
  const [groovyEditorInput, setGroovyEditorInput] = useState();

  const dsMode = useSelector((store) => store.datasource.dsMode || {});

  const [isUploading, setIsUploading] = useState({
    [UPLOAD_FILE_CONFIG]: false,
    [UPLOAD_FILE_DATA]: false
  });
  const [activeViewInfo, setActiveViewInfo] = useState({})
  const [uploadFileName, setUploadFileName] = useState(editData?.extraOptions?.dataFile || "");
  const [fileUploaded, setFileUploaded] = useState(false);

  const datasourceName = editData?.type === "sql.jdbc.groovy" ? editData.name : editData["@name"];
  const otherOptionsRef = useRef(null);

  const uploadFileConfigRef = useRef(null);
  const uploadFileDataRef = useRef(null);

  const clickedRecordData = useSelector(
    (store) => store.datasource.clickedRecordData
  );

  const isFlatFile = () => {
    return selectedDriverInfo?.databaseDialect === "flatfile"
  }

  const isHiApi = () => {
    return selectedDriverInfo?.databaseDialect === "hihttp"
  }

  const handleUploadClick = (type) => {
    switch (type) {
      case UPLOAD_FILE_CONFIG:
        uploadFileConfigRef.current.click();
        break;
      case UPLOAD_FILE_DATA:
        uploadFileDataRef.current.click();
        break;
      default:
        return;
    }
  }

  const handleUploadChange = (event, type) => {
    const fileUploaded = event.target.files[0];
    const formData = new FormData();

    Notify.warning({
      type: "Network Call",
      message: `Uploading File ${fileUploaded.name}`,
    });

    formData.append("destination", "");
    formData.append("type", "flatfile");
    formData.append("dataFile", type !== UPLOAD_FILE_CONFIG);
    formData.append("driverType", isFlatFile() ? "flatfile" : "http");
    formData.append("file", fileUploaded);

    setUploadFileName(fileUploaded.name);
    setIsUploading(prev => ({ ...prev, [type]: true }));

    requests.datasource(dispatch).postUploadFile(
      formData,
      {},
      (res) => {
        setIsUploading(prev => ({ ...prev, [type]: false }));

        Notify.success({
          type: "Network Call",
          message: `Uploaded File : ${fileUploaded.name}`,
        });
      },
      (e) => {
        setIsUploading(prev => ({ ...prev, [type]: false }));
        // Notify.error({
        //   type: "Network Call",
        //   ...e,
        // });
      }
    );
  }

  const configEditorRef = useRef(null);
  const groovyEditorRef = useRef(null);
  const configEditorValueRef = useRef(null);
  const groovyEditorValueRef = useRef(null);

  const [groovyEdited, setGroovyEdited] = useState(false);
  const [configEdited, setConfigEdited] = useState(false);

  const ConfigurationEditor = ({ editorRefValue, urlFields, configEditorValueRef }) => {
    const [configEditorInput, setConfigEditorInput] = useState(null);
    const firstLoad = useRef(true);

    useEffect(() => {
      if (firstLoad.current === true && !configEditorInput?.length) {
        firstLoad.current = false;
        let result;
        if (editable && editData?.extraOptions?.config) {
          result = Object.keys(editData?.extraOptions?.config).length > 0 ? JSON.stringify(editData.extraOptions.config, null, 2) : ""
        } else {
          result = getDefaultCode(
            "config",
            selectedDriverInfo?.databaseDialect,
            selectedDriverInfo?.name,
            {
              datasourceName:
                urlFields.uploadDataFile?.replace(".", "_") || (editable && datasourceName) || "",
            }
          );
        }
        if (typeof configEditorRef?.current?.editor?.setValue === "function") {
          if (configEditorValueRef?.current) {
            configEditorRef?.current?.editor?.setValue(configEditorValueRef?.current);
          } else {
            configEditorRef?.current?.editor?.setValue((editorRefValue?.length || configEdited) ? editorRefValue : result);
            if (configEditorValueRef) {
              configEditorValueRef.current = (editorRefValue?.length || configEdited) ? editorRefValue : result;
            }
          }
        }
        setConfigEditorInput(configEditorValueRef?.current ? configEditorValueRef?.current : (editorRefValue?.length || configEdited) ? editorRefValue : result)
      }
    }, [configEditorInput]);

    return (
      <CodeMirror
        ref={configEditorRef}
        options={{
          lineWrapping: true,
          lint: true,
          mode: "javascript",
          json: true,
          lineNumbers: true,
          theme: "dracula",
          autoCloseTags: true,
          autoCloseBrackets: true,
        }}
        onChange={(editor, data, value) => {
          setConfigEdited(true)
          if (value !== configEditorInput) {
            setConfigEditorInput(value);
            if (configEditorValueRef)
              configEditorValueRef.current = value;
          }
        }}
      />
    );
  };

  const WrappedConfigEditor = () => {
    return <ConfigurationEditor urlFields={urlFields} editorRefValue={configEditorRef?.current?.editor?.getValue()} {...{ configEditorValueRef }} />
  }

  const GroovyEditor = ({ editorRefValueGroovy, groovyEditorValueRef }) => {
    const isGroovy =
      checkIfGroovyManaged(clickedActiveDatabaseData) ||
      checkIfGroovyPlain(clickedActiveDatabaseData);
    const [groovyEditorInput, setGroovyEditorInput] = useState('');
    const firstLoad = useRef(true);

    useEffect(() => {
      if (firstLoad.current === true && !groovyEditorInput?.length) {
        firstLoad.current = false;
        let result;
        if (editable && editData?.extraOptions?.script) {
          result = editData?.extraOptions?.script
        } else {
          result = getDefaultCode(
            "groovy",
            selectedDriverInfo?.databaseDialect,
            selectedDriverInfo?.name,
            { isGroovy }
          );
        }
        if (typeof groovyEditorRef?.current?.editor?.setValue === "function") {
          if (groovyEditorValueRef?.current) {
            groovyEditorRef?.current?.editor?.setValue(groovyEditorValueRef?.current);
          } else {
            groovyEditorRef?.current?.editor?.setValue((editorRefValueGroovy?.length || groovyEdited) ? editorRefValueGroovy : result);
            if (groovyEditorValueRef) {
              groovyEditorValueRef.current = (editorRefValueGroovy?.length || groovyEdited) ? editorRefValueGroovy : result;
            }
          }
        }
        setGroovyEditorInput(groovyEditorValueRef?.current ? groovyEditorValueRef?.current : (editorRefValueGroovy?.length || groovyEdited) ? editorRefValueGroovy : result)
      }
    }, [groovyEditorInput]);

    return (
      <CodeMirror
        ref={groovyEditorRef}
        options={{
          lineWrapping: true,
          lint: true,
          mode: "groovy",
          lineNumbers: true,
          theme: "dracula",
          autoCloseTags: true,
          autoCloseBrackets: true,
        }}
        onChange={(editor, data, value) => {
          setGroovyEdited(true)
          if (value !== groovyEditorInput) {
            setGroovyEditorInput(value);
            if (groovyEditorValueRef)
              groovyEditorValueRef.current = value;

          }
        }}
      />
    );
  };



  const CodeEditorTab = ({ onTabChange, activeTab }) => {

    return (
      <Tabs activeKey={activeTab} onChange={onTabChange}>
        <TabPane
          tab={
            <span>
              Configuration Editor
              <TooltipDrawer confType={"config"} />
            </span>
          }
          key="1"
        >
          <ConfigurationEditor urlFields={urlFields} editorRefValue={configEditorRef?.current?.editor?.getValue()} {...{ configEditorValueRef }} />
        </TabPane>
        <TabPane
          tab={
            <span>
              Groovy Editor
              <TooltipDrawer confType={"groovy"} />
            </span>
          }
          key="2"
        >
          <GroovyEditor editorRefValueGroovy={groovyEditorRef?.current?.editor?.getValue()}  {...{ groovyEditorValueRef }} />
        </TabPane>
      </Tabs>
    );
  };



  useEffect(() => {
    //This is for edit
    if (urlFields.database) {
      form.setFieldsValue({
        databaseName: urlFields.database.split("?")[0],
      });
    }
    if (urlFields.hostName) {
      form.setFieldsValue({
        hostName: getJdbcUrlSubString(urlFields.hostName, 0),
        port: urlFields.port,
      });
    }
    if (urlFields.uploadDataFile) {
      setFileUploaded(true);
      form.setFieldsValue({
        datasourceName: urlFields.uploadDataFile?.replace(".", "_"),
        hostName: urlFields.uploadDataFile,
      });
    }
  }, [urlFields]);

  useEffect(() => {
    if (activeKey === "1") {
      form.resetFields();
      if (selectedDriverInfo.parameters) {
        form.setFieldsValue({
          hostName: selectedDriverInfo.parameters.hostName,
          port: selectedDriverInfo.parameters.port,
        });
      }
    }
  }, [activeKey, selectedDriverInfo]);

  // bug 6387 - fix start
  useEffect(() => {
    if (driver && clickedActiveDatabaseData?.categoryName !== "advanced") {
      //  && clickedActiveDatabaseData?.categoryName !== "advanced"
      form.setFieldsValue({
        driver
      });
    }
  }, [activeKey, clickedActiveDatabaseData]);
  // bug 6387 - fix end

  const dispatch = useDispatch();

  const Notify = notify(dispatch);

  useEffect(() => {
    if (
      !editable &&
      (fileBrowserFolder.path || fileBrowserFolder.logicalPath)
    ) {
      setAdvancedSettingsClick(true);
    }
  }, [fileBrowserFolder]);

  useEffect(() => {
    if (clickedActiveDatabaseData.categoryType === "advanced") {
      setAdvancedSettingsClick(true);
    }
  }, [clickedActiveDatabaseData]);

  useEffect(() => {
    if (editable && Object.keys(editData).length) {
      let datasource = dataSources.filter((data) => {
        return checkIfGroovyPlain(editData)
          ? data.driver === editData.driver
          : data.driver === editData.driverName;
      });

      if (datasource.length > 1 && Object.keys(clickedRecordData).length) { // 8564
        datasource = datasource.filter(
          (eachData) => {
            if (eachData.sendVendorName) {
              if (!clickedRecordData.vendorName) {
                return false
              }
            }
            return true
          }
        )
      }

      if (checkIfGroovyManaged(editData)) {
        setDriverCategory("advanced");
        form.setFieldsValue({
          datasourceName: editData.name,
        });
      } else if (datasource.length) {
        let datasourceToSelect = datasource[0]
        // if (datasourceToSelect.url && isArray(datasourceToSelect.url)) {
        //   datasourceToSelect.url = datasourceToSelect.url[0]
        // }
        dispatch(setSelectedDriverInfo(datasourceToSelect));
        setDriverCategory(datasourceToSelect.categoryName);
        // if (checkIfGroovyPlain(editData)) {
        //   form.setFieldsValue({
        //     datasourceName: editData.name,
        //   });
        // }
      }
      editData?.data?.condition && setEditorInput(editData.data.condition);
      setDataSourceProvider(
        editData.dataSourceProvider || selectedDriverInfo.dataSourceProvider
      );
      // setOtherOptions(
      //   editData.jdbcUrl && editData.jdbcUrl.includes("?")
      //     ? editData.jdbcUrl.substring(editData.jdbcUrl.indexOf("?"))
      //     : ""
      // );
      // otherOptionsRef.current = editData.jdbcUrl && editData.jdbcUrl.includes("?")
      // ? editData.jdbcUrl.substring(editData.jdbcUrl.indexOf("?"))
      // : "";
      if (checkIfGroovyPlain(editData)) {
        setDriver(editData.driver);
        form.setFieldsValue({
          driver: editData.driver,
        });
        // setUrl(editData.data.url)
        setUrl(editData.data.jdbcUrl);
        // dispatch(setSelectedDriverInfo(editData.data));
      } else {
        setDriver(editData.driverName);
        form.setFieldsValue({
          driver: editData.driverName,
        });
        setUrl(editData.jdbcUrl);
      }
    } else {
      setDataSourceProvider(
        selectedDriverInfo?.dataSourceProvider
          ? selectedDriverInfo.dataSourceProvider
          : "tomcat"
      );
      setDriverCategory(selectedDriverInfo.categoryName);
      const selectedDriverURL = getSelectedDriverURL(selectedDriverInfo);
      setUrl(selectedDriverURL);
      form.setFieldsValue({
        driver: selectedDriverInfo.driver,
      });
      setDriver(selectedDriverInfo.driver);
    }
  }, [clickedActiveDatabaseData, selectedDriverInfo, editData]);
  const handleFormSubmit = (values) => {
    const { type, datasourceType } = buttonTypes;

    const config = configEditorValueRef?.current
      ? JSON.parse(configEditorValueRef?.current)
      : {};
    const script = (groovyEditorValueRef?.current !== undefined && groovyEditorValueRef?.current !== null)
      ? groovyEditorValueRef?.current
      : (editData?.extraOptions?.script || "");
    const FileEditName = editable ? editData?.jdbcUrl?.split(":").pop() : "";

    const newOtherOptions = (otherOptionsRef.current !== undefined && otherOptionsRef.current !== null)
      ? (otherOptionsRef.current) : (otherOptionsRef.current) || (editData?.jdbcUrl && editData?.jdbcUrl.includes("?")
        ? editData?.jdbcUrl.substring(editData?.jdbcUrl.indexOf("?"))
        : "");

    let testUri = "core/dataSource/test";
    let saveUri = "core/dataSource/write";
    let updateUri = "core/dataSource/update";
    let formData = getCreateEditFD({
      buttonTypes,
      values,
      clickedActiveDatabaseData,
      editorInput,
      driverCategory,
      driver,
      flatFileUploadName,
      driversList,
      fileBrowserFolder,
      editData,
      dataSourceProvider,
      otherOptions: newOtherOptions,
      editable,
      dsMode,
      selectedDriverInfo,
      // url
      selectedURLIndex
    });

    if (selectedDriverInfo?.databaseDialect === "flatfile") {
      formData.fileName = uploadFileName || "";
    }
    if (
      selectedDriverInfo?.databaseDialect === "flatfile" ||
      selectedDriverInfo?.databaseDialect === "hihttp"
    ) {
      formData.extraOptions = {
        config,
      };
    }
    if (selectedDriverInfo?.databaseDialect === "hihttp") {
      formData.extraOptions.script = script;
    }
    if (
      !editable &&
      type !== "test" &&
      selectedDriverInfo?.databaseDialect !== "hihttp" &&
      selectedDriverInfo?.databaseDialect === "flatfile"
    ) {
      formData.extraOptions.dataFile = urlFields.uploadDataFile;
    }

    if (
      editable &&
      type !== "test" &&
      selectedDriverInfo?.databaseDialect !== "hihttp" &&
      selectedDriverInfo?.databaseDialect === "flatfile"
    ) {
      formData.extraOptions.dataFile =
        urlFields.uploadDataFile || editData?.extraOptions?.dataFile;
    }

    // let formData = {
    //   classifier: "global",
    //   ...(clickedActiveDatabaseData.classifier !== "efwd" && {
    // dataSourceProvider: dataSourceProvider ? dataSourceProvider : "tomcat",
    //   }), //datasource provider is not required for efwd files
    // };
    // if (clickedActiveDatabaseData.type.includes('groovy')) {
    //   formData.condition = editorInput
    // }

    // if (driverCategory === "Flat Files") {
    //   const { dataName } = values;
    //   formData = {
    //     ...formData,
    //     driverName: driver,
    //     name: dataName,
    //     userName: "",
    //     password: "",
    //     jdbcUrl: flatFileUploadName,
    //   };
    // }
    // if (datasourceType === "jndi") {
    //   const { lookUpName, name } = values;
    //   formData = {
    //     ...formData,
    //     lookUpName: lookUpName,
    //     name: name,
    //   };
    // }
    // if (driverCategory !== "Flat Files" && datasourceType !== "jndi") {
    //   const {
    //     databaseName,
    //     datasourceName,
    //     password,
    //     username,
    //     hostName,
    //     port,
    //     collection,
    //     inputUrl,
    //   } = values;

    //   let defaultUrl = driversList.filter((eachData) => {
    //     if (eachData.driver === driver) {
    //       return eachData;
    //     }
    //   })[0]?.url;

    //   if (defaultUrl) {
    //     if (defaultUrl.includes("{{database}}")) {
    //       defaultUrl = defaultUrl.replace("{{database}}", databaseName);
    //     }
    //     if (defaultUrl.includes("{{hostName}}")) {
    //       defaultUrl = defaultUrl.replace("{{hostName}}", hostName);
    //     }
    //     if (defaultUrl.includes("{{port}}")) {
    //       defaultUrl = defaultUrl.replace("{{port}}", port);
    //     }
    //     if (otherOptions !== "") {
    //       defaultUrl = defaultUrl + otherOptions;
    //     }
    //   }

    //   let jdbcUrl = defaultUrl;

    //   if (checkIfGroovyManaged(clickedActiveDatabaseData)) {
    //     formData = {
    //       ...formData,
    //       "classifier": "efwd",
    //       "dataSourceType": "Groovy Managed Jdbc DataSource",
    //       "name": "TestGroovyConnection",
    //       "type": "sql.jdbc.groovy.managed",
    //       "directory": fileBrowserFolder.path,
    //       name: values?.datasourceName ? values?.datasourceName : datasourceName
    //     }
    //   }
    //   else {
    //     formData = {
    //       ...formData,
    //       driverName: driver,
    //       name: datasourceName,
    //       userName: username,
    //       password: password,
    //       database: databaseName,
    //       jdbcUrl: jdbcUrl ? jdbcUrl : inputUrl, //Input url is something user enters
    //       ...(collection && { collection: collection }),
    //     };
    //     if (checkIfGroovyPlain(clickedActiveDatabaseData)) {
    //       formData.dataSourceType = 'Groovy Plain Jdbc DataSource'
    //       formData.name = values?.datasourceName ? values?.datasourceName : datasourceName
    //     }
    //   }
    // }

    // if (clickedActiveDatabaseData.classifier === "efwd") {
    //   formData = {
    //     ...formData,
    //     classifier: "efwd",
    //     directory: fileBrowserFolder.path,
    //     type: clickedActiveDatabaseData.type,
    //   };
    // }
    // if (editable && !checkIfGroovyManaged(clickedActiveDatabaseData)) {
    //   const editId = checkIfGroovyPlain(clickedActiveDatabaseData) ? editData?.data?.id : editData["@id"];
    //   const dataSourceEditType = checkIfGroovyPlain(clickedActiveDatabaseData) ? editData?.data?.type : editData["@type"];
    //   formData = {
    //     ...formData,
    //     id: editId,
    //     type: dataSourceEditType,
    //     dataSourceProvider: editData.dataSourceProvider,
    //   };
    // }
    // else if (editable && checkIfGroovyManaged(clickedActiveDatabaseData)) {
    //   formData = {
    //     ...formData,
    //     id: editData.data.id,
    //     type: editData.data.type,
    //     // name: editData.name
    //     // dataSourceProvider: editData.dataSourceProvider,
    //   };
    // }

    const dataSourceTestandSave = (uri, formData) => {
      return new Promise((resolve, reject) => {
        requests.datasource(dispatch).postDataSourceData(
          formData,
          uri,
          (res) => {
            // Notify.success({
            //   type: "Network Call",
            //   ...res,
            // });
            setUrl(formData.jdbcUrl);
            if (uri === testUri) {
              setTestConnClick(false);
              uri === testUri && dispatch(setDataSourceTestConnection(true));
            }
            if (uri === saveUri) {
              dispatch(
                setViewData([
                  ...viewData,
                  {
                    ...res.data,
                    permissionLevel: 5, //when user creates the datasource. Default permission level is 5
                    classifier: formData.classifier,
                    dataSourceProvider: formData.dataSourceProvider,
                    name: formData.name,
                    data: {
                      id: res.data.id,
                      type: res.data.type,
                      ...(res.data.dir && {
                        dir: res.data.dir,
                      }),
                    },
                  },
                ])
              );
            }
            if (uri === updateUri) {
              const updatedData = viewData.map((eachData) => {
                if (
                  eachData.data.dir
                    ? +eachData.data.id === +formData.id && //+ is used to convert to int
                    eachData.data.dir === formData.directory
                    : +eachData.data.id === +formData.id
                ) {
                  return {
                    ...eachData,
                    name: formData.name,
                    ...res.data,
                  };
                } else {
                  return eachData;
                }
              });
              dispatch(setViewData(updatedData));
            }
            if (uri === saveUri || uri === updateUri) {
              setSaveConnClick(false);
              dispatch(setConnectedDataSourceData(res));
              dispatch(setDataSourceConnection(true));
              form.resetFields();
            }
            resolve(true);
          },
          (e) => {
            setUrl(formData.jdbcUrl);
            if (uri === testUri) {
              dispatch(setDataSourceTestConnection(false));
            }
            // Notify.error({
            //   type: "Network Call",
            //   ...e,
            // });
            setSaveConnClick(false);
            setTestConnClick(false);
            resolve(true);
          }
        );
      });
    };

    if (type === "test") {
      setTestConnClick(true);
      if (editable) {
        // formData for test
        const testFormData = { ...formData };
        if (!fileUploaded) {
          delete testFormData.fileName;
        }
        // setFileUploaded(false);
        delete testFormData.extraOptions;
        dataSourceTestandSave(testUri, testFormData);
      } else {
        dataSourceTestandSave(testUri, formData);
      }
    }

    if (type === "save" && editable === false) {
      setSaveConnClick(true);
      // formData for test
      const testFormData = { ...formData, extraOptions: { ...formData.extraOptions } };
      delete testFormData.extraOptions.dataFile;
      dataSourceTestandSave(testUri, testFormData).then(() => {
        return dataSourceTestandSave(saveUri, formData);
      });
    }

    // The Edit case
    if (type === "save" && editable) {
      setSaveConnClick(true);
      // formData for test
      const testFormData = { ...formData };
      if (!fileUploaded) {
        delete testFormData.fileName;
      }
      setFileUploaded(false);
      delete testFormData.extraOptions;

      dataSourceTestandSave(testUri, testFormData).then(() => {
        return dataSourceTestandSave(updateUri, formData);
      })
    }
  };

  const confirmSubmit = (values) => {
    if (!editable && buttonTypes?.type === "save" && clickedDSUrls?.length > 1) {
      Modal.confirm({
        title: 'Do you confirm all the details are correct and you want to submit?',
        okText: 'Yes',
        cancelText: 'No',
        onOk: () => {
          handleFormSubmit(values);
        },
        onCancel() { },
      });
    } else {
      handleFormSubmit(values);
    }
  };

  const handleDiverChange = (value) => {
    const datasource = dataSources.filter((data) => data.driver === value);
    setDriver(value);
    let datasourceToSelect = datasource[0];
    // if (datasourceToSelect.url && isArray(datasourceToSelect.url)) {
    //   datasourceToSelect.url = datasourceToSelect.url[0];
    // }
    dispatch(setSelectedDriverInfo(datasourceToSelect));
    setDriverCategory(datasourceToSelect.categoryName);
  };

  const handleAdvancedSettingClick = () => {
    setAdvancedSettingsClick(!advancedSettingsClick);
  };

  const isAdvanceDS = clickedActiveDatabaseData?.categoryType === "advanced";

  const driverSelectionDisabled = () => {
    if (editable) return true;
    if (isAdvanceDS) return false;
    return true
  }

  const renderAdvancedSettings = () => {
    return (
      <Row data-testid="hi-datasource-create-edit" gutter={16}>
        {dataSourceProvider !== "jndi" && (
          <>
            <Col span={12}>
              <Form.Item
                name="driver"
                data-testid="hi-datasource-driver"
                label={[
                  "Driver",
                  <Tooltip title="Select driver for the database. Note : Multiple drivers may be available for a specific data source. Please choose the appropriate driver for your connection.">
                    <QuestionCircleOutlined
                      style={{ marginLeft: "5px", fontSize: "10px" }}
                    />
                  </Tooltip>
                  ,
                ]}
                rules={[
                  { required: true, message: "Please select the driver" },
                ]}
              >
                <Select
                  // value={driver}
                  showSearch
                  onChange={(value) => handleDiverChange(value)}
                  placeholder="Please select the driver"
                  optionFilterProp="children"
                  filterOption={(input, option) =>
                    option.children
                      .toLowerCase()
                      .indexOf(input.toLowerCase()) >= 0
                  }
                  disabled={driverSelectionDisabled()}
                >
                  {driversList.map((eachDriver) => (
                    <Option key={uuidv4()} value={eachDriver.driver}>
                      {eachDriver.driver}
                    </Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            {selectedDriverInfo.parameters !== undefined &&
              selectedDriverInfo.parameters.collection !== undefined && (
                <Col span={12}>
                  <Form.Item
                    name="collection"
                    initialValue={`${selectedDriverInfo.parameters.collection}`}
                    label={[
                      "Collection",
                      <Tooltip title="Collection/Database name for Databases like Mongo">
                        <QuestionCircleOutlined
                          style={{ marginLeft: "5px", fontSize: "10px" }}
                        />
                      </Tooltip>,
                    ]}
                  >
                    <Input />
                  </Form.Item>
                </Col>
              )}
            {getSelectedDriverURL(selectedDriverInfo) && (
              <>
                <Col span={12}>
                  <Form.Item
                    label={[
                      "URL",
                      <Tooltip title="The generated JDBC URL for connecting to the database having driver, host, port etc. Note : Once url is selected it can not be changed.">
                        <QuestionCircleOutlined
                          style={{ marginLeft: "5px", fontSize: "10px" }}
                        />
                      </Tooltip>,
                    ]}
                  >
                    {editable ?
                      <Input disabled value={url} />
                      :
                      isArray(clickedDSUrls) ?
                        <Select
                          value={url}
                          onChange={(value) => {
                            setUrl(value);
                            const valueIndex = clickedDSUrls.indexOf(value);
                            setSelectedURLIndex(valueIndex);
                          }}
                          placeholder="Select URL"
                        >
                          {clickedDSUrls.map((url) => (
                            <Option key={url} value={url}>
                              {url}
                            </Option>
                          ))}
                        </Select>
                        :
                        <Input disabled value={url} />
                    }
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    label={[
                      "Other Options",
                      <Tooltip title="Please provide properties if other configurations are required. Eg: ?ssl=true">
                        <QuestionCircleOutlined
                          style={{ marginLeft: "5px", fontSize: "10px" }}
                        />
                      </Tooltip>,
                    ]}
                  >
                    <Input
                      defaultValue={editable && editData.jdbcUrl && editData.jdbcUrl.includes("?")
                        ? editData.jdbcUrl.substring(editData.jdbcUrl.indexOf("?"))
                        : ""}
                      // onChange={(e) => setOtherOptions(e.target.value)}
                      onChange={e => otherOptionsRef.current = e.target.value}
                    />
                  </Form.Item>
                </Col>
              </>
            )}
          </>
        )}
        {clickedActiveDatabaseData.classifier !== "efwd" && (
          <Col span={12}>
            <Form.Item
              data-testid="hi-datasource-provider"
              label={[
                "Datasource",
                <Tooltip title="Please select a datasource provider. Default is Tomcat. Note : Once datasource is selected it can not be changed.">
                  <QuestionCircleOutlined
                    style={{ marginLeft: "5px", fontSize: "10px" }}
                  />
                </Tooltip>,
              ]}
            >
              <Select
                disabled={editable ? true : false}
                value={
                  editable ? editData.dataSourceProvider : dataSourceProvider
                }
                onChange={(value) => setDataSourceProvider(value)}
              >
                <Option key="tomcat" value="tomcat">
                  Tomcat Datasource
                </Option>
                <Option key="noSql" value="noSql">
                  NoSql Datasource
                </Option>
                <Option key="none" value="none">
                  Adhoc Jdbc Connection
                </Option>
                <Option key="hikari" value="hikari">
                  Hikari Datasource
                </Option>
                <Option key="jndi" value="jndi">
                  JNDI Datasource
                </Option>
              </Select>
            </Form.Item>
          </Col>
        )}
        {/* {(isHiApi()) && (
          <Col span={12} style={{ display: "flex", alignItems: "flex-end" }}>
            <input
              type="file"
              ref={uploadFileConfigRef}
              onChange={(e) => handleUploadChange(e, UPLOAD_FILE_CONFIG)}
              style={{ display: "none" }}
            />
            <Form.Item
              label={[
                "File Config",
                <Tooltip title="Please upload your configuration file here">
                  <QuestionCircleOutlined
                    style={{ marginLeft: "5px", fontSize: "10px" }}
                  />
                </Tooltip>,
              ]}
              style={{ flex: "1" }}
            >
              <Input
                value={uploadFileConfigRef?.current?.files[0]?.name || extractConfigFilePath(url)}
                disabled={true}
              />
            </Form.Item>
            <Form.Item>
              <Button
                type="primary"
                onClick={() => handleUploadClick(UPLOAD_FILE_CONFIG)}
                icon={
                  isUploading[UPLOAD_FILE_CONFIG] ? (
                    <LoadingOutlined />
                  ) : (
                    <PlusOutlined />
                  )
                }
                disabled={isUploading[UPLOAD_FILE_CONFIG]}
              >
                Upload
              </Button>
            </Form.Item>
          </Col>
        )} */}
      </Row>
    );
  };

  const isGroovy =
    checkIfGroovyManaged(clickedActiveDatabaseData) ||
    checkIfGroovyPlain(clickedActiveDatabaseData);
  return (
    <>
      <Col span={24} style={{ textAlign: "right" }}>
        {!!!isGroovy && (
          <Button
            type="text"
            style={{ color: advancedSettingsClick ? "#1890ff" : "#000000" }}
            onClick={handleAdvancedSettingClick}
          >
            Advanced
          </Button>
        )}
      </Col>
      {isSericeCall ? (
        <Skeleton />
      ) : (
        <Form
          layout="vertical"
          hideRequiredMark
          onFinish={confirmSubmit}
          form={form}
        >
          {(advancedSettingsClick || isGroovy) &&
            (!checkIfGroovyManaged(clickedActiveDatabaseData) ||
              checkIfGroovyPlain(clickedActiveDatabaseData)) &&
            renderAdvancedSettings()}
          {(dataSourceProvider ||
            clickedActiveDatabaseData.categoryType === "advanced") &&
            (dataSourceProvider === "jndi" ? (
              <DataSourceJNDIFiles editable={editable} />
            ) : (
              <>
                <DataSourceDefaultFiles
                  editable={editable}
                  driverCategory={driverCategory}
                  urlFields={urlFields}
                  setUrlFields={setUrlFields}
                  testConnClick={testConnClick}
                  saveConnClick={saveConnClick}
                  checkIfGroovyPlain={checkIfGroovyPlain}
                  checkIfGroovyManaged={checkIfGroovyManaged}
                  advancedSettingsClick={advancedSettingsClick}
                  configEditorInput={configEditorInput}
                  setConfigEditorInput={setConfigEditorInput}
                  setGroovyEditorInput={setGroovyEditorInput}
                  groovyEditorInput={groovyEditorInput}
                  GroovyEditor={GroovyEditor}
                  ConfigurationEditor={ConfigurationEditor}
                  WrappedConfigEditor={WrappedConfigEditor}
                  CodeEditorTab={CodeEditorTab}
                  setUploadFileName={setUploadFileName}
                />
                <DataSourceFlatFiles
                  editable={editable}
                  driverCategory={driverCategory}
                  testConnClick={testConnClick}
                  saveConnClick={saveConnClick}
                />
                {clickedActiveDatabaseData.categoryName === "advanced" &&
                  !editable && (
                    <DataSourceAdvancedFiles
                      editable={editable}
                      driverCategory={driverCategory}
                      activeKey={activeKey}
                      testConnClick={testConnClick}
                      saveConnClick={saveConnClick}
                      setEditorInput={setEditorInput}
                      editorInput={editorInput}
                      checkIfGroovyPlain={checkIfGroovyPlain}
                      checkIfGroovyManaged={checkIfGroovyManaged}
                    />
                  )}
                {clickedActiveDatabaseData.categoryName === "advanced" &&
                  editable &&
                  (isGroovy ? editorInput : true) && (
                    <DataSourceAdvancedFiles
                      editable={editable}
                      driverCategory={driverCategory}
                      activeKey={activeKey}
                      testConnClick={testConnClick}
                      saveConnClick={saveConnClick}
                      setEditorInput={setEditorInput}
                      editorInput={editorInput}
                      checkIfGroovyPlain={checkIfGroovyPlain}
                      checkIfGroovyManaged={checkIfGroovyManaged}
                    />
                  )}
              </>
            ))}
        </Form>
      )}
    </>
  );
};

export default DataSourceCreateAndEdit;
