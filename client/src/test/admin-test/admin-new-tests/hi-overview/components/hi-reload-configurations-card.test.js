import { configureStore } from "@reduxjs/toolkit";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import mocks from "../../../overview-module/admin-data-mock";
import { Provider } from "react-redux";
import reducers from "../../../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { HIReloadConfigurationCard } from "../../../../../components/hi-admin/components/hi-overview/components";
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
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <HIReloadConfigurationCard />
      </Provider>
    </DndProvider>
  );
};

describe("Rendering HIReloadConfigurationCard", () => {
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

  test("HIReloadConfigurationCard component", async () => {
    await waitFor(() =>
      render(<App admin_initial_view_state={admin_initial_view_state} />)
    );
    expect(screen.queryByTestId(/hi-reload-config-card-textOne/i)).toBeTruthy();
    expect(screen.queryByTestId(/hi-reload-config-card-textOne/i).innerHTML).toBe(
      "Application"
    );

    expect(screen.queryByTestId(/hi-reload-config-card-textTwo/i)).toBeTruthy();
    expect(screen.queryByTestId(/hi-reload-config-card-textTwo/i).innerHTML).toBe(
      "Validation"
    );

    expect(screen.queryByTestId(/hi-reload-config-card-textThree/i)).toBeTruthy();
    expect(screen.queryByTestId(/hi-reload-config-card-textThree/i).innerHTML).toBe(
      "Cache"
    );

    expect(screen.queryByTestId(/hi-reload-config-card-title/i)).toBeTruthy();

    const buttonOne = screen.queryByTestId(/hi-reload-config-card-buttonOne/i);
    fireEvent.click(buttonOne);
    expect(screen.queryByTestId(/hi-reload-config-card-buttonOne/i)).toBeTruthy();
    
    const buttonTwo = screen.queryByTestId(/hi-reload-config-card-buttonTwo/i);
    fireEvent.click(buttonTwo);
    expect(screen.queryByTestId(/hi-reload-config-card-buttonTwo/i)).toBeTruthy();

    const buttonThree = screen.queryByTestId(/hi-reload-config-card-buttonThree/i);
    fireEvent.click(buttonThree);
    expect(screen.queryByTestId(/hi-reload-config-card-buttonThree/i)).toBeTruthy();
  });
});
