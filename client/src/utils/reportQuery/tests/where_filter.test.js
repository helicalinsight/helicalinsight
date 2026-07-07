import ReportQuery from '../index'

const getQuery = () => (new ReportQuery({
    "location": "1639026125991",
    "metadataFileName": "59ced249-8d96-4175-aa88-75a17b200b5c.metadata",
}))

describe('testing where which is not in select clause', () => {

    test('filter not in select clause with a dbfunction', () => {
        let query = getQuery()
        let arg4 = {
            column: 'HIUSER.travel_details.travel_date',
            alias: 'travel_date'
        }
        let result = query.select(arg4.column, arg4.alias).functionBuilder(builder => builder.year(arg4.alias), arg4.alias).where('HIUSER.travel_details.source_id', '=', ['1'], {
            databaseFunction: {
                "key": "sql.dateTime.quarter",
                "description": "Returns the quarter of the year for date/datetime. Example: quarter('2014-03-08 12:20:19') result:1",
                "value": "QUARTER",
                "signature": "(CASE MONTH(${datetime}) WHEN < 4 THEN 1 WHEN BETWEEN 4 AND 6 then 2 WHEN BETWEEN 7 AND 9 then 3 WHEN BETWEEN 10 AND 12 then 4 END)",
                "returns": "numeric",
                "parameters": [{
                    "name": "datetime",
                    "column": true,
                    "defaultValue": "'2014-03-08 12:20:19'",
                    "value": "travel_details.travel_date"
                }]
            }
        }).reportFormData({
            returnData: true
        })
        expect((() => {
            return result &&
                result.filters.length === 1 &&
                result.columns.length === 1 &&
                result.filters[0].column === 'HIUSER.travel_details.source_id' &&
                result.filters[0].databaseFunction.functionName.includes('quarter')
        })()).toBeTruthy()
    })

    test('filter not in select clause without a dbfunction', () => {
        let query = getQuery()
        let arg4 = {
            column: 'HIUSER.travel_details.travel_date',
            alias: 'travel_date'
        }
        let result = query.select(arg4.column, arg4.alias)
            .functionBuilder(builder => builder.year(arg4.alias), arg4.alias)
            .where('HIUSER.travel_details.source_id', '=', ['1']).reportFormData({
                returnData: true
            })
        expect((() => {
            return result &&
                result.filters.length === 1 &&
                result.columns.length === 1 &&
                result.filters[0].column === 'HIUSER.travel_details.source_id' &&
                result.filters[0] &&
                !result.filters[0].databaseFunction &&
                result.filters[0].condition === 'EQUALS'
        })()).toBeTruthy()
    })
})

describe('testing where condition with other database function :: #5080', () => {
    test('test cascadeFilters', () => {
        let arg4 = {
            column: 'HIUSER.travel_details.travel_date',
            alias: 'travel_date'
        }
        let query = getQuery()
        let result = query.select(arg4.column, arg4.alias)
            .functionBuilder(builder => builder.year(arg4.alias), arg4.alias)
            .where('travel_date', '=', ['2015'], {
                databaseFunction: {
                    "key": "sql.dateTime.quarter",
                    "description": "Returns the quarter of the year for date/datetime. Example: quarter('2014-03-08 12:20:19') result:1",
                    "value": "QUARTER",
                    "signature": "(CASE MONTH(${datetime}) WHEN < 4 THEN 1 WHEN BETWEEN 4 AND 6 then 2 WHEN BETWEEN 7 AND 9 then 3 WHEN BETWEEN 10 AND 12 then 4 END)",
                    "returns": "numeric",
                    "parameters": [{
                        "name": "datetime",
                        "column": true,
                        "defaultValue": "'2014-03-08 12:20:19'",
                        "value": "travel_details.travel_date"
                    }]
                }
            }).reportFormData({
                returnData: true
            })
        expect(
            result &&
            result.filters.length === 1 &&
            result.filters[0].databaseFunction.functionName.includes('quarter')
        ).toBe(true)
    })
})