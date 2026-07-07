export const store5609 = {
    "dataFetchedFor": {
        "getDatasource": true,
        "joins": false,
        "viewSessionVariables": false,
        "listDataSources": true,
        "zoqc-hceq-3xws-96cf-a5": false,
        "7238-jr60-b858-fz3s-qb": false,
        "b1is-i92p-h54s-628a-au": false,
        "s9na-lr0g-6uj5-r2yg-jl": false
    },
    "loadingStatus": {
        "getDatasource": true,
        "listDataSources": true,
        "zoqc-hceq-3xws-96cf-a5": false,
        "7238-jr60-b858-fz3s-qb": false,
        "b1is-i92p-h54s-628a-au": false,
        "s9na-lr0g-6uj5-r2yg-jl": false
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
            "data": {
                "dir": "Gagan",
                "driverName": null,
                "type": "sql.jdbc.groovy.managed",
                "id": 1,
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
                            "keyPath": "ztgp-oj2o-1ujv-uz23-ws/zoqc-hceq-3xws-96cf-a5/itzy-8bvk-4duv-cpm1-ul",
                            "key": "itzy-8bvk-4duv-cpm1-ul",
                            "uuid": "itzy-8bvk-4duv-cpm1-ul",
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
                            "keyPath": "ztgp-oj2o-1ujv-uz23-ws/zoqc-hceq-3xws-96cf-a5/tik2-dfvg-nw2d-erzz-06",
                            "key": "tik2-dfvg-nw2d-erzz-06",
                            "uuid": "tik2-dfvg-nw2d-erzz-06",
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
                            "keyPath": "ztgp-oj2o-1ujv-uz23-ws/zoqc-hceq-3xws-96cf-a5/4rrg-llgc-meyh-9l2r-sp",
                            "key": "4rrg-llgc-meyh-9l2r-sp",
                            "uuid": "4rrg-llgc-meyh-9l2r-sp",
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
                                    "keyPath": "ztgp-oj2o-1ujv-uz23-ws/zoqc-hceq-3xws-96cf-a5/7238-jr60-b858-fz3s-qb/t1c3-qv96-qutm-j27a-pk",
                                    "key": "t1c3-qv96-qutm-j27a-pk",
                                    "alias": "dimdate",
                                    "uuid": "t1c3-qv96-qutm-j27a-pk",
                                    "connId": "0uxl9",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "0uxl9",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "ztgp-oj2o-1ujv-uz23-ws/zoqc-hceq-3xws-96cf-a5/7238-jr60-b858-fz3s-qb",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "dimdate_0uxl9",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": false
                                },
                                {
                                    "id": "4e1fd245f4d13b77be423a43f01d80b2",
                                    "name": "employee_details",
                                    "data": {
                                        "id": "1",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "ztgp-oj2o-1ujv-uz23-ws/zoqc-hceq-3xws-96cf-a5/7238-jr60-b858-fz3s-qb/0yrs-tdxv-ztkg-hnh3-tq",
                                    "key": "0yrs-tdxv-ztkg-hnh3-tq",
                                    "alias": "employee_details",
                                    "uuid": "0yrs-tdxv-ztkg-hnh3-tq",
                                    "connId": "0uxl9",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "0uxl9",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "ztgp-oj2o-1ujv-uz23-ws/zoqc-hceq-3xws-96cf-a5/7238-jr60-b858-fz3s-qb",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "employee_details_0uxl9",
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
                                    "keyPath": "ztgp-oj2o-1ujv-uz23-ws/zoqc-hceq-3xws-96cf-a5/7238-jr60-b858-fz3s-qb/wpj4-zpe3-k0y7-yclz-qp",
                                    "key": "wpj4-zpe3-k0y7-yclz-qp",
                                    "alias": "geo_cordinates",
                                    "uuid": "wpj4-zpe3-k0y7-yclz-qp",
                                    "connId": "0uxl9",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "0uxl9",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "ztgp-oj2o-1ujv-uz23-ws/zoqc-hceq-3xws-96cf-a5/7238-jr60-b858-fz3s-qb",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "geo_cordinates_0uxl9",
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
                                    "keyPath": "ztgp-oj2o-1ujv-uz23-ws/zoqc-hceq-3xws-96cf-a5/7238-jr60-b858-fz3s-qb/jgt7-133r-t7c2-onwp-qw",
                                    "key": "jgt7-133r-t7c2-onwp-qw",
                                    "alias": "meeting_details",
                                    "uuid": "jgt7-133r-t7c2-onwp-qw",
                                    "connId": "0uxl9",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "0uxl9",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "ztgp-oj2o-1ujv-uz23-ws/zoqc-hceq-3xws-96cf-a5/7238-jr60-b858-fz3s-qb",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "meeting_details_0uxl9",
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
                                    "keyPath": "ztgp-oj2o-1ujv-uz23-ws/zoqc-hceq-3xws-96cf-a5/7238-jr60-b858-fz3s-qb/sdfg-118e-keqe-tx60-vs",
                                    "key": "sdfg-118e-keqe-tx60-vs",
                                    "alias": "travel_details",
                                    "uuid": "sdfg-118e-keqe-tx60-vs",
                                    "connId": "0uxl9",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "0uxl9",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "ztgp-oj2o-1ujv-uz23-ws/zoqc-hceq-3xws-96cf-a5/7238-jr60-b858-fz3s-qb",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "travel_details_0uxl9",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": false
                                }
                            ],
                            "keyPath": "ztgp-oj2o-1ujv-uz23-ws/zoqc-hceq-3xws-96cf-a5/7238-jr60-b858-fz3s-qb",
                            "key": "7238-jr60-b858-fz3s-qb",
                            "uuid": "7238-jr60-b858-fz3s-qb",
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
                            "keyPath": "ztgp-oj2o-1ujv-uz23-ws/zoqc-hceq-3xws-96cf-a5/2umv-dzk6-t8n7-iz6o-b0",
                            "key": "2umv-dzk6-t8n7-iz6o-b0",
                            "uuid": "2umv-dzk6-t8n7-iz6o-b0",
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
                            "keyPath": "ztgp-oj2o-1ujv-uz23-ws/zoqc-hceq-3xws-96cf-a5/zv4a-oxqm-0qpd-7sap-tt",
                            "key": "zv4a-oxqm-0qpd-7sap-tt",
                            "uuid": "zv4a-oxqm-0qpd-7sap-tt",
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
                            "keyPath": "ztgp-oj2o-1ujv-uz23-ws/zoqc-hceq-3xws-96cf-a5/4ssi-f5e8-4g81-n4ne-z5",
                            "key": "4ssi-f5e8-4g81-n4ne-z5",
                            "uuid": "4ssi-f5e8-4g81-n4ne-z5",
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
                            "keyPath": "ztgp-oj2o-1ujv-uz23-ws/zoqc-hceq-3xws-96cf-a5/zfuk-tfap-4xb1-qi4l-yd",
                            "key": "zfuk-tfap-4xb1-qi4l-yd",
                            "uuid": "zfuk-tfap-4xb1-qi4l-yd",
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
                            "keyPath": "ztgp-oj2o-1ujv-uz23-ws/zoqc-hceq-3xws-96cf-a5/5d81-3f7q-cgcw-iokt-oy",
                            "key": "5d81-3f7q-cgcw-iokt-oy",
                            "uuid": "5d81-3f7q-cgcw-iokt-oy",
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
                            "keyPath": "ztgp-oj2o-1ujv-uz23-ws/zoqc-hceq-3xws-96cf-a5/lhvd-l7xd-dxyy-ihv1-36",
                            "key": "lhvd-l7xd-dxyy-ihv1-36",
                            "uuid": "lhvd-l7xd-dxyy-ihv1-36",
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
                            "keyPath": "ztgp-oj2o-1ujv-uz23-ws/zoqc-hceq-3xws-96cf-a5/9doq-4k21-8skb-ld50-rn",
                            "key": "9doq-4k21-8skb-ld50-rn",
                            "uuid": "9doq-4k21-8skb-ld50-rn",
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
                            "keyPath": "ztgp-oj2o-1ujv-uz23-ws/zoqc-hceq-3xws-96cf-a5/gpb8-4sdg-g35v-rkb5-xy",
                            "key": "gpb8-4sdg-g35v-rkb5-xy",
                            "uuid": "gpb8-4sdg-g35v-rkb5-xy",
                            "data": {
                                "id": "1",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "SampleTravelDataDerby"
                        }
                    ],
                    "driverType": "Derby",
                    "keyPath": "ztgp-oj2o-1ujv-uz23-ws/zoqc-hceq-3xws-96cf-a5",
                    "key": "zoqc-hceq-3xws-96cf-a5",
                    "uuid": "zoqc-hceq-3xws-96cf-a5",
                    "fetched": true
                }
            ],
            "key": "ztgp-oj2o-1ujv-uz23-ws",
            "uuid": "ztgp-oj2o-1ujv-uz23-ws",
            "keyPath": "ztgp-oj2o-1ujv-uz23-ws",
            "category": "dsGroup",
            "imgUrl": "images/data_sources/defaut_datasource.png"
        },
        {
            "name": "DynamicSwitch",
            "children": [
                {
                    "permissionLevel": 5,
                    "driver": "dynamicSwitch",
                    "data": {
                        "dir": "Gagan",
                        "driverName": null,
                        "type": "sql.jdbc.groovy.managed",
                        "id": 1,
                        "userName": null,
                        "password": null,
                        "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                        "jdbcUrl": null,
                        "driver": "dynamicSwitch"
                    },
                    "name": "groovy man",
                    "classifier": "efwd",
                    "type": "sql.jdbc.groovy.managed",
                    "dataSourceType": "Plain Groovy Managed DataSource",
                    "category": "dataSource",
                    "children": [
                        {
                            "name": "SQLJ",
                            "children": [],
                            "keyPath": "siij-tk1m-opwl-ni1t-8s/b1is-i92p-h54s-628a-au/rcrf-34pa-xzgi-l6zh-bn",
                            "key": "rcrf-34pa-xzgi-l6zh-bn",
                            "uuid": "rcrf-34pa-xzgi-l6zh-bn",
                            "data": {
                                "dir": "Gagan",
                                "driverName": null,
                                "type": "sql.jdbc.groovy.managed",
                                "id": 1,
                                "userName": null,
                                "password": null,
                                "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                                "jdbcUrl": null,
                                "driver": "dynamicSwitch"
                            },
                            "category": "schema",
                            "datasourceName": "groovy man"
                        },
                        {
                            "name": "SYSFUN",
                            "children": [],
                            "keyPath": "siij-tk1m-opwl-ni1t-8s/b1is-i92p-h54s-628a-au/lwy7-3hek-diql-414g-q7",
                            "key": "lwy7-3hek-diql-414g-q7",
                            "uuid": "lwy7-3hek-diql-414g-q7",
                            "data": {
                                "dir": "Gagan",
                                "driverName": null,
                                "type": "sql.jdbc.groovy.managed",
                                "id": 1,
                                "userName": null,
                                "password": null,
                                "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                                "jdbcUrl": null,
                                "driver": "dynamicSwitch"
                            },
                            "category": "schema",
                            "datasourceName": "groovy man"
                        },
                        {
                            "name": "SYSCAT",
                            "children": [],
                            "keyPath": "siij-tk1m-opwl-ni1t-8s/b1is-i92p-h54s-628a-au/rlb6-x00e-31u6-b9su-g9",
                            "key": "rlb6-x00e-31u6-b9su-g9",
                            "uuid": "rlb6-x00e-31u6-b9su-g9",
                            "data": {
                                "dir": "Gagan",
                                "driverName": null,
                                "type": "sql.jdbc.groovy.managed",
                                "id": 1,
                                "userName": null,
                                "password": null,
                                "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                                "jdbcUrl": null,
                                "driver": "dynamicSwitch"
                            },
                            "category": "schema",
                            "datasourceName": "groovy man"
                        },
                        {
                            "name": "HIUSER",
                            "children": [
                                {
                                    "id": "4ac5d9f68b58bd7c0d179146e46795be",
                                    "name": "dimdate",
                                    "data": {
                                        "dir": "Gagan",
                                        "driverName": null,
                                        "type": "sql.jdbc.groovy.managed",
                                        "id": 1,
                                        "userName": null,
                                        "password": null,
                                        "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                                        "jdbcUrl": null,
                                        "driver": "dynamicSwitch"
                                    },
                                    "keyPath": "siij-tk1m-opwl-ni1t-8s/b1is-i92p-h54s-628a-au/s9na-lr0g-6uj5-r2yg-jl/figo-51qy-4o12-58bt-lg",
                                    "key": "figo-51qy-4o12-58bt-lg",
                                    "alias": "dimdate",
                                    "uuid": "figo-51qy-4o12-58bt-lg",
                                    "connId": "tgbdq",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "sql.jdbc.groovy.managed",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "dir": "Gagan",
                                        "connId": "tgbdq",
                                        "classifier": "db.workflow",
                                        "datasourceName": "groovy man",
                                        "dsKeyPath": "siij-tk1m-opwl-ni1t-8s/b1is-i92p-h54s-628a-au/s9na-lr0g-6uj5-r2yg-jl",
                                        "driverType": "DynamicSwitch",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "groovy man",
                                    "category": "table",
                                    "nameWithConnId": "dimdate_tgbdq",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "4e1fd245f4d13b77be423a43f01d80b2",
                                    "name": "employee_details",
                                    "data": {
                                        "dir": "Gagan",
                                        "driverName": null,
                                        "type": "sql.jdbc.groovy.managed",
                                        "id": 1,
                                        "userName": null,
                                        "password": null,
                                        "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                                        "jdbcUrl": null,
                                        "driver": "dynamicSwitch"
                                    },
                                    "keyPath": "siij-tk1m-opwl-ni1t-8s/b1is-i92p-h54s-628a-au/s9na-lr0g-6uj5-r2yg-jl/zk04-lhr2-fio3-nvxh-0y",
                                    "key": "zk04-lhr2-fio3-nvxh-0y",
                                    "alias": "employee_details",
                                    "uuid": "zk04-lhr2-fio3-nvxh-0y",
                                    "connId": "tgbdq",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "sql.jdbc.groovy.managed",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "dir": "Gagan",
                                        "connId": "tgbdq",
                                        "classifier": "db.workflow",
                                        "datasourceName": "groovy man",
                                        "dsKeyPath": "siij-tk1m-opwl-ni1t-8s/b1is-i92p-h54s-628a-au/s9na-lr0g-6uj5-r2yg-jl",
                                        "driverType": "DynamicSwitch",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "groovy man",
                                    "category": "table",
                                    "nameWithConnId": "employee_details_tgbdq",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": false
                                },
                                {
                                    "id": "be534112989b616b194bc59c2fb25a42",
                                    "name": "geo_cordinates",
                                    "data": {
                                        "dir": "Gagan",
                                        "driverName": null,
                                        "type": "sql.jdbc.groovy.managed",
                                        "id": 1,
                                        "userName": null,
                                        "password": null,
                                        "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                                        "jdbcUrl": null,
                                        "driver": "dynamicSwitch"
                                    },
                                    "keyPath": "siij-tk1m-opwl-ni1t-8s/b1is-i92p-h54s-628a-au/s9na-lr0g-6uj5-r2yg-jl/w3b8-hrpe-un1o-v6dm-76",
                                    "key": "w3b8-hrpe-un1o-v6dm-76",
                                    "alias": "geo_cordinates",
                                    "uuid": "w3b8-hrpe-un1o-v6dm-76",
                                    "connId": "tgbdq",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "sql.jdbc.groovy.managed",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "dir": "Gagan",
                                        "connId": "tgbdq",
                                        "classifier": "db.workflow",
                                        "datasourceName": "groovy man",
                                        "dsKeyPath": "siij-tk1m-opwl-ni1t-8s/b1is-i92p-h54s-628a-au/s9na-lr0g-6uj5-r2yg-jl",
                                        "driverType": "DynamicSwitch",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "groovy man",
                                    "category": "table",
                                    "nameWithConnId": "geo_cordinates_tgbdq",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": false
                                },
                                {
                                    "id": "9645c648a1c0dbeec1287aaf1e996db3",
                                    "name": "meeting_details",
                                    "data": {
                                        "dir": "Gagan",
                                        "driverName": null,
                                        "type": "sql.jdbc.groovy.managed",
                                        "id": 1,
                                        "userName": null,
                                        "password": null,
                                        "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                                        "jdbcUrl": null,
                                        "driver": "dynamicSwitch"
                                    },
                                    "keyPath": "siij-tk1m-opwl-ni1t-8s/b1is-i92p-h54s-628a-au/s9na-lr0g-6uj5-r2yg-jl/j674-vcdq-zgpi-segt-bc",
                                    "key": "j674-vcdq-zgpi-segt-bc",
                                    "alias": "meeting_details",
                                    "uuid": "j674-vcdq-zgpi-segt-bc",
                                    "connId": "tgbdq",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "sql.jdbc.groovy.managed",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "dir": "Gagan",
                                        "connId": "tgbdq",
                                        "classifier": "db.workflow",
                                        "datasourceName": "groovy man",
                                        "dsKeyPath": "siij-tk1m-opwl-ni1t-8s/b1is-i92p-h54s-628a-au/s9na-lr0g-6uj5-r2yg-jl",
                                        "driverType": "DynamicSwitch",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "groovy man",
                                    "category": "table",
                                    "nameWithConnId": "meeting_details_tgbdq",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": false
                                },
                                {
                                    "id": "8a28627d07d04ef096d9935f12e0c7e9",
                                    "name": "travel_details",
                                    "data": {
                                        "dir": "Gagan",
                                        "driverName": null,
                                        "type": "sql.jdbc.groovy.managed",
                                        "id": 1,
                                        "userName": null,
                                        "password": null,
                                        "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                                        "jdbcUrl": null,
                                        "driver": "dynamicSwitch"
                                    },
                                    "keyPath": "siij-tk1m-opwl-ni1t-8s/b1is-i92p-h54s-628a-au/s9na-lr0g-6uj5-r2yg-jl/9i5w-pawr-3vad-fsi9-im",
                                    "key": "9i5w-pawr-3vad-fsi9-im",
                                    "alias": "travel_details",
                                    "uuid": "9i5w-pawr-3vad-fsi9-im",
                                    "connId": "tgbdq",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "sql.jdbc.groovy.managed",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "dir": "Gagan",
                                        "connId": "tgbdq",
                                        "classifier": "db.workflow",
                                        "datasourceName": "groovy man",
                                        "dsKeyPath": "siij-tk1m-opwl-ni1t-8s/b1is-i92p-h54s-628a-au/s9na-lr0g-6uj5-r2yg-jl",
                                        "driverType": "DynamicSwitch",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "groovy man",
                                    "category": "table",
                                    "nameWithConnId": "travel_details_tgbdq",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": false
                                }
                            ],
                            "keyPath": "siij-tk1m-opwl-ni1t-8s/b1is-i92p-h54s-628a-au/s9na-lr0g-6uj5-r2yg-jl",
                            "key": "s9na-lr0g-6uj5-r2yg-jl",
                            "uuid": "s9na-lr0g-6uj5-r2yg-jl",
                            "data": {
                                "dir": "Gagan",
                                "driverName": null,
                                "type": "sql.jdbc.groovy.managed",
                                "id": 1,
                                "userName": null,
                                "password": null,
                                "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                                "jdbcUrl": null,
                                "driver": "dynamicSwitch"
                            },
                            "category": "schema",
                            "datasourceName": "groovy man",
                            "fetched": true
                        },
                        {
                            "name": "SYSCS_DIAG",
                            "children": [],
                            "keyPath": "siij-tk1m-opwl-ni1t-8s/b1is-i92p-h54s-628a-au/p90y-lauv-ki37-r1i0-pv",
                            "key": "p90y-lauv-ki37-r1i0-pv",
                            "uuid": "p90y-lauv-ki37-r1i0-pv",
                            "data": {
                                "dir": "Gagan",
                                "driverName": null,
                                "type": "sql.jdbc.groovy.managed",
                                "id": 1,
                                "userName": null,
                                "password": null,
                                "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                                "jdbcUrl": null,
                                "driver": "dynamicSwitch"
                            },
                            "category": "schema",
                            "datasourceName": "groovy man"
                        },
                        {
                            "name": "SYSCS_UTIL",
                            "children": [],
                            "keyPath": "siij-tk1m-opwl-ni1t-8s/b1is-i92p-h54s-628a-au/9w6i-amel-eze9-mdks-34",
                            "key": "9w6i-amel-eze9-mdks-34",
                            "uuid": "9w6i-amel-eze9-mdks-34",
                            "data": {
                                "dir": "Gagan",
                                "driverName": null,
                                "type": "sql.jdbc.groovy.managed",
                                "id": 1,
                                "userName": null,
                                "password": null,
                                "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                                "jdbcUrl": null,
                                "driver": "dynamicSwitch"
                            },
                            "category": "schema",
                            "datasourceName": "groovy man"
                        },
                        {
                            "name": "SYSIBM",
                            "children": [],
                            "keyPath": "siij-tk1m-opwl-ni1t-8s/b1is-i92p-h54s-628a-au/gjew-mx3r-c14l-s02r-g0",
                            "key": "gjew-mx3r-c14l-s02r-g0",
                            "uuid": "gjew-mx3r-c14l-s02r-g0",
                            "data": {
                                "dir": "Gagan",
                                "driverName": null,
                                "type": "sql.jdbc.groovy.managed",
                                "id": 1,
                                "userName": null,
                                "password": null,
                                "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                                "jdbcUrl": null,
                                "driver": "dynamicSwitch"
                            },
                            "category": "schema",
                            "datasourceName": "groovy man"
                        },
                        {
                            "name": "APP",
                            "children": [],
                            "keyPath": "siij-tk1m-opwl-ni1t-8s/b1is-i92p-h54s-628a-au/lknt-u58u-sjzt-evkg-br",
                            "key": "lknt-u58u-sjzt-evkg-br",
                            "uuid": "lknt-u58u-sjzt-evkg-br",
                            "data": {
                                "dir": "Gagan",
                                "driverName": null,
                                "type": "sql.jdbc.groovy.managed",
                                "id": 1,
                                "userName": null,
                                "password": null,
                                "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                                "jdbcUrl": null,
                                "driver": "dynamicSwitch"
                            },
                            "category": "schema",
                            "datasourceName": "groovy man"
                        },
                        {
                            "name": "NULLID",
                            "children": [],
                            "keyPath": "siij-tk1m-opwl-ni1t-8s/b1is-i92p-h54s-628a-au/j19t-bp6s-9kny-hfoo-fv",
                            "key": "j19t-bp6s-9kny-hfoo-fv",
                            "uuid": "j19t-bp6s-9kny-hfoo-fv",
                            "data": {
                                "dir": "Gagan",
                                "driverName": null,
                                "type": "sql.jdbc.groovy.managed",
                                "id": 1,
                                "userName": null,
                                "password": null,
                                "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                                "jdbcUrl": null,
                                "driver": "dynamicSwitch"
                            },
                            "category": "schema",
                            "datasourceName": "groovy man"
                        },
                        {
                            "name": "SYSPROC",
                            "children": [],
                            "keyPath": "siij-tk1m-opwl-ni1t-8s/b1is-i92p-h54s-628a-au/jpri-3bg6-clux-x2qy-cp",
                            "key": "jpri-3bg6-clux-x2qy-cp",
                            "uuid": "jpri-3bg6-clux-x2qy-cp",
                            "data": {
                                "dir": "Gagan",
                                "driverName": null,
                                "type": "sql.jdbc.groovy.managed",
                                "id": 1,
                                "userName": null,
                                "password": null,
                                "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                                "jdbcUrl": null,
                                "driver": "dynamicSwitch"
                            },
                            "category": "schema",
                            "datasourceName": "groovy man"
                        },
                        {
                            "name": "SYS",
                            "children": [],
                            "keyPath": "siij-tk1m-opwl-ni1t-8s/b1is-i92p-h54s-628a-au/o3jf-n9xd-n4fq-yejq-du",
                            "key": "o3jf-n9xd-n4fq-yejq-du",
                            "uuid": "o3jf-n9xd-n4fq-yejq-du",
                            "data": {
                                "dir": "Gagan",
                                "driverName": null,
                                "type": "sql.jdbc.groovy.managed",
                                "id": 1,
                                "userName": null,
                                "password": null,
                                "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                                "jdbcUrl": null,
                                "driver": "dynamicSwitch"
                            },
                            "category": "schema",
                            "datasourceName": "groovy man"
                        },
                        {
                            "name": "SYSSTAT",
                            "children": [],
                            "keyPath": "siij-tk1m-opwl-ni1t-8s/b1is-i92p-h54s-628a-au/r3zd-5732-q269-3gli-ym",
                            "key": "r3zd-5732-q269-3gli-ym",
                            "uuid": "r3zd-5732-q269-3gli-ym",
                            "data": {
                                "dir": "Gagan",
                                "driverName": null,
                                "type": "sql.jdbc.groovy.managed",
                                "id": 1,
                                "userName": null,
                                "password": null,
                                "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                                "jdbcUrl": null,
                                "driver": "dynamicSwitch"
                            },
                            "category": "schema",
                            "datasourceName": "groovy man"
                        }
                    ],
                    "driverType": "DynamicSwitch",
                    "keyPath": "siij-tk1m-opwl-ni1t-8s/b1is-i92p-h54s-628a-au",
                    "key": "b1is-i92p-h54s-628a-au",
                    "uuid": "b1is-i92p-h54s-628a-au",
                    "fetched": true
                }
            ],
            "key": "siij-tk1m-opwl-ni1t-8s",
            "uuid": "siij-tk1m-opwl-ni1t-8s",
            "keyPath": "siij-tk1m-opwl-ni1t-8s",
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
                "dsUUID": "zoqc-hceq-3xws-96cf-a5",
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
                        "type": "sql.jdbc.groovy.managed",
                        "baseType": "global.jdbc",
                        "catSchemaPredicted": false,
                        "sync": false,
                        "catalog": "",
                        "schema": "HIUSER",
                        "dir": "Gagan",
                        "connId": "tgbdq",
                        "classifier": "db.workflow",
                        "datasourceName": "groovy man",
                        "dsKeyPath": "siij-tk1m-opwl-ni1t-8s/b1is-i92p-h54s-628a-au/s9na-lr0g-6uj5-r2yg-jl",
                        "driverType": "DynamicSwitch",
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
                    "dir": "Gagan",
                    "driverName": null,
                    "type": "sql.jdbc.groovy.managed",
                    "id": 1,
                    "userName": null,
                    "password": null,
                    "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                    "jdbcUrl": null,
                    "driver": "dynamicSwitch"
                },
                "connId": 1,
                "dsUUID": "b1is-i92p-h54s-628a-au",
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
            }
        ]
    },
    "dataSource": [
        {
            "id": "1",
            "type": "sql.jdbc.groovy.managed",
            "baseType": "global.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "",
            "schema": "HIUSER",
            "dir": "Gagan",
            "connId": "tgbdq",
            "classifier": "db.workflow",
            "datasourceName": "groovy man",
            "dsKeyPath": "siij-tk1m-opwl-ni1t-8s/b1is-i92p-h54s-628a-au/s9na-lr0g-6uj5-r2yg-jl",
            "driverType": "DynamicSwitch",
            "database": "HIUSER"
        }
    ],
    "tables": {
        "dimdate": {
            "id": "4ac5d9f68b58bd7c0d179146e46795be",
            "name": "dimdate",
            "data": {
                "dir": "Gagan",
                "driverName": null,
                "type": "sql.jdbc.groovy.managed",
                "id": 1,
                "userName": null,
                "password": null,
                "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                "jdbcUrl": null,
                "driver": "dynamicSwitch"
            },
            "keyPath": "siij-tk1m-opwl-ni1t-8s/b1is-i92p-h54s-628a-au/s9na-lr0g-6uj5-r2yg-jl/figo-51qy-4o12-58bt-lg",
            "key": "figo-51qy-4o12-58bt-lg",
            "alias": "dimdate",
            "uuid": "figo-51qy-4o12-58bt-lg",
            "connId": "tgbdq",
            "dataSource": {
                "id": "1",
                "type": "sql.jdbc.groovy.managed",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "dir": "Gagan",
                "connId": "tgbdq",
                "classifier": "db.workflow",
                "datasourceName": "groovy man",
                "dsKeyPath": "siij-tk1m-opwl-ni1t-8s/b1is-i92p-h54s-628a-au/s9na-lr0g-6uj5-r2yg-jl",
                "driverType": "DynamicSwitch",
                "database": "HIUSER"
            },
            "dataSourceName": "groovy man",
            "category": "table",
            "nameWithConnId": "dimdate_tgbdq",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "dimdate"
        }
    },
    "views": [],
    "activeView": false,
    "categories": {
        "ztgp-oj2o-1ujv-uz23-ws": {
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
        "siij-tk1m-opwl-ni1t-8s": {
            "ds": {
                "permissionLevel": 5,
                "driver": "dynamicSwitch",
                "data": {
                    "dir": "Gagan",
                    "driverName": null,
                    "type": "sql.jdbc.groovy.managed",
                    "id": 1,
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
        }
    },
    "activeEditorTab": "info",
    "dataSourcesAddedToMetadata": [
        {
            "id": "1",
            "type": "sql.jdbc.groovy.managed",
            "baseType": "global.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "",
            "schema": "HIUSER",
            "dir": "Gagan",
            "connId": "tgbdq",
            "classifier": "db.workflow",
            "datasourceName": "groovy man",
            "dsKeyPath": "siij-tk1m-opwl-ni1t-8s/b1is-i92p-h54s-628a-au/s9na-lr0g-6uj5-r2yg-jl",
            "driverType": "DynamicSwitch",
            "database": "HIUSER"
        }
    ],
    "changeDSList": {},
    "changedTables": [],
    "changedColumns": [],
    "removedTables": [
        {
            "id": "4ac5d9f68b58bd7c0d179146e46795be",
            "connId": "0uxl9"
        }
    ],
    "removedColumns": [],
    "removedDataSources": [
        {
            "id": "1",
            "type": "dynamicDataSource",
            "baseType": "global.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "",
            "schema": "HIUSER",
            "connId": "0uxl9",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelDataDerby",
            "dsKeyPath": "ztgp-oj2o-1ujv-uz23-ws/zoqc-hceq-3xws-96cf-a5/7238-jr60-b858-fz3s-qb",
            "driverType": "Derby",
            "database": "HIUSER"
        }
    ],
    "duplicateColumnList": [],
    "duplicateTableList": [],
    "unsavedViews": [],
    "saveDetails": false,
    "savedTableIds": [],
    "savedColumnIds": [],
    "joins": [],
    "mode": "create",
    "allTablesKeys": [
        "wpj4-zpe3-k0y7-yclz-qp",
        "jgt7-133r-t7c2-onwp-qw",
        "0yrs-tdxv-ztkg-hnh3-tq",
        "t1c3-qv96-qutm-j27a-pk",
        "sdfg-118e-keqe-tx60-vs",
        "w3b8-hrpe-un1o-v6dm-76",
        "j674-vcdq-zgpi-segt-bc",
        "zk04-lhr2-fio3-nvxh-0y",
        "figo-51qy-4o12-58bt-lg",
        "9i5w-pawr-3vad-fsi9-im"
    ],
    "selectedTableKeys": [
        "figo-51qy-4o12-58bt-lg"
    ],
    "metadataName": "Metadata_1",
    "activeDataSource": false,
    "metadataToEdit": false,
    "isSavingInProgress": false,
    "editViewsTempData": {},
    "inititalStateFromJest": false,
    "timeStamp": 1665670168791,
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
    "doResetFormData": true,
    "tablesMergeType": "reload"
}