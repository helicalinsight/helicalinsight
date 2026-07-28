import "regenerator-runtime/runtime";
import "../../../utils/polyfill/url";
import { configureStore } from "@reduxjs/toolkit";
import { render, screen, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { hiMockAxios } from "../../../app/mock-axios";
import { DashboardDesigner } from "../../../pages";
const crypto = require("crypto");

const App = ({ props, store }) => {
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <DashboardDesigner />
      </Provider>
    </DndProvider>
  );
};

describe("Dashboard Designer Test", () => {
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
  
  afterAll(() => {
    global.gc && global.gc()
  })

  test("Designer ClassName test", async () => {
    await waitFor(() => render(<App store={store} />));
    const ele = screen.queryByTestId(/hi-content-area/i);
    expect(ele.classList.contains("hi-content-area")).toBeTruthy();
  });
});
