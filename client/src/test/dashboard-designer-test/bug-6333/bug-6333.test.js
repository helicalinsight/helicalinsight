import "core-js/stable";
import "regenerator-runtime/runtime";
import "../../../utils/polyfill/url";
import { configureStore } from "@reduxjs/toolkit";
import {  render, screen, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../redux";
import axios from "axios";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { SidebarFooter } from "../../../components";
import { bug6333adminReduxData, bug6333appreduxData, bug6333fileBrowserReduxData, bug6333reduxData } from "./bug-6333-mock-data";
const crypto = require("crypto");
const mockFunction=jest.fn();

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
      preloadedState:{app:bug6333appreduxData,admin:bug6333adminReduxData,fileBrowser:bug6333fileBrowserReduxData}
  });
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
      <SidebarFooter
                        fileBrowserOnClick={() => {
                        }}
                        onGlobalSearch={mockFunction}
                        color="white"
                      />
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

  
  test("hi-sidebar-footer-search element testing", async () => {
    await waitFor(() => {
      render(<App />);
    });
    expect(screen.queryByTestId("hi-sidebar-footer-search")).toBeTruthy();
    // screen.queryByTestId("hi-sidebar-footer-search").focus()
    // expect(mockFunction).toHaveBeenCalled();

});
});

