import { produce } from "immer";
import { cloneDeep } from "lodash-es";
import { v4 as uuidv4 } from "uuid";
import types from "../../../constants/metadata";
import { setKeysPressed, setShotCutCurrentLocation } from "../../../redux/actions";
import { dateConditions } from "../../../redux/reducers/hreport.reducer";
import { MEASURE_VALUE, initialMeasureFields } from "../../../redux/reducers/initialStates";
import { addDateSpecific, addDateValues, getCanvasFieldDataType, getDatePart, getInitialMapping } from "../../../utils/filter-utils";
import {
  addDrillThroughField, addMarkField, addValueToLabel, applyRulesForOrderByColumn, checkIfAggregated,
  funcMap, getFieldDisplayName, getInitialCascade
} from "../../../utils/utilities";
import ShortCutText from "../../common/hi-shortcuts/hi-shortcuts";
import notify from "../../hi-notifications/notify";
import { handleChangePane } from "../hi-editing-area/hi-editing-area";
import { getDefaultAFDataTypeValues } from "../hi-editing-area/utils/property-utils";
import { measureDataUpdateFn } from "./base";
import { initialReferenceLineList, intialMarks, markTypes, postExecutionVars, postFetchVars, preExecutionVars, preFetchVars } from "./constants";

export const getTableTree = (metadata, metadataSearchText) => {
  return produce(metadata, (draft) => {
    let { tables } = draft;
    
    // Add null/undefined check for tables
    if (!tables || typeof tables !== 'object') {
      draft.tables = [];
      return;
    }
    
    let dotExist = metadataSearchText && metadataSearchText.split(".").length > 1 ? true : false;
    RegExp.quote = function (str) {
      let regxText = str.replace(/([.])/g, "\\$1");
      if (metadataSearchText && metadataSearchText.length) {
        regxText = regxText.replace(/[*]/g, ".*");
      }
      return regxText;
    };

    const searchRegex = metadataSearchText?.length
      ? new RegExp(RegExp.quote(metadataSearchText), "i")
      : "";
    let tablesList = [];
    Object.keys(tables).forEach((table) => {
      let tableObj = tables[table];
      tableObj.key = tables[table].key || uuidv4();
      let columns = tables[table].columns || {};
      let columnsList = [];
      Object.keys(columns).forEach((column) => {
        let { type } = columns[column];
        let columnObj = {
          columnName: column,
          column: { ...columns[column] },
          table: { ...tables[table] },
          type: { ...type },
          defaultFunction: columns[column].defaultFunction,
          key: columns[column].id,
          columnId: columns[column].id,
          alias: columns[column].alias,
          tableId: tables[table].id,
          dataType: Object.values(columns[column].type)[0],
          tableName: tables[table].name,
          tableAlias: tables[table].alias,
          dataSource: {
            // databaseName:  draft.name,
            // catalog: draft.dataSource.catalog,
            // schema: draft.dataSource.schema,
            // connectionDatabaseId: draft.dataSource.connectionDatabaseId,
            databaseName: tableObj?.isMultiConnection ? tableObj?.dataSourceName : draft.name,
            catalog: tableObj?.isMultiConnection ? tableObj?.dataSource?.catalog : draft.dataSource.catalog,
            schema: tableObj?.isMultiConnection ? tableObj?.dataSource?.schema : draft.dataSource.schema,
            connectionDatabaseId: tableObj?.isMultiConnection ? tableObj?.dataSource?.dbId : draft.dataSource.connectionDatabaseId
          }
        };
        delete columnObj.table.dataSource;
        delete columnObj.table.dataSourceName;
        delete columnObj.table.isMultiConnection;
        columnsList.push(columnObj);
      });

      delete tableObj.dataSource;
      delete tableObj.dataSourceName;
      delete tableObj.isMultiConnection;
      delete tableObj.columns;
      tableObj.children = columnsList.sort((a, b) => {
        if (a.alias > b.alias) return 1;
        if (a.alias < b.alias) return -1;
      });
      if (!metadataSearchText) {
        tablesList.push(tableObj);
      } else {
        if (searchRegex.test(tableObj.alias)) {
          tablesList.push(tableObj);
        }
        // if(tableObj.alias.toLowerCase().includes(metadataSearchText.toLowerCase())){
        // }
        else {
          tableObj.children = tableObj.children.filter((clmn) => {
            let alias = `${tableObj.alias}.${clmn.alias}`;
            if (!metadataSearchText) return true;
            return searchRegex.test(alias);
          });
          if (tableObj.children.length) {
            if (dotExist) {
              tableObj.expand = true;
            }
            tablesList.push(tableObj);
          }
        }
      }
    });
    tablesList = tablesList.sort((a, b) => {
      if (a.alias > b.alias) return 1;
      if (a.alias < b.alias) return -1;
    });
    draft.tables = tablesList;
  });
};


export const generateFieldAlias = ({
  fields, prependTableNameToAlias, table, column, defaultFunction, mark, addedAs
}) => {
  let columnName = `${table.name}.${column.name}`
  let label = prependTableNameToAlias ? `${table.alias}_${column.alias}` : column.alias
  let group = defaultFunction && defaultFunction.split(".")[2]
  fields = fields.filter(field => ["row", "column", "measure_field"].includes(field.addedAs))
  if (group === "aggregate") {
    let func = defaultFunction.split(".")[3]
    label = `${func}_${column.alias}`
    let isExist = fields.find((field) => getFieldDisplayName(field) === label), count = 0
    while (isExist) {
      if (isExist) {
        count = count + 1
      }
      isExist = fields.find((field) => getFieldDisplayName(field) === `${label}_${count}`)
    }
    label = count ? `${label}_${count}` : label
    if ([...markTypes, "drillthrough_field"].includes(addedAs)) {
      label = `${func}_${column.alias}`
    }
  } else {
    let isExist = fields.find((field) => getFieldDisplayName(field) === label), count = 0
    while (isExist) {
      if (isExist) {
        count = count + 1
      }
      isExist = fields.find((field) => getFieldDisplayName(field) === `${label}_${count}`)
    }
    label = count ? `${label}_${count}` : label
    if ([...markTypes, "drillthrough_field"].includes(addedAs)) {
      label = prependTableNameToAlias ? `${table.alias}_${column.alias}` : column.alias
    }
  }
  return { label, columnName }
}
export const executeScript = (report, script, dispatch) => {
  const Notify = notify(dispatch);
  try {
    let args = Object.keys(report) || []
    let data = new Function(...args, `
         ${script}
         return {${args.join(",")}}
      `)(...args.map(arg => report[arg]))

    return data
  } catch (e) {
    Notify.error({ type: "Frontend", message: e.message })
    return report

  }
}

const executePostExecutionScript = (report, script, dispatch) => {
  const Notify = notify(dispatch);
  try {
    let args = Object.keys(report) || []
    let data = new Function(...args, `
         ${script}
         return {${args.join(",")}}
      `)(...args.map(arg => report[arg]))

    return data
  } catch (e) {
    Notify.error({ type: "Frontend", message: e.message })
    return report
  }

  // const Notify = notify(dispatch);
  // const engine = new Liquid();
  //   let result;
  //   try {
  //     result = engine.parseAndRenderSync(script,{...report,dispatch: dispatch})
  //     return new Function(`${result}`)()
  //   } catch (e) {
  //     Notify.error({ message: e.message, type: "Frontend" });
  //   }
}

export const getMilliseconds = (timeInString = "00:00:00") => {
  return (Number(timeInString.split(":")[0]) * 3600000 +
    Number(timeInString.split(":")[1]) * 60000 +
    Number(timeInString.split(":")[2]) * 1000)
}

export const addFieldToReport = (payload, report, dispatch) => {
  const Notify = notify(dispatch);
  let { marksList, fields, options, metadata, referenceLineList = [] } = report
  let { column, table, type, defaultFunction, id, addedAs, genre, columnName, mark, drillThroughId, filterId, dataSource, hiddenIncludeInResultSet = false, hidden = false, custom_frontend_field = false } = payload;
  let columnID = column?.id || ""
  if (typeof column === 'string' && typeof table === 'string') {
    table = metadata.tables[table]
    column = table?.columns[column]
  }
  if ((!table || !column) && (genre !== "custom-formula")) {
    dispatch && Notify.error({ type: "Frontend", message: "Please provide valid table or column" })
    return report
  } else if (column && (typeof column === 'object')) {
    column.name = columnName || column?.alias
  }
  defaultFunction = defaultFunction || column?.defaultFunction
  id = id || uuidv4()
  genre = genre || types.COLUMN;
  addedAs = addedAs || "column"
  if (mark || markTypes.includes(addedAs)) {
    let { mark, markType } = payload;
    report = addMarkField({ mark, markType, fieldId: id }, report);
  }
  if (filterId && drillThroughId) {
    report = addDrillThroughField({ drillThroughId, filterId, fieldId: id }, report);
  }
  if (!metadata) return report;
  let { prependTableNameToAlias } = options;
  let { tables } = metadata;
  let { rules } = applyRulesForOrderByColumn({
    column,
    type: "add",
    genre,
    defaultFunction,
    table,
    tables,
  });
  // }
  let newCol;
  let floatingType = "discrete";
  if (genre === types.COLUMN) {
    let { label, columnName } = generateFieldAlias({
      fields,
      prependTableNameToAlias,
      table,
      column,
      defaultFunction,
      mark,
      addedAs,
    });
    type = type || column.type
    let backendDataType = Object.keys(type)[0]
    let dataType = type[backendDataType]
    type = { backendDataType, dataType }
    newCol = {
      column: columnName,
      columnID,
      label,
      id,
      type,
      autogen_alias: label,
      isNormalTable: rules[2],
      tableAlias: table.alias,
    };
    if (defaultFunction) {
      let [, , group] = defaultFunction.split(".");
      let showOrderByColumn = rules[0];
      let orderByColumn = rules[1];
      if (group) {
        newCol = {
          ...newCol,
          [group]: [defaultFunction],
          orderByColumn,
          showOrderByColumn,
        };
      }
      if (group === "aggregate") {
        floatingType = "";
      }
    }
    if (addedAs === "trend_field") { // for trend field
      floatingType = ""
    }
    newCol = {
      ...newCol,
      addedAs,
      floatingType,
      functionsDefinition: "",
      applyBeforeAggregate: false,
      // hiddenIncludeInResultSet: false,
      hiddenIncludeInResultSet: hiddenIncludeInResultSet,
      hidden: hidden,
      metaDataAlias: payload.column.alias,
      databaseName: dataSource?.databaseName,
      geographicType: "",
      isView: table?.type === "view" // added view check for bug id 6587
    };
    payload.table?.databaseName && (newCol.columnDatabase = payload.table.databaseName);  // added cubeDatabase
    if (!(floatingType === "discrete" && addedAs === "measure_field")) {
      report.fields.push(newCol);
    }
  } else if (genre === types.CUSTOM_FORMULA) {
    let { alias } = payload;
    // let id = uuidv4();
    let customFieldId = !custom_frontend_field ? uuidv4() : id
    // ----7216 ----
    let isMeasure = MEASURE_VALUE === column;
    addedAs = custom_frontend_field ? addedAs : report.customColumnData && report.customColumnData.fieldType;
    floatingType = isMeasure ? "" : floatingType;
    // --- 7216 ----
    newCol = {
      label: "Custom Column",
      custom: true,
      column,
      columnID,
      alias,
      id: customFieldId,
      addedAs,
      floatingType,
      functionsDefinition: "",
      orderByColumn: false,
      showOrderByColumn: false,
      databaseName: dataSource?.databaseName
    };
    if (custom_frontend_field) { // 7216
      newCol.custom_frontend_field = custom_frontend_field;
      newCol.type = type;
      // delete newCol.custom
    }
    report.fields.push(newCol);
    report.customColumnData = null;
  }
  let { aggregate } = newCol;
  let fieldTypes = ["color", "size", "shape", "tooltip", "label", "drillthrough_field", "measure_field"];
  if (aggregate && aggregate.length && fieldTypes.indexOf(addedAs) === -1) {
    let { id } = newCol;
    let allMark = { ...intialMarks };
    allMark.id = id;
    allMark.value = getFieldDisplayName(newCol);
    marksList.push(allMark);

    //adding new reference line item
    let newReferenceItem = { ...initialReferenceLineList }
    newReferenceItem.id = id;
    newReferenceItem.display = getFieldDisplayName(newCol);
    referenceLineList.push(newReferenceItem)
  }
  if (["row", "column", "color", "size", "shape", "tooltip", "label"].includes(addedAs) && (['create'].includes(report.mode) || report?.version)) { // auto formatting property for valid field
    let fieldType = getCanvasFieldDataType(newCol);
    if (!fieldType || fieldType === "text") return report;
    report.properties.format.formatFields.push({
      id: newCol.id,
      values: getDefaultAFDataTypeValues(fieldType, "create"),
    })
  }
  return report
}
export const addFilterToReport = (payload, report, dispatch) => {
  let {
    table, column, type, defaultFunction, columnName, from, aggregate, groupBy, databaseFunction, drillDownFilterValues,
    orderBy = [], values, condition, drillownFilter, drillDownId, columnDatabase, columnID, floatingType
  } = payload;
  let {
    metadata,
    filters,
    database = table?.databaseName || columnDatabase,
    defaultValueDisplayMap,
    databaseFunctions,
    dateFunctions,
    isCube
  } = report
  if (from === "metadata") {
    if (typeof column === 'string' && typeof table === 'string') {
      table = metadata.tables[table]
      column = table?.columns[column]
    }
    type = type || column.type
    defaultFunction = defaultFunction || column.defaultFunction
    let backendDataType = Object.keys(type)[0]
    let dataType = type[backendDataType]
    type = { backendDataType, dataType }
    column.name = columnName || column.alias
  }


  if (drillownFilter) {
    if (!report.drillDown) {
      return report;
    }
    let [tableName, columnName] = column.split(".") || [];
    if (!report?.metadata?.tables[tableName]?.columns[columnName]) {
      return report;
    }
  }
  let { prependTableNameToAlias } = report.options;
  report.activeTool = "2";
  let filterLabel = getFieldDisplayName(payload);
  let { dataType, backendDataType } = type;
  let databaseFunctionName;
  if (
    databaseFunction &&
    Object.keys(databaseFunction).length &&
    databaseFunction.returns
  ) {
    dataType = databaseFunction.returns;
    databaseFunctionName = getDatePart(databaseFunction.key);
  }
  if (from === "metadata") {
    filterLabel = prependTableNameToAlias
      ? `${table.alias}_${column.alias}`
      : column.alias;
    column = `${table.name}.${column.name}`;
  }
  if (floatingType === "continous") {
    dataType = "numeric" //added this for bug id 6531
  }
  filterLabel = addValueToLabel(filterLabel, filters);
  orderBy = orderBy && orderBy.length ? orderBy[0] : "";
  let filter = {
    column,
    label: filterLabel,
    databaseFunction,
    dataType,
    backendDataType,
    condition: "IS_ONE_OF",
    values: [],
    valuesMode: "custom",
    mode: "auto",
    groupBy,
    orderBy,
    valuesRange: {},
    rangeValuesType: "",
    dateTimeToggle: false,
    rangeSelectionToggole: true,
    maxInput: "",
    minInput: "",
    valuesList: [],
    drillDownId: drillDownId ? drillDownId : "",
    uid: uuidv4(),
    configId: uuidv4(),
    dataId: uuidv4(),
    columnID
  };
  if (["date", "dateTime"].includes(dataType)) { // 6686
    filter.customDisplayDateFormats = []
    switch (dataType) {
      case "date":
        filter.format = "YYYY-MM-DD"
        break;
      case "dateTime":
        filter.format = "YYYY-MM-DD HH:mm:ss"
        break;
      default:
        break;
    }
  }
  table?.databaseName && (filter.columnDatabase = table.databaseName);
  columnDatabase && (filter.columnDatabase = columnDatabase);
  let canvasFieldDataType = getCanvasFieldDataType(payload);
  if (canvasFieldDataType === "boolean") {
    filter = { ...filter, condition: "EQUALS" };
  }
  if (values && condition) {
    filter = {
      ...filter,
      condition,
      values,
      valuesMode: "auto",
      encloseInQuotes: false,
      interactiveMode: true,
    };
  }
  if (funcMap.indexOf(databaseFunctionName) > -1) {
    filter = addDateSpecific({
      filter,
      dataType,
      databaseFunctionName,
      databaseFunctions,
      dateFunctions,
      drillownFilter
    });
  } else if (dateConditions.indexOf(type.dataType) > -1) {
    filter = addDateSpecific({
      filter, dataType, databaseFunctions, databaseFunctionName, dateFunctions, drillownFilter
    });
  }
  // filter = { ...filter, aggregate };
  filter = { ...filter, aggregate, displayAggregateForAdvanceFilters: aggregate, valueAggregateForAdvanceFilters: aggregate }; // added aggregateForAdvanceFilters for bug  6556
  let mapping = getInitialMapping({
    filter,
    defaultValueDisplayMap,
    database,
    databaseFunctions,
    tables: metadata.tables,
    orderBy,
    isCube
  });
  let cascade = getInitialCascade(filter);
  filter = { ...filter, mapping, cascade };
  filter = addDateValues({ filter });
  filter.active = true;
  if (drillownFilter) {
    filter = { ...filter, drillownFilter, drillDownFilterValues, filterLabel };
    let { drillDownList } = report;
    let currentIndex = report.drillDownList.findIndex(
      (item) => item.drillDownId === drillDownId
    );
    if (drillDownList.length > currentIndex + 1) {
      drillDownList = drillDownList.slice(0, currentIndex + 1);
    }
    report.drillDownList.push(filter);
    report.currentDrillDown = drillDownId;
  }
  report.filters.push(filter);
  return report
}

export const add_row = (args, exposedVars, dispatch) => {
  args.addedAs = "row"
  addFieldToReport(args, exposedVars, dispatch)
  let rows = exposedVars.fields.filter(field => field.addedAs === "row").map(field => field.column)
  exposedVars.rows?.splice(0, exposedVars.rows.length)
  rows.map(row => exposedVars.rows?.push(row))
}
export const remove_row = (args, exposedVars, dispatch) => {
  let index = exposedVars.fields.findIndex(field => {
    return (field.addedAs === "row" && field.column === `${args.table}.${args.column}`)
  })
  exposedVars.fields.splice(index, 1)
  let rows = exposedVars.fields.filter(field => field.addedAs === "row").map(field => field.column)
  exposedVars.rows.splice(0, exposedVars.rows.length)
  rows.map(row => exposedVars.rows.push(row))
}
export const add_column = (args, reportState, dispatch) => {
  args.addedAs = "column"
  addFieldToReport(args, reportState, dispatch)
  let columns = reportState.fields.filter(field => field.addedAs === "column").map(field => field.column)
  reportState.columns?.splice(0, reportState.columns.length)
  columns.map(column => reportState.columns?.push(column))
}
export const remove_column = (args, exposedVars, dispatch) => {
  let index = exposedVars.fields.findIndex(field => {
    return (field.addedAs === "column" && field.column === `${args.table}.${args.column}`)
  })
  exposedVars.fields.splice(index, 1)
  let columns = exposedVars.fields.filter(field => field.addedAs === "column").map(field => field.column)
  exposedVars.columns.splice(index, exposedVars.columns.length)
  columns.map(column => exposedVars.columns.push(column))
}
export const add_mark = (args, exposedVars, dispatch) => {
  args.markType = args.markType || "color"
  args.addedAs = args.markType
  addFieldToReport(args, exposedVars, dispatch)
  let mark_fields = exposedVars.fields.filter(field => markTypes.includes(field.addedAs)).map(field => field.column)
  exposedVars.mark_fields?.splice(0, exposedVars.mark_fields.length)
  mark_fields.map(mark => exposedVars.mark_fields?.push(mark))
}
export const remove_mark = (args, exposedVars, dispatch) => {
  let index = exposedVars.fields.findIndex(field => {
    return (markTypes.includes(field.addedAs) && field.column === `${args.table}.${args.column}`)
  })
  exposedVars.fields.splice(index, 1)
  let mark_fields = exposedVars.fields.filter(field => markTypes.includes(field.addedAs)).map(field => field.column)
  exposedVars.mark_fields.splice(0, exposedVars.mark_fields.length)
  mark_fields.map(mark => exposedVars.mark_fields.push(mark))
}
export const add_filter = (args, exposedVars, dispatch) => {
  args.from = args.markType || "metadata"
  args.condition = args.condition || "IS_ONE_OF"
  if (args.values && !Array.isArray(args.values)) {
    args.values = [args.values]
  }
  addFilterToReport(args, exposedVars, dispatch)
}
export const remove_filter = (args, exposedVars, dispatch) => {
  let index = exposedVars.filters.findIndex(field => {
    return field.column === `${args.table}.${args.column}`
  })
  exposedVars.filters.splice(index, 1)
}
export const setFilterExpression = (report, ...args) => {
  let customFilterExpression = args[0] || ""
  let customHavingExpression = args[1] || ""
  report.filters.normalFilters.map((fltr, i) => {
    let filterName = getFieldDisplayName(fltr)
    customFilterExpression = customFilterExpression.replace(filterName, `\${${i}}`)
  })
  report.filters.havingFilters.map((fltr, i) => {
    let filterName = getFieldDisplayName(fltr)
    customHavingExpression = customHavingExpression.replace(filterName, `\${${i}}`)
  })
  report.query.setFilterExpression({ customFilterExpression, customHavingExpression })

  // console.log(customFilterExpression,customHavingExpression)
}

export const combine_measures_data = (args, exposedVars, reportMetadata) => {
  const { data: rData } = exposedVars;
  const { data,
    //  metadata 
  } = measureDataUpdateFn(rData, reportMetadata, args, initialMeasureFields)
  return data
}

export const preExecution = (report, dispatch) => {
  let { fields, scripts, options, marksList } = report
  fields = fields.filter((field) => !field?.custom_frontend_field); // 7216
  report.fields = fields; // 7216
  let rows = fields.filter(field => field.addedAs === "row").map(field => field.column)
  let columns = fields.filter(field => field.addedAs === "column").map(field => field.column)
  let mark_fields = fields.filter(field => markTypes.includes(field.addedAs)).map(field => field.column)
  let obj = {}
  preExecutionVars.map(group => {
    group.vars.map(item => {
      let { key } = item
      obj[key] = report[key]
      if (key === "visualisation") {
        obj[key] = report["selectedType"]
      }
      if (key === "metadata_file") {
        obj[key] = report["metadata"].formData
      }
      if (key === "rows") {
        obj[key] = rows
      }
      if (key === "columns") {
        obj[key] = columns
      }
      if (key === "mark_fields") {
        obj[key] = mark_fields
      }
      if (key === "settings") {
        obj[key] = options
        obj["options"] = options
      }
      if (key === "marks") {
        obj[key] = marksList
        obj["marksList"] = marksList
      }
    })
  })
  let exposedVars = cloneDeep(obj)
  let preExecutionScript = scripts.find(script => script.id === "pre-execution")?.value || ""
  exposedVars.add_row = args => add_row(args, exposedVars, dispatch)
  exposedVars.add_column = args => add_column(args, exposedVars, dispatch)
  exposedVars.add_mark = args => add_mark(args, exposedVars, dispatch)
  exposedVars.add_filter = args => add_filter(args, exposedVars, dispatch)
  exposedVars.remove_column = args => remove_column(args, exposedVars, dispatch)
  exposedVars.remove_row = args => remove_row(args, exposedVars, dispatch)
  exposedVars.remove_mark = args => remove_mark(args, exposedVars, dispatch)
  exposedVars.remove_filter = args => remove_filter(args, exposedVars, dispatch)
  let reportData = executeScript(exposedVars, preExecutionScript, dispatch)
  Object.keys(reportData).forEach(key => {
    if (typeof reportData[key] === "function") {
      delete reportData[key]
    }
  })
  reportData.selectedType = reportData.visualisation
  return reportData
}
export const preFetch = (report, dispatch) => {
  let { filters, fields, scripts, query, analytics, options, marksList } = report
  let rows = fields.filter(field => field.addedAs === "row").map(field => field.column)
  let columns = fields.filter(field => field.addedAs === "column").map(field => field.column)
  let mark_fields = fields.filter(field => markTypes.includes(field.addedAs)).map(field => field.column)
  let normalFilters = filters.filter(filter => !checkIfAggregated(filter))
  let havingFilters = filters.filter(filter => checkIfAggregated(filter))
  let obj = {}
  preFetchVars.map(groupItem => {
    groupItem.vars.map(item => {
      let { key } = item
      if (key === "query") return null
      obj[key] = report[key]
      if (key === "visualisation") {
        obj[key] = report["selectedType"]
      }
      if (key === "metadata_file") {
        obj[key] = report["metadata"].formData
      }
      if (key === "rows") {
        obj[key] = rows
      }
      if (key === "columns") {
        obj[key] = columns
      }
      if (key === "mark_fields") {
        obj[key] = mark_fields
      }
      if (key === "filterExpression") {
        obj[key] = [
          normalFilters.map(filter => getFieldDisplayName(filter)),
          havingFilters.map(filter => getFieldDisplayName(filter))
        ]
      }
      if (key === "settings") {
        obj[key] = options
      }
      if (key === "settings") {
        obj[key] = marksList
      }
    })
  })
  let exposedVars = cloneDeep(obj)
  exposedVars.query = query
  let preExecutionScript = scripts.find(script => script.id === "pre-fetch")?.value || ""
  let manipulateFormDataCallback = null
  exposedVars.manipulateFormData = args => {
    manipulateFormDataCallback = args
  }
  exposedVars.setFilterExpression = (arg1, arg2) => setFilterExpression({
    query, filters: { normalFilters, havingFilters }
  }, arg1, arg2)
  let reportData = executeScript(exposedVars, preExecutionScript, dispatch)
  query.reportFormData().manipulateFormData(formData => {
    if (analytics.some(item => item.value)) {
      formData.analytics = []
    }
    if (formData.analytics) {
      analytics.map(item => {
        formData.analytics.push({ [item.key]: item.value })
      })
    }
    if (typeof manipulateFormDataCallback === "function") {
      manipulateFormDataCallback(formData)
    }
    return formData
  })
  Object.keys(reportData).forEach(key => {
    if (typeof reportData[key] === "function") {
      delete reportData[key]
    }
  })
  reportData.selectedType = reportData.visualisation
  return reportData
}
export const postFetch = (report, dispatch) => {
  let { fields, scripts, options, marksList, reportMetadata = [] } = report
  let rows = fields.filter(field => field.addedAs === "row").map(field => field.column)
  let columns = fields.filter(field => field.addedAs === "column").map(field => field.column)
  let mark_fields = fields.filter(field => markTypes.includes(field.addedAs)).map(field => field.column)
  let obj = {}
  postFetchVars.map(groupItem => {
    groupItem.vars.map(item => {
      let { key } = item
      obj[key] = report[key]
      if (key === "visualisation") {
        obj[key] = report["selectedType"]
      }
      if (key === "metadata_file") {
        obj[key] = report["metadata"].formData
      }
      if (key === "rows") {
        obj[key] = rows
      }
      if (key === "columns") {
        obj[key] = columns
      }
      if (key === "mark_fields") {
        obj[key] = mark_fields
      }
      if (key === "settings") {
        obj[key] = options
      }
      if (key === "marks") {
        obj[key] = marksList
      }
    })
  })
  let exposedVars = cloneDeep(obj)
  exposedVars.combine_measures_data = args => combine_measures_data(args, exposedVars, reportMetadata)
  let postFetchScript = scripts.find(script => script.id === "post-fetch")?.value || ""
  let reportData = executeScript(exposedVars, postFetchScript, dispatch)
  Object.keys(reportData).forEach(key => {
    if (typeof reportData[key] === "function") {
      delete reportData[key]
    }
  })
  const data = reportData.data
  delete reportData.data
  return { data: data, reportData: reportData }
  // return {data:reportData.data}
}

export const postExecution = (report, dispatch) => {
  let { fields, scripts, options, marksList } = report
  let rows = fields.filter(field => field.addedAs === "row").map(field => field.column)
  let columns = fields.filter(field => field.addedAs === "column").map(field => field.column)
  let mark_fields = fields.filter(field => markTypes.includes(field.addedAs)).map(field => field.column)
  let obj = {}
  postExecutionVars.map(group => {
    group.vars.map(item => {
      let { key } = item
      obj[key] = report[key]
      if (key === "visualisation") {
        obj[key] = report["selectedType"]
      }
      if (key === "metadata_file") {
        obj[key] = report["metadata"].formData
      }
      if (key === "rows") {
        obj[key] = rows
      }
      if (key === "columns") {
        obj[key] = columns
      }
      if (key === "mark_fields") {
        obj[key] = mark_fields
      }
      if (key === "settings") {
        obj[key] = options
        obj["options"] = options
      }
      if (key === "marks") {
        obj[key] = marksList
        obj["marksList"] = marksList
      }
    })
  })
  let exposedVars = cloneDeep(obj)
  let postExecutionScript = scripts.find(script => script.id === "post-execution")?.value || ""
  let reportData = executePostExecutionScript(exposedVars, postExecutionScript, dispatch)
  Object.keys(reportData).forEach(key => {
    if (typeof reportData[key] === "function") {
      delete reportData[key]
    }
  })
  reportData.selectedType = reportData.visualisation
  return reportData
}

// add_row({table:"travel_details",column:"travel_medium"})

export const resetShortcuts = (dispatch) => {
  dispatch(setKeysPressed('reset'));
  dispatch(setShotCutCurrentLocation(""))
}

export const isMatchingShortcut = (keysPressed, shortcut) => {
  return keysPressed.length === 3
    && keysPressed[0] === 'Alt'
    && keysPressed[1].toLowerCase() === shortcut[0]
    && keysPressed[2].toLowerCase() === shortcut[1];
};

export const isMatchingShortcutWithShiftKey = (keysPressed, shortcut) => {
  return keysPressed.length === 4
    && keysPressed[0] === 'Alt'
    && keysPressed[2] === 'Shift'
    && keysPressed[1].toLowerCase() === shortcut[0]
    && keysPressed[3].toLowerCase() === shortcut[1];
};

export const getHrPropertyLabel = ({ text, label }) => {
  return <ShortCutText scLocation="HR PR" {...{ text, menuItem: true }}>{label}</ShortCutText>;
};

export const handleVizShortcuts = (key, vizRef, dispatch) => {
  switch (key) {
    case "t":
      vizRef.current("Table");
      break;
    case "r":
      vizRef.current("SyncChart");
      break;
    case "g":
      vizRef.current("GridChart");
      break;
    case "m":
      vizRef.current("Antcharts");
      break;
    case "x":
      vizRef.current("MapChart")
      break;
    case "v":
      vizRef.current("VF")
      break;
    case "o":
      vizRef.current("S2Chart")
      break;
    case "k":
      vizRef.current("Card")
      break;
    default:
      return;
  }
  resetShortcuts(dispatch)
}

export const handleHrShortcuts = (dispatch, keysPressed) => {
  const subModuleNavigation = {
    "v": { paneId: "1", location: "HR VZ" },
    "f": { paneId: "2", location: "HR FIL" },
    "o": { paneId: "3", location: "HR OP" },
    "q": { paneId: "4", location: "HR SQ" },
    "t": { paneId: "5", location: "HR ST" },
    "m": { paneId: "6", location: "HR MR" },
    "p": { paneId: "7", location: "HR PR" },
  };
  const shortcut = subModuleNavigation[keysPressed[1].toLowerCase()];
  if (shortcut) {
    handleChangePane(dispatch, shortcut.paneId);
    dispatch(setShotCutCurrentLocation(shortcut.location));
  }
}

export const getCanvasStyles = ({ canvasProperties = {}, chartAreaHeight, chartAreaWidth, width = true }) => {
  let hreportCustomStyles = {}
  if (canvasProperties.view === 'fitWidth') {
    hreportCustomStyles = {
      height: canvasProperties?.height ?? chartAreaHeight,
      overflowY: 'auto',
    }
  }
  if (canvasProperties.view === 'fitHeight' && width) {
    hreportCustomStyles = {
      width: canvasProperties?.width ?? chartAreaWidth,
      overflowX: 'auto',
    }
  }
  if (canvasProperties.view === 'entireView') {
    hreportCustomStyles = {
      height: canvasProperties?.height ?? chartAreaHeight,
      overflow: 'auto',
    }
    if (width) {
      hreportCustomStyles.width = canvasProperties?.width ?? chartAreaWidth
    }
  }
  return hreportCustomStyles;
}