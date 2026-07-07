import { getColorField } from "../../../../../../components/hi-reports/hi-editing-area/utils/property-utils";
import { mock_data } from "./getColorField.mock.data";

describe("getColorField function", () => {
  test("to check the functionality of getColorField function", () => {
    const formatColor = mock_data.formatColor;
    const text = "2018-06-01 09:07:21.00";
    const result = getColorField(formatColor, text);
    const expectedResult = [
      "2018-06-01 09:07:21.00",
      { a: 1, b: 230, g: 108, r: 84 },
    ];
    expect(result).toEqual(expectedResult);
  });
});
