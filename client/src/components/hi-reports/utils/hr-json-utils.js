import { cloneDeep, omit } from "lodash-es";
import { unwrapStatePayload } from "../../common/json-editor";

// Runtime / session key that must not be overwritten from raw JSON
export const REPORT_JSON_PROTECTED_KEYS = [
  "id",
  "active",
  "mode",
  "metadata",
  "metadataLoading",
  "hreportLoading",
  "functions",
  "databaseFunctions",
  "dateFunctions",
  "reportData",
  "defaultValueDisplayMap",
  "editingField",
  "sqlString",
  "cellMenuData",
  "reportInfo",
  "saveAs",
  "cube",
  "isDrillthroughActive",
  "tableFilters",
  "activeTool",
  "isCube",
  "message",
  "user",
  "requestId",
  "isAborted",
  "generateQuery",
  "getFormData",
  "refresh",
  "printFormat",
  "isPrintMode",
  "loadState",
  "undoRedoAction",
];

export const METADATA_MISMATCH_MESSAGE =
  "Columns/rows do not match the connected metadata (column IDs or database).";

const getEditableReportState = (reportState = {}) =>
  cloneDeep(omit(reportState, REPORT_JSON_PROTECTED_KEYS));

export const buildReportJsonDocument = getEditableReportState;

export const serializeReportJson = (activeReport) =>
  JSON.stringify(getEditableReportState(activeReport), null, 2);

export const parseReportJsonText = (rawJsonText) => {
  if (!rawJsonText?.trim()) {
    return {};
  }
  const reportState = unwrapStatePayload(JSON.parse(rawJsonText));
  if (
    !reportState ||
    typeof reportState !== "object" ||
    Array.isArray(reportState)
  ) {
    throw new Error("JSON must be an object");
  }
  return reportState;
};

export const mergeReportJsonIntoReport = (
  activeReport,
  parsedReportState = {},
) => ({
  ...activeReport,
  ...getEditableReportState(parsedReportState),
  reportData: {},
  cellMenuData: null,
});

const DEFAULT_REPORT_OPTIONS = {
  sample: "sample",
  limitBy: 50,
  prependTableNameToAlias: false,
};

export const normalizeReportForFetch = (report = {}, user = {}) => ({
  ...report,
  user,
  fields: Array.isArray(report.fields) ? report.fields : [],
  filters: Array.isArray(report.filters) ? report.filters : [],
  marksList: Array.isArray(report.marksList) ? report.marksList : [],
  scripts: Array.isArray(report.scripts) ? report.scripts : [],
  analytics: Array.isArray(report.analytics) ? report.analytics : [],
  options: {
    ...DEFAULT_REPORT_OPTIONS,
    ...(report.options && typeof report.options === "object"
      ? report.options
      : {}),
  },
  selectedType: report.selectedType || "Table",
});

const shouldSkipMetadataMatch = (field = {}) =>
  Boolean(
    field.custom ||
      field.custom_frontend_field ||
      field.genre === "custom-formula" ||
      field.addedAs === "measure_field" ||
      field.addedAs === "drillthrough_field",
  );

const getFieldLabel = (field = {}) =>
  field.label || field.alias || field.column || field.columnName || "field";

const getConnectedDatabaseNames = (metadata = {}) => {
  const names = new Set();
  if (metadata.name) {
    names.add(String(metadata.name));
  }
  Object.values(metadata.tables || {}).forEach((table) => {
    if (table?.dataSourceName) {
      names.add(String(table.dataSourceName));
    }
    if (table?.databaseName) {
      names.add(String(table.databaseName));
    }
  });
  return names;
};

const findMetadataColumn = (metadata, field = {}) => {
  const tables = metadata?.tables;
  if (!tables || typeof tables !== "object") {
    return null;
  }

  const columnId = String(field.columnID ?? field.columnId ?? "").trim();
  if (columnId) {
    for (const table of Object.values(tables)) {
      for (const column of Object.values(table?.columns || {})) {
        if (String(column?.id ?? "") === columnId) {
          return { table, column };
        }
      }
    }
  }

  const qualified = String(
    field.column || field.fullyQualifiedColumn || "",
  ).trim();
  if (!qualified.includes(".")) {
    return null;
  }
  const [tableName, ...columnParts] = qualified.split(".");
  const columnName = columnParts.join(".");
  const table =
    tables[tableName] ||
    Object.values(tables).find(
      (entry) => entry?.name === tableName || entry?.alias === tableName,
    );
  if (!table?.columns) {
    return null;
  }
  if (table.columns[columnName]) {
    return { table, column: table.columns[columnName] };
  }
  const column = Object.values(table.columns).find(
    (entry) =>
      entry?.name === columnName ||
      entry?.alias === columnName ||
      `${table.name}.${entry?.name}` === qualified ||
      `${table.name}.${entry?.alias}` === qualified,
  );
  return column ? { table, column } : null;
};

const fieldDatabaseMatchesMetadata = (field, metadata, matched) => {
  const fieldDatabase = String(
    field.databaseName || field.columnDatabase || "",
  ).trim();
  if (!fieldDatabase) {
    return true;
  }
  const connectedNames = getConnectedDatabaseNames(metadata);
  if (connectedNames.has(fieldDatabase)) {
    return true;
  }
  const matchedTableDb = String(
    matched?.table?.dataSourceName ||
      matched?.table?.databaseName ||
      metadata?.name ||
      "",
  ).trim();
  return matchedTableDb === fieldDatabase;
};

// Validate column/row fields against the connected metadatas/// fetchData should run or a canvas/toast mismatch message
export const getReportJsonFetchDecision = (report = {}) => {
  if (!report?.metadata) {
    return {
      shouldFetch: false,
      reason: "no-metadata",
      message: "Metadata not found",
      mismatchedFields: [],
    };
  }

  const fields = Array.isArray(report.fields) ? report.fields : [];
  const columnAndRowFields = fields.filter((field) =>
    ["column", "row"].includes(field?.addedAs),
  );

  if (!columnAndRowFields.length) {
    return {
      shouldFetch: false,
      reason: "no-fields",
      message:
        "JSON applied successfully. Add columns/rows, then Generate to fetch data.",
      mismatchedFields: [],
    };
  }

  const mismatchedFields = [];
  columnAndRowFields.forEach((field) => {
    if (shouldSkipMetadataMatch(field)) {
      return;
    }
    const matched = findMetadataColumn(report.metadata, field);
    if (!matched) {
      mismatchedFields.push(getFieldLabel(field));
      return;
    }
    if (!fieldDatabaseMatchesMetadata(field, report.metadata, matched)) {
      mismatchedFields.push(getFieldLabel(field));
    }
  });

  if (mismatchedFields.length) {
    return {
      shouldFetch: false,
      reason: "metadata-mismatch",
      message: METADATA_MISMATCH_MESSAGE,
      mismatchedFields,
    };
  }

  return {
    shouldFetch: true,
    reason: "matched",
    message: "",
    mismatchedFields: [],
  };
};

export const canFetchReportFromJson = (report = {}) =>
  getReportJsonFetchDecision(report).shouldFetch;

export const buildMetadataMismatchReportData = (message = METADATA_MISMATCH_MESSAGE) => ({
  invalid: true,
  message,
  data: [],
  loading: false,
});
