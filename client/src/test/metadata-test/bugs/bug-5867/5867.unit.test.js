import { handleSave } from "../../../../components/hi-metadata/utils";
import {mockReduxStoreData} from './5867.mock.data';

describe('save metadata test', () => {
    test('metadata name validation', () => {
        let store = {
            getState() {
                return { metadata: mockReduxStoreData.metadata }
            }
        }
        let result = handleSave({
            store,
            dispatch: () => {},
            type: 'save',
            location: 'Folder',
            fileName: '@test123',
            returnDataForJest: true
        });
        expect(result).toEqual(undefined);
    });
});