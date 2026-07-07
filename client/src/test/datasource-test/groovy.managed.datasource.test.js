const { getCreateEditFD } = require("../../components/hi-datasources/utils/getCreateEditFD")
const { getDeleteFD } = require("../../components/hi-datasources/utils/getDeleteFD")
const { getQuickTestFD } = require("../../components/hi-datasources/utils/getQuickTestFD")
const { createConnMockData } = require("./create-connection.groovy.managed.mock.data")
const { deleteConn, deleteCascadeConn } = require("./mock-data/delete-conn.groovy.mock")
const { quickTestGroovyManaged } = require("./mock-data/quick-test.groovy.managed.mock")
const { testConnGroovyManaged, updateConnGroovyManaged } = require("./mock-data/test-conn.groovy.datasource.mock")

jest.spyOn(global.console, 'log')
jest.spyOn(global.console, 'warn')
describe('Groovy connection test cases', () => {

    test('checking CREATING groovy managed connection', async () => {
        let formData = getCreateEditFD(createConnMockData)
        expect(formData.type).toBe('sql.jdbc.groovy.managed')
        expect(formData.directory).toBe('Gagan')
        expect(formData.name).toBe('new conn')
    })

    test('checking to test (NORMAL) a groovy managed connection', async () => {
        let formData = getCreateEditFD(testConnGroovyManaged)
        expect(formData.id).toBe(201)
        expect(formData.type).toBe('sql.jdbc.groovy.managed')
        expect(formData.classifier).toBe('efwd')
        expect(formData.name).toBe('groovy man')
        expect(formData.condition.includes("GroovyUsersSession.getValue('${user}.name')")).toBeTruthy()
    })

    test('checking updating a groovy managed connection', async () => {
        let formData = getCreateEditFD(updateConnGroovyManaged)
        expect(formData.id).toBe(201)
        expect(formData.type).toBe('sql.jdbc.groovy.managed')
        expect(formData.classifier).toBe('efwd')
        expect(formData.name).toBe('groovy man updated name')
        expect(formData.condition.includes("GroovyUsersSession.getValue('${user}.name')")).toBeTruthy()
    })

    test('checking QUICK_TEST a groovy managed connection', async () => {
        let formData = getQuickTestFD(quickTestGroovyManaged)
        expect(formData.id).toBe(201)
        expect(formData.type).toBe('sql.jdbc.groovy.managed')
        expect(formData.classifier).toBe('efwd')
    })

    test('checking SIMPLE DELETING a groovy managed connection', async () => {
        let formData = getDeleteFD(deleteConn)
        expect(formData.id).toBe('301')
        expect(formData.type).toBe('simple')
        expect(formData.directory).toBe('Gagan')
    })

    test('checking CASCADE DELETING a groovy managed connection', async () => {
        let formData = getDeleteFD(deleteCascadeConn)
        expect(formData.type).toBe('cascade')
        expect(formData.directory).toBe('Gagan')
    })
})

