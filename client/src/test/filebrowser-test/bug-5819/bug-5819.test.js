import "regenerator-runtime/runtime";
import "../../../utils/polyfill/url";
import { configureStore } from "@reduxjs/toolkit";
import { render, screen, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { hiMockAxios } from "../../../app/mock-axios";
import HIIcon from "../../../components/common/icons/hi-icons";
const crypto = require("crypto");

const App = ({ store }) => {
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <HIIcon name="hi-instant-bi-svg" />
      </Provider>
    </DndProvider>
  );
};

describe("HI Icon Test", () => {
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

  test("Instant BI Icon Style", async () => {
    await waitFor(() => render(<App store={store} />));
    const iconEle = screen.queryByTestId(/hi-instant-bi-icon/i);
    const styles = getComputedStyle(iconEle);
    expect(styles.vericalAlign).toBe(undefined);
  });
});
