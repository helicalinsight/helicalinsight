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

  it("exports hierarchy rows using cube-formdata nesting", () => {
    const result = convertCubeFieldsDataToAgentData(
      {
        domainName: "",
        cubeDescription: "",
        cubeTopic: "",
        children: [
          {
            fields: "employee_name",
            isHierarchy: true,
            tableId: "323",
            columnId: "2672",
            columnName: "employee_details.employee_name",
            defaultFunction: "db.generic.groupBy.group",
            children: [
              {
                fields: "employee_name",
                isHierarchyChild: true,
                isDimensionCheck: true,
                tableId: "323",
                columnId: "2672",
                columnName: "employee_details.employee_name",
                defaultFunction: "db.generic.groupBy.group",
                semanticType: "entity",
                measure: { isMeasureCheck: false },
              },
              {
                fields: "address",
                isHierarchyChild: true,
                isDimensionCheck: true,
                tableId: "323",
                columnId: "2674",
                columnName: "employee_details.address",
                defaultFunction: "db.generic.groupBy.group",
                measure: { isMeasureCheck: false },
              },
            ],
          },
        ],
      },
      ensureShape({}),
    );

    expect(result.cube_info[0].dimensions).toHaveLength(1);
    expect(result.cube_info[0].dimensions[0]).toMatchObject({
      dimensionName: "employee_name",
      tableId: "323",
      columnId: "2672",
      semanticType: "",
      description: "",
      metric: { formula: "" },
      hierarchies: [
        {
          hierarchyName: "employee_name",
          primaryColumnId: "2672",
          levels: [
            {
              levelName: "employee_name",
              columnId: "2672",
              semanticType: "entity",
            },
            {
              levelName: "address",
              columnId: "2674",
            },
          ],
        },
      ],
    });
    expect(result.cube_info[0].dimensions[0].hierarchies[0].levels).toHaveLength(
      2,
    );
  });

  it("loads legacy flat dimensions without hierarchies unchanged", () => {
    const flatAgentData = ensureShape({
      cube_info: [
        {
          dimensions: [
            {
              dimensionName: "employee_name",
              tableId: "323",
              columnId: "2672",
              columnName: "employee_details.employee_name",
            },
            {
              dimensionName: "address",
              tableId: "323",
              columnId: "2674",
              columnName: "employee_details.address",
            },
          ],
          measures: [],
        },
      ],
    });

    const cubeFields = convertAgentDataToCubeFieldsData(flatAgentData);
    expect(cubeFields.hierarchyData.isHierarchyPresent).toBe(true);
    expect(cubeFields.hierarchyData.hierarchyList).toHaveLength(2);
    expect(cubeFields.children).toHaveLength(2);
    expect(cubeFields.children.every((child) => child.isDimensionCheck)).toBe(true);
    expect(cubeFields.children.map((child) => child.fields)).toEqual([
      "employee_name",
      "address",
    ]);
  });

  it("round-trips hierarchy through load and export", () => {
    const hierarchyAgentData = ensureShape({
      cube_info: [
        {
          dimensions: [
            {
              dimensionName: "employee_name",
              tableId: "323",
              columnId: "2672",
              columnName: "employee_details.employee_name",
              defaultFunction: "db.generic.groupBy.group",
              metric: { formula: "" },
              sortOrder: 0,
              hierarchies: [
                {
                  hierarchyName: "employee_name",
                  primaryColumnId: "2672",
                  tableId: "323",
                  columnName: "employee_details.employee_name",
                  levels: [
                    {
                      levelName: "employee_name",
                      tableId: "323",
                      columnId: "2672",
                      columnName: "employee_details.employee_name",
                      defaultFunction: "db.generic.groupBy.group",
                      metric: { formula: "" },
                    },
                    {
                      levelName: "address",
                      tableId: "323",
                      columnId: "2674",
                      columnName: "employee_details.address",
                      defaultFunction: "db.generic.groupBy.group",
                      metric: { formula: "" },
                    },
                    {
                      levelName: "cancellation_reason",
                      tableId: "325",
                      columnId: "2686",
                      columnName: "meeting_details.cancellation_reason",
                      defaultFunction: "db.generic.groupBy.group",
                      metric: { formula: "" },
                    },
                  ],
                },
              ],
            },
          ],
          measures: [],
        },
      ],
    });

    const cubeFields = convertAgentDataToCubeFieldsData(hierarchyAgentData);
    expect(cubeFields.hierarchyData.isHierarchyPresent).toBe(true);
    expect(cubeFields.hierarchyData.hierarchyList).toEqual([
      { hierarchyName: "employee_name", hierarchyKey: expect.any(String) },
    ]);
    expect(cubeFields.children).toHaveLength(1);
    expect(cubeFields.children[0].isHierarchy).toBe(true);
    expect(cubeFields.children[0].children.map((child) => child.fields)).toEqual([
      "employee_name",
      "address",
      "cancellation_reason",
    ]);

    const exported = convertCubeFieldsDataToAgentData(
      cubeFields,
      hierarchyAgentData,
    );
    expect(exported.cube_info[0].dimensions[0].hierarchies[0].levels).toHaveLength(
      3,
    );

    const reloaded = convertAgentDataToCubeFieldsData(exported);
    expect(reloaded.children[0].isHierarchy).toBe(true);
    expect(reloaded.children[0].children.map((child) => child.fields)).toEqual([
      "employee_name",
      "address",
      "cancellation_reason",
    ]);
  });

  it("registers all dimensions in hierarchyData for add-to-existing-hierarchy menu", () => {
    const cubeFields = convertAgentDataToCubeFieldsData(
      ensureShape({
        cube_info: [
          {
            dimensions: [
              {
                dimensionName: "address",
                tableId: "348",
                columnId: "2857",
                columnName: "employee_details.address",
              },
              {
                dimensionName: "destination456",
                tableId: "349",
                columnId: "2865",
                columnName: "travel_details.destination",
                hierarchies: [
                  {
                    hierarchyName: "destination456",
                    primaryColumnId: "2865",
                    tableId: "349",
                    columnName: "travel_details.destination",
                    levels: [
                      {
                        levelName: "destination457",
                        tableId: "349",
                        columnId: "2865",
                        columnName: "travel_details.destination",
                      },
                      {
                        levelName: "employee name 1",
                        tableId: "348",
                        columnId: "2855",
                        columnName: "employee_details.employee_name",
                      },
                    ],
                  },
                ],
              },
            ],
            measures: [],
          },
        ],
      }),
    );

    expect(cubeFields.hierarchyData.hierarchyList.map((item) => item.hierarchyName)).toEqual(
      ["address", "destination456"],
    );
    expect(cubeFields.children[0].isDimensionCheck).toBe(true);
    expect(cubeFields.children[1].isHierarchy).toBe(true);
  });
});
