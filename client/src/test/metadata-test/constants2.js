// snapshot for intial state (without adding any tables to metadata)
export const storeSnapshotForSave_5 = {
    "dataFetchedFor": {
        "getDatasource": true,
        "joins": false,
        "listDataSources": true
    },
    "loadingStatus": {
        "getDatasource": true,
        "listDataSources": true
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
            "name": "Amazon Redshift",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Amazondynamodb",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Dremio",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Googlebigquery",
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
            "name": "Mysql",
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
            "name": "Amazon Redshift",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Amazondynamodb",
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
            "name": "Googlebigquery",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "driver": "com.helical.mongodb.MongoJdbcDriver",
            "databaseDialect": "himongo\t\t\t\t\t\t\t\t\t\t   ",
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
            "name": "Mysql",
            "categoryType": "supported",
            "categoryName": "Supported"
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
            "name": "Presto",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "driver": "ru.yandex.clickhouse.ClickHouseDriver",
            "databaseDialect": "clickhouse",
            "name": "Ru Yandex Clickhouse",
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
            "databaseDialect": "himongo\t\t\t\t\t\t\t\t\t\t   ",
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
            "driver": "ru.yandex.clickhouse.ClickHouseDriver",
            "databaseDialect": "clickhouse",
            "name": "Ru Yandex Clickhouse",
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
            "available": "true",
            "driver": "com.helical.mongodb.MongoJdbcDriver"
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
            "name": "SampleEFWD Sample Travel Data",
            "type": "sql.jdbc",
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "dataSourceType": "Plain Jdbc DataSource",
            "classifier": "efwd",
            "permissionLevel": 2,
            "data": {
                "id": "1",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
            }
        },
        {
            "name": "1",
            "dataSourceType": "Managed DataSource",
            "classifier": "global",
            "permissionLevel": 5,
            "data": {
                "id": "1",
                "dir": "1463377807724/1593413909773",
                "type": "global.jdbc"
            }
        },
        {
            "name": "1",
            "dataSourceType": "Managed DataSource",
            "classifier": "global",
            "permissionLevel": 5,
            "data": {
                "id": "1",
                "dir": "1463377807724/1570173811451",
                "type": "global.jdbc"
            }
        },
        {
            "name": "SampleEFWD Sample Travel Data",
            "type": "sql.jdbc",
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "dataSourceType": "Plain Jdbc DataSource",
            "classifier": "efwd",
            "permissionLevel": 2,
            "data": {
                "id": "1",
                "dir": "1463377807724/1463377978248/Sample EFW Dashboard",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
            }
        },
        {
            "name": "SampleTravelDataDerby",
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "dataSourceType": "Managed DataSource",
            "classifier": "global",
            "type": "dynamicDataSource",
            "data": {
                "id": "1",
                "type": "dynamicDataSource"
            },
            "permissionLevel": 5,
            "dataSourceProvider": "tomcat"
        },
        {
            "name": "test_SampleTravelDataDerby",
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "dataSourceType": "Managed DataSource",
            "classifier": "global",
            "type": "dynamicDataSource",
            "data": {
                "id": "2",
                "type": "dynamicDataSource"
            },
            "permissionLevel": 5,
            "dataSourceProvider": "tomcat"
        },
        {
            "name": "chinook",
            "driver": "org.sqlite.JDBC",
            "dataSourceType": "Managed DataSource",
            "classifier": "global",
            "type": "dynamicDataSource",
            "data": {
                "id": "3",
                "type": "dynamicDataSource"
            },
            "permissionLevel": 5,
            "dataSourceProvider": "tomcat"
        },
        {
            "name": "sqlite std",
            "driver": "org.sqlite.JDBC",
            "dataSourceType": "Managed DataSource",
            "classifier": "global",
            "type": "dynamicDataSource",
            "data": {
                "id": "4",
                "type": "dynamicDataSource"
            },
            "permissionLevel": 5,
            "dataSourceProvider": "tomcat"
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
                    "name": "SampleEFWD Sample Travel Data",
                    "type": "sql.jdbc",
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "dataSourceType": "Plain Jdbc DataSource",
                    "classifier": "efwd",
                    "permissionLevel": 2,
                    "data": {
                        "id": "1",
                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                        "type": "sql.jdbc",
                        "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                    },
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "8xzu-h29o-rwy9-ff4u-00/p9of-o2tl-2bck-yurc-1v",
                    "key": "p9of-o2tl-2bck-yurc-1v",
                    "uuid": "p9of-o2tl-2bck-yurc-1v"
                },
                {
                    "name": "SampleEFWD Sample Travel Data",
                    "type": "sql.jdbc",
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "dataSourceType": "Plain Jdbc DataSource",
                    "classifier": "efwd",
                    "permissionLevel": 2,
                    "data": {
                        "id": "1",
                        "dir": "1463377807724/1463377978248/Sample EFW Dashboard",
                        "type": "sql.jdbc",
                        "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                    },
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "8xzu-h29o-rwy9-ff4u-00/nnj1-lny4-p8zr-m30f-4i",
                    "key": "nnj1-lny4-p8zr-m30f-4i",
                    "uuid": "nnj1-lny4-p8zr-m30f-4i"
                },
                {
                    "name": "SampleTravelDataDerby",
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "dataSourceType": "Managed DataSource",
                    "classifier": "global",
                    "type": "dynamicDataSource",
                    "data": {
                        "id": "1",
                        "type": "dynamicDataSource"
                    },
                    "permissionLevel": 5,
                    "dataSourceProvider": "tomcat",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "8xzu-h29o-rwy9-ff4u-00/6opy-fupq-dm4d-mntn-mx",
                    "key": "6opy-fupq-dm4d-mntn-mx",
                    "uuid": "6opy-fupq-dm4d-mntn-mx"
                },
                {
                    "name": "test_SampleTravelDataDerby",
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "dataSourceType": "Managed DataSource",
                    "classifier": "global",
                    "type": "dynamicDataSource",
                    "data": {
                        "id": "2",
                        "type": "dynamicDataSource"
                    },
                    "permissionLevel": 5,
                    "dataSourceProvider": "tomcat",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "8xzu-h29o-rwy9-ff4u-00/aj9n-aiuk-gn7w-uvvo-pb",
                    "key": "aj9n-aiuk-gn7w-uvvo-pb",
                    "uuid": "aj9n-aiuk-gn7w-uvvo-pb"
                }
            ],
            "key": "8xzu-h29o-rwy9-ff4u-00",
            "uuid": "8xzu-h29o-rwy9-ff4u-00",
            "keyPath": "8xzu-h29o-rwy9-ff4u-00",
            "category": "dsGroup",
            "imgUrl": "images/data_sources/defaut_datasource.png"
        },
        {
            "name": "Sqlite",
            "children": [
                {
                    "name": "chinook",
                    "driver": "org.sqlite.JDBC",
                    "dataSourceType": "Managed DataSource",
                    "classifier": "global",
                    "type": "dynamicDataSource",
                    "data": {
                        "id": "3",
                        "type": "dynamicDataSource"
                    },
                    "permissionLevel": 5,
                    "dataSourceProvider": "tomcat",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Sqlite",
                    "keyPath": "9rio-8bav-i2yu-uh72-yx/qmj8-9xhq-y3xf-94gn-nm",
                    "key": "qmj8-9xhq-y3xf-94gn-nm",
                    "uuid": "qmj8-9xhq-y3xf-94gn-nm"
                },
                {
                    "name": "sqlite std",
                    "driver": "org.sqlite.JDBC",
                    "dataSourceType": "Managed DataSource",
                    "classifier": "global",
                    "type": "dynamicDataSource",
                    "data": {
                        "id": "4",
                        "type": "dynamicDataSource"
                    },
                    "permissionLevel": 5,
                    "dataSourceProvider": "tomcat",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Sqlite",
                    "keyPath": "9rio-8bav-i2yu-uh72-yx/jaw1-koue-papb-vh3o-5i",
                    "key": "jaw1-koue-papb-vh3o-5i",
                    "uuid": "jaw1-koue-papb-vh3o-5i"
                }
            ],
            "key": "9rio-8bav-i2yu-uh72-yx",
            "uuid": "9rio-8bav-i2yu-uh72-yx",
            "keyPath": "9rio-8bav-i2yu-uh72-yx",
            "category": "dsGroup",
            "imgUrl": "images/data_sources/defaut_datasource.png"
        }
    ],
    "workFlow": {
        "dataList": []
    },
    "dataSource": [],
    "tables": {},
    "categories": {
        "8xzu-h29o-rwy9-ff4u-00": {
            "ds": {
                "name": "SampleEFWD Sample Travel Data",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                "dataSourceType": "Plain Jdbc DataSource",
                "classifier": "efwd",
                "permissionLevel": 2,
                "data": {
                    "id": "1",
                    "dir": "1463377807724/1463377978248/Sample EFW Report",
                    "type": "sql.jdbc",
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                }
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
        "9rio-8bav-i2yu-uh72-yx": {
            "ds": {
                "name": "chinook",
                "driver": "org.sqlite.JDBC",
                "dataSourceType": "Managed DataSource",
                "classifier": "global",
                "type": "dynamicDataSource",
                "data": {
                    "id": "3",
                    "type": "dynamicDataSource"
                },
                "permissionLevel": 5,
                "dataSourceProvider": "tomcat"
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
    "allTablesKeys": [],
    "selectedTableKeys": [],
    "metadataName": "Metadata_1",
    "activeDataSource": false,
    "metadataToEdit": false,
    "isSavingInProgress": false
}

//snapshot when draggred 5 tables to metadata section from derby ds
export const storeSnapshotForSave_6 = {
    "dataFetchedFor": {
        "getDatasource": true,
        "joins": false,
        "listDataSources": true,
        "81vl-nz48-tq1n-7f6u-kq": true,
        "njv4-j3qu-0md3-bc7q-6v": false,
        "mc7w-jpww-cz3j-dclr-fb": false
    },
    "loadingStatus": {
        "getDatasource": true,
        "listDataSources": true,
        "81vl-nz48-tq1n-7f6u-kq": true,
        "njv4-j3qu-0md3-bc7q-6v": false,
        "mc7w-jpww-cz3j-dclr-fb": false
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
            "name": "Amazon Redshift",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Amazondynamodb",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Dremio",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Googlebigquery",
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
            "name": "Mysql",
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
            "name": "Amazon Redshift",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Amazondynamodb",
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
            "name": "Googlebigquery",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "driver": "com.helical.mongodb.MongoJdbcDriver",
            "databaseDialect": "himongo\t\t\t\t\t\t\t\t\t\t   ",
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
            "name": "Mysql",
            "categoryType": "supported",
            "categoryName": "Supported"
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
            "name": "Presto",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "driver": "ru.yandex.clickhouse.ClickHouseDriver",
            "databaseDialect": "clickhouse",
            "name": "Ru Yandex Clickhouse",
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
            "databaseDialect": "himongo\t\t\t\t\t\t\t\t\t\t   ",
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
            "driver": "ru.yandex.clickhouse.ClickHouseDriver",
            "databaseDialect": "clickhouse",
            "name": "Ru Yandex Clickhouse",
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
            "available": "true",
            "driver": "com.helical.mongodb.MongoJdbcDriver"
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
            "name": "SampleEFWD Sample Travel Data",
            "type": "sql.jdbc",
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "dataSourceType": "Plain Jdbc DataSource",
            "classifier": "efwd",
            "permissionLevel": 2,
            "data": {
                "id": "1",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
            }
        },
        {
            "name": "1",
            "dataSourceType": "Managed DataSource",
            "classifier": "global",
            "permissionLevel": 5,
            "data": {
                "id": "1",
                "dir": "1463377807724/1593413909773",
                "type": "global.jdbc"
            }
        },
        {
            "name": "1",
            "dataSourceType": "Managed DataSource",
            "classifier": "global",
            "permissionLevel": 5,
            "data": {
                "id": "1",
                "dir": "1463377807724/1570173811451",
                "type": "global.jdbc"
            }
        },
        {
            "name": "SampleEFWD Sample Travel Data",
            "type": "sql.jdbc",
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "dataSourceType": "Plain Jdbc DataSource",
            "classifier": "efwd",
            "permissionLevel": 2,
            "data": {
                "id": "1",
                "dir": "1463377807724/1463377978248/Sample EFW Dashboard",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
            }
        },
        {
            "name": "SampleTravelDataDerby",
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "dataSourceType": "Managed DataSource",
            "classifier": "global",
            "type": "dynamicDataSource",
            "data": {
                "id": "1",
                "type": "dynamicDataSource"
            },
            "permissionLevel": 5,
            "dataSourceProvider": "tomcat"
        },
        {
            "name": "test_SampleTravelDataDerby",
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "dataSourceType": "Managed DataSource",
            "classifier": "global",
            "type": "dynamicDataSource",
            "data": {
                "id": "2",
                "type": "dynamicDataSource"
            },
            "permissionLevel": 5,
            "dataSourceProvider": "tomcat"
        },
        {
            "name": "chinook",
            "driver": "org.sqlite.JDBC",
            "dataSourceType": "Managed DataSource",
            "classifier": "global",
            "type": "dynamicDataSource",
            "data": {
                "id": "3",
                "type": "dynamicDataSource"
            },
            "permissionLevel": 5,
            "dataSourceProvider": "tomcat"
        },
        {
            "name": "sqlite std",
            "driver": "org.sqlite.JDBC",
            "dataSourceType": "Managed DataSource",
            "classifier": "global",
            "type": "dynamicDataSource",
            "data": {
                "id": "4",
                "type": "dynamicDataSource"
            },
            "permissionLevel": 5,
            "dataSourceProvider": "tomcat"
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
                    "name": "SampleEFWD Sample Travel Data",
                    "type": "sql.jdbc",
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "dataSourceType": "Plain Jdbc DataSource",
                    "classifier": "efwd",
                    "permissionLevel": 2,
                    "data": {
                        "id": "1",
                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                        "type": "sql.jdbc",
                        "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                    },
                    "category": "dataSource",
                    "children": [
                        {
                            "name": "SQLJ",
                            "children": [],
                            "keyPath": "81vl-nz48-tq1n-7f6u-kq/njv4-j3qu-0md3-bc7q-6v/iw5j-iysu-bmax-brst-sb",
                            "key": "iw5j-iysu-bmax-brst-sb",
                            "uuid": "iw5j-iysu-bmax-brst-sb",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "SYSFUN",
                            "children": [],
                            "keyPath": "81vl-nz48-tq1n-7f6u-kq/njv4-j3qu-0md3-bc7q-6v/igny-djjy-uoh3-ah90-w7",
                            "key": "igny-djjy-uoh3-ah90-w7",
                            "uuid": "igny-djjy-uoh3-ah90-w7",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "SYSCAT",
                            "children": [],
                            "keyPath": "81vl-nz48-tq1n-7f6u-kq/njv4-j3qu-0md3-bc7q-6v/lm2d-s9e0-bjtt-rlvf-m0",
                            "key": "lm2d-s9e0-bjtt-rlvf-m0",
                            "uuid": "lm2d-s9e0-bjtt-rlvf-m0",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "HIUSER",
                            "children": [
                                {
                                    "id": "be534112989b616b194bc59c2fb25a42",
                                    "name": "geo_cordinates",
                                    "data": {
                                        "id": "1",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "type": "sql.jdbc",
                                        "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                                    },
                                    "keyPath": "81vl-nz48-tq1n-7f6u-kq/njv4-j3qu-0md3-bc7q-6v/mc7w-jpww-cz3j-dclr-fb/io2j-w8pz-icw0-6bug-1k",
                                    "key": "io2j-w8pz-icw0-6bug-1k",
                                    "alias": "geo_cordinates",
                                    "uuid": "io2j-w8pz-icw0-6bug-1k",
                                    "connId": "wksqe",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "sql.jdbc",
                                        "baseType": "sql.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "connId": "wksqe",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleEFWD Sample Travel Data",
                                        "dsKeyPath": "81vl-nz48-tq1n-7f6u-kq/njv4-j3qu-0md3-bc7q-6v/mc7w-jpww-cz3j-dclr-fb",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleEFWD Sample Travel Data",
                                    "category": "table",
                                    "nameWithConnId": "geo_cordinates_wksqe",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "9645c648a1c0dbeec1287aaf1e996db3",
                                    "name": "meeting_details",
                                    "data": {
                                        "id": "1",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "type": "sql.jdbc",
                                        "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                                    },
                                    "keyPath": "81vl-nz48-tq1n-7f6u-kq/njv4-j3qu-0md3-bc7q-6v/mc7w-jpww-cz3j-dclr-fb/fscy-o54b-acm0-nl9o-f4",
                                    "key": "fscy-o54b-acm0-nl9o-f4",
                                    "alias": "meeting_details",
                                    "uuid": "fscy-o54b-acm0-nl9o-f4",
                                    "connId": "wksqe",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "sql.jdbc",
                                        "baseType": "sql.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "connId": "wksqe",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleEFWD Sample Travel Data",
                                        "dsKeyPath": "81vl-nz48-tq1n-7f6u-kq/njv4-j3qu-0md3-bc7q-6v/mc7w-jpww-cz3j-dclr-fb",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleEFWD Sample Travel Data",
                                    "category": "table",
                                    "nameWithConnId": "meeting_details_wksqe",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "4e1fd245f4d13b77be423a43f01d80b2",
                                    "name": "employee_details",
                                    "data": {
                                        "id": "1",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "type": "sql.jdbc",
                                        "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                                    },
                                    "keyPath": "81vl-nz48-tq1n-7f6u-kq/njv4-j3qu-0md3-bc7q-6v/mc7w-jpww-cz3j-dclr-fb/ba96-g7uu-2d5t-o27u-vw",
                                    "key": "ba96-g7uu-2d5t-o27u-vw",
                                    "alias": "employee_details",
                                    "uuid": "ba96-g7uu-2d5t-o27u-vw",
                                    "connId": "wksqe",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "sql.jdbc",
                                        "baseType": "sql.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "connId": "wksqe",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleEFWD Sample Travel Data",
                                        "dsKeyPath": "81vl-nz48-tq1n-7f6u-kq/njv4-j3qu-0md3-bc7q-6v/mc7w-jpww-cz3j-dclr-fb",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleEFWD Sample Travel Data",
                                    "category": "table",
                                    "nameWithConnId": "employee_details_wksqe",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "4ac5d9f68b58bd7c0d179146e46795be",
                                    "name": "dimdate",
                                    "data": {
                                        "id": "1",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "type": "sql.jdbc",
                                        "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                                    },
                                    "keyPath": "81vl-nz48-tq1n-7f6u-kq/njv4-j3qu-0md3-bc7q-6v/mc7w-jpww-cz3j-dclr-fb/xmr9-c410-rp3i-xwo5-z3",
                                    "key": "xmr9-c410-rp3i-xwo5-z3",
                                    "alias": "dimdate",
                                    "uuid": "xmr9-c410-rp3i-xwo5-z3",
                                    "connId": "wksqe",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "sql.jdbc",
                                        "baseType": "sql.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "connId": "wksqe",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleEFWD Sample Travel Data",
                                        "dsKeyPath": "81vl-nz48-tq1n-7f6u-kq/njv4-j3qu-0md3-bc7q-6v/mc7w-jpww-cz3j-dclr-fb",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleEFWD Sample Travel Data",
                                    "category": "table",
                                    "nameWithConnId": "dimdate_wksqe",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "8a28627d07d04ef096d9935f12e0c7e9",
                                    "name": "travel_details",
                                    "data": {
                                        "id": "1",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "type": "sql.jdbc",
                                        "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                                    },
                                    "keyPath": "81vl-nz48-tq1n-7f6u-kq/njv4-j3qu-0md3-bc7q-6v/mc7w-jpww-cz3j-dclr-fb/s0od-f5qd-h4bb-yeii-ly",
                                    "key": "s0od-f5qd-h4bb-yeii-ly",
                                    "alias": "travel_details",
                                    "uuid": "s0od-f5qd-h4bb-yeii-ly",
                                    "connId": "wksqe",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "sql.jdbc",
                                        "baseType": "sql.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "connId": "wksqe",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleEFWD Sample Travel Data",
                                        "dsKeyPath": "81vl-nz48-tq1n-7f6u-kq/njv4-j3qu-0md3-bc7q-6v/mc7w-jpww-cz3j-dclr-fb",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleEFWD Sample Travel Data",
                                    "category": "table",
                                    "nameWithConnId": "travel_details_wksqe",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                }
                            ],
                            "keyPath": "81vl-nz48-tq1n-7f6u-kq/njv4-j3qu-0md3-bc7q-6v/mc7w-jpww-cz3j-dclr-fb",
                            "key": "mc7w-jpww-cz3j-dclr-fb",
                            "uuid": "mc7w-jpww-cz3j-dclr-fb",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data",
                            "fetched": true
                        },
                        {
                            "name": "SYSCS_DIAG",
                            "children": [],
                            "keyPath": "81vl-nz48-tq1n-7f6u-kq/njv4-j3qu-0md3-bc7q-6v/03wk-lgt7-w9pd-pt0s-5h",
                            "key": "03wk-lgt7-w9pd-pt0s-5h",
                            "uuid": "03wk-lgt7-w9pd-pt0s-5h",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "SYSCS_UTIL",
                            "children": [],
                            "keyPath": "81vl-nz48-tq1n-7f6u-kq/njv4-j3qu-0md3-bc7q-6v/ylao-hzp3-pg3p-az2e-z9",
                            "key": "ylao-hzp3-pg3p-az2e-z9",
                            "uuid": "ylao-hzp3-pg3p-az2e-z9",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "SYSIBM",
                            "children": [],
                            "keyPath": "81vl-nz48-tq1n-7f6u-kq/njv4-j3qu-0md3-bc7q-6v/va7d-qkju-q3ba-xqw3-r3",
                            "key": "va7d-qkju-q3ba-xqw3-r3",
                            "uuid": "va7d-qkju-q3ba-xqw3-r3",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "APP",
                            "children": [],
                            "keyPath": "81vl-nz48-tq1n-7f6u-kq/njv4-j3qu-0md3-bc7q-6v/b023-2ukb-sjuo-28fm-zu",
                            "key": "b023-2ukb-sjuo-28fm-zu",
                            "uuid": "b023-2ukb-sjuo-28fm-zu",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "NULLID",
                            "children": [],
                            "keyPath": "81vl-nz48-tq1n-7f6u-kq/njv4-j3qu-0md3-bc7q-6v/71zd-adk3-2nfo-xjw1-m4",
                            "key": "71zd-adk3-2nfo-xjw1-m4",
                            "uuid": "71zd-adk3-2nfo-xjw1-m4",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "SYSPROC",
                            "children": [],
                            "keyPath": "81vl-nz48-tq1n-7f6u-kq/njv4-j3qu-0md3-bc7q-6v/ghwi-y1o6-cy8a-jei2-42",
                            "key": "ghwi-y1o6-cy8a-jei2-42",
                            "uuid": "ghwi-y1o6-cy8a-jei2-42",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "SYS",
                            "children": [],
                            "keyPath": "81vl-nz48-tq1n-7f6u-kq/njv4-j3qu-0md3-bc7q-6v/of1u-h1h5-qlgt-f2bz-if",
                            "key": "of1u-h1h5-qlgt-f2bz-if",
                            "uuid": "of1u-h1h5-qlgt-f2bz-if",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "SYSSTAT",
                            "children": [],
                            "keyPath": "81vl-nz48-tq1n-7f6u-kq/njv4-j3qu-0md3-bc7q-6v/qs3g-y3zv-7ht0-h7yu-xk",
                            "key": "qs3g-y3zv-7ht0-h7yu-xk",
                            "uuid": "qs3g-y3zv-7ht0-h7yu-xk",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        }
                    ],
                    "driverType": "Derby",
                    "keyPath": "81vl-nz48-tq1n-7f6u-kq/njv4-j3qu-0md3-bc7q-6v",
                    "key": "njv4-j3qu-0md3-bc7q-6v",
                    "uuid": "njv4-j3qu-0md3-bc7q-6v",
                    "fetched": true
                },
                {
                    "name": "SampleEFWD Sample Travel Data",
                    "type": "sql.jdbc",
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "dataSourceType": "Plain Jdbc DataSource",
                    "classifier": "efwd",
                    "permissionLevel": 2,
                    "data": {
                        "id": "1",
                        "dir": "1463377807724/1463377978248/Sample EFW Dashboard",
                        "type": "sql.jdbc",
                        "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                    },
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "81vl-nz48-tq1n-7f6u-kq/qpth-qgq8-1iq7-7qof-vc",
                    "key": "qpth-qgq8-1iq7-7qof-vc",
                    "uuid": "qpth-qgq8-1iq7-7qof-vc"
                },
                {
                    "name": "SampleTravelDataDerby",
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "dataSourceType": "Managed DataSource",
                    "classifier": "global",
                    "type": "dynamicDataSource",
                    "data": {
                        "id": "1",
                        "type": "dynamicDataSource"
                    },
                    "permissionLevel": 5,
                    "dataSourceProvider": "tomcat",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "81vl-nz48-tq1n-7f6u-kq/femc-bk09-oaa2-c6kq-9z",
                    "key": "femc-bk09-oaa2-c6kq-9z",
                    "uuid": "femc-bk09-oaa2-c6kq-9z"
                },
                {
                    "name": "test_SampleTravelDataDerby",
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "dataSourceType": "Managed DataSource",
                    "classifier": "global",
                    "type": "dynamicDataSource",
                    "data": {
                        "id": "2",
                        "type": "dynamicDataSource"
                    },
                    "permissionLevel": 5,
                    "dataSourceProvider": "tomcat",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "81vl-nz48-tq1n-7f6u-kq/chl4-x1m2-doh9-wll1-df",
                    "key": "chl4-x1m2-doh9-wll1-df",
                    "uuid": "chl4-x1m2-doh9-wll1-df"
                }
            ],
            "key": "81vl-nz48-tq1n-7f6u-kq",
            "uuid": "81vl-nz48-tq1n-7f6u-kq",
            "keyPath": "81vl-nz48-tq1n-7f6u-kq",
            "category": "dsGroup",
            "imgUrl": "images/data_sources/defaut_datasource.png"
        },
        {
            "name": "Sqlite",
            "children": [
                {
                    "name": "chinook",
                    "driver": "org.sqlite.JDBC",
                    "dataSourceType": "Managed DataSource",
                    "classifier": "global",
                    "type": "dynamicDataSource",
                    "data": {
                        "id": "3",
                        "type": "dynamicDataSource"
                    },
                    "permissionLevel": 5,
                    "dataSourceProvider": "tomcat",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Sqlite",
                    "keyPath": "0kzn-hfko-l0jf-dyvy-6g/l42i-5j2b-vte1-znsv-ky",
                    "key": "l42i-5j2b-vte1-znsv-ky",
                    "uuid": "l42i-5j2b-vte1-znsv-ky"
                },
                {
                    "name": "sqlite std",
                    "driver": "org.sqlite.JDBC",
                    "dataSourceType": "Managed DataSource",
                    "classifier": "global",
                    "type": "dynamicDataSource",
                    "data": {
                        "id": "4",
                        "type": "dynamicDataSource"
                    },
                    "permissionLevel": 5,
                    "dataSourceProvider": "tomcat",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Sqlite",
                    "keyPath": "0kzn-hfko-l0jf-dyvy-6g/6uve-w1y4-hcfr-h4qy-lg",
                    "key": "6uve-w1y4-hcfr-h4qy-lg",
                    "uuid": "6uve-w1y4-hcfr-h4qy-lg"
                }
            ],
            "key": "0kzn-hfko-l0jf-dyvy-6g",
            "uuid": "0kzn-hfko-l0jf-dyvy-6g",
            "keyPath": "0kzn-hfko-l0jf-dyvy-6g",
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
                    "dir": "1463377807724/1463377978248/Sample EFW Report",
                    "type": "sql.jdbc",
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                },
                "connId": "1",
                "dsUUID": "njv4-j3qu-0md3-bc7q-6v",
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
                        "type": "sql.jdbc",
                        "baseType": "sql.jdbc",
                        "catSchemaPredicted": false,
                        "sync": false,
                        "catalog": "",
                        "schema": "HIUSER",
                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                        "connId": "wksqe",
                        "classifier": "db.workflow",
                        "datasourceName": "SampleEFWD Sample Travel Data",
                        "dsKeyPath": "81vl-nz48-tq1n-7f6u-kq/njv4-j3qu-0md3-bc7q-6v/mc7w-jpww-cz3j-dclr-fb",
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
            "type": "sql.jdbc",
            "baseType": "sql.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "",
            "schema": "HIUSER",
            "dir": "1463377807724/1463377978248/Sample EFW Report",
            "connId": "wksqe",
            "classifier": "db.workflow",
            "datasourceName": "SampleEFWD Sample Travel Data",
            "dsKeyPath": "81vl-nz48-tq1n-7f6u-kq/njv4-j3qu-0md3-bc7q-6v/mc7w-jpww-cz3j-dclr-fb",
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
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
            },
            "keyPath": "81vl-nz48-tq1n-7f6u-kq/njv4-j3qu-0md3-bc7q-6v/mc7w-jpww-cz3j-dclr-fb/io2j-w8pz-icw0-6bug-1k",
            "key": "io2j-w8pz-icw0-6bug-1k",
            "alias": "geo_cordinates",
            "uuid": "io2j-w8pz-icw0-6bug-1k",
            "connId": "wksqe",
            "dataSource": {
                "id": "1",
                "type": "sql.jdbc",
                "baseType": "sql.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "connId": "wksqe",
                "classifier": "db.workflow",
                "datasourceName": "SampleEFWD Sample Travel Data",
                "dsKeyPath": "81vl-nz48-tq1n-7f6u-kq/njv4-j3qu-0md3-bc7q-6v/mc7w-jpww-cz3j-dclr-fb",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleEFWD Sample Travel Data",
            "category": "table",
            "nameWithConnId": "geo_cordinates_wksqe",
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
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
            },
            "keyPath": "81vl-nz48-tq1n-7f6u-kq/njv4-j3qu-0md3-bc7q-6v/mc7w-jpww-cz3j-dclr-fb/fscy-o54b-acm0-nl9o-f4",
            "key": "fscy-o54b-acm0-nl9o-f4",
            "alias": "meeting_details",
            "uuid": "fscy-o54b-acm0-nl9o-f4",
            "connId": "wksqe",
            "dataSource": {
                "id": "1",
                "type": "sql.jdbc",
                "baseType": "sql.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "connId": "wksqe",
                "classifier": "db.workflow",
                "datasourceName": "SampleEFWD Sample Travel Data",
                "dsKeyPath": "81vl-nz48-tq1n-7f6u-kq/njv4-j3qu-0md3-bc7q-6v/mc7w-jpww-cz3j-dclr-fb",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleEFWD Sample Travel Data",
            "category": "table",
            "nameWithConnId": "meeting_details_wksqe",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "meeting_details"
        },
        "employee_details": {
            "id": "4e1fd245f4d13b77be423a43f01d80b2",
            "name": "employee_details",
            "data": {
                "id": "1",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
            },
            "keyPath": "81vl-nz48-tq1n-7f6u-kq/njv4-j3qu-0md3-bc7q-6v/mc7w-jpww-cz3j-dclr-fb/ba96-g7uu-2d5t-o27u-vw",
            "key": "ba96-g7uu-2d5t-o27u-vw",
            "alias": "employee_details",
            "uuid": "ba96-g7uu-2d5t-o27u-vw",
            "connId": "wksqe",
            "dataSource": {
                "id": "1",
                "type": "sql.jdbc",
                "baseType": "sql.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "connId": "wksqe",
                "classifier": "db.workflow",
                "datasourceName": "SampleEFWD Sample Travel Data",
                "dsKeyPath": "81vl-nz48-tq1n-7f6u-kq/njv4-j3qu-0md3-bc7q-6v/mc7w-jpww-cz3j-dclr-fb",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleEFWD Sample Travel Data",
            "category": "table",
            "nameWithConnId": "employee_details_wksqe",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "employee_details"
        },
        "dimdate": {
            "id": "4ac5d9f68b58bd7c0d179146e46795be",
            "name": "dimdate",
            "data": {
                "id": "1",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
            },
            "keyPath": "81vl-nz48-tq1n-7f6u-kq/njv4-j3qu-0md3-bc7q-6v/mc7w-jpww-cz3j-dclr-fb/xmr9-c410-rp3i-xwo5-z3",
            "key": "xmr9-c410-rp3i-xwo5-z3",
            "alias": "dimdate",
            "uuid": "xmr9-c410-rp3i-xwo5-z3",
            "connId": "wksqe",
            "dataSource": {
                "id": "1",
                "type": "sql.jdbc",
                "baseType": "sql.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "connId": "wksqe",
                "classifier": "db.workflow",
                "datasourceName": "SampleEFWD Sample Travel Data",
                "dsKeyPath": "81vl-nz48-tq1n-7f6u-kq/njv4-j3qu-0md3-bc7q-6v/mc7w-jpww-cz3j-dclr-fb",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleEFWD Sample Travel Data",
            "category": "table",
            "nameWithConnId": "dimdate_wksqe",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "dimdate"
        },
        "travel_details": {
            "id": "8a28627d07d04ef096d9935f12e0c7e9",
            "name": "travel_details",
            "data": {
                "id": "1",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
            },
            "keyPath": "81vl-nz48-tq1n-7f6u-kq/njv4-j3qu-0md3-bc7q-6v/mc7w-jpww-cz3j-dclr-fb/s0od-f5qd-h4bb-yeii-ly",
            "key": "s0od-f5qd-h4bb-yeii-ly",
            "alias": "travel_details",
            "uuid": "s0od-f5qd-h4bb-yeii-ly",
            "connId": "wksqe",
            "dataSource": {
                "id": "1",
                "type": "sql.jdbc",
                "baseType": "sql.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "connId": "wksqe",
                "classifier": "db.workflow",
                "datasourceName": "SampleEFWD Sample Travel Data",
                "dsKeyPath": "81vl-nz48-tq1n-7f6u-kq/njv4-j3qu-0md3-bc7q-6v/mc7w-jpww-cz3j-dclr-fb",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleEFWD Sample Travel Data",
            "category": "table",
            "nameWithConnId": "travel_details_wksqe",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "travel_details"
        }
    },
    "categories": {
        "81vl-nz48-tq1n-7f6u-kq": {
            "ds": {
                "name": "SampleEFWD Sample Travel Data",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                "dataSourceType": "Plain Jdbc DataSource",
                "classifier": "efwd",
                "permissionLevel": 2,
                "data": {
                    "id": "1",
                    "dir": "1463377807724/1463377978248/Sample EFW Report",
                    "type": "sql.jdbc",
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                }
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
        "0kzn-hfko-l0jf-dyvy-6g": {
            "ds": {
                "name": "chinook",
                "driver": "org.sqlite.JDBC",
                "dataSourceType": "Managed DataSource",
                "classifier": "global",
                "type": "dynamicDataSource",
                "data": {
                    "id": "3",
                    "type": "dynamicDataSource"
                },
                "permissionLevel": 5,
                "dataSourceProvider": "tomcat"
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
            "id": "1",
            "type": "sql.jdbc",
            "baseType": "sql.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "",
            "schema": "HIUSER",
            "dir": "1463377807724/1463377978248/Sample EFW Report",
            "connId": "wksqe",
            "classifier": "db.workflow",
            "datasourceName": "SampleEFWD Sample Travel Data",
            "dsKeyPath": "81vl-nz48-tq1n-7f6u-kq/njv4-j3qu-0md3-bc7q-6v/mc7w-jpww-cz3j-dclr-fb",
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
        "io2j-w8pz-icw0-6bug-1k",
        "fscy-o54b-acm0-nl9o-f4",
        "ba96-g7uu-2d5t-o27u-vw",
        "xmr9-c410-rp3i-xwo5-z3",
        "s0od-f5qd-h4bb-yeii-ly"
    ],
    "selectedTableKeys": [
        "io2j-w8pz-icw0-6bug-1k",
        "fscy-o54b-acm0-nl9o-f4",
        "ba96-g7uu-2d5t-o27u-vw",
        "xmr9-c410-rp3i-xwo5-z3",
        "s0od-f5qd-h4bb-yeii-ly"
    ],
    "metadataName": "Metadata_1",
    "activeDataSource": false,
    "metadataToEdit": false,
    "isSavingInProgress": false
}

//snapshot with duplicated table
export const storeSnapshotForSave_7 = {
    "dataFetchedFor": {
        "getDatasource": true,
        "joins": false,
        "listDataSources": true,
        "vwwx-i5fg-tlbk-4kss-qh": true,
        "zbo4-j91e-0176-v9zi-ui": false,
        "9cs1-5kqq-vafg-dvsb-go": false
    },
    "loadingStatus": {
        "getDatasource": true,
        "listDataSources": true,
        "vwwx-i5fg-tlbk-4kss-qh": true,
        "zbo4-j91e-0176-v9zi-ui": false,
        "9cs1-5kqq-vafg-dvsb-go": false
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
            "name": "Amazon Redshift",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Amazondynamodb",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Dremio",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Googlebigquery",
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
            "name": "Mysql",
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
            "name": "Amazon Redshift",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Amazondynamodb",
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
            "name": "Googlebigquery",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "driver": "com.helical.mongodb.MongoJdbcDriver",
            "databaseDialect": "himongo\t\t\t\t\t\t\t\t\t\t   ",
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
            "name": "Mysql",
            "categoryType": "supported",
            "categoryName": "Supported"
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
            "name": "Presto",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "driver": "ru.yandex.clickhouse.ClickHouseDriver",
            "databaseDialect": "clickhouse",
            "name": "Ru Yandex Clickhouse",
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
            "databaseDialect": "himongo\t\t\t\t\t\t\t\t\t\t   ",
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
            "driver": "ru.yandex.clickhouse.ClickHouseDriver",
            "databaseDialect": "clickhouse",
            "name": "Ru Yandex Clickhouse",
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
            "available": "true",
            "driver": "com.helical.mongodb.MongoJdbcDriver"
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
            "name": "SampleEFWD Sample Travel Data",
            "type": "sql.jdbc",
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "dataSourceType": "Plain Jdbc DataSource",
            "classifier": "efwd",
            "permissionLevel": 2,
            "data": {
                "id": "1",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
            }
        },
        {
            "name": "1",
            "dataSourceType": "Managed DataSource",
            "classifier": "global",
            "permissionLevel": 5,
            "data": {
                "id": "1",
                "dir": "1463377807724/1593413909773",
                "type": "global.jdbc"
            }
        },
        {
            "name": "1",
            "dataSourceType": "Managed DataSource",
            "classifier": "global",
            "permissionLevel": 5,
            "data": {
                "id": "1",
                "dir": "1463377807724/1570173811451",
                "type": "global.jdbc"
            }
        },
        {
            "name": "SampleEFWD Sample Travel Data",
            "type": "sql.jdbc",
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "dataSourceType": "Plain Jdbc DataSource",
            "classifier": "efwd",
            "permissionLevel": 2,
            "data": {
                "id": "1",
                "dir": "1463377807724/1463377978248/Sample EFW Dashboard",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
            }
        },
        {
            "name": "SampleTravelDataDerby",
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "dataSourceType": "Managed DataSource",
            "classifier": "global",
            "type": "dynamicDataSource",
            "data": {
                "id": "1",
                "type": "dynamicDataSource"
            },
            "permissionLevel": 5,
            "dataSourceProvider": "tomcat"
        },
        {
            "name": "test_SampleTravelDataDerby",
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "dataSourceType": "Managed DataSource",
            "classifier": "global",
            "type": "dynamicDataSource",
            "data": {
                "id": "2",
                "type": "dynamicDataSource"
            },
            "permissionLevel": 5,
            "dataSourceProvider": "tomcat"
        },
        {
            "name": "chinook",
            "driver": "org.sqlite.JDBC",
            "dataSourceType": "Managed DataSource",
            "classifier": "global",
            "type": "dynamicDataSource",
            "data": {
                "id": "3",
                "type": "dynamicDataSource"
            },
            "permissionLevel": 5,
            "dataSourceProvider": "tomcat"
        },
        {
            "name": "sqlite std",
            "driver": "org.sqlite.JDBC",
            "dataSourceType": "Managed DataSource",
            "classifier": "global",
            "type": "dynamicDataSource",
            "data": {
                "id": "4",
                "type": "dynamicDataSource"
            },
            "permissionLevel": 5,
            "dataSourceProvider": "tomcat"
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
                    "name": "SampleEFWD Sample Travel Data",
                    "type": "sql.jdbc",
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "dataSourceType": "Plain Jdbc DataSource",
                    "classifier": "efwd",
                    "permissionLevel": 2,
                    "data": {
                        "id": "1",
                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                        "type": "sql.jdbc",
                        "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                    },
                    "category": "dataSource",
                    "children": [
                        {
                            "name": "SQLJ",
                            "children": [],
                            "keyPath": "vwwx-i5fg-tlbk-4kss-qh/zbo4-j91e-0176-v9zi-ui/h5no-skzo-34qi-i6h6-0s",
                            "key": "h5no-skzo-34qi-i6h6-0s",
                            "uuid": "h5no-skzo-34qi-i6h6-0s",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "SYSFUN",
                            "children": [],
                            "keyPath": "vwwx-i5fg-tlbk-4kss-qh/zbo4-j91e-0176-v9zi-ui/7imu-hqfy-t4sq-rq11-1x",
                            "key": "7imu-hqfy-t4sq-rq11-1x",
                            "uuid": "7imu-hqfy-t4sq-rq11-1x",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "SYSCAT",
                            "children": [],
                            "keyPath": "vwwx-i5fg-tlbk-4kss-qh/zbo4-j91e-0176-v9zi-ui/g5qv-3ey3-jpci-99pz-du",
                            "key": "g5qv-3ey3-jpci-99pz-du",
                            "uuid": "g5qv-3ey3-jpci-99pz-du",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "HIUSER",
                            "children": [
                                {
                                    "id": "be534112989b616b194bc59c2fb25a42",
                                    "name": "geo_cordinates",
                                    "data": {
                                        "id": "1",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "type": "sql.jdbc",
                                        "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                                    },
                                    "keyPath": "vwwx-i5fg-tlbk-4kss-qh/zbo4-j91e-0176-v9zi-ui/9cs1-5kqq-vafg-dvsb-go/fnz8-p6go-6g86-br34-sl",
                                    "key": "fnz8-p6go-6g86-br34-sl",
                                    "alias": "geo_cordinates",
                                    "uuid": "fnz8-p6go-6g86-br34-sl",
                                    "connId": "7xvwn",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "sql.jdbc",
                                        "baseType": "sql.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "connId": "7xvwn",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleEFWD Sample Travel Data",
                                        "dsKeyPath": "vwwx-i5fg-tlbk-4kss-qh/zbo4-j91e-0176-v9zi-ui/9cs1-5kqq-vafg-dvsb-go",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleEFWD Sample Travel Data",
                                    "category": "table",
                                    "nameWithConnId": "geo_cordinates_7xvwn",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "9645c648a1c0dbeec1287aaf1e996db3",
                                    "name": "meeting_details",
                                    "data": {
                                        "id": "1",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "type": "sql.jdbc",
                                        "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                                    },
                                    "keyPath": "vwwx-i5fg-tlbk-4kss-qh/zbo4-j91e-0176-v9zi-ui/9cs1-5kqq-vafg-dvsb-go/z3ps-2gcw-m8go-bblb-7p",
                                    "key": "z3ps-2gcw-m8go-bblb-7p",
                                    "alias": "meeting_details",
                                    "uuid": "z3ps-2gcw-m8go-bblb-7p",
                                    "connId": "7xvwn",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "sql.jdbc",
                                        "baseType": "sql.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "connId": "7xvwn",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleEFWD Sample Travel Data",
                                        "dsKeyPath": "vwwx-i5fg-tlbk-4kss-qh/zbo4-j91e-0176-v9zi-ui/9cs1-5kqq-vafg-dvsb-go",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleEFWD Sample Travel Data",
                                    "category": "table",
                                    "nameWithConnId": "meeting_details_7xvwn",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "4e1fd245f4d13b77be423a43f01d80b2",
                                    "name": "employee_details",
                                    "data": {
                                        "id": "1",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "type": "sql.jdbc",
                                        "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                                    },
                                    "keyPath": "vwwx-i5fg-tlbk-4kss-qh/zbo4-j91e-0176-v9zi-ui/9cs1-5kqq-vafg-dvsb-go/edys-xxk1-ci7y-r2k6-aj",
                                    "key": "edys-xxk1-ci7y-r2k6-aj",
                                    "alias": "employee_details",
                                    "uuid": "edys-xxk1-ci7y-r2k6-aj",
                                    "connId": "7xvwn",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "sql.jdbc",
                                        "baseType": "sql.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "connId": "7xvwn",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleEFWD Sample Travel Data",
                                        "dsKeyPath": "vwwx-i5fg-tlbk-4kss-qh/zbo4-j91e-0176-v9zi-ui/9cs1-5kqq-vafg-dvsb-go",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleEFWD Sample Travel Data",
                                    "category": "table",
                                    "nameWithConnId": "employee_details_7xvwn",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "4ac5d9f68b58bd7c0d179146e46795be",
                                    "name": "dimdate",
                                    "data": {
                                        "id": "1",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "type": "sql.jdbc",
                                        "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                                    },
                                    "keyPath": "vwwx-i5fg-tlbk-4kss-qh/zbo4-j91e-0176-v9zi-ui/9cs1-5kqq-vafg-dvsb-go/1eng-chtz-cpix-am2s-ar",
                                    "key": "1eng-chtz-cpix-am2s-ar",
                                    "alias": "dimdate",
                                    "uuid": "1eng-chtz-cpix-am2s-ar",
                                    "connId": "7xvwn",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "sql.jdbc",
                                        "baseType": "sql.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "connId": "7xvwn",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleEFWD Sample Travel Data",
                                        "dsKeyPath": "vwwx-i5fg-tlbk-4kss-qh/zbo4-j91e-0176-v9zi-ui/9cs1-5kqq-vafg-dvsb-go",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleEFWD Sample Travel Data",
                                    "category": "table",
                                    "nameWithConnId": "dimdate_7xvwn",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "8a28627d07d04ef096d9935f12e0c7e9",
                                    "name": "travel_details",
                                    "data": {
                                        "id": "1",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "type": "sql.jdbc",
                                        "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                                    },
                                    "keyPath": "vwwx-i5fg-tlbk-4kss-qh/zbo4-j91e-0176-v9zi-ui/9cs1-5kqq-vafg-dvsb-go/v8pl-7j4h-ytpm-h37s-ul",
                                    "key": "v8pl-7j4h-ytpm-h37s-ul",
                                    "alias": "travel_details",
                                    "uuid": "v8pl-7j4h-ytpm-h37s-ul",
                                    "connId": "7xvwn",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "sql.jdbc",
                                        "baseType": "sql.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "connId": "7xvwn",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleEFWD Sample Travel Data",
                                        "dsKeyPath": "vwwx-i5fg-tlbk-4kss-qh/zbo4-j91e-0176-v9zi-ui/9cs1-5kqq-vafg-dvsb-go",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleEFWD Sample Travel Data",
                                    "category": "table",
                                    "nameWithConnId": "travel_details_7xvwn",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                }
                            ],
                            "keyPath": "vwwx-i5fg-tlbk-4kss-qh/zbo4-j91e-0176-v9zi-ui/9cs1-5kqq-vafg-dvsb-go",
                            "key": "9cs1-5kqq-vafg-dvsb-go",
                            "uuid": "9cs1-5kqq-vafg-dvsb-go",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data",
                            "fetched": true
                        },
                        {
                            "name": "SYSCS_DIAG",
                            "children": [],
                            "keyPath": "vwwx-i5fg-tlbk-4kss-qh/zbo4-j91e-0176-v9zi-ui/i7f0-bvrl-uxlz-9v87-su",
                            "key": "i7f0-bvrl-uxlz-9v87-su",
                            "uuid": "i7f0-bvrl-uxlz-9v87-su",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "SYSCS_UTIL",
                            "children": [],
                            "keyPath": "vwwx-i5fg-tlbk-4kss-qh/zbo4-j91e-0176-v9zi-ui/a4ez-j9ae-kyz2-y7kz-u5",
                            "key": "a4ez-j9ae-kyz2-y7kz-u5",
                            "uuid": "a4ez-j9ae-kyz2-y7kz-u5",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "SYSIBM",
                            "children": [],
                            "keyPath": "vwwx-i5fg-tlbk-4kss-qh/zbo4-j91e-0176-v9zi-ui/v5j0-c7nh-qrc5-f5ge-yc",
                            "key": "v5j0-c7nh-qrc5-f5ge-yc",
                            "uuid": "v5j0-c7nh-qrc5-f5ge-yc",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "APP",
                            "children": [],
                            "keyPath": "vwwx-i5fg-tlbk-4kss-qh/zbo4-j91e-0176-v9zi-ui/nks5-4x3h-j9s0-fgyp-9e",
                            "key": "nks5-4x3h-j9s0-fgyp-9e",
                            "uuid": "nks5-4x3h-j9s0-fgyp-9e",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "NULLID",
                            "children": [],
                            "keyPath": "vwwx-i5fg-tlbk-4kss-qh/zbo4-j91e-0176-v9zi-ui/8gvr-q90i-vler-1uuc-tq",
                            "key": "8gvr-q90i-vler-1uuc-tq",
                            "uuid": "8gvr-q90i-vler-1uuc-tq",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "SYSPROC",
                            "children": [],
                            "keyPath": "vwwx-i5fg-tlbk-4kss-qh/zbo4-j91e-0176-v9zi-ui/1xxv-49ui-kqky-01e3-32",
                            "key": "1xxv-49ui-kqky-01e3-32",
                            "uuid": "1xxv-49ui-kqky-01e3-32",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "SYS",
                            "children": [],
                            "keyPath": "vwwx-i5fg-tlbk-4kss-qh/zbo4-j91e-0176-v9zi-ui/b0bu-fvha-f9gn-dfzh-ps",
                            "key": "b0bu-fvha-f9gn-dfzh-ps",
                            "uuid": "b0bu-fvha-f9gn-dfzh-ps",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "SYSSTAT",
                            "children": [],
                            "keyPath": "vwwx-i5fg-tlbk-4kss-qh/zbo4-j91e-0176-v9zi-ui/w19a-zzn8-gnqc-mv1x-bz",
                            "key": "w19a-zzn8-gnqc-mv1x-bz",
                            "uuid": "w19a-zzn8-gnqc-mv1x-bz",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        }
                    ],
                    "driverType": "Derby",
                    "keyPath": "vwwx-i5fg-tlbk-4kss-qh/zbo4-j91e-0176-v9zi-ui",
                    "key": "zbo4-j91e-0176-v9zi-ui",
                    "uuid": "zbo4-j91e-0176-v9zi-ui",
                    "fetched": true
                },
                {
                    "name": "SampleEFWD Sample Travel Data",
                    "type": "sql.jdbc",
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "dataSourceType": "Plain Jdbc DataSource",
                    "classifier": "efwd",
                    "permissionLevel": 2,
                    "data": {
                        "id": "1",
                        "dir": "1463377807724/1463377978248/Sample EFW Dashboard",
                        "type": "sql.jdbc",
                        "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                    },
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "vwwx-i5fg-tlbk-4kss-qh/40yd-56m9-l23k-bzm6-p5",
                    "key": "40yd-56m9-l23k-bzm6-p5",
                    "uuid": "40yd-56m9-l23k-bzm6-p5"
                },
                {
                    "name": "SampleTravelDataDerby",
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "dataSourceType": "Managed DataSource",
                    "classifier": "global",
                    "type": "dynamicDataSource",
                    "data": {
                        "id": "1",
                        "type": "dynamicDataSource"
                    },
                    "permissionLevel": 5,
                    "dataSourceProvider": "tomcat",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "vwwx-i5fg-tlbk-4kss-qh/f0lm-qnpk-2tu0-8xjq-g0",
                    "key": "f0lm-qnpk-2tu0-8xjq-g0",
                    "uuid": "f0lm-qnpk-2tu0-8xjq-g0"
                },
                {
                    "name": "test_SampleTravelDataDerby",
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "dataSourceType": "Managed DataSource",
                    "classifier": "global",
                    "type": "dynamicDataSource",
                    "data": {
                        "id": "2",
                        "type": "dynamicDataSource"
                    },
                    "permissionLevel": 5,
                    "dataSourceProvider": "tomcat",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "vwwx-i5fg-tlbk-4kss-qh/c6ge-fk4h-sc58-fp69-x3",
                    "key": "c6ge-fk4h-sc58-fp69-x3",
                    "uuid": "c6ge-fk4h-sc58-fp69-x3"
                }
            ],
            "key": "vwwx-i5fg-tlbk-4kss-qh",
            "uuid": "vwwx-i5fg-tlbk-4kss-qh",
            "keyPath": "vwwx-i5fg-tlbk-4kss-qh",
            "category": "dsGroup",
            "imgUrl": "images/data_sources/defaut_datasource.png"
        },
        {
            "name": "Sqlite",
            "children": [
                {
                    "name": "chinook",
                    "driver": "org.sqlite.JDBC",
                    "dataSourceType": "Managed DataSource",
                    "classifier": "global",
                    "type": "dynamicDataSource",
                    "data": {
                        "id": "3",
                        "type": "dynamicDataSource"
                    },
                    "permissionLevel": 5,
                    "dataSourceProvider": "tomcat",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Sqlite",
                    "keyPath": "vlj7-brfe-7048-prfe-2f/x7l2-dtpk-3xef-jn6t-bs",
                    "key": "x7l2-dtpk-3xef-jn6t-bs",
                    "uuid": "x7l2-dtpk-3xef-jn6t-bs"
                },
                {
                    "name": "sqlite std",
                    "driver": "org.sqlite.JDBC",
                    "dataSourceType": "Managed DataSource",
                    "classifier": "global",
                    "type": "dynamicDataSource",
                    "data": {
                        "id": "4",
                        "type": "dynamicDataSource"
                    },
                    "permissionLevel": 5,
                    "dataSourceProvider": "tomcat",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Sqlite",
                    "keyPath": "vlj7-brfe-7048-prfe-2f/bh5e-uf4e-gcgh-8rh9-sh",
                    "key": "bh5e-uf4e-gcgh-8rh9-sh",
                    "uuid": "bh5e-uf4e-gcgh-8rh9-sh"
                }
            ],
            "key": "vlj7-brfe-7048-prfe-2f",
            "uuid": "vlj7-brfe-7048-prfe-2f",
            "keyPath": "vlj7-brfe-7048-prfe-2f",
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
                    "dir": "1463377807724/1463377978248/Sample EFW Report",
                    "type": "sql.jdbc",
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                },
                "connId": "1",
                "dsUUID": "zbo4-j91e-0176-v9zi-ui",
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
                        "type": "sql.jdbc",
                        "baseType": "sql.jdbc",
                        "catSchemaPredicted": false,
                        "sync": false,
                        "catalog": "",
                        "schema": "HIUSER",
                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                        "connId": "7xvwn",
                        "classifier": "db.workflow",
                        "datasourceName": "SampleEFWD Sample Travel Data",
                        "dsKeyPath": "vwwx-i5fg-tlbk-4kss-qh/zbo4-j91e-0176-v9zi-ui/9cs1-5kqq-vafg-dvsb-go",
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
            "type": "sql.jdbc",
            "baseType": "sql.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "",
            "schema": "HIUSER",
            "dir": "1463377807724/1463377978248/Sample EFW Report",
            "connId": "7xvwn",
            "classifier": "db.workflow",
            "datasourceName": "SampleEFWD Sample Travel Data",
            "dsKeyPath": "vwwx-i5fg-tlbk-4kss-qh/zbo4-j91e-0176-v9zi-ui/9cs1-5kqq-vafg-dvsb-go",
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
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
            },
            "keyPath": "vwwx-i5fg-tlbk-4kss-qh/zbo4-j91e-0176-v9zi-ui/9cs1-5kqq-vafg-dvsb-go/fnz8-p6go-6g86-br34-sl",
            "key": "fnz8-p6go-6g86-br34-sl",
            "alias": "geo_cordinates",
            "uuid": "fnz8-p6go-6g86-br34-sl",
            "connId": "7xvwn",
            "dataSource": {
                "id": "1",
                "type": "sql.jdbc",
                "baseType": "sql.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "connId": "7xvwn",
                "classifier": "db.workflow",
                "datasourceName": "SampleEFWD Sample Travel Data",
                "dsKeyPath": "vwwx-i5fg-tlbk-4kss-qh/zbo4-j91e-0176-v9zi-ui/9cs1-5kqq-vafg-dvsb-go",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleEFWD Sample Travel Data",
            "category": "table",
            "nameWithConnId": "geo_cordinates_7xvwn",
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
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
            },
            "keyPath": "vwwx-i5fg-tlbk-4kss-qh/zbo4-j91e-0176-v9zi-ui/9cs1-5kqq-vafg-dvsb-go/z3ps-2gcw-m8go-bblb-7p",
            "key": "z3ps-2gcw-m8go-bblb-7p",
            "alias": "meeting_details",
            "uuid": "z3ps-2gcw-m8go-bblb-7p",
            "connId": "7xvwn",
            "dataSource": {
                "id": "1",
                "type": "sql.jdbc",
                "baseType": "sql.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "connId": "7xvwn",
                "classifier": "db.workflow",
                "datasourceName": "SampleEFWD Sample Travel Data",
                "dsKeyPath": "vwwx-i5fg-tlbk-4kss-qh/zbo4-j91e-0176-v9zi-ui/9cs1-5kqq-vafg-dvsb-go",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleEFWD Sample Travel Data",
            "category": "table",
            "nameWithConnId": "meeting_details_7xvwn",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "meeting_details"
        },
        "employee_details": {
            "id": "4e1fd245f4d13b77be423a43f01d80b2",
            "name": "employee_details",
            "data": {
                "id": "1",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
            },
            "keyPath": "vwwx-i5fg-tlbk-4kss-qh/zbo4-j91e-0176-v9zi-ui/9cs1-5kqq-vafg-dvsb-go/edys-xxk1-ci7y-r2k6-aj",
            "key": "edys-xxk1-ci7y-r2k6-aj",
            "alias": "employee_details",
            "uuid": "edys-xxk1-ci7y-r2k6-aj",
            "connId": "7xvwn",
            "dataSource": {
                "id": "1",
                "type": "sql.jdbc",
                "baseType": "sql.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "connId": "7xvwn",
                "classifier": "db.workflow",
                "datasourceName": "SampleEFWD Sample Travel Data",
                "dsKeyPath": "vwwx-i5fg-tlbk-4kss-qh/zbo4-j91e-0176-v9zi-ui/9cs1-5kqq-vafg-dvsb-go",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleEFWD Sample Travel Data",
            "category": "table",
            "nameWithConnId": "employee_details_7xvwn",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "employee_details"
        },
        "dimdate": {
            "id": "4ac5d9f68b58bd7c0d179146e46795be",
            "name": "dimdate",
            "data": {
                "id": "1",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
            },
            "keyPath": "vwwx-i5fg-tlbk-4kss-qh/zbo4-j91e-0176-v9zi-ui/9cs1-5kqq-vafg-dvsb-go/1eng-chtz-cpix-am2s-ar",
            "key": "1eng-chtz-cpix-am2s-ar",
            "alias": "dimdate",
            "uuid": "1eng-chtz-cpix-am2s-ar",
            "connId": "7xvwn",
            "dataSource": {
                "id": "1",
                "type": "sql.jdbc",
                "baseType": "sql.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "connId": "7xvwn",
                "classifier": "db.workflow",
                "datasourceName": "SampleEFWD Sample Travel Data",
                "dsKeyPath": "vwwx-i5fg-tlbk-4kss-qh/zbo4-j91e-0176-v9zi-ui/9cs1-5kqq-vafg-dvsb-go",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleEFWD Sample Travel Data",
            "category": "table",
            "nameWithConnId": "dimdate_7xvwn",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "dimdate"
        },
        "travel_details": {
            "id": "8a28627d07d04ef096d9935f12e0c7e9",
            "name": "travel_details",
            "data": {
                "id": "1",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
            },
            "keyPath": "vwwx-i5fg-tlbk-4kss-qh/zbo4-j91e-0176-v9zi-ui/9cs1-5kqq-vafg-dvsb-go/v8pl-7j4h-ytpm-h37s-ul",
            "key": "v8pl-7j4h-ytpm-h37s-ul",
            "alias": "travel_details",
            "uuid": "v8pl-7j4h-ytpm-h37s-ul",
            "connId": "7xvwn",
            "dataSource": {
                "id": "1",
                "type": "sql.jdbc",
                "baseType": "sql.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "connId": "7xvwn",
                "classifier": "db.workflow",
                "datasourceName": "SampleEFWD Sample Travel Data",
                "dsKeyPath": "vwwx-i5fg-tlbk-4kss-qh/zbo4-j91e-0176-v9zi-ui/9cs1-5kqq-vafg-dvsb-go",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleEFWD Sample Travel Data",
            "category": "table",
            "nameWithConnId": "travel_details_7xvwn",
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
                    "originalId": "9619947f-99d9-4123-a287-721a0712ec28",
                    "tableKey": "travel_details",
                    "connId": "7xvwn",
                    "duplicateIndex": 0,
                    "columnKey": "travel_id"
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
                    "originalId": "d0b8d0b2-3365-455a-b5b0-205d1f8efc48",
                    "tableKey": "travel_details",
                    "connId": "7xvwn",
                    "duplicateIndex": 0,
                    "columnKey": "travel_date"
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
                    "originalId": "7bccb5cf-3079-40c1-860d-9d867b90f3d4",
                    "tableKey": "travel_details",
                    "connId": "7xvwn",
                    "duplicateIndex": 0,
                    "columnKey": "travel_type"
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
                    "originalId": "843e40ed-2887-4e3a-a498-5ed3ed41edda",
                    "tableKey": "travel_details",
                    "connId": "7xvwn",
                    "duplicateIndex": 0,
                    "columnKey": "travel_medium"
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
                    "originalId": "63a5b488-72ac-4fe7-804a-3d9f99f0b80d",
                    "tableKey": "travel_details",
                    "connId": "7xvwn",
                    "duplicateIndex": 0,
                    "columnKey": "source_id"
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
                    "originalId": "300ac4b0-dbf1-45a7-84d8-b326d12ef030",
                    "tableKey": "travel_details",
                    "connId": "7xvwn",
                    "duplicateIndex": 0,
                    "columnKey": "source"
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
                    "originalId": "083abc21-f0c3-41f1-b318-1a61c8eeb9aa",
                    "tableKey": "travel_details",
                    "connId": "7xvwn",
                    "duplicateIndex": 0,
                    "columnKey": "destination_id"
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
                    "originalId": "f5312743-3426-4a0e-b6a4-6747e7df8943",
                    "tableKey": "travel_details",
                    "connId": "7xvwn",
                    "duplicateIndex": 0,
                    "columnKey": "destination"
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
                    "originalId": "4e782206-1e27-4aca-92a4-eb09bc5af38b",
                    "tableKey": "travel_details",
                    "connId": "7xvwn",
                    "duplicateIndex": 0,
                    "columnKey": "travel_cost"
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
                    "originalId": "22285a62-e67d-4485-9d5c-f31bad0f54fb",
                    "tableKey": "travel_details",
                    "connId": "7xvwn",
                    "duplicateIndex": 0,
                    "columnKey": "mode_of_payment"
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
                    "originalId": "ec68b9d0-b1d3-4b9e-865c-e572441e1b76",
                    "tableKey": "travel_details",
                    "connId": "7xvwn",
                    "duplicateIndex": 0,
                    "columnKey": "booking_platform"
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
                    "originalId": "79dc4de0-b840-4df3-9dc5-e62da59f9094",
                    "tableKey": "travel_details",
                    "connId": "7xvwn",
                    "duplicateIndex": 0,
                    "columnKey": "travelled_by"
                }
            },
            "columnsFetched": true,
            "duplicateIndex": 1
        },
        "travel_details_1": {
            "id": "8a28627d07d04ef096d9935f12e0c7e99",
            "name": "travel_details",
            "data": {
                "id": "1",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
            },
            "keyPath": "vwwx-i5fg-tlbk-4kss-qh/zbo4-j91e-0176-v9zi-ui/9cs1-5kqq-vafg-dvsb-go/eddq-bn1i-tab9-f15z-12",
            "key": "eddq-bn1i-tab9-f15z-12",
            "alias": "travel_details_1",
            "uuid": "eddq-bn1i-tab9-f15z-12",
            "connId": "7xvwn",
            "dataSource": {
                "id": "1",
                "type": "sql.jdbc",
                "baseType": "sql.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "connId": "7xvwn",
                "classifier": "db.workflow",
                "datasourceName": "SampleEFWD Sample Travel Data",
                "dsKeyPath": "vwwx-i5fg-tlbk-4kss-qh/zbo4-j91e-0176-v9zi-ui/9cs1-5kqq-vafg-dvsb-go",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleEFWD Sample Travel Data",
            "category": "table",
            "nameWithConnId": "travel_details_7xvwn",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "travel_details_1",
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
                    "originalId": "9619947f-99d9-4123-a287-721a0712ec28",
                    "tableKey": "travel_details_1",
                    "connId": "7xvwn",
                    "duplicateIndex": 0,
                    "columnKey": "travel_id"
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
                    "originalId": "d0b8d0b2-3365-455a-b5b0-205d1f8efc48",
                    "tableKey": "travel_details_1",
                    "connId": "7xvwn",
                    "duplicateIndex": 0,
                    "columnKey": "travel_date"
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
                    "originalId": "7bccb5cf-3079-40c1-860d-9d867b90f3d4",
                    "tableKey": "travel_details_1",
                    "connId": "7xvwn",
                    "duplicateIndex": 0,
                    "columnKey": "travel_type"
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
                    "originalId": "843e40ed-2887-4e3a-a498-5ed3ed41edda",
                    "tableKey": "travel_details_1",
                    "connId": "7xvwn",
                    "duplicateIndex": 0,
                    "columnKey": "travel_medium"
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
                    "originalId": "63a5b488-72ac-4fe7-804a-3d9f99f0b80d",
                    "tableKey": "travel_details_1",
                    "connId": "7xvwn",
                    "duplicateIndex": 0,
                    "columnKey": "source_id"
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
                    "originalId": "300ac4b0-dbf1-45a7-84d8-b326d12ef030",
                    "tableKey": "travel_details_1",
                    "connId": "7xvwn",
                    "duplicateIndex": 0,
                    "columnKey": "source"
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
                    "originalId": "083abc21-f0c3-41f1-b318-1a61c8eeb9aa",
                    "tableKey": "travel_details_1",
                    "connId": "7xvwn",
                    "duplicateIndex": 0,
                    "columnKey": "destination_id"
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
                    "originalId": "f5312743-3426-4a0e-b6a4-6747e7df8943",
                    "tableKey": "travel_details_1",
                    "connId": "7xvwn",
                    "duplicateIndex": 0,
                    "columnKey": "destination"
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
                    "originalId": "4e782206-1e27-4aca-92a4-eb09bc5af38b",
                    "tableKey": "travel_details_1",
                    "connId": "7xvwn",
                    "duplicateIndex": 0,
                    "columnKey": "travel_cost"
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
                    "originalId": "22285a62-e67d-4485-9d5c-f31bad0f54fb",
                    "tableKey": "travel_details_1",
                    "connId": "7xvwn",
                    "duplicateIndex": 0,
                    "columnKey": "mode_of_payment"
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
                    "originalId": "ec68b9d0-b1d3-4b9e-865c-e572441e1b76",
                    "tableKey": "travel_details_1",
                    "connId": "7xvwn",
                    "duplicateIndex": 0,
                    "columnKey": "booking_platform"
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
                    "originalId": "79dc4de0-b840-4df3-9dc5-e62da59f9094",
                    "tableKey": "travel_details_1",
                    "connId": "7xvwn",
                    "duplicateIndex": 0,
                    "columnKey": "travelled_by"
                }
            },
            "columnsFetched": true,
            "duplicateIndex": 0,
            "root": "travel_details",
            "duplicate": true,
            "originalId": "8a28627d07d04ef096d9935f12e0c7e9",
            "originalName": "travel_details_1"
        }
    },
    "categories": {
        "vwwx-i5fg-tlbk-4kss-qh": {
            "ds": {
                "name": "SampleEFWD Sample Travel Data",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                "dataSourceType": "Plain Jdbc DataSource",
                "classifier": "efwd",
                "permissionLevel": 2,
                "data": {
                    "id": "1",
                    "dir": "1463377807724/1463377978248/Sample EFW Report",
                    "type": "sql.jdbc",
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                }
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
        "vlj7-brfe-7048-prfe-2f": {
            "ds": {
                "name": "chinook",
                "driver": "org.sqlite.JDBC",
                "dataSourceType": "Managed DataSource",
                "classifier": "global",
                "type": "dynamicDataSource",
                "data": {
                    "id": "3",
                    "type": "dynamicDataSource"
                },
                "permissionLevel": 5,
                "dataSourceProvider": "tomcat"
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
            "id": "1",
            "type": "sql.jdbc",
            "baseType": "sql.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "",
            "schema": "HIUSER",
            "dir": "1463377807724/1463377978248/Sample EFW Report",
            "connId": "7xvwn",
            "classifier": "db.workflow",
            "datasourceName": "SampleEFWD Sample Travel Data",
            "dsKeyPath": "vwwx-i5fg-tlbk-4kss-qh/zbo4-j91e-0176-v9zi-ui/9cs1-5kqq-vafg-dvsb-go",
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
    "duplicateTableList": ['8a28627d07d04ef096d9935f12e0c7e99'],
    "unsavedViews": [],
    "saveDetails": false,
    "savedTableIds": [],
    "savedColumnIds": [],
    "joins": [],
    "mode": "create",
    "allTablesKeys": [
        "fnz8-p6go-6g86-br34-sl",
        "z3ps-2gcw-m8go-bblb-7p",
        "edys-xxk1-ci7y-r2k6-aj",
        "1eng-chtz-cpix-am2s-ar",
        "v8pl-7j4h-ytpm-h37s-ul"
    ],
    "selectedTableKeys": [
        "fnz8-p6go-6g86-br34-sl",
        "z3ps-2gcw-m8go-bblb-7p",
        "edys-xxk1-ci7y-r2k6-aj",
        "1eng-chtz-cpix-am2s-ar",
        "v8pl-7j4h-ytpm-h37s-ul"
    ],
    "metadataName": "Metadata_1",
    "activeDataSource": false,
    "metadataToEdit": false,
    "isSavingInProgress": false
}

// snapshot with duplicated column
export const storeSnapshotForSave_8 = {
    "dataFetchedFor": {
        "getDatasource": true,
        "joins": false,
        "listDataSources": true,
        "9fxi-uhua-iv3k-2972-oi": true,
        "7pp1-19iz-h5nf-48hf-0f": false,
        "lz43-8drd-y8nj-r22v-tv": false
    },
    "loadingStatus": {
        "getDatasource": true,
        "listDataSources": true,
        "9fxi-uhua-iv3k-2972-oi": true,
        "7pp1-19iz-h5nf-48hf-0f": false,
        "lz43-8drd-y8nj-r22v-tv": false
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
            "name": "Amazon Redshift",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Amazondynamodb",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Dremio",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Googlebigquery",
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
            "name": "Mysql",
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
            "name": "Amazon Redshift",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Amazondynamodb",
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
            "name": "Googlebigquery",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "driver": "com.helical.mongodb.MongoJdbcDriver",
            "databaseDialect": "himongo\t\t\t\t\t\t\t\t\t\t   ",
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
            "name": "Mysql",
            "categoryType": "supported",
            "categoryName": "Supported"
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
            "name": "Presto",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "driver": "ru.yandex.clickhouse.ClickHouseDriver",
            "databaseDialect": "clickhouse",
            "name": "Ru Yandex Clickhouse",
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
            "databaseDialect": "himongo\t\t\t\t\t\t\t\t\t\t   ",
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
            "driver": "ru.yandex.clickhouse.ClickHouseDriver",
            "databaseDialect": "clickhouse",
            "name": "Ru Yandex Clickhouse",
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
            "available": "true",
            "driver": "com.helical.mongodb.MongoJdbcDriver"
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
            "name": "SampleEFWD Sample Travel Data",
            "type": "sql.jdbc",
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "dataSourceType": "Plain Jdbc DataSource",
            "classifier": "efwd",
            "permissionLevel": 2,
            "data": {
                "id": "1",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
            }
        },
        {
            "name": "1",
            "dataSourceType": "Managed DataSource",
            "classifier": "global",
            "permissionLevel": 5,
            "data": {
                "id": "1",
                "dir": "1463377807724/1593413909773",
                "type": "global.jdbc"
            }
        },
        {
            "name": "1",
            "dataSourceType": "Managed DataSource",
            "classifier": "global",
            "permissionLevel": 5,
            "data": {
                "id": "1",
                "dir": "1463377807724/1570173811451",
                "type": "global.jdbc"
            }
        },
        {
            "name": "SampleEFWD Sample Travel Data",
            "type": "sql.jdbc",
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "dataSourceType": "Plain Jdbc DataSource",
            "classifier": "efwd",
            "permissionLevel": 2,
            "data": {
                "id": "1",
                "dir": "1463377807724/1463377978248/Sample EFW Dashboard",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
            }
        },
        {
            "name": "SampleTravelDataDerby",
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "dataSourceType": "Managed DataSource",
            "classifier": "global",
            "type": "dynamicDataSource",
            "data": {
                "id": "1",
                "type": "dynamicDataSource"
            },
            "permissionLevel": 5,
            "dataSourceProvider": "tomcat"
        },
        {
            "name": "test_SampleTravelDataDerby",
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "dataSourceType": "Managed DataSource",
            "classifier": "global",
            "type": "dynamicDataSource",
            "data": {
                "id": "2",
                "type": "dynamicDataSource"
            },
            "permissionLevel": 5,
            "dataSourceProvider": "tomcat"
        },
        {
            "name": "chinook",
            "driver": "org.sqlite.JDBC",
            "dataSourceType": "Managed DataSource",
            "classifier": "global",
            "type": "dynamicDataSource",
            "data": {
                "id": "3",
                "type": "dynamicDataSource"
            },
            "permissionLevel": 5,
            "dataSourceProvider": "tomcat"
        },
        {
            "name": "sqlite std",
            "driver": "org.sqlite.JDBC",
            "dataSourceType": "Managed DataSource",
            "classifier": "global",
            "type": "dynamicDataSource",
            "data": {
                "id": "4",
                "type": "dynamicDataSource"
            },
            "permissionLevel": 5,
            "dataSourceProvider": "tomcat"
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
                    "name": "SampleEFWD Sample Travel Data",
                    "type": "sql.jdbc",
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "dataSourceType": "Plain Jdbc DataSource",
                    "classifier": "efwd",
                    "permissionLevel": 2,
                    "data": {
                        "id": "1",
                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                        "type": "sql.jdbc",
                        "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                    },
                    "category": "dataSource",
                    "children": [
                        {
                            "name": "SQLJ",
                            "children": [],
                            "keyPath": "9fxi-uhua-iv3k-2972-oi/7pp1-19iz-h5nf-48hf-0f/gudb-yrvg-tb3x-b9de-m5",
                            "key": "gudb-yrvg-tb3x-b9de-m5",
                            "uuid": "gudb-yrvg-tb3x-b9de-m5",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "SYSFUN",
                            "children": [],
                            "keyPath": "9fxi-uhua-iv3k-2972-oi/7pp1-19iz-h5nf-48hf-0f/se9j-z8ht-csw4-ufn7-ez",
                            "key": "se9j-z8ht-csw4-ufn7-ez",
                            "uuid": "se9j-z8ht-csw4-ufn7-ez",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "SYSCAT",
                            "children": [],
                            "keyPath": "9fxi-uhua-iv3k-2972-oi/7pp1-19iz-h5nf-48hf-0f/ninb-g714-8rlh-2h5p-w7",
                            "key": "ninb-g714-8rlh-2h5p-w7",
                            "uuid": "ninb-g714-8rlh-2h5p-w7",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "HIUSER",
                            "children": [
                                {
                                    "id": "be534112989b616b194bc59c2fb25a42",
                                    "name": "geo_cordinates",
                                    "data": {
                                        "id": "1",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "type": "sql.jdbc",
                                        "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                                    },
                                    "keyPath": "9fxi-uhua-iv3k-2972-oi/7pp1-19iz-h5nf-48hf-0f/lz43-8drd-y8nj-r22v-tv/dr7t-26mr-xazs-9y6y-3q",
                                    "key": "dr7t-26mr-xazs-9y6y-3q",
                                    "alias": "geo_cordinates",
                                    "uuid": "dr7t-26mr-xazs-9y6y-3q",
                                    "connId": "3wpwo",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "sql.jdbc",
                                        "baseType": "sql.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "connId": "3wpwo",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleEFWD Sample Travel Data",
                                        "dsKeyPath": "9fxi-uhua-iv3k-2972-oi/7pp1-19iz-h5nf-48hf-0f/lz43-8drd-y8nj-r22v-tv",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleEFWD Sample Travel Data",
                                    "category": "table",
                                    "nameWithConnId": "geo_cordinates_3wpwo",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "9645c648a1c0dbeec1287aaf1e996db3",
                                    "name": "meeting_details",
                                    "data": {
                                        "id": "1",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "type": "sql.jdbc",
                                        "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                                    },
                                    "keyPath": "9fxi-uhua-iv3k-2972-oi/7pp1-19iz-h5nf-48hf-0f/lz43-8drd-y8nj-r22v-tv/3jk7-sw7y-dvjj-xt3n-t7",
                                    "key": "3jk7-sw7y-dvjj-xt3n-t7",
                                    "alias": "meeting_details",
                                    "uuid": "3jk7-sw7y-dvjj-xt3n-t7",
                                    "connId": "3wpwo",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "sql.jdbc",
                                        "baseType": "sql.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "connId": "3wpwo",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleEFWD Sample Travel Data",
                                        "dsKeyPath": "9fxi-uhua-iv3k-2972-oi/7pp1-19iz-h5nf-48hf-0f/lz43-8drd-y8nj-r22v-tv",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleEFWD Sample Travel Data",
                                    "category": "table",
                                    "nameWithConnId": "meeting_details_3wpwo",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "4e1fd245f4d13b77be423a43f01d80b2",
                                    "name": "employee_details",
                                    "data": {
                                        "id": "1",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "type": "sql.jdbc",
                                        "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                                    },
                                    "keyPath": "9fxi-uhua-iv3k-2972-oi/7pp1-19iz-h5nf-48hf-0f/lz43-8drd-y8nj-r22v-tv/9tke-nux4-t1qa-pfgj-f7",
                                    "key": "9tke-nux4-t1qa-pfgj-f7",
                                    "alias": "employee_details",
                                    "uuid": "9tke-nux4-t1qa-pfgj-f7",
                                    "connId": "3wpwo",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "sql.jdbc",
                                        "baseType": "sql.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "connId": "3wpwo",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleEFWD Sample Travel Data",
                                        "dsKeyPath": "9fxi-uhua-iv3k-2972-oi/7pp1-19iz-h5nf-48hf-0f/lz43-8drd-y8nj-r22v-tv",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleEFWD Sample Travel Data",
                                    "category": "table",
                                    "nameWithConnId": "employee_details_3wpwo",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "4ac5d9f68b58bd7c0d179146e46795be",
                                    "name": "dimdate",
                                    "data": {
                                        "id": "1",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "type": "sql.jdbc",
                                        "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                                    },
                                    "keyPath": "9fxi-uhua-iv3k-2972-oi/7pp1-19iz-h5nf-48hf-0f/lz43-8drd-y8nj-r22v-tv/bwad-59hn-dcmq-2031-3s",
                                    "key": "bwad-59hn-dcmq-2031-3s",
                                    "alias": "dimdate",
                                    "uuid": "bwad-59hn-dcmq-2031-3s",
                                    "connId": "3wpwo",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "sql.jdbc",
                                        "baseType": "sql.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "connId": "3wpwo",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleEFWD Sample Travel Data",
                                        "dsKeyPath": "9fxi-uhua-iv3k-2972-oi/7pp1-19iz-h5nf-48hf-0f/lz43-8drd-y8nj-r22v-tv",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleEFWD Sample Travel Data",
                                    "category": "table",
                                    "nameWithConnId": "dimdate_3wpwo",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "8a28627d07d04ef096d9935f12e0c7e9",
                                    "name": "travel_details",
                                    "data": {
                                        "id": "1",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "type": "sql.jdbc",
                                        "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                                    },
                                    "keyPath": "9fxi-uhua-iv3k-2972-oi/7pp1-19iz-h5nf-48hf-0f/lz43-8drd-y8nj-r22v-tv/bx5s-hu6l-5yi0-of56-n2",
                                    "key": "bx5s-hu6l-5yi0-of56-n2",
                                    "alias": "travel_details",
                                    "uuid": "bx5s-hu6l-5yi0-of56-n2",
                                    "connId": "3wpwo",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "sql.jdbc",
                                        "baseType": "sql.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "connId": "3wpwo",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleEFWD Sample Travel Data",
                                        "dsKeyPath": "9fxi-uhua-iv3k-2972-oi/7pp1-19iz-h5nf-48hf-0f/lz43-8drd-y8nj-r22v-tv",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleEFWD Sample Travel Data",
                                    "category": "table",
                                    "nameWithConnId": "travel_details_3wpwo",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                }
                            ],
                            "keyPath": "9fxi-uhua-iv3k-2972-oi/7pp1-19iz-h5nf-48hf-0f/lz43-8drd-y8nj-r22v-tv",
                            "key": "lz43-8drd-y8nj-r22v-tv",
                            "uuid": "lz43-8drd-y8nj-r22v-tv",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data",
                            "fetched": true
                        },
                        {
                            "name": "SYSCS_DIAG",
                            "children": [],
                            "keyPath": "9fxi-uhua-iv3k-2972-oi/7pp1-19iz-h5nf-48hf-0f/hrdp-qhao-w990-dtmo-zn",
                            "key": "hrdp-qhao-w990-dtmo-zn",
                            "uuid": "hrdp-qhao-w990-dtmo-zn",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "SYSCS_UTIL",
                            "children": [],
                            "keyPath": "9fxi-uhua-iv3k-2972-oi/7pp1-19iz-h5nf-48hf-0f/6o7l-nwm5-xy9o-6r23-pm",
                            "key": "6o7l-nwm5-xy9o-6r23-pm",
                            "uuid": "6o7l-nwm5-xy9o-6r23-pm",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "SYSIBM",
                            "children": [],
                            "keyPath": "9fxi-uhua-iv3k-2972-oi/7pp1-19iz-h5nf-48hf-0f/upk4-tu2v-gwid-nrrf-e8",
                            "key": "upk4-tu2v-gwid-nrrf-e8",
                            "uuid": "upk4-tu2v-gwid-nrrf-e8",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "APP",
                            "children": [],
                            "keyPath": "9fxi-uhua-iv3k-2972-oi/7pp1-19iz-h5nf-48hf-0f/4mom-tc9g-0y6h-fxpo-gj",
                            "key": "4mom-tc9g-0y6h-fxpo-gj",
                            "uuid": "4mom-tc9g-0y6h-fxpo-gj",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "NULLID",
                            "children": [],
                            "keyPath": "9fxi-uhua-iv3k-2972-oi/7pp1-19iz-h5nf-48hf-0f/kkot-5u2j-uf43-jorm-tw",
                            "key": "kkot-5u2j-uf43-jorm-tw",
                            "uuid": "kkot-5u2j-uf43-jorm-tw",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "SYSPROC",
                            "children": [],
                            "keyPath": "9fxi-uhua-iv3k-2972-oi/7pp1-19iz-h5nf-48hf-0f/9iz6-pq11-apsq-cart-pi",
                            "key": "9iz6-pq11-apsq-cart-pi",
                            "uuid": "9iz6-pq11-apsq-cart-pi",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "SYS",
                            "children": [],
                            "keyPath": "9fxi-uhua-iv3k-2972-oi/7pp1-19iz-h5nf-48hf-0f/w8av-1bq9-5o2c-zd0r-hw",
                            "key": "w8av-1bq9-5o2c-zd0r-hw",
                            "uuid": "w8av-1bq9-5o2c-zd0r-hw",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "SYSSTAT",
                            "children": [],
                            "keyPath": "9fxi-uhua-iv3k-2972-oi/7pp1-19iz-h5nf-48hf-0f/ox76-3jc9-wf6v-n2kb-9i",
                            "key": "ox76-3jc9-wf6v-n2kb-9i",
                            "uuid": "ox76-3jc9-wf6v-n2kb-9i",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        }
                    ],
                    "driverType": "Derby",
                    "keyPath": "9fxi-uhua-iv3k-2972-oi/7pp1-19iz-h5nf-48hf-0f",
                    "key": "7pp1-19iz-h5nf-48hf-0f",
                    "uuid": "7pp1-19iz-h5nf-48hf-0f",
                    "fetched": true
                },
                {
                    "name": "SampleEFWD Sample Travel Data",
                    "type": "sql.jdbc",
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "dataSourceType": "Plain Jdbc DataSource",
                    "classifier": "efwd",
                    "permissionLevel": 2,
                    "data": {
                        "id": "1",
                        "dir": "1463377807724/1463377978248/Sample EFW Dashboard",
                        "type": "sql.jdbc",
                        "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                    },
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "9fxi-uhua-iv3k-2972-oi/rw45-1pr0-ljvv-sjqx-re",
                    "key": "rw45-1pr0-ljvv-sjqx-re",
                    "uuid": "rw45-1pr0-ljvv-sjqx-re"
                },
                {
                    "name": "SampleTravelDataDerby",
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "dataSourceType": "Managed DataSource",
                    "classifier": "global",
                    "type": "dynamicDataSource",
                    "data": {
                        "id": "1",
                        "type": "dynamicDataSource"
                    },
                    "permissionLevel": 5,
                    "dataSourceProvider": "tomcat",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "9fxi-uhua-iv3k-2972-oi/0hru-adjf-jd6t-ikff-nk",
                    "key": "0hru-adjf-jd6t-ikff-nk",
                    "uuid": "0hru-adjf-jd6t-ikff-nk"
                },
                {
                    "name": "test_SampleTravelDataDerby",
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "dataSourceType": "Managed DataSource",
                    "classifier": "global",
                    "type": "dynamicDataSource",
                    "data": {
                        "id": "2",
                        "type": "dynamicDataSource"
                    },
                    "permissionLevel": 5,
                    "dataSourceProvider": "tomcat",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "9fxi-uhua-iv3k-2972-oi/a2dc-zsm7-0qlo-ua4l-b3",
                    "key": "a2dc-zsm7-0qlo-ua4l-b3",
                    "uuid": "a2dc-zsm7-0qlo-ua4l-b3"
                }
            ],
            "key": "9fxi-uhua-iv3k-2972-oi",
            "uuid": "9fxi-uhua-iv3k-2972-oi",
            "keyPath": "9fxi-uhua-iv3k-2972-oi",
            "category": "dsGroup",
            "imgUrl": "images/data_sources/defaut_datasource.png"
        },
        {
            "name": "Sqlite",
            "children": [
                {
                    "name": "chinook",
                    "driver": "org.sqlite.JDBC",
                    "dataSourceType": "Managed DataSource",
                    "classifier": "global",
                    "type": "dynamicDataSource",
                    "data": {
                        "id": "3",
                        "type": "dynamicDataSource"
                    },
                    "permissionLevel": 5,
                    "dataSourceProvider": "tomcat",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Sqlite",
                    "keyPath": "t0k3-a31z-yd9a-lkvc-ij/2frn-4qem-2yjk-y7f6-e6",
                    "key": "2frn-4qem-2yjk-y7f6-e6",
                    "uuid": "2frn-4qem-2yjk-y7f6-e6"
                },
                {
                    "name": "sqlite std",
                    "driver": "org.sqlite.JDBC",
                    "dataSourceType": "Managed DataSource",
                    "classifier": "global",
                    "type": "dynamicDataSource",
                    "data": {
                        "id": "4",
                        "type": "dynamicDataSource"
                    },
                    "permissionLevel": 5,
                    "dataSourceProvider": "tomcat",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Sqlite",
                    "keyPath": "t0k3-a31z-yd9a-lkvc-ij/zryy-bkfi-olvi-gxeb-do",
                    "key": "zryy-bkfi-olvi-gxeb-do",
                    "uuid": "zryy-bkfi-olvi-gxeb-do"
                }
            ],
            "key": "t0k3-a31z-yd9a-lkvc-ij",
            "uuid": "t0k3-a31z-yd9a-lkvc-ij",
            "keyPath": "t0k3-a31z-yd9a-lkvc-ij",
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
                    "dir": "1463377807724/1463377978248/Sample EFW Report",
                    "type": "sql.jdbc",
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                },
                "connId": "1",
                "dsUUID": "7pp1-19iz-h5nf-48hf-0f",
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
                        "type": "sql.jdbc",
                        "baseType": "sql.jdbc",
                        "catSchemaPredicted": false,
                        "sync": false,
                        "catalog": "",
                        "schema": "HIUSER",
                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                        "connId": "3wpwo",
                        "classifier": "db.workflow",
                        "datasourceName": "SampleEFWD Sample Travel Data",
                        "dsKeyPath": "9fxi-uhua-iv3k-2972-oi/7pp1-19iz-h5nf-48hf-0f/lz43-8drd-y8nj-r22v-tv",
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
            "type": "sql.jdbc",
            "baseType": "sql.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "",
            "schema": "HIUSER",
            "dir": "1463377807724/1463377978248/Sample EFW Report",
            "connId": "3wpwo",
            "classifier": "db.workflow",
            "datasourceName": "SampleEFWD Sample Travel Data",
            "dsKeyPath": "9fxi-uhua-iv3k-2972-oi/7pp1-19iz-h5nf-48hf-0f/lz43-8drd-y8nj-r22v-tv",
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
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
            },
            "keyPath": "9fxi-uhua-iv3k-2972-oi/7pp1-19iz-h5nf-48hf-0f/lz43-8drd-y8nj-r22v-tv/dr7t-26mr-xazs-9y6y-3q",
            "key": "dr7t-26mr-xazs-9y6y-3q",
            "alias": "geo_cordinates",
            "uuid": "dr7t-26mr-xazs-9y6y-3q",
            "connId": "3wpwo",
            "dataSource": {
                "id": "1",
                "type": "sql.jdbc",
                "baseType": "sql.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "connId": "3wpwo",
                "classifier": "db.workflow",
                "datasourceName": "SampleEFWD Sample Travel Data",
                "dsKeyPath": "9fxi-uhua-iv3k-2972-oi/7pp1-19iz-h5nf-48hf-0f/lz43-8drd-y8nj-r22v-tv",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleEFWD Sample Travel Data",
            "category": "table",
            "nameWithConnId": "geo_cordinates_3wpwo",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "geo_cordinates",
            "columns": {
                "location_id": {
                    "alias": "location_id",
                    "fullyQualifiedColumn": "geo_cordinates.location_id",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    },
                    "category": "column",
                    "originalName": "location_id",
                    "originalId": "86c47bfb-af10-44f1-9a24-01907a6c1628",
                    "tableKey": "geo_cordinates",
                    "connId": "3wpwo",
                    "duplicateIndex": 0,
                    "columnKey": "location_id"
                },
                "location": {
                    "alias": "location",
                    "fullyQualifiedColumn": "geo_cordinates.location",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "originalName": "location",
                    "originalId": "850bf601-b22c-46ad-b1ca-91b07b74ddf6",
                    "tableKey": "geo_cordinates",
                    "connId": "3wpwo",
                    "duplicateIndex": 0,
                    "columnKey": "location"
                },
                "latitude": {
                    "alias": "latitude",
                    "fullyQualifiedColumn": "geo_cordinates.latitude",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Double": "numeric"
                    },
                    "category": "column",
                    "originalName": "latitude",
                    "originalId": "05f0a719-1f8a-499b-be6c-46ea16737834",
                    "tableKey": "geo_cordinates",
                    "connId": "3wpwo",
                    "duplicateIndex": 1,
                    "columnKey": "latitude"
                },
                "longitude": {
                    "alias": "longitude",
                    "fullyQualifiedColumn": "geo_cordinates.longitude",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Double": "numeric"
                    },
                    "category": "column",
                    "originalName": "longitude",
                    "originalId": "d2efcf14-2e65-445e-a692-1ba1b6d39d5e",
                    "tableKey": "geo_cordinates",
                    "connId": "3wpwo",
                    "duplicateIndex": 1,
                    "columnKey": "longitude"
                },
                "longitude_1": {
                    "alias": "longitude_1",
                    "fullyQualifiedColumn": "geo_cordinates.longitude",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Double": "numeric"
                    },
                    "category": "column",
                    "originalName": "longitude_1",
                    "originalId": "d2efcf14-2e65-445e-a692-1ba1b6d39d5e",
                    "tableKey": "geo_cordinates",
                    "connId": "3wpwo",
                    "duplicateIndex": 0,
                    "columnKey": "longitude_1",
                    "name": "longitude",
                    "keyPath": "9fxi-uhua-iv3k-2972-oi/7pp1-19iz-h5nf-48hf-0f/lz43-8drd-y8nj-r22v-tv/dr7t-26mr-xazs-9y6y-3q/y97p-oom4-c46k-8h0t-33",
                    "key": "y97p-oom4-c46k-8h0t-33",
                    "uuid": "y26x-i3za-3ega-kajw-uk",
                    "tableName": "geo_cordinates",
                    "duplicate": true
                },
                "latitude_1": {
                    "alias": "latitude_1",
                    "fullyQualifiedColumn": "geo_cordinates.latitude",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Double": "numeric"
                    },
                    "category": "column",
                    "originalName": "latitude_1",
                    "originalId": "05f0a719-1f8a-499b-be6c-46ea16737834",
                    "tableKey": "geo_cordinates",
                    "connId": "3wpwo",
                    "duplicateIndex": 0,
                    "columnKey": "latitude_1",
                    "name": "latitude",
                    "keyPath": "9fxi-uhua-iv3k-2972-oi/7pp1-19iz-h5nf-48hf-0f/lz43-8drd-y8nj-r22v-tv/dr7t-26mr-xazs-9y6y-3q/cr1l-yrw4-1u17-j2j2-3q",
                    "key": "cr1l-yrw4-1u17-j2j2-3q",
                    "uuid": "1rct-ygnp-5v8k-4gu1-xr",
                    "tableName": "geo_cordinates",
                    "duplicate": true
                }
            },
            "columnsFetched": true
        },
        "meeting_details": {
            "id": "9645c648a1c0dbeec1287aaf1e996db3",
            "name": "meeting_details",
            "data": {
                "id": "1",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
            },
            "keyPath": "9fxi-uhua-iv3k-2972-oi/7pp1-19iz-h5nf-48hf-0f/lz43-8drd-y8nj-r22v-tv/3jk7-sw7y-dvjj-xt3n-t7",
            "key": "3jk7-sw7y-dvjj-xt3n-t7",
            "alias": "meeting_details",
            "uuid": "3jk7-sw7y-dvjj-xt3n-t7",
            "connId": "3wpwo",
            "dataSource": {
                "id": "1",
                "type": "sql.jdbc",
                "baseType": "sql.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "connId": "3wpwo",
                "classifier": "db.workflow",
                "datasourceName": "SampleEFWD Sample Travel Data",
                "dsKeyPath": "9fxi-uhua-iv3k-2972-oi/7pp1-19iz-h5nf-48hf-0f/lz43-8drd-y8nj-r22v-tv",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleEFWD Sample Travel Data",
            "category": "table",
            "nameWithConnId": "meeting_details_3wpwo",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "meeting_details"
        },
        "employee_details": {
            "id": "4e1fd245f4d13b77be423a43f01d80b2",
            "name": "employee_details",
            "data": {
                "id": "1",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
            },
            "keyPath": "9fxi-uhua-iv3k-2972-oi/7pp1-19iz-h5nf-48hf-0f/lz43-8drd-y8nj-r22v-tv/9tke-nux4-t1qa-pfgj-f7",
            "key": "9tke-nux4-t1qa-pfgj-f7",
            "alias": "employee_details",
            "uuid": "9tke-nux4-t1qa-pfgj-f7",
            "connId": "3wpwo",
            "dataSource": {
                "id": "1",
                "type": "sql.jdbc",
                "baseType": "sql.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "connId": "3wpwo",
                "classifier": "db.workflow",
                "datasourceName": "SampleEFWD Sample Travel Data",
                "dsKeyPath": "9fxi-uhua-iv3k-2972-oi/7pp1-19iz-h5nf-48hf-0f/lz43-8drd-y8nj-r22v-tv",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleEFWD Sample Travel Data",
            "category": "table",
            "nameWithConnId": "employee_details_3wpwo",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "employee_details"
        },
        "dimdate": {
            "id": "4ac5d9f68b58bd7c0d179146e46795be",
            "name": "dimdate",
            "data": {
                "id": "1",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
            },
            "keyPath": "9fxi-uhua-iv3k-2972-oi/7pp1-19iz-h5nf-48hf-0f/lz43-8drd-y8nj-r22v-tv/bwad-59hn-dcmq-2031-3s",
            "key": "bwad-59hn-dcmq-2031-3s",
            "alias": "dimdate",
            "uuid": "bwad-59hn-dcmq-2031-3s",
            "connId": "3wpwo",
            "dataSource": {
                "id": "1",
                "type": "sql.jdbc",
                "baseType": "sql.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "connId": "3wpwo",
                "classifier": "db.workflow",
                "datasourceName": "SampleEFWD Sample Travel Data",
                "dsKeyPath": "9fxi-uhua-iv3k-2972-oi/7pp1-19iz-h5nf-48hf-0f/lz43-8drd-y8nj-r22v-tv",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleEFWD Sample Travel Data",
            "category": "table",
            "nameWithConnId": "dimdate_3wpwo",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "dimdate"
        },
        "travel_details": {
            "id": "8a28627d07d04ef096d9935f12e0c7e9",
            "name": "travel_details",
            "data": {
                "id": "1",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
            },
            "keyPath": "9fxi-uhua-iv3k-2972-oi/7pp1-19iz-h5nf-48hf-0f/lz43-8drd-y8nj-r22v-tv/bx5s-hu6l-5yi0-of56-n2",
            "key": "bx5s-hu6l-5yi0-of56-n2",
            "alias": "travel_details",
            "uuid": "bx5s-hu6l-5yi0-of56-n2",
            "connId": "3wpwo",
            "dataSource": {
                "id": "1",
                "type": "sql.jdbc",
                "baseType": "sql.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "connId": "3wpwo",
                "classifier": "db.workflow",
                "datasourceName": "SampleEFWD Sample Travel Data",
                "dsKeyPath": "9fxi-uhua-iv3k-2972-oi/7pp1-19iz-h5nf-48hf-0f/lz43-8drd-y8nj-r22v-tv",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleEFWD Sample Travel Data",
            "category": "table",
            "nameWithConnId": "travel_details_3wpwo",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "travel_details"
        }
    },
    "categories": {
        "9fxi-uhua-iv3k-2972-oi": {
            "ds": {
                "name": "SampleEFWD Sample Travel Data",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                "dataSourceType": "Plain Jdbc DataSource",
                "classifier": "efwd",
                "permissionLevel": 2,
                "data": {
                    "id": "1",
                    "dir": "1463377807724/1463377978248/Sample EFW Report",
                    "type": "sql.jdbc",
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                }
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
        "t0k3-a31z-yd9a-lkvc-ij": {
            "ds": {
                "name": "chinook",
                "driver": "org.sqlite.JDBC",
                "dataSourceType": "Managed DataSource",
                "classifier": "global",
                "type": "dynamicDataSource",
                "data": {
                    "id": "3",
                    "type": "dynamicDataSource"
                },
                "permissionLevel": 5,
                "dataSourceProvider": "tomcat"
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
            "id": "1",
            "type": "sql.jdbc",
            "baseType": "sql.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "",
            "schema": "HIUSER",
            "dir": "1463377807724/1463377978248/Sample EFW Report",
            "connId": "3wpwo",
            "classifier": "db.workflow",
            "datasourceName": "SampleEFWD Sample Travel Data",
            "dsKeyPath": "9fxi-uhua-iv3k-2972-oi/7pp1-19iz-h5nf-48hf-0f/lz43-8drd-y8nj-r22v-tv",
            "driverType": "Derby",
            "database": "HIUSER"
        }
    ],
    "changeDSList": {},
    "changedTables": [],
    "changedColumns": [
        {
            "alias": "longitude_1",
            "columnId": "d2efcf14-2e65-445e-a692-1ba1b6d39d5e",
            "connId": "3wpwo",
            "tableId": "be534112989b616b194bc59c2fb25a42",
            "duplicate": true
        },
        {
            "alias": "latitude_1",
            "columnId": "05f0a719-1f8a-499b-be6c-46ea16737834",
            "connId": "3wpwo",
            "tableId": "be534112989b616b194bc59c2fb25a42",
            "duplicate": true
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
    "joins": [],
    "mode": "create",
    "allTablesKeys": [
        "dr7t-26mr-xazs-9y6y-3q",
        "3jk7-sw7y-dvjj-xt3n-t7",
        "9tke-nux4-t1qa-pfgj-f7",
        "bwad-59hn-dcmq-2031-3s",
        "bx5s-hu6l-5yi0-of56-n2"
    ],
    "selectedTableKeys": [
        "dr7t-26mr-xazs-9y6y-3q",
        "3jk7-sw7y-dvjj-xt3n-t7",
        "9tke-nux4-t1qa-pfgj-f7",
        "bwad-59hn-dcmq-2031-3s",
        "bx5s-hu6l-5yi0-of56-n2"
    ],
    "metadataName": "Metadata_1",
    "activeDataSource": false,
    "metadataToEdit": false,
    "isSavingInProgress": false
}

//snapshot with alias column and aliased table
export const storeSnapshotForSave_9 = {
    "dataFetchedFor": {
        "getDatasource": true,
        "joins": false,
        "listDataSources": true,
        "m884-hquy-0v6o-wyrk-rn": true,
        "12eo-as55-6e7a-lixe-dg": false,
        "ezjy-1rp4-znqh-v89j-s8": false
    },
    "loadingStatus": {
        "getDatasource": true,
        "listDataSources": true,
        "m884-hquy-0v6o-wyrk-rn": true,
        "12eo-as55-6e7a-lixe-dg": false,
        "ezjy-1rp4-znqh-v89j-s8": false
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
            "name": "Amazon Redshift",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Amazondynamodb",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Dremio",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Googlebigquery",
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
            "name": "Mysql",
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
            "name": "Amazon Redshift",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Amazondynamodb",
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
            "name": "Googlebigquery",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "driver": "com.helical.mongodb.MongoJdbcDriver",
            "databaseDialect": "himongo\t\t\t\t\t\t\t\t\t\t   ",
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
            "name": "Mysql",
            "categoryType": "supported",
            "categoryName": "Supported"
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
            "name": "Presto",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "driver": "ru.yandex.clickhouse.ClickHouseDriver",
            "databaseDialect": "clickhouse",
            "name": "Ru Yandex Clickhouse",
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
            "databaseDialect": "himongo\t\t\t\t\t\t\t\t\t\t   ",
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
            "driver": "ru.yandex.clickhouse.ClickHouseDriver",
            "databaseDialect": "clickhouse",
            "name": "Ru Yandex Clickhouse",
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
            "available": "true",
            "driver": "com.helical.mongodb.MongoJdbcDriver"
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
            "name": "SampleEFWD Sample Travel Data",
            "type": "sql.jdbc",
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "dataSourceType": "Plain Jdbc DataSource",
            "classifier": "efwd",
            "permissionLevel": 2,
            "data": {
                "id": "1",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
            }
        },
        {
            "name": "1",
            "dataSourceType": "Managed DataSource",
            "classifier": "global",
            "permissionLevel": 5,
            "data": {
                "id": "1",
                "dir": "1463377807724/1593413909773",
                "type": "global.jdbc"
            }
        },
        {
            "name": "1",
            "dataSourceType": "Managed DataSource",
            "classifier": "global",
            "permissionLevel": 5,
            "data": {
                "id": "1",
                "dir": "1463377807724/1570173811451",
                "type": "global.jdbc"
            }
        },
        {
            "name": "SampleEFWD Sample Travel Data",
            "type": "sql.jdbc",
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "dataSourceType": "Plain Jdbc DataSource",
            "classifier": "efwd",
            "permissionLevel": 2,
            "data": {
                "id": "1",
                "dir": "1463377807724/1463377978248/Sample EFW Dashboard",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
            }
        },
        {
            "name": "SampleTravelDataDerby",
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "dataSourceType": "Managed DataSource",
            "classifier": "global",
            "type": "dynamicDataSource",
            "data": {
                "id": "1",
                "type": "dynamicDataSource"
            },
            "permissionLevel": 5,
            "dataSourceProvider": "tomcat"
        },
        {
            "name": "test_SampleTravelDataDerby",
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "dataSourceType": "Managed DataSource",
            "classifier": "global",
            "type": "dynamicDataSource",
            "data": {
                "id": "2",
                "type": "dynamicDataSource"
            },
            "permissionLevel": 5,
            "dataSourceProvider": "tomcat"
        },
        {
            "name": "chinook",
            "driver": "org.sqlite.JDBC",
            "dataSourceType": "Managed DataSource",
            "classifier": "global",
            "type": "dynamicDataSource",
            "data": {
                "id": "3",
                "type": "dynamicDataSource"
            },
            "permissionLevel": 5,
            "dataSourceProvider": "tomcat"
        },
        {
            "name": "sqlite std",
            "driver": "org.sqlite.JDBC",
            "dataSourceType": "Managed DataSource",
            "classifier": "global",
            "type": "dynamicDataSource",
            "data": {
                "id": "4",
                "type": "dynamicDataSource"
            },
            "permissionLevel": 5,
            "dataSourceProvider": "tomcat"
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
                    "name": "SampleEFWD Sample Travel Data",
                    "type": "sql.jdbc",
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "dataSourceType": "Plain Jdbc DataSource",
                    "classifier": "efwd",
                    "permissionLevel": 2,
                    "data": {
                        "id": "1",
                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                        "type": "sql.jdbc",
                        "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                    },
                    "category": "dataSource",
                    "children": [
                        {
                            "name": "SQLJ",
                            "children": [],
                            "keyPath": "m884-hquy-0v6o-wyrk-rn/12eo-as55-6e7a-lixe-dg/5e5b-0zjh-iypm-c3s6-4v",
                            "key": "5e5b-0zjh-iypm-c3s6-4v",
                            "uuid": "5e5b-0zjh-iypm-c3s6-4v",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "SYSFUN",
                            "children": [],
                            "keyPath": "m884-hquy-0v6o-wyrk-rn/12eo-as55-6e7a-lixe-dg/mttt-gro1-nx70-i4hj-ng",
                            "key": "mttt-gro1-nx70-i4hj-ng",
                            "uuid": "mttt-gro1-nx70-i4hj-ng",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "SYSCAT",
                            "children": [],
                            "keyPath": "m884-hquy-0v6o-wyrk-rn/12eo-as55-6e7a-lixe-dg/h8p1-rvu0-4t0q-o70u-08",
                            "key": "h8p1-rvu0-4t0q-o70u-08",
                            "uuid": "h8p1-rvu0-4t0q-o70u-08",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "HIUSER",
                            "children": [
                                {
                                    "id": "be534112989b616b194bc59c2fb25a42",
                                    "name": "geo_cordinates",
                                    "data": {
                                        "id": "1",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "type": "sql.jdbc",
                                        "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                                    },
                                    "keyPath": "m884-hquy-0v6o-wyrk-rn/12eo-as55-6e7a-lixe-dg/ezjy-1rp4-znqh-v89j-s8/neja-aqlx-mw4s-94s8-5e",
                                    "key": "neja-aqlx-mw4s-94s8-5e",
                                    "alias": "geo_cordinates",
                                    "uuid": "neja-aqlx-mw4s-94s8-5e",
                                    "connId": "aaonk",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "sql.jdbc",
                                        "baseType": "sql.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "connId": "aaonk",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleEFWD Sample Travel Data",
                                        "dsKeyPath": "m884-hquy-0v6o-wyrk-rn/12eo-as55-6e7a-lixe-dg/ezjy-1rp4-znqh-v89j-s8",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleEFWD Sample Travel Data",
                                    "category": "table",
                                    "nameWithConnId": "geo_cordinates_aaonk",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "9645c648a1c0dbeec1287aaf1e996db3",
                                    "name": "meeting_details",
                                    "data": {
                                        "id": "1",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "type": "sql.jdbc",
                                        "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                                    },
                                    "keyPath": "m884-hquy-0v6o-wyrk-rn/12eo-as55-6e7a-lixe-dg/ezjy-1rp4-znqh-v89j-s8/hl4e-qr0q-e9m8-r77p-bq",
                                    "key": "hl4e-qr0q-e9m8-r77p-bq",
                                    "alias": "meeting_details",
                                    "uuid": "hl4e-qr0q-e9m8-r77p-bq",
                                    "connId": "aaonk",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "sql.jdbc",
                                        "baseType": "sql.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "connId": "aaonk",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleEFWD Sample Travel Data",
                                        "dsKeyPath": "m884-hquy-0v6o-wyrk-rn/12eo-as55-6e7a-lixe-dg/ezjy-1rp4-znqh-v89j-s8",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleEFWD Sample Travel Data",
                                    "category": "table",
                                    "nameWithConnId": "meeting_details_aaonk",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "4e1fd245f4d13b77be423a43f01d80b2",
                                    "name": "employee_details",
                                    "data": {
                                        "id": "1",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "type": "sql.jdbc",
                                        "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                                    },
                                    "keyPath": "m884-hquy-0v6o-wyrk-rn/12eo-as55-6e7a-lixe-dg/ezjy-1rp4-znqh-v89j-s8/rysf-m6b8-dp4f-rg56-u4",
                                    "key": "rysf-m6b8-dp4f-rg56-u4",
                                    "alias": "employee_details",
                                    "uuid": "rysf-m6b8-dp4f-rg56-u4",
                                    "connId": "aaonk",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "sql.jdbc",
                                        "baseType": "sql.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "connId": "aaonk",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleEFWD Sample Travel Data",
                                        "dsKeyPath": "m884-hquy-0v6o-wyrk-rn/12eo-as55-6e7a-lixe-dg/ezjy-1rp4-znqh-v89j-s8",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleEFWD Sample Travel Data",
                                    "category": "table",
                                    "nameWithConnId": "employee_details_aaonk",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "4ac5d9f68b58bd7c0d179146e46795be",
                                    "name": "dimdate",
                                    "data": {
                                        "id": "1",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "type": "sql.jdbc",
                                        "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                                    },
                                    "keyPath": "m884-hquy-0v6o-wyrk-rn/12eo-as55-6e7a-lixe-dg/ezjy-1rp4-znqh-v89j-s8/6z7h-7hyl-tagf-1alm-3w",
                                    "key": "6z7h-7hyl-tagf-1alm-3w",
                                    "alias": "dimdate",
                                    "uuid": "6z7h-7hyl-tagf-1alm-3w",
                                    "connId": "aaonk",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "sql.jdbc",
                                        "baseType": "sql.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "connId": "aaonk",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleEFWD Sample Travel Data",
                                        "dsKeyPath": "m884-hquy-0v6o-wyrk-rn/12eo-as55-6e7a-lixe-dg/ezjy-1rp4-znqh-v89j-s8",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleEFWD Sample Travel Data",
                                    "category": "table",
                                    "nameWithConnId": "dimdate_aaonk",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "8a28627d07d04ef096d9935f12e0c7e9",
                                    "name": "travel_details",
                                    "data": {
                                        "id": "1",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "type": "sql.jdbc",
                                        "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                                    },
                                    "keyPath": "m884-hquy-0v6o-wyrk-rn/12eo-as55-6e7a-lixe-dg/ezjy-1rp4-znqh-v89j-s8/087v-ofb5-ooqa-c2t6-wg",
                                    "key": "087v-ofb5-ooqa-c2t6-wg",
                                    "alias": "travel_details",
                                    "uuid": "087v-ofb5-ooqa-c2t6-wg",
                                    "connId": "aaonk",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "sql.jdbc",
                                        "baseType": "sql.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "connId": "aaonk",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleEFWD Sample Travel Data",
                                        "dsKeyPath": "m884-hquy-0v6o-wyrk-rn/12eo-as55-6e7a-lixe-dg/ezjy-1rp4-znqh-v89j-s8",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleEFWD Sample Travel Data",
                                    "category": "table",
                                    "nameWithConnId": "travel_details_aaonk",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                }
                            ],
                            "keyPath": "m884-hquy-0v6o-wyrk-rn/12eo-as55-6e7a-lixe-dg/ezjy-1rp4-znqh-v89j-s8",
                            "key": "ezjy-1rp4-znqh-v89j-s8",
                            "uuid": "ezjy-1rp4-znqh-v89j-s8",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data",
                            "fetched": true
                        },
                        {
                            "name": "SYSCS_DIAG",
                            "children": [],
                            "keyPath": "m884-hquy-0v6o-wyrk-rn/12eo-as55-6e7a-lixe-dg/2c1u-3yzs-b7b9-j1e0-fx",
                            "key": "2c1u-3yzs-b7b9-j1e0-fx",
                            "uuid": "2c1u-3yzs-b7b9-j1e0-fx",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "SYSCS_UTIL",
                            "children": [],
                            "keyPath": "m884-hquy-0v6o-wyrk-rn/12eo-as55-6e7a-lixe-dg/64a8-r9ry-h33p-y1oi-7t",
                            "key": "64a8-r9ry-h33p-y1oi-7t",
                            "uuid": "64a8-r9ry-h33p-y1oi-7t",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "SYSIBM",
                            "children": [],
                            "keyPath": "m884-hquy-0v6o-wyrk-rn/12eo-as55-6e7a-lixe-dg/lac9-ahpt-bm6p-kfxv-b9",
                            "key": "lac9-ahpt-bm6p-kfxv-b9",
                            "uuid": "lac9-ahpt-bm6p-kfxv-b9",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "APP",
                            "children": [],
                            "keyPath": "m884-hquy-0v6o-wyrk-rn/12eo-as55-6e7a-lixe-dg/oqhh-djok-mwne-ig6y-lo",
                            "key": "oqhh-djok-mwne-ig6y-lo",
                            "uuid": "oqhh-djok-mwne-ig6y-lo",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "NULLID",
                            "children": [],
                            "keyPath": "m884-hquy-0v6o-wyrk-rn/12eo-as55-6e7a-lixe-dg/g6ul-8qmt-14ad-z6m5-md",
                            "key": "g6ul-8qmt-14ad-z6m5-md",
                            "uuid": "g6ul-8qmt-14ad-z6m5-md",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "SYSPROC",
                            "children": [],
                            "keyPath": "m884-hquy-0v6o-wyrk-rn/12eo-as55-6e7a-lixe-dg/qyaa-c2td-c2uw-n36g-f8",
                            "key": "qyaa-c2td-c2uw-n36g-f8",
                            "uuid": "qyaa-c2td-c2uw-n36g-f8",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "SYS",
                            "children": [],
                            "keyPath": "m884-hquy-0v6o-wyrk-rn/12eo-as55-6e7a-lixe-dg/98ac-holp-gt5j-9wy3-73",
                            "key": "98ac-holp-gt5j-9wy3-73",
                            "uuid": "98ac-holp-gt5j-9wy3-73",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "SYSSTAT",
                            "children": [],
                            "keyPath": "m884-hquy-0v6o-wyrk-rn/12eo-as55-6e7a-lixe-dg/zdd5-xol9-8xqn-37mw-yn",
                            "key": "zdd5-xol9-8xqn-37mw-yn",
                            "uuid": "zdd5-xol9-8xqn-37mw-yn",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        }
                    ],
                    "driverType": "Derby",
                    "keyPath": "m884-hquy-0v6o-wyrk-rn/12eo-as55-6e7a-lixe-dg",
                    "key": "12eo-as55-6e7a-lixe-dg",
                    "uuid": "12eo-as55-6e7a-lixe-dg",
                    "fetched": true
                },
                {
                    "name": "SampleEFWD Sample Travel Data",
                    "type": "sql.jdbc",
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "dataSourceType": "Plain Jdbc DataSource",
                    "classifier": "efwd",
                    "permissionLevel": 2,
                    "data": {
                        "id": "1",
                        "dir": "1463377807724/1463377978248/Sample EFW Dashboard",
                        "type": "sql.jdbc",
                        "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                    },
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "m884-hquy-0v6o-wyrk-rn/oqns-uaoc-f5m7-ot9k-vc",
                    "key": "oqns-uaoc-f5m7-ot9k-vc",
                    "uuid": "oqns-uaoc-f5m7-ot9k-vc"
                },
                {
                    "name": "SampleTravelDataDerby",
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "dataSourceType": "Managed DataSource",
                    "classifier": "global",
                    "type": "dynamicDataSource",
                    "data": {
                        "id": "1",
                        "type": "dynamicDataSource"
                    },
                    "permissionLevel": 5,
                    "dataSourceProvider": "tomcat",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "m884-hquy-0v6o-wyrk-rn/ye28-bmdi-3fnv-mtpn-o2",
                    "key": "ye28-bmdi-3fnv-mtpn-o2",
                    "uuid": "ye28-bmdi-3fnv-mtpn-o2"
                },
                {
                    "name": "test_SampleTravelDataDerby",
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "dataSourceType": "Managed DataSource",
                    "classifier": "global",
                    "type": "dynamicDataSource",
                    "data": {
                        "id": "2",
                        "type": "dynamicDataSource"
                    },
                    "permissionLevel": 5,
                    "dataSourceProvider": "tomcat",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "m884-hquy-0v6o-wyrk-rn/i6fx-3990-27mm-kyys-ev",
                    "key": "i6fx-3990-27mm-kyys-ev",
                    "uuid": "i6fx-3990-27mm-kyys-ev"
                }
            ],
            "key": "m884-hquy-0v6o-wyrk-rn",
            "uuid": "m884-hquy-0v6o-wyrk-rn",
            "keyPath": "m884-hquy-0v6o-wyrk-rn",
            "category": "dsGroup",
            "imgUrl": "images/data_sources/defaut_datasource.png"
        },
        {
            "name": "Sqlite",
            "children": [
                {
                    "name": "chinook",
                    "driver": "org.sqlite.JDBC",
                    "dataSourceType": "Managed DataSource",
                    "classifier": "global",
                    "type": "dynamicDataSource",
                    "data": {
                        "id": "3",
                        "type": "dynamicDataSource"
                    },
                    "permissionLevel": 5,
                    "dataSourceProvider": "tomcat",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Sqlite",
                    "keyPath": "f94p-kr5w-lbm2-m4m2-lb/5a52-dgql-6si7-kdz7-2a",
                    "key": "5a52-dgql-6si7-kdz7-2a",
                    "uuid": "5a52-dgql-6si7-kdz7-2a"
                },
                {
                    "name": "sqlite std",
                    "driver": "org.sqlite.JDBC",
                    "dataSourceType": "Managed DataSource",
                    "classifier": "global",
                    "type": "dynamicDataSource",
                    "data": {
                        "id": "4",
                        "type": "dynamicDataSource"
                    },
                    "permissionLevel": 5,
                    "dataSourceProvider": "tomcat",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Sqlite",
                    "keyPath": "f94p-kr5w-lbm2-m4m2-lb/0ye8-5x2a-7hyd-p0xh-5t",
                    "key": "0ye8-5x2a-7hyd-p0xh-5t",
                    "uuid": "0ye8-5x2a-7hyd-p0xh-5t"
                }
            ],
            "key": "f94p-kr5w-lbm2-m4m2-lb",
            "uuid": "f94p-kr5w-lbm2-m4m2-lb",
            "keyPath": "f94p-kr5w-lbm2-m4m2-lb",
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
                    "dir": "1463377807724/1463377978248/Sample EFW Report",
                    "type": "sql.jdbc",
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                },
                "connId": "1",
                "dsUUID": "12eo-as55-6e7a-lixe-dg",
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
                        "type": "sql.jdbc",
                        "baseType": "sql.jdbc",
                        "catSchemaPredicted": false,
                        "sync": false,
                        "catalog": "",
                        "schema": "HIUSER",
                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                        "connId": "aaonk",
                        "classifier": "db.workflow",
                        "datasourceName": "SampleEFWD Sample Travel Data",
                        "dsKeyPath": "m884-hquy-0v6o-wyrk-rn/12eo-as55-6e7a-lixe-dg/ezjy-1rp4-znqh-v89j-s8",
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
            "type": "sql.jdbc",
            "baseType": "sql.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "",
            "schema": "HIUSER",
            "dir": "1463377807724/1463377978248/Sample EFW Report",
            "connId": "aaonk",
            "classifier": "db.workflow",
            "datasourceName": "SampleEFWD Sample Travel Data",
            "dsKeyPath": "m884-hquy-0v6o-wyrk-rn/12eo-as55-6e7a-lixe-dg/ezjy-1rp4-znqh-v89j-s8",
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
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
            },
            "keyPath": "m884-hquy-0v6o-wyrk-rn/12eo-as55-6e7a-lixe-dg/ezjy-1rp4-znqh-v89j-s8/neja-aqlx-mw4s-94s8-5e",
            "key": "neja-aqlx-mw4s-94s8-5e",
            "alias": "geo_cordinates",
            "uuid": "neja-aqlx-mw4s-94s8-5e",
            "connId": "aaonk",
            "dataSource": {
                "id": "1",
                "type": "sql.jdbc",
                "baseType": "sql.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "connId": "aaonk",
                "classifier": "db.workflow",
                "datasourceName": "SampleEFWD Sample Travel Data",
                "dsKeyPath": "m884-hquy-0v6o-wyrk-rn/12eo-as55-6e7a-lixe-dg/ezjy-1rp4-znqh-v89j-s8",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleEFWD Sample Travel Data",
            "category": "table",
            "nameWithConnId": "geo_cordinates_aaonk",
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
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
            },
            "keyPath": "m884-hquy-0v6o-wyrk-rn/12eo-as55-6e7a-lixe-dg/ezjy-1rp4-znqh-v89j-s8/hl4e-qr0q-e9m8-r77p-bq",
            "key": "hl4e-qr0q-e9m8-r77p-bq",
            "alias": "meeting_details",
            "uuid": "hl4e-qr0q-e9m8-r77p-bq",
            "connId": "aaonk",
            "dataSource": {
                "id": "1",
                "type": "sql.jdbc",
                "baseType": "sql.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "connId": "aaonk",
                "classifier": "db.workflow",
                "datasourceName": "SampleEFWD Sample Travel Data",
                "dsKeyPath": "m884-hquy-0v6o-wyrk-rn/12eo-as55-6e7a-lixe-dg/ezjy-1rp4-znqh-v89j-s8",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleEFWD Sample Travel Data",
            "category": "table",
            "nameWithConnId": "meeting_details_aaonk",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "meeting_details"
        },
        "employee_details": {
            "id": "4e1fd245f4d13b77be423a43f01d80b2",
            "name": "employee_details",
            "data": {
                "id": "1",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
            },
            "keyPath": "m884-hquy-0v6o-wyrk-rn/12eo-as55-6e7a-lixe-dg/ezjy-1rp4-znqh-v89j-s8/rysf-m6b8-dp4f-rg56-u4",
            "key": "rysf-m6b8-dp4f-rg56-u4",
            "alias": "employee_details",
            "uuid": "rysf-m6b8-dp4f-rg56-u4",
            "connId": "aaonk",
            "dataSource": {
                "id": "1",
                "type": "sql.jdbc",
                "baseType": "sql.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "connId": "aaonk",
                "classifier": "db.workflow",
                "datasourceName": "SampleEFWD Sample Travel Data",
                "dsKeyPath": "m884-hquy-0v6o-wyrk-rn/12eo-as55-6e7a-lixe-dg/ezjy-1rp4-znqh-v89j-s8",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleEFWD Sample Travel Data",
            "category": "table",
            "nameWithConnId": "employee_details_aaonk",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "employee_details"
        },
        "dimdate": {
            "id": "4ac5d9f68b58bd7c0d179146e46795be",
            "name": "dimdate",
            "data": {
                "id": "1",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
            },
            "keyPath": "m884-hquy-0v6o-wyrk-rn/12eo-as55-6e7a-lixe-dg/ezjy-1rp4-znqh-v89j-s8/6z7h-7hyl-tagf-1alm-3w",
            "key": "6z7h-7hyl-tagf-1alm-3w",
            "alias": "dimdate",
            "uuid": "6z7h-7hyl-tagf-1alm-3w",
            "connId": "aaonk",
            "dataSource": {
                "id": "1",
                "type": "sql.jdbc",
                "baseType": "sql.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "connId": "aaonk",
                "classifier": "db.workflow",
                "datasourceName": "SampleEFWD Sample Travel Data",
                "dsKeyPath": "m884-hquy-0v6o-wyrk-rn/12eo-as55-6e7a-lixe-dg/ezjy-1rp4-znqh-v89j-s8",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleEFWD Sample Travel Data",
            "category": "table",
            "nameWithConnId": "dimdate_aaonk",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "dimdate",
            "columns": {
                "dim_id": {
                    "alias": "dim_id",
                    "fullyQualifiedColumn": "dimdate.dim_id",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    },
                    "category": "column",
                    "originalName": "dim_id",
                    "originalId": "5a7202b5-f5bf-4279-b72f-abc634b9eb75",
                    "tableKey": "dimdate",
                    "connId": "aaonk",
                    "duplicateIndex": 0,
                    "columnKey": "dim_id"
                },
                "fiscal_year": {
                    "alias": "fiscal_year",
                    "fullyQualifiedColumn": "dimdate.fiscal_year",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.sql.Date": "date"
                    },
                    "category": "column",
                    "originalName": "fiscal_year",
                    "originalId": "10baf2a5-4466-4d31-b000-99166911e9d5",
                    "tableKey": "dimdate",
                    "connId": "aaonk",
                    "duplicateIndex": 0,
                    "columnKey": "fiscal_year"
                },
                "modified_date": {
                    "alias": "modified_date",
                    "fullyQualifiedColumn": "dimdate.modified_date",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.sql.Timestamp": "dateTime"
                    },
                    "category": "column",
                    "originalName": "modified_date",
                    "originalId": "629b18de-1410-415a-b5d7-aff5fdb5ea2c",
                    "tableKey": "dimdate",
                    "connId": "aaonk",
                    "duplicateIndex": 0,
                    "columnKey": "modified_date"
                },
                "date_key": {
                    "alias": "date_key",
                    "fullyQualifiedColumn": "dimdate.date_key",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "originalName": "date_key",
                    "originalId": "d3044df9-3f1e-427b-80ac-bfd746882283",
                    "tableKey": "dimdate",
                    "connId": "aaonk",
                    "duplicateIndex": 0,
                    "columnKey": "date_key"
                },
                "day_number": {
                    "alias": "day_number",
                    "fullyQualifiedColumn": "dimdate.day_number",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "originalName": "day_number",
                    "originalId": "dec2ab4a-7d31-4bcb-8dd2-731b3a558c2d",
                    "tableKey": "dimdate",
                    "connId": "aaonk",
                    "duplicateIndex": 0,
                    "columnKey": "day_number"
                },
                "fiscal_month_name": {
                    "alias": "fiscal_month_name",
                    "fullyQualifiedColumn": "dimdate.fiscal_month_name",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "originalName": "fiscal_month_name",
                    "originalId": "bed2d3f6-6fd5-41d5-8b78-b2fa52b011de",
                    "tableKey": "dimdate",
                    "connId": "aaonk",
                    "duplicateIndex": 0,
                    "columnKey": "fiscal_month_name"
                },
                "fiscal_month_label": {
                    "alias": "fiscal_month_label",
                    "fullyQualifiedColumn": "dimdate.fiscal_month_label",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "originalName": "fiscal_month_label",
                    "originalId": "4b2ac446-b311-4cec-a3c0-851bad41d017",
                    "tableKey": "dimdate",
                    "connId": "aaonk",
                    "duplicateIndex": 0,
                    "columnKey": "fiscal_month_label"
                },
                "created_date": {
                    "alias": "created_date",
                    "fullyQualifiedColumn": "dimdate.created_date",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "originalName": "created_date",
                    "originalId": "906d15fa-5522-4834-85a2-e67d7f84a3e6",
                    "tableKey": "dimdate",
                    "connId": "aaonk",
                    "duplicateIndex": 0,
                    "columnKey": "created_date"
                },
                "created_time": {
                    "alias": "created_time",
                    "fullyQualifiedColumn": "dimdate.created_time",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "originalName": "created_time",
                    "originalId": "e76b8e83-ab54-4b9f-8aa8-cca38fda6960",
                    "tableKey": "dimdate",
                    "connId": "aaonk",
                    "duplicateIndex": 0,
                    "columnKey": "created_time"
                },
                "rating": {
                    "alias": "rating_alias",
                    "fullyQualifiedColumn": "dimdate.rating",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "originalName": "rating",
                    "originalId": "e013c04d-f833-4b98-8395-23887ba9d674",
                    "tableKey": "dimdate",
                    "connId": "aaonk",
                    "duplicateIndex": 0,
                    "columnKey": "rating"
                }
            },
            "columnsFetched": true
        },
        "travel_details": {
            "id": "8a28627d07d04ef096d9935f12e0c7e9",
            "name": "travel_details",
            "data": {
                "id": "1",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
            },
            "keyPath": "m884-hquy-0v6o-wyrk-rn/12eo-as55-6e7a-lixe-dg/ezjy-1rp4-znqh-v89j-s8/087v-ofb5-ooqa-c2t6-wg",
            "key": "087v-ofb5-ooqa-c2t6-wg",
            "alias": "travel_details_alias",
            "uuid": "087v-ofb5-ooqa-c2t6-wg",
            "connId": "aaonk",
            "dataSource": {
                "id": "1",
                "type": "sql.jdbc",
                "baseType": "sql.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "connId": "aaonk",
                "classifier": "db.workflow",
                "datasourceName": "SampleEFWD Sample Travel Data",
                "dsKeyPath": "m884-hquy-0v6o-wyrk-rn/12eo-as55-6e7a-lixe-dg/ezjy-1rp4-znqh-v89j-s8",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleEFWD Sample Travel Data",
            "category": "table",
            "nameWithConnId": "travel_details_aaonk",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "travel_details"
        }
    },
    "categories": {
        "m884-hquy-0v6o-wyrk-rn": {
            "ds": {
                "name": "SampleEFWD Sample Travel Data",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                "dataSourceType": "Plain Jdbc DataSource",
                "classifier": "efwd",
                "permissionLevel": 2,
                "data": {
                    "id": "1",
                    "dir": "1463377807724/1463377978248/Sample EFW Report",
                    "type": "sql.jdbc",
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                }
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
        "f94p-kr5w-lbm2-m4m2-lb": {
            "ds": {
                "name": "chinook",
                "driver": "org.sqlite.JDBC",
                "dataSourceType": "Managed DataSource",
                "classifier": "global",
                "type": "dynamicDataSource",
                "data": {
                    "id": "3",
                    "type": "dynamicDataSource"
                },
                "permissionLevel": 5,
                "dataSourceProvider": "tomcat"
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
            "id": "1",
            "type": "sql.jdbc",
            "baseType": "sql.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "",
            "schema": "HIUSER",
            "dir": "1463377807724/1463377978248/Sample EFW Report",
            "connId": "aaonk",
            "classifier": "db.workflow",
            "datasourceName": "SampleEFWD Sample Travel Data",
            "dsKeyPath": "m884-hquy-0v6o-wyrk-rn/12eo-as55-6e7a-lixe-dg/ezjy-1rp4-znqh-v89j-s8",
            "driverType": "Derby",
            "database": "HIUSER"
        }
    ],
    "changeDSList": {},
    "changedTables": [
        {
            "id": "8a28627d07d04ef096d9935f12e0c7e9",
            "alias": "travel_details_alias",
            "connId": "aaonk"
        }
    ],
    "changedColumns": [
        {
            "alias": "rating_alias",
            "columnId": "e013c04d-f833-4b98-8395-23887ba9d674",
            "connId": "aaonk",
            "tableId": "4ac5d9f68b58bd7c0d179146e46795be",
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
    "joins": [],
    "mode": "create",
    "allTablesKeys": [
        "neja-aqlx-mw4s-94s8-5e",
        "hl4e-qr0q-e9m8-r77p-bq",
        "rysf-m6b8-dp4f-rg56-u4",
        "6z7h-7hyl-tagf-1alm-3w",
        "087v-ofb5-ooqa-c2t6-wg"
    ],
    "selectedTableKeys": [
        "neja-aqlx-mw4s-94s8-5e",
        "hl4e-qr0q-e9m8-r77p-bq",
        "rysf-m6b8-dp4f-rg56-u4",
        "6z7h-7hyl-tagf-1alm-3w",
        "087v-ofb5-ooqa-c2t6-wg"
    ],
    "metadataName": "Metadata_1",
    "activeDataSource": false,
    "metadataToEdit": false,
    "isSavingInProgress": false
}

// snapshots with joins
export const storeSnapshotForSave_10 = {
    "dataFetchedFor": {
        "getDatasource": true,
        "joins": true,
        "listDataSources": true,
        "6ohb-jgsl-fnbp-02si-la": true,
        "0dby-7vjm-oabi-stej-h9": false,
        "1utg-dsjo-4elt-3jiu-3d": false
    },
    "loadingStatus": {
        "getDatasource": true,
        "listDataSources": true,
        "6ohb-jgsl-fnbp-02si-la": true,
        "0dby-7vjm-oabi-stej-h9": false,
        "1utg-dsjo-4elt-3jiu-3d": false
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
            "name": "Amazon Redshift",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Amazondynamodb",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Dremio",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Googlebigquery",
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
            "name": "Mysql",
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
            "name": "Amazon Redshift",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Amazondynamodb",
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
            "name": "Googlebigquery",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "driver": "com.helical.mongodb.MongoJdbcDriver",
            "databaseDialect": "himongo\t\t\t\t\t\t\t\t\t\t   ",
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
            "name": "Mysql",
            "categoryType": "supported",
            "categoryName": "Supported"
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
            "name": "Presto",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "driver": "ru.yandex.clickhouse.ClickHouseDriver",
            "databaseDialect": "clickhouse",
            "name": "Ru Yandex Clickhouse",
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
            "databaseDialect": "himongo\t\t\t\t\t\t\t\t\t\t   ",
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
            "driver": "ru.yandex.clickhouse.ClickHouseDriver",
            "databaseDialect": "clickhouse",
            "name": "Ru Yandex Clickhouse",
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
            "available": "true",
            "driver": "com.helical.mongodb.MongoJdbcDriver"
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
            "name": "SampleEFWD Sample Travel Data",
            "type": "sql.jdbc",
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "dataSourceType": "Plain Jdbc DataSource",
            "classifier": "efwd",
            "permissionLevel": 2,
            "data": {
                "id": "1",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
            }
        },
        {
            "name": "1",
            "dataSourceType": "Managed DataSource",
            "classifier": "global",
            "permissionLevel": 5,
            "data": {
                "id": "1",
                "dir": "1463377807724/1593413909773",
                "type": "global.jdbc"
            }
        },
        {
            "name": "1",
            "dataSourceType": "Managed DataSource",
            "classifier": "global",
            "permissionLevel": 5,
            "data": {
                "id": "1",
                "dir": "1463377807724/1570173811451",
                "type": "global.jdbc"
            }
        },
        {
            "name": "SampleEFWD Sample Travel Data",
            "type": "sql.jdbc",
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "dataSourceType": "Plain Jdbc DataSource",
            "classifier": "efwd",
            "permissionLevel": 2,
            "data": {
                "id": "1",
                "dir": "1463377807724/1463377978248/Sample EFW Dashboard",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
            }
        },
        {
            "name": "SampleTravelDataDerby",
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "dataSourceType": "Managed DataSource",
            "classifier": "global",
            "type": "dynamicDataSource",
            "data": {
                "id": "1",
                "type": "dynamicDataSource"
            },
            "permissionLevel": 5,
            "dataSourceProvider": "tomcat"
        },
        {
            "name": "test_SampleTravelDataDerby",
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "dataSourceType": "Managed DataSource",
            "classifier": "global",
            "type": "dynamicDataSource",
            "data": {
                "id": "2",
                "type": "dynamicDataSource"
            },
            "permissionLevel": 5,
            "dataSourceProvider": "tomcat"
        },
        {
            "name": "chinook",
            "driver": "org.sqlite.JDBC",
            "dataSourceType": "Managed DataSource",
            "classifier": "global",
            "type": "dynamicDataSource",
            "data": {
                "id": "3",
                "type": "dynamicDataSource"
            },
            "permissionLevel": 5,
            "dataSourceProvider": "tomcat"
        },
        {
            "name": "sqlite std",
            "driver": "org.sqlite.JDBC",
            "dataSourceType": "Managed DataSource",
            "classifier": "global",
            "type": "dynamicDataSource",
            "data": {
                "id": "4",
                "type": "dynamicDataSource"
            },
            "permissionLevel": 5,
            "dataSourceProvider": "tomcat"
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
                    "name": "SampleEFWD Sample Travel Data",
                    "type": "sql.jdbc",
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "dataSourceType": "Plain Jdbc DataSource",
                    "classifier": "efwd",
                    "permissionLevel": 2,
                    "data": {
                        "id": "1",
                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                        "type": "sql.jdbc",
                        "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                    },
                    "category": "dataSource",
                    "children": [
                        {
                            "name": "SQLJ",
                            "children": [],
                            "keyPath": "6ohb-jgsl-fnbp-02si-la/0dby-7vjm-oabi-stej-h9/zsef-yef2-2378-9aj7-eq",
                            "key": "zsef-yef2-2378-9aj7-eq",
                            "uuid": "zsef-yef2-2378-9aj7-eq",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "SYSFUN",
                            "children": [],
                            "keyPath": "6ohb-jgsl-fnbp-02si-la/0dby-7vjm-oabi-stej-h9/cqo0-gkr3-hi8d-z39r-q3",
                            "key": "cqo0-gkr3-hi8d-z39r-q3",
                            "uuid": "cqo0-gkr3-hi8d-z39r-q3",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "SYSCAT",
                            "children": [],
                            "keyPath": "6ohb-jgsl-fnbp-02si-la/0dby-7vjm-oabi-stej-h9/wbda-qnlo-rjzl-4wt0-en",
                            "key": "wbda-qnlo-rjzl-4wt0-en",
                            "uuid": "wbda-qnlo-rjzl-4wt0-en",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "HIUSER",
                            "children": [
                                {
                                    "id": "be534112989b616b194bc59c2fb25a42",
                                    "name": "geo_cordinates",
                                    "data": {
                                        "id": "1",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "type": "sql.jdbc",
                                        "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                                    },
                                    "keyPath": "6ohb-jgsl-fnbp-02si-la/0dby-7vjm-oabi-stej-h9/1utg-dsjo-4elt-3jiu-3d/v8nf-1cky-tfg9-ep4b-y2",
                                    "key": "v8nf-1cky-tfg9-ep4b-y2",
                                    "alias": "geo_cordinates",
                                    "uuid": "v8nf-1cky-tfg9-ep4b-y2",
                                    "connId": "1xnt7",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "sql.jdbc",
                                        "baseType": "sql.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "connId": "1xnt7",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleEFWD Sample Travel Data",
                                        "dsKeyPath": "6ohb-jgsl-fnbp-02si-la/0dby-7vjm-oabi-stej-h9/1utg-dsjo-4elt-3jiu-3d",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleEFWD Sample Travel Data",
                                    "category": "table",
                                    "nameWithConnId": "geo_cordinates_1xnt7",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "9645c648a1c0dbeec1287aaf1e996db3",
                                    "name": "meeting_details",
                                    "data": {
                                        "id": "1",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "type": "sql.jdbc",
                                        "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                                    },
                                    "keyPath": "6ohb-jgsl-fnbp-02si-la/0dby-7vjm-oabi-stej-h9/1utg-dsjo-4elt-3jiu-3d/kv6z-yeca-um0b-q5ft-34",
                                    "key": "kv6z-yeca-um0b-q5ft-34",
                                    "alias": "meeting_details",
                                    "uuid": "kv6z-yeca-um0b-q5ft-34",
                                    "connId": "1xnt7",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "sql.jdbc",
                                        "baseType": "sql.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "connId": "1xnt7",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleEFWD Sample Travel Data",
                                        "dsKeyPath": "6ohb-jgsl-fnbp-02si-la/0dby-7vjm-oabi-stej-h9/1utg-dsjo-4elt-3jiu-3d",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleEFWD Sample Travel Data",
                                    "category": "table",
                                    "nameWithConnId": "meeting_details_1xnt7",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "4e1fd245f4d13b77be423a43f01d80b2",
                                    "name": "employee_details",
                                    "data": {
                                        "id": "1",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "type": "sql.jdbc",
                                        "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                                    },
                                    "keyPath": "6ohb-jgsl-fnbp-02si-la/0dby-7vjm-oabi-stej-h9/1utg-dsjo-4elt-3jiu-3d/liiy-x6yp-qc4p-h3df-m7",
                                    "key": "liiy-x6yp-qc4p-h3df-m7",
                                    "alias": "employee_details",
                                    "uuid": "liiy-x6yp-qc4p-h3df-m7",
                                    "connId": "1xnt7",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "sql.jdbc",
                                        "baseType": "sql.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "connId": "1xnt7",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleEFWD Sample Travel Data",
                                        "dsKeyPath": "6ohb-jgsl-fnbp-02si-la/0dby-7vjm-oabi-stej-h9/1utg-dsjo-4elt-3jiu-3d",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleEFWD Sample Travel Data",
                                    "category": "table",
                                    "nameWithConnId": "employee_details_1xnt7",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "4ac5d9f68b58bd7c0d179146e46795be",
                                    "name": "dimdate",
                                    "data": {
                                        "id": "1",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "type": "sql.jdbc",
                                        "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                                    },
                                    "keyPath": "6ohb-jgsl-fnbp-02si-la/0dby-7vjm-oabi-stej-h9/1utg-dsjo-4elt-3jiu-3d/9c4q-avz5-ocot-xu05-n5",
                                    "key": "9c4q-avz5-ocot-xu05-n5",
                                    "alias": "dimdate",
                                    "uuid": "9c4q-avz5-ocot-xu05-n5",
                                    "connId": "1xnt7",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "sql.jdbc",
                                        "baseType": "sql.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "connId": "1xnt7",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleEFWD Sample Travel Data",
                                        "dsKeyPath": "6ohb-jgsl-fnbp-02si-la/0dby-7vjm-oabi-stej-h9/1utg-dsjo-4elt-3jiu-3d",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleEFWD Sample Travel Data",
                                    "category": "table",
                                    "nameWithConnId": "dimdate_1xnt7",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                },
                                {
                                    "id": "8a28627d07d04ef096d9935f12e0c7e9",
                                    "name": "travel_details",
                                    "data": {
                                        "id": "1",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "type": "sql.jdbc",
                                        "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                                    },
                                    "keyPath": "6ohb-jgsl-fnbp-02si-la/0dby-7vjm-oabi-stej-h9/1utg-dsjo-4elt-3jiu-3d/nvt5-kkun-zn6b-4vvo-hq",
                                    "key": "nvt5-kkun-zn6b-4vvo-hq",
                                    "alias": "travel_details",
                                    "uuid": "nvt5-kkun-zn6b-4vvo-hq",
                                    "connId": "1xnt7",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "sql.jdbc",
                                        "baseType": "sql.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                                        "connId": "1xnt7",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleEFWD Sample Travel Data",
                                        "dsKeyPath": "6ohb-jgsl-fnbp-02si-la/0dby-7vjm-oabi-stej-h9/1utg-dsjo-4elt-3jiu-3d",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleEFWD Sample Travel Data",
                                    "category": "table",
                                    "nameWithConnId": "travel_details_1xnt7",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                }
                            ],
                            "keyPath": "6ohb-jgsl-fnbp-02si-la/0dby-7vjm-oabi-stej-h9/1utg-dsjo-4elt-3jiu-3d",
                            "key": "1utg-dsjo-4elt-3jiu-3d",
                            "uuid": "1utg-dsjo-4elt-3jiu-3d",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data",
                            "fetched": true
                        },
                        {
                            "name": "SYSCS_DIAG",
                            "children": [],
                            "keyPath": "6ohb-jgsl-fnbp-02si-la/0dby-7vjm-oabi-stej-h9/mrrs-0rqu-y9fj-vbjw-ht",
                            "key": "mrrs-0rqu-y9fj-vbjw-ht",
                            "uuid": "mrrs-0rqu-y9fj-vbjw-ht",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "SYSCS_UTIL",
                            "children": [],
                            "keyPath": "6ohb-jgsl-fnbp-02si-la/0dby-7vjm-oabi-stej-h9/apfl-eafj-tpy3-3lj9-8l",
                            "key": "apfl-eafj-tpy3-3lj9-8l",
                            "uuid": "apfl-eafj-tpy3-3lj9-8l",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "SYSIBM",
                            "children": [],
                            "keyPath": "6ohb-jgsl-fnbp-02si-la/0dby-7vjm-oabi-stej-h9/g5yh-gaju-eyxv-loay-24",
                            "key": "g5yh-gaju-eyxv-loay-24",
                            "uuid": "g5yh-gaju-eyxv-loay-24",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "APP",
                            "children": [],
                            "keyPath": "6ohb-jgsl-fnbp-02si-la/0dby-7vjm-oabi-stej-h9/1dde-6e3x-3myf-b88l-te",
                            "key": "1dde-6e3x-3myf-b88l-te",
                            "uuid": "1dde-6e3x-3myf-b88l-te",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "NULLID",
                            "children": [],
                            "keyPath": "6ohb-jgsl-fnbp-02si-la/0dby-7vjm-oabi-stej-h9/nqwt-9onl-43yq-r4ox-jk",
                            "key": "nqwt-9onl-43yq-r4ox-jk",
                            "uuid": "nqwt-9onl-43yq-r4ox-jk",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "SYSPROC",
                            "children": [],
                            "keyPath": "6ohb-jgsl-fnbp-02si-la/0dby-7vjm-oabi-stej-h9/i37n-6ihi-hfqh-ri3n-4l",
                            "key": "i37n-6ihi-hfqh-ri3n-4l",
                            "uuid": "i37n-6ihi-hfqh-ri3n-4l",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "SYS",
                            "children": [],
                            "keyPath": "6ohb-jgsl-fnbp-02si-la/0dby-7vjm-oabi-stej-h9/wa83-zy8n-afgq-in2o-or",
                            "key": "wa83-zy8n-afgq-in2o-or",
                            "uuid": "wa83-zy8n-afgq-in2o-or",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        },
                        {
                            "name": "SYSSTAT",
                            "children": [],
                            "keyPath": "6ohb-jgsl-fnbp-02si-la/0dby-7vjm-oabi-stej-h9/j8oj-45j3-fvro-3ani-yd",
                            "key": "j8oj-45j3-fvro-3ani-yd",
                            "uuid": "j8oj-45j3-fvro-3ani-yd",
                            "data": {
                                "id": "1",
                                "dir": "1463377807724/1463377978248/Sample EFW Report",
                                "type": "sql.jdbc",
                                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                            },
                            "category": "schema",
                            "datasourceName": "SampleEFWD Sample Travel Data"
                        }
                    ],
                    "driverType": "Derby",
                    "keyPath": "6ohb-jgsl-fnbp-02si-la/0dby-7vjm-oabi-stej-h9",
                    "key": "0dby-7vjm-oabi-stej-h9",
                    "uuid": "0dby-7vjm-oabi-stej-h9",
                    "fetched": true
                },
                {
                    "name": "SampleEFWD Sample Travel Data",
                    "type": "sql.jdbc",
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "dataSourceType": "Plain Jdbc DataSource",
                    "classifier": "efwd",
                    "permissionLevel": 2,
                    "data": {
                        "id": "1",
                        "dir": "1463377807724/1463377978248/Sample EFW Dashboard",
                        "type": "sql.jdbc",
                        "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                    },
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "6ohb-jgsl-fnbp-02si-la/878g-4nr7-rvun-mt87-t1",
                    "key": "878g-4nr7-rvun-mt87-t1",
                    "uuid": "878g-4nr7-rvun-mt87-t1"
                },
                {
                    "name": "SampleTravelDataDerby",
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "dataSourceType": "Managed DataSource",
                    "classifier": "global",
                    "type": "dynamicDataSource",
                    "data": {
                        "id": "1",
                        "type": "dynamicDataSource"
                    },
                    "permissionLevel": 5,
                    "dataSourceProvider": "tomcat",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "6ohb-jgsl-fnbp-02si-la/607k-14pm-49v2-bv38-dd",
                    "key": "607k-14pm-49v2-bv38-dd",
                    "uuid": "607k-14pm-49v2-bv38-dd"
                },
                {
                    "name": "test_SampleTravelDataDerby",
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "dataSourceType": "Managed DataSource",
                    "classifier": "global",
                    "type": "dynamicDataSource",
                    "data": {
                        "id": "2",
                        "type": "dynamicDataSource"
                    },
                    "permissionLevel": 5,
                    "dataSourceProvider": "tomcat",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "6ohb-jgsl-fnbp-02si-la/hh2f-togy-7lzq-o5uq-zj",
                    "key": "hh2f-togy-7lzq-o5uq-zj",
                    "uuid": "hh2f-togy-7lzq-o5uq-zj"
                }
            ],
            "key": "6ohb-jgsl-fnbp-02si-la",
            "uuid": "6ohb-jgsl-fnbp-02si-la",
            "keyPath": "6ohb-jgsl-fnbp-02si-la",
            "category": "dsGroup",
            "imgUrl": "images/data_sources/defaut_datasource.png"
        },
        {
            "name": "Sqlite",
            "children": [
                {
                    "name": "chinook",
                    "driver": "org.sqlite.JDBC",
                    "dataSourceType": "Managed DataSource",
                    "classifier": "global",
                    "type": "dynamicDataSource",
                    "data": {
                        "id": "3",
                        "type": "dynamicDataSource"
                    },
                    "permissionLevel": 5,
                    "dataSourceProvider": "tomcat",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Sqlite",
                    "keyPath": "184g-m6tp-gbva-v245-m7/42sa-yzye-o5bl-ovne-1a",
                    "key": "42sa-yzye-o5bl-ovne-1a",
                    "uuid": "42sa-yzye-o5bl-ovne-1a"
                },
                {
                    "name": "sqlite std",
                    "driver": "org.sqlite.JDBC",
                    "dataSourceType": "Managed DataSource",
                    "classifier": "global",
                    "type": "dynamicDataSource",
                    "data": {
                        "id": "4",
                        "type": "dynamicDataSource"
                    },
                    "permissionLevel": 5,
                    "dataSourceProvider": "tomcat",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Sqlite",
                    "keyPath": "184g-m6tp-gbva-v245-m7/xzz1-r7fz-ityo-jdeb-0j",
                    "key": "xzz1-r7fz-ityo-jdeb-0j",
                    "uuid": "xzz1-r7fz-ityo-jdeb-0j"
                }
            ],
            "key": "184g-m6tp-gbva-v245-m7",
            "uuid": "184g-m6tp-gbva-v245-m7",
            "keyPath": "184g-m6tp-gbva-v245-m7",
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
                    "dir": "1463377807724/1463377978248/Sample EFW Report",
                    "type": "sql.jdbc",
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                },
                "connId": "1",
                "dsUUID": "0dby-7vjm-oabi-stej-h9",
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
                        "type": "sql.jdbc",
                        "baseType": "sql.jdbc",
                        "catSchemaPredicted": false,
                        "sync": false,
                        "catalog": "",
                        "schema": "HIUSER",
                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                        "connId": "1xnt7",
                        "classifier": "db.workflow",
                        "datasourceName": "SampleEFWD Sample Travel Data",
                        "dsKeyPath": "6ohb-jgsl-fnbp-02si-la/0dby-7vjm-oabi-stej-h9/1utg-dsjo-4elt-3jiu-3d",
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
            "type": "sql.jdbc",
            "baseType": "sql.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "",
            "schema": "HIUSER",
            "dir": "1463377807724/1463377978248/Sample EFW Report",
            "connId": "1xnt7",
            "classifier": "db.workflow",
            "datasourceName": "SampleEFWD Sample Travel Data",
            "dsKeyPath": "6ohb-jgsl-fnbp-02si-la/0dby-7vjm-oabi-stej-h9/1utg-dsjo-4elt-3jiu-3d",
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
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
            },
            "keyPath": "6ohb-jgsl-fnbp-02si-la/0dby-7vjm-oabi-stej-h9/1utg-dsjo-4elt-3jiu-3d/v8nf-1cky-tfg9-ep4b-y2",
            "key": "v8nf-1cky-tfg9-ep4b-y2",
            "alias": "geo_cordinates",
            "uuid": "v8nf-1cky-tfg9-ep4b-y2",
            "connId": "1xnt7",
            "dataSource": {
                "id": "1",
                "type": "sql.jdbc",
                "baseType": "sql.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "connId": "1xnt7",
                "classifier": "db.workflow",
                "datasourceName": "SampleEFWD Sample Travel Data",
                "dsKeyPath": "6ohb-jgsl-fnbp-02si-la/0dby-7vjm-oabi-stej-h9/1utg-dsjo-4elt-3jiu-3d",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleEFWD Sample Travel Data",
            "category": "table",
            "nameWithConnId": "geo_cordinates_1xnt7",
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
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
            },
            "keyPath": "6ohb-jgsl-fnbp-02si-la/0dby-7vjm-oabi-stej-h9/1utg-dsjo-4elt-3jiu-3d/kv6z-yeca-um0b-q5ft-34",
            "key": "kv6z-yeca-um0b-q5ft-34",
            "alias": "meeting_details",
            "uuid": "kv6z-yeca-um0b-q5ft-34",
            "connId": "1xnt7",
            "dataSource": {
                "id": "1",
                "type": "sql.jdbc",
                "baseType": "sql.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "connId": "1xnt7",
                "classifier": "db.workflow",
                "datasourceName": "SampleEFWD Sample Travel Data",
                "dsKeyPath": "6ohb-jgsl-fnbp-02si-la/0dby-7vjm-oabi-stej-h9/1utg-dsjo-4elt-3jiu-3d",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleEFWD Sample Travel Data",
            "category": "table",
            "nameWithConnId": "meeting_details_1xnt7",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "meeting_details"
        },
        "employee_details": {
            "id": "4e1fd245f4d13b77be423a43f01d80b2",
            "name": "employee_details",
            "data": {
                "id": "1",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
            },
            "keyPath": "6ohb-jgsl-fnbp-02si-la/0dby-7vjm-oabi-stej-h9/1utg-dsjo-4elt-3jiu-3d/liiy-x6yp-qc4p-h3df-m7",
            "key": "liiy-x6yp-qc4p-h3df-m7",
            "alias": "employee_details",
            "uuid": "liiy-x6yp-qc4p-h3df-m7",
            "connId": "1xnt7",
            "dataSource": {
                "id": "1",
                "type": "sql.jdbc",
                "baseType": "sql.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "connId": "1xnt7",
                "classifier": "db.workflow",
                "datasourceName": "SampleEFWD Sample Travel Data",
                "dsKeyPath": "6ohb-jgsl-fnbp-02si-la/0dby-7vjm-oabi-stej-h9/1utg-dsjo-4elt-3jiu-3d",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleEFWD Sample Travel Data",
            "category": "table",
            "nameWithConnId": "employee_details_1xnt7",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "employee_details"
        },
        "dimdate": {
            "id": "4ac5d9f68b58bd7c0d179146e46795be",
            "name": "dimdate",
            "data": {
                "id": "1",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
            },
            "keyPath": "6ohb-jgsl-fnbp-02si-la/0dby-7vjm-oabi-stej-h9/1utg-dsjo-4elt-3jiu-3d/9c4q-avz5-ocot-xu05-n5",
            "key": "9c4q-avz5-ocot-xu05-n5",
            "alias": "dimdate",
            "uuid": "9c4q-avz5-ocot-xu05-n5",
            "connId": "1xnt7",
            "dataSource": {
                "id": "1",
                "type": "sql.jdbc",
                "baseType": "sql.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "connId": "1xnt7",
                "classifier": "db.workflow",
                "datasourceName": "SampleEFWD Sample Travel Data",
                "dsKeyPath": "6ohb-jgsl-fnbp-02si-la/0dby-7vjm-oabi-stej-h9/1utg-dsjo-4elt-3jiu-3d",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleEFWD Sample Travel Data",
            "category": "table",
            "nameWithConnId": "dimdate_1xnt7",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "dimdate"
        },
        "travel_details": {
            "id": "8a28627d07d04ef096d9935f12e0c7e9",
            "name": "travel_details",
            "data": {
                "id": "1",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
            },
            "keyPath": "6ohb-jgsl-fnbp-02si-la/0dby-7vjm-oabi-stej-h9/1utg-dsjo-4elt-3jiu-3d/nvt5-kkun-zn6b-4vvo-hq",
            "key": "nvt5-kkun-zn6b-4vvo-hq",
            "alias": "travel_details",
            "uuid": "nvt5-kkun-zn6b-4vvo-hq",
            "connId": "1xnt7",
            "dataSource": {
                "id": "1",
                "type": "sql.jdbc",
                "baseType": "sql.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "connId": "1xnt7",
                "classifier": "db.workflow",
                "datasourceName": "SampleEFWD Sample Travel Data",
                "dsKeyPath": "6ohb-jgsl-fnbp-02si-la/0dby-7vjm-oabi-stej-h9/1utg-dsjo-4elt-3jiu-3d",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleEFWD Sample Travel Data",
            "category": "table",
            "nameWithConnId": "travel_details_1xnt7",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "travel_details"
        }
    },
    "categories": {
        "6ohb-jgsl-fnbp-02si-la": {
            "ds": {
                "name": "SampleEFWD Sample Travel Data",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                "dataSourceType": "Plain Jdbc DataSource",
                "classifier": "efwd",
                "permissionLevel": 2,
                "data": {
                    "id": "1",
                    "dir": "1463377807724/1463377978248/Sample EFW Report",
                    "type": "sql.jdbc",
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                }
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
        "184g-m6tp-gbva-v245-m7": {
            "ds": {
                "name": "chinook",
                "driver": "org.sqlite.JDBC",
                "dataSourceType": "Managed DataSource",
                "classifier": "global",
                "type": "dynamicDataSource",
                "data": {
                    "id": "3",
                    "type": "dynamicDataSource"
                },
                "permissionLevel": 5,
                "dataSourceProvider": "tomcat"
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
            "type": "sql.jdbc",
            "baseType": "sql.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "",
            "schema": "HIUSER",
            "dir": "1463377807724/1463377978248/Sample EFW Report",
            "connId": "1xnt7",
            "classifier": "db.workflow",
            "datasourceName": "SampleEFWD Sample Travel Data",
            "dsKeyPath": "6ohb-jgsl-fnbp-02si-la/0dby-7vjm-oabi-stej-h9/1utg-dsjo-4elt-3jiu-3d",
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
    "joins": [
        {
            "id": "af8f3186af3703a70a3d6e219faafb4e",
            "type": "inner",
            "operator": "=",
            "left": {
                "table": "employee_details",
                "column": "employee_id"
            },
            "right": {
                "table": "meeting_details",
                "column": "meeting_by"
            },
            "uuid": "pdeh-mdxu-3d3i-a43w-ww",
            "index": 1,
            "action": "noChange"
        },
        {
            "id": "ca21d00c8c87263dedd812f8f74c05b5",
            "type": "inner",
            "operator": "=",
            "left": {
                "table": "geo_cordinates",
                "column": "location_id"
            },
            "right": {
                "table": "dimdate",
                "column": "dim_id"
            },
            "uuid": "j0sx-tx8b-7fd1-abw6-qa",
            "index": 2,
            "action": "noChange"
        },
        {
            "id": "aab02b68e2c7febf125c50c8c5175037",
            "type": "inner",
            "operator": "=",
            "left": {
                "table": "employee_details",
                "column": "employee_id"
            },
            "right": {
                "table": "travel_details",
                "column": "travelled_by"
            },
            "uuid": "kv53-sy79-qqcf-5kka-zi",
            "index": 3,
            "action": "noChange"
        },
        {
            "id": "daa3221b04c18670d4af25ac99f3ae76",
            "type": "inner",
            "operator": "=",
            "left": {
                "table": "geo_cordinates",
                "column": "location_id"
            },
            "right": {
                "table": "travel_details",
                "column": "destination_id"
            },
            "uuid": "nd3x-tqdp-zdl0-xsik-pq",
            "index": 4,
            "action": "noChange"
        },
        {
            "id": "cdeb5b19799c89335f23ed9b50cc5a22",
            "type": "inner",
            "operator": "=",
            "left": {
                "table": "geo_cordinates",
                "column": "location_id"
            },
            "right": {
                "table": "travel_details",
                "column": "source_id"
            },
            "uuid": "u57k-zu90-ohi6-s5f7-9m",
            "index": 5,
            "action": "noChange"
        }
    ],
    "mode": "create",
    "allTablesKeys": [
        "v8nf-1cky-tfg9-ep4b-y2",
        "kv6z-yeca-um0b-q5ft-34",
        "liiy-x6yp-qc4p-h3df-m7",
        "9c4q-avz5-ocot-xu05-n5",
        "nvt5-kkun-zn6b-4vvo-hq"
    ],
    "selectedTableKeys": [
        "v8nf-1cky-tfg9-ep4b-y2",
        "kv6z-yeca-um0b-q5ft-34",
        "liiy-x6yp-qc4p-h3df-m7",
        "9c4q-avz5-ocot-xu05-n5",
        "nvt5-kkun-zn6b-4vvo-hq"
    ],
    "metadataName": "Metadata_1",
    "activeDataSource": false,
    "metadataToEdit": false,
    "isSavingInProgress": false
}

// snapshot for edit sample metadata
export const storeSnapShotForEdit_1 = {
    "dataFetchedFor": {
        "getDatasource": true,
        "joins": false,
        "listDataSources": true,
        "b5hm-zg5j-93pt-mk7x-3m": false,
        "7cjy-7cxg-bp4a-62e1-8q": false
    },
    "loadingStatus": {
        "getDatasource": true,
        "listDataSources": true,
        "b5hm-zg5j-93pt-mk7x-3m": false,
        "7cjy-7cxg-bp4a-62e1-8q": false
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
            "name": "Amazon Redshift",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Amazondynamodb",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Dremio",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Googlebigquery",
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
            "name": "Mysql",
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
            "name": "Amazon Redshift",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "name": "Amazondynamodb",
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
            "name": "Googlebigquery",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "driver": "com.helical.mongodb.MongoJdbcDriver",
            "databaseDialect": "himongo\t\t\t\t\t\t\t\t\t\t   ",
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
            "name": "Mysql",
            "categoryType": "supported",
            "categoryName": "Supported"
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
            "name": "Presto",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "driver": "ru.yandex.clickhouse.ClickHouseDriver",
            "databaseDialect": "clickhouse",
            "name": "Ru Yandex Clickhouse",
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
            "databaseDialect": "himongo\t\t\t\t\t\t\t\t\t\t   ",
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
            "driver": "ru.yandex.clickhouse.ClickHouseDriver",
            "databaseDialect": "clickhouse",
            "name": "Ru Yandex Clickhouse",
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
            "available": "true",
            "driver": "com.helical.mongodb.MongoJdbcDriver"
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
            "name": "SampleEFWD Sample Travel Data",
            "type": "sql.jdbc",
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "dataSourceType": "Plain Jdbc DataSource",
            "classifier": "efwd",
            "permissionLevel": 2,
            "data": {
                "id": "1",
                "dir": "1463377807724/1463377978248/Sample EFW Report",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
            }
        },
        {
            "name": "1",
            "dataSourceType": "Managed DataSource",
            "classifier": "global",
            "permissionLevel": 5,
            "data": {
                "id": "1",
                "dir": "1463377807724/1593413909773",
                "type": "global.jdbc"
            }
        },
        {
            "name": "1",
            "dataSourceType": "Managed DataSource",
            "classifier": "global",
            "permissionLevel": 5,
            "data": {
                "id": "1",
                "dir": "1463377807724/1570173811451",
                "type": "global.jdbc"
            }
        },
        {
            "name": "SampleEFWD Sample Travel Data",
            "type": "sql.jdbc",
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "dataSourceType": "Plain Jdbc DataSource",
            "classifier": "efwd",
            "permissionLevel": 2,
            "data": {
                "id": "1",
                "dir": "1463377807724/1463377978248/Sample EFW Dashboard",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver"
            }
        },
        {
            "name": "SampleTravelDataDerby",
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "dataSourceType": "Managed DataSource",
            "classifier": "global",
            "type": "dynamicDataSource",
            "data": {
                "id": "1",
                "type": "dynamicDataSource"
            },
            "permissionLevel": 5,
            "dataSourceProvider": "tomcat"
        },
        {
            "name": "test_SampleTravelDataDerby",
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "dataSourceType": "Managed DataSource",
            "classifier": "global",
            "type": "dynamicDataSource",
            "data": {
                "id": "2",
                "type": "dynamicDataSource"
            },
            "permissionLevel": 5,
            "dataSourceProvider": "tomcat"
        },
        {
            "name": "chinook",
            "driver": "org.sqlite.JDBC",
            "dataSourceType": "Managed DataSource",
            "classifier": "global",
            "type": "dynamicDataSource",
            "data": {
                "id": "3",
                "type": "dynamicDataSource"
            },
            "permissionLevel": 5,
            "dataSourceProvider": "tomcat"
        },
        {
            "name": "sqlite std",
            "driver": "org.sqlite.JDBC",
            "dataSourceType": "Managed DataSource",
            "classifier": "global",
            "type": "dynamicDataSource",
            "data": {
                "id": "4",
                "type": "dynamicDataSource"
            },
            "permissionLevel": 5,
            "dataSourceProvider": "tomcat"
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
                    "name": "SampleEFWD Sample Travel Data",
                    "type": "sql.jdbc",
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "dataSourceType": "Plain Jdbc DataSource",
                    "classifier": "efwd",
                    "permissionLevel": 2,
                    "data": {
                        "id": "1",
                        "dir": "1463377807724/1463377978248/Sample EFW Report",
                        "type": "sql.jdbc",
                        "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                    },
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "er7y-mks4-nbek-qhw8-4e/rjju-7885-t3jk-bv07-b8",
                    "key": "rjju-7885-t3jk-bv07-b8",
                    "uuid": "rjju-7885-t3jk-bv07-b8"
                },
                {
                    "name": "SampleEFWD Sample Travel Data",
                    "type": "sql.jdbc",
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "dataSourceType": "Plain Jdbc DataSource",
                    "classifier": "efwd",
                    "permissionLevel": 2,
                    "data": {
                        "id": "1",
                        "dir": "1463377807724/1463377978248/Sample EFW Dashboard",
                        "type": "sql.jdbc",
                        "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                    },
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "er7y-mks4-nbek-qhw8-4e/98wh-rkbv-kphe-15c2-ii",
                    "key": "98wh-rkbv-kphe-15c2-ii",
                    "uuid": "98wh-rkbv-kphe-15c2-ii"
                },
                {
                    "name": "SampleTravelDataDerby",
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "dataSourceType": "Managed DataSource",
                    "classifier": "global",
                    "type": "dynamicDataSource",
                    "data": {
                        "id": "1",
                        "type": "dynamicDataSource"
                    },
                    "permissionLevel": 5,
                    "dataSourceProvider": "tomcat",
                    "category": "dataSource",
                    "children": [
                        {
                            "name": "SQLJ",
                            "children": [],
                            "keyPath": "er7y-mks4-nbek-qhw8-4e/b5hm-zg5j-93pt-mk7x-3m/tob3-c4z8-sx49-64ln-3f",
                            "key": "tob3-c4z8-sx49-64ln-3f",
                            "uuid": "tob3-c4z8-sx49-64ln-3f",
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
                            "keyPath": "er7y-mks4-nbek-qhw8-4e/b5hm-zg5j-93pt-mk7x-3m/buzf-nryo-hb1a-jhnq-1p",
                            "key": "buzf-nryo-hb1a-jhnq-1p",
                            "uuid": "buzf-nryo-hb1a-jhnq-1p",
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
                            "keyPath": "er7y-mks4-nbek-qhw8-4e/b5hm-zg5j-93pt-mk7x-3m/60l8-t7wf-sei0-950y-g1",
                            "key": "60l8-t7wf-sei0-950y-g1",
                            "uuid": "60l8-t7wf-sei0-950y-g1",
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
                                    "keyPath": "er7y-mks4-nbek-qhw8-4e/b5hm-zg5j-93pt-mk7x-3m/7cjy-7cxg-bp4a-62e1-8q/lnbw-8h6j-55sa-y6bz-3x",
                                    "key": "lnbw-8h6j-55sa-y6bz-3x",
                                    "alias": "geo_cordinates",
                                    "uuid": "lnbw-8h6j-55sa-y6bz-3x",
                                    "connId": "z9xhn",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "z9xhn",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "er7y-mks4-nbek-qhw8-4e/b5hm-zg5j-93pt-mk7x-3m/7cjy-7cxg-bp4a-62e1-8q",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "geo_cordinates_z9xhn",
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
                                    "keyPath": "er7y-mks4-nbek-qhw8-4e/b5hm-zg5j-93pt-mk7x-3m/7cjy-7cxg-bp4a-62e1-8q/5gf3-pvz3-gsy6-slad-8c",
                                    "key": "5gf3-pvz3-gsy6-slad-8c",
                                    "alias": "meeting_details",
                                    "uuid": "5gf3-pvz3-gsy6-slad-8c",
                                    "connId": "z9xhn",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "z9xhn",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "er7y-mks4-nbek-qhw8-4e/b5hm-zg5j-93pt-mk7x-3m/7cjy-7cxg-bp4a-62e1-8q",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "meeting_details_z9xhn",
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
                                    "keyPath": "er7y-mks4-nbek-qhw8-4e/b5hm-zg5j-93pt-mk7x-3m/7cjy-7cxg-bp4a-62e1-8q/gpyk-48d0-6fgb-i4lh-ev",
                                    "key": "gpyk-48d0-6fgb-i4lh-ev",
                                    "alias": "employee_details",
                                    "uuid": "gpyk-48d0-6fgb-i4lh-ev",
                                    "connId": "z9xhn",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "z9xhn",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "er7y-mks4-nbek-qhw8-4e/b5hm-zg5j-93pt-mk7x-3m/7cjy-7cxg-bp4a-62e1-8q",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "employee_details_z9xhn",
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
                                    "keyPath": "er7y-mks4-nbek-qhw8-4e/b5hm-zg5j-93pt-mk7x-3m/7cjy-7cxg-bp4a-62e1-8q/yly3-pl8y-pgqm-5h1g-b2",
                                    "key": "yly3-pl8y-pgqm-5h1g-b2",
                                    "alias": "dimdate",
                                    "uuid": "yly3-pl8y-pgqm-5h1g-b2",
                                    "connId": "z9xhn",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "z9xhn",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "er7y-mks4-nbek-qhw8-4e/b5hm-zg5j-93pt-mk7x-3m/7cjy-7cxg-bp4a-62e1-8q",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "dimdate_z9xhn",
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
                                    "keyPath": "er7y-mks4-nbek-qhw8-4e/b5hm-zg5j-93pt-mk7x-3m/7cjy-7cxg-bp4a-62e1-8q/zd96-6c7u-punh-2at4-yr",
                                    "key": "zd96-6c7u-punh-2at4-yr",
                                    "alias": "travel_details",
                                    "uuid": "zd96-6c7u-punh-2at4-yr",
                                    "connId": "z9xhn",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "z9xhn",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "er7y-mks4-nbek-qhw8-4e/b5hm-zg5j-93pt-mk7x-3m/7cjy-7cxg-bp4a-62e1-8q",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "travel_details_z9xhn",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                }
                            ],
                            "keyPath": "er7y-mks4-nbek-qhw8-4e/b5hm-zg5j-93pt-mk7x-3m/7cjy-7cxg-bp4a-62e1-8q",
                            "key": "7cjy-7cxg-bp4a-62e1-8q",
                            "uuid": "7cjy-7cxg-bp4a-62e1-8q",
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
                            "keyPath": "er7y-mks4-nbek-qhw8-4e/b5hm-zg5j-93pt-mk7x-3m/ny31-vzn6-d2y1-z166-wp",
                            "key": "ny31-vzn6-d2y1-z166-wp",
                            "uuid": "ny31-vzn6-d2y1-z166-wp",
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
                            "keyPath": "er7y-mks4-nbek-qhw8-4e/b5hm-zg5j-93pt-mk7x-3m/bp69-u740-kdpt-4yi5-q8",
                            "key": "bp69-u740-kdpt-4yi5-q8",
                            "uuid": "bp69-u740-kdpt-4yi5-q8",
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
                            "keyPath": "er7y-mks4-nbek-qhw8-4e/b5hm-zg5j-93pt-mk7x-3m/k9bv-59ek-r5yk-nefn-zq",
                            "key": "k9bv-59ek-r5yk-nefn-zq",
                            "uuid": "k9bv-59ek-r5yk-nefn-zq",
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
                            "keyPath": "er7y-mks4-nbek-qhw8-4e/b5hm-zg5j-93pt-mk7x-3m/l7l5-2mkw-rbp2-3xp5-1c",
                            "key": "l7l5-2mkw-rbp2-3xp5-1c",
                            "uuid": "l7l5-2mkw-rbp2-3xp5-1c",
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
                            "keyPath": "er7y-mks4-nbek-qhw8-4e/b5hm-zg5j-93pt-mk7x-3m/po37-0awc-sfhw-mt6x-mc",
                            "key": "po37-0awc-sfhw-mt6x-mc",
                            "uuid": "po37-0awc-sfhw-mt6x-mc",
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
                            "keyPath": "er7y-mks4-nbek-qhw8-4e/b5hm-zg5j-93pt-mk7x-3m/2axa-9uo1-8flz-hjxl-7h",
                            "key": "2axa-9uo1-8flz-hjxl-7h",
                            "uuid": "2axa-9uo1-8flz-hjxl-7h",
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
                            "keyPath": "er7y-mks4-nbek-qhw8-4e/b5hm-zg5j-93pt-mk7x-3m/ytro-wflf-xxax-abr3-np",
                            "key": "ytro-wflf-xxax-abr3-np",
                            "uuid": "ytro-wflf-xxax-abr3-np",
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
                            "keyPath": "er7y-mks4-nbek-qhw8-4e/b5hm-zg5j-93pt-mk7x-3m/qc01-xuv2-ciqe-a9ha-eu",
                            "key": "qc01-xuv2-ciqe-a9ha-eu",
                            "uuid": "qc01-xuv2-ciqe-a9ha-eu",
                            "data": {
                                "id": "1",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "SampleTravelDataDerby"
                        }
                    ],
                    "driverType": "Derby",
                    "keyPath": "er7y-mks4-nbek-qhw8-4e/b5hm-zg5j-93pt-mk7x-3m",
                    "key": "b5hm-zg5j-93pt-mk7x-3m",
                    "uuid": "b5hm-zg5j-93pt-mk7x-3m",
                    "fetched": true
                },
                {
                    "name": "test_SampleTravelDataDerby",
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                    "dataSourceType": "Managed DataSource",
                    "classifier": "global",
                    "type": "dynamicDataSource",
                    "data": {
                        "id": "2",
                        "type": "dynamicDataSource"
                    },
                    "permissionLevel": 5,
                    "dataSourceProvider": "tomcat",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "er7y-mks4-nbek-qhw8-4e/kjuu-ruah-b5qm-5mpr-yg",
                    "key": "kjuu-ruah-b5qm-5mpr-yg",
                    "uuid": "kjuu-ruah-b5qm-5mpr-yg"
                }
            ],
            "key": "er7y-mks4-nbek-qhw8-4e",
            "uuid": "er7y-mks4-nbek-qhw8-4e",
            "keyPath": "er7y-mks4-nbek-qhw8-4e",
            "category": "dsGroup",
            "imgUrl": "images/data_sources/defaut_datasource.png"
        },
        {
            "name": "Sqlite",
            "children": [
                {
                    "name": "chinook",
                    "driver": "org.sqlite.JDBC",
                    "dataSourceType": "Managed DataSource",
                    "classifier": "global",
                    "type": "dynamicDataSource",
                    "data": {
                        "id": "3",
                        "type": "dynamicDataSource"
                    },
                    "permissionLevel": 5,
                    "dataSourceProvider": "tomcat",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Sqlite",
                    "keyPath": "vc99-z7j2-f2cj-hlou-6e/m8dm-bxsg-fw7y-wsym-tp",
                    "key": "m8dm-bxsg-fw7y-wsym-tp",
                    "uuid": "m8dm-bxsg-fw7y-wsym-tp"
                },
                {
                    "name": "sqlite std",
                    "driver": "org.sqlite.JDBC",
                    "dataSourceType": "Managed DataSource",
                    "classifier": "global",
                    "type": "dynamicDataSource",
                    "data": {
                        "id": "4",
                        "type": "dynamicDataSource"
                    },
                    "permissionLevel": 5,
                    "dataSourceProvider": "tomcat",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Sqlite",
                    "keyPath": "vc99-z7j2-f2cj-hlou-6e/pumo-n53j-fcn6-28vc-z8",
                    "key": "pumo-n53j-fcn6-28vc-z8",
                    "uuid": "pumo-n53j-fcn6-28vc-z8"
                }
            ],
            "key": "vc99-z7j2-f2cj-hlou-6e",
            "uuid": "vc99-z7j2-f2cj-hlou-6e",
            "keyPath": "vc99-z7j2-f2cj-hlou-6e",
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
                "dsUUID": "b5hm-zg5j-93pt-mk7x-3m",
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
                        "connId": "z9xhn",
                        "classifier": "db.workflow",
                        "datasourceName": "SampleTravelDataDerby",
                        "dsKeyPath": "er7y-mks4-nbek-qhw8-4e/b5hm-zg5j-93pt-mk7x-3m/7cjy-7cxg-bp4a-62e1-8q",
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
            "connId": "z9xhn",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelDataDerby",
            "dsKeyPath": "er7y-mks4-nbek-qhw8-4e/b5hm-zg5j-93pt-mk7x-3m/7cjy-7cxg-bp4a-62e1-8q",
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
            "keyPath": "er7y-mks4-nbek-qhw8-4e/b5hm-zg5j-93pt-mk7x-3m/7cjy-7cxg-bp4a-62e1-8q/lnbw-8h6j-55sa-y6bz-3x",
            "key": "lnbw-8h6j-55sa-y6bz-3x",
            "alias": "geo_cordinates",
            "uuid": "lnbw-8h6j-55sa-y6bz-3x",
            "connId": "z9xhn",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "z9xhn",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "er7y-mks4-nbek-qhw8-4e/b5hm-zg5j-93pt-mk7x-3m/7cjy-7cxg-bp4a-62e1-8q",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "geo_cordinates_z9xhn",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "geo_cordinates",
            "columns": {
                "location_id": {
                    "alias": "location_id",
                    "fullyQualifiedColumn": "geo_cordinates.location_id",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    },
                    "category": "column",
                    "originalName": "location_id",
                    "originalId": "1788dbff-4b08-4d99-b8cb-c9edf051d2e5",
                    "tableKey": "geo_cordinates",
                    "connId": "z9xhn",
                    "duplicateIndex": 0,
                    "columnKey": "location_id"
                },
                "location": {
                    "alias": "location",
                    "fullyQualifiedColumn": "geo_cordinates.location",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "originalName": "location",
                    "originalId": "6abbc3c5-f00e-4b6f-907b-ef96a8da64bd",
                    "tableKey": "geo_cordinates",
                    "connId": "z9xhn",
                    "duplicateIndex": 0,
                    "columnKey": "location"
                },
                "latitude": {
                    "alias": "latitude",
                    "fullyQualifiedColumn": "geo_cordinates.latitude",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Double": "numeric"
                    },
                    "category": "column",
                    "originalName": "latitude",
                    "originalId": "88c00c69-661d-4505-a1a8-c031e85302ea",
                    "tableKey": "geo_cordinates",
                    "connId": "z9xhn",
                    "duplicateIndex": 0,
                    "columnKey": "latitude"
                },
                "longitude": {
                    "alias": "longitude",
                    "fullyQualifiedColumn": "geo_cordinates.longitude",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Double": "numeric"
                    },
                    "category": "column",
                    "originalName": "longitude",
                    "originalId": "d2776de5-5718-4b0e-ab80-8d5296a8fa60",
                    "tableKey": "geo_cordinates",
                    "connId": "z9xhn",
                    "duplicateIndex": 0,
                    "columnKey": "longitude"
                }
            },
            "columnsFetched": true
        },
        "meeting_details": {
            "id": "9645c648a1c0dbeec1287aaf1e996db3",
            "name": "meeting_details",
            "data": {
                "id": "1",
                "type": "dynamicDataSource"
            },
            "keyPath": "er7y-mks4-nbek-qhw8-4e/b5hm-zg5j-93pt-mk7x-3m/7cjy-7cxg-bp4a-62e1-8q/5gf3-pvz3-gsy6-slad-8c",
            "key": "5gf3-pvz3-gsy6-slad-8c",
            "alias": "meeting_details",
            "uuid": "5gf3-pvz3-gsy6-slad-8c",
            "connId": "z9xhn",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "z9xhn",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "er7y-mks4-nbek-qhw8-4e/b5hm-zg5j-93pt-mk7x-3m/7cjy-7cxg-bp4a-62e1-8q",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "meeting_details_z9xhn",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "meeting_details",
            "columns": {
                "meeting_id": {
                    "alias": "meeting_id",
                    "fullyQualifiedColumn": "meeting_details.meeting_id",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    },
                    "category": "column",
                    "originalName": "meeting_id",
                    "originalId": "dff5938e-eb78-410c-ad57-145ed3fa4500",
                    "tableKey": "meeting_details",
                    "connId": "z9xhn",
                    "duplicateIndex": 0,
                    "columnKey": "meeting_id"
                },
                "meeting_date": {
                    "alias": "meeting_date",
                    "fullyQualifiedColumn": "meeting_details.meeting_date",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.sql.Timestamp": "dateTime"
                    },
                    "category": "column",
                    "originalName": "meeting_date",
                    "originalId": "1292750c-f0f5-46b4-9524-924948d3e06b",
                    "tableKey": "meeting_details",
                    "connId": "z9xhn",
                    "duplicateIndex": 0,
                    "columnKey": "meeting_date"
                },
                "meeting_by": {
                    "alias": "meeting_by",
                    "fullyQualifiedColumn": "meeting_details.meeting_by",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    },
                    "category": "column",
                    "originalName": "meeting_by",
                    "originalId": "74f064fa-1639-4555-9b37-c29658775598",
                    "tableKey": "meeting_details",
                    "connId": "z9xhn",
                    "duplicateIndex": 0,
                    "columnKey": "meeting_by"
                },
                "client_name": {
                    "alias": "client_name",
                    "fullyQualifiedColumn": "meeting_details.client_name",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "originalName": "client_name",
                    "originalId": "5697901d-3bbc-4312-af78-74221421a90c",
                    "tableKey": "meeting_details",
                    "connId": "z9xhn",
                    "duplicateIndex": 0,
                    "columnKey": "client_name"
                },
                "meeting_purpose": {
                    "alias": "meeting_purpose",
                    "fullyQualifiedColumn": "meeting_details.meeting_purpose",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "originalName": "meeting_purpose",
                    "originalId": "01026ef3-56b7-4503-b966-2e0f1e776844",
                    "tableKey": "meeting_details",
                    "connId": "z9xhn",
                    "duplicateIndex": 0,
                    "columnKey": "meeting_purpose"
                },
                "meeting_impact": {
                    "alias": "meeting_impact",
                    "fullyQualifiedColumn": "meeting_details.meeting_impact",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "originalName": "meeting_impact",
                    "originalId": "ccf7994c-6fec-491a-bc0d-bcb124d6f1ae",
                    "tableKey": "meeting_details",
                    "connId": "z9xhn",
                    "duplicateIndex": 0,
                    "columnKey": "meeting_impact"
                },
                "meet_cancellation_status": {
                    "alias": "meet_cancellation_status",
                    "fullyQualifiedColumn": "meeting_details.meet_cancellation_status",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "originalName": "meet_cancellation_status",
                    "originalId": "7a5f76d7-f365-445c-a228-927c87702c39",
                    "tableKey": "meeting_details",
                    "connId": "z9xhn",
                    "duplicateIndex": 0,
                    "columnKey": "meet_cancellation_status"
                },
                "cancellation_reason": {
                    "alias": "cancellation_reason",
                    "fullyQualifiedColumn": "meeting_details.cancellation_reason",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "originalName": "cancellation_reason",
                    "originalId": "caff99b5-29f6-48eb-9f63-372cc3d7b497",
                    "tableKey": "meeting_details",
                    "connId": "z9xhn",
                    "duplicateIndex": 0,
                    "columnKey": "cancellation_reason"
                }
            },
            "columnsFetched": true
        },
        "employee_details": {
            "id": "4e1fd245f4d13b77be423a43f01d80b2",
            "name": "employee_details",
            "data": {
                "id": "1",
                "type": "dynamicDataSource"
            },
            "keyPath": "er7y-mks4-nbek-qhw8-4e/b5hm-zg5j-93pt-mk7x-3m/7cjy-7cxg-bp4a-62e1-8q/gpyk-48d0-6fgb-i4lh-ev",
            "key": "gpyk-48d0-6fgb-i4lh-ev",
            "alias": "employee_details",
            "uuid": "gpyk-48d0-6fgb-i4lh-ev",
            "connId": "z9xhn",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "z9xhn",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "er7y-mks4-nbek-qhw8-4e/b5hm-zg5j-93pt-mk7x-3m/7cjy-7cxg-bp4a-62e1-8q",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "employee_details_z9xhn",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "employee_details",
            "columns": {
                "employee_id": {
                    "alias": "employee_id",
                    "fullyQualifiedColumn": "employee_details.employee_id",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    },
                    "category": "column",
                    "originalName": "employee_id",
                    "originalId": "4c10d3ea-167a-464d-b9fd-ab0a9028ad31",
                    "tableKey": "employee_details",
                    "connId": "z9xhn",
                    "duplicateIndex": 0,
                    "columnKey": "employee_id"
                },
                "employee_name": {
                    "alias": "employee_name",
                    "fullyQualifiedColumn": "employee_details.employee_name",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "originalName": "employee_name",
                    "originalId": "7bf280a2-2a69-4e9a-a285-306d75012b93",
                    "tableKey": "employee_details",
                    "connId": "z9xhn",
                    "duplicateIndex": 0,
                    "columnKey": "employee_name"
                },
                "age": {
                    "alias": "age",
                    "fullyQualifiedColumn": "employee_details.age",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    },
                    "category": "column",
                    "originalName": "age",
                    "originalId": "b930d4ad-070a-4972-8c32-8b8398f99a7e",
                    "tableKey": "employee_details",
                    "connId": "z9xhn",
                    "duplicateIndex": 0,
                    "columnKey": "age"
                },
                "address": {
                    "alias": "address",
                    "fullyQualifiedColumn": "employee_details.address",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "originalName": "address",
                    "originalId": "4e574cad-7252-4907-923d-e42ad2b1511a",
                    "tableKey": "employee_details",
                    "connId": "z9xhn",
                    "duplicateIndex": 0,
                    "columnKey": "address"
                }
            },
            "columnsFetched": true
        },
        "dimdate": {
            "id": "4ac5d9f68b58bd7c0d179146e46795be",
            "name": "dimdate",
            "data": {
                "id": "1",
                "type": "dynamicDataSource"
            },
            "keyPath": "er7y-mks4-nbek-qhw8-4e/b5hm-zg5j-93pt-mk7x-3m/7cjy-7cxg-bp4a-62e1-8q/yly3-pl8y-pgqm-5h1g-b2",
            "key": "yly3-pl8y-pgqm-5h1g-b2",
            "alias": "dimdate",
            "uuid": "yly3-pl8y-pgqm-5h1g-b2",
            "connId": "z9xhn",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "z9xhn",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "er7y-mks4-nbek-qhw8-4e/b5hm-zg5j-93pt-mk7x-3m/7cjy-7cxg-bp4a-62e1-8q",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "dimdate_z9xhn",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "dimdate",
            "columns": {
                "dim_id": {
                    "alias": "dim_id",
                    "fullyQualifiedColumn": "dimdate.dim_id",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    },
                    "category": "column",
                    "originalName": "dim_id",
                    "originalId": "56692a4c-0d81-4652-96c3-525aeb723fac",
                    "tableKey": "dimdate",
                    "connId": "z9xhn",
                    "duplicateIndex": 0,
                    "columnKey": "dim_id"
                },
                "fiscal_year": {
                    "alias": "fiscal_year",
                    "fullyQualifiedColumn": "dimdate.fiscal_year",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.sql.Date": "date"
                    },
                    "category": "column",
                    "originalName": "fiscal_year",
                    "originalId": "e8c6b258-ea2a-4c24-b8ac-2df05ff6bf8f",
                    "tableKey": "dimdate",
                    "connId": "z9xhn",
                    "duplicateIndex": 0,
                    "columnKey": "fiscal_year"
                },
                "modified_date": {
                    "alias": "modified_date",
                    "fullyQualifiedColumn": "dimdate.modified_date",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.sql.Timestamp": "dateTime"
                    },
                    "category": "column",
                    "originalName": "modified_date",
                    "originalId": "5e4f525f-176c-4722-90e6-96dec4957e46",
                    "tableKey": "dimdate",
                    "connId": "z9xhn",
                    "duplicateIndex": 0,
                    "columnKey": "modified_date"
                },
                "date_key": {
                    "alias": "date_key",
                    "fullyQualifiedColumn": "dimdate.date_key",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "originalName": "date_key",
                    "originalId": "ff9261dc-fcce-4b49-9d31-dbc3c5db8c9c",
                    "tableKey": "dimdate",
                    "connId": "z9xhn",
                    "duplicateIndex": 0,
                    "columnKey": "date_key"
                },
                "day_number": {
                    "alias": "day_number",
                    "fullyQualifiedColumn": "dimdate.day_number",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "originalName": "day_number",
                    "originalId": "8ecc0b24-3b70-4dd1-b616-218d0955ae90",
                    "tableKey": "dimdate",
                    "connId": "z9xhn",
                    "duplicateIndex": 0,
                    "columnKey": "day_number"
                },
                "fiscal_month_name": {
                    "alias": "fiscal_month_name",
                    "fullyQualifiedColumn": "dimdate.fiscal_month_name",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "originalName": "fiscal_month_name",
                    "originalId": "92245d80-8743-4749-807a-cf986e66e6f5",
                    "tableKey": "dimdate",
                    "connId": "z9xhn",
                    "duplicateIndex": 0,
                    "columnKey": "fiscal_month_name"
                },
                "fiscal_month_label": {
                    "alias": "fiscal_month_label",
                    "fullyQualifiedColumn": "dimdate.fiscal_month_label",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "originalName": "fiscal_month_label",
                    "originalId": "7bb5ed45-0632-478c-9071-9f1aca31b749",
                    "tableKey": "dimdate",
                    "connId": "z9xhn",
                    "duplicateIndex": 0,
                    "columnKey": "fiscal_month_label"
                },
                "created_date": {
                    "alias": "created_date",
                    "fullyQualifiedColumn": "dimdate.created_date",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "originalName": "created_date",
                    "originalId": "beec622b-7e1b-4ef5-bedc-33ae6d8b459f",
                    "tableKey": "dimdate",
                    "connId": "z9xhn",
                    "duplicateIndex": 0,
                    "columnKey": "created_date"
                },
                "created_time": {
                    "alias": "created_time",
                    "fullyQualifiedColumn": "dimdate.created_time",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "originalName": "created_time",
                    "originalId": "47bd4f83-1bb8-4cfa-8b14-e7d38801c62c",
                    "tableKey": "dimdate",
                    "connId": "z9xhn",
                    "duplicateIndex": 0,
                    "columnKey": "created_time"
                },
                "rating": {
                    "alias": "rating",
                    "fullyQualifiedColumn": "dimdate.rating",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "originalName": "rating",
                    "originalId": "47d19691-1422-4fb0-a8f1-272a9f8218df",
                    "tableKey": "dimdate",
                    "connId": "z9xhn",
                    "duplicateIndex": 0,
                    "columnKey": "rating"
                }
            },
            "columnsFetched": true
        },
        "travel_details": {
            "id": "8a28627d07d04ef096d9935f12e0c7e9",
            "name": "travel_details",
            "data": {
                "id": "1",
                "type": "dynamicDataSource"
            },
            "keyPath": "er7y-mks4-nbek-qhw8-4e/b5hm-zg5j-93pt-mk7x-3m/7cjy-7cxg-bp4a-62e1-8q/zd96-6c7u-punh-2at4-yr",
            "key": "zd96-6c7u-punh-2at4-yr",
            "alias": "travel_details",
            "uuid": "zd96-6c7u-punh-2at4-yr",
            "connId": "z9xhn",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "z9xhn",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "er7y-mks4-nbek-qhw8-4e/b5hm-zg5j-93pt-mk7x-3m/7cjy-7cxg-bp4a-62e1-8q",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "travel_details_z9xhn",
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
                    "originalId": "7c1cc79e-3203-4332-a042-345f116e50cc",
                    "tableKey": "travel_details",
                    "connId": "z9xhn",
                    "duplicateIndex": 0,
                    "columnKey": "travel_id"
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
                    "originalId": "c18188cb-89ef-4a8d-94e2-111a1f9e6b64",
                    "tableKey": "travel_details",
                    "connId": "z9xhn",
                    "duplicateIndex": 0,
                    "columnKey": "travel_date"
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
                    "originalId": "a7739e81-9ffc-420b-9a10-e0e6b1bb2ea2",
                    "tableKey": "travel_details",
                    "connId": "z9xhn",
                    "duplicateIndex": 0,
                    "columnKey": "travel_type"
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
                    "originalId": "5e238de2-4c61-4672-b5ce-a629208bb495",
                    "tableKey": "travel_details",
                    "connId": "z9xhn",
                    "duplicateIndex": 0,
                    "columnKey": "travel_medium"
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
                    "originalId": "4ce15085-67f2-4038-8a1a-68597b6a3ee3",
                    "tableKey": "travel_details",
                    "connId": "z9xhn",
                    "duplicateIndex": 0,
                    "columnKey": "source_id"
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
                    "originalId": "3eda6a68-ed53-4ae2-898a-ef83f35d6b6a",
                    "tableKey": "travel_details",
                    "connId": "z9xhn",
                    "duplicateIndex": 0,
                    "columnKey": "source"
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
                    "originalId": "bb650022-743e-4ffd-ae21-38edbca8092f",
                    "tableKey": "travel_details",
                    "connId": "z9xhn",
                    "duplicateIndex": 0,
                    "columnKey": "destination_id"
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
                    "originalId": "bfdc49c3-852b-4d5f-87dd-22119ccda84d",
                    "tableKey": "travel_details",
                    "connId": "z9xhn",
                    "duplicateIndex": 0,
                    "columnKey": "destination"
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
                    "originalId": "ad0cafd9-8265-4844-97fd-e96b2aac9925",
                    "tableKey": "travel_details",
                    "connId": "z9xhn",
                    "duplicateIndex": 0,
                    "columnKey": "travel_cost"
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
                    "originalId": "c5a4fa44-8c74-4829-ada9-ffabeba2de2e",
                    "tableKey": "travel_details",
                    "connId": "z9xhn",
                    "duplicateIndex": 0,
                    "columnKey": "mode_of_payment"
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
                    "originalId": "e54cfa6f-6bf0-4ac2-be12-0c88f6b58577",
                    "tableKey": "travel_details",
                    "connId": "z9xhn",
                    "duplicateIndex": 0,
                    "columnKey": "booking_platform"
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
                    "originalId": "5b153c28-0fea-415b-93c0-baa0066e744a",
                    "tableKey": "travel_details",
                    "connId": "z9xhn",
                    "duplicateIndex": 0,
                    "columnKey": "travelled_by"
                }
            },
            "columnsFetched": true
        }
    },
    "categories": {
        "er7y-mks4-nbek-qhw8-4e": {
            "ds": {
                "name": "SampleEFWD Sample Travel Data",
                "type": "sql.jdbc",
                "driver": "org.apache.derby.jdbc.AutoloadedDriver",
                "dataSourceType": "Plain Jdbc DataSource",
                "classifier": "efwd",
                "permissionLevel": 2,
                "data": {
                    "id": "1",
                    "dir": "1463377807724/1463377978248/Sample EFW Report",
                    "type": "sql.jdbc",
                    "driver": "org.apache.derby.jdbc.AutoloadedDriver"
                }
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
        "vc99-z7j2-f2cj-hlou-6e": {
            "ds": {
                "name": "chinook",
                "driver": "org.sqlite.JDBC",
                "dataSourceType": "Managed DataSource",
                "classifier": "global",
                "type": "dynamicDataSource",
                "data": {
                    "id": "3",
                    "type": "dynamicDataSource"
                },
                "permissionLevel": 5,
                "dataSourceProvider": "tomcat"
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
            "id": "1",
            "type": "dynamicDataSource",
            "baseType": "global.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "",
            "schema": "HIUSER",
            "connId": "z9xhn",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelDataDerby",
            "dsKeyPath": "er7y-mks4-nbek-qhw8-4e/b5hm-zg5j-93pt-mk7x-3m/7cjy-7cxg-bp4a-62e1-8q",
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
        "location": "1463377807724/1463377836985",
        "uuid": "e9be6771-995b-40eb-a01c-304857a100a1.metadata"
    },
    "savedTableIds": [
        "9645c648a1c0dbeec1287aaf1e996db3",
        "8a28627d07d04ef096d9935f12e0c7e9",
        "4ac5d9f68b58bd7c0d179146e46795be",
        "4e1fd245f4d13b77be423a43f01d80b2",
        "be534112989b616b194bc59c2fb25a42"
    ],
    "savedColumnIds": [],
    "joins": [],
    "mode": "edit",
    "allTablesKeys": [
        "lnbw-8h6j-55sa-y6bz-3x",
        "5gf3-pvz3-gsy6-slad-8c",
        "gpyk-48d0-6fgb-i4lh-ev",
        "yly3-pl8y-pgqm-5h1g-b2",
        "zd96-6c7u-punh-2at4-yr"
    ],
    "selectedTableKeys": [
        "5gf3-pvz3-gsy6-slad-8c",
        "zd96-6c7u-punh-2at4-yr",
        "yly3-pl8y-pgqm-5h1g-b2",
        "gpyk-48d0-6fgb-i4lh-ev",
        "lnbw-8h6j-55sa-y6bz-3x"
    ],
    "metadataName": "Sample Travel MD",
    "activeDataSource": false,
    "metadataToEdit": false,
    "isSavingInProgress": false
}