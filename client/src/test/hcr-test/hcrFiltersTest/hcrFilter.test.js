import { getHcrParameterFilters, updateDateRangeFilterValues } from "../../../components/hi-canned-reports/hcrHelperMethods";
import { hcrFiltersTest } from "./hcrFilter-constant";


describe('Testing HCR Filters', () => {

    hcrFiltersTest.forEach(ele => {
        test(ele.title, (done) => {
            let filter = getHcrParameterFilters(ele.input);
            filter = filter.map(({ configId, ...rest }) => rest)
            expect(filter).toEqual(ele.output);
            done();
        })
    });
});


describe('test updateDateRangeFilterValues fn', () => {
    it('returns empty filters array when input is empty', () => {
        expect(updateDateRangeFilterValues()).toEqual([]);
    });

    it('returns original filters when no range filters', () => {
        const filters = [{ condition: 'EQUALS', label: 'test' }];
        expect(updateDateRangeFilterValues(filters)).toEqual(filters);
    });

    it('updates filter values when associated with range filter', () => {
        const filters = [
            { condition: 'IN_RANGE', range: ['start', 'end'], values: ['2022-01-01', '2022-01-31'] },
            { condition: 'EQUALS', label: 'start' },
        ];
        const expected = [
            { condition: 'IN_RANGE', range: ['start', 'end'], values: ['2022-01-01', '2022-01-31'] },
            { condition: 'EQUALS', label: 'start', values: ['2022-01-01'] },
        ];
        expect(updateDateRangeFilterValues(filters)).toEqual(expected);
    });

    it('updates multiple filter values when associated with multiple range filters', () => {
        const filters = [
            { condition: 'IN_RANGE', range: ['start', 'end'], values: ['2022-01-01', '2022-01-31'] },
            { condition: 'IN_RANGE', range: ['start2', 'end2'], values: ['2022-02-01', '2022-02-28'] },
            { condition: 'EQUALS', label: 'start' },
            { condition: 'EQUALS', label: 'start2' },
        ];
        const expected = [
            { condition: 'IN_RANGE', range: ['start', 'end'], values: ['2022-01-01', '2022-01-31'] },
            { condition: 'IN_RANGE', range: ['start2', 'end2'], values: ['2022-02-01', '2022-02-28'] },
            { condition: 'EQUALS', label: 'start', values: ['2022-01-01'] },
            { condition: 'EQUALS', label: 'start2', values: ['2022-02-01'] },
        ];
        expect(updateDateRangeFilterValues(filters)).toEqual(expected);
    });

    it('does not update filter values when not associated with any range filter', () => {
        const filters = [
            { condition: 'IN_RANGE', range: ['start', 'end'], values: ['2022-01-01', '2022-01-31'] },
            { condition: 'EQUALS', label: 'test' },
        ];
        expect(updateDateRangeFilterValues(filters)).toEqual(filters);
    });

    it('does not update range filter values when no associated filters', () => {
        const filters = [
            { condition: 'IN_RANGE', range: ['start', 'end'], values: ['2022-01-01', '2022-01-31'] },
        ];
        expect(updateDateRangeFilterValues(filters)).toEqual(filters);
    });
});