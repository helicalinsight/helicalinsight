import { configureStore } from "@reduxjs/toolkit";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import mocks from "../../admin-test/overview-module/admin-data-mock";
import { Provider } from "react-redux";
import reducers from "../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { hiMockAxios } from "../../../app/mock-axios";
import { HIUsers } from "../../../components/hi-users/hi-users";


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
        <HIUsers />
      </Provider>
    </DndProvider>
  );
};

describe("Rendering HIUsers", () => {
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

  test("HIUsers component", async () => {
    await waitFor(() =>
      render(<App admin_initial_view_state={admin_initial_view_state} />)
    );


    const card = screen.queryByTestId(/hi-users-card/i);
    expect(card).toBeTruthy(); 
 
  });
});
