import { getFieldName } from "../../../../../../components/hi-reports/hi-editing-area/utils/property-utils";
import { mock_data } from "./getFieldName.mock.data";
describe("getFieldName function", () => {
  test("to check the functionality of getFieldName function", () => {
   
    const formatColorField = "ee78559a-c99c-40d0-886d-18346527d26f";
    const report = mock_data.report
    const result = getFieldName (formatColorField, report);
    const expectedResult = "sum_dim_id"
    expect(result).toEqual(expectedResult);
  });
});
