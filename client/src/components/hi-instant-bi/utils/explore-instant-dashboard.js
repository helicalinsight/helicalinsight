import { applyDashboardOnDesigner } from "../../bridges/dashboard/dashboard-bridge";
import {
  assembleInstantDashboardCreateConfig,
  getGridItemsFiltersSettings,
} from "./assemble-instant-dashboard";
import { getInstantBIMetadataForReport } from "./common-utils";
import { persistConvertDashboardSeed } from "./convert-sql-to-hr-save";
import { INSTANT_EDIT_MODE } from "./save-instant-dashboard";

export const exploreInstantDashboard = ({
  dashboardModel = {},
  steps = [],
  questionsHistory = [],
  reports = [],
  activeReport = {},
} = {}) => {
  const items = Array.isArray(dashboardModel?.items) ? dashboardModel.items : [];
  if (!items.length) return false;

  const dashboardConfig = assembleInstantDashboardCreateConfig({
    dashboardModel,
    steps,
    theme: dashboardModel.theme || {},
    questionsHistory,
    isStatic: false,
    reports,
    activeReport,
  });
  const gridItemsFiltersInfo = getGridItemsFiltersSettings(dashboardConfig.gridItemsData, reports);
  const subject = getInstantBIMetadataForReport(activeReport) || {};
  persistConvertDashboardSeed(
    { ...dashboardConfig, designerMode: INSTANT_EDIT_MODE },
    { subject },
    reports,
    gridItemsFiltersInfo,
  );
  window.open(`${window.baseURL}#/dashboard-designer`);
  return true;
};

// View Dashboard on designer Slash builds the model here. The bridge paints the canvas.
export const applyInstantDashboardToCanvas = ({
  dispatch,
  dashboardModel = {},
  steps = [],
  questionsHistory = [],
  reports = [],
  activeReport = {},
} = {}) => {
  if (typeof dispatch !== "function") return false;
  const items = Array.isArray(dashboardModel?.items) ? dashboardModel.items : [];
  // Nothing to draw when the answer has no dashboard items.
  if (!items.length) return false;

  const dashboardConfig = assembleInstantDashboardCreateConfig({
    dashboardModel,
    steps,
    theme: dashboardModel.theme || {},
    questionsHistory,
    isStatic: false,
    reports,
    activeReport,
  });
  // Keep the designer editable. The compact chat does not mount a second designer.
  const bridge = applyDashboardOnDesigner({
    dispatch,
    dashboardConfig: { ...dashboardConfig, designerMode: INSTANT_EDIT_MODE },
    reports,
    subject: getInstantBIMetadataForReport(activeReport) || {},
    preview: false,
  });
  return Boolean(bridge);
};
