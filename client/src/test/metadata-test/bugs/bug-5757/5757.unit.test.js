import "core-js/stable";
import "regenerator-runtime/runtime";
import { configureStore } from "@reduxjs/toolkit";
import { render, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { BrowserRouter } from "react-router-dom";
import { HIMetadataPage } from "../../../../pages";
import { appActions } from "../../../../redux/actions";
import { editMetadataMockAxios } from "./5757.mock.axios";
const flushPromises = () => new Promise(setImmediate);
const App = ({ store }) => {
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <HIMetadataPage />
      </Provider>
    </DndProvider>
  );
};

describe("Edit Metadata test", () => {
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
    window.console.error = jest.fn();
    window.crypto.getRandomValues = (arr) => crypto.randomBytes(arr.length);
  });
  
  afterAll(() => {
    global.gc && global.gc()
  })
  
  test("Edit Metadata duplicate columns are displayed properly and columns contain category property", async () => {
    const store = configureStore({
      reducer: reducers,
      middleware: (getDefaultMiddleware) =>
        getDefaultMiddleware({
          thunk: {
            extraArgument: editMetadataMockAxios,
          },
          immutableCheck: false,
          serializableCheck: false,
        }),
    });
    await flushPromises(
      store.dispatch(
        appActions.setEditModeInfo({
          dir: "sibtain/Metadata_duplicate.metadata",
          file: "Metadata_duplicate.metadata",
          extension: "metadata",
        })
      )
    );

    await flushPromises(
      render(
        <BrowserRouter>
          <App store={store} />
        </BrowserRouter>
      )
    );
    const {dimdate} = store.getState().metadata.present.tables;
   
    expect(Object.keys(dimdate.columns)).toEqual(expect.arrayContaining(["modified_date", "modified_date_1"]));
    expect(dimdate.columnsFetched).toBe(true);
    expect(dimdate.columns.rating.category).toBe("column");
  });
});
