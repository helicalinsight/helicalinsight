/**
 * BUG DESCRIPTION
 * 
 * Metadata:: Derby(2 connections):: Select tables from 1 connection(Derby):: All tables are getting selected from another Derby connection too.

Steps to reproduce:

1. Open any browser enter application url(http://127.0.0.1:8085/hi-ee/) and login as hiadmin.
2. Go to Metadata and click on Create.
3. Expand Derby(1).
4. Expand SampleTravelDataDerby connection.
5. Expand HIUSER Schema.
6. Right click on tables and Select All.
7. Expand another derby connection.

Actual: All tables are selected in another derby connection too.
Expected: Should be select tables from another derby connection.

Note: Observed Same behavior in edit mode too.

All tables are getting selected in other derby connection while loading metadata in edit mode.
 */

import { updateDSToRenderWithTables } from "../../../../components/hi-metadata/utils"
import { mockData } from "./5857.mock"

describe('checking if there are any tables with same ID are selected if anyone of it is selected', () => {
    test('checkig the SELECTED property for the table in  datasource list to render', () =>{
        let [datasourceListToRender] = updateDSToRenderWithTables(mockData)
        expect(datasourceListToRender[0].children[0].children[3].children.filter(table => table.selected).length).toBe(0)
    })
})

// describe('Joins in Metadata Create Mode', () => {
//     test('Check join contains dataSource property', () => {
//         // let store = {
//         //     getState() {
//         //         return { metadata: mockReduxStoreData.metadata }
//         //     }
//         // }
//         // let result = handleSave({
//         //     store,
//         //     dispatch: false,
//         //     type: 'save',
//         //     location: 'Folder',
//         //     fileName: 'filename.metadata',
//         //     returnDataForJest: true
//         // });
//         // const join = store.getState().metadata.joins[0]
//         // expect(Boolean(join.left.dataSource)).toBe(true);
//         // expect(Boolean(join.right.dataSource)).toBe(true);
//         expect(5).toEqual(5);
//     });
// })