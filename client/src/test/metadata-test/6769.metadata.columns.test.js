import { handleColumnsAliasChange } from "../../components/hi-metadata/utils"

const data = [{
    input: {
        "tables": {
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
                    "connId": "5mc8n",
                    "dbId": "5mc8n",
                    "classifier": "db.workflow",
                    "datasourceName": "SampleTravelDataDerby",
                    "dsKeyPath": "fh5i-hhwt-je7y-rleq-li/e3a6-cebi-dosc-s4de-bn/rtbh-sj9k-eotk-50qb-hg",
                    "driverType": "Derby",
                    "database": "HIUSER"
                },
                "category": "table",
                "connId": "5mc8n",
                "oldDbId": "5mc8n",
                "keyPath": "fh5i-hhwt-je7y-rleq-li/e3a6-cebi-dosc-s4de-bn/rtbh-sj9k-eotk-50qb-hg/rtjy-94cf-13ki-mo2m-k7",
                "uniqueKey": "4e1fd245f4d13b77be423a43f01d80b2_5mc8n",
                "schema": "HIUSER",
                "selected": true,
                "keyName": "employee_details",
                "columns": {
                    "address": {
                        "alias": "address_o1",
                        "fullyQualifiedColumn": "employee_details.address",
                        "id": "439da890-4679-42e7-8565-5eec585c42ba",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "uniqueKey": "439da890-4679-42e7-8565-5eec585c42ba_5mc8n",
                        "name": "address",
                        "tableKey": "employee_details",
                        "connId": "5mc8n",
                        "columnKey": "address",
                        "tableId": "4e1fd245f4d13b77be423a43f01d80b2"
                    },
                    "address_1": {
                        "alias": "address_o_d1",
                        "fullyQualifiedColumn": "employee_details.address",
                        "id": "e28d-lfk3-osl7-xn7s-u6",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "uniqueKey": "e28d-lfk3-osl7-xn7s-u6_5mc8n",
                        "name": "address_1",
                        "tableKey": "employee_details",
                        "connId": "5mc8n",
                        "columnKey": "address_1",
                        "tableId": "4e1fd245f4d13b77be423a43f01d80b2",
                        "originalId": "439da890-4679-42e7-8565-5eec585c42ba",
                        "duplicate": true,
                        "originalName": "address"
                    }
                },
                "columnsFetched": true
            },
            "employee_details_1": {
                "id": "vvx7-1oob-z32r-diwg-bh",
                "name": "employee_details_1",
                "alias": "employee_details_1",
                "dataSource": {
                    "id": "1",
                    "type": "dynamicDataSource",
                    "baseType": "global.jdbc",
                    "catSchemaPredicted": false,
                    "sync": false,
                    "catalog": "",
                    "schema": "HIUSER",
                    "connId": "5mc8n",
                    "dbId": "5mc8n",
                    "classifier": "db.workflow",
                    "datasourceName": "SampleTravelDataDerby",
                    "dsKeyPath": "fh5i-hhwt-je7y-rleq-li/e3a6-cebi-dosc-s4de-bn/rtbh-sj9k-eotk-50qb-hg",
                    "driverType": "Derby",
                    "database": "HIUSER"
                },
                "category": "table",
                "connId": "5mc8n",
                "oldDbId": "5mc8n",
                "keyPath": "fh5i-hhwt-je7y-rleq-li/e3a6-cebi-dosc-s4de-bn/rtbh-sj9k-eotk-50qb-hg/rtjy-94cf-13ki-mo2m-k7",
                "uniqueKey": "vvx7-1oob-z32r-diwg-bh_5mc8n",
                "schema": "HIUSER",
                "keyName": "employee_details_1",
                "columns": {
                    "address": {
                        "alias": "address_d_o",
                        "fullyQualifiedColumn": "employee_details.address",
                        "id": "4t31-3ps2-6bhv-mapd-yi",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "uniqueKey": "4t31-3ps2-6bhv-mapd-yi_5mc8n",
                        "name": "address",
                        "tableKey": "employee_details_1",
                        "connId": "5mc8n",
                        "columnKey": "address",
                        "tableId": "vvx7-1oob-z32r-diwg-bh",
                        "originalId": "439da890-4679-42e7-8565-5eec585c42ba"
                    },
                    "address_1": {
                        "alias": "address_d_1",
                        "fullyQualifiedColumn": "employee_details.address",
                        "id": "u2ax-109i-89rd-0oof-ib",
                        "defaultFunction": "db.generic.groupBy.group",
                        "type": {
                            "java.lang.String": "text"
                        },
                        "category": "column",
                        "uniqueKey": "u2ax-109i-89rd-0oof-ib_5mc8n",
                        "name": "address_1",
                        "tableKey": "employee_details_1",
                        "connId": "5mc8n",
                        "columnKey": "address_1",
                        "tableId": "vvx7-1oob-z32r-diwg-bh",
                        "originalId": "439da890-4679-42e7-8565-5eec585c42ba",
                        "duplicate": true,
                        "originalName": "address"
                    }
                },
                "columnsFetched": true,
                "originalName": "employee_details",
                "originalId": "4e1fd245f4d13b77be423a43f01d80b2",
                "duplicate": true
            }
        },
        "tableKey": "employee_details",
        "changedColumns": [
            {
                "alias": "address_o_d1",
                "name": "address_1",
                "id": "e28d-lfk3-osl7-xn7s-u6",
                "originalId": "439da890-4679-42e7-8565-5eec585c42ba",
                "connId": "5mc8n",
                "tableId": "4e1fd245f4d13b77be423a43f01d80b2",
                "duplicate": true
            },
            {
                "alias": "address_o",
                "columnId": "439da890-4679-42e7-8565-5eec585c42ba",
                "connId": "5mc8n",
                "tableId": "4e1fd245f4d13b77be423a43f01d80b2",
                "aliasChanged": true
            }
        ],
        "record": {
            "alias": "address_o",
            "fullyQualifiedColumn": "employee_details.address",
            "id": "439da890-4679-42e7-8565-5eec585c42ba",
            "defaultFunction": "db.generic.groupBy.group",
            "type": {
                "java.lang.String": "text"
            },
            "category": "column",
            "uniqueKey": "439da890-4679-42e7-8565-5eec585c42ba_5mc8n",
            "name": "address",
            "tableKey": "employee_details",
            "connId": "5mc8n",
            "columnKey": "address",
            "tableId": "4e1fd245f4d13b77be423a43f01d80b2"
        },
        "updatedAlias": "address_o1"
    },
    output: [
        {
            "alias": "address_o_d1",
            "name": "address_1",
            "id": "e28d-lfk3-osl7-xn7s-u6",
            "originalId": "439da890-4679-42e7-8565-5eec585c42ba",
            "connId": "5mc8n",
            "tableId": "4e1fd245f4d13b77be423a43f01d80b2",
            "duplicate": true
        },
        {
            "alias": "address_o1",
            "columnId": "439da890-4679-42e7-8565-5eec585c42ba",
            "connId": "5mc8n",
            "tableId": "4e1fd245f4d13b77be423a43f01d80b2",
            "aliasChanged": true
        }
    ]
}]

describe('Testing handleColumnsAliasChange func', () => {
    test('must match the result', (done) => {
        data.forEach(ele => {
            expect(handleColumnsAliasChange({...ele.input})).toEqual(ele.output)
        })
        done();
    })
})