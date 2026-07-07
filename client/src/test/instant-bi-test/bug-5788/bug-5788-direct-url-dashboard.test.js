import "core-js/stable";
import "regenerator-runtime/runtime";
import "../../../utils/polyfill/url";
import { configureStore } from "@reduxjs/toolkit";
import { render, screen, waitFor } from "@testing-library/react";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { Provider } from "react-redux";
import { hiMockAxios } from "../../../app/mock-axios";
import { HIInstantBI } from "../../../pages";
import reducers from "../../../redux";

const crypto = require("crypto");

const App = ({ store }) => {
  const props = {
    urlObj: { dir: "sai_ganesh", file: "test.instant", mode: "dashboard" },
  };
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <HIInstantBI {...props} />
      </Provider>
    </DndProvider>
  );
};

describe("InstantBI visualisation", () => {
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
    window.createObjectURL = jest.fn();
    window.HTMLElement.prototype.scrollBy = jest.fn();
    window.crypto = {};
    window.crypto.getRandomValues = (arr) => crypto.randomBytes(arr.length);
  });

  afterAll(() => {
    global.gc && global.gc()
  })
  
  test("jest example", async () => {
    expect(1 + 1).toBeTruthy();
  });

  test("Instant BI Direct Url dashboard mode", async () => {
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
    });
    const dispatch = store.dispatch;
    const getState = store.getState;
    await waitFor(() => render(<App store={store} />));
    expect(screen.queryByTestId(/hi-instant-bi-search/i)).toBeFalsy();
    expect(
      screen.queryByTestId(/hi-instant-bi-connect-to-metadata/i)
    ).toBeFalsy();
    // await waitFor(() =>
    //   fetchInstantBIReportAPI({
    //     dispatch,
    //     file: { path: "sai_ganesh", name: "test.instant" },
    //     mode: "edit",
    //   })
    // );
    expect(screen.queryByTestId(/hi-instant-bi-search/i)).toBeFalsy();
  });
});
