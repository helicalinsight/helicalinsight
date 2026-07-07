import { handleSave } from "../../../../components/hi-metadata/utils";
import {store_data_6045} from './6045.mock.data';

describe('To Test: Metadata Module form data', () => {
    let store = {
        getState() {
            return { metadata: {
                present: store_data_6045
            } }
        }
    }
    const formData = handleSave({
        store,
        dispatch: () => {},
        type: 'save',
        location: 'Sibtain',
        fileName: 'removeTablesTest',
        returnDataForJest: true
    });
    const { removedTables } = store.getState().metadata.present;

    test('To Test: Metadata table delete functionality', () => {  
        expect(removedTables[0].id).toEqual(formData.removeItem.tables[0]);
        expect(removedTables.length).toEqual(formData.removeItem.tables.length);
        expect(formData.removeItem.tables.filter(item => item === removedTables[0].id).length).toEqual(1);
    });
});