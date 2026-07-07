import { v4 as uuidv4 } from 'uuid';
import actionTypes from '../actions/actionTypes';
import initialStates from './initialStates';

export const agentReducer = (state = initialStates.agentInitialState, action) => {
    switch (action.type) {
        case actionTypes.RESET_STORE:
        case actionTypes.AGENT_LOCAL_RESET:
            return { ...initialStates.agentInitialState };
        case actionTypes.ON_EXPAND_AGENT_TABLE: {
            let { key } = action.payload || {};
            let defaultexpandedRowKeys = [ ...(state.metadataTablesData.defaultexpandedRowKeys || []) ];
            if (defaultexpandedRowKeys.lenght) {
                if (defaultexpandedRowKeys.includes(key)) {
                    defaultexpandedRowKeys = defaultexpandedRowKeys.filter((item) => item !== key);
                } else {
                    defaultexpandedRowKeys.push(key);
                }
            } else {
                defaultexpandedRowKeys = [ key ];
            }
            return {
                ...state,
                metadataTablesData: { ...state.metadataTablesData, defaultexpandedRowKeys }
            };
        }
        case actionTypes.AGENT_DATA_AFTER_SAVE: {
            return {
                ...state,
                agentDataAfterSave: { ...action.payload }
            };
        }
        case actionTypes.AGENT_METADATA_FILE_DETAILS: {
            return {
                ...state,
                metadataDetails: { ...action.payload }
            };
        }
        case actionTypes.AGENT_MODE: {
            return { ...state, agentMode: action.payload };
        }
        case actionTypes.AGENT_METADATA_TABLES_DATA: {
            const data = { ...action.payload, uid: uuidv4() };
            return { ...state, metadataTablesData: data || {} };
        }
        default:
            return { ...state };
    }
};
