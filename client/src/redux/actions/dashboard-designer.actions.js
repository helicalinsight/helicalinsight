import actionTypes from "./actionTypes";

export const storeGridItems = (obj) => {
  return { type: actionTypes.STORE_GRID_ITEMS_DATA, payload: obj };
};

// export const storeBreakpointsData = (obj) => {
//   return { type: actionTypes.STORE_BREAKPOINTS_DATA, payload: obj };
// };
// export const storeColumnsData = (obj) => {
//   return { type: actionTypes.STORE_COLUMNS_DATA, payload: obj };
// };
export const toggleDashboardDrawer = (bool) => {
  return { type: actionTypes.TOGGLE_DASHBOARD_DRAWER, payload: bool };
};
export const changeDrawerPosition = () => {
  return { type: actionTypes.CHANGE_DRAWER_POSITION };
};
export const updateGridSettings = (obj) => {
  return { type: actionTypes.UPDATE_GRID_SETTINGS, payload: obj };
};
export const updateTempValue = (value) => {
  return { type: "tempValue", payload: value };
};
export const updateGroupId = (id) => {
  return { type: actionTypes.UPDATE_GROUP_ID, payload: id };
};
export const updateGridItemsData = (obj) => {
  return { type: actionTypes.UPDATE_GRID_ITEMS_DATA, payload: obj };
};
export const addGridItem = (obj) => {
  return { type: actionTypes.ADD_GRID_ITEM, payload: obj };
};
export const updateGridItemId = (id) => {
  return { type: actionTypes.UPDATE_GRID_ITEM_ID, payload: id };
};
export const toggleGridItemDrawer = (id) => {
  return { type: actionTypes.TOGGLE_GRID_ITEM_DRAWER, payload: id };
};
export const updateGridLayout = (layout) => {
  return { type: actionTypes.UPDATE_GRID_LAYOUT, payload: layout };
};
export const updateGridItemStyles = (obj) => {
  return { type: actionTypes.UPDATE_GRID_ITEM_STYLES, payload: obj };
};
export const changeIsDraggableInGridItem = (id) => {
  return { type: actionTypes.CHANGE_IS_DRAGGABLE_GRID_ITEM, payload: id };
};
export const changeIsSelectedInGridItem = (id) => {
  return { type: actionTypes.CHANGE_IS_SELECTED_GRID_ITEM, payload: id };
};
export const removeGridItem = (id) => {
  return { type: actionTypes.REMOVE_GRID_ITEM, payload: id };
};
export const unGroupGridItem = (id) => {
  return { type: actionTypes.UNGROUP_GRID_ITEM, payload: id };
};
export const deleteGridItem = (id) => {
  return { type: actionTypes.DELETE_GRID_ITEM, payload: id };
};
export const savedDashboardConfig = (id) => {
  return { type: actionTypes.UPDATE_DASHBOARD_UUID, payload: id };
};
export const updateNestedGridLayout = (obj) => {
  return { type: actionTypes.UPDATE_NESTED_GRID_LAYOUT, payload: obj };
};
export const storeGridSettingsData = (array) => {
  return { type: actionTypes.STORE_GRID_SETTINGS, payload: array };
};
export const storeDashboardConfig = (obj) => {
  return { type: actionTypes.STORE_DASHBOARD_CONFIG, payload: obj };
};
export const setDesignerMode = (string) => {
  return { type: actionTypes.SET_DESIGNER_MODE, payload: string };
};
export const resetDesignerState = () => {
  return { type: actionTypes.RESET_DESIGNER_STATE };
};

export const storeFiltersList = (obj) => {
  return { type: actionTypes.STORE_FILTERS_LIST, payload: obj };
};
export const setPreviewMode = (boolean) => {
  return { type: actionTypes.SET_PREVIEW_MODE, payload: boolean };
};
export const updateDesignerSettings = (obj) => {
  return { type: actionTypes.UPDATE_DESIGNER_SETTINGS, payload: obj };
};
export const addFilterItem = (obj) => {
  return { type: actionTypes.ADD_FILTER_ITEM, payload: obj };
};
export const storeFilterItemsData = (array) => {
  return { type: actionTypes.STORE_FILTER_ITEMS_DATA, payload: array };
};
export const storeFilterItemsToGridItemsData = (array) => {
  return {
    type: actionTypes.STORE_FILTER_ITEMS_TO_GRID_ITEMS_DATA,
    payload: array,
  };
};

export const updateParameterDrawerStatus = (bool) => {
  return {
    type: actionTypes.UPDATE_PARAMETER_DRAWER_STATUS,
    payload: bool,
  };
};

export const maximiseGridItem = (gridItem) => {
  return {
    type: actionTypes.MAXIMISE_GRID_ITEM,
    payload: gridItem,
  };
};

export const storeLastModified = (obj) => {
  return {
    type: actionTypes.STORE_LAST_MODIFIED,
    payload: obj,
  };
};

export const setIsLoading = (bool) => {
  return {
    type: actionTypes.SET_IS_LOADING,
    payload: bool,
  };
};

export const setFilterCounter = (bool) => {
  return {
    type: actionTypes.SET_FILTER_COUNTER,
    payload: bool,
  };
};
export const applyDashboardFilters = (date) => {
  return {
    type: actionTypes.APPLY_DASHBOARD_FILTERS,
    payload: date,
  };
};
export const isSavingDesigner = (bool) => {
  return {
    type: actionTypes.DESIGNER_IS_SAVING,
    payload: bool,
  };
};
export const designerDrawerExpansion = () => {
  return {
    type: actionTypes.DESIGNER_DRAWER_EXPANSION,
  };
};

export const designerUndo = () => {
  return { type: actionTypes.DESIGNER_UNDO };
};

export const designerRedo = () => {
  return { type: actionTypes.DESIGNER_REDO };
};

export const updateDesignerLayout = (layout) => {
  return { type: actionTypes.UPDATE_DESIGNER_LAYOUT, payload: layout };
}

export const setToggleToolsAreaShelf = () => {
  return { type: actionTypes.TOGGLE_TOOLS_AREA_SHELF }
}

export const clearDesignerUndoRedoHistory = () => {
  return {
    type: actionTypes.CLEAR_DESIGNER_HISTORY
  }
}

export const addToTab = (obj) => {
  return { type: actionTypes.ADD_TO_TAB, payload: obj };
};

export const updateTabGridItemsLayout = (obj) => {
  return { type: actionTypes.UPDATE_TAB_GRID_ITEMS_LAYOUT, payload: obj };
}

export const updateTabGridItemsLayoutFromProperties = (obj) => {
  return { type: actionTypes.UPDATE_TAB_GRID_ITEMS_LAYOUT_FROM_PROPERTIES, payload: obj };
}

export const updateDropdown = (obj) => {
  return { type: actionTypes.UPDATE_DROPDOWN, payload: obj };
};

export const removeItemsOpenThroughApi = (obj) => {
  return { type: actionTypes.REMOVE_ITEMS_OPEN_THROUGH_API, payload: obj };
};

export const changeTheReport = (obj) => {
  return { type: actionTypes.CHANGE_THE_REPORT, payload: obj };
};

export const openCompactFbBrower = (obj) => {
  return { type: actionTypes.OPEN_COMPACT_FB_BROWSER, payload: obj };
};

export const replaceReportId = (obj) => {
  return { type: actionTypes.REPLACE_REPORT_ID, payload: obj };
};

export const updateFreeFloatFilterSettings = (obj) => {
  return { type: actionTypes.UPDATE_FREE_FLOAT_FILTER_SETTINGS, payload: obj };
}