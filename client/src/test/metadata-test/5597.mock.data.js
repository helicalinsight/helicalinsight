export const store5597 = {
    "dataFetchedFor": {
        "getDatasource": true,
        "joins": true,
        "viewSessionVariables": false,
        "listDataSources": true,
        "dfvh-azrf-vt31-6szs-i1": false,
        "fgur-2uhf-t7c2-6glg-w0": true
    },
    "loadingStatus": {
        "getDatasource": true,
        "listDataSources": true,
        "joins": true,
        "dfvh-azrf-vt31-6szs-i1": false,
        "fgur-2uhf-t7c2-6glg-w0": true
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
            "driver": "com.mysql.jdbc.Driver",
            "data": {
                "dir": "",
                "driver": "com.mysql.jdbc.Driver",
                "type": "sql.jdbc",
                "id": 101,
                "username": "bugzilla",
                "password": "bugzilla@QA",
                "url": "jdbc:mysql://localhost:3306/SampleTravelData"
            },
            "name": "mysqlplainjdbc",
            "classifier": "efwd",
            "type": "sql.jdbc",
            "dataSourceType": "Plain Jdbc DataSource"
        },
        {
            "permissionLevel": 5,
            "driver": "com.mysql.jdbc.Driver",
            "data": {
                "dir": "TestFold",
                "driver": "com.mysql.jdbc.Driver",
                "type": "sql.jdbc.groovy",
                "id": 1,
                "username": "hiuser",
                "password": "sdfsfsdf",
                "condition": "import net.sf.json.JSONObject;\n    import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n    public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        responseJson.put(\"driver\", \"com.mysql.jdbc.Driver\");\n        responseJson.put(\"url\", \"jdbc:mysql://localhost:3306/\" + userName);\n        responseJson.put(\"user\", \"root\");\n        responseJson.put(\"pass\", \"root\");\n        return responseJson;\n    }",
                "url": "jdbc:mysql://127.0.0.1:3306/bugzilla"
            },
            "name": "bugzill",
            "classifier": "efwd",
            "type": "sql.jdbc.groovy",
            "dataSourceType": "Plain Groovy DataSource"
        },
        {
            "permissionLevel": 5,
            "driver": "org.postgresql.Driver",
            "data": {
                "dir": "Gagan",
                "driver": "org.postgresql.Driver",
                "type": "sql.jdbc.groovy",
                "id": 304,
                "username": "zlvkhftkogyvevhyxvalzgvu@psql-mock-database-cloud",
                "password": "srnlevoznktjiwoutcsbhgqr",
                "condition": "import net.sf.json.JSONObject;\n    import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n    public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        responseJson.put(\"driver\", \"com.mysql.jdbc.Driver\");\n        responseJson.put(\"url\", \"jdbc:mysql://localhost:3306/\" + userName);\n        responseJson.put(\"user\", \"root\");\n        responseJson.put(\"pass\", \"root\");\n        return responseJson;\n    }",
                "url": "jdbc:postgresql://psql-mock-database-cloud.postgres.database.azure.com:5432/booking1662345944710kenxhxumioueuzfj"
            },
            "name": "booking",
            "classifier": "efwd",
            "type": "sql.jdbc.groovy",
            "dataSourceType": "Plain Groovy DataSource"
        },
        {
            "permissionLevel": 5,
            "driver": null,
            "data": {
                "dir": "Gagan",
                "driver": null,
                "type": "sql.jdbc.groovy.managed",
                "id": 303,
                "username": null,
                "password": null,
                "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                "url": null
            },
            "name": "new conn",
            "classifier": "efwd",
            "type": "sql.jdbc.groovy.managed",
            "dataSourceType": "Plain Groovy Managed DataSource"
        },
        {
            "permissionLevel": 5,
            "driver": null,
            "data": {
                "dir": "Gagan",
                "driver": null,
                "type": "sql.jdbc.groovy.managed",
                "id": 201,
                "username": null,
                "password": null,
                "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n          responseJson.put(\"globalId\", 3);\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 4);\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                "url": null
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
                            "keyPath": "b8pc-8anq-tzkb-wsic-yu/dfvh-azrf-vt31-6szs-i1/ygxu-t2gj-txgq-m6ds-9h",
                            "key": "ygxu-t2gj-txgq-m6ds-9h",
                            "uuid": "ygxu-t2gj-txgq-m6ds-9h",
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
                            "keyPath": "b8pc-8anq-tzkb-wsic-yu/dfvh-azrf-vt31-6szs-i1/lsz6-ov9o-upr4-czzj-m9",
                            "key": "lsz6-ov9o-upr4-czzj-m9",
                            "uuid": "lsz6-ov9o-upr4-czzj-m9",
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
                            "keyPath": "b8pc-8anq-tzkb-wsic-yu/dfvh-azrf-vt31-6szs-i1/y738-b1kj-txfr-qaea-5y",
                            "key": "y738-b1kj-txfr-qaea-5y",
                            "uuid": "y738-b1kj-txfr-qaea-5y",
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
                                    "keyPath": "b8pc-8anq-tzkb-wsic-yu/dfvh-azrf-vt31-6szs-i1/fgur-2uhf-t7c2-6glg-w0/4e1x-zz7k-k3b0-c9ug-81",
                                    "key": "4e1x-zz7k-k3b0-c9ug-81",
                                    "alias": "dimdate",
                                    "uuid": "4e1x-zz7k-k3b0-c9ug-81",
                                    "connId": "4otw5",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "4otw5",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "b8pc-8anq-tzkb-wsic-yu/dfvh-azrf-vt31-6szs-i1/fgur-2uhf-t7c2-6glg-w0",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "dimdate_4otw5",
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
                                    "keyPath": "b8pc-8anq-tzkb-wsic-yu/dfvh-azrf-vt31-6szs-i1/fgur-2uhf-t7c2-6glg-w0/mcbb-9e3e-4mbn-tnvo-1t",
                                    "key": "mcbb-9e3e-4mbn-tnvo-1t",
                                    "alias": "employee_details",
                                    "uuid": "mcbb-9e3e-4mbn-tnvo-1t",
                                    "connId": "4otw5",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "4otw5",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "b8pc-8anq-tzkb-wsic-yu/dfvh-azrf-vt31-6szs-i1/fgur-2uhf-t7c2-6glg-w0",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "employee_details_4otw5",
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
                                    "keyPath": "b8pc-8anq-tzkb-wsic-yu/dfvh-azrf-vt31-6szs-i1/fgur-2uhf-t7c2-6glg-w0/eseh-b3e4-rx4f-wzzg-xk",
                                    "key": "eseh-b3e4-rx4f-wzzg-xk",
                                    "alias": "geo_cordinates",
                                    "uuid": "eseh-b3e4-rx4f-wzzg-xk",
                                    "connId": "4otw5",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "4otw5",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "b8pc-8anq-tzkb-wsic-yu/dfvh-azrf-vt31-6szs-i1/fgur-2uhf-t7c2-6glg-w0",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "geo_cordinates_4otw5",
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
                                    "keyPath": "b8pc-8anq-tzkb-wsic-yu/dfvh-azrf-vt31-6szs-i1/fgur-2uhf-t7c2-6glg-w0/frx7-yu73-y6y5-lwm3-86",
                                    "key": "frx7-yu73-y6y5-lwm3-86",
                                    "alias": "meeting_details",
                                    "uuid": "frx7-yu73-y6y5-lwm3-86",
                                    "connId": "4otw5",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "4otw5",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "b8pc-8anq-tzkb-wsic-yu/dfvh-azrf-vt31-6szs-i1/fgur-2uhf-t7c2-6glg-w0",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "meeting_details_4otw5",
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
                                    "keyPath": "b8pc-8anq-tzkb-wsic-yu/dfvh-azrf-vt31-6szs-i1/fgur-2uhf-t7c2-6glg-w0/ndv3-nqt5-syr3-zicb-br",
                                    "key": "ndv3-nqt5-syr3-zicb-br",
                                    "alias": "travel_details",
                                    "uuid": "ndv3-nqt5-syr3-zicb-br",
                                    "connId": "4otw5",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "4otw5",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "b8pc-8anq-tzkb-wsic-yu/dfvh-azrf-vt31-6szs-i1/fgur-2uhf-t7c2-6glg-w0",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "travel_details_4otw5",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                }
                            ],
                            "keyPath": "b8pc-8anq-tzkb-wsic-yu/dfvh-azrf-vt31-6szs-i1/fgur-2uhf-t7c2-6glg-w0",
                            "key": "fgur-2uhf-t7c2-6glg-w0",
                            "uuid": "fgur-2uhf-t7c2-6glg-w0",
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
                            "keyPath": "b8pc-8anq-tzkb-wsic-yu/dfvh-azrf-vt31-6szs-i1/ey0d-aq9s-3emw-jm2l-19",
                            "key": "ey0d-aq9s-3emw-jm2l-19",
                            "uuid": "ey0d-aq9s-3emw-jm2l-19",
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
                            "keyPath": "b8pc-8anq-tzkb-wsic-yu/dfvh-azrf-vt31-6szs-i1/rfwq-eh0g-s3ym-5w0x-pa",
                            "key": "rfwq-eh0g-s3ym-5w0x-pa",
                            "uuid": "rfwq-eh0g-s3ym-5w0x-pa",
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
                            "keyPath": "b8pc-8anq-tzkb-wsic-yu/dfvh-azrf-vt31-6szs-i1/o55x-krod-wjhw-dew0-vl",
                            "key": "o55x-krod-wjhw-dew0-vl",
                            "uuid": "o55x-krod-wjhw-dew0-vl",
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
                            "keyPath": "b8pc-8anq-tzkb-wsic-yu/dfvh-azrf-vt31-6szs-i1/jo5v-an9q-7b11-iim6-3c",
                            "key": "jo5v-an9q-7b11-iim6-3c",
                            "uuid": "jo5v-an9q-7b11-iim6-3c",
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
                            "keyPath": "b8pc-8anq-tzkb-wsic-yu/dfvh-azrf-vt31-6szs-i1/0xe1-6v3y-4tba-cna5-q9",
                            "key": "0xe1-6v3y-4tba-cna5-q9",
                            "uuid": "0xe1-6v3y-4tba-cna5-q9",
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
                            "keyPath": "b8pc-8anq-tzkb-wsic-yu/dfvh-azrf-vt31-6szs-i1/2gci-1c0e-fhw5-y6vv-16",
                            "key": "2gci-1c0e-fhw5-y6vv-16",
                            "uuid": "2gci-1c0e-fhw5-y6vv-16",
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
                            "keyPath": "b8pc-8anq-tzkb-wsic-yu/dfvh-azrf-vt31-6szs-i1/oudu-ip0o-aw9p-lt6k-sz",
                            "key": "oudu-ip0o-aw9p-lt6k-sz",
                            "uuid": "oudu-ip0o-aw9p-lt6k-sz",
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
                            "keyPath": "b8pc-8anq-tzkb-wsic-yu/dfvh-azrf-vt31-6szs-i1/woy0-25ao-e390-lqop-gp",
                            "key": "woy0-25ao-e390-lqop-gp",
                            "uuid": "woy0-25ao-e390-lqop-gp",
                            "data": {
                                "id": "1",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "SampleTravelDataDerby"
                        }
                    ],
                    "driverType": "Derby",
                    "keyPath": "b8pc-8anq-tzkb-wsic-yu/dfvh-azrf-vt31-6szs-i1",
                    "key": "dfvh-azrf-vt31-6szs-i1",
                    "uuid": "dfvh-azrf-vt31-6szs-i1",
                    "fetched": true
                }
            ],
            "key": "b8pc-8anq-tzkb-wsic-yu",
            "uuid": "b8pc-8anq-tzkb-wsic-yu",
            "keyPath": "b8pc-8anq-tzkb-wsic-yu",
            "category": "dsGroup",
            "imgUrl": "images/data_sources/defaut_datasource.png"
        },
        {
            "name": "Mysql",
            "children": [
                {
                    "permissionLevel": 5,
                    "driver": "com.mysql.jdbc.Driver",
                    "data": {
                        "dir": "",
                        "driver": "com.mysql.jdbc.Driver",
                        "type": "sql.jdbc",
                        "id": 101,
                        "username": "bugzilla",
                        "password": "bugzilla@QA",
                        "url": "jdbc:mysql://localhost:3306/SampleTravelData"
                    },
                    "name": "mysqlplainjdbc",
                    "classifier": "efwd",
                    "type": "sql.jdbc",
                    "dataSourceType": "Plain Jdbc DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Mysql",
                    "keyPath": "81ub-0qiz-3yn2-kczr-wo/jony-3k78-fu1h-r6tm-mz",
                    "key": "jony-3k78-fu1h-r6tm-mz",
                    "uuid": "jony-3k78-fu1h-r6tm-mz"
                },
                {
                    "permissionLevel": 5,
                    "driver": "com.mysql.jdbc.Driver",
                    "data": {
                        "dir": "TestFold",
                        "driver": "com.mysql.jdbc.Driver",
                        "type": "sql.jdbc.groovy",
                        "id": 1,
                        "username": "hiuser",
                        "password": "sdfsfsdf",
                        "condition": "import net.sf.json.JSONObject;\n    import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n    public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        responseJson.put(\"driver\", \"com.mysql.jdbc.Driver\");\n        responseJson.put(\"url\", \"jdbc:mysql://localhost:3306/\" + userName);\n        responseJson.put(\"user\", \"root\");\n        responseJson.put(\"pass\", \"root\");\n        return responseJson;\n    }",
                        "url": "jdbc:mysql://127.0.0.1:3306/bugzilla"
                    },
                    "name": "bugzill",
                    "classifier": "efwd",
                    "type": "sql.jdbc.groovy",
                    "dataSourceType": "Plain Groovy DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Mysql",
                    "keyPath": "81ub-0qiz-3yn2-kczr-wo/pl6i-loyr-ej1k-9krf-5w",
                    "key": "pl6i-loyr-ej1k-9krf-5w",
                    "uuid": "pl6i-loyr-ej1k-9krf-5w"
                }
            ],
            "key": "81ub-0qiz-3yn2-kczr-wo",
            "uuid": "81ub-0qiz-3yn2-kczr-wo",
            "keyPath": "81ub-0qiz-3yn2-kczr-wo",
            "category": "dsGroup",
            "imgUrl": "images/data_sources/defaut_datasource.png"
        },
        {
            "name": "Postgresql",
            "children": [
                {
                    "permissionLevel": 5,
                    "driver": "org.postgresql.Driver",
                    "data": {
                        "dir": "Gagan",
                        "driver": "org.postgresql.Driver",
                        "type": "sql.jdbc.groovy",
                        "id": 304,
                        "username": "zlvkhftkogyvevhyxvalzgvu@psql-mock-database-cloud",
                        "password": "srnlevoznktjiwoutcsbhgqr",
                        "condition": "import net.sf.json.JSONObject;\n    import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n    public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        responseJson.put(\"driver\", \"com.mysql.jdbc.Driver\");\n        responseJson.put(\"url\", \"jdbc:mysql://localhost:3306/\" + userName);\n        responseJson.put(\"user\", \"root\");\n        responseJson.put(\"pass\", \"root\");\n        return responseJson;\n    }",
                        "url": "jdbc:postgresql://psql-mock-database-cloud.postgres.database.azure.com:5432/booking1662345944710kenxhxumioueuzfj"
                    },
                    "name": "booking",
                    "classifier": "efwd",
                    "type": "sql.jdbc.groovy",
                    "dataSourceType": "Plain Groovy DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Postgresql",
                    "keyPath": "0130-5d8j-u5ki-15bw-o4/42cc-lppy-2n0o-sqan-zo",
                    "key": "42cc-lppy-2n0o-sqan-zo",
                    "uuid": "42cc-lppy-2n0o-sqan-zo"
                }
            ],
            "key": "0130-5d8j-u5ki-15bw-o4",
            "uuid": "0130-5d8j-u5ki-15bw-o4",
            "keyPath": "0130-5d8j-u5ki-15bw-o4",
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
                "dsUUID": "dfvh-azrf-vt31-6szs-i1",
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
    "dataSource": [],
    "tables": {
        "dimdate": {
            "id": "4ac5d9f68b58bd7c0d179146e46795be",
            "name": "dimdate",
            "data": {
                "id": "1",
                "type": "dynamicDataSource"
            },
            "keyPath": "b8pc-8anq-tzkb-wsic-yu/dfvh-azrf-vt31-6szs-i1/fgur-2uhf-t7c2-6glg-w0/4e1x-zz7k-k3b0-c9ug-81",
            "key": "4e1x-zz7k-k3b0-c9ug-81",
            "alias": "dimdate",
            "uuid": "4e1x-zz7k-k3b0-c9ug-81",
            "connId": "4otw5",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "4otw5",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "b8pc-8anq-tzkb-wsic-yu/dfvh-azrf-vt31-6szs-i1/fgur-2uhf-t7c2-6glg-w0",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "dimdate_4otw5",
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
            "keyPath": "b8pc-8anq-tzkb-wsic-yu/dfvh-azrf-vt31-6szs-i1/fgur-2uhf-t7c2-6glg-w0/mcbb-9e3e-4mbn-tnvo-1t",
            "key": "mcbb-9e3e-4mbn-tnvo-1t",
            "alias": "employee_details",
            "uuid": "mcbb-9e3e-4mbn-tnvo-1t",
            "connId": "4otw5",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "4otw5",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "b8pc-8anq-tzkb-wsic-yu/dfvh-azrf-vt31-6szs-i1/fgur-2uhf-t7c2-6glg-w0",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "employee_details_4otw5",
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
            "keyPath": "b8pc-8anq-tzkb-wsic-yu/dfvh-azrf-vt31-6szs-i1/fgur-2uhf-t7c2-6glg-w0/eseh-b3e4-rx4f-wzzg-xk",
            "key": "eseh-b3e4-rx4f-wzzg-xk",
            "alias": "geo_cordinates",
            "uuid": "eseh-b3e4-rx4f-wzzg-xk",
            "connId": "4otw5",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "4otw5",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "b8pc-8anq-tzkb-wsic-yu/dfvh-azrf-vt31-6szs-i1/fgur-2uhf-t7c2-6glg-w0",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "geo_cordinates_4otw5",
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
            "keyPath": "b8pc-8anq-tzkb-wsic-yu/dfvh-azrf-vt31-6szs-i1/fgur-2uhf-t7c2-6glg-w0/frx7-yu73-y6y5-lwm3-86",
            "key": "frx7-yu73-y6y5-lwm3-86",
            "alias": "meeting_details",
            "uuid": "frx7-yu73-y6y5-lwm3-86",
            "connId": "4otw5",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "4otw5",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "b8pc-8anq-tzkb-wsic-yu/dfvh-azrf-vt31-6szs-i1/fgur-2uhf-t7c2-6glg-w0",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "meeting_details_4otw5",
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
            "keyPath": "b8pc-8anq-tzkb-wsic-yu/dfvh-azrf-vt31-6szs-i1/fgur-2uhf-t7c2-6glg-w0/ndv3-nqt5-syr3-zicb-br",
            "key": "ndv3-nqt5-syr3-zicb-br",
            "alias": "travel_details",
            "uuid": "ndv3-nqt5-syr3-zicb-br",
            "connId": "4otw5",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "4otw5",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "b8pc-8anq-tzkb-wsic-yu/dfvh-azrf-vt31-6szs-i1/fgur-2uhf-t7c2-6glg-w0",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "travel_details_4otw5",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "travel_details"
        }
    },
    "views": [],
    "activeView": false,
    "categories": {
        "b8pc-8anq-tzkb-wsic-yu": {
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
        "81ub-0qiz-3yn2-kczr-wo": {
            "ds": {
                "permissionLevel": 5,
                "driver": "com.mysql.jdbc.Driver",
                "data": {
                    "dir": "",
                    "driver": "com.mysql.jdbc.Driver",
                    "type": "sql.jdbc",
                    "id": 101,
                    "username": "bugzilla",
                    "password": "bugzilla@QA",
                    "url": "jdbc:mysql://localhost:3306/SampleTravelData"
                },
                "name": "mysqlplainjdbc",
                "classifier": "efwd",
                "type": "sql.jdbc",
                "dataSourceType": "Plain Jdbc DataSource"
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
        "0130-5d8j-u5ki-15bw-o4": {
            "ds": {
                "permissionLevel": 5,
                "driver": "org.postgresql.Driver",
                "data": {
                    "dir": "Gagan",
                    "driver": "org.postgresql.Driver",
                    "type": "sql.jdbc.groovy",
                    "id": 304,
                    "username": "zlvkhftkogyvevhyxvalzgvu@psql-mock-database-cloud",
                    "password": "srnlevoznktjiwoutcsbhgqr",
                    "condition": "import net.sf.json.JSONObject;\n    import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n    public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        responseJson.put(\"driver\", \"com.mysql.jdbc.Driver\");\n        responseJson.put(\"url\", \"jdbc:mysql://localhost:3306/\" + userName);\n        responseJson.put(\"user\", \"root\");\n        responseJson.put(\"pass\", \"root\");\n        return responseJson;\n    }",
                    "url": "jdbc:postgresql://psql-mock-database-cloud.postgres.database.azure.com:5432/booking1662345944710kenxhxumioueuzfj"
                },
                "name": "booking",
                "classifier": "efwd",
                "type": "sql.jdbc.groovy",
                "dataSourceType": "Plain Groovy DataSource"
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
    "saveDetails": {
        "location": "Gagan",
        "uuid": "sample_derby.metadata"
    },
    "savedTableIds": [
        "4ac5d9f68b58bd7c0d179146e46795be",
        "4e1fd245f4d13b77be423a43f01d80b2",
        "be534112989b616b194bc59c2fb25a42",
        "9645c648a1c0dbeec1287aaf1e996db3",
        "8a28627d07d04ef096d9935f12e0c7e9"
    ],
    "savedColumnIds": [],
    "joins": [
        {
            "id": "ca21d00c8c87263dedd812f8f74c05b5",
            "type": "inner",
            "operator": "=",
            "left": {
                "table": "geo_cordinates",
                "column": "location_id",
                "alias": "geo_cordinates.location_id"
            },
            "right": {
                "table": "dimdate",
                "column": "dim_id",
                "alias": "dimdate.dim_id"
            },
            "index": 1,
            "action": "noChange",
            "uuid": "v62x-rlh4-zcdw-rdhy-2u"
        },
        {
            "id": "af8f3186af3703a70a3d6e219faafb4e",
            "type": "inner",
            "operator": "=",
            "left": {
                "table": "employee_details",
                "column": "employee_id",
                "alias": "employee_details.employee_id"
            },
            "right": {
                "table": "meeting_details",
                "column": "meeting_by",
                "alias": "meeting_details.meeting_by"
            },
            "index": 2,
            "action": "noChange",
            "uuid": "2mi5-08yf-11kf-rdot-uj"
        },
        {
            "id": "aab02b68e2c7febf125c50c8c5175037",
            "type": "inner",
            "operator": "=",
            "left": {
                "table": "employee_details",
                "column": "employee_id",
                "alias": "employee_details.employee_id"
            },
            "right": {
                "table": "travel_details",
                "column": "travelled_by",
                "alias": "travel_details.travelled_by"
            },
            "index": 3,
            "action": "noChange",
            "uuid": "2ijk-rs5v-ut25-gvrr-2z"
        },
        {
            "id": "daa3221b04c18670d4af25ac99f3ae76",
            "type": "inner",
            "operator": "=",
            "left": {
                "table": "geo_cordinates",
                "column": "location_id",
                "alias": "geo_cordinates.location_id"
            },
            "right": {
                "table": "travel_details",
                "column": "destination_id",
                "alias": "travel_details.destination_id"
            },
            "index": 4,
            "action": "noChange",
            "uuid": "7jzn-anbc-38iq-t5h1-od"
        },
        {
            "id": "cdeb5b19799c89335f23ed9b50cc5a22",
            "type": "inner",
            "operator": "=",
            "left": {
                "table": "geo_cordinates",
                "column": "location_id",
                "alias": "geo_cordinates.location_id"
            },
            "right": {
                "table": "travel_details",
                "column": "source_id",
                "alias": "travel_details.source_id"
            },
            "index": 5,
            "action": "noChange",
            "uuid": "zrcn-n0p2-takn-z590-zq"
        }
    ],
    "mode": "edit",
    "allTablesKeys": [],
    "selectedTableKeys": [
        "4e1x-zz7k-k3b0-c9ug-81",
        "mcbb-9e3e-4mbn-tnvo-1t",
        "eseh-b3e4-rx4f-wzzg-xk",
        "frx7-yu73-y6y5-lwm3-86",
        "ndv3-nqt5-syr3-zicb-br"
    ],
    "metadataName": "sample derby",
    "activeDataSource": false,
    "metadataToEdit": false,
    "isSavingInProgress": false,
    "editViewsTempData": {},
    "inititalStateFromJest": false,
    "timeStamp": 1665144129477,
    "initialEditResponse": {
        "classifier": "db.generic",
        "name": "HIUSER",
        "dataSource": {
            "sync": false,
            "id": "1",
            "catSchemaPredicted": false,
            "catalog": "",
            "schema": "HIUSER",
            "type": "dynamicDataSource",
            "baseType": "global.jdbc"
        },
        "uniqueId": "sample_derby",
        "tables": {
            "dimdate": {
                "id": "4ac5d9f68b58bd7c0d179146e46795be",
                "alias": "dimdate",
                "columns": {
                    "dim_id": {
                        "alias": "dim_id",
                        "fullyQualifiedColumn": "dimdate.dim_id",
                        "columnId": "90b72023-c5b7-40fa-b73b-1fff251e0dea",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "fiscal_year": {
                        "alias": "fiscal_year",
                        "fullyQualifiedColumn": "dimdate.fiscal_year",
                        "columnId": "590c67b3-bfc1-4bf0-8184-7319e68bb7bf",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.sql.Date": "date"
                        }
                    },
                    "modified_date": {
                        "alias": "modified_date",
                        "fullyQualifiedColumn": "dimdate.modified_date",
                        "columnId": "7590975e-ad0c-4056-9a9c-0aaa47dc059a",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.sql.Timestamp": "dateTime"
                        }
                    },
                    "date_key": {
                        "alias": "date_key",
                        "fullyQualifiedColumn": "dimdate.date_key",
                        "columnId": "a01be8d7-dfbc-41e0-b652-c5ffa2c1c798",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "day_number": {
                        "alias": "day_number",
                        "fullyQualifiedColumn": "dimdate.day_number",
                        "columnId": "d90fa697-1639-4115-8f2f-a73568368fa7",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "fiscal_month_name": {
                        "alias": "fiscal_month_name",
                        "fullyQualifiedColumn": "dimdate.fiscal_month_name",
                        "columnId": "0407ae4d-b8c5-43f0-885b-7951d39773d1",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "fiscal_month_label": {
                        "alias": "fiscal_month_label",
                        "fullyQualifiedColumn": "dimdate.fiscal_month_label",
                        "columnId": "c2a6855b-f91b-4b69-b720-73361fce1b5a",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "created_date": {
                        "alias": "created_date",
                        "fullyQualifiedColumn": "dimdate.created_date",
                        "columnId": "aafda9c2-d481-475b-bbad-9752ecedf02b",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "created_time": {
                        "alias": "created_time",
                        "fullyQualifiedColumn": "dimdate.created_time",
                        "columnId": "45f91e9e-7ecb-40f9-b0f8-db540c46a8b9",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "rating": {
                        "alias": "rating",
                        "fullyQualifiedColumn": "dimdate.rating",
                        "columnId": "5aeedfe5-e387-4334-a7d4-3d63594a6b97",
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
                        "columnId": "e65b808e-75b2-4902-85dc-700a8f2b4732",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "employee_name": {
                        "alias": "employee_name",
                        "fullyQualifiedColumn": "employee_details.employee_name",
                        "columnId": "84ed0bb3-6fe2-4ae6-9b0f-3691b3ab0a38",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "age": {
                        "alias": "age",
                        "fullyQualifiedColumn": "employee_details.age",
                        "columnId": "49de5d19-e108-4066-be37-1ccb9b570fbf",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "address": {
                        "alias": "address",
                        "fullyQualifiedColumn": "employee_details.address",
                        "columnId": "5e579880-a1c0-4659-ba9e-6d1a213c3429",
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
                        "columnId": "fa5d56a7-2fb8-4c6f-a00a-3948336c65dd",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "location": {
                        "alias": "location",
                        "fullyQualifiedColumn": "geo_cordinates.location",
                        "columnId": "92097e49-e14f-44da-b055-ae1fe8a61f70",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "latitude": {
                        "alias": "latitude",
                        "fullyQualifiedColumn": "geo_cordinates.latitude",
                        "columnId": "b2a8b3a4-5273-46e5-928a-075c0b7dba47",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Double": "numeric"
                        }
                    },
                    "longitude": {
                        "alias": "longitude",
                        "fullyQualifiedColumn": "geo_cordinates.longitude",
                        "columnId": "a146abad-1294-41e9-ad89-c062caeb1544",
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
                        "columnId": "83e04d85-5f4b-4478-9bc8-bd72c9478773",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "meeting_date": {
                        "alias": "meeting_date",
                        "fullyQualifiedColumn": "meeting_details.meeting_date",
                        "columnId": "baa75243-8b98-4b9b-b5c4-c046df54840a",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.sql.Timestamp": "dateTime"
                        }
                    },
                    "meeting_by": {
                        "alias": "meeting_by",
                        "fullyQualifiedColumn": "meeting_details.meeting_by",
                        "columnId": "1a1fec29-93b5-4c21-8898-69f0ce06df97",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "client_name": {
                        "alias": "client_name",
                        "fullyQualifiedColumn": "meeting_details.client_name",
                        "columnId": "f3fe1715-6da8-4b2d-a6fc-fd755c55cce2",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "meeting_purpose": {
                        "alias": "meeting_purpose",
                        "fullyQualifiedColumn": "meeting_details.meeting_purpose",
                        "columnId": "39a382f1-0c7e-437b-acd9-da94c46314ce",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "meeting_impact": {
                        "alias": "meeting_impact",
                        "fullyQualifiedColumn": "meeting_details.meeting_impact",
                        "columnId": "a26bc83c-57bc-4eb8-a481-37b00d00202e",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "meet_cancellation_status": {
                        "alias": "meet_cancellation_status",
                        "fullyQualifiedColumn": "meeting_details.meet_cancellation_status",
                        "columnId": "949ccee7-83a4-4ad5-af52-8d4752e57798",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "cancellation_reason": {
                        "alias": "cancellation_reason",
                        "fullyQualifiedColumn": "meeting_details.cancellation_reason",
                        "columnId": "3b86ccba-6be9-43a4-9816-e42773694d4e",
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
                        "columnId": "67d61941-88ce-4db1-8079-21d8184d79f1",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "travel_date": {
                        "alias": "travel_date",
                        "fullyQualifiedColumn": "travel_details.travel_date",
                        "columnId": "26a18d05-9a3f-432b-985c-0b12da86231d",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.sql.Timestamp": "dateTime"
                        }
                    },
                    "travel_type": {
                        "alias": "travel_type",
                        "fullyQualifiedColumn": "travel_details.travel_type",
                        "columnId": "b6ae3075-8bcf-47dc-9b9d-52a9820719c3",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "travel_medium": {
                        "alias": "travel_medium",
                        "fullyQualifiedColumn": "travel_details.travel_medium",
                        "columnId": "96290b52-e635-4fd5-ac80-6af00b37a250",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "source_id": {
                        "alias": "source_id",
                        "fullyQualifiedColumn": "travel_details.source_id",
                        "columnId": "dd3c4fac-9ae7-4203-bdf3-7ea42b8676a6",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "source": {
                        "alias": "source",
                        "fullyQualifiedColumn": "travel_details.source",
                        "columnId": "8daa3fb7-4be3-4d80-96ff-f3de6688c135",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "destination_id": {
                        "alias": "destination_id",
                        "fullyQualifiedColumn": "travel_details.destination_id",
                        "columnId": "fb2ce658-56a9-4f5e-b52d-556b1058afc5",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "destination": {
                        "alias": "destination",
                        "fullyQualifiedColumn": "travel_details.destination",
                        "columnId": "f7f143ed-eaca-403d-bf99-5e5f926fe742",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "travel_cost": {
                        "alias": "travel_cost",
                        "fullyQualifiedColumn": "travel_details.travel_cost",
                        "columnId": "34afdaf9-667a-4247-9a94-f84225b9f573",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "mode_of_payment": {
                        "alias": "mode_of_payment",
                        "fullyQualifiedColumn": "travel_details.mode_of_payment",
                        "columnId": "2d0ca827-372e-4349-aac4-14c53ae11e1b",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "booking_platform": {
                        "alias": "booking_platform",
                        "fullyQualifiedColumn": "travel_details.booking_platform",
                        "columnId": "95859ecc-a29c-43a0-b5da-4c3e63299cbb",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "travelled_by": {
                        "alias": "travelled_by",
                        "fullyQualifiedColumn": "travel_details.travelled_by",
                        "columnId": "24c1120e-92f9-417e-ac52-acd11c684330",
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
                "id": "ca21d00c8c87263dedd812f8f74c05b5",
                "type": "inner",
                "operator": "=",
                "left": {
                    "table": "geo_cordinates",
                    "column": "location_id",
                    "alias": "geo_cordinates.location_id"
                },
                "right": {
                    "table": "dimdate",
                    "column": "dim_id",
                    "alias": "dimdate.dim_id"
                }
            },
            {
                "id": "af8f3186af3703a70a3d6e219faafb4e",
                "type": "inner",
                "operator": "=",
                "left": {
                    "table": "employee_details",
                    "column": "employee_id",
                    "alias": "employee_details.employee_id"
                },
                "right": {
                    "table": "meeting_details",
                    "column": "meeting_by",
                    "alias": "meeting_details.meeting_by"
                }
            },
            {
                "id": "aab02b68e2c7febf125c50c8c5175037",
                "type": "inner",
                "operator": "=",
                "left": {
                    "table": "employee_details",
                    "column": "employee_id",
                    "alias": "employee_details.employee_id"
                },
                "right": {
                    "table": "travel_details",
                    "column": "travelled_by",
                    "alias": "travel_details.travelled_by"
                }
            },
            {
                "id": "daa3221b04c18670d4af25ac99f3ae76",
                "type": "inner",
                "operator": "=",
                "left": {
                    "table": "geo_cordinates",
                    "column": "location_id",
                    "alias": "geo_cordinates.location_id"
                },
                "right": {
                    "table": "travel_details",
                    "column": "destination_id",
                    "alias": "travel_details.destination_id"
                }
            },
            {
                "id": "cdeb5b19799c89335f23ed9b50cc5a22",
                "type": "inner",
                "operator": "=",
                "left": {
                    "table": "geo_cordinates",
                    "column": "location_id",
                    "alias": "geo_cordinates.location_id"
                },
                "right": {
                    "table": "travel_details",
                    "column": "source_id",
                    "alias": "travel_details.source_id"
                }
            }
        ],
        "metadataName": "sample derby",
        "metadataDir": "Gagan"
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
    "editMdFromHomeInfo": {
        "dir": "Gagan/sample_derby.metadata",
        "file": "sample_derby.metadata"
    }
}

export const actionPayload = {
    type: 'UPDATE_METADATA_STATE',
    payload: {
        others: [
            {
                key: 'dataSourcesAddedToMetadata',
                value: [
                    {
                        id: '1',
                        type: 'dynamicDataSource',
                        baseType: 'global.jdbc',
                        catSchemaPredicted: false,
                        sync: false,
                        catalog: '',
                        schema: 'HIUSER',
                        connId: '4otw5',
                        classifier: 'db.workflow',
                        datasourceName: 'SampleTravelDataDerby',
                        dsKeyPath: 'b8pc-8anq-tzkb-wsic-yu/dfvh-azrf-vt31-6szs-i1/fgur-2uhf-t7c2-6glg-w0',
                        driverType: 'Derby',
                        database: 'HIUSER'
                    }
                ]
            }
        ]
    }
}