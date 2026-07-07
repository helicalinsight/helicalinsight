import { handleSave } from '../../components/hi-metadata/utils/handleSave'
import { store4646 } from './4646.mock.data'

describe('testing bug 4425', () => {
    let store = {
        getState: () => ({
            metadata: {
                present: store4646
            }
        })
    }
    test('save with joins :: checking joins', () => {
        let result = handleSave({
            store,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })
        // TODO: Test case fix. Compare the whole joins formdata object
        let { joins } = result
        let isValid = true
        isValid = joins.length === 5
        isValid = joins.filter(eachJoins => !eachJoins.id).length === 0
        expect(isValid).toBe(true)
    })
})