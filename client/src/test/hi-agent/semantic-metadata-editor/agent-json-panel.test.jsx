import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import "@testing-library/jest-dom";
import { AgentJsonPanel } from "../../../components/hi-agent/components/semantic-metadata-editor/agent-shelves/agent-json-panel";

jest.mock("../../../components/common/json-editor", () => ({
  MonacoJsonEditor: ({ value, onChange, isActive }) => (
    <textarea
      data-testid="monaco-json-editor"
      value={value}
      readOnly={!isActive}
      onChange={(e) => onChange?.(e.target.value)}
    />
  ),
}));

describe("AgentJsonPanel", () => {
  it("renders Copy JSON and Save JSON toolbar actions", () => {
    render(
      <AgentJsonPanel
        value="{}"
        onCopy={jest.fn()}
        onSave={jest.fn()}
        onChange={jest.fn()}
      />,
    );
    expect(screen.getByLabelText("Copy JSON")).toBeInTheDocument();
    expect(screen.getByLabelText("Save JSON")).toBeInTheDocument();
    expect(screen.queryByLabelText("Paste JSON")).not.toBeInTheDocument();
    expect(screen.getByTestId("monaco-json-editor")).toBeInTheDocument();
  });

  it("keeps Save disabled until there are unsaved changes", () => {
    const onSave = jest.fn();
    const { rerender } = render(
      <AgentJsonPanel
        value="{}"
        onCopy={jest.fn()}
        onSave={onSave}
        onChange={jest.fn()}
        hasUnsavedChanges={false}
      />,
    );

    const saveButton = screen.getByLabelText("Save JSON");
    expect(saveButton).toHaveAttribute("aria-disabled", "true");
    fireEvent.click(saveButton);
    expect(onSave).not.toHaveBeenCalled();
    expect(saveButton.querySelector(".toolbar-unsaved-dot")).toBeNull();

    rerender(
      <AgentJsonPanel
        value='{"domain":[]}'
        onCopy={jest.fn()}
        onSave={onSave}
        onChange={jest.fn()}
        hasUnsavedChanges
      />,
    );

    const dirtySaveButton = screen.getByLabelText("Save JSON");
    expect(dirtySaveButton).toHaveAttribute("aria-disabled", "false");
    expect(dirtySaveButton.querySelector(".toolbar-unsaved-dot")).toBeInTheDocument();
    fireEvent.click(dirtySaveButton);
    expect(onSave).toHaveBeenCalledTimes(1);
  });

  it("invokes onCopy and onChange when editing", () => {
    const onCopy = jest.fn();
    const onChange = jest.fn();
    render(
      <AgentJsonPanel
        value="{}"
        onCopy={onCopy}
        onSave={jest.fn()}
        onChange={onChange}
        hasUnsavedChanges
      />,
    );

    fireEvent.click(screen.getByLabelText("Copy JSON"));
    expect(onCopy).toHaveBeenCalledTimes(1);

    fireEvent.change(screen.getByTestId("monaco-json-editor"), {
      target: { value: '{"cube":[]}' },
    });
    expect(onChange).toHaveBeenCalledWith('{"cube":[]}');
    expect(screen.getByTestId("monaco-json-editor")).not.toHaveAttribute(
      "readonly",
    );
  });
});
