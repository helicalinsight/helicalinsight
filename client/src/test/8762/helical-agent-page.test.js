import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import { Provider } from "react-redux";
import { HIAGENT } from "../../pages";
import { agentSaveAPI, agentEditServiceAPI } from "../../components/hi-instant-bi/utils/instant-bi-requests";
import actionTypes from "../../redux/actions/actionTypes";

const createMatchMediaMock = (query) => ({
  matches: false,
  media: query,
  onchange: null,
  addListener: jest.fn(),
  removeListener: jest.fn(),
  addEventListener: jest.fn(),
  removeEventListener: jest.fn(),
  dispatchEvent: jest.fn(),
});

beforeAll(() => {
  Object.defineProperty(window, "matchMedia", {
    writable: true,
    value: jest.fn().mockImplementation(createMatchMediaMock),
  });
});

global.matchMedia = global.matchMedia || createMatchMediaMock;

jest.mock("../../components", () => ({
  HINavbar: ({ taskbar }) => (
    <div data-testid="hi-navbar">
      {taskbar.map((item, index) => (
        <div key={index} data-testid={`taskbar-item-${item.tooltip}`}>
          <button
            aria-label={item.tooltip}
            onClick={item.callBack}
            data-testid={`button-${item.tooltip}`}
          >
            {item.tooltip}
          </button>
          {item.dropdown && (
            <div data-testid={`dropdown-${item.tooltip}`}>
              {item.dropdown.map((dropdownItem, idx) => (
                <button
                  key={idx}
                  data-testid={`dropdown-item-${dropdownItem.name}`}
                  onClick={dropdownItem.callBack}
                  disabled={dropdownItem.disabled}
                >
                  {dropdownItem.name}
                </button>
              ))}
            </div>
          )}
        </div>
      ))}
    </div>
  ),
}));
jest.mock("../../components/hi-agent/components/agentSidebar", () => ({
  AgentSidebar: ({ onResetAgentEditor, onAgentDataLoaded }) => (
    <div data-testid="agent-sidebar">
      <button data-testid="reset-editor-button" onClick={onResetAgentEditor}>
        Reset
      </button>
      <button
        data-testid="load-agent-button"
        onClick={() => onAgentDataLoaded?.({ test: "data" }, "TestAgent")}
      >
        Load Agent
      </button>
    </div>
  ),
}));
jest.mock("../../components/hi-fileBrowser/SaveItems", () => {
  return function MockSaveItems({ formHeading, onFormSumbit, inputValue }) {
    return (
      <div data-testid="save-items">
        <span data-testid="form-heading">{formHeading}</span>
        <span data-testid="input-value">{inputValue}</span>
        <button
          data-testid="save-button"
          onClick={() => onFormSumbit({ path: "/test/dir" }, inputValue)}
        >
          Save
        </button>
      </div>
    );
  };
});
jest.mock("../../components/hi-agent/components/semantic-metadata-editor", () => {
  const React = require("react");
  return React.forwardRef(function MockSemanticMetadataEditor(
    { onContentChange, isLoading, handleAbort, isRawJsonView },
    ref,
  ) {
    React.useImperativeHandle(ref, () => ({
      handleCopy: jest.fn(),
      handleOpenPaste: jest.fn(),
      toggleTableMode: jest.fn(),
      resetEditor: jest.fn(),
      getSaveState: () => ({
        cubeFieldsData: { children: [] },
        agentState: { business_metrics: [] },
      }),
    }));

    return (
      <div data-testid="agent-json-editor">
        {isRawJsonView ? (
          <textarea
            data-testid="json-editor-textarea"
            onChange={(e) => onContentChange(e.target.value)}
            disabled={isLoading}
          />
        ) : (
          <div data-testid="cube-editor-view">Cube Editor</div>
        )}
        {isLoading && (
          <button data-testid="abort-button" onClick={handleAbort}>
            Abort
          </button>
        )}
      </div>
    );
  });
});
jest.mock("../../components/hi-instant-bi/utils/instant-bi-requests", () => ({
  agentSaveAPI: jest.fn(() => ({ abort: jest.fn() })),
  agentEditServiceAPI: jest.fn(() => ({ abort: jest.fn() })),
}));
jest.mock("../../components/hi-notifications/notify", () => () => ({
  error: jest.fn(),
  success: jest.fn(),
}));
function createStore(initialState) {
  return {
    getState: () => initialState,
    dispatch: jest.fn(),
    subscribe: () => () => {},
  };
}
describe("HIAGENT Page", () => {
  let store;
  let initialState;
  beforeEach(() => {
    initialState = {
      app: { editModeInfo: null },
      agent: {
        agentMode: "create",
        metadataDetails: { path: "meta-dir", fileName: "Sample.metadata" },
        agentDataAfterSave: {},
        metadataTablesData: {},
      },
      fileBrowser: {
        globalFbEnabled: false,
        showFileBrowser: false,
      },
    };
    store = createStore(initialState);
    jest.clearAllMocks();
    window.matchMedia.mockImplementation(createMatchMediaMock);
  });
  it("renders cube editor by default", () => {
    render(
      <Provider store={store}>
        <HIAGENT urlObj={{}} />
      </Provider>,
    );
    expect(screen.getByTestId("cube-editor-view")).toBeInTheDocument();
  });
  it("resets editor via sidebar", () => {
    render(
      <Provider store={store}>
        <HIAGENT urlObj={{}} />
      </Provider>,
    );
    fireEvent.click(screen.getByTestId("load-agent-button"));
    fireEvent.click(screen.getByTestId("reset-editor-button"));
    expect(screen.getByTestId("cube-editor-view")).toBeInTheDocument();
  });
  it("loads agent editor when editModeInfo is for agent edit", async () => {
    agentEditServiceAPI.mockImplementationOnce(({ successCB }) => {
      successCB({
        data: {
          state: { loaded: "data" },
          agentName: "EditAgent",
        },
      });
      return { abort: jest.fn() };
    });
    initialState.app.editModeInfo = {
      dir: "agents/EditAgent.agent",
      file: "EditAgent.agent",
      extension: "agent",
      title: "EditAgent",
    };
    store = createStore(initialState);
    render(
      <Provider store={store}>
        <HIAGENT urlObj={{}} />
      </Provider>,
    );
    await waitFor(() => {
      expect(agentEditServiceAPI).toHaveBeenCalled();
    });
  });

  it("loads agent file from url", async () => {
    agentEditServiceAPI.mockImplementationOnce(({ successCB }) => {
      successCB({
        data: {
          state: { loaded: "data" },
          agentName: "UrlAgent",
          metadata: { location: "/test", metadataFileName: "test.metadata" },
        },
      });
      return { abort: jest.fn() };
    });
    render(
      <Provider store={store}>
        <HIAGENT urlObj={{ dir: "agents", file: "UrlAgent.agent" }} />
      </Provider>,
    );
    await waitFor(() => {
      expect(agentEditServiceAPI).toHaveBeenCalled();
    });
  });
});