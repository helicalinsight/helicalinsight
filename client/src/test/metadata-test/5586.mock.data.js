export const data = {
    "data": {
        "allDataSources": [
            {
                "permissionLevel": 5,
                "driver": "com.mysql.jdbc.Driver",
                "data": {
                    "dir": "TestFold",
                    "driverName": "com.mysql.jdbc.Driver",
                    "type": "sql.jdbc.groovy",
                    "id": 1,
                    "userName": "hiuser",
                    "password": "sdfsfsdf",
                    "condition": "import net.sf.json.JSONObject;\n    import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n    public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        responseJson.put(\"driver\", \"com.mysql.jdbc.Driver\");\n        responseJson.put(\"url\", \"jdbc:mysql://localhost:3306/\" + userName);\n        responseJson.put(\"user\", \"root\");\n        responseJson.put(\"pass\", \"root\");\n        return responseJson;\n    }",
                    "jdbcUrl": "jdbc:mysql://127.0.0.1:3306/bugzilla"
                },
                "name": "bugzill",
                "classifier": "efwd",
                "type": "sql.jdbc.groovy",
                "dataSourceType": "Plain Groovy DataSource"
            },
            {
                "permissionLevel": 5,
                "driver": "org.postgresql.Driver",
                "data": {
                    "dir": "Gagan",
                    "driverName": "org.postgresql.Driver",
                    "type": "sql.jdbc.groovy",
                    "id": 304,
                    "userName": "zlvkhftkogyvevhyxvalzgvu@psql-mock-database-cloud",
                    "password": "zlvkhftkogyvevhyxvalzgvu@psql-mock-database-cloud",
                    "condition": "import net.sf.json.JSONObject;\n    import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n    public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        responseJson.put(\"driver\", \"com.mysql.jdbc.Driver\");\n        responseJson.put(\"url\", \"jdbc:mysql://localhost:3306/\" + userName);\n        responseJson.put(\"user\", \"root\");\n        responseJson.put(\"pass\", \"root\");\n        return responseJson;\n    }",
                    "jdbcUrl": "jdbc:postgresql://psql-mock-database-cloud.postgres.database.azure.com:5432/booking1662345944710kenxhxumioueuzfj"
                },
                "name": "booking",
                "classifier": "efwd",
                "type": "sql.jdbc.groovy",
                "dataSourceType": "Plain Groovy DataSource"
            },
            {
                "permissionLevel": 5,
                "driver": "dynamicSwitch",
                "data": {
                    "dir": "Gagan",
                    "driverName": null,
                    "type": "sql.jdbc.groovy.managed",
                    "id": 303,
                    "userName": null,
                    "password": null,
                    "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                    "jdbcUrl": null,
                    "driver": "dynamicSwitch"
                },
                "name": "new conn",
                "classifier": "efwd",
                "type": "sql.jdbc.groovy.managed",
                "dataSourceType": "Plain Groovy Managed DataSource"
            },
            {
                "permissionLevel": 5,
                "driver": "dynamicSwitch",
                "data": {
                    "dir": "Gagan",
                    "driverName": null,
                    "type": "sql.jdbc.groovy.managed",
                    "id": 201,
                    "userName": null,
                    "password": null,
                    "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                    "jdbcUrl": null,
                    "driver": "dynamicSwitch"
                },
                "name": "groovy man",
                "classifier": "efwd",
                "type": "sql.jdbc.groovy.managed",
                "dataSourceType": "Plain Groovy Managed DataSource"
            },
            {
                "permissionLevel": 5,
                "driver": "dynamicSwitch",
                "data": {
                    "dir": "Gagan",
                    "driverName": null,
                    "type": "sql.jdbc.groovy.managed",
                    "id": 401,
                    "userName": null,
                    "password": null,
                    "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                    "jdbcUrl": null,
                    "driver": "dynamicSwitch"
                },
                "name": "new con1",
                "classifier": "efwd",
                "type": "sql.jdbc.groovy.managed",
                "dataSourceType": "Plain Groovy Managed DataSource"
            },
            {
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
                "dataSourceType": "Managed DataSource"
            }
        ],
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
        "dataSourceTypes": [
            {
                "type": "sql.jdbc.groovy.managed",
                "name": "Groovy Managed Jdbc DataSource",
                "classifier": "efwd",
                "categoryName": "advanced",
                "categoryType": "advanced"
            },
            {
                "type": "sql.jdbc.groovy",
                "name": "Groovy Plain Jdbc DataSource",
                "classifier": "efwd",
                "categoryName": "advanced",
                "categoryType": "advanced"
            },
            {
                "type": "global.jdbc",
                "name": "Managed DataSource",
                "classifier": "global",
                "categoryName": "advanced",
                "categoryType": "advanced"
            },
            {
                "type": "sql.jdbc",
                "name": "Plain Jdbc DataSource",
                "classifier": "efwd",
                "categoryName": "advanced",
                "categoryType": "advanced"
            }
        ],
        "dataSources": [
            {
                "name": "Access",
                "categoryType": "supported",
                "categoryName": "Supported"
            },
            {
                "name": "Amazon Dynamodb",
                "categoryType": "supported",
                "categoryName": "Supported"
            },
            {
                "name": "Amazon Redshift",
                "categoryType": "supported",
                "categoryName": "Supported"
            },
            {
                "driver": "org.apache.drill.jdbc.Driver",
                "databaseDialect": "drill",
                "enabledTypes": false,
                "name": "Apache Drill",
                "categoryName": "Big Data",
                "categoryType": "big_data",
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png",
                "url": "jdbc:drill:{{hostName}}:{{port}}",
                "parameters": {
                    "port": "31010",
                    "hostName": "drillbit=localhost"
                }
            },
            {
                "driver": "com.simba.athena.jdbc.Driver",
                "databaseDialect": "athena",
                "name": "Athena",
                "categoryName": "RDBMS",
                "categoryType": "rdbms",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png",
                "url": "jdbc:awsathena://{{hostName}}",
                "parameters": {
                    "hostName": "localhost"
                }
            },
            {
                "driver": "ru.yandex.clickhouse.ClickHouseDriver",
                "databaseDialect": "clickhouse",
                "name": "Clickhouse",
                "categoryName": "RDBMS",
                "categoryType": "rdbms",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png",
                "url": "jdbc:clickhouse://{{hostName}}:{{port}}/{{database}}",
                "parameters": {
                    "port": "8123",
                    "hostName": "localhost",
                    "database": "database"
                }
            },
            {
                "driver": "com.databricks.client.jdbc.Driver",
                "databaseDialect": "databricks",
                "name": "Databricks Client",
                "categoryName": "RDBMS",
                "categoryType": "rdbms",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png",
                "url": "jdbc:databricks://{{hostName}}:{{port}};{{database}}",
                "parameters": {
                    "port": "443",
                    "hostName": "localhost",
                    "database": "database"
                }
            },
            {
                "driver": "org.apache.derby.jdbc.ClientDriver",
                "databaseDialect": "derby",
                "name": "Derby",
                "categoryName": "RDBMS",
                "categoryType": "rdbms",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png",
                "url": "jdbc:derby://{{hostName}}:{{port}}/{{database}}",
                "parameters": {
                    "port": "1527",
                    "hostName": "localhost",
                    "database": "database"
                }
            },
            {
                "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                "databaseDialect": "derby",
                "name": "Derby",
                "categoryName": "RDBMS",
                "categoryType": "rdbms",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png",
                "url": "jdbc:derby:{{database}}",
                "parameters": {
                    "database": "database"
                }
            },
            {
                "name": "Dremio",
                "categoryType": "supported",
                "categoryName": "Supported"
            },
            {
                "driver": "dynamicSwitch",
                "databaseDialect": "",
                "name": "DynamicSwitch",
                "categoryName": "RDBMS",
                "categoryType": "rdbms",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png"
            },
            {
                "driver": "org.elasticsearch.xpack.sql.jdbc.EsDriver",
                "databaseDialect": "",
                "name": "Elasticsearch",
                "categoryName": "RDBMS",
                "categoryType": "rdbms",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png"
            },
            {
                "name": "Google Bigquery",
                "categoryType": "supported",
                "categoryName": "Supported"
            },
            {
                "driver": "com.helical.mongodb.MongoJdbcDriver",
                "databaseDialect": "himongo",
                "name": "Helical Mongodb",
                "categoryName": "RDBMS",
                "categoryType": "rdbms",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png"
            },
            {
                "driver": "org.apache.hive.jdbc.HiveDriver",
                "databaseDialect": "spark",
                "name": "Hive",
                "categoryName": "Big Data",
                "categoryType": "big_data",
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png",
                "url": "jdbc:hive2://{{hostName}}:{{port}}/{{database}}",
                "parameters": {
                    "port": "10001",
                    "hostName": "localhost",
                    "database": "database"
                }
            },
            {
                "driver": "com.ibm.db2.jcc.DB2Driver",
                "databaseDialect": "db2",
                "name": "IBM Db2",
                "categoryName": "RDBMS",
                "categoryType": "rdbms",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png",
                "url": "jdbc:db2://{{hostName}}:{{port}}/{{database}}",
                "parameters": {
                    "port": "50000",
                    "hostName": "localhost",
                    "database": "database"
                }
            },
            {
                "driver": "com.informix.jdbc.IfxDriver",
                "databaseDialect": "informix",
                "name": "Informix",
                "categoryName": "RDBMS",
                "categoryType": "rdbms",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png",
                "url": "jdbc:informix-sqli://{{hostName}}:{{port}}/{{database}}:delimident=y",
                "parameters": {
                    "port": "1526",
                    "hostName": "localhost",
                    "database": "database"
                }
            },
            {
                "driver": "org.mariadb.jdbc.Driver",
                "databaseDialect": "mysql",
                "name": "Mariadb",
                "categoryName": "RDBMS",
                "categoryType": "rdbms",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png",
                "url": "jdbc:mariadb://{{hostName}}:{{port}}/{{database}}",
                "parameters": {
                    "port": "3306",
                    "hostName": "localhost",
                    "database": "database"
                }
            },
            {
                "name": "Microsoft Sqlserver",
                "categoryType": "supported",
                "categoryName": "Supported"
            },
            {
                "driver": "net.sourceforge.jtds.jdbc.Driver",
                "databaseDialect": "sqlserver",
                "name": "Microsoft Sqlserver(sourceforge)",
                "categoryName": "RDBMS",
                "categoryType": "rdbms",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png",
                "url": "jdbc:jtds:sqlserver://{{hostName}}:{{port}}/{{database}}",
                "parameters": {
                    "port": "1433",
                    "hostName": "localhost",
                    "database": "database"
                }
            },
            {
                "driver": "com.mysql.jdbc.Driver",
                "databaseDialect": "mysql",
                "name": "Mysql",
                "categoryName": "RDBMS",
                "categoryType": "rdbms",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png",
                "url": "jdbc:mysql://{{hostName}}:{{port}}/{{database}}",
                "parameters": {
                    "port": "3306",
                    "hostName": "localhost",
                    "database": "database"
                }
            },
            {
                "driver": "com.mysql.cj.jdbc.Driver",
                "databaseDialect": "mysql",
                "name": "Mysql Cj",
                "categoryName": "RDBMS",
                "categoryType": "rdbms",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png",
                "url": "jdbc:mysql://{{hostName}}:{{port}}/{{database}}",
                "parameters": {
                    "port": "3306",
                    "hostName": "localhost",
                    "database": "database"
                }
            },
            {
                "name": "Oracle",
                "categoryType": "supported",
                "categoryName": "Supported"
            },
            {
                "driver": "org.postgresql.Driver",
                "databaseDialect": "postgresql",
                "name": "Postgresql",
                "categoryName": "RDBMS",
                "categoryType": "rdbms",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png",
                "url": "jdbc:postgresql://{{hostName}}:{{port}}/{{database}}",
                "parameters": {
                    "port": "5432",
                    "hostName": "localhost",
                    "database": "database"
                }
            },
            {
                "driver": "com.facebook.presto.jdbc.PrestoDriver",
                "databaseDialect": "presto",
                "name": "Presto",
                "categoryName": "RDBMS",
                "categoryType": "rdbms",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png",
                "url": "jdbc:presto://{{hostName}}:{{port}}",
                "parameters": {
                    "port": "8090",
                    "hostName": "localhost"
                }
            },
            {
                "driver": "org.sqlite.JDBC",
                "databaseDialect": "sqlite",
                "name": "Sqlite",
                "categoryName": "RDBMS",
                "categoryType": "rdbms",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png",
                "url": "jdbc:sqlite:{{database}}",
                "parameters": {
                    "database": "database"
                }
            },
            {
                "name": "Teradata",
                "categoryType": "supported",
                "categoryName": "Supported"
            },
            {
                "name": "Ξ Add Driver Ξ",
                "categoryType": "supported",
                "categoryName": "Supported"
            }
        ]
    },
    "mode": "create",
    "store": {
        "liftedStore": {}
    },
    "location": false,
    "uuid": false,
    "activeDataSource": false,
    "returnDsToRender": false
}