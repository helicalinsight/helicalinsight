
import { databaseFunctions,dateFunctions,metadata } from "../utils/mock.data"

export const filtersState = {
    "activeReportId": "test_123",
    "reports": [
        {
            "id": "test_123",
            "mode": "create",
            "active": true,
            "metadata":metadata,
            "databaseFunctions": databaseFunctions,
            "fields": [
                {
                    "column": "travel_details.booking_platform",
                    "label": "booking_platform",
                    "id": "052496c6-9de4-4a46-82ef-24a391c3ecb7",
                    "type": {
                        "backendDataType": "java.lang.String",
                        "dataType": "text"
                    },
                    "autogen_alias": "booking_platform",
                    "isNormalTable": true,
                    "groupBy": [
                        "db.generic.groupBy.group"
                    ],
                    "orderByColumn": false,
                    "showOrderByColumn": false,
                    "addedAs": "column",
                    "floatingType": "discrete",
                    "functionsDefinition": "",
                    "applyBeforeAggregate": false,
                    "hiddenIncludeInResultSet": false
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
                    "orderBy": "",
                    "valuesRange": {},
                    "rangeValuesType": "",
                    "dateTimeToggle": false,
                    "rangeSelectionToggole": true,
                    "maxInput": "",
                    "minInput": "",
                    "valuesList": [
                        {
                            "display": "Agent",
                            "value": "Agent"
                        },
                        {
                            "display": "Makemytrip",
                            "value": "Makemytrip"
                        },
                        {
                            "display": "Website",
                            "value": "Website"
                        }
                    ],
                    "drillDownId": "",
                    "uid": "d146d7cf-68cc-4bd5-b35e-44faee9c2ad6",
                    "mapping": {
                        "isEnabled": true,
                        "isDefaultFunction": true,
                        "valueDisplayMap": [],
                        "valueAliasName": "random",
                        "orderBy": {
                            "display": "none",
                            "value": "asc"
                        },
                        "valueDBFuntionInfo": {},
                        "valueColumn": {
                            "alias": "booking_platform",
                            "fullyQualifiedColumn": "travel_details.booking_platform",
                            "columnId": "264328a4-20dc-4cf0-93ba-24db7cbe0fac",
                            "defaultFunction": "none",
                            "type": {
                                "java.lang.String": "text"
                            }
                        },
                        "displayColumn": {
                            "alias": "booking_platform",
                            "fullyQualifiedColumn": "travel_details.booking_platform",
                            "columnId": "264328a4-20dc-4cf0-93ba-24db7cbe0fac",
                            "defaultFunction": "none",
                            "type": {
                                "java.lang.String": "text"
                            }
                        }
                    },
                    "cascade": {
                        "isEnabled": false,
                        "filterIds": [],
                        "filters": [],
                        "filtersCount": 0,
                        "filtersInfo": {},
                        "filtersFormData": {},
                        "autoUpdateCascadeInfoFromParent": true
                    },
                    "active": true,
                    "encloseInQuotes": false
                }
            ],
            "dateFunctions": dateFunctions
        }
    ]
}