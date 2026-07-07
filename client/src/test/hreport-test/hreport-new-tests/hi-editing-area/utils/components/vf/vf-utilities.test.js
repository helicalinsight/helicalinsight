import { checkAxesProperties } from "../../../../../../../components/hi-reports/hi-viz-area/custom-charts/utilities";

describe('to test checkAxesProperties', () => {
    it('should return default config when axisProperties is null', () => {
        const result = checkAxesProperties(null, 'x', 'xField');
        expect(result).toEqual({
            grid: null,
            rotate: 0,
            visible: true,
            rangeData: {},
        });
    });

    it('should return default config when axisProperties is an empty object', () => {
        const result = checkAxesProperties({}, 'x', 'xField');
        expect(result).toEqual({
            grid: null,
            rotate: 0,
            visible: true,
            rangeData: {},
        });
    });

    it('should set config.grid when axisProperties has gridLines including axis', () => {
        const result = checkAxesProperties({ gridLines: ['x'] }, 'x', 'xField');
        expect(result.grid).toEqual({
            line: {
                style: {
                    stroke: '#dddddd',
                    lineWidth: 1,
                },
            },
        });
    });

    it('should set config.rotate and config.visible when axisProperties has fields including axisField and rotate is 0', () => {
        const result = checkAxesProperties({ fields: [{ data: { name: 'xField', rotate: 0 } }] }, 'x', 'xField');
        expect(result.rotate).toEqual(0);
        expect(result.visible).toEqual(true);
    });

    it('should set config.rotate and config.verticalFactor when axisProperties has fields including axisField and rotate is greater than 0', () => {
        const result = checkAxesProperties({ fields: [{ data: { name: 'xField', rotate: 10 } }] }, 'x', 'xField');
        expect(result.rotate).toEqual(10);
        expect(result.verticalFactor).toEqual(-5);
    });

    it('should set config.visible when axisProperties has fields including axisField and hide is true', () => {
        const result = checkAxesProperties({ fields: [{ data: { name: 'xField', hide: true } }] }, 'x', 'xField');
        expect(result.visible).toEqual(false);
    });

    it('should set config.rangeData when axisProperties has fields including axisField and rangeData is available', () => {
        const result = checkAxesProperties({ fields: [{ data: { name: 'xField', minRange: '0', maxRange: '1000', dataType: 'numeric' } }] }, 'x', 'xField');
        expect(result.rangeData).toEqual({ min: 0, max: 1000, tickInterval: 0 });
    });
});