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

describe('testing groupBy', () => {
    test('valid groupBy', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let result = query.select(arg.column, arg.alias)
            .groupBy(arg.alias)
            .reportFormData({
                returnData: true
            })
        let { columns, functions } = result
        expect((() => {
            return columns.length == 1 && functions?.groupBy?.length == 1 && functions.groupBy[0].column == arg.alias
        })()).toBeTruthy()
    })
    test('passing invalid alias for groupby', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let result = query.select(arg.column, arg.alias)
            .groupBy('invalid')
            .reportFormData({
                returnData: true
            })
        let { columns, functions } = result
        expect((() => {
            return columns.length == 1 && !functions?.groupBy
        })()).toBeTruthy()
    })
    test('grouping on multiple columns at once', () => {
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
            .groupBy(arg.alias, arg2.alias)
            .reportFormData({
                returnData: true
            })
        let { columns, functions } = result
        expect((() => {
            return columns.length == 2 &&
                functions.groupBy?.length == 2 &&
                functions.groupBy[0].column == arg.alias &&
                functions.groupBy[1].column == arg2.alias
        })()).toBeTruthy()
    })
    test('grouping on multiple columns at once from different groupBy functions', () => {
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
            .groupBy(arg.alias)
            .groupBy(arg2.alias)
            .reportFormData({
                returnData: true
            })
        let { columns, functions } = result
        expect((() => {
            return columns.length == 2 &&
                functions.groupBy.length == 2
                && functions.groupBy[0].column == arg.alias
                && functions.groupBy[1].column == arg2.alias
        })()).toBeTruthy()
    })
    test('grouping on single column on same aliasname multiple times', () => {

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
            .groupBy(arg.alias)
            .groupBy(arg.alias)
            .reportFormData({
                returnData: true
            })
        let { columns, functions } = result
        expect((() => {
            return columns.length == 2 && 
            functions?.groupBy?.length == 1 && 
            functions.groupBy[0].column == arg.alias 
        })()).toBeTruthy()
    })
    test('grouping on multiple columns grouping on same aliasname multiple times', () => {

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
            .groupBy(arg.alias, arg2.alias)
            .groupBy(arg.alias, arg2.alias)
            .reportFormData({
                returnData: true
            })
        let { columns, functions } = result
        expect((() => {
            return columns.length == 2 && 
            functions?.groupBy?.length == 2 && 
            functions.groupBy[0].column == arg.alias && 
            functions.groupBy[1].column == arg2.alias
        })()).toBeTruthy()
    })

    test('checking the order of groupBy applied', () => {
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
            .groupBy(arg.alias, arg2.alias)
            .reportFormData({
                returnData: true
            })
        let { columns, functions } = result
        expect((() => {
            return columns.length == 2 && 
            functions?.groupBy?.length == 2 && 
            functions.groupBy[0].column == arg.alias && 
            functions.groupBy[1].column == arg2.alias
        })()).toBeTruthy()
    })

    test('checking the order of groupBy applied -reverse', () => {
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
            .groupBy(arg2.alias, arg.alias)
            .reportFormData({
                returnData: true
            })
        let { columns, functions } = result
        expect((() => {
            columns.length == 2 && 
            !functions?.groupBy?.length == 2 && 
            functions.groupBy[0] == arg2.alias && 
            functions.groupBy[1] == arg.alias
        })())
    })

})
