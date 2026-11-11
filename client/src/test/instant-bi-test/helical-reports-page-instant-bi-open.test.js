import React from "react";
import { render } from "@testing-library/react";
import actionTypes from "../../redux/actions/actionTypes";
import { HelicalReports } from "../../pages/helical-reports-page";

let mockState;
let mockDispatch;

jest.mock("react-redux", () => ({
  useDispatch: () => mockDispatch,
  useSelector: (selector) => selector(mockState),
}));

jest.mock("../../components", () => ({
  HINavbar: ({ children }) => children,
  HITabs: () => null,
}));

jest.mock("../../components/common/hi-shortcuts/HIShortcuts", () => () => null);
jest.mock("../../components/common/download-progress-modal", () => () => null);
jest.mock("../../components/hi-fileBrowser/SaveItems", () => () => null);
jest.mock("../../components/hi-fileBrowser/components", () => ({
  ShareFinalModal: () => null,
}));
jest.mock("../../components/hi-fileBrowser/hi-fileBrowser", () => ({
  HIFileBrowser: () => null,
}));
jest.mock("../../components/hi-notifications/notify", () =>
  jest.fn(() => ({ warning: jest.fn(), error: jest.fn() }))
);
jest.mock(
  "../../components/hi-reports/hi-editing-area/components/filters/filters",
  () => () => null
);
jest.mock(
  "../../components/hi-reports/hi-editing-area/components/viz-items/viz-list-new",
  () => () => null
);
jest.mock("../../components/hi-reports/hi-editing-area/hi-editing-area", () => () =>
  null
);
jest.mock("../../components/hi-reports/hi-fields-area/hi-fields-area", () => () => null);
jest.mock("../../components/hi-reports/hi-viz-area/hi-viz-area", () => () => null);
jest.mock("../../components/hi-sidebar/hr-hreportSidebar/hi-metadata-area", () => () =>
  null
);
jest.mock("../../layouts/hi-layout", () => ({ content, children }) =>
  content || children
);
jest.mock("react-grid-layout", () => {
  const React = require("react");
  const Responsive = ({ children }) => React.createElement("div", null, children);
  return {
    Responsive,
    WidthProvider: () => Responsive,
  };
});
jest.mock("../../components/hi-reports/hr-taskbar-items", () => ({
  __esModule: true,
  default: jest.fn(() => []),
  handleFullScreenClick: jest.fn(),
  toggleToolsAreaShelf: jest.fn(),
}));
jest.mock("../../components/hi-reports/hi-viz-area/utils/utillities", () => ({
  checkIfDrillThrough: jest.fn(() => false),
  clearGridChart: jest.fn(),
  addFilter: jest.fn(),
}));
jest.mock("../../components/hi-reports/hi-editing-area/utils/filter-utils", () => ({
  getRelativeDateValues: jest.fn(),
}));
jest.mock("../../utils/filter-utils", () => ({
  checkRelativeDateFilter: jest.fn(),
}));
jest.mock("../../components/hi-reports/utils/utilities", () => ({
  getMilliseconds: jest.fn(() => 0),
  handleHrShortcuts: jest.fn(),
  handleVizShortcuts: jest.fn(),
  resetShortcuts: jest.fn(),
}));
jest.mock("../../components/hi-reports/utils/base", () => ({
  fetchReport: jest.fn(),
  generateReport: jest.fn(),
  getGeoJsonData: jest.fn(),
  loadChildReport: jest.fn(),
  openCube: jest.fn(),
  openMetadata: jest.fn(),
  replaceStylesAndStylesIdInActiveReport: jest.fn(),
  saveReportFile: jest.fn(),
}));
jest.mock("../../utils/utilities", () => ({
  checkForAnchorRelativeParameters: jest.fn((parameters) => parameters),
  checkWhetherStateIsLoaded: jest.fn(),
  checkWhetherStateIsLoadedById: jest.fn(),
  exportPrintedReport: jest.fn(),
  exportReport: jest.fn(),
  getFieldDisplayName: jest.fn((field) => field?.field || field?.name),
  modifyFilters: jest.fn((state) => state),
}));
jest.mock("../../redux/reducers/hreport.reducer", () => ({
  getHreportInitialGridLayout: jest.fn(() => ({
    metadataShelf: false,
    toolsAreaShelf: false,
    fieldsAreaShelf: false,
    gridItemsLayout: {
      lg: [{ i: "sidebar-area" }, { i: "editing-area" }, { i: "chart-area" }],
      md: [],
      sm: [],
      xs: [],
      xxs: [],
    },
  })),
}));

const { generateReport } = require("../../components/hi-reports/utils/base");
const { modifyFilters } = require("../../utils/utilities");

const REPORTS = [
  {
    id: "test_123",
    reportInfo: { reportName: "Generated report" },
    filters: [],
    properties: {},
    reportData: {},
  },
  {
    id: "saved-instant-report",
    reportInfo: { reportName: "Saved instant report" },
    filters: [],
    properties: {},
    reportData: {},
  },
];

function createState() {
  return {
    hreport: {
      present: {
        reports: REPORTS,
        activeReportId: "test_123",
        isSavingInProgress: false,
        sidebarLoading: false,
        layout: {
          metadataShelf: false,
          toolsAreaShelf: false,
          fieldsAreaShelf: false,
          gridItemsLayout: {
            lg: [{ i: "sidebar-area" }, { i: "editing-area" }, { i: "chart-area" }],
            md: [],
            sm: [],
            xs: [],
            xxs: [],
          },
        },
        geoJsonData: {},
      },
      past: [],
      future: [],
    },
    app: {
      editModeInfo: null,
      keysPressed: [],
      currentSCLocation: "",
      applicationSettingsData: {
        userData: {},
        settings: { exportType: {} },
      },
    },
    fileBrowser: {
      isShareModalVisible: false,
    },
  };
}

function renderPage(props) {
  return render(<HelicalReports {...props} />);
}

beforeEach(() => {
  localStorage.clear();
  mockState = createState();
  mockDispatch = jest.fn((action) => {
    if (typeof action === "function") {
      return action(mockDispatch, () => mockState);
    }
    return action;
  });
  generateReport.mockClear();
  modifyFilters.mockClear();
});

describe("HelicalReports instant-bi-open cleanup", () => {
  it("does not remove the report when an instant-bi-open page unmounts", () => {
    const { unmount } = renderPage({
      mode: "instant-bi-open",
      reportId: "saved-instant-report",
      parameters: {},
    });

    unmount();

    expect(mockDispatch).not.toHaveBeenCalledWith(
      expect.objectContaining({ type: actionTypes.REMOVE_REPORT })
    );
  });

  it("removes the report when a non instant-bi-open page unmounts", () => {
    const { unmount } = renderPage({ mode: "create", parameters: {} });

    unmount();

    expect(mockDispatch).toHaveBeenCalledWith(
      expect.objectContaining({ type: actionTypes.REMOVE_REPORT })
    );
  });
});

describe("HelicalReports instant-bi-open parameter updates", () => {
  it("prefers the report matching props.reportId over the generated report", () => {
    const { rerender } = renderPage({
      mode: "instant-bi-open",
      reportId: "saved-instant-report",
      parameters: {},
    });

    rerender(
      <HelicalReports
        mode="instant-bi-open"
        reportId="saved-instant-report"
        parameters={{ region: "West" }}
      />
    );

    expect(modifyFilters).toHaveBeenCalledWith(
      expect.objectContaining({ id: "saved-instant-report" }),
      { region: "West" }
    );
    expect(generateReport).toHaveBeenCalledWith(
      expect.objectContaining({
        id: "saved-instant-report",
        loadState: true,
        mode: "instant-bi-open",
      }),
      mockDispatch,
      expect.any(Function)
    );
  });

  it("falls back to the generated report when props.reportId is missing", () => {
    const { rerender } = renderPage({
      mode: "instant-bi-open",
      parameters: {},
    });

    rerender(
      <HelicalReports
        mode="instant-bi-open"
        parameters={{ region: "East" }}
      />
    );

    expect(modifyFilters).toHaveBeenCalledWith(
      expect.objectContaining({ id: "test_123" }),
      { region: "East" }
    );
    expect(generateReport).toHaveBeenCalledWith(
      expect.objectContaining({ id: "test_123", mode: "instant-bi-open" }),
      mockDispatch,
      expect.any(Function)
    );
  });

  it("falls back to the generated report when props.reportId does not match", () => {
    const { rerender } = renderPage({
      mode: "instant-bi-open",
      reportId: "unknown-report",
      parameters: {},
    });

    rerender(
      <HelicalReports
        mode="instant-bi-open"
        reportId="unknown-report"
        parameters={{ region: "North" }}
      />
    );

    expect(modifyFilters).toHaveBeenCalledWith(
      expect.objectContaining({ id: "test_123" }),
      { region: "North" }
    );
    expect(generateReport).toHaveBeenCalledWith(
      expect.objectContaining({ id: "test_123", mode: "instant-bi-open" }),
      mockDispatch,
      expect.any(Function)
    );
  });
});
