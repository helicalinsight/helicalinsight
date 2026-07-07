import { getGridChartColorFormatScheme } from "../../../../../../components/hi-reports/hi-editing-area/utils/property-utils";
import { mock_data } from "./getGridChartColorFormatScheme.mock.data";
describe("getGridChartColorFormatScheme function", () => {
  test("to check the functionality of getGridChartColorFormatScheme function", () => {
    const formatColor = mock_data.formatColor_1;
    const domainData = mock_data.domainData_1;
    const combine = false;
    const result = getGridChartColorFormatScheme(
      formatColor,
      domainData,
      combine
    );
    const expectedResult = mock_data.expectedResult_1;

    expect(result).toEqual(expectedResult);
  });

  test("to check the functionality of getGridChartColorFormatScheme function with different domain data", () => {
    const formatColor =mock_data.formatColor_2;
    const domainData = mock_data.domainData_2;
    const combine = false;
    const result = getGridChartColorFormatScheme(
      formatColor,
      domainData,
      combine
    );
    const expectedResult =mock_data.expectedResult_2;

    expect(result).toEqual(expectedResult);
  });


  test("too check the functionality of getGridChartColorFormatScheme function with combine set to true", () => {
    const formatColor = mock_data.formatColor_3;
    const domainData = mock_data.domainData_3;
    const combine = true;
    const result = getGridChartColorFormatScheme(
      formatColor,
      domainData,
      combine
    );
    const expectedResult = mock_data.expectedResult_3;

    expect(result).toEqual(expectedResult);
  });
  test("to check the functionality of getGridChartColorFormatScheme function with empty data", () => {
    const formatColor = {};
    const domainData = [];
    const combine = false;
    const result = getGridChartColorFormatScheme(
      formatColor,
      domainData,
      combine
    );
    const expectedResult = []

    expect(result).toEqual(expectedResult);
  });
});
