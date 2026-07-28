import "regenerator-runtime/runtime";
import { configureStore } from "@reduxjs/toolkit";
import { render, screen, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { BrowserRouter } from "react-router-dom";
import { jndi_data } from "./mocks/datasourceJndiFiles.mock";
import { hiMockAxios } from "../../../../app/mock-axios";
import reducers from "../../../../redux";
import UploadFile from "../../../../components/hi-datasources/Components/datasource-upload";

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
      datasource: jndi_data,
    },
  });

  const props = {
    getData: jest.fn(),
    onCloseDrawer: jest.fn(),
  };

  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <UploadFile {...props} />
      </Provider>
    </DndProvider>
  );
};

describe("UploadFile", () => {
  test("Rendering UploadFile", async () => {
    await waitFor(() =>
      render(
        <BrowserRouter>
          <App />
        </BrowserRouter>
      )
    );
    expect(screen.queryByTestId(/hi-datasource-upload/i)).toBeTruthy();
  });
});
