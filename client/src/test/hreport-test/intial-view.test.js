import '../utils/mockJsdom'
import { configureStore } from "@reduxjs/toolkit";
import { render, screen, fireEvent, waitFor, queryByAttribute } from "@testing-library/react";
import { hreport_intial_view_state } from "./hreport.request.mock";
import { intailStateForTabs } from "./utils/mock.data";
import { Provider } from "react-redux";
import reducers from "../../redux";
import axios from "axios";
import { HelicalReports } from "../../pages/helical-reports-page";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";

const App = ({ intial_hreport_state }) => {
  const store = configureStore({
    reducer: reducers,
    middleware: (getDefaultMiddleware) =>
      getDefaultMiddleware({
        thunk: {
          extraArgument: axios,
        },
        immutableCheck: false,
        serializableCheck: false,
      }),
    preloadedState: { hreport: intial_hreport_state },
  });
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <HelicalReports testMode="viz" />
      </Provider>
    </DndProvider>
  );
};

describe("Rendering Intial view", () => {
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
    window.URL.createObjectURL = jest.fn()
    window.HTMLElement.prototype.scrollBy = jest.fn();
  });

  afterAll(() => {
    global.gc && global.gc()
  })
    
  test("jest example", async () => {
    expect(1 + 1).toBeTruthy();
  });
  // test("intial page content", async () => {
  //   await waitFor(() => render(<App intial_hreport_state={hreport_intial_view_state} />));
  //   expect(screen.queryByText(/New1/i)).toBeTruthy();
  //   expect(screen.queryByTestId(/connect-to-metadata/i)).toBeTruthy();
  //   expect(screen.queryByPlaceholderText(/search tables or columns/i)).toBeTruthy();
  //   expect(screen.queryByText(/Visualization/i)).toBeTruthy();
  //   expect(screen.queryByText(/Table/i)).toBeTruthy();
  //   expect(screen.queryByText(/Filters/i)).toBeTruthy();
  //   expect(screen.queryByText(/Editor/i)).toBeTruthy();
  //   expect(screen.queryByText(/Sql/i)).toBeTruthy();
  //   expect(screen.queryByText(/Settings/i)).toBeTruthy();
  //   expect(screen.queryByText(/Marks/i)).toBeTruthy();
  //   expect(screen.queryByTestId(/columns-heading/i)).toBeTruthy();
  //   expect(screen.queryByTestId(/rows-heading/i)).toBeTruthy();
  //   await waitFor(() => fireEvent.click(screen.queryByText(/Settings/i))); // opening settings tab
  //   expect(screen.queryByText(/Sample Size/i)).toBeTruthy();
  //   expect(screen.queryByRole(/spinbutton/i).value).toEqual("1000"); // checking sample size
  //   await waitFor(() =>
  //     fireEvent.change(screen.queryByRole(/spinbutton/i), { target: { value: 99 } })
  //   ); // changing sample size
  //   expect(screen.queryByRole(/spinbutton/i).value).toEqual("99"); // checking sample size
  // });
  test("rendering tabs", async () => {
    const store = configureStore({
      reducer: reducers,
      middleware: (getDefaultMiddleware) =>
        getDefaultMiddleware({
          thunk: { extraArgument: axios },
          immutableCheck: false,
          serializableCheck: false,
        }),
      preloadedState: { hreport: intailStateForTabs },
    });
    const App = () => {
      return (
        <DndProvider backend={HTML5Backend}>
          <Provider store={store}>
            <HelicalReports />
          </Provider>
        </DndProvider>
      );
    };
    const container = await waitFor(() => render(<App />));
    let addBtn = document.querySelector(".ant-tabs-nav-add");
    fireEvent.click(addBtn);
    expect(store.getState().hreport.present.reports.length).toEqual(2);
    expect(store.getState().hreport.present.reports[1].id).toEqual(store.getState().hreport.present.activeReportId);
    fireEvent.click(screen.queryByText(/Untitled 1/i));
    expect(store.getState().hreport.present.reports[0].id).toEqual(store.getState().hreport.present.activeReportId);
    // fireEvent.click(screen.queryByText(/New1/i).nextElementSibling)
    // expect(store.getState().hreport.present.reports.length).toEqual(1)
    // expect(store.getState().hreport.present.reports[0].id).toEqual(store.getState().hreport.present.activeReportId)
  });
});
