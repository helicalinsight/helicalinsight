const SET_DS_SERICE_CALL = "SET_DS_SERICE_CALL";
const SET_DS_MODE = "SET_DS_MODE";
const SET_DATA_SOURCES = "SET_DATA_SOURCES";
const SET_DATA_SOURCE_TYPES = "SET_DATA_SOURCE_TYPES";
const SET_DATA_SOURCE_DRIVERS_LIST = "SET_DATA_SOURCE_DRIVERS_LIST";
const SET_SELECTED_DRIVER_INFO = "SET_SELECTED_DRIVER_INFO";
const SET_DRIVER_CATEGORY = "SET_DRIVER_CATEGORY";
const SET_FILE_BROWSER_FOLDER = "SET_FILE_BROWSER_FOLDER";
const SET_REPORTS_DATA = "SET_REPORTS_DATA";
const SET_FLAT_FILES_UPLOAD_NAME = "SET_FLAT_FILES_UPLOAD_NAME";
const SET_VIEW_DATA = "SET_VIEW_DATA";
const SET_CLICKED_RECORD_DATA = "SET_CLICKED_RECORD_DATA";
const SET_EDIT_DATA = "SET_EDIT_DATA";
const SET_CLICKED_ACTIVE_FILE_DATA = "SET_CLICKED_ACTIVE_FILE_DATA";
const SET_IS_EDIT_CLICKED = "SET_IS_EDIT_CLICKED";
const SET_DATA_SOURCE_CONNECTION = "SET_DATA_SOURCE_CONNECTION";
const SET_DATASOURCE_TEST_CONNECTION = "SET_DATASOURCE_TEST_CONNECTION";
const SET_CONNECTED_DATASOURCE_DATA = "SET_CONNECTED_DATASOURCE_DATA";
const SET_BUTTON_TYPE = "SET_BUTTON_TYPE";
const RESET_FIELDS = "RESET_FIELDS";
const SET_CLICKED_DATA_SOURCE = "SET_CLICKED_DATA_SOURCE";

export const setDsSericeCall = (data) => {
  return {
    type: SET_DS_SERICE_CALL,
    payload: data,
  };
};

export const setDsMode = (data) => {
  return {
    type: SET_DS_MODE,
    payload: data,
  };
};

export const setDataSources = (data) => {
  return {
    type: SET_DATA_SOURCES,
    payload: data,
  };
};

export const setDataSourceTypes = (data) => {
  return {
    type: SET_DATA_SOURCE_TYPES,
    payload: data,
  };
};

export const setDataSourceDriversList = (data) => {
  return {
    type: SET_DATA_SOURCE_DRIVERS_LIST,
    payload: data,
  };
};

export const setSelectedDriverInfo = (data) => {
  return { type: SET_SELECTED_DRIVER_INFO, payload: data };
};

export const setDriverCategory = (data) => {
  return { type: SET_DRIVER_CATEGORY, payload: data };
};

export const setReportsData = (data) => {
  return { type: SET_REPORTS_DATA, payload: data };
};

export const setFlatFilesUploadName = (data) => {
  return { type: SET_FLAT_FILES_UPLOAD_NAME, payload: data };
};

export const setFileBrowserFolder = (data) => {
  return { type: SET_FILE_BROWSER_FOLDER, payload: data };
};

export const setViewData = (data) => {
  return { type: SET_VIEW_DATA, payload: data };
};

export const setClickedRecordData = (data) => {
  return { type: SET_CLICKED_RECORD_DATA, payload: data };
};

export const setEditData = (data) => {
  return { type: SET_EDIT_DATA, payload: data };
};

export const setClickedActiveDatabaseData = (data) => {
  return { type: SET_CLICKED_ACTIVE_FILE_DATA, payload: data };
};

export const setIsEditClicked = (data) => {
  return { type: SET_IS_EDIT_CLICKED, payload: data };
};

export const setDataSourceConnection = (data) => {
  return { type: SET_DATA_SOURCE_CONNECTION, payload: data };
};

export const setDataSourceTestConnection = (data) => {
  return { type: SET_DATASOURCE_TEST_CONNECTION, payload: data };
};

export const setConnectedDataSourceData = (data) => {
  return { type: SET_CONNECTED_DATASOURCE_DATA, payload: data };
};

export const setButtonType = (data) => {
  return { type: SET_BUTTON_TYPE, payload: data };
};

export const resetStateFields = (data) => {
  return { type: RESET_FIELDS, payload: data };
};

export const setClickedDataSource = (data) => {
  return { type: SET_CLICKED_DATA_SOURCE, payload: data };
};
