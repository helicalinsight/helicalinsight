export const mockUtil = (e) => {
    if (e.includes('type=adhoc&serviceType=metadata&service=get&formData')) {
        console.log('in validating request')
        return Promise.resolve({
            "status": 1,
            "data": {
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
                "uniqueId": "e9be6771-995b-40eb-a01c-304857a100a1",
                "tables": {
                    "meeting_details": {
                        "id": "9645c648a1c0dbeec1287aaf1e996db3",
                        "alias": "meeting_details",
                        "columns": {
                            "meeting_id": {
                                "alias": "meeting_id",
                                "fullyQualifiedColumn": "meeting_details.meeting_id",
                                "columnId": "dff5938e-eb78-410c-ad57-145ed3fa4500",
                                "defaultFunction": "db.generic.aggregate.sum",
                                "type": {
                                    "java.lang.Integer": "numeric"
                                }
                            },
                            "meeting_date": {
                                "alias": "meeting_date",
                                "fullyQualifiedColumn": "meeting_details.meeting_date",
                                "columnId": "1292750c-f0f5-46b4-9524-924948d3e06b",
                                "defaultFunction": "db.generic.groupBy.group",
                                "type": {
                                    "java.sql.Timestamp": "dateTime"
                                }
                            },
                            "meeting_by": {
                                "alias": "meeting_by",
                                "fullyQualifiedColumn": "meeting_details.meeting_by",
                                "columnId": "74f064fa-1639-4555-9b37-c29658775598",
                                "defaultFunction": "db.generic.aggregate.sum",
                                "type": {
                                    "java.lang.Integer": "numeric"
                                }
                            },
                            "client_name": {
                                "alias": "client_name",
                                "fullyQualifiedColumn": "meeting_details.client_name",
                                "columnId": "5697901d-3bbc-4312-af78-74221421a90c",
                                "defaultFunction": "db.generic.groupBy.group",
                                "type": {
                                    "java.lang.String": "text"
                                }
                            },
                            "meeting_purpose": {
                                "alias": "meeting_purpose",
                                "fullyQualifiedColumn": "meeting_details.meeting_purpose",
                                "columnId": "01026ef3-56b7-4503-b966-2e0f1e776844",
                                "defaultFunction": "db.generic.groupBy.group",
                                "type": {
                                    "java.lang.String": "text"
                                }
                            },
                            "meeting_impact": {
                                "alias": "meeting_impact",
                                "fullyQualifiedColumn": "meeting_details.meeting_impact",
                                "columnId": "ccf7994c-6fec-491a-bc0d-bcb124d6f1ae",
                                "defaultFunction": "db.generic.groupBy.group",
                                "type": {
                                    "java.lang.String": "text"
                                }
                            },
                            "meet_cancellation_status": {
                                "alias": "meet_cancellation_status",
                                "fullyQualifiedColumn": "meeting_details.meet_cancellation_status",
                                "columnId": "7a5f76d7-f365-445c-a228-927c87702c39",
                                "defaultFunction": "db.generic.groupBy.group",
                                "type": {
                                    "java.lang.String": "text"
                                }
                            },
                            "cancellation_reason": {
                                "alias": "cancellation_reason",
                                "fullyQualifiedColumn": "meeting_details.cancellation_reason",
                                "columnId": "caff99b5-29f6-48eb-9f63-372cc3d7b497",
                                "defaultFunction": "db.generic.groupBy.group",
                                "type": {
                                    "java.lang.String": "text"
                                }
                            }
                        },
                        "name": "meeting_details"
                    },
                    "travel_details": {
                        "id": "8a28627d07d04ef096d9935f12e0c7e9",
                        "alias": "travel_details",
                        "columns": {
                            "travel_id": {
                                "alias": "travel_id",
                                "fullyQualifiedColumn": "travel_details.travel_id",
                                "columnId": "7c1cc79e-3203-4332-a042-345f116e50cc",
                                "defaultFunction": "db.generic.aggregate.sum",
                                "type": {
                                    "java.lang.Integer": "numeric"
                                }
                            },
                            "travel_date": {
                                "alias": "travel_date",
                                "fullyQualifiedColumn": "travel_details.travel_date",
                                "columnId": "c18188cb-89ef-4a8d-94e2-111a1f9e6b64",
                                "defaultFunction": "db.generic.groupBy.group",
                                "type": {
                                    "java.sql.Timestamp": "dateTime"
                                }
                            },
                            "travel_type": {
                                "alias": "travel_type",
                                "fullyQualifiedColumn": "travel_details.travel_type",
                                "columnId": "a7739e81-9ffc-420b-9a10-e0e6b1bb2ea2",
                                "defaultFunction": "db.generic.groupBy.group",
                                "type": {
                                    "java.lang.String": "text"
                                }
                            },
                            "travel_medium": {
                                "alias": "travel_medium",
                                "fullyQualifiedColumn": "travel_details.travel_medium",
                                "columnId": "5e238de2-4c61-4672-b5ce-a629208bb495",
                                "defaultFunction": "db.generic.groupBy.group",
                                "type": {
                                    "java.lang.String": "text"
                                }
                            },
                            "source_id": {
                                "alias": "source_id",
                                "fullyQualifiedColumn": "travel_details.source_id",
                                "columnId": "4ce15085-67f2-4038-8a1a-68597b6a3ee3",
                                "defaultFunction": "db.generic.aggregate.sum",
                                "type": {
                                    "java.lang.Integer": "numeric"
                                }
                            },
                            "source": {
                                "alias": "source",
                                "fullyQualifiedColumn": "travel_details.source",
                                "columnId": "3eda6a68-ed53-4ae2-898a-ef83f35d6b6a",
                                "defaultFunction": "db.generic.groupBy.group",
                                "type": {
                                    "java.lang.String": "text"
                                }
                            },
                            "destination_id": {
                                "alias": "destination_id",
                                "fullyQualifiedColumn": "travel_details.destination_id",
                                "columnId": "bb650022-743e-4ffd-ae21-38edbca8092f",
                                "defaultFunction": "db.generic.aggregate.sum",
                                "type": {
                                    "java.lang.Integer": "numeric"
                                }
                            },
                            "destination": {
                                "alias": "destination",
                                "fullyQualifiedColumn": "travel_details.destination",
                                "columnId": "bfdc49c3-852b-4d5f-87dd-22119ccda84d",
                                "defaultFunction": "db.generic.groupBy.group",
                                "type": {
                                    "java.lang.String": "text"
                                }
                            },
                            "travel_cost": {
                                "alias": "travel_cost",
                                "fullyQualifiedColumn": "travel_details.travel_cost",
                                "columnId": "ad0cafd9-8265-4844-97fd-e96b2aac9925",
                                "defaultFunction": "db.generic.aggregate.sum",
                                "type": {
                                    "java.lang.Integer": "numeric"
                                }
                            },
                            "mode_of_payment": {
                                "alias": "mode_of_payment",
                                "fullyQualifiedColumn": "travel_details.mode_of_payment",
                                "columnId": "c5a4fa44-8c74-4829-ada9-ffabeba2de2e",
                                "defaultFunction": "db.generic.groupBy.group",
                                "type": {
                                    "java.lang.String": "text"
                                }
                            },
                            "booking_platform": {
                                "alias": "booking_platform",
                                "fullyQualifiedColumn": "travel_details.booking_platform",
                                "columnId": "e54cfa6f-6bf0-4ac2-be12-0c88f6b58577",
                                "defaultFunction": "db.generic.groupBy.group",
                                "type": {
                                    "java.lang.String": "text"
                                }
                            },
                            "travelled_by": {
                                "alias": "travelled_by",
                                "fullyQualifiedColumn": "travel_details.travelled_by",
                                "columnId": "5b153c28-0fea-415b-93c0-baa0066e744a",
                                "defaultFunction": "db.generic.aggregate.sum",
                                "type": {
                                    "java.lang.Integer": "numeric"
                                }
                            }
                        },
                        "name": "travel_details"
                    },
                    "dimdate": {
                        "id": "4ac5d9f68b58bd7c0d179146e46795be",
                        "alias": "dimdate",
                        "columns": {
                            "dim_id": {
                                "alias": "dim_id",
                                "fullyQualifiedColumn": "dimdate.dim_id",
                                "columnId": "56692a4c-0d81-4652-96c3-525aeb723fac",
                                "defaultFunction": "db.generic.aggregate.sum",
                                "type": {
                                    "java.lang.Integer": "numeric"
                                }
                            },
                            "fiscal_year": {
                                "alias": "fiscal_year",
                                "fullyQualifiedColumn": "dimdate.fiscal_year",
                                "columnId": "e8c6b258-ea2a-4c24-b8ac-2df05ff6bf8f",
                                "defaultFunction": "db.generic.groupBy.group",
                                "type": {
                                    "java.sql.Date": "date"
                                }
                            },
                            "modified_date": {
                                "alias": "modified_date",
                                "fullyQualifiedColumn": "dimdate.modified_date",
                                "columnId": "5e4f525f-176c-4722-90e6-96dec4957e46",
                                "defaultFunction": "db.generic.groupBy.group",
                                "type": {
                                    "java.sql.Timestamp": "dateTime"
                                }
                            },
                            "date_key": {
                                "alias": "date_key",
                                "fullyQualifiedColumn": "dimdate.date_key",
                                "columnId": "ff9261dc-fcce-4b49-9d31-dbc3c5db8c9c",
                                "defaultFunction": "db.generic.groupBy.group",
                                "type": {
                                    "java.lang.String": "text"
                                }
                            },
                            "day_number": {
                                "alias": "day_number",
                                "fullyQualifiedColumn": "dimdate.day_number",
                                "columnId": "8ecc0b24-3b70-4dd1-b616-218d0955ae90",
                                "defaultFunction": "db.generic.groupBy.group",
                                "type": {
                                    "java.lang.String": "text"
                                }
                            },
                            "fiscal_month_name": {
                                "alias": "fiscal_month_name",
                                "fullyQualifiedColumn": "dimdate.fiscal_month_name",
                                "columnId": "92245d80-8743-4749-807a-cf986e66e6f5",
                                "defaultFunction": "db.generic.groupBy.group",
                                "type": {
                                    "java.lang.String": "text"
                                }
                            },
                            "fiscal_month_label": {
                                "alias": "fiscal_month_label",
                                "fullyQualifiedColumn": "dimdate.fiscal_month_label",
                                "columnId": "7bb5ed45-0632-478c-9071-9f1aca31b749",
                                "defaultFunction": "db.generic.groupBy.group",
                                "type": {
                                    "java.lang.String": "text"
                                }
                            },
                            "created_date": {
                                "alias": "created_date",
                                "fullyQualifiedColumn": "dimdate.created_date",
                                "columnId": "beec622b-7e1b-4ef5-bedc-33ae6d8b459f",
                                "defaultFunction": "db.generic.groupBy.group",
                                "type": {
                                    "java.lang.String": "text"
                                }
                            },
                            "created_time": {
                                "alias": "created_time",
                                "fullyQualifiedColumn": "dimdate.created_time",
                                "columnId": "47bd4f83-1bb8-4cfa-8b14-e7d38801c62c",
                                "defaultFunction": "db.generic.groupBy.group",
                                "type": {
                                    "java.lang.String": "text"
                                }
                            },
                            "rating": {
                                "alias": "rating",
                                "fullyQualifiedColumn": "dimdate.rating",
                                "columnId": "47d19691-1422-4fb0-a8f1-272a9f8218df",
                                "defaultFunction": "db.generic.groupBy.group",
                                "type": {
                                    "java.lang.String": "text"
                                }
                            }
                        },
                        "name": "dimdate"
                    },
                    "employee_details": {
                        "id": "4e1fd245f4d13b77be423a43f01d80b2",
                        "alias": "employee_details",
                        "columns": {
                            "employee_id": {
                                "alias": "employee_id",
                                "fullyQualifiedColumn": "employee_details.employee_id",
                                "columnId": "4c10d3ea-167a-464d-b9fd-ab0a9028ad31",
                                "defaultFunction": "db.generic.aggregate.sum",
                                "type": {
                                    "java.lang.Integer": "numeric"
                                }
                            },
                            "employee_name": {
                                "alias": "employee_name",
                                "fullyQualifiedColumn": "employee_details.employee_name",
                                "columnId": "7bf280a2-2a69-4e9a-a285-306d75012b93",
                                "defaultFunction": "db.generic.groupBy.group",
                                "type": {
                                    "java.lang.String": "text"
                                }
                            },
                            "age": {
                                "alias": "age",
                                "fullyQualifiedColumn": "employee_details.age",
                                "columnId": "b930d4ad-070a-4972-8c32-8b8398f99a7e",
                                "defaultFunction": "db.generic.aggregate.sum",
                                "type": {
                                    "java.lang.Integer": "numeric"
                                }
                            },
                            "address": {
                                "alias": "address",
                                "fullyQualifiedColumn": "employee_details.address",
                                "columnId": "4e574cad-7252-4907-923d-e42ad2b1511a",
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
                                "columnId": "1788dbff-4b08-4d99-b8cb-c9edf051d2e5",
                                "defaultFunction": "db.generic.aggregate.sum",
                                "type": {
                                    "java.lang.Integer": "numeric"
                                }
                            },
                            "location": {
                                "alias": "location",
                                "fullyQualifiedColumn": "geo_cordinates.location",
                                "columnId": "6abbc3c5-f00e-4b6f-907b-ef96a8da64bd",
                                "defaultFunction": "db.generic.groupBy.group",
                                "type": {
                                    "java.lang.String": "text"
                                }
                            },
                            "latitude": {
                                "alias": "latitude",
                                "fullyQualifiedColumn": "geo_cordinates.latitude",
                                "columnId": "88c00c69-661d-4505-a1a8-c031e85302ea",
                                "defaultFunction": "db.generic.aggregate.sum",
                                "type": {
                                    "java.lang.Double": "numeric"
                                }
                            },
                            "longitude": {
                                "alias": "longitude",
                                "fullyQualifiedColumn": "geo_cordinates.longitude",
                                "columnId": "d2776de5-5718-4b0e-ab80-8d5296a8fa60",
                                "defaultFunction": "db.generic.aggregate.sum",
                                "type": {
                                    "java.lang.Double": "numeric"
                                }
                            }
                        },
                        "name": "geo_cordinates"
                    }
                },
                "sets": [
                    [
                        "geo_cordinates",
                        "travel_details",
                        "dimdate",
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
                    }
                ],
                "metadataName": "Sample Travel MD",
                "metadataDir": "Adhoc Metadata"
            }
        })
    }
    else if (e.includes('eyJpZCI6IjEiLCJ0eXBlIjoiZHluYW1pY0RhdGFTb3VyY2UiLCJwYXJhbWV0ZXJzIjp7ImZldGNoQ2F0YWxvZ3MiOnRydWUsImZldGNoU2NoZW1hcyI6dHJ1ZSwidmlldyI6InRyZWUiLCJza2lwcGVkIjp0cnVlfX')){
        return Promise.resolve({
            "status": 1,
            "data": {
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
        })
    }
    else if (e.includes('eyJpZCI6IjEiLCJ0eXBlIjoiZHluYW1pY0RhdGFTb3VyY2UiLCJwYXJhbWV0ZXJzIjp7ImZldGNoVGFibGVzIjp0cnVlLCJmZXRjaERhdGEiOlt7InNjaGVtYXMiOlt7Im5hbWUiOiJISVVTRVIifV19XX19')){
        return Promise.resolve({
            "status": 1,
            "data": {
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
        })
    }
    else{
        console.log('in get mock util', e)
    }
}