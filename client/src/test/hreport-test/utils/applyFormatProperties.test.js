import { applyFormatProperties } from "../../../components/hi-reports/hi-editing-area/utils/property-utils";

describe("applyFormatProperties", () => {
  test("to test applyFormatProperties function", () => {
    const fields = [
      {
        column: "travel_details.travel_cost",
        columnID: "26783",
        label: "sum_travel_cost",
        id: "9a38f639-68c4-4caa-88ed-55066423e484",
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
        hidden: false,
        metaDataAlias: "travel_cost",
        databaseName: "HIUSER",
        geographicType: "",
        isView: false,
      },
    ];
    const formatProperties = {
      formatFields: [],
    };
    const result = applyFormatProperties({ fields, properties: { format: formatProperties } });
    const expectedResult = {
      fields,
      properties: {
        format:
        {
          formatFields: [
            {
              id: "9a38f639-68c4-4caa-88ed-55066423e484",
              values: {
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
              }
            },
          ]
        }
      }
    };
    expect(result).toStrictEqual(expectedResult);
  });
});
