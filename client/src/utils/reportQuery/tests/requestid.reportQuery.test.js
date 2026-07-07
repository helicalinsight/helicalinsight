import ReportQuery from '../index'

const getQuery = () => (new ReportQuery({
    "location": "1639026125991",
    "metadataFileName": "59ced249-8d96-4175-aa88-75a17b200b5c.metadata",
}))

describe('testing REQUESTID', () => {

    test('checking request ID without passing requestID', () => {
        let query = getQuery()
        let arg4 = {
            column: 'HIUSER.travel_details.travel_date',
            alias: 'travel_date'
        }
        let result = query.select(arg4.column, arg4.alias)
            .reportFormData({
                returnData: true
            })
        expect((() => {
            return result &&
                result.requestId
        })()).toBeTruthy()
    })

    test('checking request ID with passing valid requestID', () => {
        let query = getQuery()
        let arg4 = {
            column: 'HIUSER.travel_details.travel_date',
            alias: 'travel_date'
        }
        let result = query.select(arg4.column, arg4.alias)
            .requestId('test__requestId')
            .reportFormData({
                returnData: true
            })
        expect((() => {
            return result &&
                result.requestId === 'test__requestId'
        })()).toBeTruthy()
    })

    test('checking request ID with passing invalid requestID', () => {
        let query = getQuery()
        let arg4 = {
            column: 'HIUSER.travel_details.travel_date',
            alias: 'travel_date'
        }
        let result = query.select(arg4.column, arg4.alias)
            .requestId([])
            .reportFormData({
                returnData: true
            })
        expect((() => {
            return result &&
                typeof result.requestId === 'string'
        })()).toBeTruthy()
    })


})
