import { calculateSerialNumber } from "../../utils/table-utils";

describe("calculateSerialNumber", () => {
  test("to test calculateSerialNumber function", async () => {
    const page = 2;
    const pageSize = 20;
    const index = 19;
    const result = calculateSerialNumber(page, pageSize, index);
    const expectedResult = 40;
   
    expect(result).toEqual(expectedResult);
  });
});
