import { handleSave, updateStoreWithFetchedMetadata } from "../../../components/hi-metadata/utils"
import { fetchedMetadata, joinsMDStore } from "./joins.mock.data"
import { mdStore_3_conn_crossJoins } from "./save.mock.data"

//all joins are provided at one place now from B.E
const updatedJoins = [
    {
        "id": "2deb64c133e7802835cba022533d7e55",
        "type": "inner",
        "operator": "=",
        "left": {
            "table": "geo_cordinates",
            "column": "location_id",
            "tableId": "f2ff93c37589ef57f40dcb15fda6d7ea",
            "connId": "1gobh",
            "dbId": "1gobh"
        },
        "right": {
            "table": "dimdate",
            "column": "dim_id",
            "tableId": "d324e793296ff76020c708f1c8fbb704",
            "connId": "1gobh",
            "dbId": "1gobh"
        },
        "uuid": "wz42-l5dd-vnmr-o3cr-lj",
        "index": 1,
        "action": "noChange"
    },
    {
        "id": "e82a776d8dbb97cc4da8eba20faa158f",
        "type": "inner",
        "operator": "=",
        "left": {
            "table": "employee_details",
            "column": "employee_id",
            "tableId": "b161910cbebfd353351a6c0b46e6a02e",
            "connId": "1gobh",
            "dbId": "1gobh"
        },
        "right": {
            "table": "meeting_details",
            "column": "meeting_by",
            "tableId": "025fbfb381cb17d4519363c3585626fb",
            "connId": "1gobh",
            "dbId": "1gobh"
        },
        "uuid": "gcud-co6s-f49d-k2o3-j7",
        "index": 2,
        "action": "noChange"
    },
    {
        "id": "28a7232594b3b5ce47f2f42ceee8cf9a",
        "type": "inner",
        "operator": "=",
        "left": {
            "table": "geo_cordinates",
            "column": "location_id",
            "tableId": "f2ff93c37589ef57f40dcb15fda6d7ea",
            "connId": "1gobh",
            "dbId": "1gobh"
        },
        "right": {
            "table": "travel_details",
            "column": "destination_id",
            "tableId": "21e1b86ae9680d0fc197ed543c3e37eb",
            "connId": "1gobh",
            "dbId": "1gobh"
        },
        "uuid": "uavl-83gs-oudp-i7qc-kp",
        "index": 3,
        "action": "noChange"
    },
    {
        "id": "0749a258652416b7d37d6174eac86ebf",
        "type": "inner",
        "operator": "=",
        "left": {
            "table": "geo_cordinates",
            "column": "location_id",
            "tableId": "f2ff93c37589ef57f40dcb15fda6d7ea",
            "connId": "1gobh",
            "dbId": "1gobh"
        },
        "right": {
            "table": "travel_details",
            "column": "source_id",
            "tableId": "21e1b86ae9680d0fc197ed543c3e37eb",
            "connId": "1gobh",
            "dbId": "1gobh"
        },
        "uuid": "ca5c-y374-haeg-utlo-3u",
        "index": 4,
        "action": "noChange"
    },
    {
        "id": "de4e30a53d95393a955a15094bf82856",
        "type": "inner",
        "operator": "=",
        "left": {
            "table": "employee_details",
            "column": "employee_id",
            "tableId": "b161910cbebfd353351a6c0b46e6a02e",
            "connId": "1gobh",
            "dbId": "1gobh"
        },
        "right": {
            "table": "travel_details",
            "column": "travelled_by",
            "tableId": "21e1b86ae9680d0fc197ed543c3e37eb",
            "connId": "1gobh",
            "dbId": "1gobh"
        },
        "uuid": "a4c9-mlie-ccx3-87wg-70",
        "index": 5,
        "action": "noChange"
    }
]

describe('testing joins in save formdata form metadata with multiple connections', () => {

    test('adding only one table but fetching catalogs and schemas for different tables', () => {
        let store = {
            getState() {
                return { metadata: {
                    present: mdStore_3_conn_crossJoins
                } }
            }
        }
        let result = handleSave({
            store,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })
        expect(result.connections.length).toBe(2) // joins related is not yet flinalized, hence commenting expects
        // console.log('result.joins', result.joins)
        // expect(result.joins.length).toBe(1)
        // expect(result.connections[0].joins.length).toBe(1)
        // expect(result.connections[1].joins.length).toBe(1)
        // expect(result.crossJoins.length).toBe(3)
        // expect(result.crossJoins.filter(join => join.databaseId || join.referenceId).length).toBe(3)
        // expect(result.crossJoins.filter(join => join.databaseId === join.referenceId).length).toBe(0)
    })

    // test('add join and update metadata - all joins from all connections should be visible', () => {
    //     let result = updateStoreWithFetchedMetadata({
    //         fetchedMetadata: {...fetchedMetadata, joins: updatedJoins},
    //         returnData: true,
    //         store: {
    //             getState: () => ({ metadata: joinsMDStore })
    //         }
    //     })
    //     expect(result.fetchedJoins.length).toBe(5)
    // })
})