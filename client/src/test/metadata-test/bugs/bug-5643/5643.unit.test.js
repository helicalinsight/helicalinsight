import { handleSave } from "../../../../components/hi-metadata/utils";
import {mockReduxStoreData} from './5643.mock.data';

describe('metadata with views test', () => {
    test('User should be able to change alias name of view in metadata section', () => {
        let store = {
            getState() {
                return { metadata: {
                    present: mockReduxStoreData.metadata
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
        });
        expect(result.changeItem.tables[0].alias).toEqual('alias_dimdate');
    });
});