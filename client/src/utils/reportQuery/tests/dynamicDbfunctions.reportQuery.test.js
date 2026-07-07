import ReportQuery from '../index'

const getQuery_withoutDB = () => (new ReportQuery({
    "location": "1639026125991",
    "metadataFileName": "59ced249-8d96-4175-aa88-75a17b200b5c.metadata",
}))

const getQuery_withDB = () => (new ReportQuery({
    "location": "1639026125991",
    "metadataFileName": "59ced249-8d96-4175-aa88-75a17b200b5c.metadata",
    "useDBFuntion": {
        test: [{
            "key": "sql.date.makedate",
            "description": "Returns a date for given year, month and day. Example: date(char('2019',4)||'-'||char('11',2)||'-'||char('23',2)) result : 2019-17-23",
            "value": "__test_fn",
            "signature": "date(${year}||'-'||${month}||'-'||${day})",
            "returns": "date",
            "parameters": [
                {
                    "name": "year",
                    "defaultValue": "'2013'"
                },
                {
                    "name": "month",
                    "defaultValue": "'7'"
                },
                {
                    "name": "day",
                    "defaultValue": "'15'"
                }
            ]
        }]
    }
}))

describe('testing default/dynamic db functions', () => {

    test('checking db function that should not be present with the default databsefunctions', () => {
        let query = getQuery_withoutDB()
        expect((() => {
            return !query.__test_fn    
        })()).toBeTruthy()
    })

    test('checking dbfunction by confguring database functions', () => {
        let query = getQuery_withDB()
        expect((() => {
            return query.sql_date_makedate && typeof query.sql_date_makedate === 'function'
        })()).toBeTruthy()
        // expect((() => {
        //     return query.__test_fn && typeof query.__test_fn === 'function'
        // })()).toBeTruthy()
    })
})
