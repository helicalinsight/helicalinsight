import {
  uid,
  ensureShape,
  allTables,
  allTopics,
  highlightJSON,
  SECTIONS,
  SEMANTIC_TYPES,
  COLUMN_TYPES,
  buildAgentClipboardPayload,
  buildMetadataClipboardBlock,
  extractMetadataReference,
  parsePastedAgentPayload,
  parseAgentPayloadForSave,
  resolveMetadataRefForPaste,
} from "../../../components/hi-agent/components/semantic-metadata-editor/semantic-metadata-utils";

describe("semantic-metadata-utils", () => {
  describe("uid", () => {
    it ("it should returns an 8-character alphanumeric string", () => {
      const id = uid();
      expect(id).toMatch(/^[a-z0-9]{8}$/);
    });
  });

  describe("ensureShape", () => {
    it ("it should normalizes null & non-object input to empty arrays & metadata", () => {
      expect(ensureShape(null)).toEqual({
        business_metrics: [],
        synonyms: [],
        examples: [],
        metadata_info: { metadata: { domain: [], description: "" } },
        domain: [],
        topic_mappings: [],
        cube_metadata: [],
      });
    });

    it("builds topic_mappings when cube dimension_name matches domain topics", () => {
      const input = {
        domain: [{ domain_name: "domain", topics: ["t1", "t2", "t3"] }],
        topic_mappings: [],
        cube_metadata: [
          {
            database_table: "employee_details",
            dimension_name: ["t2", "t3"],
            columns: [{ column_name: "address", semantic_type: "entity" }],
          },
        ],
      };

      expect(ensureShape(input).topic_mappings).toEqual([
        {
          topic_name: "t2",
          component: ["t2"],
        },
      ]);
    });

    it("replaces stale topic_mappings when a topic name is updated", () => {
      const input = {
        domain: [{ domain_name: "domain", topics: ["t1", "t4", "t3"] }],
        topic_mappings: [
          {
            topic_name: "t2",
            component: ["t2", "t3"],
          },
        ],
        cube_metadata: [
          {
            database_table: "employee_details",
            dimension_name: ["t4", "t3"],
            columns: [{ column_name: "address", semantic_type: "entity" }],
          },
        ],
      };

      expect(ensureShape(input).topic_mappings).toEqual([
        {
          topic_name: "t4",
          component: ["t4"],
        },
      ]);
    });

    it("drops orphaned topic_mappings that no longer match domain or dimension_name", () => {
      const input = {
        domain: [{ domain_name: "sd", topics: ["sdsdwewewe", "99999"] }],
        topic_mappings: [
          {
            topic_name: "sdsdwewewe",
            component: ["sdsdwewewe", "99999"],
          },
          {
            topic_name: "sdwsdsdw",
            component: ["sdwsdsdw"],
          },
          {
            topic_name: "asds",
            component: ["asds"],
          },
        ],
        cube_metadata: [
          {
            database_table: "dimdate",
            dimension_name: ["sdsdwewewe", "99999"],
            columns: [{ column_name: "created_date", semantic_type: "person" }],
          },
        ],
      };

      expect(ensureShape(input).topic_mappings).toEqual([
        {
          topic_name: "sdsdwewewe",
          component: ["sdsdwewewe"],
        },
      ]);
    });

    it ("it should preserves existing arrays & fills missing metadata fields", () => {
      const input = {
        business_metrics: [{ metric: "revenue" }],
        metadata_info: { metadata: { domain: ["Sales"], description: "desc" } },
        cube_metadata: [{ database_table: "orders" }],
      };
      const result = ensureShape(input);
      expect(result.business_metrics).toHaveLength(1);
      expect(result.metadata_info.metadata.domain).toEqual(["Sales"]);
      expect(result.cube_metadata[0].database_table).toBe("orders");
      expect(result.synonyms).toEqual([]);
    });
  });

  describe("allTables", () => {
    it ("it should collects unique table names from cube, synonyms, examples & metrics", () => {
      const data = ensureShape({
        cube_metadata: [{ database_table: "orders" }],
        synonyms: [{ database_table: "customers" }],
        examples: [{ database_table: "orders" }],
        business_metrics: [{ tables: ["products", "orders"] }],
      });
      expect(allTables(data)).toEqual(["customers", "orders", "products"]);
    });
  });

  describe("allTopics", () => {
    it ("it should collects topics from domains & topic mappings", () => {
      const data = ensureShape({
        domain: [{ topics: ["Travel", "Sales"] }],
        topic_mappings: [{ topic_name: "HR" }],
      });
      expect(allTopics(data)).toEqual(["HR", "Sales", "Travel"]);
    });
  });

  describe("constants", () => {
    it ("it should SECTIONS includes all editor section keys", () => {
      const keys = SECTIONS.map((s) => s.key);
      expect(keys).toEqual([
        "overview",
        "metrics",
        "tables",
        "synonyms",
        "examples",
        "topics",
        "json",
      ]);
    });

    it ("it should SEMANTIC_TYPES & COLUMN_TYPES are non-empty option lists", () => {
      expect(SEMANTIC_TYPES.length).toBeGreaterThan(1);
      expect(COLUMN_TYPES).toEqual(["", "temporal", "categorical", "numeric"]);
    });
  });

  describe("metadata clipboard payload", () => {
    const agentState = ensureShape({
      cube_metadata: [{ database_table: "orders", columns: [] }],
    });

    it("wraps agent state with connected metadata for copy/paste", () => {
      const payload = JSON.parse(
        buildAgentClipboardPayload(agentState, {
          path: "team/folder",
          fileName: "Sales.metadata",
        }),
      );

      expect(payload.metadata).toEqual({
        location: "team/folder",
        metadataFileName: "Sales.metadata",
      });
      expect(payload.state.cube_metadata[0].database_table).toBe("orders");
    });

    it("returns plain state when metadata is not connected", () => {
      const payload = JSON.parse(buildAgentClipboardPayload(agentState, {}));
      expect(payload.metadata).toBeUndefined();
      expect(payload.cube_metadata[0].database_table).toBe("orders");
    });

    it("parses wrapped paste payload and resolves metadata reference", () => {
      const wrapped = {
        metadata: {
          location: "team/folder",
          metadataFileName: "Sales.metadata",
        },
        state: agentState,
      };
      const { agentState: parsedState, metadataRef } = parsePastedAgentPayload(
        JSON.stringify(wrapped),
      );

      expect(parsedState.cube_metadata[0].database_table).toBe("orders");
      expect(metadataRef).toEqual({
        path: "team/folder",
        fileName: "Sales.metadata",
      });
    });

    it("falls back to connected metadata when pasted state has no metadata block", () => {
      const resolved = resolveMetadataRefForPaste(null, {
        path: "team/folder",
        fileName: "Sales.metadata",
      });

      expect(resolved).toEqual({
        path: "team/folder",
        fileName: "Sales.metadata",
      });
    });

    it("unwraps wrapped raw JSON for save", () => {
      const wrapped = {
        metadata: {
          location: "team/folder",
          metadataFileName: "Sales.metadata",
        },
        state: agentState,
      };
      const saved = parseAgentPayloadForSave(JSON.stringify(wrapped));

      expect(saved.cube_metadata[0].database_table).toBe("orders");
      expect(saved.metadata).toBeUndefined();
    });

    it("buildMetadataClipboardBlock matches save API metadata shape", () => {
      expect(
        buildMetadataClipboardBlock({
          path: "team/folder",
          fileName: "Sales.metadata",
        }),
      ).toEqual({
        location: "team/folder",
        metadataFileName: "Sales.metadata",
      });
      expect(extractMetadataReference({
        metadata: {
          location: "team/folder",
          metadataFileName: "Sales.metadata",
        },
      })).toEqual({
        path: "team/folder",
        fileName: "Sales.metadata",
      });
    });
  });
});
