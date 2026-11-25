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

const getDashboardEditableReportState = (dashboard) =>
    cloneDeep(omit(dashboard, [...DASHBOARD_STATE_PROTECTED_KEYS, "dashboardTitle"]));

const getDashboardForViewer = (dashboard) => {
    const editableState = getDashboardEditableReportState(dashboard);
    if (typeof dashboard?.savedReportName === "string" && dashboard.savedReportName) {
        editableState.dashboardTitle = dashboard.savedReportName;
    }
    return JSON.stringify(editableState, null, 4);
};

const mergeJSONIntoDashboard = (
    activeReport = {},
    parsedDashboardState = {},
) => {
    const mergedDashboard = {
        ...cloneDeep(activeReport),
        ...getDashboardEditableReportState(cloneDeep(parsedDashboardState)),
    };
    if (typeof parsedDashboardState.dashboardTitle === "string" && parsedDashboardState.dashboardTitle) {
        mergedDashboard.savedReportName = parsedDashboardState.dashboardTitle;
    }
    return mergedDashboard;
};


export {
    DASHBOARD_STATE_PROTECTED_KEYS,
    getDashboardEditableReportState,
    getDashboardForViewer,
    mergeJSONIntoDashboard
}