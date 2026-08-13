import React from "react";
import { render, screen } from "@testing-library/react";
import "@testing-library/jest-dom";
import { AgentJsonPanel } from "../../../components/hi-agent/components/semantic-metadata-editor/agent-shelves/agent-json-panel";

jest.mock("../../../components/common/json-editor", () => {
  const React = require("react");
  return {
    JsonEditorPanel: ({ saveTitle = "Save JSON", ...props }) => (
      <div data-testid="shared-json-editor-panel">
        <button type="button" aria-label="Copy JSON" onClick={props.onCopy} />
        <button
          type="button"
          aria-label={saveTitle}
          aria-disabled={props.hasUnsavedChanges ? "false" : "true"}
          onClick={props.onSave}
        />
        <textarea
          data-testid="monaco-json-editor"
          value={props.value}
          onChange={(e) => props.onChange?.(e.target.value)}
        />
      </div>
    ),
  };
});

describe("AgentJsonPanel", () => {
  it("it should  to shared JsonEditorPanel with Save JSON title", () => {
    render(
      <AgentJsonPanel
        value="{}"
        onCopy={jest.fn()}
        onSave={jest.fn()}
        onChange={jest.fn()}
      />,
    );
    expect(screen.getByTestId("shared-json-editor-panel")).toBeInTheDocument();
    expect(screen.getByLabelText("Save JSON")).toBeInTheDocument();
  });
});
