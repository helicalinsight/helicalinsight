import { isEqual } from "lodash-es"

const tables = {
    "dimdate": {
        "id": "4ac5d9f68b58bd7c0d179146e46795be",
        "alias": "dimdate",
        "columns": {
            "dim_id": {
                "alias": "dim_id",
                "fullyQualifiedColumn": "dimdate.dim_id",
                "columnId": "90b72023-c5b7-40fa-b73b-1fff251e0dea",
                "defaultFunction": "db.generic.aggregate.sum",
                "type": {
                    "java.lang.Integer": "numeric"
                }
            },
            "fiscal_year": {
                "alias": "fiscal_year",
                "fullyQualifiedColumn": "dimdate.fiscal_year",
                "columnId": "590c67b3-bfc1-4bf0-8184-7319e68bb7bf",
                "defaultFunction": "db.generic.groupBy.group",
                "type": {
                    "java.sql.Date": "date"
                }
            },
            "modified_date": {
                "alias": "modified_date",
                "fullyQualifiedColumn": "dimdate.modified_date",
                "columnId": "7590975e-ad0c-4056-9a9c-0aaa47dc059a",
                "defaultFunction": "db.generic.groupBy.group",
                "type": {
                    "java.sql.Timestamp": "dateTime"
                }
            },
            "date_key": {
                "alias": "date_key",
                "fullyQualifiedColumn": "dimdate.date_key",
                "columnId": "a01be8d7-dfbc-41e0-b652-c5ffa2c1c798",
                "defaultFunction": "db.generic.groupBy.group",
                "type": {
                    "java.lang.String": "text"
                }
            },
            "day_number": {
                "alias": "day_number",
                "fullyQualifiedColumn": "dimdate.day_number",
                "columnId": "d90fa697-1639-4115-8f2f-a73568368fa7",
                "defaultFunction": "db.generic.groupBy.group",
                "type": {
                    "java.lang.String": "text"
                }
            },
            "fiscal_month_name": {
                "alias": "fiscal_month_name",
                "fullyQualifiedColumn": "dimdate.fiscal_month_name",
                "columnId": "0407ae4d-b8c5-43f0-885b-7951d39773d1",
                "defaultFunction": "db.generic.groupBy.group",
                "type": {
                    "java.lang.String": "text"
                }
            },
            "fiscal_month_label": {
                "alias": "fiscal_month_label",
                "fullyQualifiedColumn": "dimdate.fiscal_month_label",
                "columnId": "c2a6855b-f91b-4b69-b720-73361fce1b5a",
                "defaultFunction": "db.generic.groupBy.group",
                "type": {
                    "java.lang.String": "text"
                }
            },
            "created_date": {
                "alias": "created_date",
                "fullyQualifiedColumn": "dimdate.created_date",
                "columnId": "aafda9c2-d481-475b-bbad-9752ecedf02b",
                "defaultFunction": "db.generic.groupBy.group",
                "type": {
                    "java.lang.String": "text"
                }
            },
            "created_time": {
                "alias": "created_time",
                "fullyQualifiedColumn": "dimdate.created_time",
                "columnId": "45f91e9e-7ecb-40f9-b0f8-db540c46a8b9",
                "defaultFunction": "db.generic.groupBy.group",
                "type": {
                    "java.lang.String": "text"
                }
            },
            "rating": {
                "alias": "rating",
                "fullyQualifiedColumn": "dimdate.rating",
                "columnId": "5aeedfe5-e387-4334-a7d4-3d63594a6b97",
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
                "columnId": "e65b808e-75b2-4902-85dc-700a8f2b4732",
                "defaultFunction": "db.generic.aggregate.sum",
                "type": {
                    "java.lang.Integer": "numeric"
                }
            },
            "employee_name": {
                "alias": "employee_name",
                "fullyQualifiedColumn": "employee_details.employee_name",
                "columnId": "84ed0bb3-6fe2-4ae6-9b0f-3691b3ab0a38",
                "defaultFunction": "db.generic.groupBy.group",
                "type": {
                    "java.lang.String": "text"
                }
            },
            "age": {
                "alias": "age",
                "fullyQualifiedColumn": "employee_details.age",
                "columnId": "49de5d19-e108-4066-be37-1ccb9b570fbf",
                "defaultFunction": "db.generic.aggregate.sum",
                "type": {
                    "java.lang.Integer": "numeric"
                }
            },
            "address": {
                "alias": "address",
                "fullyQualifiedColumn": "employee_details.address",
                "columnId": "5e579880-a1c0-4659-ba9e-6d1a213c3429",
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
                "columnId": "fa5d56a7-2fb8-4c6f-a00a-3948336c65dd",
                "defaultFunction": "db.generic.aggregate.sum",
                "type": {
                    "java.lang.Integer": "numeric"
                }
            },
            "location": {
                "alias": "location",
                "fullyQualifiedColumn": "geo_cordinates.location",
                "columnId": "92097e49-e14f-44da-b055-ae1fe8a61f70",
                "defaultFunction": "db.generic.groupBy.group",
                "type": {
                    "java.lang.String": "text"
                }
            },
            "latitude": {
                "alias": "latitude",
                "fullyQualifiedColumn": "geo_cordinates.latitude",
                "columnId": "b2a8b3a4-5273-46e5-928a-075c0b7dba47",
                "defaultFunction": "db.generic.aggregate.sum",
                "type": {
                    "java.lang.Double": "numeric"
                }
            },
            "longitude": {
                "alias": "longitude",
                "fullyQualifiedColumn": "geo_cordinates.longitude",
                "columnId": "a146abad-1294-41e9-ad89-c062caeb1544",
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
                "columnId": "83e04d85-5f4b-4478-9bc8-bd72c9478773",
                "defaultFunction": "db.generic.aggregate.sum",
                "type": {
                    "java.lang.Integer": "numeric"
                }
            },
            "meeting_date": {
                "alias": "meeting_date",
                "fullyQualifiedColumn": "meeting_details.meeting_date",
                "columnId": "baa75243-8b98-4b9b-b5c4-c046df54840a",
                "defaultFunction": "db.generic.groupBy.group",
                "type": {
                    "java.sql.Timestamp": "dateTime"
                }
            },
            "meeting_by": {
                "alias": "meeting_by",
                "fullyQualifiedColumn": "meeting_details.meeting_by",
                "columnId": "1a1fec29-93b5-4c21-8898-69f0ce06df97",
                "defaultFunction": "db.generic.aggregate.sum",
                "type": {
                    "java.lang.Integer": "numeric"
                }
            },
            "client_name": {
                "alias": "client_name",
                "fullyQualifiedColumn": "meeting_details.client_name",
                "columnId": "f3fe1715-6da8-4b2d-a6fc-fd755c55cce2",
                "defaultFunction": "db.generic.groupBy.group",
                "type": {
                    "java.lang.String": "text"
                }
            },
            "meeting_purpose": {
                "alias": "meeting_purpose",
                "fullyQualifiedColumn": "meeting_details.meeting_purpose",
                "columnId": "39a382f1-0c7e-437b-acd9-da94c46314ce",
                "defaultFunction": "db.generic.groupBy.group",
                "type": {
                    "java.lang.String": "text"
                }
            },
            "meeting_impact": {
                "alias": "meeting_impact",
                "fullyQualifiedColumn": "meeting_details.meeting_impact",
                "columnId": "a26bc83c-57bc-4eb8-a481-37b00d00202e",
                "defaultFunction": "db.generic.groupBy.group",
                "type": {
                    "java.lang.String": "text"
                }
            },
            "meet_cancellation_status": {
                "alias": "meet_cancellation_status",
                "fullyQualifiedColumn": "meeting_details.meet_cancellation_status",
                "columnId": "949ccee7-83a4-4ad5-af52-8d4752e57798",
                "defaultFunction": "db.generic.groupBy.group",
                "type": {
                    "java.lang.String": "text"
                }
            },
            "cancellation_reason": {
                "alias": "cancellation_reason",
                "fullyQualifiedColumn": "meeting_details.cancellation_reason",
                "columnId": "3b86ccba-6be9-43a4-9816-e42773694d4e",
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
                "columnId": "67d61941-88ce-4db1-8079-21d8184d79f1",
                "defaultFunction": "db.generic.aggregate.sum",
                "type": {
                    "java.lang.Integer": "numeric"
                }
            },
            "travel_date": {
                "alias": "travel_date",
                "fullyQualifiedColumn": "travel_details.travel_date",
                "columnId": "26a18d05-9a3f-432b-985c-0b12da86231d",
                "defaultFunction": "db.generic.groupBy.group",
                "type": {
                    "java.sql.Timestamp": "dateTime"
                }
            },
            "travel_type": {
                "alias": "travel_type",
                "fullyQualifiedColumn": "travel_details.travel_type",
                "columnId": "b6ae3075-8bcf-47dc-9b9d-52a9820719c3",
                "defaultFunction": "db.generic.groupBy.group",
                "type": {
                    "java.lang.String": "text"
                }
            },
            "travel_medium": {
                "alias": "travel_medium",
                "fullyQualifiedColumn": "travel_details.travel_medium",
                "columnId": "96290b52-e635-4fd5-ac80-6af00b37a250",
                "defaultFunction": "db.generic.groupBy.group",
                "type": {
                    "java.lang.String": "text"
                }
            },
            "source_id": {
                "alias": "source_id",
                "fullyQualifiedColumn": "travel_details.source_id",
                "columnId": "dd3c4fac-9ae7-4203-bdf3-7ea42b8676a6",
                "defaultFunction": "db.generic.aggregate.sum",
                "type": {
                    "java.lang.Integer": "numeric"
                }
            },
            "source": {
                "alias": "source",
                "fullyQualifiedColumn": "travel_details.source",
                "columnId": "8daa3fb7-4be3-4d80-96ff-f3de6688c135",
                "defaultFunction": "db.generic.groupBy.group",
                "type": {
                    "java.lang.String": "text"
                }
            },
            "destination_id": {
                "alias": "destination_id",
                "fullyQualifiedColumn": "travel_details.destination_id",
                "columnId": "fb2ce658-56a9-4f5e-b52d-556b1058afc5",
                "defaultFunction": "db.generic.aggregate.sum",
                "type": {
                    "java.lang.Integer": "numeric"
                }
            },
            "destination": {
                "alias": "destination",
                "fullyQualifiedColumn": "travel_details.destination",
                "columnId": "f7f143ed-eaca-403d-bf99-5e5f926fe742",
                "defaultFunction": "db.generic.groupBy.group",
                "type": {
                    "java.lang.String": "text"
                }
            },
            "travel_cost": {
                "alias": "travel_cost",
                "fullyQualifiedColumn": "travel_details.travel_cost",
                "columnId": "34afdaf9-667a-4247-9a94-f84225b9f573",
                "defaultFunction": "db.generic.aggregate.sum",
                "type": {
                    "java.lang.Integer": "numeric"
                }
            },
            "mode_of_payment": {
                "alias": "mode_of_payment",
                "fullyQualifiedColumn": "travel_details.mode_of_payment",
                "columnId": "2d0ca827-372e-4349-aac4-14c53ae11e1b",
                "defaultFunction": "db.generic.groupBy.group",
                "type": {
                    "java.lang.String": "text"
                }
            },
            "booking_platform": {
                "alias": "booking_platform",
                "fullyQualifiedColumn": "travel_details.booking_platform",
                "columnId": "95859ecc-a29c-43a0-b5da-4c3e63299cbb",
                "defaultFunction": "db.generic.groupBy.group",
                "type": {
                    "java.lang.String": "text"
                }
            },
            "travelled_by": {
                "alias": "travelled_by",
                "fullyQualifiedColumn": "travel_details.travelled_by",
                "columnId": "24c1120e-92f9-417e-ac52-acd11c684330",
                "defaultFunction": "db.generic.aggregate.sum",
                "type": {
                    "java.lang.Integer": "numeric"
                }
            }
        },
        "name": "travel_details"
    }
}
export const getMetadata = ({ location, metadataFileName }) => {
    if (location === "Smoke_Testing/Sample_Metadata" && metadataFileName === "JSONMetadata.metadata") {
        return {
            "status": 1,
            "response": {
                "classifier": "db.generic",
                "name": "DRILL.dfs.hidw",
                "dataSource": {
                    "sync": false,
                    "id": "1111",
                    "connectionDatabaseId": "f79f96e9-9409-468b-a296-0f2eddc0a3a1",
                    "catSchemaPredicted": false,
                    "catalog": "DRILL",
                    "schema": "dfs.hidw",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc"
                },
                "uniqueId": "JSONMetadata",
                "tables": {
                    "primer-dataset.json": {
                        "id": "4c453732043d2df123e3656e33dbcd55",
                        "alias": "primer-dataset.json",
                        "columns": {
                            "address": {
                                "alias": "address",
                                "fullyQualifiedColumn": "primer-dataset.json.address",
                                "columnId": "7732513e-0844-4fb8-ba99-117f5175c9f7",
                                "defaultFunction": "",
                                "type": {
                                    "java.lang.Object": "other"
                                }
                            },
                            "borough": {
                                "alias": "borough",
                                "fullyQualifiedColumn": "primer-dataset.json.borough",
                                "columnId": "4ab81340-ccae-43ce-a426-04cea5c29796",
                                "defaultFunction": "db.generic.groupBy.group",
                                "type": {
                                    "java.lang.String": "text"
                                }
                            },
                            "cuisine": {
                                "alias": "cuisine",
                                "fullyQualifiedColumn": "primer-dataset.json.cuisine",
                                "columnId": "f0ae786f-9b02-476c-b2da-e3c985343ce0",
                                "defaultFunction": "db.generic.groupBy.group",
                                "type": {
                                    "java.lang.String": "text"
                                }
                            },
                            "grades": {
                                "alias": "grades",
                                "fullyQualifiedColumn": "primer-dataset.json.grades",
                                "columnId": "3781845c-4577-4911-a5fb-c78881639ff1",
                                "defaultFunction": "",
                                "type": {
                                    "java.lang.Object": "other"
                                }
                            },
                            "name": {
                                "alias": "name",
                                "fullyQualifiedColumn": "primer-dataset.json.name",
                                "columnId": "763df3f2-773e-48f3-9d87-b82ea87b1ad9",
                                "defaultFunction": "db.generic.groupBy.group",
                                "type": {
                                    "java.lang.String": "text"
                                }
                            },
                            "restaurant_id": {
                                "alias": "restaurant_id",
                                "fullyQualifiedColumn": "primer-dataset.json.restaurant_id",
                                "columnId": "2aeac768-47e1-4690-b929-a640f2c41562",
                                "defaultFunction": "db.generic.groupBy.group",
                                "type": {
                                    "java.lang.String": "text"
                                }
                            }
                        },
                        "name": "primer-dataset.json"
                    }
                },
                "sets": [
                    [
                        "primer-dataset.json"
                    ]
                ],
                "crossJoins": [],
                "metadataName": "JSONMetadata",
                "metadataDir": "Smoke_Testing/Sample_Metadata"
            }
        }
    }
    return {
        "status": 1,
        "response": {
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
            tables,
            "sets": [
                [
                    "geo_cordinates",
                    "dimdate",
                    "travel_details",
                    "employee_details",
                    "meeting_details"
                ]
            ],
            "metadataName": "Metadata_1",
            "metadataDir": "naresh"
        }
    }
}
export const getDataBaseFunctions = () => {
    return {
        "status": 1,
        "response": {
            "databaseFunctions": {
                "date": [
                    {
                        "key": "sql.date.dateadd",
                        "description": "Returns the specified date with the specified number of interval added to the specified unit of that date.Example:(date({fn timestampadd(SQL_TSI_YEAR, 5, date('2010-09-21'))})) result:2015-09-21 supported units:SQL_TSI_DAY, SQL_TSI_MONTH, SQL_TSI_YEAR.",
                        "value": "DATEADD",
                        "signature": "(date({fn timestampadd(${unit}, ${value}, date(${date}))}))",
                        "returns": "date",
                        "parameters": [
                            {
                                "name": "date",
                                "column": true,
                                "defaultValue": "'2014-03-08'"
                            },
                            {
                                "name": "value",
                                "column": true,
                                "defaultValue": "2"
                            },
                            {
                                "name": "unit",
                                "column": true,
                                "defaultValue": "SQL_TSI_YEAR"
                            }
                        ]
                    },
                    {
                        "key": "sql.date.today",
                        "description": "Displays Current date.",
                        "value": "TODAY",
                        "signature": "(CURRENT_DATE)",
                        "returns": "date",
                        "parameters": []
                    },
                    {
                        "key": "sql.date.makedate",
                        "description": "Returns a date for given year, month and day. Example: date(char('2019',4)||'-'||char('11',2)||'-'||char('23',2)) result : 2019-17-23",
                        "value": "MAKEDATE",
                        "signature": "date(${year}||'-'||${month}||'-'||${day})",
                        "returns": "date",
                        "parameters": [
                            {
                                "name": "year",
                                "defaultValue": "'2013'"
                            },
                            {
                                "name": "month",
                                "defaultValue": "'7'"
                            },
                            {
                                "name": "day",
                                "defaultValue": "'15'"
                            }
                        ]
                    },
                    {
                        "key": "sql.date.datediff",
                        "description": "Returns the difference between date1 and date2 expressed in terms of unit. Example: {fn timestampdiff(SQL_TSI_YEAR, date('2018-03-08'), date('2022-03-08'))} result: 4 supported units:SQL_TSI_YEAR,SQL_TSI_MONTH,SQL_TSI_DAY.",
                        "value": "DATEDIFF",
                        "signature": "({fn timestampdiff(${unit}, date(${date1}), date(${date2}))})",
                        "returns": "numeric",
                        "parameters": [
                            {
                                "name": "unit",
                                "column": true,
                                "defaultValue": "SQL_TSI_YEAR"
                            },
                            {
                                "name": "date1",
                                "column": true,
                                "defaultValue": "'2014-03-08'"
                            },
                            {
                                "name": "date2",
                                "column": true,
                                "defaultValue": "'2019-03-08'"
                            }
                        ]
                    }
                ],
                "dateTime": [
                    {
                        "key": "sql.dateTime.hour",
                        "description": "Return hour for timestamp or a valid timestamp string. Example: hour('2014-03-08 12:20:19') result:12",
                        "value": "HOUR",
                        "signature": "hour(${datetime})",
                        "returns": "numeric",
                        "parameters": [
                            {
                                "name": "datetime",
                                "column": true,
                                "defaultValue": "'2014-03-08 12:20:19'"
                            }
                        ]
                    },
                    {
                        "key": "sql.dateTime.maketime",
                        "description": "Returns time value from the hour, minute and seconds.Example:time('11'||':'||'25'||':'||'30') Result:11:25:30\n\t ",
                        "value": "MAKETIME",
                        "signature": "time(${hour}||':'||${minute}||':'||${second})",
                        "returns": "time",
                        "parameters": [
                            {
                                "name": "hour",
                                "column": true,
                                "defaultValue": "'12'"
                            },
                            {
                                "name": "minute",
                                "column": true,
                                "defaultValue": "'30'"
                            },
                            {
                                "name": "second",
                                "column": true,
                                "defaultValue": "'40'"
                            }
                        ]
                    },
                    {
                        "key": "sql.dateTime.makedatetime",
                        "description": "Returns a datetime that combines a year,month,day,hour,minute,second. Example: timestamp('2019'||'-'||'11'||'-'||'22')||' '||'10'||':'||'25'||':'||'22.3') result: 2019-11-22 10:25:22.300000.",
                        "value": "MAKEDATETIME",
                        "signature": "timestamp(${year}||'-'||${month}||'-'||${day}||' '||${hour}||':'||${minute}||':'||${second})",
                        "returns": "dateTime",
                        "parameters": [
                            {
                                "name": "year",
                                "column": true,
                                "defaultValue": "'2013'"
                            },
                            {
                                "name": "month",
                                "column": true,
                                "defaultValue": "'7'"
                            },
                            {
                                "name": "day",
                                "column": true,
                                "defaultValue": "'15'"
                            },
                            {
                                "name": "hour",
                                "column": true,
                                "defaultValue": "'8'"
                            },
                            {
                                "name": "minute",
                                "column": true,
                                "defaultValue": "'15'"
                            },
                            {
                                "name": "second",
                                "column": true,
                                "defaultValue": "'23.5'"
                            }
                        ]
                    },
                    {
                        "key": "sql.dateTime.datetimediff",
                        "description": "Returns the difference between timestamp1 and timestamp2 expressed in terms of unit. Example:{fn timestampdiff(SQL_TSI_YEAR, timestamp( '2018-03-08 11:10:27'), timestamp('2022-03-08 11:10:27'))} result: 4. supported units:SQL_TSI_YEAR,SQL_TSI_MONTH,SQL_TSI_DAY,SQL_TSI_HOUR,SQL_TSI_MINUTE,SQL_TSI_SECOND.",
                        "value": "DATETIMEDIFF",
                        "signature": "{fn timestampdiff(${unit}, timestamp(${datetime1}), timestamp(${datetime2}))}",
                        "returns": "numeric",
                        "parameters": [
                            {
                                "name": "unit",
                                "column": true,
                                "defaultValue": "SQL_TSI_YEAR"
                            },
                            {
                                "name": "datetime1",
                                "column": true,
                                "defaultValue": "'2014-03-08 09:11:20'"
                            },
                            {
                                "name": "datetime2",
                                "column": true,
                                "defaultValue": "'2019-03-08 09:08:40'"
                            }
                        ]
                    },
                    {
                        "key": "sql.dateTime.datetimeadd",
                        "description": " Returns the specified datetime with the specified number interval added to the date_part of that datetime. Example:  {fn timestampadd(SQL_TSI_YEAR, 1, timestamp('2010-09-21 10:21:11'))} result:2011-09-21 10:21:11.000000.supported units:SQL_TSI_YEAR,SQL_TSI_MONTH,SQL_TSI_DAY,SQL_TSI_HOUR,SQL_TSI_MINUTE,SQL_TSI_SECOND.",
                        "value": "DATETIMEADD",
                        "signature": "{fn timestampadd(${unit}, ${value}, timestamp(${datetime}))}",
                        "returns": "dateTime",
                        "parameters": [
                            {
                                "name": "datetime",
                                "column": true,
                                "defaultValue": "'2014-03-08 11:10:27'"
                            },
                            {
                                "name": "value",
                                "column": true,
                                "defaultValue": "2"
                            },
                            {
                                "name": "unit",
                                "column": true,
                                "defaultValue": "SQL_TSI_YEAR"
                            }
                        ]
                    },
                    {
                        "key": "sql.dateTime.month",
                        "description": "Returns the month of the year for date/datetime. Example: month('2007-02-03 09:00:00')/month('2007-02-03') result:2",
                        "value": "MONTH",
                        "signature": "month(${datetime})",
                        "returns": "numeric",
                        "parameters": [
                            {
                                "name": "datetime",
                                "column": true,
                                "defaultValue": "'2007-02-03 09:12:30'"
                            }
                        ]
                    },
                    {
                        "key": "sql.dateTime.minute",
                        "description": "Returns minute for timestamp or a valid timestamp string. Example: minute('2014-03-08 12:20:19') result: 20",
                        "value": "MINUTE",
                        "signature": "minute(${datetime})",
                        "returns": "numeric",
                        "parameters": [
                            {
                                "name": "datetime",
                                "column": true,
                                "defaultValue": "'2007-02-03 09:12:30'"
                            }
                        ]
                    },
                    {
                        "key": "sql.dateTime.second",
                        "description": "Returns the seconds for timestamp or a valid timestamp string. Example:second('2014-03-08 12:20:19'). result: 19. NOTE:If the argument is a timestamp: The result will contains fraction of seconds along with second.",
                        "value": "SECOND",
                        "signature": "second(${datetime})",
                        "returns": "numeric",
                        "parameters": [
                            {
                                "name": "datetime",
                                "column": true,
                                "defaultValue": "'2007-02-03 09:12:30'"
                            }
                        ]
                    },
                    {
                        "key": "sql.dateTime.quarter",
                        "description": "Returns the quarter of the year for date/datetime. Example: quarter('2014-03-08 12:20:19') result:1",
                        "value": "QUARTER",
                        "signature": "(CASE MONTH(${datetime}) WHEN < 4 THEN 1 WHEN BETWEEN 4 AND 6 then 2 WHEN BETWEEN 7 AND 9 then 3 WHEN BETWEEN 10 AND 12 then 4 END)",
                        "returns": "numeric",
                        "parameters": [
                            {
                                "name": "datetime",
                                "column": true,
                                "defaultValue": "'2014-03-08 12:20:19'"
                            }
                        ]
                    },
                    {
                        "key": "sql.dateTime.day",
                        "description": "Returns day of the month for date/datetime. Example: day('2014-03-08 09:00:00')/day('2014-03-08') result: 3",
                        "value": "DAY",
                        "signature": "day(${datetime})",
                        "returns": "numeric",
                        "parameters": [
                            {
                                "name": "datetime",
                                "column": true,
                                "defaultValue": "'2007-02-03 09:00:00'"
                            }
                        ]
                    },
                    {
                        "key": "sql.dateTime.monthname",
                        "description": "Returns the month name based on the given date/datetime. Example: monthname('2014-08-08 08:00:00.000') result: August ",
                        "value": "MONTHNAME",
                        "signature": "(case when (month(${datetime})=01 OR month(${datetime})=1) then 'January' \n\t     when (month(${datetime})=02 OR month(${datetime})=2) then 'February'\n\t     when (month(${datetime})=03 OR month(${datetime})=3) then 'March'\n\t     when (month(${datetime})=04 OR month(${datetime})=4) then 'April'\n\t     when (month(${datetime})=05 OR month(${datetime})=5) then 'May'\n\t     when (month(${datetime})=06 OR month(${datetime})=6) then 'June'\n\t     when (month(${datetime})=07 OR month(${datetime})=7) then 'July'\n\t     when (month(${datetime})=08 OR month(${datetime})=8) then 'August'\n\t     when (month(${datetime})=09 OR month(${datetime})=9) then 'September'\n\t     when (month(${datetime})=10) then 'October'\n\t     when (month(${datetime})=11) then 'November'\n\t     when (month(${datetime})=12) then 'December'\n\t     else null end)",
                        "returns": "text",
                        "parameters": [
                            {
                                "name": "datetime",
                                "column": true,
                                "defaultValue": "'2014-08-08 08:00:00.000'"
                            }
                        ]
                    },
                    {
                        "key": "sql.dateTime.now",
                        "description": "Displays Current date and time. This function equivalent to current_timestamp.",
                        "value": "NOW",
                        "signature": "(CURRENT_TIMESTAMP)",
                        "returns": "dateTime",
                        "parameters": []
                    },
                    {
                        "key": "sql.dateTime.year",
                        "description": "Return year for date/dateTime. Example: year('2014-03-08 09:00:00')/year('2014-03-08') result: 2014",
                        "value": "YEAR",
                        "signature": "year(${datetime})",
                        "returns": "numeric",
                        "parameters": [
                            {
                                "name": "datetime",
                                "column": true,
                                "defaultValue": "'2007-02-03 09:00:00'"
                            }
                        ]
                    }
                ],
                "type conversion": [
                    {
                        "key": "sql.typeConversion.cast",
                        "description": "Cast function converts one dataType to another datatype. Note:All Values should be in single quotes if user provide's value.Example: CAST('2019-03-22 17:34:03.000' AS varchar(23)) result:2019-03-22 17:34:03.0",
                        "value": "CAST",
                        "signature": "CAST(${column} AS ${dataType})",
                        "returns": "text",
                        "parameters": [
                            {
                                "name": "column",
                                "column": true
                            },
                            {
                                "name": "dataType",
                                "column": false
                            }
                        ]
                    },
                    {
                        "key": "sql.typeConversion.tochar",
                        "description": "Converts value to char type. NOTE:field should be in single quotes if you are typing manually.Example1:char(date('2019-11-22'))result:2019-11-22 Example2:char(12345) result:'12345'",
                        "value": "TOCHAR",
                        "signature": "CHAR(${column})",
                        "returns": "text",
                        "parameters": [
                            {
                                "name": "column",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.typeConversion.tonum",
                        "description": "This function is used to convert character based integer value to integer type.(format is not required)Example:BIGINT('456') result:456",
                        "value": "TONUM",
                        "signature": "BIGINT(${column})",
                        "returns": "numeric",
                        "parameters": [
                            {
                                "name": "column",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.typeConversion.todecimal",
                        "description": "This function is used to convert character based decimal value to decimal type.(format is not required)Example:DOUBLE('456.34') result:456.34",
                        "value": "TODECIMAL",
                        "signature": "DOUBLE(${column})",
                        "returns": "numeric",
                        "parameters": [
                            {
                                "name": "column",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.typeConversion.todate",
                        "description": "This function is used to convert character based date value to date type.(format is not required)Example:CAST('2018-08-30' as DATE)) result:2018-08-30",
                        "value": "TODATE",
                        "signature": "CAST(${column} AS DATE)",
                        "returns": "date",
                        "parameters": [
                            {
                                "name": "column",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.typeConversion.todatetime",
                        "description": "This function is used to convert character based dateTime value to dateTime type.(format is not required)Example:CAST('2018-08-30 10:15:30' as TIMESTAMP)) result:2018-08-30 10:15:30",
                        "value": "TODATETIME",
                        "signature": "CAST(${column} AS TIMESTAMP)",
                        "returns": "dateTime",
                        "parameters": [
                            {
                                "name": "column",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.typeConversion.totime",
                        "description": "This function is used to convert character based time value to time type.(format is not required) Example:CAST('10:15:30' as TIME)) result:10:15:30",
                        "value": "TOTIME",
                        "signature": "CAST(${column} AS TIME)",
                        "returns": "time",
                        "parameters": [
                            {
                                "name": "column",
                                "column": true
                            }
                        ]
                    }
                ],
                "derby specific": [
                    {
                        "key": "sql.text.dateToString",
                        "description": "Converts the date to string",
                        "value": "dateToString",
                        "signature": "CAST (${column} AS VARCHAR(100))",
                        "returns": "text",
                        "parameters": [
                            {
                                "name": "column",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.text.numericToString",
                        "description": "Converts the time to string",
                        "value": "numericToString",
                        "signature": "CAST (${column} AS CHAR(100))",
                        "returns": "text",
                        "parameters": [
                            {
                                "name": "column",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.text.dateTimeToString",
                        "description": "Converts the datetime to string",
                        "value": "dateTimeToString",
                        "signature": "CAST (${column} AS VARCHAR(100))",
                        "returns": "text",
                        "parameters": [
                            {
                                "name": "column",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.text.timeToString",
                        "description": "Converts the time to string",
                        "value": "timeToString",
                        "signature": "CAST (${column} AS VARCHAR(100))",
                        "returns": "text",
                        "parameters": [
                            {
                                "name": "column",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.text.date",
                        "description": "Extracts the date from the date and time value",
                        "value": "date",
                        "signature": "DATE(${column})",
                        "returns": "date",
                        "parameters": [
                            {
                                "name": "column",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.date.monthyear",
                        "description": "Displays month and year in (month-year) format",
                        "value": "month-year",
                        "signature": "CAST(month(${column}) AS CHAR(20) )||  '-'  ||CAST(YEAR(${column}) AS CHAR(20) )",
                        "returns": "text",
                        "parameters": [
                            {
                                "name": "column",
                                "column": true,
                                "defaultValue": "0"
                            }
                        ]
                    },
                    {
                        "key": "sql.dateTime.currenttime",
                        "description": "The CURRENT_TIME function returns the current time.",
                        "value": "currenttime",
                        "signature": "(VALUES CURRENT_TIME)",
                        "returns": "time",
                        "parameters": []
                    }
                ],
                "numeric": [
                    {
                        "key": "sql.numeric.abs",
                        "description": "Returns the absolute value of a number. Example:abs(-24) result:24",
                        "value": "ABS",
                        "signature": "abs(${number})",
                        "returns": "numeric",
                        "parameters": [
                            {
                                "name": "number",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.numeric.acos",
                        "description": "Returns the arc cosine of number. Example: acos(0.25) result: 1.318116071652818 ",
                        "value": "ACOS",
                        "signature": "acos(${number})",
                        "returns": "numeric",
                        "parameters": [
                            {
                                "name": "number",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.numeric.asin",
                        "description": "Returns the arc sine of number. Example: asin(0.25) result: 0.25268025514207865 ",
                        "value": "ASIN",
                        "signature": "asin(${number})",
                        "returns": "numeric",
                        "parameters": [
                            {
                                "name": "number",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.numeric.atan",
                        "description": "Returns the arc tangent of number. Example: atan(0.25) result: 0.24497866312686414 ",
                        "value": "ATAN",
                        "signature": "atan(${number})",
                        "returns": "numeric",
                        "parameters": [
                            {
                                "name": "number",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.numeric.atan2",
                        "description": "Returns the arc tangent of given number. Example: atan2(0.50,1) result: 0.4636476090008061",
                        "value": "ATAN2",
                        "signature": "atan2(${number1},${number2})",
                        "returns": "numeric",
                        "parameters": [
                            {
                                "name": "number1",
                                "column": true
                            },
                            {
                                "name": "number2",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.numeric.floor",
                        "description": "Returns number rounded down to the nearest number. Example:floor(3.1415) result:3",
                        "value": "FLOOR",
                        "signature": "floor(${number})",
                        "returns": "numeric",
                        "parameters": [
                            {
                                "name": "number",
                                "column": true,
                                "defaultValue": "0"
                            }
                        ]
                    },
                    {
                        "key": "sql.numeric.ceiling",
                        "description": "Returns number rounded up to the nearest integer. Example:ceiling(0.25) result:1",
                        "value": "CEILING",
                        "signature": "CEILING(${number})",
                        "returns": "numeric",
                        "parameters": [
                            {
                                "name": "number",
                                "column": true,
                                "defaultValue": "0"
                            }
                        ]
                    },
                    {
                        "key": "sql.numeric.cos",
                        "description": "Returns the cosine of number. Example: cos(0.25) result: 0.9689124217106447 ",
                        "value": "COS",
                        "signature": "cos(${number})",
                        "returns": "numeric",
                        "parameters": [
                            {
                                "name": "number",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.numeric.cot",
                        "description": "Returns the cotangent of an angle. Specify the angle in radians. Example: 1/tan(0.25) result: 3.9163173646459399 ",
                        "value": "COT",
                        "signature": "1/tan(${number})",
                        "returns": "numeric",
                        "parameters": [
                            {
                                "name": "number",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.numeric.degrees",
                        "description": "Converts angle number in radians to degrees. Example: degrees(0.25) result: 14.32394487827058",
                        "value": "DEGREES",
                        "signature": "degrees(${number})",
                        "returns": "numeric",
                        "parameters": [
                            {
                                "name": "number",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.numeric.div",
                        "description": "Returns the integer part of a division operation. Example: (10/5) result: 2",
                        "value": "DIV",
                        "signature": "(${dividend} / NULLIF(${divisor}, 0))",
                        "returns": "numeric",
                        "parameters": [
                            {
                                "name": "dividend",
                                "column": true
                            },
                            {
                                "name": "divisor",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.numeric.exp",
                        "description": "Returns Euler’s number raised to the power of the given number. Example: exp(2) result: 7.389",
                        "value": "EXP",
                        "signature": "exp(${number})",
                        "returns": "numeric",
                        "parameters": [
                            {
                                "name": "number",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.numeric.ln",
                        "description": "Returns the natural logarithm of number. Example: ln(2) result: 0.6931471805599453 ",
                        "value": "LN",
                        "signature": "ln(${number})",
                        "returns": "numeric",
                        "parameters": [
                            {
                                "name": "number",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.numeric.log",
                        "description": "Returns the base 10 logarithm of number. Example: log(2) result: 0.3010299956639812 ",
                        "value": "LOG",
                        "signature": "log(${number})",
                        "returns": "numeric",
                        "parameters": [
                            {
                                "name": "number",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.numeric.pi",
                        "description": "Returns the constant Pi. Example: pi() result: 3.14159 ",
                        "value": "PI",
                        "signature": "pi()",
                        "returns": "numeric",
                        "parameters": []
                    },
                    {
                        "key": "sql.numeric.radians",
                        "description": "Converts given number from degrees to radians. Example : radians(4) result : 0.06981317007977318 ",
                        "value": "RADIANS",
                        "signature": "radians(${number})",
                        "returns": "numeric",
                        "parameters": [
                            {
                                "name": "number",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.numeric.sign",
                        "description": "Returns the signum function of number, that is:\n\t\t0 if the argument is 0,\n\t\t1 if the argument is greater than 0,\n\t\t-1 if the argument is less than 0. Example: sign(0.5) result: 1.",
                        "value": "SIGN",
                        "signature": "sign(${number})",
                        "returns": "numeric",
                        "parameters": [
                            {
                                "name": "number",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.numeric.sin",
                        "description": "Returns the sine of number. Example: sin(0.25) result: 0.24740395925452294",
                        "value": "SIN",
                        "signature": "sin(${number})",
                        "returns": "numeric",
                        "parameters": [
                            {
                                "name": "number",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.numeric.sqrt",
                        "description": "It displays the square root of a positive number. Example: sqrt(5) result: 2.23606797749979",
                        "value": "SQRT",
                        "signature": "sqrt(${number})",
                        "returns": "numeric",
                        "parameters": [
                            {
                                "name": "number",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.numeric.square",
                        "description": "It displays the square of a given number. Example: square(4) result: 16",
                        "value": "SQUARE",
                        "signature": "(${number} * ${number})",
                        "returns": "numeric",
                        "parameters": [
                            {
                                "name": "number",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.numeric.tan",
                        "description": "Returns the tangent of number. Example: tan(0.25) result: 0.25534192122103627 ",
                        "value": "TAN",
                        "signature": "tan(${number})",
                        "returns": "numeric",
                        "parameters": [
                            {
                                "name": "number",
                                "column": true
                            }
                        ]
                    }
                ],
                "text": [
                    {
                        "key": "sql.text.concat",
                        "description": "Returns the concatenation of string1, string2. Example:('Beng'||'aluru') result: Bengaluru ",
                        "value": "CONCAT",
                        "signature": "(${string1}||${string2})",
                        "returns": "text",
                        "parameters": [
                            {
                                "name": "string1",
                                "column": true,
                                "defaultValue": "'Beng'"
                            },
                            {
                                "name": "string2",
                                "column": true,
                                "defaultValue": "'aluru'"
                            }
                        ]
                    },
                    {
                        "key": "sql.text.contains",
                        "description": "Returns true if the given string contains the specified substring. Example: case when(locate('g', 'Bengaluru'))>0 then true else false end result: true ",
                        "value": "CONTAINS",
                        "signature": "(case when(locate(${substring}, ${string}))>0 then true else false end)",
                        "returns": "boolean",
                        "parameters": [
                            {
                                "name": "substring",
                                "column": true
                            },
                            {
                                "name": "string",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.text.endswith",
                        "description": "Returns true if the given string endswith specified substring. Example:case when('postgres' like ('%'||'res')) then true else false end result: true. Note:Please provide single quotes if you are directly typing the substring value.",
                        "value": "ENDSWITH",
                        "signature": "case when(${string} like ('%'||${substring})) then true else false end",
                        "returns": "boolean",
                        "parameters": [
                            {
                                "name": "string",
                                "column": true
                            },
                            {
                                "name": "substring",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.text.startswith",
                        "description": "Returns true if string starts with substring. Example: case when('bengaluru' like ('ben'||'%')) then true else false end result: true.  Note:Please provide single quotes if you are directly typing the substring value.",
                        "value": "STARTSWITH",
                        "signature": "case when(${string} like (${substring}||'%')) then true else false end",
                        "returns": "boolean",
                        "parameters": [
                            {
                                "name": "string",
                                "column": true
                            },
                            {
                                "name": "substring",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.text.find",
                        "description": "Returns the starting position of the first instance of substring in string. Positions start with 1. If not found, 0 is returned. Example :locate('z' in 'Bengaluru') result : 0, locate('aluru' in 'Bengaluru') result : 5",
                        "value": "FIND",
                        "signature": "locate(${substring},${string})",
                        "returns": "numeric",
                        "parameters": [
                            {
                                "name": "substring",
                                "column": true
                            },
                            {
                                "name": "string",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.text.left",
                        "description": "Returns the left most (length) character from the string . Example: substr('bengaluru',1, 4) result: beng",
                        "value": "LEFT",
                        "signature": "trim(substr(${string},1, ${length}))",
                        "returns": "text",
                        "parameters": [
                            {
                                "name": "string",
                                "column": true
                            },
                            {
                                "name": "length",
                                "column": true,
                                "defaultValue": "0"
                            }
                        ]
                    },
                    {
                        "key": "sql.text.length",
                        "description": "Returns the number of characters in text. Example: length('Bengaluru') result:9",
                        "value": "LENGTH",
                        "signature": "length(${string})",
                        "returns": "numeric",
                        "parameters": [
                            {
                                "name": "string",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.text.lower",
                        "description": "Converts all characters in the specified string to lowercase. Example: LOWER('BENGALURU') result: bengaluru ",
                        "value": "LOWER",
                        "signature": "lower(${string})",
                        "returns": "text",
                        "parameters": [
                            {
                                "name": "string",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.text.upper",
                        "description": "Converts all characters in a string to uppercase. Example: UPPER('bengaluru') result: BENGALURU",
                        "value": "UPPER",
                        "signature": "upper(${string})",
                        "returns": "text",
                        "parameters": [
                            {
                                "name": "string",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.text.right",
                        "description": "Returns the rightmost character from the string. If length is negative extract all the characters from the right side except 3 leftmost characters Example: (case when 4 > length('bengaluru') then 'bengaluru' else substr('bengaluru',length('bengaluru')-(4-1),4) end) result: 'luru'.NOTE:if the provided length is grater than the length of the string then the whole string will be returned.",
                        "value": "RIGHT",
                        "signature": "(case when ${length} > length(${string}) then ${string} else trim(substr(${string},length(${string})-(${length}-1),${length})) end)",
                        "returns": "text",
                        "parameters": [
                            {
                                "name": "string",
                                "column": true
                            },
                            {
                                "name": "length",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.text.ltrim",
                        "description": " Removes leading whitespace from string Example: LTRIM(' Bengaluru') result: Bengaluru\n        ",
                        "value": "LTRIM",
                        "signature": "ltrim(${string})",
                        "returns": "text",
                        "parameters": [
                            {
                                "name": "string",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.text.mid",
                        "description": "Returns the text starting from specified position. If position is more than string or length is less than 1 it will return empty string. Example: substr('bengaluru',2,5); result: engal",
                        "value": "MID",
                        "signature": "trim(substr(${string},${position},${length}))",
                        "returns": "text",
                        "parameters": [
                            {
                                "name": "string",
                                "column": true
                            },
                            {
                                "name": "position",
                                "column": true
                            },
                            {
                                "name": "length",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.text.rtrim",
                        "description": "Removes trailing whitespace from string. Example: RTRIM('Bengaluru  ') Result: Bengaluru ",
                        "value": "RTRIM",
                        "signature": "rtrim(${string})",
                        "returns": "text",
                        "parameters": [
                            {
                                "name": "string",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.text.trim",
                        "description": "Removes whitespace from string. Example:TRIM(' Bengaluru ') result: Bengaluru\n        ",
                        "value": "TRIM",
                        "signature": "trim(${string})",
                        "returns": "text",
                        "parameters": [
                            {
                                "name": "string",
                                "column": true
                            }
                        ]
                    }
                ],
                "logical": [
                    {
                        "key": "sql.logical.and",
                        "description": "Inside IF we will use AND. performs a logical conjunction on two expressions.\n            In 'column' paramter we will 'drag column'.\n\t\t\tIn 'condition' parameter provide conditions like (>,= etc.,like).\n\t\t\tIn 'value' parameter provide condition value.\n\t\t\tIn 'moreconditions' parameter we will use nested 'OR' condition , 'AND' conditions. \n\t\t\tExample: CASE WHEN 'Washington' like '%sh%' \n             AND 'Washington' like 'W%' THEN 'returnl washington' \n             else 'NotMatched' end  ",
                        "value": "AND",
                        "signature": "AND (${column} ${condition} ${value}) ${moreconditions} ",
                        "returns": "text",
                        "parameters": [
                            {
                                "name": "column",
                                "column": true
                            },
                            {
                                "name": "condition",
                                "defaultValue": ""
                            },
                            {
                                "name": "value",
                                "defaultValue": ""
                            },
                            {
                                "name": "moreconditions",
                                "column": true,
                                "defaultValue": ""
                            }
                        ]
                    },
                    {
                        "key": "sql.logical.case",
                        "description": "Inside case we will use when condition. Evaluates each condition from left to right and returns the result when the first condition met. If no condition met return from else if exist, otherwise return null. Example : CASE WHEN Quantity > 30 THEN 'The quantity is greater than 30'  ELSE 'The quantity is under 30' END ",
                        "value": "CASE",
                        "signature": "(CASE ${condition} END)",
                        "returns": "text",
                        "parameters": [
                            {
                                "name": "condition",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.logical.else",
                        "description": "Returns from statement_list when condition gets fail.We will use ELSE inside case function. Example: CASE when 50 > 0 then 'true' else 'false' end",
                        "value": "ELSE",
                        "signature": "ELSE ${statement_list}",
                        "returns": "text",
                        "parameters": [
                            {
                                "name": "statement_list",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.logical.elseif",
                        "description": "Evaluates conditions and returns 'conditiontrue' if condition is true, otherwise return 'conditionfalse'.  We will use nested condition inside else 'conditiontrue' parameter. Example:case when creditlim > 50000 then 'PLATINUM' when (creditlim > = 50000) then 'GOLD' else 'SILVER' end ",
                        "value": "ELSEIF",
                        "signature": "(case when ${column} ${condition} ${value} then ${conditiontrue} when ${elseIfcolumn} ${elseIfcondition} ${elseIfvalue} then ${elseIfconditiontrue} else ${conditionfalse} ${moreconditions} end)",
                        "returns": "text",
                        "parameters": [
                            {
                                "name": "column",
                                "column": true
                            },
                            {
                                "name": "condition",
                                "defaultValue": ""
                            },
                            {
                                "name": "value",
                                "defaultValue": ""
                            },
                            {
                                "name": "conditiontrue",
                                "column": true
                            },
                            {
                                "name": "elseIfcolumn",
                                "column": true
                            },
                            {
                                "name": "elseIfcondition",
                                "defaultValue": ""
                            },
                            {
                                "name": "elseIfvalue",
                                "defaultValue": ""
                            },
                            {
                                "name": "elseIfconditiontrue",
                                "column": true
                            },
                            {
                                "name": "conditionfalse",
                                "column": true,
                                "defaultValue": ""
                            },
                            {
                                "name": "moreconditions",
                                "column": true,
                                "defaultValue": ""
                            }
                        ]
                    },
                    {
                        "key": "sql.logical.if",
                        "description": "Inside IF we will use AND, OR conditions. Evaluates and returns 'conditiontrue' if condition is true, otherwise return 'conditionfalse'. \n\t\t\tIn 'column' paramter we will drag column.\n\t\t\tIn 'condition' parameter provide conditions like (>,= etc.,).\n\t\t\tIn 'value' parameter provide value.\n\t\t\tIn 'moreconditions' parameter we will use nested 'OR', 'AND' conditions. Instead of dragging column directly we will write expression in column parameter like 50 > 0 (Note : In such case don't provide anything in 'condition' parameter and 'value' parameter). Example : case when (creditlim > 50000) then 'PLATINUM' else 'SILVER' end",
                        "value": "IF",
                        "signature": "(case when ${column} ${condition} ${value} ${moreconditions} then ${conditiontrue} else ${conditionfalse} end)",
                        "returns": "text",
                        "parameters": [
                            {
                                "name": "column",
                                "column": true
                            },
                            {
                                "name": "condition",
                                "defaultValue": ""
                            },
                            {
                                "name": "value",
                                "defaultValue": ""
                            },
                            {
                                "name": "moreconditions",
                                "column": true,
                                "defaultValue": ""
                            },
                            {
                                "name": "conditiontrue",
                                "column": true,
                                "defaultValue": ""
                            },
                            {
                                "name": "conditionfalse",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.logical.ifnull",
                        "description": "Returns Expr1 if it is not null otherwise return expr2. Example : coalesce(profit, 0).NOTE:Manually entered null will not work it should be part of column, datatype of both the expressions should be match.",
                        "value": "IFNULL",
                        "signature": "(coalesce(${expr1}, ${expr2}))",
                        "returns": "text",
                        "parameters": [
                            {
                                "name": "expr1",
                                "column": true
                            },
                            {
                                "name": "expr2",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.logical.iif",
                        "description": "Evaluates and returns 'conditiontrue' if condition is true, otherwise return 'conditionfalse'. Example : case when 'washington'like 'W%' then 'true' else 'false' end",
                        "value": "IIF",
                        "signature": "(case when ${column} ${condition} ${value} then ${conditiontrue} else ${conditionfalse} end)",
                        "returns": "text",
                        "parameters": [
                            {
                                "name": "column",
                                "column": true
                            },
                            {
                                "name": "condition",
                                "defaultValue": ""
                            },
                            {
                                "name": "value",
                                "defaultValue": ""
                            },
                            {
                                "name": "conditiontrue",
                                "column": true
                            },
                            {
                                "name": "conditionfalse",
                                "column": true
                            }
                        ]
                    },
                    {
                        "key": "sql.logical.isnull",
                        "description": "Inside WHEN condition we will use ISNULL. Evalutes and returns 'Conditiontrue' if the expression contain Null. Example1 : CASE WHEN 1 ISNULL THEN Conditionfalse. Example2 : CASE WHEN NULL ISNULL THEN Conditiontrue. NOTE: Manually entered null will not work it should be part of column.",
                        "value": "ISNULL",
                        "signature": "IS NULL",
                        "returns": "boolean",
                        "parameters": []
                    },
                    {
                        "key": "sql.logical.not",
                        "description": "Evaluates and returns 'conditiontrue' if condition is false, otherwise returns 'conditionfalse'. We will use NOT inside IF. Example :  NOT(500 > 1000) result :true",
                        "value": "NOT",
                        "signature": "(NOT(${column} ${condition} ${value}))",
                        "returns": "boolean",
                        "parameters": [
                            {
                                "name": "column",
                                "column": true
                            },
                            {
                                "name": "condition",
                                "column": true,
                                "defaultValue": ""
                            },
                            {
                                "name": "value",
                                "column": true,
                                "defaultValue": ""
                            }
                        ]
                    },
                    {
                        "key": "sql.logical.or",
                        "description": "Inside IF we will use OR.Performs a logical disjunction on two expressions. \n\t\t\tIn 'column' paramter we will drag column.\n\t\t\tIn 'condition' parameter provide conditions like (>,= etc.,).\n\t\t\tIn 'value' parameter provide value.\n\t\t\tIn 'moreconditions' parameter we will use nested 'OR' , 'AND' functions. Example : CASE WHEN 'Washington' like '%sh%' \n             OR  'Washington' like 'W%' THEN 'return washington' \n             else 'NotMatched' end",
                        "value": "OR",
                        "signature": " OR ${column} ${condition} ${value} ${moreconditions}",
                        "returns": "numeric",
                        "parameters": [
                            {
                                "name": "column",
                                "column": true
                            },
                            {
                                "name": "condition",
                                "column": true,
                                "defaultValue": ""
                            },
                            {
                                "name": "value",
                                "column": true,
                                "defaultValue": ""
                            },
                            {
                                "name": "moreconditions",
                                "column": true,
                                "defaultValue": ""
                            }
                        ]
                    },
                    {
                        "key": "sql.logical.when",
                        "description": "Returns 'statement_list' when condition get satisfied .\n\t\t\tIn column paramter we will drag column.\n\t\t\tIn searchcondition parameter provide conditions like (>, =, IS Null etc .,).\n\t\t\tIn value parameter provide value(Note : IS Null used in 'condition' parameter then don't provide anything in 'value' parameter). \n\t\t\tIn moreconditions parameter we will use nested when conditions, Else condition . We will use WHEN inside CASE. Example1 : CASE WHEN 1 > 0  THEN 'one' else 'TWO' end. Example2 : CASE WHEN 'Singapore' IS NULL THEN 'Singa' ELSE 'pore'. Example3 : CASE WHEN Washington like '%sh%' THEN 'return washington' else 'NotMatched' end",
                        "value": "WHEN",
                        "signature": "WHEN ${column} ${searchcondition} ${value} THEN ${statement_list}  ${moreconditions}",
                        "returns": "numeric",
                        "parameters": [
                            {
                                "name": "column",
                                "column": true
                            },
                            {
                                "name": "searchcondition",
                                "column": true,
                                "defaultValue": ""
                            },
                            {
                                "name": "value",
                                "column": true,
                                "defaultValue": ""
                            },
                            {
                                "name": "statement_list",
                                "column": true
                            },
                            {
                                "name": "moreconditions",
                                "column": true,
                                "defaultValue": ""
                            }
                        ]
                    },
                    {
                        "key": "sql.logical.zn",
                        "description": "Returns \"expression\" if it is not null, otherwise returns zero.Example :(CASE WHEN '123' IS NULL THEN '0' ELSE '123' end) result :0 ",
                        "value": "ZN",
                        "signature": "(CASE WHEN ${expr} IS NULL THEN '0' ELSE ${expr} end)",
                        "returns": "text",
                        "parameters": [
                            {
                                "name": "expr",
                                "column": true
                            }
                        ]
                    }
                ]
            },
            "functions": {
                "db.generic.aggregate.avg": "avg",
                "db.generic.aggregate.count": "count",
                "db.generic.aggregate.distinct": "distinct",
                "db.generic.aggregate.max": "max",
                "db.generic.aggregate.min": "min",
                "db.generic.aggregate.sum": "sum",
                "db.generic.groupBy.group": "group by",
                "db.generic.orderBy.order": "order by"
            }
        }
    }
}
export const getDateFunctions = () => {
    return {
        "status": 1,
        "response": {
            "dateTime": [
                {
                    "label": "Date",
                    "part": "date",
                    "key": "sql.typeConversion.todate",
                    "returns": "date",
                    "parameters": [
                        {
                            "name": "column"
                        }
                    ]
                },
                {
                    "label": "Days",
                    "part": "day",
                    "key": "sql.dateTime.day",
                    "returns": "numeric",
                    "parameters": [
                        {
                            "name": "datetime"
                        }
                    ]
                },
                {
                    "label": "Months",
                    "part": "month",
                    "key": "sql.dateTime.month",
                    "returns": "numeric",
                    "parameters": [
                        {
                            "name": "datetime"
                        }
                    ]
                },
                {
                    "label": "Quarters",
                    "part": "quarter",
                    "key": "sql.dateTime.quarter",
                    "returns": "numeric",
                    "parameters": [
                        {
                            "name": "datetime"
                        }
                    ]
                },
                {
                    "label": "Years",
                    "part": "year",
                    "key": "sql.dateTime.year",
                    "returns": "numeric",
                    "parameters": [
                        {
                            "name": "datetime"
                        }
                    ]
                },
                {
                    "label": "Time",
                    "part": "time",
                    "key": "sql.typeConversion.totime",
                    "returns": "time",
                    "parameters": [
                        {
                            "name": "column"
                        }
                    ]
                },
                {
                    "label": "Hours",
                    "part": "hour",
                    "key": "sql.dateTime.hour",
                    "returns": "numeric",
                    "parameters": [
                        {
                            "name": "datetime"
                        }
                    ]
                },
                {
                    "label": "Minutes",
                    "part": "minute",
                    "key": "sql.dateTime.minute",
                    "returns": "numeric",
                    "parameters": [
                        {
                            "name": "datetime"
                        }
                    ]
                },
                {
                    "label": "Seconds",
                    "part": "second",
                    "key": "sql.dateTime.second",
                    "returns": "numeric",
                    "parameters": [
                        {
                            "name": "datetime"
                        }
                    ]
                },
                {
                    "label": "Individual",
                    "part": "individual",
                    "key": "individual",
                    "parameters": [
                        {
                            "name": "datetime"
                        }
                    ]
                }
            ],
            "date": [
                {
                    "label": "Date",
                    "part": "date",
                    "key": "sql.typeConversion.todate",
                    "returns": "date",
                    "parameters": [
                        {
                            "name": "column"
                        }
                    ]
                },
                {
                    "label": "Days",
                    "part": "day",
                    "key": "sql.dateTime.day",
                    "returns": "numeric",
                    "parameters": [
                        {
                            "name": "datetime"
                        }
                    ]
                },
                {
                    "label": "Months",
                    "part": "month",
                    "key": "sql.dateTime.month",
                    "returns": "numeric",
                    "parameters": [
                        {
                            "name": "datetime"
                        }
                    ]
                },
                {
                    "label": "Quarters",
                    "part": "quarter",
                    "key": "sql.dateTime.quarter",
                    "returns": "numeric",
                    "parameters": [
                        {
                            "name": "datetime"
                        }
                    ]
                },
                {
                    "label": "Years",
                    "part": "year",
                    "key": "sql.dateTime.year",
                    "returns": "numeric",
                    "parameters": [
                        {
                            "name": "datetime"
                        }
                    ]
                },
                {
                    "label": "Individual",
                    "part": "individual",
                    "key": "individual",
                    "parameters": [
                        {
                            "name": "datetime"
                        }
                    ]
                }
            ],
            "time": [
                {
                    "label": "Time",
                    "part": "time",
                    "key": "sql.typeConversion.totime",
                    "returns": "time",
                    "parameters": [
                        {
                            "name": "column"
                        }
                    ]
                },
                {
                    "label": "Hours",
                    "part": "hour",
                    "key": "sql.dateTime.hour",
                    "returns": "numeric",
                    "parameters": [
                        {
                            "name": "datetime"
                        }
                    ]
                },
                {
                    "label": "Minutes",
                    "part": "minute",
                    "key": "sql.dateTime.minute",
                    "returns": "numeric",
                    "parameters": [
                        {
                            "name": "datetime"
                        }
                    ]
                },
                {
                    "label": "Seconds",
                    "part": "second",
                    "key": "sql.dateTime.second",
                    "returns": "numeric",
                    "parameters": [
                        {
                            "name": "datetime"
                        }
                    ]
                },
                {
                    "label": "Individual",
                    "part": "individual",
                    "key": "individual",
                    "parameters": [
                        {
                            "name": "datetime"
                        }
                    ]
                }
            ]
        }
    }
}
export const getReportState = (arg) => {
    let { dir, file } = arg || {}
    if (dir === "naresh" && file === "parent.hr") {
        return {
            "status": 1,
            "response": {
                "canvas": {
                    "columns": [
                        {
                            "column": "travel_details.booking_platform",
                            "label": "booking_platform",
                            "id": "ac4af067-5335-4102-98e7-8ec251f5cf12",
                            "type": {
                                "backendDataType": "java.lang.String",
                                "dataType": "text"
                            },
                            "autogen_alias": "booking_platform",
                            "isNormalTable": true,
                            "tableAlias": "travel_details",
                            "groupBy": [
                                "db.generic.groupBy.group"
                            ],
                            "orderByColumn": false,
                            "showOrderByColumn": false,
                            "addedAs": "column",
                            "floatingType": "discrete",
                            "functionsDefinition": "",
                            "applyBeforeAggregate": false,
                            "hiddenIncludeInResultSet": false,
                            "metaDataAlias": "booking_platform"
                        }
                    ]
                },
                "reportName": "parent",
                "metadata": {
                    "location": "TestFold",
                    "metadataFileName": "Metadata_1.metadata",
                    "databaseName": "HIUSER",
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
                        "uniqueId": "Metadata_1",
                        tables,
                        "sets": [
                            [
                                "geo_cordinates",
                                "dimdate",
                                "travel_details",
                                "employee_details",
                                "meeting_details"
                            ]
                        ],
                        "metadataName": "Metadata_1",
                        "metadataDir": "TestFold"
                    }
                },
                "state": {
                    "fields": [
                        {
                            "column": "travel_details.booking_platform",
                            "label": "booking_platform",
                            "id": "ac4af067-5335-4102-98e7-8ec251f5cf12",
                            "type": {
                                "backendDataType": "java.lang.String",
                                "dataType": "text"
                            },
                            "autogen_alias": "booking_platform",
                            "isNormalTable": true,
                            "tableAlias": "travel_details",
                            "groupBy": [
                                "db.generic.groupBy.group"
                            ],
                            "orderByColumn": false,
                            "showOrderByColumn": false,
                            "addedAs": "column",
                            "floatingType": "discrete",
                            "functionsDefinition": "",
                            "applyBeforeAggregate": false,
                            "hiddenIncludeInResultSet": false,
                            "metaDataAlias": "booking_platform"
                        }
                    ],
                    "filters": [],
                    "marksList": [
                        {
                            "value": "_all_",
                            "id": "d69df69c-ec82-4fe7-85b1-d1a51d9777c5",
                            "subVizType": "",
                            "color": {
                                "fields": []
                            },
                            "size": {
                                "fields": []
                            },
                            "label": {
                                "fields": []
                            },
                            "tooltip": {
                                "fields": []
                            },
                            "shape": {
                                "fields": []
                            },
                            "detail": {
                                "fields": []
                            }
                        }
                    ],
                    "activeMark": "d69df69c-ec82-4fe7-85b1-d1a51d9777c5",
                    "activeTool": "6",
                    "scripts": [
                        {
                            "id": "pre-execution",
                            "value": "",
                            "title": "Pre Execution"
                        },
                        {
                            "id": "pre-fetch",
                            "value": "",
                            "title": "Pre Fetch"
                        },
                        {
                            "id": "post-fetch",
                            "value": "",
                            "title": "Post Fetch"
                        },
                        {
                            "id": "post-execution",
                            "value": "",
                            "title": "Post Execution"
                        }
                    ],
                    "selectedScript": "pre-execution",
                    "styles": "",
                    "options": {
                        "limitBy": 1000,
                        "sample": "sample",
                        "prependTableNameToAlias": false
                    },
                    "interactiveMode": true,
                    "drillDown": true,
                    "drillThrough": true,
                    "drillDownList": [],
                    "currentDrillDown": "",
                    "drillThroughList": [],
                    "toolbarConfig": {
                        "selectable": false
                    },
                    "selectedType": "Table",
                    "customStyles": "",
                    "customScripts": [],
                    "analytics": [
                        {
                            "value": false,
                            "key": "rowSubTotals",
                            "label": "Row Sub Totals"
                        },
                        {
                            "value": false,
                            "key": "columnSubTotals",
                            "label": "Column Sub Totals"
                        },
                        {
                            "value": false,
                            "key": "rowGrandTotals",
                            "label": "Row Grand Totals"
                        },
                        {
                            "value": false,
                            "key": "columnGrandTotals",
                            "label": "Column Grand Totals"
                        }
                    ],
                    "properties": {
                        "title": {
                            "show": false,
                            "value": "",
                            "padding": 0,
                            "fontSize": 32,
                            "fontColor": {
                                "a": 1,
                                "b": 0,
                                "g": 0,
                                "r": 0
                            },
                            "alignment": "center",
                            "position": "top"
                        },
                        "subTitle": {
                            "show": false,
                            "value": "",
                            "padding": 0,
                            "fontSize": 24,
                            "fontColor": {
                                "a": 1,
                                "b": 0,
                                "g": 0,
                                "r": 0
                            },
                            "alignment": "center",
                            "position": "top"
                        },
                        "format": {
                            "field": "",
                            "apply": [],
                            "formatDatatype": "",
                            "thousandSperator": false,
                            "decimalPlace": 2,
                            "prefix": "",
                            "suffix": "",
                            "displayUnits": "None",
                            "percentage": false,
                            "numberCustom": "",
                            "day": "dayNumber",
                            "week": "none",
                            "month": "monthNum",
                            "quarter": "none",
                            "year": "4digit",
                            "dateSeperator": "-",
                            "dateCustom": "",
                            "hour": "12hr",
                            "minute": "mintuesNumber",
                            "second": "secondsNumber",
                            "milliSecond": "milliSecondsNumber",
                            "timeSeperator": ":",
                            "timeCustom": ""
                        }
                    },
                    "showHiddenColumns": false,
                    "showHiddenRows": false,
                    "database": "HIUSER"
                }
            }
        }
    }
    if (dir === "naresh" && file === "with_report.hr") {
        return {
            "status": 1,
            "response": {
                "canvas": {
                    "columns": [
                        {
                            "column": "travel_details.booking_platform",
                            "label": "booking_platform",
                            "id": "ac4af067-5335-4102-98e7-8ec251f5cf12",
                            "type": {
                                "backendDataType": "java.lang.String",
                                "dataType": "text"
                            },
                            "autogen_alias": "booking_platform",
                            "isNormalTable": true,
                            "tableAlias": "travel_details",
                            "groupBy": [
                                "db.generic.groupBy.group"
                            ],
                            "orderByColumn": false,
                            "showOrderByColumn": false,
                            "addedAs": "column",
                            "floatingType": "discrete",
                            "functionsDefinition": "",
                            "applyBeforeAggregate": false,
                            "hiddenIncludeInResultSet": false,
                            "metaDataAlias": "booking_platform"
                        }
                    ]
                },
                "reportName": "parent",
                "metadata": {
                    "location": "TestFold",
                    "metadataFileName": "Metadata_1.metadata",
                    "databaseName": "HIUSER",
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
                        "uniqueId": "Metadata_1",
                        tables,
                        "sets": [
                            [
                                "geo_cordinates",
                                "dimdate",
                                "travel_details",
                                "employee_details",
                                "meeting_details"
                            ]
                        ],
                        "metadataName": "Metadata_1",
                        "metadataDir": "TestFold"
                    }
                },
                "state": {
                    "fields": [
                        {
                            "column": "travel_details.booking_platform",
                            "label": "booking_platform",
                            "id": "ac4af067-5335-4102-98e7-8ec251f5cf12",
                            "type": {
                                "backendDataType": "java.lang.String",
                                "dataType": "text"
                            },
                            "autogen_alias": "booking_platform",
                            "isNormalTable": true,
                            "tableAlias": "travel_details",
                            "groupBy": [
                                "db.generic.groupBy.group"
                            ],
                            "orderByColumn": false,
                            "showOrderByColumn": false,
                            "addedAs": "column",
                            "floatingType": "discrete",
                            "functionsDefinition": "",
                            "applyBeforeAggregate": false,
                            "hiddenIncludeInResultSet": false,
                            "metaDataAlias": "booking_platform"
                        }
                    ],
                    "filters": [],
                    "marksList": [
                        {
                            "value": "_all_",
                            "id": "d69df69c-ec82-4fe7-85b1-d1a51d9777c5",
                            "subVizType": "",
                            "color": {
                                "fields": []
                            },
                            "size": {
                                "fields": []
                            },
                            "label": {
                                "fields": []
                            },
                            "tooltip": {
                                "fields": []
                            },
                            "shape": {
                                "fields": []
                            },
                            "detail": {
                                "fields": []
                            }
                        }
                    ],
                    "activeMark": "d69df69c-ec82-4fe7-85b1-d1a51d9777c5",
                    "activeTool": "6",
                    "scripts": [
                        {
                            "id": "pre-execution",
                            "value": "",
                            "title": "Pre Execution"
                        },
                        {
                            "id": "pre-fetch",
                            "value": "",
                            "title": "Pre Fetch"
                        },
                        {
                            "id": "post-fetch",
                            "value": "",
                            "title": "Post Fetch"
                        },
                        {
                            "id": "post-execution",
                            "value": "",
                            "title": "Post Execution"
                        }
                    ],
                    "selectedScript": "pre-execution",
                    "styles": "",
                    "options": {
                        "limitBy": 1000,
                        "sample": "sample",
                        "prependTableNameToAlias": false
                    },
                    "interactiveMode": true,
                    "drillDown": true,
                    "drillThrough": true,
                    "drillDownList": [],
                    "currentDrillDown": "",
                    "drillThroughList": [],
                    "toolbarConfig": {
                        "selectable": false
                    },
                    "selectedType": "Table",
                    "customStyles": "",
                    "customScripts": [],
                    "analytics": [
                        {
                            "value": false,
                            "key": "rowSubTotals",
                            "label": "Row Sub Totals"
                        },
                        {
                            "value": false,
                            "key": "columnSubTotals",
                            "label": "Column Sub Totals"
                        },
                        {
                            "value": false,
                            "key": "rowGrandTotals",
                            "label": "Row Grand Totals"
                        },
                        {
                            "value": false,
                            "key": "columnGrandTotals",
                            "label": "Column Grand Totals"
                        }
                    ],
                    "properties": {
                        "title": {
                            "show": false,
                            "value": "",
                            "padding": 0,
                            "fontSize": 32,
                            "fontColor": {
                                "a": 1,
                                "b": 0,
                                "g": 0,
                                "r": 0
                            },
                            "alignment": "center",
                            "position": "top"
                        },
                        "subTitle": {
                            "show": false,
                            "value": "",
                            "padding": 0,
                            "fontSize": 24,
                            "fontColor": {
                                "a": 1,
                                "b": 0,
                                "g": 0,
                                "r": 0
                            },
                            "alignment": "center",
                            "position": "top"
                        },
                        "format": {
                            "field": "",
                            "apply": [],
                            "formatDatatype": "",
                            "thousandSperator": false,
                            "decimalPlace": 2,
                            "prefix": "",
                            "suffix": "",
                            "displayUnits": "None",
                            "percentage": false,
                            "numberCustom": "",
                            "day": "dayNumber",
                            "week": "none",
                            "month": "monthNum",
                            "quarter": "none",
                            "year": "4digit",
                            "dateSeperator": "-",
                            "dateCustom": "",
                            "hour": "12hr",
                            "minute": "mintuesNumber",
                            "second": "secondsNumber",
                            "milliSecond": "milliSecondsNumber",
                            "timeSeperator": ":",
                            "timeCustom": ""
                        }
                    },
                    "showHiddenColumns": false,
                    "showHiddenRows": false,
                    "database": "HIUSER"
                }
            }
        }
    }
    if (dir === "naresh" && file === "child.hr") {
        return {
            "status": 1,
            "response": {
                "canvas": {
                    "columns": [
                        {
                            "column": "travel_details.booking_platform",
                            "label": "booking_platform",
                            "id": "da58e8a3-89bd-4edc-9d6f-227e226149c0",
                            "type": {
                                "backendDataType": "java.lang.String",
                                "dataType": "text"
                            },
                            "autogen_alias": "booking_platform",
                            "isNormalTable": true,
                            "tableAlias": "travel_details",
                            "groupBy": [
                                "db.generic.groupBy.group"
                            ],
                            "orderByColumn": false,
                            "showOrderByColumn": false,
                            "addedAs": "column",
                            "floatingType": "discrete",
                            "functionsDefinition": "",
                            "applyBeforeAggregate": false,
                            "hiddenIncludeInResultSet": false,
                            "metaDataAlias": "booking_platform"
                        },
                        {
                            "column": "travel_details.mode_of_payment",
                            "label": "mode_of_payment",
                            "id": "99dba994-b586-4add-89b9-ca4c14311b4e",
                            "type": {
                                "backendDataType": "java.lang.String",
                                "dataType": "text"
                            },
                            "autogen_alias": "mode_of_payment",
                            "isNormalTable": true,
                            "tableAlias": "travel_details",
                            "groupBy": [
                                "db.generic.groupBy.group"
                            ],
                            "orderByColumn": false,
                            "showOrderByColumn": false,
                            "addedAs": "column",
                            "floatingType": "discrete",
                            "functionsDefinition": "",
                            "applyBeforeAggregate": false,
                            "hiddenIncludeInResultSet": false,
                            "metaDataAlias": "mode_of_payment"
                        }
                    ]
                },
                "reportName": "child",
                "metadata": {
                    "location": "TestFold",
                    "metadataFileName": "Metadata_1.metadata",
                    "databaseName": "HIUSER",
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
                        "uniqueId": "Metadata_1",
                        tables,
                        "sets": [
                            [
                                "geo_cordinates",
                                "dimdate",
                                "travel_details",
                                "employee_details",
                                "meeting_details"
                            ]
                        ],
                        "metadataName": "Metadata_1",
                        "metadataDir": "TestFold"
                    }
                },
                "state": {
                    "fields": [
                        {
                            "column": "travel_details.booking_platform",
                            "label": "booking_platform",
                            "id": "da58e8a3-89bd-4edc-9d6f-227e226149c0",
                            "type": {
                                "backendDataType": "java.lang.String",
                                "dataType": "text"
                            },
                            "autogen_alias": "booking_platform",
                            "isNormalTable": true,
                            "tableAlias": "travel_details",
                            "groupBy": [
                                "db.generic.groupBy.group"
                            ],
                            "orderByColumn": false,
                            "showOrderByColumn": false,
                            "addedAs": "column",
                            "floatingType": "discrete",
                            "functionsDefinition": "",
                            "applyBeforeAggregate": false,
                            "hiddenIncludeInResultSet": false,
                            "metaDataAlias": "booking_platform"
                        },
                        {
                            "column": "travel_details.mode_of_payment",
                            "label": "mode_of_payment",
                            "id": "99dba994-b586-4add-89b9-ca4c14311b4e",
                            "type": {
                                "backendDataType": "java.lang.String",
                                "dataType": "text"
                            },
                            "autogen_alias": "mode_of_payment",
                            "isNormalTable": true,
                            "tableAlias": "travel_details",
                            "groupBy": [
                                "db.generic.groupBy.group"
                            ],
                            "orderByColumn": false,
                            "showOrderByColumn": false,
                            "addedAs": "column",
                            "floatingType": "discrete",
                            "functionsDefinition": "",
                            "applyBeforeAggregate": false,
                            "hiddenIncludeInResultSet": false,
                            "metaDataAlias": "mode_of_payment"
                        }
                    ],
                    "filters": [
                        {
                            "column": "travel_details.booking_platform",
                            "label": "booking_platform",
                            "dataType": "text",
                            "backendDataType": "java.lang.String",
                            "condition": "IS_ONE_OF",
                            "values": [
                                "Makemytrip"
                            ],
                            "valuesMode": "custom",
                            "mode": "auto",
                            "groupBy": [
                                "db.generic.groupBy.group"
                            ],
                            "orderBy": "",
                            "valuesRange": {},
                            "rangeValuesType": "",
                            "dateTimeToggle": false,
                            "rangeSelectionToggole": true,
                            "maxInput": "",
                            "minInput": "",
                            "valuesList": [],
                            "drillDownId": "",
                            "uid": "9828701c-c36e-4a97-b539-18991c20a2e7",
                            "configId": "d0183037-50c2-4342-8268-ed8952edbf7e",
                            "dataId": "d7537b82-5828-42ea-9de4-2347485d26db",
                            "mapping": {
                                "isEnabled": true,
                                "unique": true,
                                "isDefaultFunction": true,
                                "valueDisplayMap": [],
                                "valueAliasName": "random",
                                "orderBy": {
                                    "display": "asc",
                                    "value": "none"
                                },
                                "valueDBFuntionInfo": {},
                                "valueColumn": {
                                    "alias": "booking_platform",
                                    "fullyQualifiedColumn": "travel_details.booking_platform",
                                    "columnId": "95859ecc-a29c-43a0-b5da-4c3e63299cbb",
                                    "defaultFunction": "db.generic.groupBy.group",
                                    "type": {
                                        "java.lang.String": "text"
                                    }
                                },
                                "displayColumn": {
                                    "alias": "booking_platform",
                                    "fullyQualifiedColumn": "travel_details.booking_platform",
                                    "columnId": "95859ecc-a29c-43a0-b5da-4c3e63299cbb",
                                    "defaultFunction": "db.generic.groupBy.group",
                                    "type": {
                                        "java.lang.String": "text"
                                    }
                                }
                            },
                            "cascade": {
                                "isEnabled": false,
                                "filters": [],
                                "filtersCount": 0
                            },
                            "active": false,
                            "loading": false
                        }
                    ],
                    "marksList": [
                        {
                            "value": "_all_",
                            "id": "bd754a00-ece2-449c-9706-0985a263d189",
                            "subVizType": "",
                            "color": {
                                "fields": []
                            },
                            "size": {
                                "fields": []
                            },
                            "label": {
                                "fields": []
                            },
                            "tooltip": {
                                "fields": []
                            },
                            "shape": {
                                "fields": []
                            },
                            "detail": {
                                "fields": []
                            }
                        }
                    ],
                    "activeMark": "bd754a00-ece2-449c-9706-0985a263d189",
                    "activeTool": "2",
                    "scripts": [
                        {
                            "id": "pre-execution",
                            "value": "",
                            "title": "Pre Execution"
                        },
                        {
                            "id": "pre-fetch",
                            "value": "",
                            "title": "Pre Fetch"
                        },
                        {
                            "id": "post-fetch",
                            "value": "",
                            "title": "Post Fetch"
                        },
                        {
                            "id": "post-execution",
                            "value": "",
                            "title": "Post Execution"
                        }
                    ],
                    "selectedScript": "pre-execution",
                    "styles": "",
                    "options": {
                        "limitBy": 1000,
                        "sample": "sample",
                        "prependTableNameToAlias": false
                    },
                    "interactiveMode": false,
                    "drillDown": false,
                    "drillThrough": false,
                    "drillDownList": [],
                    "currentDrillDown": "",
                    "drillThroughList": [],
                    "toolbarConfig": {
                        "selectable": false
                    },
                    "selectedType": "Table",
                    "customStyles": "",
                    "customScripts": [],
                    "analytics": [
                        {
                            "value": false,
                            "key": "rowSubTotals",
                            "label": "Row Sub Totals"
                        },
                        {
                            "value": false,
                            "key": "columnSubTotals",
                            "label": "Column Sub Totals"
                        },
                        {
                            "value": false,
                            "key": "rowGrandTotals",
                            "label": "Row Grand Totals"
                        },
                        {
                            "value": false,
                            "key": "columnGrandTotals",
                            "label": "Column Grand Totals"
                        }
                    ],
                    "properties": {
                        "title": {
                            "show": false,
                            "value": "",
                            "padding": 0,
                            "fontSize": 32,
                            "fontColor": {
                                "a": 1,
                                "b": 0,
                                "g": 0,
                                "r": 0
                            },
                            "alignment": "center",
                            "position": "top"
                        },
                        "subTitle": {
                            "show": false,
                            "value": "",
                            "padding": 0,
                            "fontSize": 24,
                            "fontColor": {
                                "a": 1,
                                "b": 0,
                                "g": 0,
                                "r": 0
                            },
                            "alignment": "center",
                            "position": "top"
                        },
                        "format": {
                            "field": "",
                            "apply": [],
                            "formatDatatype": "",
                            "thousandSperator": false,
                            "decimalPlace": 2,
                            "prefix": "",
                            "suffix": "",
                            "displayUnits": "None",
                            "percentage": false,
                            "numberCustom": "",
                            "day": "dayNumber",
                            "week": "none",
                            "month": "monthNum",
                            "quarter": "none",
                            "year": "4digit",
                            "dateSeperator": "-",
                            "dateCustom": "",
                            "hour": "12hr",
                            "minute": "mintuesNumber",
                            "second": "secondsNumber",
                            "milliSecond": "milliSecondsNumber",
                            "timeSeperator": ":",
                            "timeCustom": ""
                        }
                    },
                    "showHiddenColumns": false,
                    "showHiddenRows": false,
                    "database": "HIUSER"
                }
            }
        }
    }
    if (dir === "naresh" && file === "with_filter.hr") {
        return {
            "status": 1,
            "response": {
                "canvas": {
                    "columns": [
                        {
                            "column": "travel_details.travel_medium",
                            "label": "travel_medium",
                            "id": "a9270f2a-66a5-4689-930c-81e2a8e7e195",
                            "type": {
                                "backendDataType": "java.lang.String",
                                "dataType": "text"
                            },
                            "autogen_alias": "travel_medium",
                            "isNormalTable": true,
                            "tableAlias": "travel_details",
                            "groupBy": [
                                "db.generic.groupBy.group"
                            ],
                            "orderByColumn": false,
                            "showOrderByColumn": false,
                            "addedAs": "column",
                            "floatingType": "discrete",
                            "functionsDefinition": "",
                            "applyBeforeAggregate": false,
                            "hiddenIncludeInResultSet": false,
                            "metaDataAlias": "travel_medium"
                        }
                    ]
                },
                "reportName": "with_filter",
                "metadata": {
                    "location": "TestFold",
                    "metadataFileName": "Metadata_1.metadata",
                    "databaseName": "HIUSER",
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
                        "uniqueId": "Metadata_1",
                        tables,
                        "sets": [
                            [
                                "geo_cordinates",
                                "dimdate",
                                "travel_details",
                                "employee_details",
                                "meeting_details"
                            ]
                        ],
                        "metadataName": "Metadata_1",
                        "metadataDir": "TestFold"
                    }
                },
                "state": {
                    "fields": [
                        {
                            "column": "travel_details.travel_medium",
                            "label": "travel_medium",
                            "id": "a9270f2a-66a5-4689-930c-81e2a8e7e195",
                            "type": {
                                "backendDataType": "java.lang.String",
                                "dataType": "text"
                            },
                            "autogen_alias": "travel_medium",
                            "isNormalTable": true,
                            "tableAlias": "travel_details",
                            "groupBy": [
                                "db.generic.groupBy.group"
                            ],
                            "orderByColumn": false,
                            "showOrderByColumn": false,
                            "addedAs": "column",
                            "floatingType": "discrete",
                            "functionsDefinition": "",
                            "applyBeforeAggregate": false,
                            "hiddenIncludeInResultSet": false,
                            "metaDataAlias": "travel_medium"
                        }
                    ],
                    "filters": [
                        {
                            "column": "travel_details.booking_platform",
                            "label": "booking_platform",
                            "dataType": "text",
                            "backendDataType": "java.lang.String",
                            "condition": "IS_ONE_OF",
                            "values": [
                                "Website"
                            ],
                            "valuesMode": "custom",
                            "mode": "auto",
                            "orderBy": "",
                            "valuesRange": {},
                            "rangeValuesType": "",
                            "dateTimeToggle": false,
                            "rangeSelectionToggole": true,
                            "maxInput": "",
                            "minInput": "",
                            "valuesList": [],
                            "drillDownId": "",
                            "uid": "a3b10723-eb5b-4c1c-a993-fce64ba30282",
                            "configId": "b2a5b5dd-0bf6-4983-ab92-21d4916665b7",
                            "dataId": "afd9a056-2723-4f5c-9da2-c05176b20562",
                            "mapping": {
                                "isEnabled": true,
                                "unique": true,
                                "isDefaultFunction": true,
                                "valueDisplayMap": [],
                                "valueAliasName": "random",
                                "orderBy": {
                                    "display": "asc",
                                    "value": "none"
                                },
                                "valueDBFuntionInfo": {},
                                "valueColumn": {
                                    "alias": "booking_platform",
                                    "fullyQualifiedColumn": "travel_details.booking_platform",
                                    "columnId": "95859ecc-a29c-43a0-b5da-4c3e63299cbb",
                                    "defaultFunction": "none",
                                    "type": {
                                        "java.lang.String": "text"
                                    }
                                },
                                "displayColumn": {
                                    "alias": "booking_platform",
                                    "fullyQualifiedColumn": "travel_details.booking_platform",
                                    "columnId": "95859ecc-a29c-43a0-b5da-4c3e63299cbb",
                                    "defaultFunction": "none",
                                    "type": {
                                        "java.lang.String": "text"
                                    }
                                }
                            },
                            "cascade": {
                                "isEnabled": false,
                                "filters": [],
                                "filtersCount": 0
                            },
                            "active": false,
                            "loading": false
                        }
                    ],
                    "marksList": [
                        {
                            "value": "_all_",
                            "id": "eee664fe-d046-42a1-a999-b2b2a18dca39",
                            "subVizType": "",
                            "color": {
                                "fields": []
                            },
                            "size": {
                                "fields": []
                            },
                            "label": {
                                "fields": []
                            },
                            "tooltip": {
                                "fields": []
                            },
                            "shape": {
                                "fields": []
                            },
                            "detail": {
                                "fields": []
                            }
                        }
                    ],
                    "activeMark": "eee664fe-d046-42a1-a999-b2b2a18dca39",
                    "activeTool": "6",
                    "scripts": [
                        {
                            "id": "pre-execution",
                            "value": "",
                            "title": "Pre Execution"
                        },
                        {
                            "id": "pre-fetch",
                            "value": "",
                            "title": "Pre Fetch"
                        },
                        {
                            "id": "post-fetch",
                            "value": "",
                            "title": "Post Fetch"
                        },
                        {
                            "id": "post-execution",
                            "value": "",
                            "title": "Post Execution"
                        }
                    ],
                    "selectedScript": "pre-execution",
                    "styles": "",
                    "options": {
                        "limitBy": 1000,
                        "sample": "sample",
                        "prependTableNameToAlias": false
                    },
                    "interactiveMode": true,
                    "drillDown": true,
                    "drillThrough": true,
                    "drillDownList": [],
                    "currentDrillDown": "",
                    "drillThroughList": [],
                    "toolbarConfig": {
                        "selectable": false
                    },
                    "selectedType": "Table",
                    "customStyles": "",
                    "customScripts": [],
                    "analytics": [
                        {
                            "value": false,
                            "key": "rowSubTotals",
                            "label": "Row Sub Totals"
                        },
                        {
                            "value": false,
                            "key": "columnSubTotals",
                            "label": "Column Sub Totals"
                        },
                        {
                            "value": false,
                            "key": "rowGrandTotals",
                            "label": "Row Grand Totals"
                        },
                        {
                            "value": false,
                            "key": "columnGrandTotals",
                            "label": "Column Grand Totals"
                        }
                    ],
                    "properties": {
                        "title": {
                            "show": false,
                            "value": "",
                            "padding": 0,
                            "fontSize": 32,
                            "fontColor": {
                                "a": 1,
                                "b": 0,
                                "g": 0,
                                "r": 0
                            },
                            "alignment": "center",
                            "position": "top"
                        },
                        "subTitle": {
                            "show": false,
                            "value": "",
                            "padding": 0,
                            "fontSize": 24,
                            "fontColor": {
                                "a": 1,
                                "b": 0,
                                "g": 0,
                                "r": 0
                            },
                            "alignment": "center",
                            "position": "top"
                        },
                        "format": {
                            "field": "",
                            "apply": [],
                            "formatDatatype": "",
                            "thousandSperator": false,
                            "decimalPlace": 2,
                            "prefix": "",
                            "suffix": "",
                            "displayUnits": "None",
                            "percentage": false,
                            "numberCustom": "",
                            "day": "dayNumber",
                            "week": "none",
                            "month": "monthNum",
                            "quarter": "none",
                            "year": "4digit",
                            "dateSeperator": "-",
                            "dateCustom": "",
                            "hour": "12hr",
                            "minute": "mintuesNumber",
                            "second": "secondsNumber",
                            "milliSecond": "milliSecondsNumber",
                            "timeSeperator": ":",
                            "timeCustom": ""
                        }
                    },
                    "showHiddenColumns": false,
                    "showHiddenRows": false,
                    "database": "HIUSER"
                }
            }
        }
    }
    if (dir === "manish" && file === "crosstab_interactivity.hr") {
        return {
            "status": 1,
            "response": {
                "canvas": {
                    "columns": [
                        {
                            "column": "travel_details.travel_id",
                            "label": "sum_travel_id",
                            "id": "d6638a5f-e4ce-4f3f-9681-1b1668f05315",
                            "type": {
                                "backendDataType": "java.lang.Integer",
                                "dataType": "numeric"
                            },
                            "autogen_alias": "sum_travel_id",
                            "isNormalTable": true,
                            "tableAlias": "travel_details",
                            "aggregate": [
                                "db.generic.aggregate.sum"
                            ],
                            "orderByColumn": false,
                            "showOrderByColumn": false,
                            "addedAs": "row",
                            "floatingType": "",
                            "functionsDefinition": "",
                            "applyBeforeAggregate": false,
                            "hiddenIncludeInResultSet": false,
                            "metaDataAlias": "travel_id"
                        },
                        {
                            "column": "travel_details.booking_platform",
                            "label": "booking_platform",
                            "id": "4a2bc99a-1743-4ca4-bdc2-7315d682be00",
                            "type": {
                                "backendDataType": "java.lang.String",
                                "dataType": "text"
                            },
                            "autogen_alias": "booking_platform",
                            "isNormalTable": true,
                            "tableAlias": "travel_details",
                            "groupBy": [
                                "db.generic.groupBy.group"
                            ],
                            "orderByColumn": false,
                            "showOrderByColumn": false,
                            "addedAs": "column",
                            "floatingType": "discrete",
                            "functionsDefinition": "",
                            "applyBeforeAggregate": false,
                            "hiddenIncludeInResultSet": false,
                            "metaDataAlias": "booking_platform"
                        }
                    ]
                },
                "reportName": "6180",
                "metadata": {
                    "location": "Manish_18",
                    "metadataFileName": "Metadata_new.metadata",
                    "databaseName": "HIUSER",
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
                            "baseType": "global.jdbc",
                            "dbId": "5311"
                        },
                        "uniqueId": "Metadata_new",
                        "tables": {
                            "dimdate": {
                                "id": "5736",
                                "alias": "dimdate",
                                "columns": {
                                    "dim_id": {
                                        "alias": "dim_id",
                                        "fullyQualifiedColumn": "dimdate.dim_id",
                                        "id": "13082",
                                        "defaultFunction": "db.generic.aggregate.sum",
                                        "type": {
                                            "java.lang.Integer": "numeric"
                                        }
                                    },
                                    "fiscal_year": {
                                        "alias": "fiscal_year",
                                        "fullyQualifiedColumn": "dimdate.fiscal_year",
                                        "id": "13083",
                                        "defaultFunction": "db.generic.groupBy.group",
                                        "type": {
                                            "java.sql.Date": "date"
                                        }
                                    },
                                    "modified_date": {
                                        "alias": "modified_date",
                                        "fullyQualifiedColumn": "dimdate.modified_date",
                                        "id": "13084",
                                        "defaultFunction": "db.generic.groupBy.group",
                                        "type": {
                                            "java.sql.Timestamp": "dateTime"
                                        }
                                    },
                                    "date_key": {
                                        "alias": "date_key",
                                        "fullyQualifiedColumn": "dimdate.date_key",
                                        "id": "13085",
                                        "defaultFunction": "db.generic.groupBy.group",
                                        "type": {
                                            "java.lang.String": "text"
                                        }
                                    },
                                    "day_number": {
                                        "alias": "day_number",
                                        "fullyQualifiedColumn": "dimdate.day_number",
                                        "id": "13086",
                                        "defaultFunction": "db.generic.groupBy.group",
                                        "type": {
                                            "java.lang.String": "text"
                                        }
                                    },
                                    "fiscal_month_name": {
                                        "alias": "fiscal_month_name",
                                        "fullyQualifiedColumn": "dimdate.fiscal_month_name",
                                        "id": "13087",
                                        "defaultFunction": "db.generic.groupBy.group",
                                        "type": {
                                            "java.lang.String": "text"
                                        }
                                    },
                                    "fiscal_month_label": {
                                        "alias": "fiscal_month_label",
                                        "fullyQualifiedColumn": "dimdate.fiscal_month_label",
                                        "id": "13088",
                                        "defaultFunction": "db.generic.groupBy.group",
                                        "type": {
                                            "java.lang.String": "text"
                                        }
                                    },
                                    "created_date": {
                                        "alias": "created_date",
                                        "fullyQualifiedColumn": "dimdate.created_date",
                                        "id": "13089",
                                        "defaultFunction": "db.generic.groupBy.group",
                                        "type": {
                                            "java.lang.String": "text"
                                        }
                                    },
                                    "created_time": {
                                        "alias": "created_time",
                                        "fullyQualifiedColumn": "dimdate.created_time",
                                        "id": "13090",
                                        "defaultFunction": "db.generic.groupBy.group",
                                        "type": {
                                            "java.lang.String": "text"
                                        }
                                    },
                                    "rating": {
                                        "alias": "rating",
                                        "fullyQualifiedColumn": "dimdate.rating",
                                        "id": "13091",
                                        "defaultFunction": "db.generic.groupBy.group",
                                        "type": {
                                            "java.lang.String": "text"
                                        }
                                    }
                                },
                                "name": "dimdate",
                                "cacheId": "4ac5d9f68b58bd7c0d179146e46795be"
                            },
                            "employee_details": {
                                "id": "5737",
                                "alias": "employee_details",
                                "columns": {
                                    "employee_id": {
                                        "alias": "employee_id",
                                        "fullyQualifiedColumn": "employee_details.employee_id",
                                        "id": "13092",
                                        "defaultFunction": "db.generic.aggregate.sum",
                                        "type": {
                                            "java.lang.Integer": "numeric"
                                        }
                                    },
                                    "employee_name": {
                                        "alias": "employee_name",
                                        "fullyQualifiedColumn": "employee_details.employee_name",
                                        "id": "13093",
                                        "defaultFunction": "db.generic.groupBy.group",
                                        "type": {
                                            "java.lang.String": "text"
                                        }
                                    },
                                    "age": {
                                        "alias": "age",
                                        "fullyQualifiedColumn": "employee_details.age",
                                        "id": "13094",
                                        "defaultFunction": "db.generic.aggregate.sum",
                                        "type": {
                                            "java.lang.Integer": "numeric"
                                        }
                                    },
                                    "address": {
                                        "alias": "address",
                                        "fullyQualifiedColumn": "employee_details.address",
                                        "id": "13095",
                                        "defaultFunction": "db.generic.groupBy.group",
                                        "type": {
                                            "java.lang.String": "text"
                                        }
                                    }
                                },
                                "name": "employee_details",
                                "cacheId": "4e1fd245f4d13b77be423a43f01d80b2"
                            },
                            "geo_cordinates": {
                                "id": "5738",
                                "alias": "geo_cordinates",
                                "columns": {
                                    "location_id": {
                                        "alias": "location_id",
                                        "fullyQualifiedColumn": "geo_cordinates.location_id",
                                        "id": "13096",
                                        "defaultFunction": "db.generic.aggregate.sum",
                                        "type": {
                                            "java.lang.Integer": "numeric"
                                        }
                                    },
                                    "location": {
                                        "alias": "location",
                                        "fullyQualifiedColumn": "geo_cordinates.location",
                                        "id": "13097",
                                        "defaultFunction": "db.generic.groupBy.group",
                                        "type": {
                                            "java.lang.String": "text"
                                        }
                                    },
                                    "latitude": {
                                        "alias": "latitude",
                                        "fullyQualifiedColumn": "geo_cordinates.latitude",
                                        "id": "13098",
                                        "defaultFunction": "db.generic.aggregate.sum",
                                        "type": {
                                            "java.lang.Double": "numeric"
                                        }
                                    },
                                    "longitude": {
                                        "alias": "longitude",
                                        "fullyQualifiedColumn": "geo_cordinates.longitude",
                                        "id": "13099",
                                        "defaultFunction": "db.generic.aggregate.sum",
                                        "type": {
                                            "java.lang.Double": "numeric"
                                        }
                                    }
                                },
                                "name": "geo_cordinates",
                                "cacheId": "be534112989b616b194bc59c2fb25a42"
                            },
                            "meeting_details": {
                                "id": "5739",
                                "alias": "meeting_details",
                                "columns": {
                                    "meeting_id": {
                                        "alias": "meeting_id",
                                        "fullyQualifiedColumn": "meeting_details.meeting_id",
                                        "id": "13100",
                                        "defaultFunction": "db.generic.aggregate.sum",
                                        "type": {
                                            "java.lang.Integer": "numeric"
                                        }
                                    },
                                    "meeting_date": {
                                        "alias": "meeting_date",
                                        "fullyQualifiedColumn": "meeting_details.meeting_date",
                                        "id": "13101",
                                        "defaultFunction": "db.generic.groupBy.group",
                                        "type": {
                                            "java.sql.Timestamp": "dateTime"
                                        }
                                    },
                                    "meeting_by": {
                                        "alias": "meeting_by",
                                        "fullyQualifiedColumn": "meeting_details.meeting_by",
                                        "id": "13102",
                                        "defaultFunction": "db.generic.aggregate.sum",
                                        "type": {
                                            "java.lang.Integer": "numeric"
                                        }
                                    },
                                    "client_name": {
                                        "alias": "client_name",
                                        "fullyQualifiedColumn": "meeting_details.client_name",
                                        "id": "13103",
                                        "defaultFunction": "db.generic.groupBy.group",
                                        "type": {
                                            "java.lang.String": "text"
                                        }
                                    },
                                    "meeting_purpose": {
                                        "alias": "meeting_purpose",
                                        "fullyQualifiedColumn": "meeting_details.meeting_purpose",
                                        "id": "13104",
                                        "defaultFunction": "db.generic.groupBy.group",
                                        "type": {
                                            "java.lang.String": "text"
                                        }
                                    },
                                    "meeting_impact": {
                                        "alias": "meeting_impact",
                                        "fullyQualifiedColumn": "meeting_details.meeting_impact",
                                        "id": "13105",
                                        "defaultFunction": "db.generic.groupBy.group",
                                        "type": {
                                            "java.lang.String": "text"
                                        }
                                    },
                                    "meet_cancellation_status": {
                                        "alias": "meet_cancellation_status",
                                        "fullyQualifiedColumn": "meeting_details.meet_cancellation_status",
                                        "id": "13106",
                                        "defaultFunction": "db.generic.groupBy.group",
                                        "type": {
                                            "java.lang.String": "text"
                                        }
                                    },
                                    "cancellation_reason": {
                                        "alias": "cancellation_reason",
                                        "fullyQualifiedColumn": "meeting_details.cancellation_reason",
                                        "id": "13107",
                                        "defaultFunction": "db.generic.groupBy.group",
                                        "type": {
                                            "java.lang.String": "text"
                                        }
                                    }
                                },
                                "name": "meeting_details",
                                "cacheId": "9645c648a1c0dbeec1287aaf1e996db3"
                            },
                            "travel_details": {
                                "id": "5740",
                                "alias": "travel_details",
                                "columns": {
                                    "travel_id": {
                                        "alias": "travel_id",
                                        "fullyQualifiedColumn": "travel_details.travel_id",
                                        "id": "13108",
                                        "defaultFunction": "db.generic.aggregate.sum",
                                        "type": {
                                            "java.lang.Integer": "numeric"
                                        }
                                    },
                                    "travel_date": {
                                        "alias": "travel_date",
                                        "fullyQualifiedColumn": "travel_details.travel_date",
                                        "id": "13109",
                                        "defaultFunction": "db.generic.groupBy.group",
                                        "type": {
                                            "java.sql.Timestamp": "dateTime"
                                        }
                                    },
                                    "travel_type": {
                                        "alias": "travel_type",
                                        "fullyQualifiedColumn": "travel_details.travel_type",
                                        "id": "13110",
                                        "defaultFunction": "db.generic.groupBy.group",
                                        "type": {
                                            "java.lang.String": "text"
                                        }
                                    },
                                    "travel_medium": {
                                        "alias": "travel_medium",
                                        "fullyQualifiedColumn": "travel_details.travel_medium",
                                        "id": "13111",
                                        "defaultFunction": "db.generic.groupBy.group",
                                        "type": {
                                            "java.lang.String": "text"
                                        }
                                    },
                                    "source_id": {
                                        "alias": "source_id",
                                        "fullyQualifiedColumn": "travel_details.source_id",
                                        "id": "13112",
                                        "defaultFunction": "db.generic.aggregate.sum",
                                        "type": {
                                            "java.lang.Integer": "numeric"
                                        }
                                    },
                                    "source": {
                                        "alias": "source",
                                        "fullyQualifiedColumn": "travel_details.source",
                                        "id": "13113",
                                        "defaultFunction": "db.generic.groupBy.group",
                                        "type": {
                                            "java.lang.String": "text"
                                        }
                                    },
                                    "destination_id": {
                                        "alias": "destination_id",
                                        "fullyQualifiedColumn": "travel_details.destination_id",
                                        "id": "13114",
                                        "defaultFunction": "db.generic.aggregate.sum",
                                        "type": {
                                            "java.lang.Integer": "numeric"
                                        }
                                    },
                                    "destination": {
                                        "alias": "destination",
                                        "fullyQualifiedColumn": "travel_details.destination",
                                        "id": "13115",
                                        "defaultFunction": "db.generic.groupBy.group",
                                        "type": {
                                            "java.lang.String": "text"
                                        }
                                    },
                                    "travel_cost": {
                                        "alias": "travel_cost",
                                        "fullyQualifiedColumn": "travel_details.travel_cost",
                                        "id": "13116",
                                        "defaultFunction": "db.generic.aggregate.sum",
                                        "type": {
                                            "java.lang.Integer": "numeric"
                                        }
                                    },
                                    "mode_of_payment": {
                                        "alias": "mode_of_payment",
                                        "fullyQualifiedColumn": "travel_details.mode_of_payment",
                                        "id": "13117",
                                        "defaultFunction": "db.generic.groupBy.group",
                                        "type": {
                                            "java.lang.String": "text"
                                        }
                                    },
                                    "booking_platform": {
                                        "alias": "booking_platform",
                                        "fullyQualifiedColumn": "travel_details.booking_platform",
                                        "id": "13118",
                                        "defaultFunction": "db.generic.groupBy.group",
                                        "type": {
                                            "java.lang.String": "text"
                                        }
                                    },
                                    "travelled_by": {
                                        "alias": "travelled_by",
                                        "fullyQualifiedColumn": "travel_details.travelled_by",
                                        "id": "13119",
                                        "defaultFunction": "db.generic.aggregate.sum",
                                        "type": {
                                            "java.lang.Integer": "numeric"
                                        }
                                    }
                                },
                                "name": "travel_details",
                                "cacheId": "8a28627d07d04ef096d9935f12e0c7e9"
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
                        "metadataName": "Metadata_new",
                        "metadataDir": "Manish_18"
                    }
                },
                "state": {
                    "metadataLoading": false,
                    "fields": [
                        {
                            "column": "travel_details.travel_id",
                            "label": "sum_travel_id",
                            "id": "d6638a5f-e4ce-4f3f-9681-1b1668f05315",
                            "type": {
                                "backendDataType": "java.lang.Integer",
                                "dataType": "numeric"
                            },
                            "autogen_alias": "sum_travel_id",
                            "isNormalTable": true,
                            "tableAlias": "travel_details",
                            "aggregate": [
                                "db.generic.aggregate.sum"
                            ],
                            "orderByColumn": false,
                            "showOrderByColumn": false,
                            "addedAs": "row",
                            "floatingType": "",
                            "functionsDefinition": "",
                            "applyBeforeAggregate": false,
                            "hiddenIncludeInResultSet": false,
                            "metaDataAlias": "travel_id"
                        },
                        {
                            "column": "travel_details.booking_platform",
                            "label": "booking_platform",
                            "id": "4a2bc99a-1743-4ca4-bdc2-7315d682be00",
                            "type": {
                                "backendDataType": "java.lang.String",
                                "dataType": "text"
                            },
                            "autogen_alias": "booking_platform",
                            "isNormalTable": true,
                            "tableAlias": "travel_details",
                            "groupBy": [
                                "db.generic.groupBy.group"
                            ],
                            "orderByColumn": false,
                            "showOrderByColumn": false,
                            "addedAs": "column",
                            "floatingType": "discrete",
                            "functionsDefinition": "",
                            "applyBeforeAggregate": false,
                            "hiddenIncludeInResultSet": false,
                            "metaDataAlias": "booking_platform"
                        }
                    ],
                    "filters": [],
                    "marksList": [
                        {
                            "value": "_all_",
                            "id": "5334a1ee-4634-4cb7-bdea-d8604c5426ad",
                            "subVizType": "",
                            "color": {
                                "fields": []
                            },
                            "size": {
                                "fields": []
                            },
                            "label": {
                                "fields": []
                            },
                            "tooltip": {
                                "fields": []
                            },
                            "shape": {
                                "fields": []
                            },
                            "detail": {
                                "fields": []
                            }
                        },
                        {
                            "value": "sum_travel_id",
                            "id": "d6638a5f-e4ce-4f3f-9681-1b1668f05315",
                            "subVizType": "",
                            "color": {
                                "fields": []
                            },
                            "size": {
                                "fields": []
                            },
                            "label": {
                                "fields": []
                            },
                            "tooltip": {
                                "fields": []
                            },
                            "shape": {
                                "fields": []
                            },
                            "detail": {
                                "fields": []
                            }
                        }
                    ],
                    "activeMark": "5334a1ee-4634-4cb7-bdea-d8604c5426ad",
                    "activeTool": "6",
                    "scripts": [
                        {
                            "id": "pre-execution",
                            "value": "",
                            "title": "Pre Execution"
                        },
                        {
                            "id": "pre-fetch",
                            "value": "",
                            "title": "Pre Fetch"
                        },
                        {
                            "id": "post-fetch",
                            "value": "",
                            "title": "Post Fetch"
                        },
                        {
                            "id": "post-execution",
                            "value": "",
                            "title": "Post Execution"
                        }
                    ],
                    "selectedScript": "pre-execution",
                    "styles": "",
                    "options": {
                        "limitBy": 1000,
                        "sample": "sample",
                        "prependTableNameToAlias": false
                    },
                    "interactiveMode": true,
                    "drillDown": true,
                    "drillThrough": false,
                    "drillDownList": [],
                    "currentDrillDown": "",
                    "drillThroughList": [],
                    "toolbarConfig": {
                        "selectable": false
                    },
                    "selectedType": "CrossTab",
                    "customStyles": "",
                    "customScripts": [],
                    "analytics": [
                        {
                            "value": false,
                            "key": "subTotals",
                            "label": "Row Sub Totals"
                        }
                    ],
                    "properties": {
                        "title": {
                            "show": false,
                            "value": "",
                            "padding": 0,
                            "fontSize": 32,
                            "fontColor": {
                                "a": 1,
                                "b": 0,
                                "g": 0,
                                "r": 0
                            },
                            "alignment": "center",
                            "position": "top"
                        },
                        "subTitle": {
                            "show": false,
                            "value": "",
                            "padding": 0,
                            "fontSize": 24,
                            "fontColor": {
                                "a": 1,
                                "b": 0,
                                "g": 0,
                                "r": 0
                            },
                            "alignment": "center",
                            "position": "top"
                        },
                        "format": {
                            "formatFields": [],
                            "formatDatatype": "",
                            "activeFieldId": ""
                        },
                        "cache": {
                            "isCacheEnabled": false,
                            "interval": "00:00:01"
                        },
                        "bar": {
                            "barType": "stacked"
                        },
                        "legend": {
                            "legendPosition": "right"
                        },
                        "formatColor": {
                            "defaultColor": {
                                "r": 84,
                                "g": 108,
                                "b": 230,
                                "a": 1
                            },
                            "showAll": false,
                            "dataColors": [],
                            "formatColorStyle": "",
                            "formatColorField": "",
                            "minimum": {
                                "r": 183,
                                "g": 192,
                                "b": 232,
                                "a": 1
                            },
                            "maximum": {
                                "r": 84,
                                "g": 108,
                                "b": 230,
                                "a": 1
                            }
                        }
                    },
                    "showHiddenColumns": false,
                    "showHiddenRows": false,
                    "database": "HIUSER"
                }
            }
        }
    }
    if (dir === "manish" && file === "vf.hr") {
        return {
            "status": 1,
            "response": {
                "data": {
                    "canvas": {
                        "columns": [
                            {
                                "column": "travel_details.booking_platform",
                                "columnID": "1680",
                                "label": "booking_platform",
                                "id": "78d99bd8-53c6-4a94-8c52-db48368a911b",
                                "type": {
                                    "backendDataType": "java.lang.String",
                                    "dataType": "text"
                                },
                                "autogen_alias": "booking_platform",
                                "isNormalTable": true,
                                "tableAlias": "travel_details",
                                "groupBy": [
                                    "db.generic.groupBy.group"
                                ],
                                "orderByColumn": false,
                                "showOrderByColumn": false,
                                "addedAs": "column",
                                "floatingType": "discrete",
                                "functionsDefinition": "",
                                "applyBeforeAggregate": false,
                                "hiddenIncludeInResultSet": false,
                                "metaDataAlias": "booking_platform",
                                "databaseName": "HIUSER",
                                "geographicType": "",
                                "isView": false
                            },
                            {
                                "column": "travel_details.travel_cost",
                                "columnID": "1678",
                                "label": "sum_travel_cost",
                                "id": "0c7320bd-7c40-4ef0-b796-c841074ecf64",
                                "type": {
                                    "backendDataType": "java.lang.Integer",
                                    "dataType": "numeric"
                                },
                                "autogen_alias": "sum_travel_cost",
                                "isNormalTable": true,
                                "tableAlias": "travel_details",
                                "aggregate": [
                                    "db.generic.aggregate.sum"
                                ],
                                "orderByColumn": false,
                                "showOrderByColumn": false,
                                "addedAs": "row",
                                "floatingType": "",
                                "functionsDefinition": "",
                                "applyBeforeAggregate": false,
                                "hiddenIncludeInResultSet": false,
                                "metaDataAlias": "travel_cost",
                                "databaseName": "HIUSER",
                                "geographicType": "",
                                "isView": false
                            }
                        ]
                    },
                    "reportName": "custom_vf",
                    "metadata": {
                        "location": "Manish_05",
                        "metadataFileName": "Metadata.metadata",
                        "data": {
                            "classifier": "db.generic",
                            "name": "HIUSER",
                            "dataSource": {
                                "sync": false,
                                "id": "1400",
                                "catSchemaPredicted": false,
                                "catalog": "",
                                "schema": "HIUSER",
                                "type": "dynamicDataSource",
                                "baseType": "global.jdbc",
                                "dbId": "1302"
                            },
                            "uniqueId": "Metadata",
                            "tables": {
                                "dimdate": {
                                    "id": "1307",
                                    "alias": "dimdate",
                                    "columns": {
                                        "dim_id": {
                                            "alias": "dim_id",
                                            "fullyQualifiedColumn": "dimdate.dim_id",
                                            "id": "1644",
                                            "defaultFunction": "db.generic.aggregate.sum",
                                            "type": {
                                                "java.lang.Integer": "numeric"
                                            }
                                        },
                                        "fiscal_year": {
                                            "alias": "fiscal_year",
                                            "fullyQualifiedColumn": "dimdate.fiscal_year",
                                            "id": "1645",
                                            "defaultFunction": "db.generic.groupBy.group",
                                            "type": {
                                                "java.sql.Date": "date"
                                            }
                                        },
                                        "modified_date": {
                                            "alias": "modified_date",
                                            "fullyQualifiedColumn": "dimdate.modified_date",
                                            "id": "1646",
                                            "defaultFunction": "db.generic.groupBy.group",
                                            "type": {
                                                "java.sql.Timestamp": "dateTime"
                                            }
                                        },
                                        "date_key": {
                                            "alias": "date_key",
                                            "fullyQualifiedColumn": "dimdate.date_key",
                                            "id": "1647",
                                            "defaultFunction": "db.generic.groupBy.group",
                                            "type": {
                                                "java.lang.String": "text"
                                            }
                                        },
                                        "day_number": {
                                            "alias": "day_number",
                                            "fullyQualifiedColumn": "dimdate.day_number",
                                            "id": "1648",
                                            "defaultFunction": "db.generic.groupBy.group",
                                            "type": {
                                                "java.lang.String": "text"
                                            }
                                        },
                                        "fiscal_month_name": {
                                            "alias": "fiscal_month_name",
                                            "fullyQualifiedColumn": "dimdate.fiscal_month_name",
                                            "id": "1649",
                                            "defaultFunction": "db.generic.groupBy.group",
                                            "type": {
                                                "java.lang.String": "text"
                                            }
                                        },
                                        "fiscal_month_label": {
                                            "alias": "fiscal_month_label",
                                            "fullyQualifiedColumn": "dimdate.fiscal_month_label",
                                            "id": "1650",
                                            "defaultFunction": "db.generic.groupBy.group",
                                            "type": {
                                                "java.lang.String": "text"
                                            }
                                        },
                                        "created_date": {
                                            "alias": "created_date",
                                            "fullyQualifiedColumn": "dimdate.created_date",
                                            "id": "1651",
                                            "defaultFunction": "db.generic.groupBy.group",
                                            "type": {
                                                "java.lang.String": "text"
                                            }
                                        },
                                        "created_time": {
                                            "alias": "created_time",
                                            "fullyQualifiedColumn": "dimdate.created_time",
                                            "id": "1652",
                                            "defaultFunction": "db.generic.groupBy.group",
                                            "type": {
                                                "java.lang.String": "text"
                                            }
                                        },
                                        "rating": {
                                            "alias": "rating",
                                            "fullyQualifiedColumn": "dimdate.rating",
                                            "id": "1653",
                                            "defaultFunction": "db.generic.groupBy.group",
                                            "type": {
                                                "java.lang.String": "text"
                                            }
                                        }
                                    },
                                    "name": "dimdate",
                                    "cacheId": "4ac5d9f68b58bd7c0d179146e46795be"
                                },
                                "employee_details": {
                                    "id": "1308",
                                    "alias": "employee_details",
                                    "columns": {
                                        "employee_id": {
                                            "alias": "employee_id",
                                            "fullyQualifiedColumn": "employee_details.employee_id",
                                            "id": "1654",
                                            "defaultFunction": "db.generic.aggregate.sum",
                                            "type": {
                                                "java.lang.Integer": "numeric"
                                            }
                                        },
                                        "employee_name": {
                                            "alias": "employee_name",
                                            "fullyQualifiedColumn": "employee_details.employee_name",
                                            "id": "1655",
                                            "defaultFunction": "db.generic.groupBy.group",
                                            "type": {
                                                "java.lang.String": "text"
                                            }
                                        },
                                        "age": {
                                            "alias": "age",
                                            "fullyQualifiedColumn": "employee_details.age",
                                            "id": "1656",
                                            "defaultFunction": "db.generic.aggregate.sum",
                                            "type": {
                                                "java.lang.Integer": "numeric"
                                            }
                                        },
                                        "address": {
                                            "alias": "address",
                                            "fullyQualifiedColumn": "employee_details.address",
                                            "id": "1657",
                                            "defaultFunction": "db.generic.groupBy.group",
                                            "type": {
                                                "java.lang.String": "text"
                                            }
                                        }
                                    },
                                    "name": "employee_details",
                                    "cacheId": "4e1fd245f4d13b77be423a43f01d80b2"
                                },
                                "geo_cordinates": {
                                    "id": "1309",
                                    "alias": "geo_cordinates",
                                    "columns": {
                                        "location_id": {
                                            "alias": "location_id",
                                            "fullyQualifiedColumn": "geo_cordinates.location_id",
                                            "id": "1658",
                                            "defaultFunction": "db.generic.aggregate.sum",
                                            "type": {
                                                "java.lang.Integer": "numeric"
                                            }
                                        },
                                        "location": {
                                            "alias": "location",
                                            "fullyQualifiedColumn": "geo_cordinates.location",
                                            "id": "1659",
                                            "defaultFunction": "db.generic.groupBy.group",
                                            "type": {
                                                "java.lang.String": "text"
                                            }
                                        },
                                        "latitude": {
                                            "alias": "latitude",
                                            "fullyQualifiedColumn": "geo_cordinates.latitude",
                                            "id": "1660",
                                            "defaultFunction": "db.generic.aggregate.sum",
                                            "type": {
                                                "java.lang.Double": "numeric"
                                            }
                                        },
                                        "longitude": {
                                            "alias": "longitude",
                                            "fullyQualifiedColumn": "geo_cordinates.longitude",
                                            "id": "1661",
                                            "defaultFunction": "db.generic.aggregate.sum",
                                            "type": {
                                                "java.lang.Double": "numeric"
                                            }
                                        }
                                    },
                                    "name": "geo_cordinates",
                                    "cacheId": "be534112989b616b194bc59c2fb25a42"
                                },
                                "meeting_details": {
                                    "id": "1310",
                                    "alias": "meeting_details",
                                    "columns": {
                                        "meeting_id": {
                                            "alias": "meeting_id",
                                            "fullyQualifiedColumn": "meeting_details.meeting_id",
                                            "id": "1662",
                                            "defaultFunction": "db.generic.aggregate.sum",
                                            "type": {
                                                "java.lang.Integer": "numeric"
                                            }
                                        },
                                        "meeting_date": {
                                            "alias": "meeting_date",
                                            "fullyQualifiedColumn": "meeting_details.meeting_date",
                                            "id": "1663",
                                            "defaultFunction": "db.generic.groupBy.group",
                                            "type": {
                                                "java.sql.Timestamp": "dateTime"
                                            }
                                        },
                                        "meeting_by": {
                                            "alias": "meeting_by",
                                            "fullyQualifiedColumn": "meeting_details.meeting_by",
                                            "id": "1664",
                                            "defaultFunction": "db.generic.aggregate.sum",
                                            "type": {
                                                "java.lang.Integer": "numeric"
                                            }
                                        },
                                        "client_name": {
                                            "alias": "client_name",
                                            "fullyQualifiedColumn": "meeting_details.client_name",
                                            "id": "1665",
                                            "defaultFunction": "db.generic.groupBy.group",
                                            "type": {
                                                "java.lang.String": "text"
                                            }
                                        },
                                        "meeting_purpose": {
                                            "alias": "meeting_purpose",
                                            "fullyQualifiedColumn": "meeting_details.meeting_purpose",
                                            "id": "1666",
                                            "defaultFunction": "db.generic.groupBy.group",
                                            "type": {
                                                "java.lang.String": "text"
                                            }
                                        },
                                        "meeting_impact": {
                                            "alias": "meeting_impact",
                                            "fullyQualifiedColumn": "meeting_details.meeting_impact",
                                            "id": "1667",
                                            "defaultFunction": "db.generic.groupBy.group",
                                            "type": {
                                                "java.lang.String": "text"
                                            }
                                        },
                                        "meet_cancellation_status": {
                                            "alias": "meet_cancellation_status",
                                            "fullyQualifiedColumn": "meeting_details.meet_cancellation_status",
                                            "id": "1668",
                                            "defaultFunction": "db.generic.groupBy.group",
                                            "type": {
                                                "java.lang.String": "text"
                                            }
                                        },
                                        "cancellation_reason": {
                                            "alias": "cancellation_reason",
                                            "fullyQualifiedColumn": "meeting_details.cancellation_reason",
                                            "id": "1669",
                                            "defaultFunction": "db.generic.groupBy.group",
                                            "type": {
                                                "java.lang.String": "text"
                                            }
                                        }
                                    },
                                    "name": "meeting_details",
                                    "cacheId": "9645c648a1c0dbeec1287aaf1e996db3"
                                },
                                "travel_details": {
                                    "id": "1311",
                                    "alias": "travel_details",
                                    "columns": {
                                        "travel_id": {
                                            "alias": "travel_id",
                                            "fullyQualifiedColumn": "travel_details.travel_id",
                                            "id": "1670",
                                            "defaultFunction": "db.generic.aggregate.sum",
                                            "type": {
                                                "java.lang.Integer": "numeric"
                                            }
                                        },
                                        "travel_date": {
                                            "alias": "travel_date",
                                            "fullyQualifiedColumn": "travel_details.travel_date",
                                            "id": "1671",
                                            "defaultFunction": "db.generic.groupBy.group",
                                            "type": {
                                                "java.sql.Timestamp": "dateTime"
                                            }
                                        },
                                        "travel_type": {
                                            "alias": "travel_type",
                                            "fullyQualifiedColumn": "travel_details.travel_type",
                                            "id": "1672",
                                            "defaultFunction": "db.generic.groupBy.group",
                                            "type": {
                                                "java.lang.String": "text"
                                            }
                                        },
                                        "travel_medium": {
                                            "alias": "travel_medium",
                                            "fullyQualifiedColumn": "travel_details.travel_medium",
                                            "id": "1673",
                                            "defaultFunction": "db.generic.groupBy.group",
                                            "type": {
                                                "java.lang.String": "text"
                                            }
                                        },
                                        "source_id": {
                                            "alias": "source_id",
                                            "fullyQualifiedColumn": "travel_details.source_id",
                                            "id": "1674",
                                            "defaultFunction": "db.generic.aggregate.sum",
                                            "type": {
                                                "java.lang.Integer": "numeric"
                                            }
                                        },
                                        "source": {
                                            "alias": "source",
                                            "fullyQualifiedColumn": "travel_details.source",
                                            "id": "1675",
                                            "defaultFunction": "db.generic.groupBy.group",
                                            "type": {
                                                "java.lang.String": "text"
                                            }
                                        },
                                        "destination_id": {
                                            "alias": "destination_id",
                                            "fullyQualifiedColumn": "travel_details.destination_id",
                                            "id": "1676",
                                            "defaultFunction": "db.generic.aggregate.sum",
                                            "type": {
                                                "java.lang.Integer": "numeric"
                                            }
                                        },
                                        "destination": {
                                            "alias": "destination",
                                            "fullyQualifiedColumn": "travel_details.destination",
                                            "id": "1677",
                                            "defaultFunction": "db.generic.groupBy.group",
                                            "type": {
                                                "java.lang.String": "text"
                                            }
                                        },
                                        "travel_cost": {
                                            "alias": "travel_cost",
                                            "fullyQualifiedColumn": "travel_details.travel_cost",
                                            "id": "1678",
                                            "defaultFunction": "db.generic.aggregate.sum",
                                            "type": {
                                                "java.lang.Integer": "numeric"
                                            }
                                        },
                                        "mode_of_payment": {
                                            "alias": "mode_of_payment",
                                            "fullyQualifiedColumn": "travel_details.mode_of_payment",
                                            "id": "1679",
                                            "defaultFunction": "db.generic.groupBy.group",
                                            "type": {
                                                "java.lang.String": "text"
                                            }
                                        },
                                        "booking_platform": {
                                            "alias": "booking_platform",
                                            "fullyQualifiedColumn": "travel_details.booking_platform",
                                            "id": "1680",
                                            "defaultFunction": "db.generic.groupBy.group",
                                            "type": {
                                                "java.lang.String": "text"
                                            }
                                        },
                                        "travelled_by": {
                                            "alias": "travelled_by",
                                            "fullyQualifiedColumn": "travel_details.travelled_by",
                                            "id": "1681",
                                            "defaultFunction": "db.generic.aggregate.sum",
                                            "type": {
                                                "java.lang.Integer": "numeric"
                                            }
                                        }
                                    },
                                    "name": "travel_details",
                                    "cacheId": "8a28627d07d04ef096d9935f12e0c7e9"
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
                            "metadataName": "Metadata",
                            "metadataDir": "Manish_05",
                            "databaseName": "HIUSER"
                        },
                        "databaseName": "HIUSER"
                    },
                    "state": {
                        "metadataLoading": false,
                        "hreportLoading": false,
                        "fields": [
                            {
                                "column": "travel_details.booking_platform",
                                "columnID": "1680",
                                "label": "booking_platform",
                                "id": "78d99bd8-53c6-4a94-8c52-db48368a911b",
                                "type": {
                                    "backendDataType": "java.lang.String",
                                    "dataType": "text"
                                },
                                "autogen_alias": "booking_platform",
                                "isNormalTable": true,
                                "tableAlias": "travel_details",
                                "groupBy": [
                                    "db.generic.groupBy.group"
                                ],
                                "orderByColumn": false,
                                "showOrderByColumn": false,
                                "addedAs": "column",
                                "floatingType": "discrete",
                                "functionsDefinition": "",
                                "applyBeforeAggregate": false,
                                "hiddenIncludeInResultSet": false,
                                "metaDataAlias": "booking_platform",
                                "databaseName": "HIUSER",
                                "geographicType": "",
                                "isView": false
                            },
                            {
                                "column": "travel_details.travel_cost",
                                "columnID": "1678",
                                "label": "sum_travel_cost",
                                "id": "0c7320bd-7c40-4ef0-b796-c841074ecf64",
                                "type": {
                                    "backendDataType": "java.lang.Integer",
                                    "dataType": "numeric"
                                },
                                "autogen_alias": "sum_travel_cost",
                                "isNormalTable": true,
                                "tableAlias": "travel_details",
                                "aggregate": [
                                    "db.generic.aggregate.sum"
                                ],
                                "orderByColumn": false,
                                "showOrderByColumn": false,
                                "addedAs": "row",
                                "floatingType": "",
                                "functionsDefinition": "",
                                "applyBeforeAggregate": false,
                                "hiddenIncludeInResultSet": false,
                                "metaDataAlias": "travel_cost",
                                "databaseName": "HIUSER",
                                "geographicType": "",
                                "isView": false
                            }
                        ],
                        "filters": [],
                        "marksList": [
                            {
                                "value": "_all_",
                                "id": "72e18531-2d83-49de-80c0-38a8df77a465",
                                "subVizType": "",
                                "color": {
                                    "fields": []
                                },
                                "size": {
                                    "fields": []
                                },
                                "label": {
                                    "fields": []
                                },
                                "tooltip": {
                                    "fields": []
                                },
                                "shape": {
                                    "fields": []
                                },
                                "detail": {
                                    "fields": []
                                }
                            },
                            {
                                "value": "sum_travel_cost",
                                "id": "0c7320bd-7c40-4ef0-b796-c841074ecf64",
                                "subVizType": "",
                                "color": {
                                    "fields": []
                                },
                                "size": {
                                    "fields": []
                                },
                                "label": {
                                    "fields": []
                                },
                                "tooltip": {
                                    "fields": []
                                },
                                "shape": {
                                    "fields": []
                                },
                                "detail": {
                                    "fields": []
                                }
                            }
                        ],
                        "activeMark": "72e18531-2d83-49de-80c0-38a8df77a465",
                        "activeTool": "1",
                        "scripts": [
                            {
                                "id": "pre-execution",
                                "value": "",
                                "title": "Pre Execution"
                            },
                            {
                                "id": "pre-fetch",
                                "value": "",
                                "title": "Pre Fetch"
                            },
                            {
                                "id": "post-fetch",
                                "value": "",
                                "title": "Post Fetch"
                            },
                            {
                                "id": "post-execution",
                                "value": "",
                                "title": "Post Execution"
                            }
                        ],
                        "selectedScript": "pre-execution",
                        "styles": "",
                        "stylesId": "hi-report-d385c838",
                        "savedStyles": "",
                        "options": {
                            "limitBy": 1000,
                            "sample": "sample",
                            "prependTableNameToAlias": false
                        },
                        "interactiveMode": false,
                        "drillDown": false,
                        "drillThrough": false,
                        "drillDownList": [],
                        "currentDrillDown": "",
                        "drillThroughList": [],
                        "toolbarConfig": {
                            "selectable": false
                        },
                        "selectedType": "Table",
                        "customStyles": "",
                        "customScripts": [],
                        "analytics": [
                            {
                                "value": false,
                                "key": "subTotals",
                                "label": "Row Sub Totals"
                            }
                        ],
                        "properties": {},
                        "showHiddenColumns": false,
                        "showHiddenRows": false,
                        "geoJsonData": {},
                        "isAborted": false,
                        "referenceLineList": [
                            {
                                "display": "All",
                                "id": "cc837108-a833-4d35-a890-8034a9527103",
                                "referenceType": "Line",
                                "value": "",
                                "enabled": false,
                                "isStatic": true
                            },
                            {
                                "display": "sum_travel_cost",
                                "id": "0c7320bd-7c40-4ef0-b796-c841074ecf64",
                                "referenceType": "Line",
                                "value": "",
                                "enabled": false,
                                "isStatic": true
                            }
                        ],
                        "customChart": {
                            "selected": true,
                            "drawer": false,
                            "code": "function DrawTable() {\r\n  const {Table} = components\r\n  const dataSource = [\r\n    {\r\n      key: '1',\r\n      name: 'Mike',\r\n      age: 32,\r\n      address: '10 Downing Street',\r\n    },\r\n    {\r\n      key: '2',\r\n      name: 'John',\r\n      age: 42,\r\n      address: '10 Downing Street',\r\n    },\r\n  ];\r\n\r\n  const columns = [\r\n    {\r\n      title: 'Name',\r\n      dataIndex: 'name',\r\n      key: 'name',\r\n    },\r\n    {\r\n      title: 'Age',\r\n      dataIndex: 'age',\r\n      key: 'age',\r\n    },\r\n    {\r\n      title: 'Address',\r\n      dataIndex: 'address',\r\n      key: 'address',\r\n    },\r\n  ];\r\n\r\n  return <Table dataSource={dataSource} columns={columns} />;\r\n}",
                            "applied": true
                        },
                        "database": "HIUSER",
                        "appliedDbfs": {}
                    }
                }
            }
        }
    }
}
export const getReportData = formdata => {
    let result = {
        "status": 1,
        "response": {
            data: []
        }
    }
    const formdata_case_1 = {
        "location": "TestFold",
        "metadataFileName": "Metadata_1.metadata",
        "columns": [
            {
                "column": "HIUSER.travel_details.booking_platform",
                "alias": "booking_platform",
                "floatingType": "discrete"
            }
        ],
        "functions": {
            "groupBy": [
                {
                    "column": "booking_platform",
                    "custom": true
                }
            ]
        },
        "limitBy": 10,
        "prependTableNameToAlias": false
    }
    if (isEqual(formdata, formdata_case_1)) {
        result = {
            "status": 1,
            "response": {
                "data": [
                    {
                        "booking_platform": "Agent"
                    },
                    {
                        "booking_platform": "Makemytrip"
                    },
                    {
                        "booking_platform": "Website"
                    }
                ],
                "metadata": [
                    {
                        "1": {
                            "name": "booking_platform",
                            "type": "text"
                        }
                    },
                    {
                        "rows": 3
                    }
                ]
            },
            "lastModified": 1665553597243
        }
    }
    const formdata_case_2 = {
        "location": "TestFold",
        "metadataFileName": "Metadata_1.metadata",
        "columns": [
            {
                "column": "HIUSER.travel_details.booking_platform",
                "alias": "booking_platform",
                "floatingType": "discrete"
            },
            {
                "column": "HIUSER.travel_details.mode_of_payment",
                "alias": "mode_of_payment",
                "floatingType": "discrete"
            }
        ],
        "functions": {
            "groupBy": [
                {
                    "column": "booking_platform",
                    "custom": true
                },
                {
                    "column": "mode_of_payment",
                    "custom": true
                }
            ]
        },
        "filters": [
            {
                "values": [
                    "'Agent')"
                ],
                "mode": "auto",
                "operator": "AND",
                "dataType": "java.lang.String",
                "customCondition": " IN (",
                "encloseInQuotes": false,
                "alias": "booking_platform",
                "label": "booking_platform",
                "isCustomValue": true,
                "column": "HIUSER.travel_details.booking_platform",
                "id": 0,
                "condition": "CUSTOM"
            }
        ],
        "customFilterExpression": " ${0} ",
        "limitBy": 10,
        "prependTableNameToAlias": false
    }
    if (isEqual(formdata, formdata_case_2)) {
        result = {
            "status": 1,
            "response": {
                "data": [
                    {
                        "booking_platform": "Agent",
                        "mode_of_payment": "Cash"
                    },
                    {
                        "booking_platform": "Agent",
                        "mode_of_payment": "Cheque"
                    },
                    {
                        "booking_platform": "Agent",
                        "mode_of_payment": "Credit"
                    },
                    {
                        "booking_platform": "Agent",
                        "mode_of_payment": "Net Banking"
                    }
                ],
                "metadata": [
                    {
                        "1": {
                            "name": "booking_platform",
                            "type": "text"
                        },
                        "2": {
                            "name": "mode_of_payment",
                            "type": "text"
                        }
                    },
                    {
                        "rows": 4
                    }
                ]
            }
        }
    }
    const formdata_case_3 = {
        "location": "TestFold",
        "metadataFileName": "Metadata_1.metadata",
        "columns": [
            {
                "column": "HIUSER.travel_details.booking_platform",
                "alias": "booking_platform",
                "floatingType": "discrete"
            },
            {
                "column": "HIUSER.travel_details.mode_of_payment",
                "alias": "mode_of_payment",
                "floatingType": "discrete"
            }
        ],
        "functions": {
            "groupBy": [
                {
                    "column": "booking_platform",
                    "custom": true
                },
                {
                    "column": "mode_of_payment",
                    "custom": true
                }
            ]
        },
        "filters": [
            {
                "values": [
                    "'Website')"
                ],
                "mode": "auto",
                "operator": "AND",
                "dataType": "java.lang.String",
                "customCondition": " IN (",
                "encloseInQuotes": false,
                "alias": "booking_platform",
                "label": "booking_platform",
                "isCustomValue": true,
                "column": "HIUSER.travel_details.booking_platform",
                "id": 0,
                "condition": "CUSTOM"
            }
        ],
        "customFilterExpression": " ${0} ",
        "limitBy": 10,
        "prependTableNameToAlias": false
    }
    if (isEqual(formdata, formdata_case_3)) {
        result = {
            "status": 1,
            "response": {
                "data": [
                    {
                        "booking_platform": "Website",
                        "mode_of_payment": "Cash"
                    },
                    {
                        "booking_platform": "Website",
                        "mode_of_payment": "Cheque"
                    },
                    {
                        "booking_platform": "Website",
                        "mode_of_payment": "Credit"
                    },
                    {
                        "booking_platform": "Website",
                        "mode_of_payment": "Net Banking"
                    }
                ],
                "metadata": [
                    {
                        "1": {
                            "name": "booking_platform",
                            "type": "text"
                        },
                        "2": {
                            "name": "mode_of_payment",
                            "type": "text"
                        }
                    },
                    {
                        "rows": 4
                    }
                ]
            }
        }
    }
    const formdata_case_4 = {
        "location": "TestFold",
        "metadataFileName": "Metadata_1.metadata",
        "columns": [
            {
                "column": "HIUSER.travel_details.travel_medium",
                "alias": "travel_medium",
                "floatingType": "discrete"
            }
        ],
        "functions": {
            "groupBy": [
                {
                    "column": "travel_medium",
                    "custom": true
                }
            ]
        },
        "filters": [
            {
                "values": [
                    "'Website')"
                ],
                "mode": "auto",
                "operator": "AND",
                "dataType": "java.lang.String",
                "customCondition": " IN (",
                "encloseInQuotes": false,
                "alias": "booking_platform",
                "label": "booking_platform",
                "isCustomValue": true,
                "column": "HIUSER.travel_details.booking_platform",
                "id": 0,
                "condition": "CUSTOM"
            }
        ],
        "customFilterExpression": " ${0} ",
        "limitBy": 10,
        "prependTableNameToAlias": false
    }
    if (isEqual(formdata, formdata_case_4)) {
        result = {
            "status": 1,
            "response": {
                "data": [
                    {
                        "travel_medium": "Bus"
                    },
                    {
                        "travel_medium": "Cab"
                    },
                    {
                        "travel_medium": "Flight"
                    },
                    {
                        "travel_medium": "Misc"
                    },
                    {
                        "travel_medium": "Train"
                    }
                ],
                "metadata": [
                    {
                        "1": {
                            "name": "travel_medium",
                            "type": "text"
                        }
                    },
                    {
                        "rows": 5
                    }
                ]
            },
            "lastModified": 1665577456727
        }
    }
    const formdata_case_5 = {
        "location": "TestFold",
        "metadataFileName": "Metadata_1.metadata",
        "columns": [
            {
                "column": "HIUSER.travel_details.booking_platform",
                "alias": "booking_platform",
                "floatingType": "discrete"
            },
            {
                "column": "HIUSER.travel_details.mode_of_payment",
                "alias": "mode_of_payment",
                "floatingType": "discrete"
            }
        ],
        "functions": {
            "groupBy": [
                {
                    "column": "booking_platform",
                    "custom": true
                },
                {
                    "column": "mode_of_payment",
                    "custom": true
                }
            ]
        },
        "filters": [
            {
                "values": [
                    "'Makemytrip')"
                ],
                "mode": "auto",
                "operator": "AND",
                "dataType": "java.lang.String",
                "customCondition": " IN (",
                "encloseInQuotes": false,
                "alias": "booking_platform",
                "label": "booking_platform",
                "isCustomValue": true,
                "column": "HIUSER.travel_details.booking_platform",
                "id": 0,
                "condition": "CUSTOM"
            }
        ],
        "customFilterExpression": " ${0} ",
        "limitBy": 10,
        "prependTableNameToAlias": false
    }
    if (isEqual(formdata, formdata_case_5)) {
        result = {
            "status": 1,
            "response": {
                "data": [
                    {
                        "booking_platform": "Makemytrip",
                        "mode_of_payment": "Cash"
                    },
                    {
                        "booking_platform": "Makemytrip",
                        "mode_of_payment": "Cheque"
                    },
                    {
                        "booking_platform": "Makemytrip",
                        "mode_of_payment": "Credit"
                    },
                    {
                        "booking_platform": "Makemytrip",
                        "mode_of_payment": "Net Banking"
                    }
                ],
                "metadata": [
                    {
                        "1": {
                            "name": "booking_platform",
                            "type": "text"
                        },
                        "2": {
                            "name": "mode_of_payment",
                            "type": "text"
                        }
                    },
                    {
                        "rows": 4
                    }
                ]
            },
            "lastModified": 1666090727598
        }
    }
    const formdata_case_6 = {
        "location": "TestFold",
        "metadataFileName": "Metadata_1.metadata",
        "columns": [
            {
                "column": "HIUSER.travel_details.booking_platform",
                "alias": "booking_platform",
                "floatingType": "discrete"
            },
            {
                "column": "HIUSER.travel_details.travel_medium",
                "alias": "travel_medium",
                "floatingType": "discrete"
            }
        ],
        "functions": {
            "groupBy": [
                {
                    "column": "booking_platform",
                    "custom": true
                },
                {
                    "column": "travel_medium",
                    "custom": true
                }
            ]
        },
        "limitBy": 10,
        "prependTableNameToAlias": false
    }
    if (isEqual(formdata, formdata_case_6)) {
        result = {
            "status": 1,
            "response": {
                "data": [
                    {
                        "booking_platform": "Agent",
                        "travel_medium": "Bus"
                    },
                    {
                        "booking_platform": "Makemytrip",
                        "travel_medium": "Flight"
                    },
                    {
                        "booking_platform": "Website",
                        "travel_medium": "Flight"
                    },
                    {
                        "booking_platform": "Website",
                        "travel_medium": "Train"
                    },
                    {
                        "booking_platform": "Makemytrip",
                        "travel_medium": "Bus"
                    },
                    {
                        "booking_platform": "Website",
                        "travel_medium": "Bus"
                    },
                    {
                        "booking_platform": "Makemytrip",
                        "travel_medium": "Train"
                    },
                    {
                        "booking_platform": "Agent",
                        "travel_medium": "Cab"
                    },
                    {
                        "booking_platform": "Website",
                        "travel_medium": "Cab"
                    },
                    {
                        "booking_platform": "Agent",
                        "travel_medium": "Flight"
                    }
                ],
                "metadata": [
                    {
                        "1": {
                            "name": "booking_platform",
                            "type": "text"
                        },
                        "2": {
                            "name": "travel_medium",
                            "type": "text"
                        }
                    },
                    {
                        "rows": 10
                    }
                ]
            }
        }
    }
    const formdata_case_7 = {
        "location": "TestFold",
        "metadataFileName": "Metadata_1.metadata",
        "columns": [
            {
                "column": "HIUSER.travel_details.booking_platform",
                "alias": "booking_platform",
                "floatingType": "discrete"
            },
            {
                "column": "HIUSER.travel_details.travel_date",
                "alias": "travel_date",
                "floatingType": "discrete"
            }
        ],
        "functions": {
            "groupBy": [
                {
                    "column": "booking_platform",
                    "custom": true
                },
                {
                    "column": "travel_date",
                    "custom": true
                }
            ]
        },
        "limitBy": 10,
        "prependTableNameToAlias": false
    }
    if (isEqual(formdata, formdata_case_7)) {
        result = {
            "status": 1,
            "response": {
                "data": [
                    {
                        "booking_platform": "Agent",
                        "travel_date": "2015-03-09 11:38:00.0"
                    },
                    {
                        "booking_platform": "Agent",
                        "travel_date": "2015-11-09 11:06:00.0"
                    },
                    {
                        "booking_platform": "Agent",
                        "travel_date": "2015-02-12 16:47:00.0"
                    },
                    {
                        "booking_platform": "Agent",
                        "travel_date": "2015-08-12 16:57:00.0"
                    },
                    {
                        "booking_platform": "Agent",
                        "travel_date": "2015-03-17 13:38:00.0"
                    },
                    {
                        "booking_platform": "Agent",
                        "travel_date": "2015-08-17 14:25:00.0"
                    },
                    {
                        "booking_platform": "Agent",
                        "travel_date": "2015-09-19 13:32:00.0"
                    },
                    {
                        "booking_platform": "Agent",
                        "travel_date": "2015-10-22 15:23:00.0"
                    },
                    {
                        "booking_platform": "Agent",
                        "travel_date": "2015-10-21 16:39:00.0"
                    },
                    {
                        "booking_platform": "Agent",
                        "travel_date": "2015-04-01 09:45:00.0"
                    }
                ],
                "metadata": [
                    {
                        "1": {
                            "name": "booking_platform",
                            "type": "text"
                        },
                        "2": {
                            "name": "travel_date",
                            "type": "dateTime"
                        }
                    },
                    {
                        "rows": 10
                    }
                ]
            },
            "lastModified": 1668663680770
        }
    }
    const formdata_case_8 = {
        "location": "TestFold",
        "metadataFileName": "Metadata_1.metadata",
        "columns": [
            {
                "column": "HIUSER.travel_details.booking_platform",
                "alias": "booking_platform",
                "floatingType": "discrete"
            },
            {
                "column": "HIUSER.travel_details.travel_date",
                "alias": "travel_date",
                "floatingType": "discrete"
            }
        ],
        "functions": {
            "groupBy": [
                {
                    "column": "booking_platform",
                    "custom": true
                },
                {
                    "column": "travel_date",
                    "custom": true
                }
            ]
        },
        "filters": [
            {
                "values": [
                    "'2015-03-09 11:38:00.0')"
                ],
                "mode": "auto",
                "operator": "AND",
                "dataType": "java.lang.String",
                "customCondition": " IN (",
                "encloseInQuotes": false,
                "alias": "travel_date",
                "label": "travel_date",
                "isCustomValue": true,
                "column": "HIUSER.travel_details.travel_date",
                "id": 0,
                "condition": "CUSTOM"
            }
        ],
        "customFilterExpression": " ${0} ",
        "limitBy": 10,
        "prependTableNameToAlias": false
    }

    if (isEqual(formdata, formdata_case_8)) {
        result = {
            "status": 1,
            "response": {
                "data": [
                    {
                        "booking_platform": "Agent",
                        "travel_date": "2015-03-09 11:38:00.0"
                    }
                ],
                "metadata": [
                    {
                        "1": {
                            "name": "booking_platform",
                            "type": "text"
                        },
                        "2": {
                            "name": "travel_date",
                            "type": "dateTime"
                        }
                    },
                    {
                        "rows": 1
                    }
                ]
            },
            "lastModified": 1668665248393
        }
    }
    const formdata_case_9 = {
        "location": "Smoke_Testing/Sample_Metadata",
        "metadataFileName": "JSONMetadata.metadata",
        "columns": [
            {
                "column": "DRILL.dfs.hidw.primer-dataset.json.address",
                "alias": "address",
                "floatingType": "discrete"
            },
            {
                "column": "DRILL.dfs.hidw.primer-dataset.json.borough",
                "alias": "borough",
                "floatingType": "discrete"
            }
        ],
        "functions": {
            "groupBy": [
                {
                    "column": "borough",
                    "custom": true
                }
            ]
        },
        "limitBy": 10,
        "prependTableNameToAlias": false
    }
    // console.log("result.data", isEqual(formdata, formdata_case_9),formdata)

    if (isEqual(formdata, formdata_case_9)) {
        result = {
            "status": 1,
            "response": {
                "data": [
                    {
                        "address": {
                            "building": "1007",
                            "coord": [
                                -73.85608,
                                40.848446
                            ],
                            "street": "Morris Park Ave",
                            "zipcode": "10462"
                        },
                        "borough": "abcd"
                    },
                    {
                        "address": {
                            "building": "469",
                            "coord": [
                                -73.9617,
                                40.66294
                            ],
                            "street": "Flatbush Avenue",
                            "zipcode": "11225"
                        },
                        "borough": "abcd"
                    },
                    {
                        "address": {
                            "building": "351",
                            "coord": [
                                -73.98514,
                                40.767693
                            ],
                            "street": "West   57 Street",
                            "zipcode": "10019"
                        },
                        "borough": "abcd"
                    },
                    {
                        "address": {
                            "building": "2780",
                            "coord": [
                                -73.98242,
                                40.579506
                            ],
                            "street": "Stillwell Avenue",
                            "zipcode": "11224"
                        },
                        "borough": "abcd"
                    },
                    {
                        "address": {
                            "building": "97-22",
                            "coord": [
                                -73.860115,
                                40.731174
                            ],
                            "street": "63 Road",
                            "zipcode": "11374"
                        },
                        "borough": "abcd"
                    },
                    {
                        "address": {
                            "building": "8825",
                            "coord": [
                                -73.88039,
                                40.764313
                            ],
                            "street": "Astoria Boulevard",
                            "zipcode": "11369"
                        },
                        "borough": "abcd"
                    },
                    {
                        "address": {
                            "building": "2206",
                            "coord": [
                                -74.137726,
                                40.611958
                            ],
                            "street": "Victory Boulevard",
                            "zipcode": "10314"
                        },
                        "borough": "abcd"
                    },
                    {
                        "address": {
                            "building": "7114",
                            "coord": [
                                -73.90685,
                                40.619904
                            ],
                            "street": "Avenue U",
                            "zipcode": "11234"
                        },
                        "borough": "abcd"
                    },
                    {
                        "address": {
                            "building": "6409",
                            "coord": [
                                -74.00529,
                                40.628887
                            ],
                            "street": "11 Avenue",
                            "zipcode": "11219"
                        },
                        "borough": "abcd"
                    },
                    {
                        "address": {
                            "building": "1839",
                            "coord": [
                                -73.94826,
                                40.640827
                            ],
                            "street": "Nostrand Avenue",
                            "zipcode": "11226"
                        },
                        "borough": "abcd"
                    }
                ],
                "metadata": [
                    {
                        "1": {
                            "name": "address",
                            "type": "other"
                        },
                        "2": {
                            "name": "borough",
                            "type": "text"
                        }
                    },
                    {
                        "rows": 10
                    }
                ]
            }
        }
    }
    // console.log("result.data", result)

    const formdata_case_10 = {
        "location": "Manish_18",
        "metadataFileName": "Metadata_new.metadata",
        "columns": [
            {
                "column": "HIUSER.travel_details.travel_id",
                "alias": "sum_travel_id",
                "aggregate": true,
                "aggregateList": [
                    "db.generic.aggregate.sum"
                ],
                "floatingType": "discrete"
            },
            {
                "column": "HIUSER.travel_details.booking_platform",
                "alias": "booking_platform",
                "floatingType": "discrete"
            }
        ],
        "functions": {
            "aggregate": [
                {
                    "column": "HIUSER.travel_details.travel_id",
                    "function": "db.generic.aggregate.sum",
                    "alias": "sum_travel_id"
                }
            ],
            "groupBy": [
                {
                    "column": "booking_platform",
                    "custom": true
                }
            ]
        },
        "limitBy": 1000,
        "prependTableNameToAlias": false
    }
    if (isEqual(formdata, formdata_case_10)) {
        result = {
            "status": 1,
            "response": {
                "data": [
                    {
                        "sum_travel_id": 131223,
                        "booking_platform": "Agent"
                    },
                    {
                        "sum_travel_id": 135702,
                        "booking_platform": "Makemytrip"
                    },
                    {
                        "sum_travel_id": 234576,
                        "booking_platform": "Website"
                    }
                ],
                "metadata": [
                    {
                        "1": {
                            "name": "sum_travel_id",
                            "type": "numeric"
                        },
                        "2": {
                            "name": "booking_platform",
                            "type": "text"
                        }
                    },
                    {
                        "rows": 3
                    }
                ]
            },
            "lastModified": 1678790238711
        }
    }

    const custom_vf_formdata = {
        "location": "Manish_05",
        "metadataFileName": "Metadata.metadata",
        "columns": [
            {
                "column": {
                    "name": "HIUSER.travel_details.booking_platform",
                    "id": "1680"
                },
                "alias": "booking_platform",
                "floatingType": "discrete"
            },
            {
                "column": {
                    "name": "HIUSER.travel_details.travel_cost",
                    "id": "1678"
                },
                "alias": "sum_travel_cost",
                "aggregate": true,
                "aggregateList": [
                    "db.generic.aggregate.sum"
                ],
                "floatingType": "discrete"
            }
        ],
        "functions": {
            "aggregate": [
                {
                    "column": {
                        "name": "HIUSER.travel_details.travel_cost",
                        "id": "1678"
                    },
                    "function": "db.generic.aggregate.sum",
                    "alias": "sum_travel_cost"
                }
            ],
            "groupBy": [
                {
                    "column": "booking_platform",
                    "custom": true
                }
            ]
        },
        "limitBy": 10,
        "prependTableNameToAlias": false
    }

    if (isEqual(formdata, custom_vf_formdata)) {
        result = {
            "status": 1,
            "response": {
                "data": [
                    {
                        "booking_platform": "Agent",
                        "sum_travel_cost": 3641245
                    },
                    {
                        "booking_platform": "Makemytrip",
                        "sum_travel_cost": 6719588
                    },
                    {
                        "booking_platform": "Website",
                        "sum_travel_cost": 8173137
                    }
                ],
                "metadata": [
                    {
                        "1": {
                            "name": "booking_platform",
                            "type": "text"
                        },
                        "2": {
                            "name": "sum_travel_cost",
                            "type": "numeric"
                        }
                    },
                    {
                        "rows": 3
                    }
                ]
            },
            "lastModified": 1704708698857
        }
    }
    return result
}


