export const tablesData = {
    "geo_cordinates": {
        "id": "be534112989b616b194bc59c2fb25a42",
        "name": "geo_cordinates",
        "data": {
            "id": "1",
            "type": "dynamicDataSource"
        },
        "keyPath": "ucbi-ubn4-3bw2-r52p-83/ii4r-ow3p-6mkx-6jda-42/e8dx-uivk-4cnx-70s0-2a/i7gu-irvc-x51h-nj0e-2g",
        "key": "i7gu-irvc-x51h-nj0e-2g",
        "alias": "geo_cordinates",
        "uuid": "i7gu-irvc-x51h-nj0e-2g",
        "connId": "vjkdk",
        "dataSource": {
            "id": "1",
            "type": "dynamicDataSource",
            "baseType": "global.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "",
            "schema": "HIUSER",
            "connId": "vjkdk",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelDataDerby",
            "dsKeyPath": "ucbi-ubn4-3bw2-r52p-83/ii4r-ow3p-6mkx-6jda-42/e8dx-uivk-4cnx-70s0-2a",
            "driverType": "Derby",
            "database": "HIUSER"
        },
        "dataSourceName": "SampleTravelDataDerby",
        "category": "table",
        "nameWithConnId": "geo_cordinates_vjkdk",
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
                "originalId": "3a089d22-6490-42ca-ad22-bd41849321b8",
                "tableKey": "geo_cordinates",
                "connId": "vjkdk",
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
                "originalId": "596ac973-68dc-42fd-b1fd-b2894f476576",
                "tableKey": "geo_cordinates",
                "connId": "vjkdk",
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
                "originalId": "48652deb-89a2-4dd1-b009-d6c36a0f4889",
                "tableKey": "geo_cordinates",
                "connId": "vjkdk",
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
                "originalId": "a4ae86df-b9c8-4e89-a9bb-00ee2b8201c8",
                "tableKey": "geo_cordinates",
                "connId": "vjkdk",
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
        "keyPath": "ucbi-ubn4-3bw2-r52p-83/ii4r-ow3p-6mkx-6jda-42/e8dx-uivk-4cnx-70s0-2a/ukeq-ylax-7d7x-uicr-l5",
        "key": "ukeq-ylax-7d7x-uicr-l5",
        "alias": "meeting_details",
        "uuid": "ukeq-ylax-7d7x-uicr-l5",
        "connId": "vjkdk",
        "dataSource": {
            "id": "1",
            "type": "dynamicDataSource",
            "baseType": "global.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "",
            "schema": "HIUSER",
            "connId": "vjkdk",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelDataDerby",
            "dsKeyPath": "ucbi-ubn4-3bw2-r52p-83/ii4r-ow3p-6mkx-6jda-42/e8dx-uivk-4cnx-70s0-2a",
            "driverType": "Derby",
            "database": "HIUSER"
        },
        "dataSourceName": "SampleTravelDataDerby",
        "category": "table",
        "nameWithConnId": "meeting_details_vjkdk",
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
                "originalId": "06d24ac9-7258-4e9f-91ab-92ab5b1c24c4",
                "tableKey": "meeting_details",
                "connId": "vjkdk",
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
                "originalId": "7ed4195b-105f-4513-8395-9bdec23fdadd",
                "tableKey": "meeting_details",
                "connId": "vjkdk",
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
                "originalId": "b11e9f73-e653-4ab8-84c4-42afdd6fa177",
                "tableKey": "meeting_details",
                "connId": "vjkdk",
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
                "originalId": "2a961661-bda5-4784-b39d-480c22da115a",
                "tableKey": "meeting_details",
                "connId": "vjkdk",
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
                "originalId": "db0ad523-60e0-4b69-b5bf-386548859697",
                "tableKey": "meeting_details",
                "connId": "vjkdk",
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
                "originalId": "bdec423b-19c0-48bc-a98b-e4a0b15d186e",
                "tableKey": "meeting_details",
                "connId": "vjkdk",
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
                "originalId": "9a370f88-aacf-4008-9aca-e93b926e034f",
                "tableKey": "meeting_details",
                "connId": "vjkdk",
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
                "originalId": "7a9581ec-ee71-4b3a-b96b-41e5aa1a99a1",
                "tableKey": "meeting_details",
                "connId": "vjkdk",
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
        "keyPath": "ucbi-ubn4-3bw2-r52p-83/ii4r-ow3p-6mkx-6jda-42/e8dx-uivk-4cnx-70s0-2a/ano2-7epm-drpp-5ni3-nt",
        "key": "ano2-7epm-drpp-5ni3-nt",
        "alias": "employee_details",
        "uuid": "ano2-7epm-drpp-5ni3-nt",
        "connId": "vjkdk",
        "dataSource": {
            "id": "1",
            "type": "dynamicDataSource",
            "baseType": "global.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "",
            "schema": "HIUSER",
            "connId": "vjkdk",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelDataDerby",
            "dsKeyPath": "ucbi-ubn4-3bw2-r52p-83/ii4r-ow3p-6mkx-6jda-42/e8dx-uivk-4cnx-70s0-2a",
            "driverType": "Derby",
            "database": "HIUSER"
        },
        "dataSourceName": "SampleTravelDataDerby",
        "category": "table",
        "nameWithConnId": "employee_details_vjkdk",
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
                "originalId": "910348df-5ae3-4a21-bc7b-2465da7d4a43",
                "tableKey": "employee_details",
                "connId": "vjkdk",
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
                "originalId": "cc123572-8bd9-4257-9924-4fb2c0320489",
                "tableKey": "employee_details",
                "connId": "vjkdk",
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
                "originalId": "7e7c9d5c-f59b-4266-a130-3ae49c003934",
                "tableKey": "employee_details",
                "connId": "vjkdk",
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
                "originalId": "ecee1cf0-fc1b-48f4-9fb8-7972829a626b",
                "tableKey": "employee_details",
                "connId": "vjkdk",
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
        "keyPath": "ucbi-ubn4-3bw2-r52p-83/ii4r-ow3p-6mkx-6jda-42/e8dx-uivk-4cnx-70s0-2a/2gkd-904s-czs0-n85a-vm",
        "key": "2gkd-904s-czs0-n85a-vm",
        "alias": "dimdate",
        "uuid": "2gkd-904s-czs0-n85a-vm",
        "connId": "vjkdk",
        "dataSource": {
            "id": "1",
            "type": "dynamicDataSource",
            "baseType": "global.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "",
            "schema": "HIUSER",
            "connId": "vjkdk",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelDataDerby",
            "dsKeyPath": "ucbi-ubn4-3bw2-r52p-83/ii4r-ow3p-6mkx-6jda-42/e8dx-uivk-4cnx-70s0-2a",
            "driverType": "Derby",
            "database": "HIUSER"
        },
        "dataSourceName": "SampleTravelDataDerby",
        "category": "table",
        "nameWithConnId": "dimdate_vjkdk",
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
                "originalId": "ae2124d9-804f-4c36-8a93-15918ed719ad",
                "tableKey": "dimdate",
                "connId": "vjkdk",
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
                "originalId": "b8784a2c-44ff-4796-8196-c1e7e5675db8",
                "tableKey": "dimdate",
                "connId": "vjkdk",
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
                "originalId": "1c53e2d6-508e-45fe-b717-b3452d808881",
                "tableKey": "dimdate",
                "connId": "vjkdk",
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
                "originalId": "28fc7110-fef6-414b-9d00-99d2601ee0e7",
                "tableKey": "dimdate",
                "connId": "vjkdk",
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
                "originalId": "5d0dd41c-0925-4f62-9e29-7d7e5be3abd3",
                "tableKey": "dimdate",
                "connId": "vjkdk",
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
                "originalId": "658d6ba9-c964-4030-b35c-de2d30469acc",
                "tableKey": "dimdate",
                "connId": "vjkdk",
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
                "originalId": "8207495e-a208-4fa5-a4f3-2323b6a3ec64",
                "tableKey": "dimdate",
                "connId": "vjkdk",
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
                "originalId": "58088583-4df7-4924-a0ad-73e0fa8d0c3f",
                "tableKey": "dimdate",
                "connId": "vjkdk",
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
                "originalId": "5408b4b8-c928-4d96-bf56-f984c72a3874",
                "tableKey": "dimdate",
                "connId": "vjkdk",
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
                "originalId": "07158709-d22a-448e-a86d-8418dbf1efbf",
                "tableKey": "dimdate",
                "connId": "vjkdk",
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
        "keyPath": "ucbi-ubn4-3bw2-r52p-83/ii4r-ow3p-6mkx-6jda-42/e8dx-uivk-4cnx-70s0-2a/6av6-2uxv-vcsu-8vno-wl",
        "key": "6av6-2uxv-vcsu-8vno-wl",
        "alias": "travel_details",
        "uuid": "6av6-2uxv-vcsu-8vno-wl",
        "connId": "vjkdk",
        "dataSource": {
            "id": "1",
            "type": "dynamicDataSource",
            "baseType": "global.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "",
            "schema": "HIUSER",
            "connId": "vjkdk",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelDataDerby",
            "dsKeyPath": "ucbi-ubn4-3bw2-r52p-83/ii4r-ow3p-6mkx-6jda-42/e8dx-uivk-4cnx-70s0-2a",
            "driverType": "Derby",
            "database": "HIUSER"
        },
        "dataSourceName": "SampleTravelDataDerby",
        "category": "table",
        "nameWithConnId": "travel_details_vjkdk",
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
                "originalId": "7532edac-0801-4291-98d5-4527dc858628",
                "tableKey": "travel_details",
                "connId": "vjkdk",
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
                "originalId": "2ee85897-1b36-4105-b2a7-ccec81f2fc18",
                "tableKey": "travel_details",
                "connId": "vjkdk",
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
                "originalId": "4f4019cb-3b06-4845-baa0-f16aa746fdfd",
                "tableKey": "travel_details",
                "connId": "vjkdk",
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
                "originalId": "a4f10e1b-6d6e-462e-8fc9-0a11f4812c6f",
                "tableKey": "travel_details",
                "connId": "vjkdk",
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
                "originalId": "bf5c47f9-77f4-4673-9b6b-757f75844852",
                "tableKey": "travel_details",
                "connId": "vjkdk",
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
                "originalId": "019c6bac-405d-4e20-b193-8cf241e92ad5",
                "tableKey": "travel_details",
                "connId": "vjkdk",
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
                "originalId": "fa28cb1f-c023-41cb-aaf0-cbb6bca26935",
                "tableKey": "travel_details",
                "connId": "vjkdk",
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
                "originalId": "b473e074-cbc9-4ead-b338-203a7a4abfca",
                "tableKey": "travel_details",
                "connId": "vjkdk",
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
                "originalId": "7c57136a-7dd6-464e-b7a0-e102e8e526bc",
                "tableKey": "travel_details",
                "connId": "vjkdk",
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
                "originalId": "e3a75d9a-c5a0-4eef-989e-41275ce07cc5",
                "tableKey": "travel_details",
                "connId": "vjkdk",
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
                "originalId": "b2d32274-7c2a-4bc1-bbda-4a3942bf1030",
                "tableKey": "travel_details",
                "connId": "vjkdk",
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
                "originalId": "89c7da52-f633-4146-80ad-d7172c4d425c",
                "tableKey": "travel_details",
                "connId": "vjkdk",
                "duplicateIndex": 0,
                "columnKey": "travelled_by"
            }
        },
        "columnsFetched": true
    }
}


export const tablesWithoutColumns = {
        "geo_cordinates": {
            "id": "be534112989b616b194bc59c2fb25a42",
            "name": "geo_cordinates",
            "data": {
                "id": "1",
                "type": "dynamicDataSource"
            },
            "keyPath": "nola-8a72-38i3-x0xu-jf/73wb-slzy-htoh-nt6x-t1/pu9o-99t5-q82s-0jps-wt/j8aa-8lao-x1mk-scdp-8q",
            "key": "j8aa-8lao-x1mk-scdp-8q",
            "alias": "geo_cordinates",
            "uuid": "j8aa-8lao-x1mk-scdp-8q",
            "connId": "vw9jd",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "vw9jd",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "nola-8a72-38i3-x0xu-jf/73wb-slzy-htoh-nt6x-t1/pu9o-99t5-q82s-0jps-wt",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "geo_cordinates_vw9jd",
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
            "keyPath": "nola-8a72-38i3-x0xu-jf/73wb-slzy-htoh-nt6x-t1/pu9o-99t5-q82s-0jps-wt/mzzb-y0do-w8tj-fhtl-nk",
            "key": "mzzb-y0do-w8tj-fhtl-nk",
            "alias": "meeting_details",
            "uuid": "mzzb-y0do-w8tj-fhtl-nk",
            "connId": "vw9jd",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "vw9jd",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "nola-8a72-38i3-x0xu-jf/73wb-slzy-htoh-nt6x-t1/pu9o-99t5-q82s-0jps-wt",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "meeting_details_vw9jd",
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
                "type": "dynamicDataSource"
            },
            "keyPath": "nola-8a72-38i3-x0xu-jf/73wb-slzy-htoh-nt6x-t1/pu9o-99t5-q82s-0jps-wt/q9yy-1y6s-hl04-nwa6-30",
            "key": "q9yy-1y6s-hl04-nwa6-30",
            "alias": "employee_details",
            "uuid": "q9yy-1y6s-hl04-nwa6-30",
            "connId": "vw9jd",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "vw9jd",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "nola-8a72-38i3-x0xu-jf/73wb-slzy-htoh-nt6x-t1/pu9o-99t5-q82s-0jps-wt",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "employee_details_vw9jd",
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
                "type": "dynamicDataSource"
            },
            "keyPath": "nola-8a72-38i3-x0xu-jf/73wb-slzy-htoh-nt6x-t1/pu9o-99t5-q82s-0jps-wt/uu0f-hhq2-och5-jagd-jh",
            "key": "uu0f-hhq2-och5-jagd-jh",
            "alias": "dimdate",
            "uuid": "uu0f-hhq2-och5-jagd-jh",
            "connId": "vw9jd",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "vw9jd",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "nola-8a72-38i3-x0xu-jf/73wb-slzy-htoh-nt6x-t1/pu9o-99t5-q82s-0jps-wt",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "dimdate_vw9jd",
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
                "type": "dynamicDataSource"
            },
            "keyPath": "nola-8a72-38i3-x0xu-jf/73wb-slzy-htoh-nt6x-t1/pu9o-99t5-q82s-0jps-wt/utwn-4j2p-dtov-q5mu-uq",
            "key": "utwn-4j2p-dtov-q5mu-uq",
            "alias": "travel_details",
            "uuid": "utwn-4j2p-dtov-q5mu-uq",
            "connId": "vw9jd",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "vw9jd",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "nola-8a72-38i3-x0xu-jf/73wb-slzy-htoh-nt6x-t1/pu9o-99t5-q82s-0jps-wt",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "travel_details_vw9jd",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "travel_details"
        }
    }