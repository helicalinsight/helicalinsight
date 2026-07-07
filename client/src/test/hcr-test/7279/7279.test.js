import { removeCalculation } from "../../../components/hi-canned-reports/hcrHelperMethods";

const input = {
    reqPane: {
        canvasProperties: {
            calculations: {
                options: [{id:1}, {id:2}, {id:3}],
                keyValuePairs: {id:2}
            }
        }
    },
    newId: 111
}

const output = {
    canvasProperties: {
        calculations: {
            options: [{id:1}, {id:3}],
            keyValuePairs: {id: 111},
            selectCalculation: ''
        }
    }
}

describe('Testing delete calculation', () => {
    test('test removeCalculation function' , (done) => {
        expect(removeCalculation(input)).toEqual(output);
        done();
    })
});
