import '../../utils/mockJsdom';
import { configureStore } from '@reduxjs/toolkit';
import { waitFor } from '@testing-library/react';

jest.setTimeout(30000);

const __originalFetch = global.fetch;
global.fetch = jest.fn((url, ...rest) => {
    const urlStr = String(url);
    if (urlStr.includes('.wasm') || urlStr.includes('muze')) {
        return new Promise(() => {});
    }
    return __originalFetch ? __originalFetch(url, ...rest) : Promise.reject(new Error('fetch not available'));
});
afterAll(() => {
    global.fetch = __originalFetch;
});
const reducers = require('../../../redux').default;
const { hiMockAxios } = require('../../../app/mock-axios');
const {
    addFieldToCanvas,
    loadIntialReport,
    toggleQueryEditor,
    updateCustomColumn,
} = require('../../../redux/actions/hreport.actions');
const actionTypes = require('../../../redux/actions/actionTypes').default;
const hreportReducer = require('../../../redux/reducers/hreport.reducer').default;
const types = require('../../../constants/metadata').default;
const { openMetadata } = require('../../../components/hi-reports/utils/base');
const {
    isCastOrRawFunctionUsed,
    saveDataBaseFunction,
} = require('../../../components/hi-reports/hi-fields-area/utils/utilities');
const crypto = require('crypto');

const makeStore = () => configureStore({
    reducer: reducers,
    middleware: (getDefaultMiddleware) =>
        getDefaultMiddleware({
            thunk: { extraArgument: hiMockAxios },
            immutableCheck: false,
            serializableCheck: false,
        }),
});

const loadMeta = async (dispatch) => {
    await waitFor(() => openMetadata({
        location: 'naresh',
        metadataFileName: 'Metadata_1.metadata',
    }, dispatch));
};

const createCustomColumn = async (reportId, payload) => {
    const store = makeStore();
    const dispatch = store.dispatch;
    const getState = store.getState;
    dispatch(loadIntialReport({ reportId }));
    await loadMeta(dispatch);
    dispatch(toggleQueryEditor({ fieldType: 'column' }));
    dispatch(addFieldToCanvas({
        column: '"HIUSER"."travel_details"."travel_id"',
        alias: 'cc_test',
        genre: types.CUSTOM_FORMULA,
        ...payload,
    }));
    return getState().hreport.present.reports[0].fields[0];
};

describe('custom column datatype at creation', () => {
    test('text type stores type and applies group by by default', async () => {
        const field = await createCustomColumn('cc_text', {
            type: { backendDataType: 'java.lang.String', dataType: 'text' },
        });
        expect(field.type).toEqual({ backendDataType: 'java.lang.String', dataType: 'text' });
        expect(field.groupBy).toEqual(['db.generic.groupBy.group']);
        expect(field.aggregate).toBeFalsy();
    });

    test('numeric type stores type and applies sum aggregate with measure floating', async () => {
        const field = await createCustomColumn('cc_numeric', {
            type: { backendDataType: 'java.lang.Integer', dataType: 'numeric' },
        });
        expect(field.type).toEqual({ backendDataType: 'java.lang.Integer', dataType: 'numeric' });
        expect(field.aggregate).toEqual(['db.generic.aggregate.sum']);
        expect(field.floatingType).toBe('');
        expect(field.groupBy).toBeFalsy();
    });

    test('creation without type keeps legacy shape', async () => {
        const field = await createCustomColumn('cc_legacy', {});
        expect(field.type).toBeFalsy();
        expect(field.groupBy).toBeFalsy();
        expect(field.aggregate).toBeFalsy();
    });

    test('updateCustomColumn persists datatype on edit', async () => {
        const store = makeStore();
        const dispatch = store.dispatch;
        const getState = store.getState;
        dispatch(loadIntialReport({ reportId: 'cc_edit' }));
        await loadMeta(dispatch);
        dispatch(toggleQueryEditor({ fieldType: 'column' }));
        dispatch(addFieldToCanvas({
            column: '"HIUSER"."travel_details"."travel_id"',
            alias: 'cc_edit',
            genre: types.CUSTOM_FORMULA,
        }));
        const created = getState().hreport.present.reports[0].fields[0];
        expect(created.type).toBeFalsy();
        dispatch(updateCustomColumn({
            column: '"HIUSER"."travel_details"."travel_id"',
            alias: 'cc_edit_renamed',
            id: created.id,
            type: { backendDataType: 'java.lang.Integer', dataType: 'numeric' },
        }));
        const updated = getState().hreport.present.reports[0].fields[0];
        expect(updated.alias).toBe('cc_edit_renamed');
        expect(updated.type).toEqual({ backendDataType: 'java.lang.Integer', dataType: 'numeric' });
    });
});

describe('hreportReducer UPDATE_EDITING_FIELD', () => {
    const baseState = {
        reports: [{
            id: 'r1',
            active: true,
            fields: [{ id: 'f1', alias: 'a' }],
            editingField: { id: 'f1', alias: 'a', functionsDefinition: 'CAST(x AS INT)' },
        }],
    };

    test('merges into editingField without closing the editor or touching fields', () => {
        const next = hreportReducer(baseState, {
            type: actionTypes.UPDATE_EDITING_FIELD,
            payload: { editingField: { pendingFunctionReturns: 'numeric' } },
        });
        expect(next.reports[0].editingField.pendingFunctionReturns).toBe('numeric');
        expect(next.reports[0].editingField.functionsDefinition).toBe('CAST(x AS INT)');
        expect(next.reports[0].editingField).toBeTruthy();
        expect(next.reports[0].fields).toEqual([{ id: 'f1', alias: 'a' }]);
    });
});

describe('isCastOrRawFunctionUsed', () => {
    test('detects CAST and RAW usage', () => {
        expect(isCastOrRawFunctionUsed('CAST(travel_id AS INT)')).toBe(true);
        expect(isCastOrRawFunctionUsed('raw(lag(x) over ())')).toBe(true);
        expect(isCastOrRawFunctionUsed('SUM(CAST(x AS INT))')).toBe(true);
    });

    test('rejects non matches and empty input', () => {
        expect(isCastOrRawFunctionUsed('SUM(travel_id)')).toBe(false);
        expect(isCastOrRawFunctionUsed('travel_id')).toBe(false);
        expect(isCastOrRawFunctionUsed('CASTAWAY(x)')).toBe(false);
        expect(isCastOrRawFunctionUsed('')).toBe(false);
        expect(isCastOrRawFunctionUsed()).toBe(false);
    });
});

describe('saveDataBaseFunction pending returns', () => {
    const databaseFunctions = {
        text: [
            {
                value: 'CAST',
                returns: 'text',
                parameters: [{ value: '' }, { value: '' }],
            },
            {
                value: 'RAW',
                returns: 'text',
                parameters: [{ value: '' }],
            },
        ],
    };

    test('applies pending returns for CAST and strips the transient key', () => {
        const actions = [];
        saveDataBaseFunction({
            databaseFunctions,
            fields: [],
            editingField: {
                id: 'f1',
                alias: 'c',
                functionsDefinition: 'CAST(travel_id, INT)',
                pendingFunctionReturns: 'numeric',
            },
        }, (action) => actions.push(action));
        const update = actions.find((action) => action.type === actionTypes.UPDATE_COLUMN);
        expect(update.payload.editingField.databaseFunction.returns).toBe('numeric');
        expect(update.payload.editingField.pendingFunctionReturns).toBeUndefined();
    });

    test('ignores pending returns without CAST/RAW in the definition', () => {
        const actions = [];
        saveDataBaseFunction({
            databaseFunctions,
            fields: [],
            editingField: {
                id: 'f1',
                alias: 'c',
                functionsDefinition: 'SUM(travel_id)',
                pendingFunctionReturns: 'numeric',
            },
        }, (action) => actions.push(action));
        const update = actions.find((action) => action.type === actionTypes.UPDATE_COLUMN);
        expect(typeof update.payload.editingField.databaseFunction).not.toBe('object');
        expect(update.payload.editingField.pendingFunctionReturns).toBeUndefined();
    });
});
