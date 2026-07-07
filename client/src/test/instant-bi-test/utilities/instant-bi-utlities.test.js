import { derivedFormDataConvertorToReportProps } from "../../../components/hi-instant-bi/utils/instant-bi-utilities";

describe("addItem function", () => {
  test("working use case for dashboard-designer-component", () => {
    const reportProps = derivedFormDataConvertorToReportProps({
      derivedFormData: {
        visualization: "table",
        // columns: ["month"],
        limit: "",
        from: [],
        // where: [
        //   {
        //     filterValues: ["null", "2022", "2021"],
        //     filterCondition: "",
        //     filterColumn: "",
        //   },
        // ],
        groupBy: ["month"],
        aggregate: [
          {
            aggregateColumnName: "travels",
            aggregateFunction: "Total",
          },
        ],
        // "lematizedColumns":[
        // table:[
        // "commonColumn1":[col1,col2,col3],
        // "commonColumn2":[col1,col2,col3],
        // ]
        // ]
        columns: [{ table: "travel_details", column: "month" }],
        rows: [{ table: "travel_details", column: "month" }],
        where: [
          {
            table: "travel_details",
            column: "booking_platform",
            values: "Agent",
            condition: "",
          },
        ],
      },
      metadata: { metadataFileName: "Metadata_1.metadata", location: "naresh" },
    });
    expect(reportProps).toStrictEqual({
      mode: "instant-bi",
      metadata: {
        location: "naresh",
        metadataFileName: "Metadata_1.metadata",
      },
      columns: [{ table: "travel_details", column: "month" }],
      rows: [{ table: "travel_details", column: "month" }],
      filters: [
        {
          table: "travel_details",
          column: "booking_platform",
          values: "Agent",
          condition: "",
        },
      ],
      visualisationType: "Table",
    });
    expect(reportProps.mode).toBeTruthy();
    expect(reportProps.metadata).toBeTruthy();
    expect(reportProps.columns).toBeTruthy();
    expect(reportProps.rows).toBeTruthy();
    expect(reportProps.filters).toBeTruthy();
    expect(reportProps.visualisationType).toBeTruthy();
  });
});
