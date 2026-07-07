import initialStates from "./initialStates";
import actionTypes from "../actions/actionTypes";

const reportCEReducer = (state = initialStates.reportCEIntialState, action) => {
  const { type, payload } = action;
  switch (type) {
    case actionTypes.REPORTCE.STORE_REPORT_CE_CODE_EDIT:
      return { ...state, editing: payload };
    case actionTypes.REPORTCE.STORE_REPORT_CE_TYPES_DETAILS:
      return { ...state, typesDetails: payload };
    case actionTypes.REPORTCE.STORE_REPORT_CE_DATASOURCE_DATA:
      return { ...state, dataSourceData: payload };
    case actionTypes.REPORTCE.STORE_REPORT_CE_PARAMETERS_DATA:
      return { ...state, parametersData: payload };
    case actionTypes.REPORTCE.STORE_REPORT_CE_REPORT_DATA:
      return { ...state, reportData: payload };
    case actionTypes.REPORTCE.STORE_REPORT_CE_DASHBOARD_DATA: {
      return { ...state, dashboardLayoutData: payload };
    }
    case actionTypes.REPORTCE.STORE_REPORT_CE_ACTIVE_TAB: {
      return { ...state, activeTabs: payload };
    }
    case actionTypes.REPORTCE.STORE_REPORT_CE_ACTIVE_EDITOR_DATA:
      if (payload.type === "datasource") {
        return {
          ...state,
          activeEditorData: {
            ...state.activeEditorData,
            datasourceId: payload.id,
            name: payload.name,
            type: payload.type,
          },
        };
      } else if (payload.type === "dashboardLayout") {
        return {
          ...state,
          activeEditorData: {
            ...state.activeEditorData,
            dashboardId: payload.id,
            type: payload.type,
          },
        };
      } else if (payload.type === "parameter") {
        return {
          ...state,
          activeEditorData: {
            ...state.activeEditorData,
            paramterId: payload.id,
            name: payload.name,
            type: payload.type,
          },
        };
      } else if (payload.type === "report") {
        return {
          ...state,
          activeEditorData: {
            ...state.activeEditorData,
            reportId: payload.id,
            name: payload.name,
            type: payload.type,
          },
        };
      }

    default:
      return state;
  }
};

export default reportCEReducer;
