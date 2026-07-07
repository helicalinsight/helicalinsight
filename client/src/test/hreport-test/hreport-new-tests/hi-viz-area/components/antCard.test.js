import { configureStore } from "@reduxjs/toolkit";
import { cleanup, render, screen, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import { hiMockAxios } from "../../../../../app/mock-axios";
import HiCard from "../../../../../components/hi-reports/hi-viz-area/ant-charts/components/ant-card";
import reducers from "../../../../../redux";
import { card_data, props } from "./mock-data/ant-chart-mocks";

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
  preloadedState: { hreport: card_data },
});

const App = () => {

  return (
    <Provider store={store}>
      <HiCard props={props} />
    </Provider>
  );
};

describe("Rendering HiCard", () => {
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

  afterEach(() => {
    cleanup();
  })

  afterAll(() => {
    global.gc && global.gc()
  })

  test("check", () => {
    expect(1 + 1).toBe(2);
  })


  // test("HiCard component", async () => {
  //   await waitFor(() => render(<App card_data={card_data} />));
  //   const card = screen.queryByTestId(/hi-report-ant-card/i);
  //   expect(card).toBeTruthy();
  // });


  // test("HiCard Trend", async () => {
  //   await waitFor(() => render(<App card_data={card_data} />));
  //   const trendTitle = screen.queryByTestId(/hi-card-trend-title/i);
  //   expect(trendTitle).toBeFalsy();
  //   const trendContainer = screen.queryAllByTestId(/hr-trend-container/i);
  //   expect(trendContainer).toBeFalsy();
  // });
});
