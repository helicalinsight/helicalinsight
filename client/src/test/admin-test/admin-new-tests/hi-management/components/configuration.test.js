import { configureStore } from "@reduxjs/toolkit";
import { render, screen, fireEvent, act, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import { HTML5Backend } from "react-dnd-html5-backend";
import { DndProvider } from "react-dnd";
import Configuration from "../../../../../components/hi-admin/components/hi-management/Components/Configuration";
import reducers from "../../../../../redux";
import axios from "axios";
import { admin_data } from "./hi-managementmock-comp";
import { BrowserRouter } from "react-router-dom";
import { hiMockAxios } from "../../../../../app/mock-axios";
const crypto = require("crypto");

const App = ({ admin_data }) => {
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
    preloadedState: { admin: admin_data },
  });
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <Configuration />
      </Provider>
    </DndProvider>
  );
};

describe("Configuration", () => {
  test("Configuration component", async () => {
    render(
      <BrowserRouter>
        <App admin_data={admin_data} />
      </BrowserRouter>
    );

    expect(screen.queryByTestId(/hi-advanced-config-text/i)).toBeFalsy();

    const form = screen.queryByTestId(/hi-advanced-config-text/i); 
    expect(form).toBeFalsy();
  });
});
