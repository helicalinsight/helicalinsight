export const mockReduxStoreData = {
    metadata: {
        "dataFetchedFor": {
            "getDatasource": true,
            "joins": false,
            "viewSessionVariables": true,
            "listDataSources": true,
            "ctpr-9xu4-eyv2-zatw-q1": false,
            "5yje-lx3d-vj0j-fsbk-v9": false
        },
        "loadingStatus": {
            "getDatasource": true,
            "listDataSources": true,
            "ctpr-9xu4-eyv2-zatw-q1": false,
            "5yje-lx3d-vj0j-fsbk-v9": false
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
                                "keyPath": "4zbf-nx8l-qc0m-97ug-ci/ctpr-9xu4-eyv2-zatw-q1/5527-3cy2-n2la-ypmh-ze",
                                "key": "5527-3cy2-n2la-ypmh-ze",
                                "uuid": "5527-3cy2-n2la-ypmh-ze",
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
                                "keyPath": "4zbf-nx8l-qc0m-97ug-ci/ctpr-9xu4-eyv2-zatw-q1/eg1n-9yg5-qg75-lvtn-xp",
                                "key": "eg1n-9yg5-qg75-lvtn-xp",
                                "uuid": "eg1n-9yg5-qg75-lvtn-xp",
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
                                "keyPath": "4zbf-nx8l-qc0m-97ug-ci/ctpr-9xu4-eyv2-zatw-q1/p09i-exd2-uw4f-z5pb-be",
                                "key": "p09i-exd2-uw4f-z5pb-be",
                                "uuid": "p09i-exd2-uw4f-z5pb-be",
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
                                        "keyPath": "4zbf-nx8l-qc0m-97ug-ci/ctpr-9xu4-eyv2-zatw-q1/5yje-lx3d-vj0j-fsbk-v9/vvyp-b6ya-nvl1-90fp-t3",
                                        "key": "vvyp-b6ya-nvl1-90fp-t3",
                                        "alias": "dimdate",
                                        "uuid": "vvyp-b6ya-nvl1-90fp-t3",
                                        "connId": "c33p8",
                                        "dataSource": {
                                            "id": "1",
                                            "type": "dynamicDataSource",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "",
                                            "schema": "HIUSER",
                                            "connId": "c33p8",
                                            "classifier": "db.workflow",
                                            "datasourceName": "SampleTravelDataDerby",
                                            "dsKeyPath": "4zbf-nx8l-qc0m-97ug-ci/ctpr-9xu4-eyv2-zatw-q1/5yje-lx3d-vj0j-fsbk-v9",
                                            "driverType": "Derby",
                                            "database": "HIUSER"
                                        },
                                        "dataSourceName": "SampleTravelDataDerby",
                                        "category": "table",
                                        "nameWithConnId": "dimdate_c33p8",
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
                                        "keyPath": "4zbf-nx8l-qc0m-97ug-ci/ctpr-9xu4-eyv2-zatw-q1/5yje-lx3d-vj0j-fsbk-v9/vhgr-lmld-5hlx-gd6o-n2",
                                        "key": "vhgr-lmld-5hlx-gd6o-n2",
                                        "alias": "employee_details",
                                        "uuid": "vhgr-lmld-5hlx-gd6o-n2",
                                        "connId": "c33p8",
                                        "dataSource": {
                                            "id": "1",
                                            "type": "dynamicDataSource",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "",
                                            "schema": "HIUSER",
                                            "connId": "c33p8",
                                            "classifier": "db.workflow",
                                            "datasourceName": "SampleTravelDataDerby",
                                            "dsKeyPath": "4zbf-nx8l-qc0m-97ug-ci/ctpr-9xu4-eyv2-zatw-q1/5yje-lx3d-vj0j-fsbk-v9",
                                            "driverType": "Derby",
                                            "database": "HIUSER"
                                        },
                                        "dataSourceName": "SampleTravelDataDerby",
                                        "category": "table",
                                        "nameWithConnId": "employee_details_c33p8",
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
                                        "keyPath": "4zbf-nx8l-qc0m-97ug-ci/ctpr-9xu4-eyv2-zatw-q1/5yje-lx3d-vj0j-fsbk-v9/qpe3-4zmh-pfcw-bsvi-el",
                                        "key": "qpe3-4zmh-pfcw-bsvi-el",
                                        "alias": "geo_cordinates",
                                        "uuid": "qpe3-4zmh-pfcw-bsvi-el",
                                        "connId": "c33p8",
                                        "dataSource": {
                                            "id": "1",
                                            "type": "dynamicDataSource",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "",
                                            "schema": "HIUSER",
                                            "connId": "c33p8",
                                            "classifier": "db.workflow",
                                            "datasourceName": "SampleTravelDataDerby",
                                            "dsKeyPath": "4zbf-nx8l-qc0m-97ug-ci/ctpr-9xu4-eyv2-zatw-q1/5yje-lx3d-vj0j-fsbk-v9",
                                            "driverType": "Derby",
                                            "database": "HIUSER"
                                        },
                                        "dataSourceName": "SampleTravelDataDerby",
                                        "category": "table",
                                        "nameWithConnId": "geo_cordinates_c33p8",
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
                                        "keyPath": "4zbf-nx8l-qc0m-97ug-ci/ctpr-9xu4-eyv2-zatw-q1/5yje-lx3d-vj0j-fsbk-v9/4efl-3sjw-yjom-bn1s-28",
                                        "key": "4efl-3sjw-yjom-bn1s-28",
                                        "alias": "meeting_details",
                                        "uuid": "4efl-3sjw-yjom-bn1s-28",
                                        "connId": "c33p8",
                                        "dataSource": {
                                            "id": "1",
                                            "type": "dynamicDataSource",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "",
                                            "schema": "HIUSER",
                                            "connId": "c33p8",
                                            "classifier": "db.workflow",
                                            "datasourceName": "SampleTravelDataDerby",
                                            "dsKeyPath": "4zbf-nx8l-qc0m-97ug-ci/ctpr-9xu4-eyv2-zatw-q1/5yje-lx3d-vj0j-fsbk-v9",
                                            "driverType": "Derby",
                                            "database": "HIUSER"
                                        },
                                        "dataSourceName": "SampleTravelDataDerby",
                                        "category": "table",
                                        "nameWithConnId": "meeting_details_c33p8",
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
                                        "keyPath": "4zbf-nx8l-qc0m-97ug-ci/ctpr-9xu4-eyv2-zatw-q1/5yje-lx3d-vj0j-fsbk-v9/sa1k-bnoi-ah1g-v731-es",
                                        "key": "sa1k-bnoi-ah1g-v731-es",
                                        "alias": "travel_details",
                                        "uuid": "sa1k-bnoi-ah1g-v731-es",
                                        "connId": "c33p8",
                                        "dataSource": {
                                            "id": "1",
                                            "type": "dynamicDataSource",
                                            "baseType": "global.jdbc",
                                            "catSchemaPredicted": false,
                                            "sync": false,
                                            "catalog": "",
                                            "schema": "HIUSER",
                                            "connId": "c33p8",
                                            "classifier": "db.workflow",
                                            "datasourceName": "SampleTravelDataDerby",
                                            "dsKeyPath": "4zbf-nx8l-qc0m-97ug-ci/ctpr-9xu4-eyv2-zatw-q1/5yje-lx3d-vj0j-fsbk-v9",
                                            "driverType": "Derby",
                                            "database": "HIUSER"
                                        },
                                        "dataSourceName": "SampleTravelDataDerby",
                                        "category": "table",
                                        "nameWithConnId": "travel_details_c33p8",
                                        "database": "HIUSER",
                                        "schema": "HIUSER",
                                        "selected": false
                                    }
                                ],
                                "keyPath": "4zbf-nx8l-qc0m-97ug-ci/ctpr-9xu4-eyv2-zatw-q1/5yje-lx3d-vj0j-fsbk-v9",
                                "key": "5yje-lx3d-vj0j-fsbk-v9",
                                "uuid": "5yje-lx3d-vj0j-fsbk-v9",
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
                                "keyPath": "4zbf-nx8l-qc0m-97ug-ci/ctpr-9xu4-eyv2-zatw-q1/2od7-bd4h-s541-wtk7-n1",
                                "key": "2od7-bd4h-s541-wtk7-n1",
                                "uuid": "2od7-bd4h-s541-wtk7-n1",
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
                                "keyPath": "4zbf-nx8l-qc0m-97ug-ci/ctpr-9xu4-eyv2-zatw-q1/jsel-14y5-kife-6w2w-d2",
                                "key": "jsel-14y5-kife-6w2w-d2",
                                "uuid": "jsel-14y5-kife-6w2w-d2",
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
                                "keyPath": "4zbf-nx8l-qc0m-97ug-ci/ctpr-9xu4-eyv2-zatw-q1/ox0i-bdyn-7a2j-qaez-sh",
                                "key": "ox0i-bdyn-7a2j-qaez-sh",
                                "uuid": "ox0i-bdyn-7a2j-qaez-sh",
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
                                "keyPath": "4zbf-nx8l-qc0m-97ug-ci/ctpr-9xu4-eyv2-zatw-q1/agi8-3m9f-3z2o-50jm-0s",
                                "key": "agi8-3m9f-3z2o-50jm-0s",
                                "uuid": "agi8-3m9f-3z2o-50jm-0s",
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
                                "keyPath": "4zbf-nx8l-qc0m-97ug-ci/ctpr-9xu4-eyv2-zatw-q1/qgaq-nm0o-q4jb-i9yw-50",
                                "key": "qgaq-nm0o-q4jb-i9yw-50",
                                "uuid": "qgaq-nm0o-q4jb-i9yw-50",
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
                                "keyPath": "4zbf-nx8l-qc0m-97ug-ci/ctpr-9xu4-eyv2-zatw-q1/wsox-vaqk-wnon-jlo1-2q",
                                "key": "wsox-vaqk-wnon-jlo1-2q",
                                "uuid": "wsox-vaqk-wnon-jlo1-2q",
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
                                "keyPath": "4zbf-nx8l-qc0m-97ug-ci/ctpr-9xu4-eyv2-zatw-q1/xg41-sb67-iw8v-n50z-v2",
                                "key": "xg41-sb67-iw8v-n50z-v2",
                                "uuid": "xg41-sb67-iw8v-n50z-v2",
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
                                "keyPath": "4zbf-nx8l-qc0m-97ug-ci/ctpr-9xu4-eyv2-zatw-q1/c0pa-amm3-yv3y-981q-71",
                                "key": "c0pa-amm3-yv3y-981q-71",
                                "uuid": "c0pa-amm3-yv3y-981q-71",
                                "data": {
                                    "id": "1",
                                    "type": "dynamicDataSource"
                                },
                                "category": "schema",
                                "datasourceName": "SampleTravelDataDerby"
                            }
                        ],
                        "driverType": "Derby",
                        "keyPath": "4zbf-nx8l-qc0m-97ug-ci/ctpr-9xu4-eyv2-zatw-q1",
                        "key": "ctpr-9xu4-eyv2-zatw-q1",
                        "uuid": "ctpr-9xu4-eyv2-zatw-q1",
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
                        "keyPath": "4zbf-nx8l-qc0m-97ug-ci/hjlg-bblt-2hsu-icbf-ij",
                        "key": "hjlg-bblt-2hsu-icbf-ij",
                        "uuid": "hjlg-bblt-2hsu-icbf-ij"
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
                        "keyPath": "4zbf-nx8l-qc0m-97ug-ci/dewm-9ukh-o47x-csbk-0u",
                        "key": "dewm-9ukh-o47x-csbk-0u",
                        "uuid": "dewm-9ukh-o47x-csbk-0u"
                    }
                ],
                "key": "4zbf-nx8l-qc0m-97ug-ci",
                "uuid": "4zbf-nx8l-qc0m-97ug-ci",
                "keyPath": "4zbf-nx8l-qc0m-97ug-ci",
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
                        "keyPath": "setd-i1on-vspo-iajw-24/rypz-mmzz-sb7p-69tl-87",
                        "key": "rypz-mmzz-sb7p-69tl-87",
                        "uuid": "rypz-mmzz-sb7p-69tl-87"
                    }
                ],
                "key": "setd-i1on-vspo-iajw-24",
                "uuid": "setd-i1on-vspo-iajw-24",
                "keyPath": "setd-i1on-vspo-iajw-24",
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
                        "keyPath": "7hyo-ji5r-wm9o-ig6m-a0/92qt-4swt-afac-cgx1-oj",
                        "key": "92qt-4swt-afac-cgx1-oj",
                        "uuid": "92qt-4swt-afac-cgx1-oj"
                    }
                ],
                "key": "7hyo-ji5r-wm9o-ig6m-a0",
                "uuid": "7hyo-ji5r-wm9o-ig6m-a0",
                "keyPath": "7hyo-ji5r-wm9o-ig6m-a0",
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
                        "keyPath": "y4ww-ohfd-8o5b-mkkk-2k/zmao-57j8-1blu-wgng-y6",
                        "key": "zmao-57j8-1blu-wgng-y6",
                        "uuid": "zmao-57j8-1blu-wgng-y6"
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
                        "keyPath": "y4ww-ohfd-8o5b-mkkk-2k/vkrm-m27j-o9w0-ybax-03",
                        "key": "vkrm-m27j-o9w0-ybax-03",
                        "uuid": "vkrm-m27j-o9w0-ybax-03"
                    }
                ],
                "key": "y4ww-ohfd-8o5b-mkkk-2k",
                "uuid": "y4ww-ohfd-8o5b-mkkk-2k",
                "keyPath": "y4ww-ohfd-8o5b-mkkk-2k",
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
                    "dsUUID": "ctpr-9xu4-eyv2-zatw-q1",
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
                            "connId": "c33p8",
                            "classifier": "db.workflow",
                            "datasourceName": "SampleTravelDataDerby",
                            "dsKeyPath": "4zbf-nx8l-qc0m-97ug-ci/ctpr-9xu4-eyv2-zatw-q1/5yje-lx3d-vj0j-fsbk-v9",
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
                "connId": "c33p8",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "4zbf-nx8l-qc0m-97ug-ci/ctpr-9xu4-eyv2-zatw-q1/5yje-lx3d-vj0j-fsbk-v9",
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
                "keyPath": "4zbf-nx8l-qc0m-97ug-ci/ctpr-9xu4-eyv2-zatw-q1/5yje-lx3d-vj0j-fsbk-v9/vvyp-b6ya-nvl1-90fp-t3",
                "key": "vvyp-b6ya-nvl1-90fp-t3",
                "alias": "dimdate",
                "uuid": "vvyp-b6ya-nvl1-90fp-t3",
                "connId": "c33p8",
                "dataSource": {
                    "id": "1",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc",
                    "catSchemaPredicted": false,
                    "sync": false,
                    "catalog": "",
                    "schema": "HIUSER",
                    "connId": "c33p8",
                    "classifier": "db.workflow",
                    "datasourceName": "SampleTravelDataDerby",
                    "dsKeyPath": "4zbf-nx8l-qc0m-97ug-ci/ctpr-9xu4-eyv2-zatw-q1/5yje-lx3d-vj0j-fsbk-v9",
                    "driverType": "Derby",
                    "database": "HIUSER"
                },
                "dataSourceName": "SampleTravelDataDerby",
                "category": "table",
                "nameWithConnId": "dimdate_c33p8",
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
                "keyPath": "4zbf-nx8l-qc0m-97ug-ci/ctpr-9xu4-eyv2-zatw-q1/5yje-lx3d-vj0j-fsbk-v9/vhgr-lmld-5hlx-gd6o-n2",
                "key": "vhgr-lmld-5hlx-gd6o-n2",
                "alias": "employee_details",
                "uuid": "vhgr-lmld-5hlx-gd6o-n2",
                "connId": "c33p8",
                "dataSource": {
                    "id": "1",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc",
                    "catSchemaPredicted": false,
                    "sync": false,
                    "catalog": "",
                    "schema": "HIUSER",
                    "connId": "c33p8",
                    "classifier": "db.workflow",
                    "datasourceName": "SampleTravelDataDerby",
                    "dsKeyPath": "4zbf-nx8l-qc0m-97ug-ci/ctpr-9xu4-eyv2-zatw-q1/5yje-lx3d-vj0j-fsbk-v9",
                    "driverType": "Derby",
                    "database": "HIUSER"
                },
                "dataSourceName": "SampleTravelDataDerby",
                "category": "table",
                "nameWithConnId": "employee_details_c33p8",
                "database": "HIUSER",
                "schema": "HIUSER",
                "selected": true,
                "keyName": "employee_details"
            },
            "View 1": {
                "name": "View 1",
                "alias": "alias_dimdate",
                "columns": {
                    "dim_id": {
                        "alias": "dim_id",
                        "id": "19eb8f03-b7cc-418e-95c0-bc083ea6629d",
                        "columnId": "19eb8f03-b7cc-418e-95c0-bc083ea6629d",
                        "type": {
                            "java.lang.Float": "numeric"
                        },
                        "category": "column",
                        "parentCategory": "view",
                        "uuid": "ea91b376-9beb-4e31-8f6f-3fbc8dd8d522",
                        "tableKey": "View 1",
                        "columnKey": "dim_id",
                        "fullyQualifiedColumn": "View 1.dim_id"
                    },
                    "fiscal_year": {
                        "alias": "fiscal_year",
                        "id": "fc92133c-a494-416d-a3d5-1e08d70a84b6",
                        "columnId": "fc92133c-a494-416d-a3d5-1e08d70a84b6",
                        "type": {
                            "java.sql.Date": "date"
                        },
                        "category": "column",
                        "parentCategory": "view",
                        "uuid": "daeda90b-ebc1-407e-a336-7f8319f868cf",
                        "tableKey": "View 1",
                        "columnKey": "fiscal_year",
                        "fullyQualifiedColumn": "View 1.fiscal_year"
                    },
                    "modified_date": {
                        "alias": "modified_date",
                        "id": "e0abdba6-11aa-453b-8e33-dc807e00a7a2",
                        "columnId": "e0abdba6-11aa-453b-8e33-dc807e00a7a2",
                        "type": {
                            "java.sql.Timestamp": "dateTime"
                        },
                        "category": "column",
                        "parentCategory": "view",
                        "uuid": "09a103db-8e07-4733-b9dd-acd9ea5baeda",
                        "tableKey": "View 1",
                        "columnKey": "modified_date",
                        "fullyQualifiedColumn": "View 1.modified_date"
                    },
                    "date_key": {
                        "alias": "date_key",
                        "id": "fc4042ac-cd5e-4842-b6b8-e7a5ed49bff8",
                        "columnId": "fc4042ac-cd5e-4842-b6b8-e7a5ed49bff8",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "parentCategory": "view",
                        "uuid": "903883e8-6db5-4c8b-acb2-8786af512022",
                        "tableKey": "View 1",
                        "columnKey": "date_key",
                        "fullyQualifiedColumn": "View 1.date_key"
                    },
                    "day_number": {
                        "alias": "day_number",
                        "id": "0b1461b7-47a0-440c-aa18-3c07163d5407",
                        "columnId": "0b1461b7-47a0-440c-aa18-3c07163d5407",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "parentCategory": "view",
                        "uuid": "96f39ac7-a7b8-4c60-9b1c-5c3d61a6380e",
                        "tableKey": "View 1",
                        "columnKey": "day_number",
                        "fullyQualifiedColumn": "View 1.day_number"
                    },
                    "fiscal_month_name": {
                        "alias": "fiscal_month_name",
                        "id": "8cb635df-9ae8-4136-ba4a-37ffaa43327e",
                        "columnId": "8cb635df-9ae8-4136-ba4a-37ffaa43327e",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "parentCategory": "view",
                        "uuid": "851031f9-4c81-4a56-afed-fc2d06fbf43d",
                        "tableKey": "View 1",
                        "columnKey": "fiscal_month_name",
                        "fullyQualifiedColumn": "View 1.fiscal_month_name"
                    },
                    "fiscal_month_label": {
                        "alias": "fiscal_month_label",
                        "id": "7b0feec7-fccd-44f8-9d43-0bb9d83a4e2b",
                        "columnId": "7b0feec7-fccd-44f8-9d43-0bb9d83a4e2b",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "parentCategory": "view",
                        "uuid": "7d54e2a7-7898-4668-8987-90ec93f75650",
                        "tableKey": "View 1",
                        "columnKey": "fiscal_month_label",
                        "fullyQualifiedColumn": "View 1.fiscal_month_label"
                    },
                    "created_date": {
                        "alias": "created_date",
                        "id": "7a92bc3b-5f37-4707-8048-2ec19912498b",
                        "columnId": "7a92bc3b-5f37-4707-8048-2ec19912498b",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "parentCategory": "view",
                        "uuid": "927dfa14-e395-44a3-b364-6c80fe8a40c7",
                        "tableKey": "View 1",
                        "columnKey": "created_date",
                        "fullyQualifiedColumn": "View 1.created_date"
                    },
                    "created_time": {
                        "alias": "created_time",
                        "id": "233403fa-6d1b-4ce8-9dfb-e99fa987e238",
                        "columnId": "233403fa-6d1b-4ce8-9dfb-e99fa987e238",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "parentCategory": "view",
                        "uuid": "e04a4b06-47cb-4d64-aa49-83c8f1d44de0",
                        "tableKey": "View 1",
                        "columnKey": "created_time",
                        "fullyQualifiedColumn": "View 1.created_time"
                    },
                    "rating": {
                        "alias": "rating",
                        "id": "38d5df58-27c6-4342-b85a-4d812626959e",
                        "columnId": "38d5df58-27c6-4342-b85a-4d812626959e",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "parentCategory": "view",
                        "uuid": "bbe55c67-897b-478f-9459-aee244426916",
                        "tableKey": "View 1",
                        "columnKey": "rating",
                        "fullyQualifiedColumn": "View 1.rating"
                    }
                },
                "key": "kugm-8ksm-ja7d-p990-xb",
                "uuid": "kugm-8ksm-ja7d-p990-xb",
                "query": "select * from \"dimdate\"",
                "queryType": "conditionIf",
                "labels": [
                    {
                        "name": "dim_id",
                        "type": "numeric",
                        "checked": true
                    },
                    {
                        "name": "fiscal_year",
                        "type": "date",
                        "checked": true
                    },
                    {
                        "name": "modified_date",
                        "type": "dateTime",
                        "checked": true
                    },
                    {
                        "name": "date_key",
                        "type": "text",
                        "checked": true
                    },
                    {
                        "name": "day_number",
                        "type": "text",
                        "checked": true
                    },
                    {
                        "name": "fiscal_month_name",
                        "type": "text",
                        "checked": true
                    },
                    {
                        "name": "fiscal_month_label",
                        "type": "text",
                        "checked": true
                    },
                    {
                        "name": "created_date",
                        "type": "text",
                        "checked": true
                    },
                    {
                        "name": "created_time",
                        "type": "text",
                        "checked": true
                    },
                    {
                        "name": "rating",
                        "type": "text",
                        "checked": true
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
                    "connId": "c33p8",
                    "classifier": "db.workflow",
                    "datasourceName": "SampleTravelDataDerby",
                    "dsKeyPath": "4zbf-nx8l-qc0m-97ug-ci/ctpr-9xu4-eyv2-zatw-q1/5yje-lx3d-vj0j-fsbk-v9",
                    "driverType": "Derby",
                    "database": "HIUSER"
                },
                "error": false,
                "validate": true,
                "processedQuery": "select * from (select * from \"dimdate\") foo fetch first 1 rows only",
                "id": "7f4cf9dc-6427-4b92-b11f-de36a887e9a4",
                "type": "view",
                "isModified": true,
                "columnsFetched": true,
                "keyName": "View 1",
                "category": "view"
            }
        },
        "views": [
            {
                "name": "View 1",
                "alias": "View 1",
                "columns": {
                    "dim_id": {
                        "alias": "dim_id",
                        "id": "19eb8f03-b7cc-418e-95c0-bc083ea6629d",
                        "columnId": "19eb8f03-b7cc-418e-95c0-bc083ea6629d",
                        "type": {
                            "java.lang.Float": "numeric"
                        },
                        "category": "column",
                        "parentCategory": "view",
                        "uuid": "ea91b376-9beb-4e31-8f6f-3fbc8dd8d522",
                        "tableKey": "View 1",
                        "columnKey": "dim_id",
                        "fullyQualifiedColumn": "View 1.dim_id"
                    },
                    "fiscal_year": {
                        "alias": "fiscal_year",
                        "id": "fc92133c-a494-416d-a3d5-1e08d70a84b6",
                        "columnId": "fc92133c-a494-416d-a3d5-1e08d70a84b6",
                        "type": {
                            "java.sql.Date": "date"
                        },
                        "category": "column",
                        "parentCategory": "view",
                        "uuid": "daeda90b-ebc1-407e-a336-7f8319f868cf",
                        "tableKey": "View 1",
                        "columnKey": "fiscal_year",
                        "fullyQualifiedColumn": "View 1.fiscal_year"
                    },
                    "modified_date": {
                        "alias": "modified_date",
                        "id": "e0abdba6-11aa-453b-8e33-dc807e00a7a2",
                        "columnId": "e0abdba6-11aa-453b-8e33-dc807e00a7a2",
                        "type": {
                            "java.sql.Timestamp": "dateTime"
                        },
                        "category": "column",
                        "parentCategory": "view",
                        "uuid": "09a103db-8e07-4733-b9dd-acd9ea5baeda",
                        "tableKey": "View 1",
                        "columnKey": "modified_date",
                        "fullyQualifiedColumn": "View 1.modified_date"
                    },
                    "date_key": {
                        "alias": "date_key",
                        "id": "fc4042ac-cd5e-4842-b6b8-e7a5ed49bff8",
                        "columnId": "fc4042ac-cd5e-4842-b6b8-e7a5ed49bff8",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "parentCategory": "view",
                        "uuid": "903883e8-6db5-4c8b-acb2-8786af512022",
                        "tableKey": "View 1",
                        "columnKey": "date_key",
                        "fullyQualifiedColumn": "View 1.date_key"
                    },
                    "day_number": {
                        "alias": "day_number",
                        "id": "0b1461b7-47a0-440c-aa18-3c07163d5407",
                        "columnId": "0b1461b7-47a0-440c-aa18-3c07163d5407",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "parentCategory": "view",
                        "uuid": "96f39ac7-a7b8-4c60-9b1c-5c3d61a6380e",
                        "tableKey": "View 1",
                        "columnKey": "day_number",
                        "fullyQualifiedColumn": "View 1.day_number"
                    },
                    "fiscal_month_name": {
                        "alias": "fiscal_month_name",
                        "id": "8cb635df-9ae8-4136-ba4a-37ffaa43327e",
                        "columnId": "8cb635df-9ae8-4136-ba4a-37ffaa43327e",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "parentCategory": "view",
                        "uuid": "851031f9-4c81-4a56-afed-fc2d06fbf43d",
                        "tableKey": "View 1",
                        "columnKey": "fiscal_month_name",
                        "fullyQualifiedColumn": "View 1.fiscal_month_name"
                    },
                    "fiscal_month_label": {
                        "alias": "fiscal_month_label",
                        "id": "7b0feec7-fccd-44f8-9d43-0bb9d83a4e2b",
                        "columnId": "7b0feec7-fccd-44f8-9d43-0bb9d83a4e2b",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "parentCategory": "view",
                        "uuid": "7d54e2a7-7898-4668-8987-90ec93f75650",
                        "tableKey": "View 1",
                        "columnKey": "fiscal_month_label",
                        "fullyQualifiedColumn": "View 1.fiscal_month_label"
                    },
                    "created_date": {
                        "alias": "created_date",
                        "id": "7a92bc3b-5f37-4707-8048-2ec19912498b",
                        "columnId": "7a92bc3b-5f37-4707-8048-2ec19912498b",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "parentCategory": "view",
                        "uuid": "927dfa14-e395-44a3-b364-6c80fe8a40c7",
                        "tableKey": "View 1",
                        "columnKey": "created_date",
                        "fullyQualifiedColumn": "View 1.created_date"
                    },
                    "created_time": {
                        "alias": "created_time",
                        "id": "233403fa-6d1b-4ce8-9dfb-e99fa987e238",
                        "columnId": "233403fa-6d1b-4ce8-9dfb-e99fa987e238",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "parentCategory": "view",
                        "uuid": "e04a4b06-47cb-4d64-aa49-83c8f1d44de0",
                        "tableKey": "View 1",
                        "columnKey": "created_time",
                        "fullyQualifiedColumn": "View 1.created_time"
                    },
                    "rating": {
                        "alias": "rating",
                        "id": "38d5df58-27c6-4342-b85a-4d812626959e",
                        "columnId": "38d5df58-27c6-4342-b85a-4d812626959e",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "parentCategory": "view",
                        "uuid": "bbe55c67-897b-478f-9459-aee244426916",
                        "tableKey": "View 1",
                        "columnKey": "rating",
                        "fullyQualifiedColumn": "View 1.rating"
                    }
                },
                "key": "kugm-8ksm-ja7d-p990-xb",
                "uuid": "kugm-8ksm-ja7d-p990-xb",
                "query": "select * from \"dimdate\"",
                "queryType": "conditionIf",
                "labels": [
                    {
                        "name": "dim_id",
                        "type": "numeric",
                        "checked": true
                    },
                    {
                        "name": "fiscal_year",
                        "type": "date",
                        "checked": true
                    },
                    {
                        "name": "modified_date",
                        "type": "dateTime",
                        "checked": true
                    },
                    {
                        "name": "date_key",
                        "type": "text",
                        "checked": true
                    },
                    {
                        "name": "day_number",
                        "type": "text",
                        "checked": true
                    },
                    {
                        "name": "fiscal_month_name",
                        "type": "text",
                        "checked": true
                    },
                    {
                        "name": "fiscal_month_label",
                        "type": "text",
                        "checked": true
                    },
                    {
                        "name": "created_date",
                        "type": "text",
                        "checked": true
                    },
                    {
                        "name": "created_time",
                        "type": "text",
                        "checked": true
                    },
                    {
                        "name": "rating",
                        "type": "text",
                        "checked": true
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
                    "connId": "c33p8",
                    "classifier": "db.workflow",
                    "datasourceName": "SampleTravelDataDerby",
                    "dsKeyPath": "4zbf-nx8l-qc0m-97ug-ci/ctpr-9xu4-eyv2-zatw-q1/5yje-lx3d-vj0j-fsbk-v9",
                    "driverType": "Derby",
                    "database": "HIUSER"
                },
                "error": false,
                "validate": true,
                "processedQuery": "select * from (select * from \"dimdate\") foo fetch first 1 rows only",
                "id": "7f4cf9dc-6427-4b92-b11f-de36a887e9a4",
                "type": "view",
                "isModified": true
            }
        ],
        "activeView": null,
        "categories": {
            "4zbf-nx8l-qc0m-97ug-ci": {
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
            "setd-i1on-vspo-iajw-24": {
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
            "7hyo-ji5r-wm9o-ig6m-a0": {
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
            "y4ww-ohfd-8o5b-mkkk-2k": {
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
        "activeEditorTab": "views",
        "dataSourcesAddedToMetadata": [
            {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "c33p8",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "4zbf-nx8l-qc0m-97ug-ci/ctpr-9xu4-eyv2-zatw-q1/5yje-lx3d-vj0j-fsbk-v9",
                "driverType": "Derby",
                "database": "HIUSER"
            }
        ],
        "changeDSList": {},
        "changedTables": [
            {
                "id": "7f4cf9dc-6427-4b92-b11f-de36a887e9a4",
                "alias": "alias_dimdate"
            }
        ],
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
            "qpe3-4zmh-pfcw-bsvi-el",
            "4efl-3sjw-yjom-bn1s-28",
            "vhgr-lmld-5hlx-gd6o-n2",
            "vvyp-b6ya-nvl1-90fp-t3",
            "sa1k-bnoi-ah1g-v731-es"
        ],
        "selectedTableKeys": [
            "vvyp-b6ya-nvl1-90fp-t3",
            "vhgr-lmld-5hlx-gd6o-n2"
        ],
        "metadataName": "Metadata_1",
        "activeDataSource": false,
        "metadataToEdit": false,
        "isSavingInProgress": false,
        "editViewsTempData": {
            "kugm-8ksm-ja7d-p990-xb": {
                "name": "View 1",
                "alias": "View 1",
                "columns": {},
                "key": "kugm-8ksm-ja7d-p990-xb",
                "uuid": "kugm-8ksm-ja7d-p990-xb",
                "query": "select * from \"dimdate\"",
                "queryType": "conditionIf",
                "labels": [
                    {
                        "name": "dim_id",
                        "type": "numeric",
                        "checked": true
                    },
                    {
                        "name": "fiscal_year",
                        "type": "date",
                        "checked": true
                    },
                    {
                        "name": "modified_date",
                        "type": "dateTime",
                        "checked": true
                    },
                    {
                        "name": "date_key",
                        "type": "text",
                        "checked": true
                    },
                    {
                        "name": "day_number",
                        "type": "text",
                        "checked": true
                    },
                    {
                        "name": "fiscal_month_name",
                        "type": "text",
                        "checked": true
                    },
                    {
                        "name": "fiscal_month_label",
                        "type": "text",
                        "checked": true
                    },
                    {
                        "name": "created_date",
                        "type": "text",
                        "checked": true
                    },
                    {
                        "name": "created_time",
                        "type": "text",
                        "checked": true
                    },
                    {
                        "name": "rating",
                        "type": "text",
                        "checked": true
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
                    "connId": "c33p8",
                    "classifier": "db.workflow",
                    "datasourceName": "SampleTravelDataDerby",
                    "dsKeyPath": "4zbf-nx8l-qc0m-97ug-ci/ctpr-9xu4-eyv2-zatw-q1/5yje-lx3d-vj0j-fsbk-v9",
                    "driverType": "Derby",
                    "database": "HIUSER"
                },
                "error": false,
                "validate": true,
                "processedQuery": "select * from (select * from \"dimdate\") foo fetch first 1 rows only"
            }
        },
        "inititalStateFromJest": false,
        "timeStamp": 1670319979199,
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
        "viewSessionVariables": {
            "hasStoredProcedure": {
                "expressions": {
                    "user": [
                        "${user}.id",
                        "${user}.email",
                        "${user}.enabled",
                        "${user}.name",
                        "${user}.isExternalUser"
                    ],
                    "org": [
                        "${org}.id",
                        "${org}.name"
                    ],
                    "profile": [
                        "${profile}.name",
                        "${profile}.id",
                        "${profile}.value"
                    ],
                    "role": [
                        "${role}.id",
                        "${role}.name"
                    ]
                },
                "tooltip": "Stored procedure",
                "placeHolder": "call  yourStoredProcedure('param1','param2',...)",
                "helpText": "WW91IGNhbiBleGVjdXRlIGEgc3RvcmVkIHByb2NlZHVyZQ"
            },
            "conditionIf": {
                "expressions": {
                    "user": [
                        "${user}.id",
                        "${user}.email",
                        "${user}.enabled",
                        "${user}.name",
                        "${user}.isExternalUser"
                    ],
                    "org": [
                        "${org}.id",
                        "${org}.name"
                    ],
                    "profile": [
                        "${profile}.name",
                        "${profile}.id",
                        "${profile}.value"
                    ],
                    "role": [
                        "${role}.id",
                        "${role}.name"
                    ]
                },
                "tooltip": "Query",
                "placeHolder": "select 1",
                "helpText": "WW91IGNhbiB3cml0ZSBhIHNpbXBsZSBxdWVyeS4NCllvdSBtYXkgYWxzbyB1c2UgdGhlIHNlc3Npb24gdmFyaWFibGUgaW4gdGhlIHF1ZXJ5"
            },
            "groovy": {
                "expressions": {
                    "filter": [
                        "${filter}.label",
                        "${filter}.id",
                        "${filter}.column",
                        "${filter}.value",
                        "${filter}.values",
                        "${filter}.mode",
                        "${filter}.condition",
                        "${filter}.fullyQualifiedColumn"
                    ],
                    "globalOptions": [
                        "check(\"${filter}.label\",\"value\")",
                        "hasFilters()",
                        "addQuotes(string)",
                        "getFilterArrayByLabels([\"labelArray\"])",
                        "findFilterByLabel(\"label\")",
                        "findFilter([key:value,key:value])",
                        "findFilter('{\"key\":\"value\"}')",
                        "formData",
                        "filters",
                        "sqlContext",
                        "filters.size()"
                    ]
                },
                "tooltip": "Dynamic Query",
                "placeHolder": "return 'select 1'",
                "helpText": "Ly9SZXR1cm4gdGhlIHF1ZXJ5IHN0cmluZyBhdCB0aGUgZW5kDQpyZXR1cm4gInNlbGVjdCAgKiBmcm9tIHlvdXJfdGFibGVOYW1lICINCg0KLy9UaGlzIHJldHVybnMgdHJ1ZSBvciBmYWxzZSANCmNoZWNrKCIke2ZpbHRlcn0ubGFiZWwiLCJ0cmF2ZWxNb2RlIikNCg0KDQovL3JldHVybnMgdHJ1ZSBpZiBhIGZpbHRlciBleGlzdHMNCmhhc0ZpbHRlcnMoKQ0KDQoNCi8vcmV0dXJucyBhIGZpbHRlciANCi8vaWYgdGhlIGxhYmVsICJ0cmF2ZWxNb2RlIiBleGlzdHMgb3RoZXJ3aXNlIG51bGwNCm15RmlsdGVyPWZpbmRGaWx0ZXJCeUxhYmVsKCJ0cmF2ZWxNb2RlIikgDQoNCg0KLy9yZXR1cm5zIGEgZmlsdGVyIA0KLy9pZiB0aGUgbGFiZWwgInRyYXZlbE1vZGUiIGV4aXN0cyBvdGhlcndpc2UgbnVsbA0KbXlGaWx0ZXI9ZmluZEZpbHRlcigneyJsYWJlbCI6InRyYXZlbE1vZGUifScpDQoNCg0KDQovL3JldHVybnMgYSBmaWx0ZXIgdGhhdCBoYXMgY29uZGl0aW9uID0gYW5kIGlkIGFzIDQNCm15RmlsdGVyID0gZmluZEZpbHRlcihbY29uZGl0aW9uOiI9IixpZDo0XSkgDQoNCg0KLy9Ob3RlIA0KLy9teUZpbHRlci52YWx1ZSByZXR1cm5zIHlvdSB0aGUgdmFsdWUgYXMgYSBzdHJpbmcuIA0KLy9teUZpbHRlci52YWx1ZXMgcmV0dXJucyB5b3UgYW4gYXJyYXkgb2YgdmFsdWUgd2hpY2ggDQovLyAJCQkJICAgY2FuIGJlIGFjY2Vzc2VkIHVzaW5nIGFycmF5IGluZGV4Lg0KLy8gZGVmIGZpcnN0VmFsID0gbXlGaWx0ZXIudmFsdWVzWzBdDQoNCi8vdXNlIHRoZSBmaWx0ZXIgcmVmZXJlbmNlIGluIHlvdXIgcXVlcnkNCm15UXVlcnkgPSAic2VsZWN0ICogZnJvbSB0cmF2ZWxfZGV0YWlscyANCgkJd2hlcmUgJHtteUZpbHRlci5jb2x1bW59ID0gJHtteUZpbHRlci52YWx1ZX0iIA0KDQoNCi8vcmV0dXJucyBhbiBhcnJheSBvZiAgZmlsdGVycyANCi8vaWYgdGhlIGxhYmVsICJ0cmF2ZWxNb2RlIiBleGlzdHMgb3RoZXJ3aXNlIG51bGwNCm15ZmlsdHJBcnI9Z2V0RmlsdGVyQXJyYXlCeUxhYmVscyhbInRyYXZlbE1vZGUiLCJ0cmF2ZWxTb3VyY2UiXSkNCg0KDQoNCi8vWW91IGNhbiBkZWZpbmUgY3VzdG9tIGZ1bmN0aW9uIGF2YWlsYWJsZSBpbiB0aGUgbG9jYWwgc2NvcGUNCg0KZGVmIHlvdXJGdW5jdGlvbihwYXJtMSxwYXJhbTIpew0KLy9zdGVwIDEgd2l0aCBwYXJhbQ0KLy9zZXRwIDIgd2l0aCBwYXJhbQ0KfQ0KDQovL0V4cHJlc3Npb24gYXZhaWxhYmxlDQovL2NvbWEgc2VwYXJhdGVkIGxpc3Qgb2YgYWxsIHRoZSBmaWx0ZXIgbGFiZWxzDQogIiR7ZmlsdGVyfS5sYWJlbCINCiANCi8vY29tYSBzZXBhcmF0ZWQgbGlzdCBvZiBhbGwgdGhlIGZpbHRlciBpZA0KLy9TdGFydHMgZnJvbSAwDQogIiR7ZmlsdGVyfS5pZCINCg0KLy9jb21hIHNlcGFyYXRlZCBsaXN0IG9mIGFsbCB0aGUgZmlsdGVyIGNvbHVtbg0KICIke2ZpbHRlcn0uY29sdW1uIg0KIA0KIC8vY29tYSBzZXBhcmF0ZWQgbGlzdCBvZiBhbGwgcXVhbGlmaWVkIGNvbHVtbg0KICIke2ZpbHRlcn0uZnVsbHlRdWFsaWZpZWRDb2x1bW4iDQoNCi8vY29tYSBzZXBhcmF0ZWQgbGlzdCBvZiBhbGwgdGhlIGZpbHRlciB2YWx1ZXMNCiAiJHtmaWx0ZXJ9LnZhbHVlcyINCg0KLy9jb21hIHNlcGFyYXRlZCBsaXN0IG9mIGFsbCB0aGUgZmlsdGVyIHZhbHVlDQogIiR7ZmlsdGVyfS52YWx1ZSINCg0KLy9jb21hIHNlcGFyYXRlZCBsaXN0IG9mIGFsbCB0aGUgZmlsdGVyIG1vZGUNCiAiJHtmaWx0ZXJ9Lm1vZGUiDQoNCi8vY29tYSBzZXBhcmF0ZWQgbGlzdCBvZiBhbGwgdGhlIGZpbHRlciBjb25kaXRpb24NCiAiJHtmaWx0ZXJ9LmNvbmRpdGlvbiINCg0KLy9CZWxvdyBhcmUgdGhlIGdsb2JhbCB2YXJpYWJsZXMgdGhhdCBjYW4gYmUgYWNjZXNzZWQuIA0KZm9ybURhdGENCmZpbHRlcnMNCnNxbENvbnRleHQNCg0KLy9lZw0KLy8gdGhpcyB3aWxsIHJldHVybiB0aGUgY291bnQgb2YgZmlsdGVycyB1c2VkIGluIHJlcG9ydA0KZmlsdGVycy5zaXplKCkNCiANCg0KDQovL0FkZHMgYSBzaW5nbGUgcXVvdGUgdG8gdGhlIHRleHQgdGVzdA0KbXlRdW90ZWRDb2w9YWRkUXVvdGVzKCJ0ZXN0IikgDQovL291dHB1dCANCid0ZXN0Jw0KDQovL1NpbXBsZSBFeGFtcGxlKERlcmJ5KQ0KIA0KZGVmIHEgPSAic2VsZWN0ICogZnJvbSBcInRyYXZlbF9kZXRhaWxzXCIiDQppZihjaGVjaygiJHtmaWx0ZXJ9LmxhYmVsIiAsICJib29raW5nIikpew0KCWRlZiBmID0gZmluZEZpbHRlckJ5TGFiZWwoImJvb2tpbmciKQ0KCXJldHVybiBxKyIgd2hlcmUgXCJib29raW5nX3BsYXRmb3JtXCIgPSAkZi52YWx1ZSAiDQp9DQplbHNlew0KCXJldHVybiBxDQp9DQoNCi8vUGxlYXNlIG5vdCB0aGUgdXNlIG9mIGVzY2FwZSBkb3VibGUgcXVvdGUuDQovL1RoaXMgbWF5IGJlIHJlcXVpcmVkIHdoZW4gd2UgaGF2ZSBhIGRpYWxlY3QgbGlrZSBkZXJieQ0KLy8gd2hlcmUgdGhlIHRhYmxlIG5hbWUgcmVxdWlyZWQgdG8gYmUgIiAoZG91YmxlIHF1b3RlZCkNCg0KDQovL0FkdmFuY2VkIEV4YW1wbGUoRGVyYnkpDQoNCmRlZiBxID0gInNlbGVjdCAqIGZyb20gXCJ0cmF2ZWxfZGV0YWlsc1wiIg0KZGVmIHJlcG9ydHNfZmlsdGVyID0gWydib29raW5nJywnbW9kZSddDQppbnQgd2MgPSAwOw0KZm9yKGludCBpPTA7aTxmaWx0ZXJzLnNpemUoKTtpKyspew0KCWRlZiBpdGVtPWZpbHRlcnNbaV0NCglpZihyZXBvcnRzX2ZpbHRlci5jb250YWlucyhpdGVtLmxhYmVsKSl7DQoJCWlmKHdjID09IDApew0KCQkJcSA9IHEgKyAiIHdoZXJlICR7aXRlbS5jb2x1bW59ICR7aXRlbS5jb25kaXRpb259ICR7aXRlbS52YWx1ZX0iDQoJCQl3YysrDQoJCX0NCgkJZWxzZXsNCgkJCXEgPSBxICsgIiBhbmQgJHtpdGVtLmNvbHVtbn0gJHtpdGVtLmNvbmRpdGlvbn0gJHtpdGVtLnZhbHVlfSINCgkJfQ0KCX0NCn0NCnJldHVybiBx"
            }
        },
        "textEditingObj": {},
        "selectedJoinNameData": {
            "category": "table",
            "value": "alias_dimdate"
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
        "securityKeysChecked": [],
        "hasUnsavedData": true,
        "getSecurityTableData": {
            "tables": [],
            "columns": []
        },
        "doResetFormData": false,
        "tablesMergeType": false,
        "activeDsInfoId": "c33p8"
    }
}