import "regenerator-runtime/runtime";
import {
  INSTANT_EDIT_MODE,
  getInlineDashboardReportModel,
  isInlineDashboardChart,
  isInstantEditMode,
  syncInstantDashboardChartInteractions,
} from "../../components/hi-instant-bi/utils/save-instant-dashboard";

describe("instant-edit dashboard save helpers", () => {
  test("names the mode instant-edit", () => {
    expect(INSTANT_EDIT_MODE).toBe("instant-edit");
    expect(isInstantEditMode("instant-edit")).toBe(true);
    expect(isInstantEditMode("edit")).toBe(false);
  });

  test("detects inline chart tiles for create and edit dashboards", () => {
    expect(
      isInlineDashboardChart({
        compType: "dashboard-create-component",
        reportInfo: { inline: true, file: { inline: true } },
      })
    ).toBe(true);
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

  test("reads the report model that contains interactions", () => {
    const model = {
      viz_model: { id: "m1" },
      interactions: { selectedType: "Chart" },
    };
    expect(
      getInlineDashboardReportModel({
        reportInfo: {
          file: {
            report_model: { viz_model: { id: "stale" } },
            instant_item: { chat_response: { report_model: model } },
          },
        },
      })
    ).toEqual(model);
  });

  test("saves interactions inside the inline file report model", () => {
    const items = syncInstantDashboardChartInteractions([
      {
        id: "item-1",
        compType: "dashboard-create-component",
        reportInfo: {
          inline: true,
          file: {
            inline: true,
            report_model: {
              viz_model: { id: "m1" },
              interactions: {
                selectedType: "Chart",
                filters: [{ uid: "f1", field: "sales" }],
              },
            },
          },
        },
      },
      {
        id: "text-1",
        compType: "text",
        reportInfo: { inline: true, file: { inline: true } },
      },
    ]);

    expect(items[0].reportInfo.file.report_model).toEqual({
      viz_model: { id: "m1" },
      interactions: {
        selectedType: "Chart",
        filters: [{ uid: "f1", field: "sales" }],
      },
    });
    expect(items[1]).toEqual({
      id: "text-1",
      compType: "text",
      reportInfo: { inline: true, file: { inline: true } },
    });
  });

  test("hoists instant interactions into the inline file report model", () => {
    const [item] = syncInstantDashboardChartInteractions([
      {
        id: "item-1",
        compType: "dashboard-designer-component",
        reportInfo: {
          inline: true,
          hreportId: "hr-1",
          file: {
            inline: true,
            instant_item: {
              chat_response: {
                report_model: {
                  viz_model: { id: "m1" },
                  interactions: { selectedType: "Table" },
                },
              },
            },
          },
        },
      },
    ]);

    expect(item.reportInfo.file.report_model).toEqual({
      viz_model: { id: "m1" },
      interactions: { selectedType: "Table" },
    });
    expect(item.reportInfo.file.instant_item).toBeDefined();
  });
});
