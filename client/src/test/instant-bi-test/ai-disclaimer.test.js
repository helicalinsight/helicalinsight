import "core-js/stable";
import "regenerator-runtime/runtime";
import "@testing-library/jest-dom";
import React from "react";
import { render, screen } from "@testing-library/react";
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

const renderWithStore = (ui) =>
  render(<Provider store={createStore()}>{ui}</Provider>);

const defaultProps = {
  dispatch: jest.fn(),
  activeReport: { inputValue: "", subject: {} },
  isMetadataPresent: true,
};

describe("Instant BI AI disclaimer", () => {
  it("does not show disclaimer below editor when there are no messages", () => {
    renderWithStore(<MessageInputBoxNew {...defaultProps} messages={[]} />);
    expect(screen.queryByTestId("ib-ai-disclaimer")).not.toBeInTheDocument();
  });

  it("shows disclaimer below editor when messages exist", () => {
    renderWithStore(
      <MessageInputBoxNew
        {...defaultProps}
        messages={[{ isUser: true, text: "Show sales by region" }]}
      />,
    );
    expect(screen.getByTestId("ib-ai-disclaimer")).toBeInTheDocument();
    expect(screen.getByTestId("ib-ai-disclaimer")).toHaveTextContent(
      "Instant BI uses AI and may generate inaccurate insights. Verify key findings.",
    );
  });
});
