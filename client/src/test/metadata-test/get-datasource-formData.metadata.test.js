const { getDatasourceFD } = require("../../components/hi-metadata/utils/getDatasourceFD")
const { groovyConn, derbyConn } = require("./getDatasource.mock.data")

describe('Testing get datasource formdata', () => {
    test('testing formdata for groovy managed connection', () => {
        let formData = getDatasourceFD(groovyConn)
        expect(formData.id).toBe(303)
        expect(formData["type"]).toBe("sql.jdbc.groovy.managed")
        expect(formData["dir"]).toBe("Gagan")
        expect(formData["url"]).toBeFalsy()
        expect(formData["condition"]).toBeFalsy()
    })

    test('testing formdata for derby connection', () => {
        let formData = getDatasourceFD(derbyConn)
        expect(formData.id).toBe('1')
        expect(formData["type"]).toBe("dynamicDataSource")
        expect(formData["classifier"]).toBeFalsy()
        expect(formData["category"]).toBeFalsy()
    })
})