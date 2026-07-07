import { handleSave } from "../../../../components/hi-metadata/utils";
import {storeData} from './5976.mock.data'

describe('Metadata Module:', () => {
    let store = {
        getState() {
            return { metadata: {
                present: storeData
            } }
        }
    }
    let result = handleSave({
        store,
        dispatch: () => {},
        type: 'saveAs',
        location: 'Folder',
        fileName: 'duplicate',
        returnDataForJest: true
    });
    // TODO: Fix test case. compare whole form data object
    test('Tables are saved in metadata in saveAs mode', () => {  
        expect(result.addItem.tables.length).toEqual(1);
    });
});