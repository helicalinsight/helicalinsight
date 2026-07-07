import { v4 as uuidv4 } from "uuid";
import {
  ensureShape,
  parseAgentData,
  syncTopicMappings,
} from "../components/semantic-metadata-editor/semantic-metadata-utils";
import {
  formatArrayAsCommaSeparated,
  formatSingleTopic,
  parseCommaSeparatedToArray,
  parseSingleTopic,
} from "../../hi-cube/cubeSemanticFields";
import { cubeEditorMeasureData } from "../../hi-cube/cubeConstants";

const PER_COLUMN_AGENT_FIELDS = [
  { childField: "synonyms", agentArray: "synonyms", valueKey: "synonyms" },
  { childField: "example", agentArray: "examples", valueKey: "eg" },
];

const findTableIndex = (tables, tableName) =>
  tables.findIndex((table) => table.database_table === tableName);

const getTableNameFromChild = (child) => {
  if (child.isHierarchy) {
    return "";
  }
  return (
    child.agentSource?.table ||
    child.table?.name ||
    child.table?.alias ||
    child.tableName ||
    ""
  );
};

const getExportedFieldName = (child) =>
  (child.fields || child.columnName || "").trim();

const flattenCubeChildren = (children = []) =>
  children.flatMap((child) => {
    if (child.isDelete) {
      return [];
    }
    if (child.isHierarchy) {
      return flattenCubeChildren(child.children || []);
    }
    return [child];
  });

const omitEmpty = (obj, keys) => {
  const next = { ...obj };
  keys.forEach((key) => {
    if (next[key] === "" || next[key] == null) {
      delete next[key];
    }
  });
  return next;
};

const hasColumnScopedEntries = (items, tableName) =>
  (items || []).some(
    (item) => item.database_table === tableName && item.column_name,
  );

const splitTableScopedValuesByColumnIndex = (
  values,
  columnIndex,
  columnCount,
) => {
  if (
    columnIndex == null ||
    columnCount == null ||
    columnCount <= 1 ||
    values.length !== columnCount
  ) {
    return values;
  }
  const value = values[columnIndex];
  return value ? [value] : [];
};

const findFieldValues = (
  items,
  tableName,
  columnName,
  valueKey,
  columnIndex,
  columnCount,
) => {
  if (!items?.length || !tableName) {
    return [];
  }
  if (columnName && hasColumnScopedEntries(items, tableName)) {
    const entry = items.find(
      (item) =>
        item.database_table === tableName && item.column_name === columnName,
    );
    return entry?.[valueKey] || [];
  }
  const entry = items.find(
    (item) => item.database_table === tableName && !item.column_name,
  );
  const values = entry?.[valueKey] || [];
  if (!columnName) {
    return values;
  }
  return splitTableScopedValuesByColumnIndex(
    values,
    columnIndex,
    columnCount,
  );
};

const createColumnFieldBuckets = () =>
  Object.fromEntries(
    PER_COLUMN_AGENT_FIELDS.map(({ agentArray }) => [agentArray, new Map()]),
  );

const getColumnFieldBucketKey = (tableName, columnName) =>
  `${tableName}\0${columnName}`;

const addColumnFieldValues = (
  columnBuckets,
  agentArray,
  tableName,
  columnName,
  values,
) => {
  if (!tableName || !columnName || !values.length) {
    return;
  }
  const bucketKey = getColumnFieldBucketKey(tableName, columnName);
  if (!columnBuckets[agentArray].has(bucketKey)) {
    columnBuckets[agentArray].set(bucketKey, {
      database_table: tableName,
      column_name: columnName,
      values: new Set(),
    });
  }
  const bucket = columnBuckets[agentArray].get(bucketKey);
  values.forEach((value) => {
    if (value) {
      bucket.values.add(value);
    }
  });
};

const columnFieldBucketsToArrays = (columnBuckets) => {
  const result = {};
  PER_COLUMN_AGENT_FIELDS.forEach(({ agentArray, valueKey }) => {
    result[agentArray] = Array.from(columnBuckets[agentArray].values()).map(
      ({ database_table, column_name, values }) =>
        omitEmpty(
          {
            database_table,
            column_name,
            [valueKey]: Array.from(values),
          },
          [valueKey],
        ),
    );
  });
  return result;
};

const mergeTableScopedEntries = (existing = [], next = [], tableNames) => {
  const touchedTables = new Set(tableNames);
  return [
    ...existing.filter((item) => !touchedTables.has(item.database_table)),
    ...next,
  ];
};

const buildSemanticExtras = (
  data,
  tableName,
  columnName,
  columnIndex,
  columnCount,
) =>
  PER_COLUMN_AGENT_FIELDS.reduce(
    (extras, { childField, agentArray, valueKey }) => ({
      ...extras,
      [childField]: formatArrayAsCommaSeparated(
        findFieldValues(
          data[agentArray],
          tableName,
          columnName,
          valueKey,
          columnIndex,
          columnCount,
        ),
      ),
    }),
    {},
  );

const formatColumnTopic = (topic) => formatSingleTopic(topic);

const getTableDimensionTopic = (dimensionName) =>
  formatColumnTopic(
    Array.isArray(dimensionName) ? dimensionName[0] : dimensionName,
  );

const resolveDimensionNameFromColumns = (
  columns = [],
  existingDimensionName = [],
) => {
  let lastTopic = "";
  columns.forEach((column) => {
    const topic = formatColumnTopic(column.topic);
    if (topic) {
      lastTopic = topic;
    }
  });
  if (lastTopic) {
    return [lastTopic];
  }
  const legacyTopic = getTableDimensionTopic(existingDimensionName);
  return legacyTopic ? [legacyTopic] : [];
};

const tableHasPerColumnTopics = (table) =>
  (table.columns || []).some((col) => formatColumnTopic(col.topic));

const resolveColumnTopic = (column, table) => {
  const columnTopic = formatColumnTopic(column.topic);
  if (columnTopic) {
    return columnTopic;
  }
  if (tableHasPerColumnTopics(table)) {
    return "";
  }
  return getTableDimensionTopic(table.dimension_name);
};

const normalizeCubeMetadata = (cubeMetadata = []) =>
  cubeMetadata.map((table) => {
    const columns = (table.columns || []).map((column) =>
      omitEmpty(
        {
          ...column,
          topic: formatColumnTopic(column.topic) || undefined,
        },
        ["topic"],
      ),
    );

    return {
      ...table,
      columns,
      dimension_name: resolveDimensionNameFromColumns(
        columns,
        table.dimension_name,
      ),
    };
  });

const hasNonEmptyText = (value) => String(value ?? "").trim().length > 0;

const hasBusinessMetricFields = (child) =>
  Boolean(
    child.measure?.isMeasureCheck ||
      hasNonEmptyText(child.formula) ||
      hasNonEmptyText(child.filter) ||
      hasNonEmptyText(child.description) ||
      parseCommaSeparatedToArray(child.example).length > 0,
  );

const hasBusinessMetricData = (metric, examples = []) => {
  if (!metric?.metric) {
    return false;
  }
  if (
    hasNonEmptyText(metric.description) ||
    hasNonEmptyText(metric.formula) ||
    hasNonEmptyText(metric.filter)
  ) {
    return true;
  }
  return (examples || []).some(
    (item) =>
      item.column_name === metric.metric &&
      (!metric.tables?.length ||
        metric.tables.includes(item.database_table)) &&
      (item.eg || []).some((value) => hasNonEmptyText(value)),
  );
};

const isStandaloneBusinessMetric = hasBusinessMetricFields;

const buildBusinessMetricFromChild = (child) => {
  const tableName = getTableNameFromChild(child);
  return omitEmpty(
    {
      metric: getExportedFieldName(child),
      description: child.description || "",
      formula: child.formula || "",
      filter: child.filter || "",
      tables: tableName ? [tableName] : [],
    },
    ["description", "filter", "formula"],
  );
};

export const syncBusinessMetricsWithCubeMetadata = (agentData) => {
  const data = ensureShape(parseAgentData(agentData));
  const metricMap = new Map();

  const upsertMetric = (metricName, partial) => {
    const existing = metricMap.get(metricName) || {};
    metricMap.set(
      metricName,
      omitEmpty(
        {
          metric: metricName,
          description: partial.description ?? existing.description ?? "",
          formula: partial.formula ?? existing.formula ?? "",
          filter: partial.filter ?? existing.filter ?? "",
          tables: partial.tables ?? existing.tables ?? [],
        },
        ["description", "filter", "formula"],
      ),
    );
  };

  data.business_metrics.forEach((metric) => {
    if (!metric?.metric) {
      return;
    }
    upsertMetric(metric.metric, {
      description: metric.description || "",
      formula: metric.formula || "",
      filter: metric.filter || "",
      tables: Array.isArray(metric.tables) ? [...metric.tables] : [],
    });
  });

  data.cube_metadata.forEach((table) => {
    const tableName = table.database_table || "";
    (table.columns || []).forEach((column) => {
      const columnName = column.column_name;
      if (!columnName || !metricMap.has(columnName)) {
        return;
      }
      const existing = metricMap.get(columnName);
      upsertMetric(columnName, {
        description: existing?.description || "",
        formula: existing?.formula || "",
        filter: existing?.filter || "",
        tables: existing?.tables?.length
          ? existing.tables
          : tableName
            ? [tableName]
            : [],
      });
    });
  });

  data.business_metrics = Array.from(metricMap.values()).filter((metric) =>
    hasBusinessMetricData(metric, data.examples),
  );
  return data;
};

const collectTopicsFromCubeChildren = (children = []) => {
  const topics = new Set();
  flattenCubeChildren(children).forEach((child) => {
    const topic = parseSingleTopic(child.topic);
    if (topic) {
      topics.add(topic);
    }
  });
  return Array.from(topics);
};

const buildDomainArrayFromCubeFields = (cubeFieldsData) => {
  const domainNames = parseCommaSeparatedToArray(cubeFieldsData.domainName);
  if (!domainNames.length) {
    return [];
  }
  const topics = collectTopicsFromCubeChildren(cubeFieldsData.children);
  return domainNames.map((domain_name) => ({
    domain_name,
    topics: [...topics],
  }));
};

const ensureTableBucket = (tableColumnMap, data, tableName) => {
  if (tableColumnMap.has(tableName)) {
    return;
  }
  const existingIdx = findTableIndex(data.cube_metadata, tableName);
  tableColumnMap.set(
    tableName,
    existingIdx >= 0
      ? { ...data.cube_metadata[existingIdx], columns: [] }
      : { database_table: tableName, dimension_name: [], columns: [] },
  );
};

const upsertTableColumn = (tableColumnMap, tableName, column) => {
  const tableObj = tableColumnMap.get(tableName);
  const idx = tableObj.columns.findIndex(
    (col) => col.column_name === column.column_name,
  );
  if (idx >= 0) {
    tableObj.columns[idx] = { ...tableObj.columns[idx], ...column };
  } else {
    tableObj.columns.push(column);
  }
};

const createColumnChild = (
  table,
  column,
  metric,
  semanticExtras = {},
) => {
  const metricForColumn = metric || {};
  const tableName = table.database_table || "";
  return {
    key: uuidv4(),
    fields: column.column_name || "",
    columnName: column.column_name || "",
    semanticType: column.semantic_type || "",
    synonyms: semanticExtras.synonyms || "",
    topic: resolveColumnTopic(column, table),
    formula: metricForColumn.formula || "",
    filter: metricForColumn.filter || "",
    example: semanticExtras.example || "",
    description: metricForColumn.description || "",
    fieldsDropdownOpen: false,
    isDimensionCheck: true,
    measure: { isMeasureCheck: false, DataType: "", Format: "" },
    isVisible: true,
    sort: { isSortCheck: true, value: "Ascending" },
    aggregation: { isAggregationCheck: false, value: "" },
    isPartitionCheck: false,
    table: { name: tableName },
    agentSource: {
      kind: "column",
      table: tableName,
      columnName: column.column_name,
    },
  };
};

const createMetricChild = (metric, semanticExtras = {}) => ({
  key: uuidv4(),
  fields: metric.metric || "",
  description: metric.description || "",
  formula: metric.formula || "",
  filter: metric.filter || "",
  topic: formatArrayAsCommaSeparated(metric.tables),
  semanticType: "",
  synonyms: semanticExtras.synonyms || "",
  example: semanticExtras.example || "",
  fieldsDropdownOpen: false,
  isDimensionCheck: false,
  measure: {
    isMeasureCheck: true,
    DataType: cubeEditorMeasureData[0].dataType,
    Format: cubeEditorMeasureData[0].format[0],
  },
  isVisible: true,
  sort: { isSortCheck: false, value: "" },
  aggregation: { isAggregationCheck: true, value: "Sum" },
  isPartitionCheck: false,
  agentSource: { kind: "metric", metricName: metric.metric },
});

const findMetricForColumn = (metrics, columnName) =>
  metrics.find((metric) => metric.metric === columnName);

export const convertAgentDataToCubeFieldsData = (agentData) => {
  const data = parseAgentData(agentData);
  const domain = data.metadata_info?.metadata?.domain || [];
  const description = data.metadata_info?.metadata?.description || "";
  const children = [];
  const usedMetrics = new Set();

  data.cube_metadata.forEach((table) => {
    const tableName = table.database_table || "";
    const columns = table.columns || [];
    columns.forEach((column, columnIndex) => {
      const metric = findMetricForColumn(data.business_metrics, column.column_name);
      if (metric) {
        usedMetrics.add(metric.metric);
      }
      children.push(
        createColumnChild(
          table,
          column,
          metric,
          buildSemanticExtras(
            data,
            tableName,
            column.column_name,
            columnIndex,
            columns.length,
          ),
        ),
      );
    });
  });

  data.business_metrics.forEach((metric) => {
    if (!usedMetrics.has(metric.metric)) {
      const primaryTable = (metric.tables || [])[0] || "";
      children.push(
        createMetricChild(
          metric,
          buildSemanticExtras(
            data,
            primaryTable,
            metric.metric,
            0,
            1,
          ),
        ),
      );
    }
  });

  return {
    id: "",
    domainName: formatArrayAsCommaSeparated(domain),
    cubeDescription: description,
    children,
    hierarchyData: { isHierarchyPresent: false, hierarchyList: [] },
  };
};

export const convertCubeFieldsDataToAgentData = (
  cubeFieldsData,
  existingAgentData,
) => {
  const data = ensureShape(parseAgentData(existingAgentData));
  data.metadata_info.metadata.domain = parseCommaSeparatedToArray(
    cubeFieldsData.domainName,
  );
  data.metadata_info.metadata.description = (
    cubeFieldsData.cubeDescription ?? ""
  ).trim();
  data.domain = buildDomainArrayFromCubeFields(cubeFieldsData);

  const metrics = [];
  const tableColumnMap = new Map();
  const columnFieldBuckets = createColumnFieldBuckets();

  flattenCubeChildren(cubeFieldsData.children).forEach((child) => {
    const tableName = getTableNameFromChild(child);
    const fieldName = getExportedFieldName(child);

    if (tableName && fieldName) {
      PER_COLUMN_AGENT_FIELDS.forEach(({ childField, agentArray }) => {
        addColumnFieldValues(
          columnFieldBuckets,
          agentArray,
          tableName,
          fieldName,
          parseCommaSeparatedToArray(child[childField]),
        );
      });

      ensureTableBucket(tableColumnMap, data, tableName);
      const topic = parseSingleTopic(child.topic);

      upsertTableColumn(
        tableColumnMap,
        tableName,
        omitEmpty(
          {
            column_name: fieldName,
            semantic_type: child.semanticType || undefined,
            topic,
          },
          ["semantic_type", "topic"],
        ),
      );
      if (hasBusinessMetricFields(child)) {
        metrics.push(buildBusinessMetricFromChild(child));
      }
    } else if (fieldName && isStandaloneBusinessMetric(child)) {
      const primaryTable = getTableNameFromChild(child);
      if (primaryTable) {
        PER_COLUMN_AGENT_FIELDS.forEach(({ childField, agentArray }) => {
          addColumnFieldValues(
            columnFieldBuckets,
            agentArray,
            primaryTable,
            fieldName,
            parseCommaSeparatedToArray(child[childField]),
          );
        });
      }
      metrics.push(buildBusinessMetricFromChild(child));
    }
  });

  data.business_metrics = metrics;

  const touchedTables = Array.from(tableColumnMap.keys());
  const touchedMetricTables = new Set(
    metrics.flatMap((metric) => metric.tables || []),
  );
  const tablesToReplace = new Set([...touchedTables, ...touchedMetricTables]);
  if (tablesToReplace.size) {
    const fieldBuckets = columnFieldBucketsToArrays(columnFieldBuckets);
    PER_COLUMN_AGENT_FIELDS.forEach(({ agentArray }) => {
      data[agentArray] = mergeTableScopedEntries(
        data[agentArray],
        fieldBuckets[agentArray],
        Array.from(tablesToReplace),
      );
    });
    tableColumnMap.forEach((tableObj) => {
      tableObj.dimension_name = resolveDimensionNameFromColumns(tableObj.columns);
    });
  }

  const untouchedTables = data.cube_metadata.filter(
    (table) => !tableColumnMap.has(table.database_table),
  );
  data.cube_metadata = [
    ...untouchedTables,
    ...Array.from(tableColumnMap.values()),
  ];

  return syncTopicMappings(data);
};

export const getAgentStateFromCubeFields = (
  cubeFieldsData,
  existingAgentData,
) =>
  ensureShape(
    convertCubeFieldsDataToAgentData(
      cubeFieldsData,
      existingAgentData || ensureShape({}),
    ),
  );

const prepareAgentData = (agentData) => {
  const data = syncBusinessMetricsWithCubeMetadata(agentData);
  data.cube_metadata = normalizeCubeMetadata(data.cube_metadata);
  return data;
};

const groupByTable = (items = []) => {
  const groups = new Map();
  items.forEach((item) => {
    const tableName = item.database_table || "";
    if (!groups.has(tableName)) {
      groups.set(tableName, []);
    }
    groups.get(tableName).push(item);
  });
  return groups;
};

const mergeTableEntries = (storedItems, displayItems, valueKey) => {
  const storedByTable = groupByTable(storedItems);
  const displayByTable = groupByTable(displayItems);

  return [...displayByTable.entries()].flatMap(([tableName, displayRows]) => {
    const storedRows = storedByTable.get(tableName) || [];

    return displayRows.map((displayRow, index) => {
      const storedRow =
        storedRows.find((row) =>
          JSON.stringify(row[valueKey]) === JSON.stringify(displayRow[valueKey]),
        ) || storedRows[index];

      return omitEmpty(
        {
          ...storedRow,
          ...displayRow,
          database_table: tableName,
          column_name: storedRow?.column_name || displayRow.column_name,
          [valueKey]: displayRow[valueKey] ?? storedRow?.[valueKey] ?? [],
        },
        ["column_name"],
      );
    });
  });
};

const mergeCubeMetadata = (storedTables, displayTables) => {
  const storedByTable = new Map(
    (storedTables || []).map((table) => [table.database_table, table]),
  );

  return (displayTables || []).map((displayTable) => {
    const tableName = displayTable.database_table || "";
    const storedTable = storedByTable.get(tableName) || {
      database_table: tableName,
      columns: [],
    };
    const storedColumns = new Map(
      (storedTable.columns || []).map((column) => [column.column_name, column]),
    );

    const columns = (displayTable.columns || []).map((displayColumn) => {
      const storedColumn = storedColumns.get(displayColumn.column_name) || {};
      return omitEmpty(
        {
          ...storedColumn,
          ...displayColumn,
          topic: formatColumnTopic(storedColumn.topic) || undefined,
        },
        ["topic"],
      );
    });

    return {
      ...storedTable,
      ...displayTable,
      columns,
      dimension_name: resolveDimensionNameFromColumns(
        columns,
        storedTable.dimension_name,
      ),
    };
  });
};

const hasHiddenAgentFields = (agentData) => {
  const data = ensureShape(parseAgentData(agentData));
  return (
    (data.synonyms || []).some((item) => item.column_name) ||
    (data.examples || []).some((item) => item.column_name) ||
    (data.cube_metadata || []).some((table) =>
      (table.columns || []).some((column) => formatColumnTopic(column.topic)),
    )
  );
};

export const stripAgentDataForDisplay = (agentData) => {
  const data = ensureShape(parseAgentData(agentData));
  return {
    ...data,
    cube_metadata: (data.cube_metadata || []).map((table) => ({
      ...table,
      columns: (table.columns || []).map(({ topic, ...column }) => column),
    })),
    synonyms: (data.synonyms || []).map(({ column_name, ...entry }) => entry),
    examples: (data.examples || []).map(({ column_name, ...entry }) => entry),
  };
};

export const mergeDisplayAgentDataWithStored = (
  storedAgentData,
  displayAgentData,
) => {
  const stored = ensureShape(parseAgentData(storedAgentData));
  const display = ensureShape(parseAgentData(displayAgentData));

  return ensureShape({
    ...stored,
    ...display,
    business_metrics: display.business_metrics ?? stored.business_metrics,
    metadata_info: display.metadata_info ?? stored.metadata_info,
    domain: display.domain ?? stored.domain,
    topic_mappings: display.topic_mappings ?? stored.topic_mappings,
    cube_metadata: mergeCubeMetadata(stored.cube_metadata, display.cube_metadata),
    synonyms: mergeTableEntries(stored.synonyms, display.synonyms, "synonyms"),
    examples: mergeTableEntries(stored.examples, display.examples, "eg"),
  });
};

export const resolveAgentDataFromInput = (storedAgentData, inputAgentData) => {
  const parsed = ensureShape(parseAgentData(inputAgentData));
  return hasHiddenAgentFields(parsed)
    ? parsed
    : mergeDisplayAgentDataWithStored(storedAgentData, parsed);
};

// Backward-compatible alias used by tests and editor imports.
export const agentDataHasStoredOnlyFields = hasHiddenAgentFields;

export const serializeAgentDataForDisplay = (agentData) =>
  JSON.stringify(stripAgentDataForDisplay(prepareAgentData(agentData)), null, 2);

export const serializeAgentData = (agentData) =>
  JSON.stringify(prepareAgentData(agentData), null, 2);

export const stripInternalTableRefsFromAgentState = (agentData) => {
  const data = ensureShape(parseAgentData(agentData));
  data.cube_metadata = normalizeCubeMetadata(
    (data.cube_metadata || []).map(({ table_ref, table_id, ...table }) => table),
  );
  return data;
};

export const getSerializedAgentFromCubeFields = (
  cubeFieldsData,
  existingAgentData,
) =>
  serializeAgentData(
    stripInternalTableRefsFromAgentState(
      getAgentStateFromCubeFields(cubeFieldsData, existingAgentData),
    ),
  );
