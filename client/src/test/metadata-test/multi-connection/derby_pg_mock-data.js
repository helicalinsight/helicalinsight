export const metadataMockData = {
    getStaticDSList: {
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
                    "available": "true",
                    "driver": "org.apache.spark.deploy.DeployMessages$RequestKillDriver"
                },
                {
                    "available": "true",
                    "driver": "org.apache.spark.deploy.DeployMessages$LaunchDriver"
                },
                {
                    "available": "true",
                    "driver": "org.apache.spark.deploy.DeployMessages$KillDriver"
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
                    "driver": "org.apache.spark.deploy.DeployMessages$KillDriver",
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
                    "driver": "org.apache.spark.deploy.DeployMessages$LaunchDriver",
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
    getGetListDS: {
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
            }
        ]
    },
    getMetadata_2_pg_derby: {
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
            "uniqueId": "Metadata_2_pg_derby",
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
                    "uniqueId": "Metadata_2_pg_derby",
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
            "metadataName": "Metadata_2 pg derby",
            "metadataDir": "Gagan"
        }
    },
    workflow_1001_fetchSchema_fetchCatalog: {
        "status": 1,
        "response": {
            "classifier": "db.workflow",
            "metadata": {
                "catalogs": [
                    {
                        "name": "booking1662345944710kenxhxumioueuzfj",
                        "schemas": [
                            {
                                "name": "pg_catalog"
                            },
                            {
                                "name": "information_schema"
                            },
                            {
                                "name": "public"
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
    workflow_1_fetchSchema_fetchCatalog: {
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
    workflow_1_HIUSER: {
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
    },
    workflow_1001_public_booking: {
        "status": 1,
        "response": {
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
                    "schema": "public"
                },
                "name": "booking1662345944710kenxhxumioueuzfj.public"
            }
        },
        "position": "0",
        "maxSize": "1",
        "totalPage": 1,
        "resultPage": 1
    }
}