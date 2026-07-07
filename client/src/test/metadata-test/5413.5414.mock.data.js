export const mockStore = {
    "dataFetchedFor": {
        "getDatasource": true,
        "joins": false,
        "viewSessionVariables": true,
        "listDataSources": true,
        "w1iz-gwgj-kzc1-nd4s-kw": false,
        "qp7z-ul3m-5ksx-4t1j-w9": false
    },
    "loadingStatus": {
        "getDatasource": true,
        "listDataSources": true,
        "w1iz-gwgj-kzc1-nd4s-kw": false,
        "qp7z-ul3m-5ksx-4t1j-w9": false
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
                            "keyPath": "itmq-8qq1-oqlf-9gyl-5f/w1iz-gwgj-kzc1-nd4s-kw/lh4f-p6nq-qs7p-xkn6-4i",
                            "key": "lh4f-p6nq-qs7p-xkn6-4i",
                            "uuid": "lh4f-p6nq-qs7p-xkn6-4i",
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
                            "keyPath": "itmq-8qq1-oqlf-9gyl-5f/w1iz-gwgj-kzc1-nd4s-kw/hhn7-51a4-deuo-c5oy-7p",
                            "key": "hhn7-51a4-deuo-c5oy-7p",
                            "uuid": "hhn7-51a4-deuo-c5oy-7p",
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
                            "keyPath": "itmq-8qq1-oqlf-9gyl-5f/w1iz-gwgj-kzc1-nd4s-kw/gvw7-l27c-z2bk-d45b-ub",
                            "key": "gvw7-l27c-z2bk-d45b-ub",
                            "uuid": "gvw7-l27c-z2bk-d45b-ub",
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
                                    "keyPath": "itmq-8qq1-oqlf-9gyl-5f/w1iz-gwgj-kzc1-nd4s-kw/qp7z-ul3m-5ksx-4t1j-w9/u8d0-qw5g-fv52-8ted-9d",
                                    "key": "u8d0-qw5g-fv52-8ted-9d",
                                    "alias": "dimdate",
                                    "uuid": "u8d0-qw5g-fv52-8ted-9d",
                                    "connId": "ei14t",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "ei14t",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "itmq-8qq1-oqlf-9gyl-5f/w1iz-gwgj-kzc1-nd4s-kw/qp7z-ul3m-5ksx-4t1j-w9",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "dimdate_ei14t",
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
                                    "keyPath": "itmq-8qq1-oqlf-9gyl-5f/w1iz-gwgj-kzc1-nd4s-kw/qp7z-ul3m-5ksx-4t1j-w9/e1ja-y727-kskg-b0ta-j7",
                                    "key": "e1ja-y727-kskg-b0ta-j7",
                                    "alias": "employee_details",
                                    "uuid": "e1ja-y727-kskg-b0ta-j7",
                                    "connId": "ei14t",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "ei14t",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "itmq-8qq1-oqlf-9gyl-5f/w1iz-gwgj-kzc1-nd4s-kw/qp7z-ul3m-5ksx-4t1j-w9",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "employee_details_ei14t",
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
                                    "keyPath": "itmq-8qq1-oqlf-9gyl-5f/w1iz-gwgj-kzc1-nd4s-kw/qp7z-ul3m-5ksx-4t1j-w9/kk6e-664r-e9s0-5zmk-y3",
                                    "key": "kk6e-664r-e9s0-5zmk-y3",
                                    "alias": "geo_cordinates",
                                    "uuid": "kk6e-664r-e9s0-5zmk-y3",
                                    "connId": "ei14t",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "ei14t",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "itmq-8qq1-oqlf-9gyl-5f/w1iz-gwgj-kzc1-nd4s-kw/qp7z-ul3m-5ksx-4t1j-w9",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "geo_cordinates_ei14t",
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
                                    "keyPath": "itmq-8qq1-oqlf-9gyl-5f/w1iz-gwgj-kzc1-nd4s-kw/qp7z-ul3m-5ksx-4t1j-w9/09xl-7f7j-18sl-fson-zh",
                                    "key": "09xl-7f7j-18sl-fson-zh",
                                    "alias": "meeting_details",
                                    "uuid": "09xl-7f7j-18sl-fson-zh",
                                    "connId": "ei14t",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "ei14t",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "itmq-8qq1-oqlf-9gyl-5f/w1iz-gwgj-kzc1-nd4s-kw/qp7z-ul3m-5ksx-4t1j-w9",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "meeting_details_ei14t",
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
                                    "keyPath": "itmq-8qq1-oqlf-9gyl-5f/w1iz-gwgj-kzc1-nd4s-kw/qp7z-ul3m-5ksx-4t1j-w9/d5ue-ysas-724l-zwu5-4y",
                                    "key": "d5ue-ysas-724l-zwu5-4y",
                                    "alias": "travel_details",
                                    "uuid": "d5ue-ysas-724l-zwu5-4y",
                                    "connId": "ei14t",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "ei14t",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "itmq-8qq1-oqlf-9gyl-5f/w1iz-gwgj-kzc1-nd4s-kw/qp7z-ul3m-5ksx-4t1j-w9",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "travel_details_ei14t",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                }
                            ],
                            "keyPath": "itmq-8qq1-oqlf-9gyl-5f/w1iz-gwgj-kzc1-nd4s-kw/qp7z-ul3m-5ksx-4t1j-w9",
                            "key": "qp7z-ul3m-5ksx-4t1j-w9",
                            "uuid": "qp7z-ul3m-5ksx-4t1j-w9",
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
                            "keyPath": "itmq-8qq1-oqlf-9gyl-5f/w1iz-gwgj-kzc1-nd4s-kw/w37r-5wgl-fd1j-dd1k-4c",
                            "key": "w37r-5wgl-fd1j-dd1k-4c",
                            "uuid": "w37r-5wgl-fd1j-dd1k-4c",
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
                            "keyPath": "itmq-8qq1-oqlf-9gyl-5f/w1iz-gwgj-kzc1-nd4s-kw/o2f6-xpzt-wrpz-sh46-5t",
                            "key": "o2f6-xpzt-wrpz-sh46-5t",
                            "uuid": "o2f6-xpzt-wrpz-sh46-5t",
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
                            "keyPath": "itmq-8qq1-oqlf-9gyl-5f/w1iz-gwgj-kzc1-nd4s-kw/xx0h-ixh9-xvt0-3kp3-zs",
                            "key": "xx0h-ixh9-xvt0-3kp3-zs",
                            "uuid": "xx0h-ixh9-xvt0-3kp3-zs",
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
                            "keyPath": "itmq-8qq1-oqlf-9gyl-5f/w1iz-gwgj-kzc1-nd4s-kw/ump0-kjfi-xtx7-obsj-6t",
                            "key": "ump0-kjfi-xtx7-obsj-6t",
                            "uuid": "ump0-kjfi-xtx7-obsj-6t",
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
                            "keyPath": "itmq-8qq1-oqlf-9gyl-5f/w1iz-gwgj-kzc1-nd4s-kw/9ba1-1224-9yya-tn22-09",
                            "key": "9ba1-1224-9yya-tn22-09",
                            "uuid": "9ba1-1224-9yya-tn22-09",
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
                            "keyPath": "itmq-8qq1-oqlf-9gyl-5f/w1iz-gwgj-kzc1-nd4s-kw/g5od-2eo9-ucay-mf5h-ig",
                            "key": "g5od-2eo9-ucay-mf5h-ig",
                            "uuid": "g5od-2eo9-ucay-mf5h-ig",
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
                            "keyPath": "itmq-8qq1-oqlf-9gyl-5f/w1iz-gwgj-kzc1-nd4s-kw/mfir-14sd-nwom-22ik-54",
                            "key": "mfir-14sd-nwom-22ik-54",
                            "uuid": "mfir-14sd-nwom-22ik-54",
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
                            "keyPath": "itmq-8qq1-oqlf-9gyl-5f/w1iz-gwgj-kzc1-nd4s-kw/i7zr-dvvf-1vjw-5cpt-ye",
                            "key": "i7zr-dvvf-1vjw-5cpt-ye",
                            "uuid": "i7zr-dvvf-1vjw-5cpt-ye",
                            "data": {
                                "id": "1",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "SampleTravelDataDerby"
                        }
                    ],
                    "driverType": "Derby",
                    "keyPath": "itmq-8qq1-oqlf-9gyl-5f/w1iz-gwgj-kzc1-nd4s-kw",
                    "key": "w1iz-gwgj-kzc1-nd4s-kw",
                    "uuid": "w1iz-gwgj-kzc1-nd4s-kw",
                    "fetched": true
                }
            ],
            "key": "itmq-8qq1-oqlf-9gyl-5f",
            "uuid": "itmq-8qq1-oqlf-9gyl-5f",
            "keyPath": "itmq-8qq1-oqlf-9gyl-5f",
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
                    "keyPath": "vag1-teet-ea1y-fzza-3a/rnd8-13n6-1y1z-xtki-9r",
                    "key": "rnd8-13n6-1y1z-xtki-9r",
                    "uuid": "rnd8-13n6-1y1z-xtki-9r"
                }
            ],
            "key": "vag1-teet-ea1y-fzza-3a",
            "uuid": "vag1-teet-ea1y-fzza-3a",
            "keyPath": "vag1-teet-ea1y-fzza-3a",
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
                    "keyPath": "w54i-269u-17r1-xum8-91/dn5o-8xti-layl-7lda-km",
                    "key": "dn5o-8xti-layl-7lda-km",
                    "uuid": "dn5o-8xti-layl-7lda-km"
                }
            ],
            "key": "w54i-269u-17r1-xum8-91",
            "uuid": "w54i-269u-17r1-xum8-91",
            "keyPath": "w54i-269u-17r1-xum8-91",
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
                "dsUUID": "w1iz-gwgj-kzc1-nd4s-kw",
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
                        "connId": "ei14t",
                        "classifier": "db.workflow",
                        "datasourceName": "SampleTravelDataDerby",
                        "dsKeyPath": "itmq-8qq1-oqlf-9gyl-5f/w1iz-gwgj-kzc1-nd4s-kw/qp7z-ul3m-5ksx-4t1j-w9",
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
            "connId": "ei14t",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelDataDerby",
            "dsKeyPath": "itmq-8qq1-oqlf-9gyl-5f/w1iz-gwgj-kzc1-nd4s-kw/qp7z-ul3m-5ksx-4t1j-w9",
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
            "keyPath": "itmq-8qq1-oqlf-9gyl-5f/w1iz-gwgj-kzc1-nd4s-kw/qp7z-ul3m-5ksx-4t1j-w9/u8d0-qw5g-fv52-8ted-9d",
            "key": "u8d0-qw5g-fv52-8ted-9d",
            "alias": "dimdate",
            "uuid": "u8d0-qw5g-fv52-8ted-9d",
            "connId": "ei14t",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "ei14t",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "itmq-8qq1-oqlf-9gyl-5f/w1iz-gwgj-kzc1-nd4s-kw/qp7z-ul3m-5ksx-4t1j-w9",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "dimdate_ei14t",
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
            "keyPath": "itmq-8qq1-oqlf-9gyl-5f/w1iz-gwgj-kzc1-nd4s-kw/qp7z-ul3m-5ksx-4t1j-w9/e1ja-y727-kskg-b0ta-j7",
            "key": "e1ja-y727-kskg-b0ta-j7",
            "alias": "employee_details",
            "uuid": "e1ja-y727-kskg-b0ta-j7",
            "connId": "ei14t",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "ei14t",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "itmq-8qq1-oqlf-9gyl-5f/w1iz-gwgj-kzc1-nd4s-kw/qp7z-ul3m-5ksx-4t1j-w9",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "employee_details_ei14t",
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
            "keyPath": "itmq-8qq1-oqlf-9gyl-5f/w1iz-gwgj-kzc1-nd4s-kw/qp7z-ul3m-5ksx-4t1j-w9/kk6e-664r-e9s0-5zmk-y3",
            "key": "kk6e-664r-e9s0-5zmk-y3",
            "alias": "geo_cordinates",
            "uuid": "kk6e-664r-e9s0-5zmk-y3",
            "connId": "ei14t",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "ei14t",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "itmq-8qq1-oqlf-9gyl-5f/w1iz-gwgj-kzc1-nd4s-kw/qp7z-ul3m-5ksx-4t1j-w9",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "geo_cordinates_ei14t",
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
            "keyPath": "itmq-8qq1-oqlf-9gyl-5f/w1iz-gwgj-kzc1-nd4s-kw/qp7z-ul3m-5ksx-4t1j-w9/09xl-7f7j-18sl-fson-zh",
            "key": "09xl-7f7j-18sl-fson-zh",
            "alias": "meeting_details",
            "uuid": "09xl-7f7j-18sl-fson-zh",
            "connId": "ei14t",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "ei14t",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "itmq-8qq1-oqlf-9gyl-5f/w1iz-gwgj-kzc1-nd4s-kw/qp7z-ul3m-5ksx-4t1j-w9",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "meeting_details_ei14t",
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
            "keyPath": "itmq-8qq1-oqlf-9gyl-5f/w1iz-gwgj-kzc1-nd4s-kw/qp7z-ul3m-5ksx-4t1j-w9/d5ue-ysas-724l-zwu5-4y",
            "key": "d5ue-ysas-724l-zwu5-4y",
            "alias": "travel_details",
            "uuid": "d5ue-ysas-724l-zwu5-4y",
            "connId": "ei14t",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "ei14t",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "itmq-8qq1-oqlf-9gyl-5f/w1iz-gwgj-kzc1-nd4s-kw/qp7z-ul3m-5ksx-4t1j-w9",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "travel_details_ei14t",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "travel_details"
        }
    },
    "views": [
        {
            "name": "View 1",
            "alias": "View 1",
            "columns": {},
            "key": "ya1w-etz4-7s30-rau2-xa",
            "uuid": "ya1w-etz4-7s30-rau2-xa",
            "query": "",
            "queryType": "conditionIf",
            "labels": [],
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "ei14t",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "itmq-8qq1-oqlf-9gyl-5f/w1iz-gwgj-kzc1-nd4s-kw/qp7z-ul3m-5ksx-4t1j-w9",
                "driverType": "Derby",
                "database": "HIUSER"
            }
        }
    ],
    "activeView": "ya1w-etz4-7s30-rau2-xa",
    "categories": {
        "itmq-8qq1-oqlf-9gyl-5f": {
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
        "vag1-teet-ea1y-fzza-3a": {
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
        "w54i-269u-17r1-xum8-91": {
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
            "connId": "ei14t",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelDataDerby",
            "dsKeyPath": "itmq-8qq1-oqlf-9gyl-5f/w1iz-gwgj-kzc1-nd4s-kw/qp7z-ul3m-5ksx-4t1j-w9",
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
    "joins": [],
    "mode": "create",
    "allTablesKeys": [
        "kk6e-664r-e9s0-5zmk-y3",
        "09xl-7f7j-18sl-fson-zh",
        "e1ja-y727-kskg-b0ta-j7",
        "u8d0-qw5g-fv52-8ted-9d",
        "d5ue-ysas-724l-zwu5-4y"
    ],
    "selectedTableKeys": [
        "u8d0-qw5g-fv52-8ted-9d",
        "e1ja-y727-kskg-b0ta-j7",
        "kk6e-664r-e9s0-5zmk-y3",
        "09xl-7f7j-18sl-fson-zh",
        "d5ue-ysas-724l-zwu5-4y"
    ],
    "metadataName": "Metadata_1",
    "activeDataSource": false,
    "metadataToEdit": false,
    "isSavingInProgress": false,
    "editViewsTempData": {
        "ya1w-etz4-7s30-rau2-xa": {
            "name": "View 1",
            "alias": "View 1",
            "columns": {},
            "key": "ya1w-etz4-7s30-rau2-xa",
            "uuid": "ya1w-etz4-7s30-rau2-xa",
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
                "connId": "ei14t",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "itmq-8qq1-oqlf-9gyl-5f/w1iz-gwgj-kzc1-nd4s-kw/qp7z-ul3m-5ksx-4t1j-w9",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "error": false,
            "validate": true,
            "processedQuery": "select * from (select * from \"dimdate\") foo fetch first 1 rows only"
        }
    },
    "inititalStateFromJest": false,
    "timeStamp": 1662111202160,
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