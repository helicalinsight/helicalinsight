
/**
 * BUG 5882
 * 
 * Metadata:: MultiConnections:: Save:: EDIT:: Duplicate:: Save:: EDIT:: Save:: Unable to save the metadata getting "Error: JSONException: JSONObject[\"originalId\"] not found."

Steps to reproduce:

1. Open any browser enter application url(http://127.0.0.1:8085/hi-ee/) and login as hiadmin.
2. Go to MEtadata module.
3. Add all tables from 2 different connections(Mysql(All table) & Derby(All table) ).
4. Save the metadata.
Metadata is saved successfully.

5. Go to EDIT or File Browser.
6. Edit/Reload the save metadata.
Metadata is Loaded successfully.
7. Right click on any table and duplicate the table.
8. Save the metadata.
Metadata is saved successfully.

9. Go to EDIT or File Browser.
10. Edit/Reload the save metadata.
Metadata is Loaded successfully.

11. Save the metadata.


Actual: Metadata is not saving and getting error while saving metadata in edit mode by duplicate the table
{"status":0,"response":{"message":"Error: JSONException: JSONObject[\"originalId\"] not found."}}

Expected: Should not get above error and metadata should be saved successfully.
 */

import { handleSave } from "../../../../components/hi-metadata/utils"
import { mockData, mockData2 } from "./5882.mock.data"

describe('checking if the formdata is proper when updating metadata(with duplicate tables) without any changes', () => {
    test('checking formdata for metadata update witj duplicate tbles', () => {
        let result = handleSave({
            store: mockData,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })

        expect(result.duplicate.table.length).toBe(0)
        expect(result.duplicate.column.length).toBe(0)
    })
    test('checking formdata for metadata update witj duplicate tbles one from each connection', () => {
        let result = handleSave({
            store: mockData2,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })
        expect(result.duplicate.table.length).toBe(0)
        expect(result.duplicate.column.length).toBe(0)
    })
})

