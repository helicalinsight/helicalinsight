import { checkIsFound } from "../../../../../components/hi-reports/hi-fields-area/utils/suggetions";
import { mock_data_checkIsFound } from "./checkIsFound.mock.data";

describe("To Test: Suggestions function", () => {
  test("To Test the checkIsFound function", () => {
    const newValue = mock_data_checkIsFound.newValue;
    const databaseFunctions = mock_data_checkIsFound.databaseFunctions;
    const expectedResult = mock_data_checkIsFound.expectedResult;

    let result = checkIsFound({ newValue, databaseFunctions });
    console.log("result", result);
    expect(result).toEqual(expectedResult);
  });
});
