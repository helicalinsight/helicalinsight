import "core-js/stable";
import "regenerator-runtime/runtime";
import { configureStore } from "@reduxjs/toolkit";
import { render, screen, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { BrowserRouter } from "react-router-dom";
import { reduxStateWhenDataSourceCardIsClicked } from "./bug-5635-mock-data";
import axios from "axios";
import DataSourceDefaultFiles from "../../../components/hi-datasources/Components/datasource-default-files";

const crypto = require("crypto");

const App = () => {
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
    preloadedState: { datasource: reduxStateWhenDataSourceCardIsClicked },
  });

  const props = {
    editable: false,
    driverCategory: "Big Data",
    urlFields: {},
    testConnClick: false,
    saveConnClick: false,
  };
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <DataSourceDefaultFiles {...props} />
      </Provider>
    </DndProvider>
  );
};

describe("Datasource Create And Edit", () => {
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
  
  test("Elements present in Create And Edit", async () => {
    await waitFor(() =>
      render(
        <BrowserRouter>
          <App />
        </BrowserRouter>
      )
    );
    expect(screen.queryByTestId(/datasource-host-name/i)).toBeTruthy();
    expect(screen.queryByTestId(/datasource-port/i)).toBeTruthy();
    expect(screen.queryByTestId(/datasource-database-name/i)).toBeTruthy();
    expect(screen.queryByTestId(/datasource-datasource-name/i)).toBeTruthy();
  });
});
