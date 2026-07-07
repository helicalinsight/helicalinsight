import { isArray } from "lodash";
import { checkIfGroovyManaged } from "./checkIfGroovyManaged";
import { checkIfGroovyPlain } from "./checkIfGroovyPlain";

export const getCreateEditFD = (...args) => {
  const {
    buttonTypes,
    values,
    clickedActiveDatabaseData = {},
    editorInput,
    driverCategory,
    driver,
    flatFileUploadName,
    driversList,
    fileBrowserFolder,
    editData,
    dataSourceProvider,
    otherOptions,
    editable,
    dsMode = {},
    selectedDriverInfo,
    // url = ""
    selectedURLIndex = 0
  } = args[0];
  const { type, datasourceType } = buttonTypes;
  let testUri = "core/dataSource/test";
  let saveUri = "core/dataSource/write";
  let updateUri = "core/dataSource/update";

  let formData = {
    classifier: "global",
    ...(clickedActiveDatabaseData.classifier !== "efwd" && {
      dataSourceProvider: dataSourceProvider ? dataSourceProvider : "tomcat",
    }), //datasource provider is not required for efwd files
  };
  if (clickedActiveDatabaseData?.type?.includes("groovy")) {
    formData.condition = editorInput;
  }

  if (driverCategory === "Flat Files") {
    const { dataName } = values;
    formData = {
      ...formData,
      driverName: driver,
      name: dataName,
      userName: "",
      password: "",
      jdbcUrl: flatFileUploadName,
    };
  }
  if (datasourceType === "jndi") {
    const { lookUpName, name } = values;
    formData = {
      ...formData,
      lookUpName: lookUpName,
      name: name,
    };
  }
  if (driverCategory !== "Flat Files" && datasourceType !== "jndi") {
    const {
      databaseName,
      datasourceName,
      password,
      username,
      hostName,
      port,
      collection,
      inputUrl,
    } = values;

    // let defaultUrl = driversList.filter((eachData) => {
    //   if (eachData.driver === driver) {
    //     return eachData;
    //   }
    // })[0]?.url;

    let urlIndex = 0
    if (selectedURLIndex) {
      urlIndex = selectedURLIndex
    }
    let urlFromDriverList = driversList.filter((eachData) => {
      if (eachData.driver === driver) {
        return eachData;
      }
    })[0]?.url;

    if (isArray(urlFromDriverList)) {
      urlFromDriverList = urlFromDriverList[urlIndex]
    }

    // let defaultUrl = (editable) ? urlFromDriverList : url ? url : urlFromDriverList;
    let defaultUrl = urlFromDriverList;

    if (defaultUrl) {
      if (defaultUrl.includes("{{database}}")) {
        defaultUrl = defaultUrl.replace("{{database}}", databaseName);
      }
      if (defaultUrl.includes("{{hostName}}")) {
        defaultUrl = defaultUrl.replace("{{hostName}}", hostName);
      }
      if (defaultUrl.includes("{{port}}")) {
        defaultUrl = defaultUrl.replace("{{port}}", port);
      }
      if (otherOptions !== "") {
        defaultUrl = defaultUrl + otherOptions;
      }
    }

    let jdbcUrl = defaultUrl;

    if (checkIfGroovyManaged(clickedActiveDatabaseData)) {
      formData = {
        ...formData,
        classifier: "efwd",
        dataSourceType: "Groovy Managed Jdbc DataSource",
        name: "TestGroovyConnection",
        type: "sql.jdbc.groovy.managed",
        directory: fileBrowserFolder.path,
        name: values?.datasourceName ? values?.datasourceName : datasourceName,
      };
    } else {
      formData = {
        ...formData,
        driverName: driver,
        name: datasourceName,
        userName: username,
        password: password,
        database: databaseName,
        jdbcUrl: jdbcUrl ? jdbcUrl : inputUrl, //Input url is something user enters
        ...(collection && { collection: collection }),
      };
      if (selectedDriverInfo?.sendVendorName) {
        formData = {
          ...formData,
          vendorName: selectedDriverInfo?.name
        }
      }
      if (checkIfGroovyPlain(clickedActiveDatabaseData)) {
        formData.dataSourceType = "Groovy Plain Jdbc DataSource";
        formData.name = values?.datasourceName
          ? values?.datasourceName
          : datasourceName;
      }
    }
  }

  if (clickedActiveDatabaseData.classifier === "efwd") {
    formData = {
      ...formData,
      classifier: "efwd",
      directory: fileBrowserFolder.path,
      type: clickedActiveDatabaseData.type,
    };
  }
  if (editable) {
    if (!checkIfGroovyManaged(clickedActiveDatabaseData)) {
      const editId = checkIfGroovyPlain(clickedActiveDatabaseData)
        ? editData?.data?.id
        : editData["@id"];
      const dataSourceEditType = checkIfGroovyPlain(clickedActiveDatabaseData)
        ? editData?.data?.type
        : editData["@type"];
      formData = {
        ...formData,
        id: editId,
        type: dataSourceEditType,
        dataSourceProvider: editData.dataSourceProvider,
      };
      //for plain jdbc connections editing from metadata page
      try {
        if (
          dsMode?.data?.id === editData?.data?.id &&
          dsMode?.data?.type === editData?.data?.type &&
          dsMode?.data?.classifier
        ) {
          formData.classifier = dsMode.data.classifier;
          formData.directory = dsMode.data.dir;
        }
      } catch (e) { }
    } else if (checkIfGroovyManaged(clickedActiveDatabaseData)) {
      formData = {
        ...formData,
        id: editData.data.id,
        type: editData.data.type,
        // name: editData.name
        // dataSourceProvider: editData.dataSourceProvider,
      };
    } else {
    }
  }
  return formData;
};
