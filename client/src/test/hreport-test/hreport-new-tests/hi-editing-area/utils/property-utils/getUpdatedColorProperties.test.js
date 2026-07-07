import { getUpdatedColorProperties } from "../../../../../../components/hi-reports/hi-editing-area/utils/property-utils";
import { mock_data } from "./getUpdatedColorProperties.mock.data";
describe("getUpdatedColorProperties function", () => {
  test("to check the functionality of getUpdatedColorProperties function", () => {
    const itemsData = mock_data.itemsData;
    const groupKey = "color";
    const showAllColorFields = false;

    const formatColorField = "59eb7057-2c94-4594-812d-9e4353ec1278";
    const formatColorStyle = "gradient";
    const report = mock_data.report;
    const result = getUpdatedColorProperties(
      itemsData,
      groupKey,
      showAllColorFields,
      formatColorField,
      formatColorStyle,
      report
    );
    const expectedResult = mock_data.expectedResult;
    /// need to check thatt all expected properties matching or not 
    expect(result).toMatchObject(expectedResult);
  });
});
