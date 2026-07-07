import React, { createRef } from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import { Provider } from "react-redux";
import SemanticMetadataEditor from "../../../components/hi-agent/components/semantic-metadata-editor";
import { ensureShape } from "../../../components/hi-agent/components/semantic-metadata-editor/semantic-metadata-utils";

jest.mock("../../../components/hi-cube/cube", () => ({
  Cube: () => <div data-testid="cube-editor">Cube Editor</div>,
}));

jest.mock("../../../components/hi-cube/cubeEditorContext", () => ({
  CubeEditorProvider: ({ children }) => <>{children}</>,
}));

jest.mock("../../../components/common/json-editor/json-editor", () => {
  const actual = jest.requireActual(
    "../../../components/common/json-editor/json-editor",
  );

  return {
    ...actual,
    MonacoJsonEditor: ({ value, onChange, isActive }) => {
      if (!isActive) return null;
      return (
        <textarea
          data-testid="json-editor-textarea"
          value={value}
          onChange={(e) => onChange(e.target.value)}
        />
      );
    },
  };
});

jest.mock("../../../components/hi-agent/utils/agent-editor", () => ({
  createAgentEditorState: () => ({
    cubeFieldsData: {
      domainName: "",
      cubeDescription: "",
      children: [],
      hierarchyData: { isHierarchyPresent: false, hierarchyList: [] },
    },
    isCubeTableModeNormal: true,
  }),
  mergeAgentEditorState: (editorState) => editorState,
  agentEditorReducer: (state) => state || {},
}));

jest.mock("../../../components/common/editor-loading-view", () => ({
  EditorLoadingView: ({ handleAbort }) => (
    <div data-testid="loading-bar">
      <button type="button" onClick={handleAbort}>
        Abort
      </button>
    </div>
  ),
}));

jest.mock("../../../components/hi-notifications/notify", () => () => ({
  success: jest.fn(),
  error: jest.fn(),
}));

const renderWithStore = (agentOverrides = {}, ui) => {
  const store = {
    getState: () => ({
      agent: {
        metadataDetails: {},
        metadataTablesData: {},
        ...agentOverrides,
      },
    }),
    dispatch: jest.fn(),
    subscribe: jest.fn(() => () => {}),
  };
  return render(<Provider store={store}>{ui}</Provider>);
};

const sampleAgentData = ensureShape({
  metadata_info: {
    metadata: { domain: ["Finance"], description: "Test metadata" },
  },
  business_metrics: [{ metric: "revenue", description: "", formula: "", tables: [] }],
});

describe("SemanticMetadataEditor", () => {
  beforeEach(() => {
    Object.assign(navigator, {
      clipboard: { writeText: jest.fn().mockResolvedValue(undefined) },
    });
  });

  it("renders cube editor by default", () => {
    renderWithStore(
      {},
      <SemanticMetadataEditor
        agentData={sampleAgentData}
        onContentChange={jest.fn()}
        dispatch={jest.fn()}
      />,
    );
    expect(screen.getByTestId("cube-editor")).toBeInTheDocument();
  });

  it("shows loading UI when isLoading is true", () => {
    const handleAbort = jest.fn();
    renderWithStore(
      {},
      <SemanticMetadataEditor
        isLoading
        handleAbort={handleAbort}
        dispatch={jest.fn()}
      />,
    );
    expect(screen.getByTestId("loading-bar")).toBeInTheDocument();
    fireEvent.click(screen.getByRole("button", { name: "Abort" }));
    expect(handleAbort).toHaveBeenCalled();
  });

  it("notifies parent on content change when data updates", async () => {
    const onContentChange = jest.fn();
    renderWithStore(
      {},
      <SemanticMetadataEditor
        agentData={sampleAgentData}
        onContentChange={onContentChange}
        dispatch={jest.fn()}
      />,
    );
    await waitFor(() => expect(onContentChange).toHaveBeenCalled());
    const lastCall = onContentChange.mock.calls[onContentChange.mock.calls.length - 1][0];
    expect(lastCall).toContain("Finance");
  });

  it("renders raw JSON editor when isRawJsonView is true", () => {
    renderWithStore(
      {},
      <SemanticMetadataEditor
        agentData={sampleAgentData}
        isRawJsonView
        dispatch={jest.fn()}
      />,
    );
  });

  it("exposes copy and paste modal via ref", async () => {
    const ref = createRef();
    renderWithStore(
      {
        metadataDetails: {
          path: "team/folder",
          fileName: "Sales.metadata",
        },
      },
      <SemanticMetadataEditor
        ref={ref}
        agentData={sampleAgentData}
        dispatch={jest.fn()}
      />,
    );
    await ref.current.handleCopy();
    expect(navigator.clipboard.writeText).toHaveBeenCalled();
    const copied = JSON.parse(navigator.clipboard.writeText.mock.calls[0][0]);
    expect(copied.metadata).toBeUndefined();
    expect(copied.state).toBeUndefined();
    expect(copied.metadata_info).toBeDefined();

    ref.current.handleOpenPaste();
    expect(screen.getByText("Paste JSON")).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/Paste a metadata JSON/i)).toBeInTheDocument();
  });

  it("applies pasted agent JSON", async () => {
    const onContentChange = jest.fn();
    const ref = createRef();
    const pastedState = ensureShape({
      metadata_info: {
        metadata: { domain: ["Sales"], description: "Pasted metadata" },
      },
    });
    renderWithStore(
      {},
      <SemanticMetadataEditor
        ref={ref}
        agentData={sampleAgentData}
        onContentChange={onContentChange}
        dispatch={jest.fn()}
      />,
    );

    ref.current.handleOpenPaste();
    fireEvent.change(screen.getByPlaceholderText(/Paste a metadata JSON/i), {
      target: {
        value: JSON.stringify(pastedState),
      },
    });
    fireEvent.click(screen.getByRole("button", { name: /load/i }));
    await waitFor(() => {
      const lastContent =
        onContentChange.mock.calls[onContentChange.mock.calls.length - 1][0];
      expect(lastContent).toContain("Sales");
      expect(lastContent).toContain("Pasted metadata");
    });
  });
});
