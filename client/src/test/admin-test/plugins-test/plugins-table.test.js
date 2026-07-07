import { configureStore } from "@reduxjs/toolkit";
import { render, screen, fireEvent, act, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../redux";
import axios from "axios";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { HIPlugins } from "../../../components/hi-admin";
const crypto = require("crypto");

const admin_initial_view_state = JSON.parse(
  `{
     "pluginsData": null
    }`
);

const App = ({ admin_initial_view_state }) => {
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
    preloadedState: { admin: admin_initial_view_state },
  });
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <HIPlugins />
      </Provider>
    </DndProvider>
  );
};

describe("Plugins Table", () => {
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
  
  test("Plugins Info Table", async () => {
    render(<App admin_initial_view_state={admin_initial_view_state} />);
    expect(screen.queryByText(/Plugins/i)).toBeTruthy();
    expect(screen.queryByText(/Actions/i)).toBeTruthy();
    expect(screen.queryByText(/Installed Date/i)).toBeTruthy();
    expect(screen.queryByText(/Status/i)).toBeTruthy();
    expect(screen.queryByTestId(/plugins-table/i)).toBeTruthy();
  });
});
