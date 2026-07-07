import actionTypes from "../actions/actionTypes";
import { cannedReportIncludingTypes } from "./constants";

const checkPayload = (action) => {
  if (action?.payload?.undoRedoAction === false) return true;
  if (action?.meta?.undoRedoAction === false) return true;
  return false;
};

const filterActions = (action) => {
  if (checkPayload(action)) return false;
  return cannedReportIncludingTypes.includes(action.type);
};

export const cannedReportUndoRedoConfig = {
  limit: 100,
  undoType: actionTypes.HCR_UNDO,
  redoType: actionTypes.HCR_REDO,
  syncFilter: true,
  filter: filterActions,
  clearHistoryType: actionTypes.HCR_CLEAR_HISTORY,
};
