import { describe } from "jest-circus";
import { hcrContextMenuTypes, hcrTableBandOrder, hcrTableBandsTypes } from "../../../components/hi-canned-reports/hcr-constants";
import { getAdvancedTableConfig, getTableStyles, hcrCanvasPaneHelperMethods, makeCellId } from "../../../components/hi-canned-reports/hcrCanvas/hcrCanvasPaneHelperMethods";
import { addEscapedQuotes, checkIfBandIsDeleted, getEmptyGroupCell, getHCRParaTypeFormat, getInitialParameter, getNewStyle, getTableStylesFromReportState, isHCRDateVariable, updateCanvasTabViewComponent, updateTableStyles } from "../../../components/hi-canned-reports/hcrHelperMethods";
import { getDatasetOutlineData, getHCRTableContextMenu, getHcrTableOutlineData, getHCRTableOutlineDSContextMenu, getStylesOutline, getTableOutlinedata } from "../../../components/hi-canned-reports/hcrCanvas/advanceComponents/utils";
import { tableOutlinedData } from "../constants";
import { cloneDeep } from "lodash";

const { getHcrTableCellsPositionDetails } = hcrCanvasPaneHelperMethods


describe('test getHcrTableCellsPositionDetails fn', () => {
    const data = {
        tableFields: ['field1', 'field2', 'field3'],
        tableConfig: {
            addTableHeader: true,
            addColumnHeader: true,
            addColumnFooter: true,
            addTableFooter: true,
        },
        tablePosition: {
            x: 0,
            y: 0,
        },
    };

    it('should return the correct number of cells', () => {
        const result = getHcrTableCellsPositionDetails(data);
        expect(result.length).toBe(15);
    });

    it('should return the correct cell positions', () => {
        const result = getHcrTableCellsPositionDetails(data);
        expect(result[0]).toEqual({
            cell: 'tableHeader',
            columnIndex: 0,
            cellWidth: 100,
            cellHeight: 25,
            x: 0,
            y: 0,
        });
        expect(result[1]).toEqual({
            cell: 'tableHeader',
            columnIndex: 1,
            cellWidth: 100,
            cellHeight: 25,
            x: 100,
            y: 0,
        });
        // ... and so on
    });
});

describe("test addEscapedQuotes fn", () => {
    describe("primitives and null", () => {
        test("returns a string as-is", () => {
            expect(addEscapedQuotes("hello")).toBe("hello");
        });

        test("returns a number as-is", () => {
            expect(addEscapedQuotes(42)).toBe(42);
        });

        test("returns null as-is", () => {
            expect(addEscapedQuotes(null)).toBeNull();
        });

        test("returns undefined as-is", () => {
            expect(addEscapedQuotes(undefined)).toBeUndefined();
        });

        test("returns boolean true as-is", () => {
            expect(addEscapedQuotes(true)).toBe(true);
        });

        test("returns boolean false as-is", () => {
            expect(addEscapedQuotes(false)).toBe(false);
        });
    });

    describe("textFieldExpression — plain string (no $F/$V/$P)", () => {
        test("wraps a bare string in escaped quotes", () => {
            expect(addEscapedQuotes({ textFieldExpression: "hello" })).toEqual({
                textFieldExpression: '"hello"',
            });
        });

        test("strips a leading quote before wrapping", () => {
            expect(addEscapedQuotes({ textFieldExpression: '"hello' })).toEqual({
                textFieldExpression: '"hello"',
            });
        });

        test("strips a trailing quote before wrapping", () => {
            expect(addEscapedQuotes({ textFieldExpression: 'hello"' })).toEqual({
                textFieldExpression: '"hello"',
            });
        });

        test("strips both surrounding quotes before wrapping", () => {
            expect(addEscapedQuotes({ textFieldExpression: '"hello"' })).toEqual({
                textFieldExpression: '"hello"',
            });
        });

        test("handles an empty string", () => {
            expect(addEscapedQuotes({ textFieldExpression: "" })).toEqual({
                textFieldExpression: '""',
            });
        });

        test("handles a string that is only a quote character", () => {
            expect(addEscapedQuotes({ textFieldExpression: '"' })).toEqual({
                textFieldExpression: '""',
            });
        });

        test("handles a string with spaces", () => {
            expect(addEscapedQuotes({ textFieldExpression: "hello world" })).toEqual({
                textFieldExpression: '"hello world"',
            });
        });
    });

    describe("textFieldExpression — expression variables ($F/$V/$P)", () => {
        test("leaves a $F expression unchanged", () => {
            const input = { textFieldExpression: "$F{fieldName}" };
            expect(addEscapedQuotes(input)).toEqual(input);
        });

        test("leaves a $V expression unchanged", () => {
            const input = { textFieldExpression: "$V{variableName}" };
            expect(addEscapedQuotes(input)).toEqual(input);
        });

        test("leaves a $P expression unchanged", () => {
            const input = { textFieldExpression: "$P{paramName}" };
            expect(addEscapedQuotes(input)).toEqual(input);
        });

        test("leaves a mixed expression containing $F unchanged", () => {
            const input = { textFieldExpression: "prefix $F{field} suffix" };
            expect(addEscapedQuotes(input)).toEqual(input);
        });

        test("leaves a mixed expression containing $V unchanged", () => {
            const input = { textFieldExpression: "$V{x} + 1" };
            expect(addEscapedQuotes(input)).toEqual(input);
        });

        test("leaves a mixed expression containing $P unchanged", () => {
            const input = { textFieldExpression: "label: $P{param}" };
            expect(addEscapedQuotes(input)).toEqual(input);
        });
    });

    describe("textFieldExpression — non-string values", () => {
        test("recurses into a number value without wrapping", () => {
            expect(addEscapedQuotes({ textFieldExpression: 99 })).toEqual({
                textFieldExpression: 99,
            });
        });

        test("recurses into null value without wrapping", () => {
            expect(addEscapedQuotes({ textFieldExpression: null })).toEqual({
                textFieldExpression: null,
            });
        });

        test("recurses into an object value without wrapping", () => {
            const input = { textFieldExpression: { nested: "value" } };
            expect(addEscapedQuotes(input)).toEqual({
                textFieldExpression: { nested: "value" },
            });
        });
    });

    describe("other object keys", () => {
        test("leaves other string keys unchanged", () => {
            expect(addEscapedQuotes({ label: "hello" })).toEqual({ label: "hello" });
        });

        test("recurses into nested objects", () => {
            const input = {
                outer: {
                    textFieldExpression: "world",
                },
            };
            expect(addEscapedQuotes(input)).toEqual({
                outer: {
                    textFieldExpression: '"world"',
                },
            });
        });

        test("handles an object with multiple keys, only transforms textFieldExpression", () => {
            const input = {
                name: "report",
                textFieldExpression: "title",
                type: "text",
            };
            expect(addEscapedQuotes(input)).toEqual({
                name: "report",
                textFieldExpression: '"title"',
                type: "text",
            });
        });

        test("does not mutate the original object", () => {
            const input = { textFieldExpression: "original" };
            const original = { ...input };
            addEscapedQuotes(input);
            expect(input).toEqual(original);
        });
    });

    describe("arrays", () => {
        test("maps over a flat array of primitives", () => {
            expect(addEscapedQuotes([1, 2, 3])).toEqual([1, 2, 3]);
        });

        test("maps over an array of objects and transforms each", () => {
            const input = [
                { textFieldExpression: "a" },
                { textFieldExpression: "b" },
            ];
            expect(addEscapedQuotes(input)).toEqual([
                { textFieldExpression: '"a"' },
                { textFieldExpression: '"b"' },
            ]);
        });

        test("handles an array of mixed types", () => {
            const input = [{ textFieldExpression: "hello" }, 42, null];
            expect(addEscapedQuotes(input)).toEqual([
                { textFieldExpression: '"hello"' },
                42,
                null,
            ]);
        });

        test("handles an empty array", () => {
            expect(addEscapedQuotes([])).toEqual([]);
        });

        test("handles nested arrays", () => {
            const input = [[{ textFieldExpression: "deep" }]];
            expect(addEscapedQuotes(input)).toEqual([[{ textFieldExpression: '"deep"' }]]);
        });
    });

    describe("deeply nested structures", () => {
        test("transforms textFieldExpression at any depth", () => {
            const input = {
                level1: {
                    level2: {
                        level3: {
                            textFieldExpression: "deep value",
                        },
                    },
                },
            };
            expect(addEscapedQuotes(input)).toEqual({
                level1: {
                    level2: {
                        level3: {
                            textFieldExpression: '"deep value"',
                        },
                    },
                },
            });
        });

        test("transforms multiple textFieldExpression keys across sibling objects", () => {
            const input = {
                a: { textFieldExpression: "foo" },
                b: { textFieldExpression: "$F{bar}" },
                c: { textFieldExpression: "baz" },
            };
            expect(addEscapedQuotes(input)).toEqual({
                a: { textFieldExpression: '"foo"' },
                b: { textFieldExpression: "$F{bar}" },
                c: { textFieldExpression: '"baz"' },
            });
        });

        test("handles objects nested inside arrays inside objects", () => {
            const input = {
                items: [
                    { textFieldExpression: "x" },
                    { textFieldExpression: "$V{y}" },
                ],
            };
            expect(addEscapedQuotes(input)).toEqual({
                items: [
                    { textFieldExpression: '"x"' },
                    { textFieldExpression: "$V{y}" },
                ],
            });
        });
    });
});


jest.mock('uuid', () => ({
    v4: () => `mock-uuid-${Math.random()}`,
}));

describe("test getAdvancedTableConfig fn", () => {


    describe('return shape', () => {
        it('returns all required top-level keys', () => {
            const result = getAdvancedTableConfig();
            expect(result).toHaveProperty('columns');
            expect(result).toHaveProperty('cells');
            expect(result).toHaveProperty('nodes');
            expect(result).toHaveProperty('columnOrder');
            expect(result).toHaveProperty('bands');
            expect(result).toHaveProperty('selectedColumnId');
            expect(result).toHaveProperty('selectedBandType');
            expect(result).toHaveProperty('selectedCells');
            expect(result).toHaveProperty('selectedNodes');
        });

        it('initialises all selected* fields as empty strings', () => {
            const result = getAdvancedTableConfig();
            expect(result.selectedColumnId).toBe('');
            expect(result.selectedBandType).toBe('');
            expect(result.selectedCells).toEqual([]);
            expect(result.selectedNodes).toEqual([]);
        });
    });


    describe('default arguments', () => {
        it('creates 1 column when columnCount is omitted', () => {
            const { columnOrder } = getAdvancedTableConfig();
            expect(columnOrder).toHaveLength(1);
        });

        it('uses width 100 when columnWidth is omitted', () => {
            const { columns, columnOrder } = getAdvancedTableConfig();
            expect(columns[columnOrder[0]].width).toBe(100);
        });

        it('only includes COLUMN_DATA band when no tableConfig flags are set', () => {
            const { bands } = getAdvancedTableConfig();
            expect(Object.keys(bands)).toEqual([
                hcrTableBandsTypes.TABLE_HEADER,
                hcrTableBandsTypes.COLUMN_HEADER,
                hcrTableBandsTypes.GROUP_HEADER,
                hcrTableBandsTypes.COLUMN_DATA,
                hcrTableBandsTypes.GROUP_FOOTER,
                hcrTableBandsTypes.COLUMN_FOOTER,
                hcrTableBandsTypes.TABLE_FOOTER,
            ]);
        });

        it('cells only contain COLUMN_DATA entries when no flags are set', () => {
            const { cells } = getAdvancedTableConfig();
            const bandTypes = Object.values(cells).map(c => c.bandType);
            expect(bandTypes).toEqual([
                hcrTableBandsTypes.TABLE_HEADER,
                hcrTableBandsTypes.COLUMN_HEADER,
                hcrTableBandsTypes.COLUMN_DATA,
                hcrTableBandsTypes.COLUMN_FOOTER,
                hcrTableBandsTypes.TABLE_FOOTER,
            ]);
        });
    });

    describe('columnCount parameter', () => {
        it.each([1, 3, 5])('creates %i columns', (count) => {
            const { columnOrder } = getAdvancedTableConfig(count);
            expect(columnOrder).toHaveLength(count);
        });

        it('names columns sequentially starting at 1', () => {
            const { columns, columnOrder } = getAdvancedTableConfig(3);
            columnOrder.forEach((id, idx) => {
                expect(columns[id].name).toBe(`Column ${idx + 1}`);
            });
        });

    });


    describe('columnWidth parameter', () => {
        it('applies custom width to all columns', () => {
            const { columns, columnOrder } = getAdvancedTableConfig(3, 200);
            columnOrder.forEach(id => expect(columns[id].width).toBe(200));
        });

        it('applies custom width to all cells', () => {
            const { cells } = getAdvancedTableConfig(2, 150);
            Object.values(cells).forEach(cell => expect(cell.width).toBe(150));
        });
    });


    describe('band availability via tableConfig flags', () => {
        const allFlagsOn = {
            addTableHeader: true,
            addColumnHeader: true,
            addTableFooter: true,
            addColumnFooter: true,
        };

        it('includes TABLE_HEADER band when addTableHeader is true', () => {
            const { bands } = getAdvancedTableConfig(1, 100, { addTableHeader: true });
            expect(bands).toHaveProperty(hcrTableBandsTypes.TABLE_HEADER);
        });

        it('includes COLUMN_HEADER band when addColumnHeader is true', () => {
            const { bands } = getAdvancedTableConfig(1, 100, { addColumnHeader: true });
            expect(bands).toHaveProperty(hcrTableBandsTypes.COLUMN_HEADER);
        });

        it('includes COLUMN_FOOTER band when addColumnFooter is true', () => {
            const { bands } = getAdvancedTableConfig(1, 100, { addColumnFooter: true });
            expect(bands).toHaveProperty(hcrTableBandsTypes.COLUMN_FOOTER);
        });

        it('includes TABLE_FOOTER band when addTableFooter is true', () => {
            const { bands } = getAdvancedTableConfig(1, 100, { addTableFooter: true });
            expect(bands).toHaveProperty(hcrTableBandsTypes.TABLE_FOOTER);
        });

        it('always includes COLUMN_DATA regardless of flags', () => {
            const { bands } = getAdvancedTableConfig(1, 100, {});
            expect(bands).toHaveProperty(hcrTableBandsTypes.COLUMN_DATA);
        });

        it('respects band order when all flags are on', () => {
            const { bands } = getAdvancedTableConfig(1, 100, allFlagsOn);
            const expectedOrder = hcrTableBandOrder.filter(b =>
                [
                    hcrTableBandsTypes.TABLE_HEADER,
                    hcrTableBandsTypes.COLUMN_HEADER,
                    hcrTableBandsTypes.GROUP_HEADER,
                    hcrTableBandsTypes.COLUMN_DATA,
                    hcrTableBandsTypes.GROUP_FOOTER,
                    hcrTableBandsTypes.COLUMN_FOOTER,
                    hcrTableBandsTypes.TABLE_FOOTER,
                ].includes(b)
            );
            expect(Object.keys(bands)).toEqual(expectedOrder);
        });
    });

    describe('cells', () => {
        it('creates (columnCount × availableBands) cells', () => {
            const columnCount = 3;
            const tableConfig = { addTableHeader: true, addColumnHeader: true };
            const { cells } = getAdvancedTableConfig(columnCount, 100, tableConfig);
            expect(Object.keys(cells)).toHaveLength(columnCount * 5);
        });

        it('each cell has the correct columnId', () => {
            const { cells, columnOrder } = getAdvancedTableConfig(2);
            Object.values(cells).forEach(cell => {
                expect(columnOrder).toContain(cell.columnId);
            });
        });

        it('each cell starts with an empty nodeIds array when no tableNodes provided', () => {
            const { cells } = getAdvancedTableConfig(2);
            Object.values(cells).forEach(cell => expect(cell.nodeIds).toEqual([]));
        });

        it('band height defaults to 25 for every cell', () => {
            const { cells } = getAdvancedTableConfig(2, 100, { addTableHeader: true });
            Object.values(cells).forEach(cell => expect(cell.height).toBe(25));
        });
    });


    describe('band styles', () => {
        it('uses default border colour #000000', () => {
            const { bands } = getAdvancedTableConfig(1, 100, { addTableHeader: true });
            expect(bands[hcrTableBandsTypes.TABLE_HEADER].styles.borderColor).toBe('#000000');
        });

        it('uses default header background colour #f0f8ff', () => {
            const { bands } = getAdvancedTableConfig(1, 100, { addTableHeader: true });
            expect(bands[hcrTableBandsTypes.TABLE_HEADER].styles.backgroundColor).toBe('#f0f8ff');
        });

        it('uses default column-header background colour #bfe1ff', () => {
            const { bands } = getAdvancedTableConfig(1, 100, { addColumnHeader: true });
            expect(bands[hcrTableBandsTypes.COLUMN_HEADER].styles.backgroundColor).toBe('#bfe1ff');
        });

        it('uses default body background colour #ffffff', () => {
            const { bands } = getAdvancedTableConfig(1, 100, {});
            expect(bands[hcrTableBandsTypes.COLUMN_DATA].styles.backgroundColor).toBe('#ffffff');
        });

        it('applies custom bordersColor', () => {
            const { bands } = getAdvancedTableConfig(1, 100, { addTableHeader: true, bordersColor: '#ff0000' });
            expect(bands[hcrTableBandsTypes.TABLE_HEADER].styles.borderColor).toBe('#ff0000');
        });

        it('applies custom headerColor', () => {
            const { bands } = getAdvancedTableConfig(1, 100, { addTableHeader: true, headerColor: '#123456' });
            expect(bands[hcrTableBandsTypes.TABLE_HEADER].styles.backgroundColor).toBe('#123456');
        });

        it('applies custom bodyColor', () => {
            const { bands } = getAdvancedTableConfig(1, 100, { bodyColor: '#aabbcc' });
            expect(bands[hcrTableBandsTypes.COLUMN_DATA].styles.backgroundColor).toBe('#aabbcc');
        });
    });


    describe('tableNodes parameter', () => {
        const makeNode = (band, overrides = {}) => ({
            id: `node-${band}`,
            band,
            someData: 'value',
            ...overrides
        });

        it('populates nodes map from tableNodes', () => {
            const node = makeNode(hcrTableBandsTypes.COLUMN_DATA);
            const { nodes } = getAdvancedTableConfig(1, 100, {}, [[node]]);
            expect(nodes).toHaveProperty(node.id);
        });

        it('attaches node id to the matching cell nodeIds', () => {
            const node = makeNode(hcrTableBandsTypes.COLUMN_DATA);
            const { cells, columnOrder } = getAdvancedTableConfig(1, 100, {}, [[node]]);
            const cellId = makeCellId(columnOrder[0], hcrTableBandsTypes.COLUMN_DATA);
            expect(cells[cellId].nodeIds).toContain(node.id);
        });

        it('sets cellId on the node', () => {
            const node = makeNode(hcrTableBandsTypes.COLUMN_DATA);
            const { nodes, columnOrder } = getAdvancedTableConfig(1, 100, {}, [[node]]);
            const expectedCellId = makeCellId(columnOrder[0], hcrTableBandsTypes.COLUMN_DATA);
            expect(nodes[node.id].cellId).toBe(expectedCellId);
        });

        it('ignores nodes whose band is not available', () => {
            const node = makeNode(hcrTableBandsTypes.TABLE_HEADER);
            const { nodes } = getAdvancedTableConfig(1, 100, {}, [[node]]);
            expect(Object.keys(nodes)).toHaveLength(0);
        });

        it('handles multiple columns with separate node arrays', () => {
            const node0 = makeNode(hcrTableBandsTypes.COLUMN_DATA, { id: 'node-col0' });
            const node1 = makeNode(hcrTableBandsTypes.COLUMN_DATA, { id: 'node-col1' });
            const { nodes } = getAdvancedTableConfig(2, 100, {}, [[node0], [node1]]);
            expect(nodes).toHaveProperty('node-col0');
            expect(nodes).toHaveProperty('node-col1');
        });

        it('uses an empty array for missing column node entries', () => {
            const node = makeNode(hcrTableBandsTypes.COLUMN_DATA);
            const { nodes } = getAdvancedTableConfig(2, 100, {}, [[node]]);
            expect(Object.keys(nodes)).toHaveLength(1);
        });

        it('leaves nodes empty when tableNodes is an empty array', () => {
            const { nodes } = getAdvancedTableConfig(2, 100, {}, []);
            expect(Object.keys(nodes)).toHaveLength(0);
        });
    });

    describe('edge cases', () => {
        it('handles null tableConfig gracefully', () => {
            expect(() => getAdvancedTableConfig(1, 100, null)).not.toThrow();
        });

        it('returns empty columns/cells/nodes for columnCount 0', () => {
            const { columns, cells, nodes, columnOrder } = getAdvancedTableConfig(0);
            expect(columnOrder).toHaveLength(0);
            expect(Object.keys(columns)).toHaveLength(0);
            expect(Object.keys(cells)).toHaveLength(0);
            expect(Object.keys(nodes)).toHaveLength(0);
        });
    });


    describe('group bands', () => {
        it('uses default border colour #000000', () => {
            const { bands } = getAdvancedTableConfig(1, 100, { addGroupHeader: true });
            expect(bands[hcrTableBandsTypes.GROUP_HEADER].styles.borderColor).toBe('#000000');
        });

        it('uses default header background colour #f0f8ff', () => {
            const { bands } = getAdvancedTableConfig(1, 100, { addGroupHeader: true });
            expect(bands[hcrTableBandsTypes.GROUP_HEADER].styles.backgroundColor).toBe('#f0f8ff');
        });

        it('uses default body background colour #ffffff', () => {
            const { bands } = getAdvancedTableConfig(1, 100, {});
            expect(bands[hcrTableBandsTypes.COLUMN_DATA].styles.backgroundColor).toBe('#ffffff');
        });

        it('applies custom bordersColor', () => {
            const { bands } = getAdvancedTableConfig(1, 100, { addGroupHeader: true, bordersColor: '#ff0000' });
            expect(bands[hcrTableBandsTypes.GROUP_HEADER].styles.borderColor).toBe('#ff0000');
        });

        it('applies custom headerColor', () => {
            const { bands } = getAdvancedTableConfig(1, 100, { addGroupHeader: true, headerColor: '#123456' });
            expect(bands[hcrTableBandsTypes.GROUP_HEADER].styles.backgroundColor).toBe('#123456');
        });
    });

})

describe("test getHCRTableContextMenu fn", () => {


    const columnItems = [
        { key: "create_col_at_beginning", label: "Create Column At The Beginning" },
        { key: "create_col_at_end", label: "Create Column At The End", className: "group-end" },
    ];
    const cellColumnItems = [
        { key: "create_col_after", label: "Create Column After" },
        { key: "create_col_before", label: "Create Column Before" },
    ];
    const selectItems = [
        { key: "select_all_cells", label: "Select All Cells" },
        { key: "select_all_nodes", label: "Select All Elements", className: "group-end" },
    ];
    const undoRedoItems = (copiedNodes = []) => [
        { key: "undo", label: "Undo" },
        { key: "redo", label: "Redo", className: copiedNodes?.length ? "group-end" : "" },
    ];


    describe("default / no input", () => {
        it("returns undefined when called with no arguments", () => {
            expect(getHCRTableContextMenu()).toBeUndefined();
        });

        it("returns undefined when called with an empty object", () => {
            expect(getHCRTableContextMenu({})).toBeUndefined();
        });

        it("returns undefined when called with null", () => {
            expect(getHCRTableContextMenu(null)).toBeUndefined();
        });

        it("returns undefined for an unknown menuType", () => {
            expect(getHCRTableContextMenu({ menuType: "UNKNOWN" })).toBeUndefined();
        });
    });


    describe("menuType: BAND", () => {
        it("includes columnItems, selectItems and undoRedoItems", () => {
            const result = getHCRTableContextMenu({ menuType: hcrContextMenuTypes.BAND });
            expect(result).toEqual(expect.arrayContaining([...columnItems, ...selectItems]));
        });

        it("shows 'Delete Row' when row_deleted is false (default)", () => {
            const result = getHCRTableContextMenu({ menuType: hcrContextMenuTypes.BAND });
            expect(result).toContainEqual({ key: "delete_row", label: "Delete Row" });
            expect(result).not.toContainEqual(expect.objectContaining({ key: "create_row" }));
        });

        it("shows 'Create Row' when row_deleted is true", () => {
            const result = getHCRTableContextMenu({ menuType: hcrContextMenuTypes.BAND, row_deleted: true });
            expect(result).toContainEqual({ key: "create_row", label: "Create Row" });
            expect(result).not.toContainEqual(expect.objectContaining({ key: "delete_row" }));
        });

        it("redo has no group-end className when copiedNodes is empty", () => {
            const result = getHCRTableContextMenu({ menuType: hcrContextMenuTypes.BAND, copiedNodes: [] });
            const redo = result.find(item => item.key === "redo");
            expect(redo.className).toBe("");
        });

        it("redo has group-end className when copiedNodes is non-empty", () => {
            const result = getHCRTableContextMenu({ menuType: hcrContextMenuTypes.BAND, copiedNodes: ["node1"] });
            const redo = result.find(item => item.key === "redo");
            expect(redo.className).toBe("group-end");
        });

        it("returns items in the correct order", () => {
            const result = getHCRTableContextMenu({ menuType: hcrContextMenuTypes.BAND });
            const keys = result.map(i => i.key);
            expect(keys).toEqual([
                "create_col_at_beginning",
                "create_col_at_end",
                "delete_row",
                "select_all_cells",
                "select_all_nodes",
                "undo",
                "redo",
            ]);
        });

        it("does not include cell or node-specific items", () => {
            const result = getHCRTableContextMenu({ menuType: hcrContextMenuTypes.BAND });
            const keys = result.map(i => i.key);
            expect(keys).not.toContain("create_col_after");
            expect(keys).not.toContain("create_col_before");
            expect(keys).not.toContain("delete_column");
            expect(keys).not.toContain("paste_node");
            expect(keys).not.toContain("cut_node");
            expect(keys).not.toContain("copy_node");
        });
    });

    describe("menuType: CELL", () => {
        it("includes cellColumnItems, columnItems, selectItems and undoRedoItems", () => {
            const result = getHCRTableContextMenu({ menuType: hcrContextMenuTypes.CELL });
            expect(result).toEqual(expect.arrayContaining([...cellColumnItems, ...columnItems, ...selectItems]));
        });

        it("shows 'Delete Cell' when cell_deleted is false (default)", () => {
            const result = getHCRTableContextMenu({ menuType: hcrContextMenuTypes.CELL });
            expect(result).toContainEqual({ key: "delete_cell", label: "Delete Cell" });
            expect(result).not.toContainEqual(expect.objectContaining({ key: "create_cell" }));
        });

        it("shows 'Create Cell' when cell_deleted is true", () => {
            const result = getHCRTableContextMenu({ menuType: hcrContextMenuTypes.CELL, cell_deleted: true });
            expect(result).toContainEqual({ key: "create_cell", label: "Create Cell" });
            expect(result).not.toContainEqual(expect.objectContaining({ key: "delete_cell" }));
        });

        it("includes 'Paste' when copiedNodes is non-empty and cell is not deleted", () => {
            const result = getHCRTableContextMenu({
                menuType: hcrContextMenuTypes.CELL,
                copiedNodes: ["node1"],
                cell_deleted: false,
            });
            expect(result).toContainEqual({ key: "paste_node", label: "Paste" });
        });

        it("excludes 'Paste' when copiedNodes is empty", () => {
            const result = getHCRTableContextMenu({
                menuType: hcrContextMenuTypes.CELL,
                copiedNodes: [],
                cell_deleted: false,
            });
            expect(result).not.toContainEqual(expect.objectContaining({ key: "paste_node" }));
        });

        it("excludes 'Paste' when cell_deleted is true even if copiedNodes is non-empty", () => {
            const result = getHCRTableContextMenu({
                menuType: hcrContextMenuTypes.CELL,
                copiedNodes: ["node1"],
                cell_deleted: true,
            });
            expect(result).not.toContainEqual(expect.objectContaining({ key: "paste_node" }));
        });

        it("returns items in the correct order (without Paste)", () => {
            const result = getHCRTableContextMenu({ menuType: hcrContextMenuTypes.CELL });
            const keys = result.map(i => i.key);
            expect(keys).toEqual([
                "create_col_after",
                "create_col_before",
                "create_col_at_beginning",
                "create_col_at_end",
                "delete_column",
                "delete_cell",
                "select_all_cells",
                "select_all_nodes",
                "undo",
                "redo",
            ]);
        });

        it("returns items in the correct order (with Paste)", () => {
            const result = getHCRTableContextMenu({
                menuType: hcrContextMenuTypes.CELL,
                copiedNodes: ["node1"],
            });
            const keys = result.map(i => i.key);
            expect(keys).toEqual([
                "create_col_after",
                "create_col_before",
                "create_col_at_beginning",
                "create_col_at_end",
                "delete_column",
                "delete_cell",
                "select_all_cells",
                "select_all_nodes",
                "undo",
                "redo",
                "paste_node",
            ]);
        });

        it("does not include band or node-specific items", () => {
            const result = getHCRTableContextMenu({ menuType: hcrContextMenuTypes.CELL });
            const keys = result.map(i => i.key);
            expect(keys).not.toContain("delete_row");
            expect(keys).not.toContain("create_row");
            expect(keys).not.toContain("cut_node");
            expect(keys).not.toContain("copy_node");
            expect(keys).not.toContain("delete_node");
        });
    });

    describe("menuType: NODE", () => {
        it("returns cut, copy, delete and undoRedo items", () => {
            const result = getHCRTableContextMenu({ menuType: hcrContextMenuTypes.NODE });
            expect(result).toEqual([
                { key: "cut_node", label: "Cut" },
                { key: "copy_node", label: "Copy" },
                { key: "delete_node", label: "Delete", className: "group-end" },
                { key: "undo", label: "Undo" },
                { key: "redo", label: "Redo", className: "" },
            ]);
        });

        it("returns items in the correct order", () => {
            const result = getHCRTableContextMenu({ menuType: hcrContextMenuTypes.NODE });
            const keys = result.map(i => i.key);
            expect(keys).toEqual(["cut_node", "copy_node", "delete_node", "undo", "redo"]);
        });

        it("does not include column, cell, row, or select items", () => {
            const result = getHCRTableContextMenu({ menuType: hcrContextMenuTypes.NODE });
            const keys = result.map(i => i.key);
            expect(keys).not.toContain("create_col_at_beginning");
            expect(keys).not.toContain("delete_row");
            expect(keys).not.toContain("delete_cell");
            expect(keys).not.toContain("select_all_cells");
            expect(keys).not.toContain("paste_node");
        });

        it("redo has no group-end className regardless of copiedNodes", () => {
            const result = getHCRTableContextMenu({
                menuType: hcrContextMenuTypes.NODE,
                copiedNodes: ["node1"],
            });
            const redo = result.find(i => i.key === "redo");
            expect(redo).toBeDefined();
        });
    });
});


describe('test getHCRTableOutlineDSContextMenu fn', () => {
    test('should return an empty array for "parameters" menuType', () => {
        const result = getHCRTableOutlineDSContextMenu({ menuType: 'parameters' });
        expect(result).toEqual([{
            key: "create_parameter",
            label: "Create Parameter",
        }]);
    });

    test('should return an empty array for "parameters-item" menuType', () => {
        const result = getHCRTableOutlineDSContextMenu({ menuType: 'parameters-item' });
        expect(result).toEqual([
            {
                key: "delete_parameter_item",
                label: "Delete",
            }
        ]);
    });

    test('should return an array with "create_field" for "fields" menuType', () => {
        const result = getHCRTableOutlineDSContextMenu({ menuType: 'fields' });
        expect(result).toEqual([{ key: 'create_field', label: 'Create Field' }]);
    });

    test('should return an array with "delete_fields_item" for "fields-item" menuType', () => {
        const result = getHCRTableOutlineDSContextMenu({ menuType: 'fields-item' });
        expect(result).toEqual([{ key: 'delete_fields_item', label: 'Delete' }]);
    });

    test('should return an empty array for "variables" menuType', () => {
        const result = getHCRTableOutlineDSContextMenu({ menuType: 'variables' });
        expect(result).toEqual([]);
    });

    test('should return an empty array for "variables-item" menuType', () => {
        const result = getHCRTableOutlineDSContextMenu({ menuType: 'variables-item' });
        expect(result).toEqual([]);
    });


    test('should return an empty array for "calculations-item" menuType', () => {
        const result = getHCRTableOutlineDSContextMenu({ menuType: 'calculations-item' });
        expect(result).toHaveLength(1);
    });

    test('should return an empty array for "groups" menuType', () => {
        const result = getHCRTableOutlineDSContextMenu({ menuType: 'groups' });
        expect(result).toHaveLength(1);
    });

    test('should return an empty array for "groups-item" menuType', () => {
        const result = getHCRTableOutlineDSContextMenu({ menuType: 'groups-item' });
        expect(result).toHaveLength(1);
    });
});

describe("test checkIfBandIsDeleted fn", () => {
    it("should return false when bandType or cells is empty", () => {
        expect(checkIfBandIsDeleted("", {}, {})).toBe(false);
        expect(checkIfBandIsDeleted("bandType", {}, {})).toBe(false);
        expect(checkIfBandIsDeleted("", {}, "")).toBe(false);
    });

    it("should return false when there are cells with non-matching bandType", () => {
        const cells = {
            "cell1": { bandType: "otherBandType", deleted: false },
            "cell2": { bandType: "otherBandType", deleted: false },
        };
        expect(checkIfBandIsDeleted("bandType", cells, "")).toBe(false);
    });

    it("should return false when there are cells with non-matching groupField", () => {
        const cells = {
            "cell1": { bandType: "bandType", groupField: "otherGroupField", deleted: false },
            "cell2": { bandType: "bandType", groupField: "otherGroupField", deleted: false },
        };
        expect(checkIfBandIsDeleted("bandType", cells, "groupField")).toBe(false);
    });

    it("should return true when all cells have the matching bandType and groupField and are deleted", () => {
        const cells = {
            "cell1": { bandType: "bandType", groupField: "groupField", deleted: true },
            "cell2": { bandType: "bandType", groupField: "groupField", deleted: true },
        };
        expect(checkIfBandIsDeleted("bandType", cells, "groupField")).toBe(true);
    });

    it("should return false when not all cells have the matching bandType and groupField and are deleted", () => {
        const cells = {
            "cell1": { bandType: "bandType", groupField: "groupField", deleted: true },
            "cell2": { bandType: "bandType", groupField: "groupField", deleted: false },
        };
        expect(checkIfBandIsDeleted("bandType", cells, "groupField")).toBe(false);
    });
});

jest.mock("uuid", () => ({ v4: () => `uuid-${Math.random()}` }));

describe("test updateCanvasTabViewComponent", () => {


    const BANDS = { HEADER: "HEADER", DATA: "DATA", FOOTER: "FOOTER", GROUP: "GROUP" };
    const hcrTableBandOrder = [BANDS.HEADER, BANDS.DATA, BANDS.FOOTER, BANDS.GROUP];
    const hcrTableBandsTypes = { COLUMN_HEADER: BANDS.HEADER, COLUMN_DATA: BANDS.DATA };
    const HCR_TABLE_CELL_PROPERTIES = ["backgroundColor", "color"];

    const makeCellId = (colId, band, field) =>
        `${colId}__${band}${field ? `__${field}` : ""}`;

    const isGroupBand = (band) => band === BANDS.GROUP;

    const getAvailableBands = (bandsMap) => Object.keys(bandsMap);

    const createCell = (colId, band, width, height, field) => ({
        id: makeCellId(colId, band, field),
        columnId: colId,
        bandType: band,
        width,
        height,
        nodeIds: [],
        deleted: false,
        ...(field ? { groupField: field } : {}),
    });

    const getDroppedNodeData = (record, cell) => ({
        id: `node-uuid-${++uuidCounter}`,
        cellId: cell.id,
        band: cell.bandType,
        width: cell.width,
        height: cell.height,
        label: record.label || record.name,
    });

    const getPastedNodeData = (n, cell) => ({
        ...n,
        id: `node-uuid-${++uuidCounter}`,
        cellId: cell.id,
    });

    function makeNode(overrides = {}) {
        const col1 = "col-1";
        const col2 = "col-2";

        const node = {
            id: "table-1",
            width: 200,
            columnOrder: [col1, col2],
            columns: {
                [col1]: { id: col1, width: 100, name: "Column 1" },
                [col2]: { id: col2, width: 100, name: "Column 2" },
            },
            bands: {
                [BANDS.HEADER]: { height: 30 },
                [BANDS.DATA]: { height: 30 },
            },
            cells: {
                [makeCellId(col1, BANDS.HEADER)]: createCell(col1, BANDS.HEADER, 100, 30),
                [makeCellId(col1, BANDS.DATA)]: createCell(col1, BANDS.DATA, 100, 30),
                [makeCellId(col2, BANDS.HEADER)]: createCell(col2, BANDS.HEADER, 100, 30),
                [makeCellId(col2, BANDS.DATA)]: createCell(col2, BANDS.DATA, 100, 30),
            },
            nodes: {},
            selectedColumnId: null,
            selectedBandType: null,
            selectedCells: [],
            selectedNodes: [],
            outlineDsSelectedField: null,
            selectedTable: null,
            outlineDSFields: [],
            ...overrides,
        };

        return node;
    }


    describe("addColumn", () => {
        test("inserts a new column at the given index", () => {
            const node = makeNode();
            updateCanvasTabViewComponent(node, "addColumn", { columnIndex: 1, width: 80 });

            expect(node.columnOrder).toHaveLength(3);
            expect(node.columnOrder[1]).toMatch(/^uuid-/);
        });

        test("increases node width by the added column width", () => {
            const node = makeNode();
            updateCanvasTabViewComponent(node, "addColumn", { columnIndex: 0, width: 50 });
            expect(node.width).toBe(250);
        });

        test("renumbers column names after the insertion point", () => {
            const node = makeNode();
            updateCanvasTabViewComponent(node, "addColumn", { columnIndex: 1, width: 80 });
            expect(node.columns["col-2"].name).toBe("Column 3");
        });
    });


    describe("createCell", () => {
        test("marks the cell as not deleted", () => {
            const node = makeNode();
            const cId = makeCellId("col-1", BANDS.HEADER);
            node.cells[cId].deleted = true;
            updateCanvasTabViewComponent(node, "createCell", { cellId: cId });
            expect(node.cells[cId].deleted).toBe(false);
        });
    });

    describe("removeCell", () => {
        test("marks cell as deleted and removes its nodes", () => {
            const node = makeNode();
            const cId = makeCellId("col-1", BANDS.HEADER);
            node.nodes["n1"] = { id: "n1", cellId: cId };
            node.cells[cId].nodeIds = ["n1"];

            updateCanvasTabViewComponent(node, "removeCell", { cellId: cId });

            expect(node.cells[cId].deleted).toBe(true);
            expect(node.cells[cId].nodeIds).toEqual([]);
            expect(node.nodes["n1"]).toBeUndefined();
        });

        test("clears selection if removed cell was selected", () => {
            const node = makeNode({ selectedCells: [makeCellId("col-1", BANDS.HEADER)] });
            updateCanvasTabViewComponent(node, "removeCell", {
                cellId: makeCellId("col-1", BANDS.HEADER),
            });
            expect(node.selectedCells).toEqual([]);
            expect(node.selectedNodes).toEqual([]);
        });

        test("does nothing if cell does not exist", () => {
            const node = makeNode();
            expect(() =>
                updateCanvasTabViewComponent(node, "removeCell", { cellId: "nonexistent" })
            ).not.toThrow();
        });
    });


    describe("removeRow", () => {
        test("marks all specified cells as deleted", () => {
            const node = makeNode();
            const ids = [makeCellId("col-1", BANDS.HEADER), makeCellId("col-2", BANDS.HEADER)];
            updateCanvasTabViewComponent(node, "removeRow", { cellIds: ids });
            ids.forEach((id) => expect(node.cells[id].deleted).toBe(true));
        });

        test("does nothing when cellIds is empty", () => {
            const node = makeNode();
            const before = JSON.stringify(node.cells);
            updateCanvasTabViewComponent(node, "removeRow", { cellIds: [] });
            expect(JSON.stringify(node.cells)).toBe(before);
        });
    });

    describe("createRow", () => {
        test("marks all specified cells as not deleted", () => {
            const node = makeNode();
            const ids = [makeCellId("col-1", BANDS.DATA), makeCellId("col-2", BANDS.DATA)];
            ids.forEach((id) => (node.cells[id].deleted = true));
            updateCanvasTabViewComponent(node, "createRow", { cellIds: ids });
            ids.forEach((id) => expect(node.cells[id].deleted).toBe(false));
        });
    });


    describe("resizeCell", () => {
        test("updates band height across all columns", () => {
            const node = makeNode();
            const cId = makeCellId("col-1", BANDS.DATA);
            updateCanvasTabViewComponent(node, "resizeCell", { cellId: cId, height: 60 });
            expect(node.bands[BANDS.DATA].height).toBe(60);
            expect(node.cells[makeCellId("col-2", BANDS.DATA)].height).toBe(30);
        });

        test("enforces minimum band height of 25", () => {
            const node = makeNode();
            const cId = makeCellId("col-1", BANDS.DATA);
            updateCanvasTabViewComponent(node, "resizeCell", { cellId: cId, height: 5 });
            expect(node.bands[BANDS.DATA].height).toBe(25);
        });
    });


    describe("addNode", () => {

        test("adds a node to the given cell", () => {
            const node = makeNode();
            const cId = makeCellId("col-1", BANDS.DATA);
            updateCanvasTabViewComponent(node, "addNode", {
                cellId: cId,
                record: { name: "Status", label: "Status" },
            });
            expect(node.cells[cId].nodeIds).toHaveLength(1);
            expect(Object.keys(node.nodes)).toHaveLength(1);
        });

        test("splits cell width equally when multiple nodes are present", () => {
            const node = makeNode();
            const cId = makeCellId("col-1", BANDS.DATA);
            // First node
            updateCanvasTabViewComponent(node, "addNode", {
                cellId: cId,
                record: { name: "A" },
            });
            // Second node
            updateCanvasTabViewComponent(node, "addNode", {
                cellId: cId,
                record: { name: "B" },
            });
            const nodeIds = node.cells[cId].nodeIds;
            nodeIds.forEach((nId) => {
                expect(node.nodes[nId].width).toBe(50); // 100 / 2
            });
        });

        test("does nothing if cell does not exist", () => {
            const node = makeNode();
            expect(() =>
                updateCanvasTabViewComponent(node, "addNode", {
                    cellId: "no-such-cell",
                    record: { name: "X" },
                })
            ).not.toThrow();
            expect(Object.keys(node.nodes)).toHaveLength(0);
        });
    });


    describe("deleteNode", () => {
        function nodeWithNodes() {
            const node = makeNode();
            const cId = makeCellId("col-1", BANDS.DATA);
            node.nodes["n1"] = { id: "n1", cellId: cId };
            node.nodes["n2"] = { id: "n2", cellId: cId };
            node.cells[cId].nodeIds = ["n1", "n2"];
            return node;
        }

        test("deletes nodes in selectedNodes and clears selection", () => {
            const node = nodeWithNodes();
            node.selectedNodes = ["n1", "n2"];
            updateCanvasTabViewComponent(node, "deleteNode", {});
            expect(node.nodes["n1"]).toBeUndefined();
            expect(node.nodes["n2"]).toBeUndefined();
            expect(node.selectedNodes).toEqual([]);
        });

        test("deletes a specific node by nodeId when nothing is selected", () => {
            const node = nodeWithNodes();
            node.selectedNodes = [];
            updateCanvasTabViewComponent(node, "deleteNode", { nodeId: "n1" });
            expect(node.nodes["n1"]).toBeUndefined();
            expect(node.nodes["n2"]).toBeDefined();
        });

        test("removes the node from its cell's nodeIds", () => {
            const node = nodeWithNodes();
            node.selectedNodes = ["n1"];
            updateCanvasTabViewComponent(node, "deleteNode", {});
            const cId = makeCellId("col-1", BANDS.DATA);
            expect(node.cells[cId].nodeIds).not.toContain("n1");
        });
    });

    describe("moveNode", () => {
        test("moves node from source cell to target cell", () => {
            const node = makeNode();
            const srcId = makeCellId("col-1", BANDS.DATA);
            const tgtId = makeCellId("col-2", BANDS.DATA);
            node.nodes["n1"] = { id: "n1", cellId: srcId, width: 100, height: 30, band: BANDS.DATA };
            node.cells[srcId].nodeIds = ["n1"];

            updateCanvasTabViewComponent(node, "moveNode", {
                nodeId: "n1",
                targetCellId: tgtId,
            });

            expect(node.cells[srcId].nodeIds).not.toContain("n1");
            expect(node.cells[tgtId].nodeIds).toContain("n1");
            expect(node.nodes["n1"].cellId).toBe(tgtId);
        });

        test("redistributes widths in source cell after move", () => {
            const node = makeNode();
            const srcId = makeCellId("col-1", BANDS.DATA);
            const tgtId = makeCellId("col-2", BANDS.DATA);
            node.nodes["n1"] = { id: "n1", cellId: srcId, width: 50, height: 30, band: BANDS.DATA };
            node.nodes["n2"] = { id: "n2", cellId: srcId, width: 50, height: 30, band: BANDS.DATA };
            node.cells[srcId].nodeIds = ["n1", "n2"];

            updateCanvasTabViewComponent(node, "moveNode", {
                nodeId: "n1",
                targetCellId: tgtId,
            });

            expect(node.nodes["n2"].width).toBe(100);
        });

        test("does nothing if nodeId or targetCellId is missing", () => {
            const node = makeNode();
            expect(() =>
                updateCanvasTabViewComponent(node, "moveNode", {
                    nodeId: "ghost",
                    targetCellId: makeCellId("col-2", BANDS.DATA),
                })
            ).not.toThrow();
        });
    });


    describe("selectColumn", () => {
        test("sets selectedColumnId and clears everything else", () => {
            const node = makeNode({
                selectedNodes: ["n1"],
                selectedCells: ["c1"],
                selectedBandType: BANDS.DATA,
            });
            updateCanvasTabViewComponent(node, "selectColumn", { columnId: "col-1" });
            expect(node.selectedColumnId).toBe("col-1");
            expect(node.selectedBandType).toBeNull();
            expect(node.selectedCells).toEqual([]);
            expect(node.selectedNodes).toEqual([]);
        });
    });

    describe("selectRow", () => {
        test("sets selectedBandType and clears everything else", () => {
            const node = makeNode({ selectedColumnId: "col-1" });
            updateCanvasTabViewComponent(node, "selectRow", { bandType: BANDS.DATA });
            expect(node.selectedBandType).toBe(BANDS.DATA);
            expect(node.selectedColumnId).toBeNull();
        });
    });

    describe("selectCells", () => {
        test("sets selectedCells, columnId, and bandType", () => {
            const node = makeNode();
            const cells = [makeCellId("col-1", BANDS.DATA)];
            updateCanvasTabViewComponent(node, "selectCells", {
                selectedCells: cells,
                columnId: "col-1",
                bandType: BANDS.DATA,
            });
            expect(node.selectedCells).toEqual(cells);
            expect(node.selectedColumnId).toBe("col-1");
            expect(node.selectedBandType).toBe(BANDS.DATA);
        });
    });

    describe("selectNode", () => {
        test("sets selectedNodes to a single-item array", () => {
            const node = makeNode({ selectedCells: ["c1"] });
            updateCanvasTabViewComponent(node, "selectNode", { nodeId: "n1" });
            expect(node.selectedNodes).toEqual(["n1"]);
            expect(node.selectedCells).toEqual([]);
        });
    });

    describe("selectNodes", () => {
        test("sets selectedNodes to the provided array", () => {
            const node = makeNode();
            updateCanvasTabViewComponent(node, "selectNodes", {
                selectedNodes: ["n1", "n2"],
            });
            expect(node.selectedNodes).toEqual(["n1", "n2"]);
        });
    });

    describe("clearSelection", () => {
        test("clears all selection fields", () => {
            const node = makeNode({
                selectedColumnId: "col-1",
                selectedBandType: BANDS.DATA,
                selectedCells: ["c1"],
                selectedNodes: ["n1"],
                outlineDsSelectedField: "field1",
                selectedTable: "table-1",
            });
            updateCanvasTabViewComponent(node, "clearSelection", {});
            expect(node.selectedColumnId).toBeNull();
            expect(node.selectedBandType).toBeNull();
            expect(node.selectedCells).toEqual([]);
            expect(node.selectedNodes).toEqual([]);
            expect(node.outlineDsSelectedField).toBeNull();
            expect(node.selectedTable).toBeNull();
        });
    });


    describe("selectOutlineDSField", () => {
        test("sets outlineDsSelectedField and clears other selections", () => {
            const node = makeNode({ selectedNodes: ["n1"] });
            updateCanvasTabViewComponent(node, "selectOutlineDSField", {
                outlineDsSelectedField: "myField",
            });
            expect(node.outlineDsSelectedField).toBe("myField");
            expect(node.selectedNodes).toEqual([]);
            expect(node.selectedCells).toEqual([]);
        });
    });

    describe("selectTable", () => {
        test("sets selectedTable to node.id and clears everything else", () => {
            const node = makeNode({ selectedNodes: ["n1"] });
            updateCanvasTabViewComponent(node, "selectTable", {});
            expect(node.selectedTable).toBe("table-1");
            expect(node.selectedNodes).toEqual([]);
        });
    });


    describe("pasteCopiedNodes", () => {

        test("pastes into selectedCells[0] when cells are selected", () => {
            const node = makeNode();
            const cId = makeCellId("col-1", BANDS.DATA);
            node.selectedCells = [cId];
            const copiedNodes = [{ id: "n-copy", cellId: cId, width: 100, height: 30 }];
            updateCanvasTabViewComponent(node, "pasteCopiedNodes", { copiedNodes });
            expect(node.cells[cId].nodeIds).toHaveLength(1);
        });

        test("pastes into the cell of each selectedNode", () => {
            const node = makeNode();
            const cId = makeCellId("col-1", BANDS.DATA);
            node.nodes["n1"] = { id: "n1", cellId: cId };
            node.cells[cId].nodeIds = ["n1"];
            node.selectedNodes = ["n1"];
            const copiedNodes = [{ id: "n-copy", cellId: cId }];
            updateCanvasTabViewComponent(node, "pasteCopiedNodes", { copiedNodes });
            // original n1 + 1 pasted node
            expect(node.cells[cId].nodeIds).toHaveLength(2);
        });

        test("pastes to original cell when there is no selection", () => {
            const node = makeNode();
            const cId = makeCellId("col-1", BANDS.DATA);
            const copiedNodes = [{ id: "n-orig", cellId: cId, width: 100, height: 30 }];
            updateCanvasTabViewComponent(node, "pasteCopiedNodes", { copiedNodes });
            expect(node.cells[cId].nodeIds).toHaveLength(1);
        });
    });


    describe("cutNodes", () => {
        test("removes specified nodes and updates cell nodeIds", () => {
            const node = makeNode();
            const cId = makeCellId("col-1", BANDS.DATA);
            node.nodes["n1"] = { id: "n1", cellId: cId };
            node.cells[cId].nodeIds = ["n1"];

            updateCanvasTabViewComponent(node, "cutNodes", {
                cutNodesData: [{ id: "n1", cellId: cId }],
            });

            expect(node.nodes["n1"]).toBeUndefined();
            expect(node.cells[cId].nodeIds).not.toContain("n1");
        });
    });


    describe("updateNodeProperties", () => {
        test("merges properties into each selected node", () => {
            const node = makeNode();
            node.nodes["n1"] = { id: "n1", cellId: "c1", label: "Old" };
            node.nodes["n2"] = { id: "n2", cellId: "c1", label: "Old" };
            node.selectedNodes = ["n1", "n2"];
            updateCanvasTabViewComponent(node, "updateNodeProperties", {
                properties: { label: "New", bold: true },
            });
            expect(node.nodes["n1"].label).toBe("New");
            expect(node.nodes["n1"].bold).toBe(true);
            expect(node.nodes["n2"].label).toBe("New");
        });

        test("does nothing when no nodes are selected", () => {
            const node = makeNode();
            node.nodes["n1"] = { id: "n1", label: "Keep" };
            node.selectedNodes = [];
            updateCanvasTabViewComponent(node, "updateNodeProperties", {
                properties: { label: "Changed" },
            });
            expect(node.nodes["n1"].label).toBe("Keep");
        });
    });


    describe("updateCellProperties", () => {
        test("merges properties into each specified cell", () => {
            const node = makeNode();
            const cId = makeCellId("col-1", BANDS.DATA);
            updateCanvasTabViewComponent(node, "updateCellProperties", {
                cellIds: [cId],
                properties: { backgroundColor: "#fff" },
            });
            expect(node.cells[cId].backgroundColor).toBe("#fff");
        });
    });


    describe("createNewColAndAddNodes", () => {

        test("appends a new column to the end", () => {
            const node = makeNode();
            updateCanvasTabViewComponent(node, "createNewColAndAddNodes", {
                width: 90,
                record: { name: "City", label: "City" },
            });
            expect(node.columnOrder).toHaveLength(3);
            expect(node.width).toBe(290);
        });

    });


    describe("tableProperties", () => {
        test("sets arbitrary top-level properties on the node", () => {
            const node = makeNode();
            updateCanvasTabViewComponent(node, "tableProperties", {
                properties: { showBorders: true, theme: "dark" },
            });
            expect(node.showBorders).toBe(true);
            expect(node.theme).toBe("dark");
        });
    });


    describe("unknown action", () => {
        test("does not throw and leaves node unchanged", () => {
            const node = makeNode();
            const before = JSON.stringify(node);
            expect(() =>
                updateCanvasTabViewComponent(node, "UNKNOWN_ACTION", {})
            ).not.toThrow();
            expect(JSON.stringify(node)).toBe(before);
        });
    });

    describe("null payload", () => {
        test("handles null payload gracefully for a no-op action", () => {
            const node = makeNode();
            expect(() =>
                updateCanvasTabViewComponent(node, "clearSelection", null)
            ).not.toThrow();
        });
    });
})



describe("test getTableOutlinedata fn", () => {
    beforeEach(() => {
        jest.clearAllMocks();
    });

    const BAND = hcrTableBandsTypes;
    function makeColumn(id, name) {
        return { id, name };
    }

    function makeCell(columnId, bandType, nodeIds = [], opts = {}) {
        return {
            id: makeCellId(columnId, bandType, opts.groupField),
            bandType,
            columnId,
            nodeIds,
            deleted: opts.deleted || false,
            ...(opts.groupField ? { groupField: opts.groupField } : {}),
        };
    }

    function makeNode(id, label) {
        return { id, label };
    }

    function makeTableNode(overrides = {}) {
        const col1 = makeColumn("col-1", "Column 1");
        const col2 = makeColumn("col-2", "Column 2");
        const node1 = makeNode("node-1", "Label 1");
        const cell1 = makeCell("col-1", BAND.COLUMN_DATA, ["node-1"]);
        const cell2 = makeCell("col-2", BAND.COLUMN_DATA, []);

        return {
            id: "table-1",
            columnOrder: ["col-1", "col-2"],
            columns: { "col-1": col1, "col-2": col2 },
            bands: {
                [BAND.COLUMN_DATA]: { height: 30 },
            },
            cells: {
                [cell1.id]: cell1,
                [cell2.id]: cell2,
            },
            nodes: { "node-1": node1 },
            ...overrides,
        };
    }

    describe("early returns", () => {
        test("returns [] when called with no arguments", () => {
            expect(getTableOutlinedata()).toEqual([]);
        });

        test("returns [] when tableNode is null", () => {
            expect(getTableOutlinedata(null)).toEqual([]);
        });

        test("returns [] when tableNode is an empty object", () => {
            expect(getTableOutlinedata({})).toEqual([]);
        });

        test("returns [] when no bands are available", () => {
            const tableNode = makeTableNode({ bands: {} });
            expect(getTableOutlinedata(tableNode)).toEqual([]);
        });

        test("returns [] when all band values are falsy", () => {
            const tableNode = makeTableNode({ bands: { [BAND.COLUMN_DATA]: null } });
            expect(getTableOutlinedata(tableNode)).toEqual([]);
        });
    });

    describe("regular (non-group) bands", () => {
        test("returns one band entry per available band", () => {
            const tableNode = makeTableNode();
            const result = getTableOutlinedata(tableNode);
            expect(result).toHaveLength(1);
        });

        test("band entry has correct shape", () => {
            const tableNode = makeTableNode();
            const [band] = getTableOutlinedata(tableNode);
            expect(band).toMatchObject({
                key: BAND.COLUMN_DATA,
                bandType: BAND.COLUMN_DATA,
                selectable: false,
                contextMenu: true,
                menuType: hcrContextMenuTypes.BAND,
                tableId: "table-1",
                deleted: false,
            });
        });

        test("band entry tableData references the original tableNode", () => {
            const tableNode = makeTableNode();
            const [band] = getTableOutlinedata(tableNode);
            expect(band.tableData).toBe(tableNode);
        });

        test("band deleted is true when all its cells are deleted", () => {
            const cell1 = makeCell("col-1", BAND.COLUMN_DATA, [], { deleted: true });
            const cell2 = makeCell("col-2", BAND.COLUMN_DATA, [], { deleted: true });
            const tableNode = makeTableNode({
                cells: { [cell1.id]: cell1, [cell2.id]: cell2 },
                nodes: {},
            });
            const [band] = getTableOutlinedata(tableNode);
            expect(band.deleted).toBe(true);
        });

        test("band deleted is false when at least one cell is not deleted", () => {
            const cell1 = makeCell("col-1", BAND.COLUMN_DATA, [], { deleted: true });
            const cell2 = makeCell("col-2", BAND.COLUMN_DATA, [], { deleted: false });
            const tableNode = makeTableNode({
                cells: { [cell1.id]: cell1, [cell2.id]: cell2 },
                nodes: {},
            });
            const [band] = getTableOutlinedata(tableNode);
            expect(band.deleted).toBe(false);
        });

        test("children contains one entry per column", () => {
            const tableNode = makeTableNode();
            const [band] = getTableOutlinedata(tableNode);
            expect(band.children).toHaveLength(2);
        });

        test("filters out null children when cell does not exist", () => {
            const cell1 = makeCell("col-1", BAND.COLUMN_DATA, []);
            const tableNode = makeTableNode({
                cells: { [cell1.id]: cell1 },
                nodes: {},
            });
            const [band] = getTableOutlinedata(tableNode);
            expect(band.children).toHaveLength(1);
        });

        test("cell child has correct shape", () => {
            const tableNode = makeTableNode();
            const [band] = getTableOutlinedata(tableNode);
            const cell = band.children[0];
            expect(cell).toMatchObject({
                key: makeCellId("col-1", BAND.COLUMN_DATA),
                bandType: BAND.COLUMN_DATA,
                columnId: "col-1",
                cellId: makeCellId("col-1", BAND.COLUMN_DATA),
                isNode: false,
                contextMenu: true,
                menuType: hcrContextMenuTypes.CELL,
                tableId: "table-1",
                deleted: false,
                selectable: true,
                selectKey: "cell",
            });
        });

        test("cell child title uses column name", () => {
            const tableNode = makeTableNode();
            const [band] = getTableOutlinedata(tableNode);
            expect(band.children[0].title).toBe("Column 1");
        });

        test("cell child title falls back to Column N when column name is absent", () => {
            const tableNode = makeTableNode({
                columns: {
                    "col-1": { id: "col-1", name: "" },
                    "col-2": { id: "col-2", name: "" },
                },
            });
            const [band] = getTableOutlinedata(tableNode);
            expect(band.children[0].title).toBe("Column 1");
            expect(band.children[1].title).toBe("Column 2");
        });

        test("cell child deleted reflects cell.deleted", () => {
            const cell1 = makeCell("col-1", BAND.COLUMN_DATA, [], { deleted: true });
            const cell2 = makeCell("col-2", BAND.COLUMN_DATA, []);
            const tableNode = makeTableNode({
                cells: { [cell1.id]: cell1, [cell2.id]: cell2 },
                nodes: {},
            });
            const [band] = getTableOutlinedata(tableNode);
            expect(band.children[0].deleted).toBe(true);
            expect(band.children[1].deleted).toBe(false);
        });

        test("node child has correct shape", () => {
            const tableNode = makeTableNode();
            const [band] = getTableOutlinedata(tableNode);
            const nodeChild = band.children[0].children[0];
            expect(nodeChild).toMatchObject({
                key: "node-1",
                title: "Label 1",
                isNode: true,
                bandType: BAND.COLUMN_DATA,
                columnId: "col-1",
                cellId: makeCellId("col-1", BAND.COLUMN_DATA),
                nodeId: "node-1",
                className: "ant-tree-title-node-title",
                contextMenu: true,
                menuType: hcrContextMenuTypes.NODE,
                tableId: "table-1",
                selectable: true,
                selectKey: "node",
            });
        });

        test("cell with no nodes has empty children array", () => {
            const tableNode = makeTableNode();
            const [band] = getTableOutlinedata(tableNode);
            expect(band.children[1].children).toEqual([]);
        });

        test("handles multiple available bands", () => {
            const cell1H = makeCell("col-1", BAND.COLUMN_HEADER, []);
            const cell2H = makeCell("col-2", BAND.COLUMN_HEADER, []);
            const cell1D = makeCell("col-1", BAND.COLUMN_DATA, []);
            const cell2D = makeCell("col-2", BAND.COLUMN_DATA, []);
            const tableNode = makeTableNode({
                bands: {
                    [BAND.COLUMN_HEADER]: { height: 30 },
                    [BAND.COLUMN_DATA]: { height: 30 },
                },
                cells: {
                    [cell1H.id]: cell1H,
                    [cell2H.id]: cell2H,
                    [cell1D.id]: cell1D,
                    [cell2D.id]: cell2D,
                },
                nodes: {},
            });
            const result = getTableOutlinedata(tableNode);
            expect(result).toHaveLength(2);
            expect(result.map((b) => b.bandType)).toEqual([
                BAND.COLUMN_HEADER,
                BAND.COLUMN_DATA,
            ]);
        });
    });

    describe("group bands", () => {
        function makeGroupTableNode(groupFields, cellOpts = {}) {
            const cells = {};
            groupFields.forEach((gf) => {
                ["col-1", "col-2"].forEach((colId) => {
                    const c = makeCell(colId, BAND.GROUP_HEADER, [], {
                        groupField: gf,
                        ...cellOpts,
                    });
                    cells[c.id] = c;
                });
            });
            return makeTableNode({
                bands: {
                    [BAND.GROUP_HEADER]: { height: 25, groupFields },
                },
                cells,
                nodes: {},
            });
        }

        test("returns one entry per groupField", () => {
            const tableNode = makeGroupTableNode(["fieldA", "fieldB"]);
            const result = getTableOutlinedata(tableNode);
            expect(result).toHaveLength(2);
        });

        test("group band entry key includes groupField", () => {
            const tableNode = makeGroupTableNode(["fieldA"]);
            const [entry] = getTableOutlinedata(tableNode);
            expect(entry.key).toBe(`${BAND.GROUP_HEADER}-fieldA`);
        });

        test("group band entry bandType includes groupField", () => {
            const tableNode = makeGroupTableNode(["fieldA"]);
            const [entry] = getTableOutlinedata(tableNode);
            expect(entry.bandType).toBe(`${BAND.GROUP_HEADER}-fieldA`);
        });

        test("group band entry selectable is false", () => {
            const tableNode = makeGroupTableNode(["fieldA"]);
            const [entry] = getTableOutlinedata(tableNode);
            expect(entry.selectable).toBe(false);
        });

        test("group band deleted is true when all cells for that groupField are deleted", () => {
            const tableNode = makeGroupTableNode(["fieldA"], { deleted: true });
            const [entry] = getTableOutlinedata(tableNode);
            expect(entry.deleted).toBe(true);
        });

        test("group band deleted is false when at least one cell is not deleted", () => {
            const c1 = makeCell("col-1", BAND.GROUP_HEADER, [], { groupField: "fieldA", deleted: true });
            const c2 = makeCell("col-2", BAND.GROUP_HEADER, [], { groupField: "fieldA", deleted: false });
            const tableNode = makeTableNode({
                bands: { [BAND.GROUP_HEADER]: { height: 25, groupFields: ["fieldA"] } },
                cells: { [c1.id]: c1, [c2.id]: c2 },
                nodes: {},
            });
            const [entry] = getTableOutlinedata(tableNode);
            expect(entry.deleted).toBe(false);
        });

        test("group band children uses column-level cells for that groupField", () => {
            const tableNode = makeGroupTableNode(["fieldA"]);
            const [entry] = getTableOutlinedata(tableNode);
            expect(entry.children).toHaveLength(2);
        });

        test("group band cell child cellId includes groupField", () => {
            const tableNode = makeGroupTableNode(["fieldA"]);
            const [entry] = getTableOutlinedata(tableNode);
            const cellChild = entry.children[0];
            expect(cellChild.cellId).toBe(makeCellId("col-1", BAND.GROUP_HEADER, "fieldA"));
        });

        test("group band is excluded when groupFields is empty", () => {
            const tableNode = makeTableNode({
                bands: {
                    [BAND.GROUP_HEADER]: { height: 25, groupFields: [] },
                    [BAND.COLUMN_DATA]: { height: 30 },
                },
                cells: {
                    [makeCell("col-1", BAND.COLUMN_DATA).id]: makeCell("col-1", BAND.COLUMN_DATA),
                    [makeCell("col-2", BAND.COLUMN_DATA).id]: makeCell("col-2", BAND.COLUMN_DATA),
                },
                nodes: {},
            });
            const result = getTableOutlinedata(tableNode);
            expect(result.map((b) => b.key)).not.toContain(BAND.GROUP_HEADER);
        });

        test("result is flat — group entries are not nested in an outer array", () => {
            const tableNode = makeGroupTableNode(["fieldA", "fieldB"]);
            const result = getTableOutlinedata(tableNode);
            result.forEach((item) => {
                expect(Array.isArray(item)).toBe(false);
            });
        });
    });

    describe("mixed regular and group bands", () => {
        test("returns correct total entries for mixed bands", () => {
            const c1D = makeCell("col-1", BAND.COLUMN_DATA, []);
            const c2D = makeCell("col-2", BAND.COLUMN_DATA, []);
            const c1G = makeCell("col-1", BAND.GROUP_HEADER, [], { groupField: "gf1" });
            const c2G = makeCell("col-2", BAND.GROUP_HEADER, [], { groupField: "gf1" });
            const tableNode = makeTableNode({
                bands: {
                    [BAND.GROUP_HEADER]: { height: 25, groupFields: ["gf1"] },
                    [BAND.COLUMN_DATA]: { height: 30 },
                },
                cells: {
                    [c1D.id]: c1D,
                    [c2D.id]: c2D,
                    [c1G.id]: c1G,
                    [c2G.id]: c2G,
                },
                nodes: {},
            });
            const result = getTableOutlinedata(tableNode);
            expect(result).toHaveLength(2);
        });
    });
});


describe("test table outlined utility", () => {
    describe("getHcrTableOutlineData fn", () => {
        it("should return table outlined data", () => {
            const result = getTableOutlinedata(tableOutlinedData.tableNode)
            expect(result).toHaveLength(5);
        })

    })
})



describe("test parseHCRNodesData", () => {
    const getConvertedAdvanceTableNodes = jest.fn();

    beforeEach(() => {
        jest.clearAllMocks();
    });

    const parseHCRNodesData = (nodes = []) => {
        if (!nodes.length) return [];
        let copiedNodes = cloneDeep(nodes);
        const isTableNodePresent = copiedNodes.find((node) => node.category === "table");
        if (!isTableNodePresent) return copiedNodes;

        const tableNodes = copiedNodes
            .filter((node) => ["table"].includes(node.category))
            .map((tNode) => {
                const id = tNode.id;
                let cells = copiedNodes.filter((node) => node.parent === id) || [];
                tNode.tableCells = cells;
                return tNode;
            });
        copiedNodes = copiedNodes.filter(
            (node) => !["table"].includes(node.category) && !node.isTableCell
        );
        copiedNodes = [...copiedNodes, ...(getConvertedAdvanceTableNodes(tableNodes) || [])];
        return copiedNodes;
    };

    function makeNode(id, category, overrides = {}) {
        return { id, category, ...overrides };
    }

    function makeTableCell(id, parentId, overrides = {}) {
        return { id, category: "cell", parent: parentId, isTableCell: true, ...overrides };
    }

    describe("early returns", () => {
        test("returns [] when called with no arguments", () => {
            expect(parseHCRNodesData()).toEqual([]);
        });

        test("returns [] when nodes is an empty array", () => {
            expect(parseHCRNodesData([])).toEqual([]);
        });

        test("returns cloned nodes when no table node is present", () => {
            const nodes = [makeNode("n1", "text"), makeNode("n2", "image")];
            const result = parseHCRNodesData(nodes);
            expect(result).toEqual(nodes);
        });

        test("does not return the original array reference when no table node is present", () => {
            const nodes = [makeNode("n1", "text")];
            const result = parseHCRNodesData(nodes);
            expect(result).not.toBe(nodes);
        });

    });

    describe("when table nodes are present", () => {
        test("removes table category nodes from the final result", () => {
            const tableNode = makeNode("t1", "table");
            const textNode = makeNode("n1", "text");
            getConvertedAdvanceTableNodes.mockReturnValue([]);

            const result = parseHCRNodesData([tableNode, textNode]);

            expect(result.find((n) => n.category === "table")).toBeUndefined();
        });

        test("removes isTableCell nodes from the final result", () => {
            const tableNode = makeNode("t1", "table");
            const cellNode = makeTableCell("c1", "t1");
            getConvertedAdvanceTableNodes.mockReturnValue([]);

            const result = parseHCRNodesData([tableNode, cellNode]);

            expect(result.find((n) => n.isTableCell)).toBeUndefined();
        });

        test("attaches matching child cells to tNode.tableCells", () => {
            const tableNode = makeNode("t1", "table");
            const cell1 = makeTableCell("c1", "t1");
            const cell2 = makeTableCell("c2", "t1");
            getConvertedAdvanceTableNodes.mockReturnValue([]);

            parseHCRNodesData([tableNode, cell1, cell2]);

            const [[tableNodes]] = getConvertedAdvanceTableNodes.mock.calls;
            expect(tableNodes[0].tableCells).toHaveLength(2);
            expect(tableNodes[0].tableCells.map((c) => c.id)).toEqual(["c1", "c2"]);
        });

        test("does not attach cells belonging to a different table", () => {
            const tableNode1 = makeNode("t1", "table");
            const tableNode2 = makeNode("t2", "table");
            const cell1 = makeTableCell("c1", "t1");
            const cell2 = makeTableCell("c2", "t2");
            getConvertedAdvanceTableNodes.mockReturnValue([]);

            parseHCRNodesData([tableNode1, tableNode2, cell1, cell2]);

            const [[tableNodes]] = getConvertedAdvanceTableNodes.mock.calls;
            const t1 = tableNodes.find((t) => t.id === "t1");
            const t2 = tableNodes.find((t) => t.id === "t2");
            expect(t1.tableCells.map((c) => c.id)).toEqual(["c1"]);
            expect(t2.tableCells.map((c) => c.id)).toEqual(["c2"]);
        });

        test("attaches empty tableCells array when table has no matching children", () => {
            const tableNode = makeNode("t1", "table");
            getConvertedAdvanceTableNodes.mockReturnValue([]);

            parseHCRNodesData([tableNode]);

            const [[tableNodes]] = getConvertedAdvanceTableNodes.mock.calls;
            expect(tableNodes[0].tableCells).toEqual([]);
        });

        test("calls getConvertedAdvanceTableNodes with all table nodes", () => {
            const t1 = makeNode("t1", "table");
            const t2 = makeNode("t2", "table");
            getConvertedAdvanceTableNodes.mockReturnValue([]);

            parseHCRNodesData([t1, t2]);

            const [[tableNodes]] = getConvertedAdvanceTableNodes.mock.calls;
            expect(tableNodes.map((t) => t.id)).toEqual(["t1", "t2"]);
        });

        test("spreads converted table nodes into the result", () => {
            const tableNode = makeNode("t1", "table");
            const converted = [makeNode("converted-1", "advancedTable")];
            getConvertedAdvanceTableNodes.mockReturnValue(converted);

            const result = parseHCRNodesData([tableNode]);

            expect(result).toContainEqual(converted[0]);
        });

        test("preserves non-table, non-cell nodes in the result", () => {
            const tableNode = makeNode("t1", "table");
            const textNode = makeNode("n1", "text");
            const imageNode = makeNode("n2", "image");
            getConvertedAdvanceTableNodes.mockReturnValue([]);

            const result = parseHCRNodesData([tableNode, textNode, imageNode]);

            expect(result.map((n) => n.id)).toContain("n1");
            expect(result.map((n) => n.id)).toContain("n2");
        });

        test("result contains non-table nodes followed by converted table nodes", () => {
            const tableNode = makeNode("t1", "table");
            const textNode = makeNode("n1", "text");
            const converted = [makeNode("converted-1", "advancedTable")];
            getConvertedAdvanceTableNodes.mockReturnValue(converted);

            const result = parseHCRNodesData([tableNode, textNode]);

            expect(result[0].id).toBe("n1");
            expect(result[1].id).toBe("converted-1");
        });

        test("handles getConvertedAdvanceTableNodes returning null gracefully", () => {
            const tableNode = makeNode("t1", "table");
            getConvertedAdvanceTableNodes.mockReturnValue(null);

            expect(() => parseHCRNodesData([tableNode])).not.toThrow();
            const result = parseHCRNodesData([tableNode]);
            expect(Array.isArray(result)).toBe(true);
        });

        test("handles getConvertedAdvanceTableNodes returning undefined gracefully", () => {
            const tableNode = makeNode("t1", "table");
            getConvertedAdvanceTableNodes.mockReturnValue(undefined);

            expect(() => parseHCRNodesData([tableNode])).not.toThrow();
            const result = parseHCRNodesData([tableNode]);
            expect(Array.isArray(result)).toBe(true);
        });

        test("handles multiple converted nodes from getConvertedAdvanceTableNodes", () => {
            const tableNode = makeNode("t1", "table");
            const converted = [
                makeNode("conv-1", "advancedTable"),
                makeNode("conv-2", "advancedTable"),
            ];
            getConvertedAdvanceTableNodes.mockReturnValue(converted);

            const result = parseHCRNodesData([tableNode]);

            expect(result.map((n) => n.id)).toEqual(["conv-1", "conv-2"]);
        });

        test("does not mutate the original nodes array", () => {
            const tableNode = makeNode("t1", "table");
            const textNode = makeNode("n1", "text");
            const original = [tableNode, textNode];
            getConvertedAdvanceTableNodes.mockReturnValue([]);

            parseHCRNodesData(original);

            expect(original).toHaveLength(2);
            expect(original[0]).toBe(tableNode);
        });
    });
});

describe("test getStylesOutline", () => {

    const mockTableData = { rows: 3, columns: 4, id: 'table-1' };

    const mockTableStyles = [
        { id: 'style-1', styleName: 'Bold Header' },
        { id: 'style-2', styleName: 'Striped Rows' },
    ];


    describe('getStylesOutline — return shape', () => {
        it('returns an array', () => {
            expect(Array.isArray(getStylesOutline(mockTableStyles, mockTableData))).toBe(true);
        });

        it('returns one item per style', () => {
            const result = getStylesOutline(mockTableStyles, mockTableData);
            expect(result).toHaveLength(mockTableStyles.length);
        });

        it('returns an empty array when tableStyles is empty', () => {
            expect(getStylesOutline([], mockTableData)).toEqual([]);
        });
    });


    describe('getStylesOutline — field mapping', () => {
        let result;

        beforeEach(() => {
            result = getStylesOutline(mockTableStyles, mockTableData);
        });

        it('maps styleName to title', () => {
            expect(result[0].title).toBe('Bold Header');
            expect(result[1].title).toBe('Striped Rows');
        });

        it('maps id to key', () => {
            expect(result[0].key).toBe('style-1');
            expect(result[1].key).toBe('style-2');
        });

        it('maps id to styleId', () => {
            expect(result[0].styleId).toBe('style-1');
            expect(result[1].styleId).toBe('style-2');
        });

        it('sets children to an empty array', () => {
            result.forEach((item) => {
                expect(item.children).toEqual([]);
            });
        });

        it('sets selectable to true', () => {
            result.forEach((item) => {
                expect(item.selectable).toBe(true);
            });
        });

        it('sets selectKey to "table-style-item"', () => {
            result.forEach((item) => {
                expect(item.selectKey).toBe('table-style-item');
            });
        });

        it('sets dsContextMenu to true', () => {
            result.forEach((item) => {
                expect(item.dsContextMenu).toBe(true);
            });
        });

        it('sets menuType to "table-style-item"', () => {
            result.forEach((item) => {
                expect(item.menuType).toBe('table-style-item');
            });
        });

        it('attaches the tableData reference to every item', () => {
            result.forEach((item) => {
                expect(item.tableData).toBe(mockTableData);
            });
        });
    });


    describe('getStylesOutline — full output snapshot', () => {
        it('produces the expected object for a single style', () => {
            const result = getStylesOutline(
                [{ id: 'style-1', styleName: 'Bold Header' }],
                mockTableData
            );
            expect(result).toEqual([
                {
                    title: 'Bold Header',
                    key: 'style-1',
                    children: [],
                    selectable: true,
                    selectKey: 'table-style-item',
                    dsContextMenu: true,
                    menuType: 'table-style-item',
                    styleId: 'style-1',
                    tableData: mockTableData,
                },
            ]);
        });

        it('matches the snapshot for multiple styles', () => {
            expect(getStylesOutline(mockTableStyles, mockTableData)).toMatchSnapshot();
        });
    });


    describe('getStylesOutline — tableData handling', () => {
        it('passes the same tableData reference to all items (not a copy)', () => {
            const result = getStylesOutline(mockTableStyles, mockTableData);
            result.forEach((item) => {
                expect(item.tableData).toBe(mockTableData); // referential equality
            });
        });

        it('works when tableData is null', () => {
            const result = getStylesOutline(mockTableStyles, null);
            result.forEach((item) => {
                expect(item.tableData).toBeNull();
            });
        });

        it('works when tableData is undefined', () => {
            const result = getStylesOutline(mockTableStyles, undefined);
            result.forEach((item) => {
                expect(item.tableData).toBeUndefined();
            });
        });

        it('works when tableData is an empty object', () => {
            const result = getStylesOutline(mockTableStyles, {});
            result.forEach((item) => {
                expect(item.tableData).toEqual({});
            });
        });
    });


    describe('getStylesOutline — input variations', () => {
        it('handles a single style entry', () => {
            const result = getStylesOutline([{ id: 's1', styleName: 'Solo' }], mockTableData);
            expect(result).toHaveLength(1);
            expect(result[0].title).toBe('Solo');
        });

        it('handles styles with numeric ids', () => {
            const result = getStylesOutline([{ id: 42, styleName: 'Numeric' }], mockTableData);
            expect(result[0].key).toBe(42);
            expect(result[0].styleId).toBe(42);
        });

        it('handles styles with empty string styleName', () => {
            const result = getStylesOutline([{ id: 'x', styleName: '' }], mockTableData);
            expect(result[0].title).toBe('');
        });

        it('does not mutate the original tableStyles array', () => {
            const styles = [{ id: 'style-1', styleName: 'Bold Header' }];
            const stylesCopy = [...styles];
            getStylesOutline(styles, mockTableData);
            expect(styles).toEqual(stylesCopy);
        });

        it('returns a new array (does not return the original)', () => {
            const result = getStylesOutline(mockTableStyles, mockTableData);
            expect(result).not.toBe(mockTableStyles);
        });
    });
});

describe("test getNewStyle fn", () => {

    jest.mock('uuid', () => ({
        v4: jest.fn(() => 'mocked-uuid-1234'),
    }));

    const TABLE_ID = 'table-abc';


    const makeStyles = (...names) => names.map((styleName, i) => ({ styleName, id: `id-${i}` }));


    describe('getNewStyle — styleName generation', () => {
        it('returns "Style 1" when prevStyles is empty', () => {
            const result = getNewStyle(TABLE_ID, []);
            expect(result.styleName).toBe('Style 1');
        });

        it('returns "Style 2" when "Style 1" already exists', () => {
            const result = getNewStyle(TABLE_ID, makeStyles('Style 1'));
            expect(result.styleName).toBe('Style 2');
        });

        it('returns "Style 3" when "Style 1" and "Style 2" already exist', () => {
            const result = getNewStyle(TABLE_ID, makeStyles('Style 1', 'Style 2'));
            expect(result.styleName).toBe('Style 3');
        });

        it('skips to the next available name when there are non-contiguous gaps', () => {
            const result = getNewStyle(TABLE_ID, makeStyles('Style 1', 'Style 3'));
            expect(result.styleName).toBe('Style 2');
        });

        it('increments correctly for a large number of existing styles', () => {
            const styles = Array.from({ length: 20 }, (_, i) => ({ styleName: `Style ${i + 1}` }));
            const result = getNewStyle(TABLE_ID, styles);
            expect(result.styleName).toBe('Style 21');
        });

        it('does not match partial names (e.g. "Style 10" does not block "Style 1")', () => {
            const result = getNewStyle(TABLE_ID, makeStyles('Style 10', 'Style 11'));
            expect(result.styleName).toBe('Style 1');
        });

        it('is case-sensitive — "style 1" does not block "Style 1"', () => {
            const result = getNewStyle(TABLE_ID, makeStyles('style 1', 'STYLE 1'));
            expect(result.styleName).toBe('Style 1');
        });
    });


    describe('getNewStyle — tableId', () => {
        it('assigns the provided tableId', () => {
            const result = getNewStyle('my-table-99', []);
            expect(result.tableId).toBe('my-table-99');
        });

        it('handles null tableId', () => {
            const result = getNewStyle(null, []);
            expect(result.tableId).toBeNull();
        });

        it('handles undefined tableId', () => {
            const result = getNewStyle(undefined, []);
            expect(result.tableId).toBeUndefined();
        });
    });



    describe('getNewStyle — static fields', () => {
        let result;
        beforeEach(() => { result = getNewStyle(TABLE_ID, []); });

        it('sets isChanged to false', () => {
            expect(result.isChanged).toBe(false);
        });

        it('sets mode to "Opaque"', () => {
            expect(result.mode).toBe('Opaque');
        });

        it('sets fontFill to "#000000"', () => {
            expect(result.fontFill).toBe('#000000');
        });

        it('sets fill to "#F0F8FF"', () => {
            expect(result.fill).toBe('#F0F8FF');
        });
    });


    describe('getNewStyle — bandsApplicable', () => {
        let result;
        beforeEach(() => { result = getNewStyle(TABLE_ID, []); });

        const expectedBands = [
            'tableHeader',
            'columnHeaderOfTable',
            'tableGroupHeaders',
            'columnData',
            'tableGroupFooters',
            'columnFooterOfTable',
            'tableFooter',
        ];

        it('contains exactly 7 band types', () => {
            expect(result.bandsApplicable).toHaveLength(7);
        });

        it('contains all expected band type values', () => {
            expect(result.bandsApplicable).toEqual(expectedBands);
        });

        it('preserves band order', () => {
            expectedBands.forEach((band, index) => {
                expect(result.bandsApplicable[index]).toBe(band);
            });
        });
    });


    describe('getNewStyle — borders', () => {
        let borders;
        beforeEach(() => { borders = getNewStyle(TABLE_ID, []).borders; });

        const sides = ['Top', 'Bottom', 'Right', 'Left'];

        it('has all four sides', () => {
            sides.forEach((side) => expect(borders).toHaveProperty(side));
        });

        it.each(sides)('%s border has stroke 1', (side) => {
            expect(borders[side].stroke).toBe(1);
        });

        it.each(sides)('%s border has style "SOLID"', (side) => {
            expect(borders[side].style).toBe('SOLID');
        });

        it.each(sides)('%s border has color "#000103"', (side) => {
            expect(borders[side].color).toBe('#000103');
        });
    });


    describe('getNewStyle — padding', () => {
        let padding;
        beforeEach(() => { padding = getNewStyle(TABLE_ID, []).padding; });

        const sides = ['Top', 'Bottom', 'Right', 'Left'];

        it('has all four sides', () => {
            sides.forEach((side) => expect(padding).toHaveProperty(side));
        });

        it.each(sides)('%s padding is 0', (side) => {
            expect(padding[side]).toBe(0);
        });
    });


    describe('getNewStyle — lineStyles', () => {
        let lineStyles;
        beforeEach(() => { lineStyles = getNewStyle(TABLE_ID, []).lineStyles; });

        it('has stroke 1', () => {
            expect(lineStyles.stroke).toBe(1);
        });

        it('has style "SOLID"', () => {
            expect(lineStyles.style).toBe('SOLID');
        });

        it('has color "#000000"', () => {
            expect(lineStyles.color).toBe('#000000');
        });
    });



    describe('getNewStyle — immutability', () => {
        it('does not mutate the prevStyles array', () => {
            const prevStyles = makeStyles('Style 1');
            const copy = [...prevStyles];
            getNewStyle(TABLE_ID, prevStyles);
            expect(prevStyles).toEqual(copy);
        });

        it('returns a new object on every call', () => {
            const first = getNewStyle(TABLE_ID, []);
            const second = getNewStyle(TABLE_ID, []);
            expect(first).not.toBe(second);
        });

        it('returns independent borders objects on each call', () => {
            const first = getNewStyle(TABLE_ID, []);
            const second = getNewStyle(TABLE_ID, []);
            expect(first.borders).not.toBe(second.borders);
        });
    });
})

describe("test updateTableStyles fn", () => {


    const makeStyle = (id, tableId, extra = {}) => ({ id, tableId, styleName: `Style-${id}`, ...extra });

    const makeNode = (category, cells = {}) => ({ category, cells });

    const makeActiveReport = (overrides = {}) => ({
        tableStyles: [
            makeStyle('s1', 'table-1'),
            makeStyle('s2', 'table-1'),
            makeStyle('s3', 'table-2'),
        ],
        hcrDiagramNodesData: [],
        ...overrides,
    });


    describe('updateTableStyles — addNewStyles', () => {
        it('appends newStyles to existing tableStyles', () => {
            const report = makeActiveReport();
            const newStyles = [makeStyle('s4', 'table-3'), makeStyle('s5', 'table-3')];
            updateTableStyles(report, 'addNewStyles', { newStyles });
            expect(report.tableStyles).toHaveLength(5);
            expect(report.tableStyles.at(-2)).toEqual(newStyles[0]);
            expect(report.tableStyles.at(-1)).toEqual(newStyles[1]);
        });

        it('preserves existing styles when appending', () => {
            const report = makeActiveReport();
            const original = [...report.tableStyles];
            updateTableStyles(report, 'addNewStyles', { newStyles: [makeStyle('s99', 'table-x')] });
            original.forEach((style) => {
                expect(report.tableStyles).toContainEqual(style);
            });
        });

        it('handles an empty newStyles array without error', () => {
            const report = makeActiveReport();
            updateTableStyles(report, 'addNewStyles', { newStyles: [] });
            expect(report.tableStyles).toHaveLength(3);
        });

        it('works when tableStyles is undefined on the report', () => {
            const report = { tableStyles: undefined, hcrDiagramNodesData: [] };
            updateTableStyles(report, 'addNewStyles', { newStyles: [makeStyle('s1', 't1')] });
            expect(report.tableStyles).toHaveLength(1);
        });

        it('defaults newStyles to [] when not provided in payload', () => {
            const report = makeActiveReport();
            updateTableStyles(report, 'addNewStyles', {});
            expect(report.tableStyles).toHaveLength(3);
        });
    });


    describe('updateTableStyles — updateStyle', () => {
        it('merges updatedStyles into the matching style', () => {
            const report = makeActiveReport();
            updateTableStyles(report, 'updateStyle', {
                styleId: 's1',
                updatedStyles: { styleName: 'Renamed', isChanged: true },
            });
            const updated = report.tableStyles.find((s) => s.id === 's1');
            expect(updated.styleName).toBe('Renamed');
            expect(updated.isChanged).toBe(true);
        });

        it('preserves other fields on the matched style', () => {
            const report = makeActiveReport();
            updateTableStyles(report, 'updateStyle', { styleId: 's1', updatedStyles: { isChanged: true } });
            const updated = report.tableStyles.find((s) => s.id === 's1');
            expect(updated.tableId).toBe('table-1');
        });

        it('does not modify non-matching styles', () => {
            const report = makeActiveReport();
            const s2Before = { ...report.tableStyles.find((s) => s.id === 's2') };
            updateTableStyles(report, 'updateStyle', { styleId: 's1', updatedStyles: { isChanged: true } });
            expect(report.tableStyles.find((s) => s.id === 's2')).toEqual(s2Before);
        });

        it('changes matching styles', () => {
            const report = makeActiveReport();
            const s2ShouldBefore = { ...report.tableStyles.find((s) => s.id === 's2'), isChanged: true };
            updateTableStyles(report, 'updateStyle', { styleId: 's2', updatedStyles: { isChanged: true } });
            expect(report.tableStyles.find((s) => s.id === 's2')).toEqual(s2ShouldBefore);
        });

        it('does nothing (no error) when styleId does not match any style', () => {
            const report = makeActiveReport();
            const before = [...report.tableStyles];
            updateTableStyles(report, 'updateStyle', { styleId: 'no-match', updatedStyles: { isChanged: true } });
            expect(report.tableStyles).toEqual(before);
        });

        it('preserves array length after update', () => {
            const report = makeActiveReport();
            updateTableStyles(report, 'updateStyle', { styleId: 's1', updatedStyles: {} });
            expect(report.tableStyles).toHaveLength(3);
        });

        it('works when tableStyles is undefined on the report', () => {
            const report = { tableStyles: undefined, hcrDiagramNodesData: [] };
            expect(() =>
                updateTableStyles(report, 'updateStyle', { styleId: 's1', updatedStyles: {} })
            ).not.toThrow();
        });

    });

    describe('updateTableStyles — deleteStyleByTableId', () => {
        it('removes all styles matching the given tableId', () => {
            const report = makeActiveReport();
            updateTableStyles(report, 'deleteStyleByTableId', { tableId: 'table-1' });
            expect(report.tableStyles.some((s) => s.tableId === 'table-1')).toBe(false);
        });

        it('keeps styles belonging to other tables', () => {
            const report = makeActiveReport();
            updateTableStyles(report, 'deleteStyleByTableId', { tableId: 'table-1' });
            expect(report.tableStyles).toContainEqual(makeStyle('s3', 'table-2'));
        });

        it('results in an empty array when all styles share the deleted tableId', () => {
            const report = { tableStyles: [makeStyle('s1', 't1'), makeStyle('s2', 't1')], hcrDiagramNodesData: [] };
            updateTableStyles(report, 'deleteStyleByTableId', { tableId: 't1' });
            expect(report.tableStyles).toHaveLength(0);
        });

        it('does nothing when no styles match the tableId', () => {
            const report = makeActiveReport();
            updateTableStyles(report, 'deleteStyleByTableId', { tableId: 'non-existent' });
            expect(report.tableStyles).toHaveLength(3);
        });

        it('works when tableStyles is undefined', () => {
            const report = { tableStyles: undefined, hcrDiagramNodesData: [] };
            expect(() =>
                updateTableStyles(report, 'deleteStyleByTableId', { tableId: 'table-1' })
            ).not.toThrow();
            expect(report.tableStyles).toHaveLength(0);
        });
    });


    describe('updateTableStyles — deleteStyleById', () => {
        it('removes the style with the matching id from tableStyles', () => {
            const report = makeActiveReport();
            updateTableStyles(report, 'deleteStyleById', { styleId: 's1' });
            expect(report.tableStyles.some((s) => s.id === 's1')).toBe(false);
            expect(report.tableStyles).toHaveLength(2);
        });

        it('keeps non-matching styles intact', () => {
            const report = makeActiveReport();
            updateTableStyles(report, 'deleteStyleById', { styleId: 's1' });
            expect(report.tableStyles.map((s) => s.id)).toEqual(['s2', 's3']);
        });

        it('does nothing when styleId does not match any style', () => {
            const report = makeActiveReport();
            updateTableStyles(report, 'deleteStyleById', { styleId: 'ghost' });
            expect(report.tableStyles).toHaveLength(3);
        });

        it('sets styleNameReference to null on matching cells', () => {
            const report = makeActiveReport({
                hcrDiagramNodesData: [
                    makeNode('advancedTable', {
                        cell1: { styleNameReference: 's1', value: 'A' },
                        cell2: { styleNameReference: 's2', value: 'B' },
                    }),
                ],
            });
            updateTableStyles(report, 'deleteStyleById', { styleId: 's1' });
            const node = report.hcrDiagramNodesData[0];
            expect(node.cells.cell1.styleNameReference).toBe("s1");
            expect(node.cells.cell2.styleNameReference).toBe('s2'); // untouched
        });

        it('does not touch cells where styleNameReference does not match', () => {
            const report = makeActiveReport({
                hcrDiagramNodesData: [
                    makeNode('advancedTable', {
                        cell1: { styleNameReference: 'other-style' },
                    }),
                ],
            });
            updateTableStyles(report, 'deleteStyleById', { styleId: 's1' });
            expect(report.hcrDiagramNodesData[0].cells.cell1.styleNameReference).toBe('other-style');
        });

        it('does not modify cells in non-advancedTable nodes', () => {
            const report = makeActiveReport({
                hcrDiagramNodesData: [
                    makeNode('basicTable', { cell1: { styleNameReference: 's1' } }),
                ],
            });
            updateTableStyles(report, 'deleteStyleById', { styleId: 's1' });
            expect(report.hcrDiagramNodesData[0].cells.cell1.styleNameReference).toBe('s1');
        });

        it('handles nodes with no cells without error', () => {
            const report = makeActiveReport({
                hcrDiagramNodesData: [makeNode('advancedTable', {})],
            });
            expect(() =>
                updateTableStyles(report, 'deleteStyleById', { styleId: 's1' })
            ).not.toThrow();
        });

        it('handles multiple advancedTable nodes', () => {
            const report = makeActiveReport({
                hcrDiagramNodesData: [
                    makeNode('advancedTable', { c1: { styleNameReference: 's1' } }),
                    makeNode('advancedTable', { c2: { styleNameReference: 's1' } }),
                ],
            });
            updateTableStyles(report, 'deleteStyleById', { styleId: 's1' });
            expect(report.hcrDiagramNodesData[0].cells.c1.styleNameReference).toBe("s1");
            expect(report.hcrDiagramNodesData[1].cells.c2.styleNameReference).toBe("s1");
        });

        it('style should not be null', () => {
            const report = makeActiveReport({
                hcrDiagramNodesData: [
                    makeNode('advancedTable', { c1: { styleNameReference: 'style' } }),
                    makeNode('advancedTable', { c2: { styleNameReference: 'style2' } }),
                ],
            });
            updateTableStyles(report, 'deleteStyleById', { styleId: 'style' });
            expect(report.hcrDiagramNodesData[0].cells.c1.styleNameReference).not.toBeNull();
            expect(report.hcrDiagramNodesData[1].cells.c2.styleNameReference).not.toBeNull();
        });
    });


    describe('updateTableStyles — default / unknown actionType', () => {
        it('does not modify tableStyles for an unknown action', () => {
            const report = makeActiveReport();
            const before = [...report.tableStyles];
            updateTableStyles(report, 'unknownAction', {});
            expect(report.tableStyles).toEqual(before);
        });

        it('does not throw for an unknown action', () => {
            expect(() => updateTableStyles(makeActiveReport(), 'BOGUS', {})).not.toThrow();
        });
    });


    describe('updateTableStyles — payload edge cases', () => {
        it('handles null payload gracefully', () => {
            const report = makeActiveReport();
            expect(() => updateTableStyles(report, 'addNewStyles', null)).not.toThrow();
        });

        it('handles undefined payload gracefully', () => {
            const report = makeActiveReport();
            expect(() => updateTableStyles(report, 'addNewStyles', undefined)).not.toThrow();
        });

        it('does not throw when actionType is null', () => {
            const report = makeActiveReport();
            expect(() => updateTableStyles(report, null, {})).not.toThrow();
        });
    });
});

describe("test getTableStyles fn", () => {

    jest.mock('uuid', () => ({
        v4: jest.fn(),
    }));


    const TABLE_ID = 'table-xyz';

    const setupUuids = (...ids) => {
        let call = 0;
    };


    describe('getTableStyles — return shape', () => {
        beforeEach(() => setupUuids('id-1', 'id-2', 'id-3'));

        it('returns an array', () => {
            expect(Array.isArray(getTableStyles(TABLE_ID, 0))).toBe(true);
        });

        it('always returns exactly 3 style objects', () => {
            expect(getTableStyles(TABLE_ID, 0)).toHaveLength(3);
            expect(getTableStyles(TABLE_ID, 5)).toHaveLength(3);
        });

        it('assigns a unique id to every style', () => {
            setupUuids('uid-a', 'uid-b', 'uid-c');
            const result = getTableStyles(TABLE_ID, 0);
            const ids = result.map((s) => s.id);
            expect(new Set(ids).size).toBe(3);
        });
    });


    describe('getTableStyles — styleName when tableCount is 0', () => {
        beforeEach(() => setupUuids('id-1', 'id-2', 'id-3'));

        it('first style has styleName "TABLE_TH"', () => {
            expect(getTableStyles(TABLE_ID, 0)[0].styleName).toBe('TABLE_TH');
        });

        it('second style has styleName "TABLE_CH"', () => {
            expect(getTableStyles(TABLE_ID, 0)[1].styleName).toBe('TABLE_CH');
        });

        it('third style has styleName "TABLE_TD"', () => {
            expect(getTableStyles(TABLE_ID, 0)[2].styleName).toBe('TABLE_TD');
        });
    });


    describe('getTableStyles — styleName when tableCount > 0', () => {
        beforeEach(() => setupUuids('id-1', 'id-2', 'id-3'));

        it('first style uses "TABLE {n}_TH" pattern', () => {
            expect(getTableStyles(TABLE_ID, 1)[0].styleName).toBe('TABLE 1_TH');
            expect(getTableStyles(TABLE_ID, 5)[0].styleName).toBe('TABLE 5_TH');
        });

        it('second style uses "TABLE {n}_CH" pattern', () => {
            expect(getTableStyles(TABLE_ID, 2)[1].styleName).toBe('TABLE 2_CH');
        });

        it('third style uses "TABLE {n}_TD" pattern', () => {
            expect(getTableStyles(TABLE_ID, 3)[2].styleName).toBe('TABLE 3_TD');
        });

        it('treats negative tableCount as falsy — uses base name', () => {
            // tableCount > 0 is the guard; negative values fall through to base names
            const result = getTableStyles(TABLE_ID, -1);
            expect(result[0].styleName).toBe('TABLE_TH');
            expect(result[1].styleName).toBe('TABLE_CH');
            expect(result[2].styleName).toBe('TABLE_TD');
        });
    });


    describe('getTableStyles — tableId', () => {
        beforeEach(() => setupUuids('id-1', 'id-2', 'id-3'));

        it('assigns the given tableId to all three styles', () => {
            const result = getTableStyles('my-table', 0);
            result.forEach((style) => expect(style.tableId).toBe('my-table'));
        });

        it('handles null tableId', () => {
            const result = getTableStyles(null, 0);
            result.forEach((style) => expect(style.tableId).toBeNull());
        });

        it('handles undefined tableId', () => {
            const result = getTableStyles(undefined, 0);
            result.forEach((style) => expect(style.tableId).toBeUndefined());
        });
    });


    describe('getTableStyles — static fields', () => {
        let result;
        beforeEach(() => {
            setupUuids('id-1', 'id-2', 'id-3');
            result = getTableStyles(TABLE_ID, 0);
        });

        it('sets isChanged to false on all styles', () => {
            result.forEach((style) => expect(style.isChanged).toBe(false));
        });

        it('sets mode to "Opaque" on all styles', () => {
            result.forEach((style) => expect(style.mode).toBe('Opaque'));
        });

        it('sets fontFill to "#000000" on all styles', () => {
            result.forEach((style) => expect(style.fontFill).toBe('#000000'));
        });

        it('sets isConditionalStyleReq to true for all styles', () => {
            expect(result[0].isConditionalStyleReq).toBe(true);
            expect(result[0].expression).toBe("");
            expect(result[0].expressionBackColor).toBe("#BFE1FF");
            expect(result[1].isConditionalStyleReq).toBe(true);
            expect(result[1].expression).toBe("");
            expect(result[1].expressionBackColor).toBe("#BFE1FF");
            expect(result[2].isConditionalStyleReq).toBe(true);
            expect(result[2].expression).toBe("");
            expect(result[2].expressionBackColor).toBe("#BFE1FF");
        });
    });


    describe('getTableStyles — fill colours', () => {
        let result;
        beforeEach(() => {
            setupUuids('id-1', 'id-2', 'id-3');
            result = getTableStyles(TABLE_ID, 0);
        });

        it('TH style has fill "#F0F8FF"', () => {
            expect(result[0].fill).toBe('#F0F8FF');
        });

        it('CH style has fill "#BFE1FF"', () => {
            expect(result[1].fill).toBe('#BFE1FF');
        });

        it('TD style has fill "#FFFFFF"', () => {
            expect(result[2].fill).toBe('#FFFFFF');
        });
    });


    describe('getTableStyles — bandsApplicable', () => {
        let result;
        beforeEach(() => {
            setupUuids('id-1', 'id-2', 'id-3');
            result = getTableStyles(TABLE_ID, 0);
        });

        it('TH style covers TABLE_HEADER and TABLE_FOOTER only', () => {
            expect(result[0].bandsApplicable).toEqual(['tableHeader', 'tableFooter']);
        });

        it('CH style covers COLUMN_HEADER, GROUP_HEADER, GROUP_FOOTER, COLUMN_FOOTER', () => {
            expect(result[1].bandsApplicable).toEqual([
                'columnHeaderOfTable',
                'tableGroupHeaders',
                'tableGroupFooters',
                'columnFooterOfTable',
            ]);
        });

        it('TD style covers COLUMN_DATA only', () => {
            expect(result[2].bandsApplicable).toEqual(['columnData']);
        });

        it('each bandsApplicable list contains no duplicates', () => {
            result.forEach((style) => {
                expect(new Set(style.bandsApplicable).size).toBe(style.bandsApplicable.length);
            });
        });

        it('no band type appears in more than one style', () => {
            const allBands = result.flatMap((s) => s.bandsApplicable);
            expect(new Set(allBands).size).toBe(allBands.length);
        });
    });


    describe('getTableStyles — borders', () => {
        const sides = ['Top', 'Bottom', 'Right', 'Left'];

        let result;
        beforeEach(() => {
            setupUuids('id-1', 'id-2', 'id-3');
            result = getTableStyles(TABLE_ID, 0);
        });

        it('every style has all four border sides', () => {
            result.forEach((style) => {
                sides.forEach((side) => expect(style.borders).toHaveProperty(side));
            });
        });

        it.each([0, 1, 2])('style[%i] borders all have stroke 1, style SOLID, color #000103', (idx) => {
            sides.forEach((side) => {
                expect(result[idx].borders[side]).toEqual({ stroke: 1, style: 'SOLID', color: '#000103' });
            });
        });
    });


    describe('getTableStyles — padding', () => {
        let result;
        beforeEach(() => {
            setupUuids('id-1', 'id-2', 'id-3');
            result = getTableStyles(TABLE_ID, 0);
        });

        it('TH style has all padding values set to 0', () => {
            expect(result[0].padding).toEqual({ Top: 0, Bottom: 0, Right: 0, Left: 0 });
        });

        it('CH style has all padding values set to 0', () => {
            expect(result[1].padding).toEqual({ Top: 0, Bottom: 0, Right: 0, Left: 0 });
        });

        it('TD style has all padding values set to 0', () => {
            expect(result[2].padding).toEqual({ Top: 0, Bottom: 0, Right: 0, Left: 0 });
        });
    });


    describe('getTableStyles — lineStyles', () => {
        let result;
        beforeEach(() => {
            setupUuids('id-1', 'id-2', 'id-3');
            result = getTableStyles(TABLE_ID, 0);
        });

        it('every style has lineStyles with stroke 1, style SOLID, color #000000', () => {
            result.forEach((style) => {
                expect(style.lineStyles).toEqual({ stroke: 1, style: 'SOLID', color: '#000000' });
            });
        });
    });


    describe('getTableStyles — immutability', () => {
        it('returns a new array on every call', () => {
            setupUuids('a1', 'a2', 'a3', 'b1', 'b2', 'b3');
            const first = getTableStyles(TABLE_ID, 0);
            const second = getTableStyles(TABLE_ID, 0);
            expect(first).not.toBe(second);
        });

        it('returns independent border objects across calls', () => {
            setupUuids('a1', 'a2', 'a3', 'b1', 'b2', 'b3');
            const first = getTableStyles(TABLE_ID, 0);
            const second = getTableStyles(TABLE_ID, 0);
            expect(first[0].borders).not.toBe(second[0].borders);
        });
    });
});

describe("test getTableStylesFromReportState fn", () => {

    const makeStyle = (id, tableId, bandsApplicable = []) => ({
        id,
        tableId,
        bandsApplicable,
    });

    const makeCell = (bandType, styleNameReference = undefined) => ({
        bandType,
        styleNameReference,
    });

    const makeAdvancedTable = (id, cells = {}) => ({
        id,
        category: 'advancedTable',
        cells,
    });

    const makeOtherNode = (id, category = 'text') => ({ id, category });

    const mockStylesForTable = (tableId, index) => [
        makeStyle(`${tableId}-th`, tableId, ['tableHeader', 'tableFooter']),
        makeStyle(`${tableId}-ch`, tableId, ['columnHeaderOfTable', 'tableGroupHeaders', 'tableGroupFooters', 'columnFooterOfTable']),
        makeStyle(`${tableId}-td`, tableId, ['columnData']),
    ];



    describe('getTableStylesFromReportState — early returns', () => {
        it('returns an empty array when nodes is empty', () => {
            expect(getTableStylesFromReportState([])).toEqual({
                tableStyles: [],
                alteredNodes: []
            });
        });

        it('returns { tableStyles: [], alteredNodes: nodes } when no advancedTable nodes exist', () => {
            const nodes = [makeOtherNode('n1'), makeOtherNode('n2', 'chart')];
            const result = getTableStylesFromReportState(nodes);
            expect(result).toEqual({ tableStyles: [], alteredNodes: nodes });
        });

        it('returns the original nodes reference in alteredNodes when no advancedTables exist', () => {
            const nodes = [makeOtherNode('n1')];
            const result = getTableStylesFromReportState(nodes);
            expect(result.alteredNodes).toBe(nodes);
        });

    });


    describe('getTableStylesFromReportState — tableStyles construction', () => {

        it('flattens styles from all tables into a single array', () => {
            const nodes = [makeAdvancedTable('t1'), makeAdvancedTable('t2')];
            const { tableStyles } = getTableStylesFromReportState(nodes);
            expect(tableStyles).toHaveLength(6);
        });

        it('includes styles for all tables in the returned tableStyles', () => {
            const nodes = [makeAdvancedTable('t1'), makeAdvancedTable('t2')];
            const { tableStyles } = getTableStylesFromReportState(nodes);
            expect(tableStyles.some((s) => s.tableId === 't1')).toBe(true);
            expect(tableStyles.some((s) => s.tableId === 't2')).toBe(true);
        });

        it('returns 3 styles for a single advancedTable', () => {
            const nodes = [makeAdvancedTable('t1')];
            const { tableStyles } = getTableStylesFromReportState(nodes);
            expect(tableStyles).toHaveLength(3);
        });
    });


    describe('getTableStylesFromReportState — alteredNodes non-advancedTable nodes', () => {
        it('includes all nodes in alteredNodes', () => {
            const nodes = [makeOtherNode('n1'), makeAdvancedTable('t1')];
            const { alteredNodes } = getTableStylesFromReportState(nodes);
            expect(alteredNodes).toHaveLength(2);
        });

        it('does not modify non-advancedTable nodes', () => {
            const other = makeOtherNode('n1');
            const nodes = [other, makeAdvancedTable('t1')];
            const { alteredNodes } = getTableStylesFromReportState(nodes);
            expect(alteredNodes.find((n) => n.id === 'n1')).toEqual(other);
        });
    });

    describe('getTableStylesFromReportState — styleNameReference assignment', () => {
        it('sets styleNameReference to null when bandType does not match any style', () => {
            const nodes = [
                makeAdvancedTable('t1', {
                    cell1: makeCell('unknownBandType'),
                }),
            ];
            const { alteredNodes } = getTableStylesFromReportState(nodes);
            expect(alteredNodes[0].cells.cell1.styleNameReference).toBeNull();
        });

        it('sets styleNameReference to null when cell bandType is undefined', () => {
            const nodes = [
                makeAdvancedTable('t1', {
                    cell1: makeCell(undefined),
                }),
            ];
            const { alteredNodes } = getTableStylesFromReportState(nodes);
            expect(alteredNodes[0].cells.cell1.styleNameReference).toBeNull();
        });
    });

    it('handles an advancedTable with no cells without error', () => {
        const nodes = [makeAdvancedTable('t1', {})];
        expect(() => getTableStylesFromReportState(nodes)).not.toThrow();
    });


    describe('getTableStylesFromReportState — mixed node types', () => {
        it('preserves node order in alteredNodes', () => {
            const nodes = [
                makeOtherNode('n1'),
                makeAdvancedTable('t1'),
                makeOtherNode('n2'),
            ];
            const { alteredNodes } = getTableStylesFromReportState(nodes);
            expect(alteredNodes.map((n) => n.id)).toEqual(['n1', 't1', 'n2']);
        });
    });


    describe('getTableStylesFromReportState — return structure', () => {
        it('returns an object with tableStyles and alteredNodes keys', () => {
            const nodes = [makeAdvancedTable('t1')];
            const result = getTableStylesFromReportState(nodes);
            expect(result).toHaveProperty('tableStyles');
            expect(result).toHaveProperty('alteredNodes');
        });

        it('tableStyles is an array', () => {
            const { tableStyles } = getTableStylesFromReportState([makeAdvancedTable('t1')]);
            expect(Array.isArray(tableStyles)).toBe(true);
        });

        it('alteredNodes is an array', () => {
            const { alteredNodes } = getTableStylesFromReportState([makeAdvancedTable('t1')]);
            expect(Array.isArray(alteredNodes)).toBe(true);
        });

        it('alteredNodes has the same length as the input nodes array', () => {
            const nodes = [makeOtherNode('n1'), makeAdvancedTable('t1'), makeOtherNode('n2')];
            const { alteredNodes } = getTableStylesFromReportState(nodes);
            expect(alteredNodes).toHaveLength(nodes.length);
        });
    });

});

describe('test isHCRDateVariable fn', () => {
    describe('isHCRDateVariable — returns true', () => {
        it('returns true for "new java.util.Date()"', () => {
            expect(isHCRDateVariable('new java.util.Date()')).toBe(true);
        });
    });

    describe('isHCRDateVariable — falsy label guard', () => {
        it('returns false for null', () => {
            expect(isHCRDateVariable(null)).toBe(false);
        });

        it('returns false for undefined', () => {
            expect(isHCRDateVariable(undefined)).toBe(false);
        });

        it('returns false for an empty string', () => {
            expect(isHCRDateVariable('')).toBe(false);
        });

        it('returns false for 0', () => {
            expect(isHCRDateVariable(0)).toBe(false);
        });

        it('returns false for false', () => {
            expect(isHCRDateVariable(false)).toBe(false);
        });
    });

    describe('isHCRDateVariable — returns false for non-matching values', () => {
        it('returns false for a random string', () => {
            expect(isHCRDateVariable('someVariable')).toBe(false);
        });

        it('returns false for partial match "new java.util.Date"', () => {
            expect(isHCRDateVariable('new java.util.Date')).toBe(false);
        });

        it('returns false for extra whitespace "new java.util.Date() "', () => {
            expect(isHCRDateVariable('new java.util.Date() ')).toBe(false);
        });

        it('returns false for uppercase variant "NEW java.util.Date()"', () => {
            expect(isHCRDateVariable('NEW java.util.Date()')).toBe(false);
        });

        it('returns false for similar but different class "new java.util.Calendar()"', () => {
            expect(isHCRDateVariable('new java.util.Calendar()')).toBe(false);
        });

        it('returns false for a number', () => {
            expect(isHCRDateVariable(123)).toBe(false);
        });

        it('returns false for an object', () => {
            expect(isHCRDateVariable({})).toBe(false);
        });

        it('returns false for an array', () => {
            expect(isHCRDateVariable([])).toBe(false);
        });
    })
})

describe('test getInitialParameter fn', () => {

    jest.mock('uuid', () => ({
        v4: jest.fn(() => 'mocked-uuid-1234'),
    }));

    describe('getInitialParameter — return shape', () => {
        it('returns an object', () => {
            expect(typeof getInitialParameter('param1')).toBe('object');
        });

        it('returns a new object on every call', () => {
            const first = getInitialParameter('p1');
            const second = getInitialParameter('p1');
            expect(first).not.toBe(second);
        });


    });


    describe('getInitialParameter — name', () => {
        it('assigns the provided name', () => {
            expect(getInitialParameter('myParam').name).toBe('myParam');
        });

        it('handles an empty string name', () => {
            expect(getInitialParameter('').name).toBe('');
        });

        it('handles null name', () => {
            expect(getInitialParameter(null).name).toBeNull();
        });

        it('handles undefined name', () => {
            expect(getInitialParameter(undefined).name).toBeUndefined();
        });

        it('handles numeric name', () => {
            expect(getInitialParameter(42).name).toBe(42);
        });
    });

    describe('getInitialParameter — static primitive fields', () => {
        let result;
        beforeEach(() => { result = getInitialParameter('test'); });

        it('sets config to empty string', () => {
            expect(result.config).toBe('');
        });

        it('sets connectionDetails to null', () => {
            expect(result.connectionDetails).toBeNull();
        });

        it('sets isNameEditable to false', () => {
            expect(result.isNameEditable).toBe(false);
        });

        it('sets isSaved to true', () => {
            expect(result.isSaved).toBe(true);
        });

        it('sets type to "java.lang.String"', () => {
            expect(result.type).toBe('java.lang.String');
        });

        it('sets mappingExpression to empty string', () => {
            expect(result.mappingExpression).toBe('');
        });

        it('sets isSubDSParameter to true', () => {
            expect(result.isSubDSParameter).toBe(true);
        });
    });


    describe('getInitialParameter — executeQueryData', () => {
        let executeQueryData;
        beforeEach(() => { executeQueryData = getInitialParameter('test').executeQueryData; });

        it('has a data property set to an empty array', () => {
            expect(executeQueryData.data).toEqual([]);
        });

        it('has a field property set to an empty array', () => {
            expect(executeQueryData.field).toEqual([]);
        });

        it('returns independent executeQueryData objects across calls', () => {
            const a = getInitialParameter('p1').executeQueryData;
            const b = getInitialParameter('p2').executeQueryData;
            expect(a).not.toBe(b);
        });
    });


    describe('getInitialParameter — parameterList', () => {
        it('sets parameterList to an empty array', () => {
            expect(getInitialParameter('test').parameterList).toEqual([]);
        });

        it('returns independent parameterList arrays across calls', () => {
            const a = getInitialParameter('p1').parameterList;
            const b = getInitialParameter('p2').parameterList;
            expect(a).not.toBe(b);
        });
    });


    describe('getInitialParameter — canvasValues', () => {
        let canvasValues;
        beforeEach(() => { canvasValues = getInitialParameter('test').canvasValues; });

        it('sets filterType to "Input"', () => {
            expect(canvasValues.filterType).toBe('Input');
        });

        it('sets multipleType to false', () => {
            expect(canvasValues.multipleType).toBe(false);
        });

        it('sets disabled to false', () => {
            expect(canvasValues.disabled).toBe(false);
        });

        it('sets isChecked to false', () => {
            expect(canvasValues.isChecked).toBe(false);
        });

        it('sets open to a single quote', () => {
            expect(canvasValues.open).toBe("'");
        });

        it('sets close to a single quote', () => {
            expect(canvasValues.close).toBe("'");
        });

        it('sets defaultValue to empty string', () => {
            expect(canvasValues.defaultValue).toBe('');
        });

        it('returns independent canvasValues objects across calls', () => {
            const a = getInitialParameter('p1').canvasValues;
            const b = getInitialParameter('p2').canvasValues;
            expect(a).not.toBe(b);
        });
    });

});

describe('getEmptyGroupCell — return shape', () => {

    describe('getEmptyGroupCell — return shape', () => {
        it('returns an object', () => {
            expect(typeof getEmptyGroupCell('cell1', 40, 'style-1')).toBe('object');
        });

        it('returns a new object on every call', () => {
            const first = getEmptyGroupCell('cell1', 40, 'style-1');
            const second = getEmptyGroupCell('cell1', 40, 'style-1');
            expect(first).not.toBe(second);
        });

        it('returns an object with exactly the expected keys', () => {
            const result = getEmptyGroupCell('cell1', 40, 'style-1');
            expect(Object.keys(result).sort()).toEqual(
                ['height', 'name', 'rowSpan', 'styleNameReference', 'textField'].sort()
            );
        });
    });

    describe('getEmptyGroupCell — name', () => {
        it('assigns the provided name', () => {
            expect(getEmptyGroupCell('myCell', 40, 'style-1').name).toBe('myCell');
        });

        it('handles an empty string name', () => {
            expect(getEmptyGroupCell('', 40, 'style-1').name).toBe('');
        });

        it('handles null name', () => {
            expect(getEmptyGroupCell(null, 40, 'style-1').name).toBeNull();
        });

        it('handles undefined name', () => {
            expect(getEmptyGroupCell(undefined, 40, 'style-1').name).toBeUndefined();
        });

        it('handles numeric name', () => {
            expect(getEmptyGroupCell(123, 40, 'style-1').name).toBe(123);
        });
    });

    describe('getEmptyGroupCell — height', () => {
        it('assigns the provided height', () => {
            expect(getEmptyGroupCell('cell1', 80, 'style-1').height).toBe(80);
        });

        it('handles height of 0', () => {
            expect(getEmptyGroupCell('cell1', 0, 'style-1').height).toBe(0);
        });

        it('handles negative height', () => {
            expect(getEmptyGroupCell('cell1', -10, 'style-1').height).toBe(-10);
        });

        it('handles null height', () => {
            expect(getEmptyGroupCell('cell1', null, 'style-1').height).toBeNull();
        });

        it('handles undefined height', () => {
            expect(getEmptyGroupCell('cell1', undefined, 'style-1').height).toBeUndefined();
        });

        it('handles string height', () => {
            expect(getEmptyGroupCell('cell1', '40px', 'style-1').height).toBe('40px');
        });
    });


    describe('getEmptyGroupCell — styleNameReference', () => {
        it('assigns the provided styleNameReference', () => {
            expect(getEmptyGroupCell('cell1', 40, 'style-abc').styleNameReference).toBe('style-abc');
        });

        it('handles null styleNameReference', () => {
            expect(getEmptyGroupCell('cell1', 40, null).styleNameReference).toBeNull();
        });

        it('handles undefined styleNameReference', () => {
            expect(getEmptyGroupCell('cell1', 40, undefined).styleNameReference).toBeUndefined();
        });

        it('handles empty string styleNameReference', () => {
            expect(getEmptyGroupCell('cell1', 40, '').styleNameReference).toBe('');
        });
    });


    describe('getEmptyGroupCell — static fields', () => {
        it('sets rowSpan to 1', () => {
            expect(getEmptyGroupCell('cell1', 40, 'style-1').rowSpan).toBe(1);
        });

        it('sets textField to an empty array', () => {
            expect(getEmptyGroupCell('cell1', 40, 'style-1').textField).toEqual([]);
        });
    });

})


describe('test getHCRParaTypeFormat function', () => {

    describe('getHCRParaTypeFormat — early returns', () => {
        it('returns null when value is null', () => {
            expect(getHCRParaTypeFormat(null)).toBeNull();
        });

        it('returns null when value is undefined', () => {
            expect(getHCRParaTypeFormat(undefined)).toBeNull();
        });

        it('returns null when value is an empty string', () => {
            expect(getHCRParaTypeFormat('')).toBeNull();
        });

        it('returns null when value is 0', () => {
            expect(getHCRParaTypeFormat(0)).toBeNull();
        });

        it('returns null when value is false', () => {
            expect(getHCRParaTypeFormat(false)).toBeNull();
        });

        it('returns null when no argument is passed', () => {
            expect(getHCRParaTypeFormat()).toBeNull();
        });
    });


    describe('getHCRParaTypeFormat — recognised parameter types', () => {
        it('returns YYYY-MM-DD format for "Date"', () => {
            expect(getHCRParaTypeFormat('Date')).toEqual({
                displayFormat: 'YYYY-MM-DD',
                valueFormat: 'YYYY-MM-DD',
            });
        });

        it('returns YYYY-MM-DD HH:mm:ss.S format for "Date And Time"', () => {
            expect(getHCRParaTypeFormat('Date and Time')).toEqual({
                displayFormat: 'YYYY-MM-DD HH:mm:ss.S',
                valueFormat: 'YYYY-MM-DD HH:mm:ss.S',
            });
        });

        it('returns YYYY-MM-DD format for "Date Range"', () => {
            expect(getHCRParaTypeFormat('Date Range')).toEqual({
                displayFormat: 'YYYY-MM-DD',
                valueFormat: 'YYYY-MM-DD',
            });
        });

        it('returns YYYY-MM-DD HH:mm:ss.S format for "Date And Time Range"', () => {
            expect(getHCRParaTypeFormat('Date and Time Range')).toEqual({
                displayFormat: 'YYYY-MM-DD HH:mm:ss.S',
                valueFormat: 'YYYY-MM-DD HH:mm:ss.S',
            });
        });
    });


    describe('getHCRParaTypeFormat — return shape', () => {
        it('returns an object with exactly displayFormat and valueFormat keys', () => {
            const result = getHCRParaTypeFormat('Date');
            expect(Object.keys(result).sort()).toEqual(['displayFormat', 'valueFormat']);
        });

        it('displayFormat and valueFormat are equal for every recognised type', () => {
            ['Date', 'Date and Time', 'Date Range', 'Date and Time Range'].forEach((type) => {
                const result = getHCRParaTypeFormat(type);
                expect(result.displayFormat).toBe(result.valueFormat);
            });
        });
    });


    describe('getHCRParaTypeFormat — unrecognised values', () => {
        it('returns null for a random string not in paraTypeFormats', () => {
            expect(getHCRParaTypeFormat('Some Random Type')).toBeNull();
        });

        it('returns null for a similar but not exact key (case mismatch)', () => {
            expect(getHCRParaTypeFormat('date')).toBeNull();
        });

        it('returns null for a value with extra whitespace', () => {
            expect(getHCRParaTypeFormat('Date ')).toBeNull();
        });

        it('returns null for "String" or other unrelated parameter types', () => {
            expect(getHCRParaTypeFormat('String')).toBeNull();
            expect(getHCRParaTypeFormat('Integer')).toBeNull();
        });

        it('returns null for a numeric value that happens to be truthy', () => {
            expect(getHCRParaTypeFormat(1)).toBeNull();
        });

        it('returns null for an object value', () => {
            expect(getHCRParaTypeFormat({})).toBeNull();
        });
    });
});