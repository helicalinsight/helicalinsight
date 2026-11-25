import moment from "moment";
import { getHcrParameterFilters } from "../../components/hi-canned-reports/hcrHelperMethods";

const hcrDiagramNodesData = [
  { name: "dateRange", backendDataType: "java.sql.Date" },
  { name: "customerName", backendDataType: "java.lang.String" },
];

const createDateParameter = (name, defaultValue) => ({
  id: `${name}-id`,
  name,
  type: "java.sql.Date",
  canvasValues: {
    filterType: "Date",
    defaultValue,
    displayFormat: "YYYY-MM-DD",
    valueFormat: "YYYY-MM-DD",
    isChecked: true,
  },
});

const createRangeParameters = (startValue, endValue) => ({
  menu: [
    {
      id: "range-1",
      name: "dateRange",
      type: "java.sql.Date",
      canvasValues: {
        filterType: "Date Range",
        start: "startdate",
        end: "enddate",
        defaultValue: "",
        displayFormat: "YYYY-MM-DD",
        valueFormat: "YYYY-MM-DD",
        isChecked: true,
        multipleType: false,
      },
    },
    createDateParameter("startdate", startValue),
    createDateParameter("enddate", endValue),
  ],
});

const getRangeFilter = (parameters) =>
  getHcrParameterFilters({ parameters, hcrDiagramNodesData }).find(
    (filter) => filter.label === "dateRange"
  );

describe("getHcrParameterFilters relative date range handling", () => {
  const currentYear = moment().year();

  test.each([
    {
      name: "custom next 2 years",
      startValue: "year+1",
      endValue: "year+2",
      expected: [`${currentYear}-01-01`, `${currentYear + 1}-12-31`],
    },
    {
      name: "custom previous 2 years",
      startValue: "year-2",
      endValue: "year-1",
      expected: [`${currentYear - 1}-01-01`, `${currentYear}-12-31`],
    },
    {
      name: "preset next year",
      startValue: "year+1",
      endValue: "year+1",
      expected: [`${currentYear + 1}-01-01`, `${currentYear + 1}-12-31`],
    },
    {
      name: "preset previous year",
      startValue: "year-1",
      endValue: "year-1",
      expected: [`${currentYear - 1}-01-01`, `${currentYear - 1}-12-31`],
    },
  ])("recalculates $name IN_RANGE values from the relative anchor", ({ startValue, endValue, expected }) => {
    const result = getRangeFilter(createRangeParameters(startValue, endValue));

    expect(result.condition).toBe("IN_RANGE");
    expect(result.values).toEqual(expected);
  });

  test("keeps absolute IN_RANGE values unchanged", () => {
    const result = getRangeFilter(
      createRangeParameters("2020-01-01", "2020-12-31")
    );

    expect(result.values).toEqual(["2020-01-01", "2020-12-31"]);
  });

  test("does not modify non-range filters", () => {
    const parameters = {
      menu: [
        {
          id: "customer-name-id",
          name: "customerName",
          type: "java.lang.String",
          canvasValues: {
            filterType: "Input",
            defaultValue: "Acme",
            isChecked: true,
          },
        },
      ],
    };

    const [result] = getHcrParameterFilters({ parameters, hcrDiagramNodesData });

    expect(result.condition).toBe("CONTAINS");
    expect(result.values).toEqual(["Acme"]);
  });
});
