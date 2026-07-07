import "core-js/stable";
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
const crypto = require("crypto");
const flushPromises = () => new Promise(setImmediate);

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
      
    test("Metadata Module multi connection two derby connections", async () => {
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
        await flushPromises( store.dispatch(appActions.setEditModeInfo(
            {
                dir: 'Gagan/Metadata_1.metadata',
                file: 'Metadata_1.metadata',
                extension: "metadata"

            })))
        await flushPromises( render(<BrowserRouter>
            <App store={store} />
        </BrowserRouter>))
        const metadata = store.getState().metadata.present;
        expect(Object.keys(metadata.tables)).toStrictEqual(['dimdate'])
        expect(metadata.dataSourcesAddedToMetadata.length).toBe(1)
        expect(metadata.dataSource.length).toBe(2)

    });
});
