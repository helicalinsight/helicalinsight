import { getUpdatedRangeProperties } from "../../../../../../components/hi-reports/hi-editing-area/utils/property-utils";
import { mock_data } from "./getUpdatedRangeProperties.mock.data";

describe("getUpdatedRangeProperties function", () => {
  test("to check the functionality of getUpdatedRangeProperties function", () => {
    const reportData = mock_data.reportData;
    const itemsData = mock_data.itemsData;
    const groupKey = "axisRange";
    const fieldsData = mock_data.fieldsData;
    const fieldId = "ee78559a-c99c-40d0-886d-18346527d26f";
    const axisRangeDataType = "numeric";
    const result = getUpdatedRangeProperties(
      reportData,
      itemsData,
      groupKey,
      fieldsData,
      fieldId,
      axisRangeDataType
    );
    const expectedResult = mock_data.expectedResult;
    expect(result).toEqual(expectedResult);
  });

  test("to check the functionality of getUpdatedRangeProperties function when fields data is empty", () => {
    const reportData = mock_data.reportData;
    const itemsData = mock_data.itemsData;
    const groupKey = "axisRange";
    const fieldsData = [];
    const fieldId = "ee78559a-c99c-40d0-886d-18346527d26f";
    const axisRangeDataType = "numeric";
    const result = getUpdatedRangeProperties(
      reportData,
      itemsData,
      groupKey,
      fieldsData,
      fieldId,
      axisRangeDataType
    );
    const expectedResult = {
      activeDatatype: "numeric",
      activeId: "ee78559a-c99c-40d0-886d-18346527d26f",
      fields: [],
      gridLines: [],
    };
    expect(result).toEqual(expectedResult);
  });

  test("to check the functionality of getUpdatedRangeProperties function when field type is continuous", () => {
    const reportData = mock_data.reportData;
    const itemsData = mock_data.itemsData;
    const groupKey = "axisRange";
    const fieldsData = mock_data.fieldsData;
    const fieldId = "ee78559a-c99c-40d0-886d-18346527d26f";
    const axisRangeDataType = "numeric";
    const result = getUpdatedRangeProperties(
      reportData,
      itemsData,
      groupKey,
      fieldsData,
      fieldId,
      axisRangeDataType
    );
    const expectedResult = mock_data.expectedResult;
    expect(result).toEqual(expectedResult);
  });
});
