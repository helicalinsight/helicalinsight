import "core-js/stable";
import "regenerator-runtime/runtime";
import { configureStore } from "@reduxjs/toolkit";
import { render, screen, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { BrowserRouter } from "react-router-dom";
import { reduxStateWhenAdvancedDataSourceCardIsClicked } from "../../bug-5365/bug-5365-mock-data";
import { hiMockAxios } from "../../../../app/mock-axios";
import reducers from "../../../../redux";
import DataSourceCreateAndEdit from "../../../../components/hi-datasources/Components/datasource-create-and-edit";

const crypto = require("crypto");

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
    preloadedState: {
      datasource: reduxStateWhenAdvancedDataSourceCardIsClicked,
    },
  });
  const props = {
    editable: true,
    activeKey: "3",
  };

  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <DataSourceCreateAndEdit {...props} />
      </Provider>
    </DndProvider>
  );
};

describe("DataSourceCreateAndEdit", () => {
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
  
  test("Rendering DataSourceCreateAndEdit", async () => {
    await waitFor(() =>
      render(
        <BrowserRouter>
          <App />
        </BrowserRouter>
      )
    );
    expect(screen.queryByTestId(/hi-datasource-create-edit/i)).toBeTruthy();
  });
});
