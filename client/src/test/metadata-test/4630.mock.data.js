export const data4630 = {
    tables: {
        "geo_cordinates": {
            "id": "be534112989b616b194bc59c2fb25a42",
            "name": "geo_cordinates",
            "data": {
                "id": "1",
                "type": "dynamicDataSource"
            },
            "keyPath": "8gu6-yihc-bjtg-s2db-kk/ewdw-5f0v-roid-6zix-av/ikhk-yfkf-7l5m-v6nx-c2/qzwf-ng5n-8wfk-ainx-pw",
            "key": "qzwf-ng5n-8wfk-ainx-pw",
            "alias": "geo_cordinates",
            "uuid": "qzwf-ng5n-8wfk-ainx-pw",
            "connId": "yec7z",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "yec7z",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "8gu6-yihc-bjtg-s2db-kk/ewdw-5f0v-roid-6zix-av/ikhk-yfkf-7l5m-v6nx-c2",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "geo_cordinates_yec7z",
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
                    "originalId": "31c8be28-0646-4202-85e0-be71a3127daa",
                    "tableKey": "geo_cordinates",
                    "connId": "yec7z",
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
                    "originalId": "80d19759-87c9-40d7-8aef-3bbbff6ffdf3",
                    "tableKey": "geo_cordinates",
                    "connId": "yec7z",
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
                    "originalId": "43d10cc0-5b87-4e11-9aff-61e799f0ca06",
                    "tableKey": "geo_cordinates",
                    "connId": "yec7z",
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
                    "originalId": "2d045f77-980c-4873-9e73-88d6f1ef9b5d",
                    "tableKey": "geo_cordinates",
                    "connId": "yec7z",
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
            "keyPath": "8gu6-yihc-bjtg-s2db-kk/ewdw-5f0v-roid-6zix-av/ikhk-yfkf-7l5m-v6nx-c2/uomu-d7vm-848o-upi4-y7",
            "key": "uomu-d7vm-848o-upi4-y7",
            "alias": "meeting_details",
            "uuid": "uomu-d7vm-848o-upi4-y7",
            "connId": "yec7z",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "yec7z",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "8gu6-yihc-bjtg-s2db-kk/ewdw-5f0v-roid-6zix-av/ikhk-yfkf-7l5m-v6nx-c2",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "meeting_details_yec7z",
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
                    "originalId": "808649d5-ad45-4047-93e0-ee22a886b021",
                    "tableKey": "meeting_details",
                    "connId": "yec7z",
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
                    "originalId": "e72eb60f-bfef-4635-9d94-56d3a00130f7",
                    "tableKey": "meeting_details",
                    "connId": "yec7z",
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
                    "originalId": "c2dd08cf-7596-4656-b8db-c6dbd7a78260",
                    "tableKey": "meeting_details",
                    "connId": "yec7z",
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
                    "originalId": "989d8b94-c4a7-4362-9f33-1142650a5271",
                    "tableKey": "meeting_details",
                    "connId": "yec7z",
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
                    "originalId": "78161bfa-faa5-4d81-acf7-6e780d3e24a9",
                    "tableKey": "meeting_details",
                    "connId": "yec7z",
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
                    "originalId": "a7af7642-ad80-4e4c-b2d5-2efbb90b7870",
                    "tableKey": "meeting_details",
                    "connId": "yec7z",
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
                    "originalId": "d1829781-e8d4-4af5-9e21-a8c8faca66bd",
                    "tableKey": "meeting_details",
                    "connId": "yec7z",
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
                    "originalId": "de4fa140-87e0-4049-a05b-25e143f797c3",
                    "tableKey": "meeting_details",
                    "connId": "yec7z",
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
            "keyPath": "8gu6-yihc-bjtg-s2db-kk/ewdw-5f0v-roid-6zix-av/ikhk-yfkf-7l5m-v6nx-c2/eh1d-2l9z-14ap-atak-2m",
            "key": "eh1d-2l9z-14ap-atak-2m",
            "alias": "employee_details",
            "uuid": "eh1d-2l9z-14ap-atak-2m",
            "connId": "yec7z",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "yec7z",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "8gu6-yihc-bjtg-s2db-kk/ewdw-5f0v-roid-6zix-av/ikhk-yfkf-7l5m-v6nx-c2",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "employee_details_yec7z",
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
                    "originalId": "a7dffc37-4a83-4c7b-ab84-c1d5c8149497",
                    "tableKey": "employee_details",
                    "connId": "yec7z",
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
                    "originalId": "d900762d-959e-45b2-b8b8-12533faafbe7",
                    "tableKey": "employee_details",
                    "connId": "yec7z",
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
                    "originalId": "2b0268e0-88e2-40b2-87d9-4d0e0c6850dc",
                    "tableKey": "employee_details",
                    "connId": "yec7z",
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
                    "originalId": "07509042-6590-4a61-a92e-e15698af0ce0",
                    "tableKey": "employee_details",
                    "connId": "yec7z",
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
            "keyPath": "8gu6-yihc-bjtg-s2db-kk/ewdw-5f0v-roid-6zix-av/ikhk-yfkf-7l5m-v6nx-c2/pu0p-ywlk-l9vu-ulrg-va",
            "key": "pu0p-ywlk-l9vu-ulrg-va",
            "alias": "dimdate",
            "uuid": "pu0p-ywlk-l9vu-ulrg-va",
            "connId": "yec7z",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "yec7z",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "8gu6-yihc-bjtg-s2db-kk/ewdw-5f0v-roid-6zix-av/ikhk-yfkf-7l5m-v6nx-c2",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "dimdate_yec7z",
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
                    "originalId": "0fd750c5-d63b-4b39-ba9f-9b56234fb250",
                    "tableKey": "dimdate",
                    "connId": "yec7z",
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
                    "originalId": "5d240021-1dcd-4e9e-bc05-9829457a699f",
                    "tableKey": "dimdate",
                    "connId": "yec7z",
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
                    "originalId": "11633f13-2e56-46c6-bef1-928a43f2aa87",
                    "tableKey": "dimdate",
                    "connId": "yec7z",
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
                    "originalId": "d0063b02-e665-4d73-b1f0-ba72223dbd85",
                    "tableKey": "dimdate",
                    "connId": "yec7z",
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
                    "originalId": "f082094d-f0f5-4e67-b7c6-ae1127f202bf",
                    "tableKey": "dimdate",
                    "connId": "yec7z",
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
                    "originalId": "e2f9a23c-8669-4e5d-9840-4fcf0d24390c",
                    "tableKey": "dimdate",
                    "connId": "yec7z",
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
                    "originalId": "d5779055-ac77-48ac-bc60-c7e95d2791c0",
                    "tableKey": "dimdate",
                    "connId": "yec7z",
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
                    "originalId": "cf5f58aa-afb6-416a-8658-f8216b3603a7",
                    "tableKey": "dimdate",
                    "connId": "yec7z",
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
                    "originalId": "61a8fef3-d4ee-47be-8ea7-1d03bc6ec2a8",
                    "tableKey": "dimdate",
                    "connId": "yec7z",
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
                    "originalId": "6460a81b-a8d8-4324-a6fd-be31f0137b3d",
                    "tableKey": "dimdate",
                    "connId": "yec7z",
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
            "keyPath": "8gu6-yihc-bjtg-s2db-kk/ewdw-5f0v-roid-6zix-av/ikhk-yfkf-7l5m-v6nx-c2/5grv-ikcs-9ilr-er5b-7h",
            "key": "5grv-ikcs-9ilr-er5b-7h",
            "alias": "travel_details_alias",
            "uuid": "5grv-ikcs-9ilr-er5b-7h",
            "connId": "yec7z",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "yec7z",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "8gu6-yihc-bjtg-s2db-kk/ewdw-5f0v-roid-6zix-av/ikhk-yfkf-7l5m-v6nx-c2",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "travel_details_yec7z",
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
                    "originalId": "5487e42b-2a1d-47a6-beb1-f317a20860fa",
                    "tableKey": "travel_details",
                    "connId": "yec7z",
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
                    "originalId": "3f26aa18-6fbc-445d-bc46-a997bbccd311",
                    "tableKey": "travel_details",
                    "connId": "yec7z",
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
                    "originalId": "d112cb34-862f-443d-8ff4-1b918746b5eb",
                    "tableKey": "travel_details",
                    "connId": "yec7z",
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
                    "originalId": "b6014810-8342-49b8-b830-574c31f145e9",
                    "tableKey": "travel_details",
                    "connId": "yec7z",
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
                    "originalId": "4116b972-01d3-4136-b166-37bf3cd23a22",
                    "tableKey": "travel_details",
                    "connId": "yec7z",
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
                    "originalId": "a5ed09c1-2658-4708-8024-15bd5e3b816c",
                    "tableKey": "travel_details",
                    "connId": "yec7z",
                    "duplicateIndex": 0,
                    "columnKey": "source"
                },
                "destination_id": {
                    "alias": "destination_id_alias",
                    "fullyQualifiedColumn": "travel_details.destination_id",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    },
                    "category": "column",
                    "originalName": "destination_id",
                    "originalId": "285ee1c6-7040-44a8-8b8f-07e2c1275b53",
                    "tableKey": "travel_details",
                    "connId": "yec7z",
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
                    "originalId": "22004b91-4a43-49b3-91a7-c99cfc1a260b",
                    "tableKey": "travel_details",
                    "connId": "yec7z",
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
                    "originalId": "47589ae6-31cf-4cde-ad7b-9e4f25c77a8b",
                    "tableKey": "travel_details",
                    "connId": "yec7z",
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
                    "originalId": "304ec06f-b3bb-468a-ba34-61a771a82112",
                    "tableKey": "travel_details",
                    "connId": "yec7z",
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
                    "originalId": "406be9eb-b92b-4916-b1f4-f11a50fc5ea0",
                    "tableKey": "travel_details",
                    "connId": "yec7z",
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
                    "originalId": "a03f4dfe-8807-46c0-b52a-d4c6e28e44c9",
                    "tableKey": "travel_details",
                    "connId": "yec7z",
                    "duplicateIndex": 0,
                    "columnKey": "travelled_by"
                }
            },
            "columnsFetched": true
        }
    },
    joins: [
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
            "uuid": "b3sc-463m-j2on-q6nf-57",
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
            "uuid": "d6rd-nw6z-w9j0-ome4-fe",
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
            "uuid": "pz6l-a6l0-8z5y-h9ee-ph",
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
            "uuid": "xwma-3zq6-5i1i-8kkc-go",
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
            "uuid": "yuyg-k01h-ws5i-l1nh-5q",
            "index": 5,
            "action": "noChange"
        }
    ]
}