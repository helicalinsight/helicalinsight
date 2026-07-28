import "regenerator-runtime/runtime";
import "../../../../utils/polyfill/url";
import { configureStore } from "@reduxjs/toolkit";
import { render, waitFor, screen } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import ExportModal from "../../../../components/hi-fileBrowser/components/ExportModal";
import { file_browser } from "../mocks/exportmodal.mock";
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
        <ExportModal />
      </Provider>
    </DndProvider>
  );
};

describe("ExportModal Test", () => {
  test("ExportModal", async () => {
    await waitFor(() => render(<App file_browser={file_browser} />));
    const comp = screen.queryByTestId(/hi-file-browser-export/i);

    expect(comp).toBeFalsy();
  });
});
