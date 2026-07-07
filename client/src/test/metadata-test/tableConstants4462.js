export const tableSnapShotFor4462Basic = {
    "storeTables": {
        "geo_cordinates": {
            "id": "be534112989b616b194bc59c2fb25a42",
            "name": "geo_cordinates",
            "data": {
                "id": "1",
                "type": "dynamicDataSource"
            },
            "keyPath": "7e2j-pu9w-bsa6-e1zt-l4/ne7k-t713-wqwj-yjzh-ya/vsdw-moin-ejpf-kh40-om/qphy-u5fp-efh0-0bp3-rv",
            "key": "qphy-u5fp-efh0-0bp3-rv",
            "alias": "geo_cordinates",
            "uuid": "qphy-u5fp-efh0-0bp3-rv",
            "connId": "2yylh",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "2yylh",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "7e2j-pu9w-bsa6-e1zt-l4/ne7k-t713-wqwj-yjzh-ya/vsdw-moin-ejpf-kh40-om",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "geo_cordinates_2yylh",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "geo_cordinates",
            "columns": {
                "location_id": {
                    "alias": "location_id",
                    "fullyQualifiedColumn": "geo_cordinates.location_id",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    },
                    "category": "column",
                    "originalName": "location_id",
                    "originalId": "26bf4351-c0c4-4d12-acba-22218089dc51",
                    "tableKey": "geo_cordinates",
                    "connId": "2yylh",
                    "duplicateIndex": 0,
                    "columnKey": "location_id"
                },
                "location": {
                    "alias": "location",
                    "fullyQualifiedColumn": "geo_cordinates.location",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "originalName": "location",
                    "originalId": "69a7eac0-ccef-462c-906d-c3b8857df786",
                    "tableKey": "geo_cordinates",
                    "connId": "2yylh",
                    "duplicateIndex": 0,
                    "columnKey": "location"
                },
                "latitude": {
                    "alias": "latitude",
                    "fullyQualifiedColumn": "geo_cordinates.latitude",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Double": "numeric"
                    },
                    "category": "column",
                    "originalName": "latitude",
                    "originalId": "08e84c9c-274c-4661-a328-211813ff239a",
                    "tableKey": "geo_cordinates",
                    "connId": "2yylh",
                    "duplicateIndex": 0,
                    "columnKey": "latitude"
                },
                "longitude": {
                    "alias": "longitude",
                    "fullyQualifiedColumn": "geo_cordinates.longitude",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Double": "numeric"
                    },
                    "category": "column",
                    "originalName": "longitude",
                    "originalId": "db3f0fce-9b04-4713-8642-37a49a6e1548",
                    "tableKey": "geo_cordinates",
                    "connId": "2yylh",
                    "duplicateIndex": 0,
                    "columnKey": "longitude"
                }
            },
            "columnsFetched": true
        }
    },
    "fetchedTables": {
        "geo_cordinates": {
            "id": "be534112989b616b194bc59c2fb25a42",
            "alias": "geo_cordinates",
            "columns": {
                "location_id": {
                    "alias": "location_id",
                    "fullyQualifiedColumn": "geo_cordinates.location_id",
                    "columnId": "26bf4351-c0c4-4d12-acba-22218089dc51",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    }
                },
                "location": {
                    "alias": "location",
                    "fullyQualifiedColumn": "geo_cordinates.location",
                    "columnId": "69a7eac0-ccef-462c-906d-c3b8857df786",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "latitude": {
                    "alias": "latitude",
                    "fullyQualifiedColumn": "geo_cordinates.latitude",
                    "columnId": "08e84c9c-274c-4661-a328-211813ff239a",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Double": "numeric"
                    }
                },
                "longitude": {
                    "alias": "longitude",
                    "fullyQualifiedColumn": "geo_cordinates.longitude",
                    "columnId": "db3f0fce-9b04-4713-8642-37a49a6e1548",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Double": "numeric"
                    }
                }
            },
            "name": "geo_cordinates"
        }
    }
}

export const tablesSnapShotFor4462WithAliasedColAndDupTable = {
    "storeTables": {
        "geo_cordinates": {
            "id": "be534112989b616b194bc59c2fb25a42",
            "name": "geo_cordinates",
            "data": {
                "id": "1",
                "type": "dynamicDataSource"
            },
            "keyPath": "6u8m-x2ni-kq4y-vbhe-d4/dd75-j3fm-eq4v-rklq-wf/jomq-tgd1-pbow-k96e-13/6nzn-vwn8-r7zs-83t9-hx",
            "key": "6nzn-vwn8-r7zs-83t9-hx",
            "alias": "geo_cordinates",
            "uuid": "6nzn-vwn8-r7zs-83t9-hx",
            "connId": "pkc2d",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "pkc2d",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "6u8m-x2ni-kq4y-vbhe-d4/dd75-j3fm-eq4v-rklq-wf/jomq-tgd1-pbow-k96e-13",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "geo_cordinates_pkc2d",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "geo_cordinates",
            "columns": {
                "location_id": {
                    "alias": "location_id_alias",
                    "fullyQualifiedColumn": "geo_cordinates.location_id",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    },
                    "category": "column",
                    "originalName": "location_id_alias",
                    "originalId": "26bf4351-c0c4-4d12-acba-22218089dc51",
                    "tableKey": "geo_cordinates",
                    "connId": "pkc2d",
                    "duplicateIndex": 0,
                    "columnKey": "location_id"
                },
                "location": {
                    "alias": "location",
                    "fullyQualifiedColumn": "geo_cordinates.location",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "originalName": "location",
                    "originalId": "69a7eac0-ccef-462c-906d-c3b8857df786",
                    "tableKey": "geo_cordinates",
                    "connId": "pkc2d",
                    "duplicateIndex": 0,
                    "columnKey": "location"
                },
                "latitude": {
                    "alias": "latitude",
                    "fullyQualifiedColumn": "geo_cordinates.latitude",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Double": "numeric"
                    },
                    "category": "column",
                    "originalName": "latitude",
                    "originalId": "08e84c9c-274c-4661-a328-211813ff239a",
                    "tableKey": "geo_cordinates",
                    "connId": "pkc2d",
                    "duplicateIndex": 0,
                    "columnKey": "latitude"
                },
                "longitude": {
                    "alias": "longitude",
                    "fullyQualifiedColumn": "geo_cordinates.longitude",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Double": "numeric"
                    },
                    "category": "column",
                    "originalName": "longitude",
                    "originalId": "db3f0fce-9b04-4713-8642-37a49a6e1548",
                    "tableKey": "geo_cordinates",
                    "connId": "pkc2d",
                    "duplicateIndex": 0,
                    "columnKey": "longitude"
                }
            },
            "columnsFetched": true,
            "duplicateIndex": 1
        },
        "geo_cordinates_1": {
            "name": "geo_cordinates",
            "data": {
                "id": "1",
                "type": "dynamicDataSource"
            },
            "keyPath": "6u8m-x2ni-kq4y-vbhe-d4/dd75-j3fm-eq4v-rklq-wf/jomq-tgd1-pbow-k96e-13/murf-n0qm-domx-xpe2-oi",
            "key": "murf-n0qm-domx-xpe2-oi",
            "alias": "geo_cordinates_1",
            "uuid": "murf-n0qm-domx-xpe2-oi",
            "connId": "pkc2d",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "pkc2d",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "6u8m-x2ni-kq4y-vbhe-d4/dd75-j3fm-eq4v-rklq-wf/jomq-tgd1-pbow-k96e-13",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "geo_cordinates_pkc2d",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "geo_cordinates_1",
            "columns": {
                "location_id": {
                    "alias": "location_id_alias",
                    "fullyQualifiedColumn": "geo_cordinates.location_id",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    },
                    "category": "column",
                    "originalName": "location_id_alias",
                    "originalId": "26bf4351-c0c4-4d12-acba-22218089dc51",
                    "tableKey": "geo_cordinates_1",
                    "connId": "pkc2d",
                    "duplicateIndex": 0,
                    "columnKey": "location_id"
                },
                "location": {
                    "alias": "location",
                    "fullyQualifiedColumn": "geo_cordinates.location",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    },
                    "category": "column",
                    "originalName": "location",
                    "originalId": "69a7eac0-ccef-462c-906d-c3b8857df786",
                    "tableKey": "geo_cordinates_1",
                    "connId": "pkc2d",
                    "duplicateIndex": 0,
                    "columnKey": "location"
                },
                "latitude": {
                    "alias": "latitude",
                    "fullyQualifiedColumn": "geo_cordinates.latitude",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Double": "numeric"
                    },
                    "category": "column",
                    "originalName": "latitude",
                    "originalId": "08e84c9c-274c-4661-a328-211813ff239a",
                    "tableKey": "geo_cordinates_1",
                    "connId": "pkc2d",
                    "duplicateIndex": 0,
                    "columnKey": "latitude"
                },
                "longitude": {
                    "alias": "longitude",
                    "fullyQualifiedColumn": "geo_cordinates.longitude",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Double": "numeric"
                    },
                    "category": "column",
                    "originalName": "longitude",
                    "originalId": "db3f0fce-9b04-4713-8642-37a49a6e1548",
                    "tableKey": "geo_cordinates_1",
                    "connId": "pkc2d",
                    "duplicateIndex": 0,
                    "columnKey": "longitude"
                }
            },
            "columnsFetched": true,
            "duplicateIndex": 0,
            "root": "geo_cordinates",
            "duplicate": true,
            "originalId": "be534112989b616b194bc59c2fb25a42",
            "originalName": "geo_cordinates_1"
        }
    },
    "fetchedTables": {
        "geo_cordinates": {
            "id": "be534112989b616b194bc59c2fb25a42",
            "alias": "geo_cordinates",
            "columns": {
                "location_id": {
                    "alias": "location_id_alias",
                    "fullyQualifiedColumn": "geo_cordinates.location_id",
                    "columnId": "26bf4351-c0c4-4d12-acba-22218089dc51",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    }
                },
                "location": {
                    "alias": "location",
                    "fullyQualifiedColumn": "geo_cordinates.location",
                    "columnId": "69a7eac0-ccef-462c-906d-c3b8857df786",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "latitude": {
                    "alias": "latitude",
                    "fullyQualifiedColumn": "geo_cordinates.latitude",
                    "columnId": "08e84c9c-274c-4661-a328-211813ff239a",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Double": "numeric"
                    }
                },
                "longitude": {
                    "alias": "longitude",
                    "fullyQualifiedColumn": "geo_cordinates.longitude",
                    "columnId": "db3f0fce-9b04-4713-8642-37a49a6e1548",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Double": "numeric"
                    }
                }
            },
            "name": "geo_cordinates"
        },
        "geo_cordinates_1": {
            "id": "07582276-0a1b-4e8f-812b-567824b0159b",
            "alias": "geo_cordinates_1",
            "columns": {
                "location_id_alias": {
                    "alias": "location_id_alias",
                    "duplicate": true,
                    "originalName": "location_id",
                    "fullyQualifiedColumn": "geo_cordinates_1.location_id_alias",
                    "columnId": "b5878c43-1748-4e8b-9648-f2520a643004",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Integer": "numeric"
                    }
                },
                "location": {
                    "alias": "location",
                    "fullyQualifiedColumn": "geo_cordinates_1.location",
                    "columnId": "ada5142f-82f3-4093-96e7-08b2a43f995e",
                    "defaultFunction": "db.generic.groupBy.group",
                    "type": {
                        "java.lang.String": "text"
                    }
                },
                "latitude": {
                    "alias": "latitude",
                    "fullyQualifiedColumn": "geo_cordinates_1.latitude",
                    "columnId": "d83da76b-eec2-45dd-ab2a-faf6009ef71b",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Double": "numeric"
                    }
                },
                "longitude": {
                    "alias": "longitude",
                    "fullyQualifiedColumn": "geo_cordinates_1.longitude",
                    "columnId": "af145700-a8ad-409f-9421-fee47ed1a856",
                    "defaultFunction": "db.generic.aggregate.sum",
                    "type": {
                        "java.lang.Double": "numeric"
                    }
                }
            },
            "duplicate": "true",
            "originalName": "geo_cordinates",
            "name": "geo_cordinates_1"
        }
    }
}