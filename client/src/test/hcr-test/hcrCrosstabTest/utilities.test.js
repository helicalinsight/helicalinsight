import { v4 as uuidv4 } from 'uuid';
import { getCrosstabLayout, getCrosstabOutlineData, getMeasureCellsByCellIndex } from "../../../components/hi-canned-reports/hcrCanvas/advanceComponents/utils";
import { calculateCTTotalHeightAndWidth, getActualField, getCTColumnGroups, getCTComputedLayout, getCTMeasureCells, getCTMeasures, getCTRowGroups, getCTStaticTextNode, getCTStylesReferences, getCTTextNode, getNestedArr, getNextItemsLength } from "../../../components/hi-canned-reports/hcrCanvas/hcrCanvasPaneHelperMethods";
import { createHCRCrosstabGrid } from "../../../components/hi-canned-reports/hcrCanvas/hcrCrossTab/utilities";

describe('test createHCRCrosstabGrid fn', () => {
    it('should return empty grid', () => {
        const grid = createHCRCrosstabGrid();
        expect(grid.length).toBe(0);
        const gridWhenColumnsAreEmpty = createHCRCrosstabGrid(['row1', 'row2'], [], ['measure1']);
        expect(gridWhenColumnsAreEmpty.length).toBe(0);
        const gridWhenRowsAreEmpty = createHCRCrosstabGrid([], ['col1', 'col2'], ['measure1']);
        expect(gridWhenRowsAreEmpty.length).toBe(0);
        const gridWhenRowAndColumnsAreEmpty = createHCRCrosstabGrid([], [], ['measure1']);
        expect(gridWhenRowAndColumnsAreEmpty.length).toBe(0);
    });

    it('should create a grid with custom rows, columns, and measures', () => {
        const grid = createHCRCrosstabGrid(['row1'], ['col1'], ['measure1']);
        expect(grid.length).toBe(2);
        expect(grid[0].length).toBe(2);
        expect(grid[1].length).toBe(2);
        expect(grid[0][0]).toBe('0-0');
        expect(grid[0][1]).toBe('0-1');
        expect(grid[1][0]).toBe('1-0');
        expect(grid[1][1]).toBe('1-1');
    });
});



describe('test getNestedArr Fn', () => {
    it('should return an empty array if input array is empty', () => {
        expect(getNestedArr([])).toEqual([]);
    });

    it('should return the input array if it has only one element', () => {
        expect(getNestedArr([1])).toEqual([1]);
    });

    it('should return a nested array if the input array has more than one element', () => {
        expect(getNestedArr([1, 2, 3])).toEqual([1, [2, [3]]]);
    });

    it('should handle arrays with mixed types', () => {
        expect(getNestedArr([1, 'two', { three: 3 }])).toEqual([1, ['two', [{ three: 3 }]]]);
    });
});

describe('test getNextItemsLength fn', () => {
    it('should return 0 when the input array is empty', () => {
        expect(getNextItemsLength([])).toBe(0);
    });

    it('should return the length of the input array when it contains only one level of elements', () => {
        expect(getNextItemsLength([1, 2, 3])).toBe(3);
    });

    it('should return the length of the flattened array when the input array contains nested elements', () => {
        expect(getNextItemsLength([1, [2, 3], [4]])).toBe(4);
    });
});

jest.mock('uuid', () => ({
    v4: jest.fn()
}));

describe('CT Column Groups Utilities', () => {
    beforeEach(() => {
        let counter = 0;
        uuidv4.mockImplementation(() => `uuid-${counter++}`);
    });

    afterEach(() => {
        jest.clearAllMocks();
    });

    describe('getCTStylesReferences', () => {
        it('returns ids for CH, CT, CG, CD when all styles are present', () => {
            const styles = [
                { styleName: 'CH_Style', id: 'ch-id' },
                { styleName: 'CT_Style', id: 'ct-id' },
                { styleName: 'CG_Style', id: 'cg-id' },
                { styleName: 'CD_Style', id: 'cd-id' }
            ];
            const result = getCTStylesReferences(styles);
            expect(result).toEqual({
                CH: 'ch-id',
                CT: 'ct-id',
                CG: 'cg-id',
                CD: 'cd-id'
            });
        });

        it('returns undefined for style types that are not found', () => {
            const styles = [{ styleName: 'CH_Style', id: 'ch-id' }];
            const result = getCTStylesReferences(styles);
            expect(result).toEqual({
                CH: 'ch-id',
                CT: undefined,
                CG: undefined,
                CD: undefined
            });
        });

        it('returns all undefined for an empty styles array', () => {
            const result = getCTStylesReferences([]);
            expect(result).toEqual({
                CH: undefined,
                CT: undefined,
                CG: undefined,
                CD: undefined
            });
        });

        it('returns the first matching style when multiple styles match the same substring', () => {
            const styles = [
                { styleName: 'CH_First', id: 'first-id' },
                { styleName: 'CH_Second', id: 'second-id' }
            ];
            const result = getCTStylesReferences(styles);
            expect(result.CH).toBe('first-id');
        });

        it('matches style names that contain the substring anywhere within them', () => {
            const styles = [{ styleName: 'MyCHARTStyle', id: 'chart-id' }];
            const result = getCTStylesReferences(styles);
            expect(result.CH).toBe('chart-id');
        });
    });

    describe('getActualField', () => {
        it('returns the field matching the given name', () => {
            const fields = [{ name: 'field1' }, { name: 'field2' }];
            const result = getActualField(fields, 'field2');
            expect(result).toEqual({ name: 'field2' });
        });

        it('returns undefined when no field matches the given name', () => {
            const fields = [{ name: 'field1' }];
            const result = getActualField(fields, 'nonExistentField');
            expect(result).toBeUndefined();
        });

        it('returns undefined when fields is not provided', () => {
            const result = getActualField(undefined, 'field1');
            expect(result).toBeUndefined();
        });

        it('returns undefined when fields is an empty array', () => {
            const result = getActualField([], 'field1');
            expect(result).toBeUndefined();
        });

        it('returns the first matching field when duplicates exist', () => {
            const fields = [
                { name: 'field1', clazz: 'first' },
                { name: 'field1', clazz: 'second' }
            ];
            const result = getActualField(fields, 'field1');
            expect(result.clazz).toBe('first');
        });
    });

    describe('getCTTextNode', () => {
        it('returns a text node with the expected structure', () => {
            const result = getCTTextNode({
                width: 100,
                height: 50,
                cellId: 'cell-1',
                label: '$V{field1}',
                value: 'field1',
                actualField: { clazz: 'java.lang.String' }
            });

            expect(result).toEqual({
                id: 'uuid-0',
                name: 'field1',
                value: 'field1',
                width: 100,
                height: 50,
                label: '$V{field1}',
                renderKey: 'text',
                isLeaf: true,
                zIndex: 10,
                fontSize: 10,
                type: 'queryField',
                category: 'text',
                repeat: 'rd',
                borders: {},
                padding: {},
                backendDataType: 'java.lang.String',
                x: 0,
                y: 0,
                cellId: 'cell-1'
            });
        });

        it('defaults backendDataType to an empty string when actualField is not provided', () => {
            const result = getCTTextNode({
                width: 100,
                height: 50,
                cellId: 'cell-1',
                label: 'label',
                value: 'value'
            });
            expect(result.backendDataType).toBe('');
        });

        it('defaults backendDataType to an empty string when actualField has no clazz property', () => {
            const result = getCTTextNode({
                width: 100,
                height: 50,
                cellId: 'cell-1',
                label: 'label',
                value: 'value',
                actualField: {}
            });
            expect(result.backendDataType).toBe('');
        });

        it('merges and overrides default properties with otherProps', () => {
            const result = getCTTextNode({
                width: 100,
                height: 50,
                cellId: 'cell-1',
                label: 'label',
                value: 'value',
                otherProps: { zIndex: 99, custom: 'customValue' }
            });
            expect(result.zIndex).toBe(99);
            expect(result.custom).toBe('customValue');
        });

        it('generates the id using uuidv4', () => {
            const result = getCTTextNode({
                width: 100,
                height: 50,
                cellId: 'cell-1',
                label: 'label',
                value: 'value'
            });
            expect(uuidv4).toHaveBeenCalled();
            expect(result.id).toBe('uuid-0');
        });
    });

    describe('getCTStaticTextNode', () => {
        it('returns a static text node with the expected structure', () => {
            const result = getCTStaticTextNode({
                width: 50,
                height: 25,
                cellId: 'cell-2',
                label: 'Total field1',
                value: 'field1'
            });

            expect(result).toEqual({
                id: 'uuid-0',
                label: 'Total field1',
                value: 'field1',
                borders: {},
                padding: {},
                width: 50,
                height: 25,
                name: 'text',
                renderKey: 'text',
                parentKey: 'elements',
                isLeaf: true,
                repeat: 'na',
                category: 'text',
                zIndex: 10,
                type: 'defaultNodes',
                fontSize: 10,
                x: 0,
                y: 0,
                cellId: 'cell-2',
                static: true
            });
        });

        it('always sets name to "text" regardless of the provided value', () => {
            const result = getCTStaticTextNode({
                width: 50,
                height: 25,
                cellId: 'cell-2',
                label: 'label',
                value: 'someOtherValue'
            });
            expect(result.name).toBe('text');
        });

        it('always sets static to true', () => {
            const result = getCTStaticTextNode({
                width: 50,
                height: 25,
                cellId: 'cell-2',
                label: 'label',
                value: 'value'
            });
            expect(result.static).toBe(true);
        });

        it('generates the id using uuidv4', () => {
            const result = getCTStaticTextNode({
                width: 50,
                height: 25,
                cellId: 'cell-2',
                label: 'label',
                value: 'value'
            });
            expect(uuidv4).toHaveBeenCalled();
            expect(result.id).toBe('uuid-0');
        });
    });

    describe('getCTColumnGroups', () => {
        it('returns empty columnGroups and nodes when columnFields is an empty array', () => {
            const result = getCTColumnGroups([], [], []);
            expect(result).toEqual({ columnGroups: [], nodes: [] });
        });

        it('returns empty columnGroups and nodes when called with no arguments', () => {
            const result = getCTColumnGroups();
            expect(result).toEqual({ columnGroups: [], nodes: [] });
        });

        it('creates a columnGroup and corresponding nodes for a single column field', () => {
            const columnFields = ['field1'];
            const fields = [{ name: 'field1', clazz: 'java.lang.String' }];
            const styles = [
                { styleName: 'CH_Style', id: 'ch-id' },
                { styleName: 'CT_Style', id: 'ct-id' },
                { styleName: 'CG_Style', id: 'cg-id' }
            ];

            const { columnGroups, nodes } = getCTColumnGroups(columnFields, fields, styles);

            expect(columnGroups).toHaveLength(1);
            expect(nodes).toHaveLength(2);

            const group = columnGroups[0];
            expect(group.name).toBe('field1');
            expect(group.label).toBe('field1');
            expect(group.type).toBe('columnGroup');
            expect(group.headerCellWidth).toBe(50);
            expect(group.totalCellHeight).toBe(25);
            expect(group.className).toBe('java.lang.String');
            expect(group.expression).toBe('$F{field1}');
            expect(group.cells).toHaveLength(2);

            const [headerCell, totalCell] = group.cells;
            expect(headerCell.width).toBe(50);
            expect(headerCell.height).toBe(25);
            expect(headerCell.className).toBe('java.lang.String');
            expect(headerCell.styleNameReference).toBe('ch-id');
            expect(headerCell.nodeIds).toEqual([nodes[0].id]);

            expect(totalCell.width).toBe(50);
            expect(totalCell.height).toBe(25);
            expect(totalCell.styleNameReference).toBe('ct-id');
            expect(totalCell.nodeIds).toEqual([nodes[1].id]);
        });

        it('calculates increasing header width and total height for earlier fields with multiple column fields', () => {
            const columnFields = ['field1', 'field2', 'field3'];
            const fields = [
                { name: 'field1', clazz: 'java.lang.String' },
                { name: 'field2', clazz: 'java.lang.String' },
                { name: 'field3', clazz: 'java.lang.String' }
            ];

            const { columnGroups } = getCTColumnGroups(columnFields, fields, []);

            expect(columnGroups[0].headerCellWidth).toBe(150);
            expect(columnGroups[0].totalCellHeight).toBe(75);
            expect(columnGroups[1].headerCellWidth).toBe(100);
            expect(columnGroups[1].totalCellHeight).toBe(50);
            expect(columnGroups[2].headerCellWidth).toBe(50);
            expect(columnGroups[2].totalCellHeight).toBe(25);
        });

        it('assigns CT styleNameReference to the total cell of the first field and CG for subsequent fields', () => {
            const columnFields = ['field1', 'field2'];
            const fields = [
                { name: 'field1', clazz: 'java.lang.String' },
                { name: 'field2', clazz: 'java.lang.String' }
            ];
            const styles = [
                { styleName: 'CT_Style', id: 'ct-id' },
                { styleName: 'CG_Style', id: 'cg-id' }
            ];

            const { columnGroups } = getCTColumnGroups(columnFields, fields, styles);

            expect(columnGroups[0].cells[1].styleNameReference).toBe('ct-id');
            expect(columnGroups[1].cells[1].styleNameReference).toBe('cg-id');
        });

        it('throws when no matching field is found for a column field', () => {
            const columnFields = ['unknownField'];
            const fields = [{ name: 'otherField', clazz: 'java.lang.Integer' }];

            expect(() => getCTColumnGroups(columnFields, fields, [])).toThrow();
        });

        it('produces two nodes per column field in the nodes array', () => {
            const columnFields = ['field1', 'field2', 'field3'];
            const fields = [
                { name: 'field1', clazz: 'java.lang.String' },
                { name: 'field2', clazz: 'java.lang.String' },
                { name: 'field3', clazz: 'java.lang.String' }
            ];

            const { nodes } = getCTColumnGroups(columnFields, fields, []);
            expect(nodes).toHaveLength(6);
        });

        it('generates unique cell and node ids for each column group using uuidv4', () => {
            const columnFields = ['field1', 'field2'];
            const fields = [
                { name: 'field1', clazz: 'java.lang.String' },
                { name: 'field2', clazz: 'java.lang.String' }
            ];

            const { columnGroups, nodes } = getCTColumnGroups(columnFields, fields, []);
            const allIds = [
                ...columnGroups.map((group) => group.id),
                ...columnGroups.flatMap((group) => group.cells.map((cell) => cell.id)),
                ...nodes.map((node) => node.id)
            ];
            const uniqueIds = new Set(allIds);
            expect(uniqueIds.size).toBe(allIds.length);
        });
    });

    describe('getCTRowGroups', () => {
        it('returns empty rowGroups and nodes when rowFields is an empty array', () => {
            const result = getCTRowGroups([], [], [], []);
            expect(result).toEqual({ rowGroups: [], nodes: [] });
        });

        it('returns empty rowGroups and nodes when called with no arguments', () => {
            const result = getCTRowGroups();
            expect(result).toEqual({ rowGroups: [], nodes: [] });
        });

        it('creates a rowGroup and corresponding nodes for a single row field with no measures', () => {
            const rowFields = ['field1'];
            const fields = [{ name: 'field1', clazz: 'java.lang.String' }];
            const styles = [
                { styleName: 'CH_Style', id: 'ch-id' },
                { styleName: 'CT_Style', id: 'ct-id' },
                { styleName: 'CG_Style', id: 'cg-id' }
            ];

            const { rowGroups, nodes } = getCTRowGroups(rowFields, fields, undefined, styles);

            expect(rowGroups).toHaveLength(1);
            expect(nodes).toHaveLength(2);

            const group = rowGroups[0];
            expect(group.name).toBe('field1');
            expect(group.label).toBe('field1');
            expect(group.type).toBe('rowGroup');
            expect(group.headerCellHeight).toBe(25);
            expect(group.totalCellWidth).toBe(50);
            expect(group.className).toBe('java.lang.String');
            expect(group.expression).toBe('$F{field1}');
            expect(group.cells).toHaveLength(2);

            const [headerCell, totalCell] = group.cells;
            expect(headerCell.width).toBe(50);
            expect(headerCell.height).toBe(25);
            expect(headerCell.className).toBe('java.lang.String');
            expect(headerCell.styleNameReference).toBe('ch-id');
            expect(headerCell.nodeIds).toEqual([nodes[0].id]);

            expect(totalCell.width).toBe(50);
            expect(totalCell.height).toBe(25);
            expect(totalCell.styleNameReference).toBe('ct-id');
            expect(totalCell.nodeIds).toEqual([nodes[1].id]);
        });

        it('multiplies header height and total height by measures length when measures are provided', () => {
            const rowFields = ['field1'];
            const fields = [{ name: 'field1', clazz: 'java.lang.String' }];
            const measures = ['measure1', 'measure2', 'measure3'];

            const { rowGroups } = getCTRowGroups(rowFields, fields, measures, []);

            expect(rowGroups[0].headerCellHeight).toBe(75);
            expect(rowGroups[0].cells[0].height).toBe(75);
            expect(rowGroups[0].cells[1].height).toBe(75);
        });

        it('calculates increasing header height and total width for earlier fields with multiple row fields', () => {
            const rowFields = ['field1', 'field2', 'field3'];
            const fields = [
                { name: 'field1', clazz: 'java.lang.String' },
                { name: 'field2', clazz: 'java.lang.String' },
                { name: 'field3', clazz: 'java.lang.String' }
            ];

            const { rowGroups } = getCTRowGroups(rowFields, fields, undefined, []);

            expect(rowGroups[0].headerCellHeight).toBe(75);
            expect(rowGroups[0].totalCellWidth).toBe(150);
            expect(rowGroups[1].headerCellHeight).toBe(50);
            expect(rowGroups[1].totalCellWidth).toBe(100);
            expect(rowGroups[2].headerCellHeight).toBe(25);
            expect(rowGroups[2].totalCellWidth).toBe(50);
        });

        it('assigns CT styleNameReference to the total cell of the first field and CG for subsequent fields', () => {
            const rowFields = ['field1', 'field2'];
            const fields = [
                { name: 'field1', clazz: 'java.lang.String' },
                { name: 'field2', clazz: 'java.lang.String' }
            ];
            const styles = [
                { styleName: 'CT_Style', id: 'ct-id' },
                { styleName: 'CG_Style', id: 'cg-id' }
            ];

            const { rowGroups } = getCTRowGroups(rowFields, fields, undefined, styles);

            expect(rowGroups[0].cells[1].styleNameReference).toBe('ct-id');
            expect(rowGroups[1].cells[1].styleNameReference).toBe('cg-id');
        });

        it('throws when no matching field is found for a row field', () => {
            const rowFields = ['unknownField'];
            const fields = [{ name: 'otherField', clazz: 'java.lang.Integer' }];

            expect(() => getCTRowGroups(rowFields, fields, undefined, [])).toThrow();
        });

        it('produces two nodes per row field in the nodes array', () => {
            const rowFields = ['field1', 'field2', 'field3'];
            const fields = [
                { name: 'field1', clazz: 'java.lang.String' },
                { name: 'field2', clazz: 'java.lang.String' },
                { name: 'field3', clazz: 'java.lang.String' }
            ];

            const { nodes } = getCTRowGroups(rowFields, fields, undefined, []);
            expect(nodes).toHaveLength(6);
        });

        it('generates unique cell and node ids for each row group using uuidv4', () => {
            const rowFields = ['field1', 'field2'];
            const fields = [
                { name: 'field1', clazz: 'java.lang.String' },
                { name: 'field2', clazz: 'java.lang.String' }
            ];

            const { rowGroups, nodes } = getCTRowGroups(rowFields, fields, undefined, []);
            const allIds = [
                ...rowGroups.map((group) => group.id),
                ...rowGroups.flatMap((group) => group.cells.map((cell) => cell.id)),
                ...nodes.map((node) => node.id)
            ];
            const uniqueIds = new Set(allIds);
            expect(uniqueIds.size).toBe(allIds.length);
        });

        it('treats an empty measures array the same as no measures being provided', () => {
            const rowFields = ['field1'];
            const fields = [{ name: 'field1', clazz: 'java.lang.String' }];

            const { rowGroups } = getCTRowGroups(rowFields, fields, [], []);

            expect(rowGroups[0].headerCellHeight).toBe(25);
            expect(rowGroups[0].cells[1].height).toBe(25);
        });
    });

    describe('getCTMeasureCells', () => {
        it('creates a single Detail/Detail cell when there are no measures, columnGroups, or rowGroups', () => {
            const { cells, nodes } = getCTMeasureCells([], [], [], [], []);

            expect(cells).toHaveLength(1);
            expect(nodes).toHaveLength(0);

            const cell = cells[0];
            expect(cell.name).toBe('Detail/Detail');
            expect(cell.label).toBe('Detail/Detail');
            expect(cell.type).toBe('measureCell');
            expect(cell.cellIndex).toBe(0);
            expect(cell.width).toBe(50);
            expect(cell.height).toBe(25);
            expect(cell.nodeIds).toEqual([]);
            expect(cell.columnTotalGroup).toBeUndefined();
            expect(cell.rowTotalGroup).toBeUndefined();
        });

        it('assigns CT styleNameReference to the sole Detail/Detail cell since it is both the last row and last column', () => {
            const styles = [
                { styleName: 'CG_Style', id: 'cg-id' },
                { styleName: 'CT_Style', id: 'ct-id' },
                { styleName: 'CD_Style', id: 'cd-id' }
            ];

            const { cells } = getCTMeasureCells([], [], [], [], styles);

            expect(cells[0].styleNameReference).toBe('ct-id');
        });

        it('creates measure nodes for each measure and links them to the cell via nodeIds', () => {
            const measures = ['measure1', 'measure2'];
            const fields = [
                { name: 'measure1', clazz: 'java.lang.String' },
                { name: 'measure2', clazz: 'java.lang.Integer' }
            ];

            const { cells, nodes } = getCTMeasureCells(measures, [], [], fields, []);

            expect(nodes).toHaveLength(2);
            expect(cells).toHaveLength(1);
            expect(cells[0].nodeIds).toEqual([nodes[0].id, nodes[1].id]);
            expect(cells[0].height).toBe(50);
            expect(nodes[0].cellId).toBe(cells[0].id);
            expect(nodes[1].cellId).toBe(cells[0].id);
        });

        it('defaults cell height using a multiplier of 1 when measures is not provided', () => {
            const { cells } = getCTMeasureCells(undefined, [], [], [], []);
            expect(cells[0].height).toBe(25);
        });

        it('builds cols from columnGroups reversed and prefixed with Detail, and rows from rowGroups prefixed with Detail', () => {
            const columnGroups = [{ name: 'colA' }, { name: 'colB' }];
            const rowGroups = [{ name: 'rowX' }, { name: 'rowY' }];

            const { cells } = getCTMeasureCells([], columnGroups, rowGroups, [], []);

            const names = cells.map((cell) => cell.name);
            expect(names).toEqual([
                'Detail/Detail',
                'colB/Detail',
                'colA/Detail',
                'Detail/rowX',
                'colB/rowX',
                'colA/rowX',
                'Detail/rowY',
                'colB/rowY',
                'colA/rowY'
            ]);
        });

        it('creates 3x3 cells for two columnGroups and two rowGroups', () => {
            const columnGroups = [{ name: 'colA' }, { name: 'colB' }];
            const rowGroups = [{ name: 'rowX' }, { name: 'rowY' }];

            const { cells } = getCTMeasureCells([], columnGroups, rowGroups, [], []);
            expect(cells).toHaveLength(9);
        });

        it('sets columnTotalGroup only when col is not Detail', () => {
            const columnGroups = [{ name: 'colA' }];
            const { cells } = getCTMeasureCells([], columnGroups, [], [], []);

            const detailCell = cells.find((cell) => cell.name === 'Detail/Detail');
            const totalColCell = cells.find((cell) => cell.name === 'colA/Detail');

            expect(detailCell.columnTotalGroup).toBeUndefined();
            expect(totalColCell.columnTotalGroup).toBe('colA');
        });

        it('sets rowTotalGroup only when row is not Detail', () => {
            const rowGroups = [{ name: 'rowX' }];
            const { cells } = getCTMeasureCells([], [], rowGroups, [], []);

            const detailCell = cells.find((cell) => cell.name === 'Detail/Detail');
            const totalRowCell = cells.find((cell) => cell.name === 'Detail/rowX');

            expect(detailCell.rowTotalGroup).toBeUndefined();
            expect(totalRowCell.rowTotalGroup).toBe('rowX');
        });

        it('assigns CD styleNameReference only to the Detail/Detail cell when it is not also the last row or column', () => {
            const styles = [
                { styleName: 'CG_Style', id: 'cg-id' },
                { styleName: 'CT_Style', id: 'ct-id' },
                { styleName: 'CD_Style', id: 'cd-id' }
            ];
            const columnGroups = [{ name: 'colA' }, { name: 'colB' }];
            const rowGroups = [{ name: 'rowX' }, { name: 'rowY' }];

            const { cells } = getCTMeasureCells([], columnGroups, rowGroups, [], styles);

            const detailCell = cells.find((cell) => cell.name === 'Detail/Detail');
            expect(detailCell.styleNameReference).toBe('cd-id');
        });

        it('assigns CG styleNameReference to non-Detail, non-last-row, non-last-column cells', () => {
            const styles = [
                { styleName: 'CG_Style', id: 'cg-id' },
                { styleName: 'CT_Style', id: 'ct-id' },
                { styleName: 'CD_Style', id: 'cd-id' }
            ];
            const columnGroups = [{ name: 'colA' }, { name: 'colB' }];
            const rowGroups = [{ name: 'rowX' }, { name: 'rowY' }];

            const { cells } = getCTMeasureCells([], columnGroups, rowGroups, [], styles);

            const middleCell = cells.find((cell) => cell.name === 'colB/rowX');
            expect(middleCell.styleNameReference).toBe('cg-id');
        });

        it('overrides styleNameReference to CT for any cell in the last row or last column', () => {
            const styles = [
                { styleName: 'CG_Style', id: 'cg-id' },
                { styleName: 'CT_Style', id: 'ct-id' },
                { styleName: 'CD_Style', id: 'cd-id' }
            ];
            const columnGroups = [{ name: 'colA' }, { name: 'colB' }];
            const rowGroups = [{ name: 'rowX' }, { name: 'rowY' }];

            const { cells } = getCTMeasureCells([], columnGroups, rowGroups, [], styles);

            const lastRowCell = cells.find((cell) => cell.name === 'Detail/rowY');
            const lastColCell = cells.find((cell) => cell.name === 'colA/Detail');
            const lastRowAndColCell = cells.find((cell) => cell.name === 'colA/rowY');

            expect(lastRowCell.styleNameReference).toBe('ct-id');
            expect(lastColCell.styleNameReference).toBe('ct-id');
            expect(lastRowAndColCell.styleNameReference).toBe('ct-id');
        });

        it('sets cellIndex equal to the rowIndex for every cell in that row', () => {
            const columnGroups = [{ name: 'colA' }];
            const rowGroups = [{ name: 'rowX' }];

            const { cells } = getCTMeasureCells([], columnGroups, rowGroups, [], []);

            const rowXCells = cells.filter((cell) => cell.name.endsWith('/rowX'));
            rowXCells.forEach((cell) => {
                expect(cell.cellIndex).toBe(1);
            });

            const detailRowCells = cells.filter((cell) => cell.name.endsWith('/Detail'));
            detailRowCells.forEach((cell) => {
                expect(cell.cellIndex).toBe(0);
            });
        });

        it('generates unique cell and node ids across all measure cells', () => {
            const measures = ['measure1', 'measure2'];
            const fields = [
                { name: 'measure1', clazz: 'java.lang.String' },
                { name: 'measure2', clazz: 'java.lang.Integer' }
            ];
            const columnGroups = [{ name: 'colA' }];
            const rowGroups = [{ name: 'rowX' }];

            const { cells, nodes } = getCTMeasureCells(measures, columnGroups, rowGroups, fields, []);

            const allIds = [...cells.map((cell) => cell.id), ...nodes.map((node) => node.id)];
            const uniqueIds = new Set(allIds);
            expect(uniqueIds.size).toBe(allIds.length);
        });

        it('handles missing fields gracefully by falling back to an empty backendDataType for measure nodes', () => {
            const measures = ['unknownMeasure'];
            const { nodes } = getCTMeasureCells(measures, [], [], [], []);

            expect(nodes[0].backendDataType).toBe('');
        });
    });

    describe('getCTMeasures', () => {
        it('returns an empty array when measures is an empty array', () => {
            const result = getCTMeasures([], [], {});
            expect(result).toEqual([]);
        });

        it('creates a measure object with the expected structure for a single measure', () => {
            const measures = ['measure1'];
            const fields = [{ name: 'measure1', clazz: 'java.lang.Double' }];
            const measuresAggregateMap = { measure1: 'Sum' };

            const result = getCTMeasures(measures, fields, measuresAggregateMap);

            expect(result).toEqual([
                {
                    calculation: 'Sum',
                    className: 'java.lang.Double',
                    measureExpression: '$F{measure1}',
                    name: 'measure1',
                    label: 'measure1_MEASURE',
                    id: 'uuid-0'
                }
            ]);
        });

        it('defaults calculation to Count when the measure is not present in measuresAggregateMap', () => {
            const measures = ['measure1'];
            const fields = [{ name: 'measure1', clazz: 'java.lang.Double' }];

            const result = getCTMeasures(measures, fields, {});

            expect(result[0].calculation).toBe('Count');
        });

        it('defaults className to an empty string when no matching field is found', () => {
            const measures = ['unknownMeasure'];
            const fields = [{ name: 'otherField', clazz: 'java.lang.Integer' }];

            const result = getCTMeasures(measures, fields, {});

            expect(result[0].className).toBe('');
        });

        it('creates one measure object per measure preserving order', () => {
            const measures = ['measure1', 'measure2', 'measure3'];
            const fields = [
                { name: 'measure1', clazz: 'java.lang.String' },
                { name: 'measure2', clazz: 'java.lang.Integer' },
                { name: 'measure3', clazz: 'java.lang.Double' }
            ];
            const measuresAggregateMap = {
                measure1: 'Sum',
                measure2: 'Average',
                measure3: 'Max'
            };

            const result = getCTMeasures(measures, fields, measuresAggregateMap);

            expect(result).toHaveLength(3);
            expect(result.map((m) => m.name)).toEqual(['measure1', 'measure2', 'measure3']);
            expect(result.map((m) => m.calculation)).toEqual(['Sum', 'Average', 'Max']);
            expect(result.map((m) => m.className)).toEqual([
                'java.lang.String',
                'java.lang.Integer',
                'java.lang.Double'
            ]);
        });

        it('generates the label by appending _MEASURE to the measure name', () => {
            const measures = ['revenue'];
            const fields = [{ name: 'revenue', clazz: 'java.lang.Double' }];

            const result = getCTMeasures(measures, fields, {});

            expect(result[0].label).toBe('revenue_MEASURE');
        });

        it('generates the measureExpression using the measure name', () => {
            const measures = ['revenue'];
            const fields = [{ name: 'revenue', clazz: 'java.lang.Double' }];

            const result = getCTMeasures(measures, fields, {});

            expect(result[0].measureExpression).toBe('$F{revenue}');
        });

        it('generates unique ids for each measure using uuidv4', () => {
            const measures = ['measure1', 'measure2'];
            const fields = [
                { name: 'measure1', clazz: 'java.lang.String' },
                { name: 'measure2', clazz: 'java.lang.Integer' }
            ];

            const result = getCTMeasures(measures, fields, {});

            expect(uuidv4).toHaveBeenCalledTimes(2);
            const uniqueIds = new Set(result.map((m) => m.id));
            expect(uniqueIds.size).toBe(2);
        });

        it('falls back to Count when measuresAggregateMap is an empty object for all measures', () => {
            const measures = ['measure1', 'measure2'];
            const fields = [
                { name: 'measure1', clazz: 'java.lang.String' },
                { name: 'measure2', clazz: 'java.lang.Integer' }
            ];

            const result = getCTMeasures(measures, fields, {});

            expect(result[0].calculation).toBe('Count');
            expect(result[1].calculation).toBe('Count');
        });
    });

    describe('getCTComputedLayout', () => {
        it('returns colWidths and rowHeights of length 1 when columnGroups, rowGroups, and measures are all empty', () => {
            const result = getCTComputedLayout({ columnGroups: [], rowGroups: [], measures: [] });

            expect(result.colWidths).toEqual([50]);
            expect(result.rowHeights).toEqual([0]);
        });

        it('uses default empty arrays when called with no arguments', () => {
            const result = getCTComputedLayout({});

            expect(result.colWidths).toEqual([50]);
            expect(result.rowHeights).toEqual([0]);
        });

        it('sets every colWidths entry to HCR_CROSSTAB_CELL_WIDTH regardless of group sizes', () => {
            const columnGroups = [{ name: 'colA' }, { name: 'colB' }];
            const rowGroups = [{ name: 'rowX' }];
            const measures = ['measure1'];

            const result = getCTComputedLayout({ columnGroups, rowGroups, measures });

            expect(result.colWidths).toEqual([50, 50, 50, 50]);
        });

        it('sizes colWidths and rowHeights arrays to rowGroups.length + columnGroups.length + 1', () => {
            const columnGroups = [{ name: 'colA' }, { name: 'colB' }];
            const rowGroups = [{ name: 'rowX' }, { name: 'rowY' }, { name: 'rowZ' }];

            const result = getCTComputedLayout({ columnGroups, rowGroups, measures: [] });

            expect(result.colWidths).toHaveLength(6);
            expect(result.rowHeights).toHaveLength(6);
        });

        it('sets rowHeights entries before columnGroups.length to HCR_CROSSTAB_CELL_HEIGHT', () => {
            const columnGroups = [{ name: 'colA' }, { name: 'colB' }];
            const rowGroups = [];
            const measures = ['measure1'];

            const result = getCTComputedLayout({ columnGroups, rowGroups, measures });

            expect(result.rowHeights[0]).toBe(25);
            expect(result.rowHeights[1]).toBe(25);
        });

        it('sets rowHeights entries from columnGroups.length onward to HCR_CROSSTAB_CELL_HEIGHT multiplied by measures length', () => {
            const columnGroups = [{ name: 'colA' }];
            const rowGroups = [{ name: 'rowX' }];
            const measures = ['measure1', 'measure2', 'measure3'];

            const result = getCTComputedLayout({ columnGroups, rowGroups, measures });

            expect(result.rowHeights[0]).toBe(25);
            expect(result.rowHeights[1]).toBe(75);
            expect(result.rowHeights[2]).toBe(75);
        });

        it('sets all rowHeights to 0 when measures is empty and columnGroups is empty', () => {
            const rowGroups = [{ name: 'rowX' }, { name: 'rowY' }];

            const result = getCTComputedLayout({ columnGroups: [], rowGroups, measures: [] });

            expect(result.rowHeights).toEqual([0, 0, 0]);
        });

        it('computes rowHeights correctly when only columnGroups are provided', () => {
            const columnGroups = [{ name: 'colA' }, { name: 'colB' }, { name: 'colC' }];
            const measures = ['measure1', 'measure2'];

            const result = getCTComputedLayout({ columnGroups, rowGroups: [], measures });

            expect(result.rowHeights).toEqual([25, 25, 25, 50]);
        });

        it('computes rowHeights correctly when only rowGroups are provided', () => {
            const rowGroups = [{ name: 'rowX' }, { name: 'rowY' }];
            const measures = ['measure1'];

            const result = getCTComputedLayout({ columnGroups: [], rowGroups, measures });

            expect(result.rowHeights).toEqual([25, 25, 25]);
        });
    });

    describe('calculateCTTotalHeightAndWidth', () => {
        it('returns the base width and zero height when rows, columns, and measures are all empty', () => {
            const result = calculateCTTotalHeightAndWidth([], [], []);

            expect(result).toEqual({ width: 50, height: 0 });
        });

        it('uses default empty arrays when called with no arguments', () => {
            const result = calculateCTTotalHeightAndWidth();

            expect(result).toEqual({ width: 50, height: 0 });
        });

        it('computes width and height correctly when only columns are provided', () => {
            const columns = [{ name: 'colA' }, { name: 'colB' }];

            const result = calculateCTTotalHeightAndWidth([], columns, []);

            expect(result.width).toBe(150);
            expect(result.height).toBe(50);
        });

        it('computes width correctly when only rows are provided', () => {
            const rows = [{ name: 'rowX' }, { name: 'rowY' }, { name: 'rowZ' }];

            const result = calculateCTTotalHeightAndWidth(rows, [], []);

            expect(result.width).toBe(200);
        });

        it('computes height correctly when rows and measures are provided without columns', () => {
            const rows = [{ name: 'rowX' }, { name: 'rowY' }];
            const measures = ['measure1', 'measure2'];

            const result = calculateCTTotalHeightAndWidth(rows, [], measures);

            expect(result.height).toBe(150);
        });

        it('returns zero height when measures is empty regardless of rows', () => {
            const rows = [{ name: 'rowX' }, { name: 'rowY' }];

            const result = calculateCTTotalHeightAndWidth(rows, [], []);

            expect(result.height).toBe(0);
        });

        it('computes combined width and height when rows, columns, and measures are all provided', () => {
            const rows = [{ name: 'rowX' }, { name: 'rowY' }];
            const columns = [{ name: 'colA' }, { name: 'colB' }, { name: 'colC' }];
            const measures = ['measure1'];

            const result = calculateCTTotalHeightAndWidth(rows, columns, measures);

            expect(result.width).toBe(300);
            expect(result.height).toBe(150);
        });

        it('scales height proportionally as measures increase', () => {
            const rows = [{ name: 'rowX' }];
            const columns = [];
            const measures = ['measure1', 'measure2', 'measure3', 'measure4'];

            const result = calculateCTTotalHeightAndWidth(rows, columns, measures);

            expect(result.height).toBe(200);
        });

        it('scales width proportionally as columns increase', () => {
            const rows = [];
            const columns = [{ name: 'colA' }, { name: 'colB' }, { name: 'colC' }, { name: 'colD' }];
            const measures = [];

            const result = calculateCTTotalHeightAndWidth(rows, columns, measures);

            expect(result.width).toBe(250);
        });
    });

    describe('getMeasureCellsByCellIndex', () => {
        it('returns an empty object when measureCells is an empty array', () => {
            const result = getMeasureCellsByCellIndex([]);
            expect(result).toEqual({});
        });

        it('groups a single measure cell under its cellIndex', () => {
            const measureCells = [{ cellIndex: 0, id: 'm1' }];
            const result = getMeasureCellsByCellIndex(measureCells);
            expect(result).toEqual({ 0: [{ cellIndex: 0, id: 'm1' }] });
        });

        it('groups multiple measure cells with the same cellIndex into the same array preserving order', () => {
            const measureCells = [
                { cellIndex: 0, id: 'm1' },
                { cellIndex: 0, id: 'm2' },
                { cellIndex: 0, id: 'm3' }
            ];
            const result = getMeasureCellsByCellIndex(measureCells);
            expect(result[0]).toEqual([
                { cellIndex: 0, id: 'm1' },
                { cellIndex: 0, id: 'm2' },
                { cellIndex: 0, id: 'm3' }
            ]);
        });

        it('separates measure cells into distinct groups by their cellIndex', () => {
            const measureCells = [
                { cellIndex: 0, id: 'm1' },
                { cellIndex: 1, id: 'm2' },
                { cellIndex: 0, id: 'm3' },
                { cellIndex: 2, id: 'm4' }
            ];
            const result = getMeasureCellsByCellIndex(measureCells);
            expect(result).toEqual({
                0: [{ cellIndex: 0, id: 'm1' }, { cellIndex: 0, id: 'm3' }],
                1: [{ cellIndex: 1, id: 'm2' }],
                2: [{ cellIndex: 2, id: 'm4' }]
            });
        });

        it('uses object keys derived from cellIndex values, coerced to strings', () => {
            const measureCells = [{ cellIndex: 5, id: 'm1' }];
            const result = getMeasureCellsByCellIndex(measureCells);
            expect(Object.keys(result)).toEqual(['5']);
        });
    });

    describe('getCrosstabLayout', () => {
        it('returns only the header cell when columnGroups, rowGroups, and measureCells are all empty', () => {
            const result = getCrosstabLayout({
                columnGroups: [],
                rowGroups: [],
                measureCells: [],
                colWidths: [],
                rowHeights: []
            });

            expect(result).toEqual([
                { id: 'crosstab_header_cell', col: [1, 1], row: [1, 1], widthUpdaters: [], heightUpdaters: [] }
            ]);
        });

        it('uses default empty values when called with no config', () => {
            const result = getCrosstabLayout();

            expect(result).toEqual([
                { id: 'crosstab_header_cell', col: [1, 1], row: [1, 1], widthUpdaters: [], heightUpdaters: [] }
            ]);
        });

        it('produces the header cell plus two cells per columnGroup when only columnGroups are provided', () => {
            const columnGroups = [{ name: 'colA', cells: [{ id: 'h1' }, { id: 't1' }] }];

            const result = getCrosstabLayout({
                columnGroups,
                rowGroups: [],
                measureCells: [],
                colWidths: [10, 20],
                rowHeights: [100, 200]
            });

            expect(result).toEqual([
                { id: 'crosstab_header_cell', col: [1, 1], row: [1, 2], widthUpdaters: [], heightUpdaters: [0] },
                { col: [1, 2], row: [1, 2], widthUpdaters: [], heightUpdaters: [0], id: 'h1' },
                { col: [2, 2], row: [1, 2], widthUpdaters: [1], heightUpdaters: [0], id: 't1' }
            ]);
        });

        it('produces the header cell plus two cells per rowGroup when only rowGroups are provided', () => {
            const rowGroups = [{ name: 'rowX', cells: [{ id: 'h2' }, { id: 't2' }] }];

            const result = getCrosstabLayout({
                columnGroups: [],
                rowGroups,
                measureCells: [],
                colWidths: [10, 20],
                rowHeights: [100, 200]
            });

            expect(result).toEqual([
                { id: 'crosstab_header_cell', col: [1, 2], row: [1, 1], widthUpdaters: [0], heightUpdaters: [] },
                { col: [1, 2], row: [1, 2], widthUpdaters: [0], heightUpdaters: [], id: 'h2' },
                { col: [1, 2], row: [2, 3], widthUpdaters: [0], heightUpdaters: [1], id: 't2' }
            ]);
        });

        it('produces exactly two cells for every columnGroup and every rowGroup, in addition to the header cell', () => {
            const columnGroups = [
                { name: 'colA', cells: [{ id: 'ch1' }, { id: 'ct1' }] },
                { name: 'colB', cells: [{ id: 'ch2' }, { id: 'ct2' }] }
            ];
            const rowGroups = [{ name: 'rowX', cells: [{ id: 'rh1' }, { id: 'rt1' }] }];

            const result = getCrosstabLayout({
                columnGroups,
                rowGroups,
                measureCells: [],
                colWidths: [10, 20, 30, 40],
                rowHeights: [100, 200, 300, 400]
            });

            expect(result).toHaveLength(1 + columnGroups.length * 2 + rowGroups.length * 2);
        });

        it('preserves original cell ids from columnGroups and rowGroups via the spread on each pushed cell', () => {
            const columnGroups = [{ name: 'colA', cells: [{ id: 'ch1', custom: 'colHeader' }, { id: 'ct1', custom: 'colTotal' }] }];
            const rowGroups = [{ name: 'rowX', cells: [{ id: 'rh1', custom: 'rowHeader' }, { id: 'rt1', custom: 'rowTotal' }] }];

            const result = getCrosstabLayout({
                columnGroups,
                rowGroups,
                measureCells: [],
                colWidths: [10, 20, 30],
                rowHeights: [100, 200, 300]
            });

            const ids = result.map((cell) => cell.id);
            expect(ids).toEqual(
                expect.arrayContaining(['ch1', 'ct1', 'rh1', 'rt1'])
            );
            expect(result.find((cell) => cell.id === 'ch1').custom).toBe('colHeader');
            expect(result.find((cell) => cell.id === 'rt1').custom).toBe('rowTotal');
        });

        it('handles a columnGroup or rowGroup with no cells property by not throwing and omitting spread properties', () => {
            const columnGroups = [{ name: 'colA' }];

            expect(() =>
                getCrosstabLayout({
                    columnGroups,
                    rowGroups: [],
                    measureCells: [],
                    colWidths: [10, 20],
                    rowHeights: [100, 200]
                })
            ).not.toThrow();
        });

        it('adds one cell per measure cell, grouped and positioned by cellIndex', () => {
            const measureCells = [
                { cellIndex: 0, id: 'm1' },
                { cellIndex: 0, id: 'm2' },
                { cellIndex: 1, id: 'm3' }
            ];

            const result = getCrosstabLayout({
                columnGroups: [],
                rowGroups: [],
                measureCells,
                colWidths: [10],
                rowHeights: [100]
            });

            expect(result).toHaveLength(1 + measureCells.length);

            const m1Cell = result.find((cell) => cell.id === 'm1');
            const m2Cell = result.find((cell) => cell.id === 'm2');
            const m3Cell = result.find((cell) => cell.id === 'm3');

            expect(m1Cell.col).toEqual([1, 2]);
            expect(m1Cell.row).toEqual([1, 2]);
            expect(m2Cell.col).toEqual([2, 3]);
            expect(m2Cell.row).toEqual([1, 2]);
            expect(m3Cell.col).toEqual([1, 2]);
            expect(m3Cell.row).toEqual([2, 3]);
        });

        it('produces no additional cells beyond the header cell when measureCells is empty', () => {
            const result = getCrosstabLayout({
                columnGroups: [],
                rowGroups: [],
                measureCells: [],
                colWidths: [10],
                rowHeights: [100]
            });

            expect(result).toHaveLength(1);
        });
    });

    describe('getCrosstabOutlineData', () => {
        it('returns the default structure with empty children when crosstab is empty', () => {
            const result = getCrosstabOutlineData({});

            expect(result).toEqual([
                { title: 'Row Groups', key: 'row_groups', children: [], selectable: false },
                { title: 'Column Groups', key: 'column_groups', children: [], selectable: false },
                { title: 'Measures', key: 'measures', children: [], selectable: false }
            ]);
        });

        it('returns the default structure when called with no arguments', () => {
            const result = getCrosstabOutlineData();

            expect(result).toEqual([
                { title: 'Row Groups', key: 'row_groups', children: [], selectable: false },
                { title: 'Column Groups', key: 'column_groups', children: [], selectable: false },
                { title: 'Measures', key: 'measures', children: [], selectable: false }
            ]);
        });

        it('builds row group children with nested cells and nodes', () => {
            const config = {
                rowGroups: [
                    {
                        id: 'rg1',
                        label: 'Row Group 1',
                        cells: [
                            { id: 'cell1', label: 'Cell 1', nodeIds: ['node1'] }
                        ]
                    }
                ],
                nodes: {
                    node1: { id: 'node1', label: 'Node 1' }
                }
            };

            const result = getCrosstabOutlineData({ config });
            const rowGroupsSection = result.find((section) => section.key === 'row_groups');

            expect(rowGroupsSection.children).toEqual([
                {
                    title: 'Row Group 1',
                    key: 'rg1',
                    id: 'rg1',
                    selectable: true,
                    selectKey: 'crosstab-group-item',
                    currentData: config.rowGroups[0],
                    children: [
                        {
                            title: 'Cell 1',
                            key: 'cell1',
                            id: 'cell1',
                            selectable: true,
                            selectKey: 'cell',
                            currentData: config.rowGroups[0].cells[0],
                            children: [
                                {
                                    title: 'Node 1',
                                    key: 'node1',
                                    id: 'node1',
                                    selectable: true,
                                    selectKey: 'node',
                                    currentData: { id: 'node1', label: 'Node 1' },
                                    children: []
                                }
                            ]
                        }
                    ]
                }
            ]);
        });

        it('builds column group children with nested cells and nodes', () => {
            const config = {
                columnGroups: [
                    {
                        id: 'cg1',
                        label: 'Column Group 1',
                        cells: [
                            { id: 'cell2', label: 'Cell 2', nodeIds: ['node2'] }
                        ]
                    }
                ],
                nodes: {
                    node2: { id: 'node2', label: 'Node 2' }
                }
            };

            const result = getCrosstabOutlineData({ config });
            const columnGroupsSection = result.find((section) => section.key === 'column_groups');

            expect(columnGroupsSection.children[0].title).toBe('Column Group 1');
            expect(columnGroupsSection.children[0].children[0].title).toBe('Cell 2');
            expect(columnGroupsSection.children[0].children[0].children[0].title).toBe('Node 2');
        });

        it('builds measures as flat selectable items without children', () => {
            const config = {
                measures: [
                    { id: 'm1', label: 'Measure 1' },
                    { id: 'm2', label: 'Measure 2' }
                ]
            };

            const result = getCrosstabOutlineData({ config });
            const measuresSection = result.find((section) => section.key === 'measures');

            expect(measuresSection.children).toEqual([
                {
                    title: 'Measure 1',
                    key: 'm1',
                    id: 'm1',
                    selectable: true,
                    selectKey: 'crosstab-measure-item',
                    currentData: config.measures[0]
                },
                {
                    title: 'Measure 2',
                    key: 'm2',
                    id: 'm2',
                    selectable: true,
                    selectKey: 'crosstab-measure-item',
                    currentData: config.measures[1]
                }
            ]);
        });

        it('appends measure cells as top-level entries after the three fixed sections', () => {
            const config = {
                measureCells: [
                    { id: 'mc1', label: 'Measure Cell 1', nodeIds: ['node3'] }
                ],
                nodes: {
                    node3: { id: 'node3', label: 'Node 3' }
                }
            };

            const result = getCrosstabOutlineData({ config });

            expect(result).toHaveLength(4);
            expect(result[3]).toEqual({
                title: 'Measure Cell 1',
                key: 'mc1',
                id: 'mc1',
                selectable: true,
                selectKey: 'cell',
                currentData: config.measureCells[0],
                children: [
                    {
                        title: 'Node 3',
                        key: 'node3',
                        id: 'node3',
                        selectable: true,
                        selectKey: 'node',
                        currentData: { id: 'node3', label: 'Node 3' },
                        children: []
                    }
                ]
            });
        });

        it('appends one entry per measure cell in order when there are multiple', () => {
            const config = {
                measureCells: [
                    { id: 'mc1', label: 'Measure Cell 1', nodeIds: [] },
                    { id: 'mc2', label: 'Measure Cell 2', nodeIds: [] },
                    { id: 'mc3', label: 'Measure Cell 3', nodeIds: [] }
                ]
            };

            const result = getCrosstabOutlineData({ config });

            expect(result).toHaveLength(6);
            expect(result.slice(3).map((cell) => cell.title)).toEqual([
                'Measure Cell 1',
                'Measure Cell 2',
                'Measure Cell 3'
            ]);
        });

        it('sets the three fixed sections as non-selectable regardless of their children', () => {
            const config = {
                rowGroups: [{ id: 'rg1', label: 'Row Group 1', cells: [] }],
                columnGroups: [{ id: 'cg1', label: 'Column Group 1', cells: [] }],
                measures: [{ id: 'm1', label: 'Measure 1' }]
            };

            const result = getCrosstabOutlineData({ config });

            expect(result[0].selectable).toBe(false);
            expect(result[1].selectable).toBe(false);
            expect(result[2].selectable).toBe(false);
        });

        it('produces undefined children for a cell when it has no nodeIds', () => {
            const config = {
                rowGroups: [
                    { id: 'rg1', label: 'Row Group 1', cells: [{ id: 'cell1', label: 'Cell 1' }] }
                ]
            };

            const result = getCrosstabOutlineData({ config });
            const rowGroupsSection = result.find((section) => section.key === 'row_groups');

            expect(rowGroupsSection.children[0].children[0].children).toBeUndefined();
        });

        it('produces undefined children for a group when it has no cells', () => {
            const config = {
                rowGroups: [{ id: 'rg1', label: 'Row Group 1' }]
            };

            const result = getCrosstabOutlineData({ config });
            const rowGroupsSection = result.find((section) => section.key === 'row_groups');

            expect(rowGroupsSection.children[0].children).toBeUndefined();
        });
    });

});