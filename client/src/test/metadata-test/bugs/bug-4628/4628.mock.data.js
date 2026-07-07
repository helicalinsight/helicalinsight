export const mockReduxStoreData = {
    metadata: {
        "dataFetchedFor": {
            "getDatasource": true,
            "joins": false,
            "viewSessionVariables": false,
            "listDataSources": true,
            "vg91-ulji-ey2l-63zn-d0": false,
            "tuiv-s19f-q622-9l01-z3": false
        },
        "loadingStatus": {
            "getDatasource": true,
            "listDataSources": true,
            "vg91-ulji-ey2l-63zn-d0": false,
            "tuiv-s19f-q622-9l01-z3": false
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
                "driver": "com.helicalinsight.avro",
                "databaseDialect": "",
                "name": "Avro",
                "categoryName": "Flat Files",
                "categoryType": "flat_files",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "fileUpload": true,
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png"
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
                "driver": "com.helicalinsight.csv",
                "databaseDialect": "",
                "name": "Csv",
                "categoryName": "Flat Files",
                "categoryType": "flat_files",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "fileUpload": true,
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png"
            },
            {
                "driver": "com.helicalinsight.csvh",
                "databaseDialect": "",
                "name": "Csvh",
                "categoryName": "Flat Files",
                "categoryType": "flat_files",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "fileUpload": true,
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png"
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
                "driver": "com.helicalinsight.excel",
                "databaseDialect": "",
                "name": "Excel",
                "categoryName": "Flat Files",
                "categoryType": "flat_files",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "fileUpload": true,
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
                "driver": "com.helicalinsight.json",
                "databaseDialect": "",
                "name": "Json",
                "categoryName": "Flat Files",
                "categoryType": "flat_files",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "fileUpload": true,
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png"
            },
            {
                "driver": "com.helicalinsight.ltsv",
                "databaseDialect": "",
                "name": "Ltsv",
                "categoryName": "Flat Files",
                "categoryType": "flat_files",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "fileUpload": true,
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png"
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
                "driver": "com.helicalinsight.parquet",
                "databaseDialect": "",
                "name": "Parquet",
                "categoryName": "Flat Files",
                "categoryType": "flat_files",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "fileUpload": true,
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png"
            },
            {
                "driver": "com.helicalinsight.pcap",
                "databaseDialect": "",
                "name": "Pcap",
                "categoryName": "Flat Files",
                "categoryType": "flat_files",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "fileUpload": true,
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png"
            },
            {
                "driver": "com.helicalinsight.pcapng",
                "databaseDialect": "",
                "name": "Pcapng",
                "categoryName": "Flat Files",
                "categoryType": "flat_files",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "fileUpload": true,
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png"
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
                "driver": "com.helicalinsight.psv",
                "databaseDialect": "",
                "name": "Psv",
                "categoryName": "Flat Files",
                "categoryType": "flat_files",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "fileUpload": true,
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png"
            },
            {
                "driver": "com.helicalinsight.sequencefile",
                "databaseDialect": "",
                "name": "Sequencefile",
                "categoryName": "Flat Files",
                "categoryType": "flat_files",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "fileUpload": true,
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png"
            },
            {
                "driver": "com.helicalinsight.shp",
                "databaseDialect": "",
                "name": "Shp",
                "categoryName": "Flat Files",
                "categoryType": "flat_files",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "fileUpload": true,
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png"
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
                "driver": "com.helicalinsight.syslog",
                "databaseDialect": "",
                "name": "Syslog",
                "categoryName": "Flat Files",
                "categoryType": "flat_files",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "fileUpload": true,
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png"
            },
            {
                "name": "Teradata",
                "categoryType": "supported",
                "categoryName": "Supported"
            },
            {
                "driver": "com.helicalinsight.tsv",
                "databaseDialect": "",
                "name": "Tsv",
                "categoryName": "Flat Files",
                "categoryType": "flat_files",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "fileUpload": true,
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png"
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
                "driver": "com.helicalinsight.avro",
                "databaseDialect": "",
                "name": "Avro",
                "categoryName": "Flat Files",
                "categoryType": "flat_files",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "fileUpload": true,
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png"
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
                "driver": "com.helicalinsight.csv",
                "databaseDialect": "",
                "name": "Csv",
                "categoryName": "Flat Files",
                "categoryType": "flat_files",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "fileUpload": true,
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png"
            },
            {
                "driver": "com.helicalinsight.csvh",
                "databaseDialect": "",
                "name": "Csvh",
                "categoryName": "Flat Files",
                "categoryType": "flat_files",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "fileUpload": true,
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png"
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
                "driver": "com.helicalinsight.excel",
                "databaseDialect": "",
                "name": "Excel",
                "categoryName": "Flat Files",
                "categoryType": "flat_files",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "fileUpload": true,
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
                "driver": "com.helicalinsight.json",
                "databaseDialect": "",
                "name": "Json",
                "categoryName": "Flat Files",
                "categoryType": "flat_files",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "fileUpload": true,
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png"
            },
            {
                "driver": "com.helicalinsight.ltsv",
                "databaseDialect": "",
                "name": "Ltsv",
                "categoryName": "Flat Files",
                "categoryType": "flat_files",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "fileUpload": true,
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png"
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
                "driver": "com.helicalinsight.parquet",
                "databaseDialect": "",
                "name": "Parquet",
                "categoryName": "Flat Files",
                "categoryType": "flat_files",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "fileUpload": true,
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png"
            },
            {
                "driver": "com.helicalinsight.pcap",
                "databaseDialect": "",
                "name": "Pcap",
                "categoryName": "Flat Files",
                "categoryType": "flat_files",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "fileUpload": true,
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png"
            },
            {
                "driver": "com.helicalinsight.pcapng",
                "databaseDialect": "",
                "name": "Pcapng",
                "categoryName": "Flat Files",
                "categoryType": "flat_files",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "fileUpload": true,
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png"
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
                "driver": "com.helicalinsight.psv",
                "databaseDialect": "",
                "name": "Psv",
                "categoryName": "Flat Files",
                "categoryType": "flat_files",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "fileUpload": true,
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png"
            },
            {
                "driver": "com.helicalinsight.sequencefile",
                "databaseDialect": "",
                "name": "Sequencefile",
                "categoryName": "Flat Files",
                "categoryType": "flat_files",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "fileUpload": true,
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png"
            },
            {
                "driver": "com.helicalinsight.shp",
                "databaseDialect": "",
                "name": "Shp",
                "categoryName": "Flat Files",
                "categoryType": "flat_files",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "fileUpload": true,
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png"
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
                "driver": "com.helicalinsight.syslog",
                "databaseDialect": "",
                "name": "Syslog",
                "categoryName": "Flat Files",
                "categoryType": "flat_files",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "fileUpload": true,
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png"
            },
            {
                "driver": "com.helicalinsight.tsv",
                "databaseDialect": "",
                "name": "Tsv",
                "categoryName": "Flat Files",
                "categoryType": "flat_files",
                "type": "global.jdbc",
                "dataSourceProvider": "tomcat",
                "fileUpload": true,
                "classifier": "global",
                "imgUrl": "../images/data_sources/defaut_datasource.png"
            }
        ],
        "driversList": [
            {
                "driver": "com.helicalinsight.avro",
                "available": "true",
                "fileUpload": true,
                "parameter": {
                    "@type": "avro",
                    "@extensions": ".avro"
                }
            },
            {
                "driver": "com.helicalinsight.csv",
                "available": "true",
                "fileUpload": true,
                "parameter": {
                    "@delimiter": ",",
                    "@extensions": ".csv",
                    "@extractHeader": "true",
                    "@type": "text"
                }
            },
            {
                "driver": "com.helicalinsight.csvh",
                "available": "true",
                "fileUpload": true,
                "parameter": {
                    "@delimiter": ",",
                    "@extensions": ".csvh",
                    "@extractHeader": "true",
                    "@type": "text"
                }
            },
            {
                "driver": "com.helicalinsight.excel",
                "available": "true",
                "fileUpload": true,
                "parameter": {
                    "@type": "excel",
                    "@extensions": ".excel"
                }
            },
            {
                "driver": "com.helicalinsight.json",
                "available": "true",
                "fileUpload": true,
                "parameter": {
                    "@extensions": ".json",
                    "@type": "json"
                }
            },
            {
                "driver": "com.helicalinsight.ltsv",
                "available": "true",
                "fileUpload": true,
                "parameter": {
                    "@extensions": ".ltsv",
                    "@type": "ltsv"
                }
            },
            {
                "driver": "com.helicalinsight.parquet",
                "available": "true",
                "fileUpload": true,
                "parameter": {
                    "@type": "parquet",
                    "@extensions": ".parquet"
                }
            },
            {
                "driver": "com.helicalinsight.pcap",
                "available": "true",
                "fileUpload": true,
                "parameter": {
                    "@extensions": ".pcap",
                    "@type": "pcap"
                }
            },
            {
                "driver": "com.helicalinsight.pcapng",
                "available": "true",
                "fileUpload": true,
                "parameter": {
                    "@extensions": ".pcapng",
                    "@type": "pcapng"
                }
            },
            {
                "driver": "com.helicalinsight.psv",
                "available": "true",
                "fileUpload": true,
                "parameter": {
                    "@delimiter": "|",
                    "@extensions": ".psv",
                    "@extractHeader": "true",
                    "@type": "text"
                }
            },
            {
                "driver": "com.helicalinsight.sequencefile",
                "available": "true",
                "fileUpload": true,
                "parameter": {
                    "@extensions": ".sequencefile",
                    "@type": "sequencefile"
                }
            },
            {
                "driver": "com.helicalinsight.shp",
                "available": "true",
                "fileUpload": true,
                "parameter": {
                    "@type": "shp",
                    "@extensions": ".shp"
                }
            },
            {
                "driver": "com.helicalinsight.syslog",
                "available": "true",
                "fileUpload": true,
                "parameter": {
                    "@extensions": ".syslog",
                    "@type": "syslog"
                }
            },
            {
                "driver": "com.helicalinsight.tsv",
                "available": "true",
                "fileUpload": true,
                "parameter": {
                    "@delimiter": "\t",
                    "@extensions": ".tsv",
                    "@extractHeader": "true",
                    "@type": "text"
                }
            },
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
                        "keyPath": "8spe-mtb1-2jh0-2sjh-l4/1zmj-4dwx-5uja-udnu-vx",
                        "key": "1zmj-4dwx-5uja-udnu-vx",
                        "uuid": "1zmj-4dwx-5uja-udnu-vx"
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
                        "keyPath": "8spe-mtb1-2jh0-2sjh-l4/t1g9-fjyf-ac96-fk2c-q4",
                        "key": "t1g9-fjyf-ac96-fk2c-q4",
                        "uuid": "t1g9-fjyf-ac96-fk2c-q4"
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
                        "children": [],
                        "driverType": "Derby",
                        "keyPath": "8spe-mtb1-2jh0-2sjh-l4/4kxh-v9a9-kz8o-tsn2-ky",
                        "key": "4kxh-v9a9-kz8o-tsn2-ky",
                        "uuid": "4kxh-v9a9-kz8o-tsn2-ky"
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
                        "keyPath": "8spe-mtb1-2jh0-2sjh-l4/x78j-z2h2-ub3j-a30n-jb",
                        "key": "x78j-z2h2-ub3j-a30n-jb",
                        "uuid": "x78j-z2h2-ub3j-a30n-jb"
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
                        "keyPath": "8spe-mtb1-2jh0-2sjh-l4/ct2a-c2v6-8kmp-rphj-lb",
                        "key": "ct2a-c2v6-8kmp-rphj-lb",
                        "uuid": "ct2a-c2v6-8kmp-rphj-lb"
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
                        "keyPath": "8spe-mtb1-2jh0-2sjh-l4/hkyk-ihe6-06qh-nkf5-nd",
                        "key": "hkyk-ihe6-06qh-nkf5-nd",
                        "uuid": "hkyk-ihe6-06qh-nkf5-nd"
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
                        "keyPath": "8spe-mtb1-2jh0-2sjh-l4/un8z-ghjv-3nj9-kp9m-5r",
                        "key": "un8z-ghjv-3nj9-kp9m-5r",
                        "uuid": "un8z-ghjv-3nj9-kp9m-5r"
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
                        "keyPath": "8spe-mtb1-2jh0-2sjh-l4/tu6u-gwmz-lmkg-tyu6-kr",
                        "key": "tu6u-gwmz-lmkg-tyu6-kr",
                        "uuid": "tu6u-gwmz-lmkg-tyu6-kr"
                    }
                ],
                "key": "8spe-mtb1-2jh0-2sjh-l4",
                "uuid": "8spe-mtb1-2jh0-2sjh-l4",
                "keyPath": "8spe-mtb1-2jh0-2sjh-l4",
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
                        "keyPath": "aj34-rtxw-em1i-uafj-ag/kv3z-g4qd-fqdt-8woo-rh",
                        "key": "kv3z-g4qd-fqdt-8woo-rh",
                        "uuid": "kv3z-g4qd-fqdt-8woo-rh"
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
                        "keyPath": "aj34-rtxw-em1i-uafj-ag/vasz-kdsb-kv4s-li0j-ei",
                        "key": "vasz-kdsb-kv4s-li0j-ei",
                        "uuid": "vasz-kdsb-kv4s-li0j-ei"
                    }
                ],
                "key": "aj34-rtxw-em1i-uafj-ag",
                "uuid": "aj34-rtxw-em1i-uafj-ag",
                "keyPath": "aj34-rtxw-em1i-uafj-ag",
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
                        "keyPath": "7bop-sl18-y8w9-k23l-m9/b1h7-n30f-7phx-gy9j-be",
                        "key": "b1h7-n30f-7phx-gy9j-be",
                        "uuid": "b1h7-n30f-7phx-gy9j-be"
                    }
                ],
                "key": "7bop-sl18-y8w9-k23l-m9",
                "uuid": "7bop-sl18-y8w9-k23l-m9",
                "keyPath": "7bop-sl18-y8w9-k23l-m9",
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
                        "keyPath": "z1xe-t0ge-xylf-30j3-m7/nnm9-0yxk-cjaq-sffa-54",
                        "key": "nnm9-0yxk-cjaq-sffa-54",
                        "uuid": "nnm9-0yxk-cjaq-sffa-54"
                    }
                ],
                "key": "z1xe-t0ge-xylf-30j3-m7",
                "uuid": "z1xe-t0ge-xylf-30j3-m7",
                "keyPath": "z1xe-t0ge-xylf-30j3-m7",
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
                        "keyPath": "1mta-y1x7-3ttw-rohx-aa/q7w5-qvzi-8n3k-zpuz-s8",
                        "key": "q7w5-qvzi-8n3k-zpuz-s8",
                        "uuid": "q7w5-qvzi-8n3k-zpuz-s8"
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
                        "keyPath": "1mta-y1x7-3ttw-rohx-aa/ttqr-ty3j-ncpk-1d01-qe",
                        "key": "ttqr-ty3j-ncpk-1d01-qe",
                        "uuid": "ttqr-ty3j-ncpk-1d01-qe"
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
                        "keyPath": "1mta-y1x7-3ttw-rohx-aa/lb32-856p-ep5t-6966-60",
                        "key": "lb32-856p-ep5t-6966-60",
                        "uuid": "lb32-856p-ep5t-6966-60"
                    }
                ],
                "key": "1mta-y1x7-3ttw-rohx-aa",
                "uuid": "1mta-y1x7-3ttw-rohx-aa",
                "keyPath": "1mta-y1x7-3ttw-rohx-aa",
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
                        "keyPath": "rggh-szvu-2c0d-rx5p-ku/wqdc-kt2j-2hn2-nh96-yn",
                        "key": "wqdc-kt2j-2hn2-nh96-yn",
                        "uuid": "wqdc-kt2j-2hn2-nh96-yn"
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
                        "keyPath": "rggh-szvu-2c0d-rx5p-ku/be1y-qhb4-820j-435s-zt",
                        "key": "be1y-qhb4-820j-435s-zt",
                        "uuid": "be1y-qhb4-820j-435s-zt"
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
                        "keyPath": "rggh-szvu-2c0d-rx5p-ku/z34y-owko-32z6-titp-bt",
                        "key": "z34y-owko-32z6-titp-bt",
                        "uuid": "z34y-owko-32z6-titp-bt"
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
                        "keyPath": "rggh-szvu-2c0d-rx5p-ku/2b5n-piw4-y7k2-u1fc-hg",
                        "key": "2b5n-piw4-y7k2-u1fc-hg",
                        "uuid": "2b5n-piw4-y7k2-u1fc-hg"
                    }
                ],
                "key": "rggh-szvu-2c0d-rx5p-ku",
                "uuid": "rggh-szvu-2c0d-rx5p-ku",
                "keyPath": "rggh-szvu-2c0d-rx5p-ku",
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
                        "children": [
                            {
                                "id": "d324e793296ff76020c708f1c8fbb704",
                                "name": "dimdate",
                                "data": {
                                    "id": "1201",
                                    "type": "dynamicDataSource"
                                },
                                "keyPath": "rpv0-zzlv-59ye-ycop-u5/vg91-ulji-ey2l-63zn-d0/tuiv-s19f-q622-9l01-z3",
                                "key": "tuiv-s19f-q622-9l01-z3",
                                "alias": "dimdate",
                                "uuid": "tuiv-s19f-q622-9l01-z3",
                                "connId": "665cq",
                                "dataSource": {
                                    "id": "1201",
                                    "type": "dynamicDataSource",
                                    "baseType": "global.jdbc",
                                    "catSchemaPredicted": false,
                                    "sync": false,
                                    "catalog": "",
                                    "schema": "",
                                    "connId": "665cq",
                                    "dbId": "665cq",
                                    "classifier": "db.workflow",
                                    "datasourceName": "SampleTravelData",
                                    "dsKeyPath": "rpv0-zzlv-59ye-ycop-u5/vg91-ulji-ey2l-63zn-d0",
                                    "driverType": "Sqlite",
                                    "database": "SampleTravelData"
                                },
                                "category": "table",
                                "nameWithConnId": "dimdate_665cq",
                                "database": "SampleTravelData",
                                "selected": true
                            },
                            {
                                "id": "b161910cbebfd353351a6c0b46e6a02e",
                                "name": "employee_details",
                                "data": {
                                    "id": "1201",
                                    "type": "dynamicDataSource"
                                },
                                "keyPath": "rpv0-zzlv-59ye-ycop-u5/vg91-ulji-ey2l-63zn-d0/4l4s-g08n-u2vw-3lx2-00",
                                "key": "4l4s-g08n-u2vw-3lx2-00",
                                "alias": "employee_details",
                                "uuid": "4l4s-g08n-u2vw-3lx2-00",
                                "connId": "665cq",
                                "dataSource": {
                                    "id": "1201",
                                    "type": "dynamicDataSource",
                                    "baseType": "global.jdbc",
                                    "catSchemaPredicted": false,
                                    "sync": false,
                                    "catalog": "",
                                    "schema": "",
                                    "connId": "665cq",
                                    "dbId": "665cq",
                                    "classifier": "db.workflow",
                                    "datasourceName": "SampleTravelData",
                                    "dsKeyPath": "rpv0-zzlv-59ye-ycop-u5/vg91-ulji-ey2l-63zn-d0",
                                    "driverType": "Sqlite",
                                    "database": "SampleTravelData"
                                },
                                "category": "table",
                                "nameWithConnId": "employee_details_665cq",
                                "database": "SampleTravelData",
                                "selected": false
                            },
                            {
                                "id": "f2ff93c37589ef57f40dcb15fda6d7ea",
                                "name": "geo_cordinates",
                                "data": {
                                    "id": "1201",
                                    "type": "dynamicDataSource"
                                },
                                "keyPath": "rpv0-zzlv-59ye-ycop-u5/vg91-ulji-ey2l-63zn-d0/sbbv-t0n4-u2ui-dhh6-pb",
                                "key": "sbbv-t0n4-u2ui-dhh6-pb",
                                "alias": "geo_cordinates",
                                "uuid": "sbbv-t0n4-u2ui-dhh6-pb",
                                "connId": "665cq",
                                "dataSource": {
                                    "id": "1201",
                                    "type": "dynamicDataSource",
                                    "baseType": "global.jdbc",
                                    "catSchemaPredicted": false,
                                    "sync": false,
                                    "catalog": "",
                                    "schema": "",
                                    "connId": "665cq",
                                    "dbId": "665cq",
                                    "classifier": "db.workflow",
                                    "datasourceName": "SampleTravelData",
                                    "dsKeyPath": "rpv0-zzlv-59ye-ycop-u5/vg91-ulji-ey2l-63zn-d0",
                                    "driverType": "Sqlite",
                                    "database": "SampleTravelData"
                                },
                                "category": "table",
                                "nameWithConnId": "geo_cordinates_665cq",
                                "database": "SampleTravelData",
                                "selected": false
                            },
                            {
                                "id": "025fbfb381cb17d4519363c3585626fb",
                                "name": "meeting_details",
                                "data": {
                                    "id": "1201",
                                    "type": "dynamicDataSource"
                                },
                                "keyPath": "rpv0-zzlv-59ye-ycop-u5/vg91-ulji-ey2l-63zn-d0/fgtz-6yzu-wqrz-6lwi-0a",
                                "key": "fgtz-6yzu-wqrz-6lwi-0a",
                                "alias": "meeting_details",
                                "uuid": "fgtz-6yzu-wqrz-6lwi-0a",
                                "connId": "665cq",
                                "dataSource": {
                                    "id": "1201",
                                    "type": "dynamicDataSource",
                                    "baseType": "global.jdbc",
                                    "catSchemaPredicted": false,
                                    "sync": false,
                                    "catalog": "",
                                    "schema": "",
                                    "connId": "665cq",
                                    "dbId": "665cq",
                                    "classifier": "db.workflow",
                                    "datasourceName": "SampleTravelData",
                                    "dsKeyPath": "rpv0-zzlv-59ye-ycop-u5/vg91-ulji-ey2l-63zn-d0",
                                    "driverType": "Sqlite",
                                    "database": "SampleTravelData"
                                },
                                "category": "table",
                                "nameWithConnId": "meeting_details_665cq",
                                "database": "SampleTravelData",
                                "selected": false
                            },
                            {
                                "id": "21e1b86ae9680d0fc197ed543c3e37eb",
                                "name": "travel_details",
                                "data": {
                                    "id": "1201",
                                    "type": "dynamicDataSource"
                                },
                                "keyPath": "rpv0-zzlv-59ye-ycop-u5/vg91-ulji-ey2l-63zn-d0/03vc-q0rs-mc7o-simc-aq",
                                "key": "03vc-q0rs-mc7o-simc-aq",
                                "alias": "travel_details",
                                "uuid": "03vc-q0rs-mc7o-simc-aq",
                                "connId": "665cq",
                                "dataSource": {
                                    "id": "1201",
                                    "type": "dynamicDataSource",
                                    "baseType": "global.jdbc",
                                    "catSchemaPredicted": false,
                                    "sync": false,
                                    "catalog": "",
                                    "schema": "",
                                    "connId": "665cq",
                                    "dbId": "665cq",
                                    "classifier": "db.workflow",
                                    "datasourceName": "SampleTravelData",
                                    "dsKeyPath": "rpv0-zzlv-59ye-ycop-u5/vg91-ulji-ey2l-63zn-d0",
                                    "driverType": "Sqlite",
                                    "database": "SampleTravelData"
                                },
                                "category": "table",
                                "nameWithConnId": "travel_details_665cq",
                                "database": "SampleTravelData",
                                "selected": false
                            }
                        ],
                        "driverType": "Sqlite",
                        "keyPath": "rpv0-zzlv-59ye-ycop-u5/vg91-ulji-ey2l-63zn-d0",
                        "key": "vg91-ulji-ey2l-63zn-d0",
                        "uuid": "vg91-ulji-ey2l-63zn-d0",
                        "fetched": true
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
                        "keyPath": "rpv0-zzlv-59ye-ycop-u5/m5h8-169n-2tgz-ygf4-ui",
                        "key": "m5h8-169n-2tgz-ygf4-ui",
                        "uuid": "m5h8-169n-2tgz-ygf4-ui"
                    }
                ],
                "key": "rpv0-zzlv-59ye-ycop-u5",
                "uuid": "rpv0-zzlv-59ye-ycop-u5",
                "keyPath": "rpv0-zzlv-59ye-ycop-u5",
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
                        "id": "1201",
                        "type": "dynamicDataSource"
                    },
                    "connId": "1201",
                    "dsUUID": "vg91-ulji-ey2l-63zn-d0",
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
                            "id": "1201",
                            "type": "dynamicDataSource",
                            "baseType": "global.jdbc",
                            "catSchemaPredicted": false,
                            "sync": false,
                            "catalog": "",
                            "schema": "",
                            "connId": "665cq",
                            "dbId": "665cq",
                            "classifier": "db.workflow",
                            "datasourceName": "SampleTravelData",
                            "dsKeyPath": "rpv0-zzlv-59ye-ycop-u5/vg91-ulji-ey2l-63zn-d0",
                            "driverType": "Sqlite",
                            "database": "SampleTravelData"
                        },
                        "name": ""
                    }
                }
            ]
        },
        "dataSource": [
            {
                "id": "1201",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "",
                "connId": "665cq",
                "dbId": "665cq",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelData",
                "dsKeyPath": "rpv0-zzlv-59ye-ycop-u5/vg91-ulji-ey2l-63zn-d0",
                "driverType": "Sqlite",
                "database": ""
            }
        ],
        "tables": {
            "dimdate": {
                "id": "d324e793296ff76020c708f1c8fbb704",
                "name": "dimdate",
                "data": {
                    "id": "1201",
                    "type": "dynamicDataSource"
                },
                "keyPath": "rpv0-zzlv-59ye-ycop-u5/vg91-ulji-ey2l-63zn-d0/tuiv-s19f-q622-9l01-z3",
                "key": "tuiv-s19f-q622-9l01-z3",
                "alias": "dimdate",
                "uuid": "tuiv-s19f-q622-9l01-z3",
                "connId": "665cq",
                "dataSource": {
                    "id": "1201",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc",
                    "catSchemaPredicted": false,
                    "sync": false,
                    "catalog": "",
                    "schema": "",
                    "connId": "665cq",
                    "dbId": "665cq",
                    "classifier": "db.workflow",
                    "datasourceName": "SampleTravelData",
                    "dsKeyPath": "rpv0-zzlv-59ye-ycop-u5/vg91-ulji-ey2l-63zn-d0",
                    "driverType": "Sqlite",
                    "database": "SampleTravelData"
                },
                "category": "table",
                "nameWithConnId": "dimdate_665cq",
                "database": "SampleTravelData",
                "selected": true,
                "keyName": "dimdate",
                "columns": {
                    "dim_id": {
                        "alias": "dim_id",
                        "fullyQualifiedColumn": "dimdate.dim_id",
                        "columnId": "30d6c607-4531-4593-af51-1a11733b3891",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        },
                        "category": "column",
                        "name": "dim_id",
                        "originalName": "dim_id",
                        "originalId": "30d6c607-4531-4593-af51-1a11733b3891",
                        "tableKey": "dimdate",
                        "connId": "665cq",
                        "columnKey": "dim_id",
                        "_columnId": "30d6c607-4531-4593-af51-1a11733b3891",
                        "uuid": "rvqj-xib1-l7bs-kdjl-xe",
                        "tableId": "d324e793296ff76020c708f1c8fbb704"
                    },
                    "fiscal_year": {
                        "alias": "fiscal_year",
                        "fullyQualifiedColumn": "dimdate.fiscal_year",
                        "columnId": "c6a81a6e-0cec-4768-8c45-2c525ca3a60c",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "name": "fiscal_year",
                        "originalName": "fiscal_year",
                        "originalId": "c6a81a6e-0cec-4768-8c45-2c525ca3a60c",
                        "tableKey": "dimdate",
                        "connId": "665cq",
                        "columnKey": "fiscal_year",
                        "_columnId": "c6a81a6e-0cec-4768-8c45-2c525ca3a60c",
                        "uuid": "99mw-4yys-548u-2fg3-jf",
                        "tableId": "d324e793296ff76020c708f1c8fbb704"
                    },
                    "modified_date": {
                        "alias": "modified_date",
                        "fullyQualifiedColumn": "dimdate.modified_date",
                        "columnId": "b2edbd86-b93d-4b60-acc3-cddca28b252e",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "name": "modified_date",
                        "originalName": "modified_date",
                        "originalId": "b2edbd86-b93d-4b60-acc3-cddca28b252e",
                        "tableKey": "dimdate",
                        "connId": "665cq",
                        "columnKey": "modified_date",
                        "_columnId": "b2edbd86-b93d-4b60-acc3-cddca28b252e",
                        "uuid": "ohhh-q70h-s6ez-d9ge-ui",
                        "tableId": "d324e793296ff76020c708f1c8fbb704"
                    },
                    "date_key": {
                        "alias": "date_key",
                        "fullyQualifiedColumn": "dimdate.date_key",
                        "columnId": "0c984619-3983-466d-8353-5ccef259aa00",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "name": "date_key",
                        "originalName": "date_key",
                        "originalId": "0c984619-3983-466d-8353-5ccef259aa00",
                        "tableKey": "dimdate",
                        "connId": "665cq",
                        "columnKey": "date_key",
                        "_columnId": "0c984619-3983-466d-8353-5ccef259aa00",
                        "uuid": "uh5j-nhtk-1ayy-df1p-zz",
                        "tableId": "d324e793296ff76020c708f1c8fbb704"
                    },
                    "day_number": {
                        "alias": "day_number",
                        "fullyQualifiedColumn": "dimdate.day_number",
                        "columnId": "42017524-6593-44d0-9e5c-d92a91afdfe6",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "name": "day_number",
                        "originalName": "day_number",
                        "originalId": "42017524-6593-44d0-9e5c-d92a91afdfe6",
                        "tableKey": "dimdate",
                        "connId": "665cq",
                        "columnKey": "day_number",
                        "_columnId": "42017524-6593-44d0-9e5c-d92a91afdfe6",
                        "uuid": "3ze1-1soo-0qif-3zh9-fm",
                        "tableId": "d324e793296ff76020c708f1c8fbb704"
                    },
                    "fiscal_month_name": {
                        "alias": "fiscal_month_name",
                        "fullyQualifiedColumn": "dimdate.fiscal_month_name",
                        "columnId": "885ba7c9-585e-4a07-92a2-f37950021ebe",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "name": "fiscal_month_name",
                        "originalName": "fiscal_month_name",
                        "originalId": "885ba7c9-585e-4a07-92a2-f37950021ebe",
                        "tableKey": "dimdate",
                        "connId": "665cq",
                        "columnKey": "fiscal_month_name",
                        "_columnId": "885ba7c9-585e-4a07-92a2-f37950021ebe",
                        "uuid": "vp7q-jm0p-13ud-7rip-g0",
                        "tableId": "d324e793296ff76020c708f1c8fbb704"
                    },
                    "fiscal_month_label": {
                        "alias": "fiscal_month_label",
                        "fullyQualifiedColumn": "dimdate.fiscal_month_label",
                        "columnId": "59d93e66-aaa9-40dc-946d-b5a916247d36",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "name": "fiscal_month_label",
                        "originalName": "fiscal_month_label",
                        "originalId": "59d93e66-aaa9-40dc-946d-b5a916247d36",
                        "tableKey": "dimdate",
                        "connId": "665cq",
                        "columnKey": "fiscal_month_label",
                        "_columnId": "59d93e66-aaa9-40dc-946d-b5a916247d36",
                        "uuid": "tntz-ke59-iqqc-72gq-73",
                        "tableId": "d324e793296ff76020c708f1c8fbb704"
                    },
                    "created_date": {
                        "alias": "created_date",
                        "fullyQualifiedColumn": "dimdate.created_date",
                        "columnId": "44d633ff-aba1-4a01-9fbf-e467fefb6ba1",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "name": "created_date",
                        "originalName": "created_date",
                        "originalId": "44d633ff-aba1-4a01-9fbf-e467fefb6ba1",
                        "tableKey": "dimdate",
                        "connId": "665cq",
                        "columnKey": "created_date",
                        "_columnId": "44d633ff-aba1-4a01-9fbf-e467fefb6ba1",
                        "uuid": "wf7b-5qji-7yd8-8b2j-6l",
                        "tableId": "d324e793296ff76020c708f1c8fbb704"
                    },
                    "created_time": {
                        "alias": "created_time",
                        "fullyQualifiedColumn": "dimdate.created_time",
                        "columnId": "49c99464-f4d9-4d92-bba1-b1f98b9b31d1",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "name": "created_time",
                        "originalName": "created_time",
                        "originalId": "49c99464-f4d9-4d92-bba1-b1f98b9b31d1",
                        "tableKey": "dimdate",
                        "connId": "665cq",
                        "columnKey": "created_time",
                        "_columnId": "49c99464-f4d9-4d92-bba1-b1f98b9b31d1",
                        "uuid": "5v5v-z4ne-nc2q-n4p9-h4",
                        "tableId": "d324e793296ff76020c708f1c8fbb704"
                    },
                    "rating": {
                        "alias": "rating",
                        "fullyQualifiedColumn": "dimdate.rating",
                        "columnId": "e510e9e8-efde-41b8-8bea-52685d139e5c",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "name": "rating",
                        "originalName": "rating",
                        "originalId": "e510e9e8-efde-41b8-8bea-52685d139e5c",
                        "tableKey": "dimdate",
                        "connId": "665cq",
                        "columnKey": "rating",
                        "_columnId": "e510e9e8-efde-41b8-8bea-52685d139e5c",
                        "uuid": "eo6n-1cwx-nsth-jr2x-zk",
                        "tableId": "d324e793296ff76020c708f1c8fbb704"
                    }
                },
                "columnsFetched": true
            },
            "dimdate_1": {
                "id": "of1i-9tz1-ueu3-1rk6-wl",
                "name": "dimdate_1",
                "data": {
                    "id": "1201",
                    "type": "dynamicDataSource"
                },
                "keyPath": "rpv0-zzlv-59ye-ycop-u5/vg91-ulji-ey2l-63zn-d0/of1i-9tz1-ueu3-1rk6-wl",
                "key": "of1i-9tz1-ueu3-1rk6-wl",
                "alias": "dimdate_1",
                "uuid": "of1i-9tz1-ueu3-1rk6-wl",
                "connId": "665cq",
                "dataSource": {
                    "id": "1201",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc",
                    "catSchemaPredicted": false,
                    "sync": false,
                    "catalog": "",
                    "schema": "",
                    "connId": "665cq",
                    "dbId": "665cq",
                    "classifier": "db.workflow",
                    "datasourceName": "SampleTravelData",
                    "dsKeyPath": "rpv0-zzlv-59ye-ycop-u5/vg91-ulji-ey2l-63zn-d0",
                    "driverType": "Sqlite",
                    "database": "SampleTravelData"
                },
                "category": "table",
                "nameWithConnId": "dimdate_665cq",
                "database": "SampleTravelData",
                "selected": true,
                "keyName": "dimdate_1",
                "columns": {
                    "dim_id": {
                        "alias": "dim_id",
                        "fullyQualifiedColumn": "dimdate.dim_id",
                        "columnId": "ej8a-zzj3-x72u-fpm3-d6",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        },
                        "category": "column",
                        "name": "dim_id",
                        "originalName": "dim_id",
                        "originalId": "30d6c607-4531-4593-af51-1a11733b3891",
                        "tableKey": "dimdate_1",
                        "connId": "665cq",
                        "columnKey": "dim_id",
                        "_columnId": "30d6c607-4531-4593-af51-1a11733b3891",
                        "uuid": "ej8a-zzj3-x72u-fpm3-d6",
                        "tableId": "d324e793296ff76020c708f1c8fbb704"
                    },
                    "fiscal_year": {
                        "alias": "fiscal_year",
                        "fullyQualifiedColumn": "dimdate.fiscal_year",
                        "columnId": "eibv-rop7-k7v5-fdpc-rw",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "name": "fiscal_year",
                        "originalName": "fiscal_year",
                        "originalId": "c6a81a6e-0cec-4768-8c45-2c525ca3a60c",
                        "tableKey": "dimdate_1",
                        "connId": "665cq",
                        "columnKey": "fiscal_year",
                        "_columnId": "c6a81a6e-0cec-4768-8c45-2c525ca3a60c",
                        "uuid": "eibv-rop7-k7v5-fdpc-rw",
                        "tableId": "d324e793296ff76020c708f1c8fbb704"
                    },
                    "modified_date": {
                        "alias": "modified_date",
                        "fullyQualifiedColumn": "dimdate.modified_date",
                        "columnId": "97v3-w2bf-dm8k-km1f-hl",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "name": "modified_date",
                        "originalName": "modified_date",
                        "originalId": "b2edbd86-b93d-4b60-acc3-cddca28b252e",
                        "tableKey": "dimdate_1",
                        "connId": "665cq",
                        "columnKey": "modified_date",
                        "_columnId": "b2edbd86-b93d-4b60-acc3-cddca28b252e",
                        "uuid": "97v3-w2bf-dm8k-km1f-hl",
                        "tableId": "d324e793296ff76020c708f1c8fbb704"
                    },
                    "date_key": {
                        "alias": "date_key",
                        "fullyQualifiedColumn": "dimdate.date_key",
                        "columnId": "79ot-8dlw-buse-76tz-x4",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "name": "date_key",
                        "originalName": "date_key",
                        "originalId": "0c984619-3983-466d-8353-5ccef259aa00",
                        "tableKey": "dimdate_1",
                        "connId": "665cq",
                        "columnKey": "date_key",
                        "_columnId": "0c984619-3983-466d-8353-5ccef259aa00",
                        "uuid": "79ot-8dlw-buse-76tz-x4",
                        "tableId": "d324e793296ff76020c708f1c8fbb704"
                    },
                    "day_number": {
                        "alias": "day_number",
                        "fullyQualifiedColumn": "dimdate.day_number",
                        "columnId": "gt0r-nqd5-beul-e704-o5",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "name": "day_number",
                        "originalName": "day_number",
                        "originalId": "42017524-6593-44d0-9e5c-d92a91afdfe6",
                        "tableKey": "dimdate_1",
                        "connId": "665cq",
                        "columnKey": "day_number",
                        "_columnId": "42017524-6593-44d0-9e5c-d92a91afdfe6",
                        "uuid": "gt0r-nqd5-beul-e704-o5",
                        "tableId": "d324e793296ff76020c708f1c8fbb704"
                    },
                    "fiscal_month_name": {
                        "alias": "fiscal_month_name",
                        "fullyQualifiedColumn": "dimdate.fiscal_month_name",
                        "columnId": "eahn-524q-s0iz-her0-14",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "name": "fiscal_month_name",
                        "originalName": "fiscal_month_name",
                        "originalId": "885ba7c9-585e-4a07-92a2-f37950021ebe",
                        "tableKey": "dimdate_1",
                        "connId": "665cq",
                        "columnKey": "fiscal_month_name",
                        "_columnId": "885ba7c9-585e-4a07-92a2-f37950021ebe",
                        "uuid": "eahn-524q-s0iz-her0-14",
                        "tableId": "d324e793296ff76020c708f1c8fbb704"
                    },
                    "fiscal_month_label": {
                        "alias": "fiscal_month_label",
                        "fullyQualifiedColumn": "dimdate.fiscal_month_label",
                        "columnId": "x0t0-fj5v-gvi3-yrim-cx",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "name": "fiscal_month_label",
                        "originalName": "fiscal_month_label",
                        "originalId": "59d93e66-aaa9-40dc-946d-b5a916247d36",
                        "tableKey": "dimdate_1",
                        "connId": "665cq",
                        "columnKey": "fiscal_month_label",
                        "_columnId": "59d93e66-aaa9-40dc-946d-b5a916247d36",
                        "uuid": "x0t0-fj5v-gvi3-yrim-cx",
                        "tableId": "d324e793296ff76020c708f1c8fbb704"
                    },
                    "created_date": {
                        "alias": "created_date",
                        "fullyQualifiedColumn": "dimdate.created_date",
                        "columnId": "cn3x-18qe-cjzk-5l66-u8",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "name": "created_date",
                        "originalName": "created_date",
                        "originalId": "44d633ff-aba1-4a01-9fbf-e467fefb6ba1",
                        "tableKey": "dimdate_1",
                        "connId": "665cq",
                        "columnKey": "created_date",
                        "_columnId": "44d633ff-aba1-4a01-9fbf-e467fefb6ba1",
                        "uuid": "cn3x-18qe-cjzk-5l66-u8",
                        "tableId": "d324e793296ff76020c708f1c8fbb704"
                    },
                    "created_time": {
                        "alias": "created_time",
                        "fullyQualifiedColumn": "dimdate.created_time",
                        "columnId": "hjql-z216-21p0-2sjq-rm",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "name": "created_time",
                        "originalName": "created_time",
                        "originalId": "49c99464-f4d9-4d92-bba1-b1f98b9b31d1",
                        "tableKey": "dimdate_1",
                        "connId": "665cq",
                        "columnKey": "created_time",
                        "_columnId": "49c99464-f4d9-4d92-bba1-b1f98b9b31d1",
                        "uuid": "hjql-z216-21p0-2sjq-rm",
                        "tableId": "d324e793296ff76020c708f1c8fbb704"
                    },
                    "rating": {
                        "alias": "rating",
                        "fullyQualifiedColumn": "dimdate.rating",
                        "columnId": "6272-pyb4-or3x-zuj5-f4",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "name": "rating",
                        "originalName": "rating",
                        "originalId": "e510e9e8-efde-41b8-8bea-52685d139e5c",
                        "tableKey": "dimdate_1",
                        "connId": "665cq",
                        "columnKey": "rating",
                        "_columnId": "e510e9e8-efde-41b8-8bea-52685d139e5c",
                        "uuid": "6272-pyb4-or3x-zuj5-f4",
                        "tableId": "d324e793296ff76020c708f1c8fbb704"
                    }
                },
                "columnsFetched": true,
                "originalName": "dimdate",
                "originalId": "d324e793296ff76020c708f1c8fbb704",
                "duplicate": true
            },
            "dimdate_2": {
                "id": "tb9m-cx6z-j9dc-ruqz-gk",
                "name": "dimdate_2",
                "data": {
                    "id": "1201",
                    "type": "dynamicDataSource"
                },
                "keyPath": "rpv0-zzlv-59ye-ycop-u5/vg91-ulji-ey2l-63zn-d0/tb9m-cx6z-j9dc-ruqz-gk",
                "key": "tb9m-cx6z-j9dc-ruqz-gk",
                "alias": "dimdate_2",
                "uuid": "tb9m-cx6z-j9dc-ruqz-gk",
                "connId": "665cq",
                "dataSource": {
                    "id": "1201",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc",
                    "catSchemaPredicted": false,
                    "sync": false,
                    "catalog": "",
                    "schema": "",
                    "connId": "665cq",
                    "dbId": "665cq",
                    "classifier": "db.workflow",
                    "datasourceName": "SampleTravelData",
                    "dsKeyPath": "rpv0-zzlv-59ye-ycop-u5/vg91-ulji-ey2l-63zn-d0",
                    "driverType": "Sqlite",
                    "database": "SampleTravelData"
                },
                "category": "table",
                "nameWithConnId": "dimdate_665cq",
                "database": "SampleTravelData",
                "selected": true,
                "keyName": "dimdate_2",
                "columns": {
                    "dim_id": {
                        "alias": "dim_id",
                        "fullyQualifiedColumn": "dimdate.dim_id",
                        "columnId": "7q8l-tt8l-9t5g-4pqx-2k",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        },
                        "category": "column",
                        "name": "dim_id",
                        "originalName": "dim_id",
                        "originalId": "30d6c607-4531-4593-af51-1a11733b3891",
                        "tableKey": "dimdate_2",
                        "connId": "665cq",
                        "columnKey": "dim_id",
                        "_columnId": "30d6c607-4531-4593-af51-1a11733b3891",
                        "uuid": "7q8l-tt8l-9t5g-4pqx-2k",
                        "tableId": "d324e793296ff76020c708f1c8fbb704"
                    },
                    "fiscal_year": {
                        "alias": "fiscal_year",
                        "fullyQualifiedColumn": "dimdate.fiscal_year",
                        "columnId": "utb8-ckq9-g4lr-hy1d-co",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "name": "fiscal_year",
                        "originalName": "fiscal_year",
                        "originalId": "c6a81a6e-0cec-4768-8c45-2c525ca3a60c",
                        "tableKey": "dimdate_2",
                        "connId": "665cq",
                        "columnKey": "fiscal_year",
                        "_columnId": "c6a81a6e-0cec-4768-8c45-2c525ca3a60c",
                        "uuid": "utb8-ckq9-g4lr-hy1d-co",
                        "tableId": "d324e793296ff76020c708f1c8fbb704"
                    },
                    "modified_date": {
                        "alias": "modified_date",
                        "fullyQualifiedColumn": "dimdate.modified_date",
                        "columnId": "r4np-us8d-31vj-i3y6-rs",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "name": "modified_date",
                        "originalName": "modified_date",
                        "originalId": "b2edbd86-b93d-4b60-acc3-cddca28b252e",
                        "tableKey": "dimdate_2",
                        "connId": "665cq",
                        "columnKey": "modified_date",
                        "_columnId": "b2edbd86-b93d-4b60-acc3-cddca28b252e",
                        "uuid": "r4np-us8d-31vj-i3y6-rs",
                        "tableId": "d324e793296ff76020c708f1c8fbb704"
                    },
                    "date_key": {
                        "alias": "date_key",
                        "fullyQualifiedColumn": "dimdate.date_key",
                        "columnId": "odlb-knst-ld0c-n7pq-1x",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "name": "date_key",
                        "originalName": "date_key",
                        "originalId": "0c984619-3983-466d-8353-5ccef259aa00",
                        "tableKey": "dimdate_2",
                        "connId": "665cq",
                        "columnKey": "date_key",
                        "_columnId": "0c984619-3983-466d-8353-5ccef259aa00",
                        "uuid": "odlb-knst-ld0c-n7pq-1x",
                        "tableId": "d324e793296ff76020c708f1c8fbb704"
                    },
                    "day_number": {
                        "alias": "day_number",
                        "fullyQualifiedColumn": "dimdate.day_number",
                        "columnId": "lm3s-d73x-hbws-i5dl-h2",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "name": "day_number",
                        "originalName": "day_number",
                        "originalId": "42017524-6593-44d0-9e5c-d92a91afdfe6",
                        "tableKey": "dimdate_2",
                        "connId": "665cq",
                        "columnKey": "day_number",
                        "_columnId": "42017524-6593-44d0-9e5c-d92a91afdfe6",
                        "uuid": "lm3s-d73x-hbws-i5dl-h2",
                        "tableId": "d324e793296ff76020c708f1c8fbb704"
                    },
                    "fiscal_month_name": {
                        "alias": "fiscal_month_name",
                        "fullyQualifiedColumn": "dimdate.fiscal_month_name",
                        "columnId": "byis-lpop-gknu-53yr-z9",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "name": "fiscal_month_name",
                        "originalName": "fiscal_month_name",
                        "originalId": "885ba7c9-585e-4a07-92a2-f37950021ebe",
                        "tableKey": "dimdate_2",
                        "connId": "665cq",
                        "columnKey": "fiscal_month_name",
                        "_columnId": "885ba7c9-585e-4a07-92a2-f37950021ebe",
                        "uuid": "byis-lpop-gknu-53yr-z9",
                        "tableId": "d324e793296ff76020c708f1c8fbb704"
                    },
                    "fiscal_month_label": {
                        "alias": "fiscal_month_label",
                        "fullyQualifiedColumn": "dimdate.fiscal_month_label",
                        "columnId": "ahzj-vs2i-1wju-5die-yq",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "name": "fiscal_month_label",
                        "originalName": "fiscal_month_label",
                        "originalId": "59d93e66-aaa9-40dc-946d-b5a916247d36",
                        "tableKey": "dimdate_2",
                        "connId": "665cq",
                        "columnKey": "fiscal_month_label",
                        "_columnId": "59d93e66-aaa9-40dc-946d-b5a916247d36",
                        "uuid": "ahzj-vs2i-1wju-5die-yq",
                        "tableId": "d324e793296ff76020c708f1c8fbb704"
                    },
                    "created_date": {
                        "alias": "created_date",
                        "fullyQualifiedColumn": "dimdate.created_date",
                        "columnId": "p9sk-ghav-e6u3-wb5f-bx",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "name": "created_date",
                        "originalName": "created_date",
                        "originalId": "44d633ff-aba1-4a01-9fbf-e467fefb6ba1",
                        "tableKey": "dimdate_2",
                        "connId": "665cq",
                        "columnKey": "created_date",
                        "_columnId": "44d633ff-aba1-4a01-9fbf-e467fefb6ba1",
                        "uuid": "p9sk-ghav-e6u3-wb5f-bx",
                        "tableId": "d324e793296ff76020c708f1c8fbb704"
                    },
                    "created_time": {
                        "alias": "created_time",
                        "fullyQualifiedColumn": "dimdate.created_time",
                        "columnId": "8dbj-3hqo-26qq-2p5f-c0",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "name": "created_time",
                        "originalName": "created_time",
                        "originalId": "49c99464-f4d9-4d92-bba1-b1f98b9b31d1",
                        "tableKey": "dimdate_2",
                        "connId": "665cq",
                        "columnKey": "created_time",
                        "_columnId": "49c99464-f4d9-4d92-bba1-b1f98b9b31d1",
                        "uuid": "8dbj-3hqo-26qq-2p5f-c0",
                        "tableId": "d324e793296ff76020c708f1c8fbb704"
                    },
                    "rating": {
                        "alias": "rating",
                        "fullyQualifiedColumn": "dimdate.rating",
                        "columnId": "d6uq-av5n-4gj0-5fgn-rb",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "name": "rating",
                        "originalName": "rating",
                        "originalId": "e510e9e8-efde-41b8-8bea-52685d139e5c",
                        "tableKey": "dimdate_2",
                        "connId": "665cq",
                        "columnKey": "rating",
                        "_columnId": "e510e9e8-efde-41b8-8bea-52685d139e5c",
                        "uuid": "d6uq-av5n-4gj0-5fgn-rb",
                        "tableId": "d324e793296ff76020c708f1c8fbb704"
                    }
                },
                "columnsFetched": true,
                "originalName": "dimdate",
                "originalId": "d324e793296ff76020c708f1c8fbb704",
                "duplicate": true
            }
        },
        "views": [],
        "activeView": false,
        "categories": {
            "8spe-mtb1-2jh0-2sjh-l4": {
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
            "aj34-rtxw-em1i-uafj-ag": {
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
            "7bop-sl18-y8w9-k23l-m9": {
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
            "z1xe-t0ge-xylf-30j3-m7": {
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
            "1mta-y1x7-3ttw-rohx-aa": {
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
            "rggh-szvu-2c0d-rx5p-ku": {
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
            "rpv0-zzlv-59ye-ycop-u5": {
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
                "id": "1201",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "",
                "connId": "665cq",
                "dbId": "665cq",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelData",
                "dsKeyPath": "rpv0-zzlv-59ye-ycop-u5/vg91-ulji-ey2l-63zn-d0",
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
        "duplicateTableList": [
            "of1i-9tz1-ueu3-1rk6-wl",
            "tb9m-cx6z-j9dc-ruqz-gk"
        ],
        "unsavedViews": [],
        "saveDetails": false,
        "savedTableIds": [],
        "savedColumnIds": [],
        "joins": [],
        "mode": "create",
        "allTablesKeys": [
            "sbbv-t0n4-u2ui-dhh6-pb",
            "fgtz-6yzu-wqrz-6lwi-0a",
            "4l4s-g08n-u2vw-3lx2-00",
            "tuiv-s19f-q622-9l01-z3",
            "03vc-q0rs-mc7o-simc-aq"
        ],
        "selectedTableKeys": [
            "tuiv-s19f-q622-9l01-z3"
        ],
        "metadataName": "Metadata_1",
        "activeDataSource": false,
        "metadataToEdit": false,
        "isSavingInProgress": false,
        "editViewsTempData": {},
        "inititalStateFromJest": false,
        "timeStamp": 1676285142935,
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
        "tablesMergeType": false,
        "activeDsInfoId": "665cq",
        "setMetadataLoading": {
            "isDataSourceFetched": true
        }
    }
}