import { calculateScrollWidth, getTableTotal } from "../../../components/hi-reports/hi-viz-area/table/table-utils";

describe('getTableTotal', () => {
    it('should return the sum of a column', () => {
        const data = [
            { name: 'John', age: 30 },
            { name: 'Jane', age: 25 },
            { name: 'Bob', age: 40 }
        ];
        const column = { name: 'age' };
        const result = getTableTotal(data, column);
        expect(result).toEqual(95);
    });

    it('should return the sum of a column', () => {
        const data = [
            { name: 'John', age: 30 },
            { name: 'Jane', age: 25 },
            { name: 'Bob', age: 40 }
        ];
        const column = { name: 'age' };
        const result = getTableTotal(data, column, 'count');
        expect(result).toEqual(95);
    });

    it('should return the average of a column', () => {
        const data = [
            { name: 'John', age: 30 },
            { name: 'Jane', age: 25 },
            { name: 'Bob', age: 40 }
        ];
        const column = { name: 'age' };
        const result = getTableTotal(data, column, 'avg');
        expect(result).toEqual(31.666666666666668);
    });

    it('should return the minimum value of a column', () => {
        const data = [
            { name: 'John', age: 30 },
            { name: 'Jane', age: 25 },
            { name: 'Bob', age: 40 }
        ];
        const column = { name: 'age' };
        const result = getTableTotal(data, column, 'min');
        expect(result).toEqual(25);
    });

    it('should return the maximum value of a column', () => {
        const data = [
            { name: 'John', age: 30 },
            { name: 'Jane', age: 25 },
            { name: 'Bob', age: 40 }
        ];
        const column = { name: 'age' };
        const result = getTableTotal(data, column, 'max');
        expect(result).toEqual(40);
    });

    it('should return the default result if an invalid aggregate is provided', () => {
        const data = [
            { name: 'John', age: 30 },
            { name: 'Jane', age: 25 },
            { name: 'Bob', age: 40 }
        ];
        const column = { name: 'age' };
        const result = getTableTotal(data, column, 'invalid');
        expect(result).toEqual(95);
    });
});

const mockMeasureTextWidth = (text) => text.length * 10;

describe('test calculateScrollWidth function', () => {
    it('should return 0 for empty columns array', () => {
        const columns = [];
        expect(calculateScrollWidth(columns)).toBe(0);
    });

    it('should calculate scroll width for single column', () => {
        const columns = [{ name: 'Column 1' }];
        const result = calculateScrollWidth(columns, mockMeasureTextWidth);
        expect(result).toBe(240);
    });

    it('should calculate scroll width for multiple columns', () => {
        const columns = [{ name: 'Column 1' },{ name: 'Column 2' }];
        const result = calculateScrollWidth(columns, mockMeasureTextWidth);
        expect(result).toBe(480);
    });
});