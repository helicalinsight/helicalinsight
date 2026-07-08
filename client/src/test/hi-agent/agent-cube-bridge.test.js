import {
  convertAgentDataToCubeFieldsData,
  convertCubeFieldsDataToAgentData,
  createManualMetricChild,
  getNextManualMetricName,
  normalizeAgentData,
  serializeAgentData,
  serializeAgentDataForDisplay,
} from "../../components/hi-agent/utils/agent-cube-bridge";
import { ensureShape } from "../../components/hi-agent/components/semantic-metadata-editor/semantic-metadata-utils";

const sampleNewFormat = {
  domain: [
    {
      domain_name: "Sales Operation",
      description: "Travel domain description",
      topics: ["Travel", "Meetings"],
    },
  ],
  cube_info: [
    {
      cubeName: "Travel Cube",
      dimensions: [
        {
          dimensionName: "Booking Platform",
          semanticType: "entity",
          tableId: "112",
          columnName: "booking_platform",
          columnId: "1074",
          defaultFunction: "db.generic.groupBy.group",
          description: "Platform used for booking",
          metric: { formula: "count(*)" },
        },
      ],
      measures: [
        {
          measureName: "Destination Count",
          aggregator: "Sum",
          columnId: "1070",
          tableId: "112",
          defaultFunction: "db.generic.aggregate.sum",
          formatString: "0.00",
          semanticType: "numeric",
          columnName: "destination_id",
          description: "Number of destinations",
          metric: { formula: "" },
        },
      ],
    },
  ],
};

const sampleExportedFormat = {
  ...sampleNewFormat,
  cube_info: [
    {
      ...sampleNewFormat.cube_info[0],
      cubeName: "",
      dimensions: [
        { ...sampleNewFormat.cube_info[0].dimensions[0], sortOrder: 0 },
      ],
      measures: [
        { ...sampleNewFormat.cube_info[0].measures[0], sortOrder: 1 },
      ],
    },
  ],
};

describe("agent-cube-bridge", () => {
  it("loads domain and cube_info into cube field rows", () => {
    const cubeFields = convertAgentDataToCubeFieldsData(sampleNewFormat);

    expect(cubeFields.domainName).toBe("Sales Operation");
    expect(cubeFields.cubeDescription).toBe("Travel domain description");
    expect(cubeFields.cubeTopic).toBe("Travel, Meetings");
    expect(cubeFields.cubeName).toBe("");
    expect(cubeFields.children[1]).toMatchObject({
      fields: "Destination Count",
      tableId: "112",
      columnId: "1070",
      defaultFunction: "db.generic.aggregate.sum",
      column: { defaultFunction: "db.generic.aggregate.sum" },
      measure: { isMeasureCheck: true, Format: "0.00" },
    });
  });

  it("exports cube field rows into domain and cube_info", () => {
    const result = convertCubeFieldsDataToAgentData(
      {
        domainName: "Sales Operation",
        cubeDescription: "Domain description",
        cubeTopic: "Travel, Meetings",
        cubeName: "Travel Cube",
        children: [
          {
            fields: "Booking Platform",
            columnName: "booking_platform",
            tableId: "112",
            columnId: "1074",
            defaultFunction: "db.generic.groupBy.group",
            semanticType: "entity",
            table: { name: "travel_details" },
            measure: { isMeasureCheck: false },
          },
        ],
      },
      ensureShape({}),
    );

    expect(result.domain[0]).toMatchObject({
      domain_name: "Sales Operation",
      description: "Domain description",
      topics: ["Travel", "Meetings"],
    });
    expect(result.cube_info[0].dimensions[0].tableId).toBe("112");
    expect(result.cube_info[0].dimensions[0].defaultFunction).toBe(
      "db.generic.groupBy.group",
    );
    expect(result.cube_info[0].dimensions[0].columnName).toBe(
      "travel_details.booking_platform",
    );
    expect(result.cube_info[0].cubeName).toBe("");
  });

  it("serializes raw JSON as domain + cube_info only", () => {
    const display = JSON.parse(serializeAgentDataForDisplay(sampleNewFormat));
    expect(display.cube_metadata).toBeUndefined();
    expect(display.cube_info).toBeDefined();
    expect(display.cube_info[0].cubeName).toBe("");
  });

  it("migrates legacy cube_metadata on normalize", () => {
    const migrated = normalizeAgentData({
      metadata_info: {
        metadata: {
          domain: ["Sales Operation"],
          description: "Legacy description",
        },
      },
      domain: [{ domain_name: "Sales Operation", topics: ["Travel"] }],
      cube_metadata: [
        {
          database_table: "employee_details",
          columns: [{ column_name: "address", semantic_type: "entity" }],
        },
      ],
      business_metrics: [],
      synonyms: [],
      examples: [],
      topic_mappings: [],
    });

    expect(migrated.cube_info[0].dimensions[0].columnName).toBe("address");
    expect(migrated.domain[0].description).toBe("Legacy description");
    expect(migrated.cube_info[0].cubeName).toBe("");
  });

  it("round-trips through load and export with empty cubeName", () => {
    const cubeFields = convertAgentDataToCubeFieldsData(sampleNewFormat);
    const exported = convertCubeFieldsDataToAgentData(cubeFields, sampleNewFormat);
    expect(JSON.parse(serializeAgentData(exported))).toEqual(sampleExportedFormat);
  });

  it("preserves mixed dimension and measure order across save and load", () => {
    const agentData = ensureShape({
      domain: [{ domain_name: "dom", description: "des", topics: ["t"] }],
      cube_info: [
        {
          cubeName: "",
          dimensions: [
            {
              dimensionName: "client_name",
              columnId: "1045",
              tableId: "109",
              columnName: "meeting_details.client_name",
              sortOrder: 0,
            },
            {
              dimensionName: "client_name_1",
              columnId: "1046",
              tableId: "109",
              columnName: "meeting_details.client_name",
              sortOrder: 2,
            },
          ],
          measures: [
            {
              measureName: "meeting_id",
              columnId: "1042",
              tableId: "109",
              columnName: "meeting_details.meeting_id",
              aggregator: "Sum",
              sortOrder: 1,
            },
          ],
        },
      ],
    });
    const cubeFields = convertAgentDataToCubeFieldsData(agentData);
    expect(cubeFields.children.map((child) => child.fields)).toEqual([
      "client_name",
      "meeting_id",
      "client_name_1",
    ]);

    const exported = convertCubeFieldsDataToAgentData(cubeFields, agentData);
    const reloaded = convertAgentDataToCubeFieldsData(exported);
    expect(reloaded.children.map((child) => child.fields)).toEqual([
      "client_name",
      "meeting_id",
      "client_name_1",
    ]);
    expect(exported.cube_info[0].dimensions[0].sortOrder).toBe(0);
    expect(exported.cube_info[0].measures[0].sortOrder).toBe(1);
    expect(exported.cube_info[0].dimensions[1].sortOrder).toBe(2);

    const serialized = JSON.parse(serializeAgentData(exported));
    [...serialized.cube_info[0].dimensions, ...serialized.cube_info[0].measures].forEach(
      (field) => {
        expect(field.sortOrder).toBeDefined();
      },
    );

    const displaySerialized = JSON.parse(serializeAgentDataForDisplay(exported));
    [
      ...displaySerialized.cube_info[0].dimensions,
      ...displaySerialized.cube_info[0].measures,
    ].forEach((field) => {
      expect(field).not.toHaveProperty("sortOrder");
    });
  });

  it("creates manual metric rows with incremental names", () => {
    expect(getNextManualMetricName([])).toBe("metric_1");
    expect(getNextManualMetricName([{ fields: "metric_1" }])).toBe("metric_2");

    const child = createManualMetricChild([]);
    expect(child).toMatchObject({
      fields: "metric_1",
      columnName: "",
      tableId: "",
      columnId: "",
      measure: { isMeasureCheck: true },
      agentSource: { kind: "manual-metric", metricName: "metric_1" },
    });
  });

  it("exports manual metrics with empty table and column references", () => {
    const result = convertCubeFieldsDataToAgentData(
      {
        domainName: "Sales Operation",
        cubeDescription: "Domain description",
        cubeTopic: "Travel",
        children: [createManualMetricChild([])],
      },
      ensureShape({}),
    );

    expect(result.cube_info[0].measures[0]).toMatchObject({
      measureName: "metric_1",
      aggregator: "Sum",
      columnId: "",
      tableId: "",
      columnName: "",
      formatString: "0.00",
      metric: { formula: "" },
    });
  });

  it("loads legacy agent data with dimensions before measures when sortOrder is absent", () => {
    const legacyData = ensureShape({
      cube_info: [
        {
          dimensions: [
            { dimensionName: "client_name" },
            { dimensionName: "client_name_1" },
          ],
          measures: [{ measureName: "meeting_id" }],
        },
      ],
    });
    const cubeFields = convertAgentDataToCubeFieldsData(legacyData);
    expect(cubeFields.children.map((child) => child.fields)).toEqual([
      "client_name",
      "client_name_1",
      "meeting_id",
    ]);
  });
});
