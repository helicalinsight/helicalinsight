import {
  dropIntoMarks,
  checkMarkFieldDisable,
  dropIntoParams,
  checkIsSubVizApplicable,
} from "../../../../../components/hi-reports/hi-editing-area/utils/marks-utils";
import { mock_data } from "./marks-utils.mock.data";

describe("dropIntoMarks", () => {
  let dispatch;

  beforeEach(() => {
    dispatch = jest.fn();
  });

  afterAll(() => {
    global.gc && global.gc()
  })

  test("adds field to marks correctly when type is canvas_field", () => {
    const data_1 = mock_data.data_1;

    const dispatch = jest.fn();
    const expectedResult_1 = mock_data.expectedResult_1;
    dropIntoMarks(data_1, dispatch);
    expect(dispatch).toHaveBeenCalledWith(expectedResult_1);
  });

  test("adds new field to marks correctly when field does not exist", () => {
    const data_2 = mock_data.data_2;
    const expectedResult_2 = mock_data.expectedResult_2;

    dropIntoMarks(data_2, dispatch);

    expect(dispatch).toHaveBeenCalledWith(expectedResult_2);
  });

  test("adds existing field to marks correctly when field already exists", () => {
    const data_3 = mock_data.data_3;

    const expectedResult_3 = mock_data.expectedResult_3;

    dropIntoMarks(data_3, dispatch);

    expect(dispatch).toHaveBeenCalledWith(expectedResult_3);
  });
});

describe("checkMarkFieldDisable function", () => {
  test("returns true for SyncChart", () => {
    const result = checkMarkFieldDisable({
      selectedType: "SyncChart",
      subVizType: "point",
      markType: "size",
    });
    expect(result).toBe(true);
  });

  test("returns true for Table and label mark type", () => {
    const result = checkMarkFieldDisable({
      selectedType: "Table",
      subVizType: "point",
      markType: "label",
    });
    expect(result).toBe(true);
  });

  test("returns true for Table and allowed mark types", () => {
    const result = checkMarkFieldDisable({
      selectedType: "Table",
      subVizType: "",
      markType: "label",
    });
    expect(result).toBe(true);
  });


  test("returns true for GridChart with specific conditions", () => {
    const result = checkMarkFieldDisable({
      selectedType: "GridChart",
      subVizType: "bar",
      markType: "size",
    });
    expect(result).toBe(true);
  });


  test("returns true for GridChart and size mark type with non-point subVizType", () => {
    const result = checkMarkFieldDisable({
      selectedType: "GridChart",
      subVizType: "bar",
      markType: "size",
    });
    expect(result).toBe(true);
  });

  test("returns true for GridChart and shape mark type with bar subVizType", () => {
    const result = checkMarkFieldDisable({
      selectedType: "GridChart",
      subVizType: "bar",
      markType: "shape",
    });
    expect(result).toBe(true);
  });

  test("returns true for GridChart and detail mark type with point subVizType", () => {
    const result = checkMarkFieldDisable({
      selectedType: "GridChart",
      subVizType: "point",
      markType: "detail",
    });
    expect(result).toBe(true);
  });

  test("returns true for Antcharts and size mark type with bar subVizType", () => {
    const result = checkMarkFieldDisable({
      selectedType: "Antcharts",
      subVizType: "bar",
      markType: "size",
    });
    expect(result).toBe(true);
  });

  test("returns true for Antcharts and size mark type with arc subVizType", () => {
    const result = checkMarkFieldDisable({
      selectedType: "Antcharts",
      subVizType: "arc",
      markType: "size",
    });
    expect(result).toBe(true);
  });

  test("returns true for Antcharts and size mark type with doughnut subVizType", () => {
    const result = checkMarkFieldDisable({
      selectedType: "Antcharts",
      subVizType: "doughnut",
      markType: "size",
    });
    expect(result).toBe(true);
  });

  test("returns true for Antcharts and shape mark type with non-point subVizType", () => {
    const result = checkMarkFieldDisable({
      selectedType: "Antcharts",
      subVizType: "bar",
      markType: "shape",
    });
    expect(result).toBe(true);
  });

  test("returns true for Antcharts and detail mark type with arc subVizType", () => {
    const result = checkMarkFieldDisable({
      selectedType: "Antcharts",
      subVizType: "arc",
      markType: "detail",
    });
    expect(result).toBe(true);
  });

  test("returns true for Antcharts and detail mark type with doughnut subVizType", () => {
    const result = checkMarkFieldDisable({
      selectedType: "Antcharts",
      subVizType: "doughnut",
      markType: "detail",
    });
    expect(result).toBe(true);
  });

  test("returns true for Antcharts and detail mark type with text subVizType", () => {
    const result = checkMarkFieldDisable({
      selectedType: "Antcharts",
      subVizType: "text",
      markType: "detail",
    });
    expect(result).toBe(true);
  });

  test("returns true for Card and shape mark type with arc subVizType", () => {
    const result = checkMarkFieldDisable({
      selectedType: "Card",
      subVizType: "arc",
      markType: "shape",
    });
    expect(result).toBe(true);
  });

  test("returns true for Card and label mark type with text subVizType", () => {
    const result = checkMarkFieldDisable({
      selectedType: "Card",
      subVizType: "text",
      markType: "label",
    });
    expect(result).toBe(true);
  });

  test("returns true for Card and detail mark type with doughnut subVizType", () => {
    const result = checkMarkFieldDisable({
      selectedType: "Ant_Card",
      subVizType: "doughnut",
      markType: "detail",
    });
    expect(result).toBe(true);
  });

  test("returns true for Antcharts with specific conditions", () => {
    const result = checkMarkFieldDisable({
      selectedType: "Antcharts",
      subVizType: "line",
      markType: "size",
    });
    expect(result).toBe(true);
  });

  test("returns true for Card with specific conditions", () => {
    const result = checkMarkFieldDisable({
      selectedType: "Card",
      subVizType: "arc",
      markType: "shape",
    });
    expect(result).toBe(true);
  });

  test("returns true for Card with other combinations", () => {
    const result = checkMarkFieldDisable({
      selectedType: "Card",
      subVizType: "bar",
      markType: "color",
    });
    expect(result).toBe(true);
  });

  test("returns true for MapChart with other combinations", () => {
    const result = checkMarkFieldDisable({
      selectedType: "MapChart",
      subVizType: "heatmap",
      markType: "color",
    });
    expect(result).toBe(true);
  });
});

describe("dropIntoParams", () => {
  let dispatch;

  beforeEach(() => {
    dispatch = jest.fn();
  });
  test("should add field to canvas when present in fields", () => {
    const data = {
      type: "metadata_field",
      field: {
        columnName: "mode_of_payment",
        column: {
          alias: "mode_of_payment",
          fullyQualifiedColumn: "travel_details.mode_of_payment",
          id: "13117",
          defaultFunction: "db.generic.groupBy.group",
          type: {
            "java.lang.String": "text",
          },
        },
        table: {
          id: "5740",
          alias: "travel_details",
          columns: {
            travel_id: {
              alias: "travel_id",
              fullyQualifiedColumn: "travel_details.travel_id",
              id: "13108",
              defaultFunction: "db.generic.aggregate.sum",
              type: {
                "java.lang.Integer": "numeric",
              },
            },
            travel_date: {
              alias: "travel_date",
              fullyQualifiedColumn: "travel_details.travel_date",
              id: "13109",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.sql.Timestamp": "dateTime",
              },
            },
            travel_type: {
              alias: "travel_type",
              fullyQualifiedColumn: "travel_details.travel_type",
              id: "13110",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
            travel_medium: {
              alias: "travel_medium",
              fullyQualifiedColumn: "travel_details.travel_medium",
              id: "13111",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
            source_id: {
              alias: "source_id",
              fullyQualifiedColumn: "travel_details.source_id",
              id: "13112",
              defaultFunction: "db.generic.aggregate.sum",
              type: {
                "java.lang.Integer": "numeric",
              },
            },
            source: {
              alias: "source",
              fullyQualifiedColumn: "travel_details.source",
              id: "13113",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
            destination_id: {
              alias: "destination_id",
              fullyQualifiedColumn: "travel_details.destination_id",
              id: "13114",
              defaultFunction: "db.generic.aggregate.sum",
              type: {
                "java.lang.Integer": "numeric",
              },
            },
            destination: {
              alias: "destination",
              fullyQualifiedColumn: "travel_details.destination",
              id: "13115",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
            travel_cost: {
              alias: "travel_cost",
              fullyQualifiedColumn: "travel_details.travel_cost",
              id: "13116",
              defaultFunction: "db.generic.aggregate.sum",
              type: {
                "java.lang.Integer": "numeric",
              },
            },
            mode_of_payment: {
              alias: "mode_of_payment",
              fullyQualifiedColumn: "travel_details.mode_of_payment",
              id: "13117",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
            booking_platform: {
              alias: "booking_platform",
              fullyQualifiedColumn: "travel_details.booking_platform",
              id: "13118",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
            travelled_by: {
              alias: "travelled_by",
              fullyQualifiedColumn: "travel_details.travelled_by",
              id: "13119",
              defaultFunction: "db.generic.aggregate.sum",
              type: {
                "java.lang.Integer": "numeric",
              },
            },
          },
          name: "travel_details",
          cacheId: "8a28627d07d04ef096d9935f12e0c7e9",
          key: "913bccd8-947f-419f-a2bf-a3204e955b86",
        },
        type: {
          "java.lang.String": "text",
        },
        defaultFunction: "db.generic.groupBy.group",
        key: "13117",
        columnId: "13117",
        alias: "mode_of_payment",
        tableId: "5740",
        dataType: "text",
        tableName: "travel_details",
        tableAlias: "travel_details",
        dataSource: {
          databaseName: "HIUSER",
          catalog: "",
          schema: "HIUSER",
        },
      },
      filterId: "d946dda5-e404-4702-b380-4b565323689d",
      drillThroughId: "bf307fde-3481-4743-a497-3ffb5185c822",
    };

    dropIntoParams(data, dispatch);
    expect(dispatch).toHaveBeenCalledWith(expect.any(Function));
  });
});


const makeField = ({
  addedAs = 'row',
  dataType = 'string',
  geo = false,
  date = false,
  hidden = false
}) => {
  const field = { addedAs, hidden };

  field.type = { dataType: dataType };

  if (geo) field.geographicType = ['country'];
  if (date) field.type.dataType = 'date';

  return field;
};

const mRow = () => makeField({ addedAs: 'row', dataType: 'numeric' });
const mCol = () => makeField({ addedAs: 'column', dataType: 'numeric' });
const dRow = () => makeField({ addedAs: 'row', dataType: 'text' });
const dCol = () => makeField({ addedAs: 'column' });
const gCol = () => makeField({ addedAs: 'column', geo: true });
const dtCol = () => makeField({ addedAs: 'column', date: true });
const hideCol = () => makeField({ addedAs: 'column', dataType: 'text', hidden: true });


describe('test checkIsSubVizApplicable', () => {
  it.each(['Table', 'S2Chart', 'VF'])(
    '%s should always return true',
    (type) => {
      expect(checkIsSubVizApplicable(type, '', [])).toBe(true);
      expect(
        checkIsSubVizApplicable(type, '', [mRow(), dCol()])
      ).toBe(true);
    }
  );

  it(' should returns true when no fields are dropped at all', () => {
    expect(
      checkIsSubVizApplicable('GridChart', 'bar', [])
    ).toBe(true);
  });


  describe('test isMap flag for MapChart', () => {
    it('shoudl pass when at least one geographic field is present', () => {
      expect(
        checkIsSubVizApplicable('MapChart', 'line', [gCol()])
      ).toBe(true);
    });
    it('should fail without a geographic field', () => {
      expect(
        checkIsSubVizApplicable('MapChart', 'line', [dCol()])
      ).toBe(false);
    });
  });

  describe('test isCalendar flag Antcharts calendar viz)', () => {
    it('should pass with only date dimensions', () => {
      expect(
        checkIsSubVizApplicable('Antcharts', 'calendar', [dtCol()])
      ).toBe(true);
    });
    it('should fail if any non‑date field present', () => {
      expect(
        checkIsSubVizApplicable('Antcharts', 'calendar', [dtCol(), dRow()])
      ).toBe(false);
    });
  });

  describe('test onlyDimension flag for Antcharts, text)', () => {
    it('should pass with dimension and no measures', () => {
      expect(
        checkIsSubVizApplicable('Antcharts', 'text', [dCol()])
      ).toBe(true);
    });
    it('should fail if a measure is present', () => {
      expect(
        checkIsSubVizApplicable('Antcharts', 'text', [dCol(), mRow()])
      ).toBe(false);
    });
  });

  describe('test regular measure / dimension thresholds', () => {
    it('GridChart (bar) should pass with ≥1 measure + ≥1 dimension', () => {
      expect(
        checkIsSubVizApplicable('GridChart', 'bar', [dCol(), mRow()])
      ).toBe(true);
    });
    it('GridChart (bar should fail if dimensions are missing', () => {
      expect(
        checkIsSubVizApplicable('GridChart', 'bar', [mRow()])
      ).toBe(false);
    });
  });

  describe('test exact variants GridChart and AntChart, arc', () => {
    it('should pass with “only one measure in rows, zero everywhere else”', () => {
      expect(
        checkIsSubVizApplicable('GridChart', 'arc', [mRow()])
      ).toBe(true);
    });
    it('should fail if the exact pattern is broken (e.g. a dimension added)', () => {
      expect(
        checkIsSubVizApplicable('Antcharts', 'arc', [mCol(), dRow()])
      ).toBe(false);
    });
  });

  describe('test GridChart heatmap ', () => {
    it('should pass with 1 dimension in rows and 1 dimension in columns', () => {
      expect(
        checkIsSubVizApplicable('GridChart', 'heatmap', [dRow(), dCol()])
      ).toBe(true);
    });
    it('should fail if both dimensions are in the same axis', () => {
      expect(
        checkIsSubVizApplicable(
          'GridChart',
          'heatmap',
          [dCol(), dCol()] // two columns, zero rows
        )
      ).toBe(false);
    });
  });

  describe('test Hhdden field ', () => {
    it('should pass with 1 hidden dimension', () => {
      expect(
        checkIsSubVizApplicable('GridChart', 'arc', [hideCol()])
      ).toBe(true);
    });
  });
});
