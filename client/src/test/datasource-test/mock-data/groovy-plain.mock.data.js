export const testGP = {
    "buttonTypes": {
        "type": "test",
        "datasourceType": "efwd"
    },
    "values": {
        "driver": "org.postgresql.Driver",
        "hostName": "psql-mock-database-cloud.postgres.database.azure.com",
        "port": "5432",
        "databaseName": "booking1662345944710kenxhxumioueuzfj",
        "datasourceName": "booking",
        "username": "zlvkhftkogyvevhyxvalzgvu@psql-mock-database-cloud",
        "password": "srnlevoznktjiwoutcsbhgqr"
    },
    "clickedActiveDatabaseData": {
        "type": "sql.jdbc.groovy",
        "name": "Groovy Plain Jdbc DataSource",
        "classifier": "efwd",
        "categoryName": "advanced",
        "categoryType": "advanced"
    },
    "editorInput": "import net.sf.json.JSONObject;\n    import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n    public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        responseJson.put(\"driver\", \"com.mysql.jdbc.Driver\");\n        responseJson.put(\"url\", \"jdbc:mysql://localhost:3306/\" + userName);\n        responseJson.put(\"user\", \"root\");\n        responseJson.put(\"pass\", \"root\");\n        return responseJson;\n    }",
    "driverCategory": "RDBMS",
    "driver": "org.postgresql.Driver",
    "driversList": [
        {
            "driver": "dynamicSwitch",
            "showInDatasource": "false",
            "available": "true",
            "parameters": []
        },
        {
            "url": "jdbc:derby:{{database}}",
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "available": "true",
            "parameters": {
                "database": "database"
            }
        },
        {
            "url": "jdbc:mysql://{{hostName}}:{{port}}/{{database}}",
            "driver": "com.mysql.cj.jdbc.Driver",
            "available": "true",
            "parameters": {
                "port": "3306",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "url": "jdbc:mysql://{{hostName}}:{{port}}/{{database}}",
            "driver": "com.mysql.jdbc.Driver",
            "available": "true",
            "parameters": {
                "port": "3306",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "available": "true",
            "driver": "org.elasticsearch.xpack.sql.jdbc.EsDriver"
        },
        {
            "url": "jdbc:awsathena://{{hostName}}",
            "driver": "com.simba.athena.jdbc.Driver",
            "available": "true",
            "parameters": {
                "hostName": "localhost"
            }
        },
        {
            "url": "jdbc:jtds:sqlserver://{{hostName}}:{{port}}/{{database}}",
            "driver": "net.sourceforge.jtds.jdbc.Driver",
            "available": "true",
            "parameters": {
                "port": "1433",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "url": "jdbc:clickhouse://{{hostName}}:{{port}}/{{database}}",
            "driver": "ru.yandex.clickhouse.ClickHouseDriver",
            "available": "true",
            "parameters": {
                "port": "8123",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "available": "true",
            "driver": "com.helical.mongodb.MongoJdbcDriver"
        },
        {
            "url": "jdbc:hive2://{{hostName}}:{{port}}/{{database}}",
            "driver": "org.apache.hive.jdbc.HiveDriver",
            "available": "true",
            "parameters": {
                "port": "10001",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "url": "jdbc:drill:{{hostName}}:{{port}}",
            "driver": "org.apache.drill.jdbc.Driver",
            "available": "true",
            "parameters": {
                "port": "31010",
                "hostName": "drillbit=localhost"
            }
        },
        {
            "url": "jdbc:informix-sqli://{{hostName}}:{{port}}/{{database}}:delimident=y",
            "driver": "com.informix.jdbc.IfxDriver",
            "available": "true",
            "parameters": {
                "port": "1526",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "url": "jdbc:db2://{{hostName}}:{{port}}/{{database}}",
            "driver": "com.ibm.db2.jcc.DB2Driver",
            "available": "true",
            "parameters": {
                "port": "50000",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "url": "jdbc:mariadb://{{hostName}}:{{port}}/{{database}}",
            "driver": "org.mariadb.jdbc.Driver",
            "available": "true",
            "parameters": {
                "port": "3306",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "url": "jdbc:presto://{{hostName}}:{{port}}",
            "driver": "com.facebook.presto.jdbc.PrestoDriver",
            "available": "true",
            "parameters": {
                "port": "8090",
                "hostName": "localhost"
            }
        },
        {
            "url": "jdbc:databricks://{{hostName}}:{{port}};{{database}}",
            "driver": "com.databricks.client.jdbc.Driver",
            "available": "true",
            "parameters": {
                "port": "443",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "available": "true",
            "driver": "org.elasticsearch.xpack.sql.jdbc.EsDriver"
        },
        {
            "url": "jdbc:postgresql://{{hostName}}:{{port}}/{{database}}",
            "driver": "org.postgresql.Driver",
            "available": "true",
            "parameters": {
                "port": "5432",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "url": "jdbc:derby://{{hostName}}:{{port}}/{{database}}",
            "driver": "org.apache.derby.jdbc.ClientDriver",
            "available": "true",
            "parameters": {
                "port": "1527",
                "hostName": "localhost",
                "database": "database"
            }
        },
        {
            "url": "jdbc:sqlite:{{database}}",
            "driver": "org.sqlite.JDBC",
            "available": "true",
            "parameters": {
                "database": "database"
            }
        }
    ],
    "fileBrowserFolder": {
        "path": "Gagan"
    },
    "editData": {
        "permissionLevel": 5,
        "driver": "com.mysql.jdbc.Driver",
        "data": {
            "dir": "TestFold",
            "driver": "com.mysql.jdbc.Driver",
            "type": "sql.jdbc.groovy",
            "id": 1,
            "username": "hiuser",
            "password": "sdfsfsdf",
            "condition": "import net.sf.json.JSONObject;\n    import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n    public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        responseJson.put(\"driver\", \"com.mysql.jdbc.Driver\");\n        responseJson.put(\"url\", \"jdbc:mysql://localhost:3306/\" + userName);\n        responseJson.put(\"user\", \"root\");\n        responseJson.put(\"pass\", \"root\");\n        return responseJson;\n    }",
            "url": "jdbc:mysql://216.48.189.244:3306/bugzilla"
        },
        "name": "bugzill",
        "classifier": "efwd",
        "type": "sql.jdbc.groovy",
        "dataSourceType": "Plain Groovy DataSource"
    },
    "dataSourceProvider": "tomcat",
    "otherOptions": "",
    "editable": false
}

export const testGPResult = {
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

export const quickTest = {
    "id": "304",
    "dir": "Gagan",
    "type": "sql.jdbc.groovy",
    "driver": "org.postgresql.Driver",
    "permissionLevel": 5,
    "classifier": "efwd",
    "name": "booking",
    "data": {
        "id": "304",
        "type": "sql.jdbc.groovy",
        "dir": "Gagan"
    }
}

export const quickTestResult = {
    "id": "304",
    "type": "sql.jdbc.groovy",
    "classifier": "efwd",
    "dir": "Gagan"
}

export const deleteConnSimple = {
    "clickedRecordData": {
        "id": "304",
        "dir": "Gagan",
        "type": "sql.jdbc.groovy",
        "driver": "org.postgresql.Driver",
        "permissionLevel": 5,
        "classifier": "efwd",
        "name": "booking",
        "data": {
            "id": "304",
            "type": "sql.jdbc.groovy",
            "dir": "Gagan"
        }
    },
    "deleteType": "simple"
}

export const deleteConnCascade = {
    "clickedRecordData": {
        "id": "304",
        "dir": "Gagan",
        "type": "sql.jdbc.groovy",
        "driver": "org.postgresql.Driver",
        "permissionLevel": 5,
        "classifier": "efwd",
        "name": "booking",
        "data": {
            "id": "304",
            "type": "sql.jdbc.groovy",
            "dir": "Gagan"
        }
    },
    "deleteType": "cascade"
}