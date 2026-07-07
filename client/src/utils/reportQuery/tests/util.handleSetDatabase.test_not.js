import util from '../utils'
let { handleSetDataBase } = util


describe("test hanlde set database ", () => {
    test('passing an empty string as database', () => {
        let self = {}
        let result = handleSetDataBase('', self)
        expect(result.database).toBe(null)
    })
    test('passing an integer as database', () => {
        let self = {}
        let payload = 3
        let result = handleSetDataBase('', self)
        expect(result.database).toBe(null)
    })
    test('passing HIUSER as database', () => {
        let self = {}
        let payload = 'HIUSER'
        let result = handleSetDataBase(payload, self)
        expect(result.database).toBe(payload)
    })
})