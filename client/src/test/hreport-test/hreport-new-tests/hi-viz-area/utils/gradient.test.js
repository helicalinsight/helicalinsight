import { generateColor } from "../../../../../components/hi-reports/hi-viz-area/utils/gradient";

describe("gradient function", () => {
  test("checking the functionality of generateColor function", async () => {
    const actualValue = 8173137;
    const max = 9000000;
    let colorValue = (100 * actualValue) / max;
    const expectedResult = "1717fd";
    let result = generateColor("#0000ff", "#fffff0", colorValue);
    expect(result).toEqual(expectedResult);
  });
});
