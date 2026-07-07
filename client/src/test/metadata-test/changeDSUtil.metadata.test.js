const { changeDatasourceUtil } = require("../../components/hi-metadata/utils/changeDatasourceUtil")
const { groovyConn, derbyConn } = require("./changeDSUtil.mock.data")

describe('Testing change datasource utility', () => {
    test('changing datasource to a groovy managed connection', () => {
        let result = changeDatasourceUtil(groovyConn)
        expect(result.length).toBe(1)
        expect(result[0].id.toString()).toBe('201')
        expect(result[0].driverType.toLowerCase()).toBe('dynamicswitch')
    })

    test('changing datasource to a DERBY connection', () => {
        let result = changeDatasourceUtil(derbyConn)
        expect(result.length).toBe(1)
        expect(result[0].id.toString()).toBe('1')
        expect(result[0].driverType.toLowerCase()).toBe('derby')
    })
})