
import { configureStore } from "@reduxjs/toolkit";
import {render,screen,fireEvent} from "@testing-library/react";
import { Provider } from "react-redux";
import { HTML5Backend } from "react-dnd-html5-backend";
import { DndProvider } from "react-dnd";
import ImportFiles from "../../../../../components/hi-admin/components/hi-management/Components/ImportFiles";
import reducers from "../../../../../redux";
import { admin_data } from "./hi-managementmock-comp";
import { BrowserRouter } from "react-router-dom";
import { hiMockAxios } from "../../../../../app/mock-axios";
const crypto = require("crypto");

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
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <ImportFiles />
      </Provider>
    </DndProvider>
  );
};

describe("ImportFiles", () => {
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

  test("ImportFiles component", async () => {
    render(
      <BrowserRouter>
        <App admin_data={admin_data} />
      </BrowserRouter>
    );

    expect(screen.queryByTestId(/hi-admin-importfiles-checkbox/i)).toBeTruthy();
    expect(screen.queryByTestId(/hi-admin-importfiles-radiobtn/i)).toBeTruthy();

    const Button = screen.queryByTestId(/hi-admin-importfiles-btn/i);
    fireEvent.click(Button);
    expect(screen.queryByTestId(/hi-admin-importfiles-btn/i)).toBeTruthy();

    expect(screen.queryByTestId(/hi-admin-importfiles-cancel/i)).toBeFalsy();

    const form = screen.queryByTestId(/hi-admin-importfiles-form/i);

    expect(form).toBeTruthy();
    
    const browseButton = screen.queryByTestId(/hi-admin-importfiles-folderPath/i);

    expect(browseButton).toBeTruthy();
  });
});
