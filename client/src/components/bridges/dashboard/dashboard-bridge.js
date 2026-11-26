import { isEmpty } from "lodash";
import {
  setFilterCounter,
  setPreviewMode,
  storeDashboardConfig,
  storeFiltersList,
  updateDashboardInstantSubject,
} from "../../../redux/actions/dashboard-designer.actions";
import { getReportModelInteractions } from "../../hi-instant-bi/utils/report-model-interactions";
import createHReportBridge from "../hreport/hreport-bridge";
import { getReportById } from "../hreport/utils";

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
    const isReportPresent = getReportById(dispatch, hreportId);
    if (!isEmpty(isReportPresent)) {
      resolve(processNext(index + 1));
      return;
    }
    const reportModel = itemReportModel(item);
    const bridge = createHReportBridge({
      dispatch,
      reportModel,
      reportId: hreportId,
      reportMetadata,
      interactions: getReportModelInteractions(
        reportModel,
        interactionsById[item.reportInfo?.dashboardItemId]
        || interactionsById[item.reportInfo?.component_id]
        || interactionsById[hreportId]
      ),
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

// Instant BI builds the dashboard. This is the only place that writes it onto the designer.
export const applyDashboardOnDesigner = ({
  dispatch,
  dashboardConfig = {},
  reports = [],
  subject = {},
  preview = false,
} = {}) => {
  if (typeof dispatch !== "function") return null;
  const gridItemsData = Array.isArray(dashboardConfig.gridItemsData) ? dashboardConfig.gridItemsData : [];
  // An empty config must not clear the canvas the user is already editing.
  if (!gridItemsData.length) return null;

  // Replace the open designer canvas with this dashboard.
  dispatch(storeDashboardConfig(dashboardConfig));
  // The designer page stays editable. Preview is only for an embedded viewer.
  dispatch(setPreviewMode(Boolean(preview)));
  gridItemsData.forEach((gridItem) => {
    if (gridItem.compType === DASHBOARD_CREATE_TILE) {
      const hreportId = gridItem?.reportInfo?.hreportId || "";
      const report = reports.find((item) => item.id === hreportId);
      if (report?.filters?.length) {
        // Tile filters belong to the report. Copy them onto the designer item.
        dispatch(storeFiltersList({
          filtersList: report.filters,
          designerItemId: gridItem.id,
          reportId: hreportId,
        }));
      }
    }
    if (gridItem.compType === "filter-component") {
      // A filter tile needs the designer filter shelf.
      dispatch(setFilterCounter(true));
    }
  });
  if (subject && Object.keys(subject).length) {
    // Canvas charts query through the same semantic model as the chat.
    dispatch(updateDashboardInstantSubject(subject));
  }

  // Build tile reports that are not already in state. Reports that exist are left alone.
  const bridge = createIBDashboardBridge({ dispatch, gridItemsData });
  bridge.init();
  return bridge;
};

export default createIBDashboardBridge;
