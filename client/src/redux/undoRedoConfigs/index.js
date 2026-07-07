import actionTypes from "../actions/actionTypes";
import { hreportIncludingTypes } from "./constants";

const checkPayload = (action) => {
  if (action?.payload?.undoRedoAction) return true
  if (action?.payload?.reportData?.loading) return true
  return false
}

const filterActions = (action) => {
  if (checkPayload(action)) return false
  return hreportIncludingTypes.includes(action.type);
}

export const hreportUndoRedoConfig = {
  limit: 100,
  undoType: actionTypes.HREPORT_UNDO,
  redoType: actionTypes.HREPORT_REDO,
  syncFilter: true,
  filter: filterActions,
  clearHistoryType: actionTypes.CLEAR_HREPORT_HISTORY,
}