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
import { DashboardDesigner } from "../../../pages";
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
  });
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <DashboardDesigner />
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
  
  test("rendering of task bar access keys", async () => {
    await waitFor(() => {
      render(<App />);
    });
    fireEvent.keyDown(document, { key: "Alt", code: "AltLeft" });
    fireEvent.keyUp(document, { key: "Alt", code: "AltLeft" });
    let arrayOfAccessKeys = document.getElementsByClassName("hi-shortcut-text");
    let arrayOfAccessKeyCodes = [];
    for(let i=0;i<arrayOfAccessKeys.length;i++){
      arrayOfAccessKeyCodes.push(arrayOfAccessKeys.item(i).innerHTML);
  }
    expect(arrayOfAccessKeyCodes).toEqual(["S","R","P","L", "Z", "Y", "I","F"]);
  });
});
