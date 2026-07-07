import {
  SEMANTIC_TOOLTIPS,
  getSemanticTooltipText,
} from "../../../components/hi-agent/components/semantic-metadata-editor/semantic-metadata-tooltips";

describe("semantic-metadata-tooltips", () => {
  it ("it should SEMANTIC_TOOLTIPS exposes expected domain keys", () => {
    expect(SEMANTIC_TOOLTIPS["Business Domain(s)"]).toMatch(/business domain/i);
    expect(SEMANTIC_TOOLTIPS.Formula).toMatch(/calculation/i);
  });

  describe("getSemanticTooltipText", () => {
    it(" it should returns tooltip text for a known label", () => {
      expect(getSemanticTooltipText("Metric name")).toBe(
        SEMANTIC_TOOLTIPS["Metric name"],
      );
    });

    it ("it should returns empty string for unknown or empty labels", () => {
      expect(getSemanticTooltipText("Unknown Label")).toBe("");
      expect(getSemanticTooltipText("")).toBe("");
      expect(getSemanticTooltipText(null)).toBe("");
    });
  });
});
