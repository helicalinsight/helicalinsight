import {
  getRowFieldLabels,
  listColumns,
} from "../../../../../../components/hi-reports/hi-viz-area/pivot-view/utils/pivot-values";

import createPivotValues from "../../../../../../components/hi-reports/hi-viz-area/pivot-view/utils/pivot-values";

describe("getRowFieldLabels", () => {
  test("returns an object with labels for each row field", () => {
    const data = [
      { name: "Apple", age: 30, city: "New York" },
      { name: "Samsung", age: 25, city: "Chicago" },
      { name: "Oneplus", age: 40, city: "New York" },
      { name: "Moto", age: 35, city: "San Francisco" },
    ];
    const rows = ["name", "city"];
    const expectedOutput = {
      name: ["Apple", "Samsung", "Oneplus", "Moto"],
      city: ["New York", "Chicago", "San Francisco"],
    };
    const result = getRowFieldLabels(data, rows);
    expect(result).toEqual(expectedOutput);
  });
});

describe("listColumns", () => {
  test("returns an object with a list of unique values for each column", () => {
    const data = [
      { name: "Apple", age: 30, city: "New York" },
      { name: "Samsung", age: 25, city: "Chicago" },
      { name: "Oneplus", age: 40, city: "New York" },
      { name: "Moto", age: 35, city: "San Francisco" },
    ];
    const columns = ["name", "age", "city"];
    const expectedOutput = [
      "Apple.25.Chicago",
      "Apple.25.New York",
      "Apple.25.San Francisco",
      "Apple.30.Chicago",
      "Apple.30.New York",
      "Apple.30.San Francisco",
      "Apple.35.Chicago",
      "Apple.35.New York",
      "Apple.35.San Francisco",
      "Apple.40.Chicago",
      "Apple.40.New York",
      "Apple.40.San Francisco",
      "Moto.25.Chicago",
      "Moto.25.New York",
      "Moto.25.San Francisco",
      "Moto.30.Chicago",
      "Moto.30.New York",
      "Moto.30.San Francisco",
      "Moto.35.Chicago",
      "Moto.35.New York",
      "Moto.35.San Francisco",
      "Moto.40.Chicago",
      "Moto.40.New York",
      "Moto.40.San Francisco",
      "Oneplus.25.Chicago",
      "Oneplus.25.New York",
      "Oneplus.25.San Francisco",
      "Oneplus.30.Chicago",
      "Oneplus.30.New York",
      "Oneplus.30.San Francisco",
      "Oneplus.35.Chicago",
      "Oneplus.35.New York",
      "Oneplus.35.San Francisco",
      "Oneplus.40.Chicago",
      "Oneplus.40.New York",
      "Oneplus.40.San Francisco",
      "Samsung.25.Chicago",
      "Samsung.25.New York",
      "Samsung.25.San Francisco",
      "Samsung.30.Chicago",
      "Samsung.30.New York",
      "Samsung.30.San Francisco",
      "Samsung.35.Chicago",
      "Samsung.35.New York",
      "Samsung.35.San Francisco",
      "Samsung.40.Chicago",
      "Samsung.40.New York",
      "Samsung.40.San Francisco",
    ];
    const result = listColumns(data, columns);
    expect(result).toEqual(expectedOutput);
  });
});

describe("createPivotValues", () => {
  const data = [
    { row: "A", column: "X", value: 1 },
    { row: "A", column: "Y", value: 2 },
    { row: "B", column: "X", value: 3 },
    { row: "B", column: "Y", value: 4 },
  ];
  const rows = ["row"];
  const columns = ["column"];
  const values = ["value"];

  test("should return an array of pivot values", () => {
    const expectedOutput = [
      [
        {
          actualText: "value",
          axis: "value",
          columnHeaders: "X",
          rowHeaders: "A",
          value: 1,
        },
        {
          actualText: "value",
          axis: "value",
          columnHeaders: "Y",
          rowHeaders: "A",
          value: 2,
        },
      ],
      [
        {
          actualText: "value",
          axis: "value",
          columnHeaders: "X",
          rowHeaders: "B",
          value: 3,
        },
        {
          actualText: "value",
          axis: "value",
          columnHeaders: "Y",
          rowHeaders: "B",
          value: 4,
        },
      ],
    ];
    const result = createPivotValues(data, rows, columns, values);
    expect(result).toEqual(expectedOutput);
  });
});
