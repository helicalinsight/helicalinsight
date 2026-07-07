export const mockData = {
    getStaticContents: {
        "status": 1,
        "response": {
            "driversList": [
                {
                    "driver": "dynamicSwitch",
                    "showInDatasource": "false",
                    "available": "true",
                    "parameters": []
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
            "dataSourceTypes": [
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
                },
                {
                    "type": "sql.jdbc.groovy",
                    "name": "Groovy Plain Jdbc DataSource",
                    "classifier": "efwd",
                    "categoryName": "advanced",
                    "categoryType": "advanced"
                },
                {
                    "type": "sql.jdbc.groovy.managed",
                    "name": "Groovy Managed Jdbc DataSource",
                    "classifier": "efwd",
                    "categoryName": "advanced",
                    "categoryType": "advanced"
                }
            ],
            "dataSources": [
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
                    "name": "Dremio",
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
                    "name": "Informix",
                    "categoryType": "supported",
                    "categoryName": "Supported"
                },
                {
                    "name": "IBM Db2",
                    "categoryType": "supported",
                    "categoryName": "Supported"
                },
                {
                    "name": "Microsoft Sqlserver",
                    "categoryType": "supported",
                    "categoryName": "Supported"
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
                    "name": "Teradata",
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
                    "name": "Oracle",
                    "categoryType": "supported",
                    "categoryName": "Supported"
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
                    "name": "Amazon Redshift",
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
                    "name": "Amazon Dynamodb",
                    "categoryType": "supported",
                    "categoryName": "Supported"
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
                    "name": "Ξ Add Driver Ξ",
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
                    "name": "Presto",
                    "categoryType": "supported",
                    "categoryName": "Supported"
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
                    "name": "Access",
                    "categoryType": "supported",
                    "categoryName": "Supported"
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
                    "name": "Google Bigquery",
                    "categoryType": "supported",
                    "categoryName": "Supported"
                }
            ]
        }
    },
    listDS: {
        "dataSources": [
            {
                "data": {
                    "id": "1",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "org.apache.derby.jdbc.ClientDriver",
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
                "driver": "com.mysql.jdbc.Driver",
                "name": "MYSQLDS",
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
                "driver": "org.sqlite.JDBC",
                "name": "sqliteDS",
                "classifier": "global",
                "dataSourceType": "Managed DataSource"
            },
            {
                "data": {
                    "id": "1002",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "org.postgresql.Driver",
                "name": "PostgresDS",
                "classifier": "global",
                "dataSourceType": "Managed DataSource"
            },
            {
                "data": {
                    "id": "1003",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "org.postgresql.Driver",
                "name": "PostgresSigleDump",
                "classifier": "global",
                "dataSourceType": "Managed DataSource"
            },
            {
                "data": {
                    "id": "1004",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "com.mysql.jdbc.Driver",
                "name": "mysqlsingledumpDS",
                "classifier": "global",
                "dataSourceType": "Managed DataSource"
            }
        ]
    },
    getMetadata: {
        "status": 1,
        "response": {
            "classifier": "db.generic",
            "name": "SampleTravelData",
            "dataSource": {
                "sync": false,
                "id": "1000",
                "connectionDatabaseId": "e1a18519-2335-4d84-836a-a9ac896db4fa",
                "catSchemaPredicted": false,
                "catalog": "SampleTravelData",
                "schema": "",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc"
            },
            "uniqueId": "Metadata_1",
            "tables": {
                "dimdate": {
                    "id": "9d13652ec6bfac5f3776d2c344a6ed69",
                    "alias": "dimdate",
                    "columns": {
                        "dim_id": {
                            "alias": "dim_id",
                            "fullyQualifiedColumn": "dimdate.dim_id",
                            "columnId": "33e9592f-e7aa-4413-b09b-abafcf2e92db",
                            "defaultFunction": "db.generic.aggregate.sum",
                            "type": {
                                "java.lang.Integer": "numeric"
                            }
                        },
                        "fiscal_year": {
                            "alias": "fiscal_year",
                            "fullyQualifiedColumn": "dimdate.fiscal_year",
                            "columnId": "55281e16-532d-4900-9c60-e93ec97f3c30",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.sql.Date": "date"
                            }
                        },
                        "modified_date": {
                            "alias": "modified_date",
                            "fullyQualifiedColumn": "dimdate.modified_date",
                            "columnId": "5448fc8c-c6d3-4b7b-95d9-0cd1723b9a40",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.sql.Timestamp": "dateTime"
                            }
                        },
                        "date_key": {
                            "alias": "date_key",
                            "fullyQualifiedColumn": "dimdate.date_key",
                            "columnId": "f9ed3bbb-b384-47b3-a760-ac9835221243",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            }
                        },
                        "day_number": {
                            "alias": "day_number",
                            "fullyQualifiedColumn": "dimdate.day_number",
                            "columnId": "4092b752-ac1e-49dc-b6f9-3521cd826c8d",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            }
                        },
                        "fiscal_month_name": {
                            "alias": "fiscal_month_name",
                            "fullyQualifiedColumn": "dimdate.fiscal_month_name",
                            "columnId": "20af1eb2-06a6-48cc-bac3-610821d5b56e",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            }
                        },
                        "fiscal_month_label": {
                            "alias": "fiscal_month_label",
                            "fullyQualifiedColumn": "dimdate.fiscal_month_label",
                            "columnId": "6b09e0de-f1c3-448e-98d8-e21ae53e56ed",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            }
                        },
                        "created_date": {
                            "alias": "created_date_alias",
                            "fullyQualifiedColumn": "dimdate.created_date",
                            "columnId": "ad5f00b9-017a-4c98-84bb-2db1e1beeb71",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            }
                        },
                        "created_time": {
                            "alias": "created_time",
                            "fullyQualifiedColumn": "dimdate.created_time",
                            "columnId": "e2f2f581-c4df-43ed-aa26-61547e3ae856",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            }
                        },
                        "rating": {
                            "alias": "rating",
                            "fullyQualifiedColumn": "dimdate.rating",
                            "columnId": "3c7d79fd-9fa6-493f-89d9-014b539fe2f2",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            }
                        }
                    },
                    "name": "dimdate"
                }
            },
            "sets": [
                [
                    "dimdate"
                ]
            ],
            "joins": [],
            "connections": [
                {
                    "classifier": "db.generic",
                    "name": "HIUSER",
                    "dataSource": {
                        "sync": false,
                        "id": "1",
                        "connectionDatabaseId": "3343e467-3297-46d4-9d0d-f1c2fc0d085d",
                        "catSchemaPredicted": false,
                        "catalog": "",
                        "schema": "HIUSER",
                        "type": "dynamicDataSource",
                        "baseType": "global.jdbc"
                    },
                    "uniqueId": "Metadata_1",
                    "tables": {
                        "dimdate": {
                            "id": "4ac5d9f68b58bd7c0d179146e46795be",
                            "alias": "dimdate",
                            "columns": {
                                "dim_id": {
                                    "alias": "dim_id",
                                    "fullyQualifiedColumn": "dimdate.dim_id",
                                    "columnId": "c7760d25-e64f-48f9-8c2a-cd39790664b4",
                                    "defaultFunction": "db.generic.aggregate.sum",
                                    "type": {
                                        "java.lang.Integer": "numeric"
                                    }
                                },
                                "fiscal_year": {
                                    "alias": "fiscal_year",
                                    "fullyQualifiedColumn": "dimdate.fiscal_year",
                                    "columnId": "21dea209-a887-40da-be0c-20801ae089a9",
                                    "defaultFunction": "db.generic.groupBy.group",
                                    "type": {
                                        "java.sql.Date": "date"
                                    }
                                },
                                "modified_date": {
                                    "alias": "modified_date",
                                    "fullyQualifiedColumn": "dimdate.modified_date",
                                    "columnId": "69a8bc01-f3de-475d-9f7c-f201fae23d4e",
                                    "defaultFunction": "db.generic.groupBy.group",
                                    "type": {
                                        "java.sql.Timestamp": "dateTime"
                                    }
                                },
                                "date_key": {
                                    "alias": "date_key",
                                    "fullyQualifiedColumn": "dimdate.date_key",
                                    "columnId": "d9b30227-4f84-4b04-8ed7-0add299ce117",
                                    "defaultFunction": "db.generic.groupBy.group",
                                    "type": {
                                        "java.lang.String": "text"
                                    }
                                },
                                "day_number": {
                                    "alias": "day_number",
                                    "fullyQualifiedColumn": "dimdate.day_number",
                                    "columnId": "9bae7ba8-b575-4a7c-9df3-1be45dc4055a",
                                    "defaultFunction": "db.generic.groupBy.group",
                                    "type": {
                                        "java.lang.String": "text"
                                    }
                                },
                                "fiscal_month_name": {
                                    "alias": "fiscal_month_name",
                                    "fullyQualifiedColumn": "dimdate.fiscal_month_name",
                                    "columnId": "dd806b38-7743-421c-9139-7966f2daf660",
                                    "defaultFunction": "db.generic.groupBy.group",
                                    "type": {
                                        "java.lang.String": "text"
                                    }
                                },
                                "fiscal_month_label": {
                                    "alias": "fiscal_month_label",
                                    "fullyQualifiedColumn": "dimdate.fiscal_month_label",
                                    "columnId": "dc4223f8-b384-43d5-99c2-728d856e3d38",
                                    "defaultFunction": "db.generic.groupBy.group",
                                    "type": {
                                        "java.lang.String": "text"
                                    }
                                },
                                "created_date": {
                                    "alias": "created_date_alias",
                                    "fullyQualifiedColumn": "dimdate.created_date",
                                    "columnId": "a553e3ba-f088-4ec6-b8fa-40ea2ac9ef82",
                                    "defaultFunction": "db.generic.groupBy.group",
                                    "type": {
                                        "java.lang.String": "text"
                                    }
                                },
                                "created_time": {
                                    "alias": "created_time",
                                    "fullyQualifiedColumn": "dimdate.created_time",
                                    "columnId": "829eac01-444f-43a9-9932-5f5a04c2b3f6",
                                    "defaultFunction": "db.generic.groupBy.group",
                                    "type": {
                                        "java.lang.String": "text"
                                    }
                                },
                                "rating": {
                                    "alias": "rating",
                                    "fullyQualifiedColumn": "dimdate.rating",
                                    "columnId": "50964c89-1c0b-4c6b-806e-fa02d250747f",
                                    "defaultFunction": "db.generic.groupBy.group",
                                    "type": {
                                        "java.lang.String": "text"
                                    }
                                }
                            },
                            "name": "dimdate"
                        }
                    },
                    "sets": [
                        [
                            "dimdate"
                        ]
                    ],
                    "joins": [],
                    "connectionDatabaseId": "3343e467-3297-46d4-9d0d-f1c2fc0d085d"
                }
            ],
            "metadataName": "Metadata_1",
            "metadataDir": "Gagan"
        }
    },
    workflow_1000_cat_schema: {
        "status": 1,
        "response": {
            "classifier": "db.workflow",
            "metadata": {
                "catalogs": [
                    {
                        "name": "SampleTravelData",
                        "schemas": []
                    }
                ]
            }
        },
        "position": "0",
        "maxSize": "1",
        "totalPage": 1,
        "resultPage": 1
    },
    workflow_1_cat_schema: {
        "status": 1,
        "response": {
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
        "position": "0",
        "maxSize": "1",
        "totalPage": 1,
        "resultPage": 1
    },
    workflow_1000_tables: {
        "status": 1,
        "response": {
            "classifier": "db.workflow",
            "metadata": {
                "catalogs": [
                    {
                        "name": "SampleTravelData",
                        "schemas": [
                            {
                                "name": "Null",
                                "tables": [
                                    {
                                        "id": "16639182c9f9434f6c23adc92c13ca91",
                                        "name": "geo_cordinates"
                                    },
                                    {
                                        "id": "28527591b9b87216cf89e68720df9c87",
                                        "name": "meeting_details"
                                    },
                                    {
                                        "id": "152371825108bf241d5e58d460282bf0",
                                        "name": "employee_details"
                                    },
                                    {
                                        "id": "9d13652ec6bfac5f3776d2c344a6ed69",
                                        "name": "dimdate"
                                    },
                                    {
                                        "id": "73210e5e809f316b5b727202caffbf2f",
                                        "name": "tasks"
                                    },
                                    {
                                        "id": "0d08fba10235e4dea8cb57fd92e29e1d",
                                        "name": "travel_details"
                                    }
                                ]
                            }
                        ]
                    }
                ],
                "dataSource": {
                    "id": "1000",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc",
                    "catSchemaPredicted": false,
                    "sync": false,
                    "catalog": "SampleTravelData",
                    "schema": ""
                },
                "name": "SampleTravelData"
            }
        },
        "position": "0",
        "maxSize": "1",
        "totalPage": 1,
        "resultPage": 1
    },
    workflow_1_tables: {
        "status": 1,
        "response": {
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
                    "schema": "HIUSER"
                },
                "name": "HIUSER"
            }
        },
        "position": "0",
        "maxSize": "1",
        "totalPage": 1,
        "resultPage": 1
    }
}