import { handleEmptyPaneCheck } from "../../../components/hi-canned-reports/hcrHelperMethods";

const arr = [{
    input: [{
        "title": "Untitled 1",
        "key": "1",
        "uuid": "7c5cc049-e48b-4681-8e9e-85d5c34d5c43",
        "hcrFiltersDrawerStatus": false,
        "hcrDiagramNodesData": [],
        "selectedConnectionDetails": null,
        "dsPaneTypes": [
            {
                "key": "query",
                "dataSourcePane": "Query",
                "menu": [
                    {
                        "id": 1,
                        "name": "Query1",
                        "config": "",
                        "connectionDetails": null,
                        "executeQueryData": {
                            "data": [],
                            "field": []
                        },
                        "isNameEditable": false,
                        "isSaved": true,
                        "parameterList": []
                    }
                ]
            },
            {
                "key": "parameter",
                "dataSourcePane": "Parameter",
                "menu": []
            }
        ],
        "selectedDS": {
            "dataSourcePane": "Query",
            "id": 1
        },
        "selectedQueryId": null,
        "canvasProperties": {
            "margin": { top: 0 },
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
                    "id": "b5cf7ea3-e271-4a3b-9419-2407e0f63298"
                }
            },
            "previewParameters": {
                "showParameters": true
            },
            "groupProperties": {
                "selectGroup": "",
                "options": []
            },
            "pageStyles": {
                "selectStyles": "",
                "options": [],
                "keyValuePairs": {
                    "borders": {},
                    "padding": {},
                    "lineStyles": {}
                }
            }
        },
        "groupsOrder": [],
        "groupCount": 0,
        "sidebarPaneActiveKey": "datasource",
        "isPreviewing": false,
        "previewTag": "",
        "pageDetails": {
            "totalPageCount": 10,
            "currentPageNo": 1
        }
    }], // changed top
    output: undefined
}, {
    input: [{
        "title": "Untitled 1",
        "key": "1",
        "uuid": "7c5cc049-e48b-4681-8e9e-85d5c34d5c43",
        "hcrFiltersDrawerStatus": false,
        "hcrDiagramNodesData": [],
        "selectedConnectionDetails": null,
        "dsPaneTypes": [
            {
                "key": "query",
                "dataSourcePane": "Query",
                "menu": [
                    {
                        "id": 1,
                        "name": "Query1",
                        "config": "",
                        "connectionDetails": null,
                        "executeQueryData": {
                            "data": [],
                            "field": []
                        },
                        "isNameEditable": false,
                        "isSaved": false,
                        "parameterList": []
                    }
                ]
            },
            {
                "key": "parameter",
                "dataSourcePane": "Parameter",
                "menu": []
            }
        ],
        "selectedDS": {
            "dataSourcePane": "Query",
            "id": 1
        },
        "selectedQueryId": null,
        "canvasProperties": {
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
                    "id": "b5cf7ea3-e271-4a3b-9419-2407e0f63298"
                }
            },
            "previewParameters": {
                "showParameters": true
            },
            "groupProperties": {
                "selectGroup": "",
                "options": []
            },
            "pageStyles": {
                "selectStyles": "",
                "options": [],
                "keyValuePairs": {
                    "borders": {},
                    "padding": {},
                    "lineStyles": {}
                }
            }
        },
        "groupsOrder": [],
        "groupCount": 0,
        "sidebarPaneActiveKey": "datasource",
        "isPreviewing": false,
        "previewTag": "",
        "pageDetails": {
            "totalPageCount": 10,
            "currentPageNo": 1
        }
    }], // changed q1 saved prop
    output: undefined
}]

arr.forEach(ele => {
    test("Testing handleEmptyPaneCheck func", (done) => {
        expect(handleEmptyPaneCheck(ele.input)).toEqual(ele.output);
        done();
    })
});
