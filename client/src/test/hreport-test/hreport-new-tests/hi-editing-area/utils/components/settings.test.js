import { configureStore } from "@reduxjs/toolkit";
import { render, screen, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { hiMockAxios } from "../../../../../../app/mock-axios";
import { settings_data } from "./settings.mocks";
import Settings from "../../../../../../components/hi-reports/hi-editing-area/components/settings/settings";

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
      hreport: { past: [], present: settings_data , future: [] },
    },
  });
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <Settings  />
      </Provider>
    </DndProvider>
  );
};

describe("Rendering Settings", () => {
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
  
  test("Settings component", async () => {
    await waitFor(() => render(<App />));

    const comp = screen.queryByTestId(/hi-report-settings/i);

    expect(comp).toBeTruthy();

  });
});
