import { configureStore } from "@reduxjs/toolkit";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import mocks from "../../../overview-module/admin-data-mock";
import { Provider } from "react-redux";
import reducers from "../../../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { hiMockAxios } from "../../../../../app/mock-axios";
import { HITempDirCard } from "../../../../../components/hi-admin/components/hi-overview/components";

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
  let apiRef = jest.fn();
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <HITempDirCard apiRef={apiRef} />
      </Provider>
    </DndProvider>
  );
};

describe("Rendering HITempDirCard", () => {
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

  test("HITempDirCard component", async () => {
    await waitFor(() =>
      render(<App admin_initial_view_state={admin_initial_view_state} />)
    );
    expect(screen.queryByTestId(/hi-temp-directory-title/i)).toBeTruthy();
    expect(screen.queryByTestId(/hi-temp-directory-title/i).innerHTML).toBe(
      "Temp Directory"
    );

    expect(screen.queryByTestId(/hi-temp-dir-card-total/i)).toBeTruthy();
    expect(screen.queryByTestId(/hi-temp-dir-card-total/i).innerHTML).toBe(
      "Total temporary file(s)"
    );
    expect(screen.queryByTestId(/hi-temp-dir-card-drawer-title/i)).toBeFalsy();
  

    expect(screen.queryByTestId(/hi-temp-directory-no-of-files/i)).toBeTruthy();
    expect(
      screen.queryByTestId(/hi-temp-directory-no-of-files/i).innerHTML
    ).toBe("1");


    const Dotbutton = screen.queryByTestId(/hi-temp-directory-button/i);
    fireEvent.click(Dotbutton);
    expect(screen.queryByTestId(/hi-temp-directory-button/i)).toBeTruthy();

    const refreshIcon = screen.queryByTestId(/hi-temp-dir-card-refresh-icon/i);
    fireEvent.click(refreshIcon);
    expect(screen.queryByTestId(/hi-temp-dir-card-refresh-icon/i)).toBeFalsy();
  });
});


