import { handleSave } from "../../../../components/hi-metadata/utils";
import {storeData} from "./6519.mock.data"

describe('Create Mode - Column alias change in metadata', () => {
    let store = {
        getState() {
            return { metadata: {
                present: storeData.metadata
            } }
        }
    }
    let result = handleSave({
        store,
        dispatch: () => {},
        type: 'save',
        location: 'Folder',
        fileName: 'multiConnectionJoins',
        returnDataForJest: true
    });
    const changedColumns = result.changeItem.columns;
    test("To Test: Form-data contains changed column", () => {  
        expect(Object.values(changedColumns[0]).includes(undefined)).toBe(false);
        expect(changedColumns.length).toBe(1)
        expect(changedColumns[0].aliasChanged).toBe(true);
    });
});