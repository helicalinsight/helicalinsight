import actionTypes from '../actions/actionTypes';
import initialStates from './initialStates';
import { cloneDeep, isEqual } from 'lodash-es';
import produce from 'immer';
import { v4 as uuidv4 } from 'uuid';
import moment from 'moment';

const adminReducer = (state = initialStates.adminIntialState, action) => {
	switch (action.type) {
		// Recycle Bin
		case actionTypes.RECYCLEBIN_DATA:
			return { ...state, recycleBinData: action.payload };
		// Overview Module
		case actionTypes.ISADMINTABSDATASET:
			return { ...state, isAdminTabsDataSet: action.payload };
		case actionTypes.STORE_DISK_DATA:
			return { ...state, diskData: action.payload };
		case actionTypes.STORE_JVM_DATA:
			return { ...state, jvmData: action.payload };
		case actionTypes.STORE_TEMP_DATA:
			return { ...state, tempData: action.payload };
		case actionTypes.STORE_CACHE_SIZE:
			return { ...state, cacheSize: action.payload };
		case actionTypes.STORE_CACHED_REPORTS:
			return { ...state, cachedReports: action.payload };
		case actionTypes.STORE_CACHED_DATASOURCES:
			return { ...state, cachedDataSources: action.payload };
		case actionTypes.STORE_CURRENT_LEVEL:
			return { ...state, currentLevel: action.payload };
		case actionTypes.STORE_LOG_OPTIONS:
			return { ...state, logOptions: action.payload };
		case actionTypes.STORE_PRODUCT_DATA:
			return { ...state, productData: action.payload };
		case actionTypes.STORE_RELOAD_APPDATA:
			return { ...state, reloadAppMessage: action.payload };
		case actionTypes.STORE_RELOAD_VALIDDATA:
			return { ...state, reloadValidMessage: action.payload };
		case actionTypes.STORE_RELOAD_CACHE:
			return { ...state, reloadCacheMessage: action.payload };
		case actionTypes.UPDATE_IS_FETCHED:
			const { type, value } = action.payload;
			return produce(state, (draft) => {
				draft.isFetched[type] = value;
			});
		case actionTypes.STORE_REPORT_STATS:
			return { ...state, reportStats: action.payload };
		case actionTypes.STORE_DATA_SOURCE_COUNT:
			return { ...state, dataSourceCount: action.payload };

		// System Details Module
		case actionTypes.STORE_OS_DETAILS:
			return { ...state, osDetails: action.payload };
		case actionTypes.STORE_JVM_THREAD_DATA:
			return { ...state, jvmThreadDetails: action.payload };
		case actionTypes.SYSTEMDETAILS_OS_TABLE_EXPAND:
			return { ...state, osTableExpand: action.payload };
		case actionTypes.SYSTEMDETAILS_JVM_TABLE_EXPAND:
			return { ...state, jvmTableExpand: action.payload };
		// Plugins Module
		case actionTypes.STORE_PLUGINS_DATA:
			return { ...state, pluginsData: action.payload };
		// UserManagement Module
		case actionTypes.STORE_USER_DATA:
			return { ...state, userData: action.payload };
		case actionTypes.STORE_ORG_DATA:
			return { ...state, orgData: action.payload };
		case actionTypes.STORE_ROLE_DATA:
			return { ...state, roleData: action.payload };
		case actionTypes.UPDATE_ORG_LIST:
			return { ...state, orgList: [ ...state.orgList, action.payload ] };
		case actionTypes.SET_PROFILE_DATA:
			const { id1, obj } = action.payload;

			const newData = state.userData.slice().filter((item) => item.id !== id1);
			newData.splice(id1 - 1, 0, obj);
			return { ...state, userData: newData };
		case actionTypes.UPDATE_ROLES_LIST:
			return { ...state, rolesList: [ ...state.rolesList, action.payload ] };
		case actionTypes.UPDATE_VISIBLE_DRAWERS:
			const { key, status } = action.payload;
			const visible = { ...state.visibleDrawersUM };
			visible[key] = status;
			return {
				...state,
				visibleDrawersUM: visible
			};
		case actionTypes.UPDATE_EDIT_USER:
			return { ...state, editUser: action.payload };
		case actionTypes.DELETE_ORG_ITEM:
			const updatedOrgData = state.orgData.filter((item) => item.id !== action.payload);
			return { ...state, orgData: updatedOrgData };
		case actionTypes.DELETE_ROLE_ITEM:
			const updatedRoleData = state.roleData.filter((item) => item.id !== action.payload);
			return { ...state, roleData: updatedRoleData };
		case actionTypes.DELETE_USER_ITEM:
			const updatedUserData = state.userData.filter((item) => item.id !== action.payload);
			return { ...state, userData: updatedUserData };
		case actionTypes.DELETE_PROFILE_ITEM:
			const { profileId, userId } = action.payload;
			const requiredUserItem = state.userData.find((item) => item.id === userId);
			const updatedProfiles = requiredUserItem.profiles.filter((item) => item.id !== profileId);
			const updatedUserItem = {
				...requiredUserItem,
				profiles: updatedProfiles
			};
			const updatedUserData2 = state.userData.map((item) => {
				if (item.id === userId) {
					console.log('entered if condition');
					return updatedUserItem;
				}
				return item;
			});

			return { ...state, userData: updatedUserData2 };
		case actionTypes.ADD_ORG_ITEM:
			const updatedOrgData3 = [ ...state.orgData, action.payload ];
			return { ...state, orgData: updatedOrgData3, newOrgItem: action.payload };
		case actionTypes.ADD_PROFILE_ITEM:
			const requiredUserItem2 = state.userData.find((item) => item.id === action.payload.userId);
			const updatedProfiles2 = [ ...requiredUserItem2.profiles, action.payload.profileItem ];
			const updatedUserItem2 = {
				...requiredUserItem2,
				profiles: updatedProfiles2
			};
			const updatedUserData3 = state.userData.map((item) => {
				if (item.id === action.payload.userId) {
					return updatedUserItem2;
				}
				return item;
			});
			return { ...state, userData: updatedUserData3 };
		case actionTypes.ADD_ROLE_ITEM:
			const updatedRoleData2 = [ ...state.roleData, action.payload ];
			return { ...state, roleData: updatedRoleData2 };
		case actionTypes.ADD_USER_ITEM:
			const updatedUserData4 = [ ...state.userData, action.payload ];
			return { ...state, userData: updatedUserData4 };
		//Management Module
		case actionTypes.UPDATE_MANAGEMENT_DATA:
			return { ...state, managementData: action.payload };
		case actionTypes.UPDATE_MANAGEMENT_STATIC_DATA:
			return { ...state, managementStaticData: action.payload };
		case actionTypes.UPDATE_MANAGEMENT_ADVANCED_DATA:
			return { ...state, managementAdvancedData: action.payload };
		case actionTypes.UPDATE_MANAGEMENT_DICE_API_STATUS:
			return { ...state, isDiceApiRunning: action.payload };
		case actionTypes.UPDATE_NO_SQL_DATA:
			return { ...state, noSqlData: action.payload };
		case actionTypes.MODIFY_ADVANCED_DATA:
			const updateKey = action.payload[0];
			const updateValue = action.payload[1];
			return {
				...state,
				managementAdvancedData: { ...state.managementAdvancedData, [updateKey]: updateValue }
			};
		//scheduledLing Module
		case actionTypes.UPDATE_SCHEDULED_LIST:
			return {
				...state,
				schedulingList: action.payload
			};
		// adfs Module
		case actionTypes.STORE_METADATA_ADMINISTRATION_DATA:
			return { ...state, metadataAdministrationData: action.payload };
		case actionTypes.STORE_METADATA_GENERATION_DATA:
			return { ...state, metadataGenerationData: action.payload };
		case actionTypes.STORE_METADATA_PREVIEW_DATA:
			return { ...state, metadataPreviewData: action.payload };
		case actionTypes.UPDATE_ENTITY_ID:
			return { ...state, entityId: action.payload };
		case actionTypes.STORE_METADATA_GENERATION_FORM_VALUES:
			return { ...state, metadataGenerationFormValues: action.payload };
		case actionTypes.TOGGLE_METADATA_CONTENTS:
			const copy = {};
			Object.keys(state.showMetadataPages).forEach((item) => (copy[item] = false));
			copy[action.payload.page] = action.payload.status;
			return {
				...state,
				showMetadataPages: copy
			};
		// Reset Data
		case actionTypes.RESET_STORE:
			return { ...initialStates.adminIntialState };

		//  ----------------------------------      DICE STORAGE       -----------------------------------      //
		// CNG
		case actionTypes.DICE_STORAGE_SKELETON: {
			let rowKeys = [ ...state.diceStorage.skeletonRowKeys ];
			// let isSkeletonLoading = false;
			if (rowKeys.includes(action.payload)) {
				rowKeys = rowKeys.filter((ele) => ele !== action.payload);
			} else {
				rowKeys.push(action.payload);
			}
			return { ...state, diceStorage: { ...state.diceStorage, skeletonRowKeys: rowKeys } };
		}
		case actionTypes.UPDATE_DICE_STORAGE_TABLE_ROW: {
			const { key, value } = action.payload;
			const table = state.diceStorage.diceStorageTableData[key].map((ele) => {
				return ele.key !== value.key ? ele : value;
			});
			return {
				...state,
				diceStorage: {
					...state.diceStorage,
					diceStorageTableData: { ...state.diceStorage.diceStorageTableData, [key]: table }
				}
			};
		}
		case actionTypes.DICE_STORAGE_METADATA_DUMP_LIST: {
			const { key, value } = action.payload;
			const table = value.map((ele) => ({
				'Metadata Title': ele.title,
				fileDetails: {
					path: ele.path,
					extension: 'metadata',
					// "permissionLevel": "5",
					name: ele.name,
					description: ele.name,
					lastModified: ele.lastUpdatedTime,
					type: 'file',
					title: ele.title,
					options: {}
				},
				'Dump Type': ele.dumpType,
				'Dump Status': ele.status,
				'Last Updated': moment(new Date(JSON.parse(ele.lastUpdatedTime))).format(
					'dddd, MMMM Do, YYYY, h:mm:ss a'
				),
				id: ele.id,
				key: uuidv4()
			}));
			return {
				...state,
				diceStorage: {
					...state.diceStorage,
					diceStorageTableData: { ...state.diceStorage.diceStorageTableData, [key]: table }
				}
			};
		}
		case actionTypes.DICE_STORAGE_METADATA_FILE_DETAILS: {
			return { ...state, diceStorage: { ...state.diceStorage, metadataDetails: action.payload } };
		}
		case actionTypes.DICE_STORAGE_TAB_NAME: {
			return { ...state, diceStorage: { ...state.diceStorage, tabName: action.payload } };
		}
		case actionTypes.DICE_STORAGE_TABLE_DATA: {
			const { key, value } = action.payload;
			return {
				...state,
				diceStorage: {
					...state.diceStorage,
					diceStorageTableData: { ...state.diceStorage.diceStorageTableData, [key]: value }
				}
			};
		}
		case actionTypes.DICE_STORAGE_SEARCHED_COLUMN: {
			return { ...state, diceStorage: { ...state.diceStorage, diceStorageSearchedColumn: action.payload } };
		}
		case actionTypes.DICE_STORAGE_FIELD_SEARCH_TEXT: {
			return { ...state, diceStorage: { ...state.diceStorage, diceStorageFieldSearchText: action.payload } };
		}
		case actionTypes.STORE_WHATSNEW_CONTENT:{
			let whatsNewContent=action.payload
			whatsNewContent=Object.values(whatsNewContent).filter(item=>item!=="What's New?")
			return {...state,whatsNewContent}
		}
		default:
			return { ...state };
	}
};

export default adminReducer;
