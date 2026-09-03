import store from './hreport-bridge.store';
import createHReportBridge from '../../../components/bridges/hreport/hreport-bridge';
import { v4 as uuidv4 } from 'uuid';
import {
    addNewReport,
    loadIntialReport,
    loadMetadata,
    addFieldToCanvas,
    removeFieldFromCanvas,
    updateOrderBy,
    updateFieldAlias,
    createFilter,
    changeFilterCondition,
    changeFilterValue,
    updateFilterAlias,
    updateCustomCondition,
    updateCustomChart,
    updateSelectedType,
    updateSubVizType,
    setHReportLoading,
    updateAggregations,
    loadReportFilters,
} from '../../../redux/actions/hreport.actions';
import { checkReportsAvailable } from '../../../components/bridges/hreport/utils';
import { saveDataBaseFunction } from '../../../components/hi-reports/hi-fields-area/utils/utilities';
import { generateReport, openMetadata } from '../../../components/hi-reports/utils/base';

jest.mock('uuid', () => ({ v4: jest.fn() }));
jest.mock('../../../redux/actions/hreport.actions', () => require('./hreport-bridge.store').actionCreators);
jest.mock('../../../components/bridges/hreport/utils', () => require('./hreport-bridge.store').bridgeUtils);
jest.mock('../../../components/hi-reports/hi-editing-area/utils/constants', () => ({
    conditions: {
        EQUALS: 'Equals',
        CONTAINS: 'Contains',
        CUSTOM: 'Custom',
    },
}));
jest.mock('../../../components/hi-reports/hi-fields-area/utils/utilities', () => ({
    saveDataBaseFunction: jest.fn(({ fields, editingField }) => {
        const target = (fields || []).find((item) => item.id === editingField.id);
        if (target) {
            target.functionsDefinition = editingField.functionsDefinition;
        }
    }),
}));
jest.mock('../../../components/hi-reports/utils/base', () => ({
    generateReport: jest.fn(),
    openMetadata: jest.fn(),
}));
jest.mock('../../../components/hi-reports/utils/utilities', () => ({
    getTableTree: jest.fn(() => ({ tables: require('./hreport-bridge.store').state.tables })),
}));

const REPORT_ID = 'report-under-test';

let dispatch;
let uuidSeq = 0;

function defaultTables() {
    return [
        {
            id: 't1',
            children: [
                { alias: 'revenue', column: { id: 'col-revenue', name: 'revenue' } },
                { alias: 'region', column: { id: 'col-region', name: 'region' } },
                { alias: 'order_date', column: { id: 'col-date', name: 'order_date' } },
            ],
        },
    ];
}

function buildBridge(overrides = {}) {
    return createHReportBridge({ reportId: REPORT_ID, dispatch, ...overrides });
}

function makeReportModel({
    columns = [],
    rows = [],
    detailedColumns = [],
    filters = [],
    aggregate = [],
    location = '',
    metadataFileName = '',
    mark = '',
    viz = '',
    vfTemplate = '',
} = {}) {
    const vf_template = vfTemplate ? (typeof btoa !== 'undefined' ? btoa(vfTemplate) : Buffer.from(vfTemplate, 'binary').toString('base64')) : '';
    return {
        viz: { vf_template },
        viz_model: {
            data: { columns, rows },
            chart: { viz, mark },
        },
        data_model: {
            columns: detailedColumns,
            functions: { aggregate },
            location,
            metadataFileName,
            filters,
        },
    };
}

function makeDetailed(alias, id, overrides = {}) {
    return {
        alias,
        column: { id, name: alias },
        ...overrides,
    };
}

async function flushPromises() {
    await Promise.resolve();
    await Promise.resolve();
    await Promise.resolve();
}

async function initBridgeWithModel(reportModel, extraProps = {}) {
    const bridge = buildBridge({ reportModel, ...extraProps });
    await bridge.init();
    await flushPromises();
    return bridge;
}

async function initBridgeWithMetadata(extraProps = {}) {
    // helper for inline metadata path: reportModel is required, reportMetadata is provided
    // extraProps may contain reportModel overrides or reportMetadata
    return initBridgeWithModel(extraProps.reportModel, extraProps);
}

beforeEach(() => {
    jest.useFakeTimers();
    jest.clearAllMocks();
    store.reset(defaultTables());
    uuidSeq = 0;
    uuidv4.mockImplementation(() => `gen-${++uuidSeq}`);
    dispatch = jest.fn();
    openMetadata.mockResolvedValue({ metadata: { name: 'loaded-meta' } });
    // ensure global atob/btoa exist for node
    if (typeof global.atob === 'undefined') {
        global.atob = (str) => Buffer.from(str, 'base64').toString('binary');
    }
    if (typeof global.btoa === 'undefined') {
        global.btoa = (str) => Buffer.from(str, 'binary').toString('base64');
    }
});

afterEach(() => {
    jest.useRealTimers();
});

describe('createHReportBridge - getReportId', () => {
    test('returns the reportId passed in props', () => {
        const bridge = buildBridge({ reportId: 'my-report' });
        expect(bridge.getReportId()).toBe('my-report');
    });

    test('generates a uuid when reportId is not provided', () => {
        const bridge = createHReportBridge({ dispatch });
        expect(uuidv4).toHaveBeenCalledTimes(1);
        expect(bridge.getReportId()).toBe('gen-1');
    });
});

describe('createHReportBridge - report registration', () => {
    test('does nothing when reportModel is empty', async () => {
        const bridge = buildBridge({ reportModel: {} });
        await bridge.init();
        await flushPromises();
        expect(checkReportsAvailable).not.toHaveBeenCalled();
        expect(loadIntialReport).not.toHaveBeenCalled();
        expect(addNewReport).not.toHaveBeenCalled();
        expect(generateReport).not.toHaveBeenCalled();
    });

    test('dispatches loadIntialReport when no reports exist yet', async () => {
        const reportModel = makeReportModel({
            columns: [],
            detailedColumns: [],
            location: '/reports',
            metadataFileName: 'meta.json',
        });
        // use async metadata path so we need to wait for openMetadata
        await initBridgeWithModel(reportModel, {
            reportMetadata: { metadata: { name: 'sales-meta' }, formData: {} },
        });
        expect(checkReportsAvailable).toHaveBeenCalledWith(dispatch);
        expect(loadIntialReport).toHaveBeenCalledWith({ reportId: REPORT_ID });
        expect(addNewReport).not.toHaveBeenCalled();
    });

    test('dispatches addNewReport when reports already exist', async () => {
        store.ensureReport('existing-report');
        const reportModel = makeReportModel({
            columns: ['region'],
            detailedColumns: [makeDetailed('region', 'col-region')],
        });
        await initBridgeWithModel(reportModel, {
            reportMetadata: { metadata: { tables: defaultTables() } },
        });
        expect(addNewReport).toHaveBeenCalledWith({ reportId: REPORT_ID });
        expect(loadIntialReport).not.toHaveBeenCalled();
    });
});

describe('createHReportBridge - init with inline reportMetadata', () => {
    test('pushes metadata into redux and reads tables from it', async () => {
        const reportModel = makeReportModel({
            detailedColumns: [],
        });
        const reportMetadata = { metadata: { name: 'sales-meta' } };
        await initBridgeWithModel(reportModel, { reportMetadata });

        expect(loadMetadata).toHaveBeenCalledWith(reportMetadata);
        expect(openMetadata).not.toHaveBeenCalled();
    });

    test('adds columns resolved by plain alias string', async () => {
        const reportModel = makeReportModel({
            columns: ['region'],
            detailedColumns: [makeDetailed('region', 'col-region')],
        });
        await initBridgeWithModel(reportModel, {
            reportMetadata: { metadata: { name: 'sales-meta' } },
        });

        expect(addFieldToCanvas).toHaveBeenCalledTimes(1);
        expect(addFieldToCanvas).toHaveBeenCalledWith({
            addedAs: 'column',
            id: 'gen-1',
            alias: 'region',
            column: { id: 'col-region', name: 'region' },
        });
    });

    test('adds rows resolved by column.id for object fields', async () => {
        const reportModel = makeReportModel({
            rows: ['revenue'],
            detailedColumns: [makeDetailed('revenue', 'col-revenue')],
        });
        await initBridgeWithModel(reportModel, {
            reportMetadata: { metadata: { name: 'sales-meta' } },
        });

        expect(addFieldToCanvas).toHaveBeenCalledWith({
            addedAs: 'row',
            id: 'gen-1',
            alias: 'revenue',
            column: { id: 'col-revenue', name: 'revenue' },
        });
    });

    test('resolves fields given as dotted table.column name via column.name', async () => {
        const reportModel = makeReportModel({
            rows: ['region'],
            detailedColumns: [
                {
                    alias: 'region',
                    column: { id: 'col-region', name: 'sales.region' },
                    name: 'sales.region',
                    id: 'col-region',
                },
            ],
        });
        // The detailed column has name 'sales.region'; getFieldToAdd copies name, id, alias.
        // Then getColumnFromMetadataTables will see field.name = 'sales.region' and alias pop = 'region'
        await initBridgeWithModel(reportModel, {
            reportMetadata: { metadata: { name: 'sales-meta' } },
        });

        expect(addFieldToCanvas).toHaveBeenCalledWith(
            expect.objectContaining({ addedAs: 'row', id: 'gen-1', alias: 'region' })
        );
    });

    test('dispatches updateOrderBy when field has an order', async () => {
        const reportModel = makeReportModel({
            columns: ['region'],
            detailedColumns: [makeDetailed('region', 'col-region', { order: 'asc' })],
        });
        await initBridgeWithModel(reportModel, {
            reportMetadata: { metadata: { name: 'sales-meta' } },
        });

        expect(updateOrderBy).toHaveBeenCalledWith({ id: 'gen-1', key: 'asc' });
    });

    test('applies database function and alias for non custom fields', async () => {
        const reportModel = makeReportModel({
            columns: ['Region Label'],
            detailedColumns: [
                { alias: 'Region Label', column: { id: 'col-region', name: 'region' }, databaseFunction: 'UPPER' },
            ],
        });
        await initBridgeWithModel(reportModel, {
            reportMetadata: { metadata: { name: 'sales-meta' } },
        });

        expect(saveDataBaseFunction).toHaveBeenCalledTimes(1);
        const payload = saveDataBaseFunction.mock.calls[0][0];
        expect(payload.editingField).toEqual(
            expect.objectContaining({
                id: 'gen-1',
                functionsDefinition: 'UPPER',
            })
        );
        expect(payload.fields).toHaveLength(1);
        expect(updateFieldAlias).toHaveBeenCalledWith({ id: 'gen-1', alias: 'Region Label' });
    });

    test('skips database function and alias handling for custom fields', async () => {
        const reportModel = makeReportModel({
            columns: ['calc'],
            detailedColumns: [
                { alias: 'calc', custom: true, column: { expr: 'a + b' } },
            ],
        });
        await initBridgeWithModel(reportModel, {
            reportMetadata: { metadata: { name: 'sales-meta' } },
        });

        expect(addFieldToCanvas).toHaveBeenCalledWith({
            addedAs: 'column',
            id: 'gen-1',
            alias: 'calc',
            column: { expr: 'a + b' },
            genre: 'custom-formula',
        });
        expect(saveDataBaseFunction).not.toHaveBeenCalled();
        expect(updateFieldAlias).not.toHaveBeenCalled();
    });

    test('dispatches updateAggregations when aggregate is defined', async () => {
        const reportModel = makeReportModel({
            columns: ['revenue'],
            detailedColumns: [makeDetailed('revenue', 'col-revenue')],
            aggregate: [{ alias: 'revenue', function: 'SUM' }],
        });
        await initBridgeWithModel(reportModel, {
            reportMetadata: { metadata: { name: 'sales-meta' } },
        });

        expect(updateAggregations).toHaveBeenCalledWith({ id: 'gen-1', key: ['SUM'], group: 'aggregate' });
    });

    test('falls back to a bare field entry when a column cannot be matched in tables', async () => {
        const reportModel = makeReportModel({
            columns: ['does-not-exist'],
            detailedColumns: [makeDetailed('does-not-exist', 'col-unknown')],
        });
        await initBridgeWithModel(reportModel, {
            reportMetadata: { metadata: { name: 'sales-meta' } },
        });

        // tables have no col-unknown nor alias does-not-exist, getColumnFromMetadataTables returns {}
        expect(addFieldToCanvas).toHaveBeenCalledWith({ addedAs: 'column', id: 'gen-1' });
        expect(updateOrderBy).not.toHaveBeenCalled();
    });

    test('does not add anything when viz columns do not match any detailedColumns', async () => {
        const reportModel = makeReportModel({
            columns: ['does-not-exist'],
            detailedColumns: [makeDetailed('region', 'col-region')],
        });
        await initBridgeWithModel(reportModel, {
            reportMetadata: { metadata: { name: 'sales-meta' } },
        });
        expect(addFieldToCanvas).not.toHaveBeenCalled();
    });
});

describe('createHReportBridge - filters', () => {
    test('creates a metadata filter with condition, values and alias', async () => {
        const reportModel = makeReportModel({
            detailedColumns: [makeDetailed('region', 'col-region')],
            filters: [
                { alias: 'Zone', column: { id: 'col-region', name: 'region' }, condition: 'EQUALS', values: ['EMEA'] },
            ],
        });
        // The filter alias Zone will be copied via getFieldToAdd as alias, and id as col-region
        await initBridgeWithModel(reportModel, {
            reportMetadata: { metadata: { name: 'sales-meta' } },
        });

        expect(createFilter).toHaveBeenCalledWith(
            expect.objectContaining({
                uid: 'gen-1',
                columnID: 'col-region',
                from: 'metadata',
                alias: 'region', // from tables column alias
            })
        );
        expect(changeFilterCondition).toHaveBeenCalledWith({ uid: 'gen-1', condition: 'EQUALS' });
        expect(changeFilterValue).toHaveBeenCalledWith({
            value: ['EMEA'],
            uid: 'gen-1',
            reportId: REPORT_ID,
        });
        // updateFilterAlias uses filterField.alias which is Zone (filter's alias)
        expect(updateFilterAlias).toHaveBeenCalledWith({
            uid: 'gen-1',
            alias: 'Zone',
            reportId: REPORT_ID,
        });
    });

    test('ignores unknown filter conditions', async () => {
        const reportModel = makeReportModel({
            detailedColumns: [makeDetailed('region', 'col-region')],
            filters: [{ alias: 'region', column: { id: 'col-region', name: 'region' }, condition: 'NOPE', values: ['x'] }],
        });
        await initBridgeWithModel(reportModel, {
            reportMetadata: { metadata: { name: 'sales-meta' } },
        });

        expect(createFilter).toHaveBeenCalled();
        expect(changeFilterCondition).not.toHaveBeenCalled();
        expect(changeFilterValue).toHaveBeenCalled();
    });

    test('applies custom condition for CUSTOM filters', async () => {
        const reportModel = makeReportModel({
            detailedColumns: [makeDetailed('region', 'col-region')],
            filters: [
                {
                    alias: 'region',
                    column: { id: 'col-region', name: 'region' },
                    condition: 'CUSTOM',
                    customCondition: 'region = "EMEA"',
                },
            ],
        });
        await initBridgeWithModel(reportModel, {
            reportMetadata: { metadata: { name: 'sales-meta' } },
        });

        expect(changeFilterCondition).toHaveBeenCalledWith({ uid: 'gen-1', condition: 'CUSTOM' });
        expect(updateCustomCondition).toHaveBeenCalledWith({
            uid: 'gen-1',
            customCondition: 'region = "EMEA"',
        });
    });

    test('adds db-function backed filters as temporary columns then removes them', async () => {
        const reportModel = makeReportModel({
            detailedColumns: [makeDetailed('order_date', 'col-date')],
            filters: [
                { alias: 'order_date', column: { id: 'col-date', name: 'order_date' }, databaseFunction: 'YEAR', condition: 'EQUALS' },
            ],
        });
        await initBridgeWithModel(reportModel, {
            reportMetadata: { metadata: { name: 'sales-meta' } },
        });

        expect(addFieldToCanvas).toHaveBeenCalledWith(
            expect.objectContaining({ addedAs: 'column', id: 'gen-1' })
        );
        expect(saveDataBaseFunction).toHaveBeenCalledTimes(1);
        expect(saveDataBaseFunction.mock.calls[0][0].editingField).toEqual(
            expect.objectContaining({ id: 'gen-1', functionsDefinition: 'YEAR' })
        );
        expect(createFilter).toHaveBeenCalledWith(
            expect.objectContaining({
                uid: 'gen-1',
                reportId: REPORT_ID,
                functionsDefinition: 'YEAR',
            })
        );
        expect(removeFieldFromCanvas).toHaveBeenCalledWith({
            field: expect.objectContaining({ id: 'gen-1' }),
        });

        const report = store.ensureReport(REPORT_ID);
        expect(report.fields).toHaveLength(0);
    });

    test('skips filters whose field id is not present in metadata', async () => {
        const reportModel = makeReportModel({
            detailedColumns: [makeDetailed('region', 'col-region')],
            filters: [{ alias: 'missing', column: { id: 'missing-col', name: 'missing' }, condition: 'EQUALS', values: ['x'] }],
        });
        await initBridgeWithModel(reportModel, {
            reportMetadata: { metadata: { name: 'sales-meta' } },
        });

        expect(createFilter).not.toHaveBeenCalled();
        expect(changeFilterCondition).not.toHaveBeenCalled();
        expect(changeFilterValue).not.toHaveBeenCalled();
        expect(updateFilterAlias).not.toHaveBeenCalled();
    });

    test('calls eventUpdater on filter creation (is mocked if store has filter)', async () => {
        // To trigger eventUpdater, we need getFilterById to return something.
        // Our store does not automatically store filters, but we can mock that
        // getReportById returns a report with filters containing the created uid
        const eventUpdater = jest.fn();
        const originalGetReportById = store.bridgeUtils.getReportById.getMockImplementation();
        // Make getReportById return a report that already has the filter to trigger eventUpdater path
        // Simpler: we patch getReportById to return filters after createFilter
        const mockReport = store.ensureReport(REPORT_ID);
        mockReport.filters = [{ uid: 'gen-1', alias: 'region' }];
        store.bridgeUtils.getReportById.mockImplementation((dispatchArg, reportId) => {
            const r = store.state.reports.find((item) => item.id === reportId) || {};
            // ensure filters include gen-1 when asked
            if (r.id === REPORT_ID && !r.filters.find(f => f.uid === 'gen-1')) {
                r.filters.push({ uid: 'gen-1', alias: 'region' });
            }
            return r;
        });

        const reportModel = makeReportModel({
            detailedColumns: [makeDetailed('region', 'col-region')],
            filters: [{ alias: 'region', column: { id: 'col-region', name: 'region' }, condition: 'EQUALS', values: ['EMEA'] }],
        });
        const bridge = buildBridge({ reportModel, reportMetadata: { metadata: { name: 'x' } }, eventUpdater });
        await bridge.init();
        await flushPromises();

        expect(eventUpdater).toHaveBeenCalledWith(expect.objectContaining({ event: 'add_filter', hreportId: REPORT_ID }));

        store.bridgeUtils.getReportById.mockImplementation(originalGetReportById);
    });

    test('uses interactions.filters via loadReportFilters instead of addFilters', async () => {
        const reportModel = makeReportModel({
            detailedColumns: [makeDetailed('region', 'col-region')],
            filters: [{ alias: 'region', column: { id: 'col-region', name: 'region' }, condition: 'EQUALS', values: ['EMEA'] }],
        });
        const interactions = { filters: [{ uid: 'f1', column: 'col-region', condition: 'EQUALS' }] };
        await initBridgeWithModel(reportModel, {
            reportMetadata: { metadata: { name: 'sales-meta' } },
            interactions,
        });

        expect(loadReportFilters).toHaveBeenCalledWith({ reportId: REPORT_ID, filters: interactions.filters });
        expect(createFilter).not.toHaveBeenCalled();
    });
});

describe('createHReportBridge - viz types and marks', () => {
    test('dispatches the vf template code when provided (base64 decoded)', async () => {
        const reportModel = makeReportModel({
            vfTemplate: '<vf>chart</vf>',
        });
        await initBridgeWithModel(reportModel, {
            reportMetadata: { metadata: { name: 'sales-meta' } },
        });

        expect(updateCustomChart).toHaveBeenCalledWith({ code: '<vf>chart</vf>' });
    });

    test('maps mark and sub viz types', async () => {
        const reportModel = makeReportModel({
            mark: 'KPI',
            viz: 'bar',
        });
        await initBridgeWithModel(reportModel, {
            reportMetadata: { metadata: { name: 'sales-meta' } },
        });

        expect(updateSubVizType).toHaveBeenCalledWith({
            value: '_all_',
            name: 'bar',
            id: 'mark-all-id',
        });
        expect(updateSelectedType).toHaveBeenCalledWith({ selectedType: 'Card' });
    });

    test('maps aliased chart type names to internal types', async () => {
        const reportModel = makeReportModel({
            mark: 'GridTable',
            viz: 'bar',
        });
        await initBridgeWithModel(reportModel, {
            reportMetadata: { metadata: { name: 'sales-meta' } },
        });

        expect(updateSelectedType).toHaveBeenCalledWith({ selectedType: 'S2Chart' });
    });

    test('calls eventUpdater on viz change', async () => {
        const eventUpdater = jest.fn();
        const reportModel = makeReportModel({
            mark: 'KPI',
            viz: 'bar',
        });
        await initBridgeWithModel(reportModel, {
            reportMetadata: { metadata: { name: 'sales-meta' } },
            eventUpdater,
        });
        expect(eventUpdater).toHaveBeenCalledWith({
            hreportId: REPORT_ID,
            event: 'change_viz',
            data: { selectedType: 'KPI', subVizType: 'bar' },
        });
    });

    test('overrides viz with interactions', async () => {
        const reportModel = makeReportModel({
            mark: 'KPI',
            viz: 'bar',
        });
        const interactions = { selectedType: 'Table', subVizType: 'line' };
        await initBridgeWithModel(reportModel, {
            reportMetadata: { metadata: { name: 'sales-meta' } },
            interactions,
        });
        expect(updateSelectedType).toHaveBeenCalledWith({ selectedType: 'Table' });
        expect(updateSubVizType).toHaveBeenCalledWith(expect.objectContaining({ name: 'line' }));
    });

    test('does not map unknown chart types', async () => {
        const reportModel = makeReportModel({
            mark: 'WeirdType',
            viz: 'bar',
        });
        await initBridgeWithModel(reportModel, {
            reportMetadata: { metadata: { name: 'sales-meta' } },
        });

        expect(updateSubVizType).toHaveBeenCalled();
        expect(updateSelectedType).not.toHaveBeenCalled();
    });

    test('skips sub viz update when report has no _all_ mark', async () => {
        store.reset(defaultTables());
        const report = store.ensureReport(REPORT_ID);
        report.marksList = [];

        const reportModel = makeReportModel({
            mark: 'KPI',
            viz: 'bar',
        });
        // need to ensure registration creates report with empty marksList stays empty
        // Our before init, report already exists with empty marksList, checkReportsAvailable will be true -> addNewReport creates another report?
        // To keep single report with empty marksList, reset and manually adjust after registration
        // Simpler: mock getReportById to return report with empty marksList for viz step
        const originalImpl = store.bridgeUtils.getReportById.getMockImplementation();
        store.bridgeUtils.getReportById.mockImplementation((d, id) => {
            if (id === REPORT_ID) return { ...store.state.reports.find(r => r.id === id), marksList: [] };
            return store.state.reports.find(r => r.id === id) || {};
        });

        await initBridgeWithModel(reportModel, {
            reportMetadata: { metadata: { name: 'sales-meta' } },
        });

        expect(updateSubVizType).not.toHaveBeenCalled();
        expect(updateSelectedType).toHaveBeenCalledWith({ selectedType: 'Card' });

        store.bridgeUtils.getReportById.mockImplementation(originalImpl);
    });

    test('vizType is lowercased', async () => {
        const reportModel = makeReportModel({
            mark: 'Chart',
            viz: 'BAR',
        });
        await initBridgeWithModel(reportModel, {
            reportMetadata: { metadata: { name: 'sales-meta' } },
        });
        expect(updateSubVizType).toHaveBeenCalledWith(expect.objectContaining({ name: 'bar' }));
    });
});

describe('createHReportBridge - display report and completion', () => {
    test('passes merged active report and user to generateReport and completes on success', async () => {
        const onComplete = jest.fn();
        const reportModel = makeReportModel({});
        await initBridgeWithModel(reportModel, {
            reportMetadata: { metadata: { name: 'sales-meta' } },
            onComplete,
        });

        expect(generateReport).toHaveBeenCalledTimes(1);
        const [payload, usedDispatch, successCallback] = generateReport.mock.calls[0];
        expect(payload).toEqual(expect.objectContaining({ id: REPORT_ID }));
        expect(payload.user).toEqual(store.state.user);
        expect(usedDispatch).toBe(dispatch);
        expect(typeof successCallback).toBe('function');

        successCallback();
        expect(onComplete).toHaveBeenCalledWith(true);
    });
});

describe('createHReportBridge - hreport loading poller', () => {
    let successCallback;

    beforeEach(async () => {
        const reportModel = makeReportModel({});
        await initBridgeWithModel(reportModel, {
            reportMetadata: { metadata: { name: 'sales-meta' } },
        });
        [[, , successCallback]] = generateReport.mock.calls;
        successCallback();
        jest.clearAllMocks();
    });

    test('starts polling and clears loading flag once report is flagged as loading', () => {
        store.ensureReport(REPORT_ID).hreportLoading = true;

        jest.advanceTimersByTime(2000);

        expect(setHReportLoading).toHaveBeenCalledTimes(1);
        expect(setHReportLoading).toHaveBeenCalledWith({ reportId: REPORT_ID, loading: false });

        jest.advanceTimersByTime(6000);
        expect(setHReportLoading).toHaveBeenCalledTimes(1);
    });

    test('stops without dispatching when report has no loading flag (interval clears after first tick)', () => {
        store.ensureReport(REPORT_ID).hreportLoading = false;
        jest.advanceTimersByTime(2000);
        expect(setHReportLoading).not.toHaveBeenCalled();
        jest.advanceTimersByTime(11000);
        expect(setHReportLoading).not.toHaveBeenCalled();
    });
});

describe('createHReportBridge - init with metadataInfo (async load)', () => {
    test('loads metadata via openMetadata and builds the report from it', async () => {
        const reportModel = makeReportModel({
            columns: ['region'],
            detailedColumns: [makeDetailed('region', 'col-region')],
            location: '/reports',
            metadataFileName: 'meta.json',
        });
        const bridge = buildBridge({ reportModel });
        await bridge.init();
        await flushPromises();
        // need to flush the async getMetadataUsingInfo which calls openMetadata
        // openMetadata is mocked async, so flush again
        await flushPromises();

        expect(openMetadata).toHaveBeenCalledWith(
            { location: '/reports', metadataFileName: 'meta.json' },
            dispatch
        );
        expect(loadMetadata).not.toHaveBeenCalled();
        expect(addFieldToCanvas).toHaveBeenCalledWith(
            expect.objectContaining({ addedAs: 'column', alias: 'region' })
        );
        expect(generateReport).toHaveBeenCalled();
    });

    test('routes empty reportMetadata objects through the async branch', async () => {
        const reportModel = makeReportModel({
            columns: ['region'],
            detailedColumns: [makeDetailed('region', 'col-region')],
            location: '/reports',
            metadataFileName: 'meta.json',
        });
        const bridge = buildBridge({ reportModel, reportMetadata: {} });
        await bridge.init();
        await flushPromises();
        await flushPromises();

        expect(openMetadata).toHaveBeenCalled();
        expect(loadMetadata).not.toHaveBeenCalled();
    });

    test('reports an error and rejects when metadata loading fails', async () => {
        const onError = jest.fn();
        openMetadata.mockResolvedValue({ error: { message: 'bad metadata file' } });

        const reportModel = makeReportModel({
            location: '/x',
            metadataFileName: 'y.json',
        });
        const bridge = buildBridge({
            reportModel,
            onError,
        });

        await expect(bridge.init()).rejects.toThrow('bad metadata file');

        expect(onError).toHaveBeenCalledWith({ error: { message: 'bad metadata file' } });
        expect(generateReport).not.toHaveBeenCalled();
        expect(addFieldToCanvas).not.toHaveBeenCalled();
        expect(openMetadata).toHaveBeenCalled();
    });

    test('reports an error without throwing when no metadata comes back', async () => {
        const onError = jest.fn();
        openMetadata.mockResolvedValue(null);

        const reportModel = makeReportModel({
            location: '/x',
            metadataFileName: 'y.json',
        });
        const bridge = buildBridge({
            reportModel,
            onError,
        });

        await bridge.init();
        await flushPromises();
        await flushPromises();

        expect(onError).toHaveBeenCalledWith({ error: true });
        expect(generateReport).not.toHaveBeenCalled();
        expect(addFieldToCanvas).not.toHaveBeenCalled();
    });

    test('calls openMetadata even when metadataInfo has empty strings (isEmpty check on non-empty object)', async () => {
        const reportModel = makeReportModel({
            // no location/metadataFileName -> still {location:'', metadataFileName:''} which is not empty per lodash
        });
        const bridge = buildBridge({ reportModel });
        await bridge.init();
        await flushPromises();
        expect(openMetadata).toHaveBeenCalledWith({ location: '', metadataFileName: '' }, dispatch);
        expect(generateReport).toHaveBeenCalled();
    });
});

describe('createHReportBridge - edge cases', () => {
    test('skips databaseFunction when it is not a string', async () => {
        const reportModel = makeReportModel({
            columns: ['region'],
            detailedColumns: [makeDetailed('region', 'col-region', { databaseFunction: { key: 'sql.sum' } })],
        });
        await initBridgeWithModel(reportModel, {
            reportMetadata: { metadata: { name: 'sales-meta' } },
        });
        expect(saveDataBaseFunction).not.toHaveBeenCalled();
        expect(addFieldToCanvas).toHaveBeenCalledTimes(1);
    });

    test('handles vfTemplate empty gracefully', async () => {
        const reportModel = makeReportModel({
            vfTemplate: '',
        });
        await initBridgeWithModel(reportModel, {
            reportMetadata: { metadata: { name: 'sales-meta' } },
        });
        expect(updateCustomChart).not.toHaveBeenCalled();
    });

    test('init does not throw when reportModel missing viz/data', async () => {
        const reportModel = { viz: {}, viz_model: {}, data_model: {} };
        await initBridgeWithModel(reportModel, {
            reportMetadata: { metadata: { name: 'sales-meta' } },
        });
        expect(generateReport).toHaveBeenCalled();
    });
});
