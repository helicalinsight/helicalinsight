import produce from "immer";
import actionTypes from "../actions/actionTypes";
import initialStates from "./initialStates";
import { getFieldDisplayName } from "../../utils/utilities";

const dashboardReducer = (
  state = initialStates.dashboardIntialState,
  action
) => {
  switch (action.type) {
    case actionTypes.STORE_DASHBOARD_VARIABLES:
      return produce(state, (draft) => {
        if (!draft.dashboardVariables[action.payload.parameter]) {
          draft.dashboardVariables[action.payload.parameter] =
            action.payload.value;
        }
      });
    // case actionTypes.ADD_GRID_ITEM:
    //   const { id, parameter, listener, reportInfo, filters } = action.payload;
    //   let copy1 = [...state.components];
    //   copy1.push({ id, parameter, listener, reportInfo, filters });
    //   return { ...state, components: copy1 };
    // case actionTypes.STORE_FILTERS_LIST:
    //   return produce(state, (draft) => {
    //     let { filtersList, dashboardItemId } = action.payload;
    //     draft.components = draft.components.map((item) => {
    //       if (item.id === dashboardItemId) {
    //         item.filters = filtersList;
    //         item.listeners = filtersList.map((item) =>
    //           getFieldDisplayName(item)
    //         );
    //       }
    //       return item;
    //     });
    //   });
    case actionTypes.UPDATE_DASHBOARD_VARIABLES:
      const { value, key } = action.payload;
      return produce(state, (draft) => {
        draft.dashboardVariables[key] = value;
      });
    case actionTypes.DELETE_GRID_ITEM:
      // const { value, key } = action.payload;
      return produce(state, (draft) => {
        draft.components = draft.components.filter(
          (item) => item.id !== action.payload
        );
      });
    case actionTypes.STORE_DASHBOARD_CONFIG:
      const { dashboardVariables } = action.payload;
      return {
        ...state,
        dashboardVariables,
      };
    case actionTypes.SET_DESIGNER_MODE:
      return initialStates.dashboardIntialState;
    case actionTypes.DELETE_DASHBOARD_VARIABLES:
      return { ...state, dashboardVariables: {} };
    case actionTypes.STORE_FILTERS_LIST:
      return produce(state, (draft) => {
        action.payload.filtersList.forEach((item) => {
          const parameter = getFieldDisplayName(item);
          if (!draft.dashboardVariables[parameter]) {
            draft.dashboardVariables[parameter] = item.values;
          }
        });
      });
    case actionTypes.UPDATE_REPORTS_REFRESH:
      return { ...state, refreshReports: state.refreshReports + 1 };
    case actionTypes.REFRESH_DASHBOARD:
      return { ...state, refreshDashboard: state.refreshDashboard + 1 };
    default:
      return state;
  }
};
export default dashboardReducer;
