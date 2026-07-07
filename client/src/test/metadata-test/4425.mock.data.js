export const storeData = {
    data_4425: {
        "dataFetchedFor": {
            "getDatasource": true,
            "joins": true,
            "viewSessionVariables": false,
            "listDataSources": true,
            "omo4-wtxl-zlri-cg0g-34": false,
            "nhwt-47ai-hcae-auhw-ec": false
        },
        "loadingStatus": {
            "getDatasource": true,
            "listDataSources": true,
            "joins": true,
            "omo4-wtxl-zlri-cg0g-34": false,
            "nhwt-47ai-hcae-auhw-ec": false
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
                "driver": "org.apache.spark.deploy.DeployMessages$KillDriver",
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
                "driver": "org.apache.spark.deploy.DeployMessages$LaunchDriver",
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
                "driver": "org.apache.spark.deploy.DeployMessages$KillDriver",
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
                "driver": "org.apache.spark.deploy.DeployMessages$LaunchDriver",
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
                "available": "true",
                "driver": "org.apache.spark.deploy.DeployMessages$LaunchDriver"
            },
            {
                "available": "true",
                "driver": "org.apache.spark.deploy.DeployMessages$KillDriver"
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
        "allDataSources": [
            {
                "permissionLevel": 5,
                "driver": "dynamicSwitch",
                "name": "Groovy 1",
                "classifier": "efwd",
                "type": "sql.jdbc.groovy.managed",
                "data": {
                    "dir": "Gagan",
                    "driverName": null,
                    "type": "sql.jdbc.groovy.managed",
                    "id": 1,
                    "userName": null,
                    "password": null,
                    "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1001);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                    "jdbcUrl": null,
                    "driver": "dynamicSwitch"
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
                "driver": "org.apache.derby.jdbc.AutoloadedDriver",
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
                "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                "name": "hiee",
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
                "driver": "org.postgresql.Driver",
                "name": "booking",
                "classifier": "global",
                "dataSourceType": "Managed DataSource"
            },
            {
                "data": {
                    "id": "1101",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                "name": "derby2",
                "classifier": "global",
                "dataSourceType": "Managed DataSource"
            },
            {
                "data": {
                    "id": "1201",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "org.sqlite.JDBC",
                "name": "SampleTravelData",
                "classifier": "global",
                "dataSourceType": "Managed DataSource"
            },
            {
                "data": {
                    "id": "1302",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "org.sqlite.JDBC",
                "name": "Sqlite",
                "classifier": "global",
                "dataSourceType": "Managed DataSource"
            },
            {
                "data": {
                    "id": "1401",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "org.postgresql.Driver",
                "name": "booking",
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
                        "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                        "name": "SampleTravelDataDerby",
                        "classifier": "global",
                        "dataSourceType": "Managed DataSource",
                        "category": "dataSource",
                        "children": [
                            {
                                "name": "SQLJ",
                                "children": [],
                                "keyPath": "9i4e-8upi-3e1u-nir5-gk/omo4-wtxl-zlri-cg0g-34/ynny-ohs6-1xt3-8tqs-un",
                                "key": "ynny-ohs6-1xt3-8tqs-un",
                                "uuid": "ynny-ohs6-1xt3-8tqs-un",
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
                                "keyPath": "9i4e-8upi-3e1u-nir5-gk/omo4-wtxl-zlri-cg0g-34/cpbd-5gun-pjy1-jnu3-74",
                                "key": "cpbd-5gun-pjy1-jnu3-74",
                                "uuid": "cpbd-5gun-pjy1-jnu3-74",
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
                                "keyPath": "9i4e-8upi-3e1u-nir5-gk/omo4-wtxl-zlri-cg0g-34/c1tt-faw2-vlwa-6qq9-3b",
                                "key": "c1tt-faw2-vlwa-6qq9-3b",
                                "uuid": "c1tt-faw2-vlwa-6qq9-3b",
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
                                        "keyPath": "9i4e-8upi-3e1u-nir5-gk/omo4-wtxl-zlri-cg0g-34/nhwt-47ai-hcae-auhw-ec/krff-5pzt-p69d-mhaq-uu",
                                        "key": "krff-5pzt-p69d-mhaq-uu",
                                        "alias": "dimdate",
                                        "uuid": "krff-5pzt-p69d-mhaq-uu",
                                        "connId": "9fasa",
                                        "dataSource": {
                                            "id": "1",
                                            "type": "dynamicDataSource",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "",
                                            "schema": "HIUSER",
                                            "connId": "9fasa",
                                            "classifier": "db.workflow",
                                            "datasourceName": "SampleTravelDataDerby",
                                            "dsKeyPath": "9i4e-8upi-3e1u-nir5-gk/omo4-wtxl-zlri-cg0g-34/nhwt-47ai-hcae-auhw-ec",
                                            "driverType": "Derby",
                                            "database": "HIUSER"
                                        },
                                        "dataSourceName": "SampleTravelDataDerby",
                                        "category": "table",
                                        "nameWithConnId": "dimdate_9fasa",
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
                                        "keyPath": "9i4e-8upi-3e1u-nir5-gk/omo4-wtxl-zlri-cg0g-34/nhwt-47ai-hcae-auhw-ec/hi91-hp8m-1tii-8yz2-8v",
                                        "key": "hi91-hp8m-1tii-8yz2-8v",
                                        "alias": "employee_details",
                                        "uuid": "hi91-hp8m-1tii-8yz2-8v",
                                        "connId": "9fasa",
                                        "dataSource": {
                                            "id": "1",
                                            "type": "dynamicDataSource",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "",
                                            "schema": "HIUSER",
                                            "connId": "9fasa",
                                            "classifier": "db.workflow",
                                            "datasourceName": "SampleTravelDataDerby",
                                            "dsKeyPath": "9i4e-8upi-3e1u-nir5-gk/omo4-wtxl-zlri-cg0g-34/nhwt-47ai-hcae-auhw-ec",
                                            "driverType": "Derby",
                                            "database": "HIUSER"
                                        },
                                        "dataSourceName": "SampleTravelDataDerby",
                                        "category": "table",
                                        "nameWithConnId": "employee_details_9fasa",
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
                                        "keyPath": "9i4e-8upi-3e1u-nir5-gk/omo4-wtxl-zlri-cg0g-34/nhwt-47ai-hcae-auhw-ec/cmrh-yof7-tp2v-752g-6i",
                                        "key": "cmrh-yof7-tp2v-752g-6i",
                                        "alias": "geo_cordinates",
                                        "uuid": "cmrh-yof7-tp2v-752g-6i",
                                        "connId": "9fasa",
                                        "dataSource": {
                                            "id": "1",
                                            "type": "dynamicDataSource",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "",
                                            "schema": "HIUSER",
                                            "connId": "9fasa",
                                            "classifier": "db.workflow",
                                            "datasourceName": "SampleTravelDataDerby",
                                            "dsKeyPath": "9i4e-8upi-3e1u-nir5-gk/omo4-wtxl-zlri-cg0g-34/nhwt-47ai-hcae-auhw-ec",
                                            "driverType": "Derby",
                                            "database": "HIUSER"
                                        },
                                        "dataSourceName": "SampleTravelDataDerby",
                                        "category": "table",
                                        "nameWithConnId": "geo_cordinates_9fasa",
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
                                        "keyPath": "9i4e-8upi-3e1u-nir5-gk/omo4-wtxl-zlri-cg0g-34/nhwt-47ai-hcae-auhw-ec/7chp-ga97-kt6s-o69a-rt",
                                        "key": "7chp-ga97-kt6s-o69a-rt",
                                        "alias": "meeting_details",
                                        "uuid": "7chp-ga97-kt6s-o69a-rt",
                                        "connId": "9fasa",
                                        "dataSource": {
                                            "id": "1",
                                            "type": "dynamicDataSource",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "",
                                            "schema": "HIUSER",
                                            "connId": "9fasa",
                                            "classifier": "db.workflow",
                                            "datasourceName": "SampleTravelDataDerby",
                                            "dsKeyPath": "9i4e-8upi-3e1u-nir5-gk/omo4-wtxl-zlri-cg0g-34/nhwt-47ai-hcae-auhw-ec",
                                            "driverType": "Derby",
                                            "database": "HIUSER"
                                        },
                                        "dataSourceName": "SampleTravelDataDerby",
                                        "category": "table",
                                        "nameWithConnId": "meeting_details_9fasa",
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
                                        "keyPath": "9i4e-8upi-3e1u-nir5-gk/omo4-wtxl-zlri-cg0g-34/nhwt-47ai-hcae-auhw-ec/pxrv-mbma-tacr-h3fo-gm",
                                        "key": "pxrv-mbma-tacr-h3fo-gm",
                                        "alias": "travel_details",
                                        "uuid": "pxrv-mbma-tacr-h3fo-gm",
                                        "connId": "9fasa",
                                        "dataSource": {
                                            "id": "1",
                                            "type": "dynamicDataSource",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "",
                                            "schema": "HIUSER",
                                            "connId": "9fasa",
                                            "classifier": "db.workflow",
                                            "datasourceName": "SampleTravelDataDerby",
                                            "dsKeyPath": "9i4e-8upi-3e1u-nir5-gk/omo4-wtxl-zlri-cg0g-34/nhwt-47ai-hcae-auhw-ec",
                                            "driverType": "Derby",
                                            "database": "HIUSER"
                                        },
                                        "dataSourceName": "SampleTravelDataDerby",
                                        "category": "table",
                                        "nameWithConnId": "travel_details_9fasa",
                                        "database": "HIUSER",
                                        "schema": "HIUSER",
                                        "selected": true
                                    }
                                ],
                                "keyPath": "9i4e-8upi-3e1u-nir5-gk/omo4-wtxl-zlri-cg0g-34/nhwt-47ai-hcae-auhw-ec",
                                "key": "nhwt-47ai-hcae-auhw-ec",
                                "uuid": "nhwt-47ai-hcae-auhw-ec",
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
                                "keyPath": "9i4e-8upi-3e1u-nir5-gk/omo4-wtxl-zlri-cg0g-34/wht7-t7u4-fmyw-tpkv-6i",
                                "key": "wht7-t7u4-fmyw-tpkv-6i",
                                "uuid": "wht7-t7u4-fmyw-tpkv-6i",
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
                                "keyPath": "9i4e-8upi-3e1u-nir5-gk/omo4-wtxl-zlri-cg0g-34/t3di-i7nq-sri9-cxqp-tj",
                                "key": "t3di-i7nq-sri9-cxqp-tj",
                                "uuid": "t3di-i7nq-sri9-cxqp-tj",
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
                                "keyPath": "9i4e-8upi-3e1u-nir5-gk/omo4-wtxl-zlri-cg0g-34/5a0y-z18h-41zq-rwp5-p7",
                                "key": "5a0y-z18h-41zq-rwp5-p7",
                                "uuid": "5a0y-z18h-41zq-rwp5-p7",
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
                                "keyPath": "9i4e-8upi-3e1u-nir5-gk/omo4-wtxl-zlri-cg0g-34/yvlc-1d1s-3uia-iwtq-bs",
                                "key": "yvlc-1d1s-3uia-iwtq-bs",
                                "uuid": "yvlc-1d1s-3uia-iwtq-bs",
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
                                "keyPath": "9i4e-8upi-3e1u-nir5-gk/omo4-wtxl-zlri-cg0g-34/iv2z-3dz1-8vy5-bryx-n7",
                                "key": "iv2z-3dz1-8vy5-bryx-n7",
                                "uuid": "iv2z-3dz1-8vy5-bryx-n7",
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
                                "keyPath": "9i4e-8upi-3e1u-nir5-gk/omo4-wtxl-zlri-cg0g-34/rbzh-winm-u8go-6kik-yn",
                                "key": "rbzh-winm-u8go-6kik-yn",
                                "uuid": "rbzh-winm-u8go-6kik-yn",
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
                                "keyPath": "9i4e-8upi-3e1u-nir5-gk/omo4-wtxl-zlri-cg0g-34/318q-obvb-1g96-pz8m-lu",
                                "key": "318q-obvb-1g96-pz8m-lu",
                                "uuid": "318q-obvb-1g96-pz8m-lu",
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
                                "keyPath": "9i4e-8upi-3e1u-nir5-gk/omo4-wtxl-zlri-cg0g-34/k1bd-v4fp-hq56-6nz0-vg",
                                "key": "k1bd-v4fp-hq56-6nz0-vg",
                                "uuid": "k1bd-v4fp-hq56-6nz0-vg",
                                "data": {
                                    "id": "1",
                                    "type": "dynamicDataSource"
                                },
                                "category": "schema",
                                "datasourceName": "SampleTravelDataDerby"
                            }
                        ],
                        "driverType": "Derby",
                        "keyPath": "9i4e-8upi-3e1u-nir5-gk/omo4-wtxl-zlri-cg0g-34",
                        "key": "omo4-wtxl-zlri-cg0g-34",
                        "uuid": "omo4-wtxl-zlri-cg0g-34",
                        "fetched": true
                    },
                    {
                        "data": {
                            "id": "1000",
                            "type": "dynamicDataSource"
                        },
                        "dataSourceProvider": "tomcat",
                        "type": "dynamicDataSource",
                        "permissionLevel": 5,
                        "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                        "name": "hiee",
                        "classifier": "global",
                        "dataSourceType": "Managed DataSource",
                        "category": "dataSource",
                        "children": [],
                        "driverType": "Derby",
                        "keyPath": "9i4e-8upi-3e1u-nir5-gk/xu26-vgtj-mnwv-vut0-hx",
                        "key": "xu26-vgtj-mnwv-vut0-hx",
                        "uuid": "xu26-vgtj-mnwv-vut0-hx"
                    },
                    {
                        "data": {
                            "id": "1101",
                            "type": "dynamicDataSource"
                        },
                        "dataSourceProvider": "tomcat",
                        "type": "dynamicDataSource",
                        "permissionLevel": 5,
                        "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                        "name": "derby2",
                        "classifier": "global",
                        "dataSourceType": "Managed DataSource",
                        "category": "dataSource",
                        "children": [],
                        "driverType": "Derby",
                        "keyPath": "9i4e-8upi-3e1u-nir5-gk/h260-jheq-ywc5-mrtj-3s",
                        "key": "h260-jheq-ywc5-mrtj-3s",
                        "uuid": "h260-jheq-ywc5-mrtj-3s"
                    }
                ],
                "key": "9i4e-8upi-3e1u-nir5-gk",
                "uuid": "9i4e-8upi-3e1u-nir5-gk",
                "keyPath": "9i4e-8upi-3e1u-nir5-gk",
                "category": "dsGroup",
                "imgUrl": "images/data_sources/defaut_datasource.png"
            },
            {
                "name": "DynamicSwitch",
                "children": [
                    {
                        "permissionLevel": 5,
                        "driver": "dynamicSwitch",
                        "name": "Groovy 1",
                        "classifier": "efwd",
                        "type": "sql.jdbc.groovy.managed",
                        "data": {
                            "dir": "Gagan",
                            "driverName": null,
                            "type": "sql.jdbc.groovy.managed",
                            "id": 1,
                            "userName": null,
                            "password": null,
                            "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1001);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                            "jdbcUrl": null,
                            "driver": "dynamicSwitch"
                        },
                        "dataSourceType": "",
                        "category": "dataSource",
                        "children": [],
                        "driverType": "DynamicSwitch",
                        "keyPath": "791a-linv-1qrj-6lu9-bu/cpsk-b2hf-5r0z-g6ug-9z",
                        "key": "cpsk-b2hf-5r0z-g6ug-9z",
                        "uuid": "cpsk-b2hf-5r0z-g6ug-9z"
                    }
                ],
                "key": "791a-linv-1qrj-6lu9-bu",
                "uuid": "791a-linv-1qrj-6lu9-bu",
                "keyPath": "791a-linv-1qrj-6lu9-bu",
                "category": "dsGroup",
                "imgUrl": "images/data_sources/defaut_datasource.png"
            },
            {
                "name": "Postgresql",
                "children": [
                    {
                        "data": {
                            "id": "1001",
                            "type": "dynamicDataSource"
                        },
                        "dataSourceProvider": "tomcat",
                        "type": "dynamicDataSource",
                        "permissionLevel": 5,
                        "driver": "org.postgresql.Driver",
                        "name": "booking",
                        "classifier": "global",
                        "dataSourceType": "Managed DataSource",
                        "category": "dataSource",
                        "children": [],
                        "driverType": "Postgresql",
                        "keyPath": "im3a-4axa-m4wd-7m8l-jk/2lbn-qcxx-8wkz-all7-9p",
                        "key": "2lbn-qcxx-8wkz-all7-9p",
                        "uuid": "2lbn-qcxx-8wkz-all7-9p"
                    },
                    {
                        "data": {
                            "id": "1401",
                            "type": "dynamicDataSource"
                        },
                        "dataSourceProvider": "tomcat",
                        "type": "dynamicDataSource",
                        "permissionLevel": 5,
                        "driver": "org.postgresql.Driver",
                        "name": "booking",
                        "classifier": "global",
                        "dataSourceType": "Managed DataSource",
                        "category": "dataSource",
                        "children": [],
                        "driverType": "Postgresql",
                        "keyPath": "im3a-4axa-m4wd-7m8l-jk/van3-hyi5-dqjy-ixl2-ht",
                        "key": "van3-hyi5-dqjy-ixl2-ht",
                        "uuid": "van3-hyi5-dqjy-ixl2-ht"
                    }
                ],
                "key": "im3a-4axa-m4wd-7m8l-jk",
                "uuid": "im3a-4axa-m4wd-7m8l-jk",
                "keyPath": "im3a-4axa-m4wd-7m8l-jk",
                "category": "dsGroup",
                "imgUrl": "images/data_sources/defaut_datasource.png"
            },
            {
                "name": "Sqlite",
                "children": [
                    {
                        "data": {
                            "id": "1201",
                            "type": "dynamicDataSource"
                        },
                        "dataSourceProvider": "tomcat",
                        "type": "dynamicDataSource",
                        "permissionLevel": 5,
                        "driver": "org.sqlite.JDBC",
                        "name": "SampleTravelData",
                        "classifier": "global",
                        "dataSourceType": "Managed DataSource",
                        "category": "dataSource",
                        "children": [],
                        "driverType": "Sqlite",
                        "keyPath": "dgs9-48k7-op2w-aon5-tm/q9sp-2w4g-srx4-91k7-91",
                        "key": "q9sp-2w4g-srx4-91k7-91",
                        "uuid": "q9sp-2w4g-srx4-91k7-91"
                    },
                    {
                        "data": {
                            "id": "1302",
                            "type": "dynamicDataSource"
                        },
                        "dataSourceProvider": "tomcat",
                        "type": "dynamicDataSource",
                        "permissionLevel": 5,
                        "driver": "org.sqlite.JDBC",
                        "name": "Sqlite",
                        "classifier": "global",
                        "dataSourceType": "Managed DataSource",
                        "category": "dataSource",
                        "children": [],
                        "driverType": "Sqlite",
                        "keyPath": "dgs9-48k7-op2w-aon5-tm/fry5-61vw-ujzc-fb1a-0i",
                        "key": "fry5-61vw-ujzc-fb1a-0i",
                        "uuid": "fry5-61vw-ujzc-fb1a-0i"
                    }
                ],
                "key": "dgs9-48k7-op2w-aon5-tm",
                "uuid": "dgs9-48k7-op2w-aon5-tm",
                "keyPath": "dgs9-48k7-op2w-aon5-tm",
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
                    "dsUUID": "omo4-wtxl-zlri-cg0g-34",
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
                            "connId": "9fasa",
                            "classifier": "db.workflow",
                            "datasourceName": "SampleTravelDataDerby",
                            "dsKeyPath": "9i4e-8upi-3e1u-nir5-gk/omo4-wtxl-zlri-cg0g-34/nhwt-47ai-hcae-auhw-ec",
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
                "connId": "9fasa",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "9i4e-8upi-3e1u-nir5-gk/omo4-wtxl-zlri-cg0g-34/nhwt-47ai-hcae-auhw-ec",
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
                "keyPath": "9i4e-8upi-3e1u-nir5-gk/omo4-wtxl-zlri-cg0g-34/nhwt-47ai-hcae-auhw-ec/krff-5pzt-p69d-mhaq-uu",
                "key": "krff-5pzt-p69d-mhaq-uu",
                "alias": "dimdate",
                "uuid": "krff-5pzt-p69d-mhaq-uu",
                "connId": "9fasa",
                "dataSource": {
                    "id": "1",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc",
                    "catSchemaPredicted": false,
                    "sync": false,
                    "catalog": "",
                    "schema": "HIUSER",
                    "connId": "9fasa",
                    "classifier": "db.workflow",
                    "datasourceName": "SampleTravelDataDerby",
                    "dsKeyPath": "9i4e-8upi-3e1u-nir5-gk/omo4-wtxl-zlri-cg0g-34/nhwt-47ai-hcae-auhw-ec",
                    "driverType": "Derby",
                    "database": "HIUSER"
                },
                "dataSourceName": "SampleTravelDataDerby",
                "category": "table",
                "nameWithConnId": "dimdate_9fasa",
                "database": "HIUSER",
                "schema": "HIUSER",
                "selected": true,
                "keyName": "dimdate",
                "columnsFetched": true,
                "columns": {
                    "dim_id": {
                        "alias": "dim_id",
                        "fullyQualifiedColumn": "dimdate.dim_id",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        },
                        "category": "column",
                        "originalName": "dim_id",
                        "originalId": "21d2057f-0f32-40b8-a7d4-b3e60f941775",
                        "tableKey": "dimdate",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "dim_id",
                        "_columnId": "21d2057f-0f32-40b8-a7d4-b3e60f941775",
                        "uuid": "w6yb-rhza-ucpl-q02j-mf",
                        "originalColumnName": "dim_id"
                    },
                    "fiscal_year": {
                        "alias": "fiscal_year",
                        "fullyQualifiedColumn": "dimdate.fiscal_year",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.sql.Date": "date"
                        },
                        "category": "column",
                        "originalName": "fiscal_year",
                        "originalId": "d6b79fc3-249a-4df7-8329-e90858e7e340",
                        "tableKey": "dimdate",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "fiscal_year",
                        "_columnId": "d6b79fc3-249a-4df7-8329-e90858e7e340",
                        "uuid": "qv68-skvs-4zlt-aqfa-6h",
                        "originalColumnName": "fiscal_year"
                    },
                    "modified_date": {
                        "alias": "modified_date",
                        "fullyQualifiedColumn": "dimdate.modified_date",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.sql.Timestamp": "dateTime"
                        },
                        "category": "column",
                        "originalName": "modified_date",
                        "originalId": "c4935fcd-a208-4b70-bf9f-68034d1667bc",
                        "tableKey": "dimdate",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "modified_date",
                        "_columnId": "c4935fcd-a208-4b70-bf9f-68034d1667bc",
                        "uuid": "01fp-rmqj-fzhv-xtb0-vs",
                        "originalColumnName": "modified_date"
                    },
                    "date_key": {
                        "alias": "date_key",
                        "fullyQualifiedColumn": "dimdate.date_key",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "originalName": "date_key",
                        "originalId": "2b1fbe06-ca0a-4b9d-8d66-1ef7b1494f43",
                        "tableKey": "dimdate",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "date_key",
                        "_columnId": "2b1fbe06-ca0a-4b9d-8d66-1ef7b1494f43",
                        "uuid": "7aou-2pio-6th8-mn9n-mh",
                        "originalColumnName": "date_key"
                    },
                    "day_number": {
                        "alias": "day_number",
                        "fullyQualifiedColumn": "dimdate.day_number",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "originalName": "day_number",
                        "originalId": "dacf167a-82e1-44b0-a435-08c46961bdf2",
                        "tableKey": "dimdate",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "day_number",
                        "_columnId": "dacf167a-82e1-44b0-a435-08c46961bdf2",
                        "uuid": "637u-xqyb-eg02-vpzj-xr",
                        "originalColumnName": "day_number"
                    },
                    "fiscal_month_name": {
                        "alias": "fiscal_month_name",
                        "fullyQualifiedColumn": "dimdate.fiscal_month_name",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "originalName": "fiscal_month_name",
                        "originalId": "17695614-0388-410d-aac6-e7073f8a4f13",
                        "tableKey": "dimdate",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "fiscal_month_name",
                        "_columnId": "17695614-0388-410d-aac6-e7073f8a4f13",
                        "uuid": "2x87-1k4t-2btj-p9zv-si",
                        "originalColumnName": "fiscal_month_name"
                    },
                    "fiscal_month_label": {
                        "alias": "fiscal_month_label",
                        "fullyQualifiedColumn": "dimdate.fiscal_month_label",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "originalName": "fiscal_month_label",
                        "originalId": "172868ab-73ca-4128-a4d9-77b57d1a1703",
                        "tableKey": "dimdate",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "fiscal_month_label",
                        "_columnId": "172868ab-73ca-4128-a4d9-77b57d1a1703",
                        "uuid": "tyko-lysg-wprj-j4v8-u6",
                        "originalColumnName": "fiscal_month_label"
                    },
                    "created_date": {
                        "alias": "created_date",
                        "fullyQualifiedColumn": "dimdate.created_date",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "originalName": "created_date",
                        "originalId": "cf48b4c7-2da3-4598-8f61-f21cf57edb09",
                        "tableKey": "dimdate",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "created_date",
                        "_columnId": "cf48b4c7-2da3-4598-8f61-f21cf57edb09",
                        "uuid": "4nem-wk7c-grsr-uu22-w6",
                        "originalColumnName": "created_date"
                    },
                    "created_time": {
                        "alias": "created_time",
                        "fullyQualifiedColumn": "dimdate.created_time",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "originalName": "created_time",
                        "originalId": "e90d0f75-1938-4f14-916a-cdea891fb679",
                        "tableKey": "dimdate",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "created_time",
                        "_columnId": "e90d0f75-1938-4f14-916a-cdea891fb679",
                        "uuid": "2mlv-uiu8-0ayv-i9tq-j8",
                        "originalColumnName": "created_time"
                    },
                    "rating": {
                        "alias": "rating",
                        "fullyQualifiedColumn": "dimdate.rating",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "originalName": "rating",
                        "originalId": "1139b291-39b9-4f6b-8076-30ccb28ad620",
                        "tableKey": "dimdate",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "rating",
                        "_columnId": "1139b291-39b9-4f6b-8076-30ccb28ad620",
                        "uuid": "u0da-gu3n-a6st-odsq-3z",
                        "originalColumnName": "rating"
                    }
                },
                "duplicateIndex": 1
            },
            "employee_details": {
                "id": "4e1fd245f4d13b77be423a43f01d80b2",
                "name": "employee_details",
                "data": {
                    "id": "1",
                    "type": "dynamicDataSource"
                },
                "keyPath": "9i4e-8upi-3e1u-nir5-gk/omo4-wtxl-zlri-cg0g-34/nhwt-47ai-hcae-auhw-ec/hi91-hp8m-1tii-8yz2-8v",
                "key": "hi91-hp8m-1tii-8yz2-8v",
                "alias": "employee_details",
                "uuid": "hi91-hp8m-1tii-8yz2-8v",
                "connId": "9fasa",
                "dataSource": {
                    "id": "1",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc",
                    "catSchemaPredicted": false,
                    "sync": false,
                    "catalog": "",
                    "schema": "HIUSER",
                    "connId": "9fasa",
                    "classifier": "db.workflow",
                    "datasourceName": "SampleTravelDataDerby",
                    "dsKeyPath": "9i4e-8upi-3e1u-nir5-gk/omo4-wtxl-zlri-cg0g-34/nhwt-47ai-hcae-auhw-ec",
                    "driverType": "Derby",
                    "database": "HIUSER"
                },
                "dataSourceName": "SampleTravelDataDerby",
                "category": "table",
                "nameWithConnId": "employee_details_9fasa",
                "database": "HIUSER",
                "schema": "HIUSER",
                "selected": true,
                "keyName": "employee_details",
                "columnsFetched": true,
                "columns": {
                    "employee_id": {
                        "alias": "employee_id",
                        "fullyQualifiedColumn": "employee_details.employee_id",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        },
                        "category": "column",
                        "originalName": "employee_id",
                        "originalId": "c3467f5a-113c-4499-9618-8785df127187",
                        "tableKey": "employee_details",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "employee_id",
                        "_columnId": "c3467f5a-113c-4499-9618-8785df127187",
                        "uuid": "2ao9-5yu2-2k4m-dl3l-z0"
                    },
                    "employee_name": {
                        "alias": "employee_name",
                        "fullyQualifiedColumn": "employee_details.employee_name",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "originalName": "employee_name",
                        "originalId": "b8a4bb14-4e14-4278-8f94-47217c631c41",
                        "tableKey": "employee_details",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "employee_name",
                        "_columnId": "b8a4bb14-4e14-4278-8f94-47217c631c41",
                        "uuid": "7ghj-v5w5-a1i6-x2so-lx"
                    },
                    "age": {
                        "alias": "age",
                        "fullyQualifiedColumn": "employee_details.age",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        },
                        "category": "column",
                        "originalName": "age",
                        "originalId": "70f9e392-9fed-4ce3-bbaa-c9c408fcee0d",
                        "tableKey": "employee_details",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "age",
                        "_columnId": "70f9e392-9fed-4ce3-bbaa-c9c408fcee0d",
                        "uuid": "bf9a-nkgi-yojv-cnpz-8h"
                    },
                    "address": {
                        "alias": "address",
                        "fullyQualifiedColumn": "employee_details.address",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "originalName": "address",
                        "originalId": "700500e9-c1dc-46cd-86e7-cda8f8e3abaa",
                        "tableKey": "employee_details",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "address",
                        "_columnId": "700500e9-c1dc-46cd-86e7-cda8f8e3abaa",
                        "uuid": "jey6-otf6-m984-5o32-8v"
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
                "keyPath": "9i4e-8upi-3e1u-nir5-gk/omo4-wtxl-zlri-cg0g-34/nhwt-47ai-hcae-auhw-ec/cmrh-yof7-tp2v-752g-6i",
                "key": "cmrh-yof7-tp2v-752g-6i",
                "alias": "geo_cordinates",
                "uuid": "cmrh-yof7-tp2v-752g-6i",
                "connId": "9fasa",
                "dataSource": {
                    "id": "1",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc",
                    "catSchemaPredicted": false,
                    "sync": false,
                    "catalog": "",
                    "schema": "HIUSER",
                    "connId": "9fasa",
                    "classifier": "db.workflow",
                    "datasourceName": "SampleTravelDataDerby",
                    "dsKeyPath": "9i4e-8upi-3e1u-nir5-gk/omo4-wtxl-zlri-cg0g-34/nhwt-47ai-hcae-auhw-ec",
                    "driverType": "Derby",
                    "database": "HIUSER"
                },
                "dataSourceName": "SampleTravelDataDerby",
                "category": "table",
                "nameWithConnId": "geo_cordinates_9fasa",
                "database": "HIUSER",
                "schema": "HIUSER",
                "selected": true,
                "keyName": "geo_cordinates",
                "columnsFetched": true,
                "columns": {
                    "location_id": {
                        "alias": "location_id",
                        "fullyQualifiedColumn": "geo_cordinates.location_id",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        },
                        "category": "column",
                        "originalName": "location_id",
                        "originalId": "9372ce3f-4c32-45a8-9cfc-0ba6ca8355a1",
                        "tableKey": "geo_cordinates",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "location_id",
                        "_columnId": "9372ce3f-4c32-45a8-9cfc-0ba6ca8355a1",
                        "uuid": "8drc-l4vi-yxj4-rjj5-f9"
                    },
                    "location": {
                        "alias": "location",
                        "fullyQualifiedColumn": "geo_cordinates.location",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "originalName": "location",
                        "originalId": "89f775ee-f7de-4c5b-a43e-fef10fca8025",
                        "tableKey": "geo_cordinates",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "location",
                        "_columnId": "89f775ee-f7de-4c5b-a43e-fef10fca8025",
                        "uuid": "dpy7-5t6y-0o6g-337y-bt"
                    },
                    "latitude": {
                        "alias": "latitude",
                        "fullyQualifiedColumn": "geo_cordinates.latitude",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Double": "numeric"
                        },
                        "category": "column",
                        "originalName": "latitude",
                        "originalId": "78a58631-396b-4d7b-8d76-ac511c1bb25c",
                        "tableKey": "geo_cordinates",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "latitude",
                        "_columnId": "78a58631-396b-4d7b-8d76-ac511c1bb25c",
                        "uuid": "07pv-1ls9-x76y-v16z-l4"
                    },
                    "longitude": {
                        "alias": "longitude",
                        "fullyQualifiedColumn": "geo_cordinates.longitude",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Double": "numeric"
                        },
                        "category": "column",
                        "originalName": "longitude",
                        "originalId": "42579d7f-9f9e-4fae-8a7c-150502bd5bf7",
                        "tableKey": "geo_cordinates",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "longitude",
                        "_columnId": "42579d7f-9f9e-4fae-8a7c-150502bd5bf7",
                        "uuid": "99aa-cje8-q2me-fjoj-to"
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
                "keyPath": "9i4e-8upi-3e1u-nir5-gk/omo4-wtxl-zlri-cg0g-34/nhwt-47ai-hcae-auhw-ec/7chp-ga97-kt6s-o69a-rt",
                "key": "7chp-ga97-kt6s-o69a-rt",
                "alias": "meeting_details",
                "uuid": "7chp-ga97-kt6s-o69a-rt",
                "connId": "9fasa",
                "dataSource": {
                    "id": "1",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc",
                    "catSchemaPredicted": false,
                    "sync": false,
                    "catalog": "",
                    "schema": "HIUSER",
                    "connId": "9fasa",
                    "classifier": "db.workflow",
                    "datasourceName": "SampleTravelDataDerby",
                    "dsKeyPath": "9i4e-8upi-3e1u-nir5-gk/omo4-wtxl-zlri-cg0g-34/nhwt-47ai-hcae-auhw-ec",
                    "driverType": "Derby",
                    "database": "HIUSER"
                },
                "dataSourceName": "SampleTravelDataDerby",
                "category": "table",
                "nameWithConnId": "meeting_details_9fasa",
                "database": "HIUSER",
                "schema": "HIUSER",
                "selected": true,
                "keyName": "meeting_details",
                "columnsFetched": true,
                "columns": {
                    "meeting_id": {
                        "alias": "meeting_id",
                        "fullyQualifiedColumn": "meeting_details.meeting_id",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        },
                        "category": "column",
                        "originalName": "meeting_id",
                        "originalId": "48e2dbc1-b60f-49d8-855a-1c8db2debd38",
                        "tableKey": "meeting_details",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "meeting_id",
                        "_columnId": "48e2dbc1-b60f-49d8-855a-1c8db2debd38",
                        "uuid": "j84f-2dcn-giyp-4xno-ad"
                    },
                    "meeting_date": {
                        "alias": "meeting_date",
                        "fullyQualifiedColumn": "meeting_details.meeting_date",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.sql.Timestamp": "dateTime"
                        },
                        "category": "column",
                        "originalName": "meeting_date",
                        "originalId": "6274ee76-e090-49e0-a8c4-88e12f690147",
                        "tableKey": "meeting_details",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "meeting_date",
                        "_columnId": "6274ee76-e090-49e0-a8c4-88e12f690147",
                        "uuid": "jbr7-1ukc-vym4-0cav-p6"
                    },
                    "meeting_by": {
                        "alias": "meeting_by",
                        "fullyQualifiedColumn": "meeting_details.meeting_by",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        },
                        "category": "column",
                        "originalName": "meeting_by",
                        "originalId": "f5188782-efb2-4af5-bbfa-98c0e0e66af1",
                        "tableKey": "meeting_details",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "meeting_by",
                        "_columnId": "f5188782-efb2-4af5-bbfa-98c0e0e66af1",
                        "uuid": "epbz-kkmj-tieh-zmfv-ef"
                    },
                    "client_name": {
                        "alias": "client_name",
                        "fullyQualifiedColumn": "meeting_details.client_name",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "originalName": "client_name",
                        "originalId": "82d4a44c-054b-4d62-a3b7-0b250709da03",
                        "tableKey": "meeting_details",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "client_name",
                        "_columnId": "82d4a44c-054b-4d62-a3b7-0b250709da03",
                        "uuid": "k197-odoq-5rkt-j3ho-sb"
                    },
                    "meeting_purpose": {
                        "alias": "meeting_purpose",
                        "fullyQualifiedColumn": "meeting_details.meeting_purpose",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "originalName": "meeting_purpose",
                        "originalId": "346f00f0-c5a1-4bc5-a2d6-dd87a3346314",
                        "tableKey": "meeting_details",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "meeting_purpose",
                        "_columnId": "346f00f0-c5a1-4bc5-a2d6-dd87a3346314",
                        "uuid": "ep11-p30m-20mf-mw2l-bn"
                    },
                    "meeting_impact": {
                        "alias": "meeting_impact",
                        "fullyQualifiedColumn": "meeting_details.meeting_impact",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "originalName": "meeting_impact",
                        "originalId": "0c23a336-eeae-4761-b6a5-280845cef358",
                        "tableKey": "meeting_details",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "meeting_impact",
                        "_columnId": "0c23a336-eeae-4761-b6a5-280845cef358",
                        "uuid": "869f-d4xn-xffh-19gn-0n"
                    },
                    "meet_cancellation_status": {
                        "alias": "meet_cancellation_status",
                        "fullyQualifiedColumn": "meeting_details.meet_cancellation_status",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "originalName": "meet_cancellation_status",
                        "originalId": "1ecaeb93-4ad5-4e81-8495-f347c4f18057",
                        "tableKey": "meeting_details",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "meet_cancellation_status",
                        "_columnId": "1ecaeb93-4ad5-4e81-8495-f347c4f18057",
                        "uuid": "gjhe-b42u-7gzb-dmcm-e7"
                    },
                    "cancellation_reason": {
                        "alias": "cancellation_reason",
                        "fullyQualifiedColumn": "meeting_details.cancellation_reason",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "originalName": "cancellation_reason",
                        "originalId": "17144b0b-6ede-4447-9408-da66a8a22475",
                        "tableKey": "meeting_details",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "cancellation_reason",
                        "_columnId": "17144b0b-6ede-4447-9408-da66a8a22475",
                        "uuid": "tn1x-gi21-6bje-t4w3-s3"
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
                "keyPath": "9i4e-8upi-3e1u-nir5-gk/omo4-wtxl-zlri-cg0g-34/nhwt-47ai-hcae-auhw-ec/pxrv-mbma-tacr-h3fo-gm",
                "key": "pxrv-mbma-tacr-h3fo-gm",
                "alias": "travel_details",
                "uuid": "pxrv-mbma-tacr-h3fo-gm",
                "connId": "9fasa",
                "dataSource": {
                    "id": "1",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc",
                    "catSchemaPredicted": false,
                    "sync": false,
                    "catalog": "",
                    "schema": "HIUSER",
                    "connId": "9fasa",
                    "classifier": "db.workflow",
                    "datasourceName": "SampleTravelDataDerby",
                    "dsKeyPath": "9i4e-8upi-3e1u-nir5-gk/omo4-wtxl-zlri-cg0g-34/nhwt-47ai-hcae-auhw-ec",
                    "driverType": "Derby",
                    "database": "HIUSER"
                },
                "dataSourceName": "SampleTravelDataDerby",
                "category": "table",
                "nameWithConnId": "travel_details_9fasa",
                "database": "HIUSER",
                "schema": "HIUSER",
                "selected": true,
                "keyName": "travel_details",
                "columnsFetched": true,
                "columns": {
                    "travel_id": {
                        "alias": "travel_id",
                        "fullyQualifiedColumn": "travel_details.travel_id",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        },
                        "category": "column",
                        "originalName": "travel_id",
                        "originalId": "8103eb38-761f-469d-b686-f21888711204",
                        "tableKey": "travel_details",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "travel_id",
                        "_columnId": "8103eb38-761f-469d-b686-f21888711204",
                        "uuid": "7n8m-uy2o-j20h-fhuv-2m"
                    },
                    "travel_date": {
                        "alias": "travel_date",
                        "fullyQualifiedColumn": "travel_details.travel_date",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.sql.Timestamp": "dateTime"
                        },
                        "category": "column",
                        "originalName": "travel_date",
                        "originalId": "4b0683c5-7dd4-40cb-8d0c-6e7f6dd307ce",
                        "tableKey": "travel_details",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "travel_date",
                        "_columnId": "4b0683c5-7dd4-40cb-8d0c-6e7f6dd307ce",
                        "uuid": "7ev7-wcxn-2mz7-ylk5-mz"
                    },
                    "travel_type": {
                        "alias": "travel_type",
                        "fullyQualifiedColumn": "travel_details.travel_type",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "originalName": "travel_type",
                        "originalId": "6b06dd2d-f9d7-469d-bb4a-127bc516f33b",
                        "tableKey": "travel_details",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "travel_type",
                        "_columnId": "6b06dd2d-f9d7-469d-bb4a-127bc516f33b",
                        "uuid": "yqvq-vvm8-fkj1-wrrx-bm"
                    },
                    "travel_medium": {
                        "alias": "travel_medium",
                        "fullyQualifiedColumn": "travel_details.travel_medium",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "originalName": "travel_medium",
                        "originalId": "e4a0f3f7-45fe-433c-b298-ffea95daf206",
                        "tableKey": "travel_details",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "travel_medium",
                        "_columnId": "e4a0f3f7-45fe-433c-b298-ffea95daf206",
                        "uuid": "d42m-88k9-i747-i9kl-p4"
                    },
                    "source_id": {
                        "alias": "source_id",
                        "fullyQualifiedColumn": "travel_details.source_id",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        },
                        "category": "column",
                        "originalName": "source_id",
                        "originalId": "a95e861e-929c-4ed3-96c7-90fa9a7d8718",
                        "tableKey": "travel_details",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "source_id",
                        "_columnId": "a95e861e-929c-4ed3-96c7-90fa9a7d8718",
                        "uuid": "hrc8-ur75-n8jh-u2is-im"
                    },
                    "source": {
                        "alias": "source",
                        "fullyQualifiedColumn": "travel_details.source",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "originalName": "source",
                        "originalId": "c823a243-7f27-49c5-8707-8d9f67ced4b7",
                        "tableKey": "travel_details",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "source",
                        "_columnId": "c823a243-7f27-49c5-8707-8d9f67ced4b7",
                        "uuid": "34v6-8k2q-7r5c-8ews-6q"
                    },
                    "destination_id": {
                        "alias": "destination_id",
                        "fullyQualifiedColumn": "travel_details.destination_id",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        },
                        "category": "column",
                        "originalName": "destination_id",
                        "originalId": "f30f759d-0dc2-46f3-a2c2-e12cac105d0a",
                        "tableKey": "travel_details",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "destination_id",
                        "_columnId": "f30f759d-0dc2-46f3-a2c2-e12cac105d0a",
                        "uuid": "gnxb-585h-0emb-8pki-zx"
                    },
                    "destination": {
                        "alias": "destination",
                        "fullyQualifiedColumn": "travel_details.destination",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "originalName": "destination",
                        "originalId": "10de500a-928f-47ea-a60e-97b0a8608082",
                        "tableKey": "travel_details",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "destination",
                        "_columnId": "10de500a-928f-47ea-a60e-97b0a8608082",
                        "uuid": "lijg-2vin-fovs-tni8-vm"
                    },
                    "travel_cost": {
                        "alias": "travel_cost",
                        "fullyQualifiedColumn": "travel_details.travel_cost",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        },
                        "category": "column",
                        "originalName": "travel_cost",
                        "originalId": "25073520-c5da-407e-b582-3443640e030b",
                        "tableKey": "travel_details",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "travel_cost",
                        "_columnId": "25073520-c5da-407e-b582-3443640e030b",
                        "uuid": "zfi9-sxdi-ig0m-ijxg-mi"
                    },
                    "mode_of_payment": {
                        "alias": "mode_of_payment",
                        "fullyQualifiedColumn": "travel_details.mode_of_payment",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "originalName": "mode_of_payment",
                        "originalId": "fc82d24f-2312-4e14-941f-d7a6f387516c",
                        "tableKey": "travel_details",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "mode_of_payment",
                        "_columnId": "fc82d24f-2312-4e14-941f-d7a6f387516c",
                        "uuid": "hbot-0qyg-crjo-e0jj-nd"
                    },
                    "booking_platform": {
                        "alias": "booking_platform",
                        "fullyQualifiedColumn": "travel_details.booking_platform",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "originalName": "booking_platform",
                        "originalId": "c73590df-2565-42cf-a154-725cc301f3ec",
                        "tableKey": "travel_details",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "booking_platform",
                        "_columnId": "c73590df-2565-42cf-a154-725cc301f3ec",
                        "uuid": "u9mo-1gjz-usd5-j1pn-cs"
                    },
                    "travelled_by": {
                        "alias": "travelled_by",
                        "fullyQualifiedColumn": "travel_details.travelled_by",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        },
                        "category": "column",
                        "originalName": "travelled_by",
                        "originalId": "577329b3-91c2-4f2e-9d8e-cc2d5d8468c4",
                        "tableKey": "travel_details",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "travelled_by",
                        "_columnId": "577329b3-91c2-4f2e-9d8e-cc2d5d8468c4",
                        "uuid": "b6e3-2dqo-rqjm-iujv-2q"
                    }
                }
            },
            "dimdate_1": {
                "name": "dimdate",
                "data": {
                    "id": "1",
                    "type": "dynamicDataSource"
                },
                "keyPath": "9i4e-8upi-3e1u-nir5-gk/omo4-wtxl-zlri-cg0g-34/nhwt-47ai-hcae-auhw-ec/h8ic-6q6z-87e2-38x4-vs",
                "key": "h8ic-6q6z-87e2-38x4-vs",
                "alias": "dimdate_1",
                "uuid": "h8ic-6q6z-87e2-38x4-vs",
                "connId": "9fasa",
                "dataSource": {
                    "id": "1",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc",
                    "catSchemaPredicted": false,
                    "sync": false,
                    "catalog": "",
                    "schema": "HIUSER",
                    "connId": "9fasa",
                    "classifier": "db.workflow",
                    "datasourceName": "SampleTravelDataDerby",
                    "dsKeyPath": "9i4e-8upi-3e1u-nir5-gk/omo4-wtxl-zlri-cg0g-34/nhwt-47ai-hcae-auhw-ec",
                    "driverType": "Derby",
                    "database": "HIUSER"
                },
                "dataSourceName": "SampleTravelDataDerby",
                "category": "table",
                "nameWithConnId": "dimdate_9fasa",
                "database": "HIUSER",
                "schema": "HIUSER",
                "selected": true,
                "keyName": "dimdate_1",
                "columnsFetched": true,
                "columns": {
                    "dim_id": {
                        "alias": "dim_id",
                        "fullyQualifiedColumn": "dimdate.dim_id",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        },
                        "category": "column",
                        "originalName": "dim_id",
                        "originalId": "21d2057f-0f32-40b8-a7d4-b3e60f941775",
                        "tableKey": "dimdate_1",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "dim_id",
                        "_columnId": "21d2057f-0f32-40b8-a7d4-b3e60f941775",
                        "uuid": "v8za-9cya-bwlo-2zef-99",
                        "originalColumnName": "dim_id"
                    },
                    "fiscal_year": {
                        "alias": "fiscal_year",
                        "fullyQualifiedColumn": "dimdate.fiscal_year",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.sql.Date": "date"
                        },
                        "category": "column",
                        "originalName": "fiscal_year",
                        "originalId": "d6b79fc3-249a-4df7-8329-e90858e7e340",
                        "tableKey": "dimdate_1",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "fiscal_year",
                        "_columnId": "d6b79fc3-249a-4df7-8329-e90858e7e340",
                        "uuid": "10zn-fgaz-juox-xxag-n4",
                        "originalColumnName": "fiscal_year"
                    },
                    "modified_date": {
                        "alias": "modified_date",
                        "fullyQualifiedColumn": "dimdate.modified_date",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.sql.Timestamp": "dateTime"
                        },
                        "category": "column",
                        "originalName": "modified_date",
                        "originalId": "c4935fcd-a208-4b70-bf9f-68034d1667bc",
                        "tableKey": "dimdate_1",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "modified_date",
                        "_columnId": "c4935fcd-a208-4b70-bf9f-68034d1667bc",
                        "uuid": "bdpr-g902-norq-wnqv-ke",
                        "originalColumnName": "modified_date"
                    },
                    "date_key": {
                        "alias": "date_key",
                        "fullyQualifiedColumn": "dimdate.date_key",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "originalName": "date_key",
                        "originalId": "2b1fbe06-ca0a-4b9d-8d66-1ef7b1494f43",
                        "tableKey": "dimdate_1",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "date_key",
                        "_columnId": "2b1fbe06-ca0a-4b9d-8d66-1ef7b1494f43",
                        "uuid": "gn7b-xi3f-xfhi-6z15-75",
                        "originalColumnName": "date_key"
                    },
                    "day_number": {
                        "alias": "day_number",
                        "fullyQualifiedColumn": "dimdate.day_number",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "originalName": "day_number",
                        "originalId": "dacf167a-82e1-44b0-a435-08c46961bdf2",
                        "tableKey": "dimdate_1",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "day_number",
                        "_columnId": "dacf167a-82e1-44b0-a435-08c46961bdf2",
                        "uuid": "hz8z-lefi-4ddw-hfnc-cu",
                        "originalColumnName": "day_number"
                    },
                    "fiscal_month_name": {
                        "alias": "fiscal_month_name",
                        "fullyQualifiedColumn": "dimdate.fiscal_month_name",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "originalName": "fiscal_month_name",
                        "originalId": "17695614-0388-410d-aac6-e7073f8a4f13",
                        "tableKey": "dimdate_1",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "fiscal_month_name",
                        "_columnId": "17695614-0388-410d-aac6-e7073f8a4f13",
                        "uuid": "6wn2-42vj-k5xa-6yw2-g1",
                        "originalColumnName": "fiscal_month_name"
                    },
                    "fiscal_month_label": {
                        "alias": "fiscal_month_label",
                        "fullyQualifiedColumn": "dimdate.fiscal_month_label",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "originalName": "fiscal_month_label",
                        "originalId": "172868ab-73ca-4128-a4d9-77b57d1a1703",
                        "tableKey": "dimdate_1",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "fiscal_month_label",
                        "_columnId": "172868ab-73ca-4128-a4d9-77b57d1a1703",
                        "uuid": "ydfo-ix0t-el8q-o7ro-kb",
                        "originalColumnName": "fiscal_month_label"
                    },
                    "created_date": {
                        "alias": "created_date",
                        "fullyQualifiedColumn": "dimdate.created_date",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "originalName": "created_date",
                        "originalId": "cf48b4c7-2da3-4598-8f61-f21cf57edb09",
                        "tableKey": "dimdate_1",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "created_date",
                        "_columnId": "cf48b4c7-2da3-4598-8f61-f21cf57edb09",
                        "uuid": "xjvc-2jmr-vk1i-5yv0-jm",
                        "originalColumnName": "created_date"
                    },
                    "created_time": {
                        "alias": "created_time",
                        "fullyQualifiedColumn": "dimdate.created_time",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "originalName": "created_time",
                        "originalId": "e90d0f75-1938-4f14-916a-cdea891fb679",
                        "tableKey": "dimdate_1",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "created_time",
                        "_columnId": "e90d0f75-1938-4f14-916a-cdea891fb679",
                        "uuid": "7bd0-h6hk-vhy1-kyrw-0v",
                        "originalColumnName": "created_time"
                    },
                    "rating": {
                        "alias": "rating",
                        "fullyQualifiedColumn": "dimdate.rating",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "originalName": "rating",
                        "originalId": "1139b291-39b9-4f6b-8076-30ccb28ad620",
                        "tableKey": "dimdate_1",
                        "connId": "9fasa",
                        "duplicateIndex": 0,
                        "columnKey": "rating",
                        "_columnId": "1139b291-39b9-4f6b-8076-30ccb28ad620",
                        "uuid": "c99b-qtb7-o5s2-h7m4-5f",
                        "originalColumnName": "rating"
                    }
                },
                "duplicateIndex": 0,
                "root": "dimdate",
                "duplicate": true,
                "originalId": "4ac5d9f68b58bd7c0d179146e46795be",
                "originalName": "dimdate_1"
            }
        },
        "views": [],
        "activeView": false,
        "categories": {
            "9i4e-8upi-3e1u-nir5-gk": {
                "ds": {
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
                },
                "category": {
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
                }
            },
            "791a-linv-1qrj-6lu9-bu": {
                "ds": {
                    "permissionLevel": 5,
                    "driver": "dynamicSwitch",
                    "name": "Groovy 1",
                    "classifier": "efwd",
                    "type": "sql.jdbc.groovy.managed",
                    "data": {
                        "dir": "Gagan",
                        "driverName": null,
                        "type": "sql.jdbc.groovy.managed",
                        "id": 1,
                        "userName": null,
                        "password": null,
                        "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1001);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
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
            "im3a-4axa-m4wd-7m8l-jk": {
                "ds": {
                    "data": {
                        "id": "1001",
                        "type": "dynamicDataSource"
                    },
                    "dataSourceProvider": "tomcat",
                    "type": "dynamicDataSource",
                    "permissionLevel": 5,
                    "driver": "org.postgresql.Driver",
                    "name": "booking",
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
            "dgs9-48k7-op2w-aon5-tm": {
                "ds": {
                    "data": {
                        "id": "1201",
                        "type": "dynamicDataSource"
                    },
                    "dataSourceProvider": "tomcat",
                    "type": "dynamicDataSource",
                    "permissionLevel": 5,
                    "driver": "org.sqlite.JDBC",
                    "name": "SampleTravelData",
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
                "connId": "9fasa",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "9i4e-8upi-3e1u-nir5-gk/omo4-wtxl-zlri-cg0g-34/nhwt-47ai-hcae-auhw-ec",
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
            "location": "Sibtain",
            "uuid": "Metadatacheck.metadata"
        },
        "savedTableIds": [
            "4ac5d9f68b58bd7c0d179146e46795be",
            "4e1fd245f4d13b77be423a43f01d80b2",
            "be534112989b616b194bc59c2fb25a42",
            "9645c648a1c0dbeec1287aaf1e996db3",
            "8a28627d07d04ef096d9935f12e0c7e9"
        ],
        "savedColumnIds": [],
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
                        "connectionDatabaseId": "f983269f-57e9-4292-a5ed-625d22673e92",
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
                        "connectionDatabaseId": "f983269f-57e9-4292-a5ed-625d22673e92",
                        "catSchemaPredicted": false,
                        "catalog": "",
                        "schema": "HIUSER",
                        "type": "dynamicDataSource",
                        "baseType": "global.jdbc"
                    }
                },
                "index": 1,
                "action": "noChange",
                "uuid": "7uef-mktp-lqb2-z6ht-ic"
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
                        "connectionDatabaseId": "f983269f-57e9-4292-a5ed-625d22673e92",
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
                        "connectionDatabaseId": "f983269f-57e9-4292-a5ed-625d22673e92",
                        "catSchemaPredicted": false,
                        "catalog": "",
                        "schema": "HIUSER",
                        "type": "dynamicDataSource",
                        "baseType": "global.jdbc"
                    }
                },
                "index": 2,
                "action": "noChange",
                "uuid": "xlrn-649r-k8vv-pzhe-zf"
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
                        "connectionDatabaseId": "f983269f-57e9-4292-a5ed-625d22673e92",
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
                        "connectionDatabaseId": "f983269f-57e9-4292-a5ed-625d22673e92",
                        "catSchemaPredicted": false,
                        "catalog": "",
                        "schema": "HIUSER",
                        "type": "dynamicDataSource",
                        "baseType": "global.jdbc"
                    }
                },
                "index": 3,
                "action": "noChange",
                "uuid": "itt7-8too-kw27-20a5-y4"
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
                        "connectionDatabaseId": "f983269f-57e9-4292-a5ed-625d22673e92",
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
                        "connectionDatabaseId": "f983269f-57e9-4292-a5ed-625d22673e92",
                        "catSchemaPredicted": false,
                        "catalog": "",
                        "schema": "HIUSER",
                        "type": "dynamicDataSource",
                        "baseType": "global.jdbc"
                    }
                },
                "index": 4,
                "action": "noChange",
                "uuid": "m3pa-bb9x-xahi-ku5w-yt"
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
                        "connectionDatabaseId": "f983269f-57e9-4292-a5ed-625d22673e92",
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
                        "connectionDatabaseId": "f983269f-57e9-4292-a5ed-625d22673e92",
                        "catSchemaPredicted": false,
                        "catalog": "",
                        "schema": "HIUSER",
                        "type": "dynamicDataSource",
                        "baseType": "global.jdbc"
                    }
                },
                "index": 5,
                "action": "noChange",
                "uuid": "htbp-rvsc-5wt8-zd41-mb"
            }
        ],
        "mode": "edit",
        "allTablesKeys": [
            "cmrh-yof7-tp2v-752g-6i",
            "7chp-ga97-kt6s-o69a-rt",
            "hi91-hp8m-1tii-8yz2-8v",
            "krff-5pzt-p69d-mhaq-uu",
            "pxrv-mbma-tacr-h3fo-gm"
        ],
        "selectedTableKeys": [
            "krff-5pzt-p69d-mhaq-uu",
            "hi91-hp8m-1tii-8yz2-8v",
            "cmrh-yof7-tp2v-752g-6i",
            "7chp-ga97-kt6s-o69a-rt",
            "pxrv-mbma-tacr-h3fo-gm"
        ],
        "metadataName": "Metadatacheck",
        "activeDataSource": false,
        "metadataToEdit": false,
        "isSavingInProgress": false,
        "editViewsTempData": {},
        "inititalStateFromJest": false,
        "timeStamp": 1670479634792,
        "initialEditResponse": {
            "classifier": "db.generic",
            "name": "HIUSER",
            "dataSource": {
                "sync": false,
                "id": "1",
                "connectionDatabaseId": "f983269f-57e9-4292-a5ed-625d22673e92",
                "catSchemaPredicted": false,
                "catalog": "",
                "schema": "HIUSER",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc"
            },
            "uniqueId": "Metadatacheck",
            "tables": {
                "dimdate": {
                    "id": "4ac5d9f68b58bd7c0d179146e46795be",
                    "alias": "dimdate",
                    "columns": {
                        "dim_id": {
                            "alias": "dim_id",
                            "fullyQualifiedColumn": "dimdate.dim_id",
                            "columnId": "21d2057f-0f32-40b8-a7d4-b3e60f941775",
                            "defaultFunction": "db.generic.aggregate.sum",
                            "type": {
                                "java.lang.Integer": "numeric"
                            }
                        },
                        "fiscal_year": {
                            "alias": "fiscal_year",
                            "fullyQualifiedColumn": "dimdate.fiscal_year",
                            "columnId": "d6b79fc3-249a-4df7-8329-e90858e7e340",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.sql.Date": "date"
                            }
                        },
                        "modified_date": {
                            "alias": "modified_date",
                            "fullyQualifiedColumn": "dimdate.modified_date",
                            "columnId": "c4935fcd-a208-4b70-bf9f-68034d1667bc",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.sql.Timestamp": "dateTime"
                            }
                        },
                        "date_key": {
                            "alias": "date_key",
                            "fullyQualifiedColumn": "dimdate.date_key",
                            "columnId": "2b1fbe06-ca0a-4b9d-8d66-1ef7b1494f43",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            }
                        },
                        "day_number": {
                            "alias": "day_number",
                            "fullyQualifiedColumn": "dimdate.day_number",
                            "columnId": "dacf167a-82e1-44b0-a435-08c46961bdf2",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            }
                        },
                        "fiscal_month_name": {
                            "alias": "fiscal_month_name",
                            "fullyQualifiedColumn": "dimdate.fiscal_month_name",
                            "columnId": "17695614-0388-410d-aac6-e7073f8a4f13",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            }
                        },
                        "fiscal_month_label": {
                            "alias": "fiscal_month_label",
                            "fullyQualifiedColumn": "dimdate.fiscal_month_label",
                            "columnId": "172868ab-73ca-4128-a4d9-77b57d1a1703",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            }
                        },
                        "created_date": {
                            "alias": "created_date",
                            "fullyQualifiedColumn": "dimdate.created_date",
                            "columnId": "cf48b4c7-2da3-4598-8f61-f21cf57edb09",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            }
                        },
                        "created_time": {
                            "alias": "created_time",
                            "fullyQualifiedColumn": "dimdate.created_time",
                            "columnId": "e90d0f75-1938-4f14-916a-cdea891fb679",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            }
                        },
                        "rating": {
                            "alias": "rating",
                            "fullyQualifiedColumn": "dimdate.rating",
                            "columnId": "1139b291-39b9-4f6b-8076-30ccb28ad620",
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
                            "columnId": "c3467f5a-113c-4499-9618-8785df127187",
                            "defaultFunction": "db.generic.aggregate.sum",
                            "type": {
                                "java.lang.Integer": "numeric"
                            }
                        },
                        "employee_name": {
                            "alias": "employee_name",
                            "fullyQualifiedColumn": "employee_details.employee_name",
                            "columnId": "b8a4bb14-4e14-4278-8f94-47217c631c41",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            }
                        },
                        "age": {
                            "alias": "age",
                            "fullyQualifiedColumn": "employee_details.age",
                            "columnId": "70f9e392-9fed-4ce3-bbaa-c9c408fcee0d",
                            "defaultFunction": "db.generic.aggregate.sum",
                            "type": {
                                "java.lang.Integer": "numeric"
                            }
                        },
                        "address": {
                            "alias": "address",
                            "fullyQualifiedColumn": "employee_details.address",
                            "columnId": "700500e9-c1dc-46cd-86e7-cda8f8e3abaa",
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
                            "columnId": "9372ce3f-4c32-45a8-9cfc-0ba6ca8355a1",
                            "defaultFunction": "db.generic.aggregate.sum",
                            "type": {
                                "java.lang.Integer": "numeric"
                            }
                        },
                        "location": {
                            "alias": "location",
                            "fullyQualifiedColumn": "geo_cordinates.location",
                            "columnId": "89f775ee-f7de-4c5b-a43e-fef10fca8025",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            }
                        },
                        "latitude": {
                            "alias": "latitude",
                            "fullyQualifiedColumn": "geo_cordinates.latitude",
                            "columnId": "78a58631-396b-4d7b-8d76-ac511c1bb25c",
                            "defaultFunction": "db.generic.aggregate.sum",
                            "type": {
                                "java.lang.Double": "numeric"
                            }
                        },
                        "longitude": {
                            "alias": "longitude",
                            "fullyQualifiedColumn": "geo_cordinates.longitude",
                            "columnId": "42579d7f-9f9e-4fae-8a7c-150502bd5bf7",
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
                            "columnId": "48e2dbc1-b60f-49d8-855a-1c8db2debd38",
                            "defaultFunction": "db.generic.aggregate.sum",
                            "type": {
                                "java.lang.Integer": "numeric"
                            }
                        },
                        "meeting_date": {
                            "alias": "meeting_date",
                            "fullyQualifiedColumn": "meeting_details.meeting_date",
                            "columnId": "6274ee76-e090-49e0-a8c4-88e12f690147",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.sql.Timestamp": "dateTime"
                            }
                        },
                        "meeting_by": {
                            "alias": "meeting_by",
                            "fullyQualifiedColumn": "meeting_details.meeting_by",
                            "columnId": "f5188782-efb2-4af5-bbfa-98c0e0e66af1",
                            "defaultFunction": "db.generic.aggregate.sum",
                            "type": {
                                "java.lang.Integer": "numeric"
                            }
                        },
                        "client_name": {
                            "alias": "client_name",
                            "fullyQualifiedColumn": "meeting_details.client_name",
                            "columnId": "82d4a44c-054b-4d62-a3b7-0b250709da03",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            }
                        },
                        "meeting_purpose": {
                            "alias": "meeting_purpose",
                            "fullyQualifiedColumn": "meeting_details.meeting_purpose",
                            "columnId": "346f00f0-c5a1-4bc5-a2d6-dd87a3346314",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            }
                        },
                        "meeting_impact": {
                            "alias": "meeting_impact",
                            "fullyQualifiedColumn": "meeting_details.meeting_impact",
                            "columnId": "0c23a336-eeae-4761-b6a5-280845cef358",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            }
                        },
                        "meet_cancellation_status": {
                            "alias": "meet_cancellation_status",
                            "fullyQualifiedColumn": "meeting_details.meet_cancellation_status",
                            "columnId": "1ecaeb93-4ad5-4e81-8495-f347c4f18057",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            }
                        },
                        "cancellation_reason": {
                            "alias": "cancellation_reason",
                            "fullyQualifiedColumn": "meeting_details.cancellation_reason",
                            "columnId": "17144b0b-6ede-4447-9408-da66a8a22475",
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
                            "columnId": "8103eb38-761f-469d-b686-f21888711204",
                            "defaultFunction": "db.generic.aggregate.sum",
                            "type": {
                                "java.lang.Integer": "numeric"
                            }
                        },
                        "travel_date": {
                            "alias": "travel_date",
                            "fullyQualifiedColumn": "travel_details.travel_date",
                            "columnId": "4b0683c5-7dd4-40cb-8d0c-6e7f6dd307ce",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.sql.Timestamp": "dateTime"
                            }
                        },
                        "travel_type": {
                            "alias": "travel_type",
                            "fullyQualifiedColumn": "travel_details.travel_type",
                            "columnId": "6b06dd2d-f9d7-469d-bb4a-127bc516f33b",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            }
                        },
                        "travel_medium": {
                            "alias": "travel_medium",
                            "fullyQualifiedColumn": "travel_details.travel_medium",
                            "columnId": "e4a0f3f7-45fe-433c-b298-ffea95daf206",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            }
                        },
                        "source_id": {
                            "alias": "source_id",
                            "fullyQualifiedColumn": "travel_details.source_id",
                            "columnId": "a95e861e-929c-4ed3-96c7-90fa9a7d8718",
                            "defaultFunction": "db.generic.aggregate.sum",
                            "type": {
                                "java.lang.Integer": "numeric"
                            }
                        },
                        "source": {
                            "alias": "source",
                            "fullyQualifiedColumn": "travel_details.source",
                            "columnId": "c823a243-7f27-49c5-8707-8d9f67ced4b7",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            }
                        },
                        "destination_id": {
                            "alias": "destination_id",
                            "fullyQualifiedColumn": "travel_details.destination_id",
                            "columnId": "f30f759d-0dc2-46f3-a2c2-e12cac105d0a",
                            "defaultFunction": "db.generic.aggregate.sum",
                            "type": {
                                "java.lang.Integer": "numeric"
                            }
                        },
                        "destination": {
                            "alias": "destination",
                            "fullyQualifiedColumn": "travel_details.destination",
                            "columnId": "10de500a-928f-47ea-a60e-97b0a8608082",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            }
                        },
                        "travel_cost": {
                            "alias": "travel_cost",
                            "fullyQualifiedColumn": "travel_details.travel_cost",
                            "columnId": "25073520-c5da-407e-b582-3443640e030b",
                            "defaultFunction": "db.generic.aggregate.sum",
                            "type": {
                                "java.lang.Integer": "numeric"
                            }
                        },
                        "mode_of_payment": {
                            "alias": "mode_of_payment",
                            "fullyQualifiedColumn": "travel_details.mode_of_payment",
                            "columnId": "fc82d24f-2312-4e14-941f-d7a6f387516c",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            }
                        },
                        "booking_platform": {
                            "alias": "booking_platform",
                            "fullyQualifiedColumn": "travel_details.booking_platform",
                            "columnId": "c73590df-2565-42cf-a154-725cc301f3ec",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            }
                        },
                        "travelled_by": {
                            "alias": "travelled_by",
                            "fullyQualifiedColumn": "travel_details.travelled_by",
                            "columnId": "577329b3-91c2-4f2e-9d8e-cc2d5d8468c4",
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
            "metadataName": "Metadatacheck",
            "metadataDir": "Sibtain"
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
        "doResetFormData": true,
        "tablesMergeType": false,
        "activeDsInfoId": "9fasa",
        "editMdFromHomeInfo": {
            "dir": "Sibtain/Metadatacheck.metadata",
            "file": "Metadatacheck.metadata"
        }
    },
    metadata: {
        "dataFetchedFor": {
            "getDatasource": true,
            "joins": true,
            "viewSessionVariables": false,
            "listDataSources": true,
            "nmx5-mflu-ordv-c060-ue": false,
            "bv18-izfc-njht-35zh-h5": false,
            "fqu7-fao5-y4q6-15pv-wa": false
        },
        "loadingStatus": {
            "getDatasource": true,
            "listDataSources": true,
            "nmx5-mflu-ordv-c060-ue": false,
            "bv18-izfc-njht-35zh-h5": false,
            "fqu7-fao5-y4q6-15pv-wa": false
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
                "driver": "org.apache.spark.deploy.DeployMessages$KillDriver",
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
                "driver": "org.apache.spark.deploy.DeployMessages$LaunchDriver",
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
                "driver": "org.apache.spark.deploy.DeployMessages$KillDriver",
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
                "driver": "org.apache.spark.deploy.DeployMessages$LaunchDriver",
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
                "available": "true",
                "driver": "org.apache.spark.deploy.DeployMessages$LaunchDriver"
            },
            {
                "available": "true",
                "driver": "org.apache.spark.deploy.DeployMessages$KillDriver"
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
        "allDataSources": [
            {
                "permissionLevel": 5,
                "driver": "dynamicSwitch",
                "name": "Groovy 1",
                "classifier": "efwd",
                "type": "sql.jdbc.groovy.managed",
                "data": {
                    "dir": "Gagan",
                    "driverName": null,
                    "type": "sql.jdbc.groovy.managed",
                    "id": 1,
                    "userName": null,
                    "password": null,
                    "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1001);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                    "jdbcUrl": null,
                    "driver": "dynamicSwitch"
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
                "driver": "org.apache.derby.jdbc.AutoloadedDriver",
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
                "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                "name": "hiee",
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
                "driver": "org.postgresql.Driver",
                "name": "booking",
                "classifier": "global",
                "dataSourceType": "Managed DataSource"
            },
            {
                "data": {
                    "id": "1101",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                "name": "derby2",
                "classifier": "global",
                "dataSourceType": "Managed DataSource"
            },
            {
                "data": {
                    "id": "1201",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "org.sqlite.JDBC",
                "name": "SampleTravelData",
                "classifier": "global",
                "dataSourceType": "Managed DataSource"
            },
            {
                "data": {
                    "id": "1302",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "org.sqlite.JDBC",
                "name": "Sqlite",
                "classifier": "global",
                "dataSourceType": "Managed DataSource"
            },
            {
                "data": {
                    "id": "1401",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "org.postgresql.Driver",
                "name": "booking",
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
                        "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                        "name": "SampleTravelDataDerby",
                        "classifier": "global",
                        "dataSourceType": "Managed DataSource",
                        "category": "dataSource",
                        "children": [
                            {
                                "name": "SQLJ",
                                "children": [],
                                "keyPath": "o6yg-nuwy-p27l-nfbt-g4/nmx5-mflu-ordv-c060-ue/wwj1-xe5o-b2vb-ol3f-sf",
                                "key": "wwj1-xe5o-b2vb-ol3f-sf",
                                "uuid": "wwj1-xe5o-b2vb-ol3f-sf",
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
                                "keyPath": "o6yg-nuwy-p27l-nfbt-g4/nmx5-mflu-ordv-c060-ue/v238-b8ct-czyf-ocze-ms",
                                "key": "v238-b8ct-czyf-ocze-ms",
                                "uuid": "v238-b8ct-czyf-ocze-ms",
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
                                "keyPath": "o6yg-nuwy-p27l-nfbt-g4/nmx5-mflu-ordv-c060-ue/es94-tw1e-z8jc-79en-sm",
                                "key": "es94-tw1e-z8jc-79en-sm",
                                "uuid": "es94-tw1e-z8jc-79en-sm",
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
                                        "keyPath": "o6yg-nuwy-p27l-nfbt-g4/nmx5-mflu-ordv-c060-ue/bv18-izfc-njht-35zh-h5/fqu7-fao5-y4q6-15pv-wa",
                                        "key": "fqu7-fao5-y4q6-15pv-wa",
                                        "alias": "dimdate",
                                        "uuid": "fqu7-fao5-y4q6-15pv-wa",
                                        "connId": "2ybth",
                                        "dataSource": {
                                            "id": "1",
                                            "type": "dynamicDataSource",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "",
                                            "schema": "HIUSER",
                                            "connId": "2ybth",
                                            "classifier": "db.workflow",
                                            "datasourceName": "SampleTravelDataDerby",
                                            "dsKeyPath": "o6yg-nuwy-p27l-nfbt-g4/nmx5-mflu-ordv-c060-ue/bv18-izfc-njht-35zh-h5",
                                            "driverType": "Derby",
                                            "database": "HIUSER"
                                        },
                                        "dataSourceName": "SampleTravelDataDerby",
                                        "category": "table",
                                        "nameWithConnId": "dimdate_2ybth",
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
                                        "keyPath": "o6yg-nuwy-p27l-nfbt-g4/nmx5-mflu-ordv-c060-ue/bv18-izfc-njht-35zh-h5/tkaj-vdwk-kimm-p8h7-6q",
                                        "key": "tkaj-vdwk-kimm-p8h7-6q",
                                        "alias": "employee_details",
                                        "uuid": "tkaj-vdwk-kimm-p8h7-6q",
                                        "connId": "2ybth",
                                        "dataSource": {
                                            "id": "1",
                                            "type": "dynamicDataSource",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "",
                                            "schema": "HIUSER",
                                            "connId": "2ybth",
                                            "classifier": "db.workflow",
                                            "datasourceName": "SampleTravelDataDerby",
                                            "dsKeyPath": "o6yg-nuwy-p27l-nfbt-g4/nmx5-mflu-ordv-c060-ue/bv18-izfc-njht-35zh-h5",
                                            "driverType": "Derby",
                                            "database": "HIUSER"
                                        },
                                        "dataSourceName": "SampleTravelDataDerby",
                                        "category": "table",
                                        "nameWithConnId": "employee_details_2ybth",
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
                                        "keyPath": "o6yg-nuwy-p27l-nfbt-g4/nmx5-mflu-ordv-c060-ue/bv18-izfc-njht-35zh-h5/o7u5-x1bj-6lk7-0gw5-m9",
                                        "key": "o7u5-x1bj-6lk7-0gw5-m9",
                                        "alias": "geo_cordinates",
                                        "uuid": "o7u5-x1bj-6lk7-0gw5-m9",
                                        "connId": "2ybth",
                                        "dataSource": {
                                            "id": "1",
                                            "type": "dynamicDataSource",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "",
                                            "schema": "HIUSER",
                                            "connId": "2ybth",
                                            "classifier": "db.workflow",
                                            "datasourceName": "SampleTravelDataDerby",
                                            "dsKeyPath": "o6yg-nuwy-p27l-nfbt-g4/nmx5-mflu-ordv-c060-ue/bv18-izfc-njht-35zh-h5",
                                            "driverType": "Derby",
                                            "database": "HIUSER"
                                        },
                                        "dataSourceName": "SampleTravelDataDerby",
                                        "category": "table",
                                        "nameWithConnId": "geo_cordinates_2ybth",
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
                                        "keyPath": "o6yg-nuwy-p27l-nfbt-g4/nmx5-mflu-ordv-c060-ue/bv18-izfc-njht-35zh-h5/xtau-qy33-sz2n-y9yw-3n",
                                        "key": "xtau-qy33-sz2n-y9yw-3n",
                                        "alias": "meeting_details",
                                        "uuid": "xtau-qy33-sz2n-y9yw-3n",
                                        "connId": "2ybth",
                                        "dataSource": {
                                            "id": "1",
                                            "type": "dynamicDataSource",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "",
                                            "schema": "HIUSER",
                                            "connId": "2ybth",
                                            "classifier": "db.workflow",
                                            "datasourceName": "SampleTravelDataDerby",
                                            "dsKeyPath": "o6yg-nuwy-p27l-nfbt-g4/nmx5-mflu-ordv-c060-ue/bv18-izfc-njht-35zh-h5",
                                            "driverType": "Derby",
                                            "database": "HIUSER"
                                        },
                                        "dataSourceName": "SampleTravelDataDerby",
                                        "category": "table",
                                        "nameWithConnId": "meeting_details_2ybth",
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
                                        "keyPath": "o6yg-nuwy-p27l-nfbt-g4/nmx5-mflu-ordv-c060-ue/bv18-izfc-njht-35zh-h5/5vi8-ft8j-6764-8vlc-hb",
                                        "key": "5vi8-ft8j-6764-8vlc-hb",
                                        "alias": "travel_details",
                                        "uuid": "5vi8-ft8j-6764-8vlc-hb",
                                        "connId": "2ybth",
                                        "dataSource": {
                                            "id": "1",
                                            "type": "dynamicDataSource",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "",
                                            "schema": "HIUSER",
                                            "connId": "2ybth",
                                            "classifier": "db.workflow",
                                            "datasourceName": "SampleTravelDataDerby",
                                            "dsKeyPath": "o6yg-nuwy-p27l-nfbt-g4/nmx5-mflu-ordv-c060-ue/bv18-izfc-njht-35zh-h5",
                                            "driverType": "Derby",
                                            "database": "HIUSER"
                                        },
                                        "dataSourceName": "SampleTravelDataDerby",
                                        "category": "table",
                                        "nameWithConnId": "travel_details_2ybth",
                                        "database": "HIUSER",
                                        "schema": "HIUSER",
                                        "selected": false
                                    }
                                ],
                                "keyPath": "o6yg-nuwy-p27l-nfbt-g4/nmx5-mflu-ordv-c060-ue/bv18-izfc-njht-35zh-h5",
                                "key": "bv18-izfc-njht-35zh-h5",
                                "uuid": "bv18-izfc-njht-35zh-h5",
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
                                "keyPath": "o6yg-nuwy-p27l-nfbt-g4/nmx5-mflu-ordv-c060-ue/qlyn-p3u9-ittz-mqwr-7u",
                                "key": "qlyn-p3u9-ittz-mqwr-7u",
                                "uuid": "qlyn-p3u9-ittz-mqwr-7u",
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
                                "keyPath": "o6yg-nuwy-p27l-nfbt-g4/nmx5-mflu-ordv-c060-ue/v2xc-oks0-u4dy-ug02-yw",
                                "key": "v2xc-oks0-u4dy-ug02-yw",
                                "uuid": "v2xc-oks0-u4dy-ug02-yw",
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
                                "keyPath": "o6yg-nuwy-p27l-nfbt-g4/nmx5-mflu-ordv-c060-ue/nasv-k6ll-v11n-2rvo-9z",
                                "key": "nasv-k6ll-v11n-2rvo-9z",
                                "uuid": "nasv-k6ll-v11n-2rvo-9z",
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
                                "keyPath": "o6yg-nuwy-p27l-nfbt-g4/nmx5-mflu-ordv-c060-ue/8eh4-5e2z-8ilo-axu1-vn",
                                "key": "8eh4-5e2z-8ilo-axu1-vn",
                                "uuid": "8eh4-5e2z-8ilo-axu1-vn",
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
                                "keyPath": "o6yg-nuwy-p27l-nfbt-g4/nmx5-mflu-ordv-c060-ue/9gdw-mcni-im4t-m90j-ku",
                                "key": "9gdw-mcni-im4t-m90j-ku",
                                "uuid": "9gdw-mcni-im4t-m90j-ku",
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
                                "keyPath": "o6yg-nuwy-p27l-nfbt-g4/nmx5-mflu-ordv-c060-ue/ambk-r39s-63vi-gwef-i0",
                                "key": "ambk-r39s-63vi-gwef-i0",
                                "uuid": "ambk-r39s-63vi-gwef-i0",
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
                                "keyPath": "o6yg-nuwy-p27l-nfbt-g4/nmx5-mflu-ordv-c060-ue/r7xx-6dxc-qtq2-uok4-n3",
                                "key": "r7xx-6dxc-qtq2-uok4-n3",
                                "uuid": "r7xx-6dxc-qtq2-uok4-n3",
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
                                "keyPath": "o6yg-nuwy-p27l-nfbt-g4/nmx5-mflu-ordv-c060-ue/rqxo-yi5s-qrs2-hkfb-7i",
                                "key": "rqxo-yi5s-qrs2-hkfb-7i",
                                "uuid": "rqxo-yi5s-qrs2-hkfb-7i",
                                "data": {
                                    "id": "1",
                                    "type": "dynamicDataSource"
                                },
                                "category": "schema",
                                "datasourceName": "SampleTravelDataDerby"
                            }
                        ],
                        "driverType": "Derby",
                        "keyPath": "o6yg-nuwy-p27l-nfbt-g4/nmx5-mflu-ordv-c060-ue",
                        "key": "nmx5-mflu-ordv-c060-ue",
                        "uuid": "nmx5-mflu-ordv-c060-ue",
                        "fetched": true
                    },
                    {
                        "data": {
                            "id": "1000",
                            "type": "dynamicDataSource"
                        },
                        "dataSourceProvider": "tomcat",
                        "type": "dynamicDataSource",
                        "permissionLevel": 5,
                        "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                        "name": "hiee",
                        "classifier": "global",
                        "dataSourceType": "Managed DataSource",
                        "category": "dataSource",
                        "children": [],
                        "driverType": "Derby",
                        "keyPath": "o6yg-nuwy-p27l-nfbt-g4/x0ot-ys06-jxfn-j8q3-ph",
                        "key": "x0ot-ys06-jxfn-j8q3-ph",
                        "uuid": "x0ot-ys06-jxfn-j8q3-ph"
                    },
                    {
                        "data": {
                            "id": "1101",
                            "type": "dynamicDataSource"
                        },
                        "dataSourceProvider": "tomcat",
                        "type": "dynamicDataSource",
                        "permissionLevel": 5,
                        "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                        "name": "derby2",
                        "classifier": "global",
                        "dataSourceType": "Managed DataSource",
                        "category": "dataSource",
                        "children": [],
                        "driverType": "Derby",
                        "keyPath": "o6yg-nuwy-p27l-nfbt-g4/i67f-aby3-0647-7wp3-b0",
                        "key": "i67f-aby3-0647-7wp3-b0",
                        "uuid": "i67f-aby3-0647-7wp3-b0"
                    }
                ],
                "key": "o6yg-nuwy-p27l-nfbt-g4",
                "uuid": "o6yg-nuwy-p27l-nfbt-g4",
                "keyPath": "o6yg-nuwy-p27l-nfbt-g4",
                "category": "dsGroup",
                "imgUrl": "images/data_sources/defaut_datasource.png"
            },
            {
                "name": "DynamicSwitch",
                "children": [
                    {
                        "permissionLevel": 5,
                        "driver": "dynamicSwitch",
                        "name": "Groovy 1",
                        "classifier": "efwd",
                        "type": "sql.jdbc.groovy.managed",
                        "data": {
                            "dir": "Gagan",
                            "driverName": null,
                            "type": "sql.jdbc.groovy.managed",
                            "id": 1,
                            "userName": null,
                            "password": null,
                            "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1001);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                            "jdbcUrl": null,
                            "driver": "dynamicSwitch"
                        },
                        "dataSourceType": "",
                        "category": "dataSource",
                        "children": [],
                        "driverType": "DynamicSwitch",
                        "keyPath": "624e-5n7y-cbd6-czib-fj/nbmu-mcae-log2-w45t-xr",
                        "key": "nbmu-mcae-log2-w45t-xr",
                        "uuid": "nbmu-mcae-log2-w45t-xr"
                    }
                ],
                "key": "624e-5n7y-cbd6-czib-fj",
                "uuid": "624e-5n7y-cbd6-czib-fj",
                "keyPath": "624e-5n7y-cbd6-czib-fj",
                "category": "dsGroup",
                "imgUrl": "images/data_sources/defaut_datasource.png"
            },
            {
                "name": "Postgresql",
                "children": [
                    {
                        "data": {
                            "id": "1001",
                            "type": "dynamicDataSource"
                        },
                        "dataSourceProvider": "tomcat",
                        "type": "dynamicDataSource",
                        "permissionLevel": 5,
                        "driver": "org.postgresql.Driver",
                        "name": "booking",
                        "classifier": "global",
                        "dataSourceType": "Managed DataSource",
                        "category": "dataSource",
                        "children": [],
                        "driverType": "Postgresql",
                        "keyPath": "wf6w-ldvv-8sgv-tj7g-3j/au3a-z6b6-lr4v-zwhh-o3",
                        "key": "au3a-z6b6-lr4v-zwhh-o3",
                        "uuid": "au3a-z6b6-lr4v-zwhh-o3"
                    },
                    {
                        "data": {
                            "id": "1401",
                            "type": "dynamicDataSource"
                        },
                        "dataSourceProvider": "tomcat",
                        "type": "dynamicDataSource",
                        "permissionLevel": 5,
                        "driver": "org.postgresql.Driver",
                        "name": "booking",
                        "classifier": "global",
                        "dataSourceType": "Managed DataSource",
                        "category": "dataSource",
                        "children": [],
                        "driverType": "Postgresql",
                        "keyPath": "wf6w-ldvv-8sgv-tj7g-3j/roco-g1jq-lyjn-1gcz-zi",
                        "key": "roco-g1jq-lyjn-1gcz-zi",
                        "uuid": "roco-g1jq-lyjn-1gcz-zi"
                    }
                ],
                "key": "wf6w-ldvv-8sgv-tj7g-3j",
                "uuid": "wf6w-ldvv-8sgv-tj7g-3j",
                "keyPath": "wf6w-ldvv-8sgv-tj7g-3j",
                "category": "dsGroup",
                "imgUrl": "images/data_sources/defaut_datasource.png"
            },
            {
                "name": "Sqlite",
                "children": [
                    {
                        "data": {
                            "id": "1201",
                            "type": "dynamicDataSource"
                        },
                        "dataSourceProvider": "tomcat",
                        "type": "dynamicDataSource",
                        "permissionLevel": 5,
                        "driver": "org.sqlite.JDBC",
                        "name": "SampleTravelData",
                        "classifier": "global",
                        "dataSourceType": "Managed DataSource",
                        "category": "dataSource",
                        "children": [],
                        "driverType": "Sqlite",
                        "keyPath": "ua4a-xvk4-m2li-521m-c9/pajl-pyr4-so3i-4umw-0r",
                        "key": "pajl-pyr4-so3i-4umw-0r",
                        "uuid": "pajl-pyr4-so3i-4umw-0r"
                    },
                    {
                        "data": {
                            "id": "1302",
                            "type": "dynamicDataSource"
                        },
                        "dataSourceProvider": "tomcat",
                        "type": "dynamicDataSource",
                        "permissionLevel": 5,
                        "driver": "org.sqlite.JDBC",
                        "name": "Sqlite",
                        "classifier": "global",
                        "dataSourceType": "Managed DataSource",
                        "category": "dataSource",
                        "children": [],
                        "driverType": "Sqlite",
                        "keyPath": "ua4a-xvk4-m2li-521m-c9/tt40-6hln-myva-2c5u-zq",
                        "key": "tt40-6hln-myva-2c5u-zq",
                        "uuid": "tt40-6hln-myva-2c5u-zq"
                    }
                ],
                "key": "ua4a-xvk4-m2li-521m-c9",
                "uuid": "ua4a-xvk4-m2li-521m-c9",
                "keyPath": "ua4a-xvk4-m2li-521m-c9",
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
                    "dsUUID": "nmx5-mflu-ordv-c060-ue",
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
                            "connId": "2ybth",
                            "classifier": "db.workflow",
                            "datasourceName": "SampleTravelDataDerby",
                            "dsKeyPath": "o6yg-nuwy-p27l-nfbt-g4/nmx5-mflu-ordv-c060-ue/bv18-izfc-njht-35zh-h5",
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
                "connId": "2ybth",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "o6yg-nuwy-p27l-nfbt-g4/nmx5-mflu-ordv-c060-ue/bv18-izfc-njht-35zh-h5",
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
                "keyPath": "o6yg-nuwy-p27l-nfbt-g4/nmx5-mflu-ordv-c060-ue/bv18-izfc-njht-35zh-h5/fqu7-fao5-y4q6-15pv-wa",
                "key": "fqu7-fao5-y4q6-15pv-wa",
                "alias": "dimdate",
                "uuid": "fqu7-fao5-y4q6-15pv-wa",
                "connId": "2ybth",
                "dataSource": {
                    "id": "1",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc",
                    "catSchemaPredicted": false,
                    "sync": false,
                    "catalog": "",
                    "schema": "HIUSER",
                    "connId": "2ybth",
                    "classifier": "db.workflow",
                    "datasourceName": "SampleTravelDataDerby",
                    "dsKeyPath": "o6yg-nuwy-p27l-nfbt-g4/nmx5-mflu-ordv-c060-ue/bv18-izfc-njht-35zh-h5",
                    "driverType": "Derby",
                    "database": "HIUSER"
                },
                "dataSourceName": "SampleTravelDataDerby",
                "category": "table",
                "nameWithConnId": "dimdate_2ybth",
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
                        "category": "column",
                        "originalName": "dim_id",
                        "originalId": "21d2057f-0f32-40b8-a7d4-b3e60f941775",
                        "tableKey": "dimdate",
                        "connId": "2ybth",
                        "duplicateIndex": 0,
                        "columnKey": "dim_id",
                        "_columnId": "21d2057f-0f32-40b8-a7d4-b3e60f941775",
                        "uuid": "43pf-h1l4-c7a4-2nb3-ip"
                    },
                    "fiscal_year": {
                        "alias": "fiscal_year",
                        "fullyQualifiedColumn": "dimdate.fiscal_year",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.sql.Date": "date"
                        },
                        "category": "column",
                        "originalName": "fiscal_year",
                        "originalId": "d6b79fc3-249a-4df7-8329-e90858e7e340",
                        "tableKey": "dimdate",
                        "connId": "2ybth",
                        "duplicateIndex": 0,
                        "columnKey": "fiscal_year",
                        "_columnId": "d6b79fc3-249a-4df7-8329-e90858e7e340",
                        "uuid": "q5mx-vi6k-g4h5-h3yv-or"
                    },
                    "modified_date": {
                        "alias": "modified_date",
                        "fullyQualifiedColumn": "dimdate.modified_date",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.sql.Timestamp": "dateTime"
                        },
                        "category": "column",
                        "originalName": "modified_date",
                        "originalId": "c4935fcd-a208-4b70-bf9f-68034d1667bc",
                        "tableKey": "dimdate",
                        "connId": "2ybth",
                        "duplicateIndex": 0,
                        "columnKey": "modified_date",
                        "_columnId": "c4935fcd-a208-4b70-bf9f-68034d1667bc",
                        "uuid": "k5dt-yk80-g72a-7hi8-mj"
                    },
                    "date_key": {
                        "alias": "date_key",
                        "fullyQualifiedColumn": "dimdate.date_key",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "originalName": "date_key",
                        "originalId": "2b1fbe06-ca0a-4b9d-8d66-1ef7b1494f43",
                        "tableKey": "dimdate",
                        "connId": "2ybth",
                        "duplicateIndex": 0,
                        "columnKey": "date_key",
                        "_columnId": "2b1fbe06-ca0a-4b9d-8d66-1ef7b1494f43",
                        "uuid": "20e4-df7z-m22l-jhir-za"
                    },
                    "day_number": {
                        "alias": "day_number",
                        "fullyQualifiedColumn": "dimdate.day_number",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "originalName": "day_number",
                        "originalId": "dacf167a-82e1-44b0-a435-08c46961bdf2",
                        "tableKey": "dimdate",
                        "connId": "2ybth",
                        "duplicateIndex": 0,
                        "columnKey": "day_number",
                        "_columnId": "dacf167a-82e1-44b0-a435-08c46961bdf2",
                        "uuid": "ket1-y789-9b2t-q2vb-qs"
                    },
                    "fiscal_month_name": {
                        "alias": "fiscal_month_name",
                        "fullyQualifiedColumn": "dimdate.fiscal_month_name",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "originalName": "fiscal_month_name",
                        "originalId": "17695614-0388-410d-aac6-e7073f8a4f13",
                        "tableKey": "dimdate",
                        "connId": "2ybth",
                        "duplicateIndex": 0,
                        "columnKey": "fiscal_month_name",
                        "_columnId": "17695614-0388-410d-aac6-e7073f8a4f13",
                        "uuid": "gn9d-l23r-83c7-5m76-96"
                    },
                    "fiscal_month_label": {
                        "alias": "fiscal_month_label",
                        "fullyQualifiedColumn": "dimdate.fiscal_month_label",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "originalName": "fiscal_month_label",
                        "originalId": "172868ab-73ca-4128-a4d9-77b57d1a1703",
                        "tableKey": "dimdate",
                        "connId": "2ybth",
                        "duplicateIndex": 0,
                        "columnKey": "fiscal_month_label",
                        "_columnId": "172868ab-73ca-4128-a4d9-77b57d1a1703",
                        "uuid": "p1uv-ypee-wkgb-j9ii-mz"
                    },
                    "created_date": {
                        "alias": "created_date",
                        "fullyQualifiedColumn": "dimdate.created_date",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "originalName": "created_date",
                        "originalId": "cf48b4c7-2da3-4598-8f61-f21cf57edb09",
                        "tableKey": "dimdate",
                        "connId": "2ybth",
                        "duplicateIndex": 0,
                        "columnKey": "created_date",
                        "_columnId": "cf48b4c7-2da3-4598-8f61-f21cf57edb09",
                        "uuid": "4zzw-94gw-gvgd-8rr3-kh"
                    },
                    "created_time": {
                        "alias": "created_time",
                        "fullyQualifiedColumn": "dimdate.created_time",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "originalName": "created_time",
                        "originalId": "e90d0f75-1938-4f14-916a-cdea891fb679",
                        "tableKey": "dimdate",
                        "connId": "2ybth",
                        "duplicateIndex": 0,
                        "columnKey": "created_time",
                        "_columnId": "e90d0f75-1938-4f14-916a-cdea891fb679",
                        "uuid": "jyz4-qr4t-ig19-x23r-8p"
                    },
                    "rating": {
                        "alias": "rating",
                        "fullyQualifiedColumn": "dimdate.rating",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "originalName": "rating",
                        "originalId": "1139b291-39b9-4f6b-8076-30ccb28ad620",
                        "tableKey": "dimdate",
                        "connId": "2ybth",
                        "duplicateIndex": 0,
                        "columnKey": "rating",
                        "_columnId": "1139b291-39b9-4f6b-8076-30ccb28ad620",
                        "uuid": "vssl-nubc-5tk1-3y1q-ta"
                    }
                },
                "columnsFetched": true
            },
            "employee_details": {
                "id": "4e1fd245f4d13b77be423a43f01d80b2",
                "name": "employee_details",
                "data": {
                    "id": "1",
                    "type": "dynamicDataSource"
                },
                "keyPath": "o6yg-nuwy-p27l-nfbt-g4/nmx5-mflu-ordv-c060-ue/bv18-izfc-njht-35zh-h5/tkaj-vdwk-kimm-p8h7-6q",
                "key": "tkaj-vdwk-kimm-p8h7-6q",
                "alias": "employee_details",
                "uuid": "tkaj-vdwk-kimm-p8h7-6q",
                "connId": "2ybth",
                "dataSource": {
                    "id": "1",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc",
                    "catSchemaPredicted": false,
                    "sync": false,
                    "catalog": "",
                    "schema": "HIUSER",
                    "connId": "2ybth",
                    "classifier": "db.workflow",
                    "datasourceName": "SampleTravelDataDerby",
                    "dsKeyPath": "o6yg-nuwy-p27l-nfbt-g4/nmx5-mflu-ordv-c060-ue/bv18-izfc-njht-35zh-h5",
                    "driverType": "Derby",
                    "database": "HIUSER"
                },
                "dataSourceName": "SampleTravelDataDerby",
                "category": "table",
                "nameWithConnId": "employee_details_2ybth",
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
                "keyPath": "o6yg-nuwy-p27l-nfbt-g4/nmx5-mflu-ordv-c060-ue/bv18-izfc-njht-35zh-h5/o7u5-x1bj-6lk7-0gw5-m9",
                "key": "o7u5-x1bj-6lk7-0gw5-m9",
                "alias": "geo_cordinates",
                "uuid": "o7u5-x1bj-6lk7-0gw5-m9",
                "connId": "2ybth",
                "dataSource": {
                    "id": "1",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc",
                    "catSchemaPredicted": false,
                    "sync": false,
                    "catalog": "",
                    "schema": "HIUSER",
                    "connId": "2ybth",
                    "classifier": "db.workflow",
                    "datasourceName": "SampleTravelDataDerby",
                    "dsKeyPath": "o6yg-nuwy-p27l-nfbt-g4/nmx5-mflu-ordv-c060-ue/bv18-izfc-njht-35zh-h5",
                    "driverType": "Derby",
                    "database": "HIUSER"
                },
                "dataSourceName": "SampleTravelDataDerby",
                "category": "table",
                "nameWithConnId": "geo_cordinates_2ybth",
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
                "keyPath": "o6yg-nuwy-p27l-nfbt-g4/nmx5-mflu-ordv-c060-ue/bv18-izfc-njht-35zh-h5/xtau-qy33-sz2n-y9yw-3n",
                "key": "xtau-qy33-sz2n-y9yw-3n",
                "alias": "meeting_details",
                "uuid": "xtau-qy33-sz2n-y9yw-3n",
                "connId": "2ybth",
                "dataSource": {
                    "id": "1",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc",
                    "catSchemaPredicted": false,
                    "sync": false,
                    "catalog": "",
                    "schema": "HIUSER",
                    "connId": "2ybth",
                    "classifier": "db.workflow",
                    "datasourceName": "SampleTravelDataDerby",
                    "dsKeyPath": "o6yg-nuwy-p27l-nfbt-g4/nmx5-mflu-ordv-c060-ue/bv18-izfc-njht-35zh-h5",
                    "driverType": "Derby",
                    "database": "HIUSER"
                },
                "dataSourceName": "SampleTravelDataDerby",
                "category": "table",
                "nameWithConnId": "meeting_details_2ybth",
                "database": "HIUSER",
                "schema": "HIUSER",
                "selected": true,
                "keyName": "meeting_details"
            }
        },
        "views": [],
        "activeView": false,
        "categories": {
            "o6yg-nuwy-p27l-nfbt-g4": {
                "ds": {
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
                },
                "category": {
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
                }
            },
            "624e-5n7y-cbd6-czib-fj": {
                "ds": {
                    "permissionLevel": 5,
                    "driver": "dynamicSwitch",
                    "name": "Groovy 1",
                    "classifier": "efwd",
                    "type": "sql.jdbc.groovy.managed",
                    "data": {
                        "dir": "Gagan",
                        "driverName": null,
                        "type": "sql.jdbc.groovy.managed",
                        "id": 1,
                        "userName": null,
                        "password": null,
                        "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1001);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
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
            "wf6w-ldvv-8sgv-tj7g-3j": {
                "ds": {
                    "data": {
                        "id": "1001",
                        "type": "dynamicDataSource"
                    },
                    "dataSourceProvider": "tomcat",
                    "type": "dynamicDataSource",
                    "permissionLevel": 5,
                    "driver": "org.postgresql.Driver",
                    "name": "booking",
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
            "ua4a-xvk4-m2li-521m-c9": {
                "ds": {
                    "data": {
                        "id": "1201",
                        "type": "dynamicDataSource"
                    },
                    "dataSourceProvider": "tomcat",
                    "type": "dynamicDataSource",
                    "permissionLevel": 5,
                    "driver": "org.sqlite.JDBC",
                    "name": "SampleTravelData",
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
                "connId": "2ybth",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "o6yg-nuwy-p27l-nfbt-g4/nmx5-mflu-ordv-c060-ue/bv18-izfc-njht-35zh-h5",
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
                "type": "inner",
                "operator": "=",
                "left": {
                    "table": "dimdate",
                    "column": "day_number",
                    "alias": false,
                    "dataSource": {
                        "id": "1",
                        "type": "dynamicDataSource",
                        "baseType": "global.jdbc",
                        "catSchemaPredicted": false,
                        "sync": false,
                        "catalog": "",
                        "schema": "HIUSER",
                        "connId": "2ybth",
                        "classifier": "db.workflow",
                        "datasourceName": "SampleTravelDataDerby",
                        "dsKeyPath": "o6yg-nuwy-p27l-nfbt-g4/nmx5-mflu-ordv-c060-ue/bv18-izfc-njht-35zh-h5",
                        "driverType": "Derby",
                        "database": "HIUSER"
                    }
                },
                "right": {
                    "table": "dimdate",
                    "column": "fiscal_month_label",
                    "alias": false,
                    "dataSource": {
                        "id": "1",
                        "type": "dynamicDataSource",
                        "baseType": "global.jdbc",
                        "catSchemaPredicted": false,
                        "sync": false,
                        "catalog": "",
                        "schema": "HIUSER",
                        "connId": "2ybth",
                        "classifier": "db.workflow",
                        "datasourceName": "SampleTravelDataDerby",
                        "dsKeyPath": "o6yg-nuwy-p27l-nfbt-g4/nmx5-mflu-ordv-c060-ue/bv18-izfc-njht-35zh-h5",
                        "driverType": "Derby",
                        "database": "HIUSER"
                    }
                },
                "key": "sl0p-89td-w585-6kt0-pc",
                "uuid": "sl0p-89td-w585-6kt0-pc",
                "action": "add",
                "index": 1,
                "leftColumn": "dimdate.day_number",
                "rightColumn": "dimdate.fiscal_month_label"
            },
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
                        "connectionDatabaseId": "7c7909f8-a717-48ac-96ba-d7c85d67ee08",
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
                        "connectionDatabaseId": "7c7909f8-a717-48ac-96ba-d7c85d67ee08",
                        "catSchemaPredicted": false,
                        "catalog": "",
                        "schema": "HIUSER",
                        "type": "dynamicDataSource",
                        "baseType": "global.jdbc"
                    }
                },
                "uuid": "6iqf-tqgq-rq93-3cln-fi",
                "index": 2,
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
                        "connectionDatabaseId": "7c7909f8-a717-48ac-96ba-d7c85d67ee08",
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
                        "connectionDatabaseId": "7c7909f8-a717-48ac-96ba-d7c85d67ee08",
                        "catSchemaPredicted": false,
                        "catalog": "",
                        "schema": "HIUSER",
                        "type": "dynamicDataSource",
                        "baseType": "global.jdbc"
                    }
                },
                "uuid": "xp4f-58v7-5v0o-v1r2-nq",
                "index": 3,
                "action": "noChange"
            }
        ],
        "mode": "create",
        "allTablesKeys": [
            "o7u5-x1bj-6lk7-0gw5-m9",
            "xtau-qy33-sz2n-y9yw-3n",
            "tkaj-vdwk-kimm-p8h7-6q",
            "fqu7-fao5-y4q6-15pv-wa",
            "5vi8-ft8j-6764-8vlc-hb"
        ],
        "selectedTableKeys": [
            "fqu7-fao5-y4q6-15pv-wa",
            "tkaj-vdwk-kimm-p8h7-6q",
            "o7u5-x1bj-6lk7-0gw5-m9",
            "xtau-qy33-sz2n-y9yw-3n"
        ],
        "metadataName": "Metadata_1",
        "activeDataSource": false,
        "metadataToEdit": false,
        "isSavingInProgress": false,
        "editViewsTempData": {},
        "inititalStateFromJest": false,
        "timeStamp": 1670418611196,
        "initialEditResponse": false,
        "editorFullView": false,
        "selectedTableOrColumnKey": {
            "category": "column",
            "tableName": "dimdate",
            "key": "day_number"
        },
        "expressionObj": [],
        "securityConstants": {},
        "edit": false,
        "isAllowServiceCall": true,
        "isValidatedTableShow": false,
        "securityTableData": [],
        "addOneMoreSecurity": false,
        "viewSessionVariables": false,
        "textEditingObj": {},
        "selectedJoinNameData": {
            "category": "column",
            "value": "day_number"
        },
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
        "securityKeysChecked": [
            "dimdate.day_number"
        ],
        "hasUnsavedData": true,
        "getSecurityTableData": {
            "tables": [],
            "columns": []
        },
        "doResetFormData": false,
        "tablesMergeType": false,
        "activeDsInfoId": "2ybth"
    }
} 