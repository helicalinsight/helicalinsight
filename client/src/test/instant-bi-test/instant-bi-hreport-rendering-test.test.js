import "core-js/stable";
import "regenerator-runtime/runtime";
import "../../utils/polyfill/url";
import { configureStore } from "@reduxjs/toolkit";
import { render, screen, waitFor } from "@testing-library/react";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { Provider } from "react-redux";
import { hiMockAxios } from "../../app/mock-axios";
import { HelicalReports } from "../../pages";
import reducers from "../../redux";
import { derivedFormDataConvertorToReportProps } from "../../components/hi-instant-bi/utils/instant-bi-utilities";
import axios from "axios";

const crypto = require("crypto");

const App = ({ store, reportProps }) => {
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        {/* <HIInstantBI /> */}
        <HelicalReports {...reportProps} />
      </Provider>
    </DndProvider>
  );
};

describe("Hreport visualisation test in instant bi", () => {
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
    window.createObjectURL = jest.fn();
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
  test("Hreport rendering test", async () => {
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
    });
    const dispatch = store.dispatch;
    const getState = store.getState;
    const reportProps = derivedFormDataConvertorToReportProps({
      derivedFormData: {
        visualization: "table",
        limit: "",
        from: [],
        groupBy: ["month"],
        aggregate: [
          {
            aggregateColumnName: "travels",
            aggregateFunction: "Total",
          },
        ],
        columns: [],
        rows: [{ table: "travel_details", column: "booking_platform" }],
        // where: [
        //   {
        //     table: "travel_details",
        //     column: "booking_platform",
        //     values: "Agent",
        //     condition: "",
        //   },
        // ],
      },
      metadata: {
        metadataFileName: "Metadata_1.metadata",
        location: "TestFold",
      },
    });
    expect(reportProps).toStrictEqual({
      visualisationType: "Table",
      mode: "instant-bi",
      columns: [],
      rows: [{ table: "travel_details", column: "booking_platform" }],
      filters: [
        // {
        //   table: "travel_details",
        //   column: "booking_platform",
        //   values: "Agent",
        //   condition: "",
        // },
      ],
      metadata: {
        metadataFileName: "Metadata_1.metadata",
        location: "TestFold",
      },
    });
    const appProps = { store, reportProps };
    await waitFor(() => render(<App {...appProps} />));

    expect(screen.queryByTestId(/table-value-Agent-0/i)).toBeTruthy();
  });
});
