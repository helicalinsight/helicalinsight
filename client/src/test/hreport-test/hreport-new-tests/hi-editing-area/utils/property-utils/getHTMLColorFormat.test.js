import { getHTMLColorFormat } from "../../../../../../components/hi-reports/hi-editing-area/utils/property-utils";

describe("getHTMLColorFormat function", () => {
  test("to check the functionality of getHTMLColorFormat function", () => {
    const colorValue = {
      r: 84,
      g: 108,
      b: 230,
      a: 1,
    };

    const result = getHTMLColorFormat(colorValue);
    const expectedResult = "rgba(84, 108, 230, 1)";
    expect(result).toEqual(expectedResult);
  });
});
