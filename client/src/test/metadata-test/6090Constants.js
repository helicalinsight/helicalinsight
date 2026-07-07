export const bug6090Constants = {
    createMode: {
        withDuplicateTable: {
            "tables": {
                "dimdate": {
                    "id": "4ac5d9f68b58bd7c0d179146e46795be",
                    "name": "dimdate",
                    "alias": "dimdate",
                    "dataSource": {
                        "id": "1",
                        "type": "dynamicDataSource",
                        "baseType": "global.jdbc",
                        "catSchemaPredicted": false,
                        "sync": false,
                        "catalog": "",
                        "schema": "HIUSER",
                        "connId": "ixkl3",
                        "dbId": "ixkl3",
                        "classifier": "db.workflow",
                        "datasourceName": "SampleTravelDataDerby",
                        "dsKeyPath": "q9bb-0ojr-legx-llle-yu/q9xz-p9bb-1hfl-w0fn-o3/igs7-348i-v6hw-et9c-23",
                        "driverType": "Derby",
                        "database": "HIUSER"
                    },
                    "category": "table",
                    "connId": "ixkl3",
                    "keyPath": "q9bb-0ojr-legx-llle-yu/q9xz-p9bb-1hfl-w0fn-o3/igs7-348i-v6hw-et9c-23/5fm3-ckqj-9828-jn21-dz",
                    "uniqueKey": "4ac5d9f68b58bd7c0d179146e46795be_ixkl3",
                    "schema": "HIUSER",
                    "selected": true,
                    "keyName": "dimdate",
                    "columns": {
                        "dim_id": {
                            "alias": "dim_id",
                            "fullyQualifiedColumn": "dimdate.dim_id",
                            "id": "daac9be5-db88-4dd4-9ffb-31fadb1a7579",
                            "defaultFunction": "db.generic.aggregate.sum",
                            "type": {
                                "java.lang.Integer": "numeric"
                            },
                            "category": "column",
                            "uniqueKey": "daac9be5-db88-4dd4-9ffb-31fadb1a7579_ixkl3",
                            "name": "dim_id",
                            "tableKey": "dimdate",
                            "connId": "ixkl3",
                            "columnKey": "dim_id",
                            "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
                        },
                        "fiscal_year": {
                            "alias": "fiscal_year",
                            "fullyQualifiedColumn": "dimdate.fiscal_year",
                            "id": "390cd762-17d9-4648-a977-77ad87cd08fb",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.sql.Date": "date"
                            },
                            "category": "column",
                            "uniqueKey": "390cd762-17d9-4648-a977-77ad87cd08fb_ixkl3",
                            "name": "fiscal_year",
                            "tableKey": "dimdate",
                            "connId": "ixkl3",
                            "columnKey": "fiscal_year",
                            "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
                        },
                        "modified_date": {
                            "alias": "modified_date",
                            "fullyQualifiedColumn": "dimdate.modified_date",
                            "id": "d005b4e4-47f6-424c-8cce-edd7e0fa4909",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.sql.Timestamp": "dateTime"
                            },
                            "category": "column",
                            "uniqueKey": "d005b4e4-47f6-424c-8cce-edd7e0fa4909_ixkl3",
                            "name": "modified_date",
                            "tableKey": "dimdate",
                            "connId": "ixkl3",
                            "columnKey": "modified_date",
                            "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
                        },
                        "date_key": {
                            "alias": "date_key",
                            "fullyQualifiedColumn": "dimdate.date_key",
                            "id": "8ed84a3e-0c3d-49cc-b16a-4f5543d5e3a9",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            },
                            "category": "column",
                            "uniqueKey": "8ed84a3e-0c3d-49cc-b16a-4f5543d5e3a9_ixkl3",
                            "name": "date_key",
                            "tableKey": "dimdate",
                            "connId": "ixkl3",
                            "columnKey": "date_key",
                            "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
                        },
                        "day_number": {
                            "alias": "day_number",
                            "fullyQualifiedColumn": "dimdate.day_number",
                            "id": "06f934c8-c325-48f9-b187-f25c1b4d2360",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            },
                            "category": "column",
                            "uniqueKey": "06f934c8-c325-48f9-b187-f25c1b4d2360_ixkl3",
                            "name": "day_number",
                            "tableKey": "dimdate",
                            "connId": "ixkl3",
                            "columnKey": "day_number",
                            "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
                        },
                        "fiscal_month_name": {
                            "alias": "fiscal_month_name",
                            "fullyQualifiedColumn": "dimdate.fiscal_month_name",
                            "id": "cdf780f0-842a-4a9a-8476-7d492b73047e",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            },
                            "category": "column",
                            "uniqueKey": "cdf780f0-842a-4a9a-8476-7d492b73047e_ixkl3",
                            "name": "fiscal_month_name",
                            "tableKey": "dimdate",
                            "connId": "ixkl3",
                            "columnKey": "fiscal_month_name",
                            "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
                        },
                        "fiscal_month_label": {
                            "alias": "fiscal_month_label",
                            "fullyQualifiedColumn": "dimdate.fiscal_month_label",
                            "id": "e632a468-d5b7-441b-9674-67779af272bd",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            },
                            "category": "column",
                            "uniqueKey": "e632a468-d5b7-441b-9674-67779af272bd_ixkl3",
                            "name": "fiscal_month_label",
                            "tableKey": "dimdate",
                            "connId": "ixkl3",
                            "columnKey": "fiscal_month_label",
                            "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
                        },
                        "created_date": {
                            "alias": "created_date",
                            "fullyQualifiedColumn": "dimdate.created_date",
                            "id": "10df7a8f-25f0-44ce-833f-adcaeb1dfb93",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            },
                            "category": "column",
                            "uniqueKey": "10df7a8f-25f0-44ce-833f-adcaeb1dfb93_ixkl3",
                            "name": "created_date",
                            "tableKey": "dimdate",
                            "connId": "ixkl3",
                            "columnKey": "created_date",
                            "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
                        },
                        "created_time": {
                            "alias": "created_time",
                            "fullyQualifiedColumn": "dimdate.created_time",
                            "id": "7b4864f9-f285-4c0c-b77c-4107cba85a6e",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            },
                            "category": "column",
                            "uniqueKey": "7b4864f9-f285-4c0c-b77c-4107cba85a6e_ixkl3",
                            "name": "created_time",
                            "tableKey": "dimdate",
                            "connId": "ixkl3",
                            "columnKey": "created_time",
                            "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
                        },
                        "rating": {
                            "alias": "rating",
                            "fullyQualifiedColumn": "dimdate.rating",
                            "id": "0a56d7f2-69d4-4175-afe4-dd3d797d2988",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            },
                            "category": "column",
                            "uniqueKey": "0a56d7f2-69d4-4175-afe4-dd3d797d2988_ixkl3",
                            "name": "rating",
                            "tableKey": "dimdate",
                            "connId": "ixkl3",
                            "columnKey": "rating",
                            "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
                        }
                    },
                    "columnsFetched": true
                },
                "employee_details": {
                    "id": "4e1fd245f4d13b77be423a43f01d80b2",
                    "name": "employee_details",
                    "alias": "employee_details",
                    "dataSource": {
                        "id": "1",
                        "type": "dynamicDataSource",
                        "baseType": "global.jdbc",
                        "catSchemaPredicted": false,
                        "sync": false,
                        "catalog": "",
                        "schema": "HIUSER",
                        "connId": "ixkl3",
                        "dbId": "ixkl3",
                        "classifier": "db.workflow",
                        "datasourceName": "SampleTravelDataDerby",
                        "dsKeyPath": "q9bb-0ojr-legx-llle-yu/q9xz-p9bb-1hfl-w0fn-o3/igs7-348i-v6hw-et9c-23",
                        "driverType": "Derby",
                        "database": "HIUSER"
                    },
                    "category": "table",
                    "connId": "ixkl3",
                    "keyPath": "q9bb-0ojr-legx-llle-yu/q9xz-p9bb-1hfl-w0fn-o3/igs7-348i-v6hw-et9c-23/wwla-vyy3-i51y-e99u-ka",
                    "uniqueKey": "4e1fd245f4d13b77be423a43f01d80b2_ixkl3",
                    "schema": "HIUSER",
                    "selected": true,
                    "keyName": "employee_details"
                },
                "dimdate_1": {
                    "id": "t541-33o4-ll69-s5ba-wh",
                    "name": "dimdate_1",
                    "alias": "dimdate_1",
                    "dataSource": {
                        "id": "1",
                        "type": "dynamicDataSource",
                        "baseType": "global.jdbc",
                        "catSchemaPredicted": false,
                        "sync": false,
                        "catalog": "",
                        "schema": "HIUSER",
                        "connId": "ixkl3",
                        "dbId": "ixkl3",
                        "classifier": "db.workflow",
                        "datasourceName": "SampleTravelDataDerby",
                        "dsKeyPath": "q9bb-0ojr-legx-llle-yu/q9xz-p9bb-1hfl-w0fn-o3/igs7-348i-v6hw-et9c-23",
                        "driverType": "Derby",
                        "database": "HIUSER"
                    },
                    "category": "table",
                    "connId": "ixkl3",
                    "keyPath": "q9bb-0ojr-legx-llle-yu/q9xz-p9bb-1hfl-w0fn-o3/igs7-348i-v6hw-et9c-23/5fm3-ckqj-9828-jn21-dz",
                    "uniqueKey": "t541-33o4-ll69-s5ba-wh_ixkl3",
                    "schema": "HIUSER",
                    "selected": true,
                    "keyName": "dimdate_1",
                    "columns": {
                        "dim_id": {
                            "alias": "dim_id",
                            "fullyQualifiedColumn": "dimdate.dim_id",
                            "id": "9me3-r3y9-kot0-321e-90",
                            "defaultFunction": "db.generic.aggregate.sum",
                            "type": {
                                "java.lang.Integer": "numeric"
                            },
                            "category": "column",
                            "uniqueKey": "9me3-r3y9-kot0-321e-90_ixkl3",
                            "name": "dim_id",
                            "tableKey": "dimdate_1",
                            "connId": "ixkl3",
                            "columnKey": "dim_id",
                            "tableId": "t541-33o4-ll69-s5ba-wh",
                            "originalId": "daac9be5-db88-4dd4-9ffb-31fadb1a7579"
                        },
                        "fiscal_year": {
                            "alias": "fiscal_year",
                            "fullyQualifiedColumn": "dimdate.fiscal_year",
                            "id": "19yr-ntug-9sy2-faop-hg",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.sql.Date": "date"
                            },
                            "category": "column",
                            "uniqueKey": "19yr-ntug-9sy2-faop-hg_ixkl3",
                            "name": "fiscal_year",
                            "tableKey": "dimdate_1",
                            "connId": "ixkl3",
                            "columnKey": "fiscal_year",
                            "tableId": "t541-33o4-ll69-s5ba-wh",
                            "originalId": "390cd762-17d9-4648-a977-77ad87cd08fb"
                        },
                        "modified_date": {
                            "alias": "modified_date",
                            "fullyQualifiedColumn": "dimdate.modified_date",
                            "id": "n7w6-twbt-275c-9nqi-ik",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.sql.Timestamp": "dateTime"
                            },
                            "category": "column",
                            "uniqueKey": "n7w6-twbt-275c-9nqi-ik_ixkl3",
                            "name": "modified_date",
                            "tableKey": "dimdate_1",
                            "connId": "ixkl3",
                            "columnKey": "modified_date",
                            "tableId": "t541-33o4-ll69-s5ba-wh",
                            "originalId": "d005b4e4-47f6-424c-8cce-edd7e0fa4909"
                        },
                        "date_key": {
                            "alias": "date_key",
                            "fullyQualifiedColumn": "dimdate.date_key",
                            "id": "zz1m-0aal-z4uk-i431-vx",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            },
                            "category": "column",
                            "uniqueKey": "zz1m-0aal-z4uk-i431-vx_ixkl3",
                            "name": "date_key",
                            "tableKey": "dimdate_1",
                            "connId": "ixkl3",
                            "columnKey": "date_key",
                            "tableId": "t541-33o4-ll69-s5ba-wh",
                            "originalId": "8ed84a3e-0c3d-49cc-b16a-4f5543d5e3a9"
                        },
                        "day_number": {
                            "alias": "day_number",
                            "fullyQualifiedColumn": "dimdate.day_number",
                            "id": "h629-80nd-t7cq-hgj7-bu",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            },
                            "category": "column",
                            "uniqueKey": "h629-80nd-t7cq-hgj7-bu_ixkl3",
                            "name": "day_number",
                            "tableKey": "dimdate_1",
                            "connId": "ixkl3",
                            "columnKey": "day_number",
                            "tableId": "t541-33o4-ll69-s5ba-wh",
                            "originalId": "06f934c8-c325-48f9-b187-f25c1b4d2360"
                        },
                        "fiscal_month_name": {
                            "alias": "fiscal_month_name",
                            "fullyQualifiedColumn": "dimdate.fiscal_month_name",
                            "id": "iez2-nbeb-p1h9-0mu9-cm",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            },
                            "category": "column",
                            "uniqueKey": "iez2-nbeb-p1h9-0mu9-cm_ixkl3",
                            "name": "fiscal_month_name",
                            "tableKey": "dimdate_1",
                            "connId": "ixkl3",
                            "columnKey": "fiscal_month_name",
                            "tableId": "t541-33o4-ll69-s5ba-wh",
                            "originalId": "cdf780f0-842a-4a9a-8476-7d492b73047e"
                        },
                        "fiscal_month_label": {
                            "alias": "fiscal_month_label",
                            "fullyQualifiedColumn": "dimdate.fiscal_month_label",
                            "id": "am7t-uege-nq2h-y04l-5h",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            },
                            "category": "column",
                            "uniqueKey": "am7t-uege-nq2h-y04l-5h_ixkl3",
                            "name": "fiscal_month_label",
                            "tableKey": "dimdate_1",
                            "connId": "ixkl3",
                            "columnKey": "fiscal_month_label",
                            "tableId": "t541-33o4-ll69-s5ba-wh",
                            "originalId": "e632a468-d5b7-441b-9674-67779af272bd"
                        },
                        "created_date": {
                            "alias": "created_date",
                            "fullyQualifiedColumn": "dimdate.created_date",
                            "id": "lfjb-dupt-drk2-h7dz-vo",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            },
                            "category": "column",
                            "uniqueKey": "lfjb-dupt-drk2-h7dz-vo_ixkl3",
                            "name": "created_date",
                            "tableKey": "dimdate_1",
                            "connId": "ixkl3",
                            "columnKey": "created_date",
                            "tableId": "t541-33o4-ll69-s5ba-wh",
                            "originalId": "10df7a8f-25f0-44ce-833f-adcaeb1dfb93"
                        },
                        "created_time": {
                            "alias": "created_time",
                            "fullyQualifiedColumn": "dimdate.created_time",
                            "id": "xy4c-ytpm-ke8q-ln4c-68",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            },
                            "category": "column",
                            "uniqueKey": "xy4c-ytpm-ke8q-ln4c-68_ixkl3",
                            "name": "created_time",
                            "tableKey": "dimdate_1",
                            "connId": "ixkl3",
                            "columnKey": "created_time",
                            "tableId": "t541-33o4-ll69-s5ba-wh",
                            "originalId": "7b4864f9-f285-4c0c-b77c-4107cba85a6e"
                        },
                        "rating": {
                            "alias": "rating",
                            "fullyQualifiedColumn": "dimdate.rating",
                            "id": "javs-ou8j-wv83-xqh8-42",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            },
                            "category": "column",
                            "uniqueKey": "javs-ou8j-wv83-xqh8-42_ixkl3",
                            "name": "rating",
                            "tableKey": "dimdate_1",
                            "connId": "ixkl3",
                            "columnKey": "rating",
                            "tableId": "t541-33o4-ll69-s5ba-wh",
                            "originalId": "0a56d7f2-69d4-4175-afe4-dd3d797d2988"
                        }
                    },
                    "columnsFetched": true,
                    "originalName": "dimdate",
                    "originalId": "4ac5d9f68b58bd7c0d179146e46795be",
                    "duplicate": true
                }
            },
            "connection": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "ixkl3",
                "dbId": "ixkl3",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "q9bb-0ojr-legx-llle-yu/q9xz-p9bb-1hfl-w0fn-o3/igs7-348i-v6hw-et9c-23",
                "driverType": "Derby",
                "database": "HIUSER",
                "connectionDatabaseId": "789c27e8-3749-4878-9e99-c4c7e6bd336c",
                "databaseType": "Derby"
            },
            "duplicateTableList": [
                "t541-33o4-ll69-s5ba-wh"
            ],
            "savedTableIds": []
        },
        result: [
            {
                "alias": "dimdate_1",
                "id": "t541-33o4-ll69-s5ba-wh",
                "columns": [
                    {
                        "alias": "dim_id",
                        "connId": "ixkl3",
                        "originalId": "daac9be5-db88-4dd4-9ffb-31fadb1a7579",
                        "id": "9me3-r3y9-kot0-321e-90",
                        "name": "dim_id"
                    },
                    {
                        "alias": "fiscal_year",
                        "connId": "ixkl3",
                        "originalId": "390cd762-17d9-4648-a977-77ad87cd08fb",
                        "id": "19yr-ntug-9sy2-faop-hg",
                        "name": "fiscal_year"
                    },
                    {
                        "alias": "modified_date",
                        "connId": "ixkl3",
                        "originalId": "d005b4e4-47f6-424c-8cce-edd7e0fa4909",
                        "id": "n7w6-twbt-275c-9nqi-ik",
                        "name": "modified_date"
                    },
                    {
                        "alias": "date_key",
                        "connId": "ixkl3",
                        "originalId": "8ed84a3e-0c3d-49cc-b16a-4f5543d5e3a9",
                        "id": "zz1m-0aal-z4uk-i431-vx",
                        "name": "date_key"
                    },
                    {
                        "alias": "day_number",
                        "connId": "ixkl3",
                        "originalId": "06f934c8-c325-48f9-b187-f25c1b4d2360",
                        "id": "h629-80nd-t7cq-hgj7-bu",
                        "name": "day_number"
                    },
                    {
                        "alias": "fiscal_month_name",
                        "connId": "ixkl3",
                        "originalId": "cdf780f0-842a-4a9a-8476-7d492b73047e",
                        "id": "iez2-nbeb-p1h9-0mu9-cm",
                        "name": "fiscal_month_name"
                    },
                    {
                        "alias": "fiscal_month_label",
                        "connId": "ixkl3",
                        "originalId": "e632a468-d5b7-441b-9674-67779af272bd",
                        "id": "am7t-uege-nq2h-y04l-5h",
                        "name": "fiscal_month_label"
                    },
                    {
                        "alias": "created_date",
                        "connId": "ixkl3",
                        "originalId": "10df7a8f-25f0-44ce-833f-adcaeb1dfb93",
                        "id": "lfjb-dupt-drk2-h7dz-vo",
                        "name": "created_date"
                    },
                    {
                        "alias": "created_time",
                        "connId": "ixkl3",
                        "originalId": "7b4864f9-f285-4c0c-b77c-4107cba85a6e",
                        "id": "xy4c-ytpm-ke8q-ln4c-68",
                        "name": "created_time"
                    },
                    {
                        "alias": "rating",
                        "connId": "ixkl3",
                        "originalId": "0a56d7f2-69d4-4175-afe4-dd3d797d2988",
                        "id": "javs-ou8j-wv83-xqh8-42",
                        "name": "rating"
                    }
                ],
                "connId": "ixkl3",
                "originalName": "dimdate",
                "originalId": "4ac5d9f68b58bd7c0d179146e46795be",
                "name": "dimdate_1"
            }
        ]
    },
    ImediateSave: {
        withNewDuplicateTable: {
            "tables": {
                "dimdate": {
                    "id": "13826",
                    "alias": "dimdate",
                    "columns": {
                        "dim_id": {
                            "alias": "dim_id",
                            "fullyQualifiedColumn": "dimdate.dim_id",
                            "id": "40299",
                            "defaultFunction": "db.generic.aggregate.sum",
                            "type": {
                                "java.lang.Integer": "numeric"
                            },
                            "category": "column",
                            "columnKey": "dim_id",
                            "name": "dim_id",
                            "tableKey": "dimdate",
                            "connId": "12509",
                            "tableId": "13826",
                            "parentCategory": "table",
                            "uniqueKey": "40299"
                        },
                        "fiscal_year": {
                            "alias": "fiscal_year",
                            "fullyQualifiedColumn": "dimdate.fiscal_year",
                            "id": "40300",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.sql.Date": "date"
                            },
                            "category": "column",
                            "columnKey": "fiscal_year",
                            "name": "fiscal_year",
                            "tableKey": "dimdate",
                            "connId": "12509",
                            "tableId": "13826",
                            "parentCategory": "table",
                            "uniqueKey": "40300"
                        },
                        "modified_date": {
                            "alias": "modified_date",
                            "fullyQualifiedColumn": "dimdate.modified_date",
                            "id": "40301",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.sql.Timestamp": "dateTime"
                            },
                            "category": "column",
                            "columnKey": "modified_date",
                            "name": "modified_date",
                            "tableKey": "dimdate",
                            "connId": "12509",
                            "tableId": "13826",
                            "parentCategory": "table",
                            "uniqueKey": "40301"
                        },
                        "date_key": {
                            "alias": "date_key",
                            "fullyQualifiedColumn": "dimdate.date_key",
                            "id": "40302",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            },
                            "category": "column",
                            "columnKey": "date_key",
                            "name": "date_key",
                            "tableKey": "dimdate",
                            "connId": "12509",
                            "tableId": "13826",
                            "parentCategory": "table",
                            "uniqueKey": "40302"
                        },
                        "day_number": {
                            "alias": "day_number",
                            "fullyQualifiedColumn": "dimdate.day_number",
                            "id": "40303",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            },
                            "category": "column",
                            "columnKey": "day_number",
                            "name": "day_number",
                            "tableKey": "dimdate",
                            "connId": "12509",
                            "tableId": "13826",
                            "parentCategory": "table",
                            "uniqueKey": "40303"
                        },
                        "fiscal_month_name": {
                            "alias": "fiscal_month_name",
                            "fullyQualifiedColumn": "dimdate.fiscal_month_name",
                            "id": "40304",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            },
                            "category": "column",
                            "columnKey": "fiscal_month_name",
                            "name": "fiscal_month_name",
                            "tableKey": "dimdate",
                            "connId": "12509",
                            "tableId": "13826",
                            "parentCategory": "table",
                            "uniqueKey": "40304"
                        },
                        "fiscal_month_label": {
                            "alias": "fiscal_month_label",
                            "fullyQualifiedColumn": "dimdate.fiscal_month_label",
                            "id": "40305",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            },
                            "category": "column",
                            "columnKey": "fiscal_month_label",
                            "name": "fiscal_month_label",
                            "tableKey": "dimdate",
                            "connId": "12509",
                            "tableId": "13826",
                            "parentCategory": "table",
                            "uniqueKey": "40305"
                        },
                        "created_date": {
                            "alias": "created_date",
                            "fullyQualifiedColumn": "dimdate.created_date",
                            "id": "40306",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            },
                            "category": "column",
                            "columnKey": "created_date",
                            "name": "created_date",
                            "tableKey": "dimdate",
                            "connId": "12509",
                            "tableId": "13826",
                            "parentCategory": "table",
                            "uniqueKey": "40306"
                        },
                        "created_time": {
                            "alias": "created_time",
                            "fullyQualifiedColumn": "dimdate.created_time",
                            "id": "40307",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            },
                            "category": "column",
                            "columnKey": "created_time",
                            "name": "created_time",
                            "tableKey": "dimdate",
                            "connId": "12509",
                            "tableId": "13826",
                            "parentCategory": "table",
                            "uniqueKey": "40307"
                        },
                        "rating": {
                            "alias": "rating",
                            "fullyQualifiedColumn": "dimdate.rating",
                            "id": "40308",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            },
                            "category": "column",
                            "columnKey": "rating",
                            "name": "rating",
                            "tableKey": "dimdate",
                            "connId": "12509",
                            "tableId": "13826",
                            "parentCategory": "table",
                            "uniqueKey": "40308"
                        }
                    },
                    "name": "dimdate",
                    "cacheId": "4ac5d9f68b58bd7c0d179146e46795be",
                    "keyName": "dimdate",
                    "dataSource": {
                        "sync": false,
                        "id": "1",
                        "catSchemaPredicted": false,
                        "catalog": "",
                        "schema": "HIUSER",
                        "type": "dynamicDataSource",
                        "baseType": "global.jdbc",
                        "dbId": "12509",
                        "driver": {
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
                        "datasourceName": "SampleTravelDataDerby",
                        "driverType": "Derby",
                        "connId": "12509",
                        "classifier": "db.generic",
                        "joinsFetched": true
                    },
                    "category": "table",
                    "selected": true,
                    "columnsFetched": true,
                    "keyPath": "f6dbad15-9e84-4e60-9c96-11f18a1d45f4",
                    "uniqueKey": "13826",
                    "connId": "12509"
                },
                "dimdate_1": {
                    "id": "13827",
                    "alias": "dimdate_1",
                    "columns": {
                        "dim_id": {
                            "alias": "dim_id",
                            "fullyQualifiedColumn": "dimdate_1.dim_id",
                            "id": "40309",
                            "defaultFunction": "db.generic.aggregate.sum",
                            "type": {
                                "java.lang.Integer": "numeric"
                            },
                            "category": "column",
                            "columnKey": "dim_id",
                            "name": "dim_id",
                            "tableKey": "dimdate_1",
                            "connId": "12509",
                            "tableId": "13827",
                            "parentCategory": "table",
                            "uniqueKey": "40309"
                        },
                        "fiscal_year": {
                            "alias": "fiscal_year",
                            "fullyQualifiedColumn": "dimdate_1.fiscal_year",
                            "id": "40310",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.sql.Date": "date"
                            },
                            "category": "column",
                            "columnKey": "fiscal_year",
                            "name": "fiscal_year",
                            "tableKey": "dimdate_1",
                            "connId": "12509",
                            "tableId": "13827",
                            "parentCategory": "table",
                            "uniqueKey": "40310"
                        },
                        "modified_date": {
                            "alias": "modified_date",
                            "fullyQualifiedColumn": "dimdate_1.modified_date",
                            "id": "40311",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.sql.Timestamp": "dateTime"
                            },
                            "category": "column",
                            "columnKey": "modified_date",
                            "name": "modified_date",
                            "tableKey": "dimdate_1",
                            "connId": "12509",
                            "tableId": "13827",
                            "parentCategory": "table",
                            "uniqueKey": "40311"
                        },
                        "date_key": {
                            "alias": "date_key",
                            "fullyQualifiedColumn": "dimdate_1.date_key",
                            "id": "40312",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            },
                            "category": "column",
                            "columnKey": "date_key",
                            "name": "date_key",
                            "tableKey": "dimdate_1",
                            "connId": "12509",
                            "tableId": "13827",
                            "parentCategory": "table",
                            "uniqueKey": "40312"
                        },
                        "day_number": {
                            "alias": "day_number",
                            "fullyQualifiedColumn": "dimdate_1.day_number",
                            "id": "40313",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            },
                            "category": "column",
                            "columnKey": "day_number",
                            "name": "day_number",
                            "tableKey": "dimdate_1",
                            "connId": "12509",
                            "tableId": "13827",
                            "parentCategory": "table",
                            "uniqueKey": "40313"
                        },
                        "fiscal_month_name": {
                            "alias": "fiscal_month_name",
                            "fullyQualifiedColumn": "dimdate_1.fiscal_month_name",
                            "id": "40314",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            },
                            "category": "column",
                            "columnKey": "fiscal_month_name",
                            "name": "fiscal_month_name",
                            "tableKey": "dimdate_1",
                            "connId": "12509",
                            "tableId": "13827",
                            "parentCategory": "table",
                            "uniqueKey": "40314"
                        },
                        "fiscal_month_label": {
                            "alias": "fiscal_month_label",
                            "fullyQualifiedColumn": "dimdate_1.fiscal_month_label",
                            "id": "40315",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            },
                            "category": "column",
                            "columnKey": "fiscal_month_label",
                            "name": "fiscal_month_label",
                            "tableKey": "dimdate_1",
                            "connId": "12509",
                            "tableId": "13827",
                            "parentCategory": "table",
                            "uniqueKey": "40315"
                        },
                        "created_date": {
                            "alias": "created_date",
                            "fullyQualifiedColumn": "dimdate_1.created_date",
                            "id": "40316",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            },
                            "category": "column",
                            "columnKey": "created_date",
                            "name": "created_date",
                            "tableKey": "dimdate_1",
                            "connId": "12509",
                            "tableId": "13827",
                            "parentCategory": "table",
                            "uniqueKey": "40316"
                        },
                        "created_time": {
                            "alias": "created_time",
                            "fullyQualifiedColumn": "dimdate_1.created_time",
                            "id": "40317",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            },
                            "category": "column",
                            "columnKey": "created_time",
                            "name": "created_time",
                            "tableKey": "dimdate_1",
                            "connId": "12509",
                            "tableId": "13827",
                            "parentCategory": "table",
                            "uniqueKey": "40317"
                        },
                        "rating": {
                            "alias": "rating",
                            "fullyQualifiedColumn": "dimdate_1.rating",
                            "id": "40318",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            },
                            "category": "column",
                            "columnKey": "rating",
                            "name": "rating",
                            "tableKey": "dimdate_1",
                            "connId": "12509",
                            "tableId": "13827",
                            "parentCategory": "table",
                            "uniqueKey": "40318"
                        }
                    },
                    "duplicate": "true",
                    "originalName": "dimdate",
                    "name": "dimdate_1",
                    "cacheId": "a40fdce3061928b34e4024ab36545ae2",
                    "keyName": "dimdate_1",
                    "dataSource": {
                        "sync": false,
                        "id": "1",
                        "catSchemaPredicted": false,
                        "catalog": "",
                        "schema": "HIUSER",
                        "type": "dynamicDataSource",
                        "baseType": "global.jdbc",
                        "dbId": "12509",
                        "driver": {
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
                        "datasourceName": "SampleTravelDataDerby",
                        "driverType": "Derby",
                        "connId": "12509",
                        "classifier": "db.generic",
                        "joinsFetched": true
                    },
                    "category": "table",
                    "selected": true,
                    "columnsFetched": true,
                    "keyPath": "4117f9d0-5938-44b1-81ee-92c985123f16",
                    "uniqueKey": "13827",
                    "connId": "12509"
                },
                "employee_details": {
                    "id": "13828",
                    "alias": "employee_details",
                    "columns": {
                        "employee_id": {
                            "alias": "employee_id",
                            "fullyQualifiedColumn": "employee_details.employee_id",
                            "id": "40319",
                            "defaultFunction": "db.generic.aggregate.sum",
                            "type": {
                                "java.lang.Integer": "numeric"
                            },
                            "category": "column",
                            "columnKey": "employee_id",
                            "name": "employee_id",
                            "tableKey": "employee_details",
                            "connId": "12509",
                            "tableId": "13828",
                            "parentCategory": "table",
                            "uniqueKey": "40319_12509"
                        },
                        "employee_name": {
                            "alias": "employee_name",
                            "fullyQualifiedColumn": "employee_details.employee_name",
                            "id": "40320",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            },
                            "category": "column",
                            "columnKey": "employee_name",
                            "name": "employee_name",
                            "tableKey": "employee_details",
                            "connId": "12509",
                            "tableId": "13828",
                            "parentCategory": "table",
                            "uniqueKey": "40320_12509"
                        },
                        "age": {
                            "alias": "age",
                            "fullyQualifiedColumn": "employee_details.age",
                            "id": "40321",
                            "defaultFunction": "db.generic.aggregate.sum",
                            "type": {
                                "java.lang.Integer": "numeric"
                            },
                            "category": "column",
                            "columnKey": "age",
                            "name": "age",
                            "tableKey": "employee_details",
                            "connId": "12509",
                            "tableId": "13828",
                            "parentCategory": "table",
                            "uniqueKey": "40321_12509"
                        },
                        "address": {
                            "alias": "address",
                            "fullyQualifiedColumn": "employee_details.address",
                            "id": "40322",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            },
                            "category": "column",
                            "columnKey": "address",
                            "name": "address",
                            "tableKey": "employee_details",
                            "connId": "12509",
                            "tableId": "13828",
                            "parentCategory": "table",
                            "uniqueKey": "40322_12509"
                        }
                    },
                    "name": "employee_details",
                    "cacheId": "4e1fd245f4d13b77be423a43f01d80b2",
                    "keyName": "employee_details",
                    "dataSource": {
                        "sync": false,
                        "id": "1",
                        "catSchemaPredicted": false,
                        "catalog": "",
                        "schema": "HIUSER",
                        "type": "dynamicDataSource",
                        "baseType": "global.jdbc",
                        "dbId": "12509",
                        "driver": {
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
                        "datasourceName": "SampleTravelDataDerby",
                        "driverType": "Derby",
                        "connId": "12509",
                        "classifier": "db.generic",
                        "joinsFetched": true
                    },
                    "category": "table",
                    "selected": true,
                    "columnsFetched": true,
                    "keyPath": "449e875d-4a77-412b-a3d6-198843612e52",
                    "uniqueKey": "13828",
                    "connId": "12509"
                },
                "employee_details_1": {
                    "id": "s6d9-96f8-6b7a-hebw-h0",
                    "alias": "employee_details_1",
                    "columns": {
                        "employee_id": {
                            "alias": "employee_id",
                            "fullyQualifiedColumn": "employee_details.employee_id",
                            "id": "2ku1-a8sc-2qw5-jdbd-3q",
                            "defaultFunction": "db.generic.aggregate.sum",
                            "type": {
                                "java.lang.Integer": "numeric"
                            },
                            "category": "column",
                            "columnKey": "employee_id",
                            "name": "employee_id",
                            "tableKey": "employee_details_1",
                            "connId": "12509",
                            "tableId": "s6d9-96f8-6b7a-hebw-h0",
                            "parentCategory": "table",
                            "uniqueKey": "2ku1-a8sc-2qw5-jdbd-3q_12509",
                            "originalId": "40319"
                        },
                        "employee_name": {
                            "alias": "employee_name",
                            "fullyQualifiedColumn": "employee_details.employee_name",
                            "id": "3up8-8d16-byjb-ii6x-zf",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            },
                            "category": "column",
                            "columnKey": "employee_name",
                            "name": "employee_name",
                            "tableKey": "employee_details_1",
                            "connId": "12509",
                            "tableId": "s6d9-96f8-6b7a-hebw-h0",
                            "parentCategory": "table",
                            "uniqueKey": "3up8-8d16-byjb-ii6x-zf_12509",
                            "originalId": "40320"
                        },
                        "age": {
                            "alias": "age",
                            "fullyQualifiedColumn": "employee_details.age",
                            "id": "ux35-7w0a-3k1t-7t0b-81",
                            "defaultFunction": "db.generic.aggregate.sum",
                            "type": {
                                "java.lang.Integer": "numeric"
                            },
                            "category": "column",
                            "columnKey": "age",
                            "name": "age",
                            "tableKey": "employee_details_1",
                            "connId": "12509",
                            "tableId": "s6d9-96f8-6b7a-hebw-h0",
                            "parentCategory": "table",
                            "uniqueKey": "ux35-7w0a-3k1t-7t0b-81_12509",
                            "originalId": "40321"
                        },
                        "address": {
                            "alias": "address",
                            "fullyQualifiedColumn": "employee_details.address",
                            "id": "tosc-elur-qoa0-4qnp-i7",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            },
                            "category": "column",
                            "columnKey": "address",
                            "name": "address",
                            "tableKey": "employee_details_1",
                            "connId": "12509",
                            "tableId": "s6d9-96f8-6b7a-hebw-h0",
                            "parentCategory": "table",
                            "uniqueKey": "tosc-elur-qoa0-4qnp-i7_12509",
                            "originalId": "40322"
                        }
                    },
                    "name": "employee_details_1",
                    "cacheId": "4e1fd245f4d13b77be423a43f01d80b2",
                    "keyName": "employee_details_1",
                    "dataSource": {
                        "sync": false,
                        "id": "1",
                        "catSchemaPredicted": false,
                        "catalog": "",
                        "schema": "HIUSER",
                        "type": "dynamicDataSource",
                        "baseType": "global.jdbc",
                        "dbId": "12509",
                        "driver": {
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
                        "datasourceName": "SampleTravelDataDerby",
                        "driverType": "Derby",
                        "connId": "12509",
                        "classifier": "db.generic",
                        "joinsFetched": true
                    },
                    "category": "table",
                    "selected": true,
                    "columnsFetched": true,
                    "keyPath": "449e875d-4a77-412b-a3d6-198843612e52",
                    "uniqueKey": "s6d9-96f8-6b7a-hebw-h0_12509",
                    "connId": "12509",
                    "originalName": "employee_details",
                    "originalId": "13828",
                    "duplicate": true
                }
            },
            "connection": {
                "sync": false,
                "id": "1",
                "catSchemaPredicted": false,
                "catalog": "",
                "schema": "HIUSER",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "dbId": "12509",
                "driver": {
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
                "datasourceName": "SampleTravelDataDerby",
                "driverType": "Derby",
                "connId": "12509",
                "classifier": "db.generic",
                "joinsFetched": true,
                "database": "HIUSER",
                "connectionDatabaseId": "148e58f6-5ace-42b2-bee8-086b64897d7e",
                "databaseType": "Derby"
            },
            "duplicateTableList": [
                "s6d9-96f8-6b7a-hebw-h0"
            ],
            "savedTableIds": [
                {
                    "id": "13826",
                    "dbId": "12509"
                },
                {
                    "id": "13827",
                    "dbId": "12509"
                },
                {
                    "id": "13828",
                    "dbId": "12509"
                }
            ]
        },
        result:[
            {
                "alias": "employee_details_1",
                "id": "s6d9-96f8-6b7a-hebw-h0",
                "columns": [
                    {
                        "alias": "employee_id",
                        "connId": "12509",
                        "originalId": "40319",
                        "id": "2ku1-a8sc-2qw5-jdbd-3q",
                        "name": "employee_id"
                    },
                    {
                        "alias": "employee_name",
                        "connId": "12509",
                        "originalId": "40320",
                        "id": "3up8-8d16-byjb-ii6x-zf",
                        "name": "employee_name"
                    },
                    {
                        "alias": "age",
                        "connId": "12509",
                        "originalId": "40321",
                        "id": "ux35-7w0a-3k1t-7t0b-81",
                        "name": "age"
                    },
                    {
                        "alias": "address",
                        "connId": "12509",
                        "originalId": "40322",
                        "id": "tosc-elur-qoa0-4qnp-i7",
                        "name": "address"
                    }
                ],
                "connId": "12509",
                "originalName": "employee_details",
                "originalId": "13828",
                "name": "employee_details_1"
            }
        ]
    }
}