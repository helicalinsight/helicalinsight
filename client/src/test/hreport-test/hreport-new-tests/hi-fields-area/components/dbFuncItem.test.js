import { configureStore } from "@reduxjs/toolkit";
import { render, screen, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { hiMockAxios } from "../../../../../app/mock-axios";
import { db_data, db_props } from "./mocks/editor.mocks";
import Param from "../../../../../components/hi-reports/hi-fields-area/db-functions/db-function-item";

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
    preloadedState: { hreport: db_data },
  });

  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <Param {...db_props} />
      </Provider>
    </DndProvider>
  );
};

describe("Rendering Param", () => {
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
    
  test("Param component", async () => {
    await waitFor(() => render(<App db_data={db_data} />));

    const comp = screen.queryByTestId(/hi-report-db-func-item/i);

    expect(comp).toBeTruthy();
  });
});
