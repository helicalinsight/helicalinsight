import {
  uid,
  ensureShape,
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
    it("returns an 8-character alphanumeric string", () => {
      const id = uid();
      expect(id).toMatch(/^[a-z0-9]{8}$/);
    });
  });

  describe("ensureShape", () => {
    it("normalizes null and non-object input to domain and cube_info only", () => {
      expect(ensureShape(null)).toEqual({
        domain: [],
        cube_info: [{ cubeName: "", dimensions: [], measures: [] }],
      });
    });
  });

  describe("allTopics", () => {
    it("collects topics from domains", () => {
      const data = ensureShape({
        domain: [{ topics: ["Travel", "Sales"] }],
      });
      expect(allTopics(data)).toEqual(["Sales", "Travel"]);
    });
  });

  describe("constants", () => {
    it("SECTIONS includes overview and raw JSON keys", () => {
      const keys = SECTIONS.map((s) => s.key);
      expect(keys).toEqual(["overview", "json"]);
    });

    it("SEMANTIC_TYPES and COLUMN_TYPES are non-empty option lists", () => {
      expect(SEMANTIC_TYPES.length).toBeGreaterThan(1);
      expect(COLUMN_TYPES).toEqual(["", "temporal", "categorical", "numeric"]);
    });
  });

  describe("metadata clipboard payload", () => {
    const agentState = ensureShape({
      domain: [{ domain_name: "Sales", description: "", topics: [] }],
      cube_info: [
        {
          cubeName: "",
          dimensions: [],
          measures: [{ measureName: "revenue" }],
        },
      ],
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
      expect(payload.state.cube_info[0].measures[0].measureName).toBe("revenue");
      expect(payload.state.business_metrics).toBeUndefined();
    });

    it("returns plain state when metadata is not connected", () => {
      const payload = JSON.parse(buildAgentClipboardPayload(agentState, {}));
      expect(payload.metadata).toBeUndefined();
      expect(payload.cube_info[0].measures[0].measureName).toBe("revenue");
      expect(payload.business_metrics).toBeUndefined();
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

      expect(parsedState.cube_info[0].measures[0].measureName).toBe("revenue");
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

      expect(saved.cube_info[0].measures[0].measureName).toBe("revenue");
      expect(saved.metadata).toBeUndefined();
      expect(saved.business_metrics).toBeUndefined();
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
      expect(
        extractMetadataReference({
          metadata: {
            location: "team/folder",
            metadataFileName: "Sales.metadata",
          },
        }),
      ).toEqual({
        path: "team/folder",
        fileName: "Sales.metadata",
      });
    });
  });

  describe("highlightJSON", () => {
    it("escapes HTML in JSON text", () => {
      expect(highlightJSON('{"a":"<b>"}')).toContain("&lt;b&gt;");
    });
  });
});
