import { modifyFilters } from "../../../../utils/utilities";
import { mock_data } from "./bug-6891.mock.data";

describe("To Test: modifyFilters", () => {
    test("To Test the modifyFilters function in email/schedule", () => {

        const activeReport= mock_data.activeReport;
        const parameters =mock_data.parameters;
        const expectedResult = mock_data.expectedResult;

        let result = modifyFilters(activeReport , parameters);

        expect(result).toEqual(expectedResult)
    })
})