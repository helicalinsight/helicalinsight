import createHReportBridge from "../hreport/hreport-bridge";

export const DASHBOARD_CREATE_TILE = "dashboard-create-component";

const hasReportModel = (reportModel = {}) =>
  Boolean(reportModel && typeof reportModel === "object" && Object.keys(reportModel).length);

const stepReportModel = (step = {}) =>
  step?.report_model
  || step?.reportModel
  || step?.fullChatResponse?.report_model
  || step?.chat_response?.report_model
  || {};

export const hasDashboardTileModel = (step = {}) => hasReportModel(stepReportModel(step));

const modeledSteps = (steps = []) =>
  (Array.isArray(steps) ? steps : [])
    .map((step, arrayIndex) => ({ step, arrayIndex }))
    .filter(({ step }) => hasDashboardTileModel(step));

export const matchStepReportModel = ({ item = {}, steps = [], cursor = 0 } = {}) => {
  const modeled = modeledSteps(steps);
  let match = null;
  const suffix = String(item?.id || "").match(/-(\d+)$/);
  if (suffix) {
    const wanted = Number(suffix[1]);
    match = modeled.find(({ step }) => Number(step.index) === wanted)
      || modeled.find(({ step, arrayIndex }) => (Number(step.index) || arrayIndex + 1) === wanted)
      || null;
  }
  if (!match) {
    match = modeled[cursor] || null;
  }
  const reportModel = match
    ? stepReportModel(match.step)
    : (item?.report_model || {});
  return { reportModel, stepIndex: match ? match.arrayIndex : -1 };
};

const itemReportModel = (item = {}) =>
  item?.reportInfo?.file?.report_model || item?.reportInfo?.file?.instant_item?.chat_response?.report_model || item?.report_model || {};

const needsHydration = (item = {}) =>
  item?.compType === DASHBOARD_CREATE_TILE
  && hasReportModel(itemReportModel(item));

const itemHreportId = (item = {}) => {
  return item?.reportInfo?.hreportId || item?.reportInfo?.file?.instant_item?.hreportId || "";
}

function createIBDashboardBridge({
  dispatch,
  gridItemsData = [],
  reportMetadata = null,
  interactionsById = {},
  onItemUpdate = () => { },
  onComplete = () => { },
  onError = () => { },
  eventUpdater = () => { },
} = {}) {
  const pending = (Array.isArray(gridItemsData) ? gridItemsData : []).filter(needsHydration);

  const processNext = (index) => new Promise((resolve) => {
    if (index >= pending.length) {
      resolve();
      return;
    }
    const item = pending[index];
    const hreportId = itemHreportId(item);
    const bridge = createHReportBridge({
      dispatch,
      reportModel: itemReportModel(item),
      reportId: hreportId,
      reportMetadata,
      interactions: interactionsById[item.reportInfo?.dashboardItemId]
        || interactionsById[item.reportInfo?.component_id]
        || interactionsById[hreportId]
        || {},
      onComplete: (status, report) => {
        onItemUpdate(report);
        resolve(processNext(index + 1));
      },
      onError: (error) => {
        resolve(processNext(index + 1));
      },
      eventUpdater: (event) => {
        eventUpdater({ ...event, dashboardItemId: item.id });
      },
    });
    bridge.init().catch(() => {
      resolve(processNext(index + 1));
    });
  });

  return {
    items: pending,
    init: async () => {
      try {
        await processNext(0);
        onComplete(pending);
      } catch (error) {
        onError(error);
      }
    },
  };
}

export default createIBDashboardBridge;
