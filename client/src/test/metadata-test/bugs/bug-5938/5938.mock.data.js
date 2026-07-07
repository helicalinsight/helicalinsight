export const joinData = {
    returnDataForJest: true,
    "action": "deleteInvalid",
    "record": [],
    "type": "",
    "joins": [
        {
            "type": "inner",
            "operator": "=",
            "left": {
                "table": false,
                "column": false,
                "alias": false
            },
            "right": {
                "table": false,
                "column": false,
                "alias": false
            },
            "key": "5hyh-u8m5-0u5f-avzc-x7",
            "uuid": "5hyh-u8m5-0u5f-avzc-x7",
            "action": "add",
            "index": 1
        },
        {
            "id": "aab02b68e2c7febf125c50c8c5175037",
            "type": "inner",
            "operator": "=",
            "left": {
                "table": "employee_details",
                "column": "employee_id",
                "dataSource": {
                    "sync": false,
                    "id": "1",
                    "connectionDatabaseId": "40b611a3-f82a-4dc3-9781-789bd47b237e",
                    "catSchemaPredicted": false,
                    "catalog": "",
                    "schema": "HIUSER",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc"
                }
            },
            "right": {
                "table": "travel_details",
                "column": "travelled_by",
                "dataSource": {
                    "sync": false,
                    "id": "1",
                    "connectionDatabaseId": "40b611a3-f82a-4dc3-9781-789bd47b237e",
                    "catSchemaPredicted": false,
                    "catalog": "",
                    "schema": "HIUSER",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc"
                }
            },
            "uuid": "0eni-vlok-dgyl-6cvj-8w",
            "index": 2,
            "action": "noChange"
        },
        {
            "id": "daa3221b04c18670d4af25ac99f3ae76",
            "type": "inner",
            "operator": "=",
            "left": {
                "table": "geo_cordinates",
                "column": "location_id",
                "dataSource": {
                    "sync": false,
                    "id": "1",
                    "connectionDatabaseId": "40b611a3-f82a-4dc3-9781-789bd47b237e",
                    "catSchemaPredicted": false,
                    "catalog": "",
                    "schema": "HIUSER",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc"
                }
            },
            "right": {
                "table": "travel_details",
                "column": "destination_id",
                "dataSource": {
                    "sync": false,
                    "id": "1",
                    "connectionDatabaseId": "40b611a3-f82a-4dc3-9781-789bd47b237e",
                    "catSchemaPredicted": false,
                    "catalog": "",
                    "schema": "HIUSER",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc"
                }
            },
            "uuid": "x8nc-co7d-rmg3-pwz1-15",
            "index": 3,
            "action": "noChange"
        },
        {
            "id": "cdeb5b19799c89335f23ed9b50cc5a22",
            "type": "inner",
            "operator": "=",
            "left": {
                "table": "geo_cordinates",
                "column": "location_id",
                "dataSource": {
                    "sync": false,
                    "id": "1",
                    "connectionDatabaseId": "40b611a3-f82a-4dc3-9781-789bd47b237e",
                    "catSchemaPredicted": false,
                    "catalog": "",
                    "schema": "HIUSER",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc"
                }
            },
            "right": {
                "table": "travel_details",
                "column": "source_id",
                "dataSource": {
                    "sync": false,
                    "id": "1",
                    "connectionDatabaseId": "40b611a3-f82a-4dc3-9781-789bd47b237e",
                    "catSchemaPredicted": false,
                    "catalog": "",
                    "schema": "HIUSER",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc"
                }
            },
            "uuid": "3hi1-dbyn-2mw4-mwsr-79",
            "index": 4,
            "action": "noChange"
        },
        {
            "id": "ca21d00c8c87263dedd812f8f74c05b5",
            "type": "inner",
            "operator": "=",
            "left": {
                "table": "geo_cordinates",
                "column": "location_id",
                "dataSource": {
                    "sync": false,
                    "id": "1",
                    "connectionDatabaseId": "40b611a3-f82a-4dc3-9781-789bd47b237e",
                    "catSchemaPredicted": false,
                    "catalog": "",
                    "schema": "HIUSER",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc"
                }
            },
            "right": {
                "table": "dimdate",
                "column": "dim_id",
                "dataSource": {
                    "sync": false,
                    "id": "1",
                    "connectionDatabaseId": "40b611a3-f82a-4dc3-9781-789bd47b237e",
                    "catSchemaPredicted": false,
                    "catalog": "",
                    "schema": "HIUSER",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc"
                }
            },
            "uuid": "3xsj-613i-ii8o-tmo6-of",
            "index": 5,
            "action": "noChange"
        },
        {
            "id": "af8f3186af3703a70a3d6e219faafb4e",
            "type": "inner",
            "operator": "=",
            "left": {
                "table": "employee_details",
                "column": "employee_id",
                "dataSource": {
                    "sync": false,
                    "id": "1",
                    "connectionDatabaseId": "40b611a3-f82a-4dc3-9781-789bd47b237e",
                    "catSchemaPredicted": false,
                    "catalog": "",
                    "schema": "HIUSER",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc"
                }
            },
            "right": {
                "table": "meeting_details",
                "column": "meeting_by",
                "dataSource": {
                    "sync": false,
                    "id": "1",
                    "connectionDatabaseId": "40b611a3-f82a-4dc3-9781-789bd47b237e",
                    "catSchemaPredicted": false,
                    "catalog": "",
                    "schema": "HIUSER",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc"
                }
            },
            "uuid": "xin0-v4lo-rvu7-5z5w-w6",
            "index": 6,
            "action": "noChange"
        }
    ],
    "listOfTableNames": [
        "dimdate",
        "employee_details",
        "geo_cordinates",
        "meeting_details",
        "travel_details"
    ],
    "selectedJoins": [],
    "listOfTableColumnNames": [
        "dimdate",
        "employee_details",
        "geo_cordinates",
        "meeting_details",
        "travel_details"
    ],
    "tables": {
        "dimdate": {
            "id": "4ac5d9f68b58bd7c0d179146e46795be",
            "name": "dimdate",
            "data": {
                "id": "1",
                "type": "dynamicDataSource"
            },
            "keyPath": "t9b1-5o9u-tqwj-nrsk-7h/j8ka-73v2-444h-rv1n-e7/hnrb-blqc-25rc-at4a-d9/m12u-qobo-axez-cex6-pq",
            "key": "m12u-qobo-axez-cex6-pq",
            "alias": "dimdate",
            "uuid": "m12u-qobo-axez-cex6-pq",
            "connId": "w5a1c",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "w5a1c",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "t9b1-5o9u-tqwj-nrsk-7h/j8ka-73v2-444h-rv1n-e7/hnrb-blqc-25rc-at4a-d9",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "dimdate_w5a1c",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "dimdate"
        },
        "employee_details": {
            "id": "4e1fd245f4d13b77be423a43f01d80b2",
            "name": "employee_details",
            "data": {
                "id": "1",
                "type": "dynamicDataSource"
            },
            "keyPath": "t9b1-5o9u-tqwj-nrsk-7h/j8ka-73v2-444h-rv1n-e7/hnrb-blqc-25rc-at4a-d9/xeie-yjo4-c88a-6qzm-mz",
            "key": "xeie-yjo4-c88a-6qzm-mz",
            "alias": "employee_details",
            "uuid": "xeie-yjo4-c88a-6qzm-mz",
            "connId": "w5a1c",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "w5a1c",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "t9b1-5o9u-tqwj-nrsk-7h/j8ka-73v2-444h-rv1n-e7/hnrb-blqc-25rc-at4a-d9",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "employee_details_w5a1c",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "employee_details"
        },
        "geo_cordinates": {
            "id": "be534112989b616b194bc59c2fb25a42",
            "name": "geo_cordinates",
            "data": {
                "id": "1",
                "type": "dynamicDataSource"
            },
            "keyPath": "t9b1-5o9u-tqwj-nrsk-7h/j8ka-73v2-444h-rv1n-e7/hnrb-blqc-25rc-at4a-d9/iuxd-ezww-7hmi-gpxm-kn",
            "key": "iuxd-ezww-7hmi-gpxm-kn",
            "alias": "geo_cordinates",
            "uuid": "iuxd-ezww-7hmi-gpxm-kn",
            "connId": "w5a1c",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "w5a1c",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "t9b1-5o9u-tqwj-nrsk-7h/j8ka-73v2-444h-rv1n-e7/hnrb-blqc-25rc-at4a-d9",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "geo_cordinates_w5a1c",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "geo_cordinates"
        },
        "meeting_details": {
            "id": "9645c648a1c0dbeec1287aaf1e996db3",
            "name": "meeting_details",
            "data": {
                "id": "1",
                "type": "dynamicDataSource"
            },
            "keyPath": "t9b1-5o9u-tqwj-nrsk-7h/j8ka-73v2-444h-rv1n-e7/hnrb-blqc-25rc-at4a-d9/21gj-hohp-yfqi-c5eh-hh",
            "key": "21gj-hohp-yfqi-c5eh-hh",
            "alias": "meeting_details",
            "uuid": "21gj-hohp-yfqi-c5eh-hh",
            "connId": "w5a1c",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "w5a1c",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "t9b1-5o9u-tqwj-nrsk-7h/j8ka-73v2-444h-rv1n-e7/hnrb-blqc-25rc-at4a-d9",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "meeting_details_w5a1c",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "meeting_details"
        },
        "travel_details": {
            "id": "8a28627d07d04ef096d9935f12e0c7e9",
            "name": "travel_details",
            "data": {
                "id": "1",
                "type": "dynamicDataSource"
            },
            "keyPath": "t9b1-5o9u-tqwj-nrsk-7h/j8ka-73v2-444h-rv1n-e7/hnrb-blqc-25rc-at4a-d9/ttqo-qmxu-0zgx-lhrg-jl",
            "key": "ttqo-qmxu-0zgx-lhrg-jl",
            "alias": "travel_details",
            "uuid": "ttqo-qmxu-0zgx-lhrg-jl",
            "connId": "w5a1c",
            "dataSource": {
                "id": "1",
                "type": "dynamicDataSource",
                "baseType": "global.jdbc",
                "catSchemaPredicted": false,
                "sync": false,
                "catalog": "",
                "schema": "HIUSER",
                "connId": "w5a1c",
                "classifier": "db.workflow",
                "datasourceName": "SampleTravelDataDerby",
                "dsKeyPath": "t9b1-5o9u-tqwj-nrsk-7h/j8ka-73v2-444h-rv1n-e7/hnrb-blqc-25rc-at4a-d9",
                "driverType": "Derby",
                "database": "HIUSER"
            },
            "dataSourceName": "SampleTravelDataDerby",
            "category": "table",
            "nameWithConnId": "travel_details_w5a1c",
            "database": "HIUSER",
            "schema": "HIUSER",
            "selected": true,
            "keyName": "travel_details"
        }
    }
}