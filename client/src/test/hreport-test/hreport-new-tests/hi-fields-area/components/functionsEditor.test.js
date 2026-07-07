import { configureStore } from "@reduxjs/toolkit";
import { render, screen, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { hiMockAxios } from "../../../../../app/mock-axios";
import { editor_data, props } from "./mocks/editor.mocks";
import FunctionsEditor from "../../../../../components/hi-reports/hi-fields-area/db-functions/editor";

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
    preloadedState: { hreport: editor_data },
  });

  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <FunctionsEditor {...props} />
      </Provider>
    </DndProvider>
  );
};

describe("Rendering FunctionsEditor", () => {
  beforeAll(() => {
    delete window.matchMedia;
    window.matchMedia = (query) => ({
      matches: false,
      media: query,
      onchange: null,
      addListener: jest.fn(), // deprecated
      removeListener: jest.fn(), // deprecated
      addEventListener: jest.fn(),
      removeEventListener: jest.fn(),
      dispatchEvent: jest.fn(),
    });
    window.HTMLElement.prototype.scrollBy = jest.fn();
  });

  afterAll(() => {
    global.gc && global.gc()
  })
    
  test("FunctionsEditor component", async () => {
    await waitFor(() => render(<App editor_data={editor_data} />));

    expect(screen.queryByTestId(/functions-editor/i)).toBeTruthy();
    expect(screen.queryByTestId(/functions-editor/i).innerHTML).toBe(
      "Functions"
    );

    const menu = screen.queryByTestId(/hi-report-editor-menu/i);

    expect(menu).toBeTruthy();
  });
});
