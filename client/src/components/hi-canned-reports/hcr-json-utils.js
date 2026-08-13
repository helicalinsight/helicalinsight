import { cloneDeep, omit } from "lodash";

const HCR_REPORT_PROTECTED_KEYS = [
    "key",
    "uuid",
    "mode",
    "selectedConnectionDetails",
    "hcrFiltersDrawerStatus",
    "selectedDS",
    "sidebarPaneActiveKey",
    "isPreviewing",
    "isUpdatingCanvasPageStyles",
    "hcrExportProperties",
    "hcrQueryRunning",
    "canvasView",
    "canvasTabViews",
    "hcrPreviewData",
    "hcrTableClipboardData",
    "defaultPropertiesAdded"
]

const getHCREditableReportState = (report) => cloneDeep(omit(report, HCR_REPORT_PROTECTED_KEYS));

const getReportForViewer = (report) => JSON.stringify(getHCREditableReportState(report), null, 4);

const mergeJSONIntoHCRReport = (
    activeReport = {},
    parsedReportState = {},
) => ({
    ...cloneDeep(activeReport),
    ...getHCREditableReportState(cloneDeep(parsedReportState)),
});


export {
    getHCREditableReportState,
    getReportForViewer,
    mergeJSONIntoHCRReport,
    HCR_REPORT_PROTECTED_KEYS
};
