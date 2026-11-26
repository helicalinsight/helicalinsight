import { isEmpty } from "lodash";

// isEmpty treats a filled array as present. Semantic payloads are plain objects.
const hasSemanticEntries = (value) =>
    !isEmpty(value) && typeof value === "object" && !Array.isArray(value);

const textValue = (value) => String(value || "").trim();

// Fields the explored report keeps. SQL and token usage stay in the chat.
const exploredSemanticPayload = (details) => {
    // A normal chart draw passes null. A default parameter does not replace null.
    const source = details && typeof details === "object" ? details : {};
    const insight = textValue(source.insight);
    const chatId = textValue(source.chatId);
    const chatSequenceId = textValue(source.chatSequenceId);
    const userInput = textValue(source.userInput);
    const agent = source.agent;
    return {
        ...(hasSemanticEntries(source.reportModel) ? { reportModel: source.reportModel } : {}),
        ...(hasSemanticEntries(source.vizDetails) ? { vizDetails: source.vizDetails } : {}),
        ...(insight ? { insight } : {}),
        ...(chatId ? { chatId } : {}),
        ...(chatSequenceId ? { chatSequenceId } : {}),
        ...(userInput ? { userInput } : {}),
        ...(hasSemanticEntries(agent) ? { agent } : {}),
    };
};

const getReports = (dispatch) => {
    let reports = [];
    dispatch((_, getState) => {
        reports = getState().hreport.present.reports || [];
    })
    return reports
}
const checkReportsAvailable = (dispatch) => {
    let reports = getReports(dispatch)
    return reports.length > 0;
}

const getReportById = (dispatch, reportId) => {
    const reports = getReports(dispatch);
    return reports.find((report) => report.id === reportId) || {};
}

const getActiveHReport = (dispatch) => {
    const reports = getReports(dispatch);
    return reports.find((report) => report.active) || null;
}

const getUserState = (dispatch) => {
    let user = {};
    dispatch((_, getState) => {
        user = getState().app.applicationSettingsData.userData || {};
    })
    return user
}

export {
    hasSemanticEntries,
    exploredSemanticPayload,
    checkReportsAvailable,
    getReportById,
    getUserState,
    getActiveHReport
}