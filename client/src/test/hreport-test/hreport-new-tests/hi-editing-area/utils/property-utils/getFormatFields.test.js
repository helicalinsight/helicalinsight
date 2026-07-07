import { getFormatFields } from "../../../../../../components/hi-reports/hi-editing-area/utils/property-utils";
import { mock_data } from "./getFormatFields.mock.data";
describe("getFormatFields function", () => {
  test("to check the functionality of getFormatFields function", () => {
    const itemsData = mock_data.itemsData;
    const groupKey = "format";
    const fieldsData = mock_data.fieldsData;

    const fieldId = "ee78559a-c99c-40d0-886d-18346527d26f";

    const result = getFormatFields(itemsData, groupKey, fieldsData, fieldId);
    const expectedResult = mock_data.expectedResult;
    expect(result).toEqual(expectedResult);
  });
});
