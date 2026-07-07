import { removeKeysFromDataSource } from "../../components/hi-metadata/utils";
const dummyDatasource = {
    "id": 1122,
    "catalog": "",
    "schema": "HIUSER",
    "type": "sql.jdbc.groovy.managed",
    "baseType": "global.jdbc",
    "dbId": "2321",
    "datasourceName": "GroovyManged",
    "connId": "2321",
    "joinsFetched": true,
    "database": "HIUSER",
    "dir": "GroovyManaged",
    "driverName": null,
    "userName": null,
    "password": null,
    "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n          responseJson.put(\"catalog\",\"\");\n\t\t  responseJson.put(\"schema\",\"HIUSER\");\n          responseJson.put(\"database\",\"HIUSER\")\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
    "jdbcUrl": null,
    "changed": true,
    "databaseType": "DynamicSwitch"
}
describe('Metadata 6121', () => {
	test('checking unwanted properties presence in metadata datasource', (done) => {
        const result = removeKeysFromDataSource(dummyDatasource);
        const unWantedKeys = ['driverType', 'dsKeyPath', 'originalSchema', 'originalCatalog', 'catSchemaPredicted', 'sync', 'classifier', 'connectionDatabaseId', 'driver', 'name', 'dataSource', 'joinsFetched', 'condition', 'userName', 'password', 'driverName', 'jdbcUrl']
        if(unWantedKeys.every(ele => !(ele in result))) {
            expect(1).toBeTruthy();
        } else {
            expect(1).toBeFalsy();
        }
		done();
	});
    test('checking wanted properties presence in metadata datasource', (done) => {
        const result = removeKeysFromDataSource(dummyDatasource);
        const wantedKeys = ["id",
		"type",
		"baseType",
		"catalog",
		"schema",
		"connId",
		"dbId",
		"datasourceName",
		"database",
		"databaseType"]
        if(wantedKeys.every(ele => (ele in result))) {
            expect(1).toBeTruthy();
        } else {
            expect(1).toBeFalsy();
        }
		done();
	});
});
