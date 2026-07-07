import { configureStore } from "@reduxjs/toolkit";
import { render, screen, fireEvent, act, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import { HTML5Backend } from "react-dnd-html5-backend";
import { DndProvider } from "react-dnd";
import JvmThreadTable from "../../../../../components/hi-admin/components/hi-systemDetails/components/jvm-thread-table";
import reducers from "../../../../../redux";
import { admin_data } from "./hi-sd-mocks";
import { hiMockAxios } from "../../../../../app/mock-axios";
const crypto = require("crypto");

// const admin_initial_view_state = JSON.parse(
//   `{
//     "osTableExpand": false,
//     "jvmTableExpand": false,
//     "pluginsData": null
//    }
//   `
// );

const App = ({ admin_data }) => {
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
    preloadedState: { admin: admin_data },
  });
  let apiRef = jest.fn()
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <JvmThreadTable apiRef ={apiRef} />
      </Provider>
    </DndProvider>
  );
};

describe("JvmThreadTable Component", () => {
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
  
  test("JvmThreadTable", async () => {
    await waitFor(() => render(<App admin_data={admin_data} />));

    const Table = screen.queryByTestId(/jvm-thread-table-component/i);
    expect(Table).toBeTruthy();

    const refreshIcon = screen.queryByTestId(/jvm-thread-table-refresh/i);
      fireEvent.click(refreshIcon);
      expect(screen.queryByTestId(/jvm-thread-table-refresh/i)).toBeTruthy();

  });
});
