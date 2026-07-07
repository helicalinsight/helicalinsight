import { configureStore } from "@reduxjs/toolkit";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import mocks from "../../../overview-module/admin-data-mock";
import { Provider } from "react-redux";
import reducers from "../../../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { HIDataSourcesCard } from "../../../../../components/hi-admin/components/hi-overview/components";
import { hiMockAxios } from "../../../../../app/mock-axios";

const { admin_initial_view_state } = mocks;

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
      preloadedState: { admin: admin_initial_view_state },
  });
  let apiRef = jest.fn()
  return (
   
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <HIDataSourcesCard apiRef={apiRef} />
      </Provider>
    </DndProvider>
  );
};

describe("Rendering HIDataSourcesCard", () => {
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
  test("HIDataSourcesCard component", async () => {
    await waitFor(() =>
      render(<App admin_initial_view_state={admin_initial_view_state} />)
    );
    expect(screen.queryByTestId(/hi-data-sources-title/i)).toBeTruthy();
    expect(screen.queryByTestId(/hi-data-sources-title/i).innerHTML).toBe(
      "Data sources cached"
    );
    expect(screen.queryByTestId(/hi-data-sources-size/i)).toBeTruthy();
    expect(screen.queryByTestId(/hi-data-sources-size/i).innerHTML).toBe("1");

    const DotButton = screen.queryByTestId(/hi-data-sources-card-button/i);
   
    fireEvent.click(DotButton)
    expect(screen.queryByTestId(/hi-data-sources-card-button/i)).toBeTruthy();

    const refreshIcon = screen.queryByTestId(/hi-data-sources-card-refresh-icon/i);
   
    fireEvent.click(refreshIcon)
    expect(screen.queryByTestId(/hi-data-sources-card-refresh-icon/i)).toBeFalsy();

    // const delButton = screen.queryByTestId(/hi-data-sources-card-del-button/i);
   
    // fireEvent.click(delButton)
    expect(screen.queryByTestId(/hi-data-sources-card-drawer/i)).toBeFalsy();
  });
});
