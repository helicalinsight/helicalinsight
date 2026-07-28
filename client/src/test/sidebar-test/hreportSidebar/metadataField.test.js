import { configureStore } from "@reduxjs/toolkit";
import { render, screen, waitFor } from "@testing-library/react";
import { field_data, props } from "./mocks/metadataField.mock";
import { Provider } from "react-redux";
import reducers from "../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { hiMockAxios } from "../../../app/mock-axios";
import MetadataField from "../../../components/hi-sidebar/hr-hreportSidebar/metadata-field";

const App = ({ useractions_initial_view_state }) => {
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
      hreport: field_data,
    },
  });

  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <MetadataField {...props} />
      </Provider>
    </DndProvider>
  );
};

describe("Rendering MetadataField", () => {
 
  test("MetadataField component", async () => {
    await waitFor(() => render(<App field_data={field_data} />));
    expect(screen.queryByTestId(/hi-metadata-field-dropdown/i)).toBeTruthy();
  });
});
