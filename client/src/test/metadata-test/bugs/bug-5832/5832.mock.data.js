export const mockReduxStoreData = {
    metadata: {
        "dataFetchedFor": {
            "getDatasource": true,
            "joins": true,
            "viewSessionVariables": false,
            "listDataSources": true,
            "k0ye-hj1a-oz4o-uri2-sv": false,
            "7mu2-0ddk-6yes-13rr-an": false
        },
        "loadingStatus": {
            "getDatasource": true,
            "listDataSources": true,
            "k0ye-hj1a-oz4o-uri2-sv": false,
            "7mu2-0ddk-6yes-13rr-an": false
        },
        "serviceErrorStatus": {},
        "fetchedDSInfo": {},
        "listDataSource": [],
        "supportedDataSourceTypes": [
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
                "name": "Dremio",
                "categoryType": "supported",
                "categoryName": "Supported"
            },
            {
                "name": "Google Bigquery",
                "categoryType": "supported",
                "categoryName": "Supported"
            },
            {
                "name": "IBM Db2",
                "categoryType": "supported",
                "categoryName": "Supported"
            },
            {
                "name": "Informix",
                "categoryType": "supported",
                "categoryName": "Supported"
            },
            {
                "name": "Microsoft Sqlserver",
                "categoryType": "supported",
                "categoryName": "Supported"
            },
            {
                "name": "Oracle",
                "categoryType": "supported",
                "categoryName": "Supported"
            },
            {
                "name": "Presto",
                "categoryType": "supported",
                "categoryName": "Supported"
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
        ],
        "allDataSourceTypes": [
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
                "name": "IBM Db2",
                "categoryType": "supported",
                "categoryName": "Supported"
            },
            {
                "name": "Informix",
                "categoryType": "supported",
                "categoryName": "Supported"
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
                "name": "Presto",
                "categoryType": "supported",
                "categoryName": "Supported"
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
        ],
        "metaDataSourceList": [
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
        "allDataSources": [
            {
                "permissionLevel": 5,
                "driver": "dynamicSwitch",
                "name": "test",
                "classifier": "efwd",
                "type": "sql.jdbc.groovy.managed",
                "data": {
                    "dir": "Gagan",
                    "driverName": null,
                    "type": "sql.jdbc.groovy.managed",
                    "id": 1,
                    "userName": null,
                    "password": null,
                    "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1000);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                    "jdbcUrl": null,
                    "driver": "dynamicSwitch"
                },
                "dataSourceType": ""
            },
            {
                "permissionLevel": 5,
                "driver": "dynamicSwitch",
                "name": "DifferentDbgroovyManged",
                "classifier": "efwd",
                "type": "sql.jdbc.groovy.managed",
                "data": {
                    "dir": "Groovy_Managed",
                    "driverName": null,
                    "type": "sql.jdbc.groovy.managed",
                    "id": 2,
                    "userName": null,
                    "password": null,
                    "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1000);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 1003);\n          responseJson.put(\"catalog\",\"SampleTravelData\");\n\t\t  responseJson.put(\"schema\",\"public\");\n          responseJson.put(\"database\",\"SampleTravelData.public\")\n      \n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 1006);\n           responseJson.put(\"catalog\",\"\");\n          responseJson.put(\"schema\",\"\");\n          responseJson.put(\"database\",\"\")\n\n          \n        }\n        if (userName.equals(\"helical\")) {\n          responseJson.put(\"globalId\", 1);\n          responseJson.put(\"catalog\",\"\");\n\t\t  responseJson.put(\"schema\",\"HIUSER\");\n          responseJson.put(\"database\",\"HIUSER\")\n     \n        }\n      \n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                    "jdbcUrl": null,
                    "driver": "dynamicSwitch"
                },
                "dataSourceType": ""
            },
            {
                "permissionLevel": 5,
                "driver": "com.mysql.jdbc.Driver",
                "name": "SampleGroovyPlain",
                "classifier": "efwd",
                "type": "sql.jdbc.groovy",
                "data": {
                    "dir": "Groovy_Plain",
                    "driverName": "com.mysql.jdbc.Driver",
                    "type": "sql.jdbc.groovy",
                    "id": 3,
                    "userName": "bugzilla",
                    "password": "bugzilla@QA",
                    "condition": "import net.sf.json.JSONObject;\n    import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n    public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        responseJson.put(\"driverName\", \"com.mysql.jdbc.Driver\");\n        responseJson.put(\"jdbcUrl\", \"jdbc:mysql://localhost:3306/SampleTravelData\");\n        responseJson.put(\"userName\", \"bugzilla\");\n        responseJson.put(\"password\", \"bugzilla@QA\");\n        return responseJson;\n    }",
                    "jdbcUrl": "jdbc:mysql://localhost:3306/S"
                },
                "dataSourceType": ""
            },
            {
                "data": {
                    "id": "1",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "org.apache.derby.jdbc.ClientDriver",
                "name": "SampleTravelDataDerby",
                "classifier": "global",
                "dataSourceType": "Managed DataSource"
            },
            {
                "data": {
                    "id": "1000",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "com.mysql.jdbc.Driver",
                "name": "MYSQLSampleDS",
                "classifier": "global",
                "dataSourceType": "Managed DataSource"
            },
            {
                "data": {
                    "id": "1001",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "com.mysql.jdbc.Driver",
                "name": "MYSQLSingleDumpDS",
                "classifier": "global",
                "dataSourceType": "Managed DataSource"
            },
            {
                "data": {
                    "id": "1002",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "com.mysql.jdbc.Driver",
                "name": "MYSQLMultiConnDumpDS",
                "classifier": "global",
                "dataSourceType": "Managed DataSource"
            },
            {
                "data": {
                    "id": "1003",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "org.postgresql.Driver",
                "name": "PostgresSampleDS",
                "classifier": "global",
                "dataSourceType": "Managed DataSource"
            },
            {
                "data": {
                    "id": "1004",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "org.postgresql.Driver",
                "name": "PostgresSingleDumpDS",
                "classifier": "global",
                "dataSourceType": "Managed DataSource"
            },
            {
                "data": {
                    "id": "1005",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "org.postgresql.Driver",
                "name": "PostgresMultiConnDumpDS",
                "classifier": "global",
                "dataSourceType": "Managed DataSource"
            },
            {
                "data": {
                    "id": "1006",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "org.sqlite.JDBC",
                "name": "SqliteSampleDS",
                "classifier": "global",
                "dataSourceType": "Managed DataSource"
            },
            {
                "data": {
                    "id": "1007",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "org.sqlite.JDBC",
                "name": "SqliteSingleDumpDS",
                "classifier": "global",
                "dataSourceType": "Managed DataSource"
            },
            {
                "data": {
                    "id": "1008",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "org.apache.drill.jdbc.Driver",
                "name": "ApacheDrillSampleDS",
                "classifier": "global",
                "dataSourceType": "Managed DataSource"
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
        "datasourceListToRender": [
            {
                "name": "Apache Drill",
                "children": [
                    {
                        "data": {
                            "id": "1008",
                            "type": "dynamicDataSource"
                        },
                        "dataSourceProvider": "tomcat",
                        "type": "dynamicDataSource",
                        "permissionLevel": 5,
                        "driver": "org.apache.drill.jdbc.Driver",
                        "name": "ApacheDrillSampleDS",
                        "classifier": "global",
                        "dataSourceType": "Managed DataSource",
                        "category": "dataSource",
                        "children": [],
                        "driverType": "Apache Drill",
                        "keyPath": "ochz-464g-iksn-rs03-an/5ib2-gpy1-v0ay-csub-2c",
                        "key": "5ib2-gpy1-v0ay-csub-2c",
                        "uuid": "5ib2-gpy1-v0ay-csub-2c"
                    }
                ],
                "key": "ochz-464g-iksn-rs03-an",
                "uuid": "ochz-464g-iksn-rs03-an",
                "keyPath": "ochz-464g-iksn-rs03-an",
                "category": "dsGroup",
                "imgUrl": "images/data_sources/defaut_datasource.png"
            },
            {
                "name": "Derby",
                "children": [
                    {
                        "data": {
                            "id": "1",
                            "type": "dynamicDataSource"
                        },
                        "dataSourceProvider": "tomcat",
                        "type": "dynamicDataSource",
                        "permissionLevel": 5,
                        "driver": "org.apache.derby.jdbc.ClientDriver",
                        "name": "SampleTravelDataDerby",
                        "classifier": "global",
                        "dataSourceType": "Managed DataSource",
                        "category": "dataSource",
                        "children": [
                            {
                                "name": "SQLJ",
                                "children": [],
                                "keyPath": "8kbt-svvn-sjyc-ld3x-ch/k0ye-hj1a-oz4o-uri2-sv/43q9-haz7-p78p-8aln-jt",
                                "key": "43q9-haz7-p78p-8aln-jt",
                                "uuid": "43q9-haz7-p78p-8aln-jt",
                                "data": {
                                    "id": "1",
                                    "type": "dynamicDataSource"
                                },
                                "category": "schema",
                                "datasourceName": "SampleTravelDataDerby"
                            },
                            {
                                "name": "SYSFUN",
                                "children": [],
                                "keyPath": "8kbt-svvn-sjyc-ld3x-ch/k0ye-hj1a-oz4o-uri2-sv/r1wd-pfke-bf44-vo89-jr",
                                "key": "r1wd-pfke-bf44-vo89-jr",
                                "uuid": "r1wd-pfke-bf44-vo89-jr",
                                "data": {
                                    "id": "1",
                                    "type": "dynamicDataSource"
                                },
                                "category": "schema",
                                "datasourceName": "SampleTravelDataDerby"
                            },
                            {
                                "name": "SYSCAT",
                                "children": [],
                                "keyPath": "8kbt-svvn-sjyc-ld3x-ch/k0ye-hj1a-oz4o-uri2-sv/8blc-2rg5-pbud-2t9x-lv",
                                "key": "8blc-2rg5-pbud-2t9x-lv",
                                "uuid": "8blc-2rg5-pbud-2t9x-lv",
                                "data": {
                                    "id": "1",
                                    "type": "dynamicDataSource"
                                },
                                "category": "schema",
                                "datasourceName": "SampleTravelDataDerby"
                            },
                            {
                                "name": "HIUSER",
                                "children": [
                                    {
                                        "id": "4ac5d9f68b58bd7c0d179146e46795be",
                                        "name": "dimdate",
                                        "data": {
                                            "id": "1",
                                            "type": "dynamicDataSource"
                                        },
                                        "keyPath": "8kbt-svvn-sjyc-ld3x-ch/k0ye-hj1a-oz4o-uri2-sv/7mu2-0ddk-6yes-13rr-an/en73-96do-zhyx-48k8-yt",
                                        "key": "en73-96do-zhyx-48k8-yt",
                                        "alias": "dimdate",
                                        "uuid": "en73-96do-zhyx-48k8-yt",
                                        "connId": "6nt4u",
                                        "dataSource": {
                                            "id": "1",
                                            "type": "dynamicDataSource",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "",
                                            "schema": "HIUSER",
                                            "connId": "6nt4u",
                                            "classifier": "db.workflow",
                                            "datasourceName": "SampleTravelDataDerby",
                                            "dsKeyPath": "8kbt-svvn-sjyc-ld3x-ch/k0ye-hj1a-oz4o-uri2-sv/7mu2-0ddk-6yes-13rr-an",
                                            "driverType": "Derby",
                                            "database": "HIUSER"
                                        },
                                        "dataSourceName": "SampleTravelDataDerby",
                                        "category": "table",
                                        "nameWithConnId": "dimdate_6nt4u",
                                        "database": "HIUSER",
                                        "schema": "HIUSER",
                                        "selected": true
                                    },
                                    {
                                        "id": "4e1fd245f4d13b77be423a43f01d80b2",
                                        "name": "employee_details",
                                        "data": {
                                            "id": "1",
                                            "type": "dynamicDataSource"
                                        },
                                        "keyPath": "8kbt-svvn-sjyc-ld3x-ch/k0ye-hj1a-oz4o-uri2-sv/7mu2-0ddk-6yes-13rr-an/iuf9-d2et-d460-lyya-ay",
                                        "key": "iuf9-d2et-d460-lyya-ay",
                                        "alias": "employee_details",
                                        "uuid": "iuf9-d2et-d460-lyya-ay",
                                        "connId": "6nt4u",
                                        "dataSource": {
                                            "id": "1",
                                            "type": "dynamicDataSource",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "",
                                            "schema": "HIUSER",
                                            "connId": "6nt4u",
                                            "classifier": "db.workflow",
                                            "datasourceName": "SampleTravelDataDerby",
                                            "dsKeyPath": "8kbt-svvn-sjyc-ld3x-ch/k0ye-hj1a-oz4o-uri2-sv/7mu2-0ddk-6yes-13rr-an",
                                            "driverType": "Derby",
                                            "database": "HIUSER"
                                        },
                                        "dataSourceName": "SampleTravelDataDerby",
                                        "category": "table",
                                        "nameWithConnId": "employee_details_6nt4u",
                                        "database": "HIUSER",
                                        "schema": "HIUSER",
                                        "selected": true
                                    },
                                    {
                                        "id": "be534112989b616b194bc59c2fb25a42",
                                        "name": "geo_cordinates",
                                        "data": {
                                            "id": "1",
                                            "type": "dynamicDataSource"
                                        },
                                        "keyPath": "8kbt-svvn-sjyc-ld3x-ch/k0ye-hj1a-oz4o-uri2-sv/7mu2-0ddk-6yes-13rr-an/6rlu-nvnx-xv82-6dw7-ga",
                                        "key": "6rlu-nvnx-xv82-6dw7-ga",
                                        "alias": "geo_cordinates",
                                        "uuid": "6rlu-nvnx-xv82-6dw7-ga",
                                        "connId": "6nt4u",
                                        "dataSource": {
                                            "id": "1",
                                            "type": "dynamicDataSource",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "",
                                            "schema": "HIUSER",
                                            "connId": "6nt4u",
                                            "classifier": "db.workflow",
                                            "datasourceName": "SampleTravelDataDerby",
                                            "dsKeyPath": "8kbt-svvn-sjyc-ld3x-ch/k0ye-hj1a-oz4o-uri2-sv/7mu2-0ddk-6yes-13rr-an",
                                            "driverType": "Derby",
                                            "database": "HIUSER"
                                        },
                                        "dataSourceName": "SampleTravelDataDerby",
                                        "category": "table",
                                        "nameWithConnId": "geo_cordinates_6nt4u",
                                        "database": "HIUSER",
                                        "schema": "HIUSER",
                                        "selected": true
                                    },
                                    {
                                        "id": "9645c648a1c0dbeec1287aaf1e996db3",
                                        "name": "meeting_details",
                                        "data": {
                                            "id": "1",
                                            "type": "dynamicDataSource"
                                        },
                                        "keyPath": "8kbt-svvn-sjyc-ld3x-ch/k0ye-hj1a-oz4o-uri2-sv/7mu2-0ddk-6yes-13rr-an/zwmf-atjb-laao-sc2w-s0",
                                        "key": "zwmf-atjb-laao-sc2w-s0",
                                        "alias": "meeting_details",
                                        "uuid": "zwmf-atjb-laao-sc2w-s0",
                                        "connId": "6nt4u",
                                        "dataSource": {
                                            "id": "1",
                                            "type": "dynamicDataSource",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "",
                                            "schema": "HIUSER",
                                            "connId": "6nt4u",
                                            "classifier": "db.workflow",
                                            "datasourceName": "SampleTravelDataDerby",
                                            "dsKeyPath": "8kbt-svvn-sjyc-ld3x-ch/k0ye-hj1a-oz4o-uri2-sv/7mu2-0ddk-6yes-13rr-an",
                                            "driverType": "Derby",
                                            "database": "HIUSER"
                                        },
                                        "dataSourceName": "SampleTravelDataDerby",
                                        "category": "table",
                                        "nameWithConnId": "meeting_details_6nt4u",
                                        "database": "HIUSER",
                                        "schema": "HIUSER",
                                        "selected": true
                                    },
                                    {
                                        "id": "8a28627d07d04ef096d9935f12e0c7e9",
                                        "name": "travel_details",
                                        "data": {
                                            "id": "1",
                                            "type": "dynamicDataSource"
                                        },
                                        "keyPath": "8kbt-svvn-sjyc-ld3x-ch/k0ye-hj1a-oz4o-uri2-sv/7mu2-0ddk-6yes-13rr-an/gg7j-iwob-6xk9-tp1t-0x",
                                        "key": "gg7j-iwob-6xk9-tp1t-0x",
                                        "alias": "travel_details",
                                        "uuid": "gg7j-iwob-6xk9-tp1t-0x",
                                        "connId": "6nt4u",
                                        "dataSource": {
                                            "id": "1",
                                            "type": "dynamicDataSource",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "",
                                            "schema": "HIUSER",
                                            "connId": "6nt4u",
                                            "classifier": "db.workflow",
                                            "datasourceName": "SampleTravelDataDerby",
                                            "dsKeyPath": "8kbt-svvn-sjyc-ld3x-ch/k0ye-hj1a-oz4o-uri2-sv/7mu2-0ddk-6yes-13rr-an",
                                            "driverType": "Derby",
                                            "database": "HIUSER"
                                        },
                                        "dataSourceName": "SampleTravelDataDerby",
                                        "category": "table",
                                        "nameWithConnId": "travel_details_6nt4u",
                                        "database": "HIUSER",
                                        "schema": "HIUSER",
                                        "selected": true
                                    }
                                ],
                                "keyPath": "8kbt-svvn-sjyc-ld3x-ch/k0ye-hj1a-oz4o-uri2-sv/7mu2-0ddk-6yes-13rr-an",
                                "key": "7mu2-0ddk-6yes-13rr-an",
                                "uuid": "7mu2-0ddk-6yes-13rr-an",
                                "data": {
                                    "id": "1",
                                    "type": "dynamicDataSource"
                                },
                                "category": "schema",
                                "datasourceName": "SampleTravelDataDerby",
                                "fetched": true
                            },
                            {
                                "name": "SYSCS_DIAG",
                                "children": [],
                                "keyPath": "8kbt-svvn-sjyc-ld3x-ch/k0ye-hj1a-oz4o-uri2-sv/7cd4-6i4r-hfp8-0rzi-ik",
                                "key": "7cd4-6i4r-hfp8-0rzi-ik",
                                "uuid": "7cd4-6i4r-hfp8-0rzi-ik",
                                "data": {
                                    "id": "1",
                                    "type": "dynamicDataSource"
                                },
                                "category": "schema",
                                "datasourceName": "SampleTravelDataDerby"
                            },
                            {
                                "name": "SYSCS_UTIL",
                                "children": [],
                                "keyPath": "8kbt-svvn-sjyc-ld3x-ch/k0ye-hj1a-oz4o-uri2-sv/vlav-z2xc-g84w-rd2i-nx",
                                "key": "vlav-z2xc-g84w-rd2i-nx",
                                "uuid": "vlav-z2xc-g84w-rd2i-nx",
                                "data": {
                                    "id": "1",
                                    "type": "dynamicDataSource"
                                },
                                "category": "schema",
                                "datasourceName": "SampleTravelDataDerby"
                            },
                            {
                                "name": "SYSIBM",
                                "children": [],
                                "keyPath": "8kbt-svvn-sjyc-ld3x-ch/k0ye-hj1a-oz4o-uri2-sv/2s1n-l8mw-co2a-1t3x-2o",
                                "key": "2s1n-l8mw-co2a-1t3x-2o",
                                "uuid": "2s1n-l8mw-co2a-1t3x-2o",
                                "data": {
                                    "id": "1",
                                    "type": "dynamicDataSource"
                                },
                                "category": "schema",
                                "datasourceName": "SampleTravelDataDerby"
                            },
                            {
                                "name": "APP",
                                "children": [],
                                "keyPath": "8kbt-svvn-sjyc-ld3x-ch/k0ye-hj1a-oz4o-uri2-sv/uipp-e9j1-aft7-91qt-wh",
                                "key": "uipp-e9j1-aft7-91qt-wh",
                                "uuid": "uipp-e9j1-aft7-91qt-wh",
                                "data": {
                                    "id": "1",
                                    "type": "dynamicDataSource"
                                },
                                "category": "schema",
                                "datasourceName": "SampleTravelDataDerby"
                            },
                            {
                                "name": "NULLID",
                                "children": [],
                                "keyPath": "8kbt-svvn-sjyc-ld3x-ch/k0ye-hj1a-oz4o-uri2-sv/tzbn-8356-93wv-o8a6-8q",
                                "key": "tzbn-8356-93wv-o8a6-8q",
                                "uuid": "tzbn-8356-93wv-o8a6-8q",
                                "data": {
                                    "id": "1",
                                    "type": "dynamicDataSource"
                                },
                                "category": "schema",
                                "datasourceName": "SampleTravelDataDerby"
                            },
                            {
                                "name": "SYSPROC",
                                "children": [],
                                "keyPath": "8kbt-svvn-sjyc-ld3x-ch/k0ye-hj1a-oz4o-uri2-sv/dqpj-5mag-avg4-tv5r-9n",
                                "key": "dqpj-5mag-avg4-tv5r-9n",
                                "uuid": "dqpj-5mag-avg4-tv5r-9n",
                                "data": {
                                    "id": "1",
                                    "type": "dynamicDataSource"
                                },
                                "category": "schema",
                                "datasourceName": "SampleTravelDataDerby"
                            },
                            {
                                "name": "SYS",
                                "children": [],
                                "keyPath": "8kbt-svvn-sjyc-ld3x-ch/k0ye-hj1a-oz4o-uri2-sv/9a3a-0zn6-fy4p-oq9i-rp",
                                "key": "9a3a-0zn6-fy4p-oq9i-rp",
                                "uuid": "9a3a-0zn6-fy4p-oq9i-rp",
                                "data": {
                                    "id": "1",
                                    "type": "dynamicDataSource"
                                },
                                "category": "schema",
                                "datasourceName": "SampleTravelDataDerby"
                            },
                            {
                                "name": "SYSSTAT",
                                "children": [],
                                "keyPath": "8kbt-svvn-sjyc-ld3x-ch/k0ye-hj1a-oz4o-uri2-sv/m8m0-hzoc-huvl-hzq5-uf",
                                "key": "m8m0-hzoc-huvl-hzq5-uf",
                                "uuid": "m8m0-hzoc-huvl-hzq5-uf",
                                "data": {
                                    "id": "1",
                                    "type": "dynamicDataSource"
                                },
                                "category": "schema",
                                "datasourceName": "SampleTravelDataDerby"
                            }
                        ],
                        "driverType": "Derby",
                        "keyPath": "8kbt-svvn-sjyc-ld3x-ch/k0ye-hj1a-oz4o-uri2-sv",
                        "key": "k0ye-hj1a-oz4o-uri2-sv",
                        "uuid": "k0ye-hj1a-oz4o-uri2-sv",
                        "fetched": true
                    }
                ],
                "key": "8kbt-svvn-sjyc-ld3x-ch",
                "uuid": "8kbt-svvn-sjyc-ld3x-ch",
                "keyPath": "8kbt-svvn-sjyc-ld3x-ch",
                "category": "dsGroup",
                "imgUrl": "images/data_sources/defaut_datasource.png"
            },
            {
                "name": "DynamicSwitch",
                "children": [
                    {
                        "permissionLevel": 5,
                        "driver": "dynamicSwitch",
                        "name": "test",
                        "classifier": "efwd",
                        "type": "sql.jdbc.groovy.managed",
                        "data": {
                            "dir": "Gagan",
                            "driverName": null,
                            "type": "sql.jdbc.groovy.managed",
                            "id": 1,
                            "userName": null,
                            "password": null,
                            "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1000);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                            "jdbcUrl": null,
                            "driver": "dynamicSwitch"
                        },
                        "dataSourceType": "",
                        "category": "dataSource",
                        "children": [],
                        "driverType": "DynamicSwitch",
                        "keyPath": "fr18-u3ft-1sul-pnvi-kr/b5yh-3bkq-esu8-c8ui-lk",
                        "key": "b5yh-3bkq-esu8-c8ui-lk",
                        "uuid": "b5yh-3bkq-esu8-c8ui-lk"
                    },
                    {
                        "permissionLevel": 5,
                        "driver": "dynamicSwitch",
                        "name": "DifferentDbgroovyManged",
                        "classifier": "efwd",
                        "type": "sql.jdbc.groovy.managed",
                        "data": {
                            "dir": "Groovy_Managed",
                            "driverName": null,
                            "type": "sql.jdbc.groovy.managed",
                            "id": 2,
                            "userName": null,
                            "password": null,
                            "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1000);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 1003);\n          responseJson.put(\"catalog\",\"SampleTravelData\");\n\t\t  responseJson.put(\"schema\",\"public\");\n          responseJson.put(\"database\",\"SampleTravelData.public\")\n      \n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 1006);\n           responseJson.put(\"catalog\",\"\");\n          responseJson.put(\"schema\",\"\");\n          responseJson.put(\"database\",\"\")\n\n          \n        }\n        if (userName.equals(\"helical\")) {\n          responseJson.put(\"globalId\", 1);\n          responseJson.put(\"catalog\",\"\");\n\t\t  responseJson.put(\"schema\",\"HIUSER\");\n          responseJson.put(\"database\",\"HIUSER\")\n     \n        }\n      \n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                            "jdbcUrl": null,
                            "driver": "dynamicSwitch"
                        },
                        "dataSourceType": "",
                        "category": "dataSource",
                        "children": [],
                        "driverType": "DynamicSwitch",
                        "keyPath": "fr18-u3ft-1sul-pnvi-kr/ta73-xvp0-7qh8-tfd0-q5",
                        "key": "ta73-xvp0-7qh8-tfd0-q5",
                        "uuid": "ta73-xvp0-7qh8-tfd0-q5"
                    }
                ],
                "key": "fr18-u3ft-1sul-pnvi-kr",
                "uuid": "fr18-u3ft-1sul-pnvi-kr",
                "keyPath": "fr18-u3ft-1sul-pnvi-kr",
                "category": "dsGroup",
                "imgUrl": "images/data_sources/defaut_datasource.png"
            },
            {
                "name": "Mysql",
                "children": [
                    {
                        "permissionLevel": 5,
                        "driver": "com.mysql.jdbc.Driver",
                        "name": "SampleGroovyPlain",
                        "classifier": "efwd",
                        "type": "sql.jdbc.groovy",
                        "data": {
                            "dir": "Groovy_Plain",
                            "driverName": "com.mysql.jdbc.Driver",
                            "type": "sql.jdbc.groovy",
                            "id": 3,
                            "userName": "bugzilla",
                            "password": "bugzilla@QA",
                            "condition": "import net.sf.json.JSONObject;\n    import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n    public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        responseJson.put(\"driverName\", \"com.mysql.jdbc.Driver\");\n        responseJson.put(\"jdbcUrl\", \"jdbc:mysql://localhost:3306/SampleTravelData\");\n        responseJson.put(\"userName\", \"bugzilla\");\n        responseJson.put(\"password\", \"bugzilla@QA\");\n        return responseJson;\n    }",
                            "jdbcUrl": "jdbc:mysql://localhost:3306/S"
                        },
                        "dataSourceType": "",
                        "category": "dataSource",
                        "children": [],
                        "driverType": "Mysql",
                        "keyPath": "lsff-7vps-mk1k-969w-db/9myv-k8l8-76ej-b9e7-p9",
                        "key": "9myv-k8l8-76ej-b9e7-p9",
                        "uuid": "9myv-k8l8-76ej-b9e7-p9"
                    },
                    {
                        "data": {
                            "id": "1000",
                            "type": "dynamicDataSource"
                        },
                        "dataSourceProvider": "tomcat",
                        "type": "dynamicDataSource",
                        "permissionLevel": 5,
                        "driver": "com.mysql.jdbc.Driver",
                        "name": "MYSQLSampleDS",
                        "classifier": "global",
                        "dataSourceType": "Managed DataSource",
                        "category": "dataSource",
                        "children": [],
                        "driverType": "Mysql",
                        "keyPath": "lsff-7vps-mk1k-969w-db/snez-04lu-5e3e-x8y3-q2",
                        "key": "snez-04lu-5e3e-x8y3-q2",
                        "uuid": "snez-04lu-5e3e-x8y3-q2"
                    },
                    {
                        "data": {
                            "id": "1001",
                            "type": "dynamicDataSource"
                        },
                        "dataSourceProvider": "tomcat",
                        "type": "dynamicDataSource",
                        "permissionLevel": 5,
                        "driver": "com.mysql.jdbc.Driver",
                        "name": "MYSQLSingleDumpDS",
                        "classifier": "global",
                        "dataSourceType": "Managed DataSource",
                        "category": "dataSource",
                        "children": [],
                        "driverType": "Mysql",
                        "keyPath": "lsff-7vps-mk1k-969w-db/57lc-xklr-zxjy-f11o-ep",
                        "key": "57lc-xklr-zxjy-f11o-ep",
                        "uuid": "57lc-xklr-zxjy-f11o-ep"
                    },
                    {
                        "data": {
                            "id": "1002",
                            "type": "dynamicDataSource"
                        },
                        "dataSourceProvider": "tomcat",
                        "type": "dynamicDataSource",
                        "permissionLevel": 5,
                        "driver": "com.mysql.jdbc.Driver",
                        "name": "MYSQLMultiConnDumpDS",
                        "classifier": "global",
                        "dataSourceType": "Managed DataSource",
                        "category": "dataSource",
                        "children": [],
                        "driverType": "Mysql",
                        "keyPath": "lsff-7vps-mk1k-969w-db/znlm-300y-wfl2-670d-cx",
                        "key": "znlm-300y-wfl2-670d-cx",
                        "uuid": "znlm-300y-wfl2-670d-cx"
                    }
                ],
                "key": "lsff-7vps-mk1k-969w-db",
                "uuid": "lsff-7vps-mk1k-969w-db",
                "keyPath": "lsff-7vps-mk1k-969w-db",
                "category": "dsGroup",
                "imgUrl": "images/data_sources/defaut_datasource.png"
            },
            {
                "name": "Postgresql",
                "children": [
                    {
                        "data": {
                            "id": "1003",
                            "type": "dynamicDataSource"
                        },
                        "dataSourceProvider": "tomcat",
                        "type": "dynamicDataSource",
                        "permissionLevel": 5,
                        "driver": "org.postgresql.Driver",
                        "name": "PostgresSampleDS",
                        "classifier": "global",
                        "dataSourceType": "Managed DataSource",
                        "category": "dataSource",
                        "children": [],
                        "driverType": "Postgresql",
                        "keyPath": "aj7v-f49d-zdil-p26t-2d/dq0q-vms5-5lx2-dv9s-od",
                        "key": "dq0q-vms5-5lx2-dv9s-od",
                        "uuid": "dq0q-vms5-5lx2-dv9s-od"
                    },
                    {
                        "data": {
                            "id": "1004",
                            "type": "dynamicDataSource"
                        },
                        "dataSourceProvider": "tomcat",
                        "type": "dynamicDataSource",
                        "permissionLevel": 5,
                        "driver": "org.postgresql.Driver",
                        "name": "PostgresSingleDumpDS",
                        "classifier": "global",
                        "dataSourceType": "Managed DataSource",
                        "category": "dataSource",
                        "children": [],
                        "driverType": "Postgresql",
                        "keyPath": "aj7v-f49d-zdil-p26t-2d/xurj-6j7i-s2wk-wrz4-6i",
                        "key": "xurj-6j7i-s2wk-wrz4-6i",
                        "uuid": "xurj-6j7i-s2wk-wrz4-6i"
                    },
                    {
                        "data": {
                            "id": "1005",
                            "type": "dynamicDataSource"
                        },
                        "dataSourceProvider": "tomcat",
                        "type": "dynamicDataSource",
                        "permissionLevel": 5,
                        "driver": "org.postgresql.Driver",
                        "name": "PostgresMultiConnDumpDS",
                        "classifier": "global",
                        "dataSourceType": "Managed DataSource",
                        "category": "dataSource",
                        "children": [],
                        "driverType": "Postgresql",
                        "keyPath": "aj7v-f49d-zdil-p26t-2d/1z2b-rxet-yk7a-scor-rj",
                        "key": "1z2b-rxet-yk7a-scor-rj",
                        "uuid": "1z2b-rxet-yk7a-scor-rj"
                    }
                ],
                "key": "aj7v-f49d-zdil-p26t-2d",
                "uuid": "aj7v-f49d-zdil-p26t-2d",
                "keyPath": "aj7v-f49d-zdil-p26t-2d",
                "category": "dsGroup",
                "imgUrl": "images/data_sources/defaut_datasource.png"
            },
            {
                "name": "Sqlite",
                "children": [
                    {
                        "data": {
                            "id": "1006",
                            "type": "dynamicDataSource"
                        },
                        "dataSourceProvider": "tomcat",
                        "type": "dynamicDataSource",
                        "permissionLevel": 5,
                        "driver": "org.sqlite.JDBC",
                        "name": "SqliteSampleDS",
                        "classifier": "global",
                        "dataSourceType": "Managed DataSource",
                        "category": "dataSource",
                        "children": [],
                        "driverType": "Sqlite",
                        "keyPath": "7e4k-ub5i-8u59-jvxq-38/m3if-dnna-zu6o-ipu5-e0",
                        "key": "m3if-dnna-zu6o-ipu5-e0",
                        "uuid": "m3if-dnna-zu6o-ipu5-e0"
                    },
                    {
                        "data": {
                            "id": "1007",
                            "type": "dynamicDataSource"
                        },
                        "dataSourceProvider": "tomcat",
                        "type": "dynamicDataSource",
                        "permissionLevel": 5,
                        "driver": "org.sqlite.JDBC",
                        "name": "SqliteSingleDumpDS",
                        "classifier": "global",
                        "dataSourceType": "Managed DataSource",
                        "category": "dataSource",
                        "children": [],
                        "driverType": "Sqlite",
                        "keyPath": "7e4k-ub5i-8u59-jvxq-38/gplh-q1cz-l6lo-vtjf-2j",
                        "key": "gplh-q1cz-l6lo-vtjf-2j",
                        "uuid": "gplh-q1cz-l6lo-vtjf-2j"
                    }
                ],
                "key": "7e4k-ub5i-8u59-jvxq-38",
                "uuid": "7e4k-ub5i-8u59-jvxq-38",
                "keyPath": "7e4k-ub5i-8u59-jvxq-38",
                "category": "dsGroup",
                "imgUrl": "images/data_sources/defaut_datasource.png"
            }
        ],
        "workFlow": {
            "dataList": [
                {
                    "fetchSchemas": true,
                    "fetchCatalogs": true,
                    "working": true,
                    "connData": {
                        "id": "1",
                        "type": "dynamicDataSource"
                    },
                    "connId": "1",
                    "dsUUID": "k0ye-hj1a-oz4o-uri2-sv",
                    "classifier": "db.workflow",
                    "metadata": {
                        "catalogs": [
                            {
                                "name": "Null",
                                "schemas": [
                                    {
                                        "name": "SQLJ"
                                    },
                                    {
                                        "name": "SYSFUN"
                                    },
                                    {
                                        "name": "SYSCAT"
                                    },
                                    {
                                        "name": "HIUSER"
                                    },
                                    {
                                        "name": "SYSCS_DIAG"
                                    },
                                    {
                                        "name": "SYSCS_UTIL"
                                    },
                                    {
                                        "name": "SYSIBM"
                                    },
                                    {
                                        "name": "APP"
                                    },
                                    {
                                        "name": "NULLID"
                                    },
                                    {
                                        "name": "SYSPROC"
                                    },
                                    {
                                        "name": "SYS"
                                    },
                                    {
                                        "name": "SYSSTAT"
                                    }
                                ]
                            }
                        ]
                    }
                },
                {
                    "classifier": "db.workflow",
                    "metadata": {
                        "catalogs": [
                            {
                                "name": "Null",
                                "schemas": [
                                    {
                                        "name": "HIUSER",
                                        "tables": [
                                            {
                                                "id": "be534112989b616b194bc59c2fb25a42",
                                                "name": "geo_cordinates"
                                            },
                                            {
                                                "id": "9645c648a1c0dbeec1287aaf1e996db3",
                                                "name": "meeting_details"
                                            },
                                            {
                                                "id": "4e1fd245f4d13b77be423a43f01d80b2",
                                                "name": "employee_details"
                                            },
                                            {
                                                "id": "4ac5d9f68b58bd7c0d179146e46795be",
                                                "name": "dimdate"
                                            },
                                            {
                                                "id": "8a28627d07d04ef096d9935f12e0c7e9",
                                                "name": "travel_details"
                                            }
                                        ]
                                    }
                                ]
                            }
                        ],
                        "dataSource": {
                            "id": "1",
                            "type": "dynamicDataSource",
                            "baseType": "global.jdbc",
                            "catSchemaPredicted": false,
                            "sync": false,
                            "catalog": "",
                            "schema": "HIUSER",
                            "connId": "6nt4u",
                            "classifier": "db.workflow",
                            "datasourceName": "SampleTravelDataDerby",
                            "dsKeyPath": "8kbt-svvn-sjyc-ld3x-ch/k0ye-hj1a-oz4o-uri2-sv/7mu2-0ddk-6yes-13rr-an",
                            "driverType": "Derby",
                            "database": "HIUSER"
                        },
                        "name": "HIUSER"
                    }
                }
            ]
        },
        "dataSource": [
            {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "6nt4u",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "8kbt-svvn-sjyc-ld3x-ch/k0ye-hj1a-oz4o-uri2-sv/7mu2-0ddk-6yes-13rr-an",
                "driverType": "Derby",
                "database": "HIUSER"
            }
        ],
        "tables": {
            "dimdate": {
                "id": "4ac5d9f68b58bd7c0d179146e46795be",
                "name": "dimdate",
                "data": {
                    "id": "1",
                    "type": "dynamicDataSource"
                },
                "keyPath": "8kbt-svvn-sjyc-ld3x-ch/k0ye-hj1a-oz4o-uri2-sv/7mu2-0ddk-6yes-13rr-an/en73-96do-zhyx-48k8-yt",
                "key": "en73-96do-zhyx-48k8-yt",
                "alias": "dimdate",
                "uuid": "en73-96do-zhyx-48k8-yt",
                "connId": "6nt4u",
                "dataSource": {
                    "id": "1",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc",
                    "catSchemaPredicted": false,
                    "sync": false,
                    "catalog": "",
                    "schema": "HIUSER",
                    "connId": "6nt4u",
                    "classifier": "db.workflow",
                    "datasourceName": "SampleTravelDataDerby",
                    "dsKeyPath": "8kbt-svvn-sjyc-ld3x-ch/k0ye-hj1a-oz4o-uri2-sv/7mu2-0ddk-6yes-13rr-an",
                    "driverType": "Derby",
                    "database": "HIUSER"
                },
                "dataSourceName": "SampleTravelDataDerby",
                "category": "table",
                "nameWithConnId": "dimdate_6nt4u",
                "database": "HIUSER",
                "schema": "HIUSER",
                "selected": true,
                "keyName": "dimdate"
            },
            "employee_details": {
                "id": "4e1fd245f4d13b77be423a43f01d80b2",
                "name": "employee_details",
                "data": {
                    "id": "1",
                    "type": "dynamicDataSource"
                },
                "keyPath": "8kbt-svvn-sjyc-ld3x-ch/k0ye-hj1a-oz4o-uri2-sv/7mu2-0ddk-6yes-13rr-an/iuf9-d2et-d460-lyya-ay",
                "key": "iuf9-d2et-d460-lyya-ay",
                "alias": "employee_details",
                "uuid": "iuf9-d2et-d460-lyya-ay",
                "connId": "6nt4u",
                "dataSource": {
                    "id": "1",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc",
                    "catSchemaPredicted": false,
                    "sync": false,
                    "catalog": "",
                    "schema": "HIUSER",
                    "connId": "6nt4u",
                    "classifier": "db.workflow",
                    "datasourceName": "SampleTravelDataDerby",
                    "dsKeyPath": "8kbt-svvn-sjyc-ld3x-ch/k0ye-hj1a-oz4o-uri2-sv/7mu2-0ddk-6yes-13rr-an",
                    "driverType": "Derby",
                    "database": "HIUSER"
                },
                "dataSourceName": "SampleTravelDataDerby",
                "category": "table",
                "nameWithConnId": "employee_details_6nt4u",
                "database": "HIUSER",
                "schema": "HIUSER",
                "selected": true,
                "keyName": "employee_details"
            },
            "geo_cordinates": {
                "id": "be534112989b616b194bc59c2fb25a42",
                "name": "geo_cordinates",
                "data": {
                    "id": "1",
                    "type": "dynamicDataSource"
                },
                "keyPath": "8kbt-svvn-sjyc-ld3x-ch/k0ye-hj1a-oz4o-uri2-sv/7mu2-0ddk-6yes-13rr-an/6rlu-nvnx-xv82-6dw7-ga",
                "key": "6rlu-nvnx-xv82-6dw7-ga",
                "alias": "geo_cordinates",
                "uuid": "6rlu-nvnx-xv82-6dw7-ga",
                "connId": "6nt4u",
                "dataSource": {
                    "id": "1",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc",
                    "catSchemaPredicted": false,
                    "sync": false,
                    "catalog": "",
                    "schema": "HIUSER",
                    "connId": "6nt4u",
                    "classifier": "db.workflow",
                    "datasourceName": "SampleTravelDataDerby",
                    "dsKeyPath": "8kbt-svvn-sjyc-ld3x-ch/k0ye-hj1a-oz4o-uri2-sv/7mu2-0ddk-6yes-13rr-an",
                    "driverType": "Derby",
                    "database": "HIUSER"
                },
                "dataSourceName": "SampleTravelDataDerby",
                "category": "table",
                "nameWithConnId": "geo_cordinates_6nt4u",
                "database": "HIUSER",
                "schema": "HIUSER",
                "selected": true,
                "keyName": "geo_cordinates"
            },
            "meeting_details": {
                "id": "9645c648a1c0dbeec1287aaf1e996db3",
                "name": "meeting_details",
                "data": {
                    "id": "1",
                    "type": "dynamicDataSource"
                },
                "keyPath": "8kbt-svvn-sjyc-ld3x-ch/k0ye-hj1a-oz4o-uri2-sv/7mu2-0ddk-6yes-13rr-an/zwmf-atjb-laao-sc2w-s0",
                "key": "zwmf-atjb-laao-sc2w-s0",
                "alias": "meeting_details",
                "uuid": "zwmf-atjb-laao-sc2w-s0",
                "connId": "6nt4u",
                "dataSource": {
                    "id": "1",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc",
                    "catSchemaPredicted": false,
                    "sync": false,
                    "catalog": "",
                    "schema": "HIUSER",
                    "connId": "6nt4u",
                    "classifier": "db.workflow",
                    "datasourceName": "SampleTravelDataDerby",
                    "dsKeyPath": "8kbt-svvn-sjyc-ld3x-ch/k0ye-hj1a-oz4o-uri2-sv/7mu2-0ddk-6yes-13rr-an",
                    "driverType": "Derby",
                    "database": "HIUSER"
                },
                "dataSourceName": "SampleTravelDataDerby",
                "category": "table",
                "nameWithConnId": "meeting_details_6nt4u",
                "database": "HIUSER",
                "schema": "HIUSER",
                "selected": true,
                "keyName": "meeting_details"
            },
            "travel_details": {
                "id": "8a28627d07d04ef096d9935f12e0c7e9",
                "name": "travel_details",
                "data": {
                    "id": "1",
                    "type": "dynamicDataSource"
                },
                "keyPath": "8kbt-svvn-sjyc-ld3x-ch/k0ye-hj1a-oz4o-uri2-sv/7mu2-0ddk-6yes-13rr-an/gg7j-iwob-6xk9-tp1t-0x",
                "key": "gg7j-iwob-6xk9-tp1t-0x",
                "alias": "travel_details",
                "uuid": "gg7j-iwob-6xk9-tp1t-0x",
                "connId": "6nt4u",
                "dataSource": {
                    "id": "1",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc",
                    "catSchemaPredicted": false,
                    "sync": false,
                    "catalog": "",
                    "schema": "HIUSER",
                    "connId": "6nt4u",
                    "classifier": "db.workflow",
                    "datasourceName": "SampleTravelDataDerby",
                    "dsKeyPath": "8kbt-svvn-sjyc-ld3x-ch/k0ye-hj1a-oz4o-uri2-sv/7mu2-0ddk-6yes-13rr-an",
                    "driverType": "Derby",
                    "database": "HIUSER"
                },
                "dataSourceName": "SampleTravelDataDerby",
                "category": "table",
                "nameWithConnId": "travel_details_6nt4u",
                "database": "HIUSER",
                "schema": "HIUSER",
                "selected": true,
                "keyName": "travel_details"
            }
        },
        "views": [],
        "activeView": false,
        "categories": {
            "ochz-464g-iksn-rs03-an": {
                "ds": {
                    "data": {
                        "id": "1008",
                        "type": "dynamicDataSource"
                    },
                    "dataSourceProvider": "tomcat",
                    "type": "dynamicDataSource",
                    "permissionLevel": 5,
                    "driver": "org.apache.drill.jdbc.Driver",
                    "name": "ApacheDrillSampleDS",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource"
                },
                "category": {
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
                }
            },
            "8kbt-svvn-sjyc-ld3x-ch": {
                "ds": {
                    "data": {
                        "id": "1",
                        "type": "dynamicDataSource"
                    },
                    "dataSourceProvider": "tomcat",
                    "type": "dynamicDataSource",
                    "permissionLevel": 5,
                    "driver": "org.apache.derby.jdbc.ClientDriver",
                    "name": "SampleTravelDataDerby",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource"
                },
                "category": {
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
                }
            },
            "fr18-u3ft-1sul-pnvi-kr": {
                "ds": {
                    "permissionLevel": 5,
                    "driver": "dynamicSwitch",
                    "name": "test",
                    "classifier": "efwd",
                    "type": "sql.jdbc.groovy.managed",
                    "data": {
                        "dir": "Gagan",
                        "driverName": null,
                        "type": "sql.jdbc.groovy.managed",
                        "id": 1,
                        "userName": null,
                        "password": null,
                        "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1000);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                        "jdbcUrl": null,
                        "driver": "dynamicSwitch"
                    },
                    "dataSourceType": ""
                },
                "category": {
                    "driver": "dynamicSwitch",
                    "databaseDialect": "",
                    "name": "DynamicSwitch",
                    "categoryName": "RDBMS",
                    "categoryType": "rdbms",
                    "type": "global.jdbc",
                    "dataSourceProvider": "tomcat",
                    "classifier": "global",
                    "imgUrl": "../images/data_sources/defaut_datasource.png"
                }
            },
            "lsff-7vps-mk1k-969w-db": {
                "ds": {
                    "permissionLevel": 5,
                    "driver": "com.mysql.jdbc.Driver",
                    "name": "SampleGroovyPlain",
                    "classifier": "efwd",
                    "type": "sql.jdbc.groovy",
                    "data": {
                        "dir": "Groovy_Plain",
                        "driverName": "com.mysql.jdbc.Driver",
                        "type": "sql.jdbc.groovy",
                        "id": 3,
                        "userName": "bugzilla",
                        "password": "bugzilla@QA",
                        "condition": "import net.sf.json.JSONObject;\n    import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n    public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        responseJson.put(\"driverName\", \"com.mysql.jdbc.Driver\");\n        responseJson.put(\"jdbcUrl\", \"jdbc:mysql://localhost:3306/SampleTravelData\");\n        responseJson.put(\"userName\", \"bugzilla\");\n        responseJson.put(\"password\", \"bugzilla@QA\");\n        return responseJson;\n    }",
                        "jdbcUrl": "jdbc:mysql://localhost:3306/S"
                    },
                    "dataSourceType": ""
                },
                "category": {
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
                }
            },
            "aj7v-f49d-zdil-p26t-2d": {
                "ds": {
                    "data": {
                        "id": "1003",
                        "type": "dynamicDataSource"
                    },
                    "dataSourceProvider": "tomcat",
                    "type": "dynamicDataSource",
                    "permissionLevel": 5,
                    "driver": "org.postgresql.Driver",
                    "name": "PostgresSampleDS",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource"
                },
                "category": {
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
                }
            },
            "7e4k-ub5i-8u59-jvxq-38": {
                "ds": {
                    "data": {
                        "id": "1006",
                        "type": "dynamicDataSource"
                    },
                    "dataSourceProvider": "tomcat",
                    "type": "dynamicDataSource",
                    "permissionLevel": 5,
                    "driver": "org.sqlite.JDBC",
                    "name": "SqliteSampleDS",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource"
                },
                "category": {
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
                }
            }
        },
        "activeEditorTab": "joins",
        "dataSourcesAddedToMetadata": [
            {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "6nt4u",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "8kbt-svvn-sjyc-ld3x-ch/k0ye-hj1a-oz4o-uri2-sv/7mu2-0ddk-6yes-13rr-an",
                "driverType": "Derby",
                "database": "HIUSER"
            }
        ],
        "changeDSList": {},
        "changedTables": [],
        "changedColumns": [],
        "removedTables": [],
        "removedColumns": [],
        "removedDataSources": [],
        "duplicateColumnList": [],
        "duplicateTableList": [],
        "unsavedViews": [],
        "saveDetails": false,
        "savedTableIds": [],
        "savedColumnIds": [],
        "joins": [
            {
                "id": "ca21d00c8c87263dedd812f8f74c05b5",
                "type": "inner",
                "operator": "=",
                "left": {
                    "table": "geo_cordinates",
                    "column": "location_id",
                    "dataSource": {
                        "sync": false,
                        "id": "1",
                        "connectionDatabaseId": "e9331501-b91c-46c4-ad42-527418b9d4a0",
                        "catSchemaPredicted": false,
                        "catalog": "",
                        "schema": "HIUSER",
                        "type": "dynamicDataSource",
                        "baseType": "global.jdbc"
                    }
                },
                "right": {
                    "table": "dimdate",
                    "column": "dim_id",
                    "dataSource": {
                        "sync": false,
                        "id": "1",
                        "connectionDatabaseId": "e9331501-b91c-46c4-ad42-527418b9d4a0",
                        "catSchemaPredicted": false,
                        "catalog": "",
                        "schema": "HIUSER",
                        "type": "dynamicDataSource",
                        "baseType": "global.jdbc"
                    }
                },
                "uuid": "83ch-8iml-v3cn-icrw-ok",
                "index": 1,
                "action": "noChange"
            },
            {
                "id": "af8f3186af3703a70a3d6e219faafb4e",
                "type": "inner",
                "operator": "=",
                "left": {
                    "table": "employee_details",
                    "column": "employee_id",
                    "dataSource": {
                        "sync": false,
                        "id": "1",
                        "connectionDatabaseId": "e9331501-b91c-46c4-ad42-527418b9d4a0",
                        "catSchemaPredicted": false,
                        "catalog": "",
                        "schema": "HIUSER",
                        "type": "dynamicDataSource",
                        "baseType": "global.jdbc"
                    }
                },
                "right": {
                    "table": "meeting_details",
                    "column": "meeting_by",
                    "dataSource": {
                        "sync": false,
                        "id": "1",
                        "connectionDatabaseId": "e9331501-b91c-46c4-ad42-527418b9d4a0",
                        "catSchemaPredicted": false,
                        "catalog": "",
                        "schema": "HIUSER",
                        "type": "dynamicDataSource",
                        "baseType": "global.jdbc"
                    }
                },
                "uuid": "zhmz-a04d-na2w-8f1j-7n",
                "index": 2,
                "action": "noChange"
            },
            {
                "id": "aab02b68e2c7febf125c50c8c5175037",
                "type": "inner",
                "operator": "=",
                "left": {
                    "table": "employee_details",
                    "column": "employee_id",
                    "dataSource": {
                        "sync": false,
                        "id": "1",
                        "connectionDatabaseId": "e9331501-b91c-46c4-ad42-527418b9d4a0",
                        "catSchemaPredicted": false,
                        "catalog": "",
                        "schema": "HIUSER",
                        "type": "dynamicDataSource",
                        "baseType": "global.jdbc"
                    }
                },
                "right": {
                    "table": "travel_details",
                    "column": "travelled_by",
                    "dataSource": {
                        "sync": false,
                        "id": "1",
                        "connectionDatabaseId": "e9331501-b91c-46c4-ad42-527418b9d4a0",
                        "catSchemaPredicted": false,
                        "catalog": "",
                        "schema": "HIUSER",
                        "type": "dynamicDataSource",
                        "baseType": "global.jdbc"
                    }
                },
                "uuid": "xwqh-3jtl-ldtx-e74e-lk",
                "index": 3,
                "action": "noChange"
            },
            {
                "id": "daa3221b04c18670d4af25ac99f3ae76",
                "type": "inner",
                "operator": "=",
                "left": {
                    "table": "geo_cordinates",
                    "column": "location_id",
                    "dataSource": {
                        "sync": false,
                        "id": "1",
                        "connectionDatabaseId": "e9331501-b91c-46c4-ad42-527418b9d4a0",
                        "catSchemaPredicted": false,
                        "catalog": "",
                        "schema": "HIUSER",
                        "type": "dynamicDataSource",
                        "baseType": "global.jdbc"
                    }
                },
                "right": {
                    "table": "travel_details",
                    "column": "destination_id",
                    "dataSource": {
                        "sync": false,
                        "id": "1",
                        "connectionDatabaseId": "e9331501-b91c-46c4-ad42-527418b9d4a0",
                        "catSchemaPredicted": false,
                        "catalog": "",
                        "schema": "HIUSER",
                        "type": "dynamicDataSource",
                        "baseType": "global.jdbc"
                    }
                },
                "uuid": "et4n-mxls-cvwr-upv4-5n",
                "index": 4,
                "action": "noChange"
            },
            {
                "id": "cdeb5b19799c89335f23ed9b50cc5a22",
                "type": "inner",
                "operator": "=",
                "left": {
                    "table": "geo_cordinates",
                    "column": "location_id",
                    "dataSource": {
                        "sync": false,
                        "id": "1",
                        "connectionDatabaseId": "e9331501-b91c-46c4-ad42-527418b9d4a0",
                        "catSchemaPredicted": false,
                        "catalog": "",
                        "schema": "HIUSER",
                        "type": "dynamicDataSource",
                        "baseType": "global.jdbc"
                    }
                },
                "right": {
                    "table": "travel_details",
                    "column": "source_id",
                    "dataSource": {
                        "sync": false,
                        "id": "1",
                        "connectionDatabaseId": "e9331501-b91c-46c4-ad42-527418b9d4a0",
                        "catSchemaPredicted": false,
                        "catalog": "",
                        "schema": "HIUSER",
                        "type": "dynamicDataSource",
                        "baseType": "global.jdbc"
                    }
                },
                "uuid": "mijf-2i0h-dwvx-niqc-id",
                "index": 5,
                "action": "noChange"
            }
        ],
        "mode": "create",
        "allTablesKeys": [
            "6rlu-nvnx-xv82-6dw7-ga",
            "zwmf-atjb-laao-sc2w-s0",
            "iuf9-d2et-d460-lyya-ay",
            "en73-96do-zhyx-48k8-yt",
            "gg7j-iwob-6xk9-tp1t-0x"
        ],
        "selectedTableKeys": [
            "en73-96do-zhyx-48k8-yt",
            "iuf9-d2et-d460-lyya-ay",
            "6rlu-nvnx-xv82-6dw7-ga",
            "zwmf-atjb-laao-sc2w-s0",
            "gg7j-iwob-6xk9-tp1t-0x"
        ],
        "metadataName": "Metadata_1",
        "activeDataSource": false,
        "metadataToEdit": false,
        "isSavingInProgress": false,
        "editViewsTempData": {},
        "inititalStateFromJest": false,
        "timeStamp": 1670476592047,
        "initialEditResponse": false,
        "editorFullView": false,
        "selectedTableOrColumnKey": {},
        "expressionObj": [],
        "securityConstants": {},
        "edit": false,
        "isAllowServiceCall": true,
        "isValidatedTableShow": false,
        "securityTableData": [],
        "addOneMoreSecurity": false,
        "viewSessionVariables": false,
        "textEditingObj": {},
        "selectedJoinNameData": {},
        "filterbyData": [
            {
                "value": "All",
                "isChecked": true
            },
            {
                "value": "Table",
                "isChecked": true
            },
            {
                "value": "Column",
                "isChecked": true
            },
            {
                "value": "Global",
                "isChecked": true
            }
        ],
        "isFirstRender": true,
        "securityFormData": {},
        "accessType": "deny",
        "entityNames": "",
        "executionType": "conditionIf",
        "expressionName": null,
        "expressionType": "",
        "isApplyDisabled": true,
        "isInfoShow": true,
        "securityKeysChecked": [],
        "hasUnsavedData": true,
        "getSecurityTableData": {
            "tables": [],
            "columns": []
        },
        "doResetFormData": false,
        "tablesMergeType": false,
        "activeDsInfoId": "6nt4u"
    }
}

export const metadataStoreMock = {
    "dataFetchedFor": {
        "getDatasource": true,
        "joins": true,
        "viewSessionVariables": false,
        "listDataSources": true,
        "r3hm-abpg-hlf4-n4ho-en": false,
        "kpwh-9t0d-uh7a-yfgk-f5": false
    },
    "loadingStatus": {
        "getDatasource": true,
        "listDataSources": true,
        "r3hm-abpg-hlf4-n4ho-en": false,
        "kpwh-9t0d-uh7a-yfgk-f5": false
    },
    "joinsLoadingStatus": "",
    "serviceErrorStatus": {},
    "fetchedDSInfo": {},
    "listDataSource": [],
    "supportedDataSourceTypes": [
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
            "name": "Dremio",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Google Bigquery",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "IBM Db2",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Informix",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Microsoft Sqlserver",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Oracle",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Presto",
            "categoryType": "supported",
            "categoryName": "Supported"
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
    ],
    "allDataSourceTypes": [
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
            "name": "IBM Db2",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Informix",
            "categoryType": "supported",
            "categoryName": "Supported"
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
            "name": "Presto",
            "categoryType": "supported",
            "categoryName": "Supported"
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
    ],
    "metaDataSourceList": [
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
            "url": "jdbc:derby:{{database}}",
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "available": "true",
            "parameters": {
                "database": "database"
            }
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
    "allDataSources": [
        {
            "permissionLevel": 5,
            "driver": "org.apache.derby.jdbc.ClientDriver",
            "name": "GroovyPlainDerby",
            "classifier": "efwd",
            "type": "sql.jdbc.groovy",
            "data": {
                "dir": "Groovy_Plain",
                "driverName": "org.apache.derby.jdbc.ClientDriver",
                "type": "sql.jdbc.groovy",
                "id": 4,
                "userName": "hiuser&4&$%^#",
                "password": "hiuser",
                "condition": "import net.sf.json.JSONObject;\n    import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n    public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        responseJson.put(\"driver\", \"org.apache.derby.jdbc.ClientDriver\");\n        responseJson.put(\"url\", \"jdbc:derby://127.0.0.1:1527//home/helical/Performance/hi/db/SampleTravelData\");\n        responseJson.put(\"user\", \"hiuser\");\n        responseJson.put(\"pass\", \"hiuser\");\n        return responseJson;\n    }",
                "jdbcUrl": "jdbc:derby://localhost:1527/derby"
            },
            "dataSourceType": ""
        },
        {
            "permissionLevel": 5,
            "driver": "com.mysql.jdbc.Driver",
            "name": "PlainJdbcMysql",
            "classifier": "efwd",
            "type": "sql.jdbc",
            "data": {
                "dir": "PlainJdbc",
                "driverName": "com.mysql.jdbc.Driver",
                "type": "sql.jdbc",
                "id": 5,
                "userName": "bugzilla&^(*)",
                "password": "bugzilla@QA",
                "jdbcUrl": "jdbc:mysql://localhost:3306/SampleTravelData"
            },
            "dataSourceType": "Plain Jdbc DataSource"
        },
        {
            "permissionLevel": 5,
            "driver": "org.postgresql.Driver",
            "name": "PostgresPlainJDBC",
            "classifier": "efwd",
            "type": "sql.jdbc",
            "data": {
                "dir": "Export",
                "driverName": "org.postgresql.Driver",
                "type": "sql.jdbc",
                "id": 6,
                "userName": "postgres",
                "password": "postgres",
                "jdbcUrl": "jdbc:postgresql://127.0.0.1:5432/SampleTravelData"
            },
            "dataSourceType": "Plain Jdbc DataSource"
        },
        {
            "data": {
                "id": "1",
                "type": "dynamicDataSource"
            },
            "dataSourceProvider": "tomcat",
            "type": "dynamicDataSource",
            "permissionLevel": 5,
            "driver": "org.apache.derby.jdbc.ClientDriver",
            "name": "SampleTravelDataDerby",
            "classifier": "global",
            "dataSourceType": "Managed DataSource"
        },
        {
            "data": {
                "id": "1000",
                "type": "dynamicDataSource"
            },
            "dataSourceProvider": "tomcat",
            "type": "dynamicDataSource",
            "permissionLevel": 5,
            "driver": "com.mysql.jdbc.Driver",
            "name": "MYSQlSampleDS",
            "classifier": "global",
            "dataSourceType": "Managed DataSource"
        },
        {
            "data": {
                "id": "1001",
                "type": "dynamicDataSource"
            },
            "dataSourceProvider": "tomcat",
            "type": "dynamicDataSource",
            "permissionLevel": 5,
            "driver": "com.mysql.jdbc.Driver",
            "name": "MYSQLSingleDumpDS",
            "classifier": "global",
            "dataSourceType": "Managed DataSource"
        },
        {
            "data": {
                "id": "1002",
                "type": "dynamicDataSource"
            },
            "dataSourceProvider": "tomcat",
            "type": "dynamicDataSource",
            "permissionLevel": 5,
            "driver": "com.mysql.jdbc.Driver",
            "name": "MYSQLMultiConnDumpDS",
            "classifier": "global",
            "dataSourceType": "Managed DataSource"
        },
        {
            "data": {
                "id": "1003",
                "type": "dynamicDataSource"
            },
            "dataSourceProvider": "tomcat",
            "type": "dynamicDataSource",
            "permissionLevel": 5,
            "driver": "org.postgresql.Driver",
            "name": "PostgresSampleDS",
            "classifier": "global",
            "dataSourceType": "Managed DataSource"
        },
        {
            "data": {
                "id": "1004",
                "type": "dynamicDataSource"
            },
            "dataSourceProvider": "tomcat",
            "type": "dynamicDataSource",
            "permissionLevel": 5,
            "driver": "org.postgresql.Driver",
            "name": "PostgresSingleDumpDS",
            "classifier": "global",
            "dataSourceType": "Managed DataSource"
        },
        {
            "data": {
                "id": "1005",
                "type": "dynamicDataSource"
            },
            "dataSourceProvider": "tomcat",
            "type": "dynamicDataSource",
            "permissionLevel": 5,
            "driver": "org.postgresql.Driver",
            "name": "PostgresMultiConnDumpDS",
            "classifier": "global",
            "dataSourceType": "Managed DataSource"
        },
        {
            "data": {
                "id": "1006",
                "type": "dynamicDataSource"
            },
            "dataSourceProvider": "tomcat",
            "type": "dynamicDataSource",
            "permissionLevel": 5,
            "driver": "org.sqlite.JDBC",
            "name": "SqliteSampleDS",
            "classifier": "global",
            "dataSourceType": "Managed DataSource"
        },
        {
            "data": {
                "id": "1007",
                "type": "dynamicDataSource"
            },
            "dataSourceProvider": "tomcat",
            "type": "dynamicDataSource",
            "permissionLevel": 5,
            "driver": "org.sqlite.JDBC",
            "name": "SqliteSingleDumpDS",
            "classifier": "global",
            "dataSourceType": "Managed DataSource"
        },
        {
            "data": {
                "id": "1008",
                "type": "dynamicDataSource"
            },
            "dataSourceProvider": "tomcat",
            "type": "dynamicDataSource",
            "permissionLevel": 5,
            "driver": "org.apache.drill.jdbc.Driver",
            "name": "ApacheDrillSampleDS",
            "classifier": "global",
            "dataSourceType": "Managed DataSource"
        },
        {
            "data": {
                "id": "1009",
                "type": "dynamicDataSource"
            },
            "dataSourceProvider": "tomcat",
            "type": "dynamicDataSource",
            "permissionLevel": 5,
            "driver": "com.mysql.jdbc.Driver",
            "name": "BugzillaDBDS",
            "classifier": "global",
            "dataSourceType": "Managed DataSource"
        },
        {
            "data": {
                "id": "1010",
                "type": "dynamicDataSource"
            },
            "dataSourceProvider": "tomcat",
            "type": "dynamicDataSource",
            "permissionLevel": 5,
            "driver": "org.apache.derby.jdbc.ClientDriver",
            "name": "Derby",
            "classifier": "global",
            "dataSourceType": "Managed DataSource"
        },
        {
            "data": {
                "id": "1011",
                "type": "dynamicDataSource"
            },
            "dataSourceProvider": "tomcat",
            "type": "dynamicDataSource",
            "permissionLevel": 5,
            "driver": "org.apache.derby.jdbc.ClientDriver",
            "name": "3432derby",
            "classifier": "global",
            "dataSourceType": "Managed DataSource"
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
    "datasourceListToRender": [
        {
            "name": "Apache Drill",
            "children": [
                {
                    "data": {
                        "id": "1008",
                        "type": "dynamicDataSource"
                    },
                    "dataSourceProvider": "tomcat",
                    "type": "dynamicDataSource",
                    "permissionLevel": 5,
                    "driver": "org.apache.drill.jdbc.Driver",
                    "name": "ApacheDrillSampleDS",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Apache Drill",
                    "keyPath": "nf68-fmq7-kck8-z7mo-ls/zll7-y58a-aosv-aysf-06",
                    "key": "zll7-y58a-aosv-aysf-06",
                    "uuid": "zll7-y58a-aosv-aysf-06"
                }
            ],
            "key": "nf68-fmq7-kck8-z7mo-ls",
            "uuid": "nf68-fmq7-kck8-z7mo-ls",
            "keyPath": "nf68-fmq7-kck8-z7mo-ls",
            "category": "dsGroup",
            "imgUrl": "images/data_sources/defaut_datasource.png"
        },
        {
            "name": "Derby",
            "children": [
                {
                    "permissionLevel": 5,
                    "driver": "org.apache.derby.jdbc.ClientDriver",
                    "name": "GroovyPlainDerby",
                    "classifier": "efwd",
                    "type": "sql.jdbc.groovy",
                    "data": {
                        "dir": "Groovy_Plain",
                        "driverName": "org.apache.derby.jdbc.ClientDriver",
                        "type": "sql.jdbc.groovy",
                        "id": 4,
                        "userName": "hiuser&4&$%^#",
                        "password": "hiuser",
                        "condition": "import net.sf.json.JSONObject;\n    import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n    public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        responseJson.put(\"driver\", \"org.apache.derby.jdbc.ClientDriver\");\n        responseJson.put(\"url\", \"jdbc:derby://127.0.0.1:1527//home/helical/Performance/hi/db/SampleTravelData\");\n        responseJson.put(\"user\", \"hiuser\");\n        responseJson.put(\"pass\", \"hiuser\");\n        return responseJson;\n    }",
                        "jdbcUrl": "jdbc:derby://localhost:1527/derby"
                    },
                    "dataSourceType": "",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "lt23-apna-nykn-z0nd-42/kyyt-yop3-31ho-xfrc-0n",
                    "key": "kyyt-yop3-31ho-xfrc-0n",
                    "uuid": "kyyt-yop3-31ho-xfrc-0n"
                },
                {
                    "data": {
                        "id": "1",
                        "type": "dynamicDataSource"
                    },
                    "dataSourceProvider": "tomcat",
                    "type": "dynamicDataSource",
                    "permissionLevel": 5,
                    "driver": "org.apache.derby.jdbc.ClientDriver",
                    "name": "SampleTravelDataDerby",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource",
                    "category": "dataSource",
                    "children": [
                        {
                            "name": "SQLJ",
                            "children": [],
                            "keyPath": "lt23-apna-nykn-z0nd-42/r3hm-abpg-hlf4-n4ho-en/nhzp-mn8c-z2f1-xu4h-ih",
                            "key": "nhzp-mn8c-z2f1-xu4h-ih",
                            "uuid": "nhzp-mn8c-z2f1-xu4h-ih",
                            "data": {
                                "id": "1",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "SampleTravelDataDerby"
                        },
                        {
                            "name": "SYSFUN",
                            "children": [],
                            "keyPath": "lt23-apna-nykn-z0nd-42/r3hm-abpg-hlf4-n4ho-en/27zp-pxf0-xlx4-2ot7-bc",
                            "key": "27zp-pxf0-xlx4-2ot7-bc",
                            "uuid": "27zp-pxf0-xlx4-2ot7-bc",
                            "data": {
                                "id": "1",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "SampleTravelDataDerby"
                        },
                        {
                            "name": "SYSCAT",
                            "children": [],
                            "keyPath": "lt23-apna-nykn-z0nd-42/r3hm-abpg-hlf4-n4ho-en/m9r5-b9el-njma-cry1-q7",
                            "key": "m9r5-b9el-njma-cry1-q7",
                            "uuid": "m9r5-b9el-njma-cry1-q7",
                            "data": {
                                "id": "1",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "SampleTravelDataDerby"
                        },
                        {
                            "name": "HIUSER",
                            "children": [
                                {
                                    "id": "4ac5d9f68b58bd7c0d179146e46795be",
                                    "name": "dimdate",
                                    "data": {
                                        "id": "1",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "lt23-apna-nykn-z0nd-42/r3hm-abpg-hlf4-n4ho-en/kpwh-9t0d-uh7a-yfgk-f5/f5tj-b642-na2g-3l3i-j0",
                                    "key": "f5tj-b642-na2g-3l3i-j0",
                                    "alias": "dimdate",
                                    "uuid": "f5tj-b642-na2g-3l3i-j0",
                                    "connId": "4s8tz",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "4s8tz",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "lt23-apna-nykn-z0nd-42/r3hm-abpg-hlf4-n4ho-en/kpwh-9t0d-uh7a-yfgk-f5",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "dimdate_4s8tz",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "4e1fd245f4d13b77be423a43f01d80b2",
                                    "name": "employee_details",
                                    "data": {
                                        "id": "1",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "lt23-apna-nykn-z0nd-42/r3hm-abpg-hlf4-n4ho-en/kpwh-9t0d-uh7a-yfgk-f5/g1m5-wi6y-nfxz-xptv-do",
                                    "key": "g1m5-wi6y-nfxz-xptv-do",
                                    "alias": "employee_details",
                                    "uuid": "g1m5-wi6y-nfxz-xptv-do",
                                    "connId": "4s8tz",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "4s8tz",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "lt23-apna-nykn-z0nd-42/r3hm-abpg-hlf4-n4ho-en/kpwh-9t0d-uh7a-yfgk-f5",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "employee_details_4s8tz",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "be534112989b616b194bc59c2fb25a42",
                                    "name": "geo_cordinates",
                                    "data": {
                                        "id": "1",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "lt23-apna-nykn-z0nd-42/r3hm-abpg-hlf4-n4ho-en/kpwh-9t0d-uh7a-yfgk-f5/fr1s-9tqu-ryxv-c848-69",
                                    "key": "fr1s-9tqu-ryxv-c848-69",
                                    "alias": "geo_cordinates",
                                    "uuid": "fr1s-9tqu-ryxv-c848-69",
                                    "connId": "4s8tz",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "4s8tz",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "lt23-apna-nykn-z0nd-42/r3hm-abpg-hlf4-n4ho-en/kpwh-9t0d-uh7a-yfgk-f5",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "geo_cordinates_4s8tz",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "9645c648a1c0dbeec1287aaf1e996db3",
                                    "name": "meeting_details",
                                    "data": {
                                        "id": "1",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "lt23-apna-nykn-z0nd-42/r3hm-abpg-hlf4-n4ho-en/kpwh-9t0d-uh7a-yfgk-f5/sou4-nmc9-6cuf-y1wp-1r",
                                    "key": "sou4-nmc9-6cuf-y1wp-1r",
                                    "alias": "meeting_details",
                                    "uuid": "sou4-nmc9-6cuf-y1wp-1r",
                                    "connId": "4s8tz",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "4s8tz",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "lt23-apna-nykn-z0nd-42/r3hm-abpg-hlf4-n4ho-en/kpwh-9t0d-uh7a-yfgk-f5",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "meeting_details_4s8tz",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "8a28627d07d04ef096d9935f12e0c7e9",
                                    "name": "travel_details",
                                    "data": {
                                        "id": "1",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "lt23-apna-nykn-z0nd-42/r3hm-abpg-hlf4-n4ho-en/kpwh-9t0d-uh7a-yfgk-f5/o7wv-0rhq-nmn6-sgxx-s0",
                                    "key": "o7wv-0rhq-nmn6-sgxx-s0",
                                    "alias": "travel_details",
                                    "uuid": "o7wv-0rhq-nmn6-sgxx-s0",
                                    "connId": "4s8tz",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "4s8tz",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "lt23-apna-nykn-z0nd-42/r3hm-abpg-hlf4-n4ho-en/kpwh-9t0d-uh7a-yfgk-f5",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "travel_details_4s8tz",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                }
                            ],
                            "keyPath": "lt23-apna-nykn-z0nd-42/r3hm-abpg-hlf4-n4ho-en/kpwh-9t0d-uh7a-yfgk-f5",
                            "key": "kpwh-9t0d-uh7a-yfgk-f5",
                            "uuid": "kpwh-9t0d-uh7a-yfgk-f5",
                            "data": {
                                "id": "1",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "SampleTravelDataDerby",
                            "fetched": true
                        },
                        {
                            "name": "SYSCS_DIAG",
                            "children": [],
                            "keyPath": "lt23-apna-nykn-z0nd-42/r3hm-abpg-hlf4-n4ho-en/x36p-t2mm-xbe1-hbab-gx",
                            "key": "x36p-t2mm-xbe1-hbab-gx",
                            "uuid": "x36p-t2mm-xbe1-hbab-gx",
                            "data": {
                                "id": "1",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "SampleTravelDataDerby"
                        },
                        {
                            "name": "SYSCS_UTIL",
                            "children": [],
                            "keyPath": "lt23-apna-nykn-z0nd-42/r3hm-abpg-hlf4-n4ho-en/6j9c-ggrd-rycv-orzw-7e",
                            "key": "6j9c-ggrd-rycv-orzw-7e",
                            "uuid": "6j9c-ggrd-rycv-orzw-7e",
                            "data": {
                                "id": "1",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "SampleTravelDataDerby"
                        },
                        {
                            "name": "SYSIBM",
                            "children": [],
                            "keyPath": "lt23-apna-nykn-z0nd-42/r3hm-abpg-hlf4-n4ho-en/96ln-lzbg-c3r0-x763-sw",
                            "key": "96ln-lzbg-c3r0-x763-sw",
                            "uuid": "96ln-lzbg-c3r0-x763-sw",
                            "data": {
                                "id": "1",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "SampleTravelDataDerby"
                        },
                        {
                            "name": "APP",
                            "children": [],
                            "keyPath": "lt23-apna-nykn-z0nd-42/r3hm-abpg-hlf4-n4ho-en/dd7e-4sl8-l7l2-gmn3-1z",
                            "key": "dd7e-4sl8-l7l2-gmn3-1z",
                            "uuid": "dd7e-4sl8-l7l2-gmn3-1z",
                            "data": {
                                "id": "1",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "SampleTravelDataDerby"
                        },
                        {
                            "name": "NULLID",
                            "children": [],
                            "keyPath": "lt23-apna-nykn-z0nd-42/r3hm-abpg-hlf4-n4ho-en/8yoo-la3z-vxrl-z9o3-qx",
                            "key": "8yoo-la3z-vxrl-z9o3-qx",
                            "uuid": "8yoo-la3z-vxrl-z9o3-qx",
                            "data": {
                                "id": "1",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "SampleTravelDataDerby"
                        },
                        {
                            "name": "SYSPROC",
                            "children": [],
                            "keyPath": "lt23-apna-nykn-z0nd-42/r3hm-abpg-hlf4-n4ho-en/g2p2-fe2o-8j3c-13gb-2c",
                            "key": "g2p2-fe2o-8j3c-13gb-2c",
                            "uuid": "g2p2-fe2o-8j3c-13gb-2c",
                            "data": {
                                "id": "1",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "SampleTravelDataDerby"
                        },
                        {
                            "name": "SYS",
                            "children": [],
                            "keyPath": "lt23-apna-nykn-z0nd-42/r3hm-abpg-hlf4-n4ho-en/vyuv-yvco-9vuu-6p0g-iu",
                            "key": "vyuv-yvco-9vuu-6p0g-iu",
                            "uuid": "vyuv-yvco-9vuu-6p0g-iu",
                            "data": {
                                "id": "1",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "SampleTravelDataDerby"
                        },
                        {
                            "name": "SYSSTAT",
                            "children": [],
                            "keyPath": "lt23-apna-nykn-z0nd-42/r3hm-abpg-hlf4-n4ho-en/4xf5-n6ey-11oo-n28f-ho",
                            "key": "4xf5-n6ey-11oo-n28f-ho",
                            "uuid": "4xf5-n6ey-11oo-n28f-ho",
                            "data": {
                                "id": "1",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "SampleTravelDataDerby"
                        }
                    ],
                    "driverType": "Derby",
                    "keyPath": "lt23-apna-nykn-z0nd-42/r3hm-abpg-hlf4-n4ho-en",
                    "key": "r3hm-abpg-hlf4-n4ho-en",
                    "uuid": "r3hm-abpg-hlf4-n4ho-en",
                    "fetched": true
                },
                {
                    "data": {
                        "id": "1010",
                        "type": "dynamicDataSource"
                    },
                    "dataSourceProvider": "tomcat",
                    "type": "dynamicDataSource",
                    "permissionLevel": 5,
                    "driver": "org.apache.derby.jdbc.ClientDriver",
                    "name": "Derby",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "lt23-apna-nykn-z0nd-42/8q33-a7md-1ofg-grgc-d0",
                    "key": "8q33-a7md-1ofg-grgc-d0",
                    "uuid": "8q33-a7md-1ofg-grgc-d0"
                },
                {
                    "data": {
                        "id": "1011",
                        "type": "dynamicDataSource"
                    },
                    "dataSourceProvider": "tomcat",
                    "type": "dynamicDataSource",
                    "permissionLevel": 5,
                    "driver": "org.apache.derby.jdbc.ClientDriver",
                    "name": "3432derby",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "lt23-apna-nykn-z0nd-42/6wst-bctb-1aif-ay4t-96",
                    "key": "6wst-bctb-1aif-ay4t-96",
                    "uuid": "6wst-bctb-1aif-ay4t-96"
                }
            ],
            "key": "lt23-apna-nykn-z0nd-42",
            "uuid": "lt23-apna-nykn-z0nd-42",
            "keyPath": "lt23-apna-nykn-z0nd-42",
            "category": "dsGroup",
            "imgUrl": "images/data_sources/defaut_datasource.png"
        },
        {
            "name": "Mysql",
            "children": [
                {
                    "permissionLevel": 5,
                    "driver": "com.mysql.jdbc.Driver",
                    "name": "PlainJdbcMysql",
                    "classifier": "efwd",
                    "type": "sql.jdbc",
                    "data": {
                        "dir": "PlainJdbc",
                        "driverName": "com.mysql.jdbc.Driver",
                        "type": "sql.jdbc",
                        "id": 5,
                        "userName": "bugzilla&^(*)",
                        "password": "bugzilla@QA",
                        "jdbcUrl": "jdbc:mysql://localhost:3306/SampleTravelData"
                    },
                    "dataSourceType": "Plain Jdbc DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Mysql",
                    "keyPath": "4hej-dpvo-b48c-e64b-3n/205a-27uz-nghk-u3t0-wp",
                    "key": "205a-27uz-nghk-u3t0-wp",
                    "uuid": "205a-27uz-nghk-u3t0-wp"
                },
                {
                    "data": {
                        "id": "1000",
                        "type": "dynamicDataSource"
                    },
                    "dataSourceProvider": "tomcat",
                    "type": "dynamicDataSource",
                    "permissionLevel": 5,
                    "driver": "com.mysql.jdbc.Driver",
                    "name": "MYSQlSampleDS",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Mysql",
                    "keyPath": "4hej-dpvo-b48c-e64b-3n/3zq0-nuji-8igt-b1ws-5n",
                    "key": "3zq0-nuji-8igt-b1ws-5n",
                    "uuid": "3zq0-nuji-8igt-b1ws-5n"
                },
                {
                    "data": {
                        "id": "1001",
                        "type": "dynamicDataSource"
                    },
                    "dataSourceProvider": "tomcat",
                    "type": "dynamicDataSource",
                    "permissionLevel": 5,
                    "driver": "com.mysql.jdbc.Driver",
                    "name": "MYSQLSingleDumpDS",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Mysql",
                    "keyPath": "4hej-dpvo-b48c-e64b-3n/mx49-vdfb-m6z4-kw9v-8o",
                    "key": "mx49-vdfb-m6z4-kw9v-8o",
                    "uuid": "mx49-vdfb-m6z4-kw9v-8o"
                },
                {
                    "data": {
                        "id": "1002",
                        "type": "dynamicDataSource"
                    },
                    "dataSourceProvider": "tomcat",
                    "type": "dynamicDataSource",
                    "permissionLevel": 5,
                    "driver": "com.mysql.jdbc.Driver",
                    "name": "MYSQLMultiConnDumpDS",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Mysql",
                    "keyPath": "4hej-dpvo-b48c-e64b-3n/lu6t-sec1-ye4k-cihk-ju",
                    "key": "lu6t-sec1-ye4k-cihk-ju",
                    "uuid": "lu6t-sec1-ye4k-cihk-ju"
                },
                {
                    "data": {
                        "id": "1009",
                        "type": "dynamicDataSource"
                    },
                    "dataSourceProvider": "tomcat",
                    "type": "dynamicDataSource",
                    "permissionLevel": 5,
                    "driver": "com.mysql.jdbc.Driver",
                    "name": "BugzillaDBDS",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Mysql",
                    "keyPath": "4hej-dpvo-b48c-e64b-3n/rww9-0wke-6kds-44x1-di",
                    "key": "rww9-0wke-6kds-44x1-di",
                    "uuid": "rww9-0wke-6kds-44x1-di"
                }
            ],
            "key": "4hej-dpvo-b48c-e64b-3n",
            "uuid": "4hej-dpvo-b48c-e64b-3n",
            "keyPath": "4hej-dpvo-b48c-e64b-3n",
            "category": "dsGroup",
            "imgUrl": "images/data_sources/defaut_datasource.png"
        },
        {
            "name": "Postgresql",
            "children": [
                {
                    "permissionLevel": 5,
                    "driver": "org.postgresql.Driver",
                    "name": "PostgresPlainJDBC",
                    "classifier": "efwd",
                    "type": "sql.jdbc",
                    "data": {
                        "dir": "Export",
                        "driverName": "org.postgresql.Driver",
                        "type": "sql.jdbc",
                        "id": 6,
                        "userName": "postgres",
                        "password": "postgres",
                        "jdbcUrl": "jdbc:postgresql://127.0.0.1:5432/SampleTravelData"
                    },
                    "dataSourceType": "Plain Jdbc DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Postgresql",
                    "keyPath": "pgie-mkik-jghu-oipy-nx/v9mp-tbru-qgvm-291r-7o",
                    "key": "v9mp-tbru-qgvm-291r-7o",
                    "uuid": "v9mp-tbru-qgvm-291r-7o"
                },
                {
                    "data": {
                        "id": "1003",
                        "type": "dynamicDataSource"
                    },
                    "dataSourceProvider": "tomcat",
                    "type": "dynamicDataSource",
                    "permissionLevel": 5,
                    "driver": "org.postgresql.Driver",
                    "name": "PostgresSampleDS",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Postgresql",
                    "keyPath": "pgie-mkik-jghu-oipy-nx/u58j-ca4o-d28j-2t5r-wo",
                    "key": "u58j-ca4o-d28j-2t5r-wo",
                    "uuid": "u58j-ca4o-d28j-2t5r-wo"
                },
                {
                    "data": {
                        "id": "1004",
                        "type": "dynamicDataSource"
                    },
                    "dataSourceProvider": "tomcat",
                    "type": "dynamicDataSource",
                    "permissionLevel": 5,
                    "driver": "org.postgresql.Driver",
                    "name": "PostgresSingleDumpDS",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Postgresql",
                    "keyPath": "pgie-mkik-jghu-oipy-nx/umao-6ovb-4ecz-fm7r-d9",
                    "key": "umao-6ovb-4ecz-fm7r-d9",
                    "uuid": "umao-6ovb-4ecz-fm7r-d9"
                },
                {
                    "data": {
                        "id": "1005",
                        "type": "dynamicDataSource"
                    },
                    "dataSourceProvider": "tomcat",
                    "type": "dynamicDataSource",
                    "permissionLevel": 5,
                    "driver": "org.postgresql.Driver",
                    "name": "PostgresMultiConnDumpDS",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Postgresql",
                    "keyPath": "pgie-mkik-jghu-oipy-nx/jag1-seui-1dy1-n4r1-uv",
                    "key": "jag1-seui-1dy1-n4r1-uv",
                    "uuid": "jag1-seui-1dy1-n4r1-uv"
                }
            ],
            "key": "pgie-mkik-jghu-oipy-nx",
            "uuid": "pgie-mkik-jghu-oipy-nx",
            "keyPath": "pgie-mkik-jghu-oipy-nx",
            "category": "dsGroup",
            "imgUrl": "images/data_sources/defaut_datasource.png"
        },
        {
            "name": "Sqlite",
            "children": [
                {
                    "data": {
                        "id": "1006",
                        "type": "dynamicDataSource"
                    },
                    "dataSourceProvider": "tomcat",
                    "type": "dynamicDataSource",
                    "permissionLevel": 5,
                    "driver": "org.sqlite.JDBC",
                    "name": "SqliteSampleDS",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Sqlite",
                    "keyPath": "znxb-bls7-5xfi-0ke6-uc/jp3s-98w7-59dm-2x3r-5q",
                    "key": "jp3s-98w7-59dm-2x3r-5q",
                    "uuid": "jp3s-98w7-59dm-2x3r-5q"
                },
                {
                    "data": {
                        "id": "1007",
                        "type": "dynamicDataSource"
                    },
                    "dataSourceProvider": "tomcat",
                    "type": "dynamicDataSource",
                    "permissionLevel": 5,
                    "driver": "org.sqlite.JDBC",
                    "name": "SqliteSingleDumpDS",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Sqlite",
                    "keyPath": "znxb-bls7-5xfi-0ke6-uc/zdnq-vhkn-unxh-s73g-cy",
                    "key": "zdnq-vhkn-unxh-s73g-cy",
                    "uuid": "zdnq-vhkn-unxh-s73g-cy"
                }
            ],
            "key": "znxb-bls7-5xfi-0ke6-uc",
            "uuid": "znxb-bls7-5xfi-0ke6-uc",
            "keyPath": "znxb-bls7-5xfi-0ke6-uc",
            "category": "dsGroup",
            "imgUrl": "images/data_sources/defaut_datasource.png"
        }
    ],
    "workFlow": {
        "dataList": [
            {
                "fetchSchemas": true,
                "fetchCatalogs": true,
                "working": true,
                "connData": {
                    "id": "1",
                    "type": "dynamicDataSource"
                },
                "connId": "1",
                "dsUUID": "r3hm-abpg-hlf4-n4ho-en",
                "classifier": "db.workflow",
                "metadata": {
                    "catalogs": [
                        {
                            "name": "Null",
                            "schemas": [
                                {
                                    "name": "SQLJ"
                                },
                                {
                                    "name": "SYSFUN"
                                },
                                {
                                    "name": "SYSCAT"
                                },
                                {
                                    "name": "HIUSER"
                                },
                                {
                                    "name": "SYSCS_DIAG"
                                },
                                {
                                    "name": "SYSCS_UTIL"
                                },
                                {
                                    "name": "SYSIBM"
                                },
                                {
                                    "name": "APP"
                                },
                                {
                                    "name": "NULLID"
                                },
                                {
                                    "name": "SYSPROC"
                                },
                                {
                                    "name": "SYS"
                                },
                                {
                                    "name": "SYSSTAT"
                                }
                            ]
                        }
                    ]
                }
            },
            {
                "classifier": "db.workflow",
                "metadata": {
                    "catalogs": [
                        {
                            "name": "Null",
                            "schemas": [
                                {
                                    "name": "HIUSER",
                                    "tables": [
                                        {
                                            "id": "be534112989b616b194bc59c2fb25a42",
                                            "name": "geo_cordinates"
                                        },
                                        {
                                            "id": "9645c648a1c0dbeec1287aaf1e996db3",
                                            "name": "meeting_details"
                                        },
                                        {
                                            "id": "4e1fd245f4d13b77be423a43f01d80b2",
                                            "name": "employee_details"
                                        },
                                        {
                                            "id": "4ac5d9f68b58bd7c0d179146e46795be",
                                            "name": "dimdate"
                                        },
                                        {
                                            "id": "8a28627d07d04ef096d9935f12e0c7e9",
                                            "name": "travel_details"
                                        }
                                    ]
                                }
                            ]
                        }
                    ],
                    "dataSource": {
                        "id": "1",
                        "type": "dynamicDataSource",
                        "baseType": "global.jdbc",
                        "catSchemaPredicted": false,
                        "sync": false,
                        "catalog": "",
                        "schema": "HIUSER",
                        "connId": "4s8tz",
                        "classifier": "db.workflow",
                        "datasourceName": "SampleTravelDataDerby",
                        "dsKeyPath": "lt23-apna-nykn-z0nd-42/r3hm-abpg-hlf4-n4ho-en/kpwh-9t0d-uh7a-yfgk-f5",
                        "driverType": "Derby",
                        "database": "HIUSER"
                    },
                    "name": "HIUSER"
                }
            }
        ]
    },
    "dataSource": [
        {
            "id": "1",
            "type": "dynamicDataSource",
            "baseType": "global.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "",
            "schema": "HIUSER",
            "connId": "4s8tz",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelDataDerby",
            "dsKeyPath": "lt23-apna-nykn-z0nd-42/r3hm-abpg-hlf4-n4ho-en/kpwh-9t0d-uh7a-yfgk-f5",
            "driverType": "Derby",
            "database": "HIUSER"
        }
    ],
    "tables": {
        "dimdate": {
            "id": "4ac5d9f68b58bd7c0d179146e46795be",
            "name": "dimdate",
            "data": {
                "id": "1",
                "type": "dynamicDataSource"
            },
            "keyPath": "lt23-apna-nykn-z0nd-42/r3hm-abpg-hlf4-n4ho-en/kpwh-9t0d-uh7a-yfgk-f5/f5tj-b642-na2g-3l3i-j0",
            "key": "f5tj-b642-na2g-3l3i-j0",
            "alias": "dimdate",
            "uuid": "f5tj-b642-na2g-3l3i-j0",
            "connId": "4s8tz",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "4s8tz",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "lt23-apna-nykn-z0nd-42/r3hm-abpg-hlf4-n4ho-en/kpwh-9t0d-uh7a-yfgk-f5",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "dimdate_4s8tz",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "dimdate",
            "columns": {
                "dim_id": {
                    "alias": "dim_id",
                    "fullyQualifiedColumn": "dimdate.dim_id",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    },
                    "originalId": "53102859-8a31-4657-b67e-cc3ec9a08c78",
                    "_columnId": "53102859-8a31-4657-b67e-cc3ec9a08c78",
                    "columnId": "53102859-8a31-4657-b67e-cc3ec9a08c78"
                },
                "fiscal_year": {
                    "alias": "fiscal_year",
                    "fullyQualifiedColumn": "dimdate.fiscal_year",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.sql.Date": "date"
                    },
                    "originalId": "c2e64680-279a-4e7b-9bfe-8ff7c79cb9d8",
                    "_columnId": "c2e64680-279a-4e7b-9bfe-8ff7c79cb9d8",
                    "columnId": "c2e64680-279a-4e7b-9bfe-8ff7c79cb9d8"
                },
                "modified_date": {
                    "alias": "modified_date",
                    "fullyQualifiedColumn": "dimdate.modified_date",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.sql.Timestamp": "dateTime"
                    },
                    "originalId": "8680f59a-b07e-4ed2-a06f-333284f83fd4",
                    "_columnId": "8680f59a-b07e-4ed2-a06f-333284f83fd4",
                    "columnId": "8680f59a-b07e-4ed2-a06f-333284f83fd4"
                },
                "date_key": {
                    "alias": "date_key",
                    "fullyQualifiedColumn": "dimdate.date_key",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "originalId": "510db5cb-2418-4caa-9e81-eb8df5993671",
                    "_columnId": "510db5cb-2418-4caa-9e81-eb8df5993671",
                    "columnId": "510db5cb-2418-4caa-9e81-eb8df5993671"
                },
                "day_number": {
                    "alias": "day_number",
                    "fullyQualifiedColumn": "dimdate.day_number",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "originalId": "bea69334-5b46-4ebd-8d2b-473b2ea3796e",
                    "_columnId": "bea69334-5b46-4ebd-8d2b-473b2ea3796e",
                    "columnId": "bea69334-5b46-4ebd-8d2b-473b2ea3796e"
                },
                "fiscal_month_name": {
                    "alias": "fiscal_month_name",
                    "fullyQualifiedColumn": "dimdate.fiscal_month_name",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "originalId": "3b9698a0-52b0-489d-9abe-8eb8ba6a6e28",
                    "_columnId": "3b9698a0-52b0-489d-9abe-8eb8ba6a6e28",
                    "columnId": "3b9698a0-52b0-489d-9abe-8eb8ba6a6e28"
                },
                "fiscal_month_label": {
                    "alias": "fiscal_month_label",
                    "fullyQualifiedColumn": "dimdate.fiscal_month_label",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "originalId": "965378c7-ec1c-431c-bade-79bfdee528de",
                    "_columnId": "965378c7-ec1c-431c-bade-79bfdee528de",
                    "columnId": "965378c7-ec1c-431c-bade-79bfdee528de"
                },
                "created_date": {
                    "alias": "created_date",
                    "fullyQualifiedColumn": "dimdate.created_date",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "originalId": "e05ba08c-8be9-4b76-bf65-a1e0523c980f",
                    "_columnId": "e05ba08c-8be9-4b76-bf65-a1e0523c980f",
                    "columnId": "e05ba08c-8be9-4b76-bf65-a1e0523c980f"
                },
                "created_time": {
                    "alias": "created_time",
                    "fullyQualifiedColumn": "dimdate.created_time",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "originalId": "7108c290-4bdc-4332-a152-3d85e38701ad",
                    "_columnId": "7108c290-4bdc-4332-a152-3d85e38701ad",
                    "columnId": "7108c290-4bdc-4332-a152-3d85e38701ad"
                },
                "rating": {
                    "alias": "rating",
                    "fullyQualifiedColumn": "dimdate.rating",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "originalId": "864e6e48-749b-4119-a3fd-a87ab21f5125",
                    "_columnId": "864e6e48-749b-4119-a3fd-a87ab21f5125",
                    "columnId": "864e6e48-749b-4119-a3fd-a87ab21f5125"
                }
            }
        },
        "employee_details": {
            "id": "4e1fd245f4d13b77be423a43f01d80b2",
            "name": "employee_details",
            "data": {
                "id": "1",
                "type": "dynamicDataSource"
            },
            "keyPath": "lt23-apna-nykn-z0nd-42/r3hm-abpg-hlf4-n4ho-en/kpwh-9t0d-uh7a-yfgk-f5/g1m5-wi6y-nfxz-xptv-do",
            "key": "g1m5-wi6y-nfxz-xptv-do",
            "alias": "employee_details",
            "uuid": "g1m5-wi6y-nfxz-xptv-do",
            "connId": "4s8tz",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "4s8tz",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "lt23-apna-nykn-z0nd-42/r3hm-abpg-hlf4-n4ho-en/kpwh-9t0d-uh7a-yfgk-f5",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "employee_details_4s8tz",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "employee_details",
            "columns": {
                "employee_id": {
                    "alias": "employee_id",
                    "fullyQualifiedColumn": "employee_details.employee_id",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    },
                    "originalId": "86ef225f-e806-4baa-a959-9391d9b38079",
                    "_columnId": "86ef225f-e806-4baa-a959-9391d9b38079",
                    "columnId": "86ef225f-e806-4baa-a959-9391d9b38079"
                },
                "employee_name": {
                    "alias": "employee_name",
                    "fullyQualifiedColumn": "employee_details.employee_name",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "originalId": "d6fb7478-deec-4dc5-b915-c13d0cfa0fe5",
                    "_columnId": "d6fb7478-deec-4dc5-b915-c13d0cfa0fe5",
                    "columnId": "d6fb7478-deec-4dc5-b915-c13d0cfa0fe5"
                },
                "age": {
                    "alias": "age",
                    "fullyQualifiedColumn": "employee_details.age",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    },
                    "originalId": "5abd3c13-5f23-49c0-8d9f-4ee2d713b5e4",
                    "_columnId": "5abd3c13-5f23-49c0-8d9f-4ee2d713b5e4",
                    "columnId": "5abd3c13-5f23-49c0-8d9f-4ee2d713b5e4"
                },
                "address": {
                    "alias": "address",
                    "fullyQualifiedColumn": "employee_details.address",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "originalId": "c1169b38-9da2-4e05-b3ba-ed28665ed9a0",
                    "_columnId": "c1169b38-9da2-4e05-b3ba-ed28665ed9a0",
                    "columnId": "c1169b38-9da2-4e05-b3ba-ed28665ed9a0"
                }
            }
        },
        "geo_cordinates": {
            "id": "be534112989b616b194bc59c2fb25a42",
            "name": "geo_cordinates",
            "data": {
                "id": "1",
                "type": "dynamicDataSource"
            },
            "keyPath": "lt23-apna-nykn-z0nd-42/r3hm-abpg-hlf4-n4ho-en/kpwh-9t0d-uh7a-yfgk-f5/fr1s-9tqu-ryxv-c848-69",
            "key": "fr1s-9tqu-ryxv-c848-69",
            "alias": "geo_cordinates",
            "uuid": "fr1s-9tqu-ryxv-c848-69",
            "connId": "4s8tz",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "4s8tz",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "lt23-apna-nykn-z0nd-42/r3hm-abpg-hlf4-n4ho-en/kpwh-9t0d-uh7a-yfgk-f5",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "geo_cordinates_4s8tz",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "geo_cordinates",
            "columns": {
                "location_id": {
                    "alias": "location_id",
                    "fullyQualifiedColumn": "geo_cordinates.location_id",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    },
                    "originalId": "cf8ed06a-777e-43ab-87bd-737505166ede",
                    "_columnId": "cf8ed06a-777e-43ab-87bd-737505166ede",
                    "columnId": "cf8ed06a-777e-43ab-87bd-737505166ede"
                },
                "location": {
                    "alias": "location",
                    "fullyQualifiedColumn": "geo_cordinates.location",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "originalId": "12400a5d-67f7-4668-8dbb-714ef1b77cdc",
                    "_columnId": "12400a5d-67f7-4668-8dbb-714ef1b77cdc",
                    "columnId": "12400a5d-67f7-4668-8dbb-714ef1b77cdc"
                },
                "latitude": {
                    "alias": "latitude",
                    "fullyQualifiedColumn": "geo_cordinates.latitude",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Double": "numeric"
                    },
                    "originalId": "f38a7895-282a-40b9-8890-6d18542c1c04",
                    "_columnId": "f38a7895-282a-40b9-8890-6d18542c1c04",
                    "columnId": "f38a7895-282a-40b9-8890-6d18542c1c04"
                },
                "longitude": {
                    "alias": "longitude",
                    "fullyQualifiedColumn": "geo_cordinates.longitude",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Double": "numeric"
                    },
                    "originalId": "ca9c83ca-0609-4457-a9e8-7dcfce0eebc8",
                    "_columnId": "ca9c83ca-0609-4457-a9e8-7dcfce0eebc8",
                    "columnId": "ca9c83ca-0609-4457-a9e8-7dcfce0eebc8"
                }
            }
        },
        "meeting_details": {
            "id": "9645c648a1c0dbeec1287aaf1e996db3",
            "name": "meeting_details",
            "data": {
                "id": "1",
                "type": "dynamicDataSource"
            },
            "keyPath": "lt23-apna-nykn-z0nd-42/r3hm-abpg-hlf4-n4ho-en/kpwh-9t0d-uh7a-yfgk-f5/sou4-nmc9-6cuf-y1wp-1r",
            "key": "sou4-nmc9-6cuf-y1wp-1r",
            "alias": "meeting_details",
            "uuid": "sou4-nmc9-6cuf-y1wp-1r",
            "connId": "4s8tz",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "4s8tz",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "lt23-apna-nykn-z0nd-42/r3hm-abpg-hlf4-n4ho-en/kpwh-9t0d-uh7a-yfgk-f5",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "meeting_details_4s8tz",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "meeting_details",
            "columns": {
                "meeting_id": {
                    "alias": "meeting_id",
                    "fullyQualifiedColumn": "meeting_details.meeting_id",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    },
                    "originalId": "dd65d40e-ae4e-45f5-ab07-b0c8eed19eda",
                    "_columnId": "dd65d40e-ae4e-45f5-ab07-b0c8eed19eda",
                    "columnId": "dd65d40e-ae4e-45f5-ab07-b0c8eed19eda"
                },
                "meeting_date": {
                    "alias": "meeting_date",
                    "fullyQualifiedColumn": "meeting_details.meeting_date",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.sql.Timestamp": "dateTime"
                    },
                    "originalId": "090b1dbe-5ed8-48fa-81f8-50c9999f8342",
                    "_columnId": "090b1dbe-5ed8-48fa-81f8-50c9999f8342",
                    "columnId": "090b1dbe-5ed8-48fa-81f8-50c9999f8342"
                },
                "meeting_by": {
                    "alias": "meeting_by",
                    "fullyQualifiedColumn": "meeting_details.meeting_by",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    },
                    "originalId": "171c40fb-9568-4c8d-9c37-041941f8d0bb",
                    "_columnId": "171c40fb-9568-4c8d-9c37-041941f8d0bb",
                    "columnId": "171c40fb-9568-4c8d-9c37-041941f8d0bb"
                },
                "client_name": {
                    "alias": "client_name",
                    "fullyQualifiedColumn": "meeting_details.client_name",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "originalId": "623fa28f-600e-45fa-aeee-1bacab96f419",
                    "_columnId": "623fa28f-600e-45fa-aeee-1bacab96f419",
                    "columnId": "623fa28f-600e-45fa-aeee-1bacab96f419"
                },
                "meeting_purpose": {
                    "alias": "meeting_purpose",
                    "fullyQualifiedColumn": "meeting_details.meeting_purpose",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "originalId": "cc4f5aab-f364-4ba7-8e82-c1a7961b6fec",
                    "_columnId": "cc4f5aab-f364-4ba7-8e82-c1a7961b6fec",
                    "columnId": "cc4f5aab-f364-4ba7-8e82-c1a7961b6fec"
                },
                "meeting_impact": {
                    "alias": "meeting_impact",
                    "fullyQualifiedColumn": "meeting_details.meeting_impact",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "originalId": "7895fd23-b442-4ba4-a5b2-19131420e521",
                    "_columnId": "7895fd23-b442-4ba4-a5b2-19131420e521",
                    "columnId": "7895fd23-b442-4ba4-a5b2-19131420e521"
                },
                "meet_cancellation_status": {
                    "alias": "meet_cancellation_status",
                    "fullyQualifiedColumn": "meeting_details.meet_cancellation_status",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "originalId": "f53269fd-7cc5-4eee-80f6-9d862c5a38c5",
                    "_columnId": "f53269fd-7cc5-4eee-80f6-9d862c5a38c5",
                    "columnId": "f53269fd-7cc5-4eee-80f6-9d862c5a38c5"
                },
                "cancellation_reason": {
                    "alias": "cancellation_reason",
                    "fullyQualifiedColumn": "meeting_details.cancellation_reason",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "originalId": "4e58b89f-14fa-4f26-8cbe-376dfda80e5d",
                    "_columnId": "4e58b89f-14fa-4f26-8cbe-376dfda80e5d",
                    "columnId": "4e58b89f-14fa-4f26-8cbe-376dfda80e5d"
                }
            }
        },
        "travel_details": {
            "id": "8a28627d07d04ef096d9935f12e0c7e9",
            "name": "travel_details",
            "data": {
                "id": "1",
                "type": "dynamicDataSource"
            },
            "keyPath": "lt23-apna-nykn-z0nd-42/r3hm-abpg-hlf4-n4ho-en/kpwh-9t0d-uh7a-yfgk-f5/o7wv-0rhq-nmn6-sgxx-s0",
            "key": "o7wv-0rhq-nmn6-sgxx-s0",
            "alias": "travel_details",
            "uuid": "o7wv-0rhq-nmn6-sgxx-s0",
            "connId": "4s8tz",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "4s8tz",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "lt23-apna-nykn-z0nd-42/r3hm-abpg-hlf4-n4ho-en/kpwh-9t0d-uh7a-yfgk-f5",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "travel_details_4s8tz",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "travel_details",
            "columns": {
                "travel_id": {
                    "alias": "travel_id",
                    "fullyQualifiedColumn": "travel_details.travel_id",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    },
                    "originalId": "69e811b1-187b-4165-8f52-2f74aef03c85",
                    "_columnId": "69e811b1-187b-4165-8f52-2f74aef03c85",
                    "columnId": "69e811b1-187b-4165-8f52-2f74aef03c85"
                },
                "travel_date": {
                    "alias": "travel_date",
                    "fullyQualifiedColumn": "travel_details.travel_date",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.sql.Timestamp": "dateTime"
                    },
                    "originalId": "8f561546-9344-4fbf-9553-772402abf2b3",
                    "_columnId": "8f561546-9344-4fbf-9553-772402abf2b3",
                    "columnId": "8f561546-9344-4fbf-9553-772402abf2b3"
                },
                "travel_type": {
                    "alias": "travel_type",
                    "fullyQualifiedColumn": "travel_details.travel_type",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "originalId": "480a6dde-c0b5-4e36-9fcc-e8c922ca6624",
                    "_columnId": "480a6dde-c0b5-4e36-9fcc-e8c922ca6624",
                    "columnId": "480a6dde-c0b5-4e36-9fcc-e8c922ca6624"
                },
                "travel_medium": {
                    "alias": "travel_medium",
                    "fullyQualifiedColumn": "travel_details.travel_medium",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "originalId": "4c534025-582d-4e5a-ac97-d4ca9918510a",
                    "_columnId": "4c534025-582d-4e5a-ac97-d4ca9918510a",
                    "columnId": "4c534025-582d-4e5a-ac97-d4ca9918510a"
                },
                "source_id": {
                    "alias": "source_id",
                    "fullyQualifiedColumn": "travel_details.source_id",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    },
                    "originalId": "43c60d70-369e-48bf-a611-588b789a4321",
                    "_columnId": "43c60d70-369e-48bf-a611-588b789a4321",
                    "columnId": "43c60d70-369e-48bf-a611-588b789a4321"
                },
                "source": {
                    "alias": "source",
                    "fullyQualifiedColumn": "travel_details.source",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "originalId": "9d14be8d-2cc7-4990-915b-42a499e52a2b",
                    "_columnId": "9d14be8d-2cc7-4990-915b-42a499e52a2b",
                    "columnId": "9d14be8d-2cc7-4990-915b-42a499e52a2b"
                },
                "destination_id": {
                    "alias": "destination_id",
                    "fullyQualifiedColumn": "travel_details.destination_id",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    },
                    "originalId": "6088515a-7480-4689-908f-8671db7bfef3",
                    "_columnId": "6088515a-7480-4689-908f-8671db7bfef3",
                    "columnId": "6088515a-7480-4689-908f-8671db7bfef3"
                },
                "destination": {
                    "alias": "destination",
                    "fullyQualifiedColumn": "travel_details.destination",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "originalId": "0fba2239-20fe-4d41-b487-24b65f22df0d",
                    "_columnId": "0fba2239-20fe-4d41-b487-24b65f22df0d",
                    "columnId": "0fba2239-20fe-4d41-b487-24b65f22df0d"
                },
                "travel_cost": {
                    "alias": "travel_cost",
                    "fullyQualifiedColumn": "travel_details.travel_cost",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    },
                    "originalId": "76cb629f-3892-41cc-bc2a-e55898350e3b",
                    "_columnId": "76cb629f-3892-41cc-bc2a-e55898350e3b",
                    "columnId": "76cb629f-3892-41cc-bc2a-e55898350e3b"
                },
                "mode_of_payment": {
                    "alias": "mode_of_payment",
                    "fullyQualifiedColumn": "travel_details.mode_of_payment",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "originalId": "0a2d4f4e-98df-4ac9-8cbc-38b76610220d",
                    "_columnId": "0a2d4f4e-98df-4ac9-8cbc-38b76610220d",
                    "columnId": "0a2d4f4e-98df-4ac9-8cbc-38b76610220d"
                },
                "booking_platform": {
                    "alias": "booking_platform",
                    "fullyQualifiedColumn": "travel_details.booking_platform",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "originalId": "108ce016-b8a0-42fa-b4e6-b4501af81477",
                    "_columnId": "108ce016-b8a0-42fa-b4e6-b4501af81477",
                    "columnId": "108ce016-b8a0-42fa-b4e6-b4501af81477"
                },
                "travelled_by": {
                    "alias": "travelled_by",
                    "fullyQualifiedColumn": "travel_details.travelled_by",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    },
                    "originalId": "7a7e52fa-ee28-4b7b-8c77-cdc939564900",
                    "_columnId": "7a7e52fa-ee28-4b7b-8c77-cdc939564900",
                    "columnId": "7a7e52fa-ee28-4b7b-8c77-cdc939564900"
                }
            }
        }
    },
    "views": [],
    "activeView": false,
    "categories": {
        "nf68-fmq7-kck8-z7mo-ls": {
            "ds": {
                "data": {
                    "id": "1008",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "org.apache.drill.jdbc.Driver",
                "name": "ApacheDrillSampleDS",
                "classifier": "global",
                "dataSourceType": "Managed DataSource"
            },
            "category": {
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
            }
        },
        "lt23-apna-nykn-z0nd-42": {
            "ds": {
                "permissionLevel": 5,
                "driver": "org.apache.derby.jdbc.ClientDriver",
                "name": "GroovyPlainDerby",
                "classifier": "efwd",
                "type": "sql.jdbc.groovy",
                "data": {
                    "dir": "Groovy_Plain",
                    "driverName": "org.apache.derby.jdbc.ClientDriver",
                    "type": "sql.jdbc.groovy",
                    "id": 4,
                    "userName": "hiuser&4&$%^#",
                    "password": "hiuser",
                    "condition": "import net.sf.json.JSONObject;\n    import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n    public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        responseJson.put(\"driver\", \"org.apache.derby.jdbc.ClientDriver\");\n        responseJson.put(\"url\", \"jdbc:derby://127.0.0.1:1527//home/helical/Performance/hi/db/SampleTravelData\");\n        responseJson.put(\"user\", \"hiuser\");\n        responseJson.put(\"pass\", \"hiuser\");\n        return responseJson;\n    }",
                    "jdbcUrl": "jdbc:derby://localhost:1527/derby"
                },
                "dataSourceType": ""
            },
            "category": {
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
            }
        },
        "4hej-dpvo-b48c-e64b-3n": {
            "ds": {
                "permissionLevel": 5,
                "driver": "com.mysql.jdbc.Driver",
                "name": "PlainJdbcMysql",
                "classifier": "efwd",
                "type": "sql.jdbc",
                "data": {
                    "dir": "PlainJdbc",
                    "driverName": "com.mysql.jdbc.Driver",
                    "type": "sql.jdbc",
                    "id": 5,
                    "userName": "bugzilla&^(*)",
                    "password": "bugzilla@QA",
                    "jdbcUrl": "jdbc:mysql://localhost:3306/SampleTravelData"
                },
                "dataSourceType": "Plain Jdbc DataSource"
            },
            "category": {
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
            }
        },
        "pgie-mkik-jghu-oipy-nx": {
            "ds": {
                "permissionLevel": 5,
                "driver": "org.postgresql.Driver",
                "name": "PostgresPlainJDBC",
                "classifier": "efwd",
                "type": "sql.jdbc",
                "data": {
                    "dir": "Export",
                    "driverName": "org.postgresql.Driver",
                    "type": "sql.jdbc",
                    "id": 6,
                    "userName": "postgres",
                    "password": "postgres",
                    "jdbcUrl": "jdbc:postgresql://127.0.0.1:5432/SampleTravelData"
                },
                "dataSourceType": "Plain Jdbc DataSource"
            },
            "category": {
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
            }
        },
        "znxb-bls7-5xfi-0ke6-uc": {
            "ds": {
                "data": {
                    "id": "1006",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "org.sqlite.JDBC",
                "name": "SqliteSampleDS",
                "classifier": "global",
                "dataSourceType": "Managed DataSource"
            },
            "category": {
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
            }
        }
    },
    "activeEditorTab": "joins",
    "dataSourcesAddedToMetadata": [
        {
            "id": "1",
            "type": "dynamicDataSource",
            "baseType": "global.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "",
            "schema": "HIUSER",
            "connId": "4s8tz",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelDataDerby",
            "dsKeyPath": "lt23-apna-nykn-z0nd-42/r3hm-abpg-hlf4-n4ho-en/kpwh-9t0d-uh7a-yfgk-f5",
            "driverType": "Derby",
            "database": "HIUSER"
        }
    ],
    "changeDSList": {},
    "changedTables": [],
    "changedColumns": [],
    "removedTables": [],
    "removedColumns": [],
    "removedDataSources": [],
    "duplicateColumnList": [],
    "duplicateTableList": [],
    "unsavedViews": [],
    "saveDetails": {
        "location": "Gagan",
        "uuid": "Metadata_1_14.metadata"
    },
    "savedTableIds": [
        "4ac5d9f68b58bd7c0d179146e46795be",
        "4e1fd245f4d13b77be423a43f01d80b2",
        "be534112989b616b194bc59c2fb25a42",
        "9645c648a1c0dbeec1287aaf1e996db3",
        "8a28627d07d04ef096d9935f12e0c7e9"
    ],
    "savedColumnIds": [
        {
            "columnId": "53102859-8a31-4657-b67e-cc3ec9a08c78",
            "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
        },
        {
            "columnId": "c2e64680-279a-4e7b-9bfe-8ff7c79cb9d8",
            "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
        },
        {
            "columnId": "8680f59a-b07e-4ed2-a06f-333284f83fd4",
            "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
        },
        {
            "columnId": "510db5cb-2418-4caa-9e81-eb8df5993671",
            "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
        },
        {
            "columnId": "bea69334-5b46-4ebd-8d2b-473b2ea3796e",
            "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
        },
        {
            "columnId": "3b9698a0-52b0-489d-9abe-8eb8ba6a6e28",
            "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
        },
        {
            "columnId": "965378c7-ec1c-431c-bade-79bfdee528de",
            "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
        },
        {
            "columnId": "e05ba08c-8be9-4b76-bf65-a1e0523c980f",
            "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
        },
        {
            "columnId": "7108c290-4bdc-4332-a152-3d85e38701ad",
            "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
        },
        {
            "columnId": "864e6e48-749b-4119-a3fd-a87ab21f5125",
            "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
        },
        {
            "columnId": "86ef225f-e806-4baa-a959-9391d9b38079",
            "tableId": "4e1fd245f4d13b77be423a43f01d80b2"
        },
        {
            "columnId": "d6fb7478-deec-4dc5-b915-c13d0cfa0fe5",
            "tableId": "4e1fd245f4d13b77be423a43f01d80b2"
        },
        {
            "columnId": "5abd3c13-5f23-49c0-8d9f-4ee2d713b5e4",
            "tableId": "4e1fd245f4d13b77be423a43f01d80b2"
        },
        {
            "columnId": "c1169b38-9da2-4e05-b3ba-ed28665ed9a0",
            "tableId": "4e1fd245f4d13b77be423a43f01d80b2"
        },
        {
            "columnId": "cf8ed06a-777e-43ab-87bd-737505166ede",
            "tableId": "be534112989b616b194bc59c2fb25a42"
        },
        {
            "columnId": "12400a5d-67f7-4668-8dbb-714ef1b77cdc",
            "tableId": "be534112989b616b194bc59c2fb25a42"
        },
        {
            "columnId": "f38a7895-282a-40b9-8890-6d18542c1c04",
            "tableId": "be534112989b616b194bc59c2fb25a42"
        },
        {
            "columnId": "ca9c83ca-0609-4457-a9e8-7dcfce0eebc8",
            "tableId": "be534112989b616b194bc59c2fb25a42"
        },
        {
            "columnId": "dd65d40e-ae4e-45f5-ab07-b0c8eed19eda",
            "tableId": "9645c648a1c0dbeec1287aaf1e996db3"
        },
        {
            "columnId": "090b1dbe-5ed8-48fa-81f8-50c9999f8342",
            "tableId": "9645c648a1c0dbeec1287aaf1e996db3"
        },
        {
            "columnId": "171c40fb-9568-4c8d-9c37-041941f8d0bb",
            "tableId": "9645c648a1c0dbeec1287aaf1e996db3"
        },
        {
            "columnId": "623fa28f-600e-45fa-aeee-1bacab96f419",
            "tableId": "9645c648a1c0dbeec1287aaf1e996db3"
        },
        {
            "columnId": "cc4f5aab-f364-4ba7-8e82-c1a7961b6fec",
            "tableId": "9645c648a1c0dbeec1287aaf1e996db3"
        },
        {
            "columnId": "7895fd23-b442-4ba4-a5b2-19131420e521",
            "tableId": "9645c648a1c0dbeec1287aaf1e996db3"
        },
        {
            "columnId": "f53269fd-7cc5-4eee-80f6-9d862c5a38c5",
            "tableId": "9645c648a1c0dbeec1287aaf1e996db3"
        },
        {
            "columnId": "4e58b89f-14fa-4f26-8cbe-376dfda80e5d",
            "tableId": "9645c648a1c0dbeec1287aaf1e996db3"
        },
        {
            "columnId": "69e811b1-187b-4165-8f52-2f74aef03c85",
            "tableId": "8a28627d07d04ef096d9935f12e0c7e9"
        },
        {
            "columnId": "8f561546-9344-4fbf-9553-772402abf2b3",
            "tableId": "8a28627d07d04ef096d9935f12e0c7e9"
        },
        {
            "columnId": "480a6dde-c0b5-4e36-9fcc-e8c922ca6624",
            "tableId": "8a28627d07d04ef096d9935f12e0c7e9"
        },
        {
            "columnId": "4c534025-582d-4e5a-ac97-d4ca9918510a",
            "tableId": "8a28627d07d04ef096d9935f12e0c7e9"
        },
        {
            "columnId": "43c60d70-369e-48bf-a611-588b789a4321",
            "tableId": "8a28627d07d04ef096d9935f12e0c7e9"
        },
        {
            "columnId": "9d14be8d-2cc7-4990-915b-42a499e52a2b",
            "tableId": "8a28627d07d04ef096d9935f12e0c7e9"
        },
        {
            "columnId": "6088515a-7480-4689-908f-8671db7bfef3",
            "tableId": "8a28627d07d04ef096d9935f12e0c7e9"
        },
        {
            "columnId": "0fba2239-20fe-4d41-b487-24b65f22df0d",
            "tableId": "8a28627d07d04ef096d9935f12e0c7e9"
        },
        {
            "columnId": "76cb629f-3892-41cc-bc2a-e55898350e3b",
            "tableId": "8a28627d07d04ef096d9935f12e0c7e9"
        },
        {
            "columnId": "0a2d4f4e-98df-4ac9-8cbc-38b76610220d",
            "tableId": "8a28627d07d04ef096d9935f12e0c7e9"
        },
        {
            "columnId": "108ce016-b8a0-42fa-b4e6-b4501af81477",
            "tableId": "8a28627d07d04ef096d9935f12e0c7e9"
        },
        {
            "columnId": "7a7e52fa-ee28-4b7b-8c77-cdc939564900",
            "tableId": "8a28627d07d04ef096d9935f12e0c7e9"
        }
    ],
    "joins": [
        {
            "id": "ca21d00c8c87263dedd812f8f74c05b5",
            "type": "inner",
            "operator": "=",
            "left": {
                "table": "geo_cordinates",
                "column": "location_id",
                "alias": "geo_cordinates.location_id",
                "dataSource": {
                    "sync": false,
                    "id": "1",
                    "connectionDatabaseId": "",
                    "catSchemaPredicted": false,
                    "catalog": "",
                    "schema": "HIUSER",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc"
                }
            },
            "right": {
                "table": "dimdate",
                "column": "dim_id",
                "alias": "dimdate.dim_id",
                "dataSource": {
                    "sync": false,
                    "id": "1",
                    "connectionDatabaseId": "",
                    "catSchemaPredicted": false,
                    "catalog": "",
                    "schema": "HIUSER",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc"
                }
            },
            "action": "noChange",
            "uuid": "ca21d00c8c87263dedd812f8f74c05b5",
            "key": "ca21d00c8c87263dedd812f8f74c05b5",
            "index": 1
        },
        {
            "id": "af8f3186af3703a70a3d6e219faafb4e",
            "type": "inner",
            "operator": "=",
            "left": {
                "table": "employee_details",
                "column": "employee_id",
                "alias": "employee_details.employee_id",
                "dataSource": {
                    "sync": false,
                    "id": "1",
                    "connectionDatabaseId": "",
                    "catSchemaPredicted": false,
                    "catalog": "",
                    "schema": "HIUSER",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc"
                }
            },
            "right": {
                "table": "meeting_details",
                "column": "meeting_by",
                "alias": "meeting_details.meeting_by",
                "dataSource": {
                    "sync": false,
                    "id": "1",
                    "connectionDatabaseId": "",
                    "catSchemaPredicted": false,
                    "catalog": "",
                    "schema": "HIUSER",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc"
                }
            },
            "action": "noChange",
            "uuid": "af8f3186af3703a70a3d6e219faafb4e",
            "key": "af8f3186af3703a70a3d6e219faafb4e",
            "index": 2
        },
        {
            "id": "aab02b68e2c7febf125c50c8c5175037",
            "type": "inner",
            "operator": "=",
            "left": {
                "table": "employee_details",
                "column": "employee_id",
                "alias": "employee_details.employee_id",
                "dataSource": {
                    "sync": false,
                    "id": "1",
                    "connectionDatabaseId": "",
                    "catSchemaPredicted": false,
                    "catalog": "",
                    "schema": "HIUSER",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc"
                }
            },
            "right": {
                "table": "travel_details",
                "column": "travelled_by",
                "alias": "travel_details.travelled_by",
                "dataSource": {
                    "sync": false,
                    "id": "1",
                    "connectionDatabaseId": "",
                    "catSchemaPredicted": false,
                    "catalog": "",
                    "schema": "HIUSER",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc"
                }
            },
            "action": "noChange",
            "uuid": "aab02b68e2c7febf125c50c8c5175037",
            "key": "aab02b68e2c7febf125c50c8c5175037",
            "index": 3
        },
        {
            "id": "daa3221b04c18670d4af25ac99f3ae76",
            "type": "inner",
            "operator": "=",
            "left": {
                "table": "geo_cordinates",
                "column": "location_id",
                "alias": "geo_cordinates.location_id",
                "dataSource": {
                    "sync": false,
                    "id": "1",
                    "connectionDatabaseId": "",
                    "catSchemaPredicted": false,
                    "catalog": "",
                    "schema": "HIUSER",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc"
                }
            },
            "right": {
                "table": "travel_details",
                "column": "destination_id",
                "alias": "travel_details.destination_id",
                "dataSource": {
                    "sync": false,
                    "id": "1",
                    "connectionDatabaseId": "",
                    "catSchemaPredicted": false,
                    "catalog": "",
                    "schema": "HIUSER",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc"
                }
            },
            "action": "noChange",
            "uuid": "daa3221b04c18670d4af25ac99f3ae76",
            "key": "daa3221b04c18670d4af25ac99f3ae76",
            "index": 4
        },
        {
            "id": "cdeb5b19799c89335f23ed9b50cc5a22",
            "type": "inner",
            "operator": "=",
            "left": {
                "table": "geo_cordinates",
                "column": "location_id",
                "alias": "geo_cordinates.location_id",
                "dataSource": {
                    "sync": false,
                    "id": "1",
                    "connectionDatabaseId": "",
                    "catSchemaPredicted": false,
                    "catalog": "",
                    "schema": "HIUSER",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc"
                }
            },
            "right": {
                "table": "travel_details",
                "column": "source_id",
                "alias": "travel_details.source_id",
                "dataSource": {
                    "sync": false,
                    "id": "1",
                    "connectionDatabaseId": "",
                    "catSchemaPredicted": false,
                    "catalog": "",
                    "schema": "HIUSER",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc"
                }
            },
            "action": "noChange",
            "uuid": "cdeb5b19799c89335f23ed9b50cc5a22",
            "key": "cdeb5b19799c89335f23ed9b50cc5a22",
            "index": 5
        }
    ],
    "mode": "create",
    "allTablesKeys": [
        "fr1s-9tqu-ryxv-c848-69",
        "sou4-nmc9-6cuf-y1wp-1r",
        "g1m5-wi6y-nfxz-xptv-do",
        "f5tj-b642-na2g-3l3i-j0",
        "o7wv-0rhq-nmn6-sgxx-s0"
    ],
    "selectedTableKeys": [
        "f5tj-b642-na2g-3l3i-j0",
        "g1m5-wi6y-nfxz-xptv-do",
        "fr1s-9tqu-ryxv-c848-69",
        "sou4-nmc9-6cuf-y1wp-1r",
        "o7wv-0rhq-nmn6-sgxx-s0"
    ],
    "metadataName": "Metadata_1",
    "activeDataSource": false,
    "metadataToEdit": false,
    "isSavingInProgress": false,
    "editViewsTempData": {},
    "inititalStateFromJest": false,
    "timeStamp": 1670843102115,
    "initialEditResponse": {
        "classifier": "db.generic",
        "name": "HIUSER",
        "dataSource": {
            "sync": false,
            "id": "1",
            "connectionDatabaseId": "",
            "catSchemaPredicted": false,
            "catalog": "",
            "schema": "HIUSER",
            "type": "dynamicDataSource",
            "baseType": "global.jdbc"
        },
        "uniqueId": "Metadata_1_14",
        "tables": {
            "dimdate": {
                "id": "4ac5d9f68b58bd7c0d179146e46795be",
                "alias": "dimdate",
                "columns": {
                    "dim_id": {
                        "alias": "dim_id",
                        "fullyQualifiedColumn": "dimdate.dim_id",
                        "columnId": "53102859-8a31-4657-b67e-cc3ec9a08c78",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "fiscal_year": {
                        "alias": "fiscal_year",
                        "fullyQualifiedColumn": "dimdate.fiscal_year",
                        "columnId": "c2e64680-279a-4e7b-9bfe-8ff7c79cb9d8",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.sql.Date": "date"
                        }
                    },
                    "modified_date": {
                        "alias": "modified_date",
                        "fullyQualifiedColumn": "dimdate.modified_date",
                        "columnId": "8680f59a-b07e-4ed2-a06f-333284f83fd4",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.sql.Timestamp": "dateTime"
                        }
                    },
                    "date_key": {
                        "alias": "date_key",
                        "fullyQualifiedColumn": "dimdate.date_key",
                        "columnId": "510db5cb-2418-4caa-9e81-eb8df5993671",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "day_number": {
                        "alias": "day_number",
                        "fullyQualifiedColumn": "dimdate.day_number",
                        "columnId": "bea69334-5b46-4ebd-8d2b-473b2ea3796e",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "fiscal_month_name": {
                        "alias": "fiscal_month_name",
                        "fullyQualifiedColumn": "dimdate.fiscal_month_name",
                        "columnId": "3b9698a0-52b0-489d-9abe-8eb8ba6a6e28",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "fiscal_month_label": {
                        "alias": "fiscal_month_label",
                        "fullyQualifiedColumn": "dimdate.fiscal_month_label",
                        "columnId": "965378c7-ec1c-431c-bade-79bfdee528de",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "created_date": {
                        "alias": "created_date",
                        "fullyQualifiedColumn": "dimdate.created_date",
                        "columnId": "e05ba08c-8be9-4b76-bf65-a1e0523c980f",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "created_time": {
                        "alias": "created_time",
                        "fullyQualifiedColumn": "dimdate.created_time",
                        "columnId": "7108c290-4bdc-4332-a152-3d85e38701ad",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "rating": {
                        "alias": "rating",
                        "fullyQualifiedColumn": "dimdate.rating",
                        "columnId": "864e6e48-749b-4119-a3fd-a87ab21f5125",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    }
                },
                "name": "dimdate"
            },
            "employee_details": {
                "id": "4e1fd245f4d13b77be423a43f01d80b2",
                "alias": "employee_details",
                "columns": {
                    "employee_id": {
                        "alias": "employee_id",
                        "fullyQualifiedColumn": "employee_details.employee_id",
                        "columnId": "86ef225f-e806-4baa-a959-9391d9b38079",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "employee_name": {
                        "alias": "employee_name",
                        "fullyQualifiedColumn": "employee_details.employee_name",
                        "columnId": "d6fb7478-deec-4dc5-b915-c13d0cfa0fe5",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "age": {
                        "alias": "age",
                        "fullyQualifiedColumn": "employee_details.age",
                        "columnId": "5abd3c13-5f23-49c0-8d9f-4ee2d713b5e4",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "address": {
                        "alias": "address",
                        "fullyQualifiedColumn": "employee_details.address",
                        "columnId": "c1169b38-9da2-4e05-b3ba-ed28665ed9a0",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    }
                },
                "name": "employee_details"
            },
            "geo_cordinates": {
                "id": "be534112989b616b194bc59c2fb25a42",
                "alias": "geo_cordinates",
                "columns": {
                    "location_id": {
                        "alias": "location_id",
                        "fullyQualifiedColumn": "geo_cordinates.location_id",
                        "columnId": "cf8ed06a-777e-43ab-87bd-737505166ede",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "location": {
                        "alias": "location",
                        "fullyQualifiedColumn": "geo_cordinates.location",
                        "columnId": "12400a5d-67f7-4668-8dbb-714ef1b77cdc",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "latitude": {
                        "alias": "latitude",
                        "fullyQualifiedColumn": "geo_cordinates.latitude",
                        "columnId": "f38a7895-282a-40b9-8890-6d18542c1c04",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Double": "numeric"
                        }
                    },
                    "longitude": {
                        "alias": "longitude",
                        "fullyQualifiedColumn": "geo_cordinates.longitude",
                        "columnId": "ca9c83ca-0609-4457-a9e8-7dcfce0eebc8",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Double": "numeric"
                        }
                    }
                },
                "name": "geo_cordinates"
            },
            "meeting_details": {
                "id": "9645c648a1c0dbeec1287aaf1e996db3",
                "alias": "meeting_details",
                "columns": {
                    "meeting_id": {
                        "alias": "meeting_id",
                        "fullyQualifiedColumn": "meeting_details.meeting_id",
                        "columnId": "dd65d40e-ae4e-45f5-ab07-b0c8eed19eda",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "meeting_date": {
                        "alias": "meeting_date",
                        "fullyQualifiedColumn": "meeting_details.meeting_date",
                        "columnId": "090b1dbe-5ed8-48fa-81f8-50c9999f8342",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.sql.Timestamp": "dateTime"
                        }
                    },
                    "meeting_by": {
                        "alias": "meeting_by",
                        "fullyQualifiedColumn": "meeting_details.meeting_by",
                        "columnId": "171c40fb-9568-4c8d-9c37-041941f8d0bb",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "client_name": {
                        "alias": "client_name",
                        "fullyQualifiedColumn": "meeting_details.client_name",
                        "columnId": "623fa28f-600e-45fa-aeee-1bacab96f419",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "meeting_purpose": {
                        "alias": "meeting_purpose",
                        "fullyQualifiedColumn": "meeting_details.meeting_purpose",
                        "columnId": "cc4f5aab-f364-4ba7-8e82-c1a7961b6fec",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "meeting_impact": {
                        "alias": "meeting_impact",
                        "fullyQualifiedColumn": "meeting_details.meeting_impact",
                        "columnId": "7895fd23-b442-4ba4-a5b2-19131420e521",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "meet_cancellation_status": {
                        "alias": "meet_cancellation_status",
                        "fullyQualifiedColumn": "meeting_details.meet_cancellation_status",
                        "columnId": "f53269fd-7cc5-4eee-80f6-9d862c5a38c5",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "cancellation_reason": {
                        "alias": "cancellation_reason",
                        "fullyQualifiedColumn": "meeting_details.cancellation_reason",
                        "columnId": "4e58b89f-14fa-4f26-8cbe-376dfda80e5d",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    }
                },
                "name": "meeting_details"
            },
            "travel_details": {
                "id": "8a28627d07d04ef096d9935f12e0c7e9",
                "alias": "travel_details",
                "columns": {
                    "travel_id": {
                        "alias": "travel_id",
                        "fullyQualifiedColumn": "travel_details.travel_id",
                        "columnId": "69e811b1-187b-4165-8f52-2f74aef03c85",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "travel_date": {
                        "alias": "travel_date",
                        "fullyQualifiedColumn": "travel_details.travel_date",
                        "columnId": "8f561546-9344-4fbf-9553-772402abf2b3",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.sql.Timestamp": "dateTime"
                        }
                    },
                    "travel_type": {
                        "alias": "travel_type",
                        "fullyQualifiedColumn": "travel_details.travel_type",
                        "columnId": "480a6dde-c0b5-4e36-9fcc-e8c922ca6624",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "travel_medium": {
                        "alias": "travel_medium",
                        "fullyQualifiedColumn": "travel_details.travel_medium",
                        "columnId": "4c534025-582d-4e5a-ac97-d4ca9918510a",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "source_id": {
                        "alias": "source_id",
                        "fullyQualifiedColumn": "travel_details.source_id",
                        "columnId": "43c60d70-369e-48bf-a611-588b789a4321",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "source": {
                        "alias": "source",
                        "fullyQualifiedColumn": "travel_details.source",
                        "columnId": "9d14be8d-2cc7-4990-915b-42a499e52a2b",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "destination_id": {
                        "alias": "destination_id",
                        "fullyQualifiedColumn": "travel_details.destination_id",
                        "columnId": "6088515a-7480-4689-908f-8671db7bfef3",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "destination": {
                        "alias": "destination",
                        "fullyQualifiedColumn": "travel_details.destination",
                        "columnId": "0fba2239-20fe-4d41-b487-24b65f22df0d",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "travel_cost": {
                        "alias": "travel_cost",
                        "fullyQualifiedColumn": "travel_details.travel_cost",
                        "columnId": "76cb629f-3892-41cc-bc2a-e55898350e3b",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "mode_of_payment": {
                        "alias": "mode_of_payment",
                        "fullyQualifiedColumn": "travel_details.mode_of_payment",
                        "columnId": "0a2d4f4e-98df-4ac9-8cbc-38b76610220d",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "booking_platform": {
                        "alias": "booking_platform",
                        "fullyQualifiedColumn": "travel_details.booking_platform",
                        "columnId": "108ce016-b8a0-42fa-b4e6-b4501af81477",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "travelled_by": {
                        "alias": "travelled_by",
                        "fullyQualifiedColumn": "travel_details.travelled_by",
                        "columnId": "7a7e52fa-ee28-4b7b-8c77-cdc939564900",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    }
                },
                "name": "travel_details"
            }
        },
        "sets": [
            [
                "geo_cordinates",
                "dimdate",
                "travel_details",
                "employee_details",
                "meeting_details"
            ]
        ],
        "joins": [
            {
                "id": "ca21d00c8c87263dedd812f8f74c05b5",
                "type": "inner",
                "operator": "=",
                "left": {
                    "table": "geo_cordinates",
                    "column": "location_id",
                    "alias": "geo_cordinates.location_id"
                },
                "right": {
                    "table": "dimdate",
                    "column": "dim_id",
                    "alias": "dimdate.dim_id"
                }
            },
            {
                "id": "af8f3186af3703a70a3d6e219faafb4e",
                "type": "inner",
                "operator": "=",
                "left": {
                    "table": "employee_details",
                    "column": "employee_id",
                    "alias": "employee_details.employee_id"
                },
                "right": {
                    "table": "meeting_details",
                    "column": "meeting_by",
                    "alias": "meeting_details.meeting_by"
                }
            },
            {
                "id": "aab02b68e2c7febf125c50c8c5175037",
                "type": "inner",
                "operator": "=",
                "left": {
                    "table": "employee_details",
                    "column": "employee_id",
                    "alias": "employee_details.employee_id"
                },
                "right": {
                    "table": "travel_details",
                    "column": "travelled_by",
                    "alias": "travel_details.travelled_by"
                }
            },
            {
                "id": "daa3221b04c18670d4af25ac99f3ae76",
                "type": "inner",
                "operator": "=",
                "left": {
                    "table": "geo_cordinates",
                    "column": "location_id",
                    "alias": "geo_cordinates.location_id"
                },
                "right": {
                    "table": "travel_details",
                    "column": "destination_id",
                    "alias": "travel_details.destination_id"
                }
            },
            {
                "id": "cdeb5b19799c89335f23ed9b50cc5a22",
                "type": "inner",
                "operator": "=",
                "left": {
                    "table": "geo_cordinates",
                    "column": "location_id",
                    "alias": "geo_cordinates.location_id"
                },
                "right": {
                    "table": "travel_details",
                    "column": "source_id",
                    "alias": "travel_details.source_id"
                }
            }
        ],
        "crossJoins": [],
        "metadataName": "Metadata_1",
        "metadataDir": "Gagan"
    },
    "editorFullView": false,
    "selectedTableOrColumnKey": {},
    "expressionObj": [],
    "securityConstants": {},
    "edit": false,
    "isAllowServiceCall": true,
    "isValidatedTableShow": false,
    "securityTableData": [],
    "addOneMoreSecurity": false,
    "viewSessionVariables": false,
    "textEditingObj": {},
    "selectedJoinNameData": {},
    "filterbyData": [
        {
            "value": "All",
            "isChecked": true
        },
        {
            "value": "Table",
            "isChecked": true
        },
        {
            "value": "Column",
            "isChecked": true
        },
        {
            "value": "Global",
            "isChecked": true
        }
    ],
    "isFirstRender": true,
    "securityFormData": {},
    "accessType": "deny",
    "entityNames": "",
    "executionType": "conditionIf",
    "expressionName": null,
    "expressionType": "",
    "isApplyDisabled": true,
    "isInfoShow": true,
    "securityKeysChecked": [],
    "hasUnsavedData": true,
    "getSecurityTableData": {
        "tables": [],
        "columns": []
    },
    "doResetFormData": false,
    "tablesMergeType": false,
    "activeDsInfoId": "4s8tz"
}