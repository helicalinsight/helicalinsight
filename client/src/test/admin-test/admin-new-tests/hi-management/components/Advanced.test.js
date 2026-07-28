import { configureStore } from "@reduxjs/toolkit";
import { render, screen, fireEvent, act, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import { HTML5Backend } from "react-dnd-html5-backend";
import { DndProvider } from "react-dnd";
import Advanced from "../../../../../components/hi-admin/components/hi-management/Components/Advanced";
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
        <Advanced />
      </Provider>
    </DndProvider>
  );
};

describe("Advanced", () => {
  test("Advanced component", async () => {
    render(
      <BrowserRouter>
        <App admin_data={admin_data} />
      </BrowserRouter>
    );

    expect(screen.queryByTestId(/hi-advanced-text-one/i)).toBeTruthy();
    expect(screen.queryByTestId(/hi-advanced-text-one/i).innerHTML).toBe(
      "DICE Master"
    );

    expect(screen.queryByTestId(/hi-advanced-text-two/i)).toBeTruthy();
    expect(screen.queryByTestId(/hi-advanced-text-two/i).innerHTML).toBe(
      "DICE Worker"
    );

    expect(screen.queryByTestId(/hi-advanced-text-three/i)).toBeTruthy();
    expect(screen.queryByTestId(/hi-advanced-text-three/i).innerHTML).toBe(
      "DICE Application"
    );
    const card = screen.queryByTestId(/hi-advanced-card/i);
    
    expect(card).toBeTruthy();
  });
});
