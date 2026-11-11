import { render, screen } from "@testing-library/react";
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
  default: ({ testId = "ib-work-behind" }) =>
    require("react").createElement("div", { "data-testid": testId }),
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

jest.mock("../../../components/hi-instant-bi/utils/instant-bi-requests", () => ({
  __esModule: true,
  loadInstantBIDataInsight: jest.fn(),
}));

jest.mock("../../../components/hi-instant-bi/utils/common-utils", () => ({
  __esModule: true,
  getInstantBIAgentSubject: jest.fn(() => ({})),
}));

jest.mock("../../../components/hi-instant-bi/components/chat-screen/dashboard-view-coordinator", () => ({
  __esModule: true,
  useDashboardView: () => ({
    activeDashboardViewId: null,
    activateDashboardView: jest.fn(),
    deactivateDashboardView: jest.fn(),
  }),
}));

const ThinkPlanPanel = require("../../../components/hi-instant-bi/components/chat-screen/think-plan-panel").default;

const streamStore = createStore((state = {
  app: { applicationSettingsData: { streamResponse: true } },
}) => state);

const questionHistory = [
  {
    index: 1,
    question: "Question",
    report_model: { data_model: {}, viz_model: {} },
  },
];

const renderPanel = (isEditMode) => render(
  <Provider store={streamStore}>
    <ThinkPlanPanel
      questionHistory={questionHistory}
      planMessageId="message-1"
      dispatch={streamStore.dispatch}
      isOpenMode={false}
      isEditMode={isEditMode}
    />
  </Provider>
);

describe("ThinkPlanPanel - workings visibility", () => {
  test("shows the workings tab in create mode when streaming is enabled", () => {
    renderPanel(false);

    expect(screen.getByTestId("ib-think-plan-workings")).toBeTruthy();
  });

  test("hides the workings tab in edit mode when streaming is enabled", () => {
    renderPanel(true);

    expect(screen.queryByTestId("ib-think-plan-workings")).toBeNull();
    expect(screen.queryByTestId("ib-think-plan-inspector-workings")).toBeNull();
  });
});
