import { handleSave } from "../../../../components/hi-metadata/utils";
import {metadataStoreMock, mockReduxStoreData} from './5832.mock.data';

describe('Joins in Metadata Create Mode', () => {
    test('Check join contains dataSource property', () => {
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
        const join = store.getState().metadata.present.joins[0]
        expect(Boolean(join.left.dataSource)).toBe(true);
        expect(Boolean(join.right.dataSource)).toBe(true);
        expect(result.joins.length).toEqual(5);
    });

    test('Check if joins are passing in formdata after metadata is saved and then saving again without any changes', () =>{
        let store = {
            getState(){
                return {metadata : {
                    present: metadataStoreMock
                }}
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
        expect(result.joins.filter(j => j.action !== 'delete').length).toBe(5)
    })
});