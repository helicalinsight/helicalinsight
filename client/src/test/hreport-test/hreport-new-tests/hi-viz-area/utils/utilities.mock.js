export const mapChartMockdataWhenFormatNotApplied = {
    "data": [
        {
            "longitude": 78.008075,
            "latitude": 27.17667
        },
        {
            "longitude": 72.571362,
            "latitude": 23.022505
        },
    ],
    "labelField": null,
    "report": {
        "id": "562403a3-00ff-4678-a37f-77aaa8694246",
        "mode": "edit",
        "active": true,
        "metadataLoading": false,
        "hreportLoading": false,
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
        "fields": [
            {
                "column": "geo_cordinates.longitude",
                "columnID": "1661",
                "label": "sum_longitude",
                "id": "9148d3f7-1f14-4b85-9c99-7666f227624d",
                "type": {
                    "backendDataType": "java.lang.Double",
                    "dataType": "numeric"
                },
                "autogen_alias": "longitude",
                "isNormalTable": true,
                "tableAlias": "geo_cordinates",
                "aggregate": [],
                "orderByColumn": false,
                "showOrderByColumn": false,
                "addedAs": "column",
                "floatingType": "",
                "functionsDefinition": "",
                "applyBeforeAggregate": false,
                "hiddenIncludeInResultSet": false,
                "metaDataAlias": "longitude",
                "databaseName": "HIUSER",
                "geographicType": "long",
                "isView": false
            },
            {
                "column": "geo_cordinates.latitude",
                "columnID": "1660",
                "label": "sum_latitude",
                "id": "5009917a-386c-44a7-a4a0-2f236d4a646b",
                "type": {
                    "backendDataType": "java.lang.Double",
                    "dataType": "numeric"
                },
                "autogen_alias": "latitude",
                "isNormalTable": true,
                "tableAlias": "geo_cordinates",
                "aggregate": [],
                "orderByColumn": false,
                "showOrderByColumn": false,
                "addedAs": "row",
                "floatingType": "",
                "functionsDefinition": "",
                "applyBeforeAggregate": false,
                "hiddenIncludeInResultSet": false,
                "metaDataAlias": "latitude",
                "databaseName": "HIUSER",
                "geographicType": "lat",
                "isView": false
            }
        ],
        "filters": [],
        "defaultValueDisplayMap": {},
        "editingField": null,
        "activeMark": "b745ddcf-a126-4075-b417-7f14ea17a2e6",
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
        "selectedType": "MapChart",
        "reportData": {
            "metadata": [
                {
                    "1": {
                        "name": "longitude",
                        "type": "numeric"
                    },
                    "2": {
                        "name": "latitude",
                        "type": "numeric"
                    }
                },
                {
                    "rows": 76
                }
            ],
            "metadata_file": {
                "location": "Manish_05",
                "metadataFileName": "Metadata.metadata"
            },
            "database": "HIUSER",
            "fields": [
                {
                    "column": "geo_cordinates.longitude",
                    "columnID": "1661",
                    "label": "sum_longitude",
                    "id": "9148d3f7-1f14-4b85-9c99-7666f227624d",
                    "type": {
                        "backendDataType": "java.lang.Double",
                        "dataType": "numeric"
                    },
                    "autogen_alias": "longitude",
                    "isNormalTable": true,
                    "tableAlias": "geo_cordinates",
                    "aggregate": [],
                    "orderByColumn": false,
                    "showOrderByColumn": false,
                    "addedAs": "column",
                    "floatingType": "",
                    "functionsDefinition": "",
                    "applyBeforeAggregate": false,
                    "hiddenIncludeInResultSet": false,
                    "metaDataAlias": "longitude",
                    "databaseName": "HIUSER",
                    "geographicType": "long",
                    "isView": false
                },
                {
                    "column": "geo_cordinates.latitude",
                    "columnID": "1660",
                    "label": "sum_latitude",
                    "id": "5009917a-386c-44a7-a4a0-2f236d4a646b",
                    "type": {
                        "backendDataType": "java.lang.Double",
                        "dataType": "numeric"
                    },
                    "autogen_alias": "latitude",
                    "isNormalTable": true,
                    "tableAlias": "geo_cordinates",
                    "aggregate": [],
                    "orderByColumn": false,
                    "showOrderByColumn": false,
                    "addedAs": "row",
                    "floatingType": "",
                    "functionsDefinition": "",
                    "applyBeforeAggregate": false,
                    "hiddenIncludeInResultSet": false,
                    "metaDataAlias": "latitude",
                    "databaseName": "HIUSER",
                    "geographicType": "lat",
                    "isView": false
                }
            ],
            "rows": [
                "geo_cordinates.latitude"
            ],
            "columns": [
                "geo_cordinates.longitude"
            ],
            "filters": [],
            "mark_fields": [],
            "visualisation": "MapChart",
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
                    "activeFieldId": "",
                    "showAll": false
                },
                "axisRange": {
                    "fields": [],
                    "activeDatatype": "",
                    "activeId": "",
                    "gridLines": [],
                    "synchronize": false
                },
                "cache": {
                    "isCacheEnabled": false,
                    "interval": "00:00:01"
                },
                "card": {
                    "title": "",
                    "prefixType": "selectIcon",
                    "suffixType": "selectIcon",
                    "prefix": "",
                    "suffix": "",
                    "prefixColor": {
                        "a": 1,
                        "b": 0,
                        "g": 0,
                        "r": 0
                    },
                    "suffixColor": {
                        "a": 1,
                        "b": 0,
                        "g": 0,
                        "r": 0
                    }
                },
                "bar": {
                    "barType": "stacked"
                },
                "radial": {
                    "showRadial": false
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
                    },
                    "backgroundColor": false
                },
                "labels": {
                    "rotateLabels": false
                },
                "crosstab": {
                    "showGrandTotals": false,
                    "showRowGrandTotals": false,
                    "showColumnGrandTotals": false,
                    "showSubTotals": false,
                    "showRowSubTotals": false,
                    "showColumnSubTotals": false,
                    "grandTotalsPosition": "Bottom",
                    "subTotalsPosition": "Auto",
                    "crosstabCollapse": "None"
                },
                "table": {
                    "recordsPerPage": 10
                }
            },
            "settings": {
                "limitBy": 1000,
                "sample": "sample",
                "prependTableNameToAlias": false
            },
            "options": {
                "limitBy": 1000,
                "sample": "sample",
                "prependTableNameToAlias": false
            },
            "user": {
                "name": "hiadmin",
                "email": "admin@helicalinsight.com",
                "actualUserName": "hiadmin",
                "organization": "",
                "roles": [
                    "ROLE_ADMIN",
                    "ROLE_USER",
                    "ROLE_VIEWER"
                ],
                "profile": []
            },
            "selectedType": "MapChart",
            "data": [
                {
                    "longitude": 78.008075,
                    "latitude": 27.17667
                },
                {
                    "longitude": 72.571362,
                    "latitude": 23.022505
                },
                {
                    "longitude": 76.776697,
                    "latitude": 30.378179
                },
                {
                    "longitude": 74.872264,
                    "latitude": 31.633979
                },
                {
                    "longitude": 4.895168,
                    "latitude": 52.370216
                },
                {
                    "longitude": 174.763332,
                    "latitude": -36.84846
                },
                {
                    "longitude": 75.343314,
                    "latitude": 19.876165
                },
                {
                    "longitude": 77.594563,
                    "latitude": 12.971599
                },
                {
                    "longitude": 116.407395,
                    "latitude": 39.904211
                },
                {
                    "longitude": 77.412615,
                    "latitude": 23.259933
                },
                {
                    "longitude": 85.82454,
                    "latitude": 20.296059
                },
                {
                    "longitude": -86.80249,
                    "latitude": 33.520661
                },
                {
                    "longitude": 149.128684,
                    "latitude": -35.282
                },
                {
                    "longitude": 18.424055,
                    "latitude": -33.924869
                },
                {
                    "longitude": 76.779418,
                    "latitude": 30.733315
                },
                {
                    "longitude": 80.270718,
                    "latitude": 13.08268
                },
                {
                    "longitude": -87.629798,
                    "latitude": 41.878114
                },
                {
                    "longitude": 76.267304,
                    "latitude": 9.931233
                },
                {
                    "longitude": 76.955832,
                    "latitude": 11.016844
                },
                {
                    "longitude": 78.032192,
                    "latitude": 30.316495
                },
                {
                    "longitude": 55.270783,
                    "latitude": 25.204849
                },
                {
                    "longitude": 8.682127,
                    "latitude": 50.110922
                },
                {
                    "longitude": -4.251806,
                    "latitude": 55.864237
                },
                {
                    "longitude": 74.123996,
                    "latitude": 15.299326
                },
                {
                    "longitude": 77.026638,
                    "latitude": 28.459497
                },
                {
                    "longitude": 91.736237,
                    "latitude": 26.144517
                },
                {
                    "longitude": 78.182831,
                    "latitude": 26.218287
                },
                {
                    "longitude": 114.109497,
                    "latitude": 22.396428
                },
                {
                    "longitude": 78.486671,
                    "latitude": 17.385044
                },
                {
                    "longitude": 79.986407,
                    "latitude": 23.181467
                },
                {
                    "longitude": 75.787271,
                    "latitude": 26.912434
                },
                {
                    "longitude": 106.845599,
                    "latitude": -6.208763
                },
                {
                    "longitude": 74.857026,
                    "latitude": 32.726602
                },
                {
                    "longitude": 86.202875,
                    "latitude": 22.804566
                },
                {
                    "longitude": 39.237551,
                    "latitude": 21.285407
                },
                {
                    "longitude": 78.568459,
                    "latitude": 25.448426
                },
                {
                    "longitude": 73.024309,
                    "latitude": 26.238947
                },
                {
                    "longitude": 28.047305,
                    "latitude": -26.204103
                },
                {
                    "longitude": 80.331874,
                    "latitude": 26.449923
                },
                {
                    "longitude": 85.32396,
                    "latitude": 27.717245
                },
                {
                    "longitude": 74.243253,
                    "latitude": 16.704987
                },
                {
                    "longitude": 88.363895,
                    "latitude": 22.572646
                },
                {
                    "longitude": 101.686855,
                    "latitude": 3.139003
                },
                {
                    "longitude": 47.481766,
                    "latitude": 29.31166
                },
                {
                    "longitude": -115.13983,
                    "latitude": 36.169941
                },
                {
                    "longitude": -0.127758,
                    "latitude": 51.507351
                },
                {
                    "longitude": 80.946166,
                    "latitude": 26.846694
                },
                {
                    "longitude": 144.96328,
                    "latitude": -37.814107
                },
                {
                    "longitude": -99.133208,
                    "latitude": 19.432608
                },
                {
                    "longitude": -73.567256,
                    "latitude": 45.501689
                },
                {
                    "longitude": 72.877656,
                    "latitude": 19.075984
                },
                {
                    "longitude": 11.581981,
                    "latitude": 48.135125
                },
                {
                    "longitude": 58.405923,
                    "latitude": 23.58589
                },
                {
                    "longitude": 76.639381,
                    "latitude": 12.29581
                },
                {
                    "longitude": 79.088155,
                    "latitude": 21.1458
                },
                {
                    "longitude": 73.789802,
                    "latitude": 19.997453
                },
                {
                    "longitude": 77.209021,
                    "latitude": 28.613939
                },
                {
                    "longitude": -74.005941,
                    "latitude": 40.712784
                },
                {
                    "longitude": 77.391026,
                    "latitude": 28.535516
                },
                {
                    "longitude": 2.352222,
                    "latitude": 48.856614
                },
                {
                    "longitude": 85.137565,
                    "latitude": 25.594095
                },
                {
                    "longitude": -77.194525,
                    "latitude": 41.203322
                },
                {
                    "longitude": 121.774017,
                    "latitude": 12.879721
                },
                {
                    "longitude": 98.39225,
                    "latitude": 7.880448
                },
                {
                    "longitude": 73.856744,
                    "latitude": 18.52043
                },
                {
                    "longitude": 73.712479,
                    "latitude": 24.585445
                },
                {
                    "longitude": 85.309562,
                    "latitude": 23.3441
                },
                {
                    "longitude": -122.419416,
                    "latitude": 37.774929
                },
                {
                    "longitude": -46.633309,
                    "latitude": -23.55052
                },
                {
                    "longitude": 121.473701,
                    "latitude": 31.230416
                },
                {
                    "longitude": 151.20699,
                    "latitude": -33.867487
                },
                {
                    "longitude": 121.565418,
                    "latitude": 25.032969
                },
                {
                    "longitude": 84.572005,
                    "latitude": 23.048268
                },
                {
                    "longitude": 76.936638,
                    "latitude": 8.524139
                },
                {
                    "longitude": 139.691706,
                    "latitude": 35.689487
                },
                {
                    "longitude": -79.383184,
                    "latitude": 43.653226
                }
            ],
            "lastModified": 1706767985877,
            "limitBy": 1000,
            "offset": 0,
            "dataId": "0c8b7e7c-7f5e-4591-acfa-6430030b0629"
        },
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
                "activeFieldId": "",
                "showAll": false
            },
            "axisRange": {
                "fields": [],
                "activeDatatype": "",
                "activeId": "",
                "gridLines": [],
                "synchronize": false
            },
            "cache": {
                "isCacheEnabled": false,
                "interval": "00:00:01"
            },
            "card": {
                "title": "",
                "prefixType": "selectIcon",
                "suffixType": "selectIcon",
                "prefix": "",
                "suffix": "",
                "prefixColor": {
                    "a": 1,
                    "b": 0,
                    "g": 0,
                    "r": 0
                },
                "suffixColor": {
                    "a": 1,
                    "b": 0,
                    "g": 0,
                    "r": 0
                }
            },
            "bar": {
                "barType": "stacked"
            },
            "radial": {
                "showRadial": false
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
                },
                "backgroundColor": false
            },
            "labels": {
                "rotateLabels": false
            },
            "crosstab": {
                "showGrandTotals": false,
                "showRowGrandTotals": false,
                "showColumnGrandTotals": false,
                "showSubTotals": false,
                "showRowSubTotals": false,
                "showColumnSubTotals": false,
                "grandTotalsPosition": "Bottom",
                "subTotalsPosition": "Auto",
                "crosstabCollapse": "None"
            },
            "table": {
                "recordsPerPage": 10
            }
        },
    },
}

export const mapChartMockdataWhenFormatApplied = {
    "data": [
        {
            "longitude": 78.008075,
            "latitude": 27.17667
        },
        {
            "longitude": 72.571362,
            "latitude": 23.022505
        },
    ],
    "labelField": null,
    report: {
        "id": "562403a3-00ff-4678-a37f-77aaa8694246",
        "mode": "edit",
        "active": true,
        "metadata": {
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
            "metadataName": "Metadata",
            "metadataDir": "Manish_05",
            "databaseName": "HIUSER",
            "formData": {
                "location": "Manish_05",
                "metadataFileName": "Metadata.metadata"
            },
            "uid": "bdd9dd36-e81e-4f30-beeb-f7c4a7244050"
        },
        "metadataLoading": false,
        "hreportLoading": false,
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
        "fields": [
            {
                "column": "geo_cordinates.longitude",
                "columnID": "1661",
                "label": "sum_longitude",
                "id": "9148d3f7-1f14-4b85-9c99-7666f227624d",
                "type": {
                    "backendDataType": "java.lang.Double",
                    "dataType": "numeric"
                },
                "autogen_alias": "longitude",
                "isNormalTable": true,
                "tableAlias": "geo_cordinates",
                "aggregate": [],
                "orderByColumn": false,
                "showOrderByColumn": false,
                "addedAs": "column",
                "floatingType": "",
                "functionsDefinition": "",
                "applyBeforeAggregate": false,
                "hiddenIncludeInResultSet": false,
                "metaDataAlias": "longitude",
                "databaseName": "HIUSER",
                "geographicType": "long",
                "isView": false
            },
            {
                "column": "geo_cordinates.latitude",
                "columnID": "1660",
                "label": "sum_latitude",
                "id": "5009917a-386c-44a7-a4a0-2f236d4a646b",
                "type": {
                    "backendDataType": "java.lang.Double",
                    "dataType": "numeric"
                },
                "autogen_alias": "latitude",
                "isNormalTable": true,
                "tableAlias": "geo_cordinates",
                "aggregate": [],
                "orderByColumn": false,
                "showOrderByColumn": false,
                "addedAs": "row",
                "floatingType": "",
                "functionsDefinition": "",
                "applyBeforeAggregate": false,
                "hiddenIncludeInResultSet": false,
                "metaDataAlias": "latitude",
                "databaseName": "HIUSER",
                "geographicType": "lat",
                "isView": false
            }
        ],
        "filters": [],
        "defaultValueDisplayMap": {},
        "editingField": null,
        "marksList": [
            {
                "value": "_all_",
                "id": "b745ddcf-a126-4075-b417-7f14ea17a2e6",
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
                "value": "sum_longitude",
                "id": "9148d3f7-1f14-4b85-9c99-7666f227624d",
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
                "value": "sum_latitude",
                "id": "5009917a-386c-44a7-a4a0-2f236d4a646b",
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
        "activeMark": "b745ddcf-a126-4075-b417-7f14ea17a2e6",
        "activeTool": "7",
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
        "stylesId": "hi-report-4f410be8",
        "savedStyles": "",
        "sqlString": "",
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
        "selectedType": "MapChart",
        "reportData": {
            "metadata": [
                {
                    "1": {
                        "name": "longitude",
                        "type": "numeric"
                    },
                    "2": {
                        "name": "latitude",
                        "type": "numeric"
                    }
                },
                {
                    "rows": 76
                }
            ],
            "metadata_file": {
                "location": "Manish_05",
                "metadataFileName": "Metadata.metadata"
            },
            "database": "HIUSER",
            "fields": [
                {
                    "column": "geo_cordinates.longitude",
                    "columnID": "1661",
                    "label": "sum_longitude",
                    "id": "9148d3f7-1f14-4b85-9c99-7666f227624d",
                    "type": {
                        "backendDataType": "java.lang.Double",
                        "dataType": "numeric"
                    },
                    "autogen_alias": "longitude",
                    "isNormalTable": true,
                    "tableAlias": "geo_cordinates",
                    "aggregate": [],
                    "orderByColumn": false,
                    "showOrderByColumn": false,
                    "addedAs": "column",
                    "floatingType": "",
                    "functionsDefinition": "",
                    "applyBeforeAggregate": false,
                    "hiddenIncludeInResultSet": false,
                    "metaDataAlias": "longitude",
                    "databaseName": "HIUSER",
                    "geographicType": "long",
                    "isView": false
                },
                {
                    "column": "geo_cordinates.latitude",
                    "columnID": "1660",
                    "label": "sum_latitude",
                    "id": "5009917a-386c-44a7-a4a0-2f236d4a646b",
                    "type": {
                        "backendDataType": "java.lang.Double",
                        "dataType": "numeric"
                    },
                    "autogen_alias": "latitude",
                    "isNormalTable": true,
                    "tableAlias": "geo_cordinates",
                    "aggregate": [],
                    "orderByColumn": false,
                    "showOrderByColumn": false,
                    "addedAs": "row",
                    "floatingType": "",
                    "functionsDefinition": "",
                    "applyBeforeAggregate": false,
                    "hiddenIncludeInResultSet": false,
                    "metaDataAlias": "latitude",
                    "databaseName": "HIUSER",
                    "geographicType": "lat",
                    "isView": false
                }
            ],
            "rows": [
                "geo_cordinates.latitude"
            ],
            "columns": [
                "geo_cordinates.longitude"
            ],
            "filters": [],
            "mark_fields": [],
            "visualisation": "MapChart",
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
                    "formatFields": [
                        {
                            "id": "9148d3f7-1f14-4b85-9c99-7666f227624d",
                            "values": {
                                "thousandSperator": true,
                                "decimalPlace": 2,
                                "prefix": "",
                                "suffix": "",
                                "displayUnits": "None",
                                "percentage": "",
                                "numberCustom": "",
                                "apply": [
                                    "tooltip",
                                    "label"
                                ],
                                "isApplyClicked": true,
                                "showAllFormatFields": false,
                                "field": "9148d3f7-1f14-4b85-9c99-7666f227624d",
                                "formatDatatype": "numeric"
                            }
                        }
                    ],
                    "formatDatatype": "numeric",
                    "activeFieldId": "9148d3f7-1f14-4b85-9c99-7666f227624d",
                    "showAll": false
                },
                "axisRange": {
                    "fields": [],
                    "activeDatatype": "",
                    "activeId": "",
                    "gridLines": [],
                    "synchronize": false
                },
                "cache": {
                    "isCacheEnabled": false,
                    "interval": "00:00:01"
                },
                "card": {
                    "title": "",
                    "prefixType": "selectIcon",
                    "suffixType": "selectIcon",
                    "prefix": "",
                    "suffix": "",
                    "prefixColor": {
                        "a": 1,
                        "b": 0,
                        "g": 0,
                        "r": 0
                    },
                    "suffixColor": {
                        "a": 1,
                        "b": 0,
                        "g": 0,
                        "r": 0
                    }
                },
                "bar": {
                    "barType": "stacked"
                },
                "radial": {
                    "showRadial": false
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
                    },
                    "showAll": false,
                    "dataColors": [
                        [
                            "formatColorField",
                            ""
                        ],
                        [
                            "formatColorStyle",
                            ""
                        ]
                    ],
                    "formatColorField": "",
                    "formatColorStyle": "",
                    "backgroundColor": false
                },
                "labels": {
                    "rotateLabels": false
                },
                "crosstab": {
                    "showGrandTotals": false,
                    "showRowGrandTotals": false,
                    "showColumnGrandTotals": false,
                    "showSubTotals": false,
                    "showRowSubTotals": false,
                    "showColumnSubTotals": false,
                    "grandTotalsPosition": "Bottom",
                    "crosstabCollapse": "None"
                },
                "table": {
                    "recordsPerPage": 10
                }
            },
            "settings": {
                "limitBy": 1000,
                "sample": "sample",
                "prependTableNameToAlias": false
            },
            "options": {
                "limitBy": 1000,
                "sample": "sample",
                "prependTableNameToAlias": false
            },
            "defaultValueDisplayMap": {},
            "user": {
                "name": "hiadmin",
                "email": "admin@helicalinsight.com",
                "actualUserName": "hiadmin",
                "organization": "",
                "roles": [
                    "ROLE_ADMIN",
                    "ROLE_USER",
                    "ROLE_VIEWER"
                ],
                "profile": []
            },
            "selectedType": "MapChart",
            "data": [
                {
                    "longitude": 78.008075,
                    "latitude": 27.17667
                },
                {
                    "longitude": 72.571362,
                    "latitude": 23.022505
                },
                {
                    "longitude": 76.776697,
                    "latitude": 30.378179
                },
                {
                    "longitude": 74.872264,
                    "latitude": 31.633979
                },
                {
                    "longitude": 4.895168,
                    "latitude": 52.370216
                },
                {
                    "longitude": 174.763332,
                    "latitude": -36.84846
                },
                {
                    "longitude": 75.343314,
                    "latitude": 19.876165
                },
                {
                    "longitude": 77.594563,
                    "latitude": 12.971599
                },
                {
                    "longitude": 116.407395,
                    "latitude": 39.904211
                },
                {
                    "longitude": 77.412615,
                    "latitude": 23.259933
                },
                {
                    "longitude": 85.82454,
                    "latitude": 20.296059
                },
                {
                    "longitude": -86.80249,
                    "latitude": 33.520661
                },
                {
                    "longitude": 149.128684,
                    "latitude": -35.282
                },
                {
                    "longitude": 18.424055,
                    "latitude": -33.924869
                },
                {
                    "longitude": 76.779418,
                    "latitude": 30.733315
                },
                {
                    "longitude": 80.270718,
                    "latitude": 13.08268
                },
                {
                    "longitude": -87.629798,
                    "latitude": 41.878114
                },
                {
                    "longitude": 76.267304,
                    "latitude": 9.931233
                },
                {
                    "longitude": 76.955832,
                    "latitude": 11.016844
                },
                {
                    "longitude": 78.032192,
                    "latitude": 30.316495
                },
                {
                    "longitude": 55.270783,
                    "latitude": 25.204849
                },
                {
                    "longitude": 8.682127,
                    "latitude": 50.110922
                },
                {
                    "longitude": -4.251806,
                    "latitude": 55.864237
                },
                {
                    "longitude": 74.123996,
                    "latitude": 15.299326
                },
                {
                    "longitude": 77.026638,
                    "latitude": 28.459497
                },
                {
                    "longitude": 91.736237,
                    "latitude": 26.144517
                },
                {
                    "longitude": 78.182831,
                    "latitude": 26.218287
                },
                {
                    "longitude": 114.109497,
                    "latitude": 22.396428
                },
                {
                    "longitude": 78.486671,
                    "latitude": 17.385044
                },
                {
                    "longitude": 79.986407,
                    "latitude": 23.181467
                },
                {
                    "longitude": 75.787271,
                    "latitude": 26.912434
                },
                {
                    "longitude": 106.845599,
                    "latitude": -6.208763
                },
                {
                    "longitude": 74.857026,
                    "latitude": 32.726602
                },
                {
                    "longitude": 86.202875,
                    "latitude": 22.804566
                },
                {
                    "longitude": 39.237551,
                    "latitude": 21.285407
                },
                {
                    "longitude": 78.568459,
                    "latitude": 25.448426
                },
                {
                    "longitude": 73.024309,
                    "latitude": 26.238947
                },
                {
                    "longitude": 28.047305,
                    "latitude": -26.204103
                },
                {
                    "longitude": 80.331874,
                    "latitude": 26.449923
                },
                {
                    "longitude": 85.32396,
                    "latitude": 27.717245
                },
                {
                    "longitude": 74.243253,
                    "latitude": 16.704987
                },
                {
                    "longitude": 88.363895,
                    "latitude": 22.572646
                },
                {
                    "longitude": 101.686855,
                    "latitude": 3.139003
                },
                {
                    "longitude": 47.481766,
                    "latitude": 29.31166
                },
                {
                    "longitude": -115.13983,
                    "latitude": 36.169941
                },
                {
                    "longitude": -0.127758,
                    "latitude": 51.507351
                },
                {
                    "longitude": 80.946166,
                    "latitude": 26.846694
                },
                {
                    "longitude": 144.96328,
                    "latitude": -37.814107
                },
                {
                    "longitude": -99.133208,
                    "latitude": 19.432608
                },
                {
                    "longitude": -73.567256,
                    "latitude": 45.501689
                },
                {
                    "longitude": 72.877656,
                    "latitude": 19.075984
                },
                {
                    "longitude": 11.581981,
                    "latitude": 48.135125
                },
                {
                    "longitude": 58.405923,
                    "latitude": 23.58589
                },
                {
                    "longitude": 76.639381,
                    "latitude": 12.29581
                },
                {
                    "longitude": 79.088155,
                    "latitude": 21.1458
                },
                {
                    "longitude": 73.789802,
                    "latitude": 19.997453
                },
                {
                    "longitude": 77.209021,
                    "latitude": 28.613939
                },
                {
                    "longitude": -74.005941,
                    "latitude": 40.712784
                },
                {
                    "longitude": 77.391026,
                    "latitude": 28.535516
                },
                {
                    "longitude": 2.352222,
                    "latitude": 48.856614
                },
                {
                    "longitude": 85.137565,
                    "latitude": 25.594095
                },
                {
                    "longitude": -77.194525,
                    "latitude": 41.203322
                },
                {
                    "longitude": 121.774017,
                    "latitude": 12.879721
                },
                {
                    "longitude": 98.39225,
                    "latitude": 7.880448
                },
                {
                    "longitude": 73.856744,
                    "latitude": 18.52043
                },
                {
                    "longitude": 73.712479,
                    "latitude": 24.585445
                },
                {
                    "longitude": 85.309562,
                    "latitude": 23.3441
                },
                {
                    "longitude": -122.419416,
                    "latitude": 37.774929
                },
                {
                    "longitude": -46.633309,
                    "latitude": -23.55052
                },
                {
                    "longitude": 121.473701,
                    "latitude": 31.230416
                },
                {
                    "longitude": 151.20699,
                    "latitude": -33.867487
                },
                {
                    "longitude": 121.565418,
                    "latitude": 25.032969
                },
                {
                    "longitude": 84.572005,
                    "latitude": 23.048268
                },
                {
                    "longitude": 76.936638,
                    "latitude": 8.524139
                },
                {
                    "longitude": 139.691706,
                    "latitude": 35.689487
                },
                {
                    "longitude": -79.383184,
                    "latitude": 43.653226
                }
            ],
            "lastModified": 1706767985877,
            "limitBy": 1000,
            "offset": 0,
            "dataId": "a3e0d090-ac65-424c-ae56-bf1d8ad97e43"
        },
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
                "formatFields": [
                    {
                        "id": "9148d3f7-1f14-4b85-9c99-7666f227624d",
                        "values": {
                            "thousandSperator": true,
                            "decimalPlace": 2,
                            "prefix": "",
                            "suffix": "",
                            "displayUnits": "None",
                            "percentage": "",
                            "numberCustom": "",
                            "apply": [
                                "tooltip",
                                "label"
                            ],
                            "isApplyClicked": true,
                            "showAllFormatFields": false,
                            "field": "9148d3f7-1f14-4b85-9c99-7666f227624d",
                            "formatDatatype": "numeric"
                        }
                    }
                ],
                "formatDatatype": "numeric",
                "activeFieldId": "9148d3f7-1f14-4b85-9c99-7666f227624d",
                "showAll": false
            },
            "axisRange": {
                "fields": [],
                "activeDatatype": "",
                "activeId": "",
                "gridLines": [],
                "synchronize": false
            },
            "cache": {
                "isCacheEnabled": false,
                "interval": "00:00:01"
            },
            "card": {
                "title": "",
                "prefixType": "selectIcon",
                "suffixType": "selectIcon",
                "prefix": "",
                "suffix": "",
                "prefixColor": {
                    "a": 1,
                    "b": 0,
                    "g": 0,
                    "r": 0
                },
                "suffixColor": {
                    "a": 1,
                    "b": 0,
                    "g": 0,
                    "r": 0
                }
            },
            "bar": {
                "barType": "stacked"
            },
            "radial": {
                "showRadial": false
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
                },
                "showAll": false,
                "dataColors": [
                    [
                        "formatColorField",
                        ""
                    ],
                    [
                        "formatColorStyle",
                        ""
                    ]
                ],
                "formatColorField": "",
                "formatColorStyle": "",
                "backgroundColor": false
            },
            "labels": {
                "rotateLabels": false
            },
            "crosstab": {
                "showGrandTotals": false,
                "showRowGrandTotals": false,
                "showColumnGrandTotals": false,
                "showSubTotals": false,
                "showRowSubTotals": false,
                "showColumnSubTotals": false,
                "grandTotalsPosition": "Bottom",
                "crosstabCollapse": "None"
            },
            "table": {
                "recordsPerPage": 10
            }
        },
        "reportInfo": {
            "location": "Manish_05",
            "uuid": "map_with_lat_and_long_cords.hr",
            "reportName": "map_with_lat_and_long_cords"
        },
    }
}

export const mapChartExpectedDataWhenFormatNotApplied = [
    {
        "longitude": 78.008075,
        "latitude": 27.17667
    },
    {
        "longitude": 72.571362,
        "latitude": 23.022505
    },
]

export const mapChartExpectedDataWhenFormatApplied = [
    {
        "longitude": 78.01,
        "latitude": 27.17667
    },
    {
        "longitude": 72.57,
        "latitude": 23.022505
    },
]



