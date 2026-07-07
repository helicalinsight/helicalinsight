import { appendRequestId } from "../../../base/service";

describe('Testing abord id functionality', () => {

    test('testing appendRequestId func', async () => {
        const initialData= {
            data:{
            "type": "adhoc",
            "serviceType": "metadata",
            "service": "metadataWorkflow",
            "formData": "eyJpZCI6IjEiLCJ0eXBlIjoiZHluYW1pY0RhdGFTb3VyY2UiLCJwYXJhbWV0ZXJzIjp7ImZldGNoVGFibGVzIjp0cnVlLCJmZXRjaERhdGEiOlt7InNjaGVtYXMiOlt7Im5hbWUiOiJTWVNGVU4ifV19XX19",
        }, id: "63052999-cb3b-4813-813d-3e2eab418377"}
        const Expval = {
            "type": "adhoc",
            "serviceType": "metadata",
            "service": "metadataWorkflow",
            "formData": "eyJpZCI6IjEiLCJ0eXBlIjoiZHluYW1pY0RhdGFTb3VyY2UiLCJwYXJhbWV0ZXJzIjp7ImZldGNoVGFibGVzIjp0cnVlLCJmZXRjaERhdGEiOlt7InNjaGVtYXMiOlt7Im5hbWUiOiJTWVNGVU4ifV19XX19",
            "requestId": "63052999-cb3b-4813-813d-3e2eab418377"
        }
        expect(appendRequestId(initialData.data, initialData.id)).toEqual(Expval);
    })
})
