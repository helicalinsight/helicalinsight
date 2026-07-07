export const relativeDateFilters = [
    {
        "column": "travel_details.travel_date",
        "label": "travel_date",
        "databaseFunction": {
            "key": "sql.typeConversion.todate",
            "description": "This function is used to convert character based date value to date type.(format is not required)Example:CAST('2018-08-30' as DATE)) result:2018-08-30",
            "value": "TODATE",
            "signature": "CAST(${column} AS DATE)",
            "returns": "date",
            "parameters": [
                {
                    "name": "column",
                    "column": true,
                    "value": "travel_details.travel_date"
                }
            ]
        },
        "dataType": "dateTime",
        "backendDataType": "java.sql.Timestamp",
        "condition": "EQUALS",
        "values": [
            "today"
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
        "uid": "754ca4ec-00b8-4ad5-9f7f-39c19e563ec8",
        "configId": "ddfe5751-b13e-44a0-b94f-aefb9bcf041d",
        "dataId": "56bce38c-62e6-4476-8b16-331f6607a3c0",
        "columnID": "28",
        "datePart": "date",
        "currentDate": "Tue Aug 08 2023 15:31:59 GMT+0530 (India Standard Time)",
        "dateValuesType": "datePicker",
        "anchor": {
            "anchorDate": "2023-08-08 15:31:59",
            "isAnchor": false,
            "active": 1,
            "relativePart": "day",
            "value": 0,
            "lastInput": 3,
            "nextInput": 3,
            "part": "days"
        },
        "mapping": {
            "isEnabled": true,
            "unique": true,
            "valueDBFunction": {
                "key": "sql.typeConversion.todate",
                "description": "This function is used to convert character based date value to date type.(format is not required)Example:CAST('2018-08-30' as DATE)) result:2018-08-30",
                "value": "TODATE",
                "signature": "CAST(${column} AS DATE)",
                "returns": "date",
                "parameters": [
                    {
                        "name": "column",
                        "column": true,
                        "value": "travel_details.travel_date"
                    }
                ]
            },
            "DisplayDBFunction": {
                "key": "sql.typeConversion.todate",
                "description": "This function is used to convert character based date value to date type.(format is not required)Example:CAST('2018-08-30' as DATE)) result:2018-08-30",
                "value": "TODATE",
                "signature": "CAST(${column} AS DATE)",
                "returns": "date",
                "parameters": [
                    {
                        "name": "column",
                        "column": true,
                        "value": "travel_details.travel_date"
                    }
                ]
            },
            "isDefaultFunction": true,
            "valueDisplayMap": [],
            "valueAliasName": "random",
            "orderBy": {
                "display": "asc",
                "value": "none"
            },
            "valueDBFuntionInfo": {},
            "valueColumn": {
                "alias": "travel_date",
                "fullyQualifiedColumn": "travel_details.travel_date",
                "id": "28",
                "defaultFunction": "db.generic.groupBy.group",
                "type": {
                    "java.sql.Timestamp": "dateTime"
                }
            },
            "displayColumn": {
                "alias": "travel_date",
                "fullyQualifiedColumn": "travel_details.travel_date",
                "id": "28",
                "defaultFunction": "db.generic.groupBy.group",
                "type": {
                    "java.sql.Timestamp": "dateTime"
                }
            }
        },
        "cascade": {
            "isEnabled": false,
            "filters": [],
            "filtersCount": 0
        },
        "active": false,
        "reportId": "24017573-7b0f-466f-8f1e-fa379365f5d7",
        "search": ""
    },
    {
        "column": "travel_details.travel_date",
        "label": "travel_date",
        "databaseFunction": {
            "key": "sql.typeConversion.todate",
            "description": "This function is used to convert character based date value to date type.(format is not required)Example:CAST('2018-08-30' as DATE)) result:2018-08-30",
            "value": "TODATE",
            "signature": "CAST(${column} AS DATE)",
            "returns": "date",
            "parameters": [
                {
                    "name": "column",
                    "column": true,
                    "value": "travel_details.travel_date"
                }
            ]
        },
        "dataType": "dateTime",
        "backendDataType": "java.sql.Timestamp",
        "condition": "IN_RANGE",
        "values": [
            "today",
            "today-1"
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
        "uid": "d1e31162-7083-41c7-91f1-9725168a4c5f",
        "configId": "64331096-8021-4748-96bb-4fe56ef8a425",
        "dataId": "eefa3bdd-7f25-4c0a-88f3-5301a2537bfe",
        "columnID": "28",
        "datePart": "date",
        "currentDate": "Tue Aug 08 2023 15:16:59 GMT+0530 (India Standard Time)",
        "dateValuesType": "datePicker",
        "anchor": {
            "anchorDate": "2023-08-08 15:16:59",
            "isAnchor": false,
            "active": 1,
            "relativePart": "month",
            "value": 0,
            "lastInput": 3,
            "nextInput": 3,
            "part": "months"
        },
        "mapping": {
            "isEnabled": true,
            "unique": true,
            "valueDBFunction": {
                "key": "sql.typeConversion.todate",
                "description": "This function is used to convert character based date value to date type.(format is not required)Example:CAST('2018-08-30' as DATE)) result:2018-08-30",
                "value": "TODATE",
                "signature": "CAST(${column} AS DATE)",
                "returns": "date",
                "parameters": [
                    {
                        "name": "column",
                        "column": true,
                        "value": "travel_details.travel_date"
                    }
                ]
            },
            "DisplayDBFunction": {
                "key": "sql.typeConversion.todate",
                "description": "This function is used to convert character based date value to date type.(format is not required)Example:CAST('2018-08-30' as DATE)) result:2018-08-30",
                "value": "TODATE",
                "signature": "CAST(${column} AS DATE)",
                "returns": "date",
                "parameters": [
                    {
                        "name": "column",
                        "column": true,
                        "value": "travel_details.travel_date"
                    }
                ]
            },
            "isDefaultFunction": true,
            "valueDisplayMap": [],
            "valueAliasName": "random",
            "orderBy": {
                "display": "asc",
                "value": "none"
            },
            "valueDBFuntionInfo": {},
            "valueColumn": {
                "alias": "travel_date",
                "fullyQualifiedColumn": "travel_details.travel_date",
                "id": "28",
                "defaultFunction": "db.generic.groupBy.group",
                "type": {
                    "java.sql.Timestamp": "dateTime"
                }
            },
            "displayColumn": {
                "alias": "travel_date",
                "fullyQualifiedColumn": "travel_details.travel_date",
                "id": "28",
                "defaultFunction": "db.generic.groupBy.group",
                "type": {
                    "java.sql.Timestamp": "dateTime"
                }
            }
        },
        "cascade": {
            "isEnabled": false,
            "filters": [],
            "filtersCount": 0
        },
        "active": false,
        "reportId": "ef0b3bfb-84ec-4424-8488-3726fe1e22e4",
        "search": ""
    },
    {
        "column": "travel_details.travel_date",
        "label": "travel_date",
        "databaseFunction": {
            "key": "sql.typeConversion.todate",
            "description": "This function is used to convert character based date value to date type.(format is not required)Example:CAST('2018-08-30' as DATE)) result:2018-08-30",
            "value": "TODATE",
            "signature": "CAST(${column} AS DATE)",
            "returns": "date",
            "parameters": [
                {
                    "name": "column",
                    "column": true,
                    "value": "travel_details.travel_date"
                }
            ]
        },
        "dataType": "dateTime",
        "backendDataType": "java.sql.Timestamp",
        "condition": "IN_RANGE",
        "values": [
            "month",
            "month-1"
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
        "uid": "754ca4ec-00b8-4ad5-9f7f-39c19e563ec8",
        "configId": "0a51dd2b-f111-4fe7-bdec-8ba4cb54886e",
        "dataId": "38a19dc4-4ff9-4d2c-86f0-954344ef25fa",
        "columnID": "28",
        "datePart": "date",
        "currentDate": "Tue Aug 08 2023 15:31:59 GMT+0530 (India Standard Time)",
        "dateValuesType": "datePicker",
        "anchor": {
            "anchorDate": "2023-08-08 15:31:59",
            "isAnchor": false,
            "active": 2,
            "relativePart": "month",
            "value": 1,
            "direction": "next",
            "lastInput": 3,
            "nextInput": 3,
            "part": "months"
        },
        "mapping": {
            "isEnabled": true,
            "unique": true,
            "valueDBFunction": {
                "key": "sql.typeConversion.todate",
                "description": "This function is used to convert character based date value to date type.(format is not required)Example:CAST('2018-08-30' as DATE)) result:2018-08-30",
                "value": "TODATE",
                "signature": "CAST(${column} AS DATE)",
                "returns": "date",
                "parameters": [
                    {
                        "name": "column",
                        "column": true,
                        "value": "travel_details.travel_date"
                    }
                ]
            },
            "DisplayDBFunction": {
                "key": "sql.typeConversion.todate",
                "description": "This function is used to convert character based date value to date type.(format is not required)Example:CAST('2018-08-30' as DATE)) result:2018-08-30",
                "value": "TODATE",
                "signature": "CAST(${column} AS DATE)",
                "returns": "date",
                "parameters": [
                    {
                        "name": "column",
                        "column": true,
                        "value": "travel_details.travel_date"
                    }
                ]
            },
            "isDefaultFunction": true,
            "valueDisplayMap": [],
            "valueAliasName": "random",
            "orderBy": {
                "display": "asc",
                "value": "none"
            },
            "valueDBFuntionInfo": {},
            "valueColumn": {
                "alias": "travel_date",
                "fullyQualifiedColumn": "travel_details.travel_date",
                "id": "28",
                "defaultFunction": "db.generic.groupBy.group",
                "type": {
                    "java.sql.Timestamp": "dateTime"
                }
            },
            "displayColumn": {
                "alias": "travel_date",
                "fullyQualifiedColumn": "travel_details.travel_date",
                "id": "28",
                "defaultFunction": "db.generic.groupBy.group",
                "type": {
                    "java.sql.Timestamp": "dateTime"
                }
            }
        },
        "cascade": {
            "isEnabled": false,
            "filters": [],
            "filtersCount": 0
        },
        "active": false,
        "reportId": "24017573-7b0f-466f-8f1e-fa379365f5d7",
        "search": ""
    }
]