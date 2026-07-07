export const store4811 = {
    "dataFetchedFor": {
        "getDatasource": true,
        "joins": false,
        "listDataSources": true,
        "f29n-f2o5-4bi8-8d79-x1": true,
        "dnl1-7mj4-8dpy-tm3o-px": false,
        "rwsq-urg7-msq2-7g1d-5n": false
    },
    "loadingStatus": {
        "getDatasource": true,
        "listDataSources": true,
        "f29n-f2o5-4bi8-8d79-x1": true,
        "dnl1-7mj4-8dpy-tm3o-px": false,
        "rwsq-urg7-msq2-7g1d-5n": false
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
            "name": "Presto",
            "categoryType": "supported",
            "categoryName": "Supported"
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
                            "keyPath": "f29n-f2o5-4bi8-8d79-x1/dnl1-7mj4-8dpy-tm3o-px/lw6o-2ve8-1rru-2rpv-pq",
                            "key": "lw6o-2ve8-1rru-2rpv-pq",
                            "uuid": "lw6o-2ve8-1rru-2rpv-pq",
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
                            "keyPath": "f29n-f2o5-4bi8-8d79-x1/dnl1-7mj4-8dpy-tm3o-px/myab-4gph-h64j-sjiv-1q",
                            "key": "myab-4gph-h64j-sjiv-1q",
                            "uuid": "myab-4gph-h64j-sjiv-1q",
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
                            "keyPath": "f29n-f2o5-4bi8-8d79-x1/dnl1-7mj4-8dpy-tm3o-px/f0td-dn93-a3bw-l7x1-u5",
                            "key": "f0td-dn93-a3bw-l7x1-u5",
                            "uuid": "f0td-dn93-a3bw-l7x1-u5",
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
                                    "keyPath": "f29n-f2o5-4bi8-8d79-x1/dnl1-7mj4-8dpy-tm3o-px/rwsq-urg7-msq2-7g1d-5n/qpuv-1qg6-z7r7-elsh-y2",
                                    "key": "qpuv-1qg6-z7r7-elsh-y2",
                                    "alias": "geo_cordinates",
                                    "uuid": "qpuv-1qg6-z7r7-elsh-y2",
                                    "connId": "hojs6",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "hojs6",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "f29n-f2o5-4bi8-8d79-x1/dnl1-7mj4-8dpy-tm3o-px/rwsq-urg7-msq2-7g1d-5n",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "geo_cordinates_hojs6",
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
                                    "keyPath": "f29n-f2o5-4bi8-8d79-x1/dnl1-7mj4-8dpy-tm3o-px/rwsq-urg7-msq2-7g1d-5n/zorz-rh5h-xq2g-jmdr-j8",
                                    "key": "zorz-rh5h-xq2g-jmdr-j8",
                                    "alias": "meeting_details",
                                    "uuid": "zorz-rh5h-xq2g-jmdr-j8",
                                    "connId": "hojs6",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "hojs6",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "f29n-f2o5-4bi8-8d79-x1/dnl1-7mj4-8dpy-tm3o-px/rwsq-urg7-msq2-7g1d-5n",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "meeting_details_hojs6",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": false
                                },
                                {
                                    "id": "4e1fd245f4d13b77be423a43f01d80b2",
                                    "name": "employee_details",
                                    "data": {
                                        "id": "1",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "f29n-f2o5-4bi8-8d79-x1/dnl1-7mj4-8dpy-tm3o-px/rwsq-urg7-msq2-7g1d-5n/p8gg-sbuw-14y1-xtrh-o0",
                                    "key": "p8gg-sbuw-14y1-xtrh-o0",
                                    "alias": "employee_details",
                                    "uuid": "p8gg-sbuw-14y1-xtrh-o0",
                                    "connId": "hojs6",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "hojs6",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "f29n-f2o5-4bi8-8d79-x1/dnl1-7mj4-8dpy-tm3o-px/rwsq-urg7-msq2-7g1d-5n",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "employee_details_hojs6",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": false
                                },
                                {
                                    "id": "4ac5d9f68b58bd7c0d179146e46795be",
                                    "name": "dimdate",
                                    "data": {
                                        "id": "1",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "f29n-f2o5-4bi8-8d79-x1/dnl1-7mj4-8dpy-tm3o-px/rwsq-urg7-msq2-7g1d-5n/b514-7g6r-ghkw-y94l-oc",
                                    "key": "b514-7g6r-ghkw-y94l-oc",
                                    "alias": "dimdate",
                                    "uuid": "b514-7g6r-ghkw-y94l-oc",
                                    "connId": "hojs6",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "hojs6",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "f29n-f2o5-4bi8-8d79-x1/dnl1-7mj4-8dpy-tm3o-px/rwsq-urg7-msq2-7g1d-5n",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "dimdate_hojs6",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": false
                                },
                                {
                                    "id": "8a28627d07d04ef096d9935f12e0c7e9",
                                    "name": "travel_details",
                                    "data": {
                                        "id": "1",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "f29n-f2o5-4bi8-8d79-x1/dnl1-7mj4-8dpy-tm3o-px/rwsq-urg7-msq2-7g1d-5n/zxyb-rdiu-k4rg-f1x7-vp",
                                    "key": "zxyb-rdiu-k4rg-f1x7-vp",
                                    "alias": "travel_details",
                                    "uuid": "zxyb-rdiu-k4rg-f1x7-vp",
                                    "connId": "hojs6",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "hojs6",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "f29n-f2o5-4bi8-8d79-x1/dnl1-7mj4-8dpy-tm3o-px/rwsq-urg7-msq2-7g1d-5n",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "travel_details_hojs6",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": false
                                }
                            ],
                            "keyPath": "f29n-f2o5-4bi8-8d79-x1/dnl1-7mj4-8dpy-tm3o-px/rwsq-urg7-msq2-7g1d-5n",
                            "key": "rwsq-urg7-msq2-7g1d-5n",
                            "uuid": "rwsq-urg7-msq2-7g1d-5n",
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
                            "keyPath": "f29n-f2o5-4bi8-8d79-x1/dnl1-7mj4-8dpy-tm3o-px/lbp6-2uss-rlyx-z7ii-pq",
                            "key": "lbp6-2uss-rlyx-z7ii-pq",
                            "uuid": "lbp6-2uss-rlyx-z7ii-pq",
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
                            "keyPath": "f29n-f2o5-4bi8-8d79-x1/dnl1-7mj4-8dpy-tm3o-px/1fjk-835g-zd5s-jw68-tx",
                            "key": "1fjk-835g-zd5s-jw68-tx",
                            "uuid": "1fjk-835g-zd5s-jw68-tx",
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
                            "keyPath": "f29n-f2o5-4bi8-8d79-x1/dnl1-7mj4-8dpy-tm3o-px/u4wy-kdns-egpa-r98l-d1",
                            "key": "u4wy-kdns-egpa-r98l-d1",
                            "uuid": "u4wy-kdns-egpa-r98l-d1",
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
                            "keyPath": "f29n-f2o5-4bi8-8d79-x1/dnl1-7mj4-8dpy-tm3o-px/ele8-g3p3-20b7-c15k-ip",
                            "key": "ele8-g3p3-20b7-c15k-ip",
                            "uuid": "ele8-g3p3-20b7-c15k-ip",
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
                            "keyPath": "f29n-f2o5-4bi8-8d79-x1/dnl1-7mj4-8dpy-tm3o-px/qnyo-8n68-am28-4pq1-62",
                            "key": "qnyo-8n68-am28-4pq1-62",
                            "uuid": "qnyo-8n68-am28-4pq1-62",
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
                            "keyPath": "f29n-f2o5-4bi8-8d79-x1/dnl1-7mj4-8dpy-tm3o-px/9orb-xeut-nkih-bgf0-pa",
                            "key": "9orb-xeut-nkih-bgf0-pa",
                            "uuid": "9orb-xeut-nkih-bgf0-pa",
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
                            "keyPath": "f29n-f2o5-4bi8-8d79-x1/dnl1-7mj4-8dpy-tm3o-px/7cni-513f-koc2-9dxt-1b",
                            "key": "7cni-513f-koc2-9dxt-1b",
                            "uuid": "7cni-513f-koc2-9dxt-1b",
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
                            "keyPath": "f29n-f2o5-4bi8-8d79-x1/dnl1-7mj4-8dpy-tm3o-px/aj6u-kugj-ghwi-l6rq-ei",
                            "key": "aj6u-kugj-ghwi-l6rq-ei",
                            "uuid": "aj6u-kugj-ghwi-l6rq-ei",
                            "data": {
                                "id": "1",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "SampleTravelDataDerby"
                        }
                    ],
                    "driverType": "Derby",
                    "keyPath": "f29n-f2o5-4bi8-8d79-x1/dnl1-7mj4-8dpy-tm3o-px",
                    "key": "dnl1-7mj4-8dpy-tm3o-px",
                    "uuid": "dnl1-7mj4-8dpy-tm3o-px",
                    "fetched": true
                }
            ],
            "key": "f29n-f2o5-4bi8-8d79-x1",
            "uuid": "f29n-f2o5-4bi8-8d79-x1",
            "keyPath": "f29n-f2o5-4bi8-8d79-x1",
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
                "dsUUID": "dnl1-7mj4-8dpy-tm3o-px",
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
                        "connId": "hojs6",
                        "classifier": "db.workflow",
                        "datasourceName": "SampleTravelDataDerby",
                        "dsKeyPath": "f29n-f2o5-4bi8-8d79-x1/dnl1-7mj4-8dpy-tm3o-px/rwsq-urg7-msq2-7g1d-5n",
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
            "connId": "hojs6",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelDataDerby",
            "dsKeyPath": "f29n-f2o5-4bi8-8d79-x1/dnl1-7mj4-8dpy-tm3o-px/rwsq-urg7-msq2-7g1d-5n",
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
            "keyPath": "f29n-f2o5-4bi8-8d79-x1/dnl1-7mj4-8dpy-tm3o-px/rwsq-urg7-msq2-7g1d-5n/qpuv-1qg6-z7r7-elsh-y2",
            "key": "qpuv-1qg6-z7r7-elsh-y2",
            "alias": "geo_cordinates",
            "uuid": "qpuv-1qg6-z7r7-elsh-y2",
            "connId": "hojs6",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "hojs6",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "f29n-f2o5-4bi8-8d79-x1/dnl1-7mj4-8dpy-tm3o-px/rwsq-urg7-msq2-7g1d-5n",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "geo_cordinates_hojs6",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "geo_cordinates"
        }
    },
    "categories": {
        "f29n-f2o5-4bi8-8d79-x1": {
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
            "connId": "hojs6",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelDataDerby",
            "dsKeyPath": "f29n-f2o5-4bi8-8d79-x1/dnl1-7mj4-8dpy-tm3o-px/rwsq-urg7-msq2-7g1d-5n",
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
        "uuid": "4811_1.metadata"
    },
    "savedTableIds": [],
    "savedColumnIds": [],
    "joins": [],
    "mode": "create",
    "allTablesKeys": [
        "qpuv-1qg6-z7r7-elsh-y2",
        "zorz-rh5h-xq2g-jmdr-j8",
        "p8gg-sbuw-14y1-xtrh-o0",
        "b514-7g6r-ghkw-y94l-oc",
        "zxyb-rdiu-k4rg-f1x7-vp"
    ],
    "selectedTableKeys": [
        "qpuv-1qg6-z7r7-elsh-y2"
    ],
    "metadataName": "4811 1",
    "activeDataSource": false,
    "metadataToEdit": false,
    "isSavingInProgress": false,
    "inititalStateFromJest": false,
    "timeStamp": 1654284758495,
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
        "uniqueId": "4811_1",
        "tables": {
            "employee_details": {
                "id": "4e1fd245f4d13b77be423a43f01d80b2",
                "alias": "employee_details",
                "columns": {
                    "employee_id": {
                        "alias": "employee_id",
                        "fullyQualifiedColumn": "employee_details.employee_id",
                        "columnId": "12f7778c-5e2e-4fb5-9c79-f4b7461c16f2",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "employee_name": {
                        "alias": "employee_name",
                        "fullyQualifiedColumn": "employee_details.employee_name",
                        "columnId": "4d62b0dc-deaf-4616-806d-63421ecce0bc",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "age": {
                        "alias": "age",
                        "fullyQualifiedColumn": "employee_details.age",
                        "columnId": "aa8c53db-7cd7-490b-b5fb-966ac34d301e",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "address": {
                        "alias": "address",
                        "fullyQualifiedColumn": "employee_details.address",
                        "columnId": "ec899da7-48b2-480d-9b07-e8e5a32b162f",
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
                        "columnId": "35e711a6-851b-4d7f-9565-60167828358f",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "location": {
                        "alias": "location",
                        "fullyQualifiedColumn": "geo_cordinates.location",
                        "columnId": "1ee114f4-6b37-412d-bc24-b758973ad291",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "latitude": {
                        "alias": "latitude",
                        "fullyQualifiedColumn": "geo_cordinates.latitude",
                        "columnId": "f0d41436-5c5c-47d8-b7e6-587717dfc35d",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Double": "numeric"
                        }
                    },
                    "longitude": {
                        "alias": "longitude",
                        "fullyQualifiedColumn": "geo_cordinates.longitude",
                        "columnId": "77736857-bbf6-43f3-ae3a-b552b101c67d",
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
                        "columnId": "c7920582-030e-4e0f-bca5-3c3c231ec1e9",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "meeting_date": {
                        "alias": "meeting_date",
                        "fullyQualifiedColumn": "meeting_details.meeting_date",
                        "columnId": "60a72917-898a-495b-a44f-d4a84bf5d0fc",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.sql.Timestamp": "dateTime"
                        }
                    },
                    "meeting_by": {
                        "alias": "meeting_by",
                        "fullyQualifiedColumn": "meeting_details.meeting_by",
                        "columnId": "a96761cf-aaba-450e-87ef-f612bd83ff34",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "client_name": {
                        "alias": "client_name",
                        "fullyQualifiedColumn": "meeting_details.client_name",
                        "columnId": "784bb86c-25e3-41fd-a1ec-27a3f74faf36",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "meeting_purpose": {
                        "alias": "meeting_purpose",
                        "fullyQualifiedColumn": "meeting_details.meeting_purpose",
                        "columnId": "3db4a295-3629-4b9c-b04f-a6dd33d2b415",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "meeting_impact": {
                        "alias": "meeting_impact",
                        "fullyQualifiedColumn": "meeting_details.meeting_impact",
                        "columnId": "77bce0e9-0334-4576-995c-19cc41be068c",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "meet_cancellation_status": {
                        "alias": "meet_cancellation_status",
                        "fullyQualifiedColumn": "meeting_details.meet_cancellation_status",
                        "columnId": "2522fa26-e8db-47a0-888b-3f103c3abf9a",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "cancellation_reason": {
                        "alias": "cancellation_reason",
                        "fullyQualifiedColumn": "meeting_details.cancellation_reason",
                        "columnId": "28e4b34d-a2dc-4806-80db-35ca165862f1",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    }
                },
                "name": "meeting_details"
            },
            "dimdate": {
                "id": "4ac5d9f68b58bd7c0d179146e46795be",
                "alias": "dimdate",
                "columns": {
                    "fiscal_month_label": {
                        "alias": "fiscal_month_label",
                        "fullyQualifiedColumn": "dimdate.fiscal_month_label",
                        "columnId": "9f335ae0-1416-4072-87de-83086a563b80",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "created_date": {
                        "alias": "created_date",
                        "fullyQualifiedColumn": "dimdate.created_date",
                        "columnId": "92c038e5-8ccb-49a3-814b-b5446104a0cb",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "created_time": {
                        "alias": "created_time",
                        "fullyQualifiedColumn": "dimdate.created_time",
                        "columnId": "2fc23229-4f15-4b75-a63c-58c25a75c2d4",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "rating": {
                        "alias": "rating",
                        "fullyQualifiedColumn": "dimdate.rating",
                        "columnId": "98f2f88a-b00f-4854-9d06-a2275a2d82e3",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "dim_id": {
                        "alias": "dim_id",
                        "fullyQualifiedColumn": "dimdate.dim_id",
                        "columnId": "123d0e35-aa5f-45f9-8e3e-2befa54c9673",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "fiscal_year": {
                        "alias": "fiscal_year",
                        "fullyQualifiedColumn": "dimdate.fiscal_year",
                        "columnId": "fcb6de7f-f62d-43fd-b7fc-d6093ea7cfdb",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.sql.Date": "date"
                        }
                    },
                    "modified_date": {
                        "alias": "modified_date",
                        "fullyQualifiedColumn": "dimdate.modified_date",
                        "columnId": "faaeac7f-b4c1-4a84-a2d3-c504f0ec40c5",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.sql.Timestamp": "dateTime"
                        }
                    },
                    "date_key": {
                        "alias": "date_key",
                        "fullyQualifiedColumn": "dimdate.date_key",
                        "columnId": "fee2ff64-beed-41a8-b772-47b147afbeff",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "day_number": {
                        "alias": "day_number",
                        "fullyQualifiedColumn": "dimdate.day_number",
                        "columnId": "3ef85f91-1cf9-494d-a2bc-2388fde1917c",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "fiscal_month_name": {
                        "alias": "fiscal_month_name",
                        "fullyQualifiedColumn": "dimdate.fiscal_month_name",
                        "columnId": "602dc82d-ffaf-4f4b-be8b-489af81e1887",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    }
                },
                "name": "dimdate"
            },
            "travel_details": {
                "id": "8a28627d07d04ef096d9935f12e0c7e9",
                "alias": "travel_details",
                "columns": {
                    "travel_id": {
                        "alias": "travel_id",
                        "fullyQualifiedColumn": "travel_details.travel_id",
                        "columnId": "fc3f3c6b-984e-42d4-a217-d528fc912275",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "travel_date": {
                        "alias": "travel_date",
                        "fullyQualifiedColumn": "travel_details.travel_date",
                        "columnId": "9eb4f4f6-b9f3-4a4c-b647-ff71432c3ccc",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.sql.Timestamp": "dateTime"
                        }
                    },
                    "travel_type": {
                        "alias": "travel_type",
                        "fullyQualifiedColumn": "travel_details.travel_type",
                        "columnId": "24c629c1-ce17-4e02-81e6-3f901e8cb354",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "travel_medium": {
                        "alias": "travel_medium",
                        "fullyQualifiedColumn": "travel_details.travel_medium",
                        "columnId": "8d092ab7-c902-44c2-b336-18987937d7de",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "source_id": {
                        "alias": "source_id",
                        "fullyQualifiedColumn": "travel_details.source_id",
                        "columnId": "2d12991f-ca6a-41e9-8a9b-c172c1ef6096",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "source": {
                        "alias": "source",
                        "fullyQualifiedColumn": "travel_details.source",
                        "columnId": "6c64a134-0f8e-4fd5-95d5-85e9590a22e0",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "destination_id": {
                        "alias": "destination_id",
                        "fullyQualifiedColumn": "travel_details.destination_id",
                        "columnId": "95ca44ed-50ff-4910-ad3c-8093bcf82458",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "destination": {
                        "alias": "destination",
                        "fullyQualifiedColumn": "travel_details.destination",
                        "columnId": "666d0b88-cb4f-4146-b00b-016302a1bb04",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "travel_cost": {
                        "alias": "travel_cost",
                        "fullyQualifiedColumn": "travel_details.travel_cost",
                        "columnId": "4ad8963c-af5d-4b49-9182-01f3cd92e7bb",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "mode_of_payment": {
                        "alias": "mode_of_payment",
                        "fullyQualifiedColumn": "travel_details.mode_of_payment",
                        "columnId": "b7a36451-72ef-4d7c-b6af-37ca67a6caa7",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "booking_platform": {
                        "alias": "booking_platform",
                        "fullyQualifiedColumn": "travel_details.booking_platform",
                        "columnId": "e42afbb9-fdf9-4361-bd58-89c0094c78fe",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "travelled_by": {
                        "alias": "travelled_by",
                        "fullyQualifiedColumn": "travel_details.travelled_by",
                        "columnId": "d6c882f5-c1d1-4be4-b945-8341af0863d4",
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
                "geo_cordinates"
            ],
            [
                "meeting_details"
            ],
            [
                "employee_details"
            ],
            [
                "dimdate"
            ],
            [
                "travel_details"
            ]
        ],
        "joins": [],
        "metadataName": "4811 1",
        "metadataDir": "Gagan"
    }
}