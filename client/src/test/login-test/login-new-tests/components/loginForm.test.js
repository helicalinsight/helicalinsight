import { configureStore } from "@reduxjs/toolkit";
import { render, screen, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { BrowserRouter } from "react-router-dom";
import { login_form } from "../mocks/loginForm.mock";
import { hiMockAxios } from "../../../../app/mock-axios";
import reducers from "../../../../redux";
import HILoginForm from "../../../../components/hi-login/hi-loginForm/hi-loginForm"

const crypto = require("crypto");

const App = () => {
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
    preloadedState: {
      datasource: login_form,
    },
  });

  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <HILoginForm />
      </Provider>
    </DndProvider>
  );
};

describe("HILoginForm", () => {
  test("Rendering HILoginForm", async () => {
    await waitFor(() =>
      render(
        <BrowserRouter>
          <App />
        </BrowserRouter>
      )
    );
    expect(screen.queryByTestId(/hi-login-form/i)).toBeTruthy();
  });
});
