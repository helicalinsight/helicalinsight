export const groovyConn = {
    "permissionLevel": 5,
    "driver": "dynamicSwitch",
    "data": {
        "dir": "Gagan",
        "driver": "dynamicSwitch",
        "type": "sql.jdbc.groovy.managed",
        "id": 303,
        "username": null,
        "password": null,
        "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
        "url": null
    },
    "name": "new conn",
    "classifier": "efwd",
    "type": "sql.jdbc.groovy.managed",
    "dataSourceType": "Plain Groovy Managed DataSource",
    "category": "dataSource",
    "children": [],
    "driverType": "DynamicSwitch",
    "keyPath": "by1i-vzal-sb9s-z39c-ge/3veq-wjv1-341d-cob7-bd",
    "key": "3veq-wjv1-341d-cob7-bd",
    "uuid": "3veq-wjv1-341d-cob7-bd"
}

export const groovyConnResult = {
    "id": 303,
    "type": "sql.jdbc.groovy.managed",
    "dir": "Gagan",
    "parameters": {
        "fetchCatalogs": true,
        "fetchSchemas": true,
        "view": "tree",
        "skipped": true
    }
}

export const derbyConn = {
    "data": {
        "id": "1",
        "type": "dynamicDataSource"
    },
    "dataSourceProvider": "tomcat",
    "type": "dynamicDataSource",
    "permissionLevel": 5,
    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
    "name": "SampleTravelDataDerby",
    "classifier": "global",
    "dataSourceType": "Managed DataSource",
    "category": "dataSource",
    "children": [],
    "driverType": "Derby",
    "keyPath": "dv06-skhc-7exg-ag6u-mx/dpl7-qebs-dml4-mkqt-vt",
    "key": "dpl7-qebs-dml4-mkqt-vt",
    "uuid": "dpl7-qebs-dml4-mkqt-vt"
}

export const derbyConnResult = {
    "id": "1",
    "type": "dynamicDataSource",
    "parameters": {
        "fetchCatalogs": true,
        "fetchSchemas": true,
        "view": "tree",
        "skipped": true
    }
}