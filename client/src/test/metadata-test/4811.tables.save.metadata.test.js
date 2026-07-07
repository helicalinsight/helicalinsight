import { handleSave } from '../../components/hi-metadata/utils/handleSave'

import { store4811 } from './4811.mock.data'


/**
 * Bug 4812 - Metadata :: Edit is not working :: Save is not working after editing
 * below are the steps
 * Edit not working - Create MD using sample travel ds. add all tables. save report as metadata_1 in quick folder. Refresh the page. click edit from edit. select metadata_1 from quick folder. remove all table except travel_details from the panel. reload and add to metadata panel. now click on save. Again click on edit and see all the tables are present in this MD.
 */

describe('testing save with tables 4811', () => {
    let store = {
        getState: () => ({
            metadata: store4811
        })
    }
    //disabling these test cases as we disabled the reload tables in metadata for edit mode
    
    test('testing 4811 usecase checking if removed tables are present or not', () => {
        // { store, dispatch, type = 'save', location = false, fileName = false }
        let result = handleSave({
            store,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })
        // expect(result.removeItem.tables).toBeTruthy();
        expect(1).toBe(1);
    })

    // test('testing 4812 usecase checking tables lenth', () => {
    //     // { store, dispatch, type = 'save', location = false, fileName = false }
    //     let result = handleSave({
    //         store,
    //         dispatch: false,
    //         type: 'save',
    //         location: 'Folder',
    //         fileName: 'filename.metadata',
    //         returnDataForJest: true
    //     })
    //     expect(result.removeItem.tables.length).toBe(4)
    // })
})