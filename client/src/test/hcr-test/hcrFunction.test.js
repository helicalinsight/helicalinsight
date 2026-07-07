import { addBand } from "../../components/hi-canned-reports/hcrHelperMethods";

const band = {
    bandHeight: 0,
    break: [],
    isImageAttached: false,
    staticText: [],
    textField: [],
    image: [],
    lines: [],
    chart: [],
    crosstab: [],
    table: []
};

describe('Testing addBand functionality', () => {
    test('add band test case', (done) => {
        expect(addBand()).toEqual(band);
        done();
    })
});