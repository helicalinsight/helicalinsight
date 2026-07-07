import "core-js/stable";
import { configureStore } from "@reduxjs/toolkit";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { user_data, app_data } from "./hi-user-module-mocks";
import { Provider } from "react-redux";
import reducers from "../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { hiMockAxios } from "../../../app/mock-axios";
import { ViewerDrawerEmail } from "../../../components/hi-user-module/components/ViewerDrawerEmail";
const flushPromises = () => new Promise(setImmediate);
const App = ({ user_data, app_data }) => {
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
    preloadedState: { admin: user_data, app: app_data },
  });
  let parametersReview = { adhocFormData: "" };
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <ViewerDrawerEmail parametersReview={parametersReview} />
      </Provider>
    </DndProvider>
  );
};

describe("Rendering ViewerDrawerEmail", () => {
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

  test("ViewerDrawerEmail component", async () => {
    await flushPromises(
      render(<App user_data={user_data} app_data={app_data} />)
    );

    const drawer = screen.queryByTestId(/hi-viewer-drawer-email/i);
    expect(drawer).toBeTruthy();

    const Button = screen.queryByTestId(/hi-viewer-drawer-btn/i);
    fireEvent.click(Button);
    expect(screen.queryByTestId(/hi-viewer-drawer-btn/i)).toBeTruthy();

    const cancel = screen.queryByTestId(/hi-viewer-drawer-cancel/i);
    fireEvent.click(cancel);
    expect(screen.queryByTestId(/hi-viewer-drawer-cancel/i)).toBeTruthy();
  });
});
