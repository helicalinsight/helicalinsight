import "regenerator-runtime/runtime";
import "../../../../utils/polyfill/url";
import { configureStore } from "@reduxjs/toolkit";
import { render, waitFor, screen } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { SaveFileForm } from "../../../../components/hi-fileBrowser/components";
import { file_browser } from "../mocks/fbTable.mock";
import { hiMockAxios } from "../../../../app/mock-axios";
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
    preloadedState: { fileBrowser: file_browser },
  });
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <SaveFileForm />
      </Provider>
    </DndProvider>
  );
};

describe("SaveFileForm Test", () => {
  test("SaveFileForm", async () => {
    await waitFor(() => render(<App file_browser={file_browser} />));
    const form = screen.queryByTestId(/hi-file-browser-saveFileForm/i);

    expect(form).toBeTruthy();
  });
});
