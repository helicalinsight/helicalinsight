function getFlowchartNodeFormat(data) {
    return {
        store: {
            data: {
                data
            }
        }
    };
}

const noBrandType = {
    "position": {
        "x": 123.97600000000003,
        "y": 32
    },
    "size": {
        "width": 160,
        "height": 40
    },
    "view": "cb363ee4-22af-4bbb-8fcc-65b2e43fd20e",
    "shape": "react-shape",
    "id": "node-82f80b17-1cfb-4482-8344-27aff9f21791",
    "label": "Text",
    "borders": {},
    "padding": {},
    "name": "text",
    "renderKey": "text",
    "parentKey": "elements",
    "isLeaf": true,
    "repeat": "na",
    "category": "text",
    "zIndex": 10,
    "type": "defaultNodes",
    "fontSize": 14,
    "data": {
        "label": "Text",
        "borders": {},
        "padding": {},
        "width": 160,
        "height": 40,
        "name": "text",
        "renderKey": "text",
        "parentKey": "elements",
        "isLeaf": true,
        "repeat": "na",
        "category": "text",
        "zIndex": 10,
        "type": "defaultNodes",
        "fontSize": 14,
        "id": "node-82f80b17-1cfb-4482-8344-27aff9f21791",
        "x": 123.97600000000003,
        "y": 32
    }
}

const withBrandType = {
    "position": {
        "x": 123.97600000000003,
        "y": 32
    },
    "size": {
        "width": 160,
        "height": 40
    },
    "view": "cb363ee4-22af-4bbb-8fcc-65b2e43fd20e",
    "shape": "react-shape",
    "id": "node-82f80b17-1cfb-4482-8344-27aff9f21791",
    "label": "Text",
    "borders": {},
    "padding": {},
    "name": "text",
    "renderKey": "text",
    "parentKey": "elements",
    "isLeaf": true,
    "repeat": "rt",
    "category": "text",
    "zIndex": 10,
    "type": "defaultNodes",
    "fontSize": 14,
    "data": {
        "label": "Text",
        "borders": {},
        "padding": {},
        "width": 160,
        "height": 40,
        "name": "text",
        "renderKey": "text",
        "parentKey": "elements",
        "isLeaf": true,
        "repeat": "rt",
        "category": "text",
        "zIndex": 10,
        "type": "defaultNodes",
        "fontSize": 14,
        "id": "node-82f80b17-1cfb-4482-8344-27aff9f21791",
        "x": 123.97600000000003,
        "y": 32
    }
}

const wrongHierarchy = [
    {
        "label": "Text",
        "borders": {},
        "padding": {},
        "width": 160,
        "height": 40,
        "name": "text",
        "renderKey": "text",
        "parentKey": "elements",
        "isLeaf": true,
        "repeat": "pg",
        "category": "text",
        "zIndex": 10,
        "type": "defaultNodes",
        "fontSize": 14,
        "id": "node-5ba19827-4f5b-4029-96f1-514c8ee68099",
        "x": 190.976,
        "y": 36
    },
    {
        "label": "Text",
        "borders": {},
        "padding": {},
        "width": 160,
        "height": 40,
        "name": "text",
        "renderKey": "text",
        "parentKey": "elements",
        "isLeaf": true,
        "repeat": "rt",
        "category": "text",
        "zIndex": 10,
        "type": "defaultNodes",
        "fontSize": 14,
        "id": "node-71f5e645-a2c3-456c-aa8c-2cfcd184c485",
        "x": 335.976,
        "y": 90
    }
]

const wrongHierarchy2 = [
    {
        "name": "dim_id",
        "width": 160,
        "height": 40,
        "label": "$F{dim_id}",
        "renderKey": "text",
        "isLeaf": true,
        "zIndex": 10,
        "type": "queryField",
        "category": "text",
        "repeat": "gp1",
        "borders": {},
        "padding": {},
        "fontSize": 14,
        "backendDataType": "java.lang.Integer",
        "id": "node-f598a6b6-667a-431c-a0e7-f0843a2ca09d",
        "x": 190,
        "y": 36,
        "isGrp": true,
        "groupBy": "node-f598a6b6-667a-431c-a0e7-f0843a2ca09d"
    },
    {
        "label": "Text",
        "borders": {},
        "padding": {},
        "width": 160,
        "height": 40,
        "name": "text",
        "renderKey": "text",
        "parentKey": "elements",
        "isLeaf": true,
        "repeat": "gp1",
        "category": "text",
        "zIndex": 10,
        "type": "defaultNodes",
        "fontSize": 14,
        "id": "node-66106b8a-7e9a-4d1f-a164-b53446f40a83",
        "x": 76,
        "y": 36,
        "groupBy": "node-f598a6b6-667a-431c-a0e7-f0843a2ca09d"
    },
    {
        "name": "fiscal_year",
        "width": 160,
        "height": 40,
        "label": "$F{fiscal_year}",
        "renderKey": "text",
        "isLeaf": true,
        "zIndex": 10,
        "type": "queryField",
        "category": "text",
        "repeat": "gp2",
        "borders": {},
        "padding": {},
        "fontSize": 14,
        "backendDataType": "java.sql.Date",
        "id": "node-dc228fea-9b44-43ac-9c6b-8b83b717bd1a",
        "x": 302.976,
        "y": 36,
        "isGrp": true,
        "groupBy": "node-dc228fea-9b44-43ac-9c6b-8b83b717bd1a"
    },
    {
        "label": "Text",
        "borders": {},
        "padding": {},
        "width": 160,
        "height": 40,
        "name": "text",
        "renderKey": "text",
        "parentKey": "elements",
        "isLeaf": true,
        "repeat": "gp2",
        "category": "text",
        "zIndex": 10,
        "type": "defaultNodes",
        "fontSize": 14,
        "id": "node-e18af979-3c6b-4e89-b03d-b2869d2a5d15",
        "x": 129,
        "y": 40,
        "groupBy": "node-dc228fea-9b44-43ac-9c6b-8b83b717bd1a"
    }
]

const wrongHierarchy3 = [
    {
        "label": "Text",
        "borders": {},
        "padding": {},
        "width": 160,
        "height": 40,
        "name": "text",
        "renderKey": "text",
        "parentKey": "elements",
        "isLeaf": true,
        "repeat": "gp1",
        "category": "text",
        "zIndex": 10,
        "type": "defaultNodes",
        "fontSize": 14,
        "id": "node-bc8ce2e5-1332-4186-8c17-f9605d76adfe",
        "x": 66.97600000000003,
        "y": 40,
        "groupBy": "node-1baeed59-3464-4f2e-9b1f-4a41200fd558"
    },
    {
        "name": "dim_id",
        "width": 160,
        "height": 40,
        "label": "$F{dim_id}",
        "renderKey": "text",
        "isLeaf": true,
        "zIndex": 10,
        "type": "queryField",
        "category": "text",
        "repeat": "cl",
        "borders": {},
        "padding": {},
        "fontSize": 14,
        "backendDataType": "java.lang.Integer",
        "id": "node-1baeed59-3464-4f2e-9b1f-4a41200fd558",
        "x": 344.976,
        "y": 90,
        "isGrp": true
    }
]

const wrongHierarchy4 = [
    {
        "name": "dim_id",
        "width": 160,
        "height": 40,
        "label": "$F{dim_id}",
        "renderKey": "text",
        "isLeaf": true,
        "zIndex": 10,
        "type": "queryField",
        "category": "text",
        "repeat": "rt",
        "borders": {},
        "padding": {},
        "fontSize": 14,
        "backendDataType": "java.lang.Integer",
        "id": "node-f598a6b6-667a-431c-a0e7-f0843a2ca09d",
        "x": 0,
        "y": 0,
        "isGrp": true,
        "groupBy": "node-f598a6b6-667a-431c-a0e7-f0843a2ca09d"
    },
    {
        "label": "Text",
        "borders": {},
        "padding": {},
        "width": 160,
        "height": 40,
        "name": "text",
        "renderKey": "text",
        "parentKey": "elements",
        "isLeaf": true,
        "repeat": "pg",
        "category": "text",
        "zIndex": 10,
        "type": "defaultNodes",
        "fontSize": 14,
        "id": "node-66106b8a-7e9a-4d1f-a164-b53446f40a83",
        "x": 0,
        "y": 39,
        "groupBy": "node-f598a6b6-667a-431c-a0e7-f0843a2ca09d"
    },
]

const groupWrongHierarchy = [
    {
        "name": "dim_id",
        "width": 160,
        "height": 40,
        "label": "$F{dim_id}",
        "renderKey": "text",
        "isLeaf": true,
        "zIndex": 10,
        "type": "queryField",
        "category": "text",
        "repeat": "gp1",
        "borders": {},
        "padding": {},
        "fontSize": 14,
        "backendDataType": "java.lang.Integer",
        "id": "node-23c8f7d0-9ac9-46bb-8a4d-e013767973fb",
        "x": 294.976,
        "y": 43,
        "isGrp": true,
        "groupBy": "node-23c8f7d0-9ac9-46bb-8a4d-e013767973fb"
    },
    {
        "label": "Text",
        "borders": {},
        "padding": {},
        "width": 160,
        "height": 40,
        "name": "text",
        "renderKey": "text",
        "parentKey": "elements",
        "isLeaf": true,
        "repeat": "rd",
        "category": "text",
        "zIndex": 10,
        "type": "defaultNodes",
        "fontSize": 14,
        "id": "node-2c4e5629-a26f-4a8d-9713-445cfbe5884c",
        "x": 319.976,
        "y": 114
    },
    {
        "label": "Text",
        "borders": {},
        "padding": {},
        "width": 160,
        "height": 40,
        "name": "text",
        "renderKey": "text",
        "parentKey": "elements",
        "isLeaf": true,
        "repeat": "gp1",
        "category": "text",
        "zIndex": 10,
        "type": "defaultNodes",
        "fontSize": 14,
        "id": "node-cf2e3249-87de-4e33-8a5d-159228640bbf",
        "x": 342.976,
        "y": 193,
        "groupBy": "node-23c8f7d0-9ac9-46bb-8a4d-e013767973fb"
    },
    {
        "name": "fiscal_year",
        "width": 160,
        "height": 40,
        "label": "$F{fiscal_year}",
        "renderKey": "text",
        "isLeaf": true,
        "zIndex": 10,
        "type": "queryField",
        "category": "text",
        "repeat": "gp2",
        "borders": {},
        "padding": {},
        "fontSize": 14,
        "backendDataType": "java.sql.Date",
        "id": "node-88a67acc-806d-4242-b54a-fe7047cc7a89",
        "x": 294.976,
        "y": 271,
        "isGrp": true,
        "groupBy": "node-88a67acc-806d-4242-b54a-fe7047cc7a89"
    }
]
const groupsHierarchy = [
    {
        "name": "dim_id",
        "width": 160,
        "height": 40,
        "label": "$F{dim_id}",
        "renderKey": "text",
        "isLeaf": true,
        "zIndex": 10,
        "type": "queryField",
        "category": "text",
        "repeat": "gp1",
        "borders": {},
        "padding": {},
        "fontSize": 14,
        "backendDataType": "java.lang.Integer",
        "id": "node-23c8f7d0-9ac9-46bb-8a4d-e013767973fb",
        "x": 294.976,
        "y": 43,
        "isGrp": true,
        "groupBy": "node-23c8f7d0-9ac9-46bb-8a4d-e013767973fb"
    },
    {
        "label": "Text",
        "borders": {},
        "padding": {},
        "width": 160,
        "height": 40,
        "name": "text",
        "renderKey": "text",
        "parentKey": "elements",
        "isLeaf": true,
        "repeat": "rd",
        "category": "text",
        "zIndex": 10,
        "type": "defaultNodes",
        "fontSize": 14,
        "id": "node-2c4e5629-a26f-4a8d-9713-445cfbe5884c",
        "x": 319.976,
        "y": 114
    },
    {
        "label": "Text",
        "borders": {},
        "padding": {},
        "width": 160,
        "height": 40,
        "name": "text",
        "renderKey": "text",
        "parentKey": "elements",
        "isLeaf": true,
        "repeat": "gp2",
        "category": "text",
        "zIndex": 10,
        "type": "defaultNodes",
        "fontSize": 14,
        "id": "node-2e02b6a8-8ad7-4050-a4d8-9c724b9b7763",
        "x": 340.976,
        "y": 170,
        "groupBy": "node-88a67acc-806d-4242-b54a-fe7047cc7a89"
    },
    {
        "label": "Text",
        "borders": {},
        "padding": {},
        "width": 160,
        "height": 40,
        "name": "text",
        "renderKey": "text",
        "parentKey": "elements",
        "isLeaf": true,
        "repeat": "rd",
        "category": "text",
        "zIndex": 10,
        "type": "defaultNodes",
        "fontSize": 14,
        "id": "node-d6866dfa-965b-42b8-a0b1-de796cb7a0b8",
        "x": 326.976,
        "y": 258
    },
    {
        "name": "fiscal_year",
        "width": 160,
        "height": 40,
        "label": "$F{fiscal_year}",
        "renderKey": "text",
        "isLeaf": true,
        "zIndex": 10,
        "type": "queryField",
        "category": "text",
        "repeat": "gp2",
        "borders": {},
        "padding": {},
        "fontSize": 14,
        "backendDataType": "java.sql.Date",
        "id": "node-88a67acc-806d-4242-b54a-fe7047cc7a89",
        "x": 294.976,
        "y": 321,
        "isGrp": true,
        "groupBy": "node-88a67acc-806d-4242-b54a-fe7047cc7a89"
    },
    {
        "label": "Text",
        "borders": {},
        "padding": {},
        "width": 160,
        "height": 40,
        "name": "text",
        "renderKey": "text",
        "parentKey": "elements",
        "isLeaf": true,
        "repeat": "gp1",
        "category": "text",
        "zIndex": 10,
        "type": "defaultNodes",
        "fontSize": 14,
        "id": "node-cf2e3249-87de-4e33-8a5d-159228640bbf",
        "x": 310,
        "y": 410,
        "groupBy": "node-23c8f7d0-9ac9-46bb-8a4d-e013767973fb"
    }
]

const groupsWithRecords = [
    {
        "name": "dim_id",
        "width": 160,
        "height": 40,
        "label": "$F{dim_id}",
        "renderKey": "text",
        "isLeaf": true,
        "zIndex": 10,
        "type": "queryField",
        "category": "text",
        "repeat": "gp3",
        "borders": {},
        "padding": {},
        "fontSize": 14,
        "id": "node-1",
        "x": 100,
        "y": 50,
        "isGrp": true
    },
    {
        "name": "fiscal_year",
        "width": 160,
        "height": 40,
        "label": "$F{fiscal_year}",
        "renderKey": "text",
        "isLeaf": true,
        "zIndex": 10,
        "type": "queryField",
        "category": "text",
        "repeat": "gp2",
        "borders": {},
        "padding": {},
        "fontSize": 14,
        "id": "node-2",
        "x": 100,
        "y": 100,
        "isGrp": true
    },
    {
        "label": "Record1",
        "width": 160,
        "height": 40,
        "name": "record1",
        "renderKey": "text",
        "isLeaf": true,
        "repeat": "rd",
        "category": "text",
        "zIndex": 10,
        "type": "defaultNodes",
        "fontSize": 14,
        "id": "node-3",
        "x": 100,
        "y": 150
    },
    {
        "name": "fiscal_year",
        "width": 160,
        "height": 40,
        "label": "$F{fiscal_year}",
        "renderKey": "text",
        "isLeaf": true,
        "zIndex": 10,
        "type": "queryField",
        "category": "text",
        "repeat": "gp2",
        "borders": {},
        "padding": {},
        "fontSize": 14,
        "id": "node-4",
        "x": 100,
        "y": 200,
        "isGrp": true
    },
    {
        "label": "Record2",
        "width": 160,
        "height": 40,
        "name": "record2",
        "renderKey": "text",
        "isLeaf": true,
        "repeat": "rd",
        "category": "text",
        "zIndex": 10,
        "type": "defaultNodes",
        "fontSize": 14,
        "id": "node-5",
        "x": 100,
        "y": 250
    },
    {
        "name": "dim_id",
        "width": 160,
        "height": 40,
        "label": "$F{dim_id}",
        "renderKey": "text",
        "isLeaf": true,
        "zIndex": 10,
        "type": "queryField",
        "category": "text",
        "repeat": "gp3",
        "borders": {},
        "padding": {},
        "fontSize": 14,
        "id": "node-6",
        "x": 100,
        "y": 300,
        "isGrp": true
    }
];

const validNestedGroups = [
    {
        "name": "outer_group",
        "width": 160,
        "height": 40,
        "label": "Outer Group",
        "renderKey": "text",
        "isLeaf": true,
        "zIndex": 10,
        "type": "queryField",
        "category": "text",
        "repeat": "gp1",
        "borders": {},
        "padding": {},
        "fontSize": 14,
        "id": "node-1",
        "x": 100,
        "y": 50,
        "isGrp": true
    },
    {
        "name": "inner_group",
        "width": 160,
        "height": 40,
        "label": "Inner Group",
        "renderKey": "text",
        "isLeaf": true,
        "zIndex": 10,
        "type": "queryField",
        "category": "text",
        "repeat": "gp2",
        "borders": {},
        "padding": {},
        "fontSize": 14,
        "id": "node-2",
        "x": 100,
        "y": 100,
        "isGrp": true
    },
    {
        "label": "Record",
        "width": 160,
        "height": 40,
        "name": "record",
        "renderKey": "text",
        "isLeaf": true,
        "repeat": "rd",
        "category": "text",
        "zIndex": 10,
        "type": "defaultNodes",
        "fontSize": 14,
        "id": "node-3",
        "x": 100,
        "y": 150
    },
    {
        "name": "inner_group",
        "width": 160,
        "height": 40,
        "label": "Inner Group",
        "renderKey": "text",
        "isLeaf": true,
        "zIndex": 10,
        "type": "queryField",
        "category": "text",
        "repeat": "gp2",
        "borders": {},
        "padding": {},
        "fontSize": 14,
        "id": "node-4",
        "x": 100,
        "y": 200,
        "isGrp": true
    },
    {
        "name": "outer_group",
        "width": 160,
        "height": 40,
        "label": "Outer Group",
        "renderKey": "text",
        "isLeaf": true,
        "zIndex": 10,
        "type": "queryField",
        "category": "text",
        "repeat": "gp1",
        "borders": {},
        "padding": {},
        "fontSize": 14,
        "id": "node-5",
        "x": 100,
        "y": 250,
        "isGrp": true
    }
];

const validBandOrder = [
    {
        "label": "Report Title",
        "width": 160,
        "height": 40,
        "name": "title",
        "renderKey": "text",
        "isLeaf": true,
        "repeat": "rt",
        "category": "text",
        "zIndex": 10,
        "type": "defaultNodes",
        "fontSize": 14,
        "id": "node-1",
        "x": 100,
        "y": 50
    },
    {
        "label": "Page Header",
        "width": 160,
        "height": 40,
        "name": "pageHeader",
        "renderKey": "text",
        "isLeaf": true,
        "repeat": "pg",
        "category": "text",
        "zIndex": 10,
        "type": "defaultNodes",
        "fontSize": 14,
        "id": "node-2",
        "x": 100,
        "y": 100
    },
    {
        "label": "Column Header",
        "width": 160,
        "height": 40,
        "name": "columnHeader",
        "renderKey": "text",
        "isLeaf": true,
        "repeat": "cl",
        "category": "text",
        "zIndex": 10,
        "type": "defaultNodes",
        "fontSize": 14,
        "id": "node-3",
        "x": 100,
        "y": 150
    },
    {
        "label": "Record",
        "width": 160,
        "height": 40,
        "name": "record",
        "renderKey": "text",
        "isLeaf": true,
        "repeat": "rd",
        "category": "text",
        "zIndex": 10,
        "type": "defaultNodes",
        "fontSize": 14,
        "id": "node-4",
        "x": 100,
        "y": 200
    }
];

const invalidBandOrder = [
    {
        "label": "Page Header",
        "width": 160,
        "height": 40,
        "name": "pageHeader",
        "renderKey": "text",
        "isLeaf": true,
        "repeat": "pg",
        "category": "text",
        "zIndex": 10,
        "type": "defaultNodes",
        "fontSize": 14,
        "id": "node-1",
        "x": 100,
        "y": 50
    },
    {
        "label": "Report Title",
        "width": 160,
        "height": 40,
        "name": "title",
        "renderKey": "text",
        "isLeaf": true,
        "repeat": "rt",
        "category": "text",
        "zIndex": 10,
        "type": "defaultNodes",
        "fontSize": 14,
        "id": "node-2",
        "x": 100,
        "y": 100
    }
];

const invalidGroupOrder = [
    {
        "name": "inner_group",
        "width": 160,
        "height": 40,
        "label": "Inner Group",
        "renderKey": "text",
        "isLeaf": true,
        "zIndex": 10,
        "type": "queryField",
        "category": "text",
        "repeat": "gp2",
        "borders": {},
        "padding": {},
        "fontSize": 14,
        "id": "node-1",
        "x": 100,
        "y": 50,
        "isGrp": true
    },
    {
        "name": "outer_group",
        "width": 160,
        "height": 40,
        "label": "Outer Group",
        "renderKey": "text",
        "isLeaf": true,
        "zIndex": 10,
        "type": "queryField",
        "category": "text",
        "repeat": "gp1",
        "borders": {},
        "padding": {},
        "fontSize": 14,
        "id": "node-2",
        "x": 100,
        "y": 100,
        "isGrp": true
    }
];

export const nodeValidationTest = [
    {
        title: 'test with no repeatBy',
        input: {
            flowchartInstance: {
                current: {
                    getNodes() {
                        return [getFlowchartNodeFormat(noBrandType)]
                    },
                    getArea() {
                        return {
                            "x": 0,
                            "y": 0,
                            "width": 1107,
                            "height": 842
                        }
                    }
                }
            }
        },
        output: false
    },
    {
        title: 'test with repeatBy',
        input: {
            flowchartInstance: {
                current: {
                    getNodes() {
                        return [getFlowchartNodeFormat(withBrandType)]
                    },
                    getArea() {
                        return {
                            "x": 0,
                            "y": 0,
                            "width": 1107,
                            "height": 842
                        }
                    }
                }
            },

        },
        output: true
    },
    {
        title: 'test node hierarchy 1',
        input: {
            hcrDiagramNodesData: wrongHierarchy,
            flowchartInstance: {
                current: {
                    getArea() {
                        return {
                            "x": 0,
                            "y": 0,
                            "width": 1107,
                            "height": 842
                        }
                    }
                }
            },
            canvasProperties: {
                "margin": {},
                "layout": {
                    "name": "A4",
                    "orientation": "Portrait",
                    "size": {
                        "width": 595,
                        "height": 842
                    }
                },
                "pageProperties": {
                    "columnCount": 1
                },
                "calculations": {
                    "selectCalculation": "",
                    "options": [],
                    "keyValuePairs": {
                        "id": "8167e99f-a56f-4299-bf77-acea59d34c74"
                    }
                },
                "previewParameters": {},
                "groupProperties": {
                    "selectGroup": "",
                    "options": []
                },
                "pageStyles": {
                    "selectStyles": "",
                    "options": [],
                    "keyValuePairs": {
                        "id": "64fedade-7bc4-456b-8083-526e0d1f49a6",
                        "borders": {},
                        "padding": {},
                        "lineStyles": {}
                    }
                }
            }
        },
        output: true
    },
    {
        title: 'test node hierarchy 2',
        input: {
            hcrDiagramNodesData: wrongHierarchy2,
            flowchartInstance: {
                current: {
                    getArea() {
                        return {
                            "x": 0,
                            "y": 0,
                            "width": 1107,
                            "height": 842
                        }
                    }
                }
            },
            canvasProperties: {
                "margin": {},
                "layout": {
                    "name": "A4",
                    "orientation": "Portrait",
                    "size": {
                        "width": 595,
                        "height": 842
                    }
                },
                "pageProperties": {
                    "columnCount": 1
                },
                "calculations": {
                    "selectCalculation": "",
                    "options": [],
                    "keyValuePairs": {
                        "id": "8167e99f-a56f-4299-bf77-acea59d34c74"
                    }
                },
                "previewParameters": {},
                "groupProperties": {
                    "selectGroup": "",
                    "options": [
                        {
                            "nodeId": "node-f598a6b6-667a-431c-a0e7-f0843a2ca09d",
                            "id": 1,
                            "expression": "$F{dim_id}",
                            "name": "group_dim_id"
                        },
                        {
                            "nodeId": "node-dc228fea-9b44-43ac-9c6b-8b83b717bd1a",
                            "id": 2,
                            "expression": "$F{fiscal_year}",
                            "name": "group_fiscal_year"
                        }
                    ]
                },
                "pageStyles": {
                    "selectStyles": "",
                    "options": [],
                    "keyValuePairs": {
                        "id": "64fedade-7bc4-456b-8083-526e0d1f49a6",
                        "borders": {},
                        "padding": {},
                        "lineStyles": {}
                    }
                }
            }
        },
        output: false
    },
    {
        title: 'test node hierarchy 3',
        input: {
            hcrDiagramNodesData: wrongHierarchy3,
            flowchartInstance: {
                current: {
                    getArea() {
                        return {
                            "x": 0,
                            "y": 0,
                            "width": 1107,
                            "height": 842
                        }
                    }
                }
            },
            canvasProperties: {
                "margin": {},
                "layout": {
                    "name": "A4",
                    "orientation": "Portrait",
                    "size": {
                        "width": 595,
                        "height": 842
                    }
                },
                "pageProperties": {
                    "columnCount": 1
                },
                "calculations": {
                    "selectCalculation": "",
                    "options": [],
                    "keyValuePairs": {
                        "id": "764ffc23-01d7-4862-9379-176b70b483eb"
                    }
                },
                "previewParameters": {},
                "groupProperties": {
                    "selectGroup": "",
                    "options": [
                        {
                            "nodeId": "node-1baeed59-3464-4f2e-9b1f-4a41200fd558",
                            "id": 1,
                            "expression": "$F{dim_id}",
                            "name": "group_dim_id"
                        }
                    ]
                },
                "pageStyles": {
                    "selectStyles": "",
                    "options": [],
                    "keyValuePairs": {
                        "id": "5cb42b01-3e10-440e-8cde-ac6505507ab5",
                        "borders": {},
                        "padding": {},
                        "lineStyles": {}
                    }
                }
            }
        },
        output: true
    },
    {
        title: 'Groups wrong Hirrarchy',
        input: {
            hcrDiagramNodesData: groupWrongHierarchy,
            flowchartInstance: {
                current: {
                    getArea() {
                        return {
                            "x": 0,
                            "y": 0,
                            "width": 1107,
                            "height": 842
                        }
                    }
                }
            },
            canvasProperties: {
                "margin": {},
                "layout": {
                    "name": "A4",
                    "orientation": "Portrait",
                    "size": {
                        "width": 595,
                        "height": 842
                    }
                },
                "pageProperties": {
                    "columnCount": 1
                },
                "calculations": {
                    "selectCalculation": "",
                    "options": [],
                    "keyValuePairs": {
                        "id": "64fbdf0d-8d1b-4eb4-b227-d2160482311d"
                    }
                },
                "previewParameters": {},
                "groupProperties": {
                    "selectGroup": "",
                    "options": [
                        {
                            "nodeId": "node-23c8f7d0-9ac9-46bb-8a4d-e013767973fb",
                            "id": 1,
                            "expression": "$F{dim_id}",
                            "name": "group_dim_id"
                        },
                        {
                            "nodeId": "node-88a67acc-806d-4242-b54a-fe7047cc7a89",
                            "id": 2,
                            "expression": "$F{fiscal_year}",
                            "name": "group_fiscal_year"
                        }
                    ]
                },
                "pageStyles": {
                    "selectStyles": "",
                    "options": [],
                    "keyValuePairs": {
                        "id": "f92b9cd0-abcc-406c-8f0f-eccba89f9364",
                        "borders": {},
                        "padding": {},
                        "lineStyles": {}
                    }
                }
            }
        },
        output: false
    },
    {
        title: 'Groups correct Hirrarchy',
        input: {
            hcrDiagramNodesData: groupsHierarchy,
            flowchartInstance: {
                current: {
                    getArea() {
                        return {
                            "x": 0,
                            "y": 0,
                            "width": 1107,
                            "height": 842
                        }
                    }
                }
            },
            canvasProperties: {
                "margin": {},
                "layout": {
                    "name": "A4",
                    "orientation": "Portrait",
                    "size": {
                        "width": 595,
                        "height": 842
                    }
                },
                "pageProperties": {
                    "columnCount": 1
                },
                "calculations": {
                    "selectCalculation": "",
                    "options": [],
                    "keyValuePairs": {
                        "id": "64fbdf0d-8d1b-4eb4-b227-d2160482311d"
                    }
                },
                "previewParameters": {},
                "groupProperties": {
                    "selectGroup": "",
                    "options": [
                        {
                            "nodeId": "node-23c8f7d0-9ac9-46bb-8a4d-e013767973fb",
                            "id": 1,
                            "expression": "$F{dim_id}",
                            "name": "group_dim_id"
                        },
                        {
                            "nodeId": "node-88a67acc-806d-4242-b54a-fe7047cc7a89",
                            "id": 2,
                            "expression": "$F{fiscal_year}",
                            "name": "group_fiscal_year"
                        }
                    ]
                },
                "pageStyles": {
                    "selectStyles": "",
                    "options": [],
                    "keyValuePairs": {
                        "id": "f92b9cd0-abcc-406c-8f0f-eccba89f9364",
                        "borders": {},
                        "padding": {},
                        "lineStyles": {}
                    }
                }
            }
        },
        output: false
    },
    {
        title: 'test groups with records in correct order',
        input: {
            hcrDiagramNodesData: groupsWithRecords,
            flowchartInstance: {
                current: {
                    getArea() {
                        return {
                            "x": 0,
                            "y": 0,
                            "width": 1107,
                            "height": 842
                        }
                    }
                }
            },
            canvasProperties: {
                "margin": {},
                "layout": {
                    "name": "A4",
                    "orientation": "Portrait",
                    "size": {
                        "width": 595,
                        "height": 842
                    }
                },
                "groupProperties": {
                    "selectGroup": "",
                    "options": [
                        {
                            "id": 3,
                            "name": "group_3"
                        },
                        {
                            "id": 2,
                            "name": "group_2"
                        }
                    ]
                }
            }
        },
        output: false
    },
    {
        title: 'test valid nested groups with record',
        input: {
            hcrDiagramNodesData: validNestedGroups,
            flowchartInstance: {
                current: {
                    getArea() {
                        return {
                            "x": 0,
                            "y": 0,
                            "width": 1107,
                            "height": 842
                        }
                    }
                }
            },
            canvasProperties: {
                "margin": {},
                "layout": {
                    "name": "A4",
                    "orientation": "Portrait",
                    "size": {
                        "width": 595,
                        "height": 842
                    }
                },
                "groupProperties": {
                    "selectGroup": "",
                    "options": [
                        {
                            "id": 1,
                            "name": "group_1"
                        },
                        {
                            "id": 2,
                            "name": "group_2"
                        }
                    ]
                }
            }
        },
        output: true
    },
    {
        title: 'test valid band order',
        input: {
            hcrDiagramNodesData: validBandOrder,
            flowchartInstance: {
                current: {
                    getArea() {
                        return {
                            "x": 0,
                            "y": 0,
                            "width": 1107,
                            "height": 842
                        }
                    }
                }
            },
            canvasProperties: {
                "margin": {},
                "layout": {
                    "name": "A4",
                    "orientation": "Portrait",
                    "size": {
                        "width": 595,
                        "height": 842
                    }
                },
                "groupProperties": {
                    "selectGroup": "",
                    "options": []
                }
            }
        },
        output: true
    },
    {
        title: 'test invalid band order',
        input: {
            hcrDiagramNodesData: invalidBandOrder,
            flowchartInstance: {
                current: {
                    getArea() {
                        return {
                            "x": 0,
                            "y": 0,
                            "width": 1107,
                            "height": 842
                        }
                    }
                }
            },
            canvasProperties: {
                "margin": {},
                "layout": {
                    "name": "A4",
                    "orientation": "Portrait",
                    "size": {
                        "width": 595,
                        "height": 842
                    }
                },
                "groupProperties": {
                    "selectGroup": "",
                    "options": []
                }
            }
        },
        output: true
    },
    {
        title: 'test invalid group order',
        input: {
            hcrDiagramNodesData: invalidGroupOrder,
            flowchartInstance: {
                current: {
                    getArea() {
                        return {
                            "x": 0,
                            "y": 0,
                            "width": 1107,
                            "height": 842
                        }
                    }
                }
            },
            canvasProperties: {
                "margin": {},
                "layout": {
                    "name": "A4",
                    "orientation": "Portrait",
                    "size": {
                        "width": 595,
                        "height": 842
                    }
                },
                "groupProperties": {
                    "selectGroup": "",
                    "options": [
                        {
                            "id": 1,
                            "name": "group_1"
                        },
                        {
                            "id": 2,
                            "name": "group_2"
                        }
                    ]
                }
            }
        },
        output: true
    },
    {
        title: 'test node hierarchy 4',
        input: {
            hcrDiagramNodesData: wrongHierarchy4,
            flowchartInstance: {
                current: {
                    getArea() {
                        return {
                            "x": 0,
                            "y": 0,
                            "width": 1107,
                            "height": 842
                        }
                    }
                }
            },
            canvasProperties: {
                "margin": {},
                "layout": {
                    "name": "A4",
                    "orientation": "Portrait",
                    "size": {
                        "width": 595,
                        "height": 842
                    }
                },
                "pageProperties": {
                    "columnCount": 1
                },
                "calculations": {
                    "selectCalculation": "",
                    "options": [],
                    "keyValuePairs": {
                        "id": "8167e99f-a56f-4299-bf77-acea59d34c74"
                    }
                },
                "previewParameters": {},
                "groupProperties": {
                    "selectGroup": "",
                    "options": [
                        {
                            "nodeId": "node-f598a6b6-667a-431c-a0e7-f0843a2ca09d",
                            "id": 1,
                            "expression": "$F{dim_id}",
                            "name": "group_dim_id"
                        },
                        {
                            "nodeId": "node-dc228fea-9b44-43ac-9c6b-8b83b717bd1a",
                            "id": 2,
                            "expression": "$F{fiscal_year}",
                            "name": "group_fiscal_year"
                        }
                    ]
                },
                "pageStyles": {
                    "selectStyles": "",
                    "options": [],
                    "keyValuePairs": {
                        "id": "64fedade-7bc4-456b-8083-526e0d1f49a6",
                        "borders": {},
                        "padding": {},
                        "lineStyles": {}
                    }
                }
            }
        },
        output: false
    },
]
