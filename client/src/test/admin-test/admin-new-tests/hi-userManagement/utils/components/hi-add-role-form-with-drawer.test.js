import "core-js/stable";
import { configureStore } from "@reduxjs/toolkit";
import { fireEvent, render, screen } from "@testing-library/react";
import '@testing-library/jest-dom/extend-expect';
import { Provider } from "react-redux";
import reducers from "../../../../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { hiMockAxios } from "../../../../../../app/mock-axios";
import { admin_data } from "./hi-add-roleform-with-drawer.mock";
import { HIAddRoleFormWithDrawer } from "../../../../../../components/hi-admin/components/hi-userManagement/components";
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
        <HIAddRoleFormWithDrawer />
      </Provider>
    </DndProvider>
  );
};

describe("Rendering HIAddRoleFormWithDrawer", () => {
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

    expect(
      screen.queryByTestId("hi-add-role-form-with-drawer")
    ).toBeTruthy();

    const roleNameInput = screen.getByLabelText("Name");
    fireEvent.change(roleNameInput, { target: { value: "Invalid Role Name$" } });
    fireEvent.click(screen.getByTestId("hi-add-role-submit")); 


  });
});
