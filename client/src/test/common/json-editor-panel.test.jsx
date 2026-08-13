import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import "@testing-library/jest-dom";
import { JsonEditorPanel } from "../../components/common/json-editor";

jest.mock("../../components/common/json-editor/json-editor", () => ({
  MonacoJsonEditor: ({ value, onChange, isActive }) => (
    <textarea
      data-testid="monaco-json-editor"
      value={value}
      readOnly={!isActive}
      onChange={(e) => onChange?.(e.target.value)}
    />
  ),
}));

describe("JsonEditorPanel", () => {
  it("should renders copy json & savejson actions", () => {
    render(
      <JsonEditorPanel
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

  it("should keep save disabled until there are unsaved changes", () => {
    const onSave = jest.fn();
    const { rerender } = render(
      <JsonEditorPanel
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
      <JsonEditorPanel
        value='{"domain":[]}'
        onCopy={jest.fn()}
        onSave={onSave}
        onChange={jest.fn()}
        hasUnsavedChanges
      />,
    );
    const dirtySaveButton = screen.getByLabelText("Save JSON");
    expect(dirtySaveButton).toHaveAttribute("aria-disabled", "false");
    expect(
      dirtySaveButton.querySelector(".toolbar-unsaved-dot"),
    ).toBeInTheDocument();
    fireEvent.click(dirtySaveButton);
    expect(onSave).toHaveBeenCalledTimes(1);
  });

  it("it should invokke onCopy & onChange when editing", () => {
    const onCopy = jest.fn();
    const onChange = jest.fn();
    render(
      <JsonEditorPanel
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

  it("it shoudk supports custom save title", () => {
    render(
      <JsonEditorPanel
        value="{}"
        onCopy={jest.fn()}
        onSave={jest.fn()}
        onChange={jest.fn()}
        saveTitle="Apply JSON"
        hasUnsavedChanges
      />,
    );
    expect(screen.getByLabelText("Apply JSON")).toBeInTheDocument();
  });
});
