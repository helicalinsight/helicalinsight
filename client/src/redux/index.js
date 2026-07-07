import { combineReducers } from "redux";
import dataReducer from "./reducers/data.reducer";
import adminReducer from "./reducers/admin.reducer";
import fileBrowserReducer from "./reducers/filebrowser.reducer";
import userActionsReducer from "./reducers/useractions.reducer";
import { appReducer } from "./reducers/app.reducer";
import datasourceReducer from "./reducers/datasource.reducer";
import dashboardReducer from "./reducers/dashboard.reducer";
import hreportReducer from "./reducers/hreport.reducer";
import metadataReducer from "./reducers/metadata.reducer";
import designerReducer from "./reducers/dashboard-designer.reducer";
import reportCEReducer from "./reducers/reportce.reducer";
import { cubeReducer } from "./reducers/cube.reducer";
import { propertyPaneReducer } from "./reducers/property-pane.reducer.js";
import instantBIReducer from "./reducers/instant-bi.reducer";
import undoable, {includeAction } from "redux-undo";
import actionTypes from "./actions/actionTypes";
import { hreportUndoRedoConfig } from "./undoRedoConfigs";
import { dashboardIncludingTypes, designerIncludingTypes, metadataUndoRedoActions } from "./undoRedoConfigs/constants";
import { hcrReducer } from "./reducers/hCR.reducer.js";
import { cannedReportUndoRedoConfig } from "./cannedReportUndoRedoConfig/index.js";
import { agentReducer } from "./reducers/agent.reducer.js";

const reducers = combineReducers({
  app: appReducer,
  data: dataReducer,
  admin: adminReducer,
  datasource: datasourceReducer,
  fileBrowser: fileBrowserReducer,
  userActions: userActionsReducer,
  dashboard: undoable(dashboardReducer, {
    undoType: actionTypes.DESIGNER_UNDO,
    redoType: actionTypes.DESIGNER_REDO,
    clearHistoryType:actionTypes.CLEAR_DESIGNER_HISTORY,
    syncFilter: true,
    filter:function filterActions(action) {
      return dashboardIncludingTypes.includes(action.type);
    } 
  }),
  hreport: undoable(hreportReducer, hreportUndoRedoConfig),
  metadata: undoable(metadataReducer, {
    undoType: actionTypes.METADATA.METADATA_UNDO,
    redoType: actionTypes.METADATA.METADATA_REDO,
    syncFilter: true,
    filter: (action) => metadataUndoRedoActions.hasOwnProperty(action.type)
    // limit: false,
    // groupBy: () => [metadataUndoRedoActions.UPDATE_METADATA_STATE, metadataUndoRedoActions.UPDATE_METADATA_STATE],
    // debug: true,
  }),
  designer: undoable(designerReducer, {
    undoType: actionTypes.DESIGNER_UNDO,
    redoType: actionTypes.DESIGNER_REDO,
    clearHistoryType:actionTypes.CLEAR_DESIGNER_HISTORY,
    syncFilter: true,
    filter:function filterActions(action) {
      return designerIncludingTypes.includes(action.type);
    } 
  }),
  cannedReports: undoable(hcrReducer, cannedReportUndoRedoConfig),
  reportCe: reportCEReducer,
  cube: cubeReducer,
  agent: agentReducer,
  instantBI: instantBIReducer,
  propertyPane: propertyPaneReducer
});

export default reducers;
