import ReportQuery from '../index'
import 'core-js';
import { config } from './advfilters.mock.data'
let arg = {
    column: 'HIUSER.travel_details.destination',
    alias: 'destination'
}
let arg2 = {
    column: 'HIUSER.travel_details.source',
    alias: 'source'
}
const getQuery = () => (new ReportQuery({
    "location": "1639026125991",
    "metadataFileName": "59ced249-8d96-4175-aa88-75a17b200b5c.metadata",
}))


describe('testing where with config property - advanced filters', () => {
    beforeAll(() => {
        jest.spyOn(console, 'log').mockImplementation(() => { });
        jest.spyOn(console, 'error').mockImplementation(() => { });
        jest.spyOn(console, 'warn').mockImplementation(() => { });
        jest.spyOn(console, 'info').mockImplementation(() => { });
        jest.spyOn(console, 'debug').mockImplementation(() => { });
    });
    afterAll(() => {
        global.gc && global.gc()
      })
    test('passing where condition with config - advanced filter - valid case', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.employee_details.employee_name',
            alias: 'employee_name'
        }
        let result = query.select(arg.column, arg.alias)
            .where('HIUSER.employee_details.employee_name', 'IN', [
                "ahmed haider"
            ], config)
            .reportFormData({
                returnData: true
            })
        expect((() => {
            return result.filters.length === 1 &&
                result.filters[0].databaseFunction &&
                result.filters[0].databaseFunction.functionName.includes('lower')
        })()).toBeTruthy()
    })

    test('passing where condition with config - advanced filter - invalid case', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.employee_details.employee_name',
            alias: 'employee_name'
        }
        delete config.mapping
        let result = query.select(arg.column, arg.alias)
            .where('HIUSER.employee_details.employee_name', 'IN', [
                "ahmed haider"
            ], config)
            .reportFormData({
                returnData: true
            })
        expect((() => {
            return result.filters.length === 1 &&
                !result.filters[0].databaseFunction
        })()).toBeTruthy()
    })

})