import { configureStore } from "@reduxjs/toolkit";
import { render, screen, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../../../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { hiMockAxios } from "../../../../../../../app/mock-axios";
import { relative_list,rangeSelection_props } from "./mocks/relative-list.mocks";
import RangeSelection from "../../../../../../../components/hi-reports/hi-editing-area/components/filters/range-selection";

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
    preloadedState: { hreport: relative_list },
  });
  let handleOnChange = jest.fn();
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <RangeSelection {...rangeSelection_props} onChange={handleOnChange} />
      </Provider>
    </DndProvider>
  );
};

describe("Rendering RangeSelection", () => {
  
  test("RangeSelection component", async () => {
    await waitFor(() => render(<App relative_list={relative_list} />));

    const drawer = screen.queryByTestId(/hi-report-range-selection/i);

    expect(drawer).toBeTruthy();
  });
});
