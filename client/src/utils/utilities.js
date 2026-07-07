import moment from "moment";
import { fromJS } from "immutable";
import _ from "lodash";
import types from "../constants/metadata";
import produce from "immer";
import { generateReport } from "../components/hi-reports/utils/base";
import { postDownloadRequest, postPrintDownloadRequest } from "../base/service";
import { anchorConstant, checkIsValidDateFilter, checkRelativeDateFilter, getFilterDataType, getFloatingType } from "./filter-utils";
import { updateDashboardVariables } from "../redux/actions";
import { getActualFieldDataType } from "../components/hi-reports/hi-viz-area/utils/grid-chart-utils";
import { getRelativeDateValues } from "../components/hi-reports/hi-editing-area/utils/filter-utils";

export const dataTypes = {
  NUMERIC: "numeric",
  TEXT: "text",
  DATE: "date",
  TIME: "time",
  DATETIME: "dateTime",
  BOOLEAN: "boolean",
  OTHER: "other",
};

export const filterPluginsData = (pluginsDataSource, value) => {
  return pluginsDataSource.filter((eachData) =>
    eachData.temporaryName.includes(value)
  );
};

export const filterDeletePlugins = (pluginsDataSource, driverPath) => {
  return pluginsDataSource.filter(
    (eachData) => eachData.details.jarName !== driverPath
  );
};

export const convertToBase64 = (str) => {
  if (typeof str === "string") return window.btoa(str);
  return;
};

export const getPermissionLevelsText = () => {
  return [
    { level: 0, permission: "No Access" },
    { level: 1, permission: "Execute Only" },
    { level: 2, permission: "Read Only" },
    { level: 3, permission: "Read + Write" },
    { level: 4, permission: "Read + Write + Delete" },
    { level: 5, permission: "Read + Write + Delete + Share" },
  ];
};
export const getSpecificTable = ({ tables, tableName, aliasName }) => {
  let result = {};
  Object.keys(tables).forEach((key) => {
    if (tables[key].name === tableName && tables[key].alias === aliasName) {
      result[key] = tables[key];
    }
  });
  return result;
};
export const applyRulesForOrderByColumn = ({
  column,
  table,
  tables,
  defaultFunction,
  type,
  genre,
}) => {
  // let [, , group, func] = defaultFunction.split(".");
  let isCustomColumn = genre === types.CUSTOM_FORMULA;
  let group =
    defaultFunction && defaultFunction.split(".").length
      ? defaultFunction.split(".")[2]
      : "";
  let tableName = table ? table.name : "";
  table = table
    ? getSpecificTable({ tables, tableName, aliasName: table.alias })
    : "";
  let colAggregation =
    defaultFunction && defaultFunction.split(".").length
      ? defaultFunction.split(".")[3]
      : "";
  let rule = rules({
    column,
    table,
    tableName,
    group,
    type,
    isCustomColumn,
    colAggregation,
  });
  if (isCustomColumn) {
    return { rules: rule };
  }
  let isNormalTable = rule[2];
  column.showOrderByColumn = rule[0];
  column.orderByColumn = rule[1];
  column.isNormalTable = isNormalTable;
  return { rules: rule, columnData: column };
};

/**
 * check if the column dragged is from a normal table
 * column - column info from props
 * table - table info from props
 * metadataInfo - metadata table info from store
 */
const checkIfNormalTable = ({ table, tableName }) => {
  if (table[tableName]?.type === undefined) {
    return true;
  }
  return false;
};

const rules = ({
  column,
  table,
  tableName,
  group,
  type,
  isCustomColumn,
  colAggregation,
}) => {
  //rule 4
  if (isCustomColumn) {
    return [false, false, false];
  }
  const isDbFunctionApplied = column.databaseFunction ? true : false;
  if (type === "add") {
    // return column after updating data in column
    let isNormalTable = checkIfNormalTable({ table, tableName });
    //rule 6
    if (!isNormalTable) {
      return [false, false, isNormalTable];
    }
    if (group === "aggregate") {
      if (colAggregation === "distinct" && isDbFunctionApplied) {
        // rule 7
        return [true, false, isNormalTable];
      }
      //rule 5
      return [false, false, isNormalTable];
    }
    if (isDbFunctionApplied) {
      // rule 3
      if (["date", "dateTime"].indexOf(column.type.dataType) !== -1) {
        return [true, true, isNormalTable];
      }
      // rule 2
      return [true, false, isNormalTable];
    }
    return [false, false, isNormalTable];
  } else if (type === "update") {
    let isAggregatePresent = column.aggregate ? true : false;
    let isDistinct = false;
    try {
      isDistinct =
        column.aggregate.size === 1 &&
        column.aggregate[0].split(".")[3] === "distinct";
    } catch (err) {
      isDistinct = false;
    }
    let isNormalTable = column.isNormalTable;
    //rule 6
    if (!isNormalTable) {
      return [false, false, isNormalTable];
    }
    if (isAggregatePresent) {
      if (isDistinct && isDbFunctionApplied) {
        // rule 7
        return [
          true,
          column.orderByColumn === true ? true : false,
          isNormalTable,
        ];
      }
      //rule 5
      return [
        false,
        column.orderByColumn === true ? true : false,
        isNormalTable,
      ];
    }
    if (isDbFunctionApplied) {
      // rule 3
      if (["date", "dateTime"].indexOf(column.type.dataType) !== -1) {
        return [
          true,
          column.orderByColumn === false ? false : true,
          isNormalTable,
        ];
      }
      // rule 2
      return [
        true,
        column.orderByColumn === true ? true : false,
        isNormalTable,
      ];
    }
    return [false, column.orderByColumn === true ? true : false, isNormalTable];
  }
};

export const getFieldDisplayName = (column) => {
  return column?.alias
    ? column.alias
    : column?.autogen_alias
      ? column.autogen_alias
      : column?.label;
};
export const checkWhetherStateIsLoaded = (file, reports) => {
  let report = reports.find((report) => {
    let { location, uuid } = report.reportInfo;
    return `${location}/${uuid}` === file?.path;
  });
  if (report) {
    report = JSON.parse(JSON.stringify(report));
  }
  return report;
};
export const getFieldAliasName = (column) => {
  return column?.columnAlias
    ? column?.columnAlias
    : getFieldDisplayName(column);
};

export const checkOrderIsApplied = (key, field) => {
  return Array.isArray(field.orderBy) && field.orderBy.includes(key);
};

export const modifyFilters = (state, parameters) => {
  if (parameters && Object.keys(parameters).length) {
    return produce(state, (draft) => {
      draft.filters = draft.filters.map((filter) => {
        let value = parameters[getFieldDisplayName(filter)];
        if (!value) return filter;
        let dataType = getFilterDataType(filter);
        if (dataType === "numeric") {
          if (Array.isArray(value)) {
            filter.values = value.map((item) => {
              if (item === "_all_") {
                return item;
              }

              // return parseInt(item, 10); // bug - 6218 - fix
              return item;

            });
          } else if (value) {
            // filter.values = value === "_all_" ? value : [parseInt(value, 10)]; // bug - 6218 - fix
            filter.values = value === "_all_" ? value : [value];

          }
        } else {
          if (Array.isArray(value)) {
            filter.values = value;
          } else if (value) {
            filter.values = [value];
          }
        }
        return filter;
      });
      draft.filters = checkRelativeDateFilter(draft.filters, parameters)  // added for 6336 , was not working in open mode 
    });
  } else {
    return { ...state, filters: checkRelativeDateFilter(state.filters, parameters) } // added for 6336 , was not working in open mode 
  }
};

export const modifyTableProperties = (state, pageSize) => {
  const { properties, reportData } = state || {}
  let newReportData = { ...reportData }
  let changedProperties = { ...properties }
  let newTableProperties = {
    ...properties.table,
    recordsPerPage: pageSize
  }
  changedProperties = { ...changedProperties, table: newTableProperties };
  newReportData = {
    ...newReportData, properties: {
      ...newReportData.properties, table: newTableProperties
    }
  }
  state = { ...state, reportData: newReportData, properties: changedProperties };
  return state;
}

export const addMarkField = (data, report) => {
  let { markType, fieldId } = data;
  let value = data.mark ? data.mark.value : "_all_";
  report.marksList = report.marksList.map((mark) => {
    if (mark.value === value) {
      mark[markType]["fields"].map((markField) => {
        report.fields = report.fields.filter(field => {
          return field.id !== markField.id
        })
      })
      mark[markType]["fields"] = [{ id: fieldId }];
    }
    return mark;
  });
  return report;
};
export const addDrillThroughField = (data, report) => {
  let { drillThroughId, filterId, fieldId } = data;
  report.drillThroughList = report.drillThroughList.map((childReport) => {
    if (childReport.drillThroughId === drillThroughId) {
      childReport.parameters = childReport.parameters.map((fltr) => {
        if (fltr.uid === filterId) {
          fltr.mappedColumnId = fieldId;
        }
        return fltr;
      });
    }
    return childReport;
  });
  return report;
};

export const checkIfCardViz = (fields, selectedType) => {
  let isCardViz = false;
  let canvasFields = fields.filter((field) =>
    ["row", "column"].includes(field.addedAs)
  );
  if (
    selectedType === "Antcharts" &&
    canvasFields.length === 1 &&
    getFloatingType(canvasFields[0]).floatingType === "continous"
  ) {
    isCardViz = true;
  }
  return isCardViz;
};

export const checkIfCalendarViz = (fields, selectedType) => {
  let isCalendarViz = false;
  let canvasFields = fields.filter((field) =>
    ["row", "column"].includes(field.addedAs)
  );
  let dataType = getActualFieldDataType(canvasFields[0])
  if (
    selectedType === "Antcharts" &&
    canvasFields.length === 1 &&
    ['date', 'dateTime'].includes(dataType)
  ) {
    isCalendarViz = true;
  }
  return isCalendarViz;
};

const checkIfAliasExist = (fields, label) => {
  let isExist = fields.find((field) => getFieldDisplayName(field) === label), count = 0
  while (isExist) {
    if (isExist) {
      count = count + 1
    }
    isExist = fields.find((field) => getFieldDisplayName(field) === `${label}_${count}`)
  }
  return count
}

export const generateAlias = (
  funcsList,
  metadataAlias,
  functions,
  tableAlias,
  isTableName,
  fields
) => {
  let tempFunctions = functions;
  if (!metadataAlias) return "";
  let col = metadataAlias.split(".").pop();
  if (!funcsList.length) {
    let label = isTableName ? `${tableAlias}_${col}` : col;
    let count = checkIfAliasExist(fields, label)
    label = count ? `${label}_${count}` : label
    return label;
  }
  let existingAlias = `${funcsList
    .map((e) => tempFunctions[e])
    .join("_")}_${col}`;
  let count = checkIfAliasExist(fields, existingAlias)
  existingAlias = count ? `${existingAlias}_${count}` : existingAlias
  return existingAlias;
};

export const addValueToLabel = (label, filters) => {
  let count = 0;
  filters.map((filter) => {
    if (filter.label.match(new RegExp([label].join("|"), "i"))) {
      count++;
    }
    return null;
  });
  if (count <= 0) {
    return label;
  } else {
    return label + "_" + count;
  }
};

export const funcMap = [
  "individual",
  "datetime",
  "date",
  "time",
  "year",
  "quarter",
  "month",
  "monthname",
  "day",
  "hour",
  "minute",
  "second",
  "maketime",
  "makedatetime",
  "today",
  "now",
];

export const extractDatabaseFunctions = (funcs) => {
  let reducer = function (reduction, value, key) {
    if (key === "key") {
      reduction.functionName = value;
    } else if (key === "parameters") {
    } else if (key === "returns") {
      reduction.dataType = value;
    }
    return reduction;
  };
  return funcs.reduce(reducer, {});
};

export const extractDatabaseFunctions1 = (item, column) => {
  item.parameters = item.parameters.map((param) => {
    if (param.column) {
      param = { ...param, value: column };
    }
    return param;
  });
  return item;
};

export const getDBFunctionObject = ({
  value,
  defaultReturn,
  databaseFunctions,
}) => {
  let dbObj;
  databaseFunctions = _.flatten(Object.values(databaseFunctions));
  try {
    databaseFunctions.map((obj) => {
      if (obj.key === value) {
        dbObj = obj;
      }
      return null;
    });
    return dbObj;
  } catch (err) {
    return defaultReturn;
  }
};

export const getColumnbject = ({
  tables,
  fullyQualifiedColumn,
  columnFunction,
  type,
  isAggregated,
  defaultAggregate,
  isMappingCreate,
}) => {
  let segregatedColumns = {};
  let onlyColumns = [];
  Object.keys(tables).map((tableName) => {
    segregatedColumns[tableName] = { columns: tables[tableName].columns };
    onlyColumns.push(tables[tableName].columns);
    return null;
  });
  onlyColumns = _.flatten(onlyColumns);
  if (type === "getAllColumns") {
    return onlyColumns;
  }
  if (type === "getColumnObj") {
    let result = _.filter(onlyColumns, function (obj) {
      return obj.fullyQualifiedColumn === fullyQualifiedColumn;
    })[0];
    if (isMappingCreate) {
      if (defaultAggregate && result) {
        result.defaultFunction = defaultAggregate;
      }
      return result;
    }
    if (
      !isAggregated &&
      result &&
      result.defaultFunction &&
      result.defaultFunction.split(".").includes("aggregate")
    ) {
      delete result.defaultFunction;
    }
    if (columnFunction && columnFunction !== "None" && columnFunction.length) {
      result.defaultFunction = columnFunction;
    } else {
      if (result && !isAggregated) {
        delete result.defaultFunction;
      }
    }
    return result;
  }
};

export const getInitialMapping = ({
  filter,
  defaultValueDisplayMap,
  database,
  databaseFunctions,
  tables,
}) => {
  let DBkey = (filter.databaseFunction && filter.databaseFunction.value) || "";
  let valueDBFunction = defaultValueDisplayMap[DBkey];
  valueDBFunction = getDBFunctionObject({
    value: valueDBFunction,
    defaultReturn: false,
    databaseFunctions,
  });
  let DisplayDBFunction = DBkey;
  DisplayDBFunction = getDBFunctionObject({
    value: DisplayDBFunction,
    defaultReturn: false,
    databaseFunctions,
  });
  return {
    isEnabled: false,
    valueDBFunction: valueDBFunction
      ? extractDatabaseFunctions(
        fromJS(valueDBFunction),
        `${database}.${filter.column}`
      )
      : filter.databaseFunction, //-- commented for replacing function nem with function object
    DisplayDBFunction: DisplayDBFunction
      ? extractDatabaseFunctions(
        fromJS(DisplayDBFunction),
        `${database}.${filter.column}`
      )
      : filter.databaseFunction, //-- commented for replacing function nem with function object
    isDefaultFunction: true,
    valueDisplayMap: [],
    valueAliasName: "random",
    orderBy: {
      display: "none",
      value: "asc",
    },
    valueDBFuntionInfo: DisplayDBFunction || {},
    valueColumn: getColumnbject({
      tables,
      type: "getColumnObj",
      fullyQualifiedColumn: filter.column,
      isAggregated: filter.aggregate ? true : true,
      isMappingCreate: true,
      defaultAggregate:
        filter.groupBy && filter.groupBy.length
          ? filter.groupBy[0]
          : filter.aggregate && filter.aggregate.length
            ? filter.aggregate[0]
            : "none",
    }),
    displayColumn: getColumnbject({
      tables,
      type: "getColumnObj",
      fullyQualifiedColumn: filter.column,
      isAggregated: filter.aggregate ? true : true,
      isMappingCreate: true,
      defaultAggregate:
        filter.groupBy && filter.groupBy.length
          ? filter.groupBy[0]
          : filter.aggregate && filter.aggregate.length
            ? filter.aggregate[0]
            : "none",
    }),
  };
};

export const getInitialCascade = () => {
  return {
    isEnabled: false,
    filters: [], //all the filter related information {filterId : {logical : 'and/or'}}
    filtersCount: 0, //default count is the count same as filtersIds length but can vary as user has its control
  };
};
export const exportReport = ({ id, format, callback }, dispatch) => {
  dispatch((dispatch, getState) => {
    let activeReport = getState().hreport.present.reports.find(item => item.id === id);
    if (activeReport?.activeDrillthroughId) {
      activeReport = getState().hreport.present.reports.find(item => item.id === activeReport.activeDrillthroughId);
    }
    if (!activeReport) return null
    let { reportInfo, options } = activeReport;
    const { limitBy, sample } = options
    let { reportName } = reportInfo;
    generateReport(
      { ...activeReport, getFormData: true, printFormat: format },
      dispatch
    ).then((formData) => {
      formData = {
        ...formData,
        limitBy: sample === "sample" ? limitBy : "full",
        isAdhoc: true,
        type: format,
        requestType: "adhoc",
        serviceType: "report",
        service: "fetchData",
      };
      postDownloadRequest({ dispatch, formData, reportName, callback });
    });
  });
};

export const getRelativeCacheTime = (lastModified, serverTime, clientTime) => {
  clientTime = clientTime || new Date().getTime();
  lastModified = lastModified || new Date().getTime();
  serverTime = serverTime || new Date().getTime();
  let loginTime = new Date().getTime() - clientTime;
  serverTime = serverTime + loginTime;
  lastModified = moment(parseInt(lastModified, 10));
  let copyServerTime = serverTime + 1000;
  let lastModifiedDiff = copyServerTime - lastModified;
  let deltaTime = lastModified + lastModifiedDiff;
  let parseLMDiff = moment(parseInt(deltaTime, 10));
  let timeFrom = lastModified.from(parseLMDiff);
  return timeFrom;
};
export const getFullCacheTime = (lastModified) => {
  lastModified = lastModified || new Date().getTime();
  lastModified = moment(parseInt(lastModified, 10));
  let readableDate = lastModified.format("[Last cached on:] LLLL");
  return readableDate;
};

export const exportPrintedReport = (
  { file, format, parameters, callback },
  dispatch
) => {
  let { path, name, title } = file || {};
  if (!path || !name) {
    console.log("location or file is not valid");
    return null;
  }
  let rest = _.omit(parameters, "print");
  let dir = path.replace(name, "").replace(/[\\|\/]+$/, "");
  let formData = {
    format: format,
    xml: "",
    resultDirectory: "",
    dir: dir,
    filename: name,
    reportType: "hr",
    reportNameParam: title,
    reportParameters: {
      ...rest,
      navigatorUserAgent: "print",
      mode: "dashboard", // 6061 - fix 
    },
    reportFile: name,
    reportName: title,
  };
  return postPrintDownloadRequest({ dispatch, formData, callback });
};

export const exportPrintedReportEfwdd = (
  { file, format, callback, urlParameters },
  dispatch
) => {
  let { path, name, title } = file || {};
  if (!path || !name) {
    console.log("location or file is not valid");
    return null;
  }
  let dashboardVariables;
  dispatch((dispatch, getState) => {
    dashboardVariables = getState().dashboard.present.dashboardVariables;
  });
  let rest = _.omit(urlParameters, "print");
  let dir = path.replace(name, "").replace(/[\\|\/]+$/, "");
  let formData = {
    format: format,
    xml: "",
    resultDirectory: "",
    dir: dir,
    filename: name,
    reportType: "efwdd",
    reportNameParam: title,
    reportParameters: {
      ...dashboardVariables,
      ...rest,
      mode: "dashboard",
      navigatorUserAgent: "print",
    },
    reportFile: name,
    reportName: title,
  };
  return postPrintDownloadRequest({ dispatch, formData, callback });
};

export const createTabTitle = (tabTitles) => {
  let title = `Untitled ${tabTitles.length + 1}`;
  let i = tabTitles.length + 1;
  while (tabTitles.includes(title)) {
    i++;
    title = `Untitled ${i}`;
  }
  return title;
};

export const getDashboardVariableConfig = (variables = {}, dispatch) => {
  let dashboardVariables = { ...variables };

  dispatch((_, getState) => {
    const state = getState();
    const dashboardGridItems = state.designer?.present?.gridItemsData || [];
    const hReports = state.hreport?.present?.reports || [];

    if (!dashboardGridItems.length || !hReports.length) return;

    const filterComponents = dashboardGridItems.filter(item => item.compType === "filter");
    if (!filterComponents.length) return;

    const filterIDs = [...new Set(filterComponents.map(item => item?.parameter?.dashboardFilter?.uid).filter(Boolean))];
    if (!filterIDs.length) return;

    // const filterReports = hReports.filter(report => report.mode === "filter");
    // if (!filterReports.length) return;

    const filterMap = new Map();
    hReports.forEach(report => {
      (report.filters || []).forEach(filter => {
        if (filter?.uid) filterMap.set(filter.uid, filter);
      });
    });

    filterIDs.forEach(filterID => {
      const filter = filterMap.get(filterID);
      if (filter && checkIsValidDateFilter(filter) && filter?.anchor?.relativePart) {
        const filterName = getFieldDisplayName(filter);
        if (dashboardVariables[filterName]) {
          const { anchor = {}, values } = filter || {}
          dashboardVariables[filterName] = getRelativeDateValues({ anchorDateData: anchor }, values, filter)
          // dashboardVariables[filterName] = filter.values;
        }
      }
    });
  });

  return dashboardVariables;
};

export const getGlobalVariables = ({ dispatch, level, id }) => {
  let user;
  let report;
  let dashboardVariables;
  const findGridItemById = (items, targetId) => {
    for (const item of items) {
      if (item.id === targetId) {
        return item;
      }
      if (item.children && Array.isArray(item.children)) {
        const found = findGridItemById(item.children, targetId);
        if (found) {
          return found;
        }
      }
    }
    return null;
  };

  dispatch((dispatch, getState) => {
    user = getState().app.applicationSettingsData.userData.user;
    if (level === "reportLevel") {
      const gridItemsData = getState().designer.present.gridItemsData || [];
      const gridItem = findGridItemById(gridItemsData, id) || {};
      report = { id: gridItem.id, name: gridItem.reportInfo?.file?.title };
    }
    dashboardVariables = getState().dashboard.present.dashboardVariables;
  });
  return { user, report, ...dashboardVariables };
};

export const checkIfAggregated = (field) => {
  return field.aggregate && field.aggregate.length;
};

export const parseExpression = (value) => {
  if (typeof value !== 'string') return value;
  const regex = /^(__a)?_(year|month|quarter|today|tomorrow|yesterday|hour|minute|second)([-+]\d+)?_?(((\d{4}-\d{2}-\d{2}(?: \d{2}:\d{2}:\d{2})?)?)|(\d{4}-\d{2}-\d{2})|(\d{4})|(\d{2})|(\d{1}))?$/;
  const processedValue = value?.replace(/(?<=_(year|month|quarter|today|tomorrow|yesterday|hour|minute|second))\s/g, '+');
  const match = processedValue?.match(regex);
  if (!match) return value;

  const [, anchor, base, modifier, anchorDate] = match;
  return { anchor, base, modifier, anchorDate };
};

const isObject = (value) => !Array.isArray(value) && value !== null && typeof value === 'object';
export const isArray = (value) => Array.isArray(value);
const isString = (value) => typeof value === 'string'

const ensureArray = (value) => {
  if (isArray(value)) return value;
  return [value];
};

const parseValue = (value) => {
  const parsedResult = parseExpression(isArray(value) ? value[0] : value);
  if (isObject(parsedResult)) {
    const { anchor, base, modifier, anchorDate } = parsedResult;
    if (anchor) {
      const newDate = `anchor_${base || 'month'}${modifier || ''}`;
      return { parsedValue: [newDate], anchorValue: anchorDate };
    }
  }
  return { parsedValue: value };
};


export const checkForAnchorRelativeParameters = (parameters) => {
  if (!isObject(parameters)) return parameters;
  const result = Object.entries(parameters).reduce((acc, [key, value]) => {
    if (isObject(value)) {
      const anchorFunction = value['anchor_function'] || '';
      let tempValue;
      if (isArray(anchorFunction)) {
        tempValue = ensureArray(anchorFunction.map((v) => v.includes('anchor_') ? v : `anchor_${v}`));
      } else {
        tempValue = anchorFunction.includes('anchor_') ? anchorFunction : `anchor_${anchorFunction}`;
      }
      acc[key] = ensureArray(tempValue);
      if ('anchor_value' in value) {
        acc[`${anchorConstant}${key}`] = ensureArray(value['anchor_value']);
      }
    } else {
      if (!Array.isArray(value) || value.length === 1 || !value.some((v) => isString(v) && v.includes(anchorConstant))) {
        const { parsedValue, anchorValue } = parseValue(value);
        acc[key] = parsedValue;
        if (anchorValue) {
          acc[`${anchorConstant}${key}`] = [anchorValue];
        }
        return acc;
      }
      const anchorIndex = value?.findIndex((v) => v.includes(anchorConstant));
      const anchorValue = value[anchorIndex].split(anchorConstant);
      const filteredValues = value?.filter((_v, i) => !_v.includes(anchorConstant));
      acc[key] = filteredValues;
      acc[`${anchorConstant}${key}`] = [anchorValue[1]];
    }
    return acc;
  }, {});

  return result;
};