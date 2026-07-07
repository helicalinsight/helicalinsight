import "core-js/stable";
import { configureStore } from "@reduxjs/toolkit";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { _user_data_, _app_data_ } from "./hi-user-module-mocks";
import { Provider } from "react-redux";
import reducers from "../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { hiMockAxios } from "../../../app/mock-axios";
import { ViewerDrawerSchedule } from "../../../components/hi-user-module/components/ViewerDrawerSchedule";
const flushPromises = () => new Promise(setImmediate);
const App = ({ _user_data_, _app_data_ }) => {
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
    preloadedState: { admin: _user_data_, app: _app_data_ },
  });
  let parametersReview = { adhocFormData: "" };
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <ViewerDrawerSchedule parametersReview={parametersReview} />
      </Provider>
    </DndProvider>
  );
};

describe("Rendering ViewerDrawerSchedule", () => {
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

  test("ViewerDrawerSchedule component", async () => {
    await flushPromises(
      render(<App _user_data_={_user_data_} _app_data_={_app_data_} />)
    );

    const drawer = screen.queryByTestId(/hi-viewer-drawer-schedule/i);
    expect(drawer).toBeTruthy();

    const cancel = screen.queryByTestId(/hi-viewer-drawer-cancelbtn/i);
    fireEvent.click(cancel);
    expect(screen.queryByTestId(/hi-viewer-drawer-cancelbtn/i)).toBeFalsy();

    const nextbtn = screen.queryByTestId(/hi-viewer-drawer-nextbtn/i);
    expect(nextbtn).toBeFalsy();

    const previousbtn = screen.queryByTestId(/hi-viewer-drawer-previousbtn/i);
    expect(previousbtn).toBeFalsy();
  });
});
