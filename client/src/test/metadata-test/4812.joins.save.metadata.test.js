import { handleSave, handleJoinActions } from '../../components/hi-metadata/utils'

import { store4812, store4812_2 } from './4812.mock.data'


/**
 * Bug 4812 - Metadata :: Edit :: Joins panel not working
 * below are the steps
 * Joins panel - edit metadata_1. 
 * remove all table except travel_details from the panel. 
 * reload and add to metadata panel. 
 * click on join. See all joins disappearing. 
 * now click on save. See all the joins appears again.
 */

describe('testing save with joins', () => {
    let store = {
        getState: () => ({
            metadata: {
                present: store4812
            }
        })
    }
    test('testing 4812 usecase checking if joins are present', () => {
        // { store, dispatch, type = 'save', location = false, fileName = false }
        let result = handleSave({
            store,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })
        expect(result.joins.length).toBeTruthy()
    })

    test('testing 4812 usecase checking joins lenth', () => {
        // { store, dispatch, type = 'save', location = false, fileName = false }
        let result = handleSave({
            store,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })
        expect(result.joins.length).toBe(8)
    })

    test('testing 4812 usecase checking joins action', () => {
        // { store, dispatch, type = 'save', location = false, fileName = false }
        let result = handleSave({
            store,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })
        expect(result.joins.filter(join => join.action !== 'delete').length).toBe(0)
    })

    // test('testing 4812 usecase checking invalid joins', () => {
    //     // { store, dispatch, type = 'save', location = false, fileName = false }
    //     let store = {
    //         getState: () => ({
    //             metadata: store4812_2
    //         })
    //     }
    //     let { emptyJoins, invalidJoins } = handleJoinActions({
    //         joins: store.getState().metadata.joins,
    //         listOfTableNames: Object.values(store.getState().metadata.tables).map(eachTable => eachTable.name),
    //         action: 'validateJoins',
    //     })
    //     expect(invalidJoins).toBeTruthy()
    // })
})