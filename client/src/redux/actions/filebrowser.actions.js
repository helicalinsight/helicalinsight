import actionTypes from "./actionTypes";

const setFilterByType = (val) => {
  return { type: actionTypes.SET_FILTER_BY_TYPE, payload: val };
};

const setGroupBy = (val) => {
  return { type: actionTypes.SET_GROUP_BY, payload: val };
};

const setFbLoading = (bool) => {
  return { type: actionTypes.SET_LOADING, payload: bool };
};

const setFbContent = (obj) => {
  return { type: actionTypes.SET_CONTENT, payload: obj };
};

const setFbError = (err) => {
  return { type: actionTypes.SET_ERROR, payload: err };
};

const setFilteredFiles = (files) => {
  return { type: actionTypes.SET_FILTERED_FILES, payload: files };
};

const setSearchResults = (searchRes) => {
  return { type: actionTypes.FB_SEARCH, payload: searchRes };
};

const setContextMenuItemDetails = (obj) => {
  return { type: actionTypes.SET_CONTEXT_ITEM, payload: obj };
};

const setExpandedRow = (row) => {
  return { type: actionTypes.SET_EXPANDED_ROW, payload: row };
};

const setShareModalVisibility = () => {
  return { type: actionTypes.SHARE_MODAL_VISIBILITY };
};

const setTableColumns = (columnsArray) => {
  return { type: actionTypes.TABLE_COLUMNS, payload: columnsArray };
};

const setShowFileBrowser = (bool) => {
  return { type: actionTypes.TOGGLE_FILE_BROWSER, payload: bool };
};

const setDeletedItems = (obj) => {
  return { type: actionTypes.SET_DELETED_UPDATED, payload: obj };
};

const setShareTableData = (dataObj) => {
  return { type: actionTypes.SET_SHARE_TABLE_DATA, payload: dataObj };
};

const setShareTableDataKey = (tabKey, item) => {
  return { type: actionTypes.SET_SHARE_TABLE_DATA_KEY, payload: {tabKey, item} };
};

const setGlobalSearch = (searchTerm) => {
  return { type: actionTypes.SET_GLOBAL_SEARCH, payload: searchTerm };
};

const setModeCoordniates = (coord) => {
  return { type: actionTypes.SET_COMPACT_MODE_COORDS, payload: coord };
};

const setGlobalFbVisibility = (bool) => {
  return { type: actionTypes.TOGGLE_GLOBAL_FB, payload: bool };
};

const setExportModalData = (data) => {
  return {type: actionTypes.EXPORT_MODAL_DATA, payload: data}
}

const saveFileinFb = (file) => {
  return {type: actionTypes.SAVE_FILE_FB, payload: file}
}

// const setCutOrCopy = (data) => {
//   return {type: actionTypes.CUT_OR_COPY, payload: data}
// }

const setCutOrCopyItemDetails = (data) => {
  return {type: actionTypes.CUT_OR_COPY_ITEM_DETAILS, payload: data}
}

export const fileBrowserActions = {
  // setCutOrCopy,
  setCutOrCopyItemDetails,
  setFilterByType,
  setGroupBy,
  setFbLoading,
  setFbContent,
  setFbError,
  setFilteredFiles,
  setSearchResults,
  setContextMenuItemDetails,
  setExpandedRow,
  setShareModalVisibility,
  setTableColumns,
  setShowFileBrowser,
  setDeletedItems,
  setShareTableData,
  setShareTableDataKey,
  setGlobalSearch,
  setModeCoordniates,
  setGlobalFbVisibility,
  setExportModalData,
  saveFileinFb
};
