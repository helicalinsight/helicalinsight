import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import "@testing-library/jest-dom";
import ChatSuggestions from "../../components/hi-instant-bi/components/chat-screen/chat-suggestions";

jest.mock("../../components/common/components/hi-loading-bar", () => {
  return function MockLoadingBar({ handleClick }) {
    return (
      <button type="button" data-testid="recommendations-loading-bar" onClick={handleClick}>
        Abort recommendations
      </button>
    );
  };
});

jest.mock("antd", () => {
  const actual = jest.requireActual("antd");
  return {
    ...actual,
    Skeleton: {
      Input: ({ className }) => <div data-testid="suggestion-skeleton" className={className} />,
    },
  };
});

describe("ChatSuggestions", () => {
  it("shoudl renders suggestion items when not loading", () => {
    render(
      <ChatSuggestions
        loadingRecommendation={false}
        recommendations={["First question", "Second question"]}
      />,
    );
    expect(screen.getByText("Suggested")).toBeInTheDocument();
    expect(screen.getByText("First question")).toBeInTheDocument();
    expect(screen.getByText("Second question")).toBeInTheDocument();
    expect(screen.queryByTestId("recommendations-loading-bar")).not.toBeInTheDocument();
  });

  it("it should shows loading bar & skeleton while recommendations are loading", () => {
    render(<ChatSuggestions loadingRecommendation recommendations={[]} />);
    expect(screen.getByTestId("recommendations-loading-bar")).toBeInTheDocument();
    expect(screen.getAllByTestId("suggestion-skeleton")).toHaveLength(3);
  });

  it("should calls onAbortRecommendations when loading bar is clicked", () => {
    const onAbortRecommendations = jest.fn();
    render(
      <ChatSuggestions
        loadingRecommendation
        recommendations={[]}
        onAbortRecommendations={onAbortRecommendations}
      />,
    );
    fireEvent.click(screen.getByTestId("recommendations-loading-bar"));
    expect(onAbortRecommendations).toHaveBeenCalledTimes(1);
  });

  it("should sends the selected suggestion through handlers", () => {
    const onClickRecommendation = jest.fn();
    const handleSendMessage = jest.fn();
    render(
      <ChatSuggestions
        loadingRecommendation={false}
        recommendations={["Show total sales"]}
        onClickRecommendation={onClickRecommendation}
        handleSendMessage={handleSendMessage}
      />,
    );
    fireEvent.click(screen.getByText("Show total sales"));
    expect(onClickRecommendation).toHaveBeenCalledWith("Show total sales");
    expect(handleSendMessage).toHaveBeenCalledWith("Show total sales");
  });

  it("should does not send suggestions while bot is responding", () => {
    const handleSendMessage = jest.fn();
    render(
      <ChatSuggestions
        loadingRecommendation={false}
        recommendations={["Show total sales"]}
        botStatus
        handleSendMessage={handleSendMessage}
      />,
    );
    fireEvent.click(screen.getByText("Show total sales"));
    expect(handleSendMessage).not.toHaveBeenCalled();
  });
});
