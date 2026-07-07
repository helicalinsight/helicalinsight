import { handleTableDropToMetadata } from "../../../components/hi-metadata/utils"
import { data, storeData } from "./tabledrop.mock.data"

describe('testing table drop to metadata - if we are dragging a table with id and if it is already present in tables then it should not add', () => {
    test('should return error as the table id is already available', () => {
        let store = {
            getState: () => ({
                metadata: {
                    present: storeData
                }
            })
        }
        let result = handleTableDropToMetadata({ ...data, store, dispatch: () => { }, returnDataForJest: true })
        expect(result.length).toBe(5);
    })
})