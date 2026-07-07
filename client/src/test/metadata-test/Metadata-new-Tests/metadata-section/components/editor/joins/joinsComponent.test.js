import { configureStore } from "@reduxjs/toolkit";
import { cleanup, render, screen, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../../../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { hiMockAxios } from "../../../../../../../app/mock-axios";
import { joins_data } from "./joins.mock";
import Joins from "../../../../../../../components/hi-metadata/components/editor/joins/index";

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
    preloadedState: { metadata: joins_data },
  });

  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <Joins />
      </Provider>
    </DndProvider>
  );
};

describe("Rendering Joins component", () => {
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
    cleanup()
  })
  
  test("Joins component", async () => {
    await waitFor(() => render(<App />));

    expect(screen.queryByTestId(/metadata-joins-section/i)).toBeTruthy();
  });
});
