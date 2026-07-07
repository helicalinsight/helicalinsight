import { getFields } from "../../../../../../components/hi-reports/hi-editing-area/utils/property-utils";
import { mock_data } from "./getFields.mock.data";
describe("getFields function", () => {
  test("to check the functionality of getFields function", () => {
    const report = mock_data.report
    const type = "";

    const result = getFields(report, type);
    const expectedResult = [
      { key: "ee78559a-c99c-40d0-886d-18346527d26f", label: "sum_dim_id" },
      { key: "59eb7057-2c94-4594-812d-9e4353ec1278", label: "modified_date" },
    ];
    expect(result).toEqual(expectedResult);
  });
});
