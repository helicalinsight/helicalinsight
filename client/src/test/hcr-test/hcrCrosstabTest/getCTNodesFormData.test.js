import { getCTNodesFormData } from "../../../components/hi-canned-reports/hcrHelperMethods";

const getDispatch = (layout = {}) =>
    jest.fn((callback) =>
        callback(undefined, () => ({
            cannedReports: {
                present: {
                    hCROldConfigurations: {
                        HCR: {
                            HCR: {
                                designerProperties: {
                                    layout
                                }
                            }
                        }
                    }
                }
            }
        }))
    );

const getGroup = (name, totalPosition) => ({
    name,
    totalPosition,
    cells: [
        { nodeIds: [], width: 50, height: 25 },
        { nodeIds: [], width: 50, height: 25 }
    ]
});

describe('getCTNodesFormData total positions', () => {
    it('normalizes every column and row group totalPosition before sending the preview payload', () => {
        const dispatch = getDispatch();
        const config = {
            nodes: {},
            columnGroups: [
                getGroup('colA', 'START'),
                getGroup('colB', 'start'),
                getGroup('colC', 'NONE'),
                getGroup('colD', 'none'),
                getGroup('colE', 'middle'),
                getGroup('colF', undefined)
            ],
            rowGroups: [
                getGroup('rowA', 'End'),
                getGroup('rowB', 'END'),
                getGroup('rowC', 'NONE'),
                getGroup('rowD')
            ],
            measureCells: [],
            measures: []
        };

        const result = getCTNodesFormData({ config, styles: [] }, dispatch);

        expect(result.columnGroups.map((group) => group.totalPosition)).toEqual([
            'START',
            'START',
            'NONE',
            'NONE',
            'END',
            'END'
        ]);
        expect(result.rowGroups.map((group) => group.totalPosition)).toEqual([
            'END',
            'END',
            'NONE',
            'END'
        ]);
        expect(dispatch).toHaveBeenCalledTimes(1);
    });

    it('keeps the header and total cell sizes while normalizing positions', () => {
        const dispatch = getDispatch();
        const config = {
            nodes: {},
            columnGroups: [getGroup('colA', 'START')],
            rowGroups: [getGroup('rowA', 'NONE')],
            measureCells: [],
            measures: []
        };

        const result = getCTNodesFormData({ config, styles: [] }, dispatch);

        expect(result.columnGroups[0].height).toBe(25);
        expect(result.columnGroups[0].crosstabColumnHeader).toMatchObject({
            width: 50,
            height: 25,
            textField: []
        });
        expect(result.columnGroups[0].crosstabTotalColumnHeader).toMatchObject({
            width: 50,
            height: 25,
            textField: []
        });
        expect(result.rowGroups[0].width).toBe(50);
        expect(result.rowGroups[0].crosstabRowHeader).toMatchObject({
            width: 50,
            height: 25,
            textField: []
        });
        expect(result.rowGroups[0].crosstabTotalRowHeader).toMatchObject({
            width: 50,
            height: 25,
            textField: []
        });
    });

    it('defaults legacy groups without a totalPosition to END', () => {
        const dispatch = getDispatch();
        const config = {
            nodes: {},
            columnGroups: [getGroup('colA')],
            rowGroups: [getGroup('rowA')],
            measureCells: [],
            measures: []
        };

        const result = getCTNodesFormData({ config, styles: [] }, dispatch);

        expect(result.columnGroups[0].totalPosition).toBe('END');
        expect(result.rowGroups[0].totalPosition).toBe('END');
    });
});
