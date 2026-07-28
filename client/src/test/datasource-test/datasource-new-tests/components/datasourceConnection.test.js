import "regenerator-runtime/runtime";
import { configureStore } from "@reduxjs/toolkit";
import { render, screen, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { BrowserRouter } from "react-router-dom";
import { datasource_connection } from "./mocks/datasourceConnection.mock";
import { hiMockAxios } from "../../../../app/mock-axios";
import reducers from "../../../../redux";
import DataSourceConnection from "../../../../components/hi-datasources/Components/datasource-connection";

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
      datasource: datasource_connection,
    },
  });

  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <DataSourceConnection />
      </Provider>
    </DndProvider>
  );
};

describe("DataSourceConnection", () => {
  test("Rendering DataSourceConnection", async () => {
    await waitFor(() =>
      render(
        <BrowserRouter>
          <App />
        </BrowserRouter>
      )
    );
    expect(screen.queryByTestId(/hi-datasource-connection/i)).toBeTruthy();
  });
});
