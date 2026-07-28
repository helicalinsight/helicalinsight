import { configureStore } from "@reduxjs/toolkit";
import { render, waitFor } from "@testing-library/react";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { Provider } from "react-redux";
import { BrowserRouter } from "react-router-dom";
import "regenerator-runtime/runtime";
import { HIMetadataPage } from "../../../../../pages";
import reducers from "../../../../../redux";
// import { appActions } from "../../../redux/actions";
import { appActions } from "../../../../../redux/actions";
import { mockAxios } from "./mock-axios";
// import { derby_pg_mockAxios } from "./derby_pg.mock-axios";
const crypto = require("crypto");

// const intialState = {
//     ...initialStates.metadataInitialState, editMdFromHomeInfo: {
//         dir: 'Gagan/Metadata_1_68.metadata',
//         file: 'Metadata_1_68.metadata'
//     },
//     mode: 'edit',
//     timeStamp: 1667204866784
// }

const initialState = {}

const App = ({ intialState = {}, store }) => {
    return (
        <DndProvider backend={HTML5Backend}>
            <Provider store={store}>
                <HIMetadataPage />
            </Provider>
        </DndProvider>
    );
};

describe("Metadata multi connection edit test", () => {
   
    test("Metadata Module multi connection derby and postgres connections", async () => {
        const store = configureStore({
            reducer: reducers,
            middleware: (getDefaultMiddleware) =>
                getDefaultMiddleware({
                    thunk: {
                        extraArgument: mockAxios,
                    },
                    immutableCheck: false,
                    serializableCheck: false,
                }),
        });
        await waitFor(() => store.dispatch(appActions.setEditModeInfo(
            {
                dir: 'Gagan/Metadata_1.metadata',
                file: 'Metadata_1.metadata',
                extension: "metadata"

            })))
        await waitFor(() => render(<BrowserRouter>
            <App store={store} />
        </BrowserRouter>))
        const metadata = store.getState().metadata.present;
        expect(Object.keys(metadata.tables).length).toBe(1)
        expect(metadata.dataSourcesAddedToMetadata.length).toBe(1)
        expect(metadata.dataSource.length).toBe(1)

    });
});
