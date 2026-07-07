import { configureStore } from "@reduxjs/toolkit";
import { render, screen, fireEvent } from "@testing-library/react";
import { Provider } from "react-redux";
import { HTML5Backend } from "react-dnd-html5-backend";
import { DndProvider } from "react-dnd";
import reducers from "../../../../../redux";
import { export_data } from "./hi-managementmock-comp";
import { BrowserRouter } from "react-router-dom";
import { hiMockAxios } from "../../../../../app/mock-axios";
import ExportRepository from "../../../../../components/hi-admin/components/hi-management/Components/ExportRepository";
const crypto = require("crypto");

const App = ({ export_data }) => {
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
    preloadedState: { admin: export_data },
  });
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <ExportRepository />
      </Provider>
    </DndProvider>
  );
};

describe("Export Repository", () => {
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
    global.gc && global.gc();
  });

  test("ExportRepository component", async () => {
    render(
      <BrowserRouter>
        <App export_data={export_data} />
      </BrowserRouter>
    );

    const Button = screen.queryByTestId(/hi-admin-exportfiles-btn/i);
    fireEvent.click(Button);
    expect(screen.queryByTestId(/hi-admin-exportfiles-btn/i)).toBeTruthy();

    const form = screen.queryByTestId(/hi-admin-exportfiles-form/i);

    expect(form).toBeTruthy();
  });
});
