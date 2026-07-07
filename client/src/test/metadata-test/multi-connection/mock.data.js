export const metadataMockData = {
    getStaticDataSourceList() {
        return {
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
                        "name": "Teradata",
                        "categoryType": "supported",
                        "categoryName": "Supported"
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
        }
    },
    getListDSClassifierAll() {
        return {
            "dataSources": [
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
                }
            ]
        }
    },
    getGetMetadata_1() {
        return {
            "status": 1,
            "response": {
                "classifier": "db.generic",
                "name": "HIUSER",
                "dataSource": {
                    "sync": false,
                    "id": "1000",
                    "catSchemaPredicted": false,
                    "catalog": "",
                    "schema": "HIUSER",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc"
                },
                "uniqueId": "Metadata_1",
                "tables": {
                    "CACHE": {
                        "id": "9d0d7c2d929cc2f20de2c45f70b9640f",
                        "alias": "CACHE",
                        "columns": {},
                        "name": "CACHE"
                    }
                },
                "sets": [
                    [
                        "CACHE"
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
                                        "columnId": "26580d9f-f17e-4f69-9b95-2005885b4e47",
                                        "defaultFunction": "db.generic.aggregate.sum",
                                        "type": {
                                            "java.lang.Integer": "numeric"
                                        }
                                    },
                                    "fiscal_year": {
                                        "alias": "fiscal_year",
                                        "fullyQualifiedColumn": "dimdate.fiscal_year",
                                        "columnId": "264ce539-9742-40c6-9fa9-877ec2376075",
                                        "defaultFunction": "db.generic.groupBy.group",
                                        "type": {
                                            "java.sql.Date": "date"
                                        }
                                    },
                                    "modified_date": {
                                        "alias": "modified_date",
                                        "fullyQualifiedColumn": "dimdate.modified_date",
                                        "columnId": "a9eed807-40e0-4b8a-a6df-cb73fa4ac89f",
                                        "defaultFunction": "db.generic.groupBy.group",
                                        "type": {
                                            "java.sql.Timestamp": "dateTime"
                                        }
                                    },
                                    "date_key": {
                                        "alias": "date_key",
                                        "fullyQualifiedColumn": "dimdate.date_key",
                                        "columnId": "4e530387-a8ac-43a0-b1c2-8ad5273e7076",
                                        "defaultFunction": "db.generic.groupBy.group",
                                        "type": {
                                            "java.lang.String": "text"
                                        }
                                    },
                                    "day_number": {
                                        "alias": "day_number",
                                        "fullyQualifiedColumn": "dimdate.day_number",
                                        "columnId": "6f3c5985-1679-429d-b3cd-58299695c563",
                                        "defaultFunction": "db.generic.groupBy.group",
                                        "type": {
                                            "java.lang.String": "text"
                                        }
                                    },
                                    "fiscal_month_name": {
                                        "alias": "fiscal_month_name",
                                        "fullyQualifiedColumn": "dimdate.fiscal_month_name",
                                        "columnId": "446248b2-fac5-4127-b9d1-a0537da5f884",
                                        "defaultFunction": "db.generic.groupBy.group",
                                        "type": {
                                            "java.lang.String": "text"
                                        }
                                    },
                                    "fiscal_month_label": {
                                        "alias": "fiscal_month_label",
                                        "fullyQualifiedColumn": "dimdate.fiscal_month_label",
                                        "columnId": "30f5a138-bb48-43a7-bcaf-61b4375a2b66",
                                        "defaultFunction": "db.generic.groupBy.group",
                                        "type": {
                                            "java.lang.String": "text"
                                        }
                                    },
                                    "created_date": {
                                        "alias": "created_date",
                                        "fullyQualifiedColumn": "dimdate.created_date",
                                        "columnId": "4bb4acf4-d846-46eb-a6fd-e4ad29c8f7d3",
                                        "defaultFunction": "db.generic.groupBy.group",
                                        "type": {
                                            "java.lang.String": "text"
                                        }
                                    },
                                    "created_time": {
                                        "alias": "created_time",
                                        "fullyQualifiedColumn": "dimdate.created_time",
                                        "columnId": "ea5c1769-1d26-4715-9cda-7df52625ac5e",
                                        "defaultFunction": "db.generic.groupBy.group",
                                        "type": {
                                            "java.lang.String": "text"
                                        }
                                    },
                                    "rating": {
                                        "alias": "rating",
                                        "fullyQualifiedColumn": "dimdate.rating",
                                        "columnId": "c1a7f367-4c1e-4241-9c50-3640c0e3fdfc",
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
                        "joins": []
                    }
                ],
                "metadataName": "Metadata_1",
                "metadataDir": "Gagan"
            }
        }
    },
    getMetadataWorkflow_1() {
        return {
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
        }
    },
    getMetadataWorkflow_1000() {
        return {
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
        }
    },
    getMetadataWorkflow_1_schema_HIUSER() {
        return {
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
    },
    getMetadataWorkflow_1000_schema_HIUSER() {
        return {
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
                                            "id": "ed031d3decbf73c096c71a81e8e828b1",
                                            "name": "H_USERS"
                                        },
                                        {
                                            "id": "34db8b39094aea068b0a428e53c0b73c",
                                            "name": "DS_GLOBAL_CONNECTION_SECURITY"
                                        },
                                        {
                                            "id": "df58c7524e3a1381aefe0ef8e593b041",
                                            "name": "HI_RESOURCE_DB"
                                        },
                                        {
                                            "id": "993bf4f98653c70260360ac9e2c02c34",
                                            "name": "DS_TYPE_TOMCAT"
                                        },
                                        {
                                            "id": "4d6f22ec264b11ca74036415f03d3f2b",
                                            "name": "HI_RESOURCE_EFWVF_CUSTOM"
                                        },
                                        {
                                            "id": "48ed7946d1b134a83f39fe7605b6dd3b",
                                            "name": "HI_METADATA_DATABASE"
                                        },
                                        {
                                            "id": "5e446c8e1c45cc1757fc6b1306dad411",
                                            "name": "EFWD_CONNECTIONS_SQL_JDBC"
                                        },
                                        {
                                            "id": "49b47ca705fcfb7d089e113ccea02350",
                                            "name": "HI_RESOURCE_HREPORT"
                                        },
                                        {
                                            "id": "b6a83330e8d091fcdf9ed634ec991cdc",
                                            "name": "HI_RESOURCE_EFWVF_STEP"
                                        },
                                        {
                                            "id": "1e8e4b847246e7f4cda605f8ea4f57fa",
                                            "name": "HI_EFWD_DATAMAP_PARAMATERS"
                                        },
                                        {
                                            "id": "69c4cb6464e52cfd1063151a0eb02527",
                                            "name": "HI_RESOURCE_REPORT"
                                        },
                                        {
                                            "id": "c18d07223bb9a647c044630c607fd664",
                                            "name": "HI_RESOURCE_HWF_HI_HWF_INPUT"
                                        },
                                        {
                                            "id": "fc8d7fa7a27da8b1f551a9323ad6f940",
                                            "name": "DS_TYPE_NOSQL"
                                        },
                                        {
                                            "id": "ca9a5e0eab44dee9698553f8e0c31d63",
                                            "name": "HI_CUBE_MEASURE"
                                        },
                                        {
                                            "id": "f11bb8dbb44d200595a6b7ce2ef79d6a",
                                            "name": "DS_TYPE_JNDI"
                                        },
                                        {
                                            "id": "33795dfbe44455b917e1710fa6988e1b",
                                            "name": "SCHEDULES"
                                        },
                                        {
                                            "id": "d5b30ffedc712f0b0541e4d5f499e9d7",
                                            "name": "HI_RESOURCE_SECURITY"
                                        },
                                        {
                                            "id": "d25afe50d3bfcbb98d52ef7fc7522fca",
                                            "name": "CACHE_DATASOURCE"
                                        },
                                        {
                                            "id": "5e5d58899bf301a3b03412577f8cef66",
                                            "name": "HI_RESOURCE_EFWVF_GAUGE"
                                        },
                                        {
                                            "id": "398c79edafaaf5b5cb4d81eb957c229e",
                                            "name": "HI_HWF_INPUT"
                                        },
                                        {
                                            "id": "9d0d7c2d929cc2f20de2c45f70b9640f",
                                            "name": "CACHE"
                                        },
                                        {
                                            "id": "23e98d34011d69c8ee1f6be2428bb767",
                                            "name": "HI_RESOURCE_EFWVF_AREA_SPLINE"
                                        },
                                        {
                                            "id": "c29f1be2b7e8675c29deaf4be21dacc1",
                                            "name": "HI_EFWD_CONNECTION_MANAGED"
                                        },
                                        {
                                            "id": "1d2d90855c4c18ecd293e182291ca1cc",
                                            "name": "RESOURCE_TYPE"
                                        },
                                        {
                                            "id": "fc1466b428af255aeeccb3462fed1262",
                                            "name": "HI_RESOURCE_EFWVF_DONUT"
                                        },
                                        {
                                            "id": "1fe80f594c7b10b9c8e6cd6f7402a34d",
                                            "name": "HI_RESOURCE_EFWVF_PIE"
                                        },
                                        {
                                            "id": "1a0bc59e34176ea777c60aea5363c4ae",
                                            "name": "HI_RESOURCE_EFWVF"
                                        },
                                        {
                                            "id": "06c3800cc9f2b29570ce76aa1fdef42b",
                                            "name": "HI_RESOURCE_EFWVF_BAR"
                                        },
                                        {
                                            "id": "11b63b2c1bbbe561ca3828a945c1edd4",
                                            "name": "HI_RESOURCE_EFWVF_SCATTER"
                                        },
                                        {
                                            "id": "df6bb0a57c55a41ef5aef39fd4f0468f",
                                            "name": "CACHE_REPORT"
                                        },
                                        {
                                            "id": "3d7a433cc4eedf39afde09d35bf5c65b",
                                            "name": "HI_CUBE_HIERARCY_LEVEL"
                                        },
                                        {
                                            "id": "98e5962961801fb716d922e6d149bafa",
                                            "name": "HI_URL_MAPPING"
                                        },
                                        {
                                            "id": "29b46a5d646be06ff4c2fb5eb793b993",
                                            "name": "CUBE_METADATA"
                                        },
                                        {
                                            "id": "d5eefccc22ee7922fbb9e311e01fa1b1",
                                            "name": "HI_METADATA_SECURITY"
                                        },
                                        {
                                            "id": "f4194b23416b6a53c23aee7ecca2c265",
                                            "name": "HI_RESOURCE_HWF"
                                        },
                                        {
                                            "id": "b71ad51bc9cb723e61141e2a59830f10",
                                            "name": "HI_RESOURCE_EFWVF_AREA_STEP"
                                        },
                                        {
                                            "id": "d6321694b670ab02dd4c64d1ca18520b",
                                            "name": "JOB_PARAMETERS"
                                        },
                                        {
                                            "id": "cbdfe22729c33a8dfdcacd9750a1c561",
                                            "name": "HI_RESOURCE_INSTANT"
                                        },
                                        {
                                            "id": "cf0e3d0d96e42fb72845276ce9912c31",
                                            "name": "HWF_EXECUTION_OUTPUT"
                                        },
                                        {
                                            "id": "f7bb76df0276feb3e6a0867492e34e8d",
                                            "name": "HI_EFWD_DATAMAP"
                                        },
                                        {
                                            "id": "c92303dc9aecdc0c6608910c2c5fe6dc",
                                            "name": "HI_RESOURCE_EFWD"
                                        },
                                        {
                                            "id": "c9bfccc749e2cf03502f50d171912f2b",
                                            "name": "HI_RESOURCE_EFWVF_TABLE"
                                        },
                                        {
                                            "id": "c7f7fd880607c10a209f19118964182f",
                                            "name": "HWF_EXECUTION_INPUT"
                                        },
                                        {
                                            "id": "ebde9a380f31193cec876159dad6174e",
                                            "name": "DS_GLOBAL_CONNECTIONS"
                                        },
                                        {
                                            "id": "3b6fd39cb77afaec841158eb2298356d",
                                            "name": "HI_RESOURCE_EFWDD"
                                        },
                                        {
                                            "id": "b3d98c04b57eff05f8b9641c64b1b2d7",
                                            "name": "HI_RESOURCE_EFWVF_LINE"
                                        },
                                        {
                                            "id": "d7e205240c176e07d63f0a62b6194e81",
                                            "name": "HWF_EXECUTION_GROOVY"
                                        },
                                        {
                                            "id": "d08a989ed6d87254cd2f830b01104019",
                                            "name": "HI_RESOURCE_CUBE"
                                        },
                                        {
                                            "id": "4fe4de3afbd0ae86425d6535f8fdd40a",
                                            "name": "HI_METADATA_CONNECTION_GLOBAL"
                                        },
                                        {
                                            "id": "71ecafafb0670765849e49b7c165b047",
                                            "name": "HI_METADATA_CONNECTION_EFWD"
                                        },
                                        {
                                            "id": "ca58cba49eecfcbf99b9e2ef259963f3",
                                            "name": "HI_METADATA_TABLES"
                                        },
                                        {
                                            "id": "2e28381c3a914671fff0c6d6c387ce9b",
                                            "name": "HWF_EXECUTION_CONDITIONAL"
                                        },
                                        {
                                            "id": "1856db7fa429ecc239b67dd8dfdd4342",
                                            "name": "HI_RESOURCE_METADATA"
                                        },
                                        {
                                            "id": "056637a8977d59159281c308ba453db2",
                                            "name": "ORGANIZATION"
                                        },
                                        {
                                            "id": "cd3334547e00cb95b7d12ac3f3a844b5",
                                            "name": "ROLE"
                                        },
                                        {
                                            "id": "33f517a2af46747d437891e0847b538e",
                                            "name": "PROFILE"
                                        },
                                        {
                                            "id": "6eb242469672abbcb8362e59e1cf303c",
                                            "name": "CUBE_AUDIT"
                                        },
                                        {
                                            "id": "ade2284af3b1bb67a52b84d419e88bc7",
                                            "name": "HI_HWF_OUTPUT"
                                        },
                                        {
                                            "id": "92dbaa85d641b6e6a0b56e353f9f17ed",
                                            "name": "HI_RESOURCE_EFWVF_AREA"
                                        },
                                        {
                                            "id": "049bb19942e00cb672710e56cbbd322b",
                                            "name": "HI_RESOURCE_HCR"
                                        },
                                        {
                                            "id": "f03c8be87d50f9986799efbd8c2d5104",
                                            "name": "GENERIC_CACHE"
                                        },
                                        {
                                            "id": "adc7403efe9c9056656888a2cef6b79c",
                                            "name": "HI_METADATA_VIEWS"
                                        },
                                        {
                                            "id": "31b06a9bd64ad160300709dd6ede0dcf",
                                            "name": "HI_RESOURCE_EFW_CONTENT"
                                        },
                                        {
                                            "id": "94afa19201797a3b4badb56a9d6fb044",
                                            "name": "HI_METADATA_CONNECTIONS"
                                        },
                                        {
                                            "id": "0e1a1b77628f3af1430d5dc57f40079d",
                                            "name": "HI_HWF_EXECUTION_HWF_EXECUTION_OUTPUT"
                                        },
                                        {
                                            "id": "719020cb0397cae0f6d64fae9f9065a6",
                                            "name": "HI_METADATA_RELATIONSHIPS"
                                        },
                                        {
                                            "id": "a22e79a28550f369384afc5aa06c2a03",
                                            "name": "HI_RESOURCE_EFWCE"
                                        },
                                        {
                                            "id": "730674f20800ab4010d88c86dc02e396",
                                            "name": "HI_RESOURCE_EFWSR"
                                        },
                                        {
                                            "id": "810a31c0eb36c269065e058b3e36ba3e",
                                            "name": "HI_RESOURCE_FOLDER"
                                        },
                                        {
                                            "id": "a82ab0c94e85837d61d161bee22d738c",
                                            "name": "HI_METADATA_COLUMNS"
                                        },
                                        {
                                            "id": "e64c8ef25b8873df5eec9513ed905fa2",
                                            "name": "HI_RESOURCE_RESULT"
                                        },
                                        {
                                            "id": "85fcf09905f7dfabdb09fb7c3d515879",
                                            "name": "HI_CUBE_DIMENSION"
                                        },
                                        {
                                            "id": "dde651222806b32176f0f10d087c7282",
                                            "name": "HI_EFWD_CONNECTION_SECURITY"
                                        },
                                        {
                                            "id": "01339503958a15542555320019c45fb6",
                                            "name": "HI_RESOURCE_EFWVF_SPLINE"
                                        },
                                        {
                                            "id": "183c7acc866696dae147a4e518ee2986",
                                            "name": "EFWD_CONNECTIONS_GROOVY"
                                        },
                                        {
                                            "id": "704195eafdb114e3c72a75e95fbb5a05",
                                            "name": "HI_RESOURCE_EFWVF_CROSS_TAB"
                                        },
                                        {
                                            "id": "a322a6868a47de0c38f47098350c9621",
                                            "name": "DS_TYPE_HIKARI"
                                        },
                                        {
                                            "id": "f418014699090c608d43208ae3736bae",
                                            "name": "HI_HWF_EXECUTION_HWF_EXECUTION_INPUT"
                                        },
                                        {
                                            "id": "2e947de5115d38d6820a8491f2252923",
                                            "name": "DS_TYPE_ADHOC_JDBC"
                                        },
                                        {
                                            "id": "aad898bfd1f0d7d458000937111017c9",
                                            "name": "CUBE_PROCESS"
                                        },
                                        {
                                            "id": "8b93a18025d742c5cc747bbb40960fbb",
                                            "name": "HI_RESOURCE_HWF_HI_HWF_OUTPUT"
                                        },
                                        {
                                            "id": "80abd8b442911836f2ea474718c8c705",
                                            "name": "HI_CUBE_DIMENSION_HIERARCHY"
                                        },
                                        {
                                            "id": "2a15deea8929136e4983cabee680162d",
                                            "name": "HI_EFWD_CONNECTION"
                                        },
                                        {
                                            "id": "3e284d95730d70d64ba9c1726290d272",
                                            "name": "HI_RESOURCE"
                                        },
                                        {
                                            "id": "647595180ef44ce03d5b3fa79a15164c",
                                            "name": "USER_ROLE"
                                        },
                                        {
                                            "id": "962537e5a0c506fd4f5a9b100b338a88",
                                            "name": "HI_RESOURCE_EFW"
                                        },
                                        {
                                            "id": "0303d6a41255afb53c00c9595c554bd7",
                                            "name": "HI_RESOURCE_HWF_HI_HWF_EXECUTION"
                                        },
                                        {
                                            "id": "96c0a4c461ce5d0d5c0517db87c8cfd7",
                                            "name": "CACHE_FILE_BROWSER"
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
    },
    getGetMetadata_derby_pg(){
        return {
            "status": 1,
            "response": {
              "classifier": "db.generic",
              "name": "booking1662345944710kenxhxumioueuzfj.public",
              "dataSource": {
                "sync": false,
                "id": "1001",
                "catSchemaPredicted": false,
                "catalog": "booking1662345944710kenxhxumioueuzfj",
                "schema": "public",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc"
              },
              "uniqueId": "Metadata_derby_pg",
              "tables": {
                "appartments": {
                  "id": "ce367e8d42ec1b746afdd73c2a07e7b3",
                  "alias": "appartments",
                  "columns": {},
                  "name": "appartments"
                },
                "company": {
                  "id": "93620a4e4bdf955d0e9b132ee0ec9ae3",
                  "alias": "company",
                  "columns": {},
                  "name": "company"
                }
              },
              "sets": [
                [
                  "appartments"
                ],
                [
                  "company"
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
                    "catSchemaPredicted": false,
                    "catalog": "",
                    "schema": "HIUSER",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc"
                  },
                  "uniqueId": "Metadata_derby_pg",
                  "tables": {
                    "dimdate": {
                      "id": "4ac5d9f68b58bd7c0d179146e46795be",
                      "alias": "dimdate",
                      "columns": {
                        "dim_id": {
                          "alias": "dim_id",
                          "fullyQualifiedColumn": "dimdate.dim_id",
                          "columnId": "26580d9f-f17e-4f69-9b95-2005885b4e47",
                          "defaultFunction": "db.generic.aggregate.sum",
                          "type": {
                            "java.lang.Integer": "numeric"
                          }
                        },
                        "fiscal_year": {
                          "alias": "fiscal_year",
                          "fullyQualifiedColumn": "dimdate.fiscal_year",
                          "columnId": "264ce539-9742-40c6-9fa9-877ec2376075",
                          "defaultFunction": "db.generic.groupBy.group",
                          "type": {
                            "java.sql.Date": "date"
                          }
                        },
                        "modified_date": {
                          "alias": "modified_date",
                          "fullyQualifiedColumn": "dimdate.modified_date",
                          "columnId": "a9eed807-40e0-4b8a-a6df-cb73fa4ac89f",
                          "defaultFunction": "db.generic.groupBy.group",
                          "type": {
                            "java.sql.Timestamp": "dateTime"
                          }
                        },
                        "date_key": {
                          "alias": "date_key",
                          "fullyQualifiedColumn": "dimdate.date_key",
                          "columnId": "4e530387-a8ac-43a0-b1c2-8ad5273e7076",
                          "defaultFunction": "db.generic.groupBy.group",
                          "type": {
                            "java.lang.String": "text"
                          }
                        },
                        "day_number": {
                          "alias": "day_number",
                          "fullyQualifiedColumn": "dimdate.day_number",
                          "columnId": "6f3c5985-1679-429d-b3cd-58299695c563",
                          "defaultFunction": "db.generic.groupBy.group",
                          "type": {
                            "java.lang.String": "text"
                          }
                        },
                        "fiscal_month_name": {
                          "alias": "fiscal_month_name",
                          "fullyQualifiedColumn": "dimdate.fiscal_month_name",
                          "columnId": "446248b2-fac5-4127-b9d1-a0537da5f884",
                          "defaultFunction": "db.generic.groupBy.group",
                          "type": {
                            "java.lang.String": "text"
                          }
                        },
                        "fiscal_month_label": {
                          "alias": "fiscal_month_label",
                          "fullyQualifiedColumn": "dimdate.fiscal_month_label",
                          "columnId": "30f5a138-bb48-43a7-bcaf-61b4375a2b66",
                          "defaultFunction": "db.generic.groupBy.group",
                          "type": {
                            "java.lang.String": "text"
                          }
                        },
                        "created_date": {
                          "alias": "created_date",
                          "fullyQualifiedColumn": "dimdate.created_date",
                          "columnId": "4bb4acf4-d846-46eb-a6fd-e4ad29c8f7d3",
                          "defaultFunction": "db.generic.groupBy.group",
                          "type": {
                            "java.lang.String": "text"
                          }
                        },
                        "created_time": {
                          "alias": "created_time",
                          "fullyQualifiedColumn": "dimdate.created_time",
                          "columnId": "ea5c1769-1d26-4715-9cda-7df52625ac5e",
                          "defaultFunction": "db.generic.groupBy.group",
                          "type": {
                            "java.lang.String": "text"
                          }
                        },
                        "rating": {
                          "alias": "rating",
                          "fullyQualifiedColumn": "dimdate.rating",
                          "columnId": "c1a7f367-4c1e-4241-9c50-3640c0e3fdfc",
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
                  "joins": []
                }
              ],
              "metadataName": "Metadata_derby_pg",
              "metadataDir": "Gagan"
            }
          }
    }

}