import  { getUpdatedProperties }  from "../../../../../../components/hi-reports/hi-editing-area/utils/property-utils"
import { mock_data } from "./getUpdatedProperties.mock.data";

describe("getUpdatedProperties function", () => {
    test("to check the functionality of getUpdatedProperties function", () => {
      const itemsData = mock_data.itemsData;
      const groupKey = mock_data.groupKey;
     const initialReport = mock_data.initialReport;
      const result =  getUpdatedProperties (itemsData, groupKey, initialReport);
      const expectedResult = mock_data.expectedResult;
      
      expect(result).toEqual(expectedResult);
    });
  });