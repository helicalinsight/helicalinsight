export const mockStore = {
    "dataFetchedFor": {
        "getDatasource": true,
        "joins": false,
        "viewSessionVariables": false,
        "listDataSources": true,
        "t430-kekk-7bro-ffq9-lp": false,
        "o7bc-2l0g-8pm5-0gmf-26": false
    },
    "loadingStatus": {
        "getDatasource": true,
        "listDataSources": true,
        "t430-kekk-7bro-ffq9-lp": false,
        "o7bc-2l0g-8pm5-0gmf-26": false
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
            "enabledTypes": true,
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
            "name": "Mongodb",
            "classifier": "global",
            "categoryName": "No SQL & Big Data",
            "categoryType": "nosql_bigdata",
            "type": "global.jdbc",
            "driver": "com.helicalinsight.nosql.mongo",
            "url": "mongodb://{{hostName}}:{{port}}/{{database}}",
            "parameters": {
                "port": "27017",
                "hostName": "localhost",
                "database": "database",
                "collection": "collection",
                "sslPort": "3345"
            },
            "dataSourceProvider": "noSql"
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
            "enabledTypes": true,
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
            "name": "Mongodb",
            "classifier": "global",
            "categoryName": "No SQL & Big Data",
            "categoryType": "nosql_bigdata",
            "type": "global.jdbc",
            "driver": "com.helicalinsight.nosql.mongo",
            "url": "mongodb://{{hostName}}:{{port}}/{{database}}",
            "parameters": {
                "port": "27017",
                "hostName": "localhost",
                "database": "database",
                "collection": "collection",
                "sslPort": "3345"
            },
            "dataSourceProvider": "noSql"
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
            "driver": "com.helicalinsight.nosql.mongo",
            "available": "true",
            "url": "mongodb://{{hostName}}:{{port}}/{{database}}",
            "parameters": {
                "port": "27017",
                "hostName": "localhost",
                "database": "database",
                "collection": "collection",
                "sslPort": "3345"
            }
        },
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
                            "keyPath": "rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/n8l9-bjne-xiqh-ce1d-5z",
                            "key": "n8l9-bjne-xiqh-ce1d-5z",
                            "uuid": "n8l9-bjne-xiqh-ce1d-5z",
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
                            "keyPath": "rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/ntjf-2dxf-5o0j-xjns-3q",
                            "key": "ntjf-2dxf-5o0j-xjns-3q",
                            "uuid": "ntjf-2dxf-5o0j-xjns-3q",
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
                            "keyPath": "rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/2bli-i53o-uvzf-m5ea-d1",
                            "key": "2bli-i53o-uvzf-m5ea-d1",
                            "uuid": "2bli-i53o-uvzf-m5ea-d1",
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
                                    "keyPath": "rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/o7bc-2l0g-8pm5-0gmf-26/vw7z-x0s4-e45z-awdr-qm",
                                    "key": "vw7z-x0s4-e45z-awdr-qm",
                                    "alias": "dimdate",
                                    "uuid": "vw7z-x0s4-e45z-awdr-qm",
                                    "connId": "7tcf9",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "7tcf9",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/o7bc-2l0g-8pm5-0gmf-26",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "dimdate_7tcf9",
                                    "database": "HIUSER",
                                    "schema": "HIUSER"
                                },
                                {
                                    "id": "4e1fd245f4d13b77be423a43f01d80b2",
                                    "name": "employee_details",
                                    "data": {
                                        "id": "1",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/o7bc-2l0g-8pm5-0gmf-26/x14u-u9v1-t49p-o6fy-g3",
                                    "key": "x14u-u9v1-t49p-o6fy-g3",
                                    "alias": "employee_details",
                                    "uuid": "x14u-u9v1-t49p-o6fy-g3",
                                    "connId": "7tcf9",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "7tcf9",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/o7bc-2l0g-8pm5-0gmf-26",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "employee_details_7tcf9",
                                    "database": "HIUSER",
                                    "schema": "HIUSER"
                                },
                                {
                                    "id": "be534112989b616b194bc59c2fb25a42",
                                    "name": "geo_cordinates",
                                    "data": {
                                        "id": "1",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/o7bc-2l0g-8pm5-0gmf-26/ylhu-q8ql-xu6k-s8fm-jk",
                                    "key": "ylhu-q8ql-xu6k-s8fm-jk",
                                    "alias": "geo_cordinates",
                                    "uuid": "ylhu-q8ql-xu6k-s8fm-jk",
                                    "connId": "7tcf9",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "7tcf9",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/o7bc-2l0g-8pm5-0gmf-26",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "geo_cordinates_7tcf9",
                                    "database": "HIUSER",
                                    "schema": "HIUSER"
                                },
                                {
                                    "id": "9645c648a1c0dbeec1287aaf1e996db3",
                                    "name": "meeting_details",
                                    "data": {
                                        "id": "1",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/o7bc-2l0g-8pm5-0gmf-26/7ybz-mg0p-b2z7-yg3d-2l",
                                    "key": "7ybz-mg0p-b2z7-yg3d-2l",
                                    "alias": "meeting_details",
                                    "uuid": "7ybz-mg0p-b2z7-yg3d-2l",
                                    "connId": "7tcf9",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "7tcf9",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/o7bc-2l0g-8pm5-0gmf-26",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "meeting_details_7tcf9",
                                    "database": "HIUSER",
                                    "schema": "HIUSER"
                                },
                                {
                                    "id": "8a28627d07d04ef096d9935f12e0c7e9",
                                    "name": "travel_details",
                                    "data": {
                                        "id": "1",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/o7bc-2l0g-8pm5-0gmf-26/3cla-56dy-vw29-jupc-or",
                                    "key": "3cla-56dy-vw29-jupc-or",
                                    "alias": "travel_details",
                                    "uuid": "3cla-56dy-vw29-jupc-or",
                                    "connId": "7tcf9",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "7tcf9",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/o7bc-2l0g-8pm5-0gmf-26",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "travel_details_7tcf9",
                                    "database": "HIUSER",
                                    "schema": "HIUSER"
                                }
                            ],
                            "keyPath": "rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/o7bc-2l0g-8pm5-0gmf-26",
                            "key": "o7bc-2l0g-8pm5-0gmf-26",
                            "uuid": "o7bc-2l0g-8pm5-0gmf-26",
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
                            "keyPath": "rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/uerd-tei3-rx2d-29tv-gh",
                            "key": "uerd-tei3-rx2d-29tv-gh",
                            "uuid": "uerd-tei3-rx2d-29tv-gh",
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
                            "keyPath": "rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/hk53-fil6-geya-9dgh-we",
                            "key": "hk53-fil6-geya-9dgh-we",
                            "uuid": "hk53-fil6-geya-9dgh-we",
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
                            "keyPath": "rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/lsvp-tlks-iizc-6ose-3y",
                            "key": "lsvp-tlks-iizc-6ose-3y",
                            "uuid": "lsvp-tlks-iizc-6ose-3y",
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
                            "keyPath": "rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/6lec-zfsz-ldd4-ca8h-3k",
                            "key": "6lec-zfsz-ldd4-ca8h-3k",
                            "uuid": "6lec-zfsz-ldd4-ca8h-3k",
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
                            "keyPath": "rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/7vl0-zo1r-hoxc-32ad-zc",
                            "key": "7vl0-zo1r-hoxc-32ad-zc",
                            "uuid": "7vl0-zo1r-hoxc-32ad-zc",
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
                            "keyPath": "rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/afvo-ufqi-0ch3-sajx-3z",
                            "key": "afvo-ufqi-0ch3-sajx-3z",
                            "uuid": "afvo-ufqi-0ch3-sajx-3z",
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
                            "keyPath": "rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/rb1x-5sn8-4shx-lp8b-6m",
                            "key": "rb1x-5sn8-4shx-lp8b-6m",
                            "uuid": "rb1x-5sn8-4shx-lp8b-6m",
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
                            "keyPath": "rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/gge3-0xhc-0mpe-h4j6-aa",
                            "key": "gge3-0xhc-0mpe-h4j6-aa",
                            "uuid": "gge3-0xhc-0mpe-h4j6-aa",
                            "data": {
                                "id": "1",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "SampleTravelDataDerby"
                        }
                    ],
                    "driverType": "Derby",
                    "keyPath": "rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp",
                    "key": "t430-kekk-7bro-ffq9-lp",
                    "uuid": "t430-kekk-7bro-ffq9-lp",
                    "fetched": true
                }
            ],
            "key": "rwao-mnv9-1cbr-rrnk-ff",
            "uuid": "rwao-mnv9-1cbr-rrnk-ff",
            "keyPath": "rwao-mnv9-1cbr-rrnk-ff",
            "category": "dsGroup",
            "imgUrl": "images/data_sources/defaut_datasource.png"
        },
        {
            "name": "Mysql",
            "children": [
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
                    "keyPath": "z8jz-mad2-6out-6r5s-9b/hrk3-jk10-ne3m-tscl-e4",
                    "key": "hrk3-jk10-ne3m-tscl-e4",
                    "uuid": "hrk3-jk10-ne3m-tscl-e4"
                }
            ],
            "key": "z8jz-mad2-6out-6r5s-9b",
            "uuid": "z8jz-mad2-6out-6r5s-9b",
            "keyPath": "z8jz-mad2-6out-6r5s-9b",
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
                    "children": [],
                    "driverType": "Sqlite",
                    "keyPath": "c3yf-quir-8ucm-54iz-q3/ct0o-2aq7-2d9p-hird-jl",
                    "key": "ct0o-2aq7-2d9p-hird-jl",
                    "uuid": "ct0o-2aq7-2d9p-hird-jl"
                }
            ],
            "key": "c3yf-quir-8ucm-54iz-q3",
            "uuid": "c3yf-quir-8ucm-54iz-q3",
            "keyPath": "c3yf-quir-8ucm-54iz-q3",
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
                "dsUUID": "t430-kekk-7bro-ffq9-lp",
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
                        "connId": "7tcf9",
                        "classifier": "db.workflow",
                        "datasourceName": "SampleTravelDataDerby",
                        "dsKeyPath": "rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/o7bc-2l0g-8pm5-0gmf-26",
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
            "connId": "7tcf9",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelDataDerby",
            "dsKeyPath": "rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/o7bc-2l0g-8pm5-0gmf-26",
            "driverType": "Derby",
            "database": "HIUSER"
        }
    ],
    "tables": {},
    "views": [],
    "activeView": false,
    "categories": {
        "rwao-mnv9-1cbr-rrnk-ff": {
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
        "z8jz-mad2-6out-6r5s-9b": {
            "ds": {
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
        "c3yf-quir-8ucm-54iz-q3": {
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
    "dataSourcesAddedToMetadata": [],
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
        "ylhu-q8ql-xu6k-s8fm-jk",
        "7ybz-mg0p-b2z7-yg3d-2l",
        "x14u-u9v1-t49p-o6fy-g3",
        "vw7z-x0s4-e45z-awdr-qm",
        "3cla-56dy-vw29-jupc-or"
    ],
    "selectedTableKeys": [],
    "metadataName": "Metadata_1",
    "activeDataSource": false,
    "metadataToEdit": false,
    "isSavingInProgress": false,
    "editViewsTempData": {},
    "inititalStateFromJest": false,
    "timeStamp": 1662110567554,
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
    "hasUnsavedData": true
}

export const actionData = {
    type: 'UPDATE_METADATA_STATE',
    payload: {
      key: 'datasourceListToRender',
      value: [
        {
          name: 'Derby',
          children: [
            {
              data: {
                id: '1',
                type: 'dynamicDataSource'
              },
              dataSourceProvider: 'tomcat',
              type: 'dynamicDataSource',
              permissionLevel: 5,
              driver: 'org.apache.derby.jdbc.ClientDriver',
              name: 'SampleTravelDataDerby',
              classifier: 'global',
              dataSourceType: 'Managed DataSource',
              category: 'dataSource',
              children: [
                {
                  name: 'SQLJ',
                  children: [],
                  keyPath: 'rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/n8l9-bjne-xiqh-ce1d-5z',
                  key: 'n8l9-bjne-xiqh-ce1d-5z',
                  uuid: 'n8l9-bjne-xiqh-ce1d-5z',
                  data: {
                    id: '1',
                    type: 'dynamicDataSource'
                  },
                  category: 'schema',
                  datasourceName: 'SampleTravelDataDerby'
                },
                {
                  name: 'SYSFUN',
                  children: [],
                  keyPath: 'rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/ntjf-2dxf-5o0j-xjns-3q',
                  key: 'ntjf-2dxf-5o0j-xjns-3q',
                  uuid: 'ntjf-2dxf-5o0j-xjns-3q',
                  data: {
                    id: '1',
                    type: 'dynamicDataSource'
                  },
                  category: 'schema',
                  datasourceName: 'SampleTravelDataDerby'
                },
                {
                  name: 'SYSCAT',
                  children: [],
                  keyPath: 'rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/2bli-i53o-uvzf-m5ea-d1',
                  key: '2bli-i53o-uvzf-m5ea-d1',
                  uuid: '2bli-i53o-uvzf-m5ea-d1',
                  data: {
                    id: '1',
                    type: 'dynamicDataSource'
                  },
                  category: 'schema',
                  datasourceName: 'SampleTravelDataDerby'
                },
                {
                  name: 'HIUSER',
                  children: [
                    {
                      id: '4ac5d9f68b58bd7c0d179146e46795be',
                      name: 'dimdate',
                      data: {
                        id: '1',
                        type: 'dynamicDataSource'
                      },
                      keyPath: 'rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/o7bc-2l0g-8pm5-0gmf-26/vw7z-x0s4-e45z-awdr-qm',
                      key: 'vw7z-x0s4-e45z-awdr-qm',
                      alias: 'dimdate',
                      uuid: 'vw7z-x0s4-e45z-awdr-qm',
                      connId: '7tcf9',
                      dataSource: {
                        id: '1',
                        type: 'dynamicDataSource',
                        baseType: 'global.jdbc',
                        catSchemaPredicted: false,
                        sync: false,
                        catalog: '',
                        schema: 'HIUSER',
                        connId: '7tcf9',
                        classifier: 'db.workflow',
                        datasourceName: 'SampleTravelDataDerby',
                        dsKeyPath: 'rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/o7bc-2l0g-8pm5-0gmf-26',
                        driverType: 'Derby',
                        database: 'HIUSER'
                      },
                      dataSourceName: 'SampleTravelDataDerby',
                      category: 'table',
                      nameWithConnId: 'dimdate_7tcf9',
                      database: 'HIUSER',
                      schema: 'HIUSER',
                      selected: true
                    },
                    {
                      id: '4e1fd245f4d13b77be423a43f01d80b2',
                      name: 'employee_details',
                      data: {
                        id: '1',
                        type: 'dynamicDataSource'
                      },
                      keyPath: 'rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/o7bc-2l0g-8pm5-0gmf-26/x14u-u9v1-t49p-o6fy-g3',
                      key: 'x14u-u9v1-t49p-o6fy-g3',
                      alias: 'employee_details',
                      uuid: 'x14u-u9v1-t49p-o6fy-g3',
                      connId: '7tcf9',
                      dataSource: {
                        id: '1',
                        type: 'dynamicDataSource',
                        baseType: 'global.jdbc',
                        catSchemaPredicted: false,
                        sync: false,
                        catalog: '',
                        schema: 'HIUSER',
                        connId: '7tcf9',
                        classifier: 'db.workflow',
                        datasourceName: 'SampleTravelDataDerby',
                        dsKeyPath: 'rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/o7bc-2l0g-8pm5-0gmf-26',
                        driverType: 'Derby',
                        database: 'HIUSER'
                      },
                      dataSourceName: 'SampleTravelDataDerby',
                      category: 'table',
                      nameWithConnId: 'employee_details_7tcf9',
                      database: 'HIUSER',
                      schema: 'HIUSER'
                    },
                    {
                      id: 'be534112989b616b194bc59c2fb25a42',
                      name: 'geo_cordinates',
                      data: {
                        id: '1',
                        type: 'dynamicDataSource'
                      },
                      keyPath: 'rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/o7bc-2l0g-8pm5-0gmf-26/ylhu-q8ql-xu6k-s8fm-jk',
                      key: 'ylhu-q8ql-xu6k-s8fm-jk',
                      alias: 'geo_cordinates',
                      uuid: 'ylhu-q8ql-xu6k-s8fm-jk',
                      connId: '7tcf9',
                      dataSource: {
                        id: '1',
                        type: 'dynamicDataSource',
                        baseType: 'global.jdbc',
                        catSchemaPredicted: false,
                        sync: false,
                        catalog: '',
                        schema: 'HIUSER',
                        connId: '7tcf9',
                        classifier: 'db.workflow',
                        datasourceName: 'SampleTravelDataDerby',
                        dsKeyPath: 'rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/o7bc-2l0g-8pm5-0gmf-26',
                        driverType: 'Derby',
                        database: 'HIUSER'
                      },
                      dataSourceName: 'SampleTravelDataDerby',
                      category: 'table',
                      nameWithConnId: 'geo_cordinates_7tcf9',
                      database: 'HIUSER',
                      schema: 'HIUSER'
                    },
                    {
                      id: '9645c648a1c0dbeec1287aaf1e996db3',
                      name: 'meeting_details',
                      data: {
                        id: '1',
                        type: 'dynamicDataSource'
                      },
                      keyPath: 'rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/o7bc-2l0g-8pm5-0gmf-26/7ybz-mg0p-b2z7-yg3d-2l',
                      key: '7ybz-mg0p-b2z7-yg3d-2l',
                      alias: 'meeting_details',
                      uuid: '7ybz-mg0p-b2z7-yg3d-2l',
                      connId: '7tcf9',
                      dataSource: {
                        id: '1',
                        type: 'dynamicDataSource',
                        baseType: 'global.jdbc',
                        catSchemaPredicted: false,
                        sync: false,
                        catalog: '',
                        schema: 'HIUSER',
                        connId: '7tcf9',
                        classifier: 'db.workflow',
                        datasourceName: 'SampleTravelDataDerby',
                        dsKeyPath: 'rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/o7bc-2l0g-8pm5-0gmf-26',
                        driverType: 'Derby',
                        database: 'HIUSER'
                      },
                      dataSourceName: 'SampleTravelDataDerby',
                      category: 'table',
                      nameWithConnId: 'meeting_details_7tcf9',
                      database: 'HIUSER',
                      schema: 'HIUSER'
                    },
                    {
                      id: '8a28627d07d04ef096d9935f12e0c7e9',
                      name: 'travel_details',
                      data: {
                        id: '1',
                        type: 'dynamicDataSource'
                      },
                      keyPath: 'rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/o7bc-2l0g-8pm5-0gmf-26/3cla-56dy-vw29-jupc-or',
                      key: '3cla-56dy-vw29-jupc-or',
                      alias: 'travel_details',
                      uuid: '3cla-56dy-vw29-jupc-or',
                      connId: '7tcf9',
                      dataSource: {
                        id: '1',
                        type: 'dynamicDataSource',
                        baseType: 'global.jdbc',
                        catSchemaPredicted: false,
                        sync: false,
                        catalog: '',
                        schema: 'HIUSER',
                        connId: '7tcf9',
                        classifier: 'db.workflow',
                        datasourceName: 'SampleTravelDataDerby',
                        dsKeyPath: 'rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/o7bc-2l0g-8pm5-0gmf-26',
                        driverType: 'Derby',
                        database: 'HIUSER'
                      },
                      dataSourceName: 'SampleTravelDataDerby',
                      category: 'table',
                      nameWithConnId: 'travel_details_7tcf9',
                      database: 'HIUSER',
                      schema: 'HIUSER'
                    }
                  ],
                  keyPath: 'rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/o7bc-2l0g-8pm5-0gmf-26',
                  key: 'o7bc-2l0g-8pm5-0gmf-26',
                  uuid: 'o7bc-2l0g-8pm5-0gmf-26',
                  data: {
                    id: '1',
                    type: 'dynamicDataSource'
                  },
                  category: 'schema',
                  datasourceName: 'SampleTravelDataDerby',
                  fetched: true
                },
                {
                  name: 'SYSCS_DIAG',
                  children: [],
                  keyPath: 'rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/uerd-tei3-rx2d-29tv-gh',
                  key: 'uerd-tei3-rx2d-29tv-gh',
                  uuid: 'uerd-tei3-rx2d-29tv-gh',
                  data: {
                    id: '1',
                    type: 'dynamicDataSource'
                  },
                  category: 'schema',
                  datasourceName: 'SampleTravelDataDerby'
                },
                {
                  name: 'SYSCS_UTIL',
                  children: [],
                  keyPath: 'rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/hk53-fil6-geya-9dgh-we',
                  key: 'hk53-fil6-geya-9dgh-we',
                  uuid: 'hk53-fil6-geya-9dgh-we',
                  data: {
                    id: '1',
                    type: 'dynamicDataSource'
                  },
                  category: 'schema',
                  datasourceName: 'SampleTravelDataDerby'
                },
                {
                  name: 'SYSIBM',
                  children: [],
                  keyPath: 'rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/lsvp-tlks-iizc-6ose-3y',
                  key: 'lsvp-tlks-iizc-6ose-3y',
                  uuid: 'lsvp-tlks-iizc-6ose-3y',
                  data: {
                    id: '1',
                    type: 'dynamicDataSource'
                  },
                  category: 'schema',
                  datasourceName: 'SampleTravelDataDerby'
                },
                {
                  name: 'APP',
                  children: [],
                  keyPath: 'rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/6lec-zfsz-ldd4-ca8h-3k',
                  key: '6lec-zfsz-ldd4-ca8h-3k',
                  uuid: '6lec-zfsz-ldd4-ca8h-3k',
                  data: {
                    id: '1',
                    type: 'dynamicDataSource'
                  },
                  category: 'schema',
                  datasourceName: 'SampleTravelDataDerby'
                },
                {
                  name: 'NULLID',
                  children: [],
                  keyPath: 'rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/7vl0-zo1r-hoxc-32ad-zc',
                  key: '7vl0-zo1r-hoxc-32ad-zc',
                  uuid: '7vl0-zo1r-hoxc-32ad-zc',
                  data: {
                    id: '1',
                    type: 'dynamicDataSource'
                  },
                  category: 'schema',
                  datasourceName: 'SampleTravelDataDerby'
                },
                {
                  name: 'SYSPROC',
                  children: [],
                  keyPath: 'rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/afvo-ufqi-0ch3-sajx-3z',
                  key: 'afvo-ufqi-0ch3-sajx-3z',
                  uuid: 'afvo-ufqi-0ch3-sajx-3z',
                  data: {
                    id: '1',
                    type: 'dynamicDataSource'
                  },
                  category: 'schema',
                  datasourceName: 'SampleTravelDataDerby'
                },
                {
                  name: 'SYS',
                  children: [],
                  keyPath: 'rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/rb1x-5sn8-4shx-lp8b-6m',
                  key: 'rb1x-5sn8-4shx-lp8b-6m',
                  uuid: 'rb1x-5sn8-4shx-lp8b-6m',
                  data: {
                    id: '1',
                    type: 'dynamicDataSource'
                  },
                  category: 'schema',
                  datasourceName: 'SampleTravelDataDerby'
                },
                {
                  name: 'SYSSTAT',
                  children: [],
                  keyPath: 'rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp/gge3-0xhc-0mpe-h4j6-aa',
                  key: 'gge3-0xhc-0mpe-h4j6-aa',
                  uuid: 'gge3-0xhc-0mpe-h4j6-aa',
                  data: {
                    id: '1',
                    type: 'dynamicDataSource'
                  },
                  category: 'schema',
                  datasourceName: 'SampleTravelDataDerby'
                }
              ],
              driverType: 'Derby',
              keyPath: 'rwao-mnv9-1cbr-rrnk-ff/t430-kekk-7bro-ffq9-lp',
              key: 't430-kekk-7bro-ffq9-lp',
              uuid: 't430-kekk-7bro-ffq9-lp',
              fetched: true
            }
          ],
          key: 'rwao-mnv9-1cbr-rrnk-ff',
          uuid: 'rwao-mnv9-1cbr-rrnk-ff',
          keyPath: 'rwao-mnv9-1cbr-rrnk-ff',
          category: 'dsGroup',
          imgUrl: 'images/data_sources/defaut_datasource.png'
        },
        {
          name: 'Mysql',
          children: [
            {
              data: {
                id: '1000',
                type: 'dynamicDataSource'
              },
              dataSourceProvider: 'tomcat',
              type: 'dynamicDataSource',
              permissionLevel: 5,
              driver: 'com.mysql.jdbc.Driver',
              name: 'MySql',
              classifier: 'global',
              dataSourceType: 'Managed DataSource',
              category: 'dataSource',
              children: [],
              driverType: 'Mysql',
              keyPath: 'z8jz-mad2-6out-6r5s-9b/hrk3-jk10-ne3m-tscl-e4',
              key: 'hrk3-jk10-ne3m-tscl-e4',
              uuid: 'hrk3-jk10-ne3m-tscl-e4'
            }
          ],
          key: 'z8jz-mad2-6out-6r5s-9b',
          uuid: 'z8jz-mad2-6out-6r5s-9b',
          keyPath: 'z8jz-mad2-6out-6r5s-9b',
          category: 'dsGroup',
          imgUrl: 'images/data_sources/defaut_datasource.png'
        },
        {
          name: 'Sqlite',
          children: [
            {
              data: {
                id: '1001',
                type: 'dynamicDataSource'
              },
              dataSourceProvider: 'tomcat',
              type: 'dynamicDataSource',
              permissionLevel: 5,
              driver: 'org.sqlite.JDBC',
              name: 'SqlLite',
              classifier: 'global',
              dataSourceType: 'Managed DataSource',
              category: 'dataSource',
              children: [],
              driverType: 'Sqlite',
              keyPath: 'c3yf-quir-8ucm-54iz-q3/ct0o-2aq7-2d9p-hird-jl',
              key: 'ct0o-2aq7-2d9p-hird-jl',
              uuid: 'ct0o-2aq7-2d9p-hird-jl'
            }
          ],
          key: 'c3yf-quir-8ucm-54iz-q3',
          uuid: 'c3yf-quir-8ucm-54iz-q3',
          keyPath: 'c3yf-quir-8ucm-54iz-q3',
          category: 'dsGroup',
          imgUrl: 'images/data_sources/defaut_datasource.png'
        }
      ]
    }
  }