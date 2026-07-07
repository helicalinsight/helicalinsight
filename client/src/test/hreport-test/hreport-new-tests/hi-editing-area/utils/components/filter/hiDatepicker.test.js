import { configureStore } from "@reduxjs/toolkit";
import { render, screen, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../../../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { hiMockAxios } from "../../../../../../../app/mock-axios";
import { hiDate_picker,props } from "./mocks/hiDatePicker.mocks";
import { HiDatePicker } from "../../../../../../../components/hi-reports/hi-editing-area/components/filters/hi-datepicker";
import { HiDateRangePicker } from "../../../../../../../components/hi-reports/hi-editing-area/components/filters/hi-datepicker";

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
    preloadedState: { hreport: hiDate_picker },
  });
let handleOnChange = jest.fn()
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <HiDatePicker {...props} 
        onChange={handleOnChange}
        />
      </Provider>
    </DndProvider>
  );
};

describe("Rendering HiDatePicker", () => {
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
  
  test("HiDatePicker component", async () => {
    await waitFor(() => render(<App hiDate_picker={hiDate_picker} />));

    const comp = screen.queryByTestId(/Hi-report-hidate-picker/i);

    expect(comp).toBeTruthy();
  });
});
