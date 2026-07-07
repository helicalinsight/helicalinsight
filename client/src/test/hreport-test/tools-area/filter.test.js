import { configureStore } from "@reduxjs/toolkit";
import { render, screen, fireEvent, act, waitFor } from "@testing-library/react";
import { intialFiltersState } from "./mock.data";
import { Provider } from "react-redux";
import reducers from "../../../redux";
import axios from "axios";
import Filters from "../../../components/hi-reports/hi-editing-area/components/filters/filters";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { openMetadata } from "../../../components/hi-reports/utils/base";
import "regenerator-runtime/runtime";
import { hiMockAxios } from "../../../app/mock-axios";
const crypto = require("crypto");

const App = ({ intial_hreport_state, reportId, appStore = null }) => {
  const store = appStore || configureStore({
    reducer: reducers,
    middleware: (getDefaultMiddleware) =>
      getDefaultMiddleware({
        thunk: {
          extraArgument: axios,
        },
        immutableCheck: false,
        serializableCheck: false,
      }),
    preloadedState: {
      hreport: {
        past: [],
        present: intial_hreport_state,
        future: []
      }
    },
  });
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <Filters reportId={reportId} />
      </Provider>
    </DndProvider>
  );
};

describe("Hreport filters", () => {
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
    
  test("jest example", async () => {
    expect(1 + 1).toBeTruthy();
  });
  test("Render text filter", async () => {
    render(<App intial_hreport_state={intialFiltersState} reportId={"test_123"} />);
    expect(screen.queryByTestId(/filters-advance-btn-test_123/i)).toBeTruthy();
    const mouseenter = new MouseEvent("mouseenter", { bubbles: false, cancelable: false });
    fireEvent(screen.queryByTestId(/filters-advance-btn-test_123/i), mouseenter);
    expect(screen.queryByTestId(/filters-search-icon-test_123/i)).toBeTruthy();
    fireEvent.click(screen.queryByTestId(/filters-search-icon-test_123/i));
    expect(screen.queryByTestId(/filters-search-input-test_123/i)).toBeTruthy();
    fireEvent.click(screen.queryByTestId(/filters-search-icon-test_123/i));
    expect(screen.queryByTestId(/filters-search-input-test_123-booking_platform/i)).toBeFalsy();
  });
  test("check and uncheck hide filter", async () => {
    render(<App intial_hreport_state={intialFiltersState} reportId={"test_123"} />);
    expect(screen.queryByTestId(/filters-advance-btn-test_123-booking_platform/i)).toBeTruthy();
    const mouseenter = new MouseEvent("mouseenter", { bubbles: false, cancelable: false });
    fireEvent(screen.queryByTestId(/filters-advance-btn-test_123-booking_platform/i), mouseenter);
    expect(screen.queryByTestId(/filters-advance-btn-test_123-booking_platform/i)).toBeTruthy();
    fireEvent.click(screen.queryByTestId(/filters-advance-btn-test_123-booking_platform/i));
    expect(screen.queryByTestId(/hide-filter-test_123-test_filter_1/i)).toBeTruthy();
    expect(screen.queryByText(/Hide/i)).toBeTruthy();
    let hideInput = screen
      .queryByTestId(/hide-filter-test_123-test_filter_1/i)
      .querySelector("input");
    expect(hideInput).toBeTruthy();
    expect(hideInput.checked).toBeFalsy();
    fireEvent.click(screen.queryByText(/Hide/i));
    expect(hideInput.checked).toBeTruthy();
    fireEvent.click(screen.queryByText(/Hide/i));
    expect(hideInput.checked).toBeFalsy();
    expect(screen.queryByTestId(/ignore-filter-test_123-test_filter_1/i)).toBeTruthy();
    expect(screen.queryByText(/Ignore/i)).toBeTruthy();
    let ignoreCheckbox = screen
      .queryByTestId(/ignore-filter-test_123-test_filter_1/i)
      .querySelector("input");
    expect(ignoreCheckbox).toBeTruthy();
    expect(ignoreCheckbox.checked).toBeFalsy();
    fireEvent.click(screen.queryByText(/Ignore/i));
    expect(ignoreCheckbox.checked).toBeTruthy();
    fireEvent.click(screen.queryByText(/Ignore/i));
    expect(ignoreCheckbox.checked).toBeFalsy();
    expect(screen.queryByTestId(/unique-filter-test_123-test_filter_1/i)).toBeTruthy();
    expect(screen.queryByText(/Unique/i)).toBeTruthy();
    let uniqueCheckbox = screen
      .queryByTestId(/unique-filter-test_123-test_filter_1/i)
      .querySelector("input");
    expect(uniqueCheckbox).toBeTruthy();
    expect(uniqueCheckbox.checked).toBeFalsy();
    fireEvent.click(screen.queryByText(/Unique/i));
    expect(uniqueCheckbox.checked).toBeTruthy();
    fireEvent.click(screen.queryByText(/Unique/i));
    expect(uniqueCheckbox.checked).toBeFalsy();
  });
  test("Render filter with null condition", async () => {
    intialFiltersState.reports[0].filters[0].condition = "IS_NULL";
    intialFiltersState.reports[0].filters[0].valuesList = [];
    render(<App intial_hreport_state={intialFiltersState} reportId={"test_123"} />);
    expect(screen.queryByTestId(/filters-value-input-test_123-test_filter_1/i)).toBeFalsy();
    // expect(
    //   screen.queryByTestId(/filters-value-input-test_123-test_filter_1/i).disabled
    // ).toBeTruthy();
  });
  test("Render filter with not null condition", async () => {
    intialFiltersState.reports[0].filters[0].condition = "IS_NOT_NULL";
    intialFiltersState.reports[0].filters[0].valuesList = [];
    render(<App intial_hreport_state={intialFiltersState} reportId={"test_123"} />);
    expect(screen.queryByTestId(/filters-value-input-test_123-test_filter_1/i)).toBeFalsy();
    // expect(
    //   screen.queryByTestId(/filters-value-input-test_123-test_filter_1/i).disabled
    // ).toBeTruthy();
  });
  test("Render filter with all value", async () => {
    intialFiltersState.reports[0].filters[0].values = ["_all_"];
    render(<App intial_hreport_state={intialFiltersState} reportId={"test_123"} />);
    expect(screen.queryByTestId(/filters-value-input-test_123-test_filter_1/i)).toBeFalsy();
    // expect(
    //   screen.queryByTestId(/filters-value-input-test_123-test_filter_1/i).disabled
    // ).toBeTruthy();
  });
  test("Remove filter when metadata changes", async () => {
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
        hreport: {
          past: [],
          present: intialFiltersState,
          future: []
        }
      },
    });
    const dispatch = store.dispatch
    render(<App reportId={"test_123"} appStore={store} />);
    expect(screen.queryByTestId(/filters-advance-btn-test_123/i)).toBeTruthy();
    await waitFor(()=>openMetadata({location:"naresh",metadataFileName:"Metadata_1.metadata"},dispatch))
    expect(screen.queryByTestId(/filters-search-input-test_123-booking_platform/i)).toBeFalsy();
  });
});
