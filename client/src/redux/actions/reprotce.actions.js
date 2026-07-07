import actionTypes from "./actionTypes";

export const storeReportEdit = (data) => {
  return {
    type: actionTypes.REPORTCE.STORE_REPORT_CE_CODE_EDIT,
    payload: data,
  };
};

export const storeReportCeTypesDetails = (data) => {
  return {
    type: actionTypes.REPORTCE.STORE_REPORT_CE_TYPES_DETAILS,
    payload: data,
  };
};

export const storeReportCeDatasource = (data) => {
  return {
    type: actionTypes.REPORTCE.STORE_REPORT_CE_DATASOURCE_DATA,
    payload: data,
  };
};

export const storeReportCeParameters = (data) => {
  return {
    type: actionTypes.REPORTCE.STORE_REPORT_CE_PARAMETERS_DATA,
    payload: data,
  };
};

export const storeReportCeReport = (data) => {
  return {
    type: actionTypes.REPORTCE.STORE_REPORT_CE_REPORT_DATA,
    payload: data,
  };
};

export const storeReportCeDashboardData = (data) => {
  return {
    type: actionTypes.REPORTCE.STORE_REPORT_CE_DASHBOARD_DATA,
    payload: data,
  };
};

export const storeReportCeEditorData = (data) => {
  return {
    type: actionTypes.REPORTCE.STORE_REPORT_CE_ACTIVE_EDITOR_DATA,
    payload: data,
  };
};

export const storeActiveTab = (data) => {
  return {
    type: actionTypes.REPORTCE.STORE_REPORT_CE_ACTIVE_TAB,
    payload: data,
  };
};
