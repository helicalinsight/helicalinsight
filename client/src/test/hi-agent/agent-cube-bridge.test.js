import {
  convertAgentDataToCubeFieldsData,
  convertCubeFieldsDataToAgentData,
  getAgentStateFromCubeFields,
  mergeDisplayAgentDataWithStored,
  serializeAgentData,
  serializeAgentDataForDisplay,
  stripAgentDataForDisplay,
} from "../../components/hi-agent/utils/agent-cube-bridge";
import { ensureShape } from "../../components/hi-agent/components/semantic-metadata-editor/semantic-metadata-utils";

describe("agent-cube-bridge cube_metadata export", () => {
  

  it("should exports cube_metadata via getAgentStateFromCubeFields", () => {
    const state = getAgentStateFromCubeFields(
      {
        domainName: "",
        cubeDescription: "",
        children: [
          {
            fields: "address",
            columnName: "address",
            semanticType: "entity",
            table: { name: "employee_details" },
            measure: { isMeasureCheck: false },
          },
        ],
      },
      ensureShape({}),
    );

    expect(state.cube_metadata).toHaveLength(1);
    expect(state.cube_metadata[0].database_table).toBe("employee_details");
    expect(state.cube_metadata[0].columns[0].column_name).toBe("address");
  });

  it("exports empty domain when domain name is blank", () => {
    const result = convertCubeFieldsDataToAgentData(
      {
        domainName: "",
        cubeDescription: "",
        children: [
          {
            fields: "meeting_id",
            columnName: "meeting_id",
            topic: "Meetings, Travel",
            table: { name: "meeting_details" },
            measure: { isMeasureCheck: false },
          },
        ],
      },
      ensureShape({}),
    );

    expect(result.domain).toEqual([]);
    expect(result.business_metrics).toEqual([]);
  });

  it("includes business_metrics only when formula, description, filter, or example is set", () => {
    const baseChild = {
      fields: "created_date",
      columnName: "created_date",
      table: { name: "dimdate" },
      measure: { isMeasureCheck: false },
    };

    expect(
      convertCubeFieldsDataToAgentData(
        { domainName: "", cubeDescription: "", children: [baseChild] },
        ensureShape({}),
      ).business_metrics,
    ).toEqual([]);

    expect(
      convertCubeFieldsDataToAgentData(
        {
          domainName: "",
          cubeDescription: "",
          children: [{ ...baseChild, formula: "count(*)" }],
        },
        ensureShape({}),
      ).business_metrics,
    ).toEqual([
      { metric: "created_date", formula: "count(*)", tables: ["dimdate"] },
    ]);

    expect(
      convertCubeFieldsDataToAgentData(
        {
          domainName: "",
          cubeDescription: "",
          children: [{ ...baseChild, description: "Created date" }],
        },
        ensureShape({}),
      ).business_metrics,
    ).toEqual([
      {
        metric: "created_date",
        description: "Created date",
        tables: ["dimdate"],
      },
    ]);

    expect(
      convertCubeFieldsDataToAgentData(
        {
          domainName: "",
          cubeDescription: "",
          children: [{ ...baseChild, example: "2024-01-01" }],
        },
        ensureShape({}),
      ).business_metrics,
    ).toEqual([{ metric: "created_date", tables: ["dimdate"] }]);
  });

  it("builds domain with domain_name and topics from cube fields", () => {
    const result = convertCubeFieldsDataToAgentData(
      {
        domainName: "Sales Operation",
        cubeDescription: "desc",
        children: [
          {
            fields: "meeting_id",
            columnName: "meeting_id",
            topic: "Meetings, Travel",
            table: { name: "meeting_details" },
            measure: { isMeasureCheck: false },
          },
        ],
      },
      ensureShape({}),
    );

    expect(result.domain).toEqual([
      {
        domain_name: "Sales Operation",
        topics: ["Meetings"],
      },
    ]);
    expect(result.metadata_info.metadata.domain).toEqual(["Sales Operation"]);
  });
});

describe("agent-cube-bridge synonyms and examples load", () => {
  const agentData = {
    business_metrics: [],
    synonyms: [
      {
        database_table: "employee_details",
        synonyms: ["staff", "workers"],
      },
    ],
    examples: [
      {
        database_table: "employee_details",
        eg: ["John Doe", "Jane Smith"],
      },
    ],
    metadata_info: { metadata: { domain: [], description: "" } },
    cube_metadata: [
      {
        database_table: "employee_details",
        dimension_name: ["hr"],
        columns: [
          { column_name: "address", semantic_type: "entity" },
          { column_name: "employee_name", semantic_type: "person" },
        ],
      },
    ],
  };

  it("should hydrates table-level synonyms & examples onto cube field rows on load", () => {
    const cubeFields = convertAgentDataToCubeFieldsData(agentData);

    expect(cubeFields.children).toHaveLength(2);
    expect(cubeFields.children[0]).toMatchObject({
      fields: "address",
      synonyms: "staff",
      example: "John Doe",
    });
    expect(cubeFields.children[1]).toMatchObject({
      fields: "employee_name",
      synonyms: "workers",
      example: "Jane Smith",
    });
  });

  it("round-trips synonyms and examples through load and export", () => {
    const cubeFields = convertAgentDataToCubeFieldsData(agentData);
    const exported = convertCubeFieldsDataToAgentData(cubeFields, agentData);

    expect(exported.synonyms).toEqual([
      {
        database_table: "employee_details",
        column_name: "address",
        synonyms: ["staff"],
      },
      {
        database_table: "employee_details",
        column_name: "employee_name",
        synonyms: ["workers"],
      },
    ]);
    expect(exported.examples).toEqual([
      {
        database_table: "employee_details",
        column_name: "address",
        eg: ["John Doe"],
      },
      {
        database_table: "employee_details",
        column_name: "employee_name",
        eg: ["Jane Smith"],
      },
    ]);
  });

  it("loads legacy table-scoped synonyms and examples onto matching columns", () => {
    const agentData = ensureShape({
      business_metrics: [
        {
          metric: "address",
          description: "4",
          formula: "15",
          filter: "2",
          tables: ["employee_details"],
        },
        {
          metric: "employee_id",
          description: "8",
          formula: "5",
          filter: "6",
          tables: ["employee_details"],
        },
        {
          metric: "meeting_id",
          description: "12",
          formula: "9",
          filter: "10",
          tables: ["meeting_details"],
        },
      ],
      synonyms: [
        {
          database_table: "employee_details",
          synonyms: ["s1", "s2"],
        },
        {
          database_table: "meeting_details",
          synonyms: ["s3", "s4"],
        },
      ],
      examples: [
        {
          database_table: "employee_details",
          eg: ["3", "7"],
        },
        {
          database_table: "meeting_details",
          eg: ["11"],
        },
      ],
      metadata_info: {
        metadata: {
          domain: ["do"],
          description: "des",
        },
      },
      domain: [{ domain_name: "do", topics: ["t1", "t3"] }],
      topic_mappings: [
        { topic_name: "t1", component: ["t1"] },
        { topic_name: "t3", component: ["t3"] },
      ],
      cube_metadata: [
        {
          database_table: "employee_details",
          dimension_name: ["t1"],
          columns: [
            { column_name: "address", semantic_type: "entity" },
            { column_name: "employee_id", semantic_type: "person" },
          ],
        },
        {
          database_table: "meeting_details",
          dimension_name: ["t3"],
          columns: [
            { column_name: "meeting_id", semantic_type: "organization" },
          ],
        },
      ],
    });

    const cubeFields = convertAgentDataToCubeFieldsData(agentData);

    expect(cubeFields.children).toHaveLength(3);
    expect(cubeFields.children[0]).toMatchObject({
      fields: "address",
      synonyms: "s1",
      example: "3",
      topic: "t1",
    });
    expect(cubeFields.children[1]).toMatchObject({
      fields: "employee_id",
      synonyms: "s2",
      example: "7",
      topic: "t1",
    });
    expect(cubeFields.children[2]).toMatchObject({
      fields: "meeting_id",
      synonyms: "s3, s4",
      example: "11",
      topic: "t3",
    });
  });

  it("uses only the first topic when legacy comma-separated topic values are provided", () => {
    const result = convertCubeFieldsDataToAgentData(
      {
        domainName: "Sales",
        cubeDescription: "",
        children: [
          {
            fields: "address",
            columnName: "address",
            topic: "Sales, Travel",
            table: { name: "employee_details" },
            measure: { isMeasureCheck: false },
          },
        ],
      },
      ensureShape({}),
    );

    expect(result.cube_metadata[0].dimension_name).toEqual(["Sales"]);
    expect(result.domain).toEqual([
      { domain_name: "Sales", topics: ["Sales"] },
    ]);
  });

  it("loads column topic from table dimension_name when column topic is omitted", () => {
    const cubeFields = convertAgentDataToCubeFieldsData({
      business_metrics: [],
      synonyms: [],
      examples: [],
      metadata_info: { metadata: { domain: [], description: "" } },
      domain: [],
      topic_mappings: [],
      cube_metadata: [
        {
          database_table: "employee_details",
          dimension_name: ["y"],
          columns: [{ column_name: "address", semantic_type: "entity" }],
        },
      ],
    });

    expect(cubeFields.children[0].topic).toBe("y");
  });

  it("preserves per-column topic in serialized cube_metadata", () => {
    const serialized = serializeAgentData({
      cube_metadata: [
        {
          database_table: "employee_details",
          dimension_name: ["y"],
          columns: [
            {
              column_name: "address",
              semantic_type: "entity",
              topic: "y",
            },
          ],
        },
      ],
    });

    const parsed = JSON.parse(serialized);
    expect(parsed.cube_metadata[0].columns[0]).toEqual({
      column_name: "address",
      semantic_type: "entity",
      topic: "y",
    });
  });

  it("applies topic only to the column it was set on", () => {
    const cubeFields = convertAgentDataToCubeFieldsData(
      ensureShape({
        business_metrics: [
          {
            metric: "booking_platform",
            description: "4",
            formula: "15",
            filter: "2",
            tables: ["View 1"],
          },
          {
            metric: "destination_id",
            description: "7",
            formula: "6",
            filter: "8",
            tables: ["View 1"],
          },
        ],
        metadata_info: { metadata: { domain: ["d"], description: "des" } },
        domain: [{ domain_name: "d", topics: ["t1"] }],
        cube_metadata: [
          {
            database_table: "View 1",
            dimension_name: ["t1"],
            columns: [
              {
                column_name: "booking_platform",
                semantic_type: "entity",
                topic: "t1",
              },
              { column_name: "destination_id", semantic_type: "person" },
            ],
          },
        ],
      }),
    );

    expect(cubeFields.children[0]).toMatchObject({
      fields: "booking_platform",
      topic: "t1",
    });
    expect(cubeFields.children[1]).toMatchObject({
      fields: "destination_id",
      topic: "",
    });
  });

  it("uses the last column topic as dimension_name", () => {
    const result = convertCubeFieldsDataToAgentData(
      {
        domainName: "d",
        cubeDescription: "",
        children: [
          {
            fields: "booking_platform",
            columnName: "booking_platform",
            topic: "t1",
            table: { name: "View 1" },
            measure: { isMeasureCheck: false },
          },
          {
            fields: "destination_id",
            columnName: "destination_id",
            topic: "t134",
            table: { name: "View 1" },
            measure: { isMeasureCheck: false },
          },
        ],
      },
      ensureShape({}),
    );

    expect(result.cube_metadata[0].dimension_name).toEqual(["t134"]);
  });

  it("normalizes dimension_name to a single value on serialize", () => {
    const parsed = JSON.parse(
      serializeAgentData({
        cube_metadata: [
          {
            database_table: "View 1",
            dimension_name: ["t1", "t134"],
            columns: [
              {
                column_name: "booking_platform",
                topic: "t1",
              },
              {
                column_name: "destination_id",
                topic: "t134",
              },
            ],
          },
        ],
      }),
    );

    expect(parsed.cube_metadata[0].dimension_name).toEqual(["t134"]);
  });

  it("round-trips per-column topics through load and export", () => {
    const agentData = ensureShape({
      business_metrics: [
        {
          metric: "booking_platform",
          tables: ["View 1"],
        },
        {
          metric: "destination_id",
          tables: ["View 1"],
        },
      ],
      metadata_info: { metadata: { domain: ["d"], description: "" } },
      domain: [{ domain_name: "d", topics: ["t1", "t9"] }],
      cube_metadata: [
        {
          database_table: "View 1",
          dimension_name: ["t9"],
          columns: [
            {
              column_name: "booking_platform",
              semantic_type: "entity",
              topic: "t1",
            },
            {
              column_name: "destination_id",
              semantic_type: "person",
              topic: "t9",
            },
          ],
        },
      ],
    });

    const cubeFields = convertAgentDataToCubeFieldsData(agentData);
    expect(cubeFields.children[0].topic).toBe("t1");
    expect(cubeFields.children[1].topic).toBe("t9");

    const exported = convertCubeFieldsDataToAgentData(cubeFields, agentData);
    expect(exported.cube_metadata[0].columns).toEqual([
      {
        column_name: "booking_platform",
        semantic_type: "entity",
        topic: "t1",
      },
      {
        column_name: "destination_id",
        semantic_type: "person",
        topic: "t9",
      },
    ]);
  });

  it("preserves column_name in serialized synonyms and examples", () => {
    const serialized = serializeAgentData({
      synonyms: [
        {
          database_table: "meeting_details",
          column_name: "meeting_id",
          synonyms: ["meetings"],
        },
      ],
      examples: [
        {
          database_table: "meeting_details",
          column_name: "meeting_id",
          eg: ["Canceled -> meet_cancellation_status = Yes"],
        },
      ],
    });

    const parsed = JSON.parse(serialized);
    expect(parsed.synonyms).toEqual([
      {
        database_table: "meeting_details",
        column_name: "meeting_id",
        synonyms: ["meetings"],
      },
    ]);
    expect(parsed.examples).toEqual([
      {
        database_table: "meeting_details",
        column_name: "meeting_id",
        eg: ["Canceled -> meet_cancellation_status = Yes"],
      },
    ]);
  });
});

describe("agent-cube-bridge raw json display", () => {
  const fullAgentData = ensureShape({
    business_metrics: [
      {
        metric: "booking_platform",
        description: "4",
        tables: ["View 1"],
      },
      {
        metric: "destination_id",
        description: "7",
        tables: ["View 1"],
      },
    ],
    synonyms: [
      {
        database_table: "View 1",
        column_name: "booking_platform",
        synonyms: ["s1"],
      },
      {
        database_table: "View 1",
        column_name: "destination_id",
        synonyms: ["s3"],
      },
    ],
    examples: [
      {
        database_table: "View 1",
        column_name: "booking_platform",
        eg: ["3"],
      },
      {
        database_table: "View 1",
        column_name: "destination_id",
        eg: ["9"],
      },
    ],
    cube_metadata: [
      {
        database_table: "View 1",
        dimension_name: ["t1", "t134"],
        columns: [
          {
            column_name: "booking_platform",
            semantic_type: "entity",
            topic: "t1",
          },
          {
            column_name: "destination_id",
            semantic_type: "person",
            topic: "t134",
          },
        ],
      },
    ],
  });

  it("hides topic and column_name fields from raw json display", () => {
    const display = stripAgentDataForDisplay(fullAgentData);

    expect(display.cube_metadata[0].columns).toEqual([
      {
        column_name: "booking_platform",
        semantic_type: "entity",
      },
      {
        column_name: "destination_id",
        semantic_type: "person",
      },
    ]);
    expect(display.synonyms).toEqual([
      { database_table: "View 1", synonyms: ["s1"] },
      { database_table: "View 1", synonyms: ["s3"] },
    ]);
    expect(display.examples).toEqual([
      { database_table: "View 1", eg: ["3"] },
      { database_table: "View 1", eg: ["9"] },
    ]);
  });

  it("keeps hidden fields in save serialization", () => {
    const parsed = JSON.parse(serializeAgentData(fullAgentData));

    expect(parsed.cube_metadata[0].columns[0].topic).toBe("t1");
    expect(parsed.synonyms[0].column_name).toBe("booking_platform");
    expect(parsed.examples[0].column_name).toBe("booking_platform");
  });

  it("restores hidden fields when raw json display is edited", () => {
    const display = stripAgentDataForDisplay(fullAgentData);
    display.metadata_info = {
      metadata: { domain: ["updated"], description: "changed" },
    };

    const merged = mergeDisplayAgentDataWithStored(fullAgentData, display);

    expect(merged.metadata_info.metadata.description).toBe("changed");
    expect(merged.cube_metadata[0].columns[0].topic).toBe("t1");
    expect(merged.synonyms[0].column_name).toBe("booking_platform");
    expect(merged.examples[1].column_name).toBe("destination_id");
  });
});
