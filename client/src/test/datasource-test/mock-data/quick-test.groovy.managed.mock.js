export const quickTestGroovyManaged = {
    "permissionLevel": 5,
    "driver": null,
    "data": {
        "dir": "Gagan",
        "driver": null,
        "type": "sql.jdbc.groovy.managed",
        "id": 201,
        "username": null,
        "password": null,
        "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
        "url": null
    },
    "name": "groovy man",
    "classifier": "efwd",
    "type": "sql.jdbc.groovy.managed",
    "dataSourceType": "Plain Groovy Managed DataSource"
}