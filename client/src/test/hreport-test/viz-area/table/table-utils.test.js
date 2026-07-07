import { checkIsNumeric } from "../../../../components/hi-reports/hi-viz-area/table/table-utils"
import { modifyTableProperties } from "../../../../utils/utilities";
import { expectedPropertiesData, tableProperitesMockData } from "./table-mock-data";

describe('checkIsNumeric', () => {
  const columnList = {
    "1": {
      "name": "count_booking_platform",
      "type": "numeric"
    },
    "2": {
      "name": "sum_travel_cost",
      "type": "numeric"
    },
    "3": {
      "name": "source",
      "type": "text"
    }
  };

  test('should return true when fieldName is numeric', () => {
    const fieldName = 'count_booking_platform';
    const isNumeric = checkIsNumeric(columnList, fieldName);
    expect(isNumeric).toBe(true);
  });

  test('should return false when fieldName is not numeric', () => {
    const fieldName = 'source';
    const isNumeric = checkIsNumeric(columnList, fieldName);
    expect(isNumeric).toBe(false);
  });

  test('should return false when fieldName does not exist in columnList', () => {
    const fieldName = 'invalid_field';
    const isNumeric = checkIsNumeric(columnList, fieldName);
    expect(isNumeric).toBe(false);
  });

  test('should return false when columnList is empty', () => {
    const fieldName = 'count_booking_platform';
    const emptyColumnList = {};
    const isNumeric = checkIsNumeric(emptyColumnList, fieldName);
    expect(isNumeric).toBe(false);
  });

  test('should return false when columnList is not an object', () => {
    const fieldName = 'count_booking_platform';
    const invalidColumnList = [];
    const isNumeric = checkIsNumeric(invalidColumnList, fieldName);
    expect(isNumeric).toBe(false);
  });

  test('should return the state with changed table properties', () => {
    const numberOfRepords = 20;
    const result = modifyTableProperties(tableProperitesMockData,numberOfRepords)
    expect(result).toEqual(expectedPropertiesData)
  })
});
