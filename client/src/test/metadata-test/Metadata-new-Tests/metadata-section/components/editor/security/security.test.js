import { configureStore } from "@reduxjs/toolkit";
import { render, screen, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../../../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { hiMockAxios } from "../../../../../../../app/mock-axios";
import { security_data } from "./security.mocks";
import Security from "../../../../../../../components/hi-metadata/components/editor/security";

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
    preloadedState: { metadata: security_data },
  });

  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <Security />
      </Provider>
    </DndProvider>
  );
};

describe("Rendering Security component", () => {
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
        toJSON: () => {},
      };
    };

    range.getClientRects = () => {
      return {
        item: (index) => null,
        length: 0,
        *[Symbol.iterator]() {},
      };
    };

    return range;
  };
  
  afterAll(() => {
    global.gc && global.gc()
  })
  
  test("Security component", async () => {
    await waitFor(() => render(<App />));
    const form = screen.queryByTestId(/hi-metadata-security/i);

    expect(form).toBeFalsy();

    expect(screen.queryByTestId(/hi-metadata-security-text/i)).toBeFalsy();
  });
});
