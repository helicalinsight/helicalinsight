import produce from 'immer';
// import { roleTypes, routesUrl, serviceUrls } from "../../app/constants";
// import { URL_AUTHENTICATION } from '../../components/hi-login/helperMethods';
import actionTypes from '../actions/actionTypes';
import initialStates from './initialStates';
import { cloneDeep, isEqual } from 'lodash-es';
import { v4 as uuidv4 } from 'uuid';
import {
	handleHierarachyTitle,
	handleHierarchyData,
	handleLevelToHierarchy,
	handleRemoveHierarchy,
	uniqueHierarchyTitle
} from '../../components/hi-cube/helperMethods';
import { cubeEditorMeasureData } from '../../components/hi-cube/cubeConstants';
// import tutorialItems from "../../components/common/hi-tutorial/tutorial-items";

export const cubeReducer = (state = initialStates.cubeInitialState, action) => {
	switch (action.type) {
		case actionTypes.RESET_STORE:
		case actionTypes.CUBE_LOCAL_RESET:
			return { ...initialStates.cubeInitialState };
		case actionTypes.ON_EXPAND_CUBE_TABLE: {
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
		case actionTypes.CUBE_DATA_AFTER_SAVE: {
			return {
				...state,
				cubeDataAfterSave: { ...action.payload }
			};
		}
		case actionTypes.SAVE_CUBE_DETAILS_FOR_EDIT: {
			return {
				...state,
				cubeForEdit: { ...action.payload }
			};
		}
		case actionTypes.CUBE_METADATA_FILE_DETAILS: {
			return {
				...state,
				metadataDetails: { ...action.payload }
			};
		}
		case actionTypes.CUBE_TABLE_MODE: {
			return {
				...state,
				isCubeTableModeNormal: !state.isCubeTableModeNormal
			};
		}
		// case actionTypes.CUBE_TABLE_DELETE_KEYS: {
		// 	return {
		// 		...state,
		// 		cubeState: {
		// 			...state.cubeState,
		// 			cubeTableDeleteKeys:
		// 				action.payload === 'reset' ? [] : [ ...state.cubeState.cubeTableDeleteKeys, action.payload ]
		// 		}
		// 	};
		// }
		case actionTypes.CUBE_MODE: {
			return { ...state, cubeMode: action.payload };
		}
		case actionTypes.CUBE_INITIAL_LIST: {
			const { timeStamp, cubeFieldsData, key, type } = action.payload;
			if (type === 'delete') {
				return {
					...state,
					cubeInitialList: state.cubeInitialList.filter((ele) => ele.key !== key)
				};
			} else if (state.cubeMode !== 'edit') {
				return {
					...state,
					cubeInitialList: [ ...state.cubeInitialList, { timeStamp, cubeFieldsData, key } ]
				};
			}
			let dupCubeInitialList = JSON.parse(JSON.stringify(state.cubeInitialList));
			dupCubeInitialList = dupCubeInitialList.map((ele) => {
				if (ele.cubeFieldsData.key === cubeFieldsData.key) {
					ele.timeStamp = timeStamp;
					ele.cubeFieldsData = cubeFieldsData;
				}
				return ele;
			});
			return { ...state, cubeInitialList: dupCubeInitialList };
		}
		case actionTypes.CUBE_UPDATE_FIELD_VALUES: {
			const { recordKey, updateName, checkVal, key, value, isHierarchyChild, hierarchyKey } = action.payload;
			if (updateName === 'domainName') {
				return {
					...state,
					cubeFieldsData: {
						...state.cubeFieldsData,
						domainName: checkVal ?? ''
					}
				};
			}
			if (updateName === 'cubeDescription') {
				return {
					...state,
					cubeFieldsData: {
						...state.cubeFieldsData,
						cubeDescription: checkVal ?? ''
					}
				};
			}
			if (updateName === 'cubeTopic') {
				return {
					...state,
					cubeFieldsData: {
						...state.cubeFieldsData,
						cubeTopic: checkVal ?? ''
					}
				};
			}
			let dupCubeFieldsData = JSON.parse(JSON.stringify(state.cubeFieldsData));
			let dupChildren = [];
			let duplicateHierarchyData = {
				hierarchyList: [ ...state.cubeFieldsData.hierarchyData.hierarchyList ],
				isHierarchyPresent: state.cubeFieldsData.hierarchyData.isHierarchyPresent
			};
			if (isHierarchyChild) {
				dupCubeFieldsData.children.forEach((ele) => {
					if (ele.key === hierarchyKey) {
						dupChildren = ele.children;
					}
				});
			} else {
				dupChildren = dupCubeFieldsData.children;
			}
			// if (updateName === 'delete') {
			// 	dupCubeFieldsData.children = dupCubeFieldsData.children.filter((ele) => {
			// 		if (ele.isHierarchy && !state.cubeState.cubeTableDeleteKeys.includes(ele.key)) {
			// 			ele.children = ele.children.filter(
			// 				(eleChild) => !state.cubeState.cubeTableDeleteKeys.includes(eleChild.key)
			// 			);
			// 		}
			// 		return !state.cubeState.cubeTableDeleteKeys.includes(ele.key);
			// 	});
			// 	dupCubeFieldsData.hierarchyData.hierarchyList = dupCubeFieldsData.hierarchyData.hierarchyList.filter(
			// 		(ele) => !state.cubeState.cubeTableDeleteKeys.includes(ele.hierarchyKey)
			// 	);
			// }
			// if (updateName === 'title') {
			// 	dupCubeFieldsData.title = value;
			// } else
			if (updateName === 'rowTitle') {
				let isHierarchy = false,
					isDimensionCheck = false;
				let parentRecord;
				if (isHierarchyChild) {
					parentRecord = dupCubeFieldsData.children.find((ele) => ele.key === hierarchyKey).children;
					parentRecord.forEach((obj) => {
						if (obj.key === recordKey) {
							obj.fields = value;
							// isHierarchy = obj.isHierarchy;
							// isDimensionCheck = obj.isDimensionCheck;
						}
					});
				} else {
					dupCubeFieldsData.children = dupCubeFieldsData.children.map((obj) => {
						if (obj.key === recordKey) {
							obj.fields = value;
							isHierarchy = obj.isHierarchy;
							isDimensionCheck = obj.isDimensionCheck;
						}
						return obj;
					});
				}
				(isHierarchy || isDimensionCheck) &&
					!isHierarchyChild &&
					dupCubeFieldsData.hierarchyData.hierarchyList.forEach((hData) => {
						if (recordKey === hData.hierarchyKey) {
							hData.hierarchyName = value;
						}
					});
			} else {
				dupChildren = dupChildren.map((obj) => {
					if (updateName === 'visible__Check__') {
						if (obj.isHierarchy) {
							obj.children.forEach((objChild) => {
								objChild.isVisible = checkVal;
							});
						} else {
							obj.isVisible = checkVal;
						}
					} else {
						if (obj.key === recordKey) {
							if ([ 'measure', 'sort', 'aggregation' ].includes(updateName)) {
								obj[updateName][key] = value;
								if (updateName === 'measure' && value) {
									if (key === 'isMeasureCheck') {
										obj.measure = {
											isMeasureCheck: true,
											DataType: cubeEditorMeasureData[0].dataType,
											Format: cubeEditorMeasureData[0].format[0]
										};
										obj.isDimensionCheck = false;
										obj.aggregation = {
											isAggregationCheck: true,
											value: 'Sum'
										};
										obj.sort = {
											isSortCheck: false,
											value: ''
										};
									} else if (key === 'DataType') {
										obj.measure.Format = cubeEditorMeasureData.reduce((acc, cur) => {
											if (cur.dataType === value) {
												acc = cur.format[0];
											}
											return acc;
										}, ''); // pending
									}
								}
							} else {
								obj[updateName] = checkVal;
								if (updateName === 'isDimensionCheck') {
									if (checkVal) {
										if (obj.isHierarchy) {
											dupCubeFieldsData.hierarchyData = handleHierarchyData({
												state,
												record: obj
											});
										}
										obj.measure = {
											isMeasureCheck: false,
											DataType: '',
											Format: ''
										};
										obj.aggregation = {
											isAggregationCheck: false,
											value: ''
										};
										obj.sort = {
											isSortCheck: true,
											value: 'Ascending'
										};
									} else {
										if (obj.isHierarchy) {
											handleRemoveHierarchy({ dupCubeFieldsData, record: obj });
										}
									}
								}
							}
						}
					}
					return obj;
				});
			}
			return {
				...state,
				cubeFieldsData: { ...dupCubeFieldsData }

				// cubeTableDeleteKeys: updateName === 'delete' ? [] : state.cubeState.cubeTableDeleteKeys
			};
		}
		case actionTypes.CUBE_VISIBLE_VAL: {
			return { ...state, isCubeVisibleCheck: action.payload };
		}
		case actionTypes.CUBE_VISIBLE_INDETERMINATE: {
			return { ...state, cubeVisibleIndeterminate: action.payload };
		}
		case actionTypes.CUBE_FIELDS_DATA_BACKUP: {
			return { ...state, cubeVisibleIndeterminate: cloneDeep(action.payload) };
		}
		case actionTypes.CUBE_FIELDS_DATA: {
			const { mode, title, child, value, key } = action.payload;
			if (mode === 'reset') {
				return {
					...state,
					cubeFieldsData: {
						// title: '',
						domainName: '',
						cubeDescription: '',
						children: [],
						key: '',
						hierarchyData: { isHierarchyPresent: false, hierarchyList: [] }
					}
				};
			} else if (mode === 'setChild') {
				let hierarchyData = {
					hierarchyList: [ ...state.cubeFieldsData.hierarchyData.hierarchyList ],
					isHierarchyPresent: state.cubeFieldsData.hierarchyData.isHierarchyPresent
				};
				//pending
				child.fields = handleHierarachyTitle({
					value: child.fields,
					cubeFieldsData: state.cubeFieldsData
				});
				if (child.isDimensionCheck) {
					hierarchyData = handleHierarchyData({ state, record: child });
				}
				return {
					...state,
					cubeFieldsData: {
						...state.cubeFieldsData,
						children: [ ...state.cubeFieldsData.children, child ],
						hierarchyData
					}
				};
			} else if (mode === 'edit') {
				return {
					...state,
					cubeFieldsData: { ...value }
					// cubeFieldsDataBackup: { ...cloneDeep(value) }
				};
			}
		}
		case actionTypes.CUBE_SEARCHED_COLUMN: {
			return { ...state, cubeSearchedColumn: action.payload };
		}
		case actionTypes.SEARCH_FIELD_TEXT: {
			return { ...state, fieldSearchText: action.payload };
		}
		case actionTypes.CUBE_STATE: {
			return {
				...state,

				cubeCurrentState: action.payload
			};
		}
		// case actionTypes.CUBE_HEADER_DATA: {
		// 	return { ...state, headerData: action.payload };
		// }
		case actionTypes.CUBE_METADATA_TABLES_DATA: {
			const data = { ...action.payload, uid: uuidv4() }; //.tables.sort((a, b) => a.alias > b.alias);
			return { ...state, metadataTablesData: data || {} };
		}
		case actionTypes.CUBE_HIERARCHY_MODIFICATION: {
			const { record, step, selectedHierarchy } = action.payload;
			const dupCubeSate = cloneDeep(state);
			const dupCubeFieldsData = dupCubeSate.cubeFieldsData;
			const clonedChildren = dupCubeFieldsData.children;
			if (
				step === 'deleteHierarchyWithOutColumns' ||
				step === 'deleteHierarchyWithColumns' ||
				step === 'deleteRow'
			) {
				handleRemoveHierarchy({ dupCubeFieldsData, record });
			}
			if (step === 'deleteHierarchyWithOutColumns') {
				dupCubeFieldsData.children = clonedChildren.reduce((acc, cur) => {
					if (cur.key === record.key) {
						const { children: curChildren } = cur;
						curChildren.forEach((curChild) => {
							if (!curChild.isDelete) {
								const { isHierarchyChild, parentKey, ...restCurchild } = curChild;
								restCurchild.fields = handleHierarachyTitle({
									value: restCurchild.fields,
									cubeFieldsData: state.cubeFieldsData,
									ignoreKey: record.key
								});
								dupCubeFieldsData.hierarchyData.hierarchyList.push({
									hierarchyName: restCurchild.fields,
									hierarchyKey: restCurchild.key
								});
								dupCubeFieldsData.hierarchyData.isHierarchyPresent = true;
								acc.push({ ...restCurchild });
							}
						});
						acc.push({ ...cur, isDelete: true });
					} else {
						acc.push(cur);
					}
					return acc;
				}, []);
			} else if (step === 'deleteRow' || step === 'deleteHierarchyWithColumns') {
				dupCubeFieldsData.children = clonedChildren.map((ele) => {
					if (ele.key === record.key) {
						// if (step === 'deleteHierarchyWithColumns') {
						// 	ele.children = ele.children.map((child) => ({ ...child, isDelete: true }));
						// }
						return { ...ele, isDelete: true };
					} else {
						return ele;
					}
				});
			} else if (step === 'deleteFromHierarchy') {
				dupCubeFieldsData.children = clonedChildren.reduce((acc, ele) => {
					if (ele.key === record.parentKey) {
						ele.children = ele.children.map((eleChild) => {
							if (eleChild.key === record.key) {
								return { ...eleChild, isDelete: true };
							} else {
								return eleChild;
							}
						});
						if (ele.children.length === 1 && ele.columnId === ele.children[0].columnId) {
							const { isHierarchyChild, parentKey, ...restCurchild } = ele.children[0];
							restCurchild.key = ele.key;
							ele = { ...restCurchild };
						} else if (!ele.children.length) {
							handleRemoveHierarchy({ dupCubeFieldsData, record: { key: record.parentKey } });
							return acc;
						}
					}
					ele.fieldsDropdownOpen = false;
					acc.push(ele);
					return acc;
				}, []);
			} else if (step === 'removeFromHierarchy') {
				dupCubeFieldsData.children = clonedChildren.reduce((acc, cur) => {
					if (cur.key === record.parentKey) {
						if (cur.children.length > 2 || record.columnId === cur.columnId) {
							cur.children = cur.children.filter((curChild) => {
								if (curChild.key === record.key) {
									const { isHierarchyChild, parentKey, ...restCurchild } = curChild;
									handleLevelToHierarchy({ restCurchild, state, dupCubeFieldsData });
									acc.push({ ...restCurchild, fieldsDropdownOpen: false });
									return false;
								}
								return true;
							});
							acc.push(cur);
						} else {
							handleRemoveHierarchy({ dupCubeFieldsData, record: cur });
							cur.children.forEach((curChild) => {
								const { isHierarchyChild, parentKey, ...restCurchild } = curChild;
								handleLevelToHierarchy({ restCurchild, state, dupCubeFieldsData, parentKey });
								// if (curChild.key === record.key) {
								acc.push({ ...restCurchild, fieldsDropdownOpen: false });
								// } else {
								// 	cur = { ...restCurchild, fieldsDropdownOpen: false };
								// }
							});
						}
					} else {
						acc.push(cur);
					}
					//  else {
					// }
					return acc;
				}, []);
				// } else if (step === 'addToNewHierarchy') {
				// 	const hierarchyNewName = uniqueHierarchyTitle(state.cubeFieldsData);
				// 	const hierarchyNewKey = uuidv4();
				// 	dupCubeFieldsData.children = clonedChildren.map((ele) => {
				// 		if (ele.key === record.key) {
				// 			ele = {
				// 				key: hierarchyNewKey,
				// 				fields: hierarchyNewName,
				// 				isHierarchy: true,
				// 				fieldsDropdownOpen: false,
				// 				children: [
				// 					{
				// 						...ele,
				// 						fieldsDropdownOpen: false,
				// 						isHierarchyChild: true,
				// 						parentKey: hierarchyNewKey
				// 					}
				// 				]
				// 			};
				// 			dupCubeFieldsData.hierarchyData.hierarchyList.push({
				// 				hierarchyName: hierarchyNewName,
				// 				hierarchyKey: hierarchyNewKey
				// 			});
				// 			dupCubeFieldsData.hierarchyData.isHierarchyPresent = true;
				// 		}
				// 		return ele;
				// 	});
			} else if (step === 'addToExistingHierarchy') {
				let seperatedRecord = {};
				let hierarchyRecord = {};
				let dupChildren = []; //dupCubeFieldsData.children;
				clonedChildren.forEach((ele) => {
					if (ele.key === record.key) {
						seperatedRecord = ele;
						handleRemoveHierarchy({ dupCubeFieldsData, record });
					} else {
						if (selectedHierarchy === ele.fields) {
							if (!ele.isHierarchy) {
								ele = {
									key: ele.key,
									fields: ele.fields,
									isHierarchy: true,
									fieldsDropdownOpen: false,
									tableId: ele.tableId,
									columnId: ele.columnId,
									resHierarchyId: ele.resHierarchyId,
									resDimensionId: ele.resDimensionId,
									table: ele.table,
									column: ele.column,
									columnName: ele.columnName,
									dataType: ele.dataType,
									type: ele.type,
									children: [
										{
											...ele,
											key: uuidv4(),
											fieldsDropdownOpen: false,
											isHierarchyChild: true,
											parentKey: ele.key
										}
									]
								};
								// delete ele.children.resHierarchyId;
								// delete ele.children.resDimensionId;
							}
							hierarchyRecord = ele;
						}
						dupChildren.push(ele);
					}
				});
				dupCubeFieldsData.children = dupChildren;
				if (!seperatedRecord.isHierarchy) {
					seperatedRecord.fields = handleHierarachyTitle({
						value: seperatedRecord.fields,
						cubeFieldsData: hierarchyRecord
					});
					hierarchyRecord.children.push({
						...seperatedRecord,
						fieldsDropdownOpen: false,
						isHierarchyChild: true,
						parentKey: hierarchyRecord.key
					});
				} else {
					seperatedRecord.children.forEach((sepRec) => {
						sepRec.fields = handleHierarachyTitle({
							value: sepRec.fields,
							cubeFieldsData: hierarchyRecord
						});
						hierarchyRecord.children.push({
							...sepRec,
							fieldsDropdownOpen: false,
							// isHierarchyChild: true,
							parentKey: hierarchyRecord.key
						});
					});
				}
			}
			return {
				...state,
				...dupCubeSate
				// ...state.cubeState,
				// cubeFieldsData: {
				// 	...dupCubeFieldsData
				// }
			};
		}

		default:
			return { ...state };
	}
};
