import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import "@testing-library/jest-dom";
import {
  InfoTooltip,
  TextField,
  TextareaField,
  SelectField,
  StatTile,
} from "../../../components/hi-agent/components/semantic-metadata-editor/semantic-metadata-fields";

describe("semantic-metadata-fields", () => {
  describe("InfoTooltip", () => {
    it ("it should uses explicit info when provided", () => {
      render(<InfoTooltip label="Custom" info="Explicit help" />);
      expect(screen.getByLabelText("info-circle")).toBeInTheDocument();
    });
    it ("it should falls back to semantic tooltip for known labels", () => {
      render(<InfoTooltip label="Metric name" />);
      expect(screen.getByLabelText("info-circle")).toBeInTheDocument();
    });
  });

  describe("TextField", () => {
    it ("it should renders label and calls onChange when input changes", () => {
      const onChange = jest.fn();
      render(
        <TextField label="Metric name" value="revenue" onChange={onChange} />,
      );
      expect(screen.getByText("Metric name")).toBeInTheDocument();
      fireEvent.change(screen.getByRole("textbox"), {
        target: { value: "profit" },
      });
      expect(onChange).toHaveBeenCalledWith("profit");
    });
  });

  describe("TextareaField", () => {
    it ("it should renders textarea & propagates changes", () => {
      const onChange = jest.fn();
      render(
        <TextareaField label="Description" value="old" onChange={onChange} />,
      );
      fireEvent.change(screen.getByRole("textbox"), {
        target: { value: "new text" },
      });
      expect(onChange).toHaveBeenCalledWith("new text");
    });
  });

  describe("SelectField", () => {
    it ("should renders options & calls onChange on selection", () => {
      const onChange = jest.fn();
      render(
        <SelectField
          label="Database table"
          value=""
          options={["orders", "customers"]}
          onChange={onChange}
        />,
      );
      expect(screen.getByText("Database table")).toBeInTheDocument();
    });
  });

  describe("StatTile", () => {
    it ("it should displays label & value", () => {
      render(<StatTile label="Tables" value={3} />);
      expect(screen.getByText("Tables")).toBeInTheDocument();
      expect(screen.getByText("3")).toBeInTheDocument();
    });
  });
});
