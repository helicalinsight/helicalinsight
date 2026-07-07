import ReportQuery from '../index'
import 'core-js';
const getQuery = () => new ReportQuery({
    "location": "1639026125991",
    "metadataFileName": "59ced249-8d96-4175-aa88-75a17b200b5c.metadata",
})

/**
 * in test description 1 means valid and 0 means invalid
 */

/**
 * normal select query
 * .select('HIUSER.travel_details.destination', 'destination')
 */
describe("normal select query - .select('HIUSER.travel_details.destination', 'destination')", () => {
    // expect(true).toBeTruthy()
    test('empty arguments for select', () => {
        let query = getQuery()
        let result = query.select().reportFormData({
            returnData: true
        })
        expect((() => {
            let { columns } = result
            if (columns.length == 0) return true

        })()).toBeTruthy();

    })
    test('pass valid arguments', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let result = query.select(arg.column, arg.alias).reportFormData({
            returnData: true
        })
        expect((() => {
            let { columns } = result
            if (columns.length != 1) return false
            return columns[0].column == arg.column && columns[0].alias == arg.alias

        })()).toBeTruthy();

    })
    test('column and no alias', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let result = query.select(arg.column).reportFormData({
            returnData: true
        })
        expect((() => {
            let { columns } = result
            if (columns.length == 0) return true

        })()).toBeTruthy();

    })
    test('column and empty alias', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: ''
        }
        let result = query.select(arg.column, arg.alias).reportFormData({
            returnData: true
        })
        expect((() => {
            let { columns } = result
            if (columns.length == 0) return true

        })()).toBeTruthy();

    })
    test('no column only alias', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let result = query.select(arg.alias).reportFormData({
            returnData: true
        })
        expect((() => {
            let { columns } = result
            if (columns.length == 0) return true

        })()).toBeTruthy();

    })
    test('empy column, only alias', () => {
        let query = getQuery()
        let arg = {
            column: '',
            alias: 'destination'
        }
        let result = query.select(arg.alias).reportFormData({
            returnData: true
        })
        expect((() => {
            let { columns } = result
            if (columns.length == 0) return true

        })()).toBeTruthy();

    })
})


/**
 * select query with object - we can add muliplt columns with aliases at once
 * .select({alias1 : col1, alias2 : col2, ...})
 */

describe('selet with object as argument  .select({alias1 : col1, alias2 : col2, ...})', () => {
    test('pass valid arguments', () => {
        let query = getQuery()
        let arg = {
            'destination': 'HIUSER.travel_details.destination'
        }
        let result = query.select(arg).reportFormData({
            returnData: true
        })
        expect((() => {
            let { columns } = result
            if (columns.length != 1) return false
            return columns[0].column == arg.destination && columns[0].alias == 'destination'

        })()).toBeTruthy();

    })
    test('pass empty arguments', () => {
        let query = getQuery()
        let arg = {

        }
        let result = query.select(arg).reportFormData({
            returnData: true
        })
        expect((() => {
            let { columns } = result
            if (columns.length == 0) return true

        })()).toBeTruthy();

    })
    test('epmty alias and valid column', () => {
        let query = getQuery()
        let arg = {
            '': 'HIUSER.travel_details.destination'
        }
        let result = query.select(arg).reportFormData({
            returnData: true
        })
        expect((() => {
            let { columns } = result
            if (columns.length == 0) return true

        })()).toBeTruthy();

    })
    test('epmty column and valid alias', () => {
        let query = getQuery()
        let arg = {
            'destination': ''
        }
        let result = query.select(arg).reportFormData({
            returnData: true
        })
        expect((() => {
            let { columns } = result
            if (columns.length == 0) return true

        })()).toBeTruthy();

    })
    test('epmty column and empty alias', () => {
        let query = getQuery()
        let arg = {
            '': ''
        }
        let result = query.select(arg).reportFormData({
            returnData: true
        })
        expect((() => {
            let { columns } = result
            if (columns.length == 0) return true

        })()).toBeTruthy();

    })
    test('passing same aliases twice - invalid case', () => {
        let query = getQuery()
        let arg = {
            'aias': 'HIUSER.destination',
            'aias': 'HIUSER.source',
        }
        let result = query.select(arg).reportFormData({
            returnData: true
        })
        expect((() => {
            let { columns } = result
            if (columns.length == 1) return true

        })()).toBeTruthy();

    })
    test('epmty obhect to select', () => {
        let query = getQuery()
        let arg = {
        }
        let result = query.select(arg).reportFormData({
            returnData: true
        })
        expect((() => {
            let { columns } = result
            if (columns.length == 0) return true

        })()).toBeTruthy();

    })
    test('pass valid arguments fro two columns', () => {
        let query = getQuery()
        let arg = {
            'destination': 'HIUSER.travel_details.destination',
            'source': 'HIUSER.travel_details.source'
        }
        let result = query.select(arg).reportFormData({
            returnData: true
        })
        expect((() => {
            let { columns } = result
            if (columns.length != 2) return false
            return columns[0].column == arg.destination && columns[0].alias == 'destination' &&
                columns[1].column == arg.source && columns[1].alias == 'source'

        })()).toBeTruthy();

    })

})



/**
 * select query with builder - we can add one column at a time but can also apply dbfuncitons along with it
 * .select(e=>{e.abs('HIUSER.travel_details.destination')}, 'destination')
 */

describe(`selet with function as argument DBFUNCTION  .select(e=>{\'e.abs(HIUSER.travel_details.destination\')} 'destination')`, () => {
    test('pass valid arguments', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let cb = (builder) => builder.abs(arg.column)
        let result = query.select(cb, arg.alias).reportFormData({
            returnData: true
        })
        let { columns } = result
        //check for columns
        expect((() => {
            if (columns.length != 1) return false
            return columns[0].column == arg.column && columns[0].alias == arg.alias

        })()).toBeTruthy();
        // check for db function
        expect((() => {
            return columns[0]?.databaseFunction?.functionName?.includes('abs')
        })()).toBeTruthy()

    })
    test('not passing alias for select builder', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let cb = (builder) => builder.abs(arg.column)
        let result = query.select(cb).reportFormData({
            returnData: true
        })
        let { columns } = result
        //check for columns
        expect((() => {
            return (columns.length == 0)
            // return columns[0].column == arg.column && columns[0].alias == arg.alias

        })()).toBeTruthy();
    })
    test('passing NULL as alias for select builder', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: null
        }
        let cb = (builder) => builder.abs(arg.column)
        let result = query.select(cb).reportFormData({
            returnData: true
        })
        let { columns } = result
        //check for columns
        expect((() => {
            return (columns.length == 0)
            // return columns[0].column == arg.column && columns[0].alias == arg.alias

        })()).toBeTruthy();
    })
    test('selet with function as argument DBFUNCTION  .select(e=>{e.abs(e.sqrt(\'HIUSER.travel_details.destination\'))}, \'destination\')', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let cb = (builder) => builder.abs(builder.sqrt(arg.column))
        let result = query.select(cb, arg.alias).reportFormData({
            returnData: true
        })
        let { columns } = result
        //check for columns
        expect((() => {
            if (columns.length != 1) return false
            return columns[0].column == arg.column && columns[0].alias == arg.alias

        })()).toBeTruthy();
        // check for db function
        expect((() => {
            return columns[0]?.databaseFunction?.functionName?.includes('abs') &&
                columns[0]?.databaseFunction?.parameters.number.functionName.includes('sqrt')
        })()).toBeTruthy();
    })
    test('selet with function as argument AGGFUNCTION  .select(e=>{e.sum(e.count(\'HIUSER.travel_details.destination\'))}, \'destination\')', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let cb = (builder) => builder.sum(builder.count(arg.column))
        let result = query.select(cb, arg.alias).reportFormData({
            returnData: true
        })
        let { columns } = result
        //check for columns
        expect((() => {
            if (columns.length != 1) return false
            return columns[0].column == arg.column && columns[0].alias == arg.alias

        })()).toBeTruthy();
        // check for db function
        expect((() => {
            return columns[0].aggregate && columns[0].aggregateList[0].includes('sum') && columns[0].aggregateList[1].includes('count')
        })()).toBeTruthy();
    })
    test('selet with function as argument AGGFUNCTION, check for aggregate  .select(e=>{e.sum(e.count(\'HIUSER.travel_details.destination\'))}, \'destination\')', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let cb = (builder) => builder.sum(builder.count(arg.column))
        let result = query.select(cb, arg.alias).reportFormData({
            returnData: true
        })
        let { columns } = result
        //check for columns
        expect((() => {
            if (columns.length != 1) return false
            return columns[0].column == arg.column && columns[0].alias == arg.alias

        })()).toBeTruthy();
        // check for db function
        expect((() => {
            return columns[0].aggregate
        })()).toBeTruthy();
    })
    test('selet with function as argument AGGFUNCTION. check aggregate added to column  .select(e=>{e.sum(e.count(\'HIUSER.travel_details.destination\'))}, \'destination\')', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let cb = (builder) => builder.count(builder.sum(arg.column))
        let result = query.select(cb, arg.alias).reportFormData({
            returnData: true
        })
        let { columns } = result
        //check for columns
        expect((() => {
            if (columns.length != 1) return false
            return columns[0].column == arg.column && columns[0].alias == arg.alias
        })()).toBeTruthy();
        // check for db function
        expect((() => {
            return columns[0].aggregate && columns[0].aggregateList[0].includes('count') &&
                columns[0].aggregateList[1].includes('sum')
        })()).toBeTruthy();
    })
    test('selet with function as argument AGGFUNCTION. check aggregate added to functions propperty  .select(e=>{e.sum(e.count(\'HIUSER.travel_details.destination\'))}, \'destination\')', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let cb = (builder) => builder.count(builder.sum(arg.column))
        let result = query.select(cb, arg.alias).reportFormData({
            returnData: true
        })
        let { columns, functions } = result
        expect((() => {
            if (columns.length != 1) return false
            return functions.aggregate[0].function.includes('count')
        })()).toBeTruthy();
        expect((() => {
            if (columns.length != 1) return false
            return functions.aggregate[0].function.includes('sum')
        })()).toBeTruthy();
        // check for db function
        expect((() => {
            return functions.aggregate[0].function.indexOf('count') < functions.aggregate[0].function.indexOf('sum')
        })()).toBeTruthy();
    })
    test(`selet with function concat (multiple arguments). columns length  (builder) => builder.concat(builder.lower(arg.column), builder.upper('"test"'))`, () => {
        let query = getQuery()
        let arg = {
            column1: 'HIUSER.travel_details.destination',
            column2: 'HIUSER.travel_details.source',
            alias: 'destination'
        }
        let cb = (builder) => builder.concat(builder.upper(arg.column1), builder.lower(arg.column2))
        let result = query.select(cb, arg.alias).reportFormData({
            returnData: true
        })
        let { columns, functions } = result
        expect((() => {
            return columns.length == 1
        }))
    })
    test(`selet with function concat (multiple arguments). dbfunction  (builder) => builder.concat(builder.lower(arg.column), builder.upper('"test"'))`, () => {
        let query = getQuery()
        let arg = {
            column1: 'HIUSER.travel_details.destination',
            column2: 'HIUSER.travel_details.source',
            alias: 'destination'
        }
        let cb = (builder) => builder.concat(builder.upper(arg.column1), builder.lower(arg.column2))
        let result = query.select(cb, arg.alias).reportFormData({
            returnData: true
        })
        let { columns, functions } = result
        expect((() => {
            return columns[0].databaseFunction.functionName.includes('concat')
        }))
    })
    test(`selet with function concat (multiple arguments). check upper and lower  (builder) => builder.concat(builder.lower(arg.column), builder.upper('"test"'))`, () => {
        let query = getQuery()
        let arg = {
            column1: 'HIUSER.travel_details.destination',
            column2: 'HIUSER.travel_details.source',
            alias: 'destination'
        }
        let cb = (builder) => builder.concat(builder.upper(arg.column1), builder.lower(arg.column2))
        let result = query.select(cb, arg.alias).reportFormData({
            returnData: true
        })
        let { columns, functions } = result
        expect((() => {
            return columns[0].databaseFunction.parameters.string1.functionName.includes('upper') &&
                columns[0].databaseFunction.parameters.string2.functionName.includes('lower')
        }))
    })
    test(`selet with function concat (multiple arguments). check upper and lower columns  (builder) => builder.concat(builder.lower(arg.column), builder.upper('"test"'))`, () => {
        let query = getQuery()
        let arg = {
            column1: 'HIUSER.travel_details.destination',
            column2: 'HIUSER.travel_details.source',
            alias: 'destination'
        }
        let cb = (builder) => builder.concat(builder.upper(arg.column1), builder.lower(arg.column2))
        let result = query.select(cb, arg.alias).reportFormData({
            returnData: true
        })
        let { columns, functions } = result
        expect((() => {
            return columns[0].databaseFunction.parameters.string1.parameters.string == column1 &&
                columns[0].databaseFunction.parameters.string2.parameters.string == column2
        }))
    })

    test('pass first argument as object with extra argument as columnId and second as string', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination',
            id: "13601"
        }
        let result = query.select({ name: arg.column, id: arg.id }, arg.alias).reportFormData({
            returnData: true
        })
        expect((() => {
            let { columns } = result;
            if (columns.length != 1) return false
            return columns[0].column.name == arg.column && columns[0].alias == arg.alias && columns[0].column.id == arg.id

        })()).toBeTruthy();

    })

})
