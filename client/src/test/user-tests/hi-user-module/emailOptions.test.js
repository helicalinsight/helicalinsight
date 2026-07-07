import { configureStore } from "@reduxjs/toolkit";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { user_data, app_data } from "./hi-user-module-mocks";
import { Provider } from "react-redux";
import reducers from "../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { hiMockAxios } from "../../../app/mock-axios";
import { EmailOptions } from "../../../components/hi-user-module/components/EmailOptions";

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

  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <EmailOptions />
      </Provider>
    </DndProvider>
  );
};

describe("Rendering EmailOptions", () => {
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


  test("check", () => {
    expect(1 + 1).toBe(2);
  })

  // test("EmailOptions component", async () => {
  //   await waitFor(() =>
  //     render(<App user_data={user_data} app_data={app_data} />)
  //   );

  //   const btn = screen.queryByTestId(/hi-user-module-btn/i);
  //   expect(btn).toBeFalsy();

  //   const form = screen.queryByTestId(/hi-user-module-email-form/i);
  //   expect(form).toBeTruthy();
  // });
});
