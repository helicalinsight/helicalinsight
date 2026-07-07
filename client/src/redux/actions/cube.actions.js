import actionTypes from './actionTypes';

export const saveCubeMetadataFileDetails = (data) => {
	return {
		type: actionTypes.CUBE_METADATA_FILE_DETAILS,
		payload: data
	};
};

export const setCubeState = (data = '') => {
	return {
		type: actionTypes.CUBE_STATE,
		payload: data
	};
};

export const setCubeHeaderData = (data = {}) => {
	return {
		type: actionTypes.CUBE_HEADER_DATA,
		payload: data
	};
};

export const setFieldSearchText = (data = '') => {
	return {
		type: actionTypes.SEARCH_FIELD_TEXT,
		payload: data
	};
};

export const setCubeSearchedColumn = (data = '') => {
	return {
		type: actionTypes.CUBE_SEARCHED_COLUMN,
		payload: data
	};
};

export const setCubeFieldsData = (data = {}) => {
	return {
		type: actionTypes.CUBE_FIELDS_DATA,
		payload: data
	};
};

export const setCubeIndeterminate = (data) => {
	return {
		type: actionTypes.CUBE_VISIBLE_INDETERMINATE,
		payload: data
	};
};

export const setVisibleCheckValue = (data) => {
	return {
		type: actionTypes.CUBE_VISIBLE_VAL,
		payload: data
	};
};

export const updateFieldValues = (data = {}) => {
	return {
		type: actionTypes.CUBE_UPDATE_FIELD_VALUES,
		payload: data
	};
};

export const setCubeInintialList = (data = {}) => {
	return {
		type: actionTypes.CUBE_INITIAL_LIST,
		payload: data
	};
};

export const setCubeMode = (data = '') => {
	return {
		type: actionTypes.CUBE_MODE,
		payload: data
	};
};

// export const setCubeTableDeleteKeys = (data = '') => {
// 	return {
// 		type: actionTypes.CUBE_TABLE_DELETE_KEYS,
// 		payload: data
// 	};
// };

export const setCubeTableMode = (data = '') => {
	return {
		type: actionTypes.CUBE_TABLE_MODE,
		payload: data
	};
};

export const setCubeMetadataTablesData = (data = {}) => {
	return {
		type: actionTypes.CUBE_METADATA_TABLES_DATA,
		payload: data
	};
};

export const modifyHierarchy = (data = {}) => {
	return {
		type: actionTypes.CUBE_HIERARCHY_MODIFICATION,
		payload: data
	};
};

export const setCubeFieldsDataBackup = (data = {}) => {
	return {
		type: actionTypes.CUBE_FIELDS_DATA_BACKUP,
		payload: data
	};
};

export const savedCubeDetailsForEdit = (data = {}) => {
	return {
		type: actionTypes.SAVE_CUBE_DETAILS_FOR_EDIT,
		payload: data
	};
};

export const cubeFileDataAfterSave = (data = {}) => {
	return {
		type: actionTypes.CUBE_DATA_AFTER_SAVE,
		payload: data
	};
};

export const cubeLocalResetter = () => {
	return {
		type: actionTypes.CUBE_LOCAL_RESET
	};
};

export const onExpandCubeTable = (data) => {
	return {
		type: actionTypes.ON_EXPAND_CUBE_TABLE,
		payload: data
	};
};
