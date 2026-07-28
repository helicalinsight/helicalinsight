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
import { editMetadataMockAxios } from "./5822.mock.axios";
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
  test("metadata having view, tables are loaded in metadata panel", async () => {
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
          dir: "Sibtain/Metadata_duplicate.metadata",
          file: "view_5822.metadata",
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
    const tables = store.getState().metadata.present.tables;
    expect(tables["View 2"].type).toEqual("view");
    expect(tables["View 2"].hasOwnProperty('uuid')).toBe(true);
  });
});
