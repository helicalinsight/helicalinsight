import { getFieldInfo } from "../../../components/hi-reports/hi-viz-area/utils/grid-chart-utils";

const minAggReportData = {
    "data": [
        {
            "travel_medium": "Bus",
            "travel_type": "Domestic",
            "min_travel_cost": 500
        },
        {
            "travel_medium": "Cab",
            "travel_type": "Domestic",
            "min_travel_cost": 300
        },
        {
            "travel_medium": "Flight",
            "travel_type": "Domestic",
            "min_travel_cost": 2000
        },
        {
            "travel_medium": "Flight",
            "travel_type": "International",
            "min_travel_cost": 12000
        },
        {
            "travel_medium": "Misc",
            "travel_type": "Domestic",
            "min_travel_cost": 400
        },
        {
            "travel_medium": "Train",
            "travel_type": "Domestic",
            "min_travel_cost": 400
        }
    ],
    "metadata": [
        {
            "1": {
                "name": "travel_medium",
                "type": "text"
            },
            "2": {
                "name": "travel_type",
                "type": "text"
            },
            "3": {
                "name": "min_travel_cost",
                "type": "numeric"
            }
        },
        {
            "rows": 6
        }
    ],
    "fields": [
        {
            "column": "travel_details.travel_medium",
            "label": "travel_medium",
            "id": "47c81f06-9a0f-487b-8abe-ed49627a30f1",
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
        },
        {
            "column": "travel_details.travel_type",
            "label": "travel_type",
            "id": "b306342d-fa65-4eff-a59b-6df0bdd29ef4",
            "type": {
                "backendDataType": "java.lang.String",
                "dataType": "text"
            },
            "autogen_alias": "travel_type",
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
            "metaDataAlias": "travel_type"
        },
        {
            "column": "travel_details.travel_cost",
            "label": "sum_travel_cost",
            "id": "9fd4df3f-6cb3-4677-ab8a-ef26e08abfc1",
            "type": {
                "backendDataType": "java.lang.Integer",
                "dataType": "numeric"
            },
            "autogen_alias": "min_travel_cost",
            "isNormalTable": true,
            "tableAlias": "travel_details",
            "aggregate": [
                "db.generic.aggregate.min"
            ],
            "orderByColumn": false,
            "showOrderByColumn": false,
            "addedAs": "row",
            "floatingType": "",
            "functionsDefinition": "",
            "applyBeforeAggregate": false,
            "hiddenIncludeInResultSet": false,
            "metaDataAlias": "travel_cost"
        }
    ],
    "marksList": [
        {
            "value": "_all_",
            "id": "ddc1925d-e109-4301-9b02-34c0e1ef918d",
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
            "id": "9fd4df3f-6cb3-4677-ab8a-ef26e08abfc1",
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
    ]
}

const maxAggReportData = {
    "data": [
        {
            "travel_medium": "Bus",
            "travel_type": "Domestic",
            "min_travel_cost": 500
        },
        {
            "travel_medium": "Cab",
            "travel_type": "Domestic",
            "min_travel_cost": 300
        },
        {
            "travel_medium": "Flight",
            "travel_type": "Domestic",
            "min_travel_cost": 2000
        },
        {
            "travel_medium": "Flight",
            "travel_type": "International",
            "min_travel_cost": 12000
        },
        {
            "travel_medium": "Misc",
            "travel_type": "Domestic",
            "min_travel_cost": 400
        },
        {
            "travel_medium": "Train",
            "travel_type": "Domestic",
            "min_travel_cost": 400
        }
    ],
    "metadata": [
        {
            "1": {
                "name": "travel_medium",
                "type": "text"
            },
            "2": {
                "name": "travel_type",
                "type": "text"
            },
            "3": {
                "name": "min_travel_cost",
                "type": "numeric"
            }
        },
        {
            "rows": 6
        }
    ],
    "fields": [
        {
            "column": "travel_details.travel_medium",
            "label": "travel_medium",
            "id": "47c81f06-9a0f-487b-8abe-ed49627a30f1",
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
        },
        {
            "column": "travel_details.travel_type",
            "label": "travel_type",
            "id": "b306342d-fa65-4eff-a59b-6df0bdd29ef4",
            "type": {
                "backendDataType": "java.lang.String",
                "dataType": "text"
            },
            "autogen_alias": "travel_type",
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
            "metaDataAlias": "travel_type"
        },
        {
            "column": "travel_details.travel_cost",
            "label": "sum_travel_cost",
            "id": "9fd4df3f-6cb3-4677-ab8a-ef26e08abfc1",
            "type": {
                "backendDataType": "java.lang.Integer",
                "dataType": "numeric"
            },
            "autogen_alias": "min_travel_cost",
            "isNormalTable": true,
            "tableAlias": "travel_details",
            "aggregate": [
                "db.generic.aggregate.max"
            ],
            "orderByColumn": false,
            "showOrderByColumn": false,
            "addedAs": "row",
            "floatingType": "",
            "functionsDefinition": "",
            "applyBeforeAggregate": false,
            "hiddenIncludeInResultSet": false,
            "metaDataAlias": "travel_cost"
        }
    ],
    "marksList": [
        {
            "value": "_all_",
            "id": "ddc1925d-e109-4301-9b02-34c0e1ef918d",
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
            "id": "9fd4df3f-6cb3-4677-ab8a-ef26e08abfc1",
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
    ]
}

const avgAggReportData = {
    "data": [
        {
            "travel_medium": "Bus",
            "travel_type": "Domestic",
            "min_travel_cost": 500
        },
        {
            "travel_medium": "Cab",
            "travel_type": "Domestic",
            "min_travel_cost": 300
        },
        {
            "travel_medium": "Flight",
            "travel_type": "Domestic",
            "min_travel_cost": 2000
        },
        {
            "travel_medium": "Flight",
            "travel_type": "International",
            "min_travel_cost": 12000
        },
        {
            "travel_medium": "Misc",
            "travel_type": "Domestic",
            "min_travel_cost": 400
        },
        {
            "travel_medium": "Train",
            "travel_type": "Domestic",
            "min_travel_cost": 400
        }
    ],
    "metadata": [
        {
            "1": {
                "name": "travel_medium",
                "type": "text"
            },
            "2": {
                "name": "travel_type",
                "type": "text"
            },
            "3": {
                "name": "min_travel_cost",
                "type": "numeric"
            }
        },
        {
            "rows": 6
        }
    ],
    "fields": [
        {
            "column": "travel_details.travel_medium",
            "label": "travel_medium",
            "id": "47c81f06-9a0f-487b-8abe-ed49627a30f1",
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
        },
        {
            "column": "travel_details.travel_type",
            "label": "travel_type",
            "id": "b306342d-fa65-4eff-a59b-6df0bdd29ef4",
            "type": {
                "backendDataType": "java.lang.String",
                "dataType": "text"
            },
            "autogen_alias": "travel_type",
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
            "metaDataAlias": "travel_type"
        },
        {
            "column": "travel_details.travel_cost",
            "label": "sum_travel_cost",
            "id": "9fd4df3f-6cb3-4677-ab8a-ef26e08abfc1",
            "type": {
                "backendDataType": "java.lang.Integer",
                "dataType": "numeric"
            },
            "autogen_alias": "min_travel_cost",
            "isNormalTable": true,
            "tableAlias": "travel_details",
            "aggregate": [
                "db.generic.aggregate.avg"
            ],
            "orderByColumn": false,
            "showOrderByColumn": false,
            "addedAs": "row",
            "floatingType": "",
            "functionsDefinition": "",
            "applyBeforeAggregate": false,
            "hiddenIncludeInResultSet": false,
            "metaDataAlias": "travel_cost"
        }
    ],
    "marksList": [
        {
            "value": "_all_",
            "id": "ddc1925d-e109-4301-9b02-34c0e1ef918d",
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
            "id": "9fd4df3f-6cb3-4677-ab8a-ef26e08abfc1",
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
    ]
}

const minAggOutput = [
    {
        "name": "travel_medium",
        "type": "dimension",
        "funcName": null
    },
    {
        "name": "travel_type",
        "type": "dimension",
        "funcName": null
    },
    {
        "name": "min_travel_cost",
        "type": "measure",
        "funcName": null,
        "defAggFn": "min"
    }
]

const maxAggOutput = [
    {
        "name": "travel_medium",
        "type": "dimension",
        "funcName": null
    },
    {
        "name": "travel_type",
        "type": "dimension",
        "funcName": null
    },
    {
        "name": "min_travel_cost",
        "type": "measure",
        "funcName": null,
        "defAggFn": "max"
    }
]

const avgAggOutput = [
    {
        "name": "travel_medium",
        "type": "dimension",
        "funcName": null
    },
    {
        "name": "travel_type",
        "type": "dimension",
        "funcName": null
    },
    {
        "name": "min_travel_cost",
        "type": "measure",
        "funcName": null,
        "defAggFn": "avg"
    }
]   

describe("Grid Chart Aggregations", () => { 
    test("aggregations: Min", () => {
        const {schema} = getFieldInfo(minAggReportData)
        expect(schema).toEqual(minAggOutput);
    });

    test("aggregations: Max", () => {
        const {schema} = getFieldInfo(maxAggReportData)
        expect(schema).toEqual(maxAggOutput);
    });

    test("aggregations: Avg", () => {
        const {schema} = getFieldInfo(avgAggReportData)
        expect(schema).toEqual(avgAggOutput);
    });
})