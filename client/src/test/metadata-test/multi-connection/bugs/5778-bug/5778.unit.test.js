import { handleSave } from "../../../../../components/hi-metadata/utils";
import {mockReduxStoreData} from './5778.mock.data';

// TODO: Fix test case. Compare the whole result object.
describe('Test: Multidata join Test', () => {
    test('To Test joins are not duplicated after saving the metadata', () => {
        let store = {
            getState() {
                return { metadata: mockReduxStoreData.metadata }
            }
        }
        let result = handleSave({
            store,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        });

        expect(result.joins.length).toEqual(10);
    });
});