
import produce from "immer";
import _, { cloneDeep, isEmpty } from 'lodash-es';
import { v4 as uuidv4 } from "uuid";
import requests from "../../../base/requests";
import Base64 from "../../../base/utils/Base64";
import { fileBrowserActions } from "../../../redux/actions";
import { addVersionToReport, isSavingHreports, loadDrillReport, loadMetadata, loadReportData, setAbortRequest, setChildReportLoading, setHReportEditLoading, setHReportLoading, setHrSidebar, setHreportSidebarLoading, setHreportStylesId, updateGeoJsonData, updateReportFile, updateSqlQuery } from "../../../redux/actions/hreport.actions";
import { MEASURE_NAME, MEASURE_VALUE, getIntialReportState } from "../../../redux/reducers/initialStates";
import { checkIsValidDateFilter, getRelativeDate, prepareRelativeOptionFromAnchor, rangeConditions, updateRelativeDateAnchor } from "../../../utils/filter-utils";
import ReportQuery from "../../../utils/reportQuery";
import { getFieldDisplayName, modifyFilters } from "../../../utils/utilities";
import { makeid } from "../../hi-dashboard-designer/utils/common-functions";
import notify from "../../hi-notifications/notify";
import { manipulateMDForMultiConn } from "./manipulateMDForMultiConn";
import { add_column, add_filter, add_mark, add_row, postExecution, postFetch, preExecution, preFetch } from "./utilities";


// export const addDrillReport = (data, list) => {
//     let item = {
//         report: data.report ? data.report : { title: "parent" },
//         saveData: data.saveData ? data.saveData : {},
//         parameters: data.parameters ? data.parameters : [],
//         active: data.active,
//         id: data.uid
//     }
//     list.push(item)
//     return list
// }


const conditionSymbols = {
	EQUALS: "=",
	NOT_EQUALS: "!=",
	IS_ONE_OF: "IN",
	IS_NOT_ONE_OF: "NOTIN",
	CONTAINS: "CONTAINS",
	DOES_NOT_CONTAINS: "DOES_NOT_CONTAINS",
	STARTS_WITH: "STARTS_WITH",
	DOES_NOT_STARTS_WITH: "DOES_NOT_STARTS_WITH",
	ENDS_WITH: "ENDS_WITH",
	DOES_NOT_ENDS_WITH: "DOES_NOT_ENDS_WITH",
	IS_LESS_THAN: "<",
	IS_GREATER_THAN: ">",
	IS_LESS_THAN_OR_EQUAL_TO: "<=",
	IS_GREATER_THAN_OR_EQUAL_TO: ">=",
	IS_BETWEEN: "IS_BETWEEN",
	IS_NOT_BETWEEN: "IS_NOT_BETWEEN",
	AFTER: "AFTER",
	BEFORE: "BEFORE",
	IS_ON_OR_BEFORE: "",
	IS_ON_OR_AFTER: "",
	CUSTOM: "CUSTOM",
	IS_NULL: "IS_NULL",
	IS_NOT_NULL: "IS_NOT_NULL",
	IN_RANGE: "INRANGE",
	NOT_IN_RANGE: "NOT_IN_RANGE"
}

const HREPORT_VERSION = 1

export const loadChildReport = (file, dispatch, getApi) => {
	dispatch(setChildReportLoading(true))
	fetchReport({ file, mode: "create" }, dispatch, getApi).then((state) => {
		dispatch(setChildReportLoading(false))
		dispatch(setHReportLoading({ loading: false }))
		dispatch(loadDrillReport(state));

	}).catch(() => {
		dispatch(setChildReportLoading(false))
		dispatch(setHReportLoading({ loading: false }))
	});
};

export const prepareFetchReportStateResponse = (res) => {
	let newRes = {}
	if (res?.data) {
		const { data, message = "" } = res || {}
		newRes = { ...data, message };
	} else {
		newRes = res;
	}
	return newRes;
}

const fetchReportState = (formData, dispatch, mode, getApi) => {
	if (mode === "open" || mode === "dashboard" || mode === "filter") {
		return new Promise((resolve, reject) => {

			let apiInstance = requests.hreport(dispatch).getReport(formData, "", (res) => {
				// return Promise.resolve(res);
				res = prepareFetchReportStateResponse(res); // response got changed so added this function  [6844]
				resolve(res)

			}, (err) => {
				resolve({ ...err, isNetworkCallFail: true });
			});
			typeof getApi === "function" && getApi(apiInstance)
		})
	}
	return new Promise((resolve, reject) => {


		let apiInstance = requests.hreport(dispatch).getReportForEdit(formData, "", (res) => {
			res = prepareFetchReportStateResponse(res); // response got changed so added this function [6844]
			// return Promise.resolve(res);
			resolve(res)
		}, (err) => {
			// dispatch(setAccessDeniedInfo({ subTitle: err.message }))
			// dispatch(updateRoute("/access-denied"))
			resolve({ ...err, isNetworkCallFail: true });
		});
		typeof getApi === "function" && getApi(apiInstance)

	})
}

const extractNestedDBFunctions = (data) => {
	const result = [];
	if (typeof data === 'object') {
		if (data.hasOwnProperty('value') && typeof data.value === 'string') {
			result.push({
				key: data.key,
				description: data.description,
				value: data.value,
				signature: data.signature,
				returns: data.returns,
				parameters: data.parameters.map(param => ({
					name: param.name,
					column: param.column,
				})),
			});
		}
		if (data.hasOwnProperty('parameters') && Array.isArray(data.parameters)) {
			for (const param of data.parameters) {
				const paramFunctions = extractNestedDBFunctions(param.value);
				result.push(...paramFunctions);
			}
		}
	}
	return result;
}

const getFiltersAppliedDbfs = (filters) => {
	let filtersDbfs = []
	filters?.forEach((filter) => {
		const { mapping = {} } = filter || {}
		const { DisplayDBFunction = {}, valueDBFunction = {} } = mapping || {}
		if (!_.isEmpty(DisplayDBFunction)) {
			filtersDbfs.push(DisplayDBFunction)
		}
		if (!_.isEmpty(valueDBFunction)) {
			filtersDbfs.push(valueDBFunction)
		}
	})
	return filtersDbfs;
}

const getNestedDatabaseFunctions = (res) => {
	const { fields, filters } = res || {}
	let extractedFunctions = []
	if (fields?.length) {
		fields?.forEach((field) => {
			if (field?.databaseFunction && typeof field?.databaseFunction === 'object') {
				const dbFunctions = extractNestedDBFunctions(field?.databaseFunction)
				extractedFunctions.push(dbFunctions)
			}
		})
	}
	if (filters?.length) {
		const filtersAppliedDbfs = getFiltersAppliedDbfs(filters)
		extractedFunctions = [...extractedFunctions, ...filtersAppliedDbfs]
	}
	return _.uniqBy(extractedFunctions?.flat(Infinity), 'key');
}

const checkIfDbfAvailable = (fns) => {
	return typeof fns === "object" && Object.keys(fns)?.length;
}

export const getAppliedDatabaseFunctions = (res) => {
	let databaseFunctions = []
	if (res?.state?.appliedDbfs && checkIfDbfAvailable(res?.state?.appliedDbfs)) {
		databaseFunctions = [...databaseFunctions, ...res?.state?.appliedDbfs]
		// return {
		// 	dbFunctions: res?.state?.appliedDbfs
		// }
	}
	const dbFns = getNestedDatabaseFunctions(res?.state)
	if (dbFns?.length) {
		databaseFunctions = [...databaseFunctions, ...dbFns]
		// return {
		// 	dbFunctions : dbFns
		// }
	}
	if (databaseFunctions?.length) {
		databaseFunctions = _.uniqBy(databaseFunctions?.flat(Infinity), 'key');
		return {
			dbFunctions: databaseFunctions
		}
	}
	return {}
}

const checkIfReportsPresent = (dispatch, mode) => {
	if (["edit", "create"].includes(mode)) return false
	let reports = [];
	dispatch((_, getState) => {
		reports = getState().hreport.present.reports;
	})
	return reports.some((report) => ["create", "edit"].includes(report.mode));
}


export const fetchReport = async ({ file, mode, parameters, fromDrillThrough = false }, dispatch, getApi) => {
	let res = {}
	let formData = { dir: "", file: "" }
	if (file.metadata) {
		let { metadata, columns, filters, rows, options, scripts, visualisationType, marks, ...rest } = file
		let selectedType = visualisationType || "Table"
		let metadataResponse = await fetchMetadata(metadata, dispatch)
		let state = getIntialReportState({ active: true })
		res = {
			metadata: { data: metadataResponse, ...metadata },
			state: { ...state, ...rest, selectedType }, reportName: "Untitled1"
		}
		columns?.map(field => {
			field.addedAs = "column"
			add_column(field, { ...res.state, metadata: metadataResponse }, dispatch)
		})
		rows?.map(field => {
			field.addedAs = "row"
			add_row(field, { ...res.state, metadata: metadataResponse }, dispatch)
		})
		filters?.map(field => {
			add_filter(field, { ...res.state, metadata: metadataResponse }, dispatch)
		})
		marks?.map(field => {
			add_mark(field, cloneDeep({ ...res.state, metadata: metadataResponse }), dispatch)
		})
	} else {
		formData = { dir: file.path?.replace(file.name, "")?.replace(/[\\|\/]+$/, ""), file: file.name }
		res = await fetchReportState(formData, dispatch, mode, getApi);
		// if (res?.state?.chartColorPalette && !isEmpty(res?.state?.chartColorPalette)) {
		// 	let colorPalettes = res.state.chartColorPalette?.['Custom Colors'] || {};
		// 	dispatch(addCustomChartColorPalette(colorPalettes));
		// }
		if (res.isNetworkCallFail) {
			return res // removed the Promise reject to remove the console error , 6635
			// return Promise.reject(res);
		} else {
			if (!["open", "dashboard"].includes(mode)) {
				const dateFunctions = await fetchDateFuncs({ contentId: "Static/standardDate" }, dispatch, getApi)
				if (res?.metadata) {
					const { functions, databaseFunctions } = await fetchFunctions(res?.metadata?.data, dispatch, getApi)
					res.state = {
						...res.state, functions, databaseFunctions, dateFunctions, mode,
					}
				} else {
					res.state = {
						...res.state, functions: {}, databaseFunctions: {}, dateFunctions, mode,
					}
				}
			}
			if (["open", "dashboard"].includes(mode)) { // fix for 6602
				let databaseFunctions = getAppliedDatabaseFunctions(res)
				let dateFunctions = {}
				if (res?.state?.filters?.length) {
					dateFunctions = await fetchDateFuncs({ contentId: "Static/standardDate" }, dispatch, getApi)
				}
				res.state = {
					...res.state, databaseFunctions, dateFunctions, mode,
				}
			}
			res.parameters = parameters
		}
	}
	return new Promise((resolve, reject) => {
		let areReportsPresent = checkIfReportsPresent(dispatch, mode) // 8373 fix
		if (areReportsPresent && !fromDrillThrough) reject({})
		if (res) {
			resolve(createActiveReport(res, formData, dispatch, mode))
		} else {
			resolve(false)
		}
	})

}

export const fetchGeojsonListingData = (formData, dispatch) => {
	formData = Base64.encode(JSON.stringify(formData));
	return new Promise((resolve, reject) => {
		requests.hreport(dispatch).getGeojsonListingData(formData, "", (res) => {
			const { response } = res || {}
			resolve(response)
		}, (err) => {

			resolve({ ...err, isNetworkCallFail: true });
		});
	})
}

export const fetchGeojsonData = async (formData, dispatch) => {
	return new Promise((resolve, reject) => {
		requests.hreport(dispatch).getGeoJsonData(formData, "", (res) => {
			resolve(res)
		}, (err) => {
			resolve({ ...err, isNetworkCallFail: true });
		});
	})
}

export const getGeoJsonData = async ({ value, id, type, successCB = () => { }, errorCB = () => { } }, dispatch) => {
	let res = {}
	res = await fetchGeojsonData(value, dispatch);
	return new Promise((resolve) => {
		if (res) {
			resolve(dispatch(updateGeoJsonData({ data: res, undoRedoAction: true, type })))
			successCB()
		} else {
			resolve(false)
			errorCB()
		}
	})
}

const dbfBuilder = (parameters, key, builder) => {
	let args = []
	parameters.map(param => {
		if (param.column) {
			args.push(param.value)
		} else if (param.value) {
			if (typeof param.value === "string") {
				args.push(param.value)
			} else {
				let { key, parameters } = param.value
				// key = param.value.value
				key = param.value.key.split(".").join("_")
				args.push(dbfBuilder(parameters, key, builder))
			}
		} else if (param.defaultValue) {
			args.push(param.defaultValue)
		};
	})
	// return (builder[key.toLowerCase()])(...args)
	return (builder[key])(...args)
}
export const selectBuilder = (field, database, query) => {
	let label = getFieldDisplayName(field)
	let { aggregate, column, groupBy, custom, databaseFunction, orderBy, applyBeforeAggregate, columnID } = field
	column = column || field.fullyQualifiedColumn
	database = database || field.columnDatabase;
	let fieldName = `${database ? `${database}.` : ""}${column}`
	if (custom) {
		fieldName = column
		query.selectRaw(fieldName, label)
	}
	// query.select(fieldName, label)
	query.select({ name: fieldName, id: columnID }, label)
	if (Array.isArray(aggregate) && aggregate.length) {
		[...aggregate].map(item => {
			let func = item.split('.').pop();
			if (!item.includes("aggregate")) return null;
			(query[func](label))
		})
	}
	if (applyBeforeAggregate) {
		query.applyBeforeAggregate(label)
	}
	if (Array.isArray(groupBy) && groupBy.length) {
		query.groupBy(label)
	}
	if (Array.isArray(orderBy) && orderBy.length) {
		query.orderBy({ [label]: orderBy[0] })
	}

	if (databaseFunction && Object.keys(databaseFunction).length) {
		let { key, parameters } = databaseFunction
		// key = databaseFunction.value
		key = key.split(".").join("_")
		query.functionBuilder((builder) => dbfBuilder(parameters, key, builder), label)
		// .functionBuilder((builder) =>{builder.sum(arg.alias)}, arg.alias)
	}

	if (field.hiddenIncludeInResultSet) {
		query.hide(label)
		query.hideAndIncludeInResultSet(label)
	} else if (field.hidden) {
		query.hide(label)
	}
}

// query.select(selectBuilder(field, database), "booking_plotform")

export const whereBuilder = ({ field, query, filterCondition, database }) => {
	let { condition, values, customCondition, encloseInQuotes, databaseFunction, column, ignore, columnID } = field
	column = column || field.fullyQualifiedColumn
	database = database || field.columnDatabase;
	let fieldName = `${database ? `${database}.` : ""}${column}`
	let label = getFieldDisplayName(field)
	let config = { label, mode: "auto", alias: label, mapping: field.mapping }
	if (Array.isArray(field.aggregate) && field.aggregate.length) {
		config.aggregate = field.aggregate
	}
	config.dataType = field.dataType
	if (condition === "CUSTOM") {
		config = { ...config, customCondition, isCustomValue: true, encloseInQuotes, condition }
		config.mode = "custom"
		// if (encloseInQuotes) {
		// 	config.values = values.map(val => `'${val}'`)
		// }
	}
	if (ignore) {
		config.ignore = true
	}
	if (databaseFunction) {
		config.databaseFunction = databaseFunction
	}
	if (filterCondition === "OR") {
		query.orWhere({ name: fieldName, id: columnID }, conditionSymbols[condition], values, config)
		// query.orWhere(fieldName, conditionSymbols[condition], values, config)
	} else {
		query.where({ name: fieldName, id: columnID }, conditionSymbols[condition], values, config)
		// query.where(fieldName, conditionSymbols[condition], values, config)
	}
}

export const checkEmptyFilters = (filters, dispatch) => {
	return false
	let filterNames = [];
	filters.map(f => {
		if ((f.condition != "IS_NULL" && f.condition != "IS_NOT_NULL") && f.values && f.values.length <= 0) {
			filterNames.push(f.label);
		}
	})
	if (filterNames.length > 0) {
		let message = `${filterNames.join(",")}'s are having empty values. please provide some values to this filter.`
		return true;
	} else {
		return false;
	}
}


export const getIndexForNewValue = (arr) => {
	if (!arr.length) return 0;
	const maxNumber = Math.max(...arr);
	let unusedNumber = maxNumber + 1;

	while (arr.includes(unusedNumber)) {
		unusedNumber++;
	}

	return unusedNumber;
}

export const getValueObjectForMeasure = (name, value, customFrontendFields) => {
	let obj = {}
	for (let field of customFrontendFields) {
		const fieldName = getFieldDisplayName(field)
		const { column } = field;
		switch (column) {
			case MEASURE_NAME:
				obj[fieldName] = name;
				break;
			case MEASURE_VALUE:
				obj[fieldName] = value;
				break;
			default:
				break;
		}
	}
	return obj;
}

export const measureDataUpdateFn = (data = [], metadata = [], measureFieldNames = [], customFrontendFields = [], chartType = '') => {
	if (!measureFieldNames.length || !data.length) return { data, metadata };
	if (customFrontendFields?.length > 1) {
		customFrontendFields = customFrontendFields.reduce((acc, curr) => {
			if (!acc.some((item) => item.column === curr.column)) {
				acc.push(curr);
			}
			return acc
		}, [])
	}
	const updatedMetadata = metadata.map((meta, index) => {
		if (index !== 0) return meta;
		const updatedMeta = {};
		const availableKeys = [];
		for (const key in meta) {
			if (measureFieldNames.includes(meta[key]?.name)) {
				delete meta[key];
			} else {
				availableKeys.push(key);
			}
		}
		if (customFrontendFields.length) {
			customFrontendFields.forEach((field) => {
				const name = getFieldDisplayName(field);
				let newIndex = getIndexForNewValue(availableKeys);
				availableKeys.push(newIndex);
				updatedMeta[newIndex] = { name, type: field?.floatingType === "discrete" ? "text" : "numeric" };
			})
		}
		return { ...meta, ...updatedMeta };
	})
	let result = [];
	const nonMeasureKeys = Object.keys(data[0]).filter(key => !measureFieldNames.includes(key));
	if (measureFieldNames?.length) {
		if (customFrontendFields?.length) {
			data.forEach(_d => {
				measureFieldNames.forEach(name => {
					const tempObj = getValueObjectForMeasure(name, _d[name], customFrontendFields);
					if (!isEmpty(tempObj)) {
						if (["Table", "S2Chart"].includes(chartType)) {
							tempObj[`MEASURE_FIELD_${name}`] = _d[name];
						}
						nonMeasureKeys.forEach(key => {
							tempObj[key] = _d[key];
						});
						result.push(tempObj);
					}
				});
			});
		} else {
			data.forEach(_d => {
				const tempObj = {};
				nonMeasureKeys.forEach(key => {
					tempObj[key] = _d[key];
				});
				result.push(tempObj);
			});
		}
	}
	return { data: result, metadata: updatedMetadata };
};


// const updateColorProperties = (reportData) => {
// 	const colorField = reportData?.properties?.formatColor?.formatColorField || ""
// 	if (!colorField) return reportData;
// 	const field = reportData.fields.find(field => field.id === colorField)
// 	if (!field || !checkIsContinousField(field)) return reportData;
// 	const fieldName = getFieldDisplayName(field);
// 	let fieldData = getFieldData(reportData.data, fieldName)
// 	if (fieldData?.length) {
// 		fieldData = fieldData.sort((a, b) => a - b)
// 		const min = fieldData[0],
// 			max = fieldData[fieldData.length - 1],
// 			center = parseInt((min + max) / 2);
// 		const updatedProperties = {
// 			minValue: min,
// 			maxValue: max,
// 			centerValue: center
// 		}

// 		return {
// 			...cloneDeep(reportData),
// 			properties: {
// 				...cloneDeep(reportData.properties || {}),
// 				formatColor: {
// 					...cloneDeep(reportData.properties?.formatColor || {}),
// 					...updatedProperties
// 				}
// 			},
// 		}
// 	}
// }


export const generateReport = (activeReport, dispatch, getApi, callback) => {
	const Notify = notify(dispatch);
	try {
		let {
			filters, fields, metadata, selectedType, options, tableLimitBy, offset, pageSize, loadState, id, generateQuery,
			getFormData, refresh, analytics, mode, requestId, databaseFunctions, printFormat, scripts, marksList,
			properties, isPrintMode, user = {}, isAborted, customChart = {}, tableFilters = {}
		} = activeReport
		delete activeReport.user
		requestId = requestId || uuidv4()
		if (!metadata) {
			Notify.warning({ type: "Frontend", message: "Metadata not found" });
			return new Promise(resolve => resolve(true));
		}
		let measureFields = fields.filter(field => field.addedAs === "measure_field");
		let customFrontendFields = fields.filter(field => field?.custom_frontend_field);
		fields = fields.filter(field => field.addedAs !== "measure_field");
		if (fields.length === 0) {
			Notify.warning({ type: "Frontend", message: "Please select some columns to generate a report." });
			return new Promise(resolve => resolve(true));
		}
		fields = [...fields, ...measureFields].filter((field) => !field?.custom_frontend_field);
		if (isAborted) {
			dispatch(setAbortRequest(false))
		}
		if (!generateQuery && !getFormData) {
			dispatch(setHReportLoading({ id, loading: true }))
			dispatch(setHReportEditLoading(false))
			if ((mode !== "edit" && mode !== "create") || (activeReport?.drillThrough && activeReport?.drillThroughId)) {
				dispatch(loadReportData({ ...activeReport, undoRedoAction: true, reportData: { loading: true } }))
			}
			// dispatch(loadReportData({ ...activeReport, reportData: { loading: true } }))
		}
		if (mode === "filter") {
			dispatch(loadReportData({ ...activeReport, loadState: true }))
			dispatch(setHReportLoading({ id, loading: true }))
			return null
		}
		selectedType = selectedType || "Table"
		let limitBy = options.sample !== "full" ? options.limitBy : -1
		if (selectedType === "Table" && !["csv", "xls", "email", "pdf"].includes(printFormat) && !customChart?.applied) {
			let recordsPerPage = activeReport?.reportData ? activeReport?.reportData?.properties?.table?.recordsPerPage : activeReport?.properties?.table?.recordsPerPage
			let fetchAllRecords = activeReport?.reportData ? activeReport?.reportData?.properties?.table?.fetchAllRecords : activeReport?.properties?.table?.fetchAllRecords
			pageSize = pageSize || activeReport?.reportData?.pageSize || activeReport?.tableRecordsPerPage || recordsPerPage || 10
			// pageSize = (recordsPerPage && recordsPerPage > pageSize) ? recordsPerPage : pageSize
			// pageSize = pageSize || activeReport?.reportData?.pageSize || 10
			limitBy = limitBy < 10 ? limitBy : pageSize
			if (tableLimitBy) {
				limitBy = tableLimitBy
			}
			if (fetchAllRecords) {
				limitBy = -1
			}
		}
		offset = offset || 0
		if (checkEmptyFilters(filters, dispatch)) {
			return new Promise(resolve => resolve(true));
		}

		fields = fields.filter(field => field.addedAs !== "drillthrough_field")
		let { name, formData } = metadata // refactor
		let reportData = preExecution({ ...activeReport, user }, dispatch)
		// let reportData = preExecution(activeReport, dispatch)
		let query = new ReportQuery({ ...formData, useDBFuntion: databaseFunctions }, dispatch)
		query.requestId(requestId)

		query.limit(limitBy, offset)
		query.togglePrependTable(options.prependTableNameToAlias)
		reportData.fields.map(field => {
			selectBuilder(field, name, query)  // - fix for 6400
			// selectBuilder(field, field.databaseName || name, query) - fix for 6400 branch name:- ch-6400-generateReport-select-builder-databaseName-issue sending only name as second parameter in selectBuiler 
		})
		reportData.filters.map(filter => {
			whereBuilder({ field: filter, query, fields, database: name }) // added cubeDatabase
		})

		if (refresh) {
			query.refresh(true)
		}
		if ((isPrintMode && selectedType === "Table" && !properties?.table?.fetchAllRecords) || printFormat === "pdf") {
			limitBy = options.sample !== "full" ? options.limitBy : -1
			query.limit(limitBy)
		}
		preFetch({ ...activeReport, ...reportData, query, user }, dispatch)
		// preFetch({ ...activeReport, ...reportData, query }, dispatch)
		if (getFormData) {
			return new Promise((resolve) => {
				resolve(query.formData)
			})
		}
		return new Promise((resolve) => {
			query.execute({
				query: !!generateQuery,
				getApi,
				callBack: (res) => {
					if (res.status === 200) {
					}
					let result = postFetch({
						...activeReport, ...reportData, data: res.data, user, reportMetadata: res.metadata
					}, dispatch)
					reportData = result?.reportData ? { ...reportData, ...result.reportData } : reportData
					res.data = result.data
					res.data = res.data || []
					// json datatype fixes
					res.data = res.data.map(record => {
						let fieldInfo = res.metadata[0]
						Object.values(fieldInfo).map(value => {
							if (value.type === "other") {
								record[value.name] = record[value.name].toString()
							}
						})
						return record
					})
					let selectedChartType = reportData.visualisation || "Table";
					if (measureFields?.length) { // 7216
						const measureFieldNames = measureFields?.map(field => getFieldDisplayName(field))
						const { data, metadata } = measureDataUpdateFn(res.data, res.metadata, measureFieldNames, customFrontendFields, selectedChartType)
						res.data = data;
						res.metadata = metadata
						reportData.fields = [...reportData.fields, ...customFrontendFields]
					}
					// json datatype fixes
					res.limitBy = limitBy
					res.offset = offset
					res.pageSize = pageSize
					res.isPrintMode = isPrintMode
					res.dataId = uuidv4()
					reportData.selectedType = selectedChartType
					reportData = { ...reportData, ...res }
					// reportData = updateColorProperties(reportData)

					const postExecutionScript = activeReport?.scripts?.find((script) => script?.id === "post-execution")
					if (loadState) {
						if (postExecutionScript?.value && postExecutionScript?.editingValue) {
							const postExecutionData = postExecution({ ...activeReport, ...reportData }, dispatch)
							const newActiveReport = { ...activeReport, reportData: { ...reportData, ...postExecutionData } }
							dispatch(loadReportData(newActiveReport))
							dispatch(setHReportLoading({ id, loading: false }))
							if (callback && typeof callback === "function") {
								callback(newActiveReport)
							}
						} else {
							dispatch(loadReportData({ ...activeReport, reportData }))
							dispatch(setHReportLoading({ id, loading: false }))
							if (callback && typeof callback === "function") {
								callback({ ...activeReport, reportData })
							}
						}
					} else if (generateQuery) {
						dispatch(updateSqlQuery({ query: res.query }))
					} else {
						dispatch(loadReportData({ reportData, selectedType, id }))
						dispatch(setHReportLoading({ id, loading: false }))
						if (postExecutionScript?.value && postExecutionScript?.editingValue) {
							const postExecutionData = postExecution({ ...activeReport, ...reportData }, dispatch)
							dispatch(loadReportData({ reportData: { ...reportData, dataId: uuidv4(), ...postExecutionData, metadata_file: reportData.metadata_file }, selectedType, id, undoRedoAction: true }))
						}
						if (callback && typeof callback === "function") {
							callback(reportData)
						}
					}
					resolve(true)
				},
				errorBack: (res) => {
					res.invalid = true
					if (loadState) {
						dispatch(loadReportData({ ...activeReport, reportData: { loading: false, data: [], ...res } })
						)
						dispatch(setHReportLoading({ id, loading: false }))
					} else if (generateQuery) {
					} else {
						dispatch(loadReportData({ reportData: { loading: false, data: [], ...res }, selectedType, id }))
						dispatch(setHReportLoading({ id, loading: false }))
					}
					resolve(false)
				}
			})
		})
	} catch (e) {
	}
}

export const saveReport = (activeReport, dispatch) => {
	let { reportInfo } = activeReport
	let { location, reportName } = reportInfo
	dispatch(isSavingHreports(true))
	let saveData = getSaveData({ ...activeReport });
	requests.hreport(dispatch).saveReport(saveData, "", (res) => {
		if (res && res.uuid) {
			dispatch(updateReportFile({ location, reportName, uuid: res.uuid, mode: "edit" }))
			if (res.data) {
				dispatch(fileBrowserActions.saveFileinFb(res.data))
			}
		}
		dispatch(isSavingHreports(false))
	}, (err) => {
		dispatch(isSavingHreports(false))
	}
	);
}
export const saveReportFile = (activeReport, dispatch) => {
	let { filters } = activeReport
	let _isEmpty = checkEmptyFilters(filters, dispatch);
	if (_isEmpty) return;
	saveReport(activeReport, dispatch)
	dispatch(addVersionToReport(HREPORT_VERSION))
}

const getChangedDataOnly = (initial, changed, type) => {
	if (type === 'object') {
		const newObj = _.cloneDeep(changed);
		const deleteUnchangedData = (initial, changed, path = '') => {
			_.forEach(changed, (value, key) => {
				const newPath = path ? `${path}.${key}` : key;
				if (_.isEqual(value, initial[key])) {
					if (_.isObject(value) && _.isObject(initial[key])) {
						deleteUnchangedData(initial[key], value, newPath);
						if (_.isEmpty(newObj[newPath])) {
							_.unset(newObj, newPath);
						}
					} else {
						_.unset(newObj, newPath);
					}
				}
			});
		};
		deleteUnchangedData(initial, newObj);
		return newObj;
	}
	if (type === 'array') {
		const newArr = [];
		const findChangedItems = (initial, changed) => {
			_.forEach(changed, (changedItem) => {
				const matchingItem = _.find(initial, (initialItem) => {
					return _.isEqual(initialItem, changedItem);
				});

				if (!matchingItem) {
					newArr.push(changedItem);
				}
			});
		};
		findChangedItems(initial, changed);
		return newArr;
	}
}

const getScripts = (initialScripts, changedScripts) => {
	const unchangedScripts = initialScripts.filter((item) => changedScripts.find((i) => i.id === item?.id)).filter(Boolean)
	return [...unchangedScripts, changedScripts]
}

const extractNestedDbfs = (dbf) => {
	const dbfsNames = [];
	if (typeof dbf === 'object') {
		if (dbf.hasOwnProperty('functionName')) {
			dbfsNames.push(dbf.functionName);
		}
		if (dbf.hasOwnProperty('key')) {
			dbfsNames.push(dbf.key);
		}
		for (const key in dbf) {
			dbfsNames.push(...extractNestedDbfs(dbf[key]));
		}
	} else if (Array.isArray(dbf)) {
		for (const item of dbf) {
			dbfsNames.push(...extractNestedDbfs(item));
		}
	}
	return dbfsNames;
}

const getDatabaseFunctions = (appliedDbfs, databaseFunctios) => {
	appliedDbfs = appliedDbfs?.flat(Infinity)
	let availableDatabaseFunctions = []
	for (let key in databaseFunctios) {
		availableDatabaseFunctions = [...availableDatabaseFunctions, ...databaseFunctios[key]]
	}
	const finalDbfs = availableDatabaseFunctions?.filter((dbf) => {
		return appliedDbfs?.includes(dbf?.key)
	})
	return finalDbfs;
}

export const extractAppliedDbFunctions = (reportData, databaseFunctions) => {
	const { fields = [], filters = [] } = reportData || {}
	let dbfs = fields?.map((field) => {
		if (field?.databaseFunction) {
			return field.databaseFunction
		}
	})?.filter(Boolean)
	if (filters?.length) {
		const filtersDbfs = getFiltersAppliedDbfs(filters)
		dbfs = [...dbfs, ...filtersDbfs];
	}
	if (dbfs?.length) {
		let databaseFunctionNames = []
		for (let dbf of dbfs) {
			let dbfName = extractNestedDbfs(dbf)
			databaseFunctionNames.push(dbfName)
		}
		const uniqueDbfs = [...new Set(databaseFunctionNames)]
		const dbFunctions = getDatabaseFunctions(uniqueDbfs, databaseFunctions)
		return dbFunctions;
	}
	return {};
}

export const getSaveData = (activeReport) => {
	let tempReport = activeReport
	if (activeReport.drillThroughList?.length) {
		tempReport = { ...tempReport, drillThroughList: activeReport.drillThroughList.slice(0, 2) }
	}
	const {
		id, mode, active, defaultValueDisplayMap, editingField, sqlString, cellMenuData, reportInfo, saveAs, reportData,
		metadata, databaseFunctions, dateFunctions, functions, cube, isDrillthroughActive, tableFilters, ...rest
	} = tempReport

	let { reportName, uuid, location } = reportInfo
	rest.filters = rest.filters.map((fltr) => {
		let { data, ...rest } = fltr
		return { ...rest, search: "", valuesList: [], active: false }
	})
	rest.drillThroughList = rest.drill ? [] : rest.drillThroughList.map((item, i) => {
		let { saveData, ...newItem } = item
		return { ...newItem, active: i === 0 ? true : false }
	})
	let data = {
		isHrReport: true,
		columns: rest.fields,
		state: rest,
		// metadata: metadata.formData,
		classifier: metadata.classifier
	};
	cube ? (data.cube = { ...cube }) : (data.metadata = metadata.formData); // sending cube file details in report while saving hreport
	data.reportName = reportName;
	data.location = location;
	if (uuid && !saveAs) {
		data.uuid = uuid;
	}
	//  added changes for 6630 , only  changed properties will go with formdata 
	let changedProperties = getChangedDataOnly(getIntialReportState({ active: true })?.properties, data?.state?.properties, 'object')
	// let changedScripts = getChangedDataOnly(getIntialReportState({active:true})?.scripts, data?.state?.scripts,'array')
	data.state.properties = changedProperties
	// data.state.scripts = changedScripts
	//---------------- 6630 -----------

	//fix for 6671 && 6602
	const appliedDbFunctions = extractAppliedDbFunctions(rest, databaseFunctions)
	data.state.appliedDbfs = appliedDbFunctions;

	data.state.version = HREPORT_VERSION;

	return data;
}
const createReportFromOldState = (state) => {
	if (!state.fields) {
		state.fields = state.columns
		delete state.columns
		state.selectedType = state.visualisation.selectedType
	}
	return state
}
const createActiveReport = (res, formData, dispatch, mode) => {
	let { reportName, metadata, state, parameters, cube } = res
	const Notify = notify(dispatch);

	if (cube) {
		let tables = getTablesFromCube({ cube: res.cubes[0] });
		let data = {   // converting cube to metadata
			...res.metadata.data,
			metadataName: cube.fileName.split('.')[0], // cube
			metadataDir: cube.location, // cube
			tables,
			isCustomMetadata: true,
			cubes: res.cubes,
			metadata: { ...metadata },
			...cube
		};
		metadata = {
			...res.metadata,
			data
		}
		// res.isCube = true
		state.isCube = true
		state.cube = cube
		// state.hrSidebar = 'cube'
		dispatch(setHrSidebar('cube'));
	}
	if (state.selectedType === "CrossTab") {
		Notify.warning({ type: "Frontend", message: "Crosstab has been removed, please switch to Grid table." });
	}
	if (metadata) {
		let { location, metadataFileName } = metadata
		let metadataInfo = { ...metadata.data, formData: { location, metadataFileName }, uid: uuidv4() }
		Object.keys(metadataInfo.tables).forEach(tableName => {
			metadataInfo.tables[tableName].key = uuidv4()
		})
		state.metadata = metadataInfo
	}
	if (parameters && Object.keys(parameters).length) {
		res.state = modifyFilters(res.state, parameters)
	}
	state.marksList = state.marksList.map(mark => {
		if (!mark.detail) {
			mark = { ...mark, detail: { fields: [] } }
		}
		return mark
	})
	state.filters = state.filters.map(filter => {
		if (filter.mapping && filter.mapping.unique === undefined) {
			filter = { ...filter, mapping: { ...filter.mapping, unique: true } }
		}
		if (filter?.anchor?.relativePart) {
			filter = getRelativeDate(filter);
		}
		if (["dashboard", "filter"].includes(mode) && checkIsValidDateFilter(filter)) {
			let isRange = rangeConditions.includes(filter?.condition);
			filter = updateRelativeDateAnchor(filter, parameters, isRange);
			let { values, anchor } = filter || {};
			if (Array.isArray(values)) {
				values = [...values].map((v, _i) => prepareRelativeOptionFromAnchor({ anchor, isRange, index: _i + 1, actualValue: v }));
			}
			filter = { ...filter, values }
		}
		return filter
	})
	state = createReportFromOldState(state)
	let { dir, file } = formData
	let reportInfo = {
		location: dir,
		uuid: file,
		reportName
	}
	state.reportInfo = reportInfo
	state.properties = { ...getIntialReportState({ active: true })?.properties, ...state.properties } // added this for 6630
	state.showAllVisualizations = state?.showAllVisualizations ?? false;
	// state.scripts = state?.scripts?.length < 1 ? getIntialReportState({ active: true })?.scripts : getScripts(getIntialReportState({ active: true })?.scripts, state?.scripts) // added this for 6630
	if (res?.message) {
		state.message = res?.message || ""
	}
	return state
}

export const setActiveReport = ({ list, index, updatedData, deleteId }) => {
	return produce(list, draft => {
		let activeIndex = draft.findIndex(item => item.active)
		draft[activeIndex].active = false
		draft[activeIndex].drillData = null
		if (updatedData.id !== draft[activeIndex].drillThroughId) {
			draft[activeIndex].drillThroughId = updatedData.id
		}
		if (index === 0) {
			draft[0].active = true
		} else {
			draft[activeIndex + index].active = true
		}
		if (deleteId) {
			draft = draft.filter(item => item.id !== deleteId)
		}
	})
}
const fetchMetadata = async (formData, dispatch, getApi) => {
	return new Promise((resolve, reject) => {
		let apiIntsance = requests.hreport(dispatch).getMetadata(formData, "", res => {
			resolve(res)
		}, e => {
			resolve(false)
		}
		);
		typeof getApi === "function" && getApi(apiIntsance)
	})
}
const fetchFunctions = async (formData, dispatch, getApi) => {
	let { classifier, uniqueId, metadataName, metadataDir, metadataFileName } = formData;
	let metadata = {
		location: metadataDir,
		classifier: classifier,
		metadataFileName: metadataFileName || (uniqueId + ".metadata"),
		metadataName,
		metadataDir,
	};
	// console.log()
	return new Promise((resolve, reject) => {
		let apiIntsance = requests.hreport(dispatch).getFunctions(metadata, "", res => {
			resolve(res)
		}, e => {
			resolve(false)
		}
		);
		typeof getApi === "function" && getApi(apiIntsance)
	})
}

const fetchDateFuncs = async (formData, dispatch, getApi) => {
	return new Promise((resolve, reject) => {
		let apiIntsance = requests.hreport(dispatch).getDateFunctions(formData, "", res => {
			resolve(res)
		}, e => {
			resolve(false)
		}
		);
		typeof getApi === "function" && getApi(apiIntsance)
	})
}


export const openMetadata = async (formData, dispatch, getApi) => {
	// dispatch(setMetadataLoading({ loading: true }))
	dispatch(setHreportSidebarLoading({ loading: true }))
	const metadataResponse = await fetchMetadata(formData, dispatch, getApi);
	// dispatch(setMetadataLoading({ loading: true }))

	// if (!metadataResponse) return dispatch(setMetadataLoading({ loading: false }))
	if (!metadataResponse) return dispatch(setHreportSidebarLoading({ loading: false, undoRedoAction: true }))

	metadataResponse.formData = formData;
	const functionsResponse = await fetchFunctions(metadataResponse, dispatch, getApi)
	const dateFunctionsResponse = await fetchDateFuncs({ contentId: "Static/standardDate" }, dispatch, getApi)
	if (metadataResponse && functionsResponse && dateFunctionsResponse) {
		dispatch(
			loadMetadata({
				metadata: manipulateMDForMultiConn({ metadata: metadataResponse }),
				funcs: functionsResponse,
				dateFunctions: dateFunctionsResponse || {},
			})
		);
		// dispatch(setMetadataLoading({ loading: false }))
		dispatch(setHreportSidebarLoading({ loading: false, undoRedoAction: true }))

	} else {
		// dispatch(setMetadataLoading({ loading: false }));
		dispatch(setHreportSidebarLoading({ loading: false, undoRedoAction: true }))

	}
};

function getColumn(ele) {
	return {
		[ele.columnName]: {
			alias: ele.dimensionName || ele.hierarchyName || ele.levelName || ele.measureName,
			fullyQualifiedColumn: `${ele.table.name}.${ele.columnName}`,
			columnId: ele.columnId || ele.primaryColumnId,
			defaultFunction: null,
			type: ele.dimensionType || ele.hierarchyType || ele.levelType || ele.measureType,
			key: uuidv4()
		}
	}
}

function handleTableColumnFormation(ele, tables) {
	if (Object.keys(tables).includes(ele.table.name)) { // if table name present in tables
		if (Object.keys(tables[ele.table.name].columns).includes(ele.columnName)) {//if column present in table

		} else {//if column not present in table
			tables[ele.table.name].columns = { ...tables[ele.table.name].columns, ...getColumn(ele) };
		}
	} else {   // if table name not present in tables
		const table = {
			[ele.table.name]: {
				id: ele.tableId,
				alias: ele.table.alias,
				name: ele.table.name,
				key: uuidv4(),
				columns: {
					...getColumn(ele)
				}
			}
		}
		tables = { ...tables, ...table }
	}
	return tables;
}

function getTablesFromCube({ cube }) {
	let tables = {};
	cube.dimensions.forEach(dimension => {
		tables = handleTableColumnFormation(dimension, tables)
		const hierarchy = dimension.hierarchies[0];
		tables = handleTableColumnFormation(hierarchy, tables)
		hierarchy.levels.forEach(level => {
			tables = handleTableColumnFormation(level, tables);
		})
	})
	cube.measures.forEach(measure => {
		tables = handleTableColumnFormation(measure, tables);
	})
	return tables;
}

export const openCube = async (formData, dispatch) => {
	dispatch(loadMetadata({ loading: true, isCube: true }));
	const dateFunctionsResponse = await fetchDateFuncs({ contentId: "Static/standardDate" }, dispatch)
	dateFunctionsResponse && requests.hreport(dispatch).getCube(
		formData,
		"",
		async (res) => {
			// dispatch(saveGetCubeRes(res));
			const { metadata: cubeMetadata } = res;
			let fetchFunctionsFormData = {
				location: cubeMetadata.location,
				classifier: "db.generic",
				metadataFileName: cubeMetadata.metadataFileName,
				metadataName: cubeMetadata.metadataFileName.split('.')[0],
				metadataDir: cubeMetadata.location,
			};
			const functionsResponse = await fetchFunctions(fetchFunctionsFormData, dispatch);
			const data = {   // converting cube to metadata
				...res,
				classifier: "db.generic",
				// name: 'HIUSER',
				metadataName: res.fileName.split('.')[0], // cube
				metadataDir: res.location, // cube
				formData: res.metadata,
				tables: getTablesFromCube({ cube: res.cubes[0] }),
				isCustomMetadata: true
			}
			functionsResponse && dispatch(
				loadMetadata({
					metadata: data,
					isCube: true,
					funcs: functionsResponse,
					dateFunctions: dateFunctionsResponse || {},
					cube: {
						location: res.location,
						fileName: res.fileName
					}
				})
			);

		},
		(e) => {
			dispatch(loadMetadata({ loading: false }));
		}
	);
};

// export function handleCubeToMetdatadaInHreport() {

// }


export const replaceStylesAndStylesIdInActiveReport = ({ styles, stylesId, dispatch }) => {
	const str = styles.slice()
	const searchString = stylesId.slice()
	const regex = new RegExp(searchString, 'gi')
	const replacementString = `hi-report-${makeid({ hreport: true })}`
	const result = str.replace(regex, replacementString)
	styles = result
	stylesId = replacementString
	dispatch(setHreportStylesId({ id: stylesId, styles }))
}











