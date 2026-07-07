import { getSuggestions } from "../../../../../components/hi-reports/hi-fields-area/utils/suggetions";
import { mock_data_getSuggestions } from "./getSuggestions.mock.data";

describe("To Test: Suggestions function", () => {
    test("To Test the getSuggestions function", () => {

        const value= mock_data_getSuggestions.value;
        const list =mock_data_getSuggestions.list;
        const expectedResult = mock_data_getSuggestions.expectedResult;

        let result = getSuggestions(value, list);

        expect(result).toEqual(expectedResult)
    })
})