import { configureStore } from "@reduxjs/toolkit";
import { render, screen, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../../../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { hiMockAxios } from "../../../../../../../app/mock-axios";
import { relative_list, props } from "./mocks/relative-list.mocks";
import RelativeList from "../../../../../../../components/hi-reports/hi-editing-area/components/filters/relative-list";
import { changeEqualDateTime, changeRangeDateTime, checkIsObject, checkIsValidDateFilter, checkRelativeDateFilter, compareObjects, dateTypes, getDateFromValue, getRelativeAnchor, getRelativeDateRange, matchDateStr, prepareRelativeOptionFromAnchor, updateFilterAnchorDate, updateRelativeDateAnchor } from "../../../../../../../utils/filter-utils";
import { relativeDateFilters } from "./mocks/relative-date-filters";
import moment from "moment";
import { relativeDateFilter } from "./relativeList.mocks";
import { checkForAnchorRelativeParameters, parseExpression } from "../../../../../../../utils/utilities";

const App = () => {
  const store = configureStore({
    reducer: reducers,
    middleware: (getDefaultMiddleware) =>
      getDefaultMiddleware({
        thunk: {
          extraArgument: hiMockAxios,
        },
        immutableCheck: false,
        serializableCheck: false,
      }),
    preloadedState: { hreport: relative_list },
  });
  let handleOnChange = jest.fn();
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <RelativeList {...props} onChange={handleOnChange} />
      </Provider>
    </DndProvider>
  );
};

describe("Rendering RelativeList", () => {

  test("RelativeList component", async () => {
    await waitFor(() => render(<App relative_list={relative_list} />));

    const drawer = screen.queryByTestId(/hi-report-relativelist-drawer/i);

    expect(drawer).toBeFalsy();
    expect(screen.queryByTestId(/hi-report-relativelist-drawer-title/i)).toBeFalsy();
  });

  test("test check relative date function", () => {
    let format = "YYYY-MM-DD";
    const expectedTodayResult = [moment().startOf('day').format(format)];
    const expectedThisMonthResult = [moment().startOf('day').format(format), moment().add(-1, 'days').format(format)]
    const expectednextMonthResult = [moment().startOf('month').format(format), moment().add(-1, 'month').endOf('month').format(format)];
    const expectedValues = [expectedTodayResult, expectedThisMonthResult, expectednextMonthResult];
    const filters = checkRelativeDateFilter(relativeDateFilters);
    const values = filters?.map((item) => item?.values);
    expect(values).toEqual(expectedValues);
  })
  test("test check relative date function when parameters are there", () => {
    let format = "YYYY-MM-DD";
    const expectedTodayResult = [moment().startOf('day').format(format)];
    const expectedThisMonthResult = [moment().startOf('day').format(format), moment().add(-1, 'days').format(format)]
    const expectednextMonthResult = [moment().startOf('month').format(format), moment().add(-1, 'month').endOf('month').format(format)];
    const expectedValues = [expectedTodayResult, expectedThisMonthResult, expectednextMonthResult];
    const filters = checkRelativeDateFilter(relativeDateFilters, {});
    const values = filters?.map((item) => item?.values);
    expect(values).toEqual(expectedValues);
  })
});

describe('to test getDateFromValue', () => {
  it('should return today date', () => {
    expect(getDateFromValue('today', relativeDateFilters[0], 0)).toMatch(/\d{4}-\d{2}-\d{2}/);
  });

  it('should return tomorrow date', () => {
    expect(getDateFromValue('tomorrow', relativeDateFilters[1], 0)).toMatch(/\d{4}-\d{2}-\d{2}/);
  });

  it('should return date from yesterday', () => {
    expect(getDateFromValue('yesterday', relativeDateFilters[2], 0)).toMatch(/\d{4}-\d{2}-\d{2}/);
  });

  it('should return date from month', () => {
    expect(getDateFromValue('month', relativeDateFilters[0], 0)).toMatch(/\d{4}-\d{2}-\d{2}/);
  });

  it('should return date from year', () => {
    expect(getDateFromValue('year', relativeDateFilters[1], 0)).toMatch(/\d{4}-\d{2}-\d{2}/);
  });

  it('should return date from quarter', () => {
    expect(getDateFromValue('quarter', relativeDateFilters[2], 0)).toMatch(/\d{4}-\d{2}-\d{2}/);
  });

  it('should return date from hour', () => {
    expect(getDateFromValue('hour', relativeDateFilters[0], 0)).toMatch(/\d{4}-\d{2}-\d{2}/);
  });

  it('should return date from minute', () => {
    expect(getDateFromValue('minute', relativeDateFilters[1], 0)).toMatch(/\d{4}-\d{2}-\d{2}/);
  });

  it('should return date from second', () => {
    expect(getDateFromValue('second', relativeDateFilters[2], 0)).toMatch(/\d{4}-\d{2}-\d{2}/);
  });

  it('should return date with numeric modifier', () => {
    expect(getDateFromValue('today+3', relativeDateFilters[0], 0)).toMatch(/\d{4}-\d{2}-\d{2}/);
  });

  it('should return null for unrecognized base value', () => {
    expect(getDateFromValue('invalid', relativeDateFilters[0], 0)).toEqual("invalid");
  });

  it('should return null for invalid modifier', () => {
    expect(getDateFromValue('today+abc', relativeDateFilters[0], 0)).toEqual('today+abc');
  });

  it('should return date with filter and index', () => {
    expect(getDateFromValue('today', relativeDateFilters[0], 2)).toMatch(/\d{4}-\d{2}-\d{2}/);
  });

  it('should return date with different data types in the filter', () => {
    expect(getDateFromValue('today', relativeDateFilters[0], 0)).toMatch(/\d{4}-\d{2}-\d{2}/);
  });

  it('should return value when value is not a string', () => {
    expect(getDateFromValue(2020, relativeDateFilters[0], 0)).toEqual(2020);
  });

  it('should return anchor date parts when returnRelativeDateParts is true', () => {
    const expectedValue = {
      value: 1,
      part: 'months',
      direction: 'last',
      relativePart: 'month',
      anchorDate: "2023-08-08 15:31:59",
      isAnchor: false
    }
    expect(getDateFromValue('month-1', relativeDateFilters[0], 0, true)).toEqual(expectedValue);
  });
});

describe('to test prepareRelativeOptionFromAnchor', () => {
  const prepare = (anchor, options = {}) =>
    prepareRelativeOptionFromAnchor({ anchor, ...options });

  test('returns undefined when no data is provided', () => {
    expect(prepareRelativeOptionFromAnchor()).toBeUndefined();
  });

  describe('non-range expressions', () => {
    test('uses a negative offset for the last direction', () => {
      expect(prepare({ relativePart: "month", value: 5, direction: "last" })).toBe("month-5");
    });

    test('uses a positive offset for the next direction', () => {
      expect(prepare({ relativePart: "week", value: 3, direction: "next" })).toBe("week+3");
    });

    test('maps the day relative part to today', () => {
      expect(prepare({ relativePart: "day" })).toBe("today");
    });

    test('preserves the actual value when relative date information is missing', () => {
      expect(
        prepare(
          { relativePart: "", value: 2, direction: "last" },
          { actualValue: "12-12-2020" }
        )
      ).toBe("12-12-2020");
    });
  });

  describe('range expressions', () => {
    test('custom next range starts at the current period', () => {
      const anchor = { relativePart: "year", value: 2, direction: "next", active: 4 };
      expect(prepare(anchor, { isRange: true, index: 1 })).toBe("year");
      expect(prepare(anchor, { isRange: true, index: 2 })).toBe("year+1");
    });

    test('custom last range ends at the current period', () => {
      const anchor = { relativePart: "year", value: 2, direction: "last", active: 5 };
      expect(prepare(anchor, { isRange: true, index: 1 })).toBe("year-1");
      expect(prepare(anchor, { isRange: true, index: 2 })).toBe("year");
    });

    test('custom next range uses the full offset when factor is zero', () => {
      const anchor = { relativePart: "year", value: 2, direction: "next", active: 4 };
      expect(prepare(anchor, { isRange: true, index: 1, factor: 0 })).toBe("year");
      expect(prepare(anchor, { isRange: true, index: 2, factor: 0 })).toBe("year+2");
    });

    test('custom last range uses the full offset when factor is zero', () => {
      const anchor = { relativePart: "year", value: 2, direction: "last", active: 5 };
      expect(prepare(anchor, { isRange: true, index: 1, factor: 0 })).toBe("year-2");
      expect(prepare(anchor, { isRange: true, index: 2, factor: 0 })).toBe("year");
    });

    test('single custom next range selects the current period', () => {
      const anchor = { relativePart: "year", value: 1, direction: "next", active: 4 };
      expect(prepare(anchor, { isRange: true, index: 1 })).toBe("year");
      expect(prepare(anchor, { isRange: true, index: 2 })).toBe("year");
    });

    test('single custom last range selects the current period', () => {
      const anchor = { relativePart: "year", value: 1, direction: "last", active: 5 };
      expect(prepare(anchor, { isRange: true, index: 1 })).toBe("year");
      expect(prepare(anchor, { isRange: true, index: 2 })).toBe("year");
    });

    test('preset next range selects the adjacent period', () => {
      const anchor = { relativePart: "year", value: 1, direction: "next", active: 2 };
      expect(prepare(anchor, { isRange: true, index: 1 })).toBe("year+1");
      expect(prepare(anchor, { isRange: true, index: 2 })).toBe("year+1");
    });

    test('preset last range selects the adjacent period', () => {
      const anchor = { relativePart: "year", value: 1, direction: "last", active: 3 };
      expect(prepare(anchor, { isRange: true, index: 1 })).toBe("year-1");
      expect(prepare(anchor, { isRange: true, index: 2 })).toBe("year-1");
    });

    test('this period returns the current period for both bounds', () => {
      const anchor = { relativePart: "year", value: 0 };
      expect(prepare(anchor, { isRange: true, index: 1 })).toBe("year");
      expect(prepare(anchor, { isRange: true, index: 2 })).toBe("year");
    });

    test.each([
      ["month", "month", "month+1"],
      ["week", "week", "week+1"],
      ["quarter", "quarter", "quarter+1"],
    ])("custom next %s range includes the current period", (relativePart, start, end) => {
      const anchor = { relativePart, value: 2, direction: "next", active: 4 };
      expect(prepare(anchor, { isRange: true, index: 1 })).toBe(start);
      expect(prepare(anchor, { isRange: true, index: 2 })).toBe(end);
    });

    test.each([
      ["month", "month-1", "month"],
      ["week", "week-1", "week"],
      ["quarter", "quarter-1", "quarter"],
    ])("custom last %s range includes the current period", (relativePart, start, end) => {
      const anchor = { relativePart, value: 2, direction: "last", active: 5 };
      expect(prepare(anchor, { isRange: true, index: 1 })).toBe(start);
      expect(prepare(anchor, { isRange: true, index: 2 })).toBe(end);
    });

    test('custom day range uses today with inclusive bounds', () => {
      const anchor = { relativePart: "day", value: 2, direction: "next", active: 4 };
      expect(prepare(anchor, { isRange: true, index: 1 })).toBe("today");
      expect(prepare(anchor, { isRange: true, index: 2 })).toBe("today+1");
    });

    test('returns relative part and to_date for year to date', () => {
      const anchor = { relativePart: "month", value: "toDate", direction: "last" };
      expect(prepare(anchor, { isRange: true, index: 1 })).toBe("month");
      expect(prepare(anchor, { isRange: true, index: 2 })).toBe("to_date");
    });

    test('applies the anchor prefix to custom range bounds', () => {
      const nextAnchor = { relativePart: "year", value: 2, direction: "next", active: 4, isAnchor: true };
      const lastAnchor = { relativePart: "year", value: 2, direction: "last", active: 5, isAnchor: true };
      expect(prepare(nextAnchor, { isRange: true, index: 1 })).toBe("anchor_year");
      expect(prepare(nextAnchor, { isRange: true, index: 2 })).toBe("anchor_year+1");
      expect(prepare(lastAnchor, { isRange: true, index: 1 })).toBe("anchor_year-1");
      expect(prepare(lastAnchor, { isRange: true, index: 2 })).toBe("anchor_year");
    });
  });
})


describe('test changeEqualDateTime', () => {
  it('should format datetime to date and time', () => {
    const dateTime = moment('2022-01-01T12:00:00');
    const result = changeEqualDateTime(dateTime, dateTypes.INDIVIDUAL, dateTypes.DATETIME);
    expect(result).toEqual('2022-01-01 12:00:00.0');
  });

  it('should format datetime to date', () => {
    const dateTime = moment('2022-01-01T12:00:00');
    const result = changeEqualDateTime(dateTime, dateTypes.INDIVIDUAL, dateTypes.DATE);
    expect(result).toEqual('2022-01-01');
  });

  it('should format datetime to time', () => {
    const dateTime = moment('2022-01-01T12:00:00');
    const result = changeEqualDateTime(dateTime, dateTypes.TIME);
    expect(result).toEqual('12:00:00');
  });

  it('should format datetime to year', () => {
    const dateTime = moment('2022-01-01T12:00:00');
    const result = changeEqualDateTime(dateTime, dateTypes.YEAR);
    expect(result).toEqual(2022);
  });

  it('should format datetime to quarter', () => {
    const dateTime = moment('2022-01-01T12:00:00');
    const result = changeEqualDateTime(dateTime, dateTypes.QUARTER);
    expect(result).toEqual(1);
  });

  it('should format datetime to month name', () => {
    const dateTime = moment('2022-01-01T12:00:00');
    const result = changeEqualDateTime(dateTime, dateTypes.MONTHNAME);
    expect(result).toEqual('January');
  });

  it('should format datetime to month', () => {
    const dateTime = moment('2022-01-01T12:00:00');
    const result = changeEqualDateTime(dateTime, dateTypes.MONTH);
    expect(result).toEqual(1);
  });

  it('should format datetime to day', () => {
    const dateTime = moment('2022-01-01T12:00:00');
    const result = changeEqualDateTime(dateTime, dateTypes.DAY);
    expect(result).toEqual(1);
  });

  it('should format datetime to hour', () => {
    const dateTime = moment('2022-01-01T12:00:00');
    const result = changeEqualDateTime(dateTime, dateTypes.HOUR);
    expect(result).toEqual(12);
  });

  it('should format datetime to minute', () => {
    const dateTime = new Date('2022-01-01T12:30:00');
    const result = changeEqualDateTime(dateTime, dateTypes.MINUTE);
    expect(result).toEqual(30);
  });

  it('should format datetime to second', () => {
    const dateTime = new Date('2022-01-01T12:00:45');
    const result = changeEqualDateTime(dateTime, dateTypes.SECOND);
    expect(result).toEqual(45);
  });
});

describe('test changeRangeDateTime', () => {
  test('when changeRangeDateTime is INDIVIDUAL and DATETIME', () => {
    const startDate = moment('2022-01-01T12:00:00');
    const endDate = moment('2022-01-02T12:00:00');
    const filter = { datePart: dateTypes.INDIVIDUAL, dataType: dateTypes.DATETIME };
    expect(changeRangeDateTime(startDate, endDate, filter)).toEqual([
      '2022-01-01 12:00:00.0',
      '2022-01-02 12:00:00.0'
    ]);
  });

  test('when changeRangeDateTime is DATE', () => {
    const startDate = moment('2022-01-01T12:00:00');
    const endDate = moment('2022-01-02T12:00:00');
    const filter = { datePart: dateTypes.DATE, dataType: null };
    expect(changeRangeDateTime(startDate, endDate, filter)).toEqual([
      '2022-01-01',
      '2022-01-02'
    ]);
  });

  test('when changeRangeDateTime is TIME', () => {
    const startDate = moment('2022-01-01T12:00:00');
    const endDate = moment('2022-01-02T12:00:00');
    const filter = { datePart: dateTypes.TIME, dataType: null };
    expect(changeRangeDateTime(startDate, endDate, filter)).toEqual([
      '12:00:00',
      '12:00:00'
    ]);
  });

  test('when changeRangeDateTime - other dateTypes', () => {
    const startDate = moment('2022-01-01T12:00:00')
    const endDate = moment('2022-01-02T12:00:00')
    let filter = { datePart: dateTypes.YEAR }
    expect(changeRangeDateTime(startDate, endDate, filter)).toEqual([2022, 2022])

    filter = { datePart: dateTypes.QUARTER }
    expect(changeRangeDateTime(startDate, endDate, filter)).toEqual([1, 1])

    filter = { datePart: dateTypes.MONTH }
    expect(changeRangeDateTime(startDate, endDate, filter)).toEqual([1, 1])

    filter = { datePart: dateTypes.DAY }
    expect(changeRangeDateTime(startDate, endDate, filter)).toEqual([1, 2])

    filter = { datePart: dateTypes.HOUR }
    expect(changeRangeDateTime(startDate, endDate, filter)).toEqual([12, 12])

    filter = { datePart: dateTypes.MINUTE }
    expect(changeRangeDateTime(startDate, endDate, filter)).toEqual([0, 0])

    filter = { datePart: dateTypes.SECOND }
    expect(changeRangeDateTime(startDate, endDate, filter)).toEqual([0, 0])
  })

})

describe('test checkIsValidDateFilter', () => {
  it('should return false when filter is null or undefined', () => {
    expect(checkIsValidDateFilter(null)).toBe(false);
    expect(checkIsValidDateFilter(undefined)).toBe(false);
  });

  it('should return false when filter has an invalid dataType and datePart is undefined', () => {
    const filter = { dataType: 'invalidType' };
    expect(checkIsValidDateFilter(filter)).toBe(undefined);
  });

  it('should return true when filter has a valid dataType', () => {
    const filter = { dataType: 'dateTime' };
    expect(checkIsValidDateFilter(filter)).toBe(true);
  });

  it('should return true when filter has a datePart property', () => {
    const filter = { datePart: true };
    expect(checkIsValidDateFilter(filter)).toBe(true);
  });
});

describe('test matchDateStr', () => {
  test('matches "today"', () => {
    expect(matchDateStr('today')).toBeTruthy();
  });

  test('matches "tomorrow"', () => {
    expect(matchDateStr('tomorrow')).toBeTruthy();
  });

  test('matches "yesterday"', () => {
    expect(matchDateStr('yesterday')).toBeTruthy();
  });

  test('matches "month" with offset', () => {
    expect(matchDateStr('month+1')).toBeTruthy();
  });

  test('does not match invalid input', () => {
    expect(matchDateStr('invalid input')).toBeFalsy();
  });

  test('Empty input string should return false', () => {
    expect(matchDateStr('')).toBe(false);
  });

  test('Non-string input should return false', () => {
    expect(matchDateStr(123)).toBe(false);
  });

  test('Valid date strings should return a match', () => {
    expect(matchDateStr('month')).not.toBeNull();
    expect(matchDateStr('year')).not.toBeNull();
    expect(matchDateStr('quarter')).not.toBeNull();
    expect(matchDateStr('hour')).not.toBeNull();
    expect(matchDateStr('minute')).not.toBeNull();
    expect(matchDateStr('second')).not.toBeNull();
    expect(matchDateStr('to_date')).not.toBeNull();
  });
})

describe('test checkIsObject', () => {
  test('Check if input is an object', () => {
    expect(checkIsObject({})).toBe(true);
    expect(checkIsObject([])).toBe(true);
    expect(checkIsObject(new Date())).toBe(true);
  });

  test('Check if input is not an object', () => {
    expect(checkIsObject(null)).toBe(false);
    expect(checkIsObject('string')).toBe(false);
    expect(checkIsObject(123)).toBe(false);
  });
})

const assert = require('assert');

describe('compareObjects', () => {
  it('should return obj1 when obj1 value is equal to obj2 value', () => {
    const obj1 = { value: 5 };
    const obj2 = { value: 5 };
    expect(compareObjects(obj1, obj2)).toEqual(obj1);
  });

  it('should return obj1 when obj1 value is greater than obj2 value', () => {
    const obj1 = { value: 10 };
    const obj2 = { value: 5 };
    expect(compareObjects(obj1, obj2)).toEqual(obj1);
  });

  it('should return obj2 when obj2 value is greater than obj1 value', () => {
    const obj1 = { value: 5 };
    const obj2 = { value: 10 };
    expect(compareObjects(obj1, obj2)).toEqual(obj2);
  });
});

describe("getRelativeAnchor", () => {
  it("should return the same string when returnObj is a string", () => {
    const returnObj = "example";
    expect(getRelativeAnchor(returnObj)).toBe(returnObj);
  });

  it("should return an object with nextInput, lastInput, and active properties when value is greater than 1", () => {
    const returnObj = { value: 2, part: "example", direction: "next", relativePart: "example" };
    const expectedResult = { value: 2, part: "example", direction: "next", relativePart: "example", nextInput: 2, lastInput: 3, active: 4 };
    expect(getRelativeAnchor(returnObj)).toEqual(expectedResult);
  });

  it("should have active property 5 when value is greater than 1 and direction is 'last'", () => {
    const returnObj = { value: 2, part: "example", direction: "last", relativePart: "example" };
    const expectedResult = { value: 2, part: "example", direction: "last", relativePart: "example", nextInput: 3, lastInput: 2, active: 5 };
    expect(getRelativeAnchor(returnObj)).toEqual(expectedResult);
  });

  it("should have active property 4 when value is greater than 1 and direction is 'next'", () => {
    const returnObj = { value: 2, part: "example", direction: "next", relativePart: "example" };
    const expectedResult = { value: 2, part: "example", direction: "next", relativePart: "example", nextInput: 2, lastInput: 3, active: 4 };
    expect(getRelativeAnchor(returnObj)).toEqual(expectedResult);
  });

  it("should return an object with active property when value is 1", () => {
    const returnObj = { value: 1, part: "example", direction: "next", relativePart: "example" };
    const expectedResult = { value: 1, part: "example", direction: "next", relativePart: "example", active: 2 };
    expect(getRelativeAnchor(returnObj)).toEqual(expectedResult);
  });

  it("should have active property 3 when value is 1 and direction is 'last'", () => {
    const returnObj = { value: 1, part: "example", direction: "last", relativePart: "example" };
    const expectedResult = { value: 1, part: "example", direction: "last", relativePart: "example", active: 3 };
    expect(getRelativeAnchor(returnObj)).toEqual(expectedResult);
  });

  it("should have active property 2 when value is 1 and direction is 'next'", () => {
    const returnObj = { value: 1, part: "example", direction: "next", relativePart: "example" };
    const expectedResult = { value: 1, part: "example", direction: "next", relativePart: "example", active: 2 };
    expect(getRelativeAnchor(returnObj)).toEqual(expectedResult);
  });
})

describe('test updateRelativeDateAnchor', () => {
  const createRangeFilter = () => ({
    label: "travel_date",
    dataType: "dateTime",
    condition: "IN_RANGE",
    datePart: "individual",
    values: ["year-1", "year+2"],
    anchor: {},
  });
  const rangeParameters = { travel_date: ["year-1", "year+2"] };

  it('should return correct relative date anchor', () => {
    const filter = updateRelativeDateAnchor(relativeDateFilter, {}, false)
    const { anchor } = filter[0]
    const expectedResult = {
      anchorDate: "2024-03-20 17:19:55",
      isAnchor: false,
      active: 2,
      relativePart: "month",
      value: 1,
      direction: "next",
      lastInput: 3,
      nextInput: 3,
      part: "months"
    }
    expect(expectedResult).toEqual(anchor)
  })

  it('should store temporary anchors for each range endpoint when addTempAnchors is true', () => {
    const filter = updateRelativeDateAnchor(createRangeFilter(), rangeParameters, true, true)

    expect(filter.relativeDateAnchors).toHaveLength(2)
    expect(filter.relativeDateAnchors[0]).toMatchObject({
      relativePart: "year",
      part: "years",
      value: 1,
      direction: "last",
    })
    expect(filter.relativeDateAnchors[1]).toMatchObject({
      relativePart: "year",
      part: "years",
      value: 2,
      direction: "next",
    })
  })

  it('should not store temporary anchors by default', () => {
    const filter = updateRelativeDateAnchor(createRangeFilter(), rangeParameters, true)

    expect(filter).not.toHaveProperty("relativeDateAnchors")
  })
})

describe('test checkRelativeDateFilter addTempAnchors flag', () => {
  const createRangeFilter = () => ({
    label: "travel_date",
    dataType: "dateTime",
    condition: "IN_RANGE",
    datePart: "individual",
    values: ["year-1", "year+2"],
    anchor: {},
  });
  const rangeParameters = { travel_date: ["year-1", "year+2"] };

  it('should add temporary range anchors when requested', () => {
    const filters = checkRelativeDateFilter([createRangeFilter()], rangeParameters, true)

    expect(filters[0].relativeDateAnchors).toHaveLength(2)
  })

  it('should not add temporary range anchors by default', () => {
    const filters = checkRelativeDateFilter([createRangeFilter()], rangeParameters)

    expect(filters[0]).not.toHaveProperty("relativeDateAnchors")
  })
})

describe('test updateFilterAnchorDate', () => {
  test('Update anchor date if parameter exists for filter name', () => {
    const anchor = { anchorDate: '2022-01-01' };
    const parameters = { __anchor__testFilter: '2022-02-02' };
    const filterName = 'testFilter';

    const updatedAnchor = updateFilterAnchorDate(anchor, parameters, filterName);

    expect(updatedAnchor.anchorDate).toBe('2022-02-02');
  });
  test('Keep anchor date unchanged if parameter does not exist for filter name', () => {
    const anchor = { anchorDate: '2022-01-01' };
    const parameters = { otherFilter: '2022-02-02' };
    const filterName = 'testFilter';

    const updatedAnchor = updateFilterAnchorDate(anchor, parameters, filterName);

    expect(updatedAnchor.anchorDate).toBe('2022-01-01');
  });
})

describe(('test function checkForAnchorRelativeParameters'), () => {
  test('Testing when parameter is undefined', () => {
    const parameters = undefined;
    const result = checkForAnchorRelativeParameters(parameters);
    expect(result).toEqual(parameters);
  })

  test('Testing empty parameter', () => {
    const parameters = {};
    const result = checkForAnchorRelativeParameters(parameters);
    expect(result).toEqual(parameters);
  })

  test('Testing parameter with no anchor value', () => {
    const parameters = {
      param1: ['value1'],
      param2: ['value2'],
    };
    const result = checkForAnchorRelativeParameters(parameters);
    expect(result).toEqual(parameters);
  });

  test('Testing parameter with a single anchor value', () => {
    const parameters = {
      param1: ['value1', '__anchor__2015-02-02'],
      param2: ['value2'],
    };
    const result = checkForAnchorRelativeParameters(parameters);
    expect(result).toEqual({
      param1: ['value1'],
      __anchor__param1: ['2015-02-02'],
      param2: ['value2'],
    });
  });

  test('Testing parameter with multiple anchor values', () => {
    const parameters = {
      param1: ['value1', '__anchor__2015-02-02', '__anchor__2015-02-01'],
      param2: ['value2'],
    };
    const result = checkForAnchorRelativeParameters(parameters);
    expect(result).toEqual({
      param1: ['value1'],
      __anchor__param1: ['2015-02-02'],
      param2: ['value2'],
    });
  });


  test('Testing parameter with anchor date in object', () => {
    const parameters = {
      param1: { anchor_function: 'anchor_year', anchor_value: '2015-02-02' },
      param2: ['value2'],
    };
    const result = checkForAnchorRelativeParameters(parameters);
    expect(result).toEqual({
      param1: ['anchor_year'],
      __anchor__param1: ['2015-02-02'],
      param2: ['value2'],
    });
  });

  test('Testing parameter with anchor date in object when anchor in anchor_function is not present', () => {
    const parameters = {
      param1: { anchor_function: 'year', anchor_value: '2015-02-02' },
      param2: ['value2'],
    };
    const result = checkForAnchorRelativeParameters(parameters);
    expect(result).toEqual({
      param1: ['anchor_year'],
      __anchor__param1: ['2015-02-02'],
      param2: ['value2'],
    });
  });

  test('Testing parameter with anchor date in single value', () => {
    const parameters = {
      param1: ['__a_year+1_2021-01-01'],
      param2: ['value2'],
    };
    const result = checkForAnchorRelativeParameters(parameters);
    expect(result).toEqual({
      param1: ['anchor_year+1'],
      __anchor__param1: ['2021-01-01'],
      param2: ['value2'],
    });
  });

  test('Testing parameter with number in array', () => {
    const parameters = {
      param1: [1, 2],
    };
    const result = checkForAnchorRelativeParameters(parameters);
    expect(result).toEqual(parameters);
  });
})


describe('test parseExpression', () => {
  test('parseExpression returns the input value if it is not a string', () => {
    expect(parseExpression(123)).toBe(123);
  });

  test('parseExpression returns the input value if it does not match the expected pattern', () => {
    expect(parseExpression('not a match')).toBe('not a match');
  });

  test('parseExpression returns the correct object when the input value matches the expected pattern', () => {
    expect(parseExpression('__a_year+1_2021-01-01')).toEqual({
      anchor: '__a',
      base: 'year',
      modifier: '+1',
      anchorDate: '2021-01-01'
    });
  });
  test('parseExpression returns the correct object when the single input value matches the expected pattern', () => {
    expect(parseExpression('__a_year+1_05')).toEqual({
      anchor: '__a',
      base: 'year',
      modifier: '+1',
      anchorDate: '05'
    });
  });
  test('parseExpression returns the correct object when the input value with time,  matches the expected pattern', () => {
    expect(parseExpression('__a_second+1_2022-01-01 10:10:10')).toEqual({ anchor: '__a', base: 'second', modifier: '+1', anchorDate: '2022-01-01 10:10:10' });
  })
})

describe('test getRelativeDateRange', () => {
  const dateNow = moment('2026-11-05')
  const format = 'YYYY-MM-DD'
  const getRange = (value, direction, active, part = 'years', relativePart = 'year') => getRelativeDateRange(dateNow, value, part, direction, format, relativePart, active).map(date => date.format(format))

  test('next N years includes the current year', () => {
    expect(getRange(2, 'next')).toEqual(['2026-01-01', '2027-12-31'])
    expect(getRange(3, 'next')).toEqual(['2026-01-01', '2028-12-31'])
  })

  test('previous N years includes the current year', () => {
    expect(getRange(2, 'last')).toEqual(['2025-01-01', '2026-12-31'])
    expect(getRange(3, 'last')).toEqual(['2024-01-01', '2026-12-31'])
  })

  test('single next and previous preset period selects the adjacent period', () => {
    expect(getRange(1, 'next', 2)).toEqual(['2027-01-01', '2027-12-31'])
    expect(getRange(1, 'last', 3)).toEqual(['2025-01-01', '2025-12-31'])
  })

  test('single next and previous custom period selects the current period', () => {
    expect(getRange(1, 'next', 4)).toEqual(['2026-01-01', '2026-12-31'])
    expect(getRange(1, 'last', 5)).toEqual(['2026-01-01', '2026-12-31'])
  })

  test('single next and previous preset month, week and quarter select the adjacent period', () => {
    expect(getRange(1, 'next', 2, 'months', 'month')).toEqual(['2026-12-01', '2026-12-31'])
    expect(getRange(1, 'last', 3, 'months', 'month')).toEqual(['2026-10-01', '2026-10-31'])
    expect(getRange(1, 'next', 2, 'weeks', 'week')).toEqual(['2026-11-08', '2026-11-14'])
    expect(getRange(1, 'last', 3, 'weeks', 'week')).toEqual(['2026-10-25', '2026-10-31'])
    expect(getRange(1, 'next', 2, 'quarters', 'quarter')).toEqual(['2027-01-01', '2027-03-31'])
    expect(getRange(1, 'last', 3, 'quarters', 'quarter')).toEqual(['2026-07-01', '2026-09-30'])
  })

  test('year to date ends at the anchor date', () => {
    expect(getRange('toDate', 'last')).toEqual(['2026-01-01', '2026-11-05'])
  })
})