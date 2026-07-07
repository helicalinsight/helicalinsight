import {
  generateColorRanges,
  getDataIndex,
  getDefaultAFDataTypeValues,
  getFields,
  getGridChartColorFormatScheme,
  getHTMLColorFormat,
  getMinMax,
  getPropertyAxisRange,
  getPropertyItems,
  getRangeTooltipInfo,
  getSingleDimensionData,
  rgbaToHex,
} from "../../../components/hi-reports/hi-editing-area/utils/property-utils";
import {
  getAxisConfig,
  getNumberFormat,
  getUnFormmatedText,
} from "../../../components/hi-reports/hi-viz-area/utils/utillities";
import { applyColor } from "../../../components/hi-reports/hi-editing-area/utils/property-utils";
import moment from "moment";

describe("Get Properties in H Report test cases", () => {
  test("getPropertyItems should return an array", () => {
    const result = getPropertyItems({});
    expect(Array.isArray(result)).toBe(true);
  });

  test("getPropertyItems should return the correct number of items", () => {
    const report = {
      reportData: {
        properties: {
          title: {},
          subTitle: {},
          format: {},
          cache: {},
          formatColor: {},
          bar: {},
          legend: {},
        },
      },
      fields: [{ id: "1" }, { id: "2" }],
    };
    const result = getPropertyItems({ report });
    expect(result.length).toBe(92);
  });
  test("getPropertyItems should have the required properties", () => {
    const report = { reportData: { properties: {} } };
    const fields = [];
    const fieldId = "";
    const data = [];
    const items = getPropertyItems({ report, fields, fieldId, data });

    expect(items).toBeInstanceOf(Array);
    expect(items.length).toBeGreaterThan(0);
    expect(items[0]).toHaveProperty("key");
    expect(items[0]).toHaveProperty("label");
    expect(items[0]).toHaveProperty("value");
    expect(items[0]).toHaveProperty("elementType");
    expect(items[0]).toHaveProperty("groupId");
  });

  test("should return correct smooth switch element when smooth is true", () => {
    const smooth = true;
    const result = getPropertyItems({ smooth });

    expect(result).toContainEqual({
      key: "smooth",
      label: "Smooth",
      value: true,
      elementType: "Switch",
      groupId: "line",
      tooltip:
        "Transform your line chart into a spline chart. When you enable this option, it will connect the data points with smooth curves.",
    });
  });

  test("should return correct fetchAllRecords switch element when it is true", () => {
    const fetchAllRecords = true;
    const result = getPropertyItems({ fetchAllRecords });

    expect(result).toContainEqual({
      key: "fetchAllRecords",
      label: "Fetch All Records",
      value: true,
      elementType: "Switch",
      groupId: "table",
      tooltip: "Fetch all records in one go. Applying the 'Fetch All Records' option for generating reports with large data sets may decrease performance and result in longer loading times."
    });
  });

  test("should return correct smooth switch element when smooth is false", () => {
    const smooth = false;
    const result = getPropertyItems({ smooth });

    expect(result).toContainEqual({
      key: "smooth",
      label: "Smooth",
      value: false,
      elementType: "Switch",
      groupId: "line",
      tooltip:
        "Transform your line chart into a spline chart. When you enable this option, it will connect the data points with smooth curves.",
    });
  });

  describe("getAxisConfig", () => {
    const context = {
      axisFields: ["sum_travel_cost"]
    };
    const report = {};
    const axis = {
      fields: []
    }

    it("returns an object", () => {
      const result = getAxisConfig(context, report, axis, "x");
      expect(typeof result).toBe("object");
    });

    it("returns an object with a tickFormat property", () => {
      const result = getAxisConfig(context, report);
      expect(result).toHaveProperty("tickFormat");
    });

    describe('getAxisConfig', () => {
      it('should return axis configuration with domain when rangeData is present', () => {
        const context = {
          axisFields: ['Field1'],
        };

        const report = {};

        const axis = {
          fields: [
            {
              data: {
                name: 'Field1',
                minRange: "2000",
                maxRange: "50000"
              },
            },
          ],
        };

        const type = 'x';

        const result = getAxisConfig(context, report, axis, type);

        expect(result.domain).toBe(undefined);
      });


      it('should return axis configuration without domain when rangeData is not present', () => {
        const context = {
          axisFields: ['Field1'],
        };

        const report = {};

        const axis = {
          fields: [
            {
              data: {
                name: 'Field1',
              },
            },
          ],
        };

        const type = 'x';

        const result = getAxisConfig(context, report, axis, type);

        expect(result.domain).toBe(undefined);
      });

      it('should set axisData.show to false when hide is true', () => {
        const context = {
          axisFields: ['Field1'],
        };

        const report = {};

        const axis = {
          fields: [
            {
              data: {
                name: 'Field1',
                hide: true,
              },
            },
          ],
        };

        const type = 'x';

        const result = getAxisConfig(context, report, axis, type);

        expect(result.show).toBe(false);
      });

    });

  });

  describe('getAxisConfig', () => {
    const context = {
      axisFields: ['field1', 'field2']
    };

    const report = {}; // Provide a sample report object if needed

    const axis = {
      fields: [
        { data: { name: 'field1', hide: true } },
        { data: { name: 'field2', rotate: 90 } }
      ],
      showGridChartAxisName: true
    };

    const type = 'x';

    const data = [1, 2, 3, 4, 5];

    it('should return the correct axis configuration when domain data is available', () => {
      const expectedResult = {
        name: 'field1',
        labels: {
          rotation: 0,
        },
        tickFormat: expect.any(Function),
        "showAxisName": true,
        show: false,
      };

      const result = getAxisConfig(context, report, axis, type, data);

      expect(result).toEqual(expectedResult);
    });

  });

  describe("getNumberFormat", () => {
    test("should return empty string for empty input", () => {
      const result = getNumberFormat({ text: "" });
      expect(result).toBe("");
    });

    test("should return same input if not a number or string representation of a number", () => {
      const result = getNumberFormat({ text: "foo", format: {} });
      expect(result).toBe("foo");
    });

    test("should format integer with decimal place", () => {
      const result = getNumberFormat({
        text: 12345,
        format: { decimalPlace: 2 },
      });
      expect(result).toBe("12345.00");
    });

    test("should format float with decimal place", () => {
      const result = getNumberFormat({
        text: 123.45,
        format: { decimalPlace: 2 },
      });
      expect(result).toBe("123.45");
    });

    test("should format integer as percentage", () => {
      const result = getNumberFormat({
        text: 12345,
        format: { percentage: true, decimalPlace: 2, suffix: "%" },
      });
      expect(result).toBe("1234500.00%");
    });

    test("should format float as percentage", () => {
      const result = getNumberFormat({
        text: 0.12345,
        format: { percentage: true, decimalPlace: 2, suffix: "%" },
      });
      expect(result).toBe("12.35%");
    });

    test("should format integer with display unit", () => {
      const result = getNumberFormat({
        text: 1234567,
        format: { displayUnits: "m", decimalPlace: 2 },
      });
      expect(result).toBe("1.23M");
    });

    test("should format float with display unit and decimal place", () => {
      const result = getNumberFormat({
        text: "12345.6789",
        format: { displayUnits: "k", decimalPlace: 2 },
      });
      expect(result).toBe("12.35K");
    });

    test("should format number with thousand separator", () => {
      const result = getNumberFormat({
        text: 1234567,
        format: { thousandSperator: true },
      });
      expect(result).toBe("1,234,567");
    });

    test("should add prefix and suffix to number", () => {
      const result = getNumberFormat({
        text: 12345,
        format: { prefix: "$", suffix: " USD" },
      });
      expect(result).toBe("$12345 USD");
    });

    test("should format 0 with prefix and suffix", () => {
      const result = getNumberFormat({
        text: 0,
        format: { prefix: "Value: ", suffix: " units" },
      });
      expect(result).toBe("Value: 0 units");
    });

    test("should format '0' with prefix and suffix", () => {
      const result = getNumberFormat({
        text: "0",
        format: { prefix: "Value: ", suffix: " units" },
      });
      expect(result).toBe("Value: 0 units");
    });

    test("should return undefined for undefined input", () => {
      // 5698 bug use case
      const result = getNumberFormat({ text: undefined });
      expect(result).toBeUndefined();
    });
  });

  describe("getDataIndex", () => {
    it("returns the correct index when the input is present in the uniqueData array", () => {
      const uniqueData = [1, 2, 3, 4, 5];
      const text = "3";
      const expectedIndex = 2;
      const result = getDataIndex(uniqueData, text, "numeric");
      expect(result).toEqual(expectedIndex);
    });

    it("returns -1 when the input is not present in the uniqueData array", () => {
      const uniqueData = [1, 2, 3, 4, 5];
      const text = "6";
      const expectedIndex = -1;
      const result = getDataIndex(uniqueData, text);
      expect(result).toEqual(expectedIndex);
    });

    it("correctly handles non-integer input", () => {
      const uniqueData = [1, 2, 3, 4, 5];
      const text = "3.14";
      const expectedIndex = -1;
      const result = getDataIndex(uniqueData, text);
      expect(result).toEqual(expectedIndex);
    });

    it("correctly handles float type input", () => {
      const uniqueData = [1, 2, 3.14, 4, 5];
      const text = "3.14";
      const expectedIndex = 2;
      const result = getDataIndex(uniqueData, text, "numeric");
      expect(result).toEqual(expectedIndex);
    });

    it("correctly handles input that cannot be parsed to an integer", () => {
      const uniqueData = [1, 2, 3, 4, 5];
      const text = "foo";
      const expectedIndex = -1;
      const result = getDataIndex(uniqueData, text);
      expect(result).toEqual(expectedIndex);
    });

    test('should return the correct index for a string value in uniqueData array', () => {
      const uniqueData = ['apple', 'banana', 'orange', 'kiwi', 'mango'];
      const text = 'orange';
      const fieldType = 'text';
      const expectedIndex = 2;

      const result = getDataIndex(uniqueData, text, fieldType);

      expect(result).toEqual(expectedIndex);
    });

    test('should return -1 for a string value not present in uniqueData array', () => {
      const uniqueData = ['apple', 'banana', 'orange', 'kiwi', 'mango'];
      const text = 'strawberry';
      const fieldType = 'text';
      const expectedIndex = -1;

      const result = getDataIndex(uniqueData, text, fieldType);

      expect(result).toEqual(expectedIndex);
    });
  });

  describe("applyColor", () => {
    test("returns the color for the provided field when `showAll` is true and the field has a color", () => {
      const obj = { booking_platform: "Agent" };
      const layer = {
        type: "column",
        options: {
          xField: "booking_platform",
          yField: "sum_travel_cost",
          colorField: "booking_platform",
        },
      };
      const formatColor = {
        defaultColor: { r: 84, g: 108, b: 230, a: 1 },
        showAll: true,
        Agent: { r: 245, g: 166, b: 35, a: 1 },
        dataColors: [
          [
            "Agent",
            {
              r: 245,
              g: 166,
              b: 35,
              a: 1,
            },
          ],
        ],
      };

      const propertyColorField = "booking_platform";
      expect(applyColor(obj, layer, formatColor, propertyColorField)).toEqual(
        "rgba(245, 166, 35, 1)"
      );
    });

    test("returns the default color when `showAll` is true and the field does not have a color", () => {
      const obj = { booking_platform: "Unknown" };
      const layer = {
        type: "column",
        options: {
          xField: "booking_platform",
          yField: "sum_travel_cost",
          colorField: "booking_platform",
        },
      };
      const formatColor = {
        defaultColor: { r: 84, g: 108, b: 230, a: 1 },
        showAll: true,
        Agent: { r: 245, g: 166, b: 35, a: 1 },
      };
      const propertyColorField = "booking_platform";
      expect(applyColor(obj, layer, formatColor, propertyColorField)).toEqual(
        "rgba(84, 108, 230, 1)"
      );
    });

    test("returns the default color when `showAll` is false and the property color field is not used", () => {
      const obj = { booking_platform: "Agent" };
      const layer = {
        type: "column",
        options: {
          xField: "some_other_field",
          yField: "sum_travel_cost",
          colorField: "some_other_field",
        },
      };
      const formatColor = {
        defaultColor: { r: 84, g: 108, b: 230, a: 1 },
        showAll: false,
      };
      const propertyColorField = "booking_platform";
      expect(applyColor(obj, layer, formatColor, propertyColorField)).toEqual(
        "rgba(84, 108, 230, 1)"
      );
    });
  });

  describe("applyColor", () => {
    const obj = {
      booking_platform: "Agent",
    };
    const layer = {
      type: "column",
      options: {
        xField: "booking_platform",
        yField: "sum_travel_cost",
        colorField: "booking_platform",
      },
    };
    const formatColor = {
      defaultColor: {
        r: 84,
        g: 108,
        b: 230,
        a: 1,
      },
      minimum: {
        r: 245,
        g: 166,
        b: 35,
        a: 1,
      },
      maximum: {
        r: 84,
        g: 108,
        b: 230,
        a: 1,
      },
      showAll: true,
      dataColors: [
        ["formatColorField", "aba669f3-1aa1-4ecb-9628-032ea68ff2b0"],
        ["formatColorStyle", "fieldValue"],
        [
          "defaultColor",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        ["backgroundColor", false],
        ["showAll", true],
        [
          "Agent",
          {
            r: 245,
            g: 166,
            b: 35,
            a: 1,
          },
        ],
        [
          "Makemytrip",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Website",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
      ],
      formatColorField: "aba669f3-1aa1-4ecb-9628-032ea68ff2b0",
      formatColorStyle: "fieldValue",
      backgroundColor: false,
    };
    const propertyColorField = "booking_platform";

    test("returns the default color if showAll is false and the propertyColorField is not xField or yField", () => {
      const result = applyColor(obj, layer, formatColor, "someOtherField");
      expect(result).toEqual("rgba(245, 166, 35, 1)");
    });

    test("returns the default color if showAll is false and the propertyColorField is not xField or yField", () => {
      const result = applyColor(obj, layer, formatColor, "someOtherField");
      expect(result).toEqual("rgba(245, 166, 35, 1)");
    });

    test("returns the default color if showAll is true but the formatColor dataColors array does not contain the property value", () => {
      const objWithoutPlatform = {
        foo: "bar",
      };
      const result = applyColor(
        objWithoutPlatform,
        layer,
        formatColor,
        propertyColorField
      );
      expect(result).toEqual("rgba(84, 108, 230, 1)");
    });
  });

  describe("getGridChartColorFormatScheme", () => {
    const formatColor = {
      defaultColor: {
        r: 84,
        g: 108,
        b: 230,
        a: 1,
      },
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
      showAll: true,
      dataColors: [
        ["formatColorField", "b5dc0a3c-814e-414c-b0aa-8b23a7b8c257"],
        ["formatColorStyle", "fieldValue"],
        [
          "defaultColor",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        ["backgroundColor", false],
        ["showAll", true],
        [
          "Agent",
          {
            r: 245,
            g: 166,
            b: 35,
            a: 1,
          },
        ],
        [
          "Makemytrip",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
        [
          "Website",
          {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
        ],
      ],
      formatColorField: "b5dc0a3c-814e-414c-b0aa-8b23a7b8c257",
      formatColorStyle: "fieldValue",
      backgroundColor: false,
    };
    const domainData = ["Agent", "Makemytrip", "Website"];

    const combine = false;

    const output = [
      "rgba(245, 166, 35, 1)",
      "rgba(84, 108, 230, 1)",
      "rgba(84, 108, 230, 1)",
    ];

    it("should return an array with only the first 5 colors if combine is true and formatColor.showAll is true and dataColors has more than 5 items", () => {
      expect(
        getGridChartColorFormatScheme(formatColor, domainData, combine)
      ).toEqual(output);
    });
  });

  describe('getHTMLColorFormat', () => {
    test('returns the correct rgba string when given valid color values', () => {
      const colorValue = { a: 1, b: 128, g: 255, r: 0 }
      const result = getHTMLColorFormat(colorValue)
      expect(result).toEqual('rgba(0, 255, 128, 1)')
    })

    test('returns the correct rgba string when given default color values', () => {
      const result = getHTMLColorFormat()
      expect(result).toEqual('rgba(0, 0, 0, 1)')
    })

    test('returns the correct rgba string when given a float alpha value', () => {
      const colorValue = { a: 0.5, b: 128, g: 255, r: 0 }
      const result = getHTMLColorFormat(colorValue)
      expect(result).toEqual('rgba(0, 255, 128, 0.5)')
    })
  })

  test("get fields when type is axisRange", () => {
    const report = {
      fields: [
        {
          "column": "travel_details.travel_cost",
          "label": "sum_travel_cost",
          "id": "c90a3610-4cf4-4a73-b8a7-9bff8a9f0c98",
          "type": {
            "backendDataType": "java.lang.Integer",
            "dataType": "numeric"
          },
          "autogen_alias": "sum_travel_cost",
          "isNormalTable": true,
          "tableAlias": "travel_details",
          "aggregate": [
            "db.generic.aggregate.sum"
          ],
          "orderByColumn": false,
          "showOrderByColumn": false,
          "addedAs": "column",
          "floatingType": "",
          "functionsDefinition": "",
          "applyBeforeAggregate": false,
          "hiddenIncludeInResultSet": false,
          "metaDataAlias": "travel_cost"
        },
        {
          "column": "travel_details.travel_cost",
          "label": "sum_travel_cost_1",
          "id": "6575a522-2ca9-4f2b-93c5-48944175ca32",
          "type": {
            "backendDataType": "java.lang.Integer",
            "dataType": "numeric"
          },
          "autogen_alias": "sum_travel_cost_1",
          "isNormalTable": true,
          "tableAlias": "travel_details",
          "aggregate": [
            "db.generic.aggregate.sum"
          ],
          "orderByColumn": false,
          "showOrderByColumn": false,
          "addedAs": "column",
          "floatingType": "",
          "functionsDefinition": "",
          "applyBeforeAggregate": false,
          "hiddenIncludeInResultSet": false,
          "metaDataAlias": "travel_cost"
        },
        {
          "column": "travel_details.travel_date",
          "label": "travel_date",
          "id": "c68104ab-194e-4dac-bd93-85b5d3d94555",
          "type": {
            "backendDataType": "java.sql.Timestamp",
            "dataType": "dateTime"
          },
          "autogen_alias": "travel_date",
          "isNormalTable": true,
          "tableAlias": "travel_details",
          "groupBy": [
            "db.generic.groupBy.group"
          ],
          "orderByColumn": false,
          "showOrderByColumn": false,
          "addedAs": "color",
          "floatingType": "discrete",
          "functionsDefinition": "",
          "applyBeforeAggregate": false,
          "hiddenIncludeInResultSet": false,
          "metaDataAlias": "travel_date"
        },
        {
          "column": "travel_details.source",
          "label": "source",
          "id": "d4f9dc30-bf8d-4f6a-bae3-6a33aefb29d3",
          "type": {
            "backendDataType": "java.lang.String",
            "dataType": "text"
          },
          "autogen_alias": "source",
          "isNormalTable": true,
          "tableAlias": "travel_details",
          "groupBy": [
            "db.generic.groupBy.group"
          ],
          "orderByColumn": false,
          "showOrderByColumn": false,
          "addedAs": "label",
          "floatingType": "discrete",
          "functionsDefinition": "",
          "applyBeforeAggregate": false,
          "hiddenIncludeInResultSet": false,
          "metaDataAlias": "source"
        },
        {
          "column": "travel_details.travel_cost",
          "label": "sum_travel_cost",
          "id": "e769b5a3-1279-4f45-9e91-29a6216aebd1",
          "type": {
            "backendDataType": "java.lang.Integer",
            "dataType": "numeric"
          },
          "autogen_alias": "sum_travel_cost",
          "isNormalTable": true,
          "tableAlias": "travel_details",
          "aggregate": [
            "db.generic.aggregate.sum"
          ],
          "orderByColumn": false,
          "showOrderByColumn": false,
          "addedAs": "size",
          "floatingType": "",
          "functionsDefinition": "",
          "applyBeforeAggregate": false,
          "hiddenIncludeInResultSet": false,
          "metaDataAlias": "travel_cost"
        }
      ],
      "filters": [],
      "defaultValueDisplayMap": {},
      "editingField": null,
      "activeMark": "ede88fa8-7f80-432f-979e-5981fe6d4cfd",
      "activeTool": "7",
      "scripts": [
        {
          "id": "pre-execution",
          "value": "",
          "title": "Pre Execution"
        },
        {
          "id": "pre-fetch",
          "value": "",
          "title": "Pre Fetch"
        },
        {
          "id": "post-fetch",
          "value": "",
          "title": "Post Fetch"
        },
        {
          "id": "post-execution",
          "value": "",
          "title": "Post Execution"
        }
      ],
      "selectedScript": "pre-execution",
      "styles": "",
      "sqlString": "",
      "selectedType": "Table",
      "customStyles": "",
      "customScripts": [],
      "analytics": [
        {
          "value": false,
          "key": "subTotals",
          "label": "Row Sub Totals"
        }
      ],
    }

    const result = [
      {
        "key": "c90a3610-4cf4-4a73-b8a7-9bff8a9f0c98",
        "label": "sum_travel_cost"
      },
      {
        "key": "6575a522-2ca9-4f2b-93c5-48944175ca32",
        "label": "sum_travel_cost_1"
      },

    ]

    expect(getFields(report, false, "axisRange")).toEqual(result);
  });

  describe("getRangeTooltipInfo", () => {
    it("should return tooltip info for numeric data type and index 1", () => {
      const dataType = "numeric";
      const index = 1;
      const tooltipInfo = getRangeTooltipInfo(dataType, index);
      const expectedTooltipInfo = "Range/domain setting value lets you enter a value in an input box to define a range of chart. Give lower limit of the range in this input box (Percentage charts do not support range options).";
      expect(tooltipInfo).toBe(expectedTooltipInfo);
    });

    it("should return tooltip info for numeric data type and index 2", () => {
      const dataType = "numeric";
      const index = 2;
      const tooltipInfo = getRangeTooltipInfo(dataType, index);
      const expectedTooltipInfo = "Range/domain setting value lets you enter a value in an input box to define a range of chart. Give upper limit of the range in this input box (Percentage charts do not support range options).";
      expect(tooltipInfo).toBe(expectedTooltipInfo);
    });

    it("should return tooltip info for dateTime or date data type and index 1", () => {
      const dataTypes = ["dateTime", "date"];
      const index = 1;
      dataTypes.forEach((dataType) => {
        const tooltipInfo = getRangeTooltipInfo(dataType, index);
        const expectedTooltipInfo = "Range/domain setting value lets you enter a value in an input box to define a range of chart. Give lower limit of the date range in this input box.";
        expect(tooltipInfo).toBe(expectedTooltipInfo);
      });
    });

    it("should return tooltip info for dateTime or date data type and index 2", () => {
      const dataTypes = ["dateTime", "date"];
      const index = 2;
      dataTypes.forEach((dataType) => {
        const tooltipInfo = getRangeTooltipInfo(dataType, index);
        const expectedTooltipInfo = "Range/domain setting value lets you enter a value in an input box to define a range of chart. Give upper limit of the date range in this input box.";
        expect(tooltipInfo).toBe(expectedTooltipInfo);
      });
    });

    it("should return an empty string for unknown dataType or index", () => {
      const dataType = "unknown";
      const index = 3;
      const tooltipInfo = getRangeTooltipInfo(dataType, index);
      expect(tooltipInfo).toBe("");
    });
  });

  describe('getPropertyAxisRange', () => {
    test('should return null when no valid range data is provided', () => {
      const result = getPropertyAxisRange({}, 'lineChart');
      expect(result).toBeNull();
    });

    test('should return null when dataType is not "numeric", "date", or "dateTime"', () => {
      const field = {
        data: {
          minRange: 0,
          maxRange: 100,
          dataType: 'string',
        },
      };
      const result = getPropertyAxisRange(field, 'lineChart');
      expect(result).toBeNull();
    });

    test('should return null when chartType is not "antChart" or "grid"', () => {
      const field = {
        data: {
          minRange: 0,
          maxRange: 100,
          dataType: 'numeric',
        },
      };
      const result = getPropertyAxisRange(field, 'barChart');
      expect(result).toEqual({ "max": 100, "min": 0 });
    });

    test('should return correct rangeData for date dataType and antChart', () => {
      const field = {
        data: {
          minRange: '2023-01-01',
          maxRange: '2023-12-31',
          dataType: 'date',
        },
      };
      const result = getPropertyAxisRange(field, 'antChart');
      expect(result).toEqual({
        min: '2023-01-01',
        max: '2023-12-31',
        type: 'time',
      });
    });

    test('should return correct rangeData for dateTime dataType and grid', () => {
      const field = {
        data: {
          minRange: '2023-01-01 00:00:00.000',
          maxRange: '2023-01-02 23:59:59.999',
          dataType: 'dateTime',
        },
      };
      const result = getPropertyAxisRange(field, 'grid');
      expect(result).toEqual({
        min: '2023-01-01 00:00:00.000',
        max: '2023-01-02 23:59:59.999',
      });
    });
  });

  describe('getUnFormmatedText', () => {
    it('should return formatted date when fieldType is "date"', () => {
      const text = '2021-06-15T15:57:00.000Z';
      const fieldType = 'date';
      const expectedOutput = '2021-06-15';

      const result = getUnFormmatedText(text, fieldType, "label");

      expect(result).toBe(expectedOutput);
    });

    test('should format timestamp as date', () => {
      const timestamp = 1623713420000;
      const fieldType = 'date';
      const expectedDate = moment(timestamp).format('YYYY-MM-DD');
      const unformattedText = getUnFormmatedText(timestamp, fieldType, "label");
      expect(unformattedText).toBe(expectedDate);
    });

    test('should format timestamp as datetime with milliseconds', () => {
      const timestamp = 1623713420000;
      const fieldType = 'dateTime';
      const expectedDatetime = moment(timestamp).format('YYYY-MM-DD HH:mm:ss.S');
      const unformattedText = getUnFormmatedText(timestamp, fieldType, "label");
      expect(unformattedText).toBe(expectedDatetime);
    });

    test('should return unchanged text for unrecognized fieldType', () => {
      const text = 'Hello World';
      const fieldType = 'unknown';
      const unformattedText = getUnFormmatedText(text, fieldType, "label");
      expect(unformattedText).toBe(text);
    });

    it('should return the original text when fieldType is neither "date" nor "datetime"', () => {
      const text = 'Some text';
      const fieldType = 'other';
      const expectedOutput = 'Some text';

      const result = getUnFormmatedText(text, fieldType);

      expect(result).toBe(expectedOutput);
    });
  });




});

describe("test getSingleDimensionData", () => {
  test('returns single dimension data for array with nested arrays and objects', () => {
    const data = [[1, { name: 'John', getMembers: () => ['John'] }], [2, { name: 'Jane', getMembers: () => ['Jane'] }]];
    const result = getSingleDimensionData(data);
    expect(result).toEqual([1, 'John', 2, 'Jane']);
  });

  test('returns same data for array with no nested arrays or objects', () => {
    const data = [1, 2, 3];
    const result = getSingleDimensionData(data);
    expect(result).toEqual([1, 2, 3]);
  });

  test('returns empty array for empty array input', () => {
    const data = [];
    const result = getSingleDimensionData(data);
    expect(result).toEqual([]);
  });

  test('returns input data for null or undefined input', () => {
    const data = null;
    const result = getSingleDimensionData(data);
    expect(result).toBeNull();
  });
})

describe(' test rgbaToHex', () => {
  it('should convert rgba to hex', () => {
    expect(rgbaToHex('rgba(255, 0, 0, 1)')).toEqual('#ff0000');
    expect(rgbaToHex('rgba(0, 255, 0, 1)')).toEqual('#00ff00');
    expect(rgbaToHex('rgba(0, 0, 255, 1)')).toEqual('#0000ff');
    expect(rgbaToHex('rgba(255, 255, 255, 1)')).toEqual('#ffffff');
  });

  it('should handle invalid input', () => {
    expect(rgbaToHex('')).toEqual('');
    expect(rgbaToHex('rgba(255, 0, 0)')).toEqual("#ff0000");
    expect(rgbaToHex('rgb(255, 0, 0)')).toEqual("#ff0000");
  });
});

describe('test getDefaultAFDataTypeValues', () => {
  it('should return numberItemsCreateMode for numeric data type', () => {
    const result = getDefaultAFDataTypeValues('numeric');
    expect(result).toEqual({
      thousandSperator: true,
      decimalPlace: 0,
      prefix: "",
      suffix: "",
      displayUnits: "None",
      percentage: false,
      numberCustom: "",
      apply: ["pane", "tooltip", "label", "axis", "actions", "legend"],
      isApplyClicked: true,
      autoFormatting: true,
      enableCustomFormatting: false
    });
  });

  it('should return dateItemsCreateMode for date data type', () => {
    const result = getDefaultAFDataTypeValues('date');
    expect(result).toEqual({
      day: "dayNumWithZero",
      week: "none",
      month: "monthNumWithZero",
      quarter: "none",
      year: "4digit",
      dateSeperator: "-",
      dateCustom: "",
      apply: ["pane", "tooltip", "label", "axis", "actions", "legend"],
      autoFormatting: true,
      isApplyClicked: true,
      enableCustomFormatting: false
    });
  });

  it('should return timeItemsCreateMode for time data type', () => {
    const result = getDefaultAFDataTypeValues('time');
    expect(result).toEqual({
      hour: "24hr",
      minute: "mintuesNumber",
      second: "secondsNumber",
      milliSecond: "milliSecondsNumber",
      timeSeperator: ":",
      timeCustom: "",
      apply: ["pane", "tooltip", "label", "axis", "actions", "legend",],
      autoFormatting: true,
      isApplyClicked: true,
      enableCustomFormatting: false 
    });
  });

  it('should return combined date and time items for dateTime data type', () => {
    const result = getDefaultAFDataTypeValues('dateTime');
    expect(result).toEqual({
      day: "dayNumWithZero",
      week: "none",
      month: "monthNumWithZero",
      quarter: "none",
      year: "4digit",
      dateSeperator: "-",
      dateCustom: "",
      apply: ["pane", "tooltip", "label", "axis", "actions", "legend"],
      autoFormatting: true,
      isApplyClicked: true,
      hour: "24hr",
      minute: "mintuesNumber",
      second: "secondsNumber",
      milliSecond: "milliSecondsNumber",
      timeSeperator: ":",
      timeCustom: "",
      enableCustomFormatting: false 
    });
  });

  it('should return an empty object for unknown data type', () => {
    const result = getDefaultAFDataTypeValues('unknown');
    expect(result).toEqual({});
  });
});






describe(' test generateColorRanges fn', () => {

  const red = { r: 255, g: 0, b: 0, a: 1 };
  const blue = { r: 0, g: 0, b: 255, a: 1 };

  const baseFormat = (steps, extra = {}) => ({
    minimum: red,
    maximum: blue,
    steps,
    ...extra,
  });


  it('returns [] when data is empty', () => {
    expect(generateColorRanges([], baseFormat(4))).toEqual([]);
  });

  it('returns [] when steps is 0', () => {
    expect(generateColorRanges([1, 2, 3], baseFormat(0))).toEqual([]);
  });

  it('returns [] when steps is undefined', () => {
    expect(generateColorRanges([1, 2, 3], { minimum: red, maximum: blue })).toEqual([]);
  });

  it('returns [] when formatColor is null', () => {
    expect(generateColorRanges([1, 2, 3], null)).toEqual([]);
  });

  it('returns [] when formatColor is undefined', () => {
    expect(generateColorRanges([1, 2, 3])).toEqual([]);
  });


  it('returns (steps - 1) ranges for even steps', () => {
    const result = generateColorRanges([0, 100], baseFormat(4));
    expect(result).toHaveLength(4);
  });

  it('returns steps ranges for odd steps (includes center)', () => {
    const result = generateColorRanges([0, 100], baseFormat(5));
    expect(result).toHaveLength(5);
  });

  it('handles steps=2', () => {
    const result = generateColorRanges([0, 100], baseFormat(2));
    expect(result).toHaveLength(2);
  });


  it('first range starts at minValue', () => {
    const result = generateColorRanges([-10, 90], baseFormat(4));
    expect(result[0].min).toBe(-90);
  });

  it('first range starts at 0 for all-positive data (min of data)', () => {
    const data = [10, 50, 90];
    const result = generateColorRanges(data, baseFormat(4));
    expect(result[0].min).toBeCloseTo(10);
  });

  it('last range ends at maxValue', () => {
    const data = [10, 50, 90];
    const result = generateColorRanges(data, baseFormat(4));
    expect(result[result.length - 1].max).toBeCloseTo(90);
  });

  it('ranges are contiguous (each max === next min)', () => {
    const data = [0, 100];
    const result = generateColorRanges(data, baseFormat(4));
    for (let i = 0; i < result.length - 1; i++) {
      if (result[i].min === result[i].max) continue;
      expect(result[i].max).toBeCloseTo(result[i + 1].min);
    }
  });


  it('first range color is startColor (red) when steps produce ratio=0', () => {
    const result = generateColorRanges([0, 100], baseFormat(2));
    expect(result[0].color).toBe('rgba(255, 0, 0, 1)');
  });

  it('last range color is endColor (blue) when steps produce ratio=1', () => {
    const result = generateColorRanges([0, 100], baseFormat(2));
    expect(result[result.length - 1].color).toBe('rgba(0, 0, 255, 1)');
  });

  it('each range has a color string in rgba(...) format', () => {
    const result = generateColorRanges([0, 100], baseFormat(4));
    result.forEach(r => {
      expect(r.color).toMatch(/^rgba\(\d+, \d+, \d+, [\d.]+\)$/);
    });
  });

  it('interpolates alpha correctly', () => {
    const startWithAlpha = { r: 0, g: 0, b: 0, a: 0 };
    const endWithAlpha = { r: 255, g: 0, b: 0, a: 1 };
    const fmt = { minimum: startWithAlpha, maximum: endWithAlpha, steps: 2 };
    const result = generateColorRanges([0, 100], fmt);
    expect(result[0].color).toBe('rgba(0, 0, 0, 0)');
    expect(result[result.length - 1].color).toBe('rgba(255, 0, 0, 1)');
  });


  it('sets min to -largestAbsolute and max to largestAbsolute', () => {
    const data = [-80, 20, 50];
    const result = generateColorRanges(data, baseFormat(4));
    expect(result[0].min).toBeCloseTo(-80);
    expect(result[result.length - 1].max).toBeCloseTo(80);
  });

  it('first range starts at negative value', () => {
    const data = [-100, 0, 100];
    const result = generateColorRanges(data, baseFormat(4));
    expect(result[0].min).toBe(-100);
  });

  it('center range has min === max', () => {
    const data = [0, 100];
    const result = generateColorRanges(data, baseFormat(3));
    const center = result[Math.floor(result.length / 2)];
    expect(center.min).toBe(center.max);
  });

  it('center value is midpoint of min and max', () => {
    const data = [0, 100];
    const result = generateColorRanges(data, baseFormat(3));
    const center = result[1];
    expect(center.min).toBe(50);
  });

  it('uses custom minValue, maxValue, centerValue when enableAdvanceSteps=true', () => {
    const data = [0, 100];
    const fmt = {
      ...baseFormat(4),
      enableAdvanceSteps: true,
      minValue: -200,
      maxValue: 200,
      centerValue: 0,
    };
    const result = generateColorRanges(data, fmt);
    expect(result[0].min).toBeCloseTo(-200);
    expect(result[result.length - 1].max).toBeCloseTo(200);
  });

  it('ignores data min/max when enableAdvanceSteps=true', () => {
    const data = [10, 90];
    const fmt = {
      ...baseFormat(4),
      enableAdvanceSteps: true,
      minValue: 0,
      maxValue: 1000,
      centerValue: 500,
    };
    const result = generateColorRanges(data, fmt);
    expect(result[0].min).toBeCloseTo(0);
    expect(result[result.length - 1].max).toBeCloseTo(1000);
  });


  it('each item has min, max, and color properties', () => {
    const result = generateColorRanges([0, 100], baseFormat(4));
    result.forEach(item => {
      expect(item).toHaveProperty('min');
      expect(item).toHaveProperty('max');
      expect(item).toHaveProperty('color');
    });
  });

  it('min <= max for all non-center ranges', () => {
    const result = generateColorRanges([0, 100], baseFormat(4));
    result.forEach(item => {
      expect(item.min).toBeLessThanOrEqual(item.max);
    });
  });

});


describe('test getMinMax fn', () => {
  it('returns min/max for all-positive data', () => {
    expect(getMinMax([1, 5, 3])).toEqual({ min: 1, max: 5 });
  });

  it('returns symmetric range for data with negatives', () => {
    expect(getMinMax([-80, 20])).toEqual({ min: -80, max: 80 });
  });

  it('handles single element positive', () => {
    expect(getMinMax([42])).toEqual({ min: 42, max: 42 });
  });

  it('handles all-negative data', () => {
    // largest absolute is 90, so min=-90, max=90
    expect(getMinMax([-10, -90, -30])).toEqual({ min: -90, max: 90 });
  });

  it('handles zero in data', () => {
    expect(getMinMax([0, 50])).toEqual({ min: 0, max: 50 });
  });
});