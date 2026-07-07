import { configureStore } from "@reduxjs/toolkit";
import { render, screen, fireEvent, act, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import { HTML5Backend } from "react-dnd-html5-backend";
import { DndProvider } from "react-dnd";
import { DiceStorage } from "../../../../../components/hi-admin/components/hi-management/Components/dice-storage/DiceStorage";
import reducers from "../../../../../redux";
import axios from "axios";
import { DiceStorsge } from "./hi-ds-mocks";
import { BrowserRouter } from "react-router-dom";
import { hiMockAxios } from "../../../../../app/mock-axios";
const crypto = require("crypto");


const App = ({ DiceStorsge }) => {
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
    preloadedState: { admin: DiceStorsge },
  });
  let apiRef = jest.fn();
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <DiceStorage apiRef ={apiRef} />
      </Provider>
    </DndProvider>
  );
};

describe("DiceStorage", () => {
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

  test("DiceStorage component", async () => {
    render(
      <BrowserRouter>
        <App DiceStorsge={DiceStorsge} />
      </BrowserRouter>
    );
    expect(screen.queryByTestId(/dice-storage-text/i)).toBeFalsy();
    const tab = screen.queryByTestId(/dice-storage-tabs/i);
    expect(tab).toBeTruthy();

    const refreshIcon = screen.queryByTestId(/dice-storage-refresh/i);
      fireEvent.click(refreshIcon);
      expect(screen.queryByTestId(/dice-storage-refresh/i)).toBeTruthy();

  
  }); 
});
