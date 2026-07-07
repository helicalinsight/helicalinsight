import { storeData, fetchedData } from "./6165.mock.data";
import { updateStoreWithFetchedMetadata } from "../../../../components/hi-metadata/utils";

const store = {
    getState() {
        return { metadata: {
            present: storeData
        } }
    }
}

describe('To Test: Views are updated in metadata after saving metadata', () => {
    test('All views are updated with all the properties', () => {
        const {fetchedViews} = updateStoreWithFetchedMetadata({
            fetchedMetadata: fetchedData,
            returnData: true,
            store
        });
        
        expect(fetchedViews.length).toBe(1);
        expect(Number(fetchedViews[0].id)).not.toBeNaN();
    })
})