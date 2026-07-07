import { configureStore } from "@reduxjs/toolkit";
import { render, screen, waitFor } from "@testing-library/react";
import mocks from "./admin-data-mock";
import { Provider } from "react-redux";
import reducers from "../../../redux";
import axios from "axios";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { HIAddUserFormWithDrawer } from "../../../components/hi-admin/components/hi-userManagement/components";

const { admin_initial_view_state } = mocks;

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
    // <div>hello</div>
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <HIAddUserFormWithDrawer />
      </Provider>
    </DndProvider>
  );
};

describe("Rendering Intial view", () => {
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
  });

  afterAll(() => {
    global.gc && global.gc()
  })
  
  test("Add User Drawer component", async () => {
    await waitFor(() =>
      render(<App />)
    );
    expect(screen.queryByTestId(/hi-add-user-form/i)).toBeFalsy();
  });
});
