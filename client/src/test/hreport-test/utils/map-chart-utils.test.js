import { calculateScaleValues, generateShapeRanges } from "../../../components/hi-reports/hi-viz-area/map-chart/osm-charts/utilities";


describe('test calculateScaleValues function', () => {
    test('should return an empty object when given an empty array', () => {
        const data = [];
        const sizeField = 'size';
        const result = calculateScaleValues(data, sizeField);
        expect(result).toEqual({});
    });

    test('should return an object with scaled values when given an array with values', () => {
        const data = [
            { size: 10 },
            { size: 20 },
            { size: 30 },
        ];
        const sizeField = 'size';
        const result = calculateScaleValues(data, sizeField);
        expect(result).toEqual({
            10: 4,
            20: 8,
            30: 12,
        });
    });
});