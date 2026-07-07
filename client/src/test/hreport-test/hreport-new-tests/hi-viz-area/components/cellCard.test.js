import { configureStore } from "@reduxjs/toolkit";
import { render, screen, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { hiMockAxios } from "../../../../../app/mock-axios";
import { hreport_data } from "./mock-data/hreport-mocks";
import CellCard from "../../../../../components/hi-reports/hi-viz-area/cell-card";

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
    preloadedState: { hreport: hreport_data },
  });



  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <CellCard  />
      </Provider>
    </DndProvider>
  );
};

describe("Rendering CellCard", () => {
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
    
  test("CellCard component", async () => {
    await waitFor(() => render(<App hreport_data={hreport_data} />));
    const comp = screen.queryByTestId(/hi-report-cell-card/i);

    expect(comp).toBeFalsy();
  });
});
