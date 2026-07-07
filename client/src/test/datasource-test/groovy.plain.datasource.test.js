const { getCreateEditFD } = require("../../components/hi-datasources/utils/getCreateEditFD")
const { getDeleteFD } = require("../../components/hi-datasources/utils/getDeleteFD")
const { getQuickTestFD } = require("../../components/hi-datasources/utils/getQuickTestFD")
const { testGP, testGPResult, quickTestResult, quickTest, deleteConnSimple, deleteConnCascade } = require("./mock-data/groovy-plain.mock.data")


jest.spyOn(global.console, 'log')
jest.spyOn(global.console, 'warn')

const checkObjets = (first, second) => {
    let result = true
    Object.keys(second).map(key => {
        if (result && (second[key] !== first[key])) (result = false)
    })
    return result
}

describe('Testing  GROOVY PLAIN JDBC CONNECTIONS', () => {
    test('checking TEST CONNECTION  for groovy plain jdbc connection', () => {
        let formData = getCreateEditFD(testGP)
        let result = testGPResult
        expect(checkObjets(formData, result)).toBeTruthy()
    })

    test('checking CREATE CONNECTION  for groovy plain jdbc connection', () => {
        let formData = getCreateEditFD(testGP)
        let result = {
            "classifier": "efwd",
            "condition": "import net.sf.json.JSONObject;\n    import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n    public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        responseJson.put(\"driver\", \"com.mysql.jdbc.Driver\");\n        responseJson.put(\"url\", \"jdbc:mysql://localhost:3306/\" + userName);\n        responseJson.put(\"user\", \"root\");\n        responseJson.put(\"pass\", \"root\");\n        return responseJson;\n    }",
            "driverName": "org.postgresql.Driver",
            "name": "booking",
            "userName": "zlvkhftkogyvevhyxvalzgvu@psql-mock-database-cloud",
            "password": "srnlevoznktjiwoutcsbhgqr",
            "database": "booking1662345944710kenxhxumioueuzfj",
            "jdbcUrl": "jdbc:postgresql://psql-mock-database-cloud.postgres.database.azure.com:5432/booking1662345944710kenxhxumioueuzfj",
            "dataSourceType": "Groovy Plain Jdbc DataSource",
            "directory": "Gagan",
            "type": "sql.jdbc.groovy"
        }
        expect(checkObjets(formData, result)).toBeTruthy()
    })

    test('checking QUICK TEST  for groovy plain jdbc', () => {
        let formData = getQuickTestFD(quickTest)
        expect(checkObjets(formData, quickTestResult)).toBeTruthy()
    })

    test('checking DELETE CONNECTION SIMPLE  groovy plain connection', () => {
        let formData = getDeleteFD(deleteConnSimple)
        expect(checkObjets(formData, {
            "classifier": "efwd",
            "id": "304",
            "type": "simple",
            "directory": "Gagan",
            "driver": "org.postgresql.Driver"
        })).toBeTruthy()
    })

    test('checking DELETE CONNECTION CASCADE  groovy plain connection', () => {
        let formData = getDeleteFD(deleteConnCascade)
        expect(checkObjets(formData, {
            "classifier": "efwd",
            "id": "304",
            "type": "cascade",
            "directory": "Gagan",
            "driver": "org.postgresql.Driver"
        })).toBeTruthy()
    })
})