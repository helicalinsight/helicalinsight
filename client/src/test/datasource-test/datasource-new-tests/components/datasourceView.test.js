import "regenerator-runtime/runtime";
import { configureStore } from "@reduxjs/toolkit";
import { render, screen, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { BrowserRouter } from "react-router-dom";
import { view_data } from "./mocks/datasourceView.mock";
import { hiMockAxios } from "../../../../app/mock-axios";
import reducers from "../../../../redux";
import DataSourceView from "../../../../components/hi-datasources/Components/datasource-view";

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
      datasource: view_data,
    },
  });

  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <DataSourceView />
      </Provider>
    </DndProvider>
  );
};

describe("DataSourceView", () => {
  test("Rendering DataSourceView", async () => {
    await waitFor(() =>
      render(
        <BrowserRouter>
          <App />
        </BrowserRouter>
      )
    );
    expect(screen.queryByTestId(/hi-datasource-view-row/i)).toBeTruthy();
  });
});
