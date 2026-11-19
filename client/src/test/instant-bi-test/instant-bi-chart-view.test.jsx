import React from "react";
import { act, fireEvent, render, screen, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import { Provider } from "react-redux";
import { createStore } from "redux";
import {
  ChartView,
  parseBackendErrorMessage,
} from "../../components/hi-instant-bi/utils/common-utils";
import InstantChartView from "../../components/hi-instant-bi/components/chat-screen/chart-view";
import { updateCacheRefreshStatus } from "../../redux/actions/instant-bi.actions";
import instantBIReducer from "../../redux/reducers/instant-bi.reducer";
import initialStates from "../../redux/reducers/initialStates";

jest.mock("../../components/hi-instant-bi/components/ib-custom-chart", () => {
  const React = require("react");
  return {
    __esModule: true,
    default: () => <div data-testid="ib-custom-chart" />,
    IB_CHART_RENDER_ERROR: "Something went wrong. Please try again.",
    IbResponseError: ({ details = "" }) => {
      const [show, setShow] = React.useState(false);
      return (
        <div data-testid="ib-vf-template-error">
          <span>Something went wrong. Please try again.</span>
          {details ? (
            <button type="button" onClick={() => setShow((v) => !v)}>
              {show ? "Hide Details" : "View Details"}
            </button>
          ) : null}
          {show && details ? <div>{details}</div> : null}
        </div>
      );
    },
  };
});

const mockHelicalReportsProps = { current: null };
jest.mock("../../pages", () => {
  const React = require("react");
  return {
    HelicalReports: (props) => {
      mockHelicalReportsProps.current = props;
      return <div data-testid="ib-custom-chart" />;
    },
  };
});

function createMockStore() {
  return {
    getState: () => ({
      hreport: { present: { reports: [] } },
      app: {},
    }),
    dispatch: jest.fn(),
    subscribe: () => () => {},
  };
}

function renderWithProvider(ui) {
  return render(<Provider store={createMockStore()}>{ui}</Provider>);
}

const tableData = [
  { alias: "Chennai", total_travel_cost: 1350 },
  { alias: "Jaipur", total_travel_cost: 1200 },
];

describe("ChartView", () => {
  it("should show chart render error when vf is missing for non-table charts", () => {
    renderWithProvider(
      <ChartView
        data={tableData}
        vf=""
        chartName="bar"
        id="msg-3"
      />,
    );
    expect(
      screen.getByText("Something went wrong. Please try again."),
    ).toBeInTheDocument();
    expect(screen.queryByText("View Details")).not.toBeInTheDocument();
    expect(screen.queryByTestId("ib-custom-chart")).not.toBeInTheDocument();
  });

  it("should show backend error details when View Details is clicked", () => {
    const backendError =
      "{'message': 'Error: SecurityException: The Table you are trying to access is out of scope or not found in the metadata', 'className': 'SecurityException'}";

    renderWithProvider(
      <ChartView
        data={tableData}
        vf=""
        chartName="bar"
        id="msg-4"
        backendError={backendError}
      />,
    );

    expect(screen.getByText("View Details")).toBeInTheDocument();
    fireEvent.click(screen.getByText("View Details"));
    expect(
      screen.getByText(
        "Error: SecurityException: The Table you are trying to access is out of scope or not found in the metadata",
      ),
    ).toBeInTheDocument();
  });

  it("should use compact kpi wrapper without fixed chart height", async () => {
    const { container } = renderWithProvider(
      <ChartView
        compact
        width={400}
        data={[{ total: 41 }]}
        vf="function DrawKPI() { return null; }"
        chartName="kpi"
        id="msg-5"
      />,
    );
    const wrapper = container.querySelector(".chart-wrapper--kpi");
    expect(wrapper).toBeTruthy();
    expect(wrapper.style.height).toBe("");
    await waitFor(() =>
      expect(screen.getByTestId("ib-custom-chart")).toBeInTheDocument(),
    );
  });
});

describe("InstantChartView cache refresh", () => {
  const chartViewProps = {
    fullChatResponse: { hreportId: "hr-cache" },
    vizType: "Chart",
  };

  const createCacheRefreshStore = () => {
    const rootReducer = (
      state = {
        hreport: { present: { reports: [] } },
        instantBI: initialStates.instantBIInitialState,
      },
      action,
    ) => ({
      ...state,
      instantBI: instantBIReducer(state.instantBI, action),
    });
    return createStore(rootReducer);
  };

  const renderView = (store) => (
    <Provider store={store}>
      <InstantChartView {...chartViewProps} />
    </Provider>
  );

  beforeEach(() => {
    mockHelicalReportsProps.current = null;
  });

  it("should not flag the initial render as a cache refresh", () => {
    const store = createCacheRefreshStore();
    render(renderView(store));

    expect(mockHelicalReportsProps.current.cacheRefresh).toBe(false);
    expect(mockHelicalReportsProps.current.cacheRefreshCount).toBe(0);
  });

  it("should forward a one-shot cache refresh and the refresh count", () => {
    const store = createCacheRefreshStore();
    const { rerender } = render(renderView(store));

    act(() => {
      store.dispatch(updateCacheRefreshStatus());
    });

    expect(mockHelicalReportsProps.current.cacheRefresh).toBe(true);
    expect(mockHelicalReportsProps.current.cacheRefreshCount).toBe(1);

    rerender(renderView(store));
    expect(mockHelicalReportsProps.current.cacheRefresh).toBe(false);
    expect(mockHelicalReportsProps.current.cacheRefreshCount).toBe(1);

    act(() => {
      store.dispatch(updateCacheRefreshStatus());
    });

    expect(mockHelicalReportsProps.current.cacheRefresh).toBe(true);
    expect(mockHelicalReportsProps.current.cacheRefreshCount).toBe(2);
  });
});

describe("parseBackendErrorMessage", () => {
  it("should extract message from backend error string", () => {
    expect(
      parseBackendErrorMessage(
        "{'message': 'Error: SecurityException: Table not found', 'className': 'SecurityException'}",
      ),
    ).toBe("Error: SecurityException: Table not found");
  });

  it("should return object message as-is", () => {
    expect(
      parseBackendErrorMessage({
        message: "Error: Table out of scope",
        className: "SecurityException",
      }),
    ).toBe("Error: Table out of scope");
  });
});
