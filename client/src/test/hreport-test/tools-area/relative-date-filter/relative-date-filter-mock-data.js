const dataWithDatepartAsIndividual = {
    "dateValuesType": "relative-list",
    "filter": {
        "column": "travel_details.travel_date",
        "label": "travel_date",
        "databaseFunction": null,
        "dataType": "dateTime",
        "backendDataType": "java.sql.Timestamp",
        "condition": "EQUALS",
        "values": [
            "2023-03-07 11:39:13.0"
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
        "uid": "51be067a-a8d3-4d9a-a2b1-72b554b2c14b",
        "configId": "3f84441a-9a11-48ab-9177-8952c517e285",
        "dataId": "d876aed0-aa17-4566-b03b-7f94fb23ca40",
        "datePart": "individual",
        "currentDate": "Tue Mar 07 2023 13:37:01 GMT+0530 (India Standard Time)",
        "dateValuesType": "relative-list",
        "anchor": {
            "anchorDate": "2023-03-07 11:39:13",
            "isAnchor": true,
            "active": 1,
            "relativePart": "year",
            "value": 0,
            "direction": "",
            "lastInput": 3,
            "nextInput": 3,
            "part": "years"
        },
        "mapping": {
            "isEnabled": true,
            "unique": true,
            "valueDBFunction": null,
            "DisplayDBFunction": null,
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
                "id": "13109",
                "defaultFunction": "none",
                "type": {
                    "java.sql.Timestamp": "dateTime"
                }
            },
            "displayColumn": {
                "alias": "travel_date",
                "fullyQualifiedColumn": "travel_details.travel_date",
                "id": "13109",
                "defaultFunction": "none",
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
        "active": true,
        "reportId": "d0f82bd0-d5ad-424e-8d7d-4bf2e6a0a75c"
    }
}

const dataWithDatepartAsDate = {
    "dateValuesType": "relative-list",
    "filter": {
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
            "2023-03-07"
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
        "uid": "51be067a-a8d3-4d9a-a2b1-72b554b2c14b",
        "configId": "fd2e3541-0422-4e4a-921d-5055ce256999",
        "dataId": "a77d260f-ad70-4086-93ca-59513e63ad79",
        "datePart": "date",
        "currentDate": "Tue Mar 07 2023 13:37:01 GMT+0530 (India Standard Time)",
        "dateValuesType": "relative-list",
        "anchor": {
            "anchorDate": "2023-03-07 11:39:13",
            "isAnchor": true,
            "active": 1,
            "relativePart": "year",
            "value": 0,
            "direction": "",
            "lastInput": 3,
            "nextInput": 3,
            "part": "years"
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
                "id": "13109",
                "defaultFunction": "none",
                "type": {
                    "java.sql.Timestamp": "dateTime"
                }
            },
            "displayColumn": {
                "alias": "travel_date",
                "fullyQualifiedColumn": "travel_details.travel_date",
                "id": "13109",
                "defaultFunction": "none",
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
        "active": true,
        "reportId": "d0f82bd0-d5ad-424e-8d7d-4bf2e6a0a75c"
    }
}

const dataWithDatepartAsYear = {
    "dateValuesType": "relative-list",
    "filter": {
        "column": "travel_details.travel_date",
        "label": "travel_date",
        "databaseFunction": {
            "key": "sql.dateTime.year",
            "description": "Return year for date/dateTime. Example: year('2014-03-08 09:00:00')/year('2014-03-08') result: 2014",
            "value": "YEAR",
            "signature": "year(${datetime})",
            "returns": "numeric",
            "parameters": [
                {
                    "name": "datetime",
                    "column": true,
                    "defaultValue": "'2007-02-03 09:00:00'",
                    "value": "travel_details.travel_date"
                }
            ]
        },
        "dataType": "dateTime",
        "backendDataType": "java.sql.Timestamp",
        "condition": "EQUALS",
        "values": [
            2023
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
        "uid": "51be067a-a8d3-4d9a-a2b1-72b554b2c14b",
        "configId": "d509e555-04e2-4062-9f95-e06d82b7113c",
        "dataId": "2e3628ff-07c6-417f-b6bd-6426697c2924",
        "datePart": "year",
        "currentDate": "Tue Mar 07 2023 13:37:01 GMT+0530 (India Standard Time)",
        "dateValuesType": "datetime",
        "anchor": {
            "anchorDate": "2023-03-07 11:39:13",
            "isAnchor": true,
            "active": 1,
            "relativePart": "year",
            "value": 0,
            "direction": "",
            "lastInput": 3,
            "nextInput": 3,
            "part": "years"
        },
        "mapping": {
            "isEnabled": true,
            "unique": true,
            "valueDBFunction": {
                "key": "sql.dateTime.year",
                "description": "Return year for date/dateTime. Example: year('2014-03-08 09:00:00')/year('2014-03-08') result: 2014",
                "value": "YEAR",
                "signature": "year(${datetime})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "datetime",
                        "column": true,
                        "defaultValue": "'2007-02-03 09:00:00'",
                        "value": "travel_details.travel_date"
                    }
                ]
            },
            "DisplayDBFunction": {
                "key": "sql.dateTime.year",
                "description": "Return year for date/dateTime. Example: year('2014-03-08 09:00:00')/year('2014-03-08') result: 2014",
                "value": "YEAR",
                "signature": "year(${datetime})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "datetime",
                        "column": true,
                        "defaultValue": "'2007-02-03 09:00:00'",
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
                "id": "13109",
                "defaultFunction": "none",
                "type": {
                    "java.sql.Timestamp": "dateTime"
                }
            },
            "displayColumn": {
                "alias": "travel_date",
                "fullyQualifiedColumn": "travel_details.travel_date",
                "id": "13109",
                "defaultFunction": "none",
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
        "active": true,
        "reportId": "d0f82bd0-d5ad-424e-8d7d-4bf2e6a0a75c"
    }
}

const dataWithDatepartAsMonth = {
    "dateValuesType": "relative-list",
    "filter": {
        "column": "travel_details.travel_date",
        "label": "travel_date",
        "databaseFunction": {
            "key": "sql.dateTime.month",
            "description": "Returns the month of the year for date/datetime. Example: month('2007-02-03 09:00:00')/month('2007-02-03') result:2",
            "value": "MONTH",
            "signature": "month(${datetime})",
            "returns": "numeric",
            "parameters": [
                {
                    "name": "datetime",
                    "column": true,
                    "defaultValue": "'2007-02-03 09:12:30'",
                    "value": "travel_details.travel_date"
                }
            ]
        },
        "dataType": "dateTime",
        "backendDataType": "java.sql.Timestamp",
        "condition": "EQUALS",
        "values": [
            3
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
        "uid": "51be067a-a8d3-4d9a-a2b1-72b554b2c14b",
        "configId": "4a591931-87c6-4832-93ee-d2414a993b44",
        "dataId": "3fd24073-558f-4c5b-853b-603728d5a765",
        "datePart": "month",
        "currentDate": "Tue Mar 07 2023 13:37:01 GMT+0530 (India Standard Time)",
        "dateValuesType": "relative-list",
        "anchor": {
            "anchorDate": "2023-03-07 11:39:13",
            "isAnchor": true,
            "active": 1,
            "relativePart": "year",
            "value": 0,
            "direction": "",
            "lastInput": 3,
            "nextInput": 3,
            "part": "years"
        },
        "mapping": {
            "isEnabled": true,
            "unique": true,
            "valueDBFunction": {
                "key": "sql.dateTime.month",
                "description": "Returns the month of the year for date/datetime. Example: month('2007-02-03 09:00:00')/month('2007-02-03') result:2",
                "value": "MONTH",
                "signature": "month(${datetime})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "datetime",
                        "column": true,
                        "defaultValue": "'2007-02-03 09:12:30'",
                        "value": "travel_details.travel_date"
                    }
                ]
            },
            "DisplayDBFunction": {
                "key": "sql.dateTime.month",
                "description": "Returns the month of the year for date/datetime. Example: month('2007-02-03 09:00:00')/month('2007-02-03') result:2",
                "value": "MONTH",
                "signature": "month(${datetime})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "datetime",
                        "column": true,
                        "defaultValue": "'2007-02-03 09:12:30'",
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
                "id": "13109",
                "defaultFunction": "none",
                "type": {
                    "java.sql.Timestamp": "dateTime"
                }
            },
            "displayColumn": {
                "alias": "travel_date",
                "fullyQualifiedColumn": "travel_details.travel_date",
                "id": "13109",
                "defaultFunction": "none",
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
        "active": true,
        "reportId": "d0f82bd0-d5ad-424e-8d7d-4bf2e6a0a75c"
    }
}

const dataWithDatepartAsQuarter = {
    "dateValuesType": "relative-list",
    "filter": {
        "column": "travel_details.travel_date",
        "label": "travel_date",
        "databaseFunction": {
            "key": "sql.dateTime.quarter",
            "description": "Returns the quarter of the year for date/datetime. Example: quarter('2014-03-08 12:20:19') result:1",
            "value": "QUARTER",
            "signature": "(CASE MONTH(${datetime}) WHEN < 4 THEN 1 WHEN BETWEEN 4 AND 6 then 2 WHEN BETWEEN 7 AND 9 then 3 WHEN BETWEEN 10 AND 12 then 4 END)",
            "returns": "numeric",
            "parameters": [
                {
                    "name": "datetime",
                    "column": true,
                    "defaultValue": "'2014-03-08 12:20:19'",
                    "value": "travel_details.travel_date"
                }
            ]
        },
        "dataType": "dateTime",
        "backendDataType": "java.sql.Timestamp",
        "condition": "EQUALS",
        "values": [
            1
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
        "uid": "51be067a-a8d3-4d9a-a2b1-72b554b2c14b",
        "configId": "1541931f-3877-44f7-82ae-001ca966d13b",
        "dataId": "0c934942-bf0f-415d-a90f-f478e9fce080",
        "datePart": "quarter",
        "currentDate": "Tue Mar 07 2023 13:37:01 GMT+0530 (India Standard Time)",
        "dateValuesType": "relative-list",
        "anchor": {
            "anchorDate": "2023-03-07 11:39:13",
            "isAnchor": true,
            "active": 1,
            "relativePart": "year",
            "value": 0,
            "direction": "",
            "lastInput": 3,
            "nextInput": 3,
            "part": "years"
        },
        "mapping": {
            "isEnabled": true,
            "unique": true,
            "valueDBFunction": {
                "key": "sql.dateTime.quarter",
                "description": "Returns the quarter of the year for date/datetime. Example: quarter('2014-03-08 12:20:19') result:1",
                "value": "QUARTER",
                "signature": "(CASE MONTH(${datetime}) WHEN < 4 THEN 1 WHEN BETWEEN 4 AND 6 then 2 WHEN BETWEEN 7 AND 9 then 3 WHEN BETWEEN 10 AND 12 then 4 END)",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "datetime",
                        "column": true,
                        "defaultValue": "'2014-03-08 12:20:19'",
                        "value": "travel_details.travel_date"
                    }
                ]
            },
            "DisplayDBFunction": {
                "key": "sql.dateTime.quarter",
                "description": "Returns the quarter of the year for date/datetime. Example: quarter('2014-03-08 12:20:19') result:1",
                "value": "QUARTER",
                "signature": "(CASE MONTH(${datetime}) WHEN < 4 THEN 1 WHEN BETWEEN 4 AND 6 then 2 WHEN BETWEEN 7 AND 9 then 3 WHEN BETWEEN 10 AND 12 then 4 END)",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "datetime",
                        "column": true,
                        "defaultValue": "'2014-03-08 12:20:19'",
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
                "id": "13109",
                "defaultFunction": "none",
                "type": {
                    "java.sql.Timestamp": "dateTime"
                }
            },
            "displayColumn": {
                "alias": "travel_date",
                "fullyQualifiedColumn": "travel_details.travel_date",
                "id": "13109",
                "defaultFunction": "none",
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
        "active": true,
        "reportId": "d0f82bd0-d5ad-424e-8d7d-4bf2e6a0a75c"
    }
}

const dataWithDatepartAsTime = {
    "dateValuesType": "relative-list",
    "filter": {
        "column": "travel_details.travel_date",
        "label": "travel_date",
        "databaseFunction": {
            "key": "sql.typeConversion.totime",
            "description": "This function is used to convert character based time value to time type.(format is not required) Example:CAST('10:15:30' as TIME)) result:10:15:30",
            "value": "TOTIME",
            "signature": "CAST(${column} AS TIME)",
            "returns": "time",
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
            "14:03:51"
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
        "uid": "51be067a-a8d3-4d9a-a2b1-72b554b2c14b",
        "configId": "f5c2066e-7986-478f-a374-7a49c391c6e6",
        "dataId": "bb8cadad-0470-4f29-a9ee-b8205cd41d11",
        "datePart": "time",
        "currentDate": "Tue Mar 07 2023 13:37:01 GMT+0530 (India Standard Time)",
        "dateValuesType": "relative-list",
        "anchor": {
            "anchorDate": "2023-03-07 11:39:13",
            "isAnchor": true,
            "active": 1,
            "relativePart": "year",
            "value": 0,
            "direction": "",
            "lastInput": 3,
            "nextInput": 3,
            "part": "years"
        },
        "mapping": {
            "isEnabled": true,
            "unique": true,
            "valueDBFunction": {
                "key": "sql.typeConversion.totime",
                "description": "This function is used to convert character based time value to time type.(format is not required) Example:CAST('10:15:30' as TIME)) result:10:15:30",
                "value": "TOTIME",
                "signature": "CAST(${column} AS TIME)",
                "returns": "time",
                "parameters": [
                    {
                        "name": "column",
                        "column": true,
                        "value": "travel_details.travel_date"
                    }
                ]
            },
            "DisplayDBFunction": {
                "key": "sql.typeConversion.totime",
                "description": "This function is used to convert character based time value to time type.(format is not required) Example:CAST('10:15:30' as TIME)) result:10:15:30",
                "value": "TOTIME",
                "signature": "CAST(${column} AS TIME)",
                "returns": "time",
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
                "id": "13109",
                "defaultFunction": "none",
                "type": {
                    "java.sql.Timestamp": "dateTime"
                }
            },
            "displayColumn": {
                "alias": "travel_date",
                "fullyQualifiedColumn": "travel_details.travel_date",
                "id": "13109",
                "defaultFunction": "none",
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
        "active": true,
        "reportId": "d0f82bd0-d5ad-424e-8d7d-4bf2e6a0a75c"
    }
}

const dataWithDatepartAsDay = {
    "dateValuesType": "relative-list",
    "filter": {
        "column": "travel_details.travel_date",
        "label": "travel_date",
        "databaseFunction": {
            "key": "sql.dateTime.day",
            "description": "Returns day of the month for date/datetime. Example: day('2014-03-08 09:00:00')/day('2014-03-08') result: 3",
            "value": "DAY",
            "signature": "day(${datetime})",
            "returns": "numeric",
            "parameters": [
                {
                    "name": "datetime",
                    "column": true,
                    "defaultValue": "'2007-02-03 09:00:00'",
                    "value": "travel_details.travel_date"
                }
            ]
        },
        "dataType": "dateTime",
        "backendDataType": "java.sql.Timestamp",
        "condition": "EQUALS",
        "values": [
            7
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
        "uid": "51be067a-a8d3-4d9a-a2b1-72b554b2c14b",
        "configId": "c34820d5-2e31-46ca-8af2-61a52868adea",
        "dataId": "fe542ca3-a09b-4553-b7df-3495c2e799f3",
        "datePart": "day",
        "currentDate": "Tue Mar 07 2023 13:37:01 GMT+0530 (India Standard Time)",
        "dateValuesType": "relative-list",
        "anchor": {
            "anchorDate": "2023-03-07 11:39:13",
            "isAnchor": true,
            "active": 1,
            "relativePart": "year",
            "value": 0,
            "direction": "",
            "lastInput": 3,
            "nextInput": 3,
            "part": "years"
        },
        "mapping": {
            "isEnabled": true,
            "unique": true,
            "valueDBFunction": {
                "key": "sql.dateTime.day",
                "description": "Returns day of the month for date/datetime. Example: day('2014-03-08 09:00:00')/day('2014-03-08') result: 3",
                "value": "DAY",
                "signature": "day(${datetime})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "datetime",
                        "column": true,
                        "defaultValue": "'2007-02-03 09:00:00'",
                        "value": "travel_details.travel_date"
                    }
                ]
            },
            "DisplayDBFunction": {
                "key": "sql.dateTime.day",
                "description": "Returns day of the month for date/datetime. Example: day('2014-03-08 09:00:00')/day('2014-03-08') result: 3",
                "value": "DAY",
                "signature": "day(${datetime})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "datetime",
                        "column": true,
                        "defaultValue": "'2007-02-03 09:00:00'",
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
                "id": "13109",
                "defaultFunction": "none",
                "type": {
                    "java.sql.Timestamp": "dateTime"
                }
            },
            "displayColumn": {
                "alias": "travel_date",
                "fullyQualifiedColumn": "travel_details.travel_date",
                "id": "13109",
                "defaultFunction": "none",
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
        "active": true,
        "reportId": "d0f82bd0-d5ad-424e-8d7d-4bf2e6a0a75c"
    }
}

const dataWithDatepartAsHour = {
    "dateValuesType": "relative-list",
    "filter": {
        "column": "travel_details.travel_date",
        "label": "travel_date",
        "databaseFunction": {
            "key": "sql.dateTime.hour",
            "description": "Return hour for timestamp or a valid timestamp string. Example: hour('2014-03-08 12:20:19') result:12",
            "value": "HOUR",
            "signature": "hour(${datetime})",
            "returns": "numeric",
            "parameters": [
                {
                    "name": "datetime",
                    "column": true,
                    "defaultValue": "'2014-03-08 12:20:19'",
                    "value": "travel_details.travel_date"
                }
            ]
        },
        "dataType": "dateTime",
        "backendDataType": "java.sql.Timestamp",
        "condition": "EQUALS",
        "values": [
            14
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
        "uid": "51be067a-a8d3-4d9a-a2b1-72b554b2c14b",
        "configId": "114de04e-d8ff-4359-9c21-acacbc9e4db9",
        "dataId": "021ac877-e7e4-43e4-b664-37e01907b43e",
        "datePart": "hour",
        "currentDate": "Tue Mar 07 2023 13:37:01 GMT+0530 (India Standard Time)",
        "dateValuesType": "relative-list",
        "anchor": {
            "anchorDate": "2023-03-07 11:39:13",
            "isAnchor": true,
            "active": 1,
            "relativePart": "year",
            "value": 0,
            "direction": "",
            "lastInput": 3,
            "nextInput": 3,
            "part": "years"
        },
        "mapping": {
            "isEnabled": true,
            "unique": true,
            "valueDBFunction": {
                "key": "sql.dateTime.hour",
                "description": "Return hour for timestamp or a valid timestamp string. Example: hour('2014-03-08 12:20:19') result:12",
                "value": "HOUR",
                "signature": "hour(${datetime})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "datetime",
                        "column": true,
                        "defaultValue": "'2014-03-08 12:20:19'",
                        "value": "travel_details.travel_date"
                    }
                ]
            },
            "DisplayDBFunction": {
                "key": "sql.dateTime.hour",
                "description": "Return hour for timestamp or a valid timestamp string. Example: hour('2014-03-08 12:20:19') result:12",
                "value": "HOUR",
                "signature": "hour(${datetime})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "datetime",
                        "column": true,
                        "defaultValue": "'2014-03-08 12:20:19'",
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
                "id": "13109",
                "defaultFunction": "none",
                "type": {
                    "java.sql.Timestamp": "dateTime"
                }
            },
            "displayColumn": {
                "alias": "travel_date",
                "fullyQualifiedColumn": "travel_details.travel_date",
                "id": "13109",
                "defaultFunction": "none",
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
        "active": true,
        "reportId": "d0f82bd0-d5ad-424e-8d7d-4bf2e6a0a75c"
    }
}

const dataWithDatepartAsMinute = {
    "dateValuesType": "relative-list",
    "filter": {
        "column": "travel_details.travel_date",
        "label": "travel_date",
        "databaseFunction": {
            "key": "sql.dateTime.minute",
            "description": "Returns minute for timestamp or a valid timestamp string. Example: minute('2014-03-08 12:20:19') result: 20",
            "value": "MINUTE",
            "signature": "minute(${datetime})",
            "returns": "numeric",
            "parameters": [
                {
                    "name": "datetime",
                    "column": true,
                    "defaultValue": "'2007-02-03 09:12:30'",
                    "value": "travel_details.travel_date"
                }
            ]
        },
        "dataType": "dateTime",
        "backendDataType": "java.sql.Timestamp",
        "condition": "EQUALS",
        "values": [
            5
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
        "uid": "51be067a-a8d3-4d9a-a2b1-72b554b2c14b",
        "configId": "c624ff41-0336-48b1-bb6f-2896f41af14a",
        "dataId": "098e5051-20a2-4332-a291-a2b11432516b",
        "datePart": "minute",
        "currentDate": "Tue Mar 07 2023 13:37:01 GMT+0530 (India Standard Time)",
        "dateValuesType": "relative-list",
        "anchor": {
            "anchorDate": "2023-03-07 11:39:13",
            "isAnchor": true,
            "active": 1,
            "relativePart": "year",
            "value": 0,
            "direction": "",
            "lastInput": 3,
            "nextInput": 3,
            "part": "years"
        },
        "mapping": {
            "isEnabled": true,
            "unique": true,
            "valueDBFunction": {
                "key": "sql.dateTime.minute",
                "description": "Returns minute for timestamp or a valid timestamp string. Example: minute('2014-03-08 12:20:19') result: 20",
                "value": "MINUTE",
                "signature": "minute(${datetime})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "datetime",
                        "column": true,
                        "defaultValue": "'2007-02-03 09:12:30'",
                        "value": "travel_details.travel_date"
                    }
                ]
            },
            "DisplayDBFunction": {
                "key": "sql.dateTime.minute",
                "description": "Returns minute for timestamp or a valid timestamp string. Example: minute('2014-03-08 12:20:19') result: 20",
                "value": "MINUTE",
                "signature": "minute(${datetime})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "datetime",
                        "column": true,
                        "defaultValue": "'2007-02-03 09:12:30'",
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
                "id": "13109",
                "defaultFunction": "none",
                "type": {
                    "java.sql.Timestamp": "dateTime"
                }
            },
            "displayColumn": {
                "alias": "travel_date",
                "fullyQualifiedColumn": "travel_details.travel_date",
                "id": "13109",
                "defaultFunction": "none",
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
        "active": true,
        "reportId": "d0f82bd0-d5ad-424e-8d7d-4bf2e6a0a75c"
    }
}

const dataWithDatepartAsSecond = {
    "dateValuesType": "relative-list",
    "filter": {
        "column": "travel_details.travel_date",
        "label": "travel_date",
        "databaseFunction": {
            "key": "sql.dateTime.second",
            "description": "Returns the seconds for timestamp or a valid timestamp string. Example:second('2014-03-08 12:20:19'). result: 19. NOTE:If the argument is a timestamp: The result will contains fraction of seconds along with second.",
            "value": "SECOND",
            "signature": "second(${datetime})",
            "returns": "numeric",
            "parameters": [
                {
                    "name": "datetime",
                    "column": true,
                    "defaultValue": "'2007-02-03 09:12:30'",
                    "value": "travel_details.travel_date"
                }
            ]
        },
        "dataType": "dateTime",
        "backendDataType": "java.sql.Timestamp",
        "condition": "EQUALS",
        "values": [
            53
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
        "uid": "51be067a-a8d3-4d9a-a2b1-72b554b2c14b",
        "configId": "79f0f11f-14ef-4316-a976-2a75e3959cb0",
        "dataId": "631b5f66-c7ba-4c0b-8987-1713d0725e45",
        "datePart": "second",
        "currentDate": "Tue Mar 07 2023 13:37:01 GMT+0530 (India Standard Time)",
        "dateValuesType": "relative-list",
        "anchor": {
            "anchorDate": "2023-03-07 11:39:13",
            "isAnchor": true,
            "active": 1,
            "relativePart": "year",
            "value": 0,
            "direction": "",
            "lastInput": 3,
            "nextInput": 3,
            "part": "years"
        },
        "mapping": {
            "isEnabled": true,
            "unique": true,
            "valueDBFunction": {
                "key": "sql.dateTime.second",
                "description": "Returns the seconds for timestamp or a valid timestamp string. Example:second('2014-03-08 12:20:19'). result: 19. NOTE:If the argument is a timestamp: The result will contains fraction of seconds along with second.",
                "value": "SECOND",
                "signature": "second(${datetime})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "datetime",
                        "column": true,
                        "defaultValue": "'2007-02-03 09:12:30'",
                        "value": "travel_details.travel_date"
                    }
                ]
            },
            "DisplayDBFunction": {
                "key": "sql.dateTime.second",
                "description": "Returns the seconds for timestamp or a valid timestamp string. Example:second('2014-03-08 12:20:19'). result: 19. NOTE:If the argument is a timestamp: The result will contains fraction of seconds along with second.",
                "value": "SECOND",
                "signature": "second(${datetime})",
                "returns": "numeric",
                "parameters": [
                    {
                        "name": "datetime",
                        "column": true,
                        "defaultValue": "'2007-02-03 09:12:30'",
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
                "id": "13109",
                "defaultFunction": "none",
                "type": {
                    "java.sql.Timestamp": "dateTime"
                }
            },
            "displayColumn": {
                "alias": "travel_date",
                "fullyQualifiedColumn": "travel_details.travel_date",
                "id": "13109",
                "defaultFunction": "none",
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
        "active": true,
        "reportId": "d0f82bd0-d5ad-424e-8d7d-4bf2e6a0a75c"
    }
}


export {
    dataWithDatepartAsIndividual,
    dataWithDatepartAsDate,
    dataWithDatepartAsYear,
    dataWithDatepartAsMonth,
    dataWithDatepartAsQuarter,
    dataWithDatepartAsTime,
    dataWithDatepartAsDay,
    dataWithDatepartAsHour,
    dataWithDatepartAsMinute,
    dataWithDatepartAsSecond
}
