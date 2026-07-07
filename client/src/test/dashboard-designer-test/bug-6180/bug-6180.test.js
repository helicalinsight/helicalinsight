import "core-js/stable";
import "regenerator-runtime/runtime";
import "../../../utils/polyfill/url";
import { configureStore } from "@reduxjs/toolkit";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../redux";
import axios from "axios";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { HIDashboardDesigner } from "../../../components";
const crypto = require("crypto");

const App = ({ refresh }) => {
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
  });
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <HIDashboardDesigner refresh={refresh}/>
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
  
  test("rendering of designer", async () => {
    await waitFor(() => {
      const {rerender}=render(<App refresh={new Date()}/>);
      const originalComponent=<App refresh={new Date()}/>
      rerender(<App refresh={new Date()}/>)
      const updatedComponent=<App refresh={new Date()}/>
      expect(originalComponent).not.toBe(updatedComponent);
 
    });

   
  });
});
