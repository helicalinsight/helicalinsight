import "regenerator-runtime/runtime";
import { configureStore } from "@reduxjs/toolkit";
import { render, screen, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { BrowserRouter } from "react-router-dom";
import { flat_files } from "./mocks/datasourceFlatFiles.mock";
import { hiMockAxios } from "../../../../app/mock-axios";
import reducers from "../../../../redux";
import DataSourceFlatFiles from "../../../../components/hi-datasources/Components/datasource-flat-files";

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
      datasource: flat_files,
    },
  });
  const props = {
    editable: true,
    driverCategory: "RDBMS",
    testConnClick: false,
    saveConnClick: false,
  };

  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <DataSourceFlatFiles {...props} />
      </Provider>
    </DndProvider>
  );
};

describe("DataSourceFlatFiles", () => {
  test("Rendering DataSourceFlatFiles", async () => {
    await waitFor(() =>
      render(
        <BrowserRouter>
          <App />
        </BrowserRouter>
      )
    );
    expect(screen.queryByTestId(/hi-datasource-flat-files/i)).toBeFalsy();
  });
});
