import { handleFetchDetailsDataSource, handleOwnershipFormdata, handleSlnoOrder } from "../../../components/hi-admin/components/hi-recycle-bin/helperMethods";
import { fetchDetailsMockData, ownershipMockFormdata, recycleBinSlonoTestObj } from "./recycle-bin-test-helpermethods";

describe("Testing recycle bin helpermethods", () => {
    describe("Testing recycle bin handleSlnoOrder func", () => {
        test('Return Value must match with given output', (done) => {
            const {data, value} = recycleBinSlonoTestObj;
            expect(
                handleSlnoOrder(data.rec, data.i)
            ).toEqual(value);
            done();
        });
    });

    describe("Testing recycle bin handleFetchDetailsDataSource func", () => {
        test('Return Value must match with given output', (done) => {
            const {input, output} = fetchDetailsMockData;
            expect(
                handleFetchDetailsDataSource({...input})
            ).toEqual(output);
            done();
        });
    });
    describe("Testing recycle bin handleOwnershipFormdata func", () => {
        test('Return Value must match with given output', (done) => {
            const {input, output} = ownershipMockFormdata;
            expect(
                handleOwnershipFormdata({...input})
            ).toEqual(output);
            done();
        });
    });
})