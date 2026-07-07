// import extractDatabaseFunctions from './extractDBFunction'
// // import { Map } from "immutable";
// import _ from "lodash";
// import { List as iList, Map as iMap, fromJS } from "immutable";
// const prepareFilterFormData = (arg, that) => {
//     let {
//         column = '',
//         aggregate,
//         orderBy,
//         mapping = {},
//         cascade = {},
//         datePart,
//         databaseFunction = {},
//         dataSource = {},
//         database = '',
//         searchText = '',
//         columns : columnsFromArg,
//         alias : aliasFromArg,
//         limitBy = 50,
//         offset = 0
//     } = arg
//     let col = `${database}.${column}`.replace(/^\./, "")
//     let colArr = column.split('.');
//     let modifiedAlias = aliasFromArg || colArr[colArr.length - 1];
//     databaseFunction = iMap.isMap(databaseFunction) && databaseFunction.size > 0
//         ? extractDatabaseFunctions(databaseFunction, `${database}.${filter.get("column")}`)
//         : null;
//     //     databaseFunction = null
//     let columns = [{
//         column: col,
//         alias: modifiedAlias
//     }]
//     if (databaseFunction) {
//         columns[0].databaseFunction = databaseFunction
//     }

//     let formData = {
//         ...dataSource,
//         columns,
//         distinctResults: true,
//         functions: {
//             orderBy: [{
//                 alias: modifiedAlias,
//                 order: (typeof (orderBy) == 'undefined' || orderBy == "") ? "asc" : orderBy,
//                 custom: true
//             }]
//         },
//         refresh: true,
//         limitBy: limitBy,
//         offset: offset
//     }
//     if (searchText) {
//         formData.filters = [{
//             column: col,
//             condition: "CUSTOM",
//             customCondition: "LIKE",
//             values: ["%" + searchText + "%"]
//         }]
//         formData.customFilterExpression = "${0}";
//     }
//     if (Array.isArray(aggregate) && aggregate.length) {
//         formData.columns[0].aggregate = true;
//         formData.functions.aggregate = [{
//             column: col,
//             "function": aggregate.join("_"),
//             alias: modifiedAlias
//         }];
//     }
//     let formDatawithoutChange = Object.assign({}, formData)
//     let { valueDBFunction, DisplayDBFunction, valueColumn, displayColumn } = mapping | {}
//     if (mapping.isEnabled) {
//         try {
//             let originalColumns = [{
//                 column: `${database}.${displayColumn.fullyQualifiedColumn}`.replace(/^\./, ""),
//                 alias: displayColumn.alias
//             }]
//             if (displayColumn.defaultFunction && displayColumn.defaultFunction.split('.').indexOf('aggregate') != -1) {
//                 originalColumns[0].defaultFunction = displayColumn.defaultFunction
//                 originalColumns[0].aggregate = true;
//             }
//             if (displayColumn.databaseFunction) {
//                 originalColumns[0].databaseFunction = databaseFunction
//             }
//             formData.functions.aggregate = []

//             let originalOrderBy = formData.functions.orderBy
//             //modify columns
//             if (originalColumns.length == 1 && originalColumns.length != 0) {
//                 if (DisplayDBFunction) {
//                     originalColumns[0].databaseFunction = DisplayDBFunction
//                 }
//                 let newColumn = _.cloneDeep({
//                     ...originalColumns[0]
//                 })
//                 if (valueDBFunction) {
//                     newColumn.databaseFunction = valueDBFunction
//                 }
//                 if (valueColumn) {
//                     newColumn.column = `${database}.${valueColumn.fullyQualifiedColumn}`.replace(/^\./, "")
//                 }
//                 newColumn.alias = valAliasName
//                 let addAggregatePropToValue = false
//                 //for value
//                 if (valueColumn.defaultFunction && valueColumn.defaultFunction.split('.').indexOf('aggregate') != -1) {
//                     addAggregatePropToValue = true
//                     formData.functions.aggregate.push({
//                         column: `${database}.${valueColumn.fullyQualifiedColumn}`.replace(/^\./, ""),
//                         "function": valueColumn.defaultFunction,
//                         alias: valAliasName
//                     });
//                 }
//                 if (displayColumn.defaultFunction && displayColumn.defaultFunction.split('.').indexOf('aggregate') != -1) {
//                     formData.functions.aggregate.push({
//                         column: `${database}.${displayColumn.fullyQualifiedColumn}`.replace(/^\./, ""),
//                         "function": displayColumn.defaultFunction,
//                         alias: "display"
//                     });
//                 }
//                 if (addAggregatePropToValue) {
//                     newColumn.aggregate = true
//                 } else {
//                     delete newColumn.aggregate
//                 }
//                 originalColumns.push(newColumn)
//                 //check if databasefunction is an empty object and delete
//                 originalColumns = originalColumns.map(function (value, index) {
//                     if (value.databaseFunction && Object.keys(value.databaseFunction).length == 0) {
//                         delete value.databaseFunction
//                     }
//                     return value
//                 })
//                 formData.columns = originalColumns
//             }
//             // modify order by section
//             // if (originalOrderBy.length == 1 && originalOrderBy.length != 0) {
//             if (true) {
//                 let { display, value } = mapping.orderBy

//                 //create orderby for both display and value
//                 let result = []
//                 _.map(originalColumns, function (obj, index) {
//                     if (index == 1 && value != 'none') {
//                         result.push({
//                             alias: originalColumns[index].alias,
//                             custom: true,
//                             order: index == 0 ? display : value
//                         })
//                     } else if (index == 0 && display != 'none') {
//                         result.push({
//                             alias: originalColumns[index].alias,
//                             custom: true,
//                             order: index == 0 ? display : value
//                         })
//                     }
//                 })
//                 if (result.length) {
//                     formData.functions.orderBy = result
//                 } else {
//                     delete formData.functions.orderBy
//                 }
//                 if (Array.isArray(formData.functions.aggregate) && formData.functions.aggregate.length == 1) {
//                     //not required to add group by in formdata if there are no aggregates or all aggregates
//                     let changedArr = [valueColumn, displayColumn]
//                     changedArr.map(function (val, index) {
//                         if (val.defaultFunction && val.defaultFunction.split('.').indexOf('groupBy') != -1) {
//                             formData.functions.groupBy = [{
//                                 column: val.fullyQualifiedColumn,
//                                 custom: true
//                             }]
//                         }
//                     })
//                 }
//                 if (!formData.functions.aggregate.length) {
//                     delete formData.functions.aggregate
//                 }
//             }
//         } catch (e) {
//             formData = formDatawithoutChange
//         }
//     }
//     if (cascade.isEnabled) {
//         let cascadeFilters = cascade.filters
//         let filterIdsUsed = []
//             , filtersUsed = []
//             , updatedValues = {}
//         let fCount = 0
//         // using this because for every filter id starts with 0-- if filter ids not starting with 0- it is 
//         //causing error
//         //if this select is having prop _id = fromFilter, then latest values from cascading will be available in cascade.getFilterlatestValues()

//         // if (getFilterlatestValues && DashboardGlobals && DashboardGlobals.urlParameters && DashboardGlobals.urlParameters.mode) {
//         //     cascadeFilters = cascadeFilters.map(function (filter, index) {
//         //         let latestValue = getFilterlatestValues()
//         //         filter.values = latestValue[filter.id]
//         //         updatedValues[filter.id] = latestValue[filter.id]
//         //         return filter
//         //     })
//         // }

//         cascadeFilters.map(function (value, index) {
//             if (value.id != undefined) {
//                 filterIdsUsed.push(value.id)
//                 value.id = fCount
//                 fCount++
//                 filtersUsed.push(value)
//             }
//         })
//         let fluxFilters = flux.filters.store.state.filters.toJS()
//         let processedFilters = filterIdsUsed.map(function (id, index) {
//             return _.filter(fluxFilters, function (obj) {
//                 if (obj.id == id) {
//                     //check if IS_NULL or IS_NOT_NULL, or _all_ is selected - start
//                     if (['IS_NULL', 'IS_NOT_NULL'].indexOf(obj.condition) != -1 || obj.values.indexOf('_all_') != -1 || obj.values.length == 0) {
//                         return false
//                     }
//                     //check if IS_NULL or IS_NOT_NULL, or _all_ is selected - end

//                     obj.dataType = obj.backendDataType
//                     //replacing filter datatype with backenddatatype property
//                     if (!obj.column.startsWith(database)) {
//                         obj.column = `${database}.${obj.column}`.replace(/^\./, "")
//                     }
//                     if (obj.databaseFunction) {
//                         obj.databaseFunction = extractDatabaseFunctions(fromJS(obj.databaseFunction))
//                     }
//                     // obj.databaseFunction = createDbFuncObj()
//                     if (obj.mapping && obj.mapping.valueDBFunction) {
//                         obj.databaseFunction = obj.mapping.valueDBFunction
//                     }
//                     if (obj.databaseFunction && Object.keys(obj.databaseFunction).length == 0) {
//                         delete obj.databaseFunction
//                     }
//                     if (Object.keys(updatedValues).length && updatedValues[obj.id]) {
//                         obj.values = updatedValues[obj.id]
//                     }
//                     //deleting those two from fomData as they are not required to be sent
//                     delete obj.mapping
//                     //deleting those two from fomData as they are not required to be sent
//                     delete obj.cascade
//                     return true
//                 }
//                 return false
//             })
//         })
//         let customFilterExpression = ''
//         filtersUsed.map(function (obj, index) {
//             customFilterExpression = customFilterExpression + ' ${' + obj.id + '} '
//             if (index < filtersUsed.length - 1) {
//                 customFilterExpression = customFilterExpression + obj.condition
//             }
//         })
//         if (Array.isArray(formData.filters)) {
//             // lateron we should combine with existing filters 
//             formData.filters.push(_.flatten(processedFilters))
//         } else {
//             // lateron we should combine with existing filters 
//             formData.filters = _.flatten(processedFilters)
//         }
//         formData.customFilterExpression = customFilterExpression
//         if (formData.filters.length == 0) {
//             delete formData.filters
//             delete formData.customFilterExpression
//         }
//     }

//     if (!mapping.DisplayDBFunction && datePart && datePart !== "individual") {
//         let datePartList = selectedColumns.store.getState().dateFunctions["dateTime"]
//         let dateFunc = datePartList.find(func => func.part === datePart)
//         if (dateFunc && dateFunc.key) {
//             formData.columns[0].databaseFunction = {
//                 functionName: dateFunc.key,
//                 dataType: dateFunc.returns,
//                 parameters: {
//                     [dateFunc.parameters[0].name]: formData.columns[0].column
//                 }
//             }
//         }
//     }
//     if (!mapping.valueDBFunction && datePart && datePart !== "individual") {
//         let datePartList = selectedColumns.store.getState().dateFunctions["dateTime"]
//         let dateFunc = datePartList.find(func => func.part === datePart)
//         if (formData.columns[1] && dateFunc && dateFunc.key) {
//             formData.columns[1].databaseFunction = {
//                 functionName: dateFunc.key,
//                 dataType: dateFunc.returns,
//                 parameters: {
//                     [dateFunc.parameters[0].name]: formData.columns[1].column
//                 }
//             }
//         }
//     }

//     that.formData = formData
//     return that
// }
// export default prepareFilterFormData
// let window = window || {}

// window.prepareFilterFormData = prepareFilterFormData

// // prepareFilterFormData({
// //     column: 'Dynamic View.booking_platform',
// //     aggregate: undefined,
// //     orderBy: '',
// //     mapping: {},
// //     cascade: {},
// //     datePart: '',
// //     databaseFunction: {},
// //     database: "HIUSER",
// //     dataSource: {
// //         "location": "1463377807724/1463377836985/1591703058466",
// //         "metadataFileName": "aa3d9571-5568-444e-9446-79ec1f53c78f.metadata"
// //     },
// //     searchText: ''
// // }, null)
