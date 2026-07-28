import { configureStore } from "@reduxjs/toolkit";
import { render, screen, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../../../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { hiMockAxios } from "../../../../../../../app/mock-axios";
import { metadata_data } from "./changeDataSource.mock";
import ChangeDataSource from "../../../../../../../components/hi-metadata/components/editor/info/changeDataSource";

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
    preloadedState: { metadata: metadata_data },
  });

  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <ChangeDataSource />
      </Provider>
    </DndProvider>
  );
};

describe("Rendering ChangeDataSource", () => {
  
  test("ChangeDataSource component", async () => {
    await waitFor(() => render(<App />));
    const row = screen.queryByTestId(/hi-metadata-changeDataSource/i);

    expect(row).toBeTruthy();
  });
});
