import { handleUpdatedStoreDs } from "../../components/hi-metadata/utils";

const tempDataSources = [{
    "sync": false,
    "id": "1",
    "catSchemaPredicted": false,
    "catalog": "",
    "schema": "HIUSER",
    "type": "dynamicDataSource",
    "baseType": "global.jdbc",
    "dbId": "14326",
    "driver": {
        "data": {
            "id": "1",
            "type": "dynamicDataSource"
        },
        "dataSourceProvider": "tomcat",
        "type": "dynamicDataSource",
        "permissionLevel": 5,
        "driver": "org.apache.derby.jdbc.AutoloadedDriver",
        "name": "SampleTravelDataDerby",
        "classifier": "global",
        "dataSourceType": "Managed DataSource"
    },
    "datasourceName": "SampleTravelDataDerby",
    "driverType": "Derby",
    "connId": "14326",
    "classifier": "db.generic",
    "joinsFetched": true,
    "oldDbId": "dxik1"
}, {
    "sync": false,
    "id": "1302",
    "catSchemaPredicted": false,
    "catalog": "",
    "schema": "",
    "type": "dynamicDataSource",
    "baseType": "global.jdbc",
    "dbId": "14327",
    "driver": {
        "data": {
            "id": "1302",
            "type": "dynamicDataSource"
        },
        "dataSourceProvider": "tomcat",
        "type": "dynamicDataSource",
        "permissionLevel": 5,
        "driver": "org.sqlite.JDBC",
        "name": "Sqlite",
        "classifier": "global",
        "dataSourceType": "Managed DataSource"
    },
    "datasourceName": "Sqlite",
    "driverType": "Sqlite",
    "connId": "14327",
    "classifier": "db.generic",
    "joinsFetched": true,
    "oldDbId": "tub2y",
    "isExistedDS": true
}];
const result = [
    {
        "sync": false,
        "id": "1",
        "catSchemaPredicted": false,
        "catalog": "",
        "schema": "HIUSER",
        "type": "dynamicDataSource",
        "baseType": "global.jdbc",
        "dbId": "14326",
        "driver": {
            "data": {
                "id": "1",
                "type": "dynamicDataSource"
            },
            "dataSourceProvider": "tomcat",
            "type": "dynamicDataSource",
            "permissionLevel": 5,
            "driver": "org.apache.derby.jdbc.AutoloadedDriver",
            "name": "SampleTravelDataDerby",
            "classifier": "global",
            "dataSourceType": "Managed DataSource"
        },
        "datasourceName": "SampleTravelDataDerby",
        "driverType": "Derby",
        "connId": "14326",
        "classifier": "db.generic",
        "joinsFetched": true,
        "oldDbId": "dxik1"
    },
    {
        "sync": false,
        "id": "1302",
        "catSchemaPredicted": false,
        "catalog": "",
        "schema": "",
        "type": "dynamicDataSource",
        "baseType": "global.jdbc",
        "dbId": "14327",
        "driver": {
            "data": {
                "id": "1302",
                "type": "dynamicDataSource"
            },
            "dataSourceProvider": "tomcat",
            "type": "dynamicDataSource",
            "permissionLevel": 5,
            "driver": "org.sqlite.JDBC",
            "name": "Sqlite",
            "classifier": "global",
            "dataSourceType": "Managed DataSource"
        },
        "datasourceName": "Sqlite",
        "driverType": "Sqlite",
        "connId": "14327",
        "classifier": "db.generic",
        "joinsFetched": true,
        "oldDbId": "tub2y",
        "isExistedDS": true
    }
];
let updatedStoreDs = [
    {
        "id": "1",
        "type": "dynamicDataSource",
        "baseType": "global.jdbc",
        "catSchemaPredicted": false,
        "sync": false,
        "catalog": "",
        "schema": "HIUSER",
        "connId": "dxik1",
        "dbId": "dxik1",
        "classifier": "db.workflow",
        "datasourceName": "SampleTravelDataDerby",
        "dsKeyPath": "r372-bbav-1gz8-mphm-ow/veo7-vbxk-jze5-9og2-a5/wifd-ekm5-vte7-ikyu-6u",
        "driverType": "Derby",
        "database": "HIUSER"
    },
    {
        "id": "1302",
        "type": "dynamicDataSource",
        "baseType": "global.jdbc",
        "catSchemaPredicted": false,
        "sync": false,
        "catalog": "",
        "schema": "",
        "connId": "tub2y",
        "dbId": "tub2y",
        "classifier": "db.workflow",
        "datasourceName": "Sqlite",
        "dsKeyPath": "excy-n626-wird-0ph8-es/r6yw-b33t-8xnc-8hq2-sj",
        "driverType": "Sqlite",
        "database": "Sqlite"
    }
];

describe('Testing handleUpdatedStoreDs func', () => {
    test('must match the result', (done) => {
        tempDataSources.forEach(ds => {
            updatedStoreDs = handleUpdatedStoreDs({updatedStoreDs, tempDataSource: ds})
        })
        expect(updatedStoreDs).toEqual(result)
        done();
    })
})