import actionTypes from "./actionTypes";

export const setDashboardVariables = (obj) => {
  return { type: actionTypes.STORE_DASHBOARD_VARIABLES, payload: obj };
};
export const storeDashboardComponent = (obj) => {
  return { type: actionTypes.STORE_DASHBOARD_COMPONENT, payload: obj };
};

export const updateDashboardVariables = (obj) => {
  return { type: actionTypes.UPDATE_DASHBOARD_VARIABLES, payload: obj };
};
export const deleteDashboardVariables = (obj) => {
  return { type: actionTypes.DELETE_DASHBOARD_VARIABLES, payload: obj };
};
export const updateReportsRefresh = (bool) => {
  return { type: actionTypes.UPDATE_REPORTS_REFRESH, payload: bool };
};
export const refreshDashboard = (bool) => {
  return { type: actionTypes.REFRESH_DASHBOARD, payload: bool };
};

