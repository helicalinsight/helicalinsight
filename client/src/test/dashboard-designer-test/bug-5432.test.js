import "core-js/stable";
import "regenerator-runtime/runtime";
import "../../utils/polyfill/url";
import { configureStore } from "@reduxjs/toolkit";
import { render, screen } from "@testing-library/react";
import { itemAddedDesignerState, itemSavedDesignerState } from "./mock.data";
import { Provider } from "react-redux";
import reducers from "../../redux";
import axios from "axios";
import { HIDashboardDesigner } from "../../components/hi-dashboard-designer";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
const crypto = require("crypto");

const App = ({ intial_designer_state }) => {
  const store = configureStore({
    reducer: reducers,
    middleware: (getDefaultMiddleware) =>
      getDefaultMiddleware({
        thunk: {
          extraArgument: axios,
        },
        immutableCheck: false,
        serializableCheck: false,
      }),
    preloadedState: {
      designer: { present: { ...intial_designer_state }, past: [], future: [] },
    },
  });
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <HIDashboardDesigner />
      </Provider>
    </DndProvider>
  );
};

describe("Dashboard Designer Test", () => {
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
    window.crypto = {};
    window.crypto.getRandomValues = (arr) => crypto.randomBytes(arr.length);
  });

  afterAll(() => {
    global.gc && global.gc()
  })

  test("Grid item initial className test", async () => {
    render(<App intial_designer_state={itemAddedDesignerState} />);
    expect(screen.queryByTestId(/item-0u1vF/i)).toBeTruthy();
    const { container } = render(
      <App intial_designer_state={itemAddedDesignerState} />
    );
    expect(container.getElementsByClassName("hi-outline-hover").length).toBe(1);
  });
  test("Grid item saved classname test", async () => {
    const { container } = render(
      <App intial_designer_state={itemSavedDesignerState} />
    );

    expect(container.getElementsByClassName("hi-outline-hover").length).toBe(0);
  });
});
