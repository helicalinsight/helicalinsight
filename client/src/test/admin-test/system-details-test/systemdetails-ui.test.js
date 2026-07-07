import { configureStore } from "@reduxjs/toolkit";
import { render, screen, fireEvent, act, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import { HTML5Backend } from "react-dnd-html5-backend";
import { DndProvider } from "react-dnd";
import { HISystemDetails } from "../../../components/hi-admin";
import reducers from "../../../redux";
import axios from "axios";
const crypto = require("crypto");

const admin_initial_view_state = JSON.parse(
  `{
    "osTableExpand": false,
    "jvmTableExpand": false,
    "pluginsData": null
   }
  `
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
        <HISystemDetails />
      </Provider>
    </DndProvider>
  );
};

describe("System Details Component", () => {
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
  
  test("System Details", async () => {
    await waitFor(() => render(<App admin_initial_view_state={admin_initial_view_state} />));
    expect(screen.queryByTestId(/jvm-thread-details/i)).toBeTruthy();
    // expect(screen.queryByTestId(/os-details/i)).toBeTruthy();
    expect(screen.queryByTestId(/jvm-thread-details/i).innerHTML).toBe("JVM Thread Details");
    // expect(screen.queryByTestId(/os-details/i).innerHTML).toBe("OS Details");
    // expect(screen.queryByTestId(/jvm-thread-table/i)).toBeTruthy();
    expect(screen.queryByTestId(/os-table/i)).toBeTruthy();
  });
});
