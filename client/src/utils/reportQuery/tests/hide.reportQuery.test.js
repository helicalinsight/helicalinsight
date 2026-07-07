import ReportQuery from '../index'

const getQuery = () => (new ReportQuery({
    "location": "1639026125991",
    "metadataFileName": "59ced249-8d96-4175-aa88-75a17b200b5c.metadata",
}))

describe('testing reportquery hide functionlality', () => {

    test('adding hide to a column : 5207', () => {
        let query = getQuery()
        let arg3 = {
            column: 'HIUSER.travel_details.source_id',
            alias: 'source_id'
        }
        let arg4 = {
            column: 'HIUSER.travel_details.travel_date',
            alias: 'travel_date'
        }
        let result = query.select(arg4.column, arg4.alias)
            .select(arg3.column, arg3.alias)
            .hide(arg4.alias)
            .reportFormData({
                returnData: true
            })
        expect((() => {
            return result
                && result.columns.length === 2
                && result.columns[0].hidden
                && !result.columns[1].hidden
        })()).toBeTruthy()
    })

    test('adding hide to a column which is having groupby: 5207', () => {
        let query = getQuery()
        let arg3 = {
            column: 'HIUSER.travel_details.source_id',
            alias: 'source_id'
        }
        let arg4 = {
            column: 'HIUSER.travel_details.travel_date',
            alias: 'travel_date'
        }
        let result = query.select(arg4.column, arg4.alias)
            .select(arg3.column, arg3.alias)
            .groupBy(arg4.alias)
            .hide(arg4.alias)
            .reportFormData({
                returnData: true
            })
        expect((() => {
            return result
                && result.columns.length === 2
                && result.columns[0].hidden
                && !result.columns[1].hidden
                && !result.functions.groupBy
        })()).toBeTruthy()
    })
})
