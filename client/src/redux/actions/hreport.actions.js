import actionTypes from './actionTypes';

export const loadIntialReport = (data) => {
	return {
		type: actionTypes.LOAD_INTIAL_REPORT,
		payload: data
	};
};
export const addNewReport = (data) => {
	return {
		type: actionTypes.ADD_NEW_REPORT,
		payload: data
	};
};
export const changeReport = (data) => {
	return {
		type: actionTypes.CHANGE_REPORT,
		payload: data
	};
};
export const removeReport = (data) => {
	return {
		type: actionTypes.REMOVE_REPORT,
		payload: data
	};
};
export const removeAllReports = (data) => {
	return {
		type: actionTypes.REMOVE_ALL_REPORTS,
		payload: data
	};
};
export const loadMetadata = (data) => {
	return {
		type: actionTypes.LOAD_METADATA,
		payload: data
	};
};
export const loadCube = (data) => {
	return {
		type: actionTypes.LOAD_CUBE,
		payload: data
	};
};
export const searchInMetadata = (data) => {
	return {
		type: actionTypes.SEARCH_IN_METADATA,
		payload: data
	};
};
export const onExpandTable = (data) => {
	return {
		type: actionTypes.ON_EXPAND_TABLE,
		payload: data
	};
};
export const addFieldToCanvas = (data) => {
	return {
		type: actionTypes.ADD_FIELD_TO_CANVAS,
		payload: data
	};
};
export const updateFieldAlias = (data) => {
	return {
		type: actionTypes.UPDATE_FIELD_ALIAS,
		payload: data
	};
};
export const updateCustomColumn = (data) => {
	return {
		type: actionTypes.UPDATE_CUSTOM_COLUMN,
		payload: data
	};
};
export const moveFieldInCanvas = (data) => {
	return {
		type: actionTypes.MOVE_FIELD_IN_CANVAS,
		payload: data
	};
};
export const swapCanvasField = (data) => {
	return {
		type: actionTypes.SWAP_CANVAS_FIELD,
		payload: data
	};
};
export const clearFiledsShelf = (data) => {
	return {
		type: actionTypes.CLEAR_FIELDS_SHELF,
		payload: data
	};
};
export const removeFieldFromCanvas = (data) => {
	return {
		type: actionTypes.REMOVE_FIELD_FROM_CANVAS,
		payload: data
	};
};
export const updateCanvasField = (data) => {
	return {
		type: actionTypes.UPDATE_CANVAS_FIELD,
		payload: data
	};
};
export const updateAggregations = (data) => {
	return {
		type: actionTypes.UPDATE_AGGREGATIONS,
		payload: data
	};
};
export const updateOrderBy = (data) => {
	return {
		type: actionTypes.UPDATE_ORDER_BY,
		payload: data
	};
};
export const hideField = (data) => {
	return {
		type: actionTypes.HIDE_FIELD,
		payload: data
	};
};
export const toggleHidenFields = (data) => {
	return {
		type: actionTypes.TOGGLE_HIDDEN_FIELDS,
		payload: data
	};
};
export const createFilter = (data) => {
	return {
		type: actionTypes.CREATE_FILTER,
		payload: data
	};
};
export const updateFilterAlias = (data) => {
	return {
		type: actionTypes.UPDATE_FILTER_ALIAS,
		payload: data
	};
};
export const toggleFilter = (data) => {
	return {
		type: actionTypes.TOGGLE_FILTER,
		payload: data
	};
};
export const changeFilterCondition = (data) => {
	return {
		type: actionTypes.CHANGE_FILTER_CONDITION,
		payload: data
	};
};
export const updateCustomCondition = (data) => {
	return {
		type: actionTypes.UPDATE_CUSTOM_CONDITION,
		payload: data
	};
};
export const changeFilterSearch = (data) => {
	return {
		type: actionTypes.CHANGE_FILTER_SEARCH,
		payload: data
	};
};
export const loadFilterValues = (data) => {
	return {
		type: actionTypes.FETCH_FILTER_VALUES,
		payload: data
	};
};
export const loadValuesRange = (data) => {
	return {
		type: actionTypes.LOAD_VALUES_RANGE,
		payload: data
	};
};
export const updateFilter = (data) => {
	return {
		type: actionTypes.UPDATE_FILTER,
		payload: data
	};
};
export const updateDatePart = (data) => {
	return {
		type: actionTypes.UPDATE_DATE_PART,
		payload: data
	};
};
export const changeFilterValue = (data) => {
	return {
		type: actionTypes.CHANGE_FILTER_VALUES,
		payload: data
	};
};
export const updateAnchorDate = (data) => {
	return {
		type: actionTypes.UPDATE_ANCHOR_DATE,
		payload: data
	};
};
export const updateFilterMapping = (data) => {
	return {
		type: actionTypes.UPDATE_FILTER_MAPPING,
		payload: data
	};
};
export const toggleFilterVisibility = (data) => {
	return {
		type: actionTypes.TOGGLE_FILTER_VISIBILITY,
		payload: data
	};
};
export const toggleFilterUnique = (data) => {
	return {
		type: actionTypes.TOGGLE_FILTER_UNIQUE,
		payload: data
	};
};
export const toggleFilterIngnorance = (data) => {
	return {
		type: actionTypes.TOGGLE_FILTER_IGNORANCE,
		payload: data
	};
};
export const toggleFloating = (data) => {
	return {
		type: actionTypes.TOGGLE_FLOATING,
		payload: data
	};
};
export const editFieldFunctions = (data) => {
	return {
		type: actionTypes.EDIT_FIELD_FUNCTIONS,
		payload: data
	};
};
export const toggleQueryEditor = (data) => {
	return {
		type: actionTypes.TOGGLE_QUERY_EDITOR,
		payload: data
	};
};
export const saveQuery = (data) => {
	return {
		type: actionTypes.SAVE_QUERY,
		payload: data
	};
};
export const updateFunctionDefination = (data) => {
	return {
		type: actionTypes.UPADATE_FUNCTION_DEFINATION,
		payload: data
	};
};
export const clearDatabaseFunction = (data) => {
	return {
		type: actionTypes.CLEAR_DB_FUNCTION,
		payload: data
	};
};
export const updateColumn = (data) => {
	return {
		type: actionTypes.UPDATE_COLUMN,
		payload: data
	};
};
export const changeEditingPane = (data) => {
	return {
		type: actionTypes.CHANGE_EDITING_PANE,
		payload: data
	};
};
export const deleteFilter = (data) => {
	return {
		type: actionTypes.DELETE_FILTER,
		payload: data
	};
};
export const addDilldownFilter = (data) => {
	return {
		type: actionTypes.ADD_DRILLDOWN_FILTER,
		payload: data
	};
};
export const deleteDilldownFilter = (data) => {
	return {
		type: actionTypes.DELETE_DRILLDOWN_FILTER,
		payload: data
	};
};
export const changeActiveScript = (data) => {
	return {
		type: actionTypes.CHANGE_ACTIVE_SCRIPT,
		payload: data
	};
};
export const addNewScript = (data) => {
	return {
		type: actionTypes.ADD_NEW_SCRIPT,
		payload: data
	};
};
export const deleteScript = (data) => {
	return {
		type: actionTypes.DELETE_SCRIPT,
		payload: data
	};
};
export const changeEditorContent = (data) => {
	return {
		type: actionTypes.CHANGE_EDITOR_CONTENT,
		payload: data
	};
};
export const applyReportScripts = (data) => {
	return {
		type: actionTypes.APPLY_REPORT_SCRIPTS,
		payload: data
	};
};
export const changeOptions = (data) => {
	return {
		type: actionTypes.CHANGE_OPTIONS,
		payload: data
	};
};
export const updateSubVizType = (data) => {
	return {
		type: actionTypes.UPDATE_SUB_VIZ_TYPE,
		payload: data
	};
};
export const addFieldToMarks = (data) => {
	return {
		type: actionTypes.ADD_FIELD_TO_MARKS,
		payload: data
	};
};
export const removeFieldFromMarks = (data) => {
	return {
		type: actionTypes.REMOVE_FIELD_FROM_MARKS,
		payload: data
	};
};
export const changeInteractivity = (data) => {
	return {
		type: actionTypes.CHANGE_INTERACTIVITY,
		payload: data
	};
};
export const toggleCombineCharts = (data) => {
	return {
		type: actionTypes.TOGGLE_COMBINE_CHARTS,
		payload: data
	};
};
export const loadReportState = (data) => {
	return {
		type: actionTypes.LOAD_REPORT_STATE,
		payload: data
	};
};
export const loadDrillReport = (data) => {
	return {
		type: actionTypes.LOAD_DRILL_REPORT,
		payload: data
	};
};
export const loadReportData = (data) => {
	return {
		type: actionTypes.LOAD_REPORT_DATA,
		payload: data
	};
};
export const updateSqlQuery = (data) => {
	return {
		type: actionTypes.UPDATE_SQL_QUERY,
		payload: data
	};
};
export const updatePreviewState = (data) => {
	return {
		type: actionTypes.UPDATE_PREVIEW_STATE,
		payload: data
	};
};
export const updateFullScreenState = (data) => {
	return {
		type: actionTypes.UPDATE_FULLSCREEN_STATE,
		payload: data
	};
};
export const updateReportFile = (data) => {
	return {
		type: actionTypes.UPDATE_REPORT_FILE,
		payload: data
	};
};

export const setMenuData = (data) => {
	return {
		type: actionTypes.SET_MENU_DATA,
		payload: data
	};
};
export const updateDrillThroughList = (data) => {
	return {
		type: actionTypes.UPDATE_DRILL_THROUGH_LIST,
		payload: data
	};
};
export const removeDrillReport = (data) => {
	return {
		type: actionTypes.REMOVE_DRILL_REPORT,
		payload: data
	};
};
export const updateReportProperty = (data) => {
	return {
		type: actionTypes.UPDATE_REPORT_PROPERTY,
		payload: data
	};
};
export const updateReportLayout = (data) => {
	return {
		type: actionTypes.UPDATE_REPORT_LAYOUT,
		payload: data
	};
};
export const updateAnalytics = (data) => {
	return {
		type: actionTypes.UPDATE_ANALYTICS,
		payload: data
	};
};
export const saveGetCubeRes = (data) => {
	return {
		type: actionTypes.GET_CUBE_RES,
		payload: data
	};
};

export const setHrSidebar = (data) => {
	return {
		type: actionTypes.HR_SIDEBAR,
		payload: data
	};
};

export const hreportUndo = () => {
	return { type: actionTypes.HREPORT_UNDO };
};

export const hreportRedo = () => {
	return { type: actionTypes.HREPORT_REDO };
};

export const setMetadataLoading = (data) => {
	return { type: actionTypes.SET_METADATA_LOADING, payload: data }
}

export const setHReportLoading = (data) => {
	return { type: actionTypes.SET_HREPORT_LOADING, payload: data }
}

export const setHreportStylesId = (data) => {
	return { type: actionTypes.SET_HREPORT_STYLES_ID, payload: data }
}

export const saveHreportStyles = (data) => {
	return { type: actionTypes.SAVE_HREPORT_STYLES, payload: data }
}

export const updateGeoJsonData = (data) => {
	return {
		type: actionTypes.UPDATE_GEO_JSON_DATA,
		payload: data
	};
};

export const updateGeographicType = (data) => {
	return {
		type: actionTypes.UPDATE_GEOGRAPHIC_TYPE,
		payload: data
	};
};
export const setChildReportLoading = (boolean) => {
	return { type: actionTypes.SET_CHILD_REPORT_LOADING, payload: boolean }
}
export const clearUndoRedoHistory = () => {
	return {
		// type: '@@redux-undo/CLEAR_HISTORY' 6105 - fix
		type: actionTypes.CLEAR_HREPORT_HISTORY
	}
}

export const isSavingHreports = (bool) => {
	return {
		type: actionTypes.REPORT_IS_SAVING,
		payload: bool,
	};
};

export const setHreportSidebarLoading = (data) => {
	return { type: actionTypes.SET_HREPORT_SIDEBAR_LOADING, payload: data }
}

export const removeAggregateForAdvanceFilter = (data) => {
	return {
		type: actionTypes.REMOVE_ADVANCE_AGGREGATE_FROM_REORT, payload: data
	}
}

export const setHReportEditLoading = (data) => {
	return { type: actionTypes.SET_HREPORT_EDIT_LOADING, payload: data }
}

export const setAbortRequest = (data) => {
	return { type: actionTypes.ABORT_REQUEST, payload: data }
}

export const resetTableProperty = (data) => {
	return { type: actionTypes.RESET_TABLE_PROPERTY, payload: data }
}

export const changeReferenceLine = (data) => {
	return { type: actionTypes.CHANGE_REFERENCE_LINE_VALUE, payload: data }
}

export const addConvertedDimensionToMarks = (data) => {
	return { type: actionTypes.ADD_CONVERTED_DIMENSION_TO_MARKSLIST, payload: data }
}

export const updateReferenceLineValue = (data) => {
	return { type: actionTypes.UPDATE_REFERENCE_LINE_VALUE, payload: data }
}

export const updateCustomChart = (data) => {
	return { type: actionTypes.UPDATE_CUSTOM_CHART, payload: data }
}

export const resetCustomChart = (data) => {
	return { type: actionTypes.RESET_CUSTOM_CHART, payload: data }
}

export const updateMapChartProperties = (data) => {
	return { type: actionTypes.UPDATE_MAP_CHART_PROPERTIES, payload: data }
}

export const reRenderGridChart = (data) => {
	return { type: actionTypes.RE_RENDER_GRID_CHART, payload: data }
}

export const addConvertedDimensionToReference = (data) => {
	return { type: actionTypes.ADD_CONVERTED_DIMENSION_TO_REFERENCELIST, payload: data }
}

export const changeTableRecordsPerPage = (data) => {
	return { type: actionTypes.CHANGE_TABLE_RECORDS_PER_PAGE, payload: data }
}

export const enableMeasureMarks = (data) => {
	return { type: actionTypes.ENABLE_MEASURE_MARKS, payload: data }
}

export const resetActiveReport = (data) => {
	return {
		type: actionTypes.RESET_ACTIVE_REPORT,
		payload: data
	};
};

export const changeDateFilterFormat = (data) => {
	return { type: actionTypes.CHANGE_DATE_FILTER_FORMAT, payload: data }
}

export const addCustomDisplayDateFormat = (data) => {
	return { type: actionTypes.ADD_CUSTOM_DISPLAY_DATE_FORMAT, payload: data }
}

export const updateCustomDisplayDateFormat = (data) => {
	return { type: actionTypes.UPDATE_CUSTOM_DISPLAY_DATE_FORMAT, payload: data }
}

export const updateGridItemsLayout = (data) => {
	return {
		type: actionTypes.UPDATE_GRID_ITEMS_LAYOUT,
		payload: data
	};
};

export const updateReportModal = (data) => {
	return {
		type: actionTypes.UPDATE_REPORT_MODAL,
		payload: data
	}
}

export const resetReportsFilters = () => {
	return {
		type: actionTypes.RESET_REPORTS_FILTERS,
	}
}

export const addCustomChartColorPalette = (data) => {
	return { type: actionTypes.ADD_CUSTOM_CHART_COLOR_PALETTE, payload: data }
}

export const deleteCustomChartColorPalette = (data) => {
	return { type: actionTypes.DELETE_CUSTOM_CHART_COLOR_PALETTE, payload: data }
}

export const removeCustomChartColorPalette = (data) => {
	return { type: actionTypes.REMOVE_CUSTOM_CHART_COLOR_PALETTE, payload: data }
}

export const addMeasureField = (data) => {
	return { type: actionTypes.ADD_MEASURE_FIELD, payload: data }
}

export const removeDrillthroughChildData = (data) => {
	return { type: actionTypes.REMOVE_DRILLTHROUGH_CHILD_REPORT_DATA, payload: data }
}

export const setDrillthroughActive = (data) => {
	return { type: actionTypes.SET_DRILLTHROUGH_ACTIVE, payload: data }
}


export const copyColorPaletteToOtherReports = (data) => {
	return { type: actionTypes.COPY_COLOR_PALETTE_TO_OTHER_REPORTS, payload: data }
}

export const updateTrendField = (data) => {
	return {
		type: actionTypes.UPDATE_TREND_FIELD,
		payload: data
	}
}

export const updateFormatProperty = (data) => {
	return {
		type: actionTypes.UPDATE_FORMAT_PROPERTY,
		payload: data
	}
}


export const addVersionToReport = (data) => {
	return {
		type: actionTypes.ADD_VERSION_TO_REPORT,
		payload: data
	}
}

export const setShowAllVizualizations = (data) => {
	return {
		type: actionTypes.SHOW_ALL_VIZUALIZATIONS,
		payload: data
	}
}

export const updateCardProperties = (data) => {
	return { type: actionTypes.UPDATE_CARD_PROPERTIES, payload: data }
}

export const setEnableDrillthroughReportLink = (data) => {
	return {
		type: actionTypes.ENABLE_DRILLTHROUGH_REPORT_LINK,
		payload: data
	};
};

export const updateTableFilters = (data) => {
	return {
		type: actionTypes.UPDATE_TABLE_FILTERS,
		payload: data
	};
};