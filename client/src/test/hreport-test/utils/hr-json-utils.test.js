import {
  METADATA_MISMATCH_MESSAGE,
  buildMetadataMismatchReportData,
  getReportJsonFetchDecision,
  mergeReportJsonIntoReport,
  normalizeReportForFetch,
  serializeReportJson,
} from "../../../components/hi-reports/utils/hr-json-utils";

const metadata = {
  name: "HIUSER",
  tables: {
    travel_details: {
      name: "travel_details",
      columns: {
        travel_date: { id: "col-1", name: "travel_date", alias: "travel_date" },
        destination: { id: "col-2", name: "destination", alias: "destination" },
      },
    },
  },
};

describe("getReportJsonFetchDecision", () => {
  it("it should rreturns no fields when columns/rows are missing", () => {
    const decision = getReportJsonFetchDecision({
      metadata,
      fields: [{ addedAs: "tooltip", column: "travel_details.travel_date" }],
    });
    expect(decision.shouldFetch).toBe(false);
    expect(decision.reason).toBe("no-fields");
  });

  it("should fetches when column/row fields match metadata ids", () => {
    const decision = getReportJsonFetchDecision({
      metadata,
      fields: [
        {
          addedAs: "column",
          column: "travel_details.travel_date",
          columnID: "col-1",
          databaseName: "HIUSER",
        },
        {
          addedAs: "row",
          column: "travel_details.destination",
          columnID: "col-2",
        },
      ],
    });
    expect(decision.shouldFetch).toBe(true);
    expect(decision.reason).toBe("matched");
  });

  it("ir shoudl reports metadata mismatch when column ids belong to another metadata", () => {
    const decision = getReportJsonFetchDecision({
      metadata,
      fields: [
        {
          addedAs: "column",
          column: "travel_details.travel_date",
          columnID: "other-metadata-col",
          databaseName: "OTHER_DB",
        },
      ],
    });
    expect(decision.shouldFetch).toBe(false);
    expect(decision.reason).toBe("metadata-mismatch");
    expect(decision.message).toBe(METADATA_MISMATCH_MESSAGE);
  });

  it("it should report metadata mismatch when database name differs", () => {
    const decision = getReportJsonFetchDecision({
      metadata,
      fields: [
        {
          addedAs: "column",
          column: "travel_details.travel_date",
          columnID: "col-1",
          databaseName: "OTHER_DB",
        },
      ],
    });
    expect(decision.shouldFetch).toBe(false);
    expect(decision.reason).toBe("metadata-mismatch");
  });

  it("shoudl buidss canvas message payload for mismatch", () => {
    expect(buildMetadataMismatchReportData()).toEqual({
      invalid: true,
      message: METADATA_MISMATCH_MESSAGE,
      data: [],
      loading: false,
    });
  });

  it("it shoudl be  missing options/filters before fetch", () => {
    const normalized = normalizeReportForFetch(
      {
        id: "r1",
        metadata,
        fields: [{ addedAs: "column", column: "travel_details.travel_date" }],
        options: null,
        filters: null,
      },
      { name: "u1" },
    );
    expect(normalized.options.sample).toBe("sample");
    expect(normalized.filters).toEqual([]);
    expect(normalized.user).toEqual({ name: "u1" });
  });

  it("should keep protected keys when merging pasting thee JSON", () => {
    const merged = mergeReportJsonIntoReport(
      {
        id: "report-1",
        metadata,
        fields: [],
        reportData: { data: [1] },
      },
      {
        id: "hacked",
        metadata: null,
        fields: [
          {
            addedAs: "column",
            column: "travel_details.travel_date",
            columnID: "col-1",
          },
        ],
      },
    );
    expect(merged.id).toBe("report-1");
    expect(merged.metadata).toEqual(metadata);
    expect(merged.reportData).toEqual({});
    expect(merged.fields).toHaveLength(1);
    expect(JSON.parse(serializeReportJson(merged)).id).toBeUndefined();
  });
});
