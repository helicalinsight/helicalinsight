export const store5303 =  {
    "dataFetchedFor": {
        "getDatasource": true,
        "joins": true,
        "viewSessionVariables": false,
        "listDataSources": true,
        "gcaj-w9vo-otil-cu61-pe": false
    },
    "loadingStatus": {
        "getDatasource": true,
        "listDataSources": true,
        "joins": true,
        "gcaj-w9vo-otil-cu61-pe": false
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
            "name": "Sampletest24",
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
            "name": "Mysql2",
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
            "name": "sqliteDB",
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
                    "keyPath": "kx8p-t1u9-b8bg-6r3h-cc/49oi-5yhz-c4nd-jqy3-y6",
                    "key": "49oi-5yhz-c4nd-jqy3-y6",
                    "uuid": "49oi-5yhz-c4nd-jqy3-y6"
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
                    "name": "Sampletest24",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "kx8p-t1u9-b8bg-6r3h-cc/67wk-mxj8-2djd-pyt4-1u",
                    "key": "67wk-mxj8-2djd-pyt4-1u",
                    "uuid": "67wk-mxj8-2djd-pyt4-1u"
                }
            ],
            "key": "kx8p-t1u9-b8bg-6r3h-cc",
            "uuid": "kx8p-t1u9-b8bg-6r3h-cc",
            "keyPath": "kx8p-t1u9-b8bg-6r3h-cc",
            "category": "dsGroup",
            "imgUrl": "images/data_sources/defaut_datasource.png"
        },
        {
            "name": "Mysql",
            "children": [
                {
                    "data": {
                        "id": "1001",
                        "type": "dynamicDataSource"
                    },
                    "dataSourceProvider": "tomcat",
                    "type": "dynamicDataSource",
                    "permissionLevel": 5,
                    "driver": "com.mysql.jdbc.Driver",
                    "name": "Mysql2",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Mysql",
                    "keyPath": "tirl-tmg6-egqj-55pd-9x/qw7j-huaz-7wfc-jm4y-3o",
                    "key": "qw7j-huaz-7wfc-jm4y-3o",
                    "uuid": "qw7j-huaz-7wfc-jm4y-3o"
                }
            ],
            "key": "tirl-tmg6-egqj-55pd-9x",
            "uuid": "tirl-tmg6-egqj-55pd-9x",
            "keyPath": "tirl-tmg6-egqj-55pd-9x",
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
                    "name": "sqliteDB",
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
                            "keyPath": "19n8-g0k5-bteg-mz9a-6g/gcaj-w9vo-otil-cu61-pe/vxx2-cphy-3tqp-oida-35",
                            "key": "vxx2-cphy-3tqp-oida-35",
                            "alias": "dimdate",
                            "uuid": "vxx2-cphy-3tqp-oida-35",
                            "connId": "cpvjm",
                            "dataSource": {
                                "id": "1100",
                                "type": "dynamicDataSource",
                                "baseType": "global.jdbc",
                                "catSchemaPredicted": false,
                                "sync": false,
                                "catalog": "",
                                "schema": "",
                                "connId": "cpvjm",
                                "classifier": "db.workflow",
                                "datasourceName": "sqliteDB",
                                "dsKeyPath": "19n8-g0k5-bteg-mz9a-6g/gcaj-w9vo-otil-cu61-pe",
                                "driverType": "Sqlite",
                                "database": "sqliteDB"
                            },
                            "category": "table",
                            "nameWithConnId": "dimdate_cpvjm",
                            "database": "sqliteDB",
                            "selected": true
                        },
                        {
                            "id": "b161910cbebfd353351a6c0b46e6a02e",
                            "name": "employee_details",
                            "data": {
                                "id": "1100",
                                "type": "dynamicDataSource"
                            },
                            "keyPath": "19n8-g0k5-bteg-mz9a-6g/gcaj-w9vo-otil-cu61-pe/0ktm-847h-8brc-a6yw-ta",
                            "key": "0ktm-847h-8brc-a6yw-ta",
                            "alias": "employee_details",
                            "uuid": "0ktm-847h-8brc-a6yw-ta",
                            "connId": "cpvjm",
                            "dataSource": {
                                "id": "1100",
                                "type": "dynamicDataSource",
                                "baseType": "global.jdbc",
                                "catSchemaPredicted": false,
                                "sync": false,
                                "catalog": "",
                                "schema": "",
                                "connId": "cpvjm",
                                "classifier": "db.workflow",
                                "datasourceName": "sqliteDB",
                                "dsKeyPath": "19n8-g0k5-bteg-mz9a-6g/gcaj-w9vo-otil-cu61-pe",
                                "driverType": "Sqlite",
                                "database": "sqliteDB"
                            },
                            "category": "table",
                            "nameWithConnId": "employee_details_cpvjm",
                            "database": "sqliteDB",
                            "selected": true
                        },
                        {
                            "id": "f2ff93c37589ef57f40dcb15fda6d7ea",
                            "name": "geo_cordinates",
                            "data": {
                                "id": "1100",
                                "type": "dynamicDataSource"
                            },
                            "keyPath": "19n8-g0k5-bteg-mz9a-6g/gcaj-w9vo-otil-cu61-pe/fxsg-bcry-hjgn-bz6m-8s",
                            "key": "fxsg-bcry-hjgn-bz6m-8s",
                            "alias": "geo_cordinates",
                            "uuid": "fxsg-bcry-hjgn-bz6m-8s",
                            "connId": "cpvjm",
                            "dataSource": {
                                "id": "1100",
                                "type": "dynamicDataSource",
                                "baseType": "global.jdbc",
                                "catSchemaPredicted": false,
                                "sync": false,
                                "catalog": "",
                                "schema": "",
                                "connId": "cpvjm",
                                "classifier": "db.workflow",
                                "datasourceName": "sqliteDB",
                                "dsKeyPath": "19n8-g0k5-bteg-mz9a-6g/gcaj-w9vo-otil-cu61-pe",
                                "driverType": "Sqlite",
                                "database": "sqliteDB"
                            },
                            "category": "table",
                            "nameWithConnId": "geo_cordinates_cpvjm",
                            "database": "sqliteDB",
                            "selected": true
                        },
                        {
                            "id": "025fbfb381cb17d4519363c3585626fb",
                            "name": "meeting_details",
                            "data": {
                                "id": "1100",
                                "type": "dynamicDataSource"
                            },
                            "keyPath": "19n8-g0k5-bteg-mz9a-6g/gcaj-w9vo-otil-cu61-pe/j9n7-dxj6-herh-g6uy-uz",
                            "key": "j9n7-dxj6-herh-g6uy-uz",
                            "alias": "meeting_details",
                            "uuid": "j9n7-dxj6-herh-g6uy-uz",
                            "connId": "cpvjm",
                            "dataSource": {
                                "id": "1100",
                                "type": "dynamicDataSource",
                                "baseType": "global.jdbc",
                                "catSchemaPredicted": false,
                                "sync": false,
                                "catalog": "",
                                "schema": "",
                                "connId": "cpvjm",
                                "classifier": "db.workflow",
                                "datasourceName": "sqliteDB",
                                "dsKeyPath": "19n8-g0k5-bteg-mz9a-6g/gcaj-w9vo-otil-cu61-pe",
                                "driverType": "Sqlite",
                                "database": "sqliteDB"
                            },
                            "category": "table",
                            "nameWithConnId": "meeting_details_cpvjm",
                            "database": "sqliteDB",
                            "selected": true
                        },
                        {
                            "id": "21e1b86ae9680d0fc197ed543c3e37eb",
                            "name": "travel_details",
                            "data": {
                                "id": "1100",
                                "type": "dynamicDataSource"
                            },
                            "keyPath": "19n8-g0k5-bteg-mz9a-6g/gcaj-w9vo-otil-cu61-pe/koff-7wjk-56wn-tetx-4v",
                            "key": "koff-7wjk-56wn-tetx-4v",
                            "alias": "travel_details",
                            "uuid": "koff-7wjk-56wn-tetx-4v",
                            "connId": "cpvjm",
                            "dataSource": {
                                "id": "1100",
                                "type": "dynamicDataSource",
                                "baseType": "global.jdbc",
                                "catSchemaPredicted": false,
                                "sync": false,
                                "catalog": "",
                                "schema": "",
                                "connId": "cpvjm",
                                "classifier": "db.workflow",
                                "datasourceName": "sqliteDB",
                                "dsKeyPath": "19n8-g0k5-bteg-mz9a-6g/gcaj-w9vo-otil-cu61-pe",
                                "driverType": "Sqlite",
                                "database": "sqliteDB"
                            },
                            "category": "table",
                            "nameWithConnId": "travel_details_cpvjm",
                            "database": "sqliteDB",
                            "selected": true
                        }
                    ],
                    "driverType": "Sqlite",
                    "keyPath": "19n8-g0k5-bteg-mz9a-6g/gcaj-w9vo-otil-cu61-pe",
                    "key": "gcaj-w9vo-otil-cu61-pe",
                    "uuid": "gcaj-w9vo-otil-cu61-pe",
                    "fetched": true
                }
            ],
            "key": "19n8-g0k5-bteg-mz9a-6g",
            "uuid": "19n8-g0k5-bteg-mz9a-6g",
            "keyPath": "19n8-g0k5-bteg-mz9a-6g",
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
                "dsUUID": "gcaj-w9vo-otil-cu61-pe",
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
                        "connId": "cpvjm",
                        "classifier": "db.workflow",
                        "datasourceName": "sqliteDB",
                        "dsKeyPath": "19n8-g0k5-bteg-mz9a-6g/gcaj-w9vo-otil-cu61-pe",
                        "driverType": "Sqlite",
                        "database": "sqliteDB"
                    },
                    "name": ""
                }
            }
        ]
    },
    "dataSource": [
        {
            "sync": false,
            "id": "1100",
            "catSchemaPredicted": false,
            "catalog": "",
            "schema": "",
            "type": "dynamicDataSource",
            "baseType": "global.jdbc",
            "connId": "cpvjm",
            "datasourceName": "sqliteDB",
            "classifier": "db.workflow",
            "dsKeyPath": "19n8-g0k5-bteg-mz9a-6g/gcaj-w9vo-otil-cu61-pe",
            "driverType": "Sqlite",
            "database": "sqliteDB"
        }
    ],
    "tables": {
        "dimdate": {
            "id": "d324e793296ff76020c708f1c8fbb704",
            "alias": "dimdate",
            "columns": {
                "dim_id": {
                    "alias": "dim_id",
                    "fullyQualifiedColumn": "dimdate.dim_id",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    },
                    "category": "column",
                    "keyName": "dim_id",
                    "uuid": "wc42-oei5-upyp-9248-1l",
                    "key": "c0d027cd-6aee-48b8-abfe-bf0b0e031ddf",
                    "keyPath": "86136b0c-6a4d-4258-93be-1379f41bcc63/c0d027cd-6aee-48b8-abfe-bf0b0e031ddf",
                    "tableKey": "dimdate",
                    "duplicateIndex": 0,
                    "columnKey": "dim_id",
                    "_columnId": "f39a3b5f-5b79-4cf8-bf15-cac9c4bbbd9d",
                    "originalName": "dim_id",
                    "originalId": "f39a3b5f-5b79-4cf8-bf15-cac9c4bbbd9d",
                    "connId": "616a0d6e-36bc-4a97-b0eb-b6a3598d6bba"
                },
                "fiscal_year": {
                    "alias": "fiscal_year",
                    "fullyQualifiedColumn": "dimdate.fiscal_year",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "keyName": "fiscal_year",
                    "uuid": "p5ao-664e-r0k4-a944-h6",
                    "key": "1f11e01d-a2e9-40c8-96ce-4cc87ecffe66",
                    "keyPath": "86136b0c-6a4d-4258-93be-1379f41bcc63/1f11e01d-a2e9-40c8-96ce-4cc87ecffe66",
                    "tableKey": "dimdate",
                    "duplicateIndex": 0,
                    "columnKey": "fiscal_year",
                    "_columnId": "aff50a47-2f9d-411f-9292-a15b6a50da7d",
                    "originalName": "fiscal_year",
                    "originalId": "aff50a47-2f9d-411f-9292-a15b6a50da7d",
                    "connId": "616a0d6e-36bc-4a97-b0eb-b6a3598d6bba"
                },
                "modified_date": {
                    "alias": "modified_date",
                    "fullyQualifiedColumn": "dimdate.modified_date",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "keyName": "modified_date",
                    "uuid": "1oho-9mwo-uxf5-kbsu-k6",
                    "key": "5a4e11ef-f4b6-40b1-9fbc-b237ae06eeed",
                    "keyPath": "86136b0c-6a4d-4258-93be-1379f41bcc63/5a4e11ef-f4b6-40b1-9fbc-b237ae06eeed",
                    "tableKey": "dimdate",
                    "duplicateIndex": 0,
                    "columnKey": "modified_date",
                    "_columnId": "246e470f-1fe7-4900-aa6a-0a1de66b73c4",
                    "originalName": "modified_date",
                    "originalId": "246e470f-1fe7-4900-aa6a-0a1de66b73c4",
                    "connId": "616a0d6e-36bc-4a97-b0eb-b6a3598d6bba"
                },
                "date_key": {
                    "alias": "date_key",
                    "fullyQualifiedColumn": "dimdate.date_key",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "keyName": "date_key",
                    "uuid": "58xx-pixc-nvhs-wfom-0g",
                    "key": "aa875342-cf80-4a1a-b174-4b1da892f3b0",
                    "keyPath": "86136b0c-6a4d-4258-93be-1379f41bcc63/aa875342-cf80-4a1a-b174-4b1da892f3b0",
                    "tableKey": "dimdate",
                    "duplicateIndex": 0,
                    "columnKey": "date_key",
                    "_columnId": "ef17d783-1aaa-49a8-8ae8-7084802436b1",
                    "originalName": "date_key",
                    "originalId": "ef17d783-1aaa-49a8-8ae8-7084802436b1",
                    "connId": "616a0d6e-36bc-4a97-b0eb-b6a3598d6bba"
                },
                "day_number": {
                    "alias": "day_number",
                    "fullyQualifiedColumn": "dimdate.day_number",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "keyName": "day_number",
                    "uuid": "8vpt-29jr-53j0-r8xd-mq",
                    "key": "b54ba6bb-293f-49c6-897f-c7eef061c436",
                    "keyPath": "86136b0c-6a4d-4258-93be-1379f41bcc63/b54ba6bb-293f-49c6-897f-c7eef061c436",
                    "tableKey": "dimdate",
                    "duplicateIndex": 0,
                    "columnKey": "day_number",
                    "_columnId": "63855bcc-a834-4a5b-b1f9-d366710943f9",
                    "originalName": "day_number",
                    "originalId": "63855bcc-a834-4a5b-b1f9-d366710943f9",
                    "connId": "616a0d6e-36bc-4a97-b0eb-b6a3598d6bba"
                },
                "fiscal_month_name": {
                    "alias": "fiscal_month_name",
                    "fullyQualifiedColumn": "dimdate.fiscal_month_name",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "keyName": "fiscal_month_name",
                    "uuid": "uhru-62xo-2cft-edew-et",
                    "key": "a762937a-bd95-4a69-8912-0d1e53cbe068",
                    "keyPath": "86136b0c-6a4d-4258-93be-1379f41bcc63/a762937a-bd95-4a69-8912-0d1e53cbe068",
                    "tableKey": "dimdate",
                    "duplicateIndex": 0,
                    "columnKey": "fiscal_month_name",
                    "_columnId": "0f798d52-86b7-4209-afdd-7305e23c9820",
                    "originalName": "fiscal_month_name",
                    "originalId": "0f798d52-86b7-4209-afdd-7305e23c9820",
                    "connId": "616a0d6e-36bc-4a97-b0eb-b6a3598d6bba"
                },
                "fiscal_month_label": {
                    "alias": "fiscal_month_label",
                    "fullyQualifiedColumn": "dimdate.fiscal_month_label",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "keyName": "fiscal_month_label",
                    "uuid": "xt6e-2dmd-pyai-yr0h-g7",
                    "key": "e2119409-31ea-4be2-8f6b-33d4297fe8ac",
                    "keyPath": "86136b0c-6a4d-4258-93be-1379f41bcc63/e2119409-31ea-4be2-8f6b-33d4297fe8ac",
                    "tableKey": "dimdate",
                    "duplicateIndex": 0,
                    "columnKey": "fiscal_month_label",
                    "_columnId": "2200b7a9-cef7-40d7-9584-65daa01f7ada",
                    "originalName": "fiscal_month_label",
                    "originalId": "2200b7a9-cef7-40d7-9584-65daa01f7ada",
                    "connId": "616a0d6e-36bc-4a97-b0eb-b6a3598d6bba"
                },
                "created_date": {
                    "alias": "created_date",
                    "fullyQualifiedColumn": "dimdate.created_date",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "keyName": "created_date",
                    "uuid": "8mg2-epg4-svo3-4ess-y8",
                    "key": "4e8e83d6-0bc4-465e-b6fb-ad3cc3cafeea",
                    "keyPath": "86136b0c-6a4d-4258-93be-1379f41bcc63/4e8e83d6-0bc4-465e-b6fb-ad3cc3cafeea",
                    "tableKey": "dimdate",
                    "duplicateIndex": 0,
                    "columnKey": "created_date",
                    "_columnId": "dd03a297-c5b2-4fba-b65f-fa3d46778fae",
                    "originalName": "created_date",
                    "originalId": "dd03a297-c5b2-4fba-b65f-fa3d46778fae",
                    "connId": "616a0d6e-36bc-4a97-b0eb-b6a3598d6bba"
                },
                "created_time": {
                    "alias": "created_time",
                    "fullyQualifiedColumn": "dimdate.created_time",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "keyName": "created_time",
                    "uuid": "47mn-gibi-58fa-73ia-bt",
                    "key": "00fa3c88-ff08-42ba-a27d-8be98e1429bc",
                    "keyPath": "86136b0c-6a4d-4258-93be-1379f41bcc63/00fa3c88-ff08-42ba-a27d-8be98e1429bc",
                    "tableKey": "dimdate",
                    "duplicateIndex": 0,
                    "columnKey": "created_time",
                    "_columnId": "a7666784-54c7-48f4-8b0d-57203a965355",
                    "originalName": "created_time",
                    "originalId": "a7666784-54c7-48f4-8b0d-57203a965355",
                    "connId": "616a0d6e-36bc-4a97-b0eb-b6a3598d6bba"
                },
                "rating": {
                    "alias": "rating",
                    "fullyQualifiedColumn": "dimdate.rating",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "keyName": "rating",
                    "uuid": "dsrw-y8jf-f0du-ri4d-x3",
                    "key": "c01b0d41-9e48-41c3-be7c-ae6887dacb2f",
                    "keyPath": "86136b0c-6a4d-4258-93be-1379f41bcc63/c01b0d41-9e48-41c3-be7c-ae6887dacb2f",
                    "tableKey": "dimdate",
                    "duplicateIndex": 0,
                    "columnKey": "rating",
                    "_columnId": "4dea6a7f-a6af-40b4-8c06-689bb5e766f7",
                    "originalName": "rating",
                    "originalId": "4dea6a7f-a6af-40b4-8c06-689bb5e766f7",
                    "connId": "616a0d6e-36bc-4a97-b0eb-b6a3598d6bba"
                }
            },
            "name": "dimdate",
            "keyName": "dimdate",
            "uuid": "86136b0c-6a4d-4258-93be-1379f41bcc63",
            "key": "86136b0c-6a4d-4258-93be-1379f41bcc63",
            "keyPath": "86136b0c-6a4d-4258-93be-1379f41bcc63",
            "category": "table",
            "selected": true,
            "dataSource": {
                "sync": false,
                "id": "1100",
                "catSchemaPredicted": false,
                "catalog": "",
                "schema": "",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "connId": "616a0d6e-36bc-4a97-b0eb-b6a3598d6bba",
                "datasourceName": "sqliteDB"
            },
            "columnsFetched": true,
            "connId": "616a0d6e-36bc-4a97-b0eb-b6a3598d6bba",
            "data": {
                "id": "1100",
                "type": "dynamicDataSource"
            }
        },
        "employee_details": {
            "id": "b161910cbebfd353351a6c0b46e6a02e",
            "alias": "employee_details",
            "columns": {
                "employee_id": {
                    "alias": "employee_id",
                    "fullyQualifiedColumn": "employee_details.employee_id",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    },
                    "category": "column",
                    "keyName": "employee_id",
                    "uuid": "3r8j-mqut-1pag-y1mc-wn",
                    "key": "0cfdeb2d-b6ff-435f-9385-cea37acf346d",
                    "keyPath": "268cca41-585f-4382-b6e5-a98c55fc6ea7/0cfdeb2d-b6ff-435f-9385-cea37acf346d",
                    "tableKey": "employee_details",
                    "duplicateIndex": 0,
                    "columnKey": "employee_id",
                    "_columnId": "8c34c53e-e706-4431-a3c2-ba371402516e",
                    "originalName": "employee_id",
                    "originalId": "8c34c53e-e706-4431-a3c2-ba371402516e",
                    "connId": "616a0d6e-36bc-4a97-b0eb-b6a3598d6bba"
                },
                "employee_name": {
                    "alias": "employee_name",
                    "fullyQualifiedColumn": "employee_details.employee_name",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "keyName": "employee_name",
                    "uuid": "ikux-e0hw-9qbx-863b-kn",
                    "key": "47aa8a7a-a366-457e-bdba-d62180c05791",
                    "keyPath": "268cca41-585f-4382-b6e5-a98c55fc6ea7/47aa8a7a-a366-457e-bdba-d62180c05791",
                    "tableKey": "employee_details",
                    "duplicateIndex": 0,
                    "columnKey": "employee_name",
                    "_columnId": "39d964c2-dd97-4a84-97ef-21f3fd3e9062",
                    "originalName": "employee_name",
                    "originalId": "39d964c2-dd97-4a84-97ef-21f3fd3e9062",
                    "connId": "616a0d6e-36bc-4a97-b0eb-b6a3598d6bba"
                },
                "age": {
                    "alias": "age",
                    "fullyQualifiedColumn": "employee_details.age",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    },
                    "category": "column",
                    "keyName": "age",
                    "uuid": "5dk8-q5x0-pp7e-keqe-er",
                    "key": "619b652f-5f4e-4c17-968e-421a282d4d7f",
                    "keyPath": "268cca41-585f-4382-b6e5-a98c55fc6ea7/619b652f-5f4e-4c17-968e-421a282d4d7f",
                    "tableKey": "employee_details",
                    "duplicateIndex": 0,
                    "columnKey": "age",
                    "_columnId": "f64007a8-d92c-4e76-96d6-39ae8b0eec50",
                    "originalName": "age",
                    "originalId": "f64007a8-d92c-4e76-96d6-39ae8b0eec50",
                    "connId": "616a0d6e-36bc-4a97-b0eb-b6a3598d6bba"
                },
                "address": {
                    "alias": "address",
                    "fullyQualifiedColumn": "employee_details.address",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "keyName": "address",
                    "uuid": "ctnd-l0ye-pv7n-19j9-80",
                    "key": "3181c539-202d-42f2-a784-f813c8350ea3",
                    "keyPath": "268cca41-585f-4382-b6e5-a98c55fc6ea7/3181c539-202d-42f2-a784-f813c8350ea3",
                    "tableKey": "employee_details",
                    "duplicateIndex": 0,
                    "columnKey": "address",
                    "_columnId": "2a453d30-e7b7-4adb-9afe-dee160ef6318",
                    "originalName": "address",
                    "originalId": "2a453d30-e7b7-4adb-9afe-dee160ef6318",
                    "connId": "616a0d6e-36bc-4a97-b0eb-b6a3598d6bba"
                }
            },
            "name": "employee_details",
            "keyName": "employee_details",
            "uuid": "268cca41-585f-4382-b6e5-a98c55fc6ea7",
            "key": "268cca41-585f-4382-b6e5-a98c55fc6ea7",
            "keyPath": "268cca41-585f-4382-b6e5-a98c55fc6ea7",
            "category": "table",
            "selected": true,
            "dataSource": {
                "sync": false,
                "id": "1100",
                "catSchemaPredicted": false,
                "catalog": "",
                "schema": "",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "connId": "616a0d6e-36bc-4a97-b0eb-b6a3598d6bba",
                "datasourceName": "sqliteDB"
            },
            "columnsFetched": true,
            "connId": "616a0d6e-36bc-4a97-b0eb-b6a3598d6bba",
            "data": {
                "id": "1100",
                "type": "dynamicDataSource"
            }
        }
    },
    "views": [],
    "activeView": false,
    "categories": {
        "kx8p-t1u9-b8bg-6r3h-cc": {
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
        "tirl-tmg6-egqj-55pd-9x": {
            "ds": {
                "data": {
                    "id": "1001",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "com.mysql.jdbc.Driver",
                "name": "Mysql2",
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
        "19n8-g0k5-bteg-mz9a-6g": {
            "ds": {
                "data": {
                    "id": "1100",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "org.sqlite.JDBC",
                "name": "sqliteDB",
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
            "sync": false,
            "id": "1100",
            "catSchemaPredicted": false,
            "catalog": "",
            "schema": "",
            "type": "dynamicDataSource",
            "baseType": "global.jdbc",
            "connId": "616a0d6e-36bc-4a97-b0eb-b6a3598d6bba",
            "datasourceName": "sqliteDB",
            "driverType": "Sqlite",
            "database": ""
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
        "location": "5303_G",
        "uuid": "Metadata_1.metadata"
    },
    "savedTableIds": [
        "d324e793296ff76020c708f1c8fbb704",
        "b161910cbebfd353351a6c0b46e6a02e"
    ],
    "savedColumnIds": [
        {
            "columnId": "f39a3b5f-5b79-4cf8-bf15-cac9c4bbbd9d",
            "tableId": "d324e793296ff76020c708f1c8fbb704"
        },
        {
            "columnId": "aff50a47-2f9d-411f-9292-a15b6a50da7d",
            "tableId": "d324e793296ff76020c708f1c8fbb704"
        },
        {
            "columnId": "246e470f-1fe7-4900-aa6a-0a1de66b73c4",
            "tableId": "d324e793296ff76020c708f1c8fbb704"
        },
        {
            "columnId": "ef17d783-1aaa-49a8-8ae8-7084802436b1",
            "tableId": "d324e793296ff76020c708f1c8fbb704"
        },
        {
            "columnId": "63855bcc-a834-4a5b-b1f9-d366710943f9",
            "tableId": "d324e793296ff76020c708f1c8fbb704"
        },
        {
            "columnId": "0f798d52-86b7-4209-afdd-7305e23c9820",
            "tableId": "d324e793296ff76020c708f1c8fbb704"
        },
        {
            "columnId": "2200b7a9-cef7-40d7-9584-65daa01f7ada",
            "tableId": "d324e793296ff76020c708f1c8fbb704"
        },
        {
            "columnId": "dd03a297-c5b2-4fba-b65f-fa3d46778fae",
            "tableId": "d324e793296ff76020c708f1c8fbb704"
        },
        {
            "columnId": "a7666784-54c7-48f4-8b0d-57203a965355",
            "tableId": "d324e793296ff76020c708f1c8fbb704"
        },
        {
            "columnId": "4dea6a7f-a6af-40b4-8c06-689bb5e766f7",
            "tableId": "d324e793296ff76020c708f1c8fbb704"
        },
        {
            "columnId": "8c34c53e-e706-4431-a3c2-ba371402516e",
            "tableId": "b161910cbebfd353351a6c0b46e6a02e"
        },
        {
            "columnId": "39d964c2-dd97-4a84-97ef-21f3fd3e9062",
            "tableId": "b161910cbebfd353351a6c0b46e6a02e"
        },
        {
            "columnId": "f64007a8-d92c-4e76-96d6-39ae8b0eec50",
            "tableId": "b161910cbebfd353351a6c0b46e6a02e"
        },
        {
            "columnId": "2a453d30-e7b7-4adb-9afe-dee160ef6318",
            "tableId": "b161910cbebfd353351a6c0b46e6a02e"
        }
    ],
    "joins": [],
    "mode": "edit",
    "allTablesKeys": [
        "fxsg-bcry-hjgn-bz6m-8s",
        "j9n7-dxj6-herh-g6uy-uz",
        "0ktm-847h-8brc-a6yw-ta",
        "vxx2-cphy-3tqp-oida-35",
        "koff-7wjk-56wn-tetx-4v"
    ],
    "selectedTableKeys": [
        "vxx2-cphy-3tqp-oida-35",
        "0ktm-847h-8brc-a6yw-ta",
        "fxsg-bcry-hjgn-bz6m-8s",
        "j9n7-dxj6-herh-g6uy-uz",
        "koff-7wjk-56wn-tetx-4v"
    ],
    "metadataName": "Metadata_1",
    "activeDataSource": false,
    "metadataToEdit": false,
    "isSavingInProgress": false,
    "editViewsTempData": {},
    "inititalStateFromJest": false,
    "timeStamp": 1661857210063,
    "initialEditResponse": {
        "classifier": "db.generic",
        "name": "",
        "dataSource": {
            "sync": false,
            "id": "1100",
            "catSchemaPredicted": false,
            "catalog": "",
            "schema": "",
            "type": "dynamicDataSource",
            "baseType": "global.jdbc"
        },
        "uniqueId": "Metadata_1",
        "tables": {
            "dimdate": {
                "id": "d324e793296ff76020c708f1c8fbb704",
                "alias": "dimdate",
                "columns": {
                    "dim_id": {
                        "alias": "dim_id",
                        "fullyQualifiedColumn": "dimdate.dim_id",
                        "columnId": "f39a3b5f-5b79-4cf8-bf15-cac9c4bbbd9d",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "fiscal_year": {
                        "alias": "fiscal_year",
                        "fullyQualifiedColumn": "dimdate.fiscal_year",
                        "columnId": "aff50a47-2f9d-411f-9292-a15b6a50da7d",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "modified_date": {
                        "alias": "modified_date",
                        "fullyQualifiedColumn": "dimdate.modified_date",
                        "columnId": "246e470f-1fe7-4900-aa6a-0a1de66b73c4",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "date_key": {
                        "alias": "date_key",
                        "fullyQualifiedColumn": "dimdate.date_key",
                        "columnId": "ef17d783-1aaa-49a8-8ae8-7084802436b1",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "day_number": {
                        "alias": "day_number",
                        "fullyQualifiedColumn": "dimdate.day_number",
                        "columnId": "63855bcc-a834-4a5b-b1f9-d366710943f9",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "fiscal_month_name": {
                        "alias": "fiscal_month_name",
                        "fullyQualifiedColumn": "dimdate.fiscal_month_name",
                        "columnId": "0f798d52-86b7-4209-afdd-7305e23c9820",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "fiscal_month_label": {
                        "alias": "fiscal_month_label",
                        "fullyQualifiedColumn": "dimdate.fiscal_month_label",
                        "columnId": "2200b7a9-cef7-40d7-9584-65daa01f7ada",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "created_date": {
                        "alias": "created_date",
                        "fullyQualifiedColumn": "dimdate.created_date",
                        "columnId": "dd03a297-c5b2-4fba-b65f-fa3d46778fae",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "created_time": {
                        "alias": "created_time",
                        "fullyQualifiedColumn": "dimdate.created_time",
                        "columnId": "a7666784-54c7-48f4-8b0d-57203a965355",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "rating": {
                        "alias": "rating",
                        "fullyQualifiedColumn": "dimdate.rating",
                        "columnId": "4dea6a7f-a6af-40b4-8c06-689bb5e766f7",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    }
                },
                "name": "dimdate"
            },
            "employee_details": {
                "id": "b161910cbebfd353351a6c0b46e6a02e",
                "alias": "employee_details",
                "columns": {
                    "employee_id": {
                        "alias": "employee_id",
                        "fullyQualifiedColumn": "employee_details.employee_id",
                        "columnId": "8c34c53e-e706-4431-a3c2-ba371402516e",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "employee_name": {
                        "alias": "employee_name",
                        "fullyQualifiedColumn": "employee_details.employee_name",
                        "columnId": "39d964c2-dd97-4a84-97ef-21f3fd3e9062",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "age": {
                        "alias": "age",
                        "fullyQualifiedColumn": "employee_details.age",
                        "columnId": "f64007a8-d92c-4e76-96d6-39ae8b0eec50",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "address": {
                        "alias": "address",
                        "fullyQualifiedColumn": "employee_details.address",
                        "columnId": "2a453d30-e7b7-4adb-9afe-dee160ef6318",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    }
                },
                "name": "employee_details"
            }
        },
        "sets": [
            [
                "employee_details"
            ],
            [
                "dimdate"
            ]
        ],
        "joins": [],
        "metadataName": "Metadata_1",
        "metadataDir": "5303_G"
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
    "expressionName": "",
    "expressionType": "",
    "isApplyDisabled": true,
    "isInfoShow": true,
    "securityKeysChecked": [],
    "hasUnsavedData": true,
    "editMdFromHomeInfo": {
        "dir": "5303_G/Metadata_1.metadata",
        "file": "Metadata_1.metadata"
    }
}

export const actionData = {
    type: 'UPDATE_METADATA_STATE',
    payload: {
      key: 'tables',
      mode: 'updateTables',
      value: {
        dimdate: {
          id: 'd324e793296ff76020c708f1c8fbb704',
          alias: 'dimdate',
          columns: {
            dim_id: {
              alias: 'dim_id',
              fullyQualifiedColumn: 'dimdate.dim_id',
              defaultFunction: 'db.generic.aggregate.sum',
              type: {
                'java.lang.Integer': 'numeric'
              },
              category: 'column',
              keyName: 'dim_id',
              uuid: 'wc42-oei5-upyp-9248-1l',
              key: 'c0d027cd-6aee-48b8-abfe-bf0b0e031ddf',
              keyPath: '86136b0c-6a4d-4258-93be-1379f41bcc63/c0d027cd-6aee-48b8-abfe-bf0b0e031ddf',
              tableKey: 'dimdate',
              duplicateIndex: 0,
              columnKey: 'dim_id',
              _columnId: 'f39a3b5f-5b79-4cf8-bf15-cac9c4bbbd9d',
              originalName: 'dim_id',
              originalId: 'f39a3b5f-5b79-4cf8-bf15-cac9c4bbbd9d',
              connId: '616a0d6e-36bc-4a97-b0eb-b6a3598d6bba'
            },
            fiscal_year: {
              alias: 'fiscal_year',
              fullyQualifiedColumn: 'dimdate.fiscal_year',
              defaultFunction: 'db.generic.groupBy.group',
              type: {
                'java.lang.String': 'text'
              },
              category: 'column',
              keyName: 'fiscal_year',
              uuid: 'p5ao-664e-r0k4-a944-h6',
              key: '1f11e01d-a2e9-40c8-96ce-4cc87ecffe66',
              keyPath: '86136b0c-6a4d-4258-93be-1379f41bcc63/1f11e01d-a2e9-40c8-96ce-4cc87ecffe66',
              tableKey: 'dimdate',
              duplicateIndex: 0,
              columnKey: 'fiscal_year',
              _columnId: 'aff50a47-2f9d-411f-9292-a15b6a50da7d',
              originalName: 'fiscal_year',
              originalId: 'aff50a47-2f9d-411f-9292-a15b6a50da7d',
              connId: '616a0d6e-36bc-4a97-b0eb-b6a3598d6bba'
            },
            modified_date: {
              alias: 'modified_date',
              fullyQualifiedColumn: 'dimdate.modified_date',
              defaultFunction: 'db.generic.groupBy.group',
              type: {
                'java.lang.String': 'text'
              },
              category: 'column',
              keyName: 'modified_date',
              uuid: '1oho-9mwo-uxf5-kbsu-k6',
              key: '5a4e11ef-f4b6-40b1-9fbc-b237ae06eeed',
              keyPath: '86136b0c-6a4d-4258-93be-1379f41bcc63/5a4e11ef-f4b6-40b1-9fbc-b237ae06eeed',
              tableKey: 'dimdate',
              duplicateIndex: 0,
              columnKey: 'modified_date',
              _columnId: '246e470f-1fe7-4900-aa6a-0a1de66b73c4',
              originalName: 'modified_date',
              originalId: '246e470f-1fe7-4900-aa6a-0a1de66b73c4',
              connId: '616a0d6e-36bc-4a97-b0eb-b6a3598d6bba'
            },
            date_key: {
              alias: 'date_key',
              fullyQualifiedColumn: 'dimdate.date_key',
              defaultFunction: 'db.generic.groupBy.group',
              type: {
                'java.lang.String': 'text'
              },
              category: 'column',
              keyName: 'date_key',
              uuid: '58xx-pixc-nvhs-wfom-0g',
              key: 'aa875342-cf80-4a1a-b174-4b1da892f3b0',
              keyPath: '86136b0c-6a4d-4258-93be-1379f41bcc63/aa875342-cf80-4a1a-b174-4b1da892f3b0',
              tableKey: 'dimdate',
              duplicateIndex: 0,
              columnKey: 'date_key',
              _columnId: 'ef17d783-1aaa-49a8-8ae8-7084802436b1',
              originalName: 'date_key',
              originalId: 'ef17d783-1aaa-49a8-8ae8-7084802436b1',
              connId: '616a0d6e-36bc-4a97-b0eb-b6a3598d6bba'
            },
            day_number: {
              alias: 'day_number',
              fullyQualifiedColumn: 'dimdate.day_number',
              defaultFunction: 'db.generic.groupBy.group',
              type: {
                'java.lang.String': 'text'
              },
              category: 'column',
              keyName: 'day_number',
              uuid: '8vpt-29jr-53j0-r8xd-mq',
              key: 'b54ba6bb-293f-49c6-897f-c7eef061c436',
              keyPath: '86136b0c-6a4d-4258-93be-1379f41bcc63/b54ba6bb-293f-49c6-897f-c7eef061c436',
              tableKey: 'dimdate',
              duplicateIndex: 0,
              columnKey: 'day_number',
              _columnId: '63855bcc-a834-4a5b-b1f9-d366710943f9',
              originalName: 'day_number',
              originalId: '63855bcc-a834-4a5b-b1f9-d366710943f9',
              connId: '616a0d6e-36bc-4a97-b0eb-b6a3598d6bba'
            },
            fiscal_month_name: {
              alias: 'fiscal_month_name',
              fullyQualifiedColumn: 'dimdate.fiscal_month_name',
              defaultFunction: 'db.generic.groupBy.group',
              type: {
                'java.lang.String': 'text'
              },
              category: 'column',
              keyName: 'fiscal_month_name',
              uuid: 'uhru-62xo-2cft-edew-et',
              key: 'a762937a-bd95-4a69-8912-0d1e53cbe068',
              keyPath: '86136b0c-6a4d-4258-93be-1379f41bcc63/a762937a-bd95-4a69-8912-0d1e53cbe068',
              tableKey: 'dimdate',
              duplicateIndex: 0,
              columnKey: 'fiscal_month_name',
              _columnId: '0f798d52-86b7-4209-afdd-7305e23c9820',
              originalName: 'fiscal_month_name',
              originalId: '0f798d52-86b7-4209-afdd-7305e23c9820',
              connId: '616a0d6e-36bc-4a97-b0eb-b6a3598d6bba'
            },
            fiscal_month_label: {
              alias: 'fiscal_month_label',
              fullyQualifiedColumn: 'dimdate.fiscal_month_label',
              defaultFunction: 'db.generic.groupBy.group',
              type: {
                'java.lang.String': 'text'
              },
              category: 'column',
              keyName: 'fiscal_month_label',
              uuid: 'xt6e-2dmd-pyai-yr0h-g7',
              key: 'e2119409-31ea-4be2-8f6b-33d4297fe8ac',
              keyPath: '86136b0c-6a4d-4258-93be-1379f41bcc63/e2119409-31ea-4be2-8f6b-33d4297fe8ac',
              tableKey: 'dimdate',
              duplicateIndex: 0,
              columnKey: 'fiscal_month_label',
              _columnId: '2200b7a9-cef7-40d7-9584-65daa01f7ada',
              originalName: 'fiscal_month_label',
              originalId: '2200b7a9-cef7-40d7-9584-65daa01f7ada',
              connId: '616a0d6e-36bc-4a97-b0eb-b6a3598d6bba'
            },
            created_date: {
              alias: 'created_date',
              fullyQualifiedColumn: 'dimdate.created_date',
              defaultFunction: 'db.generic.groupBy.group',
              type: {
                'java.lang.String': 'text'
              },
              category: 'column',
              keyName: 'created_date',
              uuid: '8mg2-epg4-svo3-4ess-y8',
              key: '4e8e83d6-0bc4-465e-b6fb-ad3cc3cafeea',
              keyPath: '86136b0c-6a4d-4258-93be-1379f41bcc63/4e8e83d6-0bc4-465e-b6fb-ad3cc3cafeea',
              tableKey: 'dimdate',
              duplicateIndex: 0,
              columnKey: 'created_date',
              _columnId: 'dd03a297-c5b2-4fba-b65f-fa3d46778fae',
              originalName: 'created_date',
              originalId: 'dd03a297-c5b2-4fba-b65f-fa3d46778fae',
              connId: '616a0d6e-36bc-4a97-b0eb-b6a3598d6bba'
            },
            created_time: {
              alias: 'created_time',
              fullyQualifiedColumn: 'dimdate.created_time',
              defaultFunction: 'db.generic.groupBy.group',
              type: {
                'java.lang.String': 'text'
              },
              category: 'column',
              keyName: 'created_time',
              uuid: '47mn-gibi-58fa-73ia-bt',
              key: '00fa3c88-ff08-42ba-a27d-8be98e1429bc',
              keyPath: '86136b0c-6a4d-4258-93be-1379f41bcc63/00fa3c88-ff08-42ba-a27d-8be98e1429bc',
              tableKey: 'dimdate',
              duplicateIndex: 0,
              columnKey: 'created_time',
              _columnId: 'a7666784-54c7-48f4-8b0d-57203a965355',
              originalName: 'created_time',
              originalId: 'a7666784-54c7-48f4-8b0d-57203a965355',
              connId: '616a0d6e-36bc-4a97-b0eb-b6a3598d6bba'
            },
            rating: {
              alias: 'rating',
              fullyQualifiedColumn: 'dimdate.rating',
              defaultFunction: 'db.generic.groupBy.group',
              type: {
                'java.lang.String': 'text'
              },
              category: 'column',
              keyName: 'rating',
              uuid: 'dsrw-y8jf-f0du-ri4d-x3',
              key: 'c01b0d41-9e48-41c3-be7c-ae6887dacb2f',
              keyPath: '86136b0c-6a4d-4258-93be-1379f41bcc63/c01b0d41-9e48-41c3-be7c-ae6887dacb2f',
              tableKey: 'dimdate',
              duplicateIndex: 0,
              columnKey: 'rating',
              _columnId: '4dea6a7f-a6af-40b4-8c06-689bb5e766f7',
              originalName: 'rating',
              originalId: '4dea6a7f-a6af-40b4-8c06-689bb5e766f7',
              connId: '616a0d6e-36bc-4a97-b0eb-b6a3598d6bba'
            }
          },
          name: 'dimdate',
          keyName: 'dimdate',
          uuid: '86136b0c-6a4d-4258-93be-1379f41bcc63',
          key: '86136b0c-6a4d-4258-93be-1379f41bcc63',
          keyPath: '86136b0c-6a4d-4258-93be-1379f41bcc63',
          category: 'table',
          selected: true,
          dataSource: {
            sync: false,
            id: '1100',
            catSchemaPredicted: false,
            catalog: '',
            schema: '',
            type: 'dynamicDataSource',
            baseType: 'global.jdbc',
            connId: '616a0d6e-36bc-4a97-b0eb-b6a3598d6bba',
            datasourceName: 'sqliteDB'
          },
          columnsFetched: true,
          connId: '616a0d6e-36bc-4a97-b0eb-b6a3598d6bba',
          data: {
            id: '1100',
            type: 'dynamicDataSource'
          }
        },
        employee_details: {
          id: 'b161910cbebfd353351a6c0b46e6a02e',
          alias: 'employee_details',
          columns: {
            employee_id: {
              alias: 'employee_id',
              fullyQualifiedColumn: 'employee_details.employee_id',
              defaultFunction: 'db.generic.aggregate.sum',
              type: {
                'java.lang.Integer': 'numeric'
              },
              category: 'column',
              keyName: 'employee_id',
              uuid: '3r8j-mqut-1pag-y1mc-wn',
              key: '0cfdeb2d-b6ff-435f-9385-cea37acf346d',
              keyPath: '268cca41-585f-4382-b6e5-a98c55fc6ea7/0cfdeb2d-b6ff-435f-9385-cea37acf346d',
              tableKey: 'employee_details',
              duplicateIndex: 0,
              columnKey: 'employee_id',
              _columnId: '8c34c53e-e706-4431-a3c2-ba371402516e',
              originalName: 'employee_id',
              originalId: '8c34c53e-e706-4431-a3c2-ba371402516e',
              connId: '616a0d6e-36bc-4a97-b0eb-b6a3598d6bba'
            },
            employee_name: {
              alias: 'employee_name',
              fullyQualifiedColumn: 'employee_details.employee_name',
              defaultFunction: 'db.generic.groupBy.group',
              type: {
                'java.lang.String': 'text'
              },
              category: 'column',
              keyName: 'employee_name',
              uuid: 'ikux-e0hw-9qbx-863b-kn',
              key: '47aa8a7a-a366-457e-bdba-d62180c05791',
              keyPath: '268cca41-585f-4382-b6e5-a98c55fc6ea7/47aa8a7a-a366-457e-bdba-d62180c05791',
              tableKey: 'employee_details',
              duplicateIndex: 0,
              columnKey: 'employee_name',
              _columnId: '39d964c2-dd97-4a84-97ef-21f3fd3e9062',
              originalName: 'employee_name',
              originalId: '39d964c2-dd97-4a84-97ef-21f3fd3e9062',
              connId: '616a0d6e-36bc-4a97-b0eb-b6a3598d6bba'
            },
            age: {
              alias: 'age',
              fullyQualifiedColumn: 'employee_details.age',
              defaultFunction: 'db.generic.aggregate.sum',
              type: {
                'java.lang.Integer': 'numeric'
              },
              category: 'column',
              keyName: 'age',
              uuid: '5dk8-q5x0-pp7e-keqe-er',
              key: '619b652f-5f4e-4c17-968e-421a282d4d7f',
              keyPath: '268cca41-585f-4382-b6e5-a98c55fc6ea7/619b652f-5f4e-4c17-968e-421a282d4d7f',
              tableKey: 'employee_details',
              duplicateIndex: 0,
              columnKey: 'age',
              _columnId: 'f64007a8-d92c-4e76-96d6-39ae8b0eec50',
              originalName: 'age',
              originalId: 'f64007a8-d92c-4e76-96d6-39ae8b0eec50',
              connId: '616a0d6e-36bc-4a97-b0eb-b6a3598d6bba'
            },
            address: {
              alias: 'address',
              fullyQualifiedColumn: 'employee_details.address',
              defaultFunction: 'db.generic.groupBy.group',
              type: {
                'java.lang.String': 'text'
              },
              category: 'column',
              keyName: 'address',
              uuid: 'ctnd-l0ye-pv7n-19j9-80',
              key: '3181c539-202d-42f2-a784-f813c8350ea3',
              keyPath: '268cca41-585f-4382-b6e5-a98c55fc6ea7/3181c539-202d-42f2-a784-f813c8350ea3',
              tableKey: 'employee_details',
              duplicateIndex: 0,
              columnKey: 'address',
              _columnId: '2a453d30-e7b7-4adb-9afe-dee160ef6318',
              originalName: 'address',
              originalId: '2a453d30-e7b7-4adb-9afe-dee160ef6318',
              connId: '616a0d6e-36bc-4a97-b0eb-b6a3598d6bba'
            }
          },
          name: 'employee_details',
          keyName: 'employee_details',
          uuid: '268cca41-585f-4382-b6e5-a98c55fc6ea7',
          key: '268cca41-585f-4382-b6e5-a98c55fc6ea7',
          keyPath: '268cca41-585f-4382-b6e5-a98c55fc6ea7',
          category: 'table',
          selected: true,
          dataSource: {
            sync: false,
            id: '1100',
            catSchemaPredicted: false,
            catalog: '',
            schema: '',
            type: 'dynamicDataSource',
            baseType: 'global.jdbc',
            connId: '616a0d6e-36bc-4a97-b0eb-b6a3598d6bba',
            datasourceName: 'sqliteDB'
          },
          columnsFetched: true,
          connId: '616a0d6e-36bc-4a97-b0eb-b6a3598d6bba',
          data: {
            id: '1100',
            type: 'dynamicDataSource'
          }
        },
        geo_cordinates: {
          id: 'f2ff93c37589ef57f40dcb15fda6d7ea',
          name: 'geo_cordinates',
          data: {
            id: '1100',
            type: 'dynamicDataSource'
          },
          keyPath: '19n8-g0k5-bteg-mz9a-6g/gcaj-w9vo-otil-cu61-pe/fxsg-bcry-hjgn-bz6m-8s',
          key: 'fxsg-bcry-hjgn-bz6m-8s',
          alias: 'geo_cordinates',
          uuid: 'fxsg-bcry-hjgn-bz6m-8s',
          connId: 'cpvjm',
          dataSource: {
            id: '1100',
            type: 'dynamicDataSource',
            baseType: 'global.jdbc',
            catSchemaPredicted: false,
            sync: false,
            catalog: '',
            schema: '',
            connId: 'cpvjm',
            classifier: 'db.workflow',
            datasourceName: 'sqliteDB',
            dsKeyPath: '19n8-g0k5-bteg-mz9a-6g/gcaj-w9vo-otil-cu61-pe',
            driverType: 'Sqlite',
            database: 'sqliteDB'
          },
          category: 'table',
          nameWithConnId: 'geo_cordinates_cpvjm',
          database: 'sqliteDB',
          selected: true,
          keyName: 'geo_cordinates'
        },
        meeting_details: {
          id: '025fbfb381cb17d4519363c3585626fb',
          name: 'meeting_details',
          data: {
            id: '1100',
            type: 'dynamicDataSource'
          },
          keyPath: '19n8-g0k5-bteg-mz9a-6g/gcaj-w9vo-otil-cu61-pe/j9n7-dxj6-herh-g6uy-uz',
          key: 'j9n7-dxj6-herh-g6uy-uz',
          alias: 'meeting_details',
          uuid: 'j9n7-dxj6-herh-g6uy-uz',
          connId: 'cpvjm',
          dataSource: {
            id: '1100',
            type: 'dynamicDataSource',
            baseType: 'global.jdbc',
            catSchemaPredicted: false,
            sync: false,
            catalog: '',
            schema: '',
            connId: 'cpvjm',
            classifier: 'db.workflow',
            datasourceName: 'sqliteDB',
            dsKeyPath: '19n8-g0k5-bteg-mz9a-6g/gcaj-w9vo-otil-cu61-pe',
            driverType: 'Sqlite',
            database: 'sqliteDB'
          },
          category: 'table',
          nameWithConnId: 'meeting_details_cpvjm',
          database: 'sqliteDB',
          selected: true,
          keyName: 'meeting_details'
        },
        travel_details: {
          id: '21e1b86ae9680d0fc197ed543c3e37eb',
          name: 'travel_details',
          data: {
            id: '1100',
            type: 'dynamicDataSource'
          },
          keyPath: '19n8-g0k5-bteg-mz9a-6g/gcaj-w9vo-otil-cu61-pe/koff-7wjk-56wn-tetx-4v',
          key: 'koff-7wjk-56wn-tetx-4v',
          alias: 'travel_details',
          uuid: 'koff-7wjk-56wn-tetx-4v',
          connId: 'cpvjm',
          dataSource: {
            id: '1100',
            type: 'dynamicDataSource',
            baseType: 'global.jdbc',
            catSchemaPredicted: false,
            sync: false,
            catalog: '',
            schema: '',
            connId: 'cpvjm',
            classifier: 'db.workflow',
            datasourceName: 'sqliteDB',
            dsKeyPath: '19n8-g0k5-bteg-mz9a-6g/gcaj-w9vo-otil-cu61-pe',
            driverType: 'Sqlite',
            database: 'sqliteDB'
          },
          category: 'table',
          nameWithConnId: 'travel_details_cpvjm',
          database: 'sqliteDB',
          selected: true,
          keyName: 'travel_details'
        }
      },
      notSelectedKeys: [],
      mergeType: 'reload',
      actualMergeType: 'merge',
      metadataForEdit: false,
      deleteViews: false,
      others: [
        {
          key: 'savedTableIds',
          value: [
            'd324e793296ff76020c708f1c8fbb704',
            'b161910cbebfd353351a6c0b46e6a02e'
          ]
        },
        {
          key: 'savedColumnIds',
          value: [
            {
              columnId: 'f39a3b5f-5b79-4cf8-bf15-cac9c4bbbd9d',
              tableId: 'd324e793296ff76020c708f1c8fbb704'
            },
            {
              columnId: 'aff50a47-2f9d-411f-9292-a15b6a50da7d',
              tableId: 'd324e793296ff76020c708f1c8fbb704'
            },
            {
              columnId: '246e470f-1fe7-4900-aa6a-0a1de66b73c4',
              tableId: 'd324e793296ff76020c708f1c8fbb704'
            },
            {
              columnId: 'ef17d783-1aaa-49a8-8ae8-7084802436b1',
              tableId: 'd324e793296ff76020c708f1c8fbb704'
            },
            {
              columnId: '63855bcc-a834-4a5b-b1f9-d366710943f9',
              tableId: 'd324e793296ff76020c708f1c8fbb704'
            },
            {
              columnId: '0f798d52-86b7-4209-afdd-7305e23c9820',
              tableId: 'd324e793296ff76020c708f1c8fbb704'
            },
            {
              columnId: '2200b7a9-cef7-40d7-9584-65daa01f7ada',
              tableId: 'd324e793296ff76020c708f1c8fbb704'
            },
            {
              columnId: 'dd03a297-c5b2-4fba-b65f-fa3d46778fae',
              tableId: 'd324e793296ff76020c708f1c8fbb704'
            },
            {
              columnId: 'a7666784-54c7-48f4-8b0d-57203a965355',
              tableId: 'd324e793296ff76020c708f1c8fbb704'
            },
            {
              columnId: '4dea6a7f-a6af-40b4-8c06-689bb5e766f7',
              tableId: 'd324e793296ff76020c708f1c8fbb704'
            },
            {
              columnId: '8c34c53e-e706-4431-a3c2-ba371402516e',
              tableId: 'b161910cbebfd353351a6c0b46e6a02e'
            },
            {
              columnId: '39d964c2-dd97-4a84-97ef-21f3fd3e9062',
              tableId: 'b161910cbebfd353351a6c0b46e6a02e'
            },
            {
              columnId: 'f64007a8-d92c-4e76-96d6-39ae8b0eec50',
              tableId: 'b161910cbebfd353351a6c0b46e6a02e'
            },
            {
              columnId: '2a453d30-e7b7-4adb-9afe-dee160ef6318',
              tableId: 'b161910cbebfd353351a6c0b46e6a02e'
            }
          ]
        }
      ]
    }
  }