import { handleSave } from "../../../../components/hi-metadata/utils";
import {mockReduxStoreData} from './4628.mock.data';

describe('Metadata Module:', () => {
    let store = {
        getState() {
            return { metadata: {
                present: mockReduxStoreData.metadata
            } }
        }
    }
    let result = handleSave({
        store,
        dispatch: () => {},
        type: 'save',
        location: 'Folder',
        fileName: 'duplicate',
        returnDataForJest: true
    });
    test("To Test: Metadata duplicate tables are having unique names in form data", () => {  
        expect(result.duplicate.table[0].alias).toEqual("dimdate_1");
        expect(result.duplicate.table[1].alias).toEqual("dimdate_2");
        expect(result.duplicate.table[0].originalName).toEqual("dimdate");
        expect(result.duplicate.table[1].originalName).toEqual("dimdate");
    });
});