import ReportQuery from '../index'
import 'core-js';
import { groupBy } from 'lodash';
const getQuery = () => new ReportQuery({
    "location": "1639026125991",
    "metadataFileName": "59ced249-8d96-4175-aa88-75a17b200b5c.metadata",
})


describe('testing REFRESH  - reportQuery', () => {
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
    test('valid refresh', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let result = query.select(arg.column, arg.alias)
            .refresh()
            .reportFormData({
                returnData: true
            })
        let { columns, functions, refresh } = result
        expect((() => {
            return refresh
            // && 
            // functions.orderBy[0].alias == arg.alias
        })()).toBeTruthy()
    })
    test('valid refresh passign true', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let result = query.select(arg.column, arg.alias)
            .refresh(true)
            .reportFormData({
                returnData: true
            })
        let { columns, functions, refresh } = result
        expect((() => {
            return refresh
            // && 
            // functions.orderBy[0].alias == arg.alias
        })()).toBeTruthy()
    })

    test('valid refresh passign false', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let result = query.select(arg.column, arg.alias)
            .refresh(false)
            .reportFormData({
                returnData: true
            })
        let { columns, functions, refresh } = result
        expect((() => {
            return !refresh
            // && 
            // functions.orderBy[0].alias == arg.alias
        })()).toBeTruthy()
    })

    test('invalid refresh passing string', () => {
        let query = getQuery()
        let arg = {
            column: 'HIUSER.travel_details.destination',
            alias: 'destination'
        }
        let result = query.select(arg.column, arg.alias)
            .refresh('random')
            .reportFormData({
                returnData: true
            })
        let { columns, functions, refresh } = result
        expect((() => {
            return !refresh
            // && 
            // functions.orderBy[0].alias == arg.alias
        })()).toBeTruthy()
    })

})



describe('testing hiddem hide and include in resultset  - reportQuery', () => {
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
    test('valid hide function for one column FIRST COL', () => {
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
            .hide(arg.alias)
            .reportFormData({
                returnData: true
            })
        let { columns, functions } = result
        let [col1, col2] = columns
        expect((() => {
            return columns.length == 2 &&
            col1.alias === arg.alias &&
            'hidden' in col1 &&
            col1.hidden
        })()).toBeTruthy()
    })

    test('valid hide function for one column SECOND COL', () => {
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
            .hide(arg2.alias)
            .reportFormData({
                returnData: true
            })
        let { columns, functions } = result
        let [col1, col2] = columns
        expect((() => {
            return columns.length == 2 &&
                col2.alias === arg2.alias &&
                'hidden' in col2 &&
                col2.hidden
        })()).toBeTruthy()
    })

    test('valid hide function for multiple columns', () => {
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
            .hide(arg2.alias, arg.alias)
            .reportFormData({
                returnData: true
            })
        let { columns, functions } = result
        let [col1, col2] = columns
        expect((() => {
            return columns.length == 2 &&
                col2.alias === arg2.alias &&
                col1.alias === arg.alias &&
                'hidden' in col2 &&
                'hidden' in col1 &&
                col2.hidden &&
                col1.hidden 
        })()).toBeTruthy()
    })

    test('valid hide and include in resultset multiple columns', () => {
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
            .hideAndIncludeInResultSet(arg2.alias, arg.alias)
            .reportFormData({
                returnData: true
            })
        let { columns, functions } = result
        let [col1, col2] = columns
        expect((() => {
            return columns.length == 2 &&
                col2.alias === arg2.alias &&
                col1.alias === arg.alias &&
                'hidden' in col2 &&
                'hidden' in col1 &&
                col2.hidden &&
                col1.hidden &&
                'includeInResultset' in col2 &&
                'includeInResultset' in col1 &&
                col2.includeInResultset &&
                col1.includeInResultset
        })()).toBeTruthy()
    })

    test('valid hide and include in resultset first columns', () => {
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
            .hideAndIncludeInResultSet(arg.alias)
            .reportFormData({
                returnData: true
            })
        let { columns, functions } = result
        let [col1, col2] = columns
        expect((() => {
            return columns.length == 2 &&
                col2.alias === arg2.alias &&
                col1.alias === arg.alias &&
                !('includeInResultset' in col2 ) &&
                'includeInResultset' in col1 &&
                !col2.includeInResultset &&
                col1.includeInResultset
        })()).toBeTruthy()
    })

    test('valid hide and include in resultset first columns', () => {
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
            .hideAndIncludeInResultSet(arg.alias)
            .reportFormData({
                returnData: true
            })
        let { columns, functions } = result
        let [col1, col2] = columns
        expect((() => {
            return columns.length == 2 &&
                col2.alias === arg2.alias &&
                col1.alias === arg.alias &&
                !('includeInResultset' in col2) &&
                'includeInResultset' in col1 &&
                !col2.includeInResultset &&
                col1.includeInResultset &&
                col1.hidden 
        })()).toBeTruthy()
    })

    test(' combining HIDE and valid HIDE AND INCLUDE IN RESULTSET first columns', () => {
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
            .hideAndIncludeInResultSet(arg.alias, arg2.alias)
            .reportFormData({
                returnData: true
            })
        let { columns, functions } = result
        let [col1, col2] = columns
        expect((() => {
            return columns.length == 2
             &&
                col2.alias === arg2.alias &&
                col1.alias === arg.alias 
                &&
                ('includeInResultset' in col2)
                 &&
                'includeInResultset' in col1 &&
                col2.includeInResultset &&
                col1.includeInResultset &&
                col1.hidden && col2.hidden
        })()).toBeTruthy()
    })
})

describe('testing ANALYTICS', () => {
    //WROTE ONLY ONE TESTCASE FOR NOW. WILL UPDATE LATER
    test('should add object of formdata', () => {
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
            .analytics('test data')
            .reportFormData({
                returnData: true
            })
        let { columns, functions, analytics } = result
        let [col1, col2] = columns
        expect((() => {
            return 'analytics' in result &&
            analytics === 'test data'
        })()).toBeTruthy()
    })
    
})
