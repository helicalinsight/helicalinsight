import { updateHCRRelativeDateFilters } from "../../components/hi-canned-reports/hcrHelperMethods";

const createAnchor = (overrides = {}) => ({
  relativePart: "year",
  part: "years",
  value: 2,
  direction: "next",
  active: 4,
  ...overrides,
});

const createFilters = (anchor) => [
  {
    uid: "range-1",
    label: "dateRange",
    condition: "IN_RANGE",
    dataType: "date",
    datePart: "individual",
    values: ["2026-01-01", "2027-12-31"],
    range: ["startdate", "enddate"],
    anchor,
  },
  {
    uid: "start-1",
    label: "startdate",
    condition: "EQUALS",
    dataType: "date",
    datePart: "individual",
    values: ["2026-01-01"],
  },
  {
    uid: "end-1",
    label: "enddate",
    condition: "EQUALS",
    dataType: "date",
    datePart: "individual",
    values: ["2027-12-31"],
  },
];

const getFilter = (filters, label) =>
  filters.find((filter) => filter.label === label);

describe("updateHCRRelativeDateFilters URL parameter handling", () => {
  test("uses inclusive custom next range bounds without URL parameters", () => {
    const updated = updateHCRRelativeDateFilters(createFilters(createAnchor()));

    expect(getFilter(updated, "dateRange").values).toEqual(["year", "year+1"]);
    expect(getFilter(updated, "startdate").values).toEqual(["year"]);
    expect(getFilter(updated, "enddate").values).toEqual(["year+1"]);
  });

  test.each([
    {
      name: "custom next range",
      anchor: createAnchor({ value: 2, direction: "next", active: 4 }),
      urlParameters: { dateRange: ["year+1", "year+2"] },
      expectedRange: ["year", "year+2"],
    },
    {
      name: "custom previous range",
      anchor: createAnchor({ value: 2, direction: "last", active: 5 }),
      urlParameters: { dateRange: ["year-2", "year-1"] },
      expectedRange: ["year-2", "year"],
    },
  ])("uses a zero factor when a $name has relative URL parameters", ({ anchor, urlParameters, expectedRange }) => {
    const updated = updateHCRRelativeDateFilters(
      createFilters(anchor),
      urlParameters
    );

    expect(getFilter(updated, "dateRange").values).toEqual(expectedRange);
    expect(getFilter(updated, "startdate").values).toEqual([expectedRange[0]]);
    expect(getFilter(updated, "enddate").values).toEqual([expectedRange[1]]);
  });

  test.each([
    {
      name: "absolute dates",
      urlParameters: { dateRange: ["2026-01-01", "2027-12-31"] },
    },
    {
      name: "unrelated URL parameters",
      urlParameters: { anotherFilter: ["year+1", "year+2"] },
    },
  ])("keeps the default factor for $name", ({ urlParameters }) => {
    const updated = updateHCRRelativeDateFilters(
      createFilters(createAnchor()),
      urlParameters
    );

    expect(getFilter(updated, "dateRange").values).toEqual(["year", "year+1"]);
  });

  test("detects a relative value in the end URL parameter", () => {
    const updated = updateHCRRelativeDateFilters(createFilters(createAnchor()), {
      dateRange: ["", "year+2"],
    });

    expect(getFilter(updated, "dateRange").values).toEqual(["year", "year+2"]);
  });

  test("does not modify non-date filters", () => {
    const textFilter = {
      uid: "text-1",
      label: "customerName",
      condition: "CONTAINS",
      dataType: "text",
      values: ["Acme"],
    };

    const [result] = updateHCRRelativeDateFilters([textFilter], {
      customerName: ["year+1"],
    });

    expect(result).toBe(textFilter);
  });
});
