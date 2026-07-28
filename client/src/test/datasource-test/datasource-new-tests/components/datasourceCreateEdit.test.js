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
