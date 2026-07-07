export const store4646 = {
    "dataFetchedFor": {
        "getDatasource": true,
        "joins": true,
        "listDataSources": true,
        "69d2-njth-vikz-nvsj-j5": true,
        "muh6-kby7-whci-8jtb-rg": false,
        "1n2s-mytx-bsrr-b335-dg": false
    },
    "loadingStatus": {
        "getDatasource": true,
        "listDataSources": true,
        "69d2-njth-vikz-nvsj-j5": true,
        "muh6-kby7-whci-8jtb-rg": false,
        "1n2s-mytx-bsrr-b335-dg": false
    },
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
            "categoryName": "No SQL & Big Data",
            "categoryType": "nosql_bigdata",
            "classifier": "global",
            "dataSourceProvider": "noSql",
            "driver": "com.helicalinsight.nosql.mongo",
            "name": "Mongodb",
            "parameters": {
                "collection": "collection",
                "database": "database",
                "hostName": "localhost",
                "port": "27017",
                "sslPort": "3345"
            },
            "type": "global.jdbc",
            "url": "mongodb://{{hostName}}:{{port}}/{{database}}"
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
            "driver": "oadd.org.apache.commons.dbcp2.PoolingDriver",
            "databaseDialect": "",
            "name": "Oadd Commons Dbcp2",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png"
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
            "categoryName": "No SQL & Big Data",
            "categoryType": "nosql_bigdata",
            "classifier": "global",
            "dataSourceProvider": "noSql",
            "driver": "com.helicalinsight.nosql.mongo",
            "name": "Mongodb",
            "parameters": {
                "collection": "collection",
                "database": "database",
                "hostName": "localhost",
                "port": "27017",
                "sslPort": "3345"
            },
            "type": "global.jdbc",
            "url": "mongodb://{{hostName}}:{{port}}/{{database}}"
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
            "driver": "oadd.org.apache.commons.dbcp2.PoolingDriver",
            "databaseDialect": "",
            "name": "Oadd Commons Dbcp2",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
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
            "available": "true",
            "driver": "oadd.org.apache.commons.dbcp2.PoolingDriver"
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
                            "keyPath": "69d2-njth-vikz-nvsj-j5/muh6-kby7-whci-8jtb-rg/k654-whaw-aqu6-2yku-a6",
                            "key": "k654-whaw-aqu6-2yku-a6",
                            "uuid": "k654-whaw-aqu6-2yku-a6",
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
                            "keyPath": "69d2-njth-vikz-nvsj-j5/muh6-kby7-whci-8jtb-rg/fmsw-egl7-4gsu-y7z1-5m",
                            "key": "fmsw-egl7-4gsu-y7z1-5m",
                            "uuid": "fmsw-egl7-4gsu-y7z1-5m",
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
                            "keyPath": "69d2-njth-vikz-nvsj-j5/muh6-kby7-whci-8jtb-rg/mrbc-vsqx-sb7f-se2l-07",
                            "key": "mrbc-vsqx-sb7f-se2l-07",
                            "uuid": "mrbc-vsqx-sb7f-se2l-07",
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
                                    "id": "be534112989b616b194bc59c2fb25a42",
                                    "name": "geo_cordinates",
                                    "data": {
                                        "id": "1",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "69d2-njth-vikz-nvsj-j5/muh6-kby7-whci-8jtb-rg/1n2s-mytx-bsrr-b335-dg/85k6-boee-4b0e-w64k-bv",
                                    "key": "85k6-boee-4b0e-w64k-bv",
                                    "alias": "geo_cordinates",
                                    "uuid": "85k6-boee-4b0e-w64k-bv",
                                    "connId": "ashuj",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "ashuj",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "69d2-njth-vikz-nvsj-j5/muh6-kby7-whci-8jtb-rg/1n2s-mytx-bsrr-b335-dg",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "geo_cordinates_ashuj",
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
                                    "keyPath": "69d2-njth-vikz-nvsj-j5/muh6-kby7-whci-8jtb-rg/1n2s-mytx-bsrr-b335-dg/7emy-rgo6-rhtn-2wlb-dm",
                                    "key": "7emy-rgo6-rhtn-2wlb-dm",
                                    "alias": "meeting_details",
                                    "uuid": "7emy-rgo6-rhtn-2wlb-dm",
                                    "connId": "ashuj",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "ashuj",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "69d2-njth-vikz-nvsj-j5/muh6-kby7-whci-8jtb-rg/1n2s-mytx-bsrr-b335-dg",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "meeting_details_ashuj",
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
                                    "keyPath": "69d2-njth-vikz-nvsj-j5/muh6-kby7-whci-8jtb-rg/1n2s-mytx-bsrr-b335-dg/115v-mhnw-nkbj-rzbr-ri",
                                    "key": "115v-mhnw-nkbj-rzbr-ri",
                                    "alias": "employee_details",
                                    "uuid": "115v-mhnw-nkbj-rzbr-ri",
                                    "connId": "ashuj",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "ashuj",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "69d2-njth-vikz-nvsj-j5/muh6-kby7-whci-8jtb-rg/1n2s-mytx-bsrr-b335-dg",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "employee_details_ashuj",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "4ac5d9f68b58bd7c0d179146e46795be",
                                    "name": "dimdate",
                                    "data": {
                                        "id": "1",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "69d2-njth-vikz-nvsj-j5/muh6-kby7-whci-8jtb-rg/1n2s-mytx-bsrr-b335-dg/446e-q6nn-9zwi-zrtk-eu",
                                    "key": "446e-q6nn-9zwi-zrtk-eu",
                                    "alias": "dimdate",
                                    "uuid": "446e-q6nn-9zwi-zrtk-eu",
                                    "connId": "ashuj",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "ashuj",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "69d2-njth-vikz-nvsj-j5/muh6-kby7-whci-8jtb-rg/1n2s-mytx-bsrr-b335-dg",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "dimdate_ashuj",
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
                                    "keyPath": "69d2-njth-vikz-nvsj-j5/muh6-kby7-whci-8jtb-rg/1n2s-mytx-bsrr-b335-dg/42dk-qgaq-nupe-2t9v-n5",
                                    "key": "42dk-qgaq-nupe-2t9v-n5",
                                    "alias": "travel_details",
                                    "uuid": "42dk-qgaq-nupe-2t9v-n5",
                                    "connId": "ashuj",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "ashuj",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "69d2-njth-vikz-nvsj-j5/muh6-kby7-whci-8jtb-rg/1n2s-mytx-bsrr-b335-dg",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "travel_details_ashuj",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                }
                            ],
                            "keyPath": "69d2-njth-vikz-nvsj-j5/muh6-kby7-whci-8jtb-rg/1n2s-mytx-bsrr-b335-dg",
                            "key": "1n2s-mytx-bsrr-b335-dg",
                            "uuid": "1n2s-mytx-bsrr-b335-dg",
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
                            "keyPath": "69d2-njth-vikz-nvsj-j5/muh6-kby7-whci-8jtb-rg/3fxu-kyn1-kfkw-t32u-wc",
                            "key": "3fxu-kyn1-kfkw-t32u-wc",
                            "uuid": "3fxu-kyn1-kfkw-t32u-wc",
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
                            "keyPath": "69d2-njth-vikz-nvsj-j5/muh6-kby7-whci-8jtb-rg/zk2n-aeks-xxtp-u41j-si",
                            "key": "zk2n-aeks-xxtp-u41j-si",
                            "uuid": "zk2n-aeks-xxtp-u41j-si",
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
                            "keyPath": "69d2-njth-vikz-nvsj-j5/muh6-kby7-whci-8jtb-rg/ykum-p3gb-m6oe-xaz4-al",
                            "key": "ykum-p3gb-m6oe-xaz4-al",
                            "uuid": "ykum-p3gb-m6oe-xaz4-al",
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
                            "keyPath": "69d2-njth-vikz-nvsj-j5/muh6-kby7-whci-8jtb-rg/2xt4-1oft-8ij9-c2x7-1h",
                            "key": "2xt4-1oft-8ij9-c2x7-1h",
                            "uuid": "2xt4-1oft-8ij9-c2x7-1h",
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
                            "keyPath": "69d2-njth-vikz-nvsj-j5/muh6-kby7-whci-8jtb-rg/ljqp-oz92-22xr-23pn-wr",
                            "key": "ljqp-oz92-22xr-23pn-wr",
                            "uuid": "ljqp-oz92-22xr-23pn-wr",
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
                            "keyPath": "69d2-njth-vikz-nvsj-j5/muh6-kby7-whci-8jtb-rg/u6md-4uul-h16j-sxjo-3j",
                            "key": "u6md-4uul-h16j-sxjo-3j",
                            "uuid": "u6md-4uul-h16j-sxjo-3j",
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
                            "keyPath": "69d2-njth-vikz-nvsj-j5/muh6-kby7-whci-8jtb-rg/tmag-u9ro-hdqn-peub-sl",
                            "key": "tmag-u9ro-hdqn-peub-sl",
                            "uuid": "tmag-u9ro-hdqn-peub-sl",
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
                            "keyPath": "69d2-njth-vikz-nvsj-j5/muh6-kby7-whci-8jtb-rg/1w7i-l5b7-eypi-ubl6-s2",
                            "key": "1w7i-l5b7-eypi-ubl6-s2",
                            "uuid": "1w7i-l5b7-eypi-ubl6-s2",
                            "data": {
                                "id": "1",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "SampleTravelDataDerby"
                        }
                    ],
                    "driverType": "Derby",
                    "keyPath": "69d2-njth-vikz-nvsj-j5/muh6-kby7-whci-8jtb-rg",
                    "key": "muh6-kby7-whci-8jtb-rg",
                    "uuid": "muh6-kby7-whci-8jtb-rg",
                    "fetched": true
                }
            ],
            "key": "69d2-njth-vikz-nvsj-j5",
            "uuid": "69d2-njth-vikz-nvsj-j5",
            "keyPath": "69d2-njth-vikz-nvsj-j5",
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
                "dsUUID": "muh6-kby7-whci-8jtb-rg",
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
                        "connId": "ashuj",
                        "classifier": "db.workflow",
                        "datasourceName": "SampleTravelDataDerby",
                        "dsKeyPath": "69d2-njth-vikz-nvsj-j5/muh6-kby7-whci-8jtb-rg/1n2s-mytx-bsrr-b335-dg",
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
            "connId": "ashuj",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelDataDerby",
            "dsKeyPath": "69d2-njth-vikz-nvsj-j5/muh6-kby7-whci-8jtb-rg/1n2s-mytx-bsrr-b335-dg",
            "driverType": "Derby",
            "database": "HIUSER"
        }
    ],
    "tables": {
        "geo_cordinates": {
            "id": "be534112989b616b194bc59c2fb25a42",
            "name": "geo_cordinates",
            "data": {
                "id": "1",
                "type": "dynamicDataSource"
            },
            "keyPath": "69d2-njth-vikz-nvsj-j5/muh6-kby7-whci-8jtb-rg/1n2s-mytx-bsrr-b335-dg/85k6-boee-4b0e-w64k-bv",
            "key": "85k6-boee-4b0e-w64k-bv",
            "alias": "geo_cordinates",
            "uuid": "85k6-boee-4b0e-w64k-bv",
            "connId": "ashuj",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "ashuj",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "69d2-njth-vikz-nvsj-j5/muh6-kby7-whci-8jtb-rg/1n2s-mytx-bsrr-b335-dg",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "geo_cordinates_ashuj",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "geo_cordinates",
            "columns": {
                "location_id": {
                    "alias": "location_id",
                    "fullyQualifiedColumn": "geo_cordinates.location_id",
                    "columnId": "92965c24-c0bd-4958-ae44-182ef0442b96",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    }
                },
                "location": {
                    "alias": "location",
                    "fullyQualifiedColumn": "geo_cordinates.location",
                    "columnId": "305851c1-6642-43e9-bfc9-b3bbfac14f9c",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "latitude": {
                    "alias": "latitude",
                    "fullyQualifiedColumn": "geo_cordinates.latitude",
                    "columnId": "97545df7-36d5-4b64-b1d8-e9ce12e18697",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Double": "numeric"
                    }
                },
                "longitude": {
                    "alias": "longitude",
                    "fullyQualifiedColumn": "geo_cordinates.longitude",
                    "columnId": "1c4ae245-3157-4c90-a92e-ff6d99ef6013",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Double": "numeric"
                    }
                }
            }
        },
        "meeting_details": {
            "id": "9645c648a1c0dbeec1287aaf1e996db3",
            "name": "meeting_details",
            "data": {
                "id": "1",
                "type": "dynamicDataSource"
            },
            "keyPath": "69d2-njth-vikz-nvsj-j5/muh6-kby7-whci-8jtb-rg/1n2s-mytx-bsrr-b335-dg/7emy-rgo6-rhtn-2wlb-dm",
            "key": "7emy-rgo6-rhtn-2wlb-dm",
            "alias": "meeting_details",
            "uuid": "7emy-rgo6-rhtn-2wlb-dm",
            "connId": "ashuj",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "ashuj",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "69d2-njth-vikz-nvsj-j5/muh6-kby7-whci-8jtb-rg/1n2s-mytx-bsrr-b335-dg",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "meeting_details_ashuj",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "meeting_details",
            "columns": {
                "meeting_id": {
                    "alias": "meeting_id",
                    "fullyQualifiedColumn": "meeting_details.meeting_id",
                    "columnId": "4c1fbd71-9ecf-44fa-9007-4d0d2c372245",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    }
                },
                "meeting_date": {
                    "alias": "meeting_date",
                    "fullyQualifiedColumn": "meeting_details.meeting_date",
                    "columnId": "70b787b9-6e16-49d4-baf2-5037c020b920",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.sql.Timestamp": "dateTime"
                    }
                },
                "meeting_by": {
                    "alias": "meeting_by",
                    "fullyQualifiedColumn": "meeting_details.meeting_by",
                    "columnId": "7303cfba-1a6c-423f-a0cb-ca165565d389",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    }
                },
                "client_name": {
                    "alias": "client_name",
                    "fullyQualifiedColumn": "meeting_details.client_name",
                    "columnId": "520aa051-6d60-4b9e-8815-a7192bdb1275",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "meeting_purpose": {
                    "alias": "meeting_purpose",
                    "fullyQualifiedColumn": "meeting_details.meeting_purpose",
                    "columnId": "4e6a3580-6ea5-4b91-9319-db6d6a8bb2b1",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "meeting_impact": {
                    "alias": "meeting_impact",
                    "fullyQualifiedColumn": "meeting_details.meeting_impact",
                    "columnId": "846fba89-0dd2-486b-82cf-5e310db52c0c",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "meet_cancellation_status": {
                    "alias": "meet_cancellation_status",
                    "fullyQualifiedColumn": "meeting_details.meet_cancellation_status",
                    "columnId": "de1a5462-0069-4580-8e0e-546f4586701c",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "cancellation_reason": {
                    "alias": "cancellation_reason",
                    "fullyQualifiedColumn": "meeting_details.cancellation_reason",
                    "columnId": "b005335d-b217-47ac-911a-c205932c0f50",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                }
            }
        },
        "employee_details": {
            "id": "4e1fd245f4d13b77be423a43f01d80b2",
            "name": "employee_details",
            "data": {
                "id": "1",
                "type": "dynamicDataSource"
            },
            "keyPath": "69d2-njth-vikz-nvsj-j5/muh6-kby7-whci-8jtb-rg/1n2s-mytx-bsrr-b335-dg/115v-mhnw-nkbj-rzbr-ri",
            "key": "115v-mhnw-nkbj-rzbr-ri",
            "alias": "employee_details",
            "uuid": "115v-mhnw-nkbj-rzbr-ri",
            "connId": "ashuj",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "ashuj",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "69d2-njth-vikz-nvsj-j5/muh6-kby7-whci-8jtb-rg/1n2s-mytx-bsrr-b335-dg",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "employee_details_ashuj",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "employee_details",
            "columns": {
                "employee_id": {
                    "alias": "employee_id",
                    "fullyQualifiedColumn": "employee_details.employee_id",
                    "columnId": "3e50169b-84da-4d8b-9f38-fced74745be5",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    }
                },
                "employee_name": {
                    "alias": "employee_name",
                    "fullyQualifiedColumn": "employee_details.employee_name",
                    "columnId": "2600887b-d5c9-48e0-b4f4-d1f7b7087e01",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "age": {
                    "alias": "age",
                    "fullyQualifiedColumn": "employee_details.age",
                    "columnId": "ccb63630-5063-4737-8a64-3e947bc3fa5e",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    }
                },
                "address": {
                    "alias": "address",
                    "fullyQualifiedColumn": "employee_details.address",
                    "columnId": "2ae69d1b-b827-47e7-b61f-c18ee5cf8c43",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                }
            }
        },
        "dimdate": {
            "id": "4ac5d9f68b58bd7c0d179146e46795be",
            "name": "dimdate",
            "data": {
                "id": "1",
                "type": "dynamicDataSource"
            },
            "keyPath": "69d2-njth-vikz-nvsj-j5/muh6-kby7-whci-8jtb-rg/1n2s-mytx-bsrr-b335-dg/446e-q6nn-9zwi-zrtk-eu",
            "key": "446e-q6nn-9zwi-zrtk-eu",
            "alias": "dimdate",
            "uuid": "446e-q6nn-9zwi-zrtk-eu",
            "connId": "ashuj",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "ashuj",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "69d2-njth-vikz-nvsj-j5/muh6-kby7-whci-8jtb-rg/1n2s-mytx-bsrr-b335-dg",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "dimdate_ashuj",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "dimdate",
            "columns": {
                "dim_id": {
                    "alias": "dim_id",
                    "fullyQualifiedColumn": "dimdate.dim_id",
                    "columnId": "73d70f4f-9afd-4349-9b96-178af3f5cde8",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    }
                },
                "fiscal_year": {
                    "alias": "fiscal_year",
                    "fullyQualifiedColumn": "dimdate.fiscal_year",
                    "columnId": "702cfa41-d087-41dc-8f59-3b09ff9dd541",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.sql.Date": "date"
                    }
                },
                "modified_date": {
                    "alias": "modified_date",
                    "fullyQualifiedColumn": "dimdate.modified_date",
                    "columnId": "e97f8a7c-6c9e-4805-85cf-6263cf2ac90e",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.sql.Timestamp": "dateTime"
                    }
                },
                "date_key": {
                    "alias": "date_key",
                    "fullyQualifiedColumn": "dimdate.date_key",
                    "columnId": "1cb64df5-803a-4392-954f-c539cabe65b3",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "day_number": {
                    "alias": "day_number",
                    "fullyQualifiedColumn": "dimdate.day_number",
                    "columnId": "8319bfc1-51ec-43c2-bbb1-2fb0a2c3e2c7",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "fiscal_month_name": {
                    "alias": "fiscal_month_name",
                    "fullyQualifiedColumn": "dimdate.fiscal_month_name",
                    "columnId": "bc0f8da2-6fa7-4c63-8570-a37036708f44",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "fiscal_month_label": {
                    "alias": "fiscal_month_label",
                    "fullyQualifiedColumn": "dimdate.fiscal_month_label",
                    "columnId": "37c660d6-4723-423e-9af9-6d86545fa8cb",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "created_date": {
                    "alias": "created_date",
                    "fullyQualifiedColumn": "dimdate.created_date",
                    "columnId": "9f68edee-3af9-4cd1-89b8-2c07bd469575",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "created_time": {
                    "alias": "created_time",
                    "fullyQualifiedColumn": "dimdate.created_time",
                    "columnId": "176165dd-443d-4d66-aeae-e68d0e8a7f00",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "rating": {
                    "alias": "rating",
                    "fullyQualifiedColumn": "dimdate.rating",
                    "columnId": "0eaf3b59-b54c-402e-a0ef-2db90b1600c2",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                }
            }
        },
        "travel_details": {
            "id": "8a28627d07d04ef096d9935f12e0c7e9",
            "name": "travel_details",
            "data": {
                "id": "1",
                "type": "dynamicDataSource"
            },
            "keyPath": "69d2-njth-vikz-nvsj-j5/muh6-kby7-whci-8jtb-rg/1n2s-mytx-bsrr-b335-dg/42dk-qgaq-nupe-2t9v-n5",
            "key": "42dk-qgaq-nupe-2t9v-n5",
            "alias": "travel_details",
            "uuid": "42dk-qgaq-nupe-2t9v-n5",
            "connId": "ashuj",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "ashuj",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "69d2-njth-vikz-nvsj-j5/muh6-kby7-whci-8jtb-rg/1n2s-mytx-bsrr-b335-dg",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "travel_details_ashuj",
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
                    "originalName": "travel_id",
                    "originalId": "199190ce-6414-4181-bd9a-95bd461c1254",
                    "tableKey": "travel_details",
                    "connId": "ashuj",
                    "duplicateIndex": 0,
                    "columnKey": "travel_id",
                    "columnId": "199190ce-6414-4181-bd9a-95bd461c1254"
                },
                "travel_date": {
                    "alias": "travel_date",
                    "fullyQualifiedColumn": "travel_details.travel_date",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.sql.Timestamp": "dateTime"
                    },
                    "category": "column",
                    "originalName": "travel_date",
                    "originalId": "cd95cc4e-9fbf-441b-9db4-213ba2695f0c",
                    "tableKey": "travel_details",
                    "connId": "ashuj",
                    "duplicateIndex": 0,
                    "columnKey": "travel_date",
                    "columnId": "cd95cc4e-9fbf-441b-9db4-213ba2695f0c"
                },
                "travel_type": {
                    "alias": "travel_type",
                    "fullyQualifiedColumn": "travel_details.travel_type",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "originalName": "travel_type",
                    "originalId": "56f42b66-3997-4f69-955e-76a5ffd6d9c1",
                    "tableKey": "travel_details",
                    "connId": "ashuj",
                    "duplicateIndex": 0,
                    "columnKey": "travel_type",
                    "columnId": "56f42b66-3997-4f69-955e-76a5ffd6d9c1"
                },
                "travel_medium": {
                    "alias": "travel_medium",
                    "fullyQualifiedColumn": "travel_details.travel_medium",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "originalName": "travel_medium",
                    "originalId": "c9ba38b1-e817-4797-8c05-d7663edfd2f1",
                    "tableKey": "travel_details",
                    "connId": "ashuj",
                    "duplicateIndex": 0,
                    "columnKey": "travel_medium",
                    "columnId": "c9ba38b1-e817-4797-8c05-d7663edfd2f1"
                },
                "source_id": {
                    "alias": "source_id",
                    "fullyQualifiedColumn": "travel_details.source_id",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    },
                    "category": "column",
                    "originalName": "source_id",
                    "originalId": "3630c4e9-90b5-4397-940c-baaa27516699",
                    "tableKey": "travel_details",
                    "connId": "ashuj",
                    "duplicateIndex": 0,
                    "columnKey": "source_id",
                    "columnId": "3630c4e9-90b5-4397-940c-baaa27516699"
                },
                "source": {
                    "alias": "source",
                    "fullyQualifiedColumn": "travel_details.source",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "originalName": "source",
                    "originalId": "62839e07-c289-4481-858f-a71daa0fb6d7",
                    "tableKey": "travel_details",
                    "connId": "ashuj",
                    "duplicateIndex": 0,
                    "columnKey": "source",
                    "columnId": "62839e07-c289-4481-858f-a71daa0fb6d7"
                },
                "destination_id": {
                    "alias": "destination_id",
                    "fullyQualifiedColumn": "travel_details.destination_id",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    },
                    "category": "column",
                    "originalName": "destination_id",
                    "originalId": "917a5cf2-9ab3-497a-9725-1f406215084e",
                    "tableKey": "travel_details",
                    "connId": "ashuj",
                    "duplicateIndex": 0,
                    "columnKey": "destination_id",
                    "columnId": "917a5cf2-9ab3-497a-9725-1f406215084e"
                },
                "destination": {
                    "alias": "destination",
                    "fullyQualifiedColumn": "travel_details.destination",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "originalName": "destination",
                    "originalId": "63a3ab46-92c3-4cb0-b280-0977f8a420e0",
                    "tableKey": "travel_details",
                    "connId": "ashuj",
                    "duplicateIndex": 0,
                    "columnKey": "destination",
                    "columnId": "63a3ab46-92c3-4cb0-b280-0977f8a420e0"
                },
                "travel_cost": {
                    "alias": "travel_cost",
                    "fullyQualifiedColumn": "travel_details.travel_cost",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    },
                    "category": "column",
                    "originalName": "travel_cost",
                    "originalId": "4e55f3d9-1485-4380-aa47-728a501361a5",
                    "tableKey": "travel_details",
                    "connId": "ashuj",
                    "duplicateIndex": 0,
                    "columnKey": "travel_cost",
                    "columnId": "4e55f3d9-1485-4380-aa47-728a501361a5"
                },
                "mode_of_payment": {
                    "alias": "mode_of_payment",
                    "fullyQualifiedColumn": "travel_details.mode_of_payment",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "originalName": "mode_of_payment",
                    "originalId": "cab49001-1b6e-4051-ba73-a1936c389905",
                    "tableKey": "travel_details",
                    "connId": "ashuj",
                    "duplicateIndex": 0,
                    "columnKey": "mode_of_payment",
                    "columnId": "cab49001-1b6e-4051-ba73-a1936c389905"
                },
                "booking_platform": {
                    "alias": "booking_platform",
                    "fullyQualifiedColumn": "travel_details.booking_platform",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "originalName": "booking_platform",
                    "originalId": "b12b3fe9-cb43-4e7e-af0d-a4a8fc817f8e",
                    "tableKey": "travel_details",
                    "connId": "ashuj",
                    "duplicateIndex": 0,
                    "columnKey": "booking_platform",
                    "columnId": "b12b3fe9-cb43-4e7e-af0d-a4a8fc817f8e"
                },
                "travelled_by": {
                    "alias": "travelled_by",
                    "fullyQualifiedColumn": "travel_details.travelled_by",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    },
                    "category": "column",
                    "originalName": "travelled_by",
                    "originalId": "a5239d12-57f3-4e2f-8d00-95cb0c4b6802",
                    "tableKey": "travel_details",
                    "connId": "ashuj",
                    "duplicateIndex": 0,
                    "columnKey": "travelled_by",
                    "columnId": "a5239d12-57f3-4e2f-8d00-95cb0c4b6802"
                }
            },
            "columnsFetched": true
        }
    },
    "categories": {
        "69d2-njth-vikz-nvsj-j5": {
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
            "connId": "ashuj",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelDataDerby",
            "dsKeyPath": "69d2-njth-vikz-nvsj-j5/muh6-kby7-whci-8jtb-rg/1n2s-mytx-bsrr-b335-dg",
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
    "saveDetails": {
        "location": "Gagan",
        "uuid": "Metadata_1.metadata"
    },
    "savedTableIds": [
        "be534112989b616b194bc59c2fb25a42",
        "9645c648a1c0dbeec1287aaf1e996db3",
        "4e1fd245f4d13b77be423a43f01d80b2",
        "4ac5d9f68b58bd7c0d179146e46795be",
        "8a28627d07d04ef096d9935f12e0c7e9"
    ],
    "savedColumnIds": [
        {
            "columnId": "92965c24-c0bd-4958-ae44-182ef0442b96",
            "tableId": "be534112989b616b194bc59c2fb25a42"
        },
        {
            "columnId": "305851c1-6642-43e9-bfc9-b3bbfac14f9c",
            "tableId": "be534112989b616b194bc59c2fb25a42"
        },
        {
            "columnId": "97545df7-36d5-4b64-b1d8-e9ce12e18697",
            "tableId": "be534112989b616b194bc59c2fb25a42"
        },
        {
            "columnId": "1c4ae245-3157-4c90-a92e-ff6d99ef6013",
            "tableId": "be534112989b616b194bc59c2fb25a42"
        },
        {
            "columnId": "4c1fbd71-9ecf-44fa-9007-4d0d2c372245",
            "tableId": "9645c648a1c0dbeec1287aaf1e996db3"
        },
        {
            "columnId": "70b787b9-6e16-49d4-baf2-5037c020b920",
            "tableId": "9645c648a1c0dbeec1287aaf1e996db3"
        },
        {
            "columnId": "7303cfba-1a6c-423f-a0cb-ca165565d389",
            "tableId": "9645c648a1c0dbeec1287aaf1e996db3"
        },
        {
            "columnId": "520aa051-6d60-4b9e-8815-a7192bdb1275",
            "tableId": "9645c648a1c0dbeec1287aaf1e996db3"
        },
        {
            "columnId": "4e6a3580-6ea5-4b91-9319-db6d6a8bb2b1",
            "tableId": "9645c648a1c0dbeec1287aaf1e996db3"
        },
        {
            "columnId": "846fba89-0dd2-486b-82cf-5e310db52c0c",
            "tableId": "9645c648a1c0dbeec1287aaf1e996db3"
        },
        {
            "columnId": "de1a5462-0069-4580-8e0e-546f4586701c",
            "tableId": "9645c648a1c0dbeec1287aaf1e996db3"
        },
        {
            "columnId": "b005335d-b217-47ac-911a-c205932c0f50",
            "tableId": "9645c648a1c0dbeec1287aaf1e996db3"
        },
        {
            "columnId": "3e50169b-84da-4d8b-9f38-fced74745be5",
            "tableId": "4e1fd245f4d13b77be423a43f01d80b2"
        },
        {
            "columnId": "2600887b-d5c9-48e0-b4f4-d1f7b7087e01",
            "tableId": "4e1fd245f4d13b77be423a43f01d80b2"
        },
        {
            "columnId": "ccb63630-5063-4737-8a64-3e947bc3fa5e",
            "tableId": "4e1fd245f4d13b77be423a43f01d80b2"
        },
        {
            "columnId": "2ae69d1b-b827-47e7-b61f-c18ee5cf8c43",
            "tableId": "4e1fd245f4d13b77be423a43f01d80b2"
        },
        {
            "columnId": "73d70f4f-9afd-4349-9b96-178af3f5cde8",
            "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
        },
        {
            "columnId": "702cfa41-d087-41dc-8f59-3b09ff9dd541",
            "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
        },
        {
            "columnId": "e97f8a7c-6c9e-4805-85cf-6263cf2ac90e",
            "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
        },
        {
            "columnId": "1cb64df5-803a-4392-954f-c539cabe65b3",
            "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
        },
        {
            "columnId": "8319bfc1-51ec-43c2-bbb1-2fb0a2c3e2c7",
            "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
        },
        {
            "columnId": "bc0f8da2-6fa7-4c63-8570-a37036708f44",
            "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
        },
        {
            "columnId": "37c660d6-4723-423e-9af9-6d86545fa8cb",
            "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
        },
        {
            "columnId": "9f68edee-3af9-4cd1-89b8-2c07bd469575",
            "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
        },
        {
            "columnId": "176165dd-443d-4d66-aeae-e68d0e8a7f00",
            "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
        },
        {
            "columnId": "0eaf3b59-b54c-402e-a0ef-2db90b1600c2",
            "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
        },
        {
            "columnId": "199190ce-6414-4181-bd9a-95bd461c1254",
            "tableId": "8a28627d07d04ef096d9935f12e0c7e9"
        },
        {
            "columnId": "cd95cc4e-9fbf-441b-9db4-213ba2695f0c",
            "tableId": "8a28627d07d04ef096d9935f12e0c7e9"
        },
        {
            "columnId": "56f42b66-3997-4f69-955e-76a5ffd6d9c1",
            "tableId": "8a28627d07d04ef096d9935f12e0c7e9"
        },
        {
            "columnId": "c9ba38b1-e817-4797-8c05-d7663edfd2f1",
            "tableId": "8a28627d07d04ef096d9935f12e0c7e9"
        },
        {
            "columnId": "3630c4e9-90b5-4397-940c-baaa27516699",
            "tableId": "8a28627d07d04ef096d9935f12e0c7e9"
        },
        {
            "columnId": "62839e07-c289-4481-858f-a71daa0fb6d7",
            "tableId": "8a28627d07d04ef096d9935f12e0c7e9"
        },
        {
            "columnId": "917a5cf2-9ab3-497a-9725-1f406215084e",
            "tableId": "8a28627d07d04ef096d9935f12e0c7e9"
        },
        {
            "columnId": "63a3ab46-92c3-4cb0-b280-0977f8a420e0",
            "tableId": "8a28627d07d04ef096d9935f12e0c7e9"
        },
        {
            "columnId": "4e55f3d9-1485-4380-aa47-728a501361a5",
            "tableId": "8a28627d07d04ef096d9935f12e0c7e9"
        },
        {
            "columnId": "cab49001-1b6e-4051-ba73-a1936c389905",
            "tableId": "8a28627d07d04ef096d9935f12e0c7e9"
        },
        {
            "columnId": "b12b3fe9-cb43-4e7e-af0d-a4a8fc817f8e",
            "tableId": "8a28627d07d04ef096d9935f12e0c7e9"
        },
        {
            "columnId": "a5239d12-57f3-4e2f-8d00-95cb0c4b6802",
            "tableId": "8a28627d07d04ef096d9935f12e0c7e9"
        }
    ],
    "joins": [
        {
            "type": "inner",
            "operator": "=",
            "left": {
                "table": "travel_details",
                "column": "travel_cost",
                "alias": false
            },
            "right": {
                "table": "travel_details",
                "column": "booking_platform",
                "alias": false
            },
            "key": "nly0-1i00-8ad2-4tjm-0k",
            "uuid": "nly0-1i00-8ad2-4tjm-0k",
            "action": "delete",
            "leftColumn": "travel_details.travel_cost",
            "rightColumn": "travel_details.booking_platform"
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
            "action": "noChange",
            "uuid": "af8f3186af3703a70a3d6e219faafb4e",
            "key": "af8f3186af3703a70a3d6e219faafb4e",
            "index": 1
        },
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
            "action": "noChange",
            "uuid": "ca21d00c8c87263dedd812f8f74c05b5",
            "key": "ca21d00c8c87263dedd812f8f74c05b5",
            "index": 2
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
            "action": "noChange",
            "uuid": "aab02b68e2c7febf125c50c8c5175037",
            "key": "aab02b68e2c7febf125c50c8c5175037",
            "index": 3
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
            "action": "noChange",
            "uuid": "daa3221b04c18670d4af25ac99f3ae76",
            "key": "daa3221b04c18670d4af25ac99f3ae76",
            "index": 4
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
            "action": "noChange",
            "uuid": "cdeb5b19799c89335f23ed9b50cc5a22",
            "key": "cdeb5b19799c89335f23ed9b50cc5a22",
            "index": 5
        }
    ],
    "mode": "create",
    "allTablesKeys": [
        "85k6-boee-4b0e-w64k-bv",
        "7emy-rgo6-rhtn-2wlb-dm",
        "115v-mhnw-nkbj-rzbr-ri",
        "446e-q6nn-9zwi-zrtk-eu",
        "42dk-qgaq-nupe-2t9v-n5"
    ],
    "selectedTableKeys": [
        "85k6-boee-4b0e-w64k-bv",
        "7emy-rgo6-rhtn-2wlb-dm",
        "115v-mhnw-nkbj-rzbr-ri",
        "446e-q6nn-9zwi-zrtk-eu",
        "42dk-qgaq-nupe-2t9v-n5"
    ],
    "metadataName": "Metadata_1",
    "activeDataSource": false,
    "metadataToEdit": false,
    "isSavingInProgress": false,
    "inititalStateFromJest": false
}