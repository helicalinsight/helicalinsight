import { handleSave } from "../../../../components/hi-metadata/utils";
import {storeData} from "./6113.mock.data";

const store = {
    getState() {
        return { metadata: {
            present: storeData.metadata
        } }
    }
}

describe('Save metadata: form data test', () => {
    test('To Test: metadata delete view formdata does not contain duplicate item', () => {
        let result = handleSave({
            store,
            dispatch: false,
            type: 'save',
            location: 'Folder',
            fileName: 'filename.metadata',
            returnDataForJest: true
        });
        // TODO:: Fix this test case. compare the whole result.removeItems object
        expect(result.removeItem.views.length).toBe(1);
        expect(result.removeItem.views[0].columns.length).toBe(1)
    })
})