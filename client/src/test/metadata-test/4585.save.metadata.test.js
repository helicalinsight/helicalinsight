import { handleSave } from '../../components/hi-metadata/utils/handleSave'
import { store4585 } from './4585.mock.data'
/**
 * Bug 4585 - Metadata::Save As:: Metadata is saving in same old folder even though we selected different folder at the time of Save As
 */

describe('Test cases for 4584 :: Save metadata usecases', () => {
    let store = {
        getState: () => ({
            metadata: {
                present: store4585
            }
        })
    }
    test('saving in a new location SAVE AS', () => {
        let result = handleSave({
            store,
            dispatch: false,
            type: 'saveAs',
            location: 'NewFolder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })
        expect(result.location).toBe('Gagan')
    })
    test('Chekcing update with previous location', () => {
        let result = handleSave({
            store,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        })
        expect(result.location).toBe('Gagan')
    })
})