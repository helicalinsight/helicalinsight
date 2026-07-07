import { handleTableDropToMetadata } from "../../../../components/hi-metadata/utils";
import { mockData, expectedResult } from "./6055.mock.data";

describe("Testing table drop to metadata", () => {
  test("To Test: After save unselected table should be removed from metadata section", () => {
    let result = handleTableDropToMetadata(mockData);
    expect(Object.keys(result).length).toBe(1);
    expect(result).toMatchObject(expectedResult);
  });
});
