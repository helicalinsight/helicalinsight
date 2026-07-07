import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import "@testing-library/jest-dom";
import { ensureShape } from "../../../components/hi-agent/components/semantic-metadata-editor/semantic-metadata-utils";
import {
  OverviewSection,
  MetricsSection,
  TablesSection,
  SynonymsSection,
  ExamplesSection,
  TopicsSection,
  JsonSection,
} from "../../../components/hi-agent/components/semantic-metadata-editor/semantic-metadata-sections";

function emptyData() {
  return ensureShape({});
}

describe("semantic-metadata-sections", () => {
  describe("OverviewSection", () => {
    it ("ir should renders domain editor & stat tiles for empty data", () => {
      const setData = jest.fn();
      render(<OverviewSection data={emptyData()} setData={setData} />);
      expect(screen.getByText("Business Domain(s)")).toBeInTheDocument();
      expect(screen.getByText("Tables")).toBeInTheDocument();
    });

    it ("it should updates description via setData", () => {
      const setData = jest.fn();
      const data = ensureShape({
        metadata_info: {
          metadata: { domain: [], description: "initial" },
        },
      });
      render(<OverviewSection data={data} setData={setData} />);
      const textareas = screen.getAllByRole("textbox");
      const descriptionArea = textareas.find(
        (el) => el.value === "initial",
      );
      fireEvent.change(descriptionArea, { target: { value: "updated" } });
      expect(setData).toHaveBeenCalled();
    });
  });

  describe("MetricsSection", () => {
    it ("it should shows empty state when no metrics", () => {
      render(<MetricsSection data={emptyData()} setData={jest.fn()} />);
      expect(
        screen.getByText(/No business metrics yet/i),
      ).toBeInTheDocument();
      expect(screen.getByRole("button", { name: /Add Metric/i })).toBeInTheDocument();
    });
  });

  describe("TablesSection", () => {
    it ("it should shows empty state when no tables", () => {
      render(<TablesSection data={emptyData()} setData={jest.fn()} />);
      expect(screen.getByText("No tables defined.")).toBeInTheDocument();
    });
  });

  describe("SynonymsSection", () => {
    it ("it should shows empty state when no synonyms", () => {
      render(<SynonymsSection data={emptyData()} setData={jest.fn()} />);
      expect(screen.getByText("No synonym sets yet.")).toBeInTheDocument();
    });
  });

  describe("ExamplesSection", () => {
    it ("it should shows empty state when no examples", () => {
      render(<ExamplesSection data={emptyData()} setData={jest.fn()} />);
      expect(screen.getByText("No example groups yet.")).toBeInTheDocument();
    });
  });

  describe("TopicsSection", () => {
    it ("it should shows empty domain & mapping states", () => {
      render(<TopicsSection data={emptyData()} setData={jest.fn()} />);
      expect(screen.getByText("No domains yet.")).toBeInTheDocument();
      expect(screen.getByText("No topic mappings yet.")).toBeInTheDocument();
      expect(screen.getByText("Domains - Topics")).toBeInTheDocument();
      expect(screen.getByText("Topic - Components")).toBeInTheDocument();
    });

    it("it shoould collapse & expands a domain card", () => {
      const data = ensureShape({
        domain: [{ domain_name: "Sales", topics: ["Revenue"] }],
      });
      render(<TopicsSection data={data} setData={jest.fn()} />);
      expect(screen.getByDisplayValue("Sales")).toBeInTheDocument();
      const toggle = screen.getByText("1.Sales").closest("[role='button']");
      fireEvent.click(toggle);
      expect(screen.queryByDisplayValue("Sales")).not.toBeInTheDocument();
      fireEvent.click(toggle);
      expect(screen.getByDisplayValue("Sales")).toBeInTheDocument();
    });
  });

  describe("JsonSection", () => {
    it ("t should renders stringified metadata JSON preview", () => {
      const data = ensureShape({
        metadata_info: { metadata: { domain: ["Retail"], description: "" } },
      });
      const { container } = render(<JsonSection data={data} />);
      const pre = container.querySelector("pre");
      expect(pre.innerHTML).toContain("Retail");
      expect(pre.innerHTML).toContain("tok-str");
    });
  });
});
