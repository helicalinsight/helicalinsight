import {
  CUBE_EDITOR_TOOLTIPS,
  AGENT_EDITOR_TOOLTIPS,
  getCubeEditorTooltipText,
} from "../../components/hi-cube/cubeEditorTooltips";

describe("cubeEditorTooltips", () => {
  it("exposes domain and topic description tooltips for agent", () => {
    expect(AGENT_EDITOR_TOOLTIPS["Domain Description"]).toMatch(/domain/i);
    expect(AGENT_EDITOR_TOOLTIPS["Topic Description"]).toMatch(/topic/i);
  });

  it("exposes delete tooltips for domain, topic, and field-from-topic", () => {
    expect(AGENT_EDITOR_TOOLTIPS["Delete Domain"]).toMatch(/domain/i);
    expect(AGENT_EDITOR_TOOLTIPS["Delete Topic"]).toMatch(/topic/i);
    expect(AGENT_EDITOR_TOOLTIPS["Remove field from topic"]).toMatch(
      /Fields list/i,
    );
  });

  describe("getCubeEditorTooltipText", () => {
    it("returns agent-specific tooltip when variant is agent", () => {
      expect(getCubeEditorTooltipText("Domain Description", "agent")).toBe(
        AGENT_EDITOR_TOOLTIPS["Domain Description"],
      );
      expect(getCubeEditorTooltipText("Delete Domain", "agent")).toBe(
        AGENT_EDITOR_TOOLTIPS["Delete Domain"],
      );
    });

    it("falls back to cube tooltip when agent key is missing", () => {
      expect(getCubeEditorTooltipText("Fields", "agent")).toBe(
        AGENT_EDITOR_TOOLTIPS.Fields,
      );
      expect(getCubeEditorTooltipText("Partition", "agent")).toBe(
        CUBE_EDITOR_TOOLTIPS.Partition,
      );
    });

    it("returns empty string for unknown or empty labels", () => {
      expect(getCubeEditorTooltipText("Unknown Label", "agent")).toBe("");
      expect(getCubeEditorTooltipText("", "agent")).toBe("");
      expect(getCubeEditorTooltipText(null)).toBe("");
    });
  });
});
