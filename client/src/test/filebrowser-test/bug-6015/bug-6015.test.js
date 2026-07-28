import "regenerator-runtime/runtime";
import "../../../utils/polyfill/url";
import { configureStore } from "@reduxjs/toolkit";
import { render, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { DashboardDesigner } from "../../../pages";
import { hiMockAxios } from "../../../app/mock-axios";
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
  });
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <DashboardDesigner />
      </Provider>
    </DndProvider>
  );
};

describe("Dashboard Designer Test", () => {
  test("File Browser Compact Mode z-index test case", async () => {
    await waitFor(() => render(<App />));
    const fbContainer=document.querySelector('.fb-compact')
    const computedStyle = window.getComputedStyle(fbContainer);
    expect(computedStyle.getPropertyValue('z-index')).toBe('1000');
  });
    
});