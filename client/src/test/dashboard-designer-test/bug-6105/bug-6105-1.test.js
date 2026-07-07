import "core-js/stable";
import "regenerator-runtime/runtime";
import "../../../utils/polyfill/url";
import '@testing-library/jest-dom'
import { configureStore } from "@reduxjs/toolkit";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { hiMockAxios } from "../../../app/mock-axios";
import { DashboardDesigner } from "../../../pages";
import { openInDashboardCallback } from "../../../components/hi-dashboard-designer/utils/dashboard-requests";
import { HIDashboardDesigner } from "../../../components";
const crypto = require("crypto");
jest.setTimeout(30000);

const App = ({ store }) => {
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <DashboardDesigner  />
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
  });

  afterAll(() => {
    global.gc && global.gc()
  })
  
test("Designer undo and redo disabled", async () => {
  await waitFor(() => render(<App store={store} />));
  const undoButton = document.querySelectorAll("[data-testid='hi-designer-undo']");
  const undoButtonComputedStyle = window.getComputedStyle(undoButton[0]);
  expect(undoButtonComputedStyle.cursor).toBe('not-allowed');
  const redoButton = document.querySelectorAll("[data-testid='hi-designer-redo']");
  const redoButtonComputedStyle = window.getComputedStyle(redoButton[0]);
  expect(redoButtonComputedStyle.cursor).toBe('not-allowed');; 
});




});



