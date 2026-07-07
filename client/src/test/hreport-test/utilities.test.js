import { Liquid } from "liquidjs";
import { getMarkTitle } from "../../components/hi-reports/hi-editing-area/components/values/utils";
import { checkIsCustomContinuousField, getCustomFieldDataType, getFields, getHTMLColorFormat, getPropertyAxisRange, getPropertyItems } from "../../components/hi-reports/hi-editing-area/utils/property-utils";
import {
  applyProperties,
  applyStylesToMuzeTicks,
  calcFunc,
  calculateRadialPercentage,
  constructGeoJsonData,
  createMaxValue,
  generateFormatColorRamp,
  getFormattedDataForMap,
  getGeometryForMap,
  getMapChartValues,
  getMapColorFieldScale,
  getMapPropertiesPayload,
  getNewDateFormat,
  getNewTimeFormat,
  getNumberFormat,
  handleApplyCustomFormatting,
  isColorFieldDimension,
  prepareLongitudeLatitudeData,
  tooltipTemplateLiquidJS
} from "../../components/hi-reports/hi-viz-area/utils/utillities";
import { getSaveData, prepareFetchReportStateResponse } from "../../components/hi-reports/utils/base";
import { getVisulisationState } from "./hreport.request.mock.js";
import { expectedReportDataWithFeedbackFormat, mapChartExpectedDataWhenFormatApplied, mapChartExpectedDataWhenFormatNotApplied, mapChartMockdataWhenFormatApplied, mapChartMockdataWhenFormatNotApplied, radialEnableData, reportDataWithFeedbackFormat } from './utilities-mockdata.js';
import { getSaveFormData } from "./utils/mock.data";
import "core-js/stable";
import { wrapSpecialVariables } from "../../components/hi-dashboard-designer/utils/common-functions.js";
import { describe } from "jest-circus";

describe("Helical Report Utilities", () => {
  test("jest example", async () => {
    expect(1 + 1).toBeTruthy();
  });
  test("save formdata creation", async () => {
    let avtiveReport = getVisulisationState("Table").reports[0];
    expect(getSaveData(avtiveReport)).toEqual(getSaveFormData());
  });

  test("creating max  value for mark", async () => {
    let data = [
      { location: "Agra", sum_latitude: 27.17667 },
      { location: "Ahmedabad", sum_latitude: 23.022505 },
      { location: "Ambala", sum_latitude: 30.378179 },
      { location: "Amritsar", sum_latitude: 31.633979 },
      { location: "Amsterdam", sum_latitude: 52.370216 },
      { location: "Auckland", sum_latitude: -36.84846 },
      { location: "Aurangabad", sum_latitude: 19.876165 },
      { location: "Bangalore", sum_latitude: 12.971599 },
      { location: "Beijing", sum_latitude: 39.904211 },
      { location: "Bhopal", sum_latitude: 23.259933 },
    ];
    expect(createMaxValue("sum_latitude", data)).toEqual(52.370216);
  });

  test("creating max  value for mark", async () => {
    let data = [
      { booking_platform: "Agent", sum_travel_cost: 3641245 },
      { booking_platform: "Makemytrip", sum_travel_cost: 6719588 },
      { booking_platform: "Website", sum_travel_cost: 8173137 },
    ];
    expect(createMaxValue("sum_travel_cost", data)).toEqual(8173137);
  });

  describe("Hreport Format", () => {
    test("Format Number Type", () => {
      const text = 8173137;
      const format = {
        percentage: true,
        suffix: "%",
      };
      expect(getNumberFormat({ text, format })).toEqual("817313700%");
    });

    test("Number 0", () => {
      const text = 0;
      const format = {
        suffix: "%",
        decimalPlace: 2,
      };
      expect(getNumberFormat({ text, format })).toEqual("0.00%");
    });

    test("Display Number and and decimal place", () => {
      const text = 8173137;
      const format = {
        displayUnits: "m",
        decimalPlace: 3,
      };
      expect(getNumberFormat({ text, format })).toEqual("8.173M");
    });

    test("Display Number and and decimal place", () => {
      const text = 8173137;
      const format = {
        thousandSperator: true,
      };
      expect(getNumberFormat({ text, format })).toEqual("8,173,137");
    });

    test("prefix and suffix", () => {
      const text = 8173137;
      const format = {
        prefix: "$",
        suffix: "/-",
      };
      expect(getNumberFormat({ text, format })).toEqual("$8173137/-");
    });

    test("Time format 12 Hour", () => {
      const text = "14 40 24";
      const format = {
        hour: "none",
        minute: "none",
        second: "none",
        timeSeperator: ":",
      };
      expect(getNewTimeFormat({ text, format })).toEqual("");
    });

    test("Time format 24 Hour", () => {
      const text = "14 40 24";
      const format = {
        hour: "24hr",
        milliSecond: "milliSecondsNumber"
      };
      expect(getNewTimeFormat({ text, format })).toEqual("14.0");
    });

    test("Apply Properties", () => {
      const fieldType = "dateTime";
      const text = "2015-05-09 10:34:00.0";
      const format = {
        day: "dayNumWithZero",
        week: "none",
        month: "monthNumWithZero",
        quarter: "none",
        year: "4digit",
        dateSeperator: "-",
        hour: "24hr",
        minute: "mintuesNumber",
        second: "secondsNumber",
        milliSecond: "none",
        timeSeperator: ":",
      };
      expect(applyProperties({ fieldType, text, format })).toEqual("2015-05-09 10:34:00  ");
    });

    test("Apply Properties day none", () => {
      const fieldType = "dateTime";
      const text = "2015-05-09 10:34:00.0";
      const format = {
        day: "none",
        week: "none",
        month: "monthNumWithZero",
        quarter: "none",
        year: "4digit",
        dateSeperator: "-",
        hour: "24hr",
        minute: "mintuesNumber",
        second: "secondsNumber",
        milliSecond: "none",
        timeSeperator: ":",
      };
      expect(applyProperties({ fieldType, text, format })).toEqual("2015-05 10:34:00  ");
    });

    test("Apply Properties month none", () => {
      const fieldType = "dateTime";
      const text = "2015-05-09 10:34:00.0";
      const format = {
        day: "none",
        week: "none",
        month: "none",
        quarter: "none",
        year: "4digit",
        dateSeperator: "-",
        hour: "24hr",
        minute: "mintuesNumber",
        second: "secondsNumber",
        milliSecond: "none",
        timeSeperator: ":",
      };
      expect(applyProperties({ fieldType, text, format })).toEqual("2015 10:34:00  ");
    });

    test("Apply Properties year none", () => {
      const fieldType = "dateTime";
      const text = "2015-05-09 10:34:00.0";
      const format = {
        day: "none",
        week: "none",
        month: "none",
        quarter: "none",
        year: "none",
        dateSeperator: "-",
        hour: "24hr",
        minute: "mintuesNumber",
        second: "secondsNumber",
        milliSecond: "none",
        timeSeperator: ":",
      };
      expect(applyProperties({ fieldType, text, format })).toEqual(" 10:34:00  ");
    });

    test("Apply Properties hour none", () => {
      const fieldType = "dateTime";
      const text = "2015-05-09 10:34:00.0";
      const format = {
        day: "none",
        week: "none",
        month: "none",
        quarter: "none",
        year: "none",
        dateSeperator: "-",
        hour: "none",
        minute: "mintuesNumber",
        second: "secondsNumber",
        milliSecond: "none",
        timeSeperator: ":",
      };
      expect(applyProperties({ fieldType, text, format })).toEqual(" 34:00  ");
    });

    test("Apply Properties minutes none", () => {
      const fieldType = "dateTime";
      const text = "2015-05-09 10:34:00.0";
      const format = {
        day: "none",
        week: "none",
        month: "none",
        quarter: "none",
        year: "none",
        dateSeperator: "-",
        hour: "none",
        minute: "none",
        second: "secondsNumber",
        milliSecond: "none",
        timeSeperator: ":",
      };
      expect(applyProperties({ fieldType, text, format })).toEqual(" 00  ");
    });

    test("Apply Properties seconds none", () => {
      const fieldType = "dateTime";
      const text = "2015-05-09 10:34:00.0";
      const format = {
        day: "none",
        week: "none",
        month: "none",
        quarter: "none",
        year: "none",
        dateSeperator: "-",
        hour: "none",
        minute: "none",
        second: "none",
        milliSecond: "none",
        timeSeperator: ":",
      };
      expect(applyProperties({ fieldType, text, format })).toEqual("   ");
    });

    test("Apply Properties seconds number", () => {
      const fieldType = "dateTime";
      const text = "2015-05-09 10:34:00.0";
      const format = {
        day: "none",
        week: "none",
        month: "none",
        quarter: "none",
        year: "none",
        dateSeperator: "-",
        hour: "none",
        minute: "none",
        second: "secondsNumber",
        milliSecond: "none",
        timeSeperator: ":",
      };
      expect(applyProperties({ fieldType, text, format })).toEqual(" 00  ");
    });

    test("Apply Properties 24 hours", () => {
      const fieldType = "dateTime";
      const text = "2015-05-09 10:34:00.0";
      const format = {
        day: "none",
        week: "none",
        month: "none",
        quarter: "none",
        year: "none",
        dateSeperator: "-",
        hour: "24hr",
        minute: "mintuesNumber",
        second: "secondsNumber",
        milliSecond: "none",
        timeSeperator: ":",
        timeCustom: "",
      };
      expect(applyProperties({ fieldType, text, format })).toEqual(" 10:34:00  ");
    });

    test("Apply Properties Quater Abbrevation", () => {
      const fieldType = "dateTime";
      const text = "2015-05-09 10:34:00.0";
      const format = {
        day: "none",
        week: "none",
        month: "none",
        quarter: "quaterAbbrevation",
        year: "none",
        dateSeperator: "-",
        hour: "24hr",
        minute: "mintuesNumber",
        second: "secondsNumber",
        milliSecond: "none",
        timeSeperator: ":",
        timeCustom: "",
      };
      expect(applyProperties({ fieldType, text, format })).toEqual(" 10:34:00  Q2");
    });

    test("Apply Properties Week Abbrevation", () => {
      const fieldType = "dateTime";
      const text = "2015-05-09 10:34:00.0";
      const format = {
        day: "none",
        week: "weekAbbrevation",
        month: "none",
        quarter: "quaterAbbrevation",
        year: "none",
        dateSeperator: "-",
        hour: "24hr",
        minute: "mintuesNumber",
        second: "secondsNumber",
        milliSecond: "milliSecondsNumber",
        timeSeperator: ":",
        timeCustom: "",
      };
      expect(applyProperties({ fieldType, text, format })).toEqual(" 10:34:00.0 W19 Q2");
    });

    test("Apply Properties Week full and dateSeperator(/)", () => {
      const fieldType = "dateTime";
      const text = "2015-05-09 10:34:00.0";
      const format = {
        day: "none",
        week: "weekFull",
        month: "none",
        quarter: "quaterAbbrevation",
        year: "none",
        dateSeperator: "/",
        hour: "24hr",
        minute: "mintuesNumber",
        second: "secondsNumber",
        milliSecond: "milliSecondsNumber",
        timeSeperator: ":",
        timeCustom: "",
      };
      expect(applyProperties({ fieldType, text, format })).toEqual(" 10:34:00.0 Week19 Q2");
    });

    test("Apply Properties 12 Hour and dateSeperator(.)", () => {
      const fieldType = "dateTime";
      const text = "2015-05-09 10:34:00.0";
      const format = {
        day: "dayNumber",
        week: "none",
        month: "monthAbbrevation",
        quarter: "none",
        year: "4digit",
        dateSeperator: ".",
        hour: "12hr",
        minute: "mintuesNumber",
        second: "secondsNumber",
        milliSecond: "milliSecondsNumber",
        timeSeperator: ":",
        isApplyClicked: true,
      };
      expect(applyProperties({ fieldType, text, format })).toEqual("2015.May.9 10:34:00.0 am  ");
    });

    test("Apply Properties on datetime timestamp", () => {
      const fieldType = "dateTime";
      const text = 1672727258819;
      const format = {
        day: "dayNumber",
        week: "none",
        month: "monthAbbrevation",
        quarter: "none",
        year: "4digit",
        dateSeperator: ".",
        hour: "12hr",
        minute: "mintuesNumber",
        second: "secondsNumber",
        milliSecond: "milliSecondsNumber",
        timeSeperator: ":",
        isApplyClicked: true,
      };
      expect(applyProperties({ fieldType, text, format })).toEqual("1672727258819  ");
    });

    test("Color Property Fields", () => {
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
            "addedAs": "row",
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
            "addedAs": "color",
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
        {
          "key": "c68104ab-194e-4dac-bd93-85b5d3d94555",
          "label": "travel_date"
        },
        {
          "key": "d4f9dc30-bf8d-4f6a-bae3-6a33aefb29d3",
          "label": "source"
        }
      ]

      expect(getFields(report, "color")).toEqual(result);
    });

    test("Radial Enable Property Test", () => {
      const items = getPropertyItems(radialEnableData)
      const radialitem = items.find((item) => item.key === "showRadial")
      expect(radialitem.value).toBeTruthy()
    })

    describe('getPropertyAxisRange', () => {
      it('returns null if no field data is provided', () => {
        const result = getPropertyAxisRange(undefined, 'antChart')
        expect(result).toBeNull()
      })

      it('returns null if field data is missing min or max range', () => {
        const field = { data: { dataType: 'numeric' } }
        const result = getPropertyAxisRange(field, 'antChart')
        expect(result).toBeNull()
      })

      it('correctly parses numeric data', () => {
        const field = { data: { dataType: 'numeric', minRange: '10', maxRange: '20' } }
        const result = getPropertyAxisRange(field, 'antChart')
        expect(result).toEqual({ min: 10, max: 20 })
      })

      it('correctly parses date/time data for Ant charts', () => {
        const field = { data: { dataType: 'date', minRange: '2022-01-01', maxRange: '2022-12-31' } }
        const result = getPropertyAxisRange(field, 'antChart')
        expect(result).toEqual({ min: '2022-01-01', max: '2022-12-31', type: 'time' })
      })

      it('correctly parses date/time data for non-Ant charts', () => {
        const field = { data: { dataType: 'dateTime', minRange: '2022-01-01 12:00:00', maxRange: '2022-12-31 12:00:00' } }
        const result = getPropertyAxisRange(field, 'otherChart')
        expect(result).toEqual({ min: '2022-01-01', max: '2022-12-31' })
      })
      it('returns range data for dateTime data type and chartType "grid"', () => {
        const field = {
          data: {
            dataType: 'dateTime',
            minRange: '2023-05-20 10:00:00.000',
            maxRange: '2023-05-20 12:00:00.000'
          }
        };
        const chartType = 'grid';
        const expectedRangeData = {
          min: '2023-05-20 10:00:00.000',
          max: '2023-05-20 12:00:00.000'
        };
        expect(getPropertyAxisRange(field, chartType)).toEqual(expectedRangeData);
      });
    })

    describe('getMarkTitle', () => {
      it('returns "Text" when selectedType is "GridChart", subVizType is "text", and markType is "label"', () => {
        const selectedType = 'GridChart';
        const subVizType = 'text';
        const markType = 'label';
        const result = getMarkTitle(selectedType, subVizType, markType);
        expect(result).toBe('Text');
      });

      it('returns the markType when selectedType is not "GridChart" or subVizType is not "text"', () => {
        const selectedType = 'BarChart';
        const subVizType = 'image';
        const markType = 'icon';
        const result = getMarkTitle(selectedType, subVizType, markType);
        expect(result).toBe(markType);
      });
    });
  });

  test("test the feedback format report data response", () => {
    const res = prepareFetchReportStateResponse(reportDataWithFeedbackFormat);
    expect(res).toEqual(expectedReportDataWithFeedbackFormat);
  })
});



describe('to test constructGeoJsonData', () => {
  it('should return an empty object if mapData is empty', () => {
    const actualData = [];
    const geographicRoles = [{ name: 'name', geographicRole: 'role' }];
    const type = 'point';
    const dispatch = jest.fn();
    const result = constructGeoJsonData(actualData, geographicRoles, type, dispatch);
    expect(result).toEqual({});
  });

  it('should construct GeoJson data correctly', () => {
    const actualData = [
      { name: 'item1', lng: 1, lat: 2, otherProp: 'prop1' },
      { name: 'item2', lng: 3, lat: 4, otherProp: 'prop2' },
    ];
    const geographicRoles = [{ name: 'name', geographicRole: 'role' }];
    const type = 'point';
    const dispatch = jest.fn((callback) => {
      const getState = () => ({
        hreport: {
          present: {
            geoJsonData: {
              role: [
                { name: 'item1', lng: 1, lat: 2 },
                { name: 'item2', lng: 3, lat: 4 },
              ],
            },
          },
        },
      });
      const services = {};
      callback(null, getState, services);
    });
    const result = constructGeoJsonData(actualData, geographicRoles, type, dispatch);
    expect(result).toEqual({
      type: 'FeatureCollection',
      features: [
        {
          type: 'Feature',
          id: 'item1',
          properties: { name: 'item1', lng: 1, lat: 2, otherProp: 'prop1', displayText: "item1" },
          geometry: { type: 'Point', coordinates: [1, 2] },
        },
        {
          type: 'Feature',
          id: 'item2',
          properties: { name: 'item2', lng: 3, lat: 4, otherProp: 'prop2', displayText: "item2" },
          geometry: { type: 'Point', coordinates: [3, 4] },
        },
      ],
    });
  });

  it('should return Point geometry for type "point"', () => {
    const item = { longitude: 10, latitude: 20 };
    const arr = [];
    const type = "point";
    const longitudeField = "longitude";
    const latitudeField = "latitude";
    expect(getGeometryForMap(item, 0, arr, type, longitudeField, latitudeField)).toEqual({
      type: "Point",
      coordinates: [10, 20]
    });
  });

  it('should return Point geometry for type "heatmap"', () => {
    const item = { longitude: 10, latitude: 20 };
    const arr = [];
    const type = "heatmap";
    const longitudeField = "longitude";
    const latitudeField = "latitude";
    expect(getGeometryForMap(item, 0, arr, type, longitudeField, latitudeField)).toEqual({
      type: "Point",
      coordinates: [10, 20]
    });
  });

  it('should return LineString geometry for other types', () => {
    const item1 = { longitude: 10, latitude: 20 };
    const item2 = { longitude: 30, latitude: 40 };
    const arr = [item1, item2];
    const type = "line";
    const longitudeField = "longitude";
    const latitudeField = "latitude";
    expect(getGeometryForMap(item1, 0, arr, type, longitudeField, latitudeField)).toEqual({
      type: "LineString",
      coordinates: [[10, 20], [30, 40]]
    });
  });
});

describe('to test prepareLongitudeLatitudeData', () => {
  it('should return an empty FeatureCollection if data is empty', () => {
    const data = [];
    const geographicRoles = [];
    const type = 'point';

    const result = prepareLongitudeLatitudeData(data, geographicRoles, type);

    expect(result).toEqual({
      type: 'FeatureCollection',
      features: [],
    });
  });

  it('should correctly prepare longitude and latitude data for a point type', () => {
    const data = [
      { id: 1, long: 10, lat: 20 },
      { id: 2, long: 30, lat: 40 },
    ];
    const geographicRoles = [
      { geographicRole: 'long', name: 'long' },
      { geographicRole: 'lat', name: 'lat' },
    ];
    const type = 'point';

    const result = prepareLongitudeLatitudeData(data, geographicRoles, type);

    expect(result).toEqual({
      type: 'FeatureCollection',
      features: [
        {
          type: 'Feature',
          id: '(10,20)',
          properties: {
            displayText: "(10,20)",
            name: '(10,20)',
            id: 1,
            long: 10,
            lat: 20,
          },
          geometry: {
            type: 'Point',
            coordinates: [10, 20],
          },
        },
        {
          type: 'Feature',
          id: '(30,40)',
          properties: {
            displayText: '(30,40)',
            name: '(30,40)',
            id: 2,
            long: 30,
            lat: 40,
          },
          geometry: {
            type: 'Point',
            coordinates: [30, 40],
          },
        },
      ],
    });
  });

  it('should correctly prepare longitude and latitude data for a line type', () => {
    const data = [
      { id: 1, long: 10, lat: 20 },
      { id: 2, long: 30, lat: 40 },
      { id: 3, long: 50, lat: 60 },
    ];
    const geographicRoles = [
      { geographicRole: 'long', name: 'long' },
      { geographicRole: 'lat', name: 'lat' },
    ];
    const type = 'line';

    const result = prepareLongitudeLatitudeData(data, geographicRoles, type);
    expect(result).toEqual({
      type: 'FeatureCollection',
      features: [
        {
          type: 'Feature',
          id: '(10,20)',
          properties: {
            displayText: '(10,20)',
            name: '(10,20)',
            id: 1,
            long: 10,
            lat: 20,
          },
          geometry: {
            type: 'LineString',
            coordinates: [[10, 20], [30, 40]],
          },
        },
        {
          type: 'Feature',
          id: '(30,40)',
          properties: {
            displayText: '(30,40)',
            name: '(30,40)',
            id: 2,
            long: 30,
            lat: 40,
          },
          geometry: {
            type: 'LineString',
            coordinates: [[30, 40], [50, 60]],
          },
        },
      ],
    });
  });
});


describe('To test getFormattedDataForMap', () => {
  test('when formatting applied ', () => {
    let result = getFormattedDataForMap(mapChartMockdataWhenFormatApplied)
    expect(result).toEqual(mapChartExpectedDataWhenFormatApplied)
  });

  test('when formatting is not applied', () => {
    let result = getFormattedDataForMap(mapChartMockdataWhenFormatNotApplied)
    expect(result).toEqual(mapChartExpectedDataWhenFormatNotApplied)
  });
})


describe('to test getMapChartValues', () => {
  it('should return default values when mapObject is undefined', () => {
    const dispatch = jest.fn();
    const { token, mapType } = getMapChartValues(dispatch);
    expect(token).toBeUndefined();
    expect(mapType).toBe('mapbox');
  });

  it('should return default values when mapObject is not an object', () => {
    const dispatch = jest.fn((callback) => {
      const getState = () => ({
        app: {
          applicationSettingsData: {
            map: 'invalidMapObject',
          },
        },
      });
      callback(null, getState);
    });
    const { token, mapType } = getMapChartValues(dispatch);
    expect(token).toBeUndefined();
    expect(mapType).toBe('mapbox');
  });

  it('should return default values when mapObject is an empty object', () => {
    const dispatch = jest.fn((callback) => {
      const getState = () => ({
        app: {
          applicationSettingsData: {
            map: {},
          },
        },
      });
      callback(null, getState);
    });
    const { token, mapType } = getMapChartValues(dispatch);
    expect(token).toBeUndefined();
    expect(mapType).toBe('mapbox');
  });

  it('should return valid token and map type when mapObject contains a valid token and map type', () => {
    const dispatch = jest.fn((callback) => {
      const getState = () => ({
        app: {
          applicationSettingsData: {
            map: {
              mapbox:
                { token: 'validToken' }
            }
          },
        },
      });
      callback(null, getState);
    });
    const { token, mapType } = getMapChartValues(dispatch);
    expect(token).toBe('validToken');
    expect(mapType).toBe('mapbox');
  });

  it('should return first valid token and map type when mapObject contains multiple valid tokens and map types', () => {
    const dispatch = jest.fn((callback) => {
      const getState = () => ({
        app: {
          applicationSettingsData: {
            map: {
              mapbox:
              {
                token: 'validToken1'
              },
              test:
              {
                token: 'validToken2'
              }
            }
          },
        },
      });
      callback(null, getState);
    });
    const { token, mapType } = getMapChartValues(dispatch);
    expect(token).toBe('validToken1');
    expect(mapType).toBe('mapbox');
  });
});

describe('to test getMapPropertiesPayload', () => {
  test('getMapPropertiesPayload with valid event object', () => {
    const event = {
      target: {
        transform: {
          center: { lng: 10.12345623, lat: 20.654321424 },
          zoom: 15.123456
        }
      }
    };
    const chart = {
      zoomControl: {
        zoomInButton: { style: {} },
        zoomOutButton: { style: {} }
      }
    }
    const result = getMapPropertiesPayload(event, chart);
    expect(result).toEqual({
      longitude: 10.123456,
      latitude: 20.654321,
      zoom: 15.12
    });
  });

})

describe('to test getMapColorFieldScale', () => {
  const report = {
    fields: [
      { alias: 'testAlias', floatingType: 'discrete' },
      { autogen_alias: 'testAutogenAlias', floatingType: '' },
    ]
  };

  it('should return a type of "cat" when finding a field with a matching alias', () => {
    expect(getMapColorFieldScale(report, 'testAlias')).toEqual({ type: 'cat' });
  });

  it('should return a type of "cat" when finding a field with a matching autogen_alias', () => {
    expect(getMapColorFieldScale(report, 'testAutogenAlias')).toEqual({ type: 'quantile' });
  });

  it('should return a type of "cat" when the field has a floatingType of "discrete"', () => {
    expect(getMapColorFieldScale(report, 'testAlias')).toEqual({ type: 'cat' });
  });

  it('should return a type of "quantile" when the field has a floatingType other than "discrete"', () => {
    expect(getMapColorFieldScale(report, 'testAutogenAlias')).toEqual({ type: 'quantile' });
  });
});

describe('to test generateFormatColorRamp', () => {
  const testResut = [
    {
      "color": "#9d0208",
      "position": 1 / 7,
    },
    {
      "color": "#d00000",
      "position": 2 / 7,
    },
    {
      "color": "#dc2f02",
      "position": 3 / 7,
    },
    {
      "color": "#e85d04",
      "position": 4 / 7,
    },
    {
      "color": "#f48c06",
      "position": 5 / 7,
    },
    {
      "color": "#faa307",
      "position": 6 / 7,
    },
    {
      "color": "#ffba08",
      "position": 1,
    },
  ]
  test('generateFormatColorRamp with empty array', () => {
    const result = generateFormatColorRamp([]);
    expect(result).toEqual(testResut);
  });

  test('generateFormatColorRamp with array of colors', () => {
    const colors = ['#FF0000', '#00FF00', '#0000FF'];
    const result = generateFormatColorRamp(colors);
    expect(result).toEqual([
      { color: '#FF0000', position: 1 / 3 },
      { color: '#00FF00', position: 2 / 3 },
      { color: '#0000FF', position: 3 / 3 },
    ]);
  });

  test('generateFormatColorRamp with single color', () => {
    const colors = ['#FF0000'];
    const result = generateFormatColorRamp(colors);
    expect(result).toEqual([
      { color: '#FF0000', position: 1 },
    ]);
  });
})

describe('test calcFunc', () => {
  it('should return 0 when arr is empty', () => {
    expect(calcFunc({}, [])).toEqual(0);
  });

  it('should return the sum of all values when aggregateFn is "sum"', () => {
    const query = { $$extra$$: 'value' };
    const arr = [{ value: 1 }, { value: 2 }, { value: 3 }];
    const fieldsAggregateFn = { value: 'sum' };
    expect(calcFunc(query, arr, fieldsAggregateFn)).toEqual(6);
  });

  it('should return the sum of all values when aggregateFn is "count"', () => {
    const query = { $$extra$$: 'value' };
    const arr = [{ value: 1 }, { value: 2 }, { value: 3 }];
    const fieldsAggregateFn = { value: 'count' };
    expect(calcFunc(query, arr, fieldsAggregateFn)).toEqual(6);
  });

  it('should return the minimum value when aggregateFn is "min"', () => {
    const query = { $$extra$$: 'value' };
    const arr = [{ value: 1 }, { value: 2 }, { value: 3 }];
    const fieldsAggregateFn = { value: 'min' };
    expect(calcFunc(query, arr, fieldsAggregateFn)).toEqual(1);
  });

  it('should return the maximum value when aggregateFn is "max"', () => {
    const query = { $$extra$$: 'value' };
    const arr = [{ value: 1 }, { value: 2 }, { value: 3 }];
    const fieldsAggregateFn = { value: 'max' };
    expect(calcFunc(query, arr, fieldsAggregateFn)).toEqual(3);
  });

  it('should return the average value when aggregateFn is "avg"', () => {
    const query = { $$extra$$: 'value' };
    const arr = [{ value: 1 }, { value: 2 }, { value: 3 }];
    const fieldsAggregateFn = { value: 'avg' };
    expect(calcFunc(query, arr, fieldsAggregateFn)).toEqual(2);
  });

  it('should return the sum of all values when aggregateFn is not defined', () => {
    const query = { $$extra$$: 'value' };
    const arr = [{ value: 1 }, { value: 2 }, { value: 3 }];
    expect(calcFunc(query, arr, {})).toEqual(6);
  });
});

describe('getCustomFieldDataType function', () => {


  test('to test getCustomFieldDataType function', () => {
    const metadata = [
      {
        1: {
          name: "sum_source_id",
          type: "numeric",
        },
        2: {
          name: "Cost",
          type: "numeric",
        },
      },
      {
        rows: 103,
      },
    ];
    const selectedField = {
      label: "Custom Column",
      custom: true,
      column: '("HIUSER"."travel_details"."travel_cost")',
      columnID: "",
      alias: "Cost",
      id: "5987317c-82c6-43ea-945b-a47704c37e08",
      addedAs: "column",
      floatingType: "discrete",
      functionsDefinition: "",
      orderByColumn: false,
      showOrderByColumn: false,
      groupBy: ["db.generic.groupBy.group"],
    };
    const result = getCustomFieldDataType(metadata, selectedField);
    const expectedResult = 'numeric'
    expect(result).toBe(expectedResult);
  });

  test('should return an empty string if field type is netesther continuous nor discrete', () => {
    expect(getCustomFieldDataType([{ customField1: { name: 'Custom Field 1', type: 'numeric' } }], { custom: true, floatingType: 'invalid' })).toBe('');
  });

  test('should return an empty string if field is not a custom field', () => {
    expect(getCustomFieldDataType([{ customField1: { name: 'Custom Field 1', type: 'numeric' } }], { custom: false, floatingType: 'continous' })).toBe('');
  });

  test('should return an empty string if field is empty', () => {
    expect(getCustomFieldDataType([{ customField1: { name: 'Custom Field 1', type: 'numeric' } }], {})).toBe('');
  });

  test('should return an empty string if metadata is empty', () => {
    expect(getCustomFieldDataType([], { custom: true, floatingType: 'continous' })).toBe('');
  });

  test('to test getCustomFieldDataType function numeric', () => {
    const metadata = [
      {
        1: {
          name: "booking_platform",
          type: "text",
        },
        2: {
          name: "Cost",
          type: "numeric",
        },
      },
      {
        rows: 3,
      },
    ];
    const selectedField = {
      label: "Custom Column",
      custom: true,
      column: '("HIUSER"."travel_details"."travel_cost")',
      columnID: "",
      alias: "Cost",
      id: "ed0d5ff8-51c1-4811-9d54-d42da5ba4c46",
      addedAs: "row",
      floatingType: "continous",
      functionsDefinition: "",
      orderByColumn: false,
      showOrderByColumn: false,
      aggregate: ["db.generic.aggregate.sum"],
      autogen_alias: "",
    };
    const result = getCustomFieldDataType(metadata, selectedField);
    const expectedResult = 'numeric'
    expect(result).toBe(expectedResult);
  });

});


describe('checkIsCustomContinuousField', () => {
  test('returns true if field is custom continuous field', () => {
    const field = {
      custom: true,
      floatingType: 'continous'
    };
    expect(checkIsCustomContinuousField(field)).toBe(true);
  });

  test('returns false if field is not custom continuous field', () => {
    const field1 = {
      custom: true,
      floatingType: 'discrete'
    };
    const field2 = {
      custom: false,
      floatingType: 'continuous'
    };
    const field3 = {
      custom: false,
      floatingType: 'discrete'
    };
    expect(checkIsCustomContinuousField(field1)).toBe(false);
    expect(checkIsCustomContinuousField(field2)).toBe(false);
    expect(checkIsCustomContinuousField(field3)).toBe(false);
  });

});

const flushPromises = () => new Promise(setImmediate);

describe('test applyStylesToMuzeTicks', () => {
  beforeEach(() => {
    document.body.innerHTML = '<div id="hr-muze-chart-container"></div>';
  });

  afterEach(() => {
    document.body.innerHTML = '';
  });

  it('should apply styles with valid font size and color', async () => {
    const fontSize = 12;
    const fontColor = { r: 0, g: 0, b: 0, a: 1 };
    const type = 'x';
    const muzeChartId = "hr-muze-chart-container"
    await flushPromises(applyStylesToMuzeTicks({ fontSize, fontColor, type, muzeChartId }));
    const style = document.querySelector('#hr-muze-chart-container style');
    expect(style.innerHTML).toContain(`font-size: ${fontSize}px`);
    expect(style.innerHTML).toContain(`fill: ${getHTMLColorFormat(fontColor)}`);
  });

  it('should apply styles with different types', async () => {
    const fontSize = 12;
    const fontColor = { r: 0, g: 0, b: 0, a: 1 };
    const type = 'y';
    const muzeChartId = "hr-muze-chart-container"
    await flushPromises(applyStylesToMuzeTicks({ fontSize, fontColor, type, muzeChartId }));
    const style = document.querySelector('#hr-muze-chart-container style');
    expect(style.innerHTML).toContain('.muze-axis-container-left');
  });

});

jest.mock('liquidjs', () => {
  return {
    Liquid: jest.fn().mockImplementation(() => ({
      parseAndRenderSync: jest.fn(),
    })),
  };
});

describe('test wrapSpecialVariables', () => {
  describe('edge cases', () => {
    it('should return template as-is if falsy (null)', () => {
      expect(wrapSpecialVariables(null)).toBeNull();
    });

    it('should return template as-is if falsy (empty string)', () => {
      expect(wrapSpecialVariables('')).toBe('');
    });

    it('should return template as-is if falsy (undefined)', () => {
      expect(wrapSpecialVariables(undefined)).toBeUndefined();
    });

    it('should return template unchanged if no {{ }} present', () => {
      const template = '<p>Hello World</p>';
      expect(wrapSpecialVariables(template)).toBe('<p>Hello World</p>');
    });
  });

  describe('normal variables (no wrapping needed)', () => {
    it('should not wrap simple word variables', () => {
      expect(wrapSpecialVariables('{{ name }}')).toBe('{{ name }}');
    });

    it('should not wrap dot-notation variables', () => {
      expect(wrapSpecialVariables('{{ user.name }}')).toBe("{{['user.name ']}}");
    });

    it('should not wrap variables with filters', () => {
      expect(wrapSpecialVariables('{{ name | upcase }}')).toBe('{{ name | upcase }}');
    });

    it('should not wrap already bracket-wrapped variables', () => {
      expect(wrapSpecialVariables("{{\t['special var']\t}}")).toBe("{{\t['special var']\t}}");
    });
  });

  describe('special character variables (wrapping needed)', () => {
    it('should wrap variables containing spaces', () => {
      expect(wrapSpecialVariables('{{ my variable }}')).toBe("{{['my variable ']}}");
    });

    it('should wrap variables containing hyphens', () => {
      expect(wrapSpecialVariables('{{ my-variable }}')).toBe("{{['my-variable']}}");
    });

    it('should wrap variables containing special chars like @', () => {
      expect(wrapSpecialVariables('{{ @variable }}')).toBe("{{['@variable']}}");
    });

    it('should wrap variables containing parentheses', () => {
      expect(wrapSpecialVariables('{{ var(1) }}')).toBe("{{['var(1)']}}");
    });

    it('should decode &amp; inside special variable names', () => {
      expect(wrapSpecialVariables('{{ foo&amp;bar }}')).toBe("{{['foo&bar ']}}");
    });
  });

  describe('&amp; decoding in match', () => {
    it('should replace &amp; with & in the matched token', () => {
      const result = wrapSpecialVariables('{{ foo&amp;bar }}');
      expect(result).toBe("{{['foo&bar ']}}");
    });
  });

  describe('multiple variables in template', () => {
    it('should handle multiple variables in the same template', () => {
      const template = '{{ name }} and {{ my-variable }}';
      expect(wrapSpecialVariables(template)).toBe("{{ name }} and {{['my-variable']}}");
    });

    it('should handle multiple special variables', () => {
      const template = '{{ my var }} {{ other-var }}';
      expect(wrapSpecialVariables(template)).toBe("{{['my var ']}} {{['other-var']}}");
    });
  });
});


describe('test tooltipTemplateLiquidJS', () => {
  let mockParseAndRenderSync;
  let mockNotify;

  beforeEach(() => {
    mockParseAndRenderSync = jest.fn();
    Liquid.mockImplementation(() => ({
      parseAndRenderSync: mockParseAndRenderSync,
    }));

    mockNotify = {
      error: jest.fn(),
    };
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('test successful rendering', () => {
    it('should render template with given scope', () => {
      mockParseAndRenderSync.mockReturnValue('<p>John</p>');
      const result = tooltipTemplateLiquidJS({
        value: '<p>{{ name }}</p>',
        scope: { name: 'John' },
        Notify: mockNotify,
      });
      expect(result).toBe('<p>John</p>');
      expect(mockNotify.error).not.toHaveBeenCalled();
    });

    it('should use default value "<p></p>" when value is not provided', () => {
      mockParseAndRenderSync.mockReturnValue('<p></p>');
      const result = tooltipTemplateLiquidJS({
        scope: {},
        Notify: mockNotify,
      });
      expect(mockParseAndRenderSync).toHaveBeenCalledWith('<p></p>', {});
      expect(result).toBe('<p></p>');
    });

    it('should pass processed (wrapSpecialVariables) value to engine', () => {
      mockParseAndRenderSync.mockReturnValue('wrapped result');
      tooltipTemplateLiquidJS({
        value: '{{ my-var }}',
        scope: { 'my-var': 'hello' },
        Notify: mockNotify,
      });
      expect(mockParseAndRenderSync).toHaveBeenCalledWith("{{['my-var']}}", { 'my-var': 'hello' });
    });

    it('should render an empty string value', () => {
      mockParseAndRenderSync.mockReturnValue('');
      const result = tooltipTemplateLiquidJS({
        value: '',
        scope: {},
        Notify: mockNotify,
      });
      expect(result).toBe('');
    });

    it('should handle scope with multiple variables', () => {
      mockParseAndRenderSync.mockReturnValue('<p>John Doe</p>');
      const result = tooltipTemplateLiquidJS({
        value: '<p>{{ first }} {{ last }}</p>',
        scope: { first: 'John', last: 'Doe' },
        Notify: mockNotify,
      });
      expect(result).toBe('<p>John Doe</p>');
    });
  });

  describe('error handling', () => {
    it('should call Notify.error when engine throws', () => {
      const error = new Error('Liquid parse error');
      mockParseAndRenderSync.mockImplementation(() => { throw error; });

      const result = tooltipTemplateLiquidJS({
        value: '{% invalid %}',
        scope: {},
        Notify: mockNotify,
      });

      expect(mockNotify.error).toHaveBeenCalledWith({
        message: 'Liquid parse error',
        type: 'Frontend',
      });
      expect(result).toBeUndefined();
    });

    it('should return undefined when an error is thrown', () => {
      mockParseAndRenderSync.mockImplementation(() => { throw new Error('fail'); });

      const result = tooltipTemplateLiquidJS({
        value: '{{ bad template }',
        scope: {},
        Notify: mockNotify,
      });

      expect(result).toBeUndefined();
    });

    it('should not rethrow the error', () => {
      mockParseAndRenderSync.mockImplementation(() => { throw new Error('oops'); });

      expect(() =>
        tooltipTemplateLiquidJS({ value: 'x', scope: {}, Notify: mockNotify })
      ).not.toThrow();
    });
  });

  describe('Liquid engine instantiation', () => {
    it('should create a new Liquid engine on each call', () => {
      mockParseAndRenderSync.mockReturnValue('');
      tooltipTemplateLiquidJS({ scope: {}, Notify: mockNotify });
      tooltipTemplateLiquidJS({ scope: {}, Notify: mockNotify });
      expect(Liquid).toHaveBeenCalledTimes(2);
    });
  });
});

describe('test handleApplyCustomFormatting', () => {

  describe('should return original text', () => {
    test('returns text when format is null', () => {
      const result = handleApplyCustomFormatting(null, 'hello', 'numeric');
      expect(result).toBe('hello');
    });

    test('returns text when format is undefined (default param)', () => {
      const result = handleApplyCustomFormatting(undefined, 'hello', 'numeric');
      expect(result).toBe('hello');
    });

    test('returns text when customFormat is empty string', () => {
      const result = handleApplyCustomFormatting({ customFormat: '' }, 'hello', 'numeric');
      expect(result).toBe('hello');
    });

    test('returns text when customFormat is missing from format object', () => {
      const result = handleApplyCustomFormatting({}, 'hello', 'numeric');
      expect(result).toBe('hello');
    });

    test('returns text when customFormat is invalid (isValidFormat returns false)', () => {
      const result = handleApplyCustomFormatting({ customFormat: '###INVALID' }, '123', 'numeric');
      expect(result).toBe('123');
    });
  });


  describe('fieldType: numeric', () => {
    const format = { customFormat: '#,##0.00' };

    test('formats a valid numeric string', () => {
      const result = handleApplyCustomFormatting(format, '1234.5', 'numeric');
      expect(result).toBe('1,234.50')
    });

    test('formats zero', () => {
      const result = handleApplyCustomFormatting(format, '0', 'numeric');
      expect(result).toBe('0.00');
    });

    test('formats negative number', () => {
      const result = handleApplyCustomFormatting(format, '-99.99', 'numeric');
      expect(result).toBe('-99.99');
    });

    test('returns original text when value is NaN', () => {
      const result = handleApplyCustomFormatting(format, 'abc', 'numeric');
      expect(result).toBe('abc');
    });

    test('returns original text when value is empty string (NaN)', () => {
      const result = handleApplyCustomFormatting(format, '', 'numeric');
      expect(result).toBe('0.00');
    });
  });

  describe('fieldType: date', () => {
    const format = { customFormat: 'YYYY-MM-DD' };

    test('formats a valid ISO date string', () => {
      const result = handleApplyCustomFormatting(format, '2024-01-15', 'date');
      expect(result).toBe('2024-01-15');
    });

    test('formats a valid datetime string', () => {
      const result = handleApplyCustomFormatting(format, new Date('2024-06-01T10:30:00'), 'date');
      expect(result).toBe('2024-06-01');
    });

    test('returns original text for invalid date string', () => {
      const result = handleApplyCustomFormatting(format, 'not-a-date', 'date');
      expect(result).toBe('not-a-date');
    });

    test('returns original text for empty string', () => {
      const result = handleApplyCustomFormatting(format, '', 'date');
      expect(result).toBe('');
    });

    test('returns original text for null', () => {
      const result = handleApplyCustomFormatting(format, null, 'date');
      expect(result).toBeNull();
    });

    test('returns original text for a small numeric timestamp (out of valid range)', () => {
      const result = handleApplyCustomFormatting(format, '12345', 'date');
      expect(result).toBe('12345');
    });

    test('formats a valid unix timestamp in ms (within range)', () => {
      const validTs = '1700000000000';
      const result = handleApplyCustomFormatting(format, validTs, 'date');
      expect(result).toBe("Invalid Date");
    });
  });


  describe('fieldType: dateTime', () => {
    const format = { customFormat: 'YYYY-MM-DD HH:mm:ss' };

    test('formats a valid datetime string', () => {
      const result = handleApplyCustomFormatting(format, '2024-03-20T14:30:00', 'dateTime');
      expect(result).toBe("2024-03-20 14:30:00");
    });

    test('returns original text for invalid datetime', () => {
      const result = handleApplyCustomFormatting(format, 'invalid', 'dateTime');
      expect(result).toBe('invalid');
    });
  });

  describe('fieldType: time', () => {
    const format = { customFormat: 'HH:mm' };

    test('formats a valid time/date string', () => {
      const result = handleApplyCustomFormatting(format, '2024-01-01T08:00:00', 'time');
      expect(result).toBe('08:00');
    });

    test('returns original text for invalid time value', () => {
      const result = handleApplyCustomFormatting(format, 'not-a-time', 'time');
      expect(result).toBe('not-a-time');
    });
  });

  describe('fieldType: unknown / default', () => {
    test('returns original text for unknown fieldType', () => {
      const result = handleApplyCustomFormatting({ customFormat: '#,##0' }, '9999', 'text');
      expect(result).toBe('9999');
    });

    test('formats a valid string', () => {
      const result = handleApplyCustomFormatting({ customFormat: '"Test:"@' }, 'Hello', 'text');
      expect(result).toBe('Test:Hello')
    });

    test('returns original text when fieldType is undefined', () => {
      const result = handleApplyCustomFormatting({ customFormat: '#,##0' }, 'hello', undefined);
      expect(result).toBe('hello');
    });
  });
});

describe('test calculateRadialPercentage fn', () => {

  const sampleData = [
    { category: 'A', region: 'North', sales: 200 },
    { category: 'B', region: 'North', sales: 300 },
    { category: 'A', region: 'South', sales: 100 },
    { category: 'B', region: 'South', sales: 400 },
  ];

  describe('when measureField is missing', () => {
    it('returns null when measureField is an empty string', () => {
      expect(calculateRadialPercentage({ data: sampleData, obj: sampleData[0], measureField: '', dimensions: [] }))
        .toBeNull();
    });

    it('returns null when measureField is not provided', () => {
      expect(calculateRadialPercentage({ data: sampleData, obj: sampleData[0] }))
        .toBeNull();
    });

    it('returns null with all defaults', () => {
      expect(calculateRadialPercentage({})).toBeNull();
    });
  });

  describe('when dimensions array is empty', () => {
    it('calculates percentage against total of all data', () => {
      const result = calculateRadialPercentage({
        data: sampleData,
        obj: sampleData[0],
        measureField: 'sales',
        dimensions: [],
      });
      expect(result).toBe('20.00%');
    });

    it('returns 100% when there is only one item', () => {
      const single = [{ sales: 500 }];
      const result = calculateRadialPercentage({
        data: single,
        obj: single[0],
        measureField: 'sales',
        dimensions: [],
      });
      expect(result).toBe('100.00%');
    });

    it('calculates correct percentage for a mid-range value', () => {
      const result = calculateRadialPercentage({
        data: sampleData,
        obj: sampleData[1],
        measureField: 'sales',
        dimensions: [],
      });
      expect(result).toBe('30.00%');
    });

    it('returns percentage with exactly 2 decimal places', () => {
      const data = [{ v: 1 }, { v: 3 }];
      const result = calculateRadialPercentage({ data, obj: data[0], measureField: 'v', dimensions: [] });
      expect(result).toMatch(/^\d+\.\d{2}%$/);
      expect(result).toBe('25.00%');
    });
  });

  describe('when dimensions array has one dimension', () => {
    it('filters data by the first dimension value before calculating', () => {
      const result = calculateRadialPercentage({
        data: sampleData,
        obj: sampleData[0],
        measureField: 'sales',
        dimensions: ['region'],
      });
      expect(result).toBe('40.00%');
    });

    it('uses only items matching the dimension value of obj', () => {
      const result = calculateRadialPercentage({
        data: sampleData,
        obj: sampleData[3],
        measureField: 'sales',
        dimensions: ['region'],
      });
      expect(result).toBe('80.00%');
    });

    it('uses only the first dimension even when multiple are provided', () => {
      const result = calculateRadialPercentage({
        data: sampleData,
        obj: sampleData[0],
        measureField: 'sales',
        dimensions: ['region', 'category'],
      });
      expect(result).toBe('40.00%');
    });

    it('returns 100% when obj is the only item matching the dimension', () => {
      const data = [
        { type: 'X', value: 50 },
        { type: 'Y', value: 200 },
      ];
      const result = calculateRadialPercentage({
        data,
        obj: data[0],
        measureField: 'value',
        dimensions: ['type'],
      });
      expect(result).toBe('100.00%');
    });
  });

  describe('default parameter handling', () => {
    it('uses empty array as default for data', () => {
      expect(() =>
        calculateRadialPercentage({ obj: { sales: 100 }, measureField: 'sales' })
      ).not.toThrow();
    });

    it('uses empty object as default for obj', () => {
      expect(() =>
        calculateRadialPercentage({ data: sampleData, measureField: 'sales' })
      ).not.toThrow();
    });

    it('uses empty array as default for dimensions', () => {
      const result = calculateRadialPercentage({
        data: sampleData,
        obj: sampleData[0],
        measureField: 'sales',
      });
      expect(result).toBe('20.00%');
    });
  });

  describe('return value format', () => {
    it('always returns a string ending with %', () => {
      const result = calculateRadialPercentage({
        data: sampleData,
        obj: sampleData[0],
        measureField: 'sales',
        dimensions: [],
      });
      expect(typeof result).toBe('string');
      expect(result.endsWith('%')).toBe(true);
    });

    it('always returns exactly 2 decimal places', () => {
      const result = calculateRadialPercentage({
        data: sampleData,
        obj: sampleData[0],
        measureField: 'sales',
        dimensions: [],
      });
      expect(result).toMatch(/^\d+\.\d{2}%$/);
    });
  });

});


describe('test isColorFieldDimension fn', () => {

  const makeReport = (fields) => ({ fields });

  const discreteField = { id: 'field_1', floatingType: 'discrete' };
  const continuousField = { id: 'field_2', floatingType: 'continuous' };
  const measureField = { id: 'field_3', floatingType: 'measure' };
  const noTypeField = { id: 'field_4' };

  describe('when required arguments are missing or falsy', () => {
    it('returns false when clrFldId is null', () => {
      expect(isColorFieldDimension(null, makeReport([discreteField]))).toBe(false);
    });

    it('returns false when clrFldId is undefined', () => {
      expect(isColorFieldDimension(undefined, makeReport([discreteField]))).toBe(false);
    });

    it('returns false when clrFldId is an empty string', () => {
      expect(isColorFieldDimension('', makeReport([discreteField]))).toBe(false);
    });

    it('returns false when report is null', () => {
      expect(isColorFieldDimension('field_1', null)).toBe(false);
    });

    it('returns false when report is undefined', () => {
      expect(isColorFieldDimension('field_1', undefined)).toBe(false);
    });

    it('returns false when both arguments are falsy', () => {
      expect(isColorFieldDimension(null, null)).toBe(false);
    });
  });

  describe('when the field is not found in report.fields', () => {
    it('returns false when no field matches the given clrFldId', () => {
      expect(isColorFieldDimension('nonexistent_id', makeReport([discreteField]))).toBe(false);
    });

    it('returns false when report.fields is empty', () => {
      expect(isColorFieldDimension('field_1', makeReport([]))).toBe(false);
    });

    it('returns false when clrFldId is a partial match of a field id', () => {
      expect(isColorFieldDimension('field', makeReport([discreteField]))).toBe(false);
    });
  });

  describe('when the matched field has floatingType "discrete"', () => {
    it('returns true for a discrete field', () => {
      expect(isColorFieldDimension('field_1', makeReport([discreteField]))).toBe(true);
    });

    it('returns true when multiple fields exist and the matching one is discrete', () => {
      const report = makeReport([continuousField, discreteField, measureField]);
      expect(isColorFieldDimension('field_1', makeReport([continuousField, discreteField]))).toBe(true);
    });
  });

  describe('when the matched field has a non-discrete floatingType', () => {
    it('returns false for floatingType "continuous"', () => {
      expect(isColorFieldDimension('field_2', makeReport([continuousField]))).toBe(false);
    });

    it('returns false for floatingType "measure"', () => {
      expect(isColorFieldDimension('field_3', makeReport([measureField]))).toBe(false);
    });

    it('returns false when floatingType is undefined', () => {
      expect(isColorFieldDimension('field_4', makeReport([noTypeField]))).toBe(false);
    });

    it('returns false when floatingType is an empty string', () => {
      const field = { id: 'field_5', floatingType: '' };
      expect(isColorFieldDimension('field_5', makeReport([field]))).toBe(false);
    });

    it('returns false when floatingType is null', () => {
      const field = { id: 'field_6', floatingType: null };
      expect(isColorFieldDimension('field_6', makeReport([field]))).toBe(false);
    });

    it('is case-sensitive and rejects "Discrete" (capitalised)', () => {
      const field = { id: 'field_7', floatingType: 'Discrete' };
      expect(isColorFieldDimension('field_7', makeReport([field]))).toBe(false);
    });
  });

  describe('with multiple fields in the report', () => {
    const report = makeReport([discreteField, continuousField, measureField, noTypeField]);

    it('correctly identifies a discrete field among many', () => {
      expect(isColorFieldDimension('field_1', report)).toBe(true);
    });

    it('correctly rejects a continuous field among many', () => {
      expect(isColorFieldDimension('field_2', report)).toBe(false);
    });

    it('correctly rejects a measure field among many', () => {
      expect(isColorFieldDimension('field_3', report)).toBe(false);
    });
  });

});

describe('test getNewDateFormat fn', () => {

  describe('getNewDateFormat', () => {

    describe('when all format parts are none', () => {
      it('should return empty string', () => {
        expect(getNewDateFormat({
          text: '2024-01-15T10:00:00Z',
          format: { day: 'none', month: 'none', year: 'none', dateSeperator: '-' }
        })).toBe('');
      });
    });

    describe('year formatting', () => {
      it('should return 4-digit year', () => {
        expect(getNewDateFormat({
          text: '2024-03-05T00:00:00Z',
          format: { day: 'none', month: 'none', year: '4digit', dateSeperator: '-' }
        })).toBe('2024');
      });

      it('should return 2-digit year', () => {
        expect(getNewDateFormat({
          text: '2024-03-05T00:00:00Z',
          format: { day: 'none', month: 'none', year: '2digit', dateSeperator: '-' }
        })).toBe('24');
      });

      it('should return empty string for year none', () => {
        expect(getNewDateFormat({
          text: '2024-03-05T00:00:00Z',
          format: { day: 'none', month: 'none', year: 'none', dateSeperator: '-' }
        })).toBe('');
      });
    });

    describe('month formatting', () => {
      it('should return month number without zero (monthNum)', () => {
        expect(getNewDateFormat({
          text: '2024-03-05T00:00:00Z',
          format: { day: 'none', month: 'monthNum', year: 'none', dateSeperator: '-' }
        })).toBe('3');
      });

      it('should return month number with leading zero for single digit (monthNumWithZero)', () => {
        expect(getNewDateFormat({
          text: '2024-03-05T00:00:00Z',
          format: { day: 'none', month: 'monthNumWithZero', year: 'none', dateSeperator: '-' }
        })).toBe('03');
      });

      it('should return month number without leading zero for double digit (monthNumWithZero)', () => {
        expect(getNewDateFormat({
          text: '2024-11-15T00:00:00Z',
          format: { day: 'none', month: 'monthNumWithZero', year: 'none', dateSeperator: '-' }
        })).toBe('11');
      });

      it('should return abbreviated month name (monthAbbrevation)', () => {
        expect(getNewDateFormat({
          text: '2024-03-05T00:00:00Z',
          format: { day: 'none', month: 'monthAbbrevation', year: 'none', dateSeperator: '-' }
        })).toBe('Mar');
      });

      it('should return full month name (monthFull)', () => {
        expect(getNewDateFormat({
          text: '2024-03-05T00:00:00Z',
          format: { day: 'none', month: 'monthFull', year: 'none', dateSeperator: '-' }
        })).toBe('March');
      });
    });

    describe('day formatting', () => {
      it('should return day number without zero (dayNumber)', () => {
        expect(getNewDateFormat({
          text: '2024-03-05T00:00:00Z',
          format: { day: 'dayNumber', month: 'none', year: 'none', dateSeperator: '-' }
        })).toBe('5');
      });

      it('should return day with leading zero for single digit (dayNumWithZero)', () => {
        expect(getNewDateFormat({
          text: '2024-03-05T00:00:00Z',
          format: { day: 'dayNumWithZero', month: 'none', year: 'none', dateSeperator: '-' }
        })).toBe('05');
      });

      it('should return day without leading zero for double digit (dayNumWithZero)', () => {
        expect(getNewDateFormat({
          text: '2024-03-15T00:00:00Z',
          format: { day: 'dayNumWithZero', month: 'none', year: 'none', dateSeperator: '-' }
        })).toBe('15');
      });
    });

    describe('date separator', () => {
      it('should join parts with hyphen', () => {
        expect(getNewDateFormat({
          text: '2024-03-05T00:00:00Z',
          format: { day: 'dayNumWithZero', month: 'monthNumWithZero', year: '4digit', dateSeperator: '-' }
        })).toBe('2024-03-05');
      });

      it('should join parts with slash', () => {
        expect(getNewDateFormat({
          text: '2024-03-05T00:00:00Z',
          format: { day: 'dayNumWithZero', month: 'monthNumWithZero', year: '4digit', dateSeperator: '/' }
        })).toBe('2024/03/05');
      });

      it('should join parts with dot', () => {
        expect(getNewDateFormat({
          text: '2024-03-05T00:00:00Z',
          format: { day: 'dayNumWithZero', month: 'monthNumWithZero', year: '4digit', dateSeperator: '.' }
        })).toBe('2024.03.05');
      });
    });

    describe('timezone consistency', () => {
      it('should not shift date for UTC+5:30 edge case (11pm UTC = next day in IST)', () => {
        expect(getNewDateFormat({
          text: '2024-01-15T22:00:00Z',
          format: { day: 'dayNumWithZero', month: 'monthNumWithZero', year: '4digit', dateSeperator: '-' }
        })).toBe('2024-01-15');
      });

      it('should not shift date for UTC-5 edge case (1am UTC = prev day in EST)', () => {
        expect(getNewDateFormat({
          text: '2024-01-15T01:00:00Z',
          format: { day: 'dayNumWithZero', month: 'monthNumWithZero', year: '4digit', dateSeperator: '-' }
        })).toBe('2024-01-15');
      });

      it('should handle explicit +05:30 offset and stay on UTC date', () => {
        expect(getNewDateFormat({
          text: '2024-06-01T00:00:00+05:30',
          format: { day: 'dayNumWithZero', month: 'monthNumWithZero', year: '4digit', dateSeperator: '-' }
        })).toBe('2024-05-31');
      });
    });

    describe('combined formats', () => {
      it('should format as YYYY/DD/MM', () => {
        expect(getNewDateFormat({
          text: '2024-03-05T00:00:00Z',
          format: { day: 'dayNumWithZero', month: 'monthNumWithZero', year: '4digit', dateSeperator: '/' }
        })).toBe('2024/03/05');
      });

      it('should format with abbreviated month and 4 digit year', () => {
        expect(getNewDateFormat({
          text: '2024-12-25T00:00:00Z',
          format: { day: 'dayNumWithZero', month: 'monthAbbrevation', year: '4digit', dateSeperator: ' ' }
        })).toBe('2024 Dec 25');
      });

      it('should omit year when year is none', () => {
        expect(getNewDateFormat({
          text: '2024-03-05T00:00:00Z',
          format: { day: 'dayNumWithZero', month: 'monthNumWithZero', year: 'none', dateSeperator: '-' }
        })).toBe('03-05');
      });

      it('should omit day when day is none', () => {
        expect(getNewDateFormat({
          text: '2024-03-05T00:00:00Z',
          format: { day: 'none', month: 'monthFull', year: '4digit', dateSeperator: ' ' }
        })).toBe('2024 March');
      });
    });

    describe('edge cases', () => {
      it('should handle Dec 31 correctly', () => {
        expect(getNewDateFormat({
          text: '2024-12-31T00:00:00Z',
          format: { day: 'dayNumWithZero', month: 'monthNumWithZero', year: '4digit', dateSeperator: '-' }
        })).toBe('2024-12-31');
      });

      it('should handle Jan 1 correctly', () => {
        expect(getNewDateFormat({
          text: '2024-01-01T00:00:00Z',
          format: { day: 'dayNumWithZero', month: 'monthNumWithZero', year: '4digit', dateSeperator: '-' }
        })).toBe('2024-01-01');
      });

      it('should handle leap day Feb 29', () => {
        expect(getNewDateFormat({
          text: '2024-02-29T00:00:00Z',
          format: { day: 'dayNumWithZero', month: 'monthNumWithZero', year: '4digit', dateSeperator: '-' }
        })).toBe('2024-02-29');
      });
    });

  });
});