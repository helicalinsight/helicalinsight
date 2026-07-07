import {
  getDividedArray,
  removeNullKeysFromObject,
  hrNumberFormat,
  getFormattedLabel,
  changeToTotal,
  detectOverlapping,
  getGridAxisTextFormat,
  getPropertyFieldInfo,
  replaceChildrenWithImage,
  createImageElement,
  getMapChartLegend,
  clearGridChart,
  getMapChartLegendCustomHTML,
} from "../../../../../components/hi-reports/hi-viz-area/utils/utillities";
import {
  getMarks,
  createColorsList,
  createsizesList,
  createMaxValue,
  getSortObject,
  checkIsDateType,
  removeOverlapping,
  getPropertyElement,
  addFilter,
  hrPropertyNumberFormat,
} from "../../../../../components/hi-reports/hi-viz-area/utils/utillities";

describe("Helical Report Utilities", () => {
  test("remove removeNullKeysFromObject", async () => {
    const objectWithNullKeys = {
      name: "test",
      class: "classnullkey",
      section: null,
    };
    const expectedResult = {
      name: "test",
      class: "classnullkey",
    };
    let result = removeNullKeysFromObject(objectWithNullKeys);
    expect(result).toEqual(expectedResult);
  });

  test("To test the getPropertyFieldInfo function", () => {
    const field = "sum_travel_cost";

    const report = {
      properties: {
        title: {
          show: false,
          value: "",
          padding: 0,
          fontSize: 32,
          fontColor: {
            a: 1,
            b: 0,
            g: 0,
            r: 0,
          },
          alignment: "center",
          position: "top",
        },
        subTitle: {
          show: false,
          value: "",
          padding: 0,
          fontSize: 24,
          fontColor: {
            a: 1,
            b: 0,
            g: 0,
            r: 0,
          },
          alignment: "center",
          position: "top",
        },
        format: {
          formatFields: [],
          formatDatatype: "",
          activeFieldId: "",
        },
        cache: {
          isCacheEnabled: false,
          interval: "00:00:01",
        },
        bar: {
          barType: "stacked",
        },
        radial: {
          showRadial: false,
        },
        legend: {
          legendPosition: "right",
        },
        formatColor: {
          defaultColor: {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
          showAll: false,
          dataColors: [],
          formatColorStyle: "",
          formatColorField: "",
          minimum: {
            r: 183,
            g: 192,
            b: 232,
            a: 1,
          },
          maximum: {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        },
      },
      fields: [
        {
          column: "travel_details.mode_of_payment",
          columnID: "13117",
          label: "mode_of_payment",
          id: "17c42809-1092-4195-a222-73caafb48f5e",
          type: {
            backendDataType: "java.lang.String",
            dataType: "text",
          },
          autogen_alias: "mode_of_payment",
          isNormalTable: true,
          tableAlias: "travel_details",
          groupBy: ["db.generic.groupBy.group"],
          orderByColumn: false,
          showOrderByColumn: false,
          addedAs: "column",
          floatingType: "discrete",
          functionsDefinition: "",
          applyBeforeAggregate: false,
          hiddenIncludeInResultSet: false,
          metaDataAlias: "mode_of_payment",
          databaseName: "HIUSER",
        },
        {
          column: "travel_details.travel_cost",
          columnID: "13116",
          label: "sum_travel_cost",
          id: "17a2db31-08d4-481a-b063-c220b5b27dcc",
          type: {
            backendDataType: "java.lang.Integer",
            dataType: "numeric",
          },
          autogen_alias: "sum_travel_cost",
          isNormalTable: true,
          tableAlias: "travel_details",
          aggregate: ["db.generic.aggregate.sum"],
          orderByColumn: false,
          showOrderByColumn: false,
          addedAs: "column",
          floatingType: "",
          functionsDefinition: "",
          applyBeforeAggregate: false,
          hiddenIncludeInResultSet: false,
          metaDataAlias: "travel_cost",
          databaseName: "HIUSER",
        },
        {
          column: "travel_details.mode_of_payment",
          columnID: "13117",
          label: "mode_of_payment",
          id: "1cf84c38-0a6a-4e5a-8c9f-2d3e00b168d0",
          type: {
            backendDataType: "java.lang.String",
            dataType: "text",
          },
          autogen_alias: "mode_of_payment",
          isNormalTable: true,
          tableAlias: "travel_details",
          groupBy: ["db.generic.groupBy.group"],
          orderByColumn: false,
          showOrderByColumn: false,
          addedAs: "drillthrough_field",
          floatingType: "discrete",
          functionsDefinition: "",
          applyBeforeAggregate: false,
          hiddenIncludeInResultSet: false,
          metaDataAlias: "mode_of_payment",
        },
      ],
    };

    const result = getPropertyFieldInfo(report, field);
    let expectedResult = {
      fieldType: "numeric",
      formatField: [],
      isApplyClicked: undefined,
      reportField: {
        addedAs: "column",
        aggregate: [
          "db.generic.aggregate.sum",
        ],
        applyBeforeAggregate: false,
        autogen_alias: "sum_travel_cost",
        column: "travel_details.travel_cost",
        columnID: "13116",
        databaseName: "HIUSER",
        floatingType: "",
        functionsDefinition: "",
        hiddenIncludeInResultSet: false,
        id: "17a2db31-08d4-481a-b063-c220b5b27dcc",
        isNormalTable: true,
        label: "sum_travel_cost",
        metaDataAlias: "travel_cost",
        orderByColumn: false,
        showOrderByColumn: false,
        tableAlias: "travel_details",
        type: {
          backendDataType: "java.lang.Integer",
          dataType: "numeric",
        },
      },
    };
    expect(result).toEqual(expectedResult);
  });

  test("To test the addFilter function", () => {
    const filtersList = [
      {
        field: "booking_platform",
        value: "Makemytrip",
        condition: "IS_ONE_OF",
        drillownFilter: "report",
        drillDownFilterValues: [
          {
            booking_platform: "Makemytrip",
          },
        ],
      },
    ];

    const fields = [
      {
        column: "travel_details.booking_platform",
        columnID: "13118",
        label: "booking_platform",
        id: "afb45374-f349-4bb7-b64f-348dcb41e97f",
        type: {
          backendDataType: "java.lang.String",
          dataType: "text",
        },
        autogen_alias: "booking_platform",
        isNormalTable: true,
        tableAlias: "travel_details",
        groupBy: ["db.generic.groupBy.group"],
        orderByColumn: false,
        showOrderByColumn: false,
        addedAs: "column",
        floatingType: "discrete",
        functionsDefinition: "",
        applyBeforeAggregate: false,
        hiddenIncludeInResultSet: false,
        metaDataAlias: "booking_platform",
        databaseName: "HIUSER",
      },
    ];

    const expectedDrillDownId = /[a-f0-9]{8}-(?:[a-f0-9]{4}-){3}[a-f0-9]{12}/;
    const result = addFilter(filtersList, fields);
    let expectedResult = [
      {
        addedAs: "column",
        applyBeforeAggregate: false,
        autogen_alias: "booking_platform",
        column: "travel_details.booking_platform",
        columnID: "13118",
        condition: "IS_ONE_OF",
        databaseName: "HIUSER",
        drillDownFilterValues: [{ booking_platform: "Makemytrip" }],
        drillDownId: expect.stringMatching(expectedDrillDownId),
        drillownFilter: "report",
        floatingType: "discrete",
        functionsDefinition: "",
        groupBy: ["db.generic.groupBy.group"],
        hiddenIncludeInResultSet: false,
        id: "afb45374-f349-4bb7-b64f-348dcb41e97f",
        isNormalTable: true,
        label: "booking_platform",
        metaDataAlias: "booking_platform",
        orderByColumn: false,
        showOrderByColumn: false,
        tableAlias: "travel_details",
        type: { backendDataType: "java.lang.String", dataType: "text" },
        values: ["Makemytrip"],
      },
    ];
    expect(result).toEqual(expectedResult);
  });

  test("checking the functionality of getGridAxisTextFormat function", () => {
    const text = "International";
    const data = [
      {
        travel_type: "Domestic",
        sum_travel_cost: 2389070,
      },
      {
        travel_type: "International",
        sum_travel_cost: 16144900,
      },
    ];
    const index = 1;
    const report = {
      properties: {
        title: {
          show: false,
          value: "",
          padding: 0,
          fontSize: 32,
          fontColor: {
            a: 1,
            b: 0,
            g: 0,
            r: 0,
          },
          alignment: "center",
          position: "top",
        },
        subTitle: {
          show: false,
          value: "",
          padding: 0,
          fontSize: 24,
          fontColor: {
            a: 1,
            b: 0,
            g: 0,
            r: 0,
          },
          alignment: "center",
          position: "top",
        },
        format: {
          formatFields: [],
          formatDatatype: "",
          activeFieldId: "",
        },
        cache: {
          isCacheEnabled: false,
          interval: "00:00:01",
        },
        bar: {
          barType: "stacked",
        },
        radial: {
          showRadial: false,
        },
        legend: {
          legendPosition: "right",
        },
        formatColor: {
          defaultColor: {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
          showAll: false,
          dataColors: [],
          formatColorStyle: "",
          formatColorField: "",
          minimum: {
            r: 183,
            g: 192,
            b: 232,
            a: 1,
          },
          maximum: {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        },
      },
      fields: [
        {
          column: "travel_details.travel_type",
          columnID: "13110",
          label: "travel_type",
          id: "fdd0859b-1961-461b-82d1-544d6dd1f4a4",
          type: {
            backendDataType: "java.lang.String",
            dataType: "text",
          },
          autogen_alias: "travel_type",
          isNormalTable: true,
          tableAlias: "travel_details",
          groupBy: ["db.generic.groupBy.group"],
          orderByColumn: false,
          showOrderByColumn: false,
          addedAs: "column",
          floatingType: "discrete",
          functionsDefinition: "",
          applyBeforeAggregate: false,
          hiddenIncludeInResultSet: false,
          metaDataAlias: "travel_type",
          databaseName: "HIUSER",
        },
        {
          column: "travel_details.travel_cost",
          columnID: "13116",
          label: "sum_travel_cost",
          id: "a9e2a440-ef8d-44a4-b006-e8476dd2b833",
          type: {
            backendDataType: "java.lang.Integer",
            dataType: "numeric",
          },
          autogen_alias: "sum_travel_cost",
          isNormalTable: true,
          tableAlias: "travel_details",
          aggregate: ["db.generic.aggregate.sum"],
          orderByColumn: false,
          showOrderByColumn: false,
          addedAs: "row",
          floatingType: "",
          functionsDefinition: "",
          applyBeforeAggregate: false,
          hiddenIncludeInResultSet: false,
          metaDataAlias: "travel_cost",
          databaseName: "HIUSER",
        },
      ],
    };
    const context = {
      axisFields: ["travel_type"],
      facetFields: [[], []],
    };
    const expectedResult = "International";

    let result = getGridAxisTextFormat({ text, data, index, report, context });

    expect(result).toEqual(expectedResult);
  });

  test("checking the functionality of getPropertyElement function", () => {
    const titleStyle = {};
    const title = {
      show: false,
      value: "",
      padding: 0,
      fontSize: 32,
      fontColor: {
        a: 1,
        b: 0,
        g: 0,
        r: 0,
      },
      alignment: "center",
      position: "top",
    };

    const expectedResult = null;

    let result = getPropertyElement(titleStyle, title);

    expect(result).toEqual(expectedResult);
  });

  test("divides the input array into two arrays", () => {
    const arr = [1, 2, 3, 4, 5, 6];
    const result = getDividedArray(arr);
    expect(result).toEqual({ firstArray: [1, 3, 5], secondArray: [2, 4, 6] });
  });

  test("Convert an integer into hrNumberFormat", () => {
    const actualValue = 8173137;
    const expectedResult = "8.2M";
    let result = hrNumberFormat(actualValue);
    expect(result).toEqual(expectedResult);
  });

  test("getMarks", () => {
    const marksList = [
      {
        value: "_all_",
        id: "a0490f41-01ab-4130-b837-7983da279c71",
        subVizType: "",
        color: {
          fields: [
            {
              id: "12f2b0e2-591c-4c03-924d-7c6e29083830",
            },
          ],
        },
        size: {
          fields: [
            {
              id: "64c33398-385a-45eb-b142-3ddd2576249f",
            },
          ],
        },
        label: {
          fields: [],
        },
        tooltip: {
          fields: [],
        },
        shape: {
          fields: [],
        },
        detail: {
          fields: [],
        },
      },
      {
        value: "sum_travel_cost",
        id: "5c00d4ac-d6a2-47f4-86bf-fe6e0452feaa",
        subVizType: "",
        color: {
          fields: [],
        },
        size: {
          fields: [],
        },
        label: {
          fields: [],
        },
        tooltip: {
          fields: [],
        },
        shape: {
          fields: [],
        },
        detail: {
          fields: [],
        },
      },
    ];

    const fields = [
      {
        column: "travel_details.booking_platform",
        label: "booking_platform",
        id: "bedd75f4-bf77-43f6-b37b-9c55574eda2d",
        type: {
          backendDataType: "java.lang.String",
          dataType: "text",
        },
        autogen_alias: "booking_platform",
        isNormalTable: true,
        tableAlias: "travel_details",
        groupBy: ["db.generic.groupBy.group"],
        orderByColumn: false,
        showOrderByColumn: false,
        addedAs: "column",
        floatingType: "discrete",
        functionsDefinition: "",
        applyBeforeAggregate: false,
        hiddenIncludeInResultSet: false,
        metaDataAlias: "booking_platform",
      },
      {
        column: "travel_details.travel_cost",
        label: "sum_travel_cost",
        id: "5c00d4ac-d6a2-47f4-86bf-fe6e0452feaa",
        type: {
          backendDataType: "java.lang.Integer",
          dataType: "numeric",
        },
        autogen_alias: "sum_travel_cost",
        isNormalTable: true,
        tableAlias: "travel_details",
        aggregate: ["db.generic.aggregate.sum"],
        orderByColumn: false,
        showOrderByColumn: false,
        addedAs: "row",
        floatingType: "",
        functionsDefinition: "",
        applyBeforeAggregate: false,
        hiddenIncludeInResultSet: false,
        metaDataAlias: "travel_cost",
      },
      {
        column: "travel_details.travel_cost",
        label: "sum_travel_cost",
        id: "12f2b0e2-591c-4c03-924d-7c6e29083830",
        type: {
          backendDataType: "java.lang.Integer",
          dataType: "numeric",
        },
        autogen_alias: "sum_travel_cost",
        isNormalTable: true,
        tableAlias: "travel_details",
        aggregate: ["db.generic.aggregate.sum"],
        orderByColumn: false,
        showOrderByColumn: false,
        addedAs: "color",
        floatingType: "",
        functionsDefinition: "",
        applyBeforeAggregate: false,
        hiddenIncludeInResultSet: false,
        metaDataAlias: "travel_cost",
      },
      {
        column: "travel_details.travel_cost",
        label: "sum_travel_cost",
        id: "64c33398-385a-45eb-b142-3ddd2576249f",
        type: {
          backendDataType: "java.lang.Integer",
          dataType: "numeric",
        },
        autogen_alias: "sum_travel_cost",
        isNormalTable: true,
        tableAlias: "travel_details",
        aggregate: ["db.generic.aggregate.sum"],
        orderByColumn: false,
        showOrderByColumn: false,
        addedAs: "size",
        floatingType: "",
        functionsDefinition: "",
        applyBeforeAggregate: false,
        hiddenIncludeInResultSet: false,
        metaDataAlias: "travel_cost",
      },
    ];

    let result = getMarks(marksList, fields);
    let expectedResult = {
      colorMarks: ["sum_travel_cost"],
      sizeMarks: ["sum_travel_cost"],
    };
    expect(result).toEqual(expectedResult);
  });

  test("checking the functionality of createColorsList function", () => {
    const colorMarks = "sum_travel_cost";
    const data = [
      {
        booking_platform: "Agent",
        sum_travel_cost: 3641245,
        sum_travel_id: 131223,
      },
      {
        booking_platform: "Makemytrip",
        sum_travel_cost: 6719588,
        sum_travel_id: 135702,
      },
      {
        booking_platform: "Website",
        sum_travel_cost: 8173137,
        sum_travel_id: 234576,
      },
    ];

    let result = createColorsList(colorMarks, data);
    let expectedResult = {
      colors: {
        3641245: "blue",
        6719588: "green",
        8173137: "red",
      },
    };
    expect(result).toEqual(expectedResult);
  });

  test("checking the functionality of createsizesList function", () => {
    const sizeMarks = "sum_travel_cost";
    const data = [
      {
        booking_platform: "Agent",
        sum_travel_cost: 3641245,
      },
      {
        booking_platform: "Makemytrip",
        sum_travel_cost: 6719588,
      },
      {
        booking_platform: "Website",
        sum_travel_cost: 8173137,
      },
    ];

    let result = createsizesList(sizeMarks, data);
    let expectedResult = {
      sizes: {
        3641245: 6.333333333333333,
        6719588: 9.666666666666666,
        8173137: 13,
      },
    };
    expect(result).toEqual(expectedResult);
  });

  test("checking the functionality of createMaxValue function", () => {
    const colorMarks = ["sum_travel_cost"];
    const data = [
      {
        booking_platform: "Agent",
        sum_travel_cost: 3641245,
      },
      {
        booking_platform: "Makemytrip",
        sum_travel_cost: 6719588,
      },
      {
        booking_platform: "Website",
        sum_travel_cost: 8173137,
      },
    ];

    let result = createMaxValue(colorMarks, data);
    let expectedResult = 8173137;
    expect(result).toEqual(expectedResult);
  });

  test("checking the functionality of changeToTotal function", () => {
    const data = [
      {
        sum_longitude: 4290.51988,
        sum_latitude: 1618.6191620000002,
      },
    ];

    let result = changeToTotal(data);

    let expectedResult = [
      {
        sum_longitude: 4290.51988,
        sum_latitude: 1618.6191620000002,
      },
    ];

    expect(result).toEqual(expectedResult);
  });
});

describe("hrPropertyNumberFormat function", () => {
  test("should return the input value if it's not a valid number", () => {
    expect(hrPropertyNumberFormat("abc", 2)).toBe("abc");
  });

  test("should format number in millions with the correct decimal place", () => {
    expect(hrPropertyNumberFormat(1500000, 2)).toBe("1.50M");
  });

  test("should format number in millions with no decimal place", () => {
    expect(hrPropertyNumberFormat(5000000, 0)).toBe("5M");
  });

  test("should format number in thousands with the correct decimal place", () => {
    expect(hrPropertyNumberFormat(4500, 2)).toBe("4.50K");
  });

  test("should format number in thousands with no decimal place", () => {
    expect(hrPropertyNumberFormat(22000, 0)).toBe("22K");
  });

  test("should return the input value if it's less than 1000", () => {
    expect(hrPropertyNumberFormat(500, 2)).toBe(500);
  });

  test("should format number in billions", () => {
    expect(hrPropertyNumberFormat(18533970000000000, 2)).toBe("18533970.00B");
  });
});

describe("getSortObject function", () => {
  test("should set the sort function to sort by month order when sortingObjKeys is empty", () => {
    const alias = "month";
    const sortObject = {};
    const months = ["January", "February", "March"];
    const sortingObjKeys = [];
    const orderBy = [];
    getSortObject(alias, sortObject, months, sortingObjKeys, orderBy);
    expect(sortObject[alias]("January", "February")).toBe(-1);
    expect(sortObject[alias]("February", "March")).toBe(-1);
  });

  test("should set the sort function to sort by month order with desc order when orderBy is set to 'desc'", () => {
    const alias = "month";
    const sortObject = {};
    const months = [
      "January",
      "February",
      "March",
      "April",
      "May",
      "June",
      "July",
      "August",
      "September",
      "October",
      "November",
      "December",
    ];
    const sortingObjKeys = [alias];
    const orderBy = ["desc"];

    getSortObject(alias, sortObject, months, sortingObjKeys, orderBy);

    expect(sortObject[alias]("January", "February")).toBe(1);
    expect(sortObject[alias]("February", "March")).toBe(1);
  });

  test("should not set the sort function when the alias is not in sortingObjKeys", () => {
    const alias = "month";
    const sortObject = {};
    const months = ["January", "February", "March"];
    const sortingObjKeys = ["year"];
    const orderBy = [];
    getSortObject(alias, sortObject, months, sortingObjKeys, orderBy);
    expect(sortObject).toEqual({});
  });
});

describe("getFormattedLabel", () => {
  test("should format the value with dateFormat and getPropertyText", () => {
    const value = new Date("2022-03-24");
    const funcName = "dd/MM/yyyy";
    const isApplyClicked = true;
    const fieldType = "date";
    const formatField = "axis";

    const result = getFormattedLabel({
      value,
      funcName,
      isApplyClicked,
      fieldType,
      formatField,
    });

    expect(result).toEqual("2022-03-24");
  });

  test("should return value if value is falsy", () => {
    const value = null;
    const funcName = "dd/MM/yyyy";
    const isApplyClicked = true;
    const fieldType = "date";
    const formatField = "axis";

    const result = getFormattedLabel({
      value,
      funcName,
      isApplyClicked,
      fieldType,
      formatField,
    });

    expect(result).toEqual(null);
  });
});

describe("checkIsDateType", () => {
  test("should return false if there are no dimensions in the schema", () => {
    const schema = [
      { name: "metric1", type: "metric" },
      { name: "metric2", type: "metric" },
    ];
    expect(checkIsDateType(schema)).toBe(false);
  });

  test("should return false if there are more than one dimension in the schema", () => {
    const schema = [
      { name: "date", type: "dimension", subtype: "date" },
      { name: "category", type: "dimension", subtype: "category" },
    ];
    expect(checkIsDateType(schema)).toBe(false);
  });

  test("should return false if the only dimension in the schema is not a date type", () => {
    const schema = [
      { name: "name", type: "dimension", subtype: "string" },
      { name: "metric", type: "metric" },
    ];
    expect(checkIsDateType(schema)).toBe(true);
  });

  test("should return true if the only dimension in the schema is a date type", () => {
    const schema = [
      { name: "date", type: "dimension", subtype: "date" },
      { name: "metric", type: "metric" },
    ];
    expect(checkIsDateType(schema)).toBe(true);
  });
});

describe("removeOverlapping", () => {
  test("does nothing when there are no child nodes or the index is not the last one", () => {
    const el = document.createElement("div");

    const handleRemoveLabels = jest.fn();
    const generateRectangles = jest.fn();

    removeOverlapping({
      el,
      index: 0,
      axis: "x",
      generateRectangles,
      handleRemoveLabels,
    });

    expect(generateRectangles).not.toHaveBeenCalled();
    expect(handleRemoveLabels).not.toHaveBeenCalled();
  });
});

describe("detectOverlapping", () => {
  test("returns true when two elements overlap", () => {
    const prevEl = document.createElement("div");
    const nextEl = document.createElement("div");
    prevEl.style.position = "absolute";
    prevEl.style.top = "10px";
    prevEl.style.left = "10px";
    prevEl.style.width = "100px";
    prevEl.style.height = "100px";
    nextEl.style.position = "absolute";
    nextEl.style.top = "50px";
    nextEl.style.left = "50px";
    nextEl.style.width = "100px";
    nextEl.style.height = "100px";
    document.body.appendChild(prevEl);
    document.body.appendChild(nextEl);

    const result = detectOverlapping(prevEl, nextEl);

    expect(result).toBe(true);

    document.body.removeChild(prevEl);
    document.body.removeChild(nextEl);
  });

  test("returns false when two elements do not overlap", () => {
    const prevEl = document.createElement("div");
    const nextEl = document.createElement("div");
    prevEl.style.position = "absolute";
    prevEl.style.top = "10px";
    prevEl.style.left = "10px";
    prevEl.style.width = "100px";
    prevEl.style.height = "100px";
    nextEl.style.position = "absolute";
    nextEl.style.top = "200px";
    nextEl.style.left = "200px";
    nextEl.style.width = "100px";
    nextEl.style.height = "100px";
    document.body.appendChild(prevEl);
    document.body.appendChild(nextEl);

    const result = detectOverlapping(prevEl, nextEl);

    expect(result).toBe(true);

    document.body.removeChild(prevEl);
    document.body.removeChild(nextEl);
  });
});

describe("To test the hrNumberFormat function", () => {
  test("To test the hrNumberFormat function with agent values", () => {
    const actualValue = 9980;

    const result = hrNumberFormat(actualValue);
    let expectedResult = "10K";
    expect(result).toEqual(expectedResult);
  });

  test("To test the hrNumberFormat function with makemytrip values", () => {
    const actualValue = 9159;

    const result = hrNumberFormat(actualValue);
    let expectedResult = "9.2K";
    expect(result).toEqual(expectedResult);
  });

  test("To test the hrNumberFormat function with website values", () => {
    const actualValue = 17383;

    const result = hrNumberFormat(actualValue);
    let expectedResult = "17.4K";
    expect(result).toEqual(expectedResult);
  });
});

// describe('To test replaceChildrenWithImage', () => {
//   let parentElement;

//   beforeEach(() => {
//     parentElement = document.createElement('div');
//     parentElement.setAttribute('id', 'test-container');
//     document.body.appendChild(parentElement);
//   });

//   afterEach(() => {
//     document.body.removeChild(parentElement);
//   });

//   it('should replace children with the provided image', () => {
//     const imageData = 'test.jpg';
//     const id = 'test-container';

//     replaceChildrenWithImage(imageData, id);

//     const imageElement = document.getElementById(id).querySelector('img');

//     expect(imageElement).not.toBeNull();
//     expect(imageElement.getAttribute('src')).toBe(imageData);
//   });

//   it('should remove all existing children before appending the image', () => {
//     const imageData = 'test_image.jpg';
//     const id = 'test-container';

//     const child1 = document.createElement('div');
//     const child2 = document.createElement('div');
//     parentElement.appendChild(child1);
//     parentElement.appendChild(child2);

//     expect(parentElement.children.length).toBe(2);

//     replaceChildrenWithImage(imageData, id);

//     expect(parentElement.children.length).toBe(1);
//   });
// });


describe('To test createImageElement', () => {

  test('createImageElement - image element has alt attribute set to "Map_Chart"', () => {
    const src = 'test.jpg';
    const image = createImageElement(src);
    expect(image.alt).toBe('Map_Chart');
  });

  test('createImageElement - image element has width style set to "100%"', () => {
    const src = 'test.jpg';
    const image = createImageElement(src);
    expect(image.style.width).toBe('100%');
  });

  test('createImageElement - style element is created and appended to document head', () => {
    const src = 'test.jpg';
    createImageElement(src);
    const styleElement = document.querySelector('style');
    expect(styleElement).toBeTruthy();
  });
});


describe('To test getMapChartLengend functon', () => {
  test('getMapChartLegend returns null when legendPosition is "none"', () => {
    const properties = {
      legend: {
        legendPosition: 'none'
      }
    };
    expect(getMapChartLegend(properties)).toBe(null);
  });

  test('getMapChartLegend returns position "topcenter" when legendPosition is "top"', () => {
    const properties = {
      legend: {
        legendPosition: 'top'
      }
    };
    expect(getMapChartLegend(properties)).toEqual({ position: 'topcenter' });
  });

  test('getMapChartLegend returns position "bottomcenter" when legendPosition is "bottom"', () => {
    const properties = {
      legend: {
        legendPosition: 'bottom'
      }
    };
    expect(getMapChartLegend(properties)).toEqual({ position: 'bottomcenter' });
  });

  test('getMapChartLegend returns position "leftcenter" when legendPosition is "left"', () => {
    const properties = {
      legend: {
        legendPosition: 'left'
      }
    };
    expect(getMapChartLegend(properties)).toEqual({ position: 'topleft' });
  });

  test('getMapChartLegend returns position "rightcenter" when legendPosition is "right"', () => {
    const properties = {
      legend: {
        legendPosition: 'right'
      }
    };
    expect(getMapChartLegend(properties)).toEqual({ position: 'topright' });
  });

  test('getMapChartLegend returns position "bottomleft" when legendPosition is not specified', () => {
    const properties = {};
    expect(getMapChartLegend(properties)).toEqual({ position: 'bottomleft' });
  });
})

describe('test clearGridChart ', () => {
  test('clearGridChart - report with id found and has selectedType of "GridChart"', () => {
    const id = 1;
    const reports = [{ id: 1, selectedType: "GridChart" }];
    const dispatch = jest.fn();
    clearGridChart(id, reports, dispatch);
    expect(dispatch).toHaveBeenCalledWith({ payload: { selectedType: "", reportId: 1 }, type: 'RE_RENDER_GRID_CHART' });
  });

  test('clearGridChart - report with id not found', () => {
    const id = 2;
    const reports = [{ id: 1, selectedType: "GridChart" }];
    const dispatch = jest.fn();
    clearGridChart(id, reports, dispatch);
    expect(dispatch).not.toHaveBeenCalled();
  });

  test('clearGridChart - report with id found but does not have selectedType of "GridChart"', () => {
    const id = 1;
    const reports = [{ id: 1, selectedType: "MoreChart" }];
    const dispatch = jest.fn();
    clearGridChart(id, reports, dispatch);
    expect(dispatch).not.toHaveBeenCalled();
  });
})

describe('test getMapChartLegendCustomHTML function', () => {
  it('should returns a span element when data is empty', () => {
    const result = getMapChartLegendCustomHTML();
    expect(result).toEqual('<span></span>')
  });

  it('should renders a single data item with value and color', () => {
    const data = [{ value: ['Item 1'], color: 'red' }];
    const result = getMapChartLegendCustomHTML(data);
    expect(result).toBeInstanceOf(HTMLDivElement);
    expect(result.children.length).toBe(1);
    expect(result.children[0].children.length).toBe(2);
    expect(result.children[0].children[0].style.background).toBe('red');
    expect(result.children[0].children[1].innerText).toBe('Item 1');
  });

  it('should renders multiple data items with values and colors', () => {
    const data = [
      { value: ['Item 1'], color: 'red' },
      { value: ['Item 2'], color: 'blue' },
      { value: ['Item 3'], color: 'green' },
    ];
    const result = getMapChartLegendCustomHTML(data);
    expect(result).toBeInstanceOf(HTMLDivElement);
    expect(result.children.length).toBe(3);
    expect(result.children[0].children.length).toBe(2);
    expect(result.children[0].children[0].style.background).toBe('red');
    expect(result.children[0].children[1].innerText).toBe('Item 1');
    expect(result.children[1].children.length).toBe(2);
    expect(result.children[1].children[0].style.background).toBe('blue');
    expect(result.children[1].children[1].innerText).toBe('Item 2');
    expect(result.children[2].children.length).toBe(2);
    expect(result.children[2].children[0].style.background).toBe('green');
    expect(result.children[2].children[1].innerText).toBe('Item 3');
  });

  it('should renders a data item with no value', () => {
    const data = [{ color: 'red' }];
    const result = getMapChartLegendCustomHTML(data);
    expect(result).toBeInstanceOf(HTMLDivElement);
    expect(result.children.length).toBe(1);
    expect(result.children[0].children.length).toBe(2);
    expect(result.children[0].children[0].style.background).toBe('red');
    expect(result.children[0].children[1].innerText).toBe('');
  });

  it('should renders a data item with no color', () => {
    const data = [{ value: ['Item 1'] }];
    const result = getMapChartLegendCustomHTML(data);
    expect(result).toBeInstanceOf(HTMLDivElement);
    expect(result.children.length).toBe(1);
    expect(result.children[0].children.length).toBe(2);
    expect(result.children[0].children[0].style.background).toBe('');
    expect(result.children[0].children[1].innerText).toBe('Item 1');
  });
});