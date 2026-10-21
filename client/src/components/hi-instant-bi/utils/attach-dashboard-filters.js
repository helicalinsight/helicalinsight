import { cloneDeep } from "lodash-es";
import { v4 as uuidv4 } from "uuid";
import { addFilterToReport } from "../../hi-reports/utils/utilities";
import {
  displayAlias,
  findBoundReportFilter,
  findDonorFilter,
  hreportIdForItem,
} from "./filter-bind";

const bindText = (value) => String(value ?? "").trim();

const columnLeaf = (value) => {
  if (value && typeof value === "object") {
    return columnLeaf(value.name || value.alias || value.column || "");
  }
  const text = bindText(value).replace(/"/g, "");
  const parts = text.split(".").filter(Boolean);
  return parts[parts.length - 1] || "";
};

const cloneDonorFilter = (donor, model, reportId) => {
  const filter = cloneDeep(donor);
  const alias = displayAlias(model) || filter.alias || filter.label || "";
  filter.uid = uuidv4();
  filter.configId = uuidv4();
  filter.dataId = uuidv4();
  filter.reportId = reportId;
  filter.values = Array.isArray(model.defaultValues) ? [...model.defaultValues] : [];
  filter.alias = alias;
  filter.label = alias || filter.label;
  filter.active = true;
  return filter;
};

const buildFilterFromMetadata = (report, model) => {
  const tableName = bindText(model.table);
  const columnName = columnLeaf(model.column);
  if (!tableName || !columnName || !report?.metadata?.tables?.[tableName]?.columns?.[columnName]) {
    return null;
  }
  const draft = cloneDeep(report);
  draft.filters = Array.isArray(draft.filters) ? draft.filters : [];
  const before = draft.filters.length;
  try {
    addFilterToReport({
      from: "metadata",
      table: tableName,
      column: columnName,
      alias: displayAlias(model),
      label: displayAlias(model),
      active: true,
    }, draft);
  } catch (error) {
    return null;
  }
  const created = draft.filters[draft.filters.length - 1];
  if (!created || draft.filters.length === before) return null;
  const alias = displayAlias(model);
  if (alias) {
    created.alias = alias;
    created.label = alias;
  }
  created.values = Array.isArray(model.defaultValues) ? [...model.defaultValues] : [];
  created.reportId = report.id;
  created.active = true;
  return created;
};

export const attachMissingDashboardFilters = ({
  reports = [],
  dashboardModel = {},
  questionsHistory = [],
} = {}) => {
  const items = Array.isArray(dashboardModel.items) ? dashboardModel.items : [];
  const instantItems = items.filter((item) => item?.kind === "instant");
  const filterItems = items.filter((item) => String(item?.kind || "").toLowerCase() === "instant.filter");
  if (!filterItems.length) {
    return { reports, changedIds: [] };
  }

  const nextReports = reports.map((report) => ({
    ...report,
    filters: [...(report.filters || [])],
  }));
  const byId = new Map(nextReports.map((report) => [report.id, report]));
  const changedIds = [];

  filterItems.forEach((model) => {
    const source = instantItems.find((item) => item.component_id === model.sourceItemId);
    if (!source) return;
    const hreportId = hreportIdForItem(source, questionsHistory);
    const report = byId.get(hreportId);
    if (!report || findBoundReportFilter(report, model)) return;

    const donor = findDonorFilter(nextReports, model);
    const created = donor
      ? cloneDonorFilter(donor, model, report.id)
      : buildFilterFromMetadata(report, model);
    if (!created) return;
    report.filters = [...report.filters, created];
    if (!changedIds.includes(hreportId)) changedIds.push(hreportId);
  });

  return { reports: nextReports, changedIds };
};
