import actionTypes from './actionTypes';

export const getDataSources = (data) => {
	return {
		type: actionTypes.METADATA.SERVICE_GET_DATASOURCE,
		payload: data
	};
};

export const updateFetchedDataSources = (data) => {
	return {
		type: actionTypes.METADATA.UPDATE_FETCHED_DATASOURCES,
		payload: data
	};
};

export const updateLoadingStatus = (data) => {
	return {
		type: actionTypes.METADATA.UPDATE_LOADING_STATUS,
		payload: data
	};
};

export const fetchListDataSources = (data) => {
	return {
		type: actionTypes.METADATA.FETCH_LIST_DATASOURCES,
		payload: data
	};
};
export const updateWorkflowDataList = (data) => {
	return {
		type: actionTypes.METADATA.UPDATE_WORKFLOW_DATALIST,
		payload: data
	};
};

export const updateMetadataState = (data) => {
	return {
		type: actionTypes.METADATA.UPDATE_METADATA_STATE,
		payload: data
	};
};

export const updateMetadataTables = (data) => {
	return {
		type: actionTypes.METADATA.UPDATE_METADATA_TABLES,
		payload: data
	};
};

export const updateActiveEditorTab = (data) => {
	return {
		type: actionTypes.METADATA.UPDATE_ACTIVE_EDITOR_TAB,
		payload: data
	};
};

export const resetMetadataState = (data) => {
	return {
		type: actionTypes.METADATA.RESET_METADATA_STATE,
		payload: data
	};
};

export const updateDataSourceInfo = (data) => {
	return {
		type: actionTypes.METADATA.UPDATE_DATASOURCE_INFO,
		payload: data
	};
};

export const resetDuplicateData = (data) => {
	return {
		type: actionTypes.METADATA.RESET_DUPLICATE_DATA,
		payload: data
	};
};

export const updateJoinsData = (data) => {
	return {
		type: actionTypes.METADATA.UPDATE_JOINS_DATA,
		payload: data
	};
};

export const metadataDetailsToEdit = (data) => {
	return {
		type: actionTypes.METADATA.METADATA_DETAILS_TO_EDIT,
		payload: data
	};
};
export const initialStateForJest = (data) => {
	return {
		type: actionTypes.METADATA.INITIAL_STATE_FOR_JEST,
		payload: data
	};
};

export const setIntialEditResponse = (data) => {
	return {
		type: actionTypes.METADATA.INITIAL_EDIT_RESPONSE,
		payload: data
	};
};

export const setServiceErrorStatus = (data) => {
	return {
		type: actionTypes.METADATA.SERVICE_ERROR_STATUS,
		payload: data
	};
};

export const setSelectedTableOrColumnKey = (data) => {
	return {
		type: actionTypes.METADATA.SELECTED_TABLE_OR_COLUMN_KEY,
		payload: data
	};
};

export const saveExpressionData = (data) => {
	return {
		type: actionTypes.METADATA.SAVE_EXPRESSION_ID,
		payload: data
	};
};

export const metadataSecurityData = (data) => {
	return {
		type: actionTypes.METADATA.SECURITY_CONSTANTS,
		payload: data
	};
};

export const setSecurityEdit = (data) => {
	return {
		type: actionTypes.METADATA.SECURITY_EDIT,
		payload: data
	};
};

export const deleteView = (data) => {
	return {
		type: actionTypes.METADATA.DELETE_VIEW,
		payload: data
	};
};

export const toggleMDEditor = (data = {}) => {
	return {
		type: actionTypes.METADATA.TOGGLE_MD_EDITOR_VIEW,
		payload: data
	};
};

export const setShowValidatedTable = (data = false) => {
	return {
		type: actionTypes.METADATA.IS_VALIDATED_TABLE_SHOW,
		payload: data
	};
};

export const setSecurityTableData = (data = []) => {
	return {
		type: actionTypes.METADATA.SECURITY_TABLE_DATA,
		payload: data
	};
};

export const handleAddOneMoreSecurity = (data = false) => {
	return {
		type: actionTypes.METADATA.ONE_MORE_SECURITY,
		payload: data
	};
};

export const handleTextEditingObj = (data = {}) => {
	return {
		type: actionTypes.METADATA.TEXT_EDITING_OBJ,
		payload: data
	};
};

export const handleJoinHighlight = (data = {}) => {
	return {
		type: actionTypes.METADATA.JOIN_HIGHLIGHT,
		payload: data
	};
};

export const setFlterbyData = (data = {}) => {
	return {
		type: actionTypes.METADATA.MENU_FILTER,
		payload: data
	};
};

export const setFirstRender = (data) => {
	return {
		type: actionTypes.METADATA.FIRST_RENDER,
		payload: data
	};
};

export const setFormData = (data = {}) => {
	return {
		type: actionTypes.METADATA.SECURITY_FORM_DATA,
		payload: data
	};
};

export const setAccessType = (data = '') => {
	return {
		type: actionTypes.METADATA.ACCESS_TYPE,
		payload: data
	};
};

export const setEntityNames = (data = '') => {
	return {
		type: actionTypes.METADATA.ENTITY_NAMES,
		payload: data
	};
};

export const setExecutionType = (data = '') => {
	return {
		type: actionTypes.METADATA.EXECUTION_TYPE,
		payload: data
	};
};

export const setExpressionName = (data = '') => {
	return {
		type: actionTypes.METADATA.EXPRESSION_NAME,
		payload: data
	};
};

export const setExpressionType = (data = '') => {
	return {
		type: actionTypes.METADATA.EXPRESSION_TYPE,
		payload: data
	};
};

export const setIsApplyDisabled = (data) => {
	return {
		type: actionTypes.METADATA.SET_APPLY_BTN_STATE,
		payload: data
	};
};

export const setIsInfoShow = (data) => {
	return {
		type: actionTypes.METADATA.IS_INFO_SHOW,
		payload: data
	};
};

export const setSecurityKeysChecked = (data) => {
	return {
		type: actionTypes.METADATA.SECURITY_KEYS_CHECKED,
		payload: data
	};
};

export const updateTableKeys = (data) => {
	return {
		type: actionTypes.METADATA.UPDATE_TABLE_KEYS,
		payload: data
	};
};

export const setGetSecurityTableData = (data) => {
	return {
		type: actionTypes.METADATA.GETSECURITY_TABLE_DATA,
		payload: data
	};
};

export const setDoResetFormData = (data) => {
	return {
		type: actionTypes.METADATA.SET_RESET_FORMDATA,
		payload: data
	};
};

export const afterMetadataSave = (data) => {
	return {
		type: actionTypes.METADATA.AFTER_METADATA_SAVE,
		payload: data
	};
};

export const updateActiveDsInfoId = (data) => {
	return {
		type: actionTypes.METADATA.UPDATE_ACTIVE_DS_INFO_ID,
		payload: data
	}
}

export const updateLoadingStatusForJoins = data => {
	return {
		type: actionTypes.METADATA.JOINS_LOADING_STATUS,
		payload: data
	}
};

export const setMetadataLoadingStatus = (payload) => {
	return {
		type: actionTypes.METADATA.SET_METADATA_LOADING_STATUS,
		payload
	};
};

export const updateViewsConnId = data => {
	return {
		type: actionTypes.METADATA.VIEWS_CONN_ID,
		payload: data
	}
}

export const updateTablesWithNewViews = data => {
	return {
		type: actionTypes.METADATA.UPDATE_TABLES_WITH_NEW_VIEWS,
		// payload: data
	}
}

export const createJoinAction = data => {
	return {
		type: actionTypes.METADATA.CREATE_JOIN,
		payload: data
	}
}

export const updateJoinAction = data => {
	return {
		type: actionTypes.METADATA.UPDATE_JOIN,
		payload: data
	}
}

export const deleteSelectedJoinsAction = data => {
	return {
		type: actionTypes.METADATA.DELETE_SELECTED_JOINS,
		payload: data
	}
}

export const deleteInvalidJoinsAction = data => {
	return {
		type: actionTypes.METADATA.DELETE_INVALID_JOINS,
		payload: data
	}
}

export const discardJoinAction = data => {
	return {
		type: actionTypes.METADATA.DISCARD_JOIN,
		payload: data
	}
}

export const updateJoinsWithChangedDS = data => {
	return {
		type: actionTypes.METADATA.UPDATE_JOINS_WITH_CHANGED_DS,
		// payload: data
	}
}

export const updateTablesJoinsWithChangedDS = data => {
	return {
		type: actionTypes.METADATA.UPDATE_TABLES_JOINS_WITH_CHANGED_DS,
		// payload: data
	}
}

export const addDuplicateTable = data => {
	return {
		type: actionTypes.METADATA.ADD_DUPLICATE_TABLE,
		payload: data
	}
}

export const updateTable = data => {
	//updates changed tables (alias change name) in store.metadata.changeTables (for formData). Does not update tables data directly. 
	return {
		type: actionTypes.METADATA.UPDATE_TABLE,
		payload: data
	}
}

export const updateViewAlias = data => {
	return {
		type: actionTypes.METADATA.UPDATE_VIEW_ALIAS,
		payload: data
	}
}

export const setShowJoinModal = data => {
	return {
		type: actionTypes.METADATA.IS_SHOW_JOIN_MODAL,
		payload: data
	}
}

const resetChangedTables = () => {
	return {
		type: actionTypes.METADATA.RESET_CAHNGED_TABLES_DATA,
	}
}

export const removedTablesState = (payload) => {
	return {
		type: actionTypes.METADATA.UPDATE_REMOVED_TABLES_STATE,
		payload
	}
}

export const isAfterSaveMode = (payload) => {
	return {
		type: actionTypes.METADATA.IS_AFTER_SAVE_MODE,
		payload
	}
}

export const setViewFetching = (payload) => {
	return {
		type: actionTypes.METADATA.VIEW_FETCHING,
		payload
	}
}

export const metadataUndo = () => {
	return {
		type: actionTypes.METADATA.METADATA_UNDO
	} 
}

export const metadataRedo = () => {
	return {
		type: actionTypes.METADATA.METADATA_REDO
	} 
}
export const clearUndoRedoHistory = () => {
	return {
		type: '@@redux-undo/CLEAR_HISTORY'
	}
}

export const addColumnsToTable = (payload) => {
	return {
		type: actionTypes.METADATA.ADD_COLUMNS_TO_TABLE,
		payload
	}
}

export const doGetSecurityCall = (payload) => {
	return {
		type: actionTypes.METADATA.IS_GET_SECURITY_SERVICE_DONE,
		payload
	}
}

export const handleDSExpanedRowKeys = (payload) => {
	return {
		type: actionTypes.METADATA.DATASOURCE_EXPANDED_ROW_KEYS,
		payload
	}
}

export const handleMetadataSectionRowsExpand = (payload) => {
	return {
		type: actionTypes.METADATA.METADATA_SECTION_EXPANDED_ROWS,
		payload
	}
}

export const handleMetadataSavingProgress = (payload) => {
	return {
		type: actionTypes.METADATA.METADATA_SAVING_PROGRESS,
		payload
	}
}

export const handleUpdateStoreWithFetchedMetadata = (data) => {
	return {
		type: actionTypes.METADATA.UPDATE_STORE_WITH_FETCHED_METADATA_STATE,
		payload: data
	};
};

export const setViewName = (data) => {
	return {
		type: actionTypes.METADATA.SET_VIEW_NAME,
		payload: data
	};
};

export const setConditionAndFilterValue = (data) => {
	return {
		type: actionTypes.METADATA.SET_CONDITION_AND_FILTER_VALUE,
		payload: data
	};
};

export const updateSecurityItemsOnExpEdit = (data) => {
	return {
		type: actionTypes.METADATA.UPDATE_SECURITY_ITEMS_ON_EXP_EDIT,
		payload: data
	};
};

export const metadataUndoRedoPurpose = (data) => {
	return {
		type: actionTypes.METADATA.UNDO_REDO_PURPOSE,
		payload: data
	};
};

export const metadataSecurityDeleteSelectedAll = (data) => {
	return {
		type: actionTypes.METADATA.EXPRESSION_DELETE_SELECTED,
		payload: data
	};
};

export const metadataOutsideClicked = (data) => {
	return {
		type: actionTypes.METADATA.OUTSIDE_CLICKED,
		payload: data
	};
};


export const metadataActions = {
	metadataOutsideClicked,
	metadataSecurityDeleteSelectedAll,
	metadataUndoRedoPurpose,
	updateSecurityItemsOnExpEdit,
	setConditionAndFilterValue,
	setViewName,
	handleUpdateStoreWithFetchedMetadata,
	handleMetadataSavingProgress,
	handleMetadataSectionRowsExpand,
	handleDSExpanedRowKeys,
	doGetSecurityCall,
	setViewFetching,
	setShowJoinModal,
	discardJoinAction,
	updateJoinsWithChangedDS,
	updateTablesJoinsWithChangedDS,
	deleteInvalidJoinsAction,
	deleteSelectedJoinsAction,
	updateJoinAction,
	createJoinAction,
	updateTablesWithNewViews,
	updateViewsConnId,
	afterMetadataSave,
	setGetSecurityTableData,
	handleJoinHighlight,
	handleTextEditingObj,
	setSecurityEdit,
	metadataSecurityData,
	saveExpressionData,
	updateWorkflowDataList,
	updateMetadataState,
	updateMetadataTables,
	updateActiveEditorTab,
	resetMetadataState,
	updateDataSourceInfo,
	resetDuplicateData,
	updateJoinsData,
	metadataDetailsToEdit,
	updateLoadingStatus,
	initialStateForJest,
	setIntialEditResponse,
	setServiceErrorStatus,
	setSelectedTableOrColumnKey,
	deleteView,
	toggleMDEditor,
	setShowValidatedTable,
	setSecurityTableData,
	handleAddOneMoreSecurity,
	setFlterbyData,
	setFirstRender,
	setFormData,
	setAccessType,
	setEntityNames,
	setExecutionType,
	setExpressionName,
	setExpressionType,
	setIsApplyDisabled,
	setIsInfoShow,
	setSecurityKeysChecked,
	updateTableKeys,
	updateActiveDsInfoId,
	updateLoadingStatusForJoins,
	setMetadataLoadingStatus,
	addDuplicateTable,
	resetChangedTables,
	updateTable,
	removedTablesState,
	isAfterSaveMode,
	metadataRedo,
	metadataUndo,
	clearUndoRedoHistory,
	addColumnsToTable
};
