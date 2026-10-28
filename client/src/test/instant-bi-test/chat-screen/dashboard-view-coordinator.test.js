import { fireEvent, render, screen } from "@testing-library/react";
import { Provider } from "react-redux";
import { createStore } from "redux";

jest.mock("react-markdown", () => ({
  __esModule: true,
  default: ({ children }) =>
    require("react").createElement("div", { "data-testid": "markdown" }, children),
}));

jest.mock("../../../components/hi-instant-bi/components/chat-screen/ib-dashboard-view/ib-dashboard-view", () => ({
  __esModule: true,
  default: ({ planMessageId = "", active = false }) =>
    require("react").createElement("div", {
      "data-testid": `ib-dashboard-view-${planMessageId}`,
      "data-active": active ? "true" : "false",
    }),
}));

jest.mock("../../../components/hi-instant-bi/components/chat-screen/think-step-card", () => ({
  __esModule: true,
  default: ({ item }) =>
    require("react").createElement("div", {
      "data-testid": `think-step-${item?.index || ""}`,
    }),
}));

jest.mock("../../../components/hi-instant-bi/components/chat-screen/think-citation-markdown", () => ({
  __esModule: true,
  default: ({ children }) =>
    require("react").createElement("div", null, children),
}));

jest.mock("../../../components/hi-instant-bi/components/chat-screen/chat-typing-text", () => ({
  __esModule: true,
  default: () => null,
}));

jest.mock("../../../components/hi-instant-bi/components/chat-screen/ai-sparkles-icon", () => ({
  __esModule: true,
  default: () => null,
}));

jest.mock("../../../components/hi-instant-bi/components/chat-screen/work-behind-icon", () => ({
  __esModule: true,
  default: () => null,
}));

jest.mock("../../../components/hi-instant-bi/components/chat-screen/chat-tabs", () => ({
  __esModule: true,
  JsonTab: () => null,
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

const ThinkPlanPanel = require("../../../components/hi-instant-bi/components/chat-screen/think-plan-panel").default;
const { DashboardViewProvider } = require("../../../components/hi-instant-bi/components/chat-screen/dashboard-view-coordinator");

const store = createStore((state = {
  app: { applicationSettingsData: { streamResponse: false } },
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

const renderPanels = () => render(
  <Provider store={store}>
    <DashboardViewProvider>
      <ThinkPlanPanel
        questionHistory={questionHistory}
        planMessageId="message-1"
        dashboardModel={dashboardModel}
        dispatch={store.dispatch}
        isOpenMode={false}
      />
      <ThinkPlanPanel
        questionHistory={questionHistory}
        planMessageId="message-2"
        dashboardModel={dashboardModel}
        dispatch={store.dispatch}
        isOpenMode={false}
      />
    </DashboardViewProvider>
  </Provider>
);

describe("dashboard view coordination", () => {
  test("opening a dashboard closes the previously open dashboard view", () => {
    renderPanels();
    const buttons = screen.getAllByTestId("ib-think-plan-view-dashboard");

    fireEvent.click(buttons[0]);
    expect(buttons[0].getAttribute("aria-pressed")).toBe("true");
    expect(buttons[1].getAttribute("aria-pressed")).toBe("false");
    expect(screen.getByTestId("ib-dashboard-view-message-1").getAttribute("data-active")).toBe("true");

    fireEvent.click(buttons[1]);
    expect(buttons[0].getAttribute("aria-pressed")).toBe("false");
    expect(buttons[1].getAttribute("aria-pressed")).toBe("true");
    expect(screen.getByTestId("ib-dashboard-view-message-2").getAttribute("data-active")).toBe("true");
    expect(screen.getByTestId("ib-dashboard-view-message-1").getAttribute("data-active")).toBe("false");

    fireEvent.click(buttons[1]);
    expect(buttons[0].getAttribute("aria-pressed")).toBe("false");
    expect(buttons[1].getAttribute("aria-pressed")).toBe("false");

    fireEvent.click(buttons[0]);
    expect(buttons[0].getAttribute("aria-pressed")).toBe("true");
    expect(buttons[1].getAttribute("aria-pressed")).toBe("false");
  });
});
