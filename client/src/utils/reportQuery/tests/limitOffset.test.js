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
    test('checking valid limit', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination',
            limit : 10,
            offset : 10
        }
        let result = query.select(arg.column, arg.alias).limit(arg.limit)
            .reportFormData({
                returnData: true
            })
        let {columns, limitBy, offset} = result
        expect((() => {
            return limitBy == arg.limit
        })()).toBeTruthy()
    })

    test('inValid limit', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination',
            limit: null,
            offset: 10
        }
        let result = query.select(arg.column, arg.alias).limit(arg.limit)
            .reportFormData({
                returnData: true
            })
        let { columns, limitBy, offset } = result
        expect((() => {
            return limitBy == 50
        })()).toBeTruthy()
    })

    test('inValid limit', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination',
            limit: '55',
            offset: 10
        }
        let result = query.select(arg.column, arg.alias).limit(arg.limit)
            .reportFormData({
                returnData: true
            })
        let { columns, limitBy, offset } = result
        expect((() => {
            return limitBy == 50
        })()).toBeTruthy()
    })

    test('checking default limit', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination',
            limit: '55',
            offset: 10
        }
        let result = query.select(arg.column, arg.alias)
            .reportFormData({
                returnData: true
            })
        let { columns, limitBy, offset } = result
        expect((() => {
            return limitBy == 50
        })()).toBeTruthy()
    })

    test('checking ful; limit', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination',
            limit: -1,
            offset: 10
        }
        let result = query.select(arg.column, arg.alias)
            .limit(arg.limit, arg.offset)
            .reportFormData({
                returnData: true
            })
        let { columns, limitBy, offset } = result
        expect((() => {
            return limitBy == 'full'
        })()).toBeTruthy()
    })

})

describe('testing offset', () => {
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
    test('checking valid offset', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination',
            limit: 10,
            offset: 10
        }
        let result = query.select(arg.column, arg.alias).limit(arg.limit, arg.offset)
            .reportFormData({
                returnData: true
            })
        let { columns, limitBy, offset } = result
        expect((() => {
            return offset == arg.offset
        })()).toBeTruthy()
    })

    test('inValid offset', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination',
            limit: null,
            offset: 0
        }
        let result = query.select(arg.column, arg.alias).limit(arg.limit, arg.offset)
            .reportFormData({
                returnData: true
            })
        let { columns, limitBy, offset } = result
        expect((() => {
            return !('offset' in result)
        })()).toBeTruthy()
    })

    test('inValid limit', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination',
            limit: '55',
            offset: null
        }
        let result = query.select(arg.column, arg.alias).limit(arg.limit, arg.offset)
            .reportFormData({
                returnData: true
            })
        let { columns, limitBy, offset } = result
        expect((() => {
            return !('offset' in result)
        })()).toBeTruthy()
    })

})