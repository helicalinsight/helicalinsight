export const store4529_1 = {
    "dataFetchedFor": {
        "getDatasource": true,
        "joins": true,
        "listDataSources": true,
        "k32z-z16v-8fdj-xjnb-63": false,
        "pa5l-k4a1-qtuu-g097-cd": false
    },
    "loadingStatus": {
        "getDatasource": true,
        "listDataSources": true,
        "joins": true,
        "k32z-z16v-8fdj-xjnb-63": false,
        "pa5l-k4a1-qtuu-g097-cd": false
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
                            "keyPath": "ww0e-wwmz-kct6-hvlu-n1/k32z-z16v-8fdj-xjnb-63/p3yv-vmii-zgk6-51hm-n7",
                            "key": "p3yv-vmii-zgk6-51hm-n7",
                            "uuid": "p3yv-vmii-zgk6-51hm-n7",
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
                            "keyPath": "ww0e-wwmz-kct6-hvlu-n1/k32z-z16v-8fdj-xjnb-63/1qk7-n212-gfy1-qgs8-st",
                            "key": "1qk7-n212-gfy1-qgs8-st",
                            "uuid": "1qk7-n212-gfy1-qgs8-st",
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
                            "keyPath": "ww0e-wwmz-kct6-hvlu-n1/k32z-z16v-8fdj-xjnb-63/qvpi-nruz-cssh-oyw8-9n",
                            "key": "qvpi-nruz-cssh-oyw8-9n",
                            "uuid": "qvpi-nruz-cssh-oyw8-9n",
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
                                    "keyPath": "ww0e-wwmz-kct6-hvlu-n1/k32z-z16v-8fdj-xjnb-63/pa5l-k4a1-qtuu-g097-cd/bldy-0129-clkd-nw96-of",
                                    "key": "bldy-0129-clkd-nw96-of",
                                    "alias": "geo_cordinates",
                                    "uuid": "bldy-0129-clkd-nw96-of",
                                    "connId": "k2cef",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "k2cef",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "ww0e-wwmz-kct6-hvlu-n1/k32z-z16v-8fdj-xjnb-63/pa5l-k4a1-qtuu-g097-cd",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "geo_cordinates_k2cef",
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
                                    "keyPath": "ww0e-wwmz-kct6-hvlu-n1/k32z-z16v-8fdj-xjnb-63/pa5l-k4a1-qtuu-g097-cd/j82p-98iz-mo9b-xa6e-p7",
                                    "key": "j82p-98iz-mo9b-xa6e-p7",
                                    "alias": "meeting_details",
                                    "uuid": "j82p-98iz-mo9b-xa6e-p7",
                                    "connId": "k2cef",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "k2cef",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "ww0e-wwmz-kct6-hvlu-n1/k32z-z16v-8fdj-xjnb-63/pa5l-k4a1-qtuu-g097-cd",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "meeting_details_k2cef",
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
                                    "keyPath": "ww0e-wwmz-kct6-hvlu-n1/k32z-z16v-8fdj-xjnb-63/pa5l-k4a1-qtuu-g097-cd/vhb4-gar5-rsr7-basy-15",
                                    "key": "vhb4-gar5-rsr7-basy-15",
                                    "alias": "employee_details",
                                    "uuid": "vhb4-gar5-rsr7-basy-15",
                                    "connId": "k2cef",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "k2cef",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "ww0e-wwmz-kct6-hvlu-n1/k32z-z16v-8fdj-xjnb-63/pa5l-k4a1-qtuu-g097-cd",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "employee_details_k2cef",
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
                                    "keyPath": "ww0e-wwmz-kct6-hvlu-n1/k32z-z16v-8fdj-xjnb-63/pa5l-k4a1-qtuu-g097-cd/ufhr-w4o9-m5v6-tdlc-6a",
                                    "key": "ufhr-w4o9-m5v6-tdlc-6a",
                                    "alias": "dimdate",
                                    "uuid": "ufhr-w4o9-m5v6-tdlc-6a",
                                    "connId": "k2cef",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "k2cef",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "ww0e-wwmz-kct6-hvlu-n1/k32z-z16v-8fdj-xjnb-63/pa5l-k4a1-qtuu-g097-cd",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "dimdate_k2cef",
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
                                    "keyPath": "ww0e-wwmz-kct6-hvlu-n1/k32z-z16v-8fdj-xjnb-63/pa5l-k4a1-qtuu-g097-cd/x2fs-xnnf-tzmt-qywa-mh",
                                    "key": "x2fs-xnnf-tzmt-qywa-mh",
                                    "alias": "travel_details",
                                    "uuid": "x2fs-xnnf-tzmt-qywa-mh",
                                    "connId": "k2cef",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "k2cef",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "ww0e-wwmz-kct6-hvlu-n1/k32z-z16v-8fdj-xjnb-63/pa5l-k4a1-qtuu-g097-cd",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "travel_details_k2cef",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": true
                                }
                            ],
                            "keyPath": "ww0e-wwmz-kct6-hvlu-n1/k32z-z16v-8fdj-xjnb-63/pa5l-k4a1-qtuu-g097-cd",
                            "key": "pa5l-k4a1-qtuu-g097-cd",
                            "uuid": "pa5l-k4a1-qtuu-g097-cd",
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
                            "keyPath": "ww0e-wwmz-kct6-hvlu-n1/k32z-z16v-8fdj-xjnb-63/s936-t8g0-j6it-ybhj-86",
                            "key": "s936-t8g0-j6it-ybhj-86",
                            "uuid": "s936-t8g0-j6it-ybhj-86",
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
                            "keyPath": "ww0e-wwmz-kct6-hvlu-n1/k32z-z16v-8fdj-xjnb-63/s3y4-tfu4-qjhj-2zot-to",
                            "key": "s3y4-tfu4-qjhj-2zot-to",
                            "uuid": "s3y4-tfu4-qjhj-2zot-to",
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
                            "keyPath": "ww0e-wwmz-kct6-hvlu-n1/k32z-z16v-8fdj-xjnb-63/ez7t-gux7-x2qt-ohx9-ez",
                            "key": "ez7t-gux7-x2qt-ohx9-ez",
                            "uuid": "ez7t-gux7-x2qt-ohx9-ez",
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
                            "keyPath": "ww0e-wwmz-kct6-hvlu-n1/k32z-z16v-8fdj-xjnb-63/e9c0-r268-oby7-39xo-lg",
                            "key": "e9c0-r268-oby7-39xo-lg",
                            "uuid": "e9c0-r268-oby7-39xo-lg",
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
                            "keyPath": "ww0e-wwmz-kct6-hvlu-n1/k32z-z16v-8fdj-xjnb-63/vx96-vfh4-556d-hawe-8n",
                            "key": "vx96-vfh4-556d-hawe-8n",
                            "uuid": "vx96-vfh4-556d-hawe-8n",
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
                            "keyPath": "ww0e-wwmz-kct6-hvlu-n1/k32z-z16v-8fdj-xjnb-63/163r-9ry7-a5l6-rll5-qm",
                            "key": "163r-9ry7-a5l6-rll5-qm",
                            "uuid": "163r-9ry7-a5l6-rll5-qm",
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
                            "keyPath": "ww0e-wwmz-kct6-hvlu-n1/k32z-z16v-8fdj-xjnb-63/2z0i-xmwr-0q04-jzzj-07",
                            "key": "2z0i-xmwr-0q04-jzzj-07",
                            "uuid": "2z0i-xmwr-0q04-jzzj-07",
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
                            "keyPath": "ww0e-wwmz-kct6-hvlu-n1/k32z-z16v-8fdj-xjnb-63/b15j-u5rp-a95y-99ul-15",
                            "key": "b15j-u5rp-a95y-99ul-15",
                            "uuid": "b15j-u5rp-a95y-99ul-15",
                            "data": {
                                "id": "1",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "SampleTravelDataDerby"
                        }
                    ],
                    "driverType": "Derby",
                    "keyPath": "ww0e-wwmz-kct6-hvlu-n1/k32z-z16v-8fdj-xjnb-63",
                    "key": "k32z-z16v-8fdj-xjnb-63",
                    "uuid": "k32z-z16v-8fdj-xjnb-63",
                    "fetched": true
                }
            ],
            "key": "ww0e-wwmz-kct6-hvlu-n1",
            "uuid": "ww0e-wwmz-kct6-hvlu-n1",
            "keyPath": "ww0e-wwmz-kct6-hvlu-n1",
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
                "dsUUID": "k32z-z16v-8fdj-xjnb-63",
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
                        "connId": "k2cef",
                        "classifier": "db.workflow",
                        "datasourceName": "SampleTravelDataDerby",
                        "dsKeyPath": "ww0e-wwmz-kct6-hvlu-n1/k32z-z16v-8fdj-xjnb-63/pa5l-k4a1-qtuu-g097-cd",
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
            "connId": "k2cef",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelDataDerby",
            "dsKeyPath": "ww0e-wwmz-kct6-hvlu-n1/k32z-z16v-8fdj-xjnb-63/pa5l-k4a1-qtuu-g097-cd",
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
            "keyPath": "ww0e-wwmz-kct6-hvlu-n1/k32z-z16v-8fdj-xjnb-63/pa5l-k4a1-qtuu-g097-cd/bldy-0129-clkd-nw96-of",
            "key": "bldy-0129-clkd-nw96-of",
            "alias": "geo_cordinates",
            "uuid": "bldy-0129-clkd-nw96-of",
            "connId": "k2cef",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "k2cef",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "ww0e-wwmz-kct6-hvlu-n1/k32z-z16v-8fdj-xjnb-63/pa5l-k4a1-qtuu-g097-cd",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "geo_cordinates_k2cef",
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
                    "originalId": "b9b9c7c8-9679-4e33-9944-d09aa6935810",
                    "tableKey": "geo_cordinates",
                    "connId": "k2cef",
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
                    "originalId": "6244e5bd-392d-494a-8462-5b62e9992492",
                    "tableKey": "geo_cordinates",
                    "connId": "k2cef",
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
                    "originalId": "833b8623-86cf-40de-8e34-df44eafe1f23",
                    "tableKey": "geo_cordinates",
                    "connId": "k2cef",
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
                    "originalId": "acb3574d-8a43-4ee7-bfe2-3ecc0d314e1a",
                    "tableKey": "geo_cordinates",
                    "connId": "k2cef",
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
            "keyPath": "ww0e-wwmz-kct6-hvlu-n1/k32z-z16v-8fdj-xjnb-63/pa5l-k4a1-qtuu-g097-cd/j82p-98iz-mo9b-xa6e-p7",
            "key": "j82p-98iz-mo9b-xa6e-p7",
            "alias": "meeting_details",
            "uuid": "j82p-98iz-mo9b-xa6e-p7",
            "connId": "k2cef",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "k2cef",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "ww0e-wwmz-kct6-hvlu-n1/k32z-z16v-8fdj-xjnb-63/pa5l-k4a1-qtuu-g097-cd",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "meeting_details_k2cef",
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
                    "originalId": "484df51d-5c79-4f3f-abfa-523ab53e233a",
                    "tableKey": "meeting_details",
                    "connId": "k2cef",
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
                    "originalId": "92ff2381-ec81-4a7a-b11d-0e6d337e28a1",
                    "tableKey": "meeting_details",
                    "connId": "k2cef",
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
                    "originalId": "866924a4-8e9b-4be7-9943-bcb589bab597",
                    "tableKey": "meeting_details",
                    "connId": "k2cef",
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
                    "originalId": "07ac3677-79ae-4bc0-ab9a-3c8845d0452f",
                    "tableKey": "meeting_details",
                    "connId": "k2cef",
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
                    "originalId": "b1f8ec3a-1f99-472c-8aec-29b04dff6616",
                    "tableKey": "meeting_details",
                    "connId": "k2cef",
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
                    "originalId": "67593084-d62d-4681-b1ec-8bbf7bda2068",
                    "tableKey": "meeting_details",
                    "connId": "k2cef",
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
                    "originalId": "e2734a0c-7fd1-444e-a2ff-950ec5894913",
                    "tableKey": "meeting_details",
                    "connId": "k2cef",
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
                    "originalId": "d7ff7f79-8cb9-410f-93fa-8365f770dc3c",
                    "tableKey": "meeting_details",
                    "connId": "k2cef",
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
            "keyPath": "ww0e-wwmz-kct6-hvlu-n1/k32z-z16v-8fdj-xjnb-63/pa5l-k4a1-qtuu-g097-cd/vhb4-gar5-rsr7-basy-15",
            "key": "vhb4-gar5-rsr7-basy-15",
            "alias": "employee_details",
            "uuid": "vhb4-gar5-rsr7-basy-15",
            "connId": "k2cef",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "k2cef",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "ww0e-wwmz-kct6-hvlu-n1/k32z-z16v-8fdj-xjnb-63/pa5l-k4a1-qtuu-g097-cd",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "employee_details_k2cef",
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
                    "originalId": "c7cbe2ba-6941-451b-b7bd-8b111f17a325",
                    "tableKey": "employee_details",
                    "connId": "k2cef",
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
                    "originalId": "a69e1ab9-77ad-418f-92a4-42ecf87a0293",
                    "tableKey": "employee_details",
                    "connId": "k2cef",
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
                    "originalId": "d2e624be-8147-4e44-931d-4a7e7583d155",
                    "tableKey": "employee_details",
                    "connId": "k2cef",
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
                    "originalId": "fba5d5ad-85a8-4f7c-8fff-8b24908eac0e",
                    "tableKey": "employee_details",
                    "connId": "k2cef",
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
            "keyPath": "ww0e-wwmz-kct6-hvlu-n1/k32z-z16v-8fdj-xjnb-63/pa5l-k4a1-qtuu-g097-cd/ufhr-w4o9-m5v6-tdlc-6a",
            "key": "ufhr-w4o9-m5v6-tdlc-6a",
            "alias": "dimdate",
            "uuid": "ufhr-w4o9-m5v6-tdlc-6a",
            "connId": "k2cef",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "k2cef",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "ww0e-wwmz-kct6-hvlu-n1/k32z-z16v-8fdj-xjnb-63/pa5l-k4a1-qtuu-g097-cd",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "dimdate_k2cef",
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
                    "originalId": "7f508abe-786a-43aa-a37b-c6cb1e46634a",
                    "tableKey": "dimdate",
                    "connId": "k2cef",
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
                    "originalId": "90b3afa3-fd3c-4a58-9188-96d151c7c24e",
                    "tableKey": "dimdate",
                    "connId": "k2cef",
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
                    "originalId": "dc5d8664-e360-452e-b490-a36b2b73d7bf",
                    "tableKey": "dimdate",
                    "connId": "k2cef",
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
                    "originalId": "ec37a826-e42e-4d3f-894a-4c93d6bd8b95",
                    "tableKey": "dimdate",
                    "connId": "k2cef",
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
                    "originalId": "7feac155-ea4c-4e49-891f-ba300be4ee06",
                    "tableKey": "dimdate",
                    "connId": "k2cef",
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
                    "originalId": "d5c6bc45-3d4a-4610-a2e0-b929d3d11c35",
                    "tableKey": "dimdate",
                    "connId": "k2cef",
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
                    "originalId": "957643f2-3557-4380-a4e8-895647457004",
                    "tableKey": "dimdate",
                    "connId": "k2cef",
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
                    "originalId": "447ba05f-b049-4785-8140-7385d6e9914d",
                    "tableKey": "dimdate",
                    "connId": "k2cef",
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
                    "originalId": "7385de93-f654-4181-994f-0abd3b8b2914",
                    "tableKey": "dimdate",
                    "connId": "k2cef",
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
                    "originalId": "97d936ae-07dd-41e5-b2ee-def064e804cb",
                    "tableKey": "dimdate",
                    "connId": "k2cef",
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
            "keyPath": "ww0e-wwmz-kct6-hvlu-n1/k32z-z16v-8fdj-xjnb-63/pa5l-k4a1-qtuu-g097-cd/x2fs-xnnf-tzmt-qywa-mh",
            "key": "x2fs-xnnf-tzmt-qywa-mh",
            "alias": "travel_details",
            "uuid": "x2fs-xnnf-tzmt-qywa-mh",
            "connId": "k2cef",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "k2cef",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "ww0e-wwmz-kct6-hvlu-n1/k32z-z16v-8fdj-xjnb-63/pa5l-k4a1-qtuu-g097-cd",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "travel_details_k2cef",
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
                    "originalId": "adfbb6c2-1c35-4297-a6f2-3ef40007ba31",
                    "tableKey": "travel_details",
                    "connId": "k2cef",
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
                    "originalId": "df729937-e5d7-47f9-81f4-db3a9f0a875e",
                    "tableKey": "travel_details",
                    "connId": "k2cef",
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
                    "originalId": "59bbc862-e98a-4c34-b017-8f1dfcb5a4ec",
                    "tableKey": "travel_details",
                    "connId": "k2cef",
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
                    "originalId": "2ff70fc6-3f52-4a9c-8074-fbc2809f1836",
                    "tableKey": "travel_details",
                    "connId": "k2cef",
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
                    "originalId": "254ae171-3d10-4210-ac6f-317af36d4e24",
                    "tableKey": "travel_details",
                    "connId": "k2cef",
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
                    "originalId": "3e0c2583-4554-40bb-a18a-8f9b3f5a854f",
                    "tableKey": "travel_details",
                    "connId": "k2cef",
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
                    "originalId": "a9914a13-97f5-43b9-bf20-9296abcbb8c2",
                    "tableKey": "travel_details",
                    "connId": "k2cef",
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
                    "originalId": "76ee045f-7968-45e1-82e5-d71b4da24330",
                    "tableKey": "travel_details",
                    "connId": "k2cef",
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
                    "originalId": "062b62ad-cc24-40eb-ba4e-fd8a0452252c",
                    "tableKey": "travel_details",
                    "connId": "k2cef",
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
                    "originalId": "fac582d7-8aa7-42ff-b3f5-f8fa7b44d113",
                    "tableKey": "travel_details",
                    "connId": "k2cef",
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
                    "originalId": "aa0dde37-5bd9-47cd-ad79-9ecd09a6df78",
                    "tableKey": "travel_details",
                    "connId": "k2cef",
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
                    "originalId": "e557c5b9-8647-4efb-957c-f46846049d84",
                    "tableKey": "travel_details",
                    "connId": "k2cef",
                    "duplicateIndex": 0,
                    "columnKey": "travelled_by"
                }
            },
            "columnsFetched": true
        }
    },
    "categories": {
        "ww0e-wwmz-kct6-hvlu-n1": {
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
            "connId": "k2cef",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelDataDerby",
            "dsKeyPath": "ww0e-wwmz-kct6-hvlu-n1/k32z-z16v-8fdj-xjnb-63/pa5l-k4a1-qtuu-g097-cd",
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
        "uuid": "4529 1.metadata"
    },
    "savedTableIds": [
        "be534112989b616b194bc59c2fb25a42",
        "9645c648a1c0dbeec1287aaf1e996db3",
        "4e1fd245f4d13b77be423a43f01d80b2",
        "4ac5d9f68b58bd7c0d179146e46795be",
        "8a28627d07d04ef096d9935f12e0c7e9"
    ],
    "savedColumnIds": [],
    "joins": [
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
            "index": 1,
            "action": "noChange",
            "uuid": "ath2-krzg-fn6m-ufwo-0n"
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
            "index": 2,
            "action": "noChange",
            "uuid": "gsv0-9s2i-5rue-pewa-5l"
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
            "uuid": "d7sx-9500-p4n0-c11n-c2"
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
            "uuid": "hvwu-kxle-43oj-apkd-t9"
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
            "uuid": "535g-2702-43hy-9sa2-tq"
        }
    ],
    "mode": "edit",
    "allTablesKeys": [
        "bldy-0129-clkd-nw96-of",
        "j82p-98iz-mo9b-xa6e-p7",
        "vhb4-gar5-rsr7-basy-15",
        "ufhr-w4o9-m5v6-tdlc-6a",
        "x2fs-xnnf-tzmt-qywa-mh"
    ],
    "selectedTableKeys": [
        "bldy-0129-clkd-nw96-of",
        "j82p-98iz-mo9b-xa6e-p7",
        "vhb4-gar5-rsr7-basy-15",
        "ufhr-w4o9-m5v6-tdlc-6a",
        "x2fs-xnnf-tzmt-qywa-mh"
    ],
    "metadataName": "4529 1",
    "activeDataSource": false,
    "metadataToEdit": false,
    "isSavingInProgress": false,
    "inititalStateFromJest": false
}
export const app4529_2 = {
    "value": true,
    "isAuthenticated": true,
    "isLogoutManually": false,
    "hasError": false,
    "applicationSettingsData": {
        "userData": {
            "serverTime": 1652177691306,
            "expiryTime": 1652179491306,
            "sessionUserName": "hiadmin",
            "sessionUserEmail": "admin@helicalinsight.com",
            "user": {
                "name": "hiadmin",
                "email": "admin@helicalinsight.com",
                "actualUserName": "hiadmin",
                "roles": [
                    "ROLE_ADMIN",
                    "ROLE_USER",
                    "ROLE_VIEWER"
                ],
                "profile": []
            },
            "rootDirectoryPermission": "4",
            "provideHTMLExport": false,
            "enableReportSave": "false",
            "defaultEmailResourceType": "url",
            "saml": {
                "enabled": false,
                "logoutLink": "/saml/logout/?"
            },
            "baseUrl": "http://127.0.0.1:7085/hi-ee/"
        },
        "contentId": "Static/DashboardGlobals",
        "settings": {
            "jwtToken": null,
            "adminPaths": {
                "users": "admin/users",
                "profiles": "admin/profiles",
                "roles": "admin/roles",
                "organisations": "admin/organisations",
                "services": "services.html"
            },
            "services": "services",
            "optionalReportParams": {},
            "recursiveDirectoryLoad": false,
            "HDI": {
                "adhoc": {
                    "urls": {
                        "services": "/services",
                        "createDataSource": "/createDataSource",
                        "listDataSources": "/listDataSources",
                        "listLocations": "/listLocations",
                        "getResources": "/getResources",
                        "adhocReport": "/adhocReport"
                    }
                }
            },
            "DashboardGlobals": {
                "solutionLoader": "getSolutionResources",
                "resourceLoader": "/getEFWSolution.html",
                "updateService": "/executeDatasource.html",
                "chartingService": "/visualizeData.html",
                "exportData": "/exportData.html",
                "reportDownload": "/downloadReport.html",
                "productInfo": "getProductInformation.html",
                "sendMail": "/sendMail.html",
                "updateEFWTemplate": "/updateEFWTemplate.html",
                "openHcr ": "/hcr-report.html",
                "editHcr ": "/hcr-report-edit.html",
                "controllers": {
                    "efw": "/getEFWSolution.html",
                    "efwsr": "/executeSavedReport.html",
                    "efwfav": "/executeFavourite.html",
                    "report": "/hi.html"
                },
                "saveReport": "/saveReport.html",
                "fsop": "/fileSystemOperations",
                "importFile": "/importFile.html",
                "downloadEnableSavedReport": "/downloadEnableSavedReport.html",
                "scheduling": {
                    "get": "/getScheduleData.html",
                    "update": "/updateScheduleData.html"
                },
                "services": "/services",
                "designerCreate": "/designer.html",
                "designerEdit": "/designer-edit.html",
                "ceReportCreate": "/ce-report-create.html",
                "ceReportEdit": "/ce-report-edit.html",
                "adhocEdit": "/adhoc/report-edit.html",
                "datasourceCreate": "/adhoc/datasources.html",
                "metadataEdit": "/adhoc/metadata-edit.html",
                "metadataCreate": "/adhoc/metadata-create.html",
                "adhocReportCreate": "/adhoc/report-create.html",
                "helicalReportCreate": "/adhoc/helical-report.html",
                "helicalReportEdit": "/adhoc/helical-report-edit.html",
                "openAdhoc": "/hi.html",
                "openEfw": "/hi.html",
                "visualizeAdhoc": "/visualizeAdhoc.html"
            }
        }
    },
    "routes": [
        {
            "title": "Home",
            "addInNavbar": true,
            "url": "/admin",
            "roles": [
                "ROLE_ADMIN"
            ]
        },
        {
            "title": "Login",
            "url": "/",
            "addInNavbar": false,
            "roles": []
        },
        {
            "title": "Data Sources",
            "url": "/datasource",
            "addInNavbar": true,
            "roles": [
                "ROLE_ADMIN"
            ]
        },
        {
            "title": "Meta Data",
            "url": "/metadata",
            "addInNavbar": true,
            "roles": [
                "ROLE_ADMIN"
            ]
        },
        {
            "title": "Reports",
            "url": "/helical-report",
            "addInNavbar": true,
            "roles": [
                "ROLE_ADMIN",
                "ROLE_USER",
                "ROLE_DOWNLOAD"
            ]
        },
        {
            "title": "Dashboard Designer",
            "url": "/dashboard-designer",
            "addInNavbar": true,
            "roles": [
                "ROLE_ADMIN",
                "ROLE_USER",
                "ROLE_DOWNLOAD"
            ]
        },
        {
            "title": "Report CE",
            "url": "/report-ce",
            "addInNavbar": true,
            "roles": [
                "ROLE_ADMIN",
                "ROLE_USER",
                "ROLE_DOWNLOAD"
            ]
        },
        {
            "title": "Canned Report",
            "url": "/hcr",
            "addInNavbar": true,
            "roles": [
                "ROLE_ADMIN",
                "ROLE_USER",
                "ROLE_DOWNLOAD"
            ]
        },
        {
            "title": "Report View",
            "url": "/report-viewer",
            "addInNavbar": false,
            "roles": [
                "ROLE_ADMIN",
                "ROLE_USER",
                "ROLE_DOWNLOAD",
                "ROLE_VIEWER"
            ]
        },
        {
            "title": "HI",
            "url": "/hi",
            "addInNavbar": false,
            "roles": [],
            "accessbleForAll": true
        }
    ],
    "activeRoute": "/metadata?",
    "isApplicationSettingsServiceCheck": true,
    "nxtRoute": "",
    "toggleSidebar": false,
    "showNavbar": true,
    "logType": "",
    "isUrlAuthenticating": false,
    "isSessionOver": false,
    "viewModeInfo": null
}