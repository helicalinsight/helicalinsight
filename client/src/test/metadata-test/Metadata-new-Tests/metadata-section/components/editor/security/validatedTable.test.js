import { configureStore } from "@reduxjs/toolkit";
import { render, screen, waitFor, fireEvent } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../../../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { hiMockAxios } from "../../../../../../../app/mock-axios";
import { validated_table } from "./validatedTable.mock";
import ValidatedTable from "../../../../../../../components/hi-metadata/components/editor/security/validatedTable/validatedTable";

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
    preloadedState: { metadata: validated_table },
  });
  const props = {
    setIsApplyDisabled: jest.fn(),
    setFormData: jest.fn(),
    setIsInfoShow: jest.fn(),
    setShowContent: jest.fn(),
    securityKeysChecked: ["61d124bb-0674-44b6-951a-06c35be39a93_74px3"],
    addOneMoreSecurity: false,
    selectedTableOrColumnKey: {
      category: "column",
      dbId: "74px3",
      key: "CACHE_FILE_SIZE",
    },
  };
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <ValidatedTable props={props} />
      </Provider>
    </DndProvider>
  );
};

describe("Rendering ValidatedTable component", () => {
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
  
  test("ValidatedTable component", async () => {
    await waitFor(() => render(<App />));
    const form = screen.queryByTestId(/hi-metadata-validated-table-form/i);

    expect(form).toBeTruthy();

    const Button = screen.queryByTestId(/hi-metadata-validated-table-btn/i);

    fireEvent.click(Button);
    expect(
      screen.queryByTestId(/hi-metadata-validated-table-btn/i)
    ).toBeTruthy();
  });
});
