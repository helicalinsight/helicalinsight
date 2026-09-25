export const looksLikeRawData = (text = "") => {
  const cleaned = String(text || "").trim();
  return cleaned.startsWith("[") || cleaned.startsWith("{");
};

export const cleanText = (text = "") => {
  const value = String(text || "").trim();
  return looksLikeRawData(value) ? "" : value;
};

export const asNonEmptyList = (value) => {
  if (Array.isArray(value)) {
    return value.filter((item) => item !== null && item !== undefined && item !== "");
  }
  if (value === null || value === undefined || value === "") return [];
  return [value];
};

export const firstNonEmptyList = (...candidates) => {
  for (const candidate of candidates) {
    const list = asNonEmptyList(candidate);
    if (list.length) return list;
  }
  return [];
};

export const firstNonEmptyJson = (...candidates) => {
  for (const candidate of candidates) {
    if (candidate == null) continue;
    if (typeof candidate === "string" && candidate.trim()) return candidate;
    if (typeof candidate === "object" && !Array.isArray(candidate) && Object.keys(candidate).length) {
      return candidate;
    }
    if (Array.isArray(candidate) && candidate.length) return candidate;
  }
  return null;
};

export const columnsFromReportModel = (reportModel = {}) => {
  const dataModel = reportModel?.data_model || reportModel?.dataModel || {};
  const columns = Array.isArray(dataModel.columns) ? dataModel.columns : [];
  return columns
    .map((col) => {
      if (typeof col === "string") return col;
      if (!col || typeof col !== "object") return "";
      return col.column || col.name || col.field || col.label || "";
    })
    .filter(Boolean);
};

export const tablesFromReportModel = (reportModel = {}) => {
  const dataModel = reportModel?.data_model || reportModel?.dataModel || {};
  if (Array.isArray(dataModel.tables) && dataModel.tables.length) {
    return dataModel.tables.map((table) => (
      typeof table === "string" ? table : (table?.name || table?.table || "")
    )).filter(Boolean);
  }
  if (dataModel.table) return asNonEmptyList(dataModel.table);
  return [];
};

export const buildSqlDetailsFallback = (item = {}, llmActivityDetails = null, plan = null) => {
  const existing = item.fullChatResponse?.sql || item.chat_response?.sql || {};
  const reportModel = item.report_model
    || item.reportModel
    || item.fullChatResponse?.report_model
    || item.chat_response?.report_model
    || {};
  const existingCube = existing.required_cube_info && typeof existing.required_cube_info === "object"
    ? existing.required_cube_info
    : {};
  const domains = firstNonEmptyList(
    existing.required_domain,
    llmActivityDetails?.domains,
    plan?.domain ? [plan.domain] : [],
    plan?.domains,
  );
  const topics = firstNonEmptyList(
    existing.required_topic,
    llmActivityDetails?.topics,
    plan?.topics,
  );
  const tables = firstNonEmptyList(
    existing.required_table,
    llmActivityDetails?.tables,
    tablesFromReportModel(reportModel),
  );
  const columns = firstNonEmptyList(
    existing.required_column,
    item.components,
    item.measure_hints,
    columnsFromReportModel(reportModel),
  );
  const metrics = firstNonEmptyList(
    existingCube.picked_metrics,
    item.components,
    item.measure_hints,
    llmActivityDetails?.metrics,
  );
  const dimensions = firstNonEmptyList(
    existingCube.picked_dimensions,
    item.dimensions,
    llmActivityDetails?.dimensions,
  );
  const cubeInfo = {
    ...(existingCube || {}),
    ...(metrics.length ? { picked_metrics: metrics } : {}),
    ...(dimensions.length ? { picked_dimensions: dimensions } : {}),
  };
  return {
    ...existing,
    required_domain: domains,
    required_topic: topics,
    required_table: tables,
    required_column: columns,
    required_cube_info: Object.keys(cubeInfo).length ? cubeInfo : existing.required_cube_info,
    dialect: existing.dialect || "",
    raw_sql: existing.raw_sql || item.generated_sql || "",
  };
};

/** Turn inline [n] markers into markdown links that keep visible text `[n]`. */
export const linkifyThinkCitations = (text = "") =>
  String(text || "").replace(/\[(\d+)\]/g, "[[$1]](#ib-think-step-$1)");

export const CITATION_HREF_RE = /^#ib-think-step-(\d+)$/;

/** Chat-style leads for a collapsed step tooltip, matched to the question word. */
export const COLLAPSED_QUESTION_LEADS = [
  { test: /^how\b/i, lead: "Let's drill through" },
  { test: /^when\b/i, lead: "Let's check" },
  { test: /^where\b/i, lead: "Let's explore" },
  { test: /^what\b/i, lead: "Let's see the finding" },
  { test: /^why\b/i, lead: "Let's explore" },
  { test: /^which\b/i, lead: "Let's compare" },
  { test: /^who\b/i, lead: "Let's find" },
];

export const DEFAULT_COLLAPSED_QUESTION_LEAD = "Let's take a closer look";

export const collapsedQuestionLead = (question = "") => {
  const text = String(question || "").trim();
  const match = COLLAPSED_QUESTION_LEADS.find((item) => item.test.test(text));
  return match?.lead || DEFAULT_COLLAPSED_QUESTION_LEAD;
};
