
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
    checkReportsAvailable,
    getReportById,
    getUserState,
    getActiveHReport
}