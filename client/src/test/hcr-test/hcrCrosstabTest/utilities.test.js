import { getHCRCrosstabCols, getNestedArr, getNextItemsLength } from "../../../components/hi-canned-reports/hcrCanvas/hcrCanvasPaneHelperMethods";
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

describe("test getHCRCrosstabCols Fn", () => {
    test('getHCRCrosstabCols returns correct columns when there are no children', () => {
        const columnHeaders = [
            { items: [{ value: 'A', isTotal: false }, { value: 'B', isTotal: false }] },
            { items: [{ value: 'C', isTotal: false }, { value: 'D', isTotal: false }] }
        ];
        const expected = [
            { value: 'B', isTotal: false },
            { value: 'A', isTotal: false },
            { value: 'D', isTotal: false },
            { value: 'C', isTotal: false }
        ];
        expect(getHCRCrosstabCols(columnHeaders)).toEqual(expected);
    });

    test('getHCRCrosstabCols returns correct columns when there are children', () => {
        const columnHeaders = [
            {
                items: [{ value: 'A', isTotal: false }, { value: 'B', isTotal: false }],
                children: [
                    { items: [{ value: 'C', isTotal: true }, { value: 'D', isTotal: false }] }
                ]
            }
        ];
        const expected = [
            { value: 'D', isTotal: false },
            { value: 'C', isTotal: true },
        ];
        expect(getHCRCrosstabCols(columnHeaders)).toEqual(expected);
    });

    test('getHCRCrosstabCols handles empty columnHeaders', () => {
        const columnHeaders = [];
        const expected = [];
        expect(getHCRCrosstabCols(columnHeaders)).toEqual(expected);
    });
})


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