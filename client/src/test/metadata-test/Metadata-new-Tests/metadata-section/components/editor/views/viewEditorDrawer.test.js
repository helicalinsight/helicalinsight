import "core-js/stable";
import { configureStore } from "@reduxjs/toolkit";
import { render, screen } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../../../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { hiMockAxios } from "../../../../../../../app/mock-axios";
import { view_editor_drawer_data } from "./viewEditorDrawer.mock";
import ViewEditorDrawer from "../../../../../../../components/hi-metadata/components/editor/views/viewEditorDrawer";
const flushPromises = () => new Promise(setImmediate);
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
    preloadedState: { metadata: view_editor_drawer_data },
  });

  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <ViewEditorDrawer />
      </Provider>
    </DndProvider>
  );
};

describe("Rendering ViewEditorDrawer component", () => {
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
    global.gc && global.gc();
  });

  test("ViewEditorDrawer component", async () => {
    await flushPromises(render(<App />));
    const row = screen.queryByTestId(/view-editor-drawer/i);
    expect(row).toBeTruthy();
  });
});
