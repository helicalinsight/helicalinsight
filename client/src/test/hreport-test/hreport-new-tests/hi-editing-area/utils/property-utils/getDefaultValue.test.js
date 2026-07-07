import { getDefaultValue } from "../../../../../../components/hi-reports/hi-editing-area/utils/property-utils";
import { mock_data } from "./getDefaultValue.mock.data";
describe("getDefaultValue function", () => {
  test("to check the functionality of getDefaultValue function", () => {
    const report = mock_data.report;
    const formatColorField = "";

    const result = getDefaultValue(report, formatColorField);
    const expectedResult = "gradient";
    expect(result).toEqual(expectedResult);
  });
});
