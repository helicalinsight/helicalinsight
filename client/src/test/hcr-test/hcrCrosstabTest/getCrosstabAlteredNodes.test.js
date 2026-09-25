import { getCrosstabAlteredNodes } from "../../../components/hi-canned-reports/hcrHelperMethods";
import { CROSSTAB_VERSION } from "../../../components/hi-canned-reports/hcrCanvas/advanceComponents/contants";

describe('getCrosstabAlteredNodes', () => {
    const baseCrosstabNode = (overrides = {}) => ({
        id: 'node-1',
        category: 'crosstabv2',
        version: 'c_v1.0',
        config: {
            measures: ['m1'],
            columnGroups: [],
            rowGroups: [],
            measureCells: [
                { name: 'Detail/Detail', styleNameReference: 'old-style', cellIndex: 99, id: 'cell-1' }
            ]
        },
        ...overrides
    });

    it('returns empty array when nodes is empty array', () => {
        expect(getCrosstabAlteredNodes({ nodes: [], tableStyles: [] })).toEqual([]);
    });

    it('returns empty array when called with empty object, throws when called with no arguments (no default object)', () => {
        expect(getCrosstabAlteredNodes({})).toEqual([]);
        expect(() => getCrosstabAlteredNodes()).toThrow();
    });

    it('returns empty array when nodes is undefined (default param)', () => {
        expect(getCrosstabAlteredNodes({ tableStyles: [] })).toEqual([]);
    });

    it('does not mutate original nodes array (cloneDeep)', () => {
        const original = [baseCrosstabNode()];
        const originalSnapshot = JSON.stringify(original);
        const result = getCrosstabAlteredNodes({ nodes: original, tableStyles: [] });
        expect(JSON.stringify(original)).toBe(originalSnapshot);
        expect(result).not.toBe(original);
        expect(result[0]).not.toBe(original[0]);
    });

    it('returns nodes unchanged when category is not crosstabv2', () => {
        const node = {
            id: 'node-2',
            category: 'advancedTable',
            version: 'c_v1.0',
            config: {
                measures: ['m1'],
                columnGroups: [],
                rowGroups: [],
                measureCells: [{ name: 'Detail/Detail', styleNameReference: 'old', cellIndex: 5 }]
            }
        };
        const tableStyles = [{ styleName: 'CT_Style', id: 'ct-id', crosstabId: 'node-2' }];
        const result = getCrosstabAlteredNodes({ nodes: [node], tableStyles });
        expect(result[0].category).toBe('advancedTable');
        expect(result[0].version).toBe('c_v1.0');
        expect(result[0].config.measureCells[0].styleNameReference).toBe('old');
        expect(result[0].config.measureCells[0].cellIndex).toBe(5);
    });

    it('returns nodes unchanged when version already equals CROSSTAB_VERSION', () => {
        const node = baseCrosstabNode({ version: CROSSTAB_VERSION });
        const tableStyles = [{ styleName: 'CT_Style', id: 'ct-id', crosstabId: 'node-1' }];
        const result = getCrosstabAlteredNodes({ nodes: [node], tableStyles });
        expect(result[0].version).toBe(CROSSTAB_VERSION);
        expect(result[0].config.measureCells[0].styleNameReference).toBe('old-style');
    });

    it('does not update and does not bump version when no matching styles found (empty filter result)', () => {
        const node = baseCrosstabNode();
        const tableStyles = [
            { styleName: 'CT_Style', id: 'ct-id', crosstabId: 'other-node' }
        ];
        const result = getCrosstabAlteredNodes({ nodes: [node], tableStyles });
        expect(result[0].version).toBe('c_v1.0');
        expect(result[0].config.measureCells[0].styleNameReference).toBe('old-style');
        expect(result[0].config.measureCells[0].cellIndex).toBe(99);
    });

    it('does not update when tableStyles is empty array', () => {
        const node = baseCrosstabNode();
        const result = getCrosstabAlteredNodes({ nodes: [node], tableStyles: [] });
        expect(result[0].version).toBe('c_v1.0');
        expect(result[0].config.measureCells[0].styleNameReference).toBe('old-style');
    });

    it('updates measureCells styleNameReference and cellIndex and bumps version when matching styles exist - single Detail cell', () => {
        const node = baseCrosstabNode();
        const tableStyles = [
            { styleName: 'CG_Style', id: 'cg-id', crosstabId: 'node-1' },
            { styleName: 'CT_Style', id: 'ct-id', crosstabId: 'node-1' },
            { styleName: 'CD_Style', id: 'cd-id', crosstabId: 'node-1' }
        ];
        const result = getCrosstabAlteredNodes({ nodes: [node], tableStyles });
        // Detail/Detail with no row/col groups is both last row and last col => CT wins
        expect(result[0].config.measureCells[0].styleNameReference).toBe('ct-id');
        expect(result[0].config.measureCells[0].cellIndex).toBe(0);
        expect(result[0].version).toBe(CROSSTAB_VERSION);
    });

    it('correctly handles multiple measureCells filtered by crosstabId - only matching id styles apply', () => {
        const node1 = {
            id: 'node-1',
            category: 'crosstabv2',
            version: 'old',
            config: {
                measures: ['m1'],
                columnGroups: [],
                rowGroups: [],
                measureCells: [{ name: 'Detail/Detail', styleNameReference: 'old', cellIndex: 5 }]
            }
        };
        const node2 = {
            id: 'node-2',
            category: 'crosstabv2',
            version: 'old',
            config: {
                measures: ['m1'],
                columnGroups: [],
                rowGroups: [],
                measureCells: [{ name: 'Detail/Detail', styleNameReference: 'old2', cellIndex: 5 }]
            }
        };
        const tableStyles = [
            { styleName: 'CT_Style', id: 'ct-for-node1', crosstabId: 'node-1' }
        ];
        const result = getCrosstabAlteredNodes({ nodes: [node1, node2], tableStyles });
        expect(result[0].config.measureCells[0].styleNameReference).toBe('ct-for-node1');
        expect(result[0].version).toBe(CROSSTAB_VERSION);
        expect(result[1].config.measureCells[0].styleNameReference).toBe('old2');
        expect(result[1].version).toBe('old');
    });

    it('preserves cells whose name is not found in updatedCells', () => {
        const node = {
            id: 'node-1',
            category: 'crosstabv2',
            version: 'old',
            config: {
                measures: ['m1'],
                columnGroups: [],
                rowGroups: [],
                measureCells: [
                    { name: 'Detail/Detail', styleNameReference: 'old', cellIndex: 5 },
                    { name: 'NonExistent/Detail', styleNameReference: 'should-stay', cellIndex: 5 }
                ]
            }
        };
        const tableStyles = [
            { styleName: 'CT_Style', id: 'ct-id', crosstabId: 'node-1' }
        ];
        const result = getCrosstabAlteredNodes({ nodes: [node], tableStyles });
        const updated = result[0].config.measureCells.find(c => c.name === 'Detail/Detail');
        const untouched = result[0].config.measureCells.find(c => c.name === 'NonExistent/Detail');
        expect(updated.styleNameReference).toBe('ct-id');
        expect(untouched.styleNameReference).toBe('should-stay');
        expect(untouched.cellIndex).toBe(5);
    });

    it('handles mixed categories - only crosstabv2 nodes are potentially updated', () => {
        const crosstabNode = baseCrosstabNode({ id: 'ct-node' });
        const tableNode = { id: 'table-1', category: 'advancedTable', version: 'old', config: {} };
        const textNode = { id: 'text-1', category: 'text', version: 'old' };
        const tableStyles = [{ styleName: 'CT_Style', id: 'ct-id', crosstabId: 'ct-node' }];
        const result = getCrosstabAlteredNodes({ nodes: [crosstabNode, tableNode, textNode], tableStyles });
        expect(result[0].version).toBe(CROSSTAB_VERSION);
        expect(result[1].category).toBe('advancedTable');
        expect(result[1].version).toBe('old');
        expect(result[2].category).toBe('text');
    });

    it('passes correct params to getCTMeasureCells - verifies CD vs CT logic with 2x2 grid', () => {
        const node = {
            id: 'node-1',
            category: 'crosstabv2',
            version: 'old',
            config: {
                measures: [],
                columnGroups: [{ name: 'colA' }],
                rowGroups: [{ name: 'rowX' }],
                measureCells: [
                    { name: 'Detail/Detail', styleNameReference: 'old', cellIndex: 99 },
                    { name: 'colA/Detail', styleNameReference: 'old', cellIndex: 99 },
                    { name: 'Detail/rowX', styleNameReference: 'old', cellIndex: 99 },
                    { name: 'colA/rowX', styleNameReference: 'old', cellIndex: 99 }
                ]
            }
        };
        const tableStyles = [
            { styleName: 'CG_Style', id: 'cg-id', crosstabId: 'node-1' },
            { styleName: 'CT_Style', id: 'ct-id', crosstabId: 'node-1' },
            { styleName: 'CD_Style', id: 'cd-id', crosstabId: 'node-1' }
        ];
        const result = getCrosstabAlteredNodes({ nodes: [node], tableStyles });
        const cellsByName = Object.fromEntries(result[0].config.measureCells.map(c => [c.name, c]));
        expect(cellsByName['Detail/Detail'].styleNameReference).toBe('cd-id');
        expect(cellsByName['Detail/rowX'].styleNameReference).toBe('ct-id');
        expect(cellsByName['colA/Detail'].styleNameReference).toBe('ct-id');
        expect(cellsByName['colA/rowX'].styleNameReference).toBe('ct-id');
        // cellIndex should be rowIndex (0 for Detail row, 1 for rowX)
        expect(cellsByName['Detail/Detail'].cellIndex).toBe(0);
        expect(cellsByName['Detail/rowX'].cellIndex).toBe(1);
    });

    it('handles node with missing config gracefully - creates empty updatedCells and still bumps version if styles exist', () => {
        const node = { id: 'node-1', category: 'crosstabv2', version: 'old' };
        const tableStyles = [{ styleName: 'CT_Style', id: 'ct-id', crosstabId: 'node-1' }];
        const nodeWithEmptyCells = {
            id: 'node-1',
            category: 'crosstabv2',
            version: 'old',
            config: {
                measures: [],
                columnGroups: [],
                rowGroups: [],
                measureCells: []
            }
        };
        const result = getCrosstabAlteredNodes({ nodes: [nodeWithEmptyCells], tableStyles });
        expect(result[0].version).toBe(CROSSTAB_VERSION);
        expect(result[0].config.measureCells).toEqual([]);
    });

    it('handles tableStyles with duplicate crosstabId entries correctly', () => {
        const node = baseCrosstabNode();
        const tableStyles = [
            { styleName: 'CG_Style', id: 'cg-id', crosstabId: 'node-1' },
            { styleName: 'CT_Style', id: 'ct-id', crosstabId: 'node-1' },
            { styleName: 'CG_Style', id: 'cg-id-2', crosstabId: 'node-1' }
        ];
        const result = getCrosstabAlteredNodes({ nodes: [node], tableStyles });
        expect(result[0].config.measureCells[0].styleNameReference).toBe('ct-id');
    });

    it('returns cloned nodes even when no update is needed - reference inequality check', () => {
        const node = baseCrosstabNode();
        const tableStyles = [];
        const result = getCrosstabAlteredNodes({ nodes: [node], tableStyles });
        expect(result[0]).not.toBe(node);
        expect(result[0].config).not.toBe(node.config);
    });

    it('handles large number of nodes efficiently', () => {
        const nodes = Array.from({ length: 10 }, (_, i) => ({
            id: `node-${i}`,
            category: i % 2 === 0 ? 'crosstabv2' : 'advancedTable',
            version: 'old',
            config: {
                measures: ['m1'],
                columnGroups: [],
                rowGroups: [],
                measureCells: [{ name: 'Detail/Detail', styleNameReference: 'old', cellIndex: 0 }]
            }
        }));
        const tableStyles = nodes
            .filter(n => n.category === 'crosstabv2')
            .map(n => ({ styleName: 'CT_Style', id: `ct-${n.id}`, crosstabId: n.id }));
        const result = getCrosstabAlteredNodes({ nodes, tableStyles });
        expect(result).toHaveLength(10);
        result.filter(n => n.category === 'crosstabv2').forEach(n => {
            expect(n.version).toBe(CROSSTAB_VERSION);
        });
        result.filter(n => n.category !== 'crosstabv2').forEach(n => {
            expect(n.version).toBe('old');
        });
    });

    it('preserves other config properties unchanged after update', () => {
        const node = {
            id: 'node-1',
            category: 'crosstabv2',
            version: 'old',
            config: {
                measures: ['m1'],
                columnGroups: [{ name: 'colA' }],
                rowGroups: [],
                measureCells: [{ name: 'Detail/Detail', styleNameReference: 'old', cellIndex: 0 }],
                extraProp: 'keep-me',
                nested: { a: 1 }
            }
        };
        const tableStyles = [{ styleName: 'CT_Style', id: 'ct-id', crosstabId: 'node-1' }];
        const result = getCrosstabAlteredNodes({ nodes: [node], tableStyles });
        expect(result[0].config.extraProp).toBe('keep-me');
        expect(result[0].config.nested).toEqual({ a: 1 });
        expect(result[0].config.measures).toEqual(['m1']);
    });
});
