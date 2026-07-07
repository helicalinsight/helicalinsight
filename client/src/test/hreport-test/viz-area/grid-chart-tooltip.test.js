import { formatterFn } from "../../../components/hi-reports/hi-viz-area/utils/grid-chart-utils";


describe("grid chart tooltip", () => {
    test("jest example",async () => {
        expect(1+1).toBeTruthy();
    });
    test("basic grid chart tooltip",async () => {
        const dataStore = {
            getData: () =>{
                return {
                    data:[['Flight', 17752, 2]],
                    schema:[
                        {
                            "name": "travel_medium",
                            "type": "dimension",
                            "subtype": "categorical",
                            "defAggFn": "sum",
                            "displayName": "travel_medium"
                        },
                        {
                            "name": "sum_destination_id",
                            "type": "measure",
                            "subtype": "continuous",
                            "defAggFn": "sum",
                            "displayName": "sum_destination_id"
                        },
                        {
                            "name": "__id__",
                            "displayName": "__id__",
                            "type": "measure",
                            "subtype": "__id",
                            "defAggFn": "sum"
                        }
                    ]
                }
            }
        };
        const context = {
            payload:{
                target:[]
            }
        }
        const color = []
        const size = []
        const schema = [
            {
                "name": "sum_destination_id",
                "type": "measure",
                "funcName": null
            },
            {
                "name": "travel_medium",
                "type": "dimension",
                "funcName": null
            }
        ]
        const tooltipField = ""
        const template = formatterFn({dataStore, context,color,size,schema,tooltipField})
        expect(template.data).toEqual([
            { field: 'sum_destination_id', value: 17752 },
            { field: 'travel_medium', value: 'Flight' }
          ])
    });
    test("grid chart tooltip with canvas field as color mark ",async () => {
        const dataStore = {
            getData: () =>{
                return {
                    data:[['Flight', 17752, 2]],
                    schema:[
                        {
                            "name": "travel_medium",
                            "type": "dimension",
                            "subtype": "categorical",
                            "defAggFn": "sum",
                            "displayName": "travel_medium"
                        },
                        {
                            "name": "sum_destination_id",
                            "type": "measure",
                            "subtype": "continuous",
                            "defAggFn": "sum",
                            "displayName": "sum_destination_id"
                        },
                        {
                            "name": "__id__",
                            "displayName": "__id__",
                            "type": "measure",
                            "subtype": "__id",
                            "defAggFn": "sum"
                        }
                    ]
                }
            }
        };
        const context = {
            payload:{
                target:[
                    ["travel_medium"],
                    ['Flight']
                ]
            }
        }
        const color = [{field:"travel_medium"}]
        const size = []
        const schema = [
            {
                "name": "sum_destination_id",
                "type": "measure",
                "funcName": null
            },
            {
                "name": "travel_medium",
                "type": "dimension",
                "funcName": null
            }
        ]
        const tooltipField = ""
        const template = formatterFn({dataStore, context,color,size,schema,tooltipField})
        expect(template.data).toEqual([
            { field: 'sum_destination_id', value: 17752 },
            { field: 'travel_medium', value: 'Flight' }
          ])
    });
    test("grid chart tooltip with new field as color mark ",async () => {
        const dataStore = {
            getData: () =>{
                return {
                    data:[
                        ["Flight","Agent",3920,6],
                        ["Flight","Makemytrip",5721,7],
                        ["Flight","Website",8111,8]
                    ],
                    schema:[
                        {
                            "name": "travel_medium",
                            "type": "dimension",
                            "subtype": "categorical",
                            "defAggFn": "sum",
                            "displayName": "travel_medium"
                        },
                        {
                            "name": "booking_platform",
                            "type": "dimension",
                            "subtype": "categorical",
                            "defAggFn": "sum",
                            "displayName": "booking_platform"
                        },
                        {
                            "name": "sum_destination_id",
                            "type": "measure",
                            "subtype": "continuous",
                            "defAggFn": "sum",
                            "displayName": "sum_destination_id"
                        },
                        {
                            "name": "__id__",
                            "displayName": "__id__",
                            "type": "measure",
                            "subtype": "__id",
                            "defAggFn": "sum"
                        }
                    ]
                }
            }
        };
        const context = {
            payload:{
                target:[
                    ["travel_medium","booking_platform"],
                    ['Flight','Agent']
                ]
            }
        }
        const color = [{field:"booking_platform"}]
        const size = []
        const schema = [
            {
                "name": "sum_destination_id",
                "type": "measure",
                "funcName": null
            },
            {
                "name": "travel_medium",
                "type": "dimension",
                "funcName": null
            },
            {
                "name": "booking_platform",
                "type": "dimension",
                "funcName": null
            }
        ]
        const tooltipField = ""
        const template = formatterFn({dataStore, context,color,size,schema,tooltipField})
        expect(template.data).toEqual([
            { field: 'sum_destination_id', value: 3920 },
            { field: 'travel_medium', value: 'Flight' },
            { field: 'booking_platform', value: 'Agent' }
          ])
    });
    test("grid chart tooltip with canvas field as size mark ",async () => {
        const dataStore = {
            getData: () =>{
                return {
                    data:[['Flight', 17752, 2]],
                    schema:[
                        {
                            "name": "travel_medium",
                            "type": "dimension",
                            "subtype": "categorical",
                            "defAggFn": "sum",
                            "displayName": "travel_medium"
                        },
                        {
                            "name": "sum_destination_id",
                            "type": "measure",
                            "subtype": "continuous",
                            "defAggFn": "sum",
                            "displayName": "sum_destination_id"
                        },
                        {
                            "name": "__id__",
                            "displayName": "__id__",
                            "type": "measure",
                            "subtype": "__id",
                            "defAggFn": "sum"
                        }
                    ]
                }
            }
        };
        const context = {
            payload:{
                target:[
                    ["travel_medium"],
                    ['Flight']
                ]
            }
        }
        const color = []
        const size = [{field:"travel_medium"}]
        const schema = [
            {
                "name": "sum_destination_id",
                "type": "measure",
                "funcName": null
            },
            {
                "name": "travel_medium",
                "type": "dimension",
                "funcName": null
            }
        ]
        const tooltipField = ""
        const template = formatterFn({dataStore, context,color,size,schema,tooltipField})
        expect(template.data).toEqual([
            { field: 'sum_destination_id', value: 17752 },
            { field: 'travel_medium', value: 'Flight' }
          ])
    });
    test("grid chart tooltip with new field as size mark ",async () => {
        const dataStore = {
            getData: () =>{
                return {
                    data:[
                        ["Flight","Agent",3920,6],
                        ["Flight","Makemytrip",5721,7],
                        ["Flight","Website",8111,8]
                    ],
                    schema:[
                        {
                            "name": "travel_medium",
                            "type": "dimension",
                            "subtype": "categorical",
                            "defAggFn": "sum",
                            "displayName": "travel_medium"
                        },
                        {
                            "name": "booking_platform",
                            "type": "dimension",
                            "subtype": "categorical",
                            "defAggFn": "sum",
                            "displayName": "booking_platform"
                        },
                        {
                            "name": "sum_destination_id",
                            "type": "measure",
                            "subtype": "continuous",
                            "defAggFn": "sum",
                            "displayName": "sum_destination_id"
                        },
                        {
                            "name": "__id__",
                            "displayName": "__id__",
                            "type": "measure",
                            "subtype": "__id",
                            "defAggFn": "sum"
                        }
                    ]
                }
            }
        };
        const context = {
            payload:{
                target:[
                    ["travel_medium","booking_platform"],
                    ['Flight','Agent']
                ]
            }
        }
        const color = []
        const size = [{field:"booking_platform"}]
        const schema = [
            {
                "name": "sum_destination_id",
                "type": "measure",
                "funcName": null
            },
            {
                "name": "travel_medium",
                "type": "dimension",
                "funcName": null
            },
            {
                "name": "booking_platform",
                "type": "dimension",
                "funcName": null
            }
        ]
        const tooltipField = ""
        const template = formatterFn({dataStore, context,color,size,schema,tooltipField})
        expect(template.data).toEqual([
            { field: 'sum_destination_id', value: 3920 },
            { field: 'travel_medium', value: 'Flight' },
            { field: 'booking_platform', value: 'Agent' }
          ])
    });
    test("grid chart tooltip with multiple layers",async () => {
        const dataStore = {
            getData: () =>{
                return {
                    data:[['Flight', 17752, 16305, 2]],
                    schema:[
                        {
                            "name": "travel_medium",
                            "type": "dimension",
                            "subtype": "categorical",
                            "defAggFn": "sum",
                            "displayName": "travel_medium"
                        },
                        {
                            "name": "sum_destination_id",
                            "type": "measure",
                            "subtype": "continuous",
                            "defAggFn": "sum",
                            "displayName": "sum_destination_id"
                        },
                        {
                            "name": "sum_source_id",
                            "type": "measure",
                            "subtype": "continuous",
                            "defAggFn": "sum",
                            "displayName": "sum_source_id"
                        },
                        {
                            "name": "__id__",
                            "displayName": "__id__",
                            "type": "measure",
                            "subtype": "__id",
                            "defAggFn": "sum"
                        }
                    ]
                }
            }
        };
        const context = {
            payload:{
                target:[
                    ["travel_medium"],
                    ['Flight']
                ]
            }
        }
        const color = []
        const size = []
        const schema = [
            {
                "name": "sum_destination_id",
                "type": "measure",
                "funcName": null
            },
            {
                "name": "sum_source_id",
                "type": "measure",
                "funcName": null
            },
            {
                "name": "travel_medium",
                "type": "dimension",
                "funcName": null
            }
        ]
        const tooltipField = ""
        const template = formatterFn({dataStore, context,color,size,schema,tooltipField})
        expect(template.data).toEqual([
            { field: 'sum_destination_id', value: 17752 },
            { field: 'sum_source_id', value: 16305 },
            { field: 'travel_medium', value: 'Flight' }
          ])
    });
    test("grid chart tooltip with new field as tooltip mark ",async () => {
        const dataStore = {
            getData: () =>{
                return {
                    data:[[ 17752,"Agra", 2]],
                    schema:[
                        {
                            "name": "sum_travel_cost",
                            "type": "measure",
                            "funcName": null
                        },
                        {
                            "name": "destination",
                            "type": "dimension",
                            "funcName": null
                        },
                        {
                            "name": "destination_id",
                            "type": "measure",
                            "funcName": null
                        }
                    ]
                }
            }
        };
        const context = {
            payload:{
                target:[
                    ["destination"],
                    ['Agra']
                ]
            }
        }
        const color = []
        const size = []
        const schema = [
            {
                "name": "sum_travel_cost",
                "type": "measure",
                "funcName": null
            },
            {
                "name": "destination",
                "type": "dimension",
                "funcName": null
            },
            {
                "name": "destination_id",
                "type": "measure",
                "funcName": null
            }
        ]
        const tooltipField = "destination_id"
        const template = formatterFn({dataStore, context,color,size,schema,tooltipField})
        expect(template.data).toEqual([
            { field: 'sum_travel_cost', value: 17752 },
            { field: 'destination', value: "Agra" },
            { field: 'destination_id', value: 2 },
          ])
    });
}); 