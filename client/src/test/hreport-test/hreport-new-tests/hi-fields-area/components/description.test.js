import { configureStore } from "@reduxjs/toolkit";
import { render, screen, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { hiMockAxios } from "../../../../../app/mock-axios";
import { editor_data, props_data } from "./mocks/editor.mocks";
import DescriptionComponent from "../../../../../components/hi-reports/hi-fields-area/db-functions/descrition";

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
    preloadedState: { hreport: editor_data },
  });

  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <DescriptionComponent {...props_data} />
      </Provider>
    </DndProvider>
  );
};

describe("Rendering DescriptionComponent", () => {
 
  test("DescriptionComponent component", async () => {
    await waitFor(() => render(<App editor_data={editor_data} />));

    const row = screen.queryByTestId(/hi-reports-description-row/i);

    expect(row).toBeTruthy();
  });
});
