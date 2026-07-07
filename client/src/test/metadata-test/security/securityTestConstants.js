
//   entityNameHandler 
export const entityNameHandlerConstants = {
    table: {
        args: [
            {
                "parentTitle": "travel_details",
                "childTitle": "",
                "key": "8a28627d07d04ef096d9935f12e0c7e9_4aj8m",
                "tooltipInfoObj": {
                    "Name": "travel_details",
                    "Id": "1",
                    "Datasource": "SampleTravelDataDerby",
                    "Alias": "travel_details",
                    "key": "8a28627d07d04ef096d9935f12e0c7e9_4aj8m"

                }
            },
            {
                "parentTitle": "meeting_details",
                "childTitle": "",
                "key": "9645c648a1c0dbeec1287aaf1e996db3_4aj8m",
                "tooltipInfoObj": {
                    "Name": "meeting_details",
                    "Id": "1",
                    "Datasource": "SampleTravelDataDerby",
                    "Alias": "meeting_details",
                    "key": "9645c648a1c0dbeec1287aaf1e996db3_4aj8m"
                }
            }
        ],
        returnVal: [
            {
                "entityName": "travel_details",
                "key": "8a28627d07d04ef096d9935f12e0c7e9_4aj8m",
                "tooltipInfoObj": {
                    "Name": "travel_details",
                    "Id": "1",
                    "Datasource": "SampleTravelDataDerby",
                    "Alias": "travel_details",
                    "key": "8a28627d07d04ef096d9935f12e0c7e9_4aj8m"
                }
            },
            {
                "entityName": "meeting_details",
                "key": "9645c648a1c0dbeec1287aaf1e996db3_4aj8m",
                "tooltipInfoObj": {
                    "Name": "meeting_details",
                    "Id": "1",
                    "Datasource": "SampleTravelDataDerby",
                    "Alias": "meeting_details",
                    "key": "9645c648a1c0dbeec1287aaf1e996db3_4aj8m"
                }
            }
        ]
    },
    column: {
        args: [{
                "parentTitle": "dimdate",
                "childTitle": "created_date",
                "key": "10df7a8f-25f0-44ce-833f-adcaeb1dfb93_ubldr",
                "tooltipInfoObj": {
                    "Table": "dimdate",
                    "Name": "created_date",
                    "Id": "1",
                    "Datasource": "SampleTravelDataDerby",
                    "Alias": "created_date",
                    "key": "10df7a8f-25f0-44ce-833f-adcaeb1dfb93_ubldr"
                }
            },
            {
                "parentTitle": "dimdate",
                "childTitle": "created_time",
                "key": "7b4864f9-f285-4c0c-b77c-4107cba85a6e_ubldr",
                "tooltipInfoObj": {
                    "Table": "dimdate",
                    "Name": "created_time",
                    "Id": "1",
                    "Datasource": "SampleTravelDataDerby",
                    "Alias": "created_time",
                    "key": "7b4864f9-f285-4c0c-b77c-4107cba85a6e_ubldr"
                }
            }],
        returnVal: [
            {
                "entityName": "dimdate.created_date",
                "key": "10df7a8f-25f0-44ce-833f-adcaeb1dfb93_ubldr",
                "tooltipInfoObj": {
                    "Table": "dimdate",
                    "Name": "created_date",
                    "Id": "1",
                    "Datasource": "SampleTravelDataDerby",
                    "Alias": "created_date",
                    "key": "10df7a8f-25f0-44ce-833f-adcaeb1dfb93_ubldr"
                }
            },
            {
                "entityName": "dimdate.created_time",
                "key": "7b4864f9-f285-4c0c-b77c-4107cba85a6e_ubldr",
                "tooltipInfoObj": {
                    "Table": "dimdate",
                    "Name": "created_time",
                    "Id": "1",
                    "Datasource": "SampleTravelDataDerby",
                    "Alias": "created_time",
                    "key": "7b4864f9-f285-4c0c-b77c-4107cba85a6e_ubldr"
                }
            }
        ]
    }
}
// entityNameHandlerConstants.column.args.forEach(ele => {ele.tooltipInfoObj.Table = ele.parentTitle});

const securityTestTable = {
    "dimdate": {
        "id": "4ac5d9f68b58bd7c0d179146e46795be",
        "name": "dimdate",
        "alias": "dimdate",
        "dataSource": {
            "id": "1",
            "type": "dynamicDataSource",
            "baseType": "global.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "",
            "schema": "HIUSER",
            "connId": "aa2ec",
            "dbId": "aa2ec",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelDataDerby",
            "dsKeyPath": "il9a-41qb-ikcc-t63i-wt/zjg4-5ztt-rgrc-hi2n-hn/a6m2-fens-dt75-hm4e-ps",
            "driverType": "Derby",
            "database": "HIUSER"
        },
        "category": "table",
        "connId": "aa2ec",
        "keyPath": "il9a-41qb-ikcc-t63i-wt/zjg4-5ztt-rgrc-hi2n-hn/a6m2-fens-dt75-hm4e-ps/721q-8p1d-d85m-bktb-7v",
        "uniqueKey": "4ac5d9f68b58bd7c0d179146e46795be_aa2ec",
        "schema": "HIUSER",
        "selected": true,
        "keyName": "dimdate"
    },
    "employee_details": {
        "id": "4e1fd245f4d13b77be423a43f01d80b2",
        "name": "employee_details",
        "alias": "employee_details",
        "dataSource": {
            "id": "1",
            "type": "dynamicDataSource",
            "baseType": "global.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "",
            "schema": "HIUSER",
            "connId": "aa2ec",
            "dbId": "aa2ec",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelDataDerby",
            "dsKeyPath": "il9a-41qb-ikcc-t63i-wt/zjg4-5ztt-rgrc-hi2n-hn/a6m2-fens-dt75-hm4e-ps",
            "driverType": "Derby",
            "database": "HIUSER"
        },
        "category": "table",
        "connId": "aa2ec",
        "keyPath": "il9a-41qb-ikcc-t63i-wt/zjg4-5ztt-rgrc-hi2n-hn/a6m2-fens-dt75-hm4e-ps/tq1r-cgbf-qw6q-nz30-zc",
        "uniqueKey": "4e1fd245f4d13b77be423a43f01d80b2_aa2ec",
        "schema": "HIUSER",
        "selected": true,
        "keyName": "employee_details"
    },
    "geo_cordinates": {
        "id": "be534112989b616b194bc59c2fb25a42",
        "name": "geo_cordinates",
        "alias": "geo_cordinates",
        "dataSource": {
            "id": "1",
            "type": "dynamicDataSource",
            "baseType": "global.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "",
            "schema": "HIUSER",
            "connId": "aa2ec",
            "dbId": "aa2ec",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelDataDerby",
            "dsKeyPath": "il9a-41qb-ikcc-t63i-wt/zjg4-5ztt-rgrc-hi2n-hn/a6m2-fens-dt75-hm4e-ps",
            "driverType": "Derby",
            "database": "HIUSER"
        },
        "category": "table",
        "connId": "aa2ec",
        "keyPath": "il9a-41qb-ikcc-t63i-wt/zjg4-5ztt-rgrc-hi2n-hn/a6m2-fens-dt75-hm4e-ps/vu8y-tds7-swm1-86ao-7y",
        "uniqueKey": "be534112989b616b194bc59c2fb25a42_aa2ec",
        "schema": "HIUSER",
        "selected": true,
        "keyName": "geo_cordinates"
    },
    "meeting_details": {
        "id": "9645c648a1c0dbeec1287aaf1e996db3",
        "name": "meeting_details",
        "alias": "meeting_details",
        "dataSource": {
            "id": "1",
            "type": "dynamicDataSource",
            "baseType": "global.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "",
            "schema": "HIUSER",
            "connId": "aa2ec",
            "dbId": "aa2ec",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelDataDerby",
            "dsKeyPath": "il9a-41qb-ikcc-t63i-wt/zjg4-5ztt-rgrc-hi2n-hn/a6m2-fens-dt75-hm4e-ps",
            "driverType": "Derby",
            "database": "HIUSER"
        },
        "category": "table",
        "connId": "aa2ec",
        "keyPath": "il9a-41qb-ikcc-t63i-wt/zjg4-5ztt-rgrc-hi2n-hn/a6m2-fens-dt75-hm4e-ps/l54q-dwkl-nx35-rz99-jg",
        "uniqueKey": "9645c648a1c0dbeec1287aaf1e996db3_aa2ec",
        "schema": "HIUSER",
        "selected": true,
        "keyName": "meeting_details"
    },
    "travel_details": {
        "id": "8a28627d07d04ef096d9935f12e0c7e9",
        "name": "travel_details",
        "alias": "travel_details",
        "dataSource": {
            "id": "1",
            "type": "dynamicDataSource",
            "baseType": "global.jdbc",
            "catSchemaPredicted": false,
            "sync": false,
            "catalog": "",
            "schema": "HIUSER",
            "connId": "aa2ec",
            "dbId": "aa2ec",
            "classifier": "db.workflow",
            "datasourceName": "SampleTravelDataDerby",
            "dsKeyPath": "il9a-41qb-ikcc-t63i-wt/zjg4-5ztt-rgrc-hi2n-hn/a6m2-fens-dt75-hm4e-ps",
            "driverType": "Derby",
            "database": "HIUSER"
        },
        "category": "table",
        "connId": "aa2ec",
        "keyPath": "il9a-41qb-ikcc-t63i-wt/zjg4-5ztt-rgrc-hi2n-hn/a6m2-fens-dt75-hm4e-ps/lel6-klbf-w4nl-544j-oa",
        "uniqueKey": "8a28627d07d04ef096d9935f12e0c7e9_aa2ec",
        "schema": "HIUSER",
        "selected": true,
        "keyName": "travel_details",
        "columns": {
            "travel_id": {
                "alias": "travel_id",
                "fullyQualifiedColumn": "travel_details.travel_id",
                "id": "52f8e8d1-9703-4e40-8bfb-20d44857ed92",
                "defaultFunction": "db.generic.aggregate.sum",
                "type": {
                    "java.lang.Integer": "numeric"
                },
                "category": "column",
                "uniqueKey": "52f8e8d1-9703-4e40-8bfb-20d44857ed92_aa2ec",
                "name": "travel_id",
                "tableKey": "travel_details",
                "connId": "aa2ec",
                "columnKey": "travel_id",
                "tableId": "8a28627d07d04ef096d9935f12e0c7e9"
            },
            "travel_date": {
                "alias": "travel_date",
                "fullyQualifiedColumn": "travel_details.travel_date",
                "id": "ec83d13c-d785-4f46-aa91-7bbfa3fcfc82",
                "defaultFunction": "db.generic.groupBy.group",
                "type": {
                    "java.sql.Timestamp": "dateTime"
                },
                "category": "column",
                "uniqueKey": "ec83d13c-d785-4f46-aa91-7bbfa3fcfc82_aa2ec",
                "name": "travel_date",
                "tableKey": "travel_details",
                "connId": "aa2ec",
                "columnKey": "travel_date",
                "tableId": "8a28627d07d04ef096d9935f12e0c7e9"
            },
            "travel_type": {
                "alias": "travel_type",
                "fullyQualifiedColumn": "travel_details.travel_type",
                "id": "d5a4c446-1c00-44e6-b9e0-ec3112013303",
                "defaultFunction": "db.generic.groupBy.group",
                "type": {
                    "java.lang.String": "text"
                },
                "category": "column",
                "uniqueKey": "d5a4c446-1c00-44e6-b9e0-ec3112013303_aa2ec",
                "name": "travel_type",
                "tableKey": "travel_details",
                "connId": "aa2ec",
                "columnKey": "travel_type",
                "tableId": "8a28627d07d04ef096d9935f12e0c7e9"
            },
            "travel_medium": {
                "alias": "travel_medium",
                "fullyQualifiedColumn": "travel_details.travel_medium",
                "id": "867fe506-0a32-4209-ab7e-6bfc2bc90ab2",
                "defaultFunction": "db.generic.groupBy.group",
                "type": {
                    "java.lang.String": "text"
                },
                "category": "column",
                "uniqueKey": "867fe506-0a32-4209-ab7e-6bfc2bc90ab2_aa2ec",
                "name": "travel_medium",
                "tableKey": "travel_details",
                "connId": "aa2ec",
                "columnKey": "travel_medium",
                "tableId": "8a28627d07d04ef096d9935f12e0c7e9"
            },
            "source_id": {
                "alias": "source_id",
                "fullyQualifiedColumn": "travel_details.source_id",
                "id": "31968a44-dc7d-491b-af6f-a2e65aff2779",
                "defaultFunction": "db.generic.aggregate.sum",
                "type": {
                    "java.lang.Integer": "numeric"
                },
                "category": "column",
                "uniqueKey": "31968a44-dc7d-491b-af6f-a2e65aff2779_aa2ec",
                "name": "source_id",
                "tableKey": "travel_details",
                "connId": "aa2ec",
                "columnKey": "source_id",
                "tableId": "8a28627d07d04ef096d9935f12e0c7e9"
            },
            "source": {
                "alias": "source",
                "fullyQualifiedColumn": "travel_details.source",
                "id": "26bae044-db9d-4039-a48d-d58a2e8e5da6",
                "defaultFunction": "db.generic.groupBy.group",
                "type": {
                    "java.lang.String": "text"
                },
                "category": "column",
                "uniqueKey": "26bae044-db9d-4039-a48d-d58a2e8e5da6_aa2ec",
                "name": "source",
                "tableKey": "travel_details",
                "connId": "aa2ec",
                "columnKey": "source",
                "tableId": "8a28627d07d04ef096d9935f12e0c7e9"
            },
            "destination_id": {
                "alias": "destination_id",
                "fullyQualifiedColumn": "travel_details.destination_id",
                "id": "c94b0fcd-6736-4245-ab98-2f4c55974400",
                "defaultFunction": "db.generic.aggregate.sum",
                "type": {
                    "java.lang.Integer": "numeric"
                },
                "category": "column",
                "uniqueKey": "c94b0fcd-6736-4245-ab98-2f4c55974400_aa2ec",
                "name": "destination_id",
                "tableKey": "travel_details",
                "connId": "aa2ec",
                "columnKey": "destination_id",
                "tableId": "8a28627d07d04ef096d9935f12e0c7e9"
            },
            "destination": {
                "alias": "destination",
                "fullyQualifiedColumn": "travel_details.destination",
                "id": "f7d2ec31-ae39-42ee-9e72-0b0e5c1ca36f",
                "defaultFunction": "db.generic.groupBy.group",
                "type": {
                    "java.lang.String": "text"
                },
                "category": "column",
                "uniqueKey": "f7d2ec31-ae39-42ee-9e72-0b0e5c1ca36f_aa2ec",
                "name": "destination",
                "tableKey": "travel_details",
                "connId": "aa2ec",
                "columnKey": "destination",
                "tableId": "8a28627d07d04ef096d9935f12e0c7e9"
            },
            "travel_cost": {
                "alias": "travel_cost",
                "fullyQualifiedColumn": "travel_details.travel_cost",
                "id": "83d06864-6da8-4e5e-bec2-d9de7d649477",
                "defaultFunction": "db.generic.aggregate.sum",
                "type": {
                    "java.lang.Integer": "numeric"
                },
                "category": "column",
                "uniqueKey": "83d06864-6da8-4e5e-bec2-d9de7d649477_aa2ec",
                "name": "travel_cost",
                "tableKey": "travel_details",
                "connId": "aa2ec",
                "columnKey": "travel_cost",
                "tableId": "8a28627d07d04ef096d9935f12e0c7e9"
            },
            "mode_of_payment": {
                "alias": "mode_of_payment",
                "fullyQualifiedColumn": "travel_details.mode_of_payment",
                "id": "a1af36e5-0812-45f4-8847-fc81b36306a3",
                "defaultFunction": "db.generic.groupBy.group",
                "type": {
                    "java.lang.String": "text"
                },
                "category": "column",
                "uniqueKey": "a1af36e5-0812-45f4-8847-fc81b36306a3_aa2ec",
                "name": "mode_of_payment",
                "tableKey": "travel_details",
                "connId": "aa2ec",
                "columnKey": "mode_of_payment",
                "tableId": "8a28627d07d04ef096d9935f12e0c7e9"
            },
            "booking_platform": {
                "alias": "booking_platform",
                "fullyQualifiedColumn": "travel_details.booking_platform",
                "id": "3548dd28-bce7-420d-b1a9-983857428295",
                "defaultFunction": "db.generic.groupBy.group",
                "type": {
                    "java.lang.String": "text"
                },
                "category": "column",
                "uniqueKey": "3548dd28-bce7-420d-b1a9-983857428295_aa2ec",
                "name": "booking_platform",
                "tableKey": "travel_details",
                "connId": "aa2ec",
                "columnKey": "booking_platform",
                "tableId": "8a28627d07d04ef096d9935f12e0c7e9"
            },
            "travelled_by": {
                "alias": "travelled_by",
                "fullyQualifiedColumn": "travel_details.travelled_by",
                "id": "fea54ba4-87a0-4791-a561-c00222e02741",
                "defaultFunction": "db.generic.aggregate.sum",
                "type": {
                    "java.lang.Integer": "numeric"
                },
                "category": "column",
                "uniqueKey": "fea54ba4-87a0-4791-a561-c00222e02741_aa2ec",
                "name": "travelled_by",
                "tableKey": "travel_details",
                "connId": "aa2ec",
                "columnKey": "travelled_by",
                "tableId": "8a28627d07d04ef096d9935f12e0c7e9"
            }
        },
        "columnsFetched": true
    }
}

let mapTablesColumnsWithUniqueKeyConstantsReturnVal = [
    {
        "key": "4ac5d9f68b58bd7c0d179146e46795be_aa2ec",
        "value": {
            "tableAlias": "dimdate",
            "key": "4ac5d9f68b58bd7c0d179146e46795be_aa2ec",
            "tooltipInfoObj": {
                "Name": "dimdate",
                "Id": "1",
                "Datasource": "SampleTravelDataDerby",
                "Alias": "dimdate",
                "key": "4ac5d9f68b58bd7c0d179146e46795be_aa2ec"
            }
        }
    },
    {
        "key": "4e1fd245f4d13b77be423a43f01d80b2_aa2ec",
        "value": {
            "tableAlias": "employee_details",
            "key": "4e1fd245f4d13b77be423a43f01d80b2_aa2ec",
            "tooltipInfoObj": {
                "Name": "employee_details",
                "Id": "1",
                "Datasource": "SampleTravelDataDerby",
                "Alias": "employee_details",
                "key": "4e1fd245f4d13b77be423a43f01d80b2_aa2ec"
            }
        }
    },
    {
        "key": "be534112989b616b194bc59c2fb25a42_aa2ec",
        "value": {
            "tableAlias": "geo_cordinates",
            "key": "be534112989b616b194bc59c2fb25a42_aa2ec",
            "tooltipInfoObj": {
                "Name": "geo_cordinates",
                "Id": "1",
                "Datasource": "SampleTravelDataDerby",
                "Alias": "geo_cordinates",
                "key": "be534112989b616b194bc59c2fb25a42_aa2ec"
            }
        }
    },
    {
        "key": "9645c648a1c0dbeec1287aaf1e996db3_aa2ec",
        "value": {
            "tableAlias": "meeting_details",
            "key": "9645c648a1c0dbeec1287aaf1e996db3_aa2ec",
            "tooltipInfoObj": {
                "Name": "meeting_details",
                "Id": "1",
                "Datasource": "SampleTravelDataDerby",
                "Alias": "meeting_details",
                "key": "9645c648a1c0dbeec1287aaf1e996db3_aa2ec"
            }
        }
    },
    {
        "key": "8a28627d07d04ef096d9935f12e0c7e9_aa2ec",
        "value": {
            "tableAlias": "travel_details",
            "key": "8a28627d07d04ef096d9935f12e0c7e9_aa2ec",
            "tooltipInfoObj": {
                "Name": "travel_details",
                "Id": "1",
                "Datasource": "SampleTravelDataDerby",
                "Alias": "travel_details",
                "key": "8a28627d07d04ef096d9935f12e0c7e9_aa2ec"
            }
        }
    },
    {
        "key": "52f8e8d1-9703-4e40-8bfb-20d44857ed92_aa2ec",
        "value": {
            "tableAlias": "travel_details",
            "columnAlias": "travel_id",
            "key": "52f8e8d1-9703-4e40-8bfb-20d44857ed92_aa2ec",
            "tooltipInfoObj": {
                "Name": "travel_id",
                "Table": "travel_details",
                "Id": "1",
                "Datasource": "SampleTravelDataDerby",
                "Alias": "travel_id",
                "key": "52f8e8d1-9703-4e40-8bfb-20d44857ed92_aa2ec"
            }
        }
    },
    {
        "key": "ec83d13c-d785-4f46-aa91-7bbfa3fcfc82_aa2ec",
        "value": {
            "tableAlias": "travel_details",
            "columnAlias": "travel_date",
            "key": "ec83d13c-d785-4f46-aa91-7bbfa3fcfc82_aa2ec",
            "tooltipInfoObj": {
                "Name": "travel_date",
                "Table": "travel_details",
                "Id": "1",
                "Datasource": "SampleTravelDataDerby",
                "Alias": "travel_date",
                "key": "ec83d13c-d785-4f46-aa91-7bbfa3fcfc82_aa2ec"
            }
        }
    },
    {
        "key": "d5a4c446-1c00-44e6-b9e0-ec3112013303_aa2ec",
        "value": {
            "tableAlias": "travel_details",
            "columnAlias": "travel_type",
            "key": "d5a4c446-1c00-44e6-b9e0-ec3112013303_aa2ec",
            "tooltipInfoObj": {
                "Name": "travel_type",
                "Table": "travel_details",
                "Id": "1",
                "Datasource": "SampleTravelDataDerby",
                "Alias": "travel_type",
                "key": "d5a4c446-1c00-44e6-b9e0-ec3112013303_aa2ec"
            }
        }
    },
    {
        "key": "867fe506-0a32-4209-ab7e-6bfc2bc90ab2_aa2ec",
        "value": {
            "tableAlias": "travel_details",
            "columnAlias": "travel_medium",
            "key": "867fe506-0a32-4209-ab7e-6bfc2bc90ab2_aa2ec",
            "tooltipInfoObj": {
                "Name": "travel_medium",
                "Table": "travel_details",
                "Id": "1",
                "Datasource": "SampleTravelDataDerby",
                "Alias": "travel_medium",
                "key": "867fe506-0a32-4209-ab7e-6bfc2bc90ab2_aa2ec"
            }
        }
    },
    {
        "key": "31968a44-dc7d-491b-af6f-a2e65aff2779_aa2ec",
        "value": {
            "tableAlias": "travel_details",
            "columnAlias": "source_id",
            "key": "31968a44-dc7d-491b-af6f-a2e65aff2779_aa2ec",
            "tooltipInfoObj": {
                "Name": "source_id",
                "Table": "travel_details",
                "Id": "1",
                "Datasource": "SampleTravelDataDerby",
                "Alias": "source_id",
                "key": "31968a44-dc7d-491b-af6f-a2e65aff2779_aa2ec"
            }
        }
    },
    {
        "key": "26bae044-db9d-4039-a48d-d58a2e8e5da6_aa2ec",
        "value": {
            "tableAlias": "travel_details",
            "columnAlias": "source",
            "key": "26bae044-db9d-4039-a48d-d58a2e8e5da6_aa2ec",
            "tooltipInfoObj": {
                "Name": "source",
                "Table": "travel_details",
                "Id": "1",
                "Datasource": "SampleTravelDataDerby",
                "Alias": "source",
                "key": "26bae044-db9d-4039-a48d-d58a2e8e5da6_aa2ec"
            }
        }
    },
    {
        "key": "c94b0fcd-6736-4245-ab98-2f4c55974400_aa2ec",
        "value": {
            "tableAlias": "travel_details",
            "columnAlias": "destination_id",
            "key": "c94b0fcd-6736-4245-ab98-2f4c55974400_aa2ec",
            "tooltipInfoObj": {
                "Name": "destination_id",
                "Table": "travel_details",
                "Id": "1",
                "Datasource": "SampleTravelDataDerby",
                "Alias": "destination_id",
                "key": "c94b0fcd-6736-4245-ab98-2f4c55974400_aa2ec"
            }
        }
    },
    {
        "key": "f7d2ec31-ae39-42ee-9e72-0b0e5c1ca36f_aa2ec",
        "value": {
            "tableAlias": "travel_details",
            "columnAlias": "destination",
            "key": "f7d2ec31-ae39-42ee-9e72-0b0e5c1ca36f_aa2ec",
            "tooltipInfoObj": {
                "Name": "destination",
                "Table": "travel_details",
                "Id": "1",
                "Datasource": "SampleTravelDataDerby",
                "Alias": "destination",
                "key": "f7d2ec31-ae39-42ee-9e72-0b0e5c1ca36f_aa2ec"
            }
        }
    },
    {
        "key": "83d06864-6da8-4e5e-bec2-d9de7d649477_aa2ec",
        "value": {
            "tableAlias": "travel_details",
            "columnAlias": "travel_cost",
            "key": "83d06864-6da8-4e5e-bec2-d9de7d649477_aa2ec",
            "tooltipInfoObj": {
                "Name": "travel_cost",
                "Table": "travel_details",
                "Id": "1",
                "Datasource": "SampleTravelDataDerby",
                "Alias": "travel_cost",
                "key": "83d06864-6da8-4e5e-bec2-d9de7d649477_aa2ec"
            }
        }
    },
    {
        "key": "a1af36e5-0812-45f4-8847-fc81b36306a3_aa2ec",
        "value": {
            "tableAlias": "travel_details",
            "columnAlias": "mode_of_payment",
            "key": "a1af36e5-0812-45f4-8847-fc81b36306a3_aa2ec",
            "tooltipInfoObj": {
                "Name": "mode_of_payment",
                "Table": "travel_details",
                "Id": "1",
                "Datasource": "SampleTravelDataDerby",
                "Alias": "mode_of_payment",
                "key": "a1af36e5-0812-45f4-8847-fc81b36306a3_aa2ec"
            }
        }
    },
    {
        "key": "3548dd28-bce7-420d-b1a9-983857428295_aa2ec",
        "value": {
            "tableAlias": "travel_details",
            "columnAlias": "booking_platform",
            "key": "3548dd28-bce7-420d-b1a9-983857428295_aa2ec",
            "tooltipInfoObj": {
                "Name": "booking_platform",
                "Table": "travel_details",
                "Id": "1",
                "Datasource": "SampleTravelDataDerby",
                "Alias": "booking_platform",
                "key": "3548dd28-bce7-420d-b1a9-983857428295_aa2ec"
            }
        }
    },
    {
        "key": "fea54ba4-87a0-4791-a561-c00222e02741_aa2ec",
        "value": {
            "tableAlias": "travel_details",
            "columnAlias": "travelled_by",
            "key": "fea54ba4-87a0-4791-a561-c00222e02741_aa2ec",
            "tooltipInfoObj": {
                "Name": "travelled_by",
                "Id": "1",
                "Table": "travel_details",
                "Datasource": "SampleTravelDataDerby",
                "Alias": "travelled_by",
                "key": "fea54ba4-87a0-4791-a561-c00222e02741_aa2ec"
            }
        }
    }
].map(ele => {
    ele.value.tooltipInfoObj.Original = undefined;
    return ele;
})

export const bug_6327 = {
    args: {
        mappedUniqueKeys: [
            {
                "key": "123456",
                "value": {
                    "tableAlias": "dimdate",
                    "key": "123456",
                    "tooltipInfoObj": {
                        "Name": "dimdate",
                        "Id": "1",
                        "Datasource": "SampleTravelDataDerby",
                        "Alias": "dimdate",
                        "key": "123456"
                    }
                }
        }],
        securityTableData: [{securityKeysToBeCheck: ['0000000', '123456']}]
    },
    result: [{securityKeysToBeCheck: ['0000000',  { "tableAlias": "dimdate",
    "key": "123456",
    "tooltipInfoObj": {
        "Name": "dimdate",
        "Id": "1",
        "Datasource": "SampleTravelDataDerby",
        "Alias": "dimdate",
        "key": "123456"
    }}]},]
    }


export const mapTablesColumnsWithUniqueKeyConstants = {
    args: securityTestTable,
    returnVal: mapTablesColumnsWithUniqueKeyConstantsReturnVal
}

export const tablesToTableDataConverterConstants = {
    createExpWithTable : {
        args: {
            tables: {
                "dimdate": {
                    "id": "4ac5d9f68b58bd7c0d179146e46795be",
                    "name": "dimdate",
                    "alias": "dimdate",
                    "dataSource": {
                        "id": "1",
                        "type": "dynamicDataSource",
                        "baseType": "global.jdbc",
                        "catSchemaPredicted": false,
                        "sync": false,
                        "catalog": "",
                        "schema": "HIUSER",
                        "connId": "7gjap",
                        "dbId": "7gjap",
                        "classifier": "db.workflow",
                        "datasourceName": "SampleTravelDataDerby",
                        "dsKeyPath": "wsko-5iky-qcsl-cvyk-af/3kum-gql7-gov3-67nm-51/ex73-sz7p-dnvu-fyru-9x",
                        "driverType": "Derby",
                        "database": "HIUSER"
                    },
                    "category": "table",
                    "connId": "7gjap",
                    "keyPath": "wsko-5iky-qcsl-cvyk-af/3kum-gql7-gov3-67nm-51/ex73-sz7p-dnvu-fyru-9x/zh9r-mjeb-yzp6-gbe1-yv",
                    "uniqueKey": "4ac5d9f68b58bd7c0d179146e46795be_7gjap",
                    "schema": "HIUSER",
                    "selected": true,
                    "keyName": "dimdate",
                },
                "employee_details": {
                    "id": "4e1fd245f4d13b77be423a43f01d80b2",
                    "name": "employee_details",
                    "alias": "employee_details",
                    "dataSource": {
                        "id": "1",
                        "type": "dynamicDataSource",
                        "baseType": "global.jdbc",
                        "catSchemaPredicted": false,
                        "sync": false,
                        "catalog": "",
                        "schema": "HIUSER",
                        "connId": "7gjap",
                        "dbId": "7gjap",
                        "classifier": "db.workflow",
                        "datasourceName": "SampleTravelDataDerby",
                        "dsKeyPath": "wsko-5iky-qcsl-cvyk-af/3kum-gql7-gov3-67nm-51/ex73-sz7p-dnvu-fyru-9x",
                        "driverType": "Derby",
                        "database": "HIUSER"
                    },
                    "category": "table",
                    "connId": "7gjap",
                    "keyPath": "wsko-5iky-qcsl-cvyk-af/3kum-gql7-gov3-67nm-51/ex73-sz7p-dnvu-fyru-9x/ozgr-8dcn-mdim-rn0y-yz",
                    "uniqueKey": "4e1fd245f4d13b77be423a43f01d80b2_7gjap",
                    "schema": "HIUSER",
                    "selected": true,
                    "keyName": "employee_details"
                }
            },
            category: 'table',
            edit: false,
        },
        result: [
            {
                "icon": "",
                "title": "dimdate",
                "Name": "dimdate",
                "checkable": true,
                "key": "4ac5d9f68b58bd7c0d179146e46795be_7gjap",
                "dbId": "7gjap",
                "tableId": "4ac5d9f68b58bd7c0d179146e46795be",
                "Id": "1",
                "Datasource": "SampleTravelDataDerby",
                "category": "table",
                "children": [],
                "Original": undefined
            },
            {
                "icon": "",
                "title": "employee_details",
                "Name": "employee_details",
                "checkable": true,
                "key": "4e1fd245f4d13b77be423a43f01d80b2_7gjap",
                "dbId": "7gjap",
                "tableId": "4e1fd245f4d13b77be423a43f01d80b2",
                "Id": "1",
                "Datasource": "SampleTravelDataDerby",
                "category": "table",
                "children": [],
                "Original": undefined
            }
        ]
    },
    createExpWithColumn : {
        args: {
            tables: {
                "dimdate": {
                    "id": "4ac5d9f68b58bd7c0d179146e46795be",
                    "name": "dimdate",
                    "alias": "dimdate",
                    "dataSource": {
                        "id": "1",
                        "type": "dynamicDataSource",
                        "baseType": "global.jdbc",
                        "catSchemaPredicted": false,
                        "sync": false,
                        "catalog": "",
                        "schema": "HIUSER",
                        "connId": "1ws1i",
                        "dbId": "1ws1i",
                        "classifier": "db.workflow",
                        "datasourceName": "SampleTravelDataDerby",
                        "dsKeyPath": "3h64-u76p-1e0n-xtgg-j4/40cs-wm1e-fc0t-glvy-a9/cqrh-v7u3-xwl4-rn6v-i3",
                        "driverType": "Derby",
                        "database": "HIUSER"
                    },
                    "category": "table",
                    "connId": "1ws1i",
                    "keyPath": "3h64-u76p-1e0n-xtgg-j4/40cs-wm1e-fc0t-glvy-a9/cqrh-v7u3-xwl4-rn6v-i3/2h68-0wms-6xh7-a175-uj",
                    "uniqueKey": "4ac5d9f68b58bd7c0d179146e46795be_1ws1i",
                    "schema": "HIUSER",
                    "selected": true,
                    "keyName": "dimdate",
                    "columns": {
                        "dim_id": {
                            "alias": "dim_id",
                            "fullyQualifiedColumn": "dimdate.dim_id",
                            "id": "daac9be5-db88-4dd4-9ffb-31fadb1a7579",
                            "defaultFunction": "db.generic.aggregate.sum",
                            "type": {
                                "java.lang.Integer": "numeric"
                            },
                            "category": "column",
                            "uniqueKey": "daac9be5-db88-4dd4-9ffb-31fadb1a7579_1ws1i",
                            "name": "dim_id",
                            "tableKey": "dimdate",
                            "connId": "1ws1i",
                            "columnKey": "dim_id",
                            "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
                        },
                        "fiscal_year": {
                            "alias": "fiscal_year",
                            "fullyQualifiedColumn": "dimdate.fiscal_year",
                            "id": "390cd762-17d9-4648-a977-77ad87cd08fb",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.sql.Date": "date"
                            },
                            "category": "column",
                            "uniqueKey": "390cd762-17d9-4648-a977-77ad87cd08fb_1ws1i",
                            "name": "fiscal_year",
                            "tableKey": "dimdate",
                            "connId": "1ws1i",
                            "columnKey": "fiscal_year",
                            "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
                        },
                        "modified_date": {
                            "alias": "modified_date",
                            "fullyQualifiedColumn": "dimdate.modified_date",
                            "id": "d005b4e4-47f6-424c-8cce-edd7e0fa4909",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.sql.Timestamp": "dateTime"
                            },
                            "category": "column",
                            "uniqueKey": "d005b4e4-47f6-424c-8cce-edd7e0fa4909_1ws1i",
                            "name": "modified_date",
                            "tableKey": "dimdate",
                            "connId": "1ws1i",
                            "columnKey": "modified_date",
                            "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
                        },
                        "date_key": {
                            "alias": "date_key",
                            "fullyQualifiedColumn": "dimdate.date_key",
                            "id": "8ed84a3e-0c3d-49cc-b16a-4f5543d5e3a9",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            },
                            "category": "column",
                            "uniqueKey": "8ed84a3e-0c3d-49cc-b16a-4f5543d5e3a9_1ws1i",
                            "name": "date_key",
                            "tableKey": "dimdate",
                            "connId": "1ws1i",
                            "columnKey": "date_key",
                            "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
                        },
                        "day_number": {
                            "alias": "day_number",
                            "fullyQualifiedColumn": "dimdate.day_number",
                            "id": "06f934c8-c325-48f9-b187-f25c1b4d2360",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            },
                            "category": "column",
                            "uniqueKey": "06f934c8-c325-48f9-b187-f25c1b4d2360_1ws1i",
                            "name": "day_number",
                            "tableKey": "dimdate",
                            "connId": "1ws1i",
                            "columnKey": "day_number",
                            "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
                        },
                        "fiscal_month_name": {
                            "alias": "fiscal_month_name",
                            "fullyQualifiedColumn": "dimdate.fiscal_month_name",
                            "id": "cdf780f0-842a-4a9a-8476-7d492b73047e",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            },
                            "category": "column",
                            "uniqueKey": "cdf780f0-842a-4a9a-8476-7d492b73047e_1ws1i",
                            "name": "fiscal_month_name",
                            "tableKey": "dimdate",
                            "connId": "1ws1i",
                            "columnKey": "fiscal_month_name",
                            "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
                        },
                        "fiscal_month_label": {
                            "alias": "fiscal_month_label",
                            "fullyQualifiedColumn": "dimdate.fiscal_month_label",
                            "id": "e632a468-d5b7-441b-9674-67779af272bd",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            },
                            "category": "column",
                            "uniqueKey": "e632a468-d5b7-441b-9674-67779af272bd_1ws1i",
                            "name": "fiscal_month_label",
                            "tableKey": "dimdate",
                            "connId": "1ws1i",
                            "columnKey": "fiscal_month_label",
                            "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
                        },
                        "created_date": {
                            "alias": "created_date",
                            "fullyQualifiedColumn": "dimdate.created_date",
                            "id": "10df7a8f-25f0-44ce-833f-adcaeb1dfb93",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            },
                            "category": "column",
                            "uniqueKey": "10df7a8f-25f0-44ce-833f-adcaeb1dfb93_1ws1i",
                            "name": "created_date",
                            "tableKey": "dimdate",
                            "connId": "1ws1i",
                            "columnKey": "created_date",
                            "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
                        },
                        "created_time": {
                            "alias": "created_time",
                            "fullyQualifiedColumn": "dimdate.created_time",
                            "id": "7b4864f9-f285-4c0c-b77c-4107cba85a6e",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            },
                            "category": "column",
                            "uniqueKey": "7b4864f9-f285-4c0c-b77c-4107cba85a6e_1ws1i",
                            "name": "created_time",
                            "tableKey": "dimdate",
                            "connId": "1ws1i",
                            "columnKey": "created_time",
                            "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
                        },
                        "rating": {
                            "alias": "rating",
                            "fullyQualifiedColumn": "dimdate.rating",
                            "id": "0a56d7f2-69d4-4175-afe4-dd3d797d2988",
                            "defaultFunction": "db.generic.groupBy.group",
                            "type": {
                                "java.lang.String": "text"
                            },
                            "category": "column",
                            "uniqueKey": "0a56d7f2-69d4-4175-afe4-dd3d797d2988_1ws1i",
                            "name": "rating",
                            "tableKey": "dimdate",
                            "connId": "1ws1i",
                            "columnKey": "rating",
                            "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
                        }
                    },
                    "columnsFetched": true
                }
            },
            category: 'column',
            edit: false,
        },
        result: [
            {
                "icon": '',
                "title": "dimdate",
                "Name": "dimdate",
                "checkable": false,
                "key": "4ac5d9f68b58bd7c0d179146e46795be_1ws1i",
                "dbId": "1ws1i",
                "tableId": "4ac5d9f68b58bd7c0d179146e46795be",
                "Id": "1",
                "Datasource": "SampleTravelDataDerby",
                "category": "table",
                "original": undefined,
                "children": [
                    {
                        "title": "created_date",
                        "key": "10df7a8f-25f0-44ce-833f-adcaeb1dfb93_1ws1i",
                        "Name": "created_date",
                        "dbId": "1ws1i",
                        "category": "column",
                        "Id": "1",
                        "Datasource": "SampleTravelDataDerby",
                        "columnId": "10df7a8f-25f0-44ce-833f-adcaeb1dfb93",
                        "icon": '',
                        "checkable": true,
                        "original": undefined
                    },
                    {
                        "title": "created_time",
                        "key": "7b4864f9-f285-4c0c-b77c-4107cba85a6e_1ws1i",
                        "Name": "created_time",
                        "dbId": "1ws1i",
                        "category": "column",
                        "Id": "1",
                        "Datasource": "SampleTravelDataDerby",
                        "columnId": "7b4864f9-f285-4c0c-b77c-4107cba85a6e",
                        "icon": '',
                        "checkable": true,
                        "original": undefined
                    },
                    {
                        "title": "date_key",
                        "key": "8ed84a3e-0c3d-49cc-b16a-4f5543d5e3a9_1ws1i",
                        "Name": "date_key",
                        "dbId": "1ws1i",
                        "category": "column",
                        "Id": "1",
                        "Datasource": "SampleTravelDataDerby",
                        "columnId": "8ed84a3e-0c3d-49cc-b16a-4f5543d5e3a9",
                        "icon": '',
                        "checkable": true,
                        "original": undefined
                    },
                    {
                        "title": "day_number",
                        "key": "06f934c8-c325-48f9-b187-f25c1b4d2360_1ws1i",
                        "Name": "day_number",
                        "dbId": "1ws1i",
                        "category": "column",
                        "Id": "1",
                        "Datasource": "SampleTravelDataDerby",
                        "columnId": "06f934c8-c325-48f9-b187-f25c1b4d2360",
                        "icon": '',
                        "checkable": true,
                        "original": undefined
                    },
                    {
                        "title": "dim_id",
                        "key": "daac9be5-db88-4dd4-9ffb-31fadb1a7579_1ws1i",
                        "Name": "dim_id",
                        "dbId": "1ws1i",
                        "category": "column",
                        "Id": "1",
                        "Datasource": "SampleTravelDataDerby",
                        "columnId": "daac9be5-db88-4dd4-9ffb-31fadb1a7579",
                        "icon": '',
                        "checkable": true,
                        "original": undefined
                    },
                    {
                        "title": "fiscal_month_label",
                        "key": "e632a468-d5b7-441b-9674-67779af272bd_1ws1i",
                        "Name": "fiscal_month_label",
                        "dbId": "1ws1i",
                        "category": "column",
                        "Id": "1",
                        "Datasource": "SampleTravelDataDerby",
                        "columnId": "e632a468-d5b7-441b-9674-67779af272bd",
                        "icon": '',
                        "checkable": true,
                        "original": undefined
                    },
                    {
                        "title": "fiscal_month_name",
                        "key": "cdf780f0-842a-4a9a-8476-7d492b73047e_1ws1i",
                        "Name": "fiscal_month_name",
                        "dbId": "1ws1i",
                        "category": "column",
                        "Id": "1",
                        "Datasource": "SampleTravelDataDerby",
                        "columnId": "cdf780f0-842a-4a9a-8476-7d492b73047e",
                        "icon": '',
                        "checkable": true,
                        "original": undefined
                    },
                    {
                        "title": "fiscal_year",
                        "key": "390cd762-17d9-4648-a977-77ad87cd08fb_1ws1i",
                        "Name": "fiscal_year",
                        "dbId": "1ws1i",
                        "category": "column",
                        "Id": "1",
                        "Datasource": "SampleTravelDataDerby",
                        "columnId": "390cd762-17d9-4648-a977-77ad87cd08fb",
                        "icon": '',
                        "checkable": true,
                        "original": undefined
                    },
                    {
                        "title": "modified_date",
                        "key": "d005b4e4-47f6-424c-8cce-edd7e0fa4909_1ws1i",
                        "Name": "modified_date",
                        "dbId": "1ws1i",
                        "category": "column",
                        "Id": "1",
                        "Datasource": "SampleTravelDataDerby",
                        "columnId": "d005b4e4-47f6-424c-8cce-edd7e0fa4909",
                        "icon": '',
                        "checkable": true,
                        "original": undefined
                    },
                    {
                        "title": "rating",
                        "key": "0a56d7f2-69d4-4175-afe4-dd3d797d2988_1ws1i",
                        "Name": "rating",
                        "dbId": "1ws1i",
                        "category": "column",
                        "Id": "1",
                        "Datasource": "SampleTravelDataDerby",
                        "columnId": "0a56d7f2-69d4-4175-afe4-dd3d797d2988",
                        "icon": '',
                        "checkable": true,
                        "original": undefined
                    }
                ]
            }
        ]
    },
    editExpWithTableType : {
        args: {
            tables: {"dimdate": {
                        "id": "4ac5d9f68b58bd7c0d179146e46795be",
                        "name": "dimdate",
                        "alias": "dimdate",
                        "dataSource": {
                            "id": "1",
                            "type": "dynamicDataSource",
                            "baseType": "global.jdbc",
                            "catSchemaPredicted": false,
                            "sync": false,
                            "catalog": "",
                            "schema": "HIUSER",
                            "connId": "4sci1",
                            "dbId": "4sci1",
                            "classifier": "db.workflow",
                            "datasourceName": "SampleTravelDataDerby",
                            "dsKeyPath": "55uj-leli-iwqy-5107-6w/j3fd-o1lu-vix2-sdbc-ii/12uq-9wo8-su22-hkmg-nl",
                            "driverType": "Derby",
                            "database": "HIUSER"
                        },
                        "category": "table",
                        "connId": "4sci1",
                        "keyPath": "55uj-leli-iwqy-5107-6w/j3fd-o1lu-vix2-sdbc-ii/12uq-9wo8-su22-hkmg-nl/zaoa-64ej-v6kg-5ehb-cc",
                        "uniqueKey": "4ac5d9f68b58bd7c0d179146e46795be_4sci1",
                        "schema": "HIUSER",
                        "selected": true,
                        "keyName": "dimdate",
                        "columns": {
                            "dim_id": {
                                "alias": "dim_id",
                                "fullyQualifiedColumn": "dimdate.dim_id",
                                "id": "daac9be5-db88-4dd4-9ffb-31fadb1a7579",
                                "defaultFunction": "db.generic.aggregate.sum",
                                "type": {
                                    "java.lang.Integer": "numeric"
                                },
                                "category": "column",
                                "uniqueKey": "daac9be5-db88-4dd4-9ffb-31fadb1a7579_4sci1",
                                "name": "dim_id",
                                "tableKey": "dimdate",
                                "connId": "4sci1",
                                "columnKey": "dim_id",
                                "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
                            },
                            "fiscal_year": {
                                "alias": "fiscal_year",
                                "fullyQualifiedColumn": "dimdate.fiscal_year",
                                "id": "390cd762-17d9-4648-a977-77ad87cd08fb",
                                "defaultFunction": "db.generic.groupBy.group",
                                "type": {
                                    "java.sql.Date": "date"
                                },
                                "category": "column",
                                "uniqueKey": "390cd762-17d9-4648-a977-77ad87cd08fb_4sci1",
                                "name": "fiscal_year",
                                "tableKey": "dimdate",
                                "connId": "4sci1",
                                "columnKey": "fiscal_year",
                                "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
                            },
                            "modified_date": {
                                "alias": "modified_date",
                                "fullyQualifiedColumn": "dimdate.modified_date",
                                "id": "d005b4e4-47f6-424c-8cce-edd7e0fa4909",
                                "defaultFunction": "db.generic.groupBy.group",
                                "type": {
                                    "java.sql.Timestamp": "dateTime"
                                },
                                "category": "column",
                                "uniqueKey": "d005b4e4-47f6-424c-8cce-edd7e0fa4909_4sci1",
                                "name": "modified_date",
                                "tableKey": "dimdate",
                                "connId": "4sci1",
                                "columnKey": "modified_date",
                                "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
                            },
                            "date_key": {
                                "alias": "date_key",
                                "fullyQualifiedColumn": "dimdate.date_key",
                                "id": "8ed84a3e-0c3d-49cc-b16a-4f5543d5e3a9",
                                "defaultFunction": "db.generic.groupBy.group",
                                "type": {
                                    "java.lang.String": "text"
                                },
                                "category": "column",
                                "uniqueKey": "8ed84a3e-0c3d-49cc-b16a-4f5543d5e3a9_4sci1",
                                "name": "date_key",
                                "tableKey": "dimdate",
                                "connId": "4sci1",
                                "columnKey": "date_key",
                                "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
                            },
                            "day_number": {
                                "alias": "day_number",
                                "fullyQualifiedColumn": "dimdate.day_number",
                                "id": "06f934c8-c325-48f9-b187-f25c1b4d2360",
                                "defaultFunction": "db.generic.groupBy.group",
                                "type": {
                                    "java.lang.String": "text"
                                },
                                "category": "column",
                                "uniqueKey": "06f934c8-c325-48f9-b187-f25c1b4d2360_4sci1",
                                "name": "day_number",
                                "tableKey": "dimdate",
                                "connId": "4sci1",
                                "columnKey": "day_number",
                                "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
                            },
                            "fiscal_month_name": {
                                "alias": "fiscal_month_name",
                                "fullyQualifiedColumn": "dimdate.fiscal_month_name",
                                "id": "cdf780f0-842a-4a9a-8476-7d492b73047e",
                                "defaultFunction": "db.generic.groupBy.group",
                                "type": {
                                    "java.lang.String": "text"
                                },
                                "category": "column",
                                "uniqueKey": "cdf780f0-842a-4a9a-8476-7d492b73047e_4sci1",
                                "name": "fiscal_month_name",
                                "tableKey": "dimdate",
                                "connId": "4sci1",
                                "columnKey": "fiscal_month_name",
                                "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
                            },
                            "fiscal_month_label": {
                                "alias": "fiscal_month_label",
                                "fullyQualifiedColumn": "dimdate.fiscal_month_label",
                                "id": "e632a468-d5b7-441b-9674-67779af272bd",
                                "defaultFunction": "db.generic.groupBy.group",
                                "type": {
                                    "java.lang.String": "text"
                                },
                                "category": "column",
                                "uniqueKey": "e632a468-d5b7-441b-9674-67779af272bd_4sci1",
                                "name": "fiscal_month_label",
                                "tableKey": "dimdate",
                                "connId": "4sci1",
                                "columnKey": "fiscal_month_label",
                                "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
                            },
                            "created_date": {
                                "alias": "created_date",
                                "fullyQualifiedColumn": "dimdate.created_date",
                                "id": "10df7a8f-25f0-44ce-833f-adcaeb1dfb93",
                                "defaultFunction": "db.generic.groupBy.group",
                                "type": {
                                    "java.lang.String": "text"
                                },
                                "category": "column",
                                "uniqueKey": "10df7a8f-25f0-44ce-833f-adcaeb1dfb93_4sci1",
                                "name": "created_date",
                                "tableKey": "dimdate",
                                "connId": "4sci1",
                                "columnKey": "created_date",
                                "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
                            },
                            "created_time": {
                                "alias": "created_time",
                                "fullyQualifiedColumn": "dimdate.created_time",
                                "id": "7b4864f9-f285-4c0c-b77c-4107cba85a6e",
                                "defaultFunction": "db.generic.groupBy.group",
                                "type": {
                                    "java.lang.String": "text"
                                },
                                "category": "column",
                                "uniqueKey": "7b4864f9-f285-4c0c-b77c-4107cba85a6e_4sci1",
                                "name": "created_time",
                                "tableKey": "dimdate",
                                "connId": "4sci1",
                                "columnKey": "created_time",
                                "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
                            },
                            "rating": {
                                "alias": "rating",
                                "fullyQualifiedColumn": "dimdate.rating",
                                "id": "0a56d7f2-69d4-4175-afe4-dd3d797d2988",
                                "defaultFunction": "db.generic.groupBy.group",
                                "type": {
                                    "java.lang.String": "text"
                                },
                                "category": "column",
                                "uniqueKey": "0a56d7f2-69d4-4175-afe4-dd3d797d2988_4sci1",
                                "name": "rating",
                                "tableKey": "dimdate",
                                "connId": "4sci1",
                                "columnKey": "rating",
                                "tableId": "4ac5d9f68b58bd7c0d179146e46795be"
                            }
                        },
                        "columnsFetched": true
                }},
            formData: {
                "Expression Name": "edit",
                "Entity Names": "dimdate",
                "Expression Type": "global",
                "Access Type": "grant",
                "Execution Type": "conditionIf",
                "Condition": "${user}.name eq 'hiadmin'",
                "securityKeysToBeCheck": [
                    "4ac5d9f68b58bd7c0d179146e46795be_4sci1"
                ],
                "Filter": "TableName.ColumnName = Filter Condition",
                "key": 0,
                "expressionId": "f847ea67-4c73-4804-9271-0484baa7d1e6"
            },
            edit: true
        },
        result: [
            {
                "icon": "",
                "title": "dimdate",
                "Name": "dimdate",
                "checkable": true,
                "key": "4ac5d9f68b58bd7c0d179146e46795be_4sci1",
                "dbId": "4sci1",
                "tableId": "4ac5d9f68b58bd7c0d179146e46795be",
                "Id": "1",
                "Datasource": "SampleTravelDataDerby",
                "category": "table",
                "original": undefined,
                "children": [
                    {
                        "title": "created_date",
                        "key": "10df7a8f-25f0-44ce-833f-adcaeb1dfb93_4sci1",
                        "Name": "created_date",
                        "dbId": "4sci1",
                        "category": "column",
                        "Id": "1",
                        "Datasource": "SampleTravelDataDerby",
                        "columnId": "10df7a8f-25f0-44ce-833f-adcaeb1dfb93",
                        "icon": "",
                        "checkable": false,
                        "original": undefined
                    },
                    {
                        "title": "created_time",
                        "key": "7b4864f9-f285-4c0c-b77c-4107cba85a6e_4sci1",
                        "Name": "created_time",
                        "dbId": "4sci1",
                        "category": "column",
                        "Id": "1",
                        "Datasource": "SampleTravelDataDerby",
                        "columnId": "7b4864f9-f285-4c0c-b77c-4107cba85a6e",
                        "icon": "",
                        "checkable": false,
                        "original": undefined
                    },
                    {
                        "title": "date_key",
                        "key": "8ed84a3e-0c3d-49cc-b16a-4f5543d5e3a9_4sci1",
                        "Name": "date_key",
                        "dbId": "4sci1",
                        "category": "column",
                        "Id": "1",
                        "Datasource": "SampleTravelDataDerby",
                        "columnId": "8ed84a3e-0c3d-49cc-b16a-4f5543d5e3a9",
                        "icon": "",
                        "checkable": false,
                        "original": undefined
                    },
                    {
                        "title": "day_number",
                        "key": "06f934c8-c325-48f9-b187-f25c1b4d2360_4sci1",
                        "Name": "day_number",
                        "dbId": "4sci1",
                        "category": "column",
                        "Id": "1",
                        "Datasource": "SampleTravelDataDerby",
                        "columnId": "06f934c8-c325-48f9-b187-f25c1b4d2360",
                        "icon": "",
                        "checkable": false,
                        "original": undefined
                    },
                    {
                        "title": "dim_id",
                        "key": "daac9be5-db88-4dd4-9ffb-31fadb1a7579_4sci1",
                        "Name": "dim_id",
                        "dbId": "4sci1",
                        "category": "column",
                        "Id": "1",
                        "Datasource": "SampleTravelDataDerby",
                        "columnId": "daac9be5-db88-4dd4-9ffb-31fadb1a7579",
                        "icon": "",
                        "checkable": false,
                        "original": undefined
                    },
                    {
                        "title": "fiscal_month_label",
                        "key": "e632a468-d5b7-441b-9674-67779af272bd_4sci1",
                        "Name": "fiscal_month_label",
                        "dbId": "4sci1",
                        "category": "column",
                        "Id": "1",
                        "Datasource": "SampleTravelDataDerby",
                        "columnId": "e632a468-d5b7-441b-9674-67779af272bd",
                        "icon": "",
                        "checkable": false,
                        "original": undefined
                    },
                    {
                        "title": "fiscal_month_name",
                        "key": "cdf780f0-842a-4a9a-8476-7d492b73047e_4sci1",
                        "Name": "fiscal_month_name",
                        "dbId": "4sci1",
                        "category": "column",
                        "Id": "1",
                        "Datasource": "SampleTravelDataDerby",
                        "columnId": "cdf780f0-842a-4a9a-8476-7d492b73047e",
                        "icon": "",
                        "checkable": false,
                        "original": undefined
                    },
                    {
                        "title": "fiscal_year",
                        "key": "390cd762-17d9-4648-a977-77ad87cd08fb_4sci1",
                        "Name": "fiscal_year",
                        "dbId": "4sci1",
                        "category": "column",
                        "Id": "1",
                        "Datasource": "SampleTravelDataDerby",
                        "columnId": "390cd762-17d9-4648-a977-77ad87cd08fb",
                        "icon": "",
                        "checkable": false,
                        "original": undefined
                    },
                    {
                        "title": "modified_date",
                        "key": "d005b4e4-47f6-424c-8cce-edd7e0fa4909_4sci1",
                        "Name": "modified_date",
                        "dbId": "4sci1",
                        "category": "column",
                        "Id": "1",
                        "Datasource": "SampleTravelDataDerby",
                        "columnId": "d005b4e4-47f6-424c-8cce-edd7e0fa4909",
                        "icon": "",
                        "checkable": false,
                        "original": undefined
                    },
                    {
                        "title": "rating",
                        "key": "0a56d7f2-69d4-4175-afe4-dd3d797d2988_4sci1",
                        "Name": "rating",
                        "dbId": "4sci1",
                        "category": "column",
                        "Id": "1",
                        "Datasource": "SampleTravelDataDerby",
                        "columnId": "0a56d7f2-69d4-4175-afe4-dd3d797d2988",
                        "icon": "",
                        "checkable": false,
                        "original": undefined
                    }
                ]
            }
        ]
    }
}