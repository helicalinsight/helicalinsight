export const store5293 = {
    "dataFetchedFor": {
        "getDatasource": true,
        "joins": false,
        "viewSessionVariables": false,
        "listDataSources": true,
        "6prd-5fyw-d8ki-fhmf-vs": false
    },
    "loadingStatus": {
        "getDatasource": true,
        "listDataSources": true,
        "6prd-5fyw-d8ki-fhmf-vs": false
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
                    "keyPath": "3o2x-h5lc-ze3x-srye-oy/u2ua-83vv-fsrf-n1pi-eb",
                    "key": "u2ua-83vv-fsrf-n1pi-eb",
                    "uuid": "u2ua-83vv-fsrf-n1pi-eb"
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
                    "keyPath": "3o2x-h5lc-ze3x-srye-oy/4w45-idwo-4muo-bncw-53",
                    "key": "4w45-idwo-4muo-bncw-53",
                    "uuid": "4w45-idwo-4muo-bncw-53"
                }
            ],
            "key": "3o2x-h5lc-ze3x-srye-oy",
            "uuid": "3o2x-h5lc-ze3x-srye-oy",
            "keyPath": "3o2x-h5lc-ze3x-srye-oy",
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
                    "keyPath": "aver-423y-yhi2-4rld-p3/upqi-a373-f557-gn1l-hd",
                    "key": "upqi-a373-f557-gn1l-hd",
                    "uuid": "upqi-a373-f557-gn1l-hd"
                }
            ],
            "key": "aver-423y-yhi2-4rld-p3",
            "uuid": "aver-423y-yhi2-4rld-p3",
            "keyPath": "aver-423y-yhi2-4rld-p3",
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
                            "keyPath": "6wee-2s9f-ji7z-5wfd-zi/6prd-5fyw-d8ki-fhmf-vs/jnak-kzid-b328-t54r-3z",
                            "key": "jnak-kzid-b328-t54r-3z",
                            "alias": "dimdate",
                            "uuid": "jnak-kzid-b328-t54r-3z",
                            "connId": "vxhtx",
                            "dataSource": {
                                "id": "1100",
                                "type": "dynamicDataSource",
                                "baseType": "global.jdbc",
                                "catSchemaPredicted": false,
                                "sync": false,
                                "catalog": "",
                                "schema": "",
                                "connId": "vxhtx",
                                "classifier": "db.workflow",
                                "datasourceName": "sqliteDB",
                                "dsKeyPath": "6wee-2s9f-ji7z-5wfd-zi/6prd-5fyw-d8ki-fhmf-vs",
                                "driverType": "Sqlite",
                                "database": "sqliteDB"
                            },
                            "category": "table",
                            "nameWithConnId": "dimdate_vxhtx",
                            "database": "sqliteDB"
                        },
                        {
                            "id": "b161910cbebfd353351a6c0b46e6a02e",
                            "name": "employee_details",
                            "data": {
                                "id": "1100",
                                "type": "dynamicDataSource"
                            },
                            "keyPath": "6wee-2s9f-ji7z-5wfd-zi/6prd-5fyw-d8ki-fhmf-vs/was0-fv61-5r2i-4mqf-0u",
                            "key": "was0-fv61-5r2i-4mqf-0u",
                            "alias": "employee_details",
                            "uuid": "was0-fv61-5r2i-4mqf-0u",
                            "connId": "vxhtx",
                            "dataSource": {
                                "id": "1100",
                                "type": "dynamicDataSource",
                                "baseType": "global.jdbc",
                                "catSchemaPredicted": false,
                                "sync": false,
                                "catalog": "",
                                "schema": "",
                                "connId": "vxhtx",
                                "classifier": "db.workflow",
                                "datasourceName": "sqliteDB",
                                "dsKeyPath": "6wee-2s9f-ji7z-5wfd-zi/6prd-5fyw-d8ki-fhmf-vs",
                                "driverType": "Sqlite",
                                "database": "sqliteDB"
                            },
                            "category": "table",
                            "nameWithConnId": "employee_details_vxhtx",
                            "database": "sqliteDB"
                        },
                        {
                            "id": "f2ff93c37589ef57f40dcb15fda6d7ea",
                            "name": "geo_cordinates",
                            "data": {
                                "id": "1100",
                                "type": "dynamicDataSource"
                            },
                            "keyPath": "6wee-2s9f-ji7z-5wfd-zi/6prd-5fyw-d8ki-fhmf-vs/ou1r-x90v-9mk7-hrdg-7j",
                            "key": "ou1r-x90v-9mk7-hrdg-7j",
                            "alias": "geo_cordinates",
                            "uuid": "ou1r-x90v-9mk7-hrdg-7j",
                            "connId": "vxhtx",
                            "dataSource": {
                                "id": "1100",
                                "type": "dynamicDataSource",
                                "baseType": "global.jdbc",
                                "catSchemaPredicted": false,
                                "sync": false,
                                "catalog": "",
                                "schema": "",
                                "connId": "vxhtx",
                                "classifier": "db.workflow",
                                "datasourceName": "sqliteDB",
                                "dsKeyPath": "6wee-2s9f-ji7z-5wfd-zi/6prd-5fyw-d8ki-fhmf-vs",
                                "driverType": "Sqlite",
                                "database": "sqliteDB"
                            },
                            "category": "table",
                            "nameWithConnId": "geo_cordinates_vxhtx",
                            "database": "sqliteDB"
                        },
                        {
                            "id": "025fbfb381cb17d4519363c3585626fb",
                            "name": "meeting_details",
                            "data": {
                                "id": "1100",
                                "type": "dynamicDataSource"
                            },
                            "keyPath": "6wee-2s9f-ji7z-5wfd-zi/6prd-5fyw-d8ki-fhmf-vs/h7sp-061i-yaka-egn0-td",
                            "key": "h7sp-061i-yaka-egn0-td",
                            "alias": "meeting_details",
                            "uuid": "h7sp-061i-yaka-egn0-td",
                            "connId": "vxhtx",
                            "dataSource": {
                                "id": "1100",
                                "type": "dynamicDataSource",
                                "baseType": "global.jdbc",
                                "catSchemaPredicted": false,
                                "sync": false,
                                "catalog": "",
                                "schema": "",
                                "connId": "vxhtx",
                                "classifier": "db.workflow",
                                "datasourceName": "sqliteDB",
                                "dsKeyPath": "6wee-2s9f-ji7z-5wfd-zi/6prd-5fyw-d8ki-fhmf-vs",
                                "driverType": "Sqlite",
                                "database": "sqliteDB"
                            },
                            "category": "table",
                            "nameWithConnId": "meeting_details_vxhtx",
                            "database": "sqliteDB"
                        },
                        {
                            "id": "21e1b86ae9680d0fc197ed543c3e37eb",
                            "name": "travel_details",
                            "data": {
                                "id": "1100",
                                "type": "dynamicDataSource"
                            },
                            "keyPath": "6wee-2s9f-ji7z-5wfd-zi/6prd-5fyw-d8ki-fhmf-vs/od66-wv5v-al1p-7krz-ts",
                            "key": "od66-wv5v-al1p-7krz-ts",
                            "alias": "travel_details",
                            "uuid": "od66-wv5v-al1p-7krz-ts",
                            "connId": "vxhtx",
                            "dataSource": {
                                "id": "1100",
                                "type": "dynamicDataSource",
                                "baseType": "global.jdbc",
                                "catSchemaPredicted": false,
                                "sync": false,
                                "catalog": "",
                                "schema": "",
                                "connId": "vxhtx",
                                "classifier": "db.workflow",
                                "datasourceName": "sqliteDB",
                                "dsKeyPath": "6wee-2s9f-ji7z-5wfd-zi/6prd-5fyw-d8ki-fhmf-vs",
                                "driverType": "Sqlite",
                                "database": "sqliteDB"
                            },
                            "category": "table",
                            "nameWithConnId": "travel_details_vxhtx",
                            "database": "sqliteDB"
                        }
                    ],
                    "driverType": "Sqlite",
                    "keyPath": "6wee-2s9f-ji7z-5wfd-zi/6prd-5fyw-d8ki-fhmf-vs",
                    "key": "6prd-5fyw-d8ki-fhmf-vs",
                    "uuid": "6prd-5fyw-d8ki-fhmf-vs",
                    "fetched": true
                }
            ],
            "key": "6wee-2s9f-ji7z-5wfd-zi",
            "uuid": "6wee-2s9f-ji7z-5wfd-zi",
            "keyPath": "6wee-2s9f-ji7z-5wfd-zi",
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
                "dsUUID": "6prd-5fyw-d8ki-fhmf-vs",
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
                        "connId": "vxhtx",
                        "classifier": "db.workflow",
                        "datasourceName": "sqliteDB",
                        "dsKeyPath": "6wee-2s9f-ji7z-5wfd-zi/6prd-5fyw-d8ki-fhmf-vs",
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
            "id": "1100",
            "type": "dynamicDataSource",
            "baseType": "global.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "",
            "schema": "",
            "connId": "vxhtx",
            "classifier": "db.workflow",
            "datasourceName": "sqliteDB",
            "dsKeyPath": "6wee-2s9f-ji7z-5wfd-zi/6prd-5fyw-d8ki-fhmf-vs",
            "driverType": "Sqlite",
            "database": "sqliteDB"
        }
    ],
    "tables": {},
    "views": [],
    "activeView": false,
    "categories": {
        "3o2x-h5lc-ze3x-srye-oy": {
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
        "aver-423y-yhi2-4rld-p3": {
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
        "6wee-2s9f-ji7z-5wfd-zi": {
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
        "ou1r-x90v-9mk7-hrdg-7j",
        "h7sp-061i-yaka-egn0-td",
        "was0-fv61-5r2i-4mqf-0u",
        "jnak-kzid-b328-t54r-3z",
        "od66-wv5v-al1p-7krz-ts"
    ],
    "selectedTableKeys": [],
    "metadataName": "Metadata_1",
    "activeDataSource": false,
    "metadataToEdit": false,
    "isSavingInProgress": false,
    "editViewsTempData": {},
    "inititalStateFromJest": false,
    "timeStamp": 1661862635521,
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