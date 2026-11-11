import { getReportModelInteractions } from "./report-model-interactions";

export const INSTANT_EDIT_MODE = "instant-edit";

export const isInstantEditMode = (designerMode) => designerMode === INSTANT_EDIT_MODE;

const INLINE_CHART_COMP_TYPES = [
  "dashboard-designer-component",
  "dashboard-create-component",
];

const isObjectWithKeys = (value) =>
  Boolean(value && typeof value === "object" && Object.keys(value).length);

export const isInlineDashboardChart = (item = {}) =>
  INLINE_CHART_COMP_TYPES.includes(item?.compType) &&
  Boolean(item?.reportInfo?.inline || item?.reportInfo?.file?.inline);

const inlineReportModelCandidates = (item = {}) => {
  const file = item?.reportInfo?.file || {};
  const instantItem = file.instant_item || {};
  return [
    file.report_model,
    instantItem.chat_response?.report_model,
    instantItem.chatResponse?.report_model,
    instantItem.report_model,
    item?.report_model,
  ];
};

export const getInlineDashboardReportModel = (item = {}) => {
  const candidates = inlineReportModelCandidates(item).filter(isObjectWithKeys);
  return (
    candidates.find((model) =>
      Object.keys(getReportModelInteractions(model)).length
    ) ||
    candidates[0] ||
    {}
  );
};

export const syncInstantDashboardChartInteractions = (gridItemsData = []) =>
  (gridItemsData || []).map((item) => {
    if (!isInlineDashboardChart(item)) return item;
    const reportModel = getInlineDashboardReportModel(item);
    if (!isObjectWithKeys(reportModel)) return item;
    const interactions = getReportModelInteractions(reportModel);
    const file = item.reportInfo?.file || {};
    return {
      ...item,
      reportInfo: {
        ...item.reportInfo,
        file: {
          ...file,
          report_model: Object.keys(interactions).length
            ? { ...reportModel, interactions }
            : reportModel,
        },
      },
    };
  });
