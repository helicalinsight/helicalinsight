// eslint-disable-next-line jest/no-mocks-import
import { dispatch } from "../../../__mocks__/axios";
import {
  addItem,
  fallsInBreakpoint,
  sortingFunctionForContextMenu,
  wrapSpecialVariables,
} from "../../../components/hi-dashboard-designer/utils/common-functions";
import { v4 as uuidv4 } from "uuid";
import { initialConfig } from "../../../components/hi-dashboard-designer/utils/constants";
import { checkForDefaultValuesInDashboard } from "../../../components/hi-dashboard-designer/utils/config-dashboard-gridSettings";
jest.mock("uuid");

describe("addItem function", () => {
  test("working use case for dashboard-designer-component", () => {
    uuidv4.mockImplementation(() => "testid");
    const gridItem = addItem({
      dispatch,
      returnValue: true,
      compType: "dashboard-designer-component",
    });
    expect(gridItem).toStrictEqual({
      "compType": "dashboard-designer-component",
      "id": "idtestid",
      "gridItemConfig": initialConfig({ compType: "dashboard-designer-component" }),
      "initialPosition": {
        "h": 2,
        "i": "idtestid",
        "static": false,
        "w": 2,
        "x": 0,
        "y": 0,
      },
      "isGrouped": false,
      "isSaved": false,
    });
  });
});

describe("initialConfig function", () => {
  test("should include 'grid' config when compType is 'grouped-component'", () => {
    const config = initialConfig({ compType: "grouped-component", id: "testid" });
    expect(config).toContainEqual({
      key: "grid",
      values: {
        autoSize: true,
        compactType: null,
        rowHeight: 100,
        isDroppable: false,
        preventCollision: true,
        measureBeforeMount: false,
        isDraggable: true,
        isResizable: true,
        horizontalMargin: 10,
        verticalMargin: 10,
        containerMarginHorizontal: 0,
        containerMarginVertical: 0,
        allowOverlap: false,
      },
    });
  });

  test("should include 'grid' config when compType is 'tab'", () => {
    const config = initialConfig({ compType: "tab", id: "testid" });
    expect(config).toContainEqual({
      key: "grid",
      values: {
        autoSize: true,
        compactType: null,
        rowHeight: 100,
        isDroppable: false,
        preventCollision: true,
        measureBeforeMount: false,
        isDraggable: true,
        isResizable: true,
        horizontalMargin: 10,
        verticalMargin: 10,
        containerMarginHorizontal: 0,
        containerMarginVertical: 0,
        allowOverlap: false,
      },
    });
  });
});

describe("sortingFunctionForContextMenu function", () => {
  test("working use case for dashboard-designer-component", () => {
    let group = [
      {
        title: "Header",
        key: "header",
      },
      {
        title: "Shadow",
        key: "shadow",
      },
      {
        title: "Background",
        key: "background",
      },
      {
        title: "Border",
        key: "border",
      },
      {
        title: "Grid Settings",
        key: "gridsettings",
        children: [
          // {
          //   title: "Testing",
          //   key: "testing",
          // },

          {
            title: "Breakpoints",
            key: "breakpoints",
          },
          {
            title: "Columns",
            key: "columns",
          },
          {
            title: "Grid",
            key: "grid",
          },
        ],
      },
      {
        title: "Advance",
        key: "advanced",
        children: [
          {
            title: "HTML",
            key: "html",
          },

          {
            title: "CSS",
            key: "css",
          },
          {
            title: "JS",
            key: "javascript",
          },
        ],
      },
      // ...(selectedItems.length > 1
      //   ? [
      //       {
      //         title: "Group",
      //         key: "grouping",
      //         onClick: () => {
      //           groupTheGridItems();
      //         },
      //       },
      //     ]
      //   : []),
      ...(process.env.NODE_ENV === "development"
        ? [
          {
            title: "Add Item",
            key: "additem",
            onClick: () => {
              addItem({ dispatch, compType: "dashboard-designer-component" });
            },
          },
        ]
        : []),
      {
        title: "Parameters",
        key: "parameters",
      },
    ];
    expect(sortingFunctionForContextMenu(group)).toStrictEqual([
      {
        children: [
          { key: "css", title: "CSS" },
          { key: "html", title: "HTML" },
          { key: "javascript", title: "JS" },
        ],
        key: "advanced",
        title: "Advance",
      },
      { key: "background", title: "Background" },
      { key: "border", title: "Border" },
      {
        children: [
          { key: "breakpoints", title: "Breakpoints" },
          { key: "columns", title: "Columns" },
          { key: "grid", title: "Grid" },
        ],
        key: "gridsettings",
        title: "Grid Settings",
      },
      { key: "header", title: "Header" },
      { key: "parameters", title: "Parameters" },
      { key: "shadow", title: "Shadow" },
    ]);
  });
});

describe("test wrapSpecialVariables", () => {
  test('No special characters in variable names', () => {
    const template = "<p>{{Name}}</p> <span>{{Age}}</span>";
    const expected = "<p>{{Name}}</p> <span>{{Age}}</span>";
    expect(wrapSpecialVariables(template)).toEqual(expected);
  });

  test('Variable name with spaces', () => {
    const template = "<p>{{Full Name}}</p>";
    const expected = "<p>{{['Full Name']}}</p>";
    expect(wrapSpecialVariables(template)).toEqual(expected);
  });

  test('Variable name with special characters', () => {
    const template = "<p>{{Email@Address}}</p>";
    const expected = "<p>{{['Email@Address']}}</p>";
    expect(wrapSpecialVariables(template)).toEqual(expected);
  });

  test('Variable name with spaces and special characters', () => {
    const template = "<p>{{Full Name}}</p> <span>{{Email@Address}}</span>";
    const expected = "<p>{{['Full Name']}}</p> <span>{{['Email@Address']}}</span>";
    expect(wrapSpecialVariables(template)).toEqual(expected);
  });

  test('Variable names already wrapped', () => {
    const template = "<p>{{['Full Name']}}</p> <span>{{['Email@Address']}}</span>";
    const expected = "<p>{{['Full Name']}}</p> <span>{{['Email@Address']}}</span>";
    expect(wrapSpecialVariables(template)).toEqual(expected);
  });

  test('Variable name with &', () => {
    const template = "<p>{{destination&amp;fltr}}</p>";
    const expected = "<p>{{['destination&fltr']}}</p>";
    expect(wrapSpecialVariables(template)).toEqual(expected);
  });

})


describe('fallsInBreakpoint', () => {
  test('should return "xss" when breakpoints is null or undefined', () => {
    expect(fallsInBreakpoint(null, 500)).toBe("xss");
    expect(fallsInBreakpoint(undefined, 500)).toBe("xss");
  });

  test('should return "xxs" when width does not meet any breakpoint', () => {
    const breakpoints = [
      { key: 'sm', value: 480 },
      { key: 'md', value: 768 },
      { key: 'lg', value: 1024 }
    ];

    expect(fallsInBreakpoint(breakpoints, 300)).toBe('xxs');
  });

});



describe('test checkForDefaultValuesInDashboard', () => {
  it('should return empty array when both inputs are empty', () => {
    const result = checkForDefaultValuesInDashboard();
    expect(result).toEqual([]);
  });

  it('should return unchanged designerSettings when apiData is empty', () => {
    const designerSettings = [{ key: 'test', values: { foo: 'bar' } }];
    const result = checkForDefaultValuesInDashboard(designerSettings);
    expect(result).toEqual(designerSettings);
  });

  it('should return empty array when designerSettings is empty but apiData is not', () => {
    const apiData = [{ key: 'test', values: { foo: 'bar' } }];
    const result = checkForDefaultValuesInDashboard([], apiData);
    expect(result).toEqual([]);
  });

  it('should merge values when both inputs have matching keys', () => {
    const designerSettings = [{ key: 'parameters', values: { foo: 'bar' } }];
    const apiData = [{ key: 'parameters', values: { closeOnApply: true } }];
    const result = checkForDefaultValuesInDashboard(designerSettings, apiData);
    expect(result).toEqual([{ key: 'parameters', values: { foo: 'bar', closeOnApply: true } }]);
  });

  it('should add default value when parameters key is present in designerSettings but not in apiData', () => {
    const designerSettings = [{ key: 'parameters', values: { foo: 'bar' } }];
    const result = checkForDefaultValuesInDashboard(designerSettings);
    expect(result).toEqual([{ key: 'parameters', values: { foo: 'bar', closeOnApply: false } }]);
  });

  it('should merge values when parameters key is present in both inputs', () => {
    const designerSettings = [{ key: 'parameters', values: { foo: 'bar' } }];
    const apiData = [{ key: 'parameters', values: { closeOnApply: true, baz: 'qux' } }];
    const result = checkForDefaultValuesInDashboard(designerSettings, apiData);
    expect(result).toEqual([{ key: 'parameters', values: { foo: 'bar', closeOnApply: true } }]);
  });
});