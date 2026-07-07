import actionTypes from './actionTypes';

export const agentLocalResetter = () => {
	return {
		type: actionTypes.AGENT_LOCAL_RESET
	};
};

export const saveAgentMetadataFileDetails = (data) => {
	return {
		type: actionTypes.AGENT_METADATA_FILE_DETAILS,
		payload: data
	};
};

export const setAgentMetadataTablesData = (data = {}) => {
	return {
		type: actionTypes.AGENT_METADATA_TABLES_DATA,
		payload: data
	};
};

export const setAgentMode = (data = '') => {
	return {
		type: actionTypes.AGENT_MODE,
		payload: data
	};
};

export const agentFileDataAfterSave = (data = {}) => {
	return {
		type: actionTypes.AGENT_DATA_AFTER_SAVE,
		payload: data
	};
};

export const onExpandAgentTable = (data) => {
	return {
		type: actionTypes.ON_EXPAND_AGENT_TABLE,
		payload: data
	};
};
