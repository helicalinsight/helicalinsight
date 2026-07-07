import produce from 'immer';
import { cloneDeep } from 'lodash-es';
import _ from 'lodash';
import { v4 as uuidv4 } from 'uuid';
import { intialMarks, markTypes } from '../../components/hi-reports/utils/constants';
import { addFieldToReport, addFilterToReport, generateFieldAlias } from '../../components/hi-reports/utils/utilities';
import types from '../../constants/metadata';
import { addFilterValue, createDbFunc, defaultAnchor } from '../../utils/filter-utils';
import {
	addMarkField,
	addValueToLabel,
	applyRulesForOrderByColumn,
	funcMap,
	generateAlias,
	getFieldDisplayName,
	getInitialCascade,
	createTabTitle,
	addDrillThroughField
} from '../../utils/utilities';
import actionTypes from '../actions/actionTypes';
import { getIntialReportState, initialCustomChart } from './initialStates';
import {
	getFormatFields,
	getUpdatedCardProperties,
	getUpdatedColorProperties,
	getUpdatedProperties,
	getUpdatedRangeProperties
} from '../../components/hi-reports/hi-editing-area/utils/property-utils';

// import { hreport_intial_view_state } from "../../test/hreport-test/hreport.request.mock"

export const dateConditions = ['dateTime', 'date', 'time'];


export const getHreportInitialGridLayout = () => {
	let offsetHeight = (window.innerHeight + 20) / 12;
	let layoutItems = [
		{
			i: "sidebar-area",
			y: 0,
			h: offsetHeight,
			isDraggable: false,
			isResizable: false,
		},
		{
			i: "chart-area",
			y: 0,
			h: offsetHeight,
			isDraggable: false,
			isResizable: false,
		},
		{
			i: "editing-area",
			y: 0,
			h: offsetHeight,
			isDraggable: false,
			isResizable: false,
		},
	];
	let lg = [
		{ ...layoutItems[0], x: 0, w: 16.7 },
		{
			...layoutItems[1],
			x: 16.7,
			w: 100 - (16.7 + 18.5),
		},
		{ ...layoutItems[2], x: 100 - 18.5, w: 18.5 },
	];
	return {
		lg,
		md: lg,
		sm: [
			{ ...layoutItems[0], x: 0, w: 50 },
			{ ...layoutItems[1], x: 50, w: 50 },
			{ ...layoutItems[2], x: 0, w: 100 },
		],
		xs: [
			{ ...layoutItems[0], x: 0, w: 50 },
			{ ...layoutItems[1], x: 50, w: 50 },
			{ ...layoutItems[2], x: 0, w: 100 },
		],
		xxs: [
			{ ...layoutItems[0], x: 0, w: 100 },
			{ ...layoutItems[1], x: 0, w: 100 },
			{ ...layoutItems[2], x: 0, w: 100 },
		],
	}
}

let layout = {
	metadataShelf: true,
	toolsAreaShelf: true,
	fieldsAreaShelf: true,
	gridItemsLayout: getHreportInitialGridLayout()
};

let intialState = {
	isSavingInProgress: false,
	activeReportId: null,
	reports: [],
	baseStateReports: [],
	layout,
	hasUnsavedData: true,
	sidebarLoading: false,
	hreportEditLoading: false,
	hrSidebar: 'metadata',
	childReportLoading: false,
	geoJsonData: {
		country: [],
		state: [],
		city: [],
	},
};

// if(process.env.NODE_ENV === "test"){
//     intialState = hreport_intial_view_state
// }

const hreportReducer = (state = intialState, action) => {
	switch (action.type) {
		case actionTypes.HR_SIDEBAR: {
			return produce(state, (draft) => {
				draft.hrSidebar = action.payload;
			});
		}
		case actionTypes.GET_CUBE_RES: {
			return produce(state, (draft) => {
				draft.cubeData = action.payload;
			});
		}
		case actionTypes.LOAD_INTIAL_REPORT: {
			let { reportId } = action.payload;
			return produce(state, (draft) => {
				let report = getIntialReportState({ active: true });
				report.id = reportId;
				draft.reports = [report];
				draft.baseStateReports = cloneDeep([report]);
				draft.activeReportId = report.id;
				draft.layout = layout;
			});
		}
		case actionTypes.REPORT_IS_SAVING: {
			return produce(state, (draft) => {
				draft.isSavingInProgress = action.payload
			});
		}
		case actionTypes.ADD_NEW_REPORT: {
			return produce(state, (draft) => {
				let report = getIntialReportState({ active: true });
				draft.reports = draft.reports.map((item) => {
					item.active = false;
					return item;
				});
				let reportName = createTabTitle(draft.reports.map((report) => report.reportInfo.reportName));
				report.reportInfo.reportName = reportName;
				draft.reports.push(report);
				draft.baseStateReports.push(cloneDeep(report));
				draft.activeReportId = report.id;
			});
		}
		case actionTypes.CHANGE_REPORT: {
			let { id } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((item) => {
					item.active = false;
					if (item.id === id) {
						item.active = true;
					}
					return item;
				});
				draft.activeReportId = id;
			});
		}
		case actionTypes.REMOVE_REPORT: {
			let { id } = action.payload;
			return produce(state, (draft) => {
				if (draft.activeReportId === id) {
					let index = draft.reports.findIndex((item) => item.id === id);
					let nextActiveIndex = index - 1;
					if (draft.reports[index - 1]) {
						nextActiveIndex = index - 1;
					} else if (draft.reports[index + 1]) {
						nextActiveIndex = index + 1;
					}
					if (nextActiveIndex > -1) {
						draft.reports[nextActiveIndex].active = true;
						draft.activeReportId = draft.reports[nextActiveIndex].id;
					} else {
						let report = getIntialReportState({ active: true });
						report.reportInfo.reportName = `Untitled 1`;
						draft.reports.push(report);
						draft.activeReportId = report.id;
					}
				}
				draft.reports = draft.reports.filter((item) => {
					return item.id !== id;
				});
				draft.baseStateReports = draft.baseStateReports.filter((item) => {
					return item.id !== id;
				});

			});
		}
		case actionTypes.REMOVE_ALL_REPORTS: {
			return produce(state, (draft) => {
				draft.reports = [];
				draft.baseStateReports = [];
				draft.activeReportId = null;
			});
		}
		case actionTypes.LOAD_METADATA: {
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						let { funcs, metadata, dateFunctions, loading, isCube, cube } = action.payload || {};
						if (loading) {
							return { ...report, metadata: { loading } };
						}
						if (!metadata || !metadata.tables) {
							return { ...report, metadata: null };
						}
						if (!isCube) {
							delete report.isCube;
							delete report.cube;
						}
						isCube && (report.isCube = true);
						cube && (report.cube = cube);
						metadata.uid = uuidv4();
						report.metadata = metadata;
						report.database = metadata.name;
						Object.keys(metadata.tables).forEach((tableName) => {
							metadata.tables[tableName].key = uuidv4();
						});
						report.functions = funcs.functions;
						report.databaseFunctions = funcs.databaseFunctions || {};
						report.dateFunctions = dateFunctions;
					}
					return report;
				});
			});
		}
		case actionTypes.SET_METADATA_LOADING: {
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						let { loading, undoRedoAction } = action.payload || {};
						return { ...report, metadataLoading: undoRedoAction ? false : loading };
					}
					return report;
				});
			});
		}
		case actionTypes.SET_HREPORT_SIDEBAR_LOADING: {
			let { loading, undoRedoAction } = action.payload
			return produce(state, (draft) => {
				draft.sidebarLoading = undoRedoAction ? false : loading;
			});
		}
		case actionTypes.SEARCH_IN_METADATA: {
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						let { searchText } = action.payload || {};
						if (report.metadata) {
							report.metadata.defaultMetadataSearchText = searchText;
						}
					}
					return report;
				});
			});
		}
		case actionTypes.ON_EXPAND_TABLE: {
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						let { key } = action.payload || {};
						let { defaultexpandedRowKeys } = report.metadata;
						if (defaultexpandedRowKeys) {
							if (defaultexpandedRowKeys.includes(key)) {
								defaultexpandedRowKeys = defaultexpandedRowKeys.filter((item) => item !== key);
							} else {
								defaultexpandedRowKeys.push(key);
							}
						} else {
							defaultexpandedRowKeys = [key];
						}
						report.metadata.defaultexpandedRowKeys = defaultexpandedRowKeys;
					}
					return report;
				});
			});
		}
		case actionTypes.ADD_FIELD_TO_CANVAS: {
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report = addFieldToReport(cloneDeep(action.payload), report);
					}
					return report;
				});
			});
		}
		case actionTypes.REMOVE_FIELD_FROM_CANVAS: {
			let { id } = action.payload.field;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.marksList = report.marksList.map((mark) => {
							markTypes.map((key) => {
								mark[key].fields = mark[key].fields.filter((field) => field.id !== id);
							});
							return mark;
						});
						report.drillThroughList = (report.drillThroughList || []).map((childReport) => {
							childReport.parameters = childReport.parameters.map((fltr) => {
								if (fltr.mappedColumnId === id) {
									fltr.mappedColumnId = null;
								}
								return fltr;
							});
							return childReport;
						});
						report.fields = report.fields.filter((field) => field.id !== id);
						report.marksList = report.marksList.filter((mark) => mark.id !== id);
						report.referenceLineList = report.referenceLineList.filter((item) => item.id !== id); // removing reference line from canvas
					}
					return report;
				});
			});
		}
		case actionTypes.UPDATE_CANVAS_FIELD: {
			let { id, key } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.fields = report.fields.map((field) => {
							if (field.id === id) {
								field[key] = !field[key];
								let { columnData } = applyRulesForOrderByColumn({
									column: field,
									type: 'update'
								});
								field = columnData;
							}
							return field;
						});
					}
					return report;
				});
			});
		}
		case actionTypes.MOVE_FIELD_IN_CANVAS: {
			let { source, target } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						let sourceField = report.fields.find((field) => field.id === source.id);
						let sourceIndex = report.fields.findIndex((field) => field.id === source.id);
						let targetIndex = report.fields.findIndex((field) => field.id === target.id);
						report.fields.splice(sourceIndex, 1);
						report.fields.splice(targetIndex, 0, sourceField);
					}
					return report;
				});
			});
		}
		case actionTypes.SWAP_CANVAS_FIELD: {
			let { fieldIds } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						let { fields } = report;
						let newFields = fields.filter((field) => !fieldIds.includes(field.id));
						fieldIds.forEach((id) => {
							let field = fields.find((field) => field.id === id);
							if (field.addedAs === 'column') {
								field.addedAs = 'row';
							} else if (field.addedAs === 'row') {
								field.addedAs = 'column';
							}
							newFields.push(field);
						});
						report.fields = newFields;
					}
					return report;
				});
			});
		}
		case actionTypes.CLEAR_FIELDS_SHELF: {
			let { fieldType } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.fields = report.fields.filter((field) => field.addedAs !== fieldType);
						report.marksList = [intialMarks];
					}
					return report;
				});
			});
		}
		case actionTypes.UPDATE_AGGREGATIONS: {
			let { id, key, group } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						let { functions } = report;
						let { prependTableNameToAlias } = report.options;
						report.fields = report.fields.map((field) => {
							if (field.id === id) {
								if (field[group] && Array.isArray(field[group])) {
									let findIndex = field[group].indexOf(key);
									if (findIndex > -1) {
										field[group].splice(findIndex, 1);
									} else {
										field[group] = [...field[group], key];
									}
								} else {
									field[group] = [key];
								}
								if (group === 'aggregate') {
									field.autogen_alias = generateAlias(
										field[group],
										field.metaDataAlias,
										functions,
										field.tableAlias,
										prependTableNameToAlias,
										report.fields
									);
								}
							}
							return field;
						});
					}
					return report;
				});
			});
		}
		case actionTypes.UPDATE_FIELD_ALIAS: {
			let { id, alias } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.fields = report.fields.map((field) => {
							if (field.id === id) {
								field.alias = alias;
							}
							return field;
						});
					}
					return report;
				});
			});
		}
		case actionTypes.UPDATE_CUSTOM_COLUMN: {
			let { id, alias, column } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.fields = report.fields.map((field) => {
							if (field.id === id) {
								field.alias = alias;
								field.column = column;
							}
							return field;
						});
						report.customColumnData = null;
					}
					return report;
				});
			});
		}
		case actionTypes.UPDATE_ORDER_BY: {
			let { id, key } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.fields = report.fields.map((field) => {
							if (field.id === id) {
								field.orderBy = key ? [key] : null;
							}
							return field;
						});
					}
					return report;
				});
			});
		}
		case actionTypes.HIDE_FIELD: {
			let { id } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.fields = report.fields.map((field) => {
							if (field.id === id) {
								field.hidden = !field.hidden;
								if (!field.hidden) {
									field.hiddenIncludeInResultSet = false;
								}
							}
							return field;
						});
					}
					return report;
				});
			});
		}
		case actionTypes.TOGGLE_HIDDEN_FIELDS: {
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report = { ...report, ...action.payload };
					}
					return report;
				});
			});
		}
		case actionTypes.TOGGLE_FLOATING: {
			let { id, reportId, floatingType } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.id === reportId) {
						report.fields = report.fields.map((field) => {
							if (field.id === id) {
								field.floatingType = floatingType;
							}
							return field;
						});
					}
					return report;
				});
			});
		}
		case actionTypes.EDIT_FIELD_FUNCTIONS: {
			let { id } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.editingField =
							report.editingField && report.editingField.id === id
								? null
								: report.fields.find((field) => field.id === id);
					}
					return report;
				});
			});
		}
		case actionTypes.TOGGLE_QUERY_EDITOR: {
			let { fieldType, id } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.customColumnData = fieldType ? { fieldType } : null;
						if (id) {
							report.customColumnData = report.fields.find((field) => field.id === id);
						}
					}
					return report;
				});
			});
		}
		case actionTypes.SAVE_QUERY: {
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						// report.customColumnData = type ? {type} : null
					}
					return report;
				});
			});
		}
		case actionTypes.CLEAR_DB_FUNCTION: {
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.editingField.databaseFunction = null;
					}
					return report;
				});
			});
		}
		case actionTypes.UPDATE_COLUMN: {
			let { editingField } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.fields = report.fields.map((field) => {
							if (field.id === editingField.id) {
								field = editingField;
							}
							return field;
						});
						report.activeFunc = false;
						report.editingField = null;
					}
					return report;
				});
			});
		}
		case actionTypes.UPADATE_FUNCTION_DEFINATION: {
			let { functionsDefinition } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.editingField = report.editingField || {};
						report.editingField.functionsDefinition = functionsDefinition;
					}
					return report;
				});
			});
		}
		case actionTypes.CHANGE_EDITING_PANE: {
			let { id } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.activeTool = id;
					}
					return report;
				});
				// if([3,4,5,7].includes(id)){
				//     draft.layout.metadataShelf = false
				// }else{
				//     draft.layout.metadataShelf = true
				// }
			});
		}
		case actionTypes.CREATE_FILTER: {
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.id === action.payload.reportId || report.active) {
						report = addFilterToReport(cloneDeep(action.payload), report);
					}
					return report;
				});
			});
		}
		case actionTypes.UPDATE_FILTER_ALIAS: {
			let { uid, alias, reportId } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.id === reportId) {
						report.filters = report.filters.map((filter) => {
							if (filter.uid === uid) {
								filter.alias = alias;
							}
							return filter;
						});
					}
					return report;
				});
			});
		}
		case actionTypes.DELETE_FILTER: {
			let { uid } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.filters = report.filters.filter((filter) => {
							return filter.uid !== uid;
						});
						report.drillDownList = report.drillDownList.filter((filter) => {
							return filter.uid !== uid;
						});
					}
					return report;
				});
			});
		}
		case actionTypes.ADD_DRILLDOWN_FILTER: {
			let { newFltr, reportId } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.id === reportId) {
						report.filters.push(newFltr);
						report.currentDrillDown = newFltr.drillDownId;
					}
					return report;
				});
			});
		}
		case actionTypes.DELETE_DRILLDOWN_FILTER: {
			let { drillDownIds, currentDrillDown, reportId } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.id === reportId) {
						if (!report.drillDown) {
							return report;
						}
						report.filters = report.filters.filter((filter) => {
							if (drillDownIds.includes(filter.drillDownId)) {
								report.currentDrillDown = currentDrillDown;
							}
							if (!drillDownIds.includes(filter.drillDownId)) {
								return true;
							}
						});
					}
					return report;
				});
			});
		}
		case actionTypes.TOGGLE_FILTER: {
			let { uid, reportId } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.id === reportId) {
						report.filters = report.filters.map((filter) => {
							if (filter.uid === uid) {
								filter.active = !filter.active;
								filter.configId = uuidv4();
								filter.valuesList = [];
							}
							return filter;
						});
					}
					return report;
				});
			});
		}
		case actionTypes.CHANGE_FILTER_CONDITION: {
			let { uid, condition } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.filters = report.filters.map((filter) => {
							if (filter.uid === uid) {
								let configId = uuidv4();
								filter = {
									...filter,
									condition,
									values: [],
									anchor: defaultAnchor(),
									configId
								};
							}
							return filter;
						});
					}
					return report;
				});
			});
		}
		case actionTypes.UPDATE_CUSTOM_CONDITION: {
			let { uid, customCondition } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.filters = report.filters.map((filter) => {
							if (filter.uid === uid) {
								filter.customCondition = customCondition;
							}
							return filter;
						});
					}
					return report;
				});
			});
		}
		case actionTypes.CHANGE_FILTER_SEARCH: {
			let { uid, showSearch, search, reportId } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.id === reportId) {
						report.filters = report.filters.map((filter) => {
							if (filter.uid === uid) {
								filter = { ...filter, showSearch, search };
							}
							return filter;
						});
					}
					return report;
				});
			});
		}
		case actionTypes.FETCH_FILTER_VALUES: {
			let { values, uid, reportId, loading } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.id === reportId) {
						report.filters = report.filters.map((filter) => {
							if (filter.uid === uid) {
								filter = {
									...filter,
									valuesList: values,
									loading,
									dataId: uuidv4()
								};
							}
							return filter;
						});
					}
					return report;
				});
			});
		}
		case actionTypes.LOAD_VALUES_RANGE: {
			let { valuesRange, uid, loading } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.filters = report.filters.map((filter) => {
							if (filter.uid === uid) {
								filter = {
									...filter,
									valuesRange: valuesRange,
									loading,
									dataId: uuidv4()
								};
							}
							return filter;
						});
					}
					return report;
				});
			});
		}
		case actionTypes.UPDATE_DATE_PART: {
			let { uid, reportId } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.id === reportId) {
						let { databaseFunctions, dateFunctions } = report;
						report.filters = report.filters.map((filter) => {
							if (filter.uid === uid) {
								filter = action.payload;
								filter.configId = uuidv4();
								filter = createDbFunc(filter, filter.datePart, databaseFunctions, dateFunctions);
							}
							return filter;
						});
					}
					return report;
				});
			});
		}
		case actionTypes.UPDATE_FILTER: {
			let { uid, reportId } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.id === reportId) {
						report.filters = report.filters.map((filter) => {
							if (filter.uid === uid) {
								filter = action.payload;
								filter.configId = uuidv4();
							}
							return filter;
						});
					}
					return report;
				});
			});
		}
		case actionTypes.UPDATE_FILTER_MAPPING: {
			let { tempFilter, type } = action.payload;
			let { uid } = tempFilter;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.filters = report.filters.map((filter) => {
							if (filter.uid === uid) {
								if (type === 'cascade') {
									filter.cascade = tempFilter.cascade;
								} else {
									let {
										mapping,
										valuesMode,
										databaseFunction,
										column,
										backendDataType,
										dataType
									} = tempFilter;
									mapping.isEnabled = true;
									filter = {
										...filter,
										mapping,
										valuesMode,
										databaseFunction,
										column,
										backendDataType,
										dataType,
										values: []
									};
									if (!filter.databaseFunction) {
										delete filter.databaseFunction;
									}
									if (tempFilter.aggregate) {
										filter.aggregate = tempFilter.aggregate;
									}
								}
								filter.configId = uuidv4();
							}
							return filter;
						});
					}
					return report;
				});
			});
		}
		case actionTypes.TOGGLE_FILTER_VISIBILITY: {
			let { reportId, uid } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.id === reportId) {
						report.filters = report.filters.map((filter) => {
							if (filter.uid === uid) {
								filter.hideInViewMode = !filter.hideInViewMode;
							}
							return filter;
						});
					}
					return report;
				});
			});
		}
		case actionTypes.TOGGLE_FILTER_IGNORANCE: {
			let { reportId, uid } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.id === reportId) {
						report.filters = report.filters.map((filter) => {
							if (filter.uid === uid) {
								filter.ignore = !filter.ignore;
							}
							return filter;
						});
					}
					return report;
				});
			});
		}
		case actionTypes.TOGGLE_FILTER_UNIQUE: {
			let { reportId, uid } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.id === reportId) {
						report.filters = report.filters.map((filter) => {
							if (filter.uid === uid) {
								filter.mapping.unique = !filter.mapping.unique;
							}
							return filter;
						});
					}
					return report;
				});
			});
		}
		case actionTypes.CHANGE_FILTER_VALUES: {
			let { value, uid, reportId, mode } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.id === reportId || mode === "filter") { // mode is added for the 5993
						report.filters = report.filters.map((filter) => {
							if (filter.uid === uid) {
								filter = addFilterValue(filter, value);
							}
							return filter;
						});
					}
					return report;
				});
			});
		}
		case actionTypes.UPDATE_ANCHOR_DATE: {
			let { uid, value: val, anchorDateData, reportId } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.id === reportId) {
						report.filters = report.filters.map((filter) => {
							if (filter.uid === uid) {
								// if (anchorDateData.isAnchor) {
								let {
									anchorDate,
									isAnchor,
									active,
									relativePart,
									value,
									direction,
									lastInput,
									nextInput,
									part
								} = anchorDateData;
								filter = {
									...filter,
									anchor: {
										anchorDate,
										isAnchor,
										active,
										relativePart,
										value,
										direction,
										lastInput,
										nextInput,
										part
									}
								};
								// }
								filter = addFilterValue(filter, val);
								filter = { ...filter, dateValuesType: 'datePicker' };
							}
							return filter;
						});
					}
					return report;
				});
			});
		}
		case actionTypes.CHANGE_ACTIVE_SCRIPT: {
			let { id } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.selectedScript = report.selectedScript === id ? null : id;
					}
					return report;
				});
			});
		}
		case actionTypes.ADD_NEW_SCRIPT: {
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						let newScript = {
							id: `hdi-custom-script-${uuidv4()}`,
							value: ''
						};
						report.scripts.push(newScript);
						report.selectedScript = newScript.id;
					}
					return report;
				});
			});
		}
		case actionTypes.DELETE_SCRIPT: {
			let { id } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.scripts = report.scripts.filter((script) => {
							return script.id !== id;
						});
						if (id === report.selectedScript) {
							report.selectedScript = null;
						}
					}
					return report;
				});
			});
		}
		case actionTypes.CHANGE_EDITOR_CONTENT: {
			let { id, value } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						if (id) {
							report.scripts = report.scripts.map((script) => {
								if (script.id === id) {
									script.editingValue = value;
								}
								return script;
							});
						} else {
							report.styles = value;
						}
					}
					return report;
				});
			});
		}
		case actionTypes.APPLY_REPORT_SCRIPTS: {
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.scripts = report.scripts.map((script) => {
							script.value = script.editingValue;
							return script
						});
					}
					return report;
				});
			});
		}
		case actionTypes.CHANGE_OPTIONS: {
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.options = action.payload;
					}
					return report;
				});
			});
		}
		case actionTypes.ADD_FIELD_TO_MARKS: {
			let data = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						let { fieldId } = data;
						let newField = report.fields.find((field) => field.id === fieldId);
						if (newField) {
							let id = uuidv4();
							data.fieldId = id;
							report.fields.push({ ...newField, id, addedAs: data.addedAs });
						}
						report = addMarkField(data, report);
					}
					return report;
				});
			});
		}
		case actionTypes.UPDATE_SUB_VIZ_TYPE: {
			let { value, name, id } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.marksList = report.marksList.map((mark) => {
							if (value === '_all_' || mark.id === id) {
								mark.subVizType = name;
							}
							// if (value === '_all_' || mark.value === value) {
							// 	mark.subVizType = name;
							// }
							return mark;
						});
					}
					return report;
				});
			});
		}
		case actionTypes.REMOVE_FIELD_FROM_MARKS: {
			let { mark, field, markType } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						let measure = mark.value;
						report.marksList = report.marksList.map((mark) => {
							if (mark.value === measure) {
								let fieldList = mark[markType]['fields'];
								if (markType === 'size' || markType === 'color') {
									fieldList = [];
								} else {
									fieldList = fieldList.filter((markField) => markField.id !== field.id);
								}
								mark[markType]['fields'] = fieldList;
							}
							return mark;
						});
						let isExistAsOtherMark = report.marksList.some((mark) => {
							return markTypes.some((markType) => {
								return mark[markType].fields.some((item) => item.id === field.id);
							});
						});
						report.fields = report.fields.filter((canvasField) => {
							if (field.id === canvasField.id) {
								return isExistAsOtherMark || ['column', 'row'].includes(canvasField.addedAs);
							} else {
								return true;
							}
						});
					}
					return report;
				});
			});
		}
		case actionTypes.CHANGE_INTERACTIVITY: {
			let { toolbarConfig, reportId, ...rest } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.id === reportId) {
						toolbarConfig = toolbarConfig || report.toolbarConfig;
						if (report.drillThrough !== rest.drillThrough) {
							report.drillThroughList = [];
							report.activeDrillthroughId = null;
							report.fields = report.fields.filter((field) => field.addedAs !== "drillthrough_field"); //8419
						}
						report = { ...report, ...rest, toolbarConfig };
					}
					return report;
				});
			});
		}
		case actionTypes.TOGGLE_COMBINE_CHARTS: {
			let { reportId } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.id === reportId) {
						report.combine = !report.combine;
					}
					return report;
				});
			});
		}
		case actionTypes.LOAD_REPORT_STATE: {
			let tempReport = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report = { ...report, ...tempReport };
					}
					return report;
				});
			});
		}
		case actionTypes.LOAD_DRILL_REPORT: {
			let tempReport = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.drillThroughList = report.drillThroughList || [];
						if (report.drillThroughList.length === 0) {
							let currentItem = { ...report };
							delete currentItem.drillThroughList;
							report.drillThroughList.push({
								drillThroughId: report.id,
								saveData: currentItem,
								parameters: currentItem.filters,
								root: true,
								active: true
							});
						}
						tempReport.interactiveMode = true;
						tempReport.drillDown = true;
						tempReport.drillThrough = true;
						let item = {
							drillThroughId: uuidv4(),
							saveData: tempReport,
							reportInfo: tempReport.reportInfo,
							parameters: tempReport.filters
						};
						report.drillThroughList[1] = item;
					}
					return report;
				});
			});
		}
		case actionTypes.LOAD_REPORT_DATA: {
			let {
				reportData,
				loadState,
				selectedType,
				mode,
				id,
				fromDrillThrough,
				activeDrillthroughId
			} = action.payload;
			return produce(state, (draft) => {
				if (!loadState) {
					//create and edit mode
					draft.reports = draft.reports.map((report) => {
						if (report.id === id) {
							report.selectedType = selectedType;
							report.reportData = reportData;
							report.cellMenuData = null;
							if (report.drillThrough && report.id !== report.activeDrillthroughId) {
								report.activeDrillthroughId = report.id;
							}
						}
						return report;
					});
				} else if (fromDrillThrough) {
					let report = getIntialReportState({ active: false });
					let res = _.omit(action.payload, 'loadState');
					let drillThroughId = res.drillThroughId;
					draft.reports = draft.reports.map((report) => {
						if (report.id === res.id) {
							report.activeDrillthroughId = drillThroughId;
							report.reportData = res.reportData;
						}
						return report;
					});
					if (res.drillThroughList[0].drillThroughId === drillThroughId) return draft;
					report = {
						...report,
						...res,
						cellMenuData: null,
						id: res.drillThroughId
					};
					let findIndex = draft.reports.findIndex((report) => report.id === drillThroughId);
					if (findIndex > -1) {
						draft.reports[findIndex] = report;
					} else {
						draft.reports.push(report);
						draft.baseStateReports.push(cloneDeep(report));
					}
				} else {
					// edit old report

					if (['dashboard', 'filter'].includes(mode)) {

						const isCreateOrEditMode = draft.reports.some((report) => ["create", "edit"].includes(report.mode)); //8373 fix
						if (isCreateOrEditMode) return draft;

						let report = getIntialReportState({ active: false });
						let res = _.omit(action.payload, 'loadState');
						if (mode === 'filter') { // 7709 change
							res = {
								...res,
								reportData: { ..._.omit(res.reportData, 'data'), data: [] }
							};
						}
						report = { ...report, ...res, cellMenuData: null };
						let findIndex = draft.reports.findIndex((report) => report.id === res.id);
						if (findIndex > -1) {
							draft.reports[findIndex] = report;
						} else {
							draft.reports.push(report);
							draft.baseStateReports.push(cloneDeep(report));
						}
						draft.activeReportId = null;
					} else {
						if (!draft.reports.length) {
							let report = getIntialReportState({ active: true });
							draft.reports = [report];
						}
						draft.reports = draft.reports.map((report) => {
							if (report.active) {
								delete action.payload.loadState;
								report = { ...report, ...action.payload, cellMenuData: null };
								draft.activeReportId = report.id;
							}
							return report;
						});
					}
				}
			});
		}

		case actionTypes.SET_HREPORT_LOADING: {
			let {
				// id,
				loading
			} = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.hreportLoading = loading;
					}
					return report;
				});
			});
		}
		case actionTypes.SET_HREPORT_EDIT_LOADING: {
			return produce(state, (draft) => {
				draft.hreportEditLoading = action.payload;

			});
		}
		case actionTypes.SET_HREPORT_STYLES_ID: {
			let {
				id, styles
			} = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.stylesId = id;
						if (styles) {
							report.styles = styles;
							report.savedStyles = styles;
						}
					}
					return report;
				});
			});
		}

		case actionTypes.SAVE_HREPORT_STYLES: {
			let {
				styles
			} = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.savedStyles = styles;
					}
					return report;
				});
			});
		}


		case actionTypes.UPDATE_SQL_QUERY: {
			let { query } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report = { ...report, sqlString: query };
					}
					return report;
				});
			});
		}
		case actionTypes.UPDATE_PREVIEW_STATE: {
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.preview = report.preview ? false : true;
					}
					return report;
				});
			});
		}
		case actionTypes.UPDATE_FULLSCREEN_STATE: {
			const fullScreenMode = action.payload
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.fullscreen = fullScreenMode
					}
					return report;
				});
			});
		}
		case actionTypes.UPDATE_REPORT_FILE: {
			let { location, reportName, uuid, mode } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.reportInfo = { location, reportName, uuid };
						report.mode = mode;
					}
					return report;
				});
			});
		}
		case actionTypes.SET_MENU_DATA: {
			let { reportId, menu } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.id === reportId) {
						report.cellMenuData = menu;
					}
					return report;
				});
			});
		}
		case actionTypes.UPDATE_DRILL_THROUGH_LIST: {
			let { data, drillThroughId, fltr } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.drillThroughList = report.drillThroughList.map((currentItem) => {
							if (currentItem.drillThroughId === drillThroughId) {
								currentItem.parameters = currentItem.parameters.map((mapFilter) => {
									// if (mapFilter.column === fltr.column) { // 7937 fix, instead of column, check should be on the alias/label/autogen_alias
									if (getFieldDisplayName(mapFilter) === getFieldDisplayName(fltr)) {
										Object.keys(data).map((key) => {
											mapFilter[key] = data[key];
										});
									}
									return mapFilter;
								});
							}
							return currentItem;
						});
					}
					return report;
				});
			});
		}
		case actionTypes.REMOVE_DRILL_REPORT: {
			let { drillThroughId } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						let isActive = false;
						report.drillThroughList = report.drillThroughList.filter((currentItem) => {
							if (currentItem.drillThroughId === drillThroughId) {
								report.fields = report.fields.filter((field) => field.addedAs !== "drillthrough_field"); //8419
								isActive = true;
								return false;
							}
							return true;
						});
						if (isActive && report.drillThroughList[0]) {
							report.drillThroughList[0].active = true;
						}
					}
					return report;
				});
			});
		}
		case actionTypes.UPDATE_REPORT_PROPERTY: {
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					const { properties = {} } = action.payload || {}
					// const {
					// 	itemsData,
					// 	fields,
					// 	fieldId,
					// 	showAllColorFields,
					// 	showAllFormatFields,
					// 	reset,
					// 	formatColorField,
					// 	formatColorStyle,
					// 	reportData,
					// 	axisRangeId, axisRangeDataType, axisRangeFields, synchronize
					// } = action.payload;
					// let initialReport = getIntialReportState({ active: true });
					// if (reset && report.active) {
					// 	return { ...report, properties: initialReport.properties, reportData: { ...report.reportData, properties: initialReport.properties } };
					// }
					// if (report.active) {
					// 	report.reportData.dataId = uuidv4();
					// 	report.properties.title = getUpdatedProperties(itemsData, 'title');
					// 	report.properties.subTitle = getUpdatedProperties(itemsData, 'subTitle');
					// 	report.properties.format = getFormatFields(itemsData, 'format', fields, fieldId, showAllFormatFields);
					// 	report.properties.card = getUpdatedCardProperties(itemsData, 'card');
					// 	report.properties.axisRange = getUpdatedRangeProperties(reportData, itemsData, 'axisRange', axisRangeFields, axisRangeId, axisRangeDataType, synchronize, report?.reportData?.metadata);
					// 	report.properties.cache = getUpdatedProperties(itemsData, 'cache', initialReport);
					// 	report.properties.formatColor = getUpdatedColorProperties(
					// 		itemsData,
					// 		'color',
					// 		showAllColorFields,
					// 		formatColorField,
					// 		formatColorStyle,
					// 		report
					// 	)
					// 	report.properties.bar = getUpdatedProperties(itemsData, 'bar');
					// 	report.properties.radial = getUpdatedProperties(itemsData, 'radial');
					// 	report.properties.legend = getUpdatedProperties(itemsData, 'legend');
					// 	report.properties.labels = getUpdatedProperties(itemsData, 'labels');
					// 	report.properties.crosstab = getUpdatedProperties(itemsData, 'crosstab');
					// 	report.properties.table = getUpdatedProperties(itemsData, 'table');
					// 	report.properties.map = getUpdatedProperties(itemsData, 'map');
					// 	report.properties.line = getUpdatedProperties(itemsData, "line");
					// 	report.properties.progress = getUpdatedProperties(itemsData, "progress");
					// 	report.properties.radar = getUpdatedProperties(itemsData, "radar");
					// 	report.reportData.properties = report.properties;
					// }
					if (report.active) {
						report.reportData.dataId = uuidv4();
						report.properties = { ...report.properties, ...properties }
						report.reportData.properties = report.properties;
					}
					return report;
				});
			});
		}
		case actionTypes.UPDATE_REPORT_LAYOUT: {
			let { pane } = action.payload;
			return produce(state, (draft) => {
				draft.layout = { ...draft.layout, [pane]: !draft.layout[pane] };
			});
		}
		case actionTypes.UPDATE_ANALYTICS: {
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.analytics = report.analytics.map((item) => {
							if (item.key === action.payload.key) {
								item.value = !item.value;
							}
							return item;
						});
					}
					return report;
				});
			});
		}
		case actionTypes.UPDATE_GEO_JSON_DATA: {
			const { data, type } = action.payload;
			return produce(state, (draft) => {
				draft.geoJsonData = {
					...draft.geoJsonData,
					[type]: data
				}
			});
		}
		case actionTypes.UPDATE_GEOGRAPHIC_TYPE: {
			let { group, key, id } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.fields = report.fields.map((field) => {
							if (field.id === id) {
								if (field.geographicType === key) {
									field.geographicType = ""
								} else {
									field.geographicType = key;
								}
							}
							return field;
						});
					}
					return report;
				});
			});
		}
		case actionTypes.SET_CHILD_REPORT_LOADING: {
			return { ...state, childReportLoading: action.payload }
		}
		case actionTypes.REMOVE_ADVANCE_AGGREGATE_FROM_REORT: {
			let { uid, type } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.filters = report.filters.map((filter) => {
							if (filter.uid === uid) {
								if (type === "display") {
									filter.displayAggregateForAdvanceFilters = []
								} else {
									filter.valueAggregateForAdvanceFilters = []
								}
							}
							return filter;
						});
					}
					return report;
				});
			});
		}
		case actionTypes.ABORT_REQUEST: {
			let aborted = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.isAborted = aborted
					}
					return report;
				});
			});
		}
		case actionTypes.RESET_TABLE_PROPERTY: {
			const { property, value } = action.payload
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.reportData.dataId = uuidv4();
						if (report.reportData && report.reportData.properties) {
							report.reportData.properties.table = {
								...report?.reportData?.properties?.table, [property]: value
							};
						}
						report.properties.table = {
							...report.properties.table, [property]: value
						}
					}
					return report;
				});
			});
		}

		case actionTypes.UPDATE_REFERENCE_LINE_VALUE: {
			const { payload } = action.payload
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.referenceLineList = report.referenceLineList.map((rLine) => {
							if (rLine?.id === payload?.id) {
								rLine = { ...payload }
							}
							return rLine
						})
					}
					return report;
				});
			});
		}

		case actionTypes.ADD_CONVERTED_DIMENSION_TO_MARKSLIST: {
			const { marksList } = action.payload
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.marksList = marksList
					}
					return report;
				});
			});
		}

		case actionTypes.ADD_CONVERTED_DIMENSION_TO_REFERENCELIST: {
			const { referenceLineList } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.referenceLineList = referenceLineList;
					}
					return report;
				});
			});
		}

		case actionTypes.UPDATE_CUSTOM_CHART: {
			const payload = action.payload
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.customChart = { ...report.customChart, ...payload }
					}
					return report;
				});
			});
		}

		case actionTypes.RESET_CUSTOM_CHART: {
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.customChart = initialCustomChart
					}
					return report;
				});
			});
		}

		case actionTypes.UPDATE_MAP_CHART_PROPERTIES: {
			const { properties } = action.payload
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.reportData.properties.map = {
							...report.reportData.properties.map, ...properties
						}
						report.properties.map = {
							...report.properties.map, ...properties
						}
					}
					return report;
				});
			});
		}

		case actionTypes.RE_RENDER_GRID_CHART: {
			const { selectedType, reportId } = action.payload
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.id === reportId) {
						report.reportData.selectedType = selectedType
						report.selectedType = selectedType
						report.reportData.dataId = uuidv4();
					}
					return report;
				});
			});
		}

		case actionTypes.CHANGE_TABLE_RECORDS_PER_PAGE: {
			let {
				page,
				refresh = false
			} = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.tableRecordsPerPage = page;
					}
					if (refresh) {
						report.reportData.dataId = uuidv4();
					}
					return report;
				});
			});
		}
		case actionTypes.RESET_ACTIVE_REPORT: {
			let { reportId, metadata, databaseFunctions, dateFunctions } = action.payload;
			return produce(state, (draft) => {
				let report = getIntialReportState({ active: true });
				report.id = reportId;
				report.metadata = metadata;
				report.databaseFunctions = databaseFunctions;
				report.dateFunctions = dateFunctions;
				// draft.reports = [report];
				draft.reports = draft.reports.map((r, i) => {
					if (r.id === reportId) {
						report.reportInfo.reportName = `Untitled ${i + 1}`;
						return report;
					}
					return r;
				})
				draft.activeReportId = report.id;
				draft.layout = layout;
				// draft.layout = {
				// 	metadataShelf: true,
				// 	toolsAreaShelf: true,
				// 	fieldsAreaShelf: true
				// };
			});
		}

		case actionTypes.ENABLE_MEASURE_MARKS: {
			const { enable } = action.payload
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.measures.enable = enable
					}
					return report;
				});
			});
		}

		case actionTypes.CHANGE_DATE_FILTER_FORMAT: {
			const { reportId, uid, format } = action.payload
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.id === reportId) {
						report.filters = report.filters.map((filter) => {
							if (filter.uid === uid) {
								filter.format = format
							}
							return filter;
						});
					}
					return report;
				});
			});
		}

		case actionTypes.ADD_CUSTOM_DISPLAY_DATE_FORMAT: {
			const { reportId, uid, newFormat } = action.payload
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.id === reportId) {
						report.filters = report.filters.map((filter) => {
							if (filter.uid === uid) {
								filter.customDisplayDateFormats.push(newFormat)
							}
							return filter;
						});
					}
					return report;
				});
			});
		}

		case actionTypes.UPDATE_CUSTOM_DISPLAY_DATE_FORMAT: {
			const { reportId, uid, key, remove } = action.payload
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.id === reportId) {
						report.filters = report.filters.map((filter) => {
							if (filter.uid === uid && remove) {
								filter.customDisplayDateFormats = filter.customDisplayDateFormats.filter((item) => item.key !== key)
							}
							return filter;
						});
					}
					return report;
				});
			});
		}

		case actionTypes.UPDATE_GRID_ITEMS_LAYOUT: {
			let { layout } = action.payload;
			return produce(state, (draft) => {
				draft.layout = { ...draft.layout, gridItemsLayout: layout };
			});
		}

		case actionTypes.UPDATE_REPORT_MODAL: {
			let { reportId, open } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.id === reportId) {
						report.reportModal = open;
					}
					return report;
				});
			});
		}

		case actionTypes.RESET_REPORTS_FILTERS: {
			return produce(state, (draft) => {
				draft.reports = draft.reports.map(report => {
					const baseReport = draft.baseStateReports.find(r => r.id === report.id);
					return cloneDeep({
						...report,
						filters: baseReport.filters,
						reportData: baseReport.reportData
					});
				})
			});
		}

		case actionTypes.ADD_CUSTOM_CHART_COLOR_PALETTE: {
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.chartColorPalette['Custom Colors'] = { ...report.chartColorPalette['Custom Colors'], ...action.payload };
					}
					return report;
				});
			});
		}

		case actionTypes.DELETE_CUSTOM_CHART_COLOR_PALETTE: {
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.chartColorPalette['Custom Colors'] = { ..._.omit(report.chartColorPalette['Custom Colors'], action.payload) };
						console.log(report.chartColorPalette)
					}
					return report;
				});
			});
		}

		case actionTypes.COPY_COLOR_PALETTE_TO_OTHER_REPORTS: {
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (!report.active) {
						report.chartColorPalette['Custom Colors'] = { ...report.chartColorPalette['Custom Colors'], ...action.payload };
					}
					return report;
				});
			});
		}


		case actionTypes.ADD_MEASURE_FIELD: {
			let data = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						let { fieldId } = data;
						let newField = report.fields.find((field) => field.id === fieldId);
						if (newField) {
							let id = uuidv4();
							report.fields.push({ ...newField, id, addedAs: data.addedAs });
						}
					}
					return report;
				});
			});
		}

		case actionTypes.REMOVE_DRILLTHROUGH_CHILD_REPORT_DATA: {
			let { id } = action.payload || {}
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.id === id) {
						report.reportData = { ..._.omit(report.reportData, 'data'), data: [] }
					}
					return report;
				});
			});
		}

		case actionTypes.SET_DRILLTHROUGH_ACTIVE: {
			let { id } = action.payload || {}
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.id === id) {
						report.isDrillthroughActive = true;
					}
					return report;
				});
			});
		}

		case actionTypes.UPDATE_TREND_FIELD: {
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.fields = report.fields.filter((field) => field.addedAs !== 'trend_field')
					}
					return report;
				});
			});
		}

		case actionTypes.UPDATE_FORMAT_PROPERTY: {
			const { id = '', values = [] } = action.payload || {}
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						if (report?.properties?.format) {
							if (report?.properties?.format?.formatFields?.find((item) => item.id === id)) {
								report.properties.format.formatFields = report.properties.format.formatFields.map((item) => {
									if (item.id === id) {
										return { ...item, values }
									}
									return item
								})
							} else {
								report.properties.format.formatFields.push(action.payload)
							}
						}
						if (report?.reportData?.properties?.format) {
							if (report?.reportData?.properties?.format?.formatFields?.find((item) => item.id === id)) {
								report.reportData.properties.format.formatFields = report.reportData.properties.format.formatFields.map((item) => {
									if (item.id === id) {
										return { ...item, values }
									}
									return item
								})
							} else {
								report.reportData.properties.format.formatFields.push(action.payload)
							}
						}
					}
					return report;
				})
			})
		}

		case actionTypes.ADD_VERSION_TO_REPORT: {
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.version = action.payload
					}
					return report;
				});
			});
		}

		case actionTypes.SHOW_ALL_VIZUALIZATIONS: {
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						report.showAllVisualizations = action.payload
					}
					return report;
				});
			});
		}

		case actionTypes.UPDATE_CARD_PROPERTIES: {
			const { properties } = action.payload
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.active) {
						if (report.reportData.properties) {
							report.reportData.properties.card = {
								...(report?.reportData?.properties?.card || {}), ...properties
							}
						}
						report.properties.card = {
							...(report?.properties.card || {}), ...properties
						}
					}
					return report;
				});
			});
		}

		case actionTypes.ENABLE_DRILLTHROUGH_REPORT_LINK: {
			let { enableDrillthroughReportLink, reportId } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.id === reportId) {
						report.enableDrillthroughReportLink = enableDrillthroughReportLink;
					}
					return report;
				});
			});
		}

		case actionTypes.UPDATE_TABLE_FILTERS: {
			let { filters = {}, reportId } = action.payload;
			return produce(state, (draft) => {
				draft.reports = draft.reports.map((report) => {
					if (report.id === reportId) {
						report.tableFilters = filters;
						report.reportData.dataId = uuidv4();
					}
					return report;
				});
			});
		}

		default:
			return { ...state };
	}
};

export default hreportReducer;
