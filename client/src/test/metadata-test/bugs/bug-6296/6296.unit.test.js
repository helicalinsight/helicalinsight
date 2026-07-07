import { storeData, fetchedData, result } from "./6296.mock.data";
import { updateStoreWithFetchedMetadata } from "../../../../components/hi-metadata/utils";

const store = {
    getState() {
        return { metadata: {
            present: storeData
        } }
    }
}

describe('To Test: after saving metadata with changed datasource', () => {
    test('changed property from datasource should not be present in store datasource once metadata is saved', () => {
        let { updatedStoreDs } = updateStoreWithFetchedMetadata({
            fetchedMetadata: fetchedData,
            returnData: true,
            store
        });     
        expect(updatedStoreDs).toEqual(result);
    })
})