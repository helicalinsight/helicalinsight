import notify from "../../hi-notifications/notify";
import {
  getAgentStateFromCubeFields,
  getSerializedAgentFromCubeFields,
} from "./agent-cube-bridge";

export const getSerializedAgentStateFromFields = ({
  cubeFieldsData,
  existingAgentData,
}) => getSerializedAgentFromCubeFields(cubeFieldsData, existingAgentData);

export const getAgentStateForSave = ({
  cubeFieldsData,
  existingAgentData,
}) => getAgentStateFromCubeFields(cubeFieldsData, existingAgentData);

const norm = (value) => String(value ?? "").trim().toLowerCase();
const names = (...values) => values.map(norm).filter(Boolean);
const getColumnName = (field) =>
  String(field?.columnName || field?.agentSource?.columnName || "").trim();
const getTableName = (field) =>
  String(
    field?.table?.name ||
      field?.table?.alias ||
      field?.agentSource?.table ||
      field?.tableName ||
      "",
  ).trim();

const flattenFields = (children = []) =>
  (children || []).flatMap((child) => {
    if (!child || child.isDelete) return [];
    return child.isHierarchy
      ? [child, ...flattenFields(child.children)]
      : [child];
  });

const isManualMetric = (field) =>
  field?.agentSource?.kind === "manual-metric" ||
  (field?.measure?.isMeasureCheck &&
    !field.tableId &&
    !getColumnName(field) &&
    !field.columnId);

/// Metadat uses id agent json / drag-drop use columnId
export const buildMetadataColumnIndex = (metadataTablesData = {}) => {
  const tables = metadataTablesData?.tables || {};
  const ids = new Set();
  const columns = new Set();
  const qualified = new Set();

  Object.values(tables).forEach((table) => {
    if (!table || typeof table !== "object") return;
    const tableNames = names(table.name, table.alias);
    Object.entries(table.columns || {}).forEach(([key, col]) => {
      if (!col || typeof col !== "object") return;
      names(col.id, col.columnId).forEach((id) => ids.add(id));
      names(key, col.name, col.alias, col.columnName, col.originalName).forEach(
        (column) => {
          columns.add(column);
          tableNames.forEach((tableName) =>
            qualified.add(`${tableName}.${column}`),
          );
        },
      );
    });
  });
  return { ids, columns, qualified, hasTables: Object.keys(tables).length > 0 };
};

const matchesMetadata = (field, index) => {
  const columnId = String(field.columnId ?? "").trim();
  if (columnId && index.ids.has(columnId)) return true;
  const column = norm(getColumnName(field));
  if (!column) return false;
  if (column.includes(".")) return index.qualified.has(column);
  const table = norm(getTableName(field));
  return (
    (Boolean(table) && index.qualified.has(`${table}.${column}`)) ||
    index.columns.has(column)
  );
};

export const isAgentFieldMissingFromMetadata = (field, index) => {
  if (!index?.hasTables || !field || field.isDelete || isManualMetric(field)) {
    return false;
  }
  if (field.isHierarchy && !field.columnId && !getColumnName(field)) {
    return false;
  }
  const columnId = String(field.columnId ?? "").trim();
  const columnName = getColumnName(field);
  if (!columnId && !columnName) return true;
  return !matchesMetadata(field, index);
};

export const getAgentFieldsMissingFromMetadata = (
  children = [],
  metadataTablesData = {},
) => {
  const index = buildMetadataColumnIndex(metadataTablesData);
  if (!index.hasTables) return [];
  return flattenFields(children).filter((field) =>
    isAgentFieldMissingFromMetadata(field, index),
  );
};

export const getAgentMissingMetadataFieldKeys = (
  children = [],
  metadataTablesData = {},
) =>
  new Set(
    getAgentFieldsMissingFromMetadata(children, metadataTablesData)
      .map((field) => field.key)
      .filter(Boolean),
  );

export const validateAgentSaveInput = ({
  agentState,
  cubeFieldsData,
  isRawJsonView,
  metadataTablesData,
  dispatch,
}) => {
  if (isRawJsonView && !agentState) return false;
  const missing = getAgentFieldsMissingFromMetadata(
    cubeFieldsData?.children,
    metadataTablesData,
  );
  if (!missing.length) return true;
  if (dispatch) {
    notify(dispatch).error({
      type: "Frontend",
      message:
        "Some columns do not match the connected metadata. Fix the highlighted fields before saving.",
    });
  }
  return false;
};
