import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import "@testing-library/jest-dom";
import { SemanticMetadataNav } from "../../../components/hi-agent/components/semantic-metadata-editor/semantic-metadata-nav";

jest.mock(
  "../../../components/hi-instant-bi/instant-bi-tooltip-title",
  () =>
    function MockInstantBITooltip({ children, title }) {
      return (
        <div data-testid="nav-tooltip" data-title={title}>
          {children}
        </div>
      );
    },
);

describe("SemanticMetadataNav", () => {
  it ("should renders all section nav buttons with counts", () => {
    const onSectionChange = jest.fn();
    render(
      <SemanticMetadataNav
        activeSection="overview"
        onSectionChange={onSectionChange}
        counts={{ metrics: 2, tables: 1, synonyms: 0, examples: 3, topics: 1 }}
      />,
    );
    expect(screen.getByText("Overview")).toBeInTheDocument();
    expect(screen.getByText("Business Metrics")).toBeInTheDocument();
    expect(screen.getByText("Tables & Columns")).toBeInTheDocument();
  });

  it ("it should marks active section & calls onsectionchange", () => {
    const onSectionChange = jest.fn();
    render(
      <SemanticMetadataNav
        activeSection="metrics"
        onSectionChange={onSectionChange}
        counts={{}}
      />,
    );
    const metricsButton = screen.getByText("Business Metrics").closest("button");
    fireEvent.click(screen.getByText("Raw JSON"));
    expect(onSectionChange).toHaveBeenCalledWith("json");
  });
});
