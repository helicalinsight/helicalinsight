import ReportQuery from '../index'
import 'core-js';
const getQuery = () => new ReportQuery({
    "location": "1639026125991",
    "metadataFileName": "59ced249-8d96-4175-aa88-75a17b200b5c.metadata",
})

/**
 * checing selectRAW 
 * query.selectRaw('upper("HIUSER"."travel_details"."destination")', 'destination')
 */
describe(`selectraw query - query.selectRaw('upper("HIUSER"."travel_details"."destination")', 'destination')`, () => {
    test('passing only query - checking columns length', () => {
        let query = getQuery()
        let arg = {
            column: '"HIUSER"."travel_details"."destination"',
            alias: 'destination'
        }
        query.selectRaw(arg.column, arg.alias)
        let result = query.reportFormData({
            returnData: true
        })
        let { columns } = result

        expect((() => {
            return columns.length == 1
        })()).toBeTruthy()
    })
    test('passing only query - checking column', () => {
        let query = getQuery()
        let arg = {
            column: '"HIUSER"."travel_details"."destination"',
            alias: 'destination'
        }
        query.selectRaw(arg.column, arg.alias)
        let result = query.reportFormData({
            returnData: true
        })
        let { columns } = result

        expect((() => {
            return columns[0].column == arg.column
        })()).toBeTruthy()
    })
    test('passing only query - checking alias', () => {
        let query = getQuery()
        let arg = {
            column: '"HIUSER"."travel_details"."destination"',
            alias: 'destination'
        }
        query.selectRaw(arg.column, arg.alias)
        let result = query.reportFormData({
            returnData: true
        })
        let { columns } = result

        expect((() => {
            return columns[0].alias == arg.alias
        })()).toBeTruthy()
    })
    test('passing only query - checking custom=true', () => {
        let query = getQuery()
        let arg = {
            column: '"HIUSER"."travel_details"."destination"',
            alias: 'destination'
        }
        query.selectRaw(arg.column, arg.alias)
        let result = query.reportFormData({
            returnData: true
        })
        let { columns } = result

        expect((() => {
            return columns[0].custom
        })()).toBeTruthy()
    })
    test('passing empty alias', () => {
        let query = getQuery()
        let arg = {
            column: '"HIUSER"."travel_details"."destination"',
            alias: ''
        }
        query.selectRaw(arg.column, arg.alias)
        let result = query.reportFormData({
            returnData: true
        })
        let { columns } = result

        expect((() => {
            return columns.length == 0
        })()).toBeTruthy()
    })
    test('passing empty column', () => {
        let query = getQuery()
        let arg = {
            column: '',
            alias: 'destination'
        }
        query.selectRaw(arg.column, arg.alias)
        let result = query.reportFormData({
            returnData: true
        })
        let { columns } = result

        expect((() => {
            return columns.length == 0
        })()).toBeTruthy()
    })
    test('passing empty column and alais', () => {
        let query = getQuery()
        let arg = {
            column: '',
            alias: ''
        }
        query.selectRaw(arg.column, arg.alias)
        let result = query.reportFormData({
            returnData: true
        })
        let { columns } = result

        expect((() => {
            return columns.length == 0
        })()).toBeTruthy()
    })
    test('passing non-string datatype column and alias ', () => {
        let query = getQuery()
        let arg = {
            column: 5,
            alias: 5
        }
        query.selectRaw(arg.column, arg.alias)
        let result = query.reportFormData({
            returnData: true
        })
        let { columns } = result

        expect((() => {
            return columns.length == 0
        })()).toBeTruthy()
    })
    test('test dbfucntion with column - ceck columns length', () => {
        let query = getQuery()
        let arg = {
            column: 'upper("HIUSER"."travel_details"."destination")',
            alias: 'destination'
        }
        query.selectRaw(arg.column, arg.alias)
        let result = query.reportFormData({
            returnData: true
        })
        let { columns } = result
        expect((() => {
            return columns.length == 1 
        })()).toBeTruthy()
    })
    test('test dbfucntion with column - check custom', () => {
        let query = getQuery()
        let arg = {
            column: 'upper("HIUSER"."travel_details"."destination")',
            alias: 'destination'
        }
        query.selectRaw(arg.column, arg.alias)
        let result = query.reportFormData({
            returnData: true
        })
        let { columns } = result
        expect((() => {
            return columns.length == 1 && columns[0].custom
        })()).toBeTruthy()
    })
    test('test dbfucntion with column - check col name and alias name', () => {
        let query = getQuery()
        let arg = {
            column: 'upper("HIUSER"."travel_details"."destination")',
            alias: 'destination'
        }
        query.selectRaw(arg.column, arg.alias)
        let result = query.reportFormData({
            returnData: true
        })
        let { columns } = result
        expect((() => {
            return columns.length == 1 && columns[0].custom && columns[0].column == arg.column && columns[0].alias == arg.alias
        })()).toBeTruthy()
    })
    test('selectRaw twice - check col count', () => {
        let query = getQuery()
        let arg = {
            column1: 'upper("HIUSER"."travel_details"."destination")',
            alias1: 'destination',
            column2: 'upper("HIUSER"."travel_details"."source")',
            alias2: 'source',
        }
        query.selectRaw(arg.column1, arg.alias1).selectRaw(arg.column2, arg.alias2)
        let result = query.reportFormData({
            returnData: true
        })
        let { columns } = result
        expect((() => {
            return columns.length == 2
        })()).toBeTruthy()
    })
    test('selectRaw twice - check custom property', () => {
        let query = getQuery()
        let arg = {
            column1: 'upper("HIUSER"."travel_details"."destination")',
            alias1: 'destination',
            column2: 'upper("HIUSER"."travel_details"."source")',
            alias2: 'source',
        }
        query.selectRaw(arg.column1, arg.alias1).selectRaw(arg.column2, arg.alias2)
        let result = query.reportFormData({
            returnData: true
        })
        let { columns } = result
        expect((() => {
            return columns.length == 2 && columns[0].custom && columns[1].custom
        })()).toBeTruthy()
    })
    test('selectRaw twice - check columns and aliases', () => {
        let query = getQuery()
        let arg = {
            column1: 'upper("HIUSER"."travel_details"."destination")',
            alias1: 'destination',
            column2: 'upper("HIUSER"."travel_details"."source")',
            alias2: 'source',
        }
        query.selectRaw(arg.column1, arg.alias1).selectRaw(arg.column2, arg.alias2)
        let result = query.reportFormData({
            returnData: true
        })
        let { columns } = result
        expect((() => {
            return columns.length == 2 
            && columns[0].column == arg.column1 
            && columns[0].alias == arg.alias1 
            && columns[1].column == arg.column2 
            && columns[1].alias == arg.alias2
        })()).toBeTruthy()
    })
    test('selectRaw twice - pass same alias name twice', () => {
        let query = getQuery()
        let arg = {
            column1: 'upper("HIUSER"."travel_details"."destination")',
            alias1: 'destination',
            column2: 'upper("HIUSER"."travel_details"."source")',
            alias2: 'destination',
        }
        query.selectRaw(arg.column1, arg.alias1).selectRaw(arg.column2, arg.alias2)
        let result = query.reportFormData({
            returnData: true
        })
        let { columns } = result
        expect((() => {
            return columns.length == 1
                && columns[0].column == arg.column1
                && columns[0].alias == arg.alias1
        })()).toBeTruthy()
    })
    test('combinig selectRaw and select - check for col count', () => {
        let query = getQuery()
        query.selectRaw('"HIUSER"."travel_details"."destination"', 'destination')//- ** custom: true
            .select('HIUSER.travel_details.source', 'source')
        let result = query.reportFormData({
            returnData: true
        })
        let { columns } = result
        expect((() => {
            return columns.length == 2
        })()).toBeTruthy()
    })
    test('combinig selectRaw and select - check custom true for custom col', () => {
        let query = getQuery()
        query.selectRaw('"HIUSER"."travel_details"."destination"', 'destination')//- ** custom: true
            .select('HIUSER.travel_details.source', 'source')
        let result = query.reportFormData({
            returnData: true
        })
        let { columns } = result
        expect((() => {
            return columns.length == 2 &&
            columns[0].custom && !columns[1].custom
        })()).toBeTruthy()
    })
    test('combinig selectRaw and select - check col and aliases', () => {
        let query = getQuery()
        let arg = {
            column1: '"HIUSER"."travel_details"."destination"',
            alias1: 'destination',
            column2: 'HIUSER.travel_details.source',
            alias2: 'source'
        }
        query.selectRaw(arg.column1, arg.alias1)
            .select(arg.column2, arg.alias2)
        let result = query.reportFormData({
            returnData: true
        })
        let { columns } = result
        let [col1, col2] = columns
        expect((() => {
            return columns.length == 2 &&
                columns[0].custom && !columns[1].custom &&
                col1.column == arg.column1 &&
                col1.alias == arg.alias1 &&
                col2.column == arg.column2 &&
                col2.alias == arg.alias2
        })()).toBeTruthy()
    })
    test('combinig selectRaw and select - duplicating aliases', () => {
        let query = getQuery()
        let arg = {
            column1: '"HIUSER"."travel_details"."destination"',
            alias1: 'destination',
            column2: 'HIUSER.travel_details.source',
            alias2: 'destination'
        }
        query.selectRaw(arg.column1, arg.alias1)
            .select(arg.column2, arg.alias2)
        let result = query.reportFormData({
            returnData: true
        })
        let { columns } = result
        let [col1, col2] = columns
        expect((() => {
            return columns.length == 1 &&
                columns[0].custom &&
                col1.column == arg.column1 &&
                col1.alias == arg.alias1
        })()).toBeTruthy()
    })

})