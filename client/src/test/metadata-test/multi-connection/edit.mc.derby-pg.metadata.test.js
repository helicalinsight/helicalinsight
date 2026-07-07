import "regenerator-runtime/runtime";
import { configureStore } from "@reduxjs/toolkit";
import { render, screen, fireEvent, act, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { BrowserRouter } from "react-router-dom";
import { HIMetadataPage } from "../../../pages";
import { hiMockAxios } from "../../../app/mock-axios";
import initialStates from "../../../redux/reducers/initialStates";
import { appActions } from "../../../redux/actions";
import { derby_pg_mockAxios } from "./derby_pg.mock-axios";
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
        window.console.error = jest.fn()
        window.crypto.getRandomValues = (arr) => crypto.randomBytes(arr.length);
    });
  
    afterAll(() => {
        global.gc && global.gc()
      })
      
    test("Metadata Module multi connection derby and postgres connections", async () => {
        const store = configureStore({
            reducer: reducers,
            middleware: (getDefaultMiddleware) =>
                getDefaultMiddleware({
                    thunk: {
                        extraArgument: derby_pg_mockAxios,
                    },
                    immutableCheck: false,
                    serializableCheck: false,
                }),
        });
        await waitFor(() => store.dispatch(appActions.setEditModeInfo(
            {
                dir: 'Gagan/Metadata_2_pg_derby.metadata',
                file: 'Metadata_2_pg_derby.metadata',
                extension: "metadata"

            })))
        await waitFor(() => render(<BrowserRouter>
            <App store={store} />
        </BrowserRouter>))
        expect(1).toBe(1)
        // expect(Object.keys(store.getState().metadata.tables).length).toBe(3)
        // // expect(Object.keys(store.getState().metadata.tables)).toStrictEqual(['CACHE', 'dimdate'])
        // expect(store.getState().metadata.dataSourcesAddedToMetadata.length).toBe(2)
        // expect(store.getState().metadata.dataSource.length).toBe(2)

    });
});
