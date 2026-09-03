import "regenerator-runtime/runtime";
import {
  applySavedChartFileRefs,
  buildInstantChartSaveDrafts,
  INSTANT_EDIT_MODE,
  isInlineDashboardChart,
  isInstantEditMode,
  namesByItemIdFromDrafts,
  shouldConfirmInstantChartSaves,
  sanitizeInstantReportName,
  uniqueInstantReportName,
} from "../../components/hi-instant-bi/utils/save-instant-dashboard";

describe("instant-edit dashboard save helpers", () => {
  test("names the mode instant-edit", () => {
    expect(INSTANT_EDIT_MODE).toBe("instant-edit");
    expect(isInstantEditMode("instant-edit")).toBe(true);
    expect(isInstantEditMode("edit")).toBe(false);
  });

  test("confirms chart names only on first instant-edit save", () => {
    const drafts = [{ id: "item-1", name: "Cost_by_Type" }];
    expect(
      shouldConfirmInstantChartSaves({
        designerMode: "instant-edit",
        dashboardUUID: "",
        drafts,
      })
    ).toBe(true);
    expect(
      shouldConfirmInstantChartSaves({
        designerMode: "instant-edit",
        dashboardUUID: "uuid-1",
        drafts,
      })
    ).toBe(false);
    expect(
      shouldConfirmInstantChartSaves({
        designerMode: "edit",
        dashboardUUID: "",
        drafts,
      })
    ).toBe(false);
    expect(
      shouldConfirmInstantChartSaves({
        designerMode: "instant-edit",
        dashboardUUID: "",
        drafts: [],
      })
    ).toBe(false);
  });

  test("builds unique report names for the save folder", () => {
    const used = new Set();
    expect(uniqueInstantReportName("Sales by Region", used)).toBe("Sales_by_Region");
    expect(uniqueInstantReportName("Sales by Region", used)).toBe("Sales_by_Region_2");
    expect(sanitizeInstantReportName("Rev: 2025/Q1.hr")).toBe("Rev_2025_Q1");
  });

  test("builds editable save drafts for inline charts", () => {
    const drafts = buildInstantChartSaveDrafts([
      {
        id: "item-1",
        compType: "dashboard-designer-component",
        gridItemConfig: [{ key: "header", values: { title: "Cost by Type" } }],
        reportInfo: { inline: true, file: { inline: true } },
      },
      {
        id: "item-2",
        compType: "dashboard-designer-component",
        gridItemConfig: [{ key: "header", values: { title: "Cost by Type" } }],
        reportInfo: { inline: true, file: { inline: true } },
      },
      {
        id: "text-1",
        compType: "text",
        reportInfo: { inline: true },
      },
    ]);
    expect(drafts).toEqual([
      { id: "item-1", name: "Cost_by_Type" },
      { id: "item-2", name: "Cost_by_Type_2" },
    ]);
    expect(namesByItemIdFromDrafts(drafts)).toEqual({
      "item-1": "Cost_by_Type",
      "item-2": "Cost_by_Type_2",
    });
  });

  test("detects inline chart tiles", () => {
    expect(
      isInlineDashboardChart({
        compType: "dashboard-designer-component",
        reportInfo: { inline: true, file: { inline: true } },
      })
    ).toBe(true);
    expect(
      isInlineDashboardChart({
        compType: "text",
        reportInfo: { inline: true, file: { inline: true } },
      })
    ).toBe(false);
  });

  test("replaces inline chart and cloned filter reportInfo with saved path and name", () => {
    const saved = {
      uuid: "uuid-1",
      file: { path: "Sales/Revenue.hr", name: "Revenue.hr", title: "Revenue" },
    };
    const items = applySavedChartFileRefs(
      [
        {
          id: "item-1",
          compType: "dashboard-designer-component",
          reportInfo: {
            inline: true,
            file: { inline: true, dashboardItemId: "item-1", fields: [] },
          },
        },
        {
          id: "filter-1",
          compType: "filter-component",
          reportInfo: {
            inline: true,
            mode: "filter",
            file: { inline: true, dashboardItemId: "item-1", fields: [] },
          },
        },
      ],
      { "item-1": saved }
    );
    expect(items[0].reportInfo.inline).toBe(false);
    expect(items[0].reportInfo.file).toEqual(saved.file);
    expect(items[0].reportInfo.resourceId).toBe("uuid-1");
    expect(items[1].reportInfo.file.name).toBe("Revenue.hr");
    expect(items[1].reportInfo.mode).toBe("filter");
  });
});
