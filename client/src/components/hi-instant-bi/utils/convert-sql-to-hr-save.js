import { cloneDeep } from "lodash-es";
import { v4 as uuidv4 } from "uuid";
import { getUpdatedColorProperties } from "../../hi-reports/hi-editing-area/utils/property-utils";
import { getSaveData } from "../../hi-reports/utils/base";
import { add_column, add_filter, add_mark, add_row } from "../../hi-reports/utils/utilities";
import { getIntialReportState } from "../../../redux/reducers/initialStates";

export const CONVERT_HREPORT_STORAGE_KEY = "convertHreportInfo";

const CHART_NAME_TO_MARK_VIZ = {
  bar: { mark: "Chart", viz: "Bar" },
  column: { mark: "Chart", viz: "Bar" },
  line: { mark: "Chart", viz: "Line" },
  area: { mark: "Chart", viz: "Area" },
  donut: { mark: "Chart", viz: "Doughnut" },
  doughnut: { mark: "Chart", viz: "Doughnut" },
  pie: { mark: "Chart", viz: "Arc" },
  arc: { mark: "Chart", viz: "Arc" },
  gauge: { mark: "Chart", viz: "Arc" },
  point: { mark: "Chart", viz: "Point" },
  calendar: { mark: "Chart", viz: "Calendar" },
  progress: { mark: "Chart", viz: "Progress" },
  radar: { mark: "Chart", viz: "Radar" },
  relation: { mark: "Chart", viz: "Relation" },
  waterfall: { mark: "Chart", viz: "Waterfall" },
  wordcloud: { mark: "Chart", viz: "Text" },
  text: { mark: "Chart", viz: "Text" },
  heatmap: { mark: "Maps", viz: "Heatmap" },
  kpi: { mark: "Card", viz: "Bar" },
  table: { mark: "Table", viz: "" },
  grid_table: { mark: "Grid Table", viz: "" },
};

const MARK_TO_SELECTED_TYPE = {
  table: "Table",
  chart: "Antcharts",
  card: "Card",
  maps: "MapChart",
  "grid table": "SyncChart",
  "grid chart": "GridChart",
};

const noopDispatch = () => {};

const GROUP_BY_FUNCTION = "db.generic.groupBy.group";
const VIZ_DIMENSION_ON_COLUMNS = new Set(["arc", "pie", "doughnut"]);
const FILTER_CONDITION_MAP = {
  EQ: "EQUALS",
  EQUALS: "EQUALS",
  IN: "IS_ONE_OF",
  IS_ONE_OF: "IS_ONE_OF",
};

const functionKey = (value) => {
  if (!value) {
    return "";
  }
  if (typeof value === "string") {
    return value.trim();
  }
  if (typeof value === "object") {
    return String(value.key || value.functionName || "").trim();
  }
  return "";
};

const isAggregateFunction = (key) => String(key || "").includes(".aggregate.");
const isGroupByFunction = (key) => String(key || "").includes(".groupBy.");
const isSqlFunction = (key) => String(key || "").startsWith("sql.");

const sqlFunctionParameterName = (key) => {
  if (/dateTime|sql\.date\./i.test(key)) {
    return "datetime";
  }
  return "value";
};

const sqlFunctionReturns = (key) => {
  if (/name$/i.test(key) || /monthname|dayname/i.test(key)) {
    return "text";
  }
  if (/month|year|day|hour|minute|second|quarter|week/i.test(key)) {
    return "numeric";
  }
  return "text";
};

const sqlFunctionSpec = (key, columnRef) => ({
  key,
  returns: sqlFunctionReturns(key),
  parameters: [
    {
      name: sqlFunctionParameterName(key),
      column: true,
      value: columnRef,
    },
  ],
});

const lastField = (report) => {
  const fields = report.fields || [];
  return fields[fields.length - 1];
};

const lastFilter = (report) => {
  const filters = report.filters || [];
  return filters[filters.length - 1];
};

const syncShelfArrays = (report) => {
  report.rows = (report.fields || [])
    .filter((field) => field.addedAs === "row")
    .map((field) => field.column);
  report.columns = (report.fields || [])
    .filter((field) => field.addedAs === "column")
    .map((field) => field.column);
};

const alignShelvesForViz = (report, subVizType) => {
  if (!VIZ_DIMENSION_ON_COLUMNS.has(String(subVizType || "").toLowerCase())) {
    return;
  }
  (report.fields || []).forEach((field) => {
    const isMeasure = Array.isArray(field.aggregate) && field.aggregate.length;
    if (isMeasure && field.addedAs === "column") {
      field.addedAs = "row";
    } else if (!isMeasure && field.addedAs === "row") {
      field.addedAs = "column";
    }
  });
  syncShelfArrays(report);
};

const ensureGroupByForAggregates = (report) => {
  const fields = report.fields || [];
  const hasAggregate = fields.some(
    (field) => Array.isArray(field.aggregate) && field.aggregate.length
  );
  if (!hasAggregate) {
    return;
  }
  fields.forEach((field) => {
    if (Array.isArray(field.aggregate) && field.aggregate.length) {
      return;
    }
    if (!Array.isArray(field.groupBy) || !field.groupBy.length) {
      field.groupBy = [GROUP_BY_FUNCTION];
    }
  });
};

export const unwrapConvertHreportResponse = (response = {}) => {
  if (response?.sql_parts || response?.viz_parts || response?.metadata) {
    return response;
  }
  if (response?.response) {
    return response.response;
  }
  return response;
};

const hasTables = (value) =>
  value?.tables && typeof value.tables === "object" && Object.keys(value.tables).length > 0;

export const unwrapTablesMetadata = (metadata) => {
  if (!metadata || typeof metadata !== "object") {
    return null;
  }
  if (hasTables(metadata)) {
    return metadata;
  }
  const nested = metadata.data;
  if (nested && typeof nested === "object") {
    if (hasTables(nested)) {
      return nested;
    }
    const modelMeta = nested.metadata;
    if (modelMeta && typeof modelMeta === "object") {
      if (hasTables(modelMeta)) {
        return modelMeta;
      }
      if (hasTables(modelMeta.data)) {
        const formData = {
          ...(modelMeta.data.formData || {}),
          location:
            modelMeta.location ||
            modelMeta.data.metadataDir ||
            modelMeta.data.formData?.location ||
            metadata.formData?.location ||
            "",
          metadataFileName:
            modelMeta.metadataFileName ||
            modelMeta.data.formData?.metadataFileName ||
            metadata.formData?.metadataFileName ||
            "",
        };
        return {
          ...modelMeta.data,
          formData,
          location: formData.location,
          metadataFileName: formData.metadataFileName,
        };
      }
    }
  }
  return null;
};

export const vizModelToVizParts = (viz = {}) => {
  const model = viz.viz_model && typeof viz.viz_model === "object" ? viz.viz_model : {};
  const chart = model.chart && typeof model.chart === "object" ? model.chart : {};
  const props = model.properties && typeof model.properties === "object" ? model.properties : {};
  const color = props.color;
  let colorValue = "";
  let colorField = "";
  if (typeof color === "string" && color.trim()) {
    const text = color.trim();
    if (text.startsWith("#") || text.toLowerCase().startsWith("rgb")) {
      colorValue = text;
    } else {
      colorField = text;
    }
  }
  return {
    chart_name: String(viz.chart_name || ""),
    mark: String(chart.mark || ""),
    viz: String(chart.viz || ""),
    color: colorValue,
    background: String(props.background || ""),
    title: String(props.title || viz.vf_title || ""),
    colorField,
  };
};

export const normalizeConvertParts = (payload = {}) => {
  const sql = payload.sql_parts || {};
  return {
    metadata: payload.metadata,
    columns: payload.columns || sql.columns || [],
    filters: payload.filters || sql.filters || [],
    orderBy: payload.orderBy || sql.orderBy || [],
    viz: payload.viz || payload.viz_parts || {},
    reportInfo: payload.reportInfo,
    location: payload.location || sql.location || "",
    metadataFileName: payload.metadataFileName || sql.metadataFileName || "",
  };
};

export const mapVizToHelical = (viz = {}) => {
  let mark = String(viz.mark || "").trim();
  let child = String(viz.viz || "").trim();
  const chartName = String(viz.chart_name || "").trim().toLowerCase();
  if (!mark && chartName && CHART_NAME_TO_MARK_VIZ[chartName]) {
    mark = CHART_NAME_TO_MARK_VIZ[chartName].mark;
    child = child || CHART_NAME_TO_MARK_VIZ[chartName].viz;
  }
  const selectedType = MARK_TO_SELECTED_TYPE[mark.toLowerCase()] || "Table";
  const subVizType = child ? child.toLowerCase() : "";
  return { selectedType, subVizType, mark, viz: child };
};

export const parseCssColor = (value) => {
  if (!value || typeof value !== "string") {
    return null;
  }
  const text = value.trim();
  const hex = text.match(/^#([0-9a-f]{6})$/i);
  if (hex) {
    const n = parseInt(hex[1], 16);
    return { r: (n >> 16) & 255, g: (n >> 8) & 255, b: n & 255, a: 1 };
  }
  const rgb = text.match(/^rgba?\(\s*(\d+)\s*,\s*(\d+)\s*,\s*(\d+)/i);
  if (rgb) {
    return { r: Number(rgb[1]), g: Number(rgb[2]), b: Number(rgb[3]), a: 1 };
  }
  return null;
};

const columnAliasKey = (value) =>
  String(value || "")
    .trim()
    .toLowerCase()
    .replace(/[^a-z0-9]/g, "");

const findColumnByAlias = (metadata, name) => {
  const wanted = columnAliasKey(name);
  if (!wanted || !metadata?.tables) {
    return null;
  }
  const tables = metadata.tables;
  for (const tableKey of Object.keys(tables)) {
    const table = tables[tableKey];
    const columns = table?.columns || {};
    for (const columnKey of Object.keys(columns)) {
      const column = columns[columnKey];
      if (
        columnAliasKey(columnKey) === wanted ||
        columnAliasKey(column?.alias) === wanted ||
        columnAliasKey(column?.name) === wanted
      ) {
        return { table: tableKey, column: columnKey };
      }
    }
  }
  return null;
};

export const sqlPartsFromVizModel = (viz = {}, metadata) => {
  const model = viz?.viz_model && typeof viz.viz_model === "object" ? viz.viz_model : {};
  const data = model.data && typeof model.data === "object" ? model.data : {};
  const columns = [];
  const pushShelf = (names, shelf) => {
    (names || []).forEach((name) => {
      const resolved = findColumnByAlias(metadata, name);
      if (!resolved) {
        return;
      }
      const metaColumn = metadata?.tables?.[resolved.table]?.columns?.[resolved.column];
      columns.push({
        table: resolved.table,
        column: resolved.column,
        shelf,
        databaseFunction: metaColumn?.defaultFunction,
        alias: String(name || resolved.column),
      });
    });
  };
  pushShelf(data.rows, "row");
  pushShelf(data.columns, "column");
  const filters = [];
  (data.filters || []).forEach((item) => {
    if (!item || typeof item !== "object") {
      return;
    }
    const resolved = findColumnByAlias(metadata, item.name || item.column);
    if (!resolved) {
      return;
    }
    filters.push({
      table: resolved.table,
      column: resolved.column,
      condition: item.condition,
      value: item.value !== undefined ? item.value : item.values,
    });
  });
  return { columns, filters };
};

const resolveColumnRef = (metadata, tableName, columnName) => {
  const tables = metadata?.tables || {};
  const wantedTable = String(tableName || "");
  const wantedColumn = String(columnName || "");
  if (tables[wantedTable]?.columns?.[wantedColumn]) {
    return { table: wantedTable, column: wantedColumn };
  }
  const tableKey = Object.keys(tables).find(
    (key) => key.toLowerCase() === wantedTable.toLowerCase()
  );
  if (tableKey && tables[tableKey]?.columns?.[wantedColumn]) {
    return { table: tableKey, column: wantedColumn };
  }
  if (tableKey) {
    const colKey = Object.keys(tables[tableKey].columns || {}).find(
      (key) => key.toLowerCase() === wantedColumn.toLowerCase()
    );
    if (colKey) {
      return { table: tableKey, column: colKey };
    }
  }
  for (const [name, table] of Object.entries(tables)) {
    if (table?.columns?.[wantedColumn]) {
      return { table: name, column: wantedColumn };
    }
    const colKey = Object.keys(table?.columns || {}).find(
      (key) => key.toLowerCase() === wantedColumn.toLowerCase()
    );
    if (colKey) {
      return { table: name, column: colKey };
    }
  }
  return { table: wantedTable, column: wantedColumn };
};

const aggregateFunctionFromSql = (before) => {
  const text = String(before || "");
  if (/\bcount\s*\(\s*$/i.test(text)) {
    return "db.generic.aggregate.count";
  }
  if (/\bsum\s*\(\s*$/i.test(text)) {
    return "db.generic.aggregate.sum";
  }
  if (/\bavg\s*\(\s*$/i.test(text)) {
    return "db.generic.aggregate.avg";
  }
  if (/\bmin\s*\(\s*$/i.test(text)) {
    return "db.generic.aggregate.min";
  }
  if (/\bmax\s*\(\s*$/i.test(text)) {
    return "db.generic.aggregate.max";
  }
  return "";
};

export const sqlPartsFromSql = (sql = "", metadata) => {
  const text = String(sql || "")
    .replace(/```sql\s*/gi, "")
    .replace(/```/g, "");
  const columns = [];
  const seen = new Set();
  const re = /"([^"]+)"\s*\.\s*"([^"]+)"/g;
  let match;
  while ((match = re.exec(text))) {
    const key = `${match[1]}.${match[2]}`.toLowerCase();
    if (seen.has(key)) {
      continue;
    }
    seen.add(key);
    const resolved = resolveColumnRef(metadata, match[1], match[2]);
    const metaColumn = metadata?.tables?.[resolved.table]?.columns?.[resolved.column];
    if (!metaColumn) {
      continue;
    }
    const aggregated = aggregateFunctionFromSql(text.slice(Math.max(0, match.index - 48), match.index));
    columns.push({
      table: resolved.table,
      column: resolved.column,
      shelf: aggregated ? "column" : "row",
      databaseFunction: aggregated || metaColumn.defaultFunction,
      alias: metaColumn.alias || resolved.column,
    });
  }
  return { columns, filters: [] };
};

export const resolveItemSqlParts = (item = {}, metadata) => {
  if (item.sql_parts?.columns?.length) {
    return item.sql_parts;
  }
  const fromViz = sqlPartsFromVizModel(item.viz, metadata);
  if (fromViz.columns?.length) {
    return fromViz;
  }
  return sqlPartsFromSql(item.sql, metadata);
};

const attachMetadata = (report, metadata, location, metadataFileName) => {
  const next = metadata && typeof metadata === "object" ? cloneDeep(metadata) : {};
  const formData = {
    ...(next.formData || {}),
  };
  if (location && !formData.location) {
    formData.location = location;
  }
  if (metadataFileName && !formData.metadataFileName) {
    formData.metadataFileName = metadataFileName;
  }
  next.formData = formData;
  if (!next.classifier) {
    next.classifier = "db.generic";
  }
  if (!next.name && next.databaseName) {
    next.name = next.databaseName;
  }
  if (!next.dataSource || typeof next.dataSource !== "object") {
    next.dataSource = {};
  }
  next.uid = next.uid || uuidv4();
  if (next.tables && typeof next.tables === "object") {
    Object.keys(next.tables).forEach((tableName) => {
      const table = next.tables[tableName];
      if (!table || typeof table !== "object") {
        return;
      }
      table.key = table.key || uuidv4();
      table.name = table.name || tableName;
      table.alias = table.alias || tableName;
    });
  }
  report.metadata = next;
  report.database = next.name || report.database;
  return next;
};

export const persistConvertHreportSeed = (report) => {
  const payload = { extension: "convert-hreport", report, ts: Date.now() };
  try {
    localStorage.setItem(CONVERT_HREPORT_STORAGE_KEY, JSON.stringify(payload));
    return true;
  } catch (e) {
    try {
      const slim = cloneDeep(report);
      if (slim.metadata) {
        slim.metadata = {
          formData: slim.metadata.formData,
          location: slim.metadata.location,
          metadataFileName: slim.metadata.metadataFileName,
          classifier: slim.metadata.classifier,
          uid: slim.metadata.uid,
          name: slim.metadata.name,
        };
      }
      localStorage.setItem(
        CONVERT_HREPORT_STORAGE_KEY,
        JSON.stringify({ extension: "convert-hreport", report: slim, ts: Date.now() })
      );
      return true;
    } catch (err) {
      return false;
    }
  }
};

export const consumeConvertHreportSeed = () => {
  try {
    const raw = localStorage.getItem(CONVERT_HREPORT_STORAGE_KEY);
    if (!raw) {
      return null;
    }
    localStorage.removeItem(CONVERT_HREPORT_STORAGE_KEY);
    const parsed = JSON.parse(raw);
    if (parsed?.extension === "convert-hreport" && parsed.report) {
      return parsed;
    }
  } catch (e) {
    return null;
  }
  return null;
};

export const CONVERT_DASHBOARD_STORAGE_KEY = "convertDashboardInfo";

const slimDashboardMetadata = (config) => {
  const next = cloneDeep(config);
  (next.gridItemsData || []).forEach((item) => {
    const metadata = item?.reportInfo?.file?.metadata;
    if (!metadata) {
      return;
    }
    item.reportInfo.file.metadata = {
      formData: metadata.formData,
      location: metadata.location,
      metadataFileName: metadata.metadataFileName,
      classifier: metadata.classifier,
      uid: metadata.uid,
      name: metadata.name,
    };
  });
  return next;
};

export const persistConvertDashboardSeed = (dashboardConfig) => {
  const payload = { extension: "convert-dashboard", dashboardConfig, ts: Date.now() };
  try {
    localStorage.setItem(CONVERT_DASHBOARD_STORAGE_KEY, JSON.stringify(payload));
    return true;
  } catch (e) {
    try {
      localStorage.setItem(
        CONVERT_DASHBOARD_STORAGE_KEY,
        JSON.stringify({
          extension: "convert-dashboard",
          dashboardConfig: slimDashboardMetadata(dashboardConfig),
          ts: Date.now(),
        })
      );
      return true;
    } catch (err) {
      return false;
    }
  }
};

export const consumeConvertDashboardSeed = () => {
  try {
    const raw = localStorage.getItem(CONVERT_DASHBOARD_STORAGE_KEY);
    if (!raw) {
      return null;
    }
    localStorage.removeItem(CONVERT_DASHBOARD_STORAGE_KEY);
    const parsed = JSON.parse(raw);
    if (parsed?.extension === "convert-dashboard" && parsed.dashboardConfig) {
      return parsed;
    }
  } catch (e) {
    return null;
  }
  return null;
};

const applyColumns = (report, columns, dispatch) => {
  const items = columns || [];
  const hasAggregate = items.some((item) =>
    isAggregateFunction(functionKey(item.databaseFunction))
  );
  items.forEach((item) => {
    const { table, column } = resolveColumnRef(report.metadata, item.table, item.column);
    const fnKey = functionKey(item.databaseFunction);
    const shelf = String(
      item.shelf || (isAggregateFunction(fnKey) ? "column" : "row")
    ).toLowerCase();
    const payload = { table, column };
    if (shelf === "column" || isAggregateFunction(fnKey)) {
      if (isAggregateFunction(fnKey)) {
        payload.defaultFunction = fnKey;
      }
      add_column(payload, report, dispatch);
      return;
    }
    if (isSqlFunction(fnKey)) {
      payload.defaultFunction = GROUP_BY_FUNCTION;
      add_row(payload, report, dispatch);
      const field = lastField(report);
      if (field) {
        field.databaseFunction = sqlFunctionSpec(fnKey, field.column);
        if (sqlFunctionReturns(fnKey) === "numeric") {
          field.floatingType = "";
        }
      }
      return;
    }
    if (isGroupByFunction(fnKey) || hasAggregate) {
      payload.defaultFunction = isGroupByFunction(fnKey) ? fnKey : GROUP_BY_FUNCTION;
    } else if (fnKey) {
      payload.defaultFunction = fnKey;
    }
    add_row(payload, report, dispatch);
  });
  ensureGroupByForAggregates(report);
};

const applyFilters = (report, filters, dispatch) => {
  (filters || []).forEach((item) => {
    const { table, column } = resolveColumnRef(report.metadata, item.table, item.column);
    const tableMeta = report.metadata?.tables?.[table];
    if (!tableMeta?.columns?.[column]) {
      return;
    }
    let values = item.values !== undefined ? item.values : item.value;
    if (values !== undefined && values !== null && !Array.isArray(values)) {
      values = [values];
    }
    const rawCondition = String(item.condition || "IS_ONE_OF").trim() || "IS_ONE_OF";
    let condition = FILTER_CONDITION_MAP[rawCondition] || rawCondition;
    if (condition === "EQUALS" && Array.isArray(values) && values.length > 1) {
      condition = "IS_ONE_OF";
    }
    const fnKey = functionKey(item.databaseFunction);
    const sqlSpec = isSqlFunction(fnKey)
      ? sqlFunctionSpec(fnKey, `${table}.${column}`)
      : undefined;
    try {
      add_filter(
        {
          table,
          column,
          values,
          condition,
          ...(sqlSpec ? { databaseFunction: sqlSpec } : {}),
        },
        report,
        dispatch
      );
      const created = lastFilter(report);
      if (created) {
        if (values) {
          created.values = values;
          created.condition = condition;
        }
        if (sqlSpec) {
          created.databaseFunction = sqlSpec;
        }
      }
    } catch (e) {
      // Skip a malformed filter so the rest of the report can still open.
    }
  });
};

const fieldMatchesOrderBy = (field, item) => {
  const table = String(item.table || "");
  const column = String(item.column || "");
  const alias = String(item.alias || "");
  const qualified = table && column ? `${table}.${column}` : column;
  return (
    field.column === qualified ||
    field.column === column ||
    (column && String(field.column || "").endsWith(`.${column}`)) ||
    (column && field.metaDataAlias === column) ||
    (alias && (field.label === alias || field.metaDataAlias === alias))
  );
};

const applyOrderBy = (report, orderBy) => {
  (orderBy || []).forEach((item) => {
    const direction = String(item.direction || "asc").toLowerCase() === "desc" ? "desc" : "asc";
    const field = (report.fields || []).find((entry) => fieldMatchesOrderBy(entry, item));
    if (field) {
      field.orderBy = [direction];
    }
  });
};

const applyColorMark = (report, viz, columns, dispatch) => {
  const colorField = String(viz?.colorField || "").trim();
  if (!colorField) {
    return;
  }
  const fromParts = (columns || []).find(
    (col) =>
      col.column === colorField ||
      col.alias === colorField ||
      `${col.table}.${col.column}` === colorField
  );
  const table = fromParts?.table || "";
  const column = fromParts?.column || colorField;
  const resolved = resolveColumnRef(report.metadata, table, column);
  add_mark({ ...resolved, markType: "color" }, report, dispatch);
};

const applyFormatColor = (report, colorValue) => {
  const rgba = parseCssColor(colorValue);
  if (!rgba) {
    return;
  }
  const formatColor = report.properties.formatColor || {};
  report.properties.formatColor = getUpdatedColorProperties(
    [{ groupId: "formatColor", key: "defaultColor", value: rgba }],
    "formatColor",
    formatColor.showAll,
    formatColor.formatColorField,
    formatColor.formatColorStyle,
    report
  );
};

const applyViz = (report, viz = {}) => {
  const { selectedType, subVizType } = mapVizToHelical(viz);
  report.selectedType = selectedType;
  if (report.marksList?.[0] && subVizType) {
    report.marksList[0].subVizType = subVizType;
  }
  alignShelvesForViz(report, subVizType);
  if (selectedType === "Card" && viz.viz) {
    report.properties.card = {
      ...(report.properties.card || {}),
      chartType: viz.viz,
    };
  }
  if (viz.title) {
    report.properties.title = {
      ...(report.properties.title || {}),
      show: true,
      value: viz.title,
    };
  }
  if (viz.color) {
    applyFormatColor(report, viz.color);
  } else if (viz.background) {
    applyFormatColor(report, viz.background);
  }
};

export const buildHrReportFromParts = (payload = {}, dispatch = noopDispatch) => {
  const {
    metadata,
    columns,
    filters,
    orderBy,
    viz,
    reportInfo,
    location,
    metadataFileName,
  } = normalizeConvertParts(payload);

  const report = cloneDeep(getIntialReportState({ active: true }));
  report.mode = "create";
  report.interactiveMode = true;
  report.drillDown = true;
  report.databaseFunctions = report.databaseFunctions || {};
  attachMetadata(report, metadata, location, metadataFileName);
  if (reportInfo && typeof reportInfo === "object") {
    report.reportInfo = { ...report.reportInfo, ...reportInfo };
  }

  applyColumns(report, columns, dispatch);
  applyFilters(report, filters, dispatch);
  applyOrderBy(report, orderBy);
  applyColorMark(report, viz, columns, dispatch);
  applyViz(report, viz);
  return report;
};

export const convertSqlToHrSaveFormData = (payload = {}, dispatch = noopDispatch) => {
  return getSaveData(buildHrReportFromParts(payload, dispatch));
};

const toShelfField = (item = {}, metadata) => {
  const resolved = resolveColumnRef(metadata, item.table, item.column);
  return {
    table: resolved.table,
    column: resolved.column,
    defaultFunction: item.databaseFunction || item.defaultFunction,
    values: item.values !== undefined ? item.values : item.value,
    condition: item.condition,
  };
};

export const buildInlineReportFile = (payload = {}, dispatch = noopDispatch) => {
  const { metadata, columns, filters, viz, location, metadataFileName } = normalizeConvertParts(payload);
  const mapped = mapVizToHelical(viz);
  const report = buildHrReportFromParts(payload, dispatch);
  const rowCols = (columns || []).filter(
    (col) => String(col.shelf || "row").toLowerCase() !== "column"
  );
  const colCols = (columns || []).filter(
    (col) => String(col.shelf || "").toLowerCase() === "column"
  );
  let marks = [];
  const colorField = String(viz?.colorField || "").trim();
  if (colorField) {
    const fromParts = (columns || []).find(
      (col) =>
        col.column === colorField ||
        col.alias === colorField ||
        `${col.table}.${col.column}` === colorField
    );
    const resolved = resolveColumnRef(
      metadata,
      fromParts?.table || "",
      fromParts?.column || colorField
    );
    marks = [
      {
        table: resolved.table,
        column: resolved.column,
        markType: "color",
      },
    ];
  }
  const locationValue = location || metadata?.formData?.location || metadata?.location || "";
  const metadataFile =
    metadataFileName || metadata?.formData?.metadataFileName || metadata?.metadataFileName || "";
  return {
    metadata: {
      ...(metadata || {}),
      location: locationValue,
      metadataFileName: metadataFile,
    },
    columns: colCols.map((item) => toShelfField(item, metadata)),
    rows: rowCols.map((item) => toShelfField(item, metadata)),
    filters: (filters || []).map((item) => toShelfField(item, metadata)),
    fields: cloneDeep(report.fields || []),
    hydratedFilters: cloneDeep(report.filters || []),
    marks,
    visualisationType: mapped.selectedType,
    selectedType: mapped.selectedType,
    properties: report.properties,
    marksList: report.marksList,
    title: viz?.title || report.properties?.title?.value || "",
    inline: true,
  };
};

export const unwrapConvertDashboardResponse = (response = {}) => {
  if (response?.layout || response?.items) {
    return response;
  }
  if (response?.response) {
    return response.response;
  }
  return response;
};

const chatItemHasError = (msg = {}, full = {}) => {
  if (msg.error === true || msg.failed === true) {
    return true;
  }
  const status = String(msg.status || full.status || full.request_status || "").toLowerCase();
  if (["fail", "failed", "error", "aborted"].includes(status)) {
    return true;
  }
  const error = full.error || full.sql_error || msg.sql_error || msg.error;
  if (typeof error === "string" && error.trim() && error !== "Not Generated") {
    return true;
  }
  if (Array.isArray(error) && error.length) {
    return true;
  }
  return Boolean(error && error !== true && typeof error === "object");
};

export const collectChatVizItems = (activeReport = {}) => {
  const activeChat = (activeReport.chats || []).find(
    (chat) => chat.chatID === activeReport.activeChatID
  );
  const messages = activeChat?.messageList || [];
  const loaded = activeReport.loadedChatResponses || {};
  return messages
    .filter((msg) => !msg.isUser && msg.chatSequenceId)
    .map((msg) => {
      const full = {
        ...(loaded[msg.chatSequenceId] || {}),
        ...(msg.fullChatResponse || {}),
      };
      if (chatItemHasError(msg, full)) {
        return null;
      }
      const viz = { ...(full.viz || msg.vizDetails || {}) };
      delete viz.vf_template;
      const sql = String(full.sql?.raw_sql || full.sql?.sql || msg.sql || "")
        .replace(/```sql\s*/gi, "")
        .replace(/```/g, "")
        .trim();
      const userQuery = [...messages]
        .slice(0, messages.indexOf(msg))
        .reverse()
        .find((entry) => entry.isUser)?.text || full.user_query || "";
      const vizModel = viz.viz_model || full.viz_model || null;
      const dataModel = full.data_model || viz.data_model || null;
      if (!sql && !viz.viz_model && !viz.chart_name && !vizModel && !dataModel) {
        return null;
      }
      return {
        id: String(msg.chatSequenceId),
        chat_sequence_id: String(msg.chatSequenceId),
        chatid: String(activeReport.activeChatID || ""),
        user_query: String(userQuery || ""),
        data_model: dataModel,
        viz_model: vizModel,
        sql,
        viz,
        summary: full.summary?.insight || "",
      };
    })
    .filter(Boolean);
};
