import ReportQuery from '../index'
import 'core-js';
import { groupBy } from 'lodash';
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


/**
 * checing selectRAW
 * query.selectRaw('upper("HIUSER"."travel_details"."destination")', 'destination')
 */

describe('testing where', () => {
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
    test('valid where condition EQUALS for string', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let result = query.select(arg.column, arg.alias)
            .select(arg2.column, arg2.alias)
            .groupBy(arg2.alias, arg.alias)
            .where(arg.alias, '=', 'Agra')
            .functionBuilder()
            .reportFormData({
                returnData: true
            })
        let { filters } = result
        let [fil1] = filters
        expect((() => {
            return filters.length == 1 &&
                fil1.column == arg.column &&
                fil1.condition == 'EQUALS' &&
                fil1.values[0] == 'Agra' &&
                fil1.operator == 'AND'

        })()).toBeTruthy()
    })

    test('valid where condition EQUALS for string passing one valu in array', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let result = query.select(arg.column, arg.alias)
            .select(arg2.column, arg2.alias)
            .groupBy(arg2.alias, arg.alias)
            .where(arg.alias, '=', ['Agra'])
            .functionBuilder()
            .reportFormData({
                returnData: true
            })
        let { filters } = result
        let [fil1] = filters
        expect((() => {
            return filters.length == 1 &&
                fil1.column == arg.column &&
                fil1.condition == 'EQUALS' &&
                fil1.values[0] == 'Agra' &&
                fil1.operator == 'AND'

        })()).toBeTruthy()
    })

    test('NOTEQUALS string passing one valu as string', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let arg3 = {
            column: 'HIUSER.travel_details.source_id',
            alias: 'source_id'
        }
        let result = query.select(arg.column, arg.alias).select(arg2.column, arg2.alias)
            .where('source', '<>', 'Agra')
            .reportFormData({
                returnData: true
            })
        let { columns, filters, customFilterExpression } = result
        let [filter1] = filters
        expect(columns.length == 2 &&
            filters.length == 1 &&
            filter1.operator == 'AND' &&
            filter1.condition == 'CUSTOM' &&
            filter1.customCondition == '<>' &&
            customFilterExpression.includes('${0}') &&
            !customFilterExpression.includes('${1}')).toBeTruthy()
    })

    test('NOTEQUALS string passing one valu in array', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let arg3 = {
            column: 'HIUSER.travel_details.source_id',
            alias: 'source_id'
        }
        let result = query.select(arg.column, arg.alias).select(arg2.column, arg2.alias)
            .where('source', '<>', ['Agra'])
            .reportFormData({
                returnData: true
            })
        let { columns, filters, customFilterExpression } = result
        let [filter1] = filters
        expect(columns.length == 2 &&
            filters.length == 1 &&
            filter1.operator == 'AND' &&
            filter1.condition == 'CUSTOM' &&
            filter1.customCondition == '<>' &&
            customFilterExpression.includes('${0}') &&
            !customFilterExpression.includes('${1}')).toBeTruthy()
    })

    test('valid where condition for integer', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let arg3 = {
            column: 'HIUSER.travel_details.source_id',
            alias: 'source_id'
        }
        let result = query.select(arg.column, arg.alias).select(arg2.column, arg2.alias).select(arg3.column, arg3.alias)

            .where('source_id', '=', 1)
            .reportFormData({
                returnData: true
            })
        let { columns, filters, customFilterExpression } = result
        let [filter1] = filters
        expect(columns.length == 3 &&
            filters.length == 1 &&
            filter1.operator == 'AND' &&
            filter1.condition == 'EQUALS' &&
            customFilterExpression.includes('${0}') &&
            !customFilterExpression.includes('${1}')).toBeTruthy()

    })

    test('valid where condition for integer passing in an array', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let arg3 = {
            column: 'HIUSER.travel_details.source_id',
            alias: 'source_id'
        }
        let result = query.select(arg.column, arg.alias).select(arg2.column, arg2.alias).select(arg3.column, arg3.alias)

            .where('source_id', '=', [1])
            .reportFormData({
                returnData: true
            })
        let { columns, filters, customFilterExpression } = result
        let [filter1] = filters
        expect(columns.length == 3 &&
            filters.length == 1 &&
            filter1.operator == 'AND' &&
            filter1.condition == 'EQUALS' &&
            customFilterExpression.includes('${0}') &&
            !customFilterExpression.includes('${1}')).toBeTruthy()
    })

    test('NOTEQUALS integer passing in an array', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let arg3 = {
            column: 'HIUSER.travel_details.source_id',
            alias: 'source_id'
        }
        let result = query.select(arg.column, arg.alias).select(arg2.column, arg2.alias).select(arg3.column, arg3.alias)

            .where('source_id', '<>', [1])
            .reportFormData({
                returnData: true
            })
        let { columns, filters, customFilterExpression } = result
        let [filter1] = filters
        expect(columns.length == 3 &&
            filters.length == 1 &&
            filter1.operator == 'AND' &&
            filter1.condition == 'CUSTOM' &&
            filter1.customCondition == '<>' &&
            customFilterExpression.includes('${0}') &&
            !customFilterExpression.includes('${1}')).toBeTruthy()
    })

    test('GT integer passing in an array', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let arg3 = {
            column: 'HIUSER.travel_details.source_id',
            alias: 'source_id'
        }
        let result = query.select(arg.column, arg.alias).select(arg2.column, arg2.alias)
            .select(arg3.column, arg3.alias)

            .where('source_id', '>', [1])
            .reportFormData({
                returnData: true
            })
        let { columns, filters, customFilterExpression } = result
        let [filter1] = filters
        expect(columns.length == 3 &&
            filters.length == 1 &&
            filter1.operator == 'AND' &&
            filter1.condition == 'CUSTOM' &&
            filter1.customCondition == '>' &&
            customFilterExpression.includes('${0}') &&
            !customFilterExpression.includes('${1}')).toEqual(true)
    })

    test('GTE integer passing in an array', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let arg3 = {
            column: 'HIUSER.travel_details.source_id',
            alias: 'source_id'
        }
        let result = query.select(arg.column, arg.alias).select(arg2.column, arg2.alias)
            .select(arg3.column, arg3.alias)

            .where('source_id', '>=', [10])
            .reportFormData({
                returnData: true
            })
        let { columns, filters, customFilterExpression } = result
        let [filter1] = filters
        expect(columns.length == 3 &&
            filters.length == 1 &&
            filter1.operator == 'AND' &&
            filter1.condition == 'CUSTOM' &&
            filter1.customCondition == '>=' &&
            customFilterExpression.includes('${0}') &&
            !customFilterExpression.includes('${1}')).toEqual(true)
    })

    test('LTE integer passing in an array', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let arg3 = {
            column: 'HIUSER.travel_details.source_id',
            alias: 'source_id'
        }
        let result = query.select(arg.column, arg.alias).select(arg2.column, arg2.alias)
            .select(arg3.column, arg3.alias)

            .where('source_id', '<=', [10])
            .reportFormData({
                returnData: true
            })
        let { columns, filters, customFilterExpression } = result
        let [filter1] = filters
        expect(columns.length == 3 &&
            filters.length == 1 &&
            filter1.operator == 'AND' &&
            filter1.condition == 'CUSTOM' &&
            filter1.customCondition == '<=' &&
            customFilterExpression.includes('${0}') &&
            !customFilterExpression.includes('${1}')).toEqual(true)
    })

    test('where in intergers', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let arg3 = {
            column: 'HIUSER.travel_details.source_id',
            alias: 'source_id'
        }
        let result = query.select(arg.column, arg.alias).select(arg2.column, arg2.alias)
            .select(arg3.column, arg3.alias)

            .where('source_id', 'in', [1, 2, 3, 4, 5])
            .reportFormData({
                returnData: true
            })
        let { columns, filters, customFilterExpression } = result
        let [filter1] = filters
        expect(columns.length == 3 &&
            filters.length == 1 &&
            filter1.operator == 'AND' &&
            filter1.condition == 'CUSTOM' &&
            filter1.customCondition.includes('IN') &&
            filter1.customCondition.includes('(') &&
            customFilterExpression.includes('${0}') &&
            !customFilterExpression.includes('${1}')).toEqual(true)
    })

    test('where NOTIN intergers', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let arg3 = {
            column: 'HIUSER.travel_details.source_id',
            alias: 'source_id'
        }
        let result = query.select(arg.column, arg.alias).select(arg2.column, arg2.alias)
            .select(arg3.column, arg3.alias)

            .where('source_id', 'notin', [1, 2, 3, 4, 5])
            .reportFormData({
                returnData: true
            })
        let { columns, filters, customFilterExpression } = result
        let [filter1] = filters
        expect(columns.length == 3 &&
            filters.length == 1 &&
            filter1.operator == 'AND' &&
            filter1.condition == 'CUSTOM' &&
            filter1.customCondition.includes('NOT IN') &&
            filter1.customCondition.includes('(') &&
            customFilterExpression.includes('${0}') &&
            !customFilterExpression.includes('${1}')).toEqual(true)
    })

    test('where IN string array', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let arg3 = {
            column: 'HIUSER.travel_details.source_id',
            alias: 'source_id'
        }
        let result = query.select(arg.column, arg.alias).select(arg2.column, arg2.alias)
            .select(arg3.column, arg3.alias)

            .where('source', 'in', ['Agra', 'Hyderabad'])
            .reportFormData({
                returnData: true
            })

        let { columns, filters, customFilterExpression } = result
        let [filter1] = filters
        expect(columns.length == 3 &&
            filters.length == 1 &&
            filter1.operator == 'AND' &&
            filter1.condition == 'CUSTOM' &&
            filter1.customCondition.includes('IN') &&
            filter1.customCondition.includes('(') &&
            customFilterExpression.includes('${0}') &&
            !customFilterExpression.includes('${1}')).toEqual(true)
    })

    test('where NOTIN string array', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let arg3 = {
            column: 'HIUSER.travel_details.source_id',
            alias: 'source_id'
        }
        let result = query.select(arg.column, arg.alias).select(arg2.column, arg2.alias)
            .select(arg3.column, arg3.alias)

            .where('source', 'notin', ['Agra', 'Hyderabad'])
            .reportFormData({
                returnData: true
            })
        let { columns, filters, customFilterExpression } = result
        let [filter1] = filters
        expect(columns.length == 3 &&
            filters.length == 1 &&
            filter1.operator == 'AND' &&
            filter1.condition == 'CUSTOM' &&
            filter1.customCondition.includes('NOT IN') &&
            filter1.customCondition.includes('(') &&
            customFilterExpression.includes('${0}') &&
            !customFilterExpression.includes('${1}')).toEqual(true)
    })

    test('where INRANGE integer array', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let arg3 = {
            column: 'HIUSER.travel_details.source_id',
            alias: 'source_id'
        }
        let result = query.select(arg.column, arg.alias).select(arg2.column, arg2.alias)
            .select(arg3.column, arg3.alias)

            .where('source_id', 'inrange', [1, 10])
            .reportFormData({
                returnData: true
            })
        let { columns, filters, customFilterExpression } = result
        let [filter1] = filters
        expect(columns.length == 3 &&
            filters.length == 1 &&
            filter1.operator == 'AND' &&
            filter1.condition == 'IN_RANGE' &&
            customFilterExpression.includes('${0}') &&
            !customFilterExpression.includes('${1}')).toBeTruthy()
    })

    test('where NOTINRANGE integer array', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let arg3 = {
            column: 'HIUSER.travel_details.source_id',
            alias: 'source_id'
        }
        let result = query.select(arg.column, arg.alias).select(arg2.column, arg2.alias)
            .select(arg3.column, arg3.alias)

            .where('source_id', 'notinrange', [1, 10])
            .reportFormData({
                returnData: true
            })
        let { columns, filters, customFilterExpression } = result
        let [filter1] = filters
        expect(columns.length == 3 &&
            filters.length == 1 &&
            filter1.operator == 'AND' &&
            filter1.condition == 'NOT_IN_RANGE' &&
            customFilterExpression.includes('${0}') &&
            !customFilterExpression.includes('${1}')).toBeTruthy()
    })

    test('where ISNULL', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let arg3 = {
            column: 'HIUSER.travel_details.source_id',
            alias: 'source_id'
        }
        let result = query.select(arg.column, arg.alias).select(arg2.column, arg2.alias)
            .select(arg3.column, arg3.alias)
            .where('source', 'null')
            .reportFormData({
                returnData: true
            })
        let { columns, filters, customFilterExpression } = result
        let [filter1] = filters
        expect(columns.length == 3 &&
            filters.length == 1 &&
            filter1.operator == 'AND' &&
            filter1.condition == 'CUSTOM' &&
            filter1.customCondition == 'IS NULL' &&
            customFilterExpression.includes('${0}') &&
            !customFilterExpression.includes('${1}')).toBeTruthy()
    })

    test('where ISNOTNULL', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let arg3 = {
            column: 'HIUSER.travel_details.source_id',
            alias: 'source_id'
        }
        let result = query.select(arg.column, arg.alias).select(arg2.column, arg2.alias)
            .select(arg3.column, arg3.alias)

            .where('source', 'notnull')
            .reportFormData({
                returnData: true
            })
        let { columns, filters, customFilterExpression } = result
        let [filter1] = filters
        expect(columns.length == 3 &&
            filters.length == 1 &&
            filter1.operator == 'AND' &&
            filter1.condition == 'CUSTOM' &&
            filter1.customCondition == 'IS NOT NULL' &&
            customFilterExpression.includes('${0}') &&
            !customFilterExpression.includes('${1}')).toBeTruthy()
    })

    test('.whereIn intergers', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let arg3 = {
            column: 'HIUSER.travel_details.source_id',
            alias: 'source_id'
        }
        let result = query.select(arg.column, arg.alias).select(arg2.column, arg2.alias)
            .select(arg3.column, arg3.alias)

            .whereIn('source_id', [1, 2, 3, 4, 5])
            .reportFormData({
                returnData: true
            })
        let { columns, filters, customFilterExpression } = result
        let [filter1] = filters
        expect(columns.length == 3 &&
            filters.length == 1 &&
            filter1.operator == 'AND' &&
            filter1.condition == 'CUSTOM' &&
            filter1.customCondition.includes('IN') &&
            filter1.customCondition.includes('(') &&
            customFilterExpression.includes('${0}') &&
            !customFilterExpression.includes('${1}')).toEqual(true)
    })

    test('.whereNotIn intergers', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let arg3 = {
            column: 'HIUSER.travel_details.source_id',
            alias: 'source_id'
        }
        let result = query.select(arg.column, arg.alias).select(arg2.column, arg2.alias)
            .select(arg3.column, arg3.alias)

            .whereNotIn('source_id', [1, 2, 3, 4, 5])
            .reportFormData({
                returnData: true
            })
        let { columns, filters, customFilterExpression } = result
        let [filter1] = filters
        expect(columns.length == 3 &&
            filters.length == 1 &&
            filter1.operator == 'AND' &&
            filter1.condition == 'CUSTOM' &&
            filter1.customCondition.includes('NOT IN') &&
            filter1.customCondition.includes('(') &&
            customFilterExpression.includes('${0}') &&
            !customFilterExpression.includes('${1}')).toEqual(true)
    })

    test('IN date Arr', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let arg3 = {
            column: 'HIUSER.travel_details.source_id',
            alias: 'source_id'
        }
        let arg4 = {
            column: 'HIUSER.travel_details.travel_date',
            alias: 'travel_date'
        }
        let result = query.select(arg.column, arg.alias).select(arg2.column, arg2.alias)
            .select(arg3.column, arg3.alias).select(arg4.column, arg4.alias)
            .functionBuilder(builder => builder.cast(arg4.alias, 'date'), arg4.alias)
            .where('travel_date', '=', '2015-01-04')
            .reportFormData({
                returnData: true
            })

        let { columns, filters, customFilterExpression } = result
        let [filter1, filter2] = filters
        expect(columns.length == 4 &&
            filters.length == 1 &&
            filter1.operator == 'AND' &&
            filter1.condition == 'EQUALS' &&
            //     filter1.customCondition.includes('IN') &&
            customFilterExpression.includes('${0}')
        ).toBeTruthy()
    })

    test('EQUALS date year', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let arg3 = {
            column: 'HIUSER.travel_details.source_id',
            alias: 'source_id'
        }
        let arg4 = {
            column: 'HIUSER.travel_details.travel_date',
            alias: 'travel_date'
        }
        let result = query.select(arg.column, arg.alias).select(arg2.column, arg2.alias)
            .select(arg3.column, arg3.alias).select(arg4.column, arg4.alias)
            .functionBuilder(builder => builder.year(arg4.alias), arg4.alias)
            .where('travel_date', '=', ['2015'])
            .reportFormData({
                returnData: true
            })
        let { columns, filters, customFilterExpression } = result
        let [filter1, filter2] = filters
        expect(columns.length == 4 &&
            filters.length == 1 &&
            filter1.operator == 'AND' &&
            filter1.condition == 'EQUALS' &&
            filter1.databaseFunction.functionName.includes('year') &&
            customFilterExpression.includes('${0}')
        ).toBeTruthy()
    })

    test('EQUALS date month', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let arg3 = {
            column: 'HIUSER.travel_details.source_id',
            alias: 'source_id'
        }
        let arg4 = {
            column: 'HIUSER.travel_details.travel_date',
            alias: 'travel_date'
        }
        let result = query.select(arg.column, arg.alias).select(arg2.column, arg2.alias)
            .select(arg3.column, arg3.alias).select(arg4.column, arg4.alias)
            .functionBuilder(builder => builder.month(arg4.alias), arg4.alias)
            .where('travel_date', '=', ['4'])
            .reportFormData({
                returnData: true
            })
        let { columns, filters, customFilterExpression } = result
        let [filter1, filter2] = filters
        expect(columns.length == 4 &&
            filters.length == 1 &&
            filter1.operator == 'AND' &&
            filter1.condition == 'EQUALS' &&
            filter1.databaseFunction.functionName.includes('month') &&
            customFilterExpression.includes('${0}')
        ).toBeTruthy()
    })

})


describe('combining multiple where conditions', () => {
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
    test('valid where condition EQUALS for string', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let result = query.select(arg.column, arg.alias)
            .select(arg2.column, arg2.alias)
            .groupBy(arg2.alias, arg.alias)
            .where(arg.alias, '=', 'Agra')
            .functionBuilder()
            .reportFormData({
                returnData: true
            })
        let { filters } = result
        let [fil1] = filters
        expect((() => {
            return filters.length == 1 &&
                fil1.column == arg.column &&
                fil1.condition == 'EQUALS' &&
                fil1.values[0] == 'Agra' &&
                fil1.operator == 'AND'

        })()).toBeTruthy()
    })
    test('in and notin integers', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let arg3 = {
            column: 'HIUSER.travel_details.source_id',
            alias: 'source_id'
        }
        let result = query.select(arg.column, arg.alias).select(arg2.column, arg2.alias)
            .select(arg3.column, arg3.alias)

            .where('source_id', 'in', [1, 2, 3, 4, 5]).where('source_id', 'notin', [7, 8, 9])
            .reportFormData({
                returnData: true
            })
        let { columns, filters, customFilterExpression } = result
        let [filter1, filter2] = filters
        expect(columns.length == 3 &&
            filters.length == 2 &&
            filter1.operator == 'AND' &&
            filter2.operator == 'AND' &&
            filter1.condition == 'CUSTOM' &&
            filter1.customCondition.includes('IN') &&
            filter2.customCondition.includes('NOT IN') &&
            filter1.customCondition.includes('(') &&
            filter1.customCondition.includes('(') &&
            filter1.values[0].includes(')') &&
            customFilterExpression.includes('${0}') &&
            customFilterExpression.includes('AND') &&
            customFilterExpression.includes('${1}')
        ).toBeTruthy()
    })

    test('in or notin integers', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let arg3 = {
            column: 'HIUSER.travel_details.source_id',
            alias: 'source_id'
        }
        let result = query.select(arg.column, arg.alias).select(arg2.column, arg2.alias)
            .select(arg3.column, arg3.alias)

            .where('source_id', 'in', [1, 2, 3, 4, 5]).orWhere('source_id', 'notin', [7, 8, 9])
            .reportFormData({
                returnData: true
            })
        let { columns, filters, customFilterExpression } = result
        let [filter1, filter2] = filters
        expect(columns.length == 3 &&
            filters.length == 2 &&
            filter1.operator == 'AND' &&
            filter2.operator == 'OR' &&
            filter1.condition == 'CUSTOM' &&
            filter1.customCondition.includes('IN') &&
            filter2.customCondition.includes('NOT IN') &&
            filter1.customCondition.includes('(') &&
            filter1.customCondition.includes('(') &&
            filter1.values[0].includes(')') &&
            customFilterExpression.includes('${0}') &&
            customFilterExpression.includes('OR') &&
            customFilterExpression.includes('${1}')
        ).toBeTruthy()
    })

    test('notnull or notin integers', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let arg3 = {
            column: 'HIUSER.travel_details.source_id',
            alias: 'source_id'
        }
        let result = query.select(arg.column, arg.alias).select(arg2.column, arg2.alias)
            .select(arg3.column, arg3.alias)
            .where('source_id', 'notnull').orWhere('source_id', 'notin', [7, 8, 9])
            .reportFormData({
                returnData: true
            })
        let { columns, filters, customFilterExpression } = result
        let [filter1, filter2] = filters
        expect(columns.length == 3 &&
            filters.length == 2 &&
            filter1.operator == 'AND' &&
            filter2.operator == 'OR' &&
            filter1.condition == 'CUSTOM' &&
            filter1.customCondition.includes('IS NOT NULL') &&
            filter2.customCondition.includes('NOT IN') &&
            filter2.customCondition.includes('(') &&
            customFilterExpression.includes('${0}') &&
            customFilterExpression.includes('OR') &&
            customFilterExpression.includes('${1}')
        ).toBeTruthy()
    })

    test('IN date Arr', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let arg3 = {
            column: 'HIUSER.travel_details.source_id',
            alias: 'source_id'
        }
        let arg4 = {
            column: 'HIUSER.travel_details.travel_date',
            alias: 'travel_date'
        }
        let result = query.select(arg.column, arg.alias).select(arg2.column, arg2.alias)
            .select(arg3.column, arg3.alias).select(arg4.column, arg4.alias)
            .functionBuilder(builder => builder.cast(arg4.alias, 'date'), arg4.alias)
            .where('travel_date', 'in', ['2015-01-04', '2015-01-09'])
            .reportFormData({
                returnData: true
            })
        let { columns, filters, customFilterExpression } = result
        let [filter1, filter2] = filters
        expect(columns.length == 4 &&
            filters.length == 1 &&
            filter1.operator == 'AND' &&
            filter1.condition == 'CUSTOM' &&
            filter1.customCondition.includes('IN') &&
            customFilterExpression.includes('${0}')).toBeTruthy()
    })



    test('INRANGE date Arr', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let arg3 = {
            column: 'HIUSER.travel_details.source_id',
            alias: 'source_id'
        }
        let arg4 = {
            column: 'HIUSER.travel_details.travel_date',
            alias: 'travel_date'
        }
        let result = query.select(arg.column, arg.alias).select(arg2.column, arg2.alias)
            .select(arg3.column, arg3.alias).select(arg4.column, arg4.alias)
            .functionBuilder(builder => builder.cast(arg4.alias, 'date'), arg4.alias)
            .where('travel_date', 'inrange', ['2015-01-04', '2015-01-10'])
            .reportFormData({
                returnData: true
            })
        let { columns, filters, customFilterExpression } = result
        let [filter1, filter2] = filters
        expect(columns.length == 4 &&
            filters.length == 1 &&
            filter1.operator == 'AND' &&
            filter1.condition == 'IN_RANGE' &&
            //     filter1.customCondition.includes('IN') &&
            customFilterExpression.includes('${0}')
        ).toBeTruthy()
    })

    test('EQUALS OR EQUALS date', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let arg3 = {
            column: 'HIUSER.travel_details.source_id',
            alias: 'source_id'
        }
        let arg4 = {
            column: 'HIUSER.travel_details.travel_date',
            alias: 'travel_date'
        }
        let result = query.select(arg.column, arg.alias).select(arg2.column, arg2.alias)
            .select(arg3.column, arg3.alias).select(arg4.column, arg4.alias)
            .functionBuilder(builder => builder.cast(arg4.alias, 'date'), arg4.alias)
            .where('travel_date', '=', '2015-01-04').orWhere('travel_date', '=', '2015-01-06')
            .reportFormData({
                returnData: true
            })
        let { columns, filters, customFilterExpression } = result
        let [filter1, filter2] = filters
        expect(columns.length == 4 &&
            filters.length == 2 &&
            filter1.operator == 'AND' &&
            filter2.operator == 'OR' &&
            filter1.condition == 'EQUALS' &&
            filter2.condition == 'EQUALS' &&

            //     filter1.customCondition.includes('IN') &&
            customFilterExpression.includes('${0}') &&
            customFilterExpression.includes('OR') &&
            customFilterExpression.includes('${1}')
        ).toBeTruthy()
    })

    test('EQUALS OR INRANGE date', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let arg3 = {
            column: 'HIUSER.travel_details.source_id',
            alias: 'source_id'
        }
        let arg4 = {
            column: 'HIUSER.travel_details.travel_date',
            alias: 'travel_date'
        }
        let result = query.select(arg.column, arg.alias).select(arg2.column, arg2.alias)
            .select(arg3.column, arg3.alias).select(arg4.column, arg4.alias)
            .functionBuilder(builder => builder.cast(arg4.alias, 'date'), arg4.alias)
            .where('travel_date', '=', '2015-01-04')
            .orWhere('travel_date', 'inrange', ['2015-01-04', '2015-01-10'])
            .reportFormData({
                returnData: true
            })
        let { columns, filters, customFilterExpression } = result
        let [filter1, filter2] = filters
        expect(columns.length == 4 &&
            filters.length == 2 &&
            filter1.operator == 'AND' &&
            filter2.operator == 'OR' &&
            filter1.condition == 'EQUALS' &&
            filter2.condition == 'IN_RANGE' &&

            //     filter1.customCondition.includes('IN') &&
            customFilterExpression.includes('${0}') &&
            customFilterExpression.includes('OR') &&
            customFilterExpression.includes('${1}')
        ).toBeTruthy()
    })



    test('EQUALS or NE date month', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let arg3 = {
            column: 'HIUSER.travel_details.source_id',
            alias: 'source_id'
        }
        let arg4 = {
            column: 'HIUSER.travel_details.travel_date',
            alias: 'travel_date'
        }
        let result = query.select(arg.column, arg.alias).select(arg2.column, arg2.alias)
            .select(arg3.column, arg3.alias).select(arg4.column, arg4.alias)
            .functionBuilder(builder => builder.month(arg4.alias), arg4.alias)
            .where('travel_date', '=', ['4']).orWhere('travel_date', '<>', ['5'])
            .reportFormData({
                returnData: true
            })
        let { columns, filters, customFilterExpression } = result
        let [filter1, filter2] = filters
        expect(columns.length == 4 &&
            filters.length == 2 &&
            filter1.operator == 'AND' &&
            filter2.operator == 'OR' &&
            filter1.condition == 'EQUALS' &&
            filter2.condition == 'CUSTOM' &&
            filter1.databaseFunction.functionName.includes('month') &&
            customFilterExpression.includes('${0}') &&
            customFilterExpression.includes('OR') &&
            customFilterExpression.includes('${1}')
        ).toBeTruthy()
    })

})

describe('where builder', () => {
    test('WHEREBUILDER in range date array', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let arg3 = {
            column: 'HIUSER.travel_details.source_id',
            alias: 'source_id'
        }
        let arg4 = {
            column: 'HIUSER.travel_details.travel_date',
            alias: 'travel_date'
        }
        let result = query.select(arg.column, arg.alias)
            .select(arg2.column, arg2.alias)
            .select(arg3.column, arg3.alias)
            .select(arg4.column, arg4.alias)
            .functionBuilder(builder => builder.cast(arg4.alias, 'date'), arg4.alias)
            .where(builder => builder.where('travel_date', 'inrange', ['2015-01-04', '2015-01-10']))
            .reportFormData({
                returnData: true
            })
        let { columns, functions, filters } = result
        let [col1, col2] = columns
        let [{ values, databaseFunction, operator, condition }] = filters
        expect(columns.length == 4 &&
            filters.length == 1 &&
            filters[0].values.indexOf('2015-01-04') == 0 &&
            operator == 'AND' &&
            databaseFunction &&
            databaseFunction.parameters.column == arg4.column &&
            condition == 'IN_RANGE').toBeTruthy()

    })

    test('WHEREBUILDER date in range or destination not in jaipur', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let arg3 = {
            column: 'HIUSER.travel_details.source_id',
            alias: 'source_id'
        }
        let arg4 = {
            column: 'HIUSER.travel_details.travel_date',
            alias: 'travel_date'
        }
        let result = query.select(arg.column, arg.alias)
            .select(arg2.column, arg2.alias)
            .select(arg3.column, arg3.alias)
            .select(arg4.column, arg4.alias)
            .functionBuilder(builder => builder.cast(arg4.alias, 'date'), arg4.alias)
            .where(builder => builder.where('travel_date', 'inrange', ['2015-01-04', '2015-01-10'])
                .orWhere(arg2.alias, '<>', ['Jaipur']))
            .reportFormData({
                returnData: true
            })
        let { columns, functions, filters } = result
        let [col1, col2] = columns
        let [filter1, filter2] = filters
        expect(columns.length == 4 &&
            filters.length == 2 &&
            filter1.operator == 'AND' &&
            filter2.operator == 'OR' &&
            filter1.condition == 'IN_RANGE' &&
            filter2.condition == 'CUSTOM' &&
            filter1.values[0] == '2015-01-04' &&
            filter2.values[0] == 'Jaipur').toBeTruthy()
    })

    test('WHEREBUILDER date in range or destination  in jaipur or jaipur', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let arg3 = {
            column: 'HIUSER.travel_details.source_id',
            alias: 'source_id'
        }
        let arg4 = {
            column: 'HIUSER.travel_details.travel_date',
            alias: 'travel_date'
        }
        let result = query.select(arg.column, arg.alias)
            .select(arg2.column, arg2.alias)
            .select(arg3.column, arg3.alias)
            .select(arg4.column, arg4.alias)
            .functionBuilder(builder => builder.cast(arg4.alias, 'date'), arg4.alias)
            .where(builder => builder.where('travel_date', 'inrange', ['2015-01-04', '2015-01-10'])
                .orWhere(arg2.alias, 'in', ['Jaipur', 'Hyderabad']))
            .reportFormData({
                returnData: true
            })
        let { columns, functions, filters, customFilterExpression } = result
        let [col1, col2] = columns
        let [filter1, filter2] = filters
        expect(columns.length == 4 &&
            filters.length == 2 &&
            filter1.operator == 'AND' &&
            filter2.operator == 'OR' &&
            filter1.condition == 'IN_RANGE' &&
            filter2.condition == 'CUSTOM' &&
            filter1.values[0] == '2015-01-04' &&
            filter2.values[0].includes('Jaipur') &&
            customFilterExpression == " (  ${0} OR ${1} )").toBeTruthy()
    })

    test('combining where builder and normal where condition', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let arg3 = {
            column: 'HIUSER.travel_details.source_id',
            alias: 'source_id'
        }
        let arg4 = {
            column: 'HIUSER.travel_details.travel_date',
            alias: 'travel_date'
        }
        let result = query.select(arg.column, arg.alias)
            .select(arg2.column, arg2.alias)
            .select(arg3.column, arg3.alias)
            .select(arg4.column, arg4.alias)
            .functionBuilder(builder => builder.cast(arg4.alias, 'date'), arg4.alias)
            .where(arg.alias, '=', 'Nagpur')
            .where(builder => builder.where('travel_date', 'inrange', ['2015-01-04', '2015-01-10'])
                .orWhere(arg2.alias, 'in', ['Jaipur', 'Hyderabad']))
            .reportFormData({
                returnData: true
            })

        let { columns, functions, filters, customFilterExpression } = result
        let [filter1, filter2, filter3] = filters
        expect(columns.length == 4 &&
            filters.length == 3 &&
            filter1.condition == 'EQUALS' &&
            filter1.operator == 'AND' &&
            filter1.values[0] == 'Nagpur' &&
            filter2.condition == 'IN_RANGE' &&
            filter2.operator == 'AND' &&
            filter3.condition == 'CUSTOM' &&
            customFilterExpression == " ${0} AND (  ${1} OR ${2} )" &&
            filter3.values[0].includes('Jaipur')).toBeTruthy()
    })
})

describe('other where methods', () => {
    test('builder - whereIn', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let arg3 = {
            column: 'HIUSER.travel_details.source_id',
            alias: 'source_id'
        }
        let arg4 = {
            column: 'HIUSER.travel_details.travel_date',
            alias: 'travel_date'
        }
        let result = query.select(arg.column, arg.alias)
            .select(arg2.column, arg2.alias)
            .select(arg3.column, arg3.alias)
            .select(arg4.column, arg4.alias)
            .functionBuilder(builder => builder.cast(arg4.alias, 'date'), arg4.alias)
            .where(builder => builder.whereIn('travel_date', ['2015-01-04', '2015-01-10']))
            .reportFormData({
                returnData: true
            })
        let { columns, functions, filters, customFilterExpression } = result
        let [filter1] = filters
        expect(columns.length == 4 &&
            filters.length == 1 &&
            filter1.condition == 'CUSTOM' &&
            filter1.customCondition.includes('IN') &&
            filter1.databaseFunction.functionName.includes('cast') &&
            customFilterExpression.includes('${0}') &&
            customFilterExpression.split('(').length == 2 &&
            customFilterExpression.split(')').length == 2).toBeTruthy()
    })

    test('builder - whereNotIn', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let arg3 = {
            column: 'HIUSER.travel_details.source_id',
            alias: 'source_id'
        }
        let arg4 = {
            column: 'HIUSER.travel_details.travel_date',
            alias: 'travel_date'
        }
        let result = query.select(arg.column, arg.alias)
            .select(arg2.column, arg2.alias)
            .select(arg3.column, arg3.alias)
            .select(arg4.column, arg4.alias)
            .functionBuilder(builder => builder.cast(arg4.alias, 'date'), arg4.alias)
            .where(builder => builder.whereNotIn('travel_date', ['2015-01-04', '2015-01-10']))
            .reportFormData({
                returnData: true
            })
        let { columns, functions, filters, customFilterExpression } = result
        let [filter1] = filters
        expect(columns.length == 4 &&
            filters.length == 1 &&
            filter1.condition == 'CUSTOM' &&
            filter1.customCondition.includes('NOT IN') &&
            filter1.databaseFunction.functionName.includes('cast') &&
            customFilterExpression.includes('${0}') &&
            customFilterExpression.split('(').length == 2 &&
            customFilterExpression.split(')').length == 2).toBeTruthy()
    })

    test('builder - orWhereNotIn', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let arg3 = {
            column: 'HIUSER.travel_details.source_id',
            alias: 'source_id'
        }
        let arg4 = {
            column: 'HIUSER.travel_details.travel_date',
            alias: 'travel_date'
        }
        let result = query.select(arg.column, arg.alias)
            .select(arg2.column, arg2.alias)
            .select(arg3.column, arg3.alias)
            .select(arg4.column, arg4.alias)
            .functionBuilder(builder => builder.cast(arg4.alias, 'date'), arg4.alias)
            .where(builder => builder.orWhereNotIn('travel_date', ['2015-01-04', '2015-01-10']))
            .reportFormData({
                returnData: true
            })
        let { columns, functions, filters, customFilterExpression } = result
        let [filter1] = filters
        expect(columns.length == 4 &&
            filters.length == 1 &&
            filter1.condition == 'CUSTOM' &&
            filter1.customCondition.includes('NOT IN') &&
            filter1.databaseFunction.functionName.includes('cast') &&
            customFilterExpression.includes('${0}') &&
            customFilterExpression.split('(').length == 2 &&
            filter1.operator == 'OR' &&
            customFilterExpression.split(')').length == 2).toBeTruthy()
    })

    test('where builder - where in or where', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let arg3 = {
            column: 'HIUSER.travel_details.source_id',
            alias: 'source_id'
        }
        let arg4 = {
            column: 'HIUSER.travel_details.travel_date',
            alias: 'travel_date'
        }
        let result = query.select(arg.column, arg.alias)
            .select(arg2.column, arg2.alias)
            .select(arg3.column, arg3.alias)
            .select(arg4.column, arg4.alias)
            .functionBuilder(builder => builder.cast(arg4.alias, 'date'), arg4.alias)
            .where(builder => builder.whereNotIn('travel_date', ['2015-01-04', '2015-01-10'])
                .orWhere('destination', '=', 'Hyderabad'))
            .reportFormData({
                returnData: true
            })
        let { columns, functions, filters, customFilterExpression } = result
        let [filter1, filter2] = filters
        expect(columns.length == 4 &&
            filters.length == 2 &&
            filter1.operator == 'AND' &&
            filter2.operator == 'OR' &&
            filter1.customCondition.includes('NOT IN') &&
            filter2.condition == 'EQUALS' &&
            filter1.values[0].includes('2015-01-04') &&
            filter2.values[0] == 'Hyderabad' &&
            customFilterExpression.includes('${0}') &&
            customFilterExpression.includes('${1}') &&
            customFilterExpression.includes('OR') &&
            customFilterExpression.split('(').length == 2 &&
            customFilterExpression.split(')').length == 2).toBeTruthy()
    })

    test('where builder - testing LIKE', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let result = query.select(arg.column, arg.alias)
            .select(arg2.column, arg2.alias)
            .where('destination', 'like', 'abad')
            .reportFormData({
                returnData: true
            })
        let [filter1] = result.filters
        expect((() => {
            return result.filters.length == 1 &&
                filter1.condition === "CUSTOM" &&
                filter1.customCondition === "like" &&
                filter1.values[0].includes('%abad%')
        })()).toBeTruthy()
    })

    test('where builder - testing contains', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let result = query.select(arg.column, arg.alias)
            .select(arg2.column, arg2.alias)
            .where('destination', 'contains', 'Hyder')
            .reportFormData({
                returnData: true
            })
        let [filter1] = result.filters
        expect((() => {
            return result.filters.length == 1 &&
                filter1.condition === "CUSTOM" &&
                filter1.customCondition === "like" &&
                filter1.values[0].includes('%Hyder%')
        })()).toBeTruthy()
    })

    test('where builder - testing starts-with', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let result = query.select(arg.column, arg.alias)
            .select(arg2.column, arg2.alias)
            .where('destination', 'startswith', 'H')
            .reportFormData({
                returnData: true
            })
        let [filter1] = result.filters
        expect((() => {
            return result.filters.length == 1 &&
                filter1.condition === "CUSTOM" &&
                filter1.customCondition === "like" &&
                filter1.values[0].includes('H%')
        })()).toBeTruthy()
    })

    test('where builder - testing ends-with', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let result = query.select(arg.column, arg.alias)
            .select(arg2.column, arg2.alias)
            .where('destination', 'endswith', 'd')
            .reportFormData({
                returnData: true
            })
        let [filter1] = result.filters
        expect((() => {
            return result.filters.length == 1 &&
                filter1.condition === "CUSTOM" &&
                filter1.customCondition === "like" &&
                filter1.values[0].includes('%d')
        })()).toBeTruthy()
    })

    test('where builder - testing not-like', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let result = query.select(arg.column, arg.alias)
            .select(arg2.column, arg2.alias)
            .where('destination', 'notlike', 'd')
            .reportFormData({
                returnData: true
            })
        let [filter1] = result.filters
        expect((() => {
            return result.filters.length == 1 &&
                filter1.condition === "CUSTOM" &&
                filter1.customCondition === "not like" &&
                filter1.values[0].includes('%d%')
        })()).toBeTruthy()
    })

    test('where builder - testing does-not-contain', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let result = query.select(arg.column, arg.alias)
            .select(arg2.column, arg2.alias)
            .where('destination', 'doesnotcontains', 'z')
            .reportFormData({
                returnData: true
            })
        let [filter1] = result.filters
        expect((() => {
            return result.filters.length == 1 &&
                filter1.condition === "CUSTOM" &&
                filter1.customCondition === "not like" &&
                filter1.values[0].includes('%z%')
        })()).toBeTruthy()
    })

    test('where builder - testing does-not-ends-with', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let result = query.select(arg.column, arg.alias)
            .select(arg2.column, arg2.alias)
            .where('destination', 'doesnotstartswith', 'z')
            .reportFormData({
                returnData: true
            })
        let [filter1] = result.filters
        expect((() => {
            return result.filters.length == 1 &&
                filter1.condition === "CUSTOM" &&
                filter1.customCondition === "not like" &&
                filter1.values[0].includes('%z%')
        })()).toBeTruthy()
    })

    test('where builder - testing does-not-ends-with', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let result = query.select(arg.column, arg.alias)
            .select(arg2.column, arg2.alias)
            .where('destination', 'doesnotendswith', 'z')
            .reportFormData({
                returnData: true
            })
        let [filter1] = result.filters
        expect((() => {
            return result.filters.length == 1 &&
                filter1.condition === "CUSTOM" &&
                filter1.customCondition === "not like" &&
                filter1.values[0].includes('%z%')
        })()).toBeTruthy()
    })

    test('where builder - passing additional configuration -- hidden:true', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let result = query.select(arg.column, arg.alias)
            .select(arg2.column, arg2.alias)
            .where('destination', '=', 'Hyderabad', { hidden: true })
            .reportFormData({
                returnData: true
            })
        let [filter1] = result.filters
        expect((() => {
            return result.filters.length == 1 &&
                'hidden' in filter1 && filter1.hidden
        })()).toBeTruthy()
    })

    test('where builder - passing additional configuration -- someKey:someValue', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let arg2 = {
            column: 'HIUSER.travel_details.source',
            alias: 'source'
        }
        let result = query.select(arg.column, arg.alias)
            .select(arg2.column, arg2.alias)
            .where('destination', '=', 'Hyderabad', { someKey: 'someValue' })
            .reportFormData({
                returnData: true
            })
        let [filter1] = result.filters
        expect((() => {
            return result.filters.length == 1 &&
                'someKey' in filter1 && filter1.someKey === 'someValue'
        })()).toBeTruthy()
    })

    test('passing column id as extra argument with column to where condition', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.source_id',
            alias: 'source_id',
            id: "13604"
        }
        let result = query.select({ name: arg.column, id: arg.id }, arg.alias).where('source_id', '=', [1])
            .reportFormData({
                returnData: true
            })
        let { columns, filters, customFilterExpression } = result;
        let [filter1] = filters
        expect(columns.length == 1 &&
            columns[0].column.name === arg.column &&
            columns[0].column.id === arg.id &&
            filters.length == 1 &&
            filter1.operator == 'AND' &&
            filter1.condition == 'EQUALS' &&
            customFilterExpression.includes('${0}') &&
            !customFilterExpression.includes('${1}')).toBeTruthy()
    })
})
