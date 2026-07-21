import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import "@testing-library/jest-dom";
import { AgentJsonPanel } from "../../../components/hi-agent/components/semantic-metadata-editor/agent-shelves/agent-json-panel";

jest.mock("../../../components/hi-cube/cubeEditorContext", () => ({
  useCubeEditorBindings: () => ({
    cubeState: {
      cubeFieldsData: {
        domainName: "",
        cubeDescription: "",
        children: [],
        businessViewEntries: [],
      },
    },
  }),
}));

jest.mock("../../../components/common/json-editor", () => ({
  MonacoJsonEditor: ({ value }) => (
    <div data-testid="monaco-json-editor">{value}</div>
  ),
}));

jest.mock("../../../components/hi-agent/utils/agent-cube-bridge", () => ({
  getAgentStateFromCubeFields: () => ({
    domain: [],
    cube: [],
  }),
  serializeAgentDataForDisplay: () => '{\n  "domain": []\n}',
}));

describe("AgentJsonPanel", () => {
  it("renders Copy JSON and Paste JSON toolbar actions", () => {
    render(<AgentJsonPanel onCopy={jest.fn()} onPaste={jest.fn()} />);
    expect(screen.getByLabelText("Copy JSON")).toBeInTheDocument();
    expect(screen.getByLabelText("Paste JSON")).toBeInTheDocument();
    expect(screen.getByTestId("monaco-json-editor")).toBeInTheDocument();
  });

  it("invokes onCopy and onPaste when toolbar icons are clicked", () => {
    const onCopy = jest.fn();
    const onPaste = jest.fn();
    render(<AgentJsonPanel onCopy={onCopy} onPaste={onPaste} />);

    fireEvent.click(screen.getByLabelText("Copy JSON"));
    fireEvent.click(screen.getByLabelText("Paste JSON"));

    expect(onCopy).toHaveBeenCalledTimes(1);
    expect(onPaste).toHaveBeenCalledTimes(1);
  });
});
