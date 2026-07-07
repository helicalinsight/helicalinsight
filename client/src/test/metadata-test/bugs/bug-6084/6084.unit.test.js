import { storeData, fetchedData } from "./6084.mockdata";
import { updateStoreWithFetchedMetadata } from "../../../../components/hi-metadata/utils";

const store = {
    getState() {
        return { metadata: {
            present: storeData
        } }
    }
}

describe('To Test: Joins are updated to store after fetching data from API', () => {
    test('All joins are updated with all the properties', () => {
        let joins = updateStoreWithFetchedMetadata({
            fetchedMetadata: fetchedData,
            returnData: true,
            store
        });
        const uniqueJoins = [...new Set(joins.fetchedJoins.map(item => item.id))];
        
        expect(joins.fetchedJoins.length).toBe(5);
        expect(joins.fetchedJoins.length).toBe(uniqueJoins.length);
        expect(joins.fetchedJoins.filter(join => join.action === "noChange").length).toBe(5);
    })
})