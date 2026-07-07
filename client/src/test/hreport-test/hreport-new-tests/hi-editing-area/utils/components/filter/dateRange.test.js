import { configureStore } from "@reduxjs/toolkit";
import { render, screen, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../../../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { hiMockAxios } from "../../../../../../../app/mock-axios";
import { date_range , props } from "./mocks/data-range-mocks";
import DateRange from "../../../../../../../components/hi-reports/hi-editing-area/components/filters/date-range";

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
    preloadedState: { hreport: date_range },
  });
let handleOnChange = jest.fn()
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <DateRange {...props} 
        onChange={handleOnChange}
        />
      </Provider>
    </DndProvider>
  );
};

describe("Rendering DateRange", () => {
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
  
  test("DateRange component", async () => {
    await waitFor(() => render(<App date_range={date_range} />));

    const comp = screen.queryByTestId(/Hi-report-date-range/i);

    expect(comp).toBeFalsy();
  });
});
