export const relativeDateFilter = [
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
            "2024-04-01"
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
        "uid": "8533777c-e753-4162-92be-e0dd71566289",
        "configId": "8b86c83e-d456-431f-bc53-ddbf5f148b19",
        "dataId": "01258b01-aeb7-40a8-95c8-3058aa660804",
        "columnID": "1671",
        "datePart": "date",
        "currentDate": "Wed Mar 20 2024 17:19:55 GMT+0530 (India Standard Time)",
        "dateValuesType": "datePicker",
        "anchor": {
            "anchorDate": "2024-03-20 17:19:55",
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
                "id": "1671",
                "defaultFunction": "db.generic.groupBy.group",
                "type": {
                    "java.sql.Timestamp": "dateTime"
                }
            },
            "displayColumn": {
                "alias": "travel_date",
                "fullyQualifiedColumn": "travel_details.travel_date",
                "id": "1671",
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
        "reportId": "1e0c4f15-b7a1-45df-b8ef-88b274a1d31d",
        "search": ""
    }
]
