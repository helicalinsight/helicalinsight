
import { databaseFunctions,dateFunctions,metadata } from "../utils/mock.data"

export const vizState = {
    "activeReportId": "test_123",
    "reports": [
        {
            "id": "test_123",
            "mode": "create",
            "active": true,
            "metadata": metadata.metadata,
            "functions": {
                "db.generic.aggregate.avg": "avg",
                "db.generic.aggregate.count": "count",
                "db.generic.aggregate.distinct": "distinct",
                "db.generic.aggregate.max": "max",
                "db.generic.aggregate.min": "min",
                "db.generic.aggregate.sum": "sum",
                "db.generic.groupBy.group": "group by",
                "db.generic.orderBy.order": "order by"
            }, 
            "databaseFunctions": databaseFunctions,
            "fields": [
                {
                    "column": "travel_details.booking_platform",
                    "label": "booking_platform",
                    "id": "c3bfd46e-e5e6-4ef1-b002-ce3335e9e84e",
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
                        "Website"
                    ],
                    "valuesMode": "auto",
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
                    "drillDownId": "c50f6ba9-8410-4cea-aabc-2a9f517517e3",
                    "uid": "3bf56af6-3d69-4e52-8b17-f43e3f0cf996",
                    "encloseInQuotes": false,
                    "interactiveMode": true,
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
                            "columnId": "e613ae02-d22b-462d-b303-af9f9e47564e",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            }
                        },
                        "displayColumn": {
                            "alias": "booking_platform",
                            "fullyQualifiedColumn": "travel_details.booking_platform",
                            "columnId": "e613ae02-d22b-462d-b303-af9f9e47564e",
                            "defaultFunction": "db.generic.groupBy.group",
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
                    "drillownFilter": "report",
                    "drillDownFilterValues": [
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
                    "filterLabel": "booking_platform"
                }
            ],
            "defaultValueDisplayMap": {},
            "editingField": null,
            "marksList": [
                {
                    "value": "_all_",
                    "id": "e4801417-34a0-48a2-a8a9-26e944554a1d",
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
                    }
                }
            ],
            "activeMark": "e4801417-34a0-48a2-a8a9-26e944554a1d",
            "activeTool": "2",
            "scripts": [
                {
                    "id": "hdi-custom-script-58e5fe60-a686-4b01-b4df-73b0d5451e62",
                    "value": ""
                }
            ],
            "selectedScript": "hdi-custom-script-58e5fe60-a686-4b01-b4df-73b0d5451e62",
            "styles": "",
            "sqlString": "",
            "options": {
                "limitBy": 1000,
                "sample": "sample",
                "prependTableNameToAlias": false
            },
            "interactiveMode": true,
            "drillDown": true,
            "drillThrough": true,
            "drillDownList": [
                
            ],
            "currentDrillDown": "c50f6ba9-8410-4cea-aabc-2a9f517517e3",
            "drillThroughList": [],
            "toolbarConfig": {
                "selectable": false
            },
            "selectedType": "Table",
            "reportData": {
                "data": [
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
                        "rows": 1
                    }
                ],
                "lastModified": 1654601280091,
                "limitBy": 10,
                "offset": 0,
                "pageSize": 10,
                "dataId": "e2851f98-ca0e-4bb0-b62d-4f7f90e0b1a0"
            },
            "customStyles": "",
            "customScripts": [],
            "analytics": [],
            "properties": {},
            "reportInfo": {
                "location": "",
                "uuid": "",
                "reportName": "New1"
            },
            "cellMenuData": null,
            "showHiddenColumns": false,
            "showHiddenRows": false,
            "database": "HIUSER",
            "dateFunctions": dateFunctions
        }
    ],
    "layout": {
        "metadataShelf": true,
        "toolsAreaShelf": true,
        "fieldsAreaShelf": true
    }
}