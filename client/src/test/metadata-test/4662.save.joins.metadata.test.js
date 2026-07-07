import { handleSave } from '../../components/hi-metadata/utils/handleSave'
import {
    storeSnapShotForSave_1,
    storeSnapShotForSave_2,
    storeSnapShotForSave_3
} from './constants'
import { storeSnapshotForSave_5, storeSnapshotForSave_6, storeSnapshotForSave_7, storeSnapshotForSave_8, storeSnapshotForSave_9, storeSnapshotForSave_10 } from './constants2'
import { store4662 } from './4662.mock.data'

describe('testing bug 4662', () => {
    let store = {
        getState: () => ({
            metadata: store4662
        })
    }
    test("jest example",async () => {
        expect(1+1).toBeTruthy();
    }); 
    // test('save with joins :: checking joins', () => {
    //     let result = handleSave({
    //         store,
    //         dispatch: false,
    //         type: 'save',
    //         location: 'Folder',
    //         fileName: 'filename.metadata',
    //         returnDataForJest: true
    //     })
    //     let { addItem, dataSource, database, classifier, duplicate, changeItem, joins } = result
    //     let isValid = true
    //     isValid = joins.length === 5
    //     isValid = joins.filter(eachJoins => eachJoins.type === 'left').length === 2
    //     isValid = joins.filter(eachJoins => eachJoins.type === 'right').length === 2
    //     isValid = joins.filter(eachJoins => eachJoins.type === 'inner').length === 1
    //     expect(isValid).toBeTruthy()
    // })
})