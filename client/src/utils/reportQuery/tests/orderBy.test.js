import ReportQuery from '../index'
import 'core-js';
import { groupBy } from 'lodash';
const getQuery = () => new ReportQuery({
    "location": "1639026125991",
    "metadataFileName": "59ced249-8d96-4175-aa88-75a17b200b5c.metadata",
})

/**
 * checing selectRAW
 * query.selectRaw('upper("HIUSER"."travel_details"."destination")', 'destination')
 */

describe('testing orderBy', () => {
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
    test('valid orderBy', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let result = query.select(arg.column, arg.alias)
            .orderBy(arg.alias)
            .reportFormData({
                returnData: true
            })
        let { columns, functions } = result
        expect((() => {
            return columns.length == 1 && 
            functions?.orderBy?.length == 1 
            // && 
            // functions.orderBy[0].alias == arg.alias
        })()).toBeTruthy()
    })
    test('valid orderBy - check if asc applied', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let result = query.select(arg.column, arg.alias)
            .orderBy(arg.alias)
            .reportFormData({
                returnData: true
            })
        let { columns, functions } = result
        expect((() => {
            return columns.length == 1 &&
                functions?.orderBy?.length == 1 &&
                functions.orderBy[0].alias == arg.alias &&
                functions.orderBy[0].order == 'asc'
        })()).toBeTruthy()
    })
    test('valid ordeerBy - order by on multiple columns', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let result = query.select(arg.column, arg.alias).select(arg2.column, arg2.alias)
            .orderBy(arg.alias, arg2.alias)
            .reportFormData({
                returnData: true
            })
        let { columns, functions } = result
        expect((() => {
            return columns.length == 2 &&
                functions?.orderBy?.length == 2 &&
                functions.orderBy[0].alias == arg.alias &&
                functions.orderBy[1].alias == arg2.alias

        })()).toBeTruthy()
    })
    test('valid ordeerBy - order by on multiple columns with multiple calls', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let result = query.select(arg.column, arg.alias).select(arg2.column, arg2.alias)
            .orderBy(arg.alias)
            .orderBy(arg2.alias)
            .reportFormData({
                returnData: true
            })
        let { columns, functions } = result
        expect((() => {
            return columns.length == 2 &&
                functions?.orderBy?.length == 2 &&
                functions.orderBy[0].alias == arg.alias &&
                functions.orderBy[1].alias == arg2.alias
        })()).toBeTruthy()
    })
    test('cheking the order of orderby applied', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let result = query.select(arg.column, arg.alias).select(arg2.column, arg2.alias)
            .orderBy(arg.alias)
            .orderBy(arg2.alias)
            .reportFormData({
                returnData: true
            })
        let { columns, functions } = result
        expect((() => {
            return columns.length == 2 &&
                functions?.orderBy?.length == 2 &&
                functions.orderBy[0].alias == arg.alias &&
                functions.orderBy[1].alias == arg2.alias
        })()).toBeTruthy()
    })
    test('cheking the order of orderby applied - checking if asc is applied', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let result = query.select(arg.column, arg.alias).select(arg2.column, arg2.alias)
            .orderBy(arg.alias)
            .orderBy(arg2.alias)
            .reportFormData({
                returnData: true
            })
        let { columns, functions } = result
        expect((() => {
            return columns.length == 2 &&
                functions?.orderBy?.length == 2 &&
                functions.orderBy[0].alias == arg.alias &&
                functions.orderBy[1].alias == arg2.alias &&
                functions.orderBy[0].order == 'asc' &&
                functions.orderBy[1].order == 'asc'
        })()).toBeTruthy()
    })
    test('applying desc for columns - in different calls', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let result = query.select(arg.column, arg.alias).select(arg2.column, arg2.alias)
            .orderBy({ [arg.alias]: 'desc' })
            .orderBy({ [arg2.alias]: 'desc' })
            .reportFormData({
                returnData: true
            })
        let { columns, functions } = result
        expect((() => {
            return columns.length == 2 &&
                functions?.orderBy?.length == 2 &&
                functions.orderBy[0].alias == arg.alias &&
                functions.orderBy[0].order == 'desc' &&
                functions.orderBy[1].alias == arg2.alias &&
                functions.orderBy[1].order == 'desc'
        })()).toBeTruthy()
    })
    test('applying desc for columns - manually stating asc order in different calls', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let result = query.select(arg.column, arg.alias).select(arg2.column, arg2.alias)
            .orderBy({ [arg.alias]: 'asc' })
            .orderBy({ [arg2.alias]: 'asc' })
            .reportFormData({
                returnData: true
            })
        let { columns, functions } = result
        expect((() => {
            return columns.length == 2 &&
                functions?.orderBy?.length == 2 &&
                functions.orderBy[0].alias == arg.alias &&
                functions.orderBy[0].order == 'asc' &&
                functions.orderBy[1].alias == arg2.alias &&
                functions.orderBy[1].order == 'asc'
        })()).toBeTruthy()
    })
    test('applying orderby for columns - manually stating asc in single call', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let result = query.select(arg.column, arg.alias).select(arg2.column, arg2.alias)
            .orderBy({ [arg.alias]: 'asc', [arg2.alias]: 'asc' })
            .reportFormData({
                returnData: true
            })
        let { columns, functions } = result
        expect((() => {
            columns.length == 1 &&
                functions?.orderBy?.length == 2 &&
                functions.orderBy[0].alias == arg.alias &&
                functions.orderBy[0].order == 'asc' &&
                functions.orderBy[1].alias == arg2.alias &&
                functions.orderBy[1].alias == 'asc'
        })())
    })
    test('applying invalid orderby for columns ', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let result = query.select(arg.column, arg.alias).select(arg2.column, arg2.alias)
            .orderBy({ [arg.alias]: 'other', [arg2.alias]: 'other' })

            .reportFormData({
                returnData: true
            })
        let { columns, functions } = result
        expect((() => {
            columns.length == 1 &&
                functions?.orderBy?.length == 2 &&
                functions.orderBy[10].alias == arg.alias &&
                functions.orderBy[0].order == 'asc' &&
                functions.orderBy[1].alias == arg2.alias &&
                functions.orderBy[1].alias == 'asc'
        })())
    })
})