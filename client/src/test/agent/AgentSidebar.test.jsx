import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import "@testing-library/jest-dom";
import { Provider } from "react-redux";
import { AgentSidebar } from "../../components/hi-agent/components/agentSidebar";
import actionTypes from "../../redux/actions/actionTypes";

function createStore(initialState) {
  return {
    getState: () => initialState,
    dispatch: jest.fn(),
    subscribe: () => () => {},
  };
}
// Mock HiMetadataArea to avoid its internal errors
jest.mock(
  "../../components/hi-sidebar/hr-hreportSidebar/hi-metadata-area",
  () => ({
    __esModule: true,
    default: ({
      onConnectToMetadata,
      openFileBrowser,
      metadataLoading,
      abortFetchData,
    }) => (
      <div>
        <input placeholder="Search tables or columns" />
        {metadataLoading ? (
          <button type="button" data-testid="metadata-loading-bar" onClick={abortFetchData}>
            Abort metadata
          </button>
        ) : null}
        <button type="button" onClick={onConnectToMetadata}>
          Connect metadata
        </button>
        <button type="button" onClick={openFileBrowser}>
          Open file browser
        </button>
      </div>
    ),
  }),
);

jest.mock("../../components/hi-fileBrowser/hi-fileBrowser", () => ({
  HIFileBrowser: ({ onDoubleClick }) => (
    <button
      type="button"
      data-testid="fb-double-click"
      onClick={() =>
        onDoubleClick({
          path: "new-folder/Other.metadata",
          name: "Other.metadata",
          extension: "metadata",
        })
      }
    >
      Double click metadata
    </button>
  ),
}));

jest.mock("../../components/hi-fileBrowser/components", () => ({
  ShareFinalModal: () => <div data-testid="share-modal" />,
}));

jest.mock("../../components/common/custom-icons/CustomIcon", () => ({
  CustomIcon: () => <span />,
}));

const mockGetAgentMetadataTablesData = jest.fn();

jest.mock("../../base/requests/agent.requests", () => {
  return () => ({
    getAgentMetadataTablesData: (...args) => mockGetAgentMetadataTablesData(...args),
  });
});

jest.mock("../../components/hi-cube/helperMethods", () => ({
  fbOnClickHandler: jest.fn(),
}));

const baseState = {
  app: {
    applicationSettingsData: {
      userData: { user: { roles: [] } },
    },
    toggleSidebar: false,
  },
  agent: {
    agentMode: "edit",
    agentDataAfterSave: { dir: "saved", file: "Agent_1.agent" },
    metadataTablesData: {
      metadataName: "Sample.metadata",
      tables: [],
      columns: [],
    },
    metadataDetails: { path: "meta-dir", fileName: "Sample.metadata" },
  },
  fileBrowser: {
    globalFbEnabled: false,
    showFileBrowser: true,
    isShareModalVisible: false,
  },
  hreport: {
    present: {
      hrSidebar: "metadata",
      hreportEditLoading: false,
    },
  },
};

const defaultProps = {
  urlObj: {},
  shareRef: { current: false },
  fbProperties: {},
  setFilebrowserFor: jest.fn(),
  filebrowserFor: "",
  onResetAgentEditor: jest.fn(),
};

describe("AgentSidebar", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockGetAgentMetadataTablesData.mockReturnValue({ abort: jest.fn() });
  });

  test("renders metadata search and metadata name", () => {
    const store = createStore(baseState);
    render(
      <Provider store={store}>
        <AgentSidebar {...defaultProps} />
      </Provider>,
    );
    expect(
      screen.getByPlaceholderText(/search tables or columns/i),
    ).toBeInTheDocument();
    expect(screen.getByText("Sample.metadata")).toBeInTheDocument();
  });

  it("renders metadata panel only", () => {
    const store = createStore(baseState);
    render(
      <Provider store={store}>
        <AgentSidebar {...defaultProps} />
      </Provider>,
    );
    expect(
      screen.getByPlaceholderText(/search tables or columns/i),
    ).toBeInTheDocument();
    expect(screen.queryByRole("tab", { name: "Sections" })).not.toBeInTheDocument();
  });

  it("it should resets edit state when a new metadata file is selected", () => {
    const store = createStore(baseState);
    const onResetAgentEditor = jest.fn();
    render(
      <Provider store={store}>
        <AgentSidebar {...defaultProps} onResetAgentEditor={onResetAgentEditor} />
      </Provider>,
    );
    fireEvent.click(screen.getByTestId("fb-double-click"));
    fireEvent.click(screen.getByRole("button", { name: "OK" }));
    expect(onResetAgentEditor).toHaveBeenCalled();
    expect(store.dispatch).toHaveBeenCalledWith({
      type: actionTypes.AGENT_MODE,
      payload: "create",
    });
    expect(store.dispatch).toHaveBeenCalledWith({
      type: actionTypes.AGENT_DATA_AFTER_SAVE,
      payload: {},
    });
    expect(store.dispatch).toHaveBeenCalledWith({
      type: actionTypes.AGENT_METADATA_FILE_DETAILS,
      payload: {
        path: "new-folder",
        fileName: "Other.metadata",
      },
    });
  });

  test("opens metadata change modal before connecting to another metadata file", () => {
    const store = createStore(baseState);
    render(
      <Provider store={store}>
        <AgentSidebar {...defaultProps} />
      </Provider>,
    );
    fireEvent.click(screen.getByRole("button", { name: "Connect metadata" }));
    expect(
      screen.getByText(/Are you sure you want to open another file/i),
    ).toBeInTheDocument();
  });

  it("should fetch metadata tables on mount and allows aborting the request", () => {
    const abortMock = jest.fn();
    mockGetAgentMetadataTablesData.mockReturnValue({ abort: abortMock });
    const store = createStore(baseState);
    render(
      <Provider store={store}>
        <AgentSidebar {...defaultProps} />
      </Provider>,
    );
    expect(mockGetAgentMetadataTablesData).toHaveBeenCalledWith({
      path: "meta-dir",
      fileName: "Sample.metadata",
      callback: expect.any(Function),
      errback: expect.any(Function),
    });
    expect(screen.getByTestId("metadata-loading-bar")).toBeInTheDocument();
    fireEvent.click(screen.getByTestId("metadata-loading-bar"));
    expect(abortMock).toHaveBeenCalled();
  });
});
