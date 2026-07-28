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
  props = { urlObj: { dir: "sai_ganesh", file: "test.efwdd" } };
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <DashboardDesigner {...props} />
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

  test("Designer Direct Url Edit", async () => {
    await waitFor(() => render(<App store={store} />));
    const getState = store.getState;
    expect(screen.queryByTestId(/hi-designer-parameters-button/i)).toBeTruthy();
    expect(screen.queryByTestId(/hi-designer-tools-shelf/i)).toBeTruthy();
    expect(getState().designer.present?.gridItemsData[0]?.reportInfo).toStrictEqual({
      component: "hreport",
      extension: "hr",
      file: {
        name: "parent.hr",
        path: "naresh/parent.hr",
        title: "parent",
      },
      filters: [],
      mode: "dashboard",
    });
  });
});
