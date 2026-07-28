import "regenerator-runtime/runtime";
import "../../../utils/polyfill/url";
import { configureStore } from "@reduxjs/toolkit";
import {  render, screen, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../redux";
import axios from "axios";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { HIDashboardDesigner } from "../../../components";
const crypto = require("crypto");

const App = ({ intial_designer_state }) => {
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
  });
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <HIDashboardDesigner />
      </Provider>
    </DndProvider>
  );
};

describe("Dashboard Designer Test", () => {
  test("rendering of dashboard designer content", async () => {
    await waitFor(() => {
      render(<App />);
    });
   
    expect(screen.queryByTestId(/hi-dd-content/i)).toBeTruthy();
  });
});
