import util from '../utils'
let { handleSelectPayload, checkIfObject } = util
import ReportQuery from '../index'
let payload = [
    {
        column: 'booking_platform',
        alias: 'bkng_plfm'
    },
    {
        column: 'destination',
        alias: 'dest'
    }
]
describe("test hanlde select payload ", () => {
    test('passing a empty array []', () => {
        let self = {}
        let result = handleSelectPayload([], self)
        expect(result.columns).toStrictEqual([])
    })
    test('passing a empty array [] with an empty obj', () => {
        let self = {}
        let result = handleSelectPayload([{}], self)
        expect(result.columns).toStrictEqual([])
    })
    test('passing a empty array [] with an empty obj', () => {
        let self = {}
        let result = handleSelectPayload([{}], self)
        expect(result.columns).toStrictEqual([])
    })
    test('passing array with single obj with col and no alias', () => {
        let self = {}
        let payload = [{
            column: 'destination'
        }]
        let result = handleSelectPayload(payload, self)
        expect((() => {
            let { columns } = result
            if (!columns) return false
            if (columns.length != 1) return false
            if (!checkIfObject(columns[0])) return false
            if (columns[0].column != payload[0].column) return false
            if (columns[0].column != columns[0].alias) return false
            return true
        })()).toBe(true)
    })
    test('passing array with single obj with col and alias', () => {
        let self = {}
        let payload = [{
            column: 'destination',
            alias : 'dest'
        }]
        let result = handleSelectPayload(payload, self)
        expect((() => {
            let { columns } = result
            if (!columns) return false
            if (columns.length != 1) return false
            if (!checkIfObject(columns[0])) return false
            if (columns[0].column != payload[0].column) return false
            if (columns[0].column == payload[0].alias) return false
            return true
        })()).toBe(true)
    })
    test('passing array with single obj with alias and without col', () => {
        let self = {}
        let payload = [{
            alias: 'dest'
        }]
        let result = handleSelectPayload(payload, self)
        //this will return empty olumns array like {columns : []}
        expect((() => {
            let { columns } = result
            if (!columns) return false
            if (columns.length != 0) return false
            return true
        })()).toBe(true)
    })
    test('passing array with single obj with column as datatype other that string', () => {
        let self = {}
        let payload = [{
            column : 67,
            alias: 'dest'
        }]
        let result = handleSelectPayload(payload, self)
        //this will return empty olumns array like {columns : []}
        expect((() => {
            let { columns } = result
            if (!columns) return false
            if (columns.length != 0) return false
            return true
        })()).toBe(true)
    })
    // select(colname, alias)
    test('pass empty colname and alias name', ()=>{
        let dataSource = {
            "location": "1463377807724/1463377836985",
            "metadataFileName": "e9be6771-995b-40eb-a01c-304857a100a1.metadata"
        }
        let query = new ReportQuery(dataSource)
        query.select('', '')
        expect(query.getData('columns')).toStrictEqual([])
    })
    test('pass dont pass any arggs', () => {
        let dataSource = {
            "location": "1463377807724/1463377836985",
            "metadataFileName": "e9be6771-995b-40eb-a01c-304857a100a1.metadata"
        }
        let query = new ReportQuery(dataSource)
        query.select('', '')
        expect(query.getData('columns')).toStrictEqual([])
    })
})