jest.mock("../../components/hi-instant-bi/utils/assemble-instant-dashboard", () => ({
  __esModule: true,
  assembleInstantDashboardCreateConfig: jest.fn(() => ({
    gridItemsData: [{ id: "tile-1" }],
  })),
  getGridItemsFiltersSettings: jest.fn(() => [{ reportId: "report-1" }]),
}));

jest.mock("../../components/hi-instant-bi/utils/common-utils", () => ({
  __esModule: true,
  getInstantBIMetadataForReport: jest.fn(() => ({ model: {} })),
}));

jest.mock("../../components/hi-instant-bi/utils/convert-sql-to-hr-save", () => ({
  __esModule: true,
  persistConvertDashboardSeed: jest.fn(),
}));

jest.mock("../../components/hi-instant-bi/utils/save-instant-dashboard", () => ({
  __esModule: true,
  INSTANT_EDIT_MODE: "instant-edit",
}));

const { exploreInstantDashboard } = require("../../components/hi-instant-bi/utils/explore-instant-dashboard");
const {
  assembleInstantDashboardCreateConfig,
  getGridItemsFiltersSettings,
} = require("../../components/hi-instant-bi/utils/assemble-instant-dashboard");
const { persistConvertDashboardSeed } = require("../../components/hi-instant-bi/utils/convert-sql-to-hr-save");

const dashboardModel = {
  title: "Dashboard",
  theme: { primary: "#000" },
  items: [{ id: "tile-1" }],
};

describe("exploreInstantDashboard", () => {
  const originalBaseURL = window.baseURL;

  beforeEach(() => {
    assembleInstantDashboardCreateConfig.mockClear();
    getGridItemsFiltersSettings.mockClear();
    persistConvertDashboardSeed.mockClear();
    window.baseURL = "/base/";
    window.open = jest.fn();
  });

  afterAll(() => {
    window.baseURL = originalBaseURL;
  });

  test("does nothing when the dashboard has no items", () => {
    const result = exploreInstantDashboard({ dashboardModel: { items: [] } });

    expect(result).toBe(false);
    expect(assembleInstantDashboardCreateConfig).not.toHaveBeenCalled();
    expect(persistConvertDashboardSeed).not.toHaveBeenCalled();
    expect(window.open).not.toHaveBeenCalled();
  });

  test("persists the dashboard seed and opens the designer", () => {
    const steps = [{ index: 1 }];
    const questionsHistory = [{ index: 1, question: "Question" }];
    const reports = [{ id: "report-1" }];
    const activeReport = { id: "report-1" };

    const result = exploreInstantDashboard({
      dashboardModel,
      steps,
      questionsHistory,
      reports,
      activeReport,
    });

    expect(result).toBe(true);
    expect(assembleInstantDashboardCreateConfig).toHaveBeenCalledWith({
      dashboardModel,
      steps,
      theme: dashboardModel.theme,
      questionsHistory,
      isStatic: false,
      reports,
      activeReport,
    });
    expect(getGridItemsFiltersSettings).toHaveBeenCalledWith(
      [{ id: "tile-1" }],
      reports,
    );
    expect(persistConvertDashboardSeed).toHaveBeenCalledWith(
      {
        gridItemsData: [{ id: "tile-1" }],
        designerMode: "instant-edit",
      },
      { subject: { model: {} } },
      reports,
      [{ reportId: "report-1" }],
    );
    expect(window.open).toHaveBeenCalledWith("/base/#/dashboard-designer");
  });
});
