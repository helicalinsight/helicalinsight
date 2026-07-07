import { getSuggestionValue } from "../../../../../components/hi-reports/hi-fields-area/utils/suggetions";

describe("getSuggestionValue", () => {
  test("returns an object with a column value and type", () => {
    const suggestion = {
      column: "travel_details.booking_platform",
      columnID: "43087",
      label: "booking_platform",
      id: "91949bb4-b8b2-45cf-aa0f-80b73e914a72",
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
      databaseName: "",
    };
    const result = getSuggestionValue(suggestion);
    const expectedResult = {
        type: "column",
        value: "booking_platform",
      };
      
    expect(result).toEqual(expectedResult);
  });

});
