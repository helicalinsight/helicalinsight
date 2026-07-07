import "core-js/stable";
import "regenerator-runtime/runtime";
import { app5505 } from "./5505-constants";
import { HIRecentReportsCard } from "../../../../components/hi-users/components";
import { configureStore } from "@reduxjs/toolkit";
import reducers from "../../../../redux";
import axios from "axios";
import { HTML5Backend } from "react-dnd-html5-backend";
import { render, screen, waitFor, fireEvent } from "@testing-library/react";
import { Provider } from "react-redux";
import { DndProvider } from "react-dnd";
import { BrowserRouter } from "react-router-dom";

const crypto = require("crypto");

const App = ({ store }) => {
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <HIRecentReportsCard
          report={{
            "dir": "5984Gpl",
            "file": "GroovyPlainReportSampleTravelData.hr",
            "lastModified": 1684342162656,
            "logicalPath": "//5984Gpl",
            "reportPath": "5984Gpl/GroovyPlainReportSampleTravelData.hr",
            "title": "GroovyPlainReportSampleTravelData"
        }}
        />
      </Provider>
    </DndProvider>
  );
};

describe("HIRecentReportsCard testing", () => {
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
    window.crypto = {};
    window.crypto.getRandomValues = (arr) => crypto.randomBytes(arr.length);
  });

  afterAll(() => {
    global.gc && global.gc()
  })
  
  test("Elements present in HIRecentReportsCard", async () => {
    const store = configureStore({
      reducer: reducers,
      middleware: (getDefaultMiddleware) =>
        getDefaultMiddleware({
          thunk: {
            extraArgument: axios,
          },
          immutableCheck: false,
          serializableCheck: false,
        }),
      preloadedState: {
        app: app5505,
      },
    });
    await waitFor(() =>
      render(
        <BrowserRouter>
          <App store={store} />
        </BrowserRouter>
      )
    );
    // const dispatch = store.dispatch;
    expect(screen.queryByTestId(/recent-report-paragraph/i)).toBeTruthy();
  });
});
