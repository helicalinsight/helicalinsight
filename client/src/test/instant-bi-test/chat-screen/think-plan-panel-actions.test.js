import { fireEvent, render, screen } from "@testing-library/react";
import { Provider } from "react-redux";
import { createStore } from "redux";

jest.mock("react-markdown", () => ({
  __esModule: true,
  default: ({ children }) =>
    require("react").createElement("div", { "data-testid": "markdown" }, children),
}));

jest.mock("../../../components/hi-instant-bi/components/chat-screen/chat-tabs", () => ({
  __esModule: true,
  JsonTab: () => null,
}));

jest.mock("../../../components/hi-instant-bi/components/chat-screen/think-step-card", () => ({
  __esModule: true,
  default: () => null,
}));

jest.mock("../../../components/hi-instant-bi/components/chat-screen/think-citation-markdown", () => ({
  __esModule: true,
  default: ({ children }) =>
    require("react").createElement("div", null, children),
}));

jest.mock("../../../components/hi-instant-bi/components/chat-screen/work-behind-icon", () => ({
  __esModule: true,
  default: () => null,
}));

jest.mock("../../../components/hi-instant-bi/components/chat-screen/ai-sparkles-icon", () => ({
  __esModule: true,
  default: () => null,
}));

jest.mock("../../../components/hi-instant-bi/components/chat-screen/chat-typing-text", () => ({
  __esModule: true,
  default: () => null,
}));

jest.mock("../../../components/hi-instant-bi/components/chat-screen/ib-dashboard-view/ib-dashboard-view", () => ({
  __esModule: true,
  default: () => null,
}));

jest.mock("../../../components/common/components/hi-loading-bar", () => ({
  __esModule: true,
  default: () => null,
}));

jest.mock("../../../components/hi-instant-bi/instant-bi-tooltip-title", () => ({
  __esModule: true,
  default: ({ children }) => children,
}));

jest.mock("../../../components/hi-notifications/notify", () => () => ({
  error: jest.fn(),
  success: jest.fn(),
}));

jest.mock("../../../components/hi-instant-bi/utils/explore-instant-dashboard", () => ({
  __esModule: true,
  exploreInstantDashboard: jest.fn(),
}));

jest.mock("../../../components/hi-instant-bi/utils/instant-bi-requests", () => ({
  __esModule: true,
  loadInstantBIDataInsight: jest.fn(() => ({ abort: jest.fn() })),
}));

jest.mock("../../../components/hi-instant-bi/utils/common-utils", () => ({
  __esModule: true,
  getInstantBIAgentSubject: jest.fn(() => ({})),
}));

let mockActiveDashboardViewId = null;

jest.mock("../../../components/hi-instant-bi/components/chat-screen/dashboard-view-coordinator", () => ({
  __esModule: true,
  useDashboardView: () => ({
    activeDashboardViewId: mockActiveDashboardViewId,
    activateDashboardView: jest.fn(),
    deactivateDashboardView: jest.fn(),
  }),
}));

const ThinkPlanPanel = require("../../../components/hi-instant-bi/components/chat-screen/think-plan-panel").default;
const { exploreInstantDashboard } = require("../../../components/hi-instant-bi/utils/explore-instant-dashboard");
const { loadInstantBIDataInsight } = require("../../../components/hi-instant-bi/utils/instant-bi-requests");

const store = createStore((state = {
  app: { applicationSettingsData: { streamResponse: false } },
  hreport: { present: { reports: [] } },
}) => state);

const questionHistory = [{
  index: 1,
  question: "Question",
  report_model: { data_model: {}, viz_model: {} },
}];

const dashboardModel = {
  title: "Dashboard",
  items: [{ id: "tile-1" }],
};

const activeReport = {
  id: "report-1",
  activeChatID: "chat-1",
  reportInfo: { location: "agents", uuid: "sales.instant" },
  subject: { model: { file: "sales.metadata", dir: "agents" } },
};

const renderPanel = ({ isOpenMode = false, isEditMode = false, model = dashboardModel } = {}) => render(
  <Provider store={store}>
    <ThinkPlanPanel
      questionHistory={questionHistory}
      planMessageId="message-1"
      planChatSequenceId="seq-1"
      userInput="Question"
      dashboardModel={model}
      dispatch={store.dispatch}
      activeReport={activeReport}
      isOpenMode={isOpenMode}
      isEditMode={isEditMode}
    />
  </Provider>
);

describe("ThinkPlanPanel - response actions", () => {
  beforeEach(() => {
    mockActiveDashboardViewId = null;
    exploreInstantDashboard.mockClear();
    loadInstantBIDataInsight.mockClear();
  });

  test("shows Explore Dashboard in create mode when a dashboard is available", () => {
    renderPanel();

    expect(screen.getByTestId("ib-think-plan-explore-dashboard")).toBeTruthy();
    expect(screen.queryByTestId("ib-think-plan-data-insight-btn")).toBeNull();
  });

  test("shows Explore Dashboard and Explain in edit mode", () => {
    renderPanel({ isEditMode: true });

    expect(screen.getByTestId("ib-think-plan-explore-dashboard")).toBeTruthy();
    expect(screen.getByTestId("ib-think-plan-data-insight-btn")).toBeTruthy();
  });

  test("shows Explain without Explore Dashboard in open mode", () => {
    renderPanel({ isOpenMode: true });

    expect(screen.queryByTestId("ib-think-plan-explore-dashboard")).toBeNull();
    expect(screen.getByTestId("ib-think-plan-data-insight-btn")).toBeTruthy();
  });

  test("hides the overall Explain action while the dashboard view is active", () => {
    mockActiveDashboardViewId = "message-1";
    renderPanel({ isOpenMode: true });

    fireEvent.click(screen.getByTestId("ib-think-plan-view-dashboard"));

    expect(screen.queryByTestId("ib-think-plan-data-insight-btn")).toBeNull();

    fireEvent.click(screen.getByTestId("ib-think-plan-view-dashboard"));

    expect(screen.getByTestId("ib-think-plan-data-insight-btn")).toBeTruthy();
  });

  test("hides Explore Dashboard when no dashboard model is available", () => {
    renderPanel({ model: null });

    expect(screen.queryByTestId("ib-think-plan-explore-dashboard")).toBeNull();
  });

  test("explores the generated dashboard with the resolved model", () => {
    renderPanel();

    fireEvent.click(screen.getByTestId("ib-think-plan-explore-dashboard"));

    expect(exploreInstantDashboard).toHaveBeenCalledWith(expect.objectContaining({
      dashboardModel,
      activeReport,
      reports: [],
    }));
  });

  test("explains the overall dashboard in edit mode using the saved chat payload", () => {
    renderPanel({ isEditMode: true });

    fireEvent.click(screen.getByTestId("ib-think-plan-data-insight-btn"));

    expect(loadInstantBIDataInsight).toHaveBeenCalledWith(expect.objectContaining({
      chatSequenceId: "seq-1",
      chatId: "chat-1",
      useLoadChatPayload: true,
      think: true,
    }));
  });
});
