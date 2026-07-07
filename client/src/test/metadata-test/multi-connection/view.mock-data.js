export const mdStore1 = { // for multiple connection
    "dataFetchedFor": {
        "getDatasource": true,
        "joins": false,
        "viewSessionVariables": true,
        "listDataSources": true,
        "0y9o-blgs-ykpl-ytli-g6": false,
        "rpbu-n8ia-40ra-acwx-ez": false,
        "d4ve-yci4-0qao-5gwd-qa": false,
        "ywol-rvqe-p0k8-16tu-js": false
    },
    "loadingStatus": {
        "getDatasource": true,
        "listDataSources": true,
        "0y9o-blgs-ykpl-ytli-g6": false,
        "rpbu-n8ia-40ra-acwx-ez": false,
        "d4ve-yci4-0qao-5gwd-qa": false,
        "ywol-rvqe-p0k8-16tu-js": false
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
                "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
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
                            "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/5gl1-pmhh-5ev7-7q25-53",
                            "key": "5gl1-pmhh-5ev7-7q25-53",
                            "uuid": "5gl1-pmhh-5ev7-7q25-53",
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
                            "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/mp3x-6lpm-cahk-asm1-z4",
                            "key": "mp3x-6lpm-cahk-asm1-z4",
                            "uuid": "mp3x-6lpm-cahk-asm1-z4",
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
                            "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/3pk7-dbgm-4rfg-pmyg-5f",
                            "key": "3pk7-dbgm-4rfg-pmyg-5f",
                            "uuid": "3pk7-dbgm-4rfg-pmyg-5f",
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
                                    "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez/b3ps-b99n-56ol-fmr3-zz",
                                    "key": "b3ps-b99n-56ol-fmr3-zz",
                                    "alias": "dimdate",
                                    "uuid": "b3ps-b99n-56ol-fmr3-zz",
                                    "connId": "8ec1g",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "8ec1g",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "dimdate_8ec1g",
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
                                    "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez/0wig-ta2h-3e5m-vzqz-j4",
                                    "key": "0wig-ta2h-3e5m-vzqz-j4",
                                    "alias": "employee_details",
                                    "uuid": "0wig-ta2h-3e5m-vzqz-j4",
                                    "connId": "8ec1g",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "8ec1g",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "employee_details_8ec1g",
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
                                    "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez/rzyz-rdxe-evca-8eud-xu",
                                    "key": "rzyz-rdxe-evca-8eud-xu",
                                    "alias": "geo_cordinates",
                                    "uuid": "rzyz-rdxe-evca-8eud-xu",
                                    "connId": "8ec1g",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "8ec1g",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "geo_cordinates_8ec1g",
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
                                    "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez/sdjr-x9r4-yoc1-d8qy-7d",
                                    "key": "sdjr-x9r4-yoc1-d8qy-7d",
                                    "alias": "meeting_details",
                                    "uuid": "sdjr-x9r4-yoc1-d8qy-7d",
                                    "connId": "8ec1g",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "8ec1g",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "meeting_details_8ec1g",
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
                                    "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez/0do0-109u-dn1x-fri4-ev",
                                    "key": "0do0-109u-dn1x-fri4-ev",
                                    "alias": "travel_details",
                                    "uuid": "0do0-109u-dn1x-fri4-ev",
                                    "connId": "8ec1g",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "8ec1g",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "travel_details_8ec1g",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                }
                            ],
                            "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez",
                            "key": "rpbu-n8ia-40ra-acwx-ez",
                            "uuid": "rpbu-n8ia-40ra-acwx-ez",
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
                            "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/t2uj-64l2-rrv5-6a51-vl",
                            "key": "t2uj-64l2-rrv5-6a51-vl",
                            "uuid": "t2uj-64l2-rrv5-6a51-vl",
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
                            "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/tq01-8l3n-jv2i-nnzb-sy",
                            "key": "tq01-8l3n-jv2i-nnzb-sy",
                            "uuid": "tq01-8l3n-jv2i-nnzb-sy",
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
                            "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/zk9s-qeft-17ev-8tzh-zh",
                            "key": "zk9s-qeft-17ev-8tzh-zh",
                            "uuid": "zk9s-qeft-17ev-8tzh-zh",
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
                            "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/raut-ufa3-z9f9-yxm8-kf",
                            "key": "raut-ufa3-z9f9-yxm8-kf",
                            "uuid": "raut-ufa3-z9f9-yxm8-kf",
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
                            "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/5rj5-nydn-n1fq-y3li-hs",
                            "key": "5rj5-nydn-n1fq-y3li-hs",
                            "uuid": "5rj5-nydn-n1fq-y3li-hs",
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
                            "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/6vvj-3jjn-pi8i-303x-c8",
                            "key": "6vvj-3jjn-pi8i-303x-c8",
                            "uuid": "6vvj-3jjn-pi8i-303x-c8",
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
                            "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/pzvc-ug69-n64h-viqo-sa",
                            "key": "pzvc-ug69-n64h-viqo-sa",
                            "uuid": "pzvc-ug69-n64h-viqo-sa",
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
                            "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/yzo3-7q24-nk8m-c25m-zm",
                            "key": "yzo3-7q24-nk8m-c25m-zm",
                            "uuid": "yzo3-7q24-nk8m-c25m-zm",
                            "data": {
                                "id": "1",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "SampleTravelDataDerby"
                        }
                    ],
                    "driverType": "Derby",
                    "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6",
                    "key": "0y9o-blgs-ykpl-ytli-g6",
                    "uuid": "0y9o-blgs-ykpl-ytli-g6",
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
                    "keyPath": "k6sn-vq2e-513m-xapt-hk/83gp-8vuo-8uzi-ry29-cg",
                    "key": "83gp-8vuo-8uzi-ry29-cg",
                    "uuid": "83gp-8vuo-8uzi-ry29-cg"
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
                    "keyPath": "k6sn-vq2e-513m-xapt-hk/tvvy-c588-d513-f5n2-u3",
                    "key": "tvvy-c588-d513-f5n2-u3",
                    "uuid": "tvvy-c588-d513-f5n2-u3"
                }
            ],
            "key": "k6sn-vq2e-513m-xapt-hk",
            "uuid": "k6sn-vq2e-513m-xapt-hk",
            "keyPath": "k6sn-vq2e-513m-xapt-hk",
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
                        "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                        "jdbcUrl": null,
                        "driver": "dynamicSwitch"
                    },
                    "dataSourceType": "",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "DynamicSwitch",
                    "keyPath": "2d8h-fpqd-kean-ur71-5z/d7w2-v5j0-cf17-gjke-1z",
                    "key": "d7w2-v5j0-cf17-gjke-1z",
                    "uuid": "d7w2-v5j0-cf17-gjke-1z"
                }
            ],
            "key": "2d8h-fpqd-kean-ur71-5z",
            "uuid": "2d8h-fpqd-kean-ur71-5z",
            "keyPath": "2d8h-fpqd-kean-ur71-5z",
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
                    "children": [
                        {
                            "name": "booking1662345944710kenxhxumioueuzfj",
                            "schemas": [
                                {
                                    "name": "pg_catalog",
                                    "data": {
                                        "id": "1001",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/e9l9-19zi-lwcu-hph8-t8",
                                    "key": "e9l9-19zi-lwcu-hph8-t8",
                                    "uuid": "e9l9-19zi-lwcu-hph8-t8",
                                    "category": "schema",
                                    "children": [],
                                    "catalog": "booking1662345944710kenxhxumioueuzfj",
                                    "datasourceName": "booking"
                                },
                                {
                                    "name": "information_schema",
                                    "data": {
                                        "id": "1001",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/qkwy-1rwp-yyax-wljo-nk",
                                    "key": "qkwy-1rwp-yyax-wljo-nk",
                                    "uuid": "qkwy-1rwp-yyax-wljo-nk",
                                    "category": "schema",
                                    "children": [],
                                    "catalog": "booking1662345944710kenxhxumioueuzfj",
                                    "datasourceName": "booking"
                                },
                                {
                                    "name": "public",
                                    "data": {
                                        "id": "1001",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js",
                                    "key": "ywol-rvqe-p0k8-16tu-js",
                                    "uuid": "ywol-rvqe-p0k8-16tu-js",
                                    "category": "schema",
                                    "children": [
                                        {
                                            "id": "ce367e8d42ec1b746afdd73c2a07e7b3",
                                            "name": "appartments",
                                            "data": {
                                                "id": "1001",
                                                "type": "dynamicDataSource"
                                            },
                                            "keyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js/cya8-ym2g-9mw7-ca7h-x1",
                                            "key": "cya8-ym2g-9mw7-ca7h-x1",
                                            "alias": "appartments",
                                            "uuid": "cya8-ym2g-9mw7-ca7h-x1",
                                            "connId": "4958k",
                                            "dataSource": {
                                                "id": "1001",
                                                "type": "dynamicDataSource",
                                                "baseType": "global.jdbc",
                                                "catSchemaPredicted": false,
                                                "sync": false,
                                                "catalog": "booking1662345944710kenxhxumioueuzfj",
                                                "schema": "public",
                                                "connId": "4958k",
                                                "classifier": "db.workflow",
                                                "datasourceName": "booking",
                                                "dsKeyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js",
                                                "driverType": "Postgresql",
                                                "database": "public"
                                            },
                                            "dataSourceName": "booking",
                                            "category": "table",
                                            "nameWithConnId": "appartments_4958k",
                                            "database": "public",
                                            "catalog": "booking1662345944710kenxhxumioueuzfj",
                                            "schema": "public",
                                            "selected": true
                                        },
                                        {
                                            "id": "f98912a38a7295b2af4a21d9c8df2f89",
                                            "name": "bookings",
                                            "data": {
                                                "id": "1001",
                                                "type": "dynamicDataSource"
                                            },
                                            "keyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js/a5cz-rp81-g8l9-8kho-eh",
                                            "key": "a5cz-rp81-g8l9-8kho-eh",
                                            "alias": "bookings",
                                            "uuid": "a5cz-rp81-g8l9-8kho-eh",
                                            "connId": "4958k",
                                            "dataSource": {
                                                "id": "1001",
                                                "type": "dynamicDataSource",
                                                "baseType": "global.jdbc",
                                                "catSchemaPredicted": false,
                                                "sync": false,
                                                "catalog": "booking1662345944710kenxhxumioueuzfj",
                                                "schema": "public",
                                                "connId": "4958k",
                                                "classifier": "db.workflow",
                                                "datasourceName": "booking",
                                                "dsKeyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js",
                                                "driverType": "Postgresql",
                                                "database": "public"
                                            },
                                            "dataSourceName": "booking",
                                            "category": "table",
                                            "nameWithConnId": "bookings_4958k",
                                            "database": "public",
                                            "catalog": "booking1662345944710kenxhxumioueuzfj",
                                            "schema": "public",
                                            "selected": true
                                        },
                                        {
                                            "id": "93620a4e4bdf955d0e9b132ee0ec9ae3",
                                            "name": "company",
                                            "data": {
                                                "id": "1001",
                                                "type": "dynamicDataSource"
                                            },
                                            "keyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js/ia1l-scxp-euq4-ro48-ix",
                                            "key": "ia1l-scxp-euq4-ro48-ix",
                                            "alias": "company",
                                            "uuid": "ia1l-scxp-euq4-ro48-ix",
                                            "connId": "4958k",
                                            "dataSource": {
                                                "id": "1001",
                                                "type": "dynamicDataSource",
                                                "baseType": "global.jdbc",
                                                "catSchemaPredicted": false,
                                                "sync": false,
                                                "catalog": "booking1662345944710kenxhxumioueuzfj",
                                                "schema": "public",
                                                "connId": "4958k",
                                                "classifier": "db.workflow",
                                                "datasourceName": "booking",
                                                "dsKeyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js",
                                                "driverType": "Postgresql",
                                                "database": "public"
                                            },
                                            "dataSourceName": "booking",
                                            "category": "table",
                                            "nameWithConnId": "company_4958k",
                                            "database": "public",
                                            "catalog": "booking1662345944710kenxhxumioueuzfj",
                                            "schema": "public",
                                            "selected": true
                                        },
                                        {
                                            "id": "b7b0680aec5189c9db7dd9ed1414e709",
                                            "name": "users",
                                            "data": {
                                                "id": "1001",
                                                "type": "dynamicDataSource"
                                            },
                                            "keyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js/pbhy-dze9-lym6-5h9s-si",
                                            "key": "pbhy-dze9-lym6-5h9s-si",
                                            "alias": "users",
                                            "uuid": "pbhy-dze9-lym6-5h9s-si",
                                            "connId": "4958k",
                                            "dataSource": {
                                                "id": "1001",
                                                "type": "dynamicDataSource",
                                                "baseType": "global.jdbc",
                                                "catSchemaPredicted": false,
                                                "sync": false,
                                                "catalog": "booking1662345944710kenxhxumioueuzfj",
                                                "schema": "public",
                                                "connId": "4958k",
                                                "classifier": "db.workflow",
                                                "datasourceName": "booking",
                                                "dsKeyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js",
                                                "driverType": "Postgresql",
                                                "database": "public"
                                            },
                                            "dataSourceName": "booking",
                                            "category": "table",
                                            "nameWithConnId": "users_4958k",
                                            "database": "public",
                                            "catalog": "booking1662345944710kenxhxumioueuzfj",
                                            "schema": "public",
                                            "selected": true
                                        }
                                    ],
                                    "catalog": "booking1662345944710kenxhxumioueuzfj",
                                    "datasourceName": "booking",
                                    "fetched": true
                                }
                            ],
                            "children": [
                                {
                                    "name": "pg_catalog",
                                    "data": {
                                        "id": "1001",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/e9l9-19zi-lwcu-hph8-t8",
                                    "key": "e9l9-19zi-lwcu-hph8-t8",
                                    "uuid": "e9l9-19zi-lwcu-hph8-t8",
                                    "category": "schema",
                                    "children": [],
                                    "catalog": "booking1662345944710kenxhxumioueuzfj",
                                    "datasourceName": "booking"
                                },
                                {
                                    "name": "information_schema",
                                    "data": {
                                        "id": "1001",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/qkwy-1rwp-yyax-wljo-nk",
                                    "key": "qkwy-1rwp-yyax-wljo-nk",
                                    "uuid": "qkwy-1rwp-yyax-wljo-nk",
                                    "category": "schema",
                                    "children": [],
                                    "catalog": "booking1662345944710kenxhxumioueuzfj",
                                    "datasourceName": "booking"
                                },
                                {
                                    "name": "public",
                                    "data": {
                                        "id": "1001",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js",
                                    "key": "ywol-rvqe-p0k8-16tu-js",
                                    "uuid": "ywol-rvqe-p0k8-16tu-js",
                                    "category": "schema",
                                    "children": [
                                        {
                                            "id": "ce367e8d42ec1b746afdd73c2a07e7b3",
                                            "name": "appartments",
                                            "data": {
                                                "id": "1001",
                                                "type": "dynamicDataSource"
                                            },
                                            "keyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js/cya8-ym2g-9mw7-ca7h-x1",
                                            "key": "cya8-ym2g-9mw7-ca7h-x1",
                                            "alias": "appartments",
                                            "uuid": "cya8-ym2g-9mw7-ca7h-x1",
                                            "connId": "4958k",
                                            "dataSource": {
                                                "id": "1001",
                                                "type": "dynamicDataSource",
                                                "baseType": "global.jdbc",
                                                "catSchemaPredicted": false,
                                                "sync": false,
                                                "catalog": "booking1662345944710kenxhxumioueuzfj",
                                                "schema": "public",
                                                "connId": "4958k",
                                                "classifier": "db.workflow",
                                                "datasourceName": "booking",
                                                "dsKeyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js",
                                                "driverType": "Postgresql",
                                                "database": "public"
                                            },
                                            "dataSourceName": "booking",
                                            "category": "table",
                                            "nameWithConnId": "appartments_4958k",
                                            "database": "public",
                                            "catalog": "booking1662345944710kenxhxumioueuzfj",
                                            "schema": "public",
                                            "selected": true
                                        },
                                        {
                                            "id": "f98912a38a7295b2af4a21d9c8df2f89",
                                            "name": "bookings",
                                            "data": {
                                                "id": "1001",
                                                "type": "dynamicDataSource"
                                            },
                                            "keyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js/a5cz-rp81-g8l9-8kho-eh",
                                            "key": "a5cz-rp81-g8l9-8kho-eh",
                                            "alias": "bookings",
                                            "uuid": "a5cz-rp81-g8l9-8kho-eh",
                                            "connId": "4958k",
                                            "dataSource": {
                                                "id": "1001",
                                                "type": "dynamicDataSource",
                                                "baseType": "global.jdbc",
                                                "catSchemaPredicted": false,
                                                "sync": false,
                                                "catalog": "booking1662345944710kenxhxumioueuzfj",
                                                "schema": "public",
                                                "connId": "4958k",
                                                "classifier": "db.workflow",
                                                "datasourceName": "booking",
                                                "dsKeyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js",
                                                "driverType": "Postgresql",
                                                "database": "public"
                                            },
                                            "dataSourceName": "booking",
                                            "category": "table",
                                            "nameWithConnId": "bookings_4958k",
                                            "database": "public",
                                            "catalog": "booking1662345944710kenxhxumioueuzfj",
                                            "schema": "public",
                                            "selected": true
                                        },
                                        {
                                            "id": "93620a4e4bdf955d0e9b132ee0ec9ae3",
                                            "name": "company",
                                            "data": {
                                                "id": "1001",
                                                "type": "dynamicDataSource"
                                            },
                                            "keyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js/ia1l-scxp-euq4-ro48-ix",
                                            "key": "ia1l-scxp-euq4-ro48-ix",
                                            "alias": "company",
                                            "uuid": "ia1l-scxp-euq4-ro48-ix",
                                            "connId": "4958k",
                                            "dataSource": {
                                                "id": "1001",
                                                "type": "dynamicDataSource",
                                                "baseType": "global.jdbc",
                                                "catSchemaPredicted": false,
                                                "sync": false,
                                                "catalog": "booking1662345944710kenxhxumioueuzfj",
                                                "schema": "public",
                                                "connId": "4958k",
                                                "classifier": "db.workflow",
                                                "datasourceName": "booking",
                                                "dsKeyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js",
                                                "driverType": "Postgresql",
                                                "database": "public"
                                            },
                                            "dataSourceName": "booking",
                                            "category": "table",
                                            "nameWithConnId": "company_4958k",
                                            "database": "public",
                                            "catalog": "booking1662345944710kenxhxumioueuzfj",
                                            "schema": "public",
                                            "selected": true
                                        },
                                        {
                                            "id": "b7b0680aec5189c9db7dd9ed1414e709",
                                            "name": "users",
                                            "data": {
                                                "id": "1001",
                                                "type": "dynamicDataSource"
                                            },
                                            "keyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js/pbhy-dze9-lym6-5h9s-si",
                                            "key": "pbhy-dze9-lym6-5h9s-si",
                                            "alias": "users",
                                            "uuid": "pbhy-dze9-lym6-5h9s-si",
                                            "connId": "4958k",
                                            "dataSource": {
                                                "id": "1001",
                                                "type": "dynamicDataSource",
                                                "baseType": "global.jdbc",
                                                "catSchemaPredicted": false,
                                                "sync": false,
                                                "catalog": "booking1662345944710kenxhxumioueuzfj",
                                                "schema": "public",
                                                "connId": "4958k",
                                                "classifier": "db.workflow",
                                                "datasourceName": "booking",
                                                "dsKeyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js",
                                                "driverType": "Postgresql",
                                                "database": "public"
                                            },
                                            "dataSourceName": "booking",
                                            "category": "table",
                                            "nameWithConnId": "users_4958k",
                                            "database": "public",
                                            "catalog": "booking1662345944710kenxhxumioueuzfj",
                                            "schema": "public",
                                            "selected": true
                                        }
                                    ],
                                    "catalog": "booking1662345944710kenxhxumioueuzfj",
                                    "datasourceName": "booking",
                                    "fetched": true
                                }
                            ],
                            "keyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt",
                            "key": "k3rj-d2n5-bgqf-0m6u-xt",
                            "uuid": "k3rj-d2n5-bgqf-0m6u-xt",
                            "data": {
                                "id": "1001",
                                "type": "dynamicDataSource"
                            },
                            "category": "catalog",
                            "fetched": true,
                            "datasourceName": "booking"
                        }
                    ],
                    "driverType": "Postgresql",
                    "keyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa",
                    "key": "d4ve-yci4-0qao-5gwd-qa",
                    "uuid": "d4ve-yci4-0qao-5gwd-qa"
                }
            ],
            "key": "ndnq-geq4-dmrm-z7vx-7i",
            "uuid": "ndnq-geq4-dmrm-z7vx-7i",
            "keyPath": "ndnq-geq4-dmrm-z7vx-7i",
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
                "dsUUID": "0y9o-blgs-ykpl-ytli-g6",
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
                            "name": "booking1662345944710kenxhxumioueuzfj",
                            "schemas": [
                                {
                                    "name": "public",
                                    "tables": [
                                        {
                                            "id": "ce367e8d42ec1b746afdd73c2a07e7b3",
                                            "name": "appartments"
                                        },
                                        {
                                            "id": "93620a4e4bdf955d0e9b132ee0ec9ae3",
                                            "name": "company"
                                        },
                                        {
                                            "id": "f98912a38a7295b2af4a21d9c8df2f89",
                                            "name": "bookings"
                                        },
                                        {
                                            "id": "b7b0680aec5189c9db7dd9ed1414e709",
                                            "name": "users"
                                        }
                                    ]
                                }
                            ]
                        }
                    ],
                    "dataSource": {
                        "id": "1001",
                        "type": "dynamicDataSource",
                        "baseType": "global.jdbc",
                        "catSchemaPredicted": false,
                        "sync": false,
                        "catalog": "booking1662345944710kenxhxumioueuzfj",
                        "schema": "public",
                        "connId": "4958k",
                        "classifier": "db.workflow",
                        "datasourceName": "booking",
                        "dsKeyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js",
                        "driverType": "Postgresql",
                        "database": "public"
                    },
                    "name": "booking1662345944710kenxhxumioueuzfj.public"
                }
            },
            {
                "fetchSchemas": true,
                "fetchCatalogs": true,
                "working": true,
                "connData": {
                    "id": "1001",
                    "type": "dynamicDataSource"
                },
                "connId": "1001",
                "dsUUID": "d4ve-yci4-0qao-5gwd-qa",
                "classifier": "db.workflow",
                "metadata": {
                    "catalogs": [
                        {
                            "name": "booking1662345944710kenxhxumioueuzfj",
                            "schemas": [
                                {
                                    "name": "pg_catalog",
                                    "data": {
                                        "id": "1001",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/e9l9-19zi-lwcu-hph8-t8",
                                    "key": "e9l9-19zi-lwcu-hph8-t8",
                                    "uuid": "e9l9-19zi-lwcu-hph8-t8",
                                    "category": "schema",
                                    "children": [],
                                    "catalog": "booking1662345944710kenxhxumioueuzfj",
                                    "datasourceName": "booking"
                                },
                                {
                                    "name": "information_schema",
                                    "data": {
                                        "id": "1001",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/qkwy-1rwp-yyax-wljo-nk",
                                    "key": "qkwy-1rwp-yyax-wljo-nk",
                                    "uuid": "qkwy-1rwp-yyax-wljo-nk",
                                    "category": "schema",
                                    "children": [],
                                    "catalog": "booking1662345944710kenxhxumioueuzfj",
                                    "datasourceName": "booking"
                                },
                                {
                                    "name": "public",
                                    "data": {
                                        "id": "1001",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js",
                                    "key": "ywol-rvqe-p0k8-16tu-js",
                                    "uuid": "ywol-rvqe-p0k8-16tu-js",
                                    "category": "schema",
                                    "children": [],
                                    "catalog": "booking1662345944710kenxhxumioueuzfj",
                                    "datasourceName": "booking"
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
            "type": "dynamicDataSource",
            "baseType": "global.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "",
            "schema": "HIUSER",
            "connId": "8ec1g",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelDataDerby",
            "dsKeyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez",
            "driverType": "Derby",
            "database": "HIUSER"
        },
        {
            "id": "1001",
            "type": "dynamicDataSource",
            "baseType": "global.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "booking1662345944710kenxhxumioueuzfj",
            "schema": "public",
            "connId": "4958k",
            "classifier": "db.workflow",
            "datasourceName": "booking",
            "dsKeyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js",
            "driverType": "Postgresql",
            "database": "public"
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
            "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez/b3ps-b99n-56ol-fmr3-zz",
            "key": "b3ps-b99n-56ol-fmr3-zz",
            "alias": "dimdate",
            "uuid": "b3ps-b99n-56ol-fmr3-zz",
            "connId": "8ec1g",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "8ec1g",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "dimdate_8ec1g",
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
            "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez/0wig-ta2h-3e5m-vzqz-j4",
            "key": "0wig-ta2h-3e5m-vzqz-j4",
            "alias": "employee_details",
            "uuid": "0wig-ta2h-3e5m-vzqz-j4",
            "connId": "8ec1g",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "8ec1g",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "employee_details_8ec1g",
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
            "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez/rzyz-rdxe-evca-8eud-xu",
            "key": "rzyz-rdxe-evca-8eud-xu",
            "alias": "geo_cordinates",
            "uuid": "rzyz-rdxe-evca-8eud-xu",
            "connId": "8ec1g",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "8ec1g",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "geo_cordinates_8ec1g",
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
            "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez/sdjr-x9r4-yoc1-d8qy-7d",
            "key": "sdjr-x9r4-yoc1-d8qy-7d",
            "alias": "meeting_details",
            "uuid": "sdjr-x9r4-yoc1-d8qy-7d",
            "connId": "8ec1g",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "8ec1g",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "meeting_details_8ec1g",
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
            "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez/0do0-109u-dn1x-fri4-ev",
            "key": "0do0-109u-dn1x-fri4-ev",
            "alias": "travel_details",
            "uuid": "0do0-109u-dn1x-fri4-ev",
            "connId": "8ec1g",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "8ec1g",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "travel_details_8ec1g",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "travel_details"
        },
        "appartments": {
            "id": "ce367e8d42ec1b746afdd73c2a07e7b3",
            "name": "appartments",
            "data": {
                "id": "1001",
                "type": "dynamicDataSource"
            },
            "keyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js/cya8-ym2g-9mw7-ca7h-x1",
            "key": "cya8-ym2g-9mw7-ca7h-x1",
            "alias": "appartments",
            "uuid": "cya8-ym2g-9mw7-ca7h-x1",
            "connId": "4958k",
            "dataSource": {
                "id": "1001",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "booking1662345944710kenxhxumioueuzfj",
                "schema": "public",
                "connId": "4958k",
                "classifier": "db.workflow",
                "datasourceName": "booking",
                "dsKeyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js",
                "driverType": "Postgresql",
                "database": "public"
            },
            "dataSourceName": "booking",
            "category": "table",
            "nameWithConnId": "appartments_4958k",
            "database": "public",
            "catalog": "booking1662345944710kenxhxumioueuzfj",
            "schema": "public",
            "selected": true,
            "keyName": "appartments"
        },
        "bookings": {
            "id": "f98912a38a7295b2af4a21d9c8df2f89",
            "name": "bookings",
            "data": {
                "id": "1001",
                "type": "dynamicDataSource"
            },
            "keyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js/a5cz-rp81-g8l9-8kho-eh",
            "key": "a5cz-rp81-g8l9-8kho-eh",
            "alias": "bookings",
            "uuid": "a5cz-rp81-g8l9-8kho-eh",
            "connId": "4958k",
            "dataSource": {
                "id": "1001",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "booking1662345944710kenxhxumioueuzfj",
                "schema": "public",
                "connId": "4958k",
                "classifier": "db.workflow",
                "datasourceName": "booking",
                "dsKeyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js",
                "driverType": "Postgresql",
                "database": "public"
            },
            "dataSourceName": "booking",
            "category": "table",
            "nameWithConnId": "bookings_4958k",
            "database": "public",
            "catalog": "booking1662345944710kenxhxumioueuzfj",
            "schema": "public",
            "selected": true,
            "keyName": "bookings"
        },
        "company": {
            "id": "93620a4e4bdf955d0e9b132ee0ec9ae3",
            "name": "company",
            "data": {
                "id": "1001",
                "type": "dynamicDataSource"
            },
            "keyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js/ia1l-scxp-euq4-ro48-ix",
            "key": "ia1l-scxp-euq4-ro48-ix",
            "alias": "company",
            "uuid": "ia1l-scxp-euq4-ro48-ix",
            "connId": "4958k",
            "dataSource": {
                "id": "1001",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "booking1662345944710kenxhxumioueuzfj",
                "schema": "public",
                "connId": "4958k",
                "classifier": "db.workflow",
                "datasourceName": "booking",
                "dsKeyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js",
                "driverType": "Postgresql",
                "database": "public"
            },
            "dataSourceName": "booking",
            "category": "table",
            "nameWithConnId": "company_4958k",
            "database": "public",
            "catalog": "booking1662345944710kenxhxumioueuzfj",
            "schema": "public",
            "selected": true,
            "keyName": "company"
        },
        "users": {
            "id": "b7b0680aec5189c9db7dd9ed1414e709",
            "name": "users",
            "data": {
                "id": "1001",
                "type": "dynamicDataSource"
            },
            "keyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js/pbhy-dze9-lym6-5h9s-si",
            "key": "pbhy-dze9-lym6-5h9s-si",
            "alias": "users",
            "uuid": "pbhy-dze9-lym6-5h9s-si",
            "connId": "4958k",
            "dataSource": {
                "id": "1001",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "booking1662345944710kenxhxumioueuzfj",
                "schema": "public",
                "connId": "4958k",
                "classifier": "db.workflow",
                "datasourceName": "booking",
                "dsKeyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js",
                "driverType": "Postgresql",
                "database": "public"
            },
            "dataSourceName": "booking",
            "category": "table",
            "nameWithConnId": "users_4958k",
            "database": "public",
            "catalog": "booking1662345944710kenxhxumioueuzfj",
            "schema": "public",
            "selected": true,
            "keyName": "users"
        }
    },
    "views": [],
    "activeView": false,
    "categories": {
        "k6sn-vq2e-513m-xapt-hk": {
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
        "2d8h-fpqd-kean-ur71-5z": {
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
                    "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
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
        "ndnq-geq4-dmrm-z7vx-7i": {
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
            "connId": "8ec1g",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelDataDerby",
            "dsKeyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez",
            "driverType": "Derby",
            "database": "HIUSER"
        },
        {
            "id": "1001",
            "type": "dynamicDataSource",
            "baseType": "global.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "booking1662345944710kenxhxumioueuzfj",
            "schema": "public",
            "connId": "4958k",
            "classifier": "db.workflow",
            "datasourceName": "booking",
            "dsKeyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js",
            "driverType": "Postgresql",
            "database": "public"
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
        "rzyz-rdxe-evca-8eud-xu",
        "sdjr-x9r4-yoc1-d8qy-7d",
        "0wig-ta2h-3e5m-vzqz-j4",
        "b3ps-b99n-56ol-fmr3-zz",
        "0do0-109u-dn1x-fri4-ev",
        "cya8-ym2g-9mw7-ca7h-x1",
        "ia1l-scxp-euq4-ro48-ix",
        "a5cz-rp81-g8l9-8kho-eh",
        "pbhy-dze9-lym6-5h9s-si"
    ],
    "selectedTableKeys": [
        "b3ps-b99n-56ol-fmr3-zz",
        "0wig-ta2h-3e5m-vzqz-j4",
        "rzyz-rdxe-evca-8eud-xu",
        "sdjr-x9r4-yoc1-d8qy-7d",
        "0do0-109u-dn1x-fri4-ev",
        "cya8-ym2g-9mw7-ca7h-x1",
        "a5cz-rp81-g8l9-8kho-eh",
        "ia1l-scxp-euq4-ro48-ix",
        "pbhy-dze9-lym6-5h9s-si"
    ],
    "metadataName": "Metadata_1",
    "activeDataSource": false,
    "metadataToEdit": false,
    "isSavingInProgress": false,
    "editViewsTempData": {},
    "inititalStateFromJest": false,
    "timeStamp": 1667390247842,
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
    "hasUnsavedData": true,
    "getSecurityTableData": {
        "tables": [],
        "columns": []
    },
    "doResetFormData": false,
    "tablesMergeType": false
}

export const mdStore2 = { // for single connection
    "dataFetchedFor": {
        "getDatasource": true,
        "joins": false,
        "viewSessionVariables": true,
        "listDataSources": true,
        "0y9o-blgs-ykpl-ytli-g6": false,
        "rpbu-n8ia-40ra-acwx-ez": false,
        "d4ve-yci4-0qao-5gwd-qa": false,
        "ywol-rvqe-p0k8-16tu-js": false
    },
    "loadingStatus": {
        "getDatasource": true,
        "listDataSources": true,
        "0y9o-blgs-ykpl-ytli-g6": false,
        "rpbu-n8ia-40ra-acwx-ez": false,
        "d4ve-yci4-0qao-5gwd-qa": false,
        "ywol-rvqe-p0k8-16tu-js": false
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
                "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
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
                            "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/5gl1-pmhh-5ev7-7q25-53",
                            "key": "5gl1-pmhh-5ev7-7q25-53",
                            "uuid": "5gl1-pmhh-5ev7-7q25-53",
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
                            "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/mp3x-6lpm-cahk-asm1-z4",
                            "key": "mp3x-6lpm-cahk-asm1-z4",
                            "uuid": "mp3x-6lpm-cahk-asm1-z4",
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
                            "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/3pk7-dbgm-4rfg-pmyg-5f",
                            "key": "3pk7-dbgm-4rfg-pmyg-5f",
                            "uuid": "3pk7-dbgm-4rfg-pmyg-5f",
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
                                    "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez/b3ps-b99n-56ol-fmr3-zz",
                                    "key": "b3ps-b99n-56ol-fmr3-zz",
                                    "alias": "dimdate",
                                    "uuid": "b3ps-b99n-56ol-fmr3-zz",
                                    "connId": "8ec1g",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "8ec1g",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "dimdate_8ec1g",
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
                                    "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez/0wig-ta2h-3e5m-vzqz-j4",
                                    "key": "0wig-ta2h-3e5m-vzqz-j4",
                                    "alias": "employee_details",
                                    "uuid": "0wig-ta2h-3e5m-vzqz-j4",
                                    "connId": "8ec1g",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "8ec1g",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "employee_details_8ec1g",
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
                                    "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez/rzyz-rdxe-evca-8eud-xu",
                                    "key": "rzyz-rdxe-evca-8eud-xu",
                                    "alias": "geo_cordinates",
                                    "uuid": "rzyz-rdxe-evca-8eud-xu",
                                    "connId": "8ec1g",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "8ec1g",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "geo_cordinates_8ec1g",
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
                                    "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez/sdjr-x9r4-yoc1-d8qy-7d",
                                    "key": "sdjr-x9r4-yoc1-d8qy-7d",
                                    "alias": "meeting_details",
                                    "uuid": "sdjr-x9r4-yoc1-d8qy-7d",
                                    "connId": "8ec1g",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "8ec1g",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "meeting_details_8ec1g",
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
                                    "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez/0do0-109u-dn1x-fri4-ev",
                                    "key": "0do0-109u-dn1x-fri4-ev",
                                    "alias": "travel_details",
                                    "uuid": "0do0-109u-dn1x-fri4-ev",
                                    "connId": "8ec1g",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "8ec1g",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "travel_details_8ec1g",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                }
                            ],
                            "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez",
                            "key": "rpbu-n8ia-40ra-acwx-ez",
                            "uuid": "rpbu-n8ia-40ra-acwx-ez",
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
                            "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/t2uj-64l2-rrv5-6a51-vl",
                            "key": "t2uj-64l2-rrv5-6a51-vl",
                            "uuid": "t2uj-64l2-rrv5-6a51-vl",
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
                            "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/tq01-8l3n-jv2i-nnzb-sy",
                            "key": "tq01-8l3n-jv2i-nnzb-sy",
                            "uuid": "tq01-8l3n-jv2i-nnzb-sy",
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
                            "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/zk9s-qeft-17ev-8tzh-zh",
                            "key": "zk9s-qeft-17ev-8tzh-zh",
                            "uuid": "zk9s-qeft-17ev-8tzh-zh",
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
                            "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/raut-ufa3-z9f9-yxm8-kf",
                            "key": "raut-ufa3-z9f9-yxm8-kf",
                            "uuid": "raut-ufa3-z9f9-yxm8-kf",
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
                            "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/5rj5-nydn-n1fq-y3li-hs",
                            "key": "5rj5-nydn-n1fq-y3li-hs",
                            "uuid": "5rj5-nydn-n1fq-y3li-hs",
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
                            "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/6vvj-3jjn-pi8i-303x-c8",
                            "key": "6vvj-3jjn-pi8i-303x-c8",
                            "uuid": "6vvj-3jjn-pi8i-303x-c8",
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
                            "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/pzvc-ug69-n64h-viqo-sa",
                            "key": "pzvc-ug69-n64h-viqo-sa",
                            "uuid": "pzvc-ug69-n64h-viqo-sa",
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
                            "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/yzo3-7q24-nk8m-c25m-zm",
                            "key": "yzo3-7q24-nk8m-c25m-zm",
                            "uuid": "yzo3-7q24-nk8m-c25m-zm",
                            "data": {
                                "id": "1",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "SampleTravelDataDerby"
                        }
                    ],
                    "driverType": "Derby",
                    "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6",
                    "key": "0y9o-blgs-ykpl-ytli-g6",
                    "uuid": "0y9o-blgs-ykpl-ytli-g6",
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
                    "keyPath": "k6sn-vq2e-513m-xapt-hk/83gp-8vuo-8uzi-ry29-cg",
                    "key": "83gp-8vuo-8uzi-ry29-cg",
                    "uuid": "83gp-8vuo-8uzi-ry29-cg"
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
                    "keyPath": "k6sn-vq2e-513m-xapt-hk/tvvy-c588-d513-f5n2-u3",
                    "key": "tvvy-c588-d513-f5n2-u3",
                    "uuid": "tvvy-c588-d513-f5n2-u3"
                }
            ],
            "key": "k6sn-vq2e-513m-xapt-hk",
            "uuid": "k6sn-vq2e-513m-xapt-hk",
            "keyPath": "k6sn-vq2e-513m-xapt-hk",
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
                        "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                        "jdbcUrl": null,
                        "driver": "dynamicSwitch"
                    },
                    "dataSourceType": "",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "DynamicSwitch",
                    "keyPath": "2d8h-fpqd-kean-ur71-5z/d7w2-v5j0-cf17-gjke-1z",
                    "key": "d7w2-v5j0-cf17-gjke-1z",
                    "uuid": "d7w2-v5j0-cf17-gjke-1z"
                }
            ],
            "key": "2d8h-fpqd-kean-ur71-5z",
            "uuid": "2d8h-fpqd-kean-ur71-5z",
            "keyPath": "2d8h-fpqd-kean-ur71-5z",
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
                    "children": [
                        {
                            "name": "booking1662345944710kenxhxumioueuzfj",
                            "schemas": [
                                {
                                    "name": "pg_catalog",
                                    "data": {
                                        "id": "1001",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/e9l9-19zi-lwcu-hph8-t8",
                                    "key": "e9l9-19zi-lwcu-hph8-t8",
                                    "uuid": "e9l9-19zi-lwcu-hph8-t8",
                                    "category": "schema",
                                    "children": [],
                                    "catalog": "booking1662345944710kenxhxumioueuzfj",
                                    "datasourceName": "booking"
                                },
                                {
                                    "name": "information_schema",
                                    "data": {
                                        "id": "1001",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/qkwy-1rwp-yyax-wljo-nk",
                                    "key": "qkwy-1rwp-yyax-wljo-nk",
                                    "uuid": "qkwy-1rwp-yyax-wljo-nk",
                                    "category": "schema",
                                    "children": [],
                                    "catalog": "booking1662345944710kenxhxumioueuzfj",
                                    "datasourceName": "booking"
                                },
                                {
                                    "name": "public",
                                    "data": {
                                        "id": "1001",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js",
                                    "key": "ywol-rvqe-p0k8-16tu-js",
                                    "uuid": "ywol-rvqe-p0k8-16tu-js",
                                    "category": "schema",
                                    "children": [
                                        {
                                            "id": "ce367e8d42ec1b746afdd73c2a07e7b3",
                                            "name": "appartments",
                                            "data": {
                                                "id": "1001",
                                                "type": "dynamicDataSource"
                                            },
                                            "keyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js/cya8-ym2g-9mw7-ca7h-x1",
                                            "key": "cya8-ym2g-9mw7-ca7h-x1",
                                            "alias": "appartments",
                                            "uuid": "cya8-ym2g-9mw7-ca7h-x1",
                                            "connId": "4958k",
                                            "dataSource": {
                                                "id": "1001",
                                                "type": "dynamicDataSource",
                                                "baseType": "global.jdbc",
                                                "catSchemaPredicted": false,
                                                "sync": false,
                                                "catalog": "booking1662345944710kenxhxumioueuzfj",
                                                "schema": "public",
                                                "connId": "4958k",
                                                "classifier": "db.workflow",
                                                "datasourceName": "booking",
                                                "dsKeyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js",
                                                "driverType": "Postgresql",
                                                "database": "public"
                                            },
                                            "dataSourceName": "booking",
                                            "category": "table",
                                            "nameWithConnId": "appartments_4958k",
                                            "database": "public",
                                            "catalog": "booking1662345944710kenxhxumioueuzfj",
                                            "schema": "public",
                                            "selected": false
                                        },
                                        {
                                            "id": "f98912a38a7295b2af4a21d9c8df2f89",
                                            "name": "bookings",
                                            "data": {
                                                "id": "1001",
                                                "type": "dynamicDataSource"
                                            },
                                            "keyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js/a5cz-rp81-g8l9-8kho-eh",
                                            "key": "a5cz-rp81-g8l9-8kho-eh",
                                            "alias": "bookings",
                                            "uuid": "a5cz-rp81-g8l9-8kho-eh",
                                            "connId": "4958k",
                                            "dataSource": {
                                                "id": "1001",
                                                "type": "dynamicDataSource",
                                                "baseType": "global.jdbc",
                                                "catSchemaPredicted": false,
                                                "sync": false,
                                                "catalog": "booking1662345944710kenxhxumioueuzfj",
                                                "schema": "public",
                                                "connId": "4958k",
                                                "classifier": "db.workflow",
                                                "datasourceName": "booking",
                                                "dsKeyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js",
                                                "driverType": "Postgresql",
                                                "database": "public"
                                            },
                                            "dataSourceName": "booking",
                                            "category": "table",
                                            "nameWithConnId": "bookings_4958k",
                                            "database": "public",
                                            "catalog": "booking1662345944710kenxhxumioueuzfj",
                                            "schema": "public",
                                            "selected": false
                                        },
                                        {
                                            "id": "93620a4e4bdf955d0e9b132ee0ec9ae3",
                                            "name": "company",
                                            "data": {
                                                "id": "1001",
                                                "type": "dynamicDataSource"
                                            },
                                            "keyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js/ia1l-scxp-euq4-ro48-ix",
                                            "key": "ia1l-scxp-euq4-ro48-ix",
                                            "alias": "company",
                                            "uuid": "ia1l-scxp-euq4-ro48-ix",
                                            "connId": "4958k",
                                            "dataSource": {
                                                "id": "1001",
                                                "type": "dynamicDataSource",
                                                "baseType": "global.jdbc",
                                                "catSchemaPredicted": false,
                                                "sync": false,
                                                "catalog": "booking1662345944710kenxhxumioueuzfj",
                                                "schema": "public",
                                                "connId": "4958k",
                                                "classifier": "db.workflow",
                                                "datasourceName": "booking",
                                                "dsKeyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js",
                                                "driverType": "Postgresql",
                                                "database": "public"
                                            },
                                            "dataSourceName": "booking",
                                            "category": "table",
                                            "nameWithConnId": "company_4958k",
                                            "database": "public",
                                            "catalog": "booking1662345944710kenxhxumioueuzfj",
                                            "schema": "public",
                                            "selected": false
                                        },
                                        {
                                            "id": "b7b0680aec5189c9db7dd9ed1414e709",
                                            "name": "users",
                                            "data": {
                                                "id": "1001",
                                                "type": "dynamicDataSource"
                                            },
                                            "keyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js/pbhy-dze9-lym6-5h9s-si",
                                            "key": "pbhy-dze9-lym6-5h9s-si",
                                            "alias": "users",
                                            "uuid": "pbhy-dze9-lym6-5h9s-si",
                                            "connId": "4958k",
                                            "dataSource": {
                                                "id": "1001",
                                                "type": "dynamicDataSource",
                                                "baseType": "global.jdbc",
                                                "catSchemaPredicted": false,
                                                "sync": false,
                                                "catalog": "booking1662345944710kenxhxumioueuzfj",
                                                "schema": "public",
                                                "connId": "4958k",
                                                "classifier": "db.workflow",
                                                "datasourceName": "booking",
                                                "dsKeyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js",
                                                "driverType": "Postgresql",
                                                "database": "public"
                                            },
                                            "dataSourceName": "booking",
                                            "category": "table",
                                            "nameWithConnId": "users_4958k",
                                            "database": "public",
                                            "catalog": "booking1662345944710kenxhxumioueuzfj",
                                            "schema": "public",
                                            "selected": false
                                        }
                                    ],
                                    "catalog": "booking1662345944710kenxhxumioueuzfj",
                                    "datasourceName": "booking",
                                    "fetched": true
                                }
                            ],
                            "children": [
                                {
                                    "name": "pg_catalog",
                                    "data": {
                                        "id": "1001",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/e9l9-19zi-lwcu-hph8-t8",
                                    "key": "e9l9-19zi-lwcu-hph8-t8",
                                    "uuid": "e9l9-19zi-lwcu-hph8-t8",
                                    "category": "schema",
                                    "children": [],
                                    "catalog": "booking1662345944710kenxhxumioueuzfj",
                                    "datasourceName": "booking"
                                },
                                {
                                    "name": "information_schema",
                                    "data": {
                                        "id": "1001",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/qkwy-1rwp-yyax-wljo-nk",
                                    "key": "qkwy-1rwp-yyax-wljo-nk",
                                    "uuid": "qkwy-1rwp-yyax-wljo-nk",
                                    "category": "schema",
                                    "children": [],
                                    "catalog": "booking1662345944710kenxhxumioueuzfj",
                                    "datasourceName": "booking"
                                },
                                {
                                    "name": "public",
                                    "data": {
                                        "id": "1001",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js",
                                    "key": "ywol-rvqe-p0k8-16tu-js",
                                    "uuid": "ywol-rvqe-p0k8-16tu-js",
                                    "category": "schema",
                                    "children": [
                                        {
                                            "id": "ce367e8d42ec1b746afdd73c2a07e7b3",
                                            "name": "appartments",
                                            "data": {
                                                "id": "1001",
                                                "type": "dynamicDataSource"
                                            },
                                            "keyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js/cya8-ym2g-9mw7-ca7h-x1",
                                            "key": "cya8-ym2g-9mw7-ca7h-x1",
                                            "alias": "appartments",
                                            "uuid": "cya8-ym2g-9mw7-ca7h-x1",
                                            "connId": "4958k",
                                            "dataSource": {
                                                "id": "1001",
                                                "type": "dynamicDataSource",
                                                "baseType": "global.jdbc",
                                                "catSchemaPredicted": false,
                                                "sync": false,
                                                "catalog": "booking1662345944710kenxhxumioueuzfj",
                                                "schema": "public",
                                                "connId": "4958k",
                                                "classifier": "db.workflow",
                                                "datasourceName": "booking",
                                                "dsKeyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js",
                                                "driverType": "Postgresql",
                                                "database": "public"
                                            },
                                            "dataSourceName": "booking",
                                            "category": "table",
                                            "nameWithConnId": "appartments_4958k",
                                            "database": "public",
                                            "catalog": "booking1662345944710kenxhxumioueuzfj",
                                            "schema": "public",
                                            "selected": false
                                        },
                                        {
                                            "id": "f98912a38a7295b2af4a21d9c8df2f89",
                                            "name": "bookings",
                                            "data": {
                                                "id": "1001",
                                                "type": "dynamicDataSource"
                                            },
                                            "keyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js/a5cz-rp81-g8l9-8kho-eh",
                                            "key": "a5cz-rp81-g8l9-8kho-eh",
                                            "alias": "bookings",
                                            "uuid": "a5cz-rp81-g8l9-8kho-eh",
                                            "connId": "4958k",
                                            "dataSource": {
                                                "id": "1001",
                                                "type": "dynamicDataSource",
                                                "baseType": "global.jdbc",
                                                "catSchemaPredicted": false,
                                                "sync": false,
                                                "catalog": "booking1662345944710kenxhxumioueuzfj",
                                                "schema": "public",
                                                "connId": "4958k",
                                                "classifier": "db.workflow",
                                                "datasourceName": "booking",
                                                "dsKeyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js",
                                                "driverType": "Postgresql",
                                                "database": "public"
                                            },
                                            "dataSourceName": "booking",
                                            "category": "table",
                                            "nameWithConnId": "bookings_4958k",
                                            "database": "public",
                                            "catalog": "booking1662345944710kenxhxumioueuzfj",
                                            "schema": "public",
                                            "selected": false
                                        },
                                        {
                                            "id": "93620a4e4bdf955d0e9b132ee0ec9ae3",
                                            "name": "company",
                                            "data": {
                                                "id": "1001",
                                                "type": "dynamicDataSource"
                                            },
                                            "keyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js/ia1l-scxp-euq4-ro48-ix",
                                            "key": "ia1l-scxp-euq4-ro48-ix",
                                            "alias": "company",
                                            "uuid": "ia1l-scxp-euq4-ro48-ix",
                                            "connId": "4958k",
                                            "dataSource": {
                                                "id": "1001",
                                                "type": "dynamicDataSource",
                                                "baseType": "global.jdbc",
                                                "catSchemaPredicted": false,
                                                "sync": false,
                                                "catalog": "booking1662345944710kenxhxumioueuzfj",
                                                "schema": "public",
                                                "connId": "4958k",
                                                "classifier": "db.workflow",
                                                "datasourceName": "booking",
                                                "dsKeyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js",
                                                "driverType": "Postgresql",
                                                "database": "public"
                                            },
                                            "dataSourceName": "booking",
                                            "category": "table",
                                            "nameWithConnId": "company_4958k",
                                            "database": "public",
                                            "catalog": "booking1662345944710kenxhxumioueuzfj",
                                            "schema": "public",
                                            "selected": false
                                        },
                                        {
                                            "id": "b7b0680aec5189c9db7dd9ed1414e709",
                                            "name": "users",
                                            "data": {
                                                "id": "1001",
                                                "type": "dynamicDataSource"
                                            },
                                            "keyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js/pbhy-dze9-lym6-5h9s-si",
                                            "key": "pbhy-dze9-lym6-5h9s-si",
                                            "alias": "users",
                                            "uuid": "pbhy-dze9-lym6-5h9s-si",
                                            "connId": "4958k",
                                            "dataSource": {
                                                "id": "1001",
                                                "type": "dynamicDataSource",
                                                "baseType": "global.jdbc",
                                                "catSchemaPredicted": false,
                                                "sync": false,
                                                "catalog": "booking1662345944710kenxhxumioueuzfj",
                                                "schema": "public",
                                                "connId": "4958k",
                                                "classifier": "db.workflow",
                                                "datasourceName": "booking",
                                                "dsKeyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js",
                                                "driverType": "Postgresql",
                                                "database": "public"
                                            },
                                            "dataSourceName": "booking",
                                            "category": "table",
                                            "nameWithConnId": "users_4958k",
                                            "database": "public",
                                            "catalog": "booking1662345944710kenxhxumioueuzfj",
                                            "schema": "public",
                                            "selected": false
                                        }
                                    ],
                                    "catalog": "booking1662345944710kenxhxumioueuzfj",
                                    "datasourceName": "booking",
                                    "fetched": true
                                }
                            ],
                            "keyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt",
                            "key": "k3rj-d2n5-bgqf-0m6u-xt",
                            "uuid": "k3rj-d2n5-bgqf-0m6u-xt",
                            "data": {
                                "id": "1001",
                                "type": "dynamicDataSource"
                            },
                            "category": "catalog",
                            "fetched": true,
                            "datasourceName": "booking"
                        }
                    ],
                    "driverType": "Postgresql",
                    "keyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa",
                    "key": "d4ve-yci4-0qao-5gwd-qa",
                    "uuid": "d4ve-yci4-0qao-5gwd-qa"
                }
            ],
            "key": "ndnq-geq4-dmrm-z7vx-7i",
            "uuid": "ndnq-geq4-dmrm-z7vx-7i",
            "keyPath": "ndnq-geq4-dmrm-z7vx-7i",
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
                "dsUUID": "0y9o-blgs-ykpl-ytli-g6",
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
                            "name": "booking1662345944710kenxhxumioueuzfj",
                            "schemas": [
                                {
                                    "name": "public",
                                    "tables": [
                                        {
                                            "id": "ce367e8d42ec1b746afdd73c2a07e7b3",
                                            "name": "appartments"
                                        },
                                        {
                                            "id": "93620a4e4bdf955d0e9b132ee0ec9ae3",
                                            "name": "company"
                                        },
                                        {
                                            "id": "f98912a38a7295b2af4a21d9c8df2f89",
                                            "name": "bookings"
                                        },
                                        {
                                            "id": "b7b0680aec5189c9db7dd9ed1414e709",
                                            "name": "users"
                                        }
                                    ]
                                }
                            ]
                        }
                    ],
                    "dataSource": {
                        "id": "1001",
                        "type": "dynamicDataSource",
                        "baseType": "global.jdbc",
                        "catSchemaPredicted": false,
                        "sync": false,
                        "catalog": "booking1662345944710kenxhxumioueuzfj",
                        "schema": "public",
                        "connId": "4958k",
                        "classifier": "db.workflow",
                        "datasourceName": "booking",
                        "dsKeyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js",
                        "driverType": "Postgresql",
                        "database": "public"
                    },
                    "name": "booking1662345944710kenxhxumioueuzfj.public"
                }
            },
            {
                "fetchSchemas": true,
                "fetchCatalogs": true,
                "working": true,
                "connData": {
                    "id": "1001",
                    "type": "dynamicDataSource"
                },
                "connId": "1001",
                "dsUUID": "d4ve-yci4-0qao-5gwd-qa",
                "classifier": "db.workflow",
                "metadata": {
                    "catalogs": [
                        {
                            "name": "booking1662345944710kenxhxumioueuzfj",
                            "schemas": [
                                {
                                    "name": "pg_catalog",
                                    "data": {
                                        "id": "1001",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/e9l9-19zi-lwcu-hph8-t8",
                                    "key": "e9l9-19zi-lwcu-hph8-t8",
                                    "uuid": "e9l9-19zi-lwcu-hph8-t8",
                                    "category": "schema",
                                    "children": [],
                                    "catalog": "booking1662345944710kenxhxumioueuzfj",
                                    "datasourceName": "booking"
                                },
                                {
                                    "name": "information_schema",
                                    "data": {
                                        "id": "1001",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/qkwy-1rwp-yyax-wljo-nk",
                                    "key": "qkwy-1rwp-yyax-wljo-nk",
                                    "uuid": "qkwy-1rwp-yyax-wljo-nk",
                                    "category": "schema",
                                    "children": [],
                                    "catalog": "booking1662345944710kenxhxumioueuzfj",
                                    "datasourceName": "booking"
                                },
                                {
                                    "name": "public",
                                    "data": {
                                        "id": "1001",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js",
                                    "key": "ywol-rvqe-p0k8-16tu-js",
                                    "uuid": "ywol-rvqe-p0k8-16tu-js",
                                    "category": "schema",
                                    "children": [],
                                    "catalog": "booking1662345944710kenxhxumioueuzfj",
                                    "datasourceName": "booking"
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
            "type": "dynamicDataSource",
            "baseType": "global.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "",
            "schema": "HIUSER",
            "connId": "8ec1g",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelDataDerby",
            "dsKeyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez",
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
            "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez/b3ps-b99n-56ol-fmr3-zz",
            "key": "b3ps-b99n-56ol-fmr3-zz",
            "alias": "dimdate",
            "uuid": "b3ps-b99n-56ol-fmr3-zz",
            "connId": "8ec1g",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "8ec1g",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "dimdate_8ec1g",
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
            "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez/0wig-ta2h-3e5m-vzqz-j4",
            "key": "0wig-ta2h-3e5m-vzqz-j4",
            "alias": "employee_details",
            "uuid": "0wig-ta2h-3e5m-vzqz-j4",
            "connId": "8ec1g",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "8ec1g",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "employee_details_8ec1g",
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
            "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez/rzyz-rdxe-evca-8eud-xu",
            "key": "rzyz-rdxe-evca-8eud-xu",
            "alias": "geo_cordinates",
            "uuid": "rzyz-rdxe-evca-8eud-xu",
            "connId": "8ec1g",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "8ec1g",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "geo_cordinates_8ec1g",
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
            "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez/sdjr-x9r4-yoc1-d8qy-7d",
            "key": "sdjr-x9r4-yoc1-d8qy-7d",
            "alias": "meeting_details",
            "uuid": "sdjr-x9r4-yoc1-d8qy-7d",
            "connId": "8ec1g",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "8ec1g",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "meeting_details_8ec1g",
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
            "keyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez/0do0-109u-dn1x-fri4-ev",
            "key": "0do0-109u-dn1x-fri4-ev",
            "alias": "travel_details",
            "uuid": "0do0-109u-dn1x-fri4-ev",
            "connId": "8ec1g",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "8ec1g",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "travel_details_8ec1g",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "travel_details"
        }
    },
    "views": [],
    "activeView": false,
    "categories": {
        "k6sn-vq2e-513m-xapt-hk": {
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
        "2d8h-fpqd-kean-ur71-5z": {
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
                    "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
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
        "ndnq-geq4-dmrm-z7vx-7i": {
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
            "connId": "8ec1g",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelDataDerby",
            "dsKeyPath": "k6sn-vq2e-513m-xapt-hk/0y9o-blgs-ykpl-ytli-g6/rpbu-n8ia-40ra-acwx-ez",
            "driverType": "Derby",
            "database": "HIUSER"
        }
    ],
    "changeDSList": {},
    "changedTables": [],
    "changedColumns": [],
    "removedTables": [
        {
            "id": "b7b0680aec5189c9db7dd9ed1414e709",
            "connId": "4958k"
        },
        {
            "id": "ce367e8d42ec1b746afdd73c2a07e7b3",
            "connId": "4958k"
        },
        {
            "id": "93620a4e4bdf955d0e9b132ee0ec9ae3",
            "connId": "4958k"
        },
        {
            "id": "f98912a38a7295b2af4a21d9c8df2f89",
            "connId": "4958k"
        }
    ],
    "removedColumns": [],
    "removedDataSources": [
        {
            "id": "1001",
            "type": "dynamicDataSource",
            "baseType": "global.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "booking1662345944710kenxhxumioueuzfj",
            "schema": "public",
            "connId": "4958k",
            "classifier": "db.workflow",
            "datasourceName": "booking",
            "dsKeyPath": "ndnq-geq4-dmrm-z7vx-7i/d4ve-yci4-0qao-5gwd-qa/k3rj-d2n5-bgqf-0m6u-xt/ywol-rvqe-p0k8-16tu-js",
            "driverType": "Postgresql",
            "database": "public"
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
        "rzyz-rdxe-evca-8eud-xu",
        "sdjr-x9r4-yoc1-d8qy-7d",
        "0wig-ta2h-3e5m-vzqz-j4",
        "b3ps-b99n-56ol-fmr3-zz",
        "0do0-109u-dn1x-fri4-ev",
        "cya8-ym2g-9mw7-ca7h-x1",
        "ia1l-scxp-euq4-ro48-ix",
        "a5cz-rp81-g8l9-8kho-eh",
        "pbhy-dze9-lym6-5h9s-si"
    ],
    "selectedTableKeys": [
        "b3ps-b99n-56ol-fmr3-zz",
        "0wig-ta2h-3e5m-vzqz-j4",
        "rzyz-rdxe-evca-8eud-xu",
        "sdjr-x9r4-yoc1-d8qy-7d",
        "0do0-109u-dn1x-fri4-ev"
    ],
    "metadataName": "Metadata_1",
    "activeDataSource": false,
    "metadataToEdit": false,
    "isSavingInProgress": false,
    "editViewsTempData": {},
    "inititalStateFromJest": false,
    "timeStamp": 1667390247842,
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
    "hasUnsavedData": true,
    "getSecurityTableData": {
        "tables": [],
        "columns": []
    },
    "doResetFormData": false,
    "tablesMergeType": false
}