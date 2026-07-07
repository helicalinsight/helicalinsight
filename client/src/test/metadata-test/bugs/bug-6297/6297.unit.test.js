import { changeDatasourceUtil } from "../../../../components/hi-metadata/utils/changeDatasourceUtil";
import { changeDsMockData, returnData } from "./6297.mock.data";

describe("Testing change datasource in metadata", () => {
  test("To Test: Change datasource utility has correct datasource list with updated connection Id's", () => {
    let result = changeDatasourceUtil(changeDsMockData);
    expect(result).toStrictEqual(returnData);
  });
});


