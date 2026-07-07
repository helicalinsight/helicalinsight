import { configureStore } from "@reduxjs/toolkit";
import { render, screen, fireEvent, act, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import { HTML5Backend } from "react-dnd-html5-backend";
import { DndProvider } from "react-dnd";
import { HIManagement } from "../../../components/hi-admin";
import reducers from "../../../redux";
import axios from "axios";
import { BrowserRouter } from "react-router-dom";
const crypto = require("crypto");

const admin_initial_view_state = JSON.parse(
  `{
    "managementData": null,
    "managementStaticData": null,
    "managementAdvancedData": null
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
        <HIManagement />
      </Provider>
    </DndProvider>
  );
};

describe("System Details", () => {
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
  
  test("Mangement component", async () => {
    render(
      <BrowserRouter>
        <App admin_initial_view_state={admin_initial_view_state} />
      </BrowserRouter>
    );
    expect(screen.queryByTestId(/management-menu/i)).toBeTruthy();
  });
});
