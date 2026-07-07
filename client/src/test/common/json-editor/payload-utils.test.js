import {
  buildClipboardPayload,
  buildMetadataClipboardBlock,
  extractMetadataReference,
  parseClipboardPayload,
  parsePayloadForSave,
  resolveMetadataRefForPaste,
  unwrapStatePayload,
} from "../../../components/common/json-editor";

const normalizeState = (data) => ({
  ...data,
  items: Array.isArray(data?.items) ? data.items : [],
});

describe("json-editor payload-utils", () => {
  it("should extracts metadata reference from wrapped payload", () => {
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

  it("shoudl unwraps state from wrapped payload", () => {
    expect(
      unwrapStatePayload({
        metadata: { location: "a", metadataFileName: "b.metadata" },
        state: { items: ["one"] },
      }),
    ).toEqual({ items: ["one"] });
  });

  it("should builds clipboard payload with metadata block", () => {
    const payload = JSON.parse(
      buildClipboardPayload(
        { items: ["one"] },
        { path: "team/folder", fileName: "Sales.metadata" },
        { normalizeState },
      ),
    );

    expect(payload.metadata).toEqual({
      location: "team/folder",
      metadataFileName: "Sales.metadata",
    });
    expect(payload.state.items).toEqual(["one"]);
  });

  it("should parses clipboard payload with normalizeState", () => {
    const { state, metadataRef } = parseClipboardPayload(
      JSON.stringify({
        metadata: {
          location: "team/folder",
          metadataFileName: "Sales.metadata",
        },
        state: { items: ["one"] },
      }),
      { normalizeState },
    );

    expect(state.items).toEqual(["one"]);
    expect(metadataRef).toEqual({
      path: "team/folder",
      fileName: "Sales.metadata",
    });
  });

  it("should resolves metadata reference from connected metadata details", () => {
    expect(
      resolveMetadataRefForPaste(null, {
        path: "team/folder",
        fileName: "Sales.metadata",
      }),
    ).toEqual({
      path: "team/folder",
      fileName: "Sales.metadata",
    });
  });

  it("unwraps wrapped payload for save", () => {
    const saved = parsePayloadForSave(
      JSON.stringify({
        metadata: {
          location: "team/folder",
          metadataFileName: "Sales.metadata",
        },
        state: { items: ["one"] },
      }),
      { normalizeState },
    );

    expect(saved.items).toEqual(["one"]);
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
  });
});
