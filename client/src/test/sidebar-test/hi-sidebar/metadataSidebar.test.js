import { configureStore } from "@reduxjs/toolkit";
import { render, screen, waitFor } from "@testing-library/react";
import { sidebar_data } from "./mocks/metadataSidebar.mock";
import { Provider } from "react-redux";
import reducers from "../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { hiMockAxios } from "../../../app/mock-axios";
import { HIMetadatSidebar } from "../../../components";

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
    preloadedState: { admin: sidebar_data },
  });
 
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <HIMetadatSidebar  />
      </Provider>
    </DndProvider>
  );
};

describe("Rendering HIMetadatSidebar", () => {
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
  
  test("HIMetadatSidebar component", async () => {
    await waitFor(() => render(<App sidebar_data={sidebar_data} />));
    expect(screen.queryByTestId(/hi-metadata-sidebar-table/i)).toBeTruthy();
   
    expect(screen.queryByTestId(/hi-metadata-sidebar-row/i)).toBeTruthy();

    expect(screen.queryByTestId(/metadata-sidebar-search/i)).toBeTruthy();
  });
});
