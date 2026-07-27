import "regenerator-runtime/runtime";
import { configureStore } from "@reduxjs/toolkit";
import { render, waitFor} from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { BrowserRouter } from "react-router-dom";
import { HIMetadataPage } from "../../../../pages";
import { appActions } from "../../../../redux/actions";
import { editMetadataMockAxios } from "./5798.mock.axios";
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
  test("In sidebar: columns are displayed when clicked on + icon of datasource", async () => {
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
    await waitFor(() =>
      store.dispatch(
        appActions.setEditModeInfo({
          dir: "sibtain/Metadata_duplicate.metadata",
          file: "Metadata_duplicate.metadata",
          extension: "metadata",
        })
      )
    );

    await waitFor(() =>
      render(
        <BrowserRouter>
          <App store={store} />
        </BrowserRouter>
      )
    );

    const postgress = store.getState().metadata.present.datasourceListToRender[2].children[0].children;
    expect(postgress).toEqual([]);
  });
});
