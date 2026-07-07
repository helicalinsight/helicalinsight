import {
  checkIfPointChart,
  createCellId,
  getColumnHeadersHeight,
  getRowHeadersWidth,
  getNumberColumnCells,
  getRowHeaders,
  getRange,
  createHorizontalEllipsis,
  getLabelLength,
  split,
  formatNumber,
  checkParentElementBoundingRect,
  getTicks,
  getNumberRowOfCells,
  getColumnHeaders,
  getGraphHeight,
} from "../../../../../../components/hi-reports/hi-viz-area/pivot-view/utils/chart";
describe("checkIfPointChart function", () => {
  test("should return true for subVizType in pointCharts array", () => {
    const subVizType = "circle";
    const result = checkIfPointChart(subVizType);
    expect(result).toBe(true);
  });

  test("should return false for subVizType not in pointCharts array", () => {
    const subVizType = "line";
    const result = checkIfPointChart(subVizType);
    expect(result).toBe(false);
  });

  test("should return false for empty subVizType", () => {
    const subVizType = "";
    const result = checkIfPointChart(subVizType);
    expect(result).toBe(false);
  });
});

describe("createCellId function", () => {
  test("should return cell id with columnHeaders and rowHeaders separated by a dash", () => {
    const cell = {
      columnHeaders: "Header Test1",
      rowHeaders: "Header Test2",
    };
    const result = createCellId(cell);
    expect(result).toBe("Header-Test1-Header-Test2");
  });

  test("should replace spaces with dashes in the cell id", () => {
    const cell = {
      columnHeaders: "Header Test1",
      rowHeaders: "Header Test2",
    };
    const result = createCellId(cell);
    expect(result).toBe("Header-Test1-Header-Test2");
  });
});

describe("getColumnHeadersHeight function", () => {
  test("returns 0 when columns and valuesList.columns are empty and rows is not empty", () => {
    const graphRef = {
      columns: [],
      valuesList: { columns: [] },
      columnsAxisHeight: 0,
      rows: [1, 2, 3],
    };
    const result = getColumnHeadersHeight(graphRef);
    expect(result).toBe(20);
  });

  test("returns the sum of columns heights plus 20 when columns is not empty and valuesList.columns is empty", () => {
    const graphRef = {
      columns: [{ height: 50 }, { height: 70 }, { height: 25 }],
      valuesList: { columns: [] },
      columnsAxisHeight: 0,
      rows: [],
    };
    const result = getColumnHeadersHeight(graphRef);
    expect(result).toBe(165);
  });

  test("returns the sum of columns heights plus columnsAxisHeight when columns and valuesList.columns are not empty", () => {
    const graphRef = {
      columns: [{ height: 30 }, { height: 45 }, { height: 25 }],
      valuesList: { columns: [1, 2, 3] },
      columnsAxisHeight: 60,
      rows: [],
    };
    const result = getColumnHeadersHeight(graphRef);
    expect(result).toBe(180);
  });

  test("returns 0 when columns, valuesList.columns and rows are empty", () => {
    const graphRef = {
      columns: [],
      valuesList: { columns: [] },
      columnsAxisHeight: 0,
      rows: [],
    };
    const result = getColumnHeadersHeight(graphRef);
    expect(result).toBe(0);
  });
});

describe("getRowHeadersWidth function", () => {
  test("should return the correct width", () => {
    const graphRef = {
      rows: [
        { name: "TestRow 1", width: 100 },
        { name: "TestRow 2", width: 200 },
        { name: "TestRow 3", width: 300 },
      ],
      valuesList: {
        rows: [{ name: "TestRow 1" }, { name: "TestRow 2" }],
      },
      rowAxisWidth: 50,
    };

    const expectedWidth = 650;

    const result = getRowHeadersWidth(graphRef);

    expect(result).toEqual(expectedWidth);
  });

  test("should return 0 if there are no rows", () => {
    const graphRef = {
      rows: [],
      valuesList: {
        rows: [{ name: "TestRowValue 1" }, { name: "TestRowValue 2" }],
      },
      rowAxisWidth: 0,
    };

    const expectedWidth = 0;

    const result = getRowHeadersWidth(graphRef);

    expect(result).toEqual(expectedWidth);
  });

  test("should include the row axis width if valuesList contains rows", () => {
    const graphRef = {
      rows: [
        { name: "TestRow 1", width: 100 },
        { name: "TestRow 2", width: 200 },
        { name: "TestRow 3", width: 300 },
      ],
      valuesList: {
        rows: [{ name: "TestRowValue 1" }, { name: "TestRowValue 2" }],
      },
      rowAxisWidth: 100,
    };

    const expectedWidth = 700;

    const result = getRowHeadersWidth(graphRef);

    expect(result).toEqual(expectedWidth);
  });

  test("should not include the row axis width if valuesList does not contain rows", () => {
    const graphRef = {
      rows: [
        { name: "TestRow 1", width: 150 },
        { name: "TestRow 2", width: 200 },
        { name: "TestRow 3", width: 300 },
      ],
      valuesList: {
        rows: [{ name: "_no_Value_" }],
      },
      rowAxisWidth: 50,
    };

    const expectedWidth = 650;

    const result = getRowHeadersWidth(graphRef);
    expect(result).toEqual(expectedWidth);
  });
});

describe("getNumberColumnCells function", () => {
  test("should return 1 if pane does not have children", () => {
    const pane = {
      hasChild: false,
      members: [],
    };
    expect(getNumberColumnCells(pane)).toBe(1);
  });

  test("should return the total number of cells in all children panes", () => {
    const pane = {
      hasChild: true,
      members: [
        { hasChild: false },
        { hasChild: true, members: [{ hasChild: false }, { hasChild: false }] },
      ],
    };
    expect(getNumberColumnCells(pane)).toBe(3);
  });
});

describe("getRange function", () => {
  test("returns correct max and min values when values are positive", () => {
    const graphRef = {
      rows: ["Row 1", "Row 2"],
      columns: ["Column 1", "Column 2"],
    };
    const pivotValues = [
      [
        {
          axis: "row",
          value: "Row 1",
          rowHeaders: "Row 1-seperator-",
          columnHeaders: "Column 1-seperator-",
        },
        {
          axis: "column",
          value: "Column 1",
          rowHeaders: "Row 1-seperator-",
          columnHeaders: "Column 1-seperator-",
        },
        {
          axis: "value",
          value: 10,
          actualText: "Field ",
          rowHeaders: "Row 1-seperator-",
          columnHeaders: "Column 1-seperator-",
        },
      ],
      [
        {
          axis: "row",
          value: "Row 2",
          rowHeaders: "Row 2-seperator-",
          columnHeaders: "Column 2-seperator-",
        },
        {
          axis: "column",
          value: "Column 2",
          rowHeaders: "Row 2-seperator-",
          columnHeaders: "Column 2-seperator-",
        },
        {
          axis: "value",
          value: 20,
          actualText: "Field ",
          rowHeaders: "Row 2-seperator-",
          columnHeaders: "Column 2-seperator-",
        },
      ],
    ];
    const range = getRange("Field ", graphRef, pivotValues);
    expect(range.max).toBe(30);
    expect(range.min).toBe(0);
  });
});

// test('returns correct max and min values when values are negative', () => {
//   const graphRef = { rows: ['Row 1', 'Row 2'], columns: ['

describe("getRowHeaders function", () => {
  test("returns an empty object if rows is empty", () => {
    const graphRef = { rows: [] };
    const pivotValues = [
      [{ valueSort: { axis: "x-axis", levelName: "test1-seperator-test2" } }],
    ];
    expect(getRowHeaders(graphRef, pivotValues)).toEqual({});
  });

  test("correctly extracts row headers from pivotValues", () => {
    const graphRef = { rows: [{ name: "row1" }, { name: "row2" }] };
    const pivotValues = [
      [{ valueSort: { axis: "x-axis", levelName: "test1-seperator-test2" } }],
      [{ valueSort: { axis: "row2", levelName: "test3-seperator-test4" } }],
    ];
    expect(getRowHeaders(graphRef, pivotValues)).toEqual({
      row1: ["test3"],
      row2: ["test4"],
    });
  });
  test("returns an empty object if pivotValues is empty", () => {
    const graphRef = { rows: [{ name: "row1" }] };
    const pivotValues = [];
    expect(getRowHeaders(graphRef, pivotValues)).toEqual({});
  });
});

describe("createHorizontalEllipsis function", () => {
  let label = "This is a test case for the createHorizontalEllipsis function";
  let width = 110;

  beforeAll(() => {
    const div = document.createElement("div");
    div.id = "ellipsis";
    div.style.display = "inline-block";
    div.style.visibility = "hidden";
    document.body.appendChild(div);
  });

  afterAll(() => {
    const div = document.getElementById("ellipsis");
    document.body.removeChild(div);
  });

  test("should return the original label if it fits within the given width", () => {
    expect(createHorizontalEllipsis(label, 500)).toEqual(label);
  });

  test("should truncate the label with an ellipsis if it exceeds the given width", () => {
    expect(createHorizontalEllipsis(label, width)).toEqual(
      "This is a test case for the createHorizontalEllipsis function"
    );
  });

  test("should handle labels that are already truncated", () => {
    expect(createHorizontalEllipsis("Short label...", width)).toEqual(
      "Short label..."
    );
  });
});

describe("getLabelLength function", () => {
  test("should return the correct label length", () => {
    const label = "getLabelLength function test Label";
    const expectedResult = label.length * 8 - 10;
    const result = getLabelLength(label);
    expect(result).toEqual(expectedResult);
  });
});

describe("split function", () => {
  it("should split the range into equal parts", () => {
    const left = 0;
    const right = 10;
    const parts = 5;
    const expectedResult = [0, 2.5, 5, 7.5, 10];
    const result = split(left, right, parts);
    expect(result).toEqual(expectedResult);
  });
});

describe("formatNumber function", () => {
  test("should return '1B' when given 1000000000", () => {
    expect(formatNumber(1000000000)).toBe("1B");
  });

  test("should return '-2M' when given -2000000", () => {
    expect(formatNumber(-2000000)).toBe("-2M");
  });

  test("should return '5M' when given 5000000", () => {
    expect(formatNumber(5000000)).toBe("5M");
  });

  test("should return '999K' when given 999999", () => {
    expect(formatNumber(999999)).toBe("999K");
  });

  test("should return '-1B' when given -1500000000", () => {
    expect(formatNumber(-1500000000)).toBe("-1B");
  });
});

describe("checkParentElementBoundingRect function", () => {
  const id = "parentElement";
  const parentElement = document.createElement("div");
  const currentCell = document.createElement("div");
  const payload = { length: 10 };
  parentElement.id = id;
  parentElement.style.position = "absolute";
  parentElement.style.top = "100px";
  parentElement.style.left = "100px";
  parentElement.style.width = "200px";
  parentElement.style.height = "200px";
  currentCell.style.position = "absolute";
  currentCell.style.top = "150px";
  currentCell.style.left = "150px";
  document.body.appendChild(parentElement);
  document.body.appendChild(currentCell);

  test("should return the correct result when the current cell is within the parent element bounds", () => {
    const expectedResult = { bottom: 0, left: 0, right: 0, top: -305 };
    const result = checkParentElementBoundingRect(id, currentCell, payload);
    expect(result).toEqual(expectedResult);
  });
});

describe("getTicks function", () => {
  test("returns correct ticks for positive range", () => {
    const result = getTicks({ max: 100, min: 0, width: 800, axisValue: 1 });
    expect(result).toEqual([
      { displayValue: 0, value: 0 },
      { displayValue: 5, value: 5 },
      { displayValue: 10, value: 10 },
      { displayValue: 15, value: 15 },
      { displayValue: 20, value: 20 },
      { displayValue: 25, value: 25 },
      { displayValue: 30, value: 30 },
      { displayValue: 35, value: 35 },
      { displayValue: 40, value: 40 },
      { displayValue: 45, value: 45 },
      { displayValue: 50, value: 50 },
      { displayValue: 55, value: 55 },
      { displayValue: 60, value: 60 },
      { displayValue: 65, value: 65 },
      { displayValue: 70, value: 70 },
      { displayValue: 75, value: 75 },
      { displayValue: 80, value: 80 },
      { displayValue: 85, value: 85 },
      { displayValue: 90, value: 90 },
      { displayValue: 95, value: 95 },
      { displayValue: 100, value: 100 },
    ]);
  });

  test("returns correct ticks for negative range", () => {
    const result = getTicks({ max: 0, min: -100, width: 800, axisValue: 1 });
    expect(result).toEqual([
      { displayValue: -100, value: -100 },
      { displayValue: -95, value: -95 },
      { displayValue: -90, value: -90 },
      { displayValue: -85, value: -85 },
      { displayValue: -80, value: -80 },
      { displayValue: -75, value: -75 },
      { displayValue: -70, value: -70 },
      { displayValue: -65, value: -65 },
      { displayValue: -60, value: -60 },
      { displayValue: -55, value: -55 },
      { displayValue: -50, value: -50 },
      { displayValue: -45, value: -45 },
      { displayValue: -40, value: -40 },
      { displayValue: -35, value: -35 },
      { displayValue: -30, value: -30 },
      { displayValue: -25, value: -25 },
      { displayValue: -20, value: -20 },
      { displayValue: -15, value: -15 },
      { displayValue: -10, value: -10 },
      { displayValue: -5, value: -5 },
      { displayValue: 0, value: 0 },
      { displayValue: 5, value: 5 },
    ]);
  });
});

describe("getNumberRowOfCells function", () => {
  test("should return 1 for a pane without children", () => {
    const pane = { hasChild: false };
    expect(getNumberRowOfCells(pane)).toBe(1);
  });

  test("should return the total number of cells for a pane with children", () => {
    const pane = {
      hasChild: true,
      members: [
        { hasChild: false },
        { hasChild: true, members: [{ hasChild: false }, { hasChild: false }] },
      ],
    };
    expect(getNumberRowOfCells(pane)).toBe(3);
  });

  test("should handle nested children", () => {
    const pane = {
      hasChild: true,
      members: [
        {
          hasChild: true,
          members: [
            { hasChild: false },
            { hasChild: true, members: [{ hasChild: false }] },
          ],
        },
        { hasChild: false },
      ],
    };
    expect(getNumberRowOfCells(pane)).toBe(3);
  });
});

describe("getColumnHeaders function", () => {
  test("should return an array", () => {
    const graphRef = { columns: [1, 2, 3] };
    const pivotValues = [
      ["Test1", "Test2", "Test3"],
      ["Test4", "Test5", "Test6"],
      ["Test7", "Test9", "Test3"],
    ];
    const result = getColumnHeaders(graphRef, pivotValues);
    const expectedResult = [];
    expect(result).toEqual(expectedResult);
  });

  describe("getGraphHeight function", () => {
    test("should return the correct height", () => {
      const graphRef = {
        cellHeight: 50,
      };
      const pivotValues = {};
      const result = getGraphHeight(graphRef, pivotValues);
      expect(result).toEqual(75);
    });

    test("should cap the height at 400", () => {
      const graphRef = {
        cellHeight: 375,
      };
      const pivotValues = {};
      const result = getGraphHeight(graphRef, pivotValues);
      expect(result).toEqual(400);
    });
  });
});
