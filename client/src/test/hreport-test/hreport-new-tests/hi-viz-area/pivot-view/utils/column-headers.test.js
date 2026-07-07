import { getColumnHeaders , getColumnPanes1 } from "../../../../../../components/hi-reports/hi-viz-area/pivot-view/utils/column-headers";

describe("getColumnHeaders", () => {
  test("returns an array of column headers", () => {
    const data = [
      { id: 1, name: "Testname1", age: 28 },
      { id: 2, name: "Testname2", age: 33 },
      { id: 3, name: "Testname3", age: 46 },
    ];
    const columns = ["name"];
    const expected = ["Testname1", "Testname2", "Testname3"];
    const result = getColumnHeaders(data, columns);
    expect(result).toEqual(expected);
  });

  test("returns an empty array when no columns are provided", () => {
    const data = [
      { id: 1, name: "Testname1", age: 28 },
      { id: 2, name: "Testname2", age: 33 },
      { id: 3, name: "Testname3", age: 46 },
    ];
    const columns = [];
    const expected = [];
    const result = getColumnHeaders(data, columns);
    expect(result).toEqual(expected);
  });

  test("ignores duplicate headers", () => {
    const data = [
      { id: 1, name: "Testname1", age: 28 },
      { id: 2, name: "Testname2", age: 33 },
      { id: 3, name: "Testname1", age: 46 },
    ];
    const columns = ["name"];
    const expected = ["Testname1", "Testname2"];
    const result = getColumnHeaders(data, columns);
    expect(result).toEqual(expected);
  });
});

describe("getColumnPanes1", () => {
 

  test("returns an empty array when columns are not provided", () => {
    const data = [
        { id: 1, name: "Testname1", age: 28 },
        { id: 2, name: "Testname2", age: 33 },
      ]
    const columns = [];
    const cellWidth = 100;
    const expectedOutput = [];
    expect(getColumnPanes1(data, columns, cellWidth)).toEqual(expectedOutput);
  });

  

  
});
