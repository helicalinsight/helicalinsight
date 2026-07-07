export const store5304 = {
    "dataFetchedFor": {
        "getDatasource": true,
        "joins": false,
        "viewSessionVariables": false,
        "listDataSources": true,
        "ef4s-b0ov-dq4x-0con-5n": false
    },
    "loadingStatus": {
        "getDatasource": true,
        "listDataSources": true,
        "ef4s-b0ov-dq4x-0con-5n": false
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
            "name": "Sampletest",
            "classifier": "global",
            "dataSourceType": "Managed DataSource"
        },
        {
            "data": {
                "id": "1200",
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
                "id": "1100",
                "type": "dynamicDataSource"
            },
            "dataSourceProvider": "tomcat",
            "type": "dynamicDataSource",
            "permissionLevel": 5,
            "driver": "org.sqlite.JDBC",
            "name": "sqliteDB1",
            "classifier": "global",
            "dataSourceType": "Managed DataSource"
        },
        {
            "data": {
                "id": "1300",
                "type": "dynamicDataSource"
            },
            "dataSourceProvider": "tomcat",
            "type": "dynamicDataSource",
            "permissionLevel": 5,
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "name": "Derby",
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
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "r4sj-ui7r-qlwy-uhby-45/18ks-xx9b-rc4t-n04z-0b",
                    "key": "18ks-xx9b-rc4t-n04z-0b",
                    "uuid": "18ks-xx9b-rc4t-n04z-0b"
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
                    "name": "Sampletest",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "r4sj-ui7r-qlwy-uhby-45/ojf5-gayu-7dkh-ckgq-2t",
                    "key": "ojf5-gayu-7dkh-ckgq-2t",
                    "uuid": "ojf5-gayu-7dkh-ckgq-2t"
                },
                {
                    "data": {
                        "id": "1300",
                        "type": "dynamicDataSource"
                    },
                    "dataSourceProvider": "tomcat",
                    "type": "dynamicDataSource",
                    "permissionLevel": 5,
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "name": "Derby",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "r4sj-ui7r-qlwy-uhby-45/6ljz-uu0p-hpyq-q47l-id",
                    "key": "6ljz-uu0p-hpyq-q47l-id",
                    "uuid": "6ljz-uu0p-hpyq-q47l-id"
                }
            ],
            "key": "r4sj-ui7r-qlwy-uhby-45",
            "uuid": "r4sj-ui7r-qlwy-uhby-45",
            "keyPath": "r4sj-ui7r-qlwy-uhby-45",
            "category": "dsGroup",
            "imgUrl": "images/data_sources/defaut_datasource.png"
        },
        {
            "name": "Mysql",
            "children": [
                {
                    "data": {
                        "id": "1200",
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
                    "keyPath": "u5k5-phab-snw2-3vju-5y/m110-b1nc-xuyg-z610-kj",
                    "key": "m110-b1nc-xuyg-z610-kj",
                    "uuid": "m110-b1nc-xuyg-z610-kj"
                }
            ],
            "key": "u5k5-phab-snw2-3vju-5y",
            "uuid": "u5k5-phab-snw2-3vju-5y",
            "keyPath": "u5k5-phab-snw2-3vju-5y",
            "category": "dsGroup",
            "imgUrl": "images/data_sources/defaut_datasource.png"
        },
        {
            "name": "Sqlite",
            "children": [
                {
                    "data": {
                        "id": "1100",
                        "type": "dynamicDataSource"
                    },
                    "dataSourceProvider": "tomcat",
                    "type": "dynamicDataSource",
                    "permissionLevel": 5,
                    "driver": "org.sqlite.JDBC",
                    "name": "sqliteDB1",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource",
                    "category": "dataSource",
                    "children": [
                        {
                            "id": "d324e793296ff76020c708f1c8fbb704",
                            "name": "dimdate",
                            "data": {
                                "id": "1100",
                                "type": "dynamicDataSource"
                            },
                            "keyPath": "5bwi-a4d4-v3qq-dfoa-ss/ef4s-b0ov-dq4x-0con-5n/vhka-wv6m-lpn4-1a7s-d4",
                            "key": "vhka-wv6m-lpn4-1a7s-d4",
                            "alias": "dimdate",
                            "uuid": "vhka-wv6m-lpn4-1a7s-d4",
                            "connId": "faaft",
                            "dataSource": {
                                "id": "1100",
                                "type": "dynamicDataSource",
                                "baseType": "global.jdbc",
                                "catSchemaPredicted": false,
                                "sync": false,
                                "catalog": "",
                                "schema": "",
                                "connId": "faaft",
                                "classifier": "db.workflow",
                                "datasourceName": "sqliteDB1",
                                "dsKeyPath": "5bwi-a4d4-v3qq-dfoa-ss/ef4s-b0ov-dq4x-0con-5n",
                                "driverType": "Sqlite",
                                "database": "sqliteDB1"
                            },
                            "category": "table",
                            "nameWithConnId": "dimdate_faaft",
                            "database": "sqliteDB1",
                            "selected": true
                        },
                        {
                            "id": "b161910cbebfd353351a6c0b46e6a02e",
                            "name": "employee_details",
                            "data": {
                                "id": "1100",
                                "type": "dynamicDataSource"
                            },
                            "keyPath": "5bwi-a4d4-v3qq-dfoa-ss/ef4s-b0ov-dq4x-0con-5n/8h76-v6le-7za0-ez7i-yb",
                            "key": "8h76-v6le-7za0-ez7i-yb",
                            "alias": "employee_details",
                            "uuid": "8h76-v6le-7za0-ez7i-yb",
                            "connId": "faaft",
                            "dataSource": {
                                "id": "1100",
                                "type": "dynamicDataSource",
                                "baseType": "global.jdbc",
                                "catSchemaPredicted": false,
                                "sync": false,
                                "catalog": "",
                                "schema": "",
                                "connId": "faaft",
                                "classifier": "db.workflow",
                                "datasourceName": "sqliteDB1",
                                "dsKeyPath": "5bwi-a4d4-v3qq-dfoa-ss/ef4s-b0ov-dq4x-0con-5n",
                                "driverType": "Sqlite",
                                "database": "sqliteDB1"
                            },
                            "category": "table",
                            "nameWithConnId": "employee_details_faaft",
                            "database": "sqliteDB1",
                            "selected": true
                        },
                        {
                            "id": "f2ff93c37589ef57f40dcb15fda6d7ea",
                            "name": "geo_cordinates",
                            "data": {
                                "id": "1100",
                                "type": "dynamicDataSource"
                            },
                            "keyPath": "5bwi-a4d4-v3qq-dfoa-ss/ef4s-b0ov-dq4x-0con-5n/ja0a-857s-i7tw-i8ax-ba",
                            "key": "ja0a-857s-i7tw-i8ax-ba",
                            "alias": "geo_cordinates",
                            "uuid": "ja0a-857s-i7tw-i8ax-ba",
                            "connId": "faaft",
                            "dataSource": {
                                "id": "1100",
                                "type": "dynamicDataSource",
                                "baseType": "global.jdbc",
                                "catSchemaPredicted": false,
                                "sync": false,
                                "catalog": "",
                                "schema": "",
                                "connId": "faaft",
                                "classifier": "db.workflow",
                                "datasourceName": "sqliteDB1",
                                "dsKeyPath": "5bwi-a4d4-v3qq-dfoa-ss/ef4s-b0ov-dq4x-0con-5n",
                                "driverType": "Sqlite",
                                "database": "sqliteDB1"
                            },
                            "category": "table",
                            "nameWithConnId": "geo_cordinates_faaft",
                            "database": "sqliteDB1",
                            "selected": true
                        },
                        {
                            "id": "025fbfb381cb17d4519363c3585626fb",
                            "name": "meeting_details",
                            "data": {
                                "id": "1100",
                                "type": "dynamicDataSource"
                            },
                            "keyPath": "5bwi-a4d4-v3qq-dfoa-ss/ef4s-b0ov-dq4x-0con-5n/dbeb-emuq-qmmv-2oct-d4",
                            "key": "dbeb-emuq-qmmv-2oct-d4",
                            "alias": "meeting_details",
                            "uuid": "dbeb-emuq-qmmv-2oct-d4",
                            "connId": "faaft",
                            "dataSource": {
                                "id": "1100",
                                "type": "dynamicDataSource",
                                "baseType": "global.jdbc",
                                "catSchemaPredicted": false,
                                "sync": false,
                                "catalog": "",
                                "schema": "",
                                "connId": "faaft",
                                "classifier": "db.workflow",
                                "datasourceName": "sqliteDB1",
                                "dsKeyPath": "5bwi-a4d4-v3qq-dfoa-ss/ef4s-b0ov-dq4x-0con-5n",
                                "driverType": "Sqlite",
                                "database": "sqliteDB1"
                            },
                            "category": "table",
                            "nameWithConnId": "meeting_details_faaft",
                            "database": "sqliteDB1",
                            "selected": false
                        },
                        {
                            "id": "21e1b86ae9680d0fc197ed543c3e37eb",
                            "name": "travel_details",
                            "data": {
                                "id": "1100",
                                "type": "dynamicDataSource"
                            },
                            "keyPath": "5bwi-a4d4-v3qq-dfoa-ss/ef4s-b0ov-dq4x-0con-5n/dn76-xc8h-393u-tdn6-ce",
                            "key": "dn76-xc8h-393u-tdn6-ce",
                            "alias": "travel_details",
                            "uuid": "dn76-xc8h-393u-tdn6-ce",
                            "connId": "faaft",
                            "dataSource": {
                                "id": "1100",
                                "type": "dynamicDataSource",
                                "baseType": "global.jdbc",
                                "catSchemaPredicted": false,
                                "sync": false,
                                "catalog": "",
                                "schema": "",
                                "connId": "faaft",
                                "classifier": "db.workflow",
                                "datasourceName": "sqliteDB1",
                                "dsKeyPath": "5bwi-a4d4-v3qq-dfoa-ss/ef4s-b0ov-dq4x-0con-5n",
                                "driverType": "Sqlite",
                                "database": "sqliteDB1"
                            },
                            "category": "table",
                            "nameWithConnId": "travel_details_faaft",
                            "database": "sqliteDB1",
                            "selected": false
                        }
                    ],
                    "driverType": "Sqlite",
                    "keyPath": "5bwi-a4d4-v3qq-dfoa-ss/ef4s-b0ov-dq4x-0con-5n",
                    "key": "ef4s-b0ov-dq4x-0con-5n",
                    "uuid": "ef4s-b0ov-dq4x-0con-5n",
                    "fetched": true
                }
            ],
            "key": "5bwi-a4d4-v3qq-dfoa-ss",
            "uuid": "5bwi-a4d4-v3qq-dfoa-ss",
            "keyPath": "5bwi-a4d4-v3qq-dfoa-ss",
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
                    "id": "1100",
                    "type": "dynamicDataSource"
                },
                "connId": "1100",
                "dsUUID": "ef4s-b0ov-dq4x-0con-5n",
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
                        "id": "1100",
                        "type": "dynamicDataSource",
                        "baseType": "global.jdbc",
                        "catSchemaPredicted": false,
                        "sync": false,
                        "catalog": "",
                        "schema": "",
                        "connId": "faaft",
                        "classifier": "db.workflow",
                        "datasourceName": "sqliteDB1",
                        "dsKeyPath": "5bwi-a4d4-v3qq-dfoa-ss/ef4s-b0ov-dq4x-0con-5n",
                        "driverType": "Sqlite",
                        "database": "sqliteDB1"
                    },
                    "name": ""
                }
            }
        ]
    },
    "dataSource": [
        {
            "id": "1100",
            "type": "dynamicDataSource",
            "baseType": "global.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "",
            "schema": "",
            "connId": "faaft",
            "classifier": "db.workflow",
            "datasourceName": "sqliteDB1",
            "dsKeyPath": "5bwi-a4d4-v3qq-dfoa-ss/ef4s-b0ov-dq4x-0con-5n",
            "driverType": "Sqlite",
            "database": ""
        }
    ],
    "tables": {
        "dimdate": {
            "id": "d324e793296ff76020c708f1c8fbb704",
            "name": "dimdate",
            "data": {
                "id": "1100",
                "type": "dynamicDataSource"
            },
            "keyPath": "5bwi-a4d4-v3qq-dfoa-ss/ef4s-b0ov-dq4x-0con-5n/vhka-wv6m-lpn4-1a7s-d4",
            "key": "vhka-wv6m-lpn4-1a7s-d4",
            "alias": "dimdate",
            "uuid": "vhka-wv6m-lpn4-1a7s-d4",
            "connId": "faaft",
            "dataSource": {
                "id": "1100",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "",
                "connId": "faaft",
                "classifier": "db.workflow",
                "datasourceName": "sqliteDB1",
                "dsKeyPath": "5bwi-a4d4-v3qq-dfoa-ss/ef4s-b0ov-dq4x-0con-5n",
                "driverType": "Sqlite",
                "database": "sqliteDB1"
            },
            "category": "table",
            "nameWithConnId": "dimdate_faaft",
            "database": "sqliteDB1",
            "selected": true,
            "keyName": "dimdate"
        },
        "employee_details": {
            "id": "b161910cbebfd353351a6c0b46e6a02e",
            "name": "employee_details",
            "data": {
                "id": "1100",
                "type": "dynamicDataSource"
            },
            "keyPath": "5bwi-a4d4-v3qq-dfoa-ss/ef4s-b0ov-dq4x-0con-5n/8h76-v6le-7za0-ez7i-yb",
            "key": "8h76-v6le-7za0-ez7i-yb",
            "alias": "employee_details",
            "uuid": "8h76-v6le-7za0-ez7i-yb",
            "connId": "faaft",
            "dataSource": {
                "id": "1100",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "",
                "connId": "faaft",
                "classifier": "db.workflow",
                "datasourceName": "sqliteDB1",
                "dsKeyPath": "5bwi-a4d4-v3qq-dfoa-ss/ef4s-b0ov-dq4x-0con-5n",
                "driverType": "Sqlite",
                "database": "sqliteDB1"
            },
            "category": "table",
            "nameWithConnId": "employee_details_faaft",
            "database": "sqliteDB1",
            "selected": true,
            "keyName": "employee_details"
        },
        "geo_cordinates": {
            "id": "f2ff93c37589ef57f40dcb15fda6d7ea",
            "name": "geo_cordinates",
            "data": {
                "id": "1100",
                "type": "dynamicDataSource"
            },
            "keyPath": "5bwi-a4d4-v3qq-dfoa-ss/ef4s-b0ov-dq4x-0con-5n/ja0a-857s-i7tw-i8ax-ba",
            "key": "ja0a-857s-i7tw-i8ax-ba",
            "alias": "geo_cordinates",
            "uuid": "ja0a-857s-i7tw-i8ax-ba",
            "connId": "faaft",
            "dataSource": {
                "id": "1100",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "",
                "connId": "faaft",
                "classifier": "db.workflow",
                "datasourceName": "sqliteDB1",
                "dsKeyPath": "5bwi-a4d4-v3qq-dfoa-ss/ef4s-b0ov-dq4x-0con-5n",
                "driverType": "Sqlite",
                "database": "sqliteDB1"
            },
            "category": "table",
            "nameWithConnId": "geo_cordinates_faaft",
            "database": "sqliteDB1",
            "selected": true,
            "keyName": "geo_cordinates"
        }
    },
    "views": [],
    "activeView": false,
    "categories": {
        "r4sj-ui7r-qlwy-uhby-45": {
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
        "u5k5-phab-snw2-3vju-5y": {
            "ds": {
                "data": {
                    "id": "1200",
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
        "5bwi-a4d4-v3qq-dfoa-ss": {
            "ds": {
                "data": {
                    "id": "1100",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "org.sqlite.JDBC",
                "name": "sqliteDB1",
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
            "id": "1100",
            "type": "dynamicDataSource",
            "baseType": "global.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "",
            "schema": "",
            "connId": "faaft",
            "classifier": "db.workflow",
            "datasourceName": "sqliteDB1",
            "dsKeyPath": "5bwi-a4d4-v3qq-dfoa-ss/ef4s-b0ov-dq4x-0con-5n",
            "driverType": "Sqlite",
            "database": ""
        }
    ],
    "changeDSList": {},
    "changedTables": [],
    "changedColumns": [],
    "removedTables": [
        {
            "id": "21e1b86ae9680d0fc197ed543c3e37eb",
            "connId": "faaft"
        },
        {
            "id": "025fbfb381cb17d4519363c3585626fb",
            "connId": "faaft"
        }
    ],
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
    "allTablesKeys": [],
    "selectedTableKeys": [
        "vhka-wv6m-lpn4-1a7s-d4",
        "8h76-v6le-7za0-ez7i-yb",
        "ja0a-857s-i7tw-i8ax-ba"
    ],
    "metadataName": "Metadata_1",
    "activeDataSource": false,
    "metadataToEdit": false,
    "isSavingInProgress": false,
    "editViewsTempData": {},
    "inititalStateFromJest": false,
    "timeStamp": 1661876569994,
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
    "expressionName": "",
    "expressionType": "",
    "isApplyDisabled": true,
    "isInfoShow": true,
    "securityKeysChecked": [],
    "hasUnsavedData": true
}

export const actionData = {
    type: "UPDATE_METADATA_TABLES",
    payload: {
        data: {
            dimdate: {
                id: 'd324e793296ff76020c708f1c8fbb704',
                name: 'dimdate',
                data: {
                    id: '1100',
                    type: 'dynamicDataSource'
                },
                keyPath: '5bwi-a4d4-v3qq-dfoa-ss/ef4s-b0ov-dq4x-0con-5n/vhka-wv6m-lpn4-1a7s-d4',
                key: 'vhka-wv6m-lpn4-1a7s-d4',
                alias: 'dimdate',
                uuid: 'vhka-wv6m-lpn4-1a7s-d4',
                connId: 'faaft',
                dataSource: {
                    id: '1100',
                    type: 'dynamicDataSource',
                    baseType: 'global.jdbc',
                    catSchemaPredicted: false,
                    sync: false,
                    catalog: '',
                    schema: '',
                    connId: 'faaft',
                    classifier: 'db.workflow',
                    datasourceName: 'sqliteDB1',
                    dsKeyPath: '5bwi-a4d4-v3qq-dfoa-ss/ef4s-b0ov-dq4x-0con-5n',
                    driverType: 'Sqlite',
                    database: 'sqliteDB1'
                },
                category: 'table',
                nameWithConnId: 'dimdate_faaft',
                database: 'sqliteDB1',
                selected: true,
                keyName: 'dimdate'
            },
            employee_details: {
                id: 'b161910cbebfd353351a6c0b46e6a02e',
                name: 'employee_details',
                data: {
                    id: '1100',
                    type: 'dynamicDataSource'
                },
                keyPath: '5bwi-a4d4-v3qq-dfoa-ss/ef4s-b0ov-dq4x-0con-5n/8h76-v6le-7za0-ez7i-yb',
                key: '8h76-v6le-7za0-ez7i-yb',
                alias: 'employee_details',
                uuid: '8h76-v6le-7za0-ez7i-yb',
                connId: 'faaft',
                dataSource: {
                    id: '1100',
                    type: 'dynamicDataSource',
                    baseType: 'global.jdbc',
                    catSchemaPredicted: false,
                    sync: false,
                    catalog: '',
                    schema: '',
                    connId: 'faaft',
                    classifier: 'db.workflow',
                    datasourceName: 'sqliteDB1',
                    dsKeyPath: '5bwi-a4d4-v3qq-dfoa-ss/ef4s-b0ov-dq4x-0con-5n',
                    driverType: 'Sqlite',
                    database: 'sqliteDB1'
                },
                category: 'table',
                nameWithConnId: 'employee_details_faaft',
                database: 'sqliteDB1',
                selected: true,
                keyName: 'employee_details'
            }
        },
        override: true,
        removedTables: [
            {
                id: '21e1b86ae9680d0fc197ed543c3e37eb',
                connId: 'faaft'
            },
            {
                id: '025fbfb381cb17d4519363c3585626fb',
                connId: 'faaft'
            },
            {
                id: 'f2ff93c37589ef57f40dcb15fda6d7ea',
                connId: 'faaft'
            }
        ],
        updateDS: true,
        updateDSAddedToMetada: true,
        dataSource: [
            {
                id: '1100',
                type: 'dynamicDataSource',
                baseType: 'global.jdbc',
                catSchemaPredicted: false,
                sync: false,
                catalog: '',
                schema: '',
                connId: 'faaft',
                classifier: 'db.workflow',
                datasourceName: 'sqliteDB1',
                dsKeyPath: '5bwi-a4d4-v3qq-dfoa-ss/ef4s-b0ov-dq4x-0con-5n',
                driverType: 'Sqlite',
                database: ''
            }
        ],
        removedDS: []
    }
}