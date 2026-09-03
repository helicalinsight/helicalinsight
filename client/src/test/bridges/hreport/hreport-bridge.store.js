const state = {
    reports: [],
    user: { userId: 'user-1', userName: 'Test User' },
    tables: [],
};

function ensureReport(reportId) {
    let report = state.reports.find((item) => item.id === reportId);
    if (!report) {
        report = {
            id: reportId,
            fields: [],
            filters: [],
            marksList: [{ id: 'mark-all-id', value: '_all_' }],
            databaseFunctions: {},
            hreportLoading: false,
        };
        state.reports.push(report);
    }
    return report;
}

function latestReport() {
    return state.reports[state.reports.length - 1];
}

function reset(tables = []) {
    state.reports = [];
    state.tables = tables;
}

const actionCreators = {
    addNewReport: jest.fn(({ reportId }) => {
        ensureReport(reportId);
        return { type: 'ADD_NEW_REPORT', reportId };
    }),
    loadIntialReport: jest.fn(({ reportId }) => {
        ensureReport(reportId);
        return { type: 'LOAD_INITIAL_REPORT', reportId };
    }),
    loadMetadata: jest.fn((payload) => ({ type: 'LOAD_METADATA', payload })),
    addFieldToCanvas: jest.fn((payload) => {
        const report = latestReport();
        if (report) {
            report.fields.push({ ...payload });
        }
        return { type: 'ADD_FIELD_TO_CANVAS', payload };
    }),
    removeFieldFromCanvas: jest.fn(({ field }) => {
        const report = latestReport();
        if (report) {
            report.fields = report.fields.filter((item) => item.id !== field.id);
        }
        return { type: 'REMOVE_FIELD_FROM_CANVAS', field };
    }),
    updateOrderBy: jest.fn((payload) => ({ type: 'UPDATE_ORDER_BY', payload })),
    updateFieldAlias: jest.fn((payload) => ({ type: 'UPDATE_FIELD_ALIAS', payload })),
    createFilter: jest.fn((payload) => ({ type: 'CREATE_FILTER', payload })),
    changeFilterCondition: jest.fn((payload) => ({ type: 'CHANGE_FILTER_CONDITION', payload })),
    changeFilterValue: jest.fn((payload) => ({ type: 'CHANGE_FILTER_VALUE', payload })),
    updateFilterAlias: jest.fn((payload) => ({ type: 'UPDATE_FILTER_ALIAS', payload })),
    updateCustomCondition: jest.fn((payload) => ({ type: 'UPDATE_CUSTOM_CONDITION', payload })),
    updateCustomChart: jest.fn((payload) => ({ type: 'UPDATE_CUSTOM_CHART', payload })),
    updateSelectedType: jest.fn((payload) => ({ type: 'UPDATE_SELECTED_TYPE', payload })),
    updateSubVizType: jest.fn((payload) => ({ type: 'UPDATE_SUB_VIZ_TYPE', payload })),
    setHReportLoading: jest.fn((payload) => ({ type: 'SET_HREPORT_LOADING', payload })),
    updateAggregations: jest.fn((payload) => ({ type: 'UPDATE_AGGREGATIONS', payload })),
    loadReportFilters: jest.fn((payload) => ({ type: 'LOAD_REPORT_FILTERS', payload })),
};

const bridgeUtils = {
    checkReportsAvailable: jest.fn(() => state.reports.length > 0),
    getReportById: jest.fn((dispatchArg, reportId) =>
        state.reports.find((item) => item.id === reportId) || {}
    ),
    getUserState: jest.fn(() => state.user),
};

module.exports = { state, ensureReport, latestReport, reset, actionCreators, bridgeUtils };
