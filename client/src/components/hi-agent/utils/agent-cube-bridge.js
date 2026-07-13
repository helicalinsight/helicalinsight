import { v4 as uuidv4 } from "uuid";
import {
  ensureShape,
  collectDomainTopics,
} from "../components/semantic-metadata-editor/semantic-metadata-utils";
import {
  formatArrayAsCommaSeparated,
  parseCommaSeparatedToArray,
} from "../../hi-cube/cubeSemanticFields";
import { cubeEditorMeasureData } from "../../hi-cube/cubeConstants";

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

const buildMetric = (formula = "") => ({ formula: formula || "" });

const isManualMetricChild = (child) =>
  child?.agentSource?.kind === "manual-metric";

const isManualMetricEntry = (measure = {}) =>
  !String(measure.tableId ?? "").trim() &&
  !String(measure.columnId ?? "").trim() &&
  !String(measure.columnName ?? "").trim();

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

const buildDimensionEntry = (child, sortOrder) => ({
  dimensionName: (child.fields || child.columnName || "").trim(),
  semanticType: child.semanticType || "",
  tableId: asId(child.tableId ?? child.table?.id),
  columnName: buildQualifiedColumnName(child),
  columnId: asId(child.columnId),
  defaultFunction: child.defaultFunction || child.column?.defaultFunction || "",
  description: child.description || "",
  metric: buildMetric(child.formula),
  sortOrder,
});

const buildAgentLevelEntry = (child) => ({
  levelName: (child.fields || child.columnName || "").trim(),
  semanticType: child.semanticType || "",
  tableId: asId(child.tableId ?? child.table?.id),
  columnName: buildQualifiedColumnName(child),
  columnId: asId(child.columnId),
  defaultFunction: child.defaultFunction || child.column?.defaultFunction || "",
  description: child.description || "",
  metric: buildMetric(child.formula),
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
    description: child.description || "",
    metric: buildMetric(child.formula),
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

const buildMeasureEntry = (child, sortOrder) => ({
  measureName: (child.fields || child.columnName || "").trim(),
  aggregator: child.aggregation?.value || "",
  columnId: asId(child.columnId),
  tableId: asId(child.tableId ?? child.table?.id),
  defaultFunction: child.defaultFunction || child.column?.defaultFunction || "",
  formatString: child.measure?.Format || "",
  semanticType: child.semanticType || "",
  columnName: buildQualifiedColumnName(child),
  description: child.description || "",
  metric: buildMetric(child.formula),
  sortOrder,
});

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
    description: dimension.description || "",
    formula: dimension.metric?.formula || "",
    filter: "",
    example: "",
    synonyms: "",
    fieldsDropdownOpen: false,
    isDimensionCheck: true,
    measure: { isMeasureCheck: false, DataType: "", Format: "" },
    isVisible: true,
    sort: { isSortCheck: true, value: "Ascending" },
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
    description: level.description || "",
    formula: level.metric?.formula || "",
    filter: "",
    example: "",
    synonyms: "",
    fieldsDropdownOpen: false,
    isHierarchyChild: true,
    parentKey,
    isDimensionCheck: true,
    measure: { isMeasureCheck: false, DataType: "", Format: "" },
    isVisible: true,
    sort: { isSortCheck: true, value: "Ascending" },
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
      description: level.description ?? dimension.description,
      defaultFunction: level.defaultFunction ?? dimension.defaultFunction,
      metric: level.metric ?? dimension.metric,
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
    description: dimension.description || "",
    formula: dimension.metric?.formula || "",
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
  return {
    key: uuidv4(),
    fields: measure.measureName || columnName,
    columnName,
    tableId: measure.tableId || "",
    columnId: measure.columnId || "",
    defaultFunction: measure.defaultFunction || "",
    semanticType: measure.semanticType || "",
    description: measure.description || "",
    formula: measure.metric?.formula || "",
    filter: "",
    example: "",
    synonyms: "",
    fieldsDropdownOpen: false,
    isDimensionCheck: false,
    measure: {
      isMeasureCheck: true,
      DataType: cubeEditorMeasureData[0].dataType,
      Format: measure.formatString || cubeEditorMeasureData[0].format[0],
    },
    isVisible: true,
    sort: { isSortCheck: false, value: "" },
    aggregation: {
      isAggregationCheck: true,
      value: measure.aggregator || "Sum",
    },
    isPartitionCheck: false,
    table: { name: "" },
    column: { defaultFunction: measure.defaultFunction || "" },
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

export const createManualMetricChild = (children = []) => {
  const metricName = getNextManualMetricName(children);
  return {
    key: uuidv4(),
    fields: metricName,
    columnName: "",
    tableId: "",
    columnId: "",
    defaultFunction: "",
    semanticType: "",
    description: "",
    formula: "",
    filter: "",
    example: "",
    synonyms: "",
    topic: "",
    fieldsDropdownOpen: false,
    isDimensionCheck: false,
    measure: {
      isMeasureCheck: true,
      DataType: cubeEditorMeasureData[0].dataType,
      Format: cubeEditorMeasureData[0].format[0],
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

const buildDomainArrayFromCubeFields = (cubeFieldsData) => {
  const domainNames = parseCommaSeparatedToArray(cubeFieldsData.domainName);
  if (!domainNames.length) {
    return [];
  }
  const topics = parseCommaSeparatedToArray(cubeFieldsData.cubeTopic);
  const description = (cubeFieldsData.cubeDescription ?? "").trim();
  return domainNames.map((domain_name) => ({
    domain_name,
    description,
    topics: [...topics],
  }));
};

const resolveAgentTopicFromData = (data) => {
  const topics = new Set();
  (data.domain || []).forEach((entry) => {
    (entry.topics || []).forEach((topic) => {
      if (topic) {
        topics.add(topic);
      }
    });
  });
  collectDomainTopics(data).forEach((topic) => topics.add(topic));
  return formatArrayAsCommaSeparated(Array.from(topics));
};

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
        description: metric?.description || "",
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
      description: metric.description || "",
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
    cube_info: [
      {
        cubeName: data.cubeName || "",
        dimensions,
        measures,
      },
    ],
  });
};

const hasCubeInfoContent = (data) =>
  (data?.cube_info || []).some(
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
    return {
      ...previous,
      ...item,
      levelName: item.levelName || previous.levelName || "",
      columnName: item.columnName || previous.columnName || "",
      tableId: item.tableId || previous.tableId || "",
      columnId: item.columnId || previous.columnId || "",
      defaultFunction: item.defaultFunction || previous.defaultFunction || "",
      metric: {
        ...buildMetric(previous.metric?.formula),
        ...item.metric,
      },
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
      fieldKey(item.tableId, item.columnId, item.columnName),
      item,
    ]),
  );

  return display.map((item) => {
    const key = fieldKey(item.tableId, item.columnId, item.columnName);
    const previous = storedByKey.get(key) || {};
    if (kind === "dimension") {
      const merged = {
        ...previous,
        ...item,
        dimensionName: item.dimensionName || previous.dimensionName || "",
        columnName: item.columnName || previous.columnName || "",
        tableId: item.tableId || previous.tableId || "",
        columnId: item.columnId || previous.columnId || "",
        defaultFunction:
          item.defaultFunction || previous.defaultFunction || "",
        metric: {
          ...buildMetric(previous.metric?.formula),
          ...item.metric,
        },
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
      ...previous,
      ...item,
      measureName: item.measureName || previous.measureName || "",
      columnName: item.columnName || previous.columnName || "",
      tableId: item.tableId || previous.tableId || "",
      columnId: item.columnId || previous.columnId || "",
      defaultFunction: item.defaultFunction || previous.defaultFunction || "",
      metric: {
        ...buildMetric(previous.metric?.formula),
        ...item.metric,
      },
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
  const cubeEntry = data.cube_info[0] || {
    cubeName: "",
    dimensions: [],
    measures: [],
  };
  const hierarchyData = {
    isHierarchyPresent: false,
    hierarchyList: [],
  };
  const children = orderCubeFields(
    cubeEntry.dimensions || [],
    cubeEntry.measures || [],
  ).map(({ kind, item }) => {
    if (kind === "dimension") {
      const child = createChildFromAgentDimension(item);
      registerDimensionInHierarchyData(child, hierarchyData);
      return child;
    }
    return createChildFromMeasure(item);
  });

  return {
    id: "",
    domainName: resolveAgentDomainNamesFromData(data),
    cubeDescription: resolveAgentDescriptionFromData(data),
    cubeTopic: resolveAgentTopicFromData(data),
    cubeName: "",
    children,
    hierarchyData,
  };
};

export const convertCubeFieldsDataToAgentData = (
  cubeFieldsData,
  existingAgentData,
) => {
  const dimensions = [];
  const measures = [];

  (cubeFieldsData.children || []).forEach((child, sortOrder) => {
    if (child.isDelete) {
      return;
    }
    if (child.isHierarchy) {
      dimensions.push(buildAgentHierarchyDimension(child, sortOrder));
      return;
    }
    if (child.measure?.isMeasureCheck) {
      measures.push(buildMeasureEntry(child, sortOrder));
      return;
    }
    if (child.isDimensionCheck) {
      dimensions.push(buildDimensionEntry(child, sortOrder));
    }
  });

  return normalizeAgentData({
    domain: buildDomainArrayFromCubeFields(cubeFieldsData),
    cube_info: [
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

const stripSortOrderFromCubeInfo = (cube = {}) => ({
  ...cube,
  cubeName: "",
  dimensions: (cube.dimensions || []).map(stripSortOrderFromDimension),
  measures: (cube.measures || []).map(stripFieldSortOrder),
});

export const toPublicAgentData = (agentData) => {
  const data = normalizeAgentData(agentData);
  return {
    domain: data.domain,
    cube_info: (data.cube_info || []).map((cube) => ({
      ...cube,
      cubeName: "",
    })),
  };
};

export const toDisplayAgentData = (agentData) => {
  const data = toPublicAgentData(agentData);
  return {
    ...data,
    cube_info: (data.cube_info || []).map(stripSortOrderFromCubeInfo),
  };
};

const hasStoredOnlyIds = (agentData) => {
  const data = normalizeAgentData(agentData);
  return (data.cube_info || []).some((cube) =>
    [...(cube.dimensions || []), ...(cube.measures || [])].some(
      (item) => item.tableId || item.columnId,
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
  const storedCube = stored.cube_info[0] || {
    cubeName: "",
    dimensions: [],
    measures: [],
  };
  const displayCube = display.cube_info[0] || {
    cubeName: "",
    dimensions: [],
    measures: [],
  };

  return normalizeAgentData({
    domain: display.domain?.length ? display.domain : stored.domain,
    cube_info: [
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
    (parsed.cube_info?.[0]?.dimensions?.length ||
      parsed.cube_info?.[0]?.measures?.length)
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
