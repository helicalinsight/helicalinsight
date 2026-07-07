export const storeData = {
    "dataFetchedFor": {
        "getDatasource": true,
        "joins": true,
        "viewSessionVariables": false,
        "listDataSources": true,
        "ce1v-rief-wrir-v1iu-fn": false,
        "h5bi-dzjg-80fo-qznc-25": false,
        "i5oc-lrz6-y4wp-fhtf-y9": false
    },
    "loadingStatus": {
        "getDatasource": true,
        "listDataSources": true,
        "ce1v-rief-wrir-v1iu-fn": false,
        "h5bi-dzjg-80fo-qznc-25": false,
        "i5oc-lrz6-y4wp-fhtf-y9": false
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
            "name": "Microsoft Sqlserver",
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
            "databaseDialect": "elasticsearchsql",
            "name": "Elasticsearch",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png",
            "url": "jdbc:es://{{hostName}}",
            "parameters": {
                "hostName": "localhost"
            }
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
            "driver": "oracle.jdbc.OracleDriver",
            "databaseDialect": "oracle",
            "name": "Oracle",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png",
            "url": "jdbc:oracle:thin:@{{hostName}}:{{port}}:{{database}}",
            "parameters": {
                "port": "1521",
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
            "driver": "net.snowflake.client.jdbc.SnowflakeDriver",
            "databaseDialect": "snowflake",
            "name": "Snowflake Client",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png",
            "url": "jdbc:snowflake://{{hostName}}",
            "parameters": {
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
            "databaseDialect": "elasticsearchsql",
            "name": "Elasticsearch",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png",
            "url": "jdbc:es://{{hostName}}",
            "parameters": {
                "hostName": "localhost"
            }
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
            "driver": "oracle.jdbc.OracleDriver",
            "databaseDialect": "oracle",
            "name": "Oracle",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png",
            "url": "jdbc:oracle:thin:@{{hostName}}:{{port}}:{{database}}",
            "parameters": {
                "port": "1521",
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
            "driver": "net.snowflake.client.jdbc.SnowflakeDriver",
            "databaseDialect": "snowflake",
            "name": "Snowflake Client",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png",
            "url": "jdbc:snowflake://{{hostName}}",
            "parameters": {
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
            "url": "jdbc:es://{{hostName}}",
            "driver": "org.elasticsearch.xpack.sql.jdbc.EsDriver",
            "available": "true",
            "parameters": {
                "hostName": "localhost"
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
            "url": "jdbc:oracle:thin:@{{hostName}}:{{port}}:{{database}}",
            "driver": "oracle.jdbc.OracleDriver",
            "available": "true",
            "parameters": {
                "port": "1521",
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
            "url": "jdbc:snowflake://{{hostName}}",
            "driver": "net.snowflake.client.jdbc.SnowflakeDriver",
            "available": "true",
            "parameters": {
                "hostName": "localhost"
            }
        },
        {
            "url": "jdbc:es://{{hostName}}",
            "driver": "org.elasticsearch.xpack.sql.jdbc.EsDriver",
            "available": "true",
            "parameters": {
                "hostName": "localhost"
            }
        },
        {
            "url": "jdbc:es://{{hostName}}",
            "driver": "org.elasticsearch.xpack.sql.jdbc.EsDriver",
            "available": "true",
            "parameters": {
                "hostName": "localhost"
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
            "permissionLevel": 5,
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "name": "Plain Jdbc",
            "classifier": "efwd",
            "type": "sql.jdbc",
            "data": {
                "dir": "datasources",
                "driverName": "org.apache.derby.jdbc.AutoloadedDriver",
                "type": "sql.jdbc",
                "id": 501,
                "userName": "hiuser",
                "password": "hiuser",
                "jdbcUrl": "jdbc:derby:/home/helical/Performance/hi/db/SampleTravelData"
            },
            "dataSourceType": "Plain Jdbc DataSource"
        },
        {
            "permissionLevel": 5,
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "name": "Plain Jdbc",
            "classifier": "efwd",
            "type": "sql.jdbc",
            "data": {
                "dir": "datasources",
                "driverName": "org.apache.derby.jdbc.AutoloadedDriver",
                "type": "sql.jdbc",
                "id": 501,
                "userName": "hiuser",
                "password": "hiuser",
                "jdbcUrl": "jdbc:derby:/home/helical/Performance/hi/db/SampleTravelData"
            },
            "dataSourceType": "Plain Jdbc DataSource"
        },
        {
            "permissionLevel": 5,
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "name": "Plain Jdbc",
            "classifier": "efwd",
            "type": "sql.jdbc",
            "data": {
                "dir": "datasources",
                "driverName": "org.apache.derby.jdbc.AutoloadedDriver",
                "type": "sql.jdbc",
                "id": 501,
                "userName": "hiuser",
                "password": "hiuser",
                "jdbcUrl": "jdbc:derby:/home/helical/Performance/hi/db/SampleTravelData"
            },
            "dataSourceType": "Plain Jdbc DataSource"
        },
        {
            "permissionLevel": 5,
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "name": "Plain Jdbc",
            "classifier": "efwd",
            "type": "sql.jdbc",
            "data": {
                "dir": "datasources",
                "driverName": "org.apache.derby.jdbc.AutoloadedDriver",
                "type": "sql.jdbc",
                "id": 501,
                "userName": "hiuser",
                "password": "hiuser",
                "jdbcUrl": "jdbc:derby:/home/helical/Performance/hi/db/SampleTravelData"
            },
            "dataSourceType": "Plain Jdbc DataSource"
        },
        {
            "permissionLevel": 5,
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "name": "Plain Jdbc",
            "classifier": "efwd",
            "type": "sql.jdbc",
            "data": {
                "dir": "datasources",
                "driverName": "org.apache.derby.jdbc.AutoloadedDriver",
                "type": "sql.jdbc",
                "id": 501,
                "userName": "hiuser",
                "password": "hiuser",
                "jdbcUrl": "jdbc:derby:/home/helical/Performance/hi/db/SampleTravelData"
            },
            "dataSourceType": "Plain Jdbc DataSource"
        },
        {
            "permissionLevel": 5,
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "name": "Plain Jdbc",
            "classifier": "efwd",
            "type": "sql.jdbc",
            "data": {
                "dir": "datasources",
                "driverName": "org.apache.derby.jdbc.AutoloadedDriver",
                "type": "sql.jdbc",
                "id": 501,
                "userName": "hiuser",
                "password": "hiuser",
                "jdbcUrl": "jdbc:derby:/home/helical/Performance/hi/db/SampleTravelData"
            },
            "dataSourceType": "Plain Jdbc DataSource"
        },
        {
            "permissionLevel": 5,
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "name": "Plain Jdbc",
            "classifier": "efwd",
            "type": "sql.jdbc",
            "data": {
                "dir": "datasources",
                "driverName": "org.apache.derby.jdbc.AutoloadedDriver",
                "type": "sql.jdbc",
                "id": 501,
                "userName": "hiuser",
                "password": "hiuser",
                "jdbcUrl": "jdbc:derby:/home/helical/Performance/hi/db/SampleTravelData"
            },
            "dataSourceType": "Plain Jdbc DataSource"
        },
        {
            "permissionLevel": 5,
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "name": "Plain Jdbc",
            "classifier": "efwd",
            "type": "sql.jdbc",
            "data": {
                "dir": "datasources",
                "driverName": "org.apache.derby.jdbc.AutoloadedDriver",
                "type": "sql.jdbc",
                "id": 501,
                "userName": "hiuser",
                "password": "hiuser",
                "jdbcUrl": "jdbc:derby:/home/helical/Performance/hi/db/SampleTravelData"
            },
            "dataSourceType": "Plain Jdbc DataSource"
        },
        {
            "permissionLevel": 5,
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "name": "Plain Jdbc",
            "classifier": "efwd",
            "type": "sql.jdbc",
            "data": {
                "dir": "datasources",
                "driverName": "org.apache.derby.jdbc.AutoloadedDriver",
                "type": "sql.jdbc",
                "id": 501,
                "userName": "hiuser",
                "password": "hiuser",
                "jdbcUrl": "jdbc:derby:/home/helical/Performance/hi/db/SampleTravelData"
            },
            "dataSourceType": "Plain Jdbc DataSource"
        },
        {
            "permissionLevel": 5,
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "name": "Plain Jdbc",
            "classifier": "efwd",
            "type": "sql.jdbc",
            "data": {
                "dir": "datasources",
                "driverName": "org.apache.derby.jdbc.AutoloadedDriver",
                "type": "sql.jdbc",
                "id": 501,
                "userName": "hiuser",
                "password": "hiuser",
                "jdbcUrl": "jdbc:derby:/home/helical/Performance/hi/db/SampleTravelData"
            },
            "dataSourceType": "Plain Jdbc DataSource"
        },
        {
            "permissionLevel": 5,
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "name": "Plain Jdbc",
            "classifier": "efwd",
            "type": "sql.jdbc",
            "data": {
                "dir": "datasources",
                "driverName": "org.apache.derby.jdbc.AutoloadedDriver",
                "type": "sql.jdbc",
                "id": 501,
                "userName": "hiuser",
                "password": "hiuser",
                "jdbcUrl": "jdbc:derby:/home/helical/Performance/hi/db/SampleTravelData"
            },
            "dataSourceType": "Plain Jdbc DataSource"
        },
        {
            "permissionLevel": 5,
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "name": "Plain Jdbc",
            "classifier": "efwd",
            "type": "sql.jdbc",
            "data": {
                "dir": "datasources",
                "driverName": "org.apache.derby.jdbc.AutoloadedDriver",
                "type": "sql.jdbc",
                "id": 501,
                "userName": "hiuser",
                "password": "hiuser",
                "jdbcUrl": "jdbc:derby:/home/helical/Performance/hi/db/SampleTravelData"
            },
            "dataSourceType": "Plain Jdbc DataSource"
        },
        {
            "permissionLevel": 5,
            "driver": "dynamicSwitch",
            "name": "GroovyManaged",
            "classifier": "efwd",
            "type": "sql.jdbc.groovy.managed",
            "data": {
                "dir": "sai_ganesh",
                "driverName": null,
                "type": "sql.jdbc.groovy.managed",
                "id": 601,
                "userName": null,
                "password": null,
                "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1001);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 300);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                "jdbcUrl": null,
                "driver": "dynamicSwitch"
            },
            "dataSourceType": ""
        },
        {
            "permissionLevel": 5,
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "name": "5722",
            "classifier": "efwd",
            "type": "sql.jdbc",
            "data": {
                "dir": "sai_ganesh",
                "driverName": "org.apache.derby.jdbc.AutoloadedDriver",
                "type": "sql.jdbc",
                "id": 701,
                "userName": "hiuser",
                "password": "hiuser",
                "jdbcUrl": "jdbc:derby:/home/helical/Performance/hi/db/SampleTravelData"
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
        },
        {
            "data": {
                "id": "1801",
                "type": "dynamicDataSource"
            },
            "dataSourceProvider": "tomcat",
            "type": "dynamicDataSource",
            "permissionLevel": 5,
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "name": "Dump",
            "classifier": "global",
            "dataSourceType": "Managed DataSource"
        },
        {
            "data": {
                "id": "1901",
                "type": "dynamicDataSource"
            },
            "dataSourceProvider": "tomcat",
            "type": "dynamicDataSource",
            "permissionLevel": 5,
            "driver": "net.snowflake.client.jdbc.SnowflakeDriver",
            "name": "SnowflakeDS",
            "classifier": "global",
            "dataSourceType": "Managed DataSource"
        },
        {
            "data": {
                "id": "1902",
                "type": "dynamicDataSource"
            },
            "dataSourceProvider": "tomcat",
            "type": "dynamicDataSource",
            "permissionLevel": 5,
            "driver": "net.snowflake.client.jdbc.SnowflakeDriver",
            "name": "SnowFlakeDSWorking",
            "classifier": "global",
            "dataSourceType": "Managed DataSource"
        },
        {
            "data": {
                "id": "2001",
                "type": "dynamicDataSource"
            },
            "dataSourceProvider": "tomcat",
            "type": "dynamicDataSource",
            "permissionLevel": 5,
            "driver": "oracle.jdbc.OracleDriver",
            "name": "Oracle_12",
            "classifier": "global",
            "dataSourceType": "Managed DataSource"
        },
        {
            "data": {
                "id": "2101",
                "type": "dynamicDataSource"
            },
            "dataSourceProvider": "tomcat",
            "type": "dynamicDataSource",
            "permissionLevel": 5,
            "driver": "com.snowflake.client.jdbc.SnowflakeDriver",
            "name": "Edit",
            "classifier": "global",
            "dataSourceType": "Managed DataSource"
        },
        {
            "data": {
                "id": "2202",
                "type": "dynamicDataSource"
            },
            "dataSourceProvider": "tomcat",
            "type": "dynamicDataSource",
            "permissionLevel": 5,
            "driver": "org.postgresql.Driver",
            "name": "SampleTravelData",
            "classifier": "global",
            "dataSourceType": "Managed DataSource"
        },
        {
            "data": {
                "id": "2301",
                "type": "dynamicDataSource"
            },
            "dataSourceProvider": "tomcat",
            "type": "dynamicDataSource",
            "permissionLevel": 5,
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "name": "SampleTravelData",
            "classifier": "global",
            "dataSourceType": "Managed DataSource"
        },
        {
            "data": {
                "id": "2402",
                "type": "dynamicDataSource"
            },
            "dataSourceProvider": "tomcat",
            "type": "dynamicDataSource",
            "permissionLevel": 5,
            "driver": "com.mysql.jdbc.Driver",
            "name": "mysql",
            "classifier": "global",
            "dataSourceType": "Managed DataSource"
        },
        {
            "data": {
                "id": "2403",
                "type": "dynamicDataSource"
            },
            "dataSourceProvider": "tomcat",
            "type": "dynamicDataSource",
            "permissionLevel": 5,
            "driver": "net.snowflake.client.jdbc.SnowflakeDriver",
            "name": "testdata",
            "classifier": "global",
            "dataSourceType": "Managed DataSource"
        },
        {
            "data": {
                "id": "2501",
                "type": "dynamicDataSource"
            },
            "dataSourceProvider": "tomcat",
            "type": "dynamicDataSource",
            "permissionLevel": 5,
            "driver": "net.snowflake.client.jdbc.SnowflakeDriver",
            "name": "60045",
            "classifier": "global",
            "dataSourceType": "Managed DataSource"
        },
        {
            "data": {
                "id": "2601",
                "type": "dynamicDataSource"
            },
            "dataSourceProvider": "tomcat",
            "type": "dynamicDataSource",
            "permissionLevel": 5,
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "name": "AnotherDerby",
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
                    "permissionLevel": 5,
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "name": "Plain Jdbc",
                    "classifier": "efwd",
                    "type": "sql.jdbc",
                    "data": {
                        "dir": "datasources",
                        "driverName": "org.apache.derby.jdbc.AutoloadedDriver",
                        "type": "sql.jdbc",
                        "id": 501,
                        "userName": "hiuser",
                        "password": "hiuser",
                        "jdbcUrl": "jdbc:derby:/home/helical/Performance/hi/db/SampleTravelData"
                    },
                    "dataSourceType": "Plain Jdbc DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "q101-dsy6-xmgq-fpyy-6u/9udz-0lhc-yl5v-jkfr-mn",
                    "key": "9udz-0lhc-yl5v-jkfr-mn",
                    "uuid": "9udz-0lhc-yl5v-jkfr-mn"
                },
                {
                    "permissionLevel": 5,
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "name": "Plain Jdbc",
                    "classifier": "efwd",
                    "type": "sql.jdbc",
                    "data": {
                        "dir": "datasources",
                        "driverName": "org.apache.derby.jdbc.AutoloadedDriver",
                        "type": "sql.jdbc",
                        "id": 501,
                        "userName": "hiuser",
                        "password": "hiuser",
                        "jdbcUrl": "jdbc:derby:/home/helical/Performance/hi/db/SampleTravelData"
                    },
                    "dataSourceType": "Plain Jdbc DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "q101-dsy6-xmgq-fpyy-6u/zdr4-pyru-hwj1-z96m-xy",
                    "key": "zdr4-pyru-hwj1-z96m-xy",
                    "uuid": "zdr4-pyru-hwj1-z96m-xy"
                },
                {
                    "permissionLevel": 5,
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "name": "Plain Jdbc",
                    "classifier": "efwd",
                    "type": "sql.jdbc",
                    "data": {
                        "dir": "datasources",
                        "driverName": "org.apache.derby.jdbc.AutoloadedDriver",
                        "type": "sql.jdbc",
                        "id": 501,
                        "userName": "hiuser",
                        "password": "hiuser",
                        "jdbcUrl": "jdbc:derby:/home/helical/Performance/hi/db/SampleTravelData"
                    },
                    "dataSourceType": "Plain Jdbc DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "q101-dsy6-xmgq-fpyy-6u/bh9t-9hpl-b4dq-10zj-j9",
                    "key": "bh9t-9hpl-b4dq-10zj-j9",
                    "uuid": "bh9t-9hpl-b4dq-10zj-j9"
                },
                {
                    "permissionLevel": 5,
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "name": "Plain Jdbc",
                    "classifier": "efwd",
                    "type": "sql.jdbc",
                    "data": {
                        "dir": "datasources",
                        "driverName": "org.apache.derby.jdbc.AutoloadedDriver",
                        "type": "sql.jdbc",
                        "id": 501,
                        "userName": "hiuser",
                        "password": "hiuser",
                        "jdbcUrl": "jdbc:derby:/home/helical/Performance/hi/db/SampleTravelData"
                    },
                    "dataSourceType": "Plain Jdbc DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "q101-dsy6-xmgq-fpyy-6u/x8hq-7m8m-8tsm-exqi-36",
                    "key": "x8hq-7m8m-8tsm-exqi-36",
                    "uuid": "x8hq-7m8m-8tsm-exqi-36"
                },
                {
                    "permissionLevel": 5,
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "name": "Plain Jdbc",
                    "classifier": "efwd",
                    "type": "sql.jdbc",
                    "data": {
                        "dir": "datasources",
                        "driverName": "org.apache.derby.jdbc.AutoloadedDriver",
                        "type": "sql.jdbc",
                        "id": 501,
                        "userName": "hiuser",
                        "password": "hiuser",
                        "jdbcUrl": "jdbc:derby:/home/helical/Performance/hi/db/SampleTravelData"
                    },
                    "dataSourceType": "Plain Jdbc DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "q101-dsy6-xmgq-fpyy-6u/j4ot-duj6-zqg6-h03z-qa",
                    "key": "j4ot-duj6-zqg6-h03z-qa",
                    "uuid": "j4ot-duj6-zqg6-h03z-qa"
                },
                {
                    "permissionLevel": 5,
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "name": "Plain Jdbc",
                    "classifier": "efwd",
                    "type": "sql.jdbc",
                    "data": {
                        "dir": "datasources",
                        "driverName": "org.apache.derby.jdbc.AutoloadedDriver",
                        "type": "sql.jdbc",
                        "id": 501,
                        "userName": "hiuser",
                        "password": "hiuser",
                        "jdbcUrl": "jdbc:derby:/home/helical/Performance/hi/db/SampleTravelData"
                    },
                    "dataSourceType": "Plain Jdbc DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "q101-dsy6-xmgq-fpyy-6u/0ors-g09c-t159-wbew-90",
                    "key": "0ors-g09c-t159-wbew-90",
                    "uuid": "0ors-g09c-t159-wbew-90"
                },
                {
                    "permissionLevel": 5,
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "name": "Plain Jdbc",
                    "classifier": "efwd",
                    "type": "sql.jdbc",
                    "data": {
                        "dir": "datasources",
                        "driverName": "org.apache.derby.jdbc.AutoloadedDriver",
                        "type": "sql.jdbc",
                        "id": 501,
                        "userName": "hiuser",
                        "password": "hiuser",
                        "jdbcUrl": "jdbc:derby:/home/helical/Performance/hi/db/SampleTravelData"
                    },
                    "dataSourceType": "Plain Jdbc DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "q101-dsy6-xmgq-fpyy-6u/vxmo-8dck-m2kv-lqeq-06",
                    "key": "vxmo-8dck-m2kv-lqeq-06",
                    "uuid": "vxmo-8dck-m2kv-lqeq-06"
                },
                {
                    "permissionLevel": 5,
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "name": "Plain Jdbc",
                    "classifier": "efwd",
                    "type": "sql.jdbc",
                    "data": {
                        "dir": "datasources",
                        "driverName": "org.apache.derby.jdbc.AutoloadedDriver",
                        "type": "sql.jdbc",
                        "id": 501,
                        "userName": "hiuser",
                        "password": "hiuser",
                        "jdbcUrl": "jdbc:derby:/home/helical/Performance/hi/db/SampleTravelData"
                    },
                    "dataSourceType": "Plain Jdbc DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "q101-dsy6-xmgq-fpyy-6u/ps4l-zbq6-gx9m-ziqj-yv",
                    "key": "ps4l-zbq6-gx9m-ziqj-yv",
                    "uuid": "ps4l-zbq6-gx9m-ziqj-yv"
                },
                {
                    "permissionLevel": 5,
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "name": "Plain Jdbc",
                    "classifier": "efwd",
                    "type": "sql.jdbc",
                    "data": {
                        "dir": "datasources",
                        "driverName": "org.apache.derby.jdbc.AutoloadedDriver",
                        "type": "sql.jdbc",
                        "id": 501,
                        "userName": "hiuser",
                        "password": "hiuser",
                        "jdbcUrl": "jdbc:derby:/home/helical/Performance/hi/db/SampleTravelData"
                    },
                    "dataSourceType": "Plain Jdbc DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "q101-dsy6-xmgq-fpyy-6u/jc31-vgo0-fvk7-aje4-r0",
                    "key": "jc31-vgo0-fvk7-aje4-r0",
                    "uuid": "jc31-vgo0-fvk7-aje4-r0"
                },
                {
                    "permissionLevel": 5,
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "name": "Plain Jdbc",
                    "classifier": "efwd",
                    "type": "sql.jdbc",
                    "data": {
                        "dir": "datasources",
                        "driverName": "org.apache.derby.jdbc.AutoloadedDriver",
                        "type": "sql.jdbc",
                        "id": 501,
                        "userName": "hiuser",
                        "password": "hiuser",
                        "jdbcUrl": "jdbc:derby:/home/helical/Performance/hi/db/SampleTravelData"
                    },
                    "dataSourceType": "Plain Jdbc DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "q101-dsy6-xmgq-fpyy-6u/n4wt-tonp-gqs7-g5dg-lx",
                    "key": "n4wt-tonp-gqs7-g5dg-lx",
                    "uuid": "n4wt-tonp-gqs7-g5dg-lx"
                },
                {
                    "permissionLevel": 5,
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "name": "Plain Jdbc",
                    "classifier": "efwd",
                    "type": "sql.jdbc",
                    "data": {
                        "dir": "datasources",
                        "driverName": "org.apache.derby.jdbc.AutoloadedDriver",
                        "type": "sql.jdbc",
                        "id": 501,
                        "userName": "hiuser",
                        "password": "hiuser",
                        "jdbcUrl": "jdbc:derby:/home/helical/Performance/hi/db/SampleTravelData"
                    },
                    "dataSourceType": "Plain Jdbc DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "q101-dsy6-xmgq-fpyy-6u/ldd4-5pcf-dfne-6dfs-ls",
                    "key": "ldd4-5pcf-dfne-6dfs-ls",
                    "uuid": "ldd4-5pcf-dfne-6dfs-ls"
                },
                {
                    "permissionLevel": 5,
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "name": "Plain Jdbc",
                    "classifier": "efwd",
                    "type": "sql.jdbc",
                    "data": {
                        "dir": "datasources",
                        "driverName": "org.apache.derby.jdbc.AutoloadedDriver",
                        "type": "sql.jdbc",
                        "id": 501,
                        "userName": "hiuser",
                        "password": "hiuser",
                        "jdbcUrl": "jdbc:derby:/home/helical/Performance/hi/db/SampleTravelData"
                    },
                    "dataSourceType": "Plain Jdbc DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "q101-dsy6-xmgq-fpyy-6u/tj79-27yg-fgjc-pvjv-9i",
                    "key": "tj79-27yg-fgjc-pvjv-9i",
                    "uuid": "tj79-27yg-fgjc-pvjv-9i"
                },
                {
                    "permissionLevel": 5,
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "name": "5722",
                    "classifier": "efwd",
                    "type": "sql.jdbc",
                    "data": {
                        "dir": "sai_ganesh",
                        "driverName": "org.apache.derby.jdbc.AutoloadedDriver",
                        "type": "sql.jdbc",
                        "id": 701,
                        "userName": "hiuser",
                        "password": "hiuser",
                        "jdbcUrl": "jdbc:derby:/home/helical/Performance/hi/db/SampleTravelData"
                    },
                    "dataSourceType": "Plain Jdbc DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "q101-dsy6-xmgq-fpyy-6u/37tg-9qbv-znh7-x9pk-c0",
                    "key": "37tg-9qbv-znh7-x9pk-c0",
                    "uuid": "37tg-9qbv-znh7-x9pk-c0"
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
                    "dataSourceType": "Managed DataSource",
                    "category": "dataSource",
                    "children": [
                        {
                            "name": "SQLJ",
                            "children": [],
                            "keyPath": "q101-dsy6-xmgq-fpyy-6u/ce1v-rief-wrir-v1iu-fn/vl36-746l-7nxs-m80l-md",
                            "key": "vl36-746l-7nxs-m80l-md",
                            "uuid": "vl36-746l-7nxs-m80l-md",
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
                            "keyPath": "q101-dsy6-xmgq-fpyy-6u/ce1v-rief-wrir-v1iu-fn/wyql-hge9-edpt-vpl9-2p",
                            "key": "wyql-hge9-edpt-vpl9-2p",
                            "uuid": "wyql-hge9-edpt-vpl9-2p",
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
                            "keyPath": "q101-dsy6-xmgq-fpyy-6u/ce1v-rief-wrir-v1iu-fn/ghv1-gvx9-0h9i-ogzn-qj",
                            "key": "ghv1-gvx9-0h9i-ogzn-qj",
                            "uuid": "ghv1-gvx9-0h9i-ogzn-qj",
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
                                    "keyPath": "q101-dsy6-xmgq-fpyy-6u/ce1v-rief-wrir-v1iu-fn/h5bi-dzjg-80fo-qznc-25/ersh-ffr4-jsjf-tjlq-22",
                                    "key": "ersh-ffr4-jsjf-tjlq-22",
                                    "alias": "dimdate",
                                    "uuid": "ersh-ffr4-jsjf-tjlq-22",
                                    "connId": "ljb4p",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "ljb4p",
                                        "dbId": "ljb4p",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "q101-dsy6-xmgq-fpyy-6u/ce1v-rief-wrir-v1iu-fn/h5bi-dzjg-80fo-qznc-25",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "dimdate_ljb4p",
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
                                    "keyPath": "q101-dsy6-xmgq-fpyy-6u/ce1v-rief-wrir-v1iu-fn/h5bi-dzjg-80fo-qznc-25/j6el-ig17-eyxa-bkib-66",
                                    "key": "j6el-ig17-eyxa-bkib-66",
                                    "alias": "employee_details",
                                    "uuid": "j6el-ig17-eyxa-bkib-66",
                                    "connId": "ljb4p",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "ljb4p",
                                        "dbId": "ljb4p",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "q101-dsy6-xmgq-fpyy-6u/ce1v-rief-wrir-v1iu-fn/h5bi-dzjg-80fo-qznc-25",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "employee_details_ljb4p",
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
                                    "keyPath": "q101-dsy6-xmgq-fpyy-6u/ce1v-rief-wrir-v1iu-fn/h5bi-dzjg-80fo-qznc-25/lm66-jvsq-xyrk-lpev-t6",
                                    "key": "lm66-jvsq-xyrk-lpev-t6",
                                    "alias": "geo_cordinates",
                                    "uuid": "lm66-jvsq-xyrk-lpev-t6",
                                    "connId": "ljb4p",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "ljb4p",
                                        "dbId": "ljb4p",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "q101-dsy6-xmgq-fpyy-6u/ce1v-rief-wrir-v1iu-fn/h5bi-dzjg-80fo-qznc-25",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "geo_cordinates_ljb4p",
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
                                    "keyPath": "q101-dsy6-xmgq-fpyy-6u/ce1v-rief-wrir-v1iu-fn/h5bi-dzjg-80fo-qznc-25/tye4-14f5-kmis-4c1n-jt",
                                    "key": "tye4-14f5-kmis-4c1n-jt",
                                    "alias": "meeting_details",
                                    "uuid": "tye4-14f5-kmis-4c1n-jt",
                                    "connId": "ljb4p",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "ljb4p",
                                        "dbId": "ljb4p",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "q101-dsy6-xmgq-fpyy-6u/ce1v-rief-wrir-v1iu-fn/h5bi-dzjg-80fo-qznc-25",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "meeting_details_ljb4p",
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
                                    "keyPath": "q101-dsy6-xmgq-fpyy-6u/ce1v-rief-wrir-v1iu-fn/h5bi-dzjg-80fo-qznc-25/i5oc-lrz6-y4wp-fhtf-y9",
                                    "key": "i5oc-lrz6-y4wp-fhtf-y9",
                                    "alias": "travel_details",
                                    "uuid": "i5oc-lrz6-y4wp-fhtf-y9",
                                    "connId": "ljb4p",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "ljb4p",
                                        "dbId": "ljb4p",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "q101-dsy6-xmgq-fpyy-6u/ce1v-rief-wrir-v1iu-fn/h5bi-dzjg-80fo-qznc-25",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "travel_details_ljb4p",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                }
                            ],
                            "keyPath": "q101-dsy6-xmgq-fpyy-6u/ce1v-rief-wrir-v1iu-fn/h5bi-dzjg-80fo-qznc-25",
                            "key": "h5bi-dzjg-80fo-qznc-25",
                            "uuid": "h5bi-dzjg-80fo-qznc-25",
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
                            "keyPath": "q101-dsy6-xmgq-fpyy-6u/ce1v-rief-wrir-v1iu-fn/homa-r9c6-lo3l-tewb-s9",
                            "key": "homa-r9c6-lo3l-tewb-s9",
                            "uuid": "homa-r9c6-lo3l-tewb-s9",
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
                            "keyPath": "q101-dsy6-xmgq-fpyy-6u/ce1v-rief-wrir-v1iu-fn/5uay-s6nj-ke5r-mduf-5a",
                            "key": "5uay-s6nj-ke5r-mduf-5a",
                            "uuid": "5uay-s6nj-ke5r-mduf-5a",
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
                            "keyPath": "q101-dsy6-xmgq-fpyy-6u/ce1v-rief-wrir-v1iu-fn/nm8y-spv5-q044-iudy-hq",
                            "key": "nm8y-spv5-q044-iudy-hq",
                            "uuid": "nm8y-spv5-q044-iudy-hq",
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
                            "keyPath": "q101-dsy6-xmgq-fpyy-6u/ce1v-rief-wrir-v1iu-fn/r3np-lt7n-sre6-nmds-ie",
                            "key": "r3np-lt7n-sre6-nmds-ie",
                            "uuid": "r3np-lt7n-sre6-nmds-ie",
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
                            "keyPath": "q101-dsy6-xmgq-fpyy-6u/ce1v-rief-wrir-v1iu-fn/lmti-r5u3-z5r2-212q-1v",
                            "key": "lmti-r5u3-z5r2-212q-1v",
                            "uuid": "lmti-r5u3-z5r2-212q-1v",
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
                            "keyPath": "q101-dsy6-xmgq-fpyy-6u/ce1v-rief-wrir-v1iu-fn/pt1z-3omf-ckax-tyl4-m2",
                            "key": "pt1z-3omf-ckax-tyl4-m2",
                            "uuid": "pt1z-3omf-ckax-tyl4-m2",
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
                            "keyPath": "q101-dsy6-xmgq-fpyy-6u/ce1v-rief-wrir-v1iu-fn/1pj1-4t76-mg7w-m3h4-81",
                            "key": "1pj1-4t76-mg7w-m3h4-81",
                            "uuid": "1pj1-4t76-mg7w-m3h4-81",
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
                            "keyPath": "q101-dsy6-xmgq-fpyy-6u/ce1v-rief-wrir-v1iu-fn/9p35-d7s1-xep3-xd5k-hk",
                            "key": "9p35-d7s1-xep3-xd5k-hk",
                            "uuid": "9p35-d7s1-xep3-xd5k-hk",
                            "data": {
                                "id": "1",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "SampleTravelDataDerby"
                        }
                    ],
                    "driverType": "Derby",
                    "keyPath": "q101-dsy6-xmgq-fpyy-6u/ce1v-rief-wrir-v1iu-fn",
                    "key": "ce1v-rief-wrir-v1iu-fn",
                    "uuid": "ce1v-rief-wrir-v1iu-fn",
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
                    "keyPath": "q101-dsy6-xmgq-fpyy-6u/j6ta-mqb4-kdt9-8lbx-97",
                    "key": "j6ta-mqb4-kdt9-8lbx-97",
                    "uuid": "j6ta-mqb4-kdt9-8lbx-97"
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
                    "keyPath": "q101-dsy6-xmgq-fpyy-6u/d8cf-af1b-khtf-htal-d4",
                    "key": "d8cf-af1b-khtf-htal-d4",
                    "uuid": "d8cf-af1b-khtf-htal-d4"
                },
                {
                    "data": {
                        "id": "1801",
                        "type": "dynamicDataSource"
                    },
                    "dataSourceProvider": "tomcat",
                    "type": "dynamicDataSource",
                    "permissionLevel": 5,
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "name": "Dump",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "q101-dsy6-xmgq-fpyy-6u/odc1-3z1f-1rf5-1bj5-gu",
                    "key": "odc1-3z1f-1rf5-1bj5-gu",
                    "uuid": "odc1-3z1f-1rf5-1bj5-gu"
                },
                {
                    "data": {
                        "id": "2301",
                        "type": "dynamicDataSource"
                    },
                    "dataSourceProvider": "tomcat",
                    "type": "dynamicDataSource",
                    "permissionLevel": 5,
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "name": "SampleTravelData",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "q101-dsy6-xmgq-fpyy-6u/5u41-f718-flfq-uh6t-va",
                    "key": "5u41-f718-flfq-uh6t-va",
                    "uuid": "5u41-f718-flfq-uh6t-va"
                },
                {
                    "data": {
                        "id": "2601",
                        "type": "dynamicDataSource"
                    },
                    "dataSourceProvider": "tomcat",
                    "type": "dynamicDataSource",
                    "permissionLevel": 5,
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "name": "AnotherDerby",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "q101-dsy6-xmgq-fpyy-6u/sa2p-rvya-b3qq-e3ib-tn",
                    "key": "sa2p-rvya-b3qq-e3ib-tn",
                    "uuid": "sa2p-rvya-b3qq-e3ib-tn"
                }
            ],
            "key": "q101-dsy6-xmgq-fpyy-6u",
            "uuid": "q101-dsy6-xmgq-fpyy-6u",
            "keyPath": "q101-dsy6-xmgq-fpyy-6u",
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
                    "keyPath": "7nrx-7gl0-1fp0-hjxj-sp/91a8-5g37-ovcd-icc7-w1",
                    "key": "91a8-5g37-ovcd-icc7-w1",
                    "uuid": "91a8-5g37-ovcd-icc7-w1"
                },
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
                    "keyPath": "7nrx-7gl0-1fp0-hjxj-sp/0qmr-y41s-91lo-wzk1-3w",
                    "key": "0qmr-y41s-91lo-wzk1-3w",
                    "uuid": "0qmr-y41s-91lo-wzk1-3w"
                },
                {
                    "permissionLevel": 5,
                    "driver": "dynamicSwitch",
                    "name": "GroovyManaged",
                    "classifier": "efwd",
                    "type": "sql.jdbc.groovy.managed",
                    "data": {
                        "dir": "sai_ganesh",
                        "driverName": null,
                        "type": "sql.jdbc.groovy.managed",
                        "id": 601,
                        "userName": null,
                        "password": null,
                        "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1001);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 300);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                        "jdbcUrl": null,
                        "driver": "dynamicSwitch"
                    },
                    "dataSourceType": "",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "DynamicSwitch",
                    "keyPath": "7nrx-7gl0-1fp0-hjxj-sp/m8l4-djd2-ugje-cz1v-ha",
                    "key": "m8l4-djd2-ugje-cz1v-ha",
                    "uuid": "m8l4-djd2-ugje-cz1v-ha"
                }
            ],
            "key": "7nrx-7gl0-1fp0-hjxj-sp",
            "uuid": "7nrx-7gl0-1fp0-hjxj-sp",
            "keyPath": "7nrx-7gl0-1fp0-hjxj-sp",
            "category": "dsGroup",
            "imgUrl": "images/data_sources/defaut_datasource.png"
        },
        {
            "name": "Mysql",
            "children": [
                {
                    "data": {
                        "id": "2402",
                        "type": "dynamicDataSource"
                    },
                    "dataSourceProvider": "tomcat",
                    "type": "dynamicDataSource",
                    "permissionLevel": 5,
                    "driver": "com.mysql.jdbc.Driver",
                    "name": "mysql",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Mysql",
                    "keyPath": "z21a-nadg-9ddp-wwyg-h6/77u3-q896-azw2-84pt-lu",
                    "key": "77u3-q896-azw2-84pt-lu",
                    "uuid": "77u3-q896-azw2-84pt-lu"
                }
            ],
            "key": "z21a-nadg-9ddp-wwyg-h6",
            "uuid": "z21a-nadg-9ddp-wwyg-h6",
            "keyPath": "z21a-nadg-9ddp-wwyg-h6",
            "category": "dsGroup",
            "imgUrl": "images/data_sources/defaut_datasource.png"
        },
        {
            "name": "Oracle",
            "children": [
                {
                    "data": {
                        "id": "2001",
                        "type": "dynamicDataSource"
                    },
                    "dataSourceProvider": "tomcat",
                    "type": "dynamicDataSource",
                    "permissionLevel": 5,
                    "driver": "oracle.jdbc.OracleDriver",
                    "name": "Oracle_12",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Oracle",
                    "keyPath": "1ps2-uox4-vtpv-duac-i3/pwps-21gz-f0vo-4kgv-74",
                    "key": "pwps-21gz-f0vo-4kgv-74",
                    "uuid": "pwps-21gz-f0vo-4kgv-74"
                }
            ],
            "key": "1ps2-uox4-vtpv-duac-i3",
            "uuid": "1ps2-uox4-vtpv-duac-i3",
            "keyPath": "1ps2-uox4-vtpv-duac-i3",
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
                    "keyPath": "8u0p-mniy-pkds-0s56-kf/gr59-f8p5-8zbo-g0tk-y6",
                    "key": "gr59-f8p5-8zbo-g0tk-y6",
                    "uuid": "gr59-f8p5-8zbo-g0tk-y6"
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
                    "keyPath": "8u0p-mniy-pkds-0s56-kf/p92f-tnpn-ysqp-7vrf-5f",
                    "key": "p92f-tnpn-ysqp-7vrf-5f",
                    "uuid": "p92f-tnpn-ysqp-7vrf-5f"
                },
                {
                    "data": {
                        "id": "2202",
                        "type": "dynamicDataSource"
                    },
                    "dataSourceProvider": "tomcat",
                    "type": "dynamicDataSource",
                    "permissionLevel": 5,
                    "driver": "org.postgresql.Driver",
                    "name": "SampleTravelData",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Postgresql",
                    "keyPath": "8u0p-mniy-pkds-0s56-kf/1wm4-f7r9-5hsb-mbl1-2t",
                    "key": "1wm4-f7r9-5hsb-mbl1-2t",
                    "uuid": "1wm4-f7r9-5hsb-mbl1-2t"
                }
            ],
            "key": "8u0p-mniy-pkds-0s56-kf",
            "uuid": "8u0p-mniy-pkds-0s56-kf",
            "keyPath": "8u0p-mniy-pkds-0s56-kf",
            "category": "dsGroup",
            "imgUrl": "images/data_sources/defaut_datasource.png"
        },
        {
            "name": "Snowflake Client",
            "children": [
                {
                    "data": {
                        "id": "1901",
                        "type": "dynamicDataSource"
                    },
                    "dataSourceProvider": "tomcat",
                    "type": "dynamicDataSource",
                    "permissionLevel": 5,
                    "driver": "net.snowflake.client.jdbc.SnowflakeDriver",
                    "name": "SnowflakeDS",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Snowflake Client",
                    "keyPath": "ttmi-ucrw-x2k1-ycu2-ti/ifwo-zbky-d9yq-lr7i-48",
                    "key": "ifwo-zbky-d9yq-lr7i-48",
                    "uuid": "ifwo-zbky-d9yq-lr7i-48"
                },
                {
                    "data": {
                        "id": "1902",
                        "type": "dynamicDataSource"
                    },
                    "dataSourceProvider": "tomcat",
                    "type": "dynamicDataSource",
                    "permissionLevel": 5,
                    "driver": "net.snowflake.client.jdbc.SnowflakeDriver",
                    "name": "SnowFlakeDSWorking",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Snowflake Client",
                    "keyPath": "ttmi-ucrw-x2k1-ycu2-ti/g3kw-lbxt-5skf-8wko-n2",
                    "key": "g3kw-lbxt-5skf-8wko-n2",
                    "uuid": "g3kw-lbxt-5skf-8wko-n2"
                },
                {
                    "data": {
                        "id": "2403",
                        "type": "dynamicDataSource"
                    },
                    "dataSourceProvider": "tomcat",
                    "type": "dynamicDataSource",
                    "permissionLevel": 5,
                    "driver": "net.snowflake.client.jdbc.SnowflakeDriver",
                    "name": "testdata",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Snowflake Client",
                    "keyPath": "ttmi-ucrw-x2k1-ycu2-ti/01be-e6gu-xebr-sng7-la",
                    "key": "01be-e6gu-xebr-sng7-la",
                    "uuid": "01be-e6gu-xebr-sng7-la"
                },
                {
                    "data": {
                        "id": "2501",
                        "type": "dynamicDataSource"
                    },
                    "dataSourceProvider": "tomcat",
                    "type": "dynamicDataSource",
                    "permissionLevel": 5,
                    "driver": "net.snowflake.client.jdbc.SnowflakeDriver",
                    "name": "60045",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Snowflake Client",
                    "keyPath": "ttmi-ucrw-x2k1-ycu2-ti/lafx-x24p-zhox-ziyk-oy",
                    "key": "lafx-x24p-zhox-ziyk-oy",
                    "uuid": "lafx-x24p-zhox-ziyk-oy"
                }
            ],
            "key": "ttmi-ucrw-x2k1-ycu2-ti",
            "uuid": "ttmi-ucrw-x2k1-ycu2-ti",
            "keyPath": "ttmi-ucrw-x2k1-ycu2-ti",
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
                    "keyPath": "fsx8-87ek-4mnd-3ka1-uf/iof0-ssvu-t9ad-30kn-z4",
                    "key": "iof0-ssvu-t9ad-30kn-z4",
                    "uuid": "iof0-ssvu-t9ad-30kn-z4"
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
                    "keyPath": "fsx8-87ek-4mnd-3ka1-uf/tcyo-lzh0-xhbf-99vp-o9",
                    "key": "tcyo-lzh0-xhbf-99vp-o9",
                    "uuid": "tcyo-lzh0-xhbf-99vp-o9"
                }
            ],
            "key": "fsx8-87ek-4mnd-3ka1-uf",
            "uuid": "fsx8-87ek-4mnd-3ka1-uf",
            "keyPath": "fsx8-87ek-4mnd-3ka1-uf",
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
                "dsUUID": "ce1v-rief-wrir-v1iu-fn",
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
                        "connId": "ljb4p",
                        "dbId": "ljb4p",
                        "classifier": "db.workflow",
                        "datasourceName": "SampleTravelDataDerby",
                        "dsKeyPath": "q101-dsy6-xmgq-fpyy-6u/ce1v-rief-wrir-v1iu-fn/h5bi-dzjg-80fo-qznc-25",
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
            "connId": "ljb4p",
            "dbId": "ljb4p",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelDataDerby",
            "dsKeyPath": "q101-dsy6-xmgq-fpyy-6u/ce1v-rief-wrir-v1iu-fn/h5bi-dzjg-80fo-qznc-25",
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
            "keyPath": "q101-dsy6-xmgq-fpyy-6u/ce1v-rief-wrir-v1iu-fn/h5bi-dzjg-80fo-qznc-25/ersh-ffr4-jsjf-tjlq-22",
            "key": "ersh-ffr4-jsjf-tjlq-22",
            "alias": "dimdate",
            "uuid": "ersh-ffr4-jsjf-tjlq-22",
            "connId": "ljb4p",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "ljb4p",
                "dbId": "ljb4p",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "q101-dsy6-xmgq-fpyy-6u/ce1v-rief-wrir-v1iu-fn/h5bi-dzjg-80fo-qznc-25",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "dimdate_ljb4p",
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
            "keyPath": "q101-dsy6-xmgq-fpyy-6u/ce1v-rief-wrir-v1iu-fn/h5bi-dzjg-80fo-qznc-25/j6el-ig17-eyxa-bkib-66",
            "key": "j6el-ig17-eyxa-bkib-66",
            "alias": "employee_details",
            "uuid": "j6el-ig17-eyxa-bkib-66",
            "connId": "ljb4p",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "ljb4p",
                "dbId": "ljb4p",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "q101-dsy6-xmgq-fpyy-6u/ce1v-rief-wrir-v1iu-fn/h5bi-dzjg-80fo-qznc-25",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "employee_details_ljb4p",
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
            "keyPath": "q101-dsy6-xmgq-fpyy-6u/ce1v-rief-wrir-v1iu-fn/h5bi-dzjg-80fo-qznc-25/lm66-jvsq-xyrk-lpev-t6",
            "key": "lm66-jvsq-xyrk-lpev-t6",
            "alias": "geo_cordinates",
            "uuid": "lm66-jvsq-xyrk-lpev-t6",
            "connId": "ljb4p",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "ljb4p",
                "dbId": "ljb4p",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "q101-dsy6-xmgq-fpyy-6u/ce1v-rief-wrir-v1iu-fn/h5bi-dzjg-80fo-qznc-25",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "geo_cordinates_ljb4p",
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
            "keyPath": "q101-dsy6-xmgq-fpyy-6u/ce1v-rief-wrir-v1iu-fn/h5bi-dzjg-80fo-qznc-25/tye4-14f5-kmis-4c1n-jt",
            "key": "tye4-14f5-kmis-4c1n-jt",
            "alias": "meeting_details",
            "uuid": "tye4-14f5-kmis-4c1n-jt",
            "connId": "ljb4p",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "ljb4p",
                "dbId": "ljb4p",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "q101-dsy6-xmgq-fpyy-6u/ce1v-rief-wrir-v1iu-fn/h5bi-dzjg-80fo-qznc-25",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "meeting_details_ljb4p",
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
            "keyPath": "q101-dsy6-xmgq-fpyy-6u/ce1v-rief-wrir-v1iu-fn/h5bi-dzjg-80fo-qznc-25/i5oc-lrz6-y4wp-fhtf-y9",
            "key": "i5oc-lrz6-y4wp-fhtf-y9",
            "alias": "travel_details",
            "uuid": "i5oc-lrz6-y4wp-fhtf-y9",
            "connId": "ljb4p",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "ljb4p",
                "dbId": "ljb4p",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "q101-dsy6-xmgq-fpyy-6u/ce1v-rief-wrir-v1iu-fn/h5bi-dzjg-80fo-qznc-25",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "travel_details_ljb4p",
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
                    "category": "column",
                    "name": "travel_id",
                    "originalName": "travel_id",
                    "originalId": "ff18ed50-d74b-4c79-b4ff-3d3648bd3a91",
                    "tableKey": "travel_details",
                    "connId": "ljb4p",
                    "columnKey": "travel_id",
                    "_columnId": "ff18ed50-d74b-4c79-b4ff-3d3648bd3a91",
                    "uuid": "hg22-yrob-ptyj-y2rg-5h",
                    "tableId": "8a28627d07d04ef096d9935f12e0c7e9",
                    "tableUuid": "i5oc-lrz6-y4wp-fhtf-y9",
                    "tableAlias": "travel_details",
                    "tableName": "travel_details",
                    "dbId": "ljb4p"
                },
                "travel_date": {
                    "alias": "travel_date",
                    "fullyQualifiedColumn": "travel_details.travel_date",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.sql.Timestamp": "dateTime"
                    },
                    "category": "column",
                    "name": "travel_date",
                    "originalName": "travel_date",
                    "originalId": "2d05368e-7931-4688-b3bf-e3bb6228df24",
                    "tableKey": "travel_details",
                    "connId": "ljb4p",
                    "columnKey": "travel_date",
                    "_columnId": "2d05368e-7931-4688-b3bf-e3bb6228df24",
                    "uuid": "5shw-dx35-age0-1b7u-ve",
                    "tableId": "8a28627d07d04ef096d9935f12e0c7e9",
                    "tableUuid": "i5oc-lrz6-y4wp-fhtf-y9",
                    "tableAlias": "travel_details",
                    "tableName": "travel_details",
                    "dbId": "ljb4p"
                },
                "travel_type": {
                    "alias": "travel_type",
                    "fullyQualifiedColumn": "travel_details.travel_type",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "name": "travel_type",
                    "originalName": "travel_type",
                    "originalId": "e17d1d12-2855-4ad3-ab5b-b4598be46c62",
                    "tableKey": "travel_details",
                    "connId": "ljb4p",
                    "columnKey": "travel_type",
                    "_columnId": "e17d1d12-2855-4ad3-ab5b-b4598be46c62",
                    "uuid": "tsw2-m6bx-9zxg-lssd-2k",
                    "tableId": "8a28627d07d04ef096d9935f12e0c7e9",
                    "tableUuid": "i5oc-lrz6-y4wp-fhtf-y9",
                    "tableAlias": "travel_details",
                    "tableName": "travel_details",
                    "dbId": "ljb4p"
                },
                "travel_medium": {
                    "alias": "travel_medium",
                    "fullyQualifiedColumn": "travel_details.travel_medium",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "name": "travel_medium",
                    "originalName": "travel_medium",
                    "originalId": "0a5e0574-5449-4793-861c-416569422dac",
                    "tableKey": "travel_details",
                    "connId": "ljb4p",
                    "columnKey": "travel_medium",
                    "_columnId": "0a5e0574-5449-4793-861c-416569422dac",
                    "uuid": "a5d9-u6by-18k7-kj1h-o8",
                    "tableId": "8a28627d07d04ef096d9935f12e0c7e9",
                    "tableUuid": "i5oc-lrz6-y4wp-fhtf-y9",
                    "tableAlias": "travel_details",
                    "tableName": "travel_details",
                    "dbId": "ljb4p"
                },
                "source_id": {
                    "alias": "test",
                    "fullyQualifiedColumn": "travel_details.source_id",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    },
                    "category": "column",
                    "name": "source_id",
                    "originalName": "source_id",
                    "originalId": "ebe3e492-b284-459c-b9c9-47c2d7eb892d",
                    "tableKey": "travel_details",
                    "connId": "ljb4p",
                    "columnKey": "source_id",
                    "_columnId": "ebe3e492-b284-459c-b9c9-47c2d7eb892d",
                    "uuid": "dall-r2jq-vxkp-eu5h-wz",
                    "tableId": "8a28627d07d04ef096d9935f12e0c7e9",
                    "tableUuid": "i5oc-lrz6-y4wp-fhtf-y9",
                    "tableAlias": "travel_details",
                    "tableName": "travel_details",
                    "dbId": "ljb4p"
                },
                "source": {
                    "alias": "source",
                    "fullyQualifiedColumn": "travel_details.source",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "name": "source",
                    "originalName": "source",
                    "originalId": "928df4ab-6c04-4db4-acb1-01729371197f",
                    "tableKey": "travel_details",
                    "connId": "ljb4p",
                    "columnKey": "source",
                    "_columnId": "928df4ab-6c04-4db4-acb1-01729371197f",
                    "uuid": "ghlg-p7tr-ryex-nffu-yi",
                    "tableId": "8a28627d07d04ef096d9935f12e0c7e9",
                    "tableUuid": "i5oc-lrz6-y4wp-fhtf-y9",
                    "tableAlias": "travel_details",
                    "tableName": "travel_details",
                    "dbId": "ljb4p"
                },
                "destination_id": {
                    "alias": "destination_id",
                    "fullyQualifiedColumn": "travel_details.destination_id",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    },
                    "category": "column",
                    "name": "destination_id",
                    "originalName": "destination_id",
                    "originalId": "f74f5552-5419-4714-abee-76556ff8b3fe",
                    "tableKey": "travel_details",
                    "connId": "ljb4p",
                    "columnKey": "destination_id",
                    "_columnId": "f74f5552-5419-4714-abee-76556ff8b3fe",
                    "uuid": "6zyq-s751-wvh2-v6fm-rs",
                    "tableId": "8a28627d07d04ef096d9935f12e0c7e9",
                    "tableUuid": "i5oc-lrz6-y4wp-fhtf-y9",
                    "tableAlias": "travel_details",
                    "tableName": "travel_details",
                    "dbId": "ljb4p"
                },
                "destination": {
                    "alias": "destination",
                    "fullyQualifiedColumn": "travel_details.destination",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "name": "destination",
                    "originalName": "destination",
                    "originalId": "f47ad98e-d399-4099-b7f3-052ce75fb224",
                    "tableKey": "travel_details",
                    "connId": "ljb4p",
                    "columnKey": "destination",
                    "_columnId": "f47ad98e-d399-4099-b7f3-052ce75fb224",
                    "uuid": "1whm-fqoh-8gy7-kcbh-sm",
                    "tableId": "8a28627d07d04ef096d9935f12e0c7e9",
                    "tableUuid": "i5oc-lrz6-y4wp-fhtf-y9",
                    "tableAlias": "travel_details",
                    "tableName": "travel_details",
                    "dbId": "ljb4p"
                },
                "travel_cost": {
                    "alias": "travel_cost",
                    "fullyQualifiedColumn": "travel_details.travel_cost",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    },
                    "category": "column",
                    "name": "travel_cost",
                    "originalName": "travel_cost",
                    "originalId": "8f4ed0fe-1e78-49c6-bc50-1a19fc380ab0",
                    "tableKey": "travel_details",
                    "connId": "ljb4p",
                    "columnKey": "travel_cost",
                    "_columnId": "8f4ed0fe-1e78-49c6-bc50-1a19fc380ab0",
                    "uuid": "g7ch-x7qd-b5c7-hgn6-et",
                    "tableId": "8a28627d07d04ef096d9935f12e0c7e9",
                    "tableUuid": "i5oc-lrz6-y4wp-fhtf-y9",
                    "tableAlias": "travel_details",
                    "tableName": "travel_details",
                    "dbId": "ljb4p"
                },
                "mode_of_payment": {
                    "alias": "mode_of_payment",
                    "fullyQualifiedColumn": "travel_details.mode_of_payment",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "name": "mode_of_payment",
                    "originalName": "mode_of_payment",
                    "originalId": "fc747712-8e84-45b7-aff9-58313cd95cdc",
                    "tableKey": "travel_details",
                    "connId": "ljb4p",
                    "columnKey": "mode_of_payment",
                    "_columnId": "fc747712-8e84-45b7-aff9-58313cd95cdc",
                    "uuid": "6e15-b4ya-j9pj-h94c-s6",
                    "tableId": "8a28627d07d04ef096d9935f12e0c7e9",
                    "tableUuid": "i5oc-lrz6-y4wp-fhtf-y9",
                    "tableAlias": "travel_details",
                    "tableName": "travel_details",
                    "dbId": "ljb4p"
                },
                "booking_platform": {
                    "alias": "booking_platform",
                    "fullyQualifiedColumn": "travel_details.booking_platform",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "name": "booking_platform",
                    "originalName": "booking_platform",
                    "originalId": "5f8d3619-e473-425d-a33c-4676e715c0ef",
                    "tableKey": "travel_details",
                    "connId": "ljb4p",
                    "columnKey": "booking_platform",
                    "_columnId": "5f8d3619-e473-425d-a33c-4676e715c0ef",
                    "uuid": "rkww-xn1j-zsqd-1l4o-o7",
                    "tableId": "8a28627d07d04ef096d9935f12e0c7e9",
                    "tableUuid": "i5oc-lrz6-y4wp-fhtf-y9",
                    "tableAlias": "travel_details",
                    "tableName": "travel_details",
                    "dbId": "ljb4p"
                },
                "travelled_by": {
                    "alias": "travelled_by",
                    "fullyQualifiedColumn": "travel_details.travelled_by",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    },
                    "category": "column",
                    "name": "travelled_by",
                    "originalName": "travelled_by",
                    "originalId": "373c28d6-7f8a-4b74-8f9f-9cae90c88ed4",
                    "tableKey": "travel_details",
                    "connId": "ljb4p",
                    "columnKey": "travelled_by",
                    "_columnId": "373c28d6-7f8a-4b74-8f9f-9cae90c88ed4",
                    "uuid": "mx5k-s27q-iusa-1a0s-uq",
                    "tableId": "8a28627d07d04ef096d9935f12e0c7e9",
                    "tableUuid": "i5oc-lrz6-y4wp-fhtf-y9",
                    "tableAlias": "travel_details",
                    "tableName": "travel_details",
                    "dbId": "ljb4p"
                }
            },
            "columnsFetched": true
        }
    },
    "views": [],
    "activeView": false,
    "categories": {
        "q101-dsy6-xmgq-fpyy-6u": {
            "ds": {
                "permissionLevel": 5,
                "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                "name": "Plain Jdbc",
                "classifier": "efwd",
                "type": "sql.jdbc",
                "data": {
                    "dir": "datasources",
                    "driverName": "org.apache.derby.jdbc.AutoloadedDriver",
                    "type": "sql.jdbc",
                    "id": 501,
                    "userName": "hiuser",
                    "password": "hiuser",
                    "jdbcUrl": "jdbc:derby:/home/helical/Performance/hi/db/SampleTravelData"
                },
                "dataSourceType": "Plain Jdbc DataSource"
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
        "7nrx-7gl0-1fp0-hjxj-sp": {
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
        "z21a-nadg-9ddp-wwyg-h6": {
            "ds": {
                "data": {
                    "id": "2402",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "com.mysql.jdbc.Driver",
                "name": "mysql",
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
        "1ps2-uox4-vtpv-duac-i3": {
            "ds": {
                "data": {
                    "id": "2001",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "oracle.jdbc.OracleDriver",
                "name": "Oracle_12",
                "classifier": "global",
                "dataSourceType": "Managed DataSource"
            },
            "category": {
                "driver": "oracle.jdbc.OracleDriver",
                "databaseDialect": "oracle",
                "name": "Oracle",
                "categoryName": "RDBMS",
                "categoryType": "rdbms",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png",
                "url": "jdbc:oracle:thin:@{{hostName}}:{{port}}:{{database}}",
                "parameters": {
                    "port": "1521",
                    "hostName": "localhost",
                    "database": "database"
                }
            }
        },
        "8u0p-mniy-pkds-0s56-kf": {
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
        "ttmi-ucrw-x2k1-ycu2-ti": {
            "ds": {
                "data": {
                    "id": "1901",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "net.snowflake.client.jdbc.SnowflakeDriver",
                "name": "SnowflakeDS",
                "classifier": "global",
                "dataSourceType": "Managed DataSource"
            },
            "category": {
                "driver": "net.snowflake.client.jdbc.SnowflakeDriver",
                "databaseDialect": "snowflake",
                "name": "Snowflake Client",
                "categoryName": "RDBMS",
                "categoryType": "rdbms",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png",
                "url": "jdbc:snowflake://{{hostName}}",
                "parameters": {
                    "hostName": "localhost"
                }
            }
        },
        "fsx8-87ek-4mnd-3ka1-uf": {
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
            "connId": "ljb4p",
            "dbId": "ljb4p",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelDataDerby",
            "dsKeyPath": "q101-dsy6-xmgq-fpyy-6u/ce1v-rief-wrir-v1iu-fn/h5bi-dzjg-80fo-qznc-25",
            "driverType": "Derby",
            "database": "HIUSER"
        }
    ],
    "changeDSList": {},
    "changedTables": [],
    "changedColumns": [
        {
            "alias": "test",
            "columnId": "ebe3e492-b284-459c-b9c9-47c2d7eb892d",
            "connId": "ljb4p",
            "tableId": "8a28627d07d04ef096d9935f12e0c7e9",
            "aliasChanged": true
        }
    ],
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
            "id": "45ee06374e9d68c4a841d57c1be69f22_1675847966062",
            "type": "inner",
            "operator": "=",
            "left": {
                "table": "geo_cordinates",
                "column": "location_id",
                "tableId": "be534112989b616b194bc59c2fb25a42",
                "dbId": "ljb4p",
                "connId": "ljb4p",
                "columnAlias": "location_id",
                "tableAlias": "geo_cordinates",
                "tableUuid": "lm66-jvsq-xyrk-lpev-t6"
            },
            "right": {
                "table": "dimdate",
                "column": "dim_id",
                "tableId": "4ac5d9f68b58bd7c0d179146e46795be",
                "connId": "ljb4p",
                "dbId": "ljb4p",
                "columnAlias": "dim_id",
                "tableAlias": "dimdate",
                "tableUuid": "ersh-ffr4-jsjf-tjlq-22"
            },
            "uuid": "wdsz-6id4-xzm3-o7pb-ef",
            "index": 1,
            "action": "noChange"
        },
        {
            "id": "c113fa06a79370db6feb443c0023f531_1675847966062",
            "type": "inner",
            "operator": "=",
            "left": {
                "table": "employee_details",
                "column": "employee_id",
                "tableId": "4e1fd245f4d13b77be423a43f01d80b2",
                "dbId": "ljb4p",
                "connId": "ljb4p",
                "columnAlias": "employee_id",
                "tableAlias": "employee_details",
                "tableUuid": "j6el-ig17-eyxa-bkib-66"
            },
            "right": {
                "table": "meeting_details",
                "column": "meeting_by",
                "tableId": "9645c648a1c0dbeec1287aaf1e996db3",
                "connId": "ljb4p",
                "dbId": "ljb4p",
                "columnAlias": "meeting_by",
                "tableAlias": "meeting_details",
                "tableUuid": "tye4-14f5-kmis-4c1n-jt"
            },
            "uuid": "ehqk-a5iy-i3so-nlgr-xv",
            "index": 2,
            "action": "noChange"
        },
        {
            "id": "55a5cfab25247c48f9776bf9bd457a3c_1675847966062",
            "type": "inner",
            "operator": "=",
            "left": {
                "table": "employee_details",
                "column": "employee_id",
                "tableId": "4e1fd245f4d13b77be423a43f01d80b2",
                "dbId": "ljb4p",
                "connId": "ljb4p",
                "columnAlias": "employee_id",
                "tableAlias": "employee_details",
                "tableUuid": "j6el-ig17-eyxa-bkib-66"
            },
            "right": {
                "table": "travel_details",
                "column": "travelled_by",
                "tableId": "8a28627d07d04ef096d9935f12e0c7e9",
                "connId": "ljb4p",
                "dbId": "ljb4p",
                "columnAlias": "travelled_by",
                "tableAlias": "travel_details",
                "tableUuid": "i5oc-lrz6-y4wp-fhtf-y9",
                "columnUuid": "mx5k-s27q-iusa-1a0s-uq"
            },
            "uuid": "mm5r-e0vm-x71s-u8zy-20",
            "index": 3,
            "action": "noChange"
        },
        {
            "id": "aa85f3fbafd188679f5b9da8797d9ec9_1675847966062",
            "type": "inner",
            "operator": "=",
            "left": {
                "table": "geo_cordinates",
                "column": "location_id",
                "tableId": "be534112989b616b194bc59c2fb25a42",
                "dbId": "ljb4p",
                "connId": "ljb4p",
                "columnAlias": "location_id",
                "tableAlias": "geo_cordinates",
                "tableUuid": "lm66-jvsq-xyrk-lpev-t6"
            },
            "right": {
                "table": "travel_details",
                "column": "destination_id",
                "tableId": "8a28627d07d04ef096d9935f12e0c7e9",
                "connId": "ljb4p",
                "dbId": "ljb4p",
                "columnAlias": "destination_id",
                "tableAlias": "travel_details",
                "tableUuid": "i5oc-lrz6-y4wp-fhtf-y9",
                "columnUuid": "6zyq-s751-wvh2-v6fm-rs"
            },
            "uuid": "24sp-3wuf-l8i9-dz47-d7",
            "index": 4,
            "action": "noChange"
        },
        {
            "id": "1f3619f6ae1549d8a8d89c7b7466af22_1675847966062",
            "type": "inner",
            "operator": "=",
            "left": {
                "table": "geo_cordinates",
                "column": "location_id",
                "tableId": "be534112989b616b194bc59c2fb25a42",
                "dbId": "ljb4p",
                "connId": "ljb4p",
                "columnAlias": "location_id",
                "tableAlias": "geo_cordinates",
                "tableUuid": "lm66-jvsq-xyrk-lpev-t6"
            },
            "right": {
                "table": "travel_details",
                "column": "source_id",
                "tableId": "8a28627d07d04ef096d9935f12e0c7e9",
                "connId": "ljb4p",
                "dbId": "ljb4p",
                "columnAlias": "test",
                "tableAlias": "travel_details",
                "tableUuid": "i5oc-lrz6-y4wp-fhtf-y9",
                "columnUuid": "dall-r2jq-vxkp-eu5h-wz"
            },
            "uuid": "2x59-qvnl-ecxr-pav8-0x",
            "index": 5,
            "action": "noChange"
        },
        {
            "id": "45ee06374e9d68c4a841d57c1be69f22_1675851276407",
            "type": "inner",
            "operator": "=",
            "left": {
                "table": "geo_cordinates",
                "column": "location_id",
                "tableId": "be534112989b616b194bc59c2fb25a42",
                "dbId": "ljb4p",
                "connId": "ljb4p",
                "columnAlias": "location_id",
                "tableAlias": "geo_cordinates",
                "tableUuid": "lm66-jvsq-xyrk-lpev-t6"
            },
            "right": {
                "table": "dimdate",
                "column": "dim_id",
                "tableId": "4ac5d9f68b58bd7c0d179146e46795be",
                "connId": "ljb4p",
                "dbId": "ljb4p",
                "columnAlias": "dim_id",
                "tableAlias": "dimdate",
                "tableUuid": "ersh-ffr4-jsjf-tjlq-22"
            },
            "uuid": "jjp9-8ot9-rwe4-en47-yv",
            "index": 6,
            "action": "noChange"
        }
    ],
    "mode": "create",
    "allTablesKeys": [
        "lm66-jvsq-xyrk-lpev-t6",
        "tye4-14f5-kmis-4c1n-jt",
        "j6el-ig17-eyxa-bkib-66",
        "ersh-ffr4-jsjf-tjlq-22",
        "i5oc-lrz6-y4wp-fhtf-y9"
    ],
    "selectedTableKeys": [
        "ersh-ffr4-jsjf-tjlq-22",
        "j6el-ig17-eyxa-bkib-66",
        "lm66-jvsq-xyrk-lpev-t6",
        "tye4-14f5-kmis-4c1n-jt",
        "i5oc-lrz6-y4wp-fhtf-y9"
    ],
    "metadataName": "Metadata_1",
    "activeDataSource": false,
    "metadataToEdit": false,
    "isSavingInProgress": false,
    "editViewsTempData": {},
    "inititalStateFromJest": false,
    "timeStamp": 1675852038078,
    "initialEditResponse": false,
    "editorFullView": false,
    "selectedTableOrColumnKey": {
        "category": "column",
        "tableName": "travel_details",
        "key": "source_id"
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
        "dbId": "ljb4p",
        "value": "source_id"
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
        "travel_details.source_id"
    ],
    "hasUnsavedData": true,
    "getSecurityTableData": {
        "tables": [],
        "columns": []
    },
    "doResetFormData": false,
    "tablesMergeType": false,
    "activeDsInfoId": "ljb4p",
    "setMetadataLoading": {
        "isDataSourceFetched": true
    }
}

export const fetchedData = {
    "classifier": "db.generic",
    "name": "HIUSER",
    "dataSource": {
        "sync": false,
        "id": "1",
        "catSchemaPredicted": false,
        "catalog": "",
        "schema": "HIUSER",
        "type": "dynamicDataSource",
        "baseType": "global.jdbc",
        "dbId": "8127"
    },
    "uniqueId": "Metadata_1888",
    "tables": {
        "dimdate": {
            "id": "4ac5d9f68b58bd7c0d179146e46795be",
            "alias": "dimdate",
            "columns": {
                "dim_id": {
                    "alias": "dim_id",
                    "fullyQualifiedColumn": "dimdate.dim_id",
                    "columnId": "a1d7cf15-2995-4d14-8eb1-602c7114f277",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    }
                },
                "fiscal_year": {
                    "alias": "fiscal_year",
                    "fullyQualifiedColumn": "dimdate.fiscal_year",
                    "columnId": "e481975c-0afc-44d0-ba54-14ea0562305a",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.sql.Date": "date"
                    }
                },
                "modified_date": {
                    "alias": "modified_date",
                    "fullyQualifiedColumn": "dimdate.modified_date",
                    "columnId": "d3d0040b-9809-411e-a099-599672949ff9",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.sql.Timestamp": "dateTime"
                    }
                },
                "date_key": {
                    "alias": "date_key",
                    "fullyQualifiedColumn": "dimdate.date_key",
                    "columnId": "a81e7aa8-0b0f-475f-8705-b6a8ab16b30b",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "day_number": {
                    "alias": "day_number",
                    "fullyQualifiedColumn": "dimdate.day_number",
                    "columnId": "aa53946e-d835-4f28-b061-d5004c4edd1e",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "fiscal_month_name": {
                    "alias": "fiscal_month_name",
                    "fullyQualifiedColumn": "dimdate.fiscal_month_name",
                    "columnId": "c1ff31d8-503c-45cf-b710-b39b88ae804d",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "fiscal_month_label": {
                    "alias": "fiscal_month_label",
                    "fullyQualifiedColumn": "dimdate.fiscal_month_label",
                    "columnId": "59ddd853-bfec-4697-8a89-fc4740d6670f",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "created_date": {
                    "alias": "created_date",
                    "fullyQualifiedColumn": "dimdate.created_date",
                    "columnId": "43156ab8-98bd-4902-a615-1f651331507c",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "created_time": {
                    "alias": "created_time",
                    "fullyQualifiedColumn": "dimdate.created_time",
                    "columnId": "3b48a7e5-b325-4b1a-b770-ab15dd4449ed",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "rating": {
                    "alias": "rating",
                    "fullyQualifiedColumn": "dimdate.rating",
                    "columnId": "8e8d1a32-53ac-4700-a701-02b42c317fbe",
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
                    "columnId": "33f3331f-7f1f-472a-b949-c9bb506a2227",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    }
                },
                "employee_name": {
                    "alias": "employee_name",
                    "fullyQualifiedColumn": "employee_details.employee_name",
                    "columnId": "b0aed187-759f-4142-96f2-91c051288717",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "age": {
                    "alias": "age",
                    "fullyQualifiedColumn": "employee_details.age",
                    "columnId": "bed269e4-1f63-4e44-8f10-bf6cd7674f16",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    }
                },
                "address": {
                    "alias": "address",
                    "fullyQualifiedColumn": "employee_details.address",
                    "columnId": "48bbdf1b-d10d-4fed-9a8c-eff493a75276",
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
                    "columnId": "1e909f09-9bd4-4dfc-88f5-52ecd5415aa9",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    }
                },
                "location": {
                    "alias": "location",
                    "fullyQualifiedColumn": "geo_cordinates.location",
                    "columnId": "377f5d06-9d41-4e30-876f-e8ad098ed245",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "latitude": {
                    "alias": "latitude",
                    "fullyQualifiedColumn": "geo_cordinates.latitude",
                    "columnId": "26582292-540c-42ee-a97f-1c33fee9ddd7",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Double": "numeric"
                    }
                },
                "longitude": {
                    "alias": "longitude",
                    "fullyQualifiedColumn": "geo_cordinates.longitude",
                    "columnId": "df61ede5-a554-4d75-97ed-49295c9adedb",
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
                    "columnId": "d18861fc-ba5b-4608-82e9-f93d1318bcb1",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    }
                },
                "meeting_date": {
                    "alias": "meeting_date",
                    "fullyQualifiedColumn": "meeting_details.meeting_date",
                    "columnId": "de87003e-a351-4ae4-a5a4-f6e1123af5d5",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.sql.Timestamp": "dateTime"
                    }
                },
                "meeting_by": {
                    "alias": "meeting_by",
                    "fullyQualifiedColumn": "meeting_details.meeting_by",
                    "columnId": "0c16c8d0-817a-414d-8ad2-8f0e496ef991",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    }
                },
                "client_name": {
                    "alias": "client_name",
                    "fullyQualifiedColumn": "meeting_details.client_name",
                    "columnId": "b40f8387-7b56-4c84-a30b-3686281b7313",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "meeting_purpose": {
                    "alias": "meeting_purpose",
                    "fullyQualifiedColumn": "meeting_details.meeting_purpose",
                    "columnId": "f923f8b9-e7de-4ff8-868b-ccd93b3dbee2",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "meeting_impact": {
                    "alias": "meeting_impact",
                    "fullyQualifiedColumn": "meeting_details.meeting_impact",
                    "columnId": "00bcd9a9-f009-4875-840a-e1a8b58d437a",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "meet_cancellation_status": {
                    "alias": "meet_cancellation_status",
                    "fullyQualifiedColumn": "meeting_details.meet_cancellation_status",
                    "columnId": "b2e48c75-fdfa-4d2b-b21a-98bdb498e873",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "cancellation_reason": {
                    "alias": "cancellation_reason",
                    "fullyQualifiedColumn": "meeting_details.cancellation_reason",
                    "columnId": "97b1e1ca-2c1c-4980-85f6-6f27d183ad13",
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
                    "columnId": "ff18ed50-d74b-4c79-b4ff-3d3648bd3a91",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    }
                },
                "travel_date": {
                    "alias": "travel_date",
                    "fullyQualifiedColumn": "travel_details.travel_date",
                    "columnId": "2d05368e-7931-4688-b3bf-e3bb6228df24",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.sql.Timestamp": "dateTime"
                    }
                },
                "travel_type": {
                    "alias": "travel_type",
                    "fullyQualifiedColumn": "travel_details.travel_type",
                    "columnId": "e17d1d12-2855-4ad3-ab5b-b4598be46c62",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "travel_medium": {
                    "alias": "travel_medium",
                    "fullyQualifiedColumn": "travel_details.travel_medium",
                    "columnId": "0a5e0574-5449-4793-861c-416569422dac",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "source_id": {
                    "alias": "test",
                    "fullyQualifiedColumn": "travel_details.source_id",
                    "columnId": "ebe3e492-b284-459c-b9c9-47c2d7eb892d",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    }
                },
                "source": {
                    "alias": "source",
                    "fullyQualifiedColumn": "travel_details.source",
                    "columnId": "928df4ab-6c04-4db4-acb1-01729371197f",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "destination_id": {
                    "alias": "destination_id",
                    "fullyQualifiedColumn": "travel_details.destination_id",
                    "columnId": "f74f5552-5419-4714-abee-76556ff8b3fe",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    }
                },
                "destination": {
                    "alias": "destination",
                    "fullyQualifiedColumn": "travel_details.destination",
                    "columnId": "f47ad98e-d399-4099-b7f3-052ce75fb224",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "travel_cost": {
                    "alias": "travel_cost",
                    "fullyQualifiedColumn": "travel_details.travel_cost",
                    "columnId": "8f4ed0fe-1e78-49c6-bc50-1a19fc380ab0",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    }
                },
                "mode_of_payment": {
                    "alias": "mode_of_payment",
                    "fullyQualifiedColumn": "travel_details.mode_of_payment",
                    "columnId": "fc747712-8e84-45b7-aff9-58313cd95cdc",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "booking_platform": {
                    "alias": "booking_platform",
                    "fullyQualifiedColumn": "travel_details.booking_platform",
                    "columnId": "5f8d3619-e473-425d-a33c-4676e715c0ef",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "travelled_by": {
                    "alias": "travelled_by",
                    "fullyQualifiedColumn": "travel_details.travelled_by",
                    "columnId": "373c28d6-7f8a-4b74-8f9f-9cae90c88ed4",
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
            "id": "8634",
            "type": "inner",
            "operator": "=",
            "left": {
                "table": "geo_cordinates",
                "column": "location_id",
                "alias": "geo_cordinates.location_id",
                "tableId": "be534112989b616b194bc59c2fb25a42",
                "dbId": "8127"
            },
            "right": {
                "table": "dimdate",
                "column": "dim_id",
                "alias": "dimdate.dim_id",
                "tableId": "4ac5d9f68b58bd7c0d179146e46795be",
                "dbId": "8127"
            }
        },
        {
            "id": "8635",
            "type": "inner",
            "operator": "=",
            "left": {
                "table": "employee_details",
                "column": "employee_id",
                "alias": "employee_details.employee_id",
                "tableId": "4e1fd245f4d13b77be423a43f01d80b2",
                "dbId": "8127"
            },
            "right": {
                "table": "meeting_details",
                "column": "meeting_by",
                "alias": "meeting_details.meeting_by",
                "tableId": "9645c648a1c0dbeec1287aaf1e996db3",
                "dbId": "8127"
            }
        },
        {
            "id": "8636",
            "type": "inner",
            "operator": "=",
            "left": {
                "table": "employee_details",
                "column": "employee_id",
                "alias": "employee_details.employee_id",
                "tableId": "4e1fd245f4d13b77be423a43f01d80b2",
                "dbId": "8127"
            },
            "right": {
                "table": "travel_details",
                "column": "travelled_by",
                "alias": "travel_details.travelled_by",
                "tableId": "8a28627d07d04ef096d9935f12e0c7e9",
                "dbId": "8127"
            }
        },
        {
            "id": "8637",
            "type": "inner",
            "operator": "=",
            "left": {
                "table": "geo_cordinates",
                "column": "location_id",
                "alias": "geo_cordinates.location_id",
                "tableId": "be534112989b616b194bc59c2fb25a42",
                "dbId": "8127"
            },
            "right": {
                "table": "travel_details",
                "column": "destination_id",
                "alias": "travel_details.destination_id",
                "tableId": "8a28627d07d04ef096d9935f12e0c7e9",
                "dbId": "8127"
            }
        },
        {
            "id": "8638",
            "type": "inner",
            "operator": "=",
            "left": {
                "table": "geo_cordinates",
                "column": "location_id",
                "alias": "geo_cordinates.location_id",
                "tableId": "be534112989b616b194bc59c2fb25a42",
                "dbId": "8127"
            },
            "right": {
                "table": "travel_details",
                "column": "source_id",
                "alias": "travel_details.test",
                "tableId": "8a28627d07d04ef096d9935f12e0c7e9",
                "dbId": "8127"
            }
        }
    ],
    "metadataName": "Metadata_1888",
    "metadataDir": "Sibtain"
}