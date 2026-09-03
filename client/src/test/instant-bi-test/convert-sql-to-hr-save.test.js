import "regenerator-runtime/runtime";
import {
  buildHrReportFromParts,
  buildInlineReportFile,
  collectChatVizItems,
  convertSqlToHrSaveFormData,
  mapVizToHelical,
  parseCssColor,
  unwrapConvertHreportResponse,
  unwrapTablesMetadata,
  sqlPartsFromSql,
  sqlPartsFromVizModel,
} from "../../components/hi-instant-bi/utils/convert-sql-to-hr-save";

const travelMetadata = {
  classifier: "db.generic",
  name: "sampletraveldata",
  formData: {
    location: "0007",
    metadataFileName: "pg.metadata",
  },
  tables: {
    travel_details: {
      id: "t1",
      alias: "travel_details",
      name: "travel_details",
      columns: {
        booking_platform: {
          id: "2868",
          alias: "booking_platform",
          type: { "java.lang.String": "string" },
          defaultFunction: "db.generic.groupBy.group",
        },
        travel_cost: {
          id: "2866",
          alias: "travel_cost",
          type: { "java.lang.Integer": "numeric" },
          defaultFunction: "db.generic.aggregate.sum",
        },
        travel_type: {
          id: "2867",
          alias: "travel_type",
          type: { "java.lang.String": "string" },
          defaultFunction: "db.generic.groupBy.group",
        },
      },
    },
  },
};

const barParts = {
  metadata: travelMetadata,
  columns: [
    {
      table: "travel_details",
      column: "booking_platform",
      databaseFunction: "db.generic.groupBy.group",
      shelf: "row",
      alias: "Platform",
    },
    {
      table: "travel_details",
      column: "travel_cost",
      databaseFunction: "db.generic.aggregate.sum",
      shelf: "column",
      alias: "Cost",
    },
  ],
  filters: [
    {
      table: "travel_details",
      column: "booking_platform",
      condition: "EQUALS",
      value: ["Agent"],
    },
  ],
  orderBy: [
    {
      table: "travel_details",
      column: "travel_cost",
      direction: "desc",
      alias: "Cost",
    },
  ],
  viz: {
    chart_name: "bar",
    mark: "Chart",
    viz: "Bar",
    color: "#5470c6",
    background: "#ffffff",
    title: "Travel cost",
    colorField: "booking_platform",
  },
  reportInfo: {
    location: "",
    reportName: "Untitled 1",
  },
};

describe("convertSqlToHrSaveFormData", () => {
  test("unwraps convert-hreport sql and viz parts", () => {
    const wrapped = {
      status: 1,
      response: {
        sql_parts: { columns: [], location: "0007", metadataFileName: "pg.metadata" },
        viz_parts: { mark: "Chart" },
      },
    };
    expect(unwrapConvertHreportResponse(wrapped).sql_parts.metadataFileName).toBe("pg.metadata");
    expect(unwrapConvertHreportResponse({ metadata: travelMetadata }).metadata).toBe(travelMetadata);
  });

  test("unwraps Helical tables nested under an InstantBI model payload", () => {
    const modelMetadata = {
      formData: {
        location: "0806",
        metadataFileName: "Travle_Agent.model",
      },
      data: {
        modelName: "Travel Agent",
        metadata: {
          location: "test",
          metadataFileName: "pg_sample_travel_data.metadata",
          data: travelMetadata,
        },
      },
    };
    const unwrapped = unwrapTablesMetadata(modelMetadata);
    expect(unwrapped.tables.travel_details).toBeDefined();
    expect(unwrapped.formData.location).toBe("test");
    expect(unwrapped.formData.metadataFileName).toBe("pg_sample_travel_data.metadata");
  });

  test("maps InstantBI chart names onto HelicalReports selectedType", () => {
    expect(mapVizToHelical({ mark: "Chart", viz: "Bar" })).toEqual({
      selectedType: "Antcharts",
      subVizType: "bar",
      mark: "Chart",
      viz: "Bar",
    });
    expect(mapVizToHelical({ chart_name: "table" }).selectedType).toBe("Table");
    expect(mapVizToHelical({ mark: "Card", viz: "Bar" }).selectedType).toBe("Card");
    expect(mapVizToHelical({ mark: "Grid Table" }).selectedType).toBe("SyncChart");
    expect(mapVizToHelical({ mark: "Maps", viz: "Heatmap" })).toMatchObject({
      selectedType: "MapChart",
      subVizType: "heatmap",
    });
  });

  test("parses hex color into the property-pane rgba shape", () => {
    expect(parseCssColor("#5470c6")).toEqual({ r: 84, g: 112, b: 198, a: 1 });
  });

  test("builds a live report with columns, filter, orderBy, bar viz, color, and title", () => {
    const report = buildHrReportFromParts(barParts);
    const rows = report.fields.filter((field) => field.addedAs === "row");
    const columns = report.fields.filter((field) => field.addedAs === "column");
    const marks = report.fields.filter((field) => field.addedAs === "color");

    expect(rows.length).toBe(1);
    expect(columns.length).toBe(1);
    expect(rows[0].column).toContain("booking_platform");
    expect(columns[0].column).toContain("travel_cost");
    expect(columns[0].orderBy).toEqual(["desc"]);
    expect(report.filters.length).toBe(1);
    expect(report.filters[0].column).toContain("booking_platform");
    expect(report.metadata.uid).toBeDefined();
    expect(report.metadata.tables.travel_details.name).toBe("travel_details");
    expect(report.selectedType).toBe("Antcharts");
    expect(report.marksList[0].subVizType).toBe("bar");
    expect(report.properties.title).toMatchObject({ show: true, value: "Travel cost" });
    expect(report.properties.formatColor.defaultColor).toEqual({
      r: 84,
      g: 112,
      b: 198,
      a: 1,
    });
    expect(marks.length).toBe(1);
  });

  test("returns Helical Report save formData, not Adhoc executeQuery wire formData", () => {
    const saveData = convertSqlToHrSaveFormData(barParts);
    expect(saveData.isHrReport).toBe(true);
    expect(Array.isArray(saveData.columns)).toBe(true);
    expect(saveData.state.selectedType).toBe("Antcharts");
    expect(saveData.state.properties.title.value).toBe("Travel cost");
    expect(saveData.state.properties.formatColor.defaultColor).toEqual({
      r: 84,
      g: 112,
      b: 198,
      a: 1,
    });
    expect(saveData).not.toHaveProperty("limitBy");
    expect(saveData).not.toHaveProperty("functions");
  });

  test("builds an inline dashboard file with metadata and no path or name", () => {
    const file = buildInlineReportFile(barParts);
    expect(file.inline).toBe(true);
    expect(file.visualisationType).toBe("Antcharts");
    expect(file.rows[0]).toMatchObject({
      table: "travel_details",
      column: "booking_platform",
      defaultFunction: "db.generic.groupBy.group",
    });
    expect(file.columns[0]).toMatchObject({
      table: "travel_details",
      column: "travel_cost",
    });
    expect(file.filters[0].column).toBe("booking_platform");
    expect(file.marks[0]).toMatchObject({
      column: "booking_platform",
      markType: "color",
    });
    expect(file.metadata.tables).toBeDefined();
    expect(file.fields.length).toBeGreaterThan(0);
    expect(file.hydratedFilters.length).toBe(1);
    expect(file).not.toHaveProperty("path");
    expect(file).not.toHaveProperty("name");
  });

  test("applies EXTRACT month as a date function with GROUP BY so the query can run", () => {
    const report = buildHrReportFromParts({
      metadata: {
        ...travelMetadata,
        tables: {
          travel_details: {
            ...travelMetadata.tables.travel_details,
            columns: {
              ...travelMetadata.tables.travel_details.columns,
              travel_date: {
                id: "2859",
                alias: "travel_date",
                type: { "java.sql.Timestamp": "dateTime" },
                defaultFunction: "db.generic.groupBy.group",
              },
            },
          },
        },
      },
      columns: [
        {
          table: "travel_details",
          column: "travel_date",
          databaseFunction: "sql.dateTime.month",
          shelf: "row",
          alias: "Month",
        },
        {
          table: "travel_details",
          column: "travel_cost",
          databaseFunction: "db.generic.aggregate.sum",
          shelf: "column",
          alias: "Cost",
        },
      ],
      filters: [
        {
          table: "travel_details",
          column: "travel_date",
          databaseFunction: "sql.dateTime.month",
          condition: "EQ",
          value: 8,
        },
      ],
      viz: {
        chart_name: "gauge",
        mark: "Chart",
        viz: "Arc",
        title: "Travel Cost by Month",
      },
    });
    const dateField = report.fields.find((field) => field.column.includes("travel_date"));
    const costField = report.fields.find((field) => field.column.includes("travel_cost"));
    expect(dateField.groupBy).toEqual(["db.generic.groupBy.group"]);
    expect(dateField.databaseFunction).toMatchObject({
      key: "sql.dateTime.month",
      returns: "numeric",
    });
    expect(dateField.month).toBeUndefined();
    expect(costField.aggregate[0]).toContain("aggregate.sum");
    expect(dateField.addedAs).toBe("column");
    expect(costField.addedAs).toBe("row");
    expect(report.filters[0].values).toEqual([8]);
    expect(report.filters[0].condition).toBe("EQUALS");
    expect(report.filters[0].databaseFunction.key).toBe("sql.dateTime.month");
    expect(report.marksList[0].subVizType).toBe("arc");
  });

  test("collects chat viz items from loaded responses and skips vf_template", () => {
    const items = collectChatVizItems({
      activeChatID: "c1",
      chats: [
        {
          chatID: "c1",
          messageList: [
            { isUser: true, text: "show sales" },
            {
              isUser: false,
              chatSequenceId: "seq-3",
              sql: "```sql\nSELECT region FROM t\n```",
              fullChatResponse: {
                viz: { chart_name: "bar", vf_template: "function(){}" },
                sql: { raw_sql: "```sql\nSELECT region FROM t\n```" },
                summary: { insight: "West leads" },
              },
            },
          ],
        },
      ],
    });
    expect(items).toEqual([
      {
        id: "seq-3",
        chat_sequence_id: "seq-3",
        chatid: "c1",
        user_query: "show sales",
        data_model: null,
        viz_model: null,
        sql: "SELECT region FROM t",
        viz: { chart_name: "bar" },
        summary: "West leads",
      },
    ]);
  });

  test("skips failed or error chats when collecting viz items", () => {
    const items = collectChatVizItems({
      activeChatID: "c1",
      chats: [
        {
          chatID: "c1",
          messageList: [
            {
              isUser: false,
              chatSequenceId: "ok",
              fullChatResponse: {
                viz: { chart_name: "bar" },
                sql: { raw_sql: "SELECT 1" },
              },
            },
            {
              isUser: false,
              error: true,
              chatSequenceId: "failed-status",
              fullChatResponse: {
                viz: { chart_name: "bar" },
                sql: { raw_sql: "SELECT 2" },
              },
            },
            {
              isUser: false,
              chatSequenceId: "sql-error",
              fullChatResponse: {
                error: "Could not generate SQL",
                viz: { chart_name: "bar" },
                sql: { raw_sql: "SELECT 3" },
              },
            },
            {
              isUser: false,
              status: "failed",
              chatSequenceId: "status-failed",
              fullChatResponse: {
                viz: { chart_name: "bar" },
                sql: { raw_sql: "SELECT 4" },
              },
            },
          ],
        },
      ],
    });
    expect(items.map((item) => item.id)).toEqual(["ok"]);
  });

  test("derives sql_parts from InstantBI viz_model shelves when Python parts are empty", () => {
    const parts = sqlPartsFromVizModel(
      {
        viz_model: {
          data: {
            rows: ["booking_platform"],
            columns: ["Travel Cost"],
            filters: [{ name: "booking_platform", value: "MakeMyTrip" }],
          },
        },
      },
      {
        ...travelMetadata,
        tables: {
          travel_details: {
            ...travelMetadata.tables.travel_details,
            columns: {
              ...travelMetadata.tables.travel_details.columns,
              travel_cost: {
                ...travelMetadata.tables.travel_details.columns.travel_cost,
                alias: "Travel Cost",
              },
            },
          },
        },
      }
    );
    expect(parts.columns).toEqual([
      expect.objectContaining({
        table: "travel_details",
        column: "booking_platform",
        shelf: "row",
      }),
      expect.objectContaining({
        table: "travel_details",
        column: "travel_cost",
        shelf: "column",
      }),
    ]);
    expect(parts.filters[0]).toMatchObject({
      table: "travel_details",
      column: "booking_platform",
      value: "MakeMyTrip",
    });
  });

  test("maps InstantBI display names onto snake_case metadata columns", () => {
    const parts = sqlPartsFromVizModel(
      {
        viz_model: {
          data: {
            rows: ["Travel Type"],
            columns: ["Travel Cost"],
          },
        },
      },
      travelMetadata
    );
    expect(parts.columns).toEqual([
      expect.objectContaining({
        table: "travel_details",
        column: "travel_type",
        shelf: "row",
      }),
      expect.objectContaining({
        table: "travel_details",
        column: "travel_cost",
        shelf: "column",
      }),
    ]);
  });

  test("extracts sql_parts from quoted table.column SQL when viz shelves are missing", () => {
    const parts = sqlPartsFromSql(
      'SELECT "travel_details"."travel_type" AS "Travel Type", SUM("travel_details"."travel_cost") AS "Travel Cost" FROM "travel_details" GROUP BY "travel_details"."travel_type"',
      travelMetadata
    );
    expect(parts.columns).toEqual([
      expect.objectContaining({
        table: "travel_details",
        column: "travel_type",
        shelf: "row",
      }),
      expect.objectContaining({
        table: "travel_details",
        column: "travel_cost",
        shelf: "column",
        databaseFunction: "db.generic.aggregate.sum",
      }),
    ]);
  });
});
