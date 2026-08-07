import { v4 as uuidv4 } from "uuid";
import {
  ensureShape,
  collectDomainTopics,
} from "../components/semantic-metadata-editor/semantic-metadata-utils";
import { formatArrayAsCommaSeparated } from "../../hi-cube/cubeSemanticFields";
import { cubeEditorMeasureData, getDefaultFunctionForAggregator } from "../../hi-cube/cubeConstants";
import {
  addBusinessTopicAssignment,
  childMatchesBusinessTopic,
} from "../../hi-cube/agentBusinessTopicMembership";
import {
  getDefaultFormatForSemanticType,
  getDefaultSemanticTypeForRole,
  resolveFormatForSemanticType,
} from "../../hi-cube/cubeSemanticTypeField";

const asId = (value) => (value == null ? "" : String(value));

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

const isLegacyAgentData = (data) =>
  Boolean(
    data?.cube_metadata?.length ||
      data?.business_metrics?.length ||
      data?.metadata_info?.metadata?.domain?.length ||
      data?.metadata_info?.metadata?.description ||
      data?.synonyms?.length ||
      data?.examples?.length ||
      data?.topic_mappings?.length,
  );

const fieldKey = (tableId, columnId, columnName) =>
  `${asId(tableId)}\0${asId(columnId)}\0${columnName || ""}`;

const resolveFieldSortOrder = (item, fallbackOrder) =>
  Number.isFinite(item?.sortOrder) ? item.sortOrder : fallbackOrder;

const toSortValue = (sort) =>
  String((typeof sort === "string" ? sort : sort?.value) || "Ascending").trim();

const toSortState = (sort) => {
  const value = toSortValue(sort);
  return { isSortCheck: value !== "Natural", value };
};

const orderCubeFields = (dimensions = [], measures = []) =>
  [
    ...dimensions.map((item, index) => ({
      kind: "dimension",
      item,
      order: resolveFieldSortOrder(item, index),
    })),
    ...measures.map((item, index) => ({
      kind: "measure",
      item,
      order: resolveFieldSortOrder(item, dimensions.length + index),
    })),
  ].sort((left, right) => left.order - right.order);

const collapseNewlines = (value = "") =>
  String(value || "").replace(/\r?\n+/g, " ");

const readFormulaFromEntry = (entry = {}) =>
  collapseNewlines(entry.formula ?? entry.metric?.formula ?? "");

const buildAiContextForExport = (child = {}) => ({
  aiContext: {
    instructions: collapseNewlines(child.instructions || ""),
    synonyms: collapseNewlines(child.synonyms || ""),
    examples: collapseNewlines(child.example || child.examples || ""),
  },
});

const readAiContextFromEntry = (entry = {}) => {
  const aiContext = entry.aiContext || {};
  const synonyms = aiContext.synonyms ?? entry.synonyms ?? "";
  return {
    instructions: aiContext.instructions ?? entry.instructions ?? "",
    synonyms: Array.isArray(synonyms)
      ? formatArrayAsCommaSeparated(synonyms)
      : synonyms,
    example:
      aiContext.examples ??
      aiContext.example ??
      entry.examples ??
      entry.example ??
      "",
  };
};

const isManualMetricChild = (child) =>
  child?.agentSource?.kind === "manual-metric";

export const getFieldSourceTableName = (record = {}) => {
  if (isManualMetricChild(record)) {
    return "";
  }
  return (
    record.table?.name ||
    record.table?.alias ||
    record.agentSource?.table ||
    record.tableName ||
    ""
  ).trim();
};

export const getFieldSourceTableTooltip = (record = {}) => {
  if (record?.isHierarchy) {
    return "";
  }
  if (isManualMetricChild(record)) {
    return "Metric";
  }
  const qualifiedName = buildQualifiedColumnName(record);
  if (qualifiedName) {
    return qualifiedName;
  }
  const tableName = getFieldSourceTableName(record);
  const columnName = (
    record.columnName ||
    record.fields ||
    record.agentSource?.columnName ||
    ""
  ).trim();
  if (tableName && columnName && !columnName.includes(".")) {
    return `${tableName}.${columnName}`;
  }
  return tableName || columnName || "";
};

const isManualMetricEntry = (measure = {}) =>
  measure?.agentSource?.kind === "manual-metric" ||
  (!String(measure.tableId ?? "").trim() &&
    !String(measure.columnName ?? "").trim());

const buildQualifiedColumnName = (child) => {
  if (isManualMetricChild(child)) {
    return "";
  }
  const columnName = (child.columnName || "").trim();
  if (!columnName) {
    return "";
  }
  const tableName = (
    child.table?.name ||
    child.agentSource?.table ||
    ""
  ).trim();
  if (!tableName || columnName.includes(".")) {
    return columnName;
  }
  return `${tableName}.${columnName}`;
};

const hasBusinessData = (business) =>
  Boolean(business.domain || business.description || business.topic);

const isBusinessEntry = (entry) =>
  Boolean(
    hasBusinessData(entry.business || {}) || entry.domain || entry.topic,
  );

const buildTopicComponent = (child) => {
  const id = asId(child.metricId || child.columnId);
  const name = (child.fields || child.columnName || "").trim();
  if (!id && !name) {
    return null;
  }
  return { id, name };
};

const collectTopicComponents = (children = [], domainName = "", topicName = "") => {
  const domain = (domainName || "").trim();
  const topic = (topicName || "").trim();
  if (!domain || !topic) {
    return [];
  }
  return flattenCubeChildren(children)
    .filter(
      (child) =>
        !child.isDelete && childMatchesBusinessTopic(child, domain, topic),
    )
    .map(buildTopicComponent)
    .filter(Boolean);
};

const buildDomainAndTopics = (businessViewEntries = [], children = []) => {
  const domain = [];
  businessViewEntries.forEach((entry) => {
    const domainName = (entry.domain || "").trim();
    const domainDescription = collapseNewlines(
      entry.businessDescription || "",
    ).trim();
    const topics = (entry.topics || [])
      .map((topic) => {
        const topicName = (topic.name || "").trim();
        const description = collapseNewlines(topic.description || "").trim();
        const components = collectTopicComponents(
          children,
          domainName,
          topicName,
        );
        if (!topicName && !description && !components.length) {
          return null;
        }
        return {
          topic: topicName,
          description,
          ...(components.length ? { components } : {}),
        };
      })
      .filter(Boolean);
    if (domainName || domainDescription || topics.length) {
      domain.push({
        domain_name: domainName,
        description: domainDescription,
        topics,
      });
    }
  });
  return { domain };
};

const applyTopicComponentsToChildren = (children = [], domainItems = []) => {
  const assignable = flattenCubeChildren(children).filter(
    (child) => !child.isDelete,
  );
  const usedKeys = new Set();
  const childLabel = (child) =>
    (child.fields || child.columnName || "").trim();

  const findComponentMatch = (componentId, componentName) => {
    if (componentName) {
      const byName = assignable.find(
        (child) => childLabel(child) === componentName,
      );
      if (byName) {
        return byName;
      }
    }
    if (componentId) {
      return assignable.find(
        (child) =>
          !usedKeys.has(child.key) &&
          asId(child.metricId || child.columnId) === componentId,
      );
    }
    return null;
  };

  (domainItems || []).forEach((domainItem) => {
    const domainName = (domainItem.domain_name || "").trim();
    (domainItem.topics || []).forEach((topicItem) => {
      if (!topicItem || typeof topicItem === "string") {
        return;
      }
      const topicName = (topicItem.topic || topicItem.name || "").trim();
      (topicItem.components || []).forEach((component) => {
        const componentId = asId(component?.id);
        const componentName = String(component?.name || "").trim();
        const match = findComponentMatch(componentId, componentName);
        if (match) {
          usedKeys.add(match.key);
          Object.assign(
            match,
            addBusinessTopicAssignment(match, domainName, topicName),
          );
        }
      });
    });
  });
};

const buildDimensionEntry = (child, sortOrder) => ({
  dimensionName: (child.fields || child.columnName || "").trim(),
  semanticType: child.semanticType || "",
  tableId: asId(child.tableId ?? child.table?.id),
  columnName: buildQualifiedColumnName(child),
  columnId: asId(child.columnId),
  defaultFunction:
    child.defaultFunction || child.column?.defaultFunction || "",
  formatString: child.measure?.Format || "",
  ...buildAiContextForExport(child),
  formula: collapseNewlines(child.formula),
  sort: toSortValue(child.sort),
  sortOrder,
});

const buildAgentLevelEntry = (child) => ({
  levelName: (child.fields || child.columnName || "").trim(),
  semanticType: child.semanticType || "",
  tableId: asId(child.tableId ?? child.table?.id),
  columnName: buildQualifiedColumnName(child),
  columnId: asId(child.columnId),
  defaultFunction: child.defaultFunction || child.column?.defaultFunction || "",
  formatString: child.measure?.Format || "",
  formula: collapseNewlines(child.formula),
  sort: toSortValue(child.sort),
});

const buildAgentHierarchyDimension = (child, sortOrder) => {
  const levels = (child.children || [])
    .filter((level) => !level.isDelete)
    .map(buildAgentLevelEntry);
  const primaryLevel = levels[0] || buildAgentLevelEntry(child);
  const hierarchyName = (child.fields || "").trim();
  const tableId = asId(child.tableId ?? child.table?.id) || primaryLevel.tableId;
  const columnId = asId(child.columnId) || primaryLevel.columnId;
  const columnName = buildQualifiedColumnName(child) || primaryLevel.columnName;

  return {
    dimensionName: hierarchyName,
    semanticType: child.semanticType || "",
    tableId,
    columnName,
    columnId,
    defaultFunction:
      child.defaultFunction ||
      child.column?.defaultFunction ||
      primaryLevel.defaultFunction ||
      "",
    formatString: child.measure?.Format || "",
    formula: collapseNewlines(child.formula),
    sort: toSortValue(child.sort),
    sortOrder,
    hierarchies: [
      {
        hierarchyName,
        primaryColumnId: columnId,
        tableId,
        columnName,
        levels,
      },
    ],
  };
};

const buildMeasureEntry = (child, sortOrder) => {
  const aggregationValue = child.aggregation?.value || "";
  const isNoneAggregation =
    !child.aggregation?.isAggregationCheck ||
    aggregationValue === "None" ||
    aggregationValue === "";
  const isManualMetric = isManualMetricChild(child);
  return {
    measureName: (child.fields || child.columnName || "").trim(),
    aggregator: isNoneAggregation ? "None" : aggregationValue,
    ...(isManualMetric
      ? { metricId: asId(child.metricId || child.columnId) }
      : { columnId: asId(child.columnId) }),
    tableId: asId(child.tableId ?? child.table?.id),
    defaultFunction:
      isManualMetric || isNoneAggregation
        ? ""
        : child.defaultFunction || child.column?.defaultFunction || "",
    formatString: child.measure?.Format || "",
    semanticType: child.semanticType || "",
    columnName: buildQualifiedColumnName(child),
    ...buildAiContextForExport(child),
    formula: collapseNewlines(child.formula),
    sortOrder,
  };
};

const buildLevelEntry = (child) => ({
  levelName: (child.fields || child.columnName || "").trim(),
  semanticType: child.semanticType || "",
  tableId: asId(child.tableId ?? child.table?.id),
  columnName: buildQualifiedColumnName(child),
  columnId: asId(child.columnId),
  defaultFunction:
    child.defaultFunction || child.column?.defaultFunction || "",
  formatString: child.measure?.Format || "",
  ...buildAiContextForExport(child),
  formula: collapseNewlines(child.formula),
  sort: toSortValue(child.sort),
});

const buildHierarchyDimensionEntry = (hierarchyChild, sortOrder) => {
  const levelChildren = (hierarchyChild.children || []).filter(
    (child) => !child.isDelete,
  );
  const primary = levelChildren[0] || hierarchyChild;
  const hierarchyColumnId =
    asId(hierarchyChild.columnId) || asId(primary.columnId);
  const tableId =
    asId(hierarchyChild.tableId ?? hierarchyChild.table?.id) ||
    asId(primary.tableId ?? primary.table?.id);
  const columnName =
    buildQualifiedColumnName(hierarchyChild) || buildQualifiedColumnName(primary);
  return {
    dimensionName: (hierarchyChild.fields || primary.fields || "").trim(),
    semanticType: hierarchyChild.semanticType || "",
    tableId,
    columnName,
    columnId: hierarchyColumnId,
    defaultFunction:
      hierarchyChild.defaultFunction ||
      hierarchyChild.column?.defaultFunction ||
      primary.defaultFunction ||
      primary.column?.defaultFunction ||
      "",
    formatString: hierarchyChild.measure?.Format || "",
    ...buildAiContextForExport(hierarchyChild),
    formula: collapseNewlines(hierarchyChild.formula),
    sort: toSortValue(hierarchyChild.sort),
    hierarchies: [
      {
        hierarchyName: (hierarchyChild.fields || "").trim(),
        primaryColumnId: hierarchyColumnId,
        tableId,
        columnName,
        levels: levelChildren.map(buildLevelEntry),
      },
    ],
    sortOrder,
  };
};

const createChildFromLevel = (level, parentKey) => {
  const columnName = level.columnName || level.levelName || "";
  return {
    key: uuidv4(),
    fieldsDropdownOpen: false,
    isHierarchyChild: true,
    parentKey,
    fields: level.levelName || columnName,
    columnName,
    columnId: level.columnId || "",
    tableId: level.tableId || "",
    defaultFunction: level.defaultFunction || "",
    semanticType: level.semanticType || "",
    domain: level.business?.domain ?? level.domain ?? "",
    businessDescription: level.business?.description ?? "",
    isBusinessView: isBusinessEntry(level),
    formula: readFormulaFromEntry(level),
    filter: "",
    ...readAiContextFromEntry(level),
    topic: level.business?.topic ?? level.topic ?? "",
    isDimensionCheck: true,
    measure: {
      isMeasureCheck: false,
      DataType: "",
      Format: level.formatString || "",
    },
    isVisible: true,
    sort: toSortState(level.sort),
    aggregation: { isAggregationCheck: false, value: "" },
    isPartitionCheck: false,
    table: { name: "" },
    column: { defaultFunction: level.defaultFunction || "" },
    agentSource: { kind: "column", table: "", columnName },
  };
};

const createHierarchyChildFromDimension = (dimension) => {
  const hierarchy = dimension.hierarchies?.[0] || {};
  const levels = hierarchy.levels || [];
  const key = uuidv4();
  return {
    key,
    fieldsDropdownOpen: false,
    isHierarchy: true,
    fields: hierarchy.hierarchyName || dimension.dimensionName || "",
    columnId: hierarchy.primaryColumnId || dimension.columnId || "",
    tableId: hierarchy.tableId || dimension.tableId || "",
    columnName: hierarchy.columnName || dimension.columnName || "",
    table: { name: "" },
    column: { defaultFunction: dimension.defaultFunction || "" },
    agentSource: {
      kind: "column",
      table: "",
      columnName: hierarchy.columnName || dimension.columnName || "",
    },
    children: levels.map((level) => createChildFromLevel(level, key)),
  };
};

const isFlatDimension = (dimension) => {
  const hierarchy = dimension.hierarchies?.[0];
  const levels = hierarchy?.levels || [];
  if (!hierarchy || levels.length === 0) {
    return true;
  }
  return (
    levels.length === 1 &&
    (hierarchy.hierarchyName || "") === (levels[0].levelName || "") &&
    asId(hierarchy.primaryColumnId) === asId(levels[0].columnId)
  );
};

const createChildFromDimension = (dimension) => {
  const columnName = dimension.columnName || dimension.dimensionName || "";
  return {
    key: uuidv4(),
    fields: dimension.dimensionName || columnName,
    columnName,
    tableId: dimension.tableId || "",
    columnId: dimension.columnId || "",
    defaultFunction: dimension.defaultFunction || "",
    semanticType: dimension.semanticType || "",
    domain: dimension.business?.domain ?? dimension.domain ?? "",
    topic: dimension.business?.topic ?? dimension.topic ?? "",
    businessDescription: dimension.business?.description ?? "",
    isBusinessView: isBusinessEntry(dimension),
    formula: readFormulaFromEntry(dimension),
    filter: "",
    ...readAiContextFromEntry(dimension),
    fieldsDropdownOpen: false,
    isDimensionCheck: true,
    measure: {
      isMeasureCheck: false,
      DataType: "",
      Format: dimension.formatString || "",
    },
    isVisible: true,
    sort: toSortState(dimension.sort),
    aggregation: { isAggregationCheck: false, value: "" },
    isPartitionCheck: false,
    table: { name: "" },
    column: { defaultFunction: dimension.defaultFunction || "" },
    agentSource: { kind: "column", table: "", columnName },
  };
};

const createHierarchyChildFromLevel = (level, parentKey) => {
  const columnName = level.columnName || level.levelName || "";
  return {
    key: uuidv4(),
    fields: level.levelName || columnName,
    columnName,
    tableId: level.tableId || "",
    columnId: level.columnId || "",
    defaultFunction: level.defaultFunction || "",
    semanticType: level.semanticType || "",
    formula: readFormulaFromEntry(level),
    filter: "",
    example: "",
    synonyms: "",
    fieldsDropdownOpen: false,
    isHierarchyChild: true,
    parentKey,
    isDimensionCheck: true,
    measure: {
      isMeasureCheck: false,
      DataType: "",
      Format: level.formatString || "",
    },
    isVisible: true,
    sort: toSortState(level.sort),
    aggregation: { isAggregationCheck: false, value: "" },
    isPartitionCheck: false,
    table: { name: "" },
    column: { defaultFunction: level.defaultFunction || "" },
    agentSource: { kind: "column", table: "", columnName },
  };
};

const isSingleLevelHierarchyDimension = (dimension, hierarchy, levels) => {
  if (levels.length !== 1) {
    return false;
  }
  const hierarchyName = (hierarchy?.hierarchyName || dimension.dimensionName || "").trim();
  const level = levels[0];
  return (
    hierarchyName === (level.levelName || "").trim() &&
    asId(hierarchy?.primaryColumnId || dimension.columnId) === asId(level.columnId)
  );
};

const createChildFromAgentDimension = (dimension) => {
  const hierarchy = dimension.hierarchies?.[0];
  const levels = hierarchy?.levels || [];

  if (!levels.length) {
    return createChildFromDimension(dimension);
  }

  if (isSingleLevelHierarchyDimension(dimension, hierarchy, levels)) {
    const level = levels[0];
    return createChildFromDimension({
      ...dimension,
      dimensionName: level.levelName || dimension.dimensionName,
      columnId: level.columnId || dimension.columnId,
      tableId: level.tableId || dimension.tableId,
      columnName: level.columnName || dimension.columnName,
      semanticType: level.semanticType ?? dimension.semanticType,
      defaultFunction: level.defaultFunction ?? dimension.defaultFunction,
      formula: readFormulaFromEntry(level) || readFormulaFromEntry(dimension),
      sort: level.sort ?? dimension.sort,
    });
  }

  const key = uuidv4();
  const hierarchyName = (hierarchy.hierarchyName || dimension.dimensionName || "").trim();
  return {
    key,
    fields: hierarchyName,
    columnName: hierarchy.columnName || dimension.columnName || "",
    tableId: hierarchy.tableId || dimension.tableId || "",
    columnId: hierarchy.primaryColumnId || dimension.columnId || "",
    defaultFunction: dimension.defaultFunction || "",
    semanticType: dimension.semanticType || "",
    formula: readFormulaFromEntry(dimension),
    filter: "",
    example: "",
    synonyms: "",
    fieldsDropdownOpen: false,
    isHierarchy: true,
    children: levels.map((level) => createHierarchyChildFromLevel(level, key)),
    table: { name: "" },
    column: { defaultFunction: dimension.defaultFunction || "" },
  };
};

const createChildFromMeasure = (measure) => {
  const isManualMetric = isManualMetricEntry(measure);
  const columnName = isManualMetric
    ? ""
    : measure.columnName || measure.measureName || "";
  const aggregator = measure.aggregator || "Sum";
  const isNoneAggregation = aggregator === "None" || aggregator === "";
  const defaultFunction = getDefaultFunctionForAggregator(
    aggregator,
    isManualMetric,
  );
  return {
    key: uuidv4(),
    fields: measure.measureName || columnName,
    columnName,
    tableId: measure.tableId || "",
    columnId: isManualMetric ? "" : measure.columnId || "",
    metricId: isManualMetric ? measure.metricId || measure.columnId || "" : "",
    defaultFunction,
    semanticType: measure.semanticType || "",
    domain: measure.business?.domain ?? measure.domain ?? "",
    topic: measure.business?.topic ?? measure.topic ?? "",
    businessDescription: measure.business?.description ?? "",
    isBusinessView: isBusinessEntry(measure),
    formula: readFormulaFromEntry(measure),
    filter: "",
    ...readAiContextFromEntry(measure),
    fieldsDropdownOpen: false,
    isDimensionCheck: false,
    measure: {
      isMeasureCheck: true,
      DataType: cubeEditorMeasureData[0].dataType,
      Format: measure.formatString || "",
    },
    isVisible: true,
    sort: { isSortCheck: false, value: "" },
    aggregation: {
      isAggregationCheck: !isNoneAggregation,
      value: isNoneAggregation ? "None" : aggregator,
    },
    isPartitionCheck: false,
    table: { name: "" },
    column: { defaultFunction },
    agentSource: isManualMetric
      ? { kind: "manual-metric", metricName: measure.measureName || "" }
      : { kind: "metric", metricName: measure.measureName || columnName },
  };
};

export const getNextManualMetricName = (children = []) => {
  const used = new Set(
    flattenCubeChildren(children)
      .map((child) => String(child.fields || "").trim())
      .filter(Boolean),
  );
  let index = 1;
  while (used.has(`metric_${index}`)) {
    index += 1;
  }
  return `metric_${index}`;
};

export const createColumnChildFromDropRecord = (
  record = {},
  semanticTypeOptions,
) => {
  const isNumeric = record.dataType === "numeric";
  const isDimension = !isNumeric;
  const semanticType = getDefaultSemanticTypeForRole(
    isNumeric,
    "agent",
    semanticTypeOptions,
  );
  const defaultFormat =
    getDefaultFormatForSemanticType(semanticTypeOptions, semanticType) ||
    (isNumeric ? cubeEditorMeasureData[0].format[0] : "");
  return {
    key: uuidv4(),
    columnId: record.columnId,
    fields: record.alias,
    tableId: record.tableId,
    fieldsDropdownOpen: false,
    isDimensionCheck: isDimension,
    measure: {
      isMeasureCheck: isNumeric,
      DataType: isNumeric ? cubeEditorMeasureData[0].dataType : "",
      Format: defaultFormat,
    },
    isVisible: true,
    sort: {
      isSortCheck: false,
      value: isDimension ? "Natural" : "",
    },
    aggregation: {
      isAggregationCheck: isNumeric,
      value: isNumeric ? "Sum" : "",
    },
    isPartitionCheck: false,
    table: {
      name: record.tableName,
      alias: record.tableAlias,
      ...record.dataSource,
    },
    column: {
      defaultFunction: record.defaultFunction,
    },
    columnName: record.columnName,
    dataType: record.dataType,
    type: record.type,
    agentSource: {
      kind: "column",
      table: record.tableName,
      columnName: record.columnName,
    },
    semanticType,
    synonyms: "",
    domain: "",
    topic: "",
    formula: "",
    filter: "",
    example: "",
    instructions: "",
    businessDescription: "",
  };
};

export const createManualMetricChild = (children = [], semanticTypeOptions) => {
  const metricName = getNextManualMetricName(children);
  const semanticType = getDefaultSemanticTypeForRole(
    true,
    "agent",
    semanticTypeOptions,
  );
  const defaultFormat =
    getDefaultFormatForSemanticType(semanticTypeOptions, semanticType) ||
    cubeEditorMeasureData[0].format[0];
  return {
    key: uuidv4(),
    fields: metricName,
    columnName: "",
    tableId: "",
    columnId: "",
    metricId: uuidv4(),
    defaultFunction: "",
    semanticType,
    domain: "",
    businessDescription: "",
    formula: "",
    filter: "",
    example: "",
    synonyms: "",
    instructions: "",
    topic: "",
    fieldsDropdownOpen: false,
    isDimensionCheck: false,
    measure: {
      isMeasureCheck: true,
      DataType: cubeEditorMeasureData[0].dataType,
      Format: defaultFormat,
    },
    isVisible: true,
    sort: { isSortCheck: false, value: "" },
    aggregation: {
      isAggregationCheck: true,
      value: "Sum",
    },
    isPartitionCheck: false,
    table: { name: "" },
    column: { defaultFunction: "" },
    agentSource: { kind: "manual-metric", metricName },
  };
};

const resolveAgentTopicFromData = (data) =>
  formatArrayAsCommaSeparated(Array.from(collectDomainTopics(data)));

const resolveAgentDescriptionFromData = (data) => {
  const fromDomain = (data.domain || []).find((entry) =>
    String(entry.description ?? "").trim(),
  );
  if (fromDomain) {
    return String(fromDomain.description).trim();
  }
  return String(data.metadata_info?.metadata?.description ?? "").trim();
};

const resolveAgentDomainNamesFromData = (data) => {
  const names = (data.domain || [])
    .map((entry) => entry.domain_name)
    .filter(Boolean);
  if (names.length) {
    return formatArrayAsCommaSeparated(names);
  }
  return formatArrayAsCommaSeparated(
    data.metadata_info?.metadata?.domain || [],
  );
};

const migrateLegacyAgentData = (data) => {
  const domainFromEntries = (data.domain || [])
    .map((entry) => entry.domain_name)
    .filter(Boolean);
  const domainNames = domainFromEntries.length
    ? domainFromEntries
    : data.metadata_info?.metadata?.domain || [];
  const description = resolveAgentDescriptionFromData(data);
  const topics = Array.from(collectDomainTopics(data));
  if (!topics.length) {
    (data.cube_metadata || []).forEach((table) => {
      const dimensionNames = Array.isArray(table.dimension_name)
        ? table.dimension_name
        : [table.dimension_name];
      dimensionNames.forEach((topic) => {
        if (topic) {
          topics.push(String(topic).trim());
        }
      });
    });
  }

  const dimensions = [];
  const measures = [];
  const usedMetrics = new Set();

  (data.cube_metadata || []).forEach((table) => {
    (table.columns || []).forEach((column) => {
      const metric = (data.business_metrics || []).find(
        (item) => item.metric === column.column_name,
      );
      if (metric) {
        usedMetrics.add(metric.metric);
      }
      const child = {
        fields: column.column_name || "",
        columnName: column.column_name || "",
        tableId: table.table_id || table.tableId || "",
        columnId: column.column_id || column.columnId || "",
        semanticType: column.semantic_type || column.semanticType || "",
        formula: metric?.formula || "",
        measure: { isMeasureCheck: false, Format: "" },
        aggregation: { value: "" },
      };
      dimensions.push(
        buildDimensionEntry(child, dimensions.length + measures.length),
      );
    });
  });

  (data.business_metrics || []).forEach((metric) => {
    if (usedMetrics.has(metric.metric)) {
      return;
    }
    const child = {
      fields: metric.metric || "",
      columnName: metric.metric || "",
      tableId: "",
      columnId: "",
      semanticType: "",
      formula: metric.formula || "",
      measure: { isMeasureCheck: true, Format: "" },
      aggregation: { value: "Sum" },
    };
    measures.push(
      buildMeasureEntry(child, dimensions.length + measures.length),
    );
  });

  const domain =
    domainNames.length > 0
      ? domainNames.map((domain_name) => ({
          domain_name,
          description,
          topics: [...new Set(topics)],
        }))
      : description || topics.length
        ? [{ domain_name: "", description, topics: [...new Set(topics)] }]
        : [];

  return ensureShape({
    domain,
    cube: [
      {
        cubeName: data.cubeName || "",
        dimensions,
        measures,
      },
    ],
  });
};

const hasCubeInfoContent = (data) =>
  (data?.cube || data?.cube_info || []).some(
    (cube) => (cube?.dimensions?.length || 0) + (cube?.measures?.length || 0) > 0,
  );

const parseRawAgentData = (agentData) => {
  if (!agentData) {
    return {};
  }
  if (typeof agentData === "string") {
    try {
      return JSON.parse(agentData);
    } catch {
      return {};
    }
  }
  return agentData;
};

export const normalizeAgentData = (agentData) => {
  const raw = parseRawAgentData(agentData);
  if (!raw || typeof raw !== "object") {
    return ensureShape({});
  }
  if (isLegacyAgentData(raw) && !hasCubeInfoContent(raw)) {
    return ensureShape(migrateLegacyAgentData(raw));
  }
  return ensureShape(raw);
};

const findMatchingFieldEntry = (list = [], entry = {}, nameKey) =>
  list.find(
    (item) =>
      (entry.columnName && item.columnName === entry.columnName) ||
      (entry[nameKey] && item[nameKey] === entry[nameKey]) ||
      (entry.columnId && asId(item.columnId) === asId(entry.columnId)) ||
      (entry.metricId && asId(item.metricId) === asId(entry.metricId)),
  );

const resolveEntryFormat = (entry, previousEntry, semanticTypeOptions) => {
  if (!semanticTypeOptions?.length) {
    return entry;
  }
  const semanticType = entry.semanticType || "";
  const typeChanged =
    previousEntry && semanticType !== (previousEntry.semanticType || "");
  return {
    ...entry,
    formatString: typeChanged
      ? getDefaultFormatForSemanticType(semanticTypeOptions, semanticType)
      : resolveFormatForSemanticType(
          semanticTypeOptions,
          semanticType,
          entry.formatString || "",
        ),
  };
};

const resolveMeasureAggregator = (measure = {}) => {
  const aggregator = measure.aggregator || "Sum";
  return {
    ...measure,
    aggregator,
    defaultFunction: getDefaultFunctionForAggregator(
      aggregator,
      isManualMetricEntry(measure),
    ),
  };
};

export const normalizeAgentFieldFormats = (
  agentData,
  semanticTypeOptions,
  previousAgentData,
) => {
  const data = normalizeAgentData(agentData);
  const prevCube = normalizeAgentData(previousAgentData || {}).cube?.[0] || {};
  const prevDimensions = prevCube.dimensions || [];
  const prevMeasures = prevCube.measures || [];
  const normalizeDimension = (dimension) => {
    const previous = findMatchingFieldEntry(
      prevDimensions,
      dimension,
      "dimensionName",
    );
    const next = resolveEntryFormat(dimension, previous, semanticTypeOptions);
    if (!next.hierarchies?.length) {
      return next;
    }
    const prevLevels = previous?.hierarchies?.[0]?.levels || [];
    return {
      ...next,
      hierarchies: next.hierarchies.map((hierarchy) => ({
        ...hierarchy,
        levels: (hierarchy.levels || []).map((level) =>
          resolveEntryFormat(
            level,
            findMatchingFieldEntry(prevLevels, level, "levelName"),
            semanticTypeOptions,
          ),
        ),
      })),
    };
  };
  return {
    ...data,
    cube: (data.cube || []).map((cube) => ({
      ...cube,
      dimensions: (cube.dimensions || []).map(normalizeDimension),
      measures: (cube.measures || []).map((measure) =>
        resolveMeasureAggregator(
          resolveEntryFormat(
            measure,
            findMatchingFieldEntry(prevMeasures, measure, "measureName"),
            semanticTypeOptions,
          ),
        ),
      ),
    })),
  };
};

const mergeLevelList = (storedLevels = [], displayLevels = []) => {
  const storedByKey = new Map(
    storedLevels.map((item) => [
      fieldKey(item.tableId, item.columnId, item.columnName),
      item,
    ]),
  );

  return displayLevels.map((item) => {
    const key = fieldKey(item.tableId, item.columnId, item.columnName);
    const previous = storedByKey.get(key) || {};
    const { metric: _previousMetric, ...previousRest } = previous;
    const { metric: _itemMetric, ...itemRest } = item;
    return {
      ...previousRest,
      ...itemRest,
      levelName: item.levelName || previous.levelName || "",
      columnName: item.columnName || previous.columnName || "",
      tableId: item.tableId || previous.tableId || "",
      columnId: item.columnId || previous.columnId || "",
      defaultFunction: item.defaultFunction || previous.defaultFunction || "",
      formula: readFormulaFromEntry(item) || readFormulaFromEntry(previous),
      sortOrder: item.sortOrder ?? previous.sortOrder,
    };
  });
};

const mergeDimensionHierarchies = (stored = [], display = []) => {
  const storedHierarchy = stored[0] || {};
  const displayHierarchy = display[0] || {};
  return [
    {
      ...storedHierarchy,
      ...displayHierarchy,
      hierarchyName:
        displayHierarchy.hierarchyName || storedHierarchy.hierarchyName || "",
      primaryColumnId:
        displayHierarchy.primaryColumnId || storedHierarchy.primaryColumnId || "",
      tableId: displayHierarchy.tableId || storedHierarchy.tableId || "",
      columnName:
        displayHierarchy.columnName || storedHierarchy.columnName || "",
      levels: mergeLevelList(
        storedHierarchy.levels || [],
        displayHierarchy.levels || [],
      ),
    },
  ];
};

const mergeFieldList = (stored = [], display = [], kind) => {
  const storedByKey = new Map(
    stored.map((item) => [
      fieldKey(item.tableId, item.metricId || item.columnId, item.columnName),
      item,
    ]),
  );

  return display.map((item) => {
    const key = fieldKey(
      item.tableId,
      item.metricId || item.columnId,
      item.columnName,
    );
    const previous = storedByKey.get(key) || {};
    const { metric: _previousMetric, ...previousRest } = previous;
    const { metric: _itemMetric, ...itemRest } = item;
    if (kind === "dimension") {
      const merged = {
        ...previousRest,
        ...itemRest,
        dimensionName: item.dimensionName || previous.dimensionName || "",
        columnName: item.columnName || previous.columnName || "",
        tableId: item.tableId || previous.tableId || "",
        columnId: item.columnId || previous.columnId || "",
        defaultFunction:
          item.defaultFunction || previous.defaultFunction || "",
        formula: readFormulaFromEntry(item) || readFormulaFromEntry(previous),
        sortOrder: item.sortOrder ?? previous.sortOrder,
      };
      if (item.hierarchies?.length || previous.hierarchies?.length) {
        merged.hierarchies = mergeDimensionHierarchies(
          previous.hierarchies,
          item.hierarchies,
        );
      }
      return merged;
    }
    return {
      ...previousRest,
      ...itemRest,
      measureName: item.measureName || previous.measureName || "",
      columnName: item.columnName || previous.columnName || "",
      tableId: item.tableId || previous.tableId || "",
      columnId: item.columnId || previous.columnId || "",
      defaultFunction: item.defaultFunction || previous.defaultFunction || "",
      formula: readFormulaFromEntry(item) || readFormulaFromEntry(previous),
      sortOrder: item.sortOrder ?? previous.sortOrder,
    };
  });
};

const registerDimensionInHierarchyData = (child, hierarchyData) => {
  hierarchyData.hierarchyList.push({
    hierarchyName: child.fields,
    hierarchyKey: child.key,
  });
  hierarchyData.isHierarchyPresent = true;
};

export const convertAgentDataToCubeFieldsData = (agentData) => {
  const data = normalizeAgentData(agentData);
  const cubeEntry = data.cube[0] || {
    cubeName: "",
    dimensions: [],
    measures: [],
  };
  const hierarchyList = [];
  const children = orderCubeFields(
    cubeEntry.dimensions || [],
    cubeEntry.measures || [],
  ).map(({ kind, item }) => {
    if (kind === "measure") {
      return createChildFromMeasure(item);
    }
    if (isFlatDimension(item)) {
      const dimensionChild = createChildFromDimension(item);
      hierarchyList.push({
        hierarchyName: dimensionChild.fields,
        hierarchyKey: dimensionChild.key,
      });
      return dimensionChild;
    }
    const hierarchyChild = createHierarchyChildFromDimension(item);
    hierarchyList.push({
      hierarchyName: hierarchyChild.fields,
      hierarchyKey: hierarchyChild.key,
    });
    return hierarchyChild;
  });

  const businessViewEntries = (data.domain || []).map((domainItem) => {
    const topics = (domainItem.topics || []).map((topicItem) => {
      const components = Array.isArray(topicItem?.components)
        ? topicItem.components
            .map((component) => ({
              id: asId(component?.id),
              name: String(component?.name || "").trim(),
            }))
            .filter((component) => component.id || component.name)
        : [];
      return {
        key: uuidv4(),
        name:
          typeof topicItem === "string"
            ? topicItem
            : topicItem.topic || topicItem.name || "",
        description:
          typeof topicItem === "string" ? "" : topicItem.description || "",
        ...(components.length ? { components } : {}),
      };
    });
    return {
      key: uuidv4(),
      domain: domainItem.domain_name || "",
      businessDescription: domainItem.description || "",
      topics: topics.length
        ? topics
        : [{ key: uuidv4(), name: "", description: "" }],
    };
  });

  applyTopicComponentsToChildren(children, data.domain || []);

  return {
    id: "",
    domainName: resolveAgentDomainNamesFromData(data),
    cubeDescription: resolveAgentDescriptionFromData(data),
    cubeTopic: resolveAgentTopicFromData(data),
    cubeName: "",
    children,
    businessViewEntries,
    hierarchyData: {
      isHierarchyPresent: hierarchyList.length > 0,
      hierarchyList,
    },
  };
};

export const convertCubeFieldsDataToAgentData = (
  cubeFieldsData,
  existingAgentData,
) => {
  const dimensions = [];
  const measures = [];
  let sortOrder = 0;

  (cubeFieldsData.children || []).forEach((child) => {
    if (child.isDelete) {
      return;
    }
    if (child.isHierarchy) {
      dimensions.push(buildHierarchyDimensionEntry(child, sortOrder));
      sortOrder += 1;
      return;
    }
    if (child.measure?.isMeasureCheck) {
      measures.push(buildMeasureEntry(child, sortOrder));
      sortOrder += 1;
      return;
    }
    if (child.isDimensionCheck) {
      dimensions.push(buildDimensionEntry(child, sortOrder));
    }
    sortOrder += 1;
  });

  const { domain } = buildDomainAndTopics(
    cubeFieldsData.businessViewEntries,
    cubeFieldsData.children,
  );

  return normalizeAgentData({
    domain,
    cube: [
      {
        cubeName: "",
        dimensions,
        measures,
      },
    ],
  });
};

export const getAgentStateFromCubeFields = (
  cubeFieldsData,
  existingAgentData,
) =>
  convertCubeFieldsDataToAgentData(
    cubeFieldsData,
    existingAgentData || ensureShape({}),
  );

const stripFieldSortOrder = (item = {}) => {
  const { sortOrder, ...rest } = item;
  return rest;
};

const groupFieldAiContext = (item = {}) => {
  const {
    instructions,
    synonyms,
    examples,
    example,
    aiContext = {},
    ...rest
  } = item;
  return {
    ...rest,
    aiContext: {
      instructions: aiContext.instructions ?? instructions ?? "",
      synonyms: aiContext.synonyms ?? synonyms ?? "",
      examples:
        aiContext.examples ??
        aiContext.example ??
        examples ??
        example ??
        "",
    },
  };
};
const groupDimensionAiContext = (dimension = {}) => {
  const grouped = groupFieldAiContext(dimension);
  if (!Array.isArray(grouped.hierarchies)) {
    return grouped;
  }
  return {
    ...grouped,
    hierarchies: grouped.hierarchies.map((hierarchy) => ({
      ...hierarchy,
      levels: (hierarchy.levels || []).map(groupFieldAiContext),
    })),
  };
};

const groupMeasureAiContext = (measure = {}) => {
  const grouped = groupFieldAiContext(measure);
  if (!isManualMetricEntry(grouped)) {
    return grouped;
  }
  const { columnId, ...rest } = grouped;
  return {
    ...rest,
    metricId: grouped.metricId || columnId || "",
  };
};

const stripSortOrderFromDimension = (dimension = {}) => {
  const { sortOrder, hierarchies, ...rest } = dimension;
  if (!Array.isArray(hierarchies) || !hierarchies.length) {
    return rest;
  }
  return {
    ...rest,
    hierarchies: hierarchies.map((hierarchy) => ({
      ...hierarchy,
      levels: (hierarchy.levels || []).map(stripFieldSortOrder),
    })),
  };
};

const stripSortOrderFromCubeInfo = (cube = {}) => {
  const { cubeName, ...rest } = cube;
  return {
    ...rest,
    dimensions: (cube.dimensions || []).map(stripSortOrderFromDimension),
    measures: (cube.measures || []).map(stripFieldSortOrder),
  };
};

export const toPublicAgentData = (agentData) => {
  const data = normalizeAgentData(agentData);
  return {
    cube: (data.cube || []).map((cube) => {
      const { cubeName, ...rest } = cube;
      return {
        ...rest,
        dimensions: (cube.dimensions || []).map(groupDimensionAiContext),
        measures: (cube.measures || []).map(groupMeasureAiContext),
      };
    }),
    domain: data.domain || [],
  };
};

export const toDisplayAgentData = (agentData) => {
  const data = toPublicAgentData(agentData);
  return {
    ...data,
    cube: (data.cube || []).map(stripSortOrderFromCubeInfo),
  };
};

const hasStoredOnlyIds = (agentData) => {
  const data = normalizeAgentData(agentData);
  return (data.cube || []).some((cube) =>
    [...(cube.dimensions || []), ...(cube.measures || [])].some(
      (item) => item.tableId || item.columnId || item.metricId,
    ),
  );
};

export const stripAgentDataForDisplay = (agentData) =>
  toDisplayAgentData(agentData);

export const mergeDisplayAgentDataWithStored = (
  storedAgentData,
  displayAgentData,
) => {
  const stored = normalizeAgentData(storedAgentData);
  const display = normalizeAgentData(displayAgentData);
  const storedCube = stored.cube[0] || {
    cubeName: "",
    dimensions: [],
    measures: [],
  };
  const displayCube = display.cube[0] || {
    cubeName: "",
    dimensions: [],
    measures: [],
  };

  return normalizeAgentData({
    domain: display.domain?.length ? display.domain : stored.domain,
    cube: [
      {
        cubeName: "",
        dimensions: mergeFieldList(
          storedCube.dimensions,
          displayCube.dimensions,
          "dimension",
        ),
        measures: mergeFieldList(
          storedCube.measures,
          displayCube.measures,
          "measure",
        ),
      },
    ],
  });
};

export const resolveAgentDataFromInput = (storedAgentData, inputAgentData) => {
  const parsed = normalizeAgentData(inputAgentData);
  return hasStoredOnlyIds(storedAgentData) &&
    !hasStoredOnlyIds(parsed) &&
    (parsed.cube?.[0]?.dimensions?.length ||
      parsed.cube?.[0]?.measures?.length)
    ? mergeDisplayAgentDataWithStored(storedAgentData, parsed)
    : parsed;
};

export const agentDataHasStoredOnlyFields = hasStoredOnlyIds;

export const serializeAgentDataForDisplay = (agentData) =>
  JSON.stringify(toDisplayAgentData(agentData), null, 2);

export const serializeAgentData = (agentData) =>
  JSON.stringify(toPublicAgentData(agentData), null, 2);

export const stripInternalTableRefsFromAgentState = (agentData) =>
  toPublicAgentData(agentData);

export const getSerializedAgentFromCubeFields = (
  cubeFieldsData,
  existingAgentData,
) =>
  serializeAgentData(
    getAgentStateFromCubeFields(cubeFieldsData, existingAgentData),
  );
