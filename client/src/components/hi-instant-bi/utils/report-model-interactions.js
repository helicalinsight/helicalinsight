const VIZ_INTERACTION_KEYS = ["selectedType", "subVizType", "dualaxis"];

const isObject = (value) => Boolean(value && typeof value === "object" && !Array.isArray(value));

export const normalizeReportModelInteractions = (interactions = {}) => {
  if (!isObject(interactions)) return {};
  const normalized = {};
  VIZ_INTERACTION_KEYS.forEach((key) => {
    if (interactions[key] !== undefined) {
      normalized[key] = interactions[key];
    }
  });
  if (Array.isArray(interactions.filters)) {
    normalized.filters = interactions.filters;
  }
  return normalized;
};

export const getReportModelInteractions = (reportModel = {}, fallback = {}) => {
  const fromModel = normalizeReportModelInteractions(reportModel?.interactions);
  if (Object.keys(fromModel).length) {
    return fromModel;
  }
  return normalizeReportModelInteractions(fallback);
};

const mergeFilter = (filters = [], filter = null) => {
  if (!filter) return filters;
  const key = filter.uid || filter.id;
  if (!key) return [...filters, filter];
  const index = filters.findIndex((item) => (item?.uid || item?.id) === key);
  if (index < 0) return [...filters, filter];
  const next = [...filters];
  next[index] = { ...filters[index], ...filter };
  return next;
};

export const mergeReportModelInteractions = (current = {}, update = {}) => {
  const base = normalizeReportModelInteractions(current);
  const next = normalizeReportModelInteractions(update);
  const merged = {
    ...base,
    ...next,
  };
  if (Array.isArray(next.filters)) {
    merged.filters = next.filters.reduce(
      (filters, filter) => mergeFilter(filters, filter),
      Array.isArray(base.filters) ? [...base.filters] : []
    );
  }
  return merged;
};

export const applyHreportInteraction = (interactions = {}, { event, data } = {}) => {
  const current = normalizeReportModelInteractions(interactions);
  if (event === "change_viz") {
    return {
      ...current,
      ...normalizeReportModelInteractions(data),
    };
  }
  if (event === "add_filter") {
    return {
      ...current,
      filters: mergeFilter(current.filters || [], data),
    };
  }
  return current;
};

export const applyHreportInteractionToReportModel = (reportModel = {}, event, data) => {
  if (!isObject(reportModel) || !Object.keys(reportModel).length) return reportModel;
  return {
    ...reportModel,
    interactions: applyHreportInteraction(reportModel.interactions, { event, data }),
  };
};

export const applyHreportInteractionsToReportModel = (reportModel = {}, interactions = {}) => {
  if (!isObject(reportModel) || !Object.keys(reportModel).length) return reportModel;
  const normalized = normalizeReportModelInteractions(interactions);
  if (!Object.keys(normalized).length) return reportModel;
  return {
    ...reportModel,
    interactions: mergeReportModelInteractions(reportModel.interactions, normalized),
  };
};

export const applyHreportInteractionsToChatResponse = (chatResponse = {}, interactions = {}) => {
  if (!isObject(chatResponse)) return chatResponse;
  const normalized = normalizeReportModelInteractions(interactions);
  if (!Object.keys(normalized).length) return chatResponse;
  const next = { ...chatResponse };
  if (isObject(next.report_model) && Object.keys(next.report_model).length) {
    next.report_model = applyHreportInteractionsToReportModel(next.report_model, normalized);
  }
  return next;
};

const CHAT_RESPONSE_KEYS = ["chat_response", "chatResponse", "fullChatResponse"];

export const applyHreportInteractionsToChatPayload = (payload = {}, interactions = {}) => {
  if (!isObject(payload)) return payload;
  const normalized = normalizeReportModelInteractions(interactions);
  if (!Object.keys(normalized).length) return payload;
  let next = applyHreportInteractionsToChatResponse(payload, normalized);
  CHAT_RESPONSE_KEYS.forEach((key) => {
    if (next[key]) {
      next = {
        ...next,
        [key]: applyHreportInteractionsToChatResponse(next[key], normalized),
      };
    }
  });
  return next;
};
