export const preloadedStateMetadata = {
    "dataFetchedFor": {
        "getDatasource": true,
        "joins": false,
        "viewSessionVariables": false,
        "listDataSources": true,
        "639d-4eqi-up8j-vgl4-yq": false,
        "o4h3-mppg-9dnf-bmbo-gg": false,
        "eypd-ac4q-0pk9-4u0y-30": false
    },
    "loadingStatus": {
        "getDatasource": true,
        "listDataSources": true,
        "639d-4eqi-up8j-vgl4-yq": false,
        "o4h3-mppg-9dnf-bmbo-gg": false,
        "eypd-ac4q-0pk9-4u0y-30": false
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
            "driver": "org.apache.spark.deploy.DeployMessages$RequestKillDriver",
            "databaseDialect": "",
            "name": "Spark Deploy",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png"
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
            "driver": "org.apache.spark.deploy.DeployMessages$RequestKillDriver",
            "databaseDialect": "",
            "name": "Spark Deploy",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png"
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
            "available": "true",
            "driver": "org.apache.spark.deploy.DeployMessages$RequestKillDriver"
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
            "driver": "org.postgresql.Driver",
            "name": "PlainJdbc",
            "classifier": "efwd",
            "type": "sql.jdbc",
            "data": {
                "dir": "PlainJDBC",
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
            "permissionLevel": 5,
            "driver": "org.postgresql.Driver",
            "name": "postgresGroovyplain",
            "classifier": "efwd",
            "type": "sql.jdbc.groovy",
            "data": {
                "dir": "Postgresgroovyplain",
                "driverName": "org.postgresql.Driver",
                "type": "sql.jdbc.groovy",
                "id": 10,
                "userName": "postgres",
                "password": "postgres",
                "condition": "import net.sf.json.JSONObject;\n    import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n    public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        responseJson.put(\"driverName\", \"org.postgresql.Driver\");\n        responseJson.put(\"jdbcUrl\", \"jdbc:postgresql://127.0.0.1:5432/SampleTravelData\");\n        responseJson.put(\"userName\", \"postgres\");\n        responseJson.put(\"password\", \"postgres\");\n        return responseJson;\n    }",
                "jdbcUrl": "jdbc:postgresql://127.0.0.1:5432/SampleTravelData"
            },
            "dataSourceType": ""
        },
        {
            "permissionLevel": 5,
            "driver": "com.mysql.jdbc.Driver",
            "name": "asdfghjk:\"{}",
            "classifier": "efwd",
            "type": "sql.jdbc",
            "data": {
                "dir": "RETESTING",
                "driverName": "com.mysql.jdbc.Driver",
                "type": "sql.jdbc",
                "id": 13,
                "userName": "bugzilla",
                "password": "bugzilla@QA",
                "jdbcUrl": "jdbc:mysql://localhost:3306/SampleTravelData"
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
            "name": "MySql",
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
            "driver": "org.sqlite.JDBC",
            "name": "SqlLite",
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
                            "keyPath": "oivd-daem-ensr-ebfe-md/639d-4eqi-up8j-vgl4-yq/88je-rr62-82dp-jo4y-6a",
                            "key": "88je-rr62-82dp-jo4y-6a",
                            "uuid": "88je-rr62-82dp-jo4y-6a",
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
                            "keyPath": "oivd-daem-ensr-ebfe-md/639d-4eqi-up8j-vgl4-yq/j0od-a4zg-8i75-lzat-b7",
                            "key": "j0od-a4zg-8i75-lzat-b7",
                            "uuid": "j0od-a4zg-8i75-lzat-b7",
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
                            "keyPath": "oivd-daem-ensr-ebfe-md/639d-4eqi-up8j-vgl4-yq/b1t3-9vhx-w6wl-y3rq-h2",
                            "key": "b1t3-9vhx-w6wl-y3rq-h2",
                            "uuid": "b1t3-9vhx-w6wl-y3rq-h2",
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
                                    "keyPath": "oivd-daem-ensr-ebfe-md/639d-4eqi-up8j-vgl4-yq/o4h3-mppg-9dnf-bmbo-gg/bibn-4i89-n6lv-ic8r-2e",
                                    "key": "bibn-4i89-n6lv-ic8r-2e",
                                    "alias": "dimdate",
                                    "uuid": "bibn-4i89-n6lv-ic8r-2e",
                                    "connId": "xyr89",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "xyr89",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "oivd-daem-ensr-ebfe-md/639d-4eqi-up8j-vgl4-yq/o4h3-mppg-9dnf-bmbo-gg",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "dimdate_xyr89",
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
                                    "keyPath": "oivd-daem-ensr-ebfe-md/639d-4eqi-up8j-vgl4-yq/o4h3-mppg-9dnf-bmbo-gg/pmb7-33z9-jwxi-rncv-2n",
                                    "key": "pmb7-33z9-jwxi-rncv-2n",
                                    "alias": "employee_details",
                                    "uuid": "pmb7-33z9-jwxi-rncv-2n",
                                    "connId": "xyr89",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "xyr89",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "oivd-daem-ensr-ebfe-md/639d-4eqi-up8j-vgl4-yq/o4h3-mppg-9dnf-bmbo-gg",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "employee_details_xyr89",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": false
                                },
                                {
                                    "id": "be534112989b616b194bc59c2fb25a42",
                                    "name": "geo_cordinates",
                                    "data": {
                                        "id": "1",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "oivd-daem-ensr-ebfe-md/639d-4eqi-up8j-vgl4-yq/o4h3-mppg-9dnf-bmbo-gg/gxhq-eicp-njud-5cuf-mq",
                                    "key": "gxhq-eicp-njud-5cuf-mq",
                                    "alias": "geo_cordinates",
                                    "uuid": "gxhq-eicp-njud-5cuf-mq",
                                    "connId": "xyr89",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "xyr89",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "oivd-daem-ensr-ebfe-md/639d-4eqi-up8j-vgl4-yq/o4h3-mppg-9dnf-bmbo-gg",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "geo_cordinates_xyr89",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": false
                                },
                                {
                                    "id": "9645c648a1c0dbeec1287aaf1e996db3",
                                    "name": "meeting_details",
                                    "data": {
                                        "id": "1",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "oivd-daem-ensr-ebfe-md/639d-4eqi-up8j-vgl4-yq/o4h3-mppg-9dnf-bmbo-gg/pjnv-lfz4-hun8-lpsf-31",
                                    "key": "pjnv-lfz4-hun8-lpsf-31",
                                    "alias": "meeting_details",
                                    "uuid": "pjnv-lfz4-hun8-lpsf-31",
                                    "connId": "xyr89",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "xyr89",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "oivd-daem-ensr-ebfe-md/639d-4eqi-up8j-vgl4-yq/o4h3-mppg-9dnf-bmbo-gg",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "meeting_details_xyr89",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": false
                                },
                                {
                                    "id": "8a28627d07d04ef096d9935f12e0c7e9",
                                    "name": "travel_details",
                                    "data": {
                                        "id": "1",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "oivd-daem-ensr-ebfe-md/639d-4eqi-up8j-vgl4-yq/o4h3-mppg-9dnf-bmbo-gg/84eb-jl4t-o7ox-he52-j7",
                                    "key": "84eb-jl4t-o7ox-he52-j7",
                                    "alias": "travel_details",
                                    "uuid": "84eb-jl4t-o7ox-he52-j7",
                                    "connId": "xyr89",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "xyr89",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "oivd-daem-ensr-ebfe-md/639d-4eqi-up8j-vgl4-yq/o4h3-mppg-9dnf-bmbo-gg",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "travel_details_xyr89",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": false
                                }
                            ],
                            "keyPath": "oivd-daem-ensr-ebfe-md/639d-4eqi-up8j-vgl4-yq/o4h3-mppg-9dnf-bmbo-gg",
                            "key": "o4h3-mppg-9dnf-bmbo-gg",
                            "uuid": "o4h3-mppg-9dnf-bmbo-gg",
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
                            "keyPath": "oivd-daem-ensr-ebfe-md/639d-4eqi-up8j-vgl4-yq/4lmj-3krb-rdzz-6a4s-va",
                            "key": "4lmj-3krb-rdzz-6a4s-va",
                            "uuid": "4lmj-3krb-rdzz-6a4s-va",
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
                            "keyPath": "oivd-daem-ensr-ebfe-md/639d-4eqi-up8j-vgl4-yq/yuq5-wqyt-q7wa-0a84-bm",
                            "key": "yuq5-wqyt-q7wa-0a84-bm",
                            "uuid": "yuq5-wqyt-q7wa-0a84-bm",
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
                            "keyPath": "oivd-daem-ensr-ebfe-md/639d-4eqi-up8j-vgl4-yq/eulu-5o2n-6jee-b7h1-4f",
                            "key": "eulu-5o2n-6jee-b7h1-4f",
                            "uuid": "eulu-5o2n-6jee-b7h1-4f",
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
                            "keyPath": "oivd-daem-ensr-ebfe-md/639d-4eqi-up8j-vgl4-yq/b7aj-y51q-ru8e-b39h-aw",
                            "key": "b7aj-y51q-ru8e-b39h-aw",
                            "uuid": "b7aj-y51q-ru8e-b39h-aw",
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
                            "keyPath": "oivd-daem-ensr-ebfe-md/639d-4eqi-up8j-vgl4-yq/fi89-o0zn-bi99-bf6e-nj",
                            "key": "fi89-o0zn-bi99-bf6e-nj",
                            "uuid": "fi89-o0zn-bi99-bf6e-nj",
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
                            "keyPath": "oivd-daem-ensr-ebfe-md/639d-4eqi-up8j-vgl4-yq/i60q-gmli-9ujr-1412-yh",
                            "key": "i60q-gmli-9ujr-1412-yh",
                            "uuid": "i60q-gmli-9ujr-1412-yh",
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
                            "keyPath": "oivd-daem-ensr-ebfe-md/639d-4eqi-up8j-vgl4-yq/pch9-sefn-ilfm-jez9-r6",
                            "key": "pch9-sefn-ilfm-jez9-r6",
                            "uuid": "pch9-sefn-ilfm-jez9-r6",
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
                            "keyPath": "oivd-daem-ensr-ebfe-md/639d-4eqi-up8j-vgl4-yq/i6hj-fg57-z2rb-uluh-if",
                            "key": "i6hj-fg57-z2rb-uluh-if",
                            "uuid": "i6hj-fg57-z2rb-uluh-if",
                            "data": {
                                "id": "1",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "SampleTravelDataDerby"
                        }
                    ],
                    "driverType": "Derby",
                    "keyPath": "oivd-daem-ensr-ebfe-md/639d-4eqi-up8j-vgl4-yq",
                    "key": "639d-4eqi-up8j-vgl4-yq",
                    "uuid": "639d-4eqi-up8j-vgl4-yq",
                    "fetched": true
                }
            ],
            "key": "oivd-daem-ensr-ebfe-md",
            "uuid": "oivd-daem-ensr-ebfe-md",
            "keyPath": "oivd-daem-ensr-ebfe-md",
            "category": "dsGroup",
            "imgUrl": "images/data_sources/defaut_datasource.png"
        },
        {
            "name": "Mysql",
            "children": [
                {
                    "permissionLevel": 5,
                    "driver": "com.mysql.jdbc.Driver",
                    "name": "asdfghjk:\"{}",
                    "classifier": "efwd",
                    "type": "sql.jdbc",
                    "data": {
                        "dir": "RETESTING",
                        "driverName": "com.mysql.jdbc.Driver",
                        "type": "sql.jdbc",
                        "id": 13,
                        "userName": "bugzilla",
                        "password": "bugzilla@QA",
                        "jdbcUrl": "jdbc:mysql://localhost:3306/SampleTravelData"
                    },
                    "dataSourceType": "Plain Jdbc DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Mysql",
                    "keyPath": "ny09-sx6y-vm7h-f6gl-k8/efxe-lh3x-9try-z7ur-dk",
                    "key": "efxe-lh3x-9try-z7ur-dk",
                    "uuid": "efxe-lh3x-9try-z7ur-dk"
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
                    "name": "MySql",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Mysql",
                    "keyPath": "ny09-sx6y-vm7h-f6gl-k8/2n8h-96km-cdvw-e8oo-ft",
                    "key": "2n8h-96km-cdvw-e8oo-ft",
                    "uuid": "2n8h-96km-cdvw-e8oo-ft"
                }
            ],
            "key": "ny09-sx6y-vm7h-f6gl-k8",
            "uuid": "ny09-sx6y-vm7h-f6gl-k8",
            "keyPath": "ny09-sx6y-vm7h-f6gl-k8",
            "category": "dsGroup",
            "imgUrl": "images/data_sources/defaut_datasource.png"
        },
        {
            "name": "Postgresql",
            "children": [
                {
                    "permissionLevel": 5,
                    "driver": "org.postgresql.Driver",
                    "name": "PlainJdbc",
                    "classifier": "efwd",
                    "type": "sql.jdbc",
                    "data": {
                        "dir": "PlainJDBC",
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
                    "keyPath": "y80l-kyfv-cags-nyyn-yb/5ry6-us1s-yzse-htvv-92",
                    "key": "5ry6-us1s-yzse-htvv-92",
                    "uuid": "5ry6-us1s-yzse-htvv-92"
                },
                {
                    "permissionLevel": 5,
                    "driver": "org.postgresql.Driver",
                    "name": "postgresGroovyplain",
                    "classifier": "efwd",
                    "type": "sql.jdbc.groovy",
                    "data": {
                        "dir": "Postgresgroovyplain",
                        "driverName": "org.postgresql.Driver",
                        "type": "sql.jdbc.groovy",
                        "id": 10,
                        "userName": "postgres",
                        "password": "postgres",
                        "condition": "import net.sf.json.JSONObject;\n    import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n    public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        responseJson.put(\"driverName\", \"org.postgresql.Driver\");\n        responseJson.put(\"jdbcUrl\", \"jdbc:postgresql://127.0.0.1:5432/SampleTravelData\");\n        responseJson.put(\"userName\", \"postgres\");\n        responseJson.put(\"password\", \"postgres\");\n        return responseJson;\n    }",
                        "jdbcUrl": "jdbc:postgresql://127.0.0.1:5432/SampleTravelData"
                    },
                    "dataSourceType": "",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Postgresql",
                    "keyPath": "y80l-kyfv-cags-nyyn-yb/m7m4-y9cz-b18o-dwh8-gc",
                    "key": "m7m4-y9cz-b18o-dwh8-gc",
                    "uuid": "m7m4-y9cz-b18o-dwh8-gc"
                }
            ],
            "key": "y80l-kyfv-cags-nyyn-yb",
            "uuid": "y80l-kyfv-cags-nyyn-yb",
            "keyPath": "y80l-kyfv-cags-nyyn-yb",
            "category": "dsGroup",
            "imgUrl": "images/data_sources/defaut_datasource.png"
        },
        {
            "name": "Sqlite",
            "children": [
                {
                    "data": {
                        "id": "1001",
                        "type": "dynamicDataSource"
                    },
                    "dataSourceProvider": "tomcat",
                    "type": "dynamicDataSource",
                    "permissionLevel": 5,
                    "driver": "org.sqlite.JDBC",
                    "name": "SqlLite",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource",
                    "category": "dataSource",
                    "children": [
                        {
                            "id": "d324e793296ff76020c708f1c8fbb704",
                            "name": "dimdate",
                            "data": {
                                "id": "1001",
                                "type": "dynamicDataSource"
                            },
                            "keyPath": "0wkm-z8ez-di99-p71r-cb/eypd-ac4q-0pk9-4u0y-30/vfw5-2tia-cq4d-brze-gz",
                            "key": "vfw5-2tia-cq4d-brze-gz",
                            "alias": "dimdate",
                            "uuid": "vfw5-2tia-cq4d-brze-gz",
                            "connId": "qwv22",
                            "dataSource": {
                                "id": "1001",
                                "type": "dynamicDataSource",
                                "baseType": "global.jdbc",
                                "catSchemaPredicted": false,
                                "sync": false,
                                "catalog": "",
                                "schema": "",
                                "connId": "qwv22",
                                "classifier": "db.workflow",
                                "datasourceName": "SqlLite",
                                "dsKeyPath": "0wkm-z8ez-di99-p71r-cb/eypd-ac4q-0pk9-4u0y-30",
                                "driverType": "Sqlite",
                                "database": "SqlLite"
                            },
                            "category": "table",
                            "nameWithConnId": "dimdate_qwv22",
                            "database": "SqlLite",
                            "selected": true
                        },
                        {
                            "id": "b161910cbebfd353351a6c0b46e6a02e",
                            "name": "employee_details",
                            "data": {
                                "id": "1001",
                                "type": "dynamicDataSource"
                            },
                            "keyPath": "0wkm-z8ez-di99-p71r-cb/eypd-ac4q-0pk9-4u0y-30/mt54-vyhv-p9fv-eizd-zw",
                            "key": "mt54-vyhv-p9fv-eizd-zw",
                            "alias": "employee_details",
                            "uuid": "mt54-vyhv-p9fv-eizd-zw",
                            "connId": "qwv22",
                            "dataSource": {
                                "id": "1001",
                                "type": "dynamicDataSource",
                                "baseType": "global.jdbc",
                                "catSchemaPredicted": false,
                                "sync": false,
                                "catalog": "",
                                "schema": "",
                                "connId": "qwv22",
                                "classifier": "db.workflow",
                                "datasourceName": "SqlLite",
                                "dsKeyPath": "0wkm-z8ez-di99-p71r-cb/eypd-ac4q-0pk9-4u0y-30",
                                "driverType": "Sqlite",
                                "database": "SqlLite"
                            },
                            "category": "table",
                            "nameWithConnId": "employee_details_qwv22",
                            "database": "SqlLite",
                            "selected": false
                        },
                        {
                            "id": "f2ff93c37589ef57f40dcb15fda6d7ea",
                            "name": "geo_cordinates",
                            "data": {
                                "id": "1001",
                                "type": "dynamicDataSource"
                            },
                            "keyPath": "0wkm-z8ez-di99-p71r-cb/eypd-ac4q-0pk9-4u0y-30/rdpx-kjwy-609d-aa47-h7",
                            "key": "rdpx-kjwy-609d-aa47-h7",
                            "alias": "geo_cordinates",
                            "uuid": "rdpx-kjwy-609d-aa47-h7",
                            "connId": "qwv22",
                            "dataSource": {
                                "id": "1001",
                                "type": "dynamicDataSource",
                                "baseType": "global.jdbc",
                                "catSchemaPredicted": false,
                                "sync": false,
                                "catalog": "",
                                "schema": "",
                                "connId": "qwv22",
                                "classifier": "db.workflow",
                                "datasourceName": "SqlLite",
                                "dsKeyPath": "0wkm-z8ez-di99-p71r-cb/eypd-ac4q-0pk9-4u0y-30",
                                "driverType": "Sqlite",
                                "database": "SqlLite"
                            },
                            "category": "table",
                            "nameWithConnId": "geo_cordinates_qwv22",
                            "database": "SqlLite",
                            "selected": false
                        },
                        {
                            "id": "025fbfb381cb17d4519363c3585626fb",
                            "name": "meeting_details",
                            "data": {
                                "id": "1001",
                                "type": "dynamicDataSource"
                            },
                            "keyPath": "0wkm-z8ez-di99-p71r-cb/eypd-ac4q-0pk9-4u0y-30/unf9-fw2l-n2mw-elri-ew",
                            "key": "unf9-fw2l-n2mw-elri-ew",
                            "alias": "meeting_details",
                            "uuid": "unf9-fw2l-n2mw-elri-ew",
                            "connId": "qwv22",
                            "dataSource": {
                                "id": "1001",
                                "type": "dynamicDataSource",
                                "baseType": "global.jdbc",
                                "catSchemaPredicted": false,
                                "sync": false,
                                "catalog": "",
                                "schema": "",
                                "connId": "qwv22",
                                "classifier": "db.workflow",
                                "datasourceName": "SqlLite",
                                "dsKeyPath": "0wkm-z8ez-di99-p71r-cb/eypd-ac4q-0pk9-4u0y-30",
                                "driverType": "Sqlite",
                                "database": "SqlLite"
                            },
                            "category": "table",
                            "nameWithConnId": "meeting_details_qwv22",
                            "database": "SqlLite",
                            "selected": false
                        },
                        {
                            "id": "21e1b86ae9680d0fc197ed543c3e37eb",
                            "name": "travel_details",
                            "data": {
                                "id": "1001",
                                "type": "dynamicDataSource"
                            },
                            "keyPath": "0wkm-z8ez-di99-p71r-cb/eypd-ac4q-0pk9-4u0y-30/k2tv-74t3-z90t-6e9z-qf",
                            "key": "k2tv-74t3-z90t-6e9z-qf",
                            "alias": "travel_details",
                            "uuid": "k2tv-74t3-z90t-6e9z-qf",
                            "connId": "qwv22",
                            "dataSource": {
                                "id": "1001",
                                "type": "dynamicDataSource",
                                "baseType": "global.jdbc",
                                "catSchemaPredicted": false,
                                "sync": false,
                                "catalog": "",
                                "schema": "",
                                "connId": "qwv22",
                                "classifier": "db.workflow",
                                "datasourceName": "SqlLite",
                                "dsKeyPath": "0wkm-z8ez-di99-p71r-cb/eypd-ac4q-0pk9-4u0y-30",
                                "driverType": "Sqlite",
                                "database": "SqlLite"
                            },
                            "category": "table",
                            "nameWithConnId": "travel_details_qwv22",
                            "database": "SqlLite",
                            "selected": false
                        }
                    ],
                    "driverType": "Sqlite",
                    "keyPath": "0wkm-z8ez-di99-p71r-cb/eypd-ac4q-0pk9-4u0y-30",
                    "key": "eypd-ac4q-0pk9-4u0y-30",
                    "uuid": "eypd-ac4q-0pk9-4u0y-30",
                    "fetched": true
                }
            ],
            "key": "0wkm-z8ez-di99-p71r-cb",
            "uuid": "0wkm-z8ez-di99-p71r-cb",
            "keyPath": "0wkm-z8ez-di99-p71r-cb",
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
                "dsUUID": "639d-4eqi-up8j-vgl4-yq",
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
                        "connId": "xyr89",
                        "classifier": "db.workflow",
                        "datasourceName": "SampleTravelDataDerby",
                        "dsKeyPath": "oivd-daem-ensr-ebfe-md/639d-4eqi-up8j-vgl4-yq/o4h3-mppg-9dnf-bmbo-gg",
                        "driverType": "Derby",
                        "database": "HIUSER"
                    },
                    "name": "HIUSER"
                }
            },
            {
                "fetchSchemas": true,
                "fetchCatalogs": true,
                "working": true,
                "connData": {
                    "id": "1001",
                    "type": "dynamicDataSource"
                },
                "connId": "1001",
                "dsUUID": "eypd-ac4q-0pk9-4u0y-30",
                "classifier": "db.workflow",
                "metadata": {
                    "catalogs": [
                        {
                            "name": "Null",
                            "schemas": [
                                {
                                    "name": "Null",
                                    "tables": [
                                        {
                                            "id": "f2ff93c37589ef57f40dcb15fda6d7ea",
                                            "name": "geo_cordinates"
                                        },
                                        {
                                            "id": "025fbfb381cb17d4519363c3585626fb",
                                            "name": "meeting_details"
                                        },
                                        {
                                            "id": "b161910cbebfd353351a6c0b46e6a02e",
                                            "name": "employee_details"
                                        },
                                        {
                                            "id": "d324e793296ff76020c708f1c8fbb704",
                                            "name": "dimdate"
                                        },
                                        {
                                            "id": "21e1b86ae9680d0fc197ed543c3e37eb",
                                            "name": "travel_details"
                                        }
                                    ]
                                }
                            ]
                        }
                    ],
                    "dataSource": {
                        "id": "1001",
                        "type": "dynamicDataSource",
                        "baseType": "global.jdbc",
                        "catSchemaPredicted": false,
                        "sync": false,
                        "catalog": "",
                        "schema": "",
                        "connId": "qwv22",
                        "classifier": "db.workflow",
                        "datasourceName": "SqlLite",
                        "dsKeyPath": "0wkm-z8ez-di99-p71r-cb/eypd-ac4q-0pk9-4u0y-30",
                        "driverType": "Sqlite",
                        "database": "SqlLite"
                    },
                    "name": ""
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
            "connId": "xyr89",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelDataDerby",
            "dsKeyPath": "oivd-daem-ensr-ebfe-md/639d-4eqi-up8j-vgl4-yq/o4h3-mppg-9dnf-bmbo-gg",
            "driverType": "Derby",
            "database": "HIUSER"
        },
        {
            "id": "1001",
            "type": "dynamicDataSource",
            "baseType": "global.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "",
            "schema": "",
            "connId": "qwv22",
            "classifier": "db.workflow",
            "datasourceName": "SqlLite",
            "dsKeyPath": "0wkm-z8ez-di99-p71r-cb/eypd-ac4q-0pk9-4u0y-30",
            "driverType": "Sqlite",
            "database": "SqlLite"
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
            "keyPath": "oivd-daem-ensr-ebfe-md/639d-4eqi-up8j-vgl4-yq/o4h3-mppg-9dnf-bmbo-gg/bibn-4i89-n6lv-ic8r-2e",
            "key": "bibn-4i89-n6lv-ic8r-2e",
            "alias": "dimdate",
            "uuid": "bibn-4i89-n6lv-ic8r-2e",
            "connId": "xyr89",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "xyr89",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "oivd-daem-ensr-ebfe-md/639d-4eqi-up8j-vgl4-yq/o4h3-mppg-9dnf-bmbo-gg",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "dimdate_xyr89",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "dimdate"
        },
        "dimdate_qwv22": {
            "id": "d324e793296ff76020c708f1c8fbb704",
            "name": "dimdate",
            "data": {
                "id": "1001",
                "type": "dynamicDataSource"
            },
            "keyPath": "0wkm-z8ez-di99-p71r-cb/eypd-ac4q-0pk9-4u0y-30/vfw5-2tia-cq4d-brze-gz",
            "key": "vfw5-2tia-cq4d-brze-gz",
            "alias": "dimdate",
            "uuid": "vfw5-2tia-cq4d-brze-gz",
            "connId": "qwv22",
            "dataSource": {
                "id": "1001",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "",
                "connId": "qwv22",
                "classifier": "db.workflow",
                "datasourceName": "SqlLite",
                "dsKeyPath": "0wkm-z8ez-di99-p71r-cb/eypd-ac4q-0pk9-4u0y-30",
                "driverType": "Sqlite",
                "database": "SqlLite"
            },
            "category": "table",
            "nameWithConnId": "dimdate_qwv22",
            "database": "SqlLite",
            "selected": true,
            "keyName": "dimdate_qwv22"
        }
    },
    "views": [],
    "activeView": false,
    "categories": {
        "oivd-daem-ensr-ebfe-md": {
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
        "ny09-sx6y-vm7h-f6gl-k8": {
            "ds": {
                "permissionLevel": 5,
                "driver": "com.mysql.jdbc.Driver",
                "name": "asdfghjk:\"{}",
                "classifier": "efwd",
                "type": "sql.jdbc",
                "data": {
                    "dir": "RETESTING",
                    "driverName": "com.mysql.jdbc.Driver",
                    "type": "sql.jdbc",
                    "id": 13,
                    "userName": "bugzilla",
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
        "y80l-kyfv-cags-nyyn-yb": {
            "ds": {
                "permissionLevel": 5,
                "driver": "org.postgresql.Driver",
                "name": "PlainJdbc",
                "classifier": "efwd",
                "type": "sql.jdbc",
                "data": {
                    "dir": "PlainJDBC",
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
        "0wkm-z8ez-di99-p71r-cb": {
            "ds": {
                "data": {
                    "id": "1001",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "org.sqlite.JDBC",
                "name": "SqlLite",
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
    "activeEditorTab": "info",
    "dataSourcesAddedToMetadata": [
        {
            "id": "1",
            "type": "dynamicDataSource",
            "baseType": "global.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "",
            "schema": "HIUSER",
            "connId": "xyr89",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelDataDerby",
            "dsKeyPath": "oivd-daem-ensr-ebfe-md/639d-4eqi-up8j-vgl4-yq/o4h3-mppg-9dnf-bmbo-gg",
            "driverType": "Derby",
            "database": "HIUSER"
        },
        {
            "id": "1001",
            "type": "dynamicDataSource",
            "baseType": "global.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "",
            "schema": "",
            "connId": "qwv22",
            "classifier": "db.workflow",
            "datasourceName": "SqlLite",
            "dsKeyPath": "0wkm-z8ez-di99-p71r-cb/eypd-ac4q-0pk9-4u0y-30",
            "driverType": "Sqlite",
            "database": "SqlLite"
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
    "joins": [],
    "mode": "create",
    "allTablesKeys": [
        "gxhq-eicp-njud-5cuf-mq",
        "pjnv-lfz4-hun8-lpsf-31",
        "pmb7-33z9-jwxi-rncv-2n",
        "bibn-4i89-n6lv-ic8r-2e",
        "84eb-jl4t-o7ox-he52-j7",
        "rdpx-kjwy-609d-aa47-h7",
        "unf9-fw2l-n2mw-elri-ew",
        "mt54-vyhv-p9fv-eizd-zw",
        "vfw5-2tia-cq4d-brze-gz",
        "k2tv-74t3-z90t-6e9z-qf"
    ],
    "selectedTableKeys": [
        "bibn-4i89-n6lv-ic8r-2e",
        "vfw5-2tia-cq4d-brze-gz"
    ],
    "metadataName": "Metadata_1",
    "activeDataSource": false,
    "metadataToEdit": false,
    "isSavingInProgress": false,
    "editViewsTempData": {},
    "inititalStateFromJest": false,
    "timeStamp": 1668590934222,
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
    "tablesMergeType": "merge",
    "activeDsInfoId": "xyr89"
}