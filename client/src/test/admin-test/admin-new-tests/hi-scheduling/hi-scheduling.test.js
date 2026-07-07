import { configureStore } from "@reduxjs/toolkit";
import { render, screen, fireEvent } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { HIScheduling } from "../../../../components/hi-admin";
import { admin_data } from "../../scheduling-test/hi-schedule-mocks";
import { hiMockAxios } from "../../../../app/mock-axios";
const crypto = require("crypto");

const App = ({ admin_data }) => {
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
    preloadedState: { admin: admin_data },
  });
  let apiRef = jest.fn();
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <HIScheduling apiRef = {apiRef} />
      </Provider>
    </DndProvider>
  );
};

describe("HIScheduling component", () => {
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
    window.crypto = {};
    window.crypto.getRandomValues = (arr) => crypto.randomBytes(arr.length);
  });

  afterAll(() => {
    global.gc && global.gc()
  })

  test("HIScheduling", async () => {
    render(<App admin_data={admin_data} />);
    const Table = screen.queryByTestId(/scheduling-table/i);
    expect(Table).toBeTruthy();

     expect(screen.queryByTestId(/hi-scheduling-text/i)).toBeTruthy();
     expect(screen.queryByTestId(/hi-scheduling-text/i).innerHTML).toBe("Schedule");
 
     const refreshIcon = screen.queryByTestId(/refresh/i); 
     fireEvent.click(refreshIcon);
     expect(screen.queryByTestId(/refresh/i)).toBeTruthy();

     const shutdown = screen.queryByTestId(/shutdown/i); 
     fireEvent.click(shutdown);

     expect(screen.queryByTestId(/shutdown/i)).toBeTruthy();
 
    const start = screen.queryByTestId(/start/i);
    fireEvent.click(start);
    expect(screen.queryByTestId(/start/i)).toBeTruthy();

    const resume = screen.queryByTestId(/resume-all/i);
    fireEvent.click(resume);
    expect(screen.queryByTestId(/resume-all/i)).toBeTruthy();

    const pause = screen.queryByTestId(/pause-all/i);
    fireEvent.click(pause);
    expect(screen.queryByTestId(/pause-all/i)).toBeTruthy();


});
});