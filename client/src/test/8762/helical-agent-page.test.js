import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import { Provider } from "react-redux";
import { HIAGENT } from "../../pages";
import {
  agentEditServiceAPI,
} from "../../components/hi-instant-bi/utils/instant-bi-requests";

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
    { onContentChange, isLoading, handleAbort, isRawJsonView, metadataShelfProps },
    ref,
  ) {
    React.useImperativeHandle(ref, () => ({
      handleCopy: jest.fn(),
      handleOpenPaste: jest.fn(),
      toggleTableMode: jest.fn(),
      resetEditor: jest.fn(),
      getSaveState: () => ({
        cubeFieldsData: { children: [] },
        agentState: {
          domain: [{ domain_name: "Sales", description: "desc", topics: [] }],
          cube_info: [{ cubeName: "", dimensions: [], measures: [] }],
        },
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
        <button
          type="button"
          data-testid="reset-editor-button"
          onClick={() => metadataShelfProps?.onResetAgentEditor?.()}
        >
          Reset
        </button>
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

jest.mock("../../base/requests/agent.requests", () => () => ({
  getAgentMetadataTablesData: jest.fn(() => ({ abort: jest.fn() })),
  getSemanticTypes: jest.fn(() => ({ abort: jest.fn() })),
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
      app: {
        editModeInfo: null,
        showNavbar: true,
      },
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

  it("does not show Copy JSON or Paste JSON in the global taskbar", () => {
    render(
      <Provider store={store}>
        <HIAGENT urlObj={{}} />
      </Provider>,
    );
    expect(screen.getByTestId("hi-navbar")).toBeInTheDocument();
    expect(
      screen.queryByTestId("taskbar-item-Copy JSON"),
    ).not.toBeInTheDocument();
    expect(
      screen.queryByTestId("taskbar-item-Paste JSON"),
    ).not.toBeInTheDocument();
    expect(screen.getByTestId("taskbar-item-Save")).toBeInTheDocument();
    expect(screen.getByTestId("taskbar-item-Layout")).toBeInTheDocument();
  });

  it("resets editor via metadata shelf reset callback", () => {
    render(
      <Provider store={store}>
        <HIAGENT urlObj={{}} />
      </Provider>,
    );
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
      dir: "agents/EditAgent.model",
      file: "EditAgent.model",
      extension: "model",
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
});
