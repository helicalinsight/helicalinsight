import { configureStore } from "@reduxjs/toolkit";
import { render, screen, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { BrowserRouter } from "react-router-dom";
import { login_data } from "./mocks/login-mock";
import { hiMockAxios } from "../../app/mock-axios";
import reducers from "../../redux";
import { HILogin } from "../../pages";

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
      datasource: login_data,
    },
  });
const props = {
 defaultAdmin :jest.fn() ,
 defaultUser : jest.fn(),
}
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <HILogin props={props}/>
      </Provider>
    </DndProvider>
  );
};

describe("HILogin", () => {
  test("Rendering HILogin", async () => {
    await waitFor(() =>
      render(
        <BrowserRouter>
          <App />
        </BrowserRouter>
      )
    );
    expect(screen.queryByTestId(/hi-login-main-page/i)).toBeFalsy();
  });
});
