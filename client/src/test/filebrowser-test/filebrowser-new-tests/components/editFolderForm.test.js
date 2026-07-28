import "regenerator-runtime/runtime";
import "../../../../utils/polyfill/url";
import { configureStore } from "@reduxjs/toolkit";
import { render, waitFor, screen } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { EditFolderForm } from "../../../../components/hi-fileBrowser/components";
import { file_browser } from "../mocks/contextDrawer.mock";
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
        <EditFolderForm />
      </Provider>
    </DndProvider>
  );
};

describe("EditFolderForm Test", () => {
  test("EditFolderForm", async () => {
    await waitFor(() => render(<App file_browser={file_browser} />));
    const form = screen.queryByTestId(/hi-file-browser-edit-form/i);

    expect(form).toBeTruthy();
  });
});
