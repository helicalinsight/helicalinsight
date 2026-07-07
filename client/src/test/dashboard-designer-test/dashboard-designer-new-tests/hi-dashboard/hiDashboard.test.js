import { configureStore } from "@reduxjs/toolkit";
import { render, screen, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { hiMockAxios } from "../../../../app/mock-axios";
import { dashboard_data } from "./dashboard-display-mocks";
import { HIDashboard } from "../../../../components";

const App = () => {
  const store = configureStore({
    reducer: reducers,
    middleware: (getDefaultMiddleware) =>
      getDefaultMiddleware({
        thunk: {
          extraArgument: hiMockAxios,
        },
        immutableCheck: false,
        serializableCheck: false,
      }),
    preloadedState: { designer: dashboard_data },
  });

const props = {
    type: "input",
    parameter: "example",
    listeners: ["listener1", "listener2"],
    reportInfo: {
        file: {
          path: "03_02/grid_aggregate.hr",
          name: "grid_aggregate.hr",
          title: "grid_aggregate",
        },
        mode: "dashboard",
        filters: [],
        component: "hreport",
        extension: "hr",
      },
    refreshReports: false,
    id: "dashboard1",
    dashboardVariables: { listener1: "value1", listener2: "value2" },
    reportId: "report1",
    addFilter: jest.fn(),
    addForwardFilterIPC: jest.fn(),
    deleteBackwardFilterIPC: jest.fn(),
    refreshFiltersIPC: jest.fn(),
    enableApplyButton: jest.fn(),
  };

  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <HIDashboard {...props} />
      </Provider>
    </DndProvider>
  );
};

describe("Rendering HIDashboard", () => {
  beforeAll(() => {
    delete window.matchMedia;
    window.matchMedia = (query) => ({
      matches: false,
      media: query,
      onchange: null,
      addListener: jest.fn(), // deprecated
      removeListener: jest.fn(), // deprecated
      addEventListener: jest.fn(),
      removeEventListener: jest.fn(),
      dispatchEvent: jest.fn(),
    });
    window.HTMLElement.prototype.scrollBy = jest.fn();
  });

  afterAll(() => {
    global.gc && global.gc()
  })
  
  test("HIDashboard component", async () => {
    await waitFor(() => render(<App />));
    const comp = screen.queryByTestId(/hi-dashboard/i);

    expect(comp).toBeFalsy();
  });
});
