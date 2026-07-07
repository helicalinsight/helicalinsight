import { hcrDSQuery } from "../../../components/hi-canned-reports/hcr-constants";
import { getActiveSubDSParameterType, getCategoryClassNames, getMappedParameters, getParentKeys } from "../../../components/hi-canned-reports/hcrCanvas/advanceComponents/utils";
import { getInitialGroupData, getSubDataSet } from "../../../components/hi-canned-reports/hcrCanvas/hcrCanvasPaneHelperMethods";
import { getInitialSubDataSet, getSubDataSets, getSubDataSetsFromReportState, updateElementsWithStyles, updateSubDataSets } from "../../../components/hi-canned-reports/hcrHelperMethods";

jest.mock('uuid', () => ({
    v4: jest.fn(() => 'mocked-uuid')
}));

describe('test getInitialSubDataSet fn', () => {
    it('should return an object with all required keys', () => {
        const result = getInitialSubDataSet();
        expect(result).toHaveProperty('id');
        expect(result).toHaveProperty('name');
        expect(result).toHaveProperty('isEmpty');
        expect(result).toHaveProperty('fields');
        expect(result).toHaveProperty('calculations');
        expect(result).toHaveProperty('groups');
        expect(result).toHaveProperty('variables');
        expect(result).toHaveProperty('parameters');
        expect(result).toHaveProperty('selectedFields');
        expect(result).toHaveProperty('selectedGroupFields');
    });

    it('should return id as empty string', () => {
        const result = getInitialSubDataSet();
        expect(result.id).toBe('');
    });

    it('should return name as empty string', () => {
        const result = getInitialSubDataSet();
        expect(result.name).toBe('');
    });

    it('should return isEmpty as true', () => {
        const result = getInitialSubDataSet();
        expect(result.isEmpty).toBe(true);
    });

    it('should return fields as empty array', () => {
        const result = getInitialSubDataSet();
        expect(result.fields).toEqual([]);
    });

    it('should return calculations as empty array', () => {
        const result = getInitialSubDataSet();
        expect(result.calculations).toEqual([]);
    });

    it('should return groups as empty array', () => {
        const result = getInitialSubDataSet();
        expect(result.groups).toEqual([]);
    });

    it('should return variables as empty array', () => {
        const result = getInitialSubDataSet();
        expect(result.variables).toEqual([]);
    });

    it('should return parameters as empty array', () => {
        const result = getInitialSubDataSet();
        expect(result.parameters).toEqual([]);
    });

    it('should return selectedFields as empty array', () => {
        const result = getInitialSubDataSet();
        expect(result.selectedFields).toEqual([]);
    });

    it('should return selectedGroupFields as empty array', () => {
        const result = getInitialSubDataSet();
        expect(result.selectedGroupFields).toEqual([]);
    });

    it('should return a new object on each call (no shared reference)', () => {
        const result1 = getInitialSubDataSet();
        const result2 = getInitialSubDataSet();
        expect(result1).not.toBe(result2);
    });

    it('should return arrays that are independent between calls', () => {
        const result1 = getInitialSubDataSet();
        const result2 = getInitialSubDataSet();
        result1.fields.push('test');
        expect(result2.fields).toHaveLength(0);
    });

    it('should match the exact initial shape', () => {
        const result = getInitialSubDataSet();
        expect(result).toEqual({
            id: '',
            name: '',
            isEmpty: true,
            fields: [],
            calculations: [],
            groups: [],
            variables: [],
            parameters: [],
            selectedFields: [],
            selectedGroupFields: [],
        });
    });
});


describe('updateSubDataSets', () => {
    let activeReport;

    beforeEach(() => {
        activeReport = {
            subDataSets: []
        };
    });


    describe('actionType: add', () => {
        it('should add a new subDataSet with isEmpty: false', () => {
            updateSubDataSets(activeReport, 'add', { id: '1', name: 'Test' });
            expect(activeReport.subDataSets).toHaveLength(1);
            expect(activeReport.subDataSets[0].isEmpty).toBe(false);
        });

        it('should set id and name from payload', () => {
            updateSubDataSets(activeReport, 'add', { id: '1', name: 'Test DS' });
            expect(activeReport.subDataSets[0].id).toBe('1');
            expect(activeReport.subDataSets[0].name).toBe('Test DS');
        });

        it('should not set fields key when fields array is empty', () => {
            updateSubDataSets(activeReport, 'add', { id: '1', fields: [] });
            expect(activeReport.subDataSets[0].fields).toEqual([]);
        });

        it('should set calculations when provided', () => {
            const calculations = [{ formula: 'SUM(x)' }];
            updateSubDataSets(activeReport, 'add', { id: '1', calculations });
            expect(activeReport.subDataSets[0].calculations).toEqual(calculations);
        });

        it('should not override calculations when empty', () => {
            updateSubDataSets(activeReport, 'add', { id: '1', calculations: [] });
            expect(activeReport.subDataSets[0].calculations).toEqual([]);
        });

        it('should set groups when provided', () => {
            const groups = [{ groupName: 'G1' }];
            updateSubDataSets(activeReport, 'add', { id: '1', groups });
            expect(activeReport.subDataSets[0].groups).toEqual(groups);
        });

        it('should set selectedFields when provided', () => {
            const selectedFields = ['field1', 'field2'];
            updateSubDataSets(activeReport, 'add', { id: '1', selectedFields });
            expect(activeReport.subDataSets[0].selectedFields).toEqual(selectedFields);
        });

        it('should set selectedGroupFields when provided', () => {
            const selectedGroupFields = ['gf1'];
            updateSubDataSets(activeReport, 'add', { id: '1', selectedGroupFields });
            expect(activeReport.subDataSets[0].selectedGroupFields).toEqual(selectedGroupFields);
        });

        it('should use default empty values when no payload fields provided', () => {
            updateSubDataSets(activeReport, 'add', {});
            const ds = activeReport.subDataSets[0];
            expect(ds.id).toBe('');
            expect(ds.name).toBe('');
            expect(ds.fields).toEqual([]);
            expect(ds.calculations).toEqual([]);
            expect(ds.groups).toEqual([]);
        });

        it('should handle null payload gracefully', () => {
            expect(() => updateSubDataSets(activeReport, 'add', null)).not.toThrow();
        });

        it('should include all keys from getInitialSubDataSet in the added entry', () => {
            updateSubDataSets(activeReport, 'add', { id: '1' });
            const initial = getInitialSubDataSet();
            Object.keys(initial).forEach((key) => {
                expect(activeReport.subDataSets[0]).toHaveProperty(key);
            });
        });
    });


    describe('actionType: addEmpty', () => {
        it('should add an empty subDataSet with prefixed id', () => {
            updateSubDataSets(activeReport, 'addEmpty', { id: 'abc' });
            expect(activeReport.subDataSets).toHaveLength(1);
            expect(activeReport.subDataSets[0].id).toBe('EMPTY_abc');
        });

        it('should keep isEmpty: true', () => {
            updateSubDataSets(activeReport, 'addEmpty', { id: 'abc' });
            expect(activeReport.subDataSets[0].isEmpty).toBe(true);
        });

        it('should initialise all other fields from getInitialSubDataSet', () => {
            updateSubDataSets(activeReport, 'addEmpty', { id: 'abc' });
            const ds = activeReport.subDataSets[0];
            expect(ds.fields).toEqual([]);
            expect(ds.calculations).toEqual([]);
            expect(ds.groups).toEqual([]);
            expect(ds.variables).toEqual([]);
            expect(ds.parameters).toEqual([]);
            expect(ds.selectedFields).toEqual([]);
            expect(ds.selectedGroupFields).toEqual([]);
        });

        it('should prefix EMPTY_ even when id is empty string', () => {
            updateSubDataSets(activeReport, 'addEmpty', { id: '' });
            expect(activeReport.subDataSets[0].id).toBe('EMPTY_');
        });
    });


    describe('actionType: updateFields', () => {
        beforeEach(() => {
            activeReport.subDataSets = [
                { ...getInitialSubDataSet(), id: '1' },
                { ...getInitialSubDataSet(), id: '2' }
            ];
        });

        it('should update fields for the matching subDataSet', () => {
            const fields = [{ label: 'F1' }];
            updateSubDataSets(activeReport, 'updateFields', { id: '1', fields });
            expect(activeReport.subDataSets[0].fields).toEqual(fields);
        });

        it('should not affect other subDataSets', () => {
            updateSubDataSets(activeReport, 'updateFields', { id: '1', fields: [{ label: 'F1' }] });
            expect(activeReport.subDataSets[1].fields).toEqual([]);
        });

        it('should do nothing when id does not match', () => {
            updateSubDataSets(activeReport, 'updateFields', { id: 'nonexistent', fields: [{ label: 'F1' }] });
            expect(activeReport.subDataSets[0].fields).toEqual([]);
            expect(activeReport.subDataSets[1].fields).toEqual([]);
        });

        it('should replace existing fields', () => {
            activeReport.subDataSets[0].fields = [{ label: 'Old' }];
            updateSubDataSets(activeReport, 'updateFields', { id: '1', fields: [{ label: 'New' }] });
            expect(activeReport.subDataSets[0].fields).toEqual([{ label: 'New' }]);
        });
    });


    describe('actionType: updateGroups', () => {
        beforeEach(() => {
            activeReport.subDataSets = [{ ...getInitialSubDataSet(), id: '1' }];
        });

        it('should update groups for the matching subDataSet', () => {
            const groups = [{ groupName: 'G1' }];
            updateSubDataSets(activeReport, 'updateGroups', { id: '1', groups });
            expect(activeReport.subDataSets[0].groups).toEqual(groups);
        });

        it('should not affect other properties', () => {
            updateSubDataSets(activeReport, 'updateGroups', { id: '1', groups: [{ groupName: 'G1' }] });
            expect(activeReport.subDataSets[0].fields).toEqual([]);
            expect(activeReport.subDataSets[0].calculations).toEqual([]);
        });
    });

    describe('actionType: updateCalculations', () => {
        beforeEach(() => {
            activeReport.subDataSets = [{ ...getInitialSubDataSet(), id: '1' }];
        });

        it('should update calculations for the matching subDataSet', () => {
            const calculations = [{ formula: 'AVG(y)' }];
            updateSubDataSets(activeReport, 'updateCalculations', { id: '1', calculations });
            expect(activeReport.subDataSets[0].calculations).toEqual(calculations);
        });

        it('should not affect other properties', () => {
            updateSubDataSets(activeReport, 'updateCalculations', { id: '1', calculations: [{ formula: 'x' }] });
            expect(activeReport.subDataSets[0].fields).toEqual([]);
            expect(activeReport.subDataSets[0].groups).toEqual([]);
        });
    });


    describe('actionType: updateMany', () => {
        beforeEach(() => {
            activeReport.subDataSets = [{ ...getInitialSubDataSet(), id: '1' }];
        });

        it('should update multiple properties in one call', () => {
            const propertiesToUpdate = [
                { key: 'fields', value: [{ label: 'F1' }] },
                { key: 'groups', value: [{ groupName: 'G1' }] }
            ];
            updateSubDataSets(activeReport, 'updateMany', { id: '1', propertiesToUpdate });
            expect(activeReport.subDataSets[0].fields).toEqual([{ label: 'F1' }]);
            expect(activeReport.subDataSets[0].groups).toEqual([{ groupName: 'G1' }]);
        });

        it('should handle an empty propertiesToUpdate array without error', () => {
            expect(() =>
                updateSubDataSets(activeReport, 'updateMany', { id: '1', propertiesToUpdate: [] })
            ).not.toThrow();
        });

        it('should only update the matching subDataSet', () => {
            activeReport.subDataSets.push({ ...getInitialSubDataSet(), id: '2' });
            updateSubDataSets(activeReport, 'updateMany', {
                id: '1',
                propertiesToUpdate: [{ key: 'name', value: 'Updated' }]
            });
            expect(activeReport.subDataSets[0].name).toBe('Updated');
            expect(activeReport.subDataSets[1].name).toBe('');
        });

        it('should apply updates sequentially', () => {
            const propertiesToUpdate = [
                { key: 'name', value: 'First' },
                { key: 'name', value: 'Second' }
            ];
            updateSubDataSets(activeReport, 'updateMany', { id: '1', propertiesToUpdate });
            expect(activeReport.subDataSets[0].name).toBe('Second');
        });
    });


    describe('unknown actionType', () => {
        it('should not modify subDataSets for an unknown action', () => {
            activeReport.subDataSets = [{ ...getInitialSubDataSet(), id: '1' }];
            updateSubDataSets(activeReport, 'unknownAction', { id: '1' });
            expect(activeReport.subDataSets).toHaveLength(1);
            expect(activeReport.subDataSets[0]).toEqual({ ...getInitialSubDataSet(), id: '1' });
        });

        it('should not throw for an unknown action', () => {
            expect(() => updateSubDataSets(activeReport, 'unknownAction', {})).not.toThrow();
        });
    });
});


describe('test getSubDataSetsFromReportState fn', () => {
    const makeQuery = (overrides = {}) => ({
        id: 'query-1',
        name: 'Query One',
        executeQueryData: {
            data: [{ row: 1 }],
            field: [{ label: 'Field A' }, { label: 'Field B' }],
        },
        ...overrides,
    });

    const makeDsPanes = (queries = [makeQuery()]) => [
        {
            dataSourcePane: hcrDSQuery,
            menu: queries,
        },
    ];

    const makeTable = (overrides = {}) => ({
        id: 'table-1',
        category: 'advancedTable',
        selectedQueryID: 'query-1',
        selectedFields: [],
        selectedGroupFields: [],
        ...overrides,
    });

    describe('early-exit guards', () => {
        it('should return [] when nodes is empty', () => {
            expect(getSubDataSetsFromReportState([], makeDsPanes())).toEqual([]);
        });

        it('should return [] when there are no advancedTable nodes', () => {
            const nodes = [{ id: 't1', category: 'simpleTable' }];
            expect(getSubDataSetsFromReportState(nodes, makeDsPanes())).toEqual([]);
        });

        it('should ignore non-advancedTable nodes and only process advancedTable ones', () => {
            const nodes = [
                { id: 'other', category: 'chart' },
                makeTable(),
            ];
            const result = getSubDataSetsFromReportState(nodes, makeDsPanes());
            expect(result).toHaveLength(1);
        });
    });


    describe('empty table (no selectedQueryID)', () => {
        it('should return an EMPTY_ prefixed subDataSet when selectedQueryID is missing', () => {
            const nodes = [makeTable({ selectedQueryID: undefined })];
            const result = getSubDataSetsFromReportState(nodes, makeDsPanes());
            expect(result).toHaveLength(1);
            expect(result[0].id).toBe('EMPTY_table-1');
        });

        it('should return isEmpty: true for empty table', () => {
            const nodes = [makeTable({ selectedQueryID: undefined })];
            const result = getSubDataSetsFromReportState(nodes, makeDsPanes());
            expect(result[0].isEmpty).toBe(true);
        });

        it('should spread getInitialSubDataSet into the empty entry', () => {
            const nodes = [makeTable({ selectedQueryID: undefined })];
            const result = getSubDataSetsFromReportState(nodes, makeDsPanes());
            const initial = getInitialSubDataSet();
            Object.keys(initial).forEach((key) => {
                if (key !== 'id') expect(result[0]).toHaveProperty(key);
            });
        });

        it('should return EMPTY_ entry even when dsPanes is null', () => {
            const nodes = [makeTable({ selectedQueryID: undefined })];
            const result = getSubDataSetsFromReportState(nodes, null);
            expect(result[0].id).toBe('EMPTY_table-1');
        });
    });


    describe('query not found in dsPanes', () => {
        it('should filter out null when selectedQuery is not found', () => {
            const nodes = [makeTable({ selectedQueryID: 'nonexistent' })];
            const result = getSubDataSetsFromReportState(nodes, makeDsPanes());
            expect(result).toEqual([]);
        });

        it('should filter out null when queriesMenu is empty', () => {
            const nodes = [makeTable()];
            const dsPanes = [{ dataSourcePane: hcrDSQuery, menu: [] }];
            const result = getSubDataSetsFromReportState(nodes, dsPanes);
            expect(result).toEqual([]);
        });

        it('should filter out queries with no data and no field', () => {
            const emptyQuery = makeQuery({
                executeQueryData: { data: [], field: [] },
            });
            const nodes = [makeTable()];
            const result = getSubDataSetsFromReportState(nodes, makeDsPanes([emptyQuery]));
            expect(result).toEqual([]);
        });

        it('should filter out null when dsPanes has no matching dataSourcePane', () => {
            const nodes = [makeTable()];
            const dsPanes = [{ dataSourcePane: 'otherPane', menu: [makeQuery()] }];
            const result = getSubDataSetsFromReportState(nodes, dsPanes);
            expect(result).toEqual([]);
        });

        it('should filter out null when dsPanes is undefined', () => {
            const nodes = [makeTable()];
            const result = getSubDataSetsFromReportState(nodes, undefined);
            expect(result).toEqual([]);
        });
    });


    describe('successful subDataSet mapping', () => {
        it('should return a subDataSet with id from selectedQueryID', () => {
            const result = getSubDataSetsFromReportState([makeTable()], makeDsPanes());
            expect(result[0].id).toBe('query-1');
        });

        it('should return a subDataSet with name from the query', () => {
            const result = getSubDataSetsFromReportState([makeTable()], makeDsPanes());
            expect(result[0].name).toBe('Query One');
        });

        it('should set isEmpty to false', () => {
            const result = getSubDataSetsFromReportState([makeTable()], makeDsPanes());
            expect(result[0].isEmpty).toBe(false);
        });

        it('should set selectedFields from the table node', () => {
            const nodes = [makeTable({ selectedFields: ['f1', 'f2'] })];
            const result = getSubDataSetsFromReportState(nodes, makeDsPanes());
            expect(result[0].selectedFields).toEqual(['f1', 'f2']);
        });

        it('should set selectedGroupFields from the table node', () => {
            const nodes = [makeTable({ selectedGroupFields: ['gf1'] })];
            const result = getSubDataSetsFromReportState(nodes, makeDsPanes());
            expect(result[0].selectedGroupFields).toEqual(['gf1']);
        });

        it('should produce fields: [] when executeQueryData has no field array', () => {
            const query = makeQuery({ executeQueryData: { data: [{ row: 1 }], field: [] } });
            const panes = [{ dataSourcePane: hcrDSQuery, menu: [query] }];
            const result = getSubDataSetsFromReportState([makeTable()], panes);
            expect(result[0].fields).toEqual([]);
        });

        it('should include all keys from getInitialSubDataSet', () => {
            const result = getSubDataSetsFromReportState([makeTable()], makeDsPanes());
            const initial = getInitialSubDataSet();
            Object.keys(initial).forEach((key) => {
                expect(result[0]).toHaveProperty(key);
            });
        });
    });


    describe('groups from selectedGroupFields', () => {
        it('should set groups via getInitialGroupData when selectedGroupFields is non-empty', () => {
            const nodes = [makeTable({ selectedGroupFields: ['gf1', 'gf2'] })];
            const result = getSubDataSetsFromReportState(nodes, makeDsPanes());
            expect(result[0].groups).toEqual([
                getInitialGroupData('gf1'),
                getInitialGroupData('gf2'),
            ]);
        });

        it('should leave groups as [] when selectedGroupFields is empty', () => {
            const nodes = [makeTable({ selectedGroupFields: [] })];
            const result = getSubDataSetsFromReportState(nodes, makeDsPanes());
            expect(result[0].groups).toEqual([]);
        });
    });


    describe('multiple advancedTable nodes', () => {
        it('should return one subDataSet per valid table', () => {
            const query2 = makeQuery({ id: 'query-2', name: 'Query Two' });
            const nodes = [
                makeTable({ id: 'table-1', selectedQueryID: 'query-1' }),
                makeTable({ id: 'table-2', selectedQueryID: 'query-2' }),
            ];
            const result = getSubDataSetsFromReportState(nodes, makeDsPanes([makeQuery(), query2]));
            expect(result).toHaveLength(2);
            expect(result[0].id).toBe('query-1');
            expect(result[1].id).toBe('query-2');
        });

        it('should filter out null entries and keep valid ones', () => {
            const nodes = [
                makeTable({ id: 'table-1', selectedQueryID: 'query-1' }),
                makeTable({ id: 'table-2', selectedQueryID: 'nonexistent' }),
            ];
            const result = getSubDataSetsFromReportState(nodes, makeDsPanes());
            expect(result).toHaveLength(1);
            expect(result[0].id).toBe('query-1');
        });

        it('should handle mix of empty and valid tables', () => {
            const nodes = [
                makeTable({ id: 'table-1', selectedQueryID: undefined }),
                makeTable({ id: 'table-2', selectedQueryID: 'query-1' }),
            ];
            const result = getSubDataSetsFromReportState(nodes, makeDsPanes());
            expect(result).toHaveLength(2);
            expect(result[0].id).toBe('EMPTY_table-1');
            expect(result[1].id).toBe('query-1');
        });
    });


    describe('queriesMenu filter (data.length || field.length)', () => {
        it('should include query when only data is non-empty', () => {
            const query = makeQuery({ executeQueryData: { data: [{ row: 1 }], field: [] } });
            const result = getSubDataSetsFromReportState([makeTable()], makeDsPanes([query]));
            expect(result).toHaveLength(1);
        });

        it('should include query when only field is non-empty', () => {
            const query = makeQuery({ executeQueryData: { data: [], field: [{ label: 'F' }] } });
            const result = getSubDataSetsFromReportState([makeTable()], makeDsPanes([query]));
            expect(result).toHaveLength(1);
        });

        it('should exclude query when both data and field are empty', () => {
            const query = makeQuery({ executeQueryData: { data: [], field: [] } });
            const result = getSubDataSetsFromReportState([makeTable()], makeDsPanes([query]));
            expect(result).toHaveLength(0);
        });
    });
});

describe('test getSubDataSet fn', () => {
    const makeSubDataSet = (overrides = {}) => ({
        id: 'ds-1',
        name: 'DataSet 1',
        isEmpty: false,
        ...overrides,
    });

    describe('early-exit guards', () => {
        it('should return {} when subDataSets is empty array', () => {
            expect(getSubDataSet([], 'ds-1')).toEqual({});
        });

        it('should return {} when subDataSets is undefined', () => {
            expect(getSubDataSet(undefined, 'ds-1')).toEqual({});
        });

        it('should return {} when subDataSets is null', () => {
            expect(getSubDataSet(null, 'ds-1')).toEqual({});
        });

        it('should return {} when id is undefined', () => {
            expect(getSubDataSet([makeSubDataSet()], undefined)).toEqual({});
        });

        it('should return {} when id is null', () => {
            expect(getSubDataSet([makeSubDataSet()], null)).toEqual({});
        });

        it('should return {} when id is empty string', () => {
            expect(getSubDataSet([makeSubDataSet()], '')).toEqual({});
        });

        it('should return {} when both subDataSets and id are missing', () => {
            expect(getSubDataSet()).toEqual({});
        });
    });


    describe('direct id match', () => {
        it('should return the matching subDataSet by id', () => {
            const ds = makeSubDataSet({ id: 'ds-1' });
            expect(getSubDataSet([ds], 'ds-1')).toEqual(ds);
        });

        it('should return the correct entry when multiple subDataSets exist', () => {
            const ds1 = makeSubDataSet({ id: 'ds-1', name: 'First' });
            const ds2 = makeSubDataSet({ id: 'ds-2', name: 'Second' });
            expect(getSubDataSet([ds1, ds2], 'ds-2')).toEqual(ds2);
        });

        it('should return the first match when duplicate ids exist', () => {
            const ds1 = makeSubDataSet({ id: 'ds-1', name: 'First' });
            const ds2 = makeSubDataSet({ id: 'ds-1', name: 'Duplicate' });
            expect(getSubDataSet([ds1, ds2], 'ds-1')).toEqual(ds1);
        });

        it('should return non-empty subDataSet over empty ones when id matches directly', () => {
            const ds = makeSubDataSet({ id: 'ds-1', isEmpty: false });
            const empty = makeSubDataSet({ id: 'EMPTY_ds-1', isEmpty: true });
            expect(getSubDataSet([ds, empty], 'ds-1')).toEqual(ds);
        });
    });


    describe('fallback to empty subDataSet', () => {
        it('should return {} when no match and no empty subDataSets exist', () => {
            const ds = makeSubDataSet({ id: 'ds-2', isEmpty: false });
            expect(getSubDataSet([ds], 'ds-1')).toEqual({});
        });

        it('should return matching empty subDataSet when id is included in its id', () => {
            const empty = makeSubDataSet({ id: 'EMPTY_ds-1', isEmpty: true });
            expect(getSubDataSet([empty], 'ds-1')).toEqual(empty);
        });

        it('should return {} when empty subDataSet exists but id does not match', () => {
            const empty = makeSubDataSet({ id: 'EMPTY_ds-2', isEmpty: true });
            expect(getSubDataSet([empty], 'ds-1')).toEqual({});
        });

        it('should return correct empty subDataSet among multiple empty ones', () => {
            const empty1 = makeSubDataSet({ id: 'EMPTY_ds-1', isEmpty: true });
            const empty2 = makeSubDataSet({ id: 'EMPTY_ds-2', isEmpty: true });
            expect(getSubDataSet([empty1, empty2], 'ds-2')).toEqual(empty2);
        });

        it('should return {} when all empty subDataSets have non-matching ids', () => {
            const empty1 = makeSubDataSet({ id: 'EMPTY_ds-3', isEmpty: true });
            const empty2 = makeSubDataSet({ id: 'EMPTY_ds-4', isEmpty: true });
            expect(getSubDataSet([empty1, empty2], 'ds-1')).toEqual({});
        });

        it('should return {} when empty subDataSet has undefined id', () => {
            const empty = makeSubDataSet({ id: undefined, isEmpty: true });
            expect(getSubDataSet([empty], 'ds-1')).toEqual({});
        });

        it('should handle empty subDataSet with empty string id', () => {
            const empty = makeSubDataSet({ id: '', isEmpty: true });
            expect(getSubDataSet([empty], 'ds-1')).toEqual({});
        });

        it('should not return a non-empty subDataSet as a fallback', () => {
            const nonEmpty = makeSubDataSet({ id: 'EMPTY_ds-1', isEmpty: false });
            expect(getSubDataSet([nonEmpty], 'ds-1')).toEqual({});
        });
    });


    describe('mixed subDataSets', () => {
        it('should prefer direct match over an empty fallback', () => {
            const ds = makeSubDataSet({ id: 'ds-1', isEmpty: false, name: 'Direct' });
            const empty = makeSubDataSet({ id: 'EMPTY_ds-1', isEmpty: true, name: 'Empty' });
            expect(getSubDataSet([ds, empty], 'ds-1')).toEqual(ds);
        });

        it('should fall back to empty when direct match is missing', () => {
            const ds = makeSubDataSet({ id: 'ds-2', isEmpty: false });
            const empty = makeSubDataSet({ id: 'EMPTY_ds-1', isEmpty: true });
            expect(getSubDataSet([ds, empty], 'ds-1')).toEqual(empty);
        });

        it('should return {} when neither direct nor empty match exists', () => {
            const ds = makeSubDataSet({ id: 'ds-2', isEmpty: false });
            const empty = makeSubDataSet({ id: 'EMPTY_ds-3', isEmpty: true });
            expect(getSubDataSet([ds, empty], 'ds-1')).toEqual({});
        });

        it('should handle large list with match near the end', () => {
            const many = Array.from({ length: 50 }, (_, i) =>
                makeSubDataSet({ id: `ds-${i}`, isEmpty: false })
            );
            const target = makeSubDataSet({ id: 'ds-49', name: 'Last' });
            many[49] = target;
            expect(getSubDataSet(many, 'ds-49')).toEqual(target);
        });
    });


    describe('return value integrity', () => {
        it('should return the same object reference as in the array (no clone)', () => {
            const ds = makeSubDataSet({ id: 'ds-1' });
            const result = getSubDataSet([ds], 'ds-1');
            expect(result).toBe(ds);
        });

        it('should return the same reference for empty fallback (no clone)', () => {
            const empty = makeSubDataSet({ id: 'EMPTY_ds-1', isEmpty: true });
            const result = getSubDataSet([empty], 'ds-1');
            expect(result).toBe(empty);
        });
    });
});

describe('test getSubDataSetS fn', () => {

    const makeQuery = (id, extra = {}) => ({
        id,
        temp_uuid: `temp-${id}`,
        ...extra,
    });

    const makeSubDataSet = (id, name, extra = {}) => ({
        id,
        name,
        isEmpty: false,
        fields: [],
        groups: [],
        calculations: [],
        parameters: [],
        ...extra,
    });

    const makeGroup = (id, name, extra = {}) => ({
        id,
        name,
        expression: `$F{${name}}`,
        reprintHeaderOnEachPage: false,
        startNewColumn: false,
        startNewPage: false,
        resetPageNumber: false,
        keepTogether: false,
        preventOrphanFooter: false,
        minHeightToStartNewPage: 0,
        minRecordsToStartFromTop: 0,
        ...extra,
    });

    const makeCalculation = (id, calcId, name, extra = {}) => ({
        id,
        calcId,
        name,
        className: 'java.lang.Integer',
        calculation: 'SUM',
        resetType: 'Report',
        expression: '$F{amount}',
        increment: 'None',
        incrementFactoryClassName: '',
        resetGroup: '',
        incrementGroup: '',
        initialValueExp: '0',
        ...extra,
    });

    const makeParameter = (name, extra = {}) => ({
        name,
        type: 'java.lang.String',
        canvasValues: { defaultValue: `"default_${name}"` },
        ...extra,
    });

    const CONNECTION = { host: 'localhost', port: 5432 };


    describe('getSubDataSets — return shape', () => {
        it('returns an object with dataSets and parameters keys', () => {
            const result = getSubDataSets();
            expect(result).toHaveProperty('dataSets');
            expect(result).toHaveProperty('parameters');
        });

        it('dataSets is an array', () => {
            expect(Array.isArray(getSubDataSets().dataSets)).toBe(true);
        });

        it('parameters is an array', () => {
            expect(Array.isArray(getSubDataSets().parameters)).toBe(true);
        });

        it('returns empty dataSets and parameters when all args are omitted', () => {
            const { dataSets, parameters } = getSubDataSets();
            expect(dataSets).toHaveLength(0);
            expect(parameters).toHaveLength(0);
        });

        it('returns empty dataSets and parameters when subDataSets is empty', () => {
            const { dataSets, parameters } = getSubDataSets([], [makeQuery('q1')], CONNECTION);
            expect(dataSets).toHaveLength(0);
            expect(parameters).toHaveLength(0);
        });
    });


    describe('getSubDataSets — isEmpty filtering', () => {
        it('excludes items where isEmpty is true', () => {
            const items = [makeSubDataSet('q1', 'DS1', { isEmpty: true })];
            const { dataSets } = getSubDataSets(items, [makeQuery('q1')], CONNECTION);
            expect(dataSets).toHaveLength(0);
        });

        it('includes items where isEmpty is false', () => {
            const items = [makeSubDataSet('q1', 'DS1', { isEmpty: false })];
            const { dataSets } = getSubDataSets(items, [makeQuery('q1')], CONNECTION);
            expect(dataSets).toHaveLength(1);
        });

        it('filters out null results (no matching query) from dataSets', () => {
            const items = [makeSubDataSet('no-match', 'DS1')];
            const { dataSets } = getSubDataSets(items, [makeQuery('q1')], CONNECTION);
            expect(dataSets).toHaveLength(0);
        });

        it('handles a mix of isEmpty true/false items', () => {
            const items = [
                makeSubDataSet('q1', 'DS1', { isEmpty: false }),
                makeSubDataSet('q2', 'DS2', { isEmpty: true }),
            ];
            const { dataSets } = getSubDataSets(items, [makeQuery('q1'), makeQuery('q2')], CONNECTION);
            expect(dataSets).toHaveLength(1);
            expect(dataSets[0].name).toBe('DS1');
        });
    });


    describe('getSubDataSets — returnObj base fields', () => {
        let result;
        beforeEach(() => {
            const items = [makeSubDataSet('q1', 'MyDataSet')];
            result = getSubDataSets(items, [makeQuery('q1')], CONNECTION).dataSets[0];
        });

        it('sets name from item.name', () => {
            expect(result.name).toBe('MyDataSet');
        });

        it('sets isMainDataset to false', () => {
            expect(result.isMainDataset).toBe(false);
        });

        it('sets dataSetExpression using item.name', () => {
            expect(result.dataSetExpression).toBe('$P{MyDataSet}');
        });

        it('sets id from item.id', () => {
            expect(result.id).toBe('q1');
        });

        it('initialises groups as an empty array', () => {
            expect(result.groups).toEqual([]);
        });

        it('initialises variables as an empty array', () => {
            expect(result.variables).toEqual([]);
        });

        it('initialises parameters as an empty array', () => {
            expect(result.parameters).toEqual([]);
        });

        it('initialises subDatasetParameters as an empty array', () => {
            expect(result.subDatasetParameters).toEqual([]);
        });
    });

    describe('getSubDataSets — connectionDetails', () => {
        it('includes temp_uuid from the matched query', () => {
            const items = [makeSubDataSet('q1', 'DS1')];
            const { dataSets } = getSubDataSets(items, [makeQuery('q1')], CONNECTION);
            expect(dataSets[0].connectionDetails.temp_uuid).toBe('temp-q1');
        });

        it('sets map_id to 1', () => {
            const items = [makeSubDataSet('q1', 'DS1')];
            const { dataSets } = getSubDataSets(items, [makeQuery('q1')], CONNECTION);
            expect(dataSets[0].connectionDetails.map_id).toBe(1);
        });

        it('spreads the provided connectionDetails into every dataset', () => {
            const items = [makeSubDataSet('q1', 'DS1')];
            const { dataSets } = getSubDataSets(items, [makeQuery('q1')], { host: 'db-host', port: 3306 });
            expect(dataSets[0].connectionDetails.host).toBe('db-host');
            expect(dataSets[0].connectionDetails.port).toBe(3306);
        });

        it('connectionDetails spread does not override temp_uuid or map_id', () => {
            const items = [makeSubDataSet('q1', 'DS1')];
            const { dataSets } = getSubDataSets(
                items,
                [makeQuery('q1')],
                { temp_uuid: 'overridden', map_id: 99 }
            );
            // spread happens after temp_uuid/map_id, so provided values win
            expect(dataSets[0].connectionDetails.temp_uuid).toBe('overridden');
            expect(dataSets[0].connectionDetails.map_id).toBe(99);
        });

        it('works with an empty connectionDetails object', () => {
            const items = [makeSubDataSet('q1', 'DS1')];
            expect(() => getSubDataSets(items, [makeQuery('q1')], {})).not.toThrow();
        });
    });


    describe('getSubDataSets — fields', () => {
        it('strips id from each field', () => {
            const items = [makeSubDataSet('q1', 'DS1', {
                fields: [{ id: 'f1', name: 'amount', type: 'java.lang.Integer' }],
            })];
            const { dataSets } = getSubDataSets(items, [makeQuery('q1')], CONNECTION);
            expect(dataSets[0].fields[0]).not.toHaveProperty('id');
        });

        it('preserves non-id field properties', () => {
            const items = [makeSubDataSet('q1', 'DS1', {
                fields: [{ id: 'f1', name: 'amount', type: 'java.lang.Integer' }],
            })];
            const { dataSets } = getSubDataSets(items, [makeQuery('q1')], CONNECTION);
            expect(dataSets[0].fields[0]).toEqual({ name: 'amount', type: 'java.lang.Integer' });
        });

        it('returns an empty fields array when item.fields is absent', () => {
            const item = { id: 'q1', name: 'DS1', isEmpty: false };
            const { dataSets } = getSubDataSets([item], [makeQuery('q1')], CONNECTION);
            expect(dataSets[0].fields).toEqual([]);
        });

        it('handles multiple fields correctly', () => {
            const items = [makeSubDataSet('q1', 'DS1', {
                fields: [
                    { id: 'f1', name: 'col1', type: 'java.lang.String' },
                    { id: 'f2', name: 'col2', type: 'java.lang.Integer' },
                ],
            })];
            const { dataSets } = getSubDataSets(items, [makeQuery('q1')], CONNECTION);
            expect(dataSets[0].fields).toHaveLength(2);
            expect(dataSets[0].fields[0]).toEqual({ name: 'col1', type: 'java.lang.String' });
            expect(dataSets[0].fields[1]).toEqual({ name: 'col2', type: 'java.lang.Integer' });
        });
    });

    describe('getSubDataSets — groups', () => {
        it('maps group fields correctly', () => {
            const grp = makeGroup('g1', 'Region', {
                reprintHeaderOnEachPage: true,
                startNewPage: true,
                minRecordsToStartFromTop: 5,
            });
            const items = [makeSubDataSet('q1', 'DS1', { groups: [grp] })];
            const { dataSets } = getSubDataSets(items, [makeQuery('q1')], CONNECTION);
            const group = dataSets[0].groups[0];

            expect(group.name).toBe('Region');
            expect(group.groupNumber).toBe('g1');
            expect(group.expression).toBe('$F{Region}');
            expect(group.reprintHeaderOnEachPage).toBe(true);
            expect(group.startNewPage).toBe(true);
            expect(group.minDetailsToStartFromTop).toBe(5);
        });

        it('maps reprintHeaderOnEachColumn from reprintHeaderOnEachPage', () => {
            const grp = makeGroup('g1', 'Region', { reprintHeaderOnEachPage: true });
            const items = [makeSubDataSet('q1', 'DS1', { groups: [grp] })];
            const { dataSets } = getSubDataSets(items, [makeQuery('q1')], CONNECTION);
            expect(dataSets[0].groups[0].reprintHeaderOnEachColumn).toBe(true);
        });

        it('maps minDetailsToStartFromTop from minRecordsToStartFromTop', () => {
            const grp = makeGroup('g1', 'G', { minRecordsToStartFromTop: 3 });
            const items = [makeSubDataSet('q1', 'DS1', { groups: [grp] })];
            const { dataSets } = getSubDataSets(items, [makeQuery('q1')], CONNECTION);
            expect(dataSets[0].groups[0].minDetailsToStartFromTop).toBe(3);
        });

        it('handles multiple groups', () => {
            const items = [makeSubDataSet('q1', 'DS1', {
                groups: [makeGroup('g1', 'Region'), makeGroup('g2', 'City')],
            })];
            const { dataSets } = getSubDataSets(items, [makeQuery('q1')], CONNECTION);
            expect(dataSets[0].groups).toHaveLength(2);
            expect(dataSets[0].groups[1].name).toBe('City');
        });

        it('leaves groups empty when item.groups is empty', () => {
            const items = [makeSubDataSet('q1', 'DS1', { groups: [] })];
            const { dataSets } = getSubDataSets(items, [makeQuery('q1')], CONNECTION);
            expect(dataSets[0].groups).toEqual([]);
        });
    });

    describe('getSubDataSets — calculations (variables)', () => {
        it('maps calculation fields to variable object correctly', () => {
            const cal = makeCalculation('c1', 'calc-1', 'TotalAmount', {
                className: 'java.lang.Double',
                calculation: 'SUM',
                resetType: 'Group',
                expression: '$F{price}',
                increment: 'None',
                incrementFactoryClassName: 'factory',
                resetGroup: 'Region',
                incrementGroup: '',
                initialValueExp: '0.0',
            });
            const items = [makeSubDataSet('q1', 'DS1', { calculations: [cal] })];
            const { dataSets } = getSubDataSets(items, [makeQuery('q1')], CONNECTION);
            const variable = dataSets[0].variables[0];

            expect(variable.name).toBe('TotalAmount');
            expect(variable.className).toBe('java.lang.Double');
            expect(variable.calculation).toBe('SUM');
            expect(variable.resetType).toBe('Group');
            expect(variable.expression).toBe('$F{price}');
            expect(variable.incrementType).toBe('None');
            expect(variable.incrementFactoryClassName).toBe('factory');
            expect(variable.resetGroup).toBe('Region');
            expect(variable.initialValueExpression).toBe('0.0');
            expect(variable.uniqId).toBe('c1');
            expect(variable.id).toBe('calc-1');
        });

        it('maps incrementType from cal.increment (not cal.incrementType)', () => {
            const cal = makeCalculation('c1', 'cid', 'Var1', { increment: 'Group' });
            const items = [makeSubDataSet('q1', 'DS1', { calculations: [cal] })];
            const { dataSets } = getSubDataSets(items, [makeQuery('q1')], CONNECTION);
            expect(dataSets[0].variables[0].incrementType).toBe('Group');
        });

        it('maps initialValueExpression from cal.initialValueExp', () => {
            const cal = makeCalculation('c1', 'cid', 'Var1', { initialValueExp: '99' });
            const items = [makeSubDataSet('q1', 'DS1', { calculations: [cal] })];
            const { dataSets } = getSubDataSets(items, [makeQuery('q1')], CONNECTION);
            expect(dataSets[0].variables[0].initialValueExpression).toBe('99');
        });

        it('handles multiple calculations', () => {
            const items = [makeSubDataSet('q1', 'DS1', {
                calculations: [
                    makeCalculation('c1', 'cid1', 'Var1'),
                    makeCalculation('c2', 'cid2', 'Var2'),
                ],
            })];
            const { dataSets } = getSubDataSets(items, [makeQuery('q1')], CONNECTION);
            expect(dataSets[0].variables).toHaveLength(2);
        });

        it('leaves variables empty when item.calculations is empty', () => {
            const items = [makeSubDataSet('q1', 'DS1', { calculations: [] })];
            const { dataSets } = getSubDataSets(items, [makeQuery('q1')], CONNECTION);
            expect(dataSets[0].variables).toEqual([]);
        });
    });


    describe('getSubDataSets — parameters', () => {
        it('maps parameter fields correctly', () => {
            const param = makeParameter('startDate', {
                type: 'java.util.Date',
                canvasValues: { defaultValue: '"2024-01-01"' },
            });
            const items = [makeSubDataSet('q1', 'DS1', { parameters: [param] })];
            const { dataSets } = getSubDataSets(items, [makeQuery('q1')], CONNECTION);
            const p = dataSets[0].parameters[0];

            expect(p.name).toBe('startDate');
            expect(p.className).toBe('java.util.Date');
            expect(p.value).toBe('"2024-01-01"');
        });

        it('sets subDatasetParameters to the raw parameters array', () => {
            const params = [makeParameter('p1'), makeParameter('p2')];
            const items = [makeSubDataSet('q1', 'DS1', { parameters: params })];
            const { dataSets } = getSubDataSets(items, [makeQuery('q1')], CONNECTION);
            expect(dataSets[0].subDatasetParameters).toEqual(params);
        });

        it('adds each parameter name and its defaultValue (quotes stripped) to connectionDetails', () => {
            const param = makeParameter('myParam', {
                canvasValues: { defaultValue: '"hello"' },
            });
            const items = [makeSubDataSet('q1', 'DS1', { parameters: [param] })];
            const { dataSets } = getSubDataSets(items, [makeQuery('q1')], CONNECTION);
            expect(dataSets[0].connectionDetails.myParam).toBe('hello');
        });

        it('strips all double-quote characters from defaultValue in connectionDetails', () => {
            const param = makeParameter('p1', {
                canvasValues: { defaultValue: '"val"ue"' },
            });
            const items = [makeSubDataSet('q1', 'DS1', { parameters: [param] })];
            const { dataSets } = getSubDataSets(items, [makeQuery('q1')], CONNECTION);
            expect(dataSets[0].connectionDetails.p1).toBe('value');
        });

        it('sets connectionDetails parameter value to empty string when defaultValue is absent', () => {
            const param = { name: 'p1', type: 'java.lang.String', canvasValues: {} };
            const items = [makeSubDataSet('q1', 'DS1', { parameters: [param] })];
            const { dataSets } = getSubDataSets(items, [makeQuery('q1')], CONNECTION);
            expect(dataSets[0].connectionDetails.p1).toBe('');
        });

        it('defaults value and defaultExpression to empty string when defaultValue is absent', () => {
            const param = { name: 'p1', type: 'java.lang.String', canvasValues: {} };
            const items = [makeSubDataSet('q1', 'DS1', { parameters: [param] })];
            const { dataSets } = getSubDataSets(items, [makeQuery('q1')], CONNECTION);
            expect(dataSets[0].parameters[0].value).toBe('');
        });

        it('handles multiple parameters', () => {
            const params = [makeParameter('p1'), makeParameter('p2')];
            const items = [makeSubDataSet('q1', 'DS1', { parameters: params })];
            const { dataSets } = getSubDataSets(items, [makeQuery('q1')], CONNECTION);
            expect(dataSets[0].parameters).toHaveLength(2);
            expect(dataSets[0].connectionDetails).toHaveProperty('p1');
            expect(dataSets[0].connectionDetails).toHaveProperty('p2');
        });

        it('leaves parameters and subDatasetParameters empty when item.parameters is empty', () => {
            const items = [makeSubDataSet('q1', 'DS1', { parameters: [] })];
            const { dataSets } = getSubDataSets(items, [makeQuery('q1')], CONNECTION);
            expect(dataSets[0].parameters).toEqual([]);
            expect(dataSets[0].subDatasetParameters).toEqual([]);
        });
    });

    describe('getSubDataSets — parameters output array', () => {
        it('creates one parameter entry per resolved dataset', () => {
            const items = [makeSubDataSet('q1', 'DS1'), makeSubDataSet('q2', 'DS2')];
            const { parameters } = getSubDataSets(items, [makeQuery('q1'), makeQuery('q2')], CONNECTION);
            expect(parameters).toHaveLength(2);
        });

        it('sets className to "net.sf.jasperreports.engine.JRDataSource"', () => {
            const items = [makeSubDataSet('q1', 'DS1')];
            const { parameters } = getSubDataSets(items, [makeQuery('q1')], CONNECTION);
            expect(parameters[0].className).toBe('net.sf.jasperreports.engine.JRDataSource');
        });

        it('sets name from the dataset name', () => {
            const items = [makeSubDataSet('q1', 'ReportDS')];
            const { parameters } = getSubDataSets(items, [makeQuery('q1')], CONNECTION);
            expect(parameters[0].name).toBe('ReportDS');
        });

        it('attaches the dataset connectionDetails to the parameter entry', () => {
            const items = [makeSubDataSet('q1', 'DS1')];
            const { dataSets, parameters } = getSubDataSets(items, [makeQuery('q1')], CONNECTION);
            expect(parameters[0].connectionDetails).toEqual(dataSets[0].connectionDetails);
        });

        it('returns empty parameters array when all items are filtered out', () => {
            const items = [makeSubDataSet('q1', 'DS1', { isEmpty: true })];
            const { parameters } = getSubDataSets(items, [makeQuery('q1')], CONNECTION);
            expect(parameters).toHaveLength(0);
        });
    });

    describe('getSubDataSets — multiple datasets', () => {
        it('resolves multiple subDataSets independently', () => {
            const items = [
                makeSubDataSet('q1', 'DS1', { fields: [{ id: 'f1', name: 'col1' }] }),
                makeSubDataSet('q2', 'DS2', { fields: [{ id: 'f2', name: 'col2' }] }),
            ];
            const { dataSets } = getSubDataSets(
                items,
                [makeQuery('q1'), makeQuery('q2')],
                CONNECTION
            );
            expect(dataSets).toHaveLength(2);
            expect(dataSets[0].name).toBe('DS1');
            expect(dataSets[1].name).toBe('DS2');
        });

        it('each dataset has its own connectionDetails with correct temp_uuid', () => {
            const items = [makeSubDataSet('q1', 'DS1'), makeSubDataSet('q2', 'DS2')];
            const { dataSets } = getSubDataSets(
                items,
                [makeQuery('q1'), makeQuery('q2')],
                CONNECTION
            );
            expect(dataSets[0].connectionDetails.temp_uuid).toBe('temp-q1');
            expect(dataSets[1].connectionDetails.temp_uuid).toBe('temp-q2');
        });
    });
})


describe("test getCategoryClassNames fn", () => {


    jest.mock('lodash', () => ({
        isEmpty: jest.fn((obj) => Object.keys(obj ?? {}).length === 0),
    }));


    const FULL_CLASS_NAMES = {
        "Double": "java.lang.Double",
        "Float": "java.lang.Float",
        "Integer": "java.lang.Integer",
        "Long": "java.lang.Long",
        "Short": "java.lang.Short",
        "Big Decimal": "java.math.BigDecimal",
        "Time": "java.sql.Time",
        "Boolean": "java.lang.Boolean",
        "Sql Date": "java.sql.Date",
        "Util Date": "java.util.Date",
        "Timestamp": "java.sql.Timestamp",
        "String": "java.lang.String",
    };


    describe('getCategoryClassNames — early returns', () => {
        it('returns an empty array when classNames is null', () => {
            expect(getCategoryClassNames(null)).toEqual([]);
        });

        it('returns an empty array when classNames is undefined', () => {
            expect(getCategoryClassNames(undefined)).toEqual([]);
        });

        it('returns an empty array when classNames is an empty object', () => {
            expect(getCategoryClassNames({})).toEqual([]);
        });

        it('returns an empty array when no argument is passed (default param)', () => {
            expect(getCategoryClassNames()).toEqual([]);
        });
    });


    describe('getCategoryClassNames — return shape', () => {
        let result;
        beforeEach(() => { result = getCategoryClassNames(FULL_CLASS_NAMES); });

        it('returns an array with exactly 3 category groups', () => {
            expect(result).toHaveLength(3);
        });

        it('first category is Numeric', () => {
            expect(result[0]).toMatchObject({ label: 'Numeric', value: 'numeric' });
        });

        it('second category is String', () => {
            expect(result[1]).toMatchObject({ label: 'String', value: 'string' });
        });

        it('third category is Collection', () => {
            expect(result[2]).toMatchObject({ label: 'Collection', value: 'collection' });
        });

        it('each category has a children array', () => {
            result.forEach((cat) => {
                expect(Array.isArray(cat.children)).toBe(true);
            });
        });
    });


    describe('getCategoryClassNames — Numeric category', () => {
        let numeric;
        beforeEach(() => { numeric = getCategoryClassNames(FULL_CLASS_NAMES)[0]; });

        const expectedNumericValues = [
            "java.lang.Double",
            "java.lang.Float",
            "java.lang.Integer",
            "java.lang.Long",
            "java.lang.Short",
            "java.math.BigDecimal"
        ];

        const expectedNumericLabels = [
            "Double",
            "Float",
            "Integer",
            "Long",
            "Short",
            "Big Decimal"
        ];

        it('has exactly 6 children', () => {
            expect(numeric.children).toHaveLength(6);
        });

        it('children labels match the numeric field names', () => {
            const labels = numeric.children.map((c) => c.label);
            expect(labels).toEqual(expectedNumericLabels);
        });

        it('children values match the numeric field names (value = name)', () => {
            const values = numeric.children.map((c) => c.value);
            expect(values).toEqual(expectedNumericValues);
        });

        it.each([
            ['Double'],
            ['Float'],
            ['Integer'],
            ['Long'],
            ['Short'],
            ['Big Decimal'],
        ])('contains child with label "%s"', (label) => {
            expect(numeric.children.some((c) => c.label === label)).toBe(true);
        });
    });


    describe('getCategoryClassNames — String category', () => {
        let string;
        beforeEach(() => { string = getCategoryClassNames(FULL_CLASS_NAMES)[1]; });

        const expectedStringValues = [
            "java.sql.Time",
            "java.sql.Date",
            "java.util.Date",
            "java.sql.Timestamp",
            "java.lang.String",
            "java.lang.Boolean",
        ];


        const expectedStringLabels = [
            "Time",
            "Sql Date",
            "Util Date",
            "Timestamp",
            "String",
            "Boolean",
        ]

        it('has exactly 6 children', () => {
            expect(string.children).toHaveLength(6);
        });

        it('children labels match the string field names', () => {
            const labels = string.children.map((c) => c.label);
            expect(labels).toEqual(expectedStringLabels);
        });

        it('children values match the string field names', () => {
            const values = string.children.map((c) => c.value);
            expect(values).toEqual(expectedStringValues);
        });

        it.each([
            ['Time'],
            ['Sql Date'],
            ['Util Date'],
            ['Timestamp'],
            ['String'],
            ['Boolean'],
        ])('contains child with label "%s"', (label) => {
            expect(string.children.some((c) => c.label === label)).toBe(true);
        });
    });


    describe('getCategoryClassNames — Collection category', () => {
        let collection;
        beforeEach(() => { collection = getCategoryClassNames(FULL_CLASS_NAMES)[2]; });

        it('has exactly 1 child', () => {
            expect(collection.children).toHaveLength(1);
        });

        it('child has label "Collection"', () => {
            expect(collection.children[0].label).toBe('Collection');
        });

        it('child has value "Collection"', () => {
            expect(collection.children[0].value).toBe('java.util.Collection');
        });
    });


    describe('getCategoryClassNames — children option shape', () => {
        it('each child has only label and value keys', () => {
            const result = getCategoryClassNames(FULL_CLASS_NAMES);
            result.forEach((cat) => {
                cat.children.forEach((child) => {
                    expect(Object.keys(child).sort()).toEqual(['label', 'value']);
                });
            });
        });

        it('label and value are both the field name string (not the Java class name)', () => {
            const result = getCategoryClassNames(FULL_CLASS_NAMES);
            const doubleChild = result[0].children.find((c) => c.label === 'Double');
            expect(doubleChild.label).toBe('Double');
            expect(doubleChild.value).toBe('java.lang.Double');
        });
    });


    describe('getCategoryClassNames — partial classNames input', () => {
        it('still returns 3 categories when classNames has only some keys', () => {
            const result = getCategoryClassNames({ Double: 'java.lang.Double' });
            expect(result).toHaveLength(3);
        });

        it('children still appear even when their classNames entry is undefined', () => {
            const result = getCategoryClassNames({ Double: 'java.lang.Double' });
            const numericLabels = result[0].children.map((c) => c.label);
            expect(numericLabels).toContain('Integer');
        });

        it('does not throw when classNames is a non-empty object with unrelated keys', () => {
            expect(() => getCategoryClassNames({ foo: 'bar' })).not.toThrow();
        });
    });
})

describe('updateElementsWithStyles — advancedTable child nodes', () => {

    const makeStyle = (id, styleName) => ({ id, styleName });

    const makeLineNode = (styleName, extra = {}) => ({
        name: 'line',
        lineStyles: { id: 'ls1', styleName, stroke: 1, style: 'SOLID', color: '#000' },
        ...extra,
    });

    const makeStyledNode = (styleName, extra = {}) => ({
        name: 'rect',
        styleName,
        fill: '#FFF',
        ...extra,
    });

    const makeCell = (styleName, styleNameReference = 'ref-1') => ({
        styleName,
        styleNameReference,
        bandType: 'columnData',
    });

    const makeAdvancedTable = (nodes = {}, cells = {}) => ({
        category: 'advancedTable',
        nodes,
        cells,
    });


    describe('updateElementsWithStyles — early returns', () => {
        it('returns original nodes when nodes is empty', () => {
            const result = updateElementsWithStyles(
                [makeStyle('s1', 'Style A')],
                [makeStyle('s1', 'Style A')],
                []
            );
            expect(result).toEqual([]);
        });

        it('returns original nodes when previousStyles is empty', () => {
            const nodes = [makeStyledNode('Style A')];
            const result = updateElementsWithStyles([], [makeStyle('s1', 'Style A')], nodes);
            expect(result).toBe(nodes);
        });

        it('returns original nodes when both nodes and previousStyles are empty', () => {
            expect(updateElementsWithStyles([], [], [])).toEqual([]);
        });

        it('uses default empty arrays when no args are provided', () => {
            expect(updateElementsWithStyles()).toEqual([]);
        });

        it('returns nodes reference unchanged when previousStyles is empty', () => {
            const nodes = [makeStyledNode('S1')];
            const result = updateElementsWithStyles([], [], nodes);
            expect(result).toBe(nodes);
        });
    });


    describe('updateElementsWithStyles — styleName not in previousStyles (always available)', () => {
        it('preserves styleName on a node whose styleName was never in previousStyles', () => {
            const prev = [makeStyle('s1', 'Old Style')];
            const updated = [makeStyle('s1', 'Old Style')];
            const node = makeStyledNode('Brand New Style');

            const [result] = updateElementsWithStyles(prev, updated, [node]);
            expect(result.styleName).toBe('Brand New Style');
        });
    });

    describe('updateElementsWithStyles — styleName in both prev and updated', () => {
        it('preserves styleName when it exists in both previousStyles and updatedStyle', () => {
            const prev = [makeStyle('s1', 'Style A')];
            const updated = [makeStyle('s1', 'Style A')];
            const node = makeStyledNode('Style A');

            const [result] = updateElementsWithStyles(prev, updated, [node]);
            expect(result.styleName).toBe('Style A');
        });
    });

    describe('updateElementsWithStyles — styleName in prev but not in updated', () => {
        it('removes styleName from a node when style was deleted (in prev but not updated)', () => {
            const prev = [makeStyle('s1', 'Style A')];
            const updated = [];
            const node = makeStyledNode('Style A');

            const [result] = updateElementsWithStyles(prev, updated, [node]);
            expect(result).toHaveProperty('styleName');
        });

        it('preserves other node properties when styleName is deleted', () => {
            const prev = [makeStyle('s1', 'Style A')];
            const updated = [];
            const node = makeStyledNode('Style A', { fill: '#123456', fontFill: '#000' });

            const [result] = updateElementsWithStyles(prev, updated, [node]);
            expect(result.fill).toBe('#fefefe');
            expect(result.fontFill).toBe('#000000');
        });

        it('does not delete styleName when it is falsy/absent', () => {
            const prev = [makeStyle('s1', 'Style A')];
            const updated = [];
            const node = { name: 'rect', fill: '#FFF' }; // no styleName

            const [result] = updateElementsWithStyles(prev, updated, [node]);
            expect(result).not.toHaveProperty('styleName');
            expect(result.fill).toBe('#FFF');
        });
    });


    describe('updateElementsWithStyles — line nodes', () => {

        it('preserves remaining lineStyles fields after stripping', () => {
            const prev = [makeStyle('s1', 'Line Style')];
            const updated = [];
            const node = makeLineNode('Line Style');

            const [result] = updateElementsWithStyles(prev, updated, [node]);
            expect(result.lineStyles.stroke).toBe(1);
            expect(result.lineStyles.style).toBe('Solid');
            expect(result.lineStyles.color).toBe('#000000');
        });

        it('preserves lineStyles.styleName when style exists in both prev and updated', () => {
            const prev = [makeStyle('s1', 'Line Style')];
            const updated = [makeStyle('s1', 'Line Style')];
            const node = makeLineNode('Line Style');

            const [result] = updateElementsWithStyles(prev, updated, [node]);
            expect(result.lineStyles.styleName).toBe('Line Style');
        });

        it('preserves lineStyles when lineStyles.styleName is absent', () => {
            const prev = [makeStyle('s1', 'Line Style')];
            const updated = [];
            const node = {
                name: 'line',
                lineStyles: { stroke: 2, style: 'DASHED', color: '#FFF' },
            };

            const [result] = updateElementsWithStyles(prev, updated, [node]);
            expect(result.lineStyles).toEqual({ stroke: 2, style: 'DASHED', color: '#FFF' });
        });

        it('preserves top-level line node properties outside lineStyles', () => {
            const prev = [makeStyle('s1', 'Line Style')];
            const updated = [];
            const node = makeLineNode('Line Style', { category: 'border', visible: true });

            const [result] = updateElementsWithStyles(prev, updated, [node]);
            expect(result.category).toBe('border');
            expect(result.visible).toBe(true);
        });
    });

    describe('updateElementsWithStyles — advancedTable child nodes', () => {
        it('removes styleName from a child node when style is deleted', () => {
            const prev = [makeStyle('s1', 'Cell Style')];
            const updated = [];
            const table = makeAdvancedTable(
                { n1: makeStyledNode('Cell Style') },
                {}
            );

            const [result] = updateElementsWithStyles(prev, updated, [table]);
            expect(result.nodes.n1.styleName).toBe("None");
        });

        it('preserves styleName on a child node when style exists in both', () => {
            const prev = [makeStyle('s1', 'Cell Style')];
            const updated = [makeStyle('s1', 'Cell Style')];
            const table = makeAdvancedTable(
                { n1: makeStyledNode('Cell Style') },
                {}
            );

            const [result] = updateElementsWithStyles(prev, updated, [table]);
            expect(result.nodes.n1.styleName).toBe('Cell Style');
        });

        it('handles line nodes inside advancedTable.nodes', () => {
            const prev = [makeStyle('s1', 'Line Style')];
            const updated = [];
            const table = makeAdvancedTable(
                { n1: makeLineNode('Line Style') },
                {}
            );

            const [result] = updateElementsWithStyles(prev, updated, [table]);
            expect(result.nodes.n1.lineStyles.styleName).toBe("None");
        });

        it('handles multiple child nodes with mixed styles', () => {
            const prev = [makeStyle('s1', 'Style A'), makeStyle('s2', 'Style B')];
            const updated = [makeStyle('s1', 'Style A')];
            const table = makeAdvancedTable(
                {
                    n1: makeStyledNode('Style A'),
                    n2: makeStyledNode('Style B'),
                },
                {}
            );

            const [result] = updateElementsWithStyles(prev, updated, [table]);
            expect(result.nodes.n1.styleName).toBe('Style A');
            expect(result.nodes.n2.styleName).toBe('None');
        });

        it('handles empty advancedTable.nodes without error', () => {
            const prev = [makeStyle('s1', 'Style A')];
            const updated = [];
            const table = makeAdvancedTable({}, {});
            expect(() => updateElementsWithStyles(prev, updated, [table])).not.toThrow();
        });
    });


    describe('updateElementsWithStyles — advancedTable cells', () => {
        it('sets styleNameReference to undefined when cell styleName is removed', () => {
            const prev = [makeStyle('s1', 'Cell Style')];
            const updated = [];
            const table = makeAdvancedTable(
                {},
                { c1: makeCell('Cell Style', 'ref-abc') }
            );

            const [result] = updateElementsWithStyles(prev, updated, [table]);
            expect(result.cells.c1.styleNameReference).toBeUndefined();
        });

        it('preserves styleNameReference when cell styleName exists in both', () => {
            const prev = [makeStyle('s1', 'Cell Style')];
            const updated = [makeStyle('s1', 'Cell Style')];
            const table = makeAdvancedTable(
                {},
                { c1: makeCell('Cell Style', 'ref-abc') }
            );

            const [result] = updateElementsWithStyles(prev, updated, [table]);
            expect(result.cells.c1.styleNameReference).toBe('ref-abc');
        });

        it('preserves other cell fields when styleNameReference is cleared', () => {
            const prev = [makeStyle('s1', 'Cell Style')];
            const updated = [];
            const table = makeAdvancedTable(
                {},
                { c1: makeCell('Cell Style', 'ref-abc') }
            );

            const [result] = updateElementsWithStyles(prev, updated, [table]);
            expect(result.cells.c1.bandType).toBe('columnData');
            expect(result.cells.c1.styleName).toBe('Cell Style');
        });

        it('does not clear styleNameReference when cell has no styleName', () => {
            const prev = [makeStyle('s1', 'Cell Style')];
            const updated = [];
            const table = makeAdvancedTable(
                {},
                { c1: { styleNameReference: 'ref-abc', bandType: 'columnData' } } // no styleName
            );

            const [result] = updateElementsWithStyles(prev, updated, [table]);
            expect(result.cells.c1.styleNameReference).toBe('ref-abc');
        });

        it('handles multiple cells with mixed style availability', () => {
            const prev = [makeStyle('s1', 'Style A'), makeStyle('s2', 'Style B')];
            const updated = [makeStyle('s1', 'Style A')]; // Style B deleted
            const table = makeAdvancedTable(
                {},
                {
                    c1: makeCell('Style A', 'ref-1'),
                    c2: makeCell('Style B', 'ref-2'),
                }
            );

            const [result] = updateElementsWithStyles(prev, updated, [table]);
            expect(result.cells.c1.styleNameReference).toBe('ref-1');     // preserved
            expect(result.cells.c2.styleNameReference).toBeUndefined();   // cleared
        });

        it('handles empty advancedTable.cells without error', () => {
            const prev = [makeStyle('s1', 'Style A')];
            const updated = [];
            const table = makeAdvancedTable({}, {});
            expect(() => updateElementsWithStyles(prev, updated, [table])).not.toThrow();
        });
    });


    describe('updateElementsWithStyles — mixed node types', () => {
        it('processes advancedTable and non-advancedTable nodes in one pass', () => {
            const prev = [makeStyle('s1', 'Style A')];
            const updated = [];
            const directNode = makeStyledNode('Style A');
            const table = makeAdvancedTable(
                {},
                { c1: makeCell('Style A', 'ref-1') }
            );

            const result = updateElementsWithStyles(prev, updated, [directNode, table]);
            expect(result[0]).toHaveProperty('styleName');
        });

        it('preserves node order in the returned array', () => {
            const prev = [makeStyle('s1', 'Style A')];
            const updated = [makeStyle('s1', 'Style A')];
            const nodes = [
                makeStyledNode('Style A', { id: 'n1' }),
                makeAdvancedTable({}, {}),
                makeStyledNode('Style A', { id: 'n3' }),
            ];

            const result = updateElementsWithStyles(prev, updated, nodes);
            expect(result[0].id).toBe('n1');
            expect(result[2].id).toBe('n3');
        });

        it('returns the same number of nodes as provided', () => {
            const prev = [makeStyle('s1', 'Style A')];
            const updated = [makeStyle('s1', 'Style A')];
            const nodes = [makeStyledNode('Style A'), makeAdvancedTable({}, {}), makeLineNode('Style A')];
            expect(updateElementsWithStyles(prev, updated, nodes)).toHaveLength(3);
        });
    });


    describe('updateElementsWithStyles — immutability', () => {
        it('does not mutate the original non-advancedTable node reference', () => {
            const prev = [makeStyle('s1', 'Style A')];
            const updated = [];
            const node = makeStyledNode('Style A');
            const originalStyleName = node.styleName;

            updateElementsWithStyles(prev, updated, [node]);
            expect(node.styleName).toBe(originalStyleName);
        });
    });
})

describe('test getActiveSubDSParameterType fn', () => {
    describe('getActiveSubDSParameterType — early returns', () => {
        it('returns null when type is null', () => {
            expect(getActiveSubDSParameterType(null)).toBeNull();
        });

        it('returns undefined when type is undefined', () => {
            expect(getActiveSubDSParameterType(undefined)).toBeUndefined();
        });

        it('returns undefined when no argument is passed', () => {
            expect(getActiveSubDSParameterType()).toBeUndefined();
        });

        it('returns 0 when type is 0 (falsy passthrough)', () => {
            expect(getActiveSubDSParameterType(0)).toBe(0);
        });

        it('returns false when type is false (falsy passthrough)', () => {
            expect(getActiveSubDSParameterType(false)).toBe(false);
        });
    });


    describe('getActiveSubDSParameterType — Numeric category', () => {
        const numericDefault = 'java.lang.Integer';

        it.each([
            ['java.lang.Double'],
            ['java.lang.Float'],
            ['java.lang.Integer'],
            ['java.lang.Long'],
            ['java.lang.Short'],
            ['java.math.BigDecimal'],
        ])('returns "%s" for type "%s"', (type) => {
            expect(getActiveSubDSParameterType(type)).toBe(numericDefault);
        });
    });


    describe('getActiveSubDSParameterType — String category', () => {
        const stringDefault = 'java.lang.String';

        it.each([
            ['java.sql.Time'],
            ['java.sql.Date'],
            ['java.util.Date'],
            ['java.sql.Timestamp'],
            ['java.lang.String'],
            ['java.lang.Boolean'],
        ])('returns "%s" for type "%s"', (type) => {
            expect(getActiveSubDSParameterType(type)).toBe(stringDefault);
        });
    });


    describe('getActiveSubDSParameterType — Collection category', () => {
        it('returns "java.util.Collection" for type "java.util.Collection"', () => {
            expect(getActiveSubDSParameterType('java.util.Collection')).toBe('java.util.Collection');
        });
    });


    describe('getActiveSubDSParameterType — unrecognised types', () => {
        it('returns empty string for a random unknown type', () => {
            expect(getActiveSubDSParameterType('java.lang.Object')).toBe('');
        });

        it('returns empty string for a raw category key like "Integer" (not the Java class name)', () => {
            expect(getActiveSubDSParameterType('Integer')).toBe('');
        });

        it('returns empty string for an empty string type', () => {
            expect(getActiveSubDSParameterType('')).toBe('');
        });

        it('returns empty string for a partial class name', () => {
            expect(getActiveSubDSParameterType('java.lang')).toBe('');
        });

        it('returns empty string for a class name with wrong casing', () => {
            expect(getActiveSubDSParameterType('Java.Lang.Integer')).toBe('');
        });

        it('returns empty string for a numeric value that is not a valid class', () => {
            expect(getActiveSubDSParameterType(42)).toBe('');
        });
    });


    describe('getActiveSubDSParameterType — category defaults', () => {
        it('Numeric default is "java.lang.Integer"', () => {
            expect(getActiveSubDSParameterType('java.lang.Double')).toBe('java.lang.Integer');
        });

        it('String default is "java.lang.String"', () => {
            expect(getActiveSubDSParameterType('java.lang.Boolean')).toBe('java.lang.String');
        });

        it('Collection default is "java.util.Collection"', () => {
            expect(getActiveSubDSParameterType('java.util.Collection')).toBe('java.util.Collection');
        });

        it('each category returns its own default, not a cross-category default', () => {
            expect(getActiveSubDSParameterType('java.lang.Double')).not.toBe('java.lang.String');
            expect(getActiveSubDSParameterType('java.lang.Boolean')).not.toBe('java.lang.Integer');
        });
    });
})

describe('test getMappedParameter fn', () => {


    describe('getMappedParameters — early returns', () => {
        it('returns an empty array when parameters is empty', () => {
            expect(getMappedParameters([])).toEqual([]);
        });

        it('returns an empty array when no argument is passed (default param)', () => {
            expect(getMappedParameters()).toEqual([]);
        });
    });


    describe('getMappedParameters — return shape', () => {
        it('returns an array', () => {
            const result = getMappedParameters([{ id: '1', name: 'p1', mappingExpression: 'expr1' }]);
            expect(Array.isArray(result)).toBe(true);
        });

        it('returns one mapped object per input parameter', () => {
            const params = [
                { id: '1', name: 'p1', mappingExpression: 'expr1' },
                { id: '2', name: 'p2', mappingExpression: 'expr2' },
            ];
            expect(getMappedParameters(params)).toHaveLength(2);
        });

        it('returns a new array (not the original reference)', () => {
            const params = [{ id: '1', name: 'p1', mappingExpression: 'expr1' }];
            expect(getMappedParameters(params)).not.toBe(params);
        });

        it('each mapped object has exactly the keys parameter, id, expression', () => {
            const result = getMappedParameters([{ id: '1', name: 'p1', mappingExpression: 'expr1' }]);
            expect(Object.keys(result[0]).sort()).toEqual(['expression', 'id', 'parameter']);
        });
    });


    describe('getMappedParameters — field mapping', () => {
        it('maps name to parameter', () => {
            const result = getMappedParameters([{ id: '1', name: 'myParam', mappingExpression: 'expr' }]);
            expect(result[0].parameter).toBe('myParam');
        });

        it('maps id directly', () => {
            const result = getMappedParameters([{ id: 'abc-123', name: 'p1', mappingExpression: 'expr' }]);
            expect(result[0].id).toBe('abc-123');
        });

        it('maps mappingExpression to expression', () => {
            const result = getMappedParameters([{ id: '1', name: 'p1', mappingExpression: '$P{value}' }]);
            expect(result[0].expression).toBe('$P{value}');
        });

        it('does not include extra/unrelated fields from the input object', () => {
            const result = getMappedParameters([
                { id: '1', name: 'p1', mappingExpression: 'expr', type: 'java.lang.String', extra: 'ignored' },
            ]);
            expect(result[0]).not.toHaveProperty('type');
            expect(result[0]).not.toHaveProperty('extra');
            expect(result[0]).not.toHaveProperty('name');
            expect(result[0]).not.toHaveProperty('mappingExpression');
        });
    });


    describe('getMappedParameters — multiple parameters', () => {
        it('maps each parameter independently and preserves order', () => {
            const params = [
                { id: '1', name: 'first', mappingExpression: 'expr1' },
                { id: '2', name: 'second', mappingExpression: 'expr2' },
                { id: '3', name: 'third', mappingExpression: 'expr3' },
            ];
            const result = getMappedParameters(params);
            expect(result).toEqual([
                { parameter: 'first', id: '1', expression: 'expr1' },
                { parameter: 'second', id: '2', expression: 'expr2' },
                { parameter: 'third', id: '3', expression: 'expr3' },
            ]);
        });
    });


    describe('getMappedParameters — missing or falsy fields', () => {
        it('sets parameter to undefined when name is missing', () => {
            const result = getMappedParameters([{ id: '1', mappingExpression: 'expr' }]);
            expect(result[0].parameter).toBeUndefined();
        });

        it('sets id to undefined when id is missing', () => {
            const result = getMappedParameters([{ name: 'p1', mappingExpression: 'expr' }]);
            expect(result[0].id).toBeUndefined();
        });

        it('sets expression to undefined when mappingExpression is missing', () => {
            const result = getMappedParameters([{ id: '1', name: 'p1' }]);
            expect(result[0].expression).toBeUndefined();
        });

        it('handles an entirely empty object in the array', () => {
            const result = getMappedParameters([{}]);
            expect(result[0]).toEqual({ parameter: undefined, id: undefined, expression: undefined });
        });

        it('handles empty string values correctly (not treated as missing)', () => {
            const result = getMappedParameters([{ id: '', name: '', mappingExpression: '' }]);
            expect(result[0]).toEqual({ parameter: '', id: '', expression: '' });
        });

        it('handles null values for fields', () => {
            const result = getMappedParameters([{ id: null, name: null, mappingExpression: null }]);
            expect(result[0]).toEqual({ parameter: null, id: null, expression: null });
        });
    });

    describe('getMappedParameters — immutability', () => {
        it('does not mutate the original parameters array', () => {
            const params = [{ id: '1', name: 'p1', mappingExpression: 'expr1' }];
            const copy = JSON.parse(JSON.stringify(params));
            getMappedParameters(params);
            expect(params).toEqual(copy);
        });

        it('does not mutate individual parameter objects', () => {
            const param = { id: '1', name: 'p1', mappingExpression: 'expr1' };
            getMappedParameters([param]);
            expect(param).toEqual({ id: '1', name: 'p1', mappingExpression: 'expr1' });
        });
    });
})



describe('test getParentKeys fn', () => {
    const mockTree = [
        {
            title: 'Styles',
            key: 'styles',
            children: [
                { title: 'TABLE_TH', key: '0fc54519-style', children: [] },
            ],
        },
        {
            title: 'Dataset',
            key: 'dataset',
            children: [
                {
                    title: 'Fields',
                    key: 'fields',
                    children: [
                        { title: 'Field 1', key: 'bd178c96-field-1', children: [] },
                    ],
                },
            ],
        },
        {
            title: 'Table',
            key: 'node-edd2d6ea-4b3c-40ca-9413-5bb0df188018',
            children: [
                {
                    title: 'Table Header',
                    key: 'tableHeader',
                    children: [
                        {
                            title: 'Column 1',
                            key: 'cce4711f-6237-4945-a741-459867197e0a-tableHeader',
                            children: [],
                        },
                    ],
                },
                {
                    title: 'Column Header',
                    key: 'columnHeaderOfTable',
                    children: [
                        {
                            title: 'Column 1',
                            key: 'cce4711f-6237-4945-a741-459867197e0a-columnHeaderOfTable',
                            children: [
                                {
                                    title: '$F{Field 1}',
                                    key: 'node-5c3961e2-062f-4d73-a951-298e6daba0f3',
                                },
                            ],
                        },
                    ],
                },
            ],
        },
    ];

    test('returns ancestor keys for a key nested 2 levels deep (table -> band -> cell)', () => {
        const result = getParentKeys(
            mockTree,
            'cce4711f-6237-4945-a741-459867197e0a-tableHeader'
        );
        expect(result).toEqual([
            'node-edd2d6ea-4b3c-40ca-9413-5bb0df188018',
            'tableHeader',
        ]);
    });

    test('returns a single ancestor for a key nested 1 level deep (table -> band)', () => {
        const result = getParentKeys(mockTree, 'tableHeader');
        expect(result).toEqual(['node-edd2d6ea-4b3c-40ca-9413-5bb0df188018']);
    });

    test('returns an empty array for a root-level key', () => {
        const result = getParentKeys(mockTree, 'styles');
        expect(result).toEqual([]);
    });

    test('returns null for a key that does not exist anywhere in the tree', () => {
        const result = getParentKeys(mockTree, 'this-key-does-not-exist');
        expect(result).toBeNull();
    });

    test('finds a key nested in a different branch (dataset -> fields -> field item)', () => {
        const result = getParentKeys(mockTree, 'bd178c96-field-1');
        expect(result).toEqual(['dataset', 'fields']);
    });

    test('handles leaf nodes that have no `children` property at all', () => {
        const result = getParentKeys(
            mockTree,
            'node-5c3961e2-062f-4d73-a951-298e6daba0f3'
        );
        expect(result).toEqual([
            'node-edd2d6ea-4b3c-40ca-9413-5bb0df188018',
            'columnHeaderOfTable',
            'cce4711f-6237-4945-a741-459867197e0a-columnHeaderOfTable',
        ]);
    });

    test('returns null when given an empty tree', () => {
        const result = getParentKeys([], 'anything');
        expect(result).toBeNull();
    });

    test('does not leak state between separate calls', () => {
        const first = getParentKeys(mockTree, 'tableHeader');
        const second = getParentKeys(mockTree, 'this-key-does-not-exist');
        const third = getParentKeys(mockTree, 'bd178c96-field-1');

        expect(first).toEqual(['node-edd2d6ea-4b3c-40ca-9413-5bb0df188018']);
        expect(second).toBeNull();
        expect(third).toEqual(['dataset', 'fields']);
    });

    test('does not mutate the original tree', () => {
        const treeCopy = JSON.parse(JSON.stringify(mockTree));
        getParentKeys(mockTree, 'cce4711f-6237-4945-a741-459867197e0a-tableHeader');
        expect(mockTree).toEqual(treeCopy);
    });
});