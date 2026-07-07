// create = (store,reducer,data) =>{
//     return  {store,reducer,data}
// }
// update = (store,reducer,data) =>{
//     return  {store,reducer,data}
// }
// remove = (store,reducer,data) =>{
//     return  {store,reducer,data}
// }

// Overview page actions
import actionTypes from './actionTypes';

export const setIsAdminTabsDataSet = (payload) => {
	return { type: actionTypes.ISADMINTABSDATASET, payload };
};

export const storeDiskData = (payload) => {
	return { type: actionTypes.STORE_DISK_DATA, payload };
};

export const storeJVMData = (payload) => {
	return { type: actionTypes.STORE_JVM_DATA, payload };
};
export const storeTempData = (payload) => {
	return { type: actionTypes.STORE_TEMP_DATA, payload };
};
export const storeCacheSize = (payload) => {
	return { type: actionTypes.STORE_CACHE_SIZE, payload };
};
export const storeCachedReports = (payload) => {
	return { type: actionTypes.STORE_CACHED_REPORTS, payload };
};

export const storeCachedDataSourcesData = (payload) => {
	return {
		type: actionTypes.STORE_CACHED_DATASOURCES,
		payload
	};
};

export const storeCurrentLevelData = (payload) => {
	return {
		type: actionTypes.STORE_CURRENT_LEVEL,
		payload
	};
};

export const storeLogOptionsData = (payload) => {
	return {
		type: actionTypes.STORE_LOG_OPTIONS,
		payload
	};
};

export const storeProductData = (payload) => {
	return {
		type: actionTypes.STORE_PRODUCT_DATA,
		payload
	};
};

export const storeReloadAppData = (payload) => {
	return {
		type: actionTypes.STORE_RELOAD_APPDATA,
		payload
	};
};

export const storeReloadValidData = (payload) => {
	return {
		type: actionTypes.STORE_RELOAD_VALIDDATA,
		payload
	};
};

export const storeReloadCacheData = (payload) => {
	return {
		type: actionTypes.STORE_RELOAD_CACHE,
		payload
	};
};

export const updateIsFetched = (payload) => {
	return { type: actionTypes.UPDATE_IS_FETCHED, payload };
};

export const storeReportStats = (payload) => {
	return { type: actionTypes.STORE_REPORT_STATS, payload };
};

export const storeDataSourceCount = (payload) => {
	return { type: actionTypes.STORE_DATA_SOURCE_COUNT, payload };
};

// System Details Actions

export const storeOsData = (payload) => {
	return {
		type: actionTypes.STORE_OS_DETAILS,
		payload
	};
};

export const storeJvmThreadData = (payload) => {
	return {
		type: actionTypes.STORE_JVM_THREAD_DATA,
		payload
	};
};

export const storeSystemDetailsOsTableExpand = (payload) => {
	return {
		type: actionTypes.SYSTEMDETAILS_OS_TABLE_EXPAND,
		payload
	};
};

export const storeSystemDetailsJvmTableExpand = (payload) => {
	return {
		type: actionTypes.SYSTEMDETAILS_JVM_TABLE_EXPAND,
		payload
	};
};

//Plugins Actions

export const storePluginsData = (payload) => {
	return {
		type: actionTypes.STORE_PLUGINS_DATA,
		payload
	};
};

// User Management Actions

export const storeUserData = (userData) => {
	return { type: actionTypes.STORE_USER_DATA, payload: userData };
};
export const storeOrgData = (orgData) => {
	return { type: actionTypes.STORE_ORG_DATA, payload: orgData };
};
export const storeRoleData = (orgData) => {
	return { type: actionTypes.STORE_ROLE_DATA, payload: orgData };
};

// export const addHIUMRowKey = (id) => {
//   return { type: "addHIUMRowKey", payload: id };
// };
// export const deleteHIUMRowKey = (id) => {
//   return { type: "deleteHIUMRowKey", payload: id };
// };

export const updateOrgList = (obj) => {
	return { type: actionTypes.UPDATE_ORG_LIST, payload: obj };
};

export const updateSelectedFunctionalities = (id, functionality) => {
	return {
		type: actionTypes.UPDATE_SELECTED_FUNCTIONALITIES,
		payload: { id, functionality }
	};
};
export const setProfileData = (id, obj) => {
	return { type: actionTypes.SET_PROFILE_DATA, payload: { id1: id, obj } };
};
export const updateRolesList = (obj) => {
	return { type: actionTypes.UPDATE_ROLES_LIST, payload: obj };
};
export const updateVisibleDrawers = (obj) => {
	return (dispatch) => {
		return new Promise((resolve) => {
			dispatch({ type: actionTypes.UPDATE_VISIBLE_DRAWERS, payload: obj });

			resolve();
		});
	};
};
export const updateEditUser = (obj) => {
	return { type: actionTypes.UPDATE_EDIT_USER, payload: obj };
};
export const deleteOrgItem = (orgId) => {
	return { type: actionTypes.DELETE_ORG_ITEM, payload: orgId };
};
export const deleteRoleItem = (roleId) => {
	return { type: actionTypes.DELETE_ROLE_ITEM, payload: roleId };
};
export const deleteUserItem = (userId) => {
	return { type: actionTypes.DELETE_USER_ITEM, payload: userId };
};
export const deleteProfileItem = (obj) => {
	return { type: actionTypes.DELETE_PROFILE_ITEM, payload: obj };
};
export const addOrgItem = (obj) => {
	return { type: actionTypes.ADD_ORG_ITEM, payload: obj };
};
export const addProfileItem = (obj) => {
	return { type: actionTypes.ADD_PROFILE_ITEM, payload: obj };
};
export const addRoleItem = (obj) => {
	return { type: actionTypes.ADD_ROLE_ITEM, payload: obj };
};
export const addUserItem = (obj) => {
	return { type: actionTypes.ADD_USER_ITEM, payload: obj };
};

//Management Module

export const updateManagementData = (data) => {
	return { type: actionTypes.UPDATE_MANAGEMENT_DATA, payload: data };
};

export const updateManagementStaticData = (data) => {
	return { type: actionTypes.UPDATE_MANAGEMENT_STATIC_DATA, payload: data };
};

export const updateManagementDiceApiStatus = (data) => {
	return {type: actionTypes.UPDATE_MANAGEMENT_DICE_API_STATUS, payload:data}
}

export const updateManagementAdvancedData = (data) => {
	return { type: actionTypes.UPDATE_MANAGEMENT_ADVANCED_DATA, payload: data };
};

export const updateNoSqlFileData = (data) => {
	return { type: actionTypes.UPDATE_NO_SQL_DATA, payload: data };
};

export const modifyAdvancedData = (data) => {
	return { type: actionTypes.MODIFY_ADVANCED_DATA, payload: data };
};

//schedule module

export const updateScheduledList = (data) => {
	return { type: actionTypes.UPDATE_SCHEDULED_LIST, payload: data };
};

// adfs module
export const storeMetadataAdministrationData = (data) => {
	return {
		type: actionTypes.STORE_METADATA_ADMINISTRATION_DATA,
		payload: data
	};
};

export const storeMetadataGenerationData = (data) => {
	return {
		type: actionTypes.STORE_METADATA_GENERATION_DATA,
		payload: data
	};
};

export const storeMetadataPreviewData = (data) => {
	return {
		type: actionTypes.STORE_METADATA_PREVIEW_DATA,
		payload: data
	};
};

export const updateEntityId = (string) => {
	return {
		type: actionTypes.UPDATE_ENTITY_ID,
		payload: string
	};
};

export const storeMetadataGenerationFormValues = (obj) => {
	return {
		type: actionTypes.STORE_METADATA_GENERATION_FORM_VALUES,
		payload: obj
	};
};

export const toggleMetadataContents = (obj) => {
	return {
		type: actionTypes.TOGGLE_METADATA_CONTENTS,
		payload: obj
	};
};
//  ------------   DICE STORAGE  ------------------------

export const saveDiceStorageMetadataFileDetails = (data) => {
	return {
		type: actionTypes.DICE_STORAGE_METADATA_FILE_DETAILS,
		payload: data
	};
};

export const setDiceStorageTabName = (data) => {
	return {
		type: actionTypes.DICE_STORAGE_TAB_NAME,
		payload: data
	};
};

export const setDiceStorageTableData = (data) => {
	return {
		type: actionTypes.DICE_STORAGE_TABLE_DATA,
		payload: data
	};
};

export const setDiceStorageFieldSearchText = (data = '') => {
	return {
		type: actionTypes.DICE_STORAGE_FIELD_SEARCH_TEXT,
		payload: data
	};
};

export const setDiceStorageSearchedColumn = (data = '') => {
	return {
		type: actionTypes.DICE_STORAGE_SEARCHED_COLUMN,
		payload: data
	};
};

export const diceStorageMetadataDumpList = (data = '') => {
	return {
		type: actionTypes.DICE_STORAGE_METADATA_DUMP_LIST,
		payload: data
	};
};

export const updateDiceStorageTableRow = (data = {}) => {
	return {
		type: actionTypes.UPDATE_DICE_STORAGE_TABLE_ROW,
		payload: data
	};
};

export const loadSkeleton = (data = '') => {
	return {
		type: actionTypes.DICE_STORAGE_SKELETON,
		payload: data
	};
};

export const storeWhatsNewContent=(data = {})=>{
	return {
		type: actionTypes.STORE_WHATSNEW_CONTENT,
		payload: data
	};

}

export const handleRecycleBinData = (payload) => {
	return { type: actionTypes.RECYCLEBIN_DATA, payload };
};