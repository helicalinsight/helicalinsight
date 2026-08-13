import { cloneDeep, omit } from "lodash";

const DASHBOARD_STATE_PROTECTED_KEYS = [
    "filterCounter",
    "previewMode",
    "designerMode",
    "dashboardVariables",
    "dashboardUUID",
    "variables",
    "dashboardConfig",
    "script",
    "printOptions",
    "toggleIframes",
    "dashboardDrawerStatus",
    "gridItemDrawerStatus",
    "currentGroupId",
    "groupId",
    "gridItemId",
    "drawerPositions",
    "currentDrawerPosition",
    "gridIndex",
    "isLoading",
    "reportId",
    "applyDashboardFilters",
    "isSaving",
    "hasUnsavedData",
    "savedReportName",
    "replaceReportId",
    "filterItemsData",
    "maximizedGridItem",
    "css",
    "itemAddedStatus",
    "maximizingStatus"
]

const getDashboardEditableReportState = (dashboard) => cloneDeep(omit(dashboard, DASHBOARD_STATE_PROTECTED_KEYS));

const getDashboardForViewer = (dashboard) => JSON.stringify(getDashboardEditableReportState(dashboard), null, 4);

const mergeJSONIntoDashboard = (
    activeReport = {},
    parsedDashboardState = {},
) => ({
    ...cloneDeep(activeReport),
    ...getDashboardEditableReportState(cloneDeep(parsedDashboardState)),
});


export {
    DASHBOARD_STATE_PROTECTED_KEYS,
    getDashboardEditableReportState,
    getDashboardForViewer,
    mergeJSONIntoDashboard
}