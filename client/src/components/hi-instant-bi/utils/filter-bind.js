const bindText = (value) => String(value ?? "").trim();

const columnLeaf = (value) => {
  if (value && typeof value === "object") {
    return columnLeaf(value.name || value.alias || value.column || "");
  }
  const text = bindText(value).replace(/"/g, "");
  const parts = text.split(".").filter(Boolean);
  return parts[parts.length - 1] || "";
};

const filterBindKeys = (model = {}) => {
  const keys = [model.column, model.alias, model.label, model.title]
    .map((value) => bindText(value))
    .filter(Boolean);
  const leaves = keys.map(columnLeaf).filter(Boolean);
  return [...new Set([...keys, ...leaves])];
};

const reportFilterNames = (filter = {}) => {
  const names = [filter.alias, filter.label, filter.autogen_alias, columnLeaf(filter.column)]
    .map(bindText)
    .filter(Boolean);
  return [...new Set(names)];
};

export const findBoundReportFilter = (report, model) => {
  const keys = filterBindKeys(model).map((key) => key.toLowerCase());
  if (!keys.length) return undefined;
  return (report?.filters || []).find((filter) =>
    reportFilterNames(filter).some((name) => keys.includes(name.toLowerCase()))
  );
};

export const hreportIdForItem = (item = {}, questionsHistory = []) => {
  const chatSeqId = item.chat_sequence_id || "";
  const history = questionsHistory.find((entry) => entry.chat_seq_id === chatSeqId) || {};
  return history.hreportId
    || history?.chat_response?.hreportId
    || history?.fullChatResponse?.hreportId
    || "";
};

export const displayAlias = (model = {}) => bindText(model.alias || model.label || model.title);

const donorScore = (filter, model) => {
  const names = reportFilterNames(filter).map((name) => name.toLowerCase());
  const aliasKeys = [model.alias, model.label, model.title].map(bindText).filter(Boolean).map((key) => key.toLowerCase());
  const columnKey = columnLeaf(model.column).toLowerCase();
  if (aliasKeys.some((key) => names.includes(key))) return 2;
  if (columnKey && names.includes(columnKey)) return 1;
  return 0;
};

export const findDonorFilter = (reports, model) => {
  let best = null;
  let bestScore = 0;
  reports.forEach((report) => {
    (report?.filters || []).forEach((filter) => {
      const score = donorScore(filter, model);
      if (score > bestScore) {
        best = filter;
        bestScore = score;
      }
    });
  });
  return best;
};
