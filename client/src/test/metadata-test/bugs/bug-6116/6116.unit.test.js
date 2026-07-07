import { handleSave } from "../../../../components/hi-metadata/utils";
import {storeData} from "./6116.mock.data"

describe('To Test: Joins in multiconnection', () => {
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
    const addedJoin = result.joins[0];
    test("To Test: Second connection join contains table name", () => {  
        expect((addedJoin.left.table.length > 0 && addedJoin.right.table.length > 0)).toBe(true);
        expect((addedJoin.right.table.length > 0 && addedJoin.right.table.length > 0)).toBe(true);
    });

    
    test("To Test: Second connection join contains table name not tableKey", () => {  
        const tables = Object.values(storeData.metadata.tables);
        let currentTable = tables.find(table => table.id === addedJoin.right.tableId && table.connId === addedJoin.right.dbId);        

        expect((addedJoin.right.table !== currentTable.keyName)).toBe(true);
        expect((addedJoin.right.table === currentTable.alias)).toBe(true);
        expect(addedJoin.right.table.includes(addedJoin.right.dbId)).toBe(false);
        expect(addedJoin.left.table.includes(addedJoin.left.dbId)).toBe(false);

    });
});