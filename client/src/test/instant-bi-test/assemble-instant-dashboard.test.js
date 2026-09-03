import "regenerator-runtime/runtime";
import { assembleInstantDashboardConfig, sanitizeDashboardSvg } from "../../components/hi-instant-bi/utils/assemble-instant-dashboard";

describe("assembleInstantDashboardConfig", () => {
  test("builds skeletal text tiles from layout, not reports", () => {
    const config = assembleInstantDashboardConfig({
      payload: {
        items: [
          {
            id: "seq-3",
            viz: { chart_name: "bar", viz_model: { properties: { title: "Cost by platform" } } },
          },
        ],
        theme: { color: "#1677ff", background: "#ffffff" },
        summary: { text: "Travel spend", x: 0, y: 0, w: 12, h: 1 },
        layout: [{ itemId: "seq-3", x: 0, y: 3, w: 6, h: 4 }],
      },
    });

    expect(config.designerMode).toBe("instant-edit");
    expect(config.gridItemsData.every((item) => item.compType === "text")).toBe(true);
    expect(config.gridItemsData.some((item) => item.compType === "dashboard-designer-component")).toBe(false);
    const titles = config.gridItemsData.map(
      (item) => item.gridItemConfig.find((entry) => entry.key === "header")?.values?.title
    );
    expect(titles).toContain("Summary");
    expect(titles).toContain("Cost by platform");
    const chart = config.gridItemsData.find(
      (item) => item.gridItemConfig.find((entry) => entry.key === "header")?.values?.title === "Cost by platform"
    );
    expect(config.layout.find((entry) => entry.i === chart.id)).toMatchObject({
      x: 0,
      y: 3,
      w: 6,
      h: 4,
    });
  });

  test("assembles skeletal tiles from dashboard_model width and height", () => {
    const config = assembleInstantDashboardConfig({
      payload: {
        items: [
          {
            chat_sequence_id: "seq-3",
            component_id: "ab12CD34",
            report_model: {
              viz_model: { properties: { title: "Bookings" } },
            },
            dashboard_model: {
              kind: "viz",
              title: "Bookings",
              layout: { x: 0, y: 2, w: 6, h: 4, width: 6, height: 4 },
            },
          },
          {
            component_id: "summ0001",
            dashboard_model: {
              kind: "summary",
              title: "Summary",
              layout: { x: 0, y: 0, width: 12, height: 1 },
              html: "Spend is rising",
            },
          },
          {
            component_id: "kpi0001",
            dashboard_model: {
              kind: "kpi",
              title: "Travel Cost",
              layout: { x: 0, y: 1, w: 3, h: 2 },
            },
          },
          {
            component_id: "flt0001",
            dashboard_model: {
              kind: "filter",
              title: "Travel Type",
              column: "travel_type",
              layout: { x: 3, y: 1, w: 3, h: 1 },
            },
          },
          {
            component_id: "svg0001",
            dashboard_model: {
              kind: "svg",
              layout: { x: 0, y: 5, w: 12, h: 1 },
              html: '<svg viewBox="0 0 120 8"><rect width="120" height="2" y="3"/></svg>',
            },
          },
        ],
      },
    });
    const titles = config.gridItemsData.map(
      (item) => item.gridItemConfig.find((entry) => entry.key === "header")?.values?.title
    );
    expect(titles).toEqual(expect.arrayContaining(["Bookings", "Summary"]));
    expect(titles).not.toEqual(expect.arrayContaining(["Travel Cost", "Travel Type", "Image", "divider"]));
    const kpi = config.gridItemsData.find((item) =>
      item.gridItemConfig.find((entry) => entry.key === "edit")?.values?.text?.includes("Travel Cost")
    );
    expect(kpi.gridItemConfig.find((entry) => entry.key === "header")?.values?.enable).toBe(false);
    expect(kpi.gridItemConfig.find((entry) => entry.key === "header")?.values?.title).toBe("");
    const filter = config.gridItemsData.find((item) =>
      item.gridItemConfig.find((entry) => entry.key === "edit")?.values?.text?.includes("Filter")
    );
    expect(filter.gridItemConfig.find((entry) => entry.key === "header")?.values?.enable).toBe(false);
    const divider = config.gridItemsData.find((item) =>
      item.gridItemConfig.find((entry) => entry.key === "edit")?.values?.text?.includes("<svg")
    );
    expect(divider.gridItemConfig.find((entry) => entry.key === "header")?.values?.enable).toBe(false);
    expect(divider.gridItemConfig.find((entry) => entry.key === "header")?.values?.title).toBe("");
    const summary = config.gridItemsData.find(
      (item) => item.gridItemConfig.find((entry) => entry.key === "header")?.values?.title === "Summary"
    );
    expect(summary.gridItemConfig.find((entry) => entry.key === "edit")?.values?.text).toContain("Spend is rising");
    const bookings = config.gridItemsData.find(
      (item) => item.gridItemConfig.find((entry) => entry.key === "header")?.values?.title === "Bookings"
    );
    expect(config.layout.find((entry) => entry.i === bookings.id)).toMatchObject({
      x: 0,
      y: 2,
      w: 6,
      h: 4,
    });
  });

  test("drops unsafe LLM svg scripts", () => {
    expect(sanitizeDashboardSvg('<svg onload="alert(1)"></svg>')).toBe("");
    expect(sanitizeDashboardSvg('<svg viewBox="0 0 8 8"><rect width="8" height="8"/></svg>')).toContain("<svg");
  });
});
