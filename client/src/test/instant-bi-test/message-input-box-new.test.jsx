import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import "@testing-library/jest-dom";
import { Provider } from "react-redux";
import MessageInputBoxNew from "../../components/hi-instant-bi/components/chat-screen/message-input-box-new";

jest.mock("../../components/hi-instant-bi/instant-bi-tooltip-title", () => {
  return function MockInstantBITooltip({ children }) {
    return <>{children}</>;
  };
});

jest.mock("../../components/hi-notifications/notify", () => () => ({
  warning: jest.fn(),
}));

jest.mock(
  "../../components/hi-instant-bi/components/chat-screen/chat-suggestions",
  () => {
    return function MockChatSuggestions(props) {
      return (
        <div data-testid="chat-suggestions-mock">
          {props.loadingRecommendation ? (
            <button
              type="button"
              data-testid="mock-abort-recommendations"
              onClick={props.onAbortRecommendations}
            >
              Abort suggestions
            </button>
          ) : null}
        </div>
      );
    };
  },
);

function createStore() {
  return {
    getState: () => ({
      app: {
        tutorialData: {},
        activeRoute: "",
      },
      fileBrowser: {
        showFileBrowser: false,
      },
    }),
    dispatch: jest.fn(),
    subscribe: () => () => {},
  };
}

const defaultActiveReport = {
  recommendationsVisible: true,
  loadingRecommendation: false,
  recommendations: [],
  inputValue: "",
  subject: { model: { file: "sample.agent" } },
};

const defaultProps = {
  onSend: jest.fn(),
  botStatus: false,
  activeReport: defaultActiveReport,
  dispatch: jest.fn(),
  isMetadataPresent: true,
  connectMetadata: jest.fn(),
  metadataLoading: false,
  onAbortRequest: jest.fn(),
  onAbortRecommendations: jest.fn(),
  activeReportId: "report-1",
  isEditMode: false,
  metadata: { data: { agentName: "sample.agent" } },
};

describe("MessageInputBoxNew", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it("should renders the square stop icon while bot is responding", () => {
    render(
      <Provider store={createStore()}>
        <MessageInputBoxNew {...defaultProps} botStatus />
      </Provider>,
    );
    expect(screen.getByTestId("ib-chat-abort-button")).toBeInTheDocument();
    expect(screen.getByLabelText("Abort this request")).toBeInTheDocument();
    expect(document.querySelector(".ib-stop-icon")).toBeInTheDocument();
    expect(screen.queryByTestId("ib-chat-send-button")).not.toBeInTheDocument();
  });

  it("calls onAbortRequest when stop button is clicked", () => {
    const onAbortRequest = jest.fn();
    render(
      <Provider store={createStore()}>
        <MessageInputBoxNew {...defaultProps} botStatus onAbortRequest={onAbortRequest} />
      </Provider>,
    );
    fireEvent.click(screen.getByTestId("ib-chat-abort-button"));
    expect(onAbortRequest).toHaveBeenCalledTimes(1);
  });

  it("should passes onAbortRecommendations to ChatSuggestions while loading", () => {
    const onAbortRecommendations = jest.fn();
    render(
      <Provider store={createStore()}>
        <MessageInputBoxNew
          {...defaultProps}
          activeReport={{
            ...defaultActiveReport,
            loadingRecommendation: true,
          }}
          onAbortRecommendations={onAbortRecommendations}
        />
      </Provider>,
    );
    fireEvent.click(screen.getByTestId("mock-abort-recommendations"));
    expect(onAbortRecommendations).toHaveBeenCalledTimes(1);
  });
});
