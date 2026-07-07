import "core-js/stable";
import { configureStore } from "@reduxjs/toolkit";
import { fireEvent, render, screen } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { hiMockAxios } from "../../../../../../app/mock-axios";
import { admin_data } from "./hi-add-user-form-with-drawer.mock";
import { HIAddUserFormWithDrawer } from "../../../../../../components/hi-admin/components/hi-userManagement/components";
const flushPromises = () => new Promise(setImmediate);
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
        <HIAddUserFormWithDrawer />
      </Provider>
    </DndProvider>
  );
};

describe("Rendering HIAddUserFormWithDrawer", () => {
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
    global.gc && global.gc();
  });

  test("HIAddProfileFormWithDrawer component", async () => {
     await flushPromises(
      render(<App admin_data={admin_data} />)
    );
    expect(screen.queryByTestId(/hi-add-user-form-with-drawer/i)).toBeTruthy();

    const usernameInput = screen.getByLabelText("Username");


    fireEvent.change(usernameInput, { target: { value: "user@name" } });
    fireEvent.submit(screen.getByTestId("hi-add-user-form"));

    fireEvent.change(usernameInput, { target: { value: "validUsername123" } });
    fireEvent.submit(screen.getByTestId("hi-add-user-form"));
    
      expect(screen.queryByText("Username can only use A-Z, a-z, 0-9, @, —,–,$, -, _, ', &, ., and can have spaces")).toBeNull();
    
  });
});



