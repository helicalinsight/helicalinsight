export const store4812 = {
    "dataFetchedFor": {
        "getDatasource": true,
        "joins": true,
        "listDataSources": true,
        "1ux8-b7jw-uk5s-yfv7-sb": true,
        "4u0i-detu-ltkw-my5w-44": false,
        "nbkb-bf1d-mybq-nl1j-fv": false
    },
    "loadingStatus": {
        "getDatasource": true,
        "listDataSources": true,
        "1ux8-b7jw-uk5s-yfv7-sb": true,
        "4u0i-detu-ltkw-my5w-44": false,
        "nbkb-bf1d-mybq-nl1j-fv": false
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
                            "keyPath": "1ux8-b7jw-uk5s-yfv7-sb/4u0i-detu-ltkw-my5w-44/oev8-9q7f-nfv9-cew9-nb",
                            "key": "oev8-9q7f-nfv9-cew9-nb",
                            "uuid": "oev8-9q7f-nfv9-cew9-nb",
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
                            "keyPath": "1ux8-b7jw-uk5s-yfv7-sb/4u0i-detu-ltkw-my5w-44/l7ez-2ilv-0khm-frat-aw",
                            "key": "l7ez-2ilv-0khm-frat-aw",
                            "uuid": "l7ez-2ilv-0khm-frat-aw",
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
                            "keyPath": "1ux8-b7jw-uk5s-yfv7-sb/4u0i-detu-ltkw-my5w-44/docw-6gp2-7ygx-f4oc-f8",
                            "key": "docw-6gp2-7ygx-f4oc-f8",
                            "uuid": "docw-6gp2-7ygx-f4oc-f8",
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
                                    "keyPath": "1ux8-b7jw-uk5s-yfv7-sb/4u0i-detu-ltkw-my5w-44/nbkb-bf1d-mybq-nl1j-fv/s6m4-qflc-y717-gxqs-vx",
                                    "key": "s6m4-qflc-y717-gxqs-vx",
                                    "alias": "geo_cordinates",
                                    "uuid": "s6m4-qflc-y717-gxqs-vx",
                                    "connId": "scv2k",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "scv2k",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "1ux8-b7jw-uk5s-yfv7-sb/4u0i-detu-ltkw-my5w-44/nbkb-bf1d-mybq-nl1j-fv",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "geo_cordinates_scv2k",
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
                                    "keyPath": "1ux8-b7jw-uk5s-yfv7-sb/4u0i-detu-ltkw-my5w-44/nbkb-bf1d-mybq-nl1j-fv/1dve-11cn-rs0o-6841-37",
                                    "key": "1dve-11cn-rs0o-6841-37",
                                    "alias": "meeting_details",
                                    "uuid": "1dve-11cn-rs0o-6841-37",
                                    "connId": "scv2k",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "scv2k",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "1ux8-b7jw-uk5s-yfv7-sb/4u0i-detu-ltkw-my5w-44/nbkb-bf1d-mybq-nl1j-fv",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "meeting_details_scv2k",
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
                                    "keyPath": "1ux8-b7jw-uk5s-yfv7-sb/4u0i-detu-ltkw-my5w-44/nbkb-bf1d-mybq-nl1j-fv/vpe2-c08u-a5pv-s2t5-ug",
                                    "key": "vpe2-c08u-a5pv-s2t5-ug",
                                    "alias": "employee_details",
                                    "uuid": "vpe2-c08u-a5pv-s2t5-ug",
                                    "connId": "scv2k",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "scv2k",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "1ux8-b7jw-uk5s-yfv7-sb/4u0i-detu-ltkw-my5w-44/nbkb-bf1d-mybq-nl1j-fv",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "employee_details_scv2k",
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
                                    "keyPath": "1ux8-b7jw-uk5s-yfv7-sb/4u0i-detu-ltkw-my5w-44/nbkb-bf1d-mybq-nl1j-fv/5eel-vvj2-l7h2-agqo-7i",
                                    "key": "5eel-vvj2-l7h2-agqo-7i",
                                    "alias": "dimdate",
                                    "uuid": "5eel-vvj2-l7h2-agqo-7i",
                                    "connId": "scv2k",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "scv2k",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "1ux8-b7jw-uk5s-yfv7-sb/4u0i-detu-ltkw-my5w-44/nbkb-bf1d-mybq-nl1j-fv",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "dimdate_scv2k",
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
                                    "keyPath": "1ux8-b7jw-uk5s-yfv7-sb/4u0i-detu-ltkw-my5w-44/nbkb-bf1d-mybq-nl1j-fv/7slm-vrkp-v4p8-j7ds-hd",
                                    "key": "7slm-vrkp-v4p8-j7ds-hd",
                                    "alias": "travel_details",
                                    "uuid": "7slm-vrkp-v4p8-j7ds-hd",
                                    "connId": "scv2k",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "scv2k",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "1ux8-b7jw-uk5s-yfv7-sb/4u0i-detu-ltkw-my5w-44/nbkb-bf1d-mybq-nl1j-fv",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "travel_details_scv2k",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": false
                                }
                            ],
                            "keyPath": "1ux8-b7jw-uk5s-yfv7-sb/4u0i-detu-ltkw-my5w-44/nbkb-bf1d-mybq-nl1j-fv",
                            "key": "nbkb-bf1d-mybq-nl1j-fv",
                            "uuid": "nbkb-bf1d-mybq-nl1j-fv",
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
                            "keyPath": "1ux8-b7jw-uk5s-yfv7-sb/4u0i-detu-ltkw-my5w-44/eec8-f338-epzm-9v1s-1s",
                            "key": "eec8-f338-epzm-9v1s-1s",
                            "uuid": "eec8-f338-epzm-9v1s-1s",
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
                            "keyPath": "1ux8-b7jw-uk5s-yfv7-sb/4u0i-detu-ltkw-my5w-44/5qgm-47we-g021-te3o-ep",
                            "key": "5qgm-47we-g021-te3o-ep",
                            "uuid": "5qgm-47we-g021-te3o-ep",
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
                            "keyPath": "1ux8-b7jw-uk5s-yfv7-sb/4u0i-detu-ltkw-my5w-44/xj4j-xlz1-k9q4-1g9w-la",
                            "key": "xj4j-xlz1-k9q4-1g9w-la",
                            "uuid": "xj4j-xlz1-k9q4-1g9w-la",
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
                            "keyPath": "1ux8-b7jw-uk5s-yfv7-sb/4u0i-detu-ltkw-my5w-44/vng5-6wn9-psom-2jor-tz",
                            "key": "vng5-6wn9-psom-2jor-tz",
                            "uuid": "vng5-6wn9-psom-2jor-tz",
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
                            "keyPath": "1ux8-b7jw-uk5s-yfv7-sb/4u0i-detu-ltkw-my5w-44/41u8-fru4-19gt-rd1j-6a",
                            "key": "41u8-fru4-19gt-rd1j-6a",
                            "uuid": "41u8-fru4-19gt-rd1j-6a",
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
                            "keyPath": "1ux8-b7jw-uk5s-yfv7-sb/4u0i-detu-ltkw-my5w-44/ipcs-l1qa-w0th-59xk-nu",
                            "key": "ipcs-l1qa-w0th-59xk-nu",
                            "uuid": "ipcs-l1qa-w0th-59xk-nu",
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
                            "keyPath": "1ux8-b7jw-uk5s-yfv7-sb/4u0i-detu-ltkw-my5w-44/jw4n-i3p6-z38x-2d43-mp",
                            "key": "jw4n-i3p6-z38x-2d43-mp",
                            "uuid": "jw4n-i3p6-z38x-2d43-mp",
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
                            "keyPath": "1ux8-b7jw-uk5s-yfv7-sb/4u0i-detu-ltkw-my5w-44/ret2-3936-73p6-djn6-32",
                            "key": "ret2-3936-73p6-djn6-32",
                            "uuid": "ret2-3936-73p6-djn6-32",
                            "data": {
                                "id": "1",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "SampleTravelDataDerby"
                        }
                    ],
                    "driverType": "Derby",
                    "keyPath": "1ux8-b7jw-uk5s-yfv7-sb/4u0i-detu-ltkw-my5w-44",
                    "key": "4u0i-detu-ltkw-my5w-44",
                    "uuid": "4u0i-detu-ltkw-my5w-44",
                    "fetched": true
                }
            ],
            "key": "1ux8-b7jw-uk5s-yfv7-sb",
            "uuid": "1ux8-b7jw-uk5s-yfv7-sb",
            "keyPath": "1ux8-b7jw-uk5s-yfv7-sb",
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
                "dsUUID": "4u0i-detu-ltkw-my5w-44",
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
                        "connId": "scv2k",
                        "classifier": "db.workflow",
                        "datasourceName": "SampleTravelDataDerby",
                        "dsKeyPath": "1ux8-b7jw-uk5s-yfv7-sb/4u0i-detu-ltkw-my5w-44/nbkb-bf1d-mybq-nl1j-fv",
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
            "connId": "scv2k",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelDataDerby",
            "dsKeyPath": "1ux8-b7jw-uk5s-yfv7-sb/4u0i-detu-ltkw-my5w-44/nbkb-bf1d-mybq-nl1j-fv",
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
            "keyPath": "1ux8-b7jw-uk5s-yfv7-sb/4u0i-detu-ltkw-my5w-44/nbkb-bf1d-mybq-nl1j-fv/s6m4-qflc-y717-gxqs-vx",
            "key": "s6m4-qflc-y717-gxqs-vx",
            "alias": "geo_cordinates",
            "uuid": "s6m4-qflc-y717-gxqs-vx",
            "connId": "scv2k",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "scv2k",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "1ux8-b7jw-uk5s-yfv7-sb/4u0i-detu-ltkw-my5w-44/nbkb-bf1d-mybq-nl1j-fv",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "geo_cordinates_scv2k",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "geo_cordinates"
        }
    },
    "categories": {
        "1ux8-b7jw-uk5s-yfv7-sb": {
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
            "connId": "scv2k",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelDataDerby",
            "dsKeyPath": "1ux8-b7jw-uk5s-yfv7-sb/4u0i-detu-ltkw-my5w-44/nbkb-bf1d-mybq-nl1j-fv",
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
        "uuid": "4812_6.metadata"
    },
    "savedTableIds": [],
    "savedColumnIds": [],
    "joins": [],
    "mode": "create",
    "allTablesKeys": [
        "s6m4-qflc-y717-gxqs-vx",
        "1dve-11cn-rs0o-6841-37",
        "vpe2-c08u-a5pv-s2t5-ug",
        "5eel-vvj2-l7h2-agqo-7i",
        "7slm-vrkp-v4p8-j7ds-hd"
    ],
    "selectedTableKeys": [
        "s6m4-qflc-y717-gxqs-vx"
    ],
    "metadataName": "4812 6",
    "activeDataSource": false,
    "metadataToEdit": false,
    "isSavingInProgress": false,
    "inititalStateFromJest": false,
    "timeStamp": 1654254790954,
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
        "uniqueId": "4812_6",
        "tables": {
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
            "dimdate": {
                "id": "4ac5d9f68b58bd7c0d179146e46795be",
                "alias": "dimdate",
                "columns": {
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
                    },
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
                "geo_cordinates",
                "dimdate",
                "travel_details",
                "employee_details",
                "meeting_details"
            ]
        ],
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
                }
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
        "metadataName": "4812 6",
        "metadataDir": "Gagan"
    }
}

export const store4812_2 = {
    "dataFetchedFor": {
        "getDatasource": true,
        "joins": true,
        "listDataSources": true,
        "r7h8-wirp-sm46-d12s-o8": false,
        "9jld-a73p-7ohm-292d-5t": false
    },
    "loadingStatus": {
        "getDatasource": true,
        "listDataSources": true,
        "joins": true,
        "r7h8-wirp-sm46-d12s-o8": false,
        "9jld-a73p-7ohm-292d-5t": false
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
            "databaseDialect": "",
            "name": "Databricks Client",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png"
        },
        {
            "driver": "com.databricks.client.jdbc.jdbc42.JDBC42AbstractDriver",
            "databaseDialect": "",
            "name": "Databricks Client",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png"
        },
        {
            "driver": "com.databricks.client.jdbc.common.AbstractDriver",
            "databaseDialect": "",
            "name": "Databricks Client Common",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png"
        },
        {
            "driver": "com.databricks.client.jdbc.core.DSDriver",
            "databaseDialect": "",
            "name": "Databricks Client Core",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png"
        },
        {
            "driver": "com.databricks.client.dsi.core.impl.DSIDriver",
            "databaseDialect": "",
            "name": "Databricks Client Dsi Core Impl",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png"
        },
        {
            "driver": "com.databricks.client.dsi.core.interfaces.IDriver",
            "databaseDialect": "",
            "name": "Databricks Client Dsi Core Interfaces",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png"
        },
        {
            "driver": "com.databricks.client.jdbc.jdbc42.future.JDBC42AbstractDriver",
            "databaseDialect": "",
            "name": "Databricks Client Future",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png"
        },
        {
            "driver": "com.databricks.client.hivecommon.core.HiveJDBCCommonDriver",
            "databaseDialect": "",
            "name": "Databricks Client Hivecommon Core",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png"
        },
        {
            "driver": "com.databricks.client.jdbc.jdbc41.JDBC41AbstractDriver",
            "databaseDialect": "",
            "name": "Databricks Client Jdbc41",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png"
        },
        {
            "driver": "com.databricks.client.jdbc.jdbc41.future.JDBC41AbstractDriver",
            "databaseDialect": "",
            "name": "Databricks Client Jdbc41 Future",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png"
        },
        {
            "driver": "com.databricks.client.spark.jdbc.SparkJDBC42Driver",
            "databaseDialect": "",
            "name": "Databricks Client Spark",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png"
        },
        {
            "driver": "com.databricks.client.spark.core.SparkJDBCDriver",
            "databaseDialect": "",
            "name": "Databricks Client Spark Core",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png"
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
            "databaseDialect": "",
            "name": "Databricks Client",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png"
        },
        {
            "driver": "com.databricks.client.jdbc.jdbc42.JDBC42AbstractDriver",
            "databaseDialect": "",
            "name": "Databricks Client",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png"
        },
        {
            "driver": "com.databricks.client.jdbc.common.AbstractDriver",
            "databaseDialect": "",
            "name": "Databricks Client Common",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png"
        },
        {
            "driver": "com.databricks.client.jdbc.core.DSDriver",
            "databaseDialect": "",
            "name": "Databricks Client Core",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png"
        },
        {
            "driver": "com.databricks.client.dsi.core.impl.DSIDriver",
            "databaseDialect": "",
            "name": "Databricks Client Dsi Core Impl",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png"
        },
        {
            "driver": "com.databricks.client.dsi.core.interfaces.IDriver",
            "databaseDialect": "",
            "name": "Databricks Client Dsi Core Interfaces",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png"
        },
        {
            "driver": "com.databricks.client.jdbc.jdbc42.future.JDBC42AbstractDriver",
            "databaseDialect": "",
            "name": "Databricks Client Future",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png"
        },
        {
            "driver": "com.databricks.client.hivecommon.core.HiveJDBCCommonDriver",
            "databaseDialect": "",
            "name": "Databricks Client Hivecommon Core",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png"
        },
        {
            "driver": "com.databricks.client.jdbc.jdbc41.JDBC41AbstractDriver",
            "databaseDialect": "",
            "name": "Databricks Client Jdbc41",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png"
        },
        {
            "driver": "com.databricks.client.jdbc.jdbc41.future.JDBC41AbstractDriver",
            "databaseDialect": "",
            "name": "Databricks Client Jdbc41 Future",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png"
        },
        {
            "driver": "com.databricks.client.spark.jdbc.SparkJDBC42Driver",
            "databaseDialect": "",
            "name": "Databricks Client Spark",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png"
        },
        {
            "driver": "com.databricks.client.spark.core.SparkJDBCDriver",
            "databaseDialect": "",
            "name": "Databricks Client Spark Core",
            "categoryName": "RDBMS",
            "categoryType": "rdbms",
            "type": "global.jdbc",
            "dataSourceProvider": "tomcat",
            "classifier": "global",
            "imgUrl": "../images/data_sources/defaut_datasource.png"
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
            "available": "true",
            "driver": "com.databricks.client.jdbc.jdbc41.future.JDBC41AbstractDriver"
        },
        {
            "available": "true",
            "driver": "com.databricks.client.dsi.core.impl.DSIDriver"
        },
        {
            "available": "true",
            "driver": "com.databricks.client.dsi.core.interfaces.IDriver"
        },
        {
            "available": "true",
            "driver": "com.databricks.client.spark.jdbc.SparkJDBC42Driver"
        },
        {
            "available": "true",
            "driver": "com.databricks.client.jdbc.Driver"
        },
        {
            "available": "true",
            "driver": "com.databricks.client.jdbc.core.DSDriver"
        },
        {
            "available": "true",
            "driver": "com.databricks.client.hivecommon.core.HiveJDBCCommonDriver"
        },
        {
            "available": "true",
            "driver": "com.databricks.client.spark.core.SparkJDBCDriver"
        },
        {
            "available": "true",
            "driver": "com.databricks.client.jdbc.common.AbstractDriver"
        },
        {
            "available": "true",
            "driver": "com.databricks.client.jdbc.jdbc42.JDBC42AbstractDriver"
        },
        {
            "available": "true",
            "driver": "com.databricks.client.jdbc.jdbc42.future.JDBC42AbstractDriver"
        },
        {
            "available": "true",
            "driver": "com.databricks.client.jdbc.jdbc41.JDBC41AbstractDriver"
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
            "name": "test",
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
            "driver": "com.simba.athena.jdbc.Driver",
            "name": "testing_291",
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
            "name": "Athena",
            "children": [
                {
                    "data": {
                        "id": "1001",
                        "type": "dynamicDataSource"
                    },
                    "dataSourceProvider": "tomcat",
                    "type": "dynamicDataSource",
                    "permissionLevel": 5,
                    "driver": "com.simba.athena.jdbc.Driver",
                    "name": "testing_291",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Athena",
                    "keyPath": "dk1n-a8po-5zyl-gpd5-0b/hxy3-szoa-vco6-eb6g-9o",
                    "key": "hxy3-szoa-vco6-eb6g-9o",
                    "uuid": "hxy3-szoa-vco6-eb6g-9o"
                }
            ],
            "key": "dk1n-a8po-5zyl-gpd5-0b",
            "uuid": "dk1n-a8po-5zyl-gpd5-0b",
            "keyPath": "dk1n-a8po-5zyl-gpd5-0b",
            "category": "dsGroup",
            "imgUrl": "images/data_sources/defaut_datasource.png"
        },
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
                            "keyPath": "k12n-aigi-01p3-ghwa-63/r7h8-wirp-sm46-d12s-o8/acum-38w1-z06e-enk5-qu",
                            "key": "acum-38w1-z06e-enk5-qu",
                            "uuid": "acum-38w1-z06e-enk5-qu",
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
                            "keyPath": "k12n-aigi-01p3-ghwa-63/r7h8-wirp-sm46-d12s-o8/645j-4xoq-gk1a-fxw8-05",
                            "key": "645j-4xoq-gk1a-fxw8-05",
                            "uuid": "645j-4xoq-gk1a-fxw8-05",
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
                            "keyPath": "k12n-aigi-01p3-ghwa-63/r7h8-wirp-sm46-d12s-o8/iggn-9201-ed72-q84v-5q",
                            "key": "iggn-9201-ed72-q84v-5q",
                            "uuid": "iggn-9201-ed72-q84v-5q",
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
                                    "keyPath": "k12n-aigi-01p3-ghwa-63/r7h8-wirp-sm46-d12s-o8/9jld-a73p-7ohm-292d-5t/41o0-5xiz-lb0t-7m4k-xs",
                                    "key": "41o0-5xiz-lb0t-7m4k-xs",
                                    "alias": "geo_cordinates",
                                    "uuid": "41o0-5xiz-lb0t-7m4k-xs",
                                    "connId": "bwsal",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "bwsal",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "k12n-aigi-01p3-ghwa-63/r7h8-wirp-sm46-d12s-o8/9jld-a73p-7ohm-292d-5t",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "geo_cordinates_bwsal",
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
                                    "keyPath": "k12n-aigi-01p3-ghwa-63/r7h8-wirp-sm46-d12s-o8/9jld-a73p-7ohm-292d-5t/b42h-v2zd-detp-fyxe-d8",
                                    "key": "b42h-v2zd-detp-fyxe-d8",
                                    "alias": "meeting_details",
                                    "uuid": "b42h-v2zd-detp-fyxe-d8",
                                    "connId": "bwsal",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "bwsal",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "k12n-aigi-01p3-ghwa-63/r7h8-wirp-sm46-d12s-o8/9jld-a73p-7ohm-292d-5t",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "meeting_details_bwsal",
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
                                    "keyPath": "k12n-aigi-01p3-ghwa-63/r7h8-wirp-sm46-d12s-o8/9jld-a73p-7ohm-292d-5t/ev7u-0bca-ujmi-2mx0-1l",
                                    "key": "ev7u-0bca-ujmi-2mx0-1l",
                                    "alias": "employee_details",
                                    "uuid": "ev7u-0bca-ujmi-2mx0-1l",
                                    "connId": "bwsal",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "bwsal",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "k12n-aigi-01p3-ghwa-63/r7h8-wirp-sm46-d12s-o8/9jld-a73p-7ohm-292d-5t",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "employee_details_bwsal",
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
                                    "keyPath": "k12n-aigi-01p3-ghwa-63/r7h8-wirp-sm46-d12s-o8/9jld-a73p-7ohm-292d-5t/5aw9-wbyu-cjml-frlg-kl",
                                    "key": "5aw9-wbyu-cjml-frlg-kl",
                                    "alias": "dimdate",
                                    "uuid": "5aw9-wbyu-cjml-frlg-kl",
                                    "connId": "bwsal",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "bwsal",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "k12n-aigi-01p3-ghwa-63/r7h8-wirp-sm46-d12s-o8/9jld-a73p-7ohm-292d-5t",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "dimdate_bwsal",
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
                                    "keyPath": "k12n-aigi-01p3-ghwa-63/r7h8-wirp-sm46-d12s-o8/9jld-a73p-7ohm-292d-5t/3wpv-e5g2-19ax-5tc5-35",
                                    "key": "3wpv-e5g2-19ax-5tc5-35",
                                    "alias": "travel_details",
                                    "uuid": "3wpv-e5g2-19ax-5tc5-35",
                                    "connId": "bwsal",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "bwsal",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "k12n-aigi-01p3-ghwa-63/r7h8-wirp-sm46-d12s-o8/9jld-a73p-7ohm-292d-5t",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "travel_details_bwsal",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": false
                                }
                            ],
                            "keyPath": "k12n-aigi-01p3-ghwa-63/r7h8-wirp-sm46-d12s-o8/9jld-a73p-7ohm-292d-5t",
                            "key": "9jld-a73p-7ohm-292d-5t",
                            "uuid": "9jld-a73p-7ohm-292d-5t",
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
                            "keyPath": "k12n-aigi-01p3-ghwa-63/r7h8-wirp-sm46-d12s-o8/0bwd-cf3n-uuv8-fbfd-9n",
                            "key": "0bwd-cf3n-uuv8-fbfd-9n",
                            "uuid": "0bwd-cf3n-uuv8-fbfd-9n",
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
                            "keyPath": "k12n-aigi-01p3-ghwa-63/r7h8-wirp-sm46-d12s-o8/k24o-kxrb-qmph-bjhf-ry",
                            "key": "k24o-kxrb-qmph-bjhf-ry",
                            "uuid": "k24o-kxrb-qmph-bjhf-ry",
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
                            "keyPath": "k12n-aigi-01p3-ghwa-63/r7h8-wirp-sm46-d12s-o8/uz94-5nve-8063-jgdw-bd",
                            "key": "uz94-5nve-8063-jgdw-bd",
                            "uuid": "uz94-5nve-8063-jgdw-bd",
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
                            "keyPath": "k12n-aigi-01p3-ghwa-63/r7h8-wirp-sm46-d12s-o8/rn6a-r71f-e7j7-2o9n-i5",
                            "key": "rn6a-r71f-e7j7-2o9n-i5",
                            "uuid": "rn6a-r71f-e7j7-2o9n-i5",
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
                            "keyPath": "k12n-aigi-01p3-ghwa-63/r7h8-wirp-sm46-d12s-o8/93ki-999d-rx3c-tk8j-ib",
                            "key": "93ki-999d-rx3c-tk8j-ib",
                            "uuid": "93ki-999d-rx3c-tk8j-ib",
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
                            "keyPath": "k12n-aigi-01p3-ghwa-63/r7h8-wirp-sm46-d12s-o8/eyu1-1vrh-xzi5-6nly-9u",
                            "key": "eyu1-1vrh-xzi5-6nly-9u",
                            "uuid": "eyu1-1vrh-xzi5-6nly-9u",
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
                            "keyPath": "k12n-aigi-01p3-ghwa-63/r7h8-wirp-sm46-d12s-o8/zdj9-eyux-3ajs-190g-rb",
                            "key": "zdj9-eyux-3ajs-190g-rb",
                            "uuid": "zdj9-eyux-3ajs-190g-rb",
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
                            "keyPath": "k12n-aigi-01p3-ghwa-63/r7h8-wirp-sm46-d12s-o8/iw18-wpza-fvmw-2jst-ej",
                            "key": "iw18-wpza-fvmw-2jst-ej",
                            "uuid": "iw18-wpza-fvmw-2jst-ej",
                            "data": {
                                "id": "1",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "SampleTravelDataDerby"
                        }
                    ],
                    "driverType": "Derby",
                    "keyPath": "k12n-aigi-01p3-ghwa-63/r7h8-wirp-sm46-d12s-o8",
                    "key": "r7h8-wirp-sm46-d12s-o8",
                    "uuid": "r7h8-wirp-sm46-d12s-o8",
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
                    "name": "test",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "k12n-aigi-01p3-ghwa-63/ogko-j3k7-31l5-636w-fi",
                    "key": "ogko-j3k7-31l5-636w-fi",
                    "uuid": "ogko-j3k7-31l5-636w-fi"
                }
            ],
            "key": "k12n-aigi-01p3-ghwa-63",
            "uuid": "k12n-aigi-01p3-ghwa-63",
            "keyPath": "k12n-aigi-01p3-ghwa-63",
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
                "dsUUID": "r7h8-wirp-sm46-d12s-o8",
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
                        "connId": "bwsal",
                        "classifier": "db.workflow",
                        "datasourceName": "SampleTravelDataDerby",
                        "dsKeyPath": "k12n-aigi-01p3-ghwa-63/r7h8-wirp-sm46-d12s-o8/9jld-a73p-7ohm-292d-5t",
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
            "connId": "bwsal",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelDataDerby",
            "dsKeyPath": "k12n-aigi-01p3-ghwa-63/r7h8-wirp-sm46-d12s-o8/9jld-a73p-7ohm-292d-5t",
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
            "keyPath": "k12n-aigi-01p3-ghwa-63/r7h8-wirp-sm46-d12s-o8/9jld-a73p-7ohm-292d-5t/41o0-5xiz-lb0t-7m4k-xs",
            "key": "41o0-5xiz-lb0t-7m4k-xs",
            "alias": "geo_cordinates",
            "uuid": "41o0-5xiz-lb0t-7m4k-xs",
            "connId": "bwsal",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "bwsal",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "k12n-aigi-01p3-ghwa-63/r7h8-wirp-sm46-d12s-o8/9jld-a73p-7ohm-292d-5t",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "geo_cordinates_bwsal",
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
            "keyPath": "k12n-aigi-01p3-ghwa-63/r7h8-wirp-sm46-d12s-o8/9jld-a73p-7ohm-292d-5t/b42h-v2zd-detp-fyxe-d8",
            "key": "b42h-v2zd-detp-fyxe-d8",
            "alias": "meeting_details",
            "uuid": "b42h-v2zd-detp-fyxe-d8",
            "connId": "bwsal",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "bwsal",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "k12n-aigi-01p3-ghwa-63/r7h8-wirp-sm46-d12s-o8/9jld-a73p-7ohm-292d-5t",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "meeting_details_bwsal",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "meeting_details"
        }
    },
    "categories": {
        "dk1n-a8po-5zyl-gpd5-0b": {
            "ds": {
                "data": {
                    "id": "1001",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "com.simba.athena.jdbc.Driver",
                "name": "testing_291",
                "classifier": "global",
                "dataSourceType": "Managed DataSource"
            },
            "category": {
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
            }
        },
        "k12n-aigi-01p3-ghwa-63": {
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
            "connId": "bwsal",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelDataDerby",
            "dsKeyPath": "k12n-aigi-01p3-ghwa-63/r7h8-wirp-sm46-d12s-o8/9jld-a73p-7ohm-292d-5t",
            "driverType": "Derby",
            "database": "HIUSER"
        }
    ],
    "changeDSList": {},
    "changedTables": [],
    "changedColumns": [],
    "removedTables": [
        {
            "id": "8a28627d07d04ef096d9935f12e0c7e9",
            "connId": "bwsal",
            "alreadySaved": true
        },
        {
            "id": "4ac5d9f68b58bd7c0d179146e46795be",
            "connId": "bwsal",
            "alreadySaved": true
        },
        {
            "id": "4e1fd245f4d13b77be423a43f01d80b2",
            "connId": "bwsal",
            "alreadySaved": true
        },
        {
            "id": "8a28627d07d04ef096d9935f12e0c7e9",
            "connId": "bwsal"
        },
        {
            "id": "4ac5d9f68b58bd7c0d179146e46795be",
            "connId": "bwsal"
        },
        {
            "id": "4e1fd245f4d13b77be423a43f01d80b2",
            "connId": "bwsal"
        }
    ],
    "removedColumns": [],
    "removedDataSources": [],
    "duplicateColumnList": [],
    "duplicateTableList": [],
    "unsavedViews": [],
    "saveDetails": {
        "location": "Gagan",
        "uuid": "4812_10_save.metadata"
    },
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
            "uuid": "vfw6-e33h-78fj-vydo-oy",
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
            "uuid": "1hfj-5xg9-vcyi-ptxi-wq",
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
            "uuid": "nvjg-sjac-e8p5-mc62-or",
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
            "uuid": "ntyi-iiuf-4768-vvim-jx",
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
            "uuid": "vee4-7h57-kcga-3gmb-ys",
            "index": 5,
            "action": "noChange"
        }
    ],
    "mode": "edit",
    "allTablesKeys": [
        "41o0-5xiz-lb0t-7m4k-xs",
        "b42h-v2zd-detp-fyxe-d8",
        "ev7u-0bca-ujmi-2mx0-1l",
        "5aw9-wbyu-cjml-frlg-kl",
        "3wpv-e5g2-19ax-5tc5-35"
    ],
    "selectedTableKeys": [
        "41o0-5xiz-lb0t-7m4k-xs",
        "b42h-v2zd-detp-fyxe-d8"
    ],
    "metadataName": "4812 10 save",
    "activeDataSource": false,
    "metadataToEdit": false,
    "isSavingInProgress": false,
    "inititalStateFromJest": false,
    "timeStamp": 1655720495460,
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
        "uniqueId": "4812_10_save",
        "tables": {
            "geo_cordinates": {
                "id": "be534112989b616b194bc59c2fb25a42",
                "alias": "geo_cordinates",
                "columns": {
                    "location_id": {
                        "alias": "location_id",
                        "fullyQualifiedColumn": "geo_cordinates.location_id",
                        "columnId": "e1223d3e-83ca-4912-b57d-2c5883dac957",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "location": {
                        "alias": "location",
                        "fullyQualifiedColumn": "geo_cordinates.location",
                        "columnId": "c0c492f6-39da-498a-937d-256338177620",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "latitude": {
                        "alias": "latitude",
                        "fullyQualifiedColumn": "geo_cordinates.latitude",
                        "columnId": "dd0ef55b-4396-4b32-a11a-a39269b3d6dc",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Double": "numeric"
                        }
                    },
                    "longitude": {
                        "alias": "longitude",
                        "fullyQualifiedColumn": "geo_cordinates.longitude",
                        "columnId": "4131e9a4-2336-4c7a-b561-1244189e9d3b",
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
                        "columnId": "a6ccaa2d-2e4e-4d34-b8f0-30f8578c5bb4",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "meeting_date": {
                        "alias": "meeting_date",
                        "fullyQualifiedColumn": "meeting_details.meeting_date",
                        "columnId": "c545b1e2-55b8-46c7-a9cd-96fb301a377a",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.sql.Timestamp": "dateTime"
                        }
                    },
                    "meeting_by": {
                        "alias": "meeting_by",
                        "fullyQualifiedColumn": "meeting_details.meeting_by",
                        "columnId": "b2b3ab03-814e-4b59-a1e2-6b1a76bfc6db",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "client_name": {
                        "alias": "client_name",
                        "fullyQualifiedColumn": "meeting_details.client_name",
                        "columnId": "f12fa8ed-1866-46e6-ad60-6f97ba0a28f2",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "meeting_purpose": {
                        "alias": "meeting_purpose",
                        "fullyQualifiedColumn": "meeting_details.meeting_purpose",
                        "columnId": "b997ea8b-18ef-4477-a65b-0f37741ad6fc",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "meeting_impact": {
                        "alias": "meeting_impact",
                        "fullyQualifiedColumn": "meeting_details.meeting_impact",
                        "columnId": "2edc5ec9-f971-4717-b4e7-1f2009d8310b",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "meet_cancellation_status": {
                        "alias": "meet_cancellation_status",
                        "fullyQualifiedColumn": "meeting_details.meet_cancellation_status",
                        "columnId": "eafbe938-a784-4aeb-9f07-2581703b3fa8",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "cancellation_reason": {
                        "alias": "cancellation_reason",
                        "fullyQualifiedColumn": "meeting_details.cancellation_reason",
                        "columnId": "7332a68b-29c3-4475-8d2f-82c3dd26483b",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    }
                },
                "name": "meeting_details"
            }
        },
        "sets": [
            [
                "geo_cordinates"
            ],
            [
                "meeting_details"
            ]
        ],
        "joins": [],
        "metadataName": "4812 10 save",
        "metadataDir": "Gagan"
    },
    "editMdFromHomeInfo": {
        "dir": "Gagan/4812_10_save.metadata",
        "file": "4812_10_save.metadata"
    }
}