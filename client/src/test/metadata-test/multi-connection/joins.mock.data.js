export const fetchedMetadata = {
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
    "uniqueId": "MysqlDery1",
    "tables": {
        "dimdate": {
            "id": "4ac5d9f68b58bd7c0d179146e46795be",
            "alias": "dimdate",
            "columns": {
                "dim_id": {
                    "alias": "dim_id",
                    "fullyQualifiedColumn": "dimdate.dim_id",
                    "columnId": "0192a847-522c-4a06-85af-93e31b45ebed",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    }
                },
                "fiscal_year": {
                    "alias": "fiscal_year",
                    "fullyQualifiedColumn": "dimdate.fiscal_year",
                    "columnId": "7c8e0aa0-d815-4241-8ad1-62330b9bce64",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.sql.Date": "date"
                    }
                },
                "modified_date": {
                    "alias": "modified_date",
                    "fullyQualifiedColumn": "dimdate.modified_date",
                    "columnId": "d32ef917-bb7d-4f44-835e-58608437d62e",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.sql.Timestamp": "dateTime"
                    }
                },
                "date_key": {
                    "alias": "date_key",
                    "fullyQualifiedColumn": "dimdate.date_key",
                    "columnId": "1e7bc352-b57a-4e91-a649-ecb407e3ef95",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "day_number": {
                    "alias": "day_number",
                    "fullyQualifiedColumn": "dimdate.day_number",
                    "columnId": "94924a35-6325-4f98-8c0a-20a62273436d",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "fiscal_month_name": {
                    "alias": "fiscal_month_name",
                    "fullyQualifiedColumn": "dimdate.fiscal_month_name",
                    "columnId": "6923b00b-0e40-4eb5-be6b-39869d3f016a",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "fiscal_month_label": {
                    "alias": "fiscal_month_label",
                    "fullyQualifiedColumn": "dimdate.fiscal_month_label",
                    "columnId": "65a02bc3-9097-4a38-b256-e7438c6a8384",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "created_date": {
                    "alias": "created_date",
                    "fullyQualifiedColumn": "dimdate.created_date",
                    "columnId": "1746b3f9-1480-4497-a3fe-d66bd24c576e",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "created_time": {
                    "alias": "created_time",
                    "fullyQualifiedColumn": "dimdate.created_time",
                    "columnId": "3bf1a483-417e-418c-abbd-51e6227bbb1c",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "rating": {
                    "alias": "rating",
                    "fullyQualifiedColumn": "dimdate.rating",
                    "columnId": "83dd07ca-7853-435e-b888-13c09e379ef4",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                }
            },
            "name": "dimdate"
        },
        "meeting_details": {
            "id": "9645c648a1c0dbeec1287aaf1e996db3",
            "alias": "meeting_details",
            "columns": {
                "meeting_id": {
                    "alias": "meeting_id",
                    "fullyQualifiedColumn": "meeting_details.meeting_id",
                    "columnId": "58d5c5b6-d3cb-4ee9-b95d-1cdcfbe8e038",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    }
                },
                "meeting_date": {
                    "alias": "meeting_date",
                    "fullyQualifiedColumn": "meeting_details.meeting_date",
                    "columnId": "44422989-997a-40bd-9e87-17f5221b1cb4",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.sql.Timestamp": "dateTime"
                    }
                },
                "meeting_by": {
                    "alias": "meeting_by",
                    "fullyQualifiedColumn": "meeting_details.meeting_by",
                    "columnId": "7eba1e91-83d1-4839-ba2d-7a1beca23778",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    }
                },
                "client_name": {
                    "alias": "client_name",
                    "fullyQualifiedColumn": "meeting_details.client_name",
                    "columnId": "53b1a600-100b-4b9b-a5e2-a817dd5552e8",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "meeting_purpose": {
                    "alias": "meeting_purpose",
                    "fullyQualifiedColumn": "meeting_details.meeting_purpose",
                    "columnId": "d85a2e91-fe18-4e44-a9ec-abdb9fe2a3e8",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "meeting_impact": {
                    "alias": "meeting_impact",
                    "fullyQualifiedColumn": "meeting_details.meeting_impact",
                    "columnId": "20b3237a-5d6d-473b-aeb1-bd4652d4b8c4",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "meet_cancellation_status": {
                    "alias": "meet_cancellation_status",
                    "fullyQualifiedColumn": "meeting_details.meet_cancellation_status",
                    "columnId": "9bedac46-f6ae-420d-a142-2912c455aa75",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "cancellation_reason": {
                    "alias": "cancellation_reason",
                    "fullyQualifiedColumn": "meeting_details.cancellation_reason",
                    "columnId": "68d48a76-7f82-4c9c-8e5c-2b16b6b127e5",
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
            "meeting_details",
            "dimdate"
        ]
    ],
    "joins": [
        {
            "id": "e971fddf3d7b4e9ecbfaada0f65e6051",
            "type": "inner",
            "operator": "=",
            "left": {
                "table": "meeting_details",
                "column": "meeting_impact",
                "alias": "meeting_details.meeting_impact"
            },
            "right": {
                "table": "dimdate",
                "column": "fiscal_month_name",
                "alias": "dimdate.fiscal_month_name"
            }
        }
    ],
    "connections": [
        {
            "classifier": "db.generic",
            "name": "SampleTravelData",
            "dataSource": {
                "sync": false,
                "id": "1002",
                "catSchemaPredicted": false,
                "catalog": "SampleTravelData",
                "schema": "",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc"
            },
            "uniqueId": "MysqlDery1",
            "tables": {
                "tasks": {
                    "id": "73210e5e809f316b5b727202caffbf2f",
                    "alias": "tasks",
                    "columns": {
                        "id": {
                            "alias": "id",
                            "fullyQualifiedColumn": "tasks.id",
                            "columnId": "6a671f38-aaef-4366-bdda-900c8b54fc56",
                            "defaultFunction": "db.generic.aggregate.sum",
                            "type": {
                                "java.lang.Integer": "numeric"
                            }
                        },
                        "title": {
                            "alias": "title",
                            "fullyQualifiedColumn": "tasks.title",
                            "columnId": "d37040d0-9175-459f-a384-43a96a1fc1cf",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            }
                        },
                        "completed": {
                            "alias": "completed",
                            "fullyQualifiedColumn": "tasks.completed",
                            "columnId": "b35dd944-cd9e-45bb-8320-409f7d665dc3",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.Boolean": "boolean"
                            }
                        }
                    },
                    "name": "tasks"
                },
                "employee_details": {
                    "id": "152371825108bf241d5e58d460282bf0",
                    "alias": "employee_details",
                    "columns": {
                        "employee_id": {
                            "alias": "employee_id",
                            "fullyQualifiedColumn": "employee_details.employee_id",
                            "columnId": "c92ce2b3-a3f2-4265-a79b-a92c0cc25ffa",
                            "defaultFunction": "db.generic.aggregate.sum",
                            "type": {
                                "java.lang.Integer": "numeric"
                            }
                        },
                        "employee_name": {
                            "alias": "employee_name",
                            "fullyQualifiedColumn": "employee_details.employee_name",
                            "columnId": "5d670be7-b10f-4a81-9962-e193c6b4a1a1",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            }
                        },
                        "age": {
                            "alias": "age",
                            "fullyQualifiedColumn": "employee_details.age",
                            "columnId": "36112396-65be-4492-9427-760912d2b808",
                            "defaultFunction": "db.generic.aggregate.sum",
                            "type": {
                                "java.lang.Integer": "numeric"
                            }
                        },
                        "address": {
                            "alias": "address",
                            "fullyQualifiedColumn": "employee_details.address",
                            "columnId": "1e90d836-d40e-4447-937c-d2bf8d4f4a02",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            }
                        }
                    },
                    "name": "employee_details"
                }
            },
            "sets": [
                [
                    "employee_details",
                    "tasks"
                ]
            ],
            "joins": [
                {
                    "id": "915eb98b6c4207de4a384e880e42a5d7",
                    "type": "inner",
                    "operator": "=",
                    "left": {
                        "table": "employee_details",
                        "column": "employee_name",
                        "alias": "employee_details.employee_name"
                    },
                    "right": {
                        "table": "tasks",
                        "column": "title",
                        "alias": "tasks.title"
                    }
                },
                {
                    "id": "96883c9278fece1a0e0e064cdbd21317",
                    "type": "inner",
                    "operator": "=",
                    "left": {
                        "table": "employee_details",
                        "column": "employee_id",
                        "alias": "employee_details.employee_id"
                    },
                    "right": {
                        "table": "tasks",
                        "column": "id",
                        "alias": "tasks.id"
                    }
                },
                {
                    "id": "9cae88661ca9f504326fca081ecc7dde",
                    "type": "inner",
                    "operator": "=",
                    "left": {
                        "table": "employee_details",
                        "column": "employee_id",
                        "alias": "employee_details.employee_id"
                    },
                    "right": {
                        "table": "tasks",
                        "column": "title",
                        "alias": "tasks.title"
                    }
                },
                {
                    "id": "3e43899b016ada2b78831d64739a4ca8",
                    "type": "inner",
                    "operator": "=",
                    "left": {
                        "table": "employee_details",
                        "column": "employee_name",
                        "alias": "employee_details.employee_name"
                    },
                    "right": {
                        "table": "employee_details",
                        "column": "address",
                        "alias": "employee_details.address"
                    }
                },
                {
                    "id": "3e43899b016ada2b78831d64739a4ca8",
                    "type": "inner",
                    "operator": "=",
                    "left": {
                        "table": "employee_details",
                        "column": "employee_name",
                        "alias": "employee_details.employee_name"
                    },
                    "right": {
                        "table": "employee_details",
                        "column": "address",
                        "alias": "employee_details.address"
                    }
                },
                {
                    "id": "3e43899b016ada2b78831d64739a4ca8",
                    "type": "inner",
                    "operator": "=",
                    "left": {
                        "table": "employee_details",
                        "column": "employee_name",
                        "alias": "employee_details.employee_name"
                    },
                    "right": {
                        "table": "employee_details",
                        "column": "address",
                        "alias": "employee_details.address"
                    }
                }
            ],
            "connectionDatabaseId": "e31a96c0-c598-457c-9d77-12ed5df36672"
        }
    ],
    "metadataName": "Metadata_1",
    "metadataDir": "Gagan"
}

export const joinsMDStore = {
    "dataFetchedFor": {
        "getDatasource": true,
        "joins": true,
        "viewSessionVariables": false,
        "listDataSources": true,
        "wfct-05ig-dtk0-wkgm-bn": false,
        "f5tw-vauy-wgvt-vcma-cq": false,
        "dk1y-1fas-qtwx-8611-2f": false,
        "6bpm-kwn2-7waq-yubu-al": false
    },
    "loadingStatus": {
        "getDatasource": true,
        "listDataSources": true,
        "joins": true,
        "wfct-05ig-dtk0-wkgm-bn": false,
        "f5tw-vauy-wgvt-vcma-cq": false,
        "dk1y-1fas-qtwx-8611-2f": false,
        "6bpm-kwn2-7waq-yubu-al": false
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
            "name": "Presto",
            "categoryType": "supported",
            "categoryName": "Supported"
        },
        {
            "driver": "org.apache.spark.deploy.DeployMessages$RequestKillDriver",
            "databaseDialect": "",
            "name": "Spark Deploy",
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
            "driver": "org.apache.spark.deploy.DeployMessages$RequestKillDriver",
            "databaseDialect": "",
            "name": "Spark Deploy",
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
            "available": "true",
            "driver": "org.apache.spark.deploy.DeployMessages$RequestKillDriver"
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
    "allDataSources": [
        {
            "permissionLevel": 5,
            "driver": "dynamicSwitch",
            "name": "DifferentConnections",
            "classifier": "efwd",
            "type": "sql.jdbc.groovy.managed",
            "data": {
                "dir": "Groovy_Manged",
                "driverName": null,
                "type": "sql.jdbc.groovy.managed",
                "id": 3,
                "userName": null,
                "password": null,
                "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1002);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n           responseJson.put(\"globalId\", 1);\n           responseJson.put(\"catalog\",\"\");\n\t\t  responseJson.put(\"schema\",\"HIUSER\");\n          responseJson.put(\"database\",\"HIUSER\")\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 1006);\n           responseJson.put(\"catalog\",\"SampleTravelData\");\n\t\t  responseJson.put(\"schema\",\"public\");\n          responseJson.put(\"database\",\"SampleTravelData.public\")\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
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
            "driver": "org.apache.derby.jdbc.ClientDriver",
            "name": "SampleTravelData",
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
                "id": "1002",
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
                "id": "1005",
                "type": "dynamicDataSource"
            },
            "dataSourceProvider": "tomcat",
            "type": "dynamicDataSource",
            "permissionLevel": 5,
            "driver": "com.mysql.jdbc.Driver",
            "name": "MysqlDynamic",
            "classifier": "global",
            "dataSourceType": "Managed DataSource"
        },
        {
            "data": {
                "id": "1006",
                "type": "dynamicDataSource"
            },
            "dataSourceProvider": "tomcat",
            "type": "dynamicDataSource",
            "permissionLevel": 5,
            "driver": "org.postgresql.Driver",
            "name": "Postgresql",
            "classifier": "global",
            "dataSourceType": "Managed DataSource"
        },
        {
            "data": {
                "id": "1007",
                "type": "dynamicDataSource"
            },
            "dataSourceProvider": "tomcat",
            "type": "dynamicDataSource",
            "permissionLevel": 5,
            "driver": "com.mysql.jdbc.Driver",
            "name": "MysqlDS",
            "classifier": "global",
            "dataSourceType": "Managed DataSource"
        },
        {
            "data": {
                "id": "1008",
                "type": "dynamicDataSource"
            },
            "dataSourceProvider": "tomcat",
            "type": "dynamicDataSource",
            "permissionLevel": 5,
            "driver": "org.sqlite.JDBC",
            "name": "SalLite",
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
                    "driver": "org.apache.derby.jdbc.ClientDriver",
                    "name": "SampleTravelDataDerby",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource",
                    "category": "dataSource",
                    "children": [
                        {
                            "name": "SQLJ",
                            "children": [],
                            "keyPath": "1mpi-l9yu-18oi-gfc7-2p/wfct-05ig-dtk0-wkgm-bn/zkaq-ivo0-ipe3-n0aw-w7",
                            "key": "zkaq-ivo0-ipe3-n0aw-w7",
                            "uuid": "zkaq-ivo0-ipe3-n0aw-w7",
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
                            "keyPath": "1mpi-l9yu-18oi-gfc7-2p/wfct-05ig-dtk0-wkgm-bn/obuf-jb8n-2klp-acgc-05",
                            "key": "obuf-jb8n-2klp-acgc-05",
                            "uuid": "obuf-jb8n-2klp-acgc-05",
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
                            "keyPath": "1mpi-l9yu-18oi-gfc7-2p/wfct-05ig-dtk0-wkgm-bn/xwsk-vea6-2hx2-e15q-ku",
                            "key": "xwsk-vea6-2hx2-e15q-ku",
                            "uuid": "xwsk-vea6-2hx2-e15q-ku",
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
                                    "keyPath": "1mpi-l9yu-18oi-gfc7-2p/wfct-05ig-dtk0-wkgm-bn/dk1y-1fas-qtwx-8611-2f/cadj-2a8p-mm88-iysa-pl",
                                    "key": "cadj-2a8p-mm88-iysa-pl",
                                    "alias": "dimdate",
                                    "uuid": "cadj-2a8p-mm88-iysa-pl",
                                    "connId": "a6kb7",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "a6kb7",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "1mpi-l9yu-18oi-gfc7-2p/wfct-05ig-dtk0-wkgm-bn/dk1y-1fas-qtwx-8611-2f",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "dimdate_a6kb7",
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
                                    "keyPath": "1mpi-l9yu-18oi-gfc7-2p/wfct-05ig-dtk0-wkgm-bn/dk1y-1fas-qtwx-8611-2f/v8dn-a46o-asze-t5p8-gc",
                                    "key": "v8dn-a46o-asze-t5p8-gc",
                                    "alias": "employee_details",
                                    "uuid": "v8dn-a46o-asze-t5p8-gc",
                                    "connId": "a6kb7",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "a6kb7",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "1mpi-l9yu-18oi-gfc7-2p/wfct-05ig-dtk0-wkgm-bn/dk1y-1fas-qtwx-8611-2f",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "employee_details_a6kb7",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": false
                                },
                                {
                                    "id": "be534112989b616b194bc59c2fb25a42",
                                    "name": "geo_cordinates",
                                    "data": {
                                        "id": "1",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "1mpi-l9yu-18oi-gfc7-2p/wfct-05ig-dtk0-wkgm-bn/dk1y-1fas-qtwx-8611-2f/5m1w-jq3q-8mf9-366z-cz",
                                    "key": "5m1w-jq3q-8mf9-366z-cz",
                                    "alias": "geo_cordinates",
                                    "uuid": "5m1w-jq3q-8mf9-366z-cz",
                                    "connId": "a6kb7",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "a6kb7",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "1mpi-l9yu-18oi-gfc7-2p/wfct-05ig-dtk0-wkgm-bn/dk1y-1fas-qtwx-8611-2f",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "geo_cordinates_a6kb7",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": false
                                },
                                {
                                    "id": "9645c648a1c0dbeec1287aaf1e996db3",
                                    "name": "meeting_details",
                                    "data": {
                                        "id": "1",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "1mpi-l9yu-18oi-gfc7-2p/wfct-05ig-dtk0-wkgm-bn/dk1y-1fas-qtwx-8611-2f/lbgi-sqwc-5csw-d7u5-x8",
                                    "key": "lbgi-sqwc-5csw-d7u5-x8",
                                    "alias": "meeting_details",
                                    "uuid": "lbgi-sqwc-5csw-d7u5-x8",
                                    "connId": "a6kb7",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "a6kb7",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "1mpi-l9yu-18oi-gfc7-2p/wfct-05ig-dtk0-wkgm-bn/dk1y-1fas-qtwx-8611-2f",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "meeting_details_a6kb7",
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
                                    "keyPath": "1mpi-l9yu-18oi-gfc7-2p/wfct-05ig-dtk0-wkgm-bn/dk1y-1fas-qtwx-8611-2f/u73q-agil-nrhc-d3c3-2a",
                                    "key": "u73q-agil-nrhc-d3c3-2a",
                                    "alias": "travel_details",
                                    "uuid": "u73q-agil-nrhc-d3c3-2a",
                                    "connId": "a6kb7",
                                    "dataSource": {
                                        "id": "1",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "",
                                        "schema": "HIUSER",
                                        "connId": "a6kb7",
                                        "classifier": "db.workflow",
                                        "datasourceName": "SampleTravelDataDerby",
                                        "dsKeyPath": "1mpi-l9yu-18oi-gfc7-2p/wfct-05ig-dtk0-wkgm-bn/dk1y-1fas-qtwx-8611-2f",
                                        "driverType": "Derby",
                                        "database": "HIUSER"
                                    },
                                    "dataSourceName": "SampleTravelDataDerby",
                                    "category": "table",
                                    "nameWithConnId": "travel_details_a6kb7",
                                    "database": "HIUSER",
                                    "schema": "HIUSER",
                                    "selected": false
                                }
                            ],
                            "keyPath": "1mpi-l9yu-18oi-gfc7-2p/wfct-05ig-dtk0-wkgm-bn/dk1y-1fas-qtwx-8611-2f",
                            "key": "dk1y-1fas-qtwx-8611-2f",
                            "uuid": "dk1y-1fas-qtwx-8611-2f",
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
                            "keyPath": "1mpi-l9yu-18oi-gfc7-2p/wfct-05ig-dtk0-wkgm-bn/1gpc-elt4-43c7-r90a-gd",
                            "key": "1gpc-elt4-43c7-r90a-gd",
                            "uuid": "1gpc-elt4-43c7-r90a-gd",
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
                            "keyPath": "1mpi-l9yu-18oi-gfc7-2p/wfct-05ig-dtk0-wkgm-bn/nvls-b8yw-3lx3-4fha-o1",
                            "key": "nvls-b8yw-3lx3-4fha-o1",
                            "uuid": "nvls-b8yw-3lx3-4fha-o1",
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
                            "keyPath": "1mpi-l9yu-18oi-gfc7-2p/wfct-05ig-dtk0-wkgm-bn/y27y-hbij-1zqe-10jg-ep",
                            "key": "y27y-hbij-1zqe-10jg-ep",
                            "uuid": "y27y-hbij-1zqe-10jg-ep",
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
                            "keyPath": "1mpi-l9yu-18oi-gfc7-2p/wfct-05ig-dtk0-wkgm-bn/p3bh-920u-toop-rf65-ux",
                            "key": "p3bh-920u-toop-rf65-ux",
                            "uuid": "p3bh-920u-toop-rf65-ux",
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
                            "keyPath": "1mpi-l9yu-18oi-gfc7-2p/wfct-05ig-dtk0-wkgm-bn/xk48-rnhk-686w-9j9o-o4",
                            "key": "xk48-rnhk-686w-9j9o-o4",
                            "uuid": "xk48-rnhk-686w-9j9o-o4",
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
                            "keyPath": "1mpi-l9yu-18oi-gfc7-2p/wfct-05ig-dtk0-wkgm-bn/tn12-ijl7-0a61-vlp6-zc",
                            "key": "tn12-ijl7-0a61-vlp6-zc",
                            "uuid": "tn12-ijl7-0a61-vlp6-zc",
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
                            "keyPath": "1mpi-l9yu-18oi-gfc7-2p/wfct-05ig-dtk0-wkgm-bn/xejx-v0ox-0sh8-x98q-22",
                            "key": "xejx-v0ox-0sh8-x98q-22",
                            "uuid": "xejx-v0ox-0sh8-x98q-22",
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
                            "keyPath": "1mpi-l9yu-18oi-gfc7-2p/wfct-05ig-dtk0-wkgm-bn/2ox6-cjuv-69xo-lngi-mk",
                            "key": "2ox6-cjuv-69xo-lngi-mk",
                            "uuid": "2ox6-cjuv-69xo-lngi-mk",
                            "data": {
                                "id": "1",
                                "type": "dynamicDataSource"
                            },
                            "category": "schema",
                            "datasourceName": "SampleTravelDataDerby"
                        }
                    ],
                    "driverType": "Derby",
                    "keyPath": "1mpi-l9yu-18oi-gfc7-2p/wfct-05ig-dtk0-wkgm-bn",
                    "key": "wfct-05ig-dtk0-wkgm-bn",
                    "uuid": "wfct-05ig-dtk0-wkgm-bn",
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
                    "driver": "org.apache.derby.jdbc.ClientDriver",
                    "name": "SampleTravelData",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Derby",
                    "keyPath": "1mpi-l9yu-18oi-gfc7-2p/83rr-kvhi-my42-13ah-lv",
                    "key": "83rr-kvhi-my42-13ah-lv",
                    "uuid": "83rr-kvhi-my42-13ah-lv"
                }
            ],
            "key": "1mpi-l9yu-18oi-gfc7-2p",
            "uuid": "1mpi-l9yu-18oi-gfc7-2p",
            "keyPath": "1mpi-l9yu-18oi-gfc7-2p",
            "category": "dsGroup",
            "imgUrl": "images/data_sources/defaut_datasource.png"
        },
        {
            "name": "DynamicSwitch",
            "children": [
                {
                    "permissionLevel": 5,
                    "driver": "dynamicSwitch",
                    "name": "DifferentConnections",
                    "classifier": "efwd",
                    "type": "sql.jdbc.groovy.managed",
                    "data": {
                        "dir": "Groovy_Manged",
                        "driverName": null,
                        "type": "sql.jdbc.groovy.managed",
                        "id": 3,
                        "userName": null,
                        "password": null,
                        "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1002);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n           responseJson.put(\"globalId\", 1);\n           responseJson.put(\"catalog\",\"\");\n\t\t  responseJson.put(\"schema\",\"HIUSER\");\n          responseJson.put(\"database\",\"HIUSER\")\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 1006);\n           responseJson.put(\"catalog\",\"SampleTravelData\");\n\t\t  responseJson.put(\"schema\",\"public\");\n          responseJson.put(\"database\",\"SampleTravelData.public\")\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
                        "jdbcUrl": null,
                        "driver": "dynamicSwitch"
                    },
                    "dataSourceType": "",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "DynamicSwitch",
                    "keyPath": "ftfo-8iqt-8ibw-o88w-c5/ataf-fz1v-95iw-ovgm-yv",
                    "key": "ataf-fz1v-95iw-ovgm-yv",
                    "uuid": "ataf-fz1v-95iw-ovgm-yv"
                }
            ],
            "key": "ftfo-8iqt-8ibw-o88w-c5",
            "uuid": "ftfo-8iqt-8ibw-o88w-c5",
            "keyPath": "ftfo-8iqt-8ibw-o88w-c5",
            "category": "dsGroup",
            "imgUrl": "images/data_sources/defaut_datasource.png"
        },
        {
            "name": "Mysql",
            "children": [
                {
                    "data": {
                        "id": "1002",
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
                    "children": [
                        {
                            "name": "SampleTravelData",
                            "schemas": [],
                            "children": [
                                {
                                    "id": "9d13652ec6bfac5f3776d2c344a6ed69",
                                    "name": "dimdate",
                                    "data": {
                                        "id": "1002",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "0krv-iw11-nwwx-d903-ga/f5tw-vauy-wgvt-vcma-cq/6bpm-kwn2-7waq-yubu-al/gjbx-b8at-5fm2-cm2o-gi",
                                    "key": "gjbx-b8at-5fm2-cm2o-gi",
                                    "alias": "dimdate",
                                    "uuid": "gjbx-b8at-5fm2-cm2o-gi",
                                    "connId": "sohod",
                                    "dataSource": {
                                        "id": "1002",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "SampleTravelData",
                                        "schema": "",
                                        "connId": "sohod",
                                        "classifier": "db.workflow",
                                        "datasourceName": "MySql",
                                        "dsKeyPath": "0krv-iw11-nwwx-d903-ga/f5tw-vauy-wgvt-vcma-cq/6bpm-kwn2-7waq-yubu-al",
                                        "driverType": "Mysql",
                                        "database": "SampleTravelData"
                                    },
                                    "dataSourceName": "MySql",
                                    "category": "table",
                                    "nameWithConnId": "dimdate_sohod",
                                    "database": "SampleTravelData",
                                    "catalog": "SampleTravelData",
                                    "selected": false
                                },
                                {
                                    "id": "152371825108bf241d5e58d460282bf0",
                                    "name": "employee_details",
                                    "data": {
                                        "id": "1002",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "0krv-iw11-nwwx-d903-ga/f5tw-vauy-wgvt-vcma-cq/6bpm-kwn2-7waq-yubu-al/oy9e-kxty-f40l-mqob-ns",
                                    "key": "oy9e-kxty-f40l-mqob-ns",
                                    "alias": "employee_details",
                                    "uuid": "oy9e-kxty-f40l-mqob-ns",
                                    "connId": "sohod",
                                    "dataSource": {
                                        "id": "1002",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "SampleTravelData",
                                        "schema": "",
                                        "connId": "sohod",
                                        "classifier": "db.workflow",
                                        "datasourceName": "MySql",
                                        "dsKeyPath": "0krv-iw11-nwwx-d903-ga/f5tw-vauy-wgvt-vcma-cq/6bpm-kwn2-7waq-yubu-al",
                                        "driverType": "Mysql",
                                        "database": "SampleTravelData"
                                    },
                                    "dataSourceName": "MySql",
                                    "category": "table",
                                    "nameWithConnId": "employee_details_sohod",
                                    "database": "SampleTravelData",
                                    "catalog": "SampleTravelData",
                                    "selected": true
                                },
                                {
                                    "id": "16639182c9f9434f6c23adc92c13ca91",
                                    "name": "geo_cordinates",
                                    "data": {
                                        "id": "1002",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "0krv-iw11-nwwx-d903-ga/f5tw-vauy-wgvt-vcma-cq/6bpm-kwn2-7waq-yubu-al/p0ix-tmb4-77z2-w9iv-y9",
                                    "key": "p0ix-tmb4-77z2-w9iv-y9",
                                    "alias": "geo_cordinates",
                                    "uuid": "p0ix-tmb4-77z2-w9iv-y9",
                                    "connId": "sohod",
                                    "dataSource": {
                                        "id": "1002",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "SampleTravelData",
                                        "schema": "",
                                        "connId": "sohod",
                                        "classifier": "db.workflow",
                                        "datasourceName": "MySql",
                                        "dsKeyPath": "0krv-iw11-nwwx-d903-ga/f5tw-vauy-wgvt-vcma-cq/6bpm-kwn2-7waq-yubu-al",
                                        "driverType": "Mysql",
                                        "database": "SampleTravelData"
                                    },
                                    "dataSourceName": "MySql",
                                    "category": "table",
                                    "nameWithConnId": "geo_cordinates_sohod",
                                    "database": "SampleTravelData",
                                    "catalog": "SampleTravelData",
                                    "selected": false
                                },
                                {
                                    "id": "28527591b9b87216cf89e68720df9c87",
                                    "name": "meeting_details",
                                    "data": {
                                        "id": "1002",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "0krv-iw11-nwwx-d903-ga/f5tw-vauy-wgvt-vcma-cq/6bpm-kwn2-7waq-yubu-al/0zql-6scw-3ka8-cn3q-z2",
                                    "key": "0zql-6scw-3ka8-cn3q-z2",
                                    "alias": "meeting_details",
                                    "uuid": "0zql-6scw-3ka8-cn3q-z2",
                                    "connId": "sohod",
                                    "dataSource": {
                                        "id": "1002",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "SampleTravelData",
                                        "schema": "",
                                        "connId": "sohod",
                                        "classifier": "db.workflow",
                                        "datasourceName": "MySql",
                                        "dsKeyPath": "0krv-iw11-nwwx-d903-ga/f5tw-vauy-wgvt-vcma-cq/6bpm-kwn2-7waq-yubu-al",
                                        "driverType": "Mysql",
                                        "database": "SampleTravelData"
                                    },
                                    "dataSourceName": "MySql",
                                    "category": "table",
                                    "nameWithConnId": "meeting_details_sohod",
                                    "database": "SampleTravelData",
                                    "catalog": "SampleTravelData",
                                    "selected": false
                                },
                                {
                                    "id": "73210e5e809f316b5b727202caffbf2f",
                                    "name": "tasks",
                                    "data": {
                                        "id": "1002",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "0krv-iw11-nwwx-d903-ga/f5tw-vauy-wgvt-vcma-cq/6bpm-kwn2-7waq-yubu-al/ombn-z1uj-ir5a-a0at-d0",
                                    "key": "ombn-z1uj-ir5a-a0at-d0",
                                    "alias": "tasks",
                                    "uuid": "ombn-z1uj-ir5a-a0at-d0",
                                    "connId": "sohod",
                                    "dataSource": {
                                        "id": "1002",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "SampleTravelData",
                                        "schema": "",
                                        "connId": "sohod",
                                        "classifier": "db.workflow",
                                        "datasourceName": "MySql",
                                        "dsKeyPath": "0krv-iw11-nwwx-d903-ga/f5tw-vauy-wgvt-vcma-cq/6bpm-kwn2-7waq-yubu-al",
                                        "driverType": "Mysql",
                                        "database": "SampleTravelData"
                                    },
                                    "dataSourceName": "MySql",
                                    "category": "table",
                                    "nameWithConnId": "tasks_sohod",
                                    "database": "SampleTravelData",
                                    "catalog": "SampleTravelData",
                                    "selected": true
                                },
                                {
                                    "id": "0d08fba10235e4dea8cb57fd92e29e1d",
                                    "name": "travel_details",
                                    "data": {
                                        "id": "1002",
                                        "type": "dynamicDataSource"
                                    },
                                    "keyPath": "0krv-iw11-nwwx-d903-ga/f5tw-vauy-wgvt-vcma-cq/6bpm-kwn2-7waq-yubu-al/6yco-gxei-0xu3-6tk0-75",
                                    "key": "6yco-gxei-0xu3-6tk0-75",
                                    "alias": "travel_details",
                                    "uuid": "6yco-gxei-0xu3-6tk0-75",
                                    "connId": "sohod",
                                    "dataSource": {
                                        "id": "1002",
                                        "type": "dynamicDataSource",
                                        "baseType": "global.jdbc",
                                        "catSchemaPredicted": false,
                                        "sync": false,
                                        "catalog": "SampleTravelData",
                                        "schema": "",
                                        "connId": "sohod",
                                        "classifier": "db.workflow",
                                        "datasourceName": "MySql",
                                        "dsKeyPath": "0krv-iw11-nwwx-d903-ga/f5tw-vauy-wgvt-vcma-cq/6bpm-kwn2-7waq-yubu-al",
                                        "driverType": "Mysql",
                                        "database": "SampleTravelData"
                                    },
                                    "dataSourceName": "MySql",
                                    "category": "table",
                                    "nameWithConnId": "travel_details_sohod",
                                    "database": "SampleTravelData",
                                    "catalog": "SampleTravelData",
                                    "selected": false
                                }
                            ],
                            "keyPath": "0krv-iw11-nwwx-d903-ga/f5tw-vauy-wgvt-vcma-cq/6bpm-kwn2-7waq-yubu-al",
                            "key": "6bpm-kwn2-7waq-yubu-al",
                            "uuid": "6bpm-kwn2-7waq-yubu-al",
                            "data": {
                                "id": "1002",
                                "type": "dynamicDataSource"
                            },
                            "category": "catalog",
                            "datasourceName": "MySql",
                            "fetched": true
                        }
                    ],
                    "driverType": "Mysql",
                    "keyPath": "0krv-iw11-nwwx-d903-ga/f5tw-vauy-wgvt-vcma-cq",
                    "key": "f5tw-vauy-wgvt-vcma-cq",
                    "uuid": "f5tw-vauy-wgvt-vcma-cq"
                },
                {
                    "data": {
                        "id": "1005",
                        "type": "dynamicDataSource"
                    },
                    "dataSourceProvider": "tomcat",
                    "type": "dynamicDataSource",
                    "permissionLevel": 5,
                    "driver": "com.mysql.jdbc.Driver",
                    "name": "MysqlDynamic",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Mysql",
                    "keyPath": "0krv-iw11-nwwx-d903-ga/ni7t-zdcd-ee38-z0nn-85",
                    "key": "ni7t-zdcd-ee38-z0nn-85",
                    "uuid": "ni7t-zdcd-ee38-z0nn-85"
                },
                {
                    "data": {
                        "id": "1007",
                        "type": "dynamicDataSource"
                    },
                    "dataSourceProvider": "tomcat",
                    "type": "dynamicDataSource",
                    "permissionLevel": 5,
                    "driver": "com.mysql.jdbc.Driver",
                    "name": "MysqlDS",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Mysql",
                    "keyPath": "0krv-iw11-nwwx-d903-ga/je09-plvt-111e-eskx-tv",
                    "key": "je09-plvt-111e-eskx-tv",
                    "uuid": "je09-plvt-111e-eskx-tv"
                }
            ],
            "key": "0krv-iw11-nwwx-d903-ga",
            "uuid": "0krv-iw11-nwwx-d903-ga",
            "keyPath": "0krv-iw11-nwwx-d903-ga",
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
                    "keyPath": "34f6-t7g1-5otk-7dku-t6/buzf-p2w7-xacl-zcsk-az",
                    "key": "buzf-p2w7-xacl-zcsk-az",
                    "uuid": "buzf-p2w7-xacl-zcsk-az"
                },
                {
                    "data": {
                        "id": "1006",
                        "type": "dynamicDataSource"
                    },
                    "dataSourceProvider": "tomcat",
                    "type": "dynamicDataSource",
                    "permissionLevel": 5,
                    "driver": "org.postgresql.Driver",
                    "name": "Postgresql",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Postgresql",
                    "keyPath": "34f6-t7g1-5otk-7dku-t6/0wzl-zhmp-u2iu-gxil-t5",
                    "key": "0wzl-zhmp-u2iu-gxil-t5",
                    "uuid": "0wzl-zhmp-u2iu-gxil-t5"
                }
            ],
            "key": "34f6-t7g1-5otk-7dku-t6",
            "uuid": "34f6-t7g1-5otk-7dku-t6",
            "keyPath": "34f6-t7g1-5otk-7dku-t6",
            "category": "dsGroup",
            "imgUrl": "images/data_sources/defaut_datasource.png"
        },
        {
            "name": "Sqlite",
            "children": [
                {
                    "data": {
                        "id": "1008",
                        "type": "dynamicDataSource"
                    },
                    "dataSourceProvider": "tomcat",
                    "type": "dynamicDataSource",
                    "permissionLevel": 5,
                    "driver": "org.sqlite.JDBC",
                    "name": "SalLite",
                    "classifier": "global",
                    "dataSourceType": "Managed DataSource",
                    "category": "dataSource",
                    "children": [],
                    "driverType": "Sqlite",
                    "keyPath": "wlqg-9dhe-mroo-tbck-i4/j6ze-l4rx-v6nr-fc07-2m",
                    "key": "j6ze-l4rx-v6nr-fc07-2m",
                    "uuid": "j6ze-l4rx-v6nr-fc07-2m"
                }
            ],
            "key": "wlqg-9dhe-mroo-tbck-i4",
            "uuid": "wlqg-9dhe-mroo-tbck-i4",
            "keyPath": "wlqg-9dhe-mroo-tbck-i4",
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
                "dsUUID": "wfct-05ig-dtk0-wkgm-bn",
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
                "fetchSchemas": true,
                "fetchCatalogs": true,
                "working": true,
                "connData": {
                    "id": "1002",
                    "type": "dynamicDataSource"
                },
                "connId": "1002",
                "dsUUID": "f5tw-vauy-wgvt-vcma-cq",
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
            {
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
                        "id": "1002",
                        "type": "dynamicDataSource",
                        "baseType": "global.jdbc",
                        "catSchemaPredicted": false,
                        "sync": false,
                        "catalog": "SampleTravelData",
                        "schema": "",
                        "connId": "sohod",
                        "classifier": "db.workflow",
                        "datasourceName": "MySql",
                        "dsKeyPath": "0krv-iw11-nwwx-d903-ga/f5tw-vauy-wgvt-vcma-cq/6bpm-kwn2-7waq-yubu-al",
                        "driverType": "Mysql",
                        "database": "SampleTravelData"
                    },
                    "name": "SampleTravelData"
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
            "connId": "a6kb7",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelDataDerby",
            "dsKeyPath": "1mpi-l9yu-18oi-gfc7-2p/wfct-05ig-dtk0-wkgm-bn/dk1y-1fas-qtwx-8611-2f",
            "driverType": "Derby",
            "database": "HIUSER"
        },
        {
            "id": "1002",
            "type": "dynamicDataSource",
            "baseType": "global.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "SampleTravelData",
            "schema": "",
            "connId": "sohod",
            "classifier": "db.workflow",
            "datasourceName": "MySql",
            "dsKeyPath": "0krv-iw11-nwwx-d903-ga/f5tw-vauy-wgvt-vcma-cq/6bpm-kwn2-7waq-yubu-al",
            "driverType": "Mysql",
            "database": "SampleTravelData"
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
            "keyPath": "1mpi-l9yu-18oi-gfc7-2p/wfct-05ig-dtk0-wkgm-bn/dk1y-1fas-qtwx-8611-2f/cadj-2a8p-mm88-iysa-pl",
            "key": "cadj-2a8p-mm88-iysa-pl",
            "alias": "dimdate",
            "uuid": "cadj-2a8p-mm88-iysa-pl",
            "connId": "a6kb7",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "a6kb7",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "1mpi-l9yu-18oi-gfc7-2p/wfct-05ig-dtk0-wkgm-bn/dk1y-1fas-qtwx-8611-2f",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "dimdate_a6kb7",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "dimdate"
        },
        "meeting_details": {
            "id": "9645c648a1c0dbeec1287aaf1e996db3",
            "name": "meeting_details",
            "data": {
                "id": "1",
                "type": "dynamicDataSource"
            },
            "keyPath": "1mpi-l9yu-18oi-gfc7-2p/wfct-05ig-dtk0-wkgm-bn/dk1y-1fas-qtwx-8611-2f/lbgi-sqwc-5csw-d7u5-x8",
            "key": "lbgi-sqwc-5csw-d7u5-x8",
            "alias": "meeting_details",
            "uuid": "lbgi-sqwc-5csw-d7u5-x8",
            "connId": "a6kb7",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "a6kb7",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "1mpi-l9yu-18oi-gfc7-2p/wfct-05ig-dtk0-wkgm-bn/dk1y-1fas-qtwx-8611-2f",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "meeting_details_a6kb7",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "meeting_details"
        },
        "employee_details": {
            "id": "152371825108bf241d5e58d460282bf0",
            "name": "employee_details",
            "data": {
                "id": "1002",
                "type": "dynamicDataSource"
            },
            "keyPath": "0krv-iw11-nwwx-d903-ga/f5tw-vauy-wgvt-vcma-cq/6bpm-kwn2-7waq-yubu-al/oy9e-kxty-f40l-mqob-ns",
            "key": "oy9e-kxty-f40l-mqob-ns",
            "alias": "employee_details",
            "uuid": "oy9e-kxty-f40l-mqob-ns",
            "connId": "sohod",
            "dataSource": {
                "id": "1002",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "SampleTravelData",
                "schema": "",
                "connId": "sohod",
                "classifier": "db.workflow",
                "datasourceName": "MySql",
                "dsKeyPath": "0krv-iw11-nwwx-d903-ga/f5tw-vauy-wgvt-vcma-cq/6bpm-kwn2-7waq-yubu-al",
                "driverType": "Mysql",
                "database": "SampleTravelData"
            },
            "dataSourceName": "MySql",
            "category": "table",
            "nameWithConnId": "employee_details_sohod",
            "database": "SampleTravelData",
            "catalog": "SampleTravelData",
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
                    "originalId": "c92ce2b3-a3f2-4265-a79b-a92c0cc25ffa",
                    "tableKey": "employee_details",
                    "connId": "sohod",
                    "duplicateIndex": 0,
                    "columnKey": "employee_id",
                    "_columnId": "c92ce2b3-a3f2-4265-a79b-a92c0cc25ffa",
                    "uuid": "obe0-ns21-amhs-z9fh-cn"
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
                    "originalId": "5d670be7-b10f-4a81-9962-e193c6b4a1a1",
                    "tableKey": "employee_details",
                    "connId": "sohod",
                    "duplicateIndex": 0,
                    "columnKey": "employee_name",
                    "_columnId": "5d670be7-b10f-4a81-9962-e193c6b4a1a1",
                    "uuid": "qopj-89fz-2q43-vqxl-zx"
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
                    "originalId": "36112396-65be-4492-9427-760912d2b808",
                    "tableKey": "employee_details",
                    "connId": "sohod",
                    "duplicateIndex": 0,
                    "columnKey": "age",
                    "_columnId": "36112396-65be-4492-9427-760912d2b808",
                    "uuid": "fdg8-5ko0-umcf-a7gl-ft"
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
                    "originalId": "1e90d836-d40e-4447-937c-d2bf8d4f4a02",
                    "tableKey": "employee_details",
                    "connId": "sohod",
                    "duplicateIndex": 0,
                    "columnKey": "address",
                    "_columnId": "1e90d836-d40e-4447-937c-d2bf8d4f4a02",
                    "uuid": "5v6t-wxi1-dqf1-xkic-an"
                }
            },
            "columnsFetched": true
        },
        "tasks": {
            "id": "73210e5e809f316b5b727202caffbf2f",
            "name": "tasks",
            "data": {
                "id": "1002",
                "type": "dynamicDataSource"
            },
            "keyPath": "0krv-iw11-nwwx-d903-ga/f5tw-vauy-wgvt-vcma-cq/6bpm-kwn2-7waq-yubu-al/ombn-z1uj-ir5a-a0at-d0",
            "key": "ombn-z1uj-ir5a-a0at-d0",
            "alias": "tasks",
            "uuid": "ombn-z1uj-ir5a-a0at-d0",
            "connId": "sohod",
            "dataSource": {
                "id": "1002",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "SampleTravelData",
                "schema": "",
                "connId": "sohod",
                "classifier": "db.workflow",
                "datasourceName": "MySql",
                "dsKeyPath": "0krv-iw11-nwwx-d903-ga/f5tw-vauy-wgvt-vcma-cq/6bpm-kwn2-7waq-yubu-al",
                "driverType": "Mysql",
                "database": "SampleTravelData"
            },
            "dataSourceName": "MySql",
            "category": "table",
            "nameWithConnId": "tasks_sohod",
            "database": "SampleTravelData",
            "catalog": "SampleTravelData",
            "selected": true,
            "keyName": "tasks",
            "columns": {
                "id": {
                    "alias": "id",
                    "fullyQualifiedColumn": "tasks.id",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    },
                    "category": "column",
                    "originalName": "id",
                    "originalId": "6a671f38-aaef-4366-bdda-900c8b54fc56",
                    "tableKey": "tasks",
                    "connId": "sohod",
                    "duplicateIndex": 0,
                    "columnKey": "id",
                    "_columnId": "6a671f38-aaef-4366-bdda-900c8b54fc56",
                    "uuid": "ckgw-30t8-qnqr-5i6q-7r"
                },
                "title": {
                    "alias": "title",
                    "fullyQualifiedColumn": "tasks.title",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "originalName": "title",
                    "originalId": "d37040d0-9175-459f-a384-43a96a1fc1cf",
                    "tableKey": "tasks",
                    "connId": "sohod",
                    "duplicateIndex": 0,
                    "columnKey": "title",
                    "_columnId": "d37040d0-9175-459f-a384-43a96a1fc1cf",
                    "uuid": "zaj9-9cmo-5cd2-glde-do"
                },
                "completed": {
                    "alias": "completed",
                    "fullyQualifiedColumn": "tasks.completed",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.Boolean": "boolean"
                    },
                    "category": "column",
                    "originalName": "completed",
                    "originalId": "b35dd944-cd9e-45bb-8320-409f7d665dc3",
                    "tableKey": "tasks",
                    "connId": "sohod",
                    "duplicateIndex": 0,
                    "columnKey": "completed",
                    "_columnId": "b35dd944-cd9e-45bb-8320-409f7d665dc3",
                    "uuid": "86du-s1xi-cok6-tgu3-lu"
                }
            },
            "columnsFetched": true
        }
    },
    "views": [],
    "activeView": false,
    "categories": {
        "1mpi-l9yu-18oi-gfc7-2p": {
            "ds": {
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
            "category": {
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
            }
        },
        "ftfo-8iqt-8ibw-o88w-c5": {
            "ds": {
                "permissionLevel": 5,
                "driver": "dynamicSwitch",
                "name": "DifferentConnections",
                "classifier": "efwd",
                "type": "sql.jdbc.groovy.managed",
                "data": {
                    "dir": "Groovy_Manged",
                    "driverName": null,
                    "type": "sql.jdbc.groovy.managed",
                    "id": 3,
                    "userName": null,
                    "password": null,
                    "condition": "import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue('${user}.name');\n        userName = userName.replaceAll(\"'\", \"\");\n        if (userName.equals(\"hiadmin\")) {\n          responseJson.put(\"globalId\", 1002);\n        }\n      \n        if (userName.equals(\"hiuser\")) {\n           responseJson.put(\"globalId\", 1);\n           responseJson.put(\"catalog\",\"\");\n\t\t  responseJson.put(\"schema\",\"HIUSER\");\n          responseJson.put(\"database\",\"HIUSER\")\n        }\n      \n        if (userName.equals(\"test\")) {\n          responseJson.put(\"globalId\", 1006);\n           responseJson.put(\"catalog\",\"SampleTravelData\");\n\t\t  responseJson.put(\"schema\",\"public\");\n          responseJson.put(\"database\",\"SampleTravelData.public\")\n        }\n      \n        responseJson.put(\"type\", \"global.jdbc\");\n      \n        //throw new RuntimeException(\"This is a test exception\" +responseJson);\n        return responseJson;\n      }",
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
        "0krv-iw11-nwwx-d903-ga": {
            "ds": {
                "data": {
                    "id": "1002",
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
        "34f6-t7g1-5otk-7dku-t6": {
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
        "wlqg-9dhe-mroo-tbck-i4": {
            "ds": {
                "data": {
                    "id": "1008",
                    "type": "dynamicDataSource"
                },
                "dataSourceProvider": "tomcat",
                "type": "dynamicDataSource",
                "permissionLevel": 5,
                "driver": "org.sqlite.JDBC",
                "name": "SalLite",
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
            "id": "1",
            "type": "dynamicDataSource",
            "baseType": "global.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "",
            "schema": "HIUSER",
            "connId": "a6kb7",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelDataDerby",
            "dsKeyPath": "1mpi-l9yu-18oi-gfc7-2p/wfct-05ig-dtk0-wkgm-bn/dk1y-1fas-qtwx-8611-2f",
            "driverType": "Derby",
            "database": "HIUSER"
        },
        {
            "id": "1002",
            "type": "dynamicDataSource",
            "baseType": "global.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "SampleTravelData",
            "schema": "",
            "connId": "sohod",
            "classifier": "db.workflow",
            "datasourceName": "MySql",
            "dsKeyPath": "0krv-iw11-nwwx-d903-ga/f5tw-vauy-wgvt-vcma-cq/6bpm-kwn2-7waq-yubu-al",
            "driverType": "Mysql",
            "database": "SampleTravelData"
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
        "uuid": "MysqlDery1.metadata"
    },
    "savedTableIds": [
        "4ac5d9f68b58bd7c0d179146e46795be",
        "9645c648a1c0dbeec1287aaf1e996db3",
        "73210e5e809f316b5b727202caffbf2f",
        "152371825108bf241d5e58d460282bf0"
    ],
    "savedColumnIds": [],
    "joins": [
        {
            "id": "e971fddf3d7b4e9ecbfaada0f65e6051",
            "type": "inner",
            "operator": "=",
            "left": {
                "table": "meeting_details",
                "column": "meeting_impact",
                "alias": "meeting_details.meeting_impact"
            },
            "right": {
                "table": "dimdate",
                "column": "fiscal_month_name",
                "alias": "dimdate.fiscal_month_name"
            },
            "index": 1,
            "action": "noChange",
            "uuid": "uujb-hb5m-41mp-f0fv-3a"
        },
        {
            "id": "915eb98b6c4207de4a384e880e42a5d7",
            "type": "inner",
            "operator": "=",
            "left": {
                "table": "employee_details",
                "column": "employee_name",
                "alias": "employee_details.employee_name"
            },
            "right": {
                "table": "tasks",
                "column": "title",
                "alias": "tasks.title"
            },
            "index": 2,
            "action": "noChange",
            "uuid": "vwhq-vv9i-dlxt-xizt-ry"
        },
        {
            "id": "96883c9278fece1a0e0e064cdbd21317",
            "type": "inner",
            "operator": "=",
            "left": {
                "table": "employee_details",
                "column": "employee_id",
                "alias": "employee_details.employee_id"
            },
            "right": {
                "table": "tasks",
                "column": "id",
                "alias": "tasks.id"
            },
            "index": 3,
            "action": "noChange",
            "uuid": "4typ-n1oa-v4pd-o9fw-qd"
        },
        {
            "id": "9cae88661ca9f504326fca081ecc7dde",
            "type": "inner",
            "operator": "=",
            "left": {
                "table": "employee_details",
                "column": "employee_id",
                "alias": "employee_details.employee_id"
            },
            "right": {
                "table": "tasks",
                "column": "title",
                "alias": "tasks.title"
            },
            "index": 4,
            "action": "noChange",
            "uuid": "iuqn-0ipk-q7jy-vjnn-oy"
        },
        {
            "id": "3e43899b016ada2b78831d64739a4ca8",
            "type": "inner",
            "operator": "=",
            "left": {
                "table": "employee_details",
                "column": "employee_name",
                "alias": "employee_details.employee_name"
            },
            "right": {
                "table": "employee_details",
                "column": "address",
                "alias": "employee_details.address"
            },
            "index": 5,
            "action": "noChange",
            "uuid": "db7s-cvw7-w0kw-9lw4-dl"
        }
    ],
    "mode": "edit",
    "allTablesKeys": [
        "5m1w-jq3q-8mf9-366z-cz",
        "lbgi-sqwc-5csw-d7u5-x8",
        "v8dn-a46o-asze-t5p8-gc",
        "cadj-2a8p-mm88-iysa-pl",
        "u73q-agil-nrhc-d3c3-2a",
        "p0ix-tmb4-77z2-w9iv-y9",
        "0zql-6scw-3ka8-cn3q-z2",
        "oy9e-kxty-f40l-mqob-ns",
        "gjbx-b8at-5fm2-cm2o-gi",
        "ombn-z1uj-ir5a-a0at-d0",
        "6yco-gxei-0xu3-6tk0-75"
    ],
    "selectedTableKeys": [
        "cadj-2a8p-mm88-iysa-pl",
        "lbgi-sqwc-5csw-d7u5-x8",
        "ombn-z1uj-ir5a-a0at-d0",
        "oy9e-kxty-f40l-mqob-ns"
    ],
    "metadataName": "Metadata_1",
    "activeDataSource": false,
    "metadataToEdit": false,
    "isSavingInProgress": false,
    "editViewsTempData": {},
    "inititalStateFromJest": false,
    "timeStamp": 1668433791864,
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
        "uniqueId": "MysqlDery1",
        "tables": {
            "dimdate": {
                "id": "4ac5d9f68b58bd7c0d179146e46795be",
                "alias": "dimdate",
                "columns": {
                    "dim_id": {
                        "alias": "dim_id",
                        "fullyQualifiedColumn": "dimdate.dim_id",
                        "columnId": "0192a847-522c-4a06-85af-93e31b45ebed",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "fiscal_year": {
                        "alias": "fiscal_year",
                        "fullyQualifiedColumn": "dimdate.fiscal_year",
                        "columnId": "7c8e0aa0-d815-4241-8ad1-62330b9bce64",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.sql.Date": "date"
                        }
                    },
                    "modified_date": {
                        "alias": "modified_date",
                        "fullyQualifiedColumn": "dimdate.modified_date",
                        "columnId": "d32ef917-bb7d-4f44-835e-58608437d62e",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.sql.Timestamp": "dateTime"
                        }
                    },
                    "date_key": {
                        "alias": "date_key",
                        "fullyQualifiedColumn": "dimdate.date_key",
                        "columnId": "1e7bc352-b57a-4e91-a649-ecb407e3ef95",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "day_number": {
                        "alias": "day_number",
                        "fullyQualifiedColumn": "dimdate.day_number",
                        "columnId": "94924a35-6325-4f98-8c0a-20a62273436d",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "fiscal_month_name": {
                        "alias": "fiscal_month_name",
                        "fullyQualifiedColumn": "dimdate.fiscal_month_name",
                        "columnId": "6923b00b-0e40-4eb5-be6b-39869d3f016a",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "fiscal_month_label": {
                        "alias": "fiscal_month_label",
                        "fullyQualifiedColumn": "dimdate.fiscal_month_label",
                        "columnId": "65a02bc3-9097-4a38-b256-e7438c6a8384",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "created_date": {
                        "alias": "created_date",
                        "fullyQualifiedColumn": "dimdate.created_date",
                        "columnId": "1746b3f9-1480-4497-a3fe-d66bd24c576e",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "created_time": {
                        "alias": "created_time",
                        "fullyQualifiedColumn": "dimdate.created_time",
                        "columnId": "3bf1a483-417e-418c-abbd-51e6227bbb1c",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "rating": {
                        "alias": "rating",
                        "fullyQualifiedColumn": "dimdate.rating",
                        "columnId": "83dd07ca-7853-435e-b888-13c09e379ef4",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    }
                },
                "name": "dimdate"
            },
            "meeting_details": {
                "id": "9645c648a1c0dbeec1287aaf1e996db3",
                "alias": "meeting_details",
                "columns": {
                    "meeting_id": {
                        "alias": "meeting_id",
                        "fullyQualifiedColumn": "meeting_details.meeting_id",
                        "columnId": "58d5c5b6-d3cb-4ee9-b95d-1cdcfbe8e038",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "meeting_date": {
                        "alias": "meeting_date",
                        "fullyQualifiedColumn": "meeting_details.meeting_date",
                        "columnId": "44422989-997a-40bd-9e87-17f5221b1cb4",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.sql.Timestamp": "dateTime"
                        }
                    },
                    "meeting_by": {
                        "alias": "meeting_by",
                        "fullyQualifiedColumn": "meeting_details.meeting_by",
                        "columnId": "7eba1e91-83d1-4839-ba2d-7a1beca23778",
                        "defaultFunction": "db.generic.aggregate.sum",
                        "type": {
                            "java.lang.Integer": "numeric"
                        }
                    },
                    "client_name": {
                        "alias": "client_name",
                        "fullyQualifiedColumn": "meeting_details.client_name",
                        "columnId": "53b1a600-100b-4b9b-a5e2-a817dd5552e8",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "meeting_purpose": {
                        "alias": "meeting_purpose",
                        "fullyQualifiedColumn": "meeting_details.meeting_purpose",
                        "columnId": "d85a2e91-fe18-4e44-a9ec-abdb9fe2a3e8",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "meeting_impact": {
                        "alias": "meeting_impact",
                        "fullyQualifiedColumn": "meeting_details.meeting_impact",
                        "columnId": "20b3237a-5d6d-473b-aeb1-bd4652d4b8c4",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "meet_cancellation_status": {
                        "alias": "meet_cancellation_status",
                        "fullyQualifiedColumn": "meeting_details.meet_cancellation_status",
                        "columnId": "9bedac46-f6ae-420d-a142-2912c455aa75",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        }
                    },
                    "cancellation_reason": {
                        "alias": "cancellation_reason",
                        "fullyQualifiedColumn": "meeting_details.cancellation_reason",
                        "columnId": "68d48a76-7f82-4c9c-8e5c-2b16b6b127e5",
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
                "meeting_details",
                "dimdate"
            ]
        ],
        "joins": [
            {
                "id": "e971fddf3d7b4e9ecbfaada0f65e6051",
                "type": "inner",
                "operator": "=",
                "left": {
                    "table": "meeting_details",
                    "column": "meeting_impact",
                    "alias": "meeting_details.meeting_impact"
                },
                "right": {
                    "table": "dimdate",
                    "column": "fiscal_month_name",
                    "alias": "dimdate.fiscal_month_name"
                }
            }
        ],
        "connections": [
            {
                "classifier": "db.generic",
                "name": "SampleTravelData",
                "dataSource": {
                    "sync": false,
                    "id": "1002",
                    "catSchemaPredicted": false,
                    "catalog": "SampleTravelData",
                    "schema": "",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc"
                },
                "uniqueId": "MysqlDery1",
                "tables": {
                    "tasks": {
                        "id": "73210e5e809f316b5b727202caffbf2f",
                        "alias": "tasks",
                        "columns": {
                            "id": {
                                "alias": "id",
                                "fullyQualifiedColumn": "tasks.id",
                                "columnId": "6a671f38-aaef-4366-bdda-900c8b54fc56",
                                "defaultFunction": "db.generic.aggregate.sum",
                                "type": {
                                    "java.lang.Integer": "numeric"
                                }
                            },
                            "title": {
                                "alias": "title",
                                "fullyQualifiedColumn": "tasks.title",
                                "columnId": "d37040d0-9175-459f-a384-43a96a1fc1cf",
                                "defaultFunction": "db.generic.groupBy.group",
                                "type": {
                                    "java.lang.String": "text"
                                }
                            },
                            "completed": {
                                "alias": "completed",
                                "fullyQualifiedColumn": "tasks.completed",
                                "columnId": "b35dd944-cd9e-45bb-8320-409f7d665dc3",
                                "defaultFunction": "db.generic.groupBy.group",
                                "type": {
                                    "java.lang.Boolean": "boolean"
                                }
                            }
                        },
                        "name": "tasks"
                    },
                    "employee_details": {
                        "id": "152371825108bf241d5e58d460282bf0",
                        "alias": "employee_details",
                        "columns": {
                            "employee_id": {
                                "alias": "employee_id",
                                "fullyQualifiedColumn": "employee_details.employee_id",
                                "columnId": "c92ce2b3-a3f2-4265-a79b-a92c0cc25ffa",
                                "defaultFunction": "db.generic.aggregate.sum",
                                "type": {
                                    "java.lang.Integer": "numeric"
                                }
                            },
                            "employee_name": {
                                "alias": "employee_name",
                                "fullyQualifiedColumn": "employee_details.employee_name",
                                "columnId": "5d670be7-b10f-4a81-9962-e193c6b4a1a1",
                                "defaultFunction": "db.generic.groupBy.group",
                                "type": {
                                    "java.lang.String": "text"
                                }
                            },
                            "age": {
                                "alias": "age",
                                "fullyQualifiedColumn": "employee_details.age",
                                "columnId": "36112396-65be-4492-9427-760912d2b808",
                                "defaultFunction": "db.generic.aggregate.sum",
                                "type": {
                                    "java.lang.Integer": "numeric"
                                }
                            },
                            "address": {
                                "alias": "address",
                                "fullyQualifiedColumn": "employee_details.address",
                                "columnId": "1e90d836-d40e-4447-937c-d2bf8d4f4a02",
                                "defaultFunction": "db.generic.groupBy.group",
                                "type": {
                                    "java.lang.String": "text"
                                }
                            }
                        },
                        "name": "employee_details"
                    }
                },
                "sets": [
                    [
                        "employee_details",
                        "tasks"
                    ]
                ],
                "joins": [
                    {
                        "id": "915eb98b6c4207de4a384e880e42a5d7",
                        "type": "inner",
                        "operator": "=",
                        "left": {
                            "table": "employee_details",
                            "column": "employee_name",
                            "alias": "employee_details.employee_name"
                        },
                        "right": {
                            "table": "tasks",
                            "column": "title",
                            "alias": "tasks.title"
                        }
                    },
                    {
                        "id": "96883c9278fece1a0e0e064cdbd21317",
                        "type": "inner",
                        "operator": "=",
                        "left": {
                            "table": "employee_details",
                            "column": "employee_id",
                            "alias": "employee_details.employee_id"
                        },
                        "right": {
                            "table": "tasks",
                            "column": "id",
                            "alias": "tasks.id"
                        }
                    },
                    {
                        "id": "9cae88661ca9f504326fca081ecc7dde",
                        "type": "inner",
                        "operator": "=",
                        "left": {
                            "table": "employee_details",
                            "column": "employee_id",
                            "alias": "employee_details.employee_id"
                        },
                        "right": {
                            "table": "tasks",
                            "column": "title",
                            "alias": "tasks.title"
                        }
                    },
                    {
                        "id": "3e43899b016ada2b78831d64739a4ca8",
                        "type": "inner",
                        "operator": "=",
                        "left": {
                            "table": "employee_details",
                            "column": "employee_name",
                            "alias": "employee_details.employee_name"
                        },
                        "right": {
                            "table": "employee_details",
                            "column": "address",
                            "alias": "employee_details.address"
                        }
                    }
                ],
                "connectionDatabaseId": "e31a96c0-c598-457c-9d77-12ed5df36672"
            }
        ],
        "metadataName": "Metadata_1",
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
    "tablesMergeType": false,
    "activeDsInfoId": "a6kb7",
    "editMdFromHomeInfo": {
        "dir": "Gagan/MysqlDery1.metadata",
        "file": "MysqlDery1.metadata"
    }
}
