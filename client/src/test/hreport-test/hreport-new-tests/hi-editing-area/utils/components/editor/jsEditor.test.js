import { configureStore } from "@reduxjs/toolkit";
import { render, screen, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../../../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { hiMockAxios } from "../../../../../../../app/mock-axios";
import { editor_data, props } from "./js-editor.mocks";
import JsEditor from "../../../../../../../components/hi-reports/hi-editing-area/components/editor/js-editor";

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
    preloadedState: {
      hreport: { past: [], present: editor_data , future: [] },
    },
  });
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <JsEditor {...props} />
      </Provider>
    </DndProvider>
  );
};

describe("Rendering JsEditor", () => {
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
  document.createRange = () => {
    const range = new Range();

    range.getBoundingClientRect = () => {
        return {
            x: 0,
            y: 0,
            bottom: 0,
            height: 0,
            left: 0,
            right: 0,
            top: 0,
            width: 0,
            toJSON: () => { }
        };
    };

    range.getClientRects = () => {
        return {
            item: (index) => null,
            length: 0,
            *[Symbol.iterator]() { }
        };
    };

    return range;
}

afterAll(() => {
  global.gc && global.gc()
})

  test("JsEditor component", async () => {
    await waitFor(() => render(<App />));

    const comp = screen.queryByTestId(/hi-report-js-editor/i);

    expect(comp).toBeTruthy();

  });
});
