import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import "@testing-library/jest-dom";
import { ChipsEditor } from "../../../components/hi-agent/components/semantic-metadata-editor/semantic-metadata-chips-editor";

describe("ChipsEditor", () => {
  it ("it should renders existing chipss", () => {
    render(
      <ChipsEditor items={["Sales", "HR"]} onChange={jest.fn()} />,
    );
    expect(screen.getByText("Sales")).toBeInTheDocument();
    expect(screen.getByText("HR")).toBeInTheDocument();
  });

  it ("it should adds a chip on enter & clears input", () => {
    const onChange = jest.fn();
    render(<ChipsEditor items={[]} placeholder="Add topic" onChange={onChange} />);
    const input = screen.getByPlaceholderText("Add topic");
    fireEvent.change(input, { target: { value: "Finance" } });
    fireEvent.keyDown(input, { key: "Enter" });
    expect(onChange).toHaveBeenCalledWith(["Finance"]);
  });

  it ("it should does not add duplicate chips", () => {
    const onChange = jest.fn();
    render(
      <ChipsEditor items={["Sales"]} placeholder="Add" onChange={onChange} />,
    );
    const input = screen.getByPlaceholderText("Add");
    fireEvent.change(input, { target: { value: "Sales" } });
    fireEvent.keyDown(input, { key: "Enter" });
    expect(onChange).not.toHaveBeenCalled();
  });

  it ("it should removes a chip when remove button is clicked", () => {
    const onChange = jest.fn();
    render(
      <ChipsEditor items={["A", "B"]} onChange={onChange} />,
    );
    const removeButtons = screen.getAllByRole("button");
    fireEvent.click(removeButtons[0]);
    expect(onChange).toHaveBeenCalledWith(["B"]);
  });

  it ("should removes last chip on backspace when input is empty", () => {
    const onChange = jest.fn();
    render(
      <ChipsEditor items={["Only"]} onChange={onChange} />,
    );
    const input = screen.getByRole("textbox");
    fireEvent.keyDown(input, { key: "Backspace" });
    expect(onChange).toHaveBeenCalledWith([]);
  });

  it("should applies scrollable class when scrollable is enabled", () => {
    const { container } = render(
      <ChipsEditor items={["Table_A"]} scrollable onChange={jest.fn()} />,
    );
    expect(container.querySelector(".me-chips-scrollable")).toBeInTheDocument();
  });
});
