import {
  convertAgentDataToCubeFieldsData,
  convertCubeFieldsDataToAgentData,
  createColumnChildFromDropRecord,
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
      topics: [
        { topic: "Travel", description: "Topics description" },
        { topic: "Meetings", description: "Topics description" },
      ],
    },
  ],
  cube: [
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
  cube: [
    {
      ...sampleNewFormat.cube[0],
      cubeName: "",
      dimensions: [
        {
          ...sampleNewFormat.cube[0].dimensions[0],
          aiContext: { instructions: "", synonyms: "", examples: "" },
          sortOrder: 0,
        },
      ],
      measures: [
        {
          ...sampleNewFormat.cube[0].measures[0],
          aiContext: { instructions: "", synonyms: "", examples: "" },
          sortOrder: 1,
        },
      ],
    },
  ],
};

describe("agent-cube-bridge", () => {
  it("loads domain and cube into cube field rows", () => {
    const cubeFields = convertAgentDataToCubeFieldsData(sampleNewFormat);

    expect(cubeFields.domainName).toBe("Sales Operation");
    expect(cubeFields.cubeDescription).toBe("Travel domain description");
    expect(cubeFields.cubeTopic).toBe("Travel, Meetings");
    expect(cubeFields.cubeName).toBe("");
    expect(cubeFields.businessViewEntries[0]).toMatchObject({
      domain: "Sales Operation",
      businessDescription: "Travel domain description",
      topics: [
        { name: "Travel", description: "Topics description" },
        { name: "Meetings", description: "Topics description" },
      ],
    });
    expect(cubeFields.children[1]).toMatchObject({
      fields: "Destination Count",
      tableId: "112",
      columnId: "1070",
      defaultFunction: "db.generic.aggregate.sum",
      column: { defaultFunction: "db.generic.aggregate.sum" },
      measure: { isMeasureCheck: true, Format: "0.00" },
    });
  });

  it("exports business view entries with nested topics under domain", () => {
    const result = convertCubeFieldsDataToAgentData(
      {
        businessViewEntries: [
          {
            domain: "Sales Operation",
            businessDescription: "Domain description",
            topics: [
              { name: "Travel", description: "Travel topic description" },
              { name: "Meetings", description: "Meetings topic description" },
            ],
          },
        ],
        cubeName: "Travel Cube",
        children: [
          {
            fields: "Booking Platform",
            columnName: "booking_platform",
            tableId: "112",
            columnId: "1074",
            defaultFunction: "db.generic.groupBy.group",
            semanticType: "entity",
            domain: "Sales Operation",
            topic: "Travel",
            table: { name: "travel_details" },
            isDimensionCheck: true,
            measure: { isMeasureCheck: false },
          },
        ],
      },
      ensureShape({}),
    );

    expect(result.domain[0]).toEqual({
      domain_name: "Sales Operation",
      description: "Domain description",
      topics: [
        {
          topic: "Travel",
          description: "Travel topic description",
          components: [{ id: "1074", name: "Booking Platform" }],
        },
        { topic: "Meetings", description: "Meetings topic description" },
      ],
    });
    expect(result.topics).toBeUndefined();
    expect(result.cube[0].dimensions[0].tableId).toBe("112");
    expect(result.cube[0].dimensions[0].defaultFunction).toBe(
      "db.generic.groupBy.group",
    );
    expect(result.cube[0].dimensions[0].columnName).toBe(
      "travel_details.booking_platform",
    );
    expect(result.cube[0].cubeName).toBe("");
  });

  it("serializes raw JSON as cube + domain with nested topics", () => {
    const display = JSON.parse(serializeAgentDataForDisplay(sampleNewFormat));
    expect(display.cube_metadata).toBeUndefined();
    expect(display.cube).toBeDefined();
    expect(display.cube[0].cubeName).toBe("");
    expect(display.domain).toBeDefined();
    expect(display.topics).toBeUndefined();
    expect(display.domain[0].topics).toEqual([
      { topic: "Travel", description: "Topics description" },
      { topic: "Meetings", description: "Topics description" },
    ]);
  });

  it("groups field AI context values in the raw JSON payload", () => {
    const display = JSON.parse(
      serializeAgentDataForDisplay({
        cube: [
          {
            cubeName: "",
            dimensions: [
              {
                dimensionName: "address",
                instructions: "Use the mailing address",
                synonyms: "location, residence",
                examples: "home address",
              },
            ],
            measures: [],
          },
        ],
        domain: [],
      }),
    );
    const dimension = display.cube[0].dimensions[0];

    expect(dimension.aiContext).toEqual({
      instructions: "Use the mailing address",
      synonyms: "location, residence",
      examples: "home address",
    });
    expect(dimension).not.toHaveProperty("instructions");
    expect(dimension).not.toHaveProperty("synonyms");
    expect(dimension).not.toHaveProperty("examples");
  });

  it("loads nested aiContext values into cube field rows", () => {
    const cubeFields = convertAgentDataToCubeFieldsData({
      cube: [
        {
          cubeName: "",
          dimensions: [
            {
              dimensionName: "address",
              aiContext: {
                instructions: "Use the mailing address",
                synonyms: ["location", "residence"],
                examples: "home address",
              },
            },
          ],
          measures: [],
        },
      ],
      domain: [],
    });

    expect(cubeFields.children[0]).toMatchObject({
      instructions: "Use the mailing address",
      synonyms: "location, residence",
      example: "home address",
    });
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

    expect(migrated.cube[0].dimensions[0].columnName).toBe("address");
    expect(migrated.domain[0].description).toBe("Legacy description");
    expect(migrated.cube[0].cubeName).toBe("");
  });

  it("round-trips through load and export with empty cubeName", () => {
    const cubeFields = convertAgentDataToCubeFieldsData(sampleNewFormat);
    const exported = convertCubeFieldsDataToAgentData(cubeFields, sampleNewFormat);
  });

  it("preserves mixed dimension and measure order across save and load", () => {
    const agentData = ensureShape({
      domain: [{ domain_name: "dom", description: "des", topics: ["t"] }],
      cube: [
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
    expect(exported.cube[0].dimensions[0].sortOrder).toBe(0);
    expect(exported.cube[0].measures[0].sortOrder).toBe(1);
    expect(exported.cube[0].dimensions[1].sortOrder).toBe(2);

    const serialized = JSON.parse(serializeAgentData(exported));
    [...serialized.cube[0].dimensions, ...serialized.cube[0].measures].forEach(
      (field) => {
        expect(field.sortOrder).toBeDefined();
      },
    );

    const displaySerialized = JSON.parse(serializeAgentDataForDisplay(exported));
    [
      ...displaySerialized.cube[0].dimensions,
      ...displaySerialized.cube[0].measures,
    ].forEach((field) => {
      expect(field).not.toHaveProperty("sortOrder");
    });
  });

  it("exports dimension sort values into raw JSON and reloads them", () => {
    const cubeFields = {
      children: [
        {
          ...createColumnChildFromDropRecord({
            alias: "address",
            dataType: "text",
            columnName: "address",
            tableId: "348",
            columnId: "2857",
            tableName: "employee_details",
          }),
          sort: { isSortCheck: true, value: "Descending" },
        },
        {
          ...createColumnChildFromDropRecord({
            alias: "employee_name",
            dataType: "text",
            columnName: "employee_name",
            tableId: "348",
            columnId: "2855",
            tableName: "employee_details",
          }),
          sort: { isSortCheck: false, value: "Natural" },
        },
      ],
    };

    const exported = convertCubeFieldsDataToAgentData(cubeFields);
    expect(exported.cube[0].dimensions[0].sort).toBe("Descending");
    expect(exported.cube[0].dimensions[1].sort).toBe("Natural");

    const displaySerialized = JSON.parse(serializeAgentDataForDisplay(exported));
    expect(displaySerialized.cube[0].dimensions[0].sort).toBe("Descending");
    expect(displaySerialized.cube[0].dimensions[1].sort).toBe("Natural");

    const reloaded = convertAgentDataToCubeFieldsData(exported);
    expect(reloaded.children[0].sort).toEqual({
      isSortCheck: true,
      value: "Descending",
    });
    expect(reloaded.children[1].sort).toEqual({
      isSortCheck: false,
      value: "Natural",
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
      semanticType: "Number",
      measure: { isMeasureCheck: true },
      agentSource: { kind: "manual-metric", metricName: "metric_1" },
    });
    expect(String(child.metricId || "").trim()).not.toBe("");
  });

  it("auto-selects semantic type Text for dimensions and Number for measures on create", () => {
    expect(
      createColumnChildFromDropRecord({
        alias: "address",
        dataType: "text",
        columnName: "address",
      }),
    ).toMatchObject({
      isDimensionCheck: true,
      semanticType: "Text",
      measure: { isMeasureCheck: false },
    });
    expect(
      createColumnChildFromDropRecord({
        alias: "age",
        dataType: "numeric",
        columnName: "age",
      }),
    ).toMatchObject({
      isDimensionCheck: false,
      semanticType: "Number",
      measure: { isMeasureCheck: true },
    });
  });

  it("exports manual metrics with metricId instead of columnId", () => {
    const result = convertCubeFieldsDataToAgentData(
      {
        domainName: "Sales Operation",
        cubeDescription: "Domain description",
        cubeTopic: "Travel",
        children: [createManualMetricChild([])],
      },
      ensureShape({}),
    );

    expect(result.cube[0].measures[0]).toMatchObject({
      measureName: "metric_1",
      aggregator: "Sum",
      tableId: "",
      columnName: "",
      formatString: "0.00",
      metric: { formula: "" },
    });
    expect(String(result.cube[0].measures[0].metricId || "").trim()).not.toBe(
      "",
    );
    expect(result.cube[0].measures[0]).not.toHaveProperty("columnId");
  });

  it("exports None aggregator with db.generic.aggregate.none defaultFunction", () => {
    const result = convertCubeFieldsDataToAgentData(
      {
        children: [
          {
            fields: "age",
            columnName: "age",
            tableId: "348",
            columnId: "2856",
            defaultFunction: "",
            semanticType: "Number",
            table: { name: "employee_details" },
            column: { defaultFunction: "db.generic.aggregate.sum" },
            measure: { isMeasureCheck: true, Format: "0.00" },
            aggregation: { isAggregationCheck: false, value: "None" },
          },
        ],
      },
      ensureShape({}),
    );

    expect(result.cube[0].measures[0]).toMatchObject({
      measureName: "age",
      aggregator: "None",
      defaultFunction: "db.generic.aggregate.none",
      columnId: "2856",
    });
  });

  it("loads legacy agent data with dimensions before measures when sortOrder is absent", () => {
    const legacyData = ensureShape({
      cube: [
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

    expect(result.cube[0].dimensions).toHaveLength(1);
    expect(result.cube[0].dimensions[0]).toMatchObject({
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
    expect(result.cube[0].dimensions[0].hierarchies[0].levels).toHaveLength(
      2,
    );
  });

  it("loads legacy flat dimensions without hierarchies unchanged", () => {
    const flatAgentData = ensureShape({
      cube: [
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
      cube: [
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
    expect(exported.cube[0].dimensions[0].hierarchies[0].levels).toHaveLength(
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
        cube: [
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
